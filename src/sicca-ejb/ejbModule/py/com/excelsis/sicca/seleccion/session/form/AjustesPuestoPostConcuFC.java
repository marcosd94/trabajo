package py.com.excelsis.sicca.seleccion.session.form;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections.map.HashedMap;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionElegibles;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.ListaElegiblesDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import py.com.excelsis.sicca.util.ValidadorController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("ajustesPuestoPostConcuFC")
public class AjustesPuestoPostConcuFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ValidadorController validadorController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private Long idEstadoDetDesierto;
	private Long idEstadoIncluido;
	private Long idExcepcion;
	private String codActividad;
	private String codCU;
	private String vacanciaOriginal;
	private Integer cantPostusIncluir;
	private String texto;
	private Boolean bloquearNoAprobados;

	private static String CANT_PUESTOS_DESIERTO = "CANT_PUESTOS_DESIERTO";
	private static String CANT_EXCEPCIONADO = "CANT_EXCEPCIONADO";
	private static String CANT_PUESTOS = "CANT_PUESTOS";
	private static String CANT_EXCLUIDO = "CANT_EXCLUIDO";
	private static String CANT_SELECCIONADOS = "CANT_SELECCIONADOS";
	private static String CANT_PASA_ELEGIBLE = "CANT_PASA_ELEGIBLE";
	private static String CANT_CAMBIAR = "CANT_CAMBIAR";
	private static String CANT_EXCLUIDO_CAMBIADO = "CANT_EXCLUIDO_CAMBIADO";
	private static String CANT_ELEGIBLES_INCLUIDOS = "CANT_ELEGIBLES_INCLUIDOS";
	private static String CANT_ELEGIBLES = "CANT_ELEGIBLES";
	private String TIPO_OBS_PUESTO = "TIPO_OBS_PUESTO";
	private String TIPO_OBS_SELE = "TIPO_OBS_SELE";
	private String TIPO_OBS_ELE = "TIPO_OBS_ELE";
	private Integer indexObs;
	private String tipoObs;
	private String observacion;
	public Observacion observacionMailSeleccionado;
	private String from;
	private Boolean primeraVez = true;
	private ListaElegiblesCab listaElegibleCab = null;

	List<ConcursoPuestoDet> lPuestos;
	List<EvalReferencialPostulante> lPostusSelected;
	List<ListaElegiblesDet> lElegibles;

	public void init() {
		if (primeraVez) {
			cargarCabecera();
			cargarPuestos();
			cargarPostulanteSeleccionado();
			cargarElegibleDisponible();
			primeraVez = false;
		}
	}

	public Boolean getPrimeraVez() {
		return primeraVez;
	}

	public void setPrimeraVez(Boolean primeraVez) {
		this.primeraVez = primeraVez;
	}

	private void cargarCabecera() {
		grupoPuestosController.initCabecera();
		vacanciaOriginal = vacanciaOriginal();
	}

	private String vacanciaOriginal() {

		Query q = em
				.createQuery("select count(ConcursoPuestoDet) from ConcursoPuestoDet ConcursoPuestoDet "
						+ " where ConcursoPuestoDet.estadoDet.estadoCab.descripcion like :desc "
						+ " and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo and ConcursoPuestoDet.activo = true ");

		q.setParameter("desc", "%CONCURSO%");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		Integer res = ((Long) q.getSingleResult()).intValue();

		return res.toString();
	}
	
	private Integer cantidadAprobados(){
		String sql = "select * from seleccion.eval_documental_cab "
				+ "where id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr ="
				+ this.grupoPuestosController.getIdConcursoPuestoAgr()
				+ ")"
				+ " and tipo like 'EVALUACION ADJUDICADOS' and aprobado = true";
		
		Integer retorno = new Integer(em.createNativeQuery(sql,EvalDocumentalCab.class).getResultList().size());
		
		return retorno;
	}
	
	private Integer cantidadPuestosDesiertos() {
		int retorno  = 0;
		
		for (ConcursoPuestoDet o : lPuestos) {
			if (o.getDesierto()) {
				retorno ++;

			}
		}
		
		return new Integer (retorno);
	}

	public void saveObs() {
		if (tipoObs.equalsIgnoreCase(TIPO_OBS_PUESTO)) {
			lPuestos.get(indexObs).setObservacion(observacion);
		} else if (tipoObs.equalsIgnoreCase(TIPO_OBS_SELE)) {
			lPostusSelected.get(indexObs).setObsExclusion(observacion);
		} else if (tipoObs.equalsIgnoreCase(TIPO_OBS_ELE)) {
			lElegibles.get(indexObs).setObsEstado(observacion);
		}
		limpiarObs();
	}

	public void limpiarObs() {
		indexObs = null;
		observacion = null;
	}

	public void cargarTipoObs(String tipo) {
		tipoObs = tipo;
	}

	private void cargarPuestos() {
		Query q = em
				.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
						+ " where ConcursoPuestoDet.activo is true and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		lPuestos = q.getResultList();
		// inicializar
		for (ConcursoPuestoDet o : lPuestos) {
			o.setExcepcionar(false);
			o.setDesierto(false);
		}
	}

	private Map<String, Integer> countsPuestos() {
		int canDesiertos = 0;
		int canPuestos = 0;
		int canExcepcionados = 0;
		for (ConcursoPuestoDet o : lPuestos) {
			if (o.getDesierto())
				canDesiertos++;
			else {
				canPuestos++;
			}
			if (o.getExcepcionar())
				canExcepcionados++;
		}
		Map<String, Integer> diccRespuesta = new HashedMap();
		diccRespuesta.put(CANT_PUESTOS_DESIERTO, canDesiertos);
		diccRespuesta.put(CANT_EXCEPCIONADO, canExcepcionados);
		diccRespuesta.put(CANT_PUESTOS, canPuestos);
		return diccRespuesta;
	}

	private Map<String, Integer> countsPostuSele() {
		int canExcluir = 0;
		int canSeleccionados = 0;
		int canPasaAelegible = 0;
		int canCambiar = 0;
		int canExclidoCambiado = 0;
		for (EvalReferencialPostulante o : lPostusSelected) {
			if (o.getExcluir())
				canExcluir++;
			else {
				canSeleccionados++;
			}
			if (o.getPasaAelegible())
				canPasaAelegible++;
			if (o.getCambiar())
				canCambiar++;
			if (o.getCambiar() && o.getExcluir()) {
				canExclidoCambiado++;
			}
		}
		Map<String, Integer> diccRespuesta = new HashedMap();
		diccRespuesta.put(CANT_EXCLUIDO, canExcluir);
		diccRespuesta.put(CANT_SELECCIONADOS, canSeleccionados);
		diccRespuesta.put(CANT_PASA_ELEGIBLE, canPasaAelegible);
		diccRespuesta.put(CANT_CAMBIAR, canCambiar);
		diccRespuesta.put(CANT_EXCLUIDO_CAMBIADO, canExclidoCambiado);
		return diccRespuesta;
	}

	private Map<String, Integer> countsElegibles() {
		int canIncluir = 0;
		for (ListaElegiblesDet o : lElegibles) {
			if (o.getIncluir())
				canIncluir++;
		}
		Map<String, Integer> diccRespuesta = new HashedMap();
		diccRespuesta.put(CANT_ELEGIBLES_INCLUIDOS, canIncluir);
		diccRespuesta.put(CANT_ELEGIBLES, lElegibles.size());
		return diccRespuesta;
	}

	private Map<String, Integer> mergeDiccCuentas() {
		Map<String, Integer> diccMerged = new HashedMap();
		Map<String, Integer> diccPuestos = countsPuestos();
		Map<String, Integer> diccPostuSele = countsPostuSele();
		Map<String, Integer> diccElegibles = countsElegibles();
		for (String o : diccPuestos.keySet()) {
			diccMerged.put(o, diccPuestos.get(o));
		}
		for (String o : diccPostuSele.keySet()) {
			diccMerged.put(o, diccPostuSele.get(o));
		}
		for (String o : diccElegibles.keySet()) {
			diccMerged.put(o, diccElegibles.get(o));
		}
		return diccMerged;
	}

	private Boolean precondSave(Map<String, Integer> diccCuentas) {
		// Si no hay puestos disponibles (CantidadPuestos = 0)
		// if (diccCuentas.get(CANT_PUESTOS).intValue() == 0) {
		// // DECLARAR DESIERTO
		// seleccionUtilFormController.declararDesierto(grupoPuestosController.getConcursoPuestoAgr());
		// reponerCategoriasController.reponerCategorias(grupoPuestosController.getConcursoPuestoAgr(),
		// "GRUPO", "CONCURSO");
		// statusMessages.add(Severity.ERROR,
		// SeamResourceBundle.getBundle().getString("GENERICO_DESIERTO"));
		// return false;
		// }
		// Si ‘Desierta’ es tildada, y si no tiene cargada la observación del
		// puesto...
		for (ConcursoPuestoDet o : lPuestos) {
			if (o.getDesierto()) {
				if (o.getObservacion() == null
						|| o.getObservacion().trim().isEmpty()) {
					statusMessages
							.add(Severity.ERROR,
									"Debe introducirse observación para puesto declarado desierto");
					return false;
				}
			}
		}
		// Si hay mas seleccionados que puestos disponibles....
		if (diccCuentas.get(CANT_SELECCIONADOS) > diccCuentas.get(CANT_PUESTOS)) {
			statusMessages
					.add(Severity.ERROR,
							"Debe excluirse seleccionados y pasar a lista de elegibles");
			return false;
		}
//		if (diccCuentas.get(CANT_SELECCIONADOS) < diccCuentas.get(CANT_PUESTOS) && diccCuentas.get(CANT_ELEGIBLES_INCLUIDOS) < diccCuentas.get(CANT_PUESTOS)
//				&& diccCuentas.get(CANT_ELEGIBLES).intValue() == 0) {
//			statusMessages
//					.add(Severity.ERROR,
//							"Hay puestos que no pueden cubrirse, deben declararse desiertos");
//			return false;
//		}
		// Validar en la grilla de ‘Postulantes seleccionados’:....
		// if
		// (!(codActividad.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS.getValor())
		// ||
		// codActividad.equalsIgnoreCase(ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION.getValor())))
		// {
		// if (diccCuentas.get(CANT_SELECCIONADOS).intValue() == 0
		// && diccCuentas.get(CANT_ELEGIBLES).intValue() == 0) {
		// // Declarar grupo desierto
		// seleccionUtilFormController.declararDesierto(grupoPuestosController.getConcursoPuestoAgr());
		// statusMessages.add(Severity.ERROR,
		// SeamResourceBundle.getBundle().getString("GENERICO_DESIERTO"));
		// return false;
		// }
		// }

		// Si esta tildado ‘Excluir?’ y no esta cargada la Observacion de ...
		for (EvalReferencialPostulante o : lPostusSelected) {
			if (o.getExcluir()) {
				if (o.getObsExclusion() == null
						|| o.getObsExclusion().trim().isEmpty()) {
					statusMessages.add(Severity.ERROR,
							"Falta  observación de exclusión");
					return false;
				}
			}
		}
		// Si esta tildado ‘Excluir?’ y no esta cargada la Observacion de...
		for (EvalReferencialPostulante o : lPostusSelected) {
			if (o.getExcluir()) {
				if (!(o.getPasaAelegible() || o.getCambiar())) {
					statusMessages.add(Severity.ERROR,
							"Falta Cambiar o Paso a elegible a excluido");
					return false;
				}
			}
		}
		// Si quedan más seleccionados cambiados que puestos disponibles...
		if (diccCuentas.get(CANT_CAMBIAR) > diccCuentas.get(CANT_PUESTOS)) {
			statusMessages
					.add(Severity.ERROR,
							"Debe excluirse seleccionados y pasarlos a lista de elegibles");
			return false;
		}
		// Si no hay suficientes elegibles para cambiar (SI NO ES LLAMADO...
//		if (codActividad == null
//				|| !(codActividad
//						.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
//								.getValor()) || codActividad
//						.equalsIgnoreCase(ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//								.getValor()))) {
			if (codActividad == null
					|| !(codActividad
							.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
									.getValor()))) {
			if (diccCuentas.get(CANT_CAMBIAR) > diccCuentas
					.get(CANT_ELEGIBLES_INCLUIDOS)) {
				statusMessages
						.add(Severity.ERROR,
								"Seleccionado no puede ser cambiado, ya no hay elegibles");
				return false;
			}
		}
		// En la grilla de ‘Elegibles disponibles’ controlar por cada registro
		// (SI NO...
