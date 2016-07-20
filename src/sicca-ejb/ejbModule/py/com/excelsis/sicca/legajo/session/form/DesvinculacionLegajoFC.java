package py.com.excelsis.sicca.legajo.session.form;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

@Scope(ScopeType.CONVERSATION)
@Name("desvinculacionLegajoFC")
public class DesvinculacionLegajoFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -788040635466613316L;

	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(value="entityManager")
    EntityManager em;
	@In
	StatusMessages statusMessages;
	

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;


	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	
	private Long idPersona;
	private String texto;
	private Persona persona= new Persona();
	private List<Desvinculacion> desvinculacionLista= new Vector<Desvinculacion>();
	private List<Inhabilitados> inhabilitadoLista= new Vector<Inhabilitados>();
	private List<Jubilados> jubiladoLista= new Vector<Jubilados>();
	
	public void init(){
		try {
			persona=em.find(Persona.class, idPersona);
			cargarDesvinculacion();
			cargarInhabilitado();
			cargarJubilados();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void cargarDesvinculacion(){
		desvinculacionLista=em.createQuery("Select d from Desvinculacion d" +
				" where  d.empleadoPuesto.persona.idPersona=:idPersona").setParameter("idPersona", idPersona).getResultList();
	}
	@SuppressWarnings("unchecked")
	private void cargarInhabilitado(){
		inhabilitadoLista=em.createQuery("Select i from Inhabilitados i " +
				" where i.persona.idPersona=:idPersona").setParameter("idPersona", idPersona).getResultList();
	}
	@SuppressWarnings("unchecked")
	private void cargarJubilados(){
		jubiladoLista=em.createQuery("Select j from Jubilados j   " +
				" where j.persona.idPersona=:idPersona").setParameter("idPersona",idPersona).getResultList();
	}
	
	
	public String verAnexos(Long id ,String tipo){
		
		String url = "/seleccion/adminDocAdjunto/AdmDocAdjunto.seam";
		String nombrePantalla = "";
		String idCU = id.toString();//RECIBE DE ACUERDO A EL PANEL SELECCIONADO
		String nombreTabla = "";
		String mostrarDoc = "false";
		String isEdit = "false";
		String from = "legajos/MenuDatosPersonales";

		String pagina =
			url + "?mostrarDoc=" + mostrarDoc + "&isEdit=" + isEdit + "&from=" + from;
		
		if(tipo.equals("D")){
			/** DESDE EL PANEL DESVINCULACION*/
			nombrePantalla="desvinculacion_edit";
			nombreTabla="Desvinculacion";
			
		}else if(tipo.equals("I")){
			/** DESDE EL PANEL INHABILITADO*/
			nombrePantalla="admInhabilitadosSFP";
			nombreTabla="inhabilitados";
			
		}else{
			/** DESDE EL PANEL JUBILACION*/
			nombrePantalla="admJubiladosSFP";
			nombreTabla="jubilados";
		}
		
			pagina +=
				"&nombrePantalla=" + nombrePantalla + "&idCU=" + idCU + "&nombreTabla="
					+ nombreTabla;
			return pagina;

		
	}
	
	
	

	
	

	
	
//	GETTERS Y SETTERS

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public List<Desvinculacion> getDesvinculacionLista() {
		return desvinculacionLista;
	}

	public void setDesvinculacionLista(List<Desvinculacion> desvinculacionLista) {
		this.desvinculacionLista = desvinculacionLista;
	}

	public List<Inhabilitados> getInhabilitadoLista() {
		return inhabilitadoLista;
	}

	public void setInhabilitadoLista(List<Inhabilitados> inhabilitadoLista) {
		this.inhabilitadoLista = inhabilitadoLista;
	}

	public List<Jubilados> getJubiladoLista() {
		return jubiladoLista;
	}

	public void setJubiladoLista(List<Jubilados> jubiladoLista) {
		this.jubiladoLista = jubiladoLista;
	}
	
	
}
