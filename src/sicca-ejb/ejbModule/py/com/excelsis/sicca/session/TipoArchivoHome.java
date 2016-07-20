package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("tipoArchivoHome")
public class TipoArchivoHome extends EntityHome<TipoArchivo> {

	
	
	
	@In
	StatusMessages statusMessages;
	
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create=true)
	ReferenciasList referenciasList;
	
	//Value holders for selectItems if exists
	private Long idReferencias=null;
	public static final String CONTEXT_VAR_NAME = "tipoArchivo";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<TipoArchivo> getTipoArchivo(){
		try{
			return getEntityManager().createQuery(" select o from " + TipoArchivo.class.getName() + " o " +
					" WHERE o.activo = true ORDER BY o.descripcion ").getResultList();
		}catch(Exception ex){
			return new Vector<TipoArchivo>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getTipoArchivoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(TipoArchivo o : getTipoArchivo())
			si.add(new SelectItem(o.getIdTipoArchivo(),"" + o.getDescripcion()));
		return si;
	}
	
	public void setTipoArchivoIdTipoArchivo(Long id) {
		setId(id);
	}

	public Long getTipoArchivoIdTipoArchivo() {
		return (Long) getId();
	}

	@Override
	protected TipoArchivo createInstance() {
		TipoArchivo tipoArchivo = new TipoArchivo();
		return tipoArchivo;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(isIdDefined()){
			unidadMedida();
		}else{
			getInstance().setActivo(true);
			idReferencias=null;
		}
			
	}

	public boolean isWired() {
		return true;
	}

	public TipoArchivo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	
	@Override
	public String persist() {
		try {
			if(!checkData())
				return null;
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
			getInstance().setExtension(getInstance().getExtension().trim().toLowerCase());
			getInstance().setMimetype(getInstance().getMimetype().trim().toLowerCase());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
			Referencias reg= getEntityManager().find(Referencias.class, idReferencias);
			getInstance().setUnidMedida(reg.getDescAbrev());
			getInstance().setUnidMedidaByte(toByte(reg.getDescLarga(), getInstance().getTamanho()));
			getInstance().setFechaAlta(new Date());
			return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setExtension(getInstance().getExtension().trim().toLowerCase());
		getInstance().setMimetype(getInstance().getMimetype().trim().toLowerCase());
		Referencias reg= getEntityManager().find(Referencias.class, idReferencias);
		getInstance().setUnidMedida(reg.getDescAbrev());
		getInstance().setUnidMedidaByte(toByte(reg.getDescLarga(), getInstance().getTamanho()));
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	private Integer toByte(String tipo,Integer tam){
		Integer val=null;
		if(tipo.toLowerCase().contains("kilobyte"))
			val=tam*1024;
		else if(tipo.toLowerCase().contains("megabyte"))
			val=tam*1048576;
		else if(tipo.toLowerCase().contains("gigabyte"))
			val=tam*1073741824;
			
		return val;
	}
	
	 	private boolean checkData(){
	
		if(getInstance().getDescripcion().trim().isEmpty() || getInstance().getExtension().trim().isEmpty()
				|| getInstance().getMimetype().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		
		
		
		
		return true;
	}
	
	private void unidadMedida(){
		referenciasList.getReferencias().setDescAbrev(getInstance().getUnidMedida());
		referenciasList.getReferencias().setDominio("UNIDAD_MEDIDA");
		List<Referencias> referencias=referenciasList.getResultList();
		if(!referencias.isEmpty())
			idReferencias=referencias.get(0).getIdReferencias();
	}
	//Public getters and setters if exists
	public Long getIdReferencias() {
		return idReferencias;
	}

	public void setIdReferencias(Long idReferencias) {
		this.idReferencias = idReferencias;
	}
	
}
