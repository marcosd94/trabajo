package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.graph.exe.ProcessInstance;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ComisionEval;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.evaluacion.session.EvaluacionDesempenoList;
import py.com.excelsis.sicca.evaluacion.session.EvaluacionDesempenoRapidaList;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admEvalDesempListFC")
public class AdmEvalDesempListFC implements Serializable {

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
	@In(create = true)
	EvaluacionDesempenoList evaluacionDesempenoList;
	@In(create = true)
	EvaluacionDesempenoRapidaList evaluacionDesempenoRapidaList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(required = false, create = true)
	private Actor actor;
	@In(required = false)
	ProcessInstance processInstance;

	private Boolean esAdministradorSFP = false;
	private Integer idEstado;
	private Integer estadoCarga;
	private Integer estadoIniciado;
	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private List<SelectItem> selectItemsEstado = new ArrayList<SelectItem>();
	private List<EvaluacionDesempeno> listaEvaluacion = new ArrayList<EvaluacionDesempeno>();

	public void init() {

		estadoCarga = referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "CARGA").getValorNum();
		estadoIniciado = referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "INICIADO CIRCUITO")
				.getValorNum();

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		selectItemsEstado = referenciasUtilFormController
				.getSelectItemValor("ESTADOS_EVALUACION_DESEMPENO");
		cargarAdministradorSFP();
		search();
		asignarRolesTarea();
		// calcEstado();

	}
	
	public void initEvalRapida() {

		estadoCarga = referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "CARGA").getValorNum();
		estadoIniciado = referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "INICIADO CIRCUITO")
				.getValorNum();

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		selectItemsEstado = referenciasUtilFormController
				.getSelectItemValor("ESTADOS_EVALUACION_DESEMPENO");
		cargarAdministradorSFP();
		searchEvalRapida();
		asignarRolesTarea();
		// calcEstado();

	}
	
	public void asignarRolesTarea() {
		roles =
			seleccionUtilFormController.getRolesTarea(ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA .getValor(), ProcesoEnum.EVALUACION.getValor());
	}

	/**
	 * Verifica si un usuario puede ver todas las OEE o no
	 * 
	 * @return
	 */
	private void cargarAdministradorSFP() {
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador()) {
				esAdministradorSFP = true;
				return;
			}
		}

		esAdministradorSFP = false;
	}

	public void searchAll() {
		idEstado = null;
		evaluacionDesempeno = new EvaluacionDesempeno();
		if (nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdConfigCab() != null) {
			ConfiguracionUoCab configuracionUoCab = em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab());
			evaluacionDesempenoList.setConfiguracionUoCab(configuracionUoCab);
		}
//		if (nivelEntidadOeeUtil != null
//				&& nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
//			ConfiguracionUoDet configuracionUoDet = em.find(
//					ConfiguracionUoDet.class,
//					nivelEntidadOeeUtil.getIdUnidadOrganizativa());
//			evaluacionDesempenoList.setConfiguracionUoDet(configuracionUoDet);
//		}
		if (idEstado != null)
			evaluacionDesempenoList.getEvaluacionDesempeno()
					.setEstado(idEstado);
		listaEvaluacion = evaluacionDesempenoList.limpiar();
	}

	public void search() {
		if (nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdConfigCab() != null) {
			ConfiguracionUoCab configuracionUoCab = em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab());
			evaluacionDesempenoList.setConfiguracionUoCab(configuracionUoCab);
		}
//		if (nivelEntidadOeeUtil != null
//				&& nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
//			ConfiguracionUoDet configuracionUoDet = em.find(
//					ConfiguracionUoDet.class,
//					nivelEntidadOeeUtil.getIdUnidadOrganizativa());
//			evaluacionDesempenoList.setConfiguracionUoDet(configuracionUoDet);
//		}
		if (evaluacionDesempeno.getTitulo() != null
				&& !evaluacionDesempeno.getTitulo().trim().isEmpty())
			evaluacionDesempenoList.getEvaluacionDesempeno().setTitulo(
					evaluacionDesempeno.getTitulo());
		if (idEstado != null)
			evaluacionDesempenoList.getEvaluacionDesempeno().setEstado(
					new Integer("" + idEstado));
		listaEvaluacion = evaluacionDesempenoList.listaResultados();
	}
	
	public void searchEvalRapida() {
		if (nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdConfigCab() != null) {
			ConfiguracionUoCab configuracionUoCab = em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab());
			evaluacionDesempenoRapidaList.setConfiguracionUoCab(configuracionUoCab);
		}
