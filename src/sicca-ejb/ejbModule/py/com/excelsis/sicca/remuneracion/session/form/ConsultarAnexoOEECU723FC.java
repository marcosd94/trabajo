package py.com.excelsis.sicca.remuneracion.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinAnxOriginal;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("consultarAnexoOEECU723FC")
public class ConsultarAnexoOEECU723FC {
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
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Integer objGasto;
	private String categoria;
	private Boolean mostrarGrilla;

	private List<SinAnx> listaSinAnx = new ArrayList<SinAnx>();
	private List<SinAnxOriginal> listaSinAnxOriginal = new ArrayList<SinAnxOriginal>();

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		mostrarGrilla = false;
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

	}

	public void cargarCabecera() {

		nivelEntidadOeeUtil.limpiar();
		buscarDatosAsociadosUsuario();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(nivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)

				if (configuracionUoCab.getEntidad() != null)
					entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				idSinNivelEntidad = null;
				return;
			}
		} else {
			nivelEntidad = new SinNivelEntidad();
			idSinNivelEntidad = null;
		}
	}

	public void consultar() {
		listaSinAnx = new ArrayList<SinAnx>();
		listaSinAnxOriginal = new ArrayList<SinAnxOriginal>();
		ConfiguracionUoCab oee = new ConfiguracionUoCab();
		if(nivelEntidadOeeUtil.getIdConfigCab() != null)
			oee = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		if (oee.getCodigoSinarh() == null) {

			mostrarGrilla = false;
			statusMessages.add(Severity.ERROR,
					"El OEE no tiene asignado Código del Sinarh. Verifique");
			return;
		}
		String[] sinarh = oee.getCodigoSinarh().split("\\/");
		String sql1 = "SELECT ANX.* FROM SINARH.SIN_ANX ANX "
				+ "WHERE ANX.NEN_CODIGO||'.'||ANX.ENT_CODIGO||'.'||ANX.TIP_CODIGO||'.'||ANX.PRO_CODIGO IN ('";
		String sql2 = "SELECT ANX.* FROM SINARH.SIN_ANX_ORIGINAL ANX " +
				"WHERE ANX.NEN_CODIGO||'.'||ANX.ENT_CODIGO||'.'||ANX.TIP_CODIGO||'.'||ANX.PRO_CODIGO IN ('";
		for (int i = 0; i < sinarh.length; i++) {
			sql1 += sinarh[i];
			sql2 += sinarh[i];
			if (i < sinarh.length - 1){
				sql1 += "', '";
				sql2 += "', '";
			}
			else
			{
				sql1 += "')";
				sql2 += "')";
			}
		}
		if (objGasto != null){
			sql1 += " AND ANX.OBJ_CODIGO = " + objGasto;
			sql2 += " AND ANX.OBJ_CODIGO = " + objGasto;
		}
		if (categoria != null && !categoria.trim().isEmpty()){
			sql1 += " AND ANX.CTG_CODIGO= '" + categoria.toUpperCase() + "'";
			sql2 += " AND ANX.CTG_CODIGO= '" + categoria.toUpperCase() + "'";
		}
		sql1 += " ORDER BY  ANX.ANI_ANIOPRE, ANX.NEN_CODIGO, ANX.ENT_CODIGO, ANX.TIP_CODIGO, ANX.PRO_CODIGO, ANX.OBJ_CODIGO, ANX.CTG_CODIGO";
		sql2 += " ORDER BY ANX.ANI_ANIOPRE, ANX.NEN_CODIGO, ANX.ENT_CODIGO, ANX.TIP_CODIGO, ANX.PRO_CODIGO, ANX.OBJ_CODIGO, ANX.CTG_CODIGO";

		listaSinAnx = em.createNativeQuery(sql1, SinAnx.class).getResultList();
		listaSinAnxOriginal = em.createNativeQuery(sql2, SinAnxOriginal.class).getResultList();
		mostrarGrilla = true;
	}

	public void limpiar() {
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		entidad = new Entidad();
		configuracionUoCab = new ConfiguracionUoCab();
		idConfigCab = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
		cargarCabecera();
		categoria = null;
		objGasto = null;
		listaSinAnx = new ArrayList<SinAnx>();
		listaSinAnxOriginal = new ArrayList<SinAnxOriginal>();
		mostrarGrilla = false;
	}

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

	public Integer getObjGasto() {
		return objGasto;
	}

	public void setObjGasto(Integer objGasto) {
		this.objGasto = objGasto;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public List<SinAnx> getListaSinAnx() {
		return listaSinAnx;
	}

	public void setListaSinAnx(List<SinAnx> listaSinAnx) {
		this.listaSinAnx = listaSinAnx;
	}

	public List<SinAnxOriginal> getListaSinAnxOriginal() {
		return listaSinAnxOriginal;
	}

	public void setListaSinAnxOriginal(List<SinAnxOriginal> listaSinAnxOriginal) {
		this.listaSinAnxOriginal = listaSinAnxOriginal;
	}

	public Boolean getMostrarGrilla() {
		return mostrarGrilla;
	}

	public void setMostrarGrilla(Boolean mostrarGrilla) {
		this.mostrarGrilla = mostrarGrilla;
	}

}
