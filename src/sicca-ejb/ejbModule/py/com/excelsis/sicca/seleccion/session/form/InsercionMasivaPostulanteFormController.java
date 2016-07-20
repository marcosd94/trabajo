package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.reportes.form.RptPostulantesInscrpCancelado;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("insercionMasivaPostulanteFormController")
public class InsercionMasivaPostulanteFormController {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	private byte[] uFile = null;
	private String cType = null;
	private String fName = null;

	List<String> lLineasArch;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgrNuevo;
	private Long idConcurso;
	private Concurso concurso;
	//ZD 06/01/16 -- CABECERA DE CONTROL DE LA PLANILLA
	private String cabeceraMatriz ="CI;NOMBRES;APELLIDOS;PARAGUAYO;PAIS;ADMITIDO;OBSERVACIONES";

	public void init() {
		concursoPuestoAgrNuevo = concursoPuestoAgrHome.getInstance();
		idConcursoPuestoAgr = concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
		idConcurso = concursoPuestoAgrNuevo.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
	}

	/**
	 * Inserta datos a partir de un archivo csv. Se espera que los datos esten
	 * dispuestos de la siguiente manera: CABECERA DEL ARCHIVO 3391416;RODRIGO
	 * GABRIEL;SANTOS BACCHETTO 3644302;ENRIQUE DAVID;CESPEDES MENDEZ
	 * 4206214;SANTIAGO DANIEL;TORTORA MORALES
	 * 
	 */

