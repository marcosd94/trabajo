package py.com.excelsis.sicca.circuitoCIO.session.form;

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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("elaboResNombamiento640FC")
public class ElaboResNombamiento640FC implements Serializable {

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

	private List<ConcursoPuestoAgr> puestoAgrList = new ArrayList<ConcursoPuestoAgr>();

	private Long idConcurso;// Recibe del cu que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;// enviado por el CU
	private String obs;

	private List<Resolucion> resoNombramientoList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;

	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla = "resolucionNombramiento_edit_ConcursoInterno";
	private String direccionFisica;
	private String entity = "ConcursoPuestoAgr";
	private Long idSave = null;

	private SeguridadUtilFormController seguridadUtilFormController;

	@SuppressWarnings("unchecked")
	public void init() {
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		validarOee();
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		puestoAgrList =
			em.createQuery("Select c from ConcursoPuestoAgr c  " + " where c.concurso.idConcurso="
				+ idConcurso + " and c.estado=" + estadoPermanenteD12()+" and c.nombramiento=false ").getResultList();

		findEntidades();// Trae las entidades segun el grupo que se envio
		cargarListas();
		anexar();
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOeeCIO(null, concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PERMANENTE D12")
			+ "", ActividadEnum.CIO_ELABORAR_RESOLUCION_NOMBRAMIENTO.getValor());
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

	

	private Boolean haySeleccionados() {

		for (ConcursoPuestoAgr f : puestoAgrList) {
			if (f.getSeleccionado() != null && f.getSeleccionado()) {
				return true;
			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String nextTask() {
		if (!tieneResoNom()) {
			statusMessages.add(Severity.ERROR,"Debe ingrear al menos una Resolución ");
			return null;
		}

		Observacion observacion = new Observacion();
		observacion.setObservacion(obs);
		observacion.setFechaAlta(new Date());
		observacion.setConcurso(em.find(Concurso.class, idConcurso));
		observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
		em.persist(observacion);
		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgr agr =
				em.find(ConcursoPuestoAgr.class, puestoAgrList.get(i).getIdConcursoPuestoAgr());
			agr.setEstado(conNombramiento());
			agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
			agr.setFechaMod(new Date());
			em.merge(agr);
			em.flush();
			/**
			 * Al igual que el estado de los puestos en PLANTA_CARGO_DET y CONCURSO_PUESTO_DET. incidencia 0001354  
			 * */
			List<ConcursoPuestoDet> d =
				em.createQuery("Select c from ConcursoPuestoDet c"
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ puestoAgrList.get(i).getIdConcursoPuestoAgr()).getResultList();
			for (int j = 0; j < d.size(); j++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, d.get(j).getIdConcursoPuestoDet());
				puestoDet.setEstadoDet(conResolucionNombramientoEstadoDet());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
				em.flush();
				if (d.get(j).getPlantaCargoDet() != null) {
					PlantaCargoDet pdet =
						em.find(PlantaCargoDet.class, d.get(j).getPlantaCargoDet().getIdPlantaCargoDet());
					pdet.setEstadoDet(conResolucionNombramientoEstadoDet());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
					em.flush();
				}
			}
			
		}
		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CIO_ELABORAR_RESOLUCION_NOMBRAMIENTO);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CIO_FIRMAR_RESOLUCION_NOMBRAMIENTO);

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}

		return "next";

	}
	
	@SuppressWarnings("unchecked")
	private EstadoDet conResolucionNombramientoEstadoDet() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'CON RESOLUCION NOMBRAMIENTO' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private Integer conNombramiento() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CON RESOLUCION NOMBRAMIENTO'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	private boolean tieneResoNom() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getResolucionNombramiento() != null)
				return true;
		}
		return false;
	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "ConcursoPuestoAgr";
		direccionFisica =
			"//SICCA//" + anio + "//OEE//"+ configuracionUoCab.getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso+"//"+idConcursoPuestoAgr;

	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoReso(Long id, String dir) {
		idResoEdit = id;
		Resolucion m = em.find(Resolucion.class, id);
		List<ConcursoPuestoAgr> p =
			em.createQuery("select c from ConcursoPuestoAgr c "
				+ " where c.resolucionNombramiento.idResolucion=" + m.getIdResolucion()).getResultList();
		if (p.isEmpty())
			idResoConsurso = idConcurso;
		else
			idResoConsurso = p.get(0).getConcurso().getIdConcurso();

		return dir;

	}

	
	private void cargarListas() {
		try {
			resoNombramientoList = new ArrayList<Resolucion>();

			for (int i = 0; i < puestoAgrList.size(); i++) {
				Resolucion nombramiento = new Resolucion();
				if (puestoAgrList.get(i).getResolucionNombramiento() != null) {
					nombramiento =
						em.find(Resolucion.class, puestoAgrList.get(i).getResolucionNombramiento().getIdResolucion());
					if (!resoNombramientoList.contains(nombramiento))
						resoNombramientoList.add(nombramiento);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String resolucion() {
		paramReso = "";
		idResoEdit = null;
		if (puestoAgrList.isEmpty()) {
			statusMessages.add(Severity.WARN, "No posee grupos para generar una Resolución. Verifique");
			return null;
		}
		
		for (ConcursoPuestoAgr f : puestoAgrList) {
				paramReso += f.getIdConcursoPuestoAgr() + ",";
				paramIdConcurso = f.getConcurso().getIdConcurso();
			
		}
		return "ir";

	}

	public String resolucionSave() {
		paramReso = "";
		idResoEdit = null;
		if (!haySeleccionados()) {
			statusMessages.clear();
			statusMessages.add("Debe seleccionar algun grupo");
			return null;
		}
		for (ConcursoPuestoAgr f : puestoAgrList) {
			if (f.getSeleccionado() != null && f.getSeleccionado()) {
				paramReso += f.getIdConcursoPuestoAgr() + ",";
				paramIdConcurso = f.getConcurso().getIdConcurso();
			}

		}
		return "/seleccion/resolucionHomologacion/ResolucionHomologacionEdit.xhtml?"
			+ "&idConcursoPuestoAgr="
			+ paramReso
			+ ""
			+ "&concursoPuestoAgrIdConcursoPuestoAgr="
			+ idConcursoPuestoAgr
			+ "&idConcursoAgr="
			+ paramIdConcurso
			+ "&"
			+ "fromCU=419&plantillaResolucionHomologacionFrom=circuitoCIO/elaboResNombamiento640/ElaboResNombamiento640";

	}

	@SuppressWarnings("unchecked")
	private Integer estadoPermanenteD12() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'PERMANENTE D12'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
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

	public List<ConcursoPuestoAgr> getPuestoAgrList() {
		return puestoAgrList;
	}

	public void setPuestoAgrList(List<ConcursoPuestoAgr> puestoAgrList) {
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

	public List<Resolucion> getResoNombramientoList() {
		return resoNombramientoList;
	}

	public void setResoNombramientoList(List<Resolucion> resoNombramientoList) {
		this.resoNombramientoList = resoNombramientoList;
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

}
