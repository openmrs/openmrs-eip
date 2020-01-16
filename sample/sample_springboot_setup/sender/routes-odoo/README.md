# Routes

#### dead-letter-channel-route.xml
Route to receive all messages in error. Default configuration logs messages, but any other Camel endpoint can be use to handle error messages

###### calling routes
all routes

###### outgoing routes
None

#### decision-route.xml
Route to redirect messages to corresponding sub-routes according to the type of the entity the message holds.

###### calling routes
select-route-only-odoo-route.xml

###### outgoing routes
decision-route.xml

person-name-to-odoo-route.xml

person-address-to-odoo-route.xml

patient-identifier-to-odoo-route.xml

observation-to-odoo-route.xml

#### get-country-from-odoo-route.xml
Route that takes an array with patient address data and returning that same array with the country replaced by the country oddo id fetched in Odoo

###### calling routes
select-route-only-odoo-route.xml

###### outgoing route
None, only updates the message

#### observation-to-odoo-workorder-status-route.xml
Route that takes an observation formatted in json as an input. If the obs is linked to the concept with the mapping *ICRC:PRP_CC_Section_Activity*, the obs is an obs group and the route fetches all the obs linked to this group.
From these obs, the route stores the following attributes as properties: the manufacturing order id, the sequence number, the start date time, the pause date time, the validation date time.
The message is then passed to the process-obs-route.xml.

###### calling routes
decision-route.xml

###### outgoing route
process-obs-route.xml

#### patient-address-to-odoo-route.xml
Route that updates the address of a patient whose uuid is contained in the message json body under the path $.model.personUuid

###### calling routes
decision-route.xml

###### outgoing route
send-patient-to-odoo-route.xml

#### patient-identifier-to-odoo-route.xml
Route that updates the identifier of a patient whose uuid is contained in the message json body under the path $.model.personUuid

###### calling routes
decision-route.xml

###### outgoing route
send-patient-to-odoo-route.xml

#### patient-name-to-odoo-route.xml
Route that updates the name of a patient whose uuid is contained in the message json body under the path $.model.personUuid

###### calling routes
decision-route.xml

###### outgoing route
send-patient-to-odoo-route.xml

#### patient-to-odoo-route.xml
Route that updates the gender and the date of birth of a patient whose uuid is contained in the message json body under the path $.uuid

###### calling routes
decision-route.xml

###### outgoing route
send-patient-to-odoo-route.xml

#### process-obs-route.xml
Route that updates a work order state in odoo according to the value of the status of the obs corresponding to a manufacturing order.
All data regarding the work order being modified are stored as properties in the incoming message
This route first checks the states of all the work orders linked to the manufacturing order of the work order being processed so that their status remains consistent with each other after the modification of the current work order.
All work orders whose status has been modified are changed according to a resulting action as follows:
- If the action is START, a time line is created in Odoo with the start date equals to the obs value date for the work order and its state is changed to PROGRESS
- If the action is PAUSE, the end date of last time line of the work order is set to the value date of the obs and its state is changed to PROGRESS
- If the action is CLOSE, the end date of last time line of the work order is set to the value date of the obs and its state is changed to DONE
- If the action is CANCEL, all time lines linked to the work order are deleted and its state is changed to READY

###### calling routes
observation-to-odoo-workorder-status-route.xml

###### outgoing route
None, sends data directly to Odoo

#### schedule-route.xml
Route that triggers the synchronisation with Odoo. Each given interval, the route fetches the last sync date of all the tables to synchronize and sends the data to the select-route.xml

###### calling routes
None, Trigger of the sync process

###### outgoing route
select-route.xml

#### select-route.xml
Route that receives as a message the table synchronize with the last sync date of that table and uses the OpenMRS Camel component to get all the modified data after that date.
This route also performs Odoo authentication and places the odoo authentication token in a header.
This route also saves the current date as the last sync date for the current table via the processor *saveSyncStatusProcessor*

###### calling routes
schedule-route.xml

###### outgoing-route
decision-route.xml

#### send-patient-to-odoo-route.xml
Route that stores patient data in Odoo. The input is a json sent with the 2 keys *endpoint* and *url*. Endpoint is the object name to modify in odoo (res.partner for patients) and the url is a concatenation of the attributes to change in Odoo as queryParams (eg.: name=toto&birthDate=2019-12-24).

As patient data come from different OpenMRS tables, a H2 management table *OdooOpenmrsIdMapping* is generated to store the mapping between the odoo id of a patient and the OpenMRS patient uuid.
When the Camel route processes data regarding a patient, if no row is present in the *OdooOpenmrsIdMapping* table, then that means the patient is not already present in Odoo and a POST is performed. A row is also added in the *OdooOpenmrsIdMapping* table.
If a row is already present in the *OdooOpenmrsIdMapping* table, that means a patient with this id has already been posted to Odoo and a PUT is performed.
As the name of the patient in Odoo is mandatory, if the data saved does not contain the name, the value *[Unknown]* is set as a partner name.

###### calling routes
patient-address-to-odoo-route.xml
patient-name-to-odoo-route.xml
patient-name-to-odoo-route.xml
patient-to-odoo-route.xml

###### outgoing-route
None, saves patient data in Odoo
