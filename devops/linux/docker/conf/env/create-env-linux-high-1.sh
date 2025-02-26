#!/bin/bash

export USE_LOCAL_REGISTRY=${USE_LOCAL_REGISTRY:-"true"}
export PROJECT_DIR=$(builtin cd ../../; pwd)
export PROJECT_DATA_DIR=${PROJECT_DIR}/docker_data/data
export PROJECT_DATA_APPS_DIR=${PROJECT_DIR}/docker_data/data-apps
export PROJECT_DATA_MONITOR_DIR=${PROJECT_DIR}/docker_data/data-monitor

export NODE1=$(hostname)
#   export NODE1_IP=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | head -1 | awk '{ print $2 }')
export NODE1_IP=$(hostname -i)

export SCALE_NGINX=1
export SCALE_KEYCLOAK_TEST_SERVER=1
export SCALE_KAFKA_01=1
export SCALE_KAFKA_02=1
export SCALE_KAFKA_03=1
export SCALE_ZERO_01=1
export SCALE_ALPHA_01=1
export SCALE_ALPHA_02=1
export SCALE_ALPHA_03=1
export SCALE_RATEL=1
export SCALE_POSTGRESQL=1

export POSTGRESQL_USERNAME="postgres"
export POSTGRESQL_PASSWORD="postgres"
export POSTGRESQL_DATABASE="notifications"

export KAFKA_SERVERS="kafka-01:9092,kafka-02:9092,kafka-03:9092"
export DGRAPH_HOSTS="alpha-01,alpha-02,alpha-03"
export DGRAPH_PORTS="9080,9081,9082"

# API related env vars
export KC_REALM_NAME="jempi-dev"
export KC_API_URL="http://keycloak-test-server:8080"
export KC_JEMPI_CLIENT_ID="jempi-oauth"
export KC_JEMPI_CLIENT_SECRET="Tbe3llP5OJIlqUjz7K1wPp8YDAdCOEMn"
export KC_JEMPI_ROOT_URL="http://localhost:3000"
export JEMPI_SESSION_SECRET="c05ll3lesrinf39t7mc5h6un6r0c69lgfno69dsak3vabeqamouq4328cuaekros401ajdpkh60rrt"
export JEMPI_FILE_IMPORT_MAX_SIZE_BYTE=10485760
# Deployment related env vars
export JEMPI_SESSION_SECURE="false"
export JEMPI_SESSION_DOMAIN_NAME="localhost"

# ram limit for linker
export POSTGRESQL_RAM_LIMIT="16G"
export KEYCLOAK_TEST_SERVER_RAM_LIMIT="8G"
export NGINX_RAM_LIMIT="16G"
export HAPROXY_RAM_LIMIT="32G"
export KAFKA_RAM_LIMIT="32G"
export DGRAPH_RAM_LIMIT="32G"
export ASYNC_RECEIVER_RAM_LIMIT="16G"
export ETL_RAM_LIMIT="16G"
export CONTROLLER_RAM_LIMIT="16G"
export EM_RAM_LIMIT="16G"
export LINKER_RAM_LIMIT="32G"
export API_RAM_LIMIT="16G"
export UI_RAM_LIMIT="16G"

#UI env vars
export NODE_ENV="developement"
export REACT_APP_JEMPI_BASE_URL=http://${NODE1_IP}:50000/JeMPI
export REACT_APP_MOCK_BACKEND="false"
export REACT_APP_ENABLE_SSO="false"
export REACT_APP_MAX_UPLOAD_CSV_SIZE_IN_MEGABYTES="128"

# DON'T CHANGE
export REGISTRY_NODE_IP=${NODE1}:5000/v2

if [ $USE_LOCAL_REGISTRY == 'true' ]; then
    export IMAGE_REGISTRY="$REGISTRY_NODE_IP/"
else
    export IMAGE_REGISTRY=""
fi

envsubst < conf-env-high-1-pc.template > conf.env
