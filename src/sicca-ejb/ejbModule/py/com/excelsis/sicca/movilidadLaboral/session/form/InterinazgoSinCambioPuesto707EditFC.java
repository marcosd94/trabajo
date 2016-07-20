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

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Name("interinazgoSinCambioPuesto707EditFC")
@Scope(ScopeType.CONVERSATION)
public class InterinazgoSinCambioPuesto707EditFC implements Serializable {

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

	@In(create=true)
	BuscadorDocsFC buscadorDocsFC;
	



	
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private boolean habUpdate=false;
	private Persona persona= new Persona();
	private Long idPersona=null;
	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList=new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos= new ArrayList<EmpleadoConceptoPago>(); 
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
	private List<EmpleadoConceptoPago> empleadoConceptoPagosActual= new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosAnterior= new ArrayList<EmpleadoConceptoPago>();
	
	public void init() throws Exception {
		if(idFuncionario!=null){
			
			funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
			if(!funcionario.getPlantaCargoDet().getPermanente()){
				statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
				funcionario= new EmpleadoPuesto();
				return;
			}else{
				idPersona=funcionario.getPersona().getIdPersona();
				persona=em.find(Persona.class, idPersona);
			}
			
			
		}
		
	
		if (primeraEntrada) {
			cargarNiveentidadOee();
			selectedRow=-1;
			selectedRowPuesto=-1;
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
			
		}				
		
		
		

	}
	public void initVer() throws Exception{
		if (idEmpleadoPuesto != null) {
			if (!seguridadUtilFormController.validarInput(
					idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(),
					null)) {
				return;
			}
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			
		}
		
		obtenerDatos();
	}
	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee(){
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList=em.createQuery("Select det from ConfiguracionUoDet det " +
		" where det.configuracionUoCab.idConfiguracionUo=:idOEE order by det.orden asc").setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab()).getResultList();
	}
	

	
	
	
	
	
	public String toFindPersonaView() {
		if(persona.getIdPersona()==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar el Funcionario");
			return null;
		}
		String from="";
		if(idEmpleadoPuesto==null)
			from="/movilidadLaboral/interinazgoSinCambioPuesto/InterinazgoSinCambioPuesto707Edit";
		else
			from="/movilidadLaboral/interinazgoSinCambioPuesto/InterinazgoSinCambioPuesto707";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+persona.getIdPersona()+"&conversationPropagation=join";
	}
	
	

	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 * **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(int index){
		
		plantaCargoDetList= em.createQuery("Select p from PlantaCargoDet p " +
				" where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true" +
				" and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab ")
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
				PlantaCargoDet puestoSelec=em.find(PlantaCargoDet.class, idPlantaCargoDet);
				/**
				 * se guardan los datos en la tabla EMPLEADO_PUESTO
				 * */	
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","INTERINO SIN CAMBIO DE PUESTO"));
				empleadoPuesto.setFechaInicio(fechaInicio);
				empleadoPuesto.setActual(true);
				empleadoPuesto.setActivo(true);
				empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO","PERMANENTE"));
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD","MOVILIDAD") );
				empleadoPuesto.setFechaAlta(new Date());
				empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,idPlantaCargoDet));
				empleadoPuesto.setPersona(persona);
				empleadoPuesto.setObservacion(observacion);
				empleadoPuesto.setIncidenAntiguedad(true);
				em.persist(empleadoPuesto);
				/**
				 * 	Registrar datos de movilidad. en la tabla EMPLEADO_MOVILIDAD_CAB
				 * */
				EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
				empleadoMovilidadCab.setActivo(true);
				empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);
				empleadoMovilidadCab.setEmpleadoPuestoNuevo(empleadoPuesto);
				empleadoMovilidadCab.setMovilidadSicca(true);
				empleadoMovilidadCab.setFechaInicio(fechaInicio);
				empleadoMovilidadCab.setFechaAlta(new Date());
				empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoMovilidadCab.setObservacion(observacion);
				em.persist(empleadoMovilidadCab);
				
				/**
				 * 	Cambiar el estado del puesto a INTERINADO. 
				 * */
				 puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("INTERINADO"));
				 puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 puestoSelec.setFechaMod(new Date());
				 em.merge(puestoSelec);
				 
				 /**
				  * 	Generar un registro del cambio de estado en la tabla HISTORICOS_ESTADO
				  * */
				 HistoricosEstado historial = new HistoricosEstado();
					historial.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("INTERINADO"));
					historial.setFechaMod(new Date());
					historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historial.setPlantaCargoDet(puestoSelec);

					em.persist(historial);
				
				 /**
				  * 	Gestionar objetos y categorías.
				  * Se insertan datos de categorías/remuneraciones:
				  * */
				 for (EmpleadoConceptoPago conceptoPago: empleadoConceptoPagos) {
					conceptoPago.setEmpleadoPuesto(empleadoPuesto);
					conceptoPago.setFechaAlta(new Date());
					conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					conceptoPago.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));
					em.persist(conceptoPago);
					/**
					 * PARA EL CASO QUE TENGA CATEGORIA
					 * */
					if(conceptoPago.getCategoria()!=null&&sinAnxSeleccionado!=null)
					{
						SinAnx anx=em.find(SinAnx.class, sinAnxSeleccionado.getIdAnx());
						anx.setCantDisponible(anx.getCantDisponible()-1);
						em.merge(anx);
					}
					
				}
				/**
				 *Insertar registros de documentos adjuntos
				 * * */
				 /**
				  * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
				  * Insertar registro en Documentos y el archivo pdf	
				  * 		
				  * 	  * */
				 Long idDoc=null;
				 if(buscadorDocsFC.getFileActoAdmin()!=null){
					if(buscadorDocsFC.getDocDecreto()==null){
						idDoc= guardarAdjuntos(buscadorDocsFC.getfName(), buscadorDocsFC.getFileActoAdmin().getFileSize(), buscadorDocsFC.getFileActoAdmin().getContentType(), buscadorDocsFC.getFileActoAdmin(), buscadorDocsFC.getIdTipoDoc(), null);
						if(idDoc==null)
							return null;
						 Documentos doc= em.find(Documentos.class, idDoc);
						 doc.setNroDocumento(buscadorDocsFC.getNroDoc());
						 doc.setAnhoDocumento(Integer.parseInt(sdfAnio.format(buscadorDocsFC.getFechaDoc())));
						 doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
						 em.merge(doc);
						
					}
					 
				 }
				 /**
				  * Si no se inserta en tabla documentos y solo se referencia
				  * */
				 if(idDoc==null)
					 idDoc=buscadorDocsFC.getDocDecreto().getIdDocumento();
				 insertarRerAdjunto(idDoc);	
				
				 
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
			empleadoConceptoPagos.remove(index);
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
			empleadoConceptoPagos.add(empleadoConceptoPago);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			setearConPago();
		}
		
