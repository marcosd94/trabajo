package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("resolucionNombramientoExcFC")
public class ResolucionNombramientoExcFC {

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

	private List<ConcursoPuestoAgrExc> puestoAgrExcList = new ArrayList<ConcursoPuestoAgrExc>();

	private Long idConcurso;// Recibe del cu que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;// enviado por el CU
	private ConcursoPuestoAgrExc concursoPuestoAgrExc;
	private Excepcion excepcion;
	private String obs;

	private List<Resolucion> resoNombramientoList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;

	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private Long idExcepcion;

	private ConcursoPuestoAgr concursoPuestoAgr;

	private String nombrePantalla = "ELABORAR_RESOL_NOMB_EXC";
	private String direccionFisica;
	private String entity = "ConcursoPuestoAgr";
	private Long idSave = null;
	private SeguridadUtilFormController seguridadUtilFormController;

	@SuppressWarnings("unchecked")
	public void init() {
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		excepcion = em.find(Excepcion.class, idExcepcion);
		concursoPuestoAgrExc = new ConcursoPuestoAgrExc();
		concursoPuestoAgrExc = buscarConcursoPuestoAgrExc();
		
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = concursoPuestoAgr.getConcurso();
		puestoAgrExcList = em
				.createQuery(
						"Select c from ConcursoPuestoAgrExc c  "
								+ " where c.concursoPuestoAgr.concurso.idConcurso = "
								+ idConcurso
								+ " and c.estado = 'PERMANENTE D12' and c.nombramiento is false ")
				.getResultList();

		findEntidades();// Trae las entidades segun el grupo que se envio
		cargarListas();
		anexar();
	}

