package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.io.File;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdminConcursosListFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("concursoCU681FC")
public class ConcursoCU681FC {

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
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ConcursoHome concursoHome;

	private Entidad entidad = new Entidad();
	private Concurso concurso;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Integer ordenUnidOrg;
	private String radioButton;
	private String operacion = null;
	private String ubicacionFisica;
	private String radioPcd;
	private SeguridadUtilFormController seguridadUtilFormController;
	private DatosEspecificos datEspe;
	private Long idConcurso;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	public void init() throws Exception {
		concurso = concursoHome.getInstance();
		idConcurso = concurso.getIdConcurso();
		if (idConcurso != null)
			operacion = "updated";
		else
			operacion = "persisted";
		datEspe = seleccionUtilFormController
				.traerTipoConcursoConcurso("CONCURSO SIMPLIFICADO");
		buscarDatosAsociadosUsuario();
		if (concursoHome.isIdDefined()) {
			validarOee();
			if (concurso.getAdReferendum())
				radioButton = "SI";
			else
				radioButton = "NO";

			if (concurso.getPcd())
				radioPcd = "SI";
			else
				radioPcd = "NO";
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separador = File.separator;
			ubicacionFisica = separador + "SICCA" + separador + anho
					+ separador + "OEE" + separador
					+ configuracionUoCab.getIdConfiguracionUo() + separador
					+ datEspe.getIdDatosEspecificos() + separador
					+ concursoHome.getInstance().getIdConcurso();

		}
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {
			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)
				ordenUnidOrg = configuracionUoCab.getOrden();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * M�todo que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String save() {
		try {
			if (!check()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"LLene los campos requeridos");
				return null;
			}

			concurso.setDatosEspecificosTipoConc(em.find(
					DatosEspecificos.class, datEspe.getIdDatosEspecificos()));
			concurso.setConfiguracionUoCab(configuracionUoCab);
			concurso.setEstado(1);
			concurso.setFechaCreacion(new Date());
			concurso.setActivo(true);

			if (radioButton != null) {
				if (radioButton.equals("SI"))
					concurso.setAdReferendum(true);
				else
					concurso.setAdReferendum(false);
			}

			if (radioPcd.equals("SI"))
				concurso.setPcd(true);
			else
				concurso.setPcd(false);

			concurso.setFechaAlta(new Date());
			concurso.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(concurso);
			em.flush();
			concursoHome.setInstance(concurso);
			idConcurso = concursoHome.getInstance().getIdConcurso();

			operacion = "persisted";
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separator = File.separator;
			ubicacionFisica = separator + "SICCA" + separator + anho
					+ separator + "OEE" + separator
					+ configuracionUoCab.getIdConfiguracionUo() + separator
					+ datEspe.getIdDatosEspecificos() + separator
					+ concursoHome.getInstance().getIdConcurso();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
		return null;
	}

	/**
	 * M�todo que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String update() {
		try {
			if (!check()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"LLene los campos requeridos");
				return null;
			}

			AdminConcursosListFormController adminConcursosListFormController = (AdminConcursosListFormController) Component
					.getInstance(AdminConcursosListFormController.class, true);
			adminConcursosListFormController.calcEstado();
			if (concurso.getEstado() != null
					&& concurso.getEstado().intValue() == adminConcursosListFormController
							.getValorNumEstadoConcurso().intValue()) {
				return null;
			}
			/********/
			concurso.setDatosEspecificosTipoConc(datEspe);
			concurso.setConfiguracionUoCab(configuracionUoCab);
			concurso.setActivo(true);

			if (radioButton != null) {
				if (radioButton.equals("SI"))
					concurso.setAdReferendum(true);
				else
					concurso.setAdReferendum(false);
			}

			if (radioPcd.equals("SI"))
				concurso.setPcd(true);
			else
				concurso.setPcd(false);
			concurso.setFechaMod(new Date());
			concurso.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concurso);

			concursoHome.setInstance(concurso);
			String result = "updated";
			operacion = result;
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}

		return null;
	}

	/**
	 * M�todo que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String delete() {
		try {
			concurso.setActivo(false);
			concursoHome.setInstance(concurso);
			String result = concursoHome.update();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return result;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	private Boolean check() {
		if (radioPcd == null)
			return false;
		if (radioButton == null)
			return false;
		if (seleccionUtilFormController != null) {
			seleccionUtilFormController = (SeleccionUtilFormController) Component
					.getInstance(SeleccionUtilFormController.class, true);

		}

		if (concurso.getNombre().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_descripcion_invalida"));
			return false;
		}

		return true;
	}

	

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
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

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getRadioButton() {
		return radioButton;
	}

	public void setRadioButton(String radioButton) {
		this.radioButton = radioButton;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getRadioPcd() {
		return radioPcd;
	}

	public void setRadioPcd(String radioPcd) {
		this.radioPcd = radioPcd;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	

}
