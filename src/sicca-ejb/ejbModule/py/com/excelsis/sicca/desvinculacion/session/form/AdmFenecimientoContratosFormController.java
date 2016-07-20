package py.com.excelsis.sicca.desvinculacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("admFenecimientoContratosFormController")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class AdmFenecimientoContratosFormController {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	

	private EstadoCab estadoCabVacante = new EstadoCab();
	private EstadoCab estadoCabPendiente = new EstadoCab();
	private DatosEspecificos tipoDesvinculacion = new DatosEspecificos();

	public void init() {
		estadoCabVacante = new EstadoCab();
		estadoCabVacante = buscarEstadoCab("VACANTE");
		estadoCabPendiente = new EstadoCab();
		estadoCabPendiente =buscarEstadoCab("PENDIENTE DE RENOVACION");
		buscarTipoDesvinculacion();
	}
	
	private EstadoCab buscarEstadoCab(String estadoCab) {
		try {
			String cad = "select cab.* from planificacion.estado_cab cab "
					+ "where lower(cab.descripcion) = '" + estadoCab.toLowerCase() + "' ";
			
			List<EstadoCab> lista = new ArrayList<EstadoCab>();
			lista = em.createNativeQuery(cad, EstadoCab.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return null;
	}

	private List<EmpleadoPuesto> lEmpleadoPuesto() {
		Query q = em
				.createQuery("select empleadoPuesto from EmpleadoPuesto empleadoPuesto "
						+ " where empleadoPuesto.actual is true "
						+ " and empleadoPuesto.contratado is true "
						+ " and empleadoPuesto.fechaFinContrato = :fecha");

		Date fecha = new Date();
		q.setParameter("fecha", fecha);

		List<EmpleadoPuesto> lista = q.getResultList();
		return lista;
	}

	// Paso 1: actualiza los datos en la tabla empleado_puesto
	private void actualizarDatosEmpleadoPuesto() {
		List<EmpleadoPuesto> listaEmpleado = new ArrayList<EmpleadoPuesto>();
		listaEmpleado = lEmpleadoPuesto();
		try {
			for (EmpleadoPuesto emp : listaEmpleado) {
				emp.setActual(false);
				emp.setUsuMod("SYSTEM");
				emp.setFechaMod(new Date());
				emp.setFechaFin(new Date());
				em.merge(emp);
				em.flush();
				actualizaDatosPuesto(emp.getPlantaCargoDet());
				registraDesvinculacion(emp);
				aumentarCantidadDisponibleAnx(emp);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Paso 2: en caso de que ESTADO_CAB <> PENDIENTE DE RENOVACION' actualiza a
	// 'VACANTE, y registra los históricos
	private void actualizaDatosPuesto(PlantaCargoDet puesto) {
		if (!puesto.getEstadoCab().getIdEstadoCab()
				.equals(estadoCabPendiente.getIdEstadoCab())) {
			puesto.setEstadoCab(estadoCabVacante);
			puesto.setFechaMod(new Date());
			puesto.setUsuMod("SYSTEM");
			try {
				em.merge(puesto);
				HistoricosEstado historico = new HistoricosEstado();
				historico.setEstadoCab(estadoCabVacante);
				historico.setPlantaCargoDet(puesto);
				historico.setFechaMod(new Date());
				historico.setUsuMod("SYSTEM");
				em.persist(historico);
				em.flush();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	// Paso 3: registra datos en la tabla desvinculacion
	private void registraDesvinculacion(EmpleadoPuesto empleado) {
		try {
			Desvinculacion desvinculacion = new Desvinculacion();
			desvinculacion.setEmpleadoPuesto(empleado);
			desvinculacion.setDatosEspecifMotivo(tipoDesvinculacion);
			desvinculacion.setFechaDesvinculacion(new Date());
			desvinculacion
					.setObservacion("REGISTRO DE FENECIMIENTO DE CONTRATO AUTOMATICO");
			desvinculacion.setUsuAlta("SYSTEM");
			desvinculacion.setFechaAlta(new Date());
			em.persist(desvinculacion);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Paso 4: Incrementar una unidad a la columna cant_disponible que cumpla
	// con las condiciones
	@SuppressWarnings("unchecked")
	private void aumentarCantidadDisponibleAnx(EmpleadoPuesto empleado) {
		Date fechaActual = new Date();
		Integer anhoActual = fechaActual.getYear() + 1900;
		String sql = "select anx.* "
				+ "from sinarh.sin_anx anx, general.empleado_concepto_pago pago "
				+ "join general.empleado_puesto empleado "
				+ "on empleado.id_empleado_puesto = pago.id_empleado_puesto "
				+ "join planificacion.planta_cargo_det p "
				+ "on empleado.id_planta_cargo_det = p.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det det "
				+ "on det.id_configuracion_uo_det = p.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab uo "
				+ "on uo.id_configuracion_uo = det.id_configuracion_uo "
				+ "where anx.ctg_codigo = pago.categoria "
				+ "and anx.obj_codigo = pago.obj_codigo "
				+ "and ani_aniopre = " + anhoActual
				+ " and empleado.id_empleado_puesto = "
				+ empleado.getIdEmpleadoPuesto()
				+ " and anx.nen_codigo ||'.'|| " + "anx.ent_codigo ||'.'|| "
				+ "anx.tip_codigo ||'.'|| " + "anx.pro_codigo = uo.codigo_sinarh";
		List<SinAnx> listaAnx = new ArrayList<SinAnx>();
		try {
			listaAnx = em.createNativeQuery(sql, SinAnx.class).getResultList();
			for (SinAnx anx : listaAnx) {
				Integer cant = anx.getCantDisponible();
				cant = cant + 1;
				anx.setCantDisponible(cant);
				em.merge(anx);
				em.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unchecked")
	private void buscarTipoDesvinculacion() {
		String sql = "SELECT DE.* " + "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'MOTIVOS DE DESVINCULACION' "
				+ "AND DE.ACTIVO = TRUE  "
				+ "AND DE.DESCRIPCION = 'FENECIMIENTO DE CONTRATO'";
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();
		if (lista != null && lista.size() > 0)
			tipoDesvinculacion = lista.get(0);
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipalFenecimientoContrato(
			@Expiration Date when, @IntervalCron String interval) {
		
		init();
		actualizarDatosEmpleadoPuesto();
		return null;
	}

	public EstadoCab getEstadoCabVacante() {
		return estadoCabVacante;
	}

	public void setEstadoCabVacante(EstadoCab estadoCabVacante) {
		this.estadoCabVacante = estadoCabVacante;
	}

	public EstadoCab getEstadoCabPendiente() {
		return estadoCabPendiente;
	}

	public void setEstadoCabPendiente(EstadoCab estadoCabPendiente) {
		this.estadoCabPendiente = estadoCabPendiente;
	}

	public DatosEspecificos getTipoDesvinculacion() {
		return tipoDesvinculacion;
	}

	public void setTipoDesvinculacion(DatosEspecificos tipoDesvinculacion) {
		this.tipoDesvinculacion = tipoDesvinculacion;
	}

}
