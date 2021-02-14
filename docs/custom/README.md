# Building Custom Applications

This guide is intended for developers that wish to create custom end user applications to watch for events in an OpenMRS
database and process them, this can be useful if you wish to integrate OpenMRS with another system based on changes
happening in the OpenMRS DB. Custom apps should be built on top of the EIP's OpenMRS watcher module.

Please refer to the [example-app](../../example-app) module as a guide. You will notice it's a simple maven spring boot project
which includes a dependency on the openmrs-watcher module. The openmrs-watcher dependency comes with a default logback
configuration file and writes the logs at `{USER.HOME}/.openmrs-eip/logs/openmrs-eip.log` where `{USER.HOME}` is the path
to your user home directory, another that can write to the console, this can be useful in a DEV environment and tests.

The `ExampleApplication` class contains the main method which bootstraps the spring application context.

The project has the following xml files containing camel route definitions,
- `init.xml`: Typically, you need to have a similar route that fires up the debezium engine to start listening for DB events,
  you can copy this route into your project without any modification, it calls a custom camel `openmrs-watcher` component
  which is a thin wrapper around the debezium MySQL component, a lightweight camel route and processors written with
  Java DSL that receive debezium events and forward them to your registered camel endpoint(s). The message body that gets
  sent to your route is an [Event](../../openmrs-watcher/src/main/java/org/openmrs/eip/mysql/watcher/Event.java) object that
  encapsulates some useful information about the affected row like the DB operation, table name, primary key value,
  OpenMRS unique identifier usually a uuid and others, please look at the class for more details.
- `event-listener`: Similarly, you will also need a listener route in your application that will be notified of DB events,
  the single listener in our example app just logs the event but in practice your will do some useful things e.g.
  integration with another system.

**NOTE:** We set the errorHandlerRef in the example listener route to `watcherErrorHandler`, this automatically enables
the built-in [Error Handling and Retry Mechanism](../../README.md#error-handling-and-retry-mechanism)

The project also contains a classic spring boot application.properties file, please don't include this file directly on
the classpath, instead include it in the same directory as your executable jar file. Your routes MUST be on the classpath
in a directory named camel in order for the framework to find them.