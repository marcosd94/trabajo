package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.evaluacion.session.EvaluacionDesempenoList;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admEvalDesempFC")
public class AdmEvalDesempFC implements Serializable {

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
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Boolean esAdministradorSFP = false;
	
	private Long idEvalDesempeno;
	private Long idSujeto;
	private String nombreFuncionarioEvaluacionDesempenoRapida;
	private Float puntajeFuncionarioEvaluacionDesempenoRapida;
	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private Sujetos sujetoEvaluado = new Sujetos();
	private GruposSujetos sujetoEvalRapida = new GruposSujetos();
	private Referencias referencias = new Referencias();

	public void init() throws Exception {
		evaluacionDesempeno = new EvaluacionDesempeno();
		referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "CARGA");
		if (idEvalDesempeno != null) {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);

			if (!sufc.validarInput(idEvalDesempeno.toString(),
					TiposDatos.LONG.getValor(), null))
				return;

			evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
					idEvalDesempeno);

			validarOee(evaluacionDesempeno
					.getConfiguracionUoCab().getIdConfiguracionUo());
		}
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		cargarAdministradorSFP();

	}

	public void initVer() throws Exception {
		evaluacionDesempeno = new EvaluacionDesempeno();
		referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_EVALUACION_DESEMPENO", "CARGA");
		if (idEvalDesempeno != null) {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);

			if (!sufc.validarInput(idEvalDesempeno.toString(),
					TiposDatos.LONG.getValor(), null))
				return;

			evaluacionDesempeno = em.find(EvaluacionDesempeno.class,
					idEvalDesempeno);

			
		}
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		cargarAdministradorSFP();

	}
	
	public void initPuntajeSujetoEvaluacionRapida() throws Exception {
		
		sujetoEvaluado= em.find(Sujetos.class, idSujeto);
		if (sujetoEvaluado != null){
			nombreFuncionarioEvaluacionDesempenoRapida = sujetoEvaluado.getEmpleadoPuesto().getPersona().getNombreCompleto();
			evaluacionDesempeno = new EvaluacionDesempeno();
			referencias = referenciasUtilFormController.getReferencia("ESTADOS_EVALUACION_DESEMPENO", "CARGA");
			idEvalDesempeno = sujetoEvaluado.getEvaluacionDesempeno().getIdEvaluacionDesempeno();
			/*if (idEvalDesempeno != null) {
				SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);

				if (!sufc.validarInput(idEvalDesempeno.toString(),TiposDatos.LONG.getValor(), null))
					return;

				evaluacionDesempeno = em.find(EvaluacionDesempeno.class,idEvalDesempeno);

			}*/
			if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
				nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
				cargarCabecera();
			}
			String sqlPuntaje = "select * from eval_desempeno.grupos_sujetos where grupos_sujetos.id_sujetos = "+ idSujeto;
			Query qPuntaje = em.createNativeQuery(sqlPuntaje, GruposSujetos.class);
			
			if(!qPuntaje.getResultList().isEmpty()){
				GruposSujetos sujetoEvaluado = (GruposSujetos) qPuntaje.getResultList().get(0);
				puntajeFuncionarioEvaluacionDesempenoRapida = sujetoEvaluado.getPuntajeFinal();
			}
			cargarAdministradorSFP();
		}

	}

	private void validarOee(Long id) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (id == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(id)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * Verifica si un usuario puede ver todas las OEE o no
	 * 
	 * @return
	 */
	private void cargarAdministradorSFP() {
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador()) {
				esAdministradorSFP = true;
				return;
			}
		}

		esAdministradorSFP = false;
	}

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
		// Unidad Organizativa
		if (idEvalDesempeno == null) {
			if (usuarioLogueado.getConfiguracionUoDet() != null
					&& usuarioLogueado.getConfiguracionUoDet()
							.getIdConfiguracionUoDet() != null)

				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		} //else