//		if (codActividad == null
//				|| !(codActividad
//						.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
//								.getValor()) || codActividad
//						.equalsIgnoreCase(ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//								.getValor()))) {
			if (codActividad == null
					|| !(codActividad
							.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
									.getValor()))) {
			for (ListaElegiblesDet o : lElegibles) {
				if (o.getIncluir()) {
					if (o.getObsEstado() == null
							|| o.getObsEstado().trim().isEmpty()) {
						statusMessages.add(Severity.ERROR,
								"Falta  observación  de inclusión de elegible");
						return false;
					}
				}
			}
			if (diccCuentas.get(CANT_PUESTOS).intValue() < (diccCuentas.get(
					CANT_SELECCIONADOS).intValue() + diccCuentas.get(
					CANT_ELEGIBLES_INCLUIDOS).intValue())) {
				statusMessages
						.add(Severity.ERROR, "Sobran elegibles incluidos");
				return false;
			}
		}

		return true;
	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab uo = em.find(ConfiguracionUoCab.class,
					usuarioLogueado.getConfiguracionUoCab()
							.getIdConfiguracionUo());
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
					.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			grupoPuestosController.setConcursoPuestoAgr(em.find(
					ConcursoPuestoAgr.class,
					grupoPuestosController.getIdConcursoPuestoAgr()));
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil
					.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel",
					diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad",
					diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee",
					diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga",
					diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("usuAlta",
					usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("idGrupo",
					grupoPuestosController.getIdConcursoPuestoAgr());
			reportUtilFormController.getParametros().put(
					"grupo",
					grupoPuestosController.getConcursoPuestoAgr()
							.getDescripcionGrupo());
			reportUtilFormController.getParametros().put(
					"concurso",
					seleccionUtilFormController.genCodigoConcurso(
							grupoPuestosController.getConcursoPuestoAgr()
									.getConcurso(), uo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pdfListadoPuestos() {

		reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController
				.setNombreReporte("RPT_CU544_imprimir_listadoPostulantes");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	public void pdfSeleccionados() {

		reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController
				.setNombreReporte("RPT_CU544_imprimir_seleccionado");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	public void pdfElegibles() {

		reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController
				.setNombreReporte("RPT_CU544_imprimir_elgibles");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	public String save() {
		Map<String, Integer> diccCuentas = mergeDiccCuentas();
		if (!precondSave(diccCuentas))
			return null;
		try {
			initIds();
//			Verificar la cantidad de aprobados con la cantidad de puestos que puedan ser necesarios declarar desiertos.
			if(this.cantidadPuestosDesiertos() > (new Integer (this.vacanciaOriginal()) - this.cantidadAprobados())){
				statusMessages.add(Severity.WARN, "Ha marcado mas puestos de los necesarios para Declarar Desierto");
				return null;
			}
				
			
			
			// Actualizar tabla CONCURSO_PUESTO_DET, para el grupo...
			for (ConcursoPuestoDet o : lPuestos) {
				if (o.getDesierto()) {
					o.setEstadoDet(new EstadoDet());
					o.getEstadoDet().setIdEstadoDet(idEstadoDetDesierto);
					o.setActivo(false);
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setFechaMod(new Date());
					reponerCategoriasController.reponerCategorias(o, "PUESTO",
							"DESIERTO");

				}
				if (o.getExcepcionar()) {
					o.setExcepcionado(true);
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setFechaMod(new Date());
					if (codCU != null && codCU.equalsIgnoreCase("CU589")
							&& o.getExcepcion() == null) {
						o.setExcepcion(new Excepcion());
						o.getExcepcion().setIdExcepcion(idExcepcion);
					}

				}
				o = em.merge(o);
			}
			// Actualizar tabla EVAL_REFERENCIAL_POSTULANTE, para los ...
			ListaElegiblesCab elegiblesCab = null;

			for (EvalReferencialPostulante o : lPostusSelected) {
				if (o.getExcluir()) {
					o.setExcluido(true);
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setFechaMod(new Date());
					//se actualiza el estado de la postulacion para que aparezca en el bloque de excluidos de la lista corta
					o.getPostulacion().setEstadoPostulacion("EXCLUIDO");
					o.getPostulacion().setObsCancelacion("No Aprobo la Evaluación Documental Adjudicados");
					
					o = em.merge(o);
				}
				if (o.getCambiar()) {
					o.setCambiado(true);
					o.setElegible(false);
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setFechaMod(new Date());
					o = em.merge(o);
				}
				if (o.getPasaAelegible() != null && o.getPasaAelegible()) {
					o.setCambiado(false);
					o.setElegible(true);
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setFechaMod(new Date());
					// Establecer datos para registro de tabla...
					o = em.merge(o);
					if (listaElegibleCab == null) {

						elegiblesCab = new ListaElegiblesCab();
						elegiblesCab.setCantElegibles(diccCuentas
								.get(CANT_ELEGIBLES_INCLUIDOS));
						elegiblesCab
								.setConcursoPuestoAgr(grupoPuestosController
										.getConcursoPuestoAgr());
						elegiblesCab.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						elegiblesCab.setFechaAlta(new Date());
						em.persist(elegiblesCab);
						ListaElegiblesDet elegiblesDet = new ListaElegiblesDet();

						elegiblesDet.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						elegiblesDet.setFechaAlta(new Date());
						elegiblesDet.setListaElegiblesCab(elegiblesCab);
						elegiblesDet.setPostulacion(o.getPostulacion());
						elegiblesDet.setFechaAlta(new Date());
						elegiblesDet.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						elegiblesDet.setSeleccionado(true);
						elegiblesDet.setDisponible(true);
						em.persist(elegiblesDet);
					} else if (o.getIncluido() == null || !o.getIncluido()) {
						o.setIncluido(true);
						o = em.merge(o);
						ListaElegiblesDet elegiblesDet = new ListaElegiblesDet();

						elegiblesDet.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						elegiblesDet.setFechaAlta(new Date());
						elegiblesDet.setListaElegiblesCab(listaElegibleCab);
						elegiblesDet.setPostulacion(o.getPostulacion());
						elegiblesDet.setFechaAlta(new Date());
						elegiblesDet.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						elegiblesDet.setSeleccionado(true);
						elegiblesDet.setDisponible(true);
						em.persist(elegiblesDet);

					} else if (o.getIncluido() != null || o.getIncluido()) {
						Query q = em
								.createQuery("select ListaElegiblesDet from ListaElegiblesDet ListaElegiblesDet "
										+ " where ListaElegiblesDet.postulacion.idPostulacion =:idPostulacion "
										+ " and ListaElegiblesDet.listaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
						q.setParameter("idGrupo",
								grupoPuestosController.getIdConcursoPuestoAgr());
						q.setParameter("idPostulacion", o.getPostulacion()
								.getIdPostulacion());

						ListaElegiblesDet led = (ListaElegiblesDet) q
								.getSingleResult();
						led.setDisponible(false);
						led.setDatosEspecificos(null);
						em.merge(led);
					}

				} else {

				}
			}

			// Actualizar datos relacionados a elegibles, por cada registro de
			// ...
//			if (codActividad == null
//					|| !(codActividad
//							.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
//									.getValor()) || codActividad
//							.equalsIgnoreCase(ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION NOTIF-ELEGIBLE-SELECCIONADO
//									.getValor()))
			if (codActividad == null
					|| !(codActividad
							.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
									.getValor()))
					|| (texto != null && texto.trim().equals("EXC"))) {
				for (ListaElegiblesDet o : lElegibles) {
					if (o.getIncluir()) {
						o.setDatosEspecificos(new DatosEspecificos());
						o.getDatosEspecificos().setIdDatosEspecificos(
								idEstadoIncluido);
						o.setSeleccionado(true);
						o.setDisponible(false);
						if (codCU != null && codCU.equalsIgnoreCase("CU589")) {
							ExcepcionElegibles ee = new ExcepcionElegibles();
							ee.setActivo(true);
							ee.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							ee.setFechaAlta(new Date());
							ee.setPostulacion(o.getPostulacion());
							ee.setExcepcion(new Excepcion());
							ee.getExcepcion().setIdExcepcion(idExcepcion);
							em.persist(ee);

							ConcursoPuestoAgrExc cpae = new ConcursoPuestoAgrExc();
							cpae.setActivo(true);
							cpae.setFechaAlta(new Date());
							cpae.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							cpae.setEstado("LISTA_CORTA");
							cpae.setExcepcion(new Excepcion());
							cpae.getExcepcion().setIdExcepcion(idExcepcion);
							cpae.setConcursoPuestoAgr(grupoPuestosController
									.getConcursoPuestoAgr());

							em.persist(cpae);

						}
						EvalReferencialPostulante erp = obtenerERP(o
								.getPostulacion().getIdPostulacion());
						erp.setIncluido(true);
						erp.setExcluido(false);
						erp.setSeleccionado(true);
						erp.setListaCorta(true);
						if (codCU != null && !codCU.equalsIgnoreCase("CU350")) {
							erp.setExcepcion(new Excepcion());
							erp.getExcepcion().setIdExcepcion(idExcepcion);
							Excepcion ex = em.find(Excepcion.class, idExcepcion);
							ex.setEstado("LISTA CORTA");
							ex.setUsuMod(usuarioLogueado.getCodigoUsuario());
							ex.setFechaMod(new Date());
							em.merge(ex);
						}
						erp.setFechaMod(new Date());
						erp.setUsuMod(usuarioLogueado.getCodigoUsuario());
						
						erp = em.merge(erp);
						
						if (codCU != null && codCU.equalsIgnoreCase("CU350")) {
							enviarMailNUEVO(o.getPostulacion()
									.getPersonaPostulante().getPersona());
						}
					}
				}
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			this.bloquearNoAprobados = true;
			return "OK";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	private ListaElegiblesCab traerLECab(Long idGrupo) {
		Query q = em
				.createQuery("select ListaElegiblesCab from ListaElegiblesCab ListaElegiblesCab "
						+ " where ListaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", idGrupo);
		List<ListaElegiblesCab> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	
	/**
	 * 
	 * Para tener unificado la forma de envio y generacion de los mails se ha agregado un nuevo metodo para envio de mail
	 * Enrique Céspedes
	 * 
	 * 
	 * */
	/*
	private void enviarMailANTERIORNoUtilizado(Persona persona) {
		if (persona.getEMail() != null && General.isEmail(persona.getEMail())) {
			if (grupoPuestosController.getConcurso() == null) {
				grupoPuestosController.setConcursoPuestoAgr(em.find(
						ConcursoPuestoAgr.class,
						grupoPuestosController.getIdConcursoPuestoAgr()));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);

			VelocityContext context = new VelocityContext();
			Long idAgr = grupoPuestosController.getConcursoPuestoAgr()
					.getIdConcursoPuestoAgr();
			ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
			agr = em.find(ConcursoPuestoAgr.class, idAgr);
			context.put("ppNOMBRE", persona.getNombres());
			context.put("ppAPELLIDO", persona.getApellidos());
			context.put("CONCURSOnumero", agr.getConcurso().getNumero());
			context.put("CONCURSOanio", agr.getConcurso().getAnhio());
			context.put("concursoPuestoDescripcionGrupo",
					agr.getDescripcionGrupo());
			context.put("uoDenominacionUnidad", agr.getConcurso()
					.getConfiguracionUoCab().getDenominacionUnidad());
			context.put("UOdescripcionCorta", agr.getConcurso()
					.getConfiguracionUoCab().getDescripcionCorta());

			context.put("docs", obtenerDocs(idAgr));

			Template t = ve.getTemplate("/templates/email_544.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController.enviarMails(persona.getEMail(),
					writer.toString(), "NOTIF-ELEGIBLE-SELECCIONADO", null);
		}
	}*/
	
	private String enviarMailNUEVO(Persona persona) {
		String error = null;
		try {
							
				String asunto = null;
				String mensaje = null;
				String destinatario = persona.getEMail();
				Long idAgr = grupoPuestosController.getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr();
				ConcursoPuestoAgr concursoPuestoAgr = new ConcursoPuestoAgr();
				concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idAgr);
				String configuracion = concursoPuestoAgr.getConcurso()
						.getConfiguracionUoCab().getDescripcionCorta();
				if (configuracion == null) {
					configuracion = concursoPuestoAgr.getConcurso()
							.getConfiguracionUoCab().getDenominacionUnidad();
				}
				
				long idTaskInstance = jbpmUtilFormController
						.getIdTaskInstanceActual(concursoPuestoAgr
								.getIdProcessInstance());
				
				observacionMailSeleccionado = seleccionUtilFormController
						.cargarObservacion(idTaskInstance);
				
				String mails = seleccionUtilFormController
						.getMailsLugaresPresentacionAclaracion(concursoPuestoAgr);

				
				asunto = "ELEGIBLE SELECCIONADO";
				mensaje = getCuerpoEmailElegibleSeleccionado(persona, concursoPuestoAgr,configuracion, mails);
			
				seleccionUtilFormController.enviarMails(destinatario, mensaje,asunto);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return error;
	}
	
	
	private String getCuerpoEmailElegibleSeleccionado(Persona persona,
												ConcursoPuestoAgr concursoPuestoAgr, 
												String configuracionUo,
												String mails) {
		Concurso concurso = concursoPuestoAgr.getConcurso();
		String mensaje = "<p> <b> Estimado/a "
				+ persona.getNombres()
				+ " "
				+ persona.getApellidos()
				+ " </b></p> "
				+ "<p> Le comunicamos que ha sido seleccionado/a para la Evaluaci&oacute;n de Adjudicados para el Concurso: </p> "
				+ " <p><b> "
				+ concurso.getNumero()
				+ concurso.getAnhio()
				+ " de "
				+ concurso.getConfiguracionUoCab().getDescripcionCorta()
				+ " "
				+ concurso.getNombre()
				+ "</b></p> "
				+ " <p><b> "
				+ concursoPuestoAgr.getDescripcionGrupo()
				+ "</b></p> "
				+" Deber&aacute; acercar los siguientes documentos: ";
				
				List<String> listaDocs = obtenerDocs(concursoPuestoAgr.getIdConcursoPuestoAgr());
				
				for (int i = 0; i < listaDocs.size() ; i++){
					mensaje += listaDocs.get(i);
				}
				
						
				
				if (observacionMailSeleccionado != null && observacionMailSeleccionado.getObservacion() != null )
					mensaje +=" <p><b> " + observacionMailSeleccionado.getObservacion() + " </b></p>" + " <p> </p>" ; 
				
				mensaje += " <p> </p>" + " <p> </p>"
						+ "<p align=\"justify\">&nbsp; </p>"
						+ "<strong>Atentamente,</strong>" + "<p><strong>"
						+ configuracionUo + "</strong></p>";

		if (!funcionarioUtilFormController.vacio(mails)) {
			mensaje += " <p> </p>"
					+ "Para mayor informaci&oacute;n comunicarse con: " + mails;
		}

		return mensaje;
	}
	
	public Boolean habPanel() {
		if (codActividad == null)
			return true;
//		if ((codActividad.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
//				.getValor()) || codActividad
//				.equalsIgnoreCase(ActividadEnum.ELABORAR_RESOLUCION_ADJUDICACION
//						.getValor()))) {
		if ((codActividad.equalsIgnoreCase(ActividadEnum.PUBLICAR_ADJUDICADOS
				.getValor()))) {
			return false;
		}
		return true;
	}

	public Boolean habOperacionED(Date fecha) {
		if (fecha.getTime() > (new Date()).getTime())
			return true;
		return false;
	}

	private List<String> obtenerDocs(Long id) {
		Query q = em
				.createQuery("select MatrizDocConfigDet.datosEspecificos from MatrizDocConfigDet MatrizDocConfigDet "
						+ " where MatrizDocConfigDet.activo is true "
						+ " and MatrizDocConfigDet.matrizDocConfigEnc.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ id + " and MatrizDocConfigDet.adjudicacion is true ");
		List<DatosEspecificos> lista = q.getResultList();
		List<String> lista2 = new ArrayList<String>();
		for (DatosEspecificos o : lista) {
			lista2.add( " <p> </p>" +o.getDescripcion()+ " <p> </p>");
		}
		return lista2;
	}

	private EvalReferencialPostulante obtenerERP(Long idPostulacion) {
		Query q = em
				.createQuery("select EvalReferencialPostulante from EvalReferencialPostulante EvalReferencialPostulante"
						+ " where EvalReferencialPostulante.activo is true "
						+ " and EvalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ " and EvalReferencialPostulante.postulacion.idPostulacion = :idPostulacion");
		q.setParameter("idPostulacion", idPostulacion);
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		return (EvalReferencialPostulante) q.getSingleResult();

	}

	private void initIds() {
		
		idEstadoDetDesierto = seleccionUtilFormController.buscarEstadoDet("CONCURSO", "DESIERTO").getIdEstadoDet();
		if (seleccionUtilFormController.traerDatosEspecificos("ESTADOS DE ELEGIBLE", "INCLUIDO").getIdDatosEspecificos() != null)
			idEstadoIncluido = seleccionUtilFormController.traerDatosEspecificos("ESTADOS DE ELEGIBLE", "INCLUIDO").getIdDatosEspecificos();
		
		listaElegibleCab = traerLECab(grupoPuestosController.getIdConcursoPuestoAgr());
	}

	public String calcPuntaje(Float puntaje) {
		if (puntaje == null)
			return "0";
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(puntaje);
	}

	public String calcPuntaje(Postulacion postulacion) {
		Query q = em
				.createQuery("select EvalReferencialPostulante  from EvalReferencialPostulante EvalReferencialPostulante "
						+ " where EvalReferencialPostulante.postulacion.idPostulacion = :idPostulacion and EvalReferencialPostulante.activo is true ");
		q.setParameter("idPostulacion", postulacion.getIdPostulacion());
		List<EvalReferencialPostulante> lista = q.getResultList();
		if (lista.size() > 0) {
			Float puntaje = lista.get(0).getPuntajeRealizado();
			if (puntaje == null)
				return "0";

			NumberFormat formatter = new DecimalFormat("#0.00");
			return formatter.format(puntaje);
		}
		return "0";

	}

	private void cargarPostulanteSeleccionado() {
		Query q = em
				.createQuery("select EvalReferencialPostulante from EvalReferencialPostulante EvalReferencialPostulante "
						+ " where EvalReferencialPostulante.activo is true "
						+ " and EvalReferencialPostulante.listaCortaReplica is true  "
						+ " and EvalReferencialPostulante.seleccionadoReplica is true "
						+ " and EvalReferencialPostulante.excluido is false "
						+ " and EvalReferencialPostulante.postulacion.usuCancelacion is null "
						+ " and EvalReferencialPostulante.postulacion.fechaCancelacion is null "
						+ " and EvalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		lPostusSelected = q.getResultList();
	}

	private void cargarElegibleDisponible() {
		Query q = em
				.createQuery("select ListaElegiblesDet from ListaElegiblesDet ListaElegiblesDet "

						+ " where   ListaElegiblesDet.disponible is true " +
								"and ListaElegiblesDet.postulacion.activo is true " +
								"and ListaElegiblesDet.postulacion.usuCancelacion is null "
						+ " and ListaElegiblesDet.listaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ " and ListaElegiblesDet.listaElegiblesCab.idListaElegiblesCab   = "
						+ "(select max(idListaElegiblesCab) from ListaElegiblesCab listaElegiblesCab where listaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo )");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		lElegibles = q.getResultList();
	}

	public String validarPuesto(ConcursoPuestoDet cpd) {
		return "-";
		// ValidadorDTO validadorDTO =
		// validadorController.validarCfg("PUESTO", "TSTVALDOC", null,
		// cpd.getPlantaCargoDet(), null, null);
		// if (validadorDTO.isValido()) {
		// return "LISTA CORTA";
		// } else {
		// return validadorDTO.getMensaje();
		// }
	}

	public String validarPuesto2(Persona persona) {
		ValidadorDTO validadorDTO = validadorController.validarCfg("PUESTO",
				"TSTVALDOC", persona, null, null, null);
		if (validadorDTO.isValido()) {
			return "OK";
		} else {
			return validadorDTO.getMensaje();
		}
	}

	public String codPostulante(Postulacion postulacion) {
		if (postulacion != null) {
			postulacion = em.find(Postulacion.class,
					postulacion.getIdPostulacion());
			if (postulacion.getPersonaPostulante() == null)
				return "-";
			return seleccionUtilFormController.usuarioPostulante(
					postulacion.getPersonaPostulante().getPersona()
							.getIdPersona()).getCodigoUsuario();
		}

		return "-";
	}

	public Postulacion updatePostulacion(Postulacion postulacion) {
		if (postulacion != null) {
			postulacion = em.find(Postulacion.class,
					postulacion.getIdPostulacion());
			// Persona persona = em.find(Persona.class,
			// postulacion.getPersona().getIdPersona());
			// postulacion.setPersona(persona);
		}
		return postulacion;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public String getCodCU() {
		return codCU;
	}

	public void setCodCU(String codCU) {
		this.codCU = codCU;
	}

	public String getVacanciaOriginal() {
		return vacanciaOriginal;
	}

	public void setVacanciaOriginal(String vacanciaOriginal) {
		this.vacanciaOriginal = vacanciaOriginal;
	}

	public List<ConcursoPuestoDet> getlPuestos() {
		return lPuestos;
	}

	public void setlPuestos(List<ConcursoPuestoDet> lPuestos) {
		this.lPuestos = lPuestos;
	}

	public List<EvalReferencialPostulante> getlPostusSelected() {
		return lPostusSelected;
	}

	public void setlPostusSelected(
			List<EvalReferencialPostulante> lPostusSelected) {
		this.lPostusSelected = lPostusSelected;
	}

	public List<ListaElegiblesDet> getlElegibles() {
		return lElegibles;
	}

	public void setlElegibles(List<ListaElegiblesDet> lElegibles) {
		this.lElegibles = lElegibles;
	}

	public Integer getCantPostusIncluir() {
		return cantPostusIncluir;
	}

	public void setCantPostusIncluir(Integer cantPostusIncluir) {
		this.cantPostusIncluir = cantPostusIncluir;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTIPO_OBS_PUESTO() {
		return TIPO_OBS_PUESTO;
	}

	public void setTIPO_OBS_PUESTO(String tIPO_OBS_PUESTO) {
		TIPO_OBS_PUESTO = tIPO_OBS_PUESTO;
	}

	public String getTIPO_OBS_SELE() {
		return TIPO_OBS_SELE;
	}

	public void setTIPO_OBS_SELE(String tIPO_OBS_SELE) {
		TIPO_OBS_SELE = tIPO_OBS_SELE;
	}

	public String getTIPO_OBS_ELE() {
		return TIPO_OBS_ELE;
	}

	public void setTIPO_OBS_ELE(String tIPO_OBS_ELE) {
		TIPO_OBS_ELE = tIPO_OBS_ELE;
	}

	public Integer getIndexObs() {
		return indexObs;
	}

	public void setIndexObs(Integer indexObs) {
		this.indexObs = indexObs;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getTipoObs() {
		return tipoObs;
	}

	public void setTipoObs(String tipoObs) {
		this.tipoObs = tipoObs;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Boolean getBloquearNoAprobados() {
		return bloquearNoAprobados;
	}

	public void setBloquearNoAprobados(Boolean bloquearNoAprobados) {
		this.bloquearNoAprobados = bloquearNoAprobados;
	}

}