//		if (nivelEntidadOeeUtil != null
//				&& nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
//			ConfiguracionUoDet configuracionUoDet = em.find(
//					ConfiguracionUoDet.class,
//					nivelEntidadOeeUtil.getIdUnidadOrganizativa());
//			evaluacionDesempenoList.setConfiguracionUoDet(configuracionUoDet);
//		}
		if (evaluacionDesempeno.getTitulo() != null
				&& !evaluacionDesempeno.getTitulo().trim().isEmpty())
			evaluacionDesempenoRapidaList.getEvaluacionDesempeno().setTitulo(
					evaluacionDesempeno.getTitulo());
		if (idEstado != null)
			evaluacionDesempenoRapidaList.getEvaluacionDesempeno().setEstado(
					new Integer("" + idEstado));
		listaEvaluacion = evaluacionDesempenoRapidaList.listaResultados();
	}

	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController
				.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController
				.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());
		// Unidad Organizativa
		if (usuarioLogueado.getConfiguracionUoDet() != null
				&& usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet() != null)

			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init();
	}

	public String obtenerEstado(EvaluacionDesempeno eval) {
		return referenciasUtilFormController.getReferenciaPorValorNum(
				"ESTADOS_EVALUACION_DESEMPENO", eval.getEstado())
				.getDescLarga();
	}

	public boolean habilitaLinkGrupo(EvaluacionDesempeno eval) {
		List<Sujetos> sujetos = em
				.createQuery(
						"Select s from Sujetos s "
								+ " where s.activo is true and s.evaluacionDesempeno.idEvaluacionDesempeno = :id")
				.setParameter("id", eval.getIdEvaluacionDesempeno())
				.getResultList();

		List<ComisionEval> comision = em
				.createQuery(
						"Select c from ComisionEval c "
								+ " where c.activo is true and c.evaluacionDesempeno.idEvaluacionDesempeno = :id")
				.setParameter("id", eval.getIdEvaluacionDesempeno())
				.getResultList();
		if (sujetos.isEmpty() || comision.isEmpty() || !esEstadoCarga(eval))
			return false;
		return true;
	}

	public boolean esEstadoCarga(EvaluacionDesempeno eval) {
		if (eval.getEstado().intValue() == estadoCarga.intValue())
			return true;
		return false;
	}
	
	public boolean puedeCargarEvalRapida(EvaluacionDesempeno eval) {
		String sqlSujetos = "select * from eval_desempeno.sujetos where sujetos.id_evaluacion_desempeno = "+eval.getIdEvaluacionDesempeno();
		Query q = em.createNativeQuery(sqlSujetos, Sujetos.class);
		
		if(!q.getResultList().isEmpty())
			return true;
		return false;
	}

	public Boolean puedeIniciar(EvaluacionDesempeno eval) {
		if (!esEstadoCarga(eval))
			return false;
		List<Sujetos> sujetos = em
				.createQuery(
						"Select s from Sujetos s "
								+ " where s.activo is true and s.evaluacionDesempeno.idEvaluacionDesempeno = :id")
				.setParameter("id", eval.getIdEvaluacionDesempeno())
				.getResultList();
		if (sujetos.isEmpty())
			return false;
		for (Sujetos s : sujetos) {
			List<GruposSujetos> grupoSujetos = em
					.createQuery(
							"Select gr from GruposSujetos gr "
//									+ " where gr.activo is true and gr.sujetos.idSujetos = :id")//original
							+ " where gr.activo is true and gr.sujetos.evaluacionDesempeno.idEvaluacionDesempeno = :id")
//					.setParameter("id", s.getIdSujetos()).getResultList();//original
							.setParameter("id", eval.getIdEvaluacionDesempeno()).getResultList();
			if (grupoSujetos.isEmpty())
				return false;
		}
		return true;
	}

	public String iniciarProceso(EvaluacionDesempeno eval) {
		try {
			statusMessages.clear();
			eval.setEstado(estadoIniciado);
			eval.setFechaMod(new Date());
			eval.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(eval);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.EVAL_DESEM_ASIGNAR_PLANTILLA);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EVALUACION);
			jbpmUtilFormController.setTransition("start_TO_asgPlan");
			actor.setId(usuarioLogueado.getCodigoUsuario());

			Long processId = jbpmUtilFormController
					.initProcess("evalDesempenho");

			if (processId != null) {
				eval.setIdProcessInstance(processId);
			} else {
				throw new Exception("");
			}

			em.flush();

			String msg = "El Proyecto de la "
					+ SeamResourceBundle.getBundle().getString("CU550_titulo")
					+ " ha iniciado satisfactoriamente";

			statusMessages.add(Severity.INFO, msg);
			search();
			return "ok";

		} catch (Exception e) {
			e.printStackTrace();
			search();
			statusMessages.add(Severity.ERROR,
					"No se ha podido iniciar. Verifique");
			return null;
		}
	}

	public void eliminar(EvaluacionDesempeno eval) {
		try {
			Boolean esEvaluacionRapida = false;
			if(eval.getEsEvaluacionRapida() != null){
				if(eval.getEsEvaluacionRapida())
					esEvaluacionRapida = eval.getEsEvaluacionRapida();
			}
			statusMessages.clear();
			List<Sujetos> sujetos = em
					.createQuery(
							"Select s from Sujetos s "
									+ " where s.activo is true and s.evaluacionDesempeno.idEvaluacionDesempeno = :id")
					.setParameter("id", eval.getIdEvaluacionDesempeno())
					.getResultList();

			for (Sujetos s : sujetos) {
				s.setActivo(false);
				s.setUsuMod(usuarioLogueado.getCodigoUsuario());
				s.setFechaMod(new Date());
				em.merge(s);
			}
			List<ComisionEval> comision = em
					.createQuery(
							"Select c from ComisionEval c "
									+ " where c.activo is true and c.evaluacionDesempeno.idEvaluacionDesempeno = :id")
					.setParameter("id", eval.getIdEvaluacionDesempeno())
					.getResultList();

			for (ComisionEval c : comision) {
				c.setActivo(false);
				c.setFechaMod(new Date());
				c.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(c);
			}

			List<GruposSujetos> grupoSujetos = em
					.createQuery(
							"Select gr from GruposSujetos gr "
									+ " where gr.activo is true and gr.sujetos.evaluacionDesempeno.idEvaluacionDesempeno = :id")
					.setParameter("id", eval.getIdEvaluacionDesempeno())
					.getResultList();

			for (GruposSujetos gr : grupoSujetos) {
				gr.setActivo(false);
				gr.setFechaMod(new Date());
				gr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(gr);
			}

			List<ComisionCapacEval> capacEval = em
					.createQuery(
							"Select cap from ComisionCapacEval cap "
									+ " where cap.activo is true and cap.comisionEval.evaluacionDesempeno.idEvaluacionDesempeno = :id")
					.setParameter("id", eval.getIdEvaluacionDesempeno())
					.getResultList();

			for (ComisionCapacEval cap : capacEval) {
				cap.setActivo(false);
				cap.setFechaMod(new Date());
				cap.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(cap);
			}

			eval.setActivo(false);
			eval.setFechaMod(new Date());
			eval.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(eval);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			if (esEvaluacionRapida)
				searchEvalRapida();
			else{
				search();
			}
		} catch (Exception e) {
			search();
		}

	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public List<SelectItem> getSelectItemsEstado() {
		return selectItemsEstado;
	}

	public void setSelectItemsEstado(List<SelectItem> selectItemsEstado) {
		this.selectItemsEstado = selectItemsEstado;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public List<EvaluacionDesempeno> getListaEvaluacion() {
		return listaEvaluacion;
	}

	public void setListaEvaluacion(List<EvaluacionDesempeno> listaEvaluacion) {
		this.listaEvaluacion = listaEvaluacion;
	}

	public Integer getEstadoCarga() {
		return estadoCarga;
	}

	public void setEstadoCarga(Integer estadoCarga) {
		this.estadoCarga = estadoCarga;
	}

	public Integer getEstadoIniciado() {
		return estadoIniciado;
	}

	public void setEstadoIniciado(Integer estadoIniciado) {
		this.estadoIniciado = estadoIniciado;
	}

}
