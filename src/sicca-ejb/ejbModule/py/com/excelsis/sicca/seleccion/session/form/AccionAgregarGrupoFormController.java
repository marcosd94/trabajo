package py.com.excelsis.sicca.seleccion.session.form;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.OrgDiscapacitadosPersona;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ComisionSeleccionCabHome;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("accionAgregarGrupoFormController")
public class AccionAgregarGrupoFormController implements Serializable {

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

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ConcursoHome concursoHome;
	

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(required = false)
	ComisionSeleccionCabHome comisionSeleccionCabHome;

	private Long idPais;
	private Long idConcurso;// recibe del CU que le llama
	NivelEntidadOeeUtil nivelEntidadOeeUtilCabecera = new NivelEntidadOeeUtil();
	private Concurso concurso;// enviado por el CU
	private String docIdentidad;
	private Persona persona = new Persona();
	private String puesto = null;
	private List<ComisionSeleccionDet> comisionSeleccionDetList;
	private Long idRol = null;
	private Long idTipoDoc = null;
	private boolean habPersona;
	private Long idperson = null;
	private Long idComisionSel;
	private String descripcionCab = null;
	private ComisionSeleccionCab comisionSeleccionCab= new ComisionSeleccionCab();
	private boolean habBtnAddPersons=false;
	private PersonaDTO personaDTO;
	private Integer anioActual;
	private String tituSuplente;
	/**
	 * DOCUMENTO ADJUNTO
	 **/
	private UploadItem item;
	private String nombreDoc;
	private String nombrePantalla;
	private Long idDoc;
	private Documentos documentos;
	private Boolean primeraEntrada = true;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	/****
	 * 
	 * */

	private boolean habPaisExp = true;
	private List<ComisionSeleccionDet> comSeleccionDetListRemove =
		new ArrayList<ComisionSeleccionDet>();
	private SeguridadUtilFormController seguridadUtilFormController;
	private String from;
	public void init() {

		concurso = concursoHome.getInstance();
		/* Incidencia 1014 */
		validarOee();
		/**/

		/* Inicio Incidencia 0000609. (antes de la incidencia no tenia la condicion de primera entrada) */
		if (primeraEntrada) {
			cargarAnhoActual();
			comSeleccionDetListRemove = new ArrayList<ComisionSeleccionDet>();
			comisionSeleccionDetList = new ArrayList<ComisionSeleccionDet>();

			if (idComisionSel != null) {
				findDet();
				comisionSeleccionCab = em.find(ComisionSeleccionCab.class, idComisionSel);
				descripcionCab = comisionSeleccionCab.getDescripcion();
			}
			primeraEntrada = false;
			setearVar();

		}
		/* End Incidencia 0000609 */

		cargarEntidades();// Trae las entidades segun el grupo que se envio

		comisionSeleccionCabHome.setInstance(comisionSeleccionCab);

	}

	private void validarOee() {
		/* Incidencia 1014 */
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		 

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
		/**/
	}

	@SuppressWarnings("unchecked")
	private void findDet() {
		comisionSeleccionDetList =
			em.createQuery("select d from ComisionSeleccionDet d "
				+ " where d.comisionSeleccionCab.idComisionSel=" + idComisionSel
				+ " and d.activo=true").getResultList();
	}

