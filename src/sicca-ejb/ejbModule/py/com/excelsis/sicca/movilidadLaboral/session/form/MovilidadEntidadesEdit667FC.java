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

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
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
import py.com.excelsis.sicca.util.SinarhUtiles;
import py.com.excelsis.sicca.util.ValidadorController;

@Name("movilidadEntidadesEdit667FC")
@Scope(ScopeType.CONVERSATION)
public class MovilidadEntidadesEdit667FC implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SinarhUtiles sinarhUtiles;


	private Long idRedCap;

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


	
	NivelEntidadOeeUtil nivelEntidadOeeUtilOrigen = new NivelEntidadOeeUtil();
	NivelEntidadOeeUtil nivelEntidadOeeUtilDestino = new NivelEntidadOeeUtil();

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
	private String condiciones;
	private Long idTipoMovilidad;
	
	
	public void init() throws Exception {
	
		if (nivelEntidadOeeUtilOrigen == null)
			nivelEntidadOeeUtilOrigen =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		if (nivelEntidadOeeUtilDestino == null)
			nivelEntidadOeeUtilDestino =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		if (primeraEntrada) {
			em.clear();
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			setearDatos();
			cargarNivelEntidadOee();
			
			
		}	
		if(idFuncionario!=null){
			funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
			if(!funcionario.getPlantaCargoDet().getPermanente()){
				statusMessages.add(Severity.ERROR,"El funcionario Seleccionado no es Permanente");
				funcionario= new EmpleadoPuesto();
				return;
			}
			
		}
	
		nivelEntidadOeeUtilDestino.setEm(em);
		nivelEntidadOeeUtilDestino.setVarDestinoConfigUo("configuracionUoDetIdConfiguracionUoDet2");
		nivelEntidadOeeUtilDestino.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilDestino.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilDestino.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
		nivelEntidadOeeUtilDestino.setVarDestinoUnidadOrg("configuracionUoDetIdConfiguracionUoDet2");
		
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();

		
					
		
	
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

		statusMessages.clear();
		
	}
	private void cargarNivelEntidadOee(){
		nivelEntidadOeeUtilOrigen.setEm(em);
		nivelEntidadOeeUtilOrigen.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtilOrigen.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtilOrigen.init2();
	}
	

	

	

	
	
	public String findNivelEntidadOrigen() {
		nivelEntidadOeeUtilOrigen.findNivelEntidad();
		nivelEntidadOeeUtilOrigen.init();
		return "";
	}

	public String findNivelEntidadDestino() {
		nivelEntidadOeeUtilDestino.findNivelEntidad();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		return "";
	}

	public String findEntidadOrigen() {
		nivelEntidadOeeUtilOrigen.findEntidad();
		nivelEntidadOeeUtilOrigen.init();
		return "";
	}

	public String findEntidadDestino() {
		nivelEntidadOeeUtilDestino.findEntidad();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		return "";
	}

	public String findUnidadOrganizativaOrigen() {
		nivelEntidadOeeUtilOrigen.findUnidadOrganizativa();
		nivelEntidadOeeUtilOrigen.init();
		return "";
	}

	public String findUnidadOrganizativaDestino() {
		nivelEntidadOeeUtilDestino.findUnidadOrganizativa();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		return "";
	}

	public String obtenerUnidadOrganizativaDepOrigen() {
		nivelEntidadOeeUtilOrigen.obtenerUnidadOrganizativaDep();
		nivelEntidadOeeUtilOrigen.init();
		return "";
	}

	public String obtenerUnidadOrganizativaDepDestino() {
		nivelEntidadOeeUtilDestino.obtenerUnidadOrganizativaDep();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		return "";
	}
	
	
	
	
	
	
	
		
	
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				String estadoPuestoAnterior;
				DatosEspecificos tipoMovilidad=em.find(DatosEspecificos.class, idTipoMovilidad);
				if(tipoMovilidad.getDescripcion().equalsIgnoreCase("FUSION DE ENTIDADES"))
					estadoPuestoAnterior="FUSIONADO POR MOVILIDAD";
				else if(tipoMovilidad.getDescripcion().equalsIgnoreCase("SUPRESION DE ENTIDAD"))
					estadoPuestoAnterior="SUPRIMIDO POR MOVILIDAD";
				else
					estadoPuestoAnterior="CAMBIADO POR MOVILIDAD";
				
				 Long idDoc=null;
				 /**
				  * Por cada funcionario del OEE origen  de la tabla EMPLEADO_PUESTO 
				  * (que tengan ACTIVO = TRUE, ACTUAL = TRUE)
				 * */
				 em.clear();
				for (EmpleadoPuesto empleadoPuesto : traerFuncionariosPorOeeOrigen()) {
					em.refresh(empleadoPuesto);
					//PlantaCargoDet puestoAnterior = em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
					//PlantaCargoDet puestoAnterior = empleadoPuesto.getPlantaCargoDet();
					PlantaCargoDet puestoAnterior = traerPlantaCargoDet(empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
					em.refresh(puestoAnterior);
					/**
					 * 	Actualiza la tabla EMPLEADO_PUESTO 
					 * del id_empleado_puesto de la persona seleccionada
					 * */
					empleadoPuesto.setActual(false);
					empleadoPuesto.setFechaMod(new Date());
					empleadoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
					empleadoPuesto.setFechaFin(new Date());
					em.merge(empleadoPuesto);
					/**
					 * 	Actualiza el estado del Puesto anterior 
					 * */
					puestoAnterior.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab(estadoPuestoAnterior));
					puestoAnterior.setFechaMod(new Date());
					puestoAnterior.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(puestoAnterior);
					/**
					 * 	Registra el histórico de cambios de estados 
					 * del Puesto anterior en la tabla HISTORICOS_ESTADO, registrando el estado actual
					 * */
					HistoricosEstado historico = new HistoricosEstado();
					historico.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab(estadoPuestoAnterior));
					historico.setFechaMod(new Date());
					historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historico.setPlantaCargoDet(puestoAnterior);
					em.persist(historico);
					/**
					 * 	Crear puesto virtual o provisional en el OEE Destino
					*	Copiar/replicar puesto en PLANTA_CARGO_DET 
					 * */
					PlantaCargoDet puestoVirtual= new PlantaCargoDet();
					puestoVirtual.setActivo(true);
					puestoVirtual.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class, nivelEntidadOeeUtilDestino.getIdUnidadOrganizativa()));
					puestoVirtual.setContratado(puestoAnterior.getContratado());
					puestoVirtual.setPermanente(puestoAnterior.getPermanente());
					puestoVirtual.setComisionado(puestoAnterior.getComisionado());
					puestoVirtual.setCpt(puestoAnterior.getCpt());
					puestoVirtual.setDescripcion("EN ESPERA DE NUEVAS FUNCIONES");
					puestoVirtual.setDoHoraDesde(puestoAnterior.getDoHoraDesde());
					puestoVirtual.setDoHoraHasta(puestoAnterior.getDoHoraHasta());
					puestoVirtual.setEntidad(puestoAnterior.getEntidad());
					puestoVirtual.setEspecialidades(puestoAnterior.getEspecialidades());
					puestoVirtual.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("VACANTE"));
					puestoVirtual.setEstadoDet(puestoAnterior.getEstadoDet());
					puestoVirtual.setFechaAlta(new Date());
					puestoVirtual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					puestoVirtual.setFundamentacionTecnica(puestoAnterior.getFundamentacionTecnica());
					puestoVirtual.setJefatura(puestoAnterior.getJefatura());
					puestoVirtual.setJuHoraDesde(puestoAnterior.getJuHoraDesde());
					puestoVirtual.setJuHoraHasta(puestoAnterior.getJuHoraHasta());
					puestoVirtual.setLuHoraDesde(puestoAnterior.getLuHoraDesde());
					puestoVirtual.setLuHoraHasta(puestoAnterior.getJuHoraHasta());
					puestoVirtual.setMaHoraDesde(puestoAnterior.getMaHoraDesde());
					puestoVirtual.setMaHoraHasta(puestoAnterior.getMaHoraHasta());
					puestoVirtual.setMiHoraDesde(puestoAnterior.getMiHoraDesde());
					puestoVirtual.setMiHoraHasta(puestoAnterior.getMiHoraHasta());
					puestoVirtual.setViHoraDesde(puestoAnterior.getViHoraDesde());
					puestoVirtual.setViHoraHasta(puestoAnterior.getViHoraHasta());
					puestoVirtual.setMision(puestoAnterior.getMision());
					puestoVirtual.setObjetivo(puestoAnterior.getObjetivo());
					puestoVirtual.setOficina(puestoAnterior.getOficina());
					puestoVirtual.setOrden(puestoAnterior.getOrden());
					puestoVirtual.setSaHoraDesde(puestoAnterior.getSaHoraDesde());
					puestoVirtual.setSaHoraHasta(puestoAnterior.getSaHoraHasta());
					em.persist(puestoVirtual);
					/**
					 * 	Insertar un registro en la tabla EMPLEADO_PUESTO
					 * */
					EmpleadoPuesto nuevoEmpleadoPuesto= new EmpleadoPuesto();
					nuevoEmpleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(em.find(DatosEspecificos.class, idTipoMovilidad));
					nuevoEmpleadoPuesto.setActual(true);
					nuevoEmpleadoPuesto.setActivo(true);
					nuevoEmpleadoPuesto.setContratado(false);
					nuevoEmpleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE"));
					nuevoEmpleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "MOVILIDAD"));
					nuevoEmpleadoPuesto.setPlantaCargoDet(puestoVirtual);
					nuevoEmpleadoPuesto.setFechaInicio(fechaInicio);
					nuevoEmpleadoPuesto.setIncidenAntiguedad(true);
					nuevoEmpleadoPuesto.setObservacion(observacion);
					nuevoEmpleadoPuesto.setFechaAlta(new Date());
					nuevoEmpleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					nuevoEmpleadoPuesto.setPersona(empleadoPuesto.getPersona());
					em.persist(nuevoEmpleadoPuesto);
					/**
					 * 	Cambiar el estado del puesto a OCUPADO. 
					 * */
					puestoVirtual.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
					puestoVirtual.setFechaMod(new Date());
					puestoVirtual.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(puestoVirtual);
					/**
					 * 	Generar un registro del cambio de estado del puesto ocupado en la tabla HISTORICOS_ESTADO
					 * */
					HistoricosEstado historial = new HistoricosEstado();
					historial.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
					historial.setFechaMod(new Date());
					historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historial.setPlantaCargoDet(puestoVirtual);
					em.persist(historial);
					
					/**
					 * 	Gestionar objetos y categorías.
					 * */
						gestionarObjCat(nuevoEmpleadoPuesto,puestoAnterior.getIdPlantaCargoDet());
						
						/**
						 *	Registrar datos de movilidad en la tabla EMPLEADO_MOVILIDAD_CAB
						 * */	
						
						EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
						empleadoMovilidadCab.setActivo(true);
						empleadoMovilidadCab.setEmpleadoPuestoAnterior(empleadoPuesto);
						empleadoMovilidadCab.setEmpleadoPuestoNuevo(nuevoEmpleadoPuesto);
						empleadoMovilidadCab.setMovilidadSicca(true);
						empleadoMovilidadCab.setFechaInicio(fechaInicio);
						empleadoMovilidadCab.setFechaAlta(new Date());
						empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						if (observacion != null && !observacion.trim().isEmpty())
							empleadoMovilidadCab.setObservacion(observacion);
						em.persist(empleadoMovilidadCab);
						
						/**
						 * 	Gestionar registros de documentos adjuntos
						 *Insertar registros de documentos adjuntos
						 * * */
						 /**
						  * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
						  * Insertar registro en Documentos y el archivo pdf	
						  * 		
						  * 	  * */
						
						 if(buscadorDocsFC.getFileActoAdmin()!=null ){
							if(buscadorDocsFC.getDocDecreto()==null && idDoc==null){
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
						 insertarRerAdjunto(idDoc,nuevoEmpleadoPuesto.getIdEmpleadoPuesto());
						 
						
					
				}
				/**
				 * 
				 * B.	Inactivar las Unidades organizacionales que dependen del OEE Origen
				 * */
				if(nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa()!=null){
					ConfiguracionUoDet uoDetOrige=em.find(ConfiguracionUoDet.class, nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa());
					uoDetOrige.setActivo(false);
					uoDetOrige.setFechaMod(new Date());
					uoDetOrige.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(uoDetOrige);
				}
			
				/**
				 * C.	Inactivar el OEE origen
				 * */
				ConfiguracionUoCab uoCabOrigen= em.find(ConfiguracionUoCab.class,nivelEntidadOeeUtilOrigen.getIdConfigCab() );
				uoCabOrigen.setActivo(false);
				uoCabOrigen.setFechaMod(new Date());
				uoCabOrigen.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(uoCabOrigen);
				 
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
		
		private void actualizarDisponibles(String categoria, Integer codigo) {

			List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(null,
					new Integer(50), codigo, categoria, null);
			if (!listaAnx.isEmpty()) {
				SinAnx anx = new SinAnx();
				anx = listaAnx.get(0);
				anx.setCantDisponible(anx.getCantDisponible() + 1);
				em.merge(anx);
			}

		}
		
		@SuppressWarnings("unchecked")
		private void gestionarObjCat(EmpleadoPuesto empleadoPuesto,Long idPlantaCargoDet){
			/**
			 * Con el identificador de puesto anterior guardado, 
			 * acceder a tabla EMPLEADO_CONCEPTO_PAGO
			 * */
			List<EmpleadoConceptoPago> empleadoConceptoPagos=em.createQuery("Select e from EmpleadoConceptoPago e " +
					" where e.empleadoPuesto.plantaCargoDet.idPlantaCargoDet=:idPlantaCargoDet ").setParameter("idPlantaCargoDet",idPlantaCargoDet).getResultList();
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
				if(nuevoEmConceptoPago.getCategoria()!=null && 
						!nuevoEmConceptoPago.getCategoria().trim().equals(""))
					actualizarDisponibles(nuevoEmConceptoPago.getCategoria(), nuevoEmConceptoPago.getObjCodigo());
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
		
		public List<SelectItem> updateTipoMovSelectItems() {
			List<SelectItem> si = new Vector<SelectItem>();
			si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for (DatosEspecificos o : datosEspecificosByTipoMov())
				si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
			return si;
		}

		@SuppressWarnings("unchecked")
		public List<DatosEspecificos> datosEspecificosByTipoMov() {
			try {
				List<DatosEspecificos> datosEspecificosLists =
					em.createQuery("Select d from DatosEspecificos d "
						+ " where d.descripcion IN ('FUSION DE ENTIDADES', 'SUPRESION DE ENTIDAD', 'CAMBIO DENOMINACION ENTIDAD')" +
								"  and d.datosGenerales.descripcion='TIPOS DE INGRESOS Y MOVILIDAD' order by d.descripcion").getResultList();

				return datosEspecificosLists;
			} catch (Exception ex) {
				return new Vector<DatosEspecificos>();
			}
		}
		
		
		
		

	
		
		
		/**
		 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto administrativo
		 * */
		private void insertarRerAdjunto(Long idDocumento,Long idEmpleadoPuesto){
			ReferenciaAdjuntos referenciaAdjuntos= new ReferenciaAdjuntos();
			referenciaAdjuntos.setPersona(empleadoPuesto.getPersona());
			referenciaAdjuntos.setDocumentos(em.find(Documentos.class, idDocumento));
			referenciaAdjuntos.setIdRegistroTabla(idEmpleadoPuesto);
			referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			referenciaAdjuntos.setFechaAlta(new Date());
			em.persist(referenciaAdjuntos);
		}
		
		
		private Long guardarAdjuntos(String fileName, int fileSize, String contentType,UploadItem file, Long idTipoDoc, Long idDocumento) {
			try {
				
				String anio=sdfAnio.format(new Date());
				Long idDocuGenerado;
				String nombreTabla = "EmpleadoPuesto";
				String nombrePantalla = "movilidadEntidadesEdit667";
				String sf=File.separator;
				String direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+nivelEntidadOeeUtilOrigen.getIdConfigCab()+sf+"MOVILIDAD";
				idDocuGenerado =
					admDocAdjuntoFormController.guardarDoc(file, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
				return idDocuGenerado;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
		@SuppressWarnings("unchecked")
		private List<EmpleadoPuesto> traerFuncionariosPorOeeOrigen(){
			List<EmpleadoPuesto> empleadoPuestos=em.createQuery("Select e from EmpleadoPuesto e " +
					" where e.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=:idConfiguracionUo " +
					" and e.actual=true and e.activo=true ")
					.setParameter("idConfiguracionUo", nivelEntidadOeeUtilOrigen.getIdConfigCab()).getResultList();
			return empleadoPuestos;
		}
		
		private PlantaCargoDet traerPlantaCargoDet(Long idPlantaCargoDet){
			String sql = "select * from planificacion.planta_cargo_det planta where planta.id_planta_cargo_det = "+idPlantaCargoDet+" and planta.activo = TRUE";
					
			Query q = em.createNativeQuery(sql, PlantaCargoDet.class);
			List<PlantaCargoDet> lista = q.getResultList();
			if(lista.size()>0)
				return lista.get(0);
			else
				return null;
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
			if(idTipoMovilidad==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el tipo de movilidad");
				return false;
			}
			
			if(nivelEntidadOeeUtilDestino.getIdConfigCab()==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Oee Origen");
				return false;
			}
			
			if(nivelEntidadOeeUtilDestino.getIdUnidadOrganizativa()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar la Unidad Org. Destino");
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
			
			empleadoPuesto=em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			idFuncionario=idEmpleadoPuesto;
			funcionario=em.find(EmpleadoPuesto.class, idFuncionario);
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla=:idRegTabla").setParameter("idRegTabla", idEmpleadoPuesto).getResultList();
			plantaCargoDetView=em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			ConfiguracionUoDet uoDet= em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtilOrigen.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
			nivelEntidadOeeUtilOrigen.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtilOrigen.init2();
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
		
		public String toFindPersonaView() {
			
			String from="";
			
			from="/movilidadLaboral/registrarMovilidadesEntidades/MovilidadEntidades667";
			return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+funcionario.getPersona().getIdPersona()+"&conversationPropagation=join";
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

	

	public void changeCb(){
		if(idTipoMovilidad!=null){
			DatosEspecificos tipoMovilidad=em.find(DatosEspecificos.class, idTipoMovilidad);
			if(tipoMovilidad.getDescripcion().equalsIgnoreCase("FUSION DE ENTIDADES ")|| 
					tipoMovilidad.getDescripcion().equalsIgnoreCase("SUPRESION DE ENTIDAD")){
				condiciones=" -	Los funcionarios son movidos a puestos temporales de la institución destino, " +
								" previo a su ubicación en la unidad organizacional y puesto definitivos. </br> " +
								" -	Asegurarse que el anexo de personal ya fue actualizado con categorías del" +
								" OEE origen mediante el Subsistema de Remuneraciones." +
								" Asegurarse con el Administrador UO SICCA de su institución.";
			}else{
				condiciones=" -	Asegurarse que haya sido creada una nueva entidad con la nueva denominación en el sistema SICCA.</br> " +
						"	- Los funcionarios son movidos a puestos temporales de la institución destino," +
					" 		 previo a su ubicación en la unidad organizacional y puesto definitivos.</br> "+
					"	- Asegurarse que el anexo de personal ya fue actualizado con categorías " +
					"   	del OEE origen mediante el Subsistema de Remuneraciones. Asegurarse con el " +
					"  		Administrador UO SICCA de su institución. ";
			}
		}else
			condiciones=null;
		
			
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

	public String getCondiciones() {
		return  condiciones;
	}

	public void setCondiciones(String condiciones) {
		this.condiciones = condiciones;
	}

	public Long getIdTipoMovilidad() {
		return idTipoMovilidad;
	}

	public void setIdTipoMovilidad(Long idTipoMovilidad) {
		this.idTipoMovilidad = idTipoMovilidad;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilOrigen() {
		return nivelEntidadOeeUtilOrigen;
	}

	public void setNivelEntidadOeeUtilOrigen(
			NivelEntidadOeeUtil nivelEntidadOeeUtilOrigen) {
		this.nivelEntidadOeeUtilOrigen = nivelEntidadOeeUtilOrigen;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilDestino() {
		return nivelEntidadOeeUtilDestino;
	}

	public void setNivelEntidadOeeUtilDestino(
			NivelEntidadOeeUtil nivelEntidadOeeUtilDestino) {
		this.nivelEntidadOeeUtilDestino = nivelEntidadOeeUtilDestino;
	}
	
	

}
