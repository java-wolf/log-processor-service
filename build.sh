#! /bin/bash

if [ -d "./target/" ]
then
	rm -r target/
fi
docker build -t log-processor-service .
docker create -it --name log-processor-service log-processor-service bash
docker cp log-processor-service:/target ./target
docker rm -f log-processor-service