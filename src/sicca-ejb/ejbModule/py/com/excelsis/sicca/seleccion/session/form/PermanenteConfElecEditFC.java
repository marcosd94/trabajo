package py.com.excelsis.sicca.seleccion.session.form;
import java.io.File;
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
import org.richfaces.model.UploadItem;

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

@Name("permanenteConfElecEditFC")
@Scope(ScopeType.CONVERSATION)
public class PermanenteConfElecEditFC implements Serializable {

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

	private Date fechaIngreso;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView= new PlantaCargoDet();
	private String pais;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private SinAnx sinAnxSeleccionado=null;;
	

	
	public void init() throws Exception {
		if(idPersona!=null){
			if (!seguridadUtilFormController.validarInput(idPersona.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			persona=em.find(Persona.class, idPersona);
			completarDatosPersona(persona, true);
			habBtnAddPersons=false;
		}
	
		if (primeraEntrada) {
			empleadoPuesto= new EmpleadoPuesto();
			persona= new Persona();
			primeraEntrada=false;
			if (idEmpleadoPuesto!= null) {
				empleadoPuesto= em.find(EmpleadoPuesto.class,idEmpleadoPuesto);
				obtenerDatos();
			} else {
				setearDatos();
			}
		}				
		
		
		

	}
	/**
	 * Carga el Nivel Entidad Oee del Usuario logueado
	 * */
	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee(){
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList=em.createQuery("Select det from ConfiguracionUoDet det " +
		" where det.configuracionUoCab.idConfiguracionUo=:idOEE order by det.orden asc").setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab()).getResultList();
	}
	

	/**
	 * Busca la persona llamando al metodo que utiliza el web service
	 * */
	public void buscarPersona() throws Exception {
		
		/* Validaciones */
		
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

	/**
	 * Llama a la pantalla del ver persona 
	 * Accedido desde el edit y el ver
	 * */
	public String toFindPersonaView() {
		if(persona.getIdPersona()==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar la Persona");
			return null;
		}
		
		String from="";
		if(idEmpleadoPuesto==null)
			from="/seleccion/ingresoDirecto/ingresoPermConfElec/PermanenteConfElecEdit";
		else
			from="/seleccion/ingresoDirecto/ingresoPermConfElec/PermanenteConfElecView";
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+persona.getIdPersona();
	}
	
	/**
	 * Redirigue a el listado de persona
	 * */
	public String toFindPersona() {
		idPersona=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/ingresoDirecto/ingresoPermConfElec/PermanenteConfElecEdit";
	}

	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 * **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(Long idConfiguracionUoDet,int index){
		plantaCargoDetList= em.createQuery("Select p from PlantaCargoDet p " +
				" where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true" +
				" and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab and p.permanente=true")
				.setParameter("idEstadoCab", getIdEstadoCabVacante()).setParameter("idDet", idConfiguracionUoDet).getResultList();
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
		setearConPago();
	}
	
	/**
	 * Carga el puesto seleccionado 
	 * setea el puesto y el codigo sinarh 
	 * */
	public void seleccionarPuesto(Long idPuesto,int index){
		ConfiguracionUoCab cab= em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet=idPuesto;
		setSelectedRowPuesto(index);
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
//				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(datosEspecificosHome.getDatosIngresoMovilidad());
				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO DIRECTO DE PERSONAL PERMANENTE A PUESTO DE CONFIANZA O ELECTIVO"));
				empleadoPuesto.setFechaInicio(fechaIngreso);
				empleadoPuesto.setActual(true);
				empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setActivo(true);
//				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(datosEspecificosHome.getDatosPermanenteEstadoEmpleado());
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE"));
//				empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(datosEspecificosHome.getTipoRegistroIngreso());
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
				  * 	El sistema genera un registro en la tabla LEGAJOS
				  * , si la persona aun no existe la persona en la tabla Legajos.
				  * */
				 if(!exiteLegajo(persona.getIdPersona())){
					 Legajos legajos= new Legajos();
					 legajos.setPersona(persona);
					 legajos.setFechaIngreso(fechaIngreso);
					 legajos.setDatosEspecificosTipoIngreso(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO DIRECTO DE PERSONAL PERMANENTE A PUESTO DE CONFIANZA O ELECTIVO"));
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
		
		public List<SelectItem> updateTipoDocSelectItems() {
			List<SelectItem> si = new Vector<SelectItem>();
			si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for (DatosEspecificos o : datosEspecificosByTipoDocumento())
				si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
			return si;
		}

		/**
		 * 
		 * */
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
		
		

		private void completarDatosPersona(Persona persona, Boolean completo) {
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		
			
		}
		@SuppressWarnings("unchecked")
		private boolean exiteLegajo(Long idPersona){
			List<Legajos> legajos= em.createQuery("Select l from Legajos l " +
					" where l.persona.idPersona=:idPersona").setParameter("idPersona", idPersona).getResultList();
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
		
		/**
		 * Llama al metodo guardarAdjuntos enviando los parametros correspondientes
		 * */
		private Long guardarAdjuntos(String fileName, int fileSize, String contentType,UploadItem file, Long idTipoDoc, Long idDocumento) {
			try {
				
				String anio=sdfAnio.format(new Date());
				Long idDocuGenerado;
				String nombreTabla = "EmpleadoPuesto";
				String nombrePantalla = "permanenteConfElec_edit";
				String direccionFisica = "";
				String sf=File.separator;
				direccionFisica = sf+ "SICCA" + sf+anio + sf+"OEE"+sf+nivelEntidadOeeUtil.getIdConfigCab();
				
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
			String query="Select p from PlantaCargoDet p " +
					" inner join p.detTipoNombramientos as detTipoNombramiento" +
					" where (detTipoNombramiento.tipoNombramiento.descripcion='NOMBRAMIENTO CONFIANZA'" +
					" or detTipoNombramiento.tipoNombramiento.descripcion='NOMBRAMIENTO CONF. CARRERA') and p.idPlantaCargoDet=:idPlanta ";
			List<PlantaCargoDet> cargoDets= em.createQuery(query)
					.setParameter("idPlanta", idPlantaCargoDet).getResultList();
			return !cargoDets.isEmpty();
		}
		@SuppressWarnings("unchecked")
		private boolean esElectivo(Long idPlantaCargoDet){
			List<PlantaCargoDet> cargoDets= em.createQuery("Select p from PlantaCargoDet p " +
					" inner  join p.detTipoNombramientos as detTipoNombramiento" +
					" where detTipoNombramiento.tipoNombramiento.descripcion='ELECTIVA' " +
					" and p.idPlantaCargoDet=:idPlanta ")
					.setParameter("idPlanta", idPlantaCargoDet).getResultList();
			return !cargoDets.isEmpty();
		}
		
		
		/**
		 * Limpia los datos y asigna a la variable el sinAnx seleccionado
		 * */
		private void setearConPago(){
			sinAnxSeleccionado=seleccionUtilFormController.getSinAnx();
			seleccionUtilFormController.setearValoresObjetosGasto();
		}
		/**
		 * @return el id de la tabla EstadoCab 
		 * */
		private Long getIdEstadoCabVacante(){
			EstadoCab cab= seleccionUtilFormController.buscarEstadoCab("VACANTE");
			return cab.getIdEstadoCab();
		}
		/**
		 * Verificar  que cumpla con las validaciones antes de guardar
		 * */
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
			if(buscadorDocsFC.getFechaDoc().after(new Date())){
				statusMessages.add(Severity.ERROR,"La fecha ingreso no puede ser mayor a la fecha actual");
				return false;
			}
			
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Persona");
				return false;
			}
			if(empleadoConceptoPagos.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un Objeto de Gasto y Monto");
				return  false;
			}
			
			String grupoValidador="";
			if(esConfianza(idPlantaCargoDet))
				grupoValidador="IDPCONF";
			else if(esElectivo(idPlantaCargoDet))
				grupoValidador="IDPELECT";
			
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
		
		
		
		@SuppressWarnings("unchecked")
		private void obtenerDatos(){
			
			persona=em.find(Persona.class, empleadoPuesto.getPersona().getIdPersona());
			completarDatosPersona(persona, true);
			fechaIngreso=empleadoPuesto.getFechaInicio();
			List<ReferenciaAdjuntos> r=em.createQuery("Select r from ReferenciaAdjuntos r " +
					" where r.idRegistroTabla="+idEmpleadoPuesto).getResultList();
			if(!r.isEmpty()){
				buscadorDocsFC.setDocDecreto(em.find(Documentos.class, r.get(0).getDocumentos().getIdDocumento()));
				buscadorDocsFC.cargarDatosDocumento();
			}
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
			cargarNiveentidadOee();
			persona= new Persona();
			idPaisExp=General.getIdParaguay();
			habPaisExp = true;
			habPersona=false;
			plantaCargoDetList= new ArrayList<PlantaCargoDet>();
			limpiarDatosPersona();
			idPaisExp=General.getIdParaguay();	
			pais="PARAGUAY";
		}
		
	
		
	// GETTER Y SETTER
		public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
			return empleadoConceptoPagos;
		}
		public void setEmpleadoConceptoPagos(
				List<EmpleadoConceptoPago> empleadoConceptoPagos) {
			this.empleadoConceptoPagos = empleadoConceptoPagos;
		}

	
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
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
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
	

}
