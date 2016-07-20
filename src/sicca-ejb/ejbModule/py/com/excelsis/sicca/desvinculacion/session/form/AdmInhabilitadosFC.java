package py.com.excelsis.sicca.desvinculacion.session.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.desvinculacion.session.InhabilitadosList;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("admInhabilitadosFC")
public class AdmInhabilitadosFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	InhabilitadosList inhabilitadosList;

	private Long idInhabilitado;
	private Inhabilitados inhabilitados;
	private Boolean modoVista;
	private String motivoHabilit;

	private Long idPais;
	private String nombres;
	private String apellidos;
	private String documento;
	private Date fechaInhabilitacionDesde;
	private Date fechaInhabilitacionHasta;

	private String inhabilitado = "S";
	private List<SelectItem> inhabilitadoSelectItem;

	private List<Inhabilitados> listaInhabilitados;

	private Date fechaDesvinculacion;
	private String motivoDesvinculacion;
	private String ubicacionFisica = "";

	public void init() {
		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		search();
	}

	public void searchAll() {
		limpiar();
		cargarCabecera();
		search();
	}

	public void search() {
		if (!validar(false))
			return;

		buscar();
	}

	public void buscar() {
		String consulta =
			"" + "select inhabilitados from Inhabilitados inhabilitados "
				+ " join inhabilitados.persona persona "
				+ " join persona.paisByIdPaisExpedicionDoc pais "
				+ " join inhabilitados.configuracionUoCab configuracionUoCab "
				+ " where  configuracionUoCab.idConfiguracionUo = :idConfiguracionUo";

		nivelEntidadOeeUtil.init();
		if (idPais != null) {
			consulta += " and pais.idPais = :idPais";
		}
		if (!Utiles.vacio(documento)) {
			consulta += " and lower(persona.documentoIdentidad) like :documento";
		}

		if (!Utiles.vacio(nombres)) {
			consulta += " and lower(persona.nombres) like :nombres";
		}

		if (!Utiles.vacio(apellidos)) {
			consulta += " and lower(persona.apellidos) like :apellidos";
		}

		if (!"T".equals(inhabilitado)) {
			if ("S".equals(inhabilitado))
				consulta += " and inhabilitados.inhabilitado is true";
			else
				consulta += " and inhabilitados.inhabilitado is false";
		}

		if (fechaInhabilitacionDesde != null) {
			consulta += " and cast(inhabilitados.fechaAlta as date) >= cast(:fechaDesde as date)";
		}

		if (fechaInhabilitacionHasta != null) {
			consulta += " and cast(inhabilitados.fechaAlta as date) <= cast(:fechaHasta as date)";
		}

		Map<String, QueryValue> params = new HashMap<String, QueryValue>();
		params.put("idConfiguracionUo", new QueryValue(nivelEntidadOeeUtil.getIdConfigCab()));

		if (idPais != null)
			params.put("idPais", new QueryValue(idPais));
		// uo cab inhabilitados
		if (!Utiles.vacio(documento))
			params.put("documento", new QueryValue("%" + documento.toLowerCase() + "%"));

		if (!Utiles.vacio(nombres))
			params.put("nombres", new QueryValue("%" + nombres.toLowerCase() + "%"));

		if (!Utiles.vacio(apellidos))
			params.put("apellidos", new QueryValue("%" + apellidos.toLowerCase() + "%"));

		if (fechaInhabilitacionDesde != null)
			params.put("fechaDesde", new QueryValue(fechaInhabilitacionDesde));

		if (fechaInhabilitacionHasta != null)
			params.put("fechaHasta", new QueryValue(fechaInhabilitacionHasta));

		listaInhabilitados = inhabilitadosList.listaResultadosCU441(consulta, params);
	}

	private void buildInhabilitadoSelectItem() {
		inhabilitadoSelectItem = new ArrayList<SelectItem>();
		inhabilitadoSelectItem.add(new SelectItem("S", "Si"));
		inhabilitadoSelectItem.add(new SelectItem("N", "No"));
		inhabilitadoSelectItem.add(new SelectItem("T", "Todos"));
	}

	public void cargarCabecera() {
		grupoPuestosController =
			(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		setNombres(null);
		setApellidos(null);
		documento = null;
		fechaInhabilitacionDesde = null;
		fechaInhabilitacionHasta = null;
		inhabilitado = "S";
		idPais = null;
	}

	private Boolean validar(Boolean controlarCab) {
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}

		// if (fechaInhabilitacionDesde != null && fechaInhabilitacionHasta != null && fechaInhabilitacionDesde.after(fechaInhabilitacionHasta)) {
		// statusMessages.add(Severity.ERROR, "El rango de fechas es inv\u00E1lido, la Fecha Desde no debe superar la Fecha Hasta.");
		// return;
		// }
		//

		return true;
	}

	/** EDITAR **/

	public void initEdit() {
		try {
			// idInhabilitado
			cargarInhabilitado();
			cargarCabeceraEdit();
			cargarDesvinculacion();

			Calendar c = Calendar.getInstance();
			String separador = File.separator;
			ubicacionFisica =
				separador + "SICCA" + separador + c.get(Calendar.YEAR) + separador
					+ "Usuario_Portal" + separador
					+ inhabilitados.getPersona().getDocumentoIdentidad() + "_"
					+ inhabilitados.getPersona().getIdPersona();
			motivoHabilit =
				inhabilitados.getIdInhabilitado() == null ? "" : inhabilitados.getMotivoHabilit();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ocurrió un error al cargar la página.");
		}

	}

	private void cargarInhabilitado() {
		inhabilitados = em.find(Inhabilitados.class, idInhabilitado);
		Persona persona =
			em.find(Persona.class, inhabilitados.getEmpleadoPuesto().getPersona().getIdPersona());
		inhabilitados.getEmpleadoPuesto().setPersona(persona);
	}

	public void cargarCabeceraEdit() {
		grupoPuestosController =
			(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.setIdConfUoCab(inhabilitados.getConfiguracionUoCab().getIdConfiguracionUo());
		grupoPuestosController.initCabecera();
	}

	@SuppressWarnings("unchecked")
	private void cargarDesvinculacion() {
		fechaDesvinculacion = null;
		motivoDesvinculacion = null;

		try {
			String sql =
				"select d.* " + "from desvinculacion.desvinculacion d "
					+ "where id_empleado_puesto = "
					+ inhabilitados.getEmpleadoPuesto().getIdEmpleadoPuesto();

			List<Desvinculacion> lista =
				em.createNativeQuery(sql, Desvinculacion.class).getResultList();

			if (lista != null && lista.size() > 0) {
				Desvinculacion desvinculacion = lista.get(0);
				fechaDesvinculacion = desvinculacion.getFechaAlta();
				DatosEspecificos datosEspecificos =
					em.find(DatosEspecificos.class, desvinculacion.getDatosEspecifMotivo().getIdDatosEspecificos());
				motivoDesvinculacion = datosEspecificos.getDescripcion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void habilitarFuncionario() {
		if (idInhabilitado != null) {
			// Nuevo
			try {

				inhabilitados = em.find(Inhabilitados.class, idInhabilitado);

				if ("FALLECIMIENTO".equalsIgnoreCase(motivoDesvinculacion)) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "El funcionario no puede ser Habilitado");
					return;
				}

				if (!existeDocumentoAdjunto()) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Debe levantar un documento adjunto antes de Habilitar al Funcionario");
					return;
				}

				if (motivoHabilit == null || motivoHabilit.trim().isEmpty()) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Debe cargar el Motivo de Habiliatción");
					return;
				}
				if (motivoHabilit.length() > 1000) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Superada la longitud máxima del campo Motivo de Habilitación");
					return;
				}
				inhabilitados.setInhabilitado(false);
				Date fecha = new Date();
				inhabilitados.setFechaHabilit(fecha);
				inhabilitados.setUsuHabilit(usuarioLogueado.getCodigoUsuario());
				inhabilitados.setFechaMod(fecha);
				inhabilitados.setUsuMod(usuarioLogueado.getCodigoUsuario());
				inhabilitados.setFechaHasta(fecha);
				inhabilitados.setMotivoHabilit(motivoHabilit);
				em.merge(inhabilitados);

				em.flush();

				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private boolean existeDocumentoAdjunto() {
		try {
			String sql =
				"" + " select documentos.* " + " from general.documentos " + " where id_tabla = "
					+ idInhabilitado + " and nombre_tabla = 'inhabilitados' "
					+ " and nombre_pantalla = 'admInhabilitadosSFP'";

			List<Documentos> lista = em.createNativeQuery(sql, Documentos.class).getResultList();
			if (lista != null && lista.size() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public String getTitulo() {
		if (modoVista) {
			return "Ver Inhabilitado";
		}

		return "Editar Inhabilitado";
	}

	/** GETTER'S && SETTER'S **/

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getNombres() {
		return nombres;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getDocumento() {
		return documento;
	}

	public void setFechaInhabilitacionDesde(Date fechaInhabilitacionDesde) {
		this.fechaInhabilitacionDesde = fechaInhabilitacionDesde;
	}

	public Date getFechaInhabilitacionDesde() {
		return fechaInhabilitacionDesde;
	}

	public void setFechaInhabilitacionHasta(Date fechaInhabilitacionHasta) {
		this.fechaInhabilitacionHasta = fechaInhabilitacionHasta;
	}

	public Date getFechaInhabilitacionHasta() {
		return fechaInhabilitacionHasta;
	}

	public void setInhabilitado(String inhabilitado) {
		this.inhabilitado = inhabilitado;
	}

	public String getInhabilitado() {
		return inhabilitado;
	}

	public void setInhabilitadoSelectItem(List<SelectItem> inhabilitadoSelectItem) {
		this.inhabilitadoSelectItem = inhabilitadoSelectItem;
	}

	public List<SelectItem> getInhabilitadoSelectItem() {
		if (inhabilitadoSelectItem == null)
			buildInhabilitadoSelectItem();

		return inhabilitadoSelectItem;
	}

	public void setListaInhabilitados(List<Inhabilitados> listaInhabilitados) {
		this.listaInhabilitados = listaInhabilitados;
	}

	public List<Inhabilitados> getListaInhabilitados() {
		return listaInhabilitados;
	}

	public void setIdInhabilitado(Long idInhabilitado) {
		this.idInhabilitado = idInhabilitado;
	}

	public Long getIdInhabilitado() {
		return idInhabilitado;
	}

	public void setInhabilitados(Inhabilitados inhabilitados) {
		this.inhabilitados = inhabilitados;
	}

	public Inhabilitados getInhabilitados() {
		return inhabilitados;
	}

	public void setModoVista(Boolean modoVista) {
		this.modoVista = modoVista;
	}

	public Boolean getModoVista() {
		return modoVista;
	}

	public void setFechaDesvinculacion(Date fechaDesvinculacion) {
		this.fechaDesvinculacion = fechaDesvinculacion;
	}

	public Date getFechaDesvinculacion() {
		return fechaDesvinculacion;
	}

	public void setMotivoDesvinculacion(String motivoDesvinculacion) {
		this.motivoDesvinculacion = motivoDesvinculacion;
	}

	public String getMotivoDesvinculacion() {
		return motivoDesvinculacion;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getMotivoHabilit() {
		return motivoHabilit;
	}

	public void setMotivoHabilit(String motivoHabilit) {
		this.motivoHabilit = motivoHabilit;
	}

}
