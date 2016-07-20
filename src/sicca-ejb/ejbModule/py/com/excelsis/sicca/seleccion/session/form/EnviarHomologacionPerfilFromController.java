package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.SinObjList;
import py.com.excelsis.sicca.session.form.AdmCargaGrupoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NumeroLetra;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("enviarHomologacionPerfilFromController")
public class EnviarHomologacionPerfilFromController implements Serializable {

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
	
	@In(create = true)
	HomologacionPerfilMatrizDetList homologacionPerfilMatrizDetList;
	
	@In(create = true)
	SinObjList sinObjList;

	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Long idConcurso;// enviado por el CU
	private Concurso concurso;
	private String obs;
	private String nombrePantalla = "enviarHomologacionPerfil_edit";
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
	private String codActividad;

	private List<HomologacionPerfilMatrizDet> listaObsRptas = new ArrayList<HomologacionPerfilMatrizDet>();
	
	
	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		String estado=seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PENDIENTE REVISION")+"#"+seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "ENVIADO A HOMOLOGACION");
		seguridadUtilFormController.verificarPerteneceOee(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), estado, ActividadEnum.HOMOLOGACION_OEE.getValor());
	}
public boolean canEdit(Long id){
		
		HomologacionPerfilMatrizDet d1 = listaObsRptas.get(listaObsRptas.size()-1);
		HomologacionPerfilMatrizDet d= em.find(HomologacionPerfilMatrizDet.class,id);
		return (d1.equals(d) && hayObservacionSFP==false)? true : false;
			
	}
