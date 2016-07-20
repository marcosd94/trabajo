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

import py.com.excelsis.sicca.entity.TblPuestoCategoria;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("tblPuestoCategoriaHome")
public class TblPuestoCategoriaHome extends EntityHome<TblPuestoCategoria> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected TblPuestoCategoria loadInstance() {
		TblPuestoCategoria o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "tblPuestoCategorias";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<TblPuestoCategoria> getTblPuestoCategorias(){
		try{
			return getEntityManager().createQuery(" select o from " + TblPuestoCategoria.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<TblPuestoCategoria>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getTblPuestoCategoriaSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(TblPuestoCategoria o : getTblPuestoCategorias())
			si.add(new SelectItem(o.getIdPuestoCategoria(),"" + o.getCodigoCategoria()));
		return si;
	}

	public void setTblPuestoCategoriaIdPuestoCategoria(Integer id) {
		setId(id);
	}

	public Integer getTblPuestoCategoriaIdPuestoCategoria() {
		return (Integer) getId();
	}

	@Override
	protected TblPuestoCategoria createInstance() {
		TblPuestoCategoria tblPuestoCategoria = new TblPuestoCategoria();
		return tblPuestoCategoria;
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

	public TblPuestoCategoria getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(new Integer(1));		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(new Integer(1));		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(new Integer(1));		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
}
