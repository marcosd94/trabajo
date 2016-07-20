package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.PlantillaEvalDet;
import py.com.excelsis.sicca.entity.ReglaCab;
import py.com.excelsis.sicca.entity.ReglaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ReglaCabHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admReglaDetalleFC")
public class AdmReglaDetalleFC implements Serializable {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReglaCabHome reglaCabHome;

	private ReglaCab reglaCab = new ReglaCab();
	private ReglaDet reglaDet = new ReglaDet();
	private List<ReglaDet> listaReglaDets = new ArrayList<ReglaDet>();
	private List<ReglaDet> listaReglaDetsInactivos = new ArrayList<ReglaDet>();

	private Integer index;
	private Integer cantVacancia;
	private Integer cantPostulantes;
	private Integer cantRelacion;
	private Boolean isEdit;

	public void init() {

		if (reglaCabHome.isIdDefined()) {
			reglaCab = reglaCabHome.getInstance();
			searchDetail();
			cantVacancia = obtenerCantidadPuestos();
		}
	}

	private void searchDetail() {
		Query q = em
				.createQuery("select det from ReglaDet det "
						+ " where det.activo is true and det.reglaCab.idReglaCab = :id  order by det.cantVacancias");
		q.setParameter("id", reglaCab.getIdReglaCab());
		listaReglaDets = q.getResultList();
	}

	private Integer obtenerCantidadPuestos() {
		Query q = em.createQuery("select det from ReglaDet det "
				+ " where det.activo is true order by det.cantVacancias ");
		List<ReglaDet> lista = q.getResultList();
		if (lista.isEmpty())
			return new Integer(1);
		else {
			return (lista.get(lista.size() - 1).getCantVacancias().intValue() + 1);
		}
	}

	private Boolean existeDetalle() {
		Query q = em
				.createQuery("select det from ReglaDet det "
						+ " where det.activo is true and det.reglaCab.idReglaCab = :id "
						+ "and det.cantVacancias = :cant");
		q.setParameter("id", reglaCab.getIdReglaCab());
		q.setParameter("cant", cantVacancia);
		List<ReglaDet> lista = q.getResultList();
		if (lista.isEmpty())
			return false;
		return true;

	}

	public void agregarDetalle() {
		if (!chequearCamposCompletos())
			return;
		reglaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		reglaDet.setFechaAlta(new Date());
		reglaDet.setActivo(true);
		reglaDet.setCantPostulantes(cantPostulantes);
		reglaDet.setCantVacancias(cantVacancia);
		reglaDet.setRelacionPuestoVac(cantRelacion);
		listaReglaDets.add(reglaDet);

		limpiarCamposDatos();

	}

	public void editar(Integer r) {
		index = r;
		isEdit = true;
		reglaDet = listaReglaDets.get(index);
		cantPostulantes = reglaDet.getCantPostulantes();
		cantRelacion = reglaDet.getRelacionPuestoVac();
		cantVacancia = reglaDet.getCantVacancias();
	}

	public void calcularRelacion() {

		if (cantPostulantes == null || cantPostulantes < 1) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo Postulantes");
			return;
		}

		cantRelacion = cantPostulantes / cantVacancia;

	}

	public void actualizarDetalle() {
		if (!chequearCamposCompletos())
			return;

		if (!reglaDet.isActivo()) {
			listaReglaDetsInactivos.add(reglaDet);
			listaReglaDets.remove(index);
		} else {
			reglaDet.setCantPostulantes(cantPostulantes);
			reglaDet.setCantVacancias(cantVacancia);
			reglaDet.setRelacionPuestoVac(cantRelacion);
			listaReglaDets.set(index, reglaDet);
		}
		cancelarEdit();

	}

	public void cancelarEdit() {
		limpiarCamposDatos();
		isEdit = false;
		index = null;
		if (!listaReglaDets.isEmpty())
			reglaDet.setCantVacancias(listaReglaDets.get(
					listaReglaDets.size() - 1).getCantVacancias() + 1);
		else
			reglaDet.setCantVacancias(1);

	}

	public void limpiarCamposDatos() {
		reglaDet = new ReglaDet();
		if (!listaReglaDets.isEmpty())
			cantVacancia = listaReglaDets.get(listaReglaDets.size() - 1)
					.getCantVacancias() + 1;
		else
			cantVacancia = 1;
		cantPostulantes = null;
		cantRelacion = null;

	}

	private Boolean chequearCamposCompletos() {
		statusMessages.clear();
		if (cantVacancia == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Cantidad de Puestos antes de realizar esta acción");
			return false;
		}

		if (cantVacancia.intValue() < 1) {
			statusMessages.add(Severity.ERROR,
					"El campo Cantidad de Puestos debe ser mayor a 0");
			return false;
		}

		if (cantPostulantes == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Postulantes antes de realizar esta acción");
			return false;
		}

		if (cantPostulantes.intValue() < 1) {
			statusMessages.add(Severity.ERROR,
					"El campo Postulantes debe ser mayor a 0");
			return false;
		}
		if (existeDetalle()) {
			statusMessages
					.add(Severity.ERROR,
							"Ya existe un detalle con la misma cabecera y Cantidad de Puestos activo, verifique");
			return false;
		}

		return true;
	}

	public String save() {
		try {
			for (ReglaDet det : listaReglaDets) {
				if (det.getIdReglaDet() == null) {
					det.setReglaCab(reglaCab);
					em.persist(det);
				} else {
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
					det.setFechaMod(new Date());
					em.merge(det);
				}
			}
			for (ReglaDet detInac : listaReglaDetsInactivos) {
				detInac.setUsuMod(usuarioLogueado.getCodigoUsuario());
				detInac.setFechaMod(new Date());
				em.merge(detInac);
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

	public ReglaCab getReglaCab() {
		return reglaCab;
	}

	public void setReglaCab(ReglaCab reglaCab) {
		this.reglaCab = reglaCab;
	}

	public ReglaDet getReglaDet() {
		return reglaDet;
	}

	public void setReglaDet(ReglaDet reglaDet) {
		this.reglaDet = reglaDet;
	}

	public List<ReglaDet> getListaReglaDets() {
		return listaReglaDets;
	}

	public void setListaReglaDets(List<ReglaDet> listaReglaDets) {
		this.listaReglaDets = listaReglaDets;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public List<ReglaDet> getListaReglaDetsInactivos() {
		return listaReglaDetsInactivos;
	}

	public void setListaReglaDetsInactivos(
			List<ReglaDet> listaReglaDetsInactivos) {
		this.listaReglaDetsInactivos = listaReglaDetsInactivos;
	}

	public Integer getCantVacancia() {
		return cantVacancia;
	}

	public void setCantVacancia(Integer cantVacancia) {
		this.cantVacancia = cantVacancia;
	}

	public Integer getCantPostulantes() {
		return cantPostulantes;
	}

	public void setCantPostulantes(Integer cantPostulantes) {
		this.cantPostulantes = cantPostulantes;
	}

	public Integer getCantRelacion() {
		return cantRelacion;
	}

	public void setCantRelacion(Integer cantRelacion) {
		this.cantRelacion = cantRelacion;
	}

}