	@SuppressWarnings("unchecked")
	private void cargarEntidades() {
		nivelEntidadOeeUtil.setIdConfigCab(concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

	}

public void buscarPersona() throws Exception {
		
		/* Validaciones */
		if (idPais == null
			|| !seguridadUtilFormController.validarInput(idPais.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		if (docIdentidad == null || docIdentidad.equals("")
				|| !seguridadUtilFormController.validarInput(docIdentidad.toString(), TiposDatos.STRING.getValor(), null)) {
				return;
			}
		Pais pais = em.find(Pais.class, idPais);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2();
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			
		}
	}
	public void limpiarDatosPersona2() {
		persona = new Persona();
	}
	public void limpiarDatosPersona() {
		persona= new Persona();
		idperson=null;
		docIdentidad = null;
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

	public void eliminarDB(int index) {
		ComisionSeleccionDet d = comisionSeleccionDetList.get(index);
		comisionSeleccionDetList.remove(d);
		comSeleccionDetListRemove.add(d);
	
	}

	public void eliminar(int index) {

		comisionSeleccionDetList.remove(comisionSeleccionDetList.get(index));

	}

	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public void addItems() {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);

		if (persona.getIdPersona() == null || docIdentidad == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la persona. Verifique");
			return;
		}
		if (idRol == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Rol. Verifique");
			return;
		}
		if (tituSuplente == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar Titular/Suplente");
			return;
		}
		if (puesto == null || puesto.trim().equals("")) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el puesto");
			return;
		}
		if (descripcionCab == null || descripcionCab.trim().equals("")) {
			statusMessages.add(Severity.ERROR, "Dede ingresar la Descripci\u00F3n de Comisi\u00F3n. Verifique ");
			return;
		}
		if(sufc.contieneCaracter(puesto.trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return ;
		}
		if (!validarRepetido()) {
			return;
		}

		ComisionSeleccionDet det = new ComisionSeleccionDet();
		det.setRol(em.find(Rol.class, idRol));
		if (item != null) {
			if (idTipoDoc == null) {
				statusMessages.add(Severity.ERROR, "Debe seleccionar el Tipo de documento");
				return;
			}
			documentos = new Documentos();
			documentos.setActivo(true);
			documentos.setFechaAlta(new Date());
			documentos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			documentos.setMimetype(item.getContentType());
			documentos.setNombreTabla("ComisionSeleccionDet");
			documentos.setNombreFisicoDoc(item.getFileName());
			documentos.setNroDocumento(item.getFileSize());
			documentos.setTamanhoDoc(String.valueOf(item.getFileSize()));
			documentos.setUbicacionFisica(item.getFileName());
			documentos.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDoc));
			documentos.setInputFile(item.getFile());

		}
		det.setEquipoTecnico(false);
		det.setTitularSuplente(tituSuplente);
		det.setDocumentos(documentos);
		det.setPuesto(puesto);
		det.setPersona(persona);
		if (nivelEntidadOeeUtilCabecera.getIdConfigCab() != null) {
			det.setConfiguracionUo(new ConfiguracionUoCab());
			det.getConfiguracionUo().setIdConfiguracionUo(nivelEntidadOeeUtilCabecera.getIdConfigCab());
		}
		det.setTipo("CONCURSO");

		comisionSeleccionDetList.add(det);
		setearVar();
	}

	private void setearVar() {
		puesto = null;
		documentos = null;
		item = null;
		idRol = null;
		docIdentidad = null;
		persona = new Persona();
		nombreDoc = null;
		nivelEntidadOeeUtilCabecera.limpiar();
		idTipoDoc = null;
		tituSuplente = "T";
		idPais = idParaguay();
		habPaisExp = true;

	}

	private boolean validarRepetido() {
		if (comisionSeleccionDetList != null && comisionSeleccionDetList.size() > 0) {
			for (ComisionSeleccionDet det : comisionSeleccionDetList) {
				if (det.getPersona() != null
					&& det.getPersona().getIdPersona() != null
					&& det.getPersona().getIdPersona().longValue() == persona.getIdPersona().longValue()) {
					statusMessages.add(Severity.ERROR, "La persona que desea agregar ya se encuentra en la comisión. Verifique.");
					return false;
				}
			}
		}
		return true;
	}

