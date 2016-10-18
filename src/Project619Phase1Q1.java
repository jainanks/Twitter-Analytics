import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Project619Phase1Q1 extends HttpServlet {

	String x = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	HashMap<String,HashMap<String, String>> map= new HashMap<String,HashMap<String,String>>();
	HashMap<String, String> mapper= new HashMap<String, String>();
	
	private String deCode(String encoded, String key) {
		String result = "";
		if(map.containsKey(key)) {
			if(mapper.containsKey(encoded)) {
				result= mapper.get(encoded);
			}
		}
		if(result==null || result.equals("")) {
			int n = (int) Math.sqrt(encoded.length());
			String decoded = "";		
			
			char[][] arr = new char[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					arr[i][j] = encoded.charAt(n * i + j);
				}
			}
			int m = 0;
			int total= (int)Math.sqrt(encoded.length())+2;
			//System.out.println("sqrt: "+Math.sqrt(n));
			while (m <= total) {
				for (int j = 0 + m; j < n - m; j++) {
					decoded = decoded + arr[m][j];
				}
				for (int i = 1 + m; i < n - m; i++) {
					decoded = decoded + arr[i][n - 1 - m];
				}
				for (int j = n - 2 - m; j >= 0 + m; j--) {
					decoded = decoded + arr[n - 1 - m][j];
				}
				for (int i = n - 2 - m; i >= 1 + m; i--) {
					decoded = decoded + arr[i][m];
				}
				m = m + 1;
			}
			//System.out.println(decoded);
	
			// calculate the minikey z
			BigInteger bigIn = new BigInteger(key);
			int y = bigIn.divide(new BigInteger(x)).intValue();
			int z = y % 25 + 1;
			//System.out.println(z);
	
			// int[] minus=new int[decoded.length()];
			
			for (int i = 0; i < decoded.length(); i++) {
				int minus = decoded.charAt(i) - z;
				if (minus < 65) {
					int check = decoded.charAt(i) - 65;
					check = z - check;
					minus = 91 - check;
				}
				char intermediate = (char) minus;
				result = result + intermediate;
			}
			
			
			mapper.put(encoded, result);
			map.put(key, mapper);
		}
		Date date = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String todayDate = formatDate.format(date);
		
		String response = "TeamCloudCrackers,117333310089,020697801606,984101504455\n"
				+ todayDate + "\n" + result;
		//System.out.println(response);
		
		return response;
	}

	public String processRequest(String encoded, String key,
			HttpServletRequest req) {
		String result = deCode(encoded, key);
		String response="";
		if (result == null || result.equals("")) {
			response= "Invalid Message";
		}
		/*Date date = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String todayDate = formatDate.format(date);*/
		response =  result;
		return response;
		
	}

	@Override


	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String key = req.getParameter("key");//"306063896731552281713201727176392168770237379582172677299123272033941091616817696059536783089054693601";//req.getParameter("key");
		String message = req.getParameter("message");//"URYEXYBJB";//req.getParameter("message");
		resp.setContentType("text/plain");
		
		String output=processRequest(message, key, req);
		PrintWriter out = resp.getWriter();
		out.println(output);
		//out.println("HELLO WORLD");

	}
}
