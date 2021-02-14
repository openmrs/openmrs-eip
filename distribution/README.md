# Installation Guide For DB Sync

1. [Introduction](#introduction)
2. [Requirements](#requirements)
3. [Assumptions](#assumptions)
5. [Installation](#installation)
    1. [JMS Server](#jms-server)
    2. [Building OpenMRS EIP](#building-openmrs-eip)
    3. [Receiver](#receiver)
    4. [Sender](#sender)
6. [Security](#security)

## Introduction
This installation guide is for those intending to set up database synchronization between 2 OpenMRS databases.
Currently, only one-way DB sync is supported therefore, you can have a receiver application to write to the destination 
database and one or more sender applications to read from the source database(s). You would also need a JMS server to 
provide a way for a sender and receiver applications to exchange sync data.

Please try to make sure you install the required applications in the order laid out in the installation section i.e. 
JMS Server, receiver and then sender(s).

## Requirements
- A unix operating system (Never been tested on windows)
- [Apache Maven](#http://maven.apache.org/install.html)
- [OpenJDK 8](#https://openjdk.java.net/install/)
- [Git](#https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## Assumptions

#### One-way data sync
Currently, the receiver application doesn't sync data back to a sender but in future release support for two-way sync 
will be supported.

#### OpenMRS Instances are already installed
This guide doesn't cover installation of the sender and receiver OpenMRS instances and databases, if not, please refer 
to the [OpenMRS](#https://openmrs.org) documentation.

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
any of your choice as long as it is supported by spring boot and apache camel's [jms](#https://camel.apache.org/components/latest/jms-component.html)
or [activemq](#https://camel.apache.org/components/latest/activemq-component.html) components but you would have to make
more changes to the sender and receiver application properties jms settings, please see
[How to install ArtemisMQ](#https://activemq.apache.org/components/artemis/documentation/latest/using-server.html)

Remember to keep note of the ArtemisMQ broker port and URL to the console application, you can peek at what's going on
inside your JMS server using the console application.

**Key things to note**
- It's VERY important to understand how JMS servers work, key concepts especially when it comes to topics vs queues
  subscription and message durability before picking one over the other. We recommend using topics therefore our
  sender and receiver application properties file templates have topic settings with durable subscriptions as we will
  later see when configuring our sender and receiver applications.
- In case you're using a topic like we recommend, after installing ArtemisMQ and creating your broker instance,
  your **MUST** connect the receiver sync application first with a durable topic subscription before any sender sync
  application connects and publishes any message. Otherwise, any message pushed by a sender to the topic will before you
  do so won't be delivered to the receiver.
- With topics, it also implies that whenever you want to onboard a new sender application to sync data from a new site
  that wishes to join the party to push data to the receiver and it's using a different JMS instance or topic name,
  then the receiver **MUST** always connect first so that a durable subscription is created for it before the new sender
  application can start pushing sync data. Technically this applies whenever you switch to a new JMS broker instance.
  
### Building OpenMRS EIP
From the terminal, navigate to your working directory, clone and build the project to generate the executable artifacts 
by running the commands below.
```shell
git clone https://github.com/openmrs/openmrs-eip.git
cd openmrs-eip
mvn clean install
```
Make sure the build completed successfully.

At the time of writing this guide, the OpenMRS EIP project is at version `1.0-SNAPSHOT`, you will to replace {VERSION}
with the actual version number from, this number can be found as part of the generated jar files or from root pom file
of the OpenMRS EIP project you cloned above.

In practice, the sender and receiver applications are installed on separate physical machines, but for local deployments
on a dev or testing machine this could be the same machine so be careful to use different directories for application 
properties that take directory paths as values e.g. log file, complex obs data directory and others.

### Receiver 
1. Create an installation directory for your receiver app.
2. Copy to the working directory the `dbsync-receiver-app-{VERSION}.jar` file that was generated when you built OpenMRS 
   EIP above, this file should be located in the `dbsync-receiver-app/target` folder.
3. There is an application.properties file in the `dbsync-receiver-app` directory found at the root of the OpenMR EIP 
   project, copy it to your installation directory.   
4. Open the `application.properties` you just copied above to the installation directory and set the properties values 
accordingly, carefully read the in-inline documentation as you set each property value.
5. Launch the receiver app by navigating to its installation directory from the terminal and run the command below.
```shell
java -jar dbsync-receiver-app-{VERSION}.jar
```
Make sure no errors are reported when the application starts, you can find the logs in the configured directory which 
defaults to `{USER.HOME}/.openmrs-eip/logs/openmrs-eip.log`, where {USER.HOME} is the path to your user home directory.

### Sender
1. Create an installation directory for your sender app.
2. Copy to the working directory the `dbsync-sender-app-{VERSION}.jar` file that was generated when you built OpenMRS 
   EIP above, this file should be located in the `dbsync-sender-app/target` folder.
3. There is an application.properties file in the`dbsync-sender-app` directory found at the root of the OpenMR EIP 
   project, copy it to your installation directory.
4. Open the `application.properties` you just copied above to the installation directory and set the properties values
   accordingly, carefully read the in-inline documentation as you set each property value.
5. Launch the sender app by navigating to its installation directory from the terminal and run the command below.
```shell
java -jar dbsync-sender-app-{VERSION}.jar
```
Make sure no errors are reported when the application starts, you can find the logs in the configured directory which
defaults to `{USER.HOME}/.openmrs-eip/logs/openmrs-eip.log`, where {USER.HOME} is the path to your user home directory.

# Security
Sync messages exchanged  between the sender and the receiver can be encrypted. For that purpose, 2 Camel processors were
developed to encrypt and sign (`PGPEncryptService`) message on one side and verify and decrypt (`PGPDecryptService`) on 
the other side. They simply need to be registered in the corresponding Camel route before being sent or after being 
received.

The encryption is performed by PGP. So public and private keys shall be generated for each side of the exchange.
* To encrypt the message, the sender needs the receiver's public key
* To sign the message, the sender needs a private key
* To verify the message, the receiver needs the sender's private key
* To decrypt the message, the receiver needs a private key

Thus, the sender needs to hold it's own private key and the receiver's public key in a folder and the 
`application.properties` file of the sender should be as follows:

`pgp.sender.keysFolderPath=<folder_path>` The path is a relative path of the working directory of the application.

`pgp.sender.userId=<private_key_user_id>`

`pgp.sender.password=<private_key_password>`

`pgp.sender.receiverUserId=<reveiver_user_id>`

The receiver needs to hold it's private key and all the public keys of the sender's providing data in a folder and the 
`application.properties` file of the sender should be as follows:

`pgp.receiver.keysFolderPath=<folder_path>` The path is a relative path of the working directory of the application.

`pgp.receiver.password=<private_key_password>`

**To be detected by the program, the private keys should all end with -sec.asc and the public keys should all end with -pub.asc**