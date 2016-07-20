package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.management.Query;
import javax.persistence.EntityManager;

import org.hibernate.mapping.Array;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("firmarResolucionHomologacionFormController")
public class FirmarResolucionHomologacionFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
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
	ConcursoPuestoAgrList concursoPuestoAgrList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	// @In(create = true)
	// HomologacionPerfilMatrizDetList homologacionPerfilMatrizDetList;

	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idConcurso;// enviado por el CU
	private Concurso concurso;
	private String obs;
	private String nombrePantalla;
	private String direccionFisica;
	private String entity;
	private Long idConcursoPuestoAgr;
	private String fromActividad;// 21-FIRMAR_RESOLUCION_ADJUDICACION
									// 7-FIRMA_RESOL_HOMOLOG
									// 26-FIRMAR_RESOLUCION_NOMBRAMIENTO
	private boolean habNombramiento = false;
	private List<ConcursoPuestoAgr> concursoPuestoAgrs = new ArrayList<ConcursoPuestoAgr>();
	private int nivel;
	private Boolean habilitarBtnResolucion;
	private String lugar;
	private Integer nroResolucion;
	private Date fecha;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean existeResolucion;
	private Boolean hayObs = false;

	public List<ConcursoPuestoAgr> listaGruposParaFirmar;
	public List<ConcursoPuestoAgr> listaGruposAtrasados;

	public void hayObs() {
		Integer count = 0;
		for (ConcursoPuestoAgr cpagr : listaGruposParaFirmar)
			count += contar(cpagr.getIdConcursoPuestoAgr());

		hayObs = (count > 0) ? true : false;
	}

	private Integer contar(Long id) {
		String s = "select count(*) from seleccion.homologacion_perfil_matriz_det "
				+ "where firma_resolucion is true and id_homologacion= "
				+ "(select id_homologacion from seleccion.homologacion_perfil_matriz "
				+ "where respuesta is null and id_concurso_puesto_agr="
				+ id
				+ ")";
		BigInteger count = (BigInteger) em.createNativeQuery(s).getResultList()
				.get(0);
		return count.intValue();
	}

	public void init() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}

		habilitarBtnResolucion = false;

		String z = "select * from seleccion.concurso_puesto_agr c where c.id_concurso_puesto_agr="
				+ idConcursoPuestoAgr;
		concursoPuestoAgr = (ConcursoPuestoAgr) em
				.createNativeQuery(z, ConcursoPuestoAgr.class).getResultList()
				.get(0);
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = concursoPuestoAgr.getConcurso();
		findEntidades();
		searchAll();
		hayObs();

		if (sinNivelEntidad.getNenCodigo() != null)
			nivel = sinNivelEntidad.getNenCodigo().intValue();

		if (fromActividad.equals("7")) {
			validarOee(
					seguridadUtilFormController.estadoActividades(
							"ESTADOS_GRUPO", "CON DOCUMENTOS DE HOMOLOGACION")
							+ "", ActividadEnum.FIRMA_RESOL_HOMOLOG.getValor());
			habilitarBtnResolucion = true;
		}
		// if (fromActividad.equals("26"))
		// validarOee(
		// seguridadUtilFormController.estadoActividades(
		// "ESTADOS_GRUPO", "CON RESOLUCION NOMBRAMIENTO")
		// + "",
		// ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor());
		/**
		 * SE AGREGA PARA LA INCIDENCIA 0001060
		 ***/

		if (fromActividad.equals("21")) {

			if (nivel != 12)
				habNombramiento = true;
			else
				habNombramiento = false;
		}

		// BUSCAR SI YA HAY UNA RESOLUCION PARA EL CONCURSO
		existeResolucion = buscarResolucion();
	}
	
	

	public Boolean buscarResolucion() {
		String nombrePantalla="";
		
		if (fromActividad.equals("7")) {
			nombrePantalla = "FirmaResolHomolog";
		}
		if (fromActividad.equals("21")) {
			nombrePantalla = "FirmaResolAdjudic";
		}
		
		
		String q = "select * from general.documentos where nombre_tabla like 'Resolucion' and nombre_pantalla like '"+nombrePantalla+"' and id_concurso = "
				+ idConcurso;
		List<?> resulList = em.createNativeQuery(q).getResultList();
		return (resulList.size() > 0) ? true : false;

	}

	public Boolean ok() {
		if (fromActividad.equals("7") || fromActividad.equals("21"))
			return (listaGruposAtrasados.size() != 0 || !existeResolucion) ? true
					: false;
		else
			return false;
	}

	public String siguienteTarea() {
		return hayObs ? nextTask2() : nextTask();

	}
	
	@SuppressWarnings("unchecked")
	public void ListarConcursoPorEstado() {

		String query = "select * from seleccion.concurso_puesto_agr cpa "
				+ "where cpa.id_concurso = " + idConcurso
				+ " and cpa.activo is true and cpa.estado = "
				+ concursoPuestoAgr.getEstado();

		listaGruposParaFirmar = new ArrayList<ConcursoPuestoAgr>();
		listaGruposParaFirmar = em.createNativeQuery(query,
				ConcursoPuestoAgr.class).getResultList();

		// listaGruposParaFirmar =
		// concursoPuestoAgrList.listaResultadosCU414(q);
		String query2 = "select * from seleccion.concurso_puesto_agr cpa "
				+ "where cpa.id_concurso = " + idConcurso
				+ " and cpa.activo is true and cpa.estado != "
				+ concursoPuestoAgr.getEstado();

		listaGruposAtrasados = new ArrayList<ConcursoPuestoAgr>();
		listaGruposAtrasados = em.createNativeQuery(query2,
				ConcursoPuestoAgr.class).getResultList();

	}

	private void validarOee(String estados, String actividad) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOee(null,
				concursoPuestoAgr.getIdConcursoPuestoAgr(), estados, actividad);
	}

	public void imprimirPerfilMatriz(int indice) {
		Long idConcursoAgr = listaGruposParaFirmar.get(indice)
				.getIdConcursoPuestoAgr();
		reportUtilFormController
				.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr",
				idConcursoAgr);
		reportUtilFormController.imprimirReportePdf();
	}

	public void searchAll() {
		String query = "";
		if (fromActividad == null)
			fromActividad = "7";

		if (fromActividad.equals("21")) {
			// query = getQuery(resolucionAdjudicacion());//MODIFICADO RV
			// concursoPuestoAgrs = concursoPuestoAgrList
			// .listaResultadosCU414(query);
			ListarConcursoPorEstado();
		}
		if (fromActividad.equals("") || fromActividad.equals("7")) {
			// query = getQuery(documentosHomologacion());

			ListarConcursoPorEstado();
		}
		if (fromActividad.equals("26")) {
			query = getQuery(resolucionNombramiento());
			concursoPuestoAgrs = concursoPuestoAgrList
					.listaResultadosCU414(query);
		}

	}

	public String getQuery(Integer ref) {

		String query = "select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
				+ "join concursoPuestoAgr.concurso concurso "
				+ "where concurso.idConcurso = "
				+ idConcurso
				+ " and concursoPuestoAgr.activo is true "
				+ "and concursoPuestoAgr.estado = " + ref.intValue();
		return query;
	}

	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso
				.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class, configuracionUoCab
					.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin = em.createQuery(
					"Select n from SinNivelEntidad n "
							+ " where n.aniAniopre ="
							+ sinEntidad.getAniAniopre() + " and n.nenCodigo="
							+ sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}

	private String genTextoPublicacion() {
		String h1O = "<h1>";
		String h1C = "</h1>";
		String br = "</br>";

		StringBuffer texto = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		texto.append(br + h1O + "Se informa que en fecha "
				+ sdf.format(new Date()) + " el concurso se ha cerrado." + h1C
				+ br);

		return texto.toString();
	}

	private void publicacionPortal(String texto, Long idConcurso,
			Long idConcursoGrupoPuestoAgr) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setActivo(true);
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setTexto(texto);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				idConcursoGrupoPuestoAgr);
		em.persist(entity);
		em.flush();
	}

	// AGREGADO JD
	public String nextTask2() {

		for (int i = 0; i < listaGruposParaFirmar.size(); i++) {

			ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
					listaGruposParaFirmar.get(i).getIdConcursoPuestoAgr());

			if (contar(agr.getIdConcursoPuestoAgr()) > 0) {

				/**
				 * FIRMA RESOLUCION HOMOLOGACION DEBE PASAR A LA SIGUIENTE
				 * ACTIVIDAD POR CONCURSO PERO PARA IR A LA ANTERIOR DEBE IR POR
				 * GRUPO. SI SE UTILIZA jbpmUtilFormController.nextTask();
				 * ENCONTRARA QUE ES POR CONCURSO Y PASARA AMBOS A LA ACTIVIDAD
				 * APROBAR HOMOLOGACION SFP, SIN EMBARGO SOLO DEBE PASAR LOS
				 * GRUPOS QUE CONTENGAN OBSERVACIONES MAI. POR ESTE HECHO SE
				 * UTILIZA jbpmUtilFormController.nextTaskGroup(agr); POR CADA
				 * GRUPO CON OBSERVACIONES DEBIDO A QUE ESTA ACTIVIDAD ES MIXTA
				 * (CONCURSO/GRUPO)
				 * **/

				jbpmUtilFormController.setTransition("next2");
				if (jbpmUtilFormController.nextTaskGroup(agr)) {
					em.flush();
					statusMessages
							.add("Observación enviada a APROBAR HOMOLOGACION SFP");

				}
				// Se actualiza el estado
				Referencias referencias = referenciasUtilFormController
						.getReferencia("ESTADOS_GRUPO", "FINALIZADO CARGA");

				agr.setEstado(referencias.getValorNum());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);
				em.flush();

				// Se actualiza los estados de las plantas
				EstadoDet estadoDet = seleccionUtilFormController
						.buscarEstadoDet("CONCURSO", "FINALIZADO CARGA");
				List<ConcursoPuestoDet> lista = concursoPuestoAgr
						.getConcursoPuestoDets();
				if (lista != null && lista.size() > 0) {
					for (ConcursoPuestoDet concursoPuestoDet : lista) {
						PlantaCargoDet plantaCargoDet = concursoPuestoDet
								.getPlantaCargoDet();
						plantaCargoDet.setEstadoDet(estadoDet);
						em.merge(plantaCargoDet);

						concursoPuestoDet.setEstadoDet(estadoDet);
						em.merge(concursoPuestoDet);
					}
				}

			}

		}

		return "persisted";
	}
