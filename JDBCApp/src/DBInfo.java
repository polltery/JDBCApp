
/*
GUI DBMS Application 
Monica Farrow 

Contains all details about the database :
	Database Name
	OwnerName
	Table / View names
	Descriptions and corresponding table/view names

Methods;
	For descriptions, returns DescQuery object, given the description

*/

class DBInfo
{
//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++
//Just change from here to the next dividing line

	public static String dbName = "finaleventdb";		//database subject - free choice
	public static String tables [] =
		{							//table / view  names that don't need a description
									//i.e. the name is self-explanatory
			"",					    //Leave first one blank
			"DBEmployee", 
			"DBVenue", 
			"DBEvent",
			"DBDepartment",
			"DBCustomer",
		 };
	
	//Table/ View names and Description
	//i.e. tables or views where a description is useful
	public static  DescQuery descQueries [] = 
		{
	        new DescQuery ("", ""),  //LEAVE BLANK
		    new DescQuery ("DBEmpLocs", "Employees, deptment and locations"),
		};
	//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++//++
	//Don't change anything beyond this line
	
	//Find desc query object from list, using query description
	public static DescQuery findDescQuery(String queryTitle)  
	{
		DescQuery q = descQueries[0];
		for (int i = 0; i < descQueries.length; i++)
		{
			if (queryTitle.equals(descQueries[i].getDesc()))
			{
				q = descQueries[i];
			}
		}
		return q;	
	}	
}