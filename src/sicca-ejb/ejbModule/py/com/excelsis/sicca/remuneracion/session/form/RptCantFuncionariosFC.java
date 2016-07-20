package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;


@Scope(ScopeType.CONVERSATION)
@Name("rptCantFuncionariosFC")
public class RptCantFuncionariosFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SinarhUtiles sinarhUtiles;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private boolean primeraVez=true;

	private Integer mes;
	private Integer anho;
	private Integer mesHasta;
	private Integer anhoHasta;
	private Connection conn = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		nivelEntidadOeeUtil.init();
		cargarCabecera();
		
	}

	
	private void cargarCabecera(){
		if(primeraVez){
			if(usuarioLogueado.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
				//nivelEntidadOeeUtil.init2();
			}
			primeraVez=false;
		}
	}

	/* Select Items */

	@SuppressWarnings("unchecked")
	public List<SelectItem> mesAnhoSI(String dominio) {
		Query q = em.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.dominio = :dominio and Referencias.activo is true order by valorNum asc ");
		q.setParameter("dominio", dominio);
		List<Referencias> lista = q.getResultList();

		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (dominio.equalsIgnoreCase("MESES")) {
			for (Referencias o : lista)
				si.add(new SelectItem(o.getValorNum(), "" + o.getDescLarga()));
			return si;
		} else {
			for (Referencias o : lista)
				si.add(new SelectItem(o.getValorNum(), "" + o.getValorNum()));
			return si;
		}
	}
	
	public void imprimir(String formato, Boolean pcd) throws Exception {
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros(pcd);
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			if ("EXCEL".equals(formato))
				if(pcd)
					JasperReportUtils.respondXLS(
						"RPT_CANT_FUN_PCD", param,
						conn, usuarioLogueado);
				else
					JasperReportUtils.respondXLS(
							"RPT_CANT_FUN", param,
							conn, usuarioLogueado);
			else
				if(pcd)
					JasperReportUtils.respondPDF(
						"RPT_CANT_FUN_PCD", param, false,
						conn, usuarioLogueado);
				else
					JasperReportUtils.respondPDF(
							"RPT_CANT_FUN", param, false,
							conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
	private HashMap<String, Object> obtenerMapaParametros(Boolean pcd) throws Exception {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("oeeUsuarioLogueado", usuarioLogueado.getCodigoUsuario());
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("sql",consulta(pcd) );

		return param;
	}

	
	private String consulta(Boolean pcd){
		String sql="";
	
		sql  =" SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad,perm_mas,perm_fem, perm_mas + perm_fem as perm_total, contrat_mas, contrat_fem, "+
				" contrat_mas + contrat_fem as contrat_total, "+
				" case when nivel IN(11,12,13,14,15) THEN total_agrup1 "+
				" when nivel IN(21,22,23,24,25,27,28) THEN total_agrup2 "+
				" when nivel IN(30) THEN total_agrup3 "+
				" when nivel IN(40) THEN total_agrup4 "+
				" else 0 end as total_agrup, "+
				" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+
				" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+
				" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+
				" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+
				" else '' end as agrup, "+
				" SUM(total_agrup1 +total_agrup2+total_agrup3+total_agrup4) OVER() as total_sum, "+
				" SUM(perm_mas) OVER() as perm_mas_sum, "+
				" SUM(perm_fem) OVER() as perm_fem_sum, "+
				" SUM(perm_mas + perm_fem) OVER() as perm_total_sum, "+
				" SUM(contrat_mas) OVER() as contrat_mas_sum, "+
				" SUM(contrat_fem) OVER() as contrat_fem_sum, "+
				" SUM(contrat_mas + contrat_fem) OVER() as contrat_total_sum, "+
				" (perm_mas + perm_fem+contrat_mas + contrat_fem) as total_cant "+
				" FROM (SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc,";
				if(pcd){
					sql+=" SUM(case when obj_codigo=111 and temp1.sexo='M' and discapacidad is true THEN 1 ELSE 0 END) AS perm_mas, "+
					" SUM(case when obj_codigo=111 and temp1.sexo='F' and discapacidad is true THEN 1 ELSE 0 END) AS perm_fem, "+
					" SUM(case when obj_codigo!=111 and temp1.sexo='M' and discapacidad is true THEN 1 ELSE 0 END) AS contrat_mas, "+
					" SUM(case when obj_codigo!=111 and temp1.sexo='F' and discapacidad is true THEN 1 ELSE 0 END) AS contrat_fem, "+
					" SUM(case when temp1.nivel IN(11,12,13,14,15) AND temp1.sexo IS NOT NULL and discapacidad is true THEN 1 ELSE 0 END) AS total_agrup1, "+
					" SUM(case when temp1.nivel IN(21,22,23,24,25,27,28) AND temp1.sexo IS NOT NULL and discapacidad is true THEN 1 ELSE 0 END) AS total_agrup2, "+
					" SUM(case when temp1.nivel IN(30) AND temp1.sexo IS NOT NULL and discapacidad is true THEN 1 ELSE 0 END) AS total_agrup3, "+
					" SUM(case when temp1.nivel IN(40) AND temp1.sexo IS NOT NULL and discapacidad is true THEN 1 ELSE 0 END) AS total_agrup4 ";
				}
				else{
					sql+=" SUM(case when obj_codigo=111 and temp1.sexo='M' THEN 1 ELSE 0 END) AS perm_mas, "+
					" SUM(case when obj_codigo=111 and temp1.sexo='F' THEN 1 ELSE 0 END) AS perm_fem, "+
				    " SUM(case when obj_codigo!=111 and temp1.sexo='M' THEN 1 ELSE 0 END) AS contrat_mas, "+
				    " SUM(case when obj_codigo!=111 and temp1.sexo='F' THEN 1 ELSE 0 END) AS contrat_fem, "+
				    " SUM(case when temp1.nivel IN(11,12,13,14,15) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup1, "+
				    " SUM(case when temp1.nivel IN(21,22,23,24,25,27,28) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup2, "+
				    " SUM(case when temp1.nivel IN(30) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup3, "+
				    " SUM(case when temp1.nivel IN(40) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup4 ";
				}
				sql+=" FROM ( "+
				" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad "+
				" FROM remuneracion.remuneraciones_tmp tmp1 "+
				" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+
				" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+
				" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+
				" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+
				" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+
				" WHERE (obj_codigo=111 OR  obj_codigo BETWEEN 140 AND 149) AND "+
				" (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+
				" ) AS temp1 "+
				" INNER JOIN ("+
				" SELECT MAX(anho) as anho,MAX(mes) as mes,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+
				" GROUP BY nivel,entidad,oee )"+
				" AS temp2 "+
				" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+
				" where 1=1 ";
					if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
						sql+="	and temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
					if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
						sql+="	and temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
						sql+="	and temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
					if(anho !=null)
						sql+="	and temp1.anho = "+anho;
					if(mes !=null)
						sql+="	and temp1.mes = "+mes;
					sql+= "	GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
					"	order BY temp1.nivel,temp1.entidad,temp1.oee) tmp ";
		return sql;
	}
	
	public void imprimirNivel() throws Exception {
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerParametrosNivel();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
					JasperReportUtils.respondXLS(
						"RPT_CANT_NIV", param,
						conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
	private HashMap<String, Object> obtenerParametrosNivel() throws Exception {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("oeeUsuarioLogueado", usuarioLogueado.getCodigoUsuario());
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("sql",consultaNivel() );

		return param;
	}

	
	private String consultaNivel(){
		String sql="";
	
		sql  =" SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad, "+
				" a_mas,b_mas,c_mas,d_mas,e_mas,f_mas,g_mas,h_mas, "+
				" a_fem,b_fem,c_fem,d_fem,d_fem,e_fem,f_fem,g_fem,h_fem, "+
				" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas) as mas_total, "+
				" (a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) as fem_total, "+
				" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas + "+
				" a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) total_cant, "+
				" case when nivel IN(11,12,13,14,15) THEN total_agrup1 "+
				" when nivel IN(21,22,23,24,25,27,28) THEN total_agrup2 "+
				" when nivel IN(30) THEN total_agrup3 "+
				" when nivel IN(40) THEN total_agrup4 "+
				" else 0 end as total_agrup, "+
				" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+
				" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+
				" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+
				" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+
				" else '' end as agrup, "+
				" SUM(total_agrup1 +total_agrup2+total_agrup3+total_agrup4) OVER() as total_sum, "+
				" SUM(cast(a_mas as integer) +cast(b_mas as integer)+cast(c_mas as integer)+cast(d_mas as integer)+cast(e_mas as integer) "+
				" +cast(f_mas as integer)+cast(g_mas as integer)+cast(h_mas as integer)) OVER() as total_mas_sum, "+
				" SUM(cast(a_fem as integer) +cast(b_fem as integer)+cast(c_fem as integer)+cast(d_fem as integer)+cast(e_fem as integer) "+
				" +cast(f_fem as integer)+cast(g_fem as integer)+cast(h_fem as integer)) OVER() as total_fem_sum, "+
				" SUM(a_mas) OVER() as a_mas_sum, "+
				" SUM(b_mas) OVER() as b_mas_sum, "+
				" SUM(c_mas) OVER() as c_mas_sum, "+
				" SUM(d_mas) OVER() as d_mas_sum, "+
				" SUM(e_mas) OVER() as e_mas_sum, "+
				" SUM(f_mas) OVER() as f_mas_sum, "+
				" SUM(g_mas) OVER() as g_mas_sum, "+
				" SUM(h_mas) OVER() as h_mas_sum, "+
				" SUM(a_fem) OVER() as a_fem_sum, "+
				" SUM(b_fem) OVER() as b_fem_sum, "+
				" SUM(c_fem) OVER() as c_fem_sum, "+
				" SUM(d_fem) OVER() as d_fem_sum, "+
				" SUM(e_fem) OVER() as e_fem_sum, "+
				" SUM(f_fem) OVER() as f_fem_sum, "+
				" SUM(g_fem) OVER() as g_fem_sum, "+
				" SUM(h_fem) OVER() as h_fem_sum "+
				" FROM (SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 0 and 1824054 then 1 else 0 end) AS a_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 1824055 and 2499999 then 1 else 0 end) AS b_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 2500000 and 3999999 then 1 else 0 end) AS c_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 4000000 and 5999999 then 1 else 0 end) AS d_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 6000000 and 7999999 then 1 else 0 end) AS e_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 8000000 and 9999999 then 1 else 0 end) AS f_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado between 10000000 and 14999999 then 1 else 0 end) AS g_mas, "+
				" SUM(case when temp1.sexo='M' and temp1.presupuestado >= 15000000 then 1 else 0 end) AS h_mas, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 0 and 1824054 then 1 else 0 end) AS a_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 1824055 and 2499999 then 1 else 0 end) AS b_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 2500000 and 3999999 then 1 else 0 end) AS c_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 4000000 and 5999999 then 1 else 0 end) AS d_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 6000000 and 7999999 then 1 else 0 end) AS e_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 8000000 and 9999999 then 1 else 0 end) AS f_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado between 10000000 and 14999999 then 1 else 0 end) AS g_fem, "+
				" SUM(case when temp1.sexo='F' and temp1.presupuestado >= 15000000 then 1 else 0 end) AS h_fem, "+
				" SUM(case when temp1.nivel IN(11,12,13,14,15) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup1, "+
				" SUM(case when temp1.nivel IN(21,22,23,24,25,27,28) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup2, "+
				" SUM(case when temp1.nivel IN(30) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup3, "+
				" SUM(case when temp1.nivel IN(40) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup4 "+
				" FROM ( "+
				" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad , tmp1.presupuestado "+
				" FROM remuneracion.remuneraciones_tmp tmp1 "+
				" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+
				" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+
				" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+
				" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+
				" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+
				" WHERE obj_codigo=111 "+
				" AND (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+
				" ) AS temp1 "+
				" INNER JOIN ( "+
				" SELECT MAX(anho) as anho,MAX(mes) as mes,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+
				" GROUP BY nivel,entidad,oee ) AS temp2 "+
				" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+
				" where 1=1 ";
					if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
						sql+="	and temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
					if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
						sql+="	and temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
						sql+="	and temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
					if(anho !=null)
						sql+="	and temp1.anho = "+anho;
					if(mes !=null)
						sql+="	and temp1.mes = "+mes;
					sql+= "	GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
					"	order BY temp1.nivel,temp1.entidad,temp1.oee) tmp ";
		return sql;
	}
	public void imprimirCat() throws Exception {
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerParametrosCat();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
					JasperReportUtils.respondXLS(
						"RPT_CANT_CAT", param,
						conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
	private HashMap<String, Object> obtenerParametrosCat() throws Exception {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("oeeUsuarioLogueado", usuarioLogueado.getCodigoUsuario());
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("sql",consultaCat() );

		return param;
	}

	
	private String consultaCat(){
		String sql="";
	
		sql  =" SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad, "+
				" a_mas,b_mas,c_mas,d_mas,e_mas,f_mas,g_mas,h_mas, "+
				" a_fem,b_fem,c_fem,d_fem,d_fem,e_fem,f_fem,g_fem,h_fem, "+
				" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas + "+
				" a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) total_cant, "+
				" case when nivel IN(11,12,13,14,15) THEN total_agrup1 "+
				" when nivel IN(21,22,23,24,25,27,28) THEN total_agrup2 "+
				" when nivel IN(30) THEN total_agrup3 "+
				" when nivel IN(40) THEN total_agrup4 "+
				" else 0 end as total_agrup, "+
				" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+
				" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+
				" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+
				" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+
				" else '' end as agrup, "+
				" SUM(total_agrup1 +total_agrup2+total_agrup3+total_agrup4) OVER() as total_sum, "+
				" SUM(cast(a_mas as integer) +cast(b_mas as integer)+cast(c_mas as integer)+cast(d_mas as integer)+cast(e_mas as integer) "+
				" +cast(f_mas as integer)+cast(g_mas as integer)+cast(h_mas as integer)) OVER() as total_mas_sum, "+
				" SUM(cast(a_fem as integer) +cast(b_fem as integer)+cast(c_fem as integer)+cast(d_fem as integer)+cast(e_fem as integer) "+
				" +cast(f_fem as integer)+cast(g_fem as integer)+cast(h_fem as integer)) OVER() as total_fem_sum, "+
				" SUM(a_mas) OVER() as a_mas_sum, "+
				" SUM(b_mas) OVER() as b_mas_sum, "+
				" SUM(c_mas) OVER() as c_mas_sum, "+
				" SUM(d_mas) OVER() as d_mas_sum, "+
				" SUM(e_mas) OVER() as e_mas_sum, "+
				" SUM(f_mas) OVER() as f_mas_sum, "+
				" SUM(g_mas) OVER() as g_mas_sum, "+
				" SUM(h_mas) OVER() as h_mas_sum, "+
				" SUM(a_fem) OVER() as a_fem_sum, "+
				" SUM(b_fem) OVER() as b_fem_sum, "+
				" SUM(c_fem) OVER() as c_fem_sum, "+
				" SUM(d_fem) OVER() as d_fem_sum, "+
				" SUM(e_fem) OVER() as e_fem_sum, "+
				" SUM(f_fem) OVER() as f_fem_sum, "+
				" SUM(g_fem) OVER() as g_fem_sum, "+
				" SUM(h_fem) OVER() as h_fem_sum "+
				" FROM (	SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc, "+
				" SUM(case when categoria LIKE 'A%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS a_mas, "+
				" SUM(case when categoria LIKE 'B%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS b_mas, "+
				" SUM(case when categoria LIKE 'C%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS c_mas, "+
				" SUM(case when categoria LIKE 'D%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS d_mas, "+
				" SUM(case when categoria LIKE 'E%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS e_mas, "+
				" SUM(case when categoria LIKE 'F%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS f_mas, "+ 
				" SUM(case when categoria LIKE 'G%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS g_mas, "+
				" SUM(case when (categoria NOT LIKE 'A%' AND categoria NOT LIKE 'B%' AND categoria NOT LIKE 'C%' "+ 
				" AND categoria NOT LIKE 'D%' AND categoria NOT LIKE 'E%' AND categoria NOT LIKE 'F%' "+
				" AND categoria NOT LIKE 'G%') and temp1.sexo='M' THEN 1 ELSE 0 END) AS h_mas, "+
				" SUM(case when categoria LIKE 'A%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS a_fem, "+
				" SUM(case when categoria LIKE 'B%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS b_fem, "+
				" SUM(case when categoria LIKE 'C%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS c_fem, "+
				" SUM(case when categoria LIKE 'D%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS d_fem, "+
				" SUM(case when categoria LIKE 'E%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS e_fem, "+
				" SUM(case when categoria LIKE 'F%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS f_fem, "+
				" SUM(case when categoria LIKE 'G%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS g_fem, "+
				" SUM(case when (categoria NOT LIKE 'A%' AND categoria NOT LIKE 'B%' AND categoria NOT LIKE 'C%'  "+
				" AND categoria NOT LIKE 'D%' AND categoria NOT LIKE 'E%' AND categoria NOT LIKE 'F%' "+ 
				" AND categoria NOT LIKE 'G%') and temp1.sexo='F' THEN 1 ELSE 0 END) AS h_fem, "+
				" SUM(case when temp1.nivel IN(11,12,13,14,15) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup1, "+
				" SUM(case when temp1.nivel IN(21,22,23,24,25,27,28) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup2, "+
				" SUM(case when temp1.nivel IN(30) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup3, "+
				" SUM(case when temp1.nivel IN(40) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup4 "+
				" FROM ( "+
				" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad , tmp1.categoria "+
				" FROM remuneracion.remuneraciones_tmp tmp1 "+
				" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+
				" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+
				" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+
				" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+
				" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+
				" WHERE obj_codigo=111 "+
				" AND (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+
				" ) AS temp1 "+
				" INNER JOIN ( "+
				" SELECT MAX(anho) as anho,MAX(mes) as mes,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+
				" GROUP BY nivel,entidad,oee ) AS temp2 "+
				" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+
				" where 1=1 ";
					if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
						sql+="	and temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
					if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
						sql+="	and temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
						sql+="	and temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
					if(anho !=null)
						sql+="	and temp1.anho = "+anho;
					if(mes !=null)
						sql+="	and temp1.mes = "+mes;
					sql+= "	GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
					"	order BY temp1.nivel,temp1.entidad,temp1.oee) tmp ";
		return sql;
	}
	public void imprimirEdad() throws Exception {
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerParametrosEdad();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
					JasperReportUtils.respondXLS(
						"RPT_CANT_EDAD", param,
						conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
	private HashMap<String, Object> obtenerParametrosEdad() throws Exception {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("oeeUsuarioLogueado", usuarioLogueado.getCodigoUsuario());
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("sql",consultaEdad() );

		return param;
	}

	
	private String consultaEdad(){
		String sql="";
	
		sql  =  " SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad, "+
				" a_mas,b_mas,c_mas,d_mas,e_mas,f_mas,g_mas,h_mas, "+
				" a_fem,b_fem,c_fem,d_fem,d_fem,e_fem,f_fem,g_fem,h_fem, "+
				" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas) as mas_total, "+
				" (a_fem + b_fem + c_fem + d_fem + d_fem + e_fem + f_fem + g_fem + h_fem) as fem_total, "+
				" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas + "+
				" a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) total_cant, "+
				" case when nivel IN(11,12,13,14,15) THEN total_agrup1 "+
				" when nivel IN(21,22,23,24,25,27,28) THEN total_agrup2 "+
				" when nivel IN(30) THEN total_agrup3 "+
				" when nivel IN(40) THEN total_agrup4 "+
				" else 0 end as total_agrup, "+
				" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+
				" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+
				" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+
				" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+
				" else '' end as agrup, "+
				" SUM(total_agrup1 +total_agrup2+total_agrup3+total_agrup4) OVER() as total_sum, "+
				" SUM(cast(a_mas as integer) +cast(b_mas as integer)+cast(c_mas as integer)+cast(d_mas as integer)+cast(e_mas as integer) "+
				" +cast(f_mas as integer)+cast(g_mas as integer)+cast(h_mas as integer)) OVER() as total_mas_sum, "+
				" SUM(cast(a_fem as integer) +cast(b_fem as integer)+cast(c_fem as integer)+cast(d_fem as integer)+cast(e_fem as integer) "+
				" +cast(f_fem as integer)+cast(g_fem as integer)+cast(h_fem as integer)) OVER() as total_fem_sum, "+
				" SUM(a_mas) OVER() as a_mas_sum, "+
				" SUM(b_mas) OVER() as b_mas_sum, "+
				" SUM(c_mas) OVER() as c_mas_sum, "+
				" SUM(d_mas) OVER() as d_mas_sum, "+
				" SUM(e_mas) OVER() as e_mas_sum, "+
				" SUM(f_mas) OVER() as f_mas_sum, "+
				" SUM(g_mas) OVER() as g_mas_sum, "+
				" SUM(h_mas) OVER() as h_mas_sum, "+
				" SUM(a_fem) OVER() as a_fem_sum, "+
				" SUM(b_fem) OVER() as b_fem_sum, "+
				" SUM(c_fem) OVER() as c_fem_sum, "+
				" SUM(d_fem) OVER() as d_fem_sum, "+
				" SUM(e_fem) OVER() as e_fem_sum, "+
				" SUM(f_fem) OVER() as f_fem_sum, "+
				" SUM(g_fem) OVER() as g_fem_sum, "+
				" SUM(h_fem) OVER() as h_fem_sum "+
				" FROM (	SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc, "+
				" SUM(case when edad < 18 and temp1.sexo='M' THEN 1 ELSE 0 END) AS a_mas, "+
				" SUM(case when (edad BETWEEN 18 and 20) and temp1.sexo='M' THEN 1 ELSE 0 END) AS b_mas, "+
				" SUM(case when (edad BETWEEN 21 and 30) and temp1.sexo='M' THEN 1 ELSE 0 END) AS c_mas, "+
				" SUM(case when (edad BETWEEN 31 and 40) and temp1.sexo='M' THEN 1 ELSE 0 END) AS d_mas, "+
				" SUM(case when (edad BETWEEN 41 and 50) and temp1.sexo='M' THEN 1 ELSE 0 END) AS e_mas, "+
				" SUM(case when (edad BETWEEN 51 and 60) and temp1.sexo='M' THEN 1 ELSE 0 END) AS f_mas, "+
				" SUM(case when (edad BETWEEN 61 and 70) and temp1.sexo='M' THEN 1 ELSE 0 END) AS g_mas, "+
				" SUM(case when  edad > 70 and temp1.sexo='M' THEN 1 ELSE 0 END) AS h_mas, "+
				" SUM(case when edad < 18 and temp1.sexo='F' THEN 1 ELSE 0 END) AS a_fem, "+
				" SUM(case when (edad BETWEEN 18 and 20) and temp1.sexo='F' THEN 1 ELSE 0 END) AS b_fem, "+
				" SUM(case when (edad BETWEEN 21 and 30) and temp1.sexo='F' THEN 1 ELSE 0 END) AS c_fem, "+
				" SUM(case when (edad BETWEEN 31 and 40) and temp1.sexo='F' THEN 1 ELSE 0 END) AS d_fem, "+
				" SUM(case when (edad BETWEEN 41 and 50) and temp1.sexo='F' THEN 1 ELSE 0 END) AS e_fem, "+
				" SUM(case when (edad BETWEEN 51 and 60) and temp1.sexo='F' THEN 1 ELSE 0 END) AS f_fem, "+
				" SUM(case when (edad BETWEEN 61 and 70) and temp1.sexo='F' THEN 1 ELSE 0 END) AS g_fem, "+
				" SUM(case when  edad > 70 and temp1.sexo='F' THEN 1 ELSE 0 END) AS h_fem, "+
				" SUM(case when temp1.nivel IN(11,12,13,14,15) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup1, "+
				" SUM(case when temp1.nivel IN(21,22,23,24,25,27,28) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup2, "+
				" SUM(case when temp1.nivel IN(30) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup3, "+
				" SUM(case when temp1.nivel IN(40) AND temp1.sexo IS NOT NULL THEN 1 ELSE 0 END) AS total_agrup4 "+
				" FROM ( "+
				" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad, "+
				" date_part('year',age(per.fecha_nacimiento)) AS edad "+
				" FROM remuneracion.remuneraciones_tmp tmp1 "+
				" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+
				" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+
				" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+
				" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+
				" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+
				" WHERE obj_codigo=111 "+
				" AND (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+
				" ) AS temp1 "+
				" INNER JOIN ( "+
				" SELECT MAX(anho) as anho,MAX(mes) as mes,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+
				" GROUP BY nivel,entidad,oee ) AS temp2 "+
				" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+
				" where 1=1 ";
					if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
						sql+="	and temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
					if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
						sql+="	and temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
						sql+="	and temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
					if(anho !=null)
						sql+="	and temp1.anho = "+anho;
					if(mes !=null)
						sql+="	and temp1.mes = "+mes;
					sql+= "	GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
					"	order BY temp1.nivel,temp1.entidad,temp1.oee) tmp ";
		return sql;
	}
	public void imprimirFecha(Boolean pcd) throws Exception {
		if (anho == null || anhoHasta == null){
			statusMessages.add(Severity.ERROR,"Año no seleccionado.");
			return;
		}
		if (mes == null || mesHasta == null){
			statusMessages.add(Severity.ERROR,"Mes no seleccionado.");
			return;
		}
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerParametrosFecha(pcd);
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			if(pcd){
			param.put("REPORT_CONNECTION", conn);
					JasperReportUtils.respondXLS(
						"RPT_CANT_PCD_FECHA", param,
						conn, usuarioLogueado);
			}else{
			param.put("REPORT_CONNECTION", conn);
					JasperReportUtils.respondXLS(
						"RPT_CANT_FECHA", param,
						conn, usuarioLogueado);
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
	private HashMap<String, Object> obtenerParametrosFecha(Boolean pcd) throws Exception {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("oeeUsuarioLogueado", usuarioLogueado.getCodigoUsuario());
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("sql",consultaFecha(pcd) );

		return param;
	}

	
	private String consultaFecha(Boolean pcd){
		String sql="";
	
		sql  =  " SELECT nivel,entidad,oee,denominacion_unidad, nivel_desc, alta_perm, baja_perm, "+
				" (alta_perm - baja_perm) as diff_perm, alta_contrat, baja_contrat, "+
				" (alta_contrat - baja_contrat) as diff_contrat, "+
				" ((alta_perm - baja_perm)+(alta_contrat - baja_contrat)) as total_cant, agrup, "+
				" SUM(alta_perm) OVER() as alta_perm_sum, "+
				" SUM(baja_perm) OVER() as baja_perm_sum, "+
				" SUM(alta_perm - baja_perm) OVER() as diff_perm_sum, "+
				" SUM(alta_contrat) OVER() as alta_contrat_sum, "+
				" SUM(baja_contrat) OVER() as baja_contrat_sum, "+
				" SUM(alta_contrat - baja_contrat) OVER() as diff_contrat_sum, "+
				" SUM((alta_perm - baja_perm) + (alta_contrat - baja_contrat)) OVER() as total_sum "+
				" FROM ( "+
				" SELECT nivel,entidad,oee,cuc.denominacion_unidad, sne.nen_nombre as nivel_desc, ";
				if(pcd){
				sql+= " SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'A' AND discapacidad is true THEN 1 ELSE 0 END) AS alta_perm, "+
				" SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'B' AND discapacidad is true THEN 1 ELSE 0 END) AS baja_perm, "+
				" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'A' AND discapacidad is true THEN 1 ELSE 0 END) AS alta_contrat, "+
				" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'B' AND discapacidad is true THEN 1 ELSE 0 END) AS baja_contrat, ";
				}else{
				sql+= " SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'A' THEN 1 ELSE 0 END) AS alta_perm, "+
				" SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'B' THEN 1 ELSE 0 END) AS baja_perm, "+
				" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'A' THEN 1 ELSE 0 END) AS alta_contrat, "+
				" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'B' THEN 1 ELSE 0 END) AS baja_contrat, ";
				}
				sql+= " case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+
				" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+
				" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+
				" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+
				" else '' end as agrup "+
				" FROM ( "+
				" SELECT DISTINCT nivel,entidad,oee,documento_identidad,estado,upper(movimiento) as movimiento, discapacidad "+
				" FROM ( "+
				" SELECT DISTINCT nivel,entidad,oee,documento_identidad,estado,upper(movimiento) as movimiento, discapacidad "+
				" FROM remuneracion.remuneraciones_tmp tmp1 "+
				" WHERE "+
				" (obj_codigo=111 OR  obj_codigo BETWEEN 140 AND 149) AND tmp1.documento_identidad <> '0' "+
				" AND (tmp1.anho BETWEEN "+anho +" AND "+anhoHasta +" AND tmp1.mes BETWEEN "+mes+ " AND " + mesHasta +")";
				if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
					sql+="	AND tmp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
				if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
					sql+="	AND tmp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
				if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
					sql+="	AND tmp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
				sql+= " UNION ALL "+
				" SELECT DISTINCT nivel,entidad,oee,documento_identidad,estado,upper(movimiento) as movimiento, discapacidad "+
				" FROM remuneracion.historico_remuneraciones_tmp tmp1 "+
				" WHERE "+
				" (obj_codigo=111 OR  obj_codigo BETWEEN 140 AND 149) AND tmp1.documento_identidad <> '0' "+
				" AND (tmp1.anho BETWEEN "+anho +" AND "+anhoHasta +" AND tmp1.mes BETWEEN "+mes+ " AND " + mesHasta +")";
				if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
					sql+="	AND tmp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
				if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
					sql+="	AND tmp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
				if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
					sql+="	AND tmp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
				sql+= " ) as temp1 "+
				" order BY nivel,entidad,oee "+
				" ) AS t1 "+
				" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = t1.nivel "+
				" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = t1.entidad "+
				" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+
				" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = t1.oee "+
				" GROUP BY nivel,entidad,oee,cuc.denominacion_unidad, sne.nen_nombre "+
				" order BY nivel,entidad,oee "+
				" ) as t2 "+
				" order BY nivel,entidad,oee ";
		return sql;
	}
	
	//ZD 11/02/16 --Descargar informe cumplimiento en formato .csv
	@SuppressWarnings("unchecked")
	public void descargarInforme() throws IOException, ParseException {
		String cabeceras = "";
		Query q = null;
		ArrayList<String> listaLineas = new ArrayList<String>();
		if (anho == null){
			anho = 0;
			statusMessages.add(Severity.ERROR,"Año no seleccionado.");
			return;
		}
		if (mes == null){
			mes = 0;
			statusMessages.add(Severity.ERROR,"Mes no seleccionado.");
			return;
		}
		try {
			
			conn = JpaResourceBean.getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(" select remuneracion.informe_cumplimiento("+anho+", "+mes+")");
			stmt.close();
			conn.close();
			
			String nArchivo = "Monitoreo_Ley_5189_2014"+ ".csv";
			File archSalida = new File(nArchivo);
			listaLineas.add(";;;;;;;PLANILLA DE VERIFICACION PARA MONITOREO DE LA LEY 5189/2014");
			listaLineas.add("");
			listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
			listaLineas.add("");
			q = em.createNativeQuery("select cast(column_name as text) from information_schema.columns"
									  +" where table_name = 'informe_cumplimiento'");

			for (Object item : q.getResultList()) {
				cabeceras += ((String) item).toString() +";";
			}
			cabeceras = cabeceras.substring(0,cabeceras.length()-1);
			listaLineas.add(cabeceras);
			String sql = "select * from remuneracion.informe_cumplimiento WHERE 1=1 ";
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
				sql+="	AND nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
			if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
				sql+="	AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
			if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
				sql+="	AND oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
			sql+="	ORDER BY nivel, entidad, oee ";
			q = em.createNativeQuery(sql);
			List<Object[]> listaInforme = q.getResultList();
			if(listaInforme.size() > 0){
				for (Object[] items : listaInforme) {
					String add="";
					for(int i= 0;i<items.length;i++)
						if(((Object) items[i]) == null)
							add += ";";
						else
							add += ((Object) items[i])+";";
					listaLineas.add(add.toString().substring(0,add.toString().length()-1));
				}
			}else{
				statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
				return;
			}
				FileUtils.writeLines(archSalida, listaLineas);
				JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
				archSalida.delete();
		
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
			System.out.println(e.getMessage());
		}
	}
	
	//ZD 10/03/16 --Descargar cantidad de funcionarios permanentes y contratados por Sexo en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCantFunc(Boolean pcd) throws IOException, ParseException {
			String cabeceras = "";
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "";
				if(pcd)
					nArchivo = "CantFuncPCDPorSexo"+ ".csv";
				else
					nArchivo =  "CantFuncPorSexo"+ ".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;;;Resultados - Cantidad de Funcionarios por Sexo");
				listaLineas.add("");
				listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "ANHO;MES;NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;PERM_MAS;PERM_FEM;PERM_TOTAL;CONTRAT_MAS;CONTRAT_FEM;CONTRAT_TOTAL;TOTAL_CANT;AGRUP";
				listaLineas.add(cabeceras);
				String sql = " SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad,perm_mas,perm_fem,"+
						" (perm_mas + perm_fem) perm_total, contrat_mas, contrat_fem, "+
						" (contrat_mas + contrat_fem) as contrat_total,(perm_mas + perm_fem + contrat_mas + contrat_fem) as total_cant, "+ 
						" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+ 
						" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+ 
						" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+ 
						" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+ 
						" else '' end as agrup "+
						" FROM (SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc,";
						if(pcd){
						sql+= " SUM(case when obj_codigo=111 and temp1.sexo='M' and discapacidad is true THEN 1 ELSE 0 END) AS perm_mas, "+ 
						"  SUM(case when obj_codigo=111 and temp1.sexo='F' and discapacidad is true THEN 1 ELSE 0 END) AS perm_fem, "+ 
						"  SUM(case when obj_codigo!=111 and temp1.sexo='M' and discapacidad is true THEN 1 ELSE 0 END) AS contrat_mas, "+ 
						"  SUM(case when obj_codigo!=111 and temp1.sexo='F' and discapacidad is true THEN 1 ELSE 0 END) AS contrat_fem ";
						}else{
						sql+= "  SUM(case when obj_codigo=111 and temp1.sexo='M' THEN 1 ELSE 0 END) AS perm_mas, "+ 
						"  SUM(case when obj_codigo=111 and temp1.sexo='F' THEN 1 ELSE 0 END) AS perm_fem, "+ 
						"  SUM(case when obj_codigo!=111 and temp1.sexo='M' THEN 1 ELSE 0 END) AS contrat_mas, "+ 
						"  SUM(case when obj_codigo!=111 and temp1.sexo='F' THEN 1 ELSE 0 END) AS contrat_fem";
						}
						sql+= " FROM ( "+
						" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad "+ 
						" FROM remuneracion.remuneraciones_tmp tmp1 "+  
						" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+  
						" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+  
						" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+  
						" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+  
						" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+  
						" WHERE (obj_codigo=111 OR  obj_codigo BETWEEN 140 AND 149) AND "+  
						" (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0') AS temp1 "+  
						" INNER JOIN ( "+ 
						" SELECT MAX(anho) as anho, MAX(mes) as mes ,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+  
						" GROUP BY nivel,entidad,oee )AS temp2 "+  
						" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+  
						"  where 1=1 "; 
						if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
							sql+="	AND temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
						if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
							sql+="	AND temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
						if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
							sql+="	AND temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
						sql+=" GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
						" order BY temp1.nivel,temp1.entidad,temp1.oee) tmp";
				q = em.createNativeQuery(sql);
				List<Object[]> rpt = q.getResultList();
				if(rpt.size() > 0){
					for (Object[] items : rpt) {
						String add="";
						for(int i= 0;i<items.length;i++)
							add += ((Object) items[i])+";";
						listaLineas.add(add.toString().substring(0,add.toString().length()-1));
					}
				}else{
					statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
					return;
				}
				FileUtils.writeLines(archSalida, listaLineas);
				JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
				archSalida.delete();
			
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
				System.out.println(e.getMessage());
			}
		}
	//ZD 10/03/16 --Descargar cantidad de funcionarios permanentes por Edad en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCantFuncEdad() throws IOException, ParseException {
			String cabeceras = "";
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "CantFuncPorEdad"+ ".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;;;Resultados - Cantidad de Funcionarios por Edad");
				listaLineas.add("");
				listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "ANHO;MES;NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;A_MAS;B_MAS;C_MAS;D_MAS;E_MAS;F_MAS;G_MAS;H_MAS;"
						+ "A_FEM;B_FEM;C_FEM;D_FEM;E_FEM;F_FEM;G_FEM;H_FEM;MAS_TOTAL;FEM_TOTAL;TOTAL_CANT;AGRUP";
				listaLineas.add(cabeceras);
				String sql = " SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad, "+ 
						" a_mas,b_mas,c_mas,d_mas,e_mas,f_mas,g_mas,h_mas, "+ 
						" a_fem,b_fem,c_fem,d_fem,e_fem,f_fem,g_fem,h_fem, "+ 
						" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas) as mas_total, "+ 
						" (a_fem + b_fem + c_fem + d_fem + d_fem + e_fem + f_fem + g_fem + h_fem) as fem_total, "+ 
						" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas + "+ 
						" a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) total_cant, "+  
						" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+ 
						" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+ 
						" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+ 
						" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+ 
						" else '' end as agrup "+
						" FROM (	SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc, "+ 
						" SUM(case when edad < 18 and temp1.sexo='M' THEN 1 ELSE 0 END) AS a_mas, "+ 
						" SUM(case when (edad BETWEEN 18 and 20) and temp1.sexo='M' THEN 1 ELSE 0 END) AS b_mas, "+ 
						" SUM(case when (edad BETWEEN 21 and 30) and temp1.sexo='M' THEN 1 ELSE 0 END) AS c_mas, "+ 
						" SUM(case when (edad BETWEEN 31 and 40) and temp1.sexo='M' THEN 1 ELSE 0 END) AS d_mas, "+ 
						" SUM(case when (edad BETWEEN 41 and 50) and temp1.sexo='M' THEN 1 ELSE 0 END) AS e_mas, "+ 
						" SUM(case when (edad BETWEEN 51 and 60) and temp1.sexo='M' THEN 1 ELSE 0 END) AS f_mas, "+ 
						" SUM(case when (edad BETWEEN 61 and 70) and temp1.sexo='M' THEN 1 ELSE 0 END) AS g_mas, "+ 
						" SUM(case when  edad > 70 and temp1.sexo='M' THEN 1 ELSE 0 END) AS h_mas, "+ 
						" SUM(case when edad < 18 and temp1.sexo='F' THEN 1 ELSE 0 END) AS a_fem, "+ 
						" SUM(case when (edad BETWEEN 18 and 20) and temp1.sexo='F' THEN 1 ELSE 0 END) AS b_fem, "+ 
						" SUM(case when (edad BETWEEN 21 and 30) and temp1.sexo='F' THEN 1 ELSE 0 END) AS c_fem, "+ 
						" SUM(case when (edad BETWEEN 31 and 40) and temp1.sexo='F' THEN 1 ELSE 0 END) AS d_fem, "+ 
						" SUM(case when (edad BETWEEN 41 and 50) and temp1.sexo='F' THEN 1 ELSE 0 END) AS e_fem, "+ 
						" SUM(case when (edad BETWEEN 51 and 60) and temp1.sexo='F' THEN 1 ELSE 0 END) AS f_fem, "+ 
						" SUM(case when (edad BETWEEN 61 and 70) and temp1.sexo='F' THEN 1 ELSE 0 END) AS g_fem, "+ 
						" SUM(case when  edad > 70 and temp1.sexo='F' THEN 1 ELSE 0 END) AS h_fem "+ 
						" FROM ( "+ 
						" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad, "+ 
						" date_part('year',age(per.fecha_nacimiento)) AS edad "+ 
						" FROM remuneracion.remuneraciones_tmp tmp1 "+ 
						" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+ 
						" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+ 
						" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+ 
						" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+ 
						" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+ 
						" WHERE obj_codigo=111 "+
						" AND (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+ 
						" ) AS temp1  "+  
						" INNER JOIN ( "+ 
						" SELECT MAX(anho) as anho, MAX(mes) as mes ,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+  
						" GROUP BY nivel,entidad,oee )AS temp2 "+  
						" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+  
						"  where 1=1 "; 
						if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
							sql+="	AND temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
						if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
							sql+="	AND temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
						if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
							sql+="	AND temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
						sql+=" GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
						" order BY temp1.nivel,temp1.entidad,temp1.oee) tmp";
				q = em.createNativeQuery(sql);
				List<Object[]> rpt = q.getResultList();
				if(rpt.size() > 0){
					for (Object[] items : rpt) {
						String add="";
						for(int i= 0;i<items.length;i++)
							add += ((Object) items[i])+";";
						listaLineas.add(add.toString().substring(0,add.toString().length()-1));
					}
				}else{
					statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
					return;
				}
					listaLineas.add("REFERENCIAS");
					listaLineas.add("NIVEL;RANGO_EDAD");
					listaLineas.add("A;<18");
					listaLineas.add("B;18-20");
					listaLineas.add("C;21-30");
					listaLineas.add("D;31-40");
					listaLineas.add("E;41-50");
					listaLineas.add("F;51-60");
					listaLineas.add("G;61-70");
					listaLineas.add("H;>70");
				FileUtils.writeLines(archSalida, listaLineas);
				JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
				archSalida.delete();
			
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
				System.out.println(e.getMessage());
			}
		}
		//ZD 10/03/16 --Descargar cantidad de funcionarios permanentes por Nivel Salarial en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCantFuncNivel() throws IOException, ParseException {
			String cabeceras = "";
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "CantFuncPorNivelSalarial"+ ".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;;;Resultados - Cantidad de Funcionarios por Nivel Salarial");
				listaLineas.add("");
				listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "ANHO;MES;NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;A_MAS;B_MAS;C_MAS;D_MAS;E_MAS;F_MAS;G_MAS;H_MAS;"
						+ "A_FEM;B_FEM;C_FEM;D_FEM;E_FEM;F_FEM;G_FEM;H_FEM;MAS_TOTAL;FEM_TOTAL;TOTAL_CANT;AGRUP";
				listaLineas.add(cabeceras);
				String sql = " SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad, "+ 
						" a_mas,b_mas,c_mas,d_mas,e_mas,f_mas,g_mas,h_mas, "+ 
						" a_fem,b_fem,c_fem,d_fem,e_fem,f_fem,g_fem,h_fem, "+ 
						" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas) as mas_total, "+ 
						" (a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) as fem_total, "+ 
						" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas + "+ 
						" a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) total_cant, "+ 
						" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+ 
						" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+ 
						" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+ 
						" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+ 
						" else '' end as agrup "+
						" FROM (SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 0 and 1824054 then 1 else 0 end) AS a_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 1824055 and 2499999 then 1 else 0 end) AS b_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 2500000 and 3999999 then 1 else 0 end) AS c_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 4000000 and 5999999 then 1 else 0 end) AS d_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 6000000 and 7999999 then 1 else 0 end) AS e_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 8000000 and 9999999 then 1 else 0 end) AS f_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado between 10000000 and 14999999 then 1 else 0 end) AS g_mas, "+ 
						" SUM(case when temp1.sexo='M' and temp1.presupuestado >= 15000000 then 1 else 0 end) AS h_mas, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 0 and 1824054 then 1 else 0 end) AS a_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 1824055 and 2499999 then 1 else 0 end) AS b_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 2500000 and 3999999 then 1 else 0 end) AS c_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 4000000 and 5999999 then 1 else 0 end) AS d_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 6000000 and 7999999 then 1 else 0 end) AS e_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 8000000 and 9999999 then 1 else 0 end) AS f_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado between 10000000 and 14999999 then 1 else 0 end) AS g_fem, "+ 
						" SUM(case when temp1.sexo='F' and temp1.presupuestado >= 15000000 then 1 else 0 end) AS h_fem "+
						" FROM ( "+ 
						" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad , tmp1.presupuestado "+ 
						" FROM remuneracion.remuneraciones_tmp tmp1 "+ 
						" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+ 
						" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+ 
						" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+ 
						" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+ 
						" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+ 
						" WHERE obj_codigo=111 "+ 
						" AND (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+ 
						" ) AS temp1 "+  
						" INNER JOIN ( "+ 
						" SELECT MAX(anho) as anho, MAX(mes) as mes ,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+  
						" GROUP BY nivel,entidad,oee )AS temp2 "+  
						" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+  
						"  where 1=1 "; 
						if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
							sql+="	AND temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
						if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
							sql+="	AND temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
						if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
							sql+="	AND temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
						sql+=" GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
						" order BY temp1.nivel,temp1.entidad,temp1.oee) tmp";
				q = em.createNativeQuery(sql);
				List<Object[]> rpt = q.getResultList();
				if(rpt.size() > 0){
					for (Object[] items : rpt) {
						String add="";
						for(int i= 0;i<items.length;i++)
							add += ((Object) items[i])+";";
						listaLineas.add(add.toString().substring(0,add.toString().length()-1));
					}
				}else{
					statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
					return;
				}
					listaLineas.add("REFERENCIAS");
					listaLineas.add("NIVEL;DESDE;;HASTA");
					listaLineas.add("A;0;a menos de;1.824.055");
					listaLineas.add("B;1.824.055;a menos de;2.500.000");
					listaLineas.add("C;2.500.000;a menos de;4.000.000");
					listaLineas.add("D;4.000.000;a menos de;6.000.000");
					listaLineas.add("E;6.000.000;a menos de;8.000.000");
					listaLineas.add("F;8.000.000;a menos de;10.000.000");
					listaLineas.add("G;10.000.000;a menos de;15.000.000");
					listaLineas.add("H;15.000.000;a menos de;");
				FileUtils.writeLines(archSalida, listaLineas);
				JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
				archSalida.delete();
			
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
				System.out.println(e.getMessage());
			}
		}
	//ZD 10/03/16 --Descargar cantidad de funcionarios permanentes por Categoria en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCantFuncCat() throws IOException, ParseException {
			String cabeceras = "";
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "CantFuncPorCategoria"+ ".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;;;Resultados - Cantidad de Funcionarios Permanentes por Categoría");
				listaLineas.add("");
				listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "ANHO;MES;NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;A_MAS;B_MAS;C_MAS;D_MAS;E_MAS;F_MAS;G_MAS;OTROS;"
						+ "A_FEM;B_FEM;C_FEM;D_FEM;E_FEM;F_FEM;G_FEM;OTROS;MAS_TOTAL;FEM_TOTAL;TOTAL_CANT;AGRUP";
				listaLineas.add(cabeceras);
				String sql = " SELECT anho,mes,nivel,nivel_desc,entidad,oee,denominacion_unidad, "+ 
						" a_mas,b_mas,c_mas,d_mas,e_mas,f_mas,g_mas,h_mas, "+ 
						" a_fem,b_fem,c_fem,d_fem,e_fem,f_fem,g_fem,h_fem, "+ 
						" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas) as mas_total, "+
						" (a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) as fem_total, "+ 
						" (a_mas + b_mas + c_mas + d_mas + e_mas + f_mas + g_mas + h_mas + "+ 
						" a_fem + b_fem + c_fem + d_fem + e_fem + f_fem + g_fem + h_fem) total_cant, "+ 
						" case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+ 
						" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+ 
						" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+ 
						" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+ 
						" else '' end as agrup "+
						" FROM (	SELECT temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad, nivel_desc, "+ 
						" SUM(case when categoria LIKE 'A%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS a_mas, "+ 
						" SUM(case when categoria LIKE 'B%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS b_mas, "+ 
						" SUM(case when categoria LIKE 'C%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS c_mas, "+ 
						" SUM(case when categoria LIKE 'D%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS d_mas, "+ 
						" SUM(case when categoria LIKE 'E%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS e_mas, "+ 
						" SUM(case when categoria LIKE 'F%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS f_mas, "+  
						" SUM(case when categoria LIKE 'G%' and temp1.sexo='M' THEN 1 ELSE 0 END) AS g_mas, "+ 
						" SUM(case when (categoria NOT LIKE 'A%' AND categoria NOT LIKE 'B%' AND categoria NOT LIKE 'C%' "+  
						" AND categoria NOT LIKE 'D%' AND categoria NOT LIKE 'E%' AND categoria NOT LIKE 'F%' "+ 
						" AND categoria NOT LIKE 'G%') and temp1.sexo='M' THEN 1 ELSE 0 END) AS h_mas, "+ 
						" SUM(case when categoria LIKE 'A%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS a_fem, "+ 
						" SUM(case when categoria LIKE 'B%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS b_fem, "+ 
						" SUM(case when categoria LIKE 'C%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS c_fem, "+ 
						" SUM(case when categoria LIKE 'D%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS d_fem, "+ 
						" SUM(case when categoria LIKE 'E%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS e_fem, "+ 
						" SUM(case when categoria LIKE 'F%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS f_fem, "+ 
						" SUM(case when categoria LIKE 'G%' and temp1.sexo='F' THEN 1 ELSE 0 END) AS g_fem, "+ 
						" SUM(case when (categoria NOT LIKE 'A%' AND categoria NOT LIKE 'B%' AND categoria NOT LIKE 'C%' "+  
						" AND categoria NOT LIKE 'D%' AND categoria NOT LIKE 'E%' AND categoria NOT LIKE 'F%' "+  
						" AND categoria NOT LIKE 'G%') and temp1.sexo='F' THEN 1 ELSE 0 END) AS h_fem "+
						" FROM ( "+ 
						" SELECT DISTINCT tmp1.documento_identidad,tmp1.anho,tmp1.mes,nivel,entidad,oee,cuc.denominacion_unidad,obj_codigo,sexo, sne.nen_nombre as nivel_desc, tmp1.discapacidad , tmp1.categoria "+ 
						" FROM remuneracion.remuneraciones_tmp tmp1 "+ 
						" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = tmp1.nivel "+ 
						" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = tmp1.entidad "+ 
						" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+ 
						" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = tmp1.oee "+ 
						" INNER JOIN general.persona per ON tmp1.documento_identidad = per.documento_identidad "+ 
						" WHERE obj_codigo=111 "+
						" AND (movimiento NOT LIKE 'B' OR movimiento IS NULL) AND tmp1.documento_identidad <> '0' "+ 
						" ) AS temp1 "+  
						" INNER JOIN ( "+ 
						" SELECT MAX(anho) as anho, MAX(mes) as mes ,nivel,entidad,oee FROM remuneracion.remuneraciones_tmp "+  
						" GROUP BY nivel,entidad,oee )AS temp2 "+  
						" ON temp1.nivel = temp2.nivel AND temp1.entidad = temp2.entidad AND temp1.oee = temp2.oee AND temp1.anho = temp2.anho AND temp1.mes = temp2.mes "+  
						"  where 1=1 "; 
						if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
							sql+="	AND temp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
						if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
							sql+="	AND temp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
						if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
							sql+="	AND temp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
						sql+=" GROUP BY temp1.anho,temp1.mes,temp1.nivel,temp1.entidad,temp1.oee,denominacion_unidad,nivel_desc "+
						" order BY temp1.nivel,temp1.entidad,temp1.oee) tmp";
				q = em.createNativeQuery(sql);
				List<Object[]> rpt = q.getResultList();
				if(rpt.size() > 0){
					for (Object[] items : rpt) {
						String add="";
						for(int i= 0;i<items.length;i++)
							add += ((Object) items[i])+";";
						listaLineas.add(add.toString().substring(0,add.toString().length()-1));
					}
				}else{
					statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
					return;
				}
				FileUtils.writeLines(archSalida, listaLineas);
				JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
				archSalida.delete();
			
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
				System.out.println(e.getMessage());
			}
		}
		
		//ZD 30/03/16 --Descargar cantidad de Altas y Bajas por Rango de Fecha en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCantAltasyBajas(Boolean pcd) throws IOException, ParseException {
			if (anho == null || anhoHasta == null){
				statusMessages.add(Severity.ERROR,"Año no seleccionado.");
				return;
			}
			if (mes == null || mesHasta == null){
				statusMessages.add(Severity.ERROR,"Mes no seleccionado.");
				return;
			}
			String cabeceras = "";
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "";
				if(pcd)
					nArchivo = "CantAltasyBajasPCD"+".csv";
				else
					nArchivo = "CantAltasyBajas"+".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;Resultados - Cantidad de Funcionarios DE "+mes+"/"+anho+" A "+mesHasta+"/"+anhoHasta);
				listaLineas.add("");
				listaLineas.add(";;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;ALTA_PERM;BAJA_PERM;DIFF_PERM;ALTA_CONTRAT;BAJA_CONTRAT;DIFF_CONTRAT;TOTAL_CANT;AGRUP";
				listaLineas.add(cabeceras);
		
				String sql  =  " SELECT nivel, nivel_desc,entidad,oee,denominacion_unidad, alta_perm, baja_perm, "+
					" (alta_perm - baja_perm) as diff_perm, alta_contrat, baja_contrat, "+
					" (alta_contrat - baja_contrat) as diff_contrat, "+
					" ((alta_perm - baja_perm)+(alta_contrat - baja_contrat)) as total_cant, agrup "+
					" FROM ( "+
					" SELECT nivel,entidad,oee,cuc.denominacion_unidad, sne.nen_nombre as nivel_desc, ";
					if(pcd){
					sql+= " SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'A' AND discapacidad is true THEN 1 ELSE 0 END) AS alta_perm, "+
					" SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'B' AND discapacidad is true THEN 1 ELSE 0 END) AS baja_perm, "+
					" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'A' AND discapacidad is true THEN 1 ELSE 0 END) AS alta_contrat, "+
					" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'B' AND discapacidad is true THEN 1 ELSE 0 END) AS baja_contrat, ";
					}else{
					sql+= " SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'A' THEN 1 ELSE 0 END) AS alta_perm, "+
					" SUM(case when estado LIKE 'PERMANENTE' AND movimiento LIKE 'B' THEN 1 ELSE 0 END) AS baja_perm, "+
					" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'A' THEN 1 ELSE 0 END) AS alta_contrat, "+
					" SUM(case when estado LIKE 'CONTRATADO' AND movimiento LIKE 'B' THEN 1 ELSE 0 END) AS baja_contrat, ";
					}
					sql+= " case when nivel IN(11,12,13,14,15) THEN 'I - ADMINISTRACION CENTRAL' "+
					" when nivel IN(21,22,23,24,25,27,28) THEN 'II - ADMINISTRACION DESCENTRALIZADA' "+
					" when nivel IN(30) THEN 'III - MUNICIPALIDADES' "+
					" when nivel IN(40) THEN 'IV - SOCIEDADES ANÓNIMAS' "+
					" else '' end as agrup "+
					" FROM ( "+
					" SELECT DISTINCT nivel,entidad,oee,documento_identidad,estado,upper(movimiento) as movimiento, discapacidad "+
					" FROM ( "+
					" SELECT DISTINCT nivel,entidad,oee,documento_identidad,estado,upper(movimiento) as movimiento, discapacidad "+
					" FROM remuneracion.remuneraciones_tmp tmp1 "+
					" WHERE "+
					" (obj_codigo=111 OR  obj_codigo BETWEEN 140 AND 149) AND tmp1.documento_identidad <> '0' "+
					" AND (tmp1.anho BETWEEN "+anho +" AND "+anhoHasta +" AND tmp1.mes BETWEEN "+mes+ " AND " + mesHasta +")";
					if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
						sql+="	AND tmp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
					if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
						sql+="	AND tmp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
						sql+="	AND tmp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
					sql+= " UNION ALL "+
					" SELECT DISTINCT nivel,entidad,oee,documento_identidad,estado,upper(movimiento) as movimiento, discapacidad "+
					" FROM remuneracion.historico_remuneraciones_tmp tmp1 "+
					" WHERE "+
					" (obj_codigo=111 OR  obj_codigo BETWEEN 140 AND 149) AND tmp1.documento_identidad <> '0' "+
					" AND (tmp1.anho BETWEEN "+anho +" AND "+anhoHasta +" AND tmp1.mes BETWEEN "+mes+ " AND " + mesHasta +")";
					if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
						sql+="	AND tmp1.nivel =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
					if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
						sql+="	AND tmp1.entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
						sql+="	AND tmp1.oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
					sql+= " ) as temp1 "+
					" order BY nivel,entidad,oee "+
					" ) AS t1 "+
					" INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = t1.nivel "+
					" INNER JOIN sinarh.sin_entidad se ON se.nen_codigo = sne.nen_codigo AND se.ent_codigo = t1.entidad "+
					" INNER JOIN planificacion.entidad en ON en.id_sin_entidad = se.id_sin_entidad "+
					" INNER JOIN planificacion.configuracion_uo_cab cuc ON cuc.id_configuracion_uo = en.id_configuracion_uo and cuc.orden = t1.oee "+
					" GROUP BY nivel,entidad,oee,cuc.denominacion_unidad, sne.nen_nombre "+
					" order BY nivel,entidad,oee "+
					" ) as t2 "+
					" order BY nivel,entidad,oee ";
					q = em.createNativeQuery(sql);
					List<Object[]> rpt = q.getResultList();
					if(rpt.size() > 0){
						for (Object[] items : rpt) {
							String add="";
							for(int i= 0;i<items.length;i++)
								add += ((Object) items[i])+";";
							listaLineas.add(add.toString().substring(0,add.toString().length()-1));
						}
					}else{
						statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
						return;
					}
					FileUtils.writeLines(archSalida, listaLineas);
					JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
					archSalida.delete();
				
				} catch (Exception e) {
					statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
					System.out.println(e.getMessage());
				}
		}
		//ZD 31/03/16 --Descargar cumplimiento de Altas y Bajas por año en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCumpliAltasyBajas() throws IOException, ParseException {
			if (anho == null){
				statusMessages.add(Severity.ERROR,"Año no seleccionado.");
				return;
			}
			String cabeceras = "";
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "RptInfCumplimientoAyB"+".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;;;INFORME DE CUMPLIMIENTO DE ALTAS Y BAJAS");
				listaLineas.add("");
				listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "NRO;NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;AÑO;ENERO;FEBRERO;MARZO;ABRIL;MAYO;JUNIO;JULIO;AGOSTO;SETIEMBRE;OCTUBRE;NOVIEMBRE;DICIEMBRE";
				listaLineas.add(cabeceras);
				String sql  =  "SELECT DISTINCT sne.nen_codigo as nivel, sne.nen_nombre as nivel_desc , se.ent_codigo as entidad, cuc.orden as oee,cuc.denominacion_unidad,"+anho+","+
							"CASE WHEN ene.mes = 1 THEN 'SI' ELSE 'NO' END as enero, "+
							"CASE WHEN fe.mes = 2 THEN 'SI' ELSE 'NO' END as febrero, "+
							"CASE WHEN mar.mes = 3 THEN 'SI' ELSE 'NO' END as marzo, "+
							"CASE WHEN abr.mes = 4 THEN 'SI' ELSE 'NO' END as abril, "+
							"CASE WHEN may.mes = 5 THEN 'SI' ELSE 'NO' END as mayo, "+
							"CASE WHEN jun.mes = 6 THEN 'SI' ELSE 'NO' END as junio, "+
							"CASE WHEN jul.mes = 7 THEN 'SI' ELSE 'NO' END as julio, "+
							"CASE WHEN ag.mes = 8 THEN 'SI' ELSE 'NO' END as agosto, "+
							"CASE WHEN set.mes = 9 THEN 'SI' ELSE 'NO' END as setiembre, "+
							"CASE WHEN oct.mes = 10 THEN 'SI' ELSE 'NO' END as octubre, "+
							"CASE WHEN nov.mes = 11 THEN 'SI' ELSE 'NO' END as noviembre, "+
							"CASE WHEN dic.mes = 12 THEN 'SI' ELSE 'NO' END as diciembre "+
							"FROM  planificacion.configuracion_uo_cab cuc "+
							"LEFT JOIN remuneracion.procesamiento proc ON cuc.id_configuracion_uo = proc.id_configuracion_uo "+
							"LEFT JOIN remuneracion.procesamiento ene on ene.id_configuracion_uo=proc.id_configuracion_uo AND ene.mes = 1 AND ene.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento fe on fe.id_configuracion_uo=proc.id_configuracion_uo AND fe.mes = 2 AND fe.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento mar on mar.id_configuracion_uo=proc.id_configuracion_uo AND mar.mes = 3 AND mar.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento abr on abr.id_configuracion_uo=proc.id_configuracion_uo AND abr.mes = 4 AND abr.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento may on may.id_configuracion_uo=proc.id_configuracion_uo AND may.mes = 5 AND may.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento jun on jun.id_configuracion_uo=proc.id_configuracion_uo AND jun.mes = 6 AND jun.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento jul on jul.id_configuracion_uo=proc.id_configuracion_uo AND jul.mes = 7 AND jul.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento ag on ag.id_configuracion_uo=proc.id_configuracion_uo AND ag.mes = 8 AND ag.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento set on set.id_configuracion_uo=proc.id_configuracion_uo AND set.mes = 9 AND set.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento oct on oct.id_configuracion_uo=proc.id_configuracion_uo AND oct.mes = 10 AND oct.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento nov on  nov.id_configuracion_uo=proc.id_configuracion_uo AND nov.mes = 11 AND nov.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento dic on dic.id_configuracion_uo=proc.id_configuracion_uo AND dic.mes = 12 AND dic.anho = "+anho+" "+
							"INNER JOIN planificacion.entidad en ON en.id_configuracion_uo = cuc.id_configuracion_uo "+
							"INNER JOIN sinarh.sin_entidad se ON en.id_sin_entidad = se.id_sin_entidad "+
							"INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = se.nen_codigo "+
							"WHERE cuc.activo is true ";
							if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
								sql+="	AND sne.nen_codigo =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
							if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
								sql+="	AND se.ent_codigo = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
							if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
								sql+="	AND cuc.orden = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
							sql+=" ORDER BY sne.nen_codigo, se.ent_codigo, cuc.orden;";
					q = em.createNativeQuery(sql);
					List<Object[]> rpt = q.getResultList();
					int cont = 1;
					if(rpt.size() > 0){
						for (Object[] items : rpt) {
							String add="";
							add = cont+";";
							for(int i= 0;i<items.length;i++)
								add += ((Object) items[i])+";";
							listaLineas.add(add.toString().substring(0,add.toString().length()-1));
							cont++;
						}
					}else{
						statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
						return;
					}
					FileUtils.writeLines(archSalida, listaLineas);
					JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
					archSalida.delete();
				
				} catch (Exception e) {
					statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
					System.out.println(e.getMessage());
				}
		}
		//ZD 31/03/16 --Descargar cumplimiento cantidad de Altas y Bajas por año en formato .csv
		@SuppressWarnings("unchecked")
		public void descargarCumpliCantAltasyBajas() throws IOException, ParseException {
			if (anho == null){
				statusMessages.add(Severity.ERROR,"Año no seleccionado.");
				return;
			}
			String cabeceras = "";
			Query q = null;
			ArrayList<String> listaLineas = new ArrayList<String>();
			try { 	
		
				String nArchivo = "RptInfCumplimientoCantAyB"+".csv";
				File archSalida = new File(nArchivo);
				listaLineas.add(";;;;;;;INFORME DE CUMPLIMIENTO CANTIDAD DE ALTAS Y BAJAS");
				listaLineas.add("");
				listaLineas.add(";;;;;Usuario: "+usuarioLogueado.getCodigoUsuario()+";;;;;Fecha: "+sdf.format(new Date()));
				listaLineas.add("");
				cabeceras = "NRO;NIVEL;NIVEL_DESC;ENTIDAD;OEE;OEE_DESC;AÑO;ENERO;FEBRERO;MARZO;ABRIL;MAYO;JUNIO;JULIO;AGOSTO;SETIEMBRE;OCTUBRE;NOVIEMBRE;DICIEMBRE";
				listaLineas.add(cabeceras);
				String sql  =  "SELECT DISTINCT sne.nen_codigo as nivel, sne.nen_nombre as nivel_desc , se.ent_codigo as entidad, cuc.orden as oee,cuc.denominacion_unidad,"+anho+","+
							"CASE WHEN ene.mes = 1 THEN ene.cant_lineas ELSE 0 END as enero, "+
							"CASE WHEN fe.mes = 2 THEN fe.cant_lineas ELSE 0 END as febrero, "+
							"CASE WHEN mar.mes = 3 THEN mar.cant_lineas ELSE 0 END as marzo, "+
							"CASE WHEN abr.mes = 4 THEN abr.cant_lineas ELSE 0 END as abril, "+
							"CASE WHEN may.mes = 5 THEN may.cant_lineas ELSE 0 END as mayo, "+
							"CASE WHEN jun.mes = 6 THEN jun.cant_lineas ELSE 0 END as junio, "+
							"CASE WHEN jul.mes = 7 THEN jul.cant_lineas ELSE 0 END as julio, "+
							"CASE WHEN ag.mes = 8 THEN ag.cant_lineas ELSE 0 END as agosto, "+
							"CASE WHEN set.mes = 9 THEN set.cant_lineas ELSE 0 END as setiembre, "+
							"CASE WHEN oct.mes = 10 THEN oct.cant_lineas ELSE 0 END as octubre, "+
							"CASE WHEN nov.mes = 11 THEN nov.cant_lineas ELSE 0 END as noviembre, "+
							"CASE WHEN dic.mes = 12 THEN dic.cant_lineas ELSE 0 END as diciembre "+
							"FROM  planificacion.configuracion_uo_cab cuc "+
							"LEFT JOIN remuneracion.procesamiento proc ON cuc.id_configuracion_uo = proc.id_configuracion_uo "+
							"LEFT JOIN remuneracion.procesamiento ene on ene.id_configuracion_uo=proc.id_configuracion_uo AND ene.mes = 1 AND ene.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento fe on fe.id_configuracion_uo=proc.id_configuracion_uo AND fe.mes = 2 AND fe.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento mar on mar.id_configuracion_uo=proc.id_configuracion_uo AND mar.mes = 3 AND mar.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento abr on abr.id_configuracion_uo=proc.id_configuracion_uo AND abr.mes = 4 AND abr.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento may on may.id_configuracion_uo=proc.id_configuracion_uo AND may.mes = 5 AND may.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento jun on jun.id_configuracion_uo=proc.id_configuracion_uo AND jun.mes = 6 AND jun.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento jul on jul.id_configuracion_uo=proc.id_configuracion_uo AND jul.mes = 7 AND jul.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento ag on ag.id_configuracion_uo=proc.id_configuracion_uo AND ag.mes = 8 AND ag.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento set on set.id_configuracion_uo=proc.id_configuracion_uo AND set.mes = 9 AND set.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento oct on oct.id_configuracion_uo=proc.id_configuracion_uo AND oct.mes = 10 AND oct.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento nov on  nov.id_configuracion_uo=proc.id_configuracion_uo AND nov.mes = 11 AND nov.anho = "+anho+" "+
							"LEFT JOIN remuneracion.procesamiento dic on dic.id_configuracion_uo=proc.id_configuracion_uo AND dic.mes = 12 AND dic.anho = "+anho+" "+
							"INNER JOIN planificacion.entidad en ON en.id_configuracion_uo = cuc.id_configuracion_uo "+
							"INNER JOIN sinarh.sin_entidad se ON en.id_sin_entidad = se.id_sin_entidad "+
							"INNER JOIN sinarh.sin_nivel_entidad sne ON sne.nen_codigo = se.nen_codigo "+
							"WHERE cuc.activo is true ";
							if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
								sql+="	AND sne.nen_codigo =  "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue();
							if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
								sql+="	AND se.ent_codigo = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue();
							if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null)
								sql+="	AND cuc.orden = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
							sql+=" ORDER BY sne.nen_codigo, se.ent_codigo, cuc.orden;";
					q = em.createNativeQuery(sql);
					List<Object[]> rpt = q.getResultList();
					int cont = 1;
					if(rpt.size() > 0){
						for (Object[] items : rpt) {
							String add="";
							add = cont+";";
							for(int i= 0;i<items.length;i++)
								add += ((Object) items[i])+";";
							listaLineas.add(add.toString().substring(0,add.toString().length()-1));
							cont++;
						}
					}else{
						statusMessages.add(Severity.WARN, "No existen datos para el NIVEL/ENTIDAD/OEE seleccionado");
						return;
					}
					FileUtils.writeLines(archSalida, listaLineas);
					JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
					archSalida.delete();
				
				} catch (Exception e) {
					statusMessages.add(Severity.ERROR, "Error en la descarga del archivo ");
					System.out.println(e.getMessage());
				}
		}
	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		primeraVez=true;
		cargarCabecera();
		mes = null;
		anho = null;
	}

	public boolean isPrimeraVez() {
		return primeraVez;
	}


	public void setPrimeraVez(boolean primeraVez) {
		this.primeraVez = primeraVez;
	}

	public Integer getMes() {
		return mes;
	}


	public void setMes(Integer mes) {
		this.mes = mes;
	}


	public Integer getAnho() {
		return anho;
	}


	public void setAnho(Integer anho) {
		this.anho = anho;
	}


	public Integer getMesHasta() {
		return mesHasta;
	}


	public void setMesHasta(Integer mesHasta) {
		this.mesHasta = mesHasta;
	}


	public Integer getAnhoHasta() {
		return anhoHasta;
	}


	public void setAnhoHasta(Integer anhoHasta) {
		this.anhoHasta = anhoHasta;
	}


	
	

}
