package py.com.excelsis.sicca.session.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.FuncionarioOee;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.FuncionarioOeeHome;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.TipoNombramientoList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import enums.ModalidadOcupacionSeleccion;

@Scope(ScopeType.CONVERSATION)
@Name("funcionarioOeeCarpetaFC")
public class FuncionarioOeeCarpetaFC {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8381131410456084595L;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;

	@In(create = true)
	FuncionarioOeeHome funcionarioOeeHome;
	@In(create = true)
	PersonaHome personaHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(create = true)
	TipoNombramientoList tipoNombramientoList;

	private FuncionarioOee funcionarioOee;
	private Persona persona;

	private Long idTipoPlanta, idTipoNombramiento, idPersona;

	private List<FuncionarioOee> listDetail;
	private List<SelectItem> tipoNombramientoSelectItem;
	public ModalidadOcupacionSeleccion modalidadOcupacionSeleccion;
	private NivelEntidadOeeUtil nivelEntidadOeeUtil;
	private Boolean mostrarTipoNombramiento = true;
	private Long personaIdPersona;
	private Long concursoPuestoAgrIdConcursoPuestoAgr;

	public void init() {
		// CARGAMOS LOS OEE ASOCIADOS AL USUARIO
		if (idPersona == null) {
			funcionarioOee = new FuncionarioOee();
			persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			idPersona = persona.getIdPersona();
			if (listDetail == null) {
				listDetail = new ArrayList<FuncionarioOee>();

				for (FuncionarioOee f : persona.getFuncionarioOees()) {
					if (f.getActivo() == null || f.getActivo()) {
						f.setMostrarEliminar(false);
						listDetail.add(f);
					}
				}
			}
		}
		if (nivelEntidadOeeUtil == null)
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		updateTipoNombramientoSelectItem();
	}

	public void cambioModalidad() {
		if (modalidadOcupacionSeleccion != null
			&& modalidadOcupacionSeleccion.getValor().equalsIgnoreCase(ModalidadOcupacionSeleccion.CONTRATADO.getValor())) {
			mostrarTipoNombramiento = false;
		} else {
			mostrarTipoNombramiento = true;
		}
	}

