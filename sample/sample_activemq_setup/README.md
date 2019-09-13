To use the openMRS synchro application with ActiveMQ, we need to simulate a network of activMQ brokers with a central and a remote broker.
1. First create docker network by executing the shell file create_docker_network.sh 
2. The central broker is composed of two nodes to simulate load balancing in node1 and node2 directories
Navigate to /node1 and execute the command >docker-compose up
Navigate to /node2 and execute the command >docker-compose up
3. Finally, lauch the remote broker, by navigating to remote and executing the command >docker-compose up
