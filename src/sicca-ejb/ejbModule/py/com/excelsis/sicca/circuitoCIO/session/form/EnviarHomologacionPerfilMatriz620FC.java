package py.com.excelsis.sicca.circuitoCIO.session.form;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("enviarHomologacionPerfilMatriz620FC")
public class EnviarHomologacionPerfilMatriz620FC  implements Serializable {

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
	ConcursoPuestoAgrList concursoPuestoAgrList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idConcurso;// enviado por el CU
	private Concurso concurso;
	private String obs;
	private String nombrePantalla = "EnviarAHomologacionConcursoInterno";
	private String direccionFisica;
	private String entity;
	private Long idConcursoPuestoAgr;
	private String estado;
	private boolean eviadoHomo;
	private HomologacionPerfilMatriz homologacionPerfilMatriz;
	private String obsSfp;
	private Date fechaSfp;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean hayObservacionSFP;
	private Boolean hayObservacionFirma=false;
	private String selectedTab = null;
	private List<HomologacionPerfilMatrizDet> listaObsRptas = new ArrayList<HomologacionPerfilMatrizDet>();
	private String codActividad;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		String estado=seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PENDIENTE REVISION")+"#"+seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "ENVIADO A HOMOLOGACION");
		seguridadUtilFormController.verificarPerteneceOeeCIO(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), estado, ActividadEnum.CIO_HOMOLOGACION_OEE.getValor());
	}
	
	public boolean canEdit(Long id){
		
		HomologacionPerfilMatrizDet d1 = listaObsRptas.get(listaObsRptas.size()-1);
		HomologacionPerfilMatrizDet d= em.find(HomologacionPerfilMatrizDet.class,id);
		return (d1.equals(d) && hayObservacionSFP==false)? true : false;
			
	}
	
	@SuppressWarnings("unchecked")
	public void listar() {
		listaObsRptas = new ArrayList<HomologacionPerfilMatrizDet>();
		homologacionPerfilMatriz.getIdHomologacion();
		listaObsRptas = em
				.createQuery(
						"Select c from HomologacionPerfilMatrizDet c "
								+ " where c.homologacionPerfilMatriz.idHomologacion= :id "
								+ "and c.firmaResolucion = false and c.activo=true order by c.nroObs")
				.setParameter("id",
						homologacionPerfilMatriz.getIdHomologacion())
				.getResultList();
		}

	@SuppressWarnings("unchecked")
	public void init() {

		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		validarOee();
		try {
			eviadoHomo = enviadoHomologacion();
			findEntidades();// Trae las entidades segun el grupo que se envio
			searchAll();
			anexar();
			findEstado();
			findObservaciones();
			List<HomologacionPerfilMatriz> matrizs =
				em.createQuery("select h from HomologacionPerfilMatriz h "
					+ " where h.concursoPuestoAgr.idConcursoPuestoAgr=" + idConcursoPuestoAgr).getResultList();

			if (!matrizs.isEmpty()){
				homologacionPerfilMatriz = matrizs.get(0);
				listar();
			}
			ultimaObs();
			enviadoHomologacion();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public Boolean verificar(){
		return (codActividad.equals("HOMOLOGACION_OEE"))? true : false;
		
	}
	public boolean habRpta(Long id){
		HomologacionPerfilMatrizDet d= em.find(HomologacionPerfilMatrizDet.class,id);
		if(d.getNroRpta()==null&& d.getRespuesta()==null)
			return true;
		
		return false;
	}

	public void searchAll() {
		concursoPuestoAgrList.setIdConcurso(idConcurso);
		concursoPuestoAgrList.listaResultados();
	}

	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab =
			em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad =
				em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}

	public void imprimirPerfilMatriz() {
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idConcursoPuestoAgr);
		reportUtilFormController.imprimirReportePdf();
	}

	@SuppressWarnings("unchecked")
	public String homologar() {
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());

			Date actual = new Date();
			Calendar calCalendario = Calendar.getInstance();
			calCalendario.setTimeInMillis(actual.getTime());
			// se suma un año a la fecha
			calCalendario.add(Calendar.YEAR, 1);

			List<HomologacionPerfilMatriz> h =
				em.createQuery(" select h from  HomologacionPerfilMatriz h "
					+ " where h.concursoPuestoAgr.idConcursoPuestoAgr=" + idConcursoPuestoAgr).getResultList();

			if (!h.isEmpty()) {
				HomologacionPerfilMatriz matriz = h.get(0);
				matriz.setUsuHomolog(usuarioLogueado.getCodigoUsuario());
				matriz.setFechaHomolog(new Date());
				matriz.setNumero(maxNum());
				matriz.setAnio(Integer.parseInt(anio));
				matriz.setEstado("HOMOLOGADO");
				matriz.setFechaVencimiento(calCalendario.getTime());
				em.merge(matriz);
				em.flush();
			}
			concursoPuestoAgr.setEstado(estadoHomologado());
			em.merge(concursoPuestoAgr);
			EstadoDet ed = eHomologado();
			eviadoHomo = enviadoHomologacion();
			List<PlantaCargoDet> plantaCargoDets =
				em.createQuery("Select d.plantaCargoDet from ConcursoPuestoDet d"
					+ " where d.concursoPuestoAgr.idConcursoPuestoAgr= " + idConcursoPuestoAgr).getResultList();

			for (int i = 0; i < plantaCargoDets.size(); i++) {
				if (ed != null) {
					PlantaCargoDet aux =
						em.find(PlantaCargoDet.class, plantaCargoDets.get(i).getIdPlantaCargoDet());
					aux.setEstadoCab(ed.getEstadoCab());
					aux.setEstadoDet(ed);
					aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
					aux.setFechaMod(new Date());
					em.merge(aux);
					em.flush();
				}
			}
			Observacion observacion = new Observacion();
			observacion.setObservacion(obs);
			observacion.setFechaAlta(new Date());
			observacion.setConcurso(em.find(Concurso.class, idConcurso));
			observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(observacion);
			em.flush();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private EstadoDet eHomologado() {
		List<EstadoDet> dets =
			em.createQuery("Select d from EstadoDet d "
				+ " where d.descripcion like 'HOMOLOGADO' and "
				+ " d.estadoCab.descripcion like 'CONCURSO' ").getResultList();
		if (dets.isEmpty())
			return null;
		else
			return dets.get(0);
	}

	private Integer maxNum() {
		List<Integer> num =
			em.createQuery("Select max(h.numero) from HomologacionPerfilMatriz h ").getResultList();
		if (!num.isEmpty() && num.get(0) != null)
			return num.get(0) + 1;
		else
			return 1;
	}

	@SuppressWarnings("unchecked")
	private Integer estadoHomologado() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'HOMOLOGADO'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	public String nextTask() {

		if (esPendienteRevision()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU36_msgnext"));
			return null;
		}
		if (!enviadoHomologacion()) {
			statusMessages.add("Debe estar con estado ENVIADO A HOMOLOGACIÓN. Verifique");
			return null;
		}

		Observacion observacion = new Observacion();
		observacion.setObservacion(obs);
		observacion.setFechaAlta(new Date());
		observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		observacion.setConcurso(em.find(Concurso.class, idConcurso));
		observacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
		em.persist(observacion);
		em.flush();
		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CIO_HOMOLOGACION_OEE);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CIO_FIRMA_RESOL_HOMOLOG);
		jbpmUtilFormController.setTransition("next");

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}

		return "next";

	}

	public String back() {
		statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "back";
	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "HomologacionPerfilMatriz";
		direccionFisica =
			"//SICCA//" + anio + "//OEE//" + configuracionUoCab.getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso;

	}

	/**
	 * true ENVIADO A HOMOLOGACION del grupo
	 */
	@SuppressWarnings("unchecked")
	public boolean enviadoHomologacion() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'ENVIADO A HOMOLOGACION'" + "  and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();

		return !ref.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private void findEstado() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();
		if (!ref.isEmpty())
			estado = ref.get(0).getDescAbrev();
	}

	/**
	 * true es pendiente de revision
	 */
	@SuppressWarnings("unchecked")
	public boolean esPendienteRevision() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'PENDIENTE REVISION'" + "  and r.valorNum ="
				+ concursoPuestoAgr.getEstado()).getResultList();
		return !ref.isEmpty();
	}

	private void ultimaObs() {
		String qry =
			" SELECT max(hom_det.nro_obs), hom_det.observacion, hom_det.fecha_obs "
				+ " FROM  seleccion.homologacion_perfil_matriz_det hom_det "
				+ " WHERE  hom_det.activo = true" + " AND hom_det.id_homologacion = "
				+ homologacionPerfilMatriz.getIdHomologacion()
				+ " GROUP BY hom_det.observacion, hom_det.fecha_obs ";

		List<Object> result = em.createNativeQuery(qry).getResultList();
		if (!result.isEmpty()) {
			for (Object obj : (List<Object>) result) {
				Object[] record = (Object[]) obj;
				obsSfp = (String) record[1];
				fechaSfp = (Date) record[2];
			}

		}
	}
