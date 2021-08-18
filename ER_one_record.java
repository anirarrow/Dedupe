package serf;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import serf.data.Record;
import serf.data.SimpleRecordFactory;
//import serf.data.YahooMatcherMerger;
import serf.data.io.XMLifyYahooData;
import serf.data.storage.impl.GetRecordsFromYahooXML;
import serf.deduplication.RSwoosh;
import serf.test.TestException;

import java.util.Properties;
import java.io.*;
import java.lang.reflect.*;


public class ER_one_record {
	static String configFile = "/home/anirban/eclipse-workspace2/serf_mli/example/example_one_record.conf";
	//static String configFile = "config";
	static String Outputfolder = null;
	static String Inputfolder = null;
	static String Interfolder = null;
	static Class matcherMerger;
	static Class algorithm;
	static final String MATCHER_MERGER_INTERFACE = "serf.data.MatcherMerger";
	static Properties properties = new Properties();	
	
	//main function reads in a property file which specifies FileSource, MatcherMerger, and Algorithm(probably next step)
	//The input folder should contain the DOB, gender blocked files of input records (even if one record)
	//The intermediate folder should contain all output clusters with the same names as inputs
	public static void main(String[] args)
	throws Exception
	{
		long startTime   = System.nanoTime();
		if (args.length > 0){
			configFile = args[0];
		}
		//Load properties from property file
		properties.load(new FileInputStream(configFile));
		//Read FolderSource, MatcherMerger properties
		Inputfolder = properties.getProperty("InputFolderName");
		//Read intermediate folder where existing clusters are kept
		Interfolder = properties.getProperty("InterFolderName");
		if (Inputfolder == null){
			throw (new TestException("No Folder Source specified!"));
		}
		if (Interfolder == null){
			throw (new TestException("No intermediate folder specified!"));
		}
		matcherMerger = Class.forName(properties.getProperty("MatcherMerger"));
		if (matcherMerger == null){
			throw (new TestException("No MatcherMerger Class specified!"));
		}
		if (checkMatcherMergerInterface(matcherMerger) != true){
			throw (new TestException("Given MatcherMerger class does not implement SimpleMatcherMerger interface!"));
			
		}
		
		
		int tr=runRSwoosh();
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Time taken to run RSwoosh on "+tr+" records:"+totalTime/1000000000+"seconds.");
	}
	
	//Recursively checks if testClass or any of its ancestor class implements MathcerMerger interface
	private static boolean checkMatcherMergerInterface(Class testClass){
		Class[] interfaces = testClass.getInterfaces();
		Class superClass = testClass.getSuperclass();
		try{
			for (int i = 0; i < interfaces.length; i++){
				if (interfaces[i] == Class.forName(MATCHER_MERGER_INTERFACE)){
					return true;					
				}
			}
			if (superClass!= null && checkMatcherMergerInterface(superClass)){
				return true;	
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	private static int runRSwoosh() throws SAXException, IOException, ParserConfigurationException
	{
		String Outputfile = null;
		File folder = new File(Inputfolder);
		File[] listOfFiles = folder.listFiles();
		int c=0,count=0;
		int tot_records=0;

		int num_records=0;	//Number of records: stored as ID field in cluster. Continuous across multiple files.
		for (File file : listOfFiles) {
			
			//count+=1;
			//if(count==2)
			//	break;
			
		    if (file.isFile()) {
		    //Input file to be read
			String filen= file.getName() ;
			String filein, fileinter;
			
			//Get the input file name, and get the same name from the intermediate folder
			filein= Inputfolder+"/"+filen;
			fileinter= Interfolder+"/"+filen;
			
			System.out.println("=====================================");
			System.out.println("=====================================");
			System.out.println("Input filename ->"+filein);
			System.out.println("Intermediate filename ->"+fileinter);
			System.out.println("=====================================");
			System.out.println("=====================================");
			
			//Read the XMLs from the input and intermediate folders
			GetRecordsFromYahooXML ydsin = new GetRecordsFromYahooXML(filein);
			ydsin.parseXML();
			GetRecordsFromYahooXML ydsinter = new GetRecordsFromYahooXML(fileinter);
			ydsinter.parseXML();
			
			//Get the records for the input and the intermediate clusters
			Set<Record> recordsin = ydsin.getAllRecords();
			Set<Record> recordsinter = ydsinter.getAllRecords();
			
			Class[] mmPartypes = new Class[1];
			try{
				//Can we assume MatcherMerger's contructor will always take in RecordFactory object? 
				//TODO: JOHNSON
				mmPartypes[0] = Properties.class;
			    Constructor mmConstructor = matcherMerger.getConstructor(mmPartypes);
				Object matcherMerger = mmConstructor.newInstance(properties);
				System.out.println("Running RSwoosh on Input size: " + recordsin.size() + " records.");
				System.out.println("Intermediate cluster size: " + recordsinter.size() + " records.");
				//RSwoosh for single/multiple inputs in real time
				Set<Record> result = RSwoosh.execute((serf.data.MatcherMerger)matcherMerger, recordsin, recordsinter);
				System.out.println("After running RSwoosh, there are " + result.size() + " records.");
				tot_records+=recordsin.size();
				c+=1;
				if (c%1==0)
					System.out.println("Ran RSwoosh on"+c+"files.");
				if ((Outputfolder = properties.getProperty("OutputFolderName")) != null)
				{
					Outputfile= Outputfolder+"/"+ file.getName();
					System.out.println("Output file ->"+Outputfile);
					FileWriter fw = new FileWriter(Outputfile);
					fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					XMLifyYahooData.openRecordSet(fw);
					for (Record r : result) {
						
						//System.out.println(r.getID2());
						//System.out.println("@@@@@@@@@@@@@@@@@");
						//System.exit(0);
						num_records+=1;
						XMLifyYahooData.serializeRecord(r, fw, num_records);
					}	
					XMLifyYahooData.closeRecordSet(fw);
					fw.close();
				}
				

			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
		}
	
		return tot_records;
	}
}