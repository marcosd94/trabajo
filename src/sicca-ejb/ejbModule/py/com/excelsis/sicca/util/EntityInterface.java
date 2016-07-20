package py.com.excelsis.sicca.util;

import java.io.Serializable;
import java.util.Properties;

public interface EntityInterface extends Serializable{
	 public Serializable getId();
	    public Properties getProperties();
	    public void setProperties(Properties properties);

	    public boolean isSelected();
	    public void setSelected(boolean value);
	    
	    public boolean isNewElement();
	    public void setNewElement(boolean newElement);
}
