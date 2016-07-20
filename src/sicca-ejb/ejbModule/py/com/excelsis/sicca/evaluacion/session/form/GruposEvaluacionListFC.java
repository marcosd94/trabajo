package py.com.excelsis.sicca.evaluacion.session.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposEvaluacion;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.evaluacion.session.GruposEvaluacionList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;



@Scope(ScopeType.CONVERSATION)
@Name("gruposEvaluacionListFC")
public class GruposEvaluacionListFC implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	GruposEvaluacionList gruposEvaluacionList;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	
	private List<GruposEvaluacion> gruposEvaluacionLista= new ArrayList<GruposEvaluacion>();
	private Long idEvaluacionDesempeno= null;
	private String grupo;
	private String evaluacion;
	private String ver;
	private String from;
	

	public void init(){
		try {
			cargarCabecera();
			search();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	public void init2(){
		try {
			cargarCabecera();
			search2();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
		
	}
	
	private void cargarCabecera(){
		EvaluacionDesempeno e=em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		ConfiguracionUoCab uo=em.find(ConfiguracionUoCab.class, e.getConfiguracionUoCab().getIdConfiguracionUo());
		evaluacion=e.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(uo.getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uo.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
	}
	
	public void search(){
		gruposEvaluacionLista= new ArrayList<GruposEvaluacion>();
		gruposEvaluacionList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		gruposEvaluacionList.getGruposEvaluacion().setActivo(true);
		gruposEvaluacionList.getGruposEvaluacion().setGrupo(grupo);
		gruposEvaluacionList.setIdEvaluacionDesempeno(idEvaluacionDesempeno);
		gruposEvaluacionLista=gruposEvaluacionList.listaResultados();
			
	}
	
	public void search2(){
		gruposEvaluacionLista= new ArrayList<GruposEvaluacion>();
		gruposEvaluacionList.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		gruposEvaluacionList.getGruposEvaluacion().setGrupo(grupo);
		gruposEvaluacionList.setIdEvaluacionDesempeno(idEvaluacionDesempeno);
		if(ver!=null && !ver.trim().equals(""))	
			gruposEvaluacionLista=gruposEvaluacionList.listaResultadosCU522BajaVer();
		else{
			List<GruposEvaluacion> g=gruposEvaluacionList.listaResultadosCU522Baja();
			for (GruposEvaluacion gruposEvaluacion : g) {
				if(!gruposEvaluacion.getActivo() && gruposEvaluacion.getMotivoCancelacion()!=null 
						&& !gruposEvaluacion.getMotivoCancelacion().trim().equals("") ){
					gruposEvaluacionLista.add(gruposEvaluacion);
				}else if(gruposEvaluacion.getActivo()){
					gruposEvaluacionLista.add(gruposEvaluacion);
				}
			}
		}
			
	}
	
	
	public void searchAll(){
		grupo=null;
		cargarCabecera();
		search();
		
	}
	public void searchAll2(){
		grupo=null;
		cargarCabecera();
		search();
		
	}
	
	@SuppressWarnings("unchecked")
	public Integer cntFuncionario(Long idGrupoEvaluacion){
		List<GruposSujetos> sujetos=em.createQuery("Select g from GruposSujetos g " +
				" where g.activo = true and g.gruposEvaluacion.idGruposEvaluacion=:idGrupoEval")
				.setParameter("idGrupoEval",idGrupoEvaluacion).getResultList();
		return sujetos.size();
		
	}

	public void eliminar(Long idGrupoEvalucion) {
		try {
			
			GruposEvaluacion gruposEvaluacion=em.find(GruposEvaluacion.class, idGrupoEvalucion);
			
			inactivarGrupoSujeto(gruposEvaluacion.getIdGruposEvaluacion());
			
//			gruposEvaluacion.setActivo(false);
//			gruposEvaluacion.setFechaMod(new Date());
//			gruposEvaluacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			em.merge(gruposEvaluacion);//original
			em.remove(gruposEvaluacion);
			
//			inactivarGrupoSujeto(gruposEvaluacion.getIdGruposEvaluacion());//original
			em.flush();
			search();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void darBaja(int index){
		if(gruposEvaluacionLista.size()<=1){
			statusMessages.add(Severity.ERROR,"El grupo no puede eliminarse ya que es el único que forma parte de la evaluaci&oacute;n");
			return;
		}
		GruposEvaluacion gruposEvaluacion=gruposEvaluacionLista.get(index);
		gruposEvaluacion.setFechaCancelacion(new Date());
		gruposEvaluacion.setUsuCancelacion(usuarioLogueado.getCodigoUsuario());
		gruposEvaluacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		gruposEvaluacion.setFechaMod(new Date());
		gruposEvaluacion.setActivo(false);
		em.merge(gruposEvaluacion);
		
		inactivarGrupoSujeto(gruposEvaluacion.getIdGruposEvaluacion());
		inactivarGrupoMetodologia(gruposEvaluacion.getIdGruposEvaluacion());
		em.flush();
		search();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		
		
	}
	@SuppressWarnings("unchecked")
	private void inactivarGrupoSujeto(Long idGrupoEvalucion){
		List<GruposSujetos> gs=em.createQuery("Select c from GruposSujetos c" +
				" where c.gruposEvaluacion.idGruposEvaluacion=:idGrupoEvalucion")
				.setParameter("idGrupoEvalucion",idGrupoEvalucion).getResultList();
		for (GruposSujetos gruposSujetos : gs) {
//			gruposSujetos.setActivo(false);
			//agregado; Werner.
//			gruposSujetos.getSujetos().setActivo(false);
			//*****************
//			gruposSujetos.setFechaMod(new Date());
//			gruposSujetos.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			em.merge(gruposSujetos);//original
			em.remove(gruposSujetos);
		}
	}
	@SuppressWarnings("unchecked")
	private void inactivarGrupoMetodologia(Long idGrupoEvalucion){
		List<GrupoMetodologia> grupoMetodologias=em.createQuery("Select m from GrupoMetodologia m " +
				" where m.gruposEvaluacion.idGruposEvaluacion=:idGruposEvaluacion")
				.setParameter("idGruposEvaluacion",idGrupoEvalucion).getResultList();
		for (GrupoMetodologia grupoMetodologia : grupoMetodologias) {
			grupoMetodologia.setActivo(false);
			grupoMetodologia.setFechaMod(new Date());
			grupoMetodologia.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(grupoMetodologia);
		}
	}
	
	

//		GETTERS Y SETTERS 

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public String getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public List<GruposEvaluacion> getGruposEvaluacionLista() {
		return gruposEvaluacionLista;
	}

	public void setGruposEvaluacionLista(
			List<GruposEvaluacion> gruposEvaluacionLista) {
		this.gruposEvaluacionLista = gruposEvaluacionLista;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

}
