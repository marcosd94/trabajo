package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
@Name("ingresoPorLeyDeEmergenciaListFC")
public class IngresoPorLeyDeEmergenciaListFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 770076852750375503L;
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

	private Boolean primeraEntrada = true;
	private Persona persona = new Persona();
	private Long idPais = null;
	private Date fechaDesde = null;
	private Date fechaHasta = null;
	private DatosEspecificos dato = new DatosEspecificos();
	
	public void init() {
		cargarCabecera();
		nivelEntidadOeeUtil.init();
		searchDatosEsp();
		search();

	}
	public void cargarCabecera() {
		// OEE
		if (primeraEntrada)
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
					.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

	}
	
	private void searchDatosEsp() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'INGRESO DIRECTO DE PERSONAL POR LEY DE EMERGENCIA' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD'");
		dato = (DatosEspecificos) q.getSingleResult();

	}
	
	public void search() {
		empleadoPuestoList.setPersona(persona);
		empleadoPuestoList.setIdPais(idPais);
		empleadoPuestoList.getSinEntidad().setIdSinEntidad(
				nivelEntidadOeeUtil.getIdSinEntidad());
		empleadoPuestoList.getSinEntidad().setNenCodigo(
				nivelEntidadOeeUtil.getCodNivelEntidad());
		empleadoPuestoList.getConfiguracionUoCab().setIdConfiguracionUo(
				nivelEntidadOeeUtil.getIdConfigCab());
		empleadoPuestoList.setFechaDesde(fechaDesde);
		empleadoPuestoList.setFechaHasta(fechaHasta);
		empleadoPuestoList.getEmpleadoPuesto()
				.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(dato);
		empleadoPuestoList.listaResultadosCU587();
	}
	
	public void searchAll() {
		primeraEntrada = true;
		cargarCabecera();
		persona = new Persona();
		fechaDesde = null;
		fechaHasta = null;
		idPais = null;
		search();

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
	public DatosEspecificos getDato() {
		return dato;
	}
	public void setDato(DatosEspecificos dato) {
		this.dato = dato;
	}
	
	
	
}
