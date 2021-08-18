package serf;
import java.io.*;
import java.util.Scanner;
import java.io.FileInputStream;
import com.wcohen.secondstring.Jaro;
import com.wcohen.secondstring.StringWrapper;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.xml.sax.SAXException;

import com.wcohen.secondstring.Jaro;
import com.wcohen.secondstring.StringWrapper;

import serf.data.Record;
import serf.data.SimpleRecordFactory;
//import serf.data.YahooMatcherMerger;
import serf.data.io.XMLifyYahooData;
import serf.data.storage.impl.GetRecordsFromYahooXML;
import serf.deduplication.RSwoosh;
import serf.test.TestException;

import java.util.Arrays;
import java.util.Properties;
import java.io.*;
import java.lang.reflect.*;

class FileIO
{
	public void writeToFile(String s1, String s2, String s3)
	{
		
		try
		{
			FileWriter myWriter = new FileWriter("/home/anirban/Dedupe_anirban/labelled_data/NMEM.csv",true);
			myWriter.write(s1+'\t'+s2+'\t'+s3+'\n');
			
			myWriter.close();
	    } 
		catch (IOException e) 
		{
			System.out.println("An error occurred in file writing.");
			e.printStackTrace();
		}
	      
	}
}

class Similarity
{
	public static String preprocess_sort_word(String name)
	{
	
		name= name.toLowerCase();
		name= name.replaceAll("\\p{Punct}", "");
		name= name.trim();
		String[] words = name.split("\\s+");
	
		String[] initials = {"smt","mrs","mr","ms","shree","shri","sri","shriman","shrimati","m.s.","m/s","messes","messesrs",
			"messors","messres","messrs","messsers","miss","misss","mistar","ms.shri","prof","prop.shri",
			"prop.smt","sh","dr","adv","col","lt","sh.shri","shri","sm","smt","lt","lt.","col.","cl","cdr",
			"captain","flight","lieutenant","colonel","commander","group","brig","prop"};
		String final_name= new String("");

		for(int i=0; i<words.length; i++) 
		{
			if(Arrays.asList(initials).contains(words[i])==false)
			{
				final_name= final_name+" "+words[i];
			}
		}
		final_name= final_name.trim();
		return final_name;
	}
	public double findNameSim(String name1, String name2) 
	{
		//System.out.println("Name:"+name1);
		//System.out.println(name1.length());
		//System.out.println("Name:"+name2);
		//System.out.println(name2.length());
		
		if(name1.length()<3 || name2.length()<3)
		{
			//System.out.println("Came here");
			return -0.5;
		}
    
		name1= preprocess_sort_word(name1);
		name2= preprocess_sort_word(name2);
		DoubleMetaphone m = new DoubleMetaphone();
		String p1,s1,p2,s2;
		p1 = m.doubleMetaphone(name1,false);
		s1 = m.doubleMetaphone(name1,true) ;
		p2 = m.doubleMetaphone(name2,false) ;
		s2 = m.doubleMetaphone(name2,true) ;  
		
		double dm1= (double)FuzzySearch.ratio(p1,p2)/(double)100;
		double dm2= (double)FuzzySearch.ratio(s1,p2)/(double)100;
		double dm3= (double)FuzzySearch.ratio(p1,s2)/(double)100;
		double dm4= (double)FuzzySearch.ratio(s1,s2)/(double)100;
		double final_dm= Math.max(dm1,dm2);
		final_dm= Math.max(final_dm, dm3);
		final_dm= Math.max(final_dm,dm4);


		
		String initial1= print_initials(name1);
		String initial2= print_initials(name2);
		//System.out.println(initial1+initial2);
		JaroWinklerDistance jdistance= new JaroWinklerDistance();
		double n= jdistance.apply(initial1,initial2);
	
		double x= (double)(FuzzySearch.ratio(name1,name2))/(double)(100);
	
		double final_score = (0.5*x +0.4*final_dm + 0.1*n);
	
		//System.out.println("Returning final_name_score:"+final_score);
	
		return final_score;


	}
	public static String print_initials(String name)
  	{
		//System.out.println(name);
		//System.out.println("***");
		if(name.length()==0)
			return "";
		name=" "+name;
		//System.out.println(name);
		String c="";
		name=name.toUpperCase();
		//System.out.println(name.length());
		for(int i=0;i<name.length()-1;i++)
		{
			//System.out.println(name.charAt(i));
			if(name.charAt(i)==' ')
			{
				char a= name.charAt(i+1);
				c=c+a;
			}
		}
		return c;

	}
	public static String getSortedInitials(String name)
	{
		
		String s=print_initials(name);
		char tempArray[] = s.toCharArray();
		
		Arrays.sort(tempArray);
		return new String(tempArray);
	}
	public static boolean doAbbrvMatch(String name1,String name2)
	{
		String abbrv1=getSortedInitials(name1);
		String abbrv2=getSortedInitials(name2);
		
		if (abbrv1.contains(abbrv2) || abbrv2.contains(abbrv1))
			return true;
		return false;
	}
	
