import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringEscapeUtils;

public class Q2 {
	// query2 sql
	public String processRequestq2(String userId, String timeStamp) {
		/*
		 * if(cache2.containsKey(userId+timeStamp)) { return
		 * cache2.get(userId+timeStamp); }
		 */
		Date d = null;
		// Fri Apr 21 15:13:41 +0000 2014
		final String tweetDate = "yyyy-MM-dd HH:mm:ss";// EEE MMM dd HH:mm:ss
														// ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(tweetDate, Locale.ENGLISH);
		sf.setLenient(true);
		try {
			d = sf.parse(timeStamp);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sfd = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		String date = sfd.format(d);
	//	System.out.println("query:" + userId + date);
		String result = getResult(userId, date);
	//	System.out.println("result::" + result);
		String response = "";
		if (result == null || result.equals("")) {
			response = "Invalid Message";
		}

		response = result;
		// cache2.put(userId+timeStamp, response);
		return response;

	}

	// query2 sql query method
	public static String getResult(String userId, String timeStamp) {
		ArrayList<String> list = new ArrayList<String>();
		list = getTweets(userId, timeStamp);
		String response = "TeamCloudCrackers,117333310089,020697801606,984101504455\n";
		if (list != null) {
			response = response + list.get(0);
		}
		return response;
	}

	// This method is used to connect to database and get results
	public static ArrayList<String> getTweets(String userId, String timeStamp) {
		Connection con = null;
		try {
			con = Project619Phase1Q1.getConnection();
		//	System.out.println(userId + " " + timeStamp);
			PreparedStatement pst = con.prepareStatement("SELECT * FROM Tweets WHERE userId=?");
			pst.setString(1, userId + timeStamp);
			ResultSet rs = pst.executeQuery();
			ArrayList<String> list = new ArrayList<String>();
			while (rs.next()) {
				String text = StringEscapeUtils.unescapeJava(rs.getString("results"));
				// System.out.println("text:;" + text);
				list.add(text);
			}
			rs.close();
			pst.close();
			Project619Phase1Q1.releaseConnection(con);
			return list;

		} catch (Exception e) {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e2) { /* ignore */
			}
			return null;
		}

	}
}
