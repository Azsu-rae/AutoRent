package orm.model;

import utilities.Pair;
import orm.Table;
import java.util.Vector;

public class Return extends Table<Return> {

    private Reservation reservation;
    private String returnDate;
    private String returnState;
    private Double additionalFees;

    public Return() {}

    public Return(Reservation reservation, String returnDate, String returnState, Double additionalFees) {

        this.reservation = reservation;
        this.returnDate = returnDate;
        this.returnState = returnState;
        this.additionalFees = additionalFees;
    }

    @Override
    protected int attributesNumber() {

        return 5;
    }

    @Override
    protected String sqliteTableName() {

        return "retours";
    }

    @Override
    protected String map(int i) {

        switch (i) {
            case 0: 
                return "id_retour";
            case 1: 
                return "id_reservation";
            case 2: 
                return "date_retour";
            case 3: 
                return "etat_retour";
            case 4: 
                return "frais_supplementaires";
            default:
                System.out.println("WRONG INDEX PASSED TO MAP!");
                return "ERROR!";
        }
    }

    @Override
    protected String type(int i) {

        switch (i) {
            case 0:
                return "i";
            case 1:
                return "r";
            case 4:
                return "d";
            default:
                return "s";
        }
    }

    @Override
    protected void setAttribute(Table tuple, int i, Object attributeValue) {

        Return retour = (Return) tuple;
        switch (i) {
            case 0:
                retour.id = (Integer) attributeValue;
                break;
            case 1:
                retour.reservation = (Reservation) attributeValue;
                break;
            case 2:
                retour.returnDate = (String) attributeValue;
                break;
            case 3:
                retour.returnState = (String) attributeValue;
                break;
            case 4:
                retour.additionalFees = (Double) attributeValue;
                break;
            default:
        }
    }

    @Override
    protected boolean boundedAttribute(int i) {

        return i == 2 || i == 4;
    }

    @Override
    protected String table() {

        String table = "CREATE TABLE IF NOT EXISTS retours(" +
            "id_retour INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_reservation INTEGER NOT NULL, " +
            "date_retour DATE NOT NULL, " +
            "etat_retour VARCHAR NOT NULL, " +
            "frais_supplementaires DECIMAL NOT NULL, " +
            "FOREIGN KEY (id_reservation) REFERENCES reservations(id_reservation)" +
            ");";

        return table;
    }

    @Override
    protected Object attribute(int i) {

        switch (i) {
            case 0:
                return this.id;
            case 1:
                return this.reservation;
            case 2:
                return this.returnDate;
            case 3:
                return this.returnState;
            case 4:
                return this.additionalFees;
            default:
                return null;
        }
    }

    @Override
    protected boolean isValid() {

        return reservation != null && returnDate != null && returnState != null && additionalFees != null;
    }

    public static Vector<Table> search(Object b1, Object b2) {

        return boundedSearch(new Return(), b1, b2);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Return());
        return search(tuples, boundedCriterias);
    }

    public Return setReservation(Reservation r) {

        if (!r.isValid() || r.getId() == null) {
            return this;
        }

        this.reservation = r;
        return this;
    }

    public Return setReturnDate(String returnDate) {

        this.returnDate = returnDate;
        return this;
    }

    public Return setReturnState(String returnState) {

        this.returnState = returnState;
        return this;
    }

    public Return setAdditionalFees(Double additionalFees) {

        this.additionalFees = additionalFees;
        return this;
    }

    public Reservation getReservation() {

        return this.reservation;
    }

    public String getReturnDate() {

        return this.returnDate;
    }

    public String getReturnState() {

        return this.returnState;
    }

    public Double getAdditionalFees() {

        return this.additionalFees;
    }
}
