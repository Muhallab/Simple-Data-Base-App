This is a simple JavaFX GUI to communicate with an Oracle sql database.
The project represents a Photo and Video Equipment Rental database Management System. 
For more information about the project scope, check the OpenMe.pdf.

If you wish to run the application in your machine then simply edit the Connect() and populateTables() methods accordingly:

Connect():
          Change the String dbURL to connect with whatever server you have available ( In my case I connected it to a local sql server)
          
PopulateTables():
          When pointing the scanner to the populate_tables.txt file(second line of the method), Change the directory to where populate_tables.txt file is stored in your machine.

The file queries.txt includes sample queries that could be used to display useful results

P.S: you must first connect to the databse, then create tables, and finally populate tables to see any results
