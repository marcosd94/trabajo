package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocumentalEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.session.MatrizDocumentalEncList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import enums.Estado;
import enums.TiposDatos;

@Scope(ScopeType.PAGE)
@Name("matrizDocumentalEncListFormController")
public class MatrizDocumentalEncListFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8292143429768801456L;

	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	MatrizDocumentalEncList matrizDocumentalEncList;
	@In(create = true)
	DatosEspecificosList datosEspecificosList;

	private List<SelectItem> tipoConcursoSelectItem = new ArrayList<SelectItem>();
	private Long idDatosEspecificos;
	private Boolean estado;
	private MatrizDocumentalEnc matrizDocumentalEnc = new MatrizDocumentalEnc();

	public void init() {
		matrizDocumentalEnc.setActivo(Estado.ACTIVO.getValor());
		updateSelectItemTipoArchivo();
		search();
	}

	public void search() {
		matrizDocumentalEncList.getDatosEspecificos().setIdDatosEspecificos(idDatosEspecificos);
		matrizDocumentalEncList.getMatrizDocumentalEnc().setActivo(matrizDocumentalEnc.getActivo());
		if (matrizDocumentalEnc != null && matrizDocumentalEnc.getDescripcion() != null
			&& matrizDocumentalEnc.getDescripcion().isEmpty())
			matrizDocumentalEnc.setDescripcion(null);

		matrizDocumentalEncList.getMatrizDocumentalEnc().setDescripcion(matrizDocumentalEnc.getDescripcion());
		matrizDocumentalEncList.setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		matrizDocumentalEncList.getResultList();
	}

	public void searchAll() {
		idDatosEspecificos = null;
		matrizDocumentalEnc = new MatrizDocumentalEnc();
		matrizDocumentalEnc.setActivo(Estado.ACTIVO.getValor());
		search();
	}

	public void pdf() throws SQLException {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				throw new Exception(SeamResourceBundle.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
			} else {
				 	conn = JpaResourceBean.getConnection();
					JasperReportUtils.respondPDF("RPT_CU257_matriz_doc_sfp", mapa, false, conn, usuarioLogueado);
					conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.close();
		}

	}

	// METODOS PRIVADOS
	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT SEL_DATOS_ESPECIFICOS_TC.DESCRIPCION AS tipoConcurso,	enc.DESCRIPCION AS descripcion  ");
		sql.append(" , 	case when det.ACTIVO = 'f'   then 'NO' else 'SI' end as activo   ");
		sql.append("  ,	det.NRO_ORDEN AS nroOrden ,SEL_DATOS_ESPECIFICOS_TD.DESCRIPCION AS tipDocumento ");
		sql.append(" FROM seleccion.matriz_documental_enc enc ");
		sql.append(" left JOIN  seleccion.matriz_documental_det det ON enc.ID_MATRIZ_DOCUMENTAL_ENC = det.ID_MATRIZ_DOCUMENTAL_ENC  ");
		sql.append(" left JOIN seleccion.datos_especificos SEL_DATOS_ESPECIFICOS_TC ON SEL_DATOS_ESPECIFICOS_TC.ID_DATOS_ESPECIFICOS = enc.ID_DATOS_ESPECIFICOS_TIPO_CONC  ");
		sql.append(" left JOIN seleccion.datos_especificos SEL_DATOS_ESPECIFICOS_TD ON SEL_DATOS_ESPECIFICOS_TD.ID_DATOS_ESPECIFICOS = det.ID_DATOS_ESPECIFICOS_TIPO_DOCU ");
		sql.append("   WHERE 1=1  ");
		if (matrizDocumentalEnc.getDescripcion() != null
			&& !matrizDocumentalEnc.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(matrizDocumentalEnc.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  AND 	LOWER(enc.DESCRIPCION) LIKE '%"
				+ sufc.caracterInvalido(matrizDocumentalEnc.getDescripcion().toLowerCase()) + "%'");
		}
		if (idDatosEspecificos != null) {
			if (!sufc.validarInput(idDatosEspecificos.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			sql.append("  AND 	enc.ID_DATOS_ESPECIFICOS_TIPO_CONC =" + idDatosEspecificos);
		}
		if (matrizDocumentalEnc.getActivo() != null) {
			sql.append(" AND    enc.ACTIVO  = " + matrizDocumentalEnc.getActivo());
			if (!sufc.validarInput(matrizDocumentalEnc.getActivo().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			if (matrizDocumentalEnc.getActivo())
				param.put("estado", "ACTIVO");
			else
				param.put("estado", "INACTIVO");
		} else
			param.put("estado", "TODOS");

		sql.append("  ORDER BY SEL_DATOS_ESPECIFICOS_TC.DESCRIPCION, det.NRO_ORDEN   ");
		param.put("sql", sql.toString());
		if (matrizDocumentalEnc.getDescripcion() != null)
			param.put("descripcion", !matrizDocumentalEnc.getDescripcion().equals("")
				? matrizDocumentalEnc.getDescripcion() : "TODOS");

		if (idDatosEspecificos != null) {
			DatosEspecificos d = em.find(DatosEspecificos.class, idDatosEspecificos);
			param.put("tipoConcurso", d.getDescripcion());
		} else
			param.put("tipoConcurso", "TODOS");
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	private void updateSelectItemTipoArchivo() {
		tipoConcursoSelectItem.clear();
		tipoConcursoSelectItem.add(new SelectItem(null, SICCAAppHelper.getBundleMessage("opt_select_one")));

		datosEspecificosList.getDatosGenerales().setDescripcion("TIPOS DE CONCURSO");
		datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
		datosEspecificosList.getDatosEspecificos().setValorAlf(null);
		datosEspecificosList.getEstado().setValor(true);
		datosEspecificosList.setMaxResults(null);

		List<DatosEspecificos> list = datosEspecificosList.getResultList();
		for (DatosEspecificos de : list) {
			tipoConcursoSelectItem.add(new SelectItem(de.getIdDatosEspecificos(), de.getDescripcion()));
		}
	}

	// GETTERS Y SETTERS
	public MatrizDocumentalEnc getMatrizDocumentalEnc() {
		return matrizDocumentalEnc;
	}

	public void setMatrizDocumentalEnc(MatrizDocumentalEnc matrizDocumentalEnc) {
		this.matrizDocumentalEnc = matrizDocumentalEnc;
	}

	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}

	public List<SelectItem> getTipoConcursoSelectItem() {
		return tipoConcursoSelectItem;
	}

	public void setTipoConcursoSelectItem(List<SelectItem> tipoConcursoSelectItem) {
		this.tipoConcursoSelectItem = tipoConcursoSelectItem;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Boolean getEstado() {
		return estado;
	}
}
