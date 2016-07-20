package py.com.excelsis.sicca.session.form;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.primefaces.model.UploadedFile;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.TipoReclamo;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ReclamoSugerenciaHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("adminReclamoSugPortalFormController")
public class AdminReclamoSugPortalFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ReclamoSugerenciaHome reclamoSugerenciaHome;

	ReclamoSugerencia reclamoSugerencia;
	TipoReclamo tipoReclamo;

	/**
	 * DOCUMENTO ADJUNTO
	 **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private String nombrePantalla;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	public void setFileName(String fileName) {
		this.fileName = fileName;

	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		reclamoSugerencia = new ReclamoSugerencia();
		reclamoSugerencia = reclamoSugerenciaHome.getInstance();
		if (reclamoSugerenciaHome.isIdDefined()) {
			tipoReclamo = TipoReclamo.getTipoReclamoPorId(reclamoSugerencia.getReclamoSugerencia());
		} else {

		}
	}

	public static File crearFile(String nombreArchivo, byte[] elByteArray) {
		try {
			String pathDir = System.getProperty("jboss.home.dir") + System.getProperty("file.separator") + "temp" +  System.getProperty("file.separator");
			
			File file = new File(pathDir);			
			try {
				if (!file.exists())
					if (!file.mkdirs())
						throw new IOException("No se pudo crear el directorio: "
								+ file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			file = new File(pathDir + nombreArchivo);
			file.createNewFile();
			
			// prints absolute path
	        //System.out.print("File path: "+file.getAbsolutePath());
	        
			FileInputStream fileInputStream = new FileInputStream(file);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(elByteArray);
			OutputStream outputStream = new FileOutputStream(file);
			int data;
			while ((data = byteArrayInputStream.read()) != -1) {
				outputStream.write(data);
			}

			fileInputStream.close();
			outputStream.close();
			return file;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void crearUploadItem(String fileName, int fileSize, String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType, crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String save() {

		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType, uploadedFile);
			else {
				statusMessages.add(Severity.INFO, "No se permite la carga de ese tipo de archivos.");
				return "fail";
			}
		}

		try {
			if (tipoReclamo == null || tipoReclamo.getId() == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
				return null;
			}
			reclamoSugerencia.setReclamoSugerencia(tipoReclamo.getValor());
			reclamoSugerencia.setFechaReclamoSugerencia(new Date());
			reclamoSugerencia.setOrigen("PO");
			reclamoSugerenciaHome.setInstance(reclamoSugerencia);
			String result = reclamoSugerenciaHome.persist();
			if (result.equals("persisted")) {
				adddocumentos();
				if (idDoc != null)
					reclamoSugerenciaHome.getInstance().setDocumentos(em.find(Documentos.class, idDoc));
				
				reclamoSugerenciaHome.update();
				idDoc = null;
			}
			reclamoSugerenciaHome.clearInstance();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			reclamoSugerenciaHome.clearInstance();
		}
		return null;
	}

	public void miListenerAdjuntar(UploadEvent event) {
		try {

			item = event.getUploadItem();
			item.getFile();
			FileInputStream fis;
			try {
				fis = new FileInputStream(item.getFile());
			} catch (FileNotFoundException e1) {
				return;
			}

			long length = item.getFileSize();

			if (length > 5242880) {
				return;
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
				return;
			}

			if (offset < bytes.length) {
				try {
					throw new IOException("No se pudo leer el archivo por completo "
						+ item.getFileName());
				} catch (IOException e) {
					return;
				}
			}

			try {
				fis.close();
			} catch (IOException e) {
				return;
			}

			item.getFileSize();
			nombreDoc = item.getFileName();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void adddocumentos() {

		/**
		 * Para el Caso de documento adjuntos
		 */
		if (item != null) {
			try {
				documento();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			if (idDoc != null) {

				if (idDoc.longValue() == -8) {
					statusMessages.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return;
				}
				if (idDoc.longValue() == -7) {
					statusMessages.add("El archivo que desea levantar, supera el tama�o permitido. Seleccione otro");
					return;
				}
				if (idDoc.longValue() == -6) {
					statusMessages.add("El archivo que desea levantar ya existe. Seleccione otro");
					return;
				}

				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(reclamoSugerenciaHome.getInstance().getIdReclamoSugerencia());
				em.merge(doc);
				em.flush();
				item = null;
				nombreDoc = null;

			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return;
			}
		}
	}

	public void documento() throws NamingException {
		nombrePantalla = "reclamo_sugerencia";
		String direccionFisica = "";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		direccionFisica = "\\SICCA\\" + anio + "\\SFP\\" + "PORTAL";
		Long idTipoDoc = recuperarTipoDoc();

		idDoc =
			AdmDocAdjuntoFormController.guardarDirectoCU367(item, direccionFisica, nombrePantalla, idTipoDoc, "PORTAL", "ReclamoSugerencia");

	}

	@SuppressWarnings("unchecked")
	private Long recuperarTipoDoc() {
		String sql =
			"SELECT DE.ID_DATOS_ESPECIFICOS " + "FROM seleccion.datos_especificos DE "
				+ "JOIN seleccion.datos_generales DG "
				+ "ON DG.id_datos_generales = DE.id_datos_generales "
				+ "WHERE DE.VALOR_ALF = 'PORTAL' " + "AND DE.DESCRIPCION = 'DOC. RECLAMOS PORTAL' "
				+ "AND DG.descripcion = 'TIPOS DE DOCUMENTOS' " + "AND DE.ACTIVO = TRUE";

		List<Object[]> config = em.createNativeQuery(sql).getResultList();
		if (config == null) {
			return null;
		}
		Object obj1 = config.get(0);
		if (obj1 == null)
			return null;
		String v = obj1.toString();

		return new Long(v);
	}

	public void abrirDoc() {

		AdmDocAdjuntoFormController.abrirDocumentoFromCU(reclamoSugerencia.getDocumentos().getIdDocumento(), "PORTAL");

	}

	public ReclamoSugerencia getReclamoSugerencia() {
		return reclamoSugerencia;
	}

	public void setReclamoSugerencia(ReclamoSugerencia reclamoSugerencia) {
		this.reclamoSugerencia = reclamoSugerencia;
	}

	public TipoReclamo getTipoReclamo() {
		return tipoReclamo;
	}

	public void setTipoReclamo(TipoReclamo tipoReclamo) {
		this.tipoReclamo = tipoReclamo;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
