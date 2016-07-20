package py.com.excelsis.sicca.legajo.session.form;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

import enums.PermanenteContratadoEnums;
import enums.SexoEnums;
import enums.SiNoTodosEnums;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.NivelEstudio;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptPersonalNivelAcademicoOee739FC")
public class RptPersonalNivelAcademicoOee739FC {

	
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
	@In(required = false)
	ReportUtilFormController reportUtilFormController;


	private boolean primeraEntrada=true;
	private SiNoTodosEnums siNoTodosEnums=SiNoTodosEnums.TODOS;
	private PermanenteContratadoEnums permanenteContratado=PermanenteContratadoEnums.TODOS;
	private SexoEnums sexo=SexoEnums.TODOS;
	private List<SelectItem> tituloSelecItem;
	private Long idNivelEstudio;
	private Long idProfesion;
	private Long idTituloAcademico;
	
	public void init() {

		
		if(primeraEntrada){
			cargarCabecera();
			limpiarLista();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
	

	}

	private void limpiarLista(){
		siNoTodosEnums=SiNoTodosEnums.TODOS;
		permanenteContratado=PermanenteContratadoEnums.TODOS;
		sexo=SexoEnums.TODOS;
		idNivelEstudio=null;
		idProfesion=null;
		idTituloAcademico=null;
		upTituloAcademico();
		
	}
	

	private String obtenerSql() throws Exception {
	
		String sql = "SELECT DISTINCT 	OEE.DENOMINACION_UNIDAD AS OEE,e.id_persona as id, "+
					"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
					"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,OEE.ORDEN AS ORDEN, "+
					"	UO.DENOMINACION_UNIDAD UNIDAD_ORGANIZATIVA, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) as codigo_uo "+
					"	, P.DOCUMENTO_IDENTIDAD, P.APELLIDOS,  P.NOMBRES, case when e.contratado = true then 'CONTRATADO' ELSE 'PERMANENTE' end AS TIPO_PERSONA,  "+
					"	case when p.sexo = 'M' then 'Masculino' else 'Femenino' end as sexo, legajo.fnc_discapacidad(e.id_persona) as discapacidad,  "+
					"	legajo.fnc_fecha_ingreso_funcion_publica(e.id_persona) as fecha_ingreso, "+
					"	p.fecha_nacimiento,  "+
					"	SUBSTRING(AGE(NOW(),p.FECHA_NACIMIENTO)::TEXT FROM 1 FOR 2) EDAD, "+ 
					"	n.descripcion as nivel_academico, t.descripcion as titulo, esp.descripcion as profesion "+ 
					"	FROM GENERAL.EMPLEADO_PUESTO E "+
					"	JOIN GENERAL.PERSONA P "+
					"	ON E.ID_PERSONA = P.ID_PERSONA "+
					"	join SELECCION.ESTUDIOS_REALIZADOS Est "+
					"	on est.id_persona = e.id_persona "+
					"	left JOIN SELECCION.TITULO_ACADEMICO T "+
					"	ON T.ID_TITULO_ACADEMICO = Est.ID_TITULO_ACADEMICO "+
					"	LEFT JOIN PLANIFICACION.ESPECIALIDADES ESp "+
					"	ON ESp.ID_ESPECIALIDADES = Est.ID_ESPECIALIDAD"+ 
					"	left JOIN SELECCION.nivel_estudio n "+
					"	ON n.id_nivel_estudio = t.id_nivel_estudio "+
					"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
					"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
					"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "+
					"	ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
					"	JOIN PLANIFICACION.ENTIDAD ENTIDAD "+ 
					"	ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
					"	  JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
					"	  JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "; 
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null){
			sql+=" AND SNE.NEN_CODIGO = "+nivelEntidadOeeUtil.getCodNivelEntidad();
		}
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null){
			sql+="AND SIN_ENTIDAD.ENT_CODIGO =  "+nivelEntidadOeeUtil.getCodSinEntidad();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND  OEE.ORDEN = "+ nivelEntidadOeeUtil.getOrdenUnidOrg();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND UO.ID_CONFIGURACION_UO_DET = "+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		
		if (sexo.getValor() != null)
			sql += " and p.sexo =  '" + sexo.getValor()+"'";
		if (permanenteContratado.getValor() != null)
			sql += " and e.contratado = " +permanenteContratado.getValor();
		if(siNoTodosEnums.getValor()!=null){
			if(siNoTodosEnums.getValor())
				sql+=" and legajo.fnc_discapacidad(e.id_persona) = 'Si'";
			else
				sql+=" and legajo.fnc_discapacidad(e.id_persona) = 'No'";
		}
		if(idNivelEstudio!=null)
			sql+=" and n.id_nivel_estudio ="+idNivelEstudio;
		if(idTituloAcademico!=null)
			sql+=" and t.ID_TITULO_ACADEMICO = "+idTituloAcademico;
		if(idProfesion!=null)
			sql+=" and  ESp.ID_ESPECIALIDADES = "+idProfesion;
		sql+=" order by SNE.NEN_CODIGO,SIN_ENTIDAD.ENT_CODIGO, "+
			"	OEE.ORDEN,UO.DENOMINACION_UNIDAD,P.APELLIDOS, N.DESCRIPCION, T.DESCRIPCION, ESP.DESCRIPCION ";
		return sql;
	}
	

