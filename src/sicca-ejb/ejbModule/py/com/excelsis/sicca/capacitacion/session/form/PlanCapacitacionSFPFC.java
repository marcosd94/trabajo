package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.PlanCapacitacion;
import py.com.excelsis.sicca.entity.PlanCapacitacionDet;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("planCapacitacionSFPFC")
public class PlanCapacitacionSFPFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idPlanCapacitacion;
	private PlanCapacitacion planCapacitacion;
	private List<PlanCapacitacionDet> listaDetalle = new ArrayList<PlanCapacitacionDet>();

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idPlanCapacitacion != null) {
			if (!sufc.validarInput(idPlanCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			planCapacitacion = em.find(PlanCapacitacion.class, idPlanCapacitacion);
			cargarDatosNivel();
			obtenerDetalles();
		}
	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (planCapacitacion.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(planCapacitacion
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}
	
	public void cargarCabecera() {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(planCapacitacion.getConfiguracionUoCab()
						.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(planCapacitacion
				.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(planCapacitacion.getConfiguracionUoCab()
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}
	
	private void obtenerDetalles(){
		String sql = "select det.* " +
				"from capacitacion.plan_capacitacion_det det " +
				"where det.activo is true " +
				"and det.id_plan = "+idPlanCapacitacion;
		listaDetalle = em.createNativeQuery(sql,
				PlanCapacitacionDet.class).getResultList();
	}
	
	public void abrirDoc(Long id) {

		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id, usuarioLogueado.getIdUsuario());

	}

	public Long getIdPlanCapacitacion() {
		return idPlanCapacitacion;
	}

	public void setIdPlanCapacitacion(Long idPlanCapacitacion) {
		this.idPlanCapacitacion = idPlanCapacitacion;
	}

	public PlanCapacitacion getPlanCapacitacion() {
		return planCapacitacion;
	}

	public void setPlanCapacitacion(PlanCapacitacion planCapacitacion) {
		this.planCapacitacion = planCapacitacion;
	}

	public List<PlanCapacitacionDet> getListaDetalle() {
		return listaDetalle;
	}

	public void setListaDetalle(List<PlanCapacitacionDet> listaDetalle) {
		this.listaDetalle = listaDetalle;
	}
	
	

}
