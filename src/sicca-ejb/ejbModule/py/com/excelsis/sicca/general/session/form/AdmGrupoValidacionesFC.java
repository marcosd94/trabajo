package py.com.excelsis.sicca.general.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CabValidacion;
import py.com.excelsis.sicca.entity.CabValidacionOee;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DetValidacion;
import py.com.excelsis.sicca.entity.DetValidacionOee;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.general.session.CabValidacionHome;
import py.com.excelsis.sicca.general.session.CabValidacionList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.PostulacionHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admGrupoValidacionesFC")
public class AdmGrupoValidacionesFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CabValidacionHome cabValidacionHome;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private CabValidacion cabValidacion = new CabValidacion();
	private List<ConfiguracionUoCab> listaOee;
	private List<CabValidacionOee> listaValidacionOee;
	private List<CabValidacion> listaValidaciones;

	public void init() {
		if (cabValidacionHome.isIdDefined()) {
			cabValidacion = cabValidacionHome.getInstance();
		} else {
			cabValidacion.setActivo(true);
		}
	}

	public String save() {
		if (!checkData())
			return null;
		cabValidacionHome.setInstance(cabValidacion);

		if (cabValidacionHome.persist().equalsIgnoreCase("persisted")) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		}
		return null;

	}

	public String updated() {
		if (!checkData())
			return null;
		cabValidacionHome.setInstance(cabValidacion);

		if (cabValidacionHome.update().equalsIgnoreCase("updated")) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		}
		return null;

	}

	private boolean checkData() {
		String operation = cabValidacionHome.isIdDefined() ? "update"
				: "persist";
		if (cabValidacion.getNombreGrupoValidacion().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio descripción");
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(cabValidacion
				.getNombreGrupoValidacion().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}

		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_registro_duplicado"));
			return false;
		}
		if (operation.equalsIgnoreCase("update")
				&& (cabValidacion.getJustifEstado() == null || cabValidacion
						.getJustifEstado().trim().isEmpty())) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio Justificación estado");
			return false;

		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql = "SELECT o FROM CabValidacion o WHERE lower(o.nombreGrupoValidacion) =:desc";
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idCabValidacion != "
					+ cabValidacion.getIdCabValidacion().longValue();
		}
		List<Pais> list = em
				.createQuery(hql)
				.setParameter(
						"desc",
						cabValidacion.getNombreGrupoValidacion().trim()
								.toLowerCase()).getResultList();
		return list.isEmpty();
	}

	private Boolean checkCopia(ConfiguracionUoCab uo) {
		for (CabValidacionOee v : listaValidacionOee) {
			if (v.getConfiguracionUoCab().equals(uo))
				return false;
		}
		return true;
	}

	public void copiar() {
		try {
			Query q = em
					.createQuery("select oee from ConfiguracionUoCab oee where oee.activo is true ");
			listaOee = new ArrayList<ConfiguracionUoCab>();
			listaOee = q.getResultList();
			Query query = em
					.createQuery("select cab from CabValidacionOee cab");
			listaValidacionOee = new ArrayList<CabValidacionOee>();
			listaValidacionOee = query.getResultList();
			Query qy = em.createQuery("select val from CabValidacion val");
			listaValidaciones = new ArrayList<CabValidacion>();
			listaValidaciones = qy.getResultList();

			for (ConfiguracionUoCab c : listaOee) {
				if (checkCopia(c)) {
					for (CabValidacion val : listaValidaciones) {
						CabValidacionOee cabValidacionOee = new CabValidacionOee();
						cabValidacionOee.setActivo(val.getActivo());
						cabValidacionOee.setConfiguracionUoCab(c);
						cabValidacionOee.setFechaAlta(new Date());
						cabValidacionOee.setJustifEstado(val.getJustifEstado());
						cabValidacionOee.setNombreGrupoValidacion(val
								.getNombreGrupoValidacion());
						cabValidacionOee.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						em.persist(cabValidacionOee);
						Query quer = em
								.createQuery("select det from DetValidacion det where det.cabValidacion.idCabValidacion =:id");
						quer.setParameter("id", val.getIdCabValidacion());
						List<DetValidacion> listaDetalle = new ArrayList<DetValidacion>();
						listaDetalle = quer.getResultList();
						for (DetValidacion d : listaDetalle) {
							DetValidacionOee detValidacionOee = new DetValidacionOee();
							detValidacionOee.setActivo(d.getActivo());
							detValidacionOee.setBloquea(d.getBloquea());
							detValidacionOee
									.setCabValidacionOee(cabValidacionOee);
							detValidacionOee.setExplicValidacion(d
									.getExplicValidacion());
							detValidacionOee.setFechaAlta(new Date());
							detValidacionOee.setJustifEstado(d
									.getJustifEstado());
							detValidacionOee.setMensaje(d.getMensaje());
							detValidacionOee.setNombreValidacion(d
									.getNombreValidacion());
							detValidacionOee.setTipo(d.getTipo());
							detValidacionOee.setUsuAlta(usuarioLogueado
									.getCodigoUsuario());
							em.persist(detValidacionOee);
						}
					}
				}
			}

			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void pdf() throws SQLException {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU520_OEE_sin_validacion", mapa,
					false, conn, usuarioLogueado);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT planificacion.configuracion_uo_cab.id_configuracion_uo,   ");
		sql.append(" planificacion.configuracion_uo_cab.denominacion_unidad AS OEE,    ");
		sql.append(" sinarh.sin_entidad.ent_nombre AS ENTIDAD, ");
		sql.append(" sinarh.sin_nivel_entidad.nen_nombre AS NIVEL  ");
		sql.append(" FROM planificacion.configuracion_uo_cab  ");
		sql.append(" LEFT JOIN general.cab_validacion_oee ON (planificacion.configuracion_uo_cab.id_configuracion_uo = general.cab_validacion_oee.id_configuracion_uo)  ");
		sql.append(" INNER JOIN planificacion.entidad ON (planificacion.configuracion_uo_cab.id_entidad = planificacion.entidad.id_entidad)  ");
		sql.append(" INNER JOIN sinarh.sin_entidad ON (planificacion.entidad.id_sin_entidad = sinarh.sin_entidad.id_sin_entidad)  ");
		sql.append(" INNER JOIN sinarh.sin_nivel_entidad ON (sinarh.sin_entidad.ani_aniopre = sinarh.sin_nivel_entidad.ani_aniopre) ");
		sql.append(" AND (sinarh.sin_entidad.nen_codigo = sinarh.sin_nivel_entidad.nen_codigo) ");
		sql.append(" order by sinarh.sin_nivel_entidad.nen_nombre ");

		param.put("sql", sql.toString());

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	public CabValidacion getCabValidacion() {
		return cabValidacion;
	}

	public void setCabValidacion(CabValidacion cabValidacion) {
		this.cabValidacion = cabValidacion;
	}

	public List<ConfiguracionUoCab> getListaOee() {
		return listaOee;
	}

	public void setListaOee(List<ConfiguracionUoCab> listaOee) {
		this.listaOee = listaOee;
	}

	public List<CabValidacionOee> getListaValidacionOee() {
		return listaValidacionOee;
	}

	public void setListaValidacionOee(List<CabValidacionOee> listaValidacionOee) {
		this.listaValidacionOee = listaValidacionOee;
	}

	public List<CabValidacion> getListaValidaciones() {
		return listaValidaciones;
	}

	public void setListaValidaciones(List<CabValidacion> listaValidaciones) {
		this.listaValidaciones = listaValidaciones;
	}

}
