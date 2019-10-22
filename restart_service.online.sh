#!/bin/bash
LOG() {
    #echo `date  +'%Y%m%d %H:%M'`, "$@"
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}
load_env="source ~/.bash_profile"
proj_dir="/root/server"

# 1. restart eureka and wait for a second
LOG ">>> starting eureka..."
ssh uniloan04 "${load_env} && cd ${proj_dir}/eureka/ && bash start.sh"

# check "Completed initialization" in eureka log
log_ready=0
while [ $log_ready == 0 ];
do
    log_ready=1
    cnt=`ssh uniloan04 "cat /root/server/eureka/log.txt" | grep -c "Completed initialization"`
    if [ $cnt -eq 0 ]; then
        log_ready=0
    fi

    if [ $log_ready == 0 ]; then
        LOG "eureka is not ready, check 5s later..."
        sleep 5s
    fi
done


# 2. 
LOG ">>> start backend service..."
ssh uniloan01 "${load_env} && cd ${proj_dir}/backend/ && bash start.sh"
ssh uniloan02 "${load_env} && cd ${proj_dir}/backend/ && bash start.sh"

LOG ">>> start paycenter service..."
ssh uniloan01 "${load_env} && cd ${proj_dir}/paycenter/ && bash start.sh"
ssh uniloan02 "${load_env} && cd ${proj_dir}/paycenter/ && bash start.sh"

LOG ">>> start market service..."
ssh uniloan01 "${load_env} && cd ${proj_dir}/market/ && bash start.sh"

LOG ">>> start core service..."
ssh uniloan03 "${load_env} && cd ${proj_dir}/core/ && bash start.sh"

LOG ">>> start cms service..."
ssh uniloan04 "${load_env} && cd ${proj_dir}/cms/ && bash start.sh"

LOG ">>> services restart succeeded."

