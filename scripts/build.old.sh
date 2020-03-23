#!/bin/bash

LOG() {
    #echo `date  +'%Y%m%d %H:%M'`, "$@"
    echo `date  +'%y/%m/%d %H:%M:%S'` "$@"
}


LOG "start to pull code from git..."
#git pull

LOG "start to build..."
mvn clean package -Dmaven.test.skip=true 

# check build result
if [ $? -ne 0 ]; then
    LOG "[ERROR] Build failed!"
    exit 1
fi

LOG "start to deploy..."
scp market/target/*.jar root@uniloan01:/root/server/market/
scp backend/target/*.jar root@uniloan01:/root/server/backend/
scp paycenter/target/*.jar root@uniloan01:/root/server/paycenter/

scp backend/target/*.jar root@uniloan02:/root/server/backend/
scp paycenter/target/*.jar root@uniloan02:/root/server/paycenter/

scp core/target/*.jar root@uniloan03:/root/server/core/

scp eureka/target/*.jar root@uniloan04:/root/server/eureka/
scp cms/target/*.jar root@uniloan04:/root/server/cms/


LOG "services restarting..."
bash restart_service.online.sh

LOG "Done."

