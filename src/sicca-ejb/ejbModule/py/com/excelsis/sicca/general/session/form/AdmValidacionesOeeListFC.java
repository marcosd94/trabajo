package py.com.excelsis.sicca.general.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.CabValidacion;
import py.com.excelsis.sicca.entity.CabValidacionOee;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DetValidacion;
import py.com.excelsis.sicca.entity.DetValidacionOee;
import py.com.excelsis.sicca.general.session.DetValidacionOeeList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.Estado;

@Scope(ScopeType.CONVERSATION)
@Name("admValidacionesOeeListFC")
public class AdmValidacionesOeeListFC implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	DetValidacionOeeList detValidacionOeeList;

	private Estado estado = Estado.ACTIVO;
	private String descripcion;
	private Long idGrupo;
	private List<DetValidacionOee> listaValidacionOee = new ArrayList<DetValidacionOee>();
	private List<SelectItem> listaSelectItems = new ArrayList<SelectItem>();
	private List<CabValidacion> listaValidaciones;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		nivelEntidadOeeUtil.init();
		searchAll();
	}

	public void searchAll() {
		listaValidacionOee = detValidacionOeeList.listaResultados();

	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
	}

	public void search() {
		detValidacionOeeList.setEstado(estado);
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			detValidacionOeeList.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
		if (idGrupo != null) {
			detValidacionOeeList.setIdGrupo(idGrupo);
		}
		if (descripcion != null && !descripcion.trim().isEmpty())
			detValidacionOeeList.getDetValidacionOee().setNombreValidacion(
					descripcion);
		listaValidacionOee = detValidacionOeeList.listaResultados();
	}

	public List<SelectItem> cargarSelectItem() {
		Query q = em
				.createQuery("select cabecera from CabValidacionOee cabecera "
						+ " where cabecera.configuracionUoCab.idConfiguracionUo = :id and cabecera.activo is true");
		q.setParameter("id", nivelEntidadOeeUtil.getIdConfigCab());
		List<CabValidacionOee> listaVal = q.getResultList();
		listaSelectItems = new Vector<SelectItem>();
		listaSelectItems.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (CabValidacionOee o : listaVal)
			listaSelectItems.add(new SelectItem(o.getIdCabValidacionOee(), ""
					+ o.getNombreGrupoValidacion()));
		return listaSelectItems;
	}

	public void reestablecerOee() {
		if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"No hay datos de OEE seleccionado");
			return;
		}
		try {
			obtenerListaValidaciones();
			borrarCabecera();
			reestablecer();
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void borrarCabecera() {
		Query queryCab = em
				.createQuery("select cabecera from CabValidacionOee cabecera "
						+ " where cabecera.configuracionUoCab.idConfiguracionUo = :id ");
		queryCab.setParameter("id", nivelEntidadOeeUtil.getIdConfigCab());
		Long id = nivelEntidadOeeUtil.getIdConfigCab();
		List<CabValidacionOee> listaCab = queryCab.getResultList();
		for (CabValidacionOee v : listaCab) {
			borrarDetalle(v.getIdCabValidacionOee());
			em.remove(v);
		}
	}

	private void borrarDetalle(Long id) {
		Query query = em.createQuery("select det from DetValidacionOee det "
				+ " where det.cabValidacionOee.idCabValidacionOee = :id ");
		query.setParameter("id", id);
		List<DetValidacionOee> listaDet = query.getResultList();
		for (DetValidacionOee d : listaDet)
			em.remove(d);
	}

	private void obtenerListaValidaciones() {
		Query qy = em.createQuery("select val from CabValidacion val");
		listaValidaciones = new ArrayList<CabValidacion>();
		listaValidaciones = qy.getResultList();
	}

	private void reestablecer() {
		for (CabValidacion val : listaValidaciones) {
			CabValidacionOee cabValidacionOee = new CabValidacionOee();
			cabValidacionOee.setActivo(val.getActivo());
			cabValidacionOee.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
			cabValidacionOee.setFechaAlta(new Date());
			cabValidacionOee.setJustifEstado(val.getJustifEstado());
			cabValidacionOee.setNombreGrupoValidacion(val
					.getNombreGrupoValidacion());
			cabValidacionOee.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(cabValidacionOee);
			Long id = val.getIdCabValidacion();
			Query quer = em
					.createQuery("select det from DetValidacion det where det.cabValidacion.idCabValidacion =:id");
			quer.setParameter("id", val.getIdCabValidacion());
			List<DetValidacion> listaDetalle = new ArrayList<DetValidacion>();
			listaDetalle = quer.getResultList();
			for (DetValidacion d : listaDetalle) {
				DetValidacionOee detValidacionOee = new DetValidacionOee();
				detValidacionOee.setActivo(d.getActivo());
				detValidacionOee.setBloquea(d.getBloquea());
				detValidacionOee.setCabValidacionOee(cabValidacionOee);
				detValidacionOee.setExplicValidacion(d.getExplicValidacion());
				detValidacionOee.setFechaAlta(new Date());
				detValidacionOee.setJustifEstado(d.getJustifEstado());
				detValidacionOee.setMensaje(d.getMensaje());
				detValidacionOee.setNombreValidacion(d.getNombreValidacion());
				detValidacionOee.setTipo(d.getTipo());
				detValidacionOee.setValorIdentif(d.getValorIdentif());
				detValidacionOee.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(detValidacionOee);
			}
		}
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<DetValidacionOee> getListaValidacionOee() {
		return listaValidacionOee;
	}

	public void setListaValidacionOee(List<DetValidacionOee> listaValidacionOee) {
		this.listaValidacionOee = listaValidacionOee;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public List<SelectItem> getListaSelectItems() {
		return listaSelectItems;
	}

	public void setListaSelectItems(List<SelectItem> listaSelectItems) {
		this.listaSelectItems = listaSelectItems;
	}

	public List<CabValidacion> getListaValidaciones() {
		return listaValidaciones;
	}

	public void setListaValidaciones(List<CabValidacion> listaValidaciones) {
		this.listaValidaciones = listaValidaciones;
	}

}
