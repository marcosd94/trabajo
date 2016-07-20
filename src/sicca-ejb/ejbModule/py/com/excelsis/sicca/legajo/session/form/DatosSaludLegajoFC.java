package py.com.excelsis.sicca.legajo.session.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
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

import py.com.excelsis.sicca.entity.DatosEnfermedad;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosLicenciaMedica;
import py.com.excelsis.sicca.entity.DatosSeguroMedico;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("datosSaludLegajoFC")
public class DatosSaludLegajoFC implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4064796744912181247L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Long idPersona;
	//private Long idEnfermedad;
	private Long idSeguro;
	private Long idTipoDocEnfermedad;
	private Long idTipoDocSangre;
	private Long idTipoDocReposo;
	private String descTipoDoc;
	private String descTipoDocSangre;
	private Boolean esOtro;
	private Boolean esOtroChequeo;

	private Long idDocEnfermedad;
	private Long idDocSangre;
	private Long idDocReposo;
	private Integer indexEnfermedad;
	private Integer indexReposo;
	private Integer indexSeguro;
	private Persona persona;
	private DatosEnfermedad datosEnfermedad = new DatosEnfermedad();
	private DatosLicenciaMedica reposo = new DatosLicenciaMedica();
	private DatosSeguroMedico seguroMedico = new DatosSeguroMedico();
	private String texto;
	private String obsEnfermedad;
	private String causaReposo;
	private String plan;
	private String asegurador;
	private String telefono;
	private String grupoSanguineo;
	private String rh;
	private Boolean enfermo;
	private Date fechaIngreso;
	private Date fechaInicio;
	private Date fechaFin;
	private boolean isEditEnfermedad = false;
	private boolean isEditReposo = false;
	private boolean isEditSeguro = false;
	

	private List<DatosEnfermedad> listaEnfermedades = new ArrayList<DatosEnfermedad>();
	private List<DatosEnfermedad> listaPersonas = new ArrayList<DatosEnfermedad>();
	private List<DatosLicenciaMedica> listaReposos = new ArrayList<DatosLicenciaMedica>();
	private List<DatosSeguroMedico> listaSeguros = new ArrayList<DatosSeguroMedico>();
	private List<SelectItem> enfermedadesSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> tipoDocEnfermedadesSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> tipoDocSangreSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> seguroMedicoSelectItems = new ArrayList<SelectItem>();

	/**
	 * DOCUMENTOS ADJUNTOS
	 * **/
	private UploadItem itemEnfermedad;
	private String nombreDocEnfermedad;
	private File inputFileEnfermedad;
	private byte[] uploadedFileEnfermedad;
	private String contentTypeEnfermedad;
	private String fileNameEnfermedad;
	
	private UploadItem itemSangre;
	private String nombreDocSangre;
	private File inputFileSangre;
	private byte[] uploadedFileSangre;
	private String contentTypeSangre;
	private String fileNameSangre;

	private UploadItem itemReposo;
	private String nombreDocReposo;
	private File inputFileReposo;
	private byte[] uploadedFileReposo;
	private String contentTypeReposo;
	private String fileNameReposo;

	/**
	 * M�todo que inicializa los datos
	 */
	public void init() {
		persona = em.find(Persona.class, idPersona);
		
		//updateEnfermedad();
		updateTipoDocEnfermedades();
		updateTipoDocSangre();
		updateSeguroMedico();
		cargarListaEnfermedades();
		//cargarListaReposos();
		cargarListaSeguros();
		if(persona.getDocumentoSangre() != null){
			idDocSangre = persona.getDocumentoSangre().getIdDocumento();
			nombreDocSangre = persona.getDocumentoSangre().getNombreFisicoDoc();
			idTipoDocSangre = persona.getDocumentoSangre().getDatosEspecificos().getIdDatosEspecificos();
		}
		grupoSanguineo = persona.getGrupoSanguineoAbo();
		rh = persona.getGrupoSanguineoRh();
		//enfermo = String.valueOf(datosEnfermedad.isEnfermo());
		esOtro = false;
		esOtroChequeo = false;
		
	}
	
	public void abrirDocSangre() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocSangre, usuarioLogueado.getIdUsuario());
	}

	public void updatePersona() {
		Documentos docSangre = null;
		try {
			if (idTipoDocSangre == null || uploadedFileSangre == null) {
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
				return;
			} else if (!validacionDocumentos(uploadedFileSangre,contentTypeSangre, fileNameSangre, itemSangre,"Sangre"))
				return;
			if(idDocSangre != null){
				docSangre = em.find(Documentos.class,idDocSangre);
				docSangre.setActivo(false);
				docSangre.setFechaMod(new Date());
				docSangre.setUsuMod(usuarioLogueado.getCodigoUsuario());
			}
			documentoSangre();
			if (idDocSangre != null){
				docSangre = em.find(Documentos.class,idDocSangre);
				docSangre.setDescripcion(descTipoDocSangre);
				docSangre.setIdTabla(persona.getIdPersona());
				docSangre.setPersona(persona);
				persona.setDocumentoSangre(docSangre);
			}
			
			//persona.setEnfermo(enfermo);
			persona.setGrupoSanguineoAbo(grupoSanguineo);
			persona.setGrupoSanguineoRh(rh);
			persona.setFechaMod(new Date());
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(persona);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puede agregar el registro, verifique");
		}
	}

	/**
	 * Combo Enfermedad
	 */
	/*public void updateEnfermedad() {
		List<DatosEspecificos> enfList = enfermedades();
		enfermedadesSelectItems = new ArrayList<SelectItem>();
		buildEnfermedadesSelectItem(enfList);
		idEnfermedad = null;

	}*/

	private void buildEnfermedadesSelectItem(List<DatosEspecificos> dptoList) {
		
		if (enfermedadesSelectItems == null)
			enfermedadesSelectItems = new ArrayList<SelectItem>();
		else
			enfermedadesSelectItems.clear();

		enfermedadesSelectItems.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos dep : dptoList) {
			enfermedadesSelectItems.add(new SelectItem(dep
					.getIdDatosEspecificos(), dep.getDescripcion()));
		}
	}

	private List<DatosEspecificos> enfermedades() {
		
		Query q = em
				.createQuery("select d from DatosEspecificos d "
						+ "  where d.activo is true and d.datosGenerales.descripcion = 'ENFERMEDADES' order by d.descripcion");
		return q.getResultList();
	}

	/**
	 * Combo Tipo Documentos Enfermedades
	 */
	public void updateTipoDocEnfermedades() {
		
		List<DatosEspecificos> enfList = tipoDocEnfermedades();
		tipoDocEnfermedadesSelectItems = new ArrayList<SelectItem>();
		buildTipoDocEnfermedadesSelectItem(enfList);
		idTipoDocEnfermedad = null;

	}
	
	public void updateTipoDocSangre() {
		
		List<DatosEspecificos> enfList = tipoDocSangre();
		tipoDocSangreSelectItems = new ArrayList<SelectItem>();
		buildTipoDocSangreSelectItem(enfList);
		idTipoDocSangre = null;

	}

	private void buildTipoDocEnfermedadesSelectItem(
			List<DatosEspecificos> dptoList) {
		
		if (tipoDocEnfermedadesSelectItems == null)
			tipoDocEnfermedadesSelectItems = new ArrayList<SelectItem>();
		else
			tipoDocEnfermedadesSelectItems.clear();

		tipoDocEnfermedadesSelectItems.add(new SelectItem(null,
				SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos dep : dptoList) {
			tipoDocEnfermedadesSelectItems.add(new SelectItem(dep
					.getIdDatosEspecificos(), dep.getDescripcion()));
		}
	}
	
	private void buildTipoDocSangreSelectItem(
			List<DatosEspecificos> dptoList) {
		
		if (tipoDocSangreSelectItems == null)
			tipoDocSangreSelectItems = new ArrayList<SelectItem>();
		else
			tipoDocSangreSelectItems.clear();

		tipoDocSangreSelectItems.add(new SelectItem(null,
				SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos dep : dptoList) {
			tipoDocSangreSelectItems.add(new SelectItem(dep
					.getIdDatosEspecificos(), dep.getDescripcion()));
		}
	}

	private List<DatosEspecificos> tipoDocEnfermedades() {
		
		Query q = em
				.createQuery("select d from DatosEspecificos d "
						+ "  where d.activo is true and d.datosGenerales.descripcion = 'TIPOS DE DOCUMENTOS' "
						+ " and d.valorAlf = 'CCMS' "
						+ "order by d.descripcion");
		return q.getResultList();
	}
	
	private List<DatosEspecificos> tipoDocSangre() {
		
		Query q = em
				.createQuery("select d from DatosEspecificos d "
						+ "  where d.activo is true and d.datosGenerales.descripcion = 'TIPOS DE DOCUMENTOS' "
						+ " and d.valorAlf = 'CRTS' "
						+ "order by d.descripcion");
		return q.getResultList();
	}

	/**
	 * Combo Seguro Medico
	 */
	public void updateSeguroMedico() {
		
		List<DatosEspecificos> enfList = seguros();
		seguroMedicoSelectItems = new ArrayList<SelectItem>();
		buildSegurosSelectItem(enfList);
		idSeguro = null;

	}

	private void buildSegurosSelectItem(List<DatosEspecificos> segList) {
		
		if (seguroMedicoSelectItems == null)
			seguroMedicoSelectItems = new ArrayList<SelectItem>();
		else
			seguroMedicoSelectItems.clear();

		seguroMedicoSelectItems.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos dep : segList) {
			seguroMedicoSelectItems.add(new SelectItem(dep
					.getIdDatosEspecificos(), dep.getDescripcion()));
		}
	}

	private List<DatosEspecificos> seguros() {
		
		Query q = em
				.createQuery("select d from DatosEspecificos d "
						+ "  where d.activo is true and d.datosGenerales.descripcion = 'SEGURO MEDICO' order by d.descripcion");
		return q.getResultList();
	}

	
	
	
	
	
	public void addEnfermedad() {
		//System.out.println("38"+contentTypeEnfermedad+":" + uploadedFileEnfermedad + ":" + fileNameEnfermedad);
		statusMessages.clear();
		/*if (idEnfermedad == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar una enfermedad");
			return;
		}*/
		/*if (existeEnfermedad()) {
			statusMessages
					.add(Severity.ERROR,
							"La enfermedad seleccionada ya forma parte de la lista. Verifique");
			return;
		}*/
		try {

			if (idTipoDocEnfermedad == null || uploadedFileEnfermedad == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
				return;
			} else if (!validacionDocumentos(uploadedFileEnfermedad,
					contentTypeEnfermedad, fileNameEnfermedad, itemEnfermedad,
					"enfermedad"))

				return;
			
			documentoEnfermedad();

			DatosEnfermedad enfermedad = new DatosEnfermedad();
			enfermedad.setActivo(true);
			
			//if(enfermo != null && enfermo.equals("true") ) enfermedad.setEnfermo(true);
			//else enfermedad.setEnfermo(false);
			
			if (obsEnfermedad != null && !obsEnfermedad.trim().isEmpty())
				enfermedad.setObsEnfermedad(obsEnfermedad);
			enfermedad.setFechaAlta(new Date());
			enfermedad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			if (idDocEnfermedad != null){
				enfermedad.setDocumento(em.find(Documentos.class,
						idDocEnfermedad));
				enfermedad.getDocumento().setDescripcion(descTipoDoc);
				em.merge(enfermedad.getDocumento());
			}
			Query q = em.createQuery("select d from DatosEnfermedad d "
					+ "  where d.persona.idPersona = "
					+ idPersona);
			listaPersonas = new ArrayList<DatosEnfermedad>();
			listaPersonas = q.getResultList();
			enfermedad.setPersona(persona);
			
			//if(listaPersonas.isEmpty())
			//{
				
				em.persist(enfermedad);
				em.flush();
				if (enfermedad.getDocumento() != null){
					enfermedad.getDocumento().setIdTabla(enfermedad.getIdDatosEnfermedad());
					enfermedad.getDocumento().setPersona(persona);
					em.merge(enfermedad.getDocumento());
				}
				em.flush();
				cancelarEnfermedad();
				listaEnfermedades.add(enfermedad);
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			/*}
			else
			{
				statusMessages.add(Severity.ERROR,
						"Error no se pudo agregar el registro, ya existe la persona");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se pudo agregar el registro, verifique");
		}
	}

	public void updatedDatosEnfermedad() {
		
		statusMessages.clear();
		/*if (idEnfermedad == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar una enfermedad");
			return;
		}*/
		/*if (existeEnfermedad()) {
			statusMessages
					.add(Severity.ERROR,
							"La enfermedad seleccionada ya forma parte de la lista. Verifique");
			return;
		}*/
		try {
			
			if (uploadedFileEnfermedad != null) {
				if (idTipoDocEnfermedad == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}
				if (!validacionDocumentos(uploadedFileEnfermedad,
						contentTypeEnfermedad, fileNameEnfermedad,
						itemEnfermedad, "enfermedad"))

					return;
				if (idDocEnfermedad != null) {
					Documentos doc = em.find(Documentos.class, idDocEnfermedad);
					doc.setActivo(false);
					doc.setFechaMod(new Date());
					doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(doc);
					idDocEnfermedad = null;
				}
				documentoEnfermedad();
			}

			/*datosEnfermedad.setDatosEspEnfermedad(em.find(
					DatosEspecificos.class, idEnfermedad));*/
			
			if (obsEnfermedad != null && !obsEnfermedad.trim().isEmpty())
				datosEnfermedad.setObsEnfermedad(obsEnfermedad);
			datosEnfermedad.setUsuMod(usuarioLogueado.getCodigoUsuario());
			datosEnfermedad.setFechaMod(new Date());
			if (idDocEnfermedad != null){
				datosEnfermedad.setDocumento(em.find(Documentos.class,
						idDocEnfermedad));
				datosEnfermedad.getDocumento().setDescripcion(descTipoDoc);
				datosEnfermedad.getDocumento().setIdTabla(datosEnfermedad.getIdDatosEnfermedad());
				datosEnfermedad.getDocumento().setPersona(persona);
			}
			//cargarListaPersonas();
			/*Query q = em.createQuery("select d from DatosEnfermedad d "
					+ "  where d.persona.idPersona = "
					+ idPersona);
			listaPersonas = new ArrayList<DatosEnfermedad>();
			listaPersonas = q.getResultList();*/
			
			/*if(listaPersonas.isEmpty())
				{System.out.println("qwerty");*/
				
				/*if(enfermo != null && enfermo.equals("true") ) datosEnfermedad.setEnfermo(true);
				else datosEnfermedad.setEnfermo(false);*/
				
				em.merge(datosEnfermedad);
				em.flush();
				cancelarEnfermedad();
				listaEnfermedades.set(indexEnfermedad, datosEnfermedad);
				cargarListaEnfermedades();
				isEditEnfermedad = false;
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
				//}
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puede agregar el registro, verifique");
		}
	}

	/*private Boolean existeEnfermedad() {
		Query q = null;
		if (datosEnfermedad == null
				|| datosEnfermedad.getIdDatosEnfermedad() == null) {
			q = em.createQuery("select d from DatosEnfermedad d "
					+ "  where d.activo is true and d.datosEspEnfermedad.idDatosEspecificos = "
					+ idEnfermedad + " and d.persona.idPersona = " + idPersona);
		} else {
			q = em.createQuery("select d from DatosEnfermedad d "
					+ "  where d.activo is true and d.datosEspEnfermedad.idDatosEspecificos = "
					+ idEnfermedad + " and d.idDatosEnfermedad != "
					+ datosEnfermedad.getIdDatosEnfermedad()
					+ " and d.persona.idPersona = " + idPersona);
		}
		if (q.getResultList().isEmpty())
			return false;
		return true;
	}*/

	private Boolean existeSeguro() {
		
		Query q = null;
		if (seguroMedico == null
				|| seguroMedico.getIdDatosSeguroMedico() == null) {
			q = em.createQuery("select d from DatosSeguroMedico d "
					+ "  where d.activo is true and d.datosEspSeguroMedico.idDatosEspecificos = "
					+ idSeguro + " and d.persona.idPersona = " + idPersona);
		} else {
			q = em.createQuery("select d from DatosSeguroMedico d "
					+ "  where d.activo is true and d.datosEspSeguroMedico.idDatosEspecificos = "
					+ idSeguro + " and d.idDatosSeguroMedico != "
					+ seguroMedico.getIdDatosSeguroMedico()
					+ " and d.persona.idPersona = " + idPersona);
		}
		if (q.getResultList().isEmpty())
			return false;
		return true;
	}

	private void cargarListaEnfermedades() {
		
		Query q = em.createQuery("select d from DatosEnfermedad d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ idPersona);
		listaEnfermedades = new ArrayList<DatosEnfermedad>();
		listaEnfermedades = q.getResultList();
	}

	private void cargarListaReposos() {
		
		Query q = em.createQuery("select d from DatosLicenciaMedica d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ idPersona);
		listaReposos = new ArrayList<DatosLicenciaMedica>();
		listaReposos = q.getResultList();
	}

	private void cargarListaSeguros() {
		
		Query q = em.createQuery("select d from DatosSeguroMedico d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ idPersona);
		listaSeguros = new ArrayList<DatosSeguroMedico>();
		listaSeguros = q.getResultList();
	}
	
	private void cargarListaPersonas() {
		
		Query q = em.createQuery("select d from DatosEnfermedad d "
				+ "  where d.persona.idPersona = "
				+ idPersona);
		listaPersonas = new ArrayList<DatosEnfermedad>();
		listaPersonas = q.getResultList();
	}

	public void cancelarEnfermedad() {
		
		idDocEnfermedad = null;
		contentTypeEnfermedad = null;
		fileNameEnfermedad = null;
		//idEnfermedad = null;
		idTipoDocEnfermedad = null;
		inputFileEnfermedad = null;
		isEditEnfermedad = false;
		itemEnfermedad = null;
		nombreDocEnfermedad = null;
		obsEnfermedad = null;
		//datosEnfermedad = new DatosEnfermedad();
	}

	public void cancelarReposo() {
		
		idDocReposo = null;
		contentTypeReposo = null;
		fileNameReposo = null;
		idTipoDocReposo = null;
		inputFileReposo = null;
		isEditReposo = false;
		itemReposo = null;
		nombreDocReposo = null;
		causaReposo = null;
		fechaFin = null;
		fechaInicio = null;
		reposo = new DatosLicenciaMedica();
	}

	public void cancelarSeguro() {
		
		idSeguro = null;
		fechaIngreso = null;
		plan = null;
		asegurador = null;
		telefono = null;
	}

	public void addReposo() {
		
		statusMessages.clear();

		if (fechaInicio == null || fechaFin == null || causaReposo == null
				|| causaReposo.trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return;
		}
		if (!General.isFechaValida(fechaInicio)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Inicio inv�lida");
			return;
		}
		if (!General.isFechaValida(fechaFin)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Fin inv�lida");
			return;
		}

		if (fechaInicio.after(fechaFin)) {
			statusMessages.add(Severity.ERROR,
					"Verifique la Fecha Inicio y Fecha Fin");
			return;
		}
		try {
			if (uploadedFileReposo != null) {
				if (idTipoDocReposo == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}
				if (!validacionDocumentos(uploadedFileReposo,
						contentTypeReposo, fileNameReposo, itemReposo, "reposo"))

					return;
				documentoReposo();
			}

			reposo = new DatosLicenciaMedica();
			reposo.setActivo(true);
			reposo.setObsLicenciaMedica(causaReposo);
			reposo.setFechaInicio(fechaInicio);
			reposo.setFechaFin(fechaFin);
			reposo.setFechaAlta(new Date());
			reposo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			if (idDocReposo != null)
				reposo.setDocumento(em.find(Documentos.class, idDocReposo));
			reposo.setPersona(persona);
			em.persist(reposo);
			em.flush();
			cancelarReposo();
			cargarListaReposos();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}
	}

	public void updatedReposo() {
		
		statusMessages.clear();
		if (fechaInicio == null || fechaFin == null || causaReposo == null
				|| causaReposo.trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return;
		}
		if (!General.isFechaValida(fechaInicio)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Inicio inv�lida");
			return;
		}
		if (!General.isFechaValida(fechaFin)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Fin inv�lida");
			return;
		}

		if (fechaInicio.after(fechaFin)) {
			statusMessages.add(Severity.ERROR,
					"Verifique la Fecha Inicio y Fecha Fin");
			return;
		}
		try {
			if (uploadedFileReposo != null) {
				if (idTipoDocReposo == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
					return;
				}
				if (!validacionDocumentos(uploadedFileReposo,
						contentTypeReposo, fileNameReposo, itemReposo, "reposo"))
					return;
				if (idDocReposo != null) {
					Documentos doc = em.find(Documentos.class, idDocReposo);
					doc.setActivo(false);
					doc.setFechaMod(new Date());
					doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(doc);
					idDocReposo = null;
				}
				documentoReposo();
			}

			reposo.setObsLicenciaMedica(causaReposo);
			reposo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			reposo.setFechaMod(new Date());
			if (idDocReposo != null)
				reposo.setDocumento(em.find(Documentos.class, idDocReposo));

			em.merge(reposo);
			em.flush();
			cancelarReposo();
			cargarListaReposos();
			isEditReposo = false;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}
	}

	public void addSeguro() {
		
		statusMessages.clear();

		if (fechaIngreso == null || idSeguro == null) {
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return;
		}
		if (!General.isFechaValida(fechaIngreso)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Inicio inv�lida");
			return;
		}
		if (existeSeguro()) {
			statusMessages
					.add(Severity.ERROR,
							"El seguro seleccionado ya forma parte de la lista. Verifique");
			return;
		}

		try {
			seguroMedico = new DatosSeguroMedico();
			seguroMedico.setActivo(true);
			seguroMedico.setFechaAlta(new Date());
			seguroMedico.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			seguroMedico.setFechaIngreso(fechaIngreso);
			seguroMedico.setDatosEspSeguroMedico(em.find(
					DatosEspecificos.class, idSeguro));
			if (plan != null && !plan.trim().isEmpty())
				seguroMedico.setPlanSeguroMedico(plan);
			if (asegurador != null && !asegurador.trim().isEmpty())
				seguroMedico.setNombreAsegurador(asegurador);
			if (telefono != null && !telefono.trim().isEmpty())
				seguroMedico.setTelefonoSeguro(telefono);
			seguroMedico.setPersona(persona);
			em.persist(seguroMedico);
			em.flush();
			cancelarSeguro();
			cargarListaSeguros();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puede agregar el registro, verifique");
		}
	}

	public void updatedSeguro() {
		
		statusMessages.clear();
		if (fechaIngreso == null || idSeguro == null) {
			statusMessages
					.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return;
		}
		if (!General.isFechaValida(fechaIngreso)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Inicio inv�lida");
			return;
		}

		if (existeSeguro()) {
			statusMessages
					.add(Severity.ERROR,
							"El seguro seleccionado ya forma parte de la lista. Verifique");
			return;
		}
		try {

			seguroMedico.setActivo(true);
			seguroMedico.setFechaMod(new Date());
			seguroMedico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			seguroMedico.setFechaIngreso(fechaIngreso);
			seguroMedico.setDatosEspSeguroMedico(em.find(
					DatosEspecificos.class, idSeguro));
			if (plan != null && !plan.trim().isEmpty())
				seguroMedico.setPlanSeguroMedico(plan);
			if (asegurador != null && !asegurador.trim().isEmpty())
				seguroMedico.setNombreAsegurador(asegurador);
			if (telefono != null && !telefono.trim().isEmpty())
				seguroMedico.setTelefonoSeguro(telefono);

			em.merge(seguroMedico);
			em.flush();
			cancelarSeguro();
			cargarListaSeguros();
			isEditSeguro = false;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}
	}

	private Boolean validacionDocumentos(byte[] uploadedFile,
			String contentType, String fileName, UploadItem item, String tipo) {
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				if (tipo.equalsIgnoreCase("enfermedad")) {
					crearUploadItemEnfermedad(fileName, uploadedFile.length,
							contentType, uploadedFile);
					item = itemEnfermedad;
				}

				if (tipo.equalsIgnoreCase("reposo")) {
					crearUploadItemReposo(fileName, uploadedFile.length,
							contentType, uploadedFile);
					item = itemReposo;
				}
				
				if (tipo.equalsIgnoreCase("sangre")) {
					crearUploadItemSangre(fileName, uploadedFile.length,
							contentType, uploadedFile);
					item = itemSangre;
				}

				String nomfinal1 = "";
				nomfinal1 = item.getFileName();
				String extension = nomfinal1.substring(nomfinal1
						.lastIndexOf("."));
				List<TipoArchivo> tam = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"El archivo supera el tama�o m�ximo permitido. L�mite "
										+ tam.get(0).getTamanho()
										+ tam.get(0).getUnidMedida()
										+ ", verifique");
						return false;
					}
				}
			} else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}

		}

		return true;

	}

	private void documentoEnfermedad() throws NamingException {
		String nombrePantalla = "DatosSaludLegajo";
		String separador = File.separator;
		

		String direccionFisica = separador + "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+separador+"DATOS_SALUD"+separador;

		idDocEnfermedad = AdmDocAdjuntoFormController.guardarDirecto(
				itemEnfermedad, direccionFisica, nombrePantalla,
				idTipoDocEnfermedad, usuarioLogueado.getIdUsuario(),
				"DatosEnfermedad");

	}
	
	private void documentoSangre() throws NamingException {
		String nombrePantalla = "DatosSaludLegajo";
		String separador = File.separator;
		

		String direccionFisica = separador + "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+separador+"DATOS_SALUD"+separador;

		idDocSangre = AdmDocAdjuntoFormController.guardarDirecto(
				itemSangre, direccionFisica, nombrePantalla,
				idTipoDocSangre, usuarioLogueado.getIdUsuario(),
				"Persona");

	}

	private void documentoReposo() throws NamingException {
		String nombrePantalla = "datos_salud_legajo";
		String direccionFisica = "";
		String separador = File.separator;
		

		direccionFisica = separador + "SICCA" + separador + "USUARIO_PORTAL"
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();

		idDocReposo = AdmDocAdjuntoFormController.guardarDirecto(itemReposo,
				direccionFisica, nombrePantalla, idTipoDocReposo,
				usuarioLogueado.getIdUsuario(), "DatosLicenciaMedica");

	}

	private void crearUploadItemEnfermedad(String fileName, int fileSize,
			String contentType, byte[] file) {
		itemEnfermedad = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDocEnfermedad = itemEnfermedad.getFileName();
	}

	private void crearUploadItemReposo(String fileName, int fileSize,
			String contentType, byte[] file) {
		itemReposo = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDocReposo = itemReposo.getFileName();
	}
	
	private void crearUploadItemSangre(String fileName, int fileSize,
			String contentType, byte[] file) {
		itemSangre = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDocSangre = itemSangre.getFileName();
	}

	public void abrirDocEnfermedad(int index) {

		DatosEnfermedad e = listaEnfermedades.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumento()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}

	public void abrirDocReposo(int index) {

		DatosLicenciaMedica e = listaReposos.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumento()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}

	public void editarEnfermedad(int index) {
		
		indexEnfermedad = index;
		isEditEnfermedad = true;
		
		datosEnfermedad = listaEnfermedades.get(indexEnfermedad);
		/*idEnfermedad = datosEnfermedad.getDatosEspEnfermedad()
				.getIdDatosEspecificos();*/
		if (datosEnfermedad.getDocumento() != null) {
			idDocEnfermedad = datosEnfermedad.getDocumento().getIdDocumento();
			nombreDocEnfermedad = datosEnfermedad.getDocumento()
					.getNombreFisicoDoc();
			idTipoDocEnfermedad = datosEnfermedad.getDocumento()
					.getDatosEspecificos().getIdDatosEspecificos();
			descTipoDoc = datosEnfermedad.getDocumento().getDescripcion();
		}
		obsEnfermedad = datosEnfermedad.getObsEnfermedad();
		
		//enfermo = String.valueOf(datosEnfermedad.isEnfermo());
	}

	public void editarReposo(int index) {
		
		indexReposo = index;
		isEditReposo = true;
		reposo = listaReposos.get(indexReposo);

		if (reposo.getDocumento() != null) {
			idDocReposo = reposo.getDocumento().getIdDocumento();
			nombreDocReposo = reposo.getDocumento().getNombreFisicoDoc();
			idTipoDocReposo = reposo.getDocumento().getDatosEspecificos()
					.getIdDatosEspecificos();
		}
		fechaInicio = reposo.getFechaInicio();
		fechaFin = reposo.getFechaFin();
		causaReposo = reposo.getObsLicenciaMedica();

	}

	public void editarSeguro(int index) {
		
		indexSeguro = index;
		isEditSeguro = true;
		seguroMedico = listaSeguros.get(indexSeguro);
		idSeguro = seguroMedico.getDatosEspSeguroMedico()
				.getIdDatosEspecificos();
		plan = seguroMedico.getPlanSeguroMedico();
		fechaIngreso = seguroMedico.getFechaIngreso();
		asegurador = seguroMedico.getNombreAsegurador();
		telefono = seguroMedico.getTelefonoSeguro();
	}

	public void deleteEnfermedad(int index) {
		
		indexEnfermedad = index;
		DatosEnfermedad e = listaEnfermedades.get(indexEnfermedad);
		e.setActivo(false);
		e.setFechaMod(new Date());
		e.setUsuMod(usuarioLogueado.getCodigoUsuario());
		if(e.getDocumento() != null){
			e.getDocumento().setActivo(false);
			e.getDocumento().setFechaMod(new Date());
			e.getDocumento().setUsuMod(usuarioLogueado.getCodigoUsuario());
		}
		try {
			//System.out.println("asd");
			em.merge(e);
			em.flush();
			cargarListaEnfermedades();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception ex) {
			ex.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puede agregar el registro, verifique");
		}
	}

	public void deleteReposo(int index) {
		
		indexReposo = index;
		DatosLicenciaMedica e = listaReposos.get(indexReposo);
		e.setActivo(false);
		e.setFechaMod(new Date());
		e.setUsuMod(usuarioLogueado.getCodigoUsuario());
		try {
			em.merge(e);
			em.flush();
			cargarListaReposos();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception ex) {
			ex.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}
	}

	public void deleteSeguro(int index) {
		
		indexSeguro = index;
		DatosSeguroMedico e = listaSeguros.get(indexSeguro);
		e.setActivo(false);
		e.setFechaMod(new Date());
		e.setUsuMod(usuarioLogueado.getCodigoUsuario());
		try {
			em.merge(e);
			em.flush();
			cargarListaSeguros();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception ex) {
			ex.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}
	}
	
	public String getfile() {
		Query q = null;
		//idTipoDocCI = (long)82;
		
		q = em.createQuery(" Select d from Documentos d, DatosEspecificos dat "
				+ " where d.activo= TRUE  and d.persona.idPersona = "
				+ idPersona 
				+ " and d.datosEspecificos.idDatosEspecificos = dat.idDatosEspecificos "
				+ " and dat.descripcion = 'CERTIFICADO MEDICO'"
				+ " order by d.fechaAlta desc ");
		List<Documentos> documentoDTOList = q.getResultList();
		
		Documentos doc = new Documentos();
		if(!documentoDTOList.isEmpty())
		{
			doc = documentoDTOList.get(0);
			return doc.getNombreFisicoDoc();
		}
		else
		{
			return "";
		}
		
	}
	
	public String nombreCertificadoMedico(int index) {

		DatosEnfermedad e = listaEnfermedades.get(index);
		return e.getDocumento().getNombreFisicoDoc();
	}
	
	public String obsCertificadoMedico(int index) {

		DatosEnfermedad e = listaEnfermedades.get(index);
		return e.getObsEnfermedad();
	}
	
	public String descCertificadoMedico(int index) {

		DatosEnfermedad e = listaEnfermedades.get(index);
		return e.getDocumento().getDescripcion();
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

	public String getTexto() {
		
		return texto;
	}

	public void setTexto(String texto) {
		
		this.texto = texto;
	}
/*
	public Long getIdEnfermedad() {
		return idEnfermedad;
	}

	public void setIdEnfermedad(Long idEnfermedad) {
		this.idEnfermedad = idEnfermedad;
	}*/

	public String getObsEnfermedad() {
		
		return obsEnfermedad;
	}

	public void setObsEnfermedad(String obsEnfermedad) {
		
		this.obsEnfermedad = obsEnfermedad;
	}

	public Long getIdTipoDocEnfermedad() {
		
		return idTipoDocEnfermedad;
	}

	public void setIdTipoDocEnfermedad(Long idTipoDocEnfermedad) {
		
		this.idTipoDocEnfermedad = idTipoDocEnfermedad;
	}
	
	public Long getIdTipoDocSangre() {
		
		return idTipoDocSangre;
	}

	public void setIdTipoDocSangre(Long idTipoDocSangre) {
		
		this.idTipoDocSangre = idTipoDocSangre;
	}

	public String getNombreDocEnfermedad() {
		
		return nombreDocEnfermedad;
	}

	public void setNombreDocEnfermedad(String nombreDocEnfermedad) {
		
		this.nombreDocEnfermedad = nombreDocEnfermedad;
	}
	
	public String getNombreDocSangre() {
		
		return nombreDocSangre;
	}

	public void setNombreDocSangre(String nombreDocSangre) {
		
		this.nombreDocSangre = nombreDocSangre;
	}

	public UploadItem getItemEnfermedad() {
		
		return itemEnfermedad;
	}

	public void setItemEnfermedad(UploadItem itemEnfermedad) {
		
		this.itemEnfermedad = itemEnfermedad;
	}
	
	public UploadItem getItemSangre() {
		
		return itemSangre;
	}

	public void setItemSangre(UploadItem itemSangre) {
		
		this.itemSangre = itemSangre;
	}

	public File getInputFileEnfermedad() {
		
		return inputFileEnfermedad;
	}

	public void setInputFileEnfermedad(File inputFileEnfermedad) {
		
		this.inputFileEnfermedad = inputFileEnfermedad;
	}
	
	public File getInputFileSangre() {
		
		return inputFileSangre;
	}

	public void setInputFileSangre(File inputFileSangre) {
		
		this.inputFileSangre = inputFileSangre;
	}

	public byte[] getUploadedFileEnfermedad() {
		
		return uploadedFileEnfermedad;
	}

	public void setUploadedFileEnfermedad(byte[] uploadedFileEnfermedad) {
		
		this.uploadedFileEnfermedad = uploadedFileEnfermedad;
	}
	
	public byte[] getUploadedFileSangre() {
		
		return uploadedFileSangre;
	}

	public void setUploadedFileSangre(byte[] uploadedFileSangre) {
		
		this.uploadedFileSangre = uploadedFileSangre;
	}

	public String getContentTypeEnfermedad() {
		
		return contentTypeEnfermedad;
	}

	public void setContentTypeEnfermedad(String contentTypeEnfermedad) {
		
		this.contentTypeEnfermedad = contentTypeEnfermedad;
	}
	
	public String getContentTypeSangre() {
		
		return contentTypeSangre;
	}

	public void setContentTypeSangre(String contentTypeSangre) {
		
		this.contentTypeSangre = contentTypeSangre;
	}

	public String getFileNameEnfermedad() {
		
		return fileNameEnfermedad;
	}

	public void setFileNameEnfermedad(String fileNameEnfermedad) {
		
		this.fileNameEnfermedad = fileNameEnfermedad;
	}
	
	public String getFileNameSangre() {
		
		return fileNameSangre;
	}

	public void setFileNameSangre(String fileNameSangre) {
		
		this.fileNameSangre = fileNameSangre;
	}

	public Long getIdDocEnfermedad() {
		
		return idDocEnfermedad;
	}

	public void setIdDocEnfermedad(Long idDocEnfermedad) {
		
		this.idDocEnfermedad = idDocEnfermedad;
	}
	
	public Long getIdDocSangre() {
		
		return idDocSangre;
	}

	public void setIdDocSangre(Long idDocSangre) {
		
		this.idDocSangre = idDocSangre;
	}

	public boolean isEditEnfermedad() {
		
		return isEditEnfermedad;
	}

	public void setEditEnfermedad(boolean isEditEnfermedad) {
		
		this.isEditEnfermedad = isEditEnfermedad;
	}

	public List<DatosEnfermedad> getListaEnfermedades() {
		
		return listaEnfermedades;
	}

	public void setListaEnfermedades(List<DatosEnfermedad> listaEnfermedades) {
		
		this.listaEnfermedades = listaEnfermedades;
	}

	public List<SelectItem> getEnfermedadesSelectItems() {
		
		return enfermedadesSelectItems;
	}

	public void setEnfermedadesSelectItems(
			List<SelectItem> enfermedadesSelectItems) {
		
		this.enfermedadesSelectItems = enfermedadesSelectItems;
	}

	public List<SelectItem> getTipoDocEnfermedadesSelectItems() {
		
		return tipoDocEnfermedadesSelectItems;
	}

	public void setTipoDocEnfermedadesSelectItems(
			List<SelectItem> tipoDocEnfermedadesSelectItems) {
		
		this.tipoDocEnfermedadesSelectItems = tipoDocEnfermedadesSelectItems;
	}
	
	public List<SelectItem> getTipoDocSangreSelectItems() {
		
		return tipoDocSangreSelectItems;
	}

	public void setTipoDocSangreSelectItems(
			List<SelectItem> tipoDocSangreSelectItems) {
		
		this.tipoDocSangreSelectItems = tipoDocSangreSelectItems;
	}

	public DatosEnfermedad getDatosEnfermedad() {
		
		return datosEnfermedad;
	}

	public void setDatosEnfermedad(DatosEnfermedad datosEnfermedad) {
		
		this.datosEnfermedad = datosEnfermedad;
	}

	public Integer getIndexEnfermedad() {
		
		return indexEnfermedad;
	}

	public void setIndexEnfermedad(Integer indexEnfermedad) {
		
		this.indexEnfermedad = indexEnfermedad;
	}

	public Long getIdTipoDocReposo() {
		
		return idTipoDocReposo;
	}

	public void setIdTipoDocReposo(Long idTipoDocReposo) {
		
		this.idTipoDocReposo = idTipoDocReposo;
	}

	public Long getIdDocReposo() {
		
		return idDocReposo;
	}

	public void setIdDocReposo(Long idDocReposo) {
		
		this.idDocReposo = idDocReposo;
	}

	public Integer getIndexReposo() {
		
		return indexReposo;
	}

	public void setIndexReposo(Integer indexReposo) {
		
		this.indexReposo = indexReposo;
	}

	public String getCausaReposo() {
		
		return causaReposo;
	}

	public void setCausaReposo(String causaReposo) {
		
		this.causaReposo = causaReposo;
	}

	public Date getFechaInicio() {
		
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		
		this.fechaFin = fechaFin;
	}

	public UploadItem getItemReposo() {
		
		return itemReposo;
	}

	public void setItemReposo(UploadItem itemReposo) {
		
		this.itemReposo = itemReposo;
	}

	public String getNombreDocReposo() {
		
		return nombreDocReposo;
	}

	public void setNombreDocReposo(String nombreDocReposo) {
		
		this.nombreDocReposo = nombreDocReposo;
	}

	public File getInputFileReposo() {
		
		return inputFileReposo;
	}

	public void setInputFileReposo(File inputFileReposo) {
		
		this.inputFileReposo = inputFileReposo;
	}

	public byte[] getUploadedFileReposo() {
		
		return uploadedFileReposo;
	}

	public void setUploadedFileReposo(byte[] uploadedFileReposo) {
		
		this.uploadedFileReposo = uploadedFileReposo;
	}

	public String getContentTypeReposo() {
		
		return contentTypeReposo;
	}

	public void setContentTypeReposo(String contentTypeReposo) {
		
		this.contentTypeReposo = contentTypeReposo;
	}

	public String getFileNameReposo() {
		
		return fileNameReposo;
	}

	public void setFileNameReposo(String fileNameReposo) {
		
		this.fileNameReposo = fileNameReposo;
	}

	public boolean isEditReposo() {
		
		return isEditReposo;
	}

	public void setEditReposo(boolean isEditReposo) {
		
		this.isEditReposo = isEditReposo;
	}

	public DatosLicenciaMedica getReposo() {
		
		return reposo;
	}

	public void setReposo(DatosLicenciaMedica reposo) {
		
		this.reposo = reposo;
	}

	public List<DatosLicenciaMedica> getListaReposos() {
		
		return listaReposos;
	}

	public void setListaReposos(List<DatosLicenciaMedica> listaReposos) {
		
		this.listaReposos = listaReposos;
	}

	public Integer getIndexSeguro() {
		
		return indexSeguro;
	}

	public void setIndexSeguro(Integer indexSeguro) {
		
		this.indexSeguro = indexSeguro;
	}

	public DatosSeguroMedico getSeguroMedico() {
		
		return seguroMedico;
	}

	public void setSeguroMedico(DatosSeguroMedico seguroMedico) {
		
		this.seguroMedico = seguroMedico;
	}

	public String getPlan() {
		
		return plan;
	}

	public void setPlan(String plan) {
		
		this.plan = plan;
	}

	public String getAsegurador() {
		
		return asegurador;
	}

	public void setAsegurador(String asegurador) {
		
		this.asegurador = asegurador;
	}
	
	public String getTelefono() {
		
		return telefono;
	}

	public void setTelefono(String telefono) {
		
		this.telefono = telefono;
	}

	public Date getFechaIngreso() {
		
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		
		this.fechaIngreso = fechaIngreso;
	}

	public boolean isEditSeguro() {
		
		return isEditSeguro;
	}

	public void setEditSeguro(boolean isEditSeguro) {
		
		this.isEditSeguro = isEditSeguro;
	}

	public List<DatosSeguroMedico> getListaSeguros() {
		
		return listaSeguros;
	}

	public void setListaSeguros(List<DatosSeguroMedico> listaSeguros) {
		
		this.listaSeguros = listaSeguros;
	}

	public List<SelectItem> getSeguroMedicoSelectItems() {
		
		return seguroMedicoSelectItems;
	}

	public void setSeguroMedicoSelectItems(
			List<SelectItem> seguroMedicoSelectItems) {
		
		this.seguroMedicoSelectItems = seguroMedicoSelectItems;
	}

	public Long getIdSeguro() {
		
		return idSeguro;
	}

	public void setIdSeguro(Long idSeguro) {
		
		this.idSeguro = idSeguro;
	}

	public String getGrupoSanguineo() {
		
		return grupoSanguineo;
	}

	public void setGrupoSanguineo(String grupoSanguineo) {
		
		this.grupoSanguineo = grupoSanguineo;
	}

	public String getRh() {
		
		return rh;
	}

	public void setRh(String rh) {
		
		this.rh = rh;
	}


	public Boolean getEnfermo() {
		
		return enfermo;
	}

	public void setEnfermo(Boolean enfermo) {
		
		this.enfermo = enfermo;
	}
	
	public String getDescTipoDoc() {
		return descTipoDoc;
	}

	public void setDescTipoDoc(String descTipoDoc) {
		this.descTipoDoc = descTipoDoc;
	}
	
	public String getDescTipoDocSangre() {
		return descTipoDocSangre;
	}

	public void setDescTipoDocSangre(String descTipoDocSangre) {
		this.descTipoDocSangre = descTipoDocSangre;
	}
	
	public Boolean getEsOtro(){
		return esOtro;
	}
	
	public void setEsOtro(Boolean esOtro){
		this.esOtro = esOtro;
	}
	
	public Boolean getEsOtroChequeo(){
		return esOtroChequeo;
	}
	
	public void setEsOtroChequeo(Boolean esOtroChequeo){
		this.esOtroChequeo = esOtroChequeo;
	}
	
	public void esOtroTipo() {
		if(idTipoDocSangre != null){
			DatosEspecificos datos= em.find(DatosEspecificos.class, idTipoDocSangre);
			if(datos.getDescripcion().equalsIgnoreCase("OTROS")){
				esOtro=true;
			} else {
				esOtro=false;
			}
		}
	}
	
	public void esOtroTipoChequeo() {
		if(idTipoDocEnfermedad != null){
			DatosEspecificos datos= em.find(DatosEspecificos.class, idTipoDocEnfermedad);
			if(datos.getDescripcion().equalsIgnoreCase("OTROS")){
				esOtroChequeo=true;
			} else {
				esOtroChequeo=false;
			}
		}
	}
	
	public void eliminarIdDocSangre() {
		Documentos docu = new Documentos();
		try {
			docu = em.find(Documentos.class, idDocSangre);
			docu.setActivo(false);
			docu.setFechaMod(new Date());
			docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(docu);
			persona.setDocumentoSangre(null);
			persona.setGrupoSanguineoAbo(null);
			persona.setGrupoSanguineoRh(null);
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona.setFechaMod(new Date());
			em.merge(persona);
			em.flush();
			uploadedFileSangre = null;
			idDocSangre = null;
			nombreDocSangre = null;
			descTipoDocSangre = null;
			grupoSanguineo = null;
			rh = null;
			updateTipoDocSangre();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"Error al eliminar el archivo, verifique");
		}

	}
}
