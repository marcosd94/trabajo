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
import org.jfree.data.time.MovingAverage;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
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

@Name("transladoConSolic662EditFC")
@Scope(ScopeType.CONVERSATION)
public class TransladoConSolic662EditFC implements Serializable {

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
	



	
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private NivelEntidadOeeUtil entidadOeeUtilUsuario;
	private boolean habUpdate=false;
	private Persona persona= new Persona();
	private Long idPersona=null;
	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList=new ArrayList<PlantaCargoDet>();
	private Integer monto;
	private EmpleadoPuesto empleadoPuesto;
	private EmpleadoPuesto funcionario;
	private Long idFuncionario;
	private List<ReferenciaAdjuntos> adjuntos= new ArrayList<ReferenciaAdjuntos>();
	private Date fechaInicio;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView= new PlantaCargoDet();
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private SinAnx sinAnxSeleccionado=null;
	private String observacion;
	private List<EmpleadoConceptoPago> conceptoPagosActual= new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> conceptoPagosOrigen= new ArrayList<EmpleadoConceptoPago>();
	private BuscadorDocsFC buscadorDocsDesctino;
	private Integer nroDoc;
	private Date fechaDoc;
	private Long idTipoDoc;
	private String fNameMostrar;
	private boolean habDescargar;
	private String fName;
	private byte[] uFile = null;
	private String cType = null;
	private Documentos docDecreto;
	private Documentos docActoAdmin;
	private UploadItem fileActoAdmin;
	private List<PlantaCargoDet> plantaCargoDetNuevasFuncionesList=new ArrayList<PlantaCargoDet>();
	private Long idPlantaCargoDetNuevasFunciones;
	private Date fechaFin;
	private Long idSolicitudTransladoCab ;
	private EmpleadoMovilidadCab empleadoMovilidadCabNuevasFunciones=new EmpleadoMovilidadCab();
	private SolicitudTrasladoCab solicitudTrasladoCab= new SolicitudTrasladoCab();
	private String from;
	private Boolean mostrarPanelAdjunto = false;
	private boolean habAdjuntar;
	private boolean comisionamiento=false;
	
