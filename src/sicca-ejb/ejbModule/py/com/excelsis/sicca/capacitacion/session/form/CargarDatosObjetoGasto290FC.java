package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.hibernate.mapping.AuxiliaryDatabaseObject;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import com.sun.org.apache.bcel.internal.generic.NEW;

import enums.DestinadoA;
import enums.InstitucionFinaciadora;
import enums.ModalidadCapacitacion;
import enums.SiNo;
import enums.TipoSeleccion;

import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Configuracion;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.HistoricoCapacitacionDoc;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarDatosObjetoGasto290FC")
public class CargarDatosObjetoGasto290FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	DatosEspecificosList datosEspecificosList;
	
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	General general;

	private Capacitaciones capacitaciones = new Capacitaciones();
	private Long idCapacitacion;// recibe del CU que le llamo

	private ModalidadCapacitacion modalidadCapacitacion = null;
	private SiNo requierePublicacion = null;
	private Long idPais;
	private Long idCiudad = null;
	private Long idDepartamento = null;
	private InstitucionFinaciadora institucionFinaciadora = null;
	private Long idOtrasInstituciones = null;
	private String fuenteFinanciacion = null;
	private DestinadoA destinadoA = null;
	private Long cupo = null;
	private List<CapacitacionFinanciacion> capacitacionFinanciacionList = new ArrayList<CapacitacionFinanciacion>();
	private List<CapacitacionCerrada> capacitacionCerradasList = new ArrayList<CapacitacionCerrada>();
	private List<SelectItem> departamentosSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecItem = new ArrayList<SelectItem>();
	private boolean primeraEntrada = true;
	NivelEntidadOeeUtil nivelEntidadOeeUtilInstitucion = new NivelEntidadOeeUtil();
	NivelEntidadOeeUtil nivelEntidadOeeUtilFinanciacion = new NivelEntidadOeeUtil();
	NivelEntidadOeeUtil nivelEntidadOeeUtilCerrado = new NivelEntidadOeeUtil();
	private Long idCapacitacionFinaciacionEdit = null;
	private boolean esEditFinaciacion = false;
	private Long idCapacitacionCerradaEdit = null;
	private boolean esEditCerrado = false;
	private int indexUp=-1;
	private int indexUpPart=-1;
	private List<CapacitacionFinanciacion> capacitacionFinanciacionRemoveList = new ArrayList<CapacitacionFinanciacion>();
	private List<CapacitacionCerrada> capacitacionCerradasRemoveList = new ArrayList<CapacitacionCerrada>();
	private boolean habOee = false;
	private boolean habCerrado = false;
	private Documentos documentos = new Documentos();
	private String costo = null;
	private String url;
	private Long idTipoCapacitacion=null;
	private String valorAlf=null;
	private Long idConsultoras=null;
	private Long idInstEducativa=null;


	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item = null;
	private String nombreDoc = null;
	private File inputFile = null;
	private byte[] uploadedFile = null;
	private String contentType = null;
	private String fileName = null;
	private Long idTipoDocumento = null;
	private Long idDocumento = null;
	private String fromCU = null;// recibe del CU que le llamo
	private String fName;
	private byte[] uploadedFileCopia = null;
	private String contentTypeCopia = null;
	private String fileNameCopia = null;

	public void init() {
		cargarNivelEntidadOEEVariosPaneles();
		if (primeraEntrada) {
			em.clear();
			capacitaciones = em.find(Capacitaciones.class, idCapacitacion);
			setearVar();
			cargarDatos();
			findCerrado();
			findInstitucionFinanciadora();
			primeraEntrada = false;
		}
		cargarCabecera();

	}
	
	private void cargarDatos(){
		idTipoCapacitacion=capacitaciones.getDatosEspecificosTipoCap().getIdDatosEspecificos();
		valorAlf=capacitaciones.getTipoCosto();
		if(capacitaciones.getConfiguracionUoCabOeeDicta()!=null)
			cargarOeeInstitucionDicta(capacitaciones.getConfiguracionUoCabOeeDicta());
		if(capacitaciones.getDatosEspecificosInstDicta()!=null)
			idConsultoras=capacitaciones.getDatosEspecificosInstDicta().getIdDatosEspecificos();
		if(capacitaciones.getInstitucionEducativa()!=null)
			idInstEducativa=capacitaciones.getInstitucionEducativa().getIdInstEducativa();
			
		if (capacitaciones.getCiudad() != null) {
			idCiudad = capacitaciones.getCiudad().getIdCiudad();
			if (capacitaciones.getCiudad().getDepartamento() != null) {
				idDepartamento = capacitaciones.getCiudad()
						.getDepartamento().getIdDepartamento();
				if (capacitaciones.getCiudad().getDepartamento().getPais() != null)
					idPais = capacitaciones.getCiudad().getDepartamento()
							.getPais().getIdPais();
			}

		} else {
			idPais = idParaguay();
		}
		
		if (capacitaciones.getCosto() != null){
			costo =getNumber(capacitaciones.getCosto().toString());
			costo=costo.replace(",",".");
		}
			
		updateDepartamento();
		updateCiudad();
		if (capacitaciones.getDocumentos() != null) {
			idTipoDocumento = capacitaciones.getDocumentos()
					.getDatosEspecificos().getIdDatosEspecificos();
			nombreDoc = capacitaciones.getDocumentos().getNombreFisicoDoc();
			documentos = em.find(Documentos.class, capacitaciones
					.getDocumentos().getIdDocumento());
		}

		
		if (capacitaciones.getDestinadoA() != null)
			destinadoA = DestinadoA.getTipoPorId(capacitaciones
					.getDestinadoA());
		if (capacitaciones.getModalidad() != null)
			modalidadCapacitacion = ModalidadCapacitacion
					.getTipoPorId(capacitaciones.getModalidad());
		if (capacitaciones.getRecepcionPostulacion() != null)
			requierePublicacion= SiNo.getSiNoPorValor(capacitaciones
					.getRecepcionPostulacion());

		

		if (destinadoA != null && destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
	}
	private void cargarNivelEntidadOEEVariosPaneles(){
		nivelEntidadOeeUtilFinanciacion.setEm(em);
		nivelEntidadOeeUtilInstitucion.setEm(em);
		nivelEntidadOeeUtilCerrado.setEm(em);
		/**
		 * CARGAR EN NIVEL, ENTIDAD, OEE DEL PANEL INSTITUCIONES QUE DICTA
		 * **/
		nivelEntidadOeeUtilFinanciacion
		.setVarDestinoSinEntidad("sinEntidadIdSinEntidad1");
			nivelEntidadOeeUtilFinanciacion
		.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad1");
			nivelEntidadOeeUtilFinanciacion
		.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo1");
		
		
		/**
		 * cargar el NIVEL ,ENTIDAD, OEE DEL PANEL INSTITUCIONES PARTICIPADORA EN CASO DE SELEC. CERRADO
		 * */
		nivelEntidadOeeUtilCerrado
				.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilCerrado
				.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilCerrado
				.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
		nivelEntidadOeeUtilCerrado
				.setVarDestinoUnidadOrg("configuracionUoDetIdConfiguracionUoDet2");
		
		/**
		 * CARGAR EL NIVEL, ENTIDAD, OEE DEL PANEL FUENTE/INST. FINACIADORA EN CASO DE SELECCIONAR OEE,
		 * **/
		nivelEntidadOeeUtilInstitucion
		.setVarDestinoSinEntidad("sinEntidadIdSinEntidad3");
		nivelEntidadOeeUtilInstitucion
		.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad3");
		nivelEntidadOeeUtilInstitucion
		.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo3");
		nivelEntidadOeeUtilInstitucion
		.setVarDestinoUnidadOrg("configuracionUoDetIdConfiguracionUoDet3");
		nivelEntidadOeeUtilInstitucion.init();
		nivelEntidadOeeUtilFinanciacion.init();
		nivelEntidadOeeUtilCerrado.init();
	}
	private void cargarCabecera(){
		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO DE LA CAPACITACION
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		if(capacitaciones.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
		else
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.init2();
	}
	
	public void initCargar(){
		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
		capacitaciones= new Capacitaciones();
		idTipoCapacitacion=null;
		
	}
	
	@SuppressWarnings("unchecked")
	private int estadoCarga(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'CARGA'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	/**
	 * GUARDAR LOS DATOS DE LA CAPACITACIÓN
	 * ***/
	public String save() {
		try {
			if(capacitaciones.getNombre()==null||capacitaciones.getNombre().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Titulo. Verifique");
				return null;
			}
			if (seguridadUtilFormController == null) {
				seguridadUtilFormController =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			if(seguridadUtilFormController.contieneCaracter(capacitaciones.getNombre().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
			capacitaciones.setActivo(true);
			capacitaciones.setFechaAlta(new Date());
			capacitaciones.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			capacitaciones.setDatosEspecificosTipoCap(em.find(DatosEspecificos.class,idTipoCapacitacion));
			capacitaciones.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
			capacitaciones.setEstado(estadoCarga());
			capacitaciones.setTipo("CAP_OG290");
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null)
				capacitaciones.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class, nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
			em.persist(capacitaciones);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			return null;
		}
	}
	
	
	private void setearVar(){
		idDepartamento=null;
		idCiudad=null;
		fuenteFinanciacion=null;
		idTipoDocumento=null;
		institucionFinaciadora=null;
		habOee = false;
		uploadedFileCopia=null;
		modalidadCapacitacion=null;
		requierePublicacion=null;
		costo=null;
		destinadoA=null;
		nombreDoc=null;
		esEditCerrado=false;
		esEditFinaciacion=false;
		limpiarCerrado();
		limpiarInsti();
		nivelEntidadOeeUtilInstitucion.limpiar();
		idInstEducativa=null;
		idConsultoras=null;
		valorAlf=null;
			
		
	}

	public String addInstitucion() {
		try {
			if (fuenteFinanciacion == null
					|| fuenteFinanciacion.trim().equals("")) {
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo fuente financiacion antes de realizar esta acción. Verifique");
				return null;
			}
			if(seguridadUtilFormController.contieneCaracter(fuenteFinanciacion.trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter")+" en el fuente financiacion");
				return null;
			}
			if (institucionFinaciadora == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar la Institución Financiadora antes de realizar esta acción. Verifique");
				return null;
			}
			if (institucionFinaciadora.getId().equals("oee")) {
				if(nivelEntidadOeeUtilFinanciacion.getIdSinNivelEntidad()==null){
					statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar Un Nivel  antes de realizar esta acción. Verifique ");
						return null;
				}
				if(nivelEntidadOeeUtilFinanciacion.getIdSinEntidad()==null){
					statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar Una Entidad  antes de realizar esta acción. Verifique ");
						return null;
				}
				if (nivelEntidadOeeUtilFinanciacion.getIdConfigCab() == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe seleccionar el Oee antes de realizar esta acción. Verifique");
					return null;
				}
			} else {
				if (idOtrasInstituciones == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe Seleccionar el items Otras Instituciones antes de realizar esta acción. Verifique");
					return null;
				}
			}

			CapacitacionFinanciacion aux = new CapacitacionFinanciacion();
			aux.setFuenteFinanciacion(fuenteFinanciacion);
			aux.setActivo(true);
			aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			aux.setFechaAlta(new Date());

			if (institucionFinaciadora.getId().equals("oee")) {
				aux.setConfiguracionUo(em.find(ConfiguracionUoCab.class,
						nivelEntidadOeeUtilFinanciacion.getIdConfigCab()));
				aux.setDatosEspecificosInst(null);
			} else {
				aux.setDatosEspecificosInst(em.find(DatosEspecificos.class,
						idOtrasInstituciones));
				aux.setConfiguracionUo(null);
			}

			capacitacionFinanciacionList.add(aux);

			esEditFinaciacion = false;
			limpiarInsti();

			return "add";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"A ocurrido un error" + e.getMessage());
			return null;
		}
	}

	public void upInstitucion() {
		try {
			if (fuenteFinanciacion == null
					|| fuenteFinanciacion.trim().equals("")) {
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo fuente financiacion antes de realizar esta acción. Verifique");
				return;
			}
			if (institucionFinaciadora == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar la Institución Financiadora antes de realizar esta acción. Verifique");
				return;
			}
			if (institucionFinaciadora.getId().equals("oee")) {
				if(nivelEntidadOeeUtilFinanciacion.getIdSinNivelEntidad()==null){
					statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar Un Nivel  antes de realizar esta acción. Verifique ");
						return ;
				}
				if(nivelEntidadOeeUtilFinanciacion.getIdSinEntidad()==null){
					statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar Una Entidad  antes de realizar esta acción. Verifique ");
						return ;
				}
				if (nivelEntidadOeeUtilFinanciacion.getIdConfigCab() == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe seleccionar el Oee antes de realizar esta acción. Verifique");
					return;
				}
			} else {
				if (idOtrasInstituciones == null) {
					statusMessages
							.add(Severity.ERROR,
									"Debe Seleccionar el items Otras Instituciones antes de realizar esta acción. Verifique");
					return;
				}
			}

			CapacitacionFinanciacion aux = new CapacitacionFinanciacion();

			if (idCapacitacionFinaciacionEdit != null) {
				aux = em.find(CapacitacionFinanciacion.class,
						idCapacitacionFinaciacionEdit);
				aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
				aux.setFechaMod(new Date());
			} else {
				aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				aux.setFechaAlta(new Date());
				aux.setActivo(true);
			}
			if (institucionFinaciadora.getId().equals("oee")) {
				aux.setConfiguracionUo(em.find(ConfiguracionUoCab.class,
						nivelEntidadOeeUtilFinanciacion.getIdConfigCab()));
				aux.setDatosEspecificosInst(null);
			} else {
				aux.setDatosEspecificosInst(em.find(DatosEspecificos.class,
						idOtrasInstituciones));
				aux.setConfiguracionUo(null);
			}
			aux.setFuenteFinanciacion(fuenteFinanciacion);
			capacitacionFinanciacionList.remove(indexUp);
			capacitacionFinanciacionList.add(indexUp, aux);

			limpiarInsti();
			indexUp =-1;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"A ocurrido un error" + e.getMessage());
		}

	}

	public List<SelectItem> getTipoCapacitacionItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : getTipoCapacitacion())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getTipoCapacitacion() {
		try {
			List<DatosEspecificos> de=em.createQuery("Select d from DatosEspecificos d  where d.activo=true" +
					" and d.datosGenerales.descripcion='TIPOS DE CAPACITACIONES' order by d.descripcion").getResultList();
			
			return de;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	
	public List<SelectItem> getConsultorasItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : getConsultoras())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getConsultoras() {
		try {
			List<DatosEspecificos> de=em.createQuery("Select d from DatosEspecificos d  where d.activo=true" +
					" and d.datosGenerales.descripcion='CONSULTORAS CAPACITACION' order by d.descripcion").getResultList();
			
			return de;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	public void eliminarInst(int index) {
		CapacitacionFinanciacion financiacion = capacitacionFinanciacionList
				.get(index);
		if (financiacion.getIdCapacitacionFinanciacion() != null) {
			capacitacionFinanciacionRemoveList.add(financiacion);
		}
		capacitacionFinanciacionList.remove(index);
	}
	public boolean habEliminarInst(int index){
		if(index==indexUp)
			return false;
		return true;
	}
	public boolean habEliminarCerrado(int index){
		if(index==indexUpPart)
			return false;
		return true;
	}

	public void editarFinanciacion(int index) {
		CapacitacionFinanciacion financiacion = capacitacionFinanciacionList
				.get(index);
		esEditFinaciacion = true;
		fuenteFinanciacion = financiacion.getFuenteFinanciacion();
		idCapacitacionFinaciacionEdit = financiacion
				.getIdCapacitacionFinanciacion();
		if (financiacion.getConfiguracionUo() != null) {
			institucionFinaciadora = InstitucionFinaciadora.getTipoPorId("oee");
			cargarOeeInsti(financiacion.getConfiguracionUo());
		} else {
			institucionFinaciadora = InstitucionFinaciadora
					.getTipoPorId("otras");
			idOtrasInstituciones = financiacion.getDatosEspecificosInst()
					.getIdDatosEspecificos();
		}
		if (institucionFinaciadora != null
				&& institucionFinaciadora.getId().equals("oee"))
			habOee = true;
		else
			habOee = false;

		indexUp = index;
	}

	public void limpiarInsti() {
		fuenteFinanciacion = null;
		institucionFinaciadora = null;
		if (institucionFinaciadora != null
				&& institucionFinaciadora.getId().equals("oee"))
			habOee = true;
		else
			habOee = false;
		idOtrasInstituciones = null;
		nivelEntidadOeeUtilFinanciacion.limpiar();
		esEditFinaciacion = false;
		indexUp = -1;
		idCapacitacionCerradaEdit = null;
		
	}

	public void cargarOeeInsti(ConfiguracionUoCab oee) {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtilFinanciacion
				.getIdSinNivelEntidad(oee.getEntidad().getSinEntidad()
						.getNenCodigo());
		nivelEntidadOeeUtilFinanciacion.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtilFinanciacion.setIdSinEntidad(oee.getEntidad()
				.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtilFinanciacion.setIdConfigCab(oee
				.getIdConfiguracionUo());

		nivelEntidadOeeUtilFinanciacion.init();

	}
	public void cargarOeeInstitucionDicta(ConfiguracionUoCab oee) {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtilInstitucion
				.getIdSinNivelEntidad(oee.getEntidad().getSinEntidad()
						.getNenCodigo());
		nivelEntidadOeeUtilInstitucion.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtilInstitucion.setIdSinEntidad(oee.getEntidad()
				.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtilInstitucion.setIdConfigCab(oee
				.getIdConfiguracionUo());

		nivelEntidadOeeUtilInstitucion.init();

	}


	public void cargarOeeCerrado(ConfiguracionUoCab oee, ConfiguracionUoDet uo) {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtilCerrado
				.getIdSinNivelEntidad(oee.getEntidad().getSinEntidad()
						.getNenCodigo());
		nivelEntidadOeeUtilCerrado.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtilCerrado.setIdSinEntidad(oee.getEntidad()
				.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtilCerrado.setIdConfigCab(oee.getIdConfiguracionUo());

		if (uo != null)
			nivelEntidadOeeUtilCerrado.setIdUnidadOrganizativa(uo
					.getIdConfiguracionUoDet());

		nivelEntidadOeeUtilCerrado.init();

	}

	public void addCerrado() {
		if (destinadoA == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar una Institucion Participadora antes de realizar esta acción. Verifique");
			return;
		}
		if (destinadoA.getId().equals("C")) {
			if(nivelEntidadOeeUtilCerrado.getIdSinNivelEntidad()==null){
				statusMessages
				.add(Severity.ERROR,
						"Debe seleccionar Un Nivel  antes de realizar esta acción. Verifique ");
					return;
			}
			if(nivelEntidadOeeUtilCerrado.getIdSinEntidad()==null){
				statusMessages
				.add(Severity.ERROR,
						"Debe seleccionar Una Entidad  antes de realizar esta acción. Verifique ");
					return;
			}
			if (nivelEntidadOeeUtilCerrado.getIdConfigCab() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar Una OEE  antes de realizar esta acción. Verifique ");
				return;
			}

		}
		if(cupo!=null&& cupo.longValue()<=0){
			statusMessages.add(Severity.ERROR,"El campo cupo debe ser mayor  a cero. verifique");
			return ;
		}
		CapacitacionCerrada aux = new CapacitacionCerrada();
		aux.setActivo(true);
		aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		aux.setCupo(cupo);

		aux.setFechaAlta(new Date());
		aux.setConfiguracionUo(em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtilCerrado.getIdConfigCab()));
		if (nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa() != null)
			aux.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class,
					nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa()));

		capacitacionCerradasList.add(aux);

		limpiarCerrado();

	}

	public void upCerrado() {
		if (destinadoA == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar una Institucion Participadora antes de realizar esta acción. Verifique");
			return;
		}
		if (destinadoA.getId().equals("C")) {
			if(nivelEntidadOeeUtilCerrado.getIdSinNivelEntidad()==null){
				statusMessages
				.add(Severity.ERROR,
						"Debe seleccionar Un Nivel  antes de realizar esta acción. Verifique ");
					return;
			}
			if(nivelEntidadOeeUtilCerrado.getIdSinEntidad()==null){
				statusMessages
				.add(Severity.ERROR,
						"Debe seleccionar Una Entidad  antes de realizar esta acción. Verifique ");
					return;
			}
			if (nivelEntidadOeeUtilCerrado.getIdConfigCab() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar Una OEE  antes de realizar esta acción. Verifique ");
				return;
			}

		}
		CapacitacionCerrada aux = new CapacitacionCerrada();

		if (idCapacitacionCerradaEdit != null) {
			aux = em.find(CapacitacionCerrada.class, idCapacitacionCerradaEdit);
			aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
			aux.setFechaMod(new Date());
		} else {
			aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			aux.setFechaAlta(new Date());
			aux.setActivo(true);
		}
		aux.setActivo(true);
		aux.setCupo(cupo);
		aux.setConfiguracionUo(em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtilCerrado.getIdConfigCab()));
		if (nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa() != null)
			aux.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class,
					nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa()));

		capacitacionCerradasList.remove(indexUpPart);
		capacitacionCerradasList.add(indexUpPart, aux);

		limpiarCerrado();

	}

	public void editarCerrado(int index) {
		CapacitacionCerrada cerrada = capacitacionCerradasList.get(index);
		esEditCerrado = true;
		idCapacitacionCerradaEdit = cerrada.getIdCapacitacionCerrada();
		cupo = cerrada.getCupo();

		if (cerrada.getConfiguracionUo() != null)
			cargarOeeCerrado(cerrada.getConfiguracionUo(),
					cerrada.getConfiguracionUoDet());

		if (destinadoA != null && destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
		esEditCerrado = true;
		indexUpPart = index;
	}

	public void limpiarCerrado() {
		nivelEntidadOeeUtilCerrado.limpiar();
		cupo = null;
		esEditCerrado = false;
		indexUpPart = -1;
		idCapacitacionCerradaEdit = null;
		if (destinadoA != null && destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
	}

	public void eliminarCerrado(int index) {
		CapacitacionCerrada cerrada = capacitacionCerradasList.get(index);
		if (cerrada.getIdCapacitacionCerrada() != null) {
			capacitacionCerradasRemoveList.add(cerrada);
		}
		capacitacionCerradasList.remove(index);
	}

	public String update() {
		try {
			if (!chkDatos())
				return null;
			/** 
			 * se guardan los datos faltantes
			 * */
		
			capacitaciones.setDatosEspecificosTipoCap(em.find(DatosEspecificos.class, idTipoCapacitacion));
			capacitaciones.setModalidad(modalidadCapacitacion.getId());
			capacitaciones
					.setRecepcionPostulacion(requierePublicacion.getValor());
			capacitaciones.setTipoCosto(valorAlf);
			capacitaciones.setCiudad(em.find(Ciudad.class, idCiudad));
			capacitaciones.setDestinadoA(destinadoA.getId());
			capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitaciones.setFechaMod(new Date());
			if(idInstEducativa!=null)
				capacitaciones.setInstitucionEducativa(em.find(InstitucionEducativa.class, idInstEducativa));
			else
				capacitaciones.setInstitucionEducativa(null);
			if(idConsultoras!=null)
				capacitaciones.setDatosEspecificosInstDicta(em.find(DatosEspecificos.class, idConsultoras));
			else
				capacitaciones.setDatosEspecificosInstDicta(null);
			if(nivelEntidadOeeUtilInstitucion.getIdConfigCab()!=null)
				capacitaciones.setConfiguracionUoCabOeeDicta(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtilInstitucion.getIdConfigCab()));
			else
				capacitaciones.setConfiguracionUoCabOeeDicta(null);
			
			/**
			 * Para el Caso de documento adjuntos
			 * */
			Documentos docAnterior = new Documentos();
			if (capacitaciones.getDocumentos() == null) {
				documento();

			} else if (item != null) {
				docAnterior = em.find(Documentos.class, capacitaciones
						.getDocumentos().getIdDocumento());
				idDocumento = AdmDocAdjuntoFormController.editar(
						item.getFile(), item.getFileSize(), item.getFileName(),
						item.getContentType(),
						docAnterior.getUbicacionFisica(),
						"editDatosObjeto290", docAnterior.getIdDocumento(), idTipoDocumento,
						usuarioLogueado.getIdUsuario(),
						capacitaciones.getIdCapacitacion(), "Capacitaciones");

			}
			if (item != null) {
				if (idDocumento != null) {

					if (idDocumento.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocumento.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocumento.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						if(capacitaciones.getDocumentos()!=null){
							nombreDoc=capacitaciones.getDocumentos().getNombreFisicoDoc();
							uploadedFileCopia=null;
							contentTypeCopia=null;
							fileNameCopia=null;
						}else
							nombreDoc=null;
						return null;
					}

				} else {
					statusMessages.add(Severity.ERROR,
							"Error al adjuntar el registro. Verifique");
					return null;
				}

				capacitaciones.setDocumentos(em.find(Documentos.class,
						idDocumento));
			}
			/**
			 * fin
			 * */

			em.merge(capacitaciones);
			em.flush();
			

			/**
			 * se guardan/actualizan los datos de las grillas
			 * */
			for (int i = 0; i < capacitacionFinanciacionList.size(); i++) {
				CapacitacionFinanciacion c = capacitacionFinanciacionList
						.get(i);
				c.setCapacitaciones(capacitaciones);
				if (c.getIdCapacitacionFinanciacion() == null)
					em.persist(c);
				else
					em.merge(c);
				em.flush();
			}
			if (destinadoA.getId().equals("C")) {
				for (int i = 0; i < capacitacionCerradasList.size(); i++) {
					CapacitacionCerrada cc = capacitacionCerradasList.get(i);
					cc.setCapacitaciones(getCapacitaciones());
					if (cc.getIdCapacitacionCerrada() == null)
						em.persist(cc);
					else
						em.merge(cc);
					em.flush();
				}
			}
			/**
			 * SE INACTIVAN LOS REGISTROS ELIMINADOS
			 * **/
			if (destinadoA.getId().equals("C")) {
				for (int i = 0; i < capacitacionCerradasRemoveList.size(); i++) {
					CapacitacionCerrada ccRemove = capacitacionCerradasRemoveList.get(i);
					ccRemove.setActivo(false);
					ccRemove.setUsuMod(usuarioLogueado.getCodigoUsuario());
					ccRemove.setFechaMod(new Date());
					em.merge(ccRemove);
					em.flush();
				}
			}
			for (int i = 0; i < capacitacionFinanciacionRemoveList.size(); i++) {
				CapacitacionFinanciacion cfRemove = capacitacionFinanciacionRemoveList
						.get(i);
				cfRemove.setActivo(false);
				cfRemove.setUsuMod(usuarioLogueado.getCodigoUsuario());
				cfRemove.setFechaMod(new Date());
				em.merge(cfRemove);
				em.flush();
			}
			primeraEntrada=true;
			nivelEntidadOeeUtilCerrado.limpiar();
			nivelEntidadOeeUtilFinanciacion.limpiar();
			nivelEntidadOeeUtilInstitucion.limpiar();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Se ha producido un error" + e.getMessage());
			return null;

		}

	}

	public void abrirDocumento() {

		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(capacitaciones
					.getDocumentos().getIdDocumento(), usuarioLogueado
					.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void documento() {
		try {
			String nombrePantalla = "editDatosObjeto290";
			String direccionFisica = "";
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());
			direccionFisica = "\\SICCA\\"
					+ anio
					+ "\\OEE\\"
					+ capacitaciones.getConfiguracionUoCab()
							.getIdConfiguracionUo()
					+ "\\UO\\"
					+ capacitaciones.getConfiguracionUoDet()
							.getIdConfiguracionUoDet() + "\\C\\"
					+ capacitaciones.getIdCapacitacion();
			idDocumento = AdmDocAdjuntoFormController.guardarDirecto(item,
					direccionFisica, nombrePantalla, idTipoDocumento,
					usuarioLogueado.getIdUsuario(), "Capacitaciones");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private boolean chkDatos() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}
			uploadedFileCopia=uploadedFile;
			contentTypeCopia=contentType;
			fileNameCopia=fileName;

		} else {
			if(uploadedFileCopia!=null){
				uploadedFile=uploadedFileCopia;
				contentType=contentTypeCopia;
				fileName=fileNameCopia;
			}else{
				item = null;
				uploadedFileCopia=null;
			}
			
		}
		if(capacitaciones.getNombre()==null || capacitaciones.getNombre().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Titulo antes de realizar esta acci\u00F3n ");
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(capacitaciones.getNombre().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(idTipoCapacitacion==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar el Tipo de Capacitaci\u00F3n antes de realizar esta acci\u00F3n ");
			return false;
		}
		
		
		if (requierePublicacion == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Requiere Publicaci\u00F3n Portal SICCA  antes de realizar esta acci\u00F3n");
			
			return false;
		}
		if (modalidadCapacitacion == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Modalidad antes de realizar esta acci\u00F3n");
		
			return false;
		}
		if (capacitaciones.getSede() == null
				|| capacitaciones.getSede().trim().equals("")) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Sede antes de realizar esta acci\u00F3n");
			
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(capacitaciones.getSede().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter")+" en el campo Sede");
			
			return false;
		}
		if(capacitaciones.getDireccion()!=null){
			if(seguridadUtilFormController.contieneCaracter(capacitaciones.getDireccion().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter")+" en el campo Direcci\u00F3n");
				
				return false;
			}
		}
		if (idPais == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Pais antes de realizar esta acci\u00F3n");
			
			return false;
		}
		if (idDepartamento == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe seleccionar el Departamento antes de realizar esta acci\u00F3n");
			return false;
		}
		if (idCiudad == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar la Ciudad antes de realizar esta acci\u00F3n");
			return false;
		}

		if (capacitaciones.getFechaInicio() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha de Inicio antes de realizar esta acci\u00F3n");
			return false;
		}
		if (capacitaciones.getFechaFin() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha Fin antes de realizar esta acci\u00F3n");
			return false;
		}
		if (!general.isFechaValida(capacitaciones.getFechaInicio())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Fecha de Inicio inv\u00E1lida. Verifique");
			return false;
		}
		if (!general.isFechaValida(capacitaciones.getFechaFin())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Fecha de Inicio inv\u00E1lida. Verifique");
			return false;
		}
		if (capacitaciones.getFechaInicio().after(capacitaciones.getFechaFin())) {
			statusMessages
					.add(Severity.ERROR,
							"La fecha de inicio no puede ser mayor a la fecha fin. Verifique");
			return false;
		}
		

		if(costo==null || costo.trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Costo, antes de realizar esta acci\u00F3n");
			return false;
		}
		if (costo != null && !costo.trim().equals("")) {
			try {
				capacitaciones
						.setCosto(Float.parseFloat(costo.replace(".", "")));
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"El campo costo debe ser numerico");
				return false;
			}

			if (capacitaciones.getCosto().intValue() <= 0) {
				statusMessages.add(Severity.ERROR,
						"El costo debe ser mayor a cero. Verifique");
				return false;
			}

		}
		if ((capacitaciones.getCupoMinimo() != null) && (capacitaciones.getCupoMaximo() != null)) {
			if ((capacitaciones.getCupoMinimo().longValue() <= 0) || (capacitaciones.getCupoMaximo().longValue() <= 0)) {
				statusMessages.add(Severity.ERROR,
						"Ambos cupos deben ser mayor a cero. Verifique");
				return false;
			}
		}
		if (capacitaciones.getCargaHoraria() == null) {
			statusMessages.add(Severity.ERROR,"Debe especificar el campo carga horaria. Verifique");
			return false;
		}
		if (capacitaciones.getCargaHoraria() != null) {
			if (capacitaciones.getCargaHoraria().longValue() <= 0) {
				statusMessages.add(Severity.ERROR,"La carga horaria debe ser mayor a cero. Verifique");
				return false;
			}
		}
		if(nivelEntidadOeeUtilInstitucion.getIdConfigCab()==null&& idInstEducativa==null && idConsultoras==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar una Instituci\u00F3n que Dicta el Curso. Verifique ");
			return false;
		}
		
		
		if (destinadoA == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Instituciones Participadoras, antes de realizar esta acci\u00F3n");
			return false;
		}

		if (destinadoA.getId().equals("C")) {
			if (capacitacionCerradasList.isEmpty()) {
				statusMessages
						.add(Severity.ERROR,
								"Debe especificar por lo menos una Instituciones Participadoras  antes de realizar esta acci\u00F3n");
				return false;
			}
		}

		if (idTipoDocumento == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
			return false;
		}

		if (item == null && capacitaciones.getDocumentos() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el archivo. Verifique");
			return false;
		}
		if (item != null) {
			String nomfinal = "";
			nomfinal = item.getFileName();
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> tam = em.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
				if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
						.intValue()) {
					statusMessages.add(Severity.ERROR,
							"El archivo supera el tamaño m\u00E1ximo permitido. L\u00EDmite "
									+ tam.get(0).getTamanho()
									+ tam.get(0).getUnidMedida()
									+ ", verifique");
					return false;
				}
			}
		}

		return true;
	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);

	}
	public void updateDepartamentoChange() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
		idDepartamento=null;
		idCiudad=null;
		updateCiudad();

	}


	/**
	 * Combo ciudad
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);

	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPais != null) {
			departamentoList.getPais().setIdPais(idPais);
			departamentoList.setOrder("descripcion");

			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDepartamento != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDepartamento);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildDepartamentoDirSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
		updateCiudad();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}
	
	
	public List<SelectItem> otrasInstitucionesSelecItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : otrasIntituciones())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> otrasIntituciones(){
		
		List<DatosEspecificos> d= em.createQuery("Select d from DatosEspecificos d " +
				" where d.datosGenerales.descripcion = 'INSTITUCIONES'  order by d.descripcion").getResultList();
		

		return d;
	}

	@SuppressWarnings("unchecked")
	private void findInstitucionFinanciadora() {
		capacitacionFinanciacionList = em.createQuery(
				"Select cf from CapacitacionFinanciacion cf "
						+ " where cf.capacitaciones.idCapacitacion="
						+ idCapacitacion + " and cf.activo=true")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findCerrado() {
		capacitacionCerradasList = em.createQuery(
				"Select cc from CapacitacionCerrada cc"
						+ " where cc.capacitaciones.idCapacitacion="
						+ idCapacitacion + " and cc.activo=true")
				.getResultList();
	}

	public void selecOee() {
		if (institucionFinaciadora.getId().equals("oee"))
			habOee = true;
		else
			habOee = false;
	}

	public void selecCerrado() {
		if (destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
	}

	public boolean cerrado() {

		return habCerrado;
	}
	
	public String back(){
		primeraEntrada=true;
		nivelEntidadOeeUtilCerrado.limpiar();
		nivelEntidadOeeUtilFinanciacion.limpiar();
		return "back";
	}
	
	private static String getNumber(String number) {
		double value;
		String numberFormat = "###,###,###,###";
		DecimalFormat formatter = new DecimalFormat(numberFormat);
		try {
			value = Double.parseDouble(number);
		} catch (Throwable t) {
			return null;
		}
		return formatter.format(value);
	}
	
	
	/** GETTER'S && SETTER'S **/

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	

	public ModalidadCapacitacion getModalidadCapacitacion() {
		return modalidadCapacitacion;
	}

	public void setModalidadCapacitacion(
			ModalidadCapacitacion modalidadCapacitacion) {
		this.modalidadCapacitacion = modalidadCapacitacion;
	}

	

	public SiNo getRequierePublicacion() {
		return requierePublicacion;
	}
	public void setRequierePublicacion(SiNo requierePublicacion) {
		this.requierePublicacion = requierePublicacion;
	}
	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public InstitucionFinaciadora getInstitucionFinaciadora() {
		return institucionFinaciadora;
	}

	public void setInstitucionFinaciadora(
			InstitucionFinaciadora institucionFinaciadora) {
		this.institucionFinaciadora = institucionFinaciadora;
	}

	public Long getIdOtrasInstituciones() {
		return idOtrasInstituciones;
	}

	public void setIdOtrasInstituciones(Long idOtrasInstituciones) {
		this.idOtrasInstituciones = idOtrasInstituciones;
	}

	public String getFuenteFinanciacion() {
		return fuenteFinanciacion;
	}

	public void setFuenteFinanciacion(String fuenteFinanciacion) {
		this.fuenteFinanciacion = fuenteFinanciacion;
	}

	public DestinadoA getDestinadoA() {
		return destinadoA;
	}

	public void setDestinadoA(DestinadoA destinadoA) {
		this.destinadoA = destinadoA;
	}

	public Long getCupo() {
		return cupo;
	}

	public void setCupo(Long cupo) {
		this.cupo = cupo;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
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

	public List<CapacitacionFinanciacion> getCapacitacionFinanciacionList() {
		return capacitacionFinanciacionList;
	}

	public void setCapacitacionFinanciacionList(
			List<CapacitacionFinanciacion> capacitacionFinanciacionList) {
		this.capacitacionFinanciacionList = capacitacionFinanciacionList;
	}

	public List<CapacitacionCerrada> getCapacitacionCerradasList() {
		return capacitacionCerradasList;
	}

	public void setCapacitacionCerradasList(
			List<CapacitacionCerrada> capacitacionCerradasList) {
		this.capacitacionCerradasList = capacitacionCerradasList;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public boolean isHabOee() {
		return habOee;
	}

	public void setHabOee(boolean habOee) {
		this.habOee = habOee;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public boolean isHabCerrado() {
		return habCerrado;
	}

	public void setHabCerrado(boolean habCerrado) {
		this.habCerrado = habCerrado;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilFinanciacion() {
		return nivelEntidadOeeUtilFinanciacion;
	}

	public void setNivelEntidadOeeUtilFinanciacion(
			NivelEntidadOeeUtil nivelEntidadOeeUtilFinanciacion) {
		this.nivelEntidadOeeUtilFinanciacion = nivelEntidadOeeUtilFinanciacion;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilCerrado() {
		return nivelEntidadOeeUtilCerrado;
	}

	public void setNivelEntidadOeeUtilCerrado(
			NivelEntidadOeeUtil nivelEntidadOeeUtilCerrado) {
		this.nivelEntidadOeeUtilCerrado = nivelEntidadOeeUtilCerrado;
	}

	public Long getIdCapacitacionFinaciacionEdit() {
		return idCapacitacionFinaciacionEdit;
	}

	public void setIdCapacitacionFinaciacionEdit(
			Long idCapacitacionFinaciacionEdit) {
		this.idCapacitacionFinaciacionEdit = idCapacitacionFinaciacionEdit;
	}

	public boolean isEsEditFinaciacion() {
		return esEditFinaciacion;
	}

	public void setEsEditFinaciacion(boolean esEditFinaciacion) {
		this.esEditFinaciacion = esEditFinaciacion;
	}

	public Long getIdCapacitacionCerradaEdit() {
		return idCapacitacionCerradaEdit;
	}

	public void setIdCapacitacionCerradaEdit(Long idCapacitacionCerradaEdit) {
		this.idCapacitacionCerradaEdit = idCapacitacionCerradaEdit;
	}

	public boolean isEsEditCerrado() {
		return esEditCerrado;
	}

	public void setEsEditCerrado(boolean esEditCerrado) {
		this.esEditCerrado = esEditCerrado;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public int getIndexUpPart() {
		return indexUpPart;
	}

	public void setIndexUpPart(int indexUpPart) {
		this.indexUpPart = indexUpPart;
	}

	public List<CapacitacionFinanciacion> getCapacitacionFinanciacionRemoveList() {
		return capacitacionFinanciacionRemoveList;
	}

	public void setCapacitacionFinanciacionRemoveList(
			List<CapacitacionFinanciacion> capacitacionFinanciacionRemoveList) {
		this.capacitacionFinanciacionRemoveList = capacitacionFinanciacionRemoveList;
	}

	public List<CapacitacionCerrada> getCapacitacionCerradasRemoveList() {
		return capacitacionCerradasRemoveList;
	}

	public void setCapacitacionCerradasRemoveList(
			List<CapacitacionCerrada> capacitacionCerradasRemoveList) {
		this.capacitacionCerradasRemoveList = capacitacionCerradasRemoveList;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	public String getCosto() {
		return costo;
	}

	public void setCosto(String costo) {
		this.costo = costo;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}

	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}
	public String getValorAlf() {
		return valorAlf;
	}
	public void setValorAlf(String valorAlf) {
		this.valorAlf = valorAlf;
	}
	public NivelEntidadOeeUtil getNivelEntidadOeeUtilInstitucion() {
		return nivelEntidadOeeUtilInstitucion;
	}
	public void setNivelEntidadOeeUtilInstitucion(
			NivelEntidadOeeUtil nivelEntidadOeeUtilInstitucion) {
		this.nivelEntidadOeeUtilInstitucion = nivelEntidadOeeUtilInstitucion;
	}
	public Long getIdConsultoras() {
		return idConsultoras;
	}
	public void setIdConsultoras(Long idConsultoras) {
		this.idConsultoras = idConsultoras;
	}
	public Long getIdInstEducativa() {
		return idInstEducativa;
	}
	public void setIdInstEducativa(Long idInstEducativa) {
		this.idInstEducativa = idInstEducativa;
	}
	

}
