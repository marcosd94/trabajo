package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;


















import org.apache.commons.io.FileUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.desvinculacion.session.form.CargaMasivaFuncioFC;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("insercionMasivaListaCortaFormController")
public class InsercionMasivaListaCortaFormController {

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
	private int cantidadLineas;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgrNuevo;
	private DatosGrupoPuesto datosGrupoPuestoNuevo;
	private Long idConcurso;
	private Concurso concurso;
	private int seleccionados;
	private int ternados;
	private int elegibles;

	public void init() {
		concursoPuestoAgrNuevo = concursoPuestoAgrHome.getInstance();
		idConcursoPuestoAgr = concursoPuestoAgrNuevo.getIdConcursoPuestoAgr();
		idConcurso = concursoPuestoAgrNuevo.getConcurso().getIdConcurso();
		datosGrupoPuestoNuevo = ObtenerDatosGrupoPuesto(concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());
		concurso = em.find(Concurso.class, idConcurso);
	}

	/**
	 * Inserta datos a partir de un archivo csv. Se espera que los datos esten
	 * dispuestos de la siguiente manera:
	 * (en caso de ser un grupo evaluado por Merito:)
	 * CABECERA DEL ARCHIVO
	 * 3391416;RODRIGO GABRIEL;SANTOS BACCHETTO;90;S
	 * 3644302;ENRIQUE DAVID;CESPEDES MENDEZ;85.6;E
	 * 4206214;SANTIAGO DANIEL;TORTORA MORALES;73;R
	 * 
	 * (en caso de ser un grupo evaluado por Terna:)
	 * CABECERA DEL ARCHIVO 
	 * 3391416;RODRIGO GABRIEL;SANTOS BACCHETTO;90;T
	 * 3644302;ENRIQUE DAVID;CESPEDES MENDEZ;85.6;R
	 * 4206214;SANTIAGO DANIEL;TORTORA MORALES;73;R
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
			statusMessages.add(Severity.INFO, "Error al subir el archivo");
		}

		StringBuilder cadenaSalida = new StringBuilder();
		String estado = "EXITO";
		cantidadLineas = 0;
		//Ciclo para contar la cantdad de adjudicados o seleccionados que se reciben
		for (String linea : lLineasArch) {
			
			cantidadLineas++;
			FilaPostulante fp = new FilaPostulante(linea,"LISTA_CORTA");
			if(cantidadLineas > 1 && !fp.valido){
				estado = "FRACASO";
				cadenaSalida.append(estado + " - ARCHIVO INGRESADO CON MENOS CAMPOS DE LOS NECESARIOS.");
				cadenaSalida.append(System.getProperty("line.separator"));
			}
			if(cantidadLineas > 1 && fp.valido){
				if(fp.getTipo().equals("S")){
					seleccionados++;
				}
				if(fp.getTipo().equals("E")){
					elegibles++;
				}
				if(fp.getTipo().equals("T")){
					ternados++;
				}
			}
		}
		//Condicion para verificar si la cantidad de seleccionados es mayor a la cantidad de vacancias (en cuyo caso se procede a abortar el proceso e imprimir
		//un mensaje de error) o verificar si esta es menor (se imprime el mensaje pero se continua)
		
		
		if (!estado.equals("FRACASO") && datosGrupoPuestoNuevo.getMerito() && !datosGrupoPuestoNuevo.getTerna()) {
			if(seleccionados > concursoPuestoAgrNuevo.getCantidadPuestos()){
				estado = "FRACASO";
				cadenaSalida.append(estado + " - CANTIDAD DE SELECCIONADOS ES SUPERIOR A LA CANTIDAD DE PUESTOS VACANTES PARA EL GRUPO");
				cadenaSalida.append(System.getProperty("line.separator"));
			}
			if(elegibles < 1){
				estado = "FRACASO";
				cadenaSalida.append(estado + " - NO EXISTEN ELEGIBLES EN EL ARCHIVO, DEBE HABER POR LO MENOS UNO");
				cadenaSalida.append(System.getProperty("line.separator"));
			}
			seleccionados = 0;
			elegibles = 0;
		}
		if (!estado.equals("FRACASO") && !datosGrupoPuestoNuevo.getMerito() && datosGrupoPuestoNuevo.getTerna()) {
			if(ternados > (concursoPuestoAgrNuevo.getCantidadPuestos() + 2)){
				estado = "FRACASO";
				cadenaSalida.append(estado + " - CANTIDAD DE TERNADOS ES SUPERIOR AL (RECOMENDADO MÁS DOS) DE PUESTOS VACANTES PARA EL GRUPO");
				cadenaSalida.append(System.getProperty("line.separator"));
			}
			ternados = 0;
		}
		
		if(!estado.equals("FRACASO")){

		cantidadLineas = 0;
		for (String linea : lLineasArch) {

			cantidadLineas++;
			estado = "EXITO";
			String observacion = "";
			FilaPostulante fp = new FilaPostulante(linea,"LISTA_CORTA");
			List<Persona> lista = null;
			if (cantidadLineas > 1 && fp != null && fp.valido) {
				//Verifica si la persona leida en el registro del archivo CSV existe en la Tabla Persona
				String sqlPersona = "select * from general.persona where persona.documento_identidad = '"+fp.getDocumento()+ "'";
				Query q1 = em.createNativeQuery(sqlPersona, Persona.class);
				Persona personaLocal = new Persona();
				int banderaEncontroPersonas = 0;
				//Si existe, compara nombre y apellido para validar
				if(!q1.getResultList().isEmpty()){
					lista = q1.getResultList();
					if (compararNomApe(lista.get(0).getNombres(), removeCadenasEspeciales(fp.getNombres()))
						&& compararNomApe(lista.get(0).getApellidos(), removeCadenasEspeciales(fp.getApellidos())) ) {
						banderaEncontroPersonas = 1;
					} else {
						estado = "FRACASO";
						observacion += " PERSONA NO REGISTRADA EN LA BASE DE DATOS LOCAL";
					}
					
				} else {
					estado = "FRACASO";
					observacion += " PERSONA NO REGITRADA EN LA BASE DE DATOS LOCAL";
				}
				//Verificamos que la persona figure en la lista larga como aprobado
				if(!estado.equals("FRACASO")){
					int banderaExisteEnListaLarga = 0;
					
					int i=0;
					if (banderaEncontroPersonas == 1) {
						while(i<lista.size()){
							personaLocal = (Persona) lista.get(i);
							Query p = em.createQuery("select E from EvalDocumentalCab E "
										+ "where E.postulacion.personaPostulante.persona.idPersona =:id_persona and  E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr "
										+ "and E.aprobado =:esta_aprobado");
										p.setParameter("id_persona", personaLocal.getIdPersona());
										p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
										p.setParameter("esta_aprobado", true);
							List<EvalDocumentalCab> listaEvalDocCab = p.getResultList();
							if (!listaEvalDocCab.isEmpty()) {
								banderaExisteEnListaLarga = 1;
								i = lista.size();
							}
							i++;
						}
					}
					if (banderaExisteEnListaLarga == 0) {
						estado = "FRACASO";
						observacion += " NO APROBADO EN LISTA LARGA";
					} else if (banderaExisteEnListaLarga == 1){
						observacion += " APROBADO EN LISTA LARGA";
					}
				}
				
				/* Verificamos si el postulante supera efectivamente el porcentaje minimo de evaluacion*/
				if (!estado.equals("FRACASO")) {
					Query p = em
							.createQuery("select D from DatosGrupoPuesto D "
									+ "where D.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
					p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
					DatosGrupoPuesto aux = (DatosGrupoPuesto) p.getResultList().get(0);
					
					if ((fp.getTipo().equalsIgnoreCase("S") || fp.getTipo().equalsIgnoreCase("E") || fp.getTipo().equalsIgnoreCase("T")) && aux.getPorcMinimo()>Double.parseDouble(fp.getPuntaje())) {
						estado = "FRACASO";
						observacion += " NO CUMPLE CON EL PORCENTAJE MINIMO";
					}
					
					
				}
				/* Verificamos si ya existe la evaluacion actual*/
				if (!estado.equals("FRACASO")) {
					Query p = em
							.createQuery("select E from EvalReferencialPostulante E "
									+ "where E.postulacion.personaPostulante.persona.idPersona =:id_persona and  E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
					p.setParameter("id_persona", personaLocal.getIdPersona());
					p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
					
					//Si se encontro la evaluacion
					if (!p.getResultList().isEmpty()) {
						estado = "FRACASO";
						observacion += " YA EXISTE EN LISTA CORTA";
						EvalReferencialPostulante auxEvalReferencialPostulante = (EvalReferencialPostulante) p.getResultList().get(0);
						
						//Validaciones para actualizaciones
						//Si el puntaje ya almacenado en la BD difiere del puntaje recien leido
						if(!(auxEvalReferencialPostulante.getPuntajeRealizado().equals(Float.parseFloat(fp.getPuntaje())))){
							auxEvalReferencialPostulante.setPuntajeRealizado(Float.parseFloat(fp.getPuntaje()));;
							observacion += " PUNTAJE ACTUALIZADO";
							em.merge(auxEvalReferencialPostulante);
							em.flush();
						}
						//Validaciones para actualizaciones de tipos en Grupos de Merito
						if (datosGrupoPuestoNuevo.getMerito() && !datosGrupoPuestoNuevo.getTerna()) {
							if(fp.getTipo().equals("S")){
								seleccionados++;
							}
							//Bandera indicadora de actualizaciones hechas
							int bandera = 0;
							//Si el puntaje ya almacenado en la BD es un RECHAZADO...
							if(auxEvalReferencialPostulante.getSeleccionado() == null){
								//...pero el puntaje recien leido es un SELECCIONADO
								if(fp.getTipo().equalsIgnoreCase("S")){
									bandera = 1;
									auxEvalReferencialPostulante.setSeleccionado(true);
									observacion += " TIPO ACTUALIZADO";
									em.merge(auxEvalReferencialPostulante);
									em.flush();
								}
								//...pero el puntaje recien leido es un ELEGIBLE
								if(fp.getTipo().equalsIgnoreCase("E")){
									bandera = 1;
									auxEvalReferencialPostulante.setSeleccionado(false);
									observacion += " TIPO ACTUALIZADO";
									em.merge(auxEvalReferencialPostulante);
									em.flush();
								}
							}
							else{
								//SELECCIONADO en la BD pero ELEGIBLE en lo leido recien
								if((bandera == 0) && (auxEvalReferencialPostulante.getSeleccionado() == true) && fp.getTipo().equalsIgnoreCase("E")){
									auxEvalReferencialPostulante.setSeleccionado(false);
									bandera = 1;
									observacion += " TIPO ACTUALIZADO";
									em.merge(auxEvalReferencialPostulante);
									em.flush();
								}
								//SELECCIONADO en la BD pero RECHAZADO en lo leido recien
								if((bandera == 0) && (auxEvalReferencialPostulante.getSeleccionado() == true) && fp.getTipo().equals("R")){
									auxEvalReferencialPostulante.setSeleccionado(null);
									bandera = 1;
									observacion += " TIPO ACTUALIZADO";
									em.merge(auxEvalReferencialPostulante);
									em.flush();
								}
								//ELEGIBLE en la BD pero SELECCIONADO en lo leido recien
								if((bandera == 0) && (auxEvalReferencialPostulante.getSeleccionado() == false) && fp.getTipo().equalsIgnoreCase("S")){
									auxEvalReferencialPostulante.setSeleccionado(true);
									bandera = 1;
									observacion += " TIPO ACTUALIZADO";
									em.merge(auxEvalReferencialPostulante);
									em.flush();
								}
								//ELEGIBLE en la BD pero RECHAZADO en lo leido recien
								if((bandera == 0) && (auxEvalReferencialPostulante.getSeleccionado() == false) && fp.getTipo().equals("R")){
									auxEvalReferencialPostulante.setSeleccionado(null);
									bandera = 1;
									observacion += " TIPO ACTUALIZADO";
									em.merge(auxEvalReferencialPostulante);
									em.flush();
								}
							}
						}
						
						//Validaciones para actualizaciones de tipos en Grupos de Terna
						if (!datosGrupoPuestoNuevo.getMerito() && datosGrupoPuestoNuevo.getTerna()) {
							if(fp.getTipo().equals("T")){
								ternados++;
							}
							//Bandera indicadora de actualizaciones hechas
							int bandera = 0;
							//RECHAZADO en la BD pero TERNADO en lo leido recien
							if((auxEvalReferencialPostulante.getListaCorta() == false) && fp.getTipo().equalsIgnoreCase("T")){
								bandera = 1;
								auxEvalReferencialPostulante.setListaCorta(true);
								observacion += " TIPO ACTUALIZADO";
								em.merge(auxEvalReferencialPostulante);
								em.flush();
							}
							//TERNADO en la BD pero RECHAZADO en lo leido recien
							if((bandera == 0) && (auxEvalReferencialPostulante.getListaCorta() == true) && fp.getTipo().equals("R")){
								auxEvalReferencialPostulante.setListaCorta(false);
								bandera = 1;
								observacion += " TIPO ACTUALIZADO";
								em.merge(auxEvalReferencialPostulante);
								em.flush();
							}
						}
					}
					
				}

				/* Si no hubo ningun error = FRACASO, creamos una nueva entrada para los que se incluiran en la lista larga*/
				
				if (!estado.equals("FRACASO")) {
					
					if(fp.getTipo().equals("S")){
						seleccionados++;
					}
					if(fp.getTipo().equals("T")){
						ternados++;
					}

					EvalReferencialPostulante nuevaEvaluacionReferencialPostulante = cargarEvalReferencialPostulante(personaLocal,fp);
					em.persist(nuevaEvaluacionReferencialPostulante);
					em.flush();

					estado = "EXITO";

				}
				agregarEstadoMotivo(linea, estado, observacion, cadenaSalida);				
			
			}//FIN IF CANTIDAD LINEAS MAYOR A 1

		}//FIN FOR LINEA A LINEA
		
