package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.TipoEvaluacion;
import py.com.excelsis.sicca.dto.EvalDocumentalDetDTO;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalDocumentalComis;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;
import py.com.excelsis.sicca.entity.EvalReferencialComis;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigGrupos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.IntegrantesDTO;
import py.com.excelsis.sicca.util.JpaResourceBean;

import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Scope(ScopeType.CONVERSATION)
@Name("evaluacionDocumentosFormController")
public class EvaluacionDocumentosFormController implements Serializable {

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
	EvalDocumentalCabHome evalDocumentalCabHome;

	ConcursoPuestoAgr concursoPuestoAgr;
	
	EvalDocumentalCab evalDocumentalCab;
	
	
	
	private TipoEvaluacion tipoEvaluacion;
	private ComisionSeleccionCab comisionSeleccionCab;
	private Concurso concurso;
	private String observacion;
	private String comision;
	private Long idIntegranteComision;
	private Long idIntegranteEmpresa;
	private Long idEmpresaTercerizada;
	private Boolean estaAgregado = false;
	private String direccionFisica;
	private String nombrePantalla;
	private String msg;
	private String msgSave;
	private String aprobo;
	private Boolean isEdit;
	private Long idDoc;
	private Long idTipoDoc;
	private Long cabeza;
	private String codActividad;
	private ReentrantLock lock = new ReentrantLock();
	private List<EvalDocumentalDet> listaEvalDocumentalDet;
	private List<EvalDocumentalDetDTO> listaEvalDocumentalDetDTO;
	private List<IntegrantesDTO> listaEvaluadoresDTO;
	private List<IntegrantesDTO> listaEvaluadoresDTOAux;
	private List<SelectItem> integrantesComisionSelecItem;
	private List<SelectItem> integrantesEmpresaSelecItem;
	private List<SelectItem> empresaTercerizadaSelecItem;
	private List<Documentos> listaDocAmostrar;
	private List<Documentos> listaDocumentosCompletos;
	private List<IntegrantesDTO> listaEvaluadoresDTOEliminar;
	private boolean habilitarVolver;
	private boolean habilitarGuardar;
	private Long idComisionSeleccion;
	List<Persona> lEvaluadores;
	private static final String PRE = locate();
	

	

	/**
	 * Incidencia 0000844
	 * **/
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	/**
	 * fin *
	 **/

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String from;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		FacesContext fc = FacesContext.getCurrentInstance();
		
		if(this.from != null && (this.from.contains("realizarEvals") || this.from.contains("evalPuntajePostulante"))) 
			isEdit = true;
		
		if(isEdit==null){
			isEdit=fc.getExternalContext().getRequestParameterMap().get("isEdit").equalsIgnoreCase("true");
		}

		//Bloqueamos con lock() 
		lock.lock();
		
		listaEvaluadoresDTOEliminar = new ArrayList<IntegrantesDTO>();
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();

