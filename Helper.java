package rootpack;
public class Helper
{
	public static double normalizeFinalScore(double final_score,double old_min,double old_max,double new_min,double new_max)
	{
		double old_range = (old_max - old_min);  
		double new_range = (new_max - new_min);  
		double new_value = (((final_score - old_min) * new_range) / old_range) + new_min;
		//System.out.println(new_value);
		return(new_value);
	}
	//Yash: Method to normalize scores between PM range when one of the strong ID attributes mismatch
	public static double normalizeForPM(double score,double min,double max)
	{
		//Find the normalized score between 0 and 1
		double norm_score = (score-min)/(max-min);
		//Multiply it with 9 (partial match upper limit)
		double new_score = norm_score*max;
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
}