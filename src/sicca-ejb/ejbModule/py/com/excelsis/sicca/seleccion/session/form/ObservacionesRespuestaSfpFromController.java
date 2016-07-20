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

import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("observacionesRespuestaSfpFromController")
public class ObservacionesRespuestaSfpFromController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	
	@In(create =  true)
	SolicProrrogaDetHome solicProrrogaDetHome;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	
	private Long idSolicDet;
	private SolicProrrogaDet solicProrrogaDet;
	private Long idConcursoPuestoAgr;
	private Long idSolicCab;

	private boolean fromRpta;
	

	public void init(){
		try {
			solicProrrogaDet= new SolicProrrogaDet();
			if(idSolicDet!=null){
				solicProrrogaDet= em.find(SolicProrrogaDet.class,idSolicDet);
				solicProrrogaDetHome.setInstance(solicProrrogaDet);
			}
			
			if(fromRpta){
				solicProrrogaDet.setFechaRpta(new Date());
				solicProrrogaDetHome.setInstance(new SolicProrrogaDet());
				solicProrrogaDet.setAceptaSfp(true);
			}
				
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String save(){
		try {
			solicProrrogaDet.setUsuRpta(usuarioLogueado.getCodigoUsuario());
			solicProrrogaDet.setFechaRpta(new Date());
			solicProrrogaDet.setEnviadoSfp(false);
			solicProrrogaDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			solicProrrogaDet.setFechaMod(new Date());
			em.merge(solicProrrogaDet);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String update(){
		try {
			solicProrrogaDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			solicProrrogaDet.setFechaMod(new Date());
			em.merge(solicProrrogaDet);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	
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
	
	

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}


	public boolean isFromRpta() {
		return fromRpta;
	}

	public void setFromRpta(boolean fromRpta) {
		this.fromRpta = fromRpta;
	}

	public Long getIdSolicDet() {
		return idSolicDet;
	}

	public void setIdSolicDet(Long idSolicDet) {
		this.idSolicDet = idSolicDet;
	}

	public SolicProrrogaDet getSolicProrrogaDet() {
		return solicProrrogaDet;
	}

	public void setSolicProrrogaDet(SolicProrrogaDet solicProrrogaDet) {
		this.solicProrrogaDet = solicProrrogaDet;
	}

	public Long getIdSolicCab() {
		return idSolicCab;
	}

	public void setIdSolicCab(Long idSolicCab) {
		this.idSolicCab = idSolicCab;
	}
	
	

	
	
	
}
