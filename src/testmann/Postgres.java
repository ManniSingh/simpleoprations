	package testmann;

	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.ResultSet;
	import java.sql.Statement;

	public class Postgres {
		
		public static ResultSet rs;
		public static Statement st;
		public static Connection db;
		
		public static void getConnection(){
	        try {
	            Class.forName("org.postgresql.Driver");
	        }
	        catch (java.lang.ClassNotFoundException e) {
	            System.out.println(e.getMessage());
	        }
	        
	        String url = "jdbc:postgresql://jumbo.db.elephantsql.com:5432/jddehwmq";
	        String username = "jddehwmq";
	        String password = "jnA02omJVr-C0SbbIaritpWqIoaz7XJU";
	        try {
	            db = DriverManager.getConnection(url, username, password);
	            st = db.createStatement();
	            }
	        catch (java.sql.SQLException e) {
	            System.out.println(e.getMessage());
	        }
	       
	    }
			
		
		public static void putTupple(String query) throws Exception{
			st.execute(query);
		}
		
		public static void createTable(String query) throws Exception{
			st.execute(query);
		}
		//extra
		public static void closeConnection() throws Exception{
			db.close();
		}
			
	}


