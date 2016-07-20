package py.com.excelsis.sicca.seleccion.session.form;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.ProcesoEnum;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("verificarModifFechaSFPListFormController")
public class VerificarModifFechaSFPListFormController implements Serializable{
	
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
	SolicProrrogaDetList solicProrrogaDetList;
	
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	

	private SolicProrrogaDet solicProrrogaDet;
	private Long idConcursoPuestoAgr;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;//enviado por el CU
	private String descripcion;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idSolicCab;
	private SolicProrrogaCab solicProrrogaCab;
	private int selectedRow = -1;
	private List<SolicProrrogaDet> prorrogaDetList;
	private Long idExcepcion;
	Excepcion excepcion= new Excepcion();
	
	public void init(){
//		if(idExcepcion!=null)
//			excepcion=em.find(Excepcion.class, idExcepcion);
//		idSolicCab=excepcion.getSolicProrrogaCab().getIdSolicCab();
		solicProrrogaCab= em.find(SolicProrrogaCab.class, idSolicCab);
		concursoPuestoAgr= new ConcursoPuestoAgr();
		concurso= new Concurso();
		if(solicProrrogaCab.getFechasGrupoPuesto()!=null ){
			if(solicProrrogaCab.getFechasGrupoPuesto().getConcursoPuestoAgr()!=null)
				concursoPuestoAgr= em.find(ConcursoPuestoAgr.class,solicProrrogaCab.getFechasGrupoPuesto().getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		
			if(solicProrrogaCab.getFechasGrupoPuesto().getConcurso()!=null){
				concurso=em.find(Concurso.class,solicProrrogaCab.getFechasGrupoPuesto().getConcurso().getIdConcurso());
				findEntidades();
			}
		}
		
		searchAll();
		if(!prorrogaDetList.isEmpty())
			selectedRow=prorrogaDetList.size()-1;
		
		
	}
	
	
	

	public void searchAll(){
		descripcion=null;
		solicProrrogaDetList.setSinEntidad(sinEntidad);
		solicProrrogaDetList.setSinNivelEntidad(sinNivelEntidad);
		solicProrrogaDetList.setIdSolicCab(idSolicCab);
		solicProrrogaDetList.setConfiguracionUoCab(configuracionUoCab);
		prorrogaDetList=solicProrrogaDetList.listaResultadosCU261();
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
	
	public boolean habRpta(Long id){
		SolicProrrogaDet d= em.find(SolicProrrogaDet.class,id);
		if( d.getRespuesta()==null || d.getRespuesta().trim().equals(""))
			return true;
		
		return false;
	}
	
	public String autorizarRechazar(){ 
		//se verifica para la ultima fila
		SolicProrrogaDet lastDet= prorrogaDetList.get(prorrogaDetList.size()-1);
		if(lastDet.getRespuesta()==null || lastDet.getRespuesta().trim().equals("")
			|| !solicProrrogaCab.getEstado().equals("PENDIENTE APROBACION")){
			statusMessages.add("Debe registrar una respuesta para Autorizar/Rechazar la solicitud");
			return null;
		}
	
		if(lastDet.getAceptaSfp()){
			solicProrrogaCab.setEstado("APROBADO");
			em.merge(solicProrrogaCab);
			FechasGrupoPuesto fechasGrupoPuesto= em.find(FechasGrupoPuesto.class, solicProrrogaCab.getFechasGrupoPuesto().getIdFechas());
			if(solicProrrogaCab.getFechaVigHastaNuev()!=null)
				fechasGrupoPuesto.setFechaVigProcHasta(solicProrrogaCab.getFechaVigHastaNuev());
			if(solicProrrogaCab.getFechaVigHastaAnt()!=null)
				fechasGrupoPuesto.setFechaVigHastaAnt(solicProrrogaCab.getFechaVigHastaAnt());
			em.merge(fechasGrupoPuesto);
			lastDet.setEnviadoSfp(true);
			em.merge(lastDet);
			em.flush();
			excepcion.setEstado("APROBADO");
			excepcion.setUsuAprobacion(usuarioLogueado.getCodigoUsuario());
			excepcion.setFechaAprobacion(new Date());
			em.merge(excepcion);
			em.flush();
			
		}else{
			solicProrrogaCab.setEstado("PENDIENTE REVISION");
			lastDet.setEnviadoSfp(true);
			em.merge(solicProrrogaCab);
			em.merge(lastDet);
			em.flush();
			//Se pasa a la siguiente tarea
			jbpmUtilFormController.setExcepcion(excepcion);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EXCEPCION);
			jbpmUtilFormController.setActividadActual(ActividadEnum.EXCEP_APROBAR_SFP);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_REVISAR_OEE);
			jbpmUtilFormController.setTransition("next");
			
			if (jbpmUtilFormController.nextTask()){
				em.flush();
			}
		}
		
		
	
	
		
		
		
	
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "ok";
	}
	public String publicar(){
		if(solicProrrogaCab.getUsuPublic()!=null){
			statusMessages.add("El registro ya se ha publicado, verifique");
			return null;
		}
		solicProrrogaCab.setUsuPublic(usuarioLogueado.getCodigoUsuario());
		solicProrrogaCab.setFechaPublic(new Date());
		em.merge(solicProrrogaCab);
		
		
		excepcion.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
		excepcion.setFechaPublicacion(new Date());
		em.merge(excepcion);
		em.flush();
		
		//Se finaliza  la tarea
		jbpmUtilFormController.setExcepcion(excepcion);
		jbpmUtilFormController.setActividadActual(ActividadEnum.EXCEP_APROBAR_SFP);
		jbpmUtilFormController.setTransition("end");
		jbpmUtilFormController.nextTask();
		
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		
		
		return "ok";
	}
	
	
	
	public boolean esPendienteAp(){
		if(solicProrrogaCab.getEstado().equals("PENDIENTE APROBACION"))
			return true;
		return false;
	}
	
	public boolean esAprobado(){
		if(solicProrrogaCab.getEstado().equals("APROBADO"))
			return true;
		return false;
	}

	public boolean esPublicado(){
		if(solicProrrogaCab.getEstado().equals("PUBLICADO"))
			return true;
		return false;
	}
	
	public String getRowClass(int currentRow)
    {
        if(selectedRow == currentRow)
        {
            return "selectedRow" ;
        }
        return "notSelectedRow" ;
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



	


	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}



	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}



	public Long getIdSolicCab() {
		return idSolicCab;
	}



	public void setIdSolicCab(Long idSolicCab) {
		this.idSolicCab = idSolicCab;
	}



	public SolicProrrogaCab getSolicProrrogaCab() {
		return solicProrrogaCab;
	}



	public void setSolicProrrogaCab(SolicProrrogaCab solicProrrogaCab) {
		this.solicProrrogaCab = solicProrrogaCab;
	}



	public int getSelectedRow() {
		return selectedRow;
	}



	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}



	public List<SolicProrrogaDet> getProrrogaDetList() {
		return prorrogaDetList;
	}



	public void setProrrogaDetList(List<SolicProrrogaDet> prorrogaDetList) {
		this.prorrogaDetList = prorrogaDetList;
	}



	public SolicProrrogaDet getSolicProrrogaDet() {
		return solicProrrogaDet;
	}



	public void setSolicProrrogaDet(SolicProrrogaDet solicProrrogaDet) {
		this.solicProrrogaDet = solicProrrogaDet;
	}




	public Long getIdExcepcion() {
		return idExcepcion;
	}




	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	
	
	
	
	

	

	
	
	
}
