# Introduction 
This project aims at providing a low level synchronization module based on Apache Camel.
Data are directly pulled from a source OpenMRS MySQL database and pushed to a target OpenMRS MySQL database without any use of OpenMRS service or data model.

The project is composed of two modules:
- The Camel component module. One component to retrieve data from the database and send them and one component
to receive the data and store them in another database.
- The app module, which is a Spring Boot application having either the role of the sender, the receiver or both that will launch the Camel routes.

Each major version of OpenMRS leads to a new branch of the project.

The application uses Lombok to allow creating POJO without coding of getters and setters. A plugin needs to be installed to the IDE to add setters and getters at compile time.

# Getting Started
A sender and a receiver directory are created to simulate a network between a remote database and a central database.
They both a `routes` directory with the Camel routes as XML files.
The following configuration is necessary:
1. Install a Mysql database
2. Create a schema and import a dump corresponding to the same major version of OpenMRS than the one of the branch you are working on.
3. Set up the sender's application.properties files with the database url and credentials
4. By default, the exchange of data between the sender and the receiver is done with the 'file' Camel endpoint. 
You need to create a folder into which the messages will transit and register it in the sender's application.properties files with the following keys/values:
    * `camel.output.endpoint=file:<folder_path>`
The same path needs to be specified in th receiver's application.properties as follows:
    * `camel.input.endpoint=file:<folder_path>`
5. If you want to also synchronize a folder, you need to specify a path to which will be created a file called 'store' that will keep trace of the files already synchronized to prevent them from being synchronized twice.
The property for that purpose is: 
    * `camel.output.endpoint.file.location:<folder_path>`
6. Each application will be launched with the profile parameter with which to launch the application. The values are 'sender' or 'receiver'. The profile will also select the right application.properties file accordingly.

#Security
The flow of messages between the sender and the receiver can be encrypted. For that purpose, 2 Camel processors where developed to encrypt and sign (`PGPEncryptService`) message on one side
and verify and decrypt (`PGPDecryptService`) on the other side. They simply need to be registered in the corresponding Camel route before being sent or after being received.

The encryption is performed by PGP. So public and private keys shall be generated for each side of the exchange.
* To encrypt the message, the sender needs the receiver's public key
* To sign the message, the sender needs a private key
* To verify the message, the receiver needs the sender's private key
* To decrypt the message, the receiver needs a private key

Thus, the sender needs to hold it's own private key and the receiver's public key in a folder and the application.properties file of the sender should be as follows:

`pgp.sender.keysFolderPath=<folder_path>` By default, the path is a relative path from the root of the module. It is better to set a folder located outside the project. To do so add the prefix `folder:` to the path

`pgp.sender.userId=<private_key_user_id>`

`pgp.sender.password=<private_key_password>`

`pgp.sender.receiverUserId=<reveiver_user_id>`

The receiver needs to hold it's private key and all the public keys of the sender's providing data in a folder and the application.properties file of the sender should be as follows:

`pgp.receiver.keysFolderPath=<folder_path>` By default, the path is a relative path from the root of the module. It is better to set a folder located outside the project. To do so add the prefix `folder:` to the path

`pgp.receiver.password=<private_key_password>`

**To be detected by the program, the private keys should all end with -sec.asc and the public keys should all end with -pub.asc**

# File synchronization
It is also possible to synchronize the content of a folder. The folder sync is performed via a different Camel route, but files will be transferred through the same endpoint. They will thus be received by the receiver via the same endpoint as the entities.
To differentiate entities from files at reception, files are encoded in Base64 and the result is placed between the `<FILE>`' and `</FILE>` tags.

# Build and Test
Unit ant Integration tests were only coded for the core module.

Integration tests are located in the core/src/it folder. They are run by default by maven test phase. As they are a bit long to execute,
you can skip them by adding the *skipITests* parameter to the build.

# Architecture
The project has a classic layer architecture with a service layer and a DAO layer.
Each action (get or save entities) of the Camel endpoints comes with the name of the table the action is performed upon.
A Facade (`EntityServiceFacade`) is used to select the right service to use to get or save the entities according to the table name passed in parameter.

Once entities are retrieved from the database, they are mapped to a model. The model does not reproduce the eventual links that an entity could have with other entities. 
It only stores the UUID of the linked entities with a constant rule:
Let's consider the entities A and B:

    class A {
        private String uuid;
    }

    class B {
        private String uuid;
        
        private A a;
    }

The model corresponding to B called BModel will look like:

    class BModel {
        private String uuid;
        
        private String aUuid;
    }
    
The model is then encapsulated in a `TransferObject` containing the type of the object and is then marshaled to a json string.

Once the json is received on the other side, it is unmarshaled into a model with the help of the object type stored in the `TransferObject`.
To reconstruct the entity and its linked entities, the mapper will use the UUID to get the linked entity from the database if it exists.
If it does not exist, it will create a light entity (`LightEntity`) with only the UUID and the non nullable attributes with default values. 
If one of the mandatory values is also an entity which does not exist, it is attached to a placeholder entity.
After a round of synchronisation, placeholder entities should no longer be attached to any entity. For example:

Let's consider the following situation when an Observation is synchronized before Encounter and Patient and Encounter is mandatory in Observation and Patient is mandatory in Encounter:

Observation -> Encounter -> Patient

An empty Encounter with the right UUID is created, but the encounter's patient is attached to a placeholder as we do not have any information about the patient.
When the Encounter is synchronized, An empty patient is created with the right UUID which was present in the EncounterModel and the Encounter will be attached to this patient.
The placeholder patient is no longer attached to the Encounter.
When the Patient is synchronized, the empty patient will be updated with the right values.
 
# Dependencies
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data](https://spring.io/projects/spring-data)
* [Apache Camel](https://camel.apache.org/)
* [Lombok](https://projectlombok.org/)
* [Bouncy Castle](https://www.bouncycastle.org/fr/)
