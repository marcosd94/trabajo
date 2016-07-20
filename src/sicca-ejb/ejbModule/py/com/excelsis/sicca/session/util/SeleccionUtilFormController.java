package py.com.excelsis.sicca.session.util;

import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.ws.core.StubExt;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.EvalReferencialTipoeval;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.ProcActividadRol;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoria;
import py.com.excelsis.sicca.entity.SinCategoriaContratados;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.movilidadLaboral.session.form.GestionarReasignacionInsercionMasivaCU710FC;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinObjList;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.LicenseNumber;
import py.com.excelsis.sicca.util.SinarhUtiles;
import py.com.excelsis.sicca.util.Utiles;
import wssfppersona.WSSFPPersona;
//import py.gov.sfp.wsidentificacionessfp.wsdl.RespuestaObtenerDatosPersona;
import _254._29._2._10.wsidentificacionessfp.wsdl.RespuestaObtenerDatosPersona;

@Scope(ScopeType.CONVERSATION)
@Name("seleccionUtilFormController")
public class SeleccionUtilFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3536773346937197789L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	public static final String WS_ESTADO_EXISTE = "EXISTE";
	public static final String WS_ESTADO_NUEVO = "NUEVO";
	public static final String WS_ESTADO_SERVIDOR_OCUPADO = "SERVIDOR OCUPADO";
	public static final String WS_ESTADO_NO_EXISTE = "NO EXISTE";
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	SinObjList sinObjList;

	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private Integer codigoObj;
	private String valorObj;
	private String codigoCategoria;
	private String categoria;
	private Integer monto;
	private PlantaCargoDet plantaCargoDet;
	private String codigoSinarh;// de la configuracionUoCab
	private SinAnx sinAnx;
	private Long idSinAnx;
	private Long idSinCategoria;

	public void init() {

	}

	/**
	 * Cuenta la cantidad de postulantes activos de un grupo
	 * 
	 * @param idGrupo
	 * @return
	 */

	public Integer countCantPostulantesActivos(Long idGrupo) {
		Query q = em
				.createQuery("select Postulacion from Postulacion Postulacion "
						+ " where Postulacion.activo is true and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ " and Postulacion.usuCancelacion is null and Postulacion.fechaCancelacion is null ");
		q.setParameter("idGrupo", idGrupo);
		List<Postulacion> lista = q.getResultList();
		return lista.size();

	}

	public Usuario usuarioPostulante(Long idUsuario) {
		Query q = em.createQuery("select Usuario from Usuario Usuario "
				+ "  where usuario.activo is true                "
				+ "  and usuario.usuAlta ='PORTAL'              "
				+ "  and usuario.persona.idPersona = :idUsuario          ");
		q.setParameter("idUsuario", idUsuario);
		Usuario usuario = (Usuario) q.getSingleResult();
		return usuario;
	}

	/**
	 * CU 347, generador de Pin
	 * 
	 * @return
	 */
	public String generarPIN() {
		try {
			StringBuffer var1 = new StringBuffer();
			var1.append("SELECT Max(Substring(x.pin, 1, 3)) AS codi, Substring(x.pin, 4, 6) AS numero,  ");
			var1.append(" Substring(x.pin, 1, 3) co FROM general.empleado_puesto AS x WHERE Cast ( ");
			var1.append("Substring(x.pin, 4, 6) AS INT) = (SELECT Max(Cast (Substring(p.pin, 4, 6) AS  ");
			var1.append("INT)) AS num FROM general.empleado_puesto p WHERE p.activo = TRUE AND  ");
			var1.append("Substring(p.pin, 1, 3) LIKE Substring(x.pin, 1, 3)) GROUP BY numero, co ORDER  ");
			var1.append("BY codi DESC ");

			String codLetra = "", codNum = "", ultCod = null, codResult = "";

			List<Object> rsl = em.createNativeQuery(var1.toString())
					.getResultList();
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				codLetra = (String) record[0];
				codNum = (String) record[1];
				break;
			}
			if (!codLetra.equals(""))
				ultCod = codLetra + codNum;
			if (ultCod != null) {
				LicenseNumber lic = new LicenseNumber(ultCod);
				lic.increment(1);
				System.out.println(lic);
				codResult = lic.toString();
			} else {
				codResult = "AAA001";
			}
			if (codResult.substring(3, 6).equals("000"))
				codResult = codResult.substring(0, 3) + "001";

			return codResult;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	public Integer cantPostulantesActivos(Long idGrupo) {
		Query q = em
				.createQuery("select count(Postulacion) from Postulacion Postulacion "
						+ " where Postulacion.activo is true and Postulacion.usuCancelacion is null "
						+ " and Postulacion.fechaCancelacion is null and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idGrupo", idGrupo);
		return ((Long) q.getSingleResult()).intValue();
	}

	public Integer cantPuestosActivos(Long idGrupo) {
		Query q = em
				.createQuery("select count(*) from ConcursoPuestoDet ConcursoPuestoDet "
						+ " where ConcursoPuestoDet.activo is true "
						+ " and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idGrupo", idGrupo);
		return ((Long) q.getSingleResult()).intValue();
	}

	public Integer diminuirPuesto(Long idGrupo) {
		// Cantidad de Postulantes Activos
		Integer cantPosActivos = cantPostulantesActivos(idGrupo);
		// Cantidad de Puestos Activos
		Integer cantPuestosActivos = cantPuestosActivos(idGrupo);
		if (cantPosActivos.intValue() <= cantPuestosActivos.intValue()) {
			// Diminuir
			return (cantPuestosActivos.intValue() - cantPosActivos.intValue()) + 1;
		}
		return null;
	}

	public Integer diminuirPuesto(Integer cantPosActivos,
			Integer cantPuestosActivos) {
		if (cantPosActivos.intValue() <= cantPuestosActivos.intValue()) {
			// Diminuir
			return (cantPuestosActivos.intValue() - cantPosActivos.intValue()) + 1;
		}
		return null;
	}

	/**
	 * Crea un UploadItem con los parametros recibidos
	 * 
	 * @param fileName
	 * @param fileSize
	 * @param contentType
	 * @param file
	 * @return
	 */
	public UploadItem crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		UploadItem item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		return item;

	}

	public DatosEspecificos traerDatosEspecificos(String descDE) {
		Query q = em
				.createQuery("select DatosEspecificos from  DatosEspecificos DatosEspecificos"
						+ " where DatosEspecificos.descripcion = :desc and DatosEspecificos.activo is true ");
		q.setParameter("desc", descDE);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public DatosEspecificos traerDatosEspecificos(String dg, String de) {
		Query q = em
				.createQuery("select DatosEspecificos from  DatosEspecificos DatosEspecificos"
						+ " where datosGenerales.descripcion = :dg and DatosEspecificos.descripcion = :de and DatosEspecificos.activo is true ");
		q.setParameter("de", de);
		q.setParameter("dg", dg);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public DatosEspecificos traerDatosEspecificosValorAlf(String dg,
			String valorAlf) {
		Query q = em
				.createQuery("select DatosEspecificos from  DatosEspecificos DatosEspecificos"
						+ " where datosGenerales.descripcion = :dg and DatosEspecificos.valorAlf = :de and DatosEspecificos.activo is true ");
		q.setParameter("de", valorAlf);
		q.setParameter("dg", dg);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public DatosEspecificos traerDatosEspecificosValorAlfDescripcion(String dg,
			String valorAlf, String descripcion) {
		Query q = em
				.createQuery("select de from  DatosEspecificos de"
						+ " where de.datosGenerales.descripcion = :dg and de.valorAlf = :de and de.descripcion=:descr and de.activo is true ");
		q.setParameter("de", valorAlf);
		q.setParameter("dg", dg);
		q.setParameter("descr", descripcion);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public DatosEspecificos traerDatosEspecificosConDG(String dg) {
		Query q = em
				.createQuery("select de from  DatosEspecificos de"
						+ " where de.datosGenerales.descripcion = :dg and de.activo is true and de.datosGenerales.activo is true ");
		q.setParameter("dg", dg);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public Persona buscarLocal(String documento, String paisExpeDoc) {
		Query q = em
				.createQuery("select Persona from Persona Persona "
						+ " where Persona.documentoIdentidad =:documento and Persona.activo = true and Persona.paisByIdPaisExpedicionDoc.descripcion = :paisExpe");
		q.setParameter("documento", documento);
		q.setParameter("paisExpe", paisExpeDoc);
		List<Persona> lista = q.getResultList();

		for (Persona o : lista) {
			if (o.getActivo()) {
				return o;
			}
		}
		if (lista.size() > 0)
			return lista.get(0);

		return null;
	}

	/**
	 * Caso de uso 535
	 * 
	 * @param documento
	 * @param paisExpeDoc
	 * @return
	 */

	public PersonaDTO buscarPersona(String documento, String paisExpeDoc) {
		PersonaDTO personaRespuesta = new PersonaDTO();
		Persona personaLocal = buscarLocal(documento, paisExpeDoc);
		if (personaLocal != null) {
			if (paisExpeDoc.equalsIgnoreCase("PARAGUAY")) {
				if (personaLocal.getActivo()) {
					personaRespuesta.setEstado(WS_ESTADO_EXISTE);
					personaRespuesta.setActivo(true);
					personaRespuesta.setPersona(personaLocal);
					personaRespuesta.setMensaje(null);
					personaRespuesta.setInsertarSug(false);
					personaRespuesta.setHabilitarBtn(false);
				} else {
					personaRespuesta.setEstado(WS_ESTADO_EXISTE);
					personaRespuesta.setActivo(false);
					personaRespuesta.setPersona(personaLocal);
					personaRespuesta
							.setMensaje("La persona se encuentra inactiva. Motivo: "
									+ personaLocal.getDatosEspecificosMotivo()
											.getDescripcion()
									+ " . Consulte con la SFP");
					personaRespuesta.setInsertarSug(false);
					personaRespuesta.setHabilitarBtn(null);
				}
			} else {
				if (personaLocal.getActivo()) {
					personaRespuesta.setEstado(WS_ESTADO_EXISTE);
					personaRespuesta.setActivo(true);
					personaRespuesta.setPersona(personaLocal);
					personaRespuesta.setMensaje(null);
					personaRespuesta.setInsertarSug(false);
					personaRespuesta.setHabilitarBtn(false);
				} else {
					personaRespuesta.setEstado(WS_ESTADO_EXISTE);
					personaRespuesta.setActivo(false);
					personaRespuesta.setPersona(personaLocal);
					personaRespuesta
							.setMensaje("La persona se encuentra inactiva. Motivo: "
									+ personaLocal.getDatosEspecificosMotivo()
											.getDescripcion()
									+ ". Debe presionar el botón Agregar Persona para insertarlo ");
					personaRespuesta.setInsertarSug(false);
					personaRespuesta.setHabilitarBtn(true);
				}
			}
		} else {
			if (paisExpeDoc.equalsIgnoreCase("PARAGUAY")) {
				// Buscar_web-service(Documento_ID)
				// Calculando los parametros de entrada
				String proxyIP = traerDatosWS("PROXY_IP");
				;
				String proxyPort = traerDatosWS("PROXY_PORT");
				String connectTimeOutStr = traerDatosWS("CONNECT_TIMEOUT");
				String readTimeOutStr = traerDatosWS("READ_TIMEOUT");

				Integer connectTimeOut = (connectTimeOutStr != null) ? new Integer(
						connectTimeOutStr) * 60000 : null;
				Integer readTimeOut = (readTimeOutStr != null) ? new Integer(
						readTimeOutStr) * 60000 : null;

				try {
					WSSFPPersona wSSFPPersona = new WSSFPPersona(proxyIP,
							proxyPort, connectTimeOut, readTimeOut);
					wSSFPPersona.getCtxt().put(StubExt.PROPERTY_CLIENT_TIMEOUT,
							readTimeOut);

					RespuestaObtenerDatosPersona wsAnsw = wSSFPPersona
							.obtenerDatosPersona(documento);
					if (wsAnsw.getEstado() == 98) {
						personaRespuesta.setEstado(WS_ESTADO_SERVIDOR_OCUPADO);
						personaRespuesta.setActivo(null);
						personaRespuesta.setPersona(null);
						personaRespuesta
								.setMensaje("El servidor no se encuentra disponible, inténtelo en algunos instantes.");
						personaRespuesta.setInsertarSug(false);
						personaRespuesta.setHabilitarBtn(null);
					} else if (wsAnsw.getEstado() == 0) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						personaRespuesta.setEstado(WS_ESTADO_NUEVO);
						personaRespuesta.setActivo(null);
						Persona persona = new Persona();
						persona.setDocumentoIdentidad(wsAnsw.getPersona()
								.getNroDocumento());
						persona.setDatosEspecificos(crearNacion(wsAnsw
								.getPersona().getNacionalidad()));
						persona.setPaisByIdPaisExpedicionDoc(crearPais(paisExpeDoc));
						persona.setNombres(wsAnsw.getPersona().getNombre());
						persona.setApellidos(wsAnsw.getPersona().getApellido());
						persona.setSexo(wsAnsw.getPersona().getSexo());
						if (wsAnsw.getPersona().getFechaNacimiento() != null
								&& !wsAnsw.getPersona().getFechaNacimiento()
										.isEmpty()) {
							try {
								persona.setFechaNacimiento(sdf.parse(wsAnsw
										.getPersona().getFechaNacimiento()));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						personaRespuesta.setPersona(persona);
						personaRespuesta
								.setMensaje("No existe la persona, debe presionar el botón Agregar Persona para insertarlo");
						personaRespuesta.setInsertarSug(true);
						personaRespuesta.setHabilitarBtn(true);
					} else {
						personaRespuesta.setEstado(WS_ESTADO_NO_EXISTE);
						personaRespuesta.setActivo(null);
						personaRespuesta.setPersona(null);
						personaRespuesta
								.setMensaje("El Doc. Identidad especificado no existe en la BD Identificaciones. Verifique o consulte con la SFP");
						personaRespuesta.setInsertarSug(false);
						personaRespuesta.setHabilitarBtn(null);

					}

				} catch (Exception e) {
					personaRespuesta.setEstado(WS_ESTADO_NO_EXISTE);
					personaRespuesta.setActivo(null);
					personaRespuesta.setPersona(null);
					personaRespuesta
							.setMensaje("No hay conexión con el Webservice Server");
					personaRespuesta.setInsertarSug(false);
					personaRespuesta.setHabilitarBtn(null);
				}

			} else {

				personaRespuesta.setEstado(WS_ESTADO_NO_EXISTE);
				personaRespuesta.setActivo(null);
				personaRespuesta.setPersona(new Persona());
				personaRespuesta
						.setMensaje("No existe el  Documento Identidad ingresado. Debe presionar el botón Agregar Persona para insertarlo");
				personaRespuesta.setInsertarSug(false);
				personaRespuesta.setHabilitarBtn(true);

			}
		}
		return personaRespuesta;
	}

	private String traerDatosWS(String desc) {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.activo is true and Referencias.dominio = 'WEBSERVICE' and descAbrev = :desc ");
		q.setParameter("desc", desc);
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			Referencias ref = lista.get(0);
			if (desc.equalsIgnoreCase("CONNECT_TIMEOUT")
					|| desc.equalsIgnoreCase("READ_TIMEOUT")) {
				return ref.getValorNum().toString();
			}
			return ref.getDescLarga();
		}
		return null;
	}

	public Referencias traerReferenciasPorDominio(String dominio) {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.activo is true and Referencias.dominio = :dominio ");
		q.setParameter("dominio", dominio);
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public void declararDesierto(ConcursoPuestoAgr grupo) {

		grupo.setEstado(getIdEstadosReferencia("ESTADOS_GRUPO",
				"GRUPO DESIERTO"));
		grupo.setUsuMod("SYSTEM");
		grupo.setActivo(true); // para que aparezca en el historial
		grupo.setFechaMod(new Date());
		grupo = em.merge(grupo);
		EstadoCab estadoCab = buscarEstadoCab("VACANTE");
		EstadoDet estadoDet = buscarEstadoDet("CONCURSO", "DESIERTO");
		List<ConcursoPuestoDet> lconcursoPuestoDet = getConcursoPuestoDet(grupo
				.getIdConcursoPuestoAgr());
		for (ConcursoPuestoDet cpd : lconcursoPuestoDet) {
			cpd.getPlantaCargoDet().setEstadoCab(estadoCab);
			cpd.getPlantaCargoDet().setActivo(false);
			cpd.getPlantaCargoDet().setUsuMod(
					usuarioLogueado.getCodigoUsuario());
			cpd.getPlantaCargoDet().setFechaMod(new Date());
			cpd.setPlantaCargoDet(em.merge(cpd.getPlantaCargoDet()));
			cpd.setEstadoDet(estadoDet);
			cpd.setActivo(false);
			cpd.setFechaMod(new Date());
			cpd.setUsuMod("SYSTEM");
			cpd = em.merge(cpd);
		}

		enviarMailIntegrantes(grupo);
		insertarPublicacionPortal(grupo.getIdConcursoPuestoAgr(), grupo
				.getConcurso().getIdConcurso(), genTextoPublicacion());
		em.flush();
	}

	private String genTextoPublicacion() {
		StringBuffer texto = new StringBuffer();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdf.format(new Date()).toString();
		texto.append(hr
				+ fechaPublicacion
				+ h1O
				+ spanO
				+ "Se declara desierto el Proceso por no reunir la cantidad suficiente de Postulantes, en fecha "
				+ sdfFecha.format(new Date()) + " Hs." + spanC + h1C);
		return texto.toString();
	}

	private void insertarPublicacionPortal(Long idConcursoPuestoAgr,
			Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);

	}

	public void enviarMailIntegrantes(ConcursoPuestoAgr grupo) {
		List<Persona> lComision = getComisionSelecion(grupo
				.getIdConcursoPuestoAgr());
		for (Persona o : lComision) {
			enviarMailComision(grupo, o);
		}
	}

	private String enviarMailComision(ConcursoPuestoAgr grupo, Persona persona) {
		if (persona.getEMail() != null && General.isEmail(persona.getEMail())) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);

			VelocityContext context = new VelocityContext();
			context.put("descripcion_grupo", grupo.getDescripcionGrupo());
			context.put("nombreConcurso", nombreConcuro(grupo.getConcurso()));
			context.put("fecha", sdf.format(new Date()));
			Template t = ve
					.getTemplate("/templates/emailContestDesierto_508.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			enviarMails(persona.getEMail(), writer.toString(),
					":  NOTIF_GRUPO_DESIERTO_"
							+ grupo.getConcurso().getNombre(), null);
		}
		return "";
	}

	public Boolean checkDesierto(ConcursoPuestoAgr grupo) {

		Integer postusActivos = countCantPostulantesActivos(grupo
				.getIdConcursoPuestoAgr());
		if (postusActivos.intValue() == 0) {
			statusMessages
					.add(Severity.ERROR,
							"Este grupo se declara desierto por no contar con Postulantes suficientes");
			declararDesierto(grupo);
			return true;

		} else
			return false;

	}

	private Pais crearPais(String desc) {
		if (desc == null) {
			return null;
		}
		desc = desc.toUpperCase();
		Query q = em
				.createQuery("select Pais from Pais Pais where Pais.descripcion = :descPais ");
		q.setParameter("descPais", desc);
		List<Pais> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		Pais pais = new Pais();
		pais.setActivo(true);
		pais.setDescripcion(desc);
		pais.setFechaAlta(new Date());
		pais.setUsuAlta("SYSTEM");
		em.persist(pais);
		return pais;
	}

	private DatosEspecificos crearNacion(String desc) {
		if (desc == null)
			return null;
		desc = desc.toUpperCase();
		
		if (desc.equals("PARAGUAY") || desc.equals("PARAGUAYO") || desc.equals("PARAGUAYA")) {
			desc = "PARAGUAYA";
		}
		
		Query q = em
				.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
						+ " where DatosEspecificos.datosGenerales.descripcion = 'NACIONALIDAD' "
						+ " and DatosEspecificos.descripcion = :descNacion and DatosEspecificos.activo = true");
		q.setParameter("descNacion", desc);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		// Datos generales
		q = em.createQuery("select DatosGenerales from DatosGenerales DatosGenerales "
				+ " where DatosGenerales.descripcion = 'NACIONALIDAD'");
		DatosGenerales dg = (DatosGenerales) q.getSingleResult();
		DatosEspecificos nacion = new DatosEspecificos();
		nacion.setActivo(true);
		nacion.setDatosGenerales(dg);
		nacion.setDescripcion(desc);
		nacion.setFechaAlta(new Date());
		nacion.setUsuAlta("SYSTEM");
		em.persist(nacion);
		em.flush();
		return nacion;
	}

	public Integer getIdEstadosReferencia(String dominio, String descAbrev) {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ "  where Referencias.activo is true and Referencias.dominio ='"
						+ dominio + "' and Referencias.descAbrev ='"
						+ descAbrev + "'");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum().intValue();
	}

	public DatosEspecificos traerTipoConcursoConcurso(String descripcion) {
		// CONCURSO INTERNO DE OPOSICION
		Query q = em
				.createQuery("Select DatosEspecificos from DatosEspecificos DatosEspecificos "
						+ " where DatosEspecificos.descripcion='"
						+ descripcion
						+ "'");
		// List<DatosEspecificos> lista = q.getResultList();
		List<DatosEspecificos> lista = ((List<DatosEspecificos>) q
				.getResultList());// MODIFICADO RV
		if (lista.size() > 0)
			return lista.get(0);
		else
			return null;
	}

	public List<Persona> getComisionSelecion(Long idGrupo) {
		Query q = em
				.createQuery("select ComisionGrupo from ComisionGrupo ComisionGrupo where ComisionGrupo.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo);
		ComisionGrupo comisionGrupo = (ComisionGrupo) q.getSingleResult();
		q = em.createQuery("select ComisionSeleccionDet from ComisionSeleccionDet ComisionSeleccionDet where ComisionSeleccionDet.comisionSeleccionCab.idComisionSel = "
				+ comisionGrupo.getComisionSeleccionCab().getIdComisionSel()
				+ " and ComisionSeleccionDet.persona.activo is true "
				+ " order by persona.nombres asc,persona.apellidos asc");
		List<ComisionSeleccionDet> lista = q.getResultList();
		List<Persona> lPersona = new ArrayList<Persona>();
		for (ComisionSeleccionDet o : lista) {
			lPersona.add(o.getPersona());
		}
		return lPersona;
	}

	/**
	 * Trae todos los puestos de un grupo
	 */
	public List<PlantaCargoDet> getPlantaCargoDet(Long idGrupo) {
		Query q = em
				.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
						+ " where ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo
						+ " and ConcursoPuestoDet.activo is true and ConcursoPuestoDet.concursoPuestoAgr.activo is true");
		List<ConcursoPuestoDet> lista = q.getResultList();
		List<PlantaCargoDet> listaR = new ArrayList<PlantaCargoDet>();
		for (ConcursoPuestoDet o : lista) {
			listaR.add(o.getPlantaCargoDet());
		}
		return listaR;
	}

	public List<ConcursoPuestoDet> getConcursoPuestoDet(Long idGrupo) {
		Query q = em
				.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet "
						+ " where ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ idGrupo
						+ " and ConcursoPuestoDet.activo is true and ConcursoPuestoDet.concursoPuestoAgr.activo is true ");
		List<ConcursoPuestoDet> lista = q.getResultList();

		return lista;
	}

	/**
	 * Busca un determinado estadoDet
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public EstadoDet buscarEstadoDet(String estadoCab, String estadoDet) {
		try {
			String cad = "select det.* from planificacion.estado_cab cab "
					+ "join planificacion.estado_det det  "
					+ "on det.id_estado_cab = cab.id_estado_cab "
					+ "where lower(cab.descripcion)='"
					+ estadoCab.toLowerCase() + "' "
					+ "and lower(det.descripcion)='" + estadoDet.toLowerCase()
					+ "'";
			List<EstadoDet> lista = new ArrayList<EstadoDet>();
			lista = em.createNativeQuery(cad, EstadoDet.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String nombreConcuro(Concurso concurso) {
		if (concurso != null) {
			concurso = em.find(Concurso.class, concurso.getIdConcurso());
			return concurso.getNumero() + "/" + concurso.getAnhio() + " de "
					+ concurso.getConfiguracionUoCab().getDescripcionCorta()
					+ " " + concurso.getNombre();
		}
		return "";
	}

	/**
	 * Busca un determinado estadoCab
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public EstadoCab buscarEstadoCab(String estadoCab) {
		try {
			String cad = "select cab.* from planificacion.estado_cab cab "
					+ "where lower(cab.descripcion) = '"
					+ estadoCab.toLowerCase() + "' ";

			List<EstadoCab> lista = new ArrayList<EstadoCab>();
			lista = em.createNativeQuery(cad, EstadoCab.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Devuelve el estado del grupo
	 * 
	 * @param idConcursoPuestoAgr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getEstadoGrupo(Long idConcursoPuestoAgr) {
		try {
			String cad = "SELECT REF.* " + "FROM SELECCION.REFERENCIAS REF "
					+ "JOIN SELECCION.CONCURSO_PUESTO_AGR GR "
					+ "ON GR.ESTADO = REF.VALOR_NUM "
					+ "WHERE REF.DOMINIO = 'ESTADOS_GRUPO' "
					+ "AND GR.ID_CONCURSO_PUESTO_AGR = " + idConcursoPuestoAgr;

			List<Referencias> lista = new ArrayList<Referencias>();
			lista = em.createNativeQuery(cad, Referencias.class)
					.getResultList();
			if (lista.size() > 0) {
				Referencias referencias = lista.get(0);
				return referencias.getDescLarga();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtiene una observacion a partir del id de la tarea
	 * 
	 * @param idTaskInstance
	 *            : id de la terea a recuperar
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Observacion getObservacion(Long idTaskInstance) {
		try {
			String cad = "SELECT * FROM seleccion.observacion where id_task_instance = "
					+ idTaskInstance;

			List<Observacion> lista = new ArrayList<Observacion>();
			lista = em.createNativeQuery(cad, Observacion.class)
					.getResultList();
			if (lista.size() > 0) {
				Observacion o = lista.get(0);
				return o;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtiene una HomologacionPerfilMatriz a partir del id de un grupo
	 * 
	 * @param idConcursoPuestoAgr
	 *            : id del grupo al cual corresponde la HomologacionPerfilMatriz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HomologacionPerfilMatriz getHomologacionPerfilMatriz(
			Long idConcursoPuestoAgr) {
		try {
			String cad = "SELECT * FROM seleccion.homologacion_perfil_matriz where id_concurso_puesto_agr = "
					+ idConcursoPuestoAgr;

			List<HomologacionPerfilMatriz> lista = new ArrayList<HomologacionPerfilMatriz>();
			lista = em.createNativeQuery(cad, HomologacionPerfilMatriz.class)
					.getResultList();
			if (lista.size() > 0) {
				HomologacionPerfilMatriz o = lista.get(0);
				return o;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtiene una FechasGrupoPuesto a partir del id de un grupo
	 * 
	 * @param idConcursoPuestoAgr
	 *            : id del grupo al cual corresponde la FechasGrupoPuesto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public FechasGrupoPuesto getFechasGrupoPuesto(Long idConcursoPuestoAgr) {
		try {
			String cad = "SELECT * FROM seleccion.fechas_grupo_puesto "
					+ "where activo is true and id_concurso_puesto_agr = "
					+ idConcursoPuestoAgr;

			List<FechasGrupoPuesto> lista = new ArrayList<FechasGrupoPuesto>();
			lista = em.createNativeQuery(cad, FechasGrupoPuesto.class)
					.getResultList();
			if (lista.size() > 0) {
				FechasGrupoPuesto o = lista.get(0);
				return o;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtiene una EvalReferencialTipoeval a partir del id de un grupo
	 * 
	 * @param idConcursoPuestoAgr
	 *            : id del grupo al cual corresponde la EvalReferencialTipoeval
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public EvalReferencialTipoeval getEvalReferencialTipoeval(
			Long idConcursoPuestoAgr) {
		try {
			String cad = "SELECT * FROM seleccion.eval_referencial_tipoeval "
					+ "where activo is true and id_concurso_puesto_agr = "
					+ idConcursoPuestoAgr;

			List<EvalReferencialTipoeval> lista = new ArrayList<EvalReferencialTipoeval>();
			lista = em.createNativeQuery(cad, EvalReferencialTipoeval.class)
					.getResultList();
			if (lista.size() > 0) {
				EvalReferencialTipoeval o = lista.get(0);
				return o;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtiene una actividad_proceso
	 * 
	 * @param actividad
	 * @param proceso
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActividadProceso buscarActividadProceso(String actividad,
			String proceso) {
		try {
			String cad = " select ap.* "
					+ " from proceso.actividad_proceso ap " +

					" join proceso.actividad a "
					+ " on a.id_actividad = ap.id_actividad " +

					" join proceso.proceso p "
					+ " on p.id_proceso = ap.id_proceso " +

					" where upper(a.cod_actividad) = upper('"
					+ actividad.toUpperCase() + "') "
					+ "      and upper(p.descripcion) = upper('"
					+ proceso.toUpperCase() + "')";

			List<ActividadProceso> lista = em.createNativeQuery(cad,
					ActividadProceso.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Asigna los roles necesarios a la primera tarea
	 */
	public String getRolesTarea(String actividad, String proceso) {
		ActividadProceso actividadProceso = buscarActividadProceso(actividad,
				proceso);
		// actividadProceso = em.find(ActividadProceso.class,
		// actividadProceso.getIdActividadProceso());
		String roles = "";
		try {
			if (actividadProceso.getProcActividadRols() != null) {
				for (ProcActividadRol procActividadRol : actividadProceso
						.getProcActividadRols()) {
					Rol rol = procActividadRol.getRol();
					roles += "," + rol.getId();
				}
				roles = roles.replaceFirst(",", "");
			}
		} catch (Exception e) {
			return null;
		}
		return roles;
	}

	/**
	 * Busca los grupos de un concurso
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConcursoPuestoAgr> buscarGruposConcurso(Long idConcurso,
			String nombreActividad) {
		try {
			String cad = " " + " select distinct grupo.* "
					+ " from seleccion.concurso_puesto_agr grupo " +

					" join seleccion.concurso c "
					+ " on(c.id_concurso = grupo.id_concurso) " +

					" join jbpm.jbpm_processinstance p "
					+ " on(grupo.id_process_instance = p.id_) " +

					" join jbpm.jbpm_taskinstance t "
					+ " on(t.procinst_ = p.id_) " +

					" where c.id_concurso = " + idConcurso
					+ " and grupo.activo = true " + " and c.activo = true "
					+ " and t.name_ = '" + nombreActividad + "'"
					+ " and t.end_ is null";

			Query q = em.createNativeQuery(cad, ConcursoPuestoAgr.class);
			List<ConcursoPuestoAgr> lista = q.getResultList();
			return lista;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Observacion cargarObservacion(long idTaskInstance) {
		try {
			String cad = " select o.* " + " from seleccion.observacion o "
					+ " where id_task_instance = " + idTaskInstance + " ";

			List<Observacion> lista = em.createNativeQuery(cad,
					Observacion.class).getResultList();
			if (lista != null && lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	/**
	 * Indica si una OEE trabaja o no en una fecha teniendo en cuenta su
	 * calendario y los feriados.
	 * 
	 * @param fecha
	 *            : fecha en que se desea saber si trabaja o no la OEE.
	 * @param idConfiguracionUo
	 *            : id de la OEE en cuestion.
	 * @return
	 */
	public boolean fechaTrabajaOee(Date fecha, Long idConfiguracionUo) {
		String consulta = " select proceso.fecha_trabaja_oee (:fecha, :idConfiguracionUo) ";
		Query q = em.createNativeQuery(consulta);
		q.setParameter("fecha", fecha);
		q.setParameter("idConfiguracionUo", idConfiguracionUo);

		List lista = q.getResultList();
		if (lista != null && lista.size() > 0)
			return (Boolean) lista.get(0);

		return false;
	}

	@SuppressWarnings("unchecked")
	public Boolean enviarMails(String destinatario, String contenido,
			String asunto) {

		Properties props = new Properties();
		List<Referencias> ref = em
				.createQuery(
						" select r from Referencias r"
								+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true")
				.getResultList();
		if (ref.isEmpty()) {
			return false;
		}

		String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
		int i = 0;
		for (Referencias r : ref) {
			if (r.getDescAbrev().equals("HOST"))
				host = r.getDescLarga();

			if (r.getDescAbrev().equals("USER"))
				user = r.getDescLarga();

			if (r.getDescAbrev().equals("PORT"))
				port = r.getDescLarga();

			if (r.getDescAbrev().equals("PASS"))
				pass = r.getDescLarga();

			if (r.getDescAbrev().equals("PROTOCOLO"))
				protocol = r.getDescLarga();

			if (r.getDescAbrev().equals("USUARIO"))
				usuarioSend = r.getDescLarga();

		}

		/*
		 * Ejemplo props.setProperty("mail.transport.protocol", "smtp");
		 * props.setProperty("mail.host", "mail.copaco.com.py");
		 * props.setProperty("mail.user", "menfran@click.com.py");
		 * props.setProperty("mail.password", "12345678");
		 */

		props.setProperty("mail.transport.protocol", protocol);
		props.setProperty("mail.host", host);
		props.setProperty("mail.user", user);
		 props.setProperty("mail.smtp.auth", "true");
			//javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(props, null);
			javax.mail.Transport transport;
			
			//Autenticador
			Authenticator aut = new Authenticator() {
				
	            
	            protected PasswordAuthentication getPasswordAuthentication(){
	            	List<Referencias> ref = em.createQuery(" select r from Referencias r"
							+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();

					if (ref.isEmpty()) {
						return null;
					}
					
					String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
					int i = 0;
					for (Referencias r : ref) {
						if (r.getDescAbrev().equals("HOST"))
							host = r.getDescLarga();

						if (r.getDescAbrev().equals("USER"))
							user = r.getDescLarga();

						if (r.getDescAbrev().equals("PORT"))
							port = r.getDescLarga();

						if (r.getDescAbrev().equals("PASS"))
							pass = r.getDescLarga();

						if (r.getDescAbrev().equals("PROTOCOLO"))
							protocol = r.getDescLarga();

						if (r.getDescAbrev().equals("USUARIO"))
							usuarioSend = r.getDescLarga();

					}
					         	
	                PasswordAuthentication passwordAuthentication=new PasswordAuthentication(user,pass);
	                return passwordAuthentication;
	              }};
			
			javax.mail.Session mailSession = javax.mail.Session.getInstance(props, aut);
			
			
		try {
			transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);

			message.setSubject(asunto);
			message.setContent(contenido, "text/html");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					destinatario));
			message.setFrom(new InternetAddress(user, usuarioSend));
			message.setSentDate(new Date());
			message.setHeader("-", "-");
			transport.connect();
			transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
			transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public Boolean enviarMails(String destinatario, String contenido,
			String asunto, List<String> adjuntos) {
		Properties props = new Properties();
		List<Referencias> ref = em.createQuery(" select r from Referencias r"
								+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();
		
		if (ref.isEmpty()) {
			return false;
		}

		String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
		int i = 0;
		for (Referencias r : ref) {
			if (r.getDescAbrev().equals("HOST"))
				host = r.getDescLarga();

			if (r.getDescAbrev().equals("USER"))
				user = r.getDescLarga();

			if (r.getDescAbrev().equals("PORT"))
				port = r.getDescLarga();

			if (r.getDescAbrev().equals("PASS"))
				pass = r.getDescLarga();

			if (r.getDescAbrev().equals("PROTOCOLO"))
				protocol = r.getDescLarga();

			if (r.getDescAbrev().equals("USUARIO"))
				usuarioSend = r.getDescLarga();

		}

		/*
		 * Ejemplo props.setProperty("mail.transport.protocol", "smtp");
		 * props.setProperty("mail.host", "mail.copaco.com.py");
		 * props.setProperty("mail.user", "xxxx@click.com.py");
		 * props.setProperty("mail.password", "12345678");
		 */
		props.setProperty("mail.transport.protocol", protocol);
		props.setProperty("mail.host", host);
		props.setProperty("mail.user", user);
		props.setProperty("mail.password", pass);
	    props.setProperty("mail.smtp.auth", "true");
		//javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(props, null);
		javax.mail.Transport transport;
		
		//Autenticador
		Authenticator aut = new Authenticator() {
			
            
            protected PasswordAuthentication getPasswordAuthentication(){
            	List<Referencias> ref = em.createQuery(" select r from Referencias r"
						+ " where r.dominio like 'CONFIG_EMAIL' and r.activo= true").getResultList();

				if (ref.isEmpty()) {
					return null;
				}
				
				String protocol = "", host = "", user = "", pass = "", port = "", usuarioSend = "";
				int i = 0;
				for (Referencias r : ref) {
					if (r.getDescAbrev().equals("HOST"))
						host = r.getDescLarga();

					if (r.getDescAbrev().equals("USER"))
						user = r.getDescLarga();

					if (r.getDescAbrev().equals("PORT"))
						port = r.getDescLarga();

					if (r.getDescAbrev().equals("PASS"))
						pass = r.getDescLarga();

					if (r.getDescAbrev().equals("PROTOCOLO"))
						protocol = r.getDescLarga();

					if (r.getDescAbrev().equals("USUARIO"))
						usuarioSend = r.getDescLarga();

				}
				         	
                PasswordAuthentication passwordAuthentication=new PasswordAuthentication(user,pass);
                return passwordAuthentication;
              }};
		
		javax.mail.Session mailSession = javax.mail.Session.getInstance(props, aut);
		
		
		
		try {
			transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);
			// Se seteo el mensaje del e-mail
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(contenido, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// Se adjuntan los archivos al correo
			if (adjuntos != null && adjuntos.size() > 0) {
				for (String rutaAdjunto : adjuntos) {
					messageBodyPart = new MimeBodyPart();
					File f = new File(rutaAdjunto);
					if (f.exists()) {
						DataSource source = new FileDataSource(rutaAdjunto);
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(f.getName());
						multipart.addBodyPart(messageBodyPart);
					}
				}
			}
			// Se junta el mensaje y los archivos adjuntos
			message.setContent(multipart);
			// Siguientes partes del mail
			message.setSubject(asunto);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					destinatario));
			message.setFrom(new InternetAddress(user, usuarioSend));
			message.setSentDate(new Date());
			message.setHeader("-", "-");
			transport.connect();
			transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
			transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Busca los grupos de un concurso por estado
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConcursoPuestoAgr> buscarGruposConcursoPorEstado(
			Long idConcurso, String estado) {
		try {
			String cad = " " + " select distinct grupo.* "
					+ " from seleccion.concurso_puesto_agr grupo  " +

					" join seleccion.concurso c "
					+ " on(c.id_concurso = grupo.id_concurso) " +

					" where c.id_concurso = " + idConcurso
					+ " and grupo.estado in  " + " ( "
					+ " 	SELECT ref.valor_num "
					+ " 	FROM seleccion.referencias ref "
					+ " 	WHERE dominio = 'ESTADOS_GRUPO' "
					+ " 	AND desc_abrev = '" + estado + "' " + " ) ";

			Query q = em.createNativeQuery(cad, ConcursoPuestoAgr.class);
			List<ConcursoPuestoAgr> lista = q.getResultList();
			return lista;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Verificar si el Grupo de Puestos tiene cargado Lugares de Presentación y
	 * Aclaración, campo ID_CONCURSO_PUESTO_AGR en la tabla
	 * SELECCION.PRESENT_ACLARAC_DOC. Caso contrario, buscar por el ID_CONCURSO
	 * y filtrar los email’s: LOS EMAILS RESULTANTES DEBERAN MOSTRARSE
	 * CONCATENADOS ENTRE COMAS.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getMailsLugaresPresentacionAclaracion(
			ConcursoPuestoAgr concursoPuestoAgr) {
		String mails = "";

		String consulta = ""
				+ " select presentAclaracDoc "
				+ " from PresentAclaracDoc presentAclaracDoc "
				+ " where presentAclaracDoc.concursoPuestoAgr.idConcursoPuestoAgr = :idConcursoPuestoAgr";

		Query q = em.createQuery(consulta);
		q.setParameter("idConcursoPuestoAgr",
				concursoPuestoAgr.getIdConcursoPuestoAgr());

		List<PresentAclaracDoc> lista = q.getResultList();
		if (lista != null && lista.size() > 0) {
			for (PresentAclaracDoc presentAclaracDoc : lista) {
				mails += ", " + presentAclaracDoc.getEmail();
			}
			mails = mails.replaceFirst(",", "");
			return mails;
		}

		// Si no es por grupo se verifica que existan por concurso
		consulta = "" + " select presentAclaracDoc "
				+ " from PresentAclaracDoc presentAclaracDoc "
				+ " where presentAclaracDoc.concurso.idConcurso = :idConcurso";

		q = em.createQuery(consulta);
		q.setParameter("idConcurso", concursoPuestoAgr.getConcurso()
				.getIdConcurso());

		lista = q.getResultList();
		if (lista != null && lista.size() > 0) {
			for (PresentAclaracDoc presentAclaracDoc : lista) {
				mails += ", " + presentAclaracDoc.getEmail();
			}
			mails = mails.replaceFirst(",", "");
			return mails;
		}

		return null;
	}

	/**
	 * Verifica si existe un algun registro o no en el query que se le pasa como
	 * parametro
	 * 
	 * @param ejbql
	 *            : consulta a ser ejecutada
	 * @return true si existe algun registro, false en otro caso.
	 */
	@SuppressWarnings("unchecked")
	public boolean existe(String ejbql) {
		try {
			List<Object> lista = new ArrayList<Object>();
			lista = em.createQuery(ejbql).getResultList();

			if (lista != null && lista.size() > 0)
				return true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Verifica si existe un algun registro o no en el query que se le pasa como
	 * parametro
	 * 
	 * @param nativeQuery
	 *            : consulta a ser ejecutada
	 * @return true si existe algun registro, false en otro caso.
	 */
	@SuppressWarnings("unchecked")
	public boolean existeNative(String nativeQuery) {
		try {
			List<Object> lista = new ArrayList<Object>();
			lista = em.createNativeQuery(nativeQuery).getResultList();

			if (lista != null && lista.size() > 0)
				return true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * @param horaCad
	 * @return
	 */
	public int[] getHora(String horaCad) {
		String[] horas = horaCad.split(":");
		if (horas.length != 2) {
			return null;
		} else {
			String hora = horas[0];
			String minuto = horas[1];
			try {
				int hh = Integer.parseInt(hora);
				int mm = Integer.parseInt(minuto);

				if (hh < 0 || hh > 23 || mm < 0 || mm >= 60) {
					return null;
				}
				int[] v = new int[2];
				v[0] = hh;
				v[1] = mm;
				return v;
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * @return Cod del Usuario de la persona pasada como parametro donde el Usu
	 *         alta sea PORTAL
	 */
	@SuppressWarnings("unchecked")
	public String getUsuarioPostulante(Long idPersona) {
		List<Usuario> usuPost = em
				.createQuery(
						"Select u from Usuario u"
								+ " where u.usuAlta='PORTAL' and u.persona.idPersona=:idPersona")
				.setParameter("idPersona", idPersona).getResultList();
		return usuPost.get(0).getCodigoUsuario();
	}

	public Boolean existePersonaPostulante(Long idAgr, Long idPersona) {
		Query q = em
				.createQuery("select Postulacion from Postulacion Postulacion "
						+ " where Postulacion.activo is true  and  Postulacion.usuCancelacion is null and Postulacion.fechaCancelacion is null "
						+ " and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ " and Postulacion.persona.idPersona = :idPersona  ");
		q.setParameter("idGrupo", idAgr);
		q.setParameter("idPersona", idPersona);

		Postulacion postulacion = new Postulacion();
		List<Postulacion> lista = q.getResultList();
		if (lista.size() > 0)
			postulacion = lista.get(0);
		if (postulacion == null || postulacion.getPersonaPostulante() == null)
			return false;

		return true;

	}

	/**
	 * @return Postulacion donde La postulación tenga campo Activo = true,
	 *         usu_cancelacion y fecha_cancelacion deben ser nulos
	 */
	public Postulacion getPostulacion(Long idGrupo, Long idPersona) {
		Postulacion p = new Postulacion();
		try {
			p = (Postulacion) em
					.createQuery(
							"select postulacion from Postulacion postulacion "
									+ " where postulacion.activo is true and postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
									+ " and postulacion.persona.idPersona = :idPersona and  postulacion.usuCancelacion is null and postulacion.fechaCancelacion is null ")
					.setParameter("idGrupo", idGrupo)
					.setParameter("idPersona", idPersona).getSingleResult();
		} catch (NoResultException e) {
		}
		return p;
	}

	/**
	 * Carga el cuerpo para el mail recibe el template y el contenido
	 */
	public String cargarCuerpoMail(String tmp, VelocityContext context) {
		VelocityEngine ve = new VelocityEngine();
		java.util.Properties p = new java.util.Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);

		Template t = ve.getTemplate(tmp);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		return writer.toString();
	}

	/**
	 * Método que obtiene el codigo del puesto
	 */
	public String obtenerCodigoPuesto(PlantaCargoDet det) {
		if (det != null) {
			ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
			uoDet = det.getConfiguracionUoDet();
			nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
					.getIdConfiguracionUo());
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
					.getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.init2();
			String code = nivelEntidadOeeUtil.getCodigoUnidOrgDep() + ".";
			if (det.getContratado())
				code = code + "C";
			if (det.getPermanente())
				code = code + "P";
			code = code + det.getOrden();
			return code;
		}
		return "";
	}

	private void limpiarDatos() {
		codigoCategoria = null;
		monto = null;
		categoria = null;
		sinAnx = null;
	}

	/**
	 * permite buscar solos los objetos !=111
	 * */
	public void findObjContratados() {
		long codPermanente = 111;
		int codMin = 140;
		int codMax = 149;
		if (codigoObj != null) {
			if (codigoObj.longValue() == codPermanente) {
				statusMessages.add(Severity.ERROR,
						"No se permite codigo de objeto 111");
				return;
			} else if (codigoObj.intValue() < codMin) {
				statusMessages.add(Severity.ERROR,
						"No se permite codigo de objeto menor a 140");
				return;

			} else if (codigoObj.intValue() > codMax) {
				statusMessages.add(Severity.ERROR,
						"No se permite codigo de objeto mayor a 149");
				return;
			}
			findObj();

		}
	}

	/**
	 * busca el codigo objeto del puesto SELECCIONADO YA DEBE POSEER EL PUESTO
	 * COMO EL CODIGOSINARH
	 */

	public void findObj() {

		if (codigoObj != null) {
			if (plantaCargoDet == null
					|| plantaCargoDet.getIdPlantaCargoDet() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar el puesto antes de realizar la accion");
				return;
			}
			sinObjList.setCod(codigoObj);
			List<SinObj> lista = new ArrayList<SinObj>();
			lista = sinObjList.listaResultados();
			limpiarDatos();
			if (lista.size() > 0)
				valorObj = lista.get(0).getObjNombre();
			else {
				valorObj = null;
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"No existe el Objeto de Gasto en el SINARH");
				return;
			}
		}
	}

	/**
	 * busca el codigo objeto del puesto SELECCIONADO YA DEBE POSEER EL PUESTO
	 * COMO EL CODIGOSINARH
	 */

	public void findObjReasigInsMasiva() {

		if (codigoObj != null) {

			if (plantaCargoDet == null
					|| plantaCargoDet.getIdPlantaCargoDet() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar el funcionario antes de realizar la accion");
				return;
			}

			sinObjList.setCod(codigoObj);
			List<SinObj> lista = new ArrayList<SinObj>();
			lista = sinObjList.listaResultados();
			limpiarDatos();
			if (lista.size() > 0)
				valorObj = lista.get(0).getObjNombre();
			else {
				valorObj = null;
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"No existe el Objeto de Gasto en el SINARH");
				return;
			}
		}
	}

	/**
	 * BUSCA LA CATEGORIA DEL PUESTO SELECCIONADO YA DEBE POSEER EL PUESTO COMO
	 * EL CODIGOSINARH
	 */
	public void findCategoria() {

		if (codigoCategoria != null && !codigoCategoria.trim().equals("")) {
			monto = null;
			sinAnx = null;
			if (codigoObj == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ingrese el Código OBJ.");
				return;
			}
			if (plantaCargoDet == null
					|| plantaCargoDet.getIdPlantaCargoDet() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar el puesto antes de realizar la accion");
				return;
			}

			if (esContratadoOpermanente().equalsIgnoreCase("PERMANENTE")) {

				if (!Utiles.vacio(codigoSinarh)) {
					try {
						List<SinAnx> lista = buscarListaSinAnx(codigoSinarh,
								codigoCategoria);
						if (lista.isEmpty()) {
							statusMessages.clear();
							statusMessages
									.add(Severity.ERROR,
											"No existe La Categoria seleccionada. Verifique ");
							return;
						}

						categoria = lista.get(0).getAnxDescrip();
						setSinAnx(em
								.find(SinAnx.class, lista.get(0).getIdAnx()));
						buscarMonto(lista.get(0).getCatGrupo());

					} catch (Exception e) {
						e.printStackTrace();
						statusMessages.clear();
						statusMessages.add(Severity.ERROR,
								"Código inválido. Verifique.");
						return;
					}
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"Debe asignar el código de sinarh al OEE antes de realizar esta acción. Verifique.");
					return;
				}
			}
			if (esContratadoOpermanente().equalsIgnoreCase("CONTRATADO")) {
				SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();
				sinCategoriaContratados = existeCategoriaContratados();
				if (sinCategoriaContratados == null) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"No existe La Categoria seleccionada. Verifique");
					return;
				}
				categoria = sinCategoriaContratados.getCtgDescrip();
				buscarSinAnx(codigoObj, codigoCategoria);
			}
		}
	}

	/**
	 * Busca el monto para la categoria de grupo actual
	 * 
	 * @param cat_grupo
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void buscarMonto(Integer cat_grupo) {

		if (plantaCargoDet.getContratado()) {
			monto = null;

		} else if (plantaCargoDet.getPermanente()) {
			String valor = "" + cat_grupo;
			String sql2 = "select * from sinarh.sin_categoria cat "
					+ "where cat.ctg_codigo = '" + codigoCategoria + "'"
					+ " and cat_grupo = " + cat_grupo
					+ " and cat.vrs_codigo = '" + valor + "'";
			List<SinCategoria> listaMonto = new ArrayList<SinCategoria>();
			listaMonto = em.createNativeQuery(sql2, SinCategoria.class)
					.getResultList();
			if (listaMonto.size() > 0) {
				Date fechaActual = new Date();
				Integer mes = fechaActual.getMonth();
				if (mes == 0)
					monto = listaMonto.get(0).getCtgValor1().intValue();
				if (mes == 1)
					monto = listaMonto.get(0).getCtgValor2().intValue();
				if (mes == 2)
					monto = listaMonto.get(0).getCtgValor3().intValue();
				if (mes == 3)
					monto = listaMonto.get(0).getCtgValor4().intValue();
				if (mes == 4)
					monto = listaMonto.get(0).getCtgValor5().intValue();
				if (mes == 5)
					monto = listaMonto.get(0).getCtgValor6().intValue();
				if (mes == 6)
					monto = listaMonto.get(0).getCtgValor7().intValue();
				if (mes == 7)
					monto = listaMonto.get(0).getCtgValor8().intValue();
				if (mes == 8)
					monto = listaMonto.get(0).getCtgValor9().intValue();
				if (mes == 9)
					monto = listaMonto.get(0).getCtgValor10().intValue();
				if (mes == 10)
					monto = listaMonto.get(0).getCtgValor11().intValue();
				if (mes == 11)
					monto = listaMonto.get(0).getCtgValor12().intValue();
			}
		}

	}

	/**
	 * Verifica si el puesto es contratado o permanente
	 * 
	 * @return
	 */
	private String esContratadoOpermanente() {

		if (plantaCargoDet.getContratado())
			return "CONTRATADO";
		if (plantaCargoDet.getPermanente())
			return "PERMANENTE";

		return null;
	}

	/**
	 * En caso de que el puesto sea contratado se obtiene la categoria en este
	 * metodo
	 * 
	 * @return
	 */
	private SinCategoriaContratados existeCategoriaContratados() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		BigDecimal an = new BigDecimal(anho.intValue());
		Query q = em
				.createQuery("select sinCat from SinCategoriaContratados sinCat "
						+ " where sinCat.aniAniopre = :anio and sinCat.objCodigo = :cod ");
		q.setParameter("anio", an);
		q.setParameter("cod", new BigDecimal(codigoObj.intValue()));
		List<SinCategoriaContratados> lista = q.getResultList();
		if (lista.isEmpty())
			return null;
		return lista.get(0);
	}

	/**
	 * Obtiene la lista SinAnx correspondiente al codigo sinarh llama a la clase
	 * util
	 * 
	 * @param codigoSinarh
	 * @param codigo
	 * @return
	 */
	private List<SinAnx> buscarListaSinAnx(String codigoSinarh, String codigo) {
		Integer vrs_codigo = new Integer(50);
		List<SinAnx> lista = new ArrayList<SinAnx>();
		lista = sinarhUtiles.obtenerListaSinAnx(null, vrs_codigo, codigoObj,
				codigo.toUpperCase(), null);

		return lista;
	}

	public void setearValoresObjetosGasto() {
		codigoObj = null;
		sinAnx = null;
		categoria = null;
		valorObj = null;
		codigoCategoria = null;
		monto = null;

	}

	public void buscarSinAnx(Integer objCodigo, String codCategoria) {
		try {
			sinAnx = (SinAnx) em
					.createQuery(
							"Select anx from SinAnx anx "
									+ " where anx.objCodigo=:oCodigo and anx.ctgCodigo=cCodigo")
					.setParameter("oCodigo", objCodigo)
					.setParameter("cCodigo", codCategoria).getSingleResult();
		} catch (NoResultException e) {
			// TODO: handle exception
		}

	}

	public String genCodigoConcurso(Concurso concurso, ConfiguracionUoCab uo) {
		String numero = concurso.getNumero() == null ? "-" : concurso
				.getNumero().toString();
		String anhio = (concurso.getAnhio() == null ? "-" : concurso.getAnhio()
				.toString());
		String resp = numero + "/" + anhio + " de " + uo.getDescripcionCorta();
		return resp;
	}

	public Long getIdTipoConcurso(String desc) {
		Long idTipo = null;
		try {
			idTipo = (Long) em
					.createQuery(
							"Select d.idDatosEspecificos from DatosEspecificos d "
									+ " where lower(d.descripcion) like :desc")
					.setParameter("desc", desc.toLowerCase()).getSingleResult();
		} catch (Exception e) {
		}
		return idTipo;
	}

	// Select que verifica la cantidad de Adjudicados: por grupo
	@SuppressWarnings("unchecked")
	public int cntAdjudicados(Long idGrupo) {
		List<EvalReferencialPostulante> evalReferencialPostulantes = em
				.createQuery(
						"Select e from EvalReferencialPostulante e "
								+ " where e.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo  and e.postulacion.activo=true "
								+ " and e.activo=true ")
				.setParameter("idGrupo", idGrupo).getResultList();
		return evalReferencialPostulantes.size();
	}

	/** cantidad de elegible de la tabla ListaElegiblesDet **/
	@SuppressWarnings("unchecked")
	public int cntElegible(Long idConcursoPuestoAgr) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		String fecActual = sdf.format(new Date());
		List<ListaElegiblesCab> cabs = em
				.createQuery(
						"Select c.listaElegiblesCab from ListaElegiblesDet c "
								+ " where c.listaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and c.disponible=true "
								+ " date(c.vtoValidezLista)>= to_date('"
								+ fecActual + "','DD-MM-YYYY') ")
				.setParameter("idGrupo", idConcursoPuestoAgr).getResultList();
		return cabs.size();
	}

	/**
	 * Cantidad de puestos activos: de cada grupo
	 * */
	@SuppressWarnings("unchecked")
	public int cntPuestoActivos(Long idGrupo) {
		List<ConcursoPuestoDet> dets = em
				.createQuery(
						"Select d from ConcursoPuestoDet d "
								+ " where d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and d.activo=true")
				.setParameter("idGrupo", idGrupo).getResultList();

		if (dets.isEmpty())
			return 0;

		return dets.size();
	}

	public Boolean esConcursoInternoOposicionInst(DatosEspecificos tipo) {
		if (tipo.getDescripcion().toUpperCase().trim()
				.equals("CONCURSO INTERNO DE OPOSICION INSTITUCIONAL"))
			return true;

		return false;
	}

	public Boolean esConcursoInternoOposicionInter(DatosEspecificos tipo) {
		if (tipo.getDescripcion().toUpperCase().trim()
				.equals("CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL"))
			return true;

		return false;
	}

	/**
	 * Recibe el di GRupo y de acuerdo a las condiciones
	 * concurso_puesto_agr.id_homologacion id_homologacion <> a nulo y
	 * homologar=false concurso_puesto_agr.homologar si cumple con esto retorna
	 * true
	 * */
	public boolean habModoLectura(Long idGrupo) {
		if (idGrupo == null)
			return false;
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class, idGrupo);
		if (agr.getHomologacionPerfilMatriz() != null
				&& agr.getHomologacionPerfilMatriz().getIdHomologacion() != null
				&& !agr.getHomologar())
			return true;
		else
			return false;
	}

	public Integer getCodigoObj() {
		return codigoObj;
	}

	public void setCodigoObj(Integer codigoObj) {
		this.codigoObj = codigoObj;
	}

	public String getValorObj() {
		return valorObj;
	}

	public void setValorObj(String valorObj) {
		this.valorObj = valorObj;
	}

	public String getCodigoCategoria() {
		return codigoCategoria;
	}

	public void setCodigoCategoria(String codigoCategoria) {
		this.codigoCategoria = codigoCategoria;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public String getCodigoSinarh() {
		return codigoSinarh;
	}

	public void setCodigoSinarh(String codigoSinarh) {
		this.codigoSinarh = codigoSinarh;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public SinAnx getSinAnx() {
		return sinAnx;
	}

	public void setSinAnx(SinAnx sinAnx) {
		this.sinAnx = sinAnx;
	}

	public Long getIdSinAnx() {
		return idSinAnx;
	}

	public void setIdSinAnx(Long idSinAnx) {
		this.idSinAnx = idSinAnx;

		if (idSinAnx != null) {
			sinAnx = new SinAnx();
			sinAnx = em.find(SinAnx.class, idSinAnx);
			categoria = sinAnx.getAnxDescrip();
			codigoCategoria = sinAnx.getCtgCodigo();
			if (plantaCargoDet.getContratado())
				monto = null;
			if (plantaCargoDet.getPermanente())
				buscarMonto(sinAnx.getCatGrupo());
		}
		idSinAnx = null;
	}

	public Long getIdSinCategoria() {
		return idSinCategoria;
	}

	public void setIdSinCategoria(Long idSinCategoria) {
		this.idSinCategoria = idSinCategoria;
	}
	
	public PersonaDTO buscarPersonaAuxiliarRenombrarPersonas(String documento, String paisExpeDoc) {
		PersonaDTO personaRespuesta = new PersonaDTO();
			if (paisExpeDoc.equalsIgnoreCase("PARAGUAY")) {
				// Buscar_web-service(Documento_ID)
				// Calculando los parametros de entrada
				String proxyIP = traerDatosWS("PROXY_IP");
				;
				String proxyPort = traerDatosWS("PROXY_PORT");
				String connectTimeOutStr = traerDatosWS("CONNECT_TIMEOUT");
				String readTimeOutStr = traerDatosWS("READ_TIMEOUT");

				Integer connectTimeOut = (connectTimeOutStr != null) ? new Integer(
						connectTimeOutStr) * 60000 : null;
				Integer readTimeOut = (readTimeOutStr != null) ? new Integer(
						readTimeOutStr) * 60000 : null;

				try {
					WSSFPPersona wSSFPPersona = new WSSFPPersona(proxyIP,
							proxyPort, connectTimeOut, readTimeOut);
					wSSFPPersona.getCtxt().put(StubExt.PROPERTY_CLIENT_TIMEOUT,
							readTimeOut);

					RespuestaObtenerDatosPersona wsAnsw = wSSFPPersona
							.obtenerDatosPersona(documento);
					if (wsAnsw.getEstado() == 98) {
						personaRespuesta.setEstado(WS_ESTADO_SERVIDOR_OCUPADO);
						personaRespuesta.setActivo(null);
						personaRespuesta.setPersona(null);
						personaRespuesta
								.setMensaje("El servidor no se encuentra disponible, inténtelo en algunos instantes.");
						personaRespuesta.setInsertarSug(false);
						personaRespuesta.setHabilitarBtn(null);
					} else if (wsAnsw.getEstado() == 0) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						personaRespuesta.setEstado(WS_ESTADO_NUEVO);
						personaRespuesta.setActivo(null);
						Persona persona = new Persona();
						persona.setDocumentoIdentidad(wsAnsw.getPersona()
								.getNroDocumento());
						persona.setDatosEspecificos(crearNacion(wsAnsw
								.getPersona().getNacionalidad()));
						persona.setPaisByIdPaisExpedicionDoc(crearPais(paisExpeDoc));
						persona.setNombres(wsAnsw.getPersona().getNombre());
						persona.setApellidos(wsAnsw.getPersona().getApellido());
						persona.setSexo(wsAnsw.getPersona().getSexo());
						if (wsAnsw.getPersona().getFechaNacimiento() != null
								&& !wsAnsw.getPersona().getFechaNacimiento()
										.isEmpty()) {
							try {
								persona.setFechaNacimiento(sdf.parse(wsAnsw
										.getPersona().getFechaNacimiento()));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						personaRespuesta.setPersona(persona);
						personaRespuesta
								.setMensaje("No existe la persona, debe presionar el botón Agregar Persona para insertarlo");
						personaRespuesta.setInsertarSug(true);
						personaRespuesta.setHabilitarBtn(true);
					} else {
						personaRespuesta.setEstado(WS_ESTADO_NO_EXISTE);
						personaRespuesta.setActivo(null);
						personaRespuesta.setPersona(null);
						personaRespuesta
								.setMensaje("El Doc. Identidad especificado no existe en la BD Identificaciones. Verifique o consulte con la SFP");
						personaRespuesta.setInsertarSug(false);
						personaRespuesta.setHabilitarBtn(null);

					}

				} catch (Exception e) {
					personaRespuesta.setEstado(WS_ESTADO_NO_EXISTE);
					personaRespuesta.setActivo(null);
					personaRespuesta.setPersona(null);
					personaRespuesta
							.setMensaje("No hay conexión con el Webservice Server");
					personaRespuesta.setInsertarSug(false);
					personaRespuesta.setHabilitarBtn(null);
				}

			}
		return personaRespuesta;
	}

}
