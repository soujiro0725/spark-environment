#!/usr/local/bin/zsh

docker-machine start spark
eval $(docker-machine env spark)
#docker run -i -t -h sandbox sequenceiq/spark:latest /etc/bootstrap.sh -bash

