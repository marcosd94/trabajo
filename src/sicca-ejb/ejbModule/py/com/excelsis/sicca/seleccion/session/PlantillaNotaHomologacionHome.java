package py.com.excelsis.sicca.seleccion.session;

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

import py.com.excelsis.sicca.entity.NotaHomologacion;
import py.com.excelsis.sicca.entity.PlantillaNotaHomologacion;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("plantillaNotaHomologacionHome")
public class PlantillaNotaHomologacionHome extends
		EntityHome<PlantillaNotaHomologacion> {

	
	
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	
	
	public static final String CONTEXT_VAR_NAME = "plantillaNotaHomologacions";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };

	
	private List<Tags> tagList= new ArrayList<Tags>();
	private String fechaActual;
	
	
	public void setPlantillaNotaHomologacionIdPlantillaNotaHomologacion(Long id) {
		setId(id);
	}

	public Long getPlantillaNotaHomologacionIdPlantillaNotaHomologacion() {
		return (Long) getId();
	}

	@Override
	protected PlantillaNotaHomologacion createInstance() {
		PlantillaNotaHomologacion plantillaNotaHomologacion = new PlantillaNotaHomologacion();
		return plantillaNotaHomologacion;
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

	public boolean isWired() {
		return true;
	}

	public PlantillaNotaHomologacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<NotaHomologacion> getNotaHomologacions() {
		return getInstance() == null ? null : new ArrayList<NotaHomologacion>(
				getInstance().getNotaHomologacions());
	}
	@Override
	public String persist() {
		if(!checkData("persist"))
			return null;
		getInstance().setDescripcion(getInstance().getDescripcion().trim());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
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

	
	
	

	
	@SuppressWarnings("unchecked")
	private void findTags(){
		tagList= getEntityManager().createQuery("Select t from Tags t " +
				" order by t.descripcion ").getResultList();
		
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
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM PlantillaNotaHomologacion o WHERE lower(o.descripcion) = :desc";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idPlantillaNotaHomologacion != " + getInstance().getIdPlantillaNotaHomologacion();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	//getter setter
	public List<Tags> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tags> tagList) {
		this.tagList = tagList;
	}

	public String getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(String fechaActual) {
		this.fechaActual = fechaActual;
	}
	
	
	

}
