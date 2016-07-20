package py.com.excelsis.sicca.util;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;


import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;

@Name("siccaAppHelper")
@Scope(ScopeType.APPLICATION)
public class SICCAAppHelper extends Helper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 106586046738929658L;

	  public static Context getApplicationContext()
	    {
	        return Contexts.getApplicationContext();
	    }

	    public static FacesContext getFacesContextCurrentInstance()
	    {
	        return FacesContext.getCurrentInstance();
	    }

	    public static Map<String, Object> getSessionVarz()
	    {
	        return getFacesContextCurrentInstance().getExternalContext().getSessionMap();
	    }

	    public static ServletContext getServletCtx()
	    {
	        return (ServletContext) getFacesContextCurrentInstance().getExternalContext().getContext();
	    }

	    public static HttpServletResponse getResponse()
	    {
	        return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	    }

	    public static String removeListContextVar(String actionResult, String compare, String contextVarName)
	    {
	        if (compare.equals(actionResult) && getApplicationContext().isSet(contextVarName))
	        {
	            getApplicationContext().remove(contextVarName);
	        }
	        return actionResult;
	    }

	    public static String getBundleMessage(String keyName)
	    {
	        return SeamResourceBundle.getBundle().getString(keyName);
	    }

	    public static Locale getLocale()
	    {
	        return SeamResourceBundle.getBundle().getLocale();
	    }

	    public String getLocaleCode()
	    {
	        return getLocale().getLanguage();
	    }

	    @SuppressWarnings("unchecked")
	    public static List<EntityInterface> getList(EntityManager em, String clazzName)
	    {
	        List<EntityInterface> collection = null;

	        clazzName = fixName(clazzName);

	        try
	        {
	            collection = em.createQuery(" select o from " + clazzName + " o ").getResultList();

	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	            collection = collection == null ? new Vector<EntityInterface>() : collection;
	        }
	        return collection;
	    }

	    private static String fixName(String clazzName)
	    {
	        if (clazzName.contains("_"))
	        {
	            String[] split = clazzName.split("_");

	            String element = split[0];
	            clazzName = String.valueOf(element.charAt(0)).toUpperCase();
	            clazzName += element.substring(1);
	            for (int index = 1; index < split.length; index++)
	            {
	                element = split[index];
	                clazzName += String.valueOf(element.charAt(0)).toUpperCase();
	                clazzName += element.substring(1);
	            }

	        }
	        return clazzName;
	    }

	    /**
	     * builds a selectItems values option with the given collection
	     * 
	     * @param entityCollection
	     * @param labelKeyName
	     * @return List<SelectItem>
	     */
	    public static List<SelectItem> buildSelectItems(List<? extends EntityInterface> entityCollection, String labelKeyName)
	    {
	        return buildSelectItems(entityCollection, labelKeyName, true);
	    }

	    /**
	     * 
	     * @param entityCollection
	     * @param labelKeyName
	     * @param addFalseItem
	     * @return
	     */
	    public static List<SelectItem> buildSelectItems(List<? extends EntityInterface> entityCollection, String labelKeyName, boolean addFalseItem)
	    {
	        List<SelectItem> selectItems = new Vector<SelectItem>();

	        if (addFalseItem)
	        {
	            selectItems.add(new SelectItem(null, getBundleMessage("opt_select_one")));
	        }

	        for (EntityInterface row : entityCollection)
	        {
	            selectItems.add(new SelectItem(row.getId(), String.valueOf(row.getProperties().get(labelKeyName))));
	        }

	        return selectItems;
	    }

	    @SuppressWarnings("unchecked")
	    public static Boolean checkExistance(EntityManager em, Class<?> clazz, String accion, String keyDesc, String keyId, Object value, Serializable id)
	    {
	        if (value == null) { 
	            return null; 
	        }
	        
	        String hql = "SELECT o FROM " + clazz.getName() + " o where ";
	        if (value instanceof String)
	        {
	            hql += "lower(o." + keyDesc + ")='" + value.toString().trim().toLowerCase() + "'";
	        }
	        else
	        {
	            hql += "o." + keyDesc + "=" + value;
	        }

	        if (accion.equalsIgnoreCase("update"))
	        {
	            hql += " and " + keyId + "!=" + id + "";
	        }
	        List<EntityInterface> list = em.createQuery(hql).getResultList();

	        return !list.isEmpty();
	    }

	    public int getDatatableRowsPerView()
	    {
	        return SICCASessionParameters.SEARCH_MAX_RESULT;
	    }

	    public String getDateFormat()
	    {
	        return SICCASessionParameters.DATE_FORMAT;
	    }

}
