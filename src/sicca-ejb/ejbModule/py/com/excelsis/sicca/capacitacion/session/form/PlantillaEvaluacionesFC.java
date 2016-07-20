package py.com.excelsis.sicca.capacitacion.session.form;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ConsultasCapacitacion;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlantillaEncuesta;
import py.com.excelsis.sicca.entity.Preguntas;
import py.com.excelsis.sicca.entity.RespuestaMultiple;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("plantillaEvaluacionesFC")
public class PlantillaEvaluacionesFC {

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
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Long idPlantilla;
	private Long idGrupoPregunta;
	private Long orden;
	private Long idTipoPregunta;
	private Boolean mostrarMultipleOpcion = false;
	private Boolean esObligatorio = true;
	private Boolean isEdit = false;
	private String pregunta;
	private String multiplesOpc;
	private Integer index;
	private PlantillaEncuesta plantillaEncuesta = new PlantillaEncuesta();
	private List<SelectItem> grupoPreguntaSelectItem = new ArrayList<SelectItem>();
	private List<SelectItem> tipoPreguntaSelectItem = new ArrayList<SelectItem>();
	private List<Preguntas> listadoPreguntas = new ArrayList<Preguntas>();
	private List<Preguntas> listadoPreguntasEliminar = new ArrayList<Preguntas>();

