 /*
class OwnQueries
DBMS Application
Monica Farrow 
A set of buttons enables the user to choose some predefined queries
This class needs to be customised for each database wherever there is a line //++//++
*/

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class OwnQueries extends JFrame 
                      implements ActionListener
{   
  	JTextArea outputArea;
  	QueryTable tableResults;
	Connection con;
	final static int NUM_BUTTONS = 7;
	JButton queryButton[] = new JButton[NUM_BUTTONS];;
	
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	//add more prepared statements here if you need more
	PreparedStatement prepStat1, prepStat2, prepStat3, prepStat4;
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  
  	/////////////////////////////////////////////////////////////////
  	//Constructor sets up frame with 
  	//	an area for SQL entry 
  	//	a table for results
  	public OwnQueries(Connection conn) 
	{
		//Initialise frame
	  	super("SQL Query");
	   	addWindowListener(new WindowAdapter()
							{public void windowClosing (WindowEvent e)	
								{dispose();}});
	   	setSize(650, 600);
	   	createButtons();	          
	   	createTableAndTextArea();
	   	
	   	//connect to database and prepare some statements
		con = conn;	   	
	   	prepareStatements();
	}
  	
  	
  	//places table in center and text area in south 
  	public void createTableAndTextArea() {
	    
	    //Set QueryTable in scrollpane in centre of layout
	   	//Results from the query will be stored here
	    tableResults = new QueryTable ();
	    JTable table = new JTable(tableResults);
	    JScrollPane scrollpane = new JScrollPane(table);
	    getContentPane().add(scrollpane, BorderLayout.CENTER);
	    
	    //Add text area
	    outputArea = new JTextArea("",8,50); 
	    JScrollPane scroll = new JScrollPane(outputArea);
	   	add(scroll, BorderLayout.SOUTH);  
	}
  	
  	public void createButtons() {
	    //Create upper panels for label, text area and buttons
	    JPanel p1 = new JPanel();
	   	p1.setLayout(new GridLayout (7,1));
	   	
	   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	   	//change the button texts in this section
	   	queryButton[0] = new JButton("employees born before");
	   	queryButton[1] = new JButton("depts with employee with salary less than limit");
	   	queryButton[2] = new JButton("insert then delete");  	
	   	queryButton[3] = new JButton("an update");  	
	   	queryButton[4] = new JButton("prepared statement with 2 parameters");
	   	queryButton[5] = new JButton("get list of params, choose, then use ");  	
	   	queryButton[6] = new JButton("");

	   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	   
	   	//add buttons to panel - don't change this
	   	for (int count = 0; count < NUM_BUTTONS; count++) {
	   		queryButton[count].addActionListener(this);
	   		p1.add(queryButton[count]);
	   	}	   	
	   	add(p1, BorderLayout.NORTH);
  	}
	   	

   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  	//set up all your prepared statements here
  	public void prepareStatements() {
  		//used in process1
  		String prepquery1 = "SELECT DISTINCT dName " 
  			+ " FROM DBDepartment, DBEmployee "
  		 	+	" WHERE dNum = empdNUm AND salary < ? ";   
  		//used in process5
  		String prepquery2= "SELECT firstnames, lastname FROM DBEmployee "
  			+ " WHERE salary < ? AND empdNum = ? ";

  		//the next 2 queries used in process 5
  		String prepquery3 = "Select * from DBEmployee, DBDepartment  "
  				+ " WHERE dNum = empdNum AND dName = ?";
  		//this one gets the types to go into a dropdown list
  		String prepquery4 = "Select dName FROM DBDepartment";
	   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  		
  		//prepares the statements once 
  		try {
  			prepStat1 = con.prepareStatement(prepquery1);
 			prepStat2 = con.prepareStatement(prepquery2);
 			prepStat3 = con.prepareStatement(prepquery3);
 			prepStat4 = con.prepareStatement(prepquery4);
  		}
  		catch (SQLException e) {
  			outputArea.setText(e.getMessage());
  		}
  	}
  
  	/////////////////////////////////////////////////////////////////
  	//Handles all actions from the GUI
  	public void actionPerformed(ActionEvent e) 
  	{	
  		if (e.getSource() == queryButton[0]) 
  			process0();
  		else if (e.getSource() == queryButton[1]) 
  			process1();
  		else if (e.getSource() == queryButton[2] ) 
  			process2();
  		else if (e.getSource() == queryButton[3] ) 
  			process3();
  		else if (e.getSource() == queryButton[4] ) 
  			process4();
  		else if (e.getSource() == queryButton[5] ) 
  			process5();
  		else if (e.getSource() == queryButton[6] ) 
  			process6();

  	}
  	
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  	//PUT YOUR OWN CODE INTO EACH PROCESS
  	//alter the comments!
  	//DELETE EXISTING CODE IF YOU con't use all the buttons
  
  	//selects some data and puts it into the  table
	public void process0() {     
		String query = "SELECT lastname, firstnames, dateOfBirth  FROM DBEmployee "
			 + "WHERE dateOfBirth < '1960-01-01' ORDER BY dateOfBirth DESC" ; 
		try
		{
			//create statements
			Statement s1 = con.createStatement();
			//run query and store results in result set
			ResultSet resSet = s1.executeQuery(query);
			//clear table
			tableResults.clearTable();
			//display in text area
			outputArea.setText("AccountNos and Types\n");
			while(resSet.next() ) {
			    outputArea.append(resSet.getString(2));
			    outputArea.append (" ");
			    outputArea.append(resSet.getString(1));
			    outputArea.append (" : ");
			    outputArea.append(resSet.getString(3).substring(0,4));  //year only
			    outputArea.append("\n");
			 }
			
		}
		catch (SQLException e){
			outputArea.setText(e.getMessage());
		}
			
			
	}
	

	//uses a prepared statement consisting of a parameterised query prepStat1
	//finds all depts where someone has a salary  less that a limit supplied by user
	public void process1() {

		try
		{
			//get balance limit from user
			String s = JOptionPane.showInputDialog(this, "Enter salary limit: " );
			//if data entered
			if (s != null) {
				//convert from text to integer
				int sInt = Integer.parseInt(s);
				//set parameter in prepared statement
				prepStat1.setInt(1,sInt);
				//run query and obtain result set
				ResultSet resSet = prepStat1 .executeQuery();
				//output heading including limit 
				outputArea.setText("Departments  with salary < " + sInt + "\n");
				//loop for each result
				while (resSet.next()) {
					//display type in text area - type is in first column of result
					outputArea.append(resSet.getString(1) + "\n");
					//clear table (not using it)
					tableResults.clearTable();
				}
			}
			//if user cancelled, clear both output area and table
			else {
				outputArea.setText("no input entered");
				tableResults.clearTable();
			}
			
		}
		catch (SQLException e){
			outputArea.setText(e.getMessage());
		}
			
			
	}
	
	//DELETE/ INSERTION 
	//just reports how many rows altered
	//you can view the table from the main switchboard to see the result in the records
	public void process2() {
		try {
			//create a statement
			Statement stmt = con.createStatement();
			
			//define an insert
			String insertQuery = "INSERT INTO DBProject VALUES "
			              + " (23,'temp2',5)";
			//execute the insert and find how many rows affected
			int howManyRowsInserted = stmt.executeUpdate(insertQuery);
			//output heading
			outputArea.append("Trying to insert project temp2\n");
			//output how many rows inserted
			outputArea.append("Rows inserted = " + howManyRowsInserted + "\n");
			
			//maybe put a query here to prove it
			
			//define a delete
			String delQuery = "DELETE FROM DBProject WHERE pName = 'temp2'";
			//execute the delete and find how many rows affected
			int howManyRowsDeleted= stmt.executeUpdate(delQuery);
			//set up heading
			outputArea.append("Trying to delete project temp2\n");
			//output result
			outputArea.append("Rows deleted = " + howManyRowsDeleted + "\n");
			

			//clear table - not using it
			tableResults.clearTable();


		}
		catch (SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	//AN UPDATE QUERY
	//we expect this to return 0 rows 
	//because ss number does not exist
	//I woulnd't expect you to provide a failure in your coursework -
	//this is just for demonstration purposes
	public void process3() {
		try {
			//create statement
			Statement stmt = con.createStatement();
			//define update
			String updateQuery = "UPDATE DBEmployee SET salary = 25000 "
			                   + " WHERE ssn = 777 ";
			//run update and get how many rows affected
			int howManyRowsUpdated = stmt.executeUpdate(updateQuery);
			//output heading
			outputArea.setText("Example of a failed update\n");
			//output how many rows updated
			outputArea.append("Rows updated = " + howManyRowsUpdated + "\n");
			//clear table - not using it
			tableResults.clearTable();
		}
		catch (SQLException e){
			outputArea.setText(e.getMessage());
		}
	}
	
	
	
	//displays employee names
	//the user supplies the salary limit and gender
	public void process4() {

		try {
	
			//Define heading
			outputArea.append("This query lists employee names with given salary limit in dept\n");

	
			//valid types are current, deposit, mortgage, investment
			String sal= JOptionPane.showInputDialog(this,"Enter an salary limit : ");
			String dep = JOptionPane.showInputDialog(this,"Enter dept num  ");
			if (sal != null && dep != null) {

				//supply the parameter(s)
				prepStat2.setInt(1, Integer.parseInt(sal));   //convert to integer
				prepStat2.setInt(2,  Integer.parseInt(dep));			
				//execture the query
				ResultSet resSet = prepStat2.executeQuery();
		
				//print heading including parameters
				outputArea.setText("These are the names of employees earning less than" 
				         + sal + " in department " + dep + "\n");
				tableResults.clearTable();
				while (resSet.next())   
				{
					tableResults.formatTable(resSet);
					//outputArea.append(prs.getInt(1) + "\n");  //print integer from column 1
				}
			}
			else {
				outputArea.setText("no input entered");
				tableResults.clearTable();
			}
		}
		catch (SQLException e1) {
			outputArea.setText(e1.getMessage());
		}
		catch (NumberFormatException nfe) {   //this error message could be more precise
			outputArea.setText("Number Format Error " + nfe.getMessage());
		}
	}
	
	//runs one query to get the list of department names
	//then the user chooses one
	//then the list of employees in this department are displayed
	public void process5() {
		try
		{
			//run the prepared query to get the department names
			ResultSet resSet= prepStat4.executeQuery();
			//define an array to hold all the names
			ArrayList<String>  params = new ArrayList<String>();
			//loop through the result set and get out the names
			int count = 0;
			while (resSet.next()){  //whilst more
				//add result data to array
				params.add(resSet.getString(1));
				count++;
			}
			//find number of types in the result set
			int size = params.size();
			//define an array to populate the drop down list
			Object[] possibilities = new Object[size];
			//copy the types into the drop down list
			for (int c = 0; c< size; c++){
				possibilities[c] = params.get(c);
			}
			//get name from the user
		    String s = (String) JOptionPane.showInputDialog(this, 
		            "Choose",
		            "Choose",
		            JOptionPane.QUESTION_MESSAGE, 
		            null, 
		            possibilities, //list of values
		            possibilities[0]);  //default

			//If a string was returned
			if ((s != null) && (s.length() > 0)) {
				//set parameter in the query
				prepStat3.setString(1,s);
				//run the query to find all employees in this department
				resSet = prepStat3.executeQuery();
				//display in table
				tableResults.formatTable(resSet);
				outputArea.setText("list of employees in department = " + s);
			}
			else {
				tableResults.clearTable();
				outputArea.setText("No selection made");
			}
				

		}
		catch(SQLException ex)
		{
			outputArea.setText(ex.getMessage());
		}
	}
	
	public void process6() {
	}


	

	
}