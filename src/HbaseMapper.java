import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
/*
* takes input in the form of which is created by first etl and converts it in the form to be inserted in hbase
*/
public class HbaseMapper {
	public static void main(String[] args) throws FileNotFoundException {
		 BufferedReader file = new BufferedReader(new
		 InputStreamReader(System.in));

		String line = "";
		try {
			while ((line = file.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] all = line.split("\t");
				
				// create key for hbase table
				String key = all[1] + all[2];
				String tweetId = all[0];
				String score = all[3];
				String text = all[4];
				System.out.print(key + "\t" + tweetId + "\t" + score + "\t" + text + "\n");
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
