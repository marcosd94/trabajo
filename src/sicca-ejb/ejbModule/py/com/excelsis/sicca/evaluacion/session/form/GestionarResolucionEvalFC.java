package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposEvaluacion;
import py.com.excelsis.sicca.entity.ObsCapEval;
import py.com.excelsis.sicca.entity.ResolucionEval;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarResolucionEvalFC")
public class GestionarResolucionEvalFC implements Serializable {

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

	private Long idEvaluacionDesempeno = null;
	private boolean primeraEntrada = false;
	private String observacion;
//	agregado
	private String nombrePantalla ;
	private String direccionFisica;
	private String entity;
	private String from;
	private Boolean aviso;
//***
	EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private List<GrupoMetodologia> grupoMetodologiaLista = new ArrayList<GrupoMetodologia>();
	private List<ResolucionEval> resolucionEvalLista = new ArrayList<ResolucionEval>();

	public void init() {
		try {
			if (!seguridadUtilFormController.validarInput(
					idEvaluacionDesempeno.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}

			seguridadUtilFormController.validarEvaluacion(
					idEvaluacionDesempeno,
					referenciasUtilFormController.getReferencia(
							"ESTADOS_EVALUACION_DESEMPENO",
							"RESOLUCION EVALUACION").getValorNum(),
					ActividadEnum.EVAL_DESEM_GENERAR_RESOLUCION.getValor());
			evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
					idEvaluacionDesempeno);
			cargarCabecera();
			searchGrupoMetodologia();
			searchResolucionEval();
//			agregado; Werner.
			anexarReglamento();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void initView() throws Exception{
		evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
				idEvaluacionDesempeno);
		cargarCabecera();
		searchGrupoMetodologia();
		searchResolucionEval();
//		agregado; Werner.*******************************************
		if (from.contentEquals("admDocAdjunto"))
			from = "evaluacionDesempenho/historialCircuito/HistorialCircuitoList";
//******************************************************************	
	}

	private void cargarCabecera() throws Exception {
		seguridadUtilFormController
				.cargarCabeceraEvaluacion(idEvaluacionDesempeno);
	}

	private void searchGrupoMetodologia() {
		grupoMetodologiaLista = em
				.createQuery(
						"Select g from GrupoMetodologia g "
								+ " where g.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=:idEval"
								+ " and g.activo=true  ")
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
	}

	public String obtenerGrupo(Integer idx) {
		GrupoMetodologia gr = grupoMetodologiaLista.get(idx);
		return em.find(GruposEvaluacion.class,
				gr.getGruposEvaluacion().getIdGruposEvaluacion()).getGrupo();
	}

	public String obtenerPlantilla(Integer idx) {
		GrupoMetodologia gr = grupoMetodologiaLista.get(idx);
		return em.find(DatosEspecificos.class,
				gr.getDatosEspecifMetod().getIdDatosEspecificos())
				.getDescripcion();
	}

	private void searchResolucionEval() {
		resolucionEvalLista = em
				.createQuery(
						"Select r from ResolucionEval r "
								+ "where r.evaluacionDesempeno.idEvaluacionDesempeno =:idEval")
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
	}

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public String aprobar() {
//		if (!check())
//			return null;
// agregado; Werner
		if (!check2())
			return null;
		
		try {
			/**
			 * MODIFICAR EL ESTADO DE LA EVALUACION
			 * */

			evaluacionDesempeno.setEstado(referenciasUtilFormController
					.getReferencia("ESTADOS_EVALUACION_DESEMPENO",
							"ENVIADO A APROBACION").getValorNum());
			evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
			evaluacionDesempeno.setFechaMod(new Date());
			em.merge(evaluacionDesempeno);

			if (observacion != null && !observacion.trim().isEmpty()) {
				ObsCapEval obsCapEval = new ObsCapEval();
				obsCapEval.setEvaluacionDesempeno(evaluacionDesempeno);
				obsCapEval.setFechaAlta(new Date());
				obsCapEval.setObservacion(observacion);
				obsCapEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				obsCapEval.setIdTaskInstance(evaluacionDesempeno
						.getIdProcessInstance());
				em.persist(obsCapEval);
			}

			// Se pasa a la siguiente tarea
			/**
			 * SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.EVAL_DESEM_GENERAR_RESOLUCION);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.EVAL_DESEM_APROBAR_EVALUACION);
			jbpmUtilFormController.setTransition("genRes_TO_aprEva");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EVALUACION);
			jbpmUtilFormController.setEvaluacionDesempeno(evaluacionDesempeno);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			}

			return "next";

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"Error inesperado: " + e.getMessage());
			return null;
		}
	}
	
	private Boolean check() {
		statusMessages.clear();
		if (resolucionEvalLista.isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU605_msg_resolucion"));
			return false;
		}
		return true;
	}
//	agregado; Werner.
	private Boolean check2(){
		String q = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
			+ idEvaluacionDesempeno + " and nombre_pantalla = '"+nombrePantalla+ "'";
		
		List<Documentos> documentoDTOList = new ArrayList<Documentos>();
		documentoDTOList = em.createNativeQuery(q, Documentos.class)
			.getResultList();
		
		if (documentoDTOList.isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU605_msg_reglamento"));
			return false;
		}
		return true;
	}
//	agregado; Werner.
	public void anexarReglamento() {
		nombrePantalla="adjuntarReglamento";
		entity = "EvaluacionDesempeno";
		Long idOEE=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab();
		Long idUO=seguridadUtilFormController.getNivelEntidadOeeUtil().getIdUnidadOrganizativa();
		direccionFisica =
			"//SICCA//" + General.anhoActual() + "//OEE//" + idOEE + "//UO//"+idUO+"REGLAM"+idEvaluacionDesempeno;
		avisandoAnexo();
	}
	
	private void avisandoAnexo(){
		String q = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
				+ idEvaluacionDesempeno + " and nombre_pantalla = '"+nombrePantalla+ "'";
		List<Documentos> documentoDTOList = new ArrayList<Documentos>();	
		documentoDTOList = em.createNativeQuery(q, Documentos.class).getResultList();
		if (documentoDTOList.isEmpty()) 
			aviso = true;
		else
			aviso = false;
	}
//	****************************************************************
	public String getNombrePantalla() {
		return nombrePantalla;
	}
	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}
	public String getDireccionFisica() {
		return direccionFisica;
	}
	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
//	****************************************************************	
	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public List<GrupoMetodologia> getGrupoMetodologiaLista() {
		return grupoMetodologiaLista;
	}

	public void setGrupoMetodologiaLista(
			List<GrupoMetodologia> grupoMetodologiaLista) {
		this.grupoMetodologiaLista = grupoMetodologiaLista;
	}

	public List<ResolucionEval> getResolucionEvalLista() {
		return resolucionEvalLista;
	}

	public void setResolucionEvalLista(List<ResolucionEval> resolucionEvalLista) {
		this.resolucionEvalLista = resolucionEvalLista;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;

	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Boolean getAviso() {
		return aviso;
	}

	public void setAviso(Boolean aviso) {
		this.aviso = aviso;
	}

}
