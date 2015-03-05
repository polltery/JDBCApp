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
	final static int NUM_BUTTONS = 8;
	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	//add more prepared statements here if you need more
	PreparedStatement prepStat0, prepStat2, prepStat3, prepStat4, prepStat7;
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	
	/* Queries
	 * Set of queries that needs to be can be added:
	 * Giving discounts:
	 * UPDATE dbcustomer SET cuAmountDue = cuAmountDue(1-cuEventsBooked*?) WHERE cuAmountDue > 0 AND cuEventsBooked >= ?
	 * UPDATE dbcustomer SET cuAmountDue = IF(cuEventsBooked <= 5, cuAmountDue*(1-cuEventsBooked*0.10),cuAmountDue*0.5) WHERE cuAmountDue > 0 AND cuEventsBooked > 1;
	 * 
	 * Updating customer stats:
	 * UPDATE dbcustomer SET cuEventsBooked = (SELECT COUNT(evCustomerID) FROM dbevent WHERE evCustomerID = cuID);
	 * UPDATE dbcustomer SET cuFeeStatus = IF(cuAmountDue > 0,'Due','Paid');
	 * 
	 * Select employee(s) and perform updates:
	 * UPDATE emSalary SET emSalary = emSalary+(emSalary*0.1) WHERE NOW() > DATE_ADD(emJoinDate,INTERVAL 1 YEAR);
	 * 
	 * Money Flow of the company:
	 * SELECT SUM(cuAmountPaid) AS 'Income', SUM(cuAmountDue) AS 'Due Income', SUM(cuAmountPaid)+SUM(cuAmountDue) AS 'Potential Income' FROM dbcustomer;
	 * SELECT SUM(emSalary) AS 'costs per month' FROM dbemployee;
	 * 
	 * Employee details:
	 * SELECT CONCAT(emFirstName,' ',emLastName) AS 'Employee Name', veName AS 'Venue', deName AS 'Departmen', emManager AS 'Manager' FROM dbemployee,dbvenue,dbdepartment WHERE emWorkVenueID = veID AND emDepartmentID = deID;
	 *
	 *
	 * Set of queries that will need extra coding:
	 * Customer transactions
	 * UPDATE dbcustomer SET cuAmountPaid=(cuAmountPaid+cuAmountDue),cuAmount=0 WHERE cuID = 1;
	 * UPDATE dbcustomer SET cuAmountPaid=cuAmountPaid+25,cuAmountDue=cuAmountDue-25 WHERE cuID =2;
	 * 
	 * Finding upcoming event detals:
	 * SELECT evName AS 'Name', cuName AS 'Held by', veName AS 'Location', DATE_FORMAT(evStartDate, '%d %b%y') AS 'Start Date', DATEDIFF(evEndDate,evStartDate) AS 'Duration (Days)' FROM dbevent,dbcustomer,dbvenue WHERE evCustomerID = cuID AND evVenueID = veID AND evStartDate BETWEEN '2005-01-01' AND '2015-01-01';
	 * SELECT evName AS 'Name', cuName AS 'Held by', DATE_FORMAT(evStartDate, '%d %b%y') AS 'Start Date', DATEDIFF(evEndDate,evStartDate) AS 'Duration (Days)' FROM dbevent,dbcustomer,dbvenue WHERE evCustomerID = cuID AND evVenueID = veID AND evStartDate = 1;
	 *
	 */
	
  	JButton queryButton[] = new JButton[NUM_BUTTONS];

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
	   	//change the button texts in this section,  each button is linked with a process of its own number.
	   	// 2 Inputs: 1. Discount 2. No. of events customers have booked in the past
	   	queryButton[0] = new JButton("Give discount to customers");
	   	//
	   	queryButton[1] = new JButton("Update Customer Detials (Events Booked & Fee Status)");
		// 2 Inputs: 1. Minimum no. of years employee has worked 2. Raise amount  
	   	queryButton[2] = new JButton("Give employees a raise");
	   	//
	   	queryButton[3] = new JButton("View employee details");  	
	   	// 
	   	queryButton[4] = new JButton("View Money Flow of the company (Expenditure or Income)");  	
	   	// 2 Inputs: 1. Option 2. Amount
	   	queryButton[5] = new JButton("Update customer amount paid (Some amount or All amount)");
	   	// 3 Inputs: 1. Option 2. Date (Date picker) 3. Location (Dropdown)
	   	queryButton[6] = new JButton("View upcoming event(s) (Filter by Date or Location)");
	   	//
	   	queryButton[7] = new JButton("");
	   	
	   	//TODO: Similar queries will be grouped and user will get an option on which specific query needs to be performed

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
  		//used in process0
  		String prepquery0 = "UPDATE dbcustomer SET cuAmountDue = cuAmountDue*(1-cuEventsBooked*?) WHERE cuAmountDue > 0 AND cuEventsBooked >= ?";
  		//used in process1
  		String prepquery2 = "UPDATE dbemployee SET emSalary = emSalary+? WHERE NOW() > DATE_ADD(emJoinDate,INTERVAL ? YEAR);";   
  		//used in process5
  		String prepqueryX = "SELECT firstnames, lastname FROM DBEmployee "
  			+ " WHERE salary < ? AND empdNum = ? ";
  		//the next 2 queries used in process 5
  		String prepquery3 = "Select * from DBEmployee, DBDepartment  "
  				+ " WHERE dNum = empdNum AND dName = ?";
  		//this one gets the types to go into a dropdown list
  		String prepquery4 = "Select dName FROM DBDepartment";
  		// Used for query 7
  		String prepquery7 = "UPDATE dbcustomer SET cuAmountPaid=cuAmountPaid+?, cuAmountDue=cuAmountDue-?"
                + " WHERE cuID= ?";
	   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  		
  		//prepares the statements once 
  		try {
  			prepStat0 = con.prepareStatement(prepquery0);
 			prepStat2 = con.prepareStatement(prepquery2);
 			prepStat3 = con.prepareStatement(prepquery3);
 			prepStat4 = con.prepareStatement(prepquery4);
 			prepStat7 = con.prepareStatement(prepquery7);
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
  		else if (e.getSource() == queryButton[7] )
            process7();

  	}
  	
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  	// CUSTOM METHODS
  	
  	public void drawTable(String tableName){
  		String query = "SELECT * FROM " + tableName;
  		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			tableResults.clearTable();
			tableResults.formatTable(resSet);
		} catch (SQLException e) {
			outputArea.setText(e.getMessage());
		}	
  	}
  	
  	// CUSTOM METHODS END
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  	//PUT YOUR OWN CODE INTO EACH PROCESS
  	//alter the comments!
  	//DELETE EXISTING CODE IF YOU con't use all the buttons
  
  	// Giving discounts:
	public void process0() {
		try{
			//get balance limit from user
            String minEvents = JOptionPane.showInputDialog(this, "Minimun events customer should have booked: " );
            //if data entered
            if (minEvents != null){
            	int minEventsInt = Integer.parseInt(minEvents);
            	prepStat0.setInt(2,minEventsInt);
            	String discount = JOptionPane.showInputDialog(this, "Please enter how much discount (in percentage 0-100) would you like to give: " );
				if(discount != null){
					int discountInt = Integer.parseInt(discount);
	            	prepStat0.setInt(1,discountInt/100);
	            	int rowsAffected = prepStat0.executeUpdate();
					JOptionPane.showMessageDialog(this, rowsAffected + " rows Affected");
					int option = JOptionPane.showConfirmDialog(this, "View customer table?");
					if(option == 0){
						drawTable("dbcustomer");
					}
				}
				else{
					outputArea.setText("Please enter a numeric value between 0 and 100");
				}
            	}
            else{
            	outputArea.setText("Please enter a numeric value greater than 0");
            }
		}
		catch(SQLException e){
			outputArea.append(e.getMessage());
		}
	}

	// Updating customer stats:
	public void process1() {
		try{
			int option = JOptionPane.showConfirmDialog(this, "This will update the fee status and no. of events booked for all customers. Continue?");
			if(option == 0){
				String query = "UPDATE dbcustomer SET cuEventsBooked = (SELECT COUNT(evCustomerID) FROM dbevent WHERE evCustomerID = cuID)";
				Statement stmt = con.createStatement();
				int rowsUpdated = stmt.executeUpdate(query);
				JOptionPane.showMessageDialog(this, rowsUpdated + " rows Affected (Events booked update)");
				query = "UPDATE dbcustomer SET cuFeeStatus = IF(cuAmountDue > 0,'Due','Paid')";
				rowsUpdated = stmt.executeUpdate(query);
				JOptionPane.showMessageDialog(this, rowsUpdated + " rows Affected (Fee status update)");
				option = JOptionPane.showConfirmDialog(this, "View customer table?");
				if(option == 0){
					drawTable("dbcustomer");
				}
			}
		}catch(SQLException e){
			outputArea.setText(e.getMessage());
		}
		
	}
		
	// Select employee(s) and perform updates:
	public void process2() {
		try {
			String minWorkYrs = JOptionPane.showInputDialog(this, "Minimum no. of years employee should have worked: ");
			if (minWorkYrs != null){
				prepStat2.setInt(2, Integer.parseInt(minWorkYrs));
				String raiseAmt = JOptionPane.showInputDialog(this, "How much raise (in amount) would you like to give?: ");
				if(raiseAmt != null){
					prepStat2.setFloat(1, Float.parseFloat(raiseAmt));
					int rowsAffected = prepStat2.executeUpdate();
					JOptionPane.showMessageDialog(this, rowsAffected + " row(s) Affected");
					int option = JOptionPane.showConfirmDialog(this, "View employee table?");
					if(option == 0){
						drawTable("dbemployee");
					}
				}
				else{
					outputArea.setText("Please enter a valid numeric value");
				}
			}
			else{
				outputArea.setText("Please enter a valid numeric value between 0 and 5");
			}
			}catch(SQLException e){
				outputArea.setText(e.getMessage());
			}
	}

	// TODO: Update process3
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
	
	//View employee details: Name, Department and Work location
		public void process4() {
		
		//Query information
		outputArea.append("This query lists all the employees, their department, work location and their manager");
		
		  String query = "SELECT CONCAT(emFirstName,' ',emLastName) AS 'Employee Name',"
				  + "CONCAT(veName) AS 'Venue', (deName) AS 'Department',"
				  + "CONCAT(emManager) AS 'Manager' " 
				  + "FROM dbemployee,dbvenue,dbdepartment "
				  + "WHERE emWorkVenueID = veID AND emDepartmentID = deID";
		 
		  try{
		  		Statement stmt = con.createStatement();		  		
		  		ResultSet resSet = stmt.executeQuery(query);
		  		tableResults.clearTable();
		  		tableResults.formatTable(resSet);
	  		   
		  	}		
		  	catch (SQLException e){
		  		outputArea.setText(e.getMessage());
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
	
	public void process7() {
		 try {
	            //get balance limit from user
	            String s = JOptionPane.showInputDialog(this, "Enter the customer ID: " );
	            //if data entered
	            if (s != null) {
	                //convert from text to integer
	                int sInt = Integer.parseInt(s);
	                //set parameter in prepared statement
	                prepStat7.setInt(3,sInt);
	                //run query and obtain result set
	                String q = JOptionPane.showInputDialog(this, "Enter the amount the customer paid: " );
	                
	                if (s != null) {
		                //convert from text to integer
		                int qInt = Integer.parseInt(q);
		                //set parameter in prepared statement
		                prepStat7.setInt(1,qInt);
		                prepStat7.setInt(2,qInt);
		                //run query and obtain result set
		                int rowsAffected = prepStat7.executeUpdate();
						JOptionPane.showMessageDialog(this, rowsAffected + " rows Affected");
		                outputArea.setText("Customer's record updated.");
	            }
	            else {
	                outputArea.setText("no input entered!");
	                tableResults.clearTable();
	            }
            }
		 }
	        catch (SQLException e){
	            outputArea.setText(e.getMessage());
	        }
		}	
}