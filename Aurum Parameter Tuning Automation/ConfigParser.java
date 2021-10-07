package rootpack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import rootpack.Address;
import rootpack.Helper;
import rootpack.NameMatcher;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.*;

public class ConfigParser
{	
	/*Main function*/
	public static HashMap<String,double[]> parseConfig() throws Exception
	{
		HashMap<String,double[]> coefficient_dict=new HashMap<String,double[]>();
		JSONParser parser = new JSONParser();
		JSONArray configArray = (JSONArray) parser.parse(new FileReader("/home/guddu/eclipse-workspace/serf/rootpack/config.json"));
		for (Object o : configArray)
		{
			JSONObject person = (JSONObject) o;
			
			String attr = (String) person.get("attribute");
			//System.out.println(attr);
			
			String type = (String) person.get("type");
			//System.out.println(type);
			
			long reliability = (long) person.get("reliability");
			//System.out.println(reliability);
			
			JSONArray category = (JSONArray) person.get("category");
			
			//for (Object c : category)
			//{
			//	System.out.println(c+"");
			//}
			
			//System.out.println(category.get(0));
			if(category.get(0).equals("Non-ID"))
			{
				if(type.equals("name"))
				{
					if((category.get(1).equals("Top-tier")) &&(reliability==1))
						coefficient_dict.put(attr,new double[]{-4,4});
					else if((category.get(1).equals("Top-tier")) &&(reliability==0))
						coefficient_dict.put(attr,new double[]{0,4});
					else if(category.get(1).equals("Bottom-tier"))
						coefficient_dict.put(attr,new double[]{0,1});
				}
				else if(type.equals("address"))
				{
					if((category.get(1).equals("Top-tier")) &&(reliability==1))
						coefficient_dict.put(attr,new double[]{0,2});
					else if((category.get(1).equals("Top-tier")) &&(reliability==0))
						coefficient_dict.put(attr,new double[]{0,2});	//Check
					else if(category.get(1).equals("Bottom-tier"))
						coefficient_dict.put(attr,new double[]{0,1});
				}
				else if(type.equals("other"))
				{
					if((category.get(1).equals("Top-tier")) &&(reliability==1))
						coefficient_dict.put(attr,new double[]{-2,2});
					else if((category.get(1).equals("Top-tier")) &&(reliability==0))
						coefficient_dict.put(attr,new double[]{0,2});	//Check
					else if(category.get(1).equals("Bottom-tier"))
						coefficient_dict.put(attr,new double[]{0,1});
				}
			}
			else
			{
				if((category.get(2).equals("Single-valued")) &&(reliability==1))
					coefficient_dict.put(attr,new double[]{-5,10});
				else if(category.get(2).equals("Single-valued")&&(reliability==0))
					coefficient_dict.put(attr,new double[]{0,1});	//Check
				else if(category.get(2).equals("Multi-valued")&&(reliability==1))
					coefficient_dict.put(attr,new double[]{0,10});
				else if(category.get(2).equals("Multi-valued")&&(reliability==0))
					coefficient_dict.put(attr,new double[]{0,1});
			}
		}
		//System.out.println(coefficient_dict.get("fullname")[1]);
		return(coefficient_dict);
	}
	
	
	//public static HashMap<String,Double> parseThresholdConfig() throws Exception
	/*public static void main(String abc[]) throws Exception
	{
		HashMap<String,Long> threshold_dict=new HashMap<String,Long>();
		JSONParser parser2 = new JSONParser();
		JSONArray thresholdArray = (JSONArray) parser2.parse(new FileReader("/home/guddu/eclipse-workspace/serf/rootpack/threshold.json"));
		for (Object o2 : thresholdArray)
		{
			JSONObject person2 = (JSONObject) o2;
			String attr = (String) person2.get("attribute");
			long threshold = (long)person2.get("threshold");
			threshold_dict.put(attr,threshold);
			
			
		}
		System.out.println(threshold_dict.get("fullname"));
		//return(threshold_dict);
	}*/
}