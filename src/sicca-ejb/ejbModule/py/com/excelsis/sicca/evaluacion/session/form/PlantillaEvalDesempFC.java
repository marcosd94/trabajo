package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PlantillaEval;
import py.com.excelsis.sicca.entity.PlantillaEvalDet;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("plantillaEvalDesempFC")
public class PlantillaEvalDesempFC implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private PlantillaEval plantillaEval = new PlantillaEval();
	private PlantillaEvalDet plantillaEvalDet = new PlantillaEvalDet();
	private Long idPlantillaEval;
	private Long idMetodologia;
	private Integer index;
	private Boolean isEdit;
	private List<PlantillaEvalDet> listaPlantillaEvalDets = new ArrayList<PlantillaEvalDet>();
	private List<PlantillaEvalDet> listadoDetallesAEliminar = new ArrayList<PlantillaEvalDet>();

	public void init() throws Exception {
		plantillaEval = new PlantillaEval();
		if (idPlantillaEval != null) {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);

			if (!sufc.validarInput(idPlantillaEval.toString(),
					TiposDatos.LONG.getValor(), null))
				return;

			plantillaEval = em.find(PlantillaEval.class, idPlantillaEval);
			idMetodologia = plantillaEval.getDatosEspecifMetod()
					.getIdDatosEspecificos();
			buscarDetalles();
		}
	}

	private String obtenerSql() {
		String sql = "select det.* from eval_desempeno.plantilla_eval_det det "
				+ "where det.activo is true and det.id_plantilla_eval = "
				+ idPlantillaEval + " order by det.orden";
		return sql;
	}

	private void buscarDetalles() {

		listaPlantillaEvalDets = em.createNativeQuery(obtenerSql(),
				PlantillaEvalDet.class).getResultList();
		listadoDetallesAEliminar.addAll(listaPlantillaEvalDets);

	}

	public void agregarDetalle() {
		if (!chequearCamposCompletos())
			return;
		plantillaEvalDet.setActivo(true);
		plantillaEvalDet.setFechaAlta(new Date());
		plantillaEvalDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		Long valorOrden = new Long(0);
		if (listaPlantillaEvalDets.size() > 0) {
			int v = listaPlantillaEvalDets
					.get(listaPlantillaEvalDets.size() - 1).getOrden()
					.intValue();
			v = v + 1;
			valorOrden = new Long("" + v);
		} else
			valorOrden = obtenerOrden();
		plantillaEvalDet.setOrden(valorOrden);
		listaPlantillaEvalDets.add(plantillaEvalDet);
		limpiarCamposDatos();

	}

	public void limpiarCamposDatos() {
		plantillaEvalDet = new PlantillaEvalDet();
	}

	private Long obtenerOrden() {
		if (idPlantillaEval == null)
			return new Long(1);
		else {
			String sql = "SELECT MAX(det.orden)  "
					+ "FROM eval_desempeno.plantilla_eval_det det "
					+ "WHERE det.activo IS TRUE  "
					+ "and det.id_plantilla_eval = " + idPlantillaEval;
			Object config = em.createNativeQuery(sql).getSingleResult();
			if (config == null || config.toString().equals("0"))
				return new Long(1);
			else {
				Integer val = new Integer(config.toString());
				val++;
				return new Long(val.toString());
			}
		}
	}

	public void cancelarEdit() {
		limpiarCamposDatos();
		isEdit = false;
		index = null;
	}

	public void actualizarDetalle() {
		if (!chequearCamposCompletos())
			return;
		listaPlantillaEvalDets.set(index, plantillaEvalDet);
		cancelarEdit();
	}

	public void editar(Integer r) {
		index = r;
		plantillaEvalDet = new PlantillaEvalDet();
		plantillaEvalDet = listaPlantillaEvalDets.get(index);

		isEdit = true;
	}

	/**
	 * chequea que esten completos los campos obligatorios para 'Agregar' y
	 * 'Actualizar' registros
	 * 
	 * @return
	 */
	private Boolean chequearCamposCompletos() {
		statusMessages.clear();
		if (plantillaEval.getNombre() == null
				|| plantillaEval.getNombre().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Nombre Plantilla antes de realizar esta acción");
			return false;
		}

		if (idMetodologia == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Metodologia antes de realizar esta acción");
			return false;
		}

		if (plantillaEvalDet.getResultadoEsperado() == null
				|| plantillaEvalDet.getResultadoEsperado().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Resultado Esperado antes de realizar esta acción");
			return false;
		}

		if (plantillaEvalDet.getActividades() == null
				|| plantillaEvalDet.getActividades().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Actividades antes de realizar esta acción");
			return false;
		}

		if (plantillaEvalDet.getIndicadoresCump() == null
				|| plantillaEvalDet.getIndicadoresCump().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Indicadores de Cumplimiento antes de realizar esta acción");
			return false;
		}

		return true;
	}

	public void eliminar(Integer r) {
		index = r;
		plantillaEvalDet = new PlantillaEvalDet();
		plantillaEvalDet = listaPlantillaEvalDets.get(index);
		Long ordenEliminado = plantillaEvalDet.getOrden();

		if (plantillaEvalDet.getIdPlantillaEvalDet() != null) {
			PlantillaEvalDet evalDet = new PlantillaEvalDet();
			evalDet = em.find(PlantillaEvalDet.class,
					plantillaEvalDet.getIdPlantillaEvalDet());
			evalDet.setActivo(false);
			listadoDetallesAEliminar.set(index, evalDet);
		}
		listaPlantillaEvalDets.remove(plantillaEvalDet);
		for (int i = 0; i < listaPlantillaEvalDets.size(); i++) {
			PlantillaEvalDet det = new PlantillaEvalDet();
			det = listaPlantillaEvalDets.get(i);
			if (det.getOrden().intValue() == ordenEliminado.intValue() + 1) {
				det.setOrden(ordenEliminado);
				ordenEliminado = ordenEliminado + 1;
				listaPlantillaEvalDets.set(i, det);
			}
		}

	}

	public String save() {
		try {
			statusMessages.clear();
			plantillaEval.setActivo(true);
			plantillaEval.setFechaAlta(new Date());
			plantillaEval.setDatosEspecifMetod(em.find(DatosEspecificos.class,
					idMetodologia));
			plantillaEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(plantillaEval);
			for (PlantillaEvalDet det : listaPlantillaEvalDets) {
				det.setPlantillaEval(plantillaEval);
				em.persist(det);
			}
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String updated() {
		try {
			statusMessages.clear();
			em.clear();
			plantillaEval.setFechaMod(new Date());
			plantillaEval.setDatosEspecifMetod(em.find(DatosEspecificos.class,
					idMetodologia));
			plantillaEval.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(plantillaEval);
			List<PlantillaEvalDet> lista = em.createNativeQuery(obtenerSql(),
					PlantillaEvalDet.class).getResultList();
			for (PlantillaEvalDet e : listadoDetallesAEliminar) {

				if (!e.isActivo()) {
					for (PlantillaEvalDet ac : lista) {
						if (e.getIdPlantillaEvalDet().equals(
								ac.getIdPlantillaEvalDet())) {
							ac.setActivo(false);
							ac.setFechaMod(new Date());
							ac.setUsuMod(usuarioLogueado.getCodigoUsuario());
							em.merge(ac);
						}
					}
				}

			}
			for (PlantillaEvalDet det : listaPlantillaEvalDets) {
				if (det.getIdPlantillaEvalDet() == null) {
					det.setPlantillaEval(plantillaEval);
					em.persist(det);
				} else {
					if(det.isActivo()){
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
					det.setFechaMod(new Date());
					em.merge(det);
					}
				}
			}
			
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public PlantillaEval getPlantillaEval() {
		return plantillaEval;
	}

	public void setPlantillaEval(PlantillaEval plantillaEval) {
		this.plantillaEval = plantillaEval;
	}

	public PlantillaEvalDet getPlantillaEvalDet() {
		return plantillaEvalDet;
	}

	public void setPlantillaEvalDet(PlantillaEvalDet plantillaEvalDet) {
		this.plantillaEvalDet = plantillaEvalDet;
	}

	public Long getIdPlantillaEval() {
		return idPlantillaEval;
	}

	public void setIdPlantillaEval(Long idPlantillaEval) {
		this.idPlantillaEval = idPlantillaEval;
	}

	public Long getIdMetodologia() {
		return idMetodologia;
	}

	public void setIdMetodologia(Long idMetodologia) {
		this.idMetodologia = idMetodologia;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public List<PlantillaEvalDet> getListaPlantillaEvalDets() {
		return listaPlantillaEvalDets;
	}

	public void setListaPlantillaEvalDets(
			List<PlantillaEvalDet> listaPlantillaEvalDets) {
		this.listaPlantillaEvalDets = listaPlantillaEvalDets;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public List<PlantillaEvalDet> getListadoDetallesAEliminar() {
		return listadoDetallesAEliminar;
	}

	public void setListadoDetallesAEliminar(
			List<PlantillaEvalDet> listadoDetallesAEliminar) {
		this.listadoDetallesAEliminar = listadoDetallesAEliminar;
	}

}
