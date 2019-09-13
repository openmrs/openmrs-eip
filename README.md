# Introduction 
This project aims at providing a low level synchronization module based on Apache Camel.
Data are directly pulled from a source OpenMRS MySQL database and pushed to a target OpenMRS MySQL database without any use of OpenMRS service or data model.

The project is composed of two modules:
- The Camel component module. One component to retrieve data from the database and send them and one component
to receive the data and store them in another database.
- The app module, which is a Spring Boot application having either the role of the sender, the receiver or both that will launch the Camel routes.

Each major version of OpenMRS leads to a new branch of the project.

The application uses Lombok to allow creating POJO without coding of getters and setters. A plugin needs to be installed to the IDE to add setters and getters at compile time.

# Configuration
A sender and a receiver directory are created to simulate a network between a remote database and a central database. They are both located in the /sample/sample_springboot_setup directory.
Please refer to the [READ.ME](sample/sample_springboot_setup/README.md) for details about configuration.

The synchro application can be used along with ActiveMQ via jms queues. A sample configuration can be found in the /sample/sample_activemq_setup directory.
Please refer to the [READ.ME](sample/sample_activemq_setup/README.md) for details about configuration.

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

Once the json is received on the other side, it is unmarshalled into a model with the help of the object type stored in the `TransferObject`.
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
