import java.util.*;
import java.io.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


class Driver {
	
	static String helpMessage = "Carroll -d <PCFG location> -f <filename> | -s <string>";
	static String pcfgFilePath = "";
	
	public static void main(String[] args) throws IOException, ParseException {
		
		Options options = new Options();
		
		options.addOption("s", true, "Read text from the given string");
		options.addOption("f", true, "Read text from the given filename");
		options.addOption("d", true, "Location for the PCFG file");
		
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse( options, args);
		
		String inputText = "";
		
		if(cmd.hasOption("d")) {
			pcfgFilePath = cmd.getOptionValue("d");
		} else {
			System.out.println(helpMessage);
			System.exit(0);
		}
		
		if(cmd.hasOption("s")) {
		    // read from string
			String inputString = cmd.getOptionValue("s");
			if(inputString == null) {
				System.out.println(helpMessage);
				System.exit(0);
			}
			
			inputText = inputString;
		}
		else if(cmd.hasOption("f")){
		    // read from file
			String inputFileName = cmd.getOptionValue("f");
			if(inputFileName == null) {
				System.out.println(helpMessage);
				System.exit(0);
					
			}
			File inputFile = new File(inputFileName);
			  
			if(!inputFile.exists()) {
				System.out.println("File doesn't exist...");
				System.exit(0);
			}
			  
			inputText = readFileAsString(inputFileName);
		} else {
			System.out.println(helpMessage);
			System.exit(0);
		}
	  
	  
	  
	  List<String> sentences = TextUtils.extractSentences(inputText);
	  
	  DepParser dp = new DepParser(pcfgFilePath);
	  PredicateExtractor pe = new PredicateExtractor();
	  
	  for(String sentence: sentences) {
		  Collection<LfPredicate> predicates = pe.extractPredicates(dp.parse(sentence));
		  for(LfPredicate predicate: predicates) {
			  System.out.print(predicate.toString()+" ");
		  }
		  if(predicates.size() == 0) {
			  System.out.print("nopred(e0)");
		  }
		  System.out.println();
	  }
	  

	  
  }
  
  /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
   */ 
   private static String readFileAsString(String filePath) throws java.io.IOException{
       StringBuffer fileData = new StringBuffer(1000);
       BufferedReader reader = new BufferedReader(
               new FileReader(filePath));
       char[] buf = new char[1024];
       int numRead=0;
       while((numRead=reader.read(buf)) != -1){
           String readData = String.valueOf(buf, 0, numRead);
           fileData.append(readData);
           buf = new char[1024];
       }
       reader.close();
       return fileData.toString();
   }
   
}

