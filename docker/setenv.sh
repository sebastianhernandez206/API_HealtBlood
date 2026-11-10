#!/bin/sh
export CATALINA_OPTS="$CATALINA_OPTS -Dhttp.port=${PORT:-8080} -Dfile.encoding=UTF-8"
