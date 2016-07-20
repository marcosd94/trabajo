package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jbpm.graph.exe.ProcessInstance;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.ProcActividadRol;
import py.com.excelsis.sicca.evaluacion.session.EvaluacionDesempenoList;
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
@Name("admGestionEvaluacionListFC")
public class AdmGestionEvaluacionListFC implements Serializable {

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
	
	@In(required = false)
	ProcessInstance processInstance;


	private Integer idEstado;
	private Integer estadoCarga;
	private Integer estadoIniciado;
	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private List<SelectItem> selectItemsEstado = new ArrayList<SelectItem>();
	private List<EvaluacionDesempeno> listaEvaluacion = new ArrayList<EvaluacionDesempeno>();
	private List<Integer> estadosfinalizados= new ArrayList<Integer>();
	
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
		.getSelectItemValorFinaalizados("ESTADOS_EVALUACION_DESEMPENO");
		search();
	

	}
	private void cargarEstados(){
	
		estadosfinalizados= new ArrayList<Integer>();
		for (int i = 0; i < selectItemsEstado.size(); i++) {
			if(selectItemsEstado.get(i).getValue()!=null)
				estadosfinalizados.add((Integer)selectItemsEstado.get(i).getValue());
		}
		
		
		
	}

	public void searchAll() {
		idEstado = null;
		evaluacionDesempeno = new EvaluacionDesempeno();
		if (usuarioLogueado.getConfiguracionUoDet() != null
				&& usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet() != null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());
		else
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		search();
		
	}

	private String getIdRolesUsuario() {
		String id = "";
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			id += "," + r.getId();
		}
		id = id.replaceFirst(",", "");
		return id;
	}
	
	public void search() {
		
		//si pertenece a SFP o la OEE
		String rolesProc = getIdRolesUsuario();
		List<ProcActividadRol> procActividadRol = new ArrayList<ProcActividadRol>();
		String q = "select * from proceso.proc_actividad_rol where id_rol IN ( "+rolesProc+") and id_actividad_proceso IN (42,43)";//procesos correspondientes a la SFP, Aprobar Evaluación; Adjuntar Resolución; Ver Resultados e Históricos OEE
		procActividadRol = em.createNativeQuery(q, ProcActividadRol.class).getResultList();
		//************
		if (procActividadRol.isEmpty()) {//agregado
			evaluacionDesempenoList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		}
		
		
		if (evaluacionDesempeno.getTitulo() != null
				&& !evaluacionDesempeno.getTitulo().trim().isEmpty())
			evaluacionDesempenoList.getEvaluacionDesempeno().setTitulo(
					evaluacionDesempeno.getTitulo());
		if (idEstado != null)
		{
			estadosfinalizados= new ArrayList<Integer>();
			estadosfinalizados.add(idEstado);
		}else
			cargarEstados();
		evaluacionDesempenoList.getEstadosFinalizados().addAll(estadosfinalizados);
		listaEvaluacion = evaluacionDesempenoList.listaResultados569();
	}
	public  boolean habLinkCarga(EvaluacionDesempeno eval){
		String estado=referenciasUtilFormController.getReferenciaPorValorNum(
				"ESTADOS_EVALUACION_DESEMPENO", eval.getEstado())
				.getDescLarga();
		if(estado.equalsIgnoreCase("FINALIZADO CIRCUITO"))
			return true;
		
		return false;
	}

	public void cargarCabecera() {
		
		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		// Unidad Organizativa
		if (usuarioLogueado.getConfiguracionUoDet() != null
				&& usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet() != null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init2();
	}

	public String obtenerEstado(EvaluacionDesempeno eval) {
		return referenciasUtilFormController.getReferenciaPorValorNum(
				"ESTADOS_EVALUACION_DESEMPENO", eval.getEstado())
				.getDescLarga();
	}

	public boolean habCarga(EvaluacionDesempeno eval) {
		boolean permitirCargar= habLinkCarga(eval);

		String rolesProc = getIdRolesUsuario();
		List<ProcActividadRol> procActividadRol = new ArrayList<ProcActividadRol>();
		String q = "select * from proceso.proc_actividad_rol where id_rol IN ( "+rolesProc+") and id_actividad_proceso IN (40,41,44)";//en el caso que tenga SFP y OEE
		procActividadRol = em.createNativeQuery(q, ProcActividadRol.class).getResultList();
		System.out.println(eval.getTitulo());
		Long idUoCabEval=eval.getConfiguracionUoCab().getIdConfiguracionUo();
		if (idUoCabEval != usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo() && permitirCargar == true) {
			return false;
		}else{
			if(!procActividadRol.isEmpty())
				return permitirCargar;
			else
				return false;

		}
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

	public List<Integer> getEstadosfinalizados() {
		return estadosfinalizados;
	}

	public void setEstadosfinalizados(List<Integer> estadosfinalizados) {
		this.estadosfinalizados = estadosfinalizados;
	}
	

}
