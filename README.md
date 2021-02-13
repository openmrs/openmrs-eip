# Table of Contents

1. [Introduction](#introduction)
2. [OpenMRS Data Model Compatibility](#openmrs-data-model-compatibility)
3. [Architecture](#architecture)
   1. [Modules](#modules)
   2. [Design Overview](#design-overview)
   3. [Logging](#logging)
   4. [Project Main Dependencies](#project-main-dependencies)
4. [Configuration](#configuration)
5. [Distribution Overview](#distribution-overview)
   1. [Management Database](#management-database)
   2. [Sender](#sender)
   3. [Receiver](#receiver)
        1. [Conflict Resolution In The Receiver](#conflict-resolution-in-the-receiver)
6. [Error Handling and Retry Mechanism](#error-handling-and-retry-mechanism)
7. [Installation Guide For DB Sync](#installation-guide-for-db-sync)
8. [Developer Guide](#developer-guide)
    1. [Build and Test](#build-and-test)
9. [Building Custom Applications](#building-custom-applications)

# Introduction
This project aims at providing a low-level OpenMRS synchronization module based on [Debezium](https://debezium.io) and 
[Apache Camel](https://camel.apache.org/manual/latest/faq/what-is-camel.html).
Data is directly pulled from a source OpenMRS MySQL database and wired onto camel routes in an effort to integrate 
OpenMRS with other systems without any use of the OpenMRS Java API or data model. The project comes with 2 built-in end 
user sister applications to sync data from one OpeMRS MySQL DB to another.

# OpenMRS Data Model Compatibility
The application was initially built against the 2.3.x branch should be compatible with the data model of the OpenMRS core
2.3.0, in theory this implies there needs to a maintenance branch for every OpenMRS minor release that has any DB changes
between it and it's ancestor, master should be compatible with the latest released OpenMRS version.

# Architecture

### Modules
Below is the high level breakdown of what is contained in each module.
#### camel-openmrs
- JPA annotated OpenMRS datamodel classes and repositories
- Services for loading and saving entities from and to the OpenMRS databases  
- Model classes used for serialization and deserialization purposes by DB sync sender and receiver. 
- The OpenMRS DB datasource configuration. 
- Utility custom camel components used to load and save entities to and from the target databases.
- Spring configurations and other utility classes
#### openmrs-watcher
- The main debezium route that fires up the debezium engine to read the MySQL binary log of the source database.
- The main listener route that gets called whenever the debezium engine emits a DB event, it calls another route that 
actually publishes the events to any registered camel endpoints, currently the endpoints are called in serial.
- A custom camel component that client code can use to start the main debezium route.
- The built-in error handling and retry mechanism in case something goes wrong while processing an event.
- The sender's liquibase changelog files used to create the management database tables.
#### common
- Management datasource configuration.
- Common spring configurations for end user apps.
- Base classes used for mapping to the management DB tables.
#### dbsync-sender-app
- End user application for sending DB sync data from a source DB to an active MQ instance.
#### dbsync-receiver-app
- End user application for receiving DB sync data from an active MQ instance and saving it to the destination DB.
#### example-app
- Example application to demonstrate usage of the openmrs-watcher as a dependency to create an end user app that process
DB events emitted by a debezium engine.
#### distribution (TO BE COMPLETED)
- Will contain scripts for build ready to run distributions of the sender and receiver apps
#### camel-odoo (TO BE REMOVED)
- Integration code with an odoo system

### Design Overview
The project has a classic architecture with a service layer and a DAO layer.
Each action (to get or save entities) of the Camel endpoints comes with the name of the table upon which the action is 
performed. A facade (`EntityServiceFacade`) is used to select the correct service to get or save entities according to 
the table name passed as a parameter.

The project uses an embedded [debezium](https://debezium.io) engine to track insert, update and delete operations of 
rows in monitored OpenMRS tables, out of the box the only monitored tables are those containing patient demographic and 
clinical data, this implies that you need to configure MySQL binary logging in the source (sender) OpenMRS DB.

It's very important to note that technically when using this application for DB sync, this is DB to DB sync happening
outside of the OpenMRS application, this has implications e.g if you sync something like person name, the search index
needs to be triggered for a rebuild, the current receiver DB sync route internally triggers this rebuild for all known
indexed entities from OpenMRS core, however it might not be up to date with later OpenMRS versions in case more indexed
entities were added and of course module tables.

Once entities are retrieved from the database they are mapped to a model object. The model contains all non-structured 
fields of the OpenMRS object and follows a systematic rule for linked structured field: it only stores the _UUID_ of the 
linked entities.

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

The application uses [Lombok](https://projectlombok.org/) to allow creating POJOs without coding their getters and setters. A plugin needs to be installed to the IDE to add setters and getters at compile time.

### Logging
The DB sync applications are spring boot applications and custom applications are also expected to be spring boot 
applications. The end user applications come with built-in logback files on the classpath i.e. `logback.xml` and 
`logback-console.xml`, the `logback.xml` file writes the logs to a file at `{USER.HOME}.openmrs-eip/logs` where 
`{USER.HOME}` is the user home directory. The `logback-console.xml` writes logs to the console, this can be useful in a 
dev environment and tests.

For camel-routes, you need to set the logging level using their route ids, for instance if your route id is `my-route`, 
then you set the logging level as below,
```
logging.level.my-route=DEBUG
```

For built-in routes and all classes in this project, you can globally set their log level by setting the value of the 
`openmrs.eip.log.level` property in the application.properties file. For all other classes please refer to 
[spring boot logging configurations](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)

### Project Main Dependencies
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data](https://spring.io/projects/spring-data)
* [Apache Camel](https://camel.apache.org/)
* [Debezium](https://debezium.io)
* [Lombok](https://projectlombok.org/)
* [Bouncy Castle](https://www.bouncycastle.org/fr/)

# Configuration
For configuration details of DB sync or your custom application, please refer to the [configuration](docs/properties/README.md) page.

# Distribution Overview

## Management Database
The `openmrs-watcher` comes with an embedded management DB where it stores failed DB events for purposes of re-processing. 
outbound DB events. Because the DB sync sender application is built on top of it, it also comes it. The DB sync receiver 
application too comes with a management DB to store failed inbound messages for re-processing.

The Management DB by default is an H2 database, it should be possible to use another DB system but we highly recommend 
those that are embeddable since they can be bootstrapped with the application, the DB should also reside on the same 
physical machine as the application to eliminate any possibility of being unreachable.

## Sender

![Sender Diagram](docs/sender/sender.jpg)

As seen from the diagram above, the sender is really a spring boot application at the core running with the active
profile set to sender, it uses Apache camel to route messages and uses [debezium](https://debezium.io) to track DB
changes in source OpenMRS database by reading the MySQL binary log which MUST be enabled with the format set to row,
please refer to the inline documentation of the various configuration properties in the sender's application.properties
in the distribution/sender folder.

Note that the default application that is bundled with the project comes with dockerized MySQL databases where the
MySQL binary log is ONLY preconfigured for the remote instance because it assumes a one-way sync from remote to central.

When the application is fired up in sender mode, the debezium route starts the debezium component which will periodically
read entries in the MySQL binary log of the remote OpenMRS instance, it constructs an [Event](openmrs-watcher/src/main/java/org/openmrs/eip/mysql/watcher/Event.java) instance which has several
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

## Receiver

![Receiver Diagram](docs/receiver/receiver.jpg)

As seen from the diagram above, the receiver is exactly the same spring boot application with Apache camel but instead
running at another physical location with an OpenMRS installation with the active profile set to receiver.

Recall from the sender documentation above, that the out-bound DB sync listener route ends by publishing the payload of
the entity to be synced to a destination shared with the receiving sync application usually a message broker, this is
where the receiver starts, its receiver route connects to this external message broker, consumes messages out of sync
queue and calls the DB sync route which syncs the associated entity to the destination OpenMRS instance's MySQL DB.

**NOTE:** In this default setup since it's a one-way sync, MySQL bin-log isn't turned on for the destination MySQL DB,
2-way sync is currently not supported.

### Conflict Resolution In The Receiver
The receiver has a built-in mechanism to detect conflicts between incoming and the existing state of the entity to be
synced i.e. if someone edits a row in the receiver and an ‘older’ sync payload reaches the receiver, this implies the
incoming data is most likely going to overwrite a change made at the receiver. Therefore, there is logic in the receiver
so that if a record was edited on the receiver side and either its date changed or voided/retired is after both of those
in the incoming payload, the application won't sync the entity and it will move the message to the `receiver_conflict_queue`
table. Currently, to resolve the conflict, the entity has to be manually updated in the receiver or sender, then as it
may dictate adjust date changed in the sender so that it is ahead of date voided/retired of the entity in the receiver
and then mark the row as resolved in `receiver_conflict_queue` table.

# Error Handling and Retry Mechanism
The `openmrs-watcher` module on which the DB sync sender is built has a built-in error handling and retry mechanism in 
case something goes wrong when the sender is processing a DB event, this also applies to any custom application built on 
top of the openmrs-watcher, the failed event gets pushed into an error queue in the [management database](#management-database) 
that comes as an embedded H2 DB, this DB can be accessed from a browser at a port and path configured in your 
application.properties file. The error queue is actually a table named `sender_retry_queue`. In theory this queue should
be empty all the time, there is a retry route periodically polls the error queue and attempts to reprocess the events, if
an failed event is finally successfully re-processed, it gets removed out of the error queue. If an enity has an event in 
the error queue, all subsequent DB events for it are automatically pushed to the queue. It's highly recommended to 
take a look at this queue regularly for failed events, at least once day and address the root cause for failed events so that 
they can be re-processed. Otherwise the retry route will indefinitely attempt to reprocesss them. You can configure how often 
the retry queue can run, please refer to the [configuration](docs/properties/README.md) page.

The DB sync receiver application also ships with a similar built-in error handling and retry mechanism with a separate 
embedded H2 [management database](#management-database), it uses a table named `receiver_retry_queue` to store failed 
incoming DB sync messages, please refer to the [configuration](docs/properties/README.md) page.

# Developer Guide
## Build and Test
Unit ant Integration tests were only coded for the camel-openmrs Maven module.
Integration tests are located in the [**app/src/it**](dbsync-sender-app/src/it) folder. They are run by default during the Maven test phase.

# Installation Guide For DB Sync

The application is designed to run in one of 2 modes i.e. sender or receiver, you decide one of these via spring's JVM
property **spring.profiles.active** with the value set to sender or receiver.

The OpenMRS dbSync can be used with a endpoint between the sender and the receiver exchanging sync data via ActiveMQ.
You can also use file-based syncing in a development or test environment but we highly discourage it in production.

A sender and a receiver directory are created containing the necessary routes and configurations to install and configure
sender and receiver sync applications at a remote and central database respectively. They are both located in the
**distribution** directory. Please refer to the [Distribution configuration README.md](./distribution/README.md) for 
installation and configuration details.

# Developer Guide
This guide is intended for developers that wish to create custom end user applications to watch for events in an OpenMRS
database and process them, this can be useful if you wish to integrate OpenMRS with another system based on changes 
happening in the OpenMRS DB.

### Building Custom Applications
The section is intended for developers wishing create a custom integration apps built on top of the EIP's OpenMRS watcher module.
Please refer to the [example-app](./example-app) module as a guide. You will notice it's a simple maven spring boot project 
which includes a dependency on the openmrs-watcher module. The openmrs-watcher dependency comes with a default logback
configuration file and writes the logs in your home directory under **openmrs-eip/logs** which is a hidden directory and
another that can write to the console, this can be useful in a DEV environment and tests. 

The `ExampleApplication` class contains the main method which bootstraps the spring application context.

The project has the following xml files containing camel route definitions,
- `init.xml`: Typically, you need to have a similar route that fires up the debezium engine to start listening for DB events,
  you can copy this route into your project without any modification, it calls a custom camel `openmrs-watcher` component 
  which is a thin wrapper around the debezium MySQL component, a lightweight camel route and processors written with 
  Java DSL that receive debezium events and forward them to your registered camel endpoint(s). The message body that gets 
  sent to your route is an [Event](openmrs-watcher/src/main/java/org/openmrs/eip/mysql/watcher/Event.java) object that
  encapsulates some useful information about the affected row like the DB operation, table name, primary key value, 
  OpenMRS unique identifier usually a uuid and others, please look at the class for more details.
- `event-listener`: Similarly, you will also need a listener route in your application that will be notified of DB events, 
  the single listener in our example app just logs the event but in practice your will do some useful things e.g. 
  integration with another system.
**NOTE:** We set the errorHandlerRef of our listener route to `watcherErrorHandler`, this automatically enables the built-in
  [Error Handling and Retry Mechanism](#error-handling-and-retry-mechanism)

The project also contains a classic spring boot application.properties file, please don't include this file directly on 
the classpath, instead include it in the same directory as your executable jar file. Please refer to the [Custom App](docs/custom/README.md) 
config file for documentation of the properties.
Note: Your routes MUST be on the classpath in a directory named camel in order for the framework to find them.