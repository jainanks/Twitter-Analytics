import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
/*
*
* combines records for same userid and tweet time to be stored in hbase as key value pair
*/
public class HbaseReducer2 {
	public static void main(String[] args) {
		String prevLine = "";
		String prevKey = "";
		String key = "";
		try {
			BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));

			String line = "";
			StringBuilder builder = new StringBuilder();
			while ((line = stream.readLine()) != null) {

				if (line.isEmpty()) {
					continue;
				}
				String all[] = line.split("\t");
				key = all[0];
				if (prevKey.equals(key)) {
					builder.append(all[1]).append(":").append(all[2]).append(":").append(all[3]+"\\"+"\\n");
					prevLine = line;
				} else {
					if (!prevKey.isEmpty()) {
						System.out.println(prevKey + "\t" + builder.toString());

					}
					builder = new StringBuilder();
					builder.append(all[1]).append(":").append(all[2]).append(":").append(all[3]+"\\"+"\\n");
					prevLine = line;
					prevKey = key;
				}
			}
			if (prevKey.equals(key) && ((!prevKey.isEmpty())) && !(key.isEmpty())) {
				System.out.println(key + "\t" + builder.toString());

			} else if (!prevKey.isEmpty()) {
				System.out.println(key + "\t" + builder.toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}