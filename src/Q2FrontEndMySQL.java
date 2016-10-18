import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

/*
 * Name- CloudCrackers
 * date-03/18/2015
 */

public class Q2FrontEndMySQL{
	//Connection pool for connecting to SQL.
	private static List<Connection> connectionPool = new ArrayList<Connection>();
	
	//This method get results from the database
	public static String getResult(String userId, String timeStamp) {
		ArrayList<String> list= new ArrayList<String>();
		list= getTweets(userId, timeStamp);
		String response = "TeamCloudCrackers,117333310089,020697801606,984101504455\n";
		if(list!=null) {
			response= response+ list.get(0);
			/*for(int i=0; i<list.size();i++) {
				if((i+1)%3!=0) {
					response= response+ list.get(i)+":";
				} else {
					response= response+list.get(i)+"\n";
				}
			}*/
		}
		return response;
	}
	
	//This method is used to connect to database and get results
	public static ArrayList<String> getTweets(String userId, String timeStamp) {
		Connection con= null;
		try {
			con= getConnection();
			
			PreparedStatement pst= con.prepareStatement("SELECT * FROM Tweets WHERE userId=?");
			pst.setString(1, userId+timeStamp);
			ResultSet rs= pst.executeQuery();
			ArrayList<String> list= new ArrayList<String>();
			while(rs.next()) {
				String text=StringEscapeUtils.unescapeJava(rs.getString("results"));
				list.add(text);
			}
			rs.close();
			pst.close();
			releaseConnection(con);
			return list;
			
		} catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
            return null;
        	//throw new MyDAOException(e);
		}
		
	}
	
	
	//Make connection to database
	private static synchronized Connection getConnection() throws MyDAOException {
		if(connectionPool.size() > 0) {
			return connectionPool.remove(connectionPool.size()-1);
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e) {
			throw new MyDAOException(e);
		}
		
		try {
			String url="jdbc:mysql://ec2-54-164-188-209.compute-1.amazonaws.com/test";
			String username="user";
			String password="password";
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new MyDAOException(e);
		}
	}
	
	
	//Release connection
	private static synchronized void releaseConnection(Connection con) {
		connectionPool.add(con);
	}
	
	//This method is used to parse the date and forward the request
	public static String processRequest(String userId, String timeStamp) {
		Date d = null;
		// Fri Apr 21 15:13:41 +0000 2014
		final String tweetDate = "yyyy-MM-dd HH:mm:ss";//EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(tweetDate, Locale.ENGLISH);
		sf.setLenient(true);
		try {
			d = sf.parse(timeStamp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sfd= new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		String date= sfd.format(d);
		String result = getResult(userId, date);
		String response="";
		if (result == null || result.equals("")) {
			response= "Invalid Message";
		}
		
		response =  result;
		return response;
		
	}
	
	//main method where the request first enters
	//We used undertow
    public static void main(final String[] args) throws FileNotFoundException {
    
        Undertow server = Undertow.builder()
                .addHttpListener(80,"ec2-54-164-188-209.compute-1.amazonaws.com")
                .setHandler(new HttpHandler() {

					@Override
					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
						if(exchange.getRequestURI().equals("/index")) {
							exchange.getResponseSender().send("hello");
						}
						Map<String,Deque<String>>map=exchange.getQueryParameters();
						String userId=map.get("userid").getFirst();
						String timeStamp=map.get("tweet_time").getFirst();
						String output=processRequest(userId, timeStamp);
						exchange.getResponseSender().send(output);
						
                        
					}
                }).build();
        server.start();
    }
}
