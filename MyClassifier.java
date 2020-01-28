import java.util.*;
import java.io.*;

class MyClassifier {

	//Data structures
	static ArrayList<String[]> train_ex;
	static ArrayList<String[]> test_ex;
	static ArrayList<TreeMap<Double, String>> sorted_testing;

	//Counters
	static int num_train = 0;
	static int num_test = 0;
	static int num_attributes = 0;

	static int neighbours; //for kNN

	static HashMap<Integer, ArrayList<String>> testData;
	static HashMap<Integer, ArrayList<Double>> meanAndStndDev;
	//static HashMap<Integer, ArrayList<Double>> meanAndStndDeviationNo;

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	public static void main(String[] args) {
		
		//Initialise arrays
		train_ex = new ArrayList<String[]>();
		test_ex = new ArrayList<String[]>();
		sorted_testing = new ArrayList<TreeMap<Double, String>>();


		File train_file = new File(args[0]);
		File test_file = new File(args[1]);

		char[] algorithm = args[2].toCharArray();
		int length = algorithm.length;
		double totalYes = 0;
		double totalNo = 0; 
		double totalPYes = 0; 
		double totalPNo = 0; 

		int numOfLines = 0; 
		int numOfHeaders = 0; 

		//NB
		if(algorithm[0] == 'N' && algorithm[1] == 'B') {


			//System.out.println("executing NB");
			//////////////////////////////////////////////////////////////
			boolean isFirstLine = true; 

			testData = new HashMap<Integer, ArrayList<String>>(); 

			try{
				Scanner train_scan = new Scanner(train_file);
				while (train_scan.hasNext()) {

					List<String> line = parseLine(train_scan.nextLine());

					//For any number of attributes including class
					numOfHeaders = line.size();
					numOfLines++;



					//If it is the first line we need first put our keys
					if(isFirstLine){
						for (int i = 0; i < numOfHeaders; i++){
							ArrayList<String> arrayListPointer = new ArrayList<String>(); 
							testData.put(i, arrayListPointer); 
						}
						isFirstLine = false; 
					}

					//Read values for each attribute
					for (int i = 0; i < numOfHeaders; i++){
						testData.get(i).add(line.get(i)); 
					}

				}
				train_scan.close(); 
			}
			catch(Exception e){

			}
			/////////////////////////////////////////////////////////////////////////////////////////

			// Calculations (training)
			meanAndStndDev = new HashMap<Integer, ArrayList<Double>>(); 

			double yesSum = 0; 
			double noSum = 0; 
			double diffYesSum = 0; 
			double diffNoSum = 0; 

			int yesCounter = 0; 
			int noCounter = 0; 

			for(int i = 0; i < numOfHeaders-1; i++){
				ArrayList<String> tmpListNumericValue = testData.get(i);
				ArrayList<String> tmpListClassValue = testData.get(numOfHeaders-1);

				// Calculate mean
				for(int j = 0; j < tmpListNumericValue.size(); j++){

					if(tmpListClassValue.get(j).equals("yes")){
						yesSum+= Double.valueOf(tmpListNumericValue.get(j)); 
						yesCounter++; 
					}
					else if(tmpListClassValue.get(j).equals("no")){
						noSum+= Double.valueOf(tmpListNumericValue.get(j)); 
						noCounter++; 
					}

				}
				double meanYes = yesSum/yesCounter; 
				double meanNo = noSum/noCounter; 
				// System.out.println("meanYes "  +meanYes); 
				// System.out.println("meanNo "  +meanNo); 

				// Calculate std
				for(int j = 0; j < tmpListNumericValue.size(); j++){
					Double x  = Double.valueOf(tmpListNumericValue.get(j));


					if(tmpListClassValue.get(j).equals("yes")){
						diffYesSum += Math.pow(x - meanYes, 2); 
					}
					else if(tmpListClassValue.get(j).equals("no")){
						diffNoSum += Math.pow(x - meanNo, 2); 
					}
				}
				double stdYes = Math.sqrt((diffYesSum/(yesCounter-1))); 
				double stdNo = Math.sqrt((diffNoSum/(noCounter-1))); 

				if(yesCounter == 1){
					stdYes = 0; 
				}
				if(noCounter == 1){
					stdNo = 0; 
				}
				//System.out.println("stdYes "  +stdYes); 
				//System.out.println("stdNo "  +stdNo); 

				// store. 

				if(meanAndStndDev.get(i) == null){
					meanAndStndDev.put(i, new ArrayList<Double>());
				}
				meanAndStndDev.get(i).add(0, meanYes);
				meanAndStndDev.get(i).add(1, meanNo);
				meanAndStndDev.get(i).add(2, stdYes);
				meanAndStndDev.get(i).add(3, stdNo);

				yesSum = 0; 
				noSum = 0; 
				diffYesSum = 0; 
				diffNoSum = 0; 

				yesCounter = 0; 
				noCounter = 0; 

			}
			/////////////////////////////////////////////////////////////////////////////////////////
			for(int i = 0; i < numOfHeaders -1; i++){
				for(int j = 0; j < meanAndStndDev.get(i).size(); j++){
					//System.out.println("value  "+ j + " "  + meanAndStndDev.get(i).get(j));
				}
				//System.out.println("");
			}
			/////////////////////////////////////////////////////////////////////////////////////////
			for(int i = 0; i < numOfLines; i ++){
				for(int j = 0; j < numOfHeaders; j++){
					ArrayList<String> tmpList = testData.get(j);
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
			//////////////////////////////////////////////////////////////////////////////////////////
			// System.out.println("totalYes " + totalYes);
			// System.out.println("totalNo " + totalNo);
			// System.out.println("");

			totalPYes = totalYes/numOfLines; 
			totalPNo = totalNo/numOfLines; 

			// System.out.println("totalPYes " + totalPYes);
			// System.out.println("totalPNo " + totalPNo);
			// System.out.println("");

			// 				System.out.println("number of attributes " + (numOfHeaders - 1));
			///////////////////////////////////////////////////////////////////////////////////////////
			//System.out.println("///////////////////////////////////////////////////////////////////////////////////////////");
			int numOfHeadersTest = 0; 
			ArrayList<Double> yesValues = new ArrayList<Double>(); 
			ArrayList<Double> noValues = new ArrayList<Double>();
			try{
				Scanner test_scan = new Scanner(test_file);
				while (test_scan.hasNext()) {

					List<String> line = parseLine(test_scan.nextLine());
					numOfHeadersTest = line.size(); 
					//meanAndStndDev

					//System.out.println("line is " + line.toString()); 

					for (int i = 0; i < numOfHeadersTest; i++){
						//System.out.print(line.get(i)+ " "); 


						double value = Double.parseDouble(line.get(i)); 

						// System.out.println("");
						// System.out.println("value is " + value);


						double meanYes = meanAndStndDev.get(i).get(0); 
						double meanNo = meanAndStndDev.get(i).get(1); 
						double stdYes = meanAndStndDev.get(i).get(2);
						double stdNo = meanAndStndDev.get(i).get(3);

						// System.out.println("meanYes is " + meanYes);
						// System.out.println("meanNo is " + meanNo);
						// System.out.println("stdYes is " + stdYes);
						// System.out.println("stdNo is " + stdNo);

						double y = 1/(stdYes * Math.sqrt(2*Math.PI));
						double x = -1 * Math.pow((value - meanYes), 2) / (2 * Math.pow(stdYes, 2));
						double z = Math.pow(Math.E, x);
						double yesResult = y*z; 
						if(stdYes == 0){
							yesResult = 1;
						}
						yesValues.add(yesResult); 

						// System.out.println("");
						// System.out.println("yesResult " + yesResult);

						y = 1/(stdNo * Math.sqrt(2*Math.PI));
						x = -1 * Math.pow((value - meanNo), 2) / (2 * Math.pow(stdNo, 2));
						z = Math.pow(Math.E, x);
						double noResult = y*z;
						if(stdNo == 0){
							noResult = 1;
						}
						noValues.add(noResult);

						// 							System.out.println("y " + y);
						// 							System.out.println("x " + x);
						// 							System.out.println("z " + z);

						// 							System.out.println("noResult " + noResult);
						// 							System.out.println("");

						// double y = 1/(6.2 * Math.sqrt(2*Math.PI)); 
						// double x = -1 * Math.pow((66 - 73), 2) / (2 * Math.pow(6.2, 2));
						// double z = Math.pow(Math.E, x);  
						// double result = y*z; 
					}
					double finalYes = Double.MAX_VALUE;
					double finalNo = Double.MAX_VALUE; 

					for(int i = 0; i < yesValues.size(); i++){
						if(i == 0){
							finalYes = yesValues.get(i) * totalPYes; 
						}
						else{
							finalYes = finalYes * yesValues.get(i);
						}

					}
					for(int i = 0; i < noValues.size(); i++){
						if(i == 0){
							finalNo = noValues.get(i)*totalPNo; 
						}
						else{
							finalNo = finalNo * noValues.get(i); 
						}

					}
					yesValues.clear();
					noValues.clear(); 

					// System.out.println("");
					// System.out.println("finalYes " + finalYes);
					// System.out.println("finalNo " + finalNo);

					if(finalYes >= finalNo){
						System.out.println("Yes");
					}
					else if(finalYes < finalNo){
						System.out.println("No");
					}

				}
			}
			catch(Exception e){

			}
		}

		//kNN
		else if(algorithm[length-1] == 'N' && algorithm[length-2] == 'N') {
			neighbours = Integer.parseInt(args[2].substring(0,length-2));
			//Scanning the files
			
			try {
				Scanner train_scan= new Scanner(train_file);
				Scanner test_scan = new Scanner(test_file);

				while(train_scan.hasNext()) {
					num_train++;
					String line = train_scan.nextLine();
					String[] values = line.split(",");
					train_ex.add(values);
				}

				while(test_scan.hasNext()) {
					num_test++;
					String line = test_scan.nextLine();
					String[] values = line.split(",");
					test_ex.add(values);
				}

				num_attributes = test_ex.get(0).length;

				TreeMap<Double,String> sorted_training;
				for (int i = 0 ; i < num_test ; i++) {
					sorted_training = new TreeMap<Double,String>();
					//System.out.println("Testing number = " + (i+1));
					for (int j = 0 ; j < num_train ; j++) {		
						double distance = 0; //Every new training example resets distance

						for (int k = 0 ; k < num_attributes ; k++) {
							//System.out.println("Training number = " + (j+1));

							double test_value = Double.parseDouble(test_ex.get(i)[k]);
							double train_value = Double.parseDouble(train_ex.get(j)[k]);

							double difference = Math.pow((train_value-test_value),2);
							distance += difference;

							//System.out.println("Attribute = " + (k+1) + " Difference = " + difference);
						}
						distance = Math.sqrt(distance);
						//System.out.println("Euclidean = " + distance);
						sorted_training.put(distance, train_ex.get(j)[num_attributes]); //Puts distance and class
					}
					sorted_testing.add(sorted_training);
					for(double key: sorted_training.keySet()) {
						//System.out.println("Euclidean Sort: " + key);
					}
				}

				for (int a = 0 ; a < sorted_testing.size() ; a++) {
					int yes = 0;
					int no = 0;
					int count = 0;

					TreeMap<Double,String> temp = new TreeMap<Double,String>();
					temp = sorted_testing.get(a);
					for(double key : temp.keySet()) {
						if(count >= neighbours) {
							continue;
						}
						count++;
						String class_string = temp.get(key);
						if(class_string.equals("yes")) {
							yes++;
						}
						else {
							no++;
						}
						//System.out.println("Training Example: " + (a+1) + " Distance: " + key + " Class: " + class_string);
					}

					if(yes >= no) {
						System.out.println("yes");
					}
					else {
						System.out.println("no");
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}

		else {
			System.out.println("Invalid algorithm: " + args[2]);
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











































