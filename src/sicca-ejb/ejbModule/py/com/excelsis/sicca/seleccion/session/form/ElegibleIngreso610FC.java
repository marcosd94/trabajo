package py.com.excelsis.sicca.seleccion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.ListaElegiblesDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("elegibleIngreso610FC")
public class ElegibleIngreso610FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	List<EmpleadoPuesto> lGrilla1 = new ArrayList<EmpleadoPuesto>();
	List<EmpleadoPuesto> lGrilla2 = new ArrayList<EmpleadoPuesto>();
	List<ListaElegiblesDet> lGrilla3 = new ArrayList<ListaElegiblesDet>();
	private String concurso;
	private String grupo;
	private String observacion;
	private String from;

	private Long idGR1;
	private Long idGR2;
	private int rowIdGrilla1;
	private int rowIdGrilla2;
	private int rowIdGrilla3;
	private Long idEstadoCabVacante;
	private Long idEstadoIncluido;
	ListaElegiblesDet ledChecked = null;
	EmpleadoPuesto epChecked = null;

	public void init() {
		cargarCabecera();
		cargarGrilla1();
		initIDs();
	}

	private void cargarCabecera() {
		if (nivelEntidadOeeUtil == null)
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	public void cargarGrilla1() {
		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.activo is true and EmpleadoPuesto.fechaFin is not null "
				+ " and EmpleadoPuesto.concursoPuestoAgr is not null "
				+ " and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet = :idUoDet");
		q.setParameter("idUoDet", usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		lGrilla1 = q.getResultList();

		// Resetear la grilla 2 y 3
		lGrilla3.clear();
		lGrilla2.clear();

	}

	private void initIDs() {
		idEstadoCabVacante =
			seleccionUtilFormController.buscarEstadoCab("VACANTE").getIdEstadoCab();
		idEstadoIncluido =
			seleccionUtilFormController.traerDatosEspecificos("ESTADOS DE ELEGIBLE", "INCLUIDO").getIdDatosEspecificos();
	}

	public void checkGrilla(String grilla, Integer index) {
		if (grilla.equalsIgnoreCase("GRILLA1")) {
			if (index != null && index.intValue() < lGrilla1.size()) {
				if (lGrilla1.get(index).isSelecciono()) {
					// deseleccionar todos
					for (EmpleadoPuesto o : lGrilla1) {
						o.setSelecciono(false);
					}
					// seleccionar el indicado
					lGrilla1.get(index).setSelecciono(true);
					idGR1 =
						lGrilla1.get(index).getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet();
				}
			}
			cargarGrilla2();
		} else if (grilla.equalsIgnoreCase("GRILLA2")) {
			if (index != null && index.intValue() < lGrilla2.size()) {
				if (lGrilla2.get(index).isSelecciono2()) {
					// deseleccionar todos
					for (EmpleadoPuesto o : lGrilla2) {
						o.setSelecciono2(false);
					}
					// seleccionar el indicado
					lGrilla2.get(index).setSelecciono2(true);
					EmpleadoPuesto ep = lGrilla2.get(index);
					if (ep.getPlantaCargoDet().getEstadoCab().getIdEstadoCab().longValue() == idEstadoCabVacante.longValue()) {
						idGR2 = ep.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
						cargarGrilla3();
						if (lGrilla3.size() > 0) {
							cargarGrupoConcurso(ep.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
						} else {
							cargarGrupoConcurso(null);
							statusMessages.add(Severity.ERROR, "No existen elegibles para el concurso y grupo al que pertenece el puesto seleccionado");
						}
					} else {
						lGrilla2.get(index).setSelecciono2(false);
						statusMessages.add(Severity.ERROR, "Puesto no está VACANTE");
					}
				}
			}
		} else if (grilla.equalsIgnoreCase("GRILLA3")) {
			if (index != null && index.intValue() < lGrilla3.size()) {
				if (lGrilla3.get(index).getSeleccionado()) {
					// deseleccionar todos
					for (EmpleadoPuesto o : lGrilla1) {
						o.setSelecciono(false);
					}
					// seleccionar el indicado
					lGrilla3.get(index).setSeleccionado(true);
				}
			}
		}
	}

	private void cargarGrupoConcurso(Long idGrupo) {
		if (idGrupo == null) {
			concurso = "-";
			grupo = "-";
			return;
		}
		ConcursoPuestoAgr elGrupo = em.find(ConcursoPuestoAgr.class, idGrupo);
		concurso = elGrupo.getConcurso().getNombre();
		grupo = elGrupo.getDescripcionGrupo();
	}

	public void cargarGrilla2() {
		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto, ListaElegiblesCab  ListaElegiblesCab "
				+ " where EmpleadoPuesto.activo is true and EmpleadoPuesto.fechaFin is not null "
				+ " and EmpleadoPuesto.concursoPuestoAgr is not null "
				+ " and ListaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = EmpleadoPuesto.concursoPuestoAgr.idConcursoPuestoAgr "
				+ " and ListaElegiblesCab.vtoValidezLista >= :fechaSistema "
				+ " and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet = :idUoDet");
		q.setParameter("idUoDet", idGR1);
		q.setParameter("fechaSistema", new Date());
		lGrilla2 = q.getResultList();
		// Incializar la grilla 2
		for (EmpleadoPuesto o : lGrilla2) {
			o.setSelecciono2(false);
		}
		// Resetear la grilla 3
		lGrilla3.clear();
	}

	public void cargarGrilla3() {

		Query q =
			em.createQuery("select ListaElegiblesDet from ListaElegiblesDet ListaElegiblesDet"
				+ " where ListaElegiblesDet.listaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idGrupo", idGR2);
		lGrilla3 = q.getResultList();

		// Cargar los datos de grupo y concurso

	}

	private Boolean precondSave() {
		ledChecked = null;

		for (ListaElegiblesDet o : lGrilla3) {
			if (o.getSeleccionado())
				ledChecked = o;
			break;
		}
		if (ledChecked == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un elemento de la Lista de Elegibles");
			return false;
		}
		if (ledChecked.getObsEstado() == null || ledChecked.getObsEstado().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe cargar una Observación para el registro seleccionado");
			return false;
		}
		if (ledChecked.getObsEstado().length() > 250) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Observación (250)");
			return false;
		}
		return true;
	}

	private EvalReferencialPostulante traerERP(Long idPostulacion) {
		Query q =
			em.createQuery("select EvalReferencialPostulante from EvalReferencialPostulante EvalReferencialPostulante "
				+ " where EvalReferencialPostulante.activo is true and EvalReferencialPostulante.postulacion.idPostulacion = :idPostulacion ");
		q.setParameter("idPostulacion", idPostulacion);
		List<EvalReferencialPostulante> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public String save() {
		if (precondSave()) {
			return "FAIL";
		}
		try {
			// Realizar cambios con el seleccionado de la grilla 3
			ledChecked.setDatosEspecificos(new DatosEspecificos());
			ledChecked.getDatosEspecificos().setIdDatosEspecificos(idEstadoIncluido);
			ledChecked.setDisponible(false);
			em.merge(ledChecked);

			EvalReferencialPostulante erp =
				traerERP(ledChecked.getPostulacion().getIdPostulacion());
			erp.setIncluido(true);
			erp.setSeleccionado(true);
			erp.setExcluido(false);
			erp.setUsuMod(usuarioLogueado.getCodigoUsuario());
			erp.setFechaMod(new Date());
			erp = em.merge(erp);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			e.printStackTrace();
			return "FAIL";
		}
	}

	public void cargarObservacion(int index) {
		if (index < lGrilla3.size()) {
			observacion = lGrilla3.get(index).getObsEstado();
			if (observacion == null)
				observacion = "";
		}
	}

	public List<EmpleadoPuesto> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<EmpleadoPuesto> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public List<EmpleadoPuesto> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<EmpleadoPuesto> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public List<ListaElegiblesDet> getlGrilla3() {
		return lGrilla3;
	}

	public void setlGrilla3(List<ListaElegiblesDet> lGrilla3) {
		this.lGrilla3 = lGrilla3;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public int getRowIdGrilla1() {
		return rowIdGrilla1;
	}

	public void setRowIdGrilla1(int rowIdGrilla1) {
		this.rowIdGrilla1 = rowIdGrilla1;
	}

	public int getRowIdGrilla2() {
		return rowIdGrilla2;
	}

	public void setRowIdGrilla2(int rowIdGrilla2) {
		this.rowIdGrilla2 = rowIdGrilla2;
	}

	public int getRowIdGrilla3() {
		return rowIdGrilla3;
	}

	public void setRowIdGrilla3(int rowIdGrilla3) {
		this.rowIdGrilla3 = rowIdGrilla3;
	}

}