//			nivelEntidadOeeUtil.setIdUnidadOrganizativa(evaluacionDesempeno
//					.getConfiguracionUoDet().getIdConfiguracionUoDet());

		nivelEntidadOeeUtil.init();
	}

	private boolean check() {
		if (evaluacionDesempeno.getTitulo() == null
				|| evaluacionDesempeno.getTitulo().trim().isEmpty())
			return false;
		else
			return true;
	}
	
	private boolean checkGuardarPuntaje() {
		if (puntajeFuncionarioEvaluacionDesempenoRapida == null)
			return false;
		else
			return true;
	}

	public String save() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese el campo obligatorio Titulo");
			return null;
		}
		evaluacionDesempeno.setEstado(referencias.getValorNum());
		evaluacionDesempeno.setActivo(true);
		evaluacionDesempeno.setFechaAlta(new Date());
		evaluacionDesempeno.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			evaluacionDesempeno.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
		try {
			em.persist(evaluacionDesempeno);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			return null;
		}

	}
	
	public String saveEvaluacionRapida() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese el campo obligatorio Titulo");
			return null;
		}
		evaluacionDesempeno.setEstado(referencias.getValorNum());
		evaluacionDesempeno.setActivo(true);
		evaluacionDesempeno.setFechaAlta(new Date());
		evaluacionDesempeno.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		evaluacionDesempeno.setEsEvaluacionRapida(true);
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			evaluacionDesempeno.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
		try {
			em.persist(evaluacionDesempeno);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			return null;
		}

	}
	
	public String savePuntajeEvaluacionRapida() {
		if (!checkGuardarPuntaje()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese el campo obligatorio Puntaje");
			return null;
		}
		sujetoEvalRapida.setSujetos(sujetoEvaluado);
		sujetoEvalRapida.setActivo(true);
		sujetoEvalRapida.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		sujetoEvalRapida.setFechaAlta(new Date());
		sujetoEvalRapida.setPuntajeFinal(puntajeFuncionarioEvaluacionDesempenoRapida);
		
		try {
			em.persist(sujetoEvalRapida);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			return null;
		}

	}

	public String updated() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese el campo obligatorio Titulo");
			return null;
		}
		
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			evaluacionDesempeno.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
		evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
		evaluacionDesempeno.setFechaMod(new Date());
		try {
			em.merge(evaluacionDesempeno);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			return null;
		}

	}
	
	public String updatedEvaluacionRapida() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese el campo obligatorio Titulo");
			return null;
		}
		
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			evaluacionDesempeno.setConfiguracionUoCab(em.find(
					ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
		evaluacionDesempeno.setUsuMod(usuarioLogueado.getCodigoUsuario());
		evaluacionDesempeno.setFechaMod(new Date());
		try {
			em.merge(evaluacionDesempeno);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			return null;
		}

	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}

	public Long getIdEvalDesempeno() {
		return idEvalDesempeno;
	}

	public void setIdEvalDesempeno(Long idEvalDesempeno) {
		this.idEvalDesempeno = idEvalDesempeno;
	}
	
	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public String getNombreFuncionarioEvaluacionDesempenoRapida() {
		return nombreFuncionarioEvaluacionDesempenoRapida;
	}

	public void setNombreFuncionarioEvaluacionDesempenoRapida(String nombreFuncionarioEvaluacionDesempenoRapida) {
		this.nombreFuncionarioEvaluacionDesempenoRapida = nombreFuncionarioEvaluacionDesempenoRapida;
	}
	
	public Float getPuntajeFuncionarioEvaluacionDesempenoRapida() {
		return puntajeFuncionarioEvaluacionDesempenoRapida;
	}

	public void setPuntajeFuncionarioEvaluacionDesempenoRapida(Float puntajeFuncionarioEvaluacionDesempenoRapida) {
		this.puntajeFuncionarioEvaluacionDesempenoRapida = puntajeFuncionarioEvaluacionDesempenoRapida;
	}
	
	public Sujetos getSujetoEvaluado(){
		return sujetoEvaluado;
	}
	
	public void setSujetoEvaluado(Sujetos sujetoEvaluado){
		this.sujetoEvaluado = sujetoEvaluado;
	}
	
	public GruposSujetos getSujetoEvalRapida(){
		return sujetoEvalRapida;
	}
	
	public void setSujetoEvalRapida(GruposSujetos sujetoEvalRapida){
		this.sujetoEvalRapida = sujetoEvalRapida;
	}
	
	public Referencias getReferencias() {
		return referencias;
	}

	public void setReferencias(Referencias referencias) {
		this.referencias = referencias;
	}

}
