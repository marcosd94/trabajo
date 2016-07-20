package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import enums.Estado;
import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.MatrizReferencialDetList;


/**
 * @author lvergara
 * 
 * */
@Scope(ScopeType.CONVERSATION)
@Name("matrizReferencialDetListFormController")
public class MatrizReferencialDetListFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2991843744538615618L;

	@In(value="entityManager")
    EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@In(create = true)
	MatrizReferencialDetList matrizReferencialDetList;
	
	
	private Long idMatrizReferencialEnc;//PARAMETRO QUE RECIBE
	private MatrizReferencialEnc matrizReferencialEnc= new MatrizReferencialEnc();
	private MatrizReferencialDet matrizReferencialDet= new MatrizReferencialDet();
	private Estado estado= Estado.ACTIVO;
	
	public void init(){
		if(idMatrizReferencialEnc!=null){
			matrizReferencialEnc=em.find(MatrizReferencialEnc.class, idMatrizReferencialEnc);
			matrizReferencialDet.setMatrizReferencialEnc(matrizReferencialEnc);
		}
		search();
		
		
		
		
	}
	
	public void search(){
		matrizReferencialDetList.getMatrizReferencialDet().setDescripcion(matrizReferencialDet.getDescripcion());
		matrizReferencialDetList.setIdMatrizReferencialEnc(matrizReferencialEnc.getIdMatrizReferencialEnc());
		matrizReferencialDetList.setEstado(estado);
		matrizReferencialDetList.listaResultados();
	}
	
	public void searchAll(){
		estado = Estado.TODOS;
		
		matrizReferencialDet.setDescripcion(null);
		matrizReferencialDetList.getMatrizReferencialDet().setDescripcion(null);
		matrizReferencialDetList.setIdMatrizReferencialEnc(matrizReferencialEnc.getIdMatrizReferencialEnc());
		matrizReferencialDetList.limpiar();
	}



	
	
//	GETTERS Y SETTERS
	public Long getIdMatrizReferencialEnc() {
		return idMatrizReferencialEnc;
	}

	public void setIdMatrizReferencialEnc(Long idMatrizReferencialEnc) {
		this.idMatrizReferencialEnc = idMatrizReferencialEnc;
	}

	public MatrizReferencialEnc getMatrizReferencialEnc() {
		return matrizReferencialEnc;
	}

	public void setMatrizReferencialEnc(MatrizReferencialEnc matrizReferencialEnc) {
		this.matrizReferencialEnc = matrizReferencialEnc;
	}

	public MatrizReferencialDet getMatrizReferencialDet() {
		return matrizReferencialDet;
	}

	public void setMatrizReferencialDet(MatrizReferencialDet matrizReferencialDet) {
		this.matrizReferencialDet = matrizReferencialDet;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
	
	
	
	


}
