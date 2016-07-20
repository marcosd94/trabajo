package py.com.excelsis.sicca.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import com.sun.org.apache.bcel.internal.generic.NEW;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("clasificadorUoList")
public class ClasificadorUoList extends EntityQuery<ClasificadorUo> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<ClasificadorUo> listaClasificadorUos;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(value = "entityManager")
	EntityManager em;

	private static final String EJBQL = "select clasificadorUo from ClasificadorUo clasificadorUo";

	private static final String[] RESTRICTIONS =
		{
			"lower(clasificadorUo.denominacionUnidad) like lower(concat('%', concat(#{clasificadorUoList.clasificadorUo.denominacionUnidad},'%')))",
			"clasificadorUo.nivel=#{clasificadorUoList.clasificadorUo.nivel}",
			"clasificadorUo.activo=#{clasificadorUoList.estado.valor}",
			"lower(clasificadorUo.usuAlta) like lower(concat(#{clasificadorUoList.clasificadorUo.usuAlta},'%'))",
			"lower(clasificadorUo.usuMod) like lower(concat(#{clasificadorUoList.clasificadorUo.usuMod},'%'))", };

	private static final String[] RESTRICTIONS_UC117 = {
		"clasificadorUo.nivel = #{clasificadorUoList.clasificadorUo.nivel}",
		"clasificadorUo.subnivel = #{clasificadorUoList.clasificadorUo.subnivel}",
		"clasificadorUo.nro = #{clasificadorUoList.clasificadorUo.nro}",
		"clasificadorUo.activo=#{clasificadorUoList.estado.valor}", };

	private static final String ORDER =
		"clasificadorUo.nivel desc, clasificadorUo.subnivel asc, clasificadorUo.nro asc ";
	private ClasificadorUo clasificadorUo = new ClasificadorUo();
	Estado estado = Estado.ACTIVO;

	public ClasificadorUoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ClasificadorUo> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<ClasificadorUo> limpiar() {
		clasificadorUo = new ClasificadorUo();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<ClasificadorUo> obtainClasificador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_UC117));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta1() {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		try {
			String sql =
				" Select distinct(clasif_uo.id_clasificador_uo) , " + "clasif_uo.nivel as nivel, "
					+ "clasif_uo.subnivel as subnivel, " + "clasif_uo.nro as numero, "
					+ "clasif_uo.denominacion_unidad as denominacion, "
					+ "case when clasif_uo.politica is true then 'X' end as politica, "
					+ "case when clasif_uo.linea is true then 'X' end as linea "
					+ "from planificacion.clasificador_uo clasif_uo left join "
					+ "planificacion.clasificador_uo_requisito req_uo On "
					+ "req_uo.id_clasificador_uo =clasif_uo.id_clasificador_uo "
					+ " left join planificacion.requisito_minimo_cuo req_min_cuo "
					+ "On req_min_cuo.id_requisito_minimo_cuo = req_uo.id_requisito_minimo_cuo "
					+ "where 1=1  ";
			if (estado != null && estado.getValor() != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return null;
				}
				sql = sql + " and clasif_uo.activo = " + estado.getValor();
			}

			if (clasificadorUo.getNivel() != null) {
				if (!sufc.validarInput(clasificadorUo.getNivel().toString(), TiposDatos.INTEGER.getValor(), null)) {
					return null;
				}
				sql = sql + " and clasif_uo.nivel = " + clasificadorUo.getNivel();
			}

			if (clasificadorUo.getDenominacionUnidad() != null
				&& !clasificadorUo.getDenominacionUnidad().trim().isEmpty()) {
				if (!sufc.validarInput(clasificadorUo.getDenominacionUnidad().toLowerCase(), TiposDatos.STRING.getValor(), null)) {
					return null;
				}
				sql =
					sql
						+ " and lower(clasif_uo.denominacion_unidad) like '%"
						+ sufc.caracterInvalido(clasificadorUo.getDenominacionUnidad().toLowerCase())
						+ "%'";
			}

			sql += "	order by  clasif_uo.nivel desc,clasif_uo.subnivel asc,clasif_uo.nro asc ";
			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return new ArrayList<Object[]>();
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void print() {
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		List<Object[]> lista = consulta1();

		if (lista == null)
			return;
		if (lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existen datos...", "No hay datos..."));
			limpiar();
			return;
		}
		for (Object[] obj : lista) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[1] != null)
				map.put("nivel", new Integer(obj[1].toString()));
			if (obj[2] != null)
				map.put("subnivel", new Integer(obj[2].toString()));
			if (obj[3] != null)
				map.put("numero", new Integer(obj[3].toString()));
			if (obj[4] != null)
				map.put("denominacion", obj[4].toString());
			if (obj[5] != null)
				map.put("politica", obj[5].toString());
			if (obj[6] != null)
				map.put("linea", obj[6].toString());
			Long idClasificador = new Long(obj[0].toString());
			List<Object[]> listaRequisitosCUO = consulta2(idClasificador);

			for (int i = 0; i < listaRequisitosCUO.size(); i++) {
				Object[] objReqCuo = listaRequisitosCUO.get(i);
				Long idReqCuo = new Long(objReqCuo[0].toString());
				List<Object[]> listaReqMin = consulta3(idClasificador, idReqCuo);
				for (int j = 0; j < listaReqMin.size(); j++) {
					Object objReqMin = listaReqMin.get(j);
					if (i == 0)
						param.put("columna1", objReqMin.toString());
					if (i == 1)
						param.put("columna2", objReqMin.toString());
					if (i == 2)
						param.put("columna3", objReqMin.toString());
					if (i == 3)
						param.put("columna4", objReqMin.toString());
					if (i == 4)
						param.put("columna5", objReqMin.toString());
				}
				if (i == 0)
					map.put("columna1", objReqCuo[1].toString());
				if (i == 1)
					map.put("columna2", objReqCuo[1].toString());
				if (i == 2)
					map.put("columna3", objReqCuo[1].toString());
				if (i == 3)
					map.put("columna4", objReqCuo[1].toString());
				if (i == 4)
					map.put("columna5", objReqCuo[1].toString());

			}
			listaDatosReporte.add(map);

		}

		JasperReportUtils.respondPDF("RPT_Clasificadores_uo_CU122", false, listaDatosReporte, param);
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta2(Long id) {
		try {
			List<Object[]> config =
				em.createNativeQuery(" select req_uo.id_clasificador_uo_requisito as id, req_uo.descripcion as requisito  "
					+ "from planificacion.clasificador_uo_requisito req_uo  "
					+ "join planificacion.clasificador_uo clasif_uo  "
					+ "On req_uo.id_clasificador_uo =clasif_uo.id_clasificador_uo  "
					+ "join planificacion.requisito_minimo_cuo req_min_cuo  "
					+ "On req_min_cuo.id_requisito_minimo_cuo = req_uo.id_requisito_minimo_cuo "
					+ "where clasif_uo.id_clasificador_uo = " + id + " and req_uo.activo is true").getResultList();
			if (config == null) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta3(Long idClasificador, Long idReq) {
		try {
			List<Object[]> config =
				em.createNativeQuery("  select req_min_cuo.descripcion "
					+ "from  planificacion.requisito_minimo_cuo req_min_cuo "
					+ "join planificacion.clasificador_uo_requisito req_uo "
					+ "on req_uo.id_requisito_minimo_cuo = req_min_cuo.id_requisito_minimo_cuo "
					+ "join planificacion.clasificador_uo clasif_uo "
					+ "on clasif_uo.id_clasificador_uo = req_uo.id_clasificador_uo "
					+ "where req_min_cuo.activo is true "
					+ "and req_uo.id_clasificador_uo_requisito = " + idReq
					+ " and clasif_uo.id_clasificador_uo  = " + idClasificador).getResultList();
			if (config == null) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public ClasificadorUo getClasificadorUo() {
		return clasificadorUo;
	}

	public List<ClasificadorUo> obtenerListaClasificadorUos() {
		listaClasificadorUos = getResultList();
		return listaClasificadorUos;
	}

	public List<ClasificadorUo> getListaClasificadorUos() {
		return listaClasificadorUos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
