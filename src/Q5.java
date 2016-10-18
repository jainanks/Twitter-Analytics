import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class Q5 {
	  String processRequestq5(String[] users, String start, String end) {

		StringBuilder response = new StringBuilder("TeamCloudCrackers,117333310089,020697801606,984101504455\n");
		final HashMap<String, Long> map = new HashMap<String, Long>();
		ArrayList<String> al = new ArrayList<String>();
		for (String s : users) {

			String first = s + ":" + start;
			String last = s + ":" + end;

			String res = getOutput(first, last);

			String[] each = res.split(" ");
			Long followers = Long.parseLong(each[0]);
			Long frnds = Long.parseLong(each[1]);
			int tweets = Integer.parseInt(each[2]);
			Long points = tweets + 3 * frnds + 5 * followers;
			map.put(s, points);
			al.add(s);
		}
		long t=System.currentTimeMillis();
		Collections.sort(al, new Comparator<String>() {
			public int compare(String s1, String s2) {
				long diff = map.get(s2) - map.get(s1);
				if (diff != 0)
					return (int) diff;
				else {
					long diff1 = Long.parseLong(s2) - Long.parseLong(s1);
					if (diff1 > 0)
						return 1;
					else
						return -1;
				}
			}
		});

		for (String s : al) {
			response.append(s + "," + map.get(s) + "\n");
		}

		return response.toString();
	}

	private static String getOutput(String start, String end) {
		Connection con = null;
		try {
			con =Project619Phase1Q1.getConnection();
		
			long t=System.currentTimeMillis();
			PreparedStatement ps = con
					.prepareStatement("SELECT MAX(follower) AS Followers, MAX(friend) AS Frnd, SUM(tweet) AS Tweet FROM q5 WHERE userId between ? AND ?");
			ps.setString(1, start);
			ps.setString(2, end);
			ResultSet rs = ps.executeQuery();
		
			String foll = "";
			String frnd = "";
			String tweet = "";
			while (rs.next()) {

				foll = rs.getString("Followers");
				frnd = rs.getString("Frnd");
				tweet = rs.getString("Tweet");
			}
			rs.close();
			ps.close();
			Project619Phase1Q1.releaseConnection(con);
			return foll + " " + frnd + " " + tweet;
		} catch (Exception e) {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e2) { /* ignore */
			}
			return null;
			// throw new MyDAOException(e);
		}
	}

}
