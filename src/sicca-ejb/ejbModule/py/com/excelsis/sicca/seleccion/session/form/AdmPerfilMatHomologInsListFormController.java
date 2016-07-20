package py.com.excelsis.sicca.seleccion.session.form;
import java.io.Serializable;
import java.util.ArrayList;
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

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetList;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admPerfilMatHomologInsListFormController")
public class AdmPerfilMatHomologInsListFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	HomologacionPerfilMatrizDetList homologacionPerfilMatrizDetList;
	
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	
	private Long idConcursoPuestoAgr;//recibe del CU que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;//enviado por el CU
	private String descripcion;
	private List<HomologacionPerfilMatrizDet> perfilMatrizDetList= new ArrayList<HomologacionPerfilMatrizDet>();
	private ConcursoPuestoAgr concursoPuestoAgr;
	List<HomologacionPerfilMatrizDet> selecionado= new ArrayList<HomologacionPerfilMatrizDet>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	
	public void init(){
	
		concursoPuestoAgr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		concurso=concursoPuestoAgr.getConcurso();
		validarOee(concurso);
		findEntidades();//Trae las entidades segun el grupo que se envio
		searchAll();
		
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void searchAll(){
		descripcion=null;
//		perfilMatrizDetList= em.createQuery("Select c from HomologacionPerfilMatrizDet c " +
//				" where c.homologacionPerfilMatriz.concursoPuestoAgr.idConcursoPuestoAgr="+idConcursoPuestoAgr).getResultList();
		homologacionPerfilMatrizDetList.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		perfilMatrizDetList=homologacionPerfilMatrizDetList.listaResultados();
	}

	@SuppressWarnings("unchecked")
	private void findEntidades(){
		configuracionUoCab=em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo()) ;
		if(configuracionUoCab.getEntidad()!=null){
			sinEntidad=em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin=em.createQuery("Select n from SinNivelEntidad n " +
					" where n.aniAniopre ="+sinEntidad.getAniAniopre() +" and n.nenCodigo="+sinEntidad.getNenCodigo()).getResultList();
			if(!sin.isEmpty())
				sinNivelEntidad=sin.get(0);
			
		}
		
	}
	@SuppressWarnings("unchecked")
	public void  enviarSfp(){
		selecionado= new ArrayList<HomologacionPerfilMatrizDet>();
		em.clear();
		List<HomologacionPerfilMatrizDet> bd= em.createQuery("Select c from HomologacionPerfilMatrizDet c " +
				" where c.homologacionPerfilMatriz.concursoPuestoAgr.idConcursoPuestoAgr="+idConcursoPuestoAgr +" and c.activo=true " +
						" order by c.fechaAlta asc " ).getResultList();
		
		for (int i = 0; i < bd.size(); i++) {
			/*if(bd.get(i).getIdHomologacionDet().intValue()==perfilMatrizDetList.get(i).getIdHomologacionDet().intValue() //JD 25/11
				&&  !bd.get(i).getAjustadoOee() && perfilMatrizDetList.get(i).getAjustadoOee()&& perfilMatrizDetList.get(i).getNroRpta()!=null)*/
			if(bd.get(i).getIdHomologacionDet().intValue()==perfilMatrizDetList.get(i).getIdHomologacionDet().intValue()
					&& perfilMatrizDetList.get(i).getNroRpta()!=null)
				selecionado.add(perfilMatrizDetList.get(i));
		}
		/*if(selecionado.isEmpty()){
			statusMessages.add("Debe Responder previamente la Observación y/o chequear la columna Enviar SFP antes de realizar esta acción. Verifique.");
			return;
		}*/
		for (int i = 0; i < selecionado.size(); i++) {
			HomologacionPerfilMatrizDet aux=em.find(HomologacionPerfilMatrizDet.class, selecionado.get(i).getIdHomologacionDet());
			aux.setAjusteEnviado(true);
			aux.setAjustadoOee(true);
			aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
			aux.setFechaMod(new Date());
			em.merge(aux);
			if(aux.getHomologacionPerfilMatriz().getConcursoPuestoAgr().getIdConcursoPuestoAgr()!=null){
				ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class, aux.getHomologacionPerfilMatriz().getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				agr.setEstado(findEstado());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);
			}
			em.flush();
				
			
			
		}
		
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return ;
	}
	

	
	public void cambio(int idx){
		 System.out.println(perfilMatrizDetList.get(idx).getAjustadoOee());
//		HomologacionPerfilMatrizDet s= perfilMatrizDetList.get(idx);
//		if(perfilMatrizDetList.get(idx).getAjustadoOee()){
//			selecionado.add(s);
//		}else{
//			if(selecionado.contains(s))
//				selecionado.remove(s);
//		}
//		
	}
	public boolean habRpta(Long id){
		HomologacionPerfilMatrizDet d= em.find(HomologacionPerfilMatrizDet.class,id);
		if(d.getNroRpta()==null&& d.getRespuesta()==null)
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private int findEstado(){
		List<Referencias> ref= em.createQuery("select r from Referencias r " +
				" where r.dominio like 'ESTADOS_GRUPO'" +
				" and r.descAbrev like 'ENVIADO A HOMOLOGACION'").getResultList();
		if(!ref.isEmpty())
			return ref.get(0).getValorNum();
		else
			return 0;
	}
	
	public void imprimirPerfilMatriz() {
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idConcursoPuestoAgr);
		reportUtilFormController.imprimirReportePdf();
	}
	
	
//	GETTERS Y SETTERS	
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}


	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}



	public List<HomologacionPerfilMatrizDet> getPerfilMatrizDetList() {
		return perfilMatrizDetList;
	}



	public void setPerfilMatrizDetList(
			List<HomologacionPerfilMatrizDet> perfilMatrizDetList) {
		this.perfilMatrizDetList = perfilMatrizDetList;
	}



	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}



	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}



	public List<HomologacionPerfilMatrizDet> getSelecionado() {
		return selecionado;
	}



	public void setSelecionado(List<HomologacionPerfilMatrizDet> selecionado) {
		this.selecionado = selecionado;
	}

	
	
	
	
	

	

	
	
	
}
