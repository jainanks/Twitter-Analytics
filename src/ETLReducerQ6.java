import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author CloudCrackers Date- 04/03/2015
 *
 */
public class ETLReducerQ6 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		String prevKey = "";
		String key = "";
		String prevLine = "";
		String value = "";
		String prevValue = "";
		boolean locationFound = false;
		// read each line
		while ((line = br.readLine()) != null) {
			String keyValue[] = line.split("\t");
			key = keyValue[0];
			value = keyValue[1];

			if (line.isEmpty()) {
				continue;
			}
			// check previous key
			if (prevKey.equals(key)) {
				if (prevValue.equals("1")) {
					locationFound = true;
				}
				prevValue = value;
				prevKey = key;
			} else {

				if (prevKey.isEmpty()) {
					prevKey = key;
					prevValue = value;
					continue;
				}
				if (prevValue.equals("1")) {
					locationFound = true;
				}
				if (!locationFound) {
					System.out.println(prevKey);
				}
				locationFound = false;
				prevKey = key;
				prevValue = value;

			}
		}
		if (prevKey.equals(key)) {
			if (value.equals("1"))
				locationFound = true;
			if (!locationFound) {
				System.out.println(key);
			}
		} else {
			if (value.equals("1"))
				locationFound = true;
			if (!locationFound) {
				System.out.println(key);
			}
		}
	}
}