	public void insercionMasiva() {
		if (!precondInsert())
			return;
		UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
				fName, uFile.length, cType, uFile);
		try {
			lLineasArch = FileUtils.readLines(fileItem.getFile(), "ISO-8859-1");
		} catch (IOException e) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Error al subir el archivo");
		}

		StringBuilder cadenaSalida = new StringBuilder();
		StringBuilder erroresSalida = new StringBuilder();
		String titulos = "";
		boolean primeraLinea = true;

		for (String linea : lLineasArch) {

			if (primeraLinea) {
				if(!validarCabecera()){
					return;
				}else{
					primeraLinea = false;
					titulos = linea + System.getProperty("line.separator");
				}
			} else {
				String estado = "FRACASO";
				String observacion = "";
				FilaPostulante fp = new FilaPostulante(linea, "POSTULANTES");

				if (fp != null) {
					if (fp.valido) {
						Persona personaLocal = new Persona();
						 while(fp.getDocumento().length() < 7)
						        fp.setDocumento("0"+fp.getDocumento());
						if (fp.getParaguayo().equalsIgnoreCase("si")) {
							PersonaDTO personaRespuesta = seleccionUtilFormController
									.buscarPersona(fp.getDocumento(),
											"PARAGUAY");
							// Si la persona ya existe en la base de datos
							// local:
							if (personaRespuesta.getEstado() == SeleccionUtilFormController.WS_ESTADO_EXISTE) {
								if (compararNomApe(personaRespuesta
										.getPersona().getNombres(),
										fp.getNombres())
										&& compararNomApe(personaRespuesta
												.getPersona().getApellidos(),
												fp.getApellidos())) {

									// observacion += " PERSONA EXISTENTE ";
									personaLocal = personaRespuesta
											.getPersona();
									estado = "EXITO";
								} else {
									estado = "FRACASO";
									observacion += " Los datos de la columna correspondiente a NOMBRES/APELLIDOS no coinciden con la base de datos de identificaciones ";
								}
//							} else if (personaRespuesta.getEstado() == SeleccionUtilFormController.WS_ESTADO_NUEVO) {
							} else if (personaRespuesta.getHabilitarBtn() != null && personaRespuesta.getHabilitarBtn() && personaRespuesta.getInsertarSug() 
									   && (personaRespuesta.getPersona().getDocumentoIdentidad().equalsIgnoreCase(fp.getDocumento()))) {
								if (compararNomApe(personaRespuesta
										.getPersona().getNombres(),
										fp.getNombres())
										&& compararNomApe(personaRespuesta
												.getPersona().getApellidos(),
												fp.getApellidos())) {

									observacion += " PERSONA NUEVA ";
									personaLocal = personaRespuesta
											.getPersona();
									personaLocal.setActivo(true);
									personaLocal.setTelefonos("");
									personaLocal.setTelContacto("MOVIL");
									personaLocal.setFechaAlta(new Date());
									personaLocal.setDocumentoIdentidad(fp
											.getDocumento());
									personaLocal
											.setUsuAlta(this.usuarioLogueado
													.getCodigoUsuario());
									em.persist(personaLocal);
									em.flush();
									estado = "EXITO";
								} else {
									estado = "FRACASO";
									observacion += " Los datos de la columna correspondiente a NOMBRES/APELLIDOS no coinciden con la base de datos de identificaciones ";
								}
//							} else if (personaRespuesta.getEstado() == SeleccionUtilFormController.WS_ESTADO_NO_EXISTE) {
							}else{
								estado = "FRACASO";
								observacion += " Los datos de la columna correspondiente a CI no coinciden con la base de datos de identificaciones ";
							}
						} else if (fp.getParaguayo().equalsIgnoreCase("no")) {

							String sqlNacionalidad = "select * from seleccion.datos_especificos where datos_especificos.descripcion = 'NO SE CONOCE'";
							Query q0 = em.createNativeQuery(sqlNacionalidad,
									DatosEspecificos.class);
							DatosEspecificos datosEsp = (DatosEspecificos) q0
									.getResultList().get(0);

							String sqlIdPais = "select * from general.pais where pais.descripcion = '"
									+ fp.getPais() + "'";
							Query q1 = em.createNativeQuery(sqlIdPais,
									Pais.class);
							
							List<Pais> paisList = q1.getResultList();
							if (paisList.isEmpty()){
								estado = "FRACASO";
								observacion += " Verifique el PAIS ";
							}else{
								Pais paisExtranjero = paisList
										.get(0);
								String codPais = paisExtranjero.getIdPais()
										.toString();
								String sqlExtranjero = "select * from general.persona where persona.documento_identidad = '"
										+ fp.getDocumento()
										+ "' and persona.id_pais_expedicion_doc = "
										+ codPais;
								Query q2 = em.createNativeQuery(sqlExtranjero,
										Persona.class);
								if (!q2.getResultList().isEmpty()) {
									personaLocal = (Persona) q2.getResultList()
											.get(0);
									estado = "EXITO";
								} else if (!fp.getDocumento().substring(0, 1)
										.equalsIgnoreCase("-")) {
									observacion += " PERSONA NUEVA ";
									personaLocal.setNombres(fp.getNombres());
									personaLocal.setApellidos(fp.getApellidos());
									personaLocal
											.setPaisByIdPaisExpedicionDoc(paisExtranjero);
									personaLocal.setTelefonos("");
									personaLocal.setTelContacto("MOVIL");
									personaLocal.setDatosEspecificos(datosEsp);
									personaLocal.setActivo(true);
									personaLocal.setFechaAlta(new Date());
									personaLocal.setDocumentoIdentidad(fp
											.getDocumento());
									personaLocal.setUsuAlta(this.usuarioLogueado
											.getCodigoUsuario());
									em.persist(personaLocal);
									em.flush();
									estado = "EXITO";
								} else {
									estado = "FRACASO";
									observacion += " Nro de CI Extranjero Invalido ";
								}
							}
							
						} else {
							estado = "FRACASO";
							observacion += " Registro del archivo CSV con error en campo PARAGUAYO";
						}

						// String sqlString =
						// "select * from seleccion.persona_postulante where persona_postulante.id_persona = "
						// + personaLocal.getIdPersona().longValue();
						if (!estado.equals("FRACASO")) {

							// Persistimos postulacion
							String sqlString = "select * from seleccion.postulacion where postulacion.id_persona = "
									+ personaLocal.getIdPersona()
									+ " and postulacion.id_concurso_puesto_agr = "
									+ this.idConcursoPuestoAgr;
							boolean nuevaPostulacion = em
									.createNativeQuery(sqlString,
											Postulacion.class).getResultList()
									.isEmpty();
							if (nuevaPostulacion) {
								PersonaPostulante postulante = cargarPostulante(personaLocal);
								em.persist(postulante);
								em.flush();
								Postulacion postulacion = cargarPostulacion(postulante);
								postulacion.setEstadoPostulacion("ACTIVO");
								em.persist(postulacion);
								em.flush();
								estado = "EXITO";
							} else {
								estado = "FRACASO";
								observacion += " POSTULACION YA EXISTE";
							}

						}

						/* Verificamos si ya existe la evaluacion actual */
						if (!estado.equals("FRACASO")) {
							Query p = em
									.createQuery("select E from EvalDocumentalCab E "
											+ "where E.postulacion.personaPostulante.persona.idPersona =:id_persona and  E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
							p.setParameter("id_persona",
									personaLocal.getIdPersona());
							p.setParameter("id_concurso_puesto_agr",
									this.idConcursoPuestoAgr);

							if (!p.getResultList().isEmpty()) {
								estado = "FRACASO";
								observacion += " EVALUACION YA EXISTE";
								int bandera = 0;
								EvalDocumentalCab eval = (EvalDocumentalCab) p
										.getResultList().get(0);
								// Control de que la admision no haya cambiado
								if ((eval.getAprobado() && fp.getAdmitido()
										.equalsIgnoreCase("no"))
										|| (!eval.getAprobado() && fp
												.getAdmitido()
												.equalsIgnoreCase("si"))) {
									if (fp.getAdmitido().equalsIgnoreCase("si")) {
										eval.setAprobado(true);
										eval.setObservacion("");
									}
									if (fp.getAdmitido().equalsIgnoreCase("no")) {
										eval.setAprobado(false);
										eval.setObservacion(fp.getObservacion());
									}
									observacion += " ADMISION ACTUALIZADA";
									em.merge(eval);
									em.flush();
									bandera = 1;
								}
								if ((bandera == 0)
										&& !eval.getAprobado()
										&& !eval.getObservacion()
												.equalsIgnoreCase(
														fp.getObservacion())) {
									eval.setObservacion(fp.getObservacion());
									observacion += " OBSERVACION ACTUALIZADA ";
									em.merge(eval);
									em.flush();
									estado = "EXITO";
								}
							}

						}

						/*
						 * Si no hubo ningun error = FRACASO, creamos una nueva
						 * entrada para los que se incluiran en la lista larga
						 */

						if (!estado.equals("FRACASO")) {

							EvalDocumentalCab nuevaEvaluacionDocumentalCab = cargarEvaluacionDocumentalCab(
									personaLocal, fp);
							em.persist(nuevaEvaluacionDocumentalCab);
							em.flush();

							estado = "EXITO";

						}

					} else {
						// TODO Si el formato no es correcto, entonces agregar
						// con observación
						estado = "FRACASO";
						observacion += fp.getObservacion();
					}

					// Preparamos el mensaje de salida
					agregarEstadoMotivo(linea, estado, observacion,
							estado.equals("FRACASO") ? erroresSalida
									: cadenaSalida);

				}
			}
		}

		if (lLineasArch.isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Archivo inválido");
			return;
		} else {
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String nArchivo = sdf2.format(new Date()) + "_" + fName;
			String direccion = System.getProperty("jboss.home.dir") + System.getProperty("file.separator") + "temp" +  System.getProperty("file.separator");
			File archSalida = new File(direccion + nArchivo);
			
			
			//File archSalida = new File(nArchivo);
			try {
				String resultado = titulos;
				resultado += erroresSalida.toString();
				resultado += cadenaSalida.toString();
				FileUtils.writeStringToFile(archSalida, resultado);
				if (archSalida.length() > 0) {
					statusMessages.clear();
					statusMessages.add(Severity.INFO, "Insercion Exitosa");
				}

				JasperReportUtils.respondFile(archSalida, nArchivo, false,
						"application/vnd.ms-excel");
			} catch (IOException e) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "IOException");
			}
		}
	}

	//ZD 06/01/16
	private Boolean validarCabecera() {
		String composCabeceraMatriz[] = cabeceraMatriz.split(";");
		Integer cursor = 0;
		if (lLineasArch.size() > 0) {
			String compos[] = lLineasArch.get(0).split(";");
			for (int i = 0; i < composCabeceraMatriz.length; i++) {
				String o = composCabeceraMatriz[i];
				for (int j = cursor; j < compos.length; j++) {
					String p = compos[j];
					if (!o.trim().equalsIgnoreCase(p.trim())) {
						statusMessages.clear();
						statusMessages
								.add(Severity.ERROR,
										//"Error al definir las columnas. Verifique el modelo con la SFP" MP - 20/01/2016
										"Error al definir la Cabecera de las Columna. Verifique el modelo de la SFP");
						return false;
					} else {
						cursor++;
						break;
					}
				}
			}
		}
		return true;
	}
	public boolean compararNomApe(String identificaciones, String parametros) {
		String[] identList = identificaciones.split(" ");
		String[] paramList = parametros.split(" ");
		boolean respuesta = false;
		for (int i = 0; i < identList.length; i++) {
			for (int j = 0; j < paramList.length; j++) {
				if (removeCadenasEspeciales(identList[i]).equalsIgnoreCase(
						removeCadenasEspeciales(paramList[j]))) {
					return true;
				} else {
					respuesta = false;
				}
			}
		}
		return respuesta;
	}

	public String removeCadenasEspeciales(String input) {
		String original = "áàäéèëíìïóòöúùüÁÀÄÉÈËÍÌÏÓÒÖÚÙÜçÇ";
		String ascii = "aaaeeeiiiooouuuAAAEEEIIIOOOUUUcC";
		String output = input;
		for (int i = 0; i < original.length(); i++) {
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}
		return output;
	}

	private Postulacion cargarPostulacion(PersonaPostulante postulante) {
		Postulacion postulacion = new Postulacion();
		postulacion.setActivo(true);
		postulacion.setConcursoPuestoAgr(this.concursoPuestoAgrNuevo);
		postulacion.setPersonaPostulante(postulante);
		postulacion.setPersona(postulante.getPersona());
		postulacion.setFechaPostulacion(new Date());
		/* Que hago con usu_postulacion? */
		postulacion.setUsuPostulacion("");
		postulacion.setTipo("CARPETA");
		return postulacion;
	}

	private PersonaPostulante cargarPostulante(Persona persona) {
		PersonaPostulante postulante = new PersonaPostulante();
		postulante.setActivo(true);
		postulante.setApellidos(persona.getApellidos());
		postulante.setNombres(persona.getNombres());
		postulante.setDocumentoIdentidad(persona.getDocumentoIdentidad());
		postulante.setFechaAlta(new Date());
		postulante.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		postulante.setPersona(persona);
		return postulante;
	}

	private Boolean precondInsert() {
		statusMessages.clear();
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar un archivo");
			return false;
		}
		if (cType != null && cType.equalsIgnoreCase("")) {
			statusMessages
					.add(Severity.ERROR,
							"Se espera archivo en formato CSV (valores separados por coma)");
			return false;
		}
		if (!fName.endsWith(".csv")) {
			statusMessages
					.add(Severity.ERROR,
							"Se espera archivo en formato CSV (valores separados por coma)");
			return false;
		}
		return true;
	}

	private void agregarEstadoMotivo(String linea, String estado,
			String motivo, StringBuilder salida) {
		String cSeparador = ";";
		linea = linea + cSeparador + estado + cSeparador + motivo;
		salida.append(linea);
		salida.append(System.getProperty("line.separator"));
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

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String volverListaGrupos() {
		concurso = em.find(Concurso.class, idConcurso);
		return "/seleccion/concursoPublicacion/AccionAgregarGrupo.seam?concursoIdConcurso="
				+ idConcurso;
	}

	public String terminarInsercionPostulantes() {
		concurso = em.find(Concurso.class, idConcurso);

		if (controlPostulantesInsertados()) {
			// imprimirCU
			Lista lista = new Lista();
			lista.setConcursoPuestoAgr(concursoPuestoAgrNuevo);
			lista.setTipo("LISTA LARGA");
			lista.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
			lista.setFechaPublicacion(new Date());

			em.persist(lista);
			em.flush();

			String textoHtml = generarTextoPublicacion();
			PublicacionPortal entity = new PublicacionPortal();
			entity.setConcurso(concurso);
			entity.setConcursoPuestoAgr(concursoPuestoAgrNuevo);
			entity.setTexto(textoHtml);
			entity.setFechaAlta(new Date());
			entity.setActivo(true);
			entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(entity);
			em.flush();

			concursoPuestoAgrNuevo = em.find(ConcursoPuestoAgr.class,
					idConcursoPuestoAgr);
			concursoPuestoAgrNuevo.setEstado(1004);
			em.merge(concursoPuestoAgrNuevo);
			em.flush();
			concurso = em.find(Concurso.class, idConcurso);

			return "/seleccion/concursoPublicacion/AccionAgregarGrupo.seam?concursoIdConcurso="
					+ idConcurso;

		} else {
			statusMessages.add(Severity.WARN,
					"No existen Postulantes insertados");
			return null;
		}

	}

	private String generarTextoPublicacion() {
		String texto = new String();
		String hr = "<hr>";
		String h1O = "<h1>";
		String h1C = "</h1>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		String hora1 = "";
		String hora2 = "";
		String ciud = "";
		String dpto = "";
		SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		// Necesario si se implementa la recepcion de los datos de una
		// convocatoria.
		/*
		 * if (listaPublicada.getHoraDesde() != null) hora1 =
		 * sdfHora.format(listaPublicada.getHoraDesde()); if
		 * (listaPublicada.getHoraHasta() != null) hora2 =
		 * sdfHora.format(listaPublicada.getHoraHasta()); if
		 * (listaPublicada.getCiudad() != null) ciud =
		 * listaPublicada.getCiudad().getDescripcion(); if
		 * (listaPublicada.getCiudad() != null &&
		 * listaPublicada.getCiudad().getDepartamento() != null) dpto =
		 * listaPublicada.getCiudad().getDepartamento() .getDescripcion();
		 * 
		 * List<Lista> listaPublicar = new ArrayList<Lista>(); listaPublicar =
		 * buscarDatosListaLarga();
		 */
		texto = hr + fechaPublicacion + texto + br + spanO;
		if (concursoPuestoAgrNuevo.getIdConcursoPuestoAgr() != null) {
			texto = texto
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_86&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgrNuevo.getIdConcursoPuestoAgr()
					+ "'>Lista Larga de Admitidos</a>";
			texto = texto + spanC;
			texto = texto + br + spanO;
			// Necesario para imprimir la lista de No Admitidos
			texto = texto
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ListaNoAdmitidos&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgrNuevo.getIdConcursoPuestoAgr()
					+ "'>Lista de NO Admitidos</a>";

		}

		texto = texto + spanC;
		// Mismo caso anterior, necesario si se agrega la carga de datos de una
		// convocatoria.
		/*
		 * if (listaPublicada.getFechaConv() != null){ texto = texto + br + h1O
		 * + "Convocatoria de ReuniÃ³n Informativa: " + h1C; texto = texto +
		 * spanO + "Fecha: " + spanC +
		 * sdfFecha.format(listaPublicada.getFechaConv()) + br; } if
		 * (listaPublicada.getHoraDesde() != null) texto = texto + spanO +
		 * "Hora: " + spanC + hora1;
		 * 
		 * if(listaPublicada.getHoraHasta() != null) texto = texto + " a " +
		 * hora2;
		 * 
		 * texto = texto + br;
		 * 
		 * if (listaPublicada.getLugar() != null) texto = texto + spanO +
		 * "Lugar: " + spanC + listaPublicada.getLugar() + br; if
		 * (listaPublicada.getCiudad() != null &&
		 * listaPublicada.getCiudad().getDepartamento() != null) texto = texto +
		 * spanO + "Departamento: " + spanC + dpto + br; if
		 * (listaPublicada.getCiudad() != null) texto = texto + spanO +
		 * "Ciudad: " + spanC + ciud + br; if (listaPublicada.getDireccion() !=
		 * null) texto = texto + spanO + "DirecciÃ³n: " + spanC +
		 * listaPublicada.getDireccion() + br; if
		 * (listaPublicada.getObservacion() != null) texto = texto + spanO +
		 * "OBS.: " + spanC + listaPublicada.getObservacion() + br;
		 */
		return texto;
	}

	public void printPostulantes() throws Exception {
		if (controlPostulantesInsertados()) {
			RptPostulantesInscrpCancelado rptPostulantesInscrpCancelado = (RptPostulantesInscrpCancelado) Component
					.getInstance(RptPostulantesInscrpCancelado.class, true);
			rptPostulantesInscrpCancelado
					.setIdConcursoPuestoAgr(concursoPuestoAgrNuevo
							.getIdConcursoPuestoAgr());
			rptPostulantesInscrpCancelado.imprimir();
		} else{
			statusMessages.clear();
			statusMessages.add(Severity.WARN,
					"No existen Postulantes insertados");
		}
	}

	public void printAdmitidos() throws Exception {

		if (controlPostulantesInsertados()) {
			ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
			SinEntidad sinEntidad = new SinEntidad();
			SinNivelEntidad nivelEntidad = new SinNivelEntidad();
			String codConcurso = "";
			if (concurso != null) {
				configuracionUoCab = concurso.getConfiguracionUoCab();
				if (configuracionUoCab != null) {
					codConcurso = codConcurso + " DE "
							+ configuracionUoCab.getDescripcionCorta();
					sinEntidad = configuracionUoCab.getEntidad()
							.getSinEntidad();
				}
				if (sinEntidad != null)
					nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
			}

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado == null ? "-"
					: usuarioLogueado.getCodigoUsuario());
			param.put("concurso", codConcurso + " - " + concurso.getNombre());
			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());
			param.put(
					"entidad",
					sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "
					+ configuracionUoCab.getDenominacionUnidad());
			param.put("grupo", concursoPuestoAgrNuevo.getDescripcionGrupo());

			param.put("codigo", "" + "Documento de Identidad");

			param.put("vacancias",
					"" + concursoPuestoAgrNuevo.getCantidadPuestos());

			List<Object[]> lista = consulta_aux();

			param.put("fecha", "Aun no se ha publicado");

			if (lista == null) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_WARN,
								"No existen datos...", "No hay datos..."));
				return;
			}

			Integer cont = 0;
			for (Object[] obj : lista) {
				cont++;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("numero", cont);
				if (obj[0] != null)
					map.put("usu_alta", obj[0].toString());

				listaDatosReporte.add(map);
			}
			JasperReportUtils.respondPDF("RPT_CU167_Lista_Larga_Admitidos_aux",
					false, listaDatosReporte, param);
		} else{
			statusMessages.clear();
			statusMessages.add(Severity.WARN,
					"No existen Postulantes insertados");
		}
	}

	public void printNoAdmitidos() throws Exception {

		if (controlPostulantesInsertados()) {

			ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
			SinEntidad sinEntidad = new SinEntidad();
			SinNivelEntidad nivelEntidad = new SinNivelEntidad();
			String codConcurso = "";
			if (concurso != null) {
				configuracionUoCab = concurso.getConfiguracionUoCab();
				if (configuracionUoCab != null) {
					codConcurso = codConcurso + " DE "
							+ configuracionUoCab.getDescripcionCorta();
					sinEntidad = configuracionUoCab.getEntidad()
							.getSinEntidad();
				}
				if (sinEntidad != null)
					nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
			}

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado == null ? "-"
					: usuarioLogueado.getCodigoUsuario());
			param.put("concurso", codConcurso + " - " + concurso.getNombre());
			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());
			param.put(
					"entidad",
					sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "
					+ configuracionUoCab.getDenominacionUnidad());
			param.put("grupo", concursoPuestoAgrNuevo.getDescripcionGrupo());

			param.put("fecha", "Aun no se ha publicado");

			List<Object[]> lista = consulta_NO_Admitidos();
			if (lista == null) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_WARN,
								"No existen datos...", "No hay datos..."));
				return;
			}

			Integer cont = 0;
			for (Object[] obj : lista) {
				cont++;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("numero", cont);
				if (obj[0] != null) {
					map.put("usu_alta", obj[0].toString());
					map.put("observacion", obj[2].toString());
					if (obj[3] != null)
						map.put("vacancia", obj[3].toString());

				}
				listaDatosReporte.add(map);
			}
			JasperReportUtils.respondPDF("RPT_Lista_NO_Admitidos", false,
					listaDatosReporte, param);
		} else{
			statusMessages.clear();
		    statusMessages.add(Severity.WARN,
					"No existen Postulantes insertados");
		}
	}

	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		SinNivelEntidad nivelEntidad = sinNivelEntidadList
				.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta_aux() {
		String sql;
		/* Obtener los numeros de cedula de todos los admitidos. */

		sql = "select per_post.documento_identidad as cod, eval_cab.id_eval_documental_cab as id "
				+ "from seleccion.eval_documental_cab eval_cab  "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = eval_cab.id_concurso_puesto_agr  "
				+ "join seleccion.postulacion postulacion  "
				+ "on postulacion.id_postulacion = eval_cab.id_postulacion  "
				+ "join seleccion.persona_postulante per_post  "
				+ "on per_post.id_persona_postulante = postulacion.id_persona_postulante  "
				+ "where eval_cab.aprobado is true  "
				+ "and postulacion.activo is true "
				+ " and eval_cab.tipo = 'EVALUACION DOCUMENTAL'"
				+ "and eval_cab.activo is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgrNuevo.getIdConcursoPuestoAgr()
				+ " order by per_post.usu_alta";
		try {

			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta_NO_Admitidos() {
		String sql = "select per_post.documento_identidad as cod, eval_cab.id_eval_documental_cab as id, eval_cab.observacion"
				+ ", agr.cantidad_puestos "
				+ "from seleccion.eval_documental_cab eval_cab  "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = eval_cab.id_concurso_puesto_agr  "
				+ "join seleccion.postulacion postulacion  "
				+ "on postulacion.id_postulacion = eval_cab.id_postulacion  "
				+ "join seleccion.persona_postulante per_post  "
				+ "on per_post.id_persona_postulante = postulacion.id_persona_postulante  "
				+ "where eval_cab.aprobado is false  "
				+ "and postulacion.activo is true "
				+ " and eval_cab.tipo = 'EVALUACION DOCUMENTAL'"
				+ "and eval_cab.activo is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgrNuevo.getIdConcursoPuestoAgr()
				+ " order by per_post.usu_alta";
		try {

			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void eliminarInsercionPostulantes() throws Exception {

		String sqlString = "select * from seleccion.postulacion where postulacion.id_concurso_puesto_agr = "
				+ this.idConcursoPuestoAgr;
		Query p = em.createNativeQuery(sqlString, Postulacion.class);

		List<Postulacion> postulaciones = p.getResultList();

		int i = 0;
		if (!postulaciones.isEmpty()) {
			while (i < postulaciones.size()) {
				Postulacion postulacion = (Postulacion) postulaciones.get(i);
				PersonaPostulante personaPostulante = postulacion
						.getPersonaPostulante();
				List<EvalDocumentalCab> evaluaciones = obtenerEvaluacionesDocumentales(personaPostulante
						.getPersona());

				for (EvalDocumentalCab evaluacion : evaluaciones) {
					em.remove(evaluacion);
				}
				em.remove(personaPostulante);
				em.remove(postulacion);

				i++;
			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO,
					"Eliminación exitosa de Postulantes");
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.INFO,
					"No existen Postulantes a eliminar");
		}
	}

	private EvalDocumentalCab cargarEvaluacionDocumentalCab(Persona persona,
			FilaPostulante fp) {
		EvalDocumentalCab evaluacionDocumentalCab = new EvalDocumentalCab();
		evaluacionDocumentalCab.setActivo(true);
		if (fp.getAdmitido().equalsIgnoreCase("si")) {
			evaluacionDocumentalCab.setAprobado(true);
		}
		if (fp.getAdmitido().equalsIgnoreCase("no")) {
			evaluacionDocumentalCab.setAprobado(false);
			/*
			 * if(fp.getObservacion().equalsIgnoreCase("")){
			 * evaluacionDocumentalCab
			 * .setObservacion("No se especificó motivo."); } else{
			 * evaluacionDocumentalCab.setObservacion(fp.getObservacion()); }
			 */
			evaluacionDocumentalCab.setObservacion(fp.getObservacion());
		}
		evaluacionDocumentalCab
				.setConcursoPuestoAgr(this.concursoPuestoAgrNuevo);
		evaluacionDocumentalCab.setEvaluado(true);
		evaluacionDocumentalCab.setTipo("EVALUACION DOCUMENTAL");
		evaluacionDocumentalCab.setFechaEvaluacion(new Date());
		evaluacionDocumentalCab.setFechaAlta(new Date());
		evaluacionDocumentalCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		evaluacionDocumentalCab.setIncluido(false);

		/*
		 * Obtenemos el id de postulacion
		 */
		Query p = em
				.createQuery("select P from Postulacion P "
						+ "where P.personaPostulante.persona.idPersona =:id_persona and  P.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
		p.setParameter("id_persona", persona.getIdPersona());
		p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
		List<Postulacion> postulacion = p.getResultList();

		evaluacionDocumentalCab.setPostulacion(postulacion.get(0));

		return evaluacionDocumentalCab;
	}

	private List<EvalDocumentalCab> obtenerEvaluacionesDocumentales(
			Persona persona) {
		Query p = em
				.createQuery("select E from EvalDocumentalCab E "
						+ "where E.postulacion.personaPostulante.persona.idPersona =:id_persona "
						+ "and  E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr");
		p.setParameter("id_persona", persona.getIdPersona());
		p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
		List<EvalDocumentalCab> listaEvalDocCab = p.getResultList();
		return listaEvalDocCab;
	}

	private boolean controlPostulantesInsertados() {

		Query p = em
				.createQuery("select count(E) from EvalDocumentalCab E "
						+ "where E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
		p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);

		Long rowCnt = (Long) p.getSingleResult();

		if (rowCnt > 0) {
			System.out.println("cant pos eval : " + rowCnt);
			return Boolean.TRUE;
		} else
			return Boolean.FALSE;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgrNuevo() {
		return concursoPuestoAgrNuevo;
	}

	public void setConcursoPuestoAgrNuevo(ConcursoPuestoAgr concursoPuestoAgrNuevo) {
		this.concursoPuestoAgrNuevo = concursoPuestoAgrNuevo;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}
}