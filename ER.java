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


public class ER {
	static String configFile = "/home/anirban/eclipse-workspace2/serf_mli/example/example.conf";
	//static String configFile = "config";
	static String Outputfolder = null;
	static String Inputfolder = null;
	static Class matcherMerger;
	static Class algorithm;	
	static final String MATCHER_MERGER_INTERFACE = "serf.data.MatcherMerger";
	static Properties properties = new Properties();	
	
	//main function reads in a property file which specifies FileSource, MatcherMerger, and Algorithm(probably next step)
	public static void main(String[] args)
	throws Exception
	{
		long startTime   = System.nanoTime();
		if (args.length > 0){
			configFile = args[0];
		}
		//Start the partial match file
		//FileWriter myWriter = new FileWriter("match_scores_mli_choice_part.csv",true);
		//myWriter.write("name1\tdateofbirth1\tgender1\tfathersname1\tmothersname1\temail1\taddress1\tphone1\tpincode1\tpancard1\tpassp1\tkyc1\tvoter1\tname2\tdateofbirth2\tgender2\tfathersname2\tmothersname2\temail2\taddress2\tphone2\tpincode2\tpancard2\tpassp2\tkyc2\tvoter2.toString()\tnamesim)\tdobsim\tString\tgendersim\tfnamesim\tmnamesim\temailsim\taddrsim\taddrsim2\tphsim\tidsim\tfinal_score\tMATCH TYPE");
		//Load properties from property file
		properties.load(new FileInputStream(configFile));
		//Read FolderSource, MatcherMerger properties
		Inputfolder = properties.getProperty("InputFolderName");
		if (Inputfolder == null){
			throw (new TestException("No Folder Source specified!"));
		}
		System.out.println(properties.getProperty("MatcherMerger"));
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

			String filen= file.getName() ;
			filen= Inputfolder+"/"+filen;
			System.out.println("=====================================");
			System.out.println("=====================================");
			System.out.println("Input filename ->"+filen);
			System.out.println("=====================================");
			System.out.println("=====================================");
			GetRecordsFromYahooXML yds = new GetRecordsFromYahooXML(filen);
			yds.parseXML();
			Set<Record> records = yds.getAllRecords();
			System.out.println(records);
			
			Class[] mmPartypes = new Class[1];
			try{
				//Can we assume MatcherMerger's contructor will always take in RecordFactory object? 
				//TODO: JOHNSON
				mmPartypes[0] = Properties.class;
			    Constructor mmConstructor = matcherMerger.getConstructor(mmPartypes);
				Object matcherMerger = mmConstructor.newInstance(properties);
				System.out.println("Running RSwoosh on " + records.size() + " records.");
				Set<Record> result = RSwoosh.execute((serf.data.MatcherMerger)matcherMerger, records);
				System.out.println("After running RSwoosh, there are " + result.size() + " records.");
				tot_records+=records.size();
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