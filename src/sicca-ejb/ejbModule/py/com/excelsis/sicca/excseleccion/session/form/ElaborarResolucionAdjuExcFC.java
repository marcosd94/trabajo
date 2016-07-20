package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Scope(ScopeType.CONVERSATION)
@Name("elaborarResolucionAdjuExcFC")
public class ElaborarResolucionAdjuExcFC implements Serializable {

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

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(required = false)
	Usuario usuarioLogueado;

	private List<ConcursoPuestoAgrExc> puestoAgrList = new ArrayList<ConcursoPuestoAgrExc>();

	private Long idConcurso;// Recibe del cu que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;// enviado por el CU
	private String obs;

	private List<Resolucion> resolucionAdjudicacionList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;

	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla = "ELABORAR_RESOLUCION_EXC";
	private String direccionFisica;
	private String entity;
	private Long idSave = null;
	private boolean habReso;
	Date fechaSistema=new Date();
	private boolean habBotones=true;
	private boolean habcControles=false;
	
	private Long idExcepcion;
	private String cu="CU 595";

	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		
		
		if (!seguridadUtilFormController.validarInput(idExcepcion.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		if (!seguridadUtilFormController.validarInput(idConcursoPuestoAgr.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		
		
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		puestoAgrList =
			em.createQuery("Select c from ConcursoPuestoAgrExc c  " + " where c.concursoPuestoAgr.idConcursoPuestoAgr="
				+ idConcursoPuestoAgr + "" + " and c.estado ='ADJUDICADO' " + " and c.activo=true  and c.excepcion.idExcepcion="+idExcepcion).getResultList();
		findEntidades();// Trae las entidades segun el grupo que se envio
		cargarListas();
		verificarResolucion();
		anexar();
		resolucion();
	}


	
	
	
	/**
	 * @return cantidad de  adjudicados
	 * */
	@SuppressWarnings("unchecked")
	private int cntPostulanteXGrupo(Long idGrupo ){
		List<EvalReferencialPostulante> evRefPostulantes=em.createQuery("Select d from EvalReferencialPostulante d " +
				" where d.selAdjudicado=true and d.postulacion.activo=true and d.activo=true " +
				" and d.excluido=false and d.incluido=true " +
				" and d.excepcion.idExcepcion=:idExcepcion" +
				" and d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo")
				.setParameter("idGrupo", idGrupo).setParameter("idExcepcion",idExcepcion).getResultList();
		if(evRefPostulantes.isEmpty())
			return 0;
		
		return evRefPostulantes.size();
	}

	/**
	 * Cantidad de puestos activos: de cada grupo
	 * */
	@SuppressWarnings("unchecked")
	private int cntPuestoActivos(Long idGrupo){
		List<ConcursoPuestoDet> dets= em.createQuery("Select d from ConcursoPuestoDet d " +
				" where d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and" +
				" d.activo=true and d.excepcionado=true and d.excepcion.idExcepcion=:idExcepcion").setParameter("idGrupo", idGrupo)
				.setParameter("idExcepcion", idExcepcion).getResultList();
		
		if(dets.isEmpty())
			return 0;
		
		return dets.size();
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
	

	

	
	@SuppressWarnings("unchecked")
	public String nextTask() {

		if (!tieneResolucion()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar al menos una resolucion");
			return null;
		}
		if(!verficarPostResolucion())
			return null;
		/**
		 * En la tabla SEL_HISTORIAL_EXCEPCION se inserta nuevo registro:
		 * */
		HistorialExcepcion historialExcepcion = new HistorialExcepcion();
		historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
		historialExcepcion.setEstado("CON RES. ADJUDICACION");
		historialExcepcion.setExcepcion(new Excepcion());
		historialExcepcion.getExcepcion().setIdExcepcion(idExcepcion);
		historialExcepcion.setObservacion(obs);
		historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		historialExcepcion.setFechaAlta(new Date());
		em.persist(historialExcepcion);
		
		
		

		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgrExc agrExc= em.find(ConcursoPuestoAgrExc.class, puestoAgrList.get(i).getIdConcursoPuestoAgrExc());
			ConcursoPuestoAgr agr =
				em.find(ConcursoPuestoAgr.class, puestoAgrList.get(i).getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			/**
			 * Actualiza el estado del grupo Exc a ‘CON RESOLUCION ADJUDICACION’.
			 *  
			 * **/
			
			agrExc.setEstado("CON RES. ADJUDICACION");
			agrExc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			agrExc.setFechaMod(new Date());
			em.merge(agr);
			
			/**
			 * Al igual que el estado de los puestos en PLANTA_CARGO_DET y 
			 * CONCURSO_PUESTO_DET. 
			 * Se actualiza el estado de los puestos a ‘CON RESOLUCION ADJUDICACION’
			 * */
			List<ConcursoPuestoDet> d =
				em.createQuery("Select c from ConcursoPuestoDet c "
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo " +
							" and c.excepcion.idExcepcion="+idExcepcion +
							" and c.estadoDet=:idEstadoDet ")
							.setParameter("idGrupo", puestoAgrList.get(i).getConcursoPuestoAgr().getIdConcursoPuestoAgr())
					.setParameter("idEstadoDet",  seleccionUtilFormController.buscarEstadoDet("CONCURSO","ADJUDICADO")).getResultList();
			for (int j = 0; j < d.size(); j++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, d.get(j).getIdConcursoPuestoDet());
				puestoDet.setEstadoDet(conResolucionAdjudicacionEstadoDet());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
			
				if (d.get(j).getPlantaCargoDet() != null) {
					PlantaCargoDet pdet =
						em.find(PlantaCargoDet.class, d.get(j).getPlantaCargoDet().getIdPlantaCargoDet());
					pdet.setEstadoDet(conResolucionAdjudicacionEstadoDet());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
				
				}
			}
			
			
		}
		
		
		/**
		 * 	Actualizar estado de la excepción: SEL_EXCEPCION
		 * **/
		Excepcion excepcion= em.find(Excepcion.class,idExcepcion);
		excepcion.setFechaMod(new Date());
		excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		excepcion.setEstado("CON RES. ADJUDICACION");
		em.merge(excepcion);

		
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		em.flush();

		return "next";

	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "Concurso";
		direccionFisica =
			"//SICCA//" + anio + "//OEE//" + configuracionUoCab.getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso+"//"+idConcursoPuestoAgr;

	}

	private boolean tieneResolucion() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getResolucionAdjudicacion() != null)
				return true;
		}
		return false;
	}

	
	@SuppressWarnings("unchecked")
	private EstadoDet conResolucionAdjudicacionEstadoDet() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'CON RESOLUCION ADJUDICACION' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	public void verificarResolucion() {
		habReso = false;
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getResolucionAdjudicacion() == null)
				habReso = true;

		}
	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoReso(Long id, String dir) {
		idResoEdit = id;
		Resolucion m = em.find(Resolucion.class, id);
		List<ConcursoPuestoAgr> p =
			em.createQuery("select c from ConcursoPuestoAgr c "
				+ " where c.resolucionHomologacion.idResolucion=" + m.getIdResolucion()).getResultList();
		if (p.isEmpty())
			idResoConsurso = idConcurso;
		else
			idResoConsurso = p.get(0).getConcurso().getIdConcurso();

		return dir;

	}


	private void cargarListas() {
		try {
			resolucionAdjudicacionList = new ArrayList<Resolucion>();
			for (int i = 0; i < puestoAgrList.size(); i++) {
				Resolucion adjudicacion = new Resolucion();
				if (puestoAgrList.get(i).getResolucionAdjudicacion() != null) {
					adjudicacion =
						em.find(Resolucion.class, puestoAgrList.get(i).getResolucionAdjudicacion().getIdResolucion());
					if (!resolucionAdjudicacionList.contains(adjudicacion))
						resolucionAdjudicacionList.add(adjudicacion);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * LLAMA AL CU	612
	 * */
	public String resolucion() {
		paramReso = "";
		idResoEdit = null;

		
		if (puestoAgrList.isEmpty()) {
			statusMessages.add("No posee grupos para realizar la Resolucion. Verifique");
			return null;
		}
		
		if(!verficarPostResolucion())
			return null;
		
		for (ConcursoPuestoAgrExc f : puestoAgrList) {
			paramReso += f.getIdConcursoPuestoAgrExc() + ",";
			paramIdConcurso = f.getConcursoPuestoAgr().getConcurso().getIdConcurso();

		}
		return "ir";

	}
	private boolean verficarPostResolucion(){
		habBotones=true;
		for (ConcursoPuestoAgrExc agr : puestoAgrList) {
			if(cntPostulanteXGrupo(agr.getConcursoPuestoAgr().getIdConcursoPuestoAgr())!=cntPuestoActivos(agr.getConcursoPuestoAgr().getIdConcursoPuestoAgr())){
				statusMessages.add(Severity.ERROR,"Cantidad de adjudicados no es igual a cantidad de puestos  para la excepcion "+agr.getConcursoPuestoAgr().getDescripcionGrupo()+" Verificar en control Puesto/Postulante");
				habBotones=false;
				habcControles=true;
				return habBotones;
			}else
				habcControles=false;
		}
		return habBotones;
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

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	
	public List<ConcursoPuestoAgrExc> getPuestoAgrList() {
		return puestoAgrList;
	}


	public void setPuestoAgrList(List<ConcursoPuestoAgrExc> puestoAgrList) {
		this.puestoAgrList = puestoAgrList;
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

	public List<Resolucion> getResolucionAdjudicacionList() {
		return resolucionAdjudicacionList;
	}

	public void setResolucionAdjudicacionList(List<Resolucion> resolucionAdjudicacionList) {
		this.resolucionAdjudicacionList = resolucionAdjudicacionList;
	}

	public String getParamMemo() {
		return paramMemo;
	}

	public void setParamMemo(String paramMemo) {
		this.paramMemo = paramMemo;
	}

	public String getParamNota() {
		return paramNota;
	}

	public void setParamNota(String paramNota) {
		this.paramNota = paramNota;
	}

	public String getParamReso() {
		return paramReso;
	}

	public void setParamReso(String paramReso) {
		this.paramReso = paramReso;
	}

	public Long getParamIdConcurso() {
		return paramIdConcurso;
	}

	public void setParamIdConcurso(Long paramIdConcurso) {
		this.paramIdConcurso = paramIdConcurso;
	}

	public Long getIdResoEdit() {
		return idResoEdit;
	}

	public void setIdResoEdit(Long idResoEdit) {
		this.idResoEdit = idResoEdit;
	}

	public Long getIdResoConsurso() {
		return idResoConsurso;
	}

	public void setIdResoConsurso(Long idResoConsurso) {
		this.idResoConsurso = idResoConsurso;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Long getIdSave() {
		return idSave;
	}

	public void setIdSave(Long idSave) {
		this.idSave = idSave;
	}

	public boolean isHabReso() {
		return habReso;
	}

	public void setHabReso(boolean habReso) {
		this.habReso = habReso;
	}
	public boolean isHabBotones() {
		return habBotones;
	}
	public void setHabBotones(boolean habBotones) {
		this.habBotones = habBotones;
	}
	
	public boolean isHabcControles() {
		return habcControles;
	}
	public void setHabcControles(boolean habcControles) {
		this.habcControles = habcControles;
	}


	public Long getIdExcepcion() {
		return idExcepcion;
	}


	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}


	public String getCu() {
		return cu;
	}


	public void setCu(String cu) {
		this.cu = cu;
	}

}
