package py.com.excelsis.sicca.desvinculacion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import enums.TiposDatos;

import py.com.excelsis.sicca.desvinculacion.session.DesvinculacionList;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("registrarDesvinculacionListFormController")
public class RegistrarDesvinculacionListFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	DesvinculacionList desvinculacionList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;

	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Persona persona = new Persona();

	private Integer ordenUnidOrg;
	private Long idMotivo;
	private Date fechaDesde;
	private Date fechaHasta;
	private Long idPais;

	/**
	 * Inicializa los datos
	 */
	public void init() {
	
		idPais = idParaguay();
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		search();
	}

	/**
	 * Busca el id pertenciente a Paraguay en la tabla pais
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	/**
	 * Carga los datos necesarios para obtener nivel-entidad-oee del usuario logueado
	 */
	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController
				.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController
				.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

/**
 * Método que se ejecuta al presionar el botón buscar
 */
	public void search() {
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			desvinculacionList.setOee(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));

		if (persona != null
				&& ((persona.getNombres() != null && !persona.getNombres()
						.trim().isEmpty())
						|| (persona.getNombres() != null && !persona
								.getNombres().trim().isEmpty()) || (persona
						.getDocumentoIdentidad() != null && !persona
						.getDocumentoIdentidad().trim().isEmpty())))
			desvinculacionList.setPersona(persona);

		if (idMotivo != null)
			desvinculacionList.setIdMotivo(idMotivo);
		if (idPais != null)
			desvinculacionList.setIdPais(idPais);
		if (fechaDesde != null && fechaHasta != null) {
			if (fechaDesde.after(fechaHasta)) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Las fechas ingresadas no son válidas");

			} else {
				desvinculacionList.setFechaDesde(fechaDesde);
				desvinculacionList.setFechaHasta(fechaHasta);
			}
		}
		desvinculacionList.listaResultadosCU440();
	}

	/**
	 * Metodo que inicializa los filtros de busqueda
	 */
	public void clean() {

		persona = new Persona();
		idMotivo = null;
		fechaDesde = null;
		fechaHasta = null;
		cargarCabecera();
		search();
	}

	public void print() {
		Connection conn = null;
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			StringBuffer sqlwhere = new StringBuffer();

			sqlwhere.append(" where 1=1");

			if (fechaDesde != null) {
				
				sqlwhere.append(" and DES.FECHA_ALTA >= to_date('"
						+ sdf.format(fechaDesde) + "','DD-MM-YYYY')");
			}
			if (fechaHasta != null) {
				sqlwhere.append(" AND DES.FECHA_ALTA <= to_date('"
						+ sdf.format(fechaHasta) + "','DD-MM-YYYY')");
			}

			if (persona.getNombres() != null
					&& !persona.getNombres().trim().equals("")) {
				if (!sufc.validarInput(persona.getNombres(), TiposDatos.STRING.getValor(), null)) {
					return ;
				}
				sqlwhere.append(" and lower(PERSONA.NOMBRES) like '%"
						+sufc.caracterInvalido( persona.getNombres().trim().toLowerCase()) + "%'");
				param.put("nombre",persona.getNombres());
			}
			if (persona.getApellidos() != null
					&& !persona.getApellidos().trim().equals("")) {
				if (!sufc.validarInput(persona.getApellidos(), TiposDatos.STRING.getValor(), null)) {
					return ;
				}
				sqlwhere.append(" and lower(PERSONA.APELLIDOS) like '%"
						+ sufc.caracterInvalido(persona.getApellidos().trim().toLowerCase()) + "%'");
				param.put("apellido",persona.getApellidos());
			}
			if (persona.getDocumentoIdentidad() != null
					&& !persona.getDocumentoIdentidad().trim().equals("")) {
				if (!sufc.validarInput(persona.getDocumentoIdentidad(), TiposDatos.STRING.getValor(), null)) {
					return ;
				}
				sqlwhere.append("  and  PERSONA.DOCUMENTO_IDENTIDAD like '"
						+ sufc.caracterInvalido(persona.getDocumentoIdentidad()) + "'");
				param.put("documento",persona.getDocumentoIdentidad());
				
			}
			if (idMotivo != null) {
				sqlwhere.append("  and  DES.ID_DATOS_ESPECIF_MOTIVO ="
						+ idMotivo);
			}
			if (nivelEntidadOeeUtil.getIdConfigCab() != null) {
				sqlwhere.append(" and OEE.ID_CONFIGURACION_UO = "
						+ nivelEntidadOeeUtil.getIdConfigCab());
			}
			if(idPais!=null){
				sqlwhere.append(" and PERSONA.id_pais_expedicion_doc ="+idPais);
				Pais p=em.find(Pais.class, idPais);
				param.put("pais",p.getDescripcion());
			}
			SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
			sqlwhere.append(" Order  by nivel_cod,entidad_nombre,oee");
			
			param.put("where", sqlwhere.toString());
			if(nivelEntidadOeeUtil.getCodNivelEntidad()!=null){
				String codNivel = nivelEntidadOeeUtil.getCodNivelEntidad()+ "";
				param.put("nivel", codNivel + "-" + nivelEntidadOeeUtil.getNombreNivelEntidad());
				
				if(nivelEntidadOeeUtil.getCodSinEntidad()!=null){
					param.put("entidad", codNivel + "." + nivelEntidadOeeUtil.getCodSinEntidad()
							+ "-" + nivelEntidadOeeUtil.getNombreSinEntidad());
					
					if(nivelEntidadOeeUtil.getOrdenUnidOrg()!=null){
						param.put("oee",
								codNivel + "." + nivelEntidadOeeUtil.getCodSinEntidad() + "."
										+ nivelEntidadOeeUtil.getOrdenUnidOrg() + "-"
										+ nivelEntidadOeeUtil.getDenominacionUnidad());
					}
				}
				
			}
			
			if (fechaDesde != null)
				param.put("desde", formatoDeFecha.format(fechaDesde));
			if (fechaHasta != null)
				param.put("hasta", formatoDeFecha.format(fechaHasta));
			if (idMotivo != null) {
				DatosEspecificos motivo = em.find(DatosEspecificos.class,
						idMotivo);
				param.put("motivo", motivo.getDescripcion());
			}

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));

			 conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU450_liq_funcionarios", param,
					false, conn, usuarioLogueado);
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}


	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
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

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

}
