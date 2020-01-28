import java.util.*;
import java.io.*;

class FoldGenerator {

	
	static HashMap<Integer, ArrayList<String>> Data;
	static ArrayList<String> yesList; 
	static ArrayList<String> noList; 
	
	//static HashMap<Integer, ArrayList<Double>> meanAndStndDeviationNo;

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	public static void main(String[] args) {

		File file = new File(args[0]);
		boolean isFirstLine = true; 
		int totalYes = 0; 
		int totalNo = 0; 
		int numOfHeaders = 0; 
		int numOfLines = 0; 
		int countExtraYes = 0; 
		Data = new HashMap<Integer, ArrayList<String>>(); 
		yesList = new ArrayList<String>(); 
		noList = new ArrayList<String>(); 
		int pimaSize = 0; 
		try{
			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNext()) {
				pimaSize++; 
				String tmpString = fileScanner.nextLine(); 
				List<String> line = parseLine(tmpString);
				//System.out.print(fileScanner.hasNext());

				//For any number of attributes including class
				numOfHeaders = line.size();
				numOfLines++;



				//If it is the first line we need first put our keys
				if(isFirstLine){
					for (int i = 0; i < numOfHeaders; i++){
						ArrayList<String> arrayListPointer = new ArrayList<String>(); 
						Data.put(i, arrayListPointer); 
					}
					isFirstLine = false; 
				}

				//Read values for each attribute
				for (int i = 0; i < numOfHeaders; i++){
					Data.get(i).add(line.get(i));
					//System.out.print(line.get(i)+ " ");
				}
				//System.out.println(""); 
				if(line.get(numOfHeaders-1).equals("yes")){
					yesList.add(tmpString); 
				}
				else if(line.get(numOfHeaders-1).equals("no")){
					noList.add(tmpString); 
				}

			}
			fileScanner.close(); 
		}
		catch(Exception e){

		}


		System.out.println("pimaSize " + pimaSize); 
		for(int i = 0; i < numOfLines; i++){
			for(int j = 0; j < numOfHeaders; j++){
				ArrayList<String> tmpList = Data.get(j);
				//System.out.print(tmpList.get(i)+ " "); 
			}
			//System.out.println(""); 
		}


		for(int i = 0; i < numOfLines; i ++){
			for(int j = 0; j < numOfHeaders; j++){
				ArrayList<String> tmpList = Data.get(j);
				//System.out.print(tmpList.get(i)+ " ");

				//Counting yes and no
				if(tmpList.get(i).equals("yes")){
					totalYes++; 
				}
				else if(tmpList.get(i).equals("no")){
					totalNo++; 
				}

			}
			//System.out.println("");
		}
		System.out.println("totalYes " + totalYes + " totalNo " + totalNo);
		System.out.println("yesList " + yesList.size() + " noList " + noList.size()); 
		
		
		try(PrintWriter writer = new PrintWriter(new File("pima-folds.csv"))){
			int n = 0; 
			int y = 0; 
			int nCount = 50; 
			int yCount = 26; 
		
			int total = 0; 
		
			for(int i = 0; i < 10; i++){
				
				writer.write("fold"+(i+1) + "\n");
				
				while(n != nCount){
					//System.out.println("n " + n);
					writer.write(noList.get(n) + "\n");
					n++;
					
					total++; 
					
				}
				// System.out.println("n " + n); 
				// System.out.println("nCount " + (nCount));
				
				while(y != yCount){
					System.out.println("y " + y); 
					writer.write(yesList.get(y) + "\n");
					y++;
					
					total++;  
				}
				// System.out.println("y " + y);
				// System.out.println("yCount " + (yCount));
				
				if(countExtraYes < 8){
					System.out.println("y " + y);
					writer.write(yesList.get(y) + "\n");
					y++;
					yCount++; 
					countExtraYes++; 
					
					
					total++; 
					
					//System.out.println("write");  
				}
			
				nCount += 50; 
				yCount += 26; 
				
				if(i != 9){
					writer.write("\n");
				}
				else{
					
				}
			}
			
			System.out.println("total " + total); 
			System.out.println("countExtraYes " + countExtraYes); 
			System.out.println("nCount " + (nCount - 50)); 
			System.out.println("yCount " + (yCount - 26));
			System.out.println("n " + n); 
			System.out.println("y " + y);
		}
		catch(FileNotFoundException e){
			
		}
	}


	///////////////////////////////////////////////////////////////////////////////////////////////

	public static List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators) {
		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		//if empty, return!
		if (cvsLine == null && cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					//Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					//Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					//double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				} else if (ch == '\r') {
					//ignore LF characters
					continue;
				} else if (ch == '\n') {
					//the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}






}











































