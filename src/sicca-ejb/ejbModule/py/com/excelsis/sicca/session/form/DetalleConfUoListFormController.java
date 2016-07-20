package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.util.Naming;

import enums.Estado;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoDetList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.session.util.UtilesFromController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.OrganigramaUtil;

@Scope(ScopeType.CONVERSATION)
@Name("detalleConfUoListFormController")
public class DetalleConfUoListFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2557230787849768112L;

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	@In(create = true)
	ConfiguracionUoDetList configuracionUoDetList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(required = false)
	UtilesFromController utilesFromController;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Estado estado = Estado.ACTIVO;

	private String codigoUnidadOrg;
	private String tipoOrganigrama;

	private List<SelectItem> tipoOrganigramaSelecItem;
	private List<SelectItem> siNoSelecItem;

	private Boolean imprimirPuestos;

	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private SeguridadUtilFormController seguridadUtilFormController;
	private String tipoReporte;// LINEAL O DETALLADO
	
	private List<ConfiguracionUoDet> listaConfiguracionUoDets = new ArrayList<ConfiguracionUoDet>();
	
	private static final String PRE = locate();

	private void validarOee(Long idOee) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (!seguridadUtilFormController.verificarPerteneceOee(idOee)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	private void validarOee(Concurso concurso) {
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
	}

	public void init() {

		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		configuracionUoDetList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		configuracionUoDetList.setActivo(estado.getValor());
		configuracionUoDetList.listaResultadosUC117();
		
//		if(configuracionUoDetList.getFirstResult() == null)
//			configuracionUoDetList.setListaConfiguracionUoDets(this.obtenerListaConfiguracionUoDetsYCodigoOee());
//		
		
		buildTipoOrganigramaSelectItem();
		utilesFromController =
			(UtilesFromController) Component.getInstance(UtilesFromController.class, true);
		siNoSelecItem = utilesFromController.getSiNoSelectItems();
		imprimirPuestos = true;

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		// tipoOrganigrama = "PDF";
	}

	public void cargarCabecera() {
		grupoPuestosController =
			(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();
		validarOee(grupoPuestosController.getConfiguracionUoCab().getIdConfiguracionUo());

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	public void search() throws Exception {
		if (!validar(false))
			return;

		configuracionUoDetList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		configuracionUoDetList.getConfiguracionUoDet().setDenominacionUnidad(configuracionUoDet.getDenominacionUnidad().trim());
		configuracionUoDetList.getConfiguracionUoDet().setCodigoUo(configuracionUoDet.getCodigoUo());
		
		configuracionUoDetList.setActivo(estado.getValor());
		configuracionUoDetList.listaResultadosUC117();

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
	}

	public void searchAll() {
		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();
		estado = Estado.ACTIVO;
		configuracionUoDetList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		configuracionUoDetList.getConfiguracionUoDet().setDenominacionUnidad(null);
		configuracionUoDet.setDenominacionUnidad(null);
		configuracionUoDetList.listaResultadosUC117();

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
	}

	private Boolean validar(Boolean controlarCab) throws Exception {
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}

		return true;
	}

	private void buildTipoOrganigramaSelectItem() {
		tipoOrganigramaSelecItem = new ArrayList<SelectItem>();
		// tipoOrganigramaSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		tipoOrganigramaSelecItem.add(new SelectItem("I", "Imagen"));
		tipoOrganigramaSelecItem.add(new SelectItem("PDF", "PDF"));
	}

	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	public void imprimirOrganigrama() {
		try {
			if (!validar(false))
				return;

			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());

			String contenidoArchivo = generarArchivoOrganigrama();

//			ServletContext servletContext =
//				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

//			String imagePath = servletContext.getRealPath("\\");
//			imagePath = imagePath.substring(0, imagePath.length() - 24); // se le saca el: \sicca-ear.ear\sicca.war\
			
			
			
			String imagePath = PRE + "\\ORGANIGRAMAS\\";
					imagePath = ponerBarra(imagePath);
			String nombreArchivo = imagePath+"organigrama_"+ configuracionUoCab.getDescripcionCorta()+".gt";
			//String nombreArchivo = imagePath+"organigrama_"+ configuracionUoCab.getDescripcionCorta()+".dot";
			// Random rand = new Random();
			// int nro = rand.nextInt(100000000);
			// String nombreImagenTmp = "organigrama" + nro + ".png";

			// ConfiguracionUoCab configuracionUoCab = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
			SinNivelEntidad sinNivelEntidad =
				em.find(SinNivelEntidad.class, nivelEntidadOeeUtil.getIdSinNivelEntidad());

			String nombreImagenTmp = imagePath + "organigrama_" + configuracionUoCab.getDescripcionCorta() + ".png";
			File arch = new File(nombreImagenTmp);
			
			System.out.println(arch.getAbsolutePath());
			String nombreImagen = nombreImagenTmp;

			OrganigramaUtil.dibujarOrganigrama(contenidoArchivo, nombreArchivo, nombreImagen);

			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			if ("PDF".equalsIgnoreCase(tipoOrganigrama)) {
				reportUtilFormController.init();
				reportUtilFormController.setNombreReporte("RPT_CU136_imprimir_organigrama");
				reportUtilFormController.getParametros().put("path_image", nombreImagen);

				reportUtilFormController.getParametros().put("entidad", configuracionUoCab.getEntidad().getSinEntidad().getEntCodigo()
					+ " " + configuracionUoCab.getEntidad().getSinEntidad().getEntNombre());
				reportUtilFormController.getParametros().put("unidadOrganizativa", configuracionUoCab.getDenominacionUnidad());
				reportUtilFormController.getParametros().put("fechaDesde", configuracionUoCab.getVigenciaDesde());
				reportUtilFormController.getParametros().put("fechaHasta", configuracionUoCab.getVigenciaHasta());
				reportUtilFormController.getParametros().put("nivel", sinNivelEntidad.getNenCodigo()
					+ " " + sinNivelEntidad.getNenNombre());
				reportUtilFormController.getParametros().put("denominacion", null);
				reportUtilFormController.getParametros().put("dependencia", null);

				reportUtilFormController.imprimirReportePdf();
			} else {
				reportUtilFormController.setNombreReporte(nombreImagen);
				reportUtilFormController.imprimirImagen();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void imprimirListado() throws Exception{
		if(this.tipoReporte.equals("LINEAL")){
			this.imprimirConfiguracioUOListaLineal();
		}else if(tipoReporte.equals("DETALLADO")){
			this.imprimirConfiguracioUO();
		}
	}

	public void imprimirConfiguracioUO() throws Exception {
		if (!validar(false))
			return;

		configuracionUoCab =
			em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());

//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU126_IMPRIMIR_CONF_UO");
		reportUtilFormController.getParametros().put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));
		reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());

		// configuracionUoCab = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		if (seleccionUtilFormController == null) {
			seleccionUtilFormController =
				(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
		}

		codigoUnidadOrg = nivelEntidadOeeUtil.traerCodUndOrg();
		
		reportUtilFormController.getParametros().put("codigoEntida", configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo()
			+ "." + configuracionUoCab.getEntidad().getSinEntidad().getEntCodigo());
		reportUtilFormController.getParametros().put("denominacionEntidad", configuracionUoCab.getEntidad().getSinEntidad().getEntNombre());
		reportUtilFormController.getParametros().put("codUo", codigoUnidadOrg);
		reportUtilFormController.getParametros().put("denominacionUo", configuracionUoCab.getDenominacionUnidad());
//		reportUtilFormController.getParametros().put("desde", /*sdf.format(*/configuracionUoCab.getVigenciaDesde()/*)*/);
//		reportUtilFormController.getParametros().put("hasta", /*sdf.format(*/configuracionUoCab.getVigenciaHasta()/*)*/);
		
		String where = " WHERE          1=1";

		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);

		if (configuracionUoDet != null) {
			where +=
				" and uo_cab.id_configuracion_uo =  "
					+ nivelEntidadOeeUtil.getIdConfigCab();

			if (configuracionUoDet.getDenominacionUnidad() != null
				&& !configuracionUoDet.getDenominacionUnidad().trim().equals("")) {
				if (!sufc.validarInput(configuracionUoDet.getDenominacionUnidad().toLowerCase(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				where +=
					" and  lower(configuracion_uo_det.denominacion_unidad) like '%"
						+ configuracionUoDet.getDenominacionUnidad().toLowerCase() + "%'";
			}

		}
		if (estado != null && estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return;
			}
			where += " and  configuracion_uo_det.activo=" + estado.getValor();
		}
		where +=
			" group by configuracion_uo_det.orden,configuracion_uo_det.denominacion_unidad, " +
			"configuracion_uo_det.cond_politica,configuracion_uo_det.prod_adm_publica,configuracion_uo_det.prod_sociedad, " +
			"configuracion_uo_det.interno,configuracion_uo_det.descripcion_finalidad,clasificador_uo.subnivel,nro,clasificador_uo.nivel,configuracion_uo_det.id_configuracion_uo_det, " +
			"sin_entidad.nen_codigo, sin_entidad.ent_codigo, uo_cab.orden,uo_cab.mision";
		reportUtilFormController.getParametros().put("where", where);
		Connection conn = JpaResourceBean.getConnection();
		reportUtilFormController.getParametros().put("REPORT_CONNECTION", conn);

		reportUtilFormController.imprimirReportePdf();
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// IMPRIME EL NUEVO REPORTE DE LISTA LINEAL, EL REPORTE ANTERIOR ERA IMPRESO DESDE imprimirConfiguracioUO()
	
	public void imprimirConfiguracioUOListaLineal() throws Exception {
		if (!validar(false))
			return;

		configuracionUoCab = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());

		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU126_IMPRIMIR_CONF_UO_LISTA_LINEAL");
		reportUtilFormController.getParametros().put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));
		reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
		reportUtilFormController.getParametros().put("idConfiguracionUoCab", configuracionUoCab.getIdConfiguracionUo());
		
		codigoUnidadOrg = nivelEntidadOeeUtil.traerCodUndOrg();
		
		reportUtilFormController.getParametros().put("codigoEntida", configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo()
			+ "." + configuracionUoCab.getEntidad().getSinEntidad().getEntCodigo());
		reportUtilFormController.getParametros().put("denominacionEntidad", configuracionUoCab.getEntidad().getSinEntidad().getEntNombre());
		reportUtilFormController.getParametros().put("codUo", codigoUnidadOrg);
		reportUtilFormController.getParametros().put("denominacionUo", configuracionUoCab.getDenominacionUnidad());

		if (seleccionUtilFormController == null) {
			seleccionUtilFormController =
				(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
		}

		Connection conn = JpaResourceBean.getConnection();
		reportUtilFormController.getParametros().put("REPORT_CONNECTION", conn);

		reportUtilFormController.imprimirReportePdf();
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// METODOS PRIVADOS

	/**
	 * Genera el contenido del archivo para el organigrama a ser impreso
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String generarArchivoOrganigrama() {
		// configuracionUoCab = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		if (configuracionUoCab != null) {
			String archivo =
				"" + "/* Archivo que define el organigrama de la OEE: "
					+ configuracionUoCab.getDenominacionUnidad() + " */ \n" + "digraph unix { \n" +
					/* "\tsize=\"14\"; \n" + */
					"\tnode [shape=box, color=lightblue2, style=filled, ordering=out]; \n";

			String orden = configuracionUoCab.getOrden().toString();
			String nombre = configuracionUoCab.getDenominacionUnidad();
			nombre = parseNombreEntidadOrganigrama(nombre);
			nombre += "\\n" + orden;

			if (configuracionUoCab.getConfiguracionUoDets() != null
				&& configuracionUoCab.getConfiguracionUoDets().size() > 0) {
				List<ConfiguracionUoDet> listaConfiguracionUoDet =
					new ArrayList<ConfiguracionUoDet>();
				for (ConfiguracionUoDet configuracionUoDet : configuracionUoCab.getConfiguracionUoDets()) {
					if (configuracionUoDet.getConfiguracionUoDet() == null) {
						listaConfiguracionUoDet.add(configuracionUoDet);
					}
				}

				archivo =
					generarArchivoOrganigramaDet(archivo, nombre, orden, listaConfiguracionUoDet);
			}

			archivo += "}";
			return archivo;
		}
		return null;
	}

	private String generarArchivoOrganigramaDet(String archivo, String nombrePadre, String ordenPadre, List<ConfiguracionUoDet> configuracionUoDets) {
		Collections.sort(configuracionUoDets);
		// Primero se crea todos los de un nivel
		for (ConfiguracionUoDet configuracionUoDet : configuracionUoDets) {
			if (configuracionUoDet.getActivo()) {
				String orden = ordenPadre + "." + configuracionUoDet.getOrden();
				String nombre = configuracionUoDet.getDenominacionUnidad();
				nombre = parseNombreEntidadOrganigrama(nombre);
				nombre += "\\n" + orden;
				archivo += "\t\"" + nombrePadre + "\" -> \"" + nombre + "\"; \n";
			}
		}

		if (imprimirPuestos) {// Se desea imprimir los puestos?
			// Se recorre de nuevo para imprimir sus puestos respentando el orden
			for (ConfiguracionUoDet configuracionUoDet : configuracionUoDets) {
				if (configuracionUoDet.getActivo()) {
					configuracionUoDet =
						em.find(ConfiguracionUoDet.class, configuracionUoDet.getIdConfiguracionUoDet());
					List<PlantaCargoDet> listaPuestos = configuracionUoDet.getPlantaCargoDets();

					String ordenP = ordenPadre + "." + configuracionUoDet.getOrden();
					String nombreP = configuracionUoDet.getDenominacionUnidad();
					nombreP = parseNombreEntidadOrganigrama(nombreP);
					nombreP += "\\n" + ordenP;

					if (listaPuestos != null && listaPuestos.size() > 0) {
						Collections.sort(listaPuestos);
						for (PlantaCargoDet plantaCargoDet : listaPuestos) {
							if (plantaCargoDet.getActivo()) {
								String orden =
									ordenPadre + "." + configuracionUoDet.getOrden() + ".p"
										+ plantaCargoDet.getOrden();
								String nombre = plantaCargoDet.getDescripcion();
								String persona = getNombreEmpleadoPuesto(plantaCargoDet);
								nombre += " " + persona;
								nombre = parseNombreEntidadOrganigrama(nombre);
								nombre += "\\n" + orden;

								archivo +=
									"\n\tsubgraph{ "
										+ "\n\t node [shape=ellipse, style=filled, color=darkolivegreen3]; "
										+ "\n\t \"" + nombre + "\"; " + "\n\tcolor=white "
										+ "\n\t} \n";

								archivo += "\t\"" + nombreP + "\" -> \"" + nombre + "\"; \n";
							}
						}
					}
				}
			}
		}

		// Se recorre de nuevo para imprimir sus hijos respentando el orden
		for (ConfiguracionUoDet configuracionUoDet : configuracionUoDets) {
			if (configuracionUoDet.getActivo()) {
				configuracionUoDet =
					em.find(ConfiguracionUoDet.class, configuracionUoDet.getIdConfiguracionUoDet());
				String orden = ordenPadre + "." + configuracionUoDet.getOrden();
				String nombre = configuracionUoDet.getDenominacionUnidad();
				nombre = parseNombreEntidadOrganigrama(nombre);
				nombre += "\\n" + orden;

				List<ConfiguracionUoDet> listaConfiguracionUoDet =
					configuracionUoDet.getConfiguracionUoDets();
				archivo =
					generarArchivoOrganigramaDet(archivo, nombre, orden, listaConfiguracionUoDet);
			}
		}

		return archivo;
	}

	/**
	 * Formatea el nombre de una oee
	 * 
	 * @param nombre
	 * @return
	 */
	private String parseNombreEntidadOrganigrama(String nombre) {

		// Se reemplazan los acentos
		// nombre = nombre.replaceAll("á", "a");
		// nombre = nombre.replaceAll("é", "e");
		// nombre = nombre.replaceAll("í", "i");
		// nombre = nombre.replaceAll("ó", "o");
		// nombre = nombre.replaceAll("ú", "u");
		//
		// nombre = nombre.replaceAll("Á", "A");
		// nombre = nombre.replaceAll("É", "E");
		// nombre = nombre.replaceAll("Í", "I");
		// nombre = nombre.replaceAll("Ó", "O");
		// nombre = nombre.replaceAll("Ú", "U");

		// Se corta el nombre si es muy largo de manera a imprimir en otra linea
		int largor = 20;
		int i = largor;
		nombre = nombre.trim();
		while (nombre.length() > i) {
			int pos = nombre.indexOf(" ", i);
			if (pos != -1) {
				nombre = nombre.substring(0, pos) + "\\n" + nombre.substring(pos + 1);
				i += largor;
			} else {
				i = nombre.length() + 1; // termina el proceso
			}
		}

		return nombre;
	}

	public String getUrlToEditPage() {
		return "/planificacion/configuracionUoDet/ConfiguracionUoDetEdit.xhtml?configuracionUoCabIdConfiguracionUo="
			+ nivelEntidadOeeUtil.getIdConfigCab();
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	private String getNombreEmpleadoPuesto(PlantaCargoDet plantaCargoDet) {
		List<EmpleadoPuesto> empleadoPuestos = plantaCargoDet.getEmpleadoPuestos();
		if (empleadoPuestos != null && empleadoPuestos.size() > 0) {
			for (EmpleadoPuesto empleadoPuesto : empleadoPuestos) {
				if (empleadoPuesto.isActivo() && empleadoPuesto.isActual()) {
					Persona p = empleadoPuesto.getPersona();
					return "(" + p.getNombreCompleto() + ")";
				}
			}
		}
		return "";
	}
	
	
	//PARA OBTENER EL LISTADO ORDENADO
	// PRESENTO UNA INCIDENCIA EN LA PAGINACION YA QUE NO PERMITE UTILIZAR EL RESULTLIST DE CONFIGURACIONUODETLIST. 
	public List<ConfiguracionUoDet> obtenerListaConfiguracionUoDetsYCodigoOee() {
		
		String sql = getSqlListaConfiguracionUoDet(this.configuracionUoDetList.getConfiguracionUoCab().getIdConfiguracionUo());
				
		listaConfiguracionUoDets = em.createNativeQuery(sql, ConfiguracionUoDet.class).getResultList(); 	
		return listaConfiguracionUoDets;
	}
	
	private String getSqlListaConfiguracionUoDet(Long idConfiguracionUoCab){
		
		String sql = "select "
				+ " id_configuracion_uo_det,"
				+ " descripcion_corta,"
				+ " orden,"
				+ " denominacion_unidad,"
				+ " cond_politica,"
				+ " prod_adm_publica,"
				+ " prod_sociedad,"
				+ " interno,"
				+ " descripcion_finalidad,"
				+ " direccion,"
				+ " telefono,"
				+ " usu_alta,"
				+ " usu_mod,"
				+ " fecha_mod,"
				+ " fecha_alta,"
				+ " activo,"
				+ " id_configuracion_uo_det_padre,"
				+ " id_clasificador_uo,"
				+ " id_configuracion_uo,"
				+ " estado_anterior"
				+ " from (select  (select planificacion.obtenercodigooee(det1.id_configuracion_uo_det)) as codigo_oee,"
				+ "		det1.* as det "
				+ "		from planificacion.configuracion_uo_det det1 where det1.id_configuracion_uo_det_padre is null "
				+ "		and det1.id_configuracion_uo = "+idConfiguracionUoCab
				+ "		and det1.activo = true"
				+ " union"
				+ " select	(select planificacion.obtenercodigooee(det2.id_configuracion_uo_det)) as codigo_oee,det2.* as det"
				+ "	from planificacion.configuracion_uo_det det2 "
				+ "	join planificacion.configuracion_uo_det det3"
				+ " on det3.id_configuracion_uo_det = det2.id_configuracion_uo_det_padre"
				+ " and det3.activo = true "
				+ " and det3.id_configuracion_uo = " +idConfiguracionUoCab
				+ " and det3.activo = true order by codigo_oee) as detalle";
		
		
		return sql;
		
	}
	
	@SuppressWarnings("unchecked")
	private static String locate() {
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		String dir = "";
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		emsta = emf.createEntityManager();
		List<Referencias> referencias = emsta
				.createQuery(
						"Select r from Referencias r "
								+ " where r.descAbrev like 'ADJUNTOS' and r.dominio like 'ADJUNTOS'")
				.getResultList();
		if (!referencias.isEmpty()) {
			dir = referencias.get(0).getDescLarga();
			if (dir.contains("\\")) {
				dir = dir.replace("\\", System.getProperty("file.separator"));

			} else if (dir.contains("//")) {
				dir = dir.replace("//", System.getProperty("file.separator"));

			} else if (dir.contains("/")) {
				dir = dir.replace("/", System.getProperty("file.separator"));

			}

		}
		return dir;

	}
	
	private static String ponerBarra(String direccion) {
		if (direccion.contains("\\")) {
			direccion = direccion.replace("\\",
					System.getProperty("file.separator"));

		} else if (direccion.contains("//")) {
			direccion = direccion.replace("//",
					System.getProperty("file.separator"));

		} else if (direccion.contains("/")) {
			direccion = direccion.replace("/",
					System.getProperty("file.separator"));

		}
		return direccion;
	}
	
	

	// GETTERS Y SETTERS

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public String getCodigoUnidadOrg() {
		return codigoUnidadOrg;
	}

	public void setCodigoUnidadOrg(String codigoUnidadOrg) {
		this.codigoUnidadOrg = codigoUnidadOrg;
	}

	public void setTipoOrganigrama(String tipoOrganigrama) {
		this.tipoOrganigrama = tipoOrganigrama;
	}

	public String getTipoOrganigrama() {
		return tipoOrganigrama;
	}

	public void setTipoOrganigramaSelecItem(List<SelectItem> tipoOrganigramaSelecItem) {
		this.tipoOrganigramaSelecItem = tipoOrganigramaSelecItem;
	}

	public List<SelectItem> getTipoOrganigramaSelecItem() {
		return tipoOrganigramaSelecItem;
	}

	public void setImprimirPuestos(Boolean imprimirPuestos) {
		this.imprimirPuestos = imprimirPuestos;
	}

	public Boolean getImprimirPuestos() {
		return imprimirPuestos;
	}

	public void setSiNoSelecItem(List<SelectItem> siNoSelecItem) {
		this.siNoSelecItem = siNoSelecItem;
	}

	public List<SelectItem> getSiNoSelecItem() {
		return siNoSelecItem;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<ConfiguracionUoDet> getListaConfiguracionUoDets() {
		return listaConfiguracionUoDets;
	}

	public void setListaConfiguracionUoDets(
			List<ConfiguracionUoDet> listaConfiguracionUoDets) {
		this.listaConfiguracionUoDets = listaConfiguracionUoDets;
	}

	public String getTipoReporte() {
		return tipoReporte;
	}

	public void setTipoReporte(String tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

}
