package py.com.excelsis.sicca.session.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import dw.IGPWS;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("seguridadUtilFormController")
public class SeguridadUtilFormController implements Serializable {

	private static final long serialVersionUID = -4991047933512556323L;
	@In(required = false)
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	IGPWS igpws;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private String evaluacionNombre;
	private Boolean esAdministradorSFP = false;
	
	public void init() {
		cargarAdministradorSFP();
		
	}

	public void validarParticipantes(Integer valor) {

		if (valor == 0) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("CU490_novalida"));
		}

	}

	/**
	 * Verifica si una lista de parametros esta limpia para evitar sql Injection
	 * 
	 * @param diccDatosdiccionario
	 *            <Tipo de dato, List<entradas a evaluar>>
	 * @return
	 * @throws Exception
	 */
	public Boolean isParametrosLimpios(Map<String, List<String>> diccDatos)
			throws Exception {
		if (diccDatos.size() == 0)
			return true;
		for (String o : diccDatos.keySet()) {
			for (String m : diccDatos.get(o)) {
				if (!validarInput(m, o, null)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Validador anti sql injection. Enfoque de lista blanca
	 * 
	 * @param tipo
	 *            tipo de dato del parametro input
	 * @param input
	 *            cadena que se quiere validar
	 * @return
	 * @throws Exception
	 */
	public Boolean validarInput(String input, String tipo, Integer longitud)
			throws Exception {
		statusMessages.clear();
		// Enfoque de lista blanca
		if (tipo.equalsIgnoreCase(TiposDatos.STRING.getValor())) {
			if (!input.toLowerCase().matches("[/'a-záéíóúñ\\s0-9]+")) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
			if (longitud != null && input.length() > longitud) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else if (tipo.equalsIgnoreCase(TiposDatos.INTEGER.getValor())) {
			try {
				Integer.parseInt(input);
			} catch (Exception e) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else if (tipo.equalsIgnoreCase(TiposDatos.LONG.getValor())) {
			try {
				Long.parseLong(input);
			} catch (Exception e) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else if (tipo.equalsIgnoreCase(TiposDatos.DOUBLE.getValor())) {
			try {
				Double.parseDouble(input);
			} catch (Exception e) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else if (tipo.equalsIgnoreCase(TiposDatos.FLOAT.getValor())) {
			try {
				Float.parseFloat(input);
			} catch (Exception e) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else if (tipo.equalsIgnoreCase(TiposDatos.BOOLEAN.getValor())) {
			try {
				Boolean.parseBoolean(input);
			} catch (Exception e) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else if (tipo.equalsIgnoreCase(TiposDatos.BIGDECIMAL.getValor())) {
			try {
				new BigDecimal(input);
			} catch (Exception e) {

				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				return false;
			}
		} else {
			throw new Exception("No existe el tipo de dato.");
		}
		return true;
	}

	public int estadoActividades(String dominio, String descAbrev) {
		List<Referencias> referencias = em.createQuery(
				" Select r from Referencias r " + " where r.dominio='"
						+ dominio + "' and r.descAbrev= '" + descAbrev
						+ "' and r.activo=true").getResultList();
		if (referencias.isEmpty())
			return 0;
		return referencias.get(0).getValorNum();
	}

	/**
	 * Permite verificar si el recurso que se quiere alcanzar es de la oee del
	 * usuario que lo pide
	 * 
	 * @param Usuario
	 *            : id del usuario que pide el recurso.
	 * @param idOeeRecurso
	 *            : id del oee del recurso
	 * @return True, si lo es, false en cualquier otro caso
	 */
	public void verificarPerteneceOee(Long idOeeRecurso, Long idGrupo,
			String estado, String actividad) {
		cargarAdministradorSFP();
		if (esAdministradorSFP)
			return;
		validarSeleccion(idGrupo, estado, actividad);

	}

	public void verificarPerteneceOeeCIO(Long idOeeRecurso, Long idGrupo,
			String estado, String actividad) {
		cargarAdministradorSFP();
		if (esAdministradorSFP)
			return;
		validarSeleccionCIO(idGrupo, estado, actividad);

	}

	public void verificarPerteneceOeeCSI(Long idOeeRecurso, Long idGrupo,
			String estado, String actividad) {
		cargarAdministradorSFP();
		
		validarSeleccionSI(idGrupo, estado, actividad);

	}

	public boolean verificarPerteneceOee(Long idOeeRecurso) {
		cargarAdministradorSFP();
		if (esAdministradorSFP)
			return true;
		if (usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo()
				.toString().equalsIgnoreCase(idOeeRecurso.toString())) {
			return true;
		}
		return false;

	}

	/**
	 * Permite verificar si el recurso que se quiere alcanzar es de la oee del
	 * usuario que lo pide
	 * 
	 * @param Usuario
	 *            : id del usuario que pide el recurso. idOeeRecurso: id del
	 *            UODet del recurso(de la tabla ConfiguracionUoDet)
	 * @return True, si lo es, false en cualquier otro caso
	 */
	public boolean verificarPerteneceUoDet(Long idUoDetRecurso) {

		if (usuarioLogueado.getConfiguracionUoDet() != null) {
			if (usuarioLogueado.getConfiguracionUoDet()
					.getIdConfiguracionUoDet().toString()
					.equalsIgnoreCase(idUoDetRecurso.toString())) {
				return true;
			}

		} else
			return true;

		return false;
	}

	/**
	 * Permite verificar si el recurso que se quiere alcanzar es de la
	 * capacitacion del usuario que lo pide
	 * 
	 * @param idCapacitacion
	 *            : id de la Capacitacion. actividad : la actividad en la que se
	 *            encuentra
	 * @return True, si lo es, false en cualquier otro caso
	 */
	@SuppressWarnings("unchecked")
	public boolean verificarPerteneceCapacitacionActor(Long idCapacitacion,
			String actividad) {
		List<BandejaCapacitacion> bandejaCapacitacions = em.createQuery(
				"select b from  BandejaCapacitacion b "
						+ " where b.idCapacitacion=" + idCapacitacion
						+ " and b.actividad.codActividad = '" + actividad
						+ "' " + " and b.usuario = '"
						+ usuarioLogueado.getCodigoUsuario() + "'")
				.getResultList();
		if (!bandejaCapacitacions.isEmpty())
			return true;
		return false;
	}

	/**
	 * Verifica si el recurso que se quiere alcanzar es del grupo del usuario
	 * que lo pide
	 * 
	 * @param idGrupo
	 *            : id de la Gruoi.
	 * @param actividad
	 *            : la actividad en la que se encuentra
	 * @return True, si lo es, false en cualquier otro caso
	 */
	@SuppressWarnings("unchecked")
	public boolean verificarPerteneceGrupoActor(Long idGrupo, String actividad) {
		List<BandejaEntrada> bandeja = em.createQuery(
				"select b from  BandejaEntrada b "
						+ " where b.idConcursoPuestoAgr=" + idGrupo
						+ " and b.actividad.codActividad = '" + actividad
						+ "' " + " and b.usuario = '"
						+ usuarioLogueado.getCodigoUsuario() + "'")
				.getResultList();

		if (!bandeja.isEmpty())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean verificarPerteneceGrupoActorCIO(Long idGrupo,
			String actividad) {
		List<BandejaEntrada> bandeja = em.createQuery(
				"select b from  BandejaEntradaCio b "
						+ " where b.idConcursoPuestoAgr=" + idGrupo
						+ " and b.actividad.codActividad = '" + actividad
						+ "' " + " and b.usuario = '"
						+ usuarioLogueado.getCodigoUsuario() + "'")
				.getResultList();

		if (!bandeja.isEmpty())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean verificarPerteneceGrupoActorSI(Long idGrupo, String actividad) {
		List<BandejaEntrada> bandeja = em.createQuery(
				"select b from  BandejaEntradaCsi b "
						+ " where b.idConcursoPuestoAgr=" + idGrupo
						+ " and b.actividad.codActividad = '" + actividad
						+ "' " + " and b.usuario = '"
						+ usuarioLogueado.getCodigoUsuario() + "'")
				.getResultList();

		if (!bandeja.isEmpty())
			return true;
		return false;
	}

	/**
	 * Permite verificar si el recurso que se quiere alcanzar es de la
	 * capacitacion del usuario que lo pide
	 * 
	 * @param idCapacitacion
	 *            : id de la Capacitacion. estado : el estado en la que se
	 *            encuentra antes de pasar a siguiente tarea
	 * @return True, si lo es, false en cualquier otro caso
	 */
	public boolean verificarPerteneceCapacitacionEstado(Long idCapacitacion,
			Integer estado) {
		Capacitaciones capacitaciones = em.find(Capacitaciones.class,
				idCapacitacion);
		if (capacitaciones.getEstado().intValue() == estado.intValue())
			return true;
		return false;
	}

	public boolean verificarPerteneceSeleccionEstadoEXC(Long idGrupo,
			String estado) {
		ConcursoPuestoAgrExc concursoPuestoAgrExc = em.find(
				ConcursoPuestoAgrExc.class, idGrupo);
		if (concursoPuestoAgrExc.getEstado() != null
				&& concursoPuestoAgrExc.getEstado().equalsIgnoreCase(estado))
			return true;
		return false;
	}

	public boolean verificarPerteneceSeleccionEstado(Long idGrupo,
			Integer estado) {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idGrupo);
		if (concursoPuestoAgr.getEstado().intValue() == estado.intValue())
			return true;
		return false;
	}

	/**
	 * Verifica si el recurso que se quiere alcanzar esat en la actividad
	 * correcta
	 * 
	 * @param id
	 *            : del grupo, excepcion, capacitacion
	 * @param estado
	 *            : estado esperado
	 * @param tipo
	 *            GRUPO, CAPACITACION, EXCEPCION
	 * @return True si coinciden los estados, false en cualquier otro caso.
	 */
	public boolean verificarEstadoCorrecto(Long id, String estado, String tipo) {
		String estadoActual = null;
		if (tipo.equals("GRUPO")) {
			ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class, id);
			estadoActual = grupo.getEstado().toString();
		} else if (tipo.equals("CAPACITACION")) {
			Capacitaciones capa = em.find(Capacitaciones.class, id);
			estadoActual = capa.getEstado().toString();
		} else if (tipo.equals("EXCEPCION")) {
			Excepcion ex = em.find(Excepcion.class, id);
			estadoActual = ex.getEstado();

		}

		if (estadoActual.toString().equalsIgnoreCase(estado))
			return true;
		return false;
	}

	public void verificarActividadCorrecta(Long id, String estado, String tipo) {
		if (!verificarEstadoCorrecto(id, estado, tipo)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_ACTIVIDAD_NO_VALIDA"));
		}
	}

	public void validarCapacitacion(Long idCapacitacion, int estado,
			String actividad) {

		if (idCapacitacion == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		/*if (!verificarPerteneceCapacitacionActor(idCapacitacion, actividad)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}*/
		if (!verificarPerteneceCapacitacionEstado(idCapacitacion, estado)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}

	}

	/**
	 * VALIDA QUE PERTENESCA EL ESTADO DE LA EVAluaCION CON EL ESTADO QUE RECIBE
	 * 
	 * @param idEvaluacionDesempenho
	 * @param estado
	 */
	public void validarEvaluacionEstado(Long idEvaluacionDesempenho, int estado) {

		if (idEvaluacionDesempenho == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (!validarEstadoEvaluacion(idEvaluacionDesempenho, estado)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("seam_authorization_exception"));
		}

	}

	/**
	 * Permite verificar si el recurso que se quiere alcanzar es de la
	 * EVALUACION del usuario que lo pide
	 * 
	 * @param idEvaluacionDesempenho
	 *            : id de la idEvaluacionDesempenho. actividad : la actividad en
	 *            la que se encuentra
	 * @return True, si lo es, false en cualquier otro caso
	 */
	@SuppressWarnings("unchecked")
	public boolean verificarPerteneceEvaluacionActor(
			Long idEvaluacionDesempenho, String actividad) {
		List<BandejaCapacitacion> bandejaCapacitacions = em
				.createQuery(
						"select b from  BandejaEvaluacion b "
								+ " where b.idEvaluacionDesempeno=:idEval"
								+ " and b.actividadProceso.actividad.codActividad =:codActividad"
								+ " and b.usuario =:codUsuario ")
				.setParameter("idEval", idEvaluacionDesempenho)
				.setParameter("codActividad", actividad)
				.setParameter("codUsuario", usuarioLogueado.getCodigoUsuario())
				.getResultList();
		if (!bandejaCapacitacions.isEmpty())
			return true;
		return false;
	}

	public void validarEvaluacion(Long idEvaluacionDesempenho, int estado,
			String actividad) {

		if (idEvaluacionDesempenho == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		validarEvaluacionEstado(idEvaluacionDesempenho, estado);

		if (!verificarPerteneceEvaluacionActor(idEvaluacionDesempenho,
				actividad)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}

	}

	private boolean validarEstadoEvaluacion(Long idEvaluacionDesempenho,
			int estado) {
		EvaluacionDesempeno evaluacionDesempeno = em.find(
				EvaluacionDesempeno.class, idEvaluacionDesempenho);
		if (evaluacionDesempeno.getEstado().intValue() == estado)
			return true;

		return false;
	}

	public void validarCapacitacionPerteneceUO(Long idCapacitacion) {
		ConfiguracionUoDet uoUsuario = em.find(ConfiguracionUoDet.class,
				usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet());
		Capacitaciones cap = em.find(Capacitaciones.class, idCapacitacion);

		if (cap.getConfiguracionUoDet() == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (cap.getConfiguracionUoDet().getIdConfiguracionUoDet() != uoUsuario
				.getIdConfiguracionUoDet()) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}

	}

	public void validarSeleccion(Long idGrupo, String estado, String actividad)
			throws AuthorizationException {

		if (idGrupo == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (!verificarPerteneceGrupoActor(idGrupo, actividad)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}
		if (estado.contains("#")) {
			String compos[] = estado.split("#");
			Boolean cumpleReq = false;
			for (String o : compos) {
				if (verificarPerteneceSeleccionEstado(idGrupo,
						Integer.parseInt(o))) {
					cumpleReq = true;
					break;
				}
			}
			// Se inicializa al default.
			if (!cumpleReq)
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		} else {
			if (!verificarPerteneceSeleccionEstado(idGrupo,
					Integer.parseInt(estado))) {
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}

	}

	public void validarSeleccionCIO(Long idGrupo, String estado,
			String actividad) throws AuthorizationException {

		if (idGrupo == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (!verificarPerteneceGrupoActorCIO(idGrupo, actividad)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}
		if (estado.contains("#")) {
			String compos[] = estado.split("#");
			Boolean cumpleReq = false;
			for (String o : compos) {
				if (verificarPerteneceSeleccionEstado(idGrupo,
						Integer.parseInt(o))) {
					cumpleReq = true;
					break;
				}
			}
			// Se inicializa al default.
			if (!cumpleReq)
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		} else {
			if (!verificarPerteneceSeleccionEstado(idGrupo,
					Integer.parseInt(estado))) {
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}

	}

	public void validarSeleccionSI(Long idGrupo, String estado, String actividad)
			throws AuthorizationException {

		if (idGrupo == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (!verificarPerteneceGrupoActorSI(idGrupo, actividad)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}
		if (estado.contains("#")) {
			String compos[] = estado.split("#");
			Boolean cumpleReq = false;
			for (String o : compos) {
				if (verificarPerteneceSeleccionEstado(idGrupo,
						Integer.parseInt(o))) {
					cumpleReq = true;
					break;
				}
			}
			// Se inicializa al default.
			if (!cumpleReq)
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		} else {
			if (!verificarPerteneceSeleccionEstado(idGrupo,
					Integer.parseInt(estado))) {
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}

	}

	public void validarSeleccion2(Long idGrupo, String estado)
			throws AuthorizationException {

		if (estado.contains("#")) {
			String compos[] = estado.split("#");
			Boolean cumpleReq = false;
			for (String o : compos) {
				if (verificarPerteneceSeleccionEstadoEXC(idGrupo, o)) {
					cumpleReq = true;
					break;
				}
			}

			if (!cumpleReq)
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		} else {
			if (!verificarPerteneceSeleccionEstadoEXC(idGrupo, estado)) {
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Rol> obtenerRolesUsuario() {
		List<Rol> usuarioRoles = new ArrayList<Rol>();
		usuarioLogueado = em
				.find(Usuario.class, usuarioLogueado.getIdUsuario());
		List<UsuarioRol> lista = new ArrayList<UsuarioRol>(
				usuarioLogueado.getUsuarioRols());

		if (lista != null && lista.size() > 0) {
			for (UsuarioRol ur : lista) {
				usuarioRoles.add(ur.getRol());
			}
		}

		Collections.sort(usuarioRoles);
		return usuarioRoles;
	}

	public List<SelectItem> obtenerRolesUsuarioSelectItems() {
		List<SelectItem> listaItem = new LinkedList<SelectItem>();
		List<Rol> roles = obtenerRolesUsuario();
		listaItem.add(new SelectItem(null, "Seleccione.."));
		for (Rol r : roles) {
			listaItem.add(new SelectItem(r.getId(), r.getDescripcion()));
		}
		return listaItem;
	}

	public String getUsuarioLogueado() {
		if (usuarioLogueado != null)
			return usuarioLogueado.getCodigoUsuario();
		return null;
	}
	
	public String getU(){
		if (usuarioLogueado != null)
			return usuarioLogueado.getCodigoUsuario();
		return null;
	}
	
	public String getP(){
		if (usuarioLogueado != null)
			return usuarioLogueado.getContrasenha();
		return null;
	}
	
	public String getUrlJuridico(){
		String sql = "select * from seleccion.referencias where dominio = 'URL_MODULOS' and desc_abrev = 'URL_JURIDICO'";
		
		List<Referencias> lista = em.createNativeQuery(sql,Referencias.class).getResultList();
		Referencias ref = new Referencias();
		if (lista.size() > 0)
		 ref = lista.get(0);
		
		if(ref != null)
			return ref.getDescLarga();
		
		return "";
	}

	public String getNombreUsuarioLogueado() {
		if (usuarioLogueado != null) {
			if (usuarioLogueado.getPersona() != null) {
				Persona persona = em.find(Persona.class, usuarioLogueado
						.getPersona().getIdPersona());
				return persona.getNombreCompleto();
			}
			return usuarioLogueado.getCodigoUsuario();
		}
		return null;
	}

	public Boolean esUsuarioPortal() {
		if( usuarioLogueado != null )
			return usuarioLogueado.getUsuAlta().equalsIgnoreCase("PORTAL");
		else
			return true;
	}
	
	public String getHomeUrl() {
		if (esUsuarioPortal())
			return "/Portal.xhtml";
		else
			return "/home.xhtml";
	}
	
	public void setUsuarioLogueado(Usuario usuarioLogueado) {
		this.usuarioLogueado = usuarioLogueado;
	}

	/**
	 * Verifica si un usuario puede ver todas las OEE o no
	 * 
	 * @return
	 */
	private void cargarAdministradorSFP() {
		for (Rol r : obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador()) {
				esAdministradorSFP = true;
				return;
			}
		}

		esAdministradorSFP = false;
	}

	@SuppressWarnings("unchecked")
	public boolean esRolHomologar(Long idUsuario) {
		List<UsuarioRol> uRols = em.createQuery(
				"Select d FROM UsuarioRol d " + " WHERE d.usuario.idUsuario="
						+ idUsuario + " AND d.rol.homologador=TRUE ")
				.getResultList();
		if (uRols.isEmpty())
			return false;
		return true;
	}

	public boolean contieneCaracter(String palabra) {

		if (palabra.startsWith("'"))
			return true;
		if (palabra.startsWith("*"))
			return true;
		if (palabra.startsWith("/"))
			return true;
		return false;
	}

	public String caracterInvalido(String caracter) {
		if (caracter != null)
			return caracter.replace("'", "''");

		return caracter;
	}

	public void validarPerteneceOEE(Long idOee) {
		ConfiguracionUoCab oeeUsuario = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class, idOee);

		if (oee == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (oee.getIdConfiguracionUo() != oeeUsuario.getIdConfiguracionUo()) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}

	}

	/**
	 * CARGA EL NOBMRE DE LA EVALUACION NO EL NIVEL,ENTIDAD,OEE
	 * 
	 * @throws Exception
	 */
	public void cargarCabeceraEvaluacion(Long idEvaluacionDesempeno)
			throws Exception {
		if (!seguridadUtilFormController.validarInput(
				idEvaluacionDesempeno.toString(), TiposDatos.LONG.getValor(),
				null)) {
			return;
		}
		EvaluacionDesempeno e = em.find(EvaluacionDesempeno.class,
				idEvaluacionDesempeno);
		ConfiguracionUoCab uo = em.find(ConfiguracionUoCab.class, e.getConfiguracionUoCab().getIdConfiguracionUo());
		evaluacionNombre = e.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(uo
				.getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uo
//				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
	}

	public Boolean validaEstado(Long idGrupo, String estado) {
		String cad = "SELECT C.* "
				+ "FROM SELECCION.CONCURSO_PUESTO_AGR C, seleccion.referencias REF  "
				+ "WHERE C.id_concurso_puesto_agr = " + idGrupo
				+ " AND C.ESTADO = REF.VALOR_NUM "
				+ "AND REF.dominio = UPPER('ESTADOS_GRUPO') "
				+ " AND REF.DESC_ABREV = '"+estado+"'";
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		if (lista.isEmpty())
			return false;
		return true;
	}
	
	
		
//	public String ObtenerToken() {
//		
//				
//		if (usuarioLogueado != null && usuarioLogueado.getToken() == null){
//			if(tieneRolOperadorVerificador(usuarioLogueado)){
//				try {
//					igpws = new IGPWS(usuarioLogueado.getCodigoUsuario());
//					if(igpws.obtenerAccesoIGP()){
//						//URL para ir a IGP con el token
//						//"https://www.paraguayconcursa.gov.py/igp/pages/public/login.xhtml?token="+igpws.getToken();
//						usuarioLogueado.setToken(igpws.getToken());
//					}
//				} catch (Exception e) {
//					statusMessages.clear();
//					statusMessages.add(Severity.INFO,
//							"Error al Acceder al WebService de IGP : "+ e.getMessage());
//					usuarioLogueado.setToken(null);
//				}
//				
//				
//			}
//				
//		}
//		return usuarioLogueado.getToken();
//	}
	
	private Boolean tieneRolOperadorVerificador (Usuario usuario){
		String sql = "select * from seguridad.rol r"
				+ " join seguridad.usuario_rol ur on ur.id_rol = r.id_rol"
				+ " join seguridad.usuario u on u.id_usuario = ur.id_usuario "
				+ " where r.descripcion in ('IGP_OPERADOR', 'IGP_VERIFICADOR','IGP_ADMINISTRADOR')"
				+ " and u.codigo_usuario = '" +usuario.getCodigoUsuario()+"'";
		List<Rol> roles = em.createNativeQuery(sql,Rol.class).getResultList();
		if(roles.size() > 0)
			return true;
		else 
			return false;
	}
	
	

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public String getEvaluacionNombre() {
		return evaluacionNombre;
	}

	public void setEvaluacionNombre(String evaluacionNombre) {
		this.evaluacionNombre = evaluacionNombre;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}
	
}
