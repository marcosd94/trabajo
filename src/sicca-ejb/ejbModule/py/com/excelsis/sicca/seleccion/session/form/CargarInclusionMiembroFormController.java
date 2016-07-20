package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import enums.TitularSuplente;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.Referencias;

import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ComisionSeleccionCabHome;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cargarInclusionMiembroFormController")
public class CargarInclusionMiembroFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;

	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private ComisionSeleccionCab comisionSeleccionCab = new ComisionSeleccionCab();
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;
	private String docIdentidad;
	private Persona persona = new Persona();
	private String puesto;
	private TitularSuplente titularSuplente;
	private List<ComisionSeleccionDet> comisionSeleccionDetList;
	private Long idRol = null;
	private Long idTipoDoc;
	private boolean habPersona;


	private Long idConcurso;
	private Long idComisionSelCab;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidads = new SinEntidad();
	private Integer ordenUnidOrg;
	private ConfiguracionUoCab configuracionUoCabs = new ConfiguracionUoCab();
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Entidad entidad = new Entidad();
	private Long idConfigCab;
	private boolean eqTec;
	private Excepcion excepcion = new Excepcion();
	private boolean esEdit;
	private Integer indexUpdate;

	private String direccionFisica;
	private String entity;
	private String nombrePantalla;
	private boolean fromAnexar = false;
	private Integer anioActual;
	private Long idPaisExp= null;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;
	private Long idPersona;
	private boolean primeraEntrada=true;

	public void init() throws Exception {
		cargarAnhoActual();
		if (idPersona != null) {
			if (idPaisExp == null
					|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
			persona = em.find(Persona.class, idPersona);
			completarDatosPersona(persona);
			habBtnAddPersons=false;
		}
		if (idSinNivelEntidad != null)
			nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);

		if (idSinEntidad != null) {
			sinEntidads = em.find(SinEntidad.class, idSinEntidad);

		}
		if (idConfigCab != null) {
			configuracionUoCabs = em.find(ConfiguracionUoCab.class, idConfigCab);
			ordenUnidOrg = configuracionUoCabs.getOrden();
		}

		
		if(primeraEntrada){
			primeraEntrada=false;
			idPersona=General.getIdParaguay();
			comisionSeleccionDetList = new ArrayList<ComisionSeleccionDet>();
			titularSuplente = TitularSuplente.TITULAR;
			findEntidades();// Trae las entidades segun el grupo que se envio
			esEdit = false;
			setearDatosAdd();
		}

	}
	private void completarDatosPersona(Persona persona) {
			
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
			
		}
		
	
	public void buscarPersona() throws Exception {
			
			/* Validaciones */
			if (idPaisExp == null
				|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			if (docIdentidad == null || docIdentidad.trim().equals("")
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
			} else if (personaDTO.getHabilitarBtn()) {
				habBtnAddPersons = true;
				limpiarDatosPersona2();
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			} else {
				habBtnAddPersons = false;
				persona = personaDTO.getPersona();
				
			}
		}

	public void limpiarDatosPersona2() {
		persona = new Persona();
	}
	public void limpiarDatosPersona() {
		persona= new Persona();
		idPersona=null;
		docIdentidad = null;
	}
	@SuppressWarnings("unchecked")
	public void editar(int index) {
		ComisionSeleccionDet det = comisionSeleccionDetList.get(index);
		docIdentidad = det.getPersona().getDocumentoIdentidad();
		persona = det.getPersona();
		puesto = det.getPuesto();
		idRol = det.getRol().getIdRol();
		titularSuplente = TitularSuplente.getTitularSuplentePorValor(det.getTitularSuplente());
		eqTec = det.getEquipoTecnico();
		configuracionUoCabs = det.getConfiguracionUo();

		if (configuracionUoCabs != null && configuracionUoCabs.getEntidad() != null) {
			ordenUnidOrg = configuracionUoCabs.getOrden();
			idConfigCab = configuracionUoCabs.getIdConfiguracionUo();
			sinEntidads =
				em.find(SinEntidad.class, configuracionUoCabs.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			idSinNivelEntidad = sinEntidads.getIdSinEntidad();
			if (!sin.isEmpty()) {
				nivelEntidad = sin.get(0);
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			}
		}
		esEdit = true;
		indexUpdate = index;
	}

	public void eliminar(int index) {

		comisionSeleccionDetList.remove(comisionSeleccionDetList.get(index));

	}

	public void addItems() {
		if (persona.getIdPersona() == null || docIdentidad == null || idComisionSelCab == null
			|| idRol == null || titularSuplente.getValor() == null) {
			statusMessages.add(Severity.ERROR, "Debe ingresar los Datos obligatorios");
			return;

		}

		if (checkDuplicate(persona.getIdPersona(), idComisionSelCab)) {
			statusMessages.add(Severity.ERROR, "No se puede agregar la misma persona  en la misma comisión de selección, verifique");
			return;
		}

		ComisionSeleccionDet det = new ComisionSeleccionDet();
		det.setRol(em.find(Rol.class, idRol));
		det.setTitularSuplente(titularSuplente.getValor());
		det.setEquipoTecnico(eqTec);
		det.setPuesto(puesto);
		det.setPersona(persona);
		det.setComisionSeleccionCab(em.find(ComisionSeleccionCab.class, idComisionSelCab));
		if (idConfigCab != null)
			det.setConfiguracionUo(em.find(ConfiguracionUoCab.class, idConfigCab));
		det.setTipo("EXC. INCLUSION");

		comisionSeleccionDetList.add(det);
		setearDatosAdd();

	}
	private void setearDatosAdd(){
		puesto = null;

		idRol = null;
		docIdentidad = null;
		persona = new Persona();

		idSinEntidad = null;
		idConfigCab = null;
		idSinNivelEntidad = null;
		sinEntidads = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		configuracionUoCabs = new ConfiguracionUoCab();
		ordenUnidOrg = null;
		idTipoDoc = null;
		indexUpdate = null;
		esEdit = false;
		eqTec = false;
		idPersona = null;
		titularSuplente = TitularSuplente.TITULAR;
		idPaisExp=General.getIdParaguay();
		habBtnAddPersons=false;
		personaDTO= new PersonaDTO();
	}

	public String save() {
		try {
			if (idConcurso == null ) {
					statusMessages.add(Severity.ERROR,"Debe seleccionar el Concurso");
					return null;

				}
			if (idConcurso == null || idComisionSelCab == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccinar una Comisión Selección");
				return null;

			}
			if (comisionSeleccionDetList.isEmpty()) {
				statusMessages.add(Severity.ERROR,"Debe ingresar almenos un detalle");
				return null;
			}
			if (idConcurso == null || idComisionSelCab == null
				|| excepcion.getObservacion() == null
				|| excepcion.getObservacion().trim().equals("")) {
				statusMessages.add(Severity.ERROR,"Debe ingresar los Datos obligatorios");
				return null;

			}
			if (excepcion.getObservacion().length() > 1000) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
					+ ": Observación (1000)");
				return null;
			}

			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_APROBAR_SFP);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EXCEPCION);
			jbpmUtilFormController.setTransition("start_TO_aprSfp");
			roles = jbpmUtilFormController.asignarRolesTarea();
			Long idProcess = jbpmUtilFormController.initProcess("excepciones");

			excepcion.setTipo("INCLUSION MIEMBRO CS");
			excepcion.setConcurso(concurso);
			excepcion.setEstado("PENDIENTE APROBACION");
			excepcion.setActivo(true);
			excepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			excepcion.setFechaAlta(new Date());
			excepcion.setUsuSolicitud(usuarioLogueado.getUsuAlta());
			excepcion.setFechaSolicitud(new Date());
			excepcion.setComisionSeleccionCab(em.find(ComisionSeleccionCab.class, idComisionSelCab));
			excepcion.setIdProcessInstance(idProcess);
			em.persist(excepcion);
			em.flush();

			for (int i = 0; i < comisionSeleccionDetList.size(); i++) {
				ComisionSeleccionDet det = new ComisionSeleccionDet();
				det = comisionSeleccionDetList.get(i);
				det.setExcepcion(excepcion);
				det.setFechaAlta(new Date());
				det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				det.setActivo(true);
				det.setComisionSeleccionCab(em.find(ComisionSeleccionCab.class, idComisionSelCab));
				em.persist(det);
				em.flush();
			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			statusMessages.add(SeamResourceBundle.getBundle().getString("CU304_msg_inicio"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}

	}

	public String update() {
		try {
			if (persona.getIdPersona() == null || docIdentidad == null || idRol == null
				|| titularSuplente.getValor() == null) {
				statusMessages.add("Debe ingresar los Datos obligatorios");
				return null;

			}

			if (checkDuplicateUpdate(persona.getIdPersona(), idComisionSelCab, indexUpdate)) {
				statusMessages.add(Severity.ERROR, "No se puede agregar la misma persona  en la misma comisión de selección, verifique");
				return null;
			}
			ComisionSeleccionDet det = new ComisionSeleccionDet();
			ComisionSeleccionDet eliminar = comisionSeleccionDetList.get(indexUpdate);
			det = comisionSeleccionDetList.get(indexUpdate);
			det.setRol(em.find(Rol.class, idRol));
			det.setEquipoTecnico(false);
			det.setTitularSuplente(titularSuplente.getValor());
			det.setEquipoTecnico(eqTec);
			det.setPuesto(puesto);
			det.setPersona(persona);
			det.setConfiguracionUo(configuracionUoCabs);
			det.setTipo("EXC. INCLUSION");
			det.setComisionSeleccionCab(comisionSeleccionCab);

			comisionSeleccionDetList.remove(eliminar);
			comisionSeleccionDetList.add(det);
			setearDatosAdd();
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}

	}

	public void cancelar() {
		puesto = null;
		idRol = null;
		docIdentidad = null;
		persona = new Persona();
		idSinEntidad = null;
		idConfigCab = null;
		idSinNivelEntidad = null;
		sinEntidads = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		configuracionUoCabs = new ConfiguracionUoCab();
		ordenUnidOrg = null;
		idTipoDoc = null;
		indexUpdate = null;
		esEdit = false;
		eqTec = false;

	}

	public String toFindPersona() {
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/excepcion/inclusionMiembroCom/CargarInclusionMiembro";
	}

	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?personaFrom=/seleccion/excepcion/inclusionMiembroCom/CargarInclusionMiembro";
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				nivelEntidad = new SinNivelEntidad();
				return;
			}
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	public String getUrlToPageNivel() {
		sinNivelEntidadList.limpiar();
		return "/planificacion/sinNivelEntidad/SinNivelEntidadList.xhtml?from=/seleccion/excepcion/inclusionMiembroCom/CargarInclusionMiembro";
	}

	/**
	 * Método que busca la entidad correspondiente al codigo ingresado y el nivel
	 */
	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null && sinEntidads.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidads.getEntCodigo());
			sinEntidadList.getSinEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			sinEntidads = sinEntidadList.entidadMaxAnho();

			if (sinEntidads != null && sinEntidads.getIdSinEntidad() != null) {
				idSinEntidad = sinEntidads.getIdSinEntidad();
				entidadList.getSinEntidad().setIdSinEntidad(sinEntidads.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
			} else {
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				sinEntidads = new SinEntidad();
				return;
			}

		}
	}

	public String getUrlToPageEntidad() {
		if (idSinNivelEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=/seleccion/excepcion/inclusionMiembroCom/CargarInclusionMiembro&codigoNivel="
			+ nivelEntidad.getNenCodigo();
	}

	public void findUnidadOrganizativa() {
		if (ordenUnidOrg != null) {
			configuracionUoCabList.getConfiguracionUoCab().setOrden(ordenUnidOrg);
			if (entidad != null) {
				Long id = entidad.getIdEntidad();
				configuracionUoCabList.setIdEntidad(id);
			}

			configuracionUoCabs = configuracionUoCabList.searchUnidad();
			if (configuracionUoCabs != null)
				idConfigCab = configuracionUoCabs.getIdConfiguracionUo();
			else {
				configuracionUoCabs = new ConfiguracionUoCab();
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else
			configuracionUoCabs = new ConfiguracionUoCab();
	}

	public String getUrlToPageOee() {
		if (idSinNivelEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}

		sinEntidads = em.find(SinEntidad.class, idSinEntidad);

		String retorno =
			"/planificacion/searchForms/FindDependencias.xhtml?from=/seleccion/excepcion/inclusionMiembroCom/CargarInclusionMiembro&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad=" + sinEntidads.getIdSinEntidad();
		retorno += "&anho=" + anioActual;
		return retorno;
	}

	private void cargarAnhoActual() {
		Calendar cal = Calendar.getInstance();
		anioActual = cal.get(Calendar.YEAR);
	}

	public List<SelectItem> getConcursoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Concurso o : getConcursos())
			si.add(new SelectItem(o.getIdConcurso(), "" + o.getNombre()));
		return si;
	}

	public List<SelectItem> getComisionSelecCabSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ComisionSeleccionCab o : getComisionSelecciones())
			si.add(new SelectItem(o.getIdComisionSel(), "" + o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<ComisionSeleccionCab> getComisionSelecciones() {
		try {
			if (idConcurso != null) {
				concurso = em.find(Concurso.class, idConcurso);

			} else
				concurso = new Concurso();

			return em.createQuery(" select o from " + ComisionSeleccionCab.class.getName() + " o "
				+ " where o.concurso.idConcurso=" + idConcurso).getResultList();
		} catch (Exception ex) {
			return new Vector<ComisionSeleccionCab>();
		}
	}

	public String anexar() {
		try {
			if (concurso == null || concurso.getIdConcurso() == null || idComisionSelCab == null) {
				statusMessages.add(" Debe seleccionar un concurso  y Debe especificar la Comisión de Selección antes de presionar este botón, verifique");
				return null;
			}

			fromAnexar = true;
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());
			direccionFisica =
				"//SICCA//" + anio + "//OEE//" + configuracionUoCab.getIdConfiguracionUo() + "//"
					+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
					+ idConcurso + "//CS";
			nombrePantalla = "cargarInclusionMiembro_edit";
			entity = "ComisionSeleccionCab";
			return "anexar";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * METODOS PRIVADOS
	 **/
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(Long idPersona, Long idCab) {

		for (int i = 0; i < comisionSeleccionDetList.size(); i++) {
			if (comisionSeleccionDetList.get(i).getPersona().getIdPersona().intValue() == idPersona
				&& comisionSeleccionDetList.get(i).getComisionSeleccionCab().getIdComisionSel().intValue() == idCab)
				return true;
		}
		List<ComisionSeleccionDet> dets =
			em.createQuery("Select c from ComisionSeleccionDet c "
				+ " where c.comisionSeleccionCab.idComisionSel=" + idCab
				+ " and c.persona.idPersona=" + idPersona + " and c.activo=true").getResultList();
		if (!dets.isEmpty())
			return true;

		return false;

	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicateUpdate(Long idPersona, Long idCab, int index) {

		for (int i = 0; i < comisionSeleccionDetList.size(); i++) {
			if (comisionSeleccionDetList.get(i).getPersona().getIdPersona().intValue() == idPersona
				&& comisionSeleccionDetList.get(i).getComisionSeleccionCab().getIdComisionSel().intValue() == idCab
				&& i != index)
				return true;
		}
		List<ComisionSeleccionDet> dets =
			em.createQuery("Select c from ComisionSeleccionDet c "
				+ " where c.comisionSeleccionCab.idComisionSel=" + idCab
				+ " and c.persona.idPersona=" + idPersona + " and c.activo=true").getResultList();
		if (!dets.isEmpty())
			return true;

		return false;

	}

	@SuppressWarnings("unchecked")
	private void findEntidades() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {
			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			if (configuracionUoCab.getEntidad() != null) {
				sinEntidad =
					em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
				List<SinNivelEntidad> sin =
					em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
						+ sinEntidad.getAniAniopre() + " and n.nenCodigo="
						+ sinEntidad.getNenCodigo()).getResultList();
				if (!sin.isEmpty())
					sinNivelEntidad = sin.get(0);

			}
		}

	}

	@SuppressWarnings("unchecked")
	private Integer estadoFinalizado() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'FINALIZADO'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	/**
	 * LLENA EL CONCURSO RELACIONADO CON EL OEE
	 **/

	@SuppressWarnings("unchecked")
	private List<Concurso> getConcursos() {
		try {
			return em.createQuery(" select o from " + Concurso.class.getName() + " o "
				+ " where o.configuracionUoCab.idConfiguracionUo="
				+ configuracionUoCab.getIdConfiguracionUo() + "" + " and o.estado != "
				+ estadoFinalizado()).getResultList();
		} catch (Exception ex) {
			return new Vector<Concurso>();
		}
	}

	// GETTERS Y SETTERS

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}

	public void setComisionSeleccionCab(ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public TitularSuplente getTitularSuplente() {
		return titularSuplente;
	}

	public void setTitularSuplente(TitularSuplente titularSuplente) {
		this.titularSuplente = titularSuplente;
	}

	public List<ComisionSeleccionDet> getComisionSeleccionDetList() {
		return comisionSeleccionDetList;
	}

	public void setComisionSeleccionDetList(List<ComisionSeleccionDet> comisionSeleccionDetList) {
		this.comisionSeleccionDetList = comisionSeleccionDetList;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public boolean isHabPersona() {
		return habPersona;
	}

	public void setHabPersona(boolean habPersona) {
		this.habPersona = habPersona;
	}


	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidads() {
		return sinEntidads;
	}

	public void setSinEntidads(SinEntidad sinEntidads) {
		this.sinEntidads = sinEntidads;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public ConfiguracionUoCab getConfiguracionUoCabs() {
		return configuracionUoCabs;
	}

	public void setConfiguracionUoCabs(ConfiguracionUoCab configuracionUoCabs) {
		this.configuracionUoCabs = configuracionUoCabs;
	}

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Long getIdComisionSelCab() {
		return idComisionSelCab;
	}

	public void setIdComisionSelCab(Long idComisionSelCab) {
		this.idComisionSelCab = idComisionSelCab;
	}

	public boolean isEqTec() {
		return eqTec;
	}

	public void setEqTec(boolean eqTec) {
		this.eqTec = eqTec;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public Integer getIndexUpdate() {
		return indexUpdate;
	}

	public void setIndexUpdate(Integer indexUpdate) {
		this.indexUpdate = indexUpdate;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public boolean isFromAnexar() {
		return fromAnexar;
	}

	public void setFromAnexar(boolean fromAnexar) {
		this.fromAnexar = fromAnexar;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
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

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}
	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}
	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	
	

}
