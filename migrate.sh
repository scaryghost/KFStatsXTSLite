#!/usr/bin/env sh

#### USER SETTINGS ####

sqliteDB=kfstatsx.sqlite
mysqlURL=localhost:3306/kfstatsx
mysqlUser=kfstatsx
mysqlPwd=server

#######################


#### DO NOT MODIFY ####
cp=KFStatsXTSLite.jar
main=com.github.etsai.kfstatsxtslite.migrate.MigrateMain

exec java -cp $cp $main jdbc:sqlite:$sqliteDB \
jdbc:mysql://$mysqlURL $mysqlUser $mysqlPwd
