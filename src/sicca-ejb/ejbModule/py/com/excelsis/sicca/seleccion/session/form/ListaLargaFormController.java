package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.hibernate.dialect.SAPDBDialect;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.PublicacionConcurso;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("listaLargaFormController")
public class ListaLargaFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private Date fecha;
	private Lista listaPublicada;

	private String codConcurso;
	private String observacion;
	private String direccionFisica;
	private String horaDesde;
	private String horaHasta;
	private String lugar;
	private Long idDpto;
	private Long idCiudad;
	private Long idConcursoGrupoPuestoAgr;
	private String direccion;
	private String obs;
	private Boolean estaPublicado;

	private List<EvalDocumentalCab> listaEvalDocumentalCab;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private Lista listaEditada = null;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}

		seguridadUtilFormController.verificarPerteneceOee(
				null,
				concursoPuestoAgr.getIdConcursoPuestoAgr(),
				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO",
						"EVALUACION DOCUMENTAL") + "",
				ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA.getValor());

	}

	/**
	 * Método que inicializa los datos
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		/* Incidencia 0000878 */

		cargarDatosEdit();
		/**/
		listaEvalDocumentalCab = new ArrayList<EvalDocumentalCab>();
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		if(idConcursoGrupoPuestoAgr==null)
			idConcursoGrupoPuestoAgr = concursoPuestoAgr.getIdConcursoPuestoAgr();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();

		estaPublicado = verificarPublicacion();
		if (concurso != null) {
			validarOee();
			if(concurso.getNumero() == null && concurso.getAnhio() == null)
			{
				codConcurso = "";
			}
			else{
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			}
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE "
						+ configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		direccionFisica = separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
				+ separador
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		buscarListaLargaPostulantes();
		updateDepartamento();
		updateCiudad();
	}

	/**
	 * Inicializa los campos de la pantalla seleccion/listaLarga/ListaLargaEdit
	 */

	public void init2() {
		if (idConcursoGrupoPuestoAgr != null) {

			listaEvalDocumentalCab = new ArrayList<EvalDocumentalCab>();
			concursoPuestoAgr = new ConcursoPuestoAgr();
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
					idConcursoGrupoPuestoAgr);
			concurso = new Concurso();
			configuracionUoCab = new ConfiguracionUoCab();
			sinEntidad = new SinEntidad();
			nivelEntidad = new SinNivelEntidad();
			concurso = concursoPuestoAgr.getConcurso();
			estaPublicado = verificarPublicacion();
			if (concurso != null) {
				if(concurso.getNumero() == null && concurso.getAnhio() == null)
				{
					codConcurso = "";
				}
				else{
				codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
				}
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

		}
	}

	/**
	 * Busca el nivel correspondiente a la entidad
	 * 
	 * @param nenCodigo
	 * @return
	 */
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	/**
	 * Busca el codigo de los postulantes para mostrar en la lista larga
	 */
	@SuppressWarnings("unchecked")
	private void buscarListaLargaPostulantes() {
		String sql = "select eval_cab.*  "
				+ "from seleccion.eval_documental_cab eval_cab  "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = eval_cab.id_concurso_puesto_agr  "
				+ "join seleccion.postulacion postulacion "
				+ "on postulacion.id_postulacion = eval_cab.id_postulacion "
				+ "join seleccion.persona_postulante pp "
				+ "on pp.id_persona_postulante = postulacion.id_persona_postulante "
				+ "where eval_cab.aprobado is true  "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and postulacion.activo is true " + "order by pp.usu_alta";

		listaEvalDocumentalCab = em.createNativeQuery(sql,
				EvalDocumentalCab.class).getResultList();
	}

	/**
	 * Este metodo es llamado desde el Link "Lista Larga de Admitidos"
	 */
	public void print2() {
		init2();
		print();
	}
	
	/**
	 * Este metodo es llamado desde el Link "Lista de NO Admitidos"
	 */
	public void imprimirListaNoAdmitidos() {
		init2();
		printListaNoAdmitidos();
	}
	
	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void printListaNoAdmitidos() {
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put(
				"usuario",
				usuarioLogueado == null ? "-" : usuarioLogueado
						.getCodigoUsuario());
		param.put("concurso", codConcurso + " - " + concurso.getNombre());
		param.put(
				"nivel",
				nivelEntidad.getNenCodigo() + " - "
						+ nivelEntidad.getNenNombre());
		param.put("entidad",
				sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "
				+ configuracionUoCab.getDenominacionUnidad());
		param.put("grupo", concursoPuestoAgr.getDescripcionGrupo());
		
		HistorialActividadesGrupo historial = obtenerFechaActividad(idConcursoGrupoPuestoAgr, ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA.getDescripcion());
		if (!validarTipoConcurso() && (historial==null || historial.getFechaFin()==null))
			param.put("fecha", "Aun no se ha publicado");
		else if(validarTipoConcurso())
		{
			Query l = em.createQuery("select L from Lista L "
					+ "where L.tipo =:tipo and  L.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
			l.setParameter("tipo", "LISTA LARGA");
			l.setParameter("id_concurso_puesto_agr", concursoPuestoAgr.getIdConcursoPuestoAgr());
			List<Lista> auxLista = l.getResultList();
			if(auxLista.isEmpty())
			{
				param.put("fecha", "Aun no se ha publicado");
			}else{
				String aux = auxLista.get(0).getFechaPublicacion().toString().substring(0, 10);
				String aux2 = aux.substring(0, 5);
				aux2 = aux2 + aux.substring(8, 10) + "-";
				aux2 = aux2 + aux.substring(5, 7);
				param.put("fecha",aux2);				
			}

		}
		else{
			Format formato = new SimpleDateFormat("dd-MM-yyyy");
			param.put("fecha", formato.format(historial.getFechaFin()));
		}

		List<Object[]> lista = consulta_NO_Admitidos();
		if (lista == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
//		Integer cont = 0;
//		for (Object[] obj : lista) {
//			cont++;
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("numero", cont);
//			if (obj[0] != null)
//				map.put("usu_alta", obj[0].toString());
//
//			listaDatosReporte.add(map);
//		}
//		JasperReportUtils.respondPDF("RPT_CU167_Lista_Larga_Admitidos", false,
//				listaDatosReporte, param);
		
		Integer cont = 0;
		for (Object[] obj : lista) {
			cont++;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("numero", cont);
			if (obj[0] != null){
				map.put("usu_alta", obj[0].toString());
				map.put("observacion", obj[2].toString());
				if(obj[3]!=null)
					map.put("vacancia", obj[3].toString());
				
			}
			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_Lista_NO_Admitidos", false,
				listaDatosReporte, param);
	}

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void print() {
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put(
				"usuario",
				usuarioLogueado == null ? "-" : usuarioLogueado
						.getCodigoUsuario());
		param.put("concurso", codConcurso + " - " + concurso.getNombre());
		param.put(
				"nivel",
				nivelEntidad.getNenCodigo() + " - "
						+ nivelEntidad.getNenNombre());
		param.put("entidad",
				sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "
				+ configuracionUoCab.getDenominacionUnidad());
		param.put("grupo", concursoPuestoAgr.getDescripcionGrupo());
		
		if(validarTipoConcurso())
		{
		param.put("codigo", ""+ "Documento de Identidad");
		}else
		{
		param.put("codigo", ""+ "Código de Postulante");
		}
		
		param.put("vacancias", ""+concursoPuestoAgr.getCantidadPuestos());
//		List<Object[]> lista = consulta();
		List<Object[]> lista = consulta_aux();

		HistorialActividadesGrupo historial = obtenerFechaActividad(idConcursoGrupoPuestoAgr, ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA.getDescripcion());
		if (!validarTipoConcurso() &&(historial==null || historial.getFechaFin()==null))
			param.put("fecha", "Aun no se ha publicado");
		else if(validarTipoConcurso())
		{
			Query l = em.createQuery("select L from Lista L "
					+ "where L.tipo =:tipo and  L.concursoPuestoAgr.idConcursoPuestoAgr =:id_concurso_puesto_agr ");
			l.setParameter("tipo", "LISTA LARGA");
			l.setParameter("id_concurso_puesto_agr", concursoPuestoAgr.getIdConcursoPuestoAgr());
			List<Lista> auxLista = l.getResultList();
			if(auxLista.isEmpty())
			{
				param.put("fecha", "Aun no se ha publicado");
			}else{
				String aux = auxLista.get(0).getFechaPublicacion().toString().substring(0, 10);
				String aux2 = aux.substring(0, 5);
				aux2 = aux2 + aux.substring(8, 10) + "-";
				aux2 = aux2 + aux.substring(5, 7);
				param.put("fecha",aux2);				
			}

		}
		else{
			Format formato = new SimpleDateFormat("dd-MM-yyyy");
			param.put("fecha", formato.format(historial.getFechaFin()));
		}
		if (lista == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
//		Integer cont = 0;
//		for (Object[] obj : lista) {
//			cont++;
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("numero", cont);
//			if (obj[0] != null)
//				map.put("usu_alta", obj[0].toString());
//
//			listaDatosReporte.add(map);
//		}
//		JasperReportUtils.respondPDF("RPT_CU167_Lista_Larga_Admitidos", false,
//				listaDatosReporte, param);
		
		Integer cont = 0;
		for (Object[] obj : lista) {
			cont++;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("numero", cont);
			if (obj[0] != null)
				map.put("usu_alta", obj[0].toString());
				
			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU167_Lista_Larga_Admitidos_aux", false,
				listaDatosReporte, param);
	}


	private HistorialActividadesGrupo obtenerFechaActividad(Long idGrupo, String descripcionActividad) {
		String sql = "SELECT * FROM seleccion.historial_actividades_grupo hist " 
				+ "WHERE hist.descripcion = '" + descripcionActividad
				+ "' and hist.id_concurso_puesto_agr = "	+ idGrupo.toString()
				+ " and hist.fecha_fin is not null";
		
		HistorialActividadesGrupo resultado;
		try {
			resultado = (HistorialActividadesGrupo) em.createNativeQuery(sql, HistorialActividadesGrupo.class).getSingleResult();
		} catch (Exception e) {
			//El que llama este metodo tiene que verificar el valor de retorno.
			resultado = null;
		}
		return resultado;
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta() {
		String sql = "select per_post.usu_alta as cod, eval_cab.id_eval_documental_cab as id  "
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
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
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
	
	/**
	 * busca los datos para ser enviados en el reporte se utilizara auxiliarmente en vez del consuta()
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta_aux() {
		String sql;
		/*Verificar si el concurso del que se desea desplegar la 
		lista de admitidos pertenece a un concurso de publicacion.*/

		if (validarTipoConcurso())
		{	
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
					+ " and eval_cab.fecha_alta = (select min(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
					+ "and agr.id_concurso_puesto_agr = "
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " order by per_post.usu_alta";
		}else{
			sql = "select per_post.usu_alta as cod, eval_cab.id_eval_documental_cab as id "
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
				+ " and eval_cab.fecha_alta = (select min(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " order by per_post.usu_alta";
		}
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
	
	/**
	 * busca los datos para ser enviados en el reporte se utilizara auxiliarmente en vez del consuta()
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta_NO_Admitidos() {
		String sql;
		if(validarTipoConcurso()){
			sql = "select per_post.documento_identidad as cod, eval_cab.id_eval_documental_cab as id, eval_cab.observacion"
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
					+ " and eval_cab.fecha_alta = (select min(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
					+ "and agr.id_concurso_puesto_agr = "
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " order by per_post.usu_alta";
		}
		else{
			sql = "select per_post.usu_alta, eval_cab.id_eval_documental_cab as id, eval_cab.observacion"
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
					+ " and eval_cab.fecha_alta = (select min(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
					+ "and agr.id_concurso_puesto_agr = "
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ " order by per_post.usu_alta";
		}
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

	/**
	 * Recupera los departamentos de Paraguay
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
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

	@SuppressWarnings("unchecked")
	private List<Departamento> getDepartamentosByPais() {
		String sql = "select dpto.* " + "from general.departamento dpto "
				+ "join general.pais p " + "on p.id_pais = dpto.id_pais "
				+ "where dpto.activo is true "
				+ "and lower(p.descripcion)='paraguay' "
				+ "order by dpto.descripcion";
		List<Departamento> listaDpto = new ArrayList<Departamento>();
		listaDpto = em.createNativeQuery(sql, Departamento.class)
				.getResultList();
		if (listaDpto.size() > 0)
			return listaDpto;

		return new ArrayList<Departamento>();
	}

	/**
	 * Carga el combo de ciudad de acuerdo al departamento seleccionado
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
			ciudadList.setMaxResults(null);
			ciudadList.setOrder("descripcion");
			return ciudadList.getResultList();
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
	 * Métodos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPageListaLargaEdit() {
		return "/seleccion/listaLarga/ListaLargaEdit.xhtml?fromToPage=seleccion/listaLarga/ListaLargaList&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	public String getUrlToPageListaLargaList() {
		return "/seleccion/listaLarga/ListaLargaList.xhtml?fromToPage=seleccion/listaLarga/ListaLargaEdit&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	private void cargarDatosEdit() throws Exception {
		setearDatosConvocatoria();
		if (concursoPuestoAgr != null) {
			Query q = em
					.createQuery("select Lista from Lista Lista where Lista.tipo = 'LISTA LARGA' and Lista.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ concursoPuestoAgr.getIdConcursoPuestoAgr());
			List<Lista> lista = q.getResultList();
			/* No puede haber mas de una coincidencia */
			if (lista.size() == 1) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Lista entity = lista.get(0);
				fecha = entity.getFechaConv();
				lugar = entity.getLugar();
				if (entity.getHoraHasta() != null)
					horaHasta = sdf.format(entity.getHoraHasta());
				if (entity.getHoraDesde() != null)
					horaDesde = sdf.format(entity.getHoraDesde());
				obs = entity.getObservacion();
				direccion = entity.getDireccion();
				if (entity.getCiudad() != null) {
					idDpto = entity.getCiudad().getDepartamento()
							.getIdDepartamento();
					idCiudad = entity.getCiudad().getIdCiudad();
				}
				listaEditada = lista.get(0);
			} else if (lista.size() > 1) {
				throw new Exception(
						"Sólo debe existir un registro que coincida con el grupo "
								+ concursoPuestoAgr.getIdConcursoPuestoAgr());
			} else {
				listaEditada = null;
			}
		}
	}

	private void setearDatosConvocatoria() {
		fecha = null;
		lugar = null;
		horaDesde = null;
		horaHasta = null;
		obs = null;
		direccion = null;
		idCiudad = null;
		idDpto = null;
	}

	/**
	 * Metodo que inserta en la tabla Lista Larga
	 * 
	 * @return
	 */
	public String save() {
		if (precond()) {
			return null;
		}

		try {
			Lista listaLarga = null;
			if (listaEditada != null) {
				listaLarga = listaEditada;
			} else {
				listaLarga = new Lista();
			}
			listaLarga.setTipo("LISTA LARGA");
			if (idCiudad != null)
				listaLarga.setCiudad(em.find(Ciudad.class, idCiudad));
			listaLarga.setConcursoPuestoAgr(concursoPuestoAgr);
			if (direccion != null && !direccion.trim().isEmpty())
				listaLarga.setDireccion(direccion);
			if (fecha != null)
				listaLarga.setFechaConv(fecha);
			if (horaDesde != null && !horaDesde.trim().isEmpty()) {
				int[] horaD = Utiles.getHora(horaDesde);
				Date fechaDesde = new Date();
				fechaDesde.setHours(horaD[0]);
				fechaDesde.setMinutes(horaD[1]);
				fechaDesde.setSeconds(0);
				listaLarga.setHoraDesde(fechaDesde);
			}
			if (horaHasta != null && !horaHasta.trim().isEmpty()) {
				int[] horaH = Utiles.getHora(horaHasta);
				Date fechaHasta = new Date();
				fechaHasta.setHours(horaH[0]);
				fechaHasta.setMinutes(horaH[1]);
				fechaHasta.setSeconds(0);
				listaLarga.setHoraHasta(fechaHasta);
			}
			if (lugar != null && !lugar.trim().isEmpty())
				listaLarga.setLugar(lugar);
			if (obs != null && !obs.trim().isEmpty())
				listaLarga.setObservacion(obs);
			listaLarga.setUsuMod(usuarioLogueado.getCodigoUsuario());
			listaLarga.setFechaMod(new Date());
			em.persist(listaLarga);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return getUrlToPageListaLargaList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		statusMessages.clear();
		statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
				.getString("GENERICO_NO_MSG"));
		return null;
	}

	/**
	 * Verifica que se cumplan las condiciones para realizar la operación en la
	 * DB
	 * 
	 * @return
	 */
	private Boolean precond() {

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setLenient(false);
		
		try {
			//VERIFICAR SI LA REUNIÓN INFORMATIVA SERÁ OBLIGATORIA
//			if(tieneReunionInformativa(this.idConcursoGrupoPuestoAgr)){
//				statusMessages.clear();
//				statusMessages.add(Severity.ERROR,
//						"Ya se registro la reunión informatíva.");
//				
//				return true;
//			}
//				
			
			Date hd = null;
			if (horaDesde != null && !horaDesde.trim().isEmpty())
				sdf.parse(horaDesde);
			Date hh = null;
			if (horaHasta != null && !horaHasta.trim().isEmpty())
				sdf.parse(horaHasta);
			if (hd != null && hh != null && hd.getTime() >= hh.getTime()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"La Hora Desde debe ser menor a la Hora Hasta");
				return true;
			}
		} catch (ParseException e) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Formato de Hora incorrecto. Debe introducir, Ej.: HH:mm (formato 24 Hs.)");
			return true;
		}
		if (obs.length() > 500) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Superada la cantidad máxima (500) de caracteres en el campo Observación");
			return true;
		}

		return false;
	}
	
	public Boolean tieneReunionInformativa(Long idConcursoPuestoAgr){
		Boolean retorno = false;
		String sql = "select * from seleccion.lista where tipo = 'LISTA LARGA'  and id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		
		List<Lista> lista = em.createNativeQuery(sql,Lista.class).getResultList();
		
		if(lista.size() > 0)
			retorno = true;
		
		
		return retorno;
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
		if (listaPublicada.getHoraDesde() != null)
			hora1 = sdfHora.format(listaPublicada.getHoraDesde());
		if (listaPublicada.getHoraHasta() != null)
			hora2 = sdfHora.format(listaPublicada.getHoraHasta());
		if (listaPublicada.getCiudad() != null)
			ciud = listaPublicada.getCiudad().getDescripcion();
		if (listaPublicada.getCiudad() != null
				&& listaPublicada.getCiudad().getDepartamento() != null)
			dpto = listaPublicada.getCiudad().getDepartamento()
					.getDescripcion();

		List<Lista> listaPublicar = new ArrayList<Lista>();
		listaPublicar = buscarDatosListaLarga();
		texto = hr + fechaPublicacion + texto + br + spanO;
		if (concursoPuestoAgr.getIdConcursoPuestoAgr() != null){
			texto = texto
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_86&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "'>Lista Larga de Admitidos</a>";
			texto = texto + spanC;
			texto = texto + br + spanO;
			texto = texto
					+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ListaNoAdmitidos&#38;idConcursoPuestoAgr="
					+ concursoPuestoAgr.getIdConcursoPuestoAgr()
					+ "'>Lista de NO Admitidos</a>";
		
		}
		
		
		texto = texto + spanC;
		
		if (listaPublicada.getFechaConv() != null){
			texto = texto + br + h1O + "Convocatoria de Reunión Informativa: " + h1C;
			texto = texto + spanO + "Fecha: " + spanC
					+ sdfFecha.format(listaPublicada.getFechaConv()) + br;
		}
		if (listaPublicada.getHoraDesde() != null)
			texto = texto + spanO + "Hora: " + spanC + hora1; 
					
		if(listaPublicada.getHoraHasta() != null)
			texto = texto + " a " + hora2;
		
		texto = texto + br;
		
		if (listaPublicada.getLugar() != null)
			texto = texto + spanO + "Lugar: " + spanC
					+ listaPublicada.getLugar() + br;
		if (listaPublicada.getCiudad() != null
				&& listaPublicada.getCiudad().getDepartamento() != null)
			texto = texto + spanO + "Departamento: " + spanC + dpto + br;
		if (listaPublicada.getCiudad() != null)
			texto = texto + spanO + "Ciudad: " + spanC + ciud + br;
		if (listaPublicada.getDireccion() != null)
			texto = texto + spanO + "Dirección: " + spanC
					+ listaPublicada.getDireccion() + br;
		if (listaPublicada.getObservacion() != null)
			texto = texto + spanO + "OBS.: " + spanC
					+ listaPublicada.getObservacion() + br;
		return texto;
	}

	private void insertarTablaPublicacion() {
		String textoHtml = generarTextoPublicacion();
		PublicacionPortal entity = new PublicacionPortal();
		entity.setConcurso(concurso);
		entity.setConcursoPuestoAgr(concursoPuestoAgr);
		entity.setTexto(textoHtml);
		entity.setFechaAlta(new Date());
		entity.setActivo(true);
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(entity);
		em.flush();
	}

	public String publicar() {
		List<Lista> listaPublicar = new ArrayList<Lista>();
		listaPublicar = buscarDatosListaLarga();
//		VERIFICAR SI LA REUNION INFORMATIVA SERA OBLIGATORIA
//		  if (listaPublicar.size() == 0) { statusMessages.clear();
//		  statusMessages .add(Severity.ERROR,
//		  "La lista no puede ser publicada, debe cargar previamente la convocatoria"
//		  ); return null; }
//		 
		try {
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"LISTA LARGA");
			if (ref != null) {
				concursoPuestoAgr.setEstado(ref.getValorNum());
				concursoPuestoAgr.setFechaEvalDocHasta(new Date());
				em.merge(concursoPuestoAgr);
			}
			listaPublicada = new Lista();
			if (listaPublicar != null && listaPublicar.size() > 0) {
				for (Lista l : listaPublicar) {
					listaPublicada = l;
					l.setFechaMod(new Date());
					l.setUsuMod(usuarioLogueado.getCodigoUsuario());
					l.setFechaPublicacion(new Date());
					l.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
					l.setTipo("LISTA LARGA");
					em.merge(l);

				}
			}
			/* Incidencia 916 */
			SeleccionUtilFormController seleccionUtilFormController = (SeleccionUtilFormController) Component
					.getInstance(SeleccionUtilFormController.class, true);
			EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet(
					"CONCURSO", "LISTA LARGA");

			List<ConcursoPuestoDet> lista = seleccionUtilFormController
					.getConcursoPuestoDet(concursoPuestoAgr
							.getIdConcursoPuestoAgr());
			for (ConcursoPuestoDet m : lista) {
				m.setEstadoDet(estadoDet);
				m.setUsuMod(usuarioLogueado.getCodigoUsuario());
				m.setFechaMod(new Date());
				em.merge(m);
				m.getPlantaCargoDet().setEstadoDet(estadoDet);
				m.getPlantaCargoDet().setUsuMod(
						usuarioLogueado.getCodigoUsuario());
				m.getPlantaCargoDet().setFechaMod(new Date());
				em.merge(m.getPlantaCargoDet());
			}

			/**/

			Observacion tablaObservacion = new Observacion();
			tablaObservacion.setConcurso(concurso);
			if (observacion != null && observacion.trim().isEmpty())
				tablaObservacion.setObservacion(observacion);

			tablaObservacion.setFechaAlta(new Date());
			tablaObservacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			tablaObservacion.setUsuMod(usuarioLogueado.getCodigoUsuario());

			tablaObservacion.setIdTaskInstance(jbpmUtilFormController
					.getIdTaskInstanceActual(concursoPuestoAgr
							.getIdProcessInstance()));

			em.persist(tablaObservacion);

			for (EvalDocumentalCab listEval : listaEvalDocumentalCab) {
				//if (listaPublicada.getFechaConv() != null//MODIFICADO RV
						//&& listaPublicada.getDireccion() != null)
					enviarEmails(listEval.getPostulacion()
							.getPersonaPostulante());
			}
			//if (listaPublicada.getFechaConv() != null//MODIFICADO RV
					//&& listaPublicada.getDireccion() != null)
				insertarTablaPublicacion();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return nextTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Boolean verificarPublicacion() {
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"LISTA LARGA");
		//Ref2, referencia para el caso de concursos de publicacion
		Referencias ref2 = new Referencias();
		ref2 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_LARGA");
		if (ref != null) {
			if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()))
				return true;
		}
		if(ref2 != null) {
			if (concursoPuestoAgr.getEstado().equals(ref2.getValorNum()))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Lista> buscarDatosListaLarga() {
		String sql = "select l.* " + "from seleccion.lista l "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = l.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		return em.createNativeQuery(sql, Lista.class).getResultList();
	}

	public String nextTask() {
		try {
			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.ELABORAR_PUBLICACION_LISTA_LARGA);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.REALIZAR_EVALUACIONES);
			jbpmUtilFormController.setTransition("next");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return "next";
	}

	@SuppressWarnings("unchecked")
	public void enviarEmails(PersonaPostulante persona) {
		String dirEmail = persona.getEMail();
		String _obs="";
		String asunto = null;
		asunto = " Admisión en Lista Larga";
		String texto = "";
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String fechaConv = "";
		if (listaPublicada.getFechaConv() != null)
			fechaConv = sdf.format(listaPublicada.getFechaConv());
		String horaDesde = "";
		String horaHasta = "";
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		if (listaPublicada.getHoraDesde() != null){// AGREGADO RV
			
			horaDesde = sdfHora.format(listaPublicada.getHoraDesde()) ;
			
			
		}
		
		if (listaPublicada.getHoraHasta() != null){
			
			horaHasta = sdfHora.format(listaPublicada.getHoraHasta()) ;
			
		}
		String lugar = "";
		if (listaPublicada.getLugar() != null)
			lugar = listaPublicada.getLugar();
		String direccion = "";
		if (listaPublicada.getDireccion() != null)
			direccion = listaPublicada.getDireccion();
		String ciudad = "";
		if (listaPublicada.getCiudad() != null)
			ciudad = listaPublicada.getCiudad().getDescripcion();
		String observacionEmail = "";
		if (listaPublicada.getObservacion() != null && !listaPublicada.equals("")){
			observacionEmail = listaPublicada.getObservacion();
			_obs="Obs.: ";		
		}
		try {
			
			texto="<p> <b> Estimado(a) "+persona.getNombres()+" "+persona.getApellidos()+",</b></p>"
					+ "<p> Le comunicamos que ha sido admitido(a) en el Concurso: <b>"+concurso.getNumero()+"/"+concurso.getAnhio()+" de "
					+configuracionUoCab.getDescripcionCorta()+ " - "+concurso.getNombre()+", "+concursoPuestoAgr.getDescripcionGrupo()+".</b></p>";
			
			if (listaPublicada.getFechaConv() != null){
					texto += "<p>  Adem&aacute;s, le informamos que se realizar&aacute; una reuni&oacute;n informativa con los postulantes admitidos. </p> "
					+ "<p>Los datos de la reunión son los siguientes:  </p>"
					+ "<blockquote><b>Fecha: </b> "+fechaConv+"</blockquote>";
					
					if (listaPublicada.getLugar() != null)
						texto += "<blockquote><b>Lugar: </b> "+lugar+"</blockquote>";
					if (listaPublicada.getDireccion() != null)
						texto += "<blockquote><b>Direcci&oacute;n: </b> "+direccion+" - "+ciudad+".</blockquote>";
					if (listaPublicada.getHoraDesde() != null)
						texto += "<blockquote><b>A partir de las</b> "+horaDesde;
					if (listaPublicada.getHoraHasta() != null)
						texto +="<b> hasta las </b> "+horaHasta+" hs.</p>";
					if (listaPublicada.getObservacion() != null && !listaPublicada.equals(""))
						texto += "<p>"+_obs+" <b>"+observacionEmail+"</b></p><br/> ";
					
					texto += "<p>&#161;Aguardaremos su presencia&#33;:</p>";
			}	
			
			texto += "<p><b> Atentamente,<br/> "+configuracionUoCab.getDenominacionUnidad()+".</b></p>";
					
			if(!informacion().equals(""))
					texto += "<p>Para mayor información comunicarse con "+informacion()+"</p>";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private String informacion() {
		String resultado = "";
		String sql1 = "select pres.* "
				+ "from seleccion.present_aclarac_doc pres "
				+ "where pres.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		String sql2 = "select pres.* "
				+ "from seleccion.present_aclarac_doc pres "
				+ "where pres.id_concurso = " + concurso.getIdConcurso();

		List<PresentAclaracDoc> listaPres = new ArrayList<PresentAclaracDoc>();
		listaPres = em.createNativeQuery(sql1, PresentAclaracDoc.class)
				.getResultList();
		if (listaPres.size() == 0) {
			listaPres = em.createNativeQuery(sql2, PresentAclaracDoc.class)
					.getResultList();
		}
		for (PresentAclaracDoc pr : listaPres) {
			resultado = resultado + " - " + pr.getEmail();
		}
		return resultado;

	}

	private String buscarHora(String cod) {

		String[] arrayCodigo = cod.split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public List<EvalDocumentalCab> getListaEvalDocumentalCab() {
		return listaEvalDocumentalCab;
	}

	public void setListaEvalDocumentalCab(
			List<EvalDocumentalCab> listaEvalDocumentalCab) {
		this.listaEvalDocumentalCab = listaEvalDocumentalCab;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getHoraDesde() {
		return horaDesde;
	}

	public void setHoraDesde(String horaDesde) {
		this.horaDesde = horaDesde;
	}

	public String getHoraHasta() {
		return horaHasta;
	}

	public void setHoraHasta(String horaHasta) {
		this.horaHasta = horaHasta;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public Boolean getEstaPublicado() {
		return estaPublicado;
	}

	public void setEstaPublicado(Boolean estaPublicado) {
		this.estaPublicado = estaPublicado;
	}

	public Lista getListaPublicada() {
		return listaPublicada;
	}

	public void setListaPublicada(Lista listaPublicada) {
		this.listaPublicada = listaPublicada;
	}

	public Long getIdConcursoGrupoPuestoAgr() {
		return idConcursoGrupoPuestoAgr;
	}

	public void setIdConcursoGrupoPuestoAgr(Long idConcursoGrupoPuestoAgr) {
		this.idConcursoGrupoPuestoAgr = idConcursoGrupoPuestoAgr;
	}

	public ConcursoPuestoAgrHome getConcursoPuestoAgrHome() {
		return concursoPuestoAgrHome;
	}

	public void setConcursoPuestoAgrHome(
			ConcursoPuestoAgrHome concursoPuestoAgrHome) {
		this.concursoPuestoAgrHome = concursoPuestoAgrHome;
	}
	
	private boolean validarTipoConcurso() {
		
		Referencias ref = new Referencias();
		Referencias ref2 = new Referencias();
		Referencias ref3 = new Referencias();
		Referencias ref4 = new Referencias();
		Referencias ref5 = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_LARGA");
		ref2 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_CORTA");
		ref3 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_FINALIZADO");
		ref4 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_TERNA_FINAL");
		ref5 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_ADJUDICADOS");
	
		if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref2.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref3.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref4.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref5.getValorNum()))
		return true;
		else
		return false;
	}

}
