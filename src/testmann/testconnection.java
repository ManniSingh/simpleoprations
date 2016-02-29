package testmann;

//for testing functions locally.. (IGNORE THIS FILE)

public class testconnection {
	
	public static void main(String[] args) throws Exception
    {
			Postgres.getConnection();
			Postgres.rs = Postgres.st.executeQuery("select * from sets");
			StringBuilder longest = new StringBuilder();
			StringBuilder chain= new StringBuilder();
			Character last = null;
			int count=0;
			while(Postgres.rs.next()){
				String s=Postgres.rs.getString(1);
				
				System.out.println(s);
				System.out.println(longest.toString());
				System.out.println(chain.toString());
				System.out.println(s.charAt(0));
				System.out.println(last);
				//System.exit(0);
				
				if(count==0){
					chain.append(s);
					last=s.charAt(s.length()-1);
					count++;
					continue;
				}
				
				if(s.charAt(0)==last&&count!=0){
					chain.append(","+s);
					last=s.charAt(s.length()-1);
				}
				
				
					if(longest.toString().equals("")){
						longest.append(chain.toString());
						chain.setLength(0);
						chain.append(s);
						last=s.charAt(s.length()-1);
						continue;
					}
					if(longest.length()<chain.length()){
						longest.setLength(0);
						longest.append(chain.toString());
						chain.setLength(0);
						chain.append(s);
						last=s.charAt(s.length()-1);
					}	
					if(longest.length()>=chain.length()){
						chain.setLength(0);
						chain.append(s);
						last=s.charAt(s.length()-1);
					}
				
			}	
			Postgres.st.close();
	        Postgres.db.close();
        
    }

}