	public double findSim(String s1, String s2)
	{
		double tmp=-1;
		if (s1.length()==0 || s2.length()==0)
			return -0.5;
		String[] l1 = s1.split(";");
		String[] l2 = s2.split(";");
		//For every string in the field, separated by ; find jaro and return the max of them
		for (String str1: l1)
		{
			for (String str2: l2)
			{
				StringWrapper sw1 = new StringWrapper(str1);
				StringWrapper sw2 = new StringWrapper(str2);
				Jaro jro = new Jaro();
				double simscore = jro.score(sw1, sw2);
				if (simscore>tmp)
					tmp=simscore;
			}
			
		}
		return tmp;
	}
	public double findSim_phone(String s1, String s2)
	{
		double tmp=-1;
		if (s1.length()==0 || s2.length()==0)
			return -0.5;
		//Phone numbers are separated by comma
		String[] l1 = s1.split(",");
		String[] l2 = s2.split(",");
		//For every string in the field, separated by ',' find jaro and return the max of them
		for (String str1: l1)
		{
			for (String str2: l2)
			{
				StringWrapper sw1 = new StringWrapper(str1);
				StringWrapper sw2 = new StringWrapper(str2);
				Jaro jro = new Jaro();
				double simscore = jro.score(sw1, sw2);
				if (simscore>tmp)
					tmp=simscore;
			}
			
		}
		return tmp;
	}
}

