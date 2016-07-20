package py.com.excelsis.sicca.session.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;



import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import enums.Estado;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.ConfiguracionUoDetList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;



@Scope(ScopeType.CONVERSATION)
@Name("listarConfiguracionUoDetFormController")
public class ListarConfiguracionUoDetFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -728563848630921468L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ConfiguracionUoDetList configuracionUoDetList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	

	private Estado estado = Estado.ACTIVO;
	

	private String denominacion;
	private Integer anio;
	
	private Integer orden;
	private Integer anioActual;
	/**
	 * PARAMETROS QUE RECIBIDOS
	 * */
	private String from;
	private Long idSinNivelEntidad;
	private Long idConfiguracionUoCab;
	private Long idSinNivelEntidadBa;
	private Long idConfiguracionUoCabBa;

	private boolean primeraEntrada=true;
	

	
	public void init(){
	
		if(idSinNivelEntidad!=null){
			nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		}else if(idSinNivelEntidadBa!=null)
			nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidadBa);
		
		if(idConfiguracionUoCab!=null){
			nivelEntidadOeeUtil.setIdConfigCab(idConfiguracionUoCab);
		}else if(idConfiguracionUoCabBa!=null)
			nivelEntidadOeeUtil.setIdConfigCab(idConfiguracionUoCabBa);
		
		if(primeraEntrada){
			denominacion=null;
			//anio=null;
			primeraEntrada=false;
		}
		anio=null;
		nivelEntidadOeeUtil.init();
		search();
	}
	

	
	
	
	public void search(){
		if(denominacion != null && !denominacion.isEmpty())
			configuracionUoDetList.getConfiguracionUoDet().setDenominacionUnidad(denominacion.trim());
		
		if(anio!=null)
			configuracionUoDetList.setAnio(anio);
		
		
		configuracionUoDetList.setEntCod(nivelEntidadOeeUtil.getCodSinEntidad() != null
				? nivelEntidadOeeUtil.getCodSinEntidad() : null);
		configuracionUoDetList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad() != null
			? nivelEntidadOeeUtil.getCodNivelEntidad() : null);
		if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null)
			configuracionUoDetList.setOrden(nivelEntidadOeeUtil.getOrdenUnidOrg());
		
	
		
		configuracionUoDetList.getResultList();
	}
	
	public String obtenerCodigoMod(ConfiguracionUoDet det) {
		ConfiguracionUoDet uoDet = det;
		String code = "";
		
		if(det!=null && uoDet.getConfiguracionUoCab()!=null){
			code += findCodNivelEntidad(uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getNenCodigo())
					+ ".";
			code += uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getEntCodigo()
					+ ".";
			code += uoDet.getConfiguracionUoCab().getOrden() + ".";
			List<Integer> listCodes = obtenerListaCodigos(uoDet, null);

			for (int i = 0; i < listCodes.size(); i++) {
				code+=listCodes.get(i);
				if(i!=listCodes.size()-1)
					code+=".";
			}
		//code = code + det.getOrden();
		}
		
	
		return code;
	}
	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	@SuppressWarnings("unchecked")
	public String findCodNivelEntidad(BigDecimal nencod) {
		SinNivelEntidad nivel = new SinNivelEntidad(); 
		if (nencod != null) {
			List<SinNivelEntidad> nivelEntidads= em.createQuery("Select n from SinNivelEntidad n " +
					" where n.nenCodigo= "+nencod +" and n.activo=true order by n.aniAniopre desc").getResultList();
			if(nivelEntidads.isEmpty())
				return "";
			else
				nivel = nivelEntidads.get(0);
		} else
			return "";
		return nivel.getNenCodigo() + "";
	}
	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}

	public void searchAll(){
		try {
			
			configuracionUoDetList.getConfiguracionUoDet().setDenominacionUnidad(null);
			if(from==null || from.equals("null")){
				idConfiguracionUoCab=null;
				idConfiguracionUoCabBa=null;
				idSinNivelEntidad=null;
				idSinNivelEntidadBa=null;
				nivelEntidadOeeUtil.limpiar();
				
			}
				
			
			anio=null;
			denominacion=null;
			estado=Estado.ACTIVO;
			search();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	



	  public String getUrlToPage(){
	    	return "/planificacion/sinEntidad/SinEntidadList.xhtml?from=planificacion/configuracionUoDet/ListarConfiguracionUoDet";
	  }
	  private void cargarAnhoActual() {
			Calendar cal = Calendar.getInstance();
			anioActual = cal.get(Calendar.YEAR);
		}
	
//	GETTERS Y SETTERS





	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}



	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	
//

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
	public Long getIdSinNivelEntidadBa() {
		return idSinNivelEntidadBa;
	}
	public void setIdSinNivelEntidadBa(Long idSinNivelEntidadBa) {
		this.idSinNivelEntidadBa = idSinNivelEntidadBa;
	}
	public Long getIdConfiguracionUoCabBa() {
		return idConfiguracionUoCabBa;
	}
	public void setIdConfiguracionUoCabBa(Long idConfiguracionUoCabBa) {
		this.idConfiguracionUoCabBa = idConfiguracionUoCabBa;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Integer getAnio() {
		return anio;
	}
	public void setAnio(Integer anio) {
		this.anio = anio;
	}
	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}
	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	
	
	
}
