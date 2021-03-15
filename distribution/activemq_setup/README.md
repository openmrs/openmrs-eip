# ActiveMQ configuration

To use the OpenMRS synchro application with ActiveMQ, we need to simulate a network of ActivMQ brokers with a central and a remote broker.

## Create docker network

```
sh distribution/activemq_setup/create_docker_network.sh
```

## Create the 'central' ActiveMQ brokers

The central broker is composed of two nodes to simulate load balancing.
Start both nodes:

```
cd distribution/activemq_setup/central/activemq/node1/
docker-compose up
```
```
cd distribution/activemq_setup/central/activemq/node2/
docker-compose up
```

## Start the 'remote' ActiveMQ broker

Finally, launch the remote broker:

```
cd distribution/activemq_setup/remote/activemq/
docker-compose up
```
