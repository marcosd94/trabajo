package py.com.excelsis.sicca.session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.PersistenceException;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.SinCategoriaContratados;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("sinCategoriaContratadosHome")
public class SinCategoriaContratadosHome extends EntityHome<SinCategoriaContratados> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected SinCategoriaContratados loadInstance() {
		SinCategoriaContratados o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "sinCategoriaContratadoss";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<SinCategoriaContratados> getSinCategoriaContratadoss(){
		try{
			return getEntityManager().createQuery(" select o from " + SinCategoriaContratados.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<SinCategoriaContratados>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getSinCategoriaContratadosSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(SinCategoriaContratados o : getSinCategoriaContratadoss())
			si.add(new SelectItem(o.getIdSinCategoriaContratados(),"" + o.getConCtg()));
		return si;
	}

	public void setSinCategoriaContratadosIdSinCategoriaContratados(Long id) {
		setId(id);
	}

	public Long getSinCategoriaContratadosIdSinCategoriaContratados() {
		return (Long) getId();
	}

	@Override
	protected SinCategoriaContratados createInstance() {
		SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();
		return sinCategoriaContratados;
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

	public SinCategoriaContratados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setFechaMod(new Date());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
}
