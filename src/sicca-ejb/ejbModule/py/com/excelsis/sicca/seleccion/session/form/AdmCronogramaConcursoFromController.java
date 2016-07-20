package py.com.excelsis.sicca.seleccion.session.form;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
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
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.CronogramaConcCab;
import py.com.excelsis.sicca.entity.CronogramaConcDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.CronogramaConcCabHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("AdmCronogramaConcursoFromController")
public class AdmCronogramaConcursoFromController {
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

	private Boolean modoEditado;
	private List<SelectItem> actividadSelecItem;
	private BigInteger actividadId;
	private String lugar;
	private java.util.Date fechaDesde;
	private java.util.Date fechaHasta;
	SimpleDateFormat sdfHORA = new SimpleDateFormat("HH:mm");
	private List<CronogramaConcDet> lActividades;
	private Boolean modoConcurso = false;
	private String formLevel1 = "";

	private Long idCronogramaConcDet;
	private Long idCronogramaConcCab;

	private String fromConcurso;
	private CronogramaConcCab cronogramaConcCab;
	private CronogramaConcDet cronogramaConcDet;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}


	public String saveCabecera() {
		try {
			if (precondicionesCabecera()) {
				cronogramaConcCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				cronogramaConcCab.setFechaAlta(new Date());
				cronogramaConcCab.setActivo(true);
				if (!modoConcurso) {
					cronogramaConcCab.setConcursoPuestoAgr(new ConcursoPuestoAgr());
					cronogramaConcCab.getConcursoPuestoAgr().setIdConcursoPuestoAgr(grupoPuestosController.getIdConcursoPuestoAgr());
				} else {
					cronogramaConcCab.setConcurso(new Concurso());
					cronogramaConcCab.getConcurso().setIdConcurso(grupoPuestosController.getConcursoPuestoAgr().getConcurso().getIdConcurso());
				}
				em.persist(cronogramaConcCab);
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return "FAIL";
		}
		return "OK";
	}

	public Boolean precondicionesCabecera() {
		if (cronogramaConcCab.getNroCronograma() == 0) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU252_nroCornoMayorCero"));
			return false;
		}
		if (cronogramaConcCab.getDescripcion() == null
			|| (cronogramaConcCab.getDescripcion() != null && cronogramaConcCab.getDescripcion().trim().isEmpty())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU252_descNoVacio"));
			return false;
		}
		return true;
	}

	/**
	 * Método que construye el selectItem de Actividad
	 */
	private void buildActividadSelectItem() {
		String sql =
			"SELECT DATOS_ESPECIFICOS.ID_DATOS_ESPECIFICOS,DATOS_ESPECIFICOS.DESCRIPCION"
				+ " FROM 	SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS"
				+ " JOIN	SELECCION.DATOS_GENERALES DATOS_GENERALES"
				+ " ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES"
				+ " WHERE "
				+ " DATOS_GENERALES.DESCRIPCION = 'ACTIVIDADES/CRONOGRAMA DEL CONCURSO'"
				+ " AND DATOS_ESPECIFICOS.ACTIVO = TRUE ORDER BY DATOS_ESPECIFICOS.DESCRIPCION";
		List<Object> elResultList = em.createNativeQuery(sql).getResultList();
		if (elResultList.size() > 0) {
			if (actividadSelecItem == null)
				actividadSelecItem = new ArrayList<SelectItem>();
			else
				actividadSelecItem.clear();
			actividadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for (Object r : elResultList) {
				System.out.println("La clase: " + (((Object[]) r)[0]).getClass());
				actividadSelecItem.add(new SelectItem((BigInteger) ((Object[]) r)[0], (String) ((Object[]) r)[1]));
			}
		}
	}

	/**
	 * Método que controla las condiciones antes de agregar un detalle
	 */
	private Boolean precondicionesAgregarDetalle(Boolean modificar) {
		if (actividadId == null) {
			return false;
		}
		if (fechaDesde == null || fechaHasta == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			if (sdf.parse(sdf.format(fechaDesde)).getTime() > sdf.parse(sdf.format(fechaHasta)).getTime()) {
				statusMessages.add(Severity.ERROR, "La Fecha Desde No puede ser mayor que la Fecha Hasta.");
				return false;
			}
		} catch (ParseException e) {
			statusMessages.add(Severity.ERROR, "Error de validación en las fechas. Verifique.");
			e.printStackTrace();
			return false;
		}
		if (lugar == null) {
			return false;
		}

		return true;
	}

	/**
	 * Método que agrega un detalle
	 */
	public String saveDetalle() {

		if (precondicionesAgregarDetalle(false)) {
			try {

				cronogramaConcDet.setLugar(lugar);
				cronogramaConcDet.setFecha(fechaDesde);
				cronogramaConcDet.setFechaHasta(fechaHasta);
				cronogramaConcDet.setDatosEspecificos(em.find(DatosEspecificos.class, actividadId.longValue()));
				cronogramaConcDet.setCronogramaConcCab(new CronogramaConcCab());
				cronogramaConcDet.getCronogramaConcCab().setIdCronogramaConcCab(idCronogramaConcCab);
				cronogramaConcDet.setFechaMod(new Date());
				cronogramaConcDet.setActivo(true);
				cronogramaConcDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				cronogramaConcDet.setActividad(em.find(DatosEspecificos.class, actividadId.longValue()).getDescripcion());
				if (modoEditado == null) {
					cronogramaConcDet.setFechaAlta(new Date());
					cronogramaConcDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				}
				em.merge(cronogramaConcDet);
				// Limpieza de variables

				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return "FAIL";
			}
		}
		return "OK";
	}

	/**
	 * Método que permite cargar el detalle en el formulario de modificación
	 */
	private void initDetalleEdit() {
		if (modoEditado != null && modoEditado) {
			Query q =
				em.createQuery("select CronogramaConcDet from CronogramaConcDet CronogramaConcDet where CronogramaConcDet.idCronogramaConcDet = "
					+ idCronogramaConcDet);
			CronogramaConcDet entity = (CronogramaConcDet) q.getSingleResult();
			cronogramaConcDet = entity;
			lugar = entity.getLugar();
			fechaDesde = entity.getFecha();
			fechaHasta = entity.getFechaHasta();
			actividadId =
				new BigInteger(entity.getDatosEspecificos().getIdDatosEspecificos().toString());

		} else {
			cronogramaConcDet = new CronogramaConcDet();
		}
	}

	public void limpiarVariablesDetalle() {
		lugar = null;
		actividadId = null;
		fechaDesde = null;
		fechaHasta = null;
	}

	public void eliminarActividad(Integer elIndice) {

		if (lActividades != null && lActividades.size() > elIndice) {
			try {
				CronogramaConcDet entity =
					em.find(CronogramaConcDet.class, lActividades.get(elIndice.intValue()).getIdCronogramaConcDet());
				em.remove(entity);
				lActividades.remove(elIndice.intValue());
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método que inicializa los datos para el Editado
	 */
	public void initEdit() {

		buildActividadSelectItem();
		limpiarVariablesDetalle();
		initDetalleEdit();
		cargarCronoCabId();
	}

	/**
	 * Método que inicializa los datos para el Listado
	 */
	public void init() {
		cargarCronoCabId();
		cargarListaActividades();

	}

	public void cargarCronoCabId() {
		Query q = null;
		if (grupoPuestosController.getIdConcursoPuestoAgr() != null) {
			validarOee(grupoPuestosController.getConcursoPuestoAgr().getConcurso());
			if (!modoConcurso) {
				q =
					em.createQuery("select CronogramaConcCab from CronogramaConcCab CronogramaConcCab where CronogramaConcCab.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
			} else {
				q =
					em.createQuery("select CronogramaConcCab from CronogramaConcCab CronogramaConcCab where CronogramaConcCab.concurso.idConcurso = "
						+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getIdConcurso());
			}

			List<CronogramaConcCab> lista = q.getResultList();
			if (lista.size() == 1) {
				idCronogramaConcCab = lista.get(0).getIdCronogramaConcCab();
				cronogramaConcCab = lista.get(0);
			} else {
				idCronogramaConcCab = null;
				cronogramaConcCab = new CronogramaConcCab();
			}
		} else {
			statusMessages.add(Severity.ERROR, "Debe proveer el Id del Concurso.");
		}
	}

	/**
	 * Carga la lista de concursos no agrupados que fueron o no reservados
	 */
	@SuppressWarnings("unchecked")
	private void cargarListaActividades() {
		if (idCronogramaConcCab != null) {
			Query q =
				em.createQuery("select CronogramaConcCab from CronogramaConcCab CronogramaConcCab where CronogramaConcCab.idCronogramaConcCab = "
					+ idCronogramaConcCab);
			CronogramaConcCab entity = (CronogramaConcCab) q.getSingleResult();
			//lActividades = new ArrayList<CronogramaConcDet>(entity.getCronogramaConcDets());
			obtenerListaActividades();
		}
	}
	
	private void obtenerListaActividades(){
		Query q =
			em.createQuery("select cronogramaConcDet from CronogramaConcDet cronogramaConcDet where cronogramaConcDet.cronogramaConcCab.idCronogramaConcCab = "
				+ idCronogramaConcCab +" order by fecha, fechaHasta");
		lActividades = q.getResultList();
	}

	public String getHoraDesde() {
		if (cronogramaConcDet != null && cronogramaConcDet.getHoraDesde() != null) {
			return sdfHORA.format(cronogramaConcDet.getHoraDesde());
		}
		return null;
	}

	public void setHoraDesde(String horaDesde) {
		try {
			cronogramaConcDet.setHoraDesde(sdfHORA.parse(horaDesde));
		} catch (ParseException e) {
			cronogramaConcDet.setHoraDesde(null);
			e.printStackTrace();
		}
	}

	public String getHoraHasta() {
		if (cronogramaConcDet != null && cronogramaConcDet.getHoraHasta() != null) {
			return sdfHORA.format(cronogramaConcDet.getHoraHasta());
		}
		return null;
	}

	public void setHoraHasta(String horaHasta) {
		try {
			cronogramaConcDet.setHoraHasta(sdfHORA.parse(horaHasta));
		} catch (ParseException e) {
			cronogramaConcDet.setHoraHasta(null);
			e.printStackTrace();
		}
	}

	public List<CronogramaConcDet> getlActividades() {
		return lActividades;
	}

	public void setlActividades(List<CronogramaConcDet> lActividades) {
		this.lActividades = lActividades;
	}

	public Long getIdCronogramaConcDet() {
		return idCronogramaConcDet;
	}

	public void setIdCronogramaConcDet(Long idCronogramaConcDet) {
		this.idCronogramaConcDet = idCronogramaConcDet;
	}

	public Long getIdCronogramaConcCab() {
		return idCronogramaConcCab;
	}

	public void setIdCronogramaConcCab(Long idCronogramaConcCab) {
		this.idCronogramaConcCab = idCronogramaConcCab;
	}

	public Boolean getModoEditado() {
		return modoEditado;
	}

	public void setModoEditado(Boolean modoEditado) {
		this.modoEditado = modoEditado;
	}

	public java.util.Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(java.util.Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public java.util.Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(java.util.Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public BigInteger getActividadId() {
		return actividadId;
	}

	public void setActividadId(BigInteger actividadId) {
		this.actividadId = actividadId;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public List<SelectItem> getActividadSelecItem() {
		return actividadSelecItem;
	}

	public void setActividadSelecItem(List<SelectItem> actividadSelecItem) {
		this.actividadSelecItem = actividadSelecItem;
	}

	public CronogramaConcCab getCronogramaConcCab() {
		return cronogramaConcCab;
	}

	public void setCronogramaConcCab(CronogramaConcCab cronogramaConcCab) {
		this.cronogramaConcCab = cronogramaConcCab;
	}

	public CronogramaConcDet getCronogramaConcDet() {
		return cronogramaConcDet;
	}

	public void setCronogramaConcDet(CronogramaConcDet cronogramaConcDet) {
		this.cronogramaConcDet = cronogramaConcDet;
	}

	public String getFromConcurso() {
		return fromConcurso;
	}

	public void setFromConcurso(String fromConcurso) {
		this.fromConcurso = fromConcurso;
	}

	public Boolean getModoConcurso() {
		return modoConcurso;
	}

	public void setModoConcurso(Boolean modoConcurso) {
		this.modoConcurso = modoConcurso;
	}

	public String getFormLevel1() {
		return formLevel1;
	}

	public void setFormLevel1(String formLevel1) {
		this.formLevel1 = formLevel1;
	}

}
