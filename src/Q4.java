import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Q4 {
	public String processresultq4(String hashtag, String start, String end) {
		StringBuilder response = new StringBuilder("TeamCloudCrackers,117333310089,020697801606,984101504455\n");
		String result = getAllId(hashtag);
		if (result == null) {
			return response.toString();
		}
		String[] splitted = result.split(";");
		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i] == null || splitted[i].isEmpty()) {
				continue;
			}
			String[] each = splitted[i].split(",");
			// System.out.println(each[2]);
			String date = each[2].split("\\+")[0];
			if (date.compareTo(start) < 0) {
				continue;
			}
			if (date.compareTo(end) > 0) {
				continue;
			}
			response.append(splitted[i] + "\n");
			// list.add(splitted[i]);

		}
		return response.toString();
	}

	// query 4 sql query
	public static String getAllId(String hashtag) {
		
		Connection con = null;
		try {
			con = Project619Phase1Q1.getConnection();
		//	System.out.println("connection made");
			PreparedStatement ps = con.prepareStatement("SELECT * FROM tweetq4 WHERE hashkey=?");
			ps.setString(1, hashtag);
			ResultSet rs = ps.executeQuery();
			String res = "";
			while (rs.next()) {
				res = rs.getString("resultq4");
			}
			rs.close();
			ps.close();
			Project619Phase1Q1.releaseConnection(con);
			return res;
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
