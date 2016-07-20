package py.com.excelsis.sicca.seleccion.session.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TipoExcepcion;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;

import py.com.excelsis.sicca.seguridad.entity.Usuario;

import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("admDesConGruPueEditFC")
public class AdmDesConGruPueEditFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	Date fechaSistema = new Date();
	String motivo;
	Date fechaDesbloqueo;
	Long idExcepcionConcurso;
	Long idExcepcionGrupoPuestoAgr;
	List<ExcepcionGrPuesto> lGrilla = new ArrayList<ExcepcionGrPuesto>();
	private List<SelectItem> concursosSI = new ArrayList<SelectItem>();
	private List<SelectItem> grupoPuestosSI = new ArrayList<SelectItem>();
	TipoExcepcion tipoExcepcion;
	Integer idTipoExcepcion;
	Long idExcepcion;
	private java.util.Date fechaDebloqueo;
	private Boolean renderPanel1;
	private Boolean renderPanel2;
	private Boolean renderPanel3;
	private Excepcion excepcionConcursoSel;
	private ExcepcionGrPuesto excepcionGrupoPuestoAgrSel;
	private String direccionFisica;
	private String nombrePantalla;
	private String descConcurso;
	private String descGrupo;

	private Excepcion excepcionGenerada;

	public void init() {
		if (idTipoExcepcion != null) {
			tipoExcepcion = TipoExcepcion.getTipoExcepcionId(idTipoExcepcion);
			grupoPuestosSelectItems();
			concursoSelectItems();
			renderPanel1 =
				tipoExcepcion.getValor().equalsIgnoreCase(TipoExcepcion.BLOQUEO_CONCURSO.getValor());
			renderPanel2 =
				tipoExcepcion.getValor().equalsIgnoreCase(TipoExcepcion.BLOQUEO_GRUPO.getValor());
			renderPanel3 =
				tipoExcepcion.getValor().equalsIgnoreCase(TipoExcepcion.BLOQUEO_PUESTO.getValor());
		}
	}

	private Date calcFechaDeBloqueo(Long idExcepcionDesbloqueo) {
		Query q =
			em.createQuery("select ExcepcionGrPuesto from ExcepcionGrPuesto ExcepcionGrPuesto "
				+ "where ExcepcionGrPuesto.excepcionByIdExcepcionDesbloq = "
				+ idExcepcionDesbloqueo);
		List<ExcepcionGrPuesto> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getFechaAlta();
		}
		return null;
	}

	public void initVer() {
		if (idExcepcion != null) {
			excepcionGenerada = em.find(Excepcion.class, idExcepcion);
			tipoExcepcion = TipoExcepcion.findTipoExcepcion(excepcionGenerada.getTipo());
			fechaDebloqueo = calcFechaDeBloqueo(idExcepcion);

			grupoPuestosSelectItems();
			concursoSelectItems();
			if (excepcionGenerada.getConcurso() != null) {
				descConcurso = excepcionGenerada.getConcurso().getNombre();
			}
			if (excepcionGenerada.getConcursoPuestoAgr() != null) {
				descGrupo = excepcionGenerada.getConcursoPuestoAgr().getDescripcionGrupo();
			}
			motivo = excepcionGenerada.getObservacion();
			fechaSistema = excepcionGenerada.getFechaAlta();
			renderPanel1 =
				tipoExcepcion.getValor().equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_POR_CONCURSO.getValor());
			renderPanel2 =
				tipoExcepcion.getValor().equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_POR_GRUPO.getValor());
			renderPanel3 =
				tipoExcepcion.getValor().equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_POR_PUESTO.getValor());
			cargarGrilla();
		}
	}

	private void cargarGrilla() {
		if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_GRUPO.getId().intValue()) {
			cargarListaGrid(idExcepcion, null);
		} else if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_PUESTO.getId().intValue()) {
			cargarListaGrid(idExcepcion, excepcionGenerada.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		}
	}

	private void obtenerDireccionFisica() {
		Calendar cal = Calendar.getInstance();
		if (excepcionGenerada != null && direccionFisica != null
			&& direccionFisica.trim().isEmpty()) {
			Concurso concurso = excepcionGenerada.getConcurso();
			direccionFisica =
				"//SICCA//" + cal.get(Calendar.YEAR) + "OEE" + "//"
					+ concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "//"
					+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
					+ concurso.getIdConcurso();
		}
	}

	public void refreshGrupo() {
		if (idExcepcionConcurso != null) {
			grupoPuestosSelectItems();
			idExcepcionGrupoPuestoAgr = null;
			lGrilla.clear();
		}
	}

	public void refreshFechaDesbloqueo() {
		System.out.println(idExcepcionConcurso);
		if (idExcepcionConcurso != null) {
			excepcionConcursoSel = em.find(Excepcion.class, idExcepcionConcurso);
			if (idExcepcionGrupoPuestoAgr != null)
				excepcionGrupoPuestoAgrSel =
					em.find(ExcepcionGrPuesto.class, idExcepcionGrupoPuestoAgr);

			// Para la fecha de debloqueo
			fechaDebloqueo = excepcionConcursoSel.getFechaAlta();
			if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_GRUPO.getId().intValue()) {
				// cargar grilla
				cargarListaGrid(idExcepcionConcurso, null);
			} else if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_PUESTO.getId().intValue()) {
				cargarListaGrid(idExcepcionConcurso, idExcepcionGrupoPuestoAgr);
			}
		}
	}

	private void cargarListaGrid(Long idExcepcion, Long idGrupo) {
		if (idGrupo == null) {
			// Por Grupo
			Query q =
				em.createQuery("select ExcepcionGrPuesto from ExcepcionGrPuesto ExcepcionGrPuesto "
					+ "where ExcepcionGrPuesto.excepcion.idExcepcion = " + idExcepcion);
			lGrilla = q.getResultList();
		} else {
			// Por Puesto
			Query q =
				em.createQuery("select ExcepcionGrPuesto from ExcepcionGrPuesto ExcepcionGrPuesto "
					+ "where ExcepcionGrPuesto.excepcion.idExcepcion = " + idExcepcion + " "
					+ " and ExcepcionGrPuesto.concursoPuestoAgr.idConcursoPuestoAgr =  " + idGrupo);
			lGrilla = q.getResultList();
		}
	}

	private Boolean precondSavePorConcurso() {
		if (motivo != null && !motivo.trim().isEmpty() && motivo.length() > 100) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Motivo (1000)");
			return false;
		}
		return true;
	}

	private Boolean precondSaverPorGrupo() {
		if (motivo != null && !motivo.trim().isEmpty() && motivo.length() > 100) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Motivo (1000)");
			return false;
		}
		return true;
	}

	private Boolean savePorGrupo() {
		if (!precondSaverPorGrupo())
			return false;

		excepcionGenerada = new Excepcion();
		excepcionGenerada.setActivo(true);
		excepcionGenerada.setTipo(TipoExcepcion.DESBLOQUEO_POR_PUESTO.getDescripcion());
		excepcionGenerada.setConcurso(new Concurso());
		excepcionGenerada.getConcurso().setIdConcurso(excepcionConcursoSel.getConcurso().getIdConcurso());
		if (excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr() != null)
			excepcionGenerada.setConcursoPuestoAgr(excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr());
		excepcionGenerada.setEstado("REGISTRADO");
		excepcionGenerada.setObservacion(motivo);
		excepcionGenerada.setFechaDesbloqueo(new Date());
		excepcionGenerada.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		excepcionGenerada.setFechaAlta(new Date());
		em.persist(excepcionGenerada);
		Iterator iter1 = (Iterator) lGrilla.iterator();
		Iterator iter2 = null;
		ExcepcionGrPuesto o = null;
		ConcursoPuestoDet p = null;
		Integer cantSeleccionados = 0;
		/* Crear un registro por cada registro chequeado de la grilla “Grupo de Puestos” en la tabla “SEL_EXCEPCION_GR_PUESTO” */
		while (iter1.hasNext()) {
			o = (ExcepcionGrPuesto) iter1.next();
			if (o.isSelected()) {
				cantSeleccionados++;
				/* Crear un registro por cada registro chequeado de la grilla “Puestos” en la tabla “SEL_EXCEPCION_GR_PUESTO” */
				ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
				if (excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr() != null)
					excepcionGrPuesto.setConcursoPuestoAgr(excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr());
				excepcionGrPuesto.setPlantaCargoDet(new PlantaCargoDet());
				excepcionGrPuesto.getPlantaCargoDet().setIdPlantaCargoDet(o.getPlantaCargoDet().getIdPlantaCargoDet());
				excepcionGrPuesto.setExcepcion(new Excepcion());
				excepcionGrPuesto.getExcepcion().setIdExcepcion(excepcionGenerada.getIdExcepcion());
				excepcionGrPuesto.setActivo(true);
				excepcionGrPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				excepcionGrPuesto.setFechaAlta(new Date());
				em.persist(excepcionGrPuesto);

				/* Actualizar la tabla SEL_EXCEPCION_GR_PUESTO del registro de bloqueo según los grupos de puestos chequeados en la grilla */
				o.setFechaDesbloqueo(new Date());
				o.setFechaModif(new Date());
				o.setUsuModif(usuarioLogueado.getCodigoUsuario());
				o.setExcepcionByIdExcepcionDesbloq(excepcionGenerada);
				o = em.merge(o);

				/* Cambiar el estado de los grupos de puestos que se encuentran seleccionados en la grilla */
				o.getConcursoPuestoAgr().setEstado(o.getIdEstadoGr().intValue());
				o.getConcursoPuestoAgr().setActivo(true);
				o.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
				o.getConcursoPuestoAgr().setFechaMod(new Date());
				o.setConcursoPuestoAgr(em.merge(o.getConcursoPuestoAgr()));
				/* Cambiar el estado de SEL_CONCURSO_PUESTO_DET donde figure el concurso_puesto_agr que había sido chequeado en la grilla y activar los mismos */
				iter2 = o.getConcursoPuestoAgr().getConcursoPuestoDets().iterator();
				while (iter2.hasNext()) {
					p = (ConcursoPuestoDet) iter2.next();
					p.setEstadoDet(o.getEstadoDet());
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					p.setActivo(true);
					p = em.merge(p);
					/*
					 * Cambiar el estado en PLANTA_CARGO_DET del puesto relacionado al Grupo de puestos que se encuentra en la tabla SEL_CONCURSO_PUESTO_DET, guardando en las columnas de la tabla
					 * PLANTA_CARGO_DET
					 */
					p.getPlantaCargoDet().setActivo(true);
					p.getPlantaCargoDet().setEstadoDet(o.getEstadoDet());
					p.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.getPlantaCargoDet().setFechaMod(new Date());
					p.setPlantaCargoDet(em.merge(p.getPlantaCargoDet()));
					/* Registrar el histórico de cambios de estados de cada Puesto en la tabla HISTORICOS_ESTADO, registrando el estado actual */
					HistoricosEstado historicosEstado = new HistoricosEstado();
					historicosEstado.setPlantaCargoDet(new PlantaCargoDet());
					historicosEstado.getPlantaCargoDet().setIdPlantaCargoDet(p.getPlantaCargoDet().getIdPlantaCargoDet());
					historicosEstado.setEstadoCab(p.getPlantaCargoDet().getEstadoCab());
					historicosEstado.setFechaMod(new Date());
					historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.persist(historicosEstado);
				}

			}
		}

		return true;
	}

	private ExcepcionGrPuesto buscarExcepcion(Long idExcepcion, Long idConcursoPuestoAgr, Long idPuesto) {
		if (idPuesto == null) {

			Query q =
				em.createQuery("select ExcepcionGrPuesto from ExcepcionGrPuesto ExcepcionGrPuesto "
					+ "where ExcepcionGrPuesto.excepcion.idExcepcion = " + idExcepcion + " "
					+ "and ExcepcionGrPuesto.concursoPuestoAgr.idConcursoPuestoAgr= "
					+ idConcursoPuestoAgr);
			ExcepcionGrPuesto excepcionGrPuesto =
				(py.com.excelsis.sicca.entity.ExcepcionGrPuesto) q.getSingleResult();
			return excepcionGrPuesto;
		} else {

			Query q =
				em.createQuery("select ExcepcionGrPuesto FROM PlantaCargoDet PlantaCargoDet, IN (PlantaCargoDet.excepcionGrPuestos) ExcepcionGrPuesto "
					+ " where PlantaCargoDet.idPlantaCargoDet = "
					+ idPuesto
					+ " and ExcepcionGrPuesto.excepcion.idExcepcion = " + idExcepcion);
			ExcepcionGrPuesto excepcionGrPuesto =
				(py.com.excelsis.sicca.entity.ExcepcionGrPuesto) q.getSingleResult();
			return excepcionGrPuesto;
		}
	}

	@Transactional(TransactionPropagationType.MANDATORY)
	private Boolean savePorConcurso() {
		if (!precondSavePorConcurso())
			return false;

		excepcionGenerada = new Excepcion();
		excepcionGenerada.setActivo(true);
		excepcionGenerada.setTipo(TipoExcepcion.DESBLOQUEO_POR_CONCURSO.getDescripcion());
		excepcionGenerada.setConcurso(new Concurso());
		excepcionGenerada.getConcurso().setIdConcurso(excepcionConcursoSel.getConcurso().getIdConcurso());
		excepcionGenerada.setEstado("REGISTRADO");
		excepcionGenerada.setObservacion(motivo);
		excepcionGenerada.setFechaDesbloqueo(new Date());
		excepcionGenerada.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		excepcionGenerada.setFechaAlta(new Date());
		excepcionGenerada.setFechaDesbloqueo(new Date());
		em.persist(excepcionGenerada);
		/* Cambiar el estado del Concurso */
		excepcionConcursoSel.getConcurso().setEstado(excepcionConcursoSel.getIdEstadoConc());
		excepcionConcursoSel.getConcurso().setUsuMod(usuarioLogueado.getCodigoUsuario());
		excepcionConcursoSel.getConcurso().setFechaMod(new Date());
		excepcionConcursoSel.getConcurso().setActivo(true);
		excepcionConcursoSel.setConcurso(em.merge(excepcionConcursoSel.getConcurso()));

		Iterator iter1 =
			(Iterator) excepcionConcursoSel.getConcurso().getConcursoPuestoAgrs().iterator();
		Iterator iter2 = null;
		ConcursoPuestoAgr o = null;
		ConcursoPuestoDet p = null;

		/* Crear un registro por cada registro chequeado de la grilla “Grupo de Puestos” en la tabla “SEL_EXCEPCION_GR_PUESTO” */
		while (iter1.hasNext()) {
			o = (ConcursoPuestoAgr) iter1.next();
			/* Crear un registro por cada “Grupo de Puestos” relacionado al concurso en la tabla “SEL_EXCEPCION_GR_PUESTO” */
			ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
			excepcionGrPuesto.setConcursoPuestoAgr(o);
			excepcionGrPuesto.setExcepcion(new Excepcion());
			excepcionGrPuesto.getExcepcion().setIdExcepcion(excepcionGenerada.getIdExcepcion());
			excepcionGrPuesto.setActivo(true);
			excepcionGrPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			excepcionGrPuesto.setFechaAlta(new Date());
			em.persist(excepcionGrPuesto);

			/* Cambiar el estado de todos los Grupos de Puestos relacionados al concurso y activarlos. */
			// o.setEstado(excepcionConcursoSel.getIdEstadoGr().intValue());
			o.setEstado(buscarExcepcion(idExcepcionConcurso, excepcionConcursoSel.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), null).getIdEstadoGr().intValue());
			o.setUsuMod(usuarioLogueado.getCodigoUsuario());
			o.setFechaMod(new Date());
			o.setActivo(true);
			o = em.merge(o);

			/* Cambiar el estado de SEL_CONCURSO_PUESTO_DET donde figure el concurso_puesto_agr que había sido chequeado en la grilla y activar los mismos */
			iter2 = o.getConcursoPuestoDets().iterator();
			while (iter2.hasNext()) {
				p = (ConcursoPuestoDet) iter2.next();
				// p.setEstadoDet(excepcionConcursoSel.getEstadoDet());
				p.setEstadoDet(buscarExcepcion(idExcepcionConcurso, excepcionConcursoSel.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), null).getEstadoDet());
				p.setUsuMod(usuarioLogueado.getCodigoUsuario());
				p.setFechaMod(new Date());
				p.setActivo(true);
				p = em.merge(p);
				/*
				 * Cambiar el estado en PLANTA_CARGO_DET del puesto relacionado al Grupo de puestos que se encuentra en la tabla SEL_CONCURSO_PUESTO_DET, guardando en las columnas de la tabla
				 * PLANTA_CARGO_DET
				 */
				p.getPlantaCargoDet().setActivo(true);
				// p.getPlantaCargoDet().setEstadoDet(excepcionConcursoSel.getEstadoDet());
				p.getPlantaCargoDet().setEstadoDet(buscarExcepcion(idExcepcionConcurso, excepcionConcursoSel.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), null).getEstadoDet());
				p.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
				p.getPlantaCargoDet().setFechaMod(new Date());
				p.setPlantaCargoDet(em.merge(p.getPlantaCargoDet()));
				/* Registrar el histórico de cambios de estados de cada Puesto en la tabla HISTORICOS_ESTADO, registrando el estado actual */
				HistoricosEstado historicosEstado = new HistoricosEstado();
				historicosEstado.setPlantaCargoDet(new PlantaCargoDet());
				historicosEstado.getPlantaCargoDet().setIdPlantaCargoDet(p.getPlantaCargoDet().getIdPlantaCargoDet());
				historicosEstado.setEstadoCab(p.getPlantaCargoDet().getEstadoCab());
				historicosEstado.setFechaMod(new Date());
				historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.persist(historicosEstado);
			}
		}

		return true;
	}

	private Boolean precondSaverPorPuesto() {
		if (motivo != null && !motivo.trim().isEmpty() && motivo.length() > 100) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Motivo (1000)");
			return false;
		}
		return true;
	}

	private Boolean savePorPuesto() {
		if (!precondSaverPorPuesto())
			return false;

		excepcionGenerada = new Excepcion();
		excepcionGenerada.setActivo(true);
		excepcionGenerada.setTipo(TipoExcepcion.DESBLOQUEO_POR_PUESTO.getDescripcion());
		excepcionGenerada.setConcurso(new Concurso());
		excepcionGenerada.getConcurso().setIdConcurso(excepcionConcursoSel.getConcurso().getIdConcurso());
		if (excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr() != null)
			excepcionGenerada.setConcursoPuestoAgr(excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr());
		excepcionGenerada.setEstado("REGISTRADO");
		excepcionGenerada.setObservacion(motivo);
		excepcionGenerada.setFechaDesbloqueo(new Date());
		excepcionGenerada.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		excepcionGenerada.setFechaAlta(new Date());
		em.persist(excepcionGenerada);
		Iterator iter1 = (Iterator) lGrilla.iterator();
		Iterator iter2 = null;
		ExcepcionGrPuesto o = null;
		ConcursoPuestoDet p = null;
		Integer cantSeleccionados = 0;
		ConcursoPuestoDet concursoPuestoDet = null;
		EstadoDet estadoDet = null;
		/*
		 * 1. Buscar en la tabla SEL_CONCURSO_PUESTO_DET un puesto activo asociado al grupo de puestos que figura en el combo. Tomar el ID_ESTADO_DET (correspondiente al bloqueo) para copiarle a los
		 * puestos que serán desbloqueados
		 */
		if (excepcionGrupoPuestoAgrSel.getIdEstadoGr().intValue() == excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr().getEstado()) {
			Query q =
				em.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet where ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr().getIdConcursoPuestoAgr()
					+ " and ConcursoPuestoDet.activo is true ");
			List<ConcursoPuestoDet> lista = q.getResultList();
			if (lista.size() > 0) {
				concursoPuestoDet = lista.get(0);
				estadoDet = concursoPuestoDet.getEstadoDet();
			}
		}
		/* Crear un registro por cada registro chequeado de la grilla “Grupo de Puestos” en la tabla “SEL_EXCEPCION_GR_PUESTO” */
		while (iter1.hasNext()) {
			o = (ExcepcionGrPuesto) iter1.next();
			if (o.isSelected()) {
				cantSeleccionados++;
				/* Crear un registro por cada registro chequeado de la grilla “Puestos” en la tabla “SEL_EXCEPCION_GR_PUESTO” */
				ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
				if (excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr() != null)
					excepcionGrPuesto.setConcursoPuestoAgr(excepcionGrupoPuestoAgrSel.getConcursoPuestoAgr());
				excepcionGrPuesto.setPlantaCargoDet(new PlantaCargoDet());
				excepcionGrPuesto.getPlantaCargoDet().setIdPlantaCargoDet(o.getPlantaCargoDet().getIdPlantaCargoDet());
				excepcionGrPuesto.setExcepcion(new Excepcion());
				excepcionGrPuesto.getExcepcion().setIdExcepcion(excepcionGenerada.getIdExcepcion());
				excepcionGrPuesto.setActivo(true);
				excepcionGrPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				excepcionGrPuesto.setFechaAlta(new Date());
				em.persist(excepcionGrPuesto);

				/* Actualizar la tabla SEL_EXCEPCION_GR_PUESTO del registro de bloqueo según los grupos de puestos chequeados en la grilla */
				o.setFechaDesbloqueo(new Date());
				o.setFechaModif(new Date());
				o.setUsuModif(usuarioLogueado.getCodigoUsuario());
				o.setExcepcionByIdExcepcionDesbloq(excepcionGenerada);
				o = em.merge(o);
				/*
				 * 1. Buscar en la tabla SEL_CONCURSO_PUESTO_DET un puesto activo asociado al grupo de puestos que figura en el combo. Tomar el ID_ESTADO_DET (correspondiente al bloqueo) para copiarle
				 * a los puestos que serán desbloqueados
				 */
				if (estadoDet != null) {
					iter2 = p.getConcursoPuestoAgr().getConcursoPuestoDets().iterator();
					/*
					 * 2. Tomar cada ID_PLANTA_CARGO_DET de la tabla SEL_EXCEPCION_GR_PUESTO e ir a activar el registro en SEL_CONCURSO_PUESTO_DET donde se encuentre asociado con el grupo de puestos
					 * correspondiente y copiar el mismo estado en ID_ESTADO_DET obtenido en el paso anterior. Actualizar las columnas USU_MOD Y FECHA_MOD
					 */
					while (iter2.hasNext()) {
						p = (ConcursoPuestoDet) iter2.next();
						p.setActivo(true);
						p.setEstadoDet(estadoDet);
						p.setUsuMod(usuarioLogueado.getCodigoUsuario());
						p.setFechaMod(new Date());
						p = em.merge(p);
						/*
						 * 3. Ir a PLANTA_CARGO_DET y activar los registros que figuran en SEL_EXCEPCION_GR_PUESTO y en ID_ESTADO_DET copiar el mismo estado obtenido en el paso 1. Actualizar las
						 * columnas USU_MOD Y FECHA_MOD
						 */
						p.getPlantaCargoDet().setActivo(true);
						p.getPlantaCargoDet().setEstadoDet(estadoDet);
						p.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
						p.getPlantaCargoDet().setFechaMod(new Date());
						p.setPlantaCargoDet(em.merge(p.getPlantaCargoDet()));
						/* 4. Registrar el histórico de cambios de estados de cada Puesto en la tabla HISTORICOS_ESTADO, registrando el estado actual */
						HistoricosEstado historicosEstado = new HistoricosEstado();
						historicosEstado.setPlantaCargoDet(new PlantaCargoDet());
						historicosEstado.getPlantaCargoDet().setIdPlantaCargoDet(p.getPlantaCargoDet().getIdPlantaCargoDet());
						historicosEstado.setEstadoCab(p.getPlantaCargoDet().getEstadoCab());
						historicosEstado.setFechaMod(new Date());
						historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.persist(historicosEstado);
					}
				}

			}
		}
		/*
		 * Si todos los Grupos de Puestos fueron seleccionados en la grilla, deberá actualizarse la columna fecha_desbloqueo de la tabla EXCEPCION correspondiente al registro del bloqueo. Caso
		 * contrario omitir este paso
		 */
		if (cantSeleccionados.intValue() == lGrilla.size()) {
			excepcionConcursoSel.setFechaDesbloqueo(new Date());
			excepcionConcursoSel = em.merge(excepcionConcursoSel);
		}

		return true;
	}

	@Transactional(TransactionPropagationType.MANDATORY)
	public String save() {
		try {
			if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_CONCURSO.getId().intValue()) {
				savePorConcurso();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				em.flush();
			} else if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_GRUPO.getId().intValue()) {
				savePorGrupo();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				em.flush();
			} else if (tipoExcepcion.getId().intValue() == TipoExcepcion.BLOQUEO_PUESTO.getId().intValue()) {
				savePorPuesto();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				em.flush();
			}
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			e.printStackTrace();

			return null;
		}
		return "OK";
	}

	private List<ExcepcionGrPuesto> getGrupoPuestos() {
		if (tipoExcepcion != null && idExcepcionConcurso != null) {
			Query q =
				em.createQuery("select distinct ExcepcionGrPuesto from ExcepcionGrPuesto ExcepcionGrPuesto  WHERE"
					+ " ExcepcionGrPuesto.excepcion.idExcepcion = "
					+ idExcepcionConcurso
					+ " and ExcepcionGrPuesto.activo is true");

			return q.getResultList();
		}
		return new ArrayList<ExcepcionGrPuesto>();
	}

	public List<SelectItem> grupoPuestosSelectItems() {
		grupoPuestosSI.clear();
		grupoPuestosSI.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		List<String> controlExiste = new ArrayList<String>();
		for (ExcepcionGrPuesto o : getGrupoPuestos()) {
			if (!controlExiste.contains(o.getConcursoPuestoAgr().getIdConcursoPuestoAgr().toString())) {
				System.out.println("-- "
					+ o.getConcursoPuestoAgr().getIdConcursoPuestoAgr().toString());
				controlExiste.add(o.getConcursoPuestoAgr().getIdConcursoPuestoAgr().toString());
				grupoPuestosSI.add(new SelectItem(o.getIdExcepcionGrPuesto(), ""
					+ o.getConcursoPuestoAgr().getDescripcionGrupo()));
			}
		}
		return grupoPuestosSI;
	}

	private List<Excepcion> getConcurso() {
		if (tipoExcepcion != null) {
			Query q =
				em.createQuery("select Excepcion from Excepcion Excepcion JOIN Excepcion.concurso concurso"
					+ " WHERE Excepcion.tipo ='"
					+ tipoExcepcion.getDescripcion()
					+ "' and Excepcion.fechaLimite < :fechaSistema and Excepcion.fechaDesbloqueo is null and Excepcion.activo is true ");
			q.setParameter("fechaSistema", new Date(), TemporalType.DATE);
			List<Excepcion> lista = q.getResultList();
			return lista;
		}
		return null;
	}

	public Date getFechaDesbloqueo() {
		return fechaDesbloqueo;
	}

	public void setFechaDesbloqueo(Date fechaDesbloqueo) {
		this.fechaDesbloqueo = fechaDesbloqueo;
	}

	public Long getIdExcepcionConcurso() {
		return idExcepcionConcurso;
	}

	public void setIdExcepcionConcurso(Long idExcepcionConcurso) {
		this.idExcepcionConcurso = idExcepcionConcurso;
	}

	public List<SelectItem> getConcursosSI() {
		return concursosSI;
	}

	public void setConcursosSI(List<SelectItem> concursosSI) {
		this.concursosSI = concursosSI;
	}

	public List<SelectItem> getGrupoPuestosSI() {
		return grupoPuestosSI;
	}

	public void setGrupoPuestosSI(List<SelectItem> grupoPuestosSI) {
		this.grupoPuestosSI = grupoPuestosSI;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public List<SelectItem> concursoSelectItems() {
		concursosSI.clear();
		concursosSI.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Excepcion o : getConcurso()) {
			System.out.println("## " + o.getIdExcepcion() + " ## "
				+ o.getConcurso().getIdConcurso() + " ## " + o.getConcurso().getNombre());
			concursosSI.add(new SelectItem(o.getIdExcepcion(), "" + o.getConcurso().getNombre()));
		}

		return concursosSI;
	}

	public Date getFechaSistema() {
		return fechaSistema;
	}

	public void setFechaSistema(Date fechaSistema) {
		this.fechaSistema = fechaSistema;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Boolean getRenderPanel1() {
		return renderPanel1;
	}

	public void setRenderPanel1(Boolean renderPanel1) {
		this.renderPanel1 = renderPanel1;
	}

	public Boolean getRenderPanel2() {
		return renderPanel2;
	}

	public void setRenderPanel2(Boolean renderPanel2) {
		this.renderPanel2 = renderPanel2;
	}

	public Boolean getRenderPanel3() {
		return renderPanel3;
	}

	public void setRenderPanel3(Boolean renderPanel3) {
		this.renderPanel3 = renderPanel3;
	}

	public Integer getIdTipoExcepcion() {
		return idTipoExcepcion;
	}

	public void setIdTipoExcepcion(Integer idTipoExcepcion) {
		this.idTipoExcepcion = idTipoExcepcion;
	}

	public Date getFechaDebloqueo() {
		return fechaDebloqueo;
	}

	public void setFechaDebloqueo(Date fechaDebloqueo) {
		this.fechaDebloqueo = fechaDebloqueo;
	}

	public List<ExcepcionGrPuesto> getlGrilla() {
		return lGrilla;
	}

	public void setlGrilla(List<ExcepcionGrPuesto> lGrilla) {
		this.lGrilla = lGrilla;
	}

	public Long getIdExcepcionGrupoPuestoAgr() {
		return idExcepcionGrupoPuestoAgr;
	}

	public void setIdExcepcionGrupoPuestoAgr(Long idExcepcionGrupoPuestoAgr) {
		this.idExcepcionGrupoPuestoAgr = idExcepcionGrupoPuestoAgr;
	}

	public String getDireccionFisica() {
		obtenerDireccionFisica();
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getNombrePantalla() {
		if (tipoExcepcion != null)
			nombrePantalla = "Cargar " + tipoExcepcion.getDescripcion();
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Excepcion getExcepcionGenerada() {
		return excepcionGenerada;
	}

	public void setExcepcionGenerada(Excepcion excepcionGenerada) {
		this.excepcionGenerada = excepcionGenerada;
	}

	public String getDescConcurso() {
		return descConcurso;
	}

	public void setDescConcurso(String descConcurso) {
		this.descConcurso = descConcurso;
	}

	public String getDescGrupo() {
		return descGrupo;
	}

	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}

}