public class TestCheck 
{
	public static void main(String[] args)
	throws Exception
	{
		int coeff_id=0,coeff_address2,coeff_email,coeff_name,coeff_fname=0,coeff_mname=0,coeff_address,coeff_contact, coeff_pan,coeff_passport, coeff_ckyc, coeff_voterid, coeff_dob, coeff_gender;
		double final_score=0;
		BufferedReader br = new BufferedReader(new FileReader("/home/anirban/Dedupe_anirban/labelled_data/test_data_labels_acc.csv"));  
		String line="";
		
		//Exact match predicted as partial/exact/no match
	    int EMPM=0;
	    int EMEM=0;
	    int EMNM=0;
	    //Partial match predicted as partial/exact/no match
	    int PMPM=0;
	    int PMEM=0;
	    int PMNM=0;
	    //No match predicted as partial/exact/no match
	    int NMPM=0;
	    int NMEM=0;
	    int NMNM=0;
		
		while ((line = br.readLine()) != null)   //returns a Boolean value  
		//while (sc.hasNext())  //returns a boolean value  
		{  
			System.out.println(line);
			String[] l = line.split("\t"); 
			System.out.println(l[19]);
			
			String source=l[0];
			
		    
			String unique_ID_left=l[1];
		    String dob_left=l[2];	
		    String gender_left=l[3];	
		    String pan_left=l[4];	
		    String aadhar_left=l[5];
		    String passport_left=l[6];
		    String ckyc_left=l[7];
		    String voterid_left=l[8];
		    String drivinglicence_left=l[9];
		    String eia_left=l[10];
		    String fullname_left=l[11];
		    String fatherfullname_left=l[12];
		    String motherfullname_left=l[13];
		    String contactnumbers_left=l[14];
		    String email_left=l[15];
		    String addresses_left=l[16];
		    String unique_ID_right=l[17];
		    String dob_right=l[18];
		    String gender_right=l[19];
		    String pan_right=l[20];
		    String aadhar_right=l[21];
		    String passport_right=l[22];
		    String ckyc_right=l[23];
		    String voterid_right=l[24];
		    String drivinglicence_right	=l[25];
		    String eia_right=l[26];
		    String fullname_right=l[27];
		    String fatherfullname_right=l[28];
		    String motherfullname_right=l[29];
		    String contactnumbers_right=l[30];
		    String email_right=l[31];
		    String addresses_right=l[32];
		    String labels=l[33];
		    
		    //Find similarity between pairs of attributes
		    Similarity s = new Similarity();
		    double namesim = s.findNameSim(fullname_left,fullname_right);
		    double fnamesim = s.findNameSim(fatherfullname_left,fatherfullname_right);
		    double mnamesim = s.findNameSim(motherfullname_left,motherfullname_right);
		    System.out.println(dob_left);
		    System.out.println(dob_right);
		    double dobsim = s.findSim(dob_left,dob_right);
		    double gendersim = s.findSim(gender_left,gender_right);
		    double addrsim = s.findSim(addresses_left,addresses_right);
		    double pansim = s.findSim(pan_left,pan_right);
		    double voteridsim = s.findSim(voterid_left,voterid_right);
		    double passportsim = s.findSim(passport_left,passport_right);
		    double ckycsim = s.findSim(ckyc_left,ckyc_right);
		    double aadhaarsim = s.findSim(aadhar_left,aadhar_right);
		    double drivinglicencesim = s.findSim(drivinglicence_left,drivinglicence_right);
		    double eiasim = s.findSim(eia_left,eia_right);
		    double emailsim = s.findSim(email_left,email_right);
		    double phsim = s.findSim_phone(contactnumbers_left,contactnumbers_right);
		    double idsim=0;
		    System.out.println(dobsim);
		    System.out.println(gendersim);
		    if (dobsim==1 && gendersim==1)
			{
				//System.out.println("Gender Match");
				coeff_dob=1;
				coeff_gender=1;
				/************************************************************/
				//NAME
				/***********************************************************/
				
				//If namesim > 0.85, positive
				if (namesim>=0.85)
					coeff_name=3;	//Same name, different people
				//If namesim < 0.65, negative
				else if (namesim<0.65 && namesim!=-0.5)
					coeff_name=-4;
				//If namesim between 0.65 and 0.85
				else if ((namesim>=0.65)&&(namesim<0.85))
				{
					//If abbreviations are subset of each other, no contribution
					if (s.doAbbrvMatch(fullname_left,fullname_right))
						coeff_name=0;
					//Else negative
					else 
					{
						coeff_name=-4;
						//System.exit(0);
					}
				}
				else
					coeff_name=0;
				//Address coeff
				if (addrsim>0.95)	//Same house
					coeff_address=2;
				else if (addrsim<=0.95 && addrsim!=-0.5)
					coeff_address=-1;	//Changed address
				else
					coeff_address=0;
				//Address2 coeff
				if (addrsim>0.7)	//Same house
					coeff_address2=2;
				else if (addrsim<=0.7 && addrsim!=-0.5)
					coeff_address2=-1;	//Changed address
				else
					coeff_address2=0;
				//Phone coeff
				if (phsim>0.95)
					coeff_contact=3;	//Parents' phone number
				else if (phsim<=0.95 && phsim!=-0.5)
					coeff_contact=-1;	//Multiple phones
				else
					coeff_contact=0;
				/************************************************************/
				//FATHER'S NAME
				/***********************************************************/
				
				if (fnamesim>=0.85)
					coeff_fname=2;	//Same name, different people
				//If namesim < 0.65, negative
				else if (fnamesim<0.65 && fnamesim!=-0.5)
					coeff_fname=-3;
				//If namesim between 0.65 and 0.85
				else if ((fnamesim>=0.65)&&(fnamesim<0.85))
				{
					//If abbreviations are subset of each other, no contribution
					if (s.doAbbrvMatch(fatherfullname_left,fatherfullname_right))
						coeff_fname=0;
					//Else negative
					else
						coeff_fname=-3;
				}
				else
					coeff_fname=0;
				/************************************************************/
				//MOTHER'S NAME
				/***********************************************************/
				if (mnamesim>=0.85)
					coeff_mname=2;	//Same name, different people
				//If namesim < 0.65, negative
				else if (mnamesim<0.65 && mnamesim!=-0.5)
					coeff_mname=-3;
				//If namesim between 0.65 and 0.85
				else if ((mnamesim>=0.65)&&(mnamesim<0.85))
				{
					//If abbreviations are subset of each other, no contribution
					if (s.doAbbrvMatch(motherfullname_left,motherfullname_right))
						coeff_mname=0;
					//Else negative
					else
						coeff_mname=-3;
				}
				else
					coeff_mname=0;
				
				//If any one ID matches, high contribution
				if(pansim>=0.97 || passportsim>=0.97 || voteridsim>=0.97 || ckycsim>=0.97 || aadhaarsim>=0.97 || drivinglicencesim>=0.97 || eiasim>=0.97)
				{
					idsim=1;
					coeff_id=5;	//Strong influence if any ID attribute matches
				}
				//If pan is available but does not match, negative contribution	
				else if((pansim!=-0.5)&&(pansim<0.97))
				{
					idsim=1;
					coeff_id=-5;	//Strong influence if all mismatch
				}
				//If pan is blank (and no other ID attributes match or are blank), no contribution.	
				else if (pansim==-0.5)
				{
					idsim=0;
					coeff_id=-4;
				}
				//Email coeff
				if (emailsim>0.9)
					coeff_email=3;	//Sometimes wife provides husband's email
				else if (emailsim<=0.9 && emailsim!=-0.5)
					coeff_email=-1;
				else
					coeff_email=0;
				
				
				//Final score calculation
				//final_score=coeff_name*namesim+coeff_fname*fnamesim+coeff_mname*mnamesim+coeff_dob*dobsim+coeff_gender*gendersim+coeff_email*emailsim+coeff_address*addrsim+coeff_address2*addrsim2+coeff_contact*phsim+coeff_id*idsim;
				//Final score without pin
				final_score=coeff_name*namesim+coeff_fname*fnamesim+coeff_mname*mnamesim+coeff_dob*dobsim+coeff_gender*gendersim+coeff_email*emailsim+coeff_address*addrsim+coeff_contact*phsim+coeff_id*idsim;
				System.out.println("Fullname left:"+fullname_left);
				System.out.println("Fullname right:"+fullname_right);
				System.out.println("NAMESIM:"+namesim);
				System.out.println("FNAMESIM:"+fnamesim);
				System.out.println("MNAMESIM:"+mnamesim);
				System.out.println("EMAILSIM:"+emailsim);
				System.out.println("PHONESIM:"+phsim);
				System.out.println("ADDRSIM:"+addrsim);
				System.out.println("DOBSIM:"+dobsim);
				System.out.println("GENDERSIM:"+gendersim);
				System.out.println("IDSIM:"+idsim);
				
				System.out.println("FINAL SCORE:"+final_score);
				
			}			    
		    //Write final score and labels in file based on conditions	
		    FileIO fio=new FileIO();
		    String final_label="X";
		    
		    if (final_score<=4)
			{
				final_label="X";
				//System.out.println("####");
				//fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label));
			}
			else if (final_score>=9)
			{
				final_label="1";
				if ((final_label.equals("1"))&&(labels.equals("X")))	//NMEM case
					fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label));
			}	
			else if ((final_score<9) && (final_score>4)) 
			{
				
				if((pansim<0.97) && (pansim!=-0.5))
				{
					final_label="X";
					//fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label));
					
				}
				else if ((namesim<0.65))
				{
					final_label="X";
					//fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label));
				}
				else
				{
					
					final_label="0";
					//fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label));
				}
		    
		    
			}
		    
		    
		    if ((final_label.equals("1"))&&(labels.equals("1")))
		    {
		    	//System.out.println("Hello");
		    	//System.exit(0);
		    	EMEM+=1;
		    }
		    	
