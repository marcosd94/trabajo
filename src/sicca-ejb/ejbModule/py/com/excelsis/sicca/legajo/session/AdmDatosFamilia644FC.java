package py.com.excelsis.sicca.legajo.session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.AuditLegajo;
import py.com.excelsis.sicca.entity.AuditLegajoDet;
import py.com.excelsis.sicca.entity.AuditLegajoObs;
import py.com.excelsis.sicca.entity.CamposLegajo;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DeclaracionJurada;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstudiosRealizadosLegajo;
import py.com.excelsis.sicca.entity.Familiares;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admDatosFamilia644FC")
public class AdmDatosFamilia644FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	private PersonaDTO personaDTO;
	private Long idGradoParentezco = null;
	private Integer idPersonasACargo = null;
	private Integer idFuncionarioPublicoActivo = null;
	private Integer idObservacion = null;
	private String observaciones;

	private Long idPais;
	private Long idPersonaFamiliar;
	private String nroDoc;
	private String nombres;
	private String apellidos;
	private Date fechaNacimiento;
	private String telefono;

	private String email;
	private String existePersonasAcargo;
	private Long idOee;
	private Boolean habBtnAddPersons = false;
	private String idEstadoCivil;
	private String idSexo;

	private Long idPersona;
	private Persona persona;
	private Integer idElemUpdate;
	private Boolean tieneCedula;
	private Boolean personaACargo = false;
	private String texto;
	private String obsSiccaUo;
	private static String SEXO = "SEXO";
	private static String FECHA_NAC = "FECHA_NAC";
	private static String PERSONAS_A_CARGO = "PERSONAS_A_CARGO";
	private static String DATOS_ESPE_FAMILIAR = "DATOS_ESPE_FAMILIAR";
	HashMap<String, String> mapaAudit = new HashMap<String, String>();

	private List<Familiares> lGrilla1;
	
	private Long idTipoDoc;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private UploadItem item;
	private String nombreDoc;
	private String nombrePantalla;
	private Boolean show644 = true;
	private Boolean esOtro = false;
	private String descTipoDoc;
	
	
	//PARA LA CARGA DE OTROS DOCUMENTOS ADJUNTOS
	private Familiares familiares;
	private Long idFamiliar;
	private List <Documentos> familiaresDocList = new ArrayList();
	private Integer tamArchivo;
	private String from;
	
	
	

	public void init() {
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		cargarCabecera();
		cargarGrilla1();
		idPais = idParaguay();
		if(idPersonaFamiliar != null){
			habBtnAddPersons = false;
			Persona personaRecibida = em.find(Persona.class, idPersonaFamiliar);
			if(personaRecibida != null){
				copiarDatosInteres(personaRecibida, null);
				completarDatosPersona(personaRecibida, false);
			}
		}
	}
	
	
	//PARA AGREGAR DOCUMENTOS NUEVOS
	public void initAgregarDocumento(){
		
		this.familiares = em.find(Familiares.class, this.idFamiliar);
		this.persona = em.find(Persona.class, familiares.getPersona().getIdPersona());
		this.familiaresDocList = obtenerDocumentosXFamiliares(this.idFamiliar);
		
		
	}
		
		
	private List<Documentos> obtenerDocumentosXFamiliares(Long idFamiliar){
		String sql = "select * from general.documentos where activo = true and nombre_pantalla = 'AdmDatosFamilia644' and nombre_tabla = 'Familiares' "
				+ "and id_tabla =  " + idFamiliar;
		
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}
	
	public void btnAgregarDocumento(){
		
		Documentos doc = this.addDocumento();
		if(doc != null){
			doc.setIdTabla(familiares.getIdFamiliar());
			doc.setDescripcion(descTipoDoc);
			em.merge(doc);
			em.flush();
			esOtro = false;
			descTipoDoc = "";
			this.familiaresDocList = obtenerDocumentosXFamiliares(familiares.getIdFamiliar());
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Documento adjuntado correctamente");
		}else{
			return;
			}
	}
	
	public void eliminarDocumento(Long idDocumento){
		Documentos doc =  em.find(Documentos.class,idDocumento);
		doc.setActivo(false);
		doc.setFechaMod(new Date());
		doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(doc);
		em.flush();
		this.familiaresDocList = obtenerDocumentosXFamiliares(this.idFamiliar);
		statusMessages.clear();
		statusMessages.add(Severity.INFO, "Documento eliminado correctamente");
	}
	
		
	public String volverFamiliares(){
		
		return "/legajos/MenuDatosPersonales.xhtml";
	}
	
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	private void cargarGrilla1() {
		Query q =
			em.createQuery("Select Familiares from Familiares Familiares where Familiares.persona.idPersona = :idPersona  and Familiares.activo is true");
		q.setParameter("idPersona", idPersona);
		lGrilla1 = q.getResultList();
	}

	private Boolean precondInit() {

		return true;
	}

	private void cargarCabecera() {
		caragrObsUoSicca();
	}

	private List<DatosEspecificos> traerGradoParentezco() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
				+ " and DatosEspecificos.activo is true order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "GRADO DE PARENTESCO");

		return q.getResultList();
	}

	public Familiares updateFamiliares(Familiares familiares) {
		if (familiares.getDatosEspecificosFamiliar() != null) {
			DatosEspecificos de =
				em.find(DatosEspecificos.class, familiares.getDatosEspecificosFamiliar().getIdDatosEspecificos());
			familiares.setDatosEspecificosFamiliar(de);
		}
		if (familiares.getConfiguracionUoCab() != null) {
			ConfiguracionUoCab cuo =
				em.find(ConfiguracionUoCab.class, familiares.getConfiguracionUoCab().getIdConfiguracionUo());
			familiares.setConfiguracionUoCab(cuo);
		}
		return familiares;
	}

	public List<SelectItem> traerDatosParentezcoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione..."));
		for (DatosEspecificos o : traerGradoParentezco())
			si.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		return si;
	}

	private List<Referencias> traerPersonasACargo() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.activo is true and Referencias.dominio = 'LEGAJO_DEPEND' order by Referencias.descLarga  ");
		return q.getResultList();
	}

	public List<SelectItem> traerPersonasACargoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione..."));
		for (Referencias o : traerPersonasACargo())
			si.add(new SelectItem(o.getValorNum(), o.getDescLarga()));
		return si;
	}

	private List<Referencias> traerFuncionarioPublico() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.activo is true and Referencias.dominio = 'LEGAJO_FUNC' order by Referencias.descLarga ");
		return q.getResultList();
	}

	public List<SelectItem> traerFuncionarioPublicoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione..."));
		for (Referencias o : traerFuncionarioPublico())
			si.add(new SelectItem(o.getValorNum(), o.getDescLarga()));
		return si;
	}

	private List<Referencias> traerObservacion() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.activo is true and Referencias.dominio = 'LEGAJO_OBS' order by Referencias.descLarga ");
		return q.getResultList();
	}

	public List<SelectItem> traerObservacionSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione..."));
		for (Referencias o : traerObservacion())
			si.add(new SelectItem(o.getValorNum(), o.getDescLarga()));
		return si;
	}

	private List<ConfiguracionUoCab> traerOee() {
		Query q =
			em.createQuery("select ConfiguracionUoCab from ConfiguracionUoCab ConfiguracionUoCab "
				+ " where ConfiguracionUoCab.activo is true order by ConfiguracionUoCab.denominacionUnidad ");
		return q.getResultList();
	}

	public String descLargaRef(String dscAbrev, String dominio) {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio =:dominio and Referencias.descAbrev = :dscAbrev");
		q.setParameter("dominio", dominio);
		q.setParameter("dscAbrev", dscAbrev);
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getDescLarga();
		} else {
			return null;
		}

	}

	public List<SelectItem> traerOeeSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione..."));
		for (ConfiguracionUoCab o : traerOee())
			si.add(new SelectItem(o.getIdConfiguracionUo(), o.getDenominacionUnidad()));

		return si;
	}

	public List<Referencias> traerSexo() {
		try {
			return em.createQuery(" select o from Referencias o WHERE o.activo = true and o.dominio = 'SEXO' ORDER BY o.descLarga").getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	public List<SelectItem> traerSexoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : traerSexo())
			si.add(new SelectItem(o.getDescAbrev(), o.getDescLarga()));
		return si;
	}

	public List<SelectItem> traerEstadoCivilSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : traerEstadoCivil())
			si.add(new SelectItem(o.getDescAbrev(), o.getDescLarga()));
		return si;
	}

	public List<Referencias> traerEstadoCivil() {
		try {
			return em.createQuery(" select o from Referencias o  WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' ORDER BY o.dominio, o.descLarga").getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	public String referencia(Long valorNum) {
		if (valorNum != null) {
			Query q =
				em.createQuery("select Referencias from Referencias Referencias where Referencias.valorNum = :valorNum "
					+ " and Referencias.dominio = 'LEGAJO_FUNC'");
			q.setParameter("valorNum", valorNum.intValue());
			List<Referencias> lista = q.getResultList();
			if (lista.size() > 0) {
				Referencias ref = lista.get(0);
				if (ref != null)
					return ref.getDescAbrev();
			}
		}
		return null;
	}
	
	
	public void limpiarDatosPersona() {
		nombres = null;
		apellidos = null;
		telefono = null;
		email = null;
		nroDoc = null;
		nombreDoc = null;
		fileName = null;
		email = null;
		telefono = null;
		idSexo = null;
		idEstadoCivil = null;
		idPersonasACargo = null;
		fechaNacimiento = null;
		idObservacion = null;
		idFuncionarioPublicoActivo = null;
		observaciones = null;
		// idGradoParentezco = null;
		idPersonasACargo = null;
		idOee = null;
		esOtro = false;
		descTipoDoc = "";
		personaDTO = null;
	}

	public String linkAddPersona() {
		habBtnAddPersons = false;
		String direccion = "/seleccion/persona/PersonaEdit.seam?personaFrom=/legajos/AdmDatosFamilia/AdmDatosFamilia644&idPersonaFamiliar=" + idPersona;
		//String direccion = "/seleccion/persona/PersonaEdit.seam?personaFrom=/legajos/MenuDatosPersonales&show644=true&texto='P'";
		return direccion;
	}

	public void buscarPersona() throws Exception {

		/* Validaciones */
		if (idPais == null
			|| !seguridadUtilFormController.validarInput(idPais.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		Pais pais = em.find(Pais.class, idPais);
		if (pais == null)
			return;
		/* fin validaciones */
		
		personaDTO = seleccionUtilFormController.buscarPersona(nroDoc, pais.getDescripcion());

		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2();

		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;

			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			copiarDatosInteres(personaDTO.getPersona(), null);
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}

	private void copiarDatosInteres(Persona persona, Familiares familiares) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (persona != null) {
			mapaAudit.clear();
			mapaAudit.put(SEXO, persona.getSexo());
			mapaAudit.put(FECHA_NAC, sdf.format(persona.getFechaNacimiento()));
		} else if (familiares != null) {
			mapaAudit.clear();
			if(familiares.getPersonaFamiliar() != null){
				mapaAudit.put(SEXO, familiares.getPersonaFamiliar().getSexo());
				mapaAudit.put(FECHA_NAC, sdf.format(familiares.getPersonaFamiliar().getFechaNacimiento()));
			}else{
				mapaAudit.put(SEXO, familiares.getSexo());
				mapaAudit.put(FECHA_NAC, sdf.format(familiares.getFechaNacimiento()));
			}
				
			if (familiares.getPersonasACargo() != null)
				mapaAudit.put(PERSONAS_A_CARGO, familiares.getPersonasACargo().toString());
			else
				mapaAudit.put(PERSONAS_A_CARGO, null);
			mapaAudit.put(DATOS_ESPE_FAMILIAR, familiares.getDatosEspecificosFamiliar().getDescripcion());
		}
	}

	private void completarDatosPersona(Persona persona, Boolean completo) {
		em.refresh(persona);
		nombres = persona.getNombres();
		apellidos = persona.getApellidos();
		email = persona.getEMail();
		telefono = persona.getTelefonos() == null ? "" : persona.getTelefonos();
		email = persona.getEMail() == null ? "" : persona.getEMail();
		idSexo = persona.getSexo();
		idEstadoCivil = persona.getEstadoCivil();
		fechaNacimiento = persona.getFechaNacimiento();
		nroDoc = persona.getDocumentoIdentidad();
		if (completo) {
			idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			nroDoc = persona.getDocumentoIdentidad();
		}
	}
	
	private void completarDatosPersonaSinCedula(Familiares familiar) {
		
		nombres = familiar.getNombresFamiliar();
		apellidos = familiar.getApellidosFamiliar();
		email = familiar.getEMail();
		telefono = familiar.getTelefonos() == null ? "" : familiar.getTelefonos();
		email = familiar.getEMail() == null ? "" : familiar.getEMail();
		idSexo = familiar.getSexo();
		idEstadoCivil = familiar.getEstadoCivil();
		fechaNacimiento = familiar.getFechaNacimiento();
		idPais = familiar.getPaisByIdPaisExpedicionDoc().getIdPais();
		fileName = familiar.getDocumentos().getNombreFisicoDoc();
		idTipoDoc = familiar.getDocumentos().getDatosEspecificos().getIdDatosEspecificos();
		descTipoDoc = familiar.getDocumentos().getDescripcion();

		
	}

	private void completarDatosFamiliar(Familiares familiar) {
		nombres = familiar.getNombresFamiliar();
		apellidos = familiar.getApellidosFamiliar();
		email = familiar.getEMail();
		telefono = familiar.getTelefonos() == null ? "" : familiar.getTelefonos();
		email = familiar.getEMail() == null ? "" : familiar.getEMail();
		idSexo = familiar.getSexo();
		idEstadoCivil = familiar.getEstadoCivil();
		fechaNacimiento = familiar.getFechaNacimiento();
		nroDoc = familiar.getPersonaFamiliar().getDocumentoIdentidad();
		idPais = familiar.getPaisByIdPaisExpedicionDoc().getIdPais();
	}
	public void limpiarDatosPersona2() {
		nombres = null;
		apellidos = null;
		telefono = null;
		email = null;
		idEstadoCivil = null;
		idSexo = null;
		telefono = null;
		email = null;
		observaciones = null;
		//nroDoc = null;

	}

	public void editar(int index) {
		limpiar();
		if (index < lGrilla1.size()) {
			idElemUpdate = index;
			Familiares entity = lGrilla1.get(idElemUpdate);
			idGradoParentezco = entity.getDatosEspecificosFamiliar().getIdDatosEspecificos();
			idObservacion =
				(entity.getObservacion() != null ? entity.getObservacion().intValue() : null);
			idPersonasACargo =
				(entity.getPersonasACargo() != null ? entity.getPersonasACargo().intValue() : null);
			idFuncionarioPublicoActivo = entity.getFuncionarioPublico();
			idOee =
				(entity.getConfiguracionUoCab() != null
					? entity.getConfiguracionUoCab().getIdConfiguracionUo() : null);
			observaciones =
					(entity.getObservaciones() != null ? entity.getObservaciones(): null);
			copiarDatosInteres(null, entity);
			personaACargo = entity.getPersonaACargo();
			if(entity.getPersonaFamiliar() != null){
				completarDatosFamiliar(entity);
				fileName = entity.getDocumentos().getNombreFisicoDoc();
				idTipoDoc = entity.getDocumentos().getDatosEspecificos().getIdDatosEspecificos();
				tieneCedula = true;
			}else{
				completarDatosPersonaSinCedula(entity);
				tieneCedula = false;
			}
		}
	}

	public String agregar() {
		if (!precondAgregar(true))
			return "FAIL";
		try {
			
			//agregar Documento
			if(idPersona != null)
				persona = em.find(Persona.class, idPersona);
			
			Documentos doc = addDocumento();
					
			Familiares familiares = new Familiares();
			
			if(doc != null){
				doc.setDescripcion(descTipoDoc);
				familiares.setDocumentos(doc);
			}
			else {
//				statusMessages.add("No se pudo guardar el Documento.");
				return null;
			}
			familiares.setActivo(true);
			familiares.setDatosEspecificosFamiliar(new DatosEspecificos());
			familiares.getDatosEspecificosFamiliar().setIdDatosEspecificos(idGradoParentezco);
			familiares.setPersonaACargo(personaACargo);
			if(idPersona != null){
				familiares.setPersona(new Persona());
				familiares.getPersona().setIdPersona(idPersona);
			}
			
			if(personaDTO != null && personaDTO.getPersona() != null && personaDTO.getPersona().getIdPersona() != null){
				familiares.setPersonaFamiliar(personaDTO.getPersona());
//				familiares.getPersonaFamiliar().setTelefonos(telefono);
//				familiares.getPersonaFamiliar().setEMail(email);
//				familiares.getPersonaFamiliar().setSexo(idSexo);
//				familiares.getPersonaFamiliar().setEstadoCivil(idEstadoCivil);
//				familiares.getPersonaFamiliar().setFechaNacimiento(fechaNacimiento);
//				familiares.getPersonaFamiliar().setUsuMod(usuarioLogueado.getCodigoUsuario());
//				familiares.getPersonaFamiliar().setFechaMod(new Date());
				
				
				familiares.setPersonaFamiliar(em.merge(familiares.getPersonaFamiliar()));
				
			}
			
			/*if(idPersonaFamiliar != null){
				familiares.setPersonaFamiliar(em.find(Persona.class, idPersonaFamiliar));
				familiares.getPersonaFamiliar().setTelefonos(telefono);
				familiares.getPersonaFamiliar().setEMail(email);
				familiares.getPersonaFamiliar().setSexo(idSexo);
				familiares.getPersonaFamiliar().setEstadoCivil(idEstadoCivil);
				familiares.getPersonaFamiliar().setFechaNacimiento(fechaNacimiento);
				familiares.getPersonaFamiliar().setUsuMod(usuarioLogueado.getCodigoUsuario());
				familiares.getPersonaFamiliar().setFechaMod(new Date());
				
				
				familiares.setPersonaFamiliar(em.merge(familiares.getPersonaFamiliar()));
				
			}*/
		
			if(nombres != null && !nombres.trim().isEmpty())
				familiares.setNombresFamiliar(nombres);
			
			if(apellidos != null && !apellidos.trim().isEmpty())
				familiares.setApellidosFamiliar(apellidos);
			
			if(idSexo != null)
				familiares.setSexo(idSexo);
			if(idEstadoCivil != null)
				familiares.setEstadoCivil(idEstadoCivil);
			if(fechaNacimiento != null)
				familiares.setFechaNacimiento(fechaNacimiento);
			if(idPais != null)
				familiares.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, idPais));
			if (observaciones != null && !observaciones.trim().isEmpty())
				familiares.setObservaciones(observaciones);
			if (email != null && !email.trim().isEmpty())
				familiares.setEMail(email);;
			if (telefono != null && !telefono.trim().isEmpty())
				familiares.setTelefonos(telefono);
			
			
			if (idPersonasACargo != null)
				familiares.setPersonasACargo(idPersonasACargo.longValue());
			familiares.setFuncionarioPublico(idFuncionarioPublicoActivo);
			if (idObservacion != null)
				familiares.setObservacion(idObservacion.longValue());

			if (idOee != null) {
				familiares.setConfiguracionUoCab(new ConfiguracionUoCab());
				familiares.getConfiguracionUoCab().setIdConfiguracionUo(idOee);
			}
			familiares.setFechaAlta(new Date());
			familiares.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(familiares);

			if (texto != null && texto.equalsIgnoreCase("P")) {
				auditoria(familiares, "AGREGAR");
			}

			em.flush();
			if(doc != null){
				doc.setIdTabla(familiares.getIdFamiliar());
				em.merge(doc);
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			// refresh
			cargarGrilla1();
			limpiarDatosPersona();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return null;

		}
	}
	
	
	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}
	
	@SuppressWarnings("unchecked")
	public Documentos addDocumento() {
				
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem("legajo_"+fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return null;
			}

		}

		if (idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Tipo de Documento");
			return null;
		}
		if (item == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Archivo");
			return null;
		}
		/**
		 * Para el Caso de documento adjuntos
		 */
		if (item != null) {
			/**
			 * VALIDACION AGREGADA
			 * */
			String nomfinal = "";
			nomfinal = item.getFileName();
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> ta = em.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
				if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
						.intValue()) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo supera el tamaño máximo permitido. Límite "
											+ ta.get(0).getTamanho()
											+ ta.get(0).getUnidMedida()
											+ ", verifique");
					return null;
				}
			}
			/**
			 * FIN
			 * */
			try {
				documentoLegajo();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			if (idDoc != null) {

				if (idDoc.longValue() == -8) {
					statusMessages
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return null;
				}
				if (idDoc.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return null;
				}
				if (idDoc.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return null;
				}

				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
//				doc.setIdTabla(persona.getIdPersona());
				doc.setPersona(persona);
				doc.setValidoLegajo(true);
				em.merge(doc);
				em.flush();
								
				idTipoDoc = null;
				idDoc = null;
				item = null;
				nombreDoc = null;
				
				
				return doc;
				
			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return null;
			}
		}
		return null;
	}
	
	
	public void documentoLegajo() throws NamingException {
		if(idPersona != null)
			persona = em.find(Persona.class, idPersona);
		nombrePantalla = "AdmDatosFamilia644";
		String cSeparador=File.separator;
		String direccionFisica = cSeparador+"LEGAJO"+cSeparador
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona()+cSeparador+"DATOS_FAMILIARES"+cSeparador;
		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "Familiares");

	}
	
	public void imprimirDocumento(Long id_documento){
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id_documento,usuarioLogueado.getCodigoUsuario());
	}
	
	public String ImprimirNombreYApellidos(Familiares familiar){
		if(familiar.getPersonaFamiliar() != null)
			return familiar.getPersonaFamiliar().getNombreCompleto();
		else
			return familiar.getNombresFamiliar()+" "+ familiar.getApellidosFamiliar();
	}


	private void auditoria(Familiares familiares, String operacion) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Calcular cabecera
		Query q =
			em.createQuery("select AuditLegajo from AuditLegajo AuditLegajo where AuditLegajo.persona.idPersona =:idPersona ");
		q.setParameter("idPersona", familiares.getPersona().getIdPersona());
		List<AuditLegajo> lista = q.getResultList();
		AuditLegajo auditLegajo = null;
		if (lista.size() == 0) {
			auditLegajo = new AuditLegajo();
			auditLegajo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			auditLegajo.setFechaAlta(new Date());
			auditLegajo.setEstado(2);
			auditLegajo.setPersona(new Persona());
			auditLegajo.getPersona().setIdPersona(familiares.getPersona().getIdPersona());
			em.persist(auditLegajo);
		} else {
			auditLegajo = lista.get(0);
		}

		if ((mapaAudit.get(SEXO) == null && familiares.getPersonaFamiliar().getSexo() != null)
			|| !mapaAudit.get(SEXO).equalsIgnoreCase(familiares.getPersonaFamiliar().getSexo())) {
			AuditLegajoDet auditLegajoDet = new AuditLegajoDet();
			auditLegajoDet.setAuditLegajo(auditLegajo);
			auditLegajoDet.setIdPersonaFamiliar(familiares.getPersonaFamiliar().getIdPersona());
			if (operacion.equalsIgnoreCase("AGREGAR")) {
				auditLegajoDet.setAccion("I");
			} else {
				auditLegajoDet.setAccion("U");
				auditLegajoDet.setNuevo(familiares.getPersonaFamiliar().getSexo());
				auditLegajoDet.setAnterior(mapaAudit.get(SEXO));
			}
			auditLegajoDet.setNuevo(familiares.getPersonaFamiliar().getSexo());
			auditLegajoDet.setCampo("sexo");
			auditLegajoDet.setTabla("Persona");
			auditLegajoDet.setEstado(2);
			auditLegajoDet.setFechaAlta(new Date());
			auditLegajoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(auditLegajoDet);
		}
		if (!mapaAudit.get(FECHA_NAC).equalsIgnoreCase(sdf.format(familiares.getPersonaFamiliar().getFechaNacimiento()))) {
			AuditLegajoDet auditLegajoDet = new AuditLegajoDet();
			auditLegajoDet.setAuditLegajo(auditLegajo);
			auditLegajoDet.setIdPersonaFamiliar(familiares.getPersonaFamiliar().getIdPersona());
			if (operacion.equalsIgnoreCase("AGREGAR")) {
				auditLegajoDet.setAccion("I");
			} else {
				auditLegajoDet.setAccion("U");
				auditLegajoDet.setAnterior(mapaAudit.get(FECHA_NAC));
			}
			auditLegajoDet.setNuevo(sdf.format(familiares.getPersonaFamiliar().getFechaNacimiento()));
			auditLegajoDet.setCampo("fecha_nacimiento");
			auditLegajoDet.setTabla("Persona");
			auditLegajoDet.setEstado(2);
			auditLegajoDet.setFechaAlta(new Date());
			auditLegajoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(auditLegajoDet);
		}
		if (familiares.getPersonasACargo() != null
			&& mapaAudit.get(PERSONAS_A_CARGO) != null
			&& !mapaAudit.get(PERSONAS_A_CARGO).equalsIgnoreCase(familiares.getPersonasACargo().toString())) {
			AuditLegajoDet auditLegajoDet = new AuditLegajoDet();
			auditLegajoDet.setAuditLegajo(auditLegajo);
			auditLegajoDet.setIdPersonaFamiliar(familiares.getPersonaFamiliar().getIdPersona());
			if (operacion.equalsIgnoreCase("AGREGAR")) {
				auditLegajoDet.setAccion("I");
			} else {
				auditLegajoDet.setAccion("U");
				auditLegajoDet.setAnterior(mapaAudit.get(PERSONAS_A_CARGO));
			}
			auditLegajoDet.setNuevo(familiares.getPersonasACargo().toString());
			auditLegajoDet.setCampo("Personas_A_Cargo");
			auditLegajoDet.setTabla("FAMILIARES");
			auditLegajoDet.setEstado(2);
			auditLegajoDet.setFechaAlta(new Date());
			auditLegajoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(auditLegajoDet);
		}
		if (familiares.getPersonasACargo() != null
			&& !mapaAudit.get(DATOS_ESPE_FAMILIAR).equalsIgnoreCase(familiares.getDatosEspecificosFamiliar().getDescripcion())) {
			AuditLegajoDet auditLegajoDet = new AuditLegajoDet();
			auditLegajoDet.setAuditLegajo(auditLegajo);
			auditLegajoDet.setIdPersonaFamiliar(familiares.getPersonaFamiliar().getIdPersona());
			if (operacion.equalsIgnoreCase("AGREGAR")) {
				auditLegajoDet.setAccion("I");
			} else {
				auditLegajoDet.setAccion("U");
				auditLegajoDet.setAnterior(mapaAudit.get(DATOS_ESPE_FAMILIAR));
			}
			auditLegajoDet.setNuevo(familiares.getDatosEspecificosFamiliar().getDescripcion());
			auditLegajoDet.setCampo("id_datos_especificos_familiar");
			auditLegajoDet.setTabla("FAMILIARES");
			auditLegajoDet.setEstado(2);
			auditLegajoDet.setFechaAlta(new Date());
			auditLegajoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(auditLegajoDet);
		}
	}

	private Boolean precondAgregar(Boolean modoAgregar) {

		if (idGradoParentezco == null || idPais == null || nombres == null
			|| nombres.trim().isEmpty() || apellidos == null || apellidos.trim().isEmpty()
			|| fechaNacimiento == null || idSexo == null /*|| idEstadoCivil == null*/) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		if (modoAgregar)
			if (lGrilla1 != null && lGrilla1.size() > 0) {
				if(personaDTO != null && personaDTO.getPersona() != null && personaDTO.getPersona().getIdPersona() != null){
					for (Familiares o : lGrilla1) {
						if(o.getPersonaFamiliar() != null && o.getPersonaFamiliar().getIdPersona() != null){
							if (o.getPersonaFamiliar().getIdPersona().longValue() == personaDTO.getPersona().getIdPersona().longValue()) {
								statusMessages.add(Severity.ERROR, personaDTO.getPersona().getNombres()
									+ " " + personaDTO.getPersona().getApellidos()
									+ " ya se encuentra en la lista ");
								return false;
							}
						}
					}
				}
			}
		if (nombres.length() > 100) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Nombres (100)");
			return false;
		}
		if (apellidos.length() > 80) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Apellidos (80)");
			return false;
		}
		if (email != null && email.length() > 50) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Email (50)");
			return false;
		}
		if (email != null && !email.trim().isEmpty() && !General.isEmail(email)) {
			statusMessages.add(Severity.ERROR, "La dirección de correo es incorrecta");
			return false;
		}

		return true;
	}

	public String actualizar() {
		if (!precondAgregar(false)) {
			return null;
		}
		if (idElemUpdate != null) {
			try {
				Documentos doc = null;
				
				if(fileName != null && !fileName.trim().isEmpty() && idTipoDoc != null)
					 doc = addDocumento();
				
				Familiares familiares = lGrilla1.get(idElemUpdate);
				familiares.setDatosEspecificosFamiliar(new DatosEspecificos());
				familiares.getDatosEspecificosFamiliar().setIdDatosEspecificos(idGradoParentezco);
				familiares.setPersonaACargo(personaACargo);
				
				if(doc != null){
					familiares.getDocumentos().setActivo(false);
					familiares.getDocumentos().setFechaMod(new Date());
					familiares.getDocumentos().setUsuMod(usuarioLogueado.getCodigoUsuario());
					doc.setDescripcion(descTipoDoc);
					familiares.setDocumentos(doc);
					//em.persist(doc);
				}
				
				
//				if(familiares.getPersonaFamiliar() != null){
//					familiares.getPersonaFamiliar().setTelefonos(telefono);
//					familiares.getPersonaFamiliar().setEMail(email);
//					familiares.getPersonaFamiliar().setSexo(idSexo);
//					familiares.getPersonaFamiliar().setEstadoCivil(idEstadoCivil);
//					familiares.getPersonaFamiliar().setFechaNacimiento(fechaNacimiento);
//					familiares.getPersonaFamiliar().setUsuMod(usuarioLogueado.getCodigoUsuario());
//					familiares.getPersonaFamiliar().setFechaMod(new Date());
//					familiares.setPersonaFamiliar(em.merge(familiares.getPersonaFamiliar()));
//				}else{
					if(nombres != null && !nombres.trim().isEmpty())
						familiares.setNombresFamiliar(nombres);
					if(apellidos != null && !apellidos.trim().isEmpty())
						familiares.setApellidosFamiliar(apellidos);
					if(idSexo != null)
						familiares.setSexo(idSexo);
					if(idEstadoCivil != null)
						familiares.setEstadoCivil(idEstadoCivil);
					if(fechaNacimiento != null)
						familiares.setFechaNacimiento(fechaNacimiento);
					if(idPais != null)
						familiares.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, idPais));
					if (observaciones != null && !observaciones.trim().isEmpty())
						familiares.setObservaciones(observaciones);
					if (email != null && !email.trim().isEmpty())
						familiares.setEMail(email);;
					if (telefono != null && !telefono.trim().isEmpty())
						familiares.setTelefonos(telefono);
					
//				}
				if (idPersonasACargo != null)
					familiares.setPersonasACargo(idPersonasACargo.longValue());
				familiares.setFuncionarioPublico(idFuncionarioPublicoActivo);
				if (idObservacion != null)
					familiares.setObservacion(idObservacion.longValue());
				if (idOee != null) {
					familiares.setConfiguracionUoCab(new ConfiguracionUoCab());
					familiares.getConfiguracionUoCab().setIdConfiguracionUo(idOee);
				}
				familiares.setFechaMod(new Date());
				familiares.setUsuMod(usuarioLogueado.getCodigoUsuario());
				idElemUpdate = null;

				if (texto != null && texto.equalsIgnoreCase("P")) {
					auditoria(familiares, "UPDATE");
				}
				em.merge(familiares);
				em.flush();
				if(doc != null){
					doc.setIdTabla(familiares.getIdFamiliar());
					em.merge(doc);
					em.flush();
				}
				limpiarDatosPersona();
				cargarGrilla1();
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return null;

			}
		}
		return null;
	}

	public String eliminar(int index) {
		try {
			if (lGrilla1.get(index).getIdFamiliar() != null) {
				Familiares dp = lGrilla1.get(index);
				dp.setFechaMod(new Date());
				dp.setUsuMod(usuarioLogueado.getCodigoUsuario());
				dp.setActivo(false);
				dp = em.merge(dp);
				em.flush();
			}
			lGrilla1.remove(index);
			limpiar();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			e.printStackTrace();
			return null;
		}
	}

	public void limpiar() {
		nombres = null;
		apellidos = null;
		telefono = null;
		email = null;
		nroDoc = null;
		fileName = null;
		email = null;
		telefono = null;
		idSexo = null;
		idEstadoCivil = null;
		idFuncionarioPublicoActivo = null;
		idObservacion = null;
		idOee = null;
		idGradoParentezco = null;
		idPais = null;
		idPersonasACargo = null;
		fechaNacimiento = null;
		idElemUpdate = null;
		idPersonaFamiliar = null;
		esOtro = false;
		descTipoDoc = "";
		
	}

	private void caragrObsUoSicca() {
		Query q =
			em.createQuery("select AuditLegajoObs from AuditLegajoObs AuditLegajoObs "
				+ " where AuditLegajoObs.ficha = 2 and AuditLegajoObs.auditLegajo.persona.idPersona = :idPersona");
		q.setParameter("idPersona", idPersona);
		List<AuditLegajoObs> lista = q.getResultList();
		if (lista.size() > 0) {
			obsSiccaUo = lista.get(0).getObservacion();
		} else {
			obsSiccaUo = "-";
		}
	}

	public Long getIdGradoParentezco() {
		return idGradoParentezco;
	}

	public void setIdGradoParentezco(Long idGradoParentezco) {
		this.idGradoParentezco = idGradoParentezco;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getNroDoc() {
		return nroDoc;
	}

	public void setNroDoc(String nroDoc) {
		this.nroDoc = nroDoc;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(String idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public void setIdSexo(String idSexo) {
		this.idSexo = idSexo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getExistePersonasAcargo() {
		return existePersonasAcargo;
	}

	public void setExistePersonasAcargo(String existePersonasAcargo) {
		this.existePersonasAcargo = existePersonasAcargo;
	}

	public Long getIdOee() {
		return idOee;
	}

	public void setIdOee(Long idOee) {
		this.idOee = idOee;
	}

	public Boolean getHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(Boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public List<Familiares> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<Familiares> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Integer getIdElemUpdate() {
		return idElemUpdate;
	}

	public void setIdElemUpdate(Integer idElemUpdate) {
		this.idElemUpdate = idElemUpdate;
	}

	public String getObsSiccaUo() {
		return obsSiccaUo;
	}

	public void setObsSiccaUo(String obsSiccaUo) {
		this.obsSiccaUo = obsSiccaUo;
	}

	public Integer getIdPersonasACargo() {
		return idPersonasACargo;
	}

	public void setIdPersonasACargo(Integer idPersonasACargo) {
		this.idPersonasACargo = idPersonasACargo;
	}

	public Integer getIdObservacion() {
		return idObservacion;
	}

	public void setIdObservacion(Integer idObservacion) {
		this.idObservacion = idObservacion;
	}

	public Integer getIdFuncionarioPublicoActivo() {
		return idFuncionarioPublicoActivo;
	}

	public void setIdFuncionarioPublicoActivo(Integer idFuncionarioPublicoActivo) {
		this.idFuncionarioPublicoActivo = idFuncionarioPublicoActivo;
	}

	public String getIdSexo() {
		return idSexo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
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

	public Boolean getTieneCedula() {
		return tieneCedula;
	}

	public void setTieneCedula(Boolean tieneCedula) {
		this.tieneCedula = tieneCedula;
	}
	
	public Boolean getShow644() {
		return show644;
	}

	public void setShow644(Boolean show644) {
		this.show644 = show644;
	}
	
	public Long getIdPersonaFamiliar() {
		return idPersonaFamiliar;
	}

	public void setIdPersonaFamiliar(Long idPersonaFamiliar) {
		this.idPersonaFamiliar = idPersonaFamiliar;
	}


	public Familiares getFamiliares() {
		return familiares;
	}


	public void setFamiliares(Familiares familiares) {
		this.familiares = familiares;
	}


	public Long getIdFamiliar() {
		return idFamiliar;
	}


	public void setIdFamiliar(Long idFamiliar) {
		this.idFamiliar = idFamiliar;
	}


	public List<Documentos> getFamiliaresDocList() {
		return familiaresDocList;
	}


	public void setFamiliaresDocList(List<Documentos> familiaresDocList) {
		this.familiaresDocList = familiaresDocList;
	}


	public Integer getTamArchivo() {
		return tamArchivo;
	}


	public void setTamArchivo(Integer tamArchivo) {
		this.tamArchivo = tamArchivo;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}
	
	public Boolean getPersonaACargo(){
		return this.personaACargo;
	}
	
	public void setPersonaACargo(Boolean personaACargo){
		this.personaACargo = personaACargo;
	}

	public Integer totalCargo(){
		Query q =
				em.createQuery("Select Familiares from Familiares Familiares where Familiares.persona.idPersona = :idPersona and Familiares.activo is true"+
				" and personaACargo is true");
			q.setParameter("idPersona", idPersona);
		return q.getResultList().size();
	}
	
	public Boolean getEsOtro(){
		return esOtro;
	}
	
	public void setEsOtro(Boolean esOtro){
		this.esOtro = esOtro;
	}
	
	public String getDescTipoDoc() {
		return descTipoDoc;
	}

	public void setDescTipoDoc(String descTipoDoc) {
		this.descTipoDoc = descTipoDoc;
	}
	
	public void esOtroTipo() {
		if(idTipoDoc != null){
			DatosEspecificos datos= em.find(DatosEspecificos.class, idTipoDoc);
			if(datos.getDescripcion().equalsIgnoreCase("OTROS")){
				esOtro=true;
			} else {
				esOtro=false;
			}
		}
	}
	
	public String nombreDoc(Documentos docu){
		if(docu.getDatosEspecificos().getDescripcion().equalsIgnoreCase("OTROS"))
			return docu.getDescripcion();
		else
			return docu.getDatosEspecificos().getDescripcion();
	}
	
	@SuppressWarnings("unchecked")
	public Boolean isOriginal(Long idDocumento){
		String sql = "select * from legajo.familiares where activo = true and id_documento =  " + idDocumento;
		List <Documentos> lista = em.createNativeQuery(sql, Documentos.class).getResultList();
		if(lista != null && lista.size() > 0)
			return true;
		else
			return false;
	}
}
