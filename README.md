# Introduction
This project aims at providing a low-level OpenMRS synchronization module based on [Apache Camel](https://camel.apache.org/manual/latest/faq/what-is-camel.html).
Data is directly pulled from a source OpenMRS MySQL database and pushed to a target OpenMRS MySQL database without any use of the OpenMRS Java API or data model.

The project is composed of two modules:
- The Camel component module. The component knows two verbs: _extract_ and _load_. _Extract_ is used to retrieve data from the database and send it into Camel routes. _Load_ is used to receive the data from Camel routes to store it in the database.
- The app module, which is a Spring Boot application having either the role of the sender, the receiver or both that will launch the Camel routes.

The application uses [Lombok](https://projectlombok.org/) to allow creating POJOs without coding their getters and setters. A plugin needs to be installed to the IDE to add setters and getters at compile time.

# OpenMRS Data Model compatibility
The master branch should be compatible with the data model of the OpenMRS's version currently on the master branch of OpenMrs core
Each released minor version of OpenMRS will lead to a maintenance branch.
For example if you intend to synchronise data between an OpenMRS instance running on Core 2.4.x and another OpenMRS instance running on Core 2.3.x, you will use the appropriate build of the OpenMRS Camel component on each end.

# Sample Configuration for Testing
A sender and a receiver directory are created to simulate a network between a remote database and a central database. They are both located in the **sample/sample_springboot_setup** directory.
Please refer to the [Sample configuration README.md](./sample/sample_springboot_setup/README.md) for details about its configuration.

The OpenMRS dbSync can be used with any Camel endpoint between the sender and the receiver including ActiveMQ via `jms` queues. A sample configuration can be found in the **/sample/sample_activemq_setup** directory.
Please refer to the [Configure ActiveMQ README.md](./sample/sample_activemq_setup/README.md) for details about its configuration.

# File synchronization
It is also possible to synchronize the content of a directory. The directory sync is performed via a different Camel route, but files will be transferred through the same Camel endpoint as the entities.
To differentiate entities from files at reception, files are encoded in Base64 and the result is placed between the `<FILE>` and `</FILE>` tags.

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
