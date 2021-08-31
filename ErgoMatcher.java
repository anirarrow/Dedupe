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




class FileIO5
{
	public void writeToFile(String s1, String s2, String s3, String s4)
	{
		
		try
		{
			FileWriter myWriter = new FileWriter("/home/guddu/Lumiq/Ergo/Ergo_Aug19_test2_output.csv",true);
			//FileWriter myWriter = new FileWriter("/home/guddu/Lumiq/Ergo/Ergo_pairs_test2_output.csv",true);
			//FileWriter myWriter = new FileWriter("/home/guddu/Dedupe_anirban/test_output.tsv",true);
			//FileWriter myWriter = new FileWriter("/home/guddu/Dedupe_anirban/final_test_set_output_500s.tsv",true);
			//FileWriter myWriter = new FileWriter("/home/guddu/Dedupe_anirban/final_test_set_output_with_percentage.tsv",true);
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
public class ErgoMatcher
{	
	/*Main function*/
	public static void main(String[] args)
	throws Exception
	{
		int coeff_id=0,coeff_address2,coeff_email,coeff_name=0,coeff_contact, coeff_dob, coeff_gender,numlines=0, acc=0;
		int coeff_city=0,coeff_state=0,coeff_pin=0,coeff_chassis=0, coeff_reg=0; //Yash
		double final_score=0;
		double gendersim;
		double sim_array[]=new double[50];
		//max_fs and min_fs are the maximum and minimum values of final_score; minPM and maxPM are the lower and upper limits of PM; norm_score is the normalized score
		double max_fs=37, min_fs=-4,minPM=6,maxPM=10,norm_score=0; //norm_score=Final Percentage
		//The threshold defined by the team to calculate the percentages (0 to 50 is no match, 50 to 75 is partial match, etc.)
		double thr1=0,thr2=70,thr3=90,thr4=100;
		/*User to specify relaxed threshold for EM, for name,dob,and gender match*/
		double relaxed_PM_threshold=4;
		
		BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Lumiq/Ergo/Ergo_Aug19_test2.csv"));
		//BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Lumiq/Ergo/Ergo_pairs_test2.csv"));  
		//BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Lumiq/Ergo/test.csv"));
		//BufferedReader br = new BufferedReader(new FileReader("/home/guddu/Dedupe_anirban/final_test_set.csv"));
		
		String line="";
		int linecount=0;
		
		String final_label="X";
		
		//Create output file to write special cases (not required in main implementation)
		//FileWriter myWriter2 = new FileWriter("/home/guddu/Lumiq/Ergo/test_output.tsv");
		//FileWriter myWriter2 = new FileWriter("/home/guddu/Lumiq/Ergo/Ergo_test_output.tsv");
		//FileWriter myWriter2 = new FileWriter("/home/guddu/Lumiq/Ergo/Ergo_sample_nomatch.tsv",true);
		FileIO5 fio=new FileIO5();
		//FileWriter myWriter2 = new FileWriter("/home/guddu/Dedupe_anirban/dedupe_test_cases_bhartiAxa_nonmatch.tsv");
		while ((line = br.readLine()) != null)   //returns a Boolean value  
		//while (sc.hasNext())  //returns a boolean value  
		{  
			if (linecount>0)
			{
				System.out.println(line);
			
			
				linecount+=1;
				//System.out.println(linecount);
				//Yash: Padding
				String[] l1 = line.split("\t");
				List<String> l = new ArrayList<>(Arrays.asList(l1));
				System.out.println(l.size());
				if (l.size()<31)
				{	
					//Padding
					for(int i=l.size();i<31;i++)
						l.add("na");
				}
					
				//System.out.println(l);
				
				//Anirban: new cases (to be handled later)
				//Left gender!=right gender: PM (if name and ID completely match, it is EM currently. If name does not match well, then no match currently.)
				//Left dob!=right dob: PM (if name and ID completely match, it is EM currently. If name does not match well, then no match currently.)
				//Only ID and name are present, and name mismatches fully, but ID exact match: PM
				//Only name and Chassis match (all other mismatch or null): EM? PM?
				//Name, Email, Phone match: Cannot be EM in Ergo
				//ID matches, name does not match at all, no other attr present: PM? NM?
				//Name, dob, gender match; nothing else matches: PM?
				//Name and address match; nothing else matches: PM?
				
				
				
				//Left tuple
				String policynum_left=l.get(0).replace("\n", "").replace("\r", "");		    
				String customercode_left=l.get(1).replace("\n", "").replace("\r", "");		
			    String fullname_left=l.get(2).replace("\n", "").replace("\r", "");
			    String dob_left=l.get(3).replace("\n", "").replace("\r", "");
			    String gender_left=l.get(4).replace("\n", "").replace("\r", "");
			    String chassisnum_left=l.get(5).replace("\n", "").replace("\r", "");
			    String regnum_left=l.get(6).replace("\n", "").replace("\r", ""); 
			    String contactnumbers_left=l.get(7).replace("\n", "").replace("\r", "");
			    String email_left=l.get(8).replace("\n", "").replace("\r", "");
			    String pan_left=l.get(9).replace("\n", "").replace("\r", "");
			    String pin_left=l.get(10).replace("\n", "").replace("\r", "");
			    String city_left=l.get(11).replace("\n", "").replace("\r", "");
			    String state_left=l.get(12).replace("\n", "").replace("\r", "");
			    String addresses_left=l.get(14).replace("\n", "").replace("\r", "");
			    
			    
			    
			   //Right tuple
			    String policynum_right=l.get(16).replace("\n", "").replace("\r", "");		    
				String customercode_right=l.get(17).replace("\n", "").replace("\r", "");		
			    String fullname_right=l.get(18).replace("\n", "").replace("\r", "");
			    String dob_right=l.get(19).replace("\n", "").replace("\r", "");
			    String gender_right=l.get(20).replace("\n", "").replace("\r", "");
			    String chassisnum_right=l.get(21).replace("\n", "").replace("\r", "");
			    String regnum_right=l.get(22).replace("\n", "").replace("\r", "");
			    String contactnumbers_right=l.get(23).replace("\n", "").replace("\r", "");
			    String email_right=l.get(24).replace("\n", "").replace("\r", "");
			    String pan_right=l.get(25).replace("\n", "").replace("\r", "");
			    String pin_right=l.get(26).replace("\n", "").replace("\r", "");
			    String city_right=l.get(27).replace("\n", "").replace("\r", "");
			    String state_right=l.get(28).replace("\n", "").replace("\r", "");
			    String addresses_right=l.get(30).replace("\n", "").replace("\r", "");
			    
			    //Find similarity between pairs of attributes
			    double namesim = NameMatcher.findNameSim(fullname_left,fullname_right);
			    double dobsim = JaroSimilarity.findSim(dob_left,dob_right);
			    double gendersimilarity = JaroSimilarity.findSim(gender_left,gender_right);
			    
			    //If gendersimilarity is above 0.8, there is a spelling mistake may be. Hence, gendersim
			    //is the direct Jaro score. Else, the genders are different actually.
			    if(gendersimilarity>0.8)
			    	gendersim=gendersimilarity;
			    else
			    	gendersim=0;
			    //Updated Address Matcher
			    double addrsim = Address.findAddSim(addresses_left,addresses_right);
			    double pinsim = JaroSimilarity.findSim(pin_left,pin_right);
			    double statesim = JaroSimilarity.findSim(state_left,state_right);
			    double citysim = JaroSimilarity.findSim(city_left,city_right);
			    double pansim = JaroSimilarity.findSim(pan_left,pan_right);
			    double emailsim = JaroSimilarity.findSim(email_left,email_right);
			    double phsim = PhoneMatcher.findSim_phone(contactnumbers_left,contactnumbers_right);
			    double regsim = JaroSimilarity.findSim(regnum_left,regnum_right);
			    double chassissim = JaroSimilarity.findSim(chassisnum_left,chassisnum_right);
			    
			    double namesim2=0;
			   
			    
			    
			    /************************************************************/
				//DOB and GENDER: Yash
				/***********************************************************/
				if(dobsim==1)
					coeff_dob=1;
				else
					coeff_dob=0;
				if(gendersim==1)
					coeff_gender=1;
				else
					coeff_gender=0;
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
					if (gender_left.equals("FEMALE"))
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
								namesim2 = 1-namesim;	//Yash
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
							else if ((firstname_left.length()==1||firstname_right.length()==1)&&(NameMatcher.doAbbrvMatch(fullname_left, fullname_right)))
							{
								if (lastname_left.equals(lastname_right))
								{
									namesim=0.85;//Yash
									coeff_name=3;
								}
							}
							//If the last name is a single alphabet, and abbreviations and first name match, increase name coefficient and similarity
							else if ((lastname_left.length()==1||lastname_right.length()==1)&&(NameMatcher.doAbbrvMatch(fullname_left, fullname_right)))
							{
								if (firstname_left.equals(firstname_right))
								{
									namesim=0.85;//Yash
									coeff_name=3;
								}
							}
							else
							{
								namesim2=1-namesim;	//Yash
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
				//Address coeff
				if (addrsim>0.7)	//Same house
				{
					coeff_address2=2;
				}
				else
					coeff_address2=0;
				
				//City matching
				if (citysim>0.7)	//Same house
				{
					coeff_city=1;
				}
				else
					coeff_city=0;
				
				//State matching
				if (statesim>0.7)	//Same house
				{
					coeff_state=1;
				}
				else
					coeff_state=0;
				//Pin matching
				if (pinsim>0.7)	//Same house
				{
					coeff_pin=2;
				}
				else
					coeff_pin=0;
				/************************************************************/
				//Chassis number match
				/***********************************************************/
				if (chassissim==1)	//Anirban: Might change
					coeff_chassis=2;
				else
					coeff_chassis=0;	//Multiple chassis numbers can be there with one person
				/************************************************************/
				//Registration number match
				/***********************************************************/
				if (regsim==1)
					coeff_reg=2;
				else
					coeff_reg=0;

					
				/************************************************************/
				//Phone match
				/***********************************************************/
				if (phsim==1)	//Anirban: Note down
				{
					coeff_contact=5;	//Parents' phone number
					if(namesim>=0.7)	//Yash
					{
						namesim2=0;
						coeff_name=5;
					}
						//Yash: Phone is a strong attribute. Hence, increasing name weight.
				}
				else
					coeff_contact=0;	//Multiple contacts	
				
				/************************************************************/
				//Yash: ID attribute match
				/***********************************************************/
				//If any one ID matches, high contribution
				double idsim=pansim;
				if(idsim==1)	//Note: should Chassis Number be considered too?
				{
					coeff_id=10;	//Strong influence if any ID attribute matches
					if(namesim>=0.7)	//Yash
					{
						namesim2=0;
						coeff_name=5;
					}	//Yash: Force increase name coefficient
				}
				//If no ID attributes are present
				else
					//System.out.println("HERE1");
					coeff_id=0;
				
				
				/************************************************************/
				//Email match
				/***********************************************************/
				if (emailsim==1)//Anirban: Note down
				{
					coeff_email=5;	//Sometimes wife provides husband's email
					if(namesim>=0.7)	//Yash
					{
						namesim2=0;
						coeff_name=5;
					} //Yash: Email is a strong attribute. So, increasing name weight as well.
				}
				else
					coeff_email=0;
				
				/**************************************************************************************/
				/*Yash: Calculating final score*/
				/**************************************************************************************/
				
				sim_array[0]=namesim;
				sim_array[1]=addrsim;
				sim_array[2]=emailsim;
				sim_array[4]=phsim;
				sim_array[5]=regsim;
				sim_array[6]=chassissim;
				sim_array[7]=citysim;
				sim_array[8]=statesim;
				sim_array[9]=pinsim;
				
				String reason="";
				//Yash: Final score calculation (address sim changed)
				//For negative contribution attributes, do (1-sim) for a certain threshold
				double ns;	//Yash
				if(namesim2!=0)
					ns=namesim2;
				else
					ns=namesim;
				
				
				
				
				final_score=coeff_name*ns+coeff_dob*dobsim+coeff_gender*gendersim+coeff_email*emailsim+coeff_address2*addrsim+coeff_contact*phsim+coeff_id*idsim+coeff_city*citysim+coeff_state*statesim+coeff_pin*pinsim+
				coeff_reg*regsim+coeff_chassis*chassissim;
				
				System.out.println("****************************************************");
				System.out.println("Name, address, email, phone, id, chassis components");
				System.out.println("****************************************************");
				//System.out.println(coeff_name);
				System.out.println(coeff_name*ns);
				//System.out.println(coeff_address2);
				System.out.println(coeff_address2*addrsim);
				//System.out.println(coeff_email);
				System.out.println(coeff_email*emailsim);
				//System.out.println(coeff_contact);
				System.out.println(coeff_contact*phsim);
				//System.out.println(coeff_id);
				System.out.println(coeff_id*idsim);
				System.out.println(chassissim);
				System.out.println("*******************************************");
				
				//Yash: Here, we allow the user to decide if it's an exact match, in case only the name, dob, and gender match.
				if((namesim>0.85)&&(dobsim==1)&&(gendersim==1)&&(addrsim==0)&&(idsim==0)&&(emailsim==0)&&(phsim==0))
				{
					if(final_score>=relaxed_PM_threshold)
					{	
						final_label="1";	//Yash: To make it PM
						if(reason.equals(""))
							reason="Relaxed match for Name, dob, gender"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim);
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						
					}
				}
				
				//final_score=coeff_name*namesim+coeff_fname*fnamesim+coeff_mname*mnamesim+coeff_dob*dobsim+coeff_gender*gendersim+coeff_email*emailsim+coeff_address2*addrsim+coeff_contact*phsim+coeff_id*idsim;
				//Yash: If ID attributes don't match
				if(idsim<1)
				{
					//If any of the strong ID attributes mismatch
					if(pansim!=-0.5)
					{
						//If at least two other attributes match (name must match)
						if(Helper.doTwoAttrMatch(sim_array))
						{
							//Yash: Normalize score between minPM and maxPM
							final_score=Helper.normalizeForPM(final_score,minPM,maxPM);
						}
					}
					
				}
				//If ID attribute matches
				//Yash to check whole block
				else
				{
					norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr3,thr4);
					reason="id match"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
				}
							   
			    
			    System.out.println("FINAL SCORE:");
			    System.out.println(final_score);
			    
			    
			    if (final_score<=minPM)
				{
					final_label="X";
					norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr1,thr2);	//Yash
					//System.out.println("####");
					if(reason.equals(""))
						reason="Kinda nothing matches"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
					fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
					System.out.println("NORMALIZED SCORE:");
					System.out.println(norm_score);
				}
				else if (final_score>=maxPM)
				{
					if ((namesim<0.65))	//Yash
					{
						final_score=Helper.normalizeForPM(final_score,minPM,maxPM);	//Yash
						final_label="0"; //Yash: Forcing it to be a PM because name is severely mismatching
						norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr2,thr3);
						if(reason.equals(""))
							reason="Score above EM threshold, but name not matching at all"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						System.out.println("NORMALIZED SCORE:");
						System.out.println(norm_score);
					}
					else
					{
						final_label="1";
						norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr3,thr4); //Yash
						if(reason.equals(""))
							reason="Score above EM threshold"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						System.out.println("NORMALIZED SCORE:");
						System.out.println(norm_score);
					}
					
				}
			    
				else if ((final_score<maxPM) && (final_score>minPM)) 
				{
					
					if((pansim<0.97) && (pansim!=-0.5))	//Change to idsim
					{
						final_label="0";
						norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr2,thr3); //Yash
						if(reason.equals(""))
							reason="Score between PM range, but ID not matching"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						System.out.println("NORMALIZED SCORE:");
						System.out.println(norm_score);
					}
					else if ((namesim<0.65))
					{
						final_label="X";
						norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr1,thr2);	//Yash
						if(reason.equals(""))
							reason="Score between PM range, but name not matching at all"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						System.out.println("NORMALIZED SCORE:");
						System.out.println(norm_score);
					}
					else
					{
						
						final_label="0";
						norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr2,thr3);	//Yash
						if(reason.equals(""))
							reason="Score between PM range, and not much problem with name/ID"+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim)+'\t'+String.valueOf(pinsim)+'\t'+String.valueOf(citysim)+'\t'+String.valueOf(statesim)+'\t'+String.valueOf(regsim)+'\t'+String.valueOf(chassissim)+'\t'+String.valueOf(norm_score);
						fio.writeToFile(line,String.valueOf(final_score),String.valueOf(final_label),reason);
						System.out.println("NORMALIZED SCORE:");
						System.out.println(norm_score);
					}
			    
			    
				}
				else
				{
					final_label="U";
					norm_score = Helper.normalizeFinalScore(final_score,min_fs,max_fs,thr1,thr2);
					fio.writeToFile(line,String.valueOf(final_score),"U","UNKNOWN");
					System.out.println("NORMALIZED SCORE:");
					System.out.println(norm_score);
				}
			    
			
				
			    /*else
			    {
			    	final_label="X";
			    	norm_score = normalizeFinalScore(final_score,min_fs,max_fs,thr1,thr2);
			    	fio.writeToFile(line,"0","X","DOB/Gender did not match");
			    	System.out.println("NORMALIZED SCORE:");
					System.out.println(norm_score);
			    	//System.out.println("Wrote to file 6");
			    }
			    //System.out.println("FINAL LABEL PREDICTED:");
			    //System.out.println(final_label);
			    //System.out.println("FINAL LABEL ACTUAL:");
			    //System.out.println(labels);
			    if(final_label.equals(labels))
			    {
			    	//System.out.println("@@@");
			    	acc=acc+1;
			    }
			    else if(final_label.equals("0")&&((labels.equals("1"))||(labels.equals("X"))))
			    {
			    	mismatch_cnt+=1;
			    	//System.out.println();
			    }
			    else
			    {
			    	myWriter2.write(line+'\t'+String.valueOf(final_score)+'\t'+String.valueOf(final_label)+'\t'+String.valueOf(dobsim)+'\t'+String.valueOf(gendersim)+'\t'+String.valueOf(namesim)+'\t'+String.valueOf(emailsim)+'\t'+String.valueOf(phsim)+'\t'+String.valueOf(addrsim));
			    }*/
			    numlines+=1;
			}
			else
			{
				//Write headers here
				fio.writeToFile(line,"FINAL SCORE","PREDICTED","REASON"+'\t'+"DOBSIM"+'\t'+"GENDERSIM"+'\t'+"NAMESIM"+'\t'+"EMAILSIM"+'\t'+"PHONESIM"+'\t'+"ADDRESSSIM"+'\t'+"PINSIM"+'\t'+"CITYSIM"+'\t'+"STATESIM"+'\t'+"REGSIM"+'\t'+"CHASSISSIM"+'\t'+"PERCENTAGE");
				linecount+=1;
			}
				
			//System.out.println("Accuracy1:");
			//System.out.println(acc);
			//double acc2=((double)acc/numlines)*100;
			
			//System.out.println(numlines);
			//System.out.println("Accuracy:");
			//System.out.println(acc2);
			System.out.println(numlines);
			//System.out.println(mismatch_cnt);
			//myWriter2.close();
		}
			
	}  
}