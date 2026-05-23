# Design Patterns × AutoRent — Analysis

The course (Partie 4 – Patrons de conception, Prof. Gaceb) covers **6 patterns** across three categories:

| Category | Pattern | Already in AutoRent? |
|---|---|---|
| Structural | **Composite** | ❌ Not yet |
| Structural | **Adapter** | ❌ Not yet |
| Creational | **Singleton** | ⚠️ Partial — mentioned in README as a TODO |
| Behavioral | **Strategy** | ❌ Not yet |
| Behavioral | **Observer** | ⚠️ Partial — `Listener`/`ToClear` are a handwritten version |
| Behavioral | **Visitor** | ❌ Not yet (but applicable) |

---

## 1. Observer — You Already Have the Bones 🦴

### What the course says
> Create a one-to-many dependency so that when one object changes state, all its dependents are notified and updated.

### What AutoRent already does
Your [Listener.java](file:///home/asura/projects/AutoRent/frontend/src/main/java/gui/contract/Listener.java) interface and its usage chain (`MainApp` → `Dashboard` → `Record`) is already a **manual Observer**:

```
MainApp (observer) ← listens ← Dashboard (observable/observer) ← listens ← Sidebar
```

And [Opts.java](file:///home/asura/projects/AutoRent/frontend/src/main/java/gui/Opts.java) with `ToClear` is another observer list (subscribe via `addToClear`, notify via `clearEvent`).

### What's missing — formalizing it

Right now the pattern is **implicit** — each component takes a single `Listener` in its constructor and events are manually forwarded. The course's pattern would generalize this:

```java
// New file: gui/contract/Observable.java
package gui.contract;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Listener> observers = new ArrayList<>();

    public void addObserver(Listener observer) {
        observers.add(observer);
    }

    public void removeObserver(Listener observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(Listener.Event event) {
        for (Listener obs : observers) {
            obs.onEvent(event);
        }
    }
}
```

Then `Dashboard` and `Record` extend `Observable` instead of manually holding a single `Listener`:

```diff
-public class Dashboard extends MyPanel implements Listener {
-    Listener listener;
-    public Dashboard(Listener listener) {
-        this.listener = listener;
+public class Dashboard extends Observable implements Listener {
+    public Dashboard(Listener listener) {
+        addObserver(listener);
```

> [!TIP]
> This also fixes the "only one listener" limitation. Multiple components could observe the same source — useful for your planned **Notifications** feature from the README.

---

## 2. Singleton — The Database Connection

### What the course says
> Restrict instantiation of a class to a single object with a global access point via a static `getInstance()` method.

### Where it applies in AutoRent

Your [Table.java](file:///home/asura/projects/AutoRent/backend/src/main/java/orm/Table.java) currently creates a **new `Connection`** on every CRUD call:

```java
// Table.java — this pattern repeats 4× (add, edit, delete, search)
try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
```

Your README literally says:
> ➜ *use dependency injection or singleton pattern properly*

Here's the Singleton applied, following the course's template exactly:

```java
// New or refactored: orm/util/DatabaseConnection.java
package orm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection uniqueInstance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:sqlite:./backend/ressources/databases/AutoRent.db"
            );
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new DatabaseConnection();
        }
        return uniqueInstance;
    }

    public Connection getConnection() {
        return connection;
    }
}
```

Then in `Table.java`:
```diff
-try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
+Connection conn = DatabaseConnection.getInstance().getConnection();
+try (PreparedStatement pstmt = conn.prepareStatement(preparedQuery.template())) {
```

> [!IMPORTANT]
> This also addresses your README items for **connection pooling** and **transaction support** — the Singleton is the natural place to add those features later.

---

## 3. Strategy — Pricing & Search Algorithms

### What the course says
> Encapsulate each algorithm in a class so the client can switch algorithms dynamically at runtime.

### Where it applies in AutoRent

**Use case A: Pricing strategies** — Your [Reservation.java](file:///home/asura/projects/AutoRent/backend/src/main/java/orm/model/Reservation.java#L60-L64) has a hardcoded pricing formula:

```java
totalAmount = (ChronoUnit.DAYS.between(startDate, endDate) + 1) * vehicle.getPricePerDay();
```

With the Strategy pattern, you could swap pricing algorithms (seasonal pricing, loyalty discount, weekend rates) without touching `Reservation`:

```java
// Strategy interface
public interface PricingStrategy {
    double calculate(Vehicle vehicle, LocalDate start, LocalDate end);
}

// Concrete strategies
public class StandardPricing implements PricingStrategy {
    public double calculate(Vehicle v, LocalDate start, LocalDate end) {
        return (ChronoUnit.DAYS.between(start, end) + 1) * v.getPricePerDay();
    }
}

public class WeekendDiscountPricing implements PricingStrategy {
    public double calculate(Vehicle v, LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        long weekendDays = /* count Saturday/Sunday */;
        return (days - weekendDays) * v.getPricePerDay()
             + weekendDays * v.getPricePerDay() * 0.85;
    }
}

public class LoyaltyPricing implements PricingStrategy {
    public double calculate(Vehicle v, LocalDate start, LocalDate end) {
        return (ChronoUnit.DAYS.between(start, end) + 1) * v.getPricePerDay() * 0.9;
    }
}
```

Then in `Reservation`:
```diff
+private static PricingStrategy pricingStrategy = new StandardPricing();
+
+public static void setPricingStrategy(PricingStrategy strategy) {
+    pricingStrategy = strategy;
+}

 private void setTotalAmountAndStatus() {
     if (vehicle != null && startDate != null && endDate != null) {
-        totalAmount = (ChronoUnit.DAYS.between(startDate, endDate)+1)*vehicle.getPricePerDay();
+        totalAmount = pricingStrategy.calculate(vehicle, startDate, endDate);
     }
     updateStatus();
 }
```

**Use case B: Search/filter strategies** — Your [ToolBar.java](file:///home/asura/projects/AutoRent/frontend/src/main/java/gui/dashboard/record/ToolBar.java) has hardcoded filter assembly logic in `onApply()`. You could extract a `SearchStrategy` interface for different search behaviors (simple, fuzzy, advanced profile-based).

---

## 4. Composite — The UI Component Tree

### What the course says
> Compose objects into tree structures so clients treat individual objects and compositions uniformly.

### Where it applies in AutoRent

Your dashboard is a tree of panels:

```
MainApp
 ├── SignIn
 └── Dashboard
      ├── Sidebar
      └── Records (JTabbedPane)
           ├── Record("Client")
           │    ├── ToolBar
           │    └── TableView
           ├── Record("Vehicle")
           ├── Record("Reservation")
           └── Record("User")
```

The Composite pattern can formalize this. Define a `UIComponent` interface that both individual components (leaves like `ToolBar`) and containers (composites like `Dashboard`) implement uniformly:

```java
// gui/contract/UIComponent.java
public interface UIComponent {
    void render();          // display/refresh
    void clear();           // cleanup resources
    String getComponentName();
}
```

```java
// gui/contract/CompositeComponent.java — the composite node
public abstract class CompositeComponent extends MyPanel implements UIComponent {
    private List<UIComponent> children = new ArrayList<>();

    public void addChild(UIComponent child) {
        children.add(child);
        if (child instanceof JComponent) {
            add((JComponent) child);
        }
    }

    public void removeChild(UIComponent child) { children.remove(child); }

    @Override
    public void render() {
        for (UIComponent child : children) { child.render(); }
    }

    @Override
    public void clear() {
        for (UIComponent child : children) { child.clear(); }
    }
}
```

> [!NOTE]
> This directly addresses your README note about memory management: *"Grid adds itself to the static list Opts.toClears but is never removed"*. The Composite's `clear()` method cascades cleanup recursively — no need for a global static list.

---

## 5. Adapter — The ORM ↔ GUI Bridge

### What the course says
> Convert the interface of a class into another interface that clients expect. Lets classes work together that couldn't otherwise.

### Where it applies in AutoRent

Your [Parser.java](file:///home/asura/projects/AutoRent/frontend/src/main/java/gui/util/Parser.java) and [Editor.java](file:///home/asura/projects/AutoRent/frontend/src/main/java/gui/dashboard/record/dialog/Editor.java) are doing **adaptation** between ORM `Table` objects and Swing UI elements — but without a clean interface:

```
Table (ORM domain)  ←→  Parser (ad-hoc conversion)  ←→  JTable/JTextField (Swing UI)
```

The Adapter pattern makes this explicit:

```java
// Adapter: Adapts a Table (ORM) to a JTable-compatible model
public class TableModelAdapter extends AbstractTableModel {

    private Vector<Table> data;
    private String[] columnNames;

    public TableModelAdapter(String ORMModelName) {
        var fields = Reflection.fieldsOf(ORMModelName);
        this.columnNames = Parser.titleCaseNames(fields.names);
        this.data = new Vector<>();
    }

    // Adapts Table.reflect.fields → getValueAt()
    @Override
    public Object getValueAt(int row, int col) {
        Table tuple = data.get(row);
        Object value = tuple.reflect.fields.get(col);
        if (value instanceof Table) {
            return ((Table) value).getId(); // FK → display ID
        }
        return value;
    }

    @Override
    public int getRowCount() { return data.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int col) { return columnNames[col]; }

    // Adaptee-specific method
    public Table getTuple(int row) { return data.get(row); }

    public void setData(Vector<Table> tuples) {
        this.data = tuples;
        fireTableDataChanged();
    }
}
```

This replaces the manual row-by-row conversion currently happening in `TableView` and `Parser`.

---

## 6. Visitor — Reports & Data Export

### What the course says
> Separate an algorithm from the object structure on which it operates, allowing new operations without modifying the classes.

### Where it applies in AutoRent

Your README mentions:
> ➜ *Reports (loyal clients, popular vehicles, ...)*

Instead of adding `generateReport()`, `exportToCSV()`, `exportToPDF()` methods to each model class, use the Visitor pattern:

```java
// Visitor interface
public interface TableVisitor {
    void visit(Client client);
    void visit(Vehicle vehicle);
    void visit(Reservation reservation);
}

// Add to Table.java
public abstract void accept(TableVisitor visitor);

// In each model:
// Client.java
@Override
public void accept(TableVisitor visitor) { visitor.visit(this); }
```

Then create visitors for different operations:

```java
public class CSVExportVisitor implements TableVisitor {
    private StringBuilder csv = new StringBuilder();

    public void visit(Client c) {
        csv.append(String.format("%s,%s,%s\n", c.getName(), c.getSurname(), c.getEmail()));
    }
    public void visit(Vehicle v) {
        csv.append(String.format("%s,%s,%.2f\n", v.getBrand(), v.getModel(), v.getPricePerDay()));
    }
    public void visit(Reservation r) {
        csv.append(String.format("%s,%s,%s\n", r.getStartDate(), r.getEndDate(), r.getStatus()));
    }

    public String getResult() { return csv.toString(); }
}

public class ReportVisitor implements TableVisitor {
    // Count popular vehicles, loyal clients, revenue stats...
}
```

---

## Summary — Priority Order for Implementation

| Priority | Pattern | Why | Effort |
|---|---|---|---|
| 🔴 **1** | **Singleton** | Fixes repeated DB connections, enables transactions & pooling | Small — 1 new class + refactor `Table.java` |
| 🟠 **2** | **Observer** | Formalizes existing `Listener` system, enables Notifications feature | Medium — refactor existing wiring |
| 🟡 **3** | **Strategy** | Makes pricing flexible, enables discount features from README | Medium — new interfaces + refactor `Reservation` |
| 🟢 **4** | **Adapter** | Cleans up Table↔Swing conversion, eliminates manual parsing | Medium — new `TableModelAdapter` class |
| 🔵 **5** | **Composite** | Solves memory cleanup issue (`ToClear`), formalizes UI tree | Large — touches all UI components |
| ⚪ **6** | **Visitor** | Enables Reports feature from README without modifying models | Medium — new interface + visitor classes |

> [!IMPORTANT]
> Each pattern directly addresses one or more TODOs already listed in your [README.txt](file:///home/asura/projects/AutoRent/README.txt). This isn't theoretical — these are practical improvements mapped to features you already planned.
