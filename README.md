# Table of Contents

1. [Introduction](#introduction)
2. [Architecture](#architecture)
   1. [Modules](#modules)
   2. [Project Main Dependencies](#project-main-dependencies)
3. [Configuration](#configuration)
4. [Logging](#logging)
5. [Metrics](#metrics)   
6. [Management Database](#management-database)
7. [Error Handling And Retry Mechanism](#error-handling-and-retry-mechanism)
8. [Developer Guide](#developer-guide)
    1. [Build](#build)
    2. [Tests](#tests)
9. [Building Custom Applications](docs/custom/README.md)

# Introduction
This project aims at providing a mechanism to track low-level changes in an OpenMRS database based on [Debezium](https://debezium.io) and 
[Apache Camel](https://camel.apache.org/manual/latest/faq/what-is-camel.html).
Data is directly pulled from a source OpenMRS MySQL database and wired onto camel routes in an effort to integrate 
OpenMRS with other systems without any use of the OpenMRS Java API or data model.

# Architecture

### Modules
Below is the high level breakdown of what is contained in each module.
#### openmrs-watcher
- The main debezium route that fires up the debezium engine to read the MySQL binary log of the source database.
- The main listener route that gets called whenever the debezium engine emits a DB event, it calls another route that 
actually publishes the events to any registered camel endpoints, currently the endpoints are called in serial.
- A custom camel component that client code can use to start the main debezium route.
- The built-in error handling and retry mechanism in case something goes wrong while processing an event.
- The sender's liquibase changelog files used to create the management database tables.
#### commons
- Management datasource configuration.
- Common spring configurations for end user apps.
- Base classes used for mapping to the management DB tables.
#### example-app
- Example application to demonstrate usage of the openmrs-watcher as a dependency to create an end user app that process
DB events emitted by a debezium engine.

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

For custom application configuration, please refer to the [custom application set up](docs/custom/README.md) documentation.

# Logging
Custom applications are also expected to be spring boot applications. The end user applications come with built-in 
logback files on the classpath i.e. `logback.xml` and `logback-console.xml`, the `logback.xml` file writes the logs to a 
file to `{eip.home}/logs` where `{eip.home}` is the path to the app's installation directory. The `logback-console.xml` 
writes logs to the console, this can be useful in a dev environment and tests.

For camel-routes, you need to set the logging level using their route ids, for instance if your route id is `my-route`,
then you set the logging level as below.
```
logging.level.my-route=DEBUG
```

For built-in routes and all classes in this project, you can globally set their log level by setting the value of the
`openmrs.eip.log.level` property in the application.properties file. For all other classes please refer to
[spring boot logging configurations](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties-core)

# Metrics
There is built-in support for metrics via spring's actuator, it is not enabled by default, custom applications can be 
setup and configured to enable and expose endpoints for metrics. Please refer to the [custom application set up](docs/custom/README.md) 
documentation for how to do this.

# Management Database
The `openmrs-watcher` requires configuration of a management database where it stores useful data for proper functioning.

The Management DB has been tested with MySQL and H2, you should be able to use any other relational database supported 
by hibernate, it is highly recommended that the management DB resides on the same physical machine as the application to 
eliminate any possibility of being unreachable and lower latency hence better performance.

# MySQL 8 Support

## Public Key Retrieval
To avoid the known issue `Unable to connect: Public Key Retrieval is not allowed` with an OpenMRS MySQL 8 database, set the MySQL URL parameter `allowPublicKeyRetrieval` to `true`. This parameter should be added to the following application properties:
- `debezium.extraParameters` ( extra parameters given to Debezium)
- `spring.openmrs-datasource.jdbcUrl`
- `spring.mngt-datasource.jdbcUrl` ( only if you use MySQL 8 for management database)

Please, see the file [example-app/application.properties](example-app/application.properties) for examples.

## Authentication protocol

For some users (debezium user), the connection could fail with the error `AuthenticationException: Client does not support authentication protocol`
To fix this, use this following command to create a user compatible with MySQL 8 ( use `WITH mysql_native_password`):

```sql
CREATE USER '${DEBEZIUM_USERNAME}'@'%' IDENTIFIED WITH mysql_native_password BY '${DEBEZIUM_PASSWORD}';
```

# Error Handling And Retry Mechanism
The `openmrs-watcher` module has a built-in error handling and retry mechanism in case something goes wrong when the 
sender is processing a DB event, this also applies to any custom application built on top of the openmrs-watcher, the 
failed event gets pushed into an error queue in the [management database](#management-database). If you are using H2, 
the management DB can be accessed from a browser at a port and path configured in your application.properties file. 
The error queue is actually a table named `sender_retry_queue`. In theory this queue should be empty all the time, there 
is a retry route which periodically polls the error queue and attempts to reprocess the events. When a failed event is 
finally successfully re-processed, it gets removed out of the error queue. If an entity has an event in the error queue, 
all subsequent DB events for it are automatically pushed to the queue. It's highly recommended to take a look at this 
queue regularly for failed events, at least once day and address the root cause for failed events so that they can be 
re-processed. Otherwise, the retry route will indefinitely attempt to re-processs them. You can configure how often the 
retry queue can run, please refer to the [configuration](#configuration) section.

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
