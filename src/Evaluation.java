import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jblas.DoubleMatrix;
import org.jblas.Singular;

/*
 * TO DO NA VGALW LINKS
 * NA KANW APOKOPH.
 */
public class Evaluation {

	private  int Positives;
	private  int Negatives;
	private ArrayList<ArrayList<mini_tweet>> data;
	private  int Neutrals;
	private  int n_tweets;
	private int empty;
	private ArrayList<Integer> Positive_day;
	private ArrayList<Integer> Negative_day;
	private ArrayList<Integer> Neutral_day;
	private ArrayList<Integer> Empty_day;
	private  List<term_df> term_df;
	private double[][] term_doc;
	private List<Double> idf;
	private DoubleMatrix USV[];
	private double[][] Uk;
	private double[][] similarity;
	private List<term> pnearest;
	private List<String> NegativesWords;
	private List<String> PositiveWords;
	private List<String> exNeg;
	private List<String> expos;
	private int p;
	private String name;
	
	public Evaluation(ArrayList<mini_tweet> list, String string) throws IOException {
     this.data=new ArrayList<ArrayList<mini_tweet>>();
     this.Positive_day=new ArrayList<Integer>();
     this.Negative_day=new ArrayList<Integer>();
     this.Empty_day=new ArrayList<Integer>();
     this.Neutral_day=new ArrayList<Integer>();
     this.term_df=new ArrayList<term_df>();
     this.idf=new ArrayList<Double>();
    SortTweetsByDays(list);
    setnumbertweets();
    this.Cleantext();
    this.term_df();
    this.setTermDoc();
    this.USV=Singular.fullSVD(new DoubleMatrix(this.term_doc));
    Uk(3);//ftiaxnei ton U[m][3]
    this.similarity();
    this.name=string;
    
	}
public ArrayList<ArrayList<mini_tweet>> getdata(){
	return this.data;
}

public ArrayList<mini_tweet> get_tweet_by_day(int i){
	if(i<=0){
		System.out.println("Put numbers >0");
	}
	return data.get(i);
}
public int getpos(){
	return this.Positives;
}
public int getneg(){
	return this.Negatives;
}
public int getneutral(){
	return this.Neutrals;
}
public int getempty(){
	return this.empty;
}
public int getnumbertweets(){
	return this.n_tweets;
}
public ArrayList<Integer> getposday(){
	return Positive_day;
}
public ArrayList<Integer> getnegday(){
	return Negative_day;
}
public ArrayList<Integer> getneutralday(){
	return Neutral_day;
}
public ArrayList<Integer> getemptyday(){
	return Empty_day;
}
	/*
	 * Το input ειναι η λιστα με ολα τα δεδομενα μας ταξινομημενα βαση της
	 * ημερομηνιας. η μεθοδος θα επιστρεφει ενα arrayList που μεσα σε αυτο θα
	 * υπαρχουν κ=αριθμος ημερων arraylist με αντικειμενα mini_tweet
	 */
	public void SortTweetsByDays(ArrayList<mini_tweet> list ){

		/*
		 * Remove -RT 
		 */
		for(int i=0;i<list.size();i++){
			for(int j=i+1;j<list.size();j++){
				if(list.get(i).gettext().equalsIgnoreCase(list.get(j).gettext())){
					list.remove(j);
				}
			}
		}
		
		ArrayList<ArrayList<mini_tweet>> finalList = new ArrayList<ArrayList<mini_tweet>>();

		int k=0;

		ArrayList<mini_tweet> firstday=new ArrayList<mini_tweet>();
		
		firstday.add(list.get(0));
		finalList.add(firstday);
		for( int i=1;i<list.size();i++){
			mini_tweet tweet_temp=new mini_tweet(list.get(i).gettext(),list.get(i).getdate(),list.get(i).getid());
			if(!(list.get(i).getdate().equals(finalList.get(k).get(0).getdate()))){
				++k;
				ArrayList<mini_tweet> nextday=new ArrayList<mini_tweet>();
				nextday.add(tweet_temp);
				finalList.add(nextday);
			}else{
				finalList.get(k).add(tweet_temp);
			}
		}

	

	this.data=finalList;
}
	/*
	 * cleans the text from numbers commas etc.
	 */
	public void Cleantext() throws IOException{
	String[] tokens;
	String text_set;
	String category="";
		for(int i=0;i<this.data.size();i++){
			/*
			 * counters for statistics per day
			 */
			int counter_pos=0;
			int counter_neg=0;
			int counter_neutral=0;
			int counter_empty=0;
			for(int j=0;j<this.data.get(i).size();j++){
				String res="";
				String temp=this.data.get(i).get(j).gettext();
				res=temp.replaceAll("@[A-Za-z]+", "");
				res=res.replaceAll("[\\@\\\"\\'\\{\\}\\;\\?\\\\|\\\\…\\>\\<\\]\\[\\)\\(\\_\\=\\&\\*\\€\\%\\#\\!\\-\\+\\.\\$\\^:,]","");
				res=res.replaceAll("[htpps://[A-Za-z]]+", "");
				res=res.replaceAll("[0-9]+", "");
				res=res.trim();
				res=stripAccents(res.toUpperCase());
				tokens=removestopwords(res);
				tokens=stem(tokens);
				category=Category(positive(tokens),negative(tokens));
				
				
				
				text_set= Arrays.toString(tokens);
				text_set=text_set.substring(1, text_set.length()-1).replaceAll(",", "");
				if(!(text_set.trim().equals(""))){
				this.data.get(i).get(j).settext(text_set);
				}else {
					this.data.get(i).get(j).settext("EMPTY");
					category="EMPTY";
				}
				SetStatics(category);
				
				if(category.equals("Positive"))
					++counter_pos;
				
				if(category.equals("Negative"))
					++counter_neg;
				
				if(category.equals("Neutral"))
					++counter_neutral;
				if(category.equals("EMPTY"));
					++counter_empty;
				//END OF THE DAY
			}
			//add the values to arraylist//
			this.Positive_day.add(counter_pos);
			this.Negative_day.add(counter_neg);
			this.Neutral_day.add(counter_neutral);
			this.Empty_day.add(counter_empty);
		}
}
	private void SetStatics(String category) {
		// TODO Auto-generated method stub
		if(category.equals("Positive"))
			++this.Positives;
		
		if(category.equals("Negative"))
			++this.Negatives;
		
		if(category.equals("Neutral"))
			++this.Neutrals;
		if(category.equals("EMPTY"));
			++this.empty;
	}
	private String[] stem(String[] tokens_) {
		// TODO Auto-generated method stub
		ArrayList<String> result=new ArrayList<String>();
		GreekStemmer GS=new GreekStemmer();
		for (String tk : tokens_){
			result.add(GS.stem(tk.trim()));
		}
		String[] returner_=result.toArray(new String[]{});
		return returner_;
	}
	private int positive(String[] tokens_) throws IOException{
		/*
		 * read the positive words and insert them into an array
		 */
		 
			BufferedReader abc = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Panagiotis\\pos.txt"), StandardCharsets.UTF_8));
			List<String> lines = new ArrayList<String>();
			String line="";
			while(( line = abc.readLine()) != null) {
			    lines.add(line);
			    
			}
			abc.close();

			this.PositiveWords=lines;
			String[] data = lines.toArray(new String[]{});
			int counter=0;
		for(String tk : tokens_){
			for(String pos : data){
				if(tk.equals(pos)){
					++counter;
				}
			}
		}
		
		return counter;	
	}
	private int negative(String[] tokens_) throws IOException{
		/*
		 * read the negatives words and insert them into an array
		 */
		 
			BufferedReader abc = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Panagiotis\\neg.txt"), StandardCharsets.UTF_8));
			List<String> lines = new ArrayList<String>();
			String line="";
			while(( line = abc.readLine()) != null) {
			    lines.add(line);
			    
			}
			abc.close();

			this.NegativesWords=lines;
			String[] data = lines.toArray(new String[]{});
			int counter=0;
		for(String tk : tokens_){
			for(String neg : data){
				if(tk.equals(neg)){
					++counter;
				}
			}
		}
		
		return counter;	
	}
	private String Category(int pos,int neg){
		String category="";
		if(pos==neg)
			category= "Neutral";
		if(pos>neg)
			category="Positive";
		if(pos<neg)
			category="Negative";
		
		return category;
		
			
	}
	private String[] removestopwords(String res) throws IOException {
		// TODO Auto-generated method stub
		/*
		 * Read the file.
		 */
		BufferedReader abc = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Panagiotis\\stop.txt"), StandardCharsets.UTF_8));
		List<String> lines = new ArrayList<String>();
		String line="";
		while(( line = abc.readLine()) != null) {
		    lines.add(line);
		    
		}
		abc.close();

		
		String[] data = lines.toArray(new String[]{});
		res= res.trim();
		String terms[]=res.split(" ");
		ArrayList<String> result=new ArrayList<String>();
		
		
		for(String token : terms){
			if(!(isstopword(data,token))&& !(token.trim().equalsIgnoreCase(""))){
				result.add(token);
			}
		}
		 String[] returner =result.toArray(new String[]{});
		
		
		return  returner;
	}
	public static String stripAccents(String s) 
	{
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}
	public static boolean	isstopword (String[] words,String token){
		 
		for(String stop : words){
			if(stop.equalsIgnoreCase(token)){
				return true;
			}
			
		}
		
		return false ;
	}
	private void setnumbertweets(){
		int counter=0;
		for(int i =0;i<this.data.size();i++){
			counter=counter+this.data.get(i).size();
		}
		this.n_tweets=counter;
	}
	public void print(int i ){
		System.out.println("plhthos tweets "+data.get(i).size());
		for(int k=0;k<data.get(i).size();k++){
			System.out.println(data.get(i).get(k).gettext()+"\n\n");
		}
		
	
	}
	//ektupwnei to plhthos twn kathgoriwn.
	public void print( ){
		
	System.out.println("POSITIVES: "+this.Positives);
	System.out.println("NEGATIVES: "+this.Negatives);
	System.out.println("NEUTRAL: "+this.Neutrals );
	System.out.println("EMPTY: "+this.empty+"\n");
	}
	//mesos oros 8etikwn sthn sulogi
	public double mean_pos(){
		return (double)this.Positives/(this.Positives+this.Negatives);
	}
	//mesos oros arnitikwn sthn sulogi//
	public double mean_neg(){
		return (double)this.Negatives/(this.Positives+this.Negatives);
	}
	public double pos_per_day(){
		return (double)this.Positives/this.data.size();
		
	}
	public double neg_per_day(){
		return (double)this.Negatives/this.data.size();
	}
	//episterefei arrayList pou sthn prwti 8esi exei to sd gia ta 8etika kai sthn deuteri to sd gia ta arnitika//
	public ArrayList<Double> standarDeviation(){
		// Positive//
		ArrayList<Double> returner=new ArrayList<Double>();
		
		double sd=0;
		double positivemean=pos_per_day();
		for(int i=0;i<this.Positive_day.size();i++){
			 sd = sd+ Math.pow((positivemean-this.Positive_day.get(i)), 2);
		}
		sd=sd/this.Positives;
		sd=Math.sqrt(sd);
		returner.add(sd);
		
		// negative//
		sd=0;
		double negativemean=neg_per_day();
		for(int i=0;i<this.Negative_day.size();i++){
			 sd = sd+ Math.pow((negativemean-this.Negative_day.get(i)), 2);
		}
		sd=sd/this.Negatives;
		sd=Math.sqrt(sd);
		returner.add(sd);
		return returner;
	}
	/*
	 * 
	 * SD Θετικών-Αρνητικών στον πινακα.
	 * θεωρούμε οτι εχουμε μια μεταβλητη bernouli που παιρνει 2 πιθανα αποτελεσματα (ΘΕΤΙΚΟ - ΑΡΝΗΤΙΚΟ)
	 * Γνωριζουμε οτι η τυπικη αποκλιση ειναι ριζα(p(1-p)) , εστω p =θετικα/θετικα+αρνητικα.
	 */
	public double sd_table(){
		return Math.sqrt(this.mean_pos()*(1-this.mean_pos()));
	}
	public void printday(){
		for(int i=0;i<this.Positive_day.size();i++){
		System.out.println(this.Positive_day.get(i));}
		
	}
	//plhthos term me df>=2//
	public void term_df(){
		ArrayList<String> terms=new ArrayList<String>();
		
		//exw olous tous orous sto terms//
		for(int i=0;i<this.data.size();i++){
			for(int j=0;j<this.data.get(i).size();j++){
				List<String> myList = new ArrayList<String>(Arrays.asList(this.data.get(i).get(j).gettext().split(" ")));
				terms.addAll(myList);
			}		
		}
		//UNIQUE TIMES TERM.
		List<String> listDistinct = terms.stream().distinct().collect(Collectors.toList());
		
		//Θα μετρήσουμε το df του καθε unique term//
		List<term_df> ListTerm=new ArrayList<term_df>();
		
		for (int i=0;i<listDistinct.size();i++){
			term_df temp=new term_df(listDistinct.get(i));
			ListTerm.add(temp);
		}
		/*
		 * gia kathe tweet an to tweet periexei ton term au3hse tou to df.
		 */
		for(int i=0;i<this.data.size();i++){
			for(int j=0;j<this.data.get(i).size();j++){
				for(int k=0;k<ListTerm.size();k++){
					if(this.data.get(i).get(j).gettext().contains(ListTerm.get(k).getterm())){
						ListTerm.get(k).increasedf();
					}
			}
		}
			}
		List<term_df> term_df = new ArrayList<term_df>();
		for(int i =0;i<ListTerm.size();i++){
			//an exei df megalutero iso tou 2 valto sthn lista 
			if(ListTerm.get(i).getdf()>=2){
				term_df.add(ListTerm.get(i));
			}
		}
		
		this.term_df=term_df;
	}
	//prepei na exw ftia3ei ton term_df prwta.
	public void setTermDoc(){
		this.term_doc=new double[this.term_df.size()][this.n_tweets];
		int doc=0;
		
		double termidf=0;
		for(int term=0;term<this.term_df.size();term++){
			doc=0;
			termidf=Math.log(this.n_tweets/this.term_df.get(term).getdf());//vriskw idf
			this.idf.add(termidf);
				for(int i=0;i<this.data.size();i++){
					for(int j=0;j<this.data.get(i).size();j++){
							Pattern p= Pattern.compile(this.term_df.get(term).getterm());//kanei pattern to term
							String input= this.data.get(i).get(j).gettext();//eisodo to tweet.
							Matcher m =p.matcher(input);
							int counter=0;
							while(m.find()){
								++counter;
							}//vriskei to tf(term,doc) 
							
							
							this.term_doc[term][doc]=counter;//tf
							++doc;
							}//days
					}//doc
		}//term
		
	}//function
	public List<term_df> getterm_df(){
		return this.term_df;
	}
	public double[][] getterm_doc(){
		return this.term_doc;
	}
	public void printtermdoc(){
		for (int i = 0; i < this.term_doc.length; i++) {
		    for (int j = 0; j < this.term_doc[i].length; j++) {
		        System.out.print(this.term_doc[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	public void printgreater(){
		int counter=0;
		for(int i=0;i<this.term_doc.length;i++){
			for(int j=0;j<this.term_doc[i].length;j++){
				if(this.term_doc[i][j]!=0){
					++counter;
				}
			}
		}
		System.out.println("weight !=0 "+counter);
		System.out.println("out of "+this.n_tweets*this.term_df.size());
		System.out.println("rate : "+(double)counter/(this.n_tweets*this.term_df.size()));
	}
	public void printU(){
		
		for (int i = 0; i < Uk.length; i++) {
		    for (int j = 0; j < Uk[i].length; j++) {
		        System.out.print(Uk[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	public void Uk(int k){
		double[][] U=this.USV[0].toArray2();
		double[][] _Uk=new double[U.length][k];
		for(int i=0;i<U.length;i++){
			Arrays.sort(U[i]);//sortarei thn grammh//
		}
		//vriskei ta k prwta//
		for(int i=0;i<U.length;i++){
			int col=0;
			for(int j=U[0].length-1;j>U[0].length-k-1;j--){//to sortarisma ginete se fthinousa seira ara pernoume apo to telos ews kai k prin to telos
				_Uk[i][col]=U[i][j];
				++col;
				
			}
		}
		List<Double> ecdnorm=new ArrayList<Double>();
		//vriskei ta norm tou ka8e term//
		for(int i=0;i<_Uk.length;i++){
			double norm=0;
			for(int j=0;j<_Uk[i].length;j++){
				norm=norm+Math.pow(_Uk[i][j], 2);
			}
			norm=Math.sqrt(norm);
			ecdnorm.add(norm);
		}
		for(int i=0;i<_Uk.length;i++){
			for(int j=0;j<_Uk[i].length;j++){
				_Uk[i][j]=_Uk[i][j]/ecdnorm.get(i);
						}
		}
		
		this.Uk=_Uk;
	}
	public void similarity(){
		DoubleMatrix _Uk=new DoubleMatrix(this.Uk);
		DoubleMatrix _UkT=_Uk.transpose();
		DoubleMatrix similarity=_Uk.mmul(_UkT);
		double[][] arraysim=similarity.toArray2();
		this.similarity=arraysim;
	}
	public void printSimil(){
		for (int i = 0; i <this.similarity.length; i++) {
		    for (int j = 0; j < similarity[i].length; j++) {
		        System.out.print(similarity[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	public void findnearest(int p){
		List<value_index> term_similars = new ArrayList<value_index>();
		List<term> term_and_nearest = new ArrayList<term>();
		this.p=p;
		for(int i=0;i<this.similarity.length;i++){
			for(int j=0;j<this.similarity[i].length;j++){
				if(i!=j){
					term_similars.add(new value_index(this.similarity[i][j],j));//value kai index
				}
				
				}
			//otan topo8etei ola ta similarities prepei na ta sortarei vasei tou value kai na kratisoume ta prwta p//
			term_similars=term_similars
		            .stream()
		            .sorted((e1, e2) -> Double.compare(e2.getvalue(),
		                    e1.getvalue())).collect(Collectors.toList());
			

			//afou exoume tous orous se fthinousa seira kratame tous prwtous p kai tous topothetoume sthn lista
			term temp=new term(this.term_df.get(i).getterm());//h seira me tous orous exei krath8ei.
			
			for(int k=0;k<p;k++){
				//pernei to index twn prwton k kai topothetei sthn lista
				int index=term_similars.get(k).getindex();
				temp.addList(this.term_df.get(index).getterm());
				}
			term_and_nearest.add(temp);
				term_similars.clear();//adeiazoume gia kathe term.
			}
		this.pnearest=term_and_nearest;
		
		}
	public void printnearest(){

		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(this.name+" "+this.p+" terms_and_nearests.txt"), "utf-8"));
		    writer.write("TERM 				NEARESTS \n\n");
		    for(int i=0;i<this.pnearest.size();i++){
		    	writer.write(this.pnearest.get(i).getname()+"\t\t\t"+this.pnearest.get(i).writerformat()+"\n") ;
		    }
		    writer.write("END OF FILE");
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		
	
	}
	
	public void ExPos(){
		List<String> expos=new ArrayList<String>();
		for(int i=0;i<this.pnearest.size();i++){
			if(this.PositiveWords.contains(this.pnearest.get(i).getname())){
				//iterate oloi thn lista me tous nearest tou term
				for(int j=0;j<this.pnearest.get(i).getlist().size();j++){
					expos.add(this.pnearest.get(i).getlist().get(j));
				}
			}
		}
		
		this.expos=expos;
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(this.name+" "+this.p+" exPos.txt"), "utf-8"));
		    writer.write("EXPOS FILE\n\n");
		   for(int i=0;i<expos.size();i++){
			   writer.write(expos.get(i)+"\n");
		   }
		    writer.write("END OF FILE");
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		
	
	}
	
	public void ExNeg(){
		List<String> exneg=new ArrayList<String>();
		for(int i=0;i<this.pnearest.size();i++){
			if(this.NegativesWords.contains(this.pnearest.get(i).getname())){
				//iterate oloi thn lista me tous nearest tou term
				for(int j=0;j<this.pnearest.get(i).getlist().size();j++){
					exneg.add(this.pnearest.get(i).getlist().get(j));
				}
			}
		}
		
		this.exNeg=exneg;
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(this.name+" "+this.p+"exNeg.txt"), "utf-8"));
		    writer.write("EXneg FILE\n\n");
		   for(int i=0;i<exneg.size();i++){
			   writer.write(exneg.get(i)+"\n");
		   }
		    writer.write("END OF FILE");
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		
	
	}
	
	public double meanExpos(){
		
		double counter=0.0;
		for(int i=0;i<this.expos.size();i++){
			if(this.PositiveWords.contains(this.expos.get(i))){
				++counter;
			}
		}
		return(counter/this.expos.size());
	}
		public double meanExneg(){
		
		double counter=0.0;
		for(int i=0;i<this.exNeg.size();i++){
			if(this.NegativesWords.contains(this.exNeg.get(i))){
				++counter;
			}
		}
	
		return(counter/this.exNeg.size());
	}
		public void NewPos(){
			List<String> newpos=new ArrayList<String>();
			for(int i=0;i<this.expos.size();i++){
				if(!(this.PositiveWords.contains(this.expos.get(i)))){
					newpos.add(this.expos.get(i));
				}
			}
			newpos=newpos.stream().distinct().collect(Collectors.toList());
			Writer writer = null;

			try {
			    writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(this.name+" "+this.p+"newpos.txt"), "utf-8"));
			    writer.write("newPos FILE"+this.p+" kontinoteroi \n\n");
			   for(int i=0;i<newpos.size();i++){
				   writer.write(newpos.get(i)+"\n");
			   }
			    writer.write("END OF FILE");
			} catch (IOException ex) {
			  // report
			} finally {
			   try {writer.close();} catch (Exception ex) {/*ignore*/}
			}
			
		
		}
		public void newneg(){
			List<String> newneg=new ArrayList<String>();
			for(int i=0;i<this.exNeg.size();i++){
				if(!(this.PositiveWords.contains(this.exNeg.get(i)))){
					newneg.add(this.exNeg.get(i));
				}
			}
			newneg=newneg.stream().distinct().collect(Collectors.toList());
			Writer writer = null;

			try {
			    writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(this.name+" "+this.p+"newneg.txt"), "utf-8"));
			    writer.write("newneg FILE"+this.p+" kontinoteroi \n\n");
			   for(int i=0;i<newneg.size();i++){
				   writer.write(newneg.get(i)+"\n");
			   }
			    writer.write("END OF FILE");
			} catch (IOException ex) {
			  // report
			} finally {
			   try {writer.close();} catch (Exception ex) {/*ignore*/}
			}
				
		}
		public void writeStatistics(){
			Writer writer = null;

			try {
			    writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(this.name+" statistics.txt"), "utf-8"));
			    writer.write("Statistics FILE \n");
			    writer.write("POSITIVES PER DAY");
			   for(int i=0;i<this.Positive_day.size();i++){
				   writer.write("day :"+i+1+"  "+this.Positive_day.get(i)+"\n");
			   }
			   writer.write("NEGATIVES PER DAY \n");
			   
			   for(int i=0;i<this.Negative_day.size();i++){
				   writer.write("day :"+i+1+"  "+this.Negative_day.get(i)+"\n");
			   }
			    writer.write("END OF FILE");
			} catch (IOException ex) {
			  // report
			} finally {
			   try {writer.close();} catch (Exception ex) {/*ignore*/}
			}
		}
			
			public String getname(){
				return this.name;
			}
		}
		
		
	
	
	