public boolean tareaCerrada(String estado){	
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", estado);
		ConcursoPuestoAgr aux = em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr);
		em.refresh(aux);
		
		System.out.println("Tarea: ");
		System.out.println(ref.getValorNum());
		System.out.println(aux.getEstado());
		
		return (aux.getEstado() >= (ref.getValorNum()))? true : false;
		
	}

	@SuppressWarnings("unchecked")
	public String nextTask() {

		boolean hayDoc = true;
		statusMessages.clear();
		Long idCU = null;
		for (int i = 0; i < concursoPuestoAgrs.size(); i++) {
			ConcursoPuestoAgr grupo = concursoPuestoAgrs.get(i);
			if (fromActividad.equals("7")) {
				nombrePantalla = "FirmaResolHomolog";
				if (grupo.getResolucionHomologacion() == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe ingresar Nro. de Resolucion/Fecha y Lugar para pasar a la siguiente tarea.");
					return null;
				}
				idCU = grupo.getResolucionHomologacion().getIdResolucion();
			}
			if (fromActividad.equals("21")) {
				nombrePantalla = "FirmaResolAdjudic";
				idCU = grupo.getResolucionAdjudicacion().getIdResolucion();
			}
			if (fromActividad.equals("26")) {
				nombrePantalla = "FirmaResolNombram";
				idCU = grupo.getResolucionNombramiento().getIdResolucion();
			}
			List<Documentos> d = em
					.createQuery(
							"Select d from Documentos d "
									+ "where lower(d.nombrePantalla) like '"
									+ nombrePantalla.toLowerCase()
									+ "' and d.activo=true and d.nombreTabla like 'Resolucion' and d.idTabla="
									+ idCU).getResultList();
			if (d.isEmpty()) {
				statusMessages.add(Severity.ERROR,
						"Debe Anexar la Resolución para el grupo: "
								+ concursoPuestoAgrs.get(i)
										.getDescripcionGrupo());
				hayDoc = false;

			}
		}
		if (!hayDoc) {
			return null;
		}
		statusMessages.clear();

		Calendar calCalendario = Calendar.getInstance();

		/**
		 * Si fue llamado desde “Firmar resolución de Homologación SFP”
		 * */
		if (fromActividad.equals("7")) {
			if(tareaCerrada("FIRMADO HOMOLOGACION")) return null;
			
			for (int i = 0; i < listaGruposParaFirmar.size(); i++) {
				ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
						listaGruposParaFirmar.get(i).getIdConcursoPuestoAgr());
				/**
				 * Deberá actualizar el estado de cada Grupo de puestos que
				 * figura en la grilla a FIRMADO HOMOLOGACION
				 * **/

				agr.setEstado(firmadoHomologacion());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);
				/**
				 * Deberá actualizar el estado de los Puestos de cada grupo:
				 * */

				List<ConcursoPuestoDet> d = em
						.createQuery(
								"Select c from ConcursoPuestoDet c"
										+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
										+ listaGruposParaFirmar.get(i)
												.getIdConcursoPuestoAgr()
										+ " and c.estadoDet.idEstadoDet="
										+ conDocumentoHomologacion())
						.getResultList();
				for (int j = 0; j < d.size(); j++) {
					ConcursoPuestoDet puestoDet = em.find(
							ConcursoPuestoDet.class, d.get(j)
									.getIdConcursoPuestoDet());
					puestoDet.setEstadoDet(estadoFirmadoHomologacion());
					puestoDet.setFechaMod(new Date());
					puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(puestoDet);

					if (d.get(j).getPlantaCargoDet() != null) {
						PlantaCargoDet pdet = em.find(PlantaCargoDet.class, d
								.get(j).getPlantaCargoDet()
								.getIdPlantaCargoDet());
						pdet.setEstadoDet(estadoFirmadoHomologacion());
						pdet.setFechaMod(new Date());
						pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(pdet);

					}
				}
				/**
				 * Deberá actualizar el estado de la Homologación de cada Grupo
				 * */

				List<HomologacionPerfilMatriz> matrizs = em
						.createQuery(
								"Select c from HomologacionPerfilMatriz c "
										+ " where c.concursoPuestoAgr.idConcursoPuestoAgr="
										+ agr.getIdConcursoPuestoAgr())
						.getResultList();

				for (int j = 0; j < matrizs.size(); j++) {
					HomologacionPerfilMatriz h = em.find(
							HomologacionPerfilMatriz.class, matrizs.get(j)
									.getIdHomologacion());
					if (h.getFechaHomolog() != null) {
						calCalendario.setTimeInMillis(h.getFechaHomolog()
								.getTime());
						// se suma un año a la fecha homologacion
						calCalendario.add(Calendar.YEAR, 1);
						h.setFechaLimite(calCalendario.getTime());
						h.setFechaVencimiento(calCalendario.getTime());
					}
					h.setEstado("HOMOLOGADO");
					h.setUsuHomolog(usuarioLogueado.getCodigoUsuario());
					h.setFechaHomolog(new Date());
					em.merge(h);

				}
				Observacion observacion = new Observacion();
				observacion.setObservacion(obs);
				observacion.setFechaAlta(new Date());
				observacion.setConcurso(em.find(Concurso.class, idConcurso));
				observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				observacion.setIdTaskInstance(jbpmUtilFormController
						.getIdTaskInstanceActual(agr.getIdProcessInstance()));
				em.persist(observacion);
				// em.flush();

			}

			jbpmUtilFormController
					.setActividadActual(ActividadEnum.FIRMA_RESOL_HOMOLOG);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.MODIFICAR_DATOS_CONCURSO);
			jbpmUtilFormController.setTransition("next");
			jbpmUtilFormController.setConcursoPuestoAgr(listaGruposParaFirmar
					.get(0));
			if (jbpmUtilFormController.nextTask()) {
				em.flush();

			}

		}
		/**
		 * Si fue llamado desde “Firmar Resolución de Adjudicación OEE por
		 * Concurso”
		 * 
		 * */
		if (fromActividad.equals("21")) {
			// PARA TODOS LOS GRUPOS SE GUARDA LA FECHA DE ADJUDICACION
			/**
			 * o Actualizar el campo FECHA_ADJUDICACION = fecha del sistema de
			 * cada grupo de puestos visualizados en la grilla
			 * */
			concursoPuestoAgrs = listaGruposParaFirmar;
			for (int i = 0; i < concursoPuestoAgrs.size(); i++) {
				ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
						concursoPuestoAgrs.get(i).getIdConcursoPuestoAgr());
				agr.setFechaAdjudicacion(new Date());
				if (agr.getNombramiento() == null || !agr.getNombramiento())
					agr.setNombramiento(false);
				else
					agr.setNombramiento(true);

				em.merge(agr);

			}

			for (int j = 0; j < concursoPuestoAgrs.size(); j++) {
				ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
						concursoPuestoAgrs.get(j).getIdConcursoPuestoAgr());
				// Se pasa a la siguiente tarea de acuerdo al nombramiento
				// seleccionado
				// Si el puesto (el primer puesto activo del grupo) es =
				List<ConcursoPuestoDet> dets = em
						.createQuery(
								"Select d from ConcursoPuestoDet d where d.activo=true"
										+ " and d.concursoPuestoAgr.idConcursoPuestoAgr="
										+ agr.getIdConcursoPuestoAgr())
						.getResultList();

				if (!dets.isEmpty()) {
					if (dets.get(0).getPlantaCargoDet().getPermanente()
							&& nivel == 12) {
						jbpmUtilFormController
								.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
						jbpmUtilFormController
								.setActividadSiguiente(ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL);
						jbpmUtilFormController.setTransition("next1");
					} else if (dets.get(0).getPlantaCargoDet().getContratado()
							|| (dets.get(0).getPlantaCargoDet().getPermanente() && nivel != 12)) {
						jbpmUtilFormController
								.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
						jbpmUtilFormController
								.setActividadSiguiente(ActividadEnum.INGRESAR_POSTULANTE);
						jbpmUtilFormController.setTransition("next2");
					}

					/**
					 * SI ES PERMANENTE
					 * */
//					if (dets.get(0).getPlantaCargoDet().getPermanente()) {
//
//						/**
//						 * SI el tipo de concurso es = ‘CONCURSO PUBLICO DE
//						 * OPOSICION’ SI el puesto es PERMANENTE SI el nivel es
//						 * 12
//						 * */
//						if (esConcursoPubOposicion(agr.getConcurso()
//								.getDatosEspecificosTipoConc())) {
//							if (nivel == 12) {
//								jbpmUtilFormController
//										.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//								jbpmUtilFormController
//										.setActividadSiguiente(ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL);
//								jbpmUtilFormController.setTransition("next1");
//							} else {
//								/**
//								 * SI el nivel es <> 12 es también la
//								 * ‘Resolución de Nombramiento’
//								 */
//								if (agr.getNombramiento()) {
//									jbpmUtilFormController
//											.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//									jbpmUtilFormController
//											.setActividadSiguiente(ActividadEnum.INGRESAR_POSTULANTE);
//									jbpmUtilFormController
//											.setTransition("next3");
//								} else {
//									/**
//									 * SINO La “Siguiente Tarea” es “ELABORAR
//									 * RESOLUCION DE NOMBRAMIENTO”
//									 */
//									jbpmUtilFormController
//											.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//									jbpmUtilFormController
//											.setActividadSiguiente(ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO);
//									jbpmUtilFormController
//											.setTransition("next2");
//								}
//							}
//						} else if (esConcursoMeritos(agr.getConcurso()
//								.getDatosEspecificosTipoConc())) {
//							/**
//							 * SINO SI el tipo de concurso es = ‘CONCURSO DE
//							 * MERITOS’
//							 */
//							/**
//							 * La “Siguiente Tarea” es la actividad “INGRESAR
//							 * POSTULANTE” (CU420)
//							 * **/
//							jbpmUtilFormController
//									.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//							jbpmUtilFormController
//									.setActividadSiguiente(ActividadEnum.INGRESAR_POSTULANTE);
//							jbpmUtilFormController.setTransition("next3");
//						} else {
//							/**
//							 * PARA CUALQUIER OTRO CASO Si el nivel <> 12
//							 * entonces, controlar los grupos de puestos
//							 * chequeados por la columna “Nombramiento”
//							 * */
//							if (nivel != 12) {
//								if (!agr.getNombramiento()) {
//									jbpmUtilFormController
//											.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//									jbpmUtilFormController
//											.setActividadSiguiente(ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO);
//									jbpmUtilFormController
//											.setTransition("next2");
//								} else {
//									jbpmUtilFormController
//											.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//									jbpmUtilFormController
//											.setActividadSiguiente(ActividadEnum.INGRESAR_POSTULANTE);
//									jbpmUtilFormController
//											.setTransition("next3");
//								}
//
//							} else if (nivel == 12) {
//								jbpmUtilFormController
//										.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//								jbpmUtilFormController
//										.setActividadSiguiente(ActividadEnum.ELABORAR_DECRECTO_PRESIDENCIAL);
//								jbpmUtilFormController.setTransition("next1");
//
//							}
//						}
//
//					}
//					if (dets.get(0).getPlantaCargoDet().getContratado()) {
//						jbpmUtilFormController
//								.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_ADJUDICACION);
//						jbpmUtilFormController
//								.setActividadSiguiente(ActividadEnum.INGRESAR_POSTULANTE);
//						jbpmUtilFormController.setTransition("next3");
//					}

					// se pasa los estados de los grupos de acuerdo a la tabla
					// ConcursoPuestoDet
					/**
					 * Deberá actualizar el estado de cada Grupo de puestos que
					 * figura en la grilla. Tabla SEL_CONCURSO_PUESTO_AGR
					 * */
					if (dets.get(0).getPlantaCargoDet().getContratado())
						agr.setEstado(contratado());
					else if (dets.get(0).getPlantaCargoDet().getPermanente()
							&& nivel != 12) {
						agr.setEstado(permanenteD12());
					} else if (dets.get(0).getPlantaCargoDet().getPermanente()
							&& nivel == 12)
						agr.setEstado(permanenteN12());

				}
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);

				/**
				 * Deberá actualizar el estado de los Puestos de cada grupo:
				 * **/

				List<ConcursoPuestoDet> detsPuesto = em
						.createQuery(
								"Select d from ConcursoPuestoDet d where d.activo=true"
										+ " and d.concursoPuestoAgr.idConcursoPuestoAgr="
										+ agr.getIdConcursoPuestoAgr()
										+ " and d.estadoDet.idEstadoDet="
										+ conResolucionAdjundicacion())
						.getResultList();

				for (int i = 0; i < detsPuesto.size(); i++) {
					ConcursoPuestoDet puestoDet = em.find(
							ConcursoPuestoDet.class, detsPuesto.get(i)
									.getIdConcursoPuestoDet());
					PlantaCargoDet pdet = null;
					if (puestoDet.getPlantaCargoDet() != null)
						pdet = em.find(PlantaCargoDet.class, puestoDet
								.getPlantaCargoDet().getIdPlantaCargoDet());

					if (puestoDet.getPlantaCargoDet().getContratado()) {
						puestoDet.setEstadoDet(estadoContratado());
						if (pdet != null)
							pdet.setEstadoDet(estadoContratado());
					} else if (puestoDet.getPlantaCargoDet().getPermanente()
							&& nivel != 12) {
						puestoDet.setEstadoDet(estadoPermanenteD12());
						if (pdet != null)
							pdet.setEstadoDet(estadoPermanenteD12());
					} else if (puestoDet.getPlantaCargoDet().getPermanente()
							&& nivel == 12) {
						puestoDet.setEstadoDet(estadoPermanenteN12());
						if (pdet != null)
							pdet.setEstadoDet(estadoPermanenteN12());
					}

					puestoDet.setFechaMod(new Date());
					puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(puestoDet);
					if (pdet != null) {
						pdet.setFechaMod(new Date());
						pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(pdet);
					}

				}
				jbpmUtilFormController.setActividadPorGrupo(true);
				// Se pasa a la siguiente tarea
				jbpmUtilFormController.setConcursoPuestoAgr(agr);
				jbpmUtilFormController.nextTask();

			}

			// publicacionPortal(genTextoPublicacion(),idConcurso ,
			// idConcursoPuestoAgr);
			em.flush();

		}
		/**
		 * Si fue llamado desde “Firmar Resolución de Nombramiento”
		 * */

		if (fromActividad.equals("26")) {
			for (int i = 0; i < concursoPuestoAgrs.size(); i++) {
				ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
						concursoPuestoAgrs.get(i).getIdConcursoPuestoAgr());
				agr.setEstado(firmadoNombramiento());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);

				Observacion observacion = new Observacion();
				observacion.setObservacion(obs);
				observacion.setFechaAlta(new Date());
				observacion.setConcurso(em.find(Concurso.class, idConcurso));
				observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				observacion.setIdTaskInstance(jbpmUtilFormController
						.getIdTaskInstanceActual(agr.getIdProcessInstance()));
				em.persist(observacion);

				/**
				 * Deberá actualizar el estado de los Puestos de cada grupo:
				 * */

				List<ConcursoPuestoDet> d = em
						.createQuery(
								"Select c from ConcursoPuestoDet c"
										+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
										+ concursoPuestoAgrs.get(i)
												.getIdConcursoPuestoAgr()
										+ " and c.estadoDet.idEstadoDet="
										+ conResolucionNombramiento())
						.getResultList();
				for (int j = 0; j < d.size(); j++) {
					ConcursoPuestoDet puestoDet = em.find(
							ConcursoPuestoDet.class, d.get(j)
									.getIdConcursoPuestoDet());
					puestoDet.setEstadoDet(estadoFirmadoNombramiento());
					puestoDet.setFechaMod(new Date());
					puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(puestoDet);

					if (d.get(j).getPlantaCargoDet() != null) {
						PlantaCargoDet pdet = em.find(PlantaCargoDet.class, d
								.get(j).getPlantaCargoDet()
								.getIdPlantaCargoDet());
						pdet.setEstadoDet(estadoFirmadoNombramiento());
						pdet.setFechaMod(new Date());
						pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(pdet);

					}
				}

			}

			// Se pasa a la siguiente tarea
