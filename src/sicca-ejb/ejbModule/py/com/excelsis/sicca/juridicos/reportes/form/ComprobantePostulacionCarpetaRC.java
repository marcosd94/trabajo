package py.com.excelsis.sicca.juridicos.reportes.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;


import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("comprobantePostulacionCarpetaRC")
public class ComprobantePostulacionCarpetaRC {

	private Long id;

	public void init() {

	}

	public void print(Long id) throws SQLException {
		Connection conn = JpaResourceBean.getConnection();
		try {
			
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("REPORT_CONNECTION", conn);
			param.put("sql", obtenerSql(id));
			JasperReportUtils.respondPDF("RPT_CU514_comprobantePostulacionCarpeta", param, false, conn, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		
	}

	private String obtenerSql(Long id){
		String sql = "SELECT P.NOMBRES ||' '|| P.APELLIDOS 	AS Postulante, P.DOCUMENTO_IDENTIDAD	AS ci, " +
				"U.CODIGO_USUARIO AS Cod_postulante, U.CONTRASENHA AS pass, " +
				"CP.DESCRIPCION_GRUPO AS Grupo_puestos, C.NOMBRE AS Concurso, " +
				"C.NUMERO ||'/'|| C.ANIO ||' de '|| CU.DESCRIPCION_CORTA AS cod_concurso, " +
				"CU.DENOMINACION_UNIDAD	AS OEE, POST.FECHA_POSTULACION AS fecha_hora_post, " +
				"POST.USU_POSTULACION AS usu_post, POST.ID_POSTULACION, PAIS.DESCRIPCION AS PAIS " +
				"FROM SELECCION.POSTULACION POST " +
				"JOIN GENERAL.PERSONA P ON P.ID_PERSONA = POST.ID_PERSONA " +
				"JOIN SEGURIDAD.USUARIO U ON U.ID_PERSONA = P.ID_PERSONA " +
				"JOIN SELECCION.CONCURSO_PUESTO_AGR CP ON CP.ID_CONCURSO_PUESTO_AGR = POST.ID_CONCURSO_PUESTO_AGR " +
				"JOIN SELECCION.CONCURSO C ON C.ID_CONCURSO  = CP.ID_CONCURSO " +
				"JOIN PLANIFICACION.CONFIGURACION_UO_CAB CU ON CU.ID_CONFIGURACION_UO  = C.ID_CONFIGURACION_UO " +
				"JOIN GENERAL.PAIS PAIS ON PAIS.ID_PAIS = P.id_pais_expedicion_doc " +
				"WHERE POST.ID_POSTULACION = "+id;
		return sql;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
