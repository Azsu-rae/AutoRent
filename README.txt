
------------------------------- DOCUMENTATION -------------------------------

you can have static fields in models but you can't add fields

. Dependencies
|
+-> . sqlite-jdbc-3.50.3.0.jar: SQLite Implementation of JDBC
+-> . json-20250517.jar
+-> . jcalendar-1.4.jar (potentially)

. 1 UI Entity = 1 class = 1 file
|
+-> . Encapsulation using packages
+-> . Ease of layout arrangement
+-> . Using interfaces as contracts to communicate between elements (log in, log out, clear event, selections, ...)

------------------------------- REQUIREMENTS -------------------------------

make a gereric dialog mechanism
make the picker

. Ideas
|
+-> . Add on focus coloring of the borders on the Sign In page
+-> . Dark & Light Mode
+-> . Bills
+-> . Undo/Redo
+-> . Keyboard shortcuts
+-> . Show loading states
+-> . Operation Cancelling

+-> . make the form a dialog that opens when you press a button
+-> . foreignKey picker
+-> . Different visiblity for managers and admins
+-> . Consistent & Compelete Business Logic
+-> . History
+-> . Payments & Return for reservations
+-> . Home Page Functions
|   |
|   +-> . Repports (loyal clients, popular vehicles, ...)
|
+-> . Notifications?

+-> . Use javadoc
+-> . Write Tests

+-> . Dire need for better logging

+-> . use proper password hashing
+-> . backend-level input validation (that's where it should be anyways, throw exceptions at bad args)

+-> . transaction support (critical for operations like payment + reservation)
+-> . connection pooling (will have performance issues)
+-> . caching mechanism

+-> . Client-side input validation (with good messages)

+-> . Make a loading screen

+-> . Check Parser for consistency (also check for empty vs real errors)

+-> use dependency injection or singleton pattern properly.

+-> check back parseSelectedRow (Grid:56)

+-> Pagination? (not loading everything at once)

+-> Deletion Confirmation

------------------------------- DONE -------------------------------

+-> . Databse operations should happen in Event Dispatch Thread (EDT) thread
+-> . Memory management for interfaces
|   |
|   +-> . Grid adds itself to the static list Opts.toClears but is never removed
|   +-> . Listeners added to stuff that will stop being used (like pickers, dialogs, ...) should be removed
