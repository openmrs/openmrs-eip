#Introduction
This module contains utility classes to facilitate the use of the Camel routes to synchronize with Odoo.
* The *aggregator* directory contains classes to be used with the EIP content enricher. Each class takes 2 Camel exchanges and combines them according to the code in the *aggregate* method.
* The *exception* directory contains the Generic OdooException that is thrown in case of error byt the *WorkOrderStatusProcessor*
* The *workordermanager* directory contains the classes used to maintain a list of workorders ordered and with a consistent state relatively to each other.
For instance, No work order can have a state CLOSED if it is after a work order with a state PROGRESS.

Basically the state of the work orders should follow the sequence: [DONE, DONE, ..., PROGRESS, READY, ...,READY]

#Testing
All the module is unit tested and the WorkOrderStatusProcessor has integration tests
