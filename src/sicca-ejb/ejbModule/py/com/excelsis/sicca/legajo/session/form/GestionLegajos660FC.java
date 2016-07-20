package py.com.excelsis.sicca.legajo.session.form;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.evaluacion.session.form.HistCalifFuncListCustom;
import py.com.excelsis.sicca.legajo.session.GestionarLegajo660ListCustom;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gestionLegajos660FC")
public class GestionLegajos660FC {
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
	@In(required = false, create = true)
	GestionarLegajo660ListCustom gestionarLegajo660ListCustom;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	private Long idPais;
	private String nombres;
	private String apellidos;
	private String cedula;
	private String estado;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public void init() {
		cargarCabecera();
		search();
	}

	public void search() {

		String sql = "select Persona from EmpleadoPuesto EmpleadoPuesto, Persona Persona  ";

		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();

		String elWhere =
			" where EmpleadoPuesto.persona.idPersona = Persona.idPersona "
			+ " and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo ";
			//+ " and EmpleadoPuesto.actual=true ";
		parametros.put("idConfiguracionUo", new QueryValue(nivelEntidadOeeUtil.getIdConfigCab()));
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
			elWhere +=
				" and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet = :idConfiguracionUoDet ";
			parametros.put("idConfiguracionUoDet", new QueryValue(nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
		}
		if (idPais != null) {
			elWhere += " and EmpleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais = :idPais ";
			parametros.put("idPais", new QueryValue(idPais));
		}
		if (nombres != null && !nombres.trim().isEmpty()) {
			elWhere += " and upper(EmpleadoPuesto.persona.nombres) like :nombres ";
			parametros.put("nombres", new QueryValue(("%" + nombres.trim().toUpperCase() + "%")));
		}
		if (apellidos != null && !apellidos.trim().isEmpty()) {
			elWhere += " and upper(EmpleadoPuesto.persona.apellidos) like :apellidos ";
			parametros.put("apellidos", new QueryValue("%" + apellidos.trim().toUpperCase() + "%"));
		}
		if (cedula != null && !cedula.trim().isEmpty()) {
			elWhere += " and upper(EmpleadoPuesto.persona.documentoIdentidad) like :cedula ";
			parametros.put("cedula", new QueryValue("%" + cedula.trim().toUpperCase() + "%"));
		}
		if (estado != null) {
			if (estado.equalsIgnoreCase("INACTIVO")) {
				elWhere += " and EmpleadoPuesto.persona.activo is false ";
			} else {
				elWhere += " and EmpleadoPuesto.persona.activo is true ";
			}
		}
		sql +=
			elWhere
				+ " group by Persona.paisByIdPaisExpedicionDoc.descripcion, Persona.idPersona,	Persona.paisByIdPaisExpedicionDoc,	Persona.ciudadByIdCiudadDirecc,	Persona.ciudadByIdCiudadNac,	Persona.datosEspecificos,	Persona.nombres,"
				+ "	Persona.apellidos,	Persona.documentoIdentidad,	Persona.EMail,	Persona.sexo,	Persona.estadoCivil,	Persona.fechaNacimiento,	Persona.departamentoNro,"
				+ "	Persona.piso,	Persona.callePrincipal,	Persona.primeraLateral,	Persona.segundaLateral,"
				+ "	Persona.direccionLaboral,	Persona.otrasDirecciones,	Persona.telefonos,"
				+ "	Persona.activo,	Persona.usuAlta,	Persona.fechaAlta,	Persona.usuMod,"
				+ "	Persona.fechaMod,	Persona.observacion,	Persona.barrio,"
				+ "	Persona.esFuncionario,	Persona.datosEspecificosMotivo,	Persona.obsMotivo ,"
				+ "	Persona.usuAltaOee,	Persona.fechaAltaOee,	Persona.usuModOee,"
				+ "	Persona.fechaModOee,	Persona.tipo,	Persona.grupoSanguineoAbo,"
				+ "	Persona.grupoSanguineoRh,	Persona.datosEspecificosEtnia,	Persona.documentos, Persona.telefonoPart, Persona.telefonoLab, "
				//rveron, 20140613
				+ " Persona.telContacto, Persona.tienePariente, Persona.cantPersonasACargo ,Persona.EMailFuncionario, Persona.enfermo, Persona.documentoSangre";

		gestionarLegajo660ListCustom.listaResultados(sql, parametros);
	}

	public String limpiar() {
		idPais = null;
		nombres = null;
		apellidos = null;
		cedula = null;
		estado = null;
		cargarCabecera();
		search();
		return "";
	}

	public void imprimir() throws Exception {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU678");
		if (cargarParametros() == null)
			return;

		reportUtilFormController.imprimirReportePdf();

	}

	private String cargarParametros() throws Exception{
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);

		Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
		reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
		reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
		reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
		
		ConfiguracionUoCab oeeusu=em.find(ConfiguracionUoCab.class,usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo() );
		reportUtilFormController.getParametros().put("oeeUsuarioLogueado", oeeusu.getDenominacionUnidad().toUpperCase());
		String where =" Where E.ACTIVO = TRUE";
		where+=" and OEE.ID_CONFIGURACION_UO="+nivelEntidadOeeUtil.getIdConfigCab();
		
		if(estado!=null){
			if (!sufc.validarInput(estado.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			reportUtilFormController.getParametros().put("estado",estado);
			if(estado.equalsIgnoreCase("ACTIVO"))
				where+=" and  PER.ACTIVO=true";
			else
				where+=" and  PER.ACTIVO=false";
		}
		if(nombres!=null&& !nombres.trim().equals("")){
			if (!sufc.validarInput(nombres.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			reportUtilFormController.getParametros().put("nombres",nombres);
			where+=" and lower(PER.NOMBRES) like '%"+nombres.toLowerCase()+"%'";
			
			
		}
		if(apellidos!=null&& !apellidos.trim().equals("")){
			if (!sufc.validarInput(apellidos.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			reportUtilFormController.getParametros().put("apellidos",apellidos);
			where+=" and lower(PER.APELLIDOS) like '%"+apellidos.toLowerCase()+"%'";
		}
			
		if(cedula!=null&& !cedula.trim().equals("")){
			if (!sufc.validarInput(cedula.toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			reportUtilFormController.getParametros().put("ci",cedula);
			where+=" and PER.DOCUMENTO_IDENTIDAD =  '"+cedula+"'";
		}
		if(idPais!=null){
			Pais pais=em.find(Pais.class, idPais);
			reportUtilFormController.getParametros().put("paisExp",pais.getDescripcion());
			where+=" and PAIS.id_pais = "+idPais;
		}
			
		where+=" ORDER BY  sne.nen_codigo,ENTIDAD.ent_codigo,oee.orden,uo, ESTADO, PER.NOMBRES ";
		reportUtilFormController.getParametros().put("where",where);
		return "ok";

	}

	private void cargarCabecera() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.init2();
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

}
