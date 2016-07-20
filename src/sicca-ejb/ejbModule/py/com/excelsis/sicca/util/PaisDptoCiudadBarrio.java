package py.com.excelsis.sicca.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;

@Scope(ScopeType.CONVERSATION)
@Name("paisDptoCiudadBarrioFC")
public class PaisDptoCiudadBarrio {
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	BarrioList barrioList;

	private Long idDpto;
	private Long idCiudad;
	private Long idBarrio;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;

	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentos();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDpto = null;
		idCiudad = null;
		idBarrio = null;
	}

	/**
	 * Busca el id correspondiente a Paraguay
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long buscarIdPais() {
		String cad = "select p.* from general.pais p where lower(p.descripcion) = 'paraguay'";
		List<Pais> lista = new ArrayList<Pais>();
		lista = em.createNativeQuery(cad, Pais.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPais();
		else
			return null;
	}

	private List<Departamento> getDepartamentos() {
		Long idPaisDir = buscarIdPais();
		if (idPaisDir != null) {
			Query q =
				em.createQuery("select departamento from Departamento departamento "
					+ " where departamento.pais.descripcion = 'PARAGUAY' and departamento.activo = true "
					+ " order by departamento.descripcion ");

			return q.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
			ciudadList.setMaxResults(null);
			ciudadList.setOrderColumn("descripcion");
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
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

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (barrioSelecItem == null)
			barrioSelecItem = new ArrayList<SelectItem>();
		else
			barrioSelecItem.clear();

		barrioSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			barrioSelecItem.add(new SelectItem(bar.getIdBarrio(), bar.getDescripcion()));
		}
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudad != null) {
			barrioList.setIdCiudad(idCiudad);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		barrioSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idBarrio = null;
	}

	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudad = null;
		idBarrio = null;
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

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
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

	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}

	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}
	
}
