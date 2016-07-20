package py.com.excelsis.sicca.session.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;



import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import enums.Estado;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.ConfiguracionUoDetList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("listarPersonasContratadasPermanenteFormController")
public class ListarPersonasContratadasPermanenteFormController implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ConfiguracionUoDetList configuracionUoDetList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	
	private ConfiguracionUoDet configuracionUoDet;
	private Estado estado = Estado.ACTIVO;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab;

	private String denominacion;
	private BigDecimal nenCod;
	
	private Integer orden;
	/**
	 * PARAMETROS QUE RECIBIRA
	 * */
	private Long idEntidad;
	private Long idSinNivelEntidad;
	private Long idConfiguracionUoCab;


	public void init(){
		try {
			configuracionUoDet= new ConfiguracionUoDet();
			denominacion=null;
			cargarCampos();//EN EL CASO QUE RECIBA PARAMETRO
			search();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	private void cargarCampos(){
		if(idSinNivelEntidad!=null){
			nivelEntidad = em.find(SinNivelEntidad.class,idSinNivelEntidad);
			
		}else
			nivelEntidad= new SinNivelEntidad();
		if (idEntidad != null) {
			entidad= em.find(Entidad.class, idEntidad);
			sinEntidad = entidad.getSinEntidad();
			

		}else{
			sinEntidad= new SinEntidad();
			entidad= new Entidad();
		}
		
		if(idConfiguracionUoCab!=null){
			configuracionUoCab=em.find(ConfiguracionUoCab.class,idConfiguracionUoCab);
		}else
			configuracionUoCab=new ConfiguracionUoCab();
		
	}
	
	
	public void search(){
		if(denominacion != null && !denominacion.isEmpty())
			configuracionUoDetList.getConfiguracionUoDet().setDenominacionUnidad(denominacion.trim());
		if(entidad!=null && entidad.getIdEntidad() != null)
			configuracionUoDetList.setIdEntidad(entidad.getIdEntidad());
		if(nivelEntidad!=null && nivelEntidad.getNenCodigo() != null)
			configuracionUoDetList.setNenCodigo(nivelEntidad.getNenCodigo());
		if(configuracionUoCab!=null && configuracionUoCab.getOrden()!=null)
			configuracionUoDetList.setOrden(configuracionUoCab.getOrden());

		configuracionUoDetList.getResultList();
	}
	
	public void searchAll(){
		try {
			configuracionUoCab= new ConfiguracionUoCab();
			configuracionUoDetList.getConfiguracionUoDet().setDenominacionUnidad(null);
			configuracionUoDetList.limpiar();
			configuracionUoDetList.getResultList();
			denominacion=null;
			if(idEntidad==null){
				entidad= new Entidad();
				sinEntidad=new SinEntidad();	
			}
			if(idSinNivelEntidad==null)
				nivelEntidad= new SinNivelEntidad();
			if(idConfiguracionUoCab==null)
				configuracionUoCab= new ConfiguracionUoCab();
				
			estado=Estado.ACTIVO;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void findNivelEntidad(){
		if(nivelEntidad.getNenCodigo()  != null){
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo() );
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}
	
	public void findEntidad(){
		if(nivelEntidad.getNenCodigo() != null && sinEntidad.getEntCodigo() != null){
			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidad.getEntCodigo());
			sinEntidadList.getSinEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			sinEntidad = sinEntidadList.entidadMaxAnho();
			if(sinEntidad.getIdSinEntidad() != null){
				entidadList.getSinEntidad().setIdSinEntidad(sinEntidad.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
			}
			orden=1;
			findConfigUoCab();
		}
	}
	
	public void findConfigUoCab(){
		if(orden!=null){
			configuracionUoCabList.getConfiguracionUoCab().setOrden(orden);
			configuracionUoCab=configuracionUoCabList.configuracionFind();
		}
	}
	
	  public String getUrlToPage(){
	    	return "/planificacion/sinEntidad/SinEntidadList.xhtml?from=planificacion/configuracionUoDet/ListarConfiguracionUoDet";
	  }
	
	
//	GETTERS Y SETTERS

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}
	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}
	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}



	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	



	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public BigDecimal getNenCod() {
		return nenCod;
	}

	public void setNenCod(BigDecimal nenCod) {
		this.nenCod = nenCod;
	}

	public Long getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}
	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}
	public Long getIdConfiguracionUoCab() {
		return idConfiguracionUoCab;
	}
	public void setIdConfiguracionUoCab(Long idConfiguracionUoCab) {
		this.idConfiguracionUoCab = idConfiguracionUoCab;
	}

	
	
	
}
