package py.com.excelsis.sicca.reportes.form;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;

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
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("rptPostulantesInscrpCancelado")
public class RptPostulantesInscrpCancelado {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
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
			boolean canSeeID = false;
			Set<UsuarioRol> roles = usuarioLogueado.getUsuarioRols();
			for (UsuarioRol rol : roles) {
				if (rol.getRol().getHomologador() != null)
					if (rol.getRol().getHomologador()) {
						canSeeID = true;
						break;
					}
			}
			if( canSeeID || validarTipoConcurso())
				JasperReportUtils.respondPDF("RPT_CU470_postulantes_insc_cancel",	obtenerMapaParametros(), false, conn, usuarioLogueado);
			else
				JasperReportUtils.respondPDF("RPT_CU470_postulantes_sin_id",	obtenerMapaParametros(), false, conn, usuarioLogueado);
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
		ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		Concurso concurso= em.find(Concurso.class,agr.getConcurso().getIdConcurso());
		nivelEntidadOeeUtil.init();
		nivelEntidadOeeUtil.setCodNivelEntidad(concurso.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.findNivelEntidad();
		nivelEntidadOeeUtil.setCodSinEntidad(concurso.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo());
		nivelEntidadOeeUtil.findEntidad();
		
		param.put("idGrupo", idConcursoPuestoAgr);
		param.put("entidad",nivelEntidadOeeUtil.getCodSinEntidad()+"-"+nivelEntidadOeeUtil.getNombreSinEntidad());
		param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+"-"+nivelEntidadOeeUtil.getNombreNivelEntidad());
		param.put("oee",concurso.getConfiguracionUoCab().getOrden()+"-"+concurso.getConfiguracionUoCab().getDenominacionUnidad());
		param.put("grupo",agr.getDescripcionGrupo());
		param.put("estadoActual", seleccionUtilFormController.getEstadoGrupo(idConcursoPuestoAgr));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("concursoCodNom", (concurso.getNumero() == null
				? "" : concurso.getNumero())
				+ (concurso.getAnhio() == null ? "" : "/")
				+ (concurso.getAnhio() == null ? "" : concurso.getAnhio())
				+ (concurso.getAnhio() == null ? "" : " - ")
				+ concurso.getConfiguracionUoCab().getDescripcionCorta()+" - "+concurso.getNombre());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));

		return param;
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
	
	private boolean validarTipoConcurso() {
		
		Referencias ref = new Referencias();
		Referencias ref2 = new Referencias();
		Referencias ref3 = new Referencias();
		Referencias ref4 = new Referencias();
		Referencias ref5 = new Referencias();
		ConcursoPuestoAgr concursoPuestoAgr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_LARGA");
		ref2 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_CORTA");
		ref3 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_FINALIZADO");
		ref4 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_TERNA_FINAL");
		ref5 = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"PUBLICACION_LISTA_ADJUDICADOS");
	
		if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref2.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref3.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref4.getValorNum()) || concursoPuestoAgr.getEstado().equals(ref5.getValorNum()))
		return true;
		else
		return false;
	}

}