	/**
	 * Paso 1: Inicializa los parámetros
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idPlantilla != null) {
			if (!sufc.validarInput(idPlantilla.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		plantillaEncuesta = em.find(PlantillaEncuesta.class, idPlantilla);
		Long idOee = plantillaEncuesta
		.getConfiguracionUoCab().getIdConfiguracionUo();
		seguridadUtilFormController.validarPerteneceOEE(idOee);
		cargarDatosNivel();
		comboGrupoPregunta();
		comboTipoPregunta();
		obtenerListaPreguntas();
	}

	public void initEdit() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idPlantilla != null) {
			if (!sufc.validarInput(idPlantilla.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		plantillaEncuesta = em.find(PlantillaEncuesta.class, idPlantilla);

		cargarDatosNivel();
		comboGrupoPregunta();
		comboTipoPregunta();
		obtenerListaPreguntas();
	}

	/**
	 * Carga datos para el paso 1
	 */
	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			
			if (usuarioLogueado.getConfiguracionUoDet() != null){
				Long idUoDet = usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet();
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(idUoDet);
			}
		}
		cargarCabecera();
	}

	/**
	 * Carga datos para el paso 1
	 */
	public void cargarCabecera() {
		Long idOee = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		ConfiguracionUoCab oee = new ConfiguracionUoCab();
		oee = em.find(ConfiguracionUoCab.class, idOee);
		Long idEntidad = oee.getEntidad().getIdEntidad();
		Entidad ent = new Entidad();
		ent = em.find(Entidad.class, idEntidad);
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(ent.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(ent.getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		nivelEntidadOeeUtil.init();

	}

	/**
	 * Carga Combo Grupo pregunta, parte del paso 1
	 */
	private void comboGrupoPregunta() {
		String sql = "SELECT DATOS_ESPECIFICOS.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES "
				+ "ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE DATOS_GENERALES.DESCRIPCION = 'GRUPOS DE PREGUNTAS CAPACITACION' "
				+ "AND DATOS_ESPECIFICOS.ACTIVO = TRUE "
				+ "ORDER BY DATOS_ESPECIFICOS.DESCRIPCION";
		List<DatosEspecificos> lista = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		grupoPreguntaSelectItem = new ArrayList<SelectItem>();
		grupoPreguntaSelectItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos l : lista) {
			grupoPreguntaSelectItem.add(new SelectItem(l
					.getIdDatosEspecificos(), l.getDescripcion()));
		}
	}

	/**
	 * Paso 2: Obtiene el orden sugerido de acuerdo al Grupo de pregunta
	 * escogido
	 */
	public void obtenerOrden() {
		String sql = "SELECT COUNT(P.*) " + "FROM CAPACITACION.PREGUNTAS P "
				+ "WHERE P.ACTIVO IS TRUE AND P.ID_DATOS_ESPECIFICOS_GRUPO = "
				+ idGrupoPregunta + " AND P.ID_PLANTILLA = "
				+ plantillaEncuesta.getIdPlantilla();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null || config.toString().equals("0"))
			orden = new Long(1);
		else {
			Integer val = new Integer(config.toString());
			val++;
			orden = new Long(val.toString());
		}
	}

	/**
	 * Carga Combo Tipo pregunta, parte del paso 1
	 */
	public void comboTipoPregunta() {
		tipoPreguntaSelectItem = new ArrayList<SelectItem>();
		tipoPreguntaSelectItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		tipoPreguntaSelectItem.add(new SelectItem(1, "Múltiples Opciones"));
		tipoPreguntaSelectItem.add(new SelectItem(2, "Texto Libre"));
	}

	/**
	 * Obtiene la lista de preguntas activas en la bd correspondientes a la
	 * plantilla recibida como parámetro, parte del paso 1
	 */
	private void obtenerListaPreguntas() {
		String sql = "SELECT P.* FROM CAPACITACION.PREGUNTAS P "
				+ "JOIN seleccion.datos_especificos D "
				+ "ON D.id_datos_especificos = P.id_datos_especificos_grupo "
				+ "WHERE P.ID_PLANTILLA = " + idPlantilla
				+ " AND P.ACTIVO IS TRUE ORDER BY D.DESCRIPCION, P.ORDEN";
		listadoPreguntas = em.createNativeQuery(sql, Preguntas.class)
				.getResultList();
		for (int i = 0; i < listadoPreguntas.size(); i++) {
			Preguntas pre = new Preguntas();
			pre = listadoPreguntas.get(i);
			pre.setRespuestasMultiples(obtenerRespuesta(pre));
		}
	}

	/**
	 * Paso 3: En caso de que el tipo pregunta es 'Múltiples opciones' habilita
	 * el panel Múltiples opciones
	 */
	public void chequearMultiplesOpciones() {
		if (idTipoPregunta == 1)
			mostrarMultipleOpcion = true;
		else
			mostrarMultipleOpcion = false;
	}

	/**
	 * Obtiene las respuestas por cada pregunta recibida como parámetro, en caso
	 * de que sea tipo 'Múltiples opciones'
	 * 
	 * @param p
	 * @return
	 */
	private String obtenerRespuesta(Preguntas p) {

		String sql = "select capacitacion.fnc_resultado_opcion_multiple_plantilla("
				+ p.getIdPregunta()
				+ ") "
				+ "from capacitacion.preguntas "
				+ "where preguntas.id_pregunta = " + p.getIdPregunta();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null)
			return null;
		else
			return config.toString();
	}

	/**
	 * Paso 4: Prepara los datos para ser editados, llamado desde el link
	 * 'Editar'
	 * 
	 * @param r
	 */
	public void editar(Integer r) {
		index = r;
		Preguntas preg = new Preguntas();
		preg = listadoPreguntas.get(index);
		idGrupoPregunta = preg.getDatosEspecificosGrupo()
				.getIdDatosEspecificos();
		orden = preg.getOrden();
		pregunta = preg.getPregunta();
		multiplesOpc = preg.getRespuestasMultiples();
		if (multiplesOpc != null && !multiplesOpc.trim().isEmpty()) {
			idTipoPregunta = new Long(1);
			mostrarMultipleOpcion = true;
		} else {
			mostrarMultipleOpcion = false;
			idTipoPregunta = new Long(2);
		}
		isEdit = true;
	}

	/**
	 * Paso 5: elimina el registro seleccionado, llamado desde el link
	 * 'Eliminar'
	 * 
	 * @param r
	 */
	public void eliminar(Integer r) {
		index = r;
		Preguntas preg = new Preguntas();
		preg = listadoPreguntas.get(index);
		listadoPreguntas.remove(preg);
		if (preg.getIdPregunta() != null)
			listadoPreguntasEliminar.add(preg);

	}

	/**
	 * Limpia los campos del panel 'Agregar Pregunta' Llamado desde el botón
	 * limpiar o desde el método cancelarEdit()
	 */
	public void limpiarCamposPregunta() {
		idGrupoPregunta = null;
		orden = null;
		pregunta = null;
		idTipoPregunta = null;
		esObligatorio = true;
		mostrarMultipleOpcion = false;
		multiplesOpc = null;
	}

	/**
	 * Paso 6: cancela la edición del registro actual
	 */
	public void cancelarEdit() {
		limpiarCamposPregunta();
		isEdit = false;
		index = null;
	}

	/**
	 * Paso 7: chequea que esten completos los campos obligatorios para
	 * 'Agregar' y 'Actualizar' las Preguntas
	 * 
	 * @return
	 */
	private Boolean chequearCamposCompletos() {
		statusMessages.clear();
		if (idGrupoPregunta == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Grupo de Pregunta antes de realizar esta acción");
			return false;
		}
		if (orden == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Orden antes de realizar esta acción");
			return false;
		}
		if (orden.intValue() < 0) {
			statusMessages.add(Severity.WARN,
					"El campo Orden no puede ser menor a cero");
			return false;
		}
		if (ordenRepetido()) {
			statusMessages.add(Severity.WARN, "El Orden ingresado ya existe");
			return false;
		}
		if (pregunta == null || pregunta.trim().isEmpty()) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Pregunta antes de realizar esta acción");
			return false;
		}
		if (idTipoPregunta == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Tipo Pregunta antes de realizar esta acción");
			return false;
		}
		if (idTipoPregunta.intValue() == 1) {
			if (multiplesOpc == null || multiplesOpc.trim().isEmpty()) {
				statusMessages
						.add(Severity.WARN,
								"Debe completar el campo Opciones de Respuesta antes de realizar esta acción");
				return false;
			}
		}
		return true;
	}

	private Boolean ordenRepetido() {
		String sql = "SELECT P.* " + "FROM CAPACITACION.PREGUNTAS P "
				+ "WHERE P.ACTIVO IS TRUE AND P.ID_DATOS_ESPECIFICOS_GRUPO = "
				+ idGrupoPregunta + " AND P.ORDEN = " + orden
				+ " AND P.ID_PLANTILLA = " + plantillaEncuesta.getIdPlantilla();
		if (index != null) {
			Preguntas pg = new Preguntas();
			pg = listadoPreguntas.get(index);
			sql += " AND P.ID_PREGUNTA != " + pg.getIdPregunta();
		}

		List<Preguntas> listaAx = em.createNativeQuery(sql, Preguntas.class)
				.getResultList();
		if (listaAx == null || listaAx.size() == 0)
			return false;
		return true;
	}

	private Boolean checkPreguntaExiste(String accion) {
		String sql = "SELECT P.* " + "FROM CAPACITACION.PREGUNTAS P "
				+ "WHERE P.ID_PLANTILLA = "
				+ plantillaEncuesta.getIdPlantilla() + " AND P.ACTIVO IS TRUE "
				+ "AND P.PREGUNTA = '" + pregunta.trim() + "'";
		if (accion.equals("update"))
			sql += " AND P.ID_PREGUNTA != "
					+ listadoPreguntas.get(index).getIdPregunta();
		List<Preguntas> listaAx = em.createNativeQuery(sql, Preguntas.class)
				.getResultList();
		if (listaAx == null || listaAx.size() == 0)
			return false;
		statusMessages.add(Severity.ERROR,
				"Ya existe la pregunta especificada. Verifique");
		return true;
	}

	/**
	 * Paso 8: Agregar el registro a grilla de Preguntas
	 */
	public void agregarPregunta() {
		if (!chequearCamposCompletos())
			return;
		if (checkPreguntaExiste("new"))
			return;
		if (checkExisteRpta(multiplesOpc))
			return;
		Preguntas preg = new Preguntas();
		preg.setActivo(true);
		preg.setDatosEspecificosGrupo(em.find(DatosEspecificos.class,
				idGrupoPregunta));
		preg.setFechaAlta(new Date());
		preg.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		preg.setObligatorio(esObligatorio);
		preg.setOrden(orden);
		preg.setPlantillaEncuesta(plantillaEncuesta);
		preg.setPregunta(pregunta.trim());
		if (idTipoPregunta.intValue() == 1) {
			preg.setTipoPregunta("M");
			preg.setRespuestasMultiples(obtenerMultipleOpc(multiplesOpc));
		} else
			preg.setTipoPregunta("T");
		listadoPreguntas.add(preg);
		limpiarCamposPregunta();

	}

	private Boolean checkExisteRpta(String valor) {
		if (valor != null && !valor.trim().isEmpty()) {
			String[] arrayRpta = valor.split(";");
			String[] arrayAux = valor.split(";");

			for (int i = 0; i < arrayRpta.length; i++) {
				String cadena = arrayRpta[i].trim();
				if (!cadena.isEmpty()) {
					for (int j = 0; j < arrayAux.length; j++) {
						String cad = arrayAux[j].trim();
						if (!cad.isEmpty() && j != i) {
							if (cad.equals(cadena)) {
								statusMessages
										.add(Severity.ERROR,
												"Existen respuestas múltiples repetidas. Verifique");
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private String obtenerMultipleOpc(String valor) {
		String[] arrayRpta = valor.split(";");
		String resultado = "";
		for (int i = 0; i < arrayRpta.length; i++) {
			String cadena = arrayRpta[i].trim();
			if (!cadena.isEmpty()) {
				cadena = cadena.substring(0, 1).toUpperCase()
						+ cadena.substring(1, cadena.length()).toLowerCase();
				resultado = resultado + cadena + "; ";
			}
		}
		return resultado.substring(0, resultado.trim().length() - 1);
	}

	/**
	 * Paso 9: Actualiza el registro seleccionado en la grilla de preguntas
	 */
	public void actualizarPregunta() {
		if (!chequearCamposCompletos())
			return;
		if (checkPreguntaExiste("update"))
			return;
		if (checkExisteRpta(multiplesOpc))
			return;

		Preguntas preg = new Preguntas();
		preg = listadoPreguntas.get(index);
		preg.setDatosEspecificosGrupo(em.find(DatosEspecificos.class,
				idGrupoPregunta));
		preg.setFechaAlta(new Date());
		preg.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		preg.setObligatorio(esObligatorio);
		preg.setOrden(orden);
		preg.setPlantillaEncuesta(plantillaEncuesta);
		preg.setPregunta(pregunta);
		if (idTipoPregunta.intValue() == 1) {
			preg.setTipoPregunta("M");
			preg.setRespuestasMultiples(obtenerMultipleOpc(multiplesOpc));
		} else
			preg.setTipoPregunta("T");
		listadoPreguntas.set(index, preg);
		cancelarEdit();
	}

	public String guardar() {
		// Elimina logicamente las preguntas y sus correspondientes respuestas
		// múltiples
		try {
			for (Preguntas pr : listadoPreguntasEliminar) {
				List<RespuestaMultiple> listaRptaMult = new ArrayList<RespuestaMultiple>();
				listaRptaMult = pr.getRespuestaMultiples();
				pr.setActivo(false);
				for (RespuestaMultiple rpta : listaRptaMult) {
					if (rpta.isActivo()) {
						rpta.setActivo(false);
						em.merge(rpta);
					}
				}
				em.merge(pr);
			}
		} catch (Exception e) {
			return null;
		}
		try {
			for (Preguntas pr : listadoPreguntas) {
				if (pr.getIdPregunta() == null) {
					em.persist(pr);
					if (!guardarRptaMultiple(pr))

						return null;
				} else {
					pr.setFechaMod(new Date());
					pr.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pr);
					actualizarRptaMultiple(pr);
				}
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Una de las preguntas ingresadas ya existe, verifique.");
			return null;
		}

	}

	private Boolean guardarRptaMultiple(Preguntas p) {
		String rpta = p.getRespuestasMultiples();
		if (rpta == null || rpta.trim().isEmpty())
			return true;
		String[] arrayRpta = rpta.split(";");
		try {
			for (int i = 0; i < arrayRpta.length; i++) {
				RespuestaMultiple rptaMult = new RespuestaMultiple();
				rptaMult.setActivo(true);
				rptaMult.setFechaAlta(new Date());
				rptaMult.setPreguntas(p);
				rptaMult.setRespuestaOpc(arrayRpta[i].trim());
				rptaMult.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(rptaMult);

			}
		} catch (Exception e) {
			statusMessages
					.add(Severity.ERROR,
							"Las opciones de respuesta no pueden repetirse, verifique.");
			return false;
		}
		return true;
	}

	private Boolean actualizarRptaMultiple(Preguntas p) {
		String rpta = p.getRespuestasMultiples();
		List<RespuestaMultiple> lista = new ArrayList<RespuestaMultiple>();

		lista = obtenerRptaMultiple(p.getIdPregunta());
		if (rpta == null || rpta.trim().isEmpty()) {
			for (RespuestaMultiple rm : lista) {
				rm.setActivo(false);
				rm.setFechaMod(new Date());
				rm.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(rm);
			}
		} else {
			String[] arrayRpta = rpta.split(";");
			try {
				for (RespuestaMultiple rm : lista) {
					Boolean esta = false;
					for (int i = 0; i < arrayRpta.length; i++) {
						String cad = arrayRpta[i].trim();
						if (rm.getRespuestaOpc().equals(cad))
							esta = true;
					}
					if (!esta) {
						rm.setActivo(false);
						rm.setFechaMod(new Date());
						rm.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(rm);
					}
				}

				for (int i = 0; i < arrayRpta.length; i++) {
					String cad = arrayRpta[i].trim();
					Boolean esta = false;
					for (RespuestaMultiple rm : lista) {
						if (rm.getRespuestaOpc().equals(cad))
							esta = true;
					}
					if (!esta) {
						RespuestaMultiple mult = new RespuestaMultiple();
						mult.setActivo(true);
						mult.setFechaAlta(new Date());
						mult.setPreguntas(p);
						mult.setRespuestaOpc(cad);
						mult.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(mult);
					}
				}

			} catch (Exception e) {
				statusMessages
						.add(Severity.ERROR,
								"Las opciones de respuesta no pueden repetirse, verifique.");
				return false;
			}
		}
		return true;
	}

	private List<RespuestaMultiple> obtenerRptaMultiple(Long id) {
		String sql = "select rm.* "
				+ "from capacitacion.respuesta_multiple rm "
				+ "where rm.activo is true " + "and rm.id_pregunta = " + id;
		return em.createNativeQuery(sql, RespuestaMultiple.class)
				.getResultList();
	}

	private String obtenerSql() {
		String sql = "SELECT DE.DESCRIPCION as grupo, DE.ID_DATOS_ESPECIFICOS AS ID_GRUPO, "
				+ "PREG.ORDEN as orden, PREG.PREGUNTA as pregunta, PREG.id_pregunta as id_pregunta, "
				+ "PREG.tipo_pregunta as tipo_pregunta "
				+ "FROM CAPACITACION.PREGUNTAS PREG "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE ON DE.ID_DATOS_ESPECIFICOS = PREG.ID_DATOS_ESPECIFICOS_GRUPO "
				+ "LEFT JOIN CAPACITACION.RESPUESTA_MULTIPLE RPTA ON RPTA.ID_PREGUNTA = PREG.ID_PREGUNTA "
				+ "JOIN CAPACITACION.PLANTILLA_ENCUESTA PLANT ON PLANT.ID_PLANTILLA = PREG.ID_PLANTILLA "
				+ "WHERE PREG.ACTIVO = TRUE AND PLANT.ID_PLANTILLA  = "
				+ idPlantilla + " ORDER BY DE.DESCRIPCION, PREG.ORDEN";
		return sql;
	}

	public void pdf() throws Exception {
		Connection conn = JpaResourceBean.getConnection();
		try {

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			if (idPlantilla != null)
				param.put("sql", obtenerSql());
			param.put("oee", usuarioLogueado.getConfiguracionUoCab()
					.getDenominacionUnidad());

			JasperReportUtils.respondPDF("RPT_CU491_Plantilla_Evaluacion",
					param, false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}

	public Long getIdPlantilla() {
		return idPlantilla;
	}

	public void setIdPlantilla(Long idPlantilla) {
		this.idPlantilla = idPlantilla;
	}

	public PlantillaEncuesta getPlantillaEncuesta() {
		return plantillaEncuesta;
	}

	public void setPlantillaEncuesta(PlantillaEncuesta plantillaEncuesta) {
		this.plantillaEncuesta = plantillaEncuesta;
	}

	public List<SelectItem> getGrupoPreguntaSelectItem() {
		return grupoPreguntaSelectItem;
	}

	public void setGrupoPreguntaSelectItem(
			List<SelectItem> grupoPreguntaSelectItem) {
		this.grupoPreguntaSelectItem = grupoPreguntaSelectItem;
	}

	public Long getIdGrupoPregunta() {
		return idGrupoPregunta;
	}

	public void setIdGrupoPregunta(Long idGrupoPregunta) {
		this.idGrupoPregunta = idGrupoPregunta;
	}

	public Long getOrden() {
		return orden;
	}

	public void setOrden(Long orden) {
		this.orden = orden;
	}

	public Boolean getMostrarMultipleOpcion() {
		return mostrarMultipleOpcion;
	}

	public void setMostrarMultipleOpcion(Boolean mostrarMultipleOpcion) {
		this.mostrarMultipleOpcion = mostrarMultipleOpcion;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public Long getIdTipoPregunta() {
		return idTipoPregunta;
	}

	public void setIdTipoPregunta(Long idTipoPregunta) {
		this.idTipoPregunta = idTipoPregunta;
	}

	public List<SelectItem> getTipoPreguntaSelectItem() {
		return tipoPreguntaSelectItem;
	}

	public void setTipoPreguntaSelectItem(
			List<SelectItem> tipoPreguntaSelectItem) {
		this.tipoPreguntaSelectItem = tipoPreguntaSelectItem;
	}

	public Boolean getEsObligatorio() {
		return esObligatorio;
	}

	public void setEsObligatorio(Boolean esObligatorio) {
		this.esObligatorio = esObligatorio;
	}

	public String getMultiplesOpc() {
		return multiplesOpc;
	}

	public void setMultiplesOpc(String multiplesOpc) {
		this.multiplesOpc = multiplesOpc;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public List<Preguntas> getListadoPreguntas() {
		return listadoPreguntas;
	}

	public void setListadoPreguntas(List<Preguntas> listadoPreguntas) {
		this.listadoPreguntas = listadoPreguntas;
	}

	public List<Preguntas> getListadoPreguntasEliminar() {
		return listadoPreguntasEliminar;
	}

	public void setListadoPreguntasEliminar(
			List<Preguntas> listadoPreguntasEliminar) {
		this.listadoPreguntasEliminar = listadoPreguntasEliminar;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
