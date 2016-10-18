import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author CloudCrackers
 * Date- 04/03/2015
 *
 */

public class q5Mapper {

	public static void main(String[] args) throws IOException, ParseException {
		//FileReader file= new FileReader(new File("C:/Users/aayush/Desktop/input.txt"));
		BufferedReader bfr= new BufferedReader(new InputStreamReader(System.in));
		String line="";
		while((line=bfr.readLine())!=null) {
			
			if(line==null || line.isEmpty()) {
				continue;
			}
			
			//Json parser to parse the tweets
			JSONParser parser= new JSONParser();
			JSONObject obj=(JSONObject) parser.parse(line);
					
			//get tweet ID
			String tweetId = (String) obj.get("id_str");
			if (tweetId == null || tweetId.isEmpty() )
				continue;
			JSONObject user = (JSONObject) obj.get("user");
			if (user == null)
				continue;
			//get userId
			String userid = (String) user.get("id_str");
			if (userid == null || userid.isEmpty())
				continue;
			String createdAt = (String) obj.get("created_at");
			if (createdAt == null || createdAt.isEmpty())
				continue;
			Long followers= (Long)user.get("followers_count");
			Long friends= (Long) user.get("friends_count");
			
			String formateddate= changedate(createdAt);
			
			System.out.println(userid+":"+formateddate+"\t"+tweetId+"\t"+followers+"\t"+friends);
		}
	}
	
	//this method is used to get the date in required format
	private static String changedate(String date) {
		Date d=null;
		final String format="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sdf= new SimpleDateFormat(format);
		try {
			d= sdf.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(System.err);
		}
		SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd");
		String res= sf.format(d);
		return res;
	}
}
