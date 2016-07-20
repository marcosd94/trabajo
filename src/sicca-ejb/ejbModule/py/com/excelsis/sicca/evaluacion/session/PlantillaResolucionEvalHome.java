package py.com.excelsis.sicca.evaluacion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.PlantillaResolucionEval;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.TagsEval;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("plantillaResolucionEvalHome")
public class PlantillaResolucionEvalHome extends
		EntityHome<PlantillaResolucionEval> {

	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;
	
	public static final String CONTEXT_VAR_NAME = "plantillaResolucionEvals";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	private List<TagsEval> tagList= new ArrayList<TagsEval>();
	private PlantillaResolucionEval plantillaResolucionEval= new PlantillaResolucionEval(); 
	
	public void setPlantillaResolucionEvalIdPlantillaResolucionEval(Long id) {
		setId(id);
	}

	public Long getPlantillaResolucionEvalIdPlantillaResolucionEval() {
		return (Long) getId();
	}

	@Override
	protected PlantillaResolucionEval createInstance() {
		PlantillaResolucionEval plantillaResolucionEval = new PlantillaResolucionEval();
		return plantillaResolucionEval;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}
		findTags();
	}
	@SuppressWarnings("unchecked")
	private void findTags(){
		tagList= getEntityManager().createQuery("Select t from TagsEval t " +
				" order by t.descripcion ").getResultList();
		plantillaResolucionEval= new PlantillaResolucionEval();
		
		
	}

	@Override
	public String persist() {
		try {
			if(!checkData("persist"))
				return null;
			getInstance().setDescripcion(getInstance().getDescripcion().trim());
			getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			
			return AppHelper.removeFromContext("persist", super.persist(),
					CONTEXT_VAR_NAMES, getEventContext());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			return null;
		}
		
	}

	@Override
	public String update() {
		if(!checkData("update"))
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		
		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	private boolean checkData(String operation){
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(sufc.contieneCaracter(getInstance().getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		
		if(getInstance().getTitulo().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe ingresar el Campo Titulo");
			return false;
		}
		if(getInstance().getVisto().trim().isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe ingresar el Campo Visto");
			return false;
		}
		if(getInstance().getConsiderando().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe ingresar el Campo Considerando");
			return false;
		}
		if(getInstance().getPorTanto().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Campo Por Tanto");
			return false;
		}
		if(getInstance().getResuelve().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe ingresar el Campo Resuelve");
			return false;
		}
		
		
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM PlantillaResolucion o WHERE lower(o.descripcion) = :desc ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idPlantillaResolucion != " + getInstance().getIdPlantillaResolucionEval();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc",getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	public boolean isWired() {
		return true;
	}

	public PlantillaResolucionEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}


	public List<TagsEval> getTagList() {
		return tagList;
	}

	public void setTagList(List<TagsEval> tagList) {
		this.tagList = tagList;
	}

	public PlantillaResolucionEval getPlantillaResolucionEval() {
		return plantillaResolucionEval;
	}

	public void setPlantillaResolucionEval(
			PlantillaResolucionEval plantillaResolucionEval) {
		this.plantillaResolucionEval = plantillaResolucionEval;
	}
	
	

}
