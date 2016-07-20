package py.com.excelsis.sicca.session.form;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.OficinaHome;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.AlquiladoProp;

@Name("oficinaEditFormController")
@Scope(ScopeType.CONVERSATION)
public class OficinaEditFormController implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;


	@In(create = true)
	OficinaHome oficinaHome;
	
	private Long idOficina;

	
	public Long getIdOficina() {
		return idOficina;
	}
	public void setIdOficina(Long idOficina) {
		this.idOficina = idOficina;
	}

	
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Oficina oficina;
	private String mensaje;
	private String codigo;

	
	private Long idCiudad=null;
	private Long codPais = null;
	private Long codDepartamento = null;
	private Long idBarrio;
	private AlquiladoProp alquiladoProp;
	
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;
	private boolean tieneUo;

	private Integer anioActual;
	private SeguridadUtilFormController seguridadUtilFormController;
	private boolean primeraEntrada=true;
	
	private void validarOee(Long idOee) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (!seguridadUtilFormController.verificarPerteneceOee(idOee)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	public void init() {
		
		
			
		if(primeraEntrada)
		{
			oficina = new Oficina();
			if (idOficina != null) {
				oficina = em.find(Oficina.class,idOficina);
				validarOee(oficina.getConfiguracionUoCab().getIdConfiguracionUo());
				oficinaHome.setInstance(oficina);
				obtenerDatos();
				tieneUo=true;
			} else {
				
				oficina.setActivo(true);
				alquiladoProp=AlquiladoProp.PROPIO;
				setearDatos();
			}
			primeraEntrada=true;
			updateDepartamento();
			updateCiudad();
			updateBarrio();
		}
		cargarAnhoActual();
		nivelEntidadOeeUtil.init();
	}
	
	public void initVer(){
		if (idOficina != null) {
			oficina = em.find(Oficina.class,idOficina);
			oficinaHome.setInstance(oficina);
			obtenerDatos();
			tieneUo=true;
		}
	}

	
	
	
	

	
	
	  public void updateDepartamento(){
			List<Departamento> dptoList = getDepartamentosByPais();
			departamentosSelecItem = new ArrayList<SelectItem>();
			buildDepartamentoSelectItem(dptoList);
		}
	  public void updateDepartamentoChange(){
			List<Departamento> dptoList = getDepartamentosByPais();
			departamentosSelecItem = new ArrayList<SelectItem>();
			codDepartamento=null;
			buildDepartamentoSelectItem(dptoList);
		}
		public void updateCiudad(){
			List<Ciudad> ciuList = getCiudadByDpto();
			ciudadSelecItem = new ArrayList<SelectItem>();
			buildCiudadSelectItem(ciuList);
		}
		public void updateCiudadChange(){
			List<Ciudad> ciuList = getCiudadByDpto();
			ciudadSelecItem = new ArrayList<SelectItem>();
			idBarrio=null;
			idCiudad=null;
			buildCiudadSelectItem(ciuList);
			updateBarrio();
		}
		public void updateBarrio(){
			List<Barrio> barrioList = getBarrioByCiudad();
			barrioSelecItem = new ArrayList<SelectItem>();
			buildBarrioSelectItem(barrioList);
		}
		
		
		public String update() {
			try {
				if(!checkData("update"))
					return null;
				oficina.setFechaMod(new Date());
				oficina.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
				oficina.setDescripcion(oficina.getDescripcion().toUpperCase().trim());
				oficina.setCiudad(em.find(Ciudad.class, idCiudad));
				if(idBarrio != null)
					oficina.setBarrio(em.find(Barrio.class, idBarrio));
				oficina.setAlquilado(alquiladoProp.getValor());
				oficina.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
				if(oficina.getDireccion()!=null)
					oficina.setDireccion(oficina.getDireccion().trim().toUpperCase());
				
				em.merge(oficina);
				em.flush();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
			mensaje = "Registro actualizado con exito";
			statusMessages.add(Severity.INFO, mensaje);
			return "updated";
		}

		public String save() {
			try {
				
				
				if(!checkData("persist"))
					return null;
					
				oficina.setFechaAlta(new Date());
				oficina.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
				oficina.setDescripcion(oficina.getDescripcion().toUpperCase().trim());
				oficina.setCiudad(em.find(Ciudad.class, idCiudad));
				if(idBarrio != null)
				oficina.setBarrio(em.find(Barrio.class, idBarrio));
				oficina.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
				oficina.setAlquilado(alquiladoProp.getValor());
				if(oficina.getDireccion()!=null)
					oficina.setDireccion(oficina.getDireccion().trim().toUpperCase());
				
				em.persist(oficina);
				em.flush();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
			mensaje = "Registro creado con exito";
			statusMessages.add(Severity.INFO, mensaje);
			return "persisted";
		}
		
		private boolean checkData(String operation){
			if(nivelEntidadOeeUtil.getIdConfigCab()==null ){
				statusMessages.add(" Debe seleccionar un OEE. Verifique");
				return false;
			}
			
			if(oficina.getDescripcion()==null || oficina.getDescripcion().trim().isEmpty()){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
				return false;
			}
			
			if (seguridadUtilFormController == null) {
				seguridadUtilFormController =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			if(seguridadUtilFormController.contieneCaracter(oficina.getDescripcion().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return false;
			}
			if(!checkDuplicate(operation)){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
				return false;
			}
			
			return true;
		}
		@SuppressWarnings("unchecked")
		private boolean checkDuplicate(String operation){
			String hql ="Select o from Oficina o where lower(o.descripcion) like :desc ";
			if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
				hql+="and o.configuracionUoCab.idConfiguracionUo="+nivelEntidadOeeUtil.getIdConfigCab().longValue();
			
			if(operation.equalsIgnoreCase("update")){
				hql += " AND o.idOficina != " +oficina.getIdOficina().longValue();
			}
			List<Oficina> list = em.createQuery(hql).setParameter("desc", oficina.getDescripcion().trim().toLowerCase()).getResultList();
			return list.isEmpty();
		}
	
		
		
		
		
//		METODOS PRIVADOS
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
		private void cargarAnhoActual() {
			Calendar cal = Calendar.getInstance();
			anioActual = cal.get(Calendar.YEAR);
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
		private void obtenerDatos(){
			idCiudad= oficina.getCiudad().getIdCiudad();
			if(oficina.getBarrio() != null)
				idBarrio=oficina.getBarrio().getIdBarrio();
			codDepartamento=oficina.getCiudad().getDepartamento().getIdDepartamento();
			codPais=oficina.getCiudad().getDepartamento().getPais().getIdPais();
			nivelEntidadOeeUtil.setIdConfigCab(oficina.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			if(oficina.getAlquilado())
				alquiladoProp=AlquiladoProp.ALQUILADO;
			else
				alquiladoProp=AlquiladoProp.PROPIO;
			
			
			updateDepartamento();
			updateCiudad();
			updateBarrio();
			
		}
		
		private void setearDatos(){
			nivelEntidadOeeUtil.limpiar();
			idBarrio=null;
			idCiudad=null;
			codDepartamento=null;
			codPais=idParaguay();
			verificarUsuarioUo();
			updateDepartamento();
			updateCiudad();
			updateBarrio();
		}
		private void verificarUsuarioUo(){
			if(usuarioLogueado.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
				tieneUo=true;
			}else{
				tieneUo=false;
				nivelEntidadOeeUtil.limpiar();
			}
				
			
		}
		@SuppressWarnings("unchecked")
		private Long idParaguay(){
				List<Pais> p= em.createQuery(" Select p from Pais p" +
						" where lower(p.descripcion) like 'paraguay'").getResultList();
				if(!p.isEmpty())
					return p.get(0).getIdPais();
				
				return null;
		}
		
		
	// GETTER Y SETTER

	


	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	
	public Oficina getOficina() {
		return oficina;
	}
	public void setOficina(Oficina oficina) {
		this.oficina = oficina;
	}
	
	public Long getIdBarrio() {
		return idBarrio;
	}
	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}
	public AlquiladoProp getAlquiladoProp() {
		return alquiladoProp;
	}
	public void setAlquiladoProp(AlquiladoProp alquiladoProp) {
		this.alquiladoProp = alquiladoProp;
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
	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}
	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
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
	public boolean isTieneUo() {
		return tieneUo;
	}
	public void setTieneUo(boolean tieneUo) {
		this.tieneUo = tieneUo;
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
