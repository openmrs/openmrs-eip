# Sample configuration

This example emulates a scenario where a remote clinic is on the field collecting data and is periodically sending data to the main HQ server, the central server, the receiver.

The **sender** is the **remote**. The **receiver** is the **central**.

[receiver/](./receiver) and [sender/](./sender) directories both contain a [routes/](./routes) folder with the Camel routes XML files and a sample _application.properties_ file.

_Note: Most of the paths set in this example are pointing to `/tmp/`, which will get wiped upon next computer restart. Sync information will not be persisted. Use another location if you want to persist across restarts_

### 1. Launch 'remote' and 'central' MySQL containers
A Docker Compose project to launch 2 MySQL instances (port `3306` for the central database, `3307` for the remote) can be found in the [db/](./db/) directory. To launch it, run:
```
cd sample/sample_springboot_setup/db
docker-compose up
```

### 2. Import database dumps
Restore a database archive for each database:


- a dump with not much data for the central database
```
cd sample/sample_springboot_setup/db
zcat dump_receiver.zip | docker exec -i db_db_central_1 /usr/bin/mysql -u root --password=root openmrs
```

- a dump with lots of data for the remote database.
```
cd sample/sample_springboot_setup/db
zcat dump_sender_2.3.zip | docker exec -i db_db_remote_1 /usr/bin/mysql -u root --password=root openmrs
```

This operation will take few minutes.


### 3. Configure `file` Camel endpoint (if not using `jms` queues)

By default, the exchange of data between the sender and the receiver is done with the `file` Camel endpoint.

Copy and rename the sample _application.properties_ files:
```
cp sample/sample_springboot_setup/sender/application.properties app/src/main/resources/application-sender.properties
```
```
cp sample/sample_springboot_setup/receiver/application.properties app/src/main/resources/application-receiver.properties
```

Create a folder into which the messages will transit.
```
mkdir -p /tmp/openmrs-dbsync/sync
```
and register this location in the sender and receiver _application.properties_ file just copied with the appropriate values.

- sender:
```
nano app/src/main/resources/application-sender.properties
```
and set the following options:
```
camel.output.endpoint=file:/tmp/openmrs-dbsync/sync
```
```
# Sender DB (remote) port is 3307
spring.openmrs-datasource.jdbcUrl=jdbc:mysql://localhost:3307/openmrs
```

- receiver:
```
nano app/src/main/resources/application-receiver.properties
```
and set the following option:
```
camel.input.endpoint=file:/tmp/openmrs-dbsync/sync
```

### 3-bis. Configure `jms` Camel endpoint, if not using the `file` endpoint

You can also use a JMS endpoint, but an ActiveMQ broker must be [configured](../sample_activemq_setup/README.md) first.

Then configure the sender and receiver properties file as follows:
```
camel.input.endpoint=jms:openmrs.sync.queue
```

Uncomment the following lines in **sender-application.properties** file
```
spring.activemq.broker-url=tcp://localhost:62616
spring.activemq.user=write
spring.activemq.password=password
```

Uncomment the following lines in **eceiver-application.properties** file
```
spring.activemq.broker-url=failover:(tcp://localhost:63616,tcp://localhost:64616)
spring.activemq.user=read
spring.activemq.password=password
```

### 4. Folder synchronization
If you want to also synchronize the content of a folder, you need to specify a path to which will be created a file called 'store' that will keep trace of the files already synchronized to prevent them from being synchronized twice.
The property for that purpose is:
```
camel.output.endpoint.file.location=/tmp/openmrs-dbsync/store
```

Note: To disable this feature, just delete the route, or rename it to **directory-sync-route.xml.disable**

### 5. Rebuild the project

```
mvn clean install
```

### 6. Launch the Spring Boot apps
Each application will be launched with the appropriate Spring Boot profile parameter. The values are `sender` or `receiver`. The profile will also select the right `application.properties` file.
- sender app:
 ```
cd sample/sample_springboot_setup/sender
java -jar -Dspring.profiles.active=sender ../../../app/target/openmrs-sync-app-1.0-SNAPSHOT.jar
```
- receiver app:
```
cd sample/sample_springboot_setup/receiver
java -jar -Dspring.profiles.active=receiver ../../../app/target/openmrs-sync-app-1.0-SNAPSHOT.jar
```

# Security
The flow of messages between the sender and the receiver can be encrypted. For that purpose, 2 Camel processors were developed to encrypt and sign (`PGPEncryptService`) message on one side and verify and decrypt (`PGPDecryptService`) on the other side. They simply need to be registered in the corresponding Camel route before being sent or after being received.

The encryption is performed by PGP. So public and private keys shall be generated for each side of the exchange.
* To encrypt the message, the sender needs the receiver's public key
* To sign the message, the sender needs a private key
* To verify the message, the receiver needs the sender's private key
* To decrypt the message, the receiver needs a private key

Thus, the sender needs to hold it's own private key and the receiver's public key in a folder and the application.properties file of the sender should be as follows:

`pgp.sender.keysFolderPath=<folder_path>` The path is a relative path of the working directory of the application.

`pgp.sender.userId=<private_key_user_id>`

`pgp.sender.password=<private_key_password>`

`pgp.sender.receiverUserId=<reveiver_user_id>`

The receiver needs to hold it's private key and all the public keys of the sender's providing data in a folder and the application.properties file of the sender should be as follows:

`pgp.receiver.keysFolderPath=<folder_path>` The path is a relative path of the working directory of the application.

`pgp.receiver.password=<private_key_password>`

**To be detected by the program, the private keys should all end with -sec.asc and the public keys should all end with -pub.asc**