//			jbpmUtilFormController
//					.setActividadActual(ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO);
//			jbpmUtilFormController
//					.setActividadSiguiente(ActividadEnum.ELABORAR_RESOLUCION_NOMBRAMIENTO);
			jbpmUtilFormController.setTransition("next");
			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgrs
					.get(0));
			if (jbpmUtilFormController.nextTask()) {
				em.flush();

			}

		 }

		return "next";

	}

	@SuppressWarnings("unchecked")
	public boolean esPermanente(Long idGrupo) {
		List<ConcursoPuestoDet> dets = em
				.createQuery(
						"Select d from ConcursoPuestoDet d where d.activo=true"
								+ " and d.concursoPuestoAgr.idConcursoPuestoAgr=:id")
				.setParameter("id", idGrupo).getResultList();
		if (!dets.isEmpty() && dets.get(0).getPlantaCargoDet().getPermanente())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean esContratado(Long idGrupo) {
		List<ConcursoPuestoDet> dets = em
				.createQuery(
						"Select d from ConcursoPuestoDet d where d.activo=true"
								+ " and d.concursoPuestoAgr.idConcursoPuestoAgr=:id")
				.setParameter("id", idGrupo).getResultList();
		if (!dets.isEmpty() && dets.get(0).getPlantaCargoDet().getContratado())
			return true;

		return false;
	}

	public String mostrarEstado(int key) {
		String estado = "";
		switch (key) {
		case 2:
			estado = "CARGA POR GRUPO";
			break;
		case 3:
			estado = "APROBAR HOMOLOGACION SFP";
			break;
		case 6:
			estado = "HOMOLOGACION OEE";
			break;
		default:

		}
		return estado;
	}

	public String back() {
		statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "back";
	}

	public String anexar(Long idConcPuestoAgr) {
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class,
				idConcPuestoAgr);
		String url = "/seleccion/adminDocAdjunto/AdmDocAdjunto.seam";
		String nombrePantalla = "";
		String idCU = "";
		String nombreTabla = "Resolucion";
		String mostrarDoc = "true";
		String isEdit = "true";
		String from = "seleccion/firmarResolucionHomologacion/FirmarResolucionHomologacion";

		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		direccionFisica = "//SICCA//"
				+ anio
				+ "//OEE//"
				+ configuracionUoCab.getIdConfiguracionUo()
				+ "//"
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + "//" + idConcurso;
		String pagina = url + "?mostrarDoc=" + mostrarDoc + "&isEdit=" + isEdit
				+ "&from=" + from + "&nombreTabla=" + nombreTabla
				+ "&direccionFisica=" + direccionFisica;

		if (fromActividad.equals("7")) {
			nombrePantalla = "FirmaResolHomolog";
			// idCU = grupo.getResolucionHomologacion().getIdResolucion() + "";

		}
		if (fromActividad.equals("21")) {
			nombrePantalla = "FirmaResolAdjudic";
			//idCU = grupo.getResolucionAdjudicacion().getIdResolucion() + "";
		}
		if (fromActividad.equals("26")) {
			nombrePantalla = "FirmaResolNombram";
			idCU = grupo.getResolucionNombramiento().getIdResolucion() + "";
		}
		// pagina += "&nombrePantalla=" + nombrePantalla + "&idCU=" + idCU;
		pagina += "&nombrePantalla=" + nombrePantalla + "&idConcurso="
				+ idConcurso;

		return pagina;
	}

	private Boolean esConcursoPubOposicion(DatosEspecificos tipo) {
		if (tipo.getDescripcion().toUpperCase().trim()
				.equals("CONCURSO PUBLICO DE OPOSICION"))
			return true;

		return false;
	}

	private Boolean esConcursoMeritos(DatosEspecificos tipo) {
		if (tipo.getDescripcion().toUpperCase().trim()
				.equals("CONCURSO DE MERITOS"))
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	private Integer resolucionAdjudicacion() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CON RESOLUCION ADJUDICACION'")
				.getResultList();
		if (ref.isEmpty())
			return new Integer(0);
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer documentosHomologacion() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CON DOCUMENTOS DE HOMOLOGACION'")
				.getResultList();
		if (ref.isEmpty())
			return new Integer(0);
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer resolucionNombramiento() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CON RESOLUCION NOMBRAMIENTO'")
				.getResultList();
		if (ref.isEmpty())
			return new Integer(0);
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer firmadoHomologacion() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'FIRMADO HOMOLOGACION'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer contratado() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CONTRATADO'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer permanenteD12() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'PERMANENTE D12'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer permanenteN12() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'PERMANENTE N12'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer finalizado() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'FINALIZADO'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer firmadoNombramiento() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'FIRMADO NOMBRAMIENTO'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoFirmadoHomologacion() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'FIRMADO HOMOLOGACION' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private Long conDocumentoHomologacion() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CON DOCUMENTOS DE HOMOLOGACION' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0).getIdEstadoDet();
	}

	@SuppressWarnings("unchecked")
	private Long conResolucionAdjundicacion() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CON RESOLUCION ADJUDICACION' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0).getIdEstadoDet();
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoContratado() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CONTRATADO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoPermanenteD12() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'PERMANENTE D12' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoPermanenteN12() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'PERMANENTE N12' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private Long conResolucionNombramiento() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CON RESOLUCION NOMBRAMIENTO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0).getIdEstadoDet();
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoFirmadoNombramiento() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'FIRMADO NOMBRAMIENTO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	public void actualizarResolucion() {
		if (fecha != null
				|| (nroResolucion != null && nroResolucion.intValue() > 0)
				|| (lugar != null && !lugar.trim().isEmpty())) {
			Resolucion resolucionActual = concursoPuestoAgr
					.getResolucionHomologacion();
			if (resolucionActual != null) {
				if (fecha != null)
					resolucionActual.setFecha(fecha);
				if (nroResolucion != null)
					resolucionActual.setNroResolucion(nroResolucion);
				if (lugar != null && !lugar.trim().isEmpty())
					resolucionActual.setLugar(lugar);
				em.merge(resolucionActual);
				em.flush();
			}
		}
	}

	public void prepararModal() {
		fecha = null;
		nroResolucion = null;
		lugar = null;
	}

	// GETTERS Y SETTERS

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public ConcursoPuestoAgrList getConcursoPuestoAgrList() {
		return concursoPuestoAgrList;
	}

	public void setConcursoPuestoAgrList(
			ConcursoPuestoAgrList concursoPuestoAgrList) {
		this.concursoPuestoAgrList = concursoPuestoAgrList;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getFromActividad() {
		return fromActividad;
	}

	public void setFromActividad(String fromActividad) {
		this.fromActividad = fromActividad;
	}

	public boolean isHabNombramiento() {
		return habNombramiento;
	}

	public void setHabNombramiento(boolean habNombramiento) {
		this.habNombramiento = habNombramiento;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public Boolean getHabilitarBtnResolucion() {
		return habilitarBtnResolucion;
	}

	public void setHabilitarBtnResolucion(Boolean habilitarBtnResolucion) {
		this.habilitarBtnResolucion = habilitarBtnResolucion;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public Integer getNroResolucion() {
		return nroResolucion;
	}

	public void setNroResolucion(Integer nroResolucion) {
		this.nroResolucion = nroResolucion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public void setListaGruposParaFirmar(
			List<ConcursoPuestoAgr> listaGruposParaFirmar) {
		this.listaGruposParaFirmar = listaGruposParaFirmar;
	}

	public List<ConcursoPuestoAgr> getListaGruposParaFirmar() {
		return listaGruposParaFirmar;
	}

	public void setListaGruposAtrasados(
			List<ConcursoPuestoAgr> listaGruposAtrasados) {
		this.listaGruposAtrasados = listaGruposAtrasados;
	}

	public List<ConcursoPuestoAgr> getListaGruposAtrasados() {
		return listaGruposAtrasados;
	}

	public Boolean getExisteResolucion() {
		return existeResolucion;
	}

	public void setExisteResolucion(Boolean existeResolucion) {
		this.existeResolucion = existeResolucion;
	}

	public Boolean getHayObs() {
		return hayObs;
	}

	public void setHayObs(Boolean hayObs) {
		this.hayObs = hayObs;
	}

}
