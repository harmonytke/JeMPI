#!/bin/bash

set -e
set -u

source ../../conf.env
docker service logs --raw ${STACK_NAME}_alpha-03
echo
