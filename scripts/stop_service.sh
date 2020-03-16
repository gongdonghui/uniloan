#!/bin/bash
LOG() {
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}

work_path=`pwd`
profile="test"

if [ $# != 2 ]; then
    echo "Usage: $0 <spring profile> <jar name>"
    echo "  spring profile  : test/online/online2"
    echo "  jar name        : backend/cms/core/eureka/market/kalapa/paycenter"
    exit 1
fi
profile=$1
jar_nm=$2
jar_dir=$2

version="-0.0.1-SNAPSHOT"
bin="${jar_nm}${version}.jar"

cnt=`ps aux | grep "${bin}" | grep "spring.profiles.active=${profile}" | grep -v "grep" | wc -l`
if [ $cnt -eq 0 ]; then
    LOG "No ${bin} found."
    exit 0
fi

ps aux | grep "${bin}" | grep "spring.profiles.active=${profile}" | grep -v "grep" | awk {'print $2'} | xargs kill -9

if [ $? -ne 0 ]; then
    LOG "[ERROR] stop ${bin} failed!"
    exit 1
fi
LOG "stop ${bin} succ."

