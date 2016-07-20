package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import enums.Estado;
import py.com.excelsis.sicca.entity.AgrupamientoCce;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.session.AgrupamientoCceList;
import py.com.excelsis.sicca.session.GradoSalarialList;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.PAGE)
@Name("gradoSalarialListFormController")
public class GradoSalarialListFormController implements Serializable{

	

	/**
	 * 
	 */
//	private static final long serialVersionUID = -6207533447217264990L;
	
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	@In(create = true)
	GradoSalarialList gradoSalarialList;
	
	@In(create=true)
	AgrupamientoCceList agrupamientoCceList;

	private GradoSalarial gradoSalarial = new GradoSalarial();
	private Estado estado= Estado.ACTIVO;
	private String descripcion;
	private Long idNivelGradoSalarial;
	private Long idTipoCce;
	List<AgrupamientoCce> niveles;
	
	

	public void init(){
		search();
	}
	
	public void search(){
		if(idTipoCce!=null){
			gradoSalarialList.setIdTipoCce(idTipoCce);
		}
		if(idNivelGradoSalarial!=null){
			gradoSalarialList.setIdNivelGradoSalarial(idNivelGradoSalarial);
		}
		if(gradoSalarial.getNumero()!=null)
			gradoSalarialList.getGradoSalarial().setNumero(gradoSalarial.getNumero());
		if(descripcion!=null && !descripcion.trim().equals(""))
			gradoSalarialList.setDescripcion(descripcion);
		gradoSalarialList.setEstado(estado);
		gradoSalarialList.listaResultados();
	}
	
	public void searchAll(){
		gradoSalarial= new GradoSalarial();
		estado = Estado.ACTIVO;
		descripcion= null;
		idNivelGradoSalarial=null;
		idTipoCce=null;
		gradoSalarialList.getGradoSalarial().setAgrupamientoCce(null);
		gradoSalarialList.getGradoSalarial().setNumero(null);
		gradoSalarialList.setDescripcion(null);
		gradoSalarialList.limpiar();
	}
	

	public void updatesNivel(){
		if (idTipoCce == null) {
			niveles= new ArrayList<AgrupamientoCce>();
        }else{
        	niveles= new ArrayList<AgrupamientoCce>();
    		agrupamientoCceList.setIdTipoCce(idTipoCce);
    		niveles=agrupamientoCceList.getResultList();
         }
        
		
	}
	public List<SelectItem> getNivelesTipoCeeSelectItem() {
	        if (niveles == null) {
	            updatesNivel();
	        }
	        List<SelectItem> si = new Vector<SelectItem>();
			si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for(AgrupamientoCce o : niveles)
				si.add(new SelectItem(o.getIdAgrupamientoCce(),"" + o.getDescripcion()));
			return si;
	    }

	

//	GETTERS Y SETTERS

	public GradoSalarial getGradoSalarial() {
		return gradoSalarial;
	}

	public void setGradoSalarial(GradoSalarial gradoSalarial) {
		this.gradoSalarial = gradoSalarial;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdNivelGradoSalarial() {
		return idNivelGradoSalarial;
	}

	public void setIdNivelGradoSalarial(Long idNivelGradoSalarial) {
		this.idNivelGradoSalarial = idNivelGradoSalarial;
	}

	public Long getIdTipoCce() {
		return idTipoCce;
	}

	public void setIdTipoCce(Long idTipoCce) {
		this.idTipoCce = idTipoCce;
	}
	    
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
}
