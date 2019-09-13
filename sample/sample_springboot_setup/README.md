#Configuration
The receiver and sender directory both contain a `routes` directory with the Camel routes as XML files and a sample application.properties file
The following configuration is necessary:
1. A docker-compose launching 2 MySQL instances (port 3306 for the central db, 3307 for the remote db) can be found in the /db directory. To launch it, open a command dialog, navigate to the /db folder and run >docker-compose up
2. Import a dump of each database: an empty dump for the central database, and a full dump for the remote database.
3. By default, the exchange of data between the sender and the receiver is done with the 'file' Camel endpoint.
You need to create a folder into which the messages will transit and register it in the sender's application.properties files with the following keys/values:
(You can use a JMS endpoint, but an activeMQ broker must be [configured](../sample_activemq_setup/README.md)):
    * camel.input.endpoint=file:<folder_path> (camel.input.endpoint=jms:<jms_queue_name>)

The same path needs to be specified in th receiver's application.properties as follows:
    * camel.input.endpoint=file:<folder_path> (camel.input.endpoint=jms:<jms_queue_name>)

if you are using ActiveMQ, follow these steps:

Uncomment the following lines in sender/application.properties file

* spring.activemq.broker-url=tcp://localhost:62616

* spring.activemq.user=write

* spring.activemq.password=password

Uncomment the following lines in receiver/application.properties file

* spring.activemq.broker-url=failover:(tcp://localhost:63616,tcp://localhost:64616)

* spring.activemq.user=read

* spring.activemq.password=password

4. If you want to also synchronize the content of a folder, you need to specify a path to which will be created a file called 'store' that will keep trace of the files already synchronized to prevent them from being synchronized twice.
The property for that purpose is: 
    * camel.output.endpoint.file.location:<folder_path>
5. Copy the sender application.properties in /camel-openmrs/src/main/resources and rename it as follows: application-sender.properties
6. Copy the receiver application.properties in /camel-openmrs/src/main/resources and rename it as follows: application-receiver.properties
7. Each application will be launched with the profile parameter with which to launch the application. The values are 'sender' or 'receiver'. The profile will also select the right application.properties file accordingly.

#Security
The flow of messages between the sender and the receiver can be encrypted. For that purpose, 2 Camel processors where developed to encrypt and sign (`PGPEncryptService`) message on one side
and verify and decrypt (`PGPDecryptService`) on the other side. They simply need to be registered in the corresponding Camel route before being sent or after being received.

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
