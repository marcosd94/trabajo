package py.com.excelsis.sicca.seleccion.session.form;




import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;










import py.com.excelsis.sicca.entity.*;

@Scope(ScopeType.CONVERSATION)
@Name("observacionesRespuestasFirmaResFC")
public class ObservacionesRespuestasFirmaResFC {
	@In(value = "entityManager")
	EntityManager em;

	private Long idConcursoPuestoAgr;
	private String from;
	private String fromActividad;
	private List <HomologacionPerfilMatrizDet> homologacionPerfilMatrizDetList = new ArrayList<HomologacionPerfilMatrizDet>();
	public List <ConcursoPuestoAgr> listaGruposParaFirmar = new ArrayList<ConcursoPuestoAgr>();
	
	private Concurso concurso;
	private Long idConcurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private List<ConcursoPuestoAgr> listaGruposAtrasados = new ArrayList<ConcursoPuestoAgr>();
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	
	public void init(){
		setIdConcurso(obtenerIdConcurso());
		concursoPuestoAgr=em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		concurso=concursoPuestoAgr.getConcurso();
	//listarConcursoPorEstado();
		findEntidades();
		listarObservaciones();
		
	}
	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso
				.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class, configuracionUoCab
					.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin = em.createQuery(
					"Select n from SinNivelEntidad n "
							+ " where n.aniAniopre ="
							+ sinEntidad.getAniAniopre() + " and n.nenCodigo="
							+ sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}
	
	@SuppressWarnings("unchecked")
	public  void listarObservaciones(){
		// Listar Observaciones
				
				String q3 = "select * from seleccion.homologacion_perfil_matriz_det hpmd "
						+ "where hpmd.id_homologacion = (select hpm.id_homologacion "
						+ "from seleccion.homologacion_perfil_matriz hpm "
						+ "where hpm.id_concurso_puesto_agr = "+idConcursoPuestoAgr+") "
						+ "and hpmd.activo is true and hpmd.firma_resolucion is true order by hpmd.nro_obs";
				
				homologacionPerfilMatrizDetList = em.createNativeQuery(q3, HomologacionPerfilMatrizDet.class).getResultList();
	}
	//Agregado; Validación para el botón "Nueva Obs.", en Firma; Werner...
	@SuppressWarnings("unchecked")
	public boolean disableBoton(){
		String consulta = "select * from seleccion.homologacion_perfil_matriz_det hpmd "
				+ "where hpmd.id_homologacion = (select hpm.id_homologacion "
				+ "from seleccion.homologacion_perfil_matriz hpm "
				+ "where hpm.id_concurso_puesto_agr = "+idConcursoPuestoAgr+") "
				+ "and hpmd.activo is true and hpmd.firma_resolucion is true and hpmd.respuesta IS NULL order by hpmd.nro_obs";
		
		List<HomologacionPerfilMatrizDet> homologPer = em.createNativeQuery(consulta)
				.getResultList();
		if (!homologPer.isEmpty()) 
			return true;
		 else 
			 return false;
}
	
	public Long obtenerIdConcurso(){
		String q="select id_concurso from seleccion.concurso_puesto_agr where id_concurso_puesto_agr = "+idConcursoPuestoAgr;
		BigInteger id=null;
		try{
		 id =  (BigInteger) em.createNativeQuery(q).getResultList().get(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return id.longValue();
		
	}
	
	@SuppressWarnings("unchecked")
	public void listarConcursoPorEstado() {
		
	listaGruposParaFirmar = new ArrayList<ConcursoPuestoAgr>();
		String q = "select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
				+ "join concursoPuestoAgr.concurso concurso "
				+ "where concurso.idConcurso = "
				+ idConcurso
				+ " and concursoPuestoAgr.activo is true "
				+ "and concursoPuestoAgr.estado = "
				+ concursoPuestoAgr.getEstado();
		listaGruposParaFirmar = em.createQuery(q).getResultList();
	

		//listaGruposParaFirmar = concursoPuestoAgrList.listaResultadosCU414(q);

		String q2 = "select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
				+ "join concursoPuestoAgr.concurso concurso "
				+ "where concurso.idConcurso = "
				+ idConcurso
				+ " and concursoPuestoAgr.activo is true "
				+ "and concursoPuestoAgr.estado != "
				+ concursoPuestoAgr.getEstado();
		listaGruposAtrasados = new ArrayList<ConcursoPuestoAgr>();
		listaGruposAtrasados = em.createQuery(q2).getResultList();

		//listaGruposAtrasados = concursoPuestoAgrList.listaResultadosCU414(q2);

		


		// homologacionPerfilMatrizDetList.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		// homologacionPerfilMatrizDetList.listaResultados();
	}
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}
	public Long getIdConcurso() {
		return idConcurso;
	}
	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}
	public List<ConcursoPuestoAgr> getListaGruposAtrasados() {
		return listaGruposAtrasados;
	}
	public void setListaGruposAtrasados(List<ConcursoPuestoAgr> listaGruposAtrasados) {
		this.listaGruposAtrasados = listaGruposAtrasados;
	}
	public List<ConcursoPuestoAgr> getListaGruposParaFirmar() {
		return listaGruposParaFirmar;
	}
	public void setListaGruposParaFirmar(List<ConcursoPuestoAgr> listaGruposParaFirmar) {
		this.listaGruposParaFirmar = listaGruposParaFirmar;
	}
	public List<HomologacionPerfilMatrizDet> getHomologacionPerfilMatrizDetList() {
		return homologacionPerfilMatrizDetList;
	}
	public void setHomologacionPerfilMatrizDetList(List<HomologacionPerfilMatrizDet> homologacionPerfilMatrizDetList) {
		this.homologacionPerfilMatrizDetList = homologacionPerfilMatrizDetList;
	}

	public String getFromActividad() {
		return fromActividad;
	}

	public void setFromActividad(String fromActividad) {
		this.fromActividad = fromActividad;
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

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}
	public Concurso getConcurso(){
		return concurso;
	}
	public void setConcurso(Concurso concurso){
		this.concurso=concurso;
	}


}
