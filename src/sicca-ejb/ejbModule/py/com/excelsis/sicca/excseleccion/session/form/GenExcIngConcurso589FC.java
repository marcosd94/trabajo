package py.com.excelsis.sicca.excseleccion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.validator.Size;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionElegibles;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TipoExcepcion;

@Scope(ScopeType.CONVERSATION)
@Name("genExcIngConcurso589FC")
public class GenExcIngConcurso589FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Long idConcurso;
	private String tipoConcurso;
	private Long idGrupoPuesto;
	private Date fecha = new Date();
	private String tipoExcepcion;
	private String estado = "PENDIENTE";
	private Integer idTipoExcepcion;
	@Size(max = 100, message = "Superada la cantidad Máxima de caracteres")
	private String observacionExcepcion;
	private List<ExcepcionElegibles> lElegibles;
	private Long idExcepcion;
	private String descGrupoVer;
	private String descTipoConcursoVer;
	private String descConcursoVer;
	private Long idGrupo ;

	public void init() {
		cargarCabecera();
	}

	public void initVer() {
		cargarCabeceraVer();
		cargarListaElegibles();

	}

	private void cargarCabeceraVer() {
		Excepcion excepcion = em.find(Excepcion.class, idExcepcion);
		Concurso concurso = em.find(Concurso.class, excepcion.getConcurso().getIdConcurso());
		descConcursoVer = concurso.getNombre();
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class, excepcion.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		idGrupo = excepcion.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
		descGrupoVer =  grupo.getDescripcionGrupo();
		descTipoConcursoVer =
			concurso.getDatosEspecificosTipoConc().getDescripcion();
		tipoExcepcion = excepcion.getTipo();
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	private void cargarListaElegibles() {
		lElegibles = new ArrayList<ExcepcionElegibles>();
		Query q =
			em.createQuery("select ExcepcionElegibles from ExcepcionElegibles ExcepcionElegibles "
				+ " where ExcepcionElegibles.excepcion.idExcepcion = :idExcepcion and ExcepcionElegibles.activo is true ");
		q.setParameter("idExcepcion", idExcepcion);
		lElegibles = q.getResultList();
	}

	private void cargarCabecera() {
		if (idTipoExcepcion != null) {
			TipoExcepcion tE = TipoExcepcion.getTipoExcepcionId(idTipoExcepcion);
			if (tE != null)
				tipoExcepcion = tE.getDescripcion();
			else
				tipoExcepcion = "-";

		}
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	private Boolean precondSave() {
		if (idConcurso == null || idGrupoPuesto == null || observacionExcepcion == null
			|| observacionExcepcion.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		if (observacionExcepcion.length() > 1000) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Descripción de la Excepción (1000)");
			return false;
		}
		return true;
	}

	public String save() {
		if (!precondSave())
			return "FAIL";
		try {
			Excepcion ex = new Excepcion();
			ex.setActivo(true);
			ex.setFechaAlta(new Date());
			ex.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			ex.setTipo(tipoExcepcion);
			ex.setConcurso(new Concurso());
			ex.getConcurso().setIdConcurso(idConcurso);
			ex.setConcursoPuestoAgr(new ConcursoPuestoAgr());
			ex.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idGrupoPuesto);
			ex.setEstado("PENDIENTE");
			ex.setObservacion(observacionExcepcion);
			em.persist(ex);
			em.flush();
			idExcepcion = ex.getIdExcepcion();
			statusMessages.add(Severity.WARN, "Debe Gestionar la Excepción");
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			
			return "OK";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}

		return null;
	}

	private List<ConcursoPuestoAgr> traerGrupoPuesto() {
		Query q =
			em.createQuery("select ConcursoPuestoAgr from ConcursoPuestoAgr ConcursoPuestoAgr, Referencias Referencias "
				+ " where ConcursoPuestoAgr.estado = Referencias.valorNum and ConcursoPuestoAgr.activo is true "
				+ " and Referencias.dominio = 'ESTADOS_GRUPO' and Referencias.descAbrev in ('EVALUACION ADJUDICADOS',"
				+ "'CON RESOLUCION ADJUDICACION','CONTRATADO','PERMANENTE D12','PERMANENTE N12','CON DECRETO',"
				+ "'FIRMADO DECRETO PRESIDENCIAL','CON RESOLUCION NOMBRAMIENTO','FIRMADO NOMBRAMIENTO','INGRESADO' )"
				+ " and ConcursoPuestoAgr.concurso.idConcurso = :idConcurso order by ConcursoPuestoAgr.descripcionGrupo ");
		q.setParameter("idConcurso", idConcurso);
		return q.getResultList();
	}

	public List<SelectItem> traerGrupoPuestoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ConcursoPuestoAgr o : traerGrupoPuesto())
			si.add(new SelectItem(o.getIdConcursoPuestoAgr(), o.getDescripcionGrupo()));
		return si;
	}

	private List<Concurso> traerConcurso() {
		Query q =
			em.createQuery("select Concurso from ConcursoPuestoAgr ConcursoPuestoAgr, Referencias Referencias, Concurso Concurso "
				+ " where ConcursoPuestoAgr.estado = Referencias.valorNum and ConcursoPuestoAgr.activo is true "
				+ " and Referencias.dominio = 'ESTADOS_GRUPO' and Referencias.descAbrev in ('EVALUACION ADJUDICADOS',"
				+ "'CON RESOLUCION ADJUDICACION','CONTRATADO','PERMANENTE D12','PERMANENTE N12','CON DECRETO',"
				+ "'FIRMADO DECRETO PRESIDENCIAL','CON RESOLUCION NOMBRAMIENTO','FIRMADO NOMBRAMIENTO','INGRESADO' )"
				+ " and ConcursoPuestoAgr.concurso.idConcurso = Concurso.idConcurso and Concurso.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo "
				+ " order by Concurso.nombre");
		q.setParameter("idConfiguracionUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		return q.getResultList();
	}

	public void actualizarTipoConcurso() {
		if (idConcurso != null) {
			Concurso concurso = em.find(Concurso.class, idConcurso);
			tipoConcurso = concurso.getDatosEspecificosTipoConc().getDescripcion();
			traerGrupoPuestoSI();
		}
	}

	public List<SelectItem> traerConcursoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Concurso o : traerConcurso())
			si.add(new SelectItem(o.getIdConcurso(), o.getNombre()));
		return si;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Long getIdGrupoPuesto() {
		return idGrupoPuesto;
	}

	public void setIdGrupoPuesto(Long idGrupoPuesto) {
		this.idGrupoPuesto = idGrupoPuesto;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getTipoExcepcion() {
		return tipoExcepcion;
	}

	public void setTipoExcepcion(String tipoExcepcion) {
		this.tipoExcepcion = tipoExcepcion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getObservacionExcepcion() {
		return observacionExcepcion;
	}

	public void setObservacionExcepcion(String observacionExcepcion) {
		this.observacionExcepcion = observacionExcepcion;
	}

	public Integer getIdTipoExcepcion() {
		return idTipoExcepcion;
	}

	public void setIdTipoExcepcion(Integer idTipoExcepcion) {
		this.idTipoExcepcion = idTipoExcepcion;
	}

	public List<ExcepcionElegibles> getlElegibles() {
		return lElegibles;
	}

	public void setlElegibles(List<ExcepcionElegibles> lElegibles) {
		this.lElegibles = lElegibles;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public String getDescGrupoVer() {
		return descGrupoVer;
	}

	public void setDescGrupoVer(String descGrupoVer) {
		this.descGrupoVer = descGrupoVer;
	}

	public String getDescTipoConcursoVer() {
		return descTipoConcursoVer;
	}

	public void setDescTipoConcursoVer(String descTipoConcursoVer) {
		this.descTipoConcursoVer = descTipoConcursoVer;
	}

	public String getDescConcursoVer() {
		return descConcursoVer;
	}

	public void setDescConcursoVer(String descConcursoVer) {
		this.descConcursoVer = descConcursoVer;
	}

	public String getTipoConcurso() {
		return tipoConcurso;
	}

	public void setTipoConcurso(String tipoConcurso) {
		this.tipoConcurso = tipoConcurso;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

}
