# Building Custom Applications

This guide is intended for developers that wish to create custom end user applications to watch for events in an OpenMRS
database and process them e.g. for integration purposes with other systems that need to react to changes happening in 
the OpenMRS DB. Custom apps should be built on top of the EIP's `openmrs-watcher` module.

Please refer to the [example-app](../../example-app) module as a guide. You will notice it's a simple maven spring boot 
project which has a dependency on the `openmrs-watcher` module that comes with a default logback configuration file 
and writes the logs to `{eip.home}/logs/openmrs-eip.log` where `{eip.home}` is the path to your app's installation
directory, there is another logback file configured to log to the console, this can be useful in a DEV environment and 
tests.

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

### Things to note
- The main class `ExampleApplication` has the `@SpringBootApplication` annotation with the `scanBasePackages` attribute 
  value set to `org.openmrs.eip`, this is important for spring to pick up important framework beans.
- We set the errorHandlerRef in the example listener route to `watcherErrorHandler`, this automatically enables the 
  built-in [Error Handling And Retry Mechanism](../../README.md#error-handling-and-retry-mechanism)
- The routes are located on the classpath in a directory named `camel` in order for the framework to find and load them.
- The project also contains a classic spring boot application.properties file so should yours, please don't include your 
application.properties file inside any of your jars, instead include it in the same directory as your executable jar 
file at deployment time. 

### Project Setup
With the example-app we just went over above in mind, below are the steps to create your own custom app to be notified 
of DB events and do something useful.

1. Create a new maven project
2. Add `openmrs-watcher` as a dependency to your pom.xml file, below is what your maven dependency should look like.
    ```xml
    <dependency>
        <groupId>org.openmrs.eip</groupId>
        <artifactId>openmrs-watcher</artifactId>
        <version>2.0-SNAPSHOT</version>
    </dependency>
    ```
3. Add a main class for your spring boot application, you can copy [`ExampleApplication`](../../example-app/src/main/java/org/openmrs/eip/example/ExampleApplication.java) 
   from the example app, change the route id and make any other additions you deem necessary.
4. Add a camel route to start the `openmrs-watcher` component, you can copy [`init.xml`](../../example-app/src/main/resources/camel/init.xml)
   from the example app and make any necessary modifications if needed, the uri of the `openmrs-watcher` 
   endpoint is of the form `openmrs-watcher:{NAME}` where {NAME} is a logical name, there is no meaning to it but for 
   future versions we might start using it meaningfully.
5. A copy of this [application.properties](../../docs/custom/application.properties) will have to be included in the 
   directory where which you will be running your executable jar file.
6. Add a camel route to be notified of DB changes, you can copy [`event-listener.xml`](../../example-app/src/main/resources/camel/event-listener.xml)
   from the example app, change the route id and make any other additions you deem necessary, it is in this file that 
   your application logic will pick up from, it wil be called everytime a change happens to any row in the watched 
   OpenMRS database tables. The value of the `uri` attribute of the first `from` tag in the route has to match the value 
   of the `db-event.destinations` property in the `application.properties` file you created in step 5. 
   **DO NOT** change the value of the `errorHandlerRef` attribute of the `route tag` otherwise you break the error 
   handling and retry mechanism. In theory, every route in your chain of routes that process a DB event needs to have 
   the same error handler set so that the framework can gracefully handle it and apply the retry mechanism. 
7. Open the copy of the `application.properties` you created in step 5 and set the property values accordingly, 
   carefully read the in-inline documentation as you set each property value.