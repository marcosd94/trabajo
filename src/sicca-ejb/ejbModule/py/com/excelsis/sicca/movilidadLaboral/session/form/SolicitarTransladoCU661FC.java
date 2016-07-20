package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ControlRemuneracionesTmp;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.RemuneracionesTmp;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.entity.SolicitudTrasladoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("solicitarTransladoCU661FC")
public class SolicitarTransladoCU661FC implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8467311205414910365L;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	BuscadorDocsFC buscadorDocsFC;
	@In(create = true)
	SinarhUtiles sinarhUtiles;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(required = false, create = true)
	private Actor actor;

	private String docIdentidad = null;
	private String observacion;
	private String nombreNivelEntidad;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private BigDecimal codNivelEntidad;
	private Long idFuncionario;
	private Long idTipoSolicitud;
	private Boolean mostrarBoton;
	private Boolean mostrarCategoriaRemuneracion;
	private EmpleadoPuesto funcionario = new EmpleadoPuesto();
	private Persona persona;
	SolicitudTrasladoCab solicitudTrasladoCab;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private SinAnx sinAnxSeleccionado = null;
	private List<EmpleadoMovilidadAnexo> conceptoPagosActual = new ArrayList<EmpleadoMovilidadAnexo>();
	private List<EmpleadoMovilidadAnexo> conceptoPagosOrigen = new ArrayList<EmpleadoMovilidadAnexo>();
	private Long idPlantaCargoDet;
	private PlantaCargoDet solicitudPlantaCargoDet;
	private String solicitudValorObj;
	private Integer solicitudCodObj;
	private String solicitudCategoria;
	private String solicitudCodCategoria;
	private Long solicitudMonto;
	private List<ConfiguracionUoDet> configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
	private List<SelectItem> tipoSolicitudSelectItems = new ArrayList<SelectItem>();
	private List<PlantaCargoDet> plantaCargoDetList = new ArrayList<PlantaCargoDet>();

	/**
	 * Método que inicializa los objetos y variables
	 */
	public void init() {
		cargarNiveentidadOee();
		mostrarBoton = true;
		if (idFuncionario != null) {
			funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
			if (funcionario.getPlantaCargoDet().getContratado()) {
				funcionario = new EmpleadoPuesto();
				statusMessages.add(Severity.ERROR,
						"Escoja un Funcionario Permanente");
				return;
			}
			if (montoActual() == 0) {
				funcionario = new EmpleadoPuesto();
				statusMessages.add(Severity.ERROR,
						"Escoja un Funcionario que sí perciba el Objeto de Gasto 111");
				return;
			}
			persona = funcionario.getPersona();
			seleccionUtilFormController.setPlantaCargoDet(funcionario
					.getPlantaCargoDet());
			seleccionUtilFormController.setCodigoSinarh(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getCodigoSinarh());
			Long idSinNivelEntidad = getIdSinNivelEntidad(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getNenCodigo());
			SinNivelEntidad nivelEntidad = em.find(SinNivelEntidad.class,
					idSinNivelEntidad);
			nombreNivelEntidad = nivelEntidad.getNenNombre();
			codNivelEntidad = nivelEntidad.getNenCodigo();
		} else
			obtenerTipoSolicitud();
		asignarRolesTarea();
		mostrarCategoriaRemuneracion = true;
	}

	/**
	 * Asigna los roles necesarios a la primera tarea
	 */
	private void asignarRolesTarea() {
		roles = seleccionUtilFormController.getRolesTarea(
				ActividadEnum.MOV_RESPONDER_TRASLADO.getValor(),
				ProcesoEnum.MOVILIDAD.getValor());
	}

	/**
	 * Método que obtiene los Tipos de Solicitud mostrados en el combo 
	 */
	private void obtenerTipoSolicitud() {
		String select = "SELECT seleccion.datos_especificos.* "
				+ "FROM seleccion.datos_generales "
				+ "INNER JOIN seleccion.datos_especificos ON (seleccion.datos_generales.id_datos_generales = seleccion.datos_especificos.id_datos_generales) "
				+ "WHERE seleccion.datos_generales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' "
				+ "AND seleccion.datos_especificos.descripcion LIKE 'TRASLADO%'";
		List<DatosEspecificos> l = em.createNativeQuery(select,
				DatosEspecificos.class).getResultList();
		tipoSolicitudSelectItems = new ArrayList<SelectItem>();
		tipoSolicitudSelectItems.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos d : l)
			tipoSolicitudSelectItems.add(new SelectItem(d
					.getIdDatosEspecificos(), "" + d.getDescripcion()));

	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	/**
	 * Método que carga Nivel-Entidad-OEE del usuario logueado
	 */
	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList = em
				.createQuery(
						"Select det from ConfiguracionUoDet det "
								+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE ")
				.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab())
				.getResultList();
	}

	/**
	 * Método que obtiene el Id del Nivel
	 * @param codNivelEntidad
	 * @return
	 */
	public Long getIdSinNivelEntidad(BigDecimal codNivelEntidad) {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		Query q = em
				.createQuery("select sinNivelEntidad from SinNivelEntidad sinNivelEntidad "
						+ "WHERE sinNivelEntidad.nenCodigo ="
						+ codNivelEntidad
						+ " and sinNivelEntidad.activo is true "
						+ "order by sinNivelEntidad.aniAniopre DESC");
		List<SinNivelEntidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdSinNivelEntidad();
		}
		return null;
	}

	/**
	 * Método que llama a la busqueda de funcionarios
	 * @return
	 */
	public String getUrlToPageSearchFuncionario() {

		return "/busquedas/funcionarios/EmpleadoPuestoList.xhtml?from=movilidadLaboral/solicitarTranslado/SolicitudTransladoCU661";
	}

	/**
	 * Metodo que llama a la pagina que muestra los datos de la persona
	 * @return
	 */
	public String toFindPersonaToView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/movilidadLaboral/solicitarTranslado/SolicitudTransladoCU661&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
	}

	/**
	 * Chequea que todos los datos obligatorios fueron ingresados 
	 * @return
	 */
	private boolean chkDatos() {
		if (buscadorDocsFC.getNroDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Numero de Documento");
			return false;
		}
		if (buscadorDocsFC.getFechaDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (buscadorDocsFC.getIdTipoDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (buscadorDocsFC.getDocDecreto() == null
				&& buscadorDocsFC.getFileActoAdmin() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar un archivo, verifique");
			return false;
		}

		if (funcionario == null || funcionario.getIdEmpleadoPuesto() == null) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio: Funcionario");
			return false;
		}
		if (idTipoSolicitud == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Tipo de Solicitud");
			return false;
		}
		
		if (seleccionUtilFormController.getPlantaCargoDet() == null) {
			statusMessages.add(Severity.ERROR, "Debe Seleccionar el Puesto");
			return false;
		}
		
		if(mostrarCategoriaRemuneracion){
			if (solicitudCodObj == null || solicitudValorObj == null) {
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Cod. Objeto Gasto");
				return false;
			}
			
			if (solicitudMonto == null) {
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Monto");
				return false;
			}
			if (solicitudMonto.intValue() < 0) {
				statusMessages.add(Severity.ERROR, "El Monto debe ser mayor a cero");
				return false;
			}
		}
		return true;
	}

	/**
	 * Método que guarda los datos e inicia la tarea
	 * @return
	 */
	public String save() {
		try {
			if (!chkDatos())
				return null;
			/**
			 * Guarda la cabecera
			 */
			solicitudTrasladoCab = new SolicitudTrasladoCab();
			Long id = funcionario.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo();
			
			solicitudTrasladoCab.setOeeOrigen(em.find(ConfiguracionUoCab.class, id));
			solicitudTrasladoCab.setOeeDestino(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
			solicitudTrasladoCab
					.setDatosEspEstadoSolicitud(searchDatosEstadoSolicitud());
			solicitudTrasladoCab.setActivo(true);
			solicitudTrasladoCab.setPersona(persona);
			solicitudTrasladoCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			solicitudTrasladoCab.setFechaAlta(new Date());
			solicitudTrasladoCab.setDatosEspTipoTranslado(em.find(
					DatosEspecificos.class, idTipoSolicitud));
			solicitudTrasladoCab.setOeeDestino(em.find(
					ConfiguracionUoCab.class, usuarioLogueado
							.getConfiguracionUoCab().getIdConfiguracionUo()));
			solicitudTrasladoCab.setEmpleadoPuesto(funcionario);
			if(mostrarCategoriaRemuneracion){
				solicitudTrasladoCab.setCodObjeto(solicitudCodObj);
				solicitudTrasladoCab.setValorObj(solicitudValorObj);
				solicitudTrasladoCab.setCodCategoria(solicitudCodCategoria);
				solicitudTrasladoCab.setCategoria(solicitudCategoria);
				solicitudTrasladoCab.setMonto(solicitudMonto);
			}
			solicitudTrasladoCab.setPlantaCargoDet(solicitudPlantaCargoDet);
			em.persist(solicitudTrasladoCab);

			/**
			 * Guarda el detalle
			 */
			if (observacion != null && !observacion.trim().isEmpty()) {
				SolicitudTrasladoDet solicitudTrasladoDet = new SolicitudTrasladoDet();
				solicitudTrasladoDet
						.setDatosEspEstadoSolicitud(searchDatosEstadoSolicitudDetalle());
				solicitudTrasladoDet.setActivo(true);
				solicitudTrasladoDet.setObservacion(observacion.trim());
				solicitudTrasladoDet
						.setSolicitudTrasladoCab(solicitudTrasladoCab);
				solicitudTrasladoDet.setFechaAlta(new Date());
				solicitudTrasladoDet.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				em.persist(solicitudTrasladoDet);
			}
				/**
				 * Insertar registros de documentos adjuntos *
				 */
				/**
				 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
				 * Insertar registro en Documentos y el archivo pdf
				 * 
				 * *
				 */
				Long idDoc = null;
				if (buscadorDocsFC.getFileActoAdmin() != null) {
					if (buscadorDocsFC.getDocDecreto() == null) {
						idDoc = guardarAdjuntos(
								buscadorDocsFC.getfName(),
								buscadorDocsFC.getFileActoAdmin().getFileSize(),
								buscadorDocsFC.getFileActoAdmin()
										.getContentType(), buscadorDocsFC
										.getFileActoAdmin(), buscadorDocsFC
										.getIdTipoDoc(), null);
						if (idDoc == null)
							return null;
						Documentos doc = em.find(Documentos.class, idDoc);
						doc.setNroDocumento(buscadorDocsFC.getNroDoc());
						doc.setAnhoDocumento(Integer.parseInt(sdfAnio
								.format(buscadorDocsFC.getFechaDoc())));
						doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
						doc.setConfiguracionUoCab(usuarioLogueado
								.getConfiguracionUoCab());
						em.merge(doc);
						solicitudTrasladoCab.setDocumentoDestino(doc);
						
					}
				}

				/**
				 * Si no se inserta en tabla documentos y solo se referencia
				 * */
				if (idDoc == null)
					idDoc = buscadorDocsFC.getDocDecreto().getIdDocumento();
				insertarAdjunto(idDoc);
		

			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.MOV_RESPONDER_TRASLADO);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
			jbpmUtilFormController.setTransition("start_TO_resSol");
			actor.setId(usuarioLogueado.getCodigoUsuario());

			Long processId = jbpmUtilFormController.initProcess("movilidad");

			if (processId != null) {
				solicitudTrasladoCab.setIdProcessInstance(processId);
			} else {
				throw new Exception("");
			}

			em.flush();
			statusMessages
					.add("La solicitud de transalado ha iniciado satisfactoriamente");
			mostrarBoton = false;
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto
	 * administrativo
	 * */
	private void insertarAdjunto(Long idDocumento) {
		ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
		referenciaAdjuntos.setDocumentos(new Documentos());
		referenciaAdjuntos.getDocumentos().setIdDocumento(idDocumento);
		referenciaAdjuntos.setIdRegistroTabla(solicitudTrasladoCab
				.getIdSolicitudTrasladoCab());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		if(actividadProcesoActual() != null){
			referenciaAdjuntos.setActividadProceso(actividadProcesoActual());
		}
		em.persist(referenciaAdjuntos);
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "SolicitudTrasladoCab";
			String nombrePantalla = "solicitar_translado_edit";// cambiar
			String direccionFisica = "";
			String sf = File.separator;

			direccionFisica = sf + "SICCA" + sf + anio + sf + "OEE" + sf
					+ nivelEntidadOeeUtil.getIdConfigCab() + sf + "MOVILIDAD";

			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(file,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Método que carga el selectItem de Tipo Doc
	 * @return
	 */
	public List<SelectItem> updateTipoDocSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoDocumento())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoDocumento() {
		try {
			List<DatosEspecificos> datosEspecificosLists = em
					.createQuery(
							"Select d from DatosEspecificos d "
									+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.descripcion like 'RESOLUCION' and d.valorAlf like 'RES' and d.activo=true order by d.descripcion")
					.getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	/**
	 * Método que retorna el Estado de la solicitud
	 * @return
	 */
	private DatosEspecificos searchDatosEstadoSolicitud() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'RESPONDER SOLICITUD' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'ESTADOS SOLICITUD MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();

	}
	

	/**
	 * Método que retorna el estado del detalle de la solicitud
	 * @return
	 */
	private DatosEspecificos searchDatosEstadoSolicitudDetalle() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'SOLICITAR TRASLADO' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'ESTADOS SOLICITUD MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();

	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getNombreNivelEntidad() {
		return nombreNivelEntidad;
	}

	public void setNombreNivelEntidad(String nombreNivelEntidad) {
		this.nombreNivelEntidad = nombreNivelEntidad;
	}

	public BigDecimal getCodNivelEntidad() {
		return codNivelEntidad;
	}

	public void setCodNivelEntidad(BigDecimal codNivelEntidad) {
		this.codNivelEntidad = codNivelEntidad;
	}

	public Long getIdFuncionario() {
		return idFuncionario;
	}

	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
	}

	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}

	public void setConfiguracionUoDetList(
			List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}

	public List<SelectItem> getTipoSolicitudSelectItems() {
		return tipoSolicitudSelectItems;
	}

	public void setTipoSolicitudSelectItems(
			List<SelectItem> tipoSolicitudSelectItems) {
		this.tipoSolicitudSelectItems = tipoSolicitudSelectItems;
	}

	public Long getIdTipoSolicitud() {
		return idTipoSolicitud;
	}

	public void setIdTipoSolicitud(Long idTipoSolicitud) {
		this.idTipoSolicitud = idTipoSolicitud;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public void setSolicitudTrasladoCab(
			SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}

	public Boolean getMostrarBoton() {
		return mostrarBoton;
	}

	public void setMostrarBoton(Boolean mostrarBoton) {
		this.mostrarBoton = mostrarBoton;
	}
	
	public Boolean getMostrarCategoriaRemuneracion() {
		return mostrarCategoriaRemuneracion;
	}

	public void setMostrarCategoriaRemuneracion(Boolean mostrarCategoriaRemuneracion) {
		this.mostrarCategoriaRemuneracion = mostrarCategoriaRemuneracion;
	}
	
	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}
	
	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	
	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}

	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
	}
	
	public int getSelectedRowPuesto() {
		return selectedRowPuesto;
	}

	public void setSelectedRowPuesto(int selectedRowPuesto) {
		this.selectedRowPuesto = selectedRowPuesto;
	}
	
	public SinAnx getSinAnxSeleccionado() {
		return sinAnxSeleccionado;
	}

	public void setSinAnxSeleccionado(SinAnx sinAnxSeleccionado) {
		this.sinAnxSeleccionado = sinAnxSeleccionado;
	}
	
	public List<EmpleadoMovilidadAnexo> getConceptoPagosActual() {
		return conceptoPagosActual;
	}

	public void setConceptoPagosActual(List<EmpleadoMovilidadAnexo> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}
	
	public List<EmpleadoMovilidadAnexo> getConceptoPagosOrigen() {
		return conceptoPagosOrigen;
	}

	public void setConceptoPagosOrigen(List<EmpleadoMovilidadAnexo> conceptoPagosOrigen) {
		this.conceptoPagosOrigen = conceptoPagosOrigen;
	}
	
	public PlantaCargoDet getSolicitudPlantaCargoDet() {
		return solicitudPlantaCargoDet;
	}

	public void setSolicitudPlantaCargoDet(PlantaCargoDet solicitudPlantaCargoDet) {
		this.solicitudPlantaCargoDet = solicitudPlantaCargoDet;
	}
	
	public String getSolicitudValorObj() {
		return solicitudValorObj;
	}

	public void setSolicitudValorObj(String solicitudValorObj) {
		this.solicitudValorObj = solicitudValorObj;
	}
	
	public Integer getSolicitudCodObj() {
		return solicitudCodObj;
	}

	public void setSolicitudCodObj(Integer solicitudCodObj) {
		this.solicitudCodObj = solicitudCodObj;
	}
	
	public String getSolicitudCategoria() {
		return solicitudCategoria;
	}

	public void setCategoria(String solicitudCategoria) {
		this.solicitudCategoria = solicitudCategoria;
	}
	
	public String getSolicitudCodCategoria() {
		return solicitudCodCategoria;
	}

	public void setCodCategoria(String solicitudCodCategoria) {
		this.solicitudCodCategoria = solicitudCodCategoria;
	}
	
	public Long getSolicitudMonto() {
		return solicitudMonto;
	}

	public void setSolicitudMonto(Long solicitudMonto) {
		this.solicitudMonto = solicitudMonto;
	}
	
	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(int index) {

		plantaCargoDetList =
			em.createQuery("Select p from PlantaCargoDet p "
				+ " where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true"
				+ " and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab ").setParameter("idEstadoCab", getIdEstadoCabVacante()).setParameter("idDet", configuracionUoDetList.get(index).getIdConfiguracionUoDet()).getResultList();
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
		setearConPago();
	}
	
	public void setearDatos() {
		persona = new Persona();
		funcionario = new EmpleadoPuesto();
		idFuncionario = null;
		observacion = null;
		plantaCargoDetList = new ArrayList<PlantaCargoDet>();
		conceptoPagosActual = new Vector<EmpleadoMovilidadAnexo>();
		conceptoPagosOrigen = new Vector<EmpleadoMovilidadAnexo>();
	}
	
	private void setearConPago() {
		sinAnxSeleccionado = seleccionUtilFormController.getSinAnx();
		seleccionUtilFormController.setearValoresObjetosGasto();
	}
	
	private Long getIdEstadoCabVacante() {
		EstadoCab cab = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		return cab.getIdEstadoCab();
	}
	
	public String getRowPuestoClass(int currentRow) {
		if (selectedRowPuesto == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}
	
	public void seleccionarPuesto(Long idPuesto, int index) {
		ConfiguracionUoCab cab =
			em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPuesto));
		solicitudPlantaCargoDet = em.find(PlantaCargoDet.class, idPuesto);
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = idPuesto;
		setearConPago();
		setSelectedRowPuesto(index);
		System.out.println("MONTO ACTUAL "+montoActual()+" LIMITE SUP "+montoActual()*1.2+" LIMITE INF "+montoActual()*0.8);
	}

	public void agregar() {
		if (seleccionUtilFormController.getPlantaCargoDet() == null) {
			statusMessages.add(Severity.ERROR, "Debe Seleccionar el Puesto");
			return;
		}

		if (seleccionUtilFormController.getCodigoObj() == null
			|| seleccionUtilFormController.getValorObj() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Cod. Objeto Gasto");
			return;
		}
		
		if (seleccionUtilFormController.getMonto() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Monto");
			return;
		}
		if (seleccionUtilFormController.getMonto().intValue() < 0) {
			statusMessages.add(Severity.ERROR, "El Monto debe ser mayor a cero");
			return;
		}
		if(idTipoSolicitud == null){
			statusMessages.add(Severity.ERROR, "Elija primero Tipo de Solicitud");
			return;
		}
		else{
			DatosEspecificos tipoSolic = em.find(DatosEspecificos.class, idTipoSolicitud);
			if(tipoSolic.getDescripcion().equalsIgnoreCase("TRASLADO DEFINITIVO SIN LINEAS DE CARGO")){
				if (!condicionMontos()) {
					statusMessages.add(Severity.ERROR, "El Monto debe estar en un rango del +-20% del monto actual ("+montoActual()+" Gs)");
					return;
				}
				if (!condicionNiveles()) {
					statusMessages.add(Severity.ERROR, "El Nivel del puesto nuevo debe ser igual al del puesto actual "+cptFuncionario().getDenominacion()+
							" de nivel "+cptFuncionario().getNivel());
					return;
				}
			}
		}
		
		EmpleadoMovilidadAnexo empleadoMovilidadAnexo = new EmpleadoMovilidadAnexo();
		empleadoMovilidadAnexo.setObjCodigo(seleccionUtilFormController.getCodigoObj());
		if (seleccionUtilFormController.getCategoria() != null)
			empleadoMovilidadAnexo.setCategoria(seleccionUtilFormController.getCodigoCategoria());
		empleadoMovilidadAnexo.setMonto(seleccionUtilFormController.getMonto());
		conceptoPagosActual.add(empleadoMovilidadAnexo);
		
		solicitudValorObj = seleccionUtilFormController.getValorObj();
		solicitudCodObj = seleccionUtilFormController.getCodigoObj();
		solicitudCategoria = seleccionUtilFormController.getCategoria();
		solicitudCodCategoria = seleccionUtilFormController.getCodigoCategoria();
		solicitudMonto = seleccionUtilFormController.getMonto().longValue();
		
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		setearConPago();
	}
	
	public boolean condicionMontos(){
		double montoFuncionario = montoActual();
		double montoSup = montoFuncionario*1.2;
		double montoInf = montoFuncionario*0.8;
			
		if((seleccionUtilFormController.getMonto().longValue() <= montoSup) && (seleccionUtilFormController.getMonto().longValue() >= montoInf)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public double montoActual(){
		String sqlMontos = "select * from general.empleado_concepto_pago where id_empleado_puesto = "+funcionario.getIdEmpleadoPuesto()+
				" and obj_codigo = 111 and activo = 'TRUE' and actual = 'TRUE';";
		Query qMontos = em.createNativeQuery(sqlMontos, EmpleadoConceptoPago.class);
		List<EmpleadoConceptoPago> listaMontos = qMontos.getResultList();

		if(listaMontos.size() > 0){
			return listaMontos.get(0).getMonto().doubleValue();
		}
		else
		{
			System.out.println("EMPLEADO_CONCEPTO_PAGO VACIO");
			return 0;
		}
			
	}
	
	public boolean condicionNiveles(){
		int nivelActual, nivelNuevo;
		Cpt cptActual = funcionario.getPlantaCargoDet().getCpt();
		Cpt cptNuevo = seleccionUtilFormController.getPlantaCargoDet().getCpt();
		
		if(cptActual.getCptPadre() != null){
			nivelActual = cptActual.getCptPadre().getNivel();
		}
		else{
			nivelActual = cptActual.getNivel();
		}
		
		if(cptNuevo.getCptPadre() != null){
			nivelNuevo = cptNuevo.getCptPadre().getNivel();
		}
		else{
			nivelNuevo = cptNuevo.getNivel();
		}
		System.out.println("NIVEL ACTUAL "+nivelActual+" NIVEL NUEVO "+nivelNuevo);
		
		if(nivelActual == nivelNuevo){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Cpt cptFuncionario(){
		Cpt cptActual = funcionario.getPlantaCargoDet().getCpt();
		
		if(cptActual.getCptPadre() != null){
			return cptActual.getCptPadre();
		}
		else{
			return cptActual;
		}
	}
	
	public void eliminar(int index) {
		conceptoPagosActual.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}
	
	public void setRenderCategoriasRemuneraciones(){
		DatosEspecificos tipoSolic = em.find(DatosEspecificos.class, idTipoSolicitud);
		if(tipoSolic.getDescripcion().equalsIgnoreCase("TRASLADO DEFINITIVO CON LINEAS DE CARGO")){
			mostrarCategoriaRemuneracion = false;
		}
		else{
			mostrarCategoriaRemuneracion = true;
		}
	}
	
	public ActividadProceso actividadProcesoActual(){
		String sqlActProc = "select * from proceso.actividad_proceso act_proc join proceso.actividad act on act_proc.id_actividad = act.id_actividad "
				+"where act.descripcion = 'SOLICITAR TRASLADO';";
		Query qActProc = em.createNativeQuery(sqlActProc, ActividadProceso.class);
		List<ActividadProceso> listaActProc = qActProc.getResultList();

		if(listaActProc.size() > 0){
			return listaActProc.get(0);
		}
		else
		{
			return null;
		}
	}

}
