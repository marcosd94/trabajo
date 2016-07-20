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
import javax.persistence.EnumType;

import org.drools.runtime.pipeline.SmooksTransformerProvider;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;
import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
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
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Name("trasladoPorConcurso734FC")
@Scope(ScopeType.CONVERSATION)
public class TrasladoPorConcurso734FC implements Serializable {

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
	private List<PlantaCargoDet> plantaCargoDetNuevasFuncionesList=new ArrayList<PlantaCargoDet>();
	private Long idPlantaCargoDetNuevasFunciones;
	private Date fechaFin;
	private Long idSolicitudTransladoCab ;
	private EmpleadoMovilidadCab empleadoMovilidadCabNuevasFunciones=new EmpleadoMovilidadCab();
	private SolicitudTrasladoCab solicitudTrasladoCab= new SolicitudTrasladoCab();
	private String from;
	private String concurso;
	
	public void init() throws Exception {
		
		if (primeraEntrada) {
			em.clear();
			selectedRow=-1;
			selectedRowPuesto=-1;
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
			

			if(idSolicitudTransladoCab!=null){
				 solicitudTrasladoCab= em.find(SolicitudTrasladoCab.class, idSolicitudTransladoCab);
				idPersona=solicitudTrasladoCab.getPersona().getIdPersona();
				persona=em.find(Persona.class, idPersona);
				cargarFuncionario();
				cargarNiveentidadOee();
				
				if(!funcionario.getPlantaCargoDet().getPermanente()){
					statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
					funcionario= new EmpleadoPuesto();
					return;
				}else{
					cargarNEOFuncionario();
					obtenerCatOrigen();
				}
				
			}
			
		}		
		
		
		
	
		
		nivelEntidadOeeUtil.init();
		cargarPuestos();

	}
	private void cargarFuncionario(){
		try {
			funcionario=(EmpleadoPuesto)em.createQuery("Select d from EmpleadoPuesto d " +
					" where d.persona.idPersona=:idPersona and d.actual=true and d.activo=true").setParameter("idPersona",idPersona).getResultList().get(0);
			idFuncionario=funcionario.getIdEmpleadoPuesto();
			Concurso co=em.find(Concurso.class, funcionario.getConcursoPuestoAgr().getConcurso().getIdConcurso());
			concurso=co.getNombre();
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
				" where e.empleadoPuesto.idEmpleadoPuesto=:idEmpleadoPuesto  ").setParameter("idEmpleadoPuesto", idFuncionario).getResultList();
	
	}
	
	public void initVer() throws Exception{
		 solicitudTrasladoCab= em.find(SolicitudTrasladoCab.class, idSolicitudTransladoCab);
			idPersona=solicitudTrasladoCab.getPersona().getIdPersona();
			persona=em.find(Persona.class, idPersona);
			
		
	
		
		obtenerDatos();
	}
	
