import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author CloudCrackers
 * Date- 04/03/2015
 *
 */
public class ETLMapperQ6 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";

		while ((line = br.readLine()) != null) {
			
			if (line.isEmpty()) {
				continue;
			}

			//parse the JSON
			JSONObject object = getParsedObject(line);
			if (object == null)
				continue;
			//get text
			String text = (String) object.get("text");

			if (text == null || text.isEmpty())
				continue;

			//get TweetId
			String tweetId = (String) object.get("id_str");
			if (tweetId == null || tweetId.isEmpty())
				continue;
			JSONObject user = (JSONObject) object.get("user");
			if (user == null)
				continue;
			//get UserId
			String userId = (String) user.get("id_str");

			if (userId == null || userId.isEmpty())
				continue;
			JSONObject place = (JSONObject) object.get("place");
			if (place == null || (place.get("name") == null || ((String) place.get("name")).isEmpty())) {
				System.out.println(userId + "\t" + 0);
				continue;
			} else {
				System.out.println(userId + "\t" + 1);
			}
		}

	}

	private static JSONObject getParsedObject(String line) {
		JSONParser parser = new JSONParser();
		JSONObject object = null;
		try {
			object = (JSONObject) parser.parse(line);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return object;
	}

}
