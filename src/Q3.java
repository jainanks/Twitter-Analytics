

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Q3 {
	static Cache cache3= new Cache();
	public String processRequestq3(String id) {

		if (cache3.containsKey(id)) {
			return "TeamCloudCrackers,117333310089,020697801606,984101504455\n" + cache3.get(id);
		}
		Connection conn = null;
		String result = "";
		try {
			conn = Project619Phase1Q1.getConnection();
			PreparedStatement ps = conn.prepareStatement("select results from q3 where id=? ");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("results");
				long ts = System.currentTimeMillis();

				// result=StringEscapeUtils.escapeJava(result);

				// System.out.println("name retrieved " + result);
				// result=StringEscapeUtils.unescapeJava(result);
			//	System.out.println("time for excaping" + (System.currentTimeMillis() - ts));
			} else {
				return "unauthorized";
			}
			rs.close();
			ps.close();
			Project619Phase1Q1.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (MyDAOException e) {
			e.printStackTrace();
		}
		cache3.put(id, result);
		return "TeamCloudCrackers,117333310089,020697801606,984101504455\n" + result;
	}

}
