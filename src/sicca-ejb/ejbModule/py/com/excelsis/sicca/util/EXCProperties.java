package py.com.excelsis.sicca.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class EXCProperties extends Properties{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8855972819760732734L;
	private final static String NULL = "null";

    public EXCProperties()
    {
    }

    public EXCProperties(EXCProperties properties)
    {
        if (properties != null)
        {
            fillProperties(properties);
        }
    }

    public EXCProperties(Map<?, ?> map)
    {
        if (map != null)
        {
            fillProperties(map);
        }
    }

    private void fillProperties(EXCProperties properties)
    {
        Iterator<Object> iterator = properties.keySet().iterator();
        while (iterator.hasNext())
        {
            Object key = iterator.next();
            Object value = properties.get(key);

            super.put(key, value == null ? NULL : value);
        }
    }

    private void fillProperties(Map<?, ?> map)
    {
        Iterator<?> iterator = map.keySet().iterator();
        while (iterator.hasNext())
        {
            Object key = iterator.next();
            Object value = map.get(key);

            super.put(key, value == null ? NULL : value);
        }

    }

    @Override
    public synchronized Object get(Object key)
    {
        Object value = super.get(key);
        return (value == null || NULL.equals(value)) ? null : value;
    }

    @Override
    public synchronized Object put(Object key, Object value)
    {
        return super.put(key, value == null ? NULL : value);
    }

}

