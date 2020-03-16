#!/bin/bash

LOG() {
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}

profile="test"

if [ $# == 1 ]; then
    profile=$1
fi

LOG "spring profile: ${profile}"
jars=("cms" "backend" "market" "core" "eureka" "kalapa" "paycenter")

for jar in ${jars[@]};
do
    bash restart_service.sh $profile $jar
done

