#!/usr/bin/env bash
docker network rm activemq-network
docker network create --subnet 172.28.0.0/24 activemq-network
