#!/usr/bin/env bash

clear

if [ -f ressources/databases/AutoRent.db]; then
    rm ressources/databases/AutoRent.db
fi

mvn exec:java
