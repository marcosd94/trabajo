package py.com.excelsis.sicca.legajo.session.form;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.jboss.seam.international.StatusMessage.Severity;

import enums.SiNo;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptDeclaracionesJuradas679FC")
public class RptDeclaracionesJuradas679FC {

	
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

	private Date fechaDesde;
	private Date fechaHasta;
	private Long idPaisExp;
	private boolean primeraEntrada=true;
	private String nombre;
	private String apellido;
	private String cidentidad;
	private SiNo siNo=SiNo.SI;

	public void init() {

		
		if(primeraEntrada){
			cargarCabecera();
			limpiarLista();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
	

	}

	private void limpiarLista(){
		fechaDesde = null;
		fechaHasta = null;
		idPaisExp=General.getIdParaguay();
		siNo=SiNo.SI;
		nombre=null;
		cidentidad=null;
		apellido=null;
	}
	
	
	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
	}

	private String obtenerSql1() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT DISTINCT OEE.DENOMINACION_UNIDAD AS OEE, "+
			"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
			"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,	"+
			"	UO.DENOMINACION_UNIDAD, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) as uo_nro"+
			"	, PAIS.DESCRIPCION as pais, P.NOMBRES ||' '|| P.APELLIDOS as nombres, P.DOCUMENTO_IDENTIDAD, DJ.FECHA_PRESENTACION, DJ.COMENTARIO " +
			"   ,OEE.ID_CONFIGURACION_UO as idOee,UO.ID_CONFIGURACION_UO_DET as idUo,p.id_persona as idPersonas ,puesto.descripcion as puesto"+
			"	FROM LEGAJO.DECLARACION_JURADA DJ "+
			"	JOIN GENERAL.PERSONA P 	ON P.ID_PERSONA = DJ.ID_PERSONA "+
			"	JOIN GENERAL.PAIS 	ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC "+
			"	JOIN GENERAL.EMPLEADO_PUESTO E 	ON E.ID_PERSONA = P.ID_PERSONA "+
			"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO 		ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
			"	JOIN PLANIFICACION.ENTIDAD ENTIDAD  ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
			"   JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
			"	JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "+
			"	WHERE E.ACTIVO = TRUE ";
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null){
			sql+=" AND SNE.NEN_CODIGO = "+nivelEntidadOeeUtil.getCodNivelEntidad();
		}
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null){
			sql+="AND SIN_ENTIDAD.ENT_CODIGO =  "+nivelEntidadOeeUtil.getCodSinEntidad();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND OEE.ID_CONFIGURACION_UO = "+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND UO.ID_CONFIGURACION_UO_DET = "+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		
		if (fechaDesde != null)
			sql += " AND DJ.FECHA_PRESENTACION >= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " AND DJ.FECHA_PRESENTACION <= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";
		if(idPaisExp!=null){
			if (!sufc.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			sql+=" AND  PAIS.id_pais="+idPaisExp.longValue();
		}
		if(cidentidad!=null && !cidentidad.trim().equals("")){
			if (!sufc.validarInput(cidentidad.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND  P.DOCUMENTO_IDENTIDAD ='"+cidentidad+"'";
			
		}
		if(nombre!=null&& !nombre.trim().equals("")){
			if (!sufc.validarInput(nombre.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND lower(P.NOMBRES) like '%"+nombre.toLowerCase()+"%'";
		}
		if(apellido!=null && !apellido.trim().equals("")){
			if (!sufc.validarInput(apellido.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND  lower(P.APELLIDOS) like '%"+apellido.toLowerCase()+"%'";
		}
		return sql;
	}
	private String obtenerSql2() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = " SELECT DISTINCT OEE.DENOMINACION_UNIDAD AS OEE, "+
				"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
				"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN, "+
				"	UO.DENOMINACION_UNIDAD, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) as uo_nro "+ 
				"	, PAIS.DESCRIPCION as pais, P.NOMBRES ||' '|| P.APELLIDOS as nombres, P.DOCUMENTO_IDENTIDAD "+
				"   ,OEE.ID_CONFIGURACION_UO as idOee,UO.ID_CONFIGURACION_UO_DET as idUo,p.id_persona as idPersonas ,puesto.descripcion as puesto "+
				"	FROM GENERAL.PERSONA P "+
				"	JOIN GENERAL.PAIS "+
				"	ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC "+
				"	JOIN GENERAL.EMPLEADO_PUESTO E "+
				"	ON E.ID_PERSONA = P.ID_PERSONA "+
				"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
				"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "+
				"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
				"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
				"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "+
				"	ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
				"	JOIN PLANIFICACION.ENTIDAD ENTIDAD  "+
				"	ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
				"	JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
				"	JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "+
				"	WHERE E.ACTUAL= TRUE "+
				"	AND P.ID_PERSONA NOT IN " +
				"(SELECT DC.ID_PERSONA FROM LEGAJO.DECLARACION_JURADA DC WHERE DC.ACTIVO = TRUE ";
		if (fechaDesde != null)
			sql += " AND DC.FECHA_PRESENTACION >= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " AND DC.FECHA_PRESENTACION <= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";		
		sql+=" )";
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null){
			sql+=" AND SNE.NEN_CODIGO = "+nivelEntidadOeeUtil.getCodNivelEntidad();
		}
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null){
			sql+="AND SIN_ENTIDAD.ENT_CODIGO =  "+nivelEntidadOeeUtil.getCodSinEntidad();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND OEE.ID_CONFIGURACION_UO = "+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND UO.ID_CONFIGURACION_UO_DET = "+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		
		
		if(idPaisExp!=null){
			if (!sufc.validarInput(idPaisExp.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND PAIS.id_pais="+idPaisExp.longValue();
		}
		if(cidentidad!=null && !cidentidad.trim().equals("")){
			if (!sufc.validarInput(cidentidad.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND  P.DOCUMENTO_IDENTIDAD ='"+cidentidad+"'";
			
		}
		if(nombre!=null && !nombre.trim().equals("")){
			if (!sufc.validarInput(nombre.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND lower(P.NOMBRES) like '%"+nombre.toLowerCase()+"%'";
		}
		if(apellido!=null && !apellido.trim().equals("")){
			if (!sufc.validarInput(apellido.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql+=" AND lower(P.APELLIDOS) like '%"+apellido.toLowerCase()+"%'";
		}
		return sql;
	}


	public void cargarCabecera() {

		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}
	
	
	
	public void imprimir() throws Exception {
			
			if(fechaDesde==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo Fecha Presentacion Desde antes de realizar esta acci\u00F3n");
				return ;
			}
			if(fechaHasta==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo  Hasta antes de realizar esta acci\u00F3n");
				return ;
			}
			
			Connection conn = null;
			try {
	
				HashMap<String, Object> param = new HashMap<String, Object>();
				param=obtenerMapaParametros();
				if(param==null)
					return;
				conn = JpaResourceBean.getConnection();
				param.put("REPORT_CONNECTION", conn);
				
				String reporte;
				if(siNo.getValor())
					reporte="RPT_CU679_1";
				else
					reporte="RPT_CU679_2";
				
				JasperReportUtils.respondPDF(reporte, param, false,
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
	
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		
	
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null){
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			param.put("nivel", diccDscNEO.get("NIVEL"));
			param.put("entidad", diccDscNEO.get("ENTIDAD"));
			param.put("oee", diccDscNEO.get("OEE"));
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null ){
				
				if (!sufc.validarInput(nivelEntidadOeeUtil.getIdUnidadOrganizativa().toString(), TiposDatos.LONG.getValor(), null)) {
					return null;
				}
				
				param.put("unidadOrga", diccDscNEO.get("UND_ORG"));
			}
			
		}
			
		
		
		param.put("ci", cidentidad);
		param.put("nombres", nombre);
		param.put("apellidos", apellido);
		param.put("declaraciones", siNo.getDescripcion());
		param.put("fecDesde", sdf.format(fechaDesde));
		param.put("fecHasta", sdf.format(fechaHasta));
		
		
		if(idPaisExp!=null){
			Pais pais=em.find(Pais.class,idPaisExp);
			param.put("paisExp", pais.getDescripcion());
		}
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oeeUsuarioLogueado", cab.getDenominacionUnidad());
		String sql=null;
		if(siNo.getValor())
		{
			sql=obtenerSql1();
			if(sql==null)
				return null;
		
		}
		else
		{
			sql=obtenerSql2();
			if(sql==null)
				return null;
			
		}
		param.put("sql",sql);
		
		return param;
	}
	
	
	


	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}


	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCidentidad() {
		return cidentidad;
	}

	public void setCidentidad(String cidentidad) {
		this.cidentidad = cidentidad;
	}

	public SiNo getSiNo() {
		return siNo;
	}

	public void setSiNo(SiNo siNo) {
		this.siNo = siNo;
	}

	
	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	
	
}
