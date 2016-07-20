package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import enums.Estado;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;

@Scope(ScopeType.CONVERSATION)
@Name("buscarEntidadesFormController")
public class BuscarEntidadesFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7100280967969242700L;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	
	@In(value = "entityManager")
    EntityManager em;
	
	private Object codigoNivel = null;
	
	private SinEntidad sinEntidad = new SinEntidad();
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	Calendar c = Calendar.getInstance();
	private String from;
	private boolean primeraEntrada=true;
	
	public void init(){
		
		String consulta = "" + 
				"select s.aniAniopre " +
				"from SinEntidad s " +
				"where  s.nenCodigo=:nenCodigo";
				
		Query q = em.createQuery(consulta);
		q.setParameter("nenCodigo",new BigDecimal(codigoNivel.toString()));

		sinEntidad.setAniAniopre(new BigDecimal(q.getResultList().get(0).toString()));
		
		initNivelEntidad();
		search();
		
	}
	
	@SuppressWarnings("unchecked")
	public void search(){
		try{
			if(codigoNivel != null){
				sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(new BigDecimal(codigoNivel.toString()));
				
				List<SinNivelEntidad> nivelEntidads= em.createQuery("Select n from SinNivelEntidad n " +
						" where n.nenCodigo="+codigoNivel+" and n.activo=true").getResultList();
				if(!nivelEntidads.isEmpty())
					sinNivelEntidad = em.find(SinNivelEntidad.class, nivelEntidads.get(0).getIdSinNivelEntidad());
				else
					sinNivelEntidad= new SinNivelEntidad();
			}
			
			sinEntidadList.getSinEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			//sinEntidadList.getSinEntidad().setAniAniopre(sinEntidad.getAniAniopre());
			sinEntidadList.getSinEntidad().setEntNombre(sinEntidad.getEntNombre() != null 
					? sinEntidad.getEntNombre().trim()
					: null);
			 
			sinEntidadList.listaResultados();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void searchAll(){
		sinNivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		sinEntidad.setAniAniopre(new BigDecimal(c.get(Calendar.YEAR)));
		search();
	}
	
	public void findNivelEntidad(){
		if(sinNivelEntidad.getNenCodigo() != null){
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			sinNivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			
			if(sinNivelEntidad == null)
				sinNivelEntidad = new SinNivelEntidad();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String buscarNombreNivelEntidad(BigDecimal codigoNivel){
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT o FROM SinNivelEntidad o ");
		hql.append("WHERE o.nenCodigo = :codigo ");
		hql.append("ORDER BY o.aniAniopre DESC");
		Query q = em.createQuery(hql.toString()).setParameter("codigo", codigoNivel).setMaxResults(1);
		
		List<SinNivelEntidad> list = q.getResultList();
		if(!list.isEmpty()){
			return list.get(0).getNenNombre().toUpperCase();
		}
		
		return null;
	}
	
//	METODOS PRIVADOS
	private Integer anhoActual(){		 
		Query q = em.createQuery("select max(Entidad.anho) from Entidad Entidad ");
		Integer maxYear = (Integer) q.getSingleResult();		
		return maxYear;
		
	}
	
	private void initNivelEntidad(){
		if(sinNivelEntidad.getIdSinNivelEntidad() != null ){
			sinNivelEntidad = em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		}
	}

//	GETTERS Y SETTERS
	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}
	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}
	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}
	public void setCodigoNivel(Object codigoNivel) {
		this.codigoNivel = codigoNivel;
	}
	public Object getCodigoNivel() {
		return codigoNivel;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	
	
	
	
}
