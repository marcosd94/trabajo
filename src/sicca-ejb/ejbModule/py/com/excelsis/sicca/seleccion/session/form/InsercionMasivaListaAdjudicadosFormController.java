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
import org.jboss.seam.core.SeamResourceBundle;
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
@Name("insercionMasivaListaAdjudicadosFormController")
public class InsercionMasivaListaAdjudicadosFormController {

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
	 * CABECERA DEL ARCHIVO
	 * 3391416;RODRIGO GABRIEL;SANTOS BACCHETTO
	 * 3644302;ENRIQUE DAVID;CESPEDES MENDEZ
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
			statusMessages.add(Severity.INFO, "Error al subir el archivo");
		}

		StringBuilder cadenaSalida = new StringBuilder();
		String estado = "EXITO";
		cantidadLineas = 0;
		//Ciclo para contar la cantdad de adjudicados o seleccionados que se reciben
		for (String linea : lLineasArch) {
			cantidadLineas++;
			if(cantidadLineas > 1){
				seleccionados++;
			}
		}
		//Condicion para verificar si la cantidad de seleccionados es mayor a la cantidad de vacancias (en cuyo caso se procede a abortar el proceso e imprimir
		//un mensaje de error) o verificar si esta es menor (se imprime el mensaje pero se continua)
		
		if(seleccionados > concursoPuestoAgrNuevo.getCantidadPuestos()){
			estado = "FRACASO";
			cadenaSalida.append(estado + " - CANTIDAD DE AJUDICADOS ES SUPERIOR A LA CANTIDAD DE PUESTOS VACANTES PARA EL GRUPO");
			cadenaSalida.append(System.getProperty("line.separator"));
		}
		else if(seleccionados < concursoPuestoAgrNuevo.getCantidadPuestos()){
			cadenaSalida.append("ADVERTENCIA - CANTIDAD DE ADJUDICADOS ES INFERIOR A LA CANTIDAD DE PUESTOS VACANTES PARA EL GRUPO");
			cadenaSalida.append(System.getProperty("line.separator"));
		}
		seleccionados = 0;
		
		if(!estado.equals("FRACASO")){

		cantidadLineas = 0;
		for (String linea : lLineasArch) {

			cantidadLineas++;
			estado = "EXITO";
			String observacion = "";
			FilaPostulante fp = new FilaPostulante(linea);
			List<Persona> lista = null;
			if (cantidadLineas > 1 && fp != null && fp.valido) {
				//Verifica si la persona leida en el registro del archivo CSV existe en la Tabla Persona
				String sqlPersona = "select * from general.persona where persona.documento_identidad = '"+fp.getDocumento()+ "'";
				Query q1 = em.createNativeQuery(sqlPersona, Persona.class);
				Persona personaLocal = new Persona();
				int banderaEncontroPersonas = 0;
				//Si existe, se almacena el registro de la tabla
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
				//Verificamos que la persona figure en la lista corta como seleccionado
				if(!estado.equals("FRACASO")){
					int banderaExisteEnListaCorta = 0;					
					int i=0;
					if (banderaEncontroPersonas == 1) {
						while(i<lista.size()){
							personaLocal = (Persona) lista.get(i);
							Query p = em.createQuery("select E from EvalReferencialPostulante E "
									+ "where E.postulacion.personaPostulante.persona.idPersona =:id_persona and  E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr and E.listaCorta=:esta_en_lista_corta");
									p.setParameter("id_persona", personaLocal.getIdPersona());
									p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
									p.setParameter("esta_en_lista_corta", true);
							List<EvalReferencialPostulante> listaEvalRefPost = p.getResultList();
							if (!listaEvalRefPost.isEmpty()) {
								banderaExisteEnListaCorta = 1;
								i = lista.size();
							}
							i++;
						}
					}
					if (banderaExisteEnListaCorta == 0) {
						estado = "FRACASO";
						observacion += " NO SELECCIONADO/ELEGIBLE EN LISTA CORTA";
					} else if (banderaExisteEnListaCorta == 1){
						observacion += " SELECCIONADO/ELEGIBLE EN LISTA CORTA";
								
						Query p = em.createQuery("select E from EvalReferencialPostulante E "
								+ "where E.postulacion.personaPostulante.persona.idPersona =:id_persona and  E.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr and E.listaCorta=:esta_en_lista_corta");
								p.setParameter("id_persona", personaLocal.getIdPersona());
								p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
								p.setParameter("esta_en_lista_corta", true);
						EvalReferencialPostulante auxEvalReferencialPostulante = (EvalReferencialPostulante) p.getResultList().get(0);
						
						//Validaciones para actualizaciones
						//Bandera indicadora de actualizaciones hechas
						int bandera = 0;
						if(auxEvalReferencialPostulante.getSelAdjudicado() == null){
							estado = "EXITO";
							auxEvalReferencialPostulante.setSelAdjudicado(true);;
							bandera = 1;
							em.merge(auxEvalReferencialPostulante);
							em.flush();
						}
					}
				}
				agregarEstadoMotivo(linea, estado, observacion, cadenaSalida);
			}
			if (cantidadLineas > 1 && !fp.valido) {
				estado = "FRACASO";
				cadenaSalida
						.append(estado
								+ " - ARCHIVO INGRESADO CON MENOS CAMPOS DE LOS NECESARIOS. DATOS INCORRECTOS EN ALGUNA COLUMNA.");
				cadenaSalida.append(System.getProperty("line.separator"));
			}

			// FIN FOR
		}
		//FIN IF
		}
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

	/*private EvalReferencialPostulante cargarEvalReferencialPostulante(Persona persona,FilaPostulante fp) {
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

		Query p = em.createQuery("select P from Postulacion P "
						+ "where P.personaPostulante.persona.idPersona =:id_persona and  P.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
		p.setParameter("id_persona", persona.getIdPersona());
		p.setParameter("id_concurso_puesto_agr", this.idConcursoPuestoAgr);
		List<Postulacion> postulacion = p.getResultList();
			
			
		evaluacionReferencialPostulante.setPostulacion(postulacion.get(0));

		return evaluacionReferencialPostulante;
	}*/

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
	
	
	public void print() {
		Connection conn = null;

		ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
		SinEntidad sinEntidad = new SinEntidad();
		SinNivelEntidad nivelEntidad = new SinNivelEntidad();
		String codConcurso = "";
		if (concurso != null) {
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				//Si el codigo de concurso no se autogenero
				if(concurso.getNumero() == null){
					codConcurso = "DE " + configuracionUoCab.getDescripcionCorta();
				}
				else{
					codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
					codConcurso = codConcurso + " DE " + configuracionUoCab.getDescripcionCorta();
				}
				
				sinEntidad = configuracionUoCab.getEntidad()
						.getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		try {
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado == null ? "PORTAL"
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
			param.put("id", concursoPuestoAgrNuevo.getIdConcursoPuestoAgr());
			
			param.put("fecha", "Aun no se ha publicado");
			
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF(
					"RPT_CU161_Lista_Postulantes_Seleccionados", param, false,
					conn, usuarioLogueado);
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null)
				JpaResourceBean.closeConnection(conn);
		}

	}
	
	public String publicarListaAdjudicados(){

		Lista lista = new Lista();
		lista.setConcursoPuestoAgr(concursoPuestoAgrNuevo);
		lista.setFechaPublicacion(new Date());
		lista.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
		lista.setTipo("ADJUDICADO");
		em.persist(lista);
		em.flush();
		
		insertarTablaPublicacion();
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		
		concursoPuestoAgrNuevo = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		//Se pasa el concurso al estado de FINALIZADO
		concursoPuestoAgrNuevo.setEstado(1002);
		concursoPuestoAgrNuevo.setFechaAdjudicacion(new Date());
		concursoPuestoAgrNuevo.setUsuMod(usuarioLogueado.getCodigoUsuario());
		concursoPuestoAgrNuevo.setFechaMod(new Date());
		em.merge(concursoPuestoAgrNuevo);
		em.flush();
		concurso = em.find(Concurso.class, idConcurso);
		return "/seleccion/concursoPublicacion/AccionAgregarGrupo.seam?concursoIdConcurso="+idConcurso;
	}
	
	private String generarTextoPublicacion() {
		StringBuffer texto = new StringBuffer();
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String hr = "<hr>";
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		texto.append(hr + fechaPublicacion + br + spanO + "Las personas seleccionadas son: " + spanC + br);
		texto.append(spanO);
		texto.append("<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_161&#38;idConcursoPuestoAgr="
				+ concursoPuestoAgrNuevo.getIdConcursoPuestoAgr()
				+ "'>Lista de seleccionados</a>");
		texto.append(spanC + br);
		return texto.toString();
	}

	private void insertarTablaPublicacion() {
		String textoHtml = generarTextoPublicacion();
		PublicacionPortal entity = new PublicacionPortal();
		entity.setConcurso(concurso);
		entity.setConcursoPuestoAgr(concursoPuestoAgrNuevo);
		entity.setTexto(textoHtml);
		entity.setFechaAlta(new Date());
		entity.setActivo(true);
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(entity);
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