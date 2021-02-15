# Table of Contents

1. [Introduction](#introduction)
2. [OpenMRS Data Model Compatibility](#openmrs-data-model-compatibility)
3. [Installation Guide For DB Sync](docs/db-sync/README.md)
4. [Architecture](#architecture)
   1. [Modules](#modules)
   2. [Design Overview](#design-overview)
   3. [Project Main Dependencies](#project-main-dependencies)
5. [Configuration](#configuration)
6. [Logging](#logging)
7. [Management Database](#management-database)
8. [Error Handling And Retry Mechanism](#error-handling-and-retry-mechanism)
9. [Developer Guide](#developer-guide)
    1. [Build](#build)
    2. [Tests](#tests)
10. [Building Custom Applications](docs/custom/README.md)

# Introduction
This project aims at providing a low-level OpenMRS synchronization module based on [Debezium](https://debezium.io) and 
[Apache Camel](https://camel.apache.org/manual/latest/faq/what-is-camel.html).
Data is directly pulled from a source OpenMRS MySQL database and wired onto camel routes in an effort to integrate 
OpenMRS with other systems without any use of the OpenMRS Java API or data model. The project comes with 2 built-in end 
user sister applications to sync data from one OpeMRS MySQL DB to another.

**Note:** Only specific database tables are watched i.e. only this set of tables get synced in case of DB sync. The list 
of these tables is defined by this [TableToSyncEnum](camel-openmrs/src/main/java/org/openmrs/eip/component/service/TableToSyncEnum.java)
with the exception of the metadata tables with the assumption that metadata is already centrally managed using the 
available metadata sharing tools. In future releases we want to make the list of tables to watch or sync configurable.

# OpenMRS Data Model Compatibility
The application was initially built against the 2.3.x branch should be compatible with the data model of the OpenMRS core
2.3.0, in theory this implies there needs to a maintenance branch for every OpenMRS minor release that has any DB changes
between it and it's ancestor, master should be compatible with the latest released OpenMRS version.

# Architecture

### Modules
Below is the high level breakdown of what is contained in each module.
#### camel-openmrs
- JPA annotated OpenMRS data model classes and repositories.
- Services for loading and saving entities from and to the OpenMRS databases.  
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
The project has a classic architecture with a service layer and a DAO layer. Each action (to get or save entities) of the 
Camel endpoints comes with the name of the table upon which the action is performed. A facade (`EntityServiceFacade`) is 
used to select the correct service to get or save entities according to the table name passed as a parameter.

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

### Project Main Dependencies
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data](https://spring.io/projects/spring-data)
* [Apache Camel](https://camel.apache.org/)
* [Debezium](https://debezium.io)
* [Lombok](https://projectlombok.org/)
* [Bouncy Castle](https://www.bouncycastle.org/fr/)

# Configuration
This project is built with spring boot therefore you can refer to spring boot's [application.properties](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)
file to further configure the applications and documentation details of each property.

For DB sync configuration, please refer to the [DB sync installation](docs/db-sync/README.md) guide.

For custom application configuration, please refer to the [custom application set up](docs/custom/README.md) documentation.

# Logging
The DB sync applications are spring boot applications and custom applications are also expected to be spring boot
applications. The end user applications come with built-in logback files on the classpath i.e. `logback.xml` and
`logback-console.xml`, the `logback.xml` file writes the logs to a file at `{USER.HOME}/.openmrs-eip/logs` where
`{USER.HOME}` is the user home directory. The `logback-console.xml` writes logs to the console, this can be useful in a
dev environment and tests.

For camel-routes, you need to set the logging level using their route ids, for instance if your route id is `my-route`,
then you set the logging level as below.
```
logging.level.my-route=DEBUG
```

For built-in routes and all classes in this project, you can globally set their log level by setting the value of the
`openmrs.eip.log.level` property in the application.properties file. For all other classes please refer to
[spring boot logging configurations](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties-core)

# Management Database
The `openmrs-watcher` comes with an embedded management DB where it stores failed DB events for purposes of re-processing.

The Management DB by default is an H2 database, it should be possible to use another DB system but we highly recommend 
those that are embeddable since they can be bootstrapped with the application, the DB should also reside on the same 
physical machine as the application to eliminate any possibility of being unreachable.

# Error Handling And Retry Mechanism
The `openmrs-watcher` module on which the DB sync sender is built has a built-in error handling and retry mechanism in 
case something goes wrong when the sender is processing a DB event, this also applies to any custom application built on 
top of the openmrs-watcher, the failed event gets pushed into an error queue in the [management database](#management-database) 
that comes as an embedded H2 DB, this DB can be accessed from a browser at a port and path configured in your 
application.properties file. The error queue is actually a table named `sender_retry_queue`. In theory this queue should
be empty all the time, there is a retry route which periodically polls the error queue and attempts to reprocess the events. When
a failed event is finally successfully re-processed, it gets removed out of the error queue. If an entity has an event in 
the error queue, all subsequent DB events for it are automatically pushed to the queue. It's highly recommended to 
take a look at this queue regularly for failed events, at least once day and address the root cause for failed events so that 
they can be re-processed. Otherwise, the retry route will indefinitely attempt to re-processs them. You can configure how often 
the retry queue can run, please refer to the [configuration](#configuration) section.

The DB sync receiver application also ships with a similar built-in error handling and retry mechanism with a separate 
embedded H2 [management database](#management-database), it uses a table named `receiver_retry_queue` to store failed 
incoming DB sync messages. You can configure how often the retry queue should run, please refer to the [configuration](#configuration) 
section.

# Developer Guide
## Build
From the terminal, navigate to your working directory, clone and build the project to generate the executable artifacts
by running the commands below.
```shell
git clone https://github.com/openmrs/openmrs-eip.git
cd openmrs-eip
mvn clean install
```
Make sure the build completed successfully.

## Tests
Unit ant Integration tests were only coded for the camel-openmrs Maven module. Integration tests are located in the 
[**dbsync-sender-app/src/it**](dbsync-sender-app/src/it) folder. They are run by default during the Maven test phase