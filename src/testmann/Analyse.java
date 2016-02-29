package testmann;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.math.NumberUtils;

public class Analyse{
	
	public static String doCommand(String command,String set) throws Exception{
		if(set.contains(",")){
			return("Invalid entry!(Hint:provide space saparation)");
		}
		switch(command){
		case "upload":			return upload(set);
		case "search":			return search(set);
		case "delete":			return delete(set);
		case "set_statistic":	return set_statistic(set);
		case "most_common":		return most_common();
		case "longest":			return longest();
		case "exactly_in":		return exactly_in(set);
		case "create_intersection":	return create_intersection();
		case "longest_chain":	return longest_chain();	
		}
		return "";
	}
	
	public static String upload(String set) throws Exception{
		if(!doValidate(set).equals("good")){
			return(doValidate(set));
		}
		try {
		Postgres.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("insert into sets values ('"+getEncoded(set)+"')");
		Postgres.putTupple(query.toString());
		Postgres.st.close();
        Postgres.db.close();
        return("done upload for:"+set);
		}catch(Exception e){
			return("duplicacy found for:"+set);
		}
	}
	
	//Some check...
	public static String doValidate(String set){
		if(set.length()>20){
			return("Given set too long, keep it under 20 charracters please");
		}	
		return("good");
	}
	
	
	/*Postgres could't handle value with spaces :(, so following two functions i had too made*/
	public static String getEncoded(String set){
		String s[]=set.split("\\s+");
		StringBuilder out = new StringBuilder();
		for(int i=0;i<s.length-1;i++){
			out.append(s[i]+",");
		}
		out.append(s[s.length-1]);
		return(out.toString());
	}
	public static String getDecoded(String set){
		String s[]=set.split(",");
		StringBuilder out = new StringBuilder();
		for(int i=0;i<s.length-1;i++){
			out.append(s[i]+" ");
		}
		out.append(s[s.length-1]);
		return(out.toString());
	}
	
	public static String search(String set) throws Exception{
		List<String> results = new ArrayList<String>();
		Postgres.getConnection();
		Postgres.rs = Postgres.st.executeQuery("select * from sets");
		boolean found=false;
		String search = ","+set+",";
		while(Postgres.rs.next()){
			String subset = ","+Postgres.rs.getString(1)+",";
			if(subset.contains(search)){
				StringBuilder result = new StringBuilder();
				result.append(Postgres.rs.getRow());
				result.append("|"+getDecoded(Postgres.rs.getString(1)));
				results.add(result.toString());
				found=true;
			}
		}		
		Postgres.st.close();
        Postgres.db.close();
        if(!found){
        	results.add("Not Found:");
        }
        return("Found records are:"+results);
	}
	
	public static String delete(String set) throws Exception{
		Postgres.getConnection();
		int result=Postgres.st.executeUpdate("DELETE FROM sets WHERE set = '"+getEncoded(set)+"';");
		Postgres.st.close();
        Postgres.db.close();
        if(result==0){
        	return("RECORD NOT FOUND FOR:"+set);
        }else{
        	return("Deleted successfully");
        }     
	}
	
	public static String set_statistic(String set)throws Exception{
		Postgres.getConnection();
		Postgres.rs=Postgres.st.executeQuery("select * from sets where set='"+getEncoded(set)+"'");
		boolean notfound=true;
		while(Postgres.rs.next()){
			notfound=false;
			continue;
		}
		if(notfound){
			Postgres.st.close();
		    Postgres.db.close();
		    return("RECORD:NOT FOUND");
		}
		String s[]=set.split("\\s+");
		StringBuilder output = new StringBuilder();
		output.append("Number of Strings:"+Integer.toString(s.length)+", ");
		
		String shortest = s[0];
		for(int i=1;i<s.length;i++){
			if(s[i].length()<shortest.length()){
				shortest=s[i];
			}
		}
		output.append("Shortest String length:"+Integer.toString(shortest.length())+", ");
		
		String longest = s[0];
		for(int i=1;i<s.length;i++){
			if(s[i].length()>longest.length()){
				longest=s[i];
			}
		}
		output.append("longest String length:"+Integer.toString(longest.length())+", ");
		
		int total=0;
		for(int i=0;i<s.length;i++){
			total+=s[i].length();
		}
		int avg=total/s.length;
		output.append("average length:"+Integer.toString(avg)+", ");
		
		List<Integer> lengths = new ArrayList<Integer>();
		for(int i=0;i<s.length;i++){
			lengths.add(s[i].length());
		}
		Collections.sort(lengths);
		int median = lengths.get((lengths.size())/2);
		output.append("median length:"+Integer.toString(median));
		
		Postgres.st.close();
        Postgres.db.close();
        return(output.toString());
	}
	
