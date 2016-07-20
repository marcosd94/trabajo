package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.entity.SolicitudTrasladoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("rptaSolicitudSfpCU733FC")
@Scope(ScopeType.CONVERSATION)
public class RptaSolicitudSfpCU733FC {

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
	BuscadorDocsFC buscadorDocsFC;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private SolicitudTrasladoCab solicitudTrasladoCab = new SolicitudTrasladoCab();
	private Persona persona = new Persona();
	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
	private Long idSolicitud;
	private Long idRpta;
	private BigDecimal codSinEntidad;
	private String nombreSinEntidad;
	private BigDecimal codNivelEntidad;
	private String nombreNivelEntidad;
	private Integer ordenUnidOrg;
	private String denominacionUnidad;
	private String observacion;
	private SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private NivelEntidadOeeUtil nivelEntidadOeeUtilPersona;
	private String linea;
	private String descripcion;

	private List<ReferenciaAdjuntos> adjuntos = new ArrayList<ReferenciaAdjuntos>();
	private List<SolicitudTrasladoDet> listaDetalles = new ArrayList<SolicitudTrasladoDet>();
	private List<SelectItem> respuestaSfpSelectItems = new ArrayList<SelectItem>();
	

	private List<EmpleadoMovilidadAnexo> conceptoPagosActual = new ArrayList<EmpleadoMovilidadAnexo>();
	private String solicitudValorObj;
	private Integer solicitudCodObj;
	private String solicitudCategoria;
	private String solicitudCodCategoria;
	private Long solicitudMonto;
	private SinAnx sinAnxSeleccionado = null;

	public void init() {
		if (idSolicitud != null) {
			solicitudTrasladoCab = em.find(SolicitudTrasladoCab.class,
					idSolicitud);
			seleccionUtilFormController.setPlantaCargoDet(solicitudTrasladoCab.getPlantaCargoDet());
			cargarNiveentidadOee();
			cargarDatos();
			cargarSelectItems();
		}
	}
	
	public void initVer(){
		if (idSolicitud != null) {
			solicitudTrasladoCab = em.find(SolicitudTrasladoCab.class,
					idSolicitud);
			cargarNiveentidadOee();
			cargarDatos();
			cargarSelectItems();
		}
	}

