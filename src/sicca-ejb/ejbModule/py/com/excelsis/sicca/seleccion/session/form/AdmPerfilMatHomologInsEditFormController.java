package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
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
import enums.Estado;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admPerfilMatHomologInsEditFormController")
public class AdmPerfilMatHomologInsEditFormController implements Serializable{

	/**
	 * 
	 */
//	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	
	@In(create =  true)
	HomologacionPerfilMatrizDetHome homologacionPerfilMatrizDetHome;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	
	
	private Long idHomologacionDet;
	private HomologacionPerfilMatrizDet homologacionPerfilMatrizDet;
	private Long idConcursoPuestoAgr;
	

	private boolean fromRpta;
	private boolean esFirma=false;
	private boolean hayObservacionSFP=false;
	private boolean hayObservacionesFirma=false;
	private String fromActividad;
	private Boolean canEdit=false;
	private Integer estado;
	private Integer estado2;
	private String codActividad;
		
	public void init(){
		try {
			homologacionPerfilMatrizDet= new HomologacionPerfilMatrizDet();
			if(idHomologacionDet!=null){
				homologacionPerfilMatrizDet= em.find(HomologacionPerfilMatrizDet.class,idHomologacionDet);
				homologacionPerfilMatrizDetHome.setInstance(homologacionPerfilMatrizDet);
			}
			
			if(fromRpta){
				homologacionPerfilMatrizDet.setNroRpta(nRpta());
				homologacionPerfilMatrizDet.setFechaRpta(new Date());
				homologacionPerfilMatrizDetHome.setInstance(new HomologacionPerfilMatrizDet());
				homologacionPerfilMatrizDet.setAceptaSfp(true);
			}
				
			hayObs();
//			System.out.println("Entrando en el INIT de admin; corre el método findEstado()... ");
			findEstado();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public Boolean fromOee(){
		if(codActividad == null){

			ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr);
			em.refresh(concursoPuestoAgr);
			if(concursoPuestoAgr.getEstado().intValue() ==  6){//PENDIENTE DE REVISION SEGUN TABLA REFERENCIAS
					codActividad = ActividadEnum.HOMOLOGACION_OEE.getValor();
					return true;
			}else 
					return false;
			
		}
		else{

			return codActividad.equals("HOMOLOGACION_OEE")? true : false;}
		
	}
	
	public String redirect(){ 
		return "/seleccion/homologacionPerfilMatriz/EnviarHomologacionPerfil.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+ idConcursoPuestoAgr + "&actividad=HOMOLOGACION_OEE";
		// view="/seleccion/homologacionPerfilMatriz/EnviarHomologacionPerfil.xhtml"
		
	}
	public String redirectCIO(){ 
		return "/circuitoCIO/enviarHomologacion620/EnviarHomologacionPerfilMatriz620.seam?concursoPuestoAgrIdConcursoPuestoAgr="
				+ idConcursoPuestoAgr + "&actividad=HOMOLOGACION_OEE";
		// view="/seleccion/homologacionPerfilMatriz/EnviarHomologacionPerfil.xhtml"
		
	}
	private void hayObs(){
		BigInteger count,count2;
		Integer count3;
		String s= "select count(*) from seleccion.homologacion_perfil_matriz_det "
				+"where firma_resolucion is false and respuesta is null and activo is true "
				+"and id_homologacion= (select id_homologacion " 
				+ "from seleccion.homologacion_perfil_matriz " 
				+"where id_concurso_puesto_agr = "+idConcursoPuestoAgr+")";
	
		count = (BigInteger) em.createNativeQuery(s).getResultList().get(0);
		setHayObservacionSFP((count.intValue() > 0)? true : false);
		
		
		String s2 = "select count(*) from seleccion.homologacion_perfil_matriz_det "
				+ "where firma_resolucion is true and respuesta is null and activo is true "
				+ "and id_homologacion= (select id_homologacion "
				+ "from seleccion.homologacion_perfil_matriz "
				+ "where id_concurso_puesto_agr= " + idConcursoPuestoAgr + ")";
		count2 = (BigInteger) em.createNativeQuery(s2).getResultList().get(0);
		hayObservacionesFirma= (count2.intValue() > 0)? true : false;
		
		String s3="select estado from seleccion.concurso_puesto_agr  where id_concurso_puesto_agr="+idConcursoPuestoAgr;
		count3 = (Integer) em.createNativeQuery(s3).getResultList().get(0);
		estado = count3;
		
	}
	
	public Boolean esFirma(){
		
		String q="select firma_resolucion from seleccion.homologacion_perfil_matriz_det "
				+ "where id_homologacion_det = "+idHomologacionDet;
		esFirma=(Boolean) em.createNativeQuery(q).getResultList().get(0);
		return esFirma;
	}
	
	/*private Boolean esFirma(){
		String s="select count(*) from seleccion.homologacion_perfil_matriz_det "
				+ "where firma_resolucion is true and id_homologacion= "
				+ "(select id_homologacion from seleccion.homologacion_perfil_matriz "
				+ "where id_concurso_puesto_agr= "+idConcursoPuestoAgr+")";
		Long count = (Long)em.createNativeQuery(s).getResultList().get(0);
		esFirma=(count.intValue() > 0)? true : false;
	return esFirma;
	}*/
	// /////AGREGADO JD 25/11//////////
	public String guardar(){
		return (fromOee())? save() : saveFirma();
	}
	
	public String saveFirma(){
		try {
			if(homologacionPerfilMatrizDet.getRespuesta().trim().equals(""))
			{
				statusMessages.add(Severity.ERROR,"Debe ingresar una respuesta");
				return null;
			}
			
			homologacionPerfilMatrizDet.setUsuRpta(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaRpta(new Date());
			homologacionPerfilMatrizDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaMod(new Date());
			homologacionPerfilMatrizDet.setFirmaResolucion(true);//punto crítico acá
			em.merge(homologacionPerfilMatrizDet);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			//acá
//			System.out.println("saveFirma - toPerfilMatriz");
			return "toPerfilMatriz";//para el retorno al formulario correspondiente en la interacción SFP; Firma Res.;Werner
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error inesperado..");
		}
		return null;
	}

	public String save(){
		try {
			if(homologacionPerfilMatrizDet.getRespuesta().trim().equals(""))
			{
				statusMessages.add(Severity.ERROR,"Debe ingresar una respuesta");
				return null;
			}
			
			homologacionPerfilMatrizDet.setUsuRpta(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaRpta(new Date());
			homologacionPerfilMatrizDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaMod(new Date());
			
			// /////AGREGADO JD 25/11//////////
			homologacionPerfilMatrizDet.setAjusteEnviado(true);
			homologacionPerfilMatrizDet.setAjustadoOee(true);
			homologacionPerfilMatrizDet.setFirmaResolucion(false);
			em.merge(homologacionPerfilMatrizDet);
			if (homologacionPerfilMatrizDet.getHomologacionPerfilMatriz().getConcursoPuestoAgr()
					.getIdConcursoPuestoAgr() != null) {
				ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class, homologacionPerfilMatrizDet
						.getHomologacionPerfilMatriz().getConcursoPuestoAgr()
						.getIdConcursoPuestoAgr());
				agr.setEstado(findEstado());//acá
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				agr.setFechaMod(new Date());
				em.merge(agr);
			}
			// ////////// FIN AGREGADO ////////////////////
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			canEdit=true;
			//acá
//			System.out.println("save - persisted en save()...");
//			System.out.println("en save() código actividad: "+codActividad+" ...");
			return esFirma ? saveToPM() : "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error inesperado..");
		}
		return null;
	}
	public String saveToPM(){
		//para el navigation de admPerfilMatriz....xml
		return"toPerfilMatriz";
	}
	
	
	@SuppressWarnings("unchecked")
	public int findEstado(){
		List<Referencias> ref= em.createQuery("select r from Referencias r " +
				" where r.dominio like 'ESTADOS_GRUPO'" +
				" and r.descAbrev like 'ENVIADO A HOMOLOGACION'").getResultList();
		if(!ref.isEmpty()){
			estado2=ref.get(0).getValorNum();
			return estado2;
		}
		else
			return 0;
		
	}
	
	public String update(){
		try {
			if(homologacionPerfilMatrizDet.getRespuesta().trim().equals(""))
			{
				statusMessages.add(Severity.ERROR,"Debe ingresar una respuesta");
				return null;
			}
			homologacionPerfilMatrizDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaMod(new Date());
			em.merge(homologacionPerfilMatrizDet);
			
			if(!homologacionPerfilMatrizDet.getActivo()){
				HomologacionPerfilMatrizDet copia= new HomologacionPerfilMatrizDet();
				copia.setNroObs(homologacionPerfilMatrizDet.getNroObs());
				copia.setObservacion(homologacionPerfilMatrizDet.getObservacion());
				copia.setActivo(true);
				copia.setFechaObs(homologacionPerfilMatrizDet.getFechaObs());
				copia.setUsuObs(homologacionPerfilMatrizDet.getUsuObs());
				copia.setAjusteEnviado(homologacionPerfilMatrizDet.getAjusteEnviado());
				copia.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				copia.setFechaAlta(new Date());
				//copia.setAjustadoOee(homologacionPerfilMatrizDet.getAjustadoOee());
				copia.setAjustadoOee(true);// JD 25/11
				copia.setHomologacionPerfilMatriz(homologacionPerfilMatrizDet.getHomologacionPerfilMatriz());
				
				
				em.persist(copia);
			}
			
			
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			System.out.println("Que imprime con esa condición.... UPDATE - UPDATED");
			
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error inesperado..");
		}
		return null;
	}

	
	
	
//	METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private Integer nRpta(){
		List<Integer>dets=em.createQuery("Select max(d.nroRpta) from HomologacionPerfilMatrizDet d ").getResultList();

		if(!dets.isEmpty() && dets.get(0)!=null)
			return dets.get(0)+1;
		else
			return 1;
	}
	

//	GETTERS Y SETTERS
	