	public static String most_common()throws Exception{
		Postgres.getConnection();
		Postgres.rs = Postgres.st.executeQuery("select * from sets");
		HashMap<String,Integer> strings  = new HashMap<String,Integer>();
		while(Postgres.rs.next()){
			String s[]=getDecoded(Postgres.rs.getString(1)).split("\\s+");
			for(String st:s){
				int value = strings.containsKey(st)?strings.get(st):0;
				strings.put(st,value+1);
			}
		}	
		int maxval=(Collections.max(strings.values())); 
		List<String> commonlist= new ArrayList<String>();
		for(Entry<String, Integer> entry:strings.entrySet()){
			if(entry.getValue()==maxval){
				commonlist.add(entry.getKey());
			}
		}
		Postgres.st.close();
        Postgres.db.close();
        return("Most common strings are:"+commonlist);
	}
	
	public static String longest()throws Exception{
		Postgres.getConnection();
		Postgres.rs = Postgres.st.executeQuery("select * from sets");
		List<String> longestlist= new ArrayList<String>();
		boolean start=true;
		while(Postgres.rs.next()){
			String s[]=getDecoded(Postgres.rs.getString(1)).split("\\s+");
			if(start){
				longestlist.add(s[0]);
				start=false;
			}
			for(String st:s){
				if(st.length()>longestlist.get(0).length()){
					longestlist.clear();
					longestlist.add(st);
				}
				if((st.length()==longestlist.get(0).length())&(!longestlist.contains(st))){
						longestlist.add(st);	
				}
			}
		}	
		Postgres.st.close();
        Postgres.db.close();
        return("Longest strings are:"+longestlist);
	}
	
	public static String exactly_in(String set)throws Exception{
		if(!NumberUtils.isNumber(set)){
			return("Please give a number");
		}
		Integer occurance = Integer.parseInt(set);
		Postgres.getConnection();
		Postgres.rs = Postgres.st.executeQuery("select * from sets");
		HashMap<String,Integer> strings  = new HashMap<String,Integer>();
		List<String> updated  = new ArrayList<String>();
		while(Postgres.rs.next()){
			String s[]=Postgres.rs.getString(1).split(",");
			for(String st:s){
				if(!updated.contains(st)){
					int value = strings.containsKey(st)?strings.get(st):0;
					strings.put(st,value+1);
					updated.add(st);
				}
			}
			updated.clear();
		}	
		List<String> occurancelist= new ArrayList<String>();
		for(Entry<String, Integer> entry:strings.entrySet()){
			if(entry.getValue()==occurance){
				occurancelist.add(entry.getKey());
			}
		}
		Postgres.st.close();
        Postgres.db.close();
        if(occurancelist.isEmpty())return("None found");
        return("found:"+occurancelist);
	}
	
	public static String create_intersection()throws Exception{
		Postgres.getConnection();
		Postgres.rs = Postgres.st.executeQuery("select * from sets order by set asc limit 2;");
		List<String> strings  = new ArrayList<String>();
		List<String> commons  = new ArrayList<String>();
		try {
		while(Postgres.rs.next()){
			String s[]=Postgres.rs.getString(1).split(",");
			for(String st:s){
				if(strings.contains(st)&!commons.contains(st)){
					commons.add(st);
					continue;
				}
				strings.add(st);
			}
		}
        if(commons.isEmpty()){
        	Postgres.st.close();
            Postgres.db.close();
        	return("No commanality found");
        }
        StringBuilder new_record = new StringBuilder();
        for(String new_str:commons){
        	new_record.append(new_str);
        }
        Postgres.st.execute("insert into sets values ('"+getEncoded(new_record.toString())+"')");
        Postgres.st.close();
        Postgres.db.close();
        return("New record created:"+new_record.toString());
		}catch(Exception e){
			return("Duplicacy detected");
		}
	}
	
	public static String longest_chain()throws Exception{
		Postgres.getConnection();
		Postgres.rs = Postgres.st.executeQuery("select * from sets");
		StringBuilder longest = new StringBuilder();
		StringBuilder chain= new StringBuilder();
		Character last = null;
		while(Postgres.rs.next()){
			String s=Postgres.rs.getString(1);
			if(last==null){
				chain.append(s);
				last=s.charAt(s.length()-1);
				continue;
			}
			if(s.charAt(0)==last){
				chain.append(s);
				last=s.charAt(s.length()-1);
				continue;
			}
			if(s.charAt(0)!=last){
				if(longest.toString().equals("")){
					longest.append(chain.toString());
					chain.setLength(0);
					chain.append(s);
					last=s.charAt(s.length()-1);
					continue;
				}
				if(longest.toString().length()<chain.length()){
					longest.setLength(0);
					longest.append(chain.toString());
					chain.setLength(0);
					chain.append(s);
					last=s.charAt(s.length()-1);
				}	
				if(longest.toString().length()>=chain.length()){
					chain.setLength(0);
					chain.append(s);
					last=s.charAt(s.length()-1);
				}
			}				
		}
		if(longest.toString().length()<chain.length()){
			longest.setLength(0);
			longest.append(chain.toString());
		}		
		Postgres.st.close();
        Postgres.db.close();
        return("Longest chain is:"+getDecoded(longest.toString()));
	}
	

}
