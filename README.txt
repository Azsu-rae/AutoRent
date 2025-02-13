
Welcome!

First, open powershell and navigate to the directory of the project (where backend.ps1 and frontend.ps1 are in) and then 
either:

 - execute ".\backend.ps1" for executing Main.java from ".\backend\src\".
 - execute "frontend.ps1" for executing MainApp.java from ".\frontend\src\" which the main application of this project.

----------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------- BACKEND ------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------

There are 6 SQLite Tables:

1.Vehicules
2.Clients
3.Users

4.Reservations
|-> 5.Returns  (complete reservations)
|-> 6.Payments (complete reservations)

The backend corresponds a class to each table to support the following 4 operations:

.add()
.edit()
.delete()
.search()

All 6 classes follow the same pattern in their implementation:

Class <Table> {

	<Attributes>
	<Constructors>
	<toString method>

	--------- MAIN METHODS -------

	<add>
	<edit>
	<delete>
	<search>

	-------- GETTERS ---------

	<getters>

	-------- SETTERS ---------

	<setters>

	-------- MAIN METHODS' HELPERS FOR SQLite REQUESTS CONSTRUCTION ------

	<scratch> // to make the statement for .search()
	<update> // to make the statement for .edit()
	<insert> // to make the statement for .add()
	// no helper method for .delete()

	-------- SIMPLIFICATIONS -------

	<check if DB file and table exists>
	<check if object is valid for insertion>
	<database file path>

	<polymorphism tools for iterating the object's attributes>
	// |-> Like the 'index' HashMap that corresponds a number to the column's name in the SQLite table
	// |-> Like the attribute(int i) method that corresponds a number to an attribute

	<method to return the resulting tuples of a 'SELECT' statement in a 'Vector<Table>'>
	<method to attach values to a PreparedStatement>
}

The class 'UserInterface' is here to do the scratch work necessary to use the previous functionalities for more specific 
backend operations as in: 

+ getting all vehicules
+ getting all the reservations of a specific client
+ getting all the reservations of a specific vehicule
+ getting the return of a specefic reservation 
+ getting the payments of a specefic reservations 
+ deleting a vehicule (which would entail the deletion of all of it's reservations)
+ formatting the frontend's filtering and searching inputs to be compatible with the backends format
+ ... etc. 

The frontend will exclisivly communicate with the backend through the UserInterface Class.

--------------------------------------------------------------------------------------------------------------------
-------------------------------------------- FRONTEND --------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------

The frontend has 5 main JPanels to provide all of the assigned fuctionalities in the project:

.MainPanel
|
+->.LoginPanel
|  |
|  +->.Styling
|
+->.AbstractTablePanel
|  |
|  +->.VehiuclePanel
|  +->.ClientPanel
|  +->.ReservationPanel
+  +->.UserPanel

