
public class mini_tweet {

	private String text;
	private String date;
	private int id;
	private String category;
	
	public mini_tweet(){
		
	}
	public mini_tweet(String text,String date,int id){
		this.text=text;
		this.date=date;
		this.id=id;
		
	}
	
	public String getcategory(){
		return this.category;
	}
	public int getid(){
		return this.id;
	}
	public String gettext(){
		return this.text;
	}
	public String getdate(){
		return this.date;
	}
	
	public void setcategory(String category){
		this.category=category;
	}
	public void settext(String text){
		text=text.toUpperCase();
		this.text=text;
	}
	
	public void setdate(String date){
		this.date=date;
	}
	
	
	
	
	
}

