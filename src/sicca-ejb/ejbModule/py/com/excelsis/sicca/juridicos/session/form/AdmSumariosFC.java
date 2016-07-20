package py.com.excelsis.sicca.juridicos.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.EstadoJuez;
import enums.EstadoSumario;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Sancion;
import py.com.excelsis.sicca.entity.SumarioCab;
import py.com.excelsis.sicca.entity.SumarioDet;
import py.com.excelsis.sicca.juridicos.session.SumarioCabHome;
import py.com.excelsis.sicca.juridicos.session.SumarioCabList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("admSumariosFC")
public class AdmSumariosFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733138495988896462L;
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
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	SumarioCabList sumarioCabList;

	@In(create = true)
	SumarioCabHome sumarioCabHome;

	@In(create = true)
	GrupoPuestosController grupoPuestosController;

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	private String cedula;
	private String nombre;
	private String apellido;
	private Date fechaDesde;
	private Date fechaHasta;
	private EstadoSumario estadoSumario;

	private List<SumarioCab> listaSumarioCab;

	// Edit
	private Long idSumarioCab;
	private SumarioCab sumarioCab;
	private EmpleadoPuesto empleadoPuesto;
	private Long idEmpleadoPuesto;
	private Boolean seleccionarTodos = false;
	private List<DatosEspecificos> listaFaltas;
	private Boolean nuevoCreado = false;
	private String ubicacionFisica = "";

	private EstadoJuez estadoJuez;
	private EstadoSumario estadoMai;
	private Long idSancionJuez;
	private Long idSancionMai;
	private Boolean inhabilitaFuncionarioJuez;
	private Boolean inhabilitaFuncionarioMai;

	private String from;

	private boolean primeraEntrada = true;
	private boolean habOee;
	private Long idPaisExp;
	private Integer expInterno;
	private Integer anioExpInterno;
	private Integer expSfp;
	private Integer anioExpSfp;
	private String descripcionPaisExp;

	/**
	 * Método que inicia los parametros
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		nivelEntidadOeeUtil.init();
		if (primeraEntrada) {
			idPaisExp = idParaguay();
			primeraEntrada = false;
			nivelEntidadOeeUtil.limpiar();
			if (esRolHomologar())
				habOee = true;
			else
				habOee = false;

			cargarCabecera();
		}
		search();
	}

	/**
	 * Verifica si el usuario tiene Rol homologar
	 * */
	@SuppressWarnings("unchecked")
	private boolean esRolHomologar() {
		List<UsuarioRol> uRols =
			em.createQuery("Select d FROM UsuarioRol d " + " WHERE d.usuario.idUsuario="
				+ usuarioLogueado.getIdUsuario() + " AND d.rol.homologador=TRUE ").getResultList();
		if (uRols.isEmpty())
			return false;
		return true;
	}

	/**
	 * Carga El Nivel Entidad Oee del usuario
	 * */
	public void cargarCabecera() {

		// Nivel
		ConfiguracionUoCab c =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Long idSinNivelEntidad =
			nivelEntidadOeeUtil.getIdSinNivelEntidad(c.getEntidad().getSinEntidad().getNenCodigo());

		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		Long idSinEnt = c.getEntidad().getSinEntidad().getIdSinEntidad();
		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(idSinEnt);

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	/**
	 * Retorna el id paraguay
	 * */
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public void searchAll() throws Exception {
		cedula = null;
		nombre = null;
		apellido = null;
		fechaDesde = null;
		fechaHasta = null;
		estadoSumario = EstadoSumario.TODOS;
		expInterno = null;
		expSfp = null;
		anioExpInterno = null;
		anioExpSfp = null;
		search();
	}

	public void search() throws Exception {
		if (fechaDesde != null && fechaHasta != null && fechaDesde.after(fechaHasta)) {
			statusMessages.add(Severity.ERROR, "El rango de fechas es inv\u00E1lido, la Fecha Desde no debe superar la Fecha Hasta.");
			return;
		}

		buscar();
	}
	/**
	 * Genera la consulta para el ser enviado al reporte
	 * */
	private String generarConsulta() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT ");
		SQL.append("     planificacion.nivel_entidad_oee(OEE.id_entidad, 'SIN_NIVEL', null, null) as nivel, ");
		SQL.append("     planificacion.nivel_entidad_oee(OEE.id_entidad, 'SIN_ENTIDAD', null, null) as entidad, ");
		SQL.append("     planificacion.nivel_entidad_oee(OEE.id_entidad, 'OEE', OEE.orden, OEE.DENOMINACION_UNIDAD) as OEE, ");
		SQL.append("     SC.ID_SUMARIO_CAB      AS ID ,    ");
		SQL.append("     PERSONA.DOCUMENTO_IDENTIDAD, ");
		SQL.append("     PAIS.DESCRIPCION, ");
		SQL.append("     PERSONA.NOMBRES || ' ' || PERSONA.APELLIDOS AS FUNCIONARIO, ");
		SQL.append("     SC.NRO_EXP || '/'||SC.ANIO_EXP              AS nro_interno, ");
		SQL.append("     SC.NRO_EXP_SFP || '/'||SC.ANIO_EXP_SFP      AS nro_sfp, ");
		SQL.append("     SC.FECHA_ALTA                               AS FECHA, ");
		SQL.append("     SC.OBS_J                                    AS OBS_J, ");
		SQL.append("     PAIS.descripcion                            AS paisExpDoc,");
		SQL.append("     CASE ");
		SQL.append("         WHEN SC.ESTADO = 'EC' ");
		SQL.append("         THEN 'EN CURSO' ");
		SQL.append("         WHEN SC.ESTADO = 'RJ' ");
		SQL.append("         THEN 'CON RESOLUCION DEL JUEZ' ");
		SQL.append("         WHEN SC.ESTADO = 'SO' ");
		SQL.append("         THEN 'SOBRESEIMIENTO' ");
		SQL.append("         WHEN SC.ESTADO = 'SA' ");
		SQL.append("         THEN 'SANCION' ");
		SQL.append("         WHEN SC.ESTADO = 'AR' ");
		SQL.append("         THEN 'ARCHIVO' ");
		SQL.append("     END AS ESTADO_SUMARIO, ");
		SQL.append("     CASE ");
		SQL.append("         WHEN SC.ESTADO_JUEZ = 'SO' ");
		SQL.append("         THEN 'SOBRESEIMIENTO' ");
		SQL.append("         WHEN SC.ESTADO_JUEZ = 'SA' ");
		SQL.append("         THEN 'SANCION' ||' - '|| S.DESCRIPCION ||' - '|| SC.TIEMPO_J ||' '|| ");
		SQL.append("             CASE ");
		SQL.append("                 WHEN SC.EXPRESADO_J = 'D' ");
		SQL.append("                 THEN 'DÍA/S' ");
		SQL.append("                 WHEN SC.EXPRESADO_J = 'M' ");
		SQL.append("                 THEN 'MES/ES' ");
		SQL.append("                 WHEN SC.EXPRESADO_J = 'A' ");
		SQL.append("                 THEN 'AÑO/S' ");
		SQL.append("             END ");
		SQL.append("         WHEN SC.ESTADO_JUEZ = 'AR' ");
		SQL.append("         THEN 'ARCHIVO' ");
		SQL.append("     END      AS RECOMENDACION_JUEZ, ");
		SQL.append("     SC.OBS_J AS OBS_JUEZ, ");
		SQL.append("     CASE ");
		SQL.append("         WHEN SC.ESTADO = 'SO' ");
		SQL.append("         THEN 'SOBRESEIMIENTO - MAI' ");
		SQL.append("         WHEN SC.ESTADO = 'SA' ");
		SQL.append("         THEN 'SANCION - MAI - '|| SD.DESCRIPCION ||' '|| ");
		SQL.append("             CASE ");
		SQL.append("                 WHEN SD.INHABILITA = TRUE ");
		SQL.append("                 THEN SC.TIEMPO_M ||' '|| ");
		SQL.append("                     CASE ");
		SQL.append("                         WHEN SC.EXPRESADO_M = 'D' ");
		SQL.append("                         THEN 'DÍA/S' ");
		SQL.append("                         WHEN SC.EXPRESADO_M = 'M' ");
		SQL.append("                         THEN 'MES/ES' ");
		SQL.append("                         WHEN SC.EXPRESADO_M = 'A' ");
		SQL.append("                         THEN 'AÑO/S' ");
		SQL.append("                     END ");
		SQL.append("                 ELSE SD.DESCRIPCION ");
		SQL.append("             END ");
		SQL.append("         WHEN SC.ESTADO = 'AR' ");
		SQL.append("         THEN 'ARCHIVO - MAI' ");
		SQL.append("     END            AS RECOMENDACION_MAI, ");
		SQL.append("     SC.OBS_M       AS OBS_MAI, ");
		SQL.append("     DE.DESCRIPCION AS FALTAS_COMETIDAS ");
		SQL.append(" FROM ");
		SQL.append("     JURIDICOS.SUMARIO_CAB SC ");
		SQL.append(" LEFT JOIN JURIDICOS.SANCION S ");
		SQL.append(" ON ");
		SQL.append("     S.ID_SANCION = SC.ID_SANCION_J ");
		SQL.append(" LEFT JOIN JURIDICOS.SANCION SD ");
		SQL.append(" ON ");
		SQL.append("     SD.ID_SANCION = SC.ID_SANCION_M ");
		SQL.append(" JOIN GENERAL.PERSONA ");
		SQL.append(" ON ");
		SQL.append("     SC.ID_PERSONA = PERSONA.ID_PERSONA ");
		SQL.append(" JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ");
		SQL.append(" ON ");
		SQL.append("     OEE.ID_CONFIGURACION_UO = SC.ID_CONFIGURACION_UO ");
		SQL.append(" JOIN JURIDICOS.SUMARIO_DET SUMD ");
		SQL.append(" ON ");
		SQL.append("     SUMD.ID_SUMARIO_CAB = SC.ID_SUMARIO_CAB ");
		SQL.append(" JOIN SELECCION.DATOS_ESPECIFICOS DE ");
		SQL.append(" ON ");
		SQL.append("     DE.ID_DATOS_ESPECIFICOS= SUMD.ID_DATOS_ESPECIF_FALTA ");
		SQL.append(" JOIN GENERAL.PAIS ");
		SQL.append(" ON ");
		SQL.append("     PAIS.ID_PAIS = PERSONA.ID_PAIS_EXPEDICION_DOC ");
		SQL.append(" WHERE ");
		SQL.append("    OEE.ID_CONFIGURACION_UO = "
			+ grupoPuestosController.getConfiguracionUoCab().getIdConfiguracionUo());

		if (!Utiles.vacio(cedula)) {
			SQL.append("         and ( ");
			SQL.append("             lower(PERSONA.documento_identidad) = '"
				+ seguridadUtilFormController.caracterInvalido(cedula) + "' ");
			SQL.append("         )  ");
		}
		if (!Utiles.vacio(nombre)) {
			SQL.append("         and ( ");
			SQL.append("             lower(PERSONA.NOMBRES) like lower('"
				+ "%"+seguridadUtilFormController.caracterInvalido(nombre.toLowerCase()).trim() + "%') ");
			SQL.append("         )  ");
		}
		if (!Utiles.vacio(apellido)) {
			SQL.append("         and ( ");
			SQL.append("             lower(PERSONA.APELLIDOS) like lower('%"
				+ seguridadUtilFormController.caracterInvalido(apellido.toLowerCase()).trim() + "%') ");
			SQL.append("         )  ");
		}
		if (fechaDesde != null) {
			SQL.append("         and cast( to_char(SC.FECHA_ALTA, 'YYYY-MM-DD') as date)>=cast('"
				+ sdf.format(fechaDesde) + "' as date)  ");
		}
		if (fechaHasta != null) {
			SQL.append("         and cast( to_char(SC.FECHA_ALTA, 'YYYY-MM-DD') as date)<=cast('"
				+ sdf.format(fechaHasta) + "' as date)  ");
		}
		if (estadoSumario != null && !EstadoSumario.TODOS.equals(estadoSumario)) {
			SQL.append("         and SC.ESTADO= '" + estadoSumario.getValor() + "'");
		}
		if (expSfp != null) {
			SQL.append("         and SC.nro_exp_sfp=  " + expSfp);
		}
		if(anioExpSfp!=null){
			SQL.append("         and SC.anio_exp_sfp=  " + anioExpSfp);
		}
		if(expInterno!=null){
			SQL.append("         and SC.nro_exp=  " + expInterno);
		}
		if(anioExpInterno!=null){
			SQL.append("         and SC.anio_exp=  " + anioExpInterno);
		}
		SQL.append("   order by  SC.ID_SUMARIO_CAB, OEE.DENOMINACION_UNIDAD ");

		return SQL.toString();
	}

	public void imprimir() {
		try {
			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.setNombreReporte("RPT_CU462_LISTADO_SUMARIOS");
			cargarParametros();
			reportUtilFormController.imprimirReportePdf();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * Carga los parametros a ser enviado en el reporte
	 * */
	private void cargarParametros() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String codNivel = grupoPuestosController.getSinNivelEntidad().getNenCodigo().toString();
		String codEntidad =
			codNivel + "." + grupoPuestosController.getSinEntidad().getEntCodigo().toString();
		String codOee =
			codEntidad + "." + grupoPuestosController.getConfiguracionUoCab().getOrden().toString();

		reportUtilFormController.getParametros().put("nivelFIL", codNivel + " "
			+ grupoPuestosController.getSinNivelEntidad().getNenNombre());
		reportUtilFormController.getParametros().put("oeeFIL", codOee + " "
			+ grupoPuestosController.getConfiguracionUoCab().getDenominacionUnidad());
		reportUtilFormController.getParametros().put("paisExpFIL", null);
		reportUtilFormController.getParametros().put("nombreFIL", nombre == null ? "-" : nombre);
		reportUtilFormController.getParametros().put("estadoFIL", estadoSumario.getDescripcion());

		reportUtilFormController.getParametros().put("fecAltaDesdeFIL", fechaDesde == null ? "-"
			: sdf.format(fechaDesde));
		reportUtilFormController.getParametros().put("entidadFIL", codEntidad + " "
			+ grupoPuestosController.getSinEntidad().getEntNombre());
		reportUtilFormController.getParametros().put("ciFIL", null);
		reportUtilFormController.getParametros().put("apellidoFIL", apellido == null ? "-"
			: apellido);
		reportUtilFormController.getParametros().put("nroAnhoFIL", null);
		reportUtilFormController.getParametros().put("fecAltaHastaFIL", fechaHasta == null ? "-"
			: sdf.format(fechaHasta));

		reportUtilFormController.getParametros().put("laConsulta", generarConsulta());
		ConfiguracionUoCab uo = new ConfiguracionUoCab();
		Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		uo = em.find(ConfiguracionUoCab.class, id);
		reportUtilFormController.getParametros().put("oeeUsuarioLogueado", uo.getDenominacionUnidad());
		reportUtilFormController.getParametros().put("nivel", grupoPuestosController.getSinNivelEntidad().getNenNombre());
		reportUtilFormController.getParametros().put("entidad", grupoPuestosController.getSinEntidad().getEntNombre());
		reportUtilFormController.getParametros().put("oee", grupoPuestosController.getConfiguracionUoCab().getDenominacionUnidad());
		reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController.getParametros().put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));
	}

	public void buscar() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String consulta =
			"" + "select sumarioCab from SumarioCab sumarioCab "
				+ " join sumarioCab.empleadoPuesto empleadoPuesto "
				+ " join empleadoPuesto.persona persona "
				+ " join empleadoPuesto.plantaCargoDet plantaCargoDet "
				+ " join plantaCargoDet.configuracionUoDet configuracionUoDet "
				+ " join configuracionUoDet.configuracionUoCab configuracionUoCab "
				+ " where configuracionUoCab.idConfiguracionUo = :idConfiguracionUo";

		if (!Utiles.vacio(cedula)) {
			if (!sufc.validarInput(cedula.toString(), TiposDatos.STRING.getValor(), null))
				return;

			consulta += " and lower(persona.documentoIdentidad) = :cedula ";
		}

		if (!Utiles.vacio(nombre)) {
			if (!sufc.validarInput(nombre.toString(), TiposDatos.STRING.getValor(), null))
				return;
			consulta += " and lower(persona.nombres) like :nombre ";
		}

		if (!Utiles.vacio(apellido)) {
			if (!sufc.validarInput(apellido.toString(), TiposDatos.STRING.getValor(), null))
				return;
			consulta += " and lower(persona.apellidos) like :apellido ";
		}
		if (!Utiles.vacio(cedula)) {
			if (!sufc.validarInput(cedula.toString(), TiposDatos.STRING.getValor(), null))
				return;
			consulta += " and lower(persona.documentoIdentidad)= :cedula ";
		}

		if (fechaDesde != null) {
			consulta += " and cast(sumarioCab.fechaAlta as date) >= cast(:fechaDesde as date)  ";
		}

		if (fechaHasta != null) {
			consulta += " and cast(sumarioCab.fechaAlta as date) <= cast(:fechaHasta as date) ";
		}

		if (estadoSumario != null && !EstadoSumario.TODOS.equals(estadoSumario)) {
			consulta += " and sumarioCab.estado = :estado ";
		}
		if (expInterno != null) {
			if (!sufc.validarInput(expInterno.toString(), TiposDatos.INTEGER.getValor(), null))
				return;
			consulta += " and sumarioCab.nroExp =:expInterno ";
		}
		if (anioExpInterno != null) {
			if (!sufc.validarInput(anioExpInterno.toString(), TiposDatos.INTEGER.getValor(), null))
				return;
			consulta += " and sumarioCab.anioExp =:anioExpInterno  ";
		}
		if (expSfp != null) {
			if (!sufc.validarInput(expSfp.toString(), TiposDatos.INTEGER.getValor(), null))
				return;
			consulta += " and sumarioCab.nroExpSfp =:expSfp ";
		}
		if (anioExpSfp != null) {
			if (!sufc.validarInput(anioExpSfp.toString(), TiposDatos.INTEGER.getValor(), null))
				return;
			consulta += " and sumarioCab.anioExpSfp =:anioExpSfp ";
		}
		if (idPaisExp != null) {
			consulta += " and persona.paisByIdPaisExpedicionDoc.idPais =:idPaisExp ";
		}

		Map<String, QueryValue> params = new HashMap<String, QueryValue>();
		params.put("idConfiguracionUo", new QueryValue(nivelEntidadOeeUtil.getIdConfigCab()));

		if (!Utiles.vacio(cedula))
			params.put("cedula", new QueryValue("%" + cedula.toLowerCase() + "%"));

		if (!Utiles.vacio(nombre))
			params.put("nombre", new QueryValue("%" + nombre.toLowerCase() + "%"));

		if (!Utiles.vacio(apellido))
			params.put("apellido", new QueryValue("%" + apellido.toLowerCase() + "%"));

		if (!Utiles.vacio(cedula))
			params.put("cedula", new QueryValue(cedula));

		if (fechaDesde != null)
			params.put("fechaDesde", new QueryValue(fechaDesde));

		if (fechaHasta != null)
			params.put("fechaHasta", new QueryValue(fechaHasta));

		if (estadoSumario != null && !EstadoSumario.TODOS.equals(estadoSumario))
			params.put("estado", new QueryValue(estadoSumario.getValor()));

		if (expInterno != null) {
			params.put("expInterno", new QueryValue(expInterno));
		}
		if (anioExpInterno != null) {
			params.put("anioExpInterno", new QueryValue(anioExpInterno));
		}
		if (expSfp != null) {
			params.put("expSfp", new QueryValue(expSfp));
		}
		if (anioExpSfp != null) {
			params.put("anioExpSfp", new QueryValue(anioExpSfp));
		}
		if (idPaisExp != null) {
			params.put("idPaisExp", new QueryValue(idPaisExp));
		}

		listaSumarioCab = sumarioCabList.listaResultadosCU455(consulta, params);
		System.out.println("---");
	}

	/**********************************************************************************/
	/********************************* EDITAR *****************************************/
	/**********************************************************************************/

	public void initEdit() {
		if (isNew()) {
			// Nuevo
			nuevoCreado = false;
			if (idEmpleadoPuesto == null) {
				clearEdit();
				sumarioCab = new SumarioCab();
				estadoSumario = EstadoSumario.EN_CURSO;
				sumarioCab.setEstado(estadoSumario.getValor());
				cedula = null;
				listaFaltas = cargarListaFaltas();
			} else {
				empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
				cedula = empleadoPuesto.getPersona().getDocumentoIdentidad();
				descripcionPaisExp =
					empleadoPuesto.getPersona().getPaisByIdPaisExpedicionDoc().getDescripcion();
			}
		} else {
			// editar
			if (idSumarioCab == null)
				return;

			sumarioCab = em.find(SumarioCab.class, idSumarioCab);
			estadoSumario = EstadoSumario.getEstadoPorValor(sumarioCab.getEstado());
			empleadoPuesto = sumarioCab.getEmpleadoPuesto();
			cedula = empleadoPuesto.getPersona().getDocumentoIdentidad();
			descripcionPaisExp =
				empleadoPuesto.getPersona().getPaisByIdPaisExpedicionDoc().getDescripcion();
			nuevoCreado = false;
			cargarDetalles();
			cargarDetalleJuez(sumarioCab);
			cargarDetalleMai(sumarioCab);
		}
		sumarioCabHome.setInstance(sumarioCab);

		if (empleadoPuesto != null && sumarioCab.getPersona() != null) {
			Calendar c = Calendar.getInstance();
			ubicacionFisica =
				"\\SICCA\\" + c.get(Calendar.YEAR) + "\\PJ\\SUM\\"
					+ sumarioCab.getPersona().getIdPersona();
		}
	}

	public void clearEdit() {
		cedula = null;
		nombre = null;
		apellido = null;
		setSeleccionarTodos(false);
		idEmpleadoPuesto = null;
		empleadoPuesto = null;
		estadoSumario = EstadoSumario.TODOS;
	}

	public boolean isNew() {
		if (idSumarioCab == null)
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	private List<DatosEspecificos> cargarListaFaltas() {
		try {
			String sql =
				" SELECT DE.* " + " FROM SELECCION.DATOS_ESPECIFICOS DE "
					+ " JOIN SELECCION.DATOS_GENERALES DG "
					+ " ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
					+ " WHERE DG.DESCRIPCION = 'FALTAS' " + " AND DE.ACTIVO = TRUE ";

			List<DatosEspecificos> lista =
				em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String save() {
		if (!validate()) {
			return null;
		}

		Date fecha = new Date();
		if (isNew()) {
			// Crear
			sumarioCab.setEmpleadoPuesto(empleadoPuesto);
			sumarioCab.setFechaAlta(fecha);
			sumarioCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			sumarioCab.setPersona(empleadoPuesto.getPersona());
			sumarioCab.setConfiguracionUoCab(grupoPuestosController.getConfiguracionUoCab());
			em.persist(sumarioCab);

			// Guardar detalle
			for (DatosEspecificos datosEspecificos : listaFaltas) {
				if (datosEspecificos.getSeleccionado()) {
					SumarioDet sumarioDet = new SumarioDet();
					sumarioDet.setDatosEspecificos(datosEspecificos);
					sumarioDet.setSumarioCab(sumarioCab);
					sumarioDet.setFechaAlta(fecha);
					sumarioDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(sumarioDet);
				}
			}

			em.flush();
			nuevoCreado = true;
			idSumarioCab = sumarioCab.getIdSumarioCab();
			sumarioCabHome.setInstance(sumarioCab);

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			statusMessages.add(Severity.WARN, "Si desea adjuntar un documento, presione el botón Anexos si no, presione el botón volver");
			return null;
		} else {
			// Editar
			if (EstadoSumario.EN_CURSO.getValor().equals(sumarioCab.getEstado())) {
				// Aqui deberia entrar la PRIMERA vez que se realiza un update.
				// Guardar Panel JUEZ
				sumarioCab.setEstadoJuez(estadoJuez.getValor());
				sumarioCab.setEstado(EstadoSumario.CON_RESOLUCION_JUEZ.getValor());
				if (estadoJuez.getDescripcion().equals("SANCION")) {
					Sancion sancion = em.find(Sancion.class, idSancionJuez);
					sumarioCab.setSancionJuez(sancion);
				}

			} else if (EstadoSumario.CON_RESOLUCION_JUEZ.getValor().equals(sumarioCab.getEstado())) {
				// Aqui deberia entrar la SEGUNDA vez que se realiza un update.
				// Guardar Panel MAI
				sumarioCab.setEstado(estadoMai.getValor());
				if (EstadoSumario.SANCION.equals(estadoMai)) {
					Sancion sancion = em.find(Sancion.class, idSancionMai);
					sumarioCab.setSancionMai(sancion);

					// ETC: Si el registro de la sanción tiene INHABILITA =
					// TRUE, Genera un registro en la tabla de INHABILITADOS
					if (sancion.getInhabilita()) {
						Inhabilitados inhabilitados = new Inhabilitados();
						inhabilitados.setEmpleadoPuesto(sumarioCab.getEmpleadoPuesto());
						inhabilitados.setInhabilitado(true);
						inhabilitados.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						inhabilitados.setFechaAlta(fecha);
						inhabilitados.setTipo("J");
						inhabilitados.setPersona(sumarioCab.getPersona());
						inhabilitados.setConfiguracionUoCab(sumarioCab.getConfiguracionUoCab());
						inhabilitados.setFechaDesde(sumarioCab.getFechaDesde());

						Calendar c = Calendar.getInstance();
						c.setTime(sumarioCab.getFechaDesde());

						if ("A".equals(sumarioCab.getExpresadoM()))
							c.add(Calendar.YEAR, sumarioCab.getTiempoM());
						else if ("M".equals(sumarioCab.getExpresadoM()))
							c.add(Calendar.MONTH, sumarioCab.getTiempoM());
						else if ("D".equals(sumarioCab.getExpresadoM()))
							c.add(Calendar.DAY_OF_MONTH, sumarioCab.getTiempoM());

						inhabilitados.setFechaHasta(c.getTime());
						em.persist(inhabilitados);
						sumarioCab.setInhabilitados(inhabilitados);
						em.merge(sumarioCab);
					}

					// ETC: Si el registro de la sanción tiene DESVINCULA =
					// TRUE, Generar un registro en la tabla DESVINCULACION
					if (sancion.getDesvincula()) {
						Desvinculacion desvinculacion = new Desvinculacion();
						desvinculacion.setEmpleadoPuesto(sumarioCab.getEmpleadoPuesto());
						DatosEspecificos datosEspecificos = getMotivoDesvinculacion();
						desvinculacion.setDatosEspecifMotivo(datosEspecificos);
						desvinculacion.setFechaDesvinculacion(sumarioCab.getFechaDesde());
						desvinculacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						desvinculacion.setFechaAlta(fecha);
						
						/***
						 * Empleadopuesto pasa a inactivo
						 * */
						EmpleadoPuesto empleadoPuestoDesvinculado=em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
						empleadoPuestoDesvinculado.setFechaMod(new Date());
						empleadoPuestoDesvinculado.setActual(false);
						empleadoPuestoDesvinculado.setFechaFin(sumarioCab.getFechaDesde());
						empleadoPuestoDesvinculado.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(empleadoPuestoDesvinculado);
						/***
						 * puesto a vacante
						 * */
						PlantaCargoDet puesto= em.find(PlantaCargoDet.class,empleadoPuestoDesvinculado.getPlantaCargoDet().getIdPlantaCargoDet() );
						puesto.setEstadoCab(seleccionUtilFormController
								.buscarEstadoCab("VACANTE"));
						puesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
						puesto.setFechaMod(new Date());
						em.merge(puesto);
						
						em.persist(desvinculacion);
						sumarioCab.setDesvinculacion(desvinculacion);
						em.merge(sumarioCab);

					}
				}
			}

			sumarioCab.setFechaMod(fecha);
			sumarioCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(sumarioCab);
			em.flush();
			clearEdit();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		}

		return "persisted";
	}

	/**
	 * Carga la lista de detalles: solicProrrogaDetList
	 */
	private void cargarDetalles() {
		String sql =
			" SELECT DE.* " + " FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ " JOIN SELECCION.DATOS_GENERALES DG "
				+ " ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ " JOIN JURIDICOS.SUMARIO_DET SD "
				+ " ON DE.id_datos_especificos = SD.id_datos_especif_falta " +

				" WHERE DG.DESCRIPCION = 'FALTAS' " + " AND DE.ACTIVO = TRUE "
				+ " and sd.id_sumario_cab = " + idSumarioCab + "" + " ORDER BY DE.DESCRIPCION ASC";

		listaFaltas = em.createNativeQuery(sql, DatosEspecificos.class).getResultList();

		for (DatosEspecificos datosEspecificos : listaFaltas) {
			datosEspecificos.setSeleccionado(true);
			seleccionarTodos = true;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<SelectItem> getSancionesSelectItem() {
		List<SelectItem> listaSI = new ArrayList<SelectItem>();

		String consulta =
			"select sancion from Sancion sancion where sancion.activo is true order by sancion.descripcion";
		Query q = em.createQuery(consulta);
		List<Sancion> lista = q.getResultList();

		listaSI.add(new SelectItem(null, "Seleccione.."));
		for (Sancion sancion : lista) {
			listaSI.add(new SelectItem(sancion.getIdSancion(), sancion.getDescripcion()));
		}

		return listaSI;
	}

	public List<SelectItem> getTiempoSelectItem() {
		List<SelectItem> listaSI = new ArrayList<SelectItem>();
		listaSI.add(new SelectItem("A", "Año/s"));
		listaSI.add(new SelectItem("M", "Mes/es"));
		listaSI.add(new SelectItem("D", "Día/s"));
		return listaSI;
	}

	/**
	 * Valida el formaulario de edicion
	 * 
	 * @return
	 */
	private boolean validate() {
		statusMessages.clear();
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		String mensaje = "";
		String idCompomente = "";

		if (sumarioCab.getNroExp() == null) {
			mensaje = "Nro. Expediente SFP es un campo requerido. Verifique.";
			idCompomente = "form:nroDecorate:nro";
		} else if (sumarioCab.getAnioExp() == null) {
			mensaje = "Año es un campo requerido. Verifique.";
			idCompomente = "form:anhoDecorate:anho";
		} else if (Utiles.vacio(cedula)) {
			mensaje = "C.I.Nº es un campo requerido. Verifique.";
			idCompomente = "form:docDecorate:doc";
		}

		// Controlar detalle de faltas
		else if (listaFaltas == null || listaFaltas.size() == 0) {
			mensaje = "Debe seleccionar por lo menos una Falta antes de guardar. Verifique.";
		} else {
			boolean seleccionado = false;
			for (DatosEspecificos datosEspecificos : listaFaltas) {
				if (datosEspecificos.getSeleccionado()) {
					seleccionado = true;
					break;
				}
			}

			if (!seleccionado)
				mensaje = "Debe seleccionar por lo menos una Falta antes de guardar. Verifique.";
		}

		if (Utiles.vacio(mensaje) && !isNew()) {
			// Solo para la Edicion
			if (EstadoSumario.EN_CURSO.getValor().equals(sumarioCab.getEstado())) {
				// Validar el panel Juez Sumariante - Primer update
				if (estadoJuez == null) {
					mensaje = "Recomendación del Juez Sumariante es un campo requerido. Verifique.";
					idCompomente = "form:estadoJuezField:estadoJuez";
				} else if (EstadoSumario.SANCION.equals(estadoJuez) && idSancionJuez == null) {
					mensaje = "Debe especificar una sanción dictada por el Juez. Verifique.";
					idCompomente = "form:especificarJuez:idEspecificarJuez";
				} else if (inhabilitaFuncionarioJuez != null && inhabilitaFuncionarioJuez
					&& sumarioCab.getTiempoJ() == null) {
					mensaje = "Tiempo de Inhabilitación es un campo requerido. Verifique.";
					idCompomente = "form:tiempoJuezDecorate:tiempoJuez";
				}
			} else if (EstadoSumario.CON_RESOLUCION_JUEZ.getValor().equals(sumarioCab.getEstado())) {
				// Validar el panel Decision de la Mai - Segundo update
				if (estadoMai == null) {
					mensaje = "Decisión de la Mai es un campo requerido. Verifique.";
					idCompomente = "form:estadoMaiField:estadoMai";
				} else if (EstadoSumario.SANCION.equals(estadoMai) && idSancionMai == null) {
					mensaje = "Debe especificar una sanción dictada por la Mai. Verifique.";
					idCompomente = "form:especificarMaiDecorate:especificarMai";
				} else if (inhabilitaFuncionarioMai != null && inhabilitaFuncionarioMai
					&& sumarioCab.getTiempoM() == null) {
					mensaje = "Tiempo de Inhabilitación es un campo requerido. Verifique.";
					idCompomente = "form:tiempoMaiDecorate:tiempoMai";
				} else if (EstadoSumario.SANCION.equals(estadoMai)
					&& sumarioCab.getFechaDesde() == null) {
					mensaje = "Debe Ingresar la Fecha de Inicio. Verifique.";
					idCompomente = "form:fechaInicioField:fecha";
				}
			}
		}

		if (!Utiles.vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.add(Severity.ERROR, mensaje);
			// FACES ERROR
			message.setDetail(mensaje);
			context.addMessage(idCompomente, message);
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private DatosEspecificos getMotivoDesvinculacion() {
		try {
			String sql =
				" SELECT DE.* " + " FROM SELECCION.DATOS_ESPECIFICOS DE "
					+ " JOIN SELECCION.DATOS_GENERALES DG "
					+ " ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
					+ " WHERE DG.DESCRIPCION = 'MOTIVOS DE DESVINCULACION' "
					+ " AND DE.ACTIVO = TRUE  " + " AND DE.DESCRIPCION = 'DESTITUCION POR SUMARIO'";

			List<DatosEspecificos> lista =
				em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
			return lista.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean mostrarPanelJuezSumariante() {
		if (EstadoSumario.EN_CURSO.getValor().equals(sumarioCab.getEstado())
			|| mostrarPanelDecisionMai())
			return true;
		return false;
	}

	public boolean mostrarPanelDecisionMai() {
		if (sumarioCab == null || Utiles.vacio(sumarioCab.getEstado()))
			return false;

		if (EstadoSumario.CON_RESOLUCION_JUEZ.getValor().equals(sumarioCab.getEstado()))
			return true;
		return false;
	}

	public boolean mostrarPanelJuezSumarianteView() {
		if (sumarioCab == null || Utiles.vacio(sumarioCab.getEstado()))
			return false;

		if (EstadoSumario.CON_RESOLUCION_JUEZ.getValor().equals(sumarioCab.getEstado()))
			return true;
		return false;
	}

	public boolean mostrarPanelDecisionMaiView() {
		if (sumarioCab == null || Utiles.vacio(sumarioCab.getEstado()))
			return false;

		if (EstadoSumario.SOBRESEIMIENTO.getValor().equals(sumarioCab.getEstado())
			|| EstadoSumario.ARCHIVO.getValor().equals(sumarioCab.getEstado())
			|| EstadoSumario.SANCION.getValor().equals(sumarioCab.getEstado()))
			return true;
		return false;
	}

	public String getDescripcionEstado(String valor) {
		EstadoSumario estadoSumario = EstadoSumario.getEstadoPorValor(valor);
		if (estadoSumario != null)
			return estadoSumario.getDescripcion();

		return null;
	}

	public boolean esEditable() {
		if (estadoSumario != null
			&& (EstadoSumario.EN_CURSO.getId().equals(estadoSumario.getId()) || EstadoSumario.CON_RESOLUCION_JUEZ.getId().equals(estadoSumario.getId()))) {
			return true;
		}
		return false;
	}

	public boolean esEditable(String valor) {
		if (valor != null
			&& (EstadoSumario.EN_CURSO.getValor().equals(valor) || EstadoSumario.CON_RESOLUCION_JUEZ.getValor().equals(valor))) {
			return true;
		}
		return false;
	}

	public void selectAll() {
		if (seleccionarTodos != null) {
			for (DatosEspecificos datosEspecificos : listaFaltas) {
				if (seleccionarTodos)
					datosEspecificos.setSeleccionado(true);
				else
					datosEspecificos.setSeleccionado(false);
			}

		}
	}

	private void cargarDetalleJuez(SumarioCab sumarioCab) {
		estadoJuez = EstadoJuez.getEstadoPorValor(sumarioCab.getEstadoJuez());

		if (sumarioCab.getSancionJuez() != null) {
			idSancionJuez = sumarioCab.getSancionJuez().getIdSancion();
			updateInhabilitaFuncionarioJuez();
		}
	}

	private void cargarDetalleMai(SumarioCab sumarioCab) {
		estadoMai = EstadoSumario.getEstadoPorValor(sumarioCab.getEstado());
		if (EstadoSumario.CON_RESOLUCION_JUEZ.getId().equals(estadoMai.getId()))
			estadoMai = null;

		if (sumarioCab.getSancionMai() != null) {
			idSancionMai = sumarioCab.getSancionMai().getIdSancion();
			updateInhabilitaFuncionarioMai();
		}
	}

	public EstadoSumario[] getEstadoSumarioPanel() {
		EstadoSumario[] estados = new EstadoSumario[3];
		estados[0] = EstadoSumario.SOBRESEIMIENTO;
		estados[1] = EstadoSumario.ARCHIVO;
		estados[2] = EstadoSumario.SANCION;
		return estados;
	}

	public EstadoJuez[] getEstadoSumarioJuezPanel() {
		EstadoJuez[] estados = new EstadoJuez[3];
		estados[0] = EstadoJuez.SOBRESEIMIENTO;
		estados[1] = EstadoJuez.ARCHIVO;
		estados[2] = EstadoJuez.SANCION;
		return estados;
	}

	public boolean mostrarDatosSancionJuez() {
		if (estadoJuez != null && EstadoSumario.SANCION.getId().equals(estadoJuez.getId()))
			return true;

		return false;
	}

	public boolean mostrarDatosSancionMai() {
		if (estadoMai != null && EstadoSumario.SANCION.getId().equals(estadoMai.getId()))
			return true;

		return false;
	}

	public void updateInhabilitaFuncionarioJuez() {
		if (idSancionJuez != null) {
			Sancion sancion = em.find(Sancion.class, idSancionJuez);
			inhabilitaFuncionarioJuez = sancion.getInhabilita();
		}
	}

	public void updateInhabilitaFuncionarioMai() {
		if (idSancionMai != null) {
			Sancion sancion = em.find(Sancion.class, idSancionMai);
			inhabilitaFuncionarioMai = sancion.getInhabilita();
		}
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	// public String getESTADO_PENDIENTE_APROBACION() {
	// return ESTADO_PENDIENTE_APROBACION;
	// }
	//
	//
	//
	// public String getESTADO_A_SOLICITAR() {
	// return ESTADO_A_SOLICITAR;
	// }

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		if (from == null)
			return "/";

		return from;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getCedula() {
		return cedula;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getApellido() {
		return apellido;
	}

	public void setListaSumarioCab(List<SumarioCab> listaSumarioCab) {
		this.listaSumarioCab = listaSumarioCab;
	}

	public List<SumarioCab> getListaSumarioCab() {
		return listaSumarioCab;
	}

	public void setEstadoSumario(EstadoSumario estadoSumario) {
		this.estadoSumario = estadoSumario;
	}

	public EstadoSumario getEstadoSumario() {
		return estadoSumario;
	}

	public void setIdSumarioCab(Long idSumarioCab) {
		this.idSumarioCab = idSumarioCab;
	}

	public Long getIdSumarioCab() {
		return idSumarioCab;
	}

	public void setSumarioCab(SumarioCab sumarioCab) {
		this.sumarioCab = sumarioCab;
	}

	public SumarioCab getSumarioCab() {
		return sumarioCab;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setSeleccionarTodos(Boolean seleccionarTodos) {
		this.seleccionarTodos = seleccionarTodos;
	}

	public Boolean getSeleccionarTodos() {
		return seleccionarTodos;
	}

	public void setListaFaltas(List<DatosEspecificos> listaFaltas) {
		this.listaFaltas = listaFaltas;
	}

	public List<DatosEspecificos> getListaFaltas() {
		return listaFaltas;
	}

	public void setNuevoCreado(Boolean nuevoCreado) {
		this.nuevoCreado = nuevoCreado;
	}

	public Boolean getNuevoCreado() {
		return nuevoCreado;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public EstadoJuez getEstadoJuez() {
		return estadoJuez;
	}

	public void setEstadoJuez(EstadoJuez estadoJuez) {
		this.estadoJuez = estadoJuez;
	}

	public void setEstadoMai(EstadoSumario estadoMai) {
		this.estadoMai = estadoMai;
	}

	public EstadoSumario getEstadoMai() {
		return estadoMai;
	}

	public void setIdSancionJuez(Long idSancionJuez) {
		this.idSancionJuez = idSancionJuez;
	}

	public Long getIdSancionJuez() {
		return idSancionJuez;
	}

	public void setIdSancionMai(Long idSancionMai) {
		this.idSancionMai = idSancionMai;
	}

	public Long getIdSancionMai() {
		return idSancionMai;
	}

	public void setInhabilitaFuncionarioJuez(Boolean inhabilitaFuncionarioJuez) {
		this.inhabilitaFuncionarioJuez = inhabilitaFuncionarioJuez;
	}

	public Boolean getInhabilitaFuncionarioJuez() {
		return inhabilitaFuncionarioJuez;
	}

	public void setInhabilitaFuncionarioMai(Boolean inhabilitaFuncionarioMai) {
		this.inhabilitaFuncionarioMai = inhabilitaFuncionarioMai;
	}

	public Boolean getInhabilitaFuncionarioMai() {
		return inhabilitaFuncionarioMai;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public boolean isHabOee() {
		return habOee;
	}

	public void setHabOee(boolean habOee) {
		this.habOee = habOee;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Integer getExpInterno() {
		return expInterno;
	}

	public void setExpInterno(Integer expInterno) {
		this.expInterno = expInterno;
	}

	public Integer getAnioExpInterno() {
		return anioExpInterno;
	}

	public void setAnioExpInterno(Integer anioExpInterno) {
		this.anioExpInterno = anioExpInterno;
	}

	public Integer getExpSfp() {
		return expSfp;
	}

	public void setExpSfp(Integer expSfp) {
		this.expSfp = expSfp;
	}

	public Integer getAnioExpSfp() {
		return anioExpSfp;
	}

	public void setAnioExpSfp(Integer anioExpSfp) {
		this.anioExpSfp = anioExpSfp;
	}

	public String getDescripcionPaisExp() {
		return descripcionPaisExp;
	}

	public void setDescripcionPaisExp(String descripcionPaisExp) {
		this.descripcionPaisExp = descripcionPaisExp;
	}

}
