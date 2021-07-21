import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import org.apache.commons.codec.language.DoubleMetaphone;


import com.wcohen.secondstring.Jaro;
import com.wcohen.secondstring.StringWrapper;

import me.xdrop.fuzzywuzzy.FuzzySearch;



class FileIO3
{
	public void writeToFile(String s1, String s2, String s3, String s4)
	{
		
		try
		{
			FileWriter myWriter = new FileWriter("/home/guddu/Dedupe_anirban/dedupe_test_cases_bhartiAxa_output.tsv",true);
			//FileWriter myWriter = new FileWriter("/home/guddu/Dedupe_anirban/test_output.tsv",true);
			//FileWriter myWriter = new FileWriter("/home/guddu/Dedupe_anirban/final_test_set_output.tsv",true);
			myWriter.write(s1+'\t'+s2+'\t'+s3+'\t'+s4+'\n');
			//System.out.println("Wrote to file");
			myWriter.close();
	    } 
		catch (IOException e) 
		{
			System.out.println("An error occurred in file writing.");
			e.printStackTrace();
		}
	      
	}
}
class Similarity3
{
	
	public static String preprocess_word(String name,String s)
	{
	
	    name= name.toLowerCase();
	    name= name.replaceAll("\\p{Punct}", "");
	    name= name.trim();
	    String[] words = name.split("\\s+");
	
	    if (s=="sort")
	    {
	    	//System.out.println("Going to sort");
	    	Arrays.sort(words);
	    }
	    
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
	
	public static String print_initials(String name)
	{
	    if(name.length()==0)
	        return "";
	    name=" "+name;
	    
	    String c="";
	    name=name.toUpperCase();
	    
	    for(int i=0;i<name.length()-1;i++)
	    {
	        
	        if(name.charAt(i)==' ')
	        {
	            char a= name.charAt(i+1);
	            c=c+a;
	        }
	    }
	    
	    return c;
	
	}
	
	public static double initial_Match(String name1,String name2)
	{
	    String abbrv1=print_initials(name1);
	    String abbrv2=print_initials(name2);
	
	    double initialMscore= (double)(FuzzySearch.ratio(abbrv1,abbrv2))/(double)(100);
	    
	    return initialMscore;
	}
	
	public List<Double> match_name(String name1,String name2)
	{
	    String[] words1 = name1.split("\\s");
	    String[] words2 = name2.split("\\s");
	    String res_name = new String("");
	    String name3 = new String("");
	    int l1 = words1.length;
	    int l2 = words2.length;
	    
	    if (l1<l2)
	    {
	    	name3 = name1;
	    	name1 = name2;
	    	name1 = name3;
	    }
	    else
	    	;
	    double r = 0.00f;
	    double t = 0.00f;
	    if (l1>=0 && l2>=0)
	    {
	    	for(int i=0; i<l2; i++)
	            {
	                if(Arrays.asList(words1).contains(words2[i])==true)
	                {
	                    res_name= res_name+" "+words2[i];
	                }
	            }
	            
	        }
	    	res_name = res_name.trim();
	    	String[] res_words = res_name.split("\\s");
	    	int d = res_words.length;
	    	
	        if (d>=0 && (d<=l1 || d<=l2))
	        {
	        	t = Math.max(l1,l2);
	        	r = (d/t);      
	        }
	        else if (d==0)
	        {
	        	t = Math.max(l1,l2);
	        	r = 0.00f;  
	        }
	        
	  return Arrays.asList(r,t);
	
	}
	
	public double findNameSim(String name1, String name2) 
	{
	    if(name1.toLowerCase().equals("nan")|| name2.toLowerCase().equals("nan"))
	    {
	        return -0.5;
	    }
	    if (name1.length()==0 || name2.length()==0)
			return -0.5;
		
	    String s = "sort";
	    String sn = "no sort";
	    String n1= preprocess_word(name1,s);
	    String n2= preprocess_word(name2,s);
	    
	    List<Double> all_name_match = match_name(n1,n2);
	    Object all_name_match1 = all_name_match.get(0);
	    Object all_name_match2 = all_name_match.get(1);
	    Double all_name_match11 = (Double) all_name_match1;
	    Double all_name_match22 = (Double) all_name_match2;
	    
	    DoubleMetaphone m = new DoubleMetaphone();
	    String p1,s1,p2,s2;
	    p1 = m.doubleMetaphone(n1,false);
	    s1 = m.doubleMetaphone(n1,true) ;
	    p2 = m.doubleMetaphone(n2,false) ;
	    s2 = m.doubleMetaphone(n2,true) ;  
	
	    double dm1= (double)FuzzySearch.ratio(p1,p2)/(double)100;
	    double dm2= (double)FuzzySearch.ratio(s1,p2)/(double)100;
	    double dm3= (double)FuzzySearch.ratio(p1,s2)/(double)100;
	    double dm4= (double)FuzzySearch.ratio(s1,s2)/(double)100;
	    double final_dm= Math.max(dm1,dm2);
	    final_dm= Math.max(final_dm, dm3);
	    final_dm= Math.max(final_dm,dm4);
	    //System.out.println("P1 S1 P2 S2: "+p1+" "+s1+" "+p2+" "+s2);
	    //System.out.println("DM1 DM2 DM3 DM4: "+dm1+" "+dm2+" "+dm3+" "+dm4);
	    double initialscore = initial_Match(n1,n2);
	    
	    double fuzzscore = (double)(FuzzySearch.ratio(n1,n2))/(double)(100);
	
	    String n3= preprocess_word(name1,sn);
	    String n4= preprocess_word(name2,sn);
	    
	    DoubleMetaphone m1 = new DoubleMetaphone();
	    String p11,s11,p22,s22;
	    p11 = m1.doubleMetaphone(n3,false);
	    s11 = m1.doubleMetaphone(n3,true) ;
	    p22 = m1.doubleMetaphone(n4,false) ;
	    s22 = m1.doubleMetaphone(n4,true) ;  
	
	    double dm11= (double)FuzzySearch.ratio(p11,p22)/(double)100;
	    double dm22= (double)FuzzySearch.ratio(s11,p22)/(double)100;
	    double dm33= (double)FuzzySearch.ratio(p11,s22)/(double)100;
	    double dm44= (double)FuzzySearch.ratio(s11,s22)/(double)100;
	    double final_dm1= Math.max(dm11,dm22);
	    final_dm1= Math.max(final_dm1, dm33);
	    final_dm1= Math.max(final_dm1,dm44);
	    
	    double initialscore1 = initial_Match(n3,n4);
	    
	    double fuzzscore1 = (double)(FuzzySearch.ratio(n3,n4))/(double)(100);
	    double final1,final2;
	    String flag;
	    
	    
	    if (all_name_match11>=0.6)
	    {
	        final1 = (0.5*fuzzscore +0.25*final_dm + 0.25*initialscore)*100; 
	        final2 = (0.5*fuzzscore1 +0.25*final_dm1 + 0.25*initialscore1)*100;
	        flag = "0";
	    }
	    else if (all_name_match11>=0.5 && all_name_match22==4.0)
	    {
	        final1 = (fuzzscore)*100; 
	        final2 = (fuzzscore1)*100;
	        flag = "1.5";
	    }
	    else if (all_name_match11>=0.5 && all_name_match22!=4.0)
	    {
	        final1 = (0.5*fuzzscore +0.5*final_dm)*100; 
	        final2 = (0.5*fuzzscore1 +0.5*final_dm1)*100;
	        flag = "1";
	    }
	    else if (all_name_match11<=0.5 && all_name_match22==4.0)
	    {
	        final1 = (fuzzscore)*100; 
	        final2 = (fuzzscore1)*100;
	        flag = "1.5";
	    }
	    else 
	    {
	        final1 = (0.5*fuzzscore+0.3*final_dm+0.2*initialscore)*100;
	        final2 = (0.5*fuzzscore1+0.3*final_dm1+0.2*initialscore1)*100;
	        flag = "2";
	    }
	    
	    double final_name_score = Math.max(final1,final2);
	    //System.out.println("final_name_score");
	    //System.out.println(final_name_score);
	    return final_name_score/100;
	}
	public double findSim(String s1, String s2)
	{
		double tmp=-1;
		System.out.println("OK");
		System.out.println(s2.toLowerCase());
		if ((s1.length()==0 || s2.length()==0||s1.toLowerCase().equals("na")||s2.toLowerCase().equals("na")))
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
		if (s1.length()==0 || s2.length()==0||s1.toLowerCase().equals("na")||s2.toLowerCase().equals("na"))
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
	
	//Yash to check: Method to do fuzzy subset matching (not required for CHOICE)
	/*public static boolean fuzzySubset(String name1,String name2)
	{
		double tmp=1;
		String[] l1;
		String[] l2;
		if (name1.length()==0 || name2.length()==0)
			return false;
		//If the first name is longer, assign it to l1
		if (name1.length() >= name2.length())
		{
			l1 = name1.split(",");
			l2 = name2.split(",");
		}
		//If the second name is longer, assign it to l1
		else
		{
			l1 = name2.split(",");
			l2 = name1.split(",");
		}	
		//Check if all words in the shorter name is 
		for (String str2: l2)	//For each word in the short name
		{
			int found = 0;	//Word not found
			for (String str1: l1)	//Check each word in the long name and see if a match is there
			{
				StringWrapper sw1 = new StringWrapper(str1);
				StringWrapper sw2 = new StringWrapper(str2);
				Jaro jro = new Jaro();
				double simscore = jro.score(sw1, sw2);
				if (simscore>tmp)
				{
					found = 1;
					break;
				}
			}
			//A word was not found in the longer name
			if (found == 0)
				return false;
		}
		return true;
	}*/
}





public class TestCheck3
{
	//Yash: Method to normalize scores between 4 and 9 (PM range) when one of the strong ID attributes mismatch
	public static double normalizeForPM(double score,double min,double max)
	{
		//Find the normalized score between 0 and 1
		double norm_score = (score-min)/(max-min);
		//Multiply it with 9 (partial match upper limit)
		double new_score = norm_score*9;
		//Return it
		return(new_score);
	}
	/*Yash: If two or more attributes have sim score above 0.97, return true*/
	//Except dob, gender, and ID
	public static boolean doTwoAttrMatch(double sim_array[])
	{
		int count_match=0,i;
		if (sim_array[0]>=0.85)	//Yash: If name matches, see if at least another attribute matches or not
		{
			
			count_match=1;
			for (i = 1; i < sim_array.length; i++)
			{
				//We keep the match threshold very high here (0.97) since this block is executed when there is an ID mismatch
				if((sim_array[i]>0.97)&&(sim_array[i]!=-0.5))
				{
					count_match=count_match+1;
					//Make coefficients of matching attributes 5
					//coeff_array[i]=5;
				}
			}
		}
		if(count_match>=2)
			return true;
		return false;
	}
	
	
	/*Main function*/
	public static void main(String[] args)
	throws Exception
	{
		int coeff_id=0,coeff_address2,coeff_email,coeff_name=0,coeff_fname=0,coeff_mname=0,coeff_address,coeff_contact, coeff_pan,coeff_passport, coeff_ckyc, coeff_voterid, coeff_dob, coeff_gender,numlines=0, acc=0;
		double final_score=0;
		double sim_array[]=new double[50];
		double coeff_array[]=new double[50];
		double max=33, min=-16;
		
		BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Dedupe_anirban/dedupe_test_cases_bhartiAxa.tsv"));  
		//BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Dedupe_anirban/test.csv"));
		//BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Dedupe_anirban/final_test_set.csv"));
		
		String line="";
		int linecount=0;
		
		String final_label="X";
		
		//Create output file to write special cases (not required in main implementation)
		//FileWriter myWriter2 = new FileWriter("/home/guddu/Dedupe_anirban/final_test_set_nonmatch.tsv");
		FileWriter myWriter2 = new FileWriter("/home/guddu/Dedupe_anirban/dedupe_test_cases_bhartiAxa_nonmatch.tsv");
		while ((line = br.readLine()) != null)   //returns a Boolean value  
		//while (sc.hasNext())  //returns a boolean value  
		{  
			System.out.println(line);
			linecount+=1;
			//System.out.println(linecount);
			String[] l = line.split("\t"); 
			//System.out.println(l[19]);
			
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
		    Similarity3 s = new Similarity3();
		    double namesim = s.findNameSim(fullname_left,fullname_right);
		    double fnamesim = s.findNameSim(fatherfullname_left,fatherfullname_right);
		    double mnamesim = s.findNameSim(motherfullname_left,motherfullname_right);
		    //System.out.println(dob_left);
		    //System.out.println(dob_right);
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
		    double namesim2=0,fnamesim2=0,mnamesim2=0,addrsim2=0,idsim2=0;
		   //System.out.println(dobsim);
		   //System.out.println(gendersim);
		    FileIO3 fio=new FileIO3();
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
				else
				{
					String [] namelist_left=fullname_left.split(" ");
					String [] namelist_right=fullname_right.split(" ");
					Set<Object> nameset_left = Arrays.stream(namelist_left).collect(Collectors.toSet());
					Set<Object> nameset_right = Arrays.stream(namelist_right).collect(Collectors.toSet());
					
					String firstname_left=namelist_left[0];
					String firstname_right=namelist_right[0];
					String lastname_left=namelist_left[namelist_left.length-1];
					String lastname_right=namelist_right[namelist_right.length-1];
					
					//If it's a female, relax if the first name matches
					if (gender_left.equals("F"))
					{
						if (namesim!=-0.5)
						{
							//If the first name of the woman matches, increase name coefficient and similarity
							if(firstname_left.equals(firstname_right))
							{
								namesim=0.85; //First name matches. Force increase namesim for female
								coeff_name=3;
							}
							//Else if one of the first names is a single letter, and it matches with the first letter of the other first name, increase name coefficient and similarity
							else if (((firstname_left.length()==1)||(firstname_right.length()==1))&&(firstname_left.charAt(0)==firstname_right.charAt(0)))
							{
								namesim=0.85;
								coeff_name=3;
							}
							else
							{
								namesim2 = 1-namesim;
								coeff_name=-4;
							}
						}
						else
							coeff_name=0;
					}
					//If it's a male
					else
					{
						
						if (namesim!=-0.5)
						{
							//Yash: If the namelists are subset of each other, increase the name coefficient and similarity
							if((nameset_left.containsAll(nameset_right)||nameset_right.containsAll(nameset_left)))
							{
								namesim=0.85;//Yash
								coeff_name=3;
							}
							//If the first name is a single alphabet, and abbreviations and last name match, increase name coefficient and similarity
							else if ((firstname_left.length()==1||firstname_right.length()==1)&&(Similarity3.doAbbrvMatch(fullname_left, fullname_right)))
							{
								if (lastname_left.equals(lastname_right))
								{
									namesim=0.85;//Yash
									coeff_name=3;
								}
							}
							//If the last name is a single alphabet, and abbreviations and first name match, increase name coefficient and similarity
							else if ((lastname_left.length()==1||lastname_right.length()==1)&&(Similarity3.doAbbrvMatch(fullname_left, fullname_right)))
							{
								if (firstname_left.equals(firstname_right))
								{
									namesim=0.85;//Yash
									coeff_name=3;
								}
							}
							else
							{
								namesim2=1-namesim;
								coeff_name=-4;
							}
								
							
						}
						else
							coeff_name=0;
					}
				}
				/************************************************************/
				//Yash: Address match
				/***********************************************************/
				/*if (addrsim>0.95)	//Same house
					coeff_address=2;
				else if (addrsim<=0.95 && addrsim!=-0.5)
					coeff_address=-1;	//Changed address
				else
					coeff_address=0;*/
				//Address2 coeff
				if (addrsim>0.7)	//Same house
					coeff_address2=2;
				else if (addrsim<=0.7 && addrsim!=-0.5)
				{
					addrsim2=1-addrsim;
					coeff_address2=-1;	//Changed address
				}
				else
					coeff_address2=0;
				/************************************************************/
				//Phone match
				/***********************************************************/
				if (phsim==1)
				{
					coeff_contact=5;	//Parents' phone number
					coeff_name=5;	//Yash: Phone is a strong attribute. Hence, increasing name weight.
				}
				else
					coeff_contact=0;	//Multiple contacts
				/************************************************************/
				//FATHER'S NAME
				/***********************************************************/
				
				if (fnamesim>=0.85)
					coeff_fname=2;	//Same name, different people
				//If namesim < 0.65, negative
				else if (fnamesim<0.65 && fnamesim!=-0.5)
				{
					fnamesim2=1-fnamesim;
					coeff_fname=-3;
				}
				//If namesim between 0.65 and 0.85
				else if ((fnamesim>=0.65)&&(fnamesim<0.85))
				{
					//If abbreviations are subset of each other, no contribution
					if (s.doAbbrvMatch(fatherfullname_left,fatherfullname_right))
						coeff_fname=0;
					//Else negative
					else
					{
						fnamesim2=1-fnamesim;
						coeff_fname=-3;
					}
						
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
				{	
					mnamesim2=1-mnamesim;
					coeff_mname=-3;
				}
				//If namesim between 0.65 and 0.85
				else if ((mnamesim>=0.65)&&(mnamesim<0.85))
				{
					//If abbreviations are subset of each other, no contribution
					if (s.doAbbrvMatch(motherfullname_left,motherfullname_right))
						coeff_mname=0;
					//Else negative
					else
					{
						mnamesim2=1-mnamesim;
						coeff_mname=-3;
					}
						
				}
				else
					coeff_mname=0;
				
				/************************************************************/
				//Yash: ID attribute match
				/***********************************************************/
				//If any one ID matches, high contribution
				if(pansim==1 || passportsim==1 || voteridsim==1 || ckycsim==1 || aadhaarsim==1 || drivinglicencesim==1 || eiasim==1)
				{
					idsim=1;
					coeff_id=10;	//Strong influence if any ID attribute matches
					coeff_name=5;	//Yash: Force increase name coefficient
				}
				//If no ID attributes are present
				else if ((pansim==-0.5)&& passportsim==-0.5 && voteridsim==-0.5 && ckycsim==-0.5 && aadhaarsim==-0.5 && drivinglicencesim==-0.5 && eiasim==-0.5)
				{
					System.out.println("HERE1");
					idsim=0;
					coeff_id=0;
				}
				//If one or more ID attributes are present, but do not exactly match
				else
				{
					//Take the strongest ID similarity as idsim (needs to be modified)
					if(pansim!=-0.5)
					{
						System.out.println("HERE2");
						idsim2=(1-pansim);//Yash
						coeff_id=-5;
					}
					else if(aadhaarsim!=-0.5)
					{
						idsim2=(1-aadhaarsim);//Yash
						coeff_id=-5;
					}
					else if(ckycsim!=-0.5)
					{
						idsim2=(1-ckycsim);//Yash
						coeff_id=-5;
					}
					else if(drivinglicencesim!=-0.5)
					{
						idsim2=(1-drivinglicencesim);//Yash
						coeff_id=0;	//Multiple driving licences might be there
					}
					else if(passportsim!=-0.5)
					{
						idsim2=(1-passportsim);//Yash
						coeff_id=0;	//Multiple passports might be there
					}
					
					else
					{
						idsim2=(1-voteridsim);//Yash
						coeff_id=0;	//Multiple voter IDs might be there
					}
					
				}
				/************************************************************/
				//Email match
				/***********************************************************/
				if (emailsim==1)
				{
					coeff_email=5;	//Sometimes wife provides husband's email
					coeff_name=5; //Yash: Email is a strong attribute. So, increasing name weight as well.
				}
				else if (emailsim<=0.9 && emailsim!=-0.5)
					coeff_email=0;
				else
					coeff_email=0;
				
				System.out.println("%%%%");
				System.out.println(namesim);
				System.out.println(fnamesim);
				System.out.println(mnamesim);
				System.out.println(addrsim);
				System.out.println(idsim);
				System.out.println(emailsim);
				System.out.println(phsim);
				
				/**************************************************************************************/
				/*Yash: Calculating final score*/
				/**************************************************************************************/
				
				sim_array[0]=namesim;
				sim_array[1]=fnamesim;
				sim_array[2]=mnamesim;
				sim_array[3]=addrsim;
				//sim_array[4]=idsim;
				sim_array[4]=emailsim;
				sim_array[5]=phsim;
				
				String reason="";
				//Yash: Final score calculation (address sim changed)
				double ns,fns,mns,ads,ids;
				if(namesim2!=0)
					ns=namesim2;
				else
					ns=namesim;
				if(fnamesim2!=0)
					fns=fnamesim2;
				else	
					fns=fnamesim;
				if(mnamesim2!=0)
					mns=mnamesim2;
				else
					mns=mnamesim;
				if(addrsim2!=0)
					ads=addrsim2;
				else
					ads=addrsim;
				if(idsim2!=0)
					ids=idsim2;
				else
					ids=idsim;
				
				
				
				final_score=coeff_name*ns+coeff_fname*fns+coeff_mname*mns+coeff_dob*dobsim+coeff_gender*gendersim+coeff_email*emailsim+coeff_address2*ads+coeff_contact*phsim+coeff_id*ids;
				//final_score=coeff_name*namesim+coeff_fname*fnamesim+coeff_mname*mnamesim+coeff_dob*dobsim+coeff_gender*gendersim+coeff_email*emailsim+coeff_address2*addrsim+coeff_contact*phsim+coeff_id*idsim;
				//Yash: If ID attributes don't match
				if(idsim<1)
				{
					//If any of the strong ID attributes mismatch
					if((pansim!=-0.5)||(aadhaarsim!=-0.5)||(ckycsim!=-0.5))
					{
						//If at least two other attributes match (name must match)
						if(doTwoAttrMatch(sim_array))
						{
							//Yash: Normalize score between 4 and 9
							final_score=normalizeForPM(final_score,min,max);
						}
					}
					
				}
				//If ID attribute matches
				else
					reason="id match";		   
			    
			    System.out.println("FINAL SCORE:");
			    System.out.println(final_score);
			    
			    if (final_score<=2)
				{
					final_label="X";
					//System.out.println("####");
					if(reason.equals(""))
						reason="Kinda nothing matches";
					fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
					//System.out.println("Wrote to file 1");
				}
				else if (final_score>=7)
				{
					final_label="1";
					if(reason.equals(""))
						reason="Score above 9";
					fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
					//System.out.println("Wrote to file 2");
					
				}	
				else if ((final_score<7) && (final_score>2)) 
				{
					
					if((pansim<0.97) && (pansim!=-0.5))	//Change to idsim
					{
						final_label="0";
						if(reason.equals(""))
							reason="Score between [4,9], but ID not matching";
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						//System.out.println("Wrote to file 3");
					}
					else if ((namesim<0.65))
					{
						final_label="X";	//partial match
						if(reason.equals(""))
							reason="Score between [4,9], but name not matching at all";
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						//System.out.println("Wrote to file 4");
					}
					else
					{
						
						final_label="0";
						if(reason.equals(""))
							reason="Score between [4,9], and not much problem with name/ID";
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						//System.out.println("Wrote to file 5");
					}
			    
			    
				}
				else
				{
					final_label="U";
					fio.writeToFile(line,String.valueOf(final_score),"U","UNKNOWN");
					//System.out.println("Wrote to file 5");
				}
		    
		
			}
		    else
		    {
		    	final_label="X";
		    	fio.writeToFile(line,"0","X","DOB/Gender did not match");
		    	//System.out.println("Wrote to file 6");
		    }
		    System.out.println("FINAL LABEL PREDICTED:");
		    System.out.println(final_label);
		    System.out.println("FINAL LABEL ACTUAL:");
		    System.out.println(labels);
		    if(final_label.equals(labels))
		    {
		    	//System.out.println("@@@");
		    	acc=acc+1;
		    }
		    else
		    {
		    	myWriter2.write(line+'\t'+String.valueOf(final_score)+'\t'+String.valueOf(final_label)+'\n');
		    }
		    numlines+=1;
		}
		//System.out.println("Accuracy1:");
		//System.out.println(acc);
		double acc2=((double)acc/numlines)*100;
		
		//System.out.println(numlines);
		System.out.println("Accuracy:");
		System.out.println(acc2);
		myWriter2.close();
			
	}  
}