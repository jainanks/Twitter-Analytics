import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Q1 {
	private final static String x = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";

	// query1 decode method
	private  String deCode(String encoded, String key) {
		//System.out.println(encoded + ":" + key);
		int n = (int) Math.sqrt(encoded.length());
		String decoded = "";
		char[][] arr = new char[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				arr[i][j] = encoded.charAt(n * i + j);
			}
		}
		int m = 0;
		while (m <= (n / 2)) {
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

		// calculate the minikey z
		BigInteger bigIn = new BigInteger(key);
		long y = bigIn.divide(new BigInteger(x)).longValue();
		int z = (int) (y % 25 + 1);
		//System.out.println("z:" + z);

		// int[] minus=new int[decoded.length()];
		String result = "";
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

		return result;
	}

	// process request for query1
	public  String processRequestq1(String encoded, String key) {
		/*
		 * if(cache1.containsKey(key+encoded)) { return cache1.get(key+encoded);
		 * }
		 */
		String result = deCode(encoded, key);
		if (result == null) {
			return "Invalid Message";
		}
		Date date = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String todayDate = formatDate.format(date);
		String response = "TeamCloudCrackers,117333310089,117333310089,117333310089\n" + todayDate + "\n" + result
				+ "\n";
		// cache1.put(key+encoded, response);
		return response;

	}

}
