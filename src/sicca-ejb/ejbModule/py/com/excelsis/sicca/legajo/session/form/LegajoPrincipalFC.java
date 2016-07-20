package py.com.excelsis.sicca.legajo.session.form;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.legajo.session.AdmDatosFamilia644FC;
import py.com.excelsis.sicca.legajo.session.AdmDeclaraJuradas656FC;
import py.com.excelsis.sicca.legajo.session.AdmPermisos655FC;
import py.com.excelsis.sicca.legajo.session.AdmVacaciones654FC;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.Tab4AdjuntarDocumentosFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("legajoPrincipalFC")
public class LegajoPrincipalFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true, required = false)
	DatosPersonalesLegajoFC datosPersonalesLegajoFC;
	@In(create = true, required = false)
	Tab4AdjuntarDocumentosFormController tab4AdjuntarDocumentosFormController;
	@In(create = true, required = false)
	DiscapacidadLegajoFC discapacidadLegajoFC;
	@In(create = true, required = false)
	ExperienciaLaboralLegajoFC experienciaLaboralLegajoFC;
	@In(create = true, required = false)
	EstudiosRealizadosLegajoFC estudiosRealizadosLegajoFC;
	@In(create = true, required = false)
	DesvinculacionLegajoFC desvinculacionLegajoFC;
	@In(create = true, required = false)
	EvaluacionesLegajoFC evaluacionesLegajoFC;
	@In(create = true, required = false)
	ProcesosJuridicosLegajoFC procesosJuridicosLegajoFC;
	@In(create = true, required = false)
	PremiosReconocimientosFC premiosReconocimientosFC;
	@In(create = true, required = false)
	AdmAmonestacionesFC admAmonestacionesFC;
	@In(create = true, required = false)
	DatosSaludLegajoFC datosSaludLegajoFC;
	@In(create = true, required = false)
	AdmCapacitacionesLegajos648FC admCapacitacionesLegajos648FC;
	@In(create = true, required = false)
	VistaPreviaLegajoFC vistaPreviaLegajoFC;
	@In(create = true, required = false)
	AdmVacaciones654FC admVacaciones654FC;
	@In(create = true, required = false)
	AdmPermisos655FC admPermisos655FC;
	@In(create = true, required = false)
	AdmDeclaraJuradas656FC admDeclaraJuradas656FC;
	@In(create = true, required = false)
	AdmDatosFamilia644FC admDatosFamilia644FC;
	@In(create = true, required = false)
	AdmRecorridoLaboralFC admRecorridoLaboralFC;

	private Long idPersona;
	private Long personaIdPersona;
	private Long idPersonaFamiliar;
	private String from;
	private String current = null;
	private String texto;
	private String nroDoc;
	private String accion;
	private Persona persona;
	private Boolean show643 = false;
	private Boolean showCargaDocumentos = false;
	private Boolean show644 = false;
	private Boolean show645 = false;
	private Boolean show646 = false;
	private Boolean show647 = false;
	private Boolean show648 = false;
	private Boolean show649 = false;
	private Boolean show650 = false;
	private Boolean show651 = false;
	private Boolean show652 = false;
	private Boolean show653 = false;
	private Boolean show654 = false;
	private Boolean show655 = false;
	private Boolean show656 = false;
	private Boolean show657 = false;
	private Boolean show658 = false;
	private Boolean show659 = false;
	private Boolean show660 = false;
	
	private String uploadedFileToString;
	private String contentType;
	private String fileName;
	
	

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
			persona = em.find(Persona.class, idPersona);
		if ((texto == null || texto.equals("P")) && current == null)
			updateCurrent("LINK_643");
		else if (texto.equals("G") && current == null && accion.equalsIgnoreCase("VER"))
			updateCurrent("LINK_659");
		else if (texto.equals("G") && current == null && accion.equalsIgnoreCase("EDITAR"))
			updateCurrent("LINK_643");
		else if (texto.equals("GShow644") && current == null && accion.equalsIgnoreCase("EDITAR")){
			texto = "G";
			updateCurrent("LINK_644");
		}
		else if (texto.equals("GShow647") && current == null && accion.equalsIgnoreCase("EDITAR")){
				texto = "G";
				updateCurrent("LINK_647");
		}else if (texto.equals("GShow651") && current == null && accion.equalsIgnoreCase("EDITAR")){
			texto = "G";
			updateCurrent("LINK_651");
		}
		
		
	}

	public void updateCurrent(String current) {
		if (current.equalsIgnoreCase("LINK_643")) {
			cancelar();
			show643 = true;
			datosPersonalesLegajoFC.setIdPersona(idPersona);
			datosPersonalesLegajoFC.setTexto(texto);
			datosPersonalesLegajoFC.init();
			
		
		}
		
		
		if (current.equalsIgnoreCase("showCargaDocumentos")) {
			cancelar();
			showCargaDocumentos = true;
			tab4AdjuntarDocumentosFormController.setIdPersona(idPersona);
			tab4AdjuntarDocumentosFormController.setTexto(texto);
			tab4AdjuntarDocumentosFormController.initLegajo();
			
		
		}
		
		if (current.equalsIgnoreCase("LINK_644")) {
			cancelar();
			show644 = true;
			admDatosFamilia644FC.setIdPersona(idPersona);
			admDatosFamilia644FC.setNroDoc(nroDoc);
			if(idPersonaFamiliar != null)
				admDatosFamilia644FC.setIdPersonaFamiliar(idPersonaFamiliar);
				
				try {
					if(nroDoc != null)
						admDatosFamilia644FC.buscarPersona();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			admDatosFamilia644FC.setTexto(texto);
			admDatosFamilia644FC.init();
		}
		if (current.equalsIgnoreCase("LINK_645")) {
			cancelar();
			show645 = true;
			datosSaludLegajoFC.setIdPersona(idPersona);
			datosSaludLegajoFC.setTexto(texto);
			datosSaludLegajoFC.init();

		}

		if (current.equalsIgnoreCase("LINK_646")) {
			cancelar();
			show646 = true;
			discapacidadLegajoFC.setIdPersona(idPersona);
			discapacidadLegajoFC.setTexto(texto);
			discapacidadLegajoFC.init();

		}
		if (current.equalsIgnoreCase("LINK_647")) {
			cancelar();
			show647 = true;
			estudiosRealizadosLegajoFC.setIdPersona(idPersona);
			estudiosRealizadosLegajoFC.setTexto(texto);
			estudiosRealizadosLegajoFC.init();

		}
		if (current.equalsIgnoreCase("LINK_648")) {
			cancelar();
			show648 = true;
			admCapacitacionesLegajos648FC.setIdPersona(idPersona);
			admCapacitacionesLegajos648FC.init();

		}
		if (current.equalsIgnoreCase("LINK_649")) {
			cancelar();
			show649 = true;
			evaluacionesLegajoFC.setIdPersona(idPersona);
			evaluacionesLegajoFC.setTexto(texto);
			evaluacionesLegajoFC.init();

		}
		if (current.equalsIgnoreCase("LINK_650")) {
			cancelar();
			show650 = true;
			experienciaLaboralLegajoFC.setIdPersona(idPersona);
			experienciaLaboralLegajoFC.setTexto(texto);
			experienciaLaboralLegajoFC.init();

		}
		if (current.equalsIgnoreCase("LINK_651")) {
			cancelar();
			show651 = true;
			admRecorridoLaboralFC.setIdPersona(idPersona);
			admRecorridoLaboralFC.setTexto(texto);
			admRecorridoLaboralFC.init();

		}
		if (current.equalsIgnoreCase("LINK_652")) {
			cancelar();
			show652 = true;
			procesosJuridicosLegajoFC.setIdPersona(idPersona);
			procesosJuridicosLegajoFC.setTexto(texto);
			procesosJuridicosLegajoFC.init();

		}
		if (current.equalsIgnoreCase("LINK_653")) {
			cancelar();
			show653 = true;
			desvinculacionLegajoFC.setIdPersona(idPersona);
			desvinculacionLegajoFC.setTexto(texto);
			desvinculacionLegajoFC.init();

		}

		if (current.equalsIgnoreCase("LINK_654")) {
			cancelar();
			show654 = true;
			admVacaciones654FC.setIdPersona(idPersona);
			admVacaciones654FC.setTexto(texto);
			admVacaciones654FC.init();

		}
		if (current.equalsIgnoreCase("LINK_655")) {
			cancelar();
			show655 = true;
			admPermisos655FC.setIdPersona(idPersona);
			admPermisos655FC.setTexto(texto);
			admPermisos655FC.init();

		}
		if (current.equalsIgnoreCase("LINK_656")) {
			cancelar();
			show656 = true;
			admDeclaraJuradas656FC.setIdPersona(idPersona);
			admDeclaraJuradas656FC.setTexto(texto);
			admDeclaraJuradas656FC.init();
		}

		if (current.equalsIgnoreCase("LINK_657")) {
			cancelar();
			show657 = true;
			premiosReconocimientosFC.setIdPersona(idPersona);
			premiosReconocimientosFC.setTexto(texto);
			premiosReconocimientosFC.init();

		}

		if (current.equalsIgnoreCase("LINK_658")) {
			cancelar();
			show658 = true;
			admAmonestacionesFC.setIdPersona(idPersona);
			admAmonestacionesFC.setTexto(texto);
			admAmonestacionesFC.init();
		}

		if (current.equalsIgnoreCase("LINK_659")) {
			cancelar();
			show659 = true;
			vistaPreviaLegajoFC.setIdPersona(idPersona);
			vistaPreviaLegajoFC.setTexto(texto);
			vistaPreviaLegajoFC.init();
		}
		if (current.equalsIgnoreCase("LINK_660")) {
			cancelar();
			show660 = true;
			admRecorridoLaboralFC.setIdPersona(idPersona);
			admRecorridoLaboralFC.setTexto(texto);
			admRecorridoLaboralFC.init2();
		}
	}

	
	public Boolean desdeCuentaUsuarioPortal(){
		if(this.from != null && this.from.equals("cuentaUsuarioPortal"))
			return true;
		else 
			return false;
	}
	
	public void cancelar() {
		current = null;
		show643 = false;
		showCargaDocumentos = false;
		show644 = false;
		show645 = false;
		show646 = false;
		show647 = false;
		show648 = false;
		show649 = false;
		show650 = false;
		show651 = false;
		show652 = false;
		show653 = false;
		show654 = false;
		show655 = false;
		show656 = false;
		show657 = false;
		show658 = false;
		show659 = false;
		show660 = false;

	}

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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public Boolean getShow643() {
		return show643;
	}

	public void setShow643(Boolean show643) {
		this.show643 = show643;
	}

	public Boolean getShow644() {
		return show644;
	}

	public void setShow644(Boolean show644) {
		this.show644 = show644;
	}

	public Boolean getShow645() {
		return show645;
	}

	public void setShow645(Boolean show645) {
		this.show645 = show645;
	}

	public Boolean getShow646() {
		return show646;
	}

	public void setShow646(Boolean show646) {
		this.show646 = show646;
	}

	public Boolean getShow647() {
		return show647;
	}

	public void setShow647(Boolean show647) {
		this.show647 = show647;
	}

	public Boolean getShow648() {
		return show648;
	}

	public void setShow648(Boolean show648) {
		this.show648 = show648;
	}

	public Boolean getShow649() {
		return show649;
	}

	public void setShow649(Boolean show649) {
		this.show649 = show649;
	}

	public Boolean getShow650() {
		return show650;
	}

	public void setShow650(Boolean show650) {
		this.show650 = show650;
	}

	public Boolean getShow651() {
		return show651;
	}

	public void setShow651(Boolean show651) {
		this.show651 = show651;
	}

	public Boolean getShow652() {
		return show652;
	}

	public void setShow652(Boolean show652) {
		this.show652 = show652;
	}

	public Boolean getShow653() {
		return show653;
	}

	public void setShow653(Boolean show653) {
		this.show653 = show653;
	}

	public Boolean getShow654() {
		return show654;
	}

	public void setShow654(Boolean show654) {
		this.show654 = show654;
	}

	public Boolean getShow655() {
		return show655;
	}

	public void setShow655(Boolean show655) {
		this.show655 = show655;
	}

	public Boolean getShow656() {
		return show656;
	}

	public void setShow656(Boolean show656) {
		this.show656 = show656;
	}

	public Boolean getShow657() {
		return show657;
	}

	public void setShow657(Boolean show657) {
		this.show657 = show657;
	}

	public Boolean getShow658() {
		return show658;
	}

	public void setShow658(Boolean show658) {
		this.show658 = show658;
	}

	public Boolean getShow659() {
		return show659;
	}

	public void setShow659(Boolean show659) {
		this.show659 = show659;
	}
	public Boolean getShow660() {
		return show660;
	}

	public void setShow660(Boolean show660) {
		this.show660 = show660;
	}
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public Boolean getShowCargaDocumentos() {
		return showCargaDocumentos;
	}

	public void setShowCargaDocumentos(Boolean showCargaDocumentos) {
		this.showCargaDocumentos = showCargaDocumentos;
	}

	public String getUploadedFileToString() {
		return uploadedFileToString;
	}

	public void setUploadedFileToString(String uploadedFileToString) {
		this.uploadedFileToString = uploadedFileToString;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNroDoc() {
		return nroDoc;
	}

	public void setNroDoc(String nroDoc) {
		this.nroDoc = nroDoc;
	}
	
	public Long getPersonaIdPersona() {
		return personaIdPersona;
	}

	public void setPersonaIdPersona(Long personaIdPersona) {
		this.personaIdPersona = personaIdPersona;
	}
	
	public Long getIdPersonaFamiliar() {
		return idPersonaFamiliar;
	}

	public void setIdPersonaFamiliar(Long idPersonaFamiliar) {
		this.idPersonaFamiliar = idPersonaFamiliar;
	}

}