	public Long getIdHomologacionDet() {
		return idHomologacionDet;
	}

	public void setIdHomologacionDet(Long idHomologacionDet) {
		this.idHomologacionDet = idHomologacionDet;
	}

	public HomologacionPerfilMatrizDet getHomologacionPerfilMatrizDet() {
		return homologacionPerfilMatrizDet;
	}

	public void setHomologacionPerfilMatrizDet(
			HomologacionPerfilMatrizDet homologacionPerfilMatrizDet) {
		this.homologacionPerfilMatrizDet = homologacionPerfilMatrizDet;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public HomologacionPerfilMatrizDetHome getHomologacionPerfilMatrizDetHome() {
		return homologacionPerfilMatrizDetHome;
	}

	public void setHomologacionPerfilMatrizDetHome(
			HomologacionPerfilMatrizDetHome homologacionPerfilMatrizDetHome) {
		this.homologacionPerfilMatrizDetHome = homologacionPerfilMatrizDetHome;
	}

	public boolean isFromRpta() {
		return fromRpta;
	}

	public void setFromRpta(boolean fromRpta) {
		this.fromRpta = fromRpta;
	}
	public boolean isEsFirma() {
		return esFirma;
	}
	public void setEsFirma(boolean esFirma) {
		this.esFirma = esFirma;
	}
	public boolean isHayObservacionSFP() {
		return hayObservacionSFP;
	}
	public void setHayObservacionSFP(boolean hayObservacionSFP) {
		this.hayObservacionSFP = hayObservacionSFP;
	}
	public String getFromActividad() {
		return fromActividad;
	}
	public void setFromActividad(String fromActividad) {
		this.fromActividad = fromActividad;
	}
	public Boolean getCanEdit() {
		return canEdit;
	}
	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}
	public boolean isHayObservacionesFirma() {
		return hayObservacionesFirma;
	}
	public void setHayObservacionesFirma(boolean hayObservacionesFirma) {
		this.hayObservacionesFirma = hayObservacionesFirma;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	public Integer getEstado2() {
		return estado2;
	}
	public void setEstado2(Integer estado2) {
		this.estado2 = estado2;
	}
	public String getCodActividad() {
		return codActividad;
	}
	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	
	
	
}
