package py.com.excelsis.sicca.evaluacion.session;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.entity.PlantillaResolucionEval;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.ResolucionEval;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.entity.TagsEval;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("borradorResolucionEvaluacion")
public class BorradorResolucionEvaluacion implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private ResolucionEval resolucionEval = new ResolucionEval();
	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private Long idEvaluacionDesemp;

	private Long idConfiguracionUo;// OEE
	// private String idConcursoPuestoAgr;
	private Long idResolucionEvaluacion;
	private List<PlantillaResolucionEval> plResoEvalList;
	private PlantillaResolucionEval evaluacionSelect;
	private String fechaResolucion;
	private String anio;

	private String fromCU;

	General general;
	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private boolean primeraEntrada = true;
	private boolean modoUpdate = false;

	public void init() {
		try {
			if (primeraEntrada) {

				resolucionEval = new ResolucionEval();
				if (idEvaluacionDesemp != null)
					evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
							idEvaluacionDesemp);

				if (idResolucionEvaluacion != null) {
					resolucionEval = em.find(ResolucionEval.class,
							idResolucionEvaluacion);
					if (resolucionEval.getDocumento() != null)
						nombreDoc = resolucionEval.getDocumento()
								.getNombreFisicoDoc();
					modoUpdate = true;
					evaluacionDesempeno = resolucionEval
							.getEvaluacionDesempeno();
					idEvaluacionDesemp = evaluacionDesempeno
							.getIdEvaluacionDesempeno();
				} else
					resolucionEval.setFecha(new Date());

			}

			allPlantilla();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void allPlantilla() {
		try {
			plResoEvalList = em.createQuery(
					" Select p from PlantillaResolucionEval p "
							+ " order by  p.descripcion").getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void findPlantilla(Long idPlantilla) {
		evaluacionSelect = em.find(PlantillaResolucionEval.class, idPlantilla);
		if (evaluacionSelect.getTitulo() != null)
			resolucionEval
					.setTitulo(eliminarTags(evaluacionSelect.getTitulo()));
		if (evaluacionSelect.getVisto() != null)
			resolucionEval.setVisto(eliminarTags(evaluacionSelect.getVisto()));
		if (evaluacionSelect.getConsiderando() != null)
			resolucionEval.setConsiderando(eliminarTags(evaluacionSelect
					.getConsiderando()));
		if (evaluacionSelect.getPorTanto() != null) {
			String porTanto = evaluacionSelect.getPorTanto();
			Long id = evaluacionSelect.getIdPlantillaResolucionEval();
			resolucionEval.setPorTanto(eliminarTags(evaluacionSelect
					.getPorTanto()));
		}
		if (evaluacionSelect.getResuelve() != null)
			resolucionEval.setResuelve(eliminarTags(evaluacionSelect
					.getResuelve()));
		if (evaluacionSelect.getFirma() != null)
			resolucionEval.setFirma(eliminarTags(evaluacionSelect.getFirma()));
		if (evaluacionSelect.getCargo() != null)
			resolucionEval.setCargo(eliminarTags(evaluacionSelect.getCargo()));
		if (evaluacionSelect.getInstitucion() != null)
			resolucionEval.setInstitucion((eliminarTags(evaluacionSelect
					.getInstitucion())));

		resolucionEval.setPlantillaResolucionEval(evaluacionSelect);

	}

	// recibe el valor del campo retorna sin los tags en caso que contenga
	private String eliminarTags(String campo) {
		String result = "";
		for (int i = 0; i < campo.length(); i++) {
			if (String.valueOf(campo.charAt(i)).equals("<")
					&& String.valueOf(campo.charAt(i + 1)).equals("@")) {
				String tags = "";
				String valorTag;
				for (int j = i; j < campo.length(); j++) {
					tags += String.valueOf(campo.charAt(j));
					if (String.valueOf(campo.charAt(j)).equals(">")) {
						i = j;
						break;
					}

				}
				valorTag = valTags(tags);
				result += " " + valorTag + " ";
			} else
				result += String.valueOf(campo.charAt(i));

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private String valTags(String tags) {
		String valorFinal = "";
		List<TagsEval> tList = em.createQuery(
				"Select t from TagsEval t " + " where t.tag like '" + tags
						+ "'").getResultList();
		if (!tList.isEmpty()) {
			TagsEval tagsEval = tList.get(0);
			if (tags.equals("<@nombre_evaluacion@>")) {
				valorFinal = selectTags(idEvaluacionDesemp,
						tagsEval.getSelect());
			} else if (tags.equals("<@sujetos@>")) {
				valorFinal = selectTags(idEvaluacionDesemp,
						tagsEval.getSelect());
			} else if (tags.equals("<@unidades_organizativas_evaluadas@>")) {
				valorFinal = selectTags(idEvaluacionDesemp,
						tagsEval.getSelect());
			}
		}
		return valorFinal;
	}

	@SuppressWarnings("unchecked")
	private String selectTags(Long id, String select) {
		String result = "";

		String[] arrayString = select.split("@");
		result += arrayString[0];
		String[] arrayCad = arrayString[1].split("id_evaluacion");
		result += id;
		if (arrayCad.length > 0)
			result += arrayCad[1];

		if (!result.equals("")) {
			List<String> query = em.createNativeQuery(result.toString())
					.getResultList();
			if (!query.isEmpty()) {
				String val = "";
				for (int i = 0; i < query.size(); i++)
					val += query.get(i)+";";
				return val;
			}
		}

		return result;
	}

	public void abrirDoc() {
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(resolucionEval
				.getDocumento().getIdDocumento(), usuarioLogueado
				.getIdUsuario());

	}

	public void findFecha() {
		String mes = "";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");

		switch (resolucionEval.getFecha().getMonth()) {
		case 0:
			mes = "Enero";
			break;
		case 1:
			mes = "Febrero";
			break;

		case 2:
			mes = "Marzo";
			break;

		case 3:
			mes = "Abril";
			break;

		case 4:
			mes = "Mayo";
			break;

		case 5:
			mes = "Junio";
			break;

		case 6:
			mes = "Julio";
			break;

		case 7:
			mes = "Agosto";
			break;

		case 8:
			mes = "Setiembre";
			break;

		case 9:
			mes = "Octubre";
			break;

		case 10:
			mes = "Noviembre";
			break;

		case 11:
			mes = "Diciembre";
			break;
		}
		fechaResolucion = resolucionEval.getLugar() + ", "
				+ resolucionEval.getFecha().getDate() + " de " + mes + " de "
				+ sdfSoloAnio.format(resolucionEval.getFecha());
		anio = "/" + sdfSoloAnio.format(resolucionEval.getFecha());
	}

	private boolean chekValores() {

		if (resolucionEval.getNro().intValue() <= 0) {
			statusMessages.add(Severity.ERROR,
					"El valor del Nro. Resolución  debe ser mayor a cero");
			return false;
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	public String save() {
		try {
			if (!chekValores())
				return null;
			if (resolucionEval.getFecha().after(new Date())) {
				statusMessages
						.add(Severity.ERROR,
								"La Fecha de Resolución no puede ser mayor a la fecha actual, verifique");
				return null;
			}
			if (!general.isFechaValida(resolucionEval.getFecha())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"La Fecha ingresada no es inválida, verifique");
				return null;
			}

			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return null;
				}
			}

			resolucionEval.setFechaAlta(new Date());
			resolucionEval.setUsuAlta(usuarioLogueado.getCodigoUsuario()
					.toUpperCase());
			resolucionEval.setEvaluacionDesempeno(em.find(
					EvaluacionDesempeno.class, idEvaluacionDesemp));
			em.persist(resolucionEval);
			em.flush();

			/**
			 * Para el Caso de documento adjuntos
			 * */
			if (item != null) {

				/**
				 * VALIDACION AGREGADA
				 * */
				String nomfinal = "";
				nomfinal = item.getFileName();
				String extension = nomfinal
						.substring(nomfinal.lastIndexOf("."));
				List<TipoArchivo> ta = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"El archivo supera el tamaño máximo permitido. Límite "
										+ ta.get(0).getTamanho()
										+ ta.get(0).getUnidMedida()
										+ ", verifique");
						return null;
					}
				}
				/**
				 * FIN
				 * */

				documento();
				if (idDoc != null) {

					if (idDoc.longValue() == -8) {
						statusMessages
								.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDoc.longValue() == -7) {
						statusMessages
								.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDoc.longValue() == -6) {
						statusMessages
								.add("El archivo que desea levantar ya existe. Seleccione otro");
						return null;
					}
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setIdTabla(resolucionEval.getIdResolucionEval());
					doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
					doc.setFechaMod(new Date());
					resolucionEval.setDocumento(em
							.find(Documentos.class, idDoc));
					em.merge(resolucionEval);
					em.flush();

				} else {
					statusMessages
							.add("Error al adjuntar el registro. Verifique");
					modoUpdate = false;
					return null;
				}

			}

			idResolucionEvaluacion = resolucionEval.getIdResolucionEval();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			modoUpdate = true;
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	 public void print() {
		 try {
			 if(idResolucionEvaluacion==null){
				 statusMessages.add("Debe guardar para poder imprimir,Verifique");
				 return;
			 }
			 
			 if (resolucionEval == null)
				 resolucionEval= em.find(ResolucionEval.class,idResolucionEvaluacion);
			 
			    SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				Connection conn = JpaResourceBean.getConnection();
				  
				HashMap<String, Object> param = new HashMap<String, Object>();
			        param.put("usuario", usuarioLogueado.getCodigoUsuario());
			        param.put("idReso", idResolucionEvaluacion);
			        ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
					param.put("path_logo", servletContext.getRealPath("/img/"));
				    param.put("REPORT_CONNECTION", conn);
				    param.put("fecha", fechaResolucion);
				    
			if (resolucionEval != null)
				    param.put("nroAnio","RESOLUCION SFP N"+ resolucionEval.getNro()+"/"+sdfSoloAnio.format(resolucionEval.getFecha()));
				   
				   
				  
				 JasperReportUtils.respondPDF("CU562", param, false, conn, usuarioLogueado);
				
					
				
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }

	@SuppressWarnings("unchecked")
	public String update() {
		modoUpdate = true;
		try {
			if (!chekValores())
				return null;
			if (resolucionEval.getFecha().after(new Date())) {
				statusMessages
						.add(Severity.ERROR,
								"La Fecha de Resolución no puede ser mayor a la fecha actual, verifique");
				return null;
			}
			if (!general.isFechaValida(resolucionEval.getFecha())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"La Fecha ingresada no es inválida, verifique");
				return null;
			}

			/**
			 * PARA EL CASO DE DOCUMENTO ADJUNTO
			 * 
			 **/

			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return null;
				}

			}

			if (item != null) {
				/**
				 * VALIDACION AGREGADA
				 * */
				String nomfinal = "";
				nomfinal = item.getFileName();
				String extension = nomfinal
						.substring(nomfinal.lastIndexOf("."));
				List<TipoArchivo> ta = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
				if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
					if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"El archivo supera el tamaño máximo permitido. Límite "
										+ ta.get(0).getTamanho()
										+ ta.get(0).getUnidMedida()
										+ ", verifique");
						return null;
					}
				}
				/**
				 * FIN
				 * */
			}
			Long idDoc = null;
			if (resolucionEval.getDocumento() != null && item != null) {
				if (resolucionEval.getDocumento().getIdDocumento() != null) {
					Documentos docDB = em.find(Documentos.class, resolucionEval
							.getDocumento().getIdDocumento());
					if (!docDB.getNombreFisicoDoc().toLowerCase()
							.equals(nombreDoc.toLowerCase())
							|| !docDB.getTamanhoDoc().equals(
									String.valueOf(item.getFileSize()))) {// significa
																			// que
																			// ubo
																			// algun
																			// cambio
																			// se
																			// envia
																			// los
																			// parametros
																			// del
																			// item
						idDoc = AdmDocAdjuntoFormController.editar(
								item.getFile(),
								Integer.valueOf(item.getFileSize()),
								item.getFileName(), item.getContentType(),
								docDB.getUbicacionFisica(),
								"resolucionHomologacion_edit",
								docDB.getIdDocumento(), findResolucion(),
								usuarioLogueado.getIdUsuario(),
								resolucionEval.getIdResolucionEval(),
								"Resolucion");
						if (idDoc != null) {

							if (idDoc.longValue() == -8) {
								statusMessages
										.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
								return null;
							}
							if (idDoc.longValue() == -7) {
								statusMessages
										.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
								return null;
							}
							if (idDoc.longValue() == -6) {
								statusMessages
										.add("El archivo que desea levantar ya existe. Seleccione otro");
								return null;
							}

							resolucionEval.setDocumento(em.find(
									Documentos.class, idDoc));
							em.merge(resolucionEval);
							em.flush();

						} else {
							statusMessages
									.add("Error al adjuntar el registro. Verifique");
							return null;
						}
					}// porque no hubo modificacion! nohace nada!

				}
			}
			if (inputFile == null && item != null) {// no habia ningun archivo y
													// se carga

				if (resolucionEval.getDocumento() == null) {
					String direccionFisica = "";
					SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
					String anio = sdfSoloAnio.format(new Date());
					direccionFisica = obtenerDirFisica();
					idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
							item.getFileSize(), item.getFileName(),
							item.getContentType(), direccionFisica,
							"resolucionEvaluacion_edit", null,
							findResolucion(), usuarioLogueado.getIdUsuario(),
							resolucionEval.getIdResolucionEval(),
							"ResolucionEval");
					if (idDoc != null) {

						if (idDoc.longValue() == -8) {
							statusMessages
									.add("La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
							return null;
						}
						if (idDoc.longValue() == -7) {
							statusMessages
									.add("El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
							return null;
						}
						if (idDoc.longValue() == -6) {
							statusMessages
									.add("El archivo que desea levantar ya existe. Seleccione otro");
							return null;
						}

						resolucionEval.setDocumento(em.find(Documentos.class,
								idDoc));
						// em.merge(resolucionHomologacion);
						// em.flush();

					} else {
						statusMessages
								.add("Error al adjuntar el registro. Verifique");
						return null;
					}

				}

			}
			em.clear();
			resolucionEval.setFechaMod(new Date());
			resolucionEval.setUsuMod(usuarioLogueado.getCodigoUsuario()
					.toUpperCase());
			em.merge(resolucionEval);
			em.flush();
			String msg = "updated";

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// METODOS PRIVADOS
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	public void documento() throws NamingException {

		String nombrePantalla = "resolucionEvaluacion_edit";
		String direccionFisica = "";
		direccionFisica = obtenerDirFisica();
		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, findResolucion(),
				usuarioLogueado.getIdUsuario(), "Resolucion");

	}

	private String obtenerDirFisica() {
		return File.separator
				+ "SICCA"
				+ File.separator
				+ anio
				+ File.separator
				+ "OEE"
				+ File.separator
				+ evaluacionDesempeno.getConfiguracionUoCab().getIdConfiguracionUo()
				+ File.separator
				+  "E"
				+ File.separator
				+ evaluacionDesempeno.getIdEvaluacionDesempeno();
	}

	@SuppressWarnings("unchecked")
	private Long findResolucion() {
		List<DatosEspecificos> dEspecificos = em.createQuery(
				"Select d from DatosEspecificos d "
						+ " where lower(d.descripcion) like 'resolucion' ")
				.getResultList();
		if (dEspecificos.isEmpty())
			return null;

		return dEspecificos.get(0).getIdDatosEspecificos();
	}

	public ResolucionEval getResolucionEval() {
		return resolucionEval;
	}

	public void setResolucionEval(ResolucionEval resolucionEval) {
		this.resolucionEval = resolucionEval;
	}

	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}

	public List<PlantillaResolucionEval> getPlResoEvalList() {
		return plResoEvalList;
	}

	public void setPlResoEvalList(List<PlantillaResolucionEval> plResoEvalList) {
		this.plResoEvalList = plResoEvalList;
	}

	public PlantillaResolucionEval getEvaluacionSelect() {
		return evaluacionSelect;
	}

	public void setEvaluacionSelect(PlantillaResolucionEval evaluacionSelect) {
		this.evaluacionSelect = evaluacionSelect;
	}

	public String getFechaResolucion() {
		return fechaResolucion;
	}

	public void setFechaResolucion(String fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
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

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public boolean isModoUpdate() {
		return modoUpdate;
	}

	public void setModoUpdate(boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

	public Long getIdResolucionEvaluacion() {
		return idResolucionEvaluacion;
	}

	public void setIdResolucionEvaluacion(Long idResolucionEvaluacion) {
		this.idResolucionEvaluacion = idResolucionEvaluacion;
	}

	public Long getIdEvaluacionDesemp() {
		return idEvaluacionDesemp;
	}

	public void setIdEvaluacionDesemp(Long idEvaluacionDesemp) {
		this.idEvaluacionDesemp = idEvaluacionDesemp;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

}
