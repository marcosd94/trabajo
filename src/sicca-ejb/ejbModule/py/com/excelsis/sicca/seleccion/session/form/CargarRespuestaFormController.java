package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ExcepcionDet;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ExcepcionDetHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarRespuestaFormController")
public class CargarRespuestaFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	
	@In(create =  true)
	ExcepcionDetHome excepcionDetHome;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	
	
	private ExcepcionDet excepcionDet;
	private Long idExcepcionDet;

	private boolean fromRpta;
	

	public void init(){
		try {
			excepcionDet= new ExcepcionDet();
			if(idExcepcionDet!=null){
				excepcionDet= em.find(ExcepcionDet.class,idExcepcionDet);
				excepcionDetHome.setInstance(excepcionDet);
			}
			
			if(fromRpta){
				excepcionDet.setFechaRpta(new Date());
				excepcionDetHome.setInstance(new ExcepcionDet());
			}
				
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String save(){
		try {
			excepcionDet.setUsuRpta(usuarioLogueado.getCodigoUsuario());
			excepcionDet.setFechaRpta(new Date());
			excepcionDet.setEnviadoSfp(false);
			em.merge(excepcionDet);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
//	public String update(){
//		try {
//			excepcionDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			excepcionDet.setFechaMod(new Date());
//			em.merge(solicProrrogaDet);
//			em.flush();
//			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
//			return "updated";
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	
	
	
//	METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private Integer nRpta(){
		List<Integer>dets=em.createQuery("Select max(d.nroRpta) from HomologacionPerfilMatrizDet d ").getResultList();

		if(!dets.isEmpty() && dets.get(0)!=null)
			return dets.get(0)+1;
		else
			return 1;
	}
	

//	GETTERS Y SETTERS
	
	

	


	public boolean isFromRpta() {
		return fromRpta;
	}

	public void setFromRpta(boolean fromRpta) {
		this.fromRpta = fromRpta;
	}

	public ExcepcionDet getExcepcionDet() {
		return excepcionDet;
	}

	public void setExcepcionDet(ExcepcionDet excepcionDet) {
		this.excepcionDet = excepcionDet;
	}

	public Long getIdExcepcionDet() {
		return idExcepcionDet;
	}

	public void setIdExcepcionDet(Long idExcepcionDet) {
		this.idExcepcionDet = idExcepcionDet;
	}

	
	

	
	
	
}
