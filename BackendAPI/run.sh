
DB_PATH="BackendAPI/ressources/databases/AutoRent.db"
echo "Initializing database..."
if [ -f "$DB_PATH" ]; then
    rm "$DB_PATH"
    echo "Deleted database"
else
    echo "No database found"
fi

echo ""
mvn -pl BackendAPI clean compile exec:java -D exec.mainClass="Main"
echo ""
