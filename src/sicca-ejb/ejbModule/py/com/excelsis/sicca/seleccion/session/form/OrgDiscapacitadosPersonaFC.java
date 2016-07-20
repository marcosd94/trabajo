package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import antlr.Utils;
import bsh.util.Util;

import py.com.excelsis.sicca.entity.OrgDiscapacitadosPersona;
import py.com.excelsis.sicca.entity.OrganizacionDiscapacitados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.Utiles;
import enums.Estado;
import enums.TiposDatos;

@Name("orgDiscapacitadosPersonaFC")
@Scope(ScopeType.CONVERSATION)
public class OrgDiscapacitadosPersonaFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1880709069113077964L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PersonaList personaList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private String docIdentidad;
	private Boolean habBtnAddPersons = false;
	private Integer posicion;
	private PersonaDTO personaDTO;
	private Long idOrganizacion;
	private OrganizacionDiscapacitados organizacionDiscapacitados;
	private Persona persona;
	private String cargo;
	private List<OrgDiscapacitadosPersona> orgDiscapacitadosPersonaList =
		new ArrayList<OrgDiscapacitadosPersona>();
	private Boolean esParaEditar = false;
	private Long idPais;
	private String nombreApellido;
	private Long idPersona;
	private String email;
	private String otrasDirecciones;
	private String telefonos;

	public void init() {

		clrVar();
		cargarPersona();
		if (idOrganizacion != null) {
			organizacionDiscapacitados = em.find(OrganizacionDiscapacitados.class, idOrganizacion);
			orgDiscapacitadosPersonaList = traerOrgDiscapacitadosPersonaList();
		}
	}

	private void cargarPersona() {
		if (idPersona != null) {
			persona = em.find(Persona.class, idPersona);
			completarDatosPersona(persona, true);

		}
	}

	private void clrVar() {
		persona = new Persona();
		cargo = null;
	}

	public String save() {
		try {

			if (orgDiscapacitadosPersonaList == null || orgDiscapacitadosPersonaList.size() == 0) {
				statusMessages.add(Severity.ERROR, "Debe agregar por lo menos un funcionario antes de guardar.");
				return null;
			}

			Date fechaActual = new Date();
			for (OrgDiscapacitadosPersona orgDiscapacitadosPersona : orgDiscapacitadosPersonaList) {
				Persona persona = orgDiscapacitadosPersona.getPersona();
				persona.setActivo(true);
				if (persona.getIdPersona() == null) {
					persona.setFechaAlta(fechaActual);
					persona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(persona);
				} else {
					persona.setFechaMod(fechaActual);
					persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					persona = em.merge(persona);
				}

				orgDiscapacitadosPersona.setPersona(persona);
				if (orgDiscapacitadosPersona.getIdOrgDiscapacitadosPersona() == null) {
					orgDiscapacitadosPersona.setFechaAlta(fechaActual);
					orgDiscapacitadosPersona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(orgDiscapacitadosPersona);
				} else {
					orgDiscapacitadosPersona.setFechaMod(fechaActual);
					orgDiscapacitadosPersona.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(orgDiscapacitadosPersona);
				}

			}

			em.flush();
			clrVar();
			orgDiscapacitadosPersonaList = new ArrayList<OrgDiscapacitadosPersona>();

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}

		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "persisted";
	}

	public void cancelar() {
		clrVar();
	}

	public void agregar() {
		if (!validate()) {
			return;
		}
		// Se verifica que no sea repetida
		if (isRepetido()) {
			statusMessages.add(Severity.ERROR, "La persona ya se encuentra en la lista. Verifique.");
			return;
		}
		cargarDatosPersona();
		persona = em.merge(persona);
		persona.setFechaMod(new Date());
		persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.flush();
		// Se agrega a la lista
		OrgDiscapacitadosPersona orgDiscapacitadosPersona = new OrgDiscapacitadosPersona();
		orgDiscapacitadosPersona.setActivo(true);
		orgDiscapacitadosPersona.setCargo(cargo);
		orgDiscapacitadosPersona.setOrganizacionDiscapacitados(organizacionDiscapacitados);
		orgDiscapacitadosPersona.setPersona(persona);

		orgDiscapacitadosPersonaList.add(orgDiscapacitadosPersona);

		clearPersona();
	}

	private void cargarDatosPersona() {
		persona.setEMail(email);
		persona.setTelefonos(telefonos);
		persona.setOtrasDirecciones(otrasDirecciones);
	}

	/**
	 * Valida el formaulario de edicion
	 * 
	 * @return
	 */
	private boolean validate() {
		statusMessages.clear();
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		String mensaje = "";
		String idCompomente = "";
		if (persona == null || (persona != null && persona.getIdPersona() == null)) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar una Persona");
			return false;
		} else if (vacio(cargo)) {
			mensaje = "Cargo es un campo requerido. Verifique.";
			idCompomente = "form:cargoField:cargo";
		} else if (!vacio(cargo) && cargo.trim().length() > 100) {
			mensaje = "Superada la longitud máxima (100). Verifique.";
			idCompomente = "form:cargoField:cargo";
		} else if (!vacio(otrasDirecciones) && otrasDirecciones.trim().length() > 100) {
			mensaje = "Superada la longitud máxima (100). Verifique.";
			idCompomente = "form:direccionField:direccion";
		} else if (!vacio(telefonos) && telefonos.trim().length() > 50) {
			mensaje = "Superada la longitud máxima (50). Verifique.";
			idCompomente = "form:idTelefonosField:telefonos";
		} else if (!vacio(email) && email.trim().length() > 50) {
			mensaje = "Superada la longitud máxima (50). Verifique.";
			idCompomente = "form:idEmailField:email";
		} else if (!vacio(email) && !General.isEmail(email)) {
			mensaje = "Dirección de correo inválida";
			idCompomente = "form:idEmailField:email";
		}

		if (!vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.add(Severity.ERROR, mensaje);
			// FACES ERROR
			message.setDetail(mensaje);
			// message.setSummary(mensaje);
			context.addMessage(idCompomente, message);
			return false;
		}

		return true;
	}

	public boolean vacio(String cadena) {
		if (cadena == null || "".equals(cadena))
			return true;
		return false;
	}

	private boolean isRepetido() {

		for (OrgDiscapacitadosPersona o : orgDiscapacitadosPersonaList) {
			if (persona.getIdPersona() != null && o.getPersona().getIdPersona() != null) {
				if (persona.getIdPersona().longValue() == o.getPersona().getIdPersona().longValue()) {
					return true;
				}
			} else if (persona.getDocumentoIdentidad().trim().equals(o.getPersona().getDocumentoIdentidad().trim())) {
				return true;
			}
		}

		return false;
	}

	public void editar(Integer fila) {
		OrgDiscapacitadosPersona orgDiscapacitadosPersona = orgDiscapacitadosPersonaList.get(fila);
		persona = orgDiscapacitadosPersona.getPersona();
		completarDatosPersona(persona, true);
		cargo = orgDiscapacitadosPersona.getCargo();
		posicion = fila;
		esParaEditar = true;
	}

	public void update() {
		try {
			OrgDiscapacitadosPersona orgDiscapacitadosPersona =
				orgDiscapacitadosPersonaList.get(posicion);
			orgDiscapacitadosPersona.setCargo(cargo);
			cargarDatosPersona();
			persona = em.merge(persona);
			persona.setFechaMod(new Date());
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.flush();
			clearPersona();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
	}

	public void clearPersona() {
		docIdentidad = null;
		idPais = null;
		persona = new Persona();
		cargo = null;
		email = null;
		telefonos = null;
		otrasDirecciones = null;
		idPersona = null;
		habBtnAddPersons = false;
		nombreApellido = null;
		setEsParaEditar(false);
	}

	@SuppressWarnings("unchecked")
	private List<OrgDiscapacitadosPersona> traerOrgDiscapacitadosPersonaList() {
		try {// orgDiscapacitadosPersonaList
			String consulta =
				" select orgDiscapacitadosPersona "
					+ " from OrgDiscapacitadosPersona orgDiscapacitadosPersona "
					+ " join orgDiscapacitadosPersona.organizacionDiscapacitados organizacionDiscapacitados "
					+ " where organizacionDiscapacitados.idOrganizacion = :idOrganizacion ";
			// "     and orgDiscapacitadosPersona.activo = :activo ";

			Query q = em.createQuery(consulta);
			q.setParameter("idOrganizacion", idOrganizacion);
			// q.setParameter("activo", new Boolean(true));

			return q.getResultList();
		} catch (Exception ex) {
			return new Vector<OrgDiscapacitadosPersona>();
		}
	}

	public void findByCi() {
		if (idPais == null) {
			statusMessages.add(Severity.WARN, "Debe seleccionar el País de Expedición.");

		} else {
			if (persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().isEmpty()) {
				personaList.getPersona().setDocumentoIdentidad(persona.getDocumentoIdentidad().trim());
				personaList.getPersona().setPaisByIdPaisExpedicionDoc(new Pais());
				personaList.getPersona().getPaisByIdPaisExpedicionDoc().setIdPais(idPais);
				personaList.setEstado(Estado.ACTIVO);
				List<Persona> listPersona = personaList.listaResultadosByCi();
				if (!listPersona.isEmpty()) {
					persona = listPersona.get(0);
				} else {
					statusMessages.add(Severity.WARN, SICCAAppHelper.getBundleMessage("CU196_persona_inexistente"));
				}
			}
		}
	}

	public String linkAddPersona(String from) {
		habBtnAddPersons = false;
		persona = null;
		return "/seleccion/persona/PersonaEdit.seam?personaFrom=" + from;

	}

	// Generico Persona
	public void buscarPersona() throws Exception {
		limpiarIdPersona();
		/* Validaciones */
		if (idPais == null
			|| !seguridadUtilFormController.validarInput(idPais.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		Pais pais = em.find(Pais.class, idPais);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());

		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2(false);
			persona = null;
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = null;
			limpiarDatosPersona2(false);
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}

	public void limpiarDatosPersona2(Boolean conDoc) {
		nombreApellido = null;
		cargo = null;
		email = null;
		otrasDirecciones = null;
		telefonos = null;
		if (conDoc) {
			docIdentidad = null;
		}

	}

	private void completarDatosPersona(Persona persona, Boolean completo) {
		nombreApellido = persona.getNombres() + " " + persona.getApellidos();
		email = persona.getEMail();
		otrasDirecciones = persona.getOtrasDirecciones();
		telefonos = persona.getTelefonos();

		if (completo) {
			idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
	}

	// GETTERS AND SETTERS
	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setOrganizacionDiscapacitados(OrganizacionDiscapacitados organizacionDiscapacitados) {
		this.organizacionDiscapacitados = organizacionDiscapacitados;
	}

	public OrganizacionDiscapacitados getOrganizacionDiscapacitados() {
		return organizacionDiscapacitados;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getCargo() {
		return cargo;
	}

	public void setOrgDiscapacitadosPersonaList(List<OrgDiscapacitadosPersona> orgDiscapacitadosPersonaList) {
		this.orgDiscapacitadosPersonaList = orgDiscapacitadosPersonaList;
	}

	public List<OrgDiscapacitadosPersona> getOrgDiscapacitadosPersonaList() {
		return orgDiscapacitadosPersonaList;
	}

	public void setEsParaEditar(Boolean esParaEditar) {
		this.esParaEditar = esParaEditar;
	}

	public Boolean getEsParaEditar() {
		return esParaEditar;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Boolean getHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(Boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public String getNombreApellido() {
		return nombreApellido;
	}

	public void setNombreApellido(String nombreApellido) {
		this.nombreApellido = nombreApellido;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public void limpiarIdPersona() {
		habBtnAddPersons = false;
		idPersona = null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

}
