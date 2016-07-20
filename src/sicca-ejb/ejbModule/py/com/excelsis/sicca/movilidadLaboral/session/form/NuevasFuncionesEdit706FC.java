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
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
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

@Name("nuevasFuncionesEdit706FC")
@Scope(ScopeType.CONVERSATION)
public class NuevasFuncionesEdit706FC implements Serializable {

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


	


	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList=new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos= new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosActual= new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosAnterior= new ArrayList<EmpleadoConceptoPago>();
	private Integer monto;
	private EmpleadoPuesto empleadoPuesto;
	private Long idFuncionario;
	private EmpleadoPuesto funcionario;
	private String observacion;
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	

	private Date fechaInicio;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView= new PlantaCargoDet();
	private List<ReferenciaAdjuntos> adjuntos= new ArrayList<ReferenciaAdjuntos>();
	private SinAnx sinAnxSeleccionado=null;
	
	
	public void init() throws Exception {
		if(idFuncionario!=null){
			funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
			if(!funcionario.getPlantaCargoDet().getPermanente()){
				statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
				funcionario= new EmpleadoPuesto();
				return;
			}else{
				cargarCatRemuneracion();
			}
			
		}
		
		
		if (primeraEntrada) {
			em.clear();
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
			cargarNivelEntidadOee();
			
			
		}				
		
		nivelEntidadOeeUtil.init();
		cargarPuestos();
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

		fechaInicio = empleadoPuesto.getFechaInicio();
	}
	private void setearDatos(){
		funcionario= new EmpleadoPuesto();
		idFuncionario=null;
		buscadorDocsFC.cambiarActo();
		observacion=null;
		nivelEntidadOeeUtil.limpiar();
		statusMessages.clear();
		
	}
	private void cargarNivelEntidadOee(){
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}
	
