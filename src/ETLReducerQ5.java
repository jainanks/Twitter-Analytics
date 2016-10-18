import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author CloudCrackers
 * Date- 04/03/2015
 *
 */

public class q5Reducer {

	public static void main(String[] args) throws FileNotFoundException {
		//FileReader file1= new FileReader(new File("C:/Users/aayush/Desktop/input1.txt"));
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		
		String line = "";
		String key="";
		String prevKey="";
		long max_followers=0;
		long max_friends=0;
		int tweet_count=0;
		try {
			while ((line = file.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				String[] each=line.split("\t");
				key= each[0];
				if(key.equals(prevKey)) {
					max_followers=Math.max(max_followers, Long.parseLong(each[2]));
					max_friends= Math.max(max_friends, Long.parseLong(each[3]));
					tweet_count++;
				} else {
					if(!prevKey.isEmpty()) {
						System.out.println(prevKey+"\t"+tweet_count+"\t"+max_followers+"\t"+max_friends);
					}
					max_followers=Long.parseLong(each[2]);
					max_friends=Long.parseLong(each[3]);
					tweet_count=1;
					prevKey=key;
				}
			}
			if(key.equals(prevKey)) {
				System.out.println(key+"\t"+tweet_count+"\t"+max_followers+"\t"+max_friends);
			} else {
				System.out.println(key+"\t"+tweet_count+"\t"+max_followers+"\t"+max_friends);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
