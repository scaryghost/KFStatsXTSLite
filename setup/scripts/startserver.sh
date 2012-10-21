#!/usr/bin/env sh

#### USER SETTINGS ####

udpPort=6000
serverPwd=server
mysqlURL=localhost:3306/kfstatsx
mysqlUser=user
mysqlPwd=password

#######################

#### DO NOT MODIFY ####
jar=KFStatsXTSLite.jar

exec java -jar $jar -dburl jdbc:mysql://$mysqlURL -dbuser $mysqlUser -dbpwd $mysqlPwd -port $udpPort -pwd $serverPwd $*
