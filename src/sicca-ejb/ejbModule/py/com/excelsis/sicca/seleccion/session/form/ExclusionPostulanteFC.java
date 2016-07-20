package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("exclusionPostulanteFC")
public class ExclusionPostulanteFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1661926467489160485L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	@In(required = false)
	CancelacionPostulanteFormController cancelacionPostulanteFormController;
	
	@In(scope=ScopeType.SESSION, required=false)  
	@Out(scope=ScopeType.SESSION, required=false)  
	String roles; 
	
	
	private String entity = "excepcion";
	private String idEntity = "";
	private String nombrePantalla = "exclusionPostulanteEdit";
	private String ubicacionFisica = "";

	private String documento;
	
	private List<Postulacion> listaPostulacion = new ArrayList<Postulacion>();
	
	private String motivo;
	private Date fecha = new Date();
	private Long idExcepcion;
	private Excepcion excepcion;
	
	private String from;
	private Boolean anexo;
	
	public void init() {
		cargar();
	}

	
	public void initView() {
		try{
			excepcion = em.find(Excepcion.class, idExcepcion);
			grupoPuestosController.setIdConcurso(excepcion.getConcurso().getIdConcurso());
			grupoPuestosController.setConcurso(em.find(Concurso.class, grupoPuestosController.getIdConcurso()));
			grupoPuestosController.setIdConcursoPuestoAgr(excepcion.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			cargar();
			motivo = excepcion.getObservacion();
			fecha = excepcion.getFechaAlta();
	
			buscarPostulantesView();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cargar(){
		cargarDireccionAnexo();
	}
	
	public void cargarDireccionAnexo(){
		if (grupoPuestosController.getIdConcurso() != null && grupoPuestosController.getIdConcursoPuestoAgr() != null){
			Calendar c = Calendar.getInstance();
			Concurso concurso = em.find(Concurso.class, grupoPuestosController.getIdConcurso());
			ubicacionFisica = "\\SICCA\\" + c.get(Calendar.YEAR) + "\\OEE\\" + concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "\\" +
							concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "\\" + 
							concurso.getIdConcurso() + "\\" + grupoPuestosController.getIdConcursoPuestoAgr();
		}
	}


	
	@SuppressWarnings("unchecked")
	public void buscarPostulantes(){
		
		listaPostulacion = new ArrayList<Postulacion>();
		if (grupoPuestosController.getIdConcursoPuestoAgr() != null){
			try {
				String consulta = " " +
					" SELECT postulacion " +
					" FROM Postulacion postulacion" +
		
					" JOIN postulacion.personaPostulante personaPostulante " +
					" JOIN postulacion.concursoPuestoAgr concursoPuestoAgr " +
		
					" WHERE postulacion.activo = :activo " +
					" AND concursoPuestoAgr.idConcursoPuestoAgr = :idConcursoPuestoAgr ";
				
				if (!funcionarioUtilFormController.vacio(documento)){
					consulta += " and personaPostulante.documentoIdentidad like '%" + documento + "%'";
				}
				
				Query q = em.createQuery(consulta);
				q.setParameter("activo", true);
				q.setParameter("idConcursoPuestoAgr", grupoPuestosController.getIdConcursoPuestoAgr());
				
				listaPostulacion = q.getResultList();

			} catch (Exception ex) {
				ex.printStackTrace();
			}	
		}
	}
	
	
	public void buscarTodosPostulantes(){
		documento = null;
		buscarPostulantes();
	}
	
	
	public String save(){
		
		if (!validate())
			return null;
		
		Excepcion excepcion = new Excepcion();
		excepcion.setTipo("EXCLUSION DE POSTULANTES POR OEE");
		Concurso concurso = em.find(Concurso.class, grupoPuestosController.getIdConcurso());
		excepcion.setConcurso(concurso);
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, grupoPuestosController.getIdConcursoPuestoAgr());
		excepcion.setConcursoPuestoAgr(concursoPuestoAgr);
		excepcion.setEstado("REGISTRADO");
		excepcion.setObservacion(motivo);
		excepcion.setActivo(true);
		excepcion.setFechaAlta(fecha);
		excepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(excepcion);
		
		String infoMails = seleccionUtilFormController.getMailsLugaresPresentacionAclaracion(concursoPuestoAgr);
		
		//Actualizar postulacion
		List<Long> idPostulaciones = new ArrayList<Long>();
		for(Postulacion p : listaPostulacion){
			if (p.getSeleccionado() != null && p.getSeleccionado()){
				//p.setActivo(false); 
				p.setObsCancelacion(motivo);
				p.setUsuCancelacion(usuarioLogueado.getCodigoUsuario());
				p.setFechaCancelacion(fecha);
				p.setEstadoPostulacion(Postulacion.ESTADO_POSTULACION_EXCLUIDO);
				p.setSeleccionado(false);
				p.setExcepcion(excepcion);
				em.merge(p);
				
				Persona persona = p.getPersonaPostulante().getPersona();
				String asunto = getAsuntoMail(concurso);
				String contenido = getContenidoMail(persona, excepcion, infoMails);
				seleccionUtilFormController.enviarMails(p.getPersonaPostulante().getEMail(), contenido, asunto);
				
				idPostulaciones.add(p.getIdPostulacion());
			}
		}
		
		if(irCU430(concursoPuestoAgr)){
			//Llamar CU 430
			cancelacionPostulanteFormController = (CancelacionPostulanteFormController) Component.getInstance(CancelacionPostulanteFormController.class, true);
			cancelacionPostulanteFormController.init();
			cancelacionPostulanteFormController.cancelarPostulaciones(idPostulaciones);
		}

		em.flush();
		
		idExcepcion = excepcion.getIdExcepcion();
		cargar();
		
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			
		return null;
	}
	
	
	


	/**
	 * Valida el formaulario de carga
	 * 
	 * @return
	 */
	private boolean validate() {
		statusMessages.clear();
		String mensaje = "";

		if (grupoPuestosController.getIdConcurso() == null) {
			mensaje = "Concurso es un campo requerido. Verifique.";
		} 
		else if (grupoPuestosController.getIdConcursoPuestoAgr() == null) {
			mensaje = "Grupo de Puestos es un campo requerido. Verifique.";
		} 
		else if (funcionarioUtilFormController.vacio(motivo)) {
			mensaje = "Motivo es un campo requerido. Verifique.";
		} 
		else if (!existeSeleccionado()) {
			mensaje = "Debe seleccionar al menos un postulante. Verifique.";
		} 
		
		
		if (!funcionarioUtilFormController.vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.add(Severity.ERROR, mensaje);
			return false;
		}

		return true;
	}
	

	/**
	 * Verifica si alguien fue seleccionado o no para ser cancelado
	 * @return
	 */
	private boolean existeSeleccionado() {
		if (listaPostulacion != null){
			for(Postulacion p : listaPostulacion){
				if (p.getSeleccionado() != null && p.getSeleccionado())
					return true;
			}
		}
		
		return false;
	}

	
	private String getAsuntoMail(Concurso concurso) {
		ConfiguracionUoCab configuracionUoCab = concurso.getConfiguracionUoCab();
		String asunto = "(";
		asunto += (configuracionUoCab.getDescripcionCorta() == null ? configuracionUoCab.getDenominacionUnidad() : configuracionUoCab.getDescripcionCorta());
		asunto += ") – EXCLUSIÓN DE POSTULANTE";
		return asunto;
	}
	
	
	private String getContenidoMail(Persona p, Excepcion excepcion, String infoMails) {
		String texto =
				"<p align=\"justify\"> <b> Estimado/a " + p.getNombreCompleto() + " </b>"
				+ "<p></p> "
				+ " Se ha registrado la exclusi&oacute;n de su postulaci&oacute;n para el Concurso: " 
				+ "<p></p> "  
				+ "<b>" 
				+ "<p>" + excepcion.getConcurso().getNumero() + " " + excepcion.getConcurso().getAnhio() + " de "
				+ (excepcion.getConcurso().getConfiguracionUoCab().getDescripcionCorta() == null? "" : excepcion.getConcurso().getConfiguracionUoCab().getDescripcionCorta())
				+ " " + excepcion.getConcurso().getNombre() + "</p>"
				+ "<p></p> "  
				+ "<p>" + excepcion.getConcursoPuestoAgr().getDescripcionGrupo() + "</p> "
				+ "</b>"
				+ "<p></p> "  
				+ "<p>Motivo: <b>" + excepcion.getObservacion() + "</b></p> "
				+ "<p></p> "
				+ "<p></p> "
				+ "<p> Atentamente,</p> "
				+ "<b><p>" + excepcion.getConcurso().getConfiguracionUoCab().getDenominacionUnidad() + "</p></b>";
		
		if (!funcionarioUtilFormController.vacio(infoMails)){
			texto += " <p> </p>"+ "Para mayor informaci&oacute;n comunicarse con: " + infoMails;
        }
		
		return texto;
	}
	
	
	@SuppressWarnings("unchecked")
	public void buscarPostulantesView(){
		listaPostulacion = new ArrayList<Postulacion>();
		if (grupoPuestosController.getIdConcursoPuestoAgr() != null){
			try {
				String consulta = " " +
					" SELECT postulacion " +
					" FROM Postulacion postulacion" +
		
					" JOIN postulacion.personaPostulante personaPostulante " +
					" JOIN postulacion.excepcion excepcion " +
		
					" WHERE excepcion.idExcepcion = :idExcepcion ";
				
				
				Query q = em.createQuery(consulta);
				q.setParameter("idExcepcion", idExcepcion);
				
				listaPostulacion = q.getResultList();

			} catch (Exception ex) {
				ex.printStackTrace();
			}	
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private boolean irCU430(ConcursoPuestoAgr concursoPuestoAgr) {
		String consulta = "" +
			" SELECT r.* " +
			" FROM seleccion.referencias r " +
			" where dominio = 'ESTADOS_GRUPO' " +
			"     and r.desc_abrev in ('LISTA CORTA', 'EVALUACION DOCUMENTAL ADJ', 'CON RESOLUCION ADJUDICACION', 'ADJUDICADO', " +
			"         'PERMANENTE D12', 'PERMANENTE N12', 'CONTRATADOS', 'FIRMADO NOMBRAMIENTO', 'CON RESOLUCION NOMBRAMIENTO') " +
			"     and r.valor_num = " + concursoPuestoAgr.getEstado();
		
		List<Referencias> lista = em.createNativeQuery(consulta, Referencias.class).getResultList();
		if(lista != null && lista.size() > 0)
			return true;
		
		return false;
	}

	
	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}


	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}


	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}


	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}




	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	public String getMotivo() {
		return motivo;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}


	public Date getFecha() {
		return fecha;
	}


	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}


	public Long getIdExcepcion() {
		return idExcepcion;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public String getFrom() {
		if (funcionarioUtilFormController.vacio(from))
			return "/";
		return from;
	}


	public void setDocumento(String documento) {
		this.documento = documento;
	}


	public String getDocumento() {
		return documento;
	}


	public void setListaPostulacion(List<Postulacion> listaPostulacion) {
		this.listaPostulacion = listaPostulacion;
	}


	public List<Postulacion> getListaPostulacion() {
		return listaPostulacion;
	}


	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}


	public Excepcion getExcepcion() {
		return excepcion;
	}


	public void setAnexo(Boolean anexo) {
		this.anexo = anexo;
	}


	public Boolean getAnexo() {
		return anexo;
	}

	
}
