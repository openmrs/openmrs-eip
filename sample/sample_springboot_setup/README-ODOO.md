# Prerequisites

The following tools should be installed to be able to build and run the application:

- JDK 8
- Maven 3 or above

# Build

To build the application package, run the command `mvn clean install`
When the build succeeds, the jar is located in the folder <project-root>/app/target

# Install

- Create a folder
- Paste the openMrs sync jar file in it
- Paste a copy of the application.properties file located in <project-root>/sample/sample_spring_boot_setup/sender
- Create a subfolder /routes and paste a copy of the following routes from <project-root>/sample/sample_spring_boot_setup/sender/routes

get-country-from-odoo-route.xml

patient-address-to-odoo-route.xml

patient-identifier-to-odoo.xml

patient-name-to-odoo-route.xml

patient-to-odoo-route.xml

schedule-route.xml

select-route-only-odoo.xml

send-to-odoo-route.xml

Configure the following Odoo routes with the correct url to Odoo:

- select-route.xml line 8
- select-route-only-odoo.xml line 20 and 31
- get-country-from-odoo-route.xml line 11 

# Run

To run the application, open a command dialog and navigate to the folder created in the above section.
Run `java -jar openmrs-sync-app-1.0-SNAPSHOT.jar --spring.profiles.active=sender`
