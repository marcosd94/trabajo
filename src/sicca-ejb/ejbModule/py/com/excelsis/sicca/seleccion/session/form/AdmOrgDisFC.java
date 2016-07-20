package py.com.excelsis.sicca.seleccion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.OrgDiscapacitadosPersona;
import py.com.excelsis.sicca.entity.OrganizacionDiscapacitados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ExcepcionGrPuestoList;
import py.com.excelsis.sicca.session.OrganizacionDiscapacitadosHome;
import py.com.excelsis.sicca.session.OrganizacionDiscapacitadosList;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("admOrgDisFC")
public class AdmOrgDisFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	BarrioList barrioList;
	@In(required = false, create = true)
	OrganizacionDiscapacitadosHome organizacionDiscapacitadosHome;
	@In(required = false)
	Usuario usuarioLogueado;
	private List<SelectItem> paisSelecItem;
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private Long idDpto;
	private Long idCiudad;
	private Long idPais;
	private List<OrganizacionDiscapacitados> lista;
	private String organizacion;
	private Estado estado = Estado.ACTIVO;

	public List<OrganizacionDiscapacitados> getLista() {
		return lista;
	}

	public void setLista(List<OrganizacionDiscapacitados> lista) {
		this.lista = lista;
	}

	public String getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(String organizacion) {
		this.organizacion = organizacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void init() {
		updatePais();
		updateDepartamento();
		updateCiudad();
		search();
	}

	public void initEdit() {

		if (organizacionDiscapacitadosHome.getInstance().getIdOrganizacion() != null) {
			OrganizacionDiscapacitados instancia = organizacionDiscapacitadosHome.getInstance();
			if (instancia.getCiudad() != null) {
				updatePais();
				idPais = instancia.getCiudad().getDepartamento().getPais().getIdPais();
				updateDepartamento();
				idDpto = instancia.getCiudad().getDepartamento().getIdDepartamento();
				updateCiudad();
				idCiudad = instancia.getCiudad().getIdCiudad();
			}
		} else {
			updatePais();
			updateDepartamento();
			updateCiudad();
		}
	}

	public void search() {
		StringBuffer SQL = new StringBuffer();
		StringBuffer elWhere = new StringBuffer();
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		SQL.append("select organizacionDiscapacitados from OrganizacionDiscapacitados organizacionDiscapacitados ");
		SQL.append(" WHERE 1=1 ");
		if (organizacion != null && !organizacion.trim().isEmpty()) {
			organizacion = organizacion.toUpperCase();
			elWhere.append(" AND upper(organizacionDiscapacitados.nombre) like '%" + organizacion
				+ "%'");
		}
		if (idPais != null) {
			elWhere.append(" AND organizacionDiscapacitados.ciudad.departamento.pais.idPais =:pais ");
			parametros.put("pais", new QueryValue(idPais));
		}
		if (idDpto != null) {
			elWhere.append(" AND organizacionDiscapacitados.ciudad.departamento.idDepartamento=:dpto ");
			parametros.put("dpto", new QueryValue(idDpto));
		}
		if (idCiudad != null) {
			elWhere.append(" AND organizacionDiscapacitados.ciudad.idCiudad =:ciudad ");
			parametros.put("ciudad", new QueryValue(idCiudad));
		}
		if (estado != null && estado.getValor() != null) {
			elWhere.append(" AND organizacionDiscapacitados.activo is :estado");
			parametros.put("estado", new QueryValue(estado.getValor()));
		}
		SQL.append(elWhere);
		OrganizacionDiscapacitadosList orgaDisList =
			(OrganizacionDiscapacitadosList) Component.getInstance("organizacionDiscapacitadosList", true);
		lista = orgaDisList.searchOrgaDis(SQL.toString(), parametros);
	}

	public String limpiar() {
		idPais = null;
		idCiudad = null;
		idDpto = null;
		organizacionDiscapacitadosHome.setInstance(em.find(OrganizacionDiscapacitados.class, new Long(0)));
		return "/seleccion/admOrgDis270/OrganizacionDiscapacitadosList.xhtml";
	}

	public String save() {
		if (organizacionDiscapacitadosHome.getInstance() != null && prencond()) {
			try {
				OrganizacionDiscapacitados instancia = organizacionDiscapacitadosHome.getInstance();
				Ciudad ciudad = em.find(Ciudad.class, idCiudad);
				instancia.setCiudad(ciudad);
				instancia.setFechaAlta(new Date());
				instancia.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				instancia.setActivo(true);
				em.persist(instancia);
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return "FAIL";
			}
		} else {
			return "";
		}
		return "OK";
	}

	private void activarInactivarPersonas(Boolean activo, List<OrgDiscapacitadosPersona> listaPersonas) {
		for (OrgDiscapacitadosPersona o : listaPersonas) {
			o.setActivo(activo);
			o = em.merge(o);
		}
		//em.flush();
	}

	public String update() {
		if (organizacionDiscapacitadosHome.getInstance() != null && prencond()) {
			try {
				OrganizacionDiscapacitados instancia = organizacionDiscapacitadosHome.getInstance();
				Ciudad ciudad = em.find(Ciudad.class, idCiudad);
				instancia.setCiudad(ciudad);
				instancia = em.merge(instancia);
				instancia.setUsuMod(usuarioLogueado.getCodigoUsuario());
				instancia.setFechaMod(new Date());
				activarInactivarPersonas(instancia.isActivo(), instancia.getOrgDiscapacitadosPersonas());
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return "FAIL";
			}
		} else {
			return "";
		}
		return "OK";
	}

	private Boolean prencond() {
		OrganizacionDiscapacitados instancia = organizacionDiscapacitadosHome.getInstance();
		if (instancia.getDireccion() == null || instancia.getEMail() == null
			|| instancia.getEMail().trim().isEmpty() || instancia.getNombre() == null
			|| instancia.getNombre().trim().isEmpty() || instancia.getRuc() == null
			|| instancia.getRuc().trim().isEmpty() || instancia.getTelefono() == null
			|| instancia.getTelefono().trim().isEmpty() || idCiudad == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar todos los datos requeridos");
			return false;
		}
		return true;
	}

	public void updatePais() {
		List<Pais> lista = getPais();
		paisSelecItem = new ArrayList<SelectItem>();
		buildPaisSelectItem(lista);
		idPais = null;
		idDpto = null;
		idCiudad = null;
	}

	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentos();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDpto = null;
		idCiudad = null;

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

	@SuppressWarnings("unchecked")
	private List<Pais> getPais() {
		String cad = "select p.* from general.pais p where p.activo is true order by p.descripcion";
		List<Pais> lista = new ArrayList<Pais>();
		lista = em.createNativeQuery(cad, Pais.class).getResultList();
		return lista;
	}

	public List<SelectItem> getPaisSelecItem() {
		return paisSelecItem;
	}

	public void setPaisSelecItem(List<SelectItem> paisSelecItem) {
		this.paisSelecItem = paisSelecItem;
	}

	private List<Departamento> getDepartamentos() {

		if (idPais != null) {
			Query q =
				em.createQuery("select departamento from Departamento departamento "
					+ " where departamento.pais.idPais = " + idPais
					+ " and departamento.activo = true " + " order by departamento.descripcion ");

			return q.getResultList();
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

	private void buildPaisSelectItem(List<Pais> lista) {
		if (paisSelecItem == null)
			paisSelecItem = new ArrayList<SelectItem>();
		else
			paisSelecItem.clear();

		paisSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Pais o : lista) {
			paisSelecItem.add(new SelectItem(o.getIdPais(), o.getDescripcion()));
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

	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudad = null;

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

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

}
