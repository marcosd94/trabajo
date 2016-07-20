package py.com.excelsis.sicca.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.CronogramaConcCab;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.CronogramaConcCabHome;
import py.com.excelsis.sicca.session.CptList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("seleGrupoPuesto")
public class SeleGrupoPuesto {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CronogramaConcCabHome cronogramaConcCabHome;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	Long idConcurso;
	List<ConcursoPuestoAgr> lGrupos;
	List<ComisionSeleccionCab> lComision;
	List<ComisionGrupo> lComiGrupo;
	private List<SelectItem> grupoSelecItem;
	private List<SelectItem> comisionSelecItem;
	private Long idGrupoSel;
	private Long idComiSel;
	private String fromSele;
	private Boolean deshabilitarAcciones = false;
	private String mensaje = "El registro esá siendo utilizado";
	private static String ESTADO_CREADO = "CREADO";
	private Map<String, Boolean> diccMostrarLinkGrupo = new HashMap<String, Boolean>();
	private SeleccionUtilFormController seleccionUtilFormController;
	private SeguridadUtilFormController seguridadUtilFormController;

	/**
	 * Metodo inicializador
	 */
	public void init() {
		idConcurso = grupoPuestosController.getIdConcurso();
		if (idConcurso != null) {
			/* Incidencia 1014 */
			validarOee();
			/**/
			updateGrupo();
			updateComision();
			cargarComisionGrupo();
			desHabilitarLink();
		}
		// Diccionario que almacena a que grupos se le pondrá el link eliminar
		diccMostrarLinkGrupo.clear();

	}

	private void validarOee() {
		/* Incidencia 1014 */
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (idConcurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		Concurso concurso = em.find(Concurso.class, idConcurso);

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
		/**/
	}

	private void buildComisionSelectItem(List<ComisionSeleccionCab> listSel) {
		if (comisionSelecItem == null)
			comisionSelecItem = new ArrayList<SelectItem>();
		else
			comisionSelecItem.clear();

		comisionSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ComisionSeleccionCab o : listSel) {
			comisionSelecItem.add(new SelectItem(o.getIdComisionSel(), o.getDescripcion()));
		}
	}

	public void updateComision() {
		cargarComiones();
		comisionSelecItem = new ArrayList<SelectItem>();
		buildComisionSelectItem(lComision);
	}

