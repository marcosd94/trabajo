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

import com.sun.org.apache.xpath.internal.operations.Bool;

import enums.TiposDatos;
import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RedCapacitacion;
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

@Name("personalContratadoConfianzaEditFC")
@Scope(ScopeType.CONVERSATION)
public class PersonalContratadoConfianzaEditFC implements Serializable {

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
	private Long idPaisExp=null;
	private String docIdentidad=null;
	private Persona persona= new Persona();
	private boolean habPersona=false;
	private Long idPersona=null;
	private Boolean primeraEntrada = true;
	private boolean habPaisExp = true;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList= new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList=new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos= new ArrayList<EmpleadoConceptoPago>(); 
	private Integer monto;
	private EmpleadoPuesto empleadoPuesto;
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
	private Boolean mostrarPanelAdjunto=false;
	
	

	private Date fechaIngreso;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView= new PlantaCargoDet();
	private int selectedRow = -1;
	private int selectedPuestoRow = -1;
	private List<ReferenciaAdjuntos> adjuntos= new ArrayList<ReferenciaAdjuntos>();
	private SinAnx sinAnxSeleccionado=null;
	
	public void init() throws Exception {
		if(idPersona!=null){
			if (!seguridadUtilFormController.validarInput(idPersona.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			persona=em.find(Persona.class, idPersona);
			completarDatosPersona(persona, true);
			habBtnAddPersons=false;
		}
		cargarNiveentidadOee();
		if (primeraEntrada) {
			em.clear();
			idPaisExp=General.getIdParaguay();
			selectedRow = -1;
			selectedPuestoRow = -1;
			empleadoPuesto= new EmpleadoPuesto();
			primeraEntrada=false;
			if (idEmpleadoPuesto!= null) {
				if (!seguridadUtilFormController.validarInput(idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(), null)) {
					return ;
				}
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
	

	
	public void buscarPersona() throws Exception {
		
		/* Validaciones */
		if (idPaisExp == null
				|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
		if (docIdentidad == null
				|| !seguridadUtilFormController.validarInput(docIdentidad.toString(), TiposDatos.STRING.getValor(), null)) {
				return;
			}
		Pais pais = em.find(Pais.class, idPaisExp);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2();
			persona = new Persona();
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = new Persona();
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}
	
	public void limpiarDatosPersona2() {
		persona= new Persona();
	}
	public void limpiarDatosPersona() {
		persona= new Persona();
		idPersona=null;
		docIdentidad = null;
	}
	private void completarDatosPersona(Persona persona, Boolean completo) {
		idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		docIdentidad = persona.getDocumentoIdentidad();
		
	}
	
	public String toFindPersonaView() {
		if(persona.getIdPersona()==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar la Persona");
			return null;
		}
		String from="";
		if(idEmpleadoPuesto==null)
			from="/seleccion/ingresoDirecto/contratadoConfiaza/PersonalContratadoConfianzaEdit";
		else
			from="/seleccion/ingresoDirecto/contratadoConfiaza/PersonalContratadoConfianzaView";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+persona.getIdPersona()+"&conversationPropagation=join";
	}
	
	public String toFindPersona() {
		idPersona=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/ingresoDirecto/contratadoConfiaza/PersonalContratadoConfianzaEdit";
	}
	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/seleccion/ingresoDirecto/contratadoConfiaza/PersonalContratadoConfianzaEdit";
	}
	
	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 * **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(Long idConfiguracionUoDet,int index){
		plantaCargoDetList= em.createQuery("Select p from PlantaCargoDet p " +
				" where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true" +
				" and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab and p.contratado=true")
				.setParameter("idEstadoCab", getIdEstadoCabVacante()).setParameter("idDet", idConfiguracionUoDet).getResultList();
		setSelectedRow(index);
		setSelectedPuestoRow(-1);
		setearConPago();
	}
	
	
	public void seleccionarPuesto(Long idPuesto,int index){
		ConfiguracionUoCab cab= em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet=idPuesto;
		setSelectedPuestoRow(index);
		setearConPago();
	}
		
		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
				PlantaCargoDet puestoSelec=em.find(PlantaCargoDet.class, idPlantaCargoDet);
				/**
				 * se guardan los datos en la tabla EMPLEADO_PUESTO
				 * */	
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","INGRESO DIRECTO DE PERSONAL CONTRATADO A PUESTO DE CONFIANZA"));
				empleadoPuesto.setFechaInicio(fechaIngreso);
				empleadoPuesto.setActual(true);
				empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
				empleadoPuesto.setContratado(true);
				empleadoPuesto.setActivo(true);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "CONTRATADO"));
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPO REGISTRO", "INGRESOS"));
				empleadoPuesto.setFechaAlta(new Date());
				empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuesto.setPersona(persona);
				empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,idPlantaCargoDet));
				em.persist(empleadoPuesto);
				/**
				 * 	Cambiar el estado del puesto a OCUPADO
				 * */
				 puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
				 puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 puestoSelec.setFechaMod(new Date());
				 em.merge(puestoSelec);
				 /**
				  * Se insertan datos de categor�as/remuneraciones:
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
				  * Si variable (AdjuntarDocumento) seteada en bot�n Buscar = ok
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
				 
				 /**
				  * PARA EL CASO DE DOCUMENTO ADJUNTO DE CONTRATO
				  * */
				 /**
				  * SI NO EXISTIA SE CREA
				  * */
				 Long idDocContrato=null;
				 if(documentoCotrato==null && fileContrato!=null){
					 idDocContrato=guardarAdjuntos(fileName, fileContrato.getFileSize(), contentType, fileContrato, getIdTipoDocContrato(), null);
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
				 
				 
				 /**
				  * 	El sistema genera un registro en la tabla LEGAJOS
				  * , si la persona aun no existe la persona en la tabla Legajos.
				  * */
				 if(!exiteLegajo(persona.getIdPersona())){
					 Legajos legajos= new Legajos();
					 legajos.setPersona(persona);
					 legajos.setFechaIngreso(fechaIngreso);
					 legajos.setDatosEspecificosTipoIngreso(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","INGRESO DIRECTO DE PERSONAL CONTRATADO A PUESTO DE CONFIANZA"));
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
		public void cambiarContrato(){
			mostrarPanelAdjunto=false;
			contentType=null;
			uploadedFile=null;
			fileContrato=null;
			nombreDoc=null;
			fechaFinContrato=null;
			fechaInicioContrato=null;
			nroContrato=null;
			
		}
		private Long getIdTipoDocContrato(){
			return seleccionUtilFormController.traerDatosEspecificosValorAlfDescripcion("TIPOS DE DOCUMENTOS", "CON","CONTRATO").getIdDatosEspecificos();
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
		

		public void descargarDocumentos(int index){
			Documentos d=em.find(Documentos.class, adjuntos.get(index).getDocumentos().getIdDocumento());
			admDocAdjuntoFormController.abrirDocumentoFromCU(d.getIdDocumento(), usuarioLogueado.getIdUsuario());
		}
		
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
		
		
		private Long guardarAdjuntos(String fileName, int fileSize, String contentType,UploadItem file, Long idTipoDoc, Long idDocumento) {
			try {
				
				String anio=sdfAnio.format(new Date());
				Long idDocuGenerado;
				String nombreTabla = "EmpleadoPuesto";
				String nombrePantalla = "personalContratadoConfianza_edit";
				String sf=File.separator;
				String direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+nivelEntidadOeeUtil.getIdConfigCab()+sf+"INGRESO_DIRECTO";
				idDocuGenerado =
					admDocAdjuntoFormController.guardarDoc(file, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
				return idDocuGenerado;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		@SuppressWarnings("unchecked")
		private boolean esConfianza(Long idPlantaCargoDet){
			List<PlantaCargoDet> cargoDets= em.createQuery("Select p from PlantaCargoDet p " +
					" inner join p.detTipoNombramientos as tipoNombramiento" +
					" where (tipoNombramiento.descripcion='NOMBRAMIENTO CONFIANZA'" +
					" or tipoNombramiento.descripcion='NOMBRAMIENTO CONF. CARRERA') and p.idPlantaCargoDet=:idPlanta ")
					.setParameter("idPlanta", idPlantaCargoDet).getResultList();
			return !cargoDets.isEmpty();
		}
		@SuppressWarnings("unchecked")
		private boolean esElectivo(Long idPlantaCargoDet){
			List<PlantaCargoDet> cargoDets= em.createQuery("Select p from PlantaCargoDet p " +
					" inner join p.detTipoNombramientos as tipoNombramiento" +
					" where (tipoNombramiento.descripcion='ELECTIVA' " +
					" and p.idPlantaCargoDet=:idPlanta ")
					.setParameter("idPlanta", idPlantaCargoDet).getResultList();
			return !cargoDets.isEmpty();
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
			if(fechaInicioContrato==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha de Inicio de Contrato");
				return false;
			}
			if(fechaFinContrato==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha de Finalizacion de Contrato");
				return false;
			}
			if(fechaInicioContrato.after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha inicio contrato no puede ser mayor a la fecha actual");
				return false;
			}
		
			if(fechaFinContrato.before(fechaInicioContrato)){
				statusMessages.add(Severity.ERROR,"La fecha inicio contrato no puede ser mayor a la fecha fin contrato");
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
			}
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"No se ingres� el dato correspondiente al campo obligatorio: Persona");
				return false;
			}
			if(empleadoConceptoPagos.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un Objeto de Gasto y Monto");
				return  false;
			}
			
			String grupoValidador="IDCCONF";
		
			PlantaCargoDet pDet=em.find(PlantaCargoDet.class,idPlantaCargoDet);
			ConfiguracionUoCab oee= em.find(ConfiguracionUoCab.class,nivelEntidadOeeUtil.getIdConfigCab());
			ValidadorDTO validadorDTOPersona=validadorController.validarCfg("PERSONA", grupoValidador,persona, pDet, null, oee);
			if(!validadorDTOPersona.isValido()){
				statusMessages.add(validadorDTOPersona.getMensaje());
				return false;
			}
			ValidadorDTO validadorDTOPuesto=validadorController.validarCfg("PUESTO", grupoValidador,persona, pDet, null, oee);
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
			
			persona=em.find(Persona.class, empleadoPuesto.getPersona().getIdPersona());
			completarDatosPersona(persona, true);
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
			idPaisExp=General.getIdParaguay();
			habPaisExp = true;
			habPersona=false;
			plantaCargoDetList= new ArrayList<PlantaCargoDet>();
			docIdentidad=null;
			selectedRow = -1;
			selectedPuestoRow = -1;
		
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
				habAdjContrato=true;
				mostrarPanelAdjunto=true;
				statusMessages.add(Severity.ERROR,"Documento no encontrado");
				
			}
			 
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
		
		
		
		public String getRowClass(int currentRow) {
			if (selectedRow == currentRow) {
				return "selectedRow";
			}
			return "notSelectedRow";
		}
		public String getRowPuestoClass(int currentRow) {
			if (selectedPuestoRow == currentRow) {
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



	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public boolean isHabPersona() {
		return habPersona;
	}

	public void setHabPersona(boolean habPersona) {
		this.habPersona = habPersona;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}

	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}

	public boolean isHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
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
	public UploadItem getFileContrato() {
		return fileContrato;
	}
	public void setFileContrato(UploadItem fileContrato) {
		this.fileContrato = fileContrato;
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
	public int getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	public int getSelectedPuestoRow() {
		return selectedPuestoRow;
	}
	public void setSelectedPuestoRow(int selectedPuestoRow) {
		this.selectedPuestoRow = selectedPuestoRow;
	}
	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}
	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}
	public Boolean getMostrarPanelAdjunto() {
		return mostrarPanelAdjunto;
	}
	public void setMostrarPanelAdjunto(Boolean mostrarPanelAdjunto) {
		this.mostrarPanelAdjunto = mostrarPanelAdjunto;
	}
	public SinAnx getSinAnxSeleccionado() {
		return sinAnxSeleccionado;
	}
	public void setSinAnxSeleccionado(SinAnx sinAnxSeleccionado) {
		this.sinAnxSeleccionado = sinAnxSeleccionado;
	}
	
	

}
