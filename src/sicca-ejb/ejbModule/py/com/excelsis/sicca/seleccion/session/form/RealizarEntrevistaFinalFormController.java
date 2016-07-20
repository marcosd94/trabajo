package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.metadata.spi.retrieval.Item;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.EstadoGrupoEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Ternas;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.ListaElegiblesDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalReferencialPostulanteList;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Scope(ScopeType.CONVERSATION)
@Name("realizarEntrevistaFinalFormController")
public class RealizarEntrevistaFinalFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1661926467489160485L;
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
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	EvalReferencialPostulanteList evalReferencialPostulanteList;
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	public ConcursoPuestoAgr concursoPuestoAgr;
	public Observacion observacion;
	public String observacionTerna;
	private boolean entrevistaEnviada = false;
	private boolean ternasEnviadas = false;
	private String entity = "Observacion";
	private String idEntity = "";
	private String nombrePantalla = "realizarEntrevistaFinal";
	private String ubicacionFisica = "";
	private Boolean isDesierto = false;
	private Boolean siguienteTarea = false;

	private List<EvalReferencialPostulante> listaPostulantes;
	private List<EvalReferencialPostulante> listaElegibles;
	private List<EvalReferencialPostulante> listaPostulantesAux;
	private List<Ternas> listaTernas;

	private Integer cantidadVacancias;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Long idTernaActual;
	private Long idEvalRefPostActual;
	private Ternas ternaActual;

	public void initEntrevista() {
		
		String sqlTerna = "select * from seleccion.ternas where ternas.id_concurso_puesto_agr="
				+ grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr() + " and ternas.id_terna=" + idTernaActual;
		Query qTerna = em.createNativeQuery(sqlTerna, Ternas.class);
		if(!qTerna.getResultList().isEmpty()){
			ternaActual = (Ternas) qTerna.getResultList().get(0);
		}
		
		cargarEntrevista();
		validarOee();
		
		isDesierto = seleccionUtilFormController
				.checkDesierto(grupoPuestosController.getConcursoPuestoAgr());
		/**
		 * Incidencia 0001640 Llamar al CU604 (le envía como parámetros el
		 * id_concurso_puesto_agr, y las cadenas ‘GRUPO’, ‘CONCURSO’)
		 * **/

		reponerCategoriasController.reponerCategorias(
				grupoPuestosController.getConcursoPuestoAgr(), "GRUPO",
				"CONCURSO");

		/**
		 * fin
		 * */
	}
	
	public void initTernas() {

		cargarTernas();
		validarOee();
		isDesierto = seleccionUtilFormController
				.checkDesierto(grupoPuestosController.getConcursoPuestoAgr());
		/**
		 * Incidencia 0001640 Llamar al CU604 (le envía como parámetros el
		 * id_concurso_puesto_agr, y las cadenas ‘GRUPO’, ‘CONCURSO’)
		 * **/

		reponerCategoriasController.reponerCategorias(
				grupoPuestosController.getConcursoPuestoAgr(), "GRUPO",
				"CONCURSO");
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOee(
				null,
				grupoPuestosController.getIdConcursoPuestoAgr(),
				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO",
						"ENTREVISTA MAI") + "",
				ActividadEnum.REALIZAR_ENTREVISTA_FINAL.getValor());
	}

	public void limpiar() {
		observacion = null;
		entrevistaEnviada = false;
		ternasEnviadas = false;
	}

	public void cargarEntrevista() {
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
				.getConcursoPuestoAgr();
		long idTaskInstance = jbpmUtilFormController
				.getIdTaskInstanceActual(concursoPuestoAgr
						.getIdProcessInstance());
		observacion = seleccionUtilFormController
				.cargarObservacion(idTaskInstance);

		if (observacion == null) {
			observacion = new Observacion();
			//entrevistaEnviada = false;
		}/* else {
			entrevistaEnviada = true;
		}*/
		
		if (ternaEntrevistada(idTernaActual)) {
			entrevistaEnviada = true;
		} else {
			entrevistaEnviada = false;
		}

		evalReferencialPostulanteList.getEvalReferencialPostulante().setActivo(
				true);
		evalReferencialPostulanteList.getEvalReferencialPostulante()
				.setConcursoPuestoAgr(concursoPuestoAgr);
		evalReferencialPostulanteList.getEvalReferencialPostulante()
				.setListaCorta(new Boolean(true));
		//listaPostulantes = evalReferencialPostulanteList.getListaCU148();
		listaPostulantes = getListaTernados();
		observacionTerna = ternaActual.getObservacion();

		Calendar c = Calendar.getInstance();
		ubicacionFisica = "\\SICCA\\"
				+ c.get(Calendar.YEAR)
				+ "\\OEE\\"
				+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab()
						.getIdConfiguracionUo()
				+ "\\"
				+ concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + "\\"
				+ concursoPuestoAgr.getConcurso().getIdConcurso() + "\\"
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		cantidadVacancias = getCantidadVacancias(concursoPuestoAgr);
	}

	private Integer getCantidadVacancias(ConcursoPuestoAgr concursoPuestoAgr) {
		// TODO Auto-generated method stub
		if (concursoPuestoAgr != null
				&& concursoPuestoAgr.getIdConcursoPuestoAgr() != null) {
			try {
				String cad = " select * "
						+ " from seleccion.concurso_puesto_det "
						+ " where id_concurso_puesto_agr = "
						+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " "
						+ "and activo = true ";

				List<ConcursoPuestoDet> lista = em.createNativeQuery(cad,
						ConcursoPuestoDet.class).getResultList();
				if (lista != null)
					return lista.size();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void generarListaElegibles() {
		listaElegibles = new ArrayList<EvalReferencialPostulante>();

		int cantSeleccionado = 0;
		for (EvalReferencialPostulante evalReferencialPostulante : listaPostulantes) {
			if (evalReferencialPostulante.getSeleccionado()) {
				
				if(!evalReferencialPostulante.getPresente()){
					statusMessages.clear();
					statusMessages
						.add(Severity.ERROR,
								"No puede seleccionar a un postulante ausente.");
				return;
				}
				else
					cantSeleccionado++;
			}
		}

		if (cantSeleccionado > cantidadVacancias) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"La cantidad de seleccionados no puede ser mayor a la cantidad de vacancias.");
			return;
		}

		for (EvalReferencialPostulante evalReferencialPostulante : listaPostulantes) {
			if (evalReferencialPostulante.getPresente()	&& !evalReferencialPostulante.getSeleccionado()) {
				listaElegibles.add(evalReferencialPostulante);
			}
		}

		if (listaElegibles.size() > 0) {
			Comparator comparator = Collections.reverseOrder();
			Collections.sort(listaElegibles, comparator);
		}
	}

	public void save() {

		// Nuevo
		try {
			ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
					.getConcursoPuestoAgr();
			// Verificar que seleccionados sea igual a la cantidad de vacancias
			// disponibles
			if (!validar(concursoPuestoAgr)) {
				return;
			}

			// Guardar Seleccionados
			int ac = 1;
			for (EvalReferencialPostulante evalReferencialPostulante : listaPostulantes) {
				if (evalReferencialPostulante.getSeleccionado()) {
					evalReferencialPostulante.setSeleccionadoReplica(evalReferencialPostulante.getSeleccionado());
					em.merge(evalReferencialPostulante);
					ternaActual.setSeleccionado(evalReferencialPostulante);
					em.merge(ternaActual);
				}
				else{
					if(ac == 1){
						evalReferencialPostulante.setSeleccionado(null);
						evalReferencialPostulante.setSeleccionadoReplica(null);
						em.merge(evalReferencialPostulante);
						ternaActual.setElegible_1(evalReferencialPostulante);
						em.merge(ternaActual);
						ac++;
					}
					else{
						evalReferencialPostulante.setSeleccionado(null);
						evalReferencialPostulante.setSeleccionadoReplica(null);
						em.merge(evalReferencialPostulante);
						ternaActual.setElegible_2(evalReferencialPostulante);
						em.merge(ternaActual);
					}
				}
			}

			// Guardar elegibles
			/*for (int i = 0; i < listaElegibles.size(); i++) {
				EvalReferencialPostulante evalReferencialPostulante = listaElegibles
						.get(i);
				Boolean presente = evalReferencialPostulante.getPresente();
				if (presente == null)
					presente = false;

				evalReferencialPostulante = em.find(
						EvalReferencialPostulante.class,
						evalReferencialPostulante
								.getIdEvalReferencialPostulante());
				evalReferencialPostulante.setSeleccionado(false);
				evalReferencialPostulante.setPresente(presente);
				evalReferencialPostulante.setOrden(i + 1);
				em.merge(evalReferencialPostulante);
			}*/

			// Guardar otros datos de la terna
			Date fecha = new Date();
			if (ternaActual.getObservacion() == null) {
				ternaActual.setObservacion(observacionTerna);
				ternaActual.setFechaAlta(fecha);
				ternaActual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.merge(ternaActual);

			} else {
				ternaActual.setObservacion(observacionTerna);
				ternaActual.setFechaMod(new Date());
				ternaActual.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(ternaActual);
			}

			em.flush();
		} catch (Exception e) {

		}

		entrevistaEnviada = true;
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}
	
	public void save2() {
		try {
			ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController.getConcursoPuestoAgr();
			if (!validar2(concursoPuestoAgr)) {
				return;
			}
			long idTaskInstance = jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance());
					
			// Guardar Observacion
			Date fecha = new Date();
			observacion.setIdTaskInstance(idTaskInstance);
			observacion.setConcurso(concursoPuestoAgr.getConcurso());
			if (observacion.getIdObservacion() == null) {
				observacion.setFechaAlta(fecha);
				observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(observacion);
			} else {
				observacion.setFechaMod(new Date());
				observacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(observacion);
			}
			
			// Guardar elegibles
			for (int i = 0; i < listaElegibles.size(); i++) {
				EvalReferencialPostulante evalReferencialPostulante = listaElegibles.get(i);
				Boolean presente = evalReferencialPostulante.getPresente();
				if (presente == null)
					presente = false;

				evalReferencialPostulante = em.find(EvalReferencialPostulante.class,evalReferencialPostulante.getIdEvalReferencialPostulante());
				evalReferencialPostulante.setSeleccionado(false);
				evalReferencialPostulante.setSeleccionadoReplica(false);
				evalReferencialPostulante.setPresente(presente);
				evalReferencialPostulante.setOrden(i + 1);
				em.merge(evalReferencialPostulante);
			}
			em.flush();
			//AGREGAR A LA LISTA DE ELEGIBLES A TODAS LAS PERSONAS QUE HAYAN SUPERADO EL MINIMO REQUERIDO 
			// Y QUE HAYAN PARTICIPADO DE TODAS LAS ETAPAS
			//08/04/2016
			String sql = "select * from seleccion.eval_referencial_postulante eval_ref_pos "
					+ " where eval_ref_pos.id_concurso_puesto_agr = "+concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " and eval_ref_pos.fecha_alta = (select max(fecha_alta) from seleccion.eval_referencial_postulante "
					+ " where id_postulacion = eval_ref_pos.id_postulacion)"
					+ " and eval_ref_pos.seleccionado is null "
					+ " and eval_ref_pos.puntaje_realizado >= (select datos.porc_minimo from seleccion.datos_grupo_puesto datos "
					+ " where id_concurso_puesto_agr = "+concursoPuestoAgr.getIdConcursoPuestoAgr()+")"
					+ " and eval_ref_pos.id_postulacion not in (select id_postulacion from seleccion.eval_referencial"
					+ " where presente is false"
					+ " and id_postulacion in (select id_postulacion from seleccion.postulacion "
					+ " where id_concurso_puesto_agr = "+concursoPuestoAgr.getIdConcursoPuestoAgr()+"))"
					+ " order by eval_ref_pos.puntaje_realizado desc";

			List<EvalReferencialPostulante> lista = em.createNativeQuery(sql, EvalReferencialPostulante.class).getResultList();
			for (int i = 0; i < lista.size(); i++) {
				EvalReferencialPostulante evalReferencialPostulante = lista.get(i);
//				Boolean presente = evalReferencialPostulante.getPresente();
//				if (presente == null)
//					presente = false;

				evalReferencialPostulante = em.find(EvalReferencialPostulante.class,evalReferencialPostulante.getIdEvalReferencialPostulante());
				evalReferencialPostulante.setSeleccionado(false);
				evalReferencialPostulante.setSeleccionadoReplica(false);
//				evalReferencialPostulante.setPresente(presente);
//				evalReferencialPostulante.setOrden(i + 1);
				em.merge(evalReferencialPostulante);
			}
			
			
			em.flush();
		} catch (Exception e) {

		}
		ternasEnviadas = true;
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	private boolean validar(ConcursoPuestoAgr concursoPuestoAgr) {
		// verificar estado
		Referencias referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO",
				EstadoGrupoEnum.ENTREVISTA_MAI.getDescripcion());
		if (referencias == null || referencias.getValorNum().longValue() != concursoPuestoAgr.getEstado().longValue()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El grupo debe estar en estado: "
							+ EstadoGrupoEnum.ENTREVISTA_MAI.getDescripcion()
							+ ". Verifique.");
			return false;
		}
		
		for (EvalReferencialPostulante eval : listaPostulantes) {
			if (eval.getSeleccionado() && !eval.getPresente()) {
				statusMessages.add(Severity.ERROR,"No puede seleccionar un postulante que no estubo presente. Verifique");
				return false;
			}
		}

		/**
		 * se modiica segun la incidencia 0001323
		 **/

		// Verificar cantidad de puestos seleccionados
		int cantidadSeleccionados = 0;
		for (EvalReferencialPostulante evalReferencialPostulante : listaPostulantes) {
			if (evalReferencialPostulante.getSeleccionado()) {
				cantidadSeleccionados++;
			}
		}

		/*if (cantidadSeleccionados != cantidadVacancias.intValue()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"La cantidad de seleccionados debe ser igual a: "
							+ cantidadVacancias.intValue());
			return false;
		}*/
		
		//if (cantidadSeleccionados != cantidadVacancias.intValue()) {
		if (cantidadSeleccionados != 1) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe haber un solo seleccionado.");
			return false;
		}

		return true;
	}
	
	private boolean validar2(ConcursoPuestoAgr concursoPuestoAgr) {
		// verificar estado
		Referencias referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO",
				EstadoGrupoEnum.ENTREVISTA_MAI.getDescripcion());
		if (referencias == null || referencias.getValorNum().longValue() != concursoPuestoAgr.getEstado().longValue()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El grupo debe estar en estado: "
							+ EstadoGrupoEnum.ENTREVISTA_MAI.getDescripcion()
							+ ". Verifique.");
			return false;
		}
				//if (cantidadSeleccionados != cantidadVacancias.intValue()) {
		if (sobranTernasSinEntrevistar()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Sobran ternas sin entrevistar.");
			return false;
		}

		return true;
	}

	public List<EvalReferencialPostulante> cantElegibles(Long idGrupo) {
		Query q = em
				.createQuery("select EvalReferencialPostulante from EvalReferencialPostulante EvalReferencialPostulante"
						+ " where EvalReferencialPostulante.activo is true and EvalReferencialPostulante.listaCortaReplica is true "
						+ " and EvalReferencialPostulante.seleccionadoReplica is false and EvalReferencialPostulante.presente is true "
						+ " and EvalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idGrupo", idGrupo);
		return q.getResultList();
	}

	public String nextTask(String sgteActividad) {
		try {
		
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
				.getConcursoPuestoAgr();

		/*if (!validar(concursoPuestoAgr)) {
			return null;
		}*/


		EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet(
				"CONCURSO", "LISTA CORTA");
		List<ConcursoPuestoDet> lConcursoPuestoDet = seleccionUtilFormController
				.getConcursoPuestoDet(grupoPuestosController
						.getIdConcursoPuestoAgr());
		for (ConcursoPuestoDet cpd : lConcursoPuestoDet) {
			cpd.setEstadoDet(estadoDet);
			cpd.getPlantaCargoDet().setEstadoDet(estadoDet);
			cpd.setPlantaCargoDet(em.merge(cpd.getPlantaCargoDet()));
			cpd = em.merge(cpd);
		}
		// Insercion de registros
		Calendar cal = Calendar.getInstance();
		// Si o si debe traer valor... la sgte linea
		Integer diasVtoValidez = seleccionUtilFormController
				.traerReferenciasPorDominio("LISTA_ELEGIBLES").getValorNum();
		List<EvalReferencialPostulante> lErp = cantElegibles(grupoPuestosController
				.getIdConcursoPuestoAgr());
		ListaElegiblesCab lec = new ListaElegiblesCab();
		lec.setFechaAlta(new Date());
		lec.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		cal.add(Calendar.DATE, diasVtoValidez);
		lec.setVtoValidezLista(cal.getTime());
		lec.setCantElegibles(lErp.size());
		lec.setConcursoPuestoAgr(concursoPuestoAgr);
		em.persist(lec);
		for (EvalReferencialPostulante o : lErp) {
			ListaElegiblesDet led = new ListaElegiblesDet();
			led.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			led.setFechaAlta(new Date());
			led.setListaElegiblesCab(lec);
			led.setDisponible(true);
			led.setDatosEspecificos(null);
			led.setPostulacion(o.getPostulacion());
			em.persist(led);
		}

		/* End incidencia 1564 */
		// // Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		if (sgteActividad.equalsIgnoreCase("REALIZAR_ENTREVISTA_FINAL")) {
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.REALIZAR_ENTREVISTA_FINAL);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ);

		} else if (sgteActividad
				.equalsIgnoreCase("REALIZAR_ENTREVISTA_FINAL_I")) {
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CIO_REALIZAR_ENTREVISTA_FINAL);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.CIO_VALIDAR_MATRIZ_DOCUMENTAL_ADJ);

		}
		jbpmUtilFormController.setTransition("next1");
		if (jbpmUtilFormController.nextTask()) {
			em.flush();
			enviarEmails(concursoPuestoAgr);
			publicacionPortal(genTextoPublicacionListaFinalTerna(), concursoPuestoAgr.getConcurso().getIdConcurso(),concursoPuestoAgr.getIdConcursoPuestoAgr());
			siguienteTarea = true;
		}
		enviarEmails(concursoPuestoAgr);
		} catch (Exception e) {
			return null;
		}
		
		return "nextTask";
	}

	/**
	 * Se envia mails a todos los postulantes
	 */
	private String enviarEmails(ConcursoPuestoAgr concursoPuestoAgr) {
		String error = null;
		String sqlListaPostulantes = "select * from seleccion.eval_referencial_postulante where eval_referencial_postulante.id_concurso_puesto_agr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and eval_referencial_postulante.lista_corta_replica=true and eval_referencial_postulante.presente=true";
		//Query q3 = em.createNativeQuery(sqlListaPostulantes, EvalReferencialPostulante.class);
		List<EvalReferencialPostulante> listaPostulantesEmails = em.createNativeQuery(sqlListaPostulantes, EvalReferencialPostulante.class).getResultList();
		try {
			for (EvalReferencialPostulante evalReferencialPostulante : listaPostulantesEmails) {
				// solo los postulantes activos
				if (!evalReferencialPostulante.getPostulacion().isActivo())
					continue;
				String asunto = null;
				String mensaje = null;
				String destinatario = evalReferencialPostulante
						.getPostulacion().getPersonaPostulante().getEMail();
				String configuracion = concursoPuestoAgr.getConcurso()
						.getConfiguracionUoCab().getDescripcionCorta();
				if (configuracion == null) {
					configuracion = concursoPuestoAgr.getConcurso()
							.getConfiguracionUoCab().getDenominacionUnidad();
				}
				String mails = seleccionUtilFormController
						.getMailsLugaresPresentacionAclaracion(concursoPuestoAgr);

				if (evalReferencialPostulante.getSeleccionado()) {
					asunto = "(" + configuracion + ") – SELECCIONADOS";
					mensaje = getCuerpoEmailSeleccionados(
							evalReferencialPostulante.getPostulacion()
									.getPersonaPostulante(), concursoPuestoAgr,
							configuracion, mails);
				} else {
					asunto = "(" + configuracion + ") – NO SELECCIONADOS";
					mensaje = getCuerpoEmailNoSeleccionados(
							evalReferencialPostulante.getPostulacion()
									.getPersonaPostulante(), concursoPuestoAgr,
							configuracion, mails);
				}
				seleccionUtilFormController.enviarMails(destinatario, mensaje,asunto);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return error;
	}

	private String getCuerpoEmailSeleccionados(
			PersonaPostulante personaPostulante,
			ConcursoPuestoAgr concursoPuestoAgr, String configuracionUo,
			String mails) {
		Concurso concurso = concursoPuestoAgr.getConcurso();
		String mensaje = "<p> <b> Estimado/a "
				+ personaPostulante.getNombres()
				+ " "
				+ personaPostulante.getApellidos()
				+ " </b></p> "
				+ "<p> Le comunicamos que ha sido seleccionado/a por la M&aacute;xima Autoridad Institucional para cubrir la vacancia del concurso: </p> "
				+ " <p><b> "
				+ concurso.getNumero()
				+ "/" +
				+ concurso.getAnhio()
				+ " de "
				+ concurso.getConfiguracionUoCab().getDescripcionCorta()
				+ " "
				+ concurso.getNombre()
				+ "</b></p> "
				+ " <p><b> "
				+ concursoPuestoAgr.getDescripcionGrupo()
				+ "</b></p> "
				+" Deber&aacute; presentar los siguientes documentos para continuar con la adjudicaci&oacute;n del puesto: "
				
				+ listaDocumentos(concursoPuestoAgr.getIdConcursoPuestoAgr()) 
				
				+ " <p><b> "
				+ (observacion.getObservacion() == null ? "" : observacion.getObservacion()) + " </b></p>" + " <p> </p>"
				+ " <p> </p>" + " <p> </p>"
				+ "<p align=\"justify\">&nbsp; </p>"
				+ "<strong>Atentamente,</strong>" + "<p><strong>"
				+ configuracionUo + "</strong></p>";

		if (!funcionarioUtilFormController.vacio(mails)) {
			mensaje += " <p> </p>"
					+ "Para mayor informaci&oacute;n comunicarse con: " + mails;
		}

		return mensaje;
	}
	private void publicacionPortal(String texto, Long idConcurso,
			Long idConcursoGrupoPuestoAgr) {
		PublicacionPortal entity = null;

		entity = new PublicacionPortal();
		entity.setActivo(true);
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setFechaAlta(new Date());
		Concurso c = new Concurso();
		c = em.find(Concurso.class, idConcurso);
		entity.setConcurso(new Concurso());
		entity.setConcurso(c);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		ConcursoPuestoAgr ag = new ConcursoPuestoAgr();
		ag = em.find(ConcursoPuestoAgr.class, idConcursoGrupoPuestoAgr);
		entity.setConcursoPuestoAgr(ag);
		entity.setObservacion(true);
		entity.setTexto(texto);
		em.persist(entity);
		em.flush();
	}
	
	private String genTextoPublicacionListaFinalTerna() {
		String texto = new String();
		String hr = "<hr>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h2O = "<h2>";
		String h2C = "</h2>";
		String spanO = "<span>";
		String spanC = "</span>";
		String br = "</br>";
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController.getConcursoPuestoAgr();
		
		if (concursoPuestoAgr.getIdConcursoPuestoAgr() != null)
			texto = texto
					+ hr
					+ fechaPublicacion + br
					+ spanO
					+ "Puede descargar aquí la: "
					+ spanC
					+ br
					+ spanO
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=imprimirListaFinalTerna&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "'>Lista Final Terna</a>"
					+ spanC;
					
		
		return texto;
	}

	private String getCuerpoEmailNoSeleccionados(
			PersonaPostulante personaPostulante,
			ConcursoPuestoAgr concursoPuestoAgr, String configuracionUo,
			String mails) {
		Concurso concurso = concursoPuestoAgr.getConcurso();
		String mensaje = "<p> <b> Estimado/a "
				+ personaPostulante.getNombres()
				+ " "
				+ personaPostulante.getApellidos()
				+ " </b></p> "
				+ "<p> Le comunicamos que NO HA SIDO SELECCIONADO, para el Concurso: </p> "
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
				+ "</b>  </p> "
				+

				" Al respecto le informamos que ha sido registrado/a en el Banco de Personas Elegibles del Concurso. Le agradecemos el inter&eacute;s que ha mostrado para este concurso, y deseamos poder contar con usted para nuestros pr&oacute;ximos concursos. "
				+ " <p> </p>" + " <p> </p>" + " <p> </p>"
				+ "<p align=\"justify\">&nbsp; </p>"
				+ "<strong>Atentamente,</strong>" + "<p><strong>"
				+ configuracionUo + "</strong></p>";

		if (!funcionarioUtilFormController.vacio(mails)) {
			mensaje += " <p> </p>"
					+ "Para mayor informaci&oacute;n comunicarse con: " + mails;
		}

		return mensaje;
	}
	
	@SuppressWarnings("unchecked")
	private String listaDocumentos(Long id) {
		String sql = "SELECT DE.* "
				+ "FROM seleccion.matriz_doc_config_det DET "
				+ "JOIN seleccion.datos_especificos DE "
				+ "ON DE.id_datos_especificos = DET.id_datos_especificos_tipo_docu "
				+ "JOIN seleccion.matriz_doc_config_enc ENC "
				+ "ON ENC.id_matriz_doc_config_enc = DET.id_matriz_doc_config_enc "
				+ "WHERE ENC.id_concurso_puesto_agr = "
				+ id
				+ " AND DET.adjudicacion = true " + "AND DET.activo = true";
		String resultado = "";
		List<DatosEspecificos> listaDatos = new ArrayList<DatosEspecificos>();
		listaDatos = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();
		for (DatosEspecificos dt : listaDatos) {
			resultado = resultado + "<p><b> - " + dt.getDescripcion() + "</b> </p> ";
		}
		return resultado;
	}

	
	
	
	public Observacion getObservacion() {
		return observacion;
	}

	public void setObservacion(Observacion observacion) {
		this.observacion = observacion;
	}
	
	public String getObservacionTerna() {
		return observacionTerna;
	}

	public void setObservacionTerna(String observacionTerna) {
		this.observacionTerna = observacionTerna;
	}

	public boolean isEntrevistaEnviada() {
		return entrevistaEnviada;
	}

	public void setEntrevistaEnviada(boolean entrevistaEnviada) {
		this.entrevistaEnviada = entrevistaEnviada;
	}
	
	public boolean isTernasEnviadas() {
		return ternasEnviadas;
	}

	public void setTernasEnviadas(boolean ternasEnviadas) {
		this.ternasEnviadas = ternasEnviadas;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public void setListaPostulantes(
			List<EvalReferencialPostulante> listaPostulantes) {
		this.listaPostulantes = listaPostulantes;
	}

	public List<EvalReferencialPostulante> getListaPostulantes() {
		return listaPostulantes;
	}

	public void setListaElegibles(List<EvalReferencialPostulante> listaElegibles) {
		this.listaElegibles = listaElegibles;
	}

	public List<EvalReferencialPostulante> getListaElegibles() {
		return listaElegibles;
	}

	public void setCantidadVacancias(Integer cantidadVacancias) {
		this.cantidadVacancias = cantidadVacancias;
	}

	public Integer getCantidadVacancias() {
		return cantidadVacancias;
	}

	public Boolean getIsDesierto() {
		return isDesierto;
	}

	public void setIsDesierto(Boolean isDesierto) {
		this.isDesierto = isDesierto;
	}

	public Boolean getSiguienteTarea() {
		return siguienteTarea;
	}

	public void setSiguienteTarea(Boolean siguienteTarea) {
		this.siguienteTarea = siguienteTarea;
	}
	
	/*Agregado para
	 * modificacion de ternas
	 * multiples*/
	
	public void setListaTernas(List<Ternas> listaTernas) {
		this.listaTernas = listaTernas;
	}

	public List<Ternas> getListaTernas() {
		return listaTernas;
	}
	
	public void cargarTernas() {
		
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController
				.getConcursoPuestoAgr();
		long idTaskInstance = jbpmUtilFormController
				.getIdTaskInstanceActual(concursoPuestoAgr
						.getIdProcessInstance());
		observacion = seleccionUtilFormController
				.cargarObservacion(idTaskInstance);

		if (observacion == null) {
			observacion = new Observacion();
			//entrevistaEnviada = false;
		} /*else {
			entrevistaEnviada = true;
		}*/
		ternasEnviadas = false;

		cantidadVacancias = getCantidadVacancias(concursoPuestoAgr);
		
		String sqlHayTernas = "select * from seleccion.ternas where ternas.id_concurso_puesto_agr="
				+ grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr() + " order by nro_terna";
		Query q = em.createNativeQuery(sqlHayTernas, Ternas.class);
		listaTernas = new ArrayList<Ternas>();
		if(q.getResultList().isEmpty()){
			for (int i=0; i<cantidadVacancias; i++) {
				//Ternas terna_aux = new Ternas(i+1, grupoPuestosController.getConcursoPuestoAgr());
				Ternas terna_aux = new Ternas();
				terna_aux.setNombre("Terna "+Integer.toString(i+1));
				terna_aux.setNroTerna(i+1);
				terna_aux.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
				em.persist(terna_aux);
				listaTernas.add(terna_aux);
			}
			em.flush();
		}else{
			listaTernas = q.getResultList();
		}
		
		//Carga de lista de Elegibles
		listaElegibles = new ArrayList<EvalReferencialPostulante>();
		String sqlPostulantes = "select * from seleccion.eval_referencial_postulante where eval_referencial_postulante.id_concurso_puesto_agr="
				+ grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr() + "and eval_referencial_postulante.lista_corta_replica=true"
				+ " and eval_referencial_postulante.seleccionado is null order by puntaje_realizado desc";
		Query q2 = em.createNativeQuery(sqlPostulantes, EvalReferencialPostulante.class);
		//listaPostulantesAux = evalReferencialPostulanteList.getListaCU148();
		listaElegibles = new ArrayList<EvalReferencialPostulante>();
		listaElegibles = q2.getResultList();
		/*for (EvalReferencialPostulante evalReferencialPostulante : listaPostulantesAux) {
			if (evalReferencialPostulante.getSeleccionado() == null
					|| !evalReferencialPostulante.getSeleccionado()) {
				listaElegibles.add(evalReferencialPostulante);
			}
		}*/
		
		cantidadVacancias = getCantidadVacancias(concursoPuestoAgr);

		Calendar c = Calendar.getInstance();
		ubicacionFisica = "\\SICCA\\"
				+ c.get(Calendar.YEAR)
				+ "\\OEE\\"
				+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab()
						.getIdConfiguracionUo()
				+ "\\"
				+ concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + "\\"
				+ concursoPuestoAgr.getConcurso().getIdConcurso() + "\\"
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}
	
	public String gestionarTerna(Long idTerna){
		this.idTernaActual = idTerna;
		return redirectEntrevista();
	}
	
	public String redirectEntrevista() {
		return "/seleccion/entrevistaFinal/RealizarEntrevistaFinal.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
	}
	
	public String redirectTernas() {
		return "/seleccion/entrevistaFinal/TernasEntrevistaFinal.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
	}
	
	public List<EvalReferencialPostulante> getListaTernados(){
		List<EvalReferencialPostulante> ternados =  new ArrayList<EvalReferencialPostulante>();
		if(!ternaEntrevistada(idTernaActual)){
			String sqlTernados = "select * from seleccion.eval_referencial_postulante where eval_referencial_postulante.id_concurso_puesto_agr="
								+ grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr() + "and eval_referencial_postulante.lista_corta_replica=true"
								+ " and eval_referencial_postulante.seleccionado_replica is null "
								+ " order by puntaje_realizado desc";
			Query q = em.createNativeQuery(sqlTernados, EvalReferencialPostulante.class);
			
			if(q.getResultList().size() > 2){
				ternados = q.getResultList().subList(0, 3);
			}
			else{
				ternados = q.getResultList().subList(0, 2);
			}
		}
		else{
			ternados.add(ternaActual.getSeleccionado());
			if(ternaActual.getElegible_1() != null){
				ternados.add(ternaActual.getElegible_1());
			}
			if(ternaActual.getElegible_2() != null){
				ternados.add(ternaActual.getElegible_2());
			}
		}
		return ternados;
	}
	
	public boolean seleccionadoTernaActual(Long id_seleccionado){
		if(entrevistaEnviada){
			Long aux = ternaActual.getSeleccionado().getIdEvalReferencialPostulante();
			if(aux.longValue() == id_seleccionado.longValue()){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public boolean ternaEntrevistada(Long idTerna){
		String sqlEntrevistadas = "select * from seleccion.ternas where ternas.id_terna=" +idTerna+" and ternas.id_eval_ref_post_seleccionado is not null";
		Query q = em.createNativeQuery(sqlEntrevistadas, Ternas.class);
		if(!q.getResultList().isEmpty()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean sobranTernasSinEntrevistar(){
		String sqlEntrevistadas = "select * from seleccion.ternas where ternas.id_eval_ref_post_seleccionado is null"
				+ " and ternas.id_concurso_puesto_agr = " + grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
		Query q = em.createNativeQuery(sqlEntrevistadas, Ternas.class);
		if(!q.getResultList().isEmpty()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Long getIdTernaActual() {
		return idTernaActual;
	}

	public void setIdTernaActual(Long idTerna) {
		this.idTernaActual = idTerna;
	}
	
	public Long getIdEvalRefPostActual() {
		return idEvalRefPostActual;
	}

	public void setIdEvalRefPostActual(Long idEvalRefPostActual) {
		this.idEvalRefPostActual = idEvalRefPostActual;
	}
	
	public GrupoPuestosController getGrupoPuestosController() {
		return grupoPuestosController;
	}

	public void setGrupoPuestosController(
			GrupoPuestosController grupoPuestosController) {
		this.grupoPuestosController = grupoPuestosController;
	}

}
