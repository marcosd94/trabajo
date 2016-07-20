package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConsultasCapacitacion;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PublicacionCapacitacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("publicarSeleccionadosFC")
public class PublicarSeleccionadosFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2272586047513414465L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private Capacitaciones capacitacion = new Capacitaciones();
	private List<PostulacionCap> listaPostulaciones = new ArrayList<PostulacionCap>();

	private Long idCapacitacion;
	private Long idPais;
	private String ci;
	private Boolean cedula;
	private Boolean nombre;
	private Boolean codigo;
	private Boolean oee;
	private String from;

	public void init() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(), TiposDatos.LONG.getValor(), null))
				return;
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);

			seguridadUtilFormController.validarCapacitacion(idCapacitacion, capacitacion.getEstado(), ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS.getValor());

			cargarDatosNivel();
			searchAll();
		}

	}

	public void initEdit() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(), TiposDatos.LONG.getValor(), null))
				return;
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);

			cargarDatosNivel();
			searchAll();
		}

	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			if (capacitacion.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {

		// Nivel
		Long idSinNivelEntidad =
			nivelEntidadOeeUtil.getIdSinNivelEntidad(capacitacion.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(capacitacion.getConfiguracionUoCab().getEntidad().getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(capacitacion.getConfiguracionUoCab().getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU530_imprimir_eval_postulante_487");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab uo = em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			String elWhere = " ";
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa", capacitacion.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa", capacitacion.getDatosEspecificosTipoCap().getDescripcion());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado", uo.getDenominacionUnidad());

			elWhere = "  AND ET.ID_CAPACITACION = " + idCapacitacion;
			elWhere +=
				"  AND ET.TIPO = "
					+ (capacitacion.getTipoSeleccion().equals("C") ? "'EVAL_POST'"
						: "'EVAL_INSCRIP'");
			if (idPais == null)
				reportUtilFormController.getParametros().put("pais", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(idPais.toString(), TiposDatos.INTEGER.getValor(), null)) {
					elWhere += "  AND P.id_pais_expedicion_doc = " + (new Date()).getTime();
				} else {
					reportUtilFormController.getParametros().put("pais", em.find(Pais.class, idPais).getDescripcion());
					elWhere += "  AND P.id_pais_expedicion_doc = " + idPais;
				}
			}

			if (ci == null || ci.trim().isEmpty())
				reportUtilFormController.getParametros().put("cedula", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(ci, TiposDatos.STRING.getValor(), null)) {
					reportUtilFormController.getParametros().put("cedula", ci);
					elWhere += "  AND P.documento_identidad = '" + (new Date()).getTime() + "'";
				} else {
					reportUtilFormController.getParametros().put("cedula", ci);
					elWhere +=
						"  AND P.documento_identidad = '"
							+ seguridadUtilFormController.caracterInvalido(ci) + "'";
				}
			}
			reportUtilFormController.getParametros().put("mostrar", "SELECCIONADOS");
			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("elWhere", elWhere);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String obtenerSql() {
		String parametro1 = null;
		if (capacitacion.getTipoSeleccion() != null) {
			if (capacitacion.getTipoSeleccion().equals("C"))
				parametro1 = "'EVAL_POST'";
			if (capacitacion.getTipoSeleccion().equals("I"))
				parametro1 = "'EVAL_INSCRIP'";
		}
		String sql =
			"SELECT POST.* FROM CAPACITACION.EVALUACION_INSC_POST EI "
				+ "JOIN CAPACITACION.EVALUACION_TIPO ET ON EI.ID_EVAL = ET.ID_EVAL "
				+ "JOIN CAPACITACION.POSTULACION_CAP POST  ON POST.ID_POSTULACION = EI.ID_POSTULACION "
				+ "JOIN GENERAL.PERSONA P ON P.ID_PERSONA = POST.ID_PERSONA "
				+ "JOIN GENERAL.PAIS ON P.ID_PAIS_EXPEDICION_DOC = PAIS.ID_PAIS "
				+ "LEFT JOIN GENERAL.EMPLEADO_PUESTO EMP ON POST.ID_EMPLEADO_PUESTO = EMP.ID_EMPLEADO_PUESTO "
				+ "LEFT JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "
				+ "LEFT JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
				+ "LEFT JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "WHERE EI.ACTIVO = TRUE " + " AND EI.RESULTADO = 'S'"
				+ " AND ET.ID_CAPACITACION = " + capacitacion.getIdCapacitacion();
		if (parametro1 != null)
			sql += " AND ET.TIPO = " + parametro1;
		return sql;
	}

	public void searchAll() {
		String sql = obtenerSql();
		sql += " ORDER BY ET.TIPO";

		listaPostulaciones = em.createNativeQuery(sql, PostulacionCap.class).getResultList();
		ci = null;
		idPais = null;
	}

	public void search() throws Exception {
		listaPostulaciones = new ArrayList<PostulacionCap>();
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String sql = obtenerSql();
		if (idPais != null) {
			if (!sufc.validarInput(idPais.toString(), TiposDatos.LONG.getValor(), null))
				return;
			sql += " AND PAIS.ID_PAIS = " + idPais;
		}
		if (ci != null && !ci.trim().isEmpty()) {
			if (!sufc.validarInput(ci, TiposDatos.STRING.getValor(), null))
				return;
			sql +=
				" AND P.DOCUMENTO_IDENTIDAD = "
					+ seguridadUtilFormController.caracterInvalido(ci.toLowerCase());
		}
		sql += " AND POST.ACTIVO = TRUE ORDER BY ET.TIPO";

		listaPostulaciones = em.createNativeQuery(sql, PostulacionCap.class).getResultList();

	}

	private Boolean check() {
		if (!cedula && !codigo && !nombre && !oee)
			return false;
		return true;
	}

	public String publicarSeleccionados() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.WARN, "Debe especificar una opción en el panel Datos a Mostrar en el Portal SICCA antes de realizar esta acción");
			return null;
		}
		try {
			Referencias ref = new Referencias();
			ref =
				referenciasUtilFormController.getReferencia("PUBLIC_CAPACITACION", "SELECCIONADOS");
			PublicacionCapacitacion publicacion = new PublicacionCapacitacion();
			publicacion.setActivo(true);
			publicacion.setBloque("SELECCIONADOS");
			publicacion.setCapacitaciones(capacitacion);
			publicacion.setFechaAlta(new Date());
			publicacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			publicacion.setTexto(generarTexto());
			publicacion.setOrden(ref.getValorNum());
			em.persist(publicacion);

			Referencias referencias =
				referenciasUtilFormController.getReferencia("ESTADOS_CAPACITACION", "FINALIZADO CIRCUITO");
			capacitacion.setEstado(referencias.getValorNum());
			capacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitacion.setFechaMod(new Date());
			em.merge(capacitacion);

			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_PUBLICAR_SELECCIONADOS);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitacion);
			jbpmUtilFormController.setTransition("pubSel_TO_end");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
		} catch (Exception e) {
			return null;
		}

		return "ok";
	}

	private String generarTexto() {
		StringBuffer texto = new StringBuffer();
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "<br/>";
		String tableO = "<table>";
		String tableC = "</table>";
		String trO = "<tr>";
		String trC = "</tr>";
		String tdO = "<td>";
		String tdC = "</td>";
		String pO = "<p>";
		String pC = "</p>";
		texto.append(spanO + "POSTULANTES SELECCIONADOS " + spanC + br);
		texto.append(pO + "Se ha seleccionado a los postulantes:" + pC + br);
		String text = tableO + trO;
		if (codigo)
			text += tdO + "Código Postulante" + tdC;
		if (nombre)
			text += tdO + "Nombre y Apellido" + tdC;
		if (cedula)
			text += tdO + "Cédula de Identidad" + tdC;
		if (oee)
			text += tdO + "OEE" + tdC;
		texto.append(text + trC);
		for (PostulacionCap l : listaPostulaciones) {
			String datos = trO;
			if (codigo)
				datos += tdO + l.getUsuFicha() + tdC;
			if (nombre)
				datos +=
					tdO + l.getPersona().getNombres() + " " + l.getPersona().getApellidos() + tdC;
			if (cedula)
				datos += tdO + l.getPersona().getDocumentoIdentidad() + tdC;
			if (oee)
				datos +=
					tdO
						+ l.getEmpleadoPuesto().getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getDenominacionUnidad()
						+ tdC;
			texto.append(datos + trC);
		}
		texto.append(tableC);

		return texto.toString();
	}

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public List<PostulacionCap> getListaPostulaciones() {
		return listaPostulaciones;
	}

	public void setListaPostulaciones(List<PostulacionCap> listaPostulaciones) {
		this.listaPostulaciones = listaPostulaciones;
	}

	public Boolean getCedula() {
		return cedula;
	}

	public void setCedula(Boolean cedula) {
		this.cedula = cedula;
	}

	public Boolean getNombre() {
		return nombre;
	}

	public void setNombre(Boolean nombre) {
		this.nombre = nombre;
	}

	public Boolean getCodigo() {
		return codigo;
	}

	public void setCodigo(Boolean codigo) {
		this.codigo = codigo;
	}

	public Boolean getOee() {
		return oee;
	}

	public void setOee(Boolean oee) {
		this.oee = oee;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	

}
