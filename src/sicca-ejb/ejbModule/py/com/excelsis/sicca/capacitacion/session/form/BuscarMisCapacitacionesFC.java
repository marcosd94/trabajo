package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import enums.ModalidadCapaSeleccione;

import py.com.excelsis.sicca.capacitacion.session.PostulacionCapList;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("buscarMisCapacitacionesFC")
public class BuscarMisCapacitacionesFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PostulacionCapList postulacionCapList;


	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	
	@In (create=true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	private ModalidadCapaSeleccione modalidadCapaSeleccione=ModalidadCapaSeleccione.NINGUNO;
	private Long idTipoCapacitacion=null;
	private String nombre;
	private List<PostulacionCap> postulacionCapLista= new ArrayList<PostulacionCap>();
	private Long idPostulacion;
	private String motivo;

	public void init() {
		
		search();
	
	}

	

	public void search() {

		postulacionCapList.getCapacitaciones().setNombre(nombre);
		postulacionCapList.getCapacitaciones().setModalidad(modalidadCapaSeleccione.getValor());
		postulacionCapList.setIdTipoCap(idTipoCapacitacion);
		postulacionCapList.getCapacitaciones().setActivo(true);
		postulacionCapList.setIdPersona(usuarioLogueado.getPersona().getIdPersona());
		postulacionCapLista= postulacionCapList.buscarResultadosCU483();
		
		
	}

	public void searchAll() {
		idTipoCapacitacion=null;
		modalidadCapaSeleccione=ModalidadCapaSeleccione.NINGUNO;
		nombre=null;
		search();
	}

	public void cancelar(Long id) {
		idPostulacion = id;
		PostulacionCap aux = em.find(PostulacionCap.class, idPostulacion);
		motivo = aux.getObsCancelacion();

	}
	private String getQuery(){
		String q=" SELECT postulacionCap FROM PostulacionCap postulacionCap" +
				" JOIN  postulacionCap.capacitacion cap ";
		String where=" cap.tipo";
		
		if(nombre!=null)
			where=" WHERE lower(cap.nombres) like lower '%"+seguridadUtilFormController.caracterInvalido(nombre.toLowerCase())+"%'  ";
		
		if(idTipoCapacitacion!=null)
			where+=" AND cap.datosEspecificosTipoCap.idDatosEspecificos = "+idTipoCapacitacion;
			
		if(modalidadCapaSeleccione.getValor()!=null)
			where+=" AND cap.modalidad = "+modalidadCapaSeleccione.getValor();
			
		return q+where;
	}

	public void abrirDocumento(Long idCapacitacion) {

		try {
			Capacitaciones c= em.find(Capacitaciones.class,idCapacitacion);
			AdmDocAdjuntoFormController.abrirDocumentoFromCU( c.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	public boolean habCancelar(Long idPostulacion){
		PostulacionCap pcap= em.find(PostulacionCap.class, idPostulacion);
		Capacitaciones c= em.find(Capacitaciones.class, pcap.getCapacitacion().getIdCapacitacion());
		Date fechaActual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		String calActual = sdf.format(fechaActual);
		
		if(pcap.getFechaCancelacion()==null){
			if(c.getFechaRecepHasta()!=null){
				String calCapa = sdf.format(c.getFechaRecepHasta());
				if(c.getFechaRecepHasta().after(fechaActual)||calActual.equals(calCapa))
					return true;
			}
			
		}
		
		return false;
	}

	public void guardarMotivo(){
		try {
			if(motivo==null || motivo.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Motivo. Verifique");
				return;
			}
			PostulacionCap aux = em.find(PostulacionCap.class, idPostulacion);
			aux.setObsCancelacion(motivo);
			aux.setUsuCancelacion(usuarioLogueado.getCodigoUsuario());
			aux.setFechaCancelacion(new Date());
			aux.setActivo(false);
			em.merge(aux);
			em.flush();
			searchAll();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			
		}
		
	}
	
	// GETTERS Y SETTERS
	public ModalidadCapaSeleccione getModalidadCapaSeleccione() {
		return modalidadCapaSeleccione;
	}



	public void setModalidadCapaSeleccione(
			ModalidadCapaSeleccione modalidadCapaSeleccione) {
		this.modalidadCapaSeleccione = modalidadCapaSeleccione;
	}



	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}



	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public List<PostulacionCap> getPostulacionCapLista() {
		return postulacionCapLista;
	}



	public void setPostulacionCapLista(List<PostulacionCap> postulacionCapLista) {
		this.postulacionCapLista = postulacionCapLista;
	}



	public Long getIdPostulacion() {
		return idPostulacion;
	}



	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}



	public String getMotivo() {
		return motivo;
	}



	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	

	

	

	
		

	

	

}