		evalDocumentalCab = new EvalDocumentalCab();
		evalDocumentalCab = evalDocumentalCabHome.getInstance();
		Long idEvalDocumentalCab = new Long(fc.getExternalContext().getRequestParameterMap().get("evalDocumentalCabIdEvalDocumentalCab"));
		evalDocumentalCab = em.find(EvalDocumentalCab.class, idEvalDocumentalCab);
		Long idConcursoPuestoAgr = new Long(fc.getExternalContext().getRequestParameterMap().get("concursoPuestoAgrIdConcursoPuestoAgr"));
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);

		if (!isEdit) {
			habilitarVolver = true;
			habilitarGuardar = false;
		} else {
			if (evalDocumentalCab.getIncluido() == null	|| !evalDocumentalCab.getIncluido()) {
				// INDICA QUE YA HA SIDO SELECCIONADO POR OTRO EVALUADOR
				evalDocumentalCab.setIncluido(true);
				evalDocumentalCab.setUsuMod(this.usuarioLogueado
						.getCodigoUsuario());
				evalDocumentalCab.setFechaMod(new Date());
				em.merge(evalDocumentalCab);
				em.flush();
				habilitarGuardar = true;
				habilitarVolver = false;
				msg = null;

			} else {

				if (evalDocumentalCab.getUsuMod().equals(
						usuarioLogueado.getCodigoUsuario())) {
					em.merge(evalDocumentalCab);
					em.flush();
					habilitarGuardar = true;
					habilitarVolver = false;
					msg = null;
				} else {
					habilitarVolver = true;
					habilitarGuardar = false;
					statusMessages.clear();
					statusMessages.add(Severity.WARN,
							"El Postulante esta siendo evaluado por el Usuario: "
									+ evalDocumentalCab.getUsuMod());
				}

			}
		}	
		
			listaEvaluadoresDTO = new ArrayList<IntegrantesDTO>();
			if (evalDocumentalCabHome.isIdDefined()) {
				validarOee(evalDocumentalCab.getConcursoPuestoAgr().getConcurso());
				observacion = evalDocumentalCab.getObservacion();
				listaEvaluadoresDTOAux = new ArrayList<IntegrantesDTO>();
				recuperarListaEvaluadores();
				estaAgregado = true;
			}
			concurso = new Concurso();
			concurso = concursoPuestoAgr.getConcurso();
			validarOee(concurso);
			buscarDetalles();
			buscarComision();
			cargarComboIntegrantesComision();
			cargarComboEmpresaTercerizada();
			cargarComboIntegrantesEmpresa();
			cargarListaDocumentosCompletos();
			String valor_persona = evalDocumentalCab.getPostulacion()
					.getPersonaPostulante().getDocumentoIdentidad()
					+ "_";
			valor_persona = valor_persona
					+ evalDocumentalCab.getPostulacion().getPersonaPostulante()
							.getPersona().getIdPersona();
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
					+ concursoPuestoAgr.getIdConcursoPuestoAgr() + separador
					+ "POSTULANTES" + separador + valor_persona;
		
			agregarTodosLosEvaluadores();
		
		lock.unlock();
	}
	
	/**
	 * Método que inicializa los datos
	 */
	public void initTRM() {
		FacesContext fc = FacesContext.getCurrentInstance();
		
		
		isEdit = true;
		
		

		
		//Bloqueamos con lock() 
		lock.lock();
		
		listaEvaluadoresDTOEliminar = new ArrayList<IntegrantesDTO>();
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();

		evalDocumentalCab = new EvalDocumentalCab();
		//evalDocumentalCab = evalDocumentalCabHome.getInstance();
		Long idEvalDocumentalCab = new Long(fc.getExternalContext().getRequestParameterMap().get("evalDocumentalCabIdEvalDocumentalCab"));
		
		EvalDocumentalCab cabAux = em.find(EvalDocumentalCab.class, idEvalDocumentalCab);
		
		String sql = "select id_eval_documental_cab from seleccion.eval_documental_cab "
				+ " where fecha_alta = (select max(fecha_alta) from seleccion.eval_documental_cab where id_postulacion = "+cabAux.getPostulacion().getIdPostulacion()+" )"
				+ " and id_postulacion = "+cabAux.getPostulacion().getIdPostulacion();
		
		BigInteger idReplica  = (BigInteger) em.createNativeQuery(sql).getSingleResult();
		
		//si solo esta la evaluacion original debera ser replicada.		
		if(idReplica.longValue() == idEvalDocumentalCab.longValue() ){
			String sqlFuncion = "select seleccion.obtener_replica_eval_documental("+ idEvalDocumentalCab +")";
			idReplica = (BigInteger) em.createNativeQuery(sqlFuncion).getSingleResult();
		}
		
		evalDocumentalCab = em.find(EvalDocumentalCab.class,new Long (idReplica.longValue()));
		
		Long idConcursoPuestoAgr = new Long(fc.getExternalContext().getRequestParameterMap().get("concursoPuestoAgrIdConcursoPuestoAgr"));
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);

		if (!isEdit) {
			habilitarVolver = true;
			habilitarGuardar = false;
		} else {
			if (evalDocumentalCab.getIncluido() == null	|| !evalDocumentalCab.getIncluido()) {
				// INDICA QUE YA HA SIDO SELECCIONADO POR OTRO EVALUADOR
				
				
				habilitarGuardar = true;
				habilitarVolver = false;
				msg = null;

			} else {

				if (evalDocumentalCab.getUsuMod().equals(
						usuarioLogueado.getCodigoUsuario())) {
					em.merge(evalDocumentalCab);
					em.flush();
					habilitarGuardar = true;
					habilitarVolver = false;
					msg = null;
				} else {
					habilitarVolver = true;
					habilitarGuardar = false;
					statusMessages.clear();
					statusMessages.add(Severity.WARN,
							"El Postulante esta siendo evaluado por el Usuario: "
									+ evalDocumentalCab.getUsuMod());
				}

			}
		}	
		
			listaEvaluadoresDTO = new ArrayList<IntegrantesDTO>();
			if (evalDocumentalCabHome.isIdDefined()) {
				validarOee(evalDocumentalCab.getConcursoPuestoAgr().getConcurso());
				observacion = evalDocumentalCab.getObservacion();
				listaEvaluadoresDTOAux = new ArrayList<IntegrantesDTO>();
				recuperarListaEvaluadores();
				estaAgregado = true;
			}
			concurso = new Concurso();
			concurso = concursoPuestoAgr.getConcurso();
			validarOee(concurso);
			buscarDetalles();
			buscarComision();
			cargarComboIntegrantesComision();
			cargarComboEmpresaTercerizada();
			cargarComboIntegrantesEmpresa();
			cargarListaDocumentosCompletos();
			String valor_persona = evalDocumentalCab.getPostulacion()
					.getPersonaPostulante().getDocumentoIdentidad()
					+ "_";
			valor_persona = valor_persona
					+ evalDocumentalCab.getPostulacion().getPersonaPostulante()
							.getPersona().getIdPersona();
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
					+ concursoPuestoAgr.getIdConcursoPuestoAgr() + separador
					+ "POSTULANTES" + separador + valor_persona;
		
			agregarTodosLosEvaluadores();
		
		lock.unlock();
	}
	
	public void agregarTodosLosEvaluadores() {
		
		
		for (Persona persona : lEvaluadores) {
			IntegrantesDTO dto = new IntegrantesDTO();
			dto.setComision(comision);
			dto.setComisionSeleccionCab(comisionSeleccionCab);
			if (!existeComisionSel(comisionSeleccionCab.getIdComisionSel(),persona.getIdPersona())) {
				dto.setPersonaComision(persona);
				listaEvaluadoresDTO.add(dto);
				
			}
			
		}	
					
		
	}
	
	

	public String cancelarEvaluacion() {
		lock.lock();
		
		String retorno  =  "/seleccion/evalDocumentosPostulantes/EvalDocumentalCabList.xhtml?concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()+"&codActividad="+codActividad;
		
		evalDocumentalCab.setIncluido(false);
		em.merge(evalDocumentalCab);
		em.flush();
		
		lock.unlock();
		
		return retorno;
	}

	public String volver() {
		
		
		String retorno  =  "/seleccion/evalDocumentosPostulantes/EvalDocumentalCabList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + "&isEdit=" + isEdit;
			
		
		return retorno;
	}
	
	public String volverTRM() {
		
		
		String retorno  =  "/seleccion/tachasReclamosModif/tachasReclamosModifList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + "&isEdit=" + isEdit;
			
		
		return retorno;
	}
	
	public String volverTRMCIO() {
		
		
		String retorno  =  "/circuitoCIO/tachasReclamosModif/tachasReclamosModifList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + "&isEdit=" + isEdit;
			
		
		return retorno;
	}
	
	public void cancelar(){
		
	}

	public String aceptarEvaluacion() {
		try {
			evalDocumentalCab.setEvaluado(true);
			evalDocumentalCab.setIncluido(false);
			em.merge(evalDocumentalCab);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "/seleccion/evalDocumentosPostulantes/EvalDocumentalCabList.xhtml?concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()+"&codActividad="+codActividad;
	}
	
	
	public String aceptarEvaluacionTRM() {
		try {
			evalDocumentalCab.setEvaluado(true);
			evalDocumentalCab.setIncluido(false);
			em.merge(evalDocumentalCab);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}

		
		return "/seleccion/tachasReclamosModif/tachasReclamosModifList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()+"&codActividad="+codActividad;
	}
	
	public String aceptarEvaluacionTRMCIO() {
		try {
			evalDocumentalCab.setEvaluado(true);
			evalDocumentalCab.setIncluido(false);
			em.merge(evalDocumentalCab);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}

		
		return "/circuitoCIO/tachasReclamosModif/tachasReclamosModifList.seam?codActividad=EVALUACION_DOCUMENTAL&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()+"&codActividad="+codActividad;
	}
	
	
	@SuppressWarnings("unchecked")
	private void recuperarListaEvaluadores() {
		String sql = "select comis.* "
				+ "from seleccion.eval_documental_comis comis "
				+ "join seleccion.eval_documental_cab cab "
				+ "on cab.id_eval_documental_cab = comis.id_eval_documental_cab "
				+ " where cab.id_postulacion="
				+ evalDocumentalCab.getPostulacion().getIdPostulacion();
		List<EvalDocumentalComis> listaComis = new ArrayList<EvalDocumentalComis>();
		listaComis = em.createNativeQuery(sql, EvalDocumentalComis.class)
				.getResultList();

		for (EvalDocumentalComis c : listaComis) {
			IntegrantesDTO dto = new IntegrantesDTO();
			dto.setId(c.getIdEvalDocumentalComis());
			if (c.getComisionSeleccionDet() != null) {
				tipoEvaluacion = tipoEvaluacion.COMISION;
				dto.setComisionSeleccionCab(c.getComisionSeleccionDet()
						.getComisionSeleccionCab());
				dto.setPersonaComision(c.getComisionSeleccionDet().getPersona());
				dto.setComision(c.getComisionSeleccionDet()
						.getComisionSeleccionCab().getDescripcion());

			}
			if (c.getEmpresaPersona() != null) {
				tipoEvaluacion = tipoEvaluacion.EMPRESA;
				dto.setEmpresaPersona(c.getEmpresaPersona());
				dto.setPersonaEmpresa(c.getEmpresaPersona().getPersona());
				dto.setEmprTercerizada(c.getEmpresaPersona()
						.getEmprTercerizada());
			}
			listaEvaluadoresDTO.add(dto);
			listaEvaluadoresDTOAux.add(dto);
		}
	}

	/**
	 * llama al metodo abrir del cu 289
	 * 
	 * @param index
	 */
	public void abrirDoc(int index) {

		EvalDocumentalDetDTO e = listaEvalDocumentalDetDTO.get(index);
		Long id = e.getDocumentos().getIdDocumento();
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,
				usuarioLogueado.getIdUsuario());

	}
	
	/**
	 * llama al metodo abrir del cu 289
	 * 
	 * @param index
	 */
	public void abrirDocumento(int index) {

		
		Long id = listaDocAmostrar.get(index).getIdDocumento();
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,
				usuarioLogueado.getIdUsuario());

	}

	/**
	 * Busca los detalles de la cabecera
	 */
	@SuppressWarnings("unchecked")
	private void buscarDetalles() {
		String sql = "select documental_det.*  "
				+ " from seleccion.eval_documental_det documental_det  "
				+ " join seleccion.eval_documental_cab documental_cab  "
				+ " on documental_cab.id_eval_documental_cab = documental_det.id_eval_documental_cab  "
				+ " join seleccion.matriz_doc_config_det matriz_det "
				+ " on documental_det.id_matriz_doc_config_det = matriz_det.id_matriz_doc_config_det "
				+ " where documental_det.activo is true  "
				+ " and documental_cab.id_eval_documental_cab = "
				+ evalDocumentalCab.getIdEvalDocumentalCab() 
				+ " order by matriz_det.nro_orden";
		listaEvalDocumentalDet = new ArrayList<EvalDocumentalDet>();
		listaEvalDocumentalDet = em.createNativeQuery(sql,
				EvalDocumentalDet.class).getResultList();
		listaEvalDocumentalDetDTO = new ArrayList<EvalDocumentalDetDTO>();
		Integer cont = 1;
		for (EvalDocumentalDet det : listaEvalDocumentalDet) {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			dto.setAprobado(det.isAprobadoPorComision());
			dto.setEvalDocumentalDet(det);
			dto.setNro(cont);
			if(!det.getMatrizDocConfigDet().getAgrupado())
				dto.setObligatorio(det.getMatrizDocConfigDet().getObligatorio());
			else{
				MatrizDocConfigGrupos grupo = em.find(MatrizDocConfigGrupos.class, det.getMatrizDocConfigDet().getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos());
				dto.setObligatorio(grupo.getObligatorio());
			}
			
			dto.setPresento(det.isAprobadoConDocumentos());
			dto.setTipoDocumento(det.getMatrizDocConfigDet()
					.getDatosEspecificos());
			dto.setDocumentos(buscarDocumentos(det.getIdEvalDocumentalDet(),
					det.getMatrizDocConfigDet().getDatosEspecificos()
							.getIdDatosEspecificos()));
			listaEvalDocumentalDetDTO.add(dto);
			cont++;
		}
		
		
	}

	/**
	 * metodo que es llamado desde la busqueda de detalles
	 * 
	 * @param idDet
	 * @param idDato
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Documentos buscarDocumentos(Long idDet, Long idDato) {
		String sql = "select doc.* "
				+ "from general.documentos doc "
				+ "join seleccion.datos_especificos datos_esp "
				+ "on datos_esp.id_datos_especificos = doc.id_datos_especificos_tipo_documento "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_documental_cab eval_cab "
				+ "on eval_cab.id_postulacion = post.id_postulacion "
				+ "join seleccion.eval_documental_det eval_det "
				+ "on eval_det.id_eval_documental_cab = eval_cab.id_eval_documental_cab "
				+ "where eval_det.id_eval_documental_det = " + idDet
				+ " and datos_esp.id_datos_especificos = " + idDato
				+ " and doc.activo is true"
				+ " order by doc.descripcion ";
		List<Documentos> lista = new ArrayList<Documentos>();
		lista = em.createNativeQuery(sql, Documentos.class).getResultList();
		if (lista.size() > 0)
			return lista.get(lista.size() - 1);
		return null;
	}

	/**
	 * metodo que selecciona todos
	 * 
	 */
	public void marcarTodos() {
		for (int i = 0; i < listaEvalDocumentalDetDTO.size(); i++) {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			dto = listaEvalDocumentalDetDTO.get(i);
			dto.setAprobado(true);
			listaEvalDocumentalDetDTO.set(i, dto);
		}

	}

	/**
	 * metodo que desmarca todo
	 */
	public void desmarcarTodos() {
		for (int i = 0; i < listaEvalDocumentalDetDTO.size(); i++) {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			dto = listaEvalDocumentalDetDTO.get(i);
			dto.setAprobado(false);
			listaEvalDocumentalDetDTO.set(i, dto);
		}
	}
	
		
	public void generarZip(String tipo) throws FileNotFoundException, IOException{
		try {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			
			Documentos doc = new Documentos();
			int i = 0;
			while ( i <= listaEvalDocumentalDetDTO.size()){
				EvalDocumentalDetDTO aux=  listaEvalDocumentalDetDTO.get(i);	
				
				if(aux != null && aux.getDocumentos() != null && aux.getDocumentos().getIdDocumento() != null){
						dto = aux;
						i= listaEvalDocumentalDetDTO.size();
				}
				i++;
			}	
			doc = em.find(Documentos.class,dto.getDocumentos().getIdDocumento());
			
			String direccion = doc.getUbicacionFisica();
			
			String destino = PRE + File.separator	+ "zipGenerados" + File.separator + concursoPuestoAgr.getIdConcursoPuestoAgr()+ File.separator;
			
			File directorioCreado = new File(destino);
			
				if (!directorioCreado.exists())
					if (!directorioCreado.mkdirs())
						throw new IOException("No se pudo crear el directorio: "+ directorioCreado.getAbsolutePath());
			
			String url = null;
			if (tipo.equals("todos")){
				url =  zipDirectorio(direccion, directorioCreado.getAbsolutePath()+ File.separator+ this.evalDocumentalCab.getPostulacion().getUsuPostulacion());
			} else if(tipo.equals("matriz")) {
				url =  zipDirectorioMatriz(listaEvalDocumentalDetDTO, direccion, directorioCreado.getAbsolutePath()+ File.separator+ this.evalDocumentalCab.getPostulacion().getUsuPostulacion());
			}			
						
			File NuevoZip = new File(url);
			
			enviarZipArchivoANavegador(this.evalDocumentalCab.getPostulacion().getUsuPostulacion() + " - " + tipo, NuevoZip);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
		 
	private static String zipDirectorio(String dir, String destino) {
	// Revisa que el directorio sea direcorio y lee sus archivos.			
	        String direccion;
	        try {
	            File d = new File(dir);
	            if (!d.isDirectory()) {
	                throw new IllegalArgumentException(dir + " no es un directorio.");
	            }
	            String[] entries = d.list();
	            byte[] buffer = new byte[4096]; // Crea un buffer para copiar
	            int bytesRead;
	            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destino + ".zip"));
	            for (int i = 0; i < entries.length; i++) {
	                File f = new File(d, entries[i]);
	                if (f.isDirectory()) {
	                    continue; //Ignora el directorio
	                }
	                FileInputStream in = new FileInputStream(f);
	                
	                
	 
	                ZipEntry entry = new ZipEntry(f.getName()); //Crea una entrada zip para cada archivo,
	                out.putNextEntry(entry);
	 
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	                in.close();
	            }
	            
	            out.close();
	 
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return destino + ".zip";
	    }
	
	private static String zipDirectorioMatriz(List<EvalDocumentalDetDTO> listaDocs, String dir, String destino) {
		// Revisa que el directorio sea direcorio y lee sus archivos.		 
	        String direccion;
	
	        try {
	            File d = new File(dir);
	            if (!d.isDirectory()) {
	                throw new IllegalArgumentException(dir + " no es un directorio.");
	            }
	            String[] entries = d.list();
	            byte[] buffer = new byte[4096]; // Crea un buffer para copiar
	            int bytesRead;
	            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destino + ".zip"));
	            for (int i = 0; i < entries.length; i++) {
	                File f = new File(d, entries[i]);
	                if (f.isDirectory()) {
	                    continue; //Ignora el directorio
	                }
	                FileInputStream in = new FileInputStream(f);
	                
 	                //Crea una entrada zip para cada archivo que se encuentre en la matriz documental
	                for (int j = 0; j < listaDocs.size(); j++) {
	               
	                	if (listaDocs.get(j).getDocumentos().getNombreFisicoDoc().equals(f.getName())) {
	                		ZipEntry entry = new ZipEntry(f.getName()); 
	    	                out.putNextEntry(entry);
	                	}
	                	
					}
	                	                	 
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	                in.close();
	            }
	            
	            out.close();
	 
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return destino + ".zip";
	}
	
	
	
	
	private static String ponerBarraSimple(String direccion) {
		if (direccion.contains("\\")) {
			direccion = direccion.replace("\\",
					System.getProperty("file.separator"));

		}
		if (direccion.contains("//")) {
			direccion = direccion.replace("//",
					System.getProperty("file.separator"));

		}
		if (direccion.contains("/")) {
			direccion = direccion.replace("/",
					System.getProperty("file.separator"));

		}
		return direccion;
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
	
	public static void enviarZipArchivoANavegador(String nombreArchivoSugerido,File archivoABajar) throws FileNotFoundException, IOException {
			
			FileInputStream archivo = new FileInputStream(archivoABajar);

			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

			response.setContentType("application/zip");
			//response.setContentType("application/octet-stream");
			response.setContentLength(archivo.available());
			response.setHeader("Content-disposition", "attachment; filename=\""+ nombreArchivoSugerido+".zip\"");
			
			
			

			ServletOutputStream  salida = response.getOutputStream();
			
			byte buffer[] = new byte[archivo.available()];

			archivo.read(buffer);
			salida.write(buffer);
			
			archivo.close();
			salida.flush();
			salida.close();
			
			FacesContext.getCurrentInstance().responseComplete();
			
			
		}
		

	/**
	 * buscar la comision de seleccion correspondiente al grupo
	 */
	@SuppressWarnings("unchecked")
	private void buscarComision() {
		String cad = "select com_cab.*  "
				+ "from seleccion.comision_seleccion_cab com_cab "
				+ "join seleccion.comision_grupo com_grupo  "
				+ "on com_grupo.id_comision_cab = com_cab.id_comision_sel "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = com_grupo.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<ComisionSeleccionCab> lista = new ArrayList<ComisionSeleccionCab>();
		lista = em.createNativeQuery(cad, ComisionSeleccionCab.class)
				.getResultList();
		if (lista.size() > 0) {
			comisionSeleccionCab = new ComisionSeleccionCab();
			comisionSeleccionCab = lista.get(0);
			comision = lista.get(0).getDescripcion();
		}
	}

	/**
	 * metodo que carga el combo correspondiente a la los integrantes de la
	 * comision
	 */
	@SuppressWarnings("unchecked")
	private void cargarComboIntegrantesComision() {
		String cad = "select p.* "
				+ "from general.persona p "
				+ "join seleccion.comision_seleccion_det com_det "
				+ "on com_det.id_persona = p.id_persona "
				+ "join seleccion.comision_seleccion_cab com_cab "
				+ "on com_cab.id_comision_sel = com_det.id_comision_sel "
				+ "join seleccion.comision_grupo com_grupo "
				+ "on com_grupo.id_comision_cab = com_cab.id_comision_sel "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = com_grupo.id_concurso_puesto_agr "
				+ "where p.activo is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		List<Persona> lista = new ArrayList<Persona>();
		lista = em.createNativeQuery(cad, Persona.class).getResultList();
		lEvaluadores = lista; //Lista de Evaluadores que se cargaran automaticamente en las evaluaciones - EDCM
		integrantesComisionSelecItem = new ArrayList<SelectItem>();
		buildIntegrantesComisionSelectItem(lista);
	}

	private void buildIntegrantesComisionSelectItem(List<Persona> personaList) {
		if (integrantesComisionSelecItem == null)
			integrantesComisionSelecItem = new ArrayList<SelectItem>();
		else
			integrantesComisionSelecItem.clear();

		integrantesComisionSelecItem.add(new SelectItem(null,
				SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Persona p : personaList) {
			String nombres = p.getNombres() + " " + p.getApellidos() + " - "
					+ p.getDocumentoIdentidad();
			integrantesComisionSelecItem.add(new SelectItem(p.getIdPersona(),
					nombres));
		}
	}

	/**
	 * metodo que carga el combo de integrantes de la empresa
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void cargarComboIntegrantesEmpresa() {
		String cad = "";
		List<Persona> lista = new ArrayList<Persona>();
		if (idEmpresaTercerizada != null) {
			cad = "select p.* "
					+ "from general.persona p "
					+ "join seleccion.empresa_persona emp_pers "
					+ "on emp_pers.id_persona = p.id_persona  "
					+ "join seleccion.empr_tercerizada emp_terc "
					+ "on emp_terc.id_empresa_tercerizada = emp_pers.id_empresa_tercerizada "
					+ "where p.activo is true "
					+ " and emp_terc.id_empresa_tercerizada = "
					+ idEmpresaTercerizada;

			lista = em.createNativeQuery(cad, Persona.class).getResultList();
			integrantesEmpresaSelecItem = new ArrayList<SelectItem>();
			buildIntegrantesEmpresaSelectItem(lista);
		} else {

			integrantesEmpresaSelecItem = null;
			buildIntegrantesEmpresaSelectItem(lista);
		}

	}

	private void buildIntegrantesEmpresaSelectItem(List<Persona> personaList) {
		if (integrantesEmpresaSelecItem == null)
			integrantesEmpresaSelecItem = new ArrayList<SelectItem>();
		else
			integrantesEmpresaSelecItem.clear();

		integrantesEmpresaSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Persona p : personaList) {
			String nombres = p.getNombres() + " " + p.getApellidos() + " - "
					+ p.getDocumentoIdentidad();
			integrantesEmpresaSelecItem.add(new SelectItem(p.getIdPersona(),
					nombres));
		}
	}

	@SuppressWarnings("unchecked")
	private void cargarComboEmpresaTercerizada() {
		String cad = "select emp.* " + "from seleccion.empr_tercerizada emp "
				+ "where emp.activo is true";
		List<EmprTercerizada> lista = new ArrayList<EmprTercerizada>();
		lista = em.createNativeQuery(cad, EmprTercerizada.class)
				.getResultList();
		empresaTercerizadaSelecItem = new ArrayList<SelectItem>();
		buildEmpresaTercerizadaSelectItem(lista);
	}

	private void buildEmpresaTercerizadaSelectItem(
			List<EmprTercerizada> empresaList) {
		if (empresaTercerizadaSelecItem == null)
			empresaTercerizadaSelecItem = new ArrayList<SelectItem>();
		else
			empresaTercerizadaSelecItem.clear();

		empresaTercerizadaSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (EmprTercerizada e : empresaList) {

			empresaTercerizadaSelecItem.add(new SelectItem(e
					.getIdEmpresaTercerizada(), e.getNombre()));
		}
	}

	/**
	 * agrega registros a la columna de evaluadores
	 */
	public void agregar() {
		try {
			IntegrantesDTO dto = new IntegrantesDTO();
			if (tipoEvaluacion == null) {
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar el tipo antes de agregar, verifique");
				return;
			}

			if (tipoEvaluacion.getId().equals("comision")) {
				if (idIntegranteComision == null) {
					statusMessages.add(Severity.ERROR,
							"Debe seleccionar el Integrante, verifique");
					return;
				}
				if (existeComisionSel(comisionSeleccionCab.getIdComisionSel(),
						idIntegranteComision)) {
					statusMessages.add(Severity.ERROR,
							"Integrante ingresado ya existe, verifique");
					return;
				}

				if (comision != null) {
					dto.setComision(comision);
					dto.setComisionSeleccionCab(comisionSeleccionCab);
				}
				if (idIntegranteComision != null) {
					Persona persona = new Persona();
					persona = em.find(Persona.class, idIntegranteComision);
					dto.setPersonaComision(persona);
				}
			}
			if (tipoEvaluacion.getId().equals("empresa")) {
				if (idIntegranteEmpresa == null) {
					statusMessages.add(Severity.ERROR,
							"Debe seleccionar el Integrante, verifique");
					return;
				}
				if (existeEmpSel(idEmpresaTercerizada, idIntegranteEmpresa)) {
					statusMessages.add(Severity.ERROR,
							"Integrante ingresado ya existe, verifique");
					return;
				}
				if (idEmpresaTercerizada != null) {
					EmprTercerizada emp = new EmprTercerizada();
					emp = em.find(EmprTercerizada.class, idEmpresaTercerizada);
					dto.setEmprTercerizada(emp);
				}

				if (idIntegranteEmpresa != null) {
					Persona persona = new Persona();
					persona = em.find(Persona.class, idIntegranteEmpresa);
					dto.setPersonaEmpresa(persona);
				}
			}

			listaEvaluadoresDTO.add(dto);
			idIntegranteEmpresa = null;
			idEmpresaTercerizada = null;
			idIntegranteComision = null;
			estaAgregado = true;
			integrantesEmpresaSelecItem = new ArrayList<SelectItem>();
			integrantesEmpresaSelecItem
					.add(new SelectItem(null, SeamResourceBundle.getBundle()
							.getString("opt_select_one")));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages
					.add(Severity.ERROR,
							"Error al intentar agregar el registro, consulte con el administrador");
		}

	}

	/**
	 * Incidencia 0000851
	 * **/
	private boolean existeComisionSel(Long idCab, Long idInte) {
		for (int i = 0; i < listaEvaluadoresDTO.size(); i++) {
			if (listaEvaluadoresDTO.get(i).getComisionSeleccionCab() != null) {
				if (listaEvaluadoresDTO.get(i).getComisionSeleccionCab()
						.getIdComisionSel().intValue() == idCab.intValue()
						&& idInte.intValue() == listaEvaluadoresDTO.get(i)
								.getPersonaComision().getIdPersona().intValue())
					return true;
			}

		}
		return false;
	}

	private boolean existeEmpSel(Long idEm, Long idInteg) {
		for (int i = 0; i < listaEvaluadoresDTO.size(); i++) {
			if (listaEvaluadoresDTO.get(i).getEmprTercerizada() == null) {
				if (idEm == null
						&& listaEvaluadoresDTO.get(i).getPersonaEmpresa()
								.getIdPersona().intValue() == idInteg
								.intValue())
					return true;
			} else if (listaEvaluadoresDTO.get(i).getEmprTercerizada()
					.getIdEmpresaTercerizada().intValue() == idEm.intValue()
					&& listaEvaluadoresDTO.get(i).getPersonaEmpresa()
							.getIdPersona().intValue() == idInteg.intValue())
				return true;
		}
		return false;
	}

	/**
	 * fin
	 * **/

	/**
	 * elimina registros de la lista de evaluadores
	 * 
	 * @param row
	 */
	public void eliminar(Integer row) {
		IntegrantesDTO dto = new IntegrantesDTO();
		dto = listaEvaluadoresDTO.get(row);
		if (dto.getId() != null)
			listaEvaluadoresDTOEliminar.add(dto);
		listaEvaluadoresDTO.remove(dto);

	}

	public void save() {
	lock.lock();
		msgSave = null;
		if (!seleDocumento()) {
			
					msgSave = "Debe Seleccionar un Documento. Verifique";
			return;
		}

		if (listaEvaluadoresDTO.size() == 0) {
			
					msgSave = "Debe contar con al menos un evaluador.";
			return;
		}
		
	
		try {
			for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
				EvalDocumentalDet documentalDet = new EvalDocumentalDet();
				documentalDet = detDto.getEvalDocumentalDet();
				
				if(detDto.getPresento()){
													
					if (detDto.getAprobado())
						documentalDet.setAprobadoPorComision(true);
					else{
						
						
						if (detDto.getObligatorio() && !detDto.getEvalDocumentalDet().getMatrizDocConfigDet().getAgrupado()){
							
							if(this.observacion == null || this.observacion.trim().equals("")){
								msgSave = "En caso de 'NO APROBADO' debe ingresar una observacion";
								
								return;
							}
							
						}else{
							//	SI NO ESTA APROBADO Y ESTA AGRUPADO DEBE VERIFICAR QUE POSEA UN DOCUMENTO APROBADO DEL MISMO GRUPO. 
							EvalDocumentalDet det = em.find(EvalDocumentalDet.class, detDto.getEvalDocumentalDet().getIdEvalDocumentalDet());
							MatrizDocConfigDet configDet = em.find(MatrizDocConfigDet.class, det.getMatrizDocConfigDet().getIdMatrizDocConfigDet());
							Boolean poseeUno = false;
							if(configDet.getAgrupado()){
								
								MatrizDocConfigGrupos grupo = em.find(MatrizDocConfigGrupos.class, configDet.getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos());
														
								for (EvalDocumentalDetDTO detDtoAux : listaEvalDocumentalDetDTO) {
									if(detDtoAux.getPresento() && detDtoAux.getAprobado()){
										if(detDtoAux.getEvalDocumentalDet().getMatrizDocConfigDet().getMatrizDocConfigGrupos() != null){
											if(grupo.getIdMatrizDocConfigGrupos().equals(detDtoAux.getEvalDocumentalDet().getMatrizDocConfigDet().getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos())){
												poseeUno = true;
											}
										}
									}
								}
								
								if((this.observacion == null || this.observacion.trim().equals("")) && !poseeUno){
									msgSave = "En caso de 'NO APROBADO' debe ingresar una observacion";
									
									return;
								}
							}
						}
						
						documentalDet.setAprobadoPorComision(false);
					}
					em.merge(documentalDet);
					em.flush();
				}	
				

			}
			evalDocumentalCab.setFechaEvaluacion(new Date());
			// evalDocumentalCab.setAprobado(todoAprobados()); SE MODIFICO POR
			// LA INCIDENCIA 0001090
			evalDocumentalCab.setAprobado(cumple());// SI ES OBLIGATORIO Y TIENE
													// CHEK Aprobado -INCIDENCIA
													// 0001090
			// evalDocumentalCab.setEvaluado(true);
			if(!evalDocumentalCab.getAprobado()){
				if (observacion != null && !observacion.trim().isEmpty())
					evalDocumentalCab.setObservacion(observacion);
				statusMessages.clear();
				statusMessages.add(Severity.INFO,
						" En Caso de No Aprobado debe cargar una observación");
				return;
			}else{
				evalDocumentalCab.setObservacion(observacion);
			}
			
			
			evalDocumentalCab.setIncluido(false);
			em.merge(evalDocumentalCab);
			em.flush();
			for (IntegrantesDTO integrantesDTO : listaEvaluadoresDTO) {
				if (integrantesDTO.getId() == null) {
					if (integrantesDTO.getPersonaComision() != null) {
						List<ComisionSeleccionDet> listaSeleccionDet = new ArrayList<ComisionSeleccionDet>();
						listaSeleccionDet = buscarComisionesAInsertar(integrantesDTO
								.getPersonaComision().getIdPersona());
						if (listaSeleccionDet != null
								&& listaSeleccionDet.size() > 0) {
							for (ComisionSeleccionDet eval : listaSeleccionDet) {
								EvalDocumentalComis comis = new EvalDocumentalComis();
								comis.setActivo(true);
								comis.setComisionSeleccionDet(eval);
								comis.setEvalDocumentalCab(evalDocumentalCab);
								comis.setFechaAlta(new Date());
								comis.setUsuAlta(usuarioLogueado
										.getCodigoUsuario());
								em.persist(comis);
							}
							em.flush();
						}

					}
					if (integrantesDTO.getPersonaEmpresa() != null) {
						List<EmpresaPersona> listaEmp = new ArrayList<EmpresaPersona>();
						listaEmp = buscarEmpresaAInsertar(integrantesDTO
								.getPersonaEmpresa().getIdPersona(),
								integrantesDTO.getEmprTercerizada()
										.getIdEmpresaTercerizada());
						if (listaEmp != null && listaEmp.size() > 0) {
							for (EmpresaPersona emp : listaEmp) {
								EvalDocumentalComis comis = new EvalDocumentalComis();
								comis.setActivo(true);
								comis.setEmpresaPersona(emp);
								comis.setEvalDocumentalCab(evalDocumentalCab);
								comis.setFechaAlta(new Date());
								comis.setUsuAlta(usuarioLogueado
										.getCodigoUsuario());
								em.persist(comis);
							}
							em.flush();
						}
					}
				}
			}

			for (IntegrantesDTO auxEliminar : listaEvaluadoresDTOEliminar) {
				EvalDocumentalComis com = new EvalDocumentalComis();
				com = em.find(EvalDocumentalComis.class, auxEliminar.getId());
				em.remove(com);
				em.flush();
			}

			if (cumple()) {
				msg = "El postulante "
						+ evalDocumentalCab.getPostulacion()
								.getPersonaPostulante().getUsuAlta();
				/*
				 * SeamResourceBundle
				 * .getBundle().getString("CU41_msg_aprobado");
				 * statusMessages.clear(); statusMessages.add(Severity.INFO,
				 * msg); statusMessages.add(Severity.INFO, SeamResourceBundle
				 * .getBundle().getString("GENERICO_MSG"));
				 */
				aprobo = "SI";
				return;
			} else {
				msg = "El postulante "
						+ evalDocumentalCab.getPostulacion()
								.getPersonaPostulante().getUsuAlta();
				/*
				 * SeamResourceBundle
				 * .getBundle().getString("CU41_msg_noaprobado");
				 * statusMessages.clear(); statusMessages.add(Severity.INFO,
				 * msg); statusMessages.add(Severity.INFO, SeamResourceBundle
				 * .getBundle().getString("GENERICO_MSG"));
				 */
				aprobo = "NO";
				return;
			}
			
			

		} catch (Exception e) {
			
			evalDocumentalCab.setIncluido(false);
			em.merge(evalDocumentalCab);
			em.flush();
			
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					" Error al intentar guardar el registro");
		}
		lock.unlock();
		return;
	}
	
	
	
	public void saveTRM() {
		lock.lock();
			msgSave = null;
			if (!seleDocumento()) {
				
						msgSave = "Debe Seleccionar un Documento. Verifique";
				return;
			}

			if (listaEvaluadoresDTO.size() == 0) {
				
						msgSave = "Debe contar con al menos un evaluador.";
				return;
			}
			
			
		
			try {
				for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
					EvalDocumentalDet documentalDet = new EvalDocumentalDet();
					documentalDet = detDto.getEvalDocumentalDet();
					if (detDto.getAprobado())
						documentalDet.setAprobadoPorComision(true);
					else{
						
						if (detDto.getObligatorio() && !detDto.getEvalDocumentalDet().getMatrizDocConfigDet().getAgrupado()){
							
							if(this.observacion == null || this.observacion.equals("")){
								msgSave = "En caso de 'NO APROBADO' debe ingresar una observacion";
								
								return;
							}
							
						}else{
							//	SI NO ESTA APROBADO Y ESTA AGRUPADO DEBE VERIFICAR QUE POSEA UN DOCUMENTO APROBADO DEL MISMO GRUPO. 
							EvalDocumentalDet det = em.find(EvalDocumentalDet.class, detDto.getEvalDocumentalDet().getIdEvalDocumentalDet());
							MatrizDocConfigDet configDet = em.find(MatrizDocConfigDet.class, det.getMatrizDocConfigDet().getIdMatrizDocConfigDet());
							Boolean poseeUno = false;
							if(configDet.getAgrupado()){
								
								MatrizDocConfigGrupos grupo = em.find(MatrizDocConfigGrupos.class, configDet.getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos());
														
								for (EvalDocumentalDetDTO detDtoAux : listaEvalDocumentalDetDTO) {
									if(detDtoAux.getPresento() && detDtoAux.getAprobado()){
										if(detDtoAux.getEvalDocumentalDet().getMatrizDocConfigDet().getMatrizDocConfigGrupos() != null){
											if(grupo.getIdMatrizDocConfigGrupos().equals(detDtoAux.getEvalDocumentalDet().getMatrizDocConfigDet().getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos())){
												poseeUno = true;
											}
										}
									}
								}
								
								if((this.observacion == null || this.observacion.equals("")) && !poseeUno){
									msgSave = "En caso de 'NO APROBADO' debe ingresar una observacion";
									
									return;
								}
							}
						}
						
						documentalDet.setAprobadoPorComision(false);
					}
						
					em.merge(documentalDet);
					em.flush();

				}
				evalDocumentalCab.setFechaEvaluacion(new Date());
				evalDocumentalCab.setAprobado(cumple());
				
				if (observacion != null && !observacion.trim().isEmpty())
					evalDocumentalCab.setObservacion(observacion);
				
				
				em.merge(evalDocumentalCab);
				em.flush();
				
				
				for (IntegrantesDTO integrantesDTO : listaEvaluadoresDTO) {
					if (integrantesDTO.getId() == null) {
						if (integrantesDTO.getPersonaComision() != null) {
							List<ComisionSeleccionDet> listaSeleccionDet = new ArrayList<ComisionSeleccionDet>();
							listaSeleccionDet = buscarComisionesAInsertar(integrantesDTO
									.getPersonaComision().getIdPersona());
							if (listaSeleccionDet != null
									&& listaSeleccionDet.size() > 0) {
								for (ComisionSeleccionDet eval : listaSeleccionDet) {
									EvalDocumentalComis comis = new EvalDocumentalComis();
									comis.setActivo(true);
									comis.setComisionSeleccionDet(eval);
									comis.setEvalDocumentalCab(evalDocumentalCab);
									comis.setFechaAlta(new Date());
									comis.setUsuAlta(usuarioLogueado
											.getCodigoUsuario());
									em.persist(comis);
								}
								em.flush();
							}

						}
						if (integrantesDTO.getPersonaEmpresa() != null) {
							List<EmpresaPersona> listaEmp = new ArrayList<EmpresaPersona>();
							listaEmp = buscarEmpresaAInsertar(integrantesDTO
									.getPersonaEmpresa().getIdPersona(),
									integrantesDTO.getEmprTercerizada()
											.getIdEmpresaTercerizada());
							if (listaEmp != null && listaEmp.size() > 0) {
								for (EmpresaPersona emp : listaEmp) {
									EvalDocumentalComis comis = new EvalDocumentalComis();
									comis.setActivo(true);
									comis.setEmpresaPersona(emp);
									comis.setEvalDocumentalCab(evalDocumentalCab);
									comis.setFechaAlta(new Date());
									comis.setUsuAlta(usuarioLogueado
											.getCodigoUsuario());
									em.persist(comis);
								}
								em.flush();
							}
						}
					}
				}

				for (IntegrantesDTO auxEliminar : listaEvaluadoresDTOEliminar) {
					EvalDocumentalComis com = new EvalDocumentalComis();
					com = em.find(EvalDocumentalComis.class, auxEliminar.getId());
					em.remove(com);
					em.flush();
				}

				if (cumple()) {
					msg = "El postulante "	+ evalDocumentalCab.getPostulacion().getPersonaPostulante().getUsuAlta();
				
					aprobo = "SI";
					return;
				} else {
					msg = "El postulante " + evalDocumentalCab.getPostulacion().getPersonaPostulante().getUsuAlta();
					
					aprobo = "NO";
					
					//Replicar las evaluaciones por grupo setteando los puntajes en 0
					
					
					if(!this.evaluacionesModificado(evalDocumentalCab.getPostulacion().getIdPostulacion())){
						String sqlFuncion = "select seleccion.replicar_evaluacionesNoAdmitido("+ evalDocumentalCab.getPostulacion().getIdPostulacion() +")";
						String retornoFuncion = (String) em.createNativeQuery(sqlFuncion).getSingleResult();
						
						if(retornoFuncion.equals("ok")){
							statusMessages.clear();
							statusMessages.add(Severity.INFO,"Función ejecutada correctamente ");
						}else if (retornoFuncion.equals("error")){
							statusMessages.clear();
							statusMessages.add(Severity.ERROR,"Función ejecutada con errores ");
						}
							
					}
					
					return;
				}
				
				

			} catch (Exception e) {
				
				evalDocumentalCab.setIncluido(false);
				em.merge(evalDocumentalCab);
				em.flush();
				
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						" Error al intentar guardar el registro");
			}
			lock.unlock();
			return;
		}
		
	
	public boolean evaluacionesModificado (Long idPostulacion){
		boolean retorno = false;
		
		String sql = "select count (*) from seleccion.eval_referencial_postulante where usu_alta = 'SYSTEM' and activo = true and id_postulacion = "+idPostulacion;
		
		int cantidad = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).intValue();
		
		if(cantidad >= 1)
			retorno = true;
		
		return retorno;
	}

	@SuppressWarnings("unchecked")
	private List<ComisionSeleccionDet> buscarComisionesAInsertar(Long id) {
		String sql = "select com_det.* "
				+ "from seleccion.comision_seleccion_det com_det "
				+ "join seleccion.comision_seleccion_cab com_cab "
				+ "on com_cab.id_comision_sel = com_det.id_comision_sel "
				+ "join general.persona p "
				+ "on p.id_persona = com_det.id_persona "
				+ "where com_cab.id_comision_sel = "
				+ comisionSeleccionCab.getIdComisionSel()
				+ "and p.id_persona = " + id;

		return em.createNativeQuery(sql, ComisionSeleccionDet.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<EmpresaPersona> buscarEmpresaAInsertar(Long idPersona,
			Long idEmpresa) {
		String sql = "select emp_persona.* "
				+ "from seleccion.empresa_persona emp_persona "
				+ "join seleccion.empr_tercerizada emp_tercerizada "
				+ "on emp_tercerizada.id_empresa_tercerizada = emp_persona.id_empresa_tercerizada "
				+ "join general.persona p on p.id_persona = emp_persona.id_persona "
				+ "where p.id_persona = " + idPersona
				+ " and emp_tercerizada.id_empresa_tercerizada = " + idEmpresa;
		return em.createNativeQuery(sql, EmpresaPersona.class).getResultList();
	}

	/**
	 * se agrego para la incidencia 0000871 INCIDENCIA 0001090 SI TIENE CHEK EN
	 * APROBADO Y NO TIENE DOC.
	 * **/
	private Boolean seleDocumento() {
		boolean existe = true;
		for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
			if (detDto.getAprobado() && detDto.getDocumentos() == null)
				existe = false;
		}
		return existe;
	}

	/**
	 * fin
	 * **/
	private Boolean cumple() {
		Boolean esta = false;
		for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
			if(detDto.getPresento() != null && detDto.getPresento()){
				
				if(detDto.getObligatorio() != null){	
					
					if ( detDto.getObligatorio()) {
							if (detDto.getAprobado())
								esta = true;
							else{
								esta = false;
								//	SI NO ESTA APROBADO Y ESTA AGRUPADO DEBE VERIFICAR QUE POSEA UN DOCUMENTO APROBADO DEL MISMO GRUPO. 
								EvalDocumentalDet det = em.find(EvalDocumentalDet.class, detDto.getEvalDocumentalDet().getIdEvalDocumentalDet());
								MatrizDocConfigDet configDet = em.find(MatrizDocConfigDet.class, det.getMatrizDocConfigDet().getIdMatrizDocConfigDet());
								if(configDet.getAgrupado()){
									
									MatrizDocConfigGrupos grupo = em.find(MatrizDocConfigGrupos.class, configDet.getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos());
															
									for (EvalDocumentalDetDTO detDtoAux : listaEvalDocumentalDetDTO) {
										if(detDtoAux.getPresento() && detDtoAux.getAprobado()){
											if(detDtoAux.getEvalDocumentalDet().getMatrizDocConfigDet().getMatrizDocConfigGrupos() != null){
												if(grupo.getIdMatrizDocConfigGrupos().equals(detDtoAux.getEvalDocumentalDet().getMatrizDocConfigDet().getMatrizDocConfigGrupos().getIdMatrizDocConfigGrupos())){
													esta = true;
												}
											}
										}
											
									}
									if(esta == false)
										return false;
								}else{
									return false;
								}
							}
					}
				}
			}

		}
		return esta;
	}

	/**
	 * llama al metodo abrir del cu 289
	 * 
	 * @param index
	 */
	public void seleccionadoParaAdjuntar(int index) {
		item = null;
		nombreDoc = null;
		EvalDocumentalDetDTO e = listaEvalDocumentalDetDTO.get(index);
		idTipoDoc = e.getTipoDocumento().getIdDatosEspecificos();

	}

	@SuppressWarnings("unchecked")
	public void documento() throws NamingException {
		nombrePantalla = "evaluar_doc_postulante_edit";

		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}

		} else {
			statusMessages.add(Severity.WARN,
					"Debe seleccionar el Documento para poder adjuntar");
			return;
		}
		/**
		 * VALIDACION AGREGADA
		 * */
		String nomfinal = "";
		nomfinal = item.getFileName();
		String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
		List<TipoArchivo> ta = em.createQuery(
				"select t from TipoArchivo t "
						+ " where lower(t.extension) like '"
						+ extension.toLowerCase() + "'").getResultList();
		if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
			if (item.getFileSize() > ta.get(0).getUnidMedidaByte().intValue()) {
				statusMessages.add(Severity.ERROR,
						"El archivo supera el tamaño máximo permitido. Límite "
								+ ta.get(0).getTamanho()
								+ ta.get(0).getUnidMedida() + ", verifique");
				return;
			}
		}
		/**
		 * FIN
		 * */

		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "PersonaPostulante");
		if (idDoc != null) {
			if (idDoc.longValue() == -8) {
				statusMessages
						.add(Severity.ERROR,
								"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
				return;
			}
			if (idDoc.longValue() == -7) {
				statusMessages
						.add(Severity.ERROR,
								"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
				return;
			}
			if (idDoc.longValue() == -6) {
				statusMessages
						.add(Severity.ERROR,
								"El archivo que desea levantar ya existe. Seleccione otro");
				return;
			}
			try {
				PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
				postulacionAdjuntos.setDocumento(em.find(Documentos.class,
						idDoc));
				postulacionAdjuntos.setPostulacion(evalDocumentalCab
						.getPostulacion());
				postulacionAdjuntos.setActivo(true);
				postulacionAdjuntos.setFechaAlta(new Date());
				postulacionAdjuntos.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				em.persist(postulacionAdjuntos);
				em.flush();
				buscarDetalles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
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
						&& (numRead = fis.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return;
			}

			if (offset < bytes.length) {
				try {
					throw new IOException(
							"No se pudo leer el archivo por completo "
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

	public String clearUploadData() {
		item = null;
		nombreDoc = null;
		uploadedFile = null;
		contentType = null;
		fileName = null;
		return null;
	}

	@SuppressWarnings("unchecked")
	public void buscarDocumentosAlistar(int index) {
		EvalDocumentalDetDTO e = listaEvalDocumentalDetDTO.get(index);
		String sql = "select doc.* "
				+ "from general.documentos doc "
				+ "join seleccion.datos_especificos datos_esp "
				+ "on datos_esp.id_datos_especificos = doc.id_datos_especificos_tipo_documento "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_documental_cab eval_cab "
				+ "on eval_cab.id_postulacion = post.id_postulacion "
				+ "join seleccion.eval_documental_det eval_det "
				+ "on eval_det.id_eval_documental_cab = eval_cab.id_eval_documental_cab "
				+ "where eval_det.id_eval_documental_det = "
				+ e.getEvalDocumentalDet().getIdEvalDocumentalDet()
				+ " and datos_esp.id_datos_especificos = "
				+ e.getTipoDocumento().getIdDatosEspecificos()
				+ " and doc.activo is true";
		listaDocAmostrar = new ArrayList<Documentos>();
		listaDocAmostrar = em.createNativeQuery(sql, Documentos.class)
				.getResultList();

	}
	
	@SuppressWarnings("unchecked")
	public void buscarDocumentosCompletos(int index) {
		Documentos e = listaDocumentosCompletos.get(index);
		
		listaDocAmostrar = new ArrayList<Documentos>();
		listaDocAmostrar.add(e);

	}
	
	
	//Generado por ECespedess
	private void cargarListaDocumentosCompletos(){
		this.listaDocumentosCompletos = new ArrayList<Documentos>();
		
		this.listaDocumentosCompletos = em.createQuery(
				"select d from Documentos d "
						+ "  where d.nombreTabla like 'persona_postulante' "
						+ " and d.idTabla =" + evalDocumentalCab.getPostulacion().getPersonaPostulante().getIdPersonaPostulante()).getResultList();

	}
	

	public List<Documentos> getListaDocumentosCompletos() {
		return listaDocumentosCompletos;
	}

	public void setListaDocumentosCompletos(
			List<Documentos> listaDocumentosCompletos) {
		this.listaDocumentosCompletos = listaDocumentosCompletos;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public EvalDocumentalCab getEvalDocumentalCab() {
		return evalDocumentalCab;
	}

	public void setEvalDocumentalCab(EvalDocumentalCab evalDocumentalCab) {
		this.evalDocumentalCab = evalDocumentalCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public List<EvalDocumentalDet> getListaEvalDocumentalDet() {
		return listaEvalDocumentalDet;
	}

	public void setListaEvalDocumentalDet(
			List<EvalDocumentalDet> listaEvalDocumentalDet) {
		this.listaEvalDocumentalDet = listaEvalDocumentalDet;
	}

	public List<EvalDocumentalDetDTO> getListaEvalDocumentalDetDTO() {
		return listaEvalDocumentalDetDTO;
	}

	public void setListaEvalDocumentalDetDTO(
			List<EvalDocumentalDetDTO> listaEvalDocumentalDetDTO) {
		this.listaEvalDocumentalDetDTO = listaEvalDocumentalDetDTO;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public TipoEvaluacion getTipoEvaluacion() {
		return tipoEvaluacion;
	}

	public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
		this.tipoEvaluacion = tipoEvaluacion;
	}

	public String getComision() {
		return comision;
	}

	public void setComision(String comision) {
		this.comision = comision;
	}

	public Long getIdIntegranteComision() {
		return idIntegranteComision;
	}

	public void setIdIntegranteComision(Long idIntegranteComision) {
		this.idIntegranteComision = idIntegranteComision;
	}

	public Long getIdIntegranteEmpresa() {
		return idIntegranteEmpresa;
	}

	public void setIdIntegranteEmpresa(Long idIntegranteEmpresa) {
		this.idIntegranteEmpresa = idIntegranteEmpresa;
	}

	public Boolean getEstaAgregado() {
		return estaAgregado;
	}

	public void setEstaAgregado(Boolean estaAgregado) {
		this.estaAgregado = estaAgregado;
	}

	public List<SelectItem> getIntegrantesComisionSelecItem() {
		return integrantesComisionSelecItem;
	}

	public void setIntegrantesComisionSelecItem(
			List<SelectItem> integrantesComisionSelecItem) {
		this.integrantesComisionSelecItem = integrantesComisionSelecItem;
	}

	public List<SelectItem> getIntegrantesEmpresaSelecItem() {
		return integrantesEmpresaSelecItem;
	}

	public void setIntegrantesEmpresaSelecItem(
			List<SelectItem> integrantesEmpresaSelecItem) {
		this.integrantesEmpresaSelecItem = integrantesEmpresaSelecItem;
	}

	public Long getIdEmpresaTercerizada() {
		return idEmpresaTercerizada;
	}

	public void setIdEmpresaTercerizada(Long idEmpresaTercerizada) {
		this.idEmpresaTercerizada = idEmpresaTercerizada;
	}

	public List<SelectItem> getEmpresaTercerizadaSelecItem() {
		return empresaTercerizadaSelecItem;
	}

	public void setEmpresaTercerizadaSelecItem(
			List<SelectItem> empresaTercerizadaSelecItem) {
		this.empresaTercerizadaSelecItem = empresaTercerizadaSelecItem;
	}

	public List<IntegrantesDTO> getListaEvaluadoresDTO() {
		return listaEvaluadoresDTO;
	}

	public void setListaEvaluadoresDTO(List<IntegrantesDTO> listaEvaluadoresDTO) {
		this.listaEvaluadoresDTO = listaEvaluadoresDTO;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}

	public void setComisionSeleccionCab(
			ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
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

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public List<Documentos> getListaDocAmostrar() {
		return listaDocAmostrar;
	}

	public void setListaDocAmostrar(List<Documentos> listaDocAmostrar) {
		this.listaDocAmostrar = listaDocAmostrar;
	}

	public List<IntegrantesDTO> getListaEvaluadoresDTOAux() {
		return listaEvaluadoresDTOAux;
	}

	public void setListaEvaluadoresDTOAux(
			List<IntegrantesDTO> listaEvaluadoresDTOAux) {
		this.listaEvaluadoresDTOAux = listaEvaluadoresDTOAux;
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

	public List<IntegrantesDTO> getListaEvaluadoresDTOEliminar() {
		return listaEvaluadoresDTOEliminar;
	}

	public void setListaEvaluadoresDTOEliminar(
			List<IntegrantesDTO> listaEvaluadoresDTOEliminar) {
		this.listaEvaluadoresDTOEliminar = listaEvaluadoresDTOEliminar;
	}

	public Long getCabeza() {
		return cabeza;
	}

	public void setCabeza(Long cabeza) {
		this.cabeza = cabeza;
	}

	public SeguridadUtilFormController getSeguridadUtilFormController() {
		return seguridadUtilFormController;
	}

	public void setSeguridadUtilFormController(
			SeguridadUtilFormController seguridadUtilFormController) {
		this.seguridadUtilFormController = seguridadUtilFormController;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getAprobo() {
		return aprobo;
	}

	public void setAprobo(String aprobo) {
		this.aprobo = aprobo;
	}

	public String getMsgSave() {
		return msgSave;
	}

	public void setMsgSave(String msgSave) {
		this.msgSave = msgSave;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public boolean isHabilitarVolver() {
		return habilitarVolver;
	}

	public void setHabilitarVolver(boolean habilitarVolver) {
		this.habilitarVolver = habilitarVolver;
	}
	public boolean isHabilitarGuardar() {
		return habilitarGuardar;
	}

	public void setHabilitarGuardar(boolean habilitarGuardar) {
		this.habilitarGuardar = habilitarGuardar;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	
}
