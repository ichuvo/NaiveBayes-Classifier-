import java.util.*;
import java.io.*;
 
class MyClassifier2 { //Euclidean
	static int neighbours;
	static int error_counter;
	static int COUNTER;
	
	public static void main(String[] args) {
		ArrayList<String[]> train_ex = new ArrayList<String[]>();
		ArrayList<String[]> test_ex = new ArrayList<String[]>();
		ArrayList<String> test_class= new ArrayList<String>();
		
		ArrayList<TreeMap<Double, String>> sorted_testing = new ArrayList<TreeMap<Double, String>>();
 
		int num_train = 0;
		int num_test = 0;
		int num_attributes = 0;
 
		//Files from arguments
		try {
			File train_file = new File(args[0]);
			File test_file = new File(args[1]);
			
			char[] char_algorithm = args[2].toCharArray();
			int length = char_algorithm.length;
			
			if(char_algorithm[length-1] == 'N' && char_algorithm[length-2] == 'N') {
				neighbours = Integer.parseInt(args[2].substring(0,length-2));
				//System.out.println("neighbours = " + neighbours);
			}
 
			// Training file test 
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
				test_class.add(values[5]);
			}
 
			num_attributes = test_ex.get(0).length;
 
			TreeMap<Double,String> sorted_training;
			for (int i = 0 ; i < num_test ; i++) {
				sorted_training = new TreeMap<Double,String>();
				//System.out.println("Testing number = " + (i+1));
				for (int j = 0 ; j < num_train ; j++) {		
					double distance = 0; //Every new training example resets distance
 
					for (int k = 0 ; k < 5 ; k++) {
						//System.out.println("Training number = " + (j+1));
 
						double test_value = Double.parseDouble(test_ex.get(i)[k]);
						double train_value = Double.parseDouble(train_ex.get(j)[k]);
 
						double difference = Math.pow((train_value-test_value),2);
						distance += difference;
 
						//System.out.println("Attribute = " + (k+1) + " Difference = " + difference);
					}
					distance = Math.sqrt(distance);
					//System.out.println("Euclidean = " + distance);
					sorted_training.put(distance, train_ex.get(j)[5]); //Puts distance and class
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
				COUNTER++;
				if(yes >= no) {
					//System.out.println("yes");
					if(test_class.get(a).equals("yes")){
						//true
					}
					else{
						error_counter++;
					}
				}
				else {
					//System.out.println("no");
					if(test_class.get(a).equals("no")){
						//true
					}
					else{
						error_counter++;
					}
				}
			}
			System.out.println(COUNTER);
			System.out.println(COUNTER-error_counter);
		}
		catch (Exception e){
			e.printStackTrace();
		}	
	}
}
