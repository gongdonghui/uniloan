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

bash stop_service.sh $1 $2
if [ $? -ne 0 ]; then
    LOG "[ERROR] stop ${bin} failed!"
    exit 1
fi
LOG "stop ${bin} succ."

bash start_service.sh $1 $2

if [ $? -ne 0 ]; then
    LOG "[ERROR] start ${bin} failed!"
    exit 1
fi
LOG "start ${bin} succ."

