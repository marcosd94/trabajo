package py.com.excelsis.sicca.legajo.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.AuditLegajo;
import py.com.excelsis.sicca.entity.AuditLegajoDet;
import py.com.excelsis.sicca.entity.AuditLegajoObs;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.CamposLegajo;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosLicenciaMedica;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.DocumentosPersona;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.RespuestaFileUploader;



@Scope(ScopeType.CONVERSATION)
@Name("datosPersonalesLegajoFC")
public class DatosPersonalesLegajoFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322089796803796369L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Long idPersona;
	private Long idSexo;
	private Long idEstadoCivil;
	private Long idNacionalidad;
	private Long idPaisNac;
	private Long idDptoNac;
	private Long idCiudadNac;
	private Long idPaisResi;
	private Long idDptoResi;
	private Long idCiudadResi;
	// private Long idLocalidadResi;
	private Long idLocalidadDir;
	private Long idEtnia;
	private Long idDoc;
	private Long idDocGuardado;
	private Long idDocCI;
	private Long idDocCVidaResidencia;
	private Long idDocCCertificadoNacimiento;
	private Long idDocCPartidaNacimiento;
	private Long idTelContacto;
	private String telefonos;
	private String telefonoPart;
	private String telefonoLab;
	private String telContacto;

	String imagenActiva = "";
	private String texto;
	private String nroReg1;
	private String especialidadReg1;
	private String nroReg2;
	private String especialidadReg2;
	private String ruc;
	private String docMilitar;
	private String regConducir;
	private String categoria;
	private String pasaporte;
	private String nroReg1Original;
	private String especialidadReg1Original;
	private String nroReg2Original;
	private String especialidadReg2Original;
	private String rucOriginal;
	private String docMilitarOriginal;
	private String regConducirOriginal;
	private String categoriaOriginal;
	private String pasaporteOriginal;
	private Persona persona;
	private Documentos documentos;
	private AuditLegajo auditLegajo;
	private RespuestaFileUploader rFileUploader;
	private UploadItem uploadItem;
	private UploadItem itemCI;//CVidaResidencia
	private UploadItem itemCVidaResidencia;
	
	private UploadItem itemCPartidaNacimiento;
	
	private String nombreDocCI;
	private String nombreDocCVidaResidencia;
	private String nombreDocCPartidaNacimiento;
	
	private Long idTipoDocCI;
	private Long idTipoDocCVidaResidencia;
	private Long idTipoDocCPartidaNacimiento;
	

	
	
	
	
	private byte[] uploadedFileCI;
	private byte[] uploadedFileCVidaResidencia;
	private byte[] uploadedFileCPartidaNacimiento;
	
	
	private Long idTipoDoc;
	private String fileNameCI;
	private String fileNameCVidaResidencia;
	private String fileNameCPartidaNacimiento;
	
	private String contentTypeCI;
	private String contentTypeCVidaResidencia;
	private String contentTypeCPartidaNacimiento;
	
	
	private List<Documentos> documentoDTOList = new ArrayList<Documentos>();
	private List<Documentos> documentoDTOList2 = new ArrayList<Documentos>();
	
	
	private byte[] uploadedFile;
	private UploadItem item;
	private String contentType;
	private String fileName;
	private String nombreDoc;
	private String observacion;
	private Integer REG_PROF_1;
	private Integer REG_PROF_2;
	private Integer RUC;
	private Integer DOC_MIL;
	private Integer REG_COND;
	private Integer PASAPORTE;
	String mimeImagenActiva = "";
	private String ciOriginal;
	private Long idPaisNacOriginal;
	private String sexoOriginal;
	private String nombreOriginal;
	private String apellidoOriginal;
	private Date fechaNacOriginal;
	private String telefonoOriginal;
	private List<SelectItem> departamentosSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecItem = new ArrayList<SelectItem>();

	private List<SelectItem> departamentosResidenciaSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecResidenciaItem = new ArrayList<SelectItem>();
	private List<SelectItem> localidadDirSelecItem = new ArrayList<SelectItem>();

	/**
	 * Mï¿½todo que inicializa los datos
	 */
	public void init() {
		persona = em.find(Persona.class, idPersona);
		cargarDatosPersona(persona.getIdPersona());
		obtenerEstados();
		cargarDatosOriginales();
		

		obtenerDatosOtrosDocs();
		if (existePersona())
			obtenerDatosObs();
		if (persona.getDocumentos() != null) {
			idDoc = persona.getDocumentos().getIdDocumento();
			idDocGuardado = idDoc;
			Documentos documentos = new Documentos();
			documentos = em.find(Documentos.class, idDoc);
			File arch = AdmDocAdjuntoFormController.recuperarImagen(idDoc,
					usuarioLogueado.getIdUsuario());
			crearUploadItem(documentos.getNombreFisicoDoc(), new Integer(
					documentos.getTamanhoDoc()), documentos.getMimetype(), arch);
			seteadorFile(arch);

		}
		documentoDTOList = findDocCI();
		Documentos doc = new Documentos();
		if(!documentoDTOList.isEmpty()){
			doc = documentoDTOList.get(0);
			Long nro_doc_id = doc.getIdDocumento();//tomar el documento de la CI
			idDocCI = nro_doc_id;
			fileNameCI = doc.getNombreFisicoDoc();
			contentTypeCI = doc.getMimetype();
			nombreDocCI = doc.getNombreFisicoDoc();
			//System.out.println("66:" + nro_doc_id);
			}
		
		documentoDTOList.clear();
		documentoDTOList2 = findDocCvidaresidencia();
		Documentos doc2 = new Documentos();
		if(!documentoDTOList2.isEmpty()){
			doc2 = documentoDTOList2.get(0);
			Long nro_doc_id2 = doc2.getIdDocumento();//tomar el documento de la CI
			idDocCVidaResidencia = nro_doc_id2;
			fileNameCVidaResidencia = doc2.getNombreFisicoDoc();
			contentTypeCVidaResidencia = doc2.getMimetype();
			nombreDocCVidaResidencia = doc2.getNombreFisicoDoc();
			//System.out.println("66:" + nro_doc_id2);
			}
		
		
		documentoDTOList.clear();
		documentoDTOList = findDocCPartidaNacimiento();
		Documentos doc3 = new Documentos();
		if(!documentoDTOList.isEmpty()){
			doc3 = documentoDTOList.get(0);
			Long nro_doc_id3 = doc3.getIdDocumento();//tomar el documento de la CI
			idDocCPartidaNacimiento = nro_doc_id3;
			fileNameCPartidaNacimiento = doc3.getNombreFisicoDoc();
			contentTypeCPartidaNacimiento = doc3.getMimetype();
			nombreDocCPartidaNacimiento = doc3.getNombreFisicoDoc();
			//System.out.println("66:" + nro_doc_id3);
			}
		
		
		
	}
	
	
	
	public void abrirDocCI() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocCI, usuarioLogueado.getIdUsuario());
	}
	
	
	
	
	
	
	public void abrirDocCvidaresidencia() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocCVidaResidencia, usuarioLogueado.getIdUsuario());
	}
	
	
	
	public void abrirDocCPartidaNacimiento() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocCPartidaNacimiento, usuarioLogueado.getIdUsuario());
	}
	
	

	private void cargarDatosOriginales() {
		ciOriginal = persona.getDocumentoIdentidad();
		if (persona.getCiudadByIdCiudadNac() != null
				&& persona.getCiudadByIdCiudadNac().getDepartamento() != null
				&& persona.getCiudadByIdCiudadNac().getDepartamento().getPais() != null)
			idPaisNacOriginal = persona.getCiudadByIdCiudadNac()
					.getDepartamento().getPais().getIdPais();
		sexoOriginal = persona.getSexo();
		nombreOriginal = persona.getNombres();
		apellidoOriginal = persona.getApellidos();
		fechaNacOriginal = persona.getFechaNacimiento();
		telefonoOriginal = persona.getTelefonos();
		DocumentosPersona docPersonaRegProf1 = new DocumentosPersona();
		docPersonaRegProf1 = existeDoc(REG_PROF_1);
		if (docPersonaRegProf1 != null) {
			nroReg1Original = docPersonaRegProf1.getDocumentoNro();
			especialidadReg1Original = docPersonaRegProf1.getOtroDato();
		}

		DocumentosPersona docPersonaRegProf2 = new DocumentosPersona();
		docPersonaRegProf2 = existeDoc(REG_PROF_2);
		if (docPersonaRegProf2 != null) {
			nroReg2Original = docPersonaRegProf2.getDocumentoNro();
			especialidadReg2Original = docPersonaRegProf2.getOtroDato();
		}

		DocumentosPersona docRuc = new DocumentosPersona();
		docRuc = existeDoc(RUC);

		if (docRuc != null)
			rucOriginal = docRuc.getDocumentoNro();

		DocumentosPersona docuMilitar = new DocumentosPersona();
		docuMilitar = existeDoc(DOC_MIL);
		if (docuMilitar != null)
			docMilitarOriginal = docuMilitar.getDocumentoNro();

		DocumentosPersona docPersonaRegConducir = new DocumentosPersona();
		docPersonaRegConducir = existeDoc(REG_COND);
		if (docPersonaRegConducir != null) {
			regConducirOriginal = docPersonaRegConducir.getDocumentoNro();
			categoriaOriginal = docPersonaRegConducir.getOtroDato();
		}
		DocumentosPersona docuPasaporte = new DocumentosPersona();
		docuPasaporte = existeDoc(PASAPORTE);
		if (docuPasaporte != null)
			pasaporteOriginal = docuPasaporte.getDocumentoNro();
		
		

	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, File file) {
		uploadItem = new UploadItem(fileName, fileSize, contentType, file);

	}

	private List<DocumentosPersona> obtenerDocPersonas() {
		Query q = em.createQuery("select doc from DocumentosPersona doc "
				+ "  where doc.persona.idPersona = " + persona.getIdPersona());
		return q.getResultList();
	}

	private void obtenerDatosObs() {
		Query q = em.createQuery("select doc from AuditLegajoObs doc "
				+ "  where doc.auditLegajo.auditLegajo = "
				+ auditLegajo.getAuditLegajo() + " and doc.ficha = " + 1);
		List<AuditLegajoObs> listaObs = q.getResultList();
		if (listaObs.isEmpty())
			observacion = null;
		else
			observacion = listaObs.get(0).getObservacion();
	}

	private void obtenerEstados() {
		REG_PROF_1 = seleccionUtilFormController.getIdEstadosReferencia(
				"LEGAJO_DOC", "REG_PROF_1");
		REG_PROF_2 = seleccionUtilFormController.getIdEstadosReferencia(
				"LEGAJO_DOC", "REG_PROF_2");
		RUC = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC",
				"RUC");
		DOC_MIL = seleccionUtilFormController.getIdEstadosReferencia(
				"LEGAJO_DOC", "DOC_MIL");
		REG_COND = seleccionUtilFormController.getIdEstadosReferencia(
				"LEGAJO_DOC", "REG_COND");
		PASAPORTE = seleccionUtilFormController.getIdEstadosReferencia(
				"LEGAJO_DOC", "PASAPORTE");
	}

	private void obtenerDatosOtrosDocs() {
		List<DocumentosPersona> lista = obtenerDocPersonas();
		for (DocumentosPersona p : lista) {
			if (p.getTipoDocumento().equals(REG_PROF_1)) {
				nroReg1 = p.getDocumentoNro();
				especialidadReg1 = p.getOtroDato();
			}
			if (p.getTipoDocumento().equals(REG_PROF_2)) {
				nroReg2 = p.getDocumentoNro();
				especialidadReg2 = p.getOtroDato();
			}
			if (p.getTipoDocumento().equals(RUC))
				ruc = p.getDocumentoNro();
			if (p.getTipoDocumento().equals(DOC_MIL))
				docMilitar = p.getDocumentoNro();
			if (p.getTipoDocumento().equals(REG_COND)) {
				regConducir = p.getDocumentoNro();
				categoria = p.getOtroDato();
			}
			if (p.getTipoDocumento().equals(PASAPORTE))
				pasaporte = p.getDocumentoNro();
		}
	}

	private void cargarDatosPersona(Long idPersona) {
		idSexo = searchSexo(persona.getSexo());
		idEstadoCivil = searchEstadoCivil(persona.getEstadoCivil());
		
		telefonos = persona.getTelefonos();
		telContacto = persona.getTelContacto();
		telefonoLab = persona.getTelefonoLab();
		telefonoPart = persona.getTelefonoPart();
		idTelContacto = searchTelContacto(persona.getTelContacto());
		
		if (persona.getDatosEspecificos() != null)
			idNacionalidad = persona.getDatosEspecificos()
					.getIdDatosEspecificos();
		if (persona.getCiudadByIdCiudadNac() != null) {
			idPaisNac = persona.getCiudadByIdCiudadNac().getDepartamento()
					.getPais().getIdPais();
			updateDepartamento();
			idDptoNac = persona.getCiudadByIdCiudadNac().getDepartamento()
					.getIdDepartamento();
			updateCiudad();
			idCiudadNac = persona.getCiudadByIdCiudadNac().getIdCiudad();
		} else {
			updateDepartamento();
			updateCiudad();
		}
		if (persona.getDatosEspecificosEtnia() != null)
			idEtnia = persona.getDatosEspecificosEtnia()
					.getIdDatosEspecificos();

		if (persona.getCiudadByIdCiudadDirecc() != null) {
			idPaisResi = persona.getCiudadByIdCiudadDirecc().getDepartamento()
					.getPais().getIdPais();
			updateDepartamentoResidencia();
			idDptoResi = persona.getCiudadByIdCiudadDirecc().getDepartamento()
					.getIdDepartamento();
			updateCiudadResidencia();
			idCiudadResi = persona.getCiudadByIdCiudadDirecc().getIdCiudad();
			if (persona.getBarrio() != null) {

				idLocalidadDir = persona.getBarrio().getIdBarrio();
			}
			updateBarrio();

		} else {
			updateDepartamentoResidencia();
			updateCiudadResidencia();
			updateBarrio();
		}
	}

	@SuppressWarnings("unchecked")
	private Long searchSexo(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'SEXO' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	@SuppressWarnings("unchecked")
	private Long searchEstadoCivil(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	/**
	 * nacionalidad
	 * */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));

		List<Object[]> n = em
				.createNativeQuery(
						"Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r,"
								+ "seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales"
								+ " Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();

		while (it.hasNext()) {
			Object[] fila = it.next();
			if (fila[0] != null)
				si.add(new SelectItem(fila[0], fila[1] != null ? fila[1]
						.toString() : ""));
		}

		return si;
	}

	/**
	 * Combo Departamento Nacimiento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDptoNac = null;
		idCiudadNac = null;

	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPaisNac != null) {
			departamentoList.getPais().setIdPais(idPaisNac);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}

	/**
	 * Combo Departamento Residencia
	 */
	public void updateDepartamentoResidencia() {
		List<Departamento> dptoList = getDepartamentosResiByPais();
		departamentosResidenciaSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoResiSelectItem(dptoList);
		idDptoResi = null;
		idCiudadResi = null;

	}

	private List<Departamento> getDepartamentosResiByPais() {
		if (idPaisResi != null) {
			departamentoList.getPais().setIdPais(idPaisResi);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoResiSelectItem(List<Departamento> dptoList) {
		if (departamentosResidenciaSelecItem == null)
			departamentosResidenciaSelecItem = new ArrayList<SelectItem>();
		else
			departamentosResidenciaSelecItem.clear();

		departamentosResidenciaSelecItem.add(new SelectItem(null,
				SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosResidenciaSelecItem.add(new SelectItem(dep
					.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Residencia
	 */
	public void updateCiudadResidencia() {
		List<Ciudad> ciuList = getCiudadByDptoResi();
		ciudadSelecResidenciaItem = new ArrayList<SelectItem>();
		buildCiudadResiSelectItem(ciuList);
		idCiudadResi = null;
	}

	private List<Ciudad> getCiudadByDptoResi() {
		if (idDptoResi != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoResi);
			ciudadList.setMaxResults(null);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadResiSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecResidenciaItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecResidenciaItem.clear();

		ciudadSelecResidenciaItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecResidenciaItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Nacimiento
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudadNac = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDptoNac != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoNac);
			ciudadList.setMaxResults(null);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		localidadDirSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
//		idLocalidadDir = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudadResi != null) {
			barrioList.setIdCiudad(idCiudadResi);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.listaPorCiudad();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (localidadDirSelecItem == null)
			localidadDirSelecItem = new ArrayList<SelectItem>();
		else
			localidadDirSelecItem.clear();

		localidadDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			localidadDirSelecItem.add(new SelectItem(bar.getIdBarrio(), bar
					.getDescripcion()));
		}
	}

	private DocumentosPersona existeDoc(Integer val) {
		List<DocumentosPersona> lista = obtenerDocPersonas();
		for (DocumentosPersona p : lista) {
			if (p.getTipoDocumento().equals(val))
				return p;
		}
		return null;
	}

	private boolean validarFechasNac(Date fecha) {
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			Integer anioActual = Integer.parseInt(sdfSoloAnio
					.format(new Date()));
			Integer anioNacimiento = Integer
					.parseInt(sdfSoloAnio.format(fecha));
			Integer edad = anioActual - anioNacimiento;
			if (edad <= edadMaxima() && edad >= edadMinima())
				return true;

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private Integer edadMaxima() {
		List<Referencias> maxima = em.createQuery(
				"Select m from Referencias m "
						+ " where m.dominio='EDAD_MAXIMA_LEGAL' ")
				.getResultList();
		if (!maxima.isEmpty())
			return maxima.get(0).getValorNum();
		return 60;// en caso que no exista dato
	}

	@SuppressWarnings("unchecked")
	private Integer edadMinima() {
		List<Referencias> minima = em.createQuery(
				"Select m from Referencias m "
						+ " where m.dominio='EDAD_MINIMA_LEGAL' ")
				.getResultList();
		if (!minima.isEmpty())
			return minima.get(0).getValorNum();
		return 18;// en caso que no exista dato
	}

	private Boolean permitirGuardar() {
		statusMessages.clear();
		if (idSexo == null) {
			statusMessages.add(Severity.ERROR,
					"El campo Sexo es obligatorio. Verifique");
			return false;
		}
		if (!General.isFechaValida(persona.getFechaNacimiento())) {
			statusMessages.add(Severity.ERROR,
					"Fecha de Nacimiento inválida. Verifique");
			return false;
		}
		if (!validarFechasNac(persona.getFechaNacimiento())) {
			statusMessages.add(Severity.ERROR,
					"El rango de edades debe comprender entre " + edadMinima()
							+ " y " + edadMaxima());
			return false;
		}

		if (persona.getEMail() != null && !persona.getEMail().trim().isEmpty() &&!General.isEmail(persona.getEMail().toLowerCase())) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un mail valido. Verifique");
			return false;
		}
		if (idNacionalidad == null) {
			statusMessages.add(Severity.ERROR,
					"El campo Nacionalidad es obligatorio. Verifique");
			return false;
		}
		
		if ((uploadedFileCI == null) && (nombreDocCI == null)) {
			statusMessages.add(Severity.WARN,
					"Debe adjuntar la Copia de Cédula de Identidad");
			return false;
		}
		if ((contentTypeCI == null || fileNameCI == null || uploadedFileCI == null) && (nombreDocCI == null)) {
			statusMessages.add(Severity.WARN,
					"Debe adjuntar la Copia de Cédula de Identidad");
			return false;
		}
		
		String aux = new String(em.find(Referencias.class, idTelContacto).getDescAbrev());
		
		if(aux.equals("MOVIL") && telefonos.trim().isEmpty() || aux.equals("PARTICULAR") && telefonoPart.trim().isEmpty() || aux.equals("LABORAL") && telefonoLab.trim().isEmpty()){
			
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR, "Debe rellenar el campo del telefono seleccionado. Verifique.");
			return null;
		}
		return true;
	}

	private Boolean guardarDocumento() throws NamingException {
		if (rFileUploader != null) {
			String nombreTabla = "Persona";
			String pantalla = "DatosPersonalesLegajo";
			String separator = File.separator;
			String ubicacionFisica = separator
					+ "LEGAJO" + separator
					+ persona.getDocumentoIdentidad() + "_"
					+ persona.getIdPersona()+ separator+"DATOS_PERSONALES"+ separator;
			DatosEspecificos datosEspecificos = obtenerDatosEspecificos();
			if (idDoc == null) {
				if (idDocGuardado != null) {
					Documentos documentos = em.find(Documentos.class,
							idDocGuardado);
					documentos.setActivo(false);
					documentos.setFechaMod(new Date());
					documentos.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(documentos);
				}
				idDoc = AdmDocAdjuntoFormController.guardarDirecto(
						rFileUploader.getItem(), ubicacionFisica, pantalla,
						datosEspecificos.getIdDatosEspecificos(),
						usuarioLogueado.getIdUsuario(), nombreTabla);
				if (idDoc != null) {
					if (idDoc.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return false;
					}
					if (idDoc.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaï¿½o permitido. Seleccione otro");
						return false;
					}
					if (idDoc.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						return false;
					}

				}
			}
		}
		return true;
	}

	private DatosEspecificos obtenerDatosEspecificos() {
		String sql = "SELECT DATOS_ESPECIFICOS.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES "
				+ "ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE DATOS_GENERALES.DESCRIPCION = 'TIPOS DE DOCUMENTOS' "
				+ "AND DATOS_ESPECIFICOS.VALOR_ALF = 'LEG_DP' "
				+ "AND DATOS_ESPECIFICOS.ACTIVO = TRUE "
				+ "AND DATOS_ESPECIFICOS.DESCRIPCION = 'FOTO CARNET' ";
		List<DatosEspecificos> l = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		if (l.isEmpty())
			return null;
		return l.get(0);
	}

	private void actualizarDatosPersona() {
		persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
		persona.setFechaMod(new Date());
		if (idCiudadNac != null)
			persona.setCiudadByIdCiudadNac(em.find(Ciudad.class, idCiudadNac));
		if (idCiudadResi != null)
			persona.setCiudadByIdCiudadDirecc(em.find(Ciudad.class,
					idCiudadResi));
		if (idLocalidadDir != null)
			persona.setBarrio(em.find(Barrio.class, idLocalidadDir));

		if (idEstadoCivil != null)
			persona.setEstadoCivil(em.find(Referencias.class, idEstadoCivil)
					.getDescAbrev());

		persona.setSexo(em.find(Referencias.class, idSexo).getDescAbrev());
		if (idEtnia != null)
			persona.setDatosEspecificosEtnia(em.find(DatosEspecificos.class,
					idEtnia));
		persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
		
		if (telefonos != null)
			persona.setTelefonos(telefonos);
		if (telefonoLab != null)
			persona.setTelefonoLab(telefonoLab);
		if (telefonoPart != null)
			persona.setTelefonoPart(telefonoPart);
		persona.setTelContacto(em.find(Referencias.class, idTelContacto).getDescAbrev());
		persona.setFechaMod(new Date());

		em.merge(persona);
	}
	
	
	
	
	
	private void crearUploadItemCI(String fileName, int fileSize,
			String contentType, byte[] file) {
		itemCI = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDocCI = itemCI.getFileName();
	}
	
	
	
	
	
	//crearUploadItemCVidaResidencia
	private void crearUploadItemCVidaResidencia(String fileName, int fileSize,
			String contentType, byte[] file) {
		itemCVidaResidencia = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDocCVidaResidencia = itemCVidaResidencia.getFileName();
	}
	
	
	
	private void crearUploadItemCPartidaNacimiento(String fileName, int fileSize,
			String contentType, byte[] file) {
		itemCPartidaNacimiento = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		
		nombreDocCPartidaNacimiento = itemCPartidaNacimiento.getFileName();
	}
	
	
	
	
	
	
	
	private Boolean validacionDocumentos(byte[] uploadedFile,
			String contentType, String fileName, UploadItem item, String tipo) {
		
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				if (tipo.equalsIgnoreCase("CI")) {
					crearUploadItemCI(fileName, uploadedFile.length,
							contentType, uploadedFile);
					item = itemCI;
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
								"El archivo supera el tamaï¿½o mï¿½ximo permitido. Lï¿½mite "
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
	
	
	
	
	
	
	
	private Boolean validacionDocumentosCVidaResidencia(byte[] uploadedFile,
			String contentType, String fileName, UploadItem item, String tipo) {
		
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				if (tipo.equalsIgnoreCase("CVidaResidencia")) {
					crearUploadItemCVidaResidencia(fileName, uploadedFile.length,
							contentType, uploadedFile);
					item = itemCVidaResidencia;
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
								"El archivo supera el tamaï¿½o mï¿½ximo permitido. Lï¿½mite "
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
	
	
	
	
	
	
	
	
	
	
	private Boolean validacionDocumentosCPartidaNacimiento(byte[] uploadedFile,
			String contentType, String fileName, UploadItem item, String tipo) {
		
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				if (tipo.equalsIgnoreCase("CPartidaNacimiento")) {
					crearUploadItemCPartidaNacimiento(fileName, uploadedFile.length,
							contentType, uploadedFile);
					item = itemCPartidaNacimiento;
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
								"El archivo supera el tamaï¿½o mï¿½ximo permitido. Lï¿½mite "
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
	
	
	
	
	
	
	
	
	
	
	private void documentoCI() throws NamingException {
		String nombrePantalla = "DatosPersonalesLegajo";
		String separador = File.separator;
		

		//direccionFisica = separador + "SICCA" + separador + "LEGAJO"
		String ubicacionFisica = separador
				+ "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+ separador+"DATOS_PERSONALES"+ separador;

		idDocCI = AdmDocAdjuntoFormController.guardarDirectoPersona(
				itemCI, ubicacionFisica, nombrePantalla,
				idTipoDocCI, usuarioLogueado.getIdUsuario(),
				"Persona", persona);

	}
//CVidaResidencia
	
	
	
	
	private void documentoCVidaResidencia() throws NamingException {
		String nombrePantalla = "DatosPersonalesLegajo";
		String separador = File.separator;
		

		//direccionFisica = separador + "SICCA" + separador + "LEGAJO"
		String ubicacionFisica = separador
				+ "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+ separador+"DATOS_PERSONALES"+ separador;

		idDocCVidaResidencia = AdmDocAdjuntoFormController.guardarDirectoPersona(
				itemCVidaResidencia, ubicacionFisica, nombrePantalla,
				idTipoDocCVidaResidencia, usuarioLogueado.getIdUsuario(),
				"Persona", persona);

	}
	
	
	
	
	private void documentoCPartidaNacimiento() throws NamingException {
		String nombrePantalla = "DatosPersonalesLegajo";
		String separador = File.separator;
		

		//direccionFisica = separador + "SICCA" + separador + "LEGAJO"
		String ubicacionFisica = separador
				+ "LEGAJO" + separador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+ separador+"DATOS_PERSONALES"+ separador;

		idDocCPartidaNacimiento = AdmDocAdjuntoFormController.guardarDirectoPersona(
				itemCPartidaNacimiento, ubicacionFisica, nombrePantalla,
				idTipoDocCPartidaNacimiento, usuarioLogueado.getIdUsuario(),
				"Persona", persona);

	}
	
	
	
	
	

	public String save() {
		try {
			//System.out.println("zxc:" + contentTypeCVidaResidencia + ":" + uploadedFileCVidaResidencia + ":" + fileNameCVidaResidencia);
			if (!permitirGuardar())
				return null;
			if (guardarDocumento()) {
				if (idDoc != null){
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setIdTabla(persona.getIdPersona());
					doc.setPersona(persona);
					persona.setDocumentos(doc);
					em.merge(doc);
					em.flush();
				}
					
			} else {
				return null;
			}
			actualizarDatosPersona();
			
			
			if (uploadedFileCI == null) {
				/*statusMessages
						.add(Severity.ERROR,
								"Debe Seleccionar el Documento. Verifique");
				return "";//validacionDocumentos guarda el archivo*/
			} else if (!validacionDocumentos(uploadedFileCI,
					contentTypeCI, fileNameCI, itemCI,
					"CI"))
				{return "";}
			
			
			if (uploadedFileCVidaResidencia == null) {
				
			} else if (!validacionDocumentosCVidaResidencia(uploadedFileCVidaResidencia,
					contentTypeCVidaResidencia, fileNameCVidaResidencia, itemCVidaResidencia,
					"CVidaResidencia"))
				{return "";}
			
			
			if (uploadedFileCPartidaNacimiento == null) {
				
			} else if (!validacionDocumentosCPartidaNacimiento(uploadedFileCPartidaNacimiento,
					contentTypeCPartidaNacimiento, fileNameCPartidaNacimiento, itemCPartidaNacimiento,
					"CPartidaNacimiento"))
				{return "";}
			
			
			DatosEspecificos de = new DatosEspecificos();
			Query q = null;
			q = em.createQuery("select d from DatosEspecificos d "
					+ "  where d.activo = true and d.descripcion = 'COPIA DE CEDULA DE IDENTIDAD' and d.valorAlf = 'LEG_DP' ");
			List<DatosEspecificos>l = q.getResultList();
			if(!l.isEmpty()){
				de = l.get(0);
				idTipoDocCI = de.getIdDatosEspecificos();//tomar el id del dato especifico de CI
			}
			
			
			DatosEspecificos de2 = new DatosEspecificos();
			q = em.createQuery("select d from DatosEspecificos d "
					+ "  where d.activo = true and d.descripcion = 'COPIA DE PARTIDA DE NACIMIENTO' and d.valorAlf = 'LEG_DP' ");
			List<DatosEspecificos>l2 = q.getResultList();
			if(!l2.isEmpty()){
				de2 = l2.get(0);
				idTipoDocCPartidaNacimiento = de2.getIdDatosEspecificos();//tomar el id del dato especifico de partida de nacimiento
			}
			
			
			
			q = em.createQuery("select d from DatosEspecificos d "
					+ "  where d.activo = true and d.descripcion = 'CERTIFICADO DE VIDA Y RESIDENCIA' and d.valorAlf = 'LEG_DP'");
			List<DatosEspecificos>l3 = q.getResultList();
			if(!l3.isEmpty()){
				DatosEspecificos de3 = l3.get(0);
				idTipoDocCVidaResidencia = de3.getIdDatosEspecificos();//tomar el id del dato especifico de certificado de vida y residencia
			}
			
			
			
			
			
			q = em.createQuery("select d from Documentos d "
					+ "  where d.persona.idPersona = "
					+ persona.getIdPersona() + " and d.datosEspecificos.idDatosEspecificos = " + idTipoDocCI + " and d.activo = true");
			List<Documentos>l4 = q.getResultList();
			
			
			Documentos doc = null;
			if (uploadedFileCI != null)
			{
				if(!l4.isEmpty()){//si ya se cargo el documento
					Documentos doc2 = l4.get(0);
					doc2.setActivo(false);//invalida la cedula anterior
					doc2.setFechaMod(new Date());
					doc2.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(doc2);
					em.flush();
					documentoCI();//guarda el registro documento con persona
				
				}
				else
				{
					documentoCI();//guarda el registro documento con persona
				}
				if(idDocCI != null){

					if (idDocCI.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						return null;
					}
					if (idDocCI.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocCI.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocCI.longValue() == -9) {
						 statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
						return null;
					}
					doc = em.find(Documentos.class, idDocCI);
					doc.setIdTabla(persona.getIdPersona());
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
				}
			}
			
			
			
			
			
			q = em.createQuery("select d from Documentos d "
					+ "  where d.persona.idPersona = "
					+ persona.getIdPersona() + " and d.datosEspecificos.idDatosEspecificos = " + idTipoDocCPartidaNacimiento + " and d.activo = true");
			List<Documentos>l6 = q.getResultList();
			
			
			
			if (uploadedFileCPartidaNacimiento != null)
			{
				if(!l6.isEmpty()){//si ya se cargo el documento
					Documentos doc2 = l6.get(0);
					doc2.setActivo(false);//invalida la cedula anterior
					doc2.setFechaMod(new Date());
					doc2.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(doc2);
					em.flush();
					documentoCPartidaNacimiento();//guarda el registro documento con persona
				
				}
				else
				{
					documentoCPartidaNacimiento();//guarda el registro documento con persona
				}
				if(idDocCPartidaNacimiento!= null){

					if (idDocCPartidaNacimiento.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						return null;
					}
					if (idDocCPartidaNacimiento.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocCPartidaNacimiento.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocCPartidaNacimiento.longValue() == -9) {
						 statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
						return null;
					}
					doc = em.find(Documentos.class, idDocCPartidaNacimiento);
					doc.setIdTabla(persona.getIdPersona());
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
				}
			}
			
			
			
			
			
			q = em.createQuery("select d from Documentos d "
					+ "  where d.persona.idPersona = " + persona.getIdPersona()
					+ " and d.datosEspecificos.idDatosEspecificos = " + idTipoDocCVidaResidencia + " and d.activo = true");
			List<Documentos>l5 = q.getResultList();
			
			
			
			
			if (uploadedFileCVidaResidencia != null)
			{
				if(!l5.isEmpty()){//si ya se cargo el documento
					Documentos doc3 = l5.get(0);
					doc3.setActivo(false);//invalida la cedula anterior
					doc3.setFechaMod(new Date());
					doc3.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(doc3);
					em.flush();
					documentoCVidaResidencia();//guarda el registro documento
				
				}
				else
				{
					
					documentoCVidaResidencia();//guarda el registro documento
				}
				if(idDocCVidaResidencia!= null){

					if (idDocCVidaResidencia.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						return null;
					}
					if (idDocCVidaResidencia.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocCVidaResidencia.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocCVidaResidencia.longValue() == -9) {
						 statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
						return null;
					}
					doc = em.find(Documentos.class, idDocCVidaResidencia);
					doc.setIdTabla(persona.getIdPersona());
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
				}
			}
			
			
			
			
			
			//idTipoDocCVidaResidencia = (long)1546;
			
			
			
			//idTipoDocCPartidaNacimiento = (long)1543;//COPIA DE PARTIDA DE NACIMIENTO
			//documentoCPartidaNacimiento();//guarda el registro documento
			
			
			
			
			if (nroReg1 != null && !nroReg1.trim().isEmpty()) {
				DocumentosPersona docPersona = new DocumentosPersona();
				docPersona = existeDoc(REG_PROF_1);
				if (docPersona == null) {
					docPersona = new DocumentosPersona();
					docPersona.setDocumentoNro(nroReg1);
					docPersona.setFechaAlta(new Date());
					docPersona.setPersona(persona);
					docPersona.setTipoDocumento(REG_PROF_1);
					docPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					if (especialidadReg1 != null
							&& !especialidadReg1.trim().isEmpty())
						docPersona.setOtroDato(especialidadReg1);
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(nroReg1);
					if (especialidadReg1 != null
							&& !especialidadReg1.trim().isEmpty())
						docPersona.setOtroDato(especialidadReg1);
					docPersona.setFechaMod(new Date());
					docPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docPersona);
				}
			}

			if (nroReg2 != null && !nroReg2.trim().isEmpty()) {
				DocumentosPersona docPersona = new DocumentosPersona();
				docPersona = existeDoc(REG_PROF_2);
				if (docPersona == null) {
					docPersona = new DocumentosPersona();
					docPersona.setDocumentoNro(nroReg2);
					docPersona.setFechaAlta(new Date());
					docPersona.setPersona(persona);
					docPersona.setTipoDocumento(REG_PROF_2);
					docPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					if (especialidadReg2 != null
							&& !especialidadReg2.trim().isEmpty())
						docPersona.setOtroDato(especialidadReg2);
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(nroReg2);
					if (especialidadReg1 != null
							&& !especialidadReg1.trim().isEmpty())
						docPersona.setOtroDato(especialidadReg2);
					docPersona.setFechaMod(new Date());
					docPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docPersona);
				}
			}

			if (ruc != null && !ruc.trim().isEmpty()) {
				DocumentosPersona docPersona = new DocumentosPersona();
				docPersona = existeDoc(RUC);
				if (docPersona == null) {
					docPersona = new DocumentosPersona();
					docPersona.setDocumentoNro(ruc);
					docPersona.setFechaAlta(new Date());
					docPersona.setPersona(persona);
					docPersona.setTipoDocumento(RUC);
					docPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(ruc);
					docPersona.setFechaMod(new Date());
					docPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docPersona);
				}
			}

			if (docMilitar != null && !docMilitar.trim().isEmpty()) {
				DocumentosPersona docPersona = new DocumentosPersona();
				docPersona = existeDoc(DOC_MIL);
				if (docPersona == null) {
					docPersona = new DocumentosPersona();
					docPersona.setDocumentoNro(docMilitar);
					docPersona.setFechaAlta(new Date());
					docPersona.setPersona(persona);
					docPersona.setTipoDocumento(DOC_MIL);
					docPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(docMilitar);
					docPersona.setFechaMod(new Date());
					docPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docPersona);
				}
			}

			if (regConducir != null && !regConducir.trim().isEmpty()) {
				DocumentosPersona docPersona = new DocumentosPersona();
				docPersona = existeDoc(REG_COND);
				if (docPersona == null) {
					docPersona = new DocumentosPersona();
					docPersona.setDocumentoNro(regConducir);
					docPersona.setFechaAlta(new Date());
					docPersona.setPersona(persona);
					docPersona.setTipoDocumento(REG_COND);
					docPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					if (categoria != null && !categoria.trim().isEmpty())
						docPersona.setOtroDato(categoria);
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(regConducir);
					if (categoria != null && !categoria.trim().isEmpty())
						docPersona.setOtroDato(categoria);
					docPersona.setFechaMod(new Date());
					docPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docPersona);
				}
			}

			if (pasaporte != null && !pasaporte.trim().isEmpty()) {
				DocumentosPersona docPersona = new DocumentosPersona();
				docPersona = existeDoc(PASAPORTE);
				if (docPersona == null) {

					docPersona = new DocumentosPersona();
					docPersona.setDocumentoNro(pasaporte);
					docPersona.setFechaAlta(new Date());
					docPersona.setPersona(persona);
					docPersona.setTipoDocumento(PASAPORTE);
					docPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(pasaporte);
					docPersona.setFechaMod(new Date());
					docPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docPersona);
				}
			}

			if (texto.equalsIgnoreCase("P"))
				auditoria();
			cargarDatosOriginales();
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Se ha producido un error inesperado: " + e.getMessage());
		}
		return null;
	}

	private void auditoria() {
		List<CamposLegajo> listaCampos = obtenerCamposLegajo();

		if (existePersona()) {
			auditLegajo.setEstado(2);
			auditLegajo.setFechaMod(new Date());
			auditLegajo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(auditLegajo);
			for (CamposLegajo p : listaCampos) {
				/**
				 * Si existe persona en la tabla audit crea un nuevo regitro en
				 * detalle
				 */

				if (p.getTabla().equalsIgnoreCase("PERSONA")) {
					/**
					 * Verifica que cada campo auditable haya sido cambiado
					 */
					// Campo documento_identidad
					if (p.getCampo().equalsIgnoreCase("documento_identidad")) {
						if (ciOriginal != null
								&& !ciOriginal.equals(persona
										.getDocumentoIdentidad()))
							saveDetail("U", ciOriginal, "documento_identidad",
									persona.getDocumentoIdentidad(), "PERSONA");
					}
					// Campo id_pais_expedicion_doc
					if (p.getCampo().equalsIgnoreCase("id_pais_expedicion_doc")) {

						if (idPaisNacOriginal != null) {

							if (idPaisNacOriginal != idPaisNac.intValue())
								saveDetail("U", idPaisNacOriginal + "",
										"id_pais_expedicion_doc", idPaisNac
												+ "", "PERSONA");
						}
					}
					// Campo sexo
					if (p.getCampo().equalsIgnoreCase("sexo")) {

						String sexo = em.find(Referencias.class, idSexo)
								.getDescAbrev();
						if (sexoOriginal != null && !sexoOriginal.equals(sexo))
							saveDetail("U", sexoOriginal, "sexo", sexo,
									"PERSONA");

					}
					// Campos nombres
					if (p.getCampo().equalsIgnoreCase("nombres")) {

						if (nombreOriginal != null
								&& !nombreOriginal.equals(persona.getNombres()))
							saveDetail("U", nombreOriginal, "nombres",
									persona.getNombres(), "PERSONA");
					}
					// Campos apellidos
					if (p.getCampo().equalsIgnoreCase("apellidos")) {

						if (apellidoOriginal != null
								&& !apellidoOriginal.equals(persona
										.getApellidos()))
							saveDetail("U", apellidoOriginal, "apellidos",
									persona.getApellidos(), "PERSONA");
					}
					// Campos fecha_nacimiento
					if (p.getCampo().equalsIgnoreCase("fecha_nacimiento")) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"dd-MM-yyyy");
						String fecAnt = sdf.format(fechaNacOriginal);
						String fecAct = sdf
								.format(persona.getFechaNacimiento());

						if (!fecAnt.equals(fecAct))
							saveDetail("U", fecAnt, "fecha_nacimiento", fecAct,
									"PERSONA");
					}

					// Campos telefonos
					if (p.getCampo().equalsIgnoreCase("telefonos")) {
						if (!telefonoOriginal.equals(persona.getTelefonos()))
							saveDetail("U", telefonoOriginal, "telefonos",
									persona.getTelefonos(), "PERSONA");
					}
				}
				if (p.getTabla().equalsIgnoreCase("DOCUMENTOS_PERSONA")) {
					/**
					 * Verifica que cada campo auditable haya sido cambiado
					 */
					// Campo tipo documento
					if (p.getCampo().equalsIgnoreCase("tipo_documento")) {
						// Registro Profesional 1
						DocumentosPersona docPersonaRegProf1 = new DocumentosPersona();
						docPersonaRegProf1 = existeDoc(REG_PROF_1);
						if (docPersonaRegProf1 == null) {
							if (nroReg1 != null && !nroReg1.trim().isEmpty())
								saveDetail("I", null, "tipo_documento",
										nroReg1, "DOCUMENTOS_PERSONA");
						}
						if (docPersonaRegProf1 != null
								&& !nroReg1Original.equalsIgnoreCase(nroReg1)) {
							if (nroReg1Original != null
									&& !nroReg1Original.trim().isEmpty())
								saveDetail("U", nroReg1Original,
										"tipo_documento", nroReg1,
										"DOCUMENTOS_PERSONA");
							else {
								saveDetail("I", null, "tipo_documento",
										nroReg1, "DOCUMENTOS_PERSONA");
							}
						}

						if (docPersonaRegProf1 != null
								&& especialidadReg1Original == null)
							saveDetail("I", null, "tipo_documento",
									especialidadReg1, "DOCUMENTOS_PERSONA");
						if (docPersonaRegProf1 != null
								&& especialidadReg1Original != null
								&& !especialidadReg1Original.trim().isEmpty()
								&& !especialidadReg1Original
										.equalsIgnoreCase(especialidadReg1))
							saveDetail("U", especialidadReg1Original,
									"tipo_documento", especialidadReg1,
									"DOCUMENTOS_PERSONA");

						// Registro Profesional 2
						DocumentosPersona docPersonaRegProf2 = new DocumentosPersona();
						docPersonaRegProf2 = existeDoc(REG_PROF_2);
						if (docPersonaRegProf2 == null) {
							if (nroReg2 != null && !nroReg2.trim().isEmpty())
								saveDetail("I", null, "tipo_documento",
										nroReg2, "DOCUMENTOS_PERSONA");
						}
						if (docPersonaRegProf2 != null
								&& nroReg2Original == null)
							saveDetail("I", null, "tipo_documento", nroReg2,
									"DOCUMENTOS_PERSONA");

						if (docPersonaRegProf2 != null
								&& nroReg2Original != null
								&& !nroReg2Original.trim().isEmpty()
								&& !nroReg2Original.equalsIgnoreCase(nroReg2))
							saveDetail("U", nroReg2Original, "tipo_documento",
									nroReg2, "DOCUMENTOS_PERSONA");

						if (docPersonaRegProf2 != null
								&& especialidadReg2Original == null)
							saveDetail("I", null, "tipo_documento",
									especialidadReg2, "DOCUMENTOS_PERSONA");

						if (docPersonaRegProf2 != null
								&& especialidadReg2Original != null
								&& !especialidadReg2Original.trim().isEmpty()
								&& !especialidadReg2Original
										.equalsIgnoreCase(especialidadReg2))
							saveDetail("U", especialidadReg2Original,
									"tipo_documento", especialidadReg2,
									"DOCUMENTOS_PERSONA");
						// RUC
						DocumentosPersona docRuc = new DocumentosPersona();
						docRuc = existeDoc(RUC);
						if (docRuc == null && ruc != null
								&& !ruc.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", ruc,
									"DOCUMENTOS_PERSONA");
						if (docRuc != null && rucOriginal == null)
							saveDetail("I", null, "tipo_documento", ruc,
									"DOCUMENTOS_PERSONA");
						if (docRuc != null && rucOriginal != null
								&& !rucOriginal.trim().isEmpty()
								&& !rucOriginal.equalsIgnoreCase(ruc))
							saveDetail("U", rucOriginal, "tipo_documento", ruc,
									"DOCUMENTOS_PERSONA");

						// DOCUMENTO MILITAR
						DocumentosPersona docuMilitar = new DocumentosPersona();
						docuMilitar = existeDoc(DOC_MIL);
						if (docuMilitar == null) {
							if (docMilitar != null
									&& !docMilitar.trim().isEmpty())
								saveDetail("I", null, "tipo_documento",
										docMilitar, "DOCUMENTOS_PERSONA");
						}
						if (docuMilitar != null && docMilitarOriginal == null)
							saveDetail("I", null, "tipo_documento", docMilitar,
									"DOCUMENTOS_PERSONA");
						if (docuMilitar != null
								&& docMilitarOriginal != null
								&& !docMilitarOriginal.trim().isEmpty()
								&& !docMilitarOriginal
										.equalsIgnoreCase(docMilitar))
							saveDetail("U", docMilitarOriginal,
									"tipo_documento", docMilitar,
									"DOCUMENTOS_PERSONA");

						// Registro de conducir
						DocumentosPersona docPersonaRegConducir = new DocumentosPersona();
						docPersonaRegConducir = existeDoc(REG_COND);
						if (docPersonaRegConducir == null
								&& regConducir != null
								&& !regConducir.trim().isEmpty())
							saveDetail("I", null, "tipo_documento",
									regConducir, "DOCUMENTOS_PERSONA");

						if (docPersonaRegConducir != null
								&& regConducirOriginal == null)
							saveDetail("I", null, "tipo_documento",
									regConducir, "DOCUMENTOS_PERSONA");

						if (docPersonaRegConducir != null
								&& regConducirOriginal != null
								&& !regConducirOriginal.trim().isEmpty()
								&& !regConducirOriginal
										.equalsIgnoreCase(regConducir))
							saveDetail("U", regConducirOriginal,
									"tipo_documento", regConducir,
									"DOCUMENTOS_PERSONA");

						if (docPersonaRegConducir != null
								&& categoriaOriginal == null)
							saveDetail("I", null, "tipo_documento", categoria,
									"DOCUMENTOS_PERSONA");

						if (docPersonaRegConducir != null
								&& categoriaOriginal != null
								&& !categoriaOriginal
										.equalsIgnoreCase(categoria))
							saveDetail("U", categoriaOriginal,
									"tipo_documento", categoria,
									"DOCUMENTOS_PERSONA");
						// pasaporte
						DocumentosPersona docuPasaporte = new DocumentosPersona();
						docuPasaporte = existeDoc(PASAPORTE);
						if (docuPasaporte == null && pasaporte != null
								&& !pasaporte.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", pasaporte,
									"DOCUMENTOS_PERSONA");
						if (docuPasaporte != null && pasaporteOriginal == null)
							saveDetail("I", null, "tipo_documento", pasaporte,
									"DOCUMENTOS_PERSONA");
						if (docuPasaporte != null
								&& pasaporteOriginal != null
								&& !pasaporteOriginal.trim().isEmpty()
								&& !pasaporteOriginal
										.equalsIgnoreCase(pasaporte))
							saveDetail("U", pasaporteOriginal,
									"tipo_documento", pasaporte,
									"DOCUMENTOS_PERSONA");
					}
				}

			}

		}
		/**
		 * Si no existe persona en la tabla audit crea la cabecera e inserta
		 * registro en detalle
		 */
		else {
			auditLegajo = new AuditLegajo();
			auditLegajo.setEstado(2);
			auditLegajo.setFechaAlta(new Date());
			auditLegajo.setPersona(persona);
			auditLegajo.setFechaMod(new Date());
			auditLegajo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			auditLegajo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(auditLegajo);
			for (CamposLegajo p : listaCampos) {
				/**
				 * Si existe persona en la tabla audit crea un nuevo regitro en
				 * detalle
				 */

				if (p.getTabla().equalsIgnoreCase("PERSONA")) {
					/**
					 * Verifica que cada campo auditable haya sido cambiado
					 */
					// Campo documento_identidad
					if (p.getCampo().equalsIgnoreCase("documento_identidad")) {
						if (persona.getDocumentoIdentidad() != null
								&& !persona.getDocumentoIdentidad().trim()
										.isEmpty())
							saveDetail("I", null, "documento_identidad",
									persona.getDocumentoIdentidad(), "PERSONA");
					}
					// Campo id_pais_expedicion_doc
					if (p.getCampo().equalsIgnoreCase("id_pais_expedicion_doc")) {
						saveDetail("I", null, "id_pais_expedicion_doc",
								idPaisNac + "", "PERSONA");
					}
					// Campo sexo
					if (p.getCampo().equalsIgnoreCase("sexo")) {

						String sexo = em.find(Referencias.class, idSexo)
								.getDescAbrev();

						saveDetail("I", null, "sexo", sexo, "PERSONA");

					}
					// Campos nombres
					if (p.getCampo().equalsIgnoreCase("nombres")) {
						saveDetail("I", null, "nombres", persona.getNombres(),
								"PERSONA");
					}
					// Campos apellidos
					if (p.getCampo().equalsIgnoreCase("apellidos")) {
						saveDetail("I", null, "apellidos",
								persona.getApellidos(), "PERSONA");
					}
					// Campos fecha_nacimiento
					if (p.getCampo().equalsIgnoreCase("fecha_nacimiento")) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"dd-MM-yyyy");
						String fecAct = sdf
								.format(persona.getFechaNacimiento());
						saveDetail("I", null, "fecha_nacimiento", fecAct,
								"PERSONA");
					}

					// Campos telefonos
					if (p.getCampo().equalsIgnoreCase("telefonos")) {
						saveDetail("I", null, "telefonos",
								persona.getTelefonos(), "PERSONA");
					}
				}
				if (p.getTabla().equalsIgnoreCase("DOCUMENTOS_PERSONA")) {
					/**
					 * Verifica que cada campo auditable haya sido cambiado
					 */
					// Campo tipo documento
					if (p.getCampo().equalsIgnoreCase("tipo_documento")) {
						// Registro Profesional 1
						if (nroReg1 != null && !nroReg1.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", nroReg1,
									"DOCUMENTOS_PERSONA");

						if (especialidadReg1 != null
								&& !especialidadReg1.trim().isEmpty())
							saveDetail("I", null, "tipo_documento",
									especialidadReg1, "DOCUMENTOS_PERSONA");
						// Registro Profesional 2

						if (nroReg2 != null && !nroReg2.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", nroReg2,
									"DOCUMENTOS_PERSONA");

						if (especialidadReg2 != null
								&& !especialidadReg2.trim().isEmpty())
							saveDetail("I", null, "tipo_documento",
									especialidadReg2, "DOCUMENTOS_PERSONA");
						// RUC
						if (ruc != null && !ruc.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", ruc,
									"DOCUMENTOS_PERSONA");

						// DOCUMENTO MILITAR

						if (docMilitar != null && !docMilitar.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", docMilitar,
									"DOCUMENTOS_PERSONA");

						// Registro de conducir

						if (regConducir != null
								&& !regConducir.trim().isEmpty())
							saveDetail("I", null, "tipo_documento",
									regConducir, "DOCUMENTOS_PERSONA");

						if (categoria != null && !categoria.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", categoria,
									"DOCUMENTOS_PERSONA");
						// pasaporte

						if (pasaporte != null && !pasaporte.trim().isEmpty())
							saveDetail("I", null, "tipo_documento", pasaporte,
									"DOCUMENTOS_PERSONA");
					}
				}

			}

		}
	}

	private void saveDetail(String accion, String valorAnterior, String campo,
			String valorNuevo, String tabla) {
		if (valorNuevo != null && !valorNuevo.trim().isEmpty()) {
			AuditLegajoDet auditLegajoDet = new AuditLegajoDet();
			auditLegajoDet.setAccion(accion);
			if (valorAnterior != null)
				auditLegajoDet.setAnterior(valorAnterior);
			auditLegajoDet.setAuditLegajo(auditLegajo);
			auditLegajoDet.setCampo(campo);
			auditLegajoDet.setEstado(2);
			auditLegajoDet.setFechaAlta(new Date());
			auditLegajoDet.setNuevo(valorNuevo);
			auditLegajoDet.setTabla(tabla);
			auditLegajoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(auditLegajoDet);
		}

	}

	private Boolean existePersona() {
		auditLegajo = new AuditLegajo();
		Query q = em.createQuery("select a from AuditLegajo a "
				+ "  where a.persona.idPersona = " + persona.getIdPersona());
		List<AuditLegajo> listaAudit = q.getResultList();
		if (listaAudit.isEmpty())
			return false;
		auditLegajo = listaAudit.get(0);
		return true;
	}

	private List<CamposLegajo> obtenerCamposLegajo() {
		Query q = em.createQuery("select c from CamposLegajo c "
				+ "  where c.ficha = " + new Long("1"));
		return q.getResultList();
	}

	public void listenerImagenes(UploadEvent event) {
		idDoc = null;
		rFileUploader = new RespuestaFileUploader();
		rFileUploader = seteadorFileUploads(event);
		if (rFileUploader != null) {
			uploadItem = rFileUploader.getItem();
			uploadedFile = rFileUploader.getByteArray();
			/*
			 * elArchivo = tblArchivoProveedorHome.getInstance();
			 * mimeImagenActiva = elArchivo.getMtypeRuc();
			 * elArchivo.setRuc(rFileUploader.getByteArray());
			 * elArchivo.setMtypeRuc(rFileUploader.getItem().getContentType());
			 * elArchivo.setNombreRuc(rFileUploader.getItem().getFileName());
			 * imagenActiva = "btnVerRuc";
			 */
		}
	}

	public void activarImagen(String elIdImagen) throws IOException {
		imagenActiva = elIdImagen;

	}

	public RespuestaFileUploader seteadorFileUploads(UploadEvent event) {
		RespuestaFileUploader respuesta = null;
		try {
			UploadItem item = event.getUploadItem();
			item.getFile();
			FileInputStream fis;
			try {
				fis = new FileInputStream(item.getFile());
			} catch (FileNotFoundException e1) {
				return null;
			}
			long length = item.getFileSize();
			if (length > Integer.MAX_VALUE) {
				return null;
			}
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			try {
				while (offset < bytes.length
						&& (numRead = fis.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return null;
			}
			if (offset < bytes.length) {
				try {
					throw new IOException(
							"No se pudo leer el archivo por completo "
									+ item.getFileName());
				} catch (IOException e) {
					return null;
				}
			}
			try {
				fis.close();
			} catch (IOException e) {
				return null;
			}
			// guarda en la instancia del objeto
			respuesta = new RespuestaFileUploader();
			respuesta.setByteArray(bytes);

			respuesta.setItem(item);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return respuesta;
	}

	public RespuestaFileUploader seteadorFile(File archivo) {
		RespuestaFileUploader respuesta = null;
		try {

			FileInputStream fis;
			try {
				fis = new FileInputStream(archivo);
			} catch (FileNotFoundException e1) {
				return null;
			}
			long length = archivo.length();
			if (length > Integer.MAX_VALUE) {
				return null;
			}
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			try {
				while (offset < bytes.length
						&& (numRead = fis.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return null;
			}
			if (offset < bytes.length) {
				try {
					throw new IOException(
							"No se pudo leer el archivo por completo ");
				} catch (IOException e) {
					return null;
				}
			}
			try {
				fis.close();
			} catch (IOException e) {
				return null;
			}
			// guarda en la instancia del objeto
			respuesta = new RespuestaFileUploader();
			respuesta.setByteArray(bytes);
			uploadedFile = bytes;

			respuesta.setItem(item);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return respuesta;
	}

	public void eliminarImagen() {
		Documentos docu = new Documentos();
		try {
			docu = em.find(Documentos.class, idDoc);
			docu.setActivo(false);
			docu.setFechaMod(new Date());
			docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(docu);
			persona.setDocumentos(null);
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona.setFechaMod(new Date());
			em.merge(persona);
			em.flush();
			uploadedFile = null;
			idDoc = null;
			idDocGuardado = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public byte[] genImage() throws IOException {

		if (imagenActiva.equalsIgnoreCase("btnVerImg")) {
			mimeImagenActiva = uploadItem.getContentType();
			if (uploadItem.getData() != null)
				return (uploadItem.getData());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void findDocsCI() {

		
		
		
		documentoDTOList = em.createQuery(
				" Select d from Documentos d, DatosEspecificos dat "
						+ " where d.activo= TRUE  and d.persona.idPersona = "
						+ idPersona 
						+ " and d.datosEspecificos.idDatosEspecificos = dat.idDatosEspecificos "
						+ " and dat.descripcion = 'COPIA DE CEDULA DE IDENTIDAD'"
						+ " order by d.fechaAlta desc ")
				.getResultList();

	}
	
	private List<Documentos> findDocCI() {

		Query q = null;
		//idTipoDocCI = (long)82;
		
		q = em.createQuery(" Select d from Documentos d, DatosEspecificos dat "
				+ " where d.activo= TRUE  and d.persona.idPersona = "
				+ idPersona 
				+ " and d.datosEspecificos.idDatosEspecificos = dat.idDatosEspecificos "
				+ " and dat.descripcion = 'COPIA DE CEDULA DE IDENTIDAD'"
				+ " and dat.valorAlf = 'LEG_DP' "
				+ " order by d.fechaAlta desc ");
		return q.getResultList();
		
		

	}
	
	
	
	private List<Documentos> findDocCvidaresidencia() {

		Query q = null;
		//idTipoDocCI = (long)82;
		
		q = em.createQuery(" Select d from Documentos d, DatosEspecificos dat "
				+ " where d.activo= TRUE  and d.persona.idPersona = "
				+ idPersona 
				+ " and d.datosEspecificos.idDatosEspecificos = dat.idDatosEspecificos "
				+ " and dat.valorAlf = 'LEG_DP' "
				+ " and dat.descripcion = 'CERTIFICADO DE VIDA Y RESIDENCIA'");
		
		
		return q.getResultList();
		
		

	}
	
	
	
	private List<Documentos> findDocCPartidaNacimiento() {

		Query q = null;
		
		
		q = em.createQuery(" Select d from Documentos d, DatosEspecificos dat "
				+ " where d.activo= TRUE  and d.persona.idPersona = "
				+ idPersona 
				+ " and d.datosEspecificos.idDatosEspecificos = dat.idDatosEspecificos "
				+ " and dat.valorAlf = 'LEG_DP' "
				+ " and dat.descripcion = 'COPIA DE PARTIDA DE NACIMIENTO'");
		
		
		return q.getResultList();
		
		

	}
	
	
	
	private List<Documentos> getDocCI(Long nro_doc_id) {

		Query q = null;
		
		
		q = em.createQuery("select d from Documentos d "
				+ "  where d.iddocumento = " + nro_doc_id);
		//System.out.println("66"+q.getResultList());
		return q.getResultList();
		
		

	}
	
	/*
	public String procesar(){
		
		if(persona.getTelContacto() != null)
			return em.find(Referencias.class, persona.getTelContacto() .getDescAbrev();
		else
			return "";
	}*/
	
	

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

	public Long getIdDptoNac() {
		return idDptoNac;
	}

	public void setIdDptoNac(Long idDptoNac) {
		this.idDptoNac = idDptoNac;
	}

	public Long getIdCiudadNac() {
		return idCiudadNac;
	}

	public void setIdCiudadNac(Long idCiudadNac) {
		this.idCiudadNac = idCiudadNac;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public Long getIdSexo() {
		return idSexo;
	}

	public void setIdSexo(Long idSexo) {
		this.idSexo = idSexo;
	}

	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
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

	public void changeNameDoc2() {

	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getMimeImagenActiva() {
		return mimeImagenActiva;
	}

	public void setMimeImagenActiva(String mimeImagenActiva) {
		this.mimeImagenActiva = mimeImagenActiva;
	}

	public UploadItem getUploadItem() {
		return uploadItem;
	}

	public void setUploadItem(UploadItem uploadItem) {
		this.uploadItem = uploadItem;
	}

	public Long getIdNacionalidad() {
		return idNacionalidad;
	}

	public void setIdNacionalidad(Long idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}

	public Long getIdPaisNac() {
		return idPaisNac;
	}

	public void setIdPaisNac(Long idPaisNac) {
		this.idPaisNac = idPaisNac;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public Long getIdEtnia() {
		return idEtnia;
	}

	public void setIdEtnia(Long idEtnia) {
		this.idEtnia = idEtnia;
	}

	public Long getIdPaisResi() {
		return idPaisResi;
	}

	public void setIdPaisResi(Long idPaisResi) {
		this.idPaisResi = idPaisResi;
	}

	public Long getIdDptoResi() {
		return idDptoResi;
	}

	public void setIdDptoResi(Long idDptoResi) {
		this.idDptoResi = idDptoResi;
	}

	public Long getIdCiudadResi() {
		return idCiudadResi;
	}

	public void setIdCiudadResi(Long idCiudadResi) {
		this.idCiudadResi = idCiudadResi;
	}

	public List<SelectItem> getDepartamentosResidenciaSelecItem() {
		return departamentosResidenciaSelecItem;
	}

	public void setDepartamentosResidenciaSelecItem(
			List<SelectItem> departamentosResidenciaSelecItem) {
		this.departamentosResidenciaSelecItem = departamentosResidenciaSelecItem;
	}

	public List<SelectItem> getCiudadSelecResidenciaItem() {
		return ciudadSelecResidenciaItem;
	}

	public void setCiudadSelecResidenciaItem(
			List<SelectItem> ciudadSelecResidenciaItem) {
		this.ciudadSelecResidenciaItem = ciudadSelecResidenciaItem;
	}

	public Long getIdLocalidadDir() {
		return idLocalidadDir;
	}

	public void setIdLocalidadDir(Long idLocalidadDir) {
		this.idLocalidadDir = idLocalidadDir;
	}

	public List<SelectItem> getLocalidadDirSelecItem() {
		return localidadDirSelecItem;
	}

	public void setLocalidadDirSelecItem(List<SelectItem> localidadDirSelecItem) {
		this.localidadDirSelecItem = localidadDirSelecItem;
	}

	public String getNroReg1() {
		return nroReg1;
	}

	public void setNroReg1(String nroReg1) {
		this.nroReg1 = nroReg1;
	}

	public String getEspecialidadReg1() {
		return especialidadReg1;
	}

	public void setEspecialidadReg1(String especialidadReg1) {
		this.especialidadReg1 = especialidadReg1;
	}

	public String getNroReg2() {
		return nroReg2;
	}

	public void setNroReg2(String nroReg2) {
		this.nroReg2 = nroReg2;
	}

	public String getEspecialidadReg2() {
		return especialidadReg2;
	}

	public void setEspecialidadReg2(String especialidadReg2) {
		this.especialidadReg2 = especialidadReg2;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getDocMilitar() {
		return docMilitar;
	}

	public void setDocMilitar(String docMilitar) {
		this.docMilitar = docMilitar;
	}

	public String getRegConducir() {
		return regConducir;
	}

	public void setRegConducir(String regConducir) {
		this.regConducir = regConducir;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPasaporte() {
		return pasaporte;
	}

	public void setPasaporte(String pasaporte) {
		this.pasaporte = pasaporte;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Integer getREG_PROF_1() {
		return REG_PROF_1;
	}

	public void setREG_PROF_1(Integer rEG_PROF_1) {
		REG_PROF_1 = rEG_PROF_1;
	}

	public Integer getREG_PROF_2() {
		return REG_PROF_2;
	}

	public void setREG_PROF_2(Integer rEG_PROF_2) {
		REG_PROF_2 = rEG_PROF_2;
	}

	public Integer getRUC() {
		return RUC;
	}

	public void setRUC(Integer rUC) {
		RUC = rUC;
	}

	public Integer getDOC_MIL() {
		return DOC_MIL;
	}

	public void setDOC_MIL(Integer dOC_MIL) {
		DOC_MIL = dOC_MIL;
	}

	public Integer getREG_COND() {
		return REG_COND;
	}

	public void setREG_COND(Integer rEG_COND) {
		REG_COND = rEG_COND;
	}

	public Integer getPASAPORTE() {
		return PASAPORTE;
	}

	public void setPASAPORTE(Integer pASAPORTE) {
		PASAPORTE = pASAPORTE;
	}

	public AuditLegajo getAuditLegajo() {
		return auditLegajo;
	}

	public void setAuditLegajo(AuditLegajo auditLegajo) {
		this.auditLegajo = auditLegajo;
	}

	public String getImagenActiva() {
		return imagenActiva;
	}

	public void setImagenActiva(String imagenActiva) {
		this.imagenActiva = imagenActiva;
	}

	public RespuestaFileUploader getrFileUploader() {
		return rFileUploader;
	}

	public void setrFileUploader(RespuestaFileUploader rFileUploader) {
		this.rFileUploader = rFileUploader;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}
	
	public Long getIdDocCI() {
		return idDocCI;
	}

	public void setIdDocCI(Long idDocCI) {
		this.idDocCI = idDocCI;
	}

	public Long getIdDocGuardado() {
		return idDocGuardado;
	}

	public void setIdDocGuardado(Long idDocGuardado) {
		this.idDocGuardado = idDocGuardado;
	}

	public String getCiOriginal() {
		return ciOriginal;
	}

	public void setCiOriginal(String ciOriginal) {
		this.ciOriginal = ciOriginal;
	}

	public Long getIdPaisNacOriginal() {
		return idPaisNacOriginal;
	}

	public void setIdPaisNacOriginal(Long idPaisNacOriginal) {
		this.idPaisNacOriginal = idPaisNacOriginal;
	}

	public String getSexoOriginal() {
		return sexoOriginal;
	}

	public void setSexoOriginal(String sexoOriginal) {
		this.sexoOriginal = sexoOriginal;
	}

	public String getNombreOriginal() {
		return nombreOriginal;
	}

	public void setNombreOriginal(String nombreOriginal) {
		this.nombreOriginal = nombreOriginal;
	}

	public String getApellidoOriginal() {
		return apellidoOriginal;
	}

	public void setApellidoOriginal(String apellidoOriginal) {
		this.apellidoOriginal = apellidoOriginal;
	}

	public Date getFechaNacOriginal() {
		return fechaNacOriginal;
	}

	public void setFechaNacOriginal(Date fechaNacOriginal) {
		this.fechaNacOriginal = fechaNacOriginal;
	}

	public String getTelefonoOriginal() {
		return telefonoOriginal;
	}

	public void setTelefonoOriginal(String telefonoOriginal) {
		this.telefonoOriginal = telefonoOriginal;
	}

	public String getNroReg1Original() {
		return nroReg1Original;
	}

	public void setNroReg1Original(String nroReg1Original) {
		this.nroReg1Original = nroReg1Original;
	}

	public String getEspecialidadReg1Original() {
		return especialidadReg1Original;
	}

	public void setEspecialidadReg1Original(String especialidadReg1Original) {
		this.especialidadReg1Original = especialidadReg1Original;
	}

	public String getNroReg2Original() {
		return nroReg2Original;
	}

	public void setNroReg2Original(String nroReg2Original) {
		this.nroReg2Original = nroReg2Original;
	}

	public String getEspecialidadReg2Original() {
		return especialidadReg2Original;
	}

	public void setEspecialidadReg2Original(String especialidadReg2Original) {
		this.especialidadReg2Original = especialidadReg2Original;
	}

	public String getRucOriginal() {
		return rucOriginal;
	}

	public void setRucOriginal(String rucOriginal) {
		this.rucOriginal = rucOriginal;
	}

	public String getDocMilitarOriginal() {
		return docMilitarOriginal;
	}

	public void setDocMilitarOriginal(String docMilitarOriginal) {
		this.docMilitarOriginal = docMilitarOriginal;
	}

	public String getRegConducirOriginal() {
		return regConducirOriginal;
	}

	public void setRegConducirOriginal(String regConducirOriginal) {
		this.regConducirOriginal = regConducirOriginal;
	}

	public String getCategoriaOriginal() {
		return categoriaOriginal;
	}

	public void setCategoriaOriginal(String categoriaOriginal) {
		this.categoriaOriginal = categoriaOriginal;
	}

	public String getPasaporteOriginal() {
		return pasaporteOriginal;
	}

	public void setPasaporteOriginal(String pasaporteOriginal) {
		this.pasaporteOriginal = pasaporteOriginal;
	}

	public byte[] getUploadedFileCI() {
		return uploadedFileCI;
	}

	public void setUploadedFileCI(byte[] uploadedFileCI) {
		this.uploadedFileCI = uploadedFileCI;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getFileNameCI() {
		return fileNameCI;
	}

	public void setFileNameCI(String fileNameCI) {
		this.fileNameCI = fileNameCI;
	}

	public String getContentTypeCI() {
		return contentTypeCI;
	}

	public void setContentTypeCI(String contentTypeCI) {
		this.contentTypeCI = contentTypeCI;
	}

	public List<Documentos> getDocumentoDTOList() {
		return documentoDTOList;
	}

	public void setDocumentoDTOList(List<Documentos> documentoDTOList) {
		this.documentoDTOList = documentoDTOList;
	}
	
	public Long getIdTipoDocCI() {
		return idTipoDocCI;
	}

	public void setIdTipoDocCI(Long idTipoDocCI) {
		this.idTipoDocCI = idTipoDocCI;
	}

	public String getNombreDocCI() {
		return nombreDocCI;
	}

	public void setNombreDocCI(String nombreDocCI) {
		this.nombreDocCI = nombreDocCI;
	}
	
	public String getContentTypeCVidaResidencia() {
		return contentTypeCVidaResidencia;
	}

	public void setContentTypeCVidaResidencia(String contentTypeCVidaResidencia) {
		this.contentTypeCVidaResidencia = contentTypeCVidaResidencia;
	}
	public byte[] getUploadedFileCVidaResidencia() {
		return uploadedFileCVidaResidencia;
	}

	public void setUploadedFileCVidaResidencia(byte[] uploadedFileCVidaResidencia) {
		this.uploadedFileCVidaResidencia = uploadedFileCVidaResidencia;
	}
	
	public String getFileNameCVidaResidencia() {
		return fileNameCVidaResidencia;
	}

	public void setFileNameCVidaResidencia(String fileNameCVidaResidencia) {
		this.fileNameCVidaResidencia = fileNameCVidaResidencia;
	}
	
	public Long getIdDocCVidaResidencia() {
		return idDocCVidaResidencia;
	}

	public void setIdDocCVidaResidencia(Long idDocCVidaResidencia) {
		this.idDocCVidaResidencia = idDocCVidaResidencia;
	}
	public String getNombreDocCVidaResidencia() {
		return nombreDocCVidaResidencia;
	}

	public void setNombreDocCVidaResidencia(String nombreDocCVidaResidencia) {
		this.nombreDocCVidaResidencia = nombreDocCVidaResidencia;
	}
	public UploadItem getItemCVidaResidencia() {
		return itemCVidaResidencia;
	}

	public void setItemCVidaResidencia(UploadItem itemCVidaResidencia) {
		this.itemCVidaResidencia = itemCVidaResidencia;
	}
	
	public Long getIdTipoDocCVidaResidencia() {
		return idTipoDocCVidaResidencia;
	}

	public void setIdTipoDocCVidaResidencia(Long idTipoDocCVidaResidencia) {
		this.idTipoDocCVidaResidencia = idTipoDocCVidaResidencia;
	}
	
	public List<Documentos> getDocumentoDTOList2() {
		return documentoDTOList2;
	}

	public void setDocumentoDTOList2(List<Documentos> documentoDTOList2) {
		this.documentoDTOList2 = documentoDTOList2;
	}
	
	public byte[] getUploadedFileCPartidaNacimiento() {
		return uploadedFileCPartidaNacimiento;
	}

	public void setUploadedFileCPartidaNacimiento(byte[] uploadedFileCPartidaNacimiento) {
		this.uploadedFileCPartidaNacimiento = uploadedFileCPartidaNacimiento;
	}
	
	public String getContentTypeCPartidaNacimiento() {
		return contentTypeCPartidaNacimiento;
	}

	public void setContentTypeCPartidaNacimiento(String contentTypeCPartidaNacimiento) {
		this.contentTypeCPartidaNacimiento = contentTypeCPartidaNacimiento;
	}
	
	public String getFileNameCPartidaNacimiento() {
		return fileNameCPartidaNacimiento;
	}

	public void setFileNameCPartidaNacimiento(String fileNameCPartidaNacimiento) {
		this.fileNameCPartidaNacimiento = fileNameCPartidaNacimiento;
	}
	
	public UploadItem getItemCPartidaNacimiento() {
		return itemCPartidaNacimiento;
	}

	public void setItemCPartidaNacimiento(UploadItem itemCPartidaNacimiento) {
		this.itemCPartidaNacimiento = itemCPartidaNacimiento;
	}
	
	public Long getIdTipoDocCPartidaNacimiento() {
		return idTipoDocCPartidaNacimiento;
	}

	public void setIdTipoDocCPartidaNacimiento(Long idTipoDocCPartidaNacimiento) {
		this.idTipoDocCPartidaNacimiento = idTipoDocCPartidaNacimiento;
	}
	
	public Long getIdDocCCertificadoNacimiento() {
		return idDocCCertificadoNacimiento;
	}

	public void setIdDocCCertificadoNacimiento(Long idDocCCertificadoNacimiento) {
		this.idDocCCertificadoNacimiento = idDocCCertificadoNacimiento;
	}

	public Long getIdDocCPartidaNacimiento() {
		return idDocCPartidaNacimiento;
	}

	public void setIdDocCPartidaNacimiento(Long idDocCPartidaNacimiento) {
		this.idDocCPartidaNacimiento = idDocCPartidaNacimiento;
	}
	
	public String getNombreDocCPartidaNacimiento() {
		return nombreDocCPartidaNacimiento;
	}

	public void setNombreDocCPartidaNacimiento(String nombreDocCPartidaNacimiento) {
		this.nombreDocCPartidaNacimiento = nombreDocCPartidaNacimiento;
	}

	public String getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}
	public String getTelContacto(){
		return this.telContacto;
	}
	public void setTelContacto(String telContacto){
		this.telContacto=telContacto;
	}
	
	public String getTelefonoPart() {
		return telefonoPart;
	}

	public void setTelefonoPart(String telefonoPart) {
		this.telefonoPart = telefonoPart;
	}

	public String getTelefonoLab() {
		return telefonoLab;
	}

	public void setTelefonoLab(String telefonoLab) {
		this.telefonoLab = telefonoLab;
	}
	public Long getIdTelContacto(){
		return idTelContacto;
	}
	public void setIdTelContacto(Long idTelContacto){
		
		this.idTelContacto = idTelContacto;
	}

	public String procesar(){
		if(idTelContacto != null)
			return em.find(Referencias.class, idTelContacto).getDescAbrev();
		else
			return "";
	}
	
	@SuppressWarnings("unchecked")
	private Long searchTelContacto(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'TEL_CONTACTO' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}
	public void eliminarIdDocCI() {
		Documentos docu = new Documentos();
		try {
			docu = em.find(Documentos.class, idDocCI);
			docu.setActivo(false);
			docu.setFechaMod(new Date());
			docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(docu);
			em.flush();
			uploadedFileCI = null;
			idDocCI = null;
			nombreDocCI = null;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"Error al eliminar el archivo, verifique");
		}

	}
	public void eliminarIdDocCPartidaNacimiento() {
		Documentos docu = new Documentos();
		try {
			docu = em.find(Documentos.class, idDocCPartidaNacimiento);
			docu.setActivo(false);
			docu.setFechaMod(new Date());
			docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(docu);
			em.flush();
			uploadedFileCPartidaNacimiento = null;
			idDocCPartidaNacimiento = null;
			nombreDocCPartidaNacimiento = null;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"Error al eliminar el archivo, verifique");
		}

	}
	public void eliminarIdDocCVidaResidencia() {
		Documentos docu = new Documentos();
		try {
			docu = em.find(Documentos.class, idDocCVidaResidencia);
			docu.setActivo(false);
			docu.setFechaMod(new Date());
			docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(docu);
			em.flush();
			uploadedFileCVidaResidencia = null;
			idDocCVidaResidencia = null;
			nombreDocCVidaResidencia = null;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"Error al eliminar el archivo, verifique");
		}

	}
}
