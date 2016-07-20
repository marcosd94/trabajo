package py.com.excelsis.sicca.session.form;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.TransicionEstado;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.TransicionEstadoHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("transicionEstadoFormController")
@Scope(ScopeType.PAGE)
public class TransicionEstadoFormController implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;


	@In(create = true)
	TransicionEstadoHome transicionEstadoHome;

	
	private Long idTrancisionEstado;
	private TransicionEstado transicionEstado;
	private String mensaje;
	private List<EstadoCab> posiblesEstados;
	private Long idEstadoCab;
	private Long idEstadoPosible;
	private List<EstadoCab> removeCollection= new ArrayList<EstadoCab>();
	







	public void init() {
		transicionEstado = new TransicionEstado();
		idEstadoCab=null;
		posiblesEstados= new ArrayList<EstadoCab>();
		if (idTrancisionEstado != null) {
			transicionEstado = em.find(TransicionEstado.class,
					idTrancisionEstado);
			transicionEstadoHome.setInstance(transicionEstado);
			idEstadoCab=transicionEstado.getEstadoCabByIdEstadoCab().getIdEstadoCab();
			
		} 

	}
	

	

	public String update() {
		try {
			EstadoCab estCab= em.find(EstadoCab.class,idEstadoCab);
			transicionEstado.setFechaAlta(new Date());
			transicionEstado.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			transicionEstado.setEstadoCabByIdEstadoCab(estCab);
			transicionEstado.setEstadoCabByPosibleEstado(em.find(EstadoCab.class, idEstadoPosible));
			em.merge(transicionEstado);
			em.flush();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		mensaje = "Registro actualizado con exito";
		statusMessages.add(Severity.INFO, mensaje);
		return "updated";
	}

	public String save() {
		try {
			EstadoCab estCab= em.find(EstadoCab.class,idEstadoCab);
			TransicionEstado aux= new TransicionEstado();
			aux.setFechaAlta(new Date());
			aux.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			aux.setEstadoCabByIdEstadoCab(estCab);
			aux.setEstadoCabByPosibleEstado(em.find(EstadoCab.class, idEstadoPosible));
			em.persist(aux);
			em.flush();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		mensaje = "Registro creado con exito";
		statusMessages.add(Severity.INFO, mensaje);
		return "persisted";
	}
	public String remove() {
		try {
			
			em.remove(transicionEstado);
			em.flush();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		mensaje = "Registro eliminado con exito";
		statusMessages.add(Severity.INFO, mensaje);
		return "persisted";
	}
	
	
	public void addPosible(){
		EstadoCab aux= new EstadoCab();
		aux= em.find(EstadoCab.class, idEstadoPosible);
		posiblesEstados.add(aux);
		idEstadoPosible= null;
	}

	public void eliminarPosible(int index){
		try {
			
			  EstadoCab element = posiblesEstados.get(index);
		        if(element.getIdEstadoCab()!=null)
		        {
		            removeCollection.add(element);
		        }
		        posiblesEstados.remove(element);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	
	
	// GETTER Y SETTER

	public List<EstadoCab> getPosiblesEstados() {
		return posiblesEstados;
	}




	public void setPosiblesEstados(List<EstadoCab> posiblesEstados) {
		this.posiblesEstados = posiblesEstados;
	}




	public Long getIdEstadoCab() {
		return idEstadoCab;
	}




	public void setIdEstadoCab(Long idEstadoCab) {
		this.idEstadoCab = idEstadoCab;
	}




	public Long getIdEstadoPosible() {
		return idEstadoPosible;
	}




	public void setIdEstadoPosible(Long idEstadoPosible) {
		this.idEstadoPosible = idEstadoPosible;
	}

	
}
