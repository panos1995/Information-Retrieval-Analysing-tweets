

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This program demonstrates how to draw XY line chart with XYDataset
 * using JFreechart library.
 * @author www.codejava.net
 *
 */
public class XYLineChartExample extends JFrame {

	private ArrayList<Integer> pos,neg,neutral,empty;
	private String name;
	public XYLineChartExample(ArrayList<Integer> pos,ArrayList<Integer> neg,ArrayList<Integer> neutral,ArrayList<Integer> empty,String name) {
		super("XY Line Chart Example with JFreechart");
		this.pos=new ArrayList<Integer>();
		this.neg=new ArrayList<Integer>();
		this.neutral= new ArrayList<Integer>();
		this.empty=new ArrayList<Integer>();
		this.name=name;
		this.pos=pos;
		this.neg=neg;
		this.neutral=neutral;
		this.empty=empty;
		JPanel chartPanel = createChartPanel();
		add(chartPanel, BorderLayout.CENTER);
		
		
		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	private JPanel createChartPanel() {
		String chartTitle = this.name;
		String xAxisLabel = "X";
		String yAxisLabel = "Y";
		
		XYDataset dataset = createDataset();
		
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, 
				xAxisLabel, yAxisLabel, dataset);
		
//		boolean showLegend = false;
//		boolean createURL = false;
//		boolean createTooltip = false;
//		
//		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, 
//				xAxisLabel, yAxisLabel, dataset, 
//				PlotOrientation.HORIZONTAL, showLegend, createTooltip, createURL);
		
		customizeChart(chart);
		
		// saves the chart as an image files
		File imageFile = new File("XYLineChart.png");
		int width = 640;
		int height = 480;
		
		try {
			ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
		} catch (IOException ex) {
			System.err.println(ex);
		}
		
		return new ChartPanel(chart);
	}

	private XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Positive");
		XYSeries series2 = new XYSeries("Negative");
		XYSeries series3 = new XYSeries("Neutrall");
		XYSeries series4 = new XYSeries("Empty");
		//Positives
		for(int i=0;i<pos.size();i++){
	/*pos*/		series1.add(i+1,pos.get(i));
	/*neg*/		series2.add(i+1,neg.get(i));
	/*neutral*/	series3.add(i+1,neutral.get(i));
	/*empty*/	series4.add(i+1,empty.get(i));
		}
		
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		dataset.addSeries(series4);
		
		return dataset;
	}
	
	private void customizeChart(JFreeChart chart) {
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.YELLOW);

		// sets thickness for series (using strokes)
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f));
		
		// sets paint color for plot outlines
		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		
		// sets renderer for lines
		plot.setRenderer(renderer);
		
		// sets plot background
		plot.setBackgroundPaint(Color.DARK_GRAY);
		
		// sets paint color for the grid lines
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				db db_=new db();
				Evaluation eval=null;
				Evaluation eval2=null;
				Evaluation eval3=null;
				Evaluation eval4=null;
				int[] p={1,2,4,5,10};
				List<Evaluation> evaluations= new ArrayList<Evaluation>();
				try {
					eval = new Evaluation(db_.selection("tsipras"),"Tsipras");
					eval2=new Evaluation(db_.selection("SYRIZA"),"Syriza");
					eval3= new Evaluation(db_.selection("nd"),"Nea-Dhmokratia");
					eval4= new Evaluation(db_.selection("mitsotakis"),"Mhtsotakis");
					evaluations.add(eval);
					evaluations.add(eval2);
					evaluations.add(eval3);
					evaluations.add(eval4);
					for(int i=0;i<evaluations.size();i++){
						int k=0;
						while(k<p.length){
						evaluations.get(i).findnearest(p[k]);
						evaluations.get(i).printnearest();
						evaluations.get(i).ExNeg();
						evaluations.get(i).ExPos();
						evaluations.get(i).NewPos();
						evaluations.get(i).newneg();
						System.out.println(evaluations.get(i).getname()+" nearest :"+p[k]+"   MeanNeg erwtima 7 = "+evaluations.get(i).meanExneg());
						System.out.println(evaluations.get(i).getname()+" nearest :"+p[k]+"   MeanPos erwtima 7 =  "+evaluations.get(i).meanExpos());
								++k;} 
						System.out.println();
						System.out.println(evaluations.get(i).getname()+"number of tweets = "+evaluations.get(i).getnumbertweets());
						System.out.println(evaluations.get(i).getname()+" mean positive = "+evaluations.get(i).mean_pos());
						System.out.println(evaluations.get(i).getname()+" mean negative = "+evaluations.get(i).mean_neg());
						System.out.println(evaluations.get(i).getname()+" standar deviation first for positive tweets and second for negative tweets v  = "+evaluations.get(i).standarDeviation());
						System.out.println(evaluations.get(i).getname()+" standar deviation as bernouli = "+evaluations.get(i).sd_table());
						System.out.println();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				new XYLineChartExample(eval.getposday(),eval.getnegday(),eval.getneutralday(),eval.getemptyday(),"TSIPRAS x=days y = values").setVisible(true);
				new XYLineChartExample(eval2.getposday(),eval2.getnegday(),eval2.getneutralday(),eval2.getemptyday(),"SYRIZA x=days y=values").setVisible(true);
				new XYLineChartExample(eval3.getposday(),eval3.getnegday(),eval3.getneutralday(),eval3.getemptyday(),"nd x=days y=values").setVisible(true);
				new XYLineChartExample(eval4.getposday(),eval4.getnegday(),eval4.getneutralday(),eval4.getemptyday(),"mitsotakis x=days y=values").setVisible(true);
			}
		});
	}
}