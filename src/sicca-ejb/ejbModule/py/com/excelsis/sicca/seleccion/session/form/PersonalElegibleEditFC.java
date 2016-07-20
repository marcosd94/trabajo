package py.com.excelsis.sicca.seleccion.session.form;
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
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;
import enums.TiposDatos;

@Name("personalElegibleEditFC")
@Scope(ScopeType.CONVERSATION)
public class PersonalElegibleEditFC implements Serializable {

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


	

	private boolean habUpdate=false;
	private Persona persona= null;
	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private List<EmpleadoPuesto> empleadoPuestoLista=new ArrayList<EmpleadoPuesto>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos= new ArrayList<EmpleadoConceptoPago>(); 
	private Integer monto;
	private EmpleadoPuesto empleadoPuesto;

	private Date fechaIngreso;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView= new PlantaCargoDet();
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private int selectedRowPostulante = -1;
	private List<EvalReferencialPostulante>  referencialPostulantesLista=new ArrayList<EvalReferencialPostulante>();
	private Date fechaInicioContrato;
	private Date fechaFinContrato;
	private Integer nroContrato;
	private String contratoAdjunto;
	private Documentos documentoCotrato;
	private boolean habDesContrato;
	private boolean habAdjContrato;
	private byte[] uploadedFile=null;
	private String contentType=null;
	private String fileName=null;
	private String nombreDoc;
	private UploadItem fileContrato;
	private String tipoIngreso;
	private boolean modalidadContrato;//si es true es permanente ,sino es contratado
	private Concurso concursoSelec;
	String sf=File.separator;
	private EvalReferencialPostulante evRefPostulanteSelec;
	private List<ReferenciaAdjuntos> adjuntos= new ArrayList<ReferenciaAdjuntos>();
	private SinAnx sinAnxSeleccionado=null;
	private Boolean mostrarPanelAdjunto=false;
	
