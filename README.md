
# Pedagogy

# Motivation

This is a professor aid tool to handle their day-to-day managing of student grades, attendance, homework, etc...

# Scope

The project should include:

A list of students in each teaching day (could be customized, not rigid to a timetable or schedule) with their attendance status.

It should be fully excel compatible and should export to it.

It should also handle test grades and homework. Assiduity should also be a metric somehow.

I think all of this can be grouped using dates so a calendar as a primary view seems adequate.

# Models

Let's take an approach where we start from the obvious and then derive what we need:

Let's start with the student:

```
student
    - name
    - surname
    - matricule
      
    - email

    - group (which also belongs to a specialty)
```

The group will basically represent the program that the student is enrolled in.

Then naturally we also get:

```
Group
	- label
	- specialty (foreign key)
	- Teaching assistante (foreigh key)
Specialty
	- name
	- cycle
```

Deriving once again:

```
TA
	- name
	- surname
	  
	- email
	- phone number
```

we also shouldn't forget about classes. Classes are important they are the thing that the grades depend on.

```
course
	- name

enrollment
	- student (foreign key)
	- course (foreign key)
```

thus yielding the following modelization:

<div align="center">
	<img src="./media/preliminary_modelization.svg">
</div>
# Framework Documentation

## Motivation

This is a Single-Source-of-Truth interfaces generator where you define your data
models in classes and a CRUD interface is generated using reflection.

The project includes a custom ORM and uses SQLite as a database. This is a
learning project for strongly typed langages and Object-Oriented separation of
concerns. It is also my first programming project with a GUI and a database in a
high-level language like Java (I had only ever worked with C and DSA/Competitive
Programming at the time).

It was overall a great learning experience.

## Tutorial

- you can have static fields in models but you can't add fields other than to-be database columns

## Dependencies

- **sqlite-jdbc-3.50.3.0.jar:** SQLite Implementation of JDBC
- **json-20250517.jar:** To store sample data using JSON key-value pairs

- **jcalendar-1.4.jar:** UI for date picking (potentially)
- **flatlaf-3.6.2.jar:** Better Look-and-Feel

## Code Explanations

### ORM

I wrote a simple ORM using reflection and annotations. The structure is pretty straightforward:

```
orm
├── Constraints.java
├── DataMapper.java
├── Reflection.java
├── SQLiteQueryConstructor.java
└── Table.java
```

### Frontend structure

Swing only provides basic building blocks like scrollables panes and clickable buttons and is quite low-level. There's no built-in
`required` option to set on text fields or some semantic construct like an HTML `form`. All of this has to be handled by the
programmer.

The following structure reduces boilerplate and tries to separate concerns into classes with one and only one job while also
trying not to spiral and drown in abstraction. That's harder than it seems to an inexperienced programmer. There is a lot of
re-writting that happened and that's invisible in the final product.

**gui package**

```
gui
├── dashboard
│   ├── Dashboard.java
│   ├── Home.java
│   ├── record
│   │   ├── dialog
│   │   │   ├── ForeignPicker.java
│   │   │   ├── MultipleSelections.java
│   │   │   ├── RangeSelection.java
│   │   │   ├── RecordEditor.java
│   │   │   └── SearchProfile.java
│   │   ├── Record.java
│   │   ├── TableView.java
│   │   └── ToolBar.java
│   ├── Records.java
│   └── Sidebar.java
├── MainApp.java
├── Opts.java
└── SignIn.java
```

The idea is that 1 UI Entity = 1 class = 1 file. Since there's no tag-based language to represent swing components I thought i
would at least try to structure it to limit spagheti-like code but i wasn't really able to eliminate it.

**contract package**

```
contract
├── Listener.java
└── ToClear.java
```

Encapsulation using packages brought a problem of communication between components. The solution I found was using interfaces as
contracts to communicate between elements (log in, log out, clear event, selections, ...). This fixed the problem that happens
when separating a `SideBar` which naturally contains the buttons to navigate the dashboard. It only needs to fire a `HOME` event
by calling the function defined in the `Listener` interface to communicate with the main container to switch the main visible panel.

**utilitites**

```
component
├── Factory.java
├── MyButton.java
├── MyDialog.java
├── MyLabel.java
└── MyPanel.java
mapper
├── FieldEditorMapper.java
├── FieldLabelFormatter.java
└── FieldValueMapper.java
```


# TODOs

## Pending

- Engineer a mapping layer to map data types to their corresponding input UI

## Done

- `./frontend/src/main/java/gui/util/Parser.java` does too much. Extract the string/name management into a separate class
    - FieldValueMapper
    - FieldLabelFormatter

- fix the `MyDialog` implementations

- Extract non-UI element classes out of the `gui` package

- Collapse the frontend/backend separation into a single project package
