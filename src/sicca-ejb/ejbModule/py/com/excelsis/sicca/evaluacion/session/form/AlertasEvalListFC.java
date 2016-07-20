package py.com.excelsis.sicca.evaluacion.session.form;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import enums.Estado;
import py.com.excelsis.sicca.entity.AlertasEval;
import py.com.excelsis.sicca.entity.AlertasEvalDet;
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.evaluacion.session.AlertasEvalList;
import py.com.excelsis.sicca.evaluacion.session.PlanMejoraList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("alertasEvalListFC")
public class AlertasEvalListFC {
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
	AlertasEvalList alertasEvalList;

	
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	
	private AlertasEval alertasEval= new AlertasEval();
	private List<AlertasEval> alertasEvalLista= new  ArrayList<AlertasEval>();
	
	


	public void init() {
		nivelEntidadOeeUtil.init();
		search();
	}
	
	
	public void searchAll(){
		alertasEval= new AlertasEval();
		nivelEntidadOeeUtil.limpiar();
		search();
	}
	
	public void search(){
		alertasEvalList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		alertasEvalList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		alertasEvalList.setIdSinEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
		alertasEvalList.getAlertasEval().setActivo(true);
		alertasEvalLista=alertasEvalList.getResultList();
	}
	
	public void eliminar(AlertasEval alertasEval){
		try {
			alertasEval.setActivo(false);
			alertasEval.setFechaMod(new Date());
			alertasEval.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(alertasEval);
			inactivarDetalles(alertasEval.getIdAlertasEval());
			em.flush();
			search();
			 statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Se produjo un error inesperado: "+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	private void inactivarDetalles(Long idCab){
		List<AlertasEvalDet> dets= em.createQuery("Select d from AlertasEvalDet d " +
				" where d.alertasEval.idAlertasEval=:idCab and d.activo= true ").setParameter("idCab",idCab).getResultList();
		for (AlertasEvalDet alertasEvalDet : dets) {
			alertasEvalDet.setActivo(false);
			alertasEvalDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			alertasEvalDet.setFechaMod(new Date());
			em.merge(alertasEvalDet);
		}
	}
	

	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public List<AlertasEval> getAlertasEvalLista() {
		return alertasEvalLista;
	}


	public void setAlertasEvalLista(List<AlertasEval> alertasEvalLista) {
		this.alertasEvalLista = alertasEvalLista;
	}





	
	
	

	
}
