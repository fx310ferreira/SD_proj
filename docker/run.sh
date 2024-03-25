#!/bin/bash

if [ "$(sudo docker ps -aq -f name=googol-container)" ]; then
	# If the container exists, just start it
	sudo docker start googol-container
else
	# If the container doesn't exist, build it and then run it
	sudo docker build -t googol .
	sudo docker run -d --name googol-container -p 5433:5432 googol
fi
