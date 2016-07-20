package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.hibernate.hql.ast.tree.Case2Node;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.HorasAnios;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalReferencial;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.PublicacionConcurso;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.TachasReclamosModificaciones;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.DatosEspecificosformController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("tachasReclamosModifFormController")
public class TachasReclamosModifFormController implements Serializable {

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
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private DatosEspecificos datosEspecificosTipoConcurso = new DatosEspecificos();
	private Concurso concurso;
	private Date fecha;
	private Lista listaPublicada;
	private Integer PorcentajeMinimo;
	private String codConcurso;
	private String observacion;
	private String direccionFisica;
	private String horaDesde;
	private String horaHasta;
	private String horatrm;
	private String lugar;
	private Long idDpto;
	private Long idCiudad;
	private Long idConcursoGrupoPuestoAgr;
	private String direccion;
	private String obs;
	private Boolean estaPublicado;
	private String tipoMT;
	private String conCedula;
	private List<EvalDocumentalCab> listaEvalDocumentalCab;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private Lista listaEditada = null;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Long idEvalDocumentalCab;
	private EvalDocumentalCab evalDocumentalCab;
	private List <TachasReclamosModificaciones> tachasReclamosModificacionesList;
	private TachasReclamosModificaciones trmNuevo;
	private Date fechaTrm;
	private List <TachasReclamosModificaciones> listaTRM;
	private Long idDatosEspecificosVer;
	
	
	

		//Documentos Adjuntos
		private byte[] uploadedFile;
		private String contentType;
		private String fileName;
		private Integer tamArchivo;
		
