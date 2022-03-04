#!/bin/sh

JAVA_OPT="-Xms1024M "
JAVA_OPT=$JAVA_OPT"-Xmx1024M "
JAVA_OPT=$JAVA_OPT"-Xnoclassgc "
JAVA_OPT=$JAVA_OPT"-XX:MetaspaceSize=256m "
JAVA_OPT=$JAVA_OPT"-XX:MaxMetaspaceSize=256m "
JAVA_OPT=$JAVA_OPT"-XX:NewSize=256M "
JAVA_OPT=$JAVA_OPT"-XX:MaxNewSize=256M "
JAVA_OPT=$JAVA_OPT"-XX:+UseParNewGC "
JAVA_OPT=$JAVA_OPT"-XX:ParallelGCThreads=4 "
JAVA_OPT=$JAVA_OPT"-XX:+UseConcMarkSweepGC "
JAVA_OPT=$JAVA_OPT"-XX:CMSInitiatingOccupancyFraction=50 "
JAVA_OPT=$JAVA_OPT"-XX:+AggressiveOpts "
JAVA_OPT=$JAVA_OPT"-Djava.net.preferIPv4Stack=true "
JAVA_OPT=$JAVA_OPT"-Djava.awt.headless=true "
JAVA_OPT=$JAVA_OPT"-Dspring.profiles.active=${ACTIVE_PROFILE}"

java -jar ${JAVA_OPT} /strato/ARTIFACT_NAME