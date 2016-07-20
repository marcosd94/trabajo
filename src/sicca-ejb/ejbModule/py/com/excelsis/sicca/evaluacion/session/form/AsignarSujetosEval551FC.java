package py.com.excelsis.sicca.evaluacion.session.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hsqldb.lib.HashSet;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("asignarSujetosEval551FC")
public class AsignarSujetosEval551FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	private Date fechaDesde;
	private Date fechaHasta;
	private String idTipoNombramiento;
	private Long idPaisExpede;
	private String objGasto;
	private String descPuesto;
	private String nroDocIden;
	private List<ConfiguracionUoDet> lGrilla0;
	private List<EmpleadoPuesto> lGrilla1;
	private List<Sujetos> lGrilla2Eliminados;
	private List<Sujetos> lGrilla2;
	private Set<String> conjuntoAntiReptido;
	private Long idEvaluacionDesempeno;
	private EvaluacionDesempeno evaluacionDesempeno;
	private String codUoDet;
	private String descUoDet;
	private String evaluacion;
	private String elFrom = "/home.seam";
	private Boolean selAllGrilla0 = false;
	private Boolean selAllGrilla1 = false;
	private String selAllGrilla1Str = "";
	private Long idDEContratado;
	private Long idDEPermanente;
	private Long idDEComisionado;
	private Integer idRefEstadoCarga;
	private String accionCU = "VER";// formato VER ó DEL
	
	private String desdeBandeja;
	
	public void init() {
		initIDs();
		cargarCabecera();
		if (!precondInit()) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("seam_authorization_exception"));
		}
		fechaDesde = evaluacionDesempeno.getFechaEvalDesde();
		fechaHasta = evaluacionDesempeno.getFechaEvalHasta();
		searchGrilla0(false);
		if (desdeBandeja.contentEquals("desdeBandeja")) {
			cargarGrilla2();
			desdeBandeja = "";
		}
		
	}

	private Boolean precondInit() {
		if (idEvaluacionDesempeno != null) {//se deshabilita condición por UO; Werner.
//			if (usuarioLogueado.getConfiguracionUoDet()
//					.getIdConfiguracionUoDet().longValue() == evaluacionDesempeno
//					.getConfiguracionUoDet().getIdConfiguracionUoDet()
//					.longValue()) {
				if (evaluacionDesempeno.getEstado().intValue() == idRefEstadoCarga
						.intValue()) {
					return true;
				}
//			}
		}
		return false;
	}

	private Boolean precondInit2() {
		if (idEvaluacionDesempeno != null) {//se deshabilita condición por UO; Werner.
//			if (usuarioLogueado.getConfiguracionUoDet()
//					.getIdConfiguracionUoDet().longValue() == evaluacionDesempeno
//					.getConfiguracionUoDet().getIdConfiguracionUoDet()
//					.longValue()) {
				return true;

//			}
		}
		return false;
	}

	public void init2() {
		initIDs();
		cargarCabecera2();
		if (!precondInit2()) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("seam_authorization_exception"));
		}
		searchGrilla2();
	}

	private void initIDs() {
		idDEContratado = seleccionUtilFormController.traerDatosEspecificos(
				"ESTADO EMPLEADO PUESTO", "CONTRATADO").getIdDatosEspecificos();
		idDEContratado = seleccionUtilFormController.traerDatosEspecificos(
				"ESTADO EMPLEADO PUESTO", "PERMANENTE").getIdDatosEspecificos();
		idDEContratado = seleccionUtilFormController.traerDatosEspecificos(
				"ESTADO EMPLEADO PUESTO", "COMISIONADO")
				.getIdDatosEspecificos();
		idRefEstadoCarga = seleccionUtilFormController.getIdEstadosReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "CARGA");
	}

	public void todos() {
		idPaisExpede = null;
		nroDocIden = null;
		searchGrilla2();
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	private Boolean precondDarDeBaja(int index) {
		if (lGrilla2.size() <= index) {
			statusMessages.add(Severity.ERROR,
					"Indice superior al tamamaño de la lista");
			return false;
		}
		if (lGrilla2.get(index).getMotivoInactivacion() == null
				|| lGrilla2.get(index).getMotivoInactivacion().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe cargar un Motivo de Baja");
			return false;
		}
		if (lGrilla2.get(index).getMotivoInactivacion().length() > 250) {
			statusMessages
					.add(Severity.ERROR,
							"Superada la cantidad máxima de caracteres para el Motivo de Baja (250)");
			return false;
		}
		if (!detectarUltimoSujeto()) {
			return false;
		}
		return true;
	}

	private Boolean detectarUltimoSujeto() {
		Query q = em
				.createQuery("select count(Sujetos) from Sujetos Sujetos  where Sujetos.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDesep and Sujetos.activo is true");
		q.setParameter("idEvalDesep", idEvaluacionDesempeno);
		Long count = (Long) q.getSingleResult();
		if (count.intValue() == 1) {
			statusMessages
					.add(Severity.ERROR,
							"El sujeto no puede eliminarse ya que es el único que forma parte de la evaluación");
			return false;
		}
		return true;
	}

	public String darDeBaja(int index) {
		if (!precondDarDeBaja(index))
			return "FAIL";
		try {
			Sujetos sujeto = lGrilla2.get(index);
			sujeto.setActivo(false);
			sujeto.setFechaMod(new Date());
			sujeto.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(sujeto);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			searchGrilla2();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}

	public void refreshDefAlcance() {
		if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
				&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim().isEmpty()
				&& nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim()
						.matches("[0-9]+[\\.0-9]+")) {
			nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
			searchGrilla0(true);
		} else {
			statusMessages.add(Severity.ERROR, "Introduzca un código válido");
		}

	}

	private void cargarCabecera2() {
		try {
			if (!seguridadUtilFormController.validarInput(
					idEvaluacionDesempeno.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
				idEvaluacionDesempeno);
		evaluacion = evaluacionDesempeno.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(evaluacionDesempeno
				.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		codUoDet = nivelEntidadOeeUtil.getCodigoUnidOrgDep();
		descUoDet = nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep();
	}

	private void cargarCabecera() {
		try {
			if (!seguridadUtilFormController.validarInput(
					idEvaluacionDesempeno.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
				idEvaluacionDesempeno);
		evaluacion = evaluacionDesempeno.getTitulo();
		if (codUoDet == null) {
			nivelEntidadOeeUtil.setIdConfigCab(evaluacionDesempeno
					.getConfiguracionUoCab()
					.getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			codUoDet = nivelEntidadOeeUtil.getCodigoUnidOrgDep();
			descUoDet = nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep();
			nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
			nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
		} else {
			nivelEntidadOeeUtil.setIdConfigCab(evaluacionDesempeno
					.getConfiguracionUoCab()
					.getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
		}

	}

	private Boolean precondSave() {
		if (fechaDesde == null || fechaHasta == null) {
			statusMessages.add(Severity.ERROR,
					"Debe cargar los datos requeridos");
			return false;
		}
		if (fechaDesde.getTime() > fechaHasta.getTime()) {
			statusMessages.add(Severity.ERROR,
					"La Fecha Desde no puede ser mayor a la Fecha Hasta");
			return false;
		}
		if ((lGrilla0 == null || lGrilla2.size() == 0)
				&& (lGrilla2Eliminados == null || lGrilla2Eliminados.size() == 0)) {
			statusMessages.add(Severity.ERROR, "No hay nada que guardar");
			return false;
		}
		return true;
	}

	/**
	 * Este método solo elimina los sujetos que estan manejador por el
	 * contenedor
	 */
	private void eliminarSujetos() {
		if (lGrilla2Eliminados != null) {
			for (Sujetos o : lGrilla2Eliminados) {
				if (o.getIdSujetos() != null) {
					o.setActivo(false);
					o.setFechaMod(new Date());
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(o);
				}
			}
		}
	}

	private void saveNuevos() {
		for (Sujetos o : lGrilla2) {
			if (o.getIdSujetos() == null) {
				o.setIdSujetos(null);
				em.persist(o);
			}
		}
	}

	public String save() {
		if (!precondSave()) {
			return "FAIL";
		}
		try {
			eliminarSujetos();
			saveNuevos();
			evaluacionDesempeno.setFechaEvalDesde(fechaDesde);
			evaluacionDesempeno.setFechaEvalHasta(fechaHasta);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			limpiar();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	public String limpiar() {
		objGasto = null;
		idPaisExpede = null;
		nroDocIden = null;
		descPuesto = null;
		if (lGrilla1 != null)
			lGrilla1.clear();
		nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
		nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
		conjuntoAntiReptido.clear();
		searchGrilla0(true);
		cargarGrilla2();
		return "OK";
	}

	private void cargarGrilla2() {
		Query q = em
				.createQuery("select Sujetos from Sujetos Sujetos "
						+ " where Sujetos.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDesep and Sujetos.activo is true");
		q.setParameter("idEvalDesep", idEvaluacionDesempeno);
		lGrilla2 = q.getResultList();
		if (conjuntoAntiReptido == null)
			conjuntoAntiReptido = new java.util.HashSet<String>();
		for (Sujetos o : lGrilla2) {
			conjuntoAntiReptido.add(o.getEmpleadoPuesto().getIdEmpleadoPuesto()
					.toString());
		}
	}

	public void searchGrilla2() {
		String sql = "select Sujetos from Sujetos Sujetos ";
		String elWhere = " where Sujetos.evaluacionDesempeno.idEvaluacionDesempeno = :idEvalDesep ";

		Map<String, Object> diccParam = new HashMap<String, Object>();

		diccParam.put("idEvalDesep", idEvaluacionDesempeno);
//		if (accionCU.equalsIgnoreCase("VER")) {
//			elWhere += " and Sujetos.activo is true ";
//		}
		if (idPaisExpede != null) {
			diccParam.put("idPais", idPaisExpede);
			elWhere += " and Sujetos.empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais = :idPais ";
		}
		if (nroDocIden != null && !nroDocIden.trim().isEmpty()) {
			diccParam.put("nroDocIden", nroDocIden);
			elWhere += " and Sujetos.empleadoPuesto.persona.documentoIdentidad = :nroDocIden ";
		}
		sql += elWhere;
		Query q = em.createQuery(sql);
		for (String o : diccParam.keySet()) {
			q.setParameter(o, diccParam.get(o));
		}
		lGrilla2 = q.getResultList();

	}

	public void agregarGrilla2() {
		if (lGrilla2 == null) {
			lGrilla2 = new ArrayList<Sujetos>();
		}
		Boolean existeRepetido = false;
		if (lGrilla1 != null && lGrilla1.size() > 0) {

			for (EmpleadoPuesto o : lGrilla1) {
				if (o.isSelecciono()) {
					if (antiRepetido(o.getIdEmpleadoPuesto().toString())) {
						Sujetos sujetos = new Sujetos();
						sujetos.setActivo(true);
						// sujetos.setEmpleadoPuesto(new EmpleadoPuesto());
						// sujetos.getEmpleadoPuesto().setIdEmpleadoPuesto(o.getIdEmpleadoPuesto());
						sujetos.setEmpleadoPuesto(o);
						sujetos.setEvaluacionDesempeno(evaluacionDesempeno);
						sujetos.setFechaAlta(new Date());
						sujetos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						lGrilla2.add(sujetos);
					} else {
						o.setSelecciono(true);
						existeRepetido = true;
					}
				}
			}
			if (existeRepetido) {
				statusMessages
						.add(Severity.ERROR,
								"Los elementos seleccionados no pudieron ser agregados puesto que ya se encuentran en la lista");
			} else {
				statusMessages.add(Severity.INFO,
						"Todos los elementos han sido agregados a la lista");
			}
		} else {
			statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar por lo menos un Funcionario para asignar a la Evaluación. Verifique");
		}
	}

	/**
	 * Verifica si existe un elemento dentro de una lista arbitraria
	 * 
	 * @return
	 */
	public Boolean antiRepetido(String idElemento) {
		if (conjuntoAntiReptido == null)
			conjuntoAntiReptido = new java.util.HashSet();
		if (conjuntoAntiReptido.add(idElemento)) {
			return true;
		}
		return false;
	}

	public void searchGrilla0(Boolean limpiarPanel) {
		String sql = "select ConfiguracionUoDet from ConfiguracionUoDet ConfiguracionUoDet "
				+ " where ConfiguracionUoDet.activo is true and ConfiguracionUoDet.configuracionUoCab.idConfiguracionUo = :idUoCab ";
		String elWhere = "";
		Map<String, Object> diccParams = new java.util.HashMap<String, Object>();
		diccParams.put("idUoCab", evaluacionDesempeno
				.getConfiguracionUoCab().getIdConfiguracionUo());
		if (limpiarPanel==true) {
			nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
			nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
		}
		if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
				&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim().isEmpty()
				&& nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim()
						.matches("[0-9]+[\\.0-9]+")) {
			String compos[] = nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim()
					.split("\\.");
			for (int i = 0; i < compos.length; i++) {
			}
			diccParams.put("orden", new Integer(compos[compos.length - 1]));
			diccParams.put("idUoDet", nivelEntidadOeeUtil.getIdUnidadOrganizativa());
			elWhere += " and ConfiguracionUoDet.orden = :orden ";
			elWhere += " and ConfiguracionUoDet.idConfiguracionUoDet = :idUoDet";
		}
		sql += elWhere + " order by ConfiguracionUoDet.denominacionUnidad ";
		Query q = em.createQuery(sql);
		// Seteando parametros

			for (String o : diccParams.keySet()) {
				q.setParameter(o, diccParams.get(o));
			}

		lGrilla0 = q.getResultList();

		// Incializando
		for (ConfiguracionUoDet o : lGrilla0) {
			o.setSelected(false);
		}

	}
	private String forInGrilla1() {
		String in = null;
		if (lGrilla0 != null && lGrilla0.size() > 0) {
			in = "";
			for (ConfiguracionUoDet o : lGrilla0) {
				if (o.getSelected())
					in += "," + o.getIdConfiguracionUoDet();
			}
			//agregado
			if (in.isEmpty()) {
				for (ConfiguracionUoDet o : lGrilla0) {
					in += "," + o.getIdConfiguracionUoDet();
				}
			}
			//*************
			in = in.replaceFirst(",", "");
		}
		return in;
	}

	private Boolean precondSearchGrilla1() {
		return true;
	}

	public void seleAll(String grilla) {
		if (grilla.equalsIgnoreCase("0")) {
			if (lGrilla0 != null)
				for (ConfiguracionUoDet o : lGrilla0) {
					o.setSelected(selAllGrilla0);
				}
		} else if (grilla.equalsIgnoreCase("1")) {
			if (lGrilla1 != null)
				for (EmpleadoPuesto o : lGrilla1) {
					o.setSelecciono(selAllGrilla1);
				}
		}
	}

	public void searchGrilla1() {
		if (!precondSearchGrilla1())
			return;
		Map<String, Object> diccParamsQuery = new java.util.HashMap<String, Object>();
		String sql = "select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto ";
		String elWhere = " where 1=1 ";
		String lInGrilla0 = forInGrilla1();
		if (lInGrilla0 != null && !lInGrilla0.isEmpty()) {//isempty para evitar sql incorrecto; Werner.
			elWhere += " and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet in ("
					+ lInGrilla0 + ") ";
		}
		if (idTipoNombramiento != null) {
//			diccParamsQuery.put("idTipoNombramiento", idTipoNombramiento);//original 
//			elWhere += " and EmpleadoPuesto.tipoNombramiento.idTipoNombramiento = :idTipoNombramiento ";//original
			if (idTipoNombramiento.equalsIgnoreCase("J")) {
				elWhere += " and EmpleadoPuesto.plantaCargoDet.jefatura is true ";
			} else if (idTipoNombramiento.equalsIgnoreCase("O")) {
				elWhere += " and EmpleadoPuesto.plantaCargoDet.jefatura is false ";
			} else if (idTipoNombramiento.equalsIgnoreCase("C")) {
				elWhere += " and EmpleadoPuesto.plantaCargoDet.contratado is true ";
//				elWhere += " and EmpleadoPuesto.datosEspecificosByIdDatosEspEstado.idDatosEspecificos = :idDEContratado ";
//				diccParamsQuery.put("idDEContratado", idDEContratado);
			} else if (idTipoNombramiento.equalsIgnoreCase("P")) {
				elWhere += " and EmpleadoPuesto.plantaCargoDet.permanente is true ";
//				elWhere += " and EmpleadoPuesto.datosEspecificosByIdDatosEspEstado.idDatosEspecificos = :idDEPermanente ";
//				diccParamsQuery.put("idDEPermanente", idDEPermanente);
			} else if (idTipoNombramiento.equalsIgnoreCase("M")) {
				elWhere += " and EmpleadoPuesto.plantaCargoDet.comisionado is true ";
//				elWhere += " and EmpleadoPuesto.datosEspecificosByIdDatosEspEstado.idDatosEspecificos = :idDEComisionado ";
//				diccParamsQuery.put("idDEComisionado", idDEComisionado);
			}
		}

		if (idPaisExpede != null) {
			diccParamsQuery.put("idPaisExpede", idPaisExpede);
			elWhere += " and EmpleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais = :idPaisExpede";
		}

		if (nroDocIden != null && !nroDocIden.trim().isEmpty()) {
			diccParamsQuery.put("nroDocIden", nroDocIden);
			elWhere += " and EmpleadoPuesto.persona.documentoIdentidad = :nroDocIden";
		}
		if (descPuesto != null && !descPuesto.trim().isEmpty())
		
		elWhere += " and lower(EmpleadoPuesto.plantaCargoDet.descripcion) like lower(concat('%', concat('"
				+ seguridadUtilFormController.caracterInvalido(descPuesto
						.toLowerCase()) + "','%')))";
		sql += elWhere
				+ " order by EmpleadoPuesto.persona.nombres,EmpleadoPuesto.persona.apellidos";
		

		Query q = em.createQuery(sql);

		// Cargar los parametros
		for (String o : diccParamsQuery.keySet()) {
			q.setParameter(o, diccParamsQuery.get(o));
		}

		lGrilla1 = q.getResultList();
		// Filtrar el objeto Gasto
		if (objGasto != null && !objGasto.trim().isEmpty()) {
			Iterator<EmpleadoPuesto> iter = lGrilla1.iterator();
			Boolean borrarElem = false;
			String composObjGasto[] = objGasto.split(",");
			while (iter.hasNext()) {
				EmpleadoPuesto empleadoPuesto = iter.next();
				// Inicializando
				empleadoPuesto.setSelecciono(false);
				borrarElem = true;
				for (EmpleadoConceptoPago o : empleadoPuesto
						.getEmpleadoConceptoPagos()) {
					for (String p : composObjGasto) {
						if (p.matches("[0-9]+")) {
							if (o.getObjCodigo() != null
									&& o.getObjCodigo().intValue() == Integer
											.parseInt(p)) {
								borrarElem = false;
								break;
							}
						}
					}
				}
				if (borrarElem) {
					iter.remove();
				}
			}
		}
		// Inicializar
		for (EmpleadoPuesto o : lGrilla1) {
 			o.setSelecciono(false);
		}

	}

	public String genCodigoUoDet(Long idUo) {
//		return nivelEntidadOeeUtil.getCodNivelEntidad() + "."
//				+ nivelEntidadOeeUtil.getCodSinEntidad() + "."
//				+ nivelEntidadOeeUtil.getOrdenUnidOrg() + "." + orden;
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(idUo);
		nivelEntidadOeeUtil.cargarUnidadOrganizativa();
		return nivelEntidadOeeUtil.getCodigoUnidOrgDep();
	}

	private void deleteFromSet(String id) {
		conjuntoAntiReptido.remove(id);
	}

	public void eliminar(int id) {
		if (lGrilla2Eliminados == null) {
			lGrilla2Eliminados = new ArrayList<Sujetos>();
		}
		if (lGrilla2.size() > id) {
			deleteFromSet(lGrilla2.get(id).getEmpleadoPuesto()
					.getIdEmpleadoPuesto().toString());
			lGrilla2Eliminados.add(lGrilla2.get(id));
			lGrilla2.remove(id);
		}
	}

	private List<Referencias> traerTipoNombramiento() {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.activo is true and Referencias.dominio = 'EVAL_CU551' ");
		return q.getResultList();
	}

	public List<SelectItem> traerTipoNombramientoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Todos los Funcionarios"));
		for (Referencias o : traerTipoNombramiento())
			si.add(new SelectItem(o.getValorAlf(), o.getDescLarga()));
		return si;
	}

	public String calcAntiguedadPuesto(Date fecha1) {
		return General.getAntiguedadFechas(fecha1, new Date());

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

	public Long getIdPaisExpede() {
		return idPaisExpede;
	}

	public void setIdPaisExpede(Long idPaisExpede) {
		this.idPaisExpede = idPaisExpede;
	}

	public List<EmpleadoPuesto> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<EmpleadoPuesto> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public List<Sujetos> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<Sujetos> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public String getIdTipoNombramiento() {
		return idTipoNombramiento;
	}

	public void setIdTipoNombramiento(String idTipoNombramiento) {
		this.idTipoNombramiento = idTipoNombramiento;
	}

	public String getObjGasto() {
		return objGasto;
	}

	public void setObjGasto(String objGasto) {
		this.objGasto = objGasto;
	}

	public String getNroDocIden() {
		return nroDocIden;
	}

	public void setNroDocIden(String nroDocIden) {
		this.nroDocIden = nroDocIden;
	}

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public String getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public List<ConfiguracionUoDet> getlGrilla0() {
		return lGrilla0;
	}

	public void setlGrilla0(List<ConfiguracionUoDet> lGrilla0) {
		this.lGrilla0 = lGrilla0;
	}

	public String getCodUoDet() {
		return codUoDet;
	}

	public void setCodUoDet(String codUoDet) {
		this.codUoDet = codUoDet;
	}

	public String getDescUoDet() {
		return descUoDet;
	}

	public void setDescUoDet(String descUoDet) {
		this.descUoDet = descUoDet;
	}

	public List<Sujetos> getlGrilla2Eliminados() {
		return lGrilla2Eliminados;
	}

	public void setlGrilla2Eliminados(List<Sujetos> lGrilla2Eliminados) {
		this.lGrilla2Eliminados = lGrilla2Eliminados;
	}

	public Boolean getSelAllGrilla0() {
		return selAllGrilla0;
	}

	public void setSelAllGrilla0(Boolean selAllGrilla0) {
		this.selAllGrilla0 = selAllGrilla0;
	}

	public Boolean getSelAllGrilla1() {
		return selAllGrilla1;
	}

	public void setSelAllGrilla1(Boolean selAllGrilla1) {
		this.selAllGrilla1 = selAllGrilla1;
	}

	public String getSelAllGrilla1Str() {
		return selAllGrilla1Str;
	}

	public void setSelAllGrilla1Str(String selAllGrilla1Str) {
		this.selAllGrilla1Str = selAllGrilla1Str;
	}

	public String getAccionCU() {
		return accionCU;
	}

	public void setAccionCU(String accionCU) {
		this.accionCU = accionCU;
	}

	public String getDescPuesto() {
		return descPuesto;
	}

	public void setDescPuesto(String descPuesto) {
		this.descPuesto = descPuesto;
	}

	public String getDesdeBandeja() {
		return desdeBandeja;
	}

	public void setDesdeBandeja(String desdeBandeja) {
		this.desdeBandeja = desdeBandeja;
	}

}
