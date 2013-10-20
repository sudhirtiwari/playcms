#!/bin/sh
test -f ~/.sbtconfig && . ~/.sbtconfig
exec java -Xms2G -Xmx2G -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=1G ${SBT_OPTS} -jar ./sbt-launch.jar "$@"
