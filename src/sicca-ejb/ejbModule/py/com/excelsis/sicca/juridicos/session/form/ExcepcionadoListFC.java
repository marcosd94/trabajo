package py.com.excelsis.sicca.juridicos.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.Estado;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Excepcionados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.juridicos.session.ExcepcionadosList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.OficinaList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("excepcionadoListFC")
public class ExcepcionadoListFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ExcepcionadosList excepcionadosList;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;

	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false)
	Usuario usuarioLogueado;

	private Estado estado = Estado.ACTIVO;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab;
	private BigDecimal nenCod;
	private Integer orden;
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Persona persona = new Persona();
	private Integer anio;
	private boolean primeraEntrada = true;
	private Long idPaisExp;
	private String usuarioAltaExp;
	private Date fechaAltaDesde;
	private Date fechaAltaHasta;
	private Date fechaExcDesde;
	private Date fechaExcHasta;

	public void init() {
		try {
			nivelEntidadOeeUtil.init();
			if (primeraEntrada) {
				primeraEntrada = false;
				nivelEntidadOeeUtil.limpiar();
				idPaisExp = General.getIdParaguay();
			}
			search();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void search() {
		excepcionadosList.getExcepcionados().setPersona(new Persona());

		if (persona.getNombres() != null && !persona.getNombres().trim().equals(""))
			excepcionadosList.getExcepcionados().getPersona().setNombres(persona.getNombres());
		if (persona.getApellidos() != null && !persona.getApellidos().trim().equals(""))
			excepcionadosList.getExcepcionados().getPersona().setApellidos(persona.getApellidos());

		if (persona.getDocumentoIdentidad() != null
			&& !persona.getDocumentoIdentidad().trim().equals(""))
			excepcionadosList.getExcepcionados().getPersona().setDocumentoIdentidad(persona.getDocumentoIdentidad());

		excepcionadosList.setEntCodigo(nivelEntidadOeeUtil.getCodSinEntidad() != null
			? nivelEntidadOeeUtil.getCodSinEntidad() : null);
		excepcionadosList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad() != null
			? nivelEntidadOeeUtil.getCodNivelEntidad() : null);
		if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null)
			excepcionadosList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());

		excepcionadosList.getExcepcionados().setAnio(anio);
		excepcionadosList.setEstado(estado);
		excepcionadosList.setFecExpDesde(fechaExcDesde);
		excepcionadosList.setFecExpHasta(fechaExcHasta);
		excepcionadosList.setFecUsuDesde(fechaAltaDesde);
		excepcionadosList.setFecExpHasta(fechaAltaHasta);
		excepcionadosList.setIdPaisExp(idPaisExp);
		excepcionadosList.getExcepcionados().setUsuAlta(usuarioAltaExp);

		excepcionadosList.listaResultados();

	}

	public String getUrlToPageNivel() {

		return "/planificacion/sinNivelEntidad/SinNivelEntidadList.xhtml?from=juridicos/excepcionados/ExcepcionadosList";
	}

	public String getUrlToPageEntidad() {
		if (idSinNivelEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=juridicos/excepcionados/ExcepcionadosList&codigoNivel="
			+ nivelEntidad.getNenCodigo();
	}

	public String getUrlToPageOee() {
		if (idSinNivelEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}

		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		configuracionUoCabList.limpiar();
		String retorno =
			"/planificacion/searchForms/FindDependencias.xhtml?from=juridicos/excepcionados/ExcepcionadosList&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad=" + sinEntidad.getIdSinEntidad();
		return retorno;
	}

	public void searchAll() {

		estado = Estado.ACTIVO;
		anio = null;
		persona = new Persona();
		fechaAltaDesde = null;
		fechaAltaHasta = null;
		fechaExcHasta = null;
		fechaExcDesde = null;
		usuarioAltaExp = null;
		idPaisExp = General.getIdParaguay();
		excepcionadosList.getExcepcionados().setPersona(persona);
		excepcionadosList.limpiar();
		sinNivelEntidadList.limpiar();
		sinEntidadList.limpiar();
		configuracionUoCabList.limpiar();
		nivelEntidadOeeUtil.limpiar();
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();

		} else
			nivelEntidad = new SinNivelEntidad();
		if (nivelEntidad != null)
			idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
	}

	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null && sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidad.getEntCodigo());
			sinEntidadList.getSinEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			sinEntidad = sinEntidadList.entidadMaxAnho();
			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
				entidadList.getSinEntidad().setIdSinEntidad(sinEntidad.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
				idSinEntidad = sinEntidad.getIdSinEntidad();
			} else
				sinEntidad = new SinEntidad();
		}
	}

	public void findConfigUoCab() {
		if (orden != null) {
			configuracionUoCabList.setEntCodigo(sinEntidad.getEntCodigo());
			configuracionUoCabList.setNenCodigo(nivelEntidad.getNenCodigo());
			configuracionUoCabList.getConfiguracionUoCab().setOrden(orden);
			configuracionUoCab = configuracionUoCabList.configuracionFind();
		}
	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU458_imprimir_excepcionados");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab uo =em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			reportUtilFormController.getParametros().put("oee_usuario", uo.getDenominacionUnidad());
			SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
			// Nivel, Entidad, OEE
			if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
				reportUtilFormController.getParametros().put("sinEntidadCod", nivelEntidadOeeUtil.getCodSinEntidad());
				reportUtilFormController.getParametros().put("entidad", nivelEntidadOeeUtil.getCodNivelEntidad()
					+ "."
					+ nivelEntidadOeeUtil.getCodNivelEntidad()
					+ "-"
					+ nivelEntidadOeeUtil.getNombreSinEntidad());
			} else
				reportUtilFormController.getParametros().put("sinEntidadCod", null);

			if (nivelEntidadOeeUtil.getCodNivelEntidad() != null) {
				reportUtilFormController.getParametros().put("nivelEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
				reportUtilFormController.getParametros().put("nivel", nivelEntidadOeeUtil.getCodNivelEntidad()
					+ "-" + nivelEntidadOeeUtil.getNombreNivelEntidad());
			} else
				reportUtilFormController.getParametros().put("nivelEntidadCod", null);

			if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null) {
				reportUtilFormController.getParametros().put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
				reportUtilFormController.getParametros().put("oee", nivelEntidadOeeUtil.getCodNivelEntidad()
					+ "."
					+ nivelEntidadOeeUtil.getCodNivelEntidad()
					+ "."
					+ nivelEntidadOeeUtil.getCodigoUnidOrgDep()
					+ "-"
					+ nivelEntidadOeeUtil.getDenominacionUnidad());
			} else
				reportUtilFormController.getParametros().put("idOEE", null);

			// Datos de la persona
			String nombres = "%%";
			String apellidos = "%%";
			String documento = "%%";
			String usuAlta = "%%";
			reportUtilFormController.getParametros().put("elWhere", crearWhere());
			if (!Utiles.vacio(persona.getNombres())) {
				nombres = "%" + persona.getNombres() + "%";
				reportUtilFormController.getParametros().put("nom", persona.getNombres());
			}
			if (!Utiles.vacio(persona.getApellidos())) {
				apellidos = "%" + persona.getApellidos() + "%";
				reportUtilFormController.getParametros().put("ape", persona.getApellidos());
			}
			if (!Utiles.vacio(persona.getDocumentoIdentidad()))
				documento = persona.getDocumentoIdentidad();

			/**
			 * ADD incidencia 0001134
			 **/
			if (usuarioAltaExp != null)
				usuAlta = usuarioAltaExp;

			reportUtilFormController.getParametros().put("fechaAltaDesde", fechaAltaDesde != null
				? sdf.format(fechaAltaDesde) : null);
			reportUtilFormController.getParametros().put("fechaAltaHasta", fechaAltaHasta != null
				? sdf.format(fechaAltaHasta) : null);
			reportUtilFormController.getParametros().put("fechaExpDesde", fechaExcDesde != null
				? sdf.format(fechaExcDesde) : null);
			reportUtilFormController.getParametros().put("fechaExpHasta", fechaExcHasta != null
				? sdf.format(fechaExcHasta) : null);
			reportUtilFormController.getParametros().put("idPaisExp", idPaisExp);
			if (idPaisExp != null) {
				Pais pa = em.find(Pais.class, idPaisExp);
				reportUtilFormController.getParametros().put("pais", pa.getDescripcion());
			}

			/**
			 * fin
			 **/

			reportUtilFormController.getParametros().put("nombres", nombres.toLowerCase());
			reportUtilFormController.getParametros().put("apellidos", apellidos.toLowerCase());
			reportUtilFormController.getParametros().put("documento", documento.toLowerCase());
			reportUtilFormController.getParametros().put("usuAlta", usuAlta);

			// Anio, Activo
			reportUtilFormController.getParametros().put("anio", anio);
			reportUtilFormController.getParametros().put("activo", estado.getValor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String crearWhere() {
		StringBuffer SQL = new StringBuffer();

		SQL.append(" WHERE 1=1 ");
		if (!Utiles.vacio(persona.getNombres())) {
			SQL.append(" AND lower(P.NOMBRES) LIKE $P{nombres} ");
		}
		if (!Utiles.vacio(persona.getApellidos())) {
			SQL.append(" AND lower(P.APELLIDOS) LIKE $P{apellidos} ");
		}
		if (!Utiles.vacio(persona.getDocumentoIdentidad()))
			SQL.append(" AND P.DOCUMENTO_IDENTIDAD LIKE $P{documento} ");

		SQL.append(" AND ");
		SQL.append("     ( ");
		SQL.append("         sne.nen_codigo = $P{nivelEntidadCod} ");
		SQL.append("      OR $P{nivelEntidadCod} IS NULL ");
		SQL.append("     ) ");
		SQL.append(" AND ");
		SQL.append("     ( ");
		SQL.append("         se.ent_codigo = $P{sinEntidadCod} ");
		SQL.append("      OR $P{sinEntidadCod} IS NULL ");
		SQL.append("     ) ");
		SQL.append(" AND ");
		SQL.append("     ( ");
		SQL.append("         OEE.ID_CONFIGURACION_UO = $P{idOEE} ");
		SQL.append("      OR $P{idOEE} IS NULL ");
		SQL.append("     ) ");
		SQL.append(" AND ");
		SQL.append("     ( ");
		SQL.append("         ex.anio= $P{anio} ");
		SQL.append("      OR $P{anio} IS NULL ");
		SQL.append("     ) ");
		SQL.append(" AND ");
		SQL.append("     ( ");
		SQL.append("         ex.activo = $P{activo} ");
		SQL.append("      OR $P{activo} IS NULL ");
		SQL.append("     ) ");

		if (fechaAltaDesde != null) {
			SQL.append(" AND ");
			SQL.append("     ( ");
			SQL.append(" to_date(to_char(ex.fecha_alta,'dd/MM/yyyy'),'dd/MM/yyyy') >= to_date($P{fechaAltaDesde},'dd/MM/yyyy')");
			SQL.append("     ) ");
		}

		if (fechaAltaHasta != null) {
			SQL.append(" AND ");
			SQL.append("     ( ");
			SQL.append(" to_date(to_char(ex.fecha_alta,'dd/MM/yyyy'),'dd/MM/yyyy') <= to_date($P{fechaAltaHasta},'dd/MM/yyyy')");
			SQL.append("     ) ");
		}
		if (fechaExcDesde != null) {
			SQL.append(" AND ");
			SQL.append("     ( ");
			SQL.append(" to_date(to_char(ex.fecha,'dd/MM/yyyy'),'dd/MM/yyyy') >= to_date($P{fechaExpDesde},'dd/MM/yyyy')");

			SQL.append("     ) ");
		}

		if (fechaExcHasta != null) {
			SQL.append(" AND ");
			SQL.append("     ( ");
			SQL.append(" to_date(to_char(ex.fecha,'dd/MM/yyyy'),'dd/MM/yyyy') <= to_date($P{fechaExpHasta},'dd/MM/yyyy')");
			SQL.append("     ) ");
		}

		SQL.append(" AND P.usu_alta LIKE $P{usuAlta} ");
		SQL.append(" AND ");
		SQL.append("     ( ");
		SQL.append("         P.id_pais_expedicion_doc =$P{idPaisExp} ");
		SQL.append("      OR $P{idPaisExp} IS NULL ");
		SQL.append("     ) ");
		return SQL.toString();

	}

	@SuppressWarnings("unchecked")
	public List<String> getUsuariosAltas() {
		try {
			return em.createQuery(" SELECT distinct (o.usuAlta) FROM "
				+ Excepcionados.class.getName() + " o "
				+ "WHERE o.activo = true ORDER BY o.usuAlta").getResultList();
		} catch (Exception ex) {
			return new Vector<String>();
		}
	}

	@Factory(value = "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getUsuariosAltasSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (String o : getUsuariosAltas())
			si.add(new SelectItem(o, "" + o));
		return si;
	}

	// GETTERS Y SETTERS

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public BigDecimal getNenCod() {
		return nenCod;
	}

	public void setNenCod(BigDecimal nenCod) {
		this.nenCod = nenCod;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getUsuarioAltaExp() {
		return usuarioAltaExp;
	}

	public void setUsuarioAltaExp(String usuarioAltaExp) {
		this.usuarioAltaExp = usuarioAltaExp;
	}

	public Date getFechaAltaDesde() {
		return fechaAltaDesde;
	}

	public void setFechaAltaDesde(Date fechaAltaDesde) {
		this.fechaAltaDesde = fechaAltaDesde;
	}

	public Date getFechaAltaHasta() {
		return fechaAltaHasta;
	}

	public void setFechaAltaHasta(Date fechaAltaHasta) {
		this.fechaAltaHasta = fechaAltaHasta;
	}

	public Date getFechaExcDesde() {
		return fechaExcDesde;
	}

	public void setFechaExcDesde(Date fechaExcDesde) {
		this.fechaExcDesde = fechaExcDesde;
	}

	public Date getFechaExcHasta() {
		return fechaExcHasta;
	}

	public void setFechaExcHasta(Date fechaExcHasta) {
		this.fechaExcHasta = fechaExcHasta;
	}

}
