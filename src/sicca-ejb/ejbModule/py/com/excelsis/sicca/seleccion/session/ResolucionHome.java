package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

@Name("resolucionHomologacionHome")
public class ResolucionHome extends
		EntityHome<Resolucion> {

	@In(create = true)
	PlantillaResolucionHome plantillaResolucionHomologacionHome;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "resolucionHomologacions";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	
	private List<Tags> tagList= new ArrayList<Tags>();
	private String fechaActual;
	private Resolucion homologacionVp;
	
	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<Resolucion> getResolucionHomologacions() {
		try {
			return getEntityManager().createQuery(
					" select o from " + Resolucion.class.getName() + " o")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Resolucion>();
		}
	}

	
	
	public void setResolucionHomologacionIdResolucionHomologacion(Long id) {
		setId(id);
	}

	public Long getResolucionHomologacionIdResolucionHomologacion() {
		return (Long) getId();
	}

	@Override
	protected Resolucion createInstance() {
		Resolucion resolucionHomologacion = new Resolucion();
		return resolucionHomologacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantillaResolucion plantillaResolucionHomologacion = plantillaResolucionHomologacionHome
				.getDefinedInstance();
		if (plantillaResolucionHomologacion != null) {
			getInstance().setPlantillaResolucionHomologacion(
					plantillaResolucionHomologacion);
		}
		
		findTags();
		findFecha();
	}

	public boolean isWired() {
//		if (getInstance().getPlantillaResolucionHomologacion() == null)
//			return false;
		return true;
	}

	

	public Resolucion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ConcursoPuestoAgr> getConcursoPuestoAgrs() {
		return getInstance() == null ? null : new ArrayList<ConcursoPuestoAgr>(
				getInstance().getConcursoPuestoAgrs());
	}

	public String save(){
		if(!isIdDefined()){
			return persist();
		}
		return update();
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		
	
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		
		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}
	
	
	@SuppressWarnings("unchecked")
	private void findTags(){
		tagList= getEntityManager().createQuery("Select t from Tags t " +
				" order by t.descripcion ").getResultList();
		homologacionVp= new Resolucion();
		
		
	}
	
	private void findFecha(){
		String mes="";
		Date today = new Date();
		switch (today.getMonth()) {
		case 0:
			mes="enero";	
			break;
		case 1:
			mes="febrero";	
			break;
			
		case 2:
			mes="marzo";	
			break;

		case 3:
			mes="abril";	
			break;

		case 4:
			mes="mayo";	
			break;

		case 5:
			mes="junio";	
			break;

		case 6:
			mes="julio";	
			break;
			
		case 7:
			mes="agosto";	
			break;
			
		case 8:
			mes="setiembre";	
			break;
			
		case 9:
			mes="octubre";	
			break;
			
		case 10:
			mes="noviembre";	
			break;
			
		case 11:
			mes="diciembre";	
			break;
			
		default:
			mes="<No definido>";
			break;
		}
		fechaActual="Asuncion, "+today.getDate()+" de"+mes+" de "+today.getYear()+1900;
		
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
