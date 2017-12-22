import java.util.ArrayList;

public class term {

	private String name;
	private ArrayList<String> nearest_terms;
	
	public term(String name){
		this.name=name;
		this.nearest_terms=new ArrayList<String>();
	}
	public void addList(String a){
		this.nearest_terms.add(a);
	}
	
	public String getname(){
		return name;
	}
	public ArrayList<String> getlist(){
		return this.nearest_terms;
	}
	public String writerformat(){
		String returner="";
		for(int i=0;i<this.nearest_terms.size();i++){
			returner=returner+this.nearest_terms.get(i)+",\t";
		}
		
		return returner;
	}
}
