package py.com.excelsis.sicca.movilidadLaboral.session.form;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
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
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;
import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.RespuestaSolicitudEnums;
import enums.TiposDatos;

@Name("respuestaSolicitudTranslado732FC")
@Scope(ScopeType.CONVERSATION)
public class RespuestaSolicitudTranslado732FC implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;



	private Long idRedCap;

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(create = true,required=false)
	JasperReportUtils jasperReportUtils;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(create=true)
	ValidadorController validadorController;
	
	@In(create=true)
	DatosEspecificosHome  datosEspecificosHome;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	
	@In(create=true)
	BuscadorDocsFC buscadorDocsFC;


	
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private boolean habUpdate=false;
	private Persona persona= new Persona();
	private Long idPersona=null;
	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private Integer monto;
	private EmpleadoPuesto empleadoPuesto;
	private EmpleadoPuesto funcionario;
	private Long idFuncionario;
	private List<ReferenciaAdjuntos> adjuntos= new ArrayList<ReferenciaAdjuntos>();
	private Date fechaInicio;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private String observacion;
		private Date fechaFin;
	private Long idSolicitudTrasladoCab ;
	private EmpleadoMovilidadCab empleadoMovilidadCabNuevasFunciones=new EmpleadoMovilidadCab();
	private SolicitudTrasladoCab solicitudTrasladoCab= new SolicitudTrasladoCab();
	private String from;
	private String concurso;
	private RespuestaSolicitudEnums respuestaSolicitud=RespuestaSolicitudEnums.Todos;
	private List<SolicitudTrasladoDet> solicitudTraslaDetLista= new ArrayList<SolicitudTrasladoDet>();
	private List<EmpleadoMovilidadAnexo> conceptoPagosActual = new ArrayList<EmpleadoMovilidadAnexo>();
	private List<PlantaCargoDet> plantaCargoDetList = new ArrayList<PlantaCargoDet>();
	
	public void init() throws Exception {
		
		if (primeraEntrada) {
			em.clear();
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
		}		
		
		if(idSolicitudTrasladoCab!=null){
			solicitudTrasladoCab= em.find(SolicitudTrasladoCab.class, idSolicitudTrasladoCab);
			idPersona=solicitudTrasladoCab.getPersona().getIdPersona();
			persona=em.find(Persona.class, idPersona);
			cargarFuncionario();
			EmpleadoMovilidadAnexo empleadoAnex = new EmpleadoMovilidadAnexo();
			if(!solicitudTrasladoCab.getDatosEspTipoTranslado().getDescripcion().equalsIgnoreCase("TRASLADO DEFINITIVO CON LINEAS DE CARGO")){
				empleadoAnex.setDescripcion(solicitudTrasladoCab.getValorObj());
				empleadoAnex.setCategoria(solicitudTrasladoCab.getCategoria());
				empleadoAnex.setMonto(Integer.parseInt(solicitudTrasladoCab.getMonto().toString()));
			}
			conceptoPagosActual.add(empleadoAnex);
			plantaCargoDetList.add(solicitudTrasladoCab.getPlantaCargoDet());
			
			if(!funcionario.getPlantaCargoDet().getPermanente()){
				statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
				funcionario= new EmpleadoPuesto();
				persona= new Persona();
				return;
			}
			cargarNEOFuncionario();
			cargarNiveentidadOee();
			cargarAdjuntos();
			cargarEstado();
			
		}		
		nivelEntidadOeeUtil.init();
	}
	@SuppressWarnings("unchecked")
	private void cargarEstado(){
		solicitudTraslaDetLista=em.createQuery("Select d from SolicitudTrasladoDet d where " +
				"d.solicitudTrasladoCab.idSolicitudTrasladoCab=:idSolicitudTrasladoCab").setParameter("idSolicitudTrasladoCab", idSolicitudTrasladoCab).getResultList();
	}
	/**
	 * Carga todos los adjuntos del idSolicitudTrasladoCab Recibido 
	 * */
	@SuppressWarnings("unchecked")
	private void cargarAdjuntos(){
		adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
				" where r.idRegistroTabla="+idSolicitudTrasladoCab+" and r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO'").getResultList();
	}
	/**
	 * Cargar el funcionario de acuerdo con la persona recibida
	 * */
	private void cargarFuncionario(){
		try {
			funcionario=(EmpleadoPuesto)em.createQuery("Select d from EmpleadoPuesto d " +
					" where d.persona.idPersona=:idPersona and d.actual=true and d.activo=true").setParameter("idPersona",idPersona).getResultList().get(0);
			idFuncionario=funcionario.getIdEmpleadoPuesto();
			if(funcionario.getConcursoPuestoAgr()!=null)
			{
				Concurso co=em.find(Concurso.class, funcionario.getConcursoPuestoAgr().getConcurso().getIdConcurso());
				concurso=co.getNombre();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * Carga el Nivel,Entidad,OEe del funcionario 
	 * */
	private void cargarNEOFuncionario(){
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilAnterior.setIdUnidadOrganizativa(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet());
		entidadOeeUtilAnterior.init2();
	}

	@SuppressWarnings("unchecked")
	public void initVer() throws Exception{
		
			 solicitudTrasladoCab= em.find(SolicitudTrasladoCab.class, idSolicitudTrasladoCab);
			idPersona=solicitudTrasladoCab.getPersona().getIdPersona();
			persona=em.find(Persona.class, idPersona);
			cargarFuncionario();
			idEmpleadoPuesto=idFuncionario;
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			ConfiguracionUoDet uoDet=em.find(ConfiguracionUoDet.class, empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla=:idSolicitudTrasladoCab").setParameter("idSolicitudTrasladoCab", idSolicitudTrasladoCab).getResultList();
		
			solicitudTraslaDetLista=em.createQuery("Select d from SolicitudTrasladoDet d where" +
					" d.solicitudTrasladoCab.idSolicitudTrasladoCab=:idSolicitudTrasladoCab and d.activo=true ")
			.setParameter("idSolicitudTrasladoCab", idSolicitudTrasladoCab).getResultList();
	}
	/**
	 * Carga en Nivel la entidad Oee del Usuario logueado
	 * */
	private void cargarNiveentidadOee(){
		try {
				nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
		} catch (Exception e) {
			
		}
		
	}
	

	
	
	
	
	/**
	 * Medoto que llama a la pantalla de ver persona
	 * accedido desde el ver
	 * */
	public String toFindPersonaView() {
		
		String from="";
		
		from="/movilidadLaboral/respuestaSolicitudTranslado/RespuestaSolicitudTranslado732Ver";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+funcionario.getPersona().getIdPersona()+"&conversationPropagation=join";
	}
	
	

		
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				/***
				 * 	Actualiza registro en la tabla SOLICITUD_MOVILIDAD_CAB 
				 * */
				solicitudTrasladoCab.setOeeOrigen(usuarioLogueado.getConfiguracionUoCab());
				if(respuestaSolicitud.getValor().equals("DENEGADA")){
					solicitudTrasladoCab.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","GESTION FINALIZADA"));
					//finaliza
					jbpmUtilFormController.setTransition("regTraTem_TO_end");
				}else{
					DatosEspecificos tipotraslado=em.find(DatosEspecificos.class, solicitudTrasladoCab.getDatosEspTipoTranslado().getIdDatosEspecificos());
					
					if(tipotraslado.getDescripcion().equalsIgnoreCase("TRASLADO TEMPORAL (COMISIONAMIENTO)")){
						solicitudTrasladoCab.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","REGISTRAR TRASLADO TEMPORAL"));
						//Pasa a traslado temporal
						jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.MOV_REGISTRAR_TRASLADO_TEMPORAL);
						jbpmUtilFormController.setTransition("respSol_TO_regTraTem");
					}else if(tipotraslado.getDescripcion().equalsIgnoreCase("TRASLADO DEFINITIVO SIN LINEAS DE CARGO")){
						solicitudTrasladoCab.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","REGISTRAR TRASLADO SIN LINEA"));
						//Pasa a traslado sin linea
						jbpmUtilFormController.setTransition("resSol_TO_regTraDefSinLin");
						jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.MOV_REGISTRAR_TRASLADO_CON_LINEA);
					}else if(tipotraslado.getDescripcion().equalsIgnoreCase("TRASLADO DEFINITIVO CON LINEAS DE CARGO")){
						solicitudTrasladoCab.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","REVISAR SOLICITUD SFP"));
						//Pasa a traslado con linea
						jbpmUtilFormController.setTransition("resSol_TO_revSolSfp");
						jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.MOV_REVISAR_SOLICITUD_SFP);
					}
					
				}
				solicitudTrasladoCab.setRespuestaOeeOrigen(respuestaSolicitud.getValor());
				solicitudTrasladoCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
				solicitudTrasladoCab.setFechaMod(new Date());
				em.merge(solicitudTrasladoCab);

				/***
				 *
				 * 	Si está cargada la observación, Insertar registro en la tabla SOLICITUD_TRASLADO_DET
				 * */
				if(observacion!=null && !observacion.trim().equals("")){
					SolicitudTrasladoDet solicitudTrasladoDet= new SolicitudTrasladoDet();
					solicitudTrasladoDet.setSolicitudTrasladoCab(solicitudTrasladoCab);
					solicitudTrasladoDet.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","RESPONDER SOLICITUD"));
					solicitudTrasladoDet.setObservacion(observacion);
					solicitudTrasladoDet.setFechaAlta(new Date());
					solicitudTrasladoDet.setUsuAlta( usuarioLogueado.getCodigoUsuario());
					em.persist(solicitudTrasladoDet);
				}
				
				 Long idDoc=null;
				 if(buscadorDocsFC.getFileActoAdmin()!=null){
					idDoc= guardarAdjuntos(buscadorDocsFC.getfName(), buscadorDocsFC.getFileActoAdmin().getFileSize(), buscadorDocsFC.getFileActoAdmin().getContentType(), buscadorDocsFC.getFileActoAdmin(), buscadorDocsFC.getIdTipoDoc(), null);
						if(idDoc==null)
							return null;
						 Documentos doc= em.find(Documentos.class, idDoc);
						 doc.setNroDocumento(buscadorDocsFC.getNroDoc());
						 doc.setAnhoDocumento(Integer.parseInt(sdfAnio.format(buscadorDocsFC.getFechaDoc())));
						 doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
						 em.merge(doc);
					 
				 }
				 /**
				  *  se inserta en tabla de referencia
				  * */
				 
				 insertarRerAdjunto(idDoc);
			
						/**
						 *	Finaliza la tarea y el circuito de traslado 
						 * */
						
							 /**
							  * GESTIONS SIGT. TAREA
							  * */
						
						jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
						jbpmUtilFormController.setActividadActual(ActividadEnum.MOV_RESPONDER_TRASLADO);
						jbpmUtilFormController.setSolicitudTrasladoCab(solicitudTrasladoCab);

						if(!jbpmUtilFormController.nextTask())	 
							return null;
						
				em.flush();
				setearDatos();
				primeraEntrada=true;
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
				
				return "next";
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				e.printStackTrace();
				return null;
			}
		
			
		}
	
		
		public List<SelectItem> updateTipoDocSelectItems() {
			List<SelectItem> si = new Vector<SelectItem>();
			si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for (DatosEspecificos o : datosEspecificosByTipoDocumento())
				si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
			return si;
		}

		/**
		 * Carga el tipo de documento
		 * */
		@SuppressWarnings("unchecked")
		public List<DatosEspecificos> datosEspecificosByTipoDocumento() {
			try {
				List<DatosEspecificos> datosEspecificosLists =
					em.createQuery("Select d from DatosEspecificos d "
						+ " where d.descripcion like 'RESOLUCION' and d.valorAlf like 'RES' and d.activo=true order by d.descripcion").getResultList();

				return datosEspecificosLists;
			} catch (Exception ex) {
				return new Vector<DatosEspecificos>();
			}
		}
		
	
		
		
		/**
		 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto administrativo
		 * */
		private void insertarRerAdjunto(Long idDocumento){
			ReferenciaAdjuntos referenciaAdjuntos= new ReferenciaAdjuntos();
			referenciaAdjuntos.setPersona(persona);
			referenciaAdjuntos.setDocumentos(em.find(Documentos.class, idDocumento));
			//referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
			referenciaAdjuntos.setIdRegistroTabla(solicitudTrasladoCab.getIdSolicitudTrasladoCab());
			referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			referenciaAdjuntos.setFechaAlta(new Date());
			if(actividadProcesoRespuesta() != null){
				referenciaAdjuntos.setActividadProceso(actividadProcesoRespuesta());
			}
			em.persist(referenciaAdjuntos);
		}
		
		/**
		 * Guarda los adjuntos con los parametros correspondiente
		 * */
		private Long guardarAdjuntos(String fileName, int fileSize, String contentType,UploadItem file, Long idTipoDoc, Long idDocumento) {
			try {
				
				String anio=sdfAnio.format(new Date());
				Long idDocuGenerado;
				String nombreTabla = "SolicitudTrasladoCab";
				String nombrePantalla = "respuestaSolicitudTranslado732";
				String direccionFisica = "";
				String sf=File.separator;
				direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+nivelEntidadOeeUtil.getIdConfigCab()+sf+"MOVILIDAD";
				
				idDocuGenerado =
					admDocAdjuntoFormController.guardarDoc(file, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
				return idDocuGenerado;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	
		
		
		
		

		/**
		 * Verifica que cumpla las validaciones antes de guardar
		 * */
		private boolean chkDatos(String op){
			if(!funcionario.getPlantaCargoDet().getPermanente()){
				statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
				funcionario= new EmpleadoPuesto();
				persona= new Persona();
				return false;
			}
			if(buscadorDocsFC.getNroDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el numero de documento");
				return false;
			}
			if(buscadorDocsFC.getFechaDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la fecha  de documento");
				return false;
			}
			if(buscadorDocsFC.getIdTipoDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el tipo de documento");
				return false;
			}
			if(buscadorDocsFC.getFileActoAdmin()==null){
				statusMessages.add(Severity.ERROR,"Debe Inrgesar un archivo, verifique");
				return false;
			}
			
			if(respuestaSolicitud.getValor()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una respuesta");
				return  false;
			}
			
			
			return true;
		}
		
		
		
		
	
		
		
	
		private void setearDatos(){
			persona= new Persona();
			funcionario= new EmpleadoPuesto();
			idFuncionario=null;
			observacion=null;
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
			nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
			buscadorDocsFC.cambiarActo();
			respuestaSolicitud=RespuestaSolicitudEnums.Todos;
			
		}
		
		/**
		 * Descargar el documento atraves del id recibido como parametro
		 * */
	@SuppressWarnings("static-access")
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
	

	
		
	
	
	

		
		
	// GETTER Y SETTER

	
	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public boolean isHabUpdate() {
		return habUpdate;
	}

	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public Long getIdRedCap() {
		return idRedCap;
	}

	public void setIdRedCap(Long idRedCap) {
		this.idRedCap = idRedCap;
	}




	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	
	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}



	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}
	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}
	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}
	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}
	public void setConfiguracionUoDetList(
			List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}
	
	public Integer getMonto() {
		return monto;
	}
	public void setMonto(Integer monto) {
		this.monto = monto;
	}


	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}
	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}
	public Long getIdFuncionario() {
		return idFuncionario;
	}
	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}
	public void setEntidadOeeUtilAnterior(NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}
	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}
	
	
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	public Long getIdSolicitudTrasladoCab() {
		return idSolicitudTrasladoCab;
	}
	public void setIdSolicitudTrasladoCab(Long idSolicitudTrasladoCab) {
		this.idSolicitudTrasladoCab = idSolicitudTrasladoCab;
	}
	public EmpleadoMovilidadCab getEmpleadoMovilidadCabNuevasFunciones() {
		return empleadoMovilidadCabNuevasFunciones;
	}
	public void setEmpleadoMovilidadCabNuevasFunciones(
			EmpleadoMovilidadCab empleadoMovilidadCabNuevasFunciones) {
		this.empleadoMovilidadCabNuevasFunciones = empleadoMovilidadCabNuevasFunciones;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getConcurso() {
		return concurso;
	}
	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}
	public RespuestaSolicitudEnums getRespuestaSolicitud() {
		return respuestaSolicitud;
	}
	public void setRespuestaSolicitud(RespuestaSolicitudEnums respuestaSolicitud) {
		this.respuestaSolicitud = respuestaSolicitud;
	}
	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}
	public void setSolicitudTrasladoCab(SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}
	public List<SolicitudTrasladoDet> getSolicitudTraslaDetLista() {
		return solicitudTraslaDetLista;
	}
	public void setSolicitudTraslaDetLista(
			List<SolicitudTrasladoDet> solicitudTraslaDetLista) {
		this.solicitudTraslaDetLista = solicitudTraslaDetLista;
	}

	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}

	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
	}
	
	public List<EmpleadoMovilidadAnexo> getConceptoPagosActual() {
		return conceptoPagosActual;
	}

	public void setConceptoPagosActual(List<EmpleadoMovilidadAnexo> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}
	
	public Boolean mostrarCampos(){
		if(solicitudTrasladoCab.getDatosEspTipoTranslado().getDescripcion().equalsIgnoreCase("TRASLADO DEFINITIVO CON LINEAS DE CARGO")){
			return false;
		}
		else{
			return true;
		}
	}
	
	public ActividadProceso actividadProcesoRespuesta(){
		String sqlActProc = "select * from proceso.actividad_proceso act_proc join proceso.actividad act on act_proc.id_actividad = act.id_actividad "
				+"where act.descripcion = 'RESPONDER TRASLADO';";
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
