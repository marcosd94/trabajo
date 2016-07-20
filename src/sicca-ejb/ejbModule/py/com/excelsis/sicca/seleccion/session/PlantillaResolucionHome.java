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

import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("plantillaResolucionHomologacionHome")
public class PlantillaResolucionHome extends
		EntityHome<PlantillaResolucion> {

	
	public static final String CONTEXT_VAR_NAME = "plantillaResolucionHomologacions";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;
	
	private List<Tags> tagList= new ArrayList<Tags>();
	private String fechaActual;
	private Resolucion homologacionVp;
	
	public void setPlantillaResolucionHomologacionIdPlantillaResolucionHomologacion(
			Long id) {
		setId(id);
	}

	public Long getPlantillaResolucionHomologacionIdPlantillaResolucionHomologacion() {
		return (Long) getId();
	}

	@Override
	protected PlantillaResolucion createInstance() {
		PlantillaResolucion plantillaResolucionHomologacion = new PlantillaResolucion();
		return plantillaResolucionHomologacion;
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
		tagList= getEntityManager().createQuery("Select t from Tags t " +
				" order by t.descripcion ").getResultList();
		homologacionVp= new Resolucion();
		
		
	}
	public boolean isWired() {
		return true;
	}

	public PlantillaResolucion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Resolucion> getResolucionHomologacions() {
		return getInstance() == null ? null
				: new ArrayList<Resolucion>(getInstance()
						.getResolucionHomologacions());
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
		if(getInstance().getPorTanto().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Campo Por Tanto");
			return false;
		}
		if(getInstance().getResuelve().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe ingresar el Campo Resuelve");
			return false;
		}
		if(getInstance().getFirma().trim().isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el Campo Firma");
			return false;
		}
		if(getInstance().getCargo().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "DEbe ingresar el Campo Cargo");
			return false;
		}
		if(getInstance().getInstitucion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Debe ingresar el Campo Institucion");
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
			hql += " AND o.idPlantillaResolucion != " + getInstance().getIdPlantillaResolucion();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc",getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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

	public Resolucion getHomologacionVp() {
		return homologacionVp;
	}

	public void setHomologacionVp(Resolucion homologacionVp) {
		this.homologacionVp = homologacionVp;
	}
	
	

}