		    if ((final_label.equals("0"))&&(labels.equals("1")))
			    EMPM+=1;
			if ((final_label.equals("X"))&&(labels.equals("1")))
				EMNM+=1;
			
			if ((final_label.equals("0"))&&(labels.equals("0")))
		    	PMPM+=1;
		    if ((final_label.equals("1"))&&(labels.equals("0")))
			    PMEM+=1;
			if ((final_label.equals("X"))&&(labels.equals("0")))
				PMNM+=1;
			
			if ((final_label.equals("X"))&&(labels.equals("X")))
		    	NMNM+=1;
		    if ((final_label.equals("0"))&&(labels.equals("X")))
			    NMPM+=1;
			if ((final_label.equals("1"))&&(labels.equals("X")))
				NMEM+=1;
	
		}
		System.out.println("EMPM:"+Integer.toString(EMPM));
		System.out.println("EMNM:"+Integer.toString(EMNM));
		System.out.println("EMEM:"+Integer.toString(EMEM));
		System.out.println("PMPM:"+Integer.toString(PMPM));
		System.out.println("PMNM:"+Integer.toString(PMNM));
		System.out.println("PMEM:"+Integer.toString(PMEM));
		System.out.println("NMPM:"+Integer.toString(NMPM));
		System.out.println("NMEM:"+Integer.toString(NMEM));
		System.out.println("NMNM:"+Integer.toString(NMNM));
	    
	
	}  
}