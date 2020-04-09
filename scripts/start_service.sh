#!/bin/bash
LOG() {
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}

work_path=`pwd`
profile="test"

if [ $# != 2 ]; then
    echo "Usage: $0 <spring profile> <jar name>"
    echo "  spring profile  : test/test2/online/online2"
    echo "  jar name        : backend/cms/core/eureka/market/kalapa/paycenter"
    exit 1
fi
profile=$1
jar_nm=$2
jar_dir=$2

version="-0.0.1-SNAPSHOT"
bin="${jar_nm}${version}.jar"

#ps aux | grep "${bin}" | grep "spring.profiles.active=${profile}" | grep -v "grep" | awk {'print $2'} | xargs kill -9

cd ${jar_dir}

#java -Xms256m -Xmx512m -Dspring.profiles.active=${profile} -jar $bin >>log.txt 2>&1 &
java -Xms256m -Xmx512m \
     -Drocketmq.client.logRoot="${work_path}/logs/$bin" \
     -Drocketmq.client.logLevel="WARN" \
     -Dspring.profiles.active=${profile} \
     -jar $bin >>log.txt 2>&1 &


if [ $? -ne 0 ]; then
    LOG "[ERROR] start ${bin} failed!"
    exit 1
fi
LOG "start ${bin} succ."

