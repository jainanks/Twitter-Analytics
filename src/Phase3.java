import io.undertow.Undertow;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 
 * @author CloudCrackers
 * Date- 04/03/2015
 *
 */
public class Project619Phase1Q1 extends HttpServlet {

	/*
	 * static Cache cache1= new Cache(); static Cache cache2= new Cache();
	 * static Cache cache4= new Cache();
	 */
	static Cache cache3 = new Cache();

	// main method
	public static void main(final String[] args) throws FileNotFoundException {
		
		final Q3 q3 = new Q3();
		final Q1 q1 = new Q1();
		final Q4 q4 = new Q4();
		final Q2 q2 = new Q2();
		final Q5 q5 = new Q5();
		final Q6 q6 = new Q6();
		ipaddress = args[0];
		
		Undertow server = Undertow.builder().addHttpListener(80, "0.0.0.0").setWorkerThreads(30)
				.setHandler(new HttpHandler() {

					@Override
					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
						Map<String, Deque<String>> map = exchange.getQueryParameters();
						String uri = exchange.getRequestURI();
						Sender sender = exchange.getResponseSender();
						if (uri.equals("/q1")) {
							String key = map.get("key").getFirst();
							String message = map.get("message").getFirst();
							String output = q1.processRequestq1(message, key);
							exchange.getResponseSender().send(output);
						}
						if (uri.equals("/q2")) {
							String userId = map.get("userid").getFirst();
							String timeStamp = map.get("tweet_time").getFirst();
							String output = q2.processRequestq2(userId, timeStamp);
							exchange.getResponseSender().send(output);
						}
						if (uri.equals("/q3")) {
							String id = map.get("userid").getFirst();
							long t1 = System.currentTimeMillis();
							// System.out.println();
							String result = q3.processRequestq3(id);
							// System.out.println(System.currentTimeMillis() -
							// t1);
							sender.send(result);
						}
						if (uri.equals("/q4")) {
							String userId = map.get("hashtag").getFirst();
							String start = map.get("start").getFirst();
							String end = map.get("end").getFirst();
							// System.out.println(userId + " " + start + " " +
							// end);
							String output = q4.processresultq4(userId, start, end);
							// System.out.println("out:" + output);
							exchange.getResponseSender().send(output);
						}
						if (uri.equals("/q5")) {
							String userIds = map.get("userlist").getFirst();
							String start = map.get("start").getFirst();
							String end = map.get("end").getFirst();
							String[] each = userIds.split(",");
							// System.out.println(start + " " + end);
							String output = q5.processRequestq5(each, start, end);
							// System.out.println("out:" + output);
							exchange.getResponseSender().send(output);
						}
						if (uri.equals("/q6")) {
							String userId = map.get("m").getFirst();
							String start = map.get("n").getFirst();
							// String output =
							// q6.processRequestFromCache(userId, start);
							// from database
							String output = q6.processRequestq6(userId, start);
							// System.out.println("out:" + output);
							exchange.getResponseSender().send(output);
						}
						if (uri.equals("/mem")) {
							long heapSize = Runtime.getRuntime().totalMemory();

							long heapMaxSize = Runtime.getRuntime().maxMemory();

							long heapFreeSize = Runtime.getRuntime().freeMemory();
							String output = "headSize :" + heapSize + "\n heapMaxSize " + heapMaxSize
									+ "\nheapFreeSize " + heapFreeSize;
							exchange.getResponseSender().send(output);
						}
					}
				}).build();
		server.start();
	}

	private static List<Connection> connectionPool = new ArrayList<Connection>();
	static String ipaddress = "";

	// get connection method for mysql
	static synchronized Connection getConnection() throws MyDAOException {
		if (connectionPool.size() > 0) {
			return connectionPool.remove(connectionPool.size() - 1);
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new MyDAOException(e);
		}

		try {
			// ec2-52-4-40-218.compute-1.amazonaws.com: aayush
			String url = "jdbc:mysql://" + ipaddress + "/test";
			String username = "user";
			String password = "password";
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new MyDAOException(e);
		}
	}

	// release connection
	static synchronized void releaseConnection(Connection con) {
		connectionPool.add(con);
	}

}

class Cache extends LinkedHashMap<String, String> {
	private static int capacity=1000000;
	public Cache(int capacity, float loadFactor) {
		super(capacity, 0.75f, true);
		Cache.capacity=capacity;
	}

	protected boolean removeEldestEntry(final Map.Entry<String, String> eldest) {
		return size() > Cache.capacity;;
	}
}
