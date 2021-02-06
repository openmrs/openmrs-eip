# Introduction
This project aims at providing a low-level OpenMRS synchronization module based on [Apache Camel](https://camel.apache.org/manual/latest/faq/what-is-camel.html).
Data is directly pulled from a source OpenMRS MySQL database and pushed to a target OpenMRS MySQL database without any use of the OpenMRS Java API or data model.

The project is composed of two modules:
- The Camel component module. The component knows two verbs: _extract_ and _load_. _Extract_ is used to retrieve data from the database and send it into Camel routes. _Load_ is used to receive the data from Camel routes to store it in the database.
- The app module, which is a Spring Boot application having either the role of the sender, the receiver or both that will launch the Camel routes.

The application uses [Lombok](https://projectlombok.org/) to allow creating POJOs without coding their getters and setters. A plugin needs to be installed to the IDE to add setters and getters at compile time.

# OpenMRS Data Model compatibility
The application was initially built against the 2.3.x branch should be compatible with the data model of the OpenMRS core 
2.3.0, in theory this implies there needs to a maintenance branch for every OpenMRS minor release that has any DB changes 
between it and it's ancestor, master should be compatible with the latest released OpenMRS version.

# Distribution Configuration for Testing
The application is designed to run in one of 2 modes i.e. sender or receiver, you decide one of these via spring's JVM 
property **spring.profiles.active** with the value set to sender or receiver.

A sender and a receiver directory are created to simulate a network between a remote database and a central database. They are both located in the **distribution** directory.
Please refer to the [Distribution configuration README.md](./distribution/README.md) for details about its configuration.

The OpenMRS dbSync can be used with a endpoint between the sender and the receiver exchanging sync data via ActiveMQ. 
You can also use file-based syncing in a development or test environment but we highly discrouage it in production.

It's very important to note that technically this is DB to DB sync happening outside of the OpenMRS application, this 
has implications e.g if you sync something like person name, the search index needs to be triggered for a rebuild,
the current receiver DB sync route internally triggers this rebuild for all known indexed entities.

### Sender

  ![Sender Diagram](distribution/resources/sender.jpg)

As seen from the diagram above, the sender is really a spring boot application at the core running with the active 
profile set to sender, it uses Apache camel to route messages and uses [debezium](https://debezium.io) to track DB 
changes in source OpenMRS database by reading the MySQL binary log which MUST be enabled with the format set to row, 
please refer to the inline documentation of the various configuration properties in the sender's application.properties 
in the distribution/sender folder.

Note that the default application that is bundled with the project comes with dockerized MySQL databases where the
MySQL binary log is ONLY preconfigured for the remote instance because it assumes a one-way sync from remote to central.

When the application is fired up in sender mode, the debezium route starts the debezium component which will periodically 
read entries in the MySQL binary log of the remote OpenMRS instance, it constructs an [Event](./camel-openmrs/src/main/java/org/openmrs/eip/component/entity/Event.java) instance which has several 
fields with key fields being the source table name, the unique identifier of the affected row usually a uuid, the 
operation that triggered the event(c, u, d) which stand for Create, Update or Delete respectively. The debezium route 
sends the event to an intermediate event processor route which has some extra logic in it which in turn sends the event 
to all configured endpoints with the DB event message set as the body. In theory, you can register as many endpoints as 
the systems that need to be notified of changes from the OpenMRS DB, the sender's application.properties file has a 
named **db-event.destinations** which takes a comma separated list of camel endpoints to which the db event will be sent. 
There is a built-in sender DB sync route that is registered as a listener for DB events, its job is to transform each 
message by loading the entity by its uuid, serialize it into a custom format and then publishes the payload to a 
configured sync destination, if you're using ActiveMQ to sync between the sender and receiver which is our recommended 
option, it means the message would be pushed to a sync queue in an external message broker that is shared with the 
receiving sync application.
    
### Receiver

  ![Receiver Diagram](distribution/resources/receiver.jpg) 

As seen from the diagram above, the receiver is exactly the same spring boot application with Apache camel but instead 
running at another physical location with an OpenMRS installation with the active profile set to receiver.

Recall from the sender documentation above, that the out-bound DB sync listener route ends by publishing the payload of 
the entity to be synced to a destination shared with the receiving sync application usually a message broker, this is 
where the receiver starts, its receiver route connects to this external message broker, consumes messages out of sync 
queue and calls the DB sync route which syncs the associated entity to the destination OpenMRS instance's MySQL DB.

**NOTE:** In this default setup since it's a one-way sync, MySQL bin-log isn't turned on for the destination MySQL DB, 
2-way sync is currently not supported.

# File synchronization ({color:red}NOT FOR PRODUCTION{color})
It is also possible to synchronize the content of a directory. The directory sync is performed via a different Camel route, 
but files will be transferred through the same Camel endpoint as the entities. To differentiate entities from files at 
reception, files are encoded in Base64 and the result is placed between the `<FILE>` and `</FILE>` tags.

# Build and Test
Unit ant Integration tests were only coded for the camel-openmrs Maven module.
Integration tests are located in the [**app/src/it**](./app/src/it) folder. They are run by default during the Maven test phase. 

# Architecture
The project has a classic architecture with a service layer and a DAO layer.
Each action (to get or save entities) of the Camel endpoints comes with the name of the table upon which the action is performed.
A facade (`EntityServiceFacade`) is used to select the correct service to get or save entities according to the table name passed as a parameter.

Once entities are retrieved from the database they are mapped to a model object. The model contains all non-structured fields of the OpenMRS object and follows a systematic rule for linked structured field: it only stores the _UUID_ of the linked entities.

For example let us consider the OpenMRS Camel entities `Observation`, `Encounter` and `Visit`.
The model corresponding to `Visit` is named `VisitModel` and will look like:
```java
class Visit {

  private String uuid;

}

class VisitModel {

  private String uuid;

}
```
The model corresponding to `Encounter` is named `EncounterModel` and will look like:
```java
class Encounter {

  private String uuid;

  @NotNull
  private Visit visit;

}

class EncounterModel {

  private String uuid;

  private String visitUuid;

}
```
Note that for the sake of this readme we assume that each encounter must be linked to a visit.
The model corresponding to `Observation` is named `ObservationModel` and will look like:
```java
public class Observation {

  private String uuid;

  @NotNull
  private Encounter encounter;

}

public class ObservationModel {

    private String uuid;

    private String encounterUuid;

}
```
The model object is then encapsulated in a wrapping `SyncModel` object that references the class of the object being synchronised, it is this wrapper that is in fact marshalled into a json string before being sent through the Camel routes.

Once the marshalled json string is received on the target side, its embedded model object is unmarshalled based on the object class referenced in the `SyncModel`. At this point the model object is reconstructed on the target side, still holding references to its linked entities as UUIDs. Such as for instance `encounterUuid` in the `ObservationModel` above.

Let us imagine the case of an `Observation` object being synchronised and arriving on the target end of the Camel route.

To reconstruct an `Observation` entity and its linked entities from an `ObservationModel` instance before saving it in the target database, the mapper will fetch and set each linked entity from the target database with their UUID.

* If the linked entity already exists in the target database it will be set as it is fetched from the target database.
* If the linked entity does not exist yet, it will create a so-called _lightweight_ entity (`LightEntity`) carrying only the UUID of the linked entity and all its non-nullable non-entity members filled with default values (so typically default dates and other strings ... etc.)

When a non-nullable member of the linked light entity is also an entity, a _placeholder_ voided (or retired) entity is set for this non-nullable member.
After some cycles of synchronisation, placeholder entities should no longer be attached to any entity. For example:

Let us consider the following situation when an `Observation` is synchronized before its `Encounter` and before the encounter's `Visit`.

A lightweight `Encounter` with the correct UUID is created and saved into the target database before being set to the `Observation`, and before saving that `Observation`.
<br/>However this `Encounter` requires a non-nullable `Visit` about which we do not have any information at all yet. For this one a lightweight **voided** _placeholder_ instance is used in order for the object tree `Observation` → `Encounter` → `Visit` to have all its non-nullable entities set before saving the `Observation`.
<br/>Each set of entities (`Visit`, `Ecnounter`, `Observation`, ... ) can have at most **one** such voided placeholder instance that is always reused to fill this exact same purpose: fill non-nullable members of other entities. This means for instance that each time a placeholder `Visit` is needed it is always the same voided `Visit` that is used to fill that gap.

When the linked `Encounter` is eventually unmarshalled on the target side through a subsequent round of synchronization, it will contain the actual UUID of the `Visit` for which the voided placeholder was used. At that point the `Encounter` is thus saved in the target database with a lightweight `Visit` carrying the correct UUID rather than the placeholder lightweight visit.

When all synchronisation rounds have successfully completed all placeholders entities should be "detached", meaning that no other entities should be linked to them anymore.

# Project Main Dependencies
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data](https://spring.io/projects/spring-data)
* [Apache Camel](https://camel.apache.org/)
* [Lombok](https://projectlombok.org/)
* [Bouncy Castle](https://www.bouncycastle.org/fr/)
