package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("plantillaEncuestaHome")
public class PlantillaEncuestaHome extends EntityHome<PlantillaEncuesta> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	public static final String CONTEXT_VAR_NAME = "plantillaEncuestas";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setPlantillaEncuestaIdPlantilla(Long id) {
		setId(id);
	}

	public Long getPlantillaEncuestaIdPlantilla() {
		return (Long) getId();
	}

	@Override
	protected PlantillaEncuesta createInstance() {
		PlantillaEncuesta plantillaEncuesta = new PlantillaEncuesta();
		return plantillaEncuesta;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public PlantillaEncuesta getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Preguntas> getPreguntases() {
		return getInstance() == null ? null : new ArrayList<Preguntas>(
				getInstance().getPreguntases());
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setConfiguracionUoCab(usuarioLogueado.getConfiguracionUoCab());
		getInstance().setActivo(true);
		getInstance().setNombre(getInstance().getNombre().trim().toUpperCase());
		String resultado = AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
		if(resultado.equals("persisted")){
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}
		return resultado;
	}

	@Override
	public String update() {
		seguridadUtilFormController.validarPerteneceOEE(getInstance().getConfiguracionUoCab().getIdConfiguracionUo());
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
		getInstance().setNombre(getInstance().getNombre().trim().toUpperCase());
		if(!getInstance().isActivo())
			inactivarDependientes();
		String resultado = AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
		if(resultado.equals("updated")){
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}
		return resultado;
	}
	
	private void inactivarDependientes(){
		
		for (Preguntas e : getInstance().getPreguntases()) {
			e.setActivo(false);
			for (RespuestaMultiple rm : e.getRespuestaMultiples()) {
				rm.setActivo(false);
				getEntityManager().merge(rm);
			}
			getEntityManager().merge(e);
			
		}
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getNombre().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, "Nombre Inválido");
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(getInstance().getNombre().trim())){
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
		String hql = "SELECT o FROM PlantillaEncuesta o WHERE lower(o.nombre) =:desc";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idPlantilla != " + getInstance().getIdPlantilla().longValue();
		}
		List<PlantillaEncuesta> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getNombre().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}

}
