package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sun.mail.imap.protocol.UIDSet;

@Name("tituloAcademicoHome")
public class TituloAcademicoHome extends EntityHome<TituloAcademico> {

	@In(create = true)
	NivelEstudioHome nivelEstudioHome;
	
	@In
	StatusMessages statusMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	private Long idNivelEstudio;
	private Long idEspecialidad;

	public void setTituloAcademicoIdTituloAcademico(Long id) {
		setId(id);
	}

	public Long getTituloAcademicoIdTituloAcademico() {
		return (Long) getId();
	}

	@Override
	protected TituloAcademico createInstance() {
		TituloAcademico tituloAcademico = new TituloAcademico();
		return tituloAcademico;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "tituloAcademico";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<TituloAcademico> getTituloAcademicos(){
		try{
			return getEntityManager().createQuery(" select o from " + TituloAcademico.class.getName() + " o where o.activo = true order by o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<TituloAcademico>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getTituloAcademicoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(TituloAcademico o : getTituloAcademicos())
			si.add(new SelectItem(o.getIdTituloAcademico(),"" + o.getDescripcion()));
		return si;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
//		NivelEstudio nivelEstudio = nivelEstudioHome.getDefinedInstance();
//		if (nivelEstudio != null) {
//			getInstance().setNivelEstudio(nivelEstudio);
//		}
		if (!isIdDefined()) {
			getInstance().setActivo(true);
			idEspecialidad=null;
			idNivelEstudio=null;
		}else{
			if(getInstance().getEspecialidades()!=null)
				idEspecialidad=getInstance().getEspecialidades().getIdEspecialidades();
			if(getInstance().getNivelEstudio()!=null)
			idNivelEstudio=getInstance().getNivelEstudio().getIdNivelEstudio();
			
		}
		
	}
	public boolean isWired() {
//		if (getInstance().getNivelEstudio() == null)
//			return false;
		return true;
	}

	public TituloAcademico getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
	//	getInstance().setEspecialidades((getEntityManager().find(Especialidades.class, idEspecialidad)));
		getInstance().setNivelEstudio(getEntityManager().find(NivelEstudio.class, idNivelEstudio));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
	//	getInstance().setEspecialidades(getEntityManager().find(Especialidades.class, idEspecialidad));
		getInstance().setNivelEstudio(getEntityManager().find(NivelEstudio.class, idNivelEstudio));
			
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM TituloAcademico o WHERE lower(o.descripcion) = :desc " +
				"  AND o.nivelEstudio.idNivelEstudio ="+idNivelEstudio;
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idTituloAcademico != " + getInstance().getIdTituloAcademico().longValue();
		}

		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(getInstance().getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
			
		if(operation.equals("update")){
			if(!getInstance().isActivo() && !getInstance().getEstudiosRealizados().isEmpty()){
				statusMessages.add("No se puede eliminar el registro porque está en uso");
				return false;
			}
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	//Public getters and setters 



	public Long getIdNivelEstudio() {
		return idNivelEstudio;
	}

	public void setIdNivelEstudio(Long idNivelEstudio) {
		this.idNivelEstudio = idNivelEstudio;
	}

	public Long getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(Long idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}
	
	

}
