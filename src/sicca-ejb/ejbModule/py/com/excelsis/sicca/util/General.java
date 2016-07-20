package py.com.excelsis.sicca.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.jboss.seam.util.Naming;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Referencias;

public class General {

	public static boolean isEmail(String correo) {
		
		Pattern pat = Pattern.compile("^\\.|^\\@");
        Matcher mat = pat.matcher(correo);
       
        if ( mat.find() )
        	return false;
		
        Pattern p = Pattern.compile("[a-zA-Z0-9]+[.[a-zA-Z0-9_-]+]*@[a-z0-9][\\w\\.-]*[a-z0-9]\\.[a-z][a-z\\.]*[a-z]$");
	    Matcher m = p.matcher(correo);
	    
	    return m.matches();
	    
	     
	}

	/**
	 * Valida si el parámetro es una fecha con el formato "dd/MM/yyyy".
	 * 
	 * @return true si cumple el formato, false en caso contrario.
	 */
	public static boolean isFechaValida(Date fechax) {
		try {

			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy",
					Locale.getDefault());
			formatoFecha.setLenient(false);
			String valor = formatoFecha.format(fechax);
			String[] campos = valor.split("/");
			if (new Integer(campos[2]) < 1000)
				return false;
			formatoFecha.parse(valor);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
		
	/**
	 * RECIBE EL AÑO A VERIFICAR , SI EL AÑO ESTA EN EL PARAMETRO ESPECIFICADO DE LA TABLA REFERENCIA
	 *  @return true SI ES EL AÑO VALIDO .
	 * **/
	@SuppressWarnings("unchecked")
	public static boolean validarAnio(Long anio){
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		emsta = emf.createEntityManager();
		if(anio.longValue()<0 || anio.toString().length()!=4)
			return false;
		
		List<Referencias> minimo= emsta.createQuery("Select r from Referencias r " +
				" where r.dominio='ANHO_MINIMO' and r.activo=true").getResultList();
		List<Referencias> maximo= emsta.createQuery("Select r from Referencias r " +
		" where r.dominio='ANHO_MAXIMO' and r.activo=true").getResultList();
		if(!minimo.isEmpty())
		{
			if(!maximo.isEmpty()){
				if(anio>=minimo.get(0).getValorNum().intValue() && anio<=maximo.get(0).getValorNum().intValue()){
					return true;
				}else
					return false;
			}else{
				if(anio>=minimo.get(0).getValorNum().intValue()){
					return true;
				}else
					return false;
			}
		}else{
			if(!maximo.isEmpty()){
				if( anio<=maximo.get(0).getValorNum().intValue()){
					return true;
				}else
					return false;
			}
		}
		
		return true;
		
		
	}
	
	public static String getNumber(String number) {
		double value;
		String numberFormat = "###,###,###,###";
		DecimalFormat formatter = new DecimalFormat(numberFormat);
		try {
			value = Double.parseDouble(number);
		} catch (Throwable t) {
			return null;
		}
		return formatter.format(value);
	}
	
	public static Integer anhoActual() {
		Calendar cal = Calendar.getInstance();
		return  cal.get(Calendar.YEAR);
	}
	
	@SuppressWarnings("unchecked")
	public static Long getIdParaguay() {
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		emsta = emf.createEntityManager();
		List<Pais> p =
			emsta.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}
	/**
	 * @return true en caso que la fecha este en el rango de Edad_minima y Edad_maxima
	 * **/
	public static boolean validarFechasNacimiento(Date fecha){
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			Integer anioActual=Integer.parseInt(sdfSoloAnio.format(new Date())) ;
			Integer anioNacimiento=Integer.parseInt(sdfSoloAnio.format(fecha)) ;
			Integer edad= anioActual-anioNacimiento;
			if(edad.intValue()<=edadMaxima().intValue()&& edad.intValue()>=edadMinima().intValue())
				return true;
	
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public static  Integer edadMaxima(){
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		emsta = emf.createEntityManager();
		List<Referencias> maxima= emsta.createQuery("Select m from Referencias m " +
				" where m.dominio='EDAD_MAXIMA_LEGAL' ").getResultList();
		if(!maxima.isEmpty())
			return maxima.get(0).getValorNum();
		return 60;//en caso que no exista dato 
	}
	@SuppressWarnings("unchecked")
	public static  Integer edadMinima(){
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		emsta = emf.createEntityManager();
		List<Referencias> minima= emsta.createQuery("Select m from Referencias m " +
				" where m.dominio='EDAD_MINIMA_LEGAL' ").getResultList();
		if(!minima.isEmpty())
			return minima.get(0).getValorNum();
		return 18;//en caso que no exista dato 
	}
	/**
	 * retorna true en caso que la cedula ingresada este correcta
	 * */
	public static   boolean validarDocIdentidad(String doc){
		 String patron ="^[a-zA-Z0-9]+$";
	        Pattern p = Pattern.compile(patron);
	        Matcher  matcher = p.matcher(doc);
	        if(!matcher.matches())
	            return false;
	       return true;
	}
	/**
	 * compara 2 fechas Si retorna valor mayor a 0 es porque la fecha 1 es mayor a la fecha 2
	 * */
	public static int compareDate(Date d1, Date d2) {
	    if (d1.getYear() != d2.getYear()) 
	        return d1.getYear() - d2.getYear();
	    if (d1.getMonth() != d2.getMonth()) 
	        return d1.getMonth() - d2.getMonth();
	    return d1.getDate() - d2.getDate();
	}
	public static Boolean sonFechasIguales(Date f1,Date f2){
		
		GregorianCalendar fecha1 = new GregorianCalendar();
		GregorianCalendar fecha2 = new GregorianCalendar();
		fecha1.set(f1.getYear(), f1.getMonth(), f1.getDate());
		fecha1.clear(GregorianCalendar.MILLISECOND);
		fecha1.clear(GregorianCalendar.SECOND);
		fecha1.clear(GregorianCalendar.MINUTE);
		fecha1.clear(GregorianCalendar.HOUR);
		fecha1.clear(GregorianCalendar.HOUR_OF_DAY);
		
		fecha2.set(f2.getYear(), f2.getMonth(), f2.getDate());
		fecha2.clear(GregorianCalendar.MILLISECOND);
		fecha2.clear(GregorianCalendar.SECOND);
		fecha2.clear(GregorianCalendar.MINUTE);
		fecha2.clear(GregorianCalendar.HOUR);
		fecha2.clear(GregorianCalendar.HOUR_OF_DAY);
	
		
		if(fecha1.equals(fecha2))
			return true;
		
		return false;
	}
	

	public static Long getIdNacionalidadParaguay() {
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		emsta = emf.createEntityManager();
		try {
			Long  idN =(Long)emsta.createQuery(" Select  de.idDatosEspecificos From DatosEspecificos  de "
					+ "  Where de.datosGenerales.descripcion = 'NACIONALIDAD' " +
					" And de.descripcion ='PARAGUAYA' and de.activo=true").getSingleResult();
			return idN;
		} catch (NoResultException e) {
		}
		
		

		return null;
	}
	 public static String getAntiguedadFechas(Date f1, Date f2){
		 GregorianCalendar desde = new GregorianCalendar();
		 GregorianCalendar hasta = new GregorianCalendar();
		
	    	if (f1 == null)
	    		return null;
	    	if(f2==null)
	    		f2= new Date();
	    	
	    	desde.setTime(f1);
	    	desde.clear(GregorianCalendar.MILLISECOND);
	    	desde.clear(GregorianCalendar.SECOND);
	    	desde.clear(GregorianCalendar.MINUTE);
	    	desde.clear(GregorianCalendar.HOUR);
	    	desde.clear(GregorianCalendar.HOUR_OF_DAY);
	    	hasta.setTime(f2);
	    	hasta.clear(GregorianCalendar.MILLISECOND);
	    	hasta.clear(GregorianCalendar.SECOND);
	    	hasta.clear(GregorianCalendar.MINUTE);
	    	hasta.clear(GregorianCalendar.HOUR);
	    	hasta.clear(GregorianCalendar.HOUR_OF_DAY);
	    	
	    	
	    	String anitiguedad = "";
	    	if (desde.get(Calendar.YEAR) == hasta.get(Calendar.YEAR)){
				int diff = 0;
				if (hasta.get(Calendar.MONTH) == desde.get(Calendar.MONTH)){
					diff = hasta.get(Calendar.DAY_OF_MONTH) - desde.get(Calendar.DAY_OF_MONTH);
					anitiguedad = diff + " día/s";
				}
				else{
					diff = hasta.get(Calendar.MONTH) - desde.get(Calendar.MONTH);
					anitiguedad = diff + " mes/es";
				}
			}
			else{
				int diff = hasta.get(Calendar.YEAR) - desde.get(Calendar.YEAR);
				anitiguedad = diff + " año/s";
			}
	    	
	    	return anitiguedad;
	    }
	
	
}
