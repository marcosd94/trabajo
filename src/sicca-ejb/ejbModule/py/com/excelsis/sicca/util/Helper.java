package py.com.excelsis.sicca.util;

import java.util.LinkedList;
import java.util.List;

public class Helper {
	 private static List<String> actionResult = new LinkedList<String>();

	    public static boolean checkStringOnly(String string)
	    {
	        for(int index=0; index < string.length(); index++)
	        {
	            try
	            {
	                Integer.valueOf(string.substring(index, index+1));
	                return false;
	            }
	            catch(Exception ex)
	            {
	            }
	        }
	        
	        return true;
	    }
	    
	    public static List<String> getActionResult()
	    {
	        if(actionResult.isEmpty())
	        {
	            actionResult.add("persisted");
	            actionResult.add("updated");
	            actionResult.add("removed");
	        }
	        return actionResult;
	    }
	    
	    public static String createComboBoxDescValue(String val1, String val2)
	    {
	        if (val1 == null) val1 = "";
	        if (val2 == null) val2 = "";

	        return val1.trim() + " - " + val2.trim();
	    }

	    public static String buildIN_SQLvalues(List<?> result)
	    {
	        if(result==null || result.isEmpty()) return null;
	        
	        String IN_values = "(";
	        for(int index=0; index < result.size(); index++)
	        {
	            IN_values += result.get(index).toString();
	            if(index+1<result.size())
	            {
	                IN_values += ", ";
	            }
	        }
	        
	        return IN_values + ")";
	    }
	    
}
