package py.com.excelsis.sicca.evaluacion.session.form;


import java.util.Date;

import java.util.List;
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

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("planMejoraEditFC")
public class PlanMejoraEditFC {
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
	
		
	private PlanMejora planMejora= new PlanMejora();
	private Boolean modoUpdate;
	private Long idPlanMejora;
	private SeguridadUtilFormController seguridadUtilFormController;
	

	public void init() throws Exception {
		em.clear();
		cargarCabecera();
		if(idPlanMejora!=null){
			if (seguridadUtilFormController == null) {
				seguridadUtilFormController =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			if (!seguridadUtilFormController.validarInput(idPlanMejora.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			planMejora= em.find(PlanMejora.class, idPlanMejora);
			seguridadUtilFormController.validarPerteneceOEE(planMejora.getConfiguracionUo().getIdConfiguracionUo());
			modoUpdate=true;
		
		}else{
			planMejora= new PlanMejora();
			modoUpdate=false;
			
		}
	}
	
	public void initVer() throws Exception {
		
		cargarCabecera();
		if(idPlanMejora!=null){
			if (seguridadUtilFormController == null) {
				seguridadUtilFormController =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			if (!seguridadUtilFormController.validarInput(idPlanMejora.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			planMejora= em.find(PlanMejora.class, idPlanMejora);
			modoUpdate=true;
			nivelEntidadOeeUtil.setIdConfigCab(planMejora.getConfiguracionUo().getIdConfiguracionUo());
		}else{
			planMejora= new PlanMejora();
			modoUpdate=false;
			
		}
	}
	
	
	

	public void cargarCabecera(){
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
	}
	
	
	@SuppressWarnings("unchecked")
	private boolean preConSaves(){
		
		if(planMejora.getDescripcion().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Criterio Plan de Mejora antes de realizar esta acción. Verifique");
			return false;
		}

		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if(seguridadUtilFormController.contieneCaracter(planMejora.getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(planMejora.getIdentificador().longValue()<0){
			statusMessages.add(Severity.ERROR,"El codigo debe ser mayor a cero. Verifique");
			return false;
		}
		String sql="Select p from PlanMejora p ";
		String sqlCodigo=sql+" where p.configuracionUo.idConfiguracionUo=:idOee and p.activo=true and p.identificador=:identificador";
		String sqlDescripcion=sql+" where p.configuracionUo.idConfiguracionUo=:idOee and p.activo=true and lower(p.descripcion)=:crit";
		String sqlCodDesOee=sql+" where p.configuracionUo.idConfiguracionUo=:idOee and p.activo=true and lower(p.descripcion)=:crit" +
		" and p.identificador=:identificador ";
		if(idPlanMejora!=null){
			String sqlId=" and p.idPlanMejora!="+idPlanMejora.longValue();
			sqlCodDesOee+=sqlId;
			sqlCodigo+=sqlId;
			sqlDescripcion+=sqlId;
		}
		
		List<PlanMejora> mejoraCodigo=em.createQuery(sqlCodigo)
				.setParameter("idOee", nivelEntidadOeeUtil.getIdConfigCab()).setParameter("identificador", planMejora.getIdentificador()).getResultList();
		if(!mejoraCodigo.isEmpty()){
			statusMessages.add(Severity.ERROR,"Ya existe el código especificado. Verifique");
			return false;
		}
		List<PlanMejora> mejoradescripcion=em.createQuery(sqlDescripcion)
		.setParameter("idOee", nivelEntidadOeeUtil.getIdConfigCab()).setParameter("crit", planMejora.getDescripcion().trim().toLowerCase()).getResultList();
		if(!mejoradescripcion.isEmpty()){
			statusMessages.add(Severity.ERROR,"Ya existe el Criterio del Plan de Mejora especificado. Verifique");
			return false;
		}
		List<PlanMejora> mejoraExite=em.createQuery(sqlCodDesOee)
		.setParameter("idOee", nivelEntidadOeeUtil.getIdConfigCab())
		.setParameter("crit", planMejora.getDescripcion().trim().toLowerCase())
		.setParameter("identificador", planMejora.getIdentificador()).getResultList();
		if(!mejoraExite.isEmpty()){
			statusMessages.add(Severity.ERROR,"Ya existe el registro especificado. Verifique");
			return false;
		}

		return true;
	}
	
	public String save() {
		try {
			if(!preConSaves())
				return null;
			
			planMejora.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			planMejora.setFechaAlta(new Date());
			planMejora.setConfiguracionUo(new ConfiguracionUoCab());
			planMejora.getConfiguracionUo().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
			planMejora.setActivo(true);
			em.persist(planMejora);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	
	
	}
	public String update() {
		try {
			if(!preConSaves())
				return null;
			planMejora.setFechaMod(new Date());
			planMejora.setFechaMod(new Date());
			em.merge(planMejora);
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";	
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		
		
}

	
	@SuppressWarnings("unchecked")
	private int estadoCarga(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'CARGA'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	
	
	private void validarOee(Long id) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (id == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(id)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	private void validarUoDet(Long id) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (id == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceUoDet(id)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}
	public Boolean getModoUpdate() {
		return modoUpdate;
	}


	public void setModoUpdate(Boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

	public PlanMejora getPlanMejora() {
		return planMejora;
	}

	public void setPlanMejora(PlanMejora planMejora) {
		this.planMejora = planMejora;
	}

	public Long getIdPlanMejora() {
		return idPlanMejora;
	}

	public void setIdPlanMejora(Long idPlanMejora) {
		this.idPlanMejora = idPlanMejora;
	}




	
	

	
}
