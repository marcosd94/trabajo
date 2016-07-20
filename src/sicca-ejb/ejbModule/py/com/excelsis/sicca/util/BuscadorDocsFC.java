package py.com.excelsis.sicca.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

@Scope(ScopeType.CONVERSATION)
@Name("buscadorDocsFC")
public class BuscadorDocsFC {
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	@In(required = false)
	Usuario usuarioLogueado;

	private Integer nroDoc;
	private Date fechaDoc;
	private Long idTipoDoc;
	private String fNameMostrar;
	private Documentos docDecreto;
	private Documentos docActoAdmin;
	private UploadItem fileActoAdmin;
	private String fName;
	private byte[] uFile = null;
	private String cType = null;
	private boolean habAdjuntar;
	private boolean habDescargar;
	private Boolean mostrarPanelAdjunto = false;
	private Boolean movilidad=false;

	public void buscarDoc() {
		if (!precondSearch())
			return;
		movilidad=false;
		docDecreto = searchDoc();
		
		if (docDecreto != null) {
			fNameMostrar = docDecreto.getNombreFisicoDoc();
			habDescargar=true;
			habAdjuntar=false;
			mostrarPanelAdjunto=false;
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			fNameMostrar = null;
			mostrarPanelAdjunto=true;
			docActoAdmin = null;
			habAdjuntar=true;
			habDescargar=false;
			fileActoAdmin = null;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");
		}
	}

	public void buscarDocMovilidad() {
		if (!precondSearch())
			return;
		movilidad=true;
		docDecreto = searchDoc();
		if (docDecreto != null) {
			fNameMostrar = docDecreto.getNombreFisicoDoc();
			habDescargar=true;
			habAdjuntar=false;
			mostrarPanelAdjunto=false;
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			fNameMostrar = null;
			mostrarPanelAdjunto=true;
			docActoAdmin = null;
			habAdjuntar=true;
			habDescargar=false;
			fileActoAdmin = null;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");
		}
	}

	public void descargarDoc() throws FileNotFoundException, IOException {
		if (docDecreto != null ) {
			String resp =	AdmDocAdjuntoFormController.abrirDocumentoFromCU(docDecreto.getIdDocumento(), usuarioLogueado.getIdUsuario());
			if(!resp.equalsIgnoreCase("OK")){
				statusMessages.add(Severity.ERROR,resp);
			}
			} else if (fileActoAdmin != null) {
				admDocAdjuntoFormController.enviarArchivoANavegador(fileActoAdmin.getFileName(), fileActoAdmin.getFile());
			} else {
				statusMessages.add(Severity.ERROR, "No existe documento que descargar");
			}
	}
	public void descargarDocBD() throws FileNotFoundException, IOException {
		if (docDecreto!=null && docDecreto.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(docDecreto.getIdDocumento(),usuarioLogueado.getIdUsuario() );
		} else {
			statusMessages.add(Severity.ERROR, "No existe documento que descargar");
		}
	}

	private Boolean precondSearch() {
		if (nroDoc == null || fechaDoc == null || idTipoDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}
		if(nroDoc < 0){
			statusMessages.add(Severity.ERROR, "El campo Nº Doc. debe ser mayor a cero");
			return false;
		}
		return true;
	}

	private Documentos searchDoc() {
		String sql="select Documentos from Documentos Documentos "
			+ " where Documentos.activo is true "
			+ " and Documentos.nroDocumento = :nroDoc and Documentos.fechaDoc = :fechaDoc"
			+ " and Documentos.datosEspecificos.idDatosEspecificos = :idTipoDoc";
		if(movilidad)
			sql+=" and Documentos.configuracionUoCab.idConfiguracionUo=:idConfiguracionUo";
		
		Query q =
			em.createQuery(sql);
		q.setParameter("nroDoc", nroDoc);
		q.setParameter("fechaDoc", fechaDoc);
		q.setParameter("idTipoDoc", idTipoDoc);
		if(movilidad)
			q.setParameter("idConfiguracionUo",usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		List<Documentos> lista = q.getResultList();
		if (lista.size() > 0) {
			fNameMostrar=lista.get(0).getNombreFisicoDoc();
			return lista.get(0);
		}
		return null;
	}
	
	private Boolean precondAdjuntarDoc() {
		if (!precondSearch())
			return false;
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}
		// Verificar que ya no exista
		Documentos doc = searchDoc();
		if (doc != null) {
			statusMessages.add(Severity.ERROR, "No se puede adjuntar, ya existe un documento con los mismos parámetros");
			return false;
		}
		return true;
	}

