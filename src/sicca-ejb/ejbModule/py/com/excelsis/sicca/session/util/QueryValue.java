package py.com.excelsis.sicca.session.util;

import javax.persistence.TemporalType;

/**
 * 
 * @author erivas
 */
public class QueryValue {
	public Object value;
	public TemporalType temporalType;
	
	public QueryValue(Object value){
		this.value = value;
	}
	
	public QueryValue(Object value, TemporalType temporalType){
		this.value = value;
		this.temporalType = temporalType;
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public TemporalType getTemporalType() {
		return temporalType;
	}
	public void setTemporalType(TemporalType temporalType) {
		this.temporalType = temporalType;
	}

}