	private void cargarDatos() {
		persona = solicitudTrasladoCab.getPersona();
		
		try {
			adjuntos = em.createQuery(
					"Select r from ReferenciaAdjuntos r "
							+ " where r.idRegistroTabla="
							+ solicitudTrasladoCab.getIdSolicitudTrasladoCab()
							+" and (r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO' or r.actividadProceso.actividad.descripcion='RESPONDER TRASLADO')")
					.getResultList();

			listaDetalles = em
					.createQuery(
							"Select det from SolicitudTrasladoDet det "
									+ " where det.solicitudTrasladoCab.idSolicitudTrasladoCab="
									+ solicitudTrasladoCab
											.getIdSolicitudTrasladoCab()
									+ " and det.activo is true")
					.getResultList();

			String sqlEmpleadoPuesto = "select * from general.empleado_puesto where empleado_puesto.activo = TRUE and empleado_puesto.actual = TRUE and empleado_puesto.id_persona = "+persona.getIdPersona();
			Query q1 = em.createNativeQuery(sqlEmpleadoPuesto, EmpleadoPuesto.class);
			List<EmpleadoPuesto> l = q1.getResultList();
			
			if (!l.isEmpty())
				empleadoPuesto = l.get(0);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public void agregar() {
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
	
	private void setearConPago() {
		sinAnxSeleccionado = seleccionUtilFormController.getSinAnx();
		seleccionUtilFormController.setearValoresObjetosGasto();
	}

	private void cargarNiveentidadOee() {

		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

		codNivelEntidad = nivelEntidadOeeUtil.getCodNivelEntidad();
		nombreNivelEntidad = nivelEntidadOeeUtil.getNombreNivelEntidad();
		codSinEntidad = nivelEntidadOeeUtil.getCodSinEntidad();
		nombreSinEntidad = nivelEntidadOeeUtil.getNombreSinEntidad();
		denominacionUnidad = nivelEntidadOeeUtil.getDenominacionUnidad();
		ordenUnidOrg = nivelEntidadOeeUtil.getOrdenUnidOrg();
		if (nivelEntidadOeeUtilPersona == null)
			nivelEntidadOeeUtilPersona = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
		nivelEntidadOeeUtilPersona.setEm(em);
		Long id = solicitudTrasladoCab.getOeeOrigen().getIdConfiguracionUo();
		nivelEntidadOeeUtilPersona.setIdConfigCab(solicitudTrasladoCab
				.getOeeOrigen().getIdConfiguracionUo());
		nivelEntidadOeeUtilPersona.init2();

	}

	private void cargarSelectItems() {
		respuestaSfpSelectItems = new Vector<SelectItem>();
		respuestaSfpSelectItems.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		respuestaSfpSelectItems.add(new SelectItem(1, "APROBADA SFP"));
		respuestaSfpSelectItems.add(new SelectItem(2, "DENEGADA SFP"));
	}

	public void descargarDocBD(Long id) throws FileNotFoundException,
			IOException {
		Documentos doc = em.find(Documentos.class, id);
		if (doc.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(
					doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	public String toFindPersonaView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Funcionario");
			return null;
		}
		String from = "/movilidadLaboral/rptaSolicitudSfp/RptaSolicitudSfpCU733";
		return "/seleccion/persona/Persona.xhtml?personaFrom=" + from
				+ "&personaIdPersona=" + persona.getIdPersona()
				+ "&conversationPropagation=join";
	}

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

		if (idRpta == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar la Respuesta de SFP");
			return false;
		}
		return true;
	}

	public String save() {
		try {
			if (!chkDatos())
				return null;
			if (idRpta.intValue() == 1) {
				solicitudTrasladoCab.setDatosEspEstadoSolicitud(searchDatosEsp(
						"ESTADOS SOLICITUD MOVILIDAD",
						"REGISTRAR TRASLADO CON LINEA"));
				solicitudTrasladoCab.setRespuestaSFP("APROBADA SFP");
				solicitudTrasladoCab.setUsuMod(usuarioLogueado
						.getCodigoUsuario());
				solicitudTrasladoCab.setFechaMod(new Date());
				solicitudTrasladoCab.setValorObj(solicitudValorObj);
				solicitudTrasladoCab.setCodObjeto(seleccionUtilFormController.getCodigoObj());
				solicitudTrasladoCab.setCodCategoria(seleccionUtilFormController.getCodigoCategoria());
				solicitudTrasladoCab.setCategoria(solicitudCategoria);
				solicitudTrasladoCab.setMonto(solicitudMonto);
				solicitudTrasladoCab.setLinea(linea);
				solicitudTrasladoCab.setDescripcion(descripcion);
				em.merge(solicitudTrasladoCab);
				if (observacion != null && !observacion.trim().isEmpty()) {
					SolicitudTrasladoDet detalle = new SolicitudTrasladoDet();
					detalle.setActivo(true);
					detalle.setDatosEspEstadoSolicitud(searchDatosEsp(
							"ESTADOS SOLICITUD MOVILIDAD",
							"REVISAR SOLICITUD SFP"));
					detalle.setFechaAlta(new Date());
					detalle.setObservacion(observacion.trim());
					detalle.setSolicitudTrasladoCab(solicitudTrasladoCab);
					detalle.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(detalle);
				}
			}

			if (idRpta.intValue() == 2) {
				solicitudTrasladoCab.setDatosEspEstadoSolicitud(searchDatosEsp(
						"ESTADOS SOLICITUD MOVILIDAD", "GESTION FINALIZADA"));
				solicitudTrasladoCab.setRespuestaSFP("DENEGADA SFP");
				solicitudTrasladoCab.setUsuMod(usuarioLogueado
						.getCodigoUsuario());
				solicitudTrasladoCab.setFechaMod(new Date());
				em.merge(solicitudTrasladoCab);
				if (observacion != null && !observacion.trim().isEmpty()) {
					SolicitudTrasladoDet detalle = new SolicitudTrasladoDet();
					detalle.setActivo(true);
					detalle.setDatosEspEstadoSolicitud(searchDatosEsp(
							"ESTADOS SOLICITUD MOVILIDAD",
							"REVISAR SOLICITUD SFP"));
					detalle.setFechaAlta(new Date());
					detalle.setObservacion(observacion.trim());
					detalle.setSolicitudTrasladoCab(solicitudTrasladoCab);
					detalle.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(detalle);
				}
			}

			Long idDoc = null;
			if (buscadorDocsFC.getFileActoAdmin() != null) {
				if (buscadorDocsFC.getDocDecreto() == null) {
					idDoc = guardarAdjuntos(buscadorDocsFC.getfName(),
							buscadorDocsFC.getFileActoAdmin().getFileSize(),
							buscadorDocsFC.getFileActoAdmin().getContentType(),
							buscadorDocsFC.getFileActoAdmin(),
							buscadorDocsFC.getIdTipoDoc(), null);
					if (idDoc == null)
						return null;
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setNroDocumento(buscadorDocsFC.getNroDoc());
					doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
					doc.setAnhoDocumento(Integer.parseInt(sdfAnio
							.format(buscadorDocsFC.getFechaDoc())));
					doc.setConfiguracionUoCab(usuarioLogueado
							.getConfiguracionUoCab());

					em.merge(doc);
					solicitudTrasladoCab.setDocumentoSfp(doc);
				}
			}

			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */
			if (idDoc == null)
				idDoc = buscadorDocsFC.getDocDecreto().getIdDocumento();
			insertarAdjunto(idDoc);
			if (idRpta.intValue() == 1) {
				jbpmUtilFormController
						.setActividadActual(ActividadEnum.MOV_RESPONDER_TRASLADO);
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.MOV_REVISAR_SOLICITUD_SFP);
				jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
				jbpmUtilFormController.setTransition("revSolSfp_TO_regTraDefConLin");
			}

			if (idRpta.intValue() == 2) {
				jbpmUtilFormController
						.setActividadActual(ActividadEnum.MOV_RESPONDER_TRASLADO);

				jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
				jbpmUtilFormController.setTransition("revSolSfp_TO_end");
			
			}
			jbpmUtilFormController.setSolicitudTrasladoCab(solicitudTrasladoCab);
			// Se pasa a la siguiente tarea
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "next";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "SolicitudTrasladoCab";
			String nombrePantalla = "rptaSolicitudSfpCU733";// cambiar
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
		if(actividadProcesoActual() != null){
			referenciaAdjuntos.setActividadProceso(actividadProcesoActual());
		}
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}

	private DatosEspecificos searchDatosEsp(String gral, String especifico) {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = '"
						+ especifico
						+ "' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = '"
						+ gral + "'");
		return (DatosEspecificos) q.getSingleResult();

	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public void setSolicitudTrasladoCab(
			SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Long idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilPersona() {
		return nivelEntidadOeeUtilPersona;
	}

	public void setNivelEntidadOeeUtilPersona(
			NivelEntidadOeeUtil nivelEntidadOeeUtilPersona) {
		this.nivelEntidadOeeUtilPersona = nivelEntidadOeeUtilPersona;
	}

	public BigDecimal getCodSinEntidad() {
		return codSinEntidad;
	}

	public void setCodSinEntidad(BigDecimal codSinEntidad) {
		this.codSinEntidad = codSinEntidad;
	}

	public String getNombreSinEntidad() {
		return nombreSinEntidad;
	}

	public void setNombreSinEntidad(String nombreSinEntidad) {
		this.nombreSinEntidad = nombreSinEntidad;
	}

	public BigDecimal getCodNivelEntidad() {
		return codNivelEntidad;
	}

	public void setCodNivelEntidad(BigDecimal codNivelEntidad) {
		this.codNivelEntidad = codNivelEntidad;
	}

	public String getNombreNivelEntidad() {
		return nombreNivelEntidad;
	}

	public void setNombreNivelEntidad(String nombreNivelEntidad) {
		this.nombreNivelEntidad = nombreNivelEntidad;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public String getDenominacionUnidad() {
		return denominacionUnidad;
	}

	public void setDenominacionUnidad(String denominacionUnidad) {
		this.denominacionUnidad = denominacionUnidad;
	}

	public List<SelectItem> getRespuestaSfpSelectItems() {
		return respuestaSfpSelectItems;
	}

	public void setRespuestaSfpSelectItems(
			List<SelectItem> respuestaSfpSelectItems) {
		this.respuestaSfpSelectItems = respuestaSfpSelectItems;
	}

	public Long getIdRpta() {
		return idRpta;
	}

	public void setIdRpta(Long idRpta) {
		this.idRpta = idRpta;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<SolicitudTrasladoDet> getListaDetalles() {
		return listaDetalles;
	}

	public void setListaDetalles(List<SolicitudTrasladoDet> listaDetalles) {
		this.listaDetalles = listaDetalles;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
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
	
	public void eliminar(int index) {
		conceptoPagosActual.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}
	
	public ActividadProceso actividadProcesoActual(){
		String sqlActProc = "select * from proceso.actividad_proceso act_proc join proceso.actividad act on act_proc.id_actividad = act.id_actividad "
				+"where act.descripcion = 'REVISAR SOLICITUD SFP';";
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
