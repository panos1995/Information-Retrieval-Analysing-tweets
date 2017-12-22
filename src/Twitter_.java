import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Twitter_ {

	private Twitter twitter;
	
	public Twitter_(){
		
		this.twitter=TwitterFactory.getSingleton();
	}
	
	@SuppressWarnings("deprecation")
	public void search_and_insert(String querry,String table,int days) throws TwitterException, SQLException{
		DateFormat DateFormat_ = new SimpleDateFormat("yyyy-MM-dd");
		Calendar Calendar_ = Calendar.getInstance();
		Calendar_.add(Calendar.DATE, -1-days);
		String since_ = DateFormat_.format(Calendar_.getTime());
		Query query_=new Query(querry).count(100).since(since_);
		long last_id=0;
		QueryResult result = null;
		do{
			try{
		 result = twitter.search(query_);
		db db_=new db();
		String category="";
		String date="";
		 for (Status status : result.getTweets()) 
		    {
			  
			 
			  category= checkcategory(status.getText());
			
		    db_.insert(table, (int)status.getId(), (int)status.getUser().getId(), DateFormat_.format(status.getCreatedAt()), status.getText(), category);
		   last_id=  status.getId(); 
		   }
		 
		 query_.setMaxId(last_id-1);
			}
			catch (TwitterException e) {
				System.out.println("error");
				e.printStackTrace();
			}
		}while(result.getMaxId() != last_id - 1);
		
	}
	/*
	 * 
	 * we break the if statement because we may have a token like goodnews:)badnews:(
	 * if we had if/else if statement this token would be good or bad.
	 * but it is neutral.
	 * 
	 */

	public String checkcategory(String text) {
		
		String[] tokens=text.split(" ");
		int counter_good=0;
		int counter_bad=0;
		for(String token : tokens){
			
			if(token.contains(":)")||(token.contains(": )"))){
				++counter_good;
			}
			
			if(token.contains(":(")||(token.contains(": ("))){
				++counter_bad;
			}
		}
		if(counter_bad>counter_good){
			return "bad";
		}else if(counter_good>counter_bad){
			return "good";
		}else return "neutral";
		
		
	}
}
