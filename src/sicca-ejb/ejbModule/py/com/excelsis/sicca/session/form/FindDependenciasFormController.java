package py.com.excelsis.sicca.session.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;



import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

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
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;



@Scope(ScopeType.CONVERSATION)
@Name("findDependenciasFormController")
public class FindDependenciasFormController implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	
	private ConfiguracionUoCab configuracionUoCab;
	private Estado estado = Estado.ACTIVO;
	private String returnfrom;
	private String from;
	
	private String denominacion;
	private BigDecimal nenCod;
	
	private Integer orden;
	private Integer anho;
	/**
	 * PARAMETROS QUE RECIBIRA
	 * */
	
	private boolean primeraEntrada=true;


	public void init(){
	
			if(primeraEntrada){
				configuracionUoCab= new ConfiguracionUoCab();
				denominacion=null;
				primeraEntrada=false;
			}
			
			cargarCampos();//EN EL CASO QUE RECIBA PARAMETRO y DEBA BLOQUEAR
			search();
		
	}
	private void cargarCampos(){
		nivelEntidadOeeUtil.init();
	}

	public void search(){
		if(denominacion != null && !denominacion.isEmpty())
			configuracionUoCabList.getConfiguracionUoCab().setDenominacionUnidad(denominacion.trim());
		else
			configuracionUoCabList.getConfiguracionUoCab().setDenominacionUnidad(null);
		
		configuracionUoCabList.setEntCodigo(nivelEntidadOeeUtil.getCodSinEntidad());
		configuracionUoCabList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		configuracionUoCabList.getEntidad().setAnho(anho);
		
	
		configuracionUoCabList.listaResultados();

	}
	
	public void searchAll(){
		try {
			if(from!=null && !from.trim().equals("")){
				denominacion=null;
				anho=null;
				search();
			}else{

				configuracionUoCabList.getConfiguracionUoCab().setDenominacionUnidad(null);
				configuracionUoCabList.setIdEntidad(null);
				configuracionUoCabList.setNenCodigo(null);
				configuracionUoCabList.getEntidad().setAnho(null);
				configuracionUoCabList.setEntCodigo(null);
				denominacion=null;
				configuracionUoCabList.limpiarTodos();
				estado=Estado.ACTIVO;
				nivelEntidadOeeUtil.limpiar();
				configuracionUoCab= new ConfiguracionUoCab();
				anho=null;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String obtenerCodigo(ConfiguracionUoCab uoCab){
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoCab, null);
		for(Integer codigo : listCodes){
			code += codigo+".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}
	
	private List<Integer> obtenerListaCodigos(ConfiguracionUoCab uoCab, List<Integer> listCodigos){
		
		if(listCodigos == null)
			listCodigos = new ArrayList<Integer>();
			listCodigos.add(0, uoCab.getOrden());
			listCodigos.add(0, uoCab.getEntidad().getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoCab.getEntidad().getSinEntidad().getNenCodigo().intValue());
	
		return listCodigos;
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

	public BigDecimal getNenCod() {
		return nenCod;
	}

	public void setNenCod(BigDecimal nenCod) {
		this.nenCod = nenCod;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getReturnfrom() {
		return returnfrom;
	}

	public void setReturnfrom(String returnfrom) {
		this.returnfrom = returnfrom;
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
