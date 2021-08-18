package rootpack;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.language.DoubleMetaphone;
import me.xdrop.fuzzywuzzy.FuzzySearch;



public class NameMatcher
{
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
	
	public static List<Double> match_name(String name1,String name2)
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
	
	public static double findNameSim(String name1, String name2) 
	{
		System.out.println(name1);
	    System.out.println(name2);
		if(name1.toLowerCase().equals("nan")|| name2.toLowerCase().equals("nan")||name1.length()<2||name2.length()<2)
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
	    
	    
	    if (all_name_match11>=0.6)
	    {
	        final1 = (0.5*fuzzscore +0.25*final_dm + 0.25*initialscore)*100; 
	        final2 = (0.5*fuzzscore1 +0.25*final_dm1 + 0.25*initialscore1)*100;
	        //flag = "0";
	    }
	    else if (all_name_match11>=0.5 && all_name_match22==4.0)
	    {
	        final1 = (fuzzscore)*100; 
	        final2 = (fuzzscore1)*100;
	        //flag = "1.5";
	    }
	    else if (all_name_match11>=0.5 && all_name_match22!=4.0)
	    {
	        final1 = (0.5*fuzzscore +0.5*final_dm)*100; 
	        final2 = (0.5*fuzzscore1 +0.5*final_dm1)*100;
	        //flag = "1";
	    }
	    else if (all_name_match11<=0.5 && all_name_match22==4.0)
	    {
	        final1 = (fuzzscore)*100; 
	        final2 = (fuzzscore1)*100;
	        //flag = "1.5";
	    }
	    else 
	    {
	        final1 = (0.5*fuzzscore+0.3*final_dm+0.2*initialscore)*100;
	        final2 = (0.5*fuzzscore1+0.3*final_dm1+0.2*initialscore1)*100;
	        //flag = "2";
	    }
	    
	    double final_name_score = Math.max(final1,final2);
	    //System.out.println("final_name_score");
	    //System.out.println(final_name_score);
	    return final_name_score/100;
	}
}