//		METODOS PRIVADOS
		@SuppressWarnings("unchecked")
		private boolean exiteLegajo(Long idPersona){
			List<Legajos> legajos= em.createQuery("Select l from Legajos l " +
					" where l.persona.idPersona=:idPersona ").setParameter("idPersona", idPersona).getResultList();
			return !legajos.isEmpty();
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
				String nombrePantalla = "interinazgoSinCambioPuesto707_edit";
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
			if(buscadorDocsFC.getNroDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Numero de Documento");
				return false;
			}
			if(buscadorDocsFC.getFechaDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha de Documento");
				return false;
			}
			if(buscadorDocsFC.getIdTipoDoc()==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Documento");
				return false;
			}
			if(buscadorDocsFC.getDocDecreto()==null && buscadorDocsFC.getFileActoAdmin()==null){
				statusMessages.add(Severity.ERROR,"Debe Inrgesar un archivo, verifique");
				return false;
			}
			if(fechaInicio==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar campo  Fecha de Ingreso");
				return false;
			}
			if(buscadorDocsFC.getFechaDoc().after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha no puede ser mayor a la fecha actual");
				return false;
			}
			if(fechaInicio.after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha ingreso no puede ser mayor a la fecha actual");
				return false;
			}
			if(observacion==null || observacion.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe ingresar una observacion");
				return false;
			}
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Persona");
				return false;
			}
			/*if(empleadoConceptoPagos.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un Objeto de Gasto y Monto");
				return  false;
			}*/
			
			
			String grupoValidador="IDOCARR";
			
			PlantaCargoDet pDet=em.find(PlantaCargoDet.class,idPlantaCargoDet);
			ConfiguracionUoCab oee= em.find(ConfiguracionUoCab.class,nivelEntidadOeeUtil.getIdConfigCab());
			ValidadorDTO validadorDTOPersona=validadorController.validarCfg("PERSONA", grupoValidador, persona, pDet, null, oee);
			if(!validadorDTOPersona.isValido()){
				statusMessages.add(validadorDTOPersona.getMensaje());
				return false;
			}
			ValidadorDTO validadorDTOPuesto=validadorController.validarCfg("PUESTO", grupoValidador,persona, pDet,null , oee);
			if(!validadorDTOPuesto.isValido()){
				statusMessages.add(validadorDTOPersona.getMensaje());
				return false;
			}
			
			return true;
		}
		
		public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
			return empleadoConceptoPagos;
		}
		public void setEmpleadoConceptoPagos(
				List<EmpleadoConceptoPago> empleadoConceptoPagos) {
			this.empleadoConceptoPagos = empleadoConceptoPagos;
		}
		
		@SuppressWarnings("unchecked")
		private void obtenerDatos(){
			
			fechaInicio=empleadoPuesto.getFechaInicio();
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla="+idEmpleadoPuesto).getResultList();
			observacion=empleadoPuesto.getObservacion();
			plantaCargoDetView=em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			ConfiguracionUoDet uoDet= em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init();
			/**
			 * El nuevo puesto
			 * */
		buscarConceptoPagoFuncionarioActual();
			
			/**
			 * datos anteriores
			 * */
			List<EmpleadoMovilidadCab> mov = em.createQuery(
					"Select r from EmpleadoMovilidadCab r "
							+ " where r.empleadoPuestoNuevo.idEmpleadoPuesto=" + idEmpleadoPuesto)
					.getResultList();
			if(!mov.isEmpty()){
				idFuncionario= mov.get(0).getEmpleadoPuestoAnterior().getIdEmpleadoPuesto();
				funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
				buscarConceptoPagoFuncionarioAterior();
				if (entidadOeeUtilAnterior == null)
					entidadOeeUtilAnterior =
						(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
				entidadOeeUtilAnterior.setEm(em);
				entidadOeeUtilAnterior.setIdConfigCab(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
				entidadOeeUtilAnterior.setIdUnidadOrganizativa(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet());
				entidadOeeUtilAnterior.init2();
			}
		}
		@SuppressWarnings("unchecked")
		private void buscarConceptoPagoFuncionarioAterior(){
			String sql = "SELECT pago.* " +
					"FROM movilidad.empleado_movilidad_cab cab " +
					"INNER JOIN general.empleado_puesto emp ON (cab.id_empleado_puesto_anterior = emp.id_empleado_puesto) " +
					"INNER JOIN general.empleado_concepto_pago pago ON (emp.id_empleado_puesto = pago.id_empleado_puesto) " +
					"INNER JOIN general.persona persona ON (emp.id_persona = persona.id_persona) " +
					"WHERE cab.id_empleado_puesto_anterior = emp.id_empleado_puesto AND  " +
					"cab.activo = TRUE  " +
					"AND persona.id_persona = "+funcionario.getPersona().getIdPersona();
			empleadoConceptoPagosAnterior = new ArrayList<EmpleadoConceptoPago>();
			empleadoConceptoPagosAnterior = em.createNativeQuery(sql, EmpleadoConceptoPago.class)
			.getResultList();
		}
		@SuppressWarnings("unchecked")
		private void buscarConceptoPagoFuncionarioActual(){
			String sql = "SELECT pago.* " +
					"  FROM  movilidad.empleado_movilidad_cab cab " +
					"	  INNER JOIN general.empleado_puesto ON (cab.id_empleado_puesto_nuevo = general.empleado_puesto.id_empleado_puesto) " +
					"	  INNER JOIN general.empleado_concepto_pago pago ON (general.empleado_puesto.id_empleado_puesto = pago.id_empleado_puesto) " +
					"	  INNER JOIN general.persona ON (general.empleado_puesto.id_persona = general.persona.id_persona) " +
					"	WHERE	  cab.id_empleado_puesto_nuevo = general.empleado_puesto.id_empleado_puesto AND  " +
					"	  cab.activo = TRUE AND " +
					"	  general.persona.id_persona = "+empleadoPuesto.getPersona().getIdPersona();
			empleadoConceptoPagosActual = new ArrayList<EmpleadoConceptoPago>();
			empleadoConceptoPagosActual = em.createNativeQuery(sql, EmpleadoConceptoPago.class)
			.getResultList();
		}
		
		private void setearDatos(){
			persona= new Persona();
			funcionario= new EmpleadoPuesto();
			idFuncionario=null;
			observacion=null;
			plantaCargoDetList= new ArrayList<PlantaCargoDet>();
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
	public BuscadorDocsFC getBuscadorDocsFC() {
		return buscadorDocsFC;
	}
	public void setBuscadorDocsFC(BuscadorDocsFC buscadorDocsFC) {
		this.buscadorDocsFC = buscadorDocsFC;
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
	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagosActual() {
		return empleadoConceptoPagosActual;
	}
	public void setEmpleadoConceptoPagosActual(
			List<EmpleadoConceptoPago> empleadoConceptoPagosActual) {
		this.empleadoConceptoPagosActual = empleadoConceptoPagosActual;
	}
	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagosAnterior() {
		return empleadoConceptoPagosAnterior;
	}
	public void setEmpleadoConceptoPagosAnterior(
			List<EmpleadoConceptoPago> empleadoConceptoPagosAnterior) {
		this.empleadoConceptoPagosAnterior = empleadoConceptoPagosAnterior;
	}
	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}
	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}
	
	

}