	public void cargarCabecera() {

		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}
	
	
	
	public void imprimir(String formato) throws Exception {
			
			
			
			
			try {
				reportUtilFormController =
					(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
				reportUtilFormController.init();
				HashMap<String, Object> param = new HashMap<String, Object>();
				param=obtenerMapaParametros();
				if(param==null)
					return;
				reportUtilFormController.setParametros(param);
				reportUtilFormController.setNombreReporte("RPT_CU739");
				if ("EXCEL".equals(formato))
					reportUtilFormController.imprimirReporteXLS();
				else
					reportUtilFormController.imprimirReportePdf();

			} catch (Exception e) {
				e.printStackTrace();
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
			
		}else {
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
				param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+" "+nivelEntidadOeeUtil.getNombreNivelEntidad());
			if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
				param.put("entidad",nivelEntidadOeeUtil.getCodSinEntidad()+" "+nivelEntidadOeeUtil.getNombreSinEntidad());
		}
			
		
		
		param.put("sexo", sexo.getDescripcion());
		param.put("tipoPersona", permanenteContratado.getDescripcion());
		param.put("discapacidad", siNoTodosEnums.getDescripcion());
		if(idNivelEstudio!=null){
			NivelEstudio nivelEstudio= em.find(NivelEstudio.class,idNivelEstudio);
			param.put("nivelAcademico", nivelEstudio.getDescripcion());
		}else
			param.put("nivelAcademico", "Todos");
			
		if(idTituloAcademico!=null){
			TituloAcademico tituloAcademico=em.find(TituloAcademico.class, idTituloAcademico);
			param.put("titulo", tituloAcademico.getDescripcion());
		}else
			param.put("titulo", "Todos");
		if(idProfesion!=null){
			Especialidades especialidades=em.find(Especialidades.class, idProfesion);
			param.put("profesion", especialidades.getDescripcion());
		}else
			param.put("profesion", "Todos");
		
		
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oee_uo", cab.getDenominacionUnidad());
		String sql=null;
		sql=obtenerSql();
		if(sql==null)
			return null;
		param.put("sql",sql);
		
		return param;
	}
	
	
	

	public List<TituloAcademico> upTituloAcademico(){
		idTituloAcademico=null;
		return getTituloAcademicos();
	}
	@SuppressWarnings("unchecked")
	public List<TituloAcademico> getTituloAcademicos(){
		List<TituloAcademico> ti= new Vector<TituloAcademico>();
		try{
			if(idNivelEstudio!=null){
				setIdNivelEstudio(idNivelEstudio);
				ti= em.createQuery(" select o from " + TituloAcademico.class.getName() + " o where o.activo = true " +
					" and o.nivelEstudio.idNivelEstudio=:idNivelEstudio order by o.descripcion").setParameter("idNivelEstudio",idNivelEstudio).getResultList();
			}
			 getTituloAcademicoSelectItems(ti);
		
			return ti;
		}catch(Exception ex){
			ex.printStackTrace();
			return ti;
		}
	}
	
	public List<SelectItem> getTituloAcademicoSelectItems(List<TituloAcademico> ti){
		List<SelectItem> si = new Vector<SelectItem>();
		tituloSelecItem= new Vector<SelectItem>();
		si.add(new SelectItem(null, "Todos"));
		for(TituloAcademico o : ti)
			si.add(new SelectItem(o.getIdTituloAcademico(), o.getDescripcion()));
		
		tituloSelecItem=si;

		return si;
	}
	

	
	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public SiNoTodosEnums getSiNoTodosEnums() {
		return siNoTodosEnums;
	}

	public void setSiNoTodosEnums(SiNoTodosEnums siNoTodosEnums) {
		this.siNoTodosEnums = siNoTodosEnums;
	}

	public PermanenteContratadoEnums getPermanenteContratado() {
		return permanenteContratado;
	}

	public void setPermanenteContratado(
			PermanenteContratadoEnums permanenteContratado) {
		this.permanenteContratado = permanenteContratado;
	}

	public SexoEnums getSexo() {
		return sexo;
	}

	public void setSexo(SexoEnums sexo) {
		this.sexo = sexo;
	}

	public List<SelectItem> getTituloSelecItem() {
		return tituloSelecItem;
	}

	public void setTituloSelecItem(List<SelectItem> tituloSelecItem) {
		this.tituloSelecItem = tituloSelecItem;
	}

	public Long getIdNivelEstudio() {
		return idNivelEstudio;
	}

	public void setIdNivelEstudio(Long idNivelEstudio) {
		this.idNivelEstudio = idNivelEstudio;
	}

	public Long getIdProfesion() {
		return idProfesion;
	}

	public void setIdProfesion(Long idProfesion) {
		this.idProfesion = idProfesion;
	}

	public Long getIdTituloAcademico() {
		return idTituloAcademico;
	}

	public void setIdTituloAcademico(Long idTituloAcademico) {
		this.idTituloAcademico = idTituloAcademico;
	}
	
	
}
