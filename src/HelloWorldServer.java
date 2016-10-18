import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

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
import java.util.Map;


public class HelloWorldServer {
	private static List<Connection> connectionPool = new ArrayList<Connection>();
	/*static String x = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	HashMap<String,HashMap<String, String>> map= new HashMap<String,HashMap<String,String>>();
	HashMap<String, String> mapper= new HashMap<String, String>();*/
	
	public static String getResult(String userId, String timeStamp) {
		ArrayList<String> list= new ArrayList<String>();
		list= getTweets(userId, timeStamp);
		
		String response = "TeamCloudCrackers,117333310089,020697801606,984101504455\n";
		for(int i=0; i<list.size();i++) {
			if(i%3!=0) {
				response= response+ list.get(i)+":";
			} else {
				response= response+list.get(i)+"\n";
			}
		}
		return response;
	}
	
	public static ArrayList<String> getTweets(String userId, String timeStamp) {
		Connection con= null;
		try {
			con= getConnection();
		
			PreparedStatement pst= con.prepareStatement("SELECT * FROM "+"Tweets"+ " WHERE userId=?"+" AND timeStamp=?");
			pst.setString(1, userId);
			pst.setString(2, timeStamp);
			ResultSet rs= pst.executeQuery();
			ArrayList<String> list= new ArrayList<String>();
			if(!rs.next()) {
				return null;
			} else {
				list.add(rs.getString("tweetId"));
				list.add(rs.getString("score"));
				list.add(rs.getString("tweet"));
			}
			rs.close();
			pst.close();
			releaseConnection(con);
			return list;
			
		} catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
		
	}
	
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
			return DriverManager.getConnection("jdbc:mysql:///test");
		} catch (SQLException e) {
			throw new MyDAOException(e);
		}
	}
	
	private static synchronized void releaseConnection(Connection con) {
		connectionPool.add(con);
	}
	
	public static String processRequest(String userId, String timeStamp) {
		String result = getResult(userId, timeStamp);
		String response="";
		if (result == null || result.equals("")) {
			response= "Invalid Message";
		}
		
		response =  result;
		return response;
		
	}

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(80,"ec2-54-152-208-39.compute-1.amazonaws.com")
                .setHandler(new HttpHandler() {

					@Override
					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
					
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

@reboot cd "path of the server" && "path of the script file"