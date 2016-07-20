package py.com.excelsis.sicca.capacitacion.session.form;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.entity.AdjuntosCap;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("configurarItemCapacitacioFC")
public class ConfigurarItemCapacitacioFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	
	private AdjuntosCap planAnualAdjunto= new AdjuntosCap();
	private AdjuntosCap ejecucionAdjunto= new AdjuntosCap();
	private AdjuntosCap modeloContratoAdjunto= new AdjuntosCap();
	private Boolean habEdit=false;
	private Long idAdjuntosCapEdit;
	private List<AdjuntosCap> adjuntosCapLista= new ArrayList<AdjuntosCap>();
	private String texto=null;

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	
	private String nombreDocAnual = null;
	private String nombreDocEjecucion = null;
	private String nombreDocmodContrato = null;
	private Long idDoc1=null;
	private Long idDoc2=null;
	private Long idDoc3=null;
	private byte[] uploadedFile1 = null;
	private String contentType1 = null;
	private String fileName1 = null;
	private byte[] uploadedFile2 = null;
	private String contentType2 = null;
	private String fileName2 = null;
	private byte[] uploadedFile3 = null;
	private String contentType3 = null;
	private String fileName3 = null;
	private DatosEspecificos tipoDocPlan= new DatosEspecificos();
	private DatosEspecificos tipoDocEjecucion= new DatosEspecificos();
	

	public void init() {
		
			findAdjuntos();
			findDocs();	
			FindTipoDoc();
			
			
	}
	
	/**
	 * EN CASO QUE YA EXISTAN ARCHIVOS , LOS CAMBIO DE ARCHIVOS FUNCIONARAN DE MODO EDITAR
	 * */
	
	private void findAdjuntos(){
		try{
			planAnualAdjunto=(AdjuntosCap)em.createQuery("select adjuntosCap from AdjuntosCap adjuntosCap where adjuntosCap.tipo ='PLPA'").getSingleResult();
			if(planAnualAdjunto.getDocumentos()!=null){
				Documentos d1=em.find(Documentos.class,planAnualAdjunto.getDocumentos().getIdDocumento());
				nombreDocAnual=d1.getNombreFisicoDoc();
				idDoc1=d1.getIdDocumento();
			}
			
		}catch(NoResultException e){
			planAnualAdjunto= new AdjuntosCap();
			idDoc1=null;
		}
		try{
			ejecucionAdjunto=(AdjuntosCap)em.createQuery("select adjuntosCap from AdjuntosCap adjuntosCap where adjuntosCap.tipo ='PLEJ'").getSingleResult();
			if(ejecucionAdjunto.getDocumentos()!=null){
				Documentos d2=em.find(Documentos.class, ejecucionAdjunto.getDocumentos().getIdDocumento());
				nombreDocEjecucion=d2.getNombreFisicoDoc();
				idDoc2=d2.getIdDocumento();
			}
			
		}catch(NoResultException e){
			ejecucionAdjunto= new AdjuntosCap();
			idDoc2=null;
		}
		try{
			modeloContratoAdjunto=(AdjuntosCap)em.createQuery("select adjuntosCap from AdjuntosCap adjuntosCap where adjuntosCap.tipo ='MODC'").getSingleResult();
			if(modeloContratoAdjunto.getDocumentos()!=null){
				Documentos d3=em.find(Documentos.class, modeloContratoAdjunto.getDocumentos().getIdDocumento());
				nombreDocmodContrato=d3.getNombreFisicoDoc();
				idDoc3=d3.getIdDocumento();
			}
			
		}catch(NoResultException e){
			modeloContratoAdjunto= new AdjuntosCap();
			idDoc3=null; 
		}
		
		
	}
	@SuppressWarnings("unchecked")
	private void findDocs(){
		adjuntosCapLista= em.createQuery("Select adjuntosCap from  AdjuntosCap adjuntosCap" +
		" where adjuntosCap.tipo in ('IM1','IM2','IM3') order by adjuntosCap.tipo").getResultList();
		habEdit=false;
		texto=null;
	}
	
	

	
	

	public void descargar(Long idAdjuntoDescarga){
		AdjuntosCap descargar= em.find(AdjuntosCap.class, idAdjuntoDescarga);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU( descargar.getDocumentos().getIdDocumento(),usuarioLogueado.getIdUsuario());
		
	}
	
	public String save() {
		try {
			if(!validarCampos())
				return null;
			Long idDoc=null;

			if (uploadedFile1 != null) {

				idDoc = guardarAdjuntos(fileName1, uploadedFile1.length, contentType1, uploadedFile1, tipoDocPlan.getIdDatosEspecificos(), idDoc1);
				if (idDoc != null) {
					planAnualAdjunto.setDocumentos(new Documentos());
					planAnualAdjunto.getDocumentos().setIdDocumento(idDoc);
					if(planAnualAdjunto.getIdAdjuntosCap()!=null)
						em.merge(planAnualAdjunto);
					else{
						planAnualAdjunto.setTipo("PLPA");
						em.persist(planAnualAdjunto);
					}
					
				} else 
					return null;
				
			}
			if (uploadedFile2 != null) {
				
				idDoc = guardarAdjuntos(fileName2, uploadedFile2.length, contentType2, uploadedFile2, tipoDocEjecucion.getIdDatosEspecificos(), idDoc2);
				if (idDoc != null) {
					ejecucionAdjunto.setDocumentos(em.find(Documentos.class, idDoc));
					if(ejecucionAdjunto.getIdAdjuntosCap()!=null)
						em.merge(ejecucionAdjunto);
					else{
						ejecucionAdjunto.setTipo("PLEJ");
						em.persist(ejecucionAdjunto);
					}
					
				} else 
					return null;
				
			}
			if (uploadedFile3!= null) {
				
				idDoc = guardarAdjuntos(fileName3, uploadedFile3.length, contentType3, uploadedFile3, tipoDocEjecucion.getIdDatosEspecificos(), idDoc3);
				if (idDoc != null) {
					modeloContratoAdjunto.setDocumentos(em.find(Documentos.class, idDoc));
					if(modeloContratoAdjunto.getIdAdjuntosCap()!=null)
						em.merge(modeloContratoAdjunto);
					else{
						modeloContratoAdjunto.setTipo("MODC");
						em.persist(modeloContratoAdjunto);
					}
					
				} else 
					return null;
				
			}
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	
	
	}
	
	
	
	private void FindTipoDoc(){
		try {
			tipoDocPlan=(DatosEspecificos)em.createQuery("Select d from DatosEspecificos d where d.datosGenerales.descripcion = 'TIPOS DE DOCUMENTOS'" +
					" and d.valorAlf='PL_CAP' and activo=true").getResultList().get(0);
		} catch (NoResultException e) {
		}
		try {
			tipoDocEjecucion=(DatosEspecificos)em.createQuery("Select d from DatosEspecificos d where d.datosGenerales.descripcion='TIPOS DE DOCUMENTOS'" +
					" and d.valorAlf='EJ_CAP' and activo=true").getResultList().get(0);
		} catch (NoResultException e) {
		}
	}
	private Long guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "AdjuntosCap";
			String nombrePantalla = "configurarItemCapacitacion";
			String direccionFisica = "";
			direccionFisica =File.separator + "SICCA" + File.separator +anio + File.separator+"SFP";
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String update() {
		try {
			if(texto==null || texto.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe completar el campo Texto antes de realizar esta acci\u00F3n");
				return null;
			}
			AdjuntosCap adjuntosCap= em.find(AdjuntosCap.class, idAdjuntosCapEdit);
			adjuntosCap.setDescripcion(texto);
			em.merge(adjuntosCap);
			em.flush();
			findDocs();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";	
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		
		
	}
	public void cancelar(){
		texto=null;
		idAdjuntosCapEdit=null;
		habEdit=null;
	}
	public void editar(Long idAdjunto){
		idAdjuntosCapEdit=idAdjunto;
		AdjuntosCap aux= em.find(AdjuntosCap.class, idAdjunto);
		habEdit=true;
		texto=aux.getDescripcion();
	}
	
	
	/**
	 * controla previamente que los campos obligatorios estén completos al momento de guardar el registro
	 * @return true en el caso que todo este bien
	 * */

	private boolean validarCampos(){
		if(uploadedFile1==null && planAnualAdjunto.getDocumentos()==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar un archivo para el campo Plantilla Plan Anual antes de realizar esta acci\u00F3n");
			return false;
		}
		if(uploadedFile2==null && ejecucionAdjunto.getDocumentos()==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar un archivo para el campo Plantilla Ejecuci\u00F3n antes de realizar esta acci\u00F3n");
			return false;
		}
		if(uploadedFile3==null && modeloContratoAdjunto.getDocumentos()==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar un archivo para el campo Modelo de Contrato antes de realizar esta acci\u00F3n");
			return false;
		}
		

		
		
		return true;
	}
	
	
	
	/** GETTER'S && SETTER'S **/

	public AdjuntosCap getPlanAnualAdjunto() {
		return planAnualAdjunto;
	}

	public void setPlanAnualAdjunto(AdjuntosCap planAnualAdjunto) {
		this.planAnualAdjunto = planAnualAdjunto;
	}

	public AdjuntosCap getEjecucionAdjunto() {
		return ejecucionAdjunto;
	}

	public void setEjecucionAdjunto(AdjuntosCap ejecucionAdjunto) {
		this.ejecucionAdjunto = ejecucionAdjunto;
	}

	public AdjuntosCap getModeloContratoAdjunto() {
		return modeloContratoAdjunto;
	}

	public void setModeloContratoAdjunto(AdjuntosCap modeloContratoAdjunto) {
		this.modeloContratoAdjunto = modeloContratoAdjunto;
	}

	public Boolean getHabEdit() {
		return habEdit;
	}

	public void setHabEdit(Boolean habEdit) {
		this.habEdit = habEdit;
	}

	public Long getIdAdjuntosCapEdit() {
		return idAdjuntosCapEdit;
	}

	public void setIdAdjuntosCapEdit(Long idAdjuntosCapEdit) {
		this.idAdjuntosCapEdit = idAdjuntosCapEdit;
	}

	public List<AdjuntosCap> getAdjuntosCapLista() {
		return adjuntosCapLista;
	}

	public void setAdjuntosCapLista(List<AdjuntosCap> adjuntosCapLista) {
		this.adjuntosCapLista = adjuntosCapLista;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getNombreDocAnual() {
		return nombreDocAnual;
	}

	public void setNombreDocAnual(String nombreDocAnual) {
		this.nombreDocAnual = nombreDocAnual;
	}

	public String getNombreDocEjecucion() {
		return nombreDocEjecucion;
	}

	public void setNombreDocEjecucion(String nombreDocEjecucion) {
		this.nombreDocEjecucion = nombreDocEjecucion;
	}

	public String getNombreDocmodContrato() {
		return nombreDocmodContrato;
	}

	public void setNombreDocmodContrato(String nombreDocmodContrato) {
		this.nombreDocmodContrato = nombreDocmodContrato;
	}

	public Long getIdDoc1() {
		return idDoc1;
	}

	public void setIdDoc1(Long idDoc1) {
		this.idDoc1 = idDoc1;
	}

	public Long getIdDoc2() {
		return idDoc2;
	}

	public void setIdDoc2(Long idDoc2) {
		this.idDoc2 = idDoc2;
	}

	public Long getIdDoc3() {
		return idDoc3;
	}

	public void setIdDoc3(Long idDoc3) {
		this.idDoc3 = idDoc3;
	}

	public byte[] getUploadedFile1() {
		return uploadedFile1;
	}

	public void setUploadedFile1(byte[] uploadedFile1) {
		this.uploadedFile1 = uploadedFile1;
	}

	public String getContentType1() {
		return contentType1;
	}

	public void setContentType1(String contentType1) {
		this.contentType1 = contentType1;
	}

	public String getFileName1() {
		return fileName1;
	}

	public void setFileName1(String fileName1) {
		this.fileName1 = fileName1;
	}

	public byte[] getUploadedFile2() {
		return uploadedFile2;
	}

	public void setUploadedFile2(byte[] uploadedFile2) {
		this.uploadedFile2 = uploadedFile2;
	}

	public String getContentType2() {
		return contentType2;
	}

	public void setContentType2(String contentType2) {
		this.contentType2 = contentType2;
	}

	public String getFileName2() {
		return fileName2;
	}

	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}

	public byte[] getUploadedFile3() {
		return uploadedFile3;
	}

	public void setUploadedFile3(byte[] uploadedFile3) {
		this.uploadedFile3 = uploadedFile3;
	}

	public String getContentType3() {
		return contentType3;
	}

	public void setContentType3(String contentType3) {
		this.contentType3 = contentType3;
	}

	public String getFileName3() {
		return fileName3;
	}

	public void setFileName3(String fileName3) {
		this.fileName3 = fileName3;
	}

	public DatosEspecificos getTipoDocPlan() {
		return tipoDocPlan;
	}

	public void setTipoDocPlan(DatosEspecificos tipoDocPlan) {
		this.tipoDocPlan = tipoDocPlan;
	}

	public DatosEspecificos getTipoDocEjecucion() {
		return tipoDocEjecucion;
	}

	public void setTipoDocEjecucion(DatosEspecificos tipoDocEjecucion) {
		this.tipoDocEjecucion = tipoDocEjecucion;
	}
	
	

	

	
	
	
}
