package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import java.sql.Connection;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import py.com.excelsis.sicca.util.JasperReportUtils;

@Scope(ScopeType.PAGE)
@Name("rptRevisionPublicacionGrupo")
public class RptRevisionPublicacionGrupo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -979560663262546380L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false,create=true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	@In(required = false,create=true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	private HashMap<String, Object> param = new HashMap<String, Object>();
	private Long idConcursoPuestoAgr;
	
	public void init() {
		param = obtenerMapaParametros();
	}


	public void imprimirReporte() {
		try {
			Connection conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("Rpt_467_imprimi_revision_publicacion_grupo",	obtenerMapaParametros(), false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private HashMap<String, Object> obtenerMapaParametros() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		if (usuarioLogueado != null){
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
		}
		else{
			param.put("usuario", "");
		}
		param.put("id_grupo", idConcursoPuestoAgr);
		if(idConcursoPuestoAgr!=null)
		{
			ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
			param.put("nroOrden", agr.getNroOrden());
			param.put("nombre",agr.getDescripcionGrupo());
			param.put("estadoGrup", seleccionUtilFormController.getEstadoGrupo(idConcursoPuestoAgr));
			Concurso concurso= em.find(Concurso.class,agr.getConcurso().getIdConcurso());
			param.put("concurso", (concurso.getNumero() == null
					? "#" : concurso.getNumero())
					+ "/"
					+ (concurso.getAnhio() == null ? "#" : concurso.getAnhio())
					+ " - "
					+ concurso.getConfiguracionUoCab().getDescripcionCorta()+" - "+concurso.getNombre());
			
			nivelEntidadOeeUtil.init();
			nivelEntidadOeeUtil.setCodNivelEntidad(concurso.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo());
			nivelEntidadOeeUtil.findNivelEntidad();
			nivelEntidadOeeUtil.setCodSinEntidad(concurso.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo());
			nivelEntidadOeeUtil.findEntidad();
			
			param.put("entidad",nivelEntidadOeeUtil.getCodSinEntidad()+"-"+nivelEntidadOeeUtil.getNombreSinEntidad());
			param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+"-"+nivelEntidadOeeUtil.getNombreNivelEntidad());
			param.put("oee",concurso.getConfiguracionUoCab().getOrden()+"-"+concurso.getConfiguracionUoCab().getDenominacionUnidad());
			
		}
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));

		return param;
	}


	

	public HashMap<String, Object> getParametros() {
		return param;
	}

	public void setParametros(HashMap<String, Object> param) {
		this.param = param;
	}


	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}


	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}
	
	
	
	
}
