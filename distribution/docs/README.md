# Installation Guide For DB Sync

1. [Introduction](#introduction)
2. [Requirements](#requirements)
3. [Assumptions](#assumptions)
4. [Installation](#installation)
    1. [JMS Server](#jms-server)
    2. [Building OpenMRS EIP](#building-openmrs-eip)
    3. [Receiver](#receiver)
    4. [Sender](#sender)
5. [Console](#console)
    1. [User Account Management](#user-account-management)
    2. [Open Webapp Setup](#open-webapp-setup)
6. [DB Sync Technical Overview](#db-sync-technical-overview)
    1. [Sender Overview](#sender-overview)
    2. [Receiver Overview](#receiver-overview)

## Introduction
This installation guide is for those intending to set up database synchronization between 2 OpenMRS databases.
Currently, only one-way DB sync is supported therefore, you can have a receiver application to write to the destination 
database and one or more sender applications to read from the source database(s). You would also need a JMS server to 
provide a way for a sender and receiver applications to exchange sync data.

Please try to make sure you install the required applications in the order laid out in the installation section i.e. 
JMS Server, receiver and then sender(s).

## Requirements
- A unix operating system (Never been tested on windows)
- [Apache Maven](http://maven.apache.org/install.html)
- [OpenJDK 8](https://openjdk.java.net/install/)
- [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## Assumptions

#### One-way data sync
Currently, the receiver application doesn't sync data back to a sender but in future release support for two-way sync 
will be supported.

#### Time Zone and Calendar system
The DB sync sender and receiver applications MUST be running in the same calendar system. It's also highly recommended
to run both the sender and receiver in UTC. If another time zone is preferred, it would be best to run both of them in
the same time zone. With that said, the application should be able to sync data between a sender and receiver in different
time zones by converting the datetime field values to the receiver's time zone. This conversion is not done for time ONLY
fields and date fields with no time component.

#### OpenMRS Instances are already installed
This guide doesn't cover installation of the sender and receiver OpenMRS instances and databases, if not, please refer 
to the [OpenMRS](https://openmrs.org) documentation.

#### Shared JMS server
Both the sender and receiver sync applications have access to a single JSM instance in order for them to be able to 
exchange sync data.

#### Only Patient and clinical data is synced
Currently, the only data that is synchronized is patient records and their clinical data, assuming metadata is already 
centrally managed using the available metadata sharing tools.

#### Receiver not a POC system (Point Of Care)
The receiving OpenMRS system isn't a POC system used to manage patient records and their clinical data that is 
synced from other sites, there is some technical reasons behind this, the changes you make to records received from 
other sites at the receiver site can always be overwritten by future sync attempts for the same record. Because of 
this, there is a built-in mechanism to minimise this happening, it checks if a record was edited on the receiver side 
since the last time it was synced and if it was, the application won't sync incoming data for the same record and will
move the event to a conflict queue, it's until you remove the item from the conflict queue that the entity gets synced
ever again, this implies you will need to periodically monitor this queue to ensure it's empty and also you will have 
to manually edit the record with conflicts in the receiver DB to apply the changes.

With that said, if you wish to use the receiver OpenMRS instance as a POC system, there has to be a mechanism to ensure 
its users only have access to patients and their clinical data entered at the receiver site, this can be achieved using 
by installing the [datafilter](https://github.com/openmrs/openmrs-module-datafilter) module, documentation can be found 
[here](https://wiki.openmrs.org/x/6QBiDQ). If your sender sites are syncing to a receiver database used for purposes of 
centralizing all site data in a single OpenMRS database. This is typical in cases where an organization wishes to have a
single centralized database with all remote sites syncing to a single centralized database and they still want to have a
POC instance at the central location, we highly recommend that they set up a separate OpenMRS instance with its own 
database and sender sync application so that it also sync to the centralized database that way the centralized database 
is kept as a read-only instance.

## Installation

### JMS Server
In this guide we recommend [ArtemisMQ](https://activemq.apache.org/components/artemis/documentation/) as the preferred
JMS server and all our documentation and properties file templates assume ArtemisMQ however, you should be able to use
any of your choice as long as it is supported by spring boot and apache camel's [jms](https://camel.apache.org/components/latest/jms-component.html)
or [activemq](https://camel.apache.org/components/latest/activemq-component.html) components but you would have to make
more changes to the sender and receiver application properties jms settings, see
[How to install ArtemisMQ](https://activemq.apache.org/components/artemis/documentation/latest/using-server.html)

Remember to keep note of the ArtemisMQ broker port and URL to the console application, you can peek at what's going on
inside your JMS server using the console application.

The configurations instructions in this section are based on artemis version 2.6.3, if running a different version 
please be sure to find the instructions for your specific version.

The `broker.xml` is usually located in `/var/lib/artemis/etc`.

#### Configuration
- In the `broker.xml` file for artemis, set `max-disk-usage` to 100, for more details see 
  [artemis configuration reference](https://activemq.apache.org/components/artemis/documentation/latest/configuration-index.html)

#### SSL Setup
For data privacy purposes, it's highly recommended to exchange messages between remote sites and central over a secure
connection, below is how to set up SSL between the DB sync applications and ActiveMQ, these instructions apply to both 
sender and receiver, for more details see [artemis SSL configuration](https://activemq.apache.org/components/artemis/migration-documentation/ssl.html)

**SSL configuration in ActiveMQ**
- First you will need to obtain the SSL certificate for the artemis server, for testing purposes you can create a self 
  signed certificate, be sure to protect your generated private key and signed certificate by saving them in a keystore 
  with a password.
- In the `broker.xml` file for artemis, locate the `acceptors` tag and a new acceptor by copying the existing acceptor 
  named **artemis**. For the new acceptor, set its name attribute to a unique value, assign it a new tcp port number in 
  the URL value and then append the line below to its URL value, 
  ```shell
  sslEnabled=true;keyStorePath={KEYSTORE_PATH};keyStorePassword={KEYSTORE_PASSWORD}
  ```
  Where `{KEYSTORE_PATH}` and `{KEYSTORE_PASSWORD}` are the path and password respectively for the keystore containing 
  the artemis private key from the first step and the signed certificate, values are separated by a semi-colon, so be 
  sure to include it at the start if the original value didn't have one at the end.
- Restart artemis

**SSL configuration in the DB Application**

The instructions below apply to both the sender and receiver DB sync applications.
- Add the signed certificate to the Java trust store by running the command below from the terminal,
  ```
  keytool -import -alias artemis -file {CERTIFICATE_PATH} -storepass {TRUSTSTORE_PASSWORD} -keystore {TRUSTSTORE_PATH}
  ```
  Where `{CERTIFICATE_PATH}` is the path to the signed certificate, while `{TRUSTSTORE_PATH}` and `{TRUSTSTORE_PASSWORD}` 
  are the path and password respectively to the trust store for the JVM used to run the DB sync application, the JVM 
  trust store is a file located at `{JAVA_INSTALL_DIR}/Contents/Home/jre/lib/security/cacerts`.
- In the `application.properties` file for the DB sync application you will need to set the value of the 
  `spring.artemis.port` property to match the port number you assigned to the SSL enabled acceptor in the artemis 
  `broker.xml` file, and also set the value of the `artemis.ssl.enabled` property to true.
- Restart the DB sync application 
    
**Key things to note**
- It's VERY important to understand how JMS servers work, key concepts especially when it comes to topics vs queues
  subscription and message durability before picking one over the other.
- In case you're using a topic, after installing ArtemisMQ and creating your broker instance, your **MUST** connect the 
  receiver sync application first with a durable topic subscription before any sender sync application connects and 
  publishes any message. Otherwise, any message pushed by a sender to the topic before you do so won't be delivered to 
  the receiver.
- With topics, it also implies that whenever you want to onboard a new sender application to sync data from a new site
  that wishes to join the party to push data to the receiver and it's using a different JMS instance or topic name,
  then the receiver **MUST** always connect first so that a durable subscription is created for it before the new sender
  application can start pushing sync data. Technically this applies whenever you switch to a new JMS broker instance.
- In case you're using queues, you MUST set auto-delete-queues under address settings to false, for more details see 
  [address model config](https://activemq.apache.org/components/artemis/documentation/latest/address-model.html)
  
### Building OpenMRS EIP
From the terminal, navigate to your working directory, clone and build the project to generate the executable artifacts 
by running the commands below.
```shell
git clone https://github.com/FriendsInGlobalHealth/openmrs-eip.git
cd openmrs-eip
mvn clean install
```

If you want to run the application in Portuguese, then you need to set the profile for the maven build to **pt** like below
```
mvn clean install -P pt
```

Make sure the build completed successfully.

At the time of writing this guide, the OpenMRS EIP project is at version `1.0-SNAPSHOT`, you will to replace {VERSION}
with the actual version number from, this number can be found as part of the generated jar files or from root pom file
of the OpenMRS EIP project you cloned above.

In practice, the sender and receiver applications are installed on separate physical machines, but for local deployments
on a dev or testing machine this could be the same machine so be careful to use different directories for application 
properties that take directory paths as values e.g. log file, complex obs data directory and others.

### Receiver
1. #### Installing the Receiver Application
    1. Create an installation directory for your receiver app.
    2. Copy to the working directory the `openmrs-eip-app-{VERSION}.jar` file that was generated when you built OpenMRS 
       EIP above, this file should be located in the `app/target` folder.
    3. There is an application.properties file in the `distribution/docs/receiver` directory relative to the root of the 
       OpenMR EIP project, copy it to your installation directory.   
    4. Open the `application.properties` you just copied in step `ii` to the installation directory and set the property values 
       accordingly, carefully read the in-inline documentation as you set each property value.
       **Note:** The receiver sync app makes rest calls to trigger search index rebuilds whenever it processes a payload for 
       an indexed entity e.g. person_name, person_attribute, patient_identifier etc. It's highly recommended that you create 
       a specific user account for this application and use its username and password as values for the `openmrs.username` 
       and `openmrs.password` properties respectively in the `application.properties`.
    5. Go to the folder where you cloned the git repository and copy the `routes` folder under `distribution/receiver` to 
       your installation directory.
    6. It is highly recommended to set the value of the `eip.home` property in your properties file to match the path to your
       installation directory.
    7. Launch the receiver app by navigating to its installation directory from the terminal and run the command below.
    ```shell
    java -jar -Dspring.profiles.active=receiver openmrs-eip-app-{VERSION}.jar
    ```
    Make sure no errors are reported when the application starts, you can find the logs in the configured directory which
    defaults to `{eip.home}/logs/openmrs-eip.log`, where {eip.home} is the path to your installation directory.

1. #### Preparation For Sync
    The receiver application individually keeps track of the timestamp it last received a sync payload from each sending 
    application in the **receiver_sync_status** table, in order to this it needs uniquely identify each sending application. 
    Therefore, before any sending application can start pushing any sync data, the sender needs to obtain this unique 
    identifier from the receiver and configure it in the sender application properties file, this is explained in detail 
    in the sender installation steps.
    
    To register a sender application, log into the management H2 database console application, to access the management 
    database console please refer to [Management Database](../../README.md#management-database), insert a row in the
    **site_info** table which has the columns below,
   
    - `id`: The database primary key, it's auto generated, you don't have to provide a value
    - `name`: A unique name for the sender application, this will be used for display purposes
    - `identifier`: A unique identifier for the sender application
    - `date_created`: The insertion date of the row

    **Note** The identifier is what is given to the sender application team and should never be changed once the sender 
    starts pushing any data.

### Sender

1.  #### Be Unique
    You MUST first request a unique sender id from the team that manages the receiver application and you'll configure it
    in your `application.properties` file.

2. #### Setup MySQL binlog
    Because the sending application relies on the embedded debezium engine, we need to first setup MySQL binary logging in 
    the sender site's OpenMRS database, please refer to this [enabling the binlog](https://debezium.io/documentation/reference/connectors/mysql.html#enable-mysql-binlog)
    section from the debezium docs, you will need some of the values you set when configuring the sender application, 
    the server-id needs to match the value of `debezium.db.serverId` property in the sender `application.properties` file.

    **DO NOT** set the `expire_logs_days` because you never want your logs to expire just in case the sync application is 
    run for a while due to unforeseen circumstances

3. #### Debezium user account

    First we need to create a user account the debezium MySQL connector will use to read the sender site's Openmrs MySQl DB.
    This is just a standard practice so that the account is assigned just the privileges it needs to read the MySQL bin-log 
    files without access to the actual OpenMRS DB data, please refer to this [creating a user](https://debezium.io/documentation/reference/connectors/mysql.html#mysql-creating-user) 
    section from the debezium docs, you will need the created user account details when configuring the sender application.
    i.e. `debezium.db.user` and `debezium.db.password` properties in the sender `application.properties` file.

4. #### Installing the Sender Application
    1. Create an installation directory for your sender app.
    2. Copy to the working directory the `openmrs-eip-app-{VERSION}.jar` file that was generated when you built OpenMRS 
       EIP above, this file should be located in the `app/target` folder.
    3. There is an application.properties file in the `distribution/docs/sender` directory relative to the root of the 
       OpenMR EIP project, copy it to your installation directory.
    4. Open the `application.properties` you just copied above to the installation directory and set the property values 
       accordingly, carefully read the in-inline documentation as you set each property value. Please remember to set the
       `db-sync.senderId` property value which MUST match the id obtained from the receiver team in the [Be Unique](#be-unique) step.
    5. Go to the folder where you cloned the git repository and copy the `routes` folder under `distribution/sender` to
       your installation directory.
    6. It is highly recommended to set the value of the `eip.home` property in your properties file to match the path to your
       installation directory.
    7. Launch the sender app by navigating to its installation directory from the terminal and run the command below.
    ```shell
    java -jar -Dspring.profiles.active=sender openmrs-eip-app-{VERSION}.jar
    ```
    Make sure no errors are reported when the application starts, you can find the logs in the configured directory which
    defaults to `{eip.home}/logs/openmrs-eip.log`, where {eip.home} is the path to your installation directory.

## Console

The sender and receiver each come with a built-in user interface which can be accessed at http://localhost:{server.port} 
where {server.port} is the value of the `server.port` property in the sender/receiver `application.properties` file.

### User Account Management
Tha console applications require user authentication, below is how to setup user accounts;

1. Create a file named `dbsync-users.properties` in your sender/receiver installation directory.
2. Add user account entries to the above file where the keys are the usernames and the values are their respective 
   passwords as demonstrated below;
   ```
   admin=test
   manager=secret
   ```
In the example above, we have defined 2 user accounts, the first user has username **admin**, password **test** and the 
second user has username **manager**, password **secret**

### Open Webapp Setup
The console can also be accessed from an OpenMRS instance as an [open webapp](https://wiki.openmrs.org/x/C4KIBQ) 
by doing the following;

1. Install the [open webapp module](https://addons.openmrs.org/show/org.openmrs.module.open-web-apps-module) in the 
   associated OpenMRS instance.
2. Navigate to the main administration page, click on **Settings** under the **Open Web Apps Module** section
3. Set the value of the **App Base URL** field to `/{openmrsContextPath}/owa` where {openmrsContextPath} is the context
   path of the OpenMRS instance, usually it is `openmrs` which would make the field value `/openmrs/owa` in most cases.
4. Keep note of the value of the **App Folder Path** field, you will need it in step 6 below.
5. Click on **Manage Apps** from the breadcrumbs links, click on the **Browse** button and select the generated
   `owa-{version}.zip` file in the `owa/target` folder of this project where {version} is this project version and then 
   finally click the **Upload** button.
6. Using your favorite editor, edit the file located at {owaAppFolderPath}/owa-{version}/js/config.js where {owaAppFolderPath} 
   is the value you noted in step 4 above and {version} is this project version, then set the value of the `dbSyncUrl` 
   to the URL of the sender/receiver console i.e. http://localhost:{server.port}

You are all set! To access the DB sync console, click on the installed owa from the **Manage Apps** page.

**WARNING!!** **DO NOT** expose the DB sync console on the public internet.

## DB Sync Technical Overview

### Sender Overview
The sender is really a spring boot application with custom camel routes management database.

When the application is fired up in sender mode, the built-in debezium route starts the debezium component which will 
periodically read entries in the MySQL binlog, it constructs an [Event](../../camel-openmrs/src/main/java/org/openmrs/eip/component/entity/Event.java) instance which has several
fields with key fields being the source table name, the unique identifier of the affected row usually a uuid, the
operation that triggered the event(c, u, d) which stand for Create, Update or Delete respectively. The debezium route
sends the event to an intermediate event processor route which has some extra logic in it which in turn sends the event
to the outbound DB sync route.

There is a built-in sender DB sync route that is registered as a listener for DB events, its job is to transform each
message by loading the entity by its uuid, serialize it into a custom format and then publishes the payload to a
configured sync destination, if you're using ActiveMQ to sync between the sender and receiver which is our recommended
option, it means the message would be pushed to a sync queue in an external message broker that is shared with the
receiving sync application.

### Receiver Overview
The receiver is also a spring boot application with its own set of camel routes but instead running at another physical 
location with an OpenMRS installation.

Recall from the sender documentation above, that the out-bound DB sync listener route ends by publishing the payload of
the entity to be synced to a destination shared with the receiving sync application usually a message broker, this is
where the receiver starts, its receiver route connects to this external message broker, consumes messages out of sync
queue and calls the DB sync route which syncs the associated entity to the destination OpenMRS instance's MySQL DB.

### Conflict Resolution In The Receiver
The receiver has a built-in mechanism to detect conflicts between incoming and the existing state of the entity to be
synced i.e. if someone edits a row in the receiver and an ‘older’ sync payload reaches the receiver, this implies the
incoming data is most likely going to overwrite a change made at the receiver. Therefore, there is logic in the receiver
so that if a record was edited on the receiver side and either its date changed or voided/retired is after both of those
in the incoming payload, the application won't sync the entity and it will move the message to the `receiver_conflict_queue`
table. Currently, to resolve the conflict, the entity has to be manually updated in the receiver or sender, then as it
may dictate adjust date changed in the sender so that it is ahead of date voided/retired of the entity in the receiver
and then mark the row as resolved in `receiver_conflict_queue` table.
