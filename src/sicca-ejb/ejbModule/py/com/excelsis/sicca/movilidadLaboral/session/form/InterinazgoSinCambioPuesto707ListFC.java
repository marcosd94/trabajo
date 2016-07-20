package py.com.excelsis.sicca.movilidadLaboral.session.form;
import java.util.Date;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("interinazgoSinCambioPuesto707ListFC")
public class InterinazgoSinCambioPuesto707ListFC{
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	EmpleadoPuestoList empleadoPuestoList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	
	


	private Boolean primeraEntrada=true;
	private Persona persona= new Persona();
	private Long idPais=null;
	private Date fechaDesde=null;
	private Date fechaHasta=null;
	
	


	public void init() {
		cargarCabecera();
		nivelEntidadOeeUtil.init();
		
		search();
	}
	
	
	public void searchAll(){
		primeraEntrada=true;
		cargarCabecera();
		persona= new Persona();
		fechaDesde=null;
		fechaHasta=null;
		idPais=null;
		search();
		
	}
	
	public void search(){
		empleadoPuestoList.setPersona(persona);
		empleadoPuestoList.setIdPais(idPais);
		empleadoPuestoList.getSinEntidad().setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
		empleadoPuestoList.getSinEntidad().setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		empleadoPuestoList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		empleadoPuestoList.setFechaDesde(fechaDesde);
		empleadoPuestoList.setFechaHasta(fechaHasta);
		empleadoPuestoList.getEmpleadoPuesto().setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(new DatosEspecificos());
		empleadoPuestoList.getEmpleadoPuesto().getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().setIdDatosEspecificos(getIdTipoMovilidad());
		empleadoPuestoList.setPermanente(null);
		//tambien es utilizado para estos cu por usar los mismos paramentros
		empleadoPuestoList.listaResultadosCU541();
	}
	
	
	private Long getIdTipoMovilidad(){
		return seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","INTERINO SIN CAMBIO DE PUESTO").getIdDatosEspecificos();
	}
	
	


	public void cargarCabecera(){
			//OEE
		if(primeraEntrada){
			nivelEntidadOeeUtil.limpiar();
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			primeraEntrada=false;
		}
			
		
	}
	

	
	
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}
	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}


	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
	}


	public Long getIdPais() {
		return idPais;
	}


	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}


	public Date getFechaDesde() {
		return fechaDesde;
	}


	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}


	public Date getFechaHasta() {
		return fechaHasta;
	}


	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}


	
	

	
}
