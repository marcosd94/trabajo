package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.EmpresaPersona;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EmprTercerizadaHome;
import py.com.excelsis.sicca.session.EmpresaPersonaHome;
import py.com.excelsis.sicca.session.EmpresaPersonaList;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.General;

@Scope(ScopeType.CONVERSATION)
@Name("staffEmpresaFormController")
public class StaffEmpresaFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1616557102861651107L;
	private static final String dominioSexo = "SEXO";
	private static final String dominioEstadoCivil = "ESTADO_CIVIL";

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;

	@In(create = true)
	EmprTercerizadaHome emprTercerizadaHome;
	@In(create = true)
	EmpresaPersonaHome empresaPersonaHome;
	@In(create = true)
	EmpresaPersonaList empresaPersonaList;
	@In(create = true)
	PersonaList personaList;
	@In(create = true)
	PersonaHome personaHome;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private EmprTercerizada emprTercerizada;
	private EmpresaPersona empresaPersona;
	private Persona persona = new Persona();
	private PersonaDTO personaDTO;

	private List<EmpresaPersona> listStaff;
	private List<SelectItem> referenciasBySexoSelectItem,
			referenciasByEstadoCivilSelectItem;

	private Long idReferenciaBySexo, idReferenciaByEstadoCivil;
	private Boolean esParaEditar = false;
	private boolean habBtnAddPersons=false;
	private String cedulaPersona;
	private String email;

	// PARA MODAL DE IMPRESION
	private Boolean estadoImpresion = true;
	private Long idPais;
	private Long idPersona;

	public void init() {
		estadoImpresion = Estado.ACTIVO.getValor();
		empresaPersona = new EmpresaPersona();
		emprTercerizada = emprTercerizadaHome.getInstance();
		if (emprTercerizadaHome.isIdDefined()) {
			empresaPersonaList.getEmpresa().setIdEmpresaTercerizada(
					emprTercerizada.getIdEmpresaTercerizada());
			empresaPersonaList.setMaxResults(null);
			listStaff = empresaPersonaList.getResultList();
		}
		updateSelectsItems();
	}

	public String save() {
		String result = null;
		try {
			if (listStaff.isEmpty()) {
				statusMessages.add(Severity.ERROR, SICCAAppHelper
						.getBundleMessage("CU196_msg_sin_funcionario"));
				return null;
			}
			// Todo debe estar ok
			for (EmpresaPersona ep : listStaff) {
				if (!detalleOk(ep)) {
					return null;
				}
			}
			for (EmpresaPersona ep : listStaff) {
				result = saveOrUpdatePersona(ep.getPersona());
				empresaPersonaHome.setInstance(ep);
				if (ep.getIdSelEmpresaPersona() == null) {

					result = empresaPersonaHome.persist();
				} else {
					result = empresaPersonaHome.update();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SICCAAppHelper
						.getBundleMessage("msg_operacion_exitosa"));
				emprTercerizadaHome.clearInstance();
			}
		}
		return result;
	}

	public void print() {
		try {
			String estado = "TODOS";
			Map<String, Object> param = new HashMap<String, Object>();

			StringBuffer whereClause = new StringBuffer();
			whereClause.append("WHERE emp.id_empresa_tercerizada = "
					+ emprTercerizada.getIdEmpresaTercerizada());
			if (estadoImpresion != null) {
				whereClause
						.append((whereClause.toString().contains("WHERE") ? " AND "
								: " WHERE ")
								+ "ep.activo = " + estadoImpresion);
				estado = Estado.getEstadoPorValor(estadoImpresion)
						.getDescripcion().toUpperCase();
			}

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("estado", estado);
			param.put("empresa", emprTercerizada.getNombre().toUpperCase());
			param.put("pais", emprTercerizada.getCiudad().getDepartamento()
					.getPais().getDescripcion());
			param.put("departamento", emprTercerizada.getCiudad()
					.getDepartamento().getDescripcion());
			param.put("ciudad", emprTercerizada.getCiudad().getDescripcion());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());

			Connection conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("ListarStaffFuncionariosUC197", param,
					false, conn, usuarioLogueado);
			JpaResourceBean.closeConnection(conn);
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		}
	}

	public void clearPrintParameters() {
		estadoImpresion = Estado.ACTIVO.getValor();
	}

	/*
	 * public void findByCi() { persona = new Persona(); if (idPais == null) {
	 * statusMessages.add(Severity.WARN,
	 * "Debe seleccionar el País de Expedición."); } else { if (cedulaPersona !=
	 * null && !cedulaPersona.isEmpty()) { List<Persona> listPersona = null;
	 * Query q = em.createQuery("select Persona from Persona Persona " +
	 * " where Persona.paisByIdPaisExpedicionDoc.idPais = " + idPais +
	 * " and Persona.documentoIdentidad ='" + cedulaPersona.trim() + "'");
	 * listPersona = q.getResultList(); if (!listPersona.isEmpty()) { persona =
	 * listPersona.get(0); } else { statusMessages.add(Severity.ERROR,
	 * SICCAAppHelper.getBundleMessage("CU196_persona_inexistente")); } } } }
	 */

	public void buscarPersona() throws Exception {

		/* Validaciones */
		if (idPais == null
				|| !seguridadUtilFormController.validarInput(
						idPais.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		if (cedulaPersona == null
				|| !seguridadUtilFormController.validarInput(
						cedulaPersona.toString(), TiposDatos.STRING.getValor(),
						null)) {
			return;
		}
		Pais pais = em.find(Pais.class, idPais);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(cedulaPersona,
				pais.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			persona = new Persona();
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = new Persona();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			completarDatosPersona(personaDTO.getPersona().getIdPersona(), false);
		}
	}

	private void completarDatosPersona(Long idPersona, Boolean completo) {
		persona = em.find(Persona.class, idPersona);
		if (completo) {
			idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			cedulaPersona = persona.getDocumentoIdentidad();
		}
	}

	public void limpiarDatosPersona() {
		persona = new Persona();
		idPersona = null;
		persona = new Persona();
		cedulaPersona = null;
		habBtnAddPersons = false;
	}

	public void addToList() {
		if (detalleOk(false)) {
			persona.setEMail(email);
			persona.setPaisByIdPaisExpedicionDoc(new Pais());
			persona.getPaisByIdPaisExpedicionDoc().setIdPais(idPais);
			empresaPersona.setEmprTercerizada(emprTercerizada);
			empresaPersona.setPersona(persona);
			empresaPersona.setCargo(empresaPersona.getCargo().trim()
					.toUpperCase());
			empresaPersona.setActivo(Estado.ACTIVO.getValor());
			listStaff.add(empresaPersona);
			clear();
		}

	}

	public void selectToEdit(int pos) {
		empresaPersona = listStaff.get(pos);
		empresaPersona.setPosicion(pos);
		persona = empresaPersona.getPersona();
		cedulaPersona = persona.getDocumentoIdentidad();
		idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		email = persona.getEMail();
		esParaEditar = true;
		habBtnAddPersons = false;
	}

	public void updateOnList() {
		if (detalleOk(true)) {
			listStaff.get(empresaPersona.getPosicion()).getPersona()
					.setEMail(email);
			listStaff.get(empresaPersona.getPosicion()).setPersona(persona);
			listStaff.get(empresaPersona.getPosicion()).setCargo(
					empresaPersona.getCargo().trim().toUpperCase());
			clear();
		}
	}

	public void clear() {
		empresaPersona = new EmpresaPersona();
		persona = new Persona();
		cedulaPersona = null;
		esParaEditar = false;
		idPais = null;
		habBtnAddPersons = false;
	}

	// METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private void updateSelectsItems() {
		referenciasBySexoSelectItem = new ArrayList<SelectItem>();
		referenciasByEstadoCivilSelectItem = new ArrayList<SelectItem>();

		referenciasBySexoSelectItem.add(new SelectItem(null, SICCAAppHelper
				.getBundleMessage("opt_select_one")));
		referenciasByEstadoCivilSelectItem.add(new SelectItem(null,
				SICCAAppHelper.getBundleMessage("opt_select_one")));

		String hqlBySexo = "SELECT o FROM Referencias o WHERE o.dominio = '"
				+ dominioSexo + "' ORDER BY o.idReferencias";
		String hqlByEstadoCivil = "SELECT o FROM Referencias o WHERE o.dominio = '"
				+ dominioEstadoCivil + "' ORDER BY o.idReferencias";

		// CARGAMOS LA LISTA DE SEXO
		List<Referencias> listSex = em.createQuery(hqlBySexo).getResultList();
		for (Referencias ref : listSex) {
			referenciasBySexoSelectItem.add(new SelectItem(ref.getDescAbrev(),
					ref.getDescLarga()));
		}

		// CARGAMOS LA LISTA DE ESTADO CIVIL
		List<Referencias> listEstadoCivil = em.createQuery(hqlByEstadoCivil)
				.getResultList();
		for (Referencias ref : listEstadoCivil) {
			referenciasByEstadoCivilSelectItem.add(new SelectItem(ref
					.getDescAbrev(), ref.getDescLarga()));
		}
	}

	private boolean detalleOk(EmpresaPersona empresaPersona) {
		Persona persona = empresaPersona.getPersona();
		String prefijoMsj = "En la lista Staff de Funcionarios: ";
		if (persona.getNombres() == null
				|| persona.getNombres().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							prefijoMsj
									+ SICCAAppHelper
											.getBundleMessage("CU196_persona_sin_nombre"));
			return false;
		}
		if (persona.getApellidos() == null
				|| persona.getApellidos().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							prefijoMsj
									+ SICCAAppHelper
											.getBundleMessage("CU196_persona_sin_apellido"));
			return false;
		}
		if (persona.getFechaNacimiento() == null) {
			statusMessages
					.add(Severity.ERROR,
							prefijoMsj
									+ SICCAAppHelper
											.getBundleMessage("CU196_persona_sin_fecha_nac"));
			return false;
		}
		if (persona.getSexo() == null || persona.getSexo().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							prefijoMsj
									+ SICCAAppHelper
											.getBundleMessage("CU196_persona_sin_sexo"));
			return false;
		}
		if (persona.getEstadoCivil() == null
				|| persona.getEstadoCivil().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							prefijoMsj
									+ SICCAAppHelper
											.getBundleMessage("CU196_persona_sin_estado_civil"));
			return false;
		}
		if (persona.getEMail() != null && !persona.getEMail().isEmpty()
				&& !General.isEmail(persona.getEMail().trim().toLowerCase())) {
			statusMessages.add(
					Severity.ERROR,
					prefijoMsj
							+ SICCAAppHelper
									.getBundleMessage("CU196_mail_invalido"));
			return false;
		}
		if (empresaPersona.getCargo() == null
				|| empresaPersona.getCargo().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							prefijoMsj
									+ SICCAAppHelper
											.getBundleMessage("CU196_persona_sin_cargo"));
			return false;
		}

		return true;
	}

	private boolean detalleOk(Boolean editado) {
		if (cedulaPersona != null
				&& !cedulaPersona.trim().isEmpty()
				&& (persona.getDocumentoIdentidad() == null || persona
						.getDocumentoIdentidad().trim().isEmpty()))
			persona.setDocumentoIdentidad(cedulaPersona.trim());
		if (idPais == null) {
			statusMessages.add(Severity.ERROR,
					"Se debe cargar el País de Expedición. Verifique");
			return false;
		}
		if (persona.getDocumentoIdentidad() == null
				|| persona.getDocumentoIdentidad().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper
					.getBundleMessage("CU196_persona_sin_nro_cedula"));
			return false;
		}
		if (persona.getNombres() == null
				|| persona.getNombres().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("CU196_persona_sin_nombre"));
			return false;
		}
		if (persona.getApellidos() == null
				|| persona.getApellidos().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper
					.getBundleMessage("CU196_persona_sin_apellido"));
			return false;
		}
		if (persona.getFechaNacimiento() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper
					.getBundleMessage("CU196_persona_sin_fecha_nac"));
			return false;
		}
		if (persona.getSexo() == null || persona.getSexo().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("CU196_persona_sin_sexo"));
			return false;
		}
		if (persona.getEstadoCivil() == null
				|| persona.getEstadoCivil().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper
					.getBundleMessage("CU196_persona_sin_estado_civil"));
			return false;
		}
		if (email != null && !email.isEmpty()
				&& !General.isEmail(email.trim().toLowerCase())) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("CU196_mail_invalido"));
			return false;
		}
		if (empresaPersona.getCargo() == null
				|| empresaPersona.getCargo().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("CU196_persona_sin_cargo"));
			return false;
		}
		if (!editado)
			for (EmpresaPersona ep : listStaff) {
				if (persona.getDocumentoIdentidad().equals(
						ep.getPersona().getDocumentoIdentidad())
						&& idPais.intValue() == ep.getPersona()
								.getPaisByIdPaisExpedicionDoc().getIdPais()
								.intValue()) {
					statusMessages.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("CU196_funcionario_existente"));
					return false;
				}
			}
		return true;
	}

	private String saveOrUpdatePersona(Persona p) {
		String result = null;
		try {
			p.setNombres(p.getNombres().trim().toUpperCase());
			p.setApellidos(p.getApellidos().trim().toUpperCase());
			p.setSexo(p.getSexo().trim().toUpperCase());
			p.setEstadoCivil(p.getEstadoCivil().trim().toUpperCase());
			p.setCallePrincipal(p.getCallePrincipal() != null ? p
					.getCallePrincipal().trim().toUpperCase() : null);
			p.setEMail(p.getEMail() != null ? p.getEMail().trim().toLowerCase()
					: null);
			if (p.getIdPersona() == null) {
				p.setActivo(true);
			}
			personaHome.setInstance(p);
			if (p.getIdPersona() != null)
				result = personaHome.update();
		/*	else {
				result = personaHome.persist();
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/seleccion/staffEmpresaTercerizada/EmpresaPersonaEdit";
	}
	public String toFindPersona() {
		idPersona=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/staffEmpresaTercerizada/EmpresaPersonaEdit";
	}

	// GETTERS Y SETTERS
	public EmprTercerizada getEmprTercerizada() {
		return emprTercerizada;
	}

	public void setEmprTercerizada(EmprTercerizada emprTercerizada) {
		this.emprTercerizada = emprTercerizada;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<EmpresaPersona> getListStaff() {
		return listStaff;
	}

	public void setListStaff(List<EmpresaPersona> listStaff) {
		this.listStaff = listStaff;
	}

	public void setEmpresaPersona(EmpresaPersona empresaPersona) {
		this.empresaPersona = empresaPersona;
	}

	public EmpresaPersona getEmpresaPersona() {
		return empresaPersona;
	}

	public void setCedulaPersona(String cedulaPersona) {
		this.cedulaPersona = cedulaPersona;
	}

	public String getCedulaPersona() {
		return cedulaPersona;
	}

	public Long getIdReferenciaBySexo() {
		return idReferenciaBySexo;
	}

	public void setIdReferenciaBySexo(Long idReferenciaBySexo) {
		this.idReferenciaBySexo = idReferenciaBySexo;
	}

	public Long getIdReferenciaByEstadoCivil() {
		return idReferenciaByEstadoCivil;
	}

	public void setIdReferenciaByEstadoCivil(Long idReferenciaByEstadoCivil) {
		this.idReferenciaByEstadoCivil = idReferenciaByEstadoCivil;
	}

	public List<SelectItem> getReferenciasBySexoSelectItem() {
		return referenciasBySexoSelectItem;
	}

	public void setReferenciasBySexoSelectItem(
			List<SelectItem> referenciasBySexoSelectItem) {
		this.referenciasBySexoSelectItem = referenciasBySexoSelectItem;
	}

	public List<SelectItem> getReferenciasByEstadoCivilSelectItem() {
		return referenciasByEstadoCivilSelectItem;
	}

	public void setReferenciasByEstadoCivilSelectItem(
			List<SelectItem> referenciasByEstadoCivilSelectItem) {
		this.referenciasByEstadoCivilSelectItem = referenciasByEstadoCivilSelectItem;
	}

	public void setEsParaEditar(Boolean esParaEditar) {
		this.esParaEditar = esParaEditar;
	}

	public Boolean getEsParaEditar() {
		return esParaEditar;
	}

	public void setEstadoImpresion(Boolean estadoImpresion) {
		this.estadoImpresion = estadoImpresion;
	}

	public Boolean getEstadoImpresion() {
		return estadoImpresion;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	

}
