package py.com.excelsis.sicca.util;

import javax.persistence.Transient;

public abstract class EntityBase implements EntityInterface {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 5493792828732614117L;
	protected boolean newElement = false;
	    protected boolean selected = false;
	    protected boolean hidden = false;

	    
	    @Override
	    public int hashCode()
	    {
	        return (getId() == null ? 0 : getId().hashCode());
	    }
	    
	    protected boolean compare(EntityInterface other)
	    {
	        return (getId() == other.getId());
	    }

	    @Transient
	    public boolean isNewElement()
	    {
	        return newElement;
	    }
	    
	    public void setNewElement(boolean newElement)
	    {
	        this.newElement = newElement;
	    }
	    
	    @Transient
	    public boolean isSelected() 
	    {
	        return selected;
	    }
	    
	    public void setSelected(boolean selected) 
	    {
	        this.selected = selected;
	    }
	    
	    @Transient
	    public boolean isHidden()
	    {
	        return hidden ;
	    }
	    
	    public void setHidden(boolean hidden) {
	        this.hidden = hidden;
	    }
	    
	    @Override
	    public boolean equals(Object object) {
	        // TODO: Warning - this method won't work in the case the id fields are
	        // not set
	        if (!(object instanceof EntityInterface)) {
	            return false;
	        }
	        EntityInterface other = (EntityInterface) object;
	        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
	            return false;
	        }
	        return true;
	    }
	    
}
