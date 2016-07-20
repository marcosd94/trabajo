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
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("elaborarDecretoExcepcionFC")
public class ElaborarDecretoExcepcionFC implements Serializable {

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

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private List<ConcursoPuestoAgrExc> puestoAgrList = new ArrayList<ConcursoPuestoAgrExc>();

	private Long idConcurso;// Recibe del cu que le llama
	private Concurso concurso;// enviado por el CU
	private String obs;
	private List<Resolucion> decretoList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;

	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla = "ELABORAR_DECRETO_EXC";
	private String direccionFisica;
	private String entity;
	private Long idSave = null;
	private boolean habDecreto;

	private Long idExcepcion;

	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		if (!seguridadUtilFormController.validarInput(idConcursoPuestoAgr.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		if (!seguridadUtilFormController.validarInput(idExcepcion.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		puestoAgrList =
			em.createQuery("Select c from ConcursoPuestoAgrExc c  " + " where c.concursoPuestoAgr.concurso.idConcurso="
				+ idConcurso + " and c.estado='PERMANENTE N12'" + " and c.activo=true and c.excepcion.idExcepcion="+idExcepcion).getResultList();
		findEntidades();// Trae las entidades segun el grupo que se envio
		cargarListas();
		anexar();
		verificarDecreto();
		resolucion();
	}

	

	private void findEntidades() {
		nivelEntidadOeeUtil.setIdConfigCab(concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	



	@SuppressWarnings("unchecked")
	public String nextTask() {
		
		if (!tieneDecreto()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar al menos decreto");
			return null;
		}
		
		/**
		 * En la tabla SEL_HISTORIAL_EXCEPCION se inserta nuevo registro:
		 * */
		HistorialExcepcion historialExcepcion = new HistorialExcepcion();
		historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
		historialExcepcion.setEstado("CON DECRETO");
		historialExcepcion.setExcepcion(new Excepcion());
		historialExcepcion.getExcepcion().setIdExcepcion(idExcepcion);
		historialExcepcion.setObservacion(obs);
		historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		historialExcepcion.setFechaAlta(new Date());
		em.persist(historialExcepcion);
		

		/**
		 * 	Actualizar estado de la excepción: SEL_EXCEPCION a 'CON DECRETO’
		 * **/
		Excepcion excepcion= em.find(Excepcion.class,idExcepcion);
		excepcion.setFechaMod(new Date());
		excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		excepcion.setEstado("CON DECRETO");
		em.merge(excepcion);

		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgrExc agrExc= em.find(ConcursoPuestoAgrExc.class, puestoAgrList.get(i).getIdConcursoPuestoAgrExc());
			ConcursoPuestoAgr agr =
				em.find(ConcursoPuestoAgr.class, puestoAgrList.get(i).getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			/**
			 * Se actualiza el estado del grupo a ‘CON DECRETO’. 
			 * */
			agrExc.setEstado("CON DECRETO");
			agrExc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			agrExc.setFechaMod(new Date());
			em.merge(agr);
			/**
			 * Al igual que el estado de los puestos en PLANTA_CARGO_DET y CONCURSO_PUESTO_DET. incidencia 0001353  
			 * */
			List<ConcursoPuestoDet> d =
				em.createQuery("Select c from ConcursoPuestoDet c"
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ puestoAgrList.get(i).getConcursoPuestoAgr().getIdConcursoPuestoAgr()).getResultList();
			for (int j = 0; j < d.size(); j++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, d.get(j).getIdConcursoPuestoDet());
				puestoDet.setEstadoDet(conDecretoEstadoDet());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
				if (d.get(j).getPlantaCargoDet() != null) {
					PlantaCargoDet pdet =
						em.find(PlantaCargoDet.class, d.get(j).getPlantaCargoDet().getIdPlantaCargoDet());
					pdet.setEstadoDet(conDecretoEstadoDet());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
				}
			}
		}
		
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		return "next";

	}


	@SuppressWarnings("unchecked")
	private EstadoDet conDecretoEstadoDet() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'CON DECRETO' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	private boolean tieneDecreto() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getDecreto() != null)
				return true;
		}
		return false;
	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "Concurso";
		direccionFisica =
			"//SICCA//" + anio + "//OEE//"
				+ nivelEntidadOeeUtil.getIdConfigCab() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso;

	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoReso(Long id, String dir) {
		idResoEdit = id;
		Resolucion m = em.find(Resolucion.class, id);
		List<ConcursoPuestoAgrExc> p =
			em.createQuery("select c from ConcursoPuestoAgrExc c "
				+ " where c.decreto.idResolucion=" + m.getIdResolucion()).getResultList();
		if (p.isEmpty())
			idResoConsurso = idConcurso;
		else
			idResoConsurso = p.get(0).getConcursoPuestoAgr().getConcurso().getIdConcurso();

		return dir;

	}


	private void cargarListas() {
		try {
			decretoList = new ArrayList<Resolucion>();

			for (int i = 0; i < puestoAgrList.size(); i++) {
				Resolucion decreto = new Resolucion();
				if (puestoAgrList.get(i).getDecreto() != null) {
					decreto = em.find(Resolucion.class, puestoAgrList.get(i).getDecreto().getIdResolucion());
					if (!decretoList.contains(decreto))
						decretoList.add(decreto);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void verificarDecreto() {
		habDecreto = false;
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getDecreto() == null)
				habDecreto = true;

		}
	}

	public String resolucion() {
		paramReso = "";
		idResoEdit = null;
		if (puestoAgrList.isEmpty()) {
			statusMessages.add(Severity.WARN, "No posee grupos para generar el Decreto. Verifique");
			return null;
		}

		for (ConcursoPuestoAgrExc f : puestoAgrList) {
			paramReso += f.getIdConcursoPuestoAgrExc() + ",";
			paramIdConcurso = f.getConcursoPuestoAgr().getConcurso().getIdConcurso();

		}
		return "ir";

	}

	

	// GETTERS Y SETTERS

	

	

	public Long getIdConcurso() {
		return idConcurso;
	}

	public List<ConcursoPuestoAgrExc> getPuestoAgrList() {
		return puestoAgrList;
	}

	public void setPuestoAgrList(List<ConcursoPuestoAgrExc> puestoAgrList) {
		this.puestoAgrList = puestoAgrList;
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

	public List<Resolucion> getDecretoList() {
		return decretoList;
	}

	public void setDecretoList(List<Resolucion> decretoList) {
		this.decretoList = decretoList;
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

	public boolean isHabDecreto() {
		return habDecreto;
	}

	public void setHabDecreto(boolean habDecreto) {
		this.habDecreto = habDecreto;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public void setIdSave(Long idSave) {
		this.idSave = idSave;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}
	

}