private void findObservaciones() {
		
		try{
		BigInteger count1, count2;
		String s = "select count(*) from seleccion.homologacion_perfil_matriz_det "
				+ "where firma_resolucion is true and respuesta is null and activo is true "
				+ "and id_homologacion in (select id_homologacion "
				+ "from seleccion.homologacion_perfil_matriz "
				+ "where id_concurso_puesto_agr= " + idConcursoPuestoAgr + ")";

		count1 = (BigInteger) em.createNativeQuery(s).getResultList().get(0);
		String s2= "select count(*) from seleccion.homologacion_perfil_matriz_det "
				+"where firma_resolucion is false and respuesta is null and activo is true "
				+"and id_homologacion in (select id_homologacion " 
				+ "from seleccion.homologacion_perfil_matriz " 
				+"where id_concurso_puesto_agr = "+idConcursoPuestoAgr+")";
		
	
		
		
		count2 = (BigInteger) em.createNativeQuery(s2).getResultList().get(0);

		hayObservacionFirma = (count1.intValue() > 0)? true : false;
		hayObservacionSFP = (count2.intValue() > 0)? true : false;

		eviadoHomo = enviadoHomologacion();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// GETTERS Y SETTERS

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

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public ConcursoPuestoAgrList getConcursoPuestoAgrList() {
		return concursoPuestoAgrList;
	}

	public void setConcursoPuestoAgrList(ConcursoPuestoAgrList concursoPuestoAgrList) {
		this.concursoPuestoAgrList = concursoPuestoAgrList;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean isEviadoHomo() {
		return eviadoHomo;
	}

	public void setEviadoHomo(boolean eviadoHomo) {
		this.eviadoHomo = eviadoHomo;
	}

	public HomologacionPerfilMatriz getHomologacionPerfilMatriz() {
		return homologacionPerfilMatriz;
	}

	public void setHomologacionPerfilMatriz(HomologacionPerfilMatriz homologacionPerfilMatriz) {
		this.homologacionPerfilMatriz = homologacionPerfilMatriz;
	}

	public String getObsSfp() {
		return obsSfp;
	}

	public void setObsSfp(String obsSfp) {
		this.obsSfp = obsSfp;
	}

	public Date getFechaSfp() {
		return fechaSfp;
	}

	public void setFechaSfp(Date fechaSfp) {
		this.fechaSfp = fechaSfp;
	}
	
	public Boolean getHayObservacionSFP() {
		return hayObservacionSFP;
	}

	public void setHayObservacionSFP(Boolean hayObservacionSFP) {
		this.hayObservacionSFP = hayObservacionSFP;
	}

	public Boolean getHayObservacionFirma() {
		return hayObservacionFirma;
	}

	public void setHayObservacionFirma(Boolean hayObservacionFirma) {
		this.hayObservacionFirma = hayObservacionFirma;
	}
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public List<HomologacionPerfilMatrizDet> getListaObsRptas() {
		return listaObsRptas;
	}

	public void setListaObsRptas(List<HomologacionPerfilMatrizDet> listaObsRptas) {
		this.listaObsRptas = listaObsRptas;
	}
	
	public String getCodActividad() {
		return codActividad;
	}
	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

}
