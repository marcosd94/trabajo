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

import py.com.excelsis.sicca.dto.AuditoriaDetDTO;
import py.com.excelsis.sicca.entity.AuditLegajo;
import py.com.excelsis.sicca.entity.AuditLegajoDet;
import py.com.excelsis.sicca.entity.AuditLegajoObs;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.DocumentosPersona;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.legajo.session.ValDatosFamiliares677FC;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.RespuestaFileUploader;

@Scope(ScopeType.CONVERSATION)
@Name("validacionDatosPersonalesLegajoFC")
public class ValidacionDatosPersonalesLegajoFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5680876767714760390L;
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

	@In(required = false, create = true)
	ValDatosFamiliares677FC valDatosFamiliares677FC;

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
	private String selectedTab = null;
	private Persona persona;
	private Documentos documentos;
	private AuditLegajo auditLegajo;
	private RespuestaFileUploader rFileUploader;
	private AuditLegajoObs auditLegajoObs;
	private UploadItem uploadItem;
	private byte[] uploadedFile;

	private UploadItem item;
	private String contentType;
	private String fileName;
	private String nombreDoc;
	// private String observacion;
	private Integer REG_PROF_1;
	private Integer REG_PROF_2;
	private Integer RUC;
	private Integer DOC_MIL;
	private Integer REG_COND;
	private Integer PASAPORTE;
	String mimeImagenActiva = "";
	private List<SelectItem> departamentosSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecItem = new ArrayList<SelectItem>();

	private List<SelectItem> departamentosResidenciaSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecResidenciaItem = new ArrayList<SelectItem>();
	private List<SelectItem> localidadDirSelecItem = new ArrayList<SelectItem>();
	private List<AuditLegajoDet> listaAuditoria = new ArrayList<AuditLegajoDet>();
	private List<AuditoriaDetDTO> listaAuditoriaDTO = new ArrayList<AuditoriaDetDTO>();

	private final String TAB_DATOS_PERSONALES = "tabDatosPersonales";
	private final String TAB_DATOS_FAMILIARES = "tabDatosFamiliares";

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		persona = em.find(Persona.class, idPersona);
		cargarDatosPersona(persona.getIdPersona());
		obtenerEstados();
		obtenerDatosOtrosDocs();
		if (existePersona())
			obtenerDatosObs();
		if (persona.getDocumentos() != null) {
			idDoc = persona.getDocumentos().getIdDocumento();
			idDocGuardado = idDoc;
			Documentos documentos = new Documentos();
			documentos = em.find(Documentos.class, idDoc);
			File arch =
				AdmDocAdjuntoFormController.recuperarImagen(idDoc, usuarioLogueado.getIdUsuario());
			crearUploadItem(documentos.getNombreFisicoDoc(), new Integer(documentos.getTamanhoDoc()), documentos.getMimetype(), arch);
			seteadorFile(arch);

		}
		valDatosFamiliares677FC.setIdPersona(idPersona);
		valDatosFamiliares677FC.init();
	}

	private void cargarDatosPersona(Long idPersona) {
		idSexo = searchSexo(persona.getSexo());
		idEstadoCivil = searchEstadoCivil(persona.getEstadoCivil());
		if (persona.getDatosEspecificos() != null)
			idNacionalidad = persona.getDatosEspecificos().getIdDatosEspecificos();
		if (persona.getCiudadByIdCiudadNac() != null) {
			idPaisNac = persona.getCiudadByIdCiudadNac().getDepartamento().getPais().getIdPais();
			updateDepartamento();
			idDptoNac = persona.getCiudadByIdCiudadNac().getDepartamento().getIdDepartamento();
			updateCiudad();
			idCiudadNac = persona.getCiudadByIdCiudadNac().getIdCiudad();
		} else {
			updateDepartamento();
			updateCiudad();
		}
		if (persona.getDatosEspecificosEtnia() != null)
			idEtnia = persona.getDatosEspecificosEtnia().getIdDatosEspecificos();

		if (persona.getCiudadByIdCiudadDirecc() != null) {
			idPaisResi =
				persona.getCiudadByIdCiudadDirecc().getDepartamento().getPais().getIdPais();
			updateDepartamentoResidencia();
			idDptoResi = persona.getCiudadByIdCiudadDirecc().getDepartamento().getIdDepartamento();
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

	/**
	 * Search de valores de combos
	 */
	@SuppressWarnings("unchecked")
	private Long searchEstadoCivil(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista =
				em.createQuery(" select o from " + Referencias.class.getName() + " o"
					+ " WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' and o.descAbrev = '"
					+ c + "' ORDER BY o.dominio, o.descLarga").getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	@SuppressWarnings("unchecked")
	private Long searchSexo(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista =
				em.createQuery(" select o from " + Referencias.class.getName() + " o"
					+ " WHERE o.activo = true and o.dominio = 'SEXO' and o.descAbrev = '" + c
					+ "' ORDER BY o.dominio, o.descLarga").getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	/**
	 * Operaciones para cargar los diferentes combos
	 */

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

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
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
		if (idPaisNac != null) {
			departamentoList.getPais().setIdPais(idPaisNac);
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

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosResidenciaSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
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

		ciudadSelecResidenciaItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecResidenciaItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
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

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		localidadDirSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idLocalidadDir = null;
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

		localidadDirSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			localidadDirSelecItem.add(new SelectItem(bar.getIdBarrio(), bar.getDescripcion()));
		}
	}

	/**
	 * nacionalidad
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));

		List<Object[]> n =
			em.createNativeQuery("Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r,"
				+ "seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales"
				+ " Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();

		while (it.hasNext()) {
			Object[] fila = it.next();
			if (fila[0] != null)
				si.add(new SelectItem(fila[0], fila[1] != null ? fila[1].toString() : ""));
		}

		return si;
	}

	private void obtenerEstados() {
		REG_PROF_1 = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC", "REG_PROF_1");
		REG_PROF_2 = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC", "REG_PROF_2");
		RUC = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC", "RUC");
		DOC_MIL = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC", "DOC_MIL");
		REG_COND = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC", "REG_COND");
		PASAPORTE = seleccionUtilFormController.getIdEstadosReferencia("LEGAJO_DOC", "PASAPORTE");
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

	private Boolean existePersona() {

		auditLegajo = new AuditLegajo();
		Query q =
			em.createQuery("select a from AuditLegajo a " + "  where a.persona.idPersona = "
				+ persona.getIdPersona());
		List<AuditLegajo> l = q.getResultList();
		if (l.isEmpty())
			return false;
		auditLegajo = l.get(0);
		obtenerDetallesLegajo();
		return true;
	}

	private void obtenerDetallesLegajo() {
		Query q =
			em.createQuery("select a from AuditLegajoDet a "
				+ "  where a.auditLegajo.auditLegajo = " + auditLegajo.getAuditLegajo()
				+ " and a.estado = 2 order by a.fechaAlta");
		listaAuditoria = q.getResultList();
		listaAuditoriaDTO = new ArrayList<AuditoriaDetDTO>();
		for (AuditLegajoDet det : listaAuditoria) {
			AuditoriaDetDTO dto = new AuditoriaDetDTO();
			dto.setAuditLegajoDet(det);
			dto.setSeleccionado(false);
			listaAuditoriaDTO.add(dto);
		}
	}

	private void obtenerDatosObs() {
		Query q =
			em.createQuery("select doc from AuditLegajoObs doc "
				+ "  where doc.auditLegajo.auditLegajo = " + auditLegajo.getAuditLegajo()
				+ " and doc.ficha = " + 1);
		List<AuditLegajoObs> listaObs = q.getResultList();
		if (listaObs.isEmpty())
			auditLegajoObs = new AuditLegajoObs();
		else
			auditLegajoObs = listaObs.get(0);
	}

	private List<DocumentosPersona> obtenerDocPersonas() {
		Query q =
			em.createQuery("select doc from DocumentosPersona doc "
				+ "  where doc.persona.idPersona = " + persona.getIdPersona());
		return q.getResultList();
	}

	/**
	 * Operaciones correspondientes a las imagenes
	 */
	private void crearUploadItem(String fileName, int fileSize, String contentType, File file) {
		uploadItem = new UploadItem(fileName, fileSize, contentType, file);

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
					&& (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return null;
			}
			if (offset < bytes.length) {
				try {
					throw new IOException("No se pudo leer el archivo por completo ");
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

	public void listenerImagenes(UploadEvent event) {
		idDoc = null;
		rFileUploader = new RespuestaFileUploader();
		rFileUploader = seteadorFileUploads(event);
		if (rFileUploader != null) {
			uploadItem = rFileUploader.getItem();
			uploadedFile = rFileUploader.getByteArray();

		}
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
					&& (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return null;
			}
			if (offset < bytes.length) {
				try {
					throw new IOException("No se pudo leer el archivo por completo "
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

	public void activarImagen(String elIdImagen) throws IOException {
		imagenActiva = elIdImagen;

	}

	public void selectTab(int tabIndex) {
		switch (tabIndex) {
		case 1:
			selectedTab = TAB_DATOS_PERSONALES;
			break;
		case 2:
			selectedTab = TAB_DATOS_PERSONALES;
			break;

		default:
			selectedTab = TAB_DATOS_PERSONALES;
			break;
		}
	}

	private boolean validarFechasNac(Date fecha) {
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			Integer anioActual = Integer.parseInt(sdfSoloAnio.format(new Date()));
			Integer anioNacimiento = Integer.parseInt(sdfSoloAnio.format(fecha));
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
		List<Referencias> maxima =
			em.createQuery("Select m from Referencias m " + " where m.dominio='EDAD_MAXIMA_LEGAL' ").getResultList();
		if (!maxima.isEmpty())
			return maxima.get(0).getValorNum();
		return 60;// en caso que no exista dato
	}

	@SuppressWarnings("unchecked")
	private Integer edadMinima() {
		List<Referencias> minima =
			em.createQuery("Select m from Referencias m " + " where m.dominio='EDAD_MINIMA_LEGAL' ").getResultList();
		if (!minima.isEmpty())
			return minima.get(0).getValorNum();
		return 18;// en caso que no exista dato
	}

	private Boolean permitirGuardar() {
		statusMessages.clear();
		if (idSexo == null) {
			statusMessages.add(Severity.ERROR, "El campo Sexo es obligatorio. Verifique");
			return false;
		}
		if (!General.isFechaValida(persona.getFechaNacimiento())) {
			statusMessages.add(Severity.ERROR, "Fecha de Nacimiento inválida. Verifique");
			return false;
		}
		if (!validarFechasNac(persona.getFechaNacimiento())) {
			statusMessages.add(Severity.ERROR, "El rango de edades debe comprender entre "
				+ edadMinima() + " y " + edadMaxima());
			return false;
		}

		if (!General.isEmail(persona.getEMail().toLowerCase())) {
			statusMessages.add(Severity.ERROR, "Ingrese un mail valido. Verifique");
			return false;
		}
		if (idNacionalidad == null) {
			statusMessages.add(Severity.ERROR, "El campo Nacionalidad es obligatorio. Verifique");
			return false;
		}
		return true;
	}

	private DatosEspecificos obtenerDatosEspecificos() {
		String sql =
			"SELECT DATOS_ESPECIFICOS.* " + "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES "
				+ "ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE DATOS_GENERALES.DESCRIPCION = 'TIPOS DE DOCUMENTOS' "
				+ "AND DATOS_ESPECIFICOS.VALOR_ALF = 'FPOS' "
				+ "AND DATOS_ESPECIFICOS.ACTIVO = TRUE "
				+ "ORDER BY DATOS_ESPECIFICOS.DESCRIPCION ";
		List<DatosEspecificos> l =
			em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
		if (l.isEmpty())
			return null;
		return l.get(0);
	}

	private Boolean guardarDocumento() throws NamingException {
		if (rFileUploader != null) {
			String nombreTabla = "Persona";
			String pantalla = "validacion_datos_personales_legajo";
			String separator = File.separator;
			String ubicacionFisica =
				separator + "SICCA" + separator + "USUARIO_PORTAL" + separator
					+ persona.getDocumentoIdentidad() + "_" + persona.getIdPersona();
			DatosEspecificos datosEspecificos = obtenerDatosEspecificos();
			if (idDoc == null) {
				if (idDocGuardado != null) {
					Documentos documentos = em.find(Documentos.class, idDocGuardado);
					documentos.setActivo(false);
					documentos.setFechaMod(new Date());
					documentos.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(documentos);
				}
				idDoc =
					AdmDocAdjuntoFormController.guardarDirecto(rFileUploader.getItem(), ubicacionFisica, pantalla, datosEspecificos.getIdDatosEspecificos(), usuarioLogueado.getIdUsuario(), nombreTabla);
				if (idDoc != null) {
					if (idDoc.longValue() == -8) {
						statusMessages.add(Severity.ERROR, "La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return false;
					}
					if (idDoc.longValue() == -7) {
						statusMessages.add(Severity.ERROR, "El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return false;
					}
					if (idDoc.longValue() == -6) {
						statusMessages.add(Severity.ERROR, "El archivo que desea levantar ya existe. Seleccione otro");
						return false;
					}

				}
			}
		}
		return true;
	}

	private DocumentosPersona existeDoc(Integer val) {
		List<DocumentosPersona> lista = obtenerDocPersonas();
		for (DocumentosPersona p : lista) {
			if (p.getTipoDocumento().equals(val))
				return p;
		}
		return null;
	}

	public String save() {
		try {
			if (!permitirGuardar())
				return null;
			if (guardarDocumento()) {
				if (idDoc != null)
					persona.setDocumentos(em.find(Documentos.class, idDoc));
			} else {
				return null;
			}
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona.setFechaMod(new Date());
			em.merge(persona);
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
					if (especialidadReg1 != null && !especialidadReg1.trim().isEmpty())
						docPersona.setOtroDato(especialidadReg1);
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(nroReg1);
					if (especialidadReg1 != null && !especialidadReg1.trim().isEmpty())
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
					if (especialidadReg2 != null && !especialidadReg2.trim().isEmpty())
						docPersona.setOtroDato(especialidadReg2);
					em.persist(docPersona);
				} else {
					docPersona.setDocumentoNro(nroReg2);
					if (especialidadReg1 != null && !especialidadReg1.trim().isEmpty())
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

			Integer cont = 0;
			for (AuditoriaDetDTO dto : listaAuditoriaDTO) {
				if (dto.getSeleccionado() != null && dto.getSeleccionado()) {
					AuditLegajoDet det = new AuditLegajoDet();
					det = em.find(AuditLegajoDet.class, dto.getAuditLegajoDet().getIdAuditLegajoDet());
					cont++;
					det.setEstado(1);
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
					det.setFechaMod(new Date());
					det.getAuditLegajo().setFechaMod(new Date());
					det.getAuditLegajo().setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(det);

				}
			}
			if (cont == listaAuditoriaDTO.size()) {
				auditLegajo.setEstado(1);
				auditLegajo.setFechaMod(new Date());
				auditLegajo.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(auditLegajo);
			}
			if (auditLegajoObs.getObservacion() != null
				&& !auditLegajoObs.getObservacion().trim().isEmpty()) {
				if (auditLegajoObs.getIdAuditLegajoObs() == null) {
					auditLegajoObs.setFechaAlta(new Date());
					auditLegajoObs.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					auditLegajoObs.setFicha(new Long(1));
					auditLegajoObs.setAuditLegajo(auditLegajo);
					em.persist(auditLegajoObs);
				} else {
					auditLegajoObs.setFechaMod(new Date());
					auditLegajoObs.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(auditLegajoObs);
				}

			}

			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Se ha producido un error inesperado: "
				+ e.getMessage());

		}
		return null;
	}
	
	public void accion(Integer row){
		AuditLegajoDet au = new AuditLegajoDet();
		
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
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

	public Long getIdLocalidadDir() {
		return idLocalidadDir;
	}

	public void setIdLocalidadDir(Long idLocalidadDir) {
		this.idLocalidadDir = idLocalidadDir;
	}

	public Long getIdEtnia() {
		return idEtnia;
	}

	public void setIdEtnia(Long idEtnia) {
		this.idEtnia = idEtnia;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public Long getIdDocGuardado() {
		return idDocGuardado;
	}

	public void setIdDocGuardado(Long idDocGuardado) {
		this.idDocGuardado = idDocGuardado;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	public AuditLegajo getAuditLegajo() {
		return auditLegajo;
	}

	public void setAuditLegajo(AuditLegajo auditLegajo) {
		this.auditLegajo = auditLegajo;
	}

	public RespuestaFileUploader getrFileUploader() {
		return rFileUploader;
	}

	public void setrFileUploader(RespuestaFileUploader rFileUploader) {
		this.rFileUploader = rFileUploader;
	}

	public UploadItem getUploadItem() {
		return uploadItem;
	}

	public void setUploadItem(UploadItem uploadItem) {
		this.uploadItem = uploadItem;
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

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
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

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public List<SelectItem> getDepartamentosResidenciaSelecItem() {
		return departamentosResidenciaSelecItem;
	}

	public void setDepartamentosResidenciaSelecItem(List<SelectItem> departamentosResidenciaSelecItem) {
		this.departamentosResidenciaSelecItem = departamentosResidenciaSelecItem;
	}

	public List<SelectItem> getCiudadSelecResidenciaItem() {
		return ciudadSelecResidenciaItem;
	}

	public void setCiudadSelecResidenciaItem(List<SelectItem> ciudadSelecResidenciaItem) {
		this.ciudadSelecResidenciaItem = ciudadSelecResidenciaItem;
	}

	public List<SelectItem> getLocalidadDirSelecItem() {
		return localidadDirSelecItem;
	}

	public void setLocalidadDirSelecItem(List<SelectItem> localidadDirSelecItem) {
		this.localidadDirSelecItem = localidadDirSelecItem;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public List<AuditLegajoDet> getListaAuditoria() {
		return listaAuditoria;
	}

	public void setListaAuditoria(List<AuditLegajoDet> listaAuditoria) {
		this.listaAuditoria = listaAuditoria;
	}

	public AuditLegajoObs getAuditLegajoObs() {
		return auditLegajoObs;
	}

	public void setAuditLegajoObs(AuditLegajoObs auditLegajoObs) {
		this.auditLegajoObs = auditLegajoObs;
	}

	public List<AuditoriaDetDTO> getListaAuditoriaDTO() {
		return listaAuditoriaDTO;
	}

	public void setListaAuditoriaDTO(List<AuditoriaDetDTO> listaAuditoriaDTO) {
		this.listaAuditoriaDTO = listaAuditoriaDTO;
	}
	
	

}
