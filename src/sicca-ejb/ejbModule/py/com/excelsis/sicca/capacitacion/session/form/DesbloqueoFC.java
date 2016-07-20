package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("desbloqueoFC")
public class DesbloqueoFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idListaDet;
	private ListaDet listaDet = new ListaDet();
	private Capacitaciones capacitaciones = new Capacitaciones();
	private PostulacionCap postulacionCap = new PostulacionCap();
	private Persona persona = new Persona();
	private String accion;
	private String bloqueado;

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idListaDet != null) {
			if (!sufc.validarInput(idListaDet.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}
			listaDet = em.find(ListaDet.class, idListaDet);
			if (listaDet.getMotivoHabilit() == null)
				bloqueado = "SI";
			else
				bloqueado = "NO";
			if (listaDet.getPostulacionCap() != null) {
				postulacionCap = listaDet.getPostulacionCap();
				if (postulacionCap.getCapacitacion() != null) {
					capacitaciones = em.find(Capacitaciones.class,
							postulacionCap.getCapacitacion()
									.getIdCapacitacion());
					cargarDatosNivel();
				}
				if (postulacionCap.getPersona() != null)
					persona = postulacionCap.getPersona();
			}
		}
	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitaciones.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(capacitaciones.getConfiguracionUoCab()
						.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(capacitaciones
				.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(capacitaciones
				.getConfiguracionUoCab().getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	public void abrirDoc(Long id) {

		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,
				usuarioLogueado.getIdUsuario());

	}

	public String desbloquear() {
		if (listaDet.getMotivoHabilit() == null
				|| listaDet.getMotivoHabilit().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Justificación para realizar la acción");
			return null;
		}
		listaDet.setFechaHabilit(new Date());
		listaDet.setUsuHabilit(usuarioLogueado.getCodigoUsuario());
		listaDet.setFechaMod(new Date());
		listaDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(listaDet);
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "ok";
	}

	public Long getIdListaDet() {
		return idListaDet;
	}

	public void setIdListaDet(Long idListaDet) {
		this.idListaDet = idListaDet;
	}

	public ListaDet getListaDet() {
		return listaDet;
	}

	public void setListaDet(ListaDet listaDet) {
		this.listaDet = listaDet;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public PostulacionCap getPostulacionCap() {
		return postulacionCap;
	}

	public void setPostulacionCap(PostulacionCap postulacionCap) {
		this.postulacionCap = postulacionCap;
	}

	public String getBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(String bloqueado) {
		this.bloqueado = bloqueado;
	}

}
