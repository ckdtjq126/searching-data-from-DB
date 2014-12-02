import java.sql.*; 
import java.util.*;

public class Query {	
	
	String url = "jdbc:db2://linux028.student.cs.uwaterloo.ca:50002/cs348";
	String user = "db2guest";
	String password = "supinebalsaanvilarea";
	
	public static void init(){
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		}
		catch(java.lang.ClassNotFoundException e){
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			System.exit(1);
		}		
	}
	
	private void PrintQuery(ResultSet res){
		try{
			// print the outline; tabs are used to align data fields
			System.out.println(" Instructor\tcourse\tSection\tDays\t" +
					"Time\t\tRoom\tSize\tAve\tMin\tMax");
			
			// in each loop 
			while(res.next()){
				// gets the required field from the set
				String Instructor = res.getString("PNAME");
				String Course = res.getString("CNO");
				String Section = res.getString("SECTION");
				String Days = res.getString("DAY");
				String Time = res.getString("HOUR");
				String Room = res.getString("ROOM");
				String Size = res.getString("Size");
				String Ave = res.getString("Ave");
				String Min = res.getString("Min");
				String Max = res.getString("Max");
				
				// if there exists same class occuring M, W and F, we combine them together.

				while(res.next()){
					if(res.getString("PNAME").equals(Instructor) && res.getString("CNO").equals(Course)
							&& res.getString("SECTION").equals(Section) && res.getString("HOUR").equals(Time)
							&& res.getString("ROOM").equals(Room)){
						Days = Days + res.getString("DAY");
					}
					else{
						res.previous();
						break;
					}
				}
				
				// in case of RT is printed, we fix it to TR
				if(Days.equals("RT"))
					Days = "TR";
				// in case of FMW is printed, we fix it to MWF
				else if(Days.equals("FMW"))
					Days = "MWF";
				
				// print the result; tabs are used to align data fields
				System.out.println(" "+Instructor+"\t\t"+Course+"\t"+Section+"\t"+
				Days+"\t"+Time+"\t"+Room+"\t"+Size+"\t"+Ave+"\t"+Min+"\t"+Max);
			}
		}
		catch(SQLException ex){
			System.err.println("SQLException in PrintQuery:" + ex.getMessage());
			System.exit(1);
		}
	}
	
	public void SearchQuery(String Dept, String Year){
		
		/*
		ResultSet res = null;
		Connection con = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		
		*/
		try{
			Connection con = DriverManager.getConnection(url, user, password);			
			Statement stmt = con.createStatement();
			stmt.execute("SET CURRENT_SCHEMA = enrollment");
			PreparedStatement ps = con.prepareStatement("SELECT P.PNAME, C.CNO, C.SECTION, S.DAY, S.HOUR, S.ROOM, " +
					"count(*) as Size, avg(E.MARK) as Ave, min(E.MARK) as Min, max(E.MARK) as Max " +
					"FROM Professor P, Class C, Schedule S, Enrollment E " +
					"WHERE P.EID = C.INSTRUCTOR AND " +
							"C.CNO = S.CNO AND C.CNO = E.CNO AND C.TERM = S.TERM AND C.TERM = E.TERM " +
							"AND C.SECTION = S.SECTION AND C.SECTION = E.SECTION " +
							"AND P.DEPT = ? AND C.TERM = ? " +
							"GROUP BY P.PNAME, C.CNO, C.SECTION, S.DAY, S.HOUR, S.ROOM "+
							// if there are more than one professor, we sort them by their name.
							// if their names are indentical, then we sort by course number.
							"order by P.PNAME, C.CNO",
							ResultSet.TYPE_SCROLL_INSENSITIVE, 
							ResultSet.CONCUR_READ_ONLY);

			
			// First line of output indicating the outline
			System.out.println("Class Information for SChool "+Dept+" in Year "+Year);
			System.out.println();
			
			System.out.println("Term: Winter");
			// preparing and executing a Query (from course notes)
			ps.setString(1, Dept);
			ps.setString(2, "W"+Year);
			ResultSet res = ps.executeQuery();
			// prints Query requested.
			PrintQuery(res);
			
			System.out.println("Term: Spring");
			ps.setString(1, Dept);
			ps.setString(2, "S"+Year);
			res = ps.executeQuery();
			PrintQuery(res);
			
			System.out.println("Term: Fall");
			ps.setString(1, Dept);
			ps.setString(2, "F"+Year);
			res = ps.executeQuery();
			PrintQuery(res);
			
			// close everything which have been used. 
			con.close();
			ps.close();
			stmt.close();
			res.close();
			
		}
		catch(SQLException ex){
			System.err.println("SQLException in SearchQuery:" + ex.getMessage());
			System.exit(1);
		}
	}
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		// create a new instance of data.
		A4 data = new A4();
		// use the driver from the assignment outline.
		init();
		
		// gets the data from input(System.in) and prints the data.
		while(true){
			// get Department from input
			System.out.println("type Department name: ");
			String Dept = in.nextLine();
			if(Dept.equals("exit")){
				System.out.println("exiting Query!");
				break;
			}
			
			// get Year from input
			System.out.println("type Year: ");
			String Year = in.nextLine();
			if(Year.equals("exit")){
				System.out.println("exiting Query!");
				break;
			}
			// Search
			data.SearchQuery(Dept, Year);
		}
	}
}