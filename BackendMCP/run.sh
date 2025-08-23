
echo ""
mvn clean compile
mvn -pl BackendMCP exec:java -Dexec.mainClass="Main"
echo ""
