package py.com.excelsis.sicca.juridicos.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.cache.EhCacheProvider;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.desvinculacion.session.DesvinculacionList;
import py.com.excelsis.sicca.entity.AccionInconstCab;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.juridicos.session.AccionInconstCabList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("accionesInconstitucionalesListFC")
public class AccionesInconstitucionalesListFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	AccionInconstCabList accionInconstCabList;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Entidad entidad = new Entidad();
	private Persona persona = new Persona();

	private Date fechaDesde;
	private Date fechaHasta;
	private String estado;
	private Boolean mostrarGrillaResultante;
	private Long idPais;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
	
		mostrarGrillaResultante = false;
		idPais = idParaguay();
	}

	/**
	 * Obtiene el id correspondiente a Paraguay de la tabla pais
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	private Boolean validoBusqueda() {
		if (persona != null
				&& (persona.getNombres() != null && !persona.getNombres()
						.trim().isEmpty())
				|| (persona.getApellidos() != null && !persona.getApellidos()
						.trim().isEmpty())
				|| (persona.getDocumentoIdentidad() != null && !persona
						.getDocumentoIdentidad().trim().isEmpty()))
			return true;
		if (fechaDesde != null && fechaHasta != null)
			return true;
		if (estado != null && !estado.equals("T"))
			return true;
		return false;
	}

	private String chequearEntradaPersona() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (persona.getNombres() != null
				&& !persona.getNombres().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getNombres().toString(),
					TiposDatos.STRING.getValor(), null)) {
				return null;
			}
		}
		if (persona.getApellidos() != null
				&& !persona.getApellidos().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getApellidos().toString(),
					TiposDatos.STRING.getValor(), null)) {
				return null;
			}
		}
		if (persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getDocumentoIdentidad().toString(),
					TiposDatos.STRING.getValor(), null)) {
				return null;
			}
		}
		return "ok";
	}

	/**
	 * Método que es llamado al presionar el boton buscar
	 * @throws Exception
	 */
	public void search() throws Exception {
		if (!validoBusqueda()) {
			mostrarGrillaResultante = false;
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe especificar por lo menos un parámetro");
			return;
		}
		if (idPais == null) {
			mostrarGrillaResultante = false;
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Seleccione el País de Expedición");
			return;
		}
		buscarDatosAsociadosUsuario();
		mostrarGrillaResultante = true;
		
		String resultadoPersona = "nuevo";

		if (persona != null
				&& ((persona.getNombres() != null && !persona.getNombres()
						.trim().isEmpty())
						|| (persona.getNombres() != null && !persona
								.getNombres().trim().isEmpty())
						|| (persona.getApellidos() != null && !persona
								.getApellidos().trim().isEmpty())
						|| (persona.getApellidos() != null && !persona
								.getApellidos().trim().isEmpty()) || (persona
						.getDocumentoIdentidad() != null && !persona
						.getDocumentoIdentidad().trim().isEmpty())))
			resultadoPersona = chequearEntradaPersona();

		if (resultadoPersona.equals("ok"))
			accionInconstCabList.setPersona(persona);
		if (resultadoPersona == null)
			return;

		if (estado != null && !estado.equals("T"))
			accionInconstCabList.setEstado(estado);
		accionInconstCabList.setIdPais(idPais);
		if (fechaDesde != null && fechaHasta != null) {
			if (fechaDesde.after(fechaHasta)) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Las fechas ingresadas no son válidas");

			} else {
				accionInconstCabList.setFechaDesde(fechaDesde);
				accionInconstCabList.setFechaHasta(fechaHasta);
			}
		}
		accionInconstCabList.listaResultadosCU453();
	}

	/**
	 * Método que limpia los filtros de busqueda
	 */
	public void clean() {
		mostrarGrillaResultante = false;
		persona = new Persona();
		estado = "T";
		fechaDesde = null;
		fechaHasta = null;
		idPais = idParaguay();
	}

	/**
	 * Método que habilita o o no el link Editar
	 * @param accion
	 * @return
	 */
	public Boolean mostrarLinkEditar(AccionInconstCab accion) {
		String select = "select a.* from juridicos.accion_inconst_cab a "
				+ "join general.persona p "
				+ "on p.id_persona = a.id_persona "
				+ "join general.empleado_puesto ep "
				+ "on ep.id_persona = p.id_persona "
				+ "join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = ep.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = cargo.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where oee.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and a.id_accion_cab = " + accion.getIdAccionCab();
		List<AccionInconstCab> lista = new ArrayList<AccionInconstCab>();
		lista = em.createNativeQuery(select, AccionInconstCab.class)
				.getResultList();

		if (lista != null && lista.size() > 0 && accion.getEstado().equals("P"))
			return true;
		return false;
	}

	/**
	 * Método que recupera los datos del usuario logueado
	 */
	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);

			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	public void imprimirCU461() {
		if (!validoBusqueda()) {
			mostrarGrillaResultante = false;
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe especificar por lo menos un parámetro");
			return;
		}
		if (idPais == null) {
			mostrarGrillaResultante = false;
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Seleccione el País de Expedición");
			return;
		}
		reportUtilFormController.setNombreReporte("RPT_CU461_listar_acciones");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		/**
		 * HALLAMOS EL OEE,NIVEL,ENTIDAD DEL USUARIO
		 * **/
		nivelEntidadOeeUtil.limpiar();
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		String where=" WHERE EMP.ACTUAL = true " +
				" and OEE.ID_CONFIGURACION_UO ="+nivelEntidadOeeUtil.getIdConfigCab();


		
		reportUtilFormController.getParametros().put(
				"nivel",
				nivelEntidadOeeUtil.getCodNivelEntidad() + " "
						+ nivelEntidadOeeUtil.getNombreNivelEntidad());
		reportUtilFormController.getParametros().put(
				"entidad",
				nivelEntidadOeeUtil.getCodNivelEntidad() + "."
						+ nivelEntidadOeeUtil.getCodSinEntidad() + " "
						+ nivelEntidadOeeUtil.getNombreSinEntidad());

		reportUtilFormController.getParametros().put(
				"oee",
				nivelEntidadOeeUtil.getCodNivelEntidad() + "."
						+ nivelEntidadOeeUtil.getCodSinEntidad() + "."
						+ nivelEntidadOeeUtil.getOrdenUnidOrg() + " "
						+ nivelEntidadOeeUtil.getDenominacionUnidad());
		reportUtilFormController.getParametros().put("idOee",
				nivelEntidadOeeUtil.getIdConfigCab());

		if (fechaDesde != null){
			where+=" AND DATE(AC.FECHA_ALTA) >= to_date(cast('"+sdf.format(fechaDesde)+"' as varchar), 'DD-MM-YYYY') ";
			reportUtilFormController.getParametros().put("fecDesde",sdf.format(fechaDesde));
				
		}
			
		if (fechaHasta != null){
			where+=" AND DATE(AC.FECHA_ALTA) <= to_date(cast('"+sdf.format(fechaHasta)+"' as varchar), 'DD-MM-YYYY') ";
			reportUtilFormController.getParametros().put("fecHasta",
					sdf.format(fechaHasta));
		}
			
		if (estado == null || estado.equals("T"))
			reportUtilFormController.getParametros().put("estado", "%%");
		else{
			where+=" AND AC.ESTADO like '"+estado+"'";
			reportUtilFormController.getParametros().put("estado", estado);
		}
			

		where+=" order by PERSONA.DOCUMENTO_IDENTIDAD,AC.id_accion_cab desc";
		reportUtilFormController.getParametros().put("where", where);
		reportUtilFormController.imprimirReportePdf();

	}

	
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

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Boolean getMostrarGrillaResultante() {
		return mostrarGrillaResultante;
	}

	public void setMostrarGrillaResultante(Boolean mostrarGrillaResultante) {
		this.mostrarGrillaResultante = mostrarGrillaResultante;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

}