	private ConcursoPuestoAgrExc buscarConcursoPuestoAgrExc() {
		Query q = em
				.createQuery("select agrExc from ConcursoPuestoAgrExc agrExc "
						+ " where agrExc.activo is true "
						+ "and agrExc.concursoPuestoAgr.idConcursoPuestoAgr = :idAgr "
						+ "and agrExc.excepcion.idExcepcion = :idExc");
		q.setParameter("idAgr", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("idExc", excepcion.getIdExcepcion());
		List<ConcursoPuestoAgrExc> lista = q.getResultList();
		if (lista.isEmpty())
			return null;
		return lista.get(0);
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

	private void cargarListas() {
		try {
			resoNombramientoList = new ArrayList<Resolucion>();

			for (int i = 0; i < puestoAgrExcList.size(); i++) {
				Resolucion nombramiento = new Resolucion();
				if (puestoAgrExcList.get(i).getResolucionNombramiento() != null) {
					nombramiento = em.find(Resolucion.class, puestoAgrExcList
							.get(i).getResolucionNombramiento()
							.getIdResolucion());
					if (!resoNombramientoList.contains(nombramiento))
						resoNombramientoList.add(nombramiento);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void anexar() {
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		entity = "ConcursoPuestoAgr";
		String separator = File.separator;
		direccionFisica = separator
				+ "SICCA"
				+ separator
				+ anio
				+ separator
				+ "OEE"
				+ separator
				+ configuracionUoCab.getIdConfiguracionUo()
				+ separator
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separator + idConcurso
				+ separator + idConcursoPuestoAgr;

	}

	public String resolucion() {
		paramReso = "";
		idResoEdit = null;
		if (puestoAgrExcList.isEmpty()) {
			statusMessages.add(Severity.WARN,
					"No posee grupos para generar una Resolución. Verifique");
			return null;
		}

		for (ConcursoPuestoAgrExc f : puestoAgrExcList) {
			paramReso += f.getIdConcursoPuestoAgrExc() + ",";
			paramIdConcurso = f.getConcursoPuestoAgr().getConcurso()
					.getIdConcurso();

		}
		return "ir";

	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoReso(Long id, String dir) {
		idResoEdit = id;
		Resolucion m = em.find(Resolucion.class, id);
		List<ConcursoPuestoAgrExc> p = em.createQuery(
				"select c from ConcursoPuestoAgrExc c "
						+ " where c.resolucionNombramiento.idResolucion="
						+ m.getIdResolucion()).getResultList();
		if (p.isEmpty())
			idResoConsurso = idConcurso;
		else
			idResoConsurso = p.get(0).getConcursoPuestoAgr().getConcurso().getIdConcurso();

		return dir;

	}

	private boolean tieneResoNom() {
		for (int i = 0; i < puestoAgrExcList.size(); i++) {
			if (puestoAgrExcList.get(i).getResolucionNombramiento() != null)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String nextTask() {
		if (!tieneResoNom()) {
			statusMessages.add(Severity.ERROR,
					"Debe ingrear al menos una Resolución ");
			return null;
		}
		try {
			HistorialExcepcion historialExcepcion = new HistorialExcepcion();
			historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
			historialExcepcion.setEstado("CON RES. NOMBRAMIENTO");
			historialExcepcion.setExcepcion(excepcion);
			historialExcepcion.setFechaAlta(new Date());
			if (obs != null && obs.trim().isEmpty())
				historialExcepcion.setObservacion(obs);
			historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(historialExcepcion);
			excepcion.setEstado("CON RES.NOMBRAMIENTO");
			excepcion.setFechaMod(new Date());
			excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(excepcion);
			concursoPuestoAgrExc.setEstado("CON RES.NOMBRAMIENTO");
			concursoPuestoAgrExc.setFechaMod(new Date());
			concursoPuestoAgrExc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concursoPuestoAgrExc);
			for (int i = 0; i < puestoAgrExcList.size(); i++) {
				ConcursoPuestoAgrExc aEx = em.find(ConcursoPuestoAgrExc.class,
						puestoAgrExcList.get(i).getIdConcursoPuestoAgrExc());
				/**
				 * Al igual que el estado de los puestos en PLANTA_CARGO_DET y
				 * CONCURSO_PUESTO_DET. incidencia 0001354
				 * */
				List<ConcursoPuestoDet> d = em
						.createQuery(
								"Select c from ConcursoPuestoDet c"
										+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
										+ aEx.getConcursoPuestoAgr()
												.getIdConcursoPuestoAgr()
										+ " and c.excepcion.idExcepcion = "
										+ excepcion.getIdExcepcion())
						.getResultList();
				for (int j = 0; j < d.size(); j++) {
					ConcursoPuestoDet puestoDet = em.find(
							ConcursoPuestoDet.class, d.get(j)
									.getIdConcursoPuestoDet());
					puestoDet
							.setEstadoDet(conResolucionNombramientoEstadoDet());
					puestoDet.setFechaMod(new Date());
					puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(puestoDet);

					if (d.get(j).getPlantaCargoDet() != null) {
						PlantaCargoDet pdet = em.find(PlantaCargoDet.class, d
								.get(j).getPlantaCargoDet()
								.getIdPlantaCargoDet());
						pdet.setEstadoDet(conResolucionNombramientoEstadoDet());
						pdet.setFechaMod(new Date());
						pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(pdet);

					}
				}
			}
			em.flush();
		} catch (Exception e) {
			return null;
		}

		return "next";

	}

	@SuppressWarnings("unchecked")
	private EstadoDet conResolucionNombramientoEstadoDet() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CON RESOLUCION NOMBRAMIENTO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	public List<ConcursoPuestoAgrExc> getPuestoAgrExcList() {
		return puestoAgrExcList;
	}

	public void setPuestoAgrExcList(List<ConcursoPuestoAgrExc> puestoAgrExcList) {
		this.puestoAgrExcList = puestoAgrExcList;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
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

	public void setIdSave(Long idSave) {
		this.idSave = idSave;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public ConcursoPuestoAgrExc getConcursoPuestoAgrExc() {
		return concursoPuestoAgrExc;
	}

	public void setConcursoPuestoAgrExc(
			ConcursoPuestoAgrExc concursoPuestoAgrExc) {
		this.concursoPuestoAgrExc = concursoPuestoAgrExc;
	}

}
