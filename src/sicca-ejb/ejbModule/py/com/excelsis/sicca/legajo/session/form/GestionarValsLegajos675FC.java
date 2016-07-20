package py.com.excelsis.sicca.legajo.session.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.AuditLegajo;
import py.com.excelsis.sicca.legajo.session.GestionarLegajo660ListCustom;
import py.com.excelsis.sicca.legajo.session.GestionarValsLegajos675ListCustom;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarValsLegajos675FC")
public class GestionarValsLegajos675FC {
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
	GestionarValsLegajos675ListCustom gestionarValsLegajos675ListCustom;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	private Long idPais;
	private String nombres;
	private String apellidos;
	private String cedula;
	private Date fechaDesde;
	private Date fechaHasta;

	public void init() {
		cargarCabecera();
		search();
	}

	private Boolean precondSearch() {
		if (fechaDesde != null && fechaHasta == null) {
			statusMessages.add(Severity.ERROR, "Indique la Fecha Hasta");
			return false;
		}
		if (fechaHasta != null && fechaDesde == null) {
			statusMessages.add(Severity.ERROR, "Indique la Fecha Desde");
			return false;
		}

		if (fechaDesde != null && fechaHasta != null) {
			if (fechaDesde.getTime() > fechaHasta.getTime()) {
				statusMessages.add(Severity.ERROR, "La Fecha Desde no puede ser mayor a la Fecha Hasta");
				return false;
			}
		}
		return true;
	}

	public void search() {
		if (!precondSearch())
			return;

		String sql =
			"select  Persona from EmpleadoPuesto EmpleadoPuesto , Persona Persona , AuditLegajo AuditLegajo ";

		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();

		String elWhere =
			" where EmpleadoPuesto.persona.idPersona = Persona.idPersona and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo and EmpleadoPuesto.persona.idPersona = AuditLegajo.persona.idPersona and  AuditLegajo.estado = 2 ";
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
		if (fechaDesde != null && fechaHasta != null) {
			elWhere +=
				" and AuditLegajo.fechaMod >= :fechaDesde and  AuditLegajo.fechaMod<= :fechaHasta";
			parametros.put("fechaDesde", new QueryValue(fechaDesde, TemporalType.DATE));
			parametros.put("fechaHasta", new QueryValue(fechaHasta, TemporalType.DATE));
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
				+ "	Persona.grupoSanguineoRh,	Persona.datosEspecificosEtnia,	Persona.documentos, Persona.telContacto, Persona.telefonoLab, Persona.telefonoPart,"
				+ " Persona.tienePariente";
		if (gestionarValsLegajos675ListCustom == null)
			gestionarValsLegajos675ListCustom =
				(GestionarValsLegajos675ListCustom) Component.getInstance(GestionarValsLegajos675ListCustom.class, true);
		gestionarValsLegajos675ListCustom.listaResultados(sql, parametros);
	}

	public String calcFechaModif(Long idPersona) {
		Query q =
			em.createQuery("select AuditLegajo from AuditLegajo AuditLegajo where AuditLegajo.persona.idPersona = :idPersona ");
		q.setParameter("idPersona", idPersona);
		List<AuditLegajo> lista = q.getResultList();
		if (lista.size() > 0) {
			AuditLegajo al = lista.get(0);
			if (al.getFechaMod() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				return sdf.format(al.getFechaMod());
			}
		}
		return null;
	}

	public String limpiar() {
		idPais = null;
		nombres = null;
		apellidos = null;
		cedula = null;
		fechaDesde = null;
		fechaHasta = null;
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

	private String cargarParametros() throws Exception {

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

}
