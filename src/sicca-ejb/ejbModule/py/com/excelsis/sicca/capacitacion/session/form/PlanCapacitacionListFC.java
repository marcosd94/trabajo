package py.com.excelsis.sicca.capacitacion.session.form;



import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.capacitacion.session.PlanCapacitacionList;
import py.com.excelsis.sicca.entity.AdjuntosCap;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("planCapacitacionListFC")
public class PlanCapacitacionListFC {
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
	@In(create = true)
	PlanCapacitacionList planCapacitacionList;

	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;


	private Long anio;
	private Long nro;
	private Boolean primeraEntrada=true;

	


	public void init() {
		if(primeraEntrada)
		{
			nivelEntidadOeeUtil.limpiar();
			primeraEntrada=false;
			if(usuarioLogueado.getConfiguracionUoDet()!=null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
			
		
		cargarCabecera();
		search();
	}

	
	public void searchAll(){
	
		
		if(usuarioLogueado.getConfiguracionUoDet()!=null){
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
			planCapacitacionList.setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		}else{
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
			planCapacitacionList.setIdConfiguracionUoDet(null);
		}
			
		anio=null;
		nro=null;
		nivelEntidadOeeUtil.init();
		planCapacitacionList.limpiarCU495();
		
		
	}
	
	public void search(){
		planCapacitacionList.getPlanCapacitacion().setAnio(anio);
		planCapacitacionList.getPlanCapacitacion().setNro(nro);
		planCapacitacionList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		planCapacitacionList.setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		planCapacitacionList.listaResultados();
	}
	
	
	@SuppressWarnings("unchecked")
	public void plantillaPlanAnual(){
		List<AdjuntosCap> adjuntosCaps= em.createQuery("Select a from AdjuntosCap a " +
				" where a.tipo='PLPA'").getResultList();
		String msg=null;
		if(!adjuntosCaps.isEmpty()){
			msg=AdmDocAdjuntoFormController.abrirDocumentoFromCU( adjuntosCaps.get(0).getDocumentos().getIdDocumento(),usuarioLogueado.getIdUsuario());
		}
		if(msg!=null && !msg.equals("OK"))
			statusMessages.add(Severity.ERROR,msg);
	}
	@SuppressWarnings("unchecked")
	public void plantillaEjcucion(){
		List<AdjuntosCap> adjuntosCaps= em.createQuery("Select a from AdjuntosCap a " +
				" where a.tipo='PLEJ'").getResultList();
		String msg=null;
		if(!adjuntosCaps.isEmpty()){
			msg=AdmDocAdjuntoFormController.abrirDocumentoFromCU( adjuntosCaps.get(0).getDocumentos().getIdDocumento(),usuarioLogueado.getIdUsuario());
		}
		if(msg!=null && !msg.equals("OK"))
			statusMessages.add(Severity.ERROR,msg);
	}
	@SuppressWarnings("unchecked")
	public void plantillaContrato(){
		List<AdjuntosCap> adjuntosCaps= em.createQuery("Select a from AdjuntosCap a " +
				" where a.tipo='MODC'").getResultList();
		String msg=null;
		if(!adjuntosCaps.isEmpty()){
			msg=AdmDocAdjuntoFormController.abrirDocumentoFromCU( adjuntosCaps.get(0).getDocumentos().getIdDocumento(),usuarioLogueado.getIdUsuario());
		}
		if(msg!=null && !msg.equals("OK"))
			statusMessages.add(Severity.ERROR,msg);
	}
	public void cargarCabecera(){
		
		
		//Nivel
		
		ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti= em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad=em.find(SinEntidad.class, enti.getSinEntidad().getIdSinEntidad());
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(enti.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());
		
		
		nivelEntidadOeeUtil.init();
		
	}
	

	
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public Long getAnio() {
		return anio;
	}


	public void setAnio(Long anio) {
		this.anio = anio;
	}


	public Long getNro() {
		return nro;
	}


	public void setNro(Long nro) {
		this.nro = nro;
	}


	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}
	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	


	
	

	
}