	@SuppressWarnings("unchecked")
	private void cargarPuestos(){
		if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null){
			plantaCargoDetList=em.createQuery("Select det from PlantaCargoDet det " +
			" where det.configuracionUoDet.idConfiguracionUoDet=:idUO " +
			" and det.descripcion='EN ESPERA DE NUEVAS FUNCIONES' and det.estadoCab.idEstadoCab=:idEstadoCab " +
			" and det.estadoDet.idEstadoDet is null order by det.orden asc")
			.setParameter("idUO", nivelEntidadOeeUtil.getIdUnidadOrganizativa())
			.setParameter("idEstadoCab", getIdEstadoCabVacante()).getResultList();
			if(plantaCargoDetList.isEmpty()){
				statusMessages.add(Severity.ERROR,"No hay puestos disponibles. Debe gestionarse por Subsistema de Planificación la creación del puesto para esta movilidad");
			}
		}
		
	}
	@SuppressWarnings("unchecked")
	private void cargarCatRemuneracion(){
		empleadoConceptoPagos=em.createQuery("Select d from EmpleadoConceptoPago d " +
				" where d.empleadoPuesto.idEmpleadoPuesto=:idEmpleadoPuesto").setParameter("idEmpleadoPuesto", idFuncionario).getResultList();
		for (int i = 0; i < empleadoConceptoPagos.size(); i++) {
			if(empleadoConceptoPagos.get(i).getObjCodigo()!=null && empleadoConceptoPagos.get(i).getObjCodigo().equals(111))
				empleadoConceptoPagos.get(i).setSeleccionar(true);
		}
			
	}

	

	

	
	public String toFindPersonaView() {
		if(funcionario==null|| funcionario.getPersona()==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar la Persona");
			return null;
		}
		String from="";
		if(idEmpleadoPuesto==null)
			from="/movilidadLaboral/nuevasFunciones/NuevasFuncionesEdit706";
		else
			from="/movilidadLaboral/nuevasFunciones/NuevasFunciones706";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+funcionario.getPersona().getIdPersona()+"&conversationPropagation=join";
	}
	
	
	
	
	
	
	
	
		
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				PlantaCargoDet puestoSelec = em.find(PlantaCargoDet.class,
						idPlantaCargoDet);
				
				 /** Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
				 * */
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
						.buscarEstadoCab("VACANTE"));
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
						.buscarEstadoCab("VACANTE"));
				historico.setFechaMod(new Date());
				historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
				historico.setPlantaCargoDet(plantaCargoDetFuncionario);
				em.persist(historico);
				/**
				 *Insertar un registro en la tabla EMPLEADO_PUESTO (Puesto virtual), 
				 * con el ID_PERSONA de la persona y el ID_PLANTA_CARGO_DET correspondiente al
				 *  puesto seleccionado en la sección PUESTO PARA ‘EN ESPERA DE NUEVAS FUNCIONES 
				 * **/
				
				empleadoPuesto= new EmpleadoPuesto();
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","EN ESPERA DE ASIGNACION DE NUEVAS FUNCIONES"));
				empleadoPuesto.setFechaInicio(fechaInicio);
				empleadoPuesto.setActual(true);
				empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setActivo(true);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE"));
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "MOVILIDAD"));
				empleadoPuesto.setFechaAlta(new Date());
				empleadoPuesto.setIncidenAntiguedad(true);
				empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuesto.setPersona(funcionario.getPersona());
				empleadoPuesto.setPlantaCargoDet(puestoSelec);
				empleadoPuesto.setObservacion(observacion);
				em.persist(empleadoPuesto);
				/**
				 * 	Cambiar el estado del puesto a OCUPADO
				 * */
				 puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
				 puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 puestoSelec.setFechaMod(new Date());
				 em.merge(puestoSelec);
				 /**
				  *  Generar un registro del cambio de estado en la tabla 
				  * */
				 HistoricosEstado historial = new HistoricosEstado();
					historial.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("OCUPADO"));
					historial.setFechaMod(new Date());
					historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historial.setPlantaCargoDet(puestoSelec);

					em.persist(historial);
					
				/**
				 * 	Gestionar objetos y categorías.
				 * */
					gestionarObjCat();
				/**
				 *	Registrar datos de movilidad en la tabla EMPLEADO_MOVILIDAD_CAB
				 * */	
				
				EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
				empleadoMovilidadCab.setActivo(true);
				empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);
				empleadoMovilidadCab.setEmpleadoPuestoNuevo(empleadoPuesto);
				empleadoMovilidadCab.setEmpleadoPuestoVirtual(empleadoPuesto);
				empleadoMovilidadCab.setMovilidadSicca(true);
				empleadoMovilidadCab.setFechaInicio(fechaInicio);
				empleadoMovilidadCab.setFechaAlta(new Date());
				empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				if (observacion != null && !observacion.trim().isEmpty())
					empleadoMovilidadCab.setObservacion(observacion);
				em.persist(empleadoMovilidadCab);
				 
				/**
				 * Por cada registro tildado en la columna Llevar? 
				 * */ 
				
				for (EmpleadoConceptoPago iterable : empleadoConceptoPagos) {
					EmpleadoConceptoPago emConceptoPagoCopia= new EmpleadoConceptoPago();
					if(iterable.isSeleccionar()){
						emConceptoPagoCopia.setEmpleadoPuesto(empleadoPuesto);
						emConceptoPagoCopia.setObjCodigo(iterable.getObjCodigo());
						emConceptoPagoCopia.setCategoria(iterable.getCategoria());
						emConceptoPagoCopia.setMonto(iterable.getMonto());
						emConceptoPagoCopia.setFechaAlta(new Date());
						emConceptoPagoCopia.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(emConceptoPagoCopia);
					}
					
				}
				
				
				/**
				 * 	Gestionar registros de documentos adjuntos
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
		
		@SuppressWarnings("unchecked")
		private void gestionarObjCat(){
			/**
			 * Con el identificador de puesto anterior guardado, 
			 * acceder a tabla EMPLEADO_CONCEPTO_PAGO
			 * */
			List<EmpleadoConceptoPago> empleadoConceptoPagos=em.createQuery("Select e from EmpleadoConceptoPago e " +
					" where e.empleadoPuesto.plantaCargoDet.idPlantaCargoDet=:idPlantaCargoDet ").setParameter("idPlantaCargoDet", funcionario.getPlantaCargoDet().getIdPlantaCargoDet()).getResultList();
			/**Por cada registro 
			Insertar registro en EMPLEADO_CONCEPTO_PAGO 
				**/
			for (EmpleadoConceptoPago empleadoConceptoPago : empleadoConceptoPagos) {
				EmpleadoConceptoPago nuevoEmConceptoPago= new EmpleadoConceptoPago();
				nuevoEmConceptoPago.setEmpleadoPuesto(empleadoPuesto);
				nuevoEmConceptoPago.setObjCodigo(empleadoConceptoPago.getObjCodigo());
				nuevoEmConceptoPago.setCategoria(empleadoConceptoPago.getCategoria());
				nuevoEmConceptoPago.setMonto(empleadoConceptoPago.getMonto());
				nuevoEmConceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				nuevoEmConceptoPago.setFechaAlta(new Date());
				em.persist(nuevoEmConceptoPago);
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
						+ " where d.descripcion like 'RESOLUCION' and d.valorAlf = 'RES' and d.activo=true order by d.descripcion").getResultList();

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
			referenciaAdjuntos.setPersona(empleadoPuesto.getPersona());
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
				String nombrePantalla = "personalContratadoConfianza_edit";
				String sf=File.separator;
				String direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+nivelEntidadOeeUtil.getIdConfigCab()+sf+"MOVILIDAD";
				idDocuGenerado =
					admDocAdjuntoFormController.guardarDoc(file, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
				return idDocuGenerado;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
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
				statusMessages.add(Severity.ERROR,"Debe ingresar campo  Fecha de Inicio");
				return false;
			}
			if(buscadorDocsFC.getFechaDoc().after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha no puede ser mayor a la fecha actual");
				return false;
			}
			if(fechaInicio.after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha inicio no puede ser mayor a la fecha actual");
				return false;
			}
			
			if(funcionario==null){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Funcionario");
				return false;
			}
			if(plantaCargoDetList.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un puesto");
				return  false;
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
			
			empleadoPuesto=em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			idFuncionario=idEmpleadoPuesto;
			funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla=:idRegTabla").setParameter("idRegTabla", idEmpleadoPuesto).getResultList();
			plantaCargoDetView=em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			ConfiguracionUoDet uoDet= em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			observacion=empleadoPuesto.getObservacion();
		
			empleadoConceptoPagosActual = em
			.createQuery(
					"Select e from EmpleadoConceptoPago e "
							+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
			.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();
			
			List<EmpleadoMovilidadCab> mov = em.createQuery(
					"Select r from EmpleadoMovilidadCab r "
							+ " where r.empleadoPuestoNuevo.idEmpleadoPuesto=" + idEmpleadoPuesto)
					.getResultList();
			//datos viejos
			if(!mov.isEmpty()){
				funcionario=mov.get(0).getEmpleadoPuestoAnterior();
				buscarConceptoPagoFuncionario();//traer de la tabla empleado
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
		private void buscarConceptoPagoFuncionario(){
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
		
	
	public void findUO(){
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
		cargarPuestos();
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

		
		
	public void selecRow(int currentRow) {
			 for (PlantaCargoDet pd : plantaCargoDetList) {
				pd.setSelected(false);
			}
			 plantaCargoDetList.get(currentRow).setSelected(true);
			 idPlantaCargoDet=plantaCargoDetList.get(currentRow).getIdPlantaCargoDet();
			 
		}
		
	// GETTER Y SETTER

	

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

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}
	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}
	
	public SinAnx getSinAnxSeleccionado() {
		return sinAnxSeleccionado;
	}
	public void setSinAnxSeleccionado(SinAnx sinAnxSeleccionado) {
		this.sinAnxSeleccionado = sinAnxSeleccionado;
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
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
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

	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}

	public void setEntidadOeeUtilAnterior(NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}
	
	

}
