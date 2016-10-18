import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringEscapeUtils;
/**
    * @authro CloudCrackers
    *	
    * this removes duplicate entries
    *
    */
public class ETLReducer {
	public static void main(String[] args) throws FileNotFoundException {
		 BufferedReader file = new BufferedReader(new
		 InputStreamReader(System.in));
		
		String line = "";
		String prevLine = "";
		String prevKey="";
		String key="";
		try {
			while ((line = file.readLine()) != null) {

				if (line.trim().isEmpty())
					continue;
				String keyvalue[]=line.split("\t");
				key=keyvalue[0];
				if (prevKey.equals(key))
					continue;
				else {
					prevLine = line;
					prevKey=key;
					System.out.println(prevLine);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