public Boolean verificar(){
	return (codActividad.equals("HOMOLOGACION_OEE"))? true : false;
	
}
private void finObservaciones() {
	try{
	BigInteger count;
	String s= "select count(*) from seleccion.homologacion_perfil_matriz_det "
			+"where firma_resolucion is false and respuesta is null and activo is true "
			+"and id_homologacion= (select id_homologacion " 
			+ "from seleccion.homologacion_perfil_matriz " 
			+"where id_concurso_puesto_agr = "+idConcursoPuestoAgr+")";
	count = (BigInteger) em.createNativeQuery(s).getResultList().get(0);
	hayObservacionSFP = (count.intValue() > 0)? true : false;
 }catch(Exception e){
		e.printStackTrace();
	}
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
			finObservaciones();
			findEstado();

			List<HomologacionPerfilMatriz> matrizs =
				em.createQuery("select h from HomologacionPerfilMatriz h "
					+ " where h.concursoPuestoAgr.idConcursoPuestoAgr=" + idConcursoPuestoAgr).getResultList();

			if (!matrizs.isEmpty()){
				homologacionPerfilMatriz = matrizs.get(0);
				listar();
				
			}
			
			ultimaObs();
			enviadoHomologacion();
			cargaSueldosRemuneracion();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	private String findObj(Integer codigoObj) {
		String valorObj = null;
		if (codigoObj != null) {
			sinObjList.setCod(codigoObj);
			List<SinObj> lista = new ArrayList<SinObj>();
			lista = sinObjList.listaResultados();
			if (lista.size() > 0)
				valorObj = lista.get(0).getObjNombre();
		}
		return valorObj;
	}

	private void cargaSueldosRemuneracion() {
		/* OTRAS REMUNERACIONES */
		Query q;
		q = em.createQuery("select concepto from GrupoConceptoPago concepto "
				+ " where concepto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo"
				+ " and concepto.activo = true"
				+ " order by concepto.categoria asc");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<GrupoConceptoPago> grupoConceptoPagos = (List<GrupoConceptoPago>) q
				.getResultList();
		String otrasRemuneraciones = "";
		for (GrupoConceptoPago grupoConceptoPago : grupoConceptoPagos) {
			if (grupoConceptoPago.getObjCodigo() != 111) {
				otrasRemuneraciones = otrasRemuneraciones
						+ "- "
						+ findObj(grupoConceptoPago.getObjCodigo())
						+ (grupoConceptoPago.getCategoria() == null ? ""
								: " Cat. " + grupoConceptoPago.getCategoria())
						+ " Monto "
						+ grupoConceptoPago.getMonto()
						+ " (Gs. "
						+ new NumeroLetra().Convertir(
								grupoConceptoPago.getMonto(), true) + ") \n";
			}
		}
		/* SUELDOS */
		List<PuestoConceptoPago> puestoConceptoPagos = new ArrayList<PuestoConceptoPago>();
		for (ConcursoPuestoDet cp : concursoPuestoAgr.getConcursoPuestoDets()) {
			q = em.createQuery("select concepto from PuestoConceptoPago concepto "
					+ " where concepto.plantaCargoDet.idPlantaCargoDet = :idPuesto"
					+ " and concepto.activo = true"
					+ " order by concepto.categoria asc");
			q.setParameter("idPuesto", cp.getPlantaCargoDet()
					.getIdPlantaCargoDet());
			puestoConceptoPagos.addAll((List<PuestoConceptoPago>) q
					.getResultList());
		}
		// puestoConceptoPagos = quicksort(puestoConceptoPagos);
		String remuneracionBasica = "";
		String categoriaActual;

		for (int j = 0; j < puestoConceptoPagos.size(); j++) {
			categoriaActual = puestoConceptoPagos.get(j).getCategoria();
			int i = 0;

			while (puestoConceptoPagos.size() > (j + i)) {
				if (categoriaActual == null) {
					i++;
					break;
				}

				if (puestoConceptoPagos.get(j + i).getCategoria() != null)
					if (categoriaActual.compareTo(puestoConceptoPagos
							.get(j + i).getCategoria()) == 0)
						i++;
					else
						break;
				else
					break;
			}
			if (puestoConceptoPagos.get(j).getPlantaCargoDet().getPermanente() ? puestoConceptoPagos
					.get(j).getObjCodigo() == 111 : false) {
				remuneracionBasica = remuneracionBasica
						+ " - "
						+ i
						+ " vacancia(s) de "
						+ (puestoConceptoPagos.get(j).getCategoria() == null ? ""
								: " Categoría "
										+ puestoConceptoPagos.get(j)
												.getCategoria())
						+ " Monto "
						+ puestoConceptoPagos.get(j).getMonto()
						+ " (Gs. "
						+ new NumeroLetra().Convertir(puestoConceptoPagos
								.get(j).getMonto(), true) + ") \n";
			}
			j = j + i - 1;

		}
		//ACTUALIZA EL CAMPO REMUNERACION y OTRAS_REMUNERACIONES DE CONCURSO_PUESTO_AGR  (0.0)?
		em.createQuery(
				"update ConcursoPuestoAgr concursoPuestoAgr "
						+ " set concursoPuestoAgr.remuneracion='"
						+ remuneracionBasica + "',"
						+ " concursoPuestoAgr.otrasRemuneraciones='"
						+ otrasRemuneraciones + "'"
						+ " where concursoPuestoAgr.idConcursoPuestoAgr ="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr())
				.executeUpdate();
		// concursoPuestoAgr.setOtrasRemuneraciones(otrasRemuneraciones);
		// concursoPuestoAgr.setRemuneracion(remuneracionBasica);
		// em.persist(concursoPuestoAgr);
		// em.flush();
	}

	
	//AGREGADO JD
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

	public boolean habRpta(Long id){
			HomologacionPerfilMatrizDet d= em.find(HomologacionPerfilMatrizDet.class,id);
			if(d.getNroRpta()==null&& d.getRespuesta()==null)
				return true;
			
			return false;
		}
		/////////////////////////////////////////
		
		
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
		jbpmUtilFormController.setActividadActual(ActividadEnum.HOMOLOGACION_OEE);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.FIRMA_RESOL_HOMOLOG);
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

	public List<HomologacionPerfilMatrizDet> getListaObsRptas() {
		return listaObsRptas;
	}

	public void setListaObsRptas(List<HomologacionPerfilMatrizDet> listaObsRptas) {
		this.listaObsRptas = listaObsRptas;
	}
	public Boolean getHayObservacionSFP() {
		return hayObservacionSFP;
	}
	public void setHayObservacionSFP(Boolean hayObservacionSFP) {
		this.hayObservacionSFP = hayObservacionSFP;
	}
	public String getCodActividad() {
		return codActividad;
	}
	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

}
