
import mcp.ParsedQuery;

public class Main {

    public static void main(String[] args) {

        ParsedQuery query = ParsedQuery.create("search", "Client");
        query.setAtrribute("Name", "Doe");
        query.setAtrribute("Name", "Smith");
        System.out.println(query.execute());
    }
}
