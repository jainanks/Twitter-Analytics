import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
*
* @author CloudCrackers
*/
public class Q2FrontEndHBase {
	
	 /**
	    * . 
	    *
	    * @param  userId from the request
	    * @param  timeStamp from the request
	    * @return   Result list to be displayed 
	    */
	public static String getResult(String userId, String timeStamp)
			throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		list = getTweets(userId, timeStamp);

		String response = "TeamCloudCrackers,117333310089,020697801606,984101504455\n";

		for (int i = 0; i < list.size(); i++) {

			response = response + list.get(i);

		}
		return response;
	}

	//Static variables which will store objects which can be used throughout the program
	static Configuration conf = null;
	static HTable table = null;
	
    //Initializes the connection with the backend and the Table
	//We need to do this just once
	public static void init() {
		conf = HBaseConfiguration.create();
		//Set properties in hbase-site.xml
		conf.set("hbase.zookeeper.quorum", "ip-172-31-60-75.ec2.internal");
		conf.set("hbase.regionserver.handler.count", "50");
		conf.set("hbase.regionserver.global.memstore.size", "0.2");
		conf.set("hfile.block.cache.size", "0.6");
		conf.set("hbase.hregion.memstore.mslab.enabled", "50");
		conf.set("hbase.zookeeper.property.maxClientCnxns", "100");

		try {
			//Create an object of table which will allow get()
			table = new HTable(conf, "test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//Method to GET data from HBase tables.
	public static ArrayList<String> getTweets(String userId, String timeStamp)
			throws IOException {
		
        //Create a string query which is the combination of userId and timestamp
		String query = userId + timeStamp;
		ArrayList<String> list = new ArrayList<String>();
		if (query.isEmpty())
			return list;
		
		//Call get for the rowId using the query string
		Get get = new Get(Bytes.toBytes(query));
		
        //Get the result from the table for the get object
		Result result = table.get(get);

		if (result.isEmpty())
			return list;
        
		//Convert the byte type result into string
		byte[] value = result.getValue(Bytes.toBytes("f"), Bytes.toBytes(""));
		String valuestr = Bytes.toString(value);
		//Escape characters two times to get the original form of data
		String str = StringEscapeUtils.unescapeJava(valuestr);
		String str1 = StringEscapeUtils.unescapeJava(str);
		list.add(str1);

		table.flushCommits();

		return list;

	} 
	
    //Convert the date  from the query format to the database format
	public static String processRequest(String userId, String timeStamp)
			throws IOException {
		Date d = null;

		final String tweetDate = "yyyy-MM-dd HH:mm:ss";
													
		SimpleDateFormat sf = new SimpleDateFormat(tweetDate, Locale.ENGLISH);
		sf.setLenient(true);
		try {
			d = sf.parse(timeStamp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat sfd = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss z yyyy");
		String date = sfd.format(d);

	
		String result = getResult(userId, date);
		String response = "";
		if (result == null || result.equals("")) {
			response = "Invalid Message";
		}

		response = result;
		return response;

	}

	public static void main(final String[] args) throws FileNotFoundException {
		PrintStream console = System.out;

		File file = new File("out.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
		System.out.println("hello");
		init();
		Undertow server = Undertow
				.builder()
				.addHttpListener(80, "ec2-54-175-31-42.compute-1.amazonaws.com")
				.setHandler(new HttpHandler() {

					@Override
					public void handleRequest(final HttpServerExchange exchange)
							throws Exception {
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
								"text/plain");

						Map<String, Deque<String>> map = exchange
								.getQueryParameters();
						if (exchange.getRequestURI().equals("/q2")) {
							String userId = map.get("userid").getFirst();
							System.out.println("userId=" + userId);
							String timeStamp = map.get("tweet_time").getFirst();

							String output = processRequest(userId, timeStamp);
							exchange.getResponseSender().send(output);
						}

					}
				}).build();
		server.start();
	}
}	
