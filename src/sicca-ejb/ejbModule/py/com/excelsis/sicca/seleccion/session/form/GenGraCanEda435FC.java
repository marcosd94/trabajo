package py.com.excelsis.sicca.seleccion.session.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.drools.lang.DRLParser.compound_operator_return;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("genGraCanEda435FC")
public class GenGraCanEda435FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	private Boolean primeraEntrada = true;

	private String formato;

	public void init() {
		if (primeraEntrada
			|| nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			ConfiguracionUoCab configuracionUoCab =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.setOrdenUnidOrg(configuracionUoCab.getOrden());
			nivelEntidadOeeUtil.setDenominacionUnidad(configuracionUoCab.getDenominacionUnidad());
			nivelEntidadOeeUtil.setIdSinEntidad(configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			nivelEntidadOeeUtil.setCodSinEntidad(configuracionUoCab.getEntidad().getSinEntidad().getEntCodigo());
			nivelEntidadOeeUtil.setNombreSinEntidad(configuracionUoCab.getEntidad().getSinEntidad().getEntNombre());
			if (configuracionUoCab.getEntidad() != null) {
				SinEntidad sinEntidad =
					em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
				List<SinNivelEntidad> sin =
					em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
						+ sinEntidad.getAniAniopre() + " and n.nenCodigo="
						+ sinEntidad.getNenCodigo()).getResultList();
				if (!sin.isEmpty()) {
					nivelEntidadOeeUtil.setIdSinNivelEntidad(sin.get(0).getIdSinNivelEntidad());
					nivelEntidadOeeUtil.setCodNivelEntidad(sin.get(0).getNenCodigo());
					nivelEntidadOeeUtil.setNombreNivelEntidad(sin.get(0).getNenNombre());
				}
			}
			primeraEntrada = false;
		}

	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
	}

	private void cargarParametros() {
		try {
			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			nivelEntidadOeeUtil.init();
		 
			grupoPuestosController =
				(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);

			String codigoNivel =
				nivelEntidadOeeUtil != null && nivelEntidadOeeUtil.getCodNivelEntidad() != null
					? nivelEntidadOeeUtil.getCodNivelEntidad().toString() : null;
			String codigoEntidad =
				nivelEntidadOeeUtil != null && nivelEntidadOeeUtil.getCodSinEntidad() != null
					? codigoNivel + "." + nivelEntidadOeeUtil.getCodSinEntidad().toString() : null;
			String codigoOEE =
				nivelEntidadOeeUtil != null && nivelEntidadOeeUtil.getOrdenUnidOrg() != null
					? codigoEntidad + "." + nivelEntidadOeeUtil.getOrdenUnidOrg().toString() : null;

			reportUtilFormController.getParametros().put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));

			reportUtilFormController.getParametros().put("nivel", codigoNivel + " "
				+ nivelEntidadOeeUtil.getNombreNivelEntidad());
			reportUtilFormController.getParametros().put("entidad", codigoEntidad + " "
				+ nivelEntidadOeeUtil.getNombreSinEntidad());
			reportUtilFormController.getParametros().put("oee", codigoOEE + " "
				+ nivelEntidadOeeUtil.getDenominacionUnidad());

			reportUtilFormController.getParametros().put("idSinEntidad", nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdSinEntidad() != null
				? nivelEntidadOeeUtil.getIdSinEntidad() : null);
			reportUtilFormController.getParametros().put("idOEE", nivelEntidadOeeUtil != null
				&& nivelEntidadOeeUtil.getIdConfigCab() != null
				? nivelEntidadOeeUtil.getIdConfigCab() : null);
			reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
			String elWhere = "";
			if (nivelEntidadOeeUtil != null && nivelEntidadOeeUtil.getIdSinEntidad() != null) {
				elWhere += " AND entidad.id_sin_entidad = " + nivelEntidadOeeUtil.getIdSinEntidad();
			}
			if (nivelEntidadOeeUtil != null && nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
				elWhere += " AND oee.id_configuracion_uo = " + nivelEntidadOeeUtil.getIdConfigCab();
			}
			if (nivelEntidadOeeUtil != null && nivelEntidadOeeUtil.getIdConfigCab() != null) {
				elWhere +=
					" AND nivel.id_sin_nivel_entidad = "
						+ nivelEntidadOeeUtil.getIdSinNivelEntidad();
			}
			reportUtilFormController.getParametros().put("elWhere", elWhere);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean validar() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}
		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdSinNivelEntidad().toString(), TiposDatos.LONG.getValor(), null)) {
			return false;
		}
		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdSinEntidad().toString(), TiposDatos.LONG.getValor(), null)) {
			return false;
		}
		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
			return false;
		}
		return true;
	}

	public void imprimir(String tipo) throws Exception {
		if (tipo != null && tipo.equalsIgnoreCase("GRAFICO")) {
			formato = "PDF";
			if (!validar())
				return;
		}

		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.setNombreReporte("RPT_CU435_cant_personas_grupo_edad");
		cargarParametros();
		if ("EXCEL".equals(formato))
			reportUtilFormController.imprimirReporteXLS();
		else
			reportUtilFormController.imprimirReportePdf();

	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

}
