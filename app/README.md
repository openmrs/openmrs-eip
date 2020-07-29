# Camel Router Spring Project

Build the project
```
mvn install
```

Run project with Maven:
```
mvn camel:run -Dspring.profiles.active=sender
```
or run the built **jar**
```
java -jar -Dspring.profiles.active=sender target/openmrs-eip-app-1.0-SNAPSHOT.jar
```

For more help see the Apache Camel documentation
http://camel.apache.org/
