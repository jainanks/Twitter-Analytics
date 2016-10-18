
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProjectMapper {
	public static void main(String[] args) throws IOException, ParseException {
		// change it to system .in
		long t1 = System.currentTimeMillis();
		Map<String, Integer> sentiment = getHashMap();
		Set<String> banned = getBannedWords();

		String dateCheckString = "Sun Apr 20 00:00:00 +0000 2014";
		Date dateCheck = getDate(dateCheckString);
		// FileWriter fw = new FileWriter(new
		// File("/media/amey/Movies/test/15619/amey.txt"));
		// BufferedReader file = new BufferedReader(new FileReader(new File(
		// "/media/amey/Movies/test/15619/15619s15twitter-parta-aa")));
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));

		String line = "";
		while ((line = file.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			}
			System.out.println("line" + line);

			JSONObject object = getParsedObject(line);

			String text = (String) object.get("text");
			byte b[] = text.getBytes("utf8");

			System.out.println("bytes: " + new String(b));
			ByteBuffer ba = Charset.forName("UTF-8").encode(text);
			b = text.getBytes(Charset.forName("UTF-8"));
			System.out.println("bytes2: " + new String(b));
			// System.out.println(text);
			if (text == null)
				continue;
			String tweetId = (String) object.get("id_str");
			if (tweetId == null)
				continue;
			JSONObject user = (JSONObject) object.get("user");
			if (user == null)
				continue;
			String id = (String) user.get("id_str");
			if (id == null)
				continue;

			String textWords[] = getTextArray(text);

			int sentimentScore = getSentimentScore(textWords, sentiment);
			if (sentimentScore <= 10)
				continue;
			String createdAt = (String) object.get("created_at");
			// System.out.println(createdAt + id);
			if (createdAt == null)
				continue;
			Date created = getDate(createdAt);
			if (created.before(dateCheck))
				continue;
			text = convert(text);
			// createdAt = formatDateForQuery(createdAt);
			String censoredText = censorText(text, textWords, banned);
			// write to database
			// System.out.print(tweetId + "#$%" + id + "#$%" + createdAt + "#$%"
			// + sentimentScore + "#$%" + text
			// + "\r\n");

		}
		file.close();
	}

	private static String convert(String text) {
		char c[] = text.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < c.length; i++) {
			builder.append(unicodeEscaped(c[i]));
		}
		return builder.toString();
	}

	public static String unicodeEscaped(char ch) {
		if (ch < 0x10) {
			return "\\u000" + Integer.toHexString(ch);
		} else if (ch < 0x100) {
			return "\\u00" + Integer.toHexString(ch);
		} else if (ch < 0x1000) {
			return "\\u0" + Integer.toHexString(ch);
		}
		return "\\u" + Integer.toHexString(ch);
	}

	private static String formatDateForQuery(String createdAt) {
		// tweet_time=2014-04-20+03:24:36
		// Fri Apr 21 15:13:41 +0000 2014
		StringBuilder builder = new StringBuilder();
		String date[] = createdAt.split("+");
		// formate date

		return null;
	}

	private static Date getDate(String createdAt) {
		Date d = null;
		// Fri Apr 21 15:13:41 +0000 2014
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

	public static String censorText(String text, String textWords[], Set<String> banned) {
		StringBuilder builder = new StringBuilder(text);

		textWords = getTextArray(text);

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

	private static List<Integer> getIndexes(StringBuilder builder, String string) {

		List<Integer> indexes = new ArrayList<Integer>();
		String word = "(" + string.charAt(0) + ")" + string.substring(1);
		Pattern p = Pattern.compile("(?<=^|[^a-zA-Z\\d])" + word + "(?=$|[^a-zA-Z\\d])");
		Matcher m = p.matcher(builder.toString());
		while (m.find()) {
			indexes.add(m.start());
		}
		return indexes;
	}

	private static String censor(StringBuilder builder2, String string, String badWord) {
		String newString = cencor(badWord);
		int a = builder2.indexOf(badWord);
		builder2.replace(a, a + badWord.length(), newString);
		return builder2.toString();
	}

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

	public static Set<String> getBannedWords() {
		Set<String> banned = new HashSet<String>(400);

		try {
			URL url = new URL("https://s3.amazonaws.com/15619s15files/banned.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			// BufferedReader br = new BufferedReader(new FileReader(new
			// File("/media/amey/Movies/test/15619/banned.txt")));

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
														// numeric
		for (String string : textArray) {
			// System.out.println(string);
		}
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

	private static Map<String, Integer> getHashMap() throws NumberFormatException, IOException {

		URL url = new URL("https://s3.amazonaws.com/15619s15files/afinn.txt");
		URLConnection con = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		String line = "";
		Map<String, Integer> sentiment = new HashMap<String, Integer>();

		long t = System.currentTimeMillis();
		while ((line = br.readLine()) != null) {
			String s[] = line.split("\t");
			sentiment.put(s[0], Integer.parseInt(s[1]));

		}
		return sentiment;
	}
}
