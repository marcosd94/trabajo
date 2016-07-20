package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Name("puestoProvisorioComisionadoEdit748FC")
@Scope(ScopeType.CONVERSATION)
public class PuestoProvisorioComisionadoEdit748FC implements Serializable {

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

	
	


	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList=new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos= new ArrayList<EmpleadoConceptoPago>();

	private EmpleadoPuesto empleadoPuesto;
	private Long idFuncionario;
	private EmpleadoPuesto funcionario;
	private String observacion;
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private EmpleadoMovilidadCab empleadoMovilidadCab;

	private Date fechaInicio;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView= new PlantaCargoDet();
	private SinAnx sinAnxSeleccionado=null;
	private Long idEmpleadoMovilidadCab;
	
	
	public void init() throws Exception {
		
		
		if (primeraEntrada) {
			em.clear();
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
			traerDatos();
			cargarNivelEntidadOee();
			
			
		}				
		
		nivelEntidadOeeUtil.init();
		cargarPuestos();
	}
	private void traerDatos(){
		empleadoMovilidadCab=em.find(EmpleadoMovilidadCab.class, idEmpleadoMovilidadCab);
		idFuncionario=empleadoMovilidadCab.getEmpleadoPuestoAnterior().getIdEmpleadoPuesto();
		funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
		cargarCatRemuneracion();
		
	}
	
	private void setearDatos(){
		funcionario= new EmpleadoPuesto();
		idFuncionario=null;
		observacion=null;
		nivelEntidadOeeUtil.limpiar();
		statusMessages.clear();
		empleadoConceptoPagos= new ArrayList<EmpleadoConceptoPago>();
		
	}
	private void cargarNivelEntidadOee(){
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.initSinUO();
	}
	
	@SuppressWarnings("unchecked")
	private void cargarPuestos(){
		if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null){
			plantaCargoDetList=em.createQuery("Select det from PlantaCargoDet det " +
			" where det.configuracionUoDet.idConfiguracionUoDet=:idUO " +
			" and det.descripcion='CUMPLIENDO FUNCIONES EN OTRA INSTITUCION'" +
			" and det.estadoCab.idEstadoCab=:idEstadoCab " +
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
				
			
				/**
				 *	Insertar registro en la tabla EMPLEADO_PUESTO (Puesto virtual)
 				*	con el ID_PERSONA y el ID_PLANTA_CARGO_DET correspondiente al 
 				*	puesto seleccionado en la sección PUESTO para ‘CUMPLIENDO FUNCIONES EN OTRA INSTITUCION’
				  **/
				
				empleadoPuesto= new EmpleadoPuesto();
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","TRASLADO TEMPORAL"));
				empleadoPuesto.setFechaInicio(new Date());
				empleadoPuesto.setActual(true);
				empleadoPuesto.setActivo(true);
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "COMISIONADO"));
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "MOVILIDAD"));
				empleadoPuesto.setDatosEspecificosByIdDatosEspSituacion(seleccionUtilFormController.traerDatosEspecificos("SITUACION EMPLEADO PUESTO", "COMISIONADO A"));
				empleadoPuesto.setFechaAlta(new Date());
				empleadoPuesto.setIncidenAntiguedad(true);
				empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuesto.setPersona(funcionario.getPersona());
				empleadoPuesto.setPlantaCargoDet(puestoSelec);
				empleadoPuesto.setObservacion(observacion);
				em.persist(empleadoPuesto);
				/**
				 * 	Cambiar el estado del puesto virtual a COMISIONADO. 
				 * */
				 puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("COMISIONADO"));
				 puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 puestoSelec.setFechaMod(new Date());
				 em.merge(puestoSelec);
				 /**
					 * 	•	Gestionar objetos y categorías para el puesto VIRTUAL
					 * */
						gestionarObjCat();
				 
				 
				 /**
				  *  Generar un registro del cambio de estado en la tabla 
				  * */
				 HistoricosEstado historial = new HistoricosEstado();
					historial.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("COMISIONADO"));
					historial.setFechaMod(new Date());
					historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historial.setPlantaCargoDet(puestoSelec);

					em.persist(historial);
					
				
				/**
				 *	Actualizar registro en tabla EMPLEADO_MOVILIDAD_CAB 
				 * */	
				
			
			
				empleadoMovilidadCab.setEmpleadoPuestoVirtual(empleadoPuesto);
				empleadoMovilidadCab.setFechaMod(new Date());
				empleadoMovilidadCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(empleadoMovilidadCab);
				 
				
				
				
				
				 
				 
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
				/**Por cada registro tildado en columna Llevar?
				*	Insertar registro en EMPLEADO_CONCEPTO_PAGO para el PUESTO_VIRTUAL 
				**/
			for (EmpleadoConceptoPago empleadoConceptoPago : empleadoConceptoPagos) {
				if(empleadoConceptoPago.isSeleccionar()){
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
		}
		
	
	
		
		
		
		

	
		
		
	
		
		private Long getIdEstadoCabVacante(){
			EstadoCab cab= seleccionUtilFormController.buscarEstadoCab("VACANTE");
			return cab.getIdEstadoCab();
		}
		private boolean chkDatos(String op){
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar una Unidad de Gestion");
				return false;
			}
			boolean seleciono=false;
			for (PlantaCargoDet pCargoDet: plantaCargoDetList) {
				if(pCargoDet.isSelected())
					seleciono=true;
			}
			if(!seleciono){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar almenos un Puesto Disponible para Comisionados, verifique");
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
	
	
		
	
	public void findUO(){
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
		cargarPuestos();
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


	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}

	public void setEntidadOeeUtilAnterior(NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}
	public EmpleadoMovilidadCab getEmpleadoMovilidadCab() {
		return empleadoMovilidadCab;
	}
	public void setEmpleadoMovilidadCab(EmpleadoMovilidadCab empleadoMovilidadCab) {
		this.empleadoMovilidadCab = empleadoMovilidadCab;
	}
	public Long getIdEmpleadoMovilidadCab() {
		return idEmpleadoMovilidadCab;
	}
	public void setIdEmpleadoMovilidadCab(Long idEmpleadoMovilidadCab) {
		this.idEmpleadoMovilidadCab = idEmpleadoMovilidadCab;
	}
	
	

}
