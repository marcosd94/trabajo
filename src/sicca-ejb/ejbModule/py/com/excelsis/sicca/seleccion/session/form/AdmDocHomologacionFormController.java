package py.com.excelsis.sicca.seleccion.session.form;

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
import py.com.excelsis.sicca.entity.MemoHomologacion;
import py.com.excelsis.sicca.entity.NotaHomologacion;
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

@Scope(ScopeType.PAGE)
@Name("admDocHomologacionFormController")
public class AdmDocHomologacionFormController implements Serializable {

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
	private List<MemoHomologacion> memoHomologacionList = new ArrayList<MemoHomologacion>();
	private List<NotaHomologacion> notaHomologacionList = new ArrayList<NotaHomologacion>();
	private List<Resolucion> resolucionHomologacionList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;
	private Long idMemoConcurso;
	private Long idMemoEdit = null;
	private Long idNotaConcurso;
	private Long idNotaEdit = null;
	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla = "admDocHomologacion_edit";
	private String direccionFisica;
	private String entity;
	private boolean habMemo;
	private boolean habNota;
	private boolean habReso;
	private String from;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		//seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo(), concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "HOMOLOGADO")+"", ActividadEnum.ELABORAR_DOC_HOMOLOG.getValor());

	}

	@SuppressWarnings("unchecked")
	public void init() {
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		validarOee(concurso);
		puestoAgrList =
			em.createQuery("Select c from ConcursoPuestoAgr c  " + " where c.concurso.idConcurso="
				+ idConcurso + " and c.estado=" + homologado() + "  and c.activo=true").getResultList();
		findEntidades();// Trae las entidades segun el grupo que se envio
		cargarListas();
		verificarGrupos();// verfica los grupos para ver que botones se van habilitar
		anexar();
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

	public void marcarTodos() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgr agr = puestoAgrList.get(i);
			agr.setSeleccionado(true);
			puestoAgrList.set(i, agr);
		}

	}

	public void desmarcarTodos() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
			agr = puestoAgrList.get(i);
			agr.setSeleccionado(false);
			puestoAgrList.set(i, agr);
		}
	}

	private Boolean haySeleccionados() {

		for (ConcursoPuestoAgr f : puestoAgrList) {
			if (f.getSeleccionado()) {
				return true;
			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoMemo(Long id, String dir) {
		idMemoEdit = id;
		MemoHomologacion m = em.find(MemoHomologacion.class, id);
		List<ConcursoPuestoAgr> p =
			em.createQuery("select c from ConcursoPuestoAgr c "
				+ " where c.memoHomologacion.idMemoHomologacion=" + m.getIdMemoHomologacion()).getResultList();
		if (p.isEmpty())
			idMemoConcurso = idConcurso;
		else
			idMemoConcurso = p.get(0).getConcurso().getIdConcurso();

		return dir;

	}

	@SuppressWarnings("unchecked")
	public String nextTask() {

		if (!tieneResolucion()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar al menos una resolución");
			return null;
		}
		Observacion observacion = new Observacion();
		observacion.setObservacion(obs);
		observacion.setFechaAlta(new Date());
		observacion.setConcurso(em.find(Concurso.class, idConcurso));
		observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		observacion.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance()));
		em.persist(observacion);

		// pasa al estado CON DOCUMENTOS DE HOMOLOGACION de la tabla PlantaCargoDet,ConcursoPuestoDet el campo estadoDEt
		for (int i = 0; i < puestoAgrList.size(); i++) {
			List<ConcursoPuestoDet> d =
				em.createQuery("Select c from ConcursoPuestoDet c"
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ puestoAgrList.get(i).getIdConcursoPuestoAgr()).getResultList();
			ConcursoPuestoAgr agr =
				em.find(ConcursoPuestoAgr.class, puestoAgrList.get(i).getIdConcursoPuestoAgr());
			agr.setEstado(conDocHomologacion());
			agr.setFechaMod(new Date());
			agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(agr);
			em.flush();
			
			for (int j = 0; j < d.size(); j++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, d.get(j).getIdConcursoPuestoDet());
				puestoDet.setEstadoDet(conDocHomolacionEstadoDet());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
				em.flush();
				if (d.get(j).getPlantaCargoDet() != null) {
					PlantaCargoDet pdet =
						em.find(PlantaCargoDet.class, d.get(j).getPlantaCargoDet().getIdPlantaCargoDet());
					pdet.setEstadoDet(conDocHomolacionEstadoDet());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
					em.flush();
				}
			}
			
		}
		em.flush();

		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		//jbpmUtilFormController.setActividadActual(ActividadEnum.ELABORAR_DOC_HOMOLOG);
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.FIRMA_RESOL_HOMOLOG);

		if (jbpmUtilFormController.nextTask()) {
			em.flush();
		}

		return "next";

	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "Concurso";
		direccionFisica =
			"//SICCA//" + anio + "//" + configuracionUoCab.getIdConfiguracionUo() + "//"
				+ configuracionUoCab.getIdConfiguracionUo() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso;
		from =
			"seleccion/admDocHomologacion/AdmDocHomologacion?concursoPuestoAgrIdConcursoPuestoAgr="
				+ idConcursoPuestoAgr;

	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoNota(Long id, String dir) {
		idNotaEdit = id;
		NotaHomologacion m = em.find(NotaHomologacion.class, id);
		List<ConcursoPuestoAgr> p =
			em.createQuery("select c from ConcursoPuestoAgr c "
				+ " where c.notaHomologacion.idNotaHomologacion=" + m.getIdNotaHomologacion()).getResultList();
		if (p.isEmpty())
			idNotaConcurso = idConcurso;
		else
			idNotaConcurso = p.get(0).getConcurso().getIdConcurso();

		return dir;

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

	@SuppressWarnings("unchecked")
	private void cargarListas() {
		try {
			memoHomologacionList = new ArrayList<MemoHomologacion>();
			notaHomologacionList = new ArrayList<NotaHomologacion>();
			resolucionHomologacionList = new ArrayList<Resolucion>();
			// List<ConcursoPuestoAgr> cAgrs= em.createQuery("Select c from ConcursoPuestoAgr c " +
			// " where c.concurso.idConcurso="+idConcurso ).getResultList();
			//
			for (int i = 0; i < puestoAgrList.size(); i++) {
				Resolucion reso = new Resolucion();
				MemoHomologacion memo = new MemoHomologacion();
				NotaHomologacion nota = new NotaHomologacion();
				if (puestoAgrList.get(i).getNotaHomologacion() != null) {
					nota =
						em.find(NotaHomologacion.class, puestoAgrList.get(i).getNotaHomologacion().getIdNotaHomologacion());
					if (!notaHomologacionList.contains(nota))
						notaHomologacionList.add(nota);
				}
				if (puestoAgrList.get(i).getResolucionHomologacion() != null) {
					reso =
						em.find(Resolucion.class, puestoAgrList.get(i).getResolucionHomologacion().getIdResolucion());
					if (!resolucionHomologacionList.contains(reso))
						resolucionHomologacionList.add(reso);
				}
				if (puestoAgrList.get(i).getMemoHomologacion() != null) {
					memo =
						em.find(MemoHomologacion.class, puestoAgrList.get(i).getMemoHomologacion().getIdMemoHomologacion());
					if (!memoHomologacionList.contains(memo))
						memoHomologacionList.add(memo);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String memo() {
		paramMemo = "";
		idMemoEdit = null;
		if (puestoAgrList.isEmpty()) {
			statusMessages.add("No existe ningun grupo, Vergifique");
			return null;
		}

		for (ConcursoPuestoAgr f : puestoAgrList) {
			if (f.getMemoHomologacion() == null) {
				paramMemo += f.getIdConcursoPuestoAgr() + ",";
				paramIdConcurso = f.getConcurso().getIdConcurso();
			}

		}
		if (paramMemo.equals("")) {
			statusMessages.add("No existe ningun grupo para generar el Memo, Vergifique");
			return null;
		}

		return "ir";

	}

	public String nota() {
		paramNota = "";
		idNotaEdit = null;
		if (puestoAgrList.isEmpty()) {
			statusMessages.add("No existe ningun grupo, Vergifique");
			return null;
		}
		for (ConcursoPuestoAgr f : puestoAgrList) {
			if (f.getNotaHomologacion() == null) {
				paramNota += f.getIdConcursoPuestoAgr() + ",";
				paramIdConcurso = f.getConcurso().getIdConcurso();
			}

		}
		if (paramNota.equals("")) {
			statusMessages.add("No existe ningun grupo para generar la Nota, Vergifique");
			return null;
		}
		return "ir";

	}

	public String resolucion() {
		paramReso = "";
		idResoEdit = null;
		if (puestoAgrList.isEmpty()) {
			statusMessages.add("No existe ningun grupo, Vergifique");
			return null;
		}
		for (ConcursoPuestoAgr f : puestoAgrList) {
			if (f.getResolucionHomologacion() == null) {
				paramReso += f.getIdConcursoPuestoAgr() + ",";
				paramIdConcurso = f.getConcurso().getIdConcurso();
			}

		}

		if (paramReso.equals("")) {
			statusMessages.add("No existe ningun grupo para generar la Resolucion, Vergifique");
			return null;
		}

		return "ir";

	}

	private boolean tieneResolucion() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getResolucionHomologacion() != null)
				return true;
		}
		return false;
	}

	private void verificarGrupos() {
		habMemo = false;
		habNota = false;
		habReso = false;

		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getMemoHomologacion() == null)
				habMemo = true;
			if (puestoAgrList.get(i).getNotaHomologacion() == null)
				habNota = true;
			if (puestoAgrList.get(i).getResolucionHomologacion() == null)
				habReso = true;
		}
	}

	@SuppressWarnings("unchecked")
	private Integer homologado() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'HOMOLOGADO'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer conDocHomologacion() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CON DOCUMENTOS DE HOMOLOGACION'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private EstadoDet conDocHomolacionEstadoDet() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'CON DOCUMENTOS DE HOMOLOGACION' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
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

	public List<MemoHomologacion> getMemoHomologacionList() {
		return memoHomologacionList;
	}

	public void setMemoHomologacionList(List<MemoHomologacion> memoHomologacionList) {
		this.memoHomologacionList = memoHomologacionList;
	}

	public List<NotaHomologacion> getNotaHomologacionList() {
		return notaHomologacionList;
	}

	public void setNotaHomologacionList(List<NotaHomologacion> notaHomologacionList) {
		this.notaHomologacionList = notaHomologacionList;
	}

	public List<Resolucion> getResolucionHomologacionList() {
		return resolucionHomologacionList;
	}

	public void setResolucionHomologacionList(List<Resolucion> resolucionHomologacionList) {
		this.resolucionHomologacionList = resolucionHomologacionList;
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

	public void setIdMemoConcurso(Long idMemoConcurso) {
		this.idMemoConcurso = idMemoConcurso;
	}

	public Long getIdMemoEdit() {
		return idMemoEdit;
	}

	public void setIdMemoEdit(Long idMemoEdit) {
		this.idMemoEdit = idMemoEdit;
	}

	public Long getIdMemoConcurso() {
		return idMemoConcurso;
	}

	public Long getIdNotaConcurso() {
		return idNotaConcurso;
	}

	public void setIdNotaConcurso(Long idNotaConcurso) {
		this.idNotaConcurso = idNotaConcurso;
	}

	public Long getIdNotaEdit() {
		return idNotaEdit;
	}

	public void setIdNotaEdit(Long idNotaEdit) {
		this.idNotaEdit = idNotaEdit;
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

	public boolean isHabMemo() {
		return habMemo;
	}

	public void setHabMemo(boolean habMemo) {
		this.habMemo = habMemo;
	}

	public boolean isHabNota() {
		return habNota;
	}

	public void setHabNota(boolean habNota) {
		this.habNota = habNota;
	}

	public boolean isHabReso() {
		return habReso;
	}

	public void setHabReso(boolean habReso) {
		this.habReso = habReso;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