		private byte[] uploadedFileComision;
		private String contentTypeComision;
		private String fileNameComision;
		private Integer tamArchivoComision;
		
		
	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}

		seguridadUtilFormController.verificarPerteneceOee(
				null,
				concursoPuestoAgr.getIdConcursoPuestoAgr(),
				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO","TACHAS_RECLAMOS_MODIF") + "",
				ActividadEnum.TACHAS_RECLAMOS_MODIF.getValor());

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
		idDatosEspecificosVer = new Long(1);
		//estaPublicado = verificarPublicacion();
		this.esMeritoOTerna();
		if (concurso != null) {
			validarOee();
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			datosEspecificosTipoConcurso = concurso.getDatosEspecificosTipoConc();
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
				+ configuracionUoCab.getIdConfiguracionUo()
				+ separador
				+ datosEspecificosTipoConcurso.getIdDatosEspecificos() + separador
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
				codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
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
	
	
	public void initAgregarTRM() throws Exception {
		
		
		tachasReclamosModificacionesList = ObtenerListaTRM(idEvalDocumentalCab);
		
		evalDocumentalCab = em.find(EvalDocumentalCab.class, idEvalDocumentalCab);
		
		this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoGrupoPuestoAgr);
		
		this.concurso = concursoPuestoAgr.getConcurso();
		 
		trmNuevo = new TachasReclamosModificaciones();
		

	}
	
	public List <TachasReclamosModificaciones> ObtenerListaTRM(Long idEvalDocumentalCab){
		
		String sql = "select * from seleccion.tachas_reclamos_modificaciones where activo = true and id_eval_documental_cab = " + idEvalDocumentalCab;
		
		List <TachasReclamosModificaciones> lista = em.createNativeQuery(sql,TachasReclamosModificaciones.class).getResultList();
		
		if(lista.isEmpty())
			return null;
		else 
			return lista;
		
	}
	
	public String formatFecha(Date fecha){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(fecha);
		
	}
	
	
	
	
	public void imprimirDocumento(Long id) {
		
		
			try{
			
		
			String usuario = "Invitado";
			
			if (usuarioLogueado != null )
				usuario = usuarioLogueado.getCodigoUsuario();
			
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,usuario);
			
			}catch(Exception e){
				e.printStackTrace();
				
			}
		
	}
	
	public String volverTRM() {
		
		
		String retorno  =  "/seleccion/tachasReclamosModif/tachasReclamosModifList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ this.idConcursoGrupoPuestoAgr;
			
		
		return retorno;
	}
	
	public String volverTRMCIO() {
		
		
		String retorno  =  "/circuitoCIO/tachasReclamosModif/tachasReclamosModifList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ this.idConcursoGrupoPuestoAgr;
			
		
		return retorno;
	}
	
	public void btnAgregarTRM() throws Exception{
		
		try {
			TachasReclamosModificaciones trm = this.trmNuevo;
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			
			trm.setFechaTrm(sdf.parse(sdfFecha.format(fechaTrm) + " " + sdfHora.format(sdfHora.parse(horatrm))));
			Long id;
			
			if(fileName != null && !fileName.equals("")){
				id = this.guardarDocumento(fileName, tamArchivo, contentType, uploadedFile, "NOTA_POSTULANTE");
				
				if(id == null ){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
					"No se pudo guardar Documento Postulante...");
					throw new Exception("No se pudo guardar Documento Postulante...");
								
				}
				
				trm.setDocumentoPostulante(em.find(Documentos.class, id));
			}
						
			if(fileNameComision != null && !fileNameComision.equals("")){
				id = this.guardarDocumento(fileNameComision, tamArchivoComision, contentTypeComision, uploadedFileComision, "NOTA_COMISION");
				
				if(id == null ){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
					"No se pudo guardar Documento Comision...");
					throw new Exception("No se pudo guardar Documento Comision...");
								
				}
			
			
				trm.setDocumentoComision(em.find(Documentos.class, id));
			}else{
				if(trmNuevo.getModificaEvalDocumental()){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
					"Para Modificar la Evaluación Documental, debe agregar un Documento de Comisión");
					throw new Exception("Para Modificar la Evaluación Documental, debe agregar un Documento de Comisión");
				}
				
			}
			
			if(trmNuevo.getIdTachasReclamosModificaciones() == null){
				trm.setActivo(true);
				trm.setFechaAlta(new Date());
				trm.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				trm.setEvalDocumentalCab(evalDocumentalCab);
				em.persist(trmNuevo);
				
			}
			else{
				trmNuevo.setUsuMod(usuarioLogueado.getCodigoUsuario());
				trmNuevo.setFechaMod(new Date());
				em.merge(trmNuevo);
			}
			
			em.flush();
			this.initAgregarTRM();
			
		} catch (Exception e) {
			
			statusMessages.clear();
			statusMessages.add(Severity.WARN,
			"Error al Agregar : "+e.getMessage());
		}
	}
	
	private EvalReferencial ObtenerEvalReferencial(Long idPostulacion, Long idDatosEspecificosTipoEval){
		String sql = " select * from seleccion.eval_referencial where id_postulacion = " +idPostulacion
				+ "	and id_eval_referencial_tipoeval = "
				+ " (select id_eval_referencial_tipoeval from seleccion.eval_referencial_tipoeval where id_datos_especificos_tipo_eval ="+idDatosEspecificosTipoEval 
				+ " and id_concurso_puesto_agr = "
				+ " (select id_concurso_puesto_agr from seleccion.postulacion where id_postulacion = "+ idPostulacion+"))";
		
		EvalReferencial evalRef;
		List <EvalReferencial> listaEvalRef = em.createNativeQuery(sql,EvalReferencial.class).getResultList(); 
		if(listaEvalRef.size()  > 0)
			evalRef = listaEvalRef.get(0);
		else
			evalRef = null;
		
		return evalRef;
	}
	
	private EvalReferencialPostulante ObtenerEvalReferencialPostulante(Long idPostulacion ){
		String sql = "select * from seleccion.eval_referencial_postulante where id_postulacion = "+idPostulacion;
		EvalReferencialPostulante evalRefPos;
		List <EvalReferencialPostulante> listaEvalRefPos = em.createNativeQuery(sql,EvalReferencialPostulante.class).getResultList(); 
		if(listaEvalRefPos.size()  > 0)
			evalRefPos = listaEvalRefPos.get(0);
		else
			evalRefPos = null;
		
		return evalRefPos;
	}
	
	
	
	private Long guardarDocumento(String fileName, int tamArchivo,String contentType, byte[] uploadedFile, String tipoDocumento) {
		try {
			
			String sql = "select * from seleccion.datos_especificos where valor_alf = '" + tipoDocumento + "'";
			
			 List <DatosEspecificos> list = em.createNativeQuery(sql,DatosEspecificos.class).getResultList();
			 
			 Long idTipoDoc = list.get(0).getIdDatosEspecificos();
			
			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(fileName, tamArchivo, contentType, uploadedFile);
			Long idDocuGenerado;
			
			String nombreTabla = "TachasReclamosModificaciones";
			
			String direccionFisica = File.separator
					+ "TACHAS_RECLAMOS_MODIFICACIONES" + File.separator +idConcursoGrupoPuestoAgr + File.separator + this.evalDocumentalCab.getPostulacion().getUsuPostulacion();
			 idDocuGenerado = admDocAdjuntoFormController.guardarDocParaPublicacion(fileItem,direccionFisica, "AgregarTRM", idTipoDoc,usuarioLogueado.getCodigoUsuario() ,nombreTabla,concurso.getIdConcurso(),idConcursoGrupoPuestoAgr);
			
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<SelectItem> seleccionModificaEvalDocumentalValueItems() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(true, "SI"));
		listaItem.add(new SelectItem(false, "NO"));
		return listaItem;
	}
	
	
	public int obtenerCantidadTRM(Long idEvalDocumentalCab){
		
		listaTRM = ObtenerListaTRM(idEvalDocumentalCab);
		if(listaTRM != null)
			return listaTRM.size();
		else 
			return 0;
	}
	
	public boolean permitirModificarEvalDocumental(List <TachasReclamosModificaciones> lista){
		boolean retorno = false;
						
			if(lista != null && lista.size() > 0){
				for (TachasReclamosModificaciones trm : lista){
					if(trm.getModificaEvalDocumental() != null && trm.getModificaEvalDocumental()){
						retorno = true;
						return retorno;
					}
						
				}
			}
		
		return retorno;
	}
	
	
	public boolean permitirModificarEvaluaciones(List <TachasReclamosModificaciones> lista){
		boolean retorno = false;
					
			if(lista != null && lista.size() > 0){
				
				if(esAdmitido (lista.get(0).getEvalDocumentalCab().getPostulacion().getIdPostulacion())){
					
					for (TachasReclamosModificaciones trm : lista){
						
							if(trm.getModificaEvaluaciones() != null && trm.getModificaEvaluaciones()){
								if(trm.getModificaEvalDocumental() == null || (trm.getModificaEvalDocumental() != null && !trm.getModificaEvalDocumental())){
									retorno = true;
								}else {
									if(this.evalDocumentalModificada(this.idConcursoGrupoPuestoAgr)){
										retorno = true;
										
									}else{
										retorno = false;
										return retorno;
									}
								}
							}else {
								if(retorno == true){
									if(trm.getModificaEvalDocumental() == null || (trm.getModificaEvalDocumental() != null && !trm.getModificaEvalDocumental())){
										retorno = true;
									}else {
										if(this.evalDocumentalModificada(this.idConcursoGrupoPuestoAgr)){
											retorno = true;
											
										}else{
											retorno = false;
											return retorno;
										}
									}
								}
							}
					
					}
				}	
			}
		return retorno;
	}
	
	
	private Boolean esAdmitido(Long idPostulacion){
		Boolean retorno = false;
		String sql = "select count (*) from seleccion.eval_documental_cab where fecha_alta = (select max(fecha_alta) from seleccion.eval_documental_cab where id_postulacion ="
				+ +idPostulacion +") and aprobado = true"
				+ " and activo = true and id_postulacion = "+idPostulacion;
			
		int cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad > 0)
			retorno = true;
		
		
		return retorno;
	}
	
	public boolean evalDocumentalModificado (Long idPostulacion){
		boolean retorno = false;
		
		String sql = "select count (*) from seleccion.eval_documental_cab where activo = true and id_postulacion = "+idPostulacion;
		
		int cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad > 1)
			retorno = true;
		
		return retorno;
	}
	
	public boolean evaluacionesModificado (Long idPostulacion){
		boolean retorno = false;
		
		String sql = "select count (*) from seleccion.eval_referencial_postulante where  activo = true and usu_alta = 'SYSTEM' and id_postulacion = "+idPostulacion;
		int cantidad = 0;
		cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad >= 1)
			retorno = true;
		
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public void esMeritoOTerna() {
		this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoGrupoPuestoAgr);
		
		String sql = "SELECT datos_gr.* "
				+ "FROM seleccion.datos_grupo_puesto datos_gr "
				+ "WHERE datos_gr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ "AND datos_gr.activo = TRUE";
		List<DatosGrupoPuesto> listaDatos = new ArrayList<DatosGrupoPuesto>();
		listaDatos = em.createNativeQuery(sql, DatosGrupoPuesto.class)
				.getResultList();
		for (DatosGrupoPuesto l : listaDatos) {
			if(l.getPorcMinimo() != null)
				this.PorcentajeMinimo = l.getPorcMinimo();
			if (l.getMerito() && !l.getTerna()) {
				tipoMT = "M";
				
			}
			if (!l.getMerito() && l.getTerna()) {
				tipoMT = "T";
				
			}
			
		}
		
		
	}
	
	public void imprimirListaCortaTerna() throws Exception{
		try {
			
			this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoGrupoPuestoAgr);
			concurso = em.find(Concurso.class,concursoPuestoAgr.getConcurso().getIdConcurso());
			configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
			sinEntidad = em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
			
			
			String sqlFuncion = "select seleccion.recalcularListaCorta("+ idConcursoGrupoPuestoAgr +")";
			String retornoFuncion = (String) em.createNativeQuery(sqlFuncion).getSingleResult();
			
			if (retornoFuncion.equals("error")){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Función recalcularListaCorta ejecutada con errores ");
				
			}
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("idConcursoPuestoAgr",this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
			param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
			HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
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
			if(usuarioLogueado != null )
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			else
				param.put("usuario", "Visitante Portal");
			
			
			
			if(conCedula != null){
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaCortaTernaTRM",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaCortaTerna_cedulaTRM",	param, false, conn,usuarioLogueado);
			}else
				
				conCedula = esConCedula(this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaCortaTernaTRM",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaCortaTerna_cedulaTRM",	param, false, conn,usuarioLogueado);
				//JasperReportUtils.respondPDF("listaCortaTerna_cedula",	param, false, conn,usuarioLogueado);
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void imprimirListaFinalMerito() throws Exception{
		try {
			this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoGrupoPuestoAgr);
			concurso = em.find(Concurso.class,concursoPuestoAgr.getConcurso().getIdConcurso());
			configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
			sinEntidad = em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		
			String sqlFuncion = "select seleccion.recalcularListaCorta("+ idConcursoGrupoPuestoAgr +")";
			String retornoFuncion = (String) em.createNativeQuery(sqlFuncion).getSingleResult();
			
			if (retornoFuncion.equals("error")){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Función recalcularListaCorta ejecutada con errores ");
				
			}
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("idConcursoPuestoAgr",this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
			param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());
			HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
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
			if(usuarioLogueado != null )
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			else
				param.put("usuario", "Visitante Portal");
			
			
			
			
			if(conCedula != null){
				
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaFinalMeritoTRM",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaFinalMerito_cedulaTRM",	param, false, conn,usuarioLogueado);
			
			}else 
				conCedula = esConCedula(this.concursoPuestoAgr.getIdConcursoPuestoAgr());
			
				if (conCedula.equals("N"))				
					JasperReportUtils.respondPDF("listaFinalMeritoTRM",	param, false, conn,usuarioLogueado);
				else if (conCedula.equals("S"))
					JasperReportUtils.respondPDF("listaFinalMerito_cedulaTRM",	param, false, conn,usuarioLogueado);
				
				//JasperReportUtils.respondPDF("listaFinalMerito_cedula",	param, false, conn,usuarioLogueado);
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public void imprimirEvaluacionesDetalladas() throws Exception {

		this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoGrupoPuestoAgr);
		concurso = em.find(Concurso.class,concursoPuestoAgr.getConcurso().getIdConcurso());
		configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		sinEntidad = em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
		nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("concurso_puesto_agr",this.idConcursoGrupoPuestoAgr);
		param.put("nivel",nivelEntidad.getNenCodigo() + " - "+ nivelEntidad.getNenNombre());
		param.put("entidad",sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "+ configuracionUoCab.getDenominacionUnidad());

		HistorialActividadesGrupo historial = obtenerFechaActividad(this.concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
		if (historial==null || historial.getFechaFin()==null)
			param.put("fecha", "Aun no se ha publicado");
		else{
			Format formato = new SimpleDateFormat("dd-MM-yyyy");
			param.put("fecha", formato.format(historial.getFechaFin()));
		}

		JasperReportUtils.respondPDF("ListaEvaluacionesRealizadasPlanillaTRM",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}


		//Busca si el reporte seleccionado para la publicacion fue con Cedula o Sin Cedula
		public String esConCedula(Long idConcursoPuestoAgr){
			String sql = "select * from seleccion.lista where tipo like 'LISTA CORTA' and id_concurso_puesto_agr = "+idConcursoPuestoAgr;
			
			List<Lista> lista = em.createNativeQuery(sql,Lista.class).getResultList();
			
			if (lista.size() == 0)
				return "S";
			else if (lista.get(0).getConCedula() == null)
				
					return "S";
				else if (lista.get(0).getConCedula())
					return "S";
				else 
					return "N";
					
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
				+ "where eval_cab.aprobado is not null  "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and postulacion.activo is true " 
				+ " and eval_cab.fecha_alta = (select min(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
				+ "order by pp.usu_alta";

		listaEvalDocumentalCab = em.createNativeQuery(sql,
				EvalDocumentalCab.class).getResultList();
	}

	/**
	 * Este metodo es llamado desde el Link "Lista Larga de Admitidos"
	 * @throws Exception 
	 */
	public void print2() throws Exception {
		init2();
		print();
	}
	
	/**
	 * Este metodo es llamado desde el Link "Lista de NO Admitidos"
	 * @throws Exception 
	 */
	public void imprimirListaNoAdmitidos() throws Exception {
		
		printListaNoAdmitidos();
	}
	
	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void printListaNoAdmitidos() {
		
		this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoGrupoPuestoAgr);
		concurso = em.find(Concurso.class,concursoPuestoAgr.getConcurso().getIdConcurso());
		configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		sinEntidad = em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
		nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		if(concurso.getNumero() == null && concurso.getAnhio() == null)
		{
			codConcurso = "";
		}
		else{
		codConcurso = concurso.getNumero() + "/" + concurso.getAnhio() + " DE "
				+ configuracionUoCab.getDescripcionCorta();;
		}
		
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
		param.put("concurso", codConcurso + " - " + concursoPuestoAgr.getConcurso().getNombre());
		param.put(
				"nivel",
				nivelEntidad.getNenCodigo() + " - "
						+ nivelEntidad.getNenNombre());
		param.put("entidad",
				sinEntidad.getEntCodigo() + " - " + sinEntidad.getEntNomabre());
		param.put("oee", configuracionUoCab.getOrden() + " - "
				+ configuracionUoCab.getDenominacionUnidad());
		param.put("grupo", concursoPuestoAgr.getDescripcionGrupo());
		param.put("rectificado", " (Rectificado)");
		
		
		HistorialActividadesGrupo historial = obtenerFechaActividad(idConcursoGrupoPuestoAgr, ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
		if (historial==null || historial.getFechaFin()==null)
			param.put("fecha", "Aun no se ha publicado");
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
	 * @throws Exception 
	 */
	public void print() throws Exception {
		
		this.concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoGrupoPuestoAgr);
		concurso = em.find(Concurso.class,concursoPuestoAgr.getConcurso().getIdConcurso());
		configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		if(concurso.getNumero() == null && concurso.getAnhio() == null)
		{
			codConcurso = "";
		}
		else{
		codConcurso = concurso.getNumero() + "/" + concurso.getAnhio() + " DE "
				+ configuracionUoCab.getDescripcionCorta();;
		}
		
		sinEntidad = em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
		nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		
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
		param.put("concurso", codConcurso + " - " +concursoPuestoAgr.getConcurso().getNombre());
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
		
		param.put("rectificado", " (Rectificado)");
//		List<Object[]> lista = consulta();
		List<Object[]> lista = consulta_aux();

		HistorialActividadesGrupo historial = obtenerFechaActividad(idConcursoGrupoPuestoAgr, ActividadEnum.TACHAS_RECLAMOS_MODIF.getDescripcion());
		if (historial==null || historial.getFechaFin()==null)
			param.put("fecha", "Aun no se ha publicado");
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
	
private boolean validarTipoConcurso() {
		
		Referencias ref = new Referencias();
		Referencias ref2 = new Referencias();
		//Referencias ref3 = new Referencias();
		Referencias ref4 = new Referencias();
		Referencias ref5 = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_LARGA");
		ref2 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_CORTA");
		/*ref3 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_SELECCIONADO");*/
		ref4 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_TERNA_FINAL");
		ref5 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_ADJUDICADOS");
	
		if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref2.getValorNum()) || /*concursoPuestoAgr.getEstado().equals(ref3.getValorNum()) ||*/ concursoPuestoAgr.getEstado().equals(ref4.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref5.getValorNum()))
		return true;
		else
		return false;
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
		String sql = "select per_post.usu_alta as cod, eval_cab.id_eval_documental_cab as id "
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
				+ " and eval_cab.activo is true "
				+ " and eval_cab.fecha_alta = (select max(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
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
	private List<Object[]> consulta_NO_Admitidos() {
		String sql = "select per_post.usu_alta, eval_cab.id_eval_documental_cab as id, eval_cab.observacion"
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
				+ " and eval_cab.activo is true "
				+ " and eval_cab.fecha_alta = (select max(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = postulacion.id_postulacion) "
				+ " and agr.id_concurso_puesto_agr = "
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
		
		HistorialActividadesGrupo historial;		
		
		
		if (concursoPuestoAgr.getIdConcursoPuestoAgr() != null){
			
			
			
			
			boolean evalDocumentalModificado = this.evalDocumentalModificada(concursoPuestoAgr.getIdConcursoPuestoAgr());
			boolean evaluacionesModificadas = this.evaluacionesModificadas(concursoPuestoAgr.getIdConcursoPuestoAgr());
			
			
	
			if(evalDocumentalModificado){
				
				texto = hr + fechaPublicacion + texto + br + spanO;
				
				texto = texto
						+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_86Rectificado&#38;idConcursoPuestoAgr="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr()
						+ "'>Lista Larga de Admitidos (Rectificado)</a> ";
				
				texto = texto + br;
						
				texto = texto
						+ "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=ListaNoAdmitidosRectificado&#38;idConcursoPuestoAgr="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr()
						+ "'>Lista de No Admitidos (Rectificado)</a>";
			}
			
			
			
			// si cambio la lista corta y las evaluaciones detalladas
			if(evaluacionesModificadas){
				if(texto.isEmpty())
					texto = hr + fechaPublicacion + texto + br + spanO;
				else
					texto = texto + br;
				
				texto = texto + "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_87Rectificado&#38;idConcursoPuestoAgr="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr()
						+ "'>Lista Corta de Pre-Seleccionados (Rectificado)</a>";
				
				texto = texto + br;
				
				texto = texto + "<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=EvalDetRectificado&#38;idConcursoPuestoAgr="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr()
						+ "'>Evaluaciones Detalladas (Rectificado)</a>";
				
				texto = texto + spanC;
			}
		
				
		}
		
			
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
		if(!textoHtml.isEmpty()){
			em.persist(entity);
			em.flush();
		}
			
	}
	
	
	public boolean evalDocumentalModificada(Long idConcursoPuestoAgr){
		boolean retorno = false;
		String sql = "select count(*) from seleccion.eval_documental_cab where usu_alta = 'SYSTEM' "
				+ "and id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "
				+ idConcursoPuestoAgr +")";
		
		int cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad > 0)
			retorno = true;
		return retorno;
	
	}

	
	public boolean evaluacionesModificadas(Long idConcursoPuestoAgr){
		boolean retorno = false;
		
		String sql = "select count(*) from seleccion.eval_referencial_postulante where usu_alta = 'SYSTEM' "
				+ "and id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "
				+ idConcursoPuestoAgr +")";
		
		int cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad > 0)
			retorno = true;
		
		return retorno;
	}


	public String SiguienteTarea() {
		
		try {
			
			 
			String sqlRecalcularListaCorta = "select seleccion.recalcularListaCorta("+concursoPuestoAgr.getIdConcursoPuestoAgr()+")";
			String retornoFuncion = (String) em.createNativeQuery(sqlRecalcularListaCorta).getSingleResult();
			if (retornoFuncion.equals("error")){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Función recalcularListaCorta ejecutada con errores ");
				
			}
			
			if(tareaCerrada())
			{
				statusMessages.add(Severity.INFO,
						"El Grupo ya se encuentra en la Siguiente Actividad. Verifique.");
				//this.continuarSgteTarea = false;
				return "nextTask";
			}
			
//			if(terminoPeriodoTRM()){
				insertarTablaPublicacion();	
				//buscarListaLargaPostulantes();
				if (this.evalDocumentalModificada(this.concursoPuestoAgr.getIdConcursoPuestoAgr())  || this.evaluacionesModificadas(this.concursoPuestoAgr.getIdConcursoPuestoAgr())){
					for (EvalDocumentalCab listEval : listaEvalDocumentalCab) {
						
						enviarEmails(listEval.getPostulacion()
								.getPersonaPostulante());
					}
				}
				
		
				
				return nextTask();
//			}else{
//				statusMessages.clear();
//				statusMessages.add(Severity.ERROR,
//				"Debe esperar a que acabe el periodo de Tachas, Reclamos y Modificaciones... ");
//				return null;
//				
//			}
				
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
public boolean tareaCerrada(){
		
		Referencias ref = new Referencias();
		
		if (tipoMT.equals("T")) {
			ref = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "ENTREVISTA MAI");
		}
		
		if (tipoMT.equals("M")) {
			ref = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "EVALUACION DOCUMENTAL ADJ");
		}
		
		ConcursoPuestoAgr aux = em.find(ConcursoPuestoAgr.class, concursoPuestoAgr.getIdConcursoPuestoAgr());
		
		em.refresh(aux);
		
		if (aux.getEstado().equals(ref.getValorNum()))
			return true;
		else
			return false;
		
	}
	private Boolean terminoPeriodoTRM(){
		Boolean retorno = false;
		
		String sql = "select * from proceso.actividad_proceso a_p join proceso.actividad a on a_p.id_actividad = a.id_actividad"
				+ " where a.cod_actividad = '"+ActividadEnum.TACHAS_RECLAMOS_MODIF.getValor()+"'";
		
		ActividadProceso ap =  (ActividadProceso) em.createNativeQuery(sql,ActividadProceso.class).getSingleResult();
		
		HistorialActividadesGrupo historial = obtenerFechaActividad(idConcursoGrupoPuestoAgr, ActividadEnum.ELABORAR_PUBLICACION_LISTA_CORTA.getDescripcion());
		
		if(ap != null && historial != null ){
			Calendar cal = new GregorianCalendar();
			cal.setTime(historial.getFechaFin());
			cal.add(Calendar.DAY_OF_YEAR, ap.getPlazoActividad().intValue()); 
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			if(fechaHabilOee(cal.getTime()).before(new Date()))
				retorno = true;
			
		}
		
		
		return retorno;
	}
	
	private Date fechaHabilOee(Date laFecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Integer tope = 365;
		Integer cuenta = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(laFecha);
		Boolean stop = false;

		while (cuenta.intValue() < tope.intValue() && !stop) {
			stop = seleccionUtilFormController.fechaTrabajaOee(cal.getTime(),this.configuracionUoCab.getIdConfiguracionUo());
			
			if (stop) {
				break;
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cuenta++;
		}
		if (cuenta.intValue() == tope.intValue() && !stop) {
			return null;
		}
		
		return cal.getTime();

	}

	private Boolean verificarPublicacion() {
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"LISTA LARGA");
		if (ref != null) {
			if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()))
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
			Referencias ref = new Referencias();
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.TACHAS_RECLAMOS_MODIF);
			if (tipoMT.equals("T")) {
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.REALIZAR_ENTREVISTA_FINAL);
				jbpmUtilFormController.setTransition("next1");
				
				
				ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
						"ENTREVISTA MAI");
				if (ref != null) {
					this.concursoPuestoAgr.setEstado(ref.getValorNum());
					em.merge(this.concursoPuestoAgr);
				}
				
				
			} else if (tipoMT.equals("M")) {
				
				
				 if(concursoPuestoAgr.getConcurso().getPromocion() != null && concursoPuestoAgr.getConcurso().getPromocion()){
					 jbpmUtilFormController.setActividadSiguiente(ActividadEnum.ASIGNAR_PROMOCION_SALARIAL);
					 ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
							 "ADJUDICADO");
				 }else{
					 jbpmUtilFormController.setActividadSiguiente(ActividadEnum.VALIDAR_MATRIZ_DOCUMENTAL_ADJ);
				 	 ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
							"EVALUACION DOCUMENTAL ADJ");
				 }
					
					 
				jbpmUtilFormController.setTransition("next2");
				
				
				
				if (ref != null) {
					this.concursoPuestoAgr.setEstado(ref.getValorNum());
					em.merge(this.concursoPuestoAgr);
				}
			}
			
