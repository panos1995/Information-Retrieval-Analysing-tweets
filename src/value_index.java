
public class value_index {

	private double value;
	private int index;
	
	public value_index(double value,int index){
		this.value=value;
		this.index=index;
	}
	
	public double getvalue(){
		return this.value;
	
	}
	public int getindex(){
		return this.index;
	}
	
	public void print(){
		System.out.println("index : "+this.index+" value : "+this.value);
	}
}
