# Routes

####dead-letter-channel-route.xml
Route to receive all messages in error. Default configuration logs messages, but any other Camel endpoint can be use to handle error messages

######calling routes
all routes

######outgoing routes
None

####write-route.xml
Route that reads messages from an input endpoint (can be JMS queue or directory).
If it has the *<FILE>* and *</FILE>* tags, it is processed as a file, unmarshalled and stored in a target directory.
Otherwise, it is passed to the OpenMRS Camel endpoint to be stored in the database.

If the message was encrypted before being sent on sender's side, the message needs to be decrypted right after being received by the route via the processor *pgpDecryptService*.

######calling routes
None

######outgoing routes
None