//			jbpmUtilFormController
//					.setActividadSiguiente(ActividadEnum.REALIZAR_EVALUACIONES);
//			jbpmUtilFormController.setTransition("next");

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
		String asunto = null;
		asunto = "Listas Rectificadas";
		String texto = "";
		
		try {
			
			texto="<p> <b> Estimado(a) "+persona.getNombres()+" "+persona.getApellidos()+",</b></p>"
					+ "<p> Le comunicamos que han sido publicadas las listas Rectificadas en el Concurso: <b>"+concurso.getNumero()+"/"+concurso.getAnhio()+" de "
					+configuracionUoCab.getDescripcionCorta()+ " - "+concurso.getNombre()+", "+concursoPuestoAgr.getDescripcionGrupo()+".</b></p>";
			
					
			texto += "<p><b> Atentamente,<br/> "+configuracionUoCab.getDenominacionUnidad()+".</b></p>";
					
			if(!informacion().equals(""))
					texto += "<p>Para mayor información comunicarse con "+informacion()+"</p>";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,null);
			
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

	public Long getIdEvalDocumentalCab() {
		return idEvalDocumentalCab;
	}

	public void setIdEvalDocumentalCab(Long idEvalDocumentalCab) {
		this.idEvalDocumentalCab = idEvalDocumentalCab;
	}

	public List<TachasReclamosModificaciones> getTachasReclamosModificacionesList() {
		return tachasReclamosModificacionesList;
	}

	public void setTachasReclamosModificacionesList(
			List<TachasReclamosModificaciones> tachasReclamosModificacionesList) {
		this.tachasReclamosModificacionesList = tachasReclamosModificacionesList;
	}

	public TachasReclamosModificaciones getTrmNuevo() {
		return trmNuevo;
	}

	public void setTrmNuevo(TachasReclamosModificaciones trmNuevo) {
		this.trmNuevo = trmNuevo;
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

	public Integer getTamArchivo() {
		return tamArchivo;
	}

	public void setTamArchivo(Integer tamArchivo) {
		this.tamArchivo = tamArchivo;
	}

	public byte[] getUploadedFileComision() {
		return uploadedFileComision;
	}

	public void setUploadedFileComision(byte[] uploadedFileComision) {
		this.uploadedFileComision = uploadedFileComision;
	}

	public String getContentTypeComision() {
		return contentTypeComision;
	}

	public void setContentTypeComision(String contentTypeComision) {
		this.contentTypeComision = contentTypeComision;
	}

	public String getFileNameComision() {
		return fileNameComision;
	}

	public void setFileNameComision(String fileNameComision) {
		this.fileNameComision = fileNameComision;
	}

	public Integer getTamArchivoComision() {
		return tamArchivoComision;
	}

	public void setTamArchivoComision(Integer tamArchivoComision) {
		this.tamArchivoComision = tamArchivoComision;
	}

	public EvalDocumentalCab getEvalDocumentalCab() {
		return evalDocumentalCab;
	}

	public void setEvalDocumentalCab(EvalDocumentalCab evalDocumentalCab) {
		this.evalDocumentalCab = evalDocumentalCab;
	}

	public Date getFechaTrm() {
		return fechaTrm;
	}

	public void setFechaTrm(Date fechaTrm) {
		this.fechaTrm = fechaTrm;
	}

	public String getHoratrm() {
		return horatrm;
	}

	public void setHoratrm(String horatrm) {
		this.horatrm = horatrm;
	}

	public List<TachasReclamosModificaciones> getListaTRM() {
		return listaTRM;
	}

	public void setListaTRM(List<TachasReclamosModificaciones> listaTRM) {
		this.listaTRM = listaTRM;
	}

	public Long getIdDatosEspecificosVer() {
		return idDatosEspecificosVer;
	}

	public void setIdDatosEspecificosVer(Long idDatosEspecificosVer) {
		this.idDatosEspecificosVer = idDatosEspecificosVer;
	}

	public String getTipoMT() {
		return tipoMT;
	}

	public void setTipoMT(String tipoMT) {
		this.tipoMT = tipoMT;
	}

	public String getConCedula() {
		return conCedula;
	}

	public void setConCedula(String conCedula) {
		this.conCedula = conCedula;
	}

	
	
	

}
