#!/bin/sh

mvn clean install
mvn clean package
docker-compose build