		if (datosGrupoPuestoNuevo.getMerito() && !datosGrupoPuestoNuevo.getTerna()) {
			if(seleccionados < concursoPuestoAgrNuevo.getCantidadPuestos()){
				cadenaSalida.append("ADVERTENCIA - CANTIDAD DE SELECCIONADOS INSERTADOS ES INFERIOR A LA CANTIDAD DE PUESTOS VACANTES PARA EL GRUPO");
				cadenaSalida.append(System.getProperty("line.separator"));
			}
			seleccionados = 0;
		}
		if (!datosGrupoPuestoNuevo.getMerito() && datosGrupoPuestoNuevo.getTerna()) {
			if(ternados < (concursoPuestoAgrNuevo.getCantidadPuestos() + 1)){
				cadenaSalida.append("ADVERTENCIA - CANTIDAD DE TERNADOS INSERTADOS ES INFERIOR AL (RECOMENDADO MÁS UNO) DE PUESTOS VACANTES PARA EL GRUPO");
				cadenaSalida.append(System.getProperty("line.separator"));
			}
			ternados = 0;
		}
		
		}//FIN IF SI NO FRACASO POR MAYOR SELECCIONADOS/TERNADOS QUE VACANCIA
		
		if (lLineasArch.isEmpty()) {
			statusMessages.add(Severity.INFO, "Archivo inválido");
			return;
		} else {		
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String nArchivo = sdf2.format(new Date()) + "_" + fName;
			String direccion = System.getProperty("jboss.home.dir") + System.getProperty("file.separator") + "temp" +  System.getProperty("file.separator");
			File archSalida = new File(direccion + nArchivo);
			
			try {
				FileUtils
						.writeStringToFile(archSalida, cadenaSalida.toString());
				if (archSalida.length() > 0) {
					statusMessages.add(Severity.INFO, "Insercion Exitosa");
				}
				JasperReportUtils.respondFile(archSalida, nArchivo, false,
						"application/vnd.ms-excel");
			} catch (IOException e) {
				statusMessages.add(Severity.ERROR, "IOException");
			}
		}
	}

	public boolean compararNomApe(String identificaciones, String parametros) {
		String[] identList = identificaciones.split(" ");
		String[] paramList = parametros.split(" ");
		boolean respuesta = false;
		for (int i = 0; i < identList.length; i++) {
			for (int j = 0; j < paramList.length; j++) {
				if (identList[i].equalsIgnoreCase(paramList[j])) {
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

	private EvalReferencialPostulante cargarEvalReferencialPostulante(Persona persona,FilaPostulante fp) {
		EvalReferencialPostulante evaluacionReferencialPostulante= new EvalReferencialPostulante();
		evaluacionReferencialPostulante.setPuntajeRealizado(Float.parseFloat(fp.getPuntaje()));
		evaluacionReferencialPostulante.setActivo(true);
		evaluacionReferencialPostulante
				.setConcursoPuestoAgr(this.concursoPuestoAgrNuevo);
		evaluacionReferencialPostulante.setFechaEvaluacion(new Date());
		evaluacionReferencialPostulante.setFechaAlta(new Date());
		evaluacionReferencialPostulante.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		if(fp.getTipo().equalsIgnoreCase("R")){
			evaluacionReferencialPostulante.setListaCorta(false);
		}
		if(fp.getTipo().equalsIgnoreCase("S"))
		{
			evaluacionReferencialPostulante.setListaCorta(true);
			evaluacionReferencialPostulante.setSeleccionado(true);
		}
		else if(fp.getTipo().equalsIgnoreCase("E"))
		{
			evaluacionReferencialPostulante.setListaCorta(true);
			evaluacionReferencialPostulante.setSeleccionado(false);
		}
		else if(fp.getTipo().equalsIgnoreCase("T"))
		{
			evaluacionReferencialPostulante.setListaCorta(true);
		}

		/*
		 * Obtenemos el id de postulacion
		 */	
		Query p = em.createQuery("select P from Postulacion P "
						+ "where P.personaPostulante.persona.idPersona =:id_persona and  P.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
		p.setParameter("id_persona", persona.getIdPersona());
		p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
		List<Postulacion> postulacion = p.getResultList();
			
			
		evaluacionReferencialPostulante.setPostulacion(postulacion.get(0));

		return evaluacionReferencialPostulante;
	}

	private Boolean precondInsert() {
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
	
	public void print() throws Exception {
		
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

		try {
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("idConcursoPuestoAgr",concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());
			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
			param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
			
			param.put("fecha", "Aun no se ha publicado");
			
			if(usuarioLogueado != null )
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			else
				param.put("usuario", "Visitante Portal");
			
			if (datosGrupoPuestoNuevo.getMerito() && !datosGrupoPuestoNuevo.getTerna()) {
				JasperReportUtils.respondPDF("listaFinalMerito_cedula",	param, false, conn,usuarioLogueado);
			}
			if (!datosGrupoPuestoNuevo.getMerito() && datosGrupoPuestoNuevo.getTerna()) {
				JasperReportUtils.respondPDF("listaCortaTerna_cedula",	param, false, conn,usuarioLogueado);
			}
			
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String publicarListaCorta(){

		Lista listaARecuperar = new Lista();
		listaARecuperar.setConcursoPuestoAgr(concursoPuestoAgrNuevo);
		listaARecuperar.setTipo("LISTA CORTA");
		listaARecuperar.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
		listaARecuperar.setFechaPublicacion(new Date());
		
		listaARecuperar.setUsuMod(usuarioLogueado.getCodigoUsuario());
		listaARecuperar.setFechaMod(new Date());
		
		listaARecuperar.setConCedula(true);
		
		em.persist(listaARecuperar);
		em.flush();
		
		publicacionPortal(genTextoPublicacion(), concursoPuestoAgrNuevo
				.getConcurso().getIdConcurso(),
				concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());	
		
		concursoPuestoAgrNuevo = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		if (datosGrupoPuestoNuevo.getMerito() && !datosGrupoPuestoNuevo.getTerna()) {
			concursoPuestoAgrNuevo.setEstado(1006);
		}
		if (!datosGrupoPuestoNuevo.getMerito() && datosGrupoPuestoNuevo.getTerna()) {
			concursoPuestoAgrNuevo.setEstado(1005);
		}
		em.merge(concursoPuestoAgrNuevo);
		em.flush();
		concurso = em.find(Concurso.class, idConcurso);
		return "/seleccion/concursoPublicacion/AccionAgregarGrupo.seam?concursoIdConcurso="+idConcurso;
	}
	
	private String genTextoPublicacion() {
		
		String texto = new String();
		String hr = "<hr>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h2O = "<h2>";
		String h2C = "</h2>";
		String spanO = "<span>";
		String spanC = "</span>";
		String br = "</br>";
		texto = texto + hr + fechaPublicacion;
		if (concursoPuestoAgrNuevo.getIdConcursoPuestoAgr() != null)
			texto = texto
					+ br
					+ spanO
					+ "Puede descargar aquí la: "
					+ spanC
					+ br
					+ spanO
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_87&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgrNuevo.getIdConcursoPuestoAgr()
					+ "'>Lista Corta de Pre-Seleccionados</a>";
		texto = texto + spanC;
	
		return texto;
	}
	
	private void publicacionPortal(String texto, Long idConcurso, Long idConcursoGrupoPuestoAgr) {
		
		PublicacionPortal entity = null;
		entity = new PublicacionPortal();
		entity.setActivo(true);
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setFechaAlta(new Date());
		Concurso c = new Concurso();
		c = em.find(Concurso.class, idConcurso);
		entity.setConcurso(new Concurso());
		entity.setConcurso(c);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		ConcursoPuestoAgr ag = new ConcursoPuestoAgr();
		ag = em.find(ConcursoPuestoAgr.class, idConcursoGrupoPuestoAgr);
		entity.setConcursoPuestoAgr(ag);
		entity.setObservacion(true);
		entity.setTexto(texto);
		em.persist(entity);
		em.flush();
	}
	
	public DatosGrupoPuesto ObtenerDatosGrupoPuesto (Long idGrupo){
		
		String sql = "select * from seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "+ idGrupo;
		
		return (DatosGrupoPuesto) em.createNativeQuery(sql,DatosGrupoPuesto.class).getResultList().get(0);	
	}
	
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		SinNivelEntidad nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

}