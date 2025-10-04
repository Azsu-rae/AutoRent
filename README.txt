
. Dependencies
|
+-> . sqlite-jdbc-3.50.3.0.jar: SQLite Implementation of JDBC
+-> . json-20250517.jar
+-> . jcalendar-1.4.jar (potentially)

. 1 UI Entity = 1 class = 1 file
|
+-> . Encapsulation using packages
+-> . Ease of arranging layouts
+-> . Using interfaces as contracts to communicate between elements (log in, log out, clear event, selections, ...)

. Ideas
|
+-> . Add on focus coloring of the borders on the Sign In page
+-> . Dark & Light Mode
+-> . Bills
+-> . Undo/Redo
+-> . Keyboard shortcuts
+-> . Show loading states
+-> . Operation Cancelling

+-> . Use javadoc
+-> . Write Tests

+-> . Dire need for better logging

+-> . use proper password hashing
+-> . backend-level input validation (that's where it should be anyways, throw exceptions at bad args)

+-> . transaction support (critical for operations like payment + reservation)
+-> . connection pooling (will have performance issues)
+-> . caching mechanism

. Requirements
|
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

+-> . Databse operations should happen in EDT thread

+-> . Client-side input validation (with good messages)

+-> . Memory management for interfaces?

+-> . Check Parser for consistency (also check for empty vs real errors)

+-> use dependency injection or singleton pattern properly. Also Factory/registry for Records

+-> check back parseSelectedRow (Grid:56)

+-> Loading indicator

+-> Pagination? (not loading everything at once)

+-> Deletion Confirmation
