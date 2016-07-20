package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.ComisionSeleccionCabDTO;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ComisionSeleccionCabList;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetList;
import py.com.excelsis.sicca.seleccion.session.MatrizDocConfigDetList;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("homologPerfilMatrizDetListFormController")
public class HomologPerfilMatrizDetListFormController implements Serializable{
	
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
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	HomologacionPerfilMatrizDetList homologacionPerfilMatrizDetList;

	
	private Long idConcursoPuestoAgr;//recibe del CU que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;//enviado por el CU
	private String descripcion;
	private List<HomologacionPerfilMatrizDet> perfilMatrizDetList= new ArrayList<HomologacionPerfilMatrizDet>();
	private ConcursoPuestoAgr concursoPuestoAgr;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String from;

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
		perfilMatrizDetList= em.createQuery("Select c from HomologacionPerfilMatrizDet c " +
				" where c.homologacionPerfilMatriz.concursoPuestoAgr.idConcursoPuestoAgr="+idConcursoPuestoAgr +" and c.activo=true").getResultList();
		homologacionPerfilMatrizDetList.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		homologacionPerfilMatrizDetList.listaResultados();
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
	public void  enviarAjuste(){
		List<HomologacionPerfilMatrizDet> selecionado= new ArrayList<HomologacionPerfilMatrizDet>();
		em.clear();
		List<HomologacionPerfilMatrizDet> bd= em.createQuery("Select c from HomologacionPerfilMatrizDet c " +
				" where c.homologacionPerfilMatriz.concursoPuestoAgr.idConcursoPuestoAgr="+idConcursoPuestoAgr).getResultList();
		
		for (int i = 0; i < bd.size(); i++) {
			if(!bd.get(i).getAjusteEnviado() && perfilMatrizDetList.get(i).getAjusteEnviado())
				selecionado.add(perfilMatrizDetList.get(i));
		}
		if(selecionado.isEmpty()){
			statusMessages.add("Debe seleccionar alguna observacion para poder enviar, verifique");
			return;
		}
		for (int i = 0; i < selecionado.size(); i++) {
			HomologacionPerfilMatrizDet aux=em.find(HomologacionPerfilMatrizDet.class, selecionado.get(i).getIdHomologacionDet());
			aux.setAjusteEnviado(true);
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
	
	
	@SuppressWarnings("unchecked")
	private int findEstado(){
		List<Referencias> ref= em.createQuery("select r from Referencias r " +
				" where r.dominio like 'ESTADOS_GRUPO'" +
				" and r.descAbrev like 'PENDIENTE REVISION'").getResultList();
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
	
	public void imprimirPerfilMatrizDelGrupo(Long idConcursoPuestoAgr) {
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



	public String getFrom() {
		return from;
	}



	public void setFrom(String from) {
		this.from = from;
	}
	
	
	
}
