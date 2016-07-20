package py.com.excelsis.sicca.seleccion.session.form;

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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Postulacion;

import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("gestConcursoInternoExc601FC")
public class GestConcursoInternoExc601FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true, required = false)
	GestionarIngresosConcursos gestionarIngresos;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ValidadorController validadorController;
	@In(create = true, required = false)
	GestionarIngresoConcurso543FC gestionarIngresoConcurso543FC;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private Long idExcepcion;

	public void init() {
		if (gestionarIngresos == null) {
			gestionarIngresos =
				(GestionarIngresosConcursos) Component.getInstance(GestionarIngresosConcursos.class, true);
		}
		gestionarIngresos.validarInit();
		gestionarIngresos.setEstadoOcupado(gestionarIngresos.traerEstadoCab());
		gestionarIngresos.initIDS();
		gestionarIngresos.updateNombramiento();
		if (gestionarIngresos.getEstadoOcupado() != null) {
			StringBuffer sql = new StringBuffer();
			sql.append(" AND evalrefere0_.activo=true ");
			sql.append(" AND evalrefere0_.seleccionado=true ");
			sql.append(" AND evalrefere0_.lista_corta=true ");
			sql.append(" AND evalrefere0_.incluido=true ");
			sql.append(" AND evalrefere0_.id_excepcion= " + idExcepcion);
			sql.append(" AND (evalrefere0_.excluido=false or evalrefere0_.excluido is null) ");
			gestionarIngresos.cargarPostulantes(sql.toString());
			gestionarIngresos.cargarPuestos(false, " and excepcion = " + idExcepcion);
			gestionarIngresos.direFisicaAdDocu = gestionarIngresos.generarDireFisicaAdDocu();
			gestionarIngresos.direFisicaAdContrato =
				gestionarIngresos.generarDireFisicaAdContrato();
			gestionarIngresos.mostrarContratado = gestionarIngresos.calcContratado();
		}
		gestionarIngresos.cargarIngreso();
	}

	private void insertarHistorial() {
		HistorialExcepcion historialExcepcion = new HistorialExcepcion();
		historialExcepcion.setConcursoGrupoAgr(new ConcursoPuestoAgr());
		historialExcepcion.getConcursoGrupoAgr().setIdConcursoPuestoAgr(grupoPuestosController.getIdConcursoPuestoAgr());
		historialExcepcion.setExcepcion(new Excepcion());
		historialExcepcion.getExcepcion().setIdExcepcion(idExcepcion);
		historialExcepcion.setEstado("INGRESADO");
		historialExcepcion.setFechaAlta(new Date());
		historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		if (gestionarIngresos.observacion != null
			&& !gestionarIngresos.observacion.trim().isEmpty())
			historialExcepcion.setObservacion(gestionarIngresos.observacion);
		em.persist(historialExcepcion);

	}

	private void updateExcepcion(Long idExcepcion) {

	}

	public String nextTask() {
		// Controles particulares
		if (!gestionarIngresos.precondSgteTarea()) {
			return "FAIL";
		}
		try {
			// Inserta el historial
			insertarHistorial();
			// Actualizar estado de la excepcion...
			Excepcion excepcion = em.find(Excepcion.class, idExcepcion);
			excepcion.setFechaMod(new Date());
			excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			excepcion.setEstado("INGRESADO");
			em.merge(excepcion);

			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
			Concurso concurso = grupoPuestosController.getConcursoPuestoAgr().getConcurso();
			if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CII")
				|| concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CIR")
				|| concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CIPS")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.CIO_INGRESAR_POSTULANTE);
				jbpmUtilFormController.setActividadSiguiente(null);
			} else if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CPO")
				|| concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CME")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.INGRESAR_POSTULANTE);
				jbpmUtilFormController.setActividadSiguiente(null);
			} else if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CSI")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_INGRESAR_POSTULANTE);
				jbpmUtilFormController.setActividadSiguiente(null);
			}
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			} else {
				return "FAIL";
			}
			return "nextTask";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}

	private Boolean precondSave() {
		if (gestionarIngresos.getSelePostulante() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Postulante");
			return false;
		}
		Postulacion postulacion =
			em.find(Postulacion.class, gestionarIngresos.getSelePostulante().getPostulacion().getIdPostulacion());
		gestionarIngresos.getSelePostulante().setPostulacion(postulacion);
		if (gestionarIngresoConcurso543FC == null) {
			gestionarIngresoConcurso543FC =
				(GestionarIngresoConcurso543FC) Component.getInstance(GestionarIngresoConcurso543FC.class, true);
		}

		DatosEspecificos tipoMovilidad =
			gestionarIngresoConcurso543FC.tipificarMovilidad(gestionarIngresos.getSelePostulante().getPostulacion().getPersona().getIdPersona());
		if (tipoMovilidad == null)
			return false;
		else {
			gestionarIngresos.tipoIngreso = tipoMovilidad;
		}

		// Testar....
		ConfiguracionUoCab oee =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Persona persona =
			gestionarIngresos.selePostulante.getPostulacion().getPersonaPostulante().getPersona();
		PlantaCargoDet puesto = gestionarIngresos.selePuesto.getPlantaCargoDet();
		String grupoValidacion = "ICINSTI";
		ValidadorDTO validadorDTO =
			validadorController.validarCfg("PERSONA", grupoValidacion, persona, puesto, null, oee);

		if (validadorDTO.isValido()) {
			validadorDTO =
				validadorController.validarCfg("PUESTO", grupoValidacion, persona, puesto, null, oee);
			if (validadorDTO.isValido())
				return true;
			else {
				statusMessages.add(Severity.ERROR, validadorDTO.getMensaje());
				return false;
			}
		} else {
			statusMessages.add(Severity.ERROR, validadorDTO.getMensaje());
			return false;
		}

	}

	private EmpleadoPuesto actualizarPuestoAnterior() {
		Query q =
			em.createQuery("select EmpleadoPuesto from  EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.actual is true and EmpleadoPuesto.activo is true and EmpleadoPuesto.persona.idPersona = :idPersona");
		q.setParameter("idPersona", gestionarIngresos.selePostulante.getPostulacion().getPersona().getIdPersona());
		List<EmpleadoPuesto> lista = q.getResultList();
		EmpleadoPuesto empleadoPuesto = empleadoPuesto = lista.get(0);
		empleadoPuesto.setActivo(false);
		empleadoPuesto.setFechaFin(new Date());
		empleadoPuesto.setFechaMod(new Date());
		empleadoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
		empleadoPuesto = em.merge(empleadoPuesto);
		empleadoPuesto.getPlantaCargoDet().setEstadoCab(new EstadoCab());
		empleadoPuesto.getPlantaCargoDet().getEstadoCab().setIdEstadoCab(gestionarIngresos.idEstadoCabVacante);
		empleadoPuesto.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
		empleadoPuesto.getPlantaCargoDet().setFechaMod(new Date());
		empleadoPuesto.setPlantaCargoDet(em.merge(empleadoPuesto.getPlantaCargoDet()));
		gestionarIngresos.insertarHistorico(empleadoPuesto.getPlantaCargoDet());
		return empleadoPuesto;

	}

	private void registrarDatosMovilidad(Long idEPA, Long idEPN) {
		EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
		empleadoMovilidadCab.setEmpleadoPuestoAnterior(new EmpleadoPuesto());
		empleadoMovilidadCab.getEmpleadoPuestoAnterior().setIdEmpleadoPuesto(idEPA);
		empleadoMovilidadCab.setEmpleadoPuestoNuevo(new EmpleadoPuesto());
		empleadoMovilidadCab.getEmpleadoPuestoNuevo().setIdEmpleadoPuesto(idEPN);
		empleadoMovilidadCab.setMovilidadSicca(true);
		empleadoMovilidadCab.setActivo(true);
		empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		empleadoMovilidadCab.setFechaAlta(new Date());
		em.persist(empleadoMovilidadCab);

	}

	public String save() {
		if (!precondSave())
			return null;
		try {
			EmpleadoPuesto empleadoPuestoAnterior = actualizarPuestoAnterior();
			EmpleadoPuesto empleadoPuestoNuevo = gestionarIngresos.agregarIngreso();
			registrarDatosMovilidad(empleadoPuestoAnterior.getIdEmpleadoPuesto(), empleadoPuestoNuevo.getIdEmpleadoPuesto());
			if (empleadoPuestoNuevo != null) {
				gestionarIngresos.nombrePantalla = "INGRESO_INSTITUCIONAL_EXC";
				gestionarIngresos.inactivarYcopiarDets();
				gestionarIngresos.updateUnicoEmpleadoPuesto();
				gestionarIngresos.adjuntarDocumento2(empleadoPuestoNuevo.getIdEmpleadoPuesto());
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				gestionarIngresos.limpiar2();
				return "OK";
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

}
