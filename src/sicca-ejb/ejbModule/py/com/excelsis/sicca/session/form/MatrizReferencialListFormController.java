package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jboss.seam.international.StatusMessages;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.session.MatrizReferencialList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.PAGE)
@Name("matrizReferencialListFormController")
public class MatrizReferencialListFormController implements Serializable {

	@In
	StatusMessages statusMessages;

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	MatrizReferencialList matrizReferencialList;
	@In(create = true)
	DatosEspecificosList datosEspecificosList;
	@In(required = false)
	Usuario usuarioLogueado;

	private List<SelectItem> tipoConcursoSelectItem = new ArrayList<SelectItem>();
	private MatrizReferencial matrizReferencial = new MatrizReferencial();
	private Long idDatosEspecificos;
	private String valueToUrlNewRecord;
	private HashMap<String, List<MatrizReferencialEnc>> cacheMapSize =
		new HashMap<String, List<MatrizReferencialEnc>>();

	StringBuffer sql = new StringBuffer();

	public void init() {
		matrizReferencial.setActivo(Estado.ACTIVO.getValor());
		updateSelectItemTipoConcurso();
		search();
	}

	public void search() {
		 
		matrizReferencialList.getDatosEspecificos().setIdDatosEspecificos(idDatosEspecificos);
		matrizReferencialList.getMatrizReferencial().setActivo(matrizReferencial.getActivo());
		if (matrizReferencial != null && matrizReferencial.getDescripcion() != null
			&& matrizReferencial.getDescripcion().isEmpty())
			matrizReferencial.setDescripcion("");

		matrizReferencialList.getMatrizReferencial().setDescripcion(matrizReferencial.getDescripcion()== null?"":matrizReferencial.getDescripcion() );
		matrizReferencialList.setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		matrizReferencialList.getResultList();
		System.out.println("## ");
		 
	}

	public void searchAll() {
		idDatosEspecificos = null;
		matrizReferencial = new MatrizReferencial();
		matrizReferencial.setActivo(Estado.ACTIVO.getValor());
		search();
	}

	 

	 
 
