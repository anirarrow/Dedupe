package rootpack;
import com.wcohen.secondstring.StringWrapper;
import com.wcohen.secondstring.Jaro;
public class Similarity
{
	public static double findSim(String s1, String s2)
	{
		double tmp=-1;
		//System.out.println("OK");
		//System.out.println(s1.toLowerCase());
		//System.out.println(s2.toLowerCase());
		if ((s1.length()==0 || s2.length()==0||s1.toLowerCase().equals("na")||s2.toLowerCase().equals("na")||s1.toLowerCase().equals("- -")||s2.toLowerCase().equals("- -")))
		{
			//System.out.println("Returning -0.5");
			return -0.5;
		}
			
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
}