	public void addRow() throws ParseException {
		if (!checkToDetail())
			return;

		personaHome.setInstance(persona);
		personaHome.update();

		funcionarioOee.setPersona(persona);
		funcionarioOee.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
		if (modalidadOcupacionSeleccion.getValor().equals(ModalidadOcupacionSeleccion.NOMBRADO.getValor())) {
			funcionarioOee.setTipoNombramiento(em.find(TipoNombramiento.class, idTipoNombramiento));
		}
		funcionarioOee.setModalidadOcupacion(modalidadOcupacionSeleccion.getValor());
		funcionarioOee.setMostrarEliminar(true);
		funcionarioOee.setActivo(true);

		funcionarioOee.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
		funcionarioOee.setFechaAltaOee(new Date());
		funcionarioOeeHome.setInstance(funcionarioOee);
		funcionarioOeeHome.save();
		funcionarioOeeHome.clearInstance();

		listDetail.add(funcionarioOee);

		clear();
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("GENERICO_MSG"));
	}

	 

	public List<SelectItem> updateTipoNombramientoSelectItem() {
		tipoNombramientoSelectItem = new ArrayList<SelectItem>();
		tipoNombramientoSelectItem.add(new SelectItem(null, SICCAAppHelper.getBundleMessage("opt_select_one")));

		if (idTipoPlanta != null) {
			tipoNombramientoList.getTipoPlanta().setIdTipoPlanta(idTipoPlanta);
			tipoNombramientoList.setOrderColumn("tipoNombramiento.descripcion");
			tipoNombramientoList.setMaxResults(null);
			List<TipoNombramiento> list = tipoNombramientoList.getResultList();
			for (TipoNombramiento tn : list) {
				tipoNombramientoSelectItem.add(new SelectItem(tn.getIdTipoNombramiento(), tn.getDescripcion()));
			}
		}
		return tipoNombramientoSelectItem;
	}

	public void eliminar(Integer fila) {
		if (fila.intValue() < listDetail.size()) {
			FuncionarioOee funcionarioOee = listDetail.remove(fila.intValue());
			funcionarioOee.setActivo(false);
			funcionarioOeeHome.setInstance(funcionarioOee);
			funcionarioOeeHome.update();
			funcionarioOeeHome.clearInstance();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("GENERICO_MSG"));
		}

	}

	private void clear() {

		funcionarioOee = new FuncionarioOee();
		idTipoPlanta = null;
		idTipoNombramiento = null;
		modalidadOcupacionSeleccion = null;
		nivelEntidadOeeUtil.limpiar();

	}

	private boolean checkToDetail() {
		if (persona.getEsFuncionario() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_respuesta"));
			return false;
		}

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_nivel"));
			return false;
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_entidad"));
			return false;
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_oee"));
			return false;
		}
		if (modalidadOcupacionSeleccion.getValor() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el combo Modalidad de Ocupación ");
			return false;
		}
		if (!modalidadOcupacionSeleccion.getValor().equals(ModalidadOcupacionSeleccion.CONTRATADO.getValor()))
			if (idTipoNombramiento == null) {
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_tipo_nombramiento"));
				return false;
			}
		if (funcionarioOee.getFechaIngreso() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_sin_fecha_ingreso"));
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		sdf.setLenient(false);
		Date ahora = null;
		try {
			ahora = sdf.parse(sdf.format(new Date()));
			sdf.parse(sdf.format(funcionarioOee.getFechaIngreso()));
			Calendar cal = Calendar.getInstance();
			cal.setTime(funcionarioOee.getFechaIngreso());

			if ((cal.get(Calendar.YEAR) + "").length() < 4) {
				throw new Exception("");
			}
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, "La fecha no es correcta. Verifique");
			e.printStackTrace();
			return false;
		}

		if (funcionarioOee.getFechaIngreso().getTime() >= (ahora.getTime())) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_fecha_ingreso_incorrecta"));
			return false;

		}
		if (modalidadOcupacionSeleccion.getValor().equals(ModalidadOcupacionSeleccion.NOMBRADO.getValor())) {
			for (FuncionarioOee func : listDetail) {
				if (func.getConfiguracionUoCab().getIdConfiguracionUo().equals(nivelEntidadOeeUtil.getIdConfigCab())
					&& func.getTipoNombramiento() != null
					&& func.getTipoNombramiento().getIdTipoNombramiento().equals(idTipoNombramiento)) {
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_existe_en_grilla"));
					return false;
				}
			}

		} else {
			for (FuncionarioOee func : listDetail) {
				if (func.getConfiguracionUoCab().getIdConfiguracionUo().equals(nivelEntidadOeeUtil.getIdConfigCab())
					&& func.getModalidadOcupacion().equals(modalidadOcupacionSeleccion.getValor())) {
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("FuncionarioOee_msg_existe_en_grilla"));
					return false;
				}
			}
		}

		return true;
	}

	// GETTERS Y SETTERS
	public FuncionarioOee getFuncionarioOee() {
		return funcionarioOee;
	}

	public void setFuncionarioOee(FuncionarioOee funcionarioOee) {
		this.funcionarioOee = funcionarioOee;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<FuncionarioOee> getListDetail() {
		return listDetail;
	}

	public void setListDetail(List<FuncionarioOee> listDetail) {
		this.listDetail = listDetail;
	}

	public Long getIdTipoPlanta() {
		return idTipoPlanta;
	}

	public void setIdTipoPlanta(Long idTipoPlanta) {
		this.idTipoPlanta = idTipoPlanta;
	}

	public Long getIdTipoNombramiento() {
		return idTipoNombramiento;
	}

	public void setIdTipoNombramiento(Long idTipoNombramiento) {
		this.idTipoNombramiento = idTipoNombramiento;
	}

	public List<SelectItem> getTipoNombramientoSelectItem() {
		return tipoNombramientoSelectItem;
	}

	public void setTipoNombramientoSelectItem(List<SelectItem> tipoNombramientoSelectItem) {
		this.tipoNombramientoSelectItem = tipoNombramientoSelectItem;
	}

	public ModalidadOcupacionSeleccion getModalidadOcupacionSeleccion() {
		return modalidadOcupacionSeleccion;
	}

	public void setModalidadOcupacionSeleccion(ModalidadOcupacionSeleccion modalidadOcupacionSeleccion) {
		this.modalidadOcupacionSeleccion = modalidadOcupacionSeleccion;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Boolean getMostrarTipoNombramiento() {
		return mostrarTipoNombramiento;
	}

	public void setMostrarTipoNombramiento(Boolean mostrarTipoNombramiento) {
		this.mostrarTipoNombramiento = mostrarTipoNombramiento;
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

}
