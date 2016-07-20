package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("concursoHome")
public class ConcursoHome extends EntityHome<Concurso> {

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "concursos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Concurso> getConcursos(){
		try{
			return getEntityManager().createQuery(" select o from " + Concurso.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Concurso>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getConcursoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Concurso o : getConcursos())
			si.add(new SelectItem(o.getIdConcurso(),"" + o.getNombre()));
		return si;
	}
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setConcursoIdConcurso(Long id) {
		setId(id);
	}

	public Long getConcursoIdConcurso() {
		return (Long) getId();
	}

	@Override
	protected Concurso createInstance() {
		Concurso concurso = new Concurso();
		return concurso;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificosTipoConc(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificosTipoConc() == null)
			return false;
		return true;
	}

	public Concurso getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());	
			
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getNombre().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
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
		String hql = "SELECT o FROM Concurso o WHERE lower(o.nombre) = '"+getInstance().getNombre().trim().toLowerCase()+"' ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idConcurso != " + getInstance().getIdConcurso().longValue();
		}
		List<Concurso> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	

	
	@Override
    public void setInstance(Concurso instance)
    {
        if (instance != null)
        {
            super.setId(instance.getIdConcurso());
        }
        super.setInstance(instance);
    }

}
