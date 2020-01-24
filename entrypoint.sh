#!/bin/bash
if [ -z "$ROOT_PATH" ]; then
  if [ -z "$1" ]; then
    echo "BadArguments: missing ROOT_PATH env or parameter"
   exit 1;
  else
    ROOT_PATH=$1
  fi
fi
java -DrootPath="$ROOT_PATH" -jar application.jar