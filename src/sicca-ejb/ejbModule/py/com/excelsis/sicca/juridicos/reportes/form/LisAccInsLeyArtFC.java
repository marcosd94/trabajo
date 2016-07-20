package py.com.excelsis.sicca.juridicos.reportes.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.EstadoSumario;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("lisAccInsLeyArtFC")
public class LisAccInsLeyArtFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Date fechaDesde;
	private Date fechaHasta;
	private String articuloEspecifico;
	private String opcionEspeArticulo = "TODOS";
	private Long idLey;
	private String idEstado;
	private List<SelectItem> leySelecItem;
	private boolean primeraEntrada = true;
	private boolean habOee;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		nivelEntidadOeeUtil.init();
		if (primeraEntrada) {
			primeraEntrada = false;
			nivelEntidadOeeUtil.limpiar();
			if (seguridadUtilFormController.esRolHomologar(usuarioLogueado
					.getIdUsuario()))
				habOee = true;
			else
				habOee = false;

			cargarCabecera();
		}
		updateLey();

	}

	public void cargarCabecera() {
		// Nivel
		ConfiguracionUoCab c = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(c
				.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(usuarioLogueado
				.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getIdSinEntidad());
		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
	}

	public void updateLey() {
		List<DatosEspecificos> leyList = leyListado();
		leySelecItem = new ArrayList<SelectItem>();
		buildLeySelectItem(leyList);

	}

	private List<DatosEspecificos> leyListado() {
		Query q = em
				.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
						+ " where DatosEspecificos.activo is true and DatosEspecificos.datosGenerales.descripcion ='NORMATIVA' "
						+ " order by DatosEspecificos.descripcion");
		return q.getResultList();
	}

	private void buildLeySelectItem(List<DatosEspecificos> leyList) {
		if (leySelecItem == null)
			leySelecItem = new ArrayList<SelectItem>();
		else
			leySelecItem.clear();

		leySelecItem.add(new SelectItem(null, "TODOS"));
		for (DatosEspecificos dep : leyList) {
			leySelecItem.add(new SelectItem(dep.getIdDatosEspecificos(), dep
					.getDescripcion()));
		}
	}

	public String limpiar() {

		opcionEspeArticulo = "TODOS";
		articuloEspecifico = null;
		fechaDesde = null;
		fechaHasta = null;
		idLey = null;
		idEstado = null;

		return "OK";
	}

	private Boolean esAdministrador() {
		String sentencia = "select r.* from seguridad.rol r "
				+ "join seguridad.usuario_rol ur " + "on ur.id_rol = r.id_rol "
				+ "join seguridad.usuario u "
				+ "on u.id_usuario = ur.id_usuario" + " where u.id_usuario = "
				+ usuarioLogueado.getIdUsuario();
		List<Rol> lista = em.createNativeQuery(sentencia, Rol.class)
				.getResultList();
		for (Rol o : lista) {
			if (o.getHomologador())
				return true;
		}
		return false;
	}

	private Boolean precondImprimir() {
		if (fechaDesde != null && fechaHasta != null) {
			if (fechaDesde.getTime() > fechaHasta.getTime()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"La Fecha Desde no puede ser mayor a la Fecha Hasta");
				return false;
			}

		}
		if (opcionEspeArticulo != null) {
			if (!(opcionEspeArticulo.equals("TODOS"))
					&& (articuloEspecifico == null || (articuloEspecifico != null && articuloEspecifico
							.trim().isEmpty()))) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_CARGAR_REQUERIDOS"));
				return false;
			}
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_CARGAR_REQUERIDOS"));
			return false;
		}
		return true;
	}

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void imprimir() {
		if (!precondImprimir()) {
			updateLey();
			return;
		}
		try {
			reportUtilFormController = (ReportUtilFormController) Component
					.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController
					.setNombreReporte("RPT_CU512_LISTADO_INCONS_LEY_ART");
			cargarParametros();
			reportUtilFormController.imprimirReportePdf();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cargarParametros() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (nivelEntidadOeeUtil != null) {
			nivelEntidadOeeUtil.init();
			String codNivel = nivelEntidadOeeUtil.getCodNivelEntidad() != null ? nivelEntidadOeeUtil
					.getCodNivelEntidad().toString() : "";
			String codEntidad;
			if (nivelEntidadOeeUtil.getCodSinEntidad() != null) {
				codEntidad = codNivel + "."
						+ nivelEntidadOeeUtil.getCodSinEntidad().toString();

			} else {
				codEntidad = "Todos";
			}
			String codOee;
			if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null) {
				codOee = codEntidad + "."
						+ nivelEntidadOeeUtil.getOrdenUnidOrg().toString();
			} else {
				codOee = "Todos";
			}

			reportUtilFormController
					.getParametros()
					.put("nivelFIL",
							codNivel
									+ " "
									+ (nivelEntidadOeeUtil
											.getNombreNivelEntidad() != null ? nivelEntidadOeeUtil
											.getNombreNivelEntidad() : ""));
			if (codEntidad.equalsIgnoreCase("Todos")) {
				reportUtilFormController.getParametros().put("entidadFIL",
						"Todos");
			} else {
				reportUtilFormController
						.getParametros()
						.put("entidadFIL",
								codEntidad
										+ " "
										+ (nivelEntidadOeeUtil
												.getNombreSinEntidad() != null ? nivelEntidadOeeUtil
												.getNombreSinEntidad() : ""));
			}
			if (codEntidad.equalsIgnoreCase("Todos")) {
				reportUtilFormController.getParametros().put("oeeFIL", "Todos");
			} else {
				reportUtilFormController
						.getParametros()
						.put("oeeFIL",
								codOee
										+ " "
										+ (nivelEntidadOeeUtil
												.getDenominacionUnidad() != null ? nivelEntidadOeeUtil
												.getDenominacionUnidad() : ""));
			}

		}

		if (idLey != null) {
			DatosEspecificos de = em.find(DatosEspecificos.class, idLey);
			reportUtilFormController.getParametros().put("leyFIL",
					de.getDescripcion());
		} else {
			reportUtilFormController.getParametros().put("leyFIL", "Todos");
		}
		if (idEstado != null && !idEstado.trim().isEmpty()) {
			reportUtilFormController.getParametros().put("estadoFIL", idEstado);
		} else {
			reportUtilFormController.getParametros().put("estadoFIL", "Todos");
		}
		if (opcionEspeArticulo != null) {
			if (opcionEspeArticulo.equals("TODOS")) {
				reportUtilFormController.getParametros().put("articuloFIL",
						"Todos");
			} else {

				reportUtilFormController.getParametros().put(
						"articuloFIL", articuloEspecifico);

			}
		}
		if (fechaDesde != null && fechaHasta != null) {
			reportUtilFormController.getParametros().put("fechaHastaFIL",
					fechaHasta == null ? "-" : sdf.format(fechaHasta));
			reportUtilFormController.getParametros().put("fechaDesdeFIL",
					fechaDesde == null ? "-" : sdf.format(fechaDesde));

		} else {
			reportUtilFormController.getParametros().put("fechaHastaFIL", "-");
			reportUtilFormController.getParametros().put("fechaDesdeFIL", "-");
		}
		reportUtilFormController.getParametros().put("laConsulta",
				generarConsulta());
		reportUtilFormController.getParametros()
				.put("oeeUsuarioLogueado",
						usuarioLogueado.getConfiguracionUoCab()
								.getDenominacionUnidad());
		reportUtilFormController.getParametros().put("usuario",
				usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController.getParametros().put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo",
				servletContext.getRealPath("/img/"));
	}

	private String generarConsulta() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		StringBuffer elWhere = new StringBuffer();
		StringBuffer SQL = new StringBuffer();
		
		SQL.append(" select distinct * from ");

		SQL.append(" (SELECT ");
		SQL.append("     (SELECT descripcion FROM general.pais where id_pais = PERSONA.id_pais_expedicion_doc) as paisExpe , ");
		SQL.append("     planificacion.nivel_entidad_oee(OEE.id_entidad, 'SIN_NIVEL', null, null) as nivel, ");
		SQL.append("     planificacion.nivel_entidad_oee(OEE.id_entidad, 'SIN_ENTIDAD', null, null) as entidad, ");
		SQL.append("     planificacion.nivel_entidad_oee(OEE.id_entidad, 'OEE', OEE.orden, OEE.DENOMINACION_UNIDAD) as OEE, ");
		SQL.append("     ac.id_accion_cab, ");
		SQL.append("     PERSONA.DOCUMENTO_IDENTIDAD, ");
		SQL.append("     PERSONA.NOMBRES || ' ' || PERSONA.APELLIDOS as nombrePersona, ");
		SQL.append("     OEE.DENOMINACION_UNIDAD, ");
		SQL.append("     CASE ");
		SQL.append("         WHEN AC.ESTADO = 'P' ");
		SQL.append("         THEN 'PENDIENTE CON MEDIDA CAUTELAR' ");
		SQL.append("         WHEN AC.ESTADO = 'A' ");
		SQL.append("         THEN 'ACUERDO Y SENTENCIA' ");
		SQL.append("     END as estado, ");
		SQL.append("     AC.FECHA_ALTA, ");
		SQL.append("     AC.RESULTADO, ");
		SQL.append("     CASE ");
		SQL.append("         WHEN DE.DESCRIPCION IS NULL ");
		SQL.append("         THEN DET.OTRA_LEY ");
		SQL.append("         ELSE DE.DESCRIPCION ");
		SQL.append("     END AS LEY, ");
		SQL.append("     CASE ");
		SQL.append("         WHEN DET.ART_TODOS = TRUE ");
		SQL.append("         THEN 'SI' ");
		SQL.append("         ELSE 'NO, ESPECIFICAR: ' || DET.ART_ESPECIF ");
		SQL.append("     END as articulo ");
		SQL.append(" FROM ");
		SQL.append("     JURIDICOS.ACCION_INCONST_CAB AC ");
		SQL.append(" LEFT JOIN JURIDICOS.ACCION_INCONST_DET DET ");
		SQL.append(" ON ");
		SQL.append("     DET.ID_ACCION_CAB = AC.ID_ACCION_CAB ");
		SQL.append(" LEFT JOIN SELECCION.DATOS_ESPECIFICOS DE ");
		SQL.append(" ON ");
		SQL.append("     DE.ID_DATOS_ESPECIFICOS = DET.ID_DATOS_ESPECIF_LEY ");
		SQL.append(" JOIN GENERAL.PERSONA ");
		SQL.append(" ON ");
		SQL.append("     AC.ID_PERSONA = PERSONA.ID_PERSONA ");
		SQL.append(" JOIN GENERAL.EMPLEADO_PUESTO EMP ");
		SQL.append(" ON ");
		SQL.append("     EMP.ID_PERSONA = AC.ID_PERSONA ");
		SQL.append(" JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ");
		SQL.append(" ON ");
		SQL.append("     PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET ");
		SQL.append(" JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ");
		SQL.append(" ON ");
		SQL.append("     UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET ");
		SQL.append(" JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ");
		SQL.append(" ON ");
		SQL.append("     OEE.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO ");
		SQL.append(" WHERE ");
		SQL.append(" 1=1 ");

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			elWhere.append("AND planificacion.nivel_entidad_bool(OEE.id_entidad, 'SIN_NIVEL',"
					+ nivelEntidadOeeUtil.getIdSinNivelEntidad() + ", NULL) ");
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			elWhere.append("AND planificacion.nivel_entidad_bool(OEE.id_entidad, 'SIN_ENTIDAD',NULL, "
					+ nivelEntidadOeeUtil.getIdSinEntidad() + ") ");
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null) {
			elWhere.append(" AND OEE.ID_CONFIGURACION_UO = "
					+ nivelEntidadOeeUtil.getIdConfigCab());
		}
		if (fechaDesde != null && fechaHasta != null) {
			SQL.append("  AND to_date(to_char(AC.FECHA_ALTA,'DD-MM-YYYY'),'DD-MM-YYYY') >= to_date('"
					+ sdf.format(fechaDesde) + "','DD-MM-YYYY') ");
			SQL.append("  AND to_date(to_char(AC.FECHA_ALTA,'DD-MM-YYYY'),'DD-MM-YYYY') <= to_date('"
					+ sdf.format(fechaHasta) + "','DD-MM-YYYY') ");
		}

		if (opcionEspeArticulo != null
				&& opcionEspeArticulo.equalsIgnoreCase("ESPE_ARTICULO")) {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
			if (articuloEspecifico != null
					&& !articuloEspecifico.trim().isEmpty()) {
				if (!sufc.validarInput(articuloEspecifico.toString(),
						TiposDatos.STRING.getValor(), null))
					return null;
				elWhere.append(" AND DET.ART_ESPECIF LIKE '%"
						+ seguridadUtilFormController
						.caracterInvalido(articuloEspecifico
								.toLowerCase()) + "%'");
			}
		}
		if (idLey != null) {
			elWhere.append(" AND DE.ID_DATOS_ESPECIFICOS = " + idLey);
		}
		if (idEstado != null && !idEstado.equalsIgnoreCase("null")) {
			elWhere.append(" AND AC.ESTADO = '" + idEstado + "' ");
		}
		SQL.append(elWhere.toString());
		SQL.append(" order by OEE.ID_CONFIGURACION_UO asc, documento_identidad asc,AC.FECHA_ALTA asc ");
		SQL.append(" ) QRY order by OEE asc, documento_identidad asc,FECHA_ALTA asc");
		return SQL.toString();
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

	public String getArticuloEspecifico() {
		return articuloEspecifico;
	}

	public void setArticuloEspecifico(String articuloEspecifico) {
		this.articuloEspecifico = articuloEspecifico;
	}

	public String getOpcionEspeArticulo() {
		return opcionEspeArticulo;
	}

	public void setOpcionEspeArticulo(String opcionEspeArticulo) {
		this.opcionEspeArticulo = opcionEspeArticulo;
	}

	public List<SelectItem> getLeySelecItem() {
		return leySelecItem;
	}

	public void setLeySelecItem(List<SelectItem> leySelecItem) {
		this.leySelecItem = leySelecItem;
	}

	public Long getIdLey() {
		return idLey;
	}

	public void setIdLey(Long idLey) {
		this.idLey = idLey;
	}

	public String getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(String idEstado) {
		this.idEstado = idEstado;
	}

	public boolean isHabOee() {
		return habOee;
	}

	public void setHabOee(boolean habOee) {
		this.habOee = habOee;
	}

}
