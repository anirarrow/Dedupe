package rootpack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import com.mapzen.jpostal.AddressParser;
import com.mapzen.jpostal.ParsedComponent;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class Address
{
	public static List<String> preprocess_add(String address)
	{
    address = address.toLowerCase();
    address = address.replaceAll("\\p{Punct}", "");
    address = address.trim();
    
    address = address.replaceAll("madras","chennai");
    address = address.replaceAll("bombay","mumbai");
    address = address.replaceAll("banglore","bengaluru");
    address = address.replaceAll("calcutta","kolkata");
    //System.out.println("ADDRESS-----"+address);
    AddressParser p = AddressParser.getInstance();
    ParsedComponent[] components = p.parseAddress(address);
    HashMap<String, String> hmap = new HashMap<String, String>();
    for (ParsedComponent c : components)
    	hmap.put(c.getLabel(), c.getValue());
    
    String house = "",house_number = "",unit = "",level = "",road = "",suburb = "",city = "",state_district = "",state = "",postcode = "";
    
    Set set = hmap.entrySet();
    Iterator iterator = set.iterator();

    while(iterator.hasNext()) 
    {
       Map.Entry mentry = (Map.Entry)iterator.next();
       //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
       //System.out.println(mentry.getValue());
       if (mentry.getKey().equals("house"))
    	   house = mentry.getValue().toString();   
       else if (mentry.getKey().equals("house_number"))
           house_number = mentry.getValue().toString();
       else if (mentry.getKey().equals("unit"))
           unit = mentry.getValue().toString();
       else if (mentry.getKey().equals("level"))
           level = mentry.getValue().toString();
       else if (mentry.getKey().equals("road"))
           road = mentry.getValue().toString();
       else if (mentry.getKey().equals("suburb"))
           suburb = mentry.getValue().toString();
       else if (mentry.getKey().equals("city"))
           city = mentry.getValue().toString();
       else if (mentry.getKey().equals("state_district"))
           state_district = mentry.getValue().toString();
       else if (mentry.getKey().equals("state"))
           state = mentry.getValue().toString();
       else if (mentry.getKey().equals("postcode"))
           postcode = mentry.getValue().toString();
    }
    /*
    System.out.println("HOUSE-"+house);
    System.out.println("HOUSE NUMBER-"+house_number);
    System.out.println("UNIT-"+unit);
    System.out.println("LEVEL-"+level);
    System.out.println("ROAD-"+road);
    System.out.println("SUBURB-"+suburb);
    System.out.println("CITY-"+city);
    System.out.println("STATE DISTRICT-"+state_district);
    System.out.println("STATE-"+state);
    System.out.println("POSTCODE-"+postcode);
    */
    
    List<String> word = Arrays.asList("plot no","floor","flat no","h no","block");
    for (int i = 0; i < word.size(); i++)
    	{
    	house = house.replaceAll(word.get(i),"");
    	house_number = house_number.replaceAll(word.get(i),"");
    	unit = unit.replaceAll(word.get(i),"");
    	level = level.replaceAll(word.get(i),"");
    	road = road.replaceAll(word.get(i),"");
    	suburb = suburb.replaceAll(word.get(i),"");
    	city = city.replaceAll(word.get(i),"");
    	state_district = state_district.replaceAll(word.get(i),"");
    	state = state.replaceAll(word.get(i),"");
    	}
    /*
    System.out.println("HOUSE-"+house);
    System.out.println("HOUSE NUMBER-"+house_number);
    System.out.println("UNIT-"+unit);
    System.out.println("LEVEL-"+level);
    System.out.println("ROAD-"+road);
    System.out.println("SUBURB-"+suburb);
    System.out.println("CITY-"+city);
    System.out.println("STATE DISTRICT-"+state_district);
    System.out.println("STATE-"+state);
    System.out.println("POSTCODE-"+postcode);
    */
    return Arrays.asList(house,house_number,unit,level,road,suburb,city,state_district,state,postcode);
	}
	
	public static double fuzz_score_func(String s1,String s2)
	{
		double score1= (double)(FuzzySearch.tokenSortRatio(s1,s2)/(double)(100));
		double score2= (double)(FuzzySearch.tokenSetRatio(s1,s2)/(double)(100));
		double score3= 100*(score1 + score2)/2;
	    //System.out.println("Fuzz match score-"+score3);
	    return score3;
	}
	
	//public static List<? extends Object> findAddSim(String address1, String address2)
	public static double findAddSim(String address1, String address2)
	{
		//System.out.println("---------ADDRESS 1 VARIABLES-------");
		List<String> all_add_var1 = preprocess_add(address1);
		//System.out.println("---------ADDRESS 2 VARIABLES-------");
		List<String> all_add_var2 = preprocess_add(address2);
		
		String[] words1 = address1.split("\\s");
		String[] words2 = address2.split("\\s");
		double jaro_score,overall_score,house_score,house_number_score,unit_score,level_score,road_score,suburb_score,city_score,state_district_score,state_score,postcode_score;
		if ((address1.length()==0 || address2.length()==0||address1.toLowerCase().equals("na")||address2.toLowerCase().equals("na")||address1.toLowerCase().equals("- -")||address2.toLowerCase().equals("- -")))
		{
			return -0.5;
		}
		//Finding score for every address field
		if (words1.length<=2 || words2.length<=2)
	        jaro_score = 10.0;
	    else
	    	jaro_score =  new JaroWinklerDistance().apply(address1,address2)*100;
		
		if (address1.equals("") || address2.equals(""))
	        overall_score = 0.0;
	    else
	    {   double a = FuzzySearch.tokenSortRatio(address1,address2);
	    	double b = FuzzySearch.tokenSetRatio(address1,address2);
	    	overall_score = Math.max(a,b);
	    	//System.out.println("OVERALLL SCORE---"+a+" "+b+" "+overall_score);
	    }
		if (all_add_var1.get(0).toString().equals("") || all_add_var2.get(0).toString().equals(""))
			house_score = 0.0d;
		else
			house_score = fuzz_score_func(all_add_var1.get(0).toString(),all_add_var2.get(0).toString());
		
		if (all_add_var1.get(1).toString().equals("") || all_add_var2.get(1).toString().equals(""))
			house_number_score = 0.0d;
		else
			house_number_score = fuzz_score_func(all_add_var1.get(1).toString(),all_add_var2.get(1).toString());
	    
		if (all_add_var1.get(2).toString().equals("") || all_add_var2.get(2).toString().equals(""))
			unit_score = 0.0d;
		else
			unit_score = fuzz_score_func(all_add_var1.get(2).toString(),all_add_var2.get(2).toString());
	    
		if (all_add_var1.get(3).toString().equals("") || all_add_var2.get(3).toString().equals(""))
			level_score = 0.0d;
		else
			level_score = fuzz_score_func(all_add_var1.get(3).toString(),all_add_var2.get(3).toString());
	    
		if (all_add_var1.get(4).toString().equals("") || all_add_var2.get(4).toString().equals(""))
			road_score = 0.0d;
		else
			road_score = fuzz_score_func(all_add_var1.get(4).toString(),all_add_var2.get(4).toString());
	    
		if (all_add_var1.get(5).toString().equals("") || all_add_var2.get(5).toString().equals(""))
			suburb_score = 0.0d;
		else
			suburb_score = fuzz_score_func(all_add_var1.get(5).toString(),all_add_var2.get(5).toString());
	    
		if (all_add_var1.get(6).toString().equals("") || all_add_var2.get(6).toString().equals(""))
			city_score = 0.0d;
		else
			city_score = fuzz_score_func(all_add_var1.get(6).toString(),all_add_var2.get(6).toString());
	    
		if (all_add_var1.get(7).toString().equals("") || all_add_var2.get(7).toString().equals(""))
			state_district_score = 0.0d;
		else
			state_district_score = fuzz_score_func(all_add_var1.get(7).toString(),all_add_var2.get(7).toString());
	    
		if (all_add_var1.get(8).toString().equals("") || all_add_var2.get(8).toString().equals(""))
			state_score = 0.0d;
		else
			state_score = fuzz_score_func(all_add_var1.get(8).toString(),all_add_var2.get(8).toString());
	    
		if (all_add_var1.get(9).toString().equals("") || all_add_var2.get(9).toString().equals(""))
			postcode_score = 0.0d;
		else
			postcode_score = fuzz_score_func(all_add_var1.get(9).toString(),all_add_var2.get(9).toString());
	    
		//System.out.println("---ALL SCORE----"+city_score+" "+state_score+" "+postcode_score);
		
		double Final_score = 0.0d;
		double Result_score = 0.0d;
		String Flag = "";
	    //Finding final address score
		if ((postcode_score!=0 && postcode_score!=100) && !(jaro_score>=90) && !(overall_score>=90))
	        {Final_score = Math.round(overall_score);
	        Flag = "0";}
	    else if ((postcode_score==0) && !(jaro_score>=90))
	        {Final_score = Math.round(jaro_score);
	        Flag = "0.5";}
	    else if (postcode_score==0 && jaro_score>=90)
	        {Final_score = Math.round(jaro_score);
	        Flag = "0.75";}
	    else if ((postcode_score!=0 && postcode_score!=100) && jaro_score>=90)
	        {Final_score = Math.round(jaro_score);
	        Flag = "1";}
	    else if (postcode_score==100 && (state_score==0 || city_score==0) && (state_score!=100 || city_score!=100))
	        {Final_score = Math.round(overall_score);
	        Flag = "2";}
	    else if (postcode_score==100 && ((state_score!=0 && state_score!=100) || (city_score!=0 && city_score!=100)))
	        {Final_score = Math.round(overall_score);
	        Flag = "3";}
	    else if (postcode_score==100 && (state_score==100 || city_score==100))
	    {
	    	if (jaro_score>=90)
	            {Final_score = Math.round(jaro_score);
	            Flag = "4";}
	    	else if (jaro_score>=50 && jaro_score<90)
	    	{
	    		//List<Double> col_field_list1 = new ArrayList<Double>(Arrays.asList(house_score,house_number_score,unit_score,level_score,road_score,suburb_score,city_score,state_district_score,state_score,postcode_score));
	    		List<Double> col_field_list2 = new ArrayList<Double>(Arrays.asList(house_score,house_number_score,unit_score,level_score,road_score,suburb_score));
	    		List<Double> col_field_list3 = new ArrayList<Double>(Arrays.asList(city_score,state_district_score,state_score,postcode_score));
	    		
	            double s1 = 0.0d,s2 = 0.0d;
	            int c1=0,c2=0,d1=0,d2=0;
	            Double ra,ra1;
	            for (int j = 0; j < col_field_list2.size(); j++)
	            {	ra = col_field_list2.get(j);
	            	//System.out.println("RAAA"+ra);
	                if (ra!=0.0)
	                {
	                    s1 = s1 + ra;
	                    c1 = c1+1;
	                }
	                else if (ra==0.0)
	                    d1 = d1+1;
	            }
	            for (int k = 0; k < col_field_list3.size(); k++)
	            {
	            	ra1 = col_field_list3.get(k);
	            	//System.out.println("RAAA1"+ra1);
	                if (ra1!=0.0)
	                {
	                    s2 = s2 + ra1;
	                    c2 = c2+1;
	                }
	                else if (ra1==0.0)
	                    d2 = d2+1;
	            }
	            System.out.println("C1--"+ c1 + "-C2--" + c2);
	            if ((c1>0 && c1<=6) && (c2>0 && c2<=4))
	                {Final_score = Math.round(0.8*(s1/c1) + 0.2*(s2/c2));
	                Flag = "5";}
	            else if (c1==0 && c2!=0)
	                {Final_score = Math.round((s2/c2)*0.5);
	                Flag = "6";}
	            else if (c1!=0 && c2==0)
	                {Final_score = Math.round((s1/c1)*0.5);
	                Flag = "6.5";}
	    	}
	        else
	            {Final_score = Math.round(jaro_score);
	            Flag = "7";}
	    }
	    else
	        {Final_score = 999;
	        Flag = "8";}
				
		if (jaro_score >= 90)
			Result_score = Math.round(jaro_score);
		else if (jaro_score <= 20)
			Result_score = Math.round(jaro_score);
		else if (Final_score < jaro_score)
			Result_score = Math.round(Final_score);
		else
			Result_score = Math.max(Final_score,jaro_score);
	
	//return Arrays.asList(jaro_score,overall_score,house_score,house_number_score,unit_score,level_score,road_score,suburb_score,city_score,state_district_score,state_score,postcode_score,Final_score,Result_score,Flag);
	return Result_score/100;
	}
//	public static void main(String[] args)
//	{
//		Address addr = new Address();
//		List<? extends Object> s=addr.findAddSim("abc", "abd");
//		System.out.println(s);
//	}
}
