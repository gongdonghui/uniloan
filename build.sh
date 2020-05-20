#!/bin/bash
LOG() {
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}

CP_AND_DEPLOY_TEST() {
    _target=$1
    _proj_dir=$2
    services=$3

    for(( i=0;i<${#services[@]};i++));
    do
        srv=${services[i]}
        dst_dir="$_proj_dir/$srv/"
        LOG "cp $srv/target/*.jar ${dst_dir}"
        cp $srv/target/*.jar ${dst_dir}
    done

    cd $_proj_dir
    bash restart_service.all.test.sh $_target
}

SCP_AND_DEPLOY() {
    _target=$1
    _proj_dir=$2
    services=$3
    services_hosts=$4

    for(( i=0;i<${#services[@]};i++));
    do
        srv=${services[i]}
        dst_dir="$_proj_dir/$srv/"
        LOG "restart $srv service..."
        OLD_IFS="$IFS"
        IFS=","
        hosts=(${services_hosts[i]})
        IFS="$OLD_IFS"
        for host in ${hosts[@]};
        do
            LOG "scp $srv/target/*.jar root@$host:${dst_dir}"
            scp $srv/target/*.jar root@$host:${dst_dir}
        done
    done

    bash services_control.sh restart $_target $_proj_dir
}

target="online2"
if [ $# == 1 ]; then
    #echo "Usage: $0 [online|online2|all|test|test2]"
    target=$1
fi


LOG "start to pull code from git..."
git pull

LOG "start to build target(online|online2|all)=$target"
mvn clean package -Dmaven.test.skip=true 

# check build result
if [ $? -ne 0 ]; then
    LOG "[ERROR] Build failed!"
    exit 1
fi

if [ "$target" = "test" -o "$target" = "test2"  ]; then
    echo ""
    LOG "start to deploy(test)..."
    proj_dir="/root/server"
    services=("eureka" "backend" "paycenter" "kalapa" "market" "core" "cms")

    CP_AND_DEPLOY_TEST "$target" $proj_dir "$services"
fi

if [ "$target" = "online" -o "$target" = "all" ]; then
    echo ""
    LOG "start to deploy(online)..."
    proj_dir="/root/server"
    services=("eureka" "backend" "paycenter" "kalapa" "market" "core" "cms")
    services_hosts=("uniloan04" \
                   "uniloan01,uniloan02" \
                   "uniloan01,uniloan02" \
                   "uniloan01,uniloan02" \
                   "uniloan01" \
                   "uniloan03" \
                   "uniloan04")

    SCP_AND_DEPLOY "online" $proj_dir "$services" "$services_hosts"
fi

if [ "$target" = "online2"  ]; then
    echo ""
    LOG "start to deploy(online2)..."
    proj_dir="/root/server"
    services=("eureka" "backend" "paycenter" "kalapa" "market" "core" "cms")

    CP_AND_DEPLOY_TEST "$target" $proj_dir "$services"
fi

#if [ "$target" = "online2" -o "$target" = "all" ]; then
#    echo ""
#    LOG "start to deploy(online2)..."
#    proj_dir="/root/server2"
#    services=("eureka" "backend" "paycenter" "kalapa" "market" "core" "cms")
#    services_hosts=("uniloan04" \
#                   "uniloan01,uniloan02" \
#                   "uniloan01,uniloan02" \
#                   "uniloan01,uniloan02" \
#                   "uniloan01" \
#                   "uniloan03" \
#                   "uniloan04")
#
#    SCP_AND_DEPLOY "online2" $proj_dir "$services" "$services_hosts"
#
#fi

LOG "Finished building & deploying $target."