	public void init() throws Exception {
		
		if (entidadOeeUtilUsuario == null)
			entidadOeeUtilUsuario =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		if (buscadorDocsDesctino == null)
			buscadorDocsDesctino =
				(BuscadorDocsFC) Component.getInstance(BuscadorDocsFC.class, true);
	
		buscadorDocsDesctino.setMovilidad(true);
		
		entidadOeeUtilUsuario.setEm(em);
		if (primeraEntrada) {
			em.clear();
			cargarNiveentidadOee();
			selectedRow=-1;
			selectedRowPuesto=-1;
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
			
		}		
		
		if(idSolicitudTransladoCab!=null){
			 solicitudTrasladoCab= em.find(SolicitudTrasladoCab.class, idSolicitudTransladoCab);
			idPersona=solicitudTrasladoCab.getPersona().getIdPersona();
			persona=em.find(Persona.class, idPersona);
			cargarFuncionario();			
			if(!funcionario.getPlantaCargoDet().getPermanente()){
				statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
				funcionario= new EmpleadoPuesto();
				return;
			}else{
				cargarNEOFuncionario();
				obtenerCatOrigen();
			}
			seleccionarPuestoInit(solicitudTrasladoCab.getPlantaCargoDet());
			seleccionUtilFormController.setCodigoObj(solicitudTrasladoCab.getCodObjeto());
			seleccionUtilFormController.setCodigoCategoria(solicitudTrasladoCab.getCodCategoria());
			seleccionUtilFormController.setValorObj(solicitudTrasladoCab.getValorObj());
			seleccionUtilFormController.setCategoria(solicitudTrasladoCab.getCategoria());
			seleccionUtilFormController.setMonto(solicitudTrasladoCab.getMonto().intValue());
			agregarInit();
			
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla="+idSolicitudTransladoCab+" and (r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO'"+
					" or r.actividadProceso.actividad.descripcion='RESPONDER TRASLADO')").getResultList();
			
		}
		
		
		
		entidadOeeUtilUsuario.init();
		

	}
	private void cargarFuncionario(){
		try {
			funcionario=(EmpleadoPuesto)em.createQuery("Select d from EmpleadoPuesto d " +
					" where d.persona.idPersona=:idPersona and d.actual=true and d.activo=true").setParameter("idPersona",idPersona).getResultList().get(0);
			idFuncionario=funcionario.getIdEmpleadoPuesto();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void cargarNEOFuncionario(){
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilAnterior.initSinUO();
	}
	@SuppressWarnings("unchecked")
	private void obtenerCatOrigen(){
		conceptoPagosOrigen= new ArrayList<EmpleadoConceptoPago>();
		conceptoPagosOrigen=em.createQuery("Select e from EmpleadoConceptoPago e " +
				" where e.empleadoPuesto.idEmpleadoPuesto=:idEmpleadoPuesto and e.objCodigo != 111 ").setParameter("idEmpleadoPuesto", idFuncionario).getResultList();
	
	}
	
	public void initVer() throws Exception{
		solicitudTrasladoCab= em.find(SolicitudTrasladoCab.class, idSolicitudTransladoCab);
		idPersona=solicitudTrasladoCab.getPersona().getIdPersona();
		persona=em.find(Persona.class, idPersona);
		if (entidadOeeUtilUsuario == null)
			entidadOeeUtilUsuario =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		obtenerDatos();
	}
	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee(){
		entidadOeeUtilUsuario.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilUsuario.setIdUnidadOrganizativa(null);
		entidadOeeUtilUsuario.initSinUO();
		configuracionUoDetList=em.createQuery("Select det from ConfiguracionUoDet det " +
		" where det.configuracionUoCab.idConfiguracionUo=:idOEE and det.activo=true order by det.orden asc ").setParameter("idOEE", entidadOeeUtilUsuario.getIdConfigCab()).getResultList();
	}
	

	
	
	
	
	
	public String toFindPersonaView() {
		
		String from="";
		from="/movilidadLaboral/transladoTmpConSolicitud/TransladoConSolic662";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+funcionario.getPersona().getIdPersona()+"&conversationPropagation=join";
	}
	
	

	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 * **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(int index){
		
		plantaCargoDetList= em.createQuery("Select p from PlantaCargoDet p " +
				" where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true" +
				" and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab and p.permanente=true")
				.setParameter("idEstadoCab", getIdEstadoCabVacante()).setParameter("idDet",configuracionUoDetList.get(index).getIdConfiguracionUoDet()).getResultList();
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
		setearConPago();
	}
	
	
	public void seleccionarPuesto(Long idPuesto,int index){
		ConfiguracionUoCab cab= em.find(ConfiguracionUoCab.class, entidadOeeUtilUsuario.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet=idPuesto;
		setearConPago();
		setSelectedRowPuesto(index);
	}
	
	public void seleccionarPuestoInit(PlantaCargoDet puesto){
		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(puesto);
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = puesto.getId();
		setearConPago();
	}
		
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				PlantaCargoDet puestoSelec=em.find(PlantaCargoDet.class, idPlantaCargoDet);
				
				 /** Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
				 * */
				em.clear();
				funcionario.setPin(seleccionUtilFormController.generarPIN());
				funcionario.setActual(false);
				funcionario.setFechaFin(new Date());
				funcionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
				funcionario.setFechaMod(new Date());
				em.merge(funcionario);
				/**
				 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al
				 * Funcionario seleccionado
				 * */
				PlantaCargoDet plantaCargoDetFuncionario = em.find(
						PlantaCargoDet.class, funcionario.getPlantaCargoDet()
								.getIdPlantaCargoDet());
				plantaCargoDetFuncionario.setEstadoCab(seleccionUtilFormController
						.buscarEstadoCab("LIBRE"));
				plantaCargoDetFuncionario.setUsuMod(usuarioLogueado
						.getCodigoUsuario());
				plantaCargoDetFuncionario.setFechaMod(new Date());
				plantaCargoDetFuncionario.setEstadoDet(null);
				em.merge(plantaCargoDetFuncionario);
				/***
				 * •	Registra el histórico de cambios de estados del Puesto anterior en la tabla HISTORICOS_ESTADO
				 * , registrando el estado actual
				 * */
				HistoricosEstado historico = new HistoricosEstado();
				historico.setEstadoCab(seleccionUtilFormController
						.buscarEstadoCab("LIBRE"));
				historico.setFechaMod(new Date());
				historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
				historico.setPlantaCargoDet(plantaCargoDetFuncionario);
				em.persist(historico);
				
				/**
				 * se guardan los datos en la tabla EMPLEADO_PUESTO
				 * */	
				empleadoPuesto= new EmpleadoPuesto();
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","TRASLADO TEMPORAL (COMISIONAMIENTO)"));
				empleadoPuesto.setFechaInicio(fechaInicio);
				empleadoPuesto.setActual(true);
				empleadoPuesto.setActivo(true);
				empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setFechaFin(fechaFin);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO","PERMANENTE"));
				if(!comisionamiento)
					empleadoPuesto.setDatosEspecificosByIdDatosEspSituacion(seleccionUtilFormController.traerDatosEspecificos("SITUACION EMPLEADO PUESTO", "COMISIONAMIENTO INTERNO"));
				else
					empleadoPuesto.setDatosEspecificosByIdDatosEspSituacion(seleccionUtilFormController.traerDatosEspecificos("SITUACION EMPLEADO PUESTO", "COMISIONADO DE"));
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD","MOVILIDAD") );
				empleadoPuesto.setFechaAlta(new Date());
				empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,idPlantaCargoDet));
				empleadoPuesto.setPersona(persona);
				empleadoPuesto.setIncidenAntiguedad(true);
				
				em.persist(empleadoPuesto);
				/**
				 * 	Cambiar el estado del puesto a OCUPADO
				 * */
				 puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
				 puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 puestoSelec.setFechaMod(new Date());
				 em.merge(puestoSelec);
				 
				 /**
				  * 	Generar un registro del cambio de estado en la tabla HISTORICOS_ESTADO
				  * */
				 HistoricosEstado historial = new HistoricosEstado();
					historial.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("OCUPADO"));
					historial.setFechaMod(new Date());
					historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historial.setPlantaCargoDet(puestoSelec);