	private void cargarNiveentidadOee(){
		try {
				nivelEntidadOeeUtil.setIdConfigCab(solicitudTrasladoCab.getOeeOrigen().getIdConfiguracionUo());
				nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
				nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
				nivelEntidadOeeUtil.initSinUO();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	
	
	
	
	
	public String toFindPersonaView() {
		
		String from="";
		
		from="/movilidadLaboral/trasladoConcurso/TrasladoPorConcurso734";
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
		ConfiguracionUoCab cab= em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet=idPuesto;
		setearConPago();
		setSelectedRowPuesto(index);
	}
		
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				/**
				 * Insertar otro registro en la tabla EMPLEADO_PUESTO (Puesto virtual),
				 *  
				 * */
					
				EmpleadoPuesto empleadoPuestoVirtual= new EmpleadoPuesto();
				
				empleadoPuestoVirtual.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPlantaCargoDetNuevasFunciones));
				empleadoPuestoVirtual.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","TRASLADO TEMPORAL"));
				empleadoPuestoVirtual.setActual(true);
				empleadoPuestoVirtual.setActivo(true);
				empleadoPuestoVirtual.setContratado(false);
				empleadoPuestoVirtual.setFechaInicio(new Date());
				empleadoPuestoVirtual.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO","PERMANENTE"));
				empleadoPuestoVirtual.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD","MOVILIDAD"));
				empleadoPuestoVirtual.setDatosEspecificosByIdDatosEspSituacion(seleccionUtilFormController.traerDatosEspecificos("SITUACION EMPLEADO PUESTO","COMISIONADO A"));
				empleadoPuestoVirtual.setFechaAlta(new Date());
				empleadoPuestoVirtual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(empleadoPuestoVirtual);
				
				
				/**
				 * 	Cambiar el estado del Puesto virtual a COMISIONADO. 
				 * */
				PlantaCargoDet cargoDetVirtual= em.find(PlantaCargoDet.class, idPlantaCargoDetNuevasFunciones);
				cargoDetVirtual.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("COMISIONADO"));
				cargoDetVirtual.setFechaMod(new Date());
				em.merge(cargoDetVirtual);
				
				 /**
				  * 	Generar un registro del cambio de estado en la tabla HISTORICOS_ESTADO para virtual
				  * */
				 HistoricosEstado historialVirtual = new HistoricosEstado();
				 historialVirtual.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("COMISIONADO"));
				 historialVirtual.setFechaMod(new Date());
				 historialVirtual.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 historialVirtual.setPlantaCargoDet(cargoDetVirtual);
				 em.persist(historialVirtual);
				 
				
				
				

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
						try {
						EmpleadoMovilidadCab empleadoMovilidadCab=(EmpleadoMovilidadCab) em.createQuery("Select e from EmpleadoMovilidadCab e " +
								" where e.solicitudTrasladoCab.idSolicitudTrasladoCab=:idSolicitudTrasladoCab").setParameter("idSolicitudTrasladoCab", idSolicitudTransladoCab).getResultList();
						empleadoMovilidadCab.setEmpleadoPuestoVirtual(empleadoPuestoVirtual);//virtual
						empleadoMovilidadCab.setFechaMod(new Date());
						empleadoMovilidadCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
						empleadoMovilidadCab.setSolicitudTrasladoCab(em.find(SolicitudTrasladoCab.class, idSolicitudTransladoCab));
						em.merge(empleadoMovilidadCab);
						} catch (Exception e) {
							// TODO: handle exception
						}
						
								
						/**
						 * Registrar cambio de estado de solicitud.
						 * */
						solicitudTrasladoCab.setDatosEspEstadoSolicitud(seleccionUtilFormController.traerDatosEspecificos("ESTADOS SOLICITUD MOVILIDAD","GESTION FINALIZADA"));
						solicitudTrasladoCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
						solicitudTrasladoCab.setFechaMod(new Date());
						em.merge(solicitudTrasladoCab);
						/**
						 *	Finaliza la tarea y el circuito de traslado 
						 * */
						
							 /**
							  * GESTIONS SIGT. TAREA
							  * */
						jbpmUtilFormController
								.setActividadActual(ActividadEnum.MOV_REGISTRAR_TRASLADADO_POR_CONCURSO);
						jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
						jbpmUtilFormController.setTransition("regTraCon_TO_end");
						jbpmUtilFormController.setSolicitudTrasladoCab(solicitudTrasladoCab);
						if(jbpmUtilFormController.nextTask())	 
							em.flush();
						else
							return null;
				
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
		

		
		
		/**
		 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto administrativo
		 * */
		private void insertarRerAdjunto(Long idDocumento){
			ReferenciaAdjuntos referenciaAdjuntos= new ReferenciaAdjuntos();
			referenciaAdjuntos.setPersona(persona);
			referenciaAdjuntos.setDocumentos(em.find(Documentos.class, idDocumento));
			referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
			referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
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
				direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+nivelEntidadOeeUtil.getIdConfigCab()+sf+"MOVILIDAD";
				
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
		
			if(idPlantaCargoDetNuevasFunciones==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar un puesto");
				return false;
			}
			
			
			
			
			return true;
		}
		
		
		
		@SuppressWarnings("unchecked")
		private void obtenerDatos(){
			List<EmpleadoMovilidadCab> mov = em.createQuery(
					"Select r from EmpleadoMovilidadCab r "
							+ " where r.solicitudTrasladoCab.idSolicitudTrasladoCab=" + idSolicitudTransladoCab)
					.getResultList();
			
			if(!mov.isEmpty()){
				idEmpleadoPuesto=mov.get(0).getEmpleadoPuestoNuevo().getIdEmpleadoPuesto();
				empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			}
			
				
			
			
			fechaInicio=empleadoPuesto.getFechaInicio();
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla="+idSolicitudTransladoCab).getResultList();
			
			plantaCargoDetView=em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			ConfiguracionUoDet uoDet= em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init();
			/**
			 * El nuevo puesto destino
			 * */
			catDestino();
			
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
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
			nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
			
			
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
		
	
	

		public void findUO(){
			nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
			cargarPuestos();
		}
		
		@SuppressWarnings("unchecked")
		private void cargarPuestos(){
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null){
				plantaCargoDetNuevasFuncionesList=em.createQuery("Select det from PlantaCargoDet det " +
				" where det.configuracionUoDet.idConfiguracionUoDet=:idUO " +
				" and det.descripcion='EN ESPERA DE NUEVAS FUNCIONES' and det.estadoCab.idEstadoCab=:idEstadoCab " +
				" and det.estadoDet.idEstadoDet is null order by det.orden asc")
				.setParameter("idUO", nivelEntidadOeeUtil.getIdUnidadOrganizativa())
				.setParameter("idEstadoCab", getIdEstadoCabVacante()).getResultList();
				if(plantaCargoDetNuevasFuncionesList.isEmpty()){
					statusMessages.add(Severity.ERROR,"No hay puestos disponibles. Debe gestionarse por Subsistema de Planificación la creación del puesto para esta movilidad");
				}
			}else
				plantaCargoDetNuevasFuncionesList= new ArrayList<PlantaCargoDet>();
			
		}
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
	public String getConcurso() {
		return concurso;
	}
	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}
	
	

}
