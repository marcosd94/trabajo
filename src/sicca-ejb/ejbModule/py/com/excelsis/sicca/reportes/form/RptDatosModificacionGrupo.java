package py.com.excelsis.sicca.reportes.form;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("rptDatosModificacionGrupo")
public class RptDatosModificacionGrupo {
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
	
	@In(required = false,create=true)
	SeleccionUtilFormController seleccionUtilFormController;
	private Long idConcursoPuestoAgr;




	public void init() {

	}
	
	
	public void limpiar(){
		nivelEntidadOeeUtil.limpiar();
		
	}

	
	
	public void imprimir() {
		

		try {
			Connection conn = JpaResourceBean.getConnection();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			if (usuarioLogueado != null){
				param.put("usuario", usuarioLogueado.getCodigoUsuario());
			}
			else{
				param.put("usuario", "");
			}
			param.put("id_grupo", idConcursoPuestoAgr);
			ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
			Concurso concurso= em.find(Concurso.class,agr.getConcurso().getIdConcurso());
			nivelEntidadOeeUtil.init();
			nivelEntidadOeeUtil.setCodNivelEntidad(concurso.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo());
			nivelEntidadOeeUtil.findNivelEntidad();
			nivelEntidadOeeUtil.setCodSinEntidad(concurso.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo());
			nivelEntidadOeeUtil.findEntidad();
			
			param.put("idGrupo", idConcursoPuestoAgr);
			param.put("idConcurso", concurso.getIdConcurso());
			param.put("entidad",nivelEntidadOeeUtil.getCodSinEntidad()+"-"+nivelEntidadOeeUtil.getNombreSinEntidad());
			param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+"-"+nivelEntidadOeeUtil.getNombreNivelEntidad());
			param.put("oee",concurso.getConfiguracionUoCab().getOrden()+"-"+concurso.getConfiguracionUoCab().getDenominacionUnidad());
			param.put("nombre",agr.getDescripcionGrupo());
			param.put("estadoGrup", seleccionUtilFormController.getEstadoGrupo(idConcursoPuestoAgr));
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("concursoCodNom", (concurso.getNumero() == null
					? "#" : concurso.getNumero())
					+ "/"
					+ (concurso.getAnhio() == null ? "#" : concurso.getAnhio())
					+ " - "
					+ concurso.getConfiguracionUoCab().getDescripcionCorta()+" - "+concurso.getNombre());
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("REPORT_CONNECTION", conn);

			
			JasperReportUtils.respondPDF("RPT_CU469",	param, false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	


	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}


	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	
	

}
