 /*
class OwnQueries
DBMS Application
Monica Farrow 
A set of buttons enables the user to choose some predefined queries
This class needs to be customised for each database wherever there is a line //++//++
*/

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.event.*;

import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.swing.*;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import java.sql.*;
import java.sql.Date;

public class OwnQueries extends JFrame 
                      implements ActionListener
{   
  	JTextArea outputArea;
  	QueryTable tableResults;
	Connection con;
	final static int NUM_BUTTONS = 9;
	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	//add more prepared statements here if you need more
	PreparedStatement prepStat0, prepStat2, prepStat4, prepStat5,prepStat5Secondary,prepStat6,prepStat6Secondary,prepStat7,prepStat7Secondary;
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++

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
	   	queryButton[1] = new JButton("Update Customer Detials");
		// 2 Inputs: 1. Minimum no. of years employee has worked 2. Raise amount  
	   	queryButton[2] = new JButton("Give employees a raise");
	   	//
	   	queryButton[3] = new JButton("View employee details");  	
	   	// 
	   	queryButton[4] = new JButton("View Money Flow of the company");  	
	   	// 2 Inputs: 1. Option 2. Amount
	   	queryButton[5] = new JButton("Update customer amount paid");
	   	// 3 Inputs: 1. Option 2. Date (Date picker) 3. Location (Dropdown)
	   	queryButton[6] = new JButton("View upcoming events");
	   	// 2 Input: 1. Employee ID 2. Location ID
	   	queryButton[7] = new JButton("Change an employee's Work location");
	   	//
	   	queryButton[8] = new JButton("");
	   	
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
  		String prepquery0 = "UPDATE dbcustomer SET cuAmountDue = cuAmountDue*(1-(cuEventsBooked*?)) WHERE cuAmountDue > 0 AND cuEventsBooked >= ?";
  		//used in process1
  		String prepquery2 = "UPDATE dbemployee SET emSalary = emSalary+? WHERE NOW() > DATE_ADD(emJoinDate,INTERVAL ? YEAR);";   
  		//this one gets the types to go into a dropdown list
  		String prepquery4 = "Select dName FROM DBDepartment";
  		// Used for query customer transactions
  		String prepquery5 = "UPDATE dbcustomer SET cuAmountPaid=cuAmountPaid+?, cuAmountDue=cuAmountDue-?"
                + " WHERE cuID= ?";
  		String prepquery5Secondary = "UPDATE dbcustomer SET cuAmountPaid=(cuAmountPaid+cuAmountDue),cuAmountDue=0 WHERE cuID = ?";
	   	// Queries for viewing upcoming events
  		String prepquery6 = "SELECT CONCAT(evName) AS 'Name', CONCAT(cuName) AS 'Held by', CONCAT(veName) AS 'Location', DATE_FORMAT(evStartDate, '%d %b%y') AS 'Start Date', DATEDIFF(evEndDate,evStartDate) AS 'Duration (Days)' FROM dbevent,dbcustomer,dbvenue WHERE evCustomerID = cuID AND evVenueID = veID AND evStartDate BETWEEN ? AND ?";
  		String prepquery6Secondary = "SELECT CONCAT(evName) AS 'Name', CONCAT(cuName) AS 'Held by', DATE_FORMAT(evStartDate, '%d %b%y') AS 'Start Date', DATEDIFF(evEndDate,evStartDate) AS 'Duration (Days)' FROM dbevent,dbcustomer,dbvenue WHERE evCustomerID = cuID AND evVenueID = veID AND veID = ?";
  		// Queries for process 7
  		String prepquery7 = "UPDATE dbemployee SET emWorkVenueID = ? WHERE emID = ?";
  		String prepquery7Secondary = "UPDATE dbemployee SET emManager = ? WHERE emID = ?";
  		
  		//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  		
  		//prepares the statements once 
  		try {
  			prepStat0 = con.prepareStatement(prepquery0);
 			prepStat2 = con.prepareStatement(prepquery2);
 			prepStat4 = con.prepareStatement(prepquery4);
 			prepStat5 = con.prepareStatement(prepquery5);
 			prepStat5Secondary = con.prepareStatement(prepquery5Secondary);
 			prepStat6 = con.prepareStatement(prepquery6);
 			prepStat6Secondary = con.prepareStatement(prepquery6Secondary);
 			prepStat7 = con.prepareStatement(prepquery7);
 			prepStat7Secondary = con.prepareStatement(prepquery7Secondary);
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
  		else if (e.getSource() == queryButton[8] )
  			process8();
  	}
  	
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  	// CUSTOM METHODS
  	
  	// Draw a table of a given table name
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
  	
  	// This converts a string to a SQL Date type
  	public java.sql.Date parseDate(String dateStr) throws java.text.ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dateParsed = dateFormat.parse(dateStr);
		java.sql.Date dateSQL = new java.sql.Date(dateParsed.getTime());
  		return dateSQL;
  	}
  	
  	// CUSTOM METHODS END
   	//++//++//++//++//++//++//++//++//++//++//++//++//++//++
  	//PUT YOUR OWN CODE INTO EACH PROCESS
  	//alter the comments!
  	//DELETE EXISTING CODE IF YOU con't use all the buttons
  
  	// Process 0: Giving discounts
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
					float discountFloat = (Float.parseFloat(discount)/100);
	            	prepStat0.setFloat(1,discountFloat);
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

	// Process 1: Updating customer stats
	public void process1() {
		try{
			int option = JOptionPane.showConfirmDialog(this, "This will update the fee status and no. of events booked for all customers. Continue?");
			if(option == 0){
				String query = "UPDATE dbcustomer SET cuEventsBooked = (SELECT COUNT(evCustomerID) FROM dbevent WHERE evCustomerID = cuID)";
				Statement stmt = con.createStatement();
				int rowsUpdated = stmt.executeUpdate(query);
				query = "UPDATE dbcustomer SET cuFeeStatus = IF(cuAmountDue > 0,'Due','Paid')";
				rowsUpdated = stmt.executeUpdate(query);
				JOptionPane.showMessageDialog(this, "Update success!");
				option = JOptionPane.showConfirmDialog(this, "View customer table?");
				if(option == 0){
					drawTable("dbcustomer");
				}
			}
		}catch(SQLException e){
			outputArea.setText(e.getMessage());
		}
		
	}
		
	// Process 2: Select employee(s) and perform updates
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

	// Process 3: View employee details: Name, Department and Work location
	public void process3() {
		
		//Query information
		outputArea.append("This query lists all the employees, their department, work location and their manager");
		
		  String query = "SELECT CONCAT(emFirstName,' ',emLastName) AS 'Employee Name',"
				  + "CONCAT(veName) AS 'Venue', CONCAT(deName) AS 'Department',"
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
	
	// Process 4: View money Flow of the company
	public void process4(){
		Object [] options = {"Expenditure","Income","Cancel"};
		String query = "";
		int option = JOptionPane.showOptionDialog(this, "Please choose an option: ", "Money Flow", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		if(option != 2){
			if(option == 1){
				query = "SELECT SUM(cuAmountPaid) AS 'Income', SUM(cuAmountDue) AS 'Due Income', SUM(cuAmountPaid)+SUM(cuAmountDue) AS 'Potential Income' FROM dbcustomer";
				outputArea.setText("This query shows incoming money into company's bank account. \nDue Income shows the money which is yet to be paid by the customers \nPotential Income is when the customers clear their money due"); 
			}
			else 
				if(option == 0){
					query = "SELECT SUM(emSalary) AS 'costs per month' FROM dbemployee";
					outputArea.setText("This query shows the expediture per month for the comany (which is a total of all the employee salaries).");
				}
			try{
				Statement stmt = con.createStatement();
				ResultSet resSet = stmt.executeQuery(query);
				tableResults.clearTable();
				tableResults.formatTable(resSet);
			}
			catch(SQLException e){
				outputArea.setText(e.getMessage());	
			}
		}
	}
	
	// Process 5: Customer transactions
	public void process5() {
		Object [] options = {"Some Amount","All Amount","Cancel"};
		int option = JOptionPane.showOptionDialog(this,"Choose how much amount is being cleared by the customer?", "Customer Transaction", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		if(option != 2){
			if(option == 0){
				try {
		            //get balance limit from user
		            String s = JOptionPane.showInputDialog(this, "Enter the customer ID: " );
		            //if data entered
		            if (s != null) {
		                //convert from text to integer
		                int sInt = Integer.parseInt(s);
		                //set parameter in prepared statement
		                prepStat5.setInt(3,sInt);
		                //run query and obtain result set
		                String q = JOptionPane.showInputDialog(this, "Enter the amount the customer paid: " );
		                if (q != null) {
			                //convert from text to float
			                float qInt = Float.parseFloat(q);
			                //set parameter in prepared statement
			                prepStat5.setFloat(1,qInt);
			                prepStat5.setFloat(2,qInt);
			                //run query and obtain result set
			                int rowsAffected = prepStat5.executeUpdate();
							JOptionPane.showMessageDialog(this, rowsAffected + " rows Affected");
			                outputArea.setText("Customer's record updated.");
			                drawTable("dbcustomer");
			            }
			            else {
			                outputArea.setText("No input entered!");
			                tableResults.clearTable();
			            }
		            }
		            else{
		            	outputArea.setText("No input entered!");
		            }
				}
				catch(SQLException e){
					outputArea.setText(e.getMessage());
				}
			}
			else if(option == 1){
				try {
		            //get balance limit from user
		            String s = JOptionPane.showInputDialog(this, "Enter the customer ID: " );
		            //if data entered
		            if (s != null) {
		                //convert from text to integer
		                int sInt = Integer.parseInt(s);
		                //set parameter in prepared statement
		                prepStat5Secondary.setInt(1,sInt);
		                //run query and obtain result set
		                int rowsAffected = prepStat5Secondary.executeUpdate();
						JOptionPane.showMessageDialog(this, rowsAffected + " rows Affected");
		                outputArea.setText("Customer's record updated.");
		                drawTable("dbcustomer");
		            }
		            else{
		            	outputArea.setText("No input entered!");
		            }
				}
				catch(SQLException e){
					outputArea.setText(e.getMessage());
				}
			}
		}
	}
	
	// Process 6: View upcoming event details by date or venue
	public void process6() {
		Object [] options = {"Date","Venue","Cancel"};
		int option = JOptionPane.showOptionDialog(this, 
				"Filter by ", 
				"View upcoming events", 
				JOptionPane.DEFAULT_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, options, 
				options[2]);
		if(option != 2){
			if(option == 0){
				String startDateStr = JOptionPane.showInputDialog(this, "Enter start date (YYYY-MM-DD)");
				String endDateStr = JOptionPane.showInputDialog(this,"Enter end date (YYYY-MM-DD)");
				try {
					try {
						prepStat6.setDate(1, parseDate(startDateStr));
						prepStat6.setDate(2, parseDate(endDateStr));
					} catch (java.text.ParseException e) {
						outputArea.setText(e.getMessage());
					}
					ResultSet resSet = prepStat6.executeQuery();
					tableResults.clearTable();
					tableResults.formatTable(resSet);
				} catch (SQLException e) {
					outputArea.setText(e.getMessage());
				}	
			}
			else if(option == 1){
				drawTable("dbvenue");
				String venue = JOptionPane.showInputDialog(this, "Enter Venue ID ");
				if (venue != null){
					int venueInt = Integer.parseInt(venue);
					try {
						prepStat6Secondary.setInt(1, venueInt);
						ResultSet resSet = prepStat6Secondary.executeQuery();
						tableResults.clearTable();
						tableResults.formatTable(resSet);
					}catch (SQLException e){
						outputArea.setText(e.getMessage());
					}
				}
				else{
					outputArea.setText("Invalid venue ID");
				}
			}
		}
	}
	
	// Process 7: Change an employee's work location
	public void process7() {
		drawTable("dbemployee");
		String emID = JOptionPane.showInputDialog(this,"Please enter the employee ID ");
		if(emID != null){
			try {
				prepStat7.setInt(2, Integer.parseInt(emID));
				drawTable("dbvenue");
				String newVeID = JOptionPane.showInputDialog(this, "Enter employee's new Work Venue ID");
				if(newVeID != null){
					prepStat7.setInt(1, Integer.parseInt(newVeID));
					prepStat7.executeUpdate();
					JOptionPane.showMessageDialog(this, "Update success!\n emID: "+ emID + " MOVED TO emWorkVenueID: " + newVeID);
					int option = JOptionPane.showConfirmDialog(this, "Would like to appoint a new manager for this employee?");
					if(option == 0){
						String query = "SELECT emFirstName,emLastName,emSSN FROM dbemployee WHERE emWorkVenueID = " + newVeID + " AND emManager = 'NULL'";
						outputArea.setText("The above table shows avilable managers at employee's new location");
						Statement stmt = con.createStatement();
						ResultSet resSet = stmt.executeQuery(query);
						tableResults.clearTable();
						tableResults.formatTable(resSet);
						String newManager = JOptionPane.showInputDialog(this, "Please enter the SSN of the new manager for this employee");
						if(newManager != null){
							prepStat7Secondary.setInt(2, Integer.parseInt(emID));
							prepStat7Secondary.setInt(1, Integer.parseInt(newManager));
							prepStat7Secondary.executeUpdate();
							JOptionPane.showMessageDialog(this, "Update success!\n emID: "+ emID + " Manager updated to emSSN: " + newManager);
						}
						else{
							outputArea.setText("Invalid SSN!");
						}
						query = "SELECT emID,emFirstName,emLastName,emSSN,emWorkVenueID,emManager FROM dbemployee WHERE emID = " + emID;
						resSet = stmt.executeQuery(query);
						tableResults.clearTable();
						tableResults.formatTable(resSet);
					}
				}
				else{
					outputArea.setText("Invalid ID");
				}
			} catch (SQLException e) {
				outputArea.setText(e.getMessage());
			}
		}
		else{
			outputArea.setText("Invalid ID!");
		}
		}
	
	// Process 8: blank process
	public void process8(){
	}
}