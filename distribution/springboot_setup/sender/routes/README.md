# Routes

####dead-letter-channel-route.xml
Route to receive all messages in error. Default configuration logs messages, but any other Camel endpoint can be use to handle error messages

######calling routes
all routes

######outgoing routes
None

####outbound-complex-obs-route.xml
Route that reads the content of the directory passed as input via spring the property *camel.output.endpoint.complex.obs.data.directory*.
The files are marshalled to Base64 and the resulting content is appended with *<FILE>* at the beginning and *</FILE>* at the end. This allows the receiver side of the sync process to differenciate complex obs files and entities.
The resulting strings corresponding to the files are then passed to a Camel endpoint that can be for instance a JMS queue or a File endpoint.

It is possible to encrypt the message before dispatching it via the processor *pgpEncryptService*.

######calling routes
None, this is a trigger route

######outgoing routes
None, the output of this route is the endpoint to with to write the files contained in the given directory

####schedule-route.xml
Route that triggers the synchronisation process. Each given interval, the route fetches the last sync date of all the tables to synchronize and sends the data to the select-route.xml

######calling routes
None, Trigger of the sync process

######outgoing route
select-route.xml

####select-route.xml
Route that receives as a message the table synchronize with the last sync date of that table and uses the OpenMRS Camel component to get all the modified data after that date.
The entities corresponding to the table extracted are then passed to a Camel endpoint that can be for instance a JMS queue or a File endpoint.
This route also saves the current date as the last sync date for the current table via the processor *saveSyncStatusProcessor*.

It is possible to encrypt the message before dispatching it via the processor *pgpEncryptService*.

######calling routes
schedule-route.xml

######outgoing-route
None, the output of this route is the endpoint to with to write the entities as json. Can be JMS queue, a folder...
