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

import javax.print.attribute.standard.OutputDeviceAssigned;
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
	   	queryButton[4] = new JButton("View Money Flow of the company");  	
	   	// 2 Inputs: 1. Option 2. Amount
	   	queryButton[5] = new JButton("Update customer amount paid");
	   	// 3 Inputs: 1. Option 2. Date (Date picker) 3. Location (Dropdown)
	   	queryButton[6] = new JButton("View upcoming events");
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

	// Process 1: Updating customer stats
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
		int option = JOptionPane.showOptionDialog(this, "Please choose an option: ", "Money Flow", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
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
				tableResults.formatTable(resSet);
			}
			catch(SQLException e){
				outputArea.setText(e.getMessage());	
			}
		}
	}
	
	// TODO: Process 5: Customer transactions
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
	
	// TODO: Process 6: View upcoming event details by date or venue
	public void process6() {
	}
	
	// TODO: Customer Transactions
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