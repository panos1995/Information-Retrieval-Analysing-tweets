import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class db {

	public db(){}
	

	public  Connection getDBConnection() {
		String url = "jdbc:mysql://localhost:3306/anaktisi?autoReconnect=true&useSSL=false";
		String username = "java";
		String password = "123";
		Connection dbConnection = null;

		

		try {

			dbConnection = (Connection) DriverManager.getConnection(
                            url, username,password);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	public void  insert(String table,int id,int userid,String date,String text,String category) throws SQLException{
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		String insertTableSQL = "INSERT IGNORE INTO "+table
				+ "(id_, userid_, date_, text_,category_) VALUES"
				+ "(?,?,?,?,?)";

		try {
			dbConnection = getDBConnection();
			preparedStatement = (PreparedStatement) dbConnection.prepareStatement(insertTableSQL);

			preparedStatement.setInt(1,id);
			preparedStatement.setInt(2, userid);
			preparedStatement.setString(3, date);
			preparedStatement.setString(4, text);
			preparedStatement.setString(5, category);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

		
	}catch (SQLException e) {

		System.out.println(e.getMessage());

	} finally {

		if (preparedStatement != null) {
			preparedStatement.close();
		}

		if (dbConnection != null) {
			dbConnection.close();
		}

	}
	
		
	}
	
	@SuppressWarnings("finally")
	public ArrayList<mini_tweet>  selection(String tablename) throws SQLException{
		
		
		Connection dbConnection = null;
		
		String query = "SELECT text_,date_,id_ FROM "+tablename+" order by date_";
		 ArrayList<mini_tweet> list=new ArrayList<mini_tweet>();
		try {
			dbConnection = getDBConnection();
			 Statement st = (Statement) dbConnection.createStatement();
			 ResultSet rs = st.executeQuery(query);
			 
			
			   while (rs.next())
			      { 
			        String text_ = rs.getString("text_");
			        String date_ = rs.getString("date_");
			        int id_=rs.getInt("id_");
			        
			        mini_tweet temp= new  mini_tweet(text_,date_,id_);
			        list.add(temp);
			        
			      }

		}catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (dbConnection != null) {
				dbConnection.close();
			}
			
			return list;
	}
		
	}	
}
