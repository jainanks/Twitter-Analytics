
	 // package preoject1_2;

	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Comparator;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Scanner;
	import java.util.Set;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;


	public class MapperWiki {

		public static void main(String[] args) {
			try {
				long s = System.currentTimeMillis();
				// String asd = "bayesfactor";
				// System.out.println("time start " + s);
				// System.out.println(asd.matches("[a-z]*"));
				// System.out.println("ABC".matches("[A-Z]*"));
				
				String line;
				Pattern p = Pattern.compile("[a-z]*");
				Matcher m;
				File f = new File("d:/test/files/Files.txt"); // code for test purpose
//				Scanner filescan = new Scanner(new FileInputStream(f)); // code
				Scanner filescan=new Scanner(System.in);
				String filename;
				// for
				// test
				// purpose
				// System.getenv("mapreduce_map_input_file"); // actual code to be
				// used on map reduce machine
				while (filescan.hasNext()) { // test code to
					filename = filescan.nextLine();			// simulate	// working with	// different	// files
					// filename=System.getenv("mapreduce_map_input_file");
					Scanner scan = new Scanner(new FileReader(new File("d:/test/files/"+filename)));
					String fileNameParts[]=filename.split("-");
					filename=fileNameParts[1];
					boolean enFound=false;
//					while ((line = scan.nextLine()) != null) {
					while(scan.hasNext()){
						line=scan.nextLine();

						if (line.startsWith("en ")) {
							String arr[] = line.split("\\s+");
							//testcode[]
							enFound=true;
							// for (String string : arr) {
							// 	System.out.println("s "+string);
							// }
							//testcode ends
							// System.out.println("length of split is "+);
							if(arr.length==4 && !arr[1].isEmpty()) // eliminate malformed entries
						
							processLine(arr[1] +"\t"+filename+ "\t" + arr[2]);
						
						} else {
							//assuming the input file is sorted
							if(enFound){//119285 //with this condition
								break;
							}else   //143106 without this condition.
							// ignore line and continue
							continue;
						}
					}
				}
			//	System.out.println("time taken "+ (System.currentTimeMillis()-s));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		}

		static String excludeTitle[] = { "Media:", "Special:", "Talk:", "User:",
				"User_talk:", "Project:", "Project_talk:", "File:", "File_talk:",
				"MediaWiki:", "MediaWiki_talk:", "Template:", "Template_talk:",
				"Help:", "Help_talk:", "Category:", "Category_talk:", "Portal:",
				"Wikipedia:", "Wikipedia_talk:" };
		private static Set<String> excludeTitles = new HashSet<String>(
				Arrays.asList(excludeTitle));
		// static List<String> records = new ArrayList<String>();

		private static void processLine(String line) {
			String arr[] = line.split("\t");
			// look if it start with a capital
			if (arr[0].matches("[a-z].*") || arr[0].endsWith(".jpg")
					|| arr[0].endsWith(".JPG") || arr[0].endsWith(".txt")
					|| arr[0].endsWith(".ico") || arr[0].endsWith(".gif")
					|| arr[0].endsWith(".GIF") || arr[0].endsWith(".png")
					|| arr[0].endsWith(".PNG")) {
				return;// no need to include the line
			} else if (arr[0].equals("404_error/") || arr[0].equals("Main_Page")
					|| arr[0].equals("Hypertext_Transfer_Protocol")
					|| arr[0].equals("Search")) {
				return;
			} else if (arr[0].contains(":")
					&& excludeTitles.contains(arr[0].split(":")[0] + ":")) {
				// System.out.println(arr[0].split(":")[0]);
				// System.out.println(arr[0]);

				return;
			}
			// records.add(line);
			System.out.println(line);
			return;

		}

	}

	// class RecordCompare implements Comparator<String> {

//	 	public int compare(String o1, String o2) {
//	 		return Integer.parseInt(o1.split("\t")[1].trim())
//	 				- Integer.parseInt(o2.split("\t")[1].trim());

//	 	}
	// }