	public void adjuntarDoc() {
		if (!precondAdjuntarDoc())
			return;
		docActoAdmin = docDecreto;
		fileActoAdmin =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		fNameMostrar=fName;

		statusMessages.add(Severity.INFO,"Documento Adjuntado ");

	}
	public void adjuntarDocSinValidar() {
		if (!precondSearch())
			return ;
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return ;
		}
		docActoAdmin = docDecreto;
		fileActoAdmin =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		fNameMostrar=fName;

		statusMessages.add(Severity.INFO,"Documento Adjuntado ");

	}
	
	public void cambiarActo(){
		nroDoc = null;
		fechaDoc = null;
		idTipoDoc = null;
		fName = null;
		uFile = null;
		cType = null;
		docDecreto = null;
		fNameMostrar = null;
		habAdjuntar=false;
		habDescargar=false;
		fileActoAdmin = null;
		mostrarPanelAdjunto=false;
	}

	public void cargarDatosDocumento(){
		fNameMostrar=docDecreto.getNombreFisicoDoc();
		idTipoDoc=docDecreto.getDatosEspecificos().getIdDatosEspecificos();
		fechaDoc=docDecreto.getFechaDoc();
		nroDoc=docDecreto.getNroDocumento();
	}
	public Integer getNroDoc() {
		return nroDoc;
	}

	public void setNroDoc(Integer nroDoc) {
		this.nroDoc = nroDoc;
	}

	public Date getFechaDoc() {
		return fechaDoc;
	}

	public void setFechaDoc(Date fechaDoc) {
		this.fechaDoc = fechaDoc;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getfNameMostrar() {
		return fNameMostrar;
	}

	public void setfNameMostrar(String fNameMostrar) {
		this.fNameMostrar = fNameMostrar;
	}

	public Documentos getDocDecreto() {
		return docDecreto;
	}

	public void setDocDecreto(Documentos docDecreto) {
		this.docDecreto = docDecreto;
	}

	public Documentos getDocActoAdmin() {
		return docActoAdmin;
	}

	public void setDocActoAdmin(Documentos docActoAdmin) {
		this.docActoAdmin = docActoAdmin;
	}

	public UploadItem getFileActoAdmin() {
		return fileActoAdmin;
	}

	public void setFileActoAdmin(UploadItem fileActoAdmin) {
		this.fileActoAdmin = fileActoAdmin;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public byte[] getuFile() {
		return uFile;
	}

	public void setuFile(byte[] uFile) {
		this.uFile = uFile;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public boolean isHabAdjuntar() {
		return habAdjuntar;
	}

	public void setHabAdjuntar(boolean habAdjuntar) {
		this.habAdjuntar = habAdjuntar;
	}

	public boolean isHabDescargar() {
		return habDescargar;
	}

	public void setHabDescargar(boolean habDescargar) {
		this.habDescargar = habDescargar;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public Boolean getMostrarPanelAdjunto() {
		return mostrarPanelAdjunto;
	}

	public void setMostrarPanelAdjunto(Boolean mostrarPanelAdjunto) {
		this.mostrarPanelAdjunto = mostrarPanelAdjunto;
	}

	public Boolean getMovilidad() {
		return movilidad;
	}

	public void setMovilidad(Boolean movilidad) {
		this.movilidad = movilidad;
	}
	

}
