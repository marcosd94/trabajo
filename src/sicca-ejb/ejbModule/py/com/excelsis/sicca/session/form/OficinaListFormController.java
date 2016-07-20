package py.com.excelsis.sicca.session.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.OficinaList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;



@Scope(ScopeType.CONVERSATION)
@Name("oficinaListFormController")
public class OficinaListFormController implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	OficinaList oficinaList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	
	
	
	
	
	
	@In(create = true)
	BarrioList barrioList;
	
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	private Estado estado = Estado.ACTIVO;
	

	private String descripcion;
	
	
	
	private Long idCiudad=null;
	private Long codPais = null;
	private Long codDepartamento = null;
	private Long idBarrio;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;
	private Integer anioActual;
	private boolean primeraEntrada=true;
	

	public void init(){
		try {
			
			
			if (primeraEntrada){
					nivelEntidadOeeUtil.limpiar();
					cargarAnhoActual();
					primeraEntrada=false;
			}
			nivelEntidadOeeUtil.init();
			
			
			search();
			updateDepartamento();
			updateCiudad();
			updateBarrio();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	
	
	
	public void search(){
		if(descripcion != null && !descripcion.isEmpty())
			oficinaList.getOficina().setDescripcion(descripcion);
		else
			oficinaList.getOficina().setDescripcion(null);
	
		oficinaList.setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
	
		oficinaList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		
		
		oficinaList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		
		oficinaList.setIdCiudad(idCiudad);
		
		oficinaList.setIdDepartamento(codDepartamento);
		oficinaList.setIdPais(codPais);
		oficinaList.setIdBarrio(idBarrio);
		
		oficinaList.setEstado(estado);
		
		oficinaList.listaResultados();
	

	}
	
	
	public void searchAll(){
		descripcion=null;
		idBarrio=null;
		idCiudad=null;
		codDepartamento=null;
		codPais=null;
		estado=Estado.ACTIVO;
		oficinaList.getOficina().setDescripcion(null);
		nivelEntidadOeeUtil.limpiar();
		oficinaList.limpiar();
	
	}

	
	
	
	
	
	
	  public void updateDepartamento(){
			List<Departamento> dptoList = getDepartamentosByPais();
			departamentosSelecItem = new ArrayList<SelectItem>();
			buildDepartamentoSelectItem(dptoList);
		}
		public void updateCiudad(){
			List<Ciudad> ciuList = getCiudadByDpto();
			ciudadSelecItem = new ArrayList<SelectItem>();
			buildCiudadSelectItem(ciuList);
		}
		public void updateBarrio(){
			List<Barrio> barrioList = getBarrioByCiudad();
			barrioSelecItem = new ArrayList<SelectItem>();
			buildBarrioSelectItem(barrioList);
		}
		
//		METODOS PRIVADOS
		
		private void cargarAnhoActual() {
			Calendar cal = Calendar.getInstance();
			anioActual = cal.get(Calendar.YEAR);
		}
		private List<Ciudad> getCiudadByDpto(){
			if(codDepartamento!= null){
				ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
				return ciudadList.listarPorDpto();
			}
			return new ArrayList<Ciudad>();
		}
		private List<Barrio> getBarrioByCiudad(){
			if(idCiudad!= null){
				barrioList.setIdCiudad(idCiudad); 
				return barrioList.listaPorCiudad();
			}
			return new ArrayList<Barrio>();
		}
		
		

		private List<Departamento> getDepartamentosByPais(){
			if(codPais != null){
				departamentoList.getPais().setIdPais(codPais);
				departamentoList.setOrder("descripcion");
				return departamentoList.litarPorPais();
			}
			return new ArrayList<Departamento>();
		}
		
		private void buildDepartamentoSelectItem(List<Departamento> dptoList){
			if(departamentosSelecItem == null)
				departamentosSelecItem = new ArrayList<SelectItem>();
			else
				departamentosSelecItem.clear();
			
			departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for(Departamento dep : dptoList){
				departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
			}
		}
		private void buildCiudadSelectItem(List<Ciudad> ciudadList){
			if(ciudadSelecItem == null)
				ciudadSelecItem = new ArrayList<SelectItem>();
			else
				ciudadSelecItem.clear();
			
			ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for(Ciudad dep : ciudadList){
				ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
			}
		}
		private void buildBarrioSelectItem(List<Barrio> barrioList){
			if(barrioSelecItem == null)
				barrioSelecItem = new ArrayList<SelectItem>();
			else
				barrioSelecItem.clear();
			
			barrioSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for(Barrio ba : barrioList){
				barrioSelecItem.add(new SelectItem(ba.getIdBarrio(), ba.getDescripcion()));
			}
		}

//		GETTERS Y SETTERS


	



	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	
	
	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public Long getIdCiudad() {
		return idCiudad;
	}



	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}



	public Long getCodPais() {
		return codPais;
	}



	public void setCodPais(Long codPais) {
		this.codPais = codPais;
	}



	public Long getCodDepartamento() {
		return codDepartamento;
	}



	public void setCodDepartamento(Long codDepartamento) {
		this.codDepartamento = codDepartamento;
	}



	public Long getIdBarrio() {
		return idBarrio;
	}



	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}



	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}



	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}



	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}



	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}



	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}



	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}


	public Integer getAnioActual() {
		return anioActual;
	}



	public void setAnioActual(Integer anioActual) {
		this.anioActual = anioActual;
	}



	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}



	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}



	
	
	
	
}