	private String formatearLista(List<String> lista) {
		String respuesta = "";
		for (String o : lista) {
			respuesta += "," + o;
		}
		if (respuesta.isEmpty()) {
			return null;
		}
		respuesta = "(" + respuesta.replaceFirst(",", "") + ")";
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	public String save() {
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);

			if (comisionSeleccionDetList.isEmpty()) {
				statusMessages.add(Severity.ERROR, "Debe ingresar al menos un detalle");
				return null;
			}
			if (descripcionCab == null || descripcionCab.trim().equals("")) {
				statusMessages.add(Severity.ERROR, "Debe ingresar los Datos obligatorios");
				return null;
			}
			if(sufc.contieneCaracter(descripcionCab.trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
			comisionSeleccionCab.setFechaAlta(new Date());
			comisionSeleccionCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			comisionSeleccionCab.setConcurso(em.find(Concurso.class, concurso.getIdConcurso()));
			comisionSeleccionCab.setDescripcion(descripcionCab);

			em.persist(comisionSeleccionCab);
			List<String> listaPersona = new ArrayList<String>();
			em.flush();
			for (int i = 0; i < comisionSeleccionDetList.size(); i++) {

				ComisionSeleccionDet det = new ComisionSeleccionDet();
				det = comisionSeleccionDetList.get(i);
				listaPersona.add(det.getPersona().getIdPersona().toString());
				det.setComisionSeleccionCab(new ComisionSeleccionCab());
				det.getComisionSeleccionCab().setIdComisionSel(comisionSeleccionCab.getIdComisionSel());
				det.setFechaAlta(new Date());
				det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				det.setActivo(true);
				if (det.getDocumentos() != null) {
					String direccionFisica = "";
					SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
					String anio = sdfSoloAnio.format(new Date());
					direccionFisica =
						"\\SICCA\\" + anio + "\\OEE\\" + nivelEntidadOeeUtil.getIdConfigCab()
							+ "\\" 	+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "\\"
							+ concurso.getIdConcurso() + "\\CS";
					/**
					 * SEA AGREGO PARA VERIFICACION DE TAMAÑO
					 * */
					 String extension = det.getDocumentos().getNombreFisicoDoc().substring( det.getDocumentos().getNombreFisicoDoc().lastIndexOf( "." ) );
						List<TipoArchivo> ta = em
								.createQuery(
										"select t from TipoArchivo t "
												+ " where lower(t.extension) like '" 
												+ extension.toLowerCase() + "'").getResultList();
						if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
							Integer tam=Integer.parseInt(det.getDocumentos().getTamanhoDoc());
							if (tam.intValue() > ta.get(0).getUnidMedidaByte()
									.intValue()) {
								statusMessages
										.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
								return null;
							}
						}
					/**
					 * FIN
					 * */
					idDoc =
						AdmDocAdjuntoFormController.editar(det.getDocumentos().getInputFile(), Integer.parseInt(det.getDocumentos().getTamanhoDoc()), det.getDocumentos().getNombreFisicoDoc(), det.getDocumentos().getMimetype(), direccionFisica, "comisionSeleccionCabEdit_edit", null, det.getDocumentos().getDatosEspecificos().getIdDatosEspecificos(), usuarioLogueado.getIdUsuario(), det.getPersona().getIdPersona(), "ComisionSeleccionCab");
					if (idDoc != null) {

						if (idDoc.longValue() == -8) {
							statusMessages.add(Severity.ERROR, "La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema, nombre: "
								+ det.getDocumentos().getNombreFisicoDoc());
							return null;
						}
						if (idDoc.longValue() == -7) {
							statusMessages.add(Severity.ERROR, "El archivo que desea levantar, supera el tamaño permitido. Seleccione otro, nombre: "
								+ det.getDocumentos().getNombreFisicoDoc());
							return null;
						}
						if (idDoc.longValue() == -6) {
							statusMessages.add(Severity.ERROR, "El archivo que desea levantar ya existe. Seleccione otro, nombre: "
								+ det.getDocumentos().getNombreFisicoDoc());
							return null;
						}

						det.setDocumentos(em.find(Documentos.class, idDoc));

					} else {
						statusMessages.add(Severity.ERROR, "Error al adjuntar el registro. Verifique");
						return null;
					}

				}

				em.persist(det);
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			if (concurso.getPcd()) {
				StringBuffer SQL = new StringBuffer();
				SQL.append(" SELECT ");
				SQL.append("     * ");
				SQL.append(" FROM ");
				SQL.append("     seleccion.org_discapacitados_persona ");
				SQL.append(" WHERE ");
				SQL.append("     seleccion.org_discapacitados_persona.activo = 'true' ");
				SQL.append(" AND seleccion.org_discapacitados_persona.id_persona IN  "
					+ formatearLista(listaPersona));
				Query q = em.createNativeQuery(SQL.toString(), OrgDiscapacitadosPersona.class);
				List lista = q.getResultList();
				if (lista.size() == 0) {
					statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle().getString("CU165_warnPcd"));
				}
			}
			primeraEntrada = true;
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public String update() {
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);

			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType, uploadedFile);
				else {
					statusMessages.add(Severity.ERROR, "No se permite la carga de ese tipo de archivos.");
					return null;
				}

			}
			if (comisionSeleccionDetList.isEmpty()) {
				statusMessages.add(Severity.ERROR, "Debe ingresar al menos un detalle");
				return null;
			}
			if (descripcionCab == null || descripcionCab.trim().equals("")) {
				statusMessages.add(Severity.ERROR, "Debe ingresar los Datos obligatorios");
				return null;
			}
			if(sufc.contieneCaracter(descripcionCab.trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
			List<String> listaPersona = new ArrayList<String>();

			/**
			 * SE ELIMINAN LOS REGISTROS INBCIDENCIA 0001005
			 **/

			for (int i = 0; i < comSeleccionDetListRemove.size(); i++) {
				ComisionSeleccionDet d =
					em.find(ComisionSeleccionDet.class, comSeleccionDetListRemove.get(i).getIdComisionSelDet());
				d.setActivo(false);
				d.setUsuMod(usuarioLogueado.getCodigoUsuario());
				d.setFechaMod(new Date());
				em.merge(d);
				if (d.getDocumentos() != null) {// inativamos el doc.
					Documentos doc = em.find(Documentos.class, d.getDocumentos().getIdDocumento());
					doc.setActivo(false);
					doc.setFechaMod(new Date());
					doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(doc);
				}

				em.flush();
			}
			/**
			 * FIN
			 **/

			comisionSeleccionCab.setFechaMod(new Date());
			comisionSeleccionCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
			comisionSeleccionCab.setConcurso(concurso);
			comisionSeleccionCab.setDescripcion(descripcionCab);
			em.merge(comisionSeleccionCab);
			em.flush();
			for (int i = 0; i < comisionSeleccionDetList.size(); i++) {
				ComisionSeleccionDet det = new ComisionSeleccionDet();
				det = comisionSeleccionDetList.get(i);
				listaPersona.add(det.getPersona().getIdPersona().toString());
				if (det.getIdComisionSelDet() != null) {
					det.setComisionSeleccionCab(comisionSeleccionCab);
					det.setFechaMod(new Date());
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(det);

				} else {
					det.setComisionSeleccionCab(comisionSeleccionCab);
					det.setFechaAlta(new Date());
					det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					det.setActivo(true);
					if (det.getDocumentos() != null) {
						String direccionFisica = "";
						SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
						String anio = sdfSoloAnio.format(new Date());
						direccionFisica =
							"\\SICCA\\" + anio + "\\OEE\\"
								+ nivelEntidadOeeUtil.getIdConfigCab()+ "\\"
								+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()
								+ "\\" + concurso.getIdConcurso() + "\\CS";
						
						/**
						 * SEA AGREGO PARA VERIFICACION DE TAMAÑO
						 * */
						 String extension = det.getDocumentos().getNombreFisicoDoc().substring( det.getDocumentos().getNombreFisicoDoc().lastIndexOf( "." ) );
							List<TipoArchivo> ta = em
									.createQuery(
											"select t from TipoArchivo t "
													+ " where lower(t.extension) like '" 
													+ extension.toLowerCase() + "'").getResultList();
							if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
								Integer tam=Integer.parseInt(det.getDocumentos().getTamanhoDoc());
								if (tam.intValue() > ta.get(0).getUnidMedidaByte()
										.intValue()) {
									statusMessages
											.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
									return null;
								}
							}
						/**
						 * FIN
						 * */
						
						idDoc =
							AdmDocAdjuntoFormController.editar(det.getDocumentos().getInputFile(), Integer.parseInt(det.getDocumentos().getTamanhoDoc()), det.getDocumentos().getNombreFisicoDoc(), det.getDocumentos().getMimetype(), direccionFisica, "comisionSeleccionCabEdit_edit", null, det.getDocumentos().getDatosEspecificos().getIdDatosEspecificos(), usuarioLogueado.getIdUsuario(), det.getPersona().getIdPersona(), "ComisionSeleccionCab");
						if (idDoc != null) {

							if (idDoc.longValue() == -8) {
								statusMessages.add(Severity.ERROR, "La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema, nombre:"
									+ det.getDocumentos().getNombreFisicoDoc());
								return null;
							}
							if (idDoc.longValue() == -7) {
								statusMessages.add(Severity.ERROR, "El archivo que desea levantar, supera el tamaño permitido. Seleccione otro, nombre:"
									+ det.getDocumentos().getNombreFisicoDoc());
								return null;
							}
							if (idDoc.longValue() == -6) {
								statusMessages.add(Severity.ERROR, "El archivo que desea levantar ya existe. Seleccione otro, nombre:"
									+ det.getDocumentos().getNombreFisicoDoc());
								return null;
							}

							det.setDocumentos(em.find(Documentos.class, idDoc));

						} else {
							statusMessages.add(Severity.ERROR, "Error al adjuntar el registro. Verifique");
							return null;
						}

					}

					em.persist(det);

				}

				em.flush();

			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			if (concurso.getPcd()) {
				StringBuffer SQL = new StringBuffer();
				SQL.append(" SELECT ");
				SQL.append("     * ");
				SQL.append(" FROM ");
				SQL.append("     seleccion.org_discapacitados_persona ");
				SQL.append(" WHERE ");
				SQL.append("     seleccion.org_discapacitados_persona.activo = 'true' ");
				SQL.append(" AND seleccion.org_discapacitados_persona.id_persona IN  "
					+ formatearLista(listaPersona));
				Query q = em.createNativeQuery(SQL.toString(), OrgDiscapacitadosPersona.class);
				List lista = q.getResultList();
				if (lista.size() == 0) {
					statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle().getString("CU165_warnPcd"));
				}
			}
			primeraEntrada = false;
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}

	}

	private void crearUploadItem(String fileName, int fileSize, String contentType, byte[] file) {
		item =
			new UploadItem(fileName, fileSize, contentType, AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	public String toFindPersona() {
		idperson=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/comisionSeleccionCab/ComisionSeleccionCabEdit";
	}

	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/seleccion/comisionSeleccionCab/ComisionSeleccionCabEdit";
	}
	

	

	private SinNivelEntidad nivelEntidadMaxAnho(BigDecimal codNivelEntidad) {

		Query q =
			em.createQuery("select sinNivelEntidad from SinNivelEntidad sinNivelEntidad "
				+ "WHERE sinNivelEntidad.nenCodigo =" + codNivelEntidad
				+ " and sinNivelEntidad.activo is true "
				+ "order by sinNivelEntidad.aniAniopre DESC");
		List<SinNivelEntidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}





	

//	@SuppressWarnings("unchecked")
//	private ConfiguracionUoCab obtenerOeeCorrespondiente() {
//		String sql =
//			"  select  configurac0_.*    " + "  from "
//				+ "  planificacion.configuracion_uo_cab configurac0_, "
//				+ "  planificacion.entidad entidad1_, " + "  sinarh.sin_entidad sinentidad2_ "
//				+ "	where    configurac0_.id_entidad=entidad1_.id_entidad "
//				+ "  and sinentidad2_.nen_codigo= " + nivelEntidad.getNenCodigo()
//				+ "  and sinentidad2_.ent_codigo= " + sinEntidads.getEntCodigo()
//				+ "  and configurac0_.orden=" + ordenUnidOrg + "  and configurac0_.activo=true "
//				+ "  and entidad1_.anho=" + anioActual;
//		List<ConfiguracionUoCab> lista = new ArrayList<ConfiguracionUoCab>();
//		lista = em.createNativeQuery(sql, ConfiguracionUoCab.class).getResultList();
//		if (lista.size() > 0)
//			return lista.get(0);
//		else
//			return null;
//	}

	private void cargarAnhoActual() {
		Calendar cal = Calendar.getInstance();
		anioActual = cal.get(Calendar.YEAR);
	}

	// GETTERS Y SETTERS
	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	

	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}

	public void setComisionSeleccionCab(ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public List<ComisionSeleccionDet> getComisionSeleccionDetList() {
		return comisionSeleccionDetList;
	}

	public void setComisionSeleccionDetList(List<ComisionSeleccionDet> comisionSeleccionDetList) {
		this.comisionSeleccionDetList = comisionSeleccionDetList;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public boolean isHabPersona() {
		return habPersona;
	}

	public void setHabPersona(boolean habPersona) {
		this.habPersona = habPersona;
	}

	public Long getIdperson() {
		return idperson;
	}

	public void setIdperson(Long idperson) {
		this.idperson = idperson;
	}

	public Long getIdComisionSel() {
		return idComisionSel;
	}

	public void setIdComisionSel(Long idComisionSel) {
		this.idComisionSel = idComisionSel;
	}

	public String getDescripcionCab() {
		return descripcionCab;
	}

	public void setDescripcionCab(String descripcionCab) {
		this.descripcionCab = descripcionCab;
	}


	public Integer getAnioActual() {
		return anioActual;
	}

	public void setAnioActual(Integer anioActual) {
		this.anioActual = anioActual;
	}

	public String getTituSuplente() {
		return tituSuplente;
	}

	public void setTituSuplente(String tituSuplente) {
		this.tituSuplente = tituSuplente;
	}

	

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
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

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilCabecera() {
		return nivelEntidadOeeUtilCabecera;
	}

	public void setNivelEntidadOeeUtilCabecera(
			NivelEntidadOeeUtil nivelEntidadOeeUtilCabecera) {
		this.nivelEntidadOeeUtilCabecera = nivelEntidadOeeUtilCabecera;
	}

	public boolean isHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}

	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	

}
