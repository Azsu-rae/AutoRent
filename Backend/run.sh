#!/usr/bin/env bash

DB_PATH="Backend/ressources/databases/AutoRent.db"
echo "Initializing database..."
if [ -f "$DB_PATH" ]; then
    rm "$DB_PATH"
    echo "Deleted database"
else
    echo "No database found"
fi

echo ""
mvn -pl Backend clean compile exec:java -D exec.mainClass="Main"
echo ""
