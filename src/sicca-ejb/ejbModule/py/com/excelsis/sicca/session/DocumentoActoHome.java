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

import py.com.excelsis.sicca.entity.DocumentoActo;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("documentoActoHome")
public class DocumentoActoHome extends EntityHome<DocumentoActo> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected DocumentoActo loadInstance() {
		DocumentoActo o = super.loadInstance();
		this.idPlantaCargoDet = o.getPlantaCargoDet().getIdPlantaCargoDet();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idPlantaCargoDet;
	public static final String CONTEXT_VAR_NAME = "documentoActos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<DocumentoActo> getDocumentoActos(){
		try{
			return getEntityManager().createQuery(" select o from " + DocumentoActo.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<DocumentoActo>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getDocumentoActoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(DocumentoActo o : getDocumentoActos())
			si.add(new SelectItem(o.getIdDocumentoActo(),"" + o.getIdDocumentoActo()));
		return si;
	}

	public void setDocumentoActoIdDocumentoActo(Long id) {
		setId(id);
	}

	public Long getDocumentoActoIdDocumentoActo() {
		return (Long) getId();
	}

	@Override
	protected DocumentoActo createInstance() {
		DocumentoActo documentoActo = new DocumentoActo();
		return documentoActo;
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

	public DocumentoActo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setPlantaCargoDet(getEntityManager().find(PlantaCargoDet.class,this.idPlantaCargoDet));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setPlantaCargoDet(getEntityManager().find(PlantaCargoDet.class,this.idPlantaCargoDet));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Long getIdPlantaCargoDet(){
		return this.idPlantaCargoDet;
	}
	
	public void setIdPlantaCargoDet(Long idPlantaCargoDet){
		this.idPlantaCargoDet = idPlantaCargoDet;
	}
	
}