					em.persist(historial);
				
					 /**
					  * 	•	Gestionar objetos y categorías para el puesto ocupado
					  *
					  * */
							
						for (EmpleadoConceptoPago conceptoPago : conceptoPagosActual) {
							conceptoPago.setEmpleadoPuesto(empleadoPuesto);
							conceptoPago.setFechaAlta(new Date());
							conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							conceptoPago.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));
						
							/**
							 * PARA EL CASO QUE TENGA CATEGORIA
							 * */
							if(conceptoPago.getCategoria()!=null&&sinAnxSeleccionado!=null)
							{
								SinAnx anx=em.find(SinAnx.class, sinAnxSeleccionado.getIdAnx());
								anx.setCantDisponible(anx.getCantDisponible()-1);
								em.merge(anx);
							}
							em.persist(conceptoPago);
							
						}
						
						/**
						 * 	De Categorías/remuneraciones en OEE Origen
						 * Por cada registro tildado
						 * */
						for (EmpleadoConceptoPago  empleadoConceptoPagoVirtual: conceptoPagosOrigen) {
							if(empleadoConceptoPagoVirtual.isSeleccionar()){
								EmpleadoConceptoPago nuevoEConceptoPago= new EmpleadoConceptoPago();
								nuevoEConceptoPago.setEmpleadoPuesto(empleadoPuesto);
								nuevoEConceptoPago.setFechaAlta(new Date());
								nuevoEConceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
								nuevoEConceptoPago.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));
								nuevoEConceptoPago.setCategoria(empleadoConceptoPagoVirtual.getCategoria());
								nuevoEConceptoPago.setMonto(empleadoConceptoPagoVirtual.getMonto());
								nuevoEConceptoPago.setObjCodigo(empleadoConceptoPagoVirtual.getObjCodigo());
								em.persist(nuevoEConceptoPago);
							}
						}	
			
					
				
				 
				/**
				 * 	Registrar datos de movilidad.EMPLEADO_MOVILIDAD_CAB 
				 * **/
					
					EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
					empleadoMovilidadCab.setActivo(true);
					empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);//anterior
					empleadoMovilidadCab.setEmpleadoPuestoNuevo(empleadoPuesto);//nuevo
					empleadoMovilidadCab.setMovilidadSicca(true);
					empleadoMovilidadCab.setFechaInicio(fechaInicio);
					empleadoMovilidadCab.setFechaFin(fechaFin);
					empleadoMovilidadCab.setFechaAlta(new Date());
					empleadoMovilidadCab.setSolicitudTrasladoCab(em.find(SolicitudTrasladoCab.class, idSolicitudTransladoCab));
					empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(empleadoMovilidadCab);		
					
					/**
					 * *	Registrar cambio de estado de solicitud.
					 * Tabla SOLICITUD_TRASLADO_CAB
					 */
					solicitudTrasladoCab.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","GESTION FINALIZADA"));
					solicitudTrasladoCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
					solicitudTrasladoCab.setFechaMod(new Date());
					
					em.merge(solicitudTrasladoCab);
					
					
					
				/**
				 *Insertar registros de documentos adjuntos
				 * * */
				 /**
				  * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
				  * Insertar registro en Documentos y el archivo pdf	
				  * 		
				  * * */
					//Por Acto administrativo de Entidad Origen 
				 Long idDocOrigen=null;
				 if(fileActoAdmin!=null && ( docDecreto==null|| docDecreto.getIdDocumento()==null)){
				
						idDocOrigen= guardarAdjuntos(fName, fileActoAdmin.getFileSize(),fileActoAdmin.getContentType(),fileActoAdmin, idTipoDoc, null);
						if(idDocOrigen==null)
							return null;
						 Documentos doc= em.find(Documentos.class, idDocOrigen);
						 doc.setNroDocumento(nroDoc);
						 doc.setAnhoDocumento(Integer.parseInt(sdfAnio.format(fechaDoc)));
						 doc.setFechaDoc(fechaDoc);
						 em.merge(doc);
						
				
					 
				 }
				 /**
				  * Si no se inserta en tabla documentos y solo se referencia
				  * */
				 /*if(idDocOrigen==null)
					 idDocOrigen=docDecreto.getIdDocumento();
			
				
				 insertarRerAdjunto(idDocOrigen);	*/
				
				//Por Acto administrativo de Entidad Destino 
				 Long idDocDestino=null;
				 if(buscadorDocsDesctino.getFileActoAdmin()!=null && buscadorDocsDesctino.getDocDecreto()==null){
				
						idDocDestino= guardarAdjuntos(buscadorDocsDesctino.getfName(), buscadorDocsDesctino.getFileActoAdmin().getFileSize(), buscadorDocsDesctino.getFileActoAdmin().getContentType(), buscadorDocsDesctino.getFileActoAdmin(), buscadorDocsDesctino.getIdTipoDoc(), null);
						if(idDocDestino==null)
							return null;
						 Documentos doc= em.find(Documentos.class, idDocDestino);
						 doc.setNroDocumento(buscadorDocsDesctino.getNroDoc());
						 doc.setAnhoDocumento(Integer.parseInt(sdfAnio.format(buscadorDocsDesctino.getFechaDoc())));
						 doc.setFechaDoc(buscadorDocsDesctino.getFechaDoc());
						 em.merge(doc);
						
					
					 
				 }
				 /**
				  * Si no se inserta en tabla documentos y solo se referencia
				  * */
				 if(idDocDestino==null)
					 idDocDestino=buscadorDocsDesctino.getDocDecreto().getIdDocumento();
			
				 insertarRerAdjunto(idDocDestino);	
				
				 /**
				  * GESTIONS SIGT. TAREA
				  * */
			
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
			jbpmUtilFormController.setTransition("regTraTem_TO_end");
			jbpmUtilFormController.setActividadActual(ActividadEnum.MOV_REGISTRAR_TRASLADO_TEMPORAL);
			jbpmUtilFormController.setSolicitudTrasladoCab(solicitudTrasladoCab);
			if(!jbpmUtilFormController.nextTask())
				return null;
			em.flush();
				
				setearDatos();
				primeraEntrada=true;
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
				
				return "persisted";
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				e.printStackTrace();
				return null;
			}
		
			
		}
	
		
		public void buscarDoc() {
			if (!precondSearch())
				return;
			docDecreto = searchDoc();
			
			if (docDecreto != null) {
				fNameMostrar = docDecreto.getNombreFisicoDoc();
				habDescargar=true;
				habAdjuntar=false;
				mostrarPanelAdjunto=false;
				statusMessages.add(Severity.INFO, "Documento encontrado!");
			} else {
				fNameMostrar = null;
				mostrarPanelAdjunto=true;
				docActoAdmin = null;
				habAdjuntar=true;
				habDescargar=false;
				fileActoAdmin = null;
				statusMessages.add(Severity.ERROR, "Documento no encontrado");
			}
		}
		private Boolean precondSearch() {
			if (nroDoc == null || fechaDoc == null || idTipoDoc == null) {
				statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
				return false;
			}
			if(nroDoc < 0){
				statusMessages.add(Severity.ERROR, "El campo Nº Doc. debe ser mayor a cero");
				return false;
			}
			return true;
		}
		private Documentos searchDoc() {
			String sql="select Documentos from Documentos Documentos "
				+ " where Documentos.activo is true "
				+ " and Documentos.nroDocumento = :nroDoc and Documentos.fechaDoc = :fechaDoc"
				+ " and Documentos.datosEspecificos.idDatosEspecificos = :idTipoDoc";
			sql+=" and Documentos.configuracionUoCab.idConfiguracionUo=:idConfiguracionUo";
			
			Query q =
				em.createQuery(sql);
			q.setParameter("nroDoc", nroDoc);
			q.setParameter("fechaDoc", fechaDoc);
			q.setParameter("idTipoDoc", idTipoDoc);
			q.setParameter("idConfiguracionUo",usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			List<Documentos> lista = q.getResultList();
			if (lista.size() > 0) {
				fNameMostrar=lista.get(0).getNombreFisicoDoc();
				return lista.get(0);
			}
			return null;
		}
		
		public List<SelectItem> updateTipoDocSelectItems() {
			List<SelectItem> si = new Vector<SelectItem>();
			si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for (DatosEspecificos o : datosEspecificosByTipoDocumento())
				si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
			return si;
		}

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
		
		public void eliminar(int index){
			conceptoPagosActual.remove(index);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}
		public void agregar(){
			if(seleccionUtilFormController.getCodigoObj()==null || seleccionUtilFormController.getValorObj()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Cod. Objeto Gasto");
				return ;
			}
			if(seleccionUtilFormController.getCodigoObj().longValue()==111){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Cod. Objeto Gasto para contratado verifique");
				return ;
			}
			if(seleccionUtilFormController.getMonto()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Monto");
				return ;
			}
			if(seleccionUtilFormController.getMonto().intValue()<0){
				statusMessages.add(Severity.ERROR,"El Monto debe ser mayor a cero");
				return ;
			}
			EmpleadoConceptoPago empleadoConceptoPago = new  EmpleadoConceptoPago();
			empleadoConceptoPago.setObjCodigo(seleccionUtilFormController.getCodigoObj());
			if(seleccionUtilFormController.getCategoria()!=null)
				empleadoConceptoPago.setCategoria(seleccionUtilFormController.getCodigoCategoria());
			empleadoConceptoPago.setMonto(seleccionUtilFormController.getMonto());
			conceptoPagosActual.add(empleadoConceptoPago);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			setearConPago();
		}
		
		public void agregarInit(){
			
			EmpleadoConceptoPago empleadoConceptoPago = new  EmpleadoConceptoPago();
			empleadoConceptoPago.setObjCodigo(seleccionUtilFormController.getCodigoObj());
			if(seleccionUtilFormController.getCategoria()!=null)
				empleadoConceptoPago.setCategoria(seleccionUtilFormController.getCodigoCategoria());
			empleadoConceptoPago.setMonto(seleccionUtilFormController.getMonto());
			conceptoPagosActual.add(empleadoConceptoPago);

			setearConPago();
		}
		

		
		
		/**
		 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto administrativo
		 * */
		private void insertarRerAdjunto(Long idDocumento){
			ReferenciaAdjuntos referenciaAdjuntos= new ReferenciaAdjuntos();
			referenciaAdjuntos.setPersona(persona);
			referenciaAdjuntos.setDocumentos(em.find(Documentos.class, idDocumento));
			referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
			referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			if(actividadProcesoActual() != null){
				referenciaAdjuntos.setActividadProceso(actividadProcesoActual());
			}
			referenciaAdjuntos.setFechaAlta(new Date());
			em.persist(referenciaAdjuntos);
		}
		
		
		private Long guardarAdjuntos(String fileName, int fileSize, String contentType,UploadItem file, Long idTipoDoc, Long idDocumento) {
			try {
				
				String anio=sdfAnio.format(new Date());
				Long idDocuGenerado;
				String nombreTabla = "EmpleadoPuesto";
				String nombrePantalla = "transladoConSolic662_edit";
				String direccionFisica = "";
				String sf=File.separator;
				direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+entidadOeeUtilUsuario.getIdConfigCab()+sf+"MOVILIDAD";
				
				idDocuGenerado =
					admDocAdjuntoFormController.guardarDoc(file, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
				return idDocuGenerado;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	
		
		
		private void setearConPago(){
			sinAnxSeleccionado=seleccionUtilFormController.getSinAnx();
			seleccionUtilFormController.setearValoresObjetosGasto();
		}
		
		private Long getIdEstadoCabVacante(){
			EstadoCab cab= seleccionUtilFormController.buscarEstadoCab("VACANTE");
			return cab.getIdEstadoCab();
		}
		private boolean chkDatos(String op){
			/*if(nroDoc==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Numero de Documento");
				return false;
			}
			if(fechaDoc==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha de Documento");
				return false;
			}
			if(idTipoDoc==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento");
				return false;
			}
			if(docDecreto==null && fileActoAdmin==null){
				statusMessages.add(Severity.ERROR,"Debe Inrgesar un archivo, verifique");
				return false;
			}*/
			if(buscadorDocsDesctino.getNroDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Numero de Documento");
				return false;
			}
			if(buscadorDocsDesctino.getFechaDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha de Documento");
				return false;
			}
			if(buscadorDocsDesctino.getIdTipoDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento");
				return false;
			}
			if(buscadorDocsDesctino.getDocDecreto()==null && buscadorDocsDesctino.getFileActoAdmin()==null){
				statusMessages.add(Severity.ERROR,"Debe Inrgesar un archivo, verifique");
				return false;
			}
			
			
			if(fechaInicio==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar la Fecha de Inicio");
				return false;
			}
			if(fechaFin==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar la Fecha de Finalizacion");
				return false;
			}
			if(fechaInicio.after(fechaFin)){
				statusMessages.add(Severity.ERROR,"La fecha de Inicion no puede ser mayo a la Fecha Fin");
			}
			
			if(conceptoPagosActual.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un Objeto de Gasto y Monto");
				return  false;
			}
			
			
			return true;
		}
		
		
		
		@SuppressWarnings("unchecked")
		private void obtenerDatos(){
			List<EmpleadoMovilidadCab> mov = em.createQuery(
					"Select r from EmpleadoMovilidadCab r "
							+ " where r.solicitudTrasladoCab.idSolicitudTrasladoCab=" + idSolicitudTransladoCab)
					.getResultList();
			/**
			 * El nuevo puesto destino
			 * */
			
			if(!mov.isEmpty()){
				idEmpleadoPuesto=mov.get(0).getEmpleadoPuestoNuevo().getIdEmpleadoPuesto();
				empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
				observacion=empleadoPuesto.getObservacion();
				fechaInicio=empleadoPuesto.getFechaInicio();
				plantaCargoDetView=em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
				if(plantaCargoDetView.getConfiguracionUoDet()!=null){
					ConfiguracionUoDet uoDet= em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
					entidadOeeUtilUsuario.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
					entidadOeeUtilUsuario.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
					entidadOeeUtilUsuario.init2();
				}
				
				catDestino();
			}
		
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla=:idSolicitudTransladoCab").setParameter("idSolicitudTransladoCab", idSolicitudTransladoCab).getResultList();
			
			
			
			
			/**
			 * datos anteriores
			 * */
		
			if(!mov.isEmpty()){
				idFuncionario= mov.get(0).getEmpleadoPuestoAnterior().getIdEmpleadoPuesto();
				funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
				catOrigen();
				cargarNEOFuncionario();
			}
			
			try {
				if(!mov.isEmpty()){
					//para el panel espera de nuevas funciones
					empleadoMovilidadCabNuevasFunciones=(EmpleadoMovilidadCab)em.createQuery("Select e from EmpleadoMovilidadCab e " +
							" where e.empleadoPuestoVirtual.idEmpleadoPuesto=:idEmpleadoPuesto").setParameter("idEmpleadoPuesto", mov.get(0).getEmpleadoPuestoVirtual().getIdEmpleadoPuesto()).getResultList().get(0);

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	
		@SuppressWarnings("unchecked")
		private void catOrigen(){
			String sql="SELECT  pago.*FROM "+
			"	  movilidad.empleado_movilidad_cab "+
			"	  INNER JOIN general.empleado_puesto ON (movilidad.empleado_movilidad_cab.id_empleado_puesto_anterior = general.empleado_puesto.id_empleado_puesto) "+
			"	  INNER JOIN general.empleado_concepto_pago as pago ON (general.empleado_puesto.id_empleado_puesto = pago .id_empleado_puesto) "+
			"	  INNER JOIN general.persona ON (general.empleado_puesto.id_persona = general.persona.id_persona) "+
			"	WHERE "+
			"	  movilidad.empleado_movilidad_cab.id_empleado_puesto_anterior = general.empleado_puesto.id_empleado_puesto AND "+ 
			"	  movilidad.empleado_movilidad_cab.activo = TRUE AND  "+
			"	  general.persona.id_persona = "+funcionario.getPersona().getIdPersona();
			conceptoPagosOrigen=em.createNativeQuery(sql, EmpleadoConceptoPago.class).getResultList();
		}
		@SuppressWarnings("unchecked")
		private void catDestino(){
			String sql=" SELECT  pagos.* FROM "+
			"  movilidad.empleado_movilidad_cab "+
			"  INNER JOIN general.empleado_puesto ON (movilidad.empleado_movilidad_cab.id_empleado_puesto_nuevo = general.empleado_puesto.id_empleado_puesto) "+
			"  INNER JOIN general.empleado_concepto_pago as pagos ON (general.empleado_puesto.id_empleado_puesto = pagos.id_empleado_puesto) "+
			"  INNER JOIN general.persona ON (general.empleado_puesto.id_persona = general.persona.id_persona) "+
			"	WHERE "+
			"  movilidad.empleado_movilidad_cab.id_empleado_puesto_nuevo = general.empleado_puesto.id_empleado_puesto AND "+ 
			"  movilidad.empleado_movilidad_cab.activo = TRUE AND  "+
			"  general.persona.id_persona = "+empleadoPuesto.getPersona().getIdPersona();

			conceptoPagosActual=em.createNativeQuery(sql, EmpleadoConceptoPago.class).getResultList();

		}
		
		
		private void setearDatos(){
			persona= new Persona();
			funcionario= new EmpleadoPuesto();
			idFuncionario=null;
			observacion=null;
			plantaCargoDetList= new ArrayList<PlantaCargoDet>();
			conceptoPagosActual= new Vector<EmpleadoConceptoPago>();
			conceptoPagosOrigen= new Vector<EmpleadoConceptoPago>();
			entidadOeeUtilUsuario.setIdUnidadOrganizativa(null);
			entidadOeeUtilUsuario.setCodigoUnidOrgDep(null);
			entidadOeeUtilUsuario.setDenominacionUnidadOrgaDep(null);
			buscadorDocsDesctino.cambiarActo();
			nroDoc=null;
			comisionamiento=false;
			
			
		}
		
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
	

	
		
		public String getRowClass(int currentRow) {
			if (selectedRow == currentRow) {
				return "selectedRow";
			}
			return "notSelectedRow";
		}

		public String getRowPuestoClass(int currentRow) {
			if (selectedRowPuesto == currentRow) {
				return "selectedRow";
			}
			return "notSelectedRow";
		}
		
		public void adjuntarDoc() {
			if (!precondAdjuntarDoc())
				return;
			docActoAdmin = docDecreto;
			fileActoAdmin =
				seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
			fNameMostrar=fName;
			mostrarPanelAdjunto=false;
		

			statusMessages.add(Severity.INFO,"Documento Adjuntado ");

		}
		@SuppressWarnings("static-access")
		public void descargarDoc() throws FileNotFoundException, IOException {
			if (docDecreto != null ) {
				String resp =	AdmDocAdjuntoFormController.abrirDocumentoFromCU(docDecreto.getIdDocumento(), usuarioLogueado.getIdUsuario());
				if(!resp.equalsIgnoreCase("OK")){
					statusMessages.add(Severity.ERROR,resp);
				}
				} else if (fileActoAdmin != null) {
					admDocAdjuntoFormController.enviarArchivoANavegador(fileActoAdmin.getFileName(), fileActoAdmin.getFile());
				} else {
					statusMessages.add(Severity.ERROR, "No existe documento que descargar");
				}
		}
		private Boolean precondAdjuntarDoc() {
			if (nroDoc == null || fechaDoc == null || idTipoDoc == null) {
				statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
				return false;
			}
			if(nroDoc < 0){
				statusMessages.add(Severity.ERROR, "El campo Nº Doc. debe ser mayor a cero");
				return false;
			}
			if (uFile == null) {
				statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
				return false;
			}
			
			return true;
		}

		public void findUO(){
			entidadOeeUtilUsuario.obtenerUnidadOrganizativaDep();
		//	cargarPuestos();
		}
		
//		@SuppressWarnings("unchecked")
//		private void cargarPuestos(){
//			if(entidadOeeUtilUsuario.getIdUnidadOrganizativa()!=null){
//				plantaCargoDetNuevasFuncionesList=em.createQuery("Select det from PlantaCargoDet det " +
//				" where det.configuracionUoDet.idConfiguracionUoDet=:idUO " +
//				" and det.descripcion='EN ESPERA DE NUEVAS FUNCIONES' and det.estadoCab.idEstadoCab=:idEstadoCab " +
//				" and det.estadoDet.idEstadoDet is null order by det.orden asc")
//				.setParameter("idUO", entidadOeeUtilUsuario.getIdUnidadOrganizativa())
//				.setParameter("idEstadoCab", getIdEstadoCabVacante()).getResultList();
//				if(plantaCargoDetNuevasFuncionesList.isEmpty()){
//					statusMessages.add(Severity.ERROR,"No hay puestos disponibles. Debe gestionarse por Subsistema de Planificación la creación del puesto para esta movilidad");
//				}
//			}else
//				plantaCargoDetNuevasFuncionesList= new ArrayList<PlantaCargoDet>();
//			
//		}
		public void selecRow(int currentRow) {
			 for (PlantaCargoDet pd : plantaCargoDetNuevasFuncionesList) {
				pd.setSelected(false);
			}
			 plantaCargoDetNuevasFuncionesList.get(currentRow).setSelected(true);
			 idPlantaCargoDetNuevasFunciones=plantaCargoDetNuevasFuncionesList.get(currentRow).getIdPlantaCargoDet();
			 
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
	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}
	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
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
	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}
	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}
	
	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}
	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
	}
	public int getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	public int getSelectedRowPuesto() {
		return selectedRowPuesto;
	}
	public void setSelectedRowPuesto(int selectedRowPuesto) {
		this.selectedRowPuesto = selectedRowPuesto;
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
	public SinAnx getSinAnxSeleccionado() {
		return sinAnxSeleccionado;
	}
	public void setSinAnxSeleccionado(SinAnx sinAnxSeleccionado) {
		this.sinAnxSeleccionado = sinAnxSeleccionado;
	}
	
	
	
	public List<EmpleadoConceptoPago> getConceptoPagosActual() {
		return conceptoPagosActual;
	}
	public void setConceptoPagosActual(
			List<EmpleadoConceptoPago> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}
	public List<EmpleadoConceptoPago> getConceptoPagosOrigen() {
		return conceptoPagosOrigen;
	}
	public void setConceptoPagosOrigen(
			List<EmpleadoConceptoPago> conceptoPagosOrigen) {
		this.conceptoPagosOrigen = conceptoPagosOrigen;
	}
	public Documentos getDocDecreto() {
		return docDecreto;
	}
	public void setDocDecreto(Documentos docDecreto) {
		this.docDecreto = docDecreto;
	}
	public Documentos getDocActoAdmin() {
		return docActoAdmin;
	}
	public void setDocActoAdmin(Documentos docActoAdmin) {
		this.docActoAdmin = docActoAdmin;
	}
	public UploadItem getFileActoAdmin() {
		return fileActoAdmin;
	}
	public void setFileActoAdmin(UploadItem fileActoAdmin) {
		this.fileActoAdmin = fileActoAdmin;
	}
	public List<PlantaCargoDet> getPlantaCargoDetNuevasFuncionesList() {
		return plantaCargoDetNuevasFuncionesList;
	}
	public void setPlantaCargoDetNuevasFuncionesList(
			List<PlantaCargoDet> plantaCargoDetNuevasFuncionesList) {
		this.plantaCargoDetNuevasFuncionesList = plantaCargoDetNuevasFuncionesList;
	}
	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}
	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}
	public BuscadorDocsFC getBuscadorDocsDesctino() {
		return buscadorDocsDesctino;
	}
	public void setBuscadorDocsDesctino(BuscadorDocsFC buscadorDocsDesctino) {
		this.buscadorDocsDesctino = buscadorDocsDesctino;
	}
	public Integer getNroDoc() {
		return nroDoc;
	}
	public void setNroDoc(Integer nroDoc) {
		this.nroDoc = nroDoc;
	}
	public Date getFechaDoc() {
		return fechaDoc;
	}
	public void setFechaDoc(Date fechaDoc) {
		this.fechaDoc = fechaDoc;
	}
	public Long getIdTipoDoc() {
		return idTipoDoc;
	}
	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}
	public String getfNameMostrar() {
		return fNameMostrar;
	}
	public void setfNameMostrar(String fNameMostrar) {
		this.fNameMostrar = fNameMostrar;
	}
	public boolean isHabDescargar() {
		return habDescargar;
	}
	public void setHabDescargar(boolean habDescargar) {
		this.habDescargar = habDescargar;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
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
	public Long getIdPlantaCargoDetNuevasFunciones() {
		return idPlantaCargoDetNuevasFunciones;
	}
	public void setIdPlantaCargoDetNuevasFunciones(
			Long idPlantaCargoDetNuevasFunciones) {
		this.idPlantaCargoDetNuevasFunciones = idPlantaCargoDetNuevasFunciones;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public Long getIdSolicitudTransladoCab() {
		return idSolicitudTransladoCab;
	}
	public void setIdSolicitudTransladoCab(Long idSolicitudTransladoCab) {
		this.idSolicitudTransladoCab = idSolicitudTransladoCab;
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
	public NivelEntidadOeeUtil getEntidadOeeUtilUsuario() {
		return entidadOeeUtilUsuario;
	}
	public void setEntidadOeeUtilUsuario(NivelEntidadOeeUtil entidadOeeUtilUsuario) {
		this.entidadOeeUtilUsuario = entidadOeeUtilUsuario;
	}
	public Boolean getMostrarPanelAdjunto() {
		return mostrarPanelAdjunto;
	}
	public void setMostrarPanelAdjunto(Boolean mostrarPanelAdjunto) {
		this.mostrarPanelAdjunto = mostrarPanelAdjunto;
	}
	public boolean isHabAdjuntar() {
		return habAdjuntar;
	}
	public void setHabAdjuntar(boolean habAdjuntar) {
		this.habAdjuntar = habAdjuntar;
	}
	public boolean isComisionamiento() {
		return comisionamiento;
	}
	public void setComisionamiento(boolean comisionamiento) {
		this.comisionamiento = comisionamiento;
	}
	
	public SolicitudTrasladoCab getSolicitudTrasladoCab(){
		return this.solicitudTrasladoCab;
	}
	
	public void setSolicitudTrasladoCab(SolicitudTrasladoCab solicitudTrasladoCab){
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}
	
	public ActividadProceso actividadProcesoActual(){
		String sqlActProc = "select * from proceso.actividad_proceso act_proc join proceso.actividad act on act_proc.id_actividad = act.id_actividad "
				+"where act.descripcion = 'REGISTRAR TRASLADO TEMPORAL';";
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
