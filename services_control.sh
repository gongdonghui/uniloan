#!/bin/bash
LOG() {
    #echo `date  +'%Y%m%d %H:%M'`, "$@"
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}
load_env="source ~/.bash_profile"
#proj_dir="/root/server"
#profile="online"

stop_bin="stop_service.sh"
#stop_bin="stop_service.old.sh"
restart_bin="restart_service.sh"

if [ $# != 3 ]; then
    echo "Usage: $0 [stop|restart] spring_profile project_root_dir"
    echo "  spring_file     : online | online2"
    echo "  project_root_dir: "
    echo "            /root/server   for online"
    echo "            /root/server2  for online2"
    exit 1
fi

op="$1"
profile="$2"
proj_dir="$3"

if [ "$op" = "stop" ]; then
    bin="${stop_bin}"
elif [ "$op" = "restart" ]; then
    bin="${restart_bin}"
else
    echo "Usage: $0 [stop|restart]"
    exit 1
fi

LOG "project dir: $proj_dir"
LOG "$op services with spring profile($profile) and bin($bin)..."

#exit 0

LOG "$op eureka..."

if [ "$op" = "restart" ]; then
    # restart eureka and wait for a second
    # check "Started Eureka Server" in eureka log
    pattern="Started Eureka Server"
    ssh uniloan04 "${load_env} && cd ${proj_dir} && echo ''>eureka/log.txt && bash ${bin} $profile eureka"
    log_ready=0
    while [ $log_ready == 0 ];
    do
        log_ready=1
        cnt=`ssh uniloan04 "cat $proj_dir/eureka/log.txt" | grep -c "$pattern"`
        if [ $cnt -eq 0 ]; then
            log_ready=0
        fi
    
        if [ $log_ready == 0 ]; then
            LOG "eureka is not ready, check 5s later..."
            sleep 5s
        fi
    done
else
    # just stop service
    ssh uniloan04 "${load_env} && cd ${proj_dir} && bash ${bin} $profile eureka"
fi


# 2. 
services=("backend" "paycenter" "kalapa" "market" "core" "cms")
services_hosts=("uniloan01,uniloan02" \
               "uniloan01,uniloan02" \
               "uniloan01,uniloan02" \
               "uniloan01" \
               "uniloan03" \
               "uniloan04")

for(( i=0;i<${#services[@]};i++));
do
    srv=${services[i]}
    LOG "$op $srv service..."
    OLD_IFS="$IFS"
    IFS=","
    hosts=(${services_hosts[i]})
    IFS="$OLD_IFS"
    for host in ${hosts[@]};
    do
        LOG " host: $host"
        ssh $host "${load_env} && cd ${proj_dir}/ && bash ${bin} $profile $srv"
    done
    echo ""
done

LOG "$op services succeeded."



