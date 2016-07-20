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
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ComisionEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GruposEvaluacion;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.evaluacion.session.ComisionEvalList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("comiteEvaluacionListFC")
public class ComiteEvaluacionListFC implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ComisionEvalList comisionEvalList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	@In(required = false,create=true)
	SeguridadUtilFormController seguridadUtilFormController;
 	
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	
	private List<ComisionEval> comisionEvals= new ArrayList<ComisionEval>();
	private Long idEvaluacionDesempeno= null;
	private String comite;
	private String evaluacionNombre;

	
	
	

	public void init(){
		try {
			cargarCabecera();
			search();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	
	private void cargarCabecera() throws Exception{

		EvaluacionDesempeno e = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		ConfiguracionUoCab uo =
			em.find(ConfiguracionUoCab.class, e.getConfiguracionUoCab().getIdConfiguracionUo());
		evaluacionNombre = e.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(uo.getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uo.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
	}
	
	public void search(){
		comisionEvalList.getConfiguracionUoCab().setIdConfiguracionUo(seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab());
		comisionEvalList.getComisionEval().setActivo(true);
		comisionEvalList.getComisionEval().setNombre(comite);
		comisionEvalList.setIdEvaluacionDesempeno(idEvaluacionDesempeno);
		comisionEvals=comisionEvalList.listaResultados();
	}
	
	
	@SuppressWarnings("unchecked")
	public String integrantes(Long idComisionEval){
		String integrantes="";
		List<ComisionCapacEval> comisionCapacEvals=em.createQuery("Select c from ComisionCapacEval c " +
				" where c.comisionEval.idComisionEval=:idComisionEval").setParameter("idComisionEval",idComisionEval).getResultList();
		for (ComisionCapacEval comisionCapacEval : comisionCapacEvals) {
			if(!integrantes.equals(""))
				integrantes+=",";
			
			integrantes+=comisionCapacEval.getPersona().getNombreCompleto();
		}
		
		return integrantes;
	}
	
	public void searchAll() throws Exception{
		comite=null;
		search();
		
	}
	
	@SuppressWarnings("unchecked")
	public void eliminar(Long idComiteEval) {
		try {
			ComisionEval comisionEval=em.find(ComisionEval.class, idComiteEval);
			comisionEval.setActivo(false);
			comisionEval.setFechaMod(new Date());
			comisionEval.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(comisionEval);
			
			List<ComisionCapacEval> gs=em.createQuery("Select c from ComisionCapacEval c" +
					" where c.comisionEval.idComisionEval=:idComisionEval").setParameter("idComisionEval",idComiteEval).getResultList();
			for (ComisionCapacEval dets : gs) {
				dets.setActivo(false);
				dets.setFechaMod(new Date());
				dets.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(dets);
			}
			em.flush();
			search();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			e.printStackTrace();
		}
	}

//		GETTERS Y SETTERS 

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}


	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}
	

	public List<ComisionEval> getComisionEvals() {
		return comisionEvals;
	}

	public void setComisionEvals(List<ComisionEval> comisionEvals) {
		this.comisionEvals = comisionEvals;
	}

	public String getComite() {
		return comite;
	}

	public void setComite(String comite) {
		this.comite = comite;
	}

	public String getEvaluacionNombre() {
		return evaluacionNombre;
	}

	public void setEvaluacionNombre(String evaluacionNombre) {
		this.evaluacionNombre = evaluacionNombre;
	}



	
}
