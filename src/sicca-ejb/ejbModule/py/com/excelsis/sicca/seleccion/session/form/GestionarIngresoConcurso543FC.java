package py.com.excelsis.sicca.seleccion.session.form;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;
import org.richfaces.model.selection.SimpleSelection;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarIngresoConcurso543FC")
public class GestionarIngresoConcurso543FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true, required = false)
	@Out(scope = ScopeType.CONVERSATION, required = false)
	GestionarIngresosConcursos gestionarIngresosConcursos;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ValidadorController validadorController;
	private SeguridadUtilFormController seguridadUtilFormController;

	public void init() {
		if (gestionarIngresosConcursos == null) {
			gestionarIngresosConcursos =
				(GestionarIngresosConcursos) Component.getInstance(GestionarIngresosConcursos.class, true);
		}
		gestionarIngresosConcursos.validarInit();
		gestionarIngresosConcursos.estadoOcupado = gestionarIngresosConcursos.traerEstadoCab();
		gestionarIngresosConcursos.initIDS();
		gestionarIngresosConcursos.updateNombramiento();
		if (gestionarIngresosConcursos.estadoOcupado != null) {
			gestionarIngresosConcursos.cargarPostulantes();
			gestionarIngresosConcursos.cargarPuestos(false, " and excepcion is null ");
			gestionarIngresosConcursos.direFisicaAdDocu =
				gestionarIngresosConcursos.generarDireFisicaAdDocu();
			gestionarIngresosConcursos.direFisicaAdContrato =
				gestionarIngresosConcursos.generarDireFisicaAdContrato();
			gestionarIngresosConcursos.mostrarContratado =
				gestionarIngresosConcursos.calcContratado();
		}
		// limpiar2();
		gestionarIngresosConcursos.cargarIngreso();
	}

	private Boolean precondSave() {

		DatosEspecificos tipoMovilidad =
			tipificarMovilidad(gestionarIngresosConcursos.getSelePostulante().getPostulacion().getPersona().getIdPersona());
		if (tipoMovilidad == null)
			return false;
		else {
			gestionarIngresosConcursos.tipoIngreso = tipoMovilidad;
		}

		// Testar....
		ConfiguracionUoCab oee =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Persona persona =
			gestionarIngresosConcursos.selePostulante.getPostulacion().getPersonaPostulante().getPersona();
		PlantaCargoDet puesto = gestionarIngresosConcursos.selePuesto.getPlantaCargoDet();
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

	public DatosEspecificos tipificarMovilidad(Long idPersona) {
		Query q =
			em.createQuery("select EmpleadoPuesto from  EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.actual is true and EmpleadoPuesto.activo is true and EmpleadoPuesto.persona.idPersona = :idPersona");
		q.setParameter("idPersona", idPersona);
		List<EmpleadoPuesto> lista = q.getResultList();
		EmpleadoPuesto empleadoPuesto = empleadoPuesto = lista.get(0);
		// Obtener el MONTO_ANTERIOR
		q =
			em.createQuery("select EmpleadoConceptoPago from EmpleadoConceptoPago EmpleadoConceptoPago "
				+ " where EmpleadoConceptoPago.empleadoPuesto.idEmpleadoPuesto = :idEP and EmpleadoConceptoPago.objCodigo = 111 ");
		q.setParameter("idEP", empleadoPuesto.getIdEmpleadoPuesto());
		List<EmpleadoConceptoPago> lEmpleadoConceptoPago = q.getResultList();

		if (lEmpleadoConceptoPago.size() > 0) {
			Integer montoAnterior = lEmpleadoConceptoPago.get(0).getMonto();
			String tipoMovilidad = null;
			// Obtener el MONTO_NUEVO
			q =
				em.createQuery("select PuestoConceptoPago from PuestoConceptoPago PuestoConceptoPago "
					+ " where PuestoConceptoPago.plantaCargoDet.idPlantaCargoDet = :idPCD "
					+ " and PuestoConceptoPago.objCodigo = 111 "
					+ " and PuestoConceptoPago.activo is true ");
			q.setParameter("idPCD", gestionarIngresosConcursos.selePuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			List<PuestoConceptoPago> lPuestoConceptoPago = q.getResultList();
			if (lPuestoConceptoPago.size() > 0) {
				Integer montoNuevo = lPuestoConceptoPago.get(0).getMonto();
				if (montoNuevo.intValue() > montoAnterior.intValue()) {
					tipoMovilidad = "PROMOCION EN LA CARRERA";
				} else {
					tipoMovilidad = "MOVILIDAD POR CONCURSO INTERNO INSTITUCIONAL";
				}
				if (tipoMovilidad != null) {
					q =
						em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
							+ " where DatosEspecificos.descripcion = :desc "
							+ " and DatosEspecificos.activo is true");
					q.setParameter("desc", tipoMovilidad);
					return (DatosEspecificos) q.getSingleResult();
				} else
					return null;
			} else {
				statusMessages.add(Severity.ERROR, "Error al calcular el Monto Nuevo");
				return null;
			}
		} else {
			statusMessages.add(Severity.ERROR, "Error al calcular el Monto Anterior");
			return null;
		}
	}

	private EmpleadoPuesto actualizarPuestoAnterior() {
		Query q =
			em.createQuery("select EmpleadoPuesto from  EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.actual is true and EmpleadoPuesto.activo is true and EmpleadoPuesto.persona.idPersona = :idPersona");
		q.setParameter("idPersona", gestionarIngresosConcursos.selePostulante.getPostulacion().getPersona().getIdPersona());
		List<EmpleadoPuesto> lista = q.getResultList();
		EmpleadoPuesto empleadoPuesto = empleadoPuesto = lista.get(0);
		empleadoPuesto.setActivo(false);
		empleadoPuesto.setFechaFin(new Date());
		empleadoPuesto.setFechaMod(new Date());
		empleadoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
		empleadoPuesto = em.merge(empleadoPuesto);
		empleadoPuesto.getPlantaCargoDet().setEstadoCab(new EstadoCab());
		empleadoPuesto.getPlantaCargoDet().getEstadoCab().setIdEstadoCab(gestionarIngresosConcursos.idEstadoCabVacante);
		empleadoPuesto.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
		empleadoPuesto.getPlantaCargoDet().setFechaMod(new Date());
		empleadoPuesto.setPlantaCargoDet(em.merge(empleadoPuesto.getPlantaCargoDet()));
		gestionarIngresosConcursos.insertarHistorico(empleadoPuesto.getPlantaCargoDet());
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
			EmpleadoPuesto empleadoPuestoNuevo = gestionarIngresosConcursos.agregarIngreso();
			registrarDatosMovilidad(empleadoPuestoAnterior.getIdEmpleadoPuesto(), empleadoPuestoNuevo.getIdEmpleadoPuesto());
			if (empleadoPuestoNuevo != null) {
				gestionarIngresosConcursos.inactivarYcopiarDets();
				gestionarIngresosConcursos.updateUnicoEmpleadoPuesto();
				gestionarIngresosConcursos.adjuntarDocumento2(empleadoPuestoNuevo.getIdEmpleadoPuesto());
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				gestionarIngresosConcursos.limpiar2();
				return "OK";
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}
}
