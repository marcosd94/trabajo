package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("rptPlanesCapacitacionEjecucionFC")
public class RptPlanesCapacitacionEjecucionFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4219668130390697342L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;


	private Long anio=Long.parseLong(General.anhoActual().toString());


	

	public void init() {

		
		nivelEntidadOeeUtil.init();

	}



	public void cargarCabecera() {

		// Nivel

		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad = em.find(SinEntidad.class, enti.getSinEntidad()
				.getIdSinEntidad());
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(enti
				.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	
	
	public  void imprimir(String tipo) throws Exception {
		if(anio==null){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el a&ntilde;o. Verifique");
			return;
		}
		
		if(anio.intValue()>General.anhoActual().intValue()){
			statusMessages.add(Severity.ERROR,"El a&ntilde;o ingresado es inv&aacute;lido");
			return;
		}
		
		Connection conn = null;
		try {

			HashMap<String, Object> param = new HashMap<String, Object>();
			param=obtenerMapaParametros();
			if(param==null)
				return;
			conn = JpaResourceBean.getConnection();
			String nombre_rpt="";
			if(tipo.equals("P"))
				nombre_rpt="RPT_CU505_planes";
			else
				nombre_rpt="RPT_CU505_ejecucion";
				
			JasperReportUtils.respondPDF(
					nombre_rpt, param, false,
					conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}
	
	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
	
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null){
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			param.put("idOee", nivelEntidadOeeUtil.getIdConfigCab());
		}
			
		if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null ){
			
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdUnidadOrganizativa().toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			param.put("idUo", nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		}
		if(anio!=null ){
			
			if (!sufc.validarInput(anio.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			param.put("anho", anio);
		
		}
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oeeUsuarioLogueado", cab.getDenominacionUnidad());
		

		
		return param;
	}

	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
	}

	public void cambiarUO() {
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
	}

	public Long getAnio() {
		return anio;
	}

	public void setAnio(Long anio) {
		this.anio = anio;
	}

	

}