	public void init() throws Exception {
		
		cargarNiveentidadOee();
		if (primeraEntrada) {
			selectedRow=-1;
			selectedRowPuesto=-1;
			selectedRowPostulante=-1;
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			if (idEmpleadoPuesto!= null) {
				empleadoPuesto= em.find(EmpleadoPuesto.class,idEmpleadoPuesto);
				obtenerDatos();
			} else {
				setearDatos();
			}
		}				
		
		
		

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
			statusMessages.add(Severity.ERROR,"Debe seleccionar la Persona");
			return null;
		}
		String from="/seleccion/ingresoDirecto/concursoElegible/PersonalElegible";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+persona.getIdPersona()+"&conversationPropagation=join";
	}
	public void descargarDocumentos(int index){
		Documentos d=em.find(Documentos.class, adjuntos.get(index).getDocumentos().getIdDocumento());
		admDocAdjuntoFormController.abrirDocumentoFromCU(d.getIdDocumento(), usuarioLogueado.getIdUsuario());
	}
	

	
	
	
	
	

	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 * **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(int index){
		empleadoPuestoLista=em.createQuery("Select em from EmpleadoPuesto em " +
					" where" +
					" em.fechaFin is not null and" +
					" em.concursoPuestoAgr.idConcursoPuestoAgr is not null " +
					" and em.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=:idDet " +
					" and em.concursoPuestoAgr.concurso.datosEspecificosTipoConc.idDatosEspecificos" +
					" in (:uno,:dos,:tres)").setParameter("uno", seleccionUtilFormController.getIdTipoConcurso("CONCURSO PUBLICO DE OPOSICION"))
					.setParameter("dos", seleccionUtilFormController.getIdTipoConcurso("CONCURSO DE MERITOS"))
					.setParameter("tres", seleccionUtilFormController.getIdTipoConcurso("CONCURSO SIMPLIFICADO"))
					.setParameter("idDet", nivelEntidadOeeUtil.getIdUnidadOrganizativa())
							.getResultList();	
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
		selectedRowPostulante=-1;
		referencialPostulantesLista= new ArrayList<EvalReferencialPostulante>();
		setearConPago();
		if(empleadoPuestoLista.isEmpty())
			statusMessages.add(Severity.WARN,"No existen puestos liberados en concursos con los criterios seleccionados");
	}
	
	
	
	public void seleccionarPuesto(Long idEmPuesto,int index){
		EmpleadoPuesto ePuesto=em.find(EmpleadoPuesto.class,idEmPuesto);
		idEmpleadoPuesto=ePuesto.getIdEmpleadoPuesto();
		ConfiguracionUoCab cab= em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, ePuesto.getPlantaCargoDet().getIdPlantaCargoDet()));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet=ePuesto.getPlantaCargoDet().getIdPlantaCargoDet();
		if(ePuesto.getPlantaCargoDet().getPermanente())
				modalidadContrato=true;
		if(ePuesto.getPlantaCargoDet().getContratado())
				modalidadContrato=false;
		setearConPago();
		setSelectedRowPuesto(index);
		obtenerPostulanteIncluido(ePuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		selectedRowPostulante=-1;
		selectedRowPuesto=index;
		
		
		
	}
	/**
	 * Los postulantes incluidos son aquellos registros que cumplen las siguientes condiciones: 
	 * Tabla EVAL_REFERENCIAL_POSTULANTE los campos INCLUIDO = true, SELECCIONADO = TRUE … entre otros
	 * @param idGrupo 
	 * idGrupo=grupo al que pertenece el puesto seleccionado en grilla Puestos disponibles
	 * */
	@SuppressWarnings("unchecked")
	private void obtenerPostulanteIncluido(Long idGrupo){
		referencialPostulantesLista=em.createQuery("Select e from EvalReferencialPostulante e " +
				" where e.activo=true and e.seleccionado=true " +
				" and e.incluido=true and e.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idGrupo).getResultList();
		evRefPostulanteSelec=null;
	}
	
		
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				PlantaCargoDet puestoSelec=em.find(PlantaCargoDet.class, idPlantaCargoDet);
				/**
				 * se guardan los datos en la tabla EMPLEADO_PUESTO
				 * */	
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","INGRESO DE ELEGIBLE DE CONCURSO"));
				empleadoPuesto.setFechaInicio(fechaIngreso);
				empleadoPuesto.setActual(true);
				empleadoPuesto.setActivo(true);
				empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
				empleadoPuesto.setContratado(false);
				/**
				 * 	Si modalidad es Permanente
				 * */
				if(modalidadContrato){
					empleadoPuesto.setContratado(false);
					empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO","PERMANENTE"));
				}else{
					/**
					 * 	Si modalidad es Contratado
					 * */
					empleadoPuesto.setContratado(true);
					empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO","CONTRATADO"));
					empleadoPuesto.setNroContrato(nroContrato);
					empleadoPuesto.setFechaInicio(fechaIngreso);
					empleadoPuesto.setFechaFirmaContrato(fechaInicioContrato);
					empleadoPuesto.setFechaFinContrato(fechaFinContrato);
				}
				
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD","INGRESOS") );
				empleadoPuesto.setFechaAlta(new Date());
				empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,idPlantaCargoDet));
				empleadoPuesto.setPersona(persona);
				empleadoPuesto.setObservacion("");
				em.persist(empleadoPuesto);
				/**
				 * 	Cambiar el estado del puesto a OCUPADO
				 * */
				 puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
				 puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 puestoSelec.setFechaMod(new Date());
				 em.merge(puestoSelec);
				 /**
				  * Generar un registro del cambio de estado en la tabla HISTORICOS_ESTADO,  
				  * con el nuevo estado del puesto.
				  * */
				 HistoricosEstado historicosEstado= new HistoricosEstado();
				 historicosEstado.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
				 historicosEstado.setPlantaCargoDet(puestoSelec);
				 em.persist(historicosEstado);
				 /**
				  * Se insertan datos de categorías/remuneraciones:
				  * */
				 for (EmpleadoConceptoPago conceptoPago: empleadoConceptoPagos) {
					conceptoPago.setEmpleadoPuesto(empleadoPuesto);
					conceptoPago.setFechaAlta(new Date());
					conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					conceptoPago.setAnho(Integer.parseInt(sdfAnio.format(fechaIngreso)));
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
						idDoc= guardarAdjuntos(buscadorDocsFC.getfName(), buscadorDocsFC.getFileActoAdmin().getFileSize(), buscadorDocsFC.getFileActoAdmin().getContentType(), buscadorDocsFC.getFileActoAdmin(), buscadorDocsFC.getIdTipoDoc(), null,getUrlDocActo());
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
				 
				 
				  /** PARA EL CASO DE DOCUMENTO ADJUNTO DE CONTRATO
				   * si es CONTRATADO
				  * */
				 if(!modalidadContrato){
					 /**
					  * SI NO EXISTIA SE CREA
					  * */
					 Long idDocContrato=null;
					 if(documentoCotrato==null && fileContrato!=null){
						 idDocContrato=guardarAdjuntos(fileName, fileContrato.getFileSize(), contentType, fileContrato, getIdTipoDocContrato(), null,getUrlDocContrato());
						 Documentos dcon= em.find(Documentos.class, idDocContrato);
						 dcon.setNroDocumento(nroContrato);
						 dcon.setAnhoDocumento(Integer.parseInt(sdfAnio.format(fechaInicioContrato)));
						 dcon.setFechaFirmaContrato(fechaInicioContrato);
						 dcon.setFechaFinContrato(fechaFinContrato);
						 dcon.setFechaDoc(fechaInicioContrato);
						 dcon.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
						 em.persist(dcon);
					 }
					 /**
					  * Si no se inserta en tabla documentos y solo se referencia
					  * 
					  * */
					 if(idDocContrato==null)
						 idDocContrato=documentoCotrato.getIdDocumento();
					 //siempre se inserta en la tala referencia 
					 insertarRerAdjunto(idDocContrato);
					 
				 }
				
				 
				 /**
				  * 	El sistema genera un registro en la tabla LEGAJOS
				  * , si la persona aun no existe la persona en la tabla Legajos.
				  * */
				 if(!exiteLegajo(persona.getIdPersona())){
					 Legajos legajos= new Legajos();
					 legajos.setPersona(persona);
					 legajos.setFechaIngreso(fechaIngreso);
					 legajos.setDatosEspecificosTipoIngreso(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO DIRECTO PERSONAL OTRAS CARRERAS"));
					 em.persist(legajos);
				 }
				 
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
		private Long getIdTipoDocContrato(){
			return seleccionUtilFormController.traerDatosEspecificos("CONTRATO").getIdDatosEspecificos();
		}
		private String getUrlDocActo(){
			String direccionFisica = sf+ "SICCA" + sf+General.anhoActual() + sf+"OEE"+sf+concursoSelec.getDatosEspecificosTipoConc().getIdDatosEspecificos()+sf+concursoSelec.getIdConcurso();
			return direccionFisica;
			
		}
		private String getUrlDocContrato(){
			String direccionFisica = sf+ "SICCA" + sf+General.anhoActual()  + sf+"OEE"+sf+nivelEntidadOeeUtil.getIdConfigCab()+sf+"INGRESO_DIRECTO";
			return direccionFisica;
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
						+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.valorAlf like 'CON' and d.activo=true order by d.descripcion").getResultList();

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
			referenciaAdjuntos.setDocumentos(new Documentos());
			referenciaAdjuntos.getDocumentos().setIdDocumento(idDocumento);
			referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
			referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			referenciaAdjuntos.setFechaAlta(new Date());
			em.persist(referenciaAdjuntos);
		}
		
		
		private Long guardarAdjuntos(String fileName, int fileSize, String contentType,UploadItem file, Long idTipoDoc, Long idDocumento,String direccionFisica) {
			try {
				Long idDocuGenerado;
				String nombreTabla = "EmpleadoPuesto";
				String nombrePantalla = "otrasCarreras_edit";
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
			if(fechaIngreso==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar campo  Fecha de Ingreso");
				return false;
			}
			if(buscadorDocsFC.getFechaDoc().after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha no puede ser mayor a la fecha actual");
				return false;
			}
			if(fechaIngreso.after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha ingreso no puede ser mayor a la fecha actual");
				return false;
			}
			if(!General.isFechaValida(fechaIngreso)){
				statusMessages.add(Severity.ERROR,"Fecha invalida");
				return false;
			}
			if(!General.isFechaValida(buscadorDocsFC.getFechaDoc())){
				statusMessages.add(Severity.ERROR,"Fecha invalida");
				return false;
			}
			if(!modalidadContrato){
				if(fechaInicioContrato.after(new Date())){
					statusMessages.add(Severity.ERROR,"La fecha inicio contrato no puede ser mayor a la fecha actual");
					return false;
				}
				if(fechaFinContrato.after(new Date())){
					statusMessages.add(Severity.ERROR,"La fecha fin contrato no puede ser mayor a la fecha actual");
					return false;
				}
				if(fechaFinContrato.before(fechaInicioContrato)){
					statusMessages.add(Severity.ERROR,"La fecha inicio contrato no puede ser mayor a la fecha fin contrato");
					return false;
				}
				if(!General.isFechaValida(fechaInicioContrato)){
					statusMessages.add(Severity.ERROR,"Fecha invalida");
					return false;
				}
				if(!General.isFechaValida(fechaFinContrato)){
					statusMessages.add(Severity.ERROR,"Fecha invalida");
					return false;
				}
				if(nroContrato==null){
					statusMessages.add(Severity.ERROR,"Debe Ingresar el numero de Contrato");
					return false;
				}
				if(nroContrato.intValue()<0){
					statusMessages.add(Severity.ERROR,"El numero de Contrato debe ser mayor de cero");
					return false;
				}
				if(documentoCotrato==null && fileContrato==null){
					statusMessages.add(Severity.ERROR,"Debe Adjuntar un Contrato");
					return false;
				}
			}
			if(empleadoConceptoPagos.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe Ingresar al menos un Objeto Gasto");
				return false;
			}
			
			EmpleadoPuesto empleadoPuesto=em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class,empleadoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			 concursoSelec=em.find(Concurso.class,agr.getConcurso().getIdConcurso());
			Postulacion p=em.find(Postulacion.class,evRefPostulanteSelec.getPostulacion().getIdPostulacion() );
			persona=em.find(Persona.class, p.getPersona().getIdPersona());
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Persona");
				return false;
			}
			if(empleadoConceptoPagos.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un Objeto de Gasto y Monto");
				return  false;
			}
			if(evRefPostulanteSelec==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar un Postulante, verifique");
				return false;
			}
			String grupoValidador="";
			
			String tipoConcurso=concursoSelec.getDatosEspecificosTipoConc().getDescripcion();
			if(tipoConcurso.equalsIgnoreCase("CONCURSO PUBLICO DE OPOSICION")){
				tipoIngreso="INGRESO POR CONCURSO PUBLICO DE OPOSICION";
				if(empleadoPuesto.getPlantaCargoDet().getPermanente()){
					grupoValidador="icpoper";
				}else
					grupoValidador="icpoco";
			}else if(tipoConcurso.equalsIgnoreCase("CONCURSO DE MERITOS")){
				tipoIngreso="INGRESO POR CONCURSO DE MERITOS";
				grupoValidador="icmer";
			}else if(tipoConcurso.equalsIgnoreCase("CONCURSO SIMPLIFICADO")){
				grupoValidador="icmer";
				tipoIngreso="INGRESO POR CONCURSO SIMPLIFICADO";
			}
			
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
		
		
		
		@SuppressWarnings("unchecked")
		private void obtenerDatos(){
			
			persona=em.find(Persona.class, empleadoPuesto.getPersona().getIdPersona());
			fechaIngreso=empleadoPuesto.getFechaInicio();
			adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
			" where r.idRegistroTabla=:idRegTabla").setParameter("idRegTabla", idEmpleadoPuesto).getResultList();
	
			plantaCargoDetView=em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			ConfiguracionUoDet uoDet= em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init();
			empleadoConceptoPagos=em.createQuery("Select e from EmpleadoConceptoPago e " +
					" where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
					.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();
			
		}
		
		
		
		private void setearDatos(){
			persona= new Persona();
			empleadoPuestoLista= new ArrayList<EmpleadoPuesto>();
			
		}
		
		
		public void descargarContrato() throws FileNotFoundException, IOException{
			if (documentoCotrato != null ) {
				String resp =	AdmDocAdjuntoFormController.abrirDocumentoFromCU(documentoCotrato.getIdDocumento(), usuarioLogueado.getIdUsuario());
				if(!resp.equalsIgnoreCase("OK")){
					statusMessages.add(Severity.ERROR,resp);
				}
				} else if (fileContrato != null) {
					admDocAdjuntoFormController.enviarArchivoANavegador(fileContrato.getFileName(), fileContrato.getFile());
				} else {
					statusMessages.add(Severity.ERROR, "No existe documento que descargar");
				}
		}
		public void adjuntarDocContrato() {
			if (uploadedFile == null) {
				statusMessages.add(Severity.ERROR, "Debe seleccionar un Contrato antes");
				return ;
			}
			fileContrato =
				seleccionUtilFormController.crearUploadItem(fileName, uploadedFile.length, contentType, uploadedFile);
			nombreDoc=fileName;

			statusMessages.add(Severity.INFO, "Documento Adjuntado");

		}
		public void buscarContratos(){
			try {
				if(fechaFinContrato==null|| fechaInicioContrato==null|| nroContrato==null){
					statusMessages.add(Severity.ERROR,"Debe completar los campos obligatorios");
					return;
				}
				documentoCotrato=(Documentos) em.createQuery("Select documentos from Documentos documentos" +
						" where date(documentos.fechaFirmaContrato)=:fechaIni " +
						" and date(documentos.fechaFinContrato)=:fechaFin " +
						" and documentos.configuracionUoCab.idConfiguracionUo=:idOEE").setParameter("fechaIni",fechaInicioContrato )
						.setParameter("fechaFin", fechaFinContrato)
						.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab()).getSingleResult();
				habAdjContrato=false;
				habDesContrato=true;
				nombreDoc=documentoCotrato.getNombreFisicoDoc();
				mostrarPanelAdjunto=false;
				statusMessages.add("Documento encontrado!");
			} catch (NoResultException e) {
				habDesContrato=false;
				mostrarPanelAdjunto=true;
				habAdjContrato=true;
				statusMessages.add(Severity.ERROR,"Documento no encontrado");
				
			}
			 
		}
		
		public String getRowClass(int currentRow) {
			if (selectedRow == currentRow) {
				return "selectedRow";
			}
			return "notSelectedRow";
		}
		public String getRowPuestoClass(int currentRow) {
			if (selectedRowPuesto== currentRow) {
				return "selectedRow";
			}
			return "notSelectedRow";
		}
		public String getRowPostulanteClass(int currentRow) {
			if (selectedRowPostulante == currentRow) {
				return "selectedRow";
			}
			return "notSelectedRow";
		}
		
	
		public void selecionarPostulante(int currentRow){
			selectedRowPostulante=currentRow;
			evRefPostulanteSelec=referencialPostulantesLista.get(currentRow);
		}
		
	// GETTER Y SETTER

	
	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
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
	
	public List<EmpleadoPuesto> getEmpleadoPuestoLista() {
		return empleadoPuestoLista;
	}
	public void setEmpleadoPuestoLista(List<EmpleadoPuesto> empleadoPuestoLista) {
		this.empleadoPuestoLista = empleadoPuestoLista;
	}
	public Integer getMonto() {
		return monto;
	}
	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
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
	public List<EvalReferencialPostulante> getReferencialPostulantesLista() {
		return referencialPostulantesLista;
	}
	public void setReferencialPostulantesLista(
			List<EvalReferencialPostulante> referencialPostulantesLista) {
		this.referencialPostulantesLista = referencialPostulantesLista;
	}
	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
		return empleadoConceptoPagos;
	}
	public void setEmpleadoConceptoPagos(
			List<EmpleadoConceptoPago> empleadoConceptoPagos) {
		this.empleadoConceptoPagos = empleadoConceptoPagos;
	}
	public Date getFechaInicioContrato() {
		return fechaInicioContrato;
	}
	public void setFechaInicioContrato(Date fechaInicioContrato) {
		this.fechaInicioContrato = fechaInicioContrato;
	}
	public Date getFechaFinContrato() {
		return fechaFinContrato;
	}
	public void setFechaFinContrato(Date fechaFinContrato) {
		this.fechaFinContrato = fechaFinContrato;
	}
	public Integer getNroContrato() {
		return nroContrato;
	}
	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}
	public String getContratoAdjunto() {
		return contratoAdjunto;
	}
	public void setContratoAdjunto(String contratoAdjunto) {
		this.contratoAdjunto = contratoAdjunto;
	}
	public Documentos getDocumentoCotrato() {
		return documentoCotrato;
	}
	public void setDocumentoCotrato(Documentos documentoCotrato) {
		this.documentoCotrato = documentoCotrato;
	}
	public boolean isHabDesContrato() {
		return habDesContrato;
	}
	public void setHabDesContrato(boolean habDesContrato) {
		this.habDesContrato = habDesContrato;
	}
	public boolean isHabAdjContrato() {
		return habAdjContrato;
	}
	public void setHabAdjContrato(boolean habAdjContrato) {
		this.habAdjContrato = habAdjContrato;
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
	public String getNombreDoc() {
		return nombreDoc;
	}
	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}
	public UploadItem getFileContrato() {
		return fileContrato;
	}
	public void setFileContrato(UploadItem fileContrato) {
		this.fileContrato = fileContrato;
	}
	public String getTipoIngreso() {
		return tipoIngreso;
	}
	public void setTipoIngreso(String tipoIngreso) {
		this.tipoIngreso = tipoIngreso;
	}

	public boolean isModalidadContrato() {
		return modalidadContrato;
	}
	public void setModalidadContrato(boolean modalidadContrato) {
		this.modalidadContrato = modalidadContrato;
	}
	public Concurso getConcursoSelec() {
		return concursoSelec;
	}
	public void setConcursoSelec(Concurso concursoSelec) {
		this.concursoSelec = concursoSelec;
	}
	public int getSelectedRowPostulante() {
		return selectedRowPostulante;
	}
	public void setSelectedRowPostulante(int selectedRowPostulante) {
		this.selectedRowPostulante = selectedRowPostulante;
	}
	public EvalReferencialPostulante getEvRefPostulanteSelec() {
		return evRefPostulanteSelec;
	}
	public void setEvRefPostulanteSelec(
			EvalReferencialPostulante evRefPostulanteSelec) {
		this.evRefPostulanteSelec = evRefPostulanteSelec;
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

	public Boolean getMostrarPanelAdjunto() {
		return mostrarPanelAdjunto;
	}

	public void setMostrarPanelAdjunto(Boolean mostrarPanelAdjunto) {
		this.mostrarPanelAdjunto = mostrarPanelAdjunto;
	}
	
	

}
