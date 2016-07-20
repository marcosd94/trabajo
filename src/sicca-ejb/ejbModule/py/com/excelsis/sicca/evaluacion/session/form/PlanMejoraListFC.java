package py.com.excelsis.sicca.evaluacion.session.form;


import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import enums.Estado;
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.evaluacion.session.PlanMejoraList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("planMejoraListFC")
public class PlanMejoraListFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	PlanMejoraList planMejoraList;

	
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	
	private PlanMejora planMejora= new PlanMejora();
	private Estado estado=Estado.ACTIVO;
	
	


	public void init() {
		cargarCabecera();
		search();
	}
	
	
	public void searchAll(){
	
		
		planMejora= new PlanMejora();
		
		planMejoraList.getPlanMejora().setIdentificador(null);
		planMejoraList.getPlanMejora().setDescripcion(null);
		planMejoraList.setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		estado=Estado.ACTIVO;
		planMejoraList.setEstado(estado);
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		planMejoraList.listaResultados();
	}
	
	public void search(){
		planMejoraList.getPlanMejora().setIdentificador(planMejora.getIdentificador());
		planMejoraList.getPlanMejora().setDescripcion(planMejora.getDescripcion());
		planMejoraList.setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		planMejoraList.setEstado(estado);
		planMejoraList.listaResultados();
	}
	
	public void cargarCabecera(){
			//OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
	}
	

	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public PlanMejora getPlanMejora() {
		return planMejora;
	}


	public void setPlanMejora(PlanMejora planMejora) {
		this.planMejora = planMejora;
	}


	public Estado getEstado() {
		return estado;
	}


	public void setEstado(Estado estado) {
		this.estado = estado;
	}




	
	
	

	
}
