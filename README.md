# Staff-Database
Interactive database of University of Maryland staff

The purpose of this application was for the use of student interns within Facilities Management (FM) at the University of Maryland, to allow easy access to employee information and location within the office, external employee information, and room information.
<br>

<h2>Functions</h2>
<b>Querying</b> <br>
To send a search query, a user simply needs to type in the search bar located at the top for one of many fields. These could include an employee's name, department, title, room number, or cell number. For rooms, they could include the room tag (descriptor) and number. <br><br>
If the query is an employee of Facilities Management or a room within the building, pressing the "Search" button will show the location on the floor plan and pop up any important information about it. If the query is outside of Facilities Management, then using the "Directory Search" button will query the publicly available University of Maryland directory search, and pull the results.<br><br>

<b>Database Management</b><br>
Using a specific format in a plain text file, the application can load an existing database to allow users to query from that database. The application can also save to a file using the same format.

<b>Additonal Functions</b><br>
- Zoom in/out on the floor map
- Scroll horizontal/vertical on the floor map
- UI loading screen until the application loads

<h2>Building Package</h2>
The building package contains multiple files. The most important one being FloorLayoutGui.java, of which an instance is created in the driver to initiate the application.
<br><br>
The rest of the files in this package are images of the building floor plan and FM logos used within the UI of the application to allow users to see where an employee is located.
<br><br>

<h2>Database Package</h2>
The database package contains multiple files necessary to the back-end. These include classes defining what an employee is, what a room is, and what a database is.<br><br>
The package also contains the class necessary for querying employee/room information.
