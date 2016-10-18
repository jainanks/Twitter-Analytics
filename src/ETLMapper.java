import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author CloudCrackers
 */
public class ETLMapper {
	static Set<String> banned;

	public static void main(String[] args) throws IOException, ParseException {

		long t=System.currentTimeMillis();
		Map<String, Integer> sentiment = getHashMap();
		banned = new HashSet<String>();
// date before which tweets are not allowed
		String dateCheckString = "Sun Apr 20 00:00:00 +0000 2014";
		Date dateCheck = getDate(dateCheckString);
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
//start reading input and process line by line
			String line = "";
			while ((line = file.readLine()) != null) {

				if (line.isEmpty()) {
					continue;
				}
			// parse the json input
			JSONObject object = getParsedObject(line);
			if (object == null)
				continue;

			String text = (String) object.get("text");
			if (text == null|| text.isEmpty())
				continue;
			
			String tweetId = (String) object.get("id_str");
			if (tweetId == null || tweetId.isEmpty() )
				continue;
			
			JSONObject user = (JSONObject) object.get("user");
			if (user == null)
				continue;
			
			String id = (String) user.get("id_str");
			if (id == null || id.isEmpty())
				continue;

			String createdAt = (String) object.get("created_at");
			if (createdAt == null || createdAt.isEmpty())
				continue;
			Date created = getDate(createdAt);
			if (created.before(dateCheck))
				continue;
			// split the tweet text at special characters
			String textWords[] = getTextArray(text);
			//calculate sentiment score of the tweet
			int sentimentScore = getSentimentScore(textWords, sentiment);
			//censor the tweet for the words given in banned list
			String censoredText = censorText(text, textWords, banned);
			//escape the special characters twice
			censoredText = StringEscapeUtils.escapeJava(censoredText);
			censoredText = StringEscapeUtils.escapeJava(censoredText);
			// print to stdin the output of the job
			System.out.print(tweetId + "\t" + id + "\t" + created + "\t" + sentimentScore + "\t" + censoredText
					+ "\n");
		}
		file.close();
	
	}


	private static Date getDate(String createdAt) {
		Date d = null;
		final String tweetDate = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(tweetDate, Locale.ENGLISH);
		sf.setLenient(true);
		try {
			d = sf.parse(createdAt);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
/**
    * @param textwords[] array of string which is the result of separating the tweet with aplha numeric characters
    * @param Set banned : contains all the banned words which are to be searched in a tweet string
    * 
    * @return String tweet text which is now censored
    */
	public static String censorText(String text, String textWords[], Set<String> banned) {
		StringBuilder builder = new StringBuilder(text);

		//textWords = getTextArray(text);

		for (int i = 0; i < textWords.length; i++) {

			if (banned.contains(textWords[i].toLowerCase())) {

				String cleanedWord = cencor(textWords[i]);
				// compile a pattern with banned word
				List<Integer> indexList = getIndexes(builder, textWords[i]);
				replaceWithCleanWord(builder, indexList, cleanedWord);
			}
		}

		return builder.toString();
	}

	private static void replaceWithCleanWord(StringBuilder builder, List<Integer> indexList, String cleanedWord) {
		for (Integer integer : indexList) {
			builder.replace(integer, integer + cleanedWord.length(), cleanedWord);
		}
	}
/**
    * @param builder the tweet where all words will be replced with their respective censored word
    * @param String which is to be searched in tweet
    * This method returns the positions of a word in the tweet
    * @return list containing index of string present in builder
    *
    */
	private static List<Integer> getIndexes(StringBuilder builder, String string) {

		List<Integer> indexes = new ArrayList<Integer>();
		
		Pattern p = Pattern.compile("(?<![^\\W_])" + string + "(?![^\\W_])");
		
		Matcher m = p.matcher(builder.toString());

		while (m.find()) {

			indexes.add(m.start());
		}

		return indexes;
	}
/**	
    *  @param string to be censored
    * 	insert '*' in the passed string leaving start and end character as it is.
    *	@return return a censored string
    *
    */
	private static String cencor(String string) {
		char cp[] = string.toCharArray();
		int start = 1;
		int end = string.length() - 1;
		cp[0] = string.charAt(0);
		for (int i = start; i < end; i++) {
			cp[i] = '*';
		}
		cp[end] = string.charAt(end);
		return new String(cp);
	}
/**
    * Load the hasmap with the banned words after decoding them. 
    *	The list is downloaded from the URL of the file.
    * This will only be done once when the program starts
    *
    */
	public static Set<String> getBannedWords() {
		Set<String> banned = new HashSet<String>(400);

		try {
			URL url = new URL("https://s3.amazonaws.com/15619s15files/banned.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				line = ROT13(line);
				banned.add(line.toLowerCase());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return banned;
	}

	private static String ROT13(String text) {
		String s = text;
		char cp[] = new char[s.length()];
		{
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c >= 'a' && c <= 'm')
					c += 13;
				else if (c >= 'A' && c <= 'M')
					c += 13;
				else if (c >= 'n' && c <= 'z')
					c -= 13;
				else if (c >= 'N' && c <= 'Z')
					c -= 13;
				cp[i] = c;
			}
		}

		return new String(cp);
	}

	private static int getSentimentScore(String textArray[], Map<String, Integer> sentiment) {
		int score = 0;
		for (int i = 0; i < textArray.length; i++) {
			if (sentiment.containsKey(textArray[i].toLowerCase())) {
				score = score + sentiment.get(textArray[i].toLowerCase());
			}
		}
		return score;
	}

	private static String[] getTextArray(String text) {
		String s = text;
		String textArray[] = s.split("\\P{Alpha}+"); // split with non alpha
		return textArray;
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
 /**
    * Load the hashmap with all the words and their sentiment score
    *	Reads the file form url provided and loads the static hashmap
    * This will only be done once when the program starts
    *
    */
	private static Map<String, Integer> getHashMap() throws NumberFormatException, IOException {

		URL url = new URL("https://s3.amazonaws.com/15619s15files/afinn.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		String line = "";
		Map<String, Integer> sentiment = new HashMap<String, Integer>();

		
		while ((line = br.readLine()) != null) {
			String s[] = line.split("\t");
			sentiment.put(s[0], Integer.parseInt(s[1]));

		}
		return sentiment;
	}
}
