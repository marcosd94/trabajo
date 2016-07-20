package py.com.excelsis.sicca.evaluacion.session.form;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.excseleccion.session.ExcepcionListCustom;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("histCalifFunc579FC")
public class HistCalifFunc579FC {
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
	HistCalifFuncListCustom histCalifFuncListCustom;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private Long idPais;
	private String nombres;
	private String apellidos;
	private String cedula;
	private String cargoActual;
	private Long idEmpleadoPuesto;
	private boolean primeraVez = true;

	public void init() {
		if (primeraVez) {
			cargarCabecera();
			primeraVez = false;
			search();
		} else {
			cargarCabecera2();
			search();
		}

	}

	private void cargarCabecera() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.init2();
	}

	private void cargarCabecera2() {
		nivelEntidadOeeUtil.init();
	}

	private Boolean precondSearch() {
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null
			|| nivelEntidadOeeUtil.getIdSinEntidad() == null
			|| nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}

		return true;
	}

	public void search() {
		String sql = "select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto ";

		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		if (!precondSearch())
			return;
		String elWhere =
			" where EmpleadoPuesto.activo is true and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo ";
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

		if (cargoActual != null && cargoActual.equalsIgnoreCase("SI")) {
			elWhere += " and EmpleadoPuesto.actual = :actual ";
			parametros.put("actual", new QueryValue(true));
		}
		if (cargoActual != null && cargoActual.equalsIgnoreCase("NO")) {
			elWhere += " and EmpleadoPuesto.actual = :actual ";
			parametros.put("actual", new QueryValue(false));
		}

		sql += elWhere;
		histCalifFuncListCustom.listaResultados(sql, parametros);
	}

	private void cargarParametros() {
		ConfiguracionUoCab configuracionUoCab =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		EmpleadoPuesto ep = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU579_imprimirHistCalifFunc");
		reportUtilFormController.getParametros().put("idUo", ep.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		reportUtilFormController.getParametros().put("idPersona", ep.getPersona().getIdPersona());
		reportUtilFormController.getParametros().put("cedula", ep.getPersona().getDocumentoIdentidad());
		reportUtilFormController.getParametros().put("puesto", ep.getPlantaCargoDet().getDescripcion());
		reportUtilFormController.getParametros().put("funcionario", ep.getPersona().getNombres()
			+ " " + ep.getPersona().getApellidos());
		reportUtilFormController.getParametros().put("oee", configuracionUoCab.getDenominacionUnidad());
		reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());

	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);

		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	public String limpiar() {
		idPais = null;
		nombres = null;
		apellidos = null;
		cedula = null;
		cargoActual = null;
		primeraVez = true;
		cargarCabecera();
		search();
		return "";
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

	public String getCargoActual() {
		return cargoActual;
	}

	public void setCargoActual(String cargoActual) {
		this.cargoActual = cargoActual;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

}