	private void buildGrupoSelectItem(List<ConcursoPuestoAgr> listSel) {
		if (grupoSelecItem == null)
			grupoSelecItem = new ArrayList<SelectItem>();
		else
			grupoSelecItem.clear();

		grupoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ConcursoPuestoAgr o : listSel) {
			grupoSelecItem.add(new SelectItem(o.getIdConcursoPuestoAgr(), o.getDescripcionGrupo()));
		}
	}

	public void updateGrupo() {
		cargarGrupos();
		grupoSelecItem = new ArrayList<SelectItem>();
		buildGrupoSelectItem(lGrupos);
	}

	private void cargarComisionGrupo() {
		if (lComiGrupo == null) {
			lComiGrupo = new ArrayList<ComisionGrupo>();
		}
		lComiGrupo =
			em.createQuery("select comisionGrupo from ComisionGrupo comisionGrupo inner join fetch comisionGrupo.concursoPuestoAgr inner join fetch comisionGrupo.comisionSeleccionCab inner join fetch comisionGrupo.concursoPuestoAgr.concurso"
				+ " where   "
				+ "  comisionGrupo.concursoPuestoAgr.concurso.idConcurso =  "
				+ idConcurso).getResultList();
		for (ComisionGrupo o : lComiGrupo) {
			// Se hace esto poruqe no carga los objetos como se esperaba
			if (o.getConcursoPuestoAgr().getDescripcionGrupo() == null) {
				o.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class, o.getConcursoPuestoAgr().getIdConcursoPuestoAgr()));
			}
			if (o.getComisionSeleccionCab().getDescripcion() == null) {
				o.setComisionSeleccionCab(em.find(ComisionSeleccionCab.class, o.getComisionSeleccionCab().getIdComisionSel()));
			}
		}
	}

	public Boolean precondiciones() {
		if (idGrupoSel == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Grupo ");
			return false;
		}
		if (idComiSel == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Comité de Selección");
			return false;
		}

		for (ComisionGrupo o : lComiGrupo) {

			if (o.getConcursoPuestoAgr().getIdConcursoPuestoAgr().toString().equalsIgnoreCase(idGrupoSel.toString())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU321_noRepetirGrupos"));
				return false;
			}
		}
		Concurso concurso = em.find(Concurso.class, idConcurso);
		if (concurso.getPcd()) {
			StringBuffer SQL = new StringBuffer();
			SQL.append(" SELECT ");
			SQL.append("     seleccion.org_discapacitados_persona.id_persona ");
			SQL.append(" FROM ");
			SQL.append("     seleccion.comision_seleccion_det ");
			SQL.append(" INNER JOIN seleccion.org_discapacitados_persona ");
			SQL.append(" ON ");
			SQL.append("     ( ");
			SQL.append("         seleccion.comision_seleccion_det.id_persona = ");
			SQL.append("         seleccion.org_discapacitados_persona.id_persona ");
			SQL.append("     ) ");
			SQL.append(" WHERE ");
			SQL.append("     seleccion.org_discapacitados_persona.activo = 'true' ");
			SQL.append(" AND seleccion.comision_seleccion_det.activo = 'true' ");
			SQL.append(" AND seleccion.comision_seleccion_det.id_comision_sel =  " + idComiSel);
			Query q = em.createNativeQuery(SQL.toString());
			List lista = q.getResultList();
			if (lista.size() == 0) {
				statusMessages.add(Severity.WARN, SeamResourceBundle.getBundle().getString("CU165_warnPcd"));
			}
		}
		/* Incidencia 813 */
		if (deshabilitarAcciones) {
			return false;
		}
		/*****/
		return true;
	}

	public void desHabilitarLink() {
		AdminConcursosListFormController adminConcursosListFormController =
			(AdminConcursosListFormController) Component.getInstance(AdminConcursosListFormController.class, true);
		adminConcursosListFormController.calcEstado();
		deshabilitarAcciones =
			adminConcursosListFormController.desHabilitarLink(em.find(Concurso.class, idConcurso));

	}

	public String save() {
		if (precondiciones()) {
			try {
				Date fechaAlta = new Date();
				ComisionGrupo comiGrupo = new ComisionGrupo();
				comiGrupo.setActivo(true);
				comiGrupo.setComisionSeleccionCab(new ComisionSeleccionCab());
				comiGrupo.getComisionSeleccionCab().setIdComisionSel(idComiSel);
				comiGrupo.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				comiGrupo.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idGrupoSel);
				comiGrupo.setFechaAlta(fechaAlta);
				comiGrupo.setUsuAlta(usuarioLogueado.getCodigoUsuario());

				em.persist(comiGrupo);
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				cargarComisionGrupo();
				limpiar();
				return "persisted";

			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		return null;
	}

	private ComisionGrupo calcGrupoComiEliminar(Integer elIndice) {
		if (lComiGrupo != null && lComiGrupo.size() > elIndice.intValue()) {
			return lComiGrupo.get(elIndice.intValue());
		}
		return null;
	}

	private void limpiar() {
		idComiSel = null;
		idGrupoSel = null;
	}

	private Boolean precondEliminar(Long idGrupo) {
		if (!grupoEstadoCreado(idGrupo)) {
			statusMessages.add(Severity.ERROR, "El Comité de Selección y Grupo están siendo utilizados");
			return false;
		}
		return true;
	}

	public Boolean mostrarLinkEliminar(Long idGrupo) {
		if (diccMostrarLinkGrupo.get(idGrupo.toString()) == null) {
			diccMostrarLinkGrupo.put(idGrupo.toString(), grupoEstadoCreado(idGrupo));
		}
		return diccMostrarLinkGrupo.get(idGrupo.toString());
	}

	/**
	 * Indica si un grupo esta en el estado CREADO
	 * 
	 * @return
	 */
	private Boolean grupoEstadoCreado(Long idGrupo) {
		if (seleccionUtilFormController == null) {
			seleccionUtilFormController =
				(SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
		}
		String estado = seleccionUtilFormController.getEstadoGrupo(idGrupo);
		if (estado.equalsIgnoreCase(this.ESTADO_CREADO)) {
			return true;
		}
		return false;
	}

	public String eliminar(Integer elIndice) {
		ComisionGrupo elComiGrupo = calcGrupoComiEliminar(elIndice);
		if (precondEliminar(elComiGrupo.getConcursoPuestoAgr().getIdConcursoPuestoAgr())) {
			try {
				elComiGrupo = em.find(ComisionGrupo.class, elComiGrupo.getIdComisionGrupo());
				em.remove(elComiGrupo);
				em.flush();
				lComiGrupo.remove(elIndice.intValue());
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				cargarComisionGrupo();
				return "persisted";

			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		return null;
	}

	private void cargarGrupos() {
		lGrupos =
			em.createQuery("select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr,Concurso concurso where concursoPuestoAgr.concurso = concurso AND concurso.idConcurso ="
				+ idConcurso
				+ " AND concursoPuestoAgr.activo = true "
				+ " order by concursoPuestoAgr.descripcionGrupo").getResultList();
		System.out.println("---");
	}

	private void cargarComiones() {
		if (lComision != null) {
			lComision.clear();
		} else {
			lComision = new ArrayList<ComisionSeleccionCab>();
		}
		Query q =
			em.createQuery("select ComisionSeleccionCab from ComisionSeleccionCab ComisionSeleccionCab "
				+ " where   ComisionSeleccionCab.concurso.idConcurso = " + idConcurso);
		lComision = q.getResultList();
	}

	public List<ConcursoPuestoAgr> getlGrupos() {
		return lGrupos;
	}

	public void setlGrupos(List<ConcursoPuestoAgr> lGrupos) {
		this.lGrupos = lGrupos;
	}

	public List<ComisionSeleccionCab> getlComision() {
		return lComision;
	}

	public void setlComision(List<ComisionSeleccionCab> lComision) {
		this.lComision = lComision;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public List<ComisionGrupo> getlComiGrupo() {
		return lComiGrupo;
	}

	public void setlComiGrupo(List<ComisionGrupo> lComiGrupo) {
		this.lComiGrupo = lComiGrupo;
	}

	public List<SelectItem> getGrupoSelecItem() {
		return grupoSelecItem;
	}

	public void setGrupoSelecItem(List<SelectItem> grupoSelecItem) {
		this.grupoSelecItem = grupoSelecItem;
	}

	public List<SelectItem> getComisionSelecItem() {
		return comisionSelecItem;
	}

	public void setComisionSelecItem(List<SelectItem> comisionSelecItem) {
		this.comisionSelecItem = comisionSelecItem;
	}

	public Long getIdGrupoSel() {
		return idGrupoSel;
	}

	public void setIdGrupoSel(Long idGrupoSel) {
		this.idGrupoSel = idGrupoSel;
	}

	public Long getIdComiSel() {
		return idComiSel;
	}

	public void setIdComiSel(Long idComiSel) {
		this.idComiSel = idComiSel;
	}

	public String getFromSele() {
		return fromSele;
	}

	public void setFromSele(String fromSele) {
		this.fromSele = fromSele;
	}

	public Boolean getDeshabilitarAcciones() {
		return deshabilitarAcciones;
	}

	public void setDeshabilitarAcciones(Boolean deshabilitarAcciones) {
		this.deshabilitarAcciones = deshabilitarAcciones;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
