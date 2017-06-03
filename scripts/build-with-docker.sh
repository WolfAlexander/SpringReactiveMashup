#!/usr/bin/env bash

echo Setting docker enviroment variables
export DOCKER_HOST=https://192.168.99.100:2376
export DOCKER_CERT_PATH=C:/Users/Alexander/.docker/machine/machines/default

echo Building the Spring Reactive Machup
cd ..
mvn package docker:build -Ddocker.host=$DOCKER_HOST -Ddocker.cert.path=$DOCKER_CERT_PATH