	@SuppressWarnings("unchecked")
	public void pdf() throws SQLException {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		Connection conn = null;
		try {

			sql = new StringBuffer();

			sql.append("SELECT 	SEL_MATRIZ_REFERENCIAL.DESCRIPCION AS plantilla ,	SEL_DATOS_ESPECIFICOS_TC.DESCRIPCION AS tipoConcurso  ");
			sql.append(" ,	SEL_MATRIZ_REFERENCIAL.PUNTAJE_MAXIMO AS puntMax,	SEL_MATRIZ_REFERENCIAL.ACTIVO AS estado	   ");
			sql.append(" ,	enc.DESCRIPCION AS factorEvaluacion,	enc.PUNTAJE_MAXIMO AS puntajeMaximo ");
			sql.append(" ,	SEL_DATOS_ESPECIFICOS_TE.DESCRIPCION AS tipoEvaluacion,	det.DESCRIPCION AS itemsEvaluacion ");
			sql.append(" , 	det.PUNTAJE_MINIMO AS minimo,	det.PUNTAJE_MAXIMO AS maximo  ");
			sql.append(" FROM seleccion.matriz_referencial  SEL_MATRIZ_REFERENCIAL  ");
			sql.append(" LEFT JOIN seleccion.datos_especificos SEL_DATOS_ESPECIFICOS_TC ON SEL_DATOS_ESPECIFICOS_TC.ID_DATOS_ESPECIFICOS = SEL_MATRIZ_REFERENCIAL.ID_DATOS_ESPECIFICOS_TIPO_CONC  ");
			sql.append(" LEFT JOIN seleccion.matriz_referencial_enc enc  ON enc.ID_MATRIZ_REFERENCIAL = SEL_MATRIZ_REFERENCIAL.ID_MATRIZ_REFERENCIAL  ");
			sql.append(" LEFT JOIN seleccion.matriz_referencial_det det ON det.ID_MATRIZ_REFERENCIAL_ENC = enc.ID_MATRIZ_REFERENCIAL_ENC  ");
			sql.append(" LEFT JOIN seleccion.datos_especificos SEL_DATOS_ESPECIFICOS_TE ON SEL_DATOS_ESPECIFICOS_TE.ID_DATOS_ESPECIFICOS = enc.ID_DATOS_ESPECIFICOS_TIPO_EVAL  ");
			sql.append("  where 1=1   ");
			if (matrizReferencial.getDescripcion() != null
				&& !matrizReferencial.getDescripcion().trim().isEmpty()) {
//				if (!sufc.validarInput(matrizReferencial.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
//					return ;
//				}
				sql.append("  AND lower(SEL_MATRIZ_REFERENCIAL.DESCRIPCION) LIKE '%"
					+ sufc.caracterInvalido(matrizReferencial.getDescripcion().toLowerCase()) + "%'");
			}
			if (idDatosEspecificos != null) {
				if (!sufc.validarInput(idDatosEspecificos.toString(), TiposDatos.LONG.getValor(), null)) {
					return ;
				}
				sql.append("  and  SEL_MATRIZ_REFERENCIAL.ID_DATOS_ESPECIFICOS_TIPO_CONC ="
					+ idDatosEspecificos);
			}

			if (matrizReferencial.getActivo() != null) {
				if (!sufc.validarInput(matrizReferencial.getActivo() .toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return ;
				}
				sql.append(" and  SEL_MATRIZ_REFERENCIAL.ACTIVO  = "
					+ matrizReferencial.getActivo());
				sql.append(" and  enc.ACTIVO  = "
						+ matrizReferencial.getActivo());
				sql.append(" and  det.ACTIVO  = "
						+ matrizReferencial.getActivo());
			}

			sql.append("  order by enc.NRO_ORDEN,det.ID_MATRIZ_REFERENCIAL_DET,det.DESCRIPCION ");

			List<Object[]> resultado = em.createNativeQuery(sql.toString()).getResultList();

			if (resultado.isEmpty()) {
				statusMessages.add("No se encontraron Datos con los parametros seleccionados");
				return;
			}
			 conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU258_matriz_referencial", obtenerMapaParametros(), false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.close();
		}
	}

	public List<MatrizReferencialEnc> obtenerListaEncOrdenado(MatrizReferencial mat) {

		List<MatrizReferencialEnc> listaEnc = new ArrayList<MatrizReferencialEnc>();
		listaEnc = mat.getMatrizReferencialEncs();
		List<MatrizReferencialEnc> listaRes = new ArrayList<MatrizReferencialEnc>();
		if (listaEnc != null && listaEnc.size() > 0) {
			MatrizReferencialEnc m = new MatrizReferencialEnc();
			m = listaEnc.get(0);
			while (listaEnc.size() > 0) {
				for (MatrizReferencialEnc matriz : listaEnc) {
					if (matriz.getNroOrden().intValue() < m.getNroOrden().intValue())
						m = matriz;
				}

				listaRes.add(m);
				listaEnc.remove(m);
			}
			return listaRes;
		}
		return null;

	}

	public String peticionNuevoRegistro() {
		return valueToUrlNewRecord;
	}

	public String cancelarPeticionNuevoRegistro() {
		valueToUrlNewRecord = null;
		return valueToUrlNewRecord;
	}

	// METODOS PRIVADOS
	private void updateSelectItemTipoConcurso() {
		tipoConcursoSelectItem.clear();
		tipoConcursoSelectItem.add(new SelectItem(null, SICCAAppHelper.getBundleMessage("opt_select_one")));

		datosEspecificosList.getDatosGenerales().setDescripcion("TIPOS DE CONCURSO");
		datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
		datosEspecificosList.setMaxResults(null);

		List<DatosEspecificos> list = datosEspecificosList.getResultList();
		for (DatosEspecificos de : list) {
			tipoConcursoSelectItem.add(new SelectItem(de.getIdDatosEspecificos(), de.getDescripcion()));
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		param.put("sql", sql.toString());
		if (matrizReferencial.getDescripcion() != null)
			param.put("descripcion", !matrizReferencial.getDescripcion().equals("")
				? matrizReferencial.getDescripcion().toUpperCase() : "TODOS");
		if (matrizReferencial.getActivo() != null)
			if (matrizReferencial.getActivo())
				param.put("estado", "ACTIVO");
			else
				param.put("estado", "INACTIVO");
		else
			param.put("estado", "TODOS");
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

	// GETTERS Y SETTERS
	public MatrizReferencial getMatrizReferencial() {
		return matrizReferencial;
	}

	public void setMatrizReferencial(MatrizReferencial matrizReferencial) {
		this.matrizReferencial = matrizReferencial;
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

	public String getValueToUrlNewRecord() {
		return valueToUrlNewRecord;
	}

	public void setValueToUrlNewRecord(String valueToUrlNewRecord) {
		this.valueToUrlNewRecord = valueToUrlNewRecord;
	}

}
