package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.Tab2FormacionAcademicaPorCarpetaFC;
import py.com.excelsis.sicca.seleccion.session.form.Tab3ExpLaboralPorCarpeta;
import py.com.excelsis.sicca.seleccion.session.form.Tab7VistaPrePostulacionActualFC;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.form.Tab3ExperienciaLaboralFormController;
import py.com.excelsis.sicca.session.util.UtilesFromController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("tab1DatosPersonalesPorCarpetaFC")
public class Tab1DatosPersonalesPorCarpetaFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	UtilesFromController utilesFromController;
	@In(create = true)
	Tab3ExpLaboralPorCarpeta tab3ExpLaboralPorCarpeta;
	@In(create = true)
	Tab2FormacionAcademicaPorCarpetaFC tab2FormacionAcademicaPorCarpetaFC;
	@In(create = true)
	Tab7VistaPrePostulacionActualFC tab7VistaPrePostulacionActualFC;

	Persona persona;
	General general;

	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;

	private List<SelectItem> departamentosDirSelecItem;
	private List<SelectItem> ciudadDirSelecItem;
	private List<SelectItem> localidadDirSelecItem;

	private Long idSexo;
	private Long idEstadoCivil;
	private Long idPaisNac;
	private Long idDptoNac;
	private Long idCiudadNac;
	private Long idPaisDir;
	private Long idDptoDir;
	private Long idCiudadDir;
	private Long idLocalidadDir;
	private Boolean cargadoTab1 = true;
	private Boolean guardado = false;
	private Boolean link = false;
	private Date fechaNac;
	private String callePrincipal;
	private String callePrimeraLateral;
	private String calleSegundaLateral;
	private String dptoNro;
	private String piso;
	private String direccionLaboral;
	private String otrasDirecciones;
	private String telefonos;
	private String telefonoPart;
	private String telefonoLab;
	private String msjCopiaRealizada;
	private Boolean boolCopiaRealizada = null;
	private Long idGrupo;
	private Long idPersona;
	private String from;
	private String codActividad;

	@RequestParameter
	private Boolean msjOk;
	@RequestParameter
	private Long personaIdPersona;
	@RequestParameter
	private Long concursoPuestoAgrIdConcursoPuestoAgr;

	/**
	 * Método que inicializa los datos
	 */
	private Boolean precondInit() {
		idGrupo = concursoPuestoAgrIdConcursoPuestoAgr;
		idPersona = personaIdPersona;
		if (idGrupo == null || idPersona == null) {

			return false;
		}
		// Seguridad
		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion "
				+ " where Postulacion.activo is true and Postulacion.tipo ='CARPETA' "
				+ " and Postulacion.persona.idPersona =:idPersona                    "
				+ " and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idPersona", idPersona);
		q.setParameter("idGrupo", idGrupo);
		List<Postulacion> lista = q.getResultList();
		if (lista.size() == 0) {
			statusMessages.add(Severity.ERROR, "No existe la Postualción");
			return false;
		}
		return true;
	}

	public void init() {
	 
		if (msjOk != null && msjOk) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			msjOk = false;
		}
		// Inicializacion de los combos

		updateDepartamentoDir();
		updateCiudadDir();
		updateBarrio();
		updateDepartamento();
		updateCiudad();
		if (!precondInit())
			return;
		verficarCopiaRealizada(idPersona, idGrupo);
		// Cargar Persona
		persona = em.find(Persona.class, idPersona);
		fechaNac = persona.getFechaNacimiento();
		callePrincipal = persona.getCallePrincipal();
		callePrimeraLateral = persona.getPrimeraLateral();
		calleSegundaLateral = persona.getSegundaLateral();
		dptoNro = persona.getDepartamentoNro();
		piso = persona.getPiso();
		direccionLaboral = persona.getDireccionLaboral();
		otrasDirecciones = persona.getOtrasDirecciones();
		telefonos = persona.getTelefonos();
		telefonoLab = persona.getTelefonoLab();
		telefonoPart = persona.getTelefonoPart();
		idSexo = searchSexo(persona.getSexo());
		idEstadoCivil = searchEstadoCivil(persona.getEstadoCivil());
		if (persona.getCiudadByIdCiudadNac() != null) {

			idPaisNac = persona.getCiudadByIdCiudadNac().getDepartamento().getPais().getIdPais();
			idDptoNac = persona.getCiudadByIdCiudadNac().getDepartamento().getIdDepartamento();
			updateDepartamento();
			updateCiudad();
			idCiudadNac = persona.getCiudadByIdCiudadNac().getIdCiudad();

		}
		if (persona.getCiudadByIdCiudadDirecc() != null) {
			idPaisDir = persona.getCiudadByIdCiudadDirecc().getDepartamento().getPais().getIdPais();
			idDptoDir = persona.getCiudadByIdCiudadDirecc().getDepartamento().getIdDepartamento();
			idCiudadDir = persona.getCiudadByIdCiudadDirecc().getIdCiudad();
			updateDepartamentoDir();
			updateCiudadDir();
			updateBarrio();
			if (persona.getBarrio() != null)
				idLocalidadDir = persona.getBarrio().getIdBarrio();
			else
				idLocalidadDir = null;
		}

	}

	private void limpiarIdCombos() {
		idDptoDir = null;
		idCiudadDir = null;
		idLocalidadDir = null;
		idDptoNac = null;
		idCiudadNac = null;
	}

	public void verficarCopiaRealizada2() {
		if (boolCopiaRealizada == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		if (boolCopiaRealizada) {
			msjCopiaRealizada = "¿Está seguro de realizar la acción?";
		} else {
			msjCopiaRealizada =
				"Se guardarán los Datos Personales para la Postulación actual, este paso se realiza una sola vez";
		}
	}

	public Postulacion verficarCopiaRealizada(Long idPersona, Long idGrupo) {

		Query q =
			em.createQuery("select Postulacion from Postulacion Postulacion "
				+ " where Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
				+ " and Postulacion.personaPostulante.persona.idPersona = :idPersona "
				+ " and Postulacion.usuCancelacion is null "
				+ " and Postulacion.fechaCancelacion is null "
				+ " and Postulacion.activo is true    ");
		q.setParameter("idPersona", idPersona);
		q.setParameter("idGrupo", idGrupo);
		List<Postulacion> lista = q.getResultList();
		if (lista.size() == 0) {
			boolCopiaRealizada = false;
		} else {
			boolCopiaRealizada = true;
			return lista.get(0);
		}
		return null;

	}

	public void limpiar() {
		msjCopiaRealizada = null;
		boolCopiaRealizada = false;;
	}

	public Usuario usuarioPostulante(Long idUsuario) {
		Query q =
			em.createQuery("select Usuario from Usuario Usuario "
				+ "  where usuario.activo is true                "
				+ "  and usuario.usuAlta ='PORTAL'              "
				+ "  and usuario.persona.idPersona = :idUsuario          ");
		q.setParameter("idUsuario", idUsuario);
		Usuario usuario = (Usuario) q.getSingleResult();
		return usuario;
	}

	public String genLinkFuncionario() {
		String url =
			"/cargarCarpeta/postulantePorCarpeta/FuncionarioOeeEdit.seam?fromLink=/cargarCarpeta/postulantePorCarpeta/FichaPostulacionPorCarpeta&personaId="
				+ personaIdPersona
				+ "&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgrIdConcursoPuestoAgr;

		return url;
	}

	public String guardar() {
		if (!precondInit()) {
			limpiar();
			return null;
		}

		if (boolCopiaRealizada == null) {
			statusMessages.add(Severity.ERROR, "No se puede verificar si ya se realizó la copia");
			return null;
		}
		if (boolCopiaRealizada) {
			if (save() != null) {
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				limpiar();
				return "OK";
			}
			limpiar();
		} else {
			// Hacer una actualización en la tabla de PERSONAS:
			if (save() != null) {
				try {
					// Insertar en la tabla PERSONA_POSTULANTE:
					PersonaPostulante personaPostulante = new PersonaPostulante();
					personaPostulante.setUsuAlta(usuarioPostulante(persona.getIdPersona()).getCodigoUsuario());
					personaPostulante.setFechaAlta(new Date());
					personaPostulante.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
					personaPostulante.setFechaAltaOee(new Date());
					personaPostulante.setTipo("CARPETA");
					personaPostulante.setActivo(true);
					personaPostulante.setNombres(persona.getNombres());
					personaPostulante.setApellidos(persona.getApellidos());
					personaPostulante.setBarrio(persona.getBarrio());
					personaPostulante.setCallePrincipal(persona.getCallePrincipal());
					personaPostulante.setCiudadDirecc(persona.getCiudadByIdCiudadDirecc());
					personaPostulante.setCiudadNac(persona.getCiudadByIdCiudadNac());
					personaPostulante.setDatosEspecificos(persona.getDatosEspecificos());
					personaPostulante.setDepartamentoNro(persona.getDepartamentoNro());
					personaPostulante.setDireccionLaboral(persona.getDireccionLaboral());
					personaPostulante.setDocumentoIdentidad(persona.getDocumentoIdentidad());
					personaPostulante.setEMail(persona.getEMail());
					personaPostulante.setEstadoCivil(persona.getEstadoCivil());
					personaPostulante.setOtrasDirecciones(persona.getOtrasDirecciones());
					personaPostulante.setPaisExpedicionDoc(persona.getPaisByIdPaisExpedicionDoc());
					personaPostulante.setPiso(persona.getPiso());
					personaPostulante.setPrimeraLateral(persona.getPrimeraLateral());
					personaPostulante.setSegundaLateral(persona.getSegundaLateral());
					personaPostulante.setSexo(persona.getSexo());
					personaPostulante.setTelefonos(persona.getTelefonos());
					personaPostulante.setTelefonoLab(persona.getTelefonoLab());
					personaPostulante.setTelefonoPart(persona.getTelefonoPart());
					personaPostulante.setPersona(new Persona());
					personaPostulante.getPersona().setIdPersona(persona.getIdPersona());
					em.persist(personaPostulante);
					// Actualizar en la tabla POSTULACION:
					Query q =
						em.createQuery("select Postulacion from Postulacion Postulacion "
							+ " where Postulacion.activo is true and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
							+ " and Postulacion.persona.idPersona = :idPersona  ");
					q.setParameter("idGrupo", idGrupo);
					q.setParameter("idPersona", idPersona);
					Postulacion postulacion = (Postulacion) q.getSingleResult();
					postulacion.setPersonaPostulante(new PersonaPostulante());
					postulacion.getPersonaPostulante().setIdPersonaPostulante(personaPostulante.getIdPersonaPostulante());
					postulacion.setUsuModif(usuarioLogueado.getCodigoUsuario());
					postulacion.setFechaModif(new Date());
					postulacion.setFechaModOee(new Date());
					postulacion.setUsuModOee(usuarioLogueado.getCodigoUsuario());

					postulacion = em.merge(postulacion);
					em.flush();
					statusMessages.clear();
					statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
					limpiar();
					inicializarTabs();
					return "OK";
				} catch (Exception e) {
					e.printStackTrace();
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
					return null;
				}
			}
		}
		limpiar();
		return null;
	}

	private void inicializarTabs() {

		try {
			tab3ExpLaboralPorCarpeta.init();
			tab2FormacionAcademicaPorCarpetaFC.init();
			tab7VistaPrePostulacionActualFC.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private Long searchSexo(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista =
				em.createQuery(" select o from " + Referencias.class.getName() + " o"
					+ " WHERE o.activo = true and o.dominio = 'SEXO' and o.descAbrev = '" + c
					+ "' ORDER BY o.dominio, o.descLarga").getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	@SuppressWarnings("unchecked")
	private Long searchEstadoCivil(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista =
				em.createQuery(" select o from " + Referencias.class.getName() + " o"
					+ " WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' and o.descAbrev = '"
					+ c + "' ORDER BY o.dominio, o.descLarga").getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	/**
	 * Combo Departamento Nacimiento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		// idDptoNac = null;
		// idCiudadNac = null;

	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPaisNac != null) {
			departamentoList.getPais().setIdPais(idPaisNac);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Nacimiento
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		// idCiudadNac = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDptoNac != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoNac);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo Departamento Direccion
	 */
	public void updateDepartamentoDir() {
		List<Departamento> dptoList = getDepartamentosDirByPais();
		departamentosDirSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
		// idDptoDir = null;
		// idCiudadDir = null;
		// idLocalidadDir = null;
	}

	private List<Departamento> getDepartamentosDirByPais() {
		if (idPaisDir != null) {
			departamentoList.getPais().setIdPais(idPaisDir);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoDirSelectItem(List<Departamento> dptoList) {
		if (departamentosDirSelecItem == null)
			departamentosDirSelecItem = new ArrayList<SelectItem>();
		else
			departamentosDirSelecItem.clear();

		departamentosDirSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosDirSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Direccion
	 */
	public void updateCiudadDir() {
		List<Ciudad> ciuList = getCiudadByDptoDir();
		ciudadDirSelecItem = new ArrayList<SelectItem>();
		buildCiudadDirSelectItem(ciuList);
		// idCiudadDir = null;
		// idLocalidadDir = null;
	}

	private List<Ciudad> getCiudadByDptoDir() {
		if (idDptoDir != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoDir);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadDirSelectItem(List<Ciudad> ciudadList) {
		if (ciudadDirSelecItem == null)
			ciudadDirSelecItem = new ArrayList<SelectItem>();
		else
			ciudadDirSelecItem.clear();

		ciudadDirSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadDirSelecItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		localidadDirSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		// idLocalidadDir = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudadDir != null) {
			barrioList.setIdCiudad(idCiudadDir);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (localidadDirSelecItem == null)
			localidadDirSelecItem = new ArrayList<SelectItem>();
		else
			localidadDirSelecItem.clear();

		localidadDirSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			localidadDirSelecItem.add(new SelectItem(bar.getIdBarrio(), bar.getDescripcion()));
		}
	}

	private Boolean precondSave() {
		if (fechaNac == null || callePrincipal == null || idSexo == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Llene los datos obligatorios");
			return false;
		}
		Date hoy = new Date();
		if (compareDate(fechaNac, hoy) >= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "La fecha de nacimiento no puede ser mayor o igual a la fecha actual. Verifique.");
			return false;
		}
		if (!general.isFechaValida(fechaNac)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Nacimiento inválida");
			return false;
		}
		if (utilesFromController.esParaguay(idPaisNac)) {
			if (idDptoNac == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe seleccionar un Departamento de Nacimiento. Verifique.");
				return false;
			} else if (idCiudadNac == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe seleccionar una Ciudad de Nacimiento. Verifique.");
				return false;
			}
		}
		return true;
	}

	public String save() {
		if (!precondSave())
			return null;
		try {
			if (idLocalidadDir != null)
				persona.setBarrio(em.find(Barrio.class, idLocalidadDir));
			if (idCiudadDir != null)
				persona.setCiudadByIdCiudadDirecc(em.find(Ciudad.class, idCiudadDir));
			if (idCiudadNac != null)
				persona.setCiudadByIdCiudadNac(em.find(Ciudad.class, idCiudadNac));
			if (idEstadoCivil != null)
				persona.setEstadoCivil(em.find(Referencias.class, idEstadoCivil).getDescAbrev());

			persona.setSexo(em.find(Referencias.class, idSexo).getDescAbrev());

			persona.setApellidos(persona.getApellidos().trim());

			persona.setCallePrincipal(callePrincipal.trim());
			if (dptoNro != null)
				persona.setDepartamentoNro(dptoNro.trim());
			if (direccionLaboral != null)
				persona.setDireccionLaboral(direccionLaboral.trim());
			persona.setDocumentoIdentidad(persona.getDocumentoIdentidad().trim().toUpperCase());
			persona.setNombres(persona.getNombres().trim());
			if (otrasDirecciones != null)
				persona.setOtrasDirecciones(otrasDirecciones.trim());
			if (piso != null)
				persona.setPiso(piso.trim());
			if (callePrimeraLateral != null)
				persona.setPrimeraLateral(callePrimeraLateral.trim());
			if (calleSegundaLateral != null)
				persona.setSegundaLateral(calleSegundaLateral.trim());
			persona.setFechaNacimiento(fechaNac);
			persona.setTelefonos(telefonos);
			if(telefonoLab != null && !telefonoLab.trim().isEmpty())
			persona.setTelefonoLab(telefonoLab);
			if(telefonoPart != null && !telefonoPart.trim().isEmpty())
			persona.setTelefonoPart(telefonoPart);
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona.setFechaMod(new Date());
			persona.setUsuModOee(usuarioLogueado.getCodigoUsuario());
			persona.setFechaModOee(new Date());

			persona = em.merge(persona);
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	// public void update() {
	//
	// try {
	//
	// em.merge(persona);
	// em.flush();
	//
	// cargadoTab1 = true;
	// if (!guardado)
	// guardado = true;
	//
	// } catch (Exception e) {
	// statusMessages.add(Severity.ERROR, e.getMessage());
	// }
	// }

	public int compareDate(Date d1, Date d2) {
		if (d1.getYear() != d2.getYear())
			return d1.getYear() - d2.getYear();
		if (d1.getMonth() != d2.getMonth())
			return d1.getMonth() - d2.getMonth();
		return d1.getDate() - d2.getDate();
	}

	public void setearDesde() {
		link = true;

	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdSexo() {
		return idSexo;
	}

	public void setIdSexo(Long idSexo) {
		this.idSexo = idSexo;
	}

	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public Long getIdPaisNac() {
		return idPaisNac;
	}

	public void setIdPaisNac(Long idPaisNac) {
		this.idPaisNac = idPaisNac;
	}

	public Long getIdDptoNac() {
		return idDptoNac;
	}

	public void setIdDptoNac(Long idDptoNac) {
		this.idDptoNac = idDptoNac;
	}

	public Long getIdCiudadNac() {
		return idCiudadNac;
	}

	public void setIdCiudadNac(Long idCiudadNac) {
		this.idCiudadNac = idCiudadNac;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public List<SelectItem> getDepartamentosDirSelecItem() {
		return departamentosDirSelecItem;
	}

	public void setDepartamentosDirSelecItem(List<SelectItem> departamentosDirSelecItem) {
		this.departamentosDirSelecItem = departamentosDirSelecItem;
	}

	public List<SelectItem> getCiudadDirSelecItem() {
		return ciudadDirSelecItem;
	}

	public void setCiudadDirSelecItem(List<SelectItem> ciudadDirSelecItem) {
		this.ciudadDirSelecItem = ciudadDirSelecItem;
	}

	public Long getIdPaisDir() {
		return idPaisDir;
	}

	public void setIdPaisDir(Long idPaisDir) {
		this.idPaisDir = idPaisDir;
	}

	public Long getIdDptoDir() {
		return idDptoDir;
	}

	public void setIdDptoDir(Long idDptoDir) {
		this.idDptoDir = idDptoDir;
	}

	public Long getIdCiudadDir() {
		return idCiudadDir;
	}

	public void setIdCiudadDir(Long idCiudadDir) {
		this.idCiudadDir = idCiudadDir;
	}

	public Long getIdLocalidadDir() {
		return idLocalidadDir;
	}

	public void setIdLocalidadDir(Long idLocalidadDir) {
		this.idLocalidadDir = idLocalidadDir;
	}

	public List<SelectItem> getLocalidadDirSelecItem() {
		return localidadDirSelecItem;
	}

	public void setLocalidadDirSelecItem(List<SelectItem> localidadDirSelecItem) {
		this.localidadDirSelecItem = localidadDirSelecItem;
	}

	public Boolean getCargadoTab1() {
		return cargadoTab1;
	}

	public void setCargadoTab1(Boolean cargadoTab1) {
		this.cargadoTab1 = cargadoTab1;
	}

	public Boolean getGuardado() {
		return guardado;
	}

	public void setGuardado(Boolean guardado) {
		this.guardado = guardado;
	}

	public Boolean getLink() {
		return link;
	}

	public void setLink(Boolean link) {
		this.link = link;
	}

	public Date getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}

	public String getCallePrincipal() {
		return callePrincipal;
	}

	public void setCallePrincipal(String callePrincipal) {
		this.callePrincipal = callePrincipal;
	}

	public String getCallePrimeraLateral() {
		return callePrimeraLateral;
	}

	public void setCallePrimeraLateral(String callePrimeraLateral) {
		this.callePrimeraLateral = callePrimeraLateral;
	}

	public String getCalleSegundaLateral() {
		return calleSegundaLateral;
	}

	public void setCalleSegundaLateral(String calleSegundaLateral) {
		this.calleSegundaLateral = calleSegundaLateral;
	}

	public String getDptoNro() {
		return dptoNro;
	}

	public void setDptoNro(String dptoNro) {
		this.dptoNro = dptoNro;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDireccionLaboral() {
		return direccionLaboral;
	}

	public void setDireccionLaboral(String direccionLaboral) {
		this.direccionLaboral = direccionLaboral;
	}

	public String getOtrasDirecciones() {
		return otrasDirecciones;
	}

	public void setOtrasDirecciones(String otrasDirecciones) {
		this.otrasDirecciones = otrasDirecciones;
	}

	public String getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}

	public String getMsjCopiaRealizada() {
		return msjCopiaRealizada;
	}

	public void setMsjCopiaRealizada(String msjCopiaRealizada) {
		this.msjCopiaRealizada = msjCopiaRealizada;
	}

	public Boolean getBoolCopiaRealizada() {
		return boolCopiaRealizada;
	}

	public void setBoolCopiaRealizada(Boolean boolCopiaRealizada) {
		this.boolCopiaRealizada = boolCopiaRealizada;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getPersonaIdPersona() {
		return personaIdPersona;
	}

	public void setPersonaIdPersona(Long personaIdPersona) {
		this.personaIdPersona = personaIdPersona;
	}

	public Long getConcursoPuestoAgrIdConcursoPuestoAgr() {
		return concursoPuestoAgrIdConcursoPuestoAgr;
	}

	public void setConcursoPuestoAgrIdConcursoPuestoAgr(Long concursoPuestoAgrIdConcursoPuestoAgr) {
		this.concursoPuestoAgrIdConcursoPuestoAgr = concursoPuestoAgrIdConcursoPuestoAgr;
	}

	public Boolean getMsjOk() {
		return msjOk;
	}

	public void setMsjOk(Boolean msjOk) {
		this.msjOk = msjOk;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public String getTelefonoPart() {
		return telefonoPart;
	}

	public void setTelefonoPart(String telefonoPart) {
		this.telefonoPart = telefonoPart;
	}

	public String getTelefonoLab() {
		return telefonoLab;
	}

	public void setTelefonoLab(String telefonoLab) {
		this.telefonoLab = telefonoLab;
	}
	
	

}
