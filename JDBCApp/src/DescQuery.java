 /*
MSc IT DBIS Courseware
Monica Farrow

This class holds details for description and table/view name
*/

class DescQuery
{
	private String name;
	private String desc;
	
	public DescQuery (String n, String d)
	{
		name = n;
		desc = d;
	}
	
	public String getName()
	{	return name; }
	
	public String getDesc()
	{	return desc;	}
}
		
		