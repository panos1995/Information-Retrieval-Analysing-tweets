
public class term_df {

	private String term;
	private int df;
	
	
	public term_df(String term){
		this.term=term;
		this.df=0;
	}
	public term_df(String term,int df){
		this.term=term;
		this.df=df;
	}
	
	public String getterm(){
		return this.term;
	}
	public int getdf(){
		return this.df;
	}
	public void setterm(String term){
		this.term=term;
	}
	public void increasedf(){
		++this.df;
	}
	
	public void  print(){
		System.out.println("TERM : "+this.getterm()+"  df : "+this.getdf());
	}
}
