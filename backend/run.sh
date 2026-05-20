#! /usr/bin/env bash

set -e

source ./backend/build.sh

rm -f ./backend/ressources/databases/AutoRent.db
java -cp $CLASSPATH Main
