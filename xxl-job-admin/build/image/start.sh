#!/bin/bash
nohup java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xms1024m -Xmx1024m -Xmn256m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar /xxl-job-admin.jar --spring.cloud.bootstrap.location=/bootstrap.yml >/dev/null 2>&1

