#!/usr/bin/env bash

if [ -f "/usr/local/bin/docker-machine" ]; then
    eval "$(docker-machine env default)"
fi

COMPOSE_FILE=docker-compose.yml

# Start everything
if [[ $* == *--detached* ]]
then
    docker-compose --file $COMPOSE_FILE up -d
else
    docker-compose --file $COMPOSE_FILE up

    # cleanup - Delete the containers
    docker-compose --file $COMPOSE_FILE rm --force
fi

#Delete the old images to pull the latest - This can be removed when we want to use the previous images
docker rmi log-processor-service:latest