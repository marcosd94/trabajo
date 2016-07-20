package py.com.excelsis.sicca.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.CabValidacionOee;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.DetValidacionOee;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.HistoricoCapacitacionDoc;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Scope(ScopeType.PAGE)
@Name("validadorController")
public class ValidadorController implements Serializable {
	@In(required = false)
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private DetValidacionOee detValidacionOee;
	private List<DetValidacionOee> detValidacionOeeList;

	public void init() {

	}

	public ValidadorDTO validarCfg(String tipo, String grupoValidacion,
			Persona persona, PlantaCargoDet puesto, String idRegla,
			ConfiguracionUoCab oee) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		/* En caso de que la validacion se realice sobre persona o puesto */
		if (tipo.equalsIgnoreCase("PERSONA") || tipo.equalsIgnoreCase("PUESTO")) {
			String query = "select det from DetValidacionOee det "
					+ " where det.cabValidacionOee.activo is true "
					+ "and det.cabValidacionOee.nombreGrupoValidacion = '"
					+ grupoValidacion
					+ "' "
					+ "and det.cabValidacionOee.configuracionUoCab.idConfiguracionUo = "
					+ oee.getIdConfiguracionUo();
			Query q = em.createQuery(query);

			detValidacionOee = new DetValidacionOee();
			detValidacionOeeList = new ArrayList<DetValidacionOee>();
			detValidacionOeeList = q.getResultList();
			if (detValidacionOeeList.isEmpty()) {
				validadorDTO = new ValidadorDTO();
				validadorDTO.setValido(true);
				return validadorDTO;
			}

			/*
			 * En caso de que la validacion se realice sobre cualquiera que no
			 * sea persona o puesto
			 */
		} else {
			try {
				Query q = em
						.createQuery("select det from DetValidacionOee det "
								+ " where det.cabValidacionOee.activo is true "
								+ "and det.activo is true "
								+ "and det.cabValidacionOee.nombreGrupoValidacion = '"
								+ grupoValidacion
								+ "' "
								+ "and det.cabValidacionOee.configuracionUoCab.idConfiguracionUo = :idOee");
				q.setParameter("idOee", oee.getIdConfiguracionUo());
				detValidacionOee = new DetValidacionOee();
				detValidacionOeeList = new ArrayList<DetValidacionOee>();
				detValidacionOeeList = q.getResultList();

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (detValidacionOeeList.isEmpty()) {
				validadorDTO = new ValidadorDTO();
				validadorDTO.setValido(true);
				return validadorDTO;
			}
		}
		for (int i = 0; i < detValidacionOeeList.size(); i++) {
			detValidacionOee = new DetValidacionOee();
			detValidacionOee = detValidacionOeeList.get(i);

			/* Caso 1 */
			if (detValidacionOee.getValorIdentif().equalsIgnoreCase("INHAB"))
				return personaInhabilitada(persona.getIdPersona());

			/* Caso 2 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"E_MINIMA"))
				return tieneEdadMinima(persona);

			/* Caso 3 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"PERS_PUESTO"))
				return ocupaMasPuestos(persona.getIdPersona());

			/* Caso 4 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"RET_VOL"))
				return cuentaConRetiroVoluntario(persona.getIdPersona());

			/* Caso 5 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"NAC_PAR"))
				return esParaguayo(persona);

			/* Caso 6 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"JUBILADO"))
				return tieneJubilacion(persona.getIdPersona());

			/* Caso 7 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"ANT_PUESTO"))
				return cuentaConAntiguedad(persona.getIdPersona());

			/* Caso 8 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"PER_INACT"))
				return personaInactiva(persona.getIdPersona());

			/* Caso 9 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"DESV_FCONT"))
				return fenecimientoContrato(persona.getIdPersona());

			/* Caso 10 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"FUN_PERM"))
				return funcionarioPermanente(persona.getIdPersona());

			/* Caso 11 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"PERSONA_OEE"))
				return permanenteOcontratadoOee(persona.getIdPersona(),
						oee.getIdConfiguracionUo());
			/* Caso 12 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_VACANTE"))
				return puestoVacante(puesto.getIdPlantaCargoDet());

			/* Caso 13 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_CONCURSO"))
				return listoParaIngreso(puesto.getIdPlantaCargoDet());

			/* Caso 14 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_ELECT"))
				return electivo(puesto.getIdPlantaCargoDet());

			/* Caso 15 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_CONF"))
				return confianza(puesto.getIdPlantaCargoDet());

			/* Caso 16 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_CONT"))
				return contratado(puesto.getIdPlantaCargoDet());

			/* Caso 17 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_JUDIC"))
				return judicializado(puesto.getIdPlantaCargoDet());

			/* Caso 18 */
			else if (detValidacionOee.getValorIdentif().equalsIgnoreCase(
					"P_RENOV"))
				return pendiente(puesto.getIdPlantaCargoDet());
		}
		return null;
	}

	/**
	 * Caso 1 - Persona no debe estar inhabilitado
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO personaInhabilitada(Long idPersona) {
		Query q = em.createQuery("select inh from Inhabilitados inh "
				+ " where inh.inhabilitado is true "
				+ "and inh.empleadoPuesto.persona.idPersona = :id");
		q.setParameter("id", idPersona);
		List<Inhabilitados> lista = new ArrayList<Inhabilitados>();
		lista = q.getResultList();
		ValidadorDTO validadorDTO = new ValidadorDTO();
		if (lista.isEmpty()) {
			validadorDTO.setValido(true);
			return validadorDTO;
		}
		validadorDTO.setValido(false);
		if (detValidacionOee.getBloquea())
			validadorDTO.setMensaje("BL "
					+ SeamResourceBundle.getBundle().getString("CU524_caso1"));
		else
			validadorDTO.setMensaje("NB "
					+ SeamResourceBundle.getBundle().getString("CU524_caso1"));
		return validadorDTO;
	}

	/**
	 * Caso 2 - Persona debe tener Edad mínima
	 * 
	 * @param persona
	 * @return
	 */
	private ValidadorDTO tieneEdadMinima(Persona persona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Integer anhoActual = (new Date()).getYear() + 1900;
		Integer anhoNac = persona.getFechaNacimiento().getYear() + 1900;
		Integer edad = anhoActual - anhoNac;
		Query q = em.createQuery("select ref from Referencias ref "
				+ " where ref.dominio = 'EDAD_MINIMA_LEGAL' ");
		Referencias referencias = (Referencias) q.getSingleResult();
		if (referencias.getValorNum().intValue() <= edad.intValue()) {
			validadorDTO.setValido(true);
			return validadorDTO;
		}
		validadorDTO.setValido(false);
		if (detValidacionOee.getBloquea())
			validadorDTO.setMensaje("BL "
					+ SeamResourceBundle.getBundle().getString("CU524_caso2"));
		else
			validadorDTO.setMensaje("NB "
					+ SeamResourceBundle.getBundle().getString("CU524_caso2"));
		return validadorDTO;
	}

	/**
	 * Caso 3 - Persona no puede ocupar de más de un puesto
	 * 
	 * @param persona
	 * @return
	 */
	private ValidadorDTO ocupaMasPuestos(Long idPersona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query q = em.createQuery("select emp from EmpleadoPuesto emp "
				+ " where emp.activo is true " + "and emp.actual is true "
				+ "and emp.persona.idPersona = :id");
		q.setParameter("id", idPersona);
		List<EmpleadoPuesto> listaEmpleados = new ArrayList<EmpleadoPuesto>();
		listaEmpleados = q.getResultList();
		if (listaEmpleados.size() > 1) {
			String sql = "SELECT  puesto.* "
					+ "FROM planificacion.planta_cargo_det puesto "
					+ "INNER JOIN planificacion.cpt cpt ON (puesto.id_cpt = cpt.id_cpt) "
					+ "INNER JOIN planificacion.tipo_cpt tipo ON (cpt.id_tipo_cpt = tipo.id_tipo_cpt) "
					+ "INNER JOIN general.empleado_puesto emp ON (puesto.id_planta_cargo_det = emp.id_planta_cargo_det) "
					+ "INNER JOIN general.persona per ON (emp.id_persona = per.id_persona) "
					+ "WHERE (tipo.descripcion = 'CARRERA SANITARIA' OR  tipo.descripcion = 'CARRERA DOCENTE') "
					+ "AND per.id_persona = " + idPersona;
			List<PlantaCargoDet> listaPuestos = new ArrayList<PlantaCargoDet>();
			listaPuestos = em.createNativeQuery(sql, PlantaCargoDet.class)
					.getResultList();
			if (listaPuestos.size() > 0) {
				validadorDTO.setValido(false);
				if (detValidacionOee.getBloquea())
					validadorDTO.setMensaje("BL "
							+ SeamResourceBundle.getBundle().getString(
									"CU524_caso3"));
				else
					validadorDTO.setMensaje("NB "
							+ SeamResourceBundle.getBundle().getString(
									"CU524_caso3"));
				return validadorDTO;
			}
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 4 - La persona no debe contar con Retiro voluntario
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO cuentaConRetiroVoluntario(Long idPersona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT desvinculacion.* "
				+ "FROM desvinculacion.desvinculacion desvinculacion "
				+ "INNER JOIN general.empleado_puesto emp ON (desvinculacion.id_empleado_puesto = emp.id_empleado_puesto) "
				+ "INNER JOIN desvinculacion.inhabilitados inh ON (inh.id_empleado_puesto = emp.id_empleado_puesto) "
				+ "INNER JOIN seleccion.datos_especificos datos ON (desvinculacion.id_datos_especif_motivo = datos.id_datos_especificos) "
				+ "INNER JOIN seleccion.datos_generales gral ON (datos.id_datos_generales = gral.id_datos_generales) "
				+ "WHERE datos.descripcion = 'RETIRO_VOLUNTARIO' AND gral.descripcion = 'MOTIVOS DE DESVINCULACION'  "
				+ "AND inh.id_persona = " + idPersona;
		List<Desvinculacion> listaDesvinculaciones = new ArrayList<Desvinculacion>();
		listaDesvinculaciones = em.createNativeQuery(sql, Desvinculacion.class)
				.getResultList();
		if (listaDesvinculaciones.size() > 0) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso4"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso4"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 5 - La persona debe tener nacionalidad paraguaya
	 * 
	 * @param p
	 * @return
	 */
	private ValidadorDTO esParaguayo(Persona p) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query q = em.createQuery("select dato from DatosEspecificos dato "
				+ " where dato.activo is true "
				+ "and dato.datosGenerales.descripcion = 'NACIONALIDAD' "
				+ "and dato.descripcion = 'PARAGUAYA'");

		DatosEspecificos dato = new DatosEspecificos();
		dato = (DatosEspecificos) q.getSingleResult();
		if (p.getDatosEspecificos() != null) {
			Long id = p.getDatosEspecificos().getIdDatosEspecificos();
			if (id.equals(dato.getIdDatosEspecificos())) {
				validadorDTO.setValido(true);
				return validadorDTO;
			}
		}
		if (detValidacionOee.getBloquea())
			validadorDTO.setMensaje("BL "
					+ SeamResourceBundle.getBundle().getString("CU524_caso5"));
		else
			validadorDTO.setMensaje("NB "
					+ SeamResourceBundle.getBundle().getString("CU524_caso5"));
		validadorDTO.setValido(false);
		return validadorDTO;
	}

	/**
	 * Caso 6 - La persona no debe estar jubilada
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO tieneJubilacion(Long idPersona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query q = em.createQuery("select jub from Jubilados jub "
				+ " where jub.inhabilitado is true "
				+ "and jub.empleadoPuesto.persona.idPersona = :id");

		q.setParameter("id", idPersona);
		List<Jubilados> lista = new ArrayList<Jubilados>();
		lista = q.getResultList();
		if (lista.isEmpty()) {
			validadorDTO.setValido(true);
			return validadorDTO;
		}
		validadorDTO.setValido(false);
		if (detValidacionOee.getBloquea())
			validadorDTO.setMensaje("BL "
					+ SeamResourceBundle.getBundle().getString("CU524_caso6"));
		else
			validadorDTO.setMensaje("NB "
					+ SeamResourceBundle.getBundle().getString("CU524_caso6"));
		return validadorDTO;
	}

	/**
	 * Caso 7 - La persona cuenta con antiguedad minima para el puesto
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO cuentaConAntiguedad(Long idPersona) {
		Query q = em.createQuery("select ref from Referencias ref "
				+ " where ref.dominio = 'ANTIGUEDAD_MINIMA' ");
		Referencias referencias = (Referencias) q.getSingleResult();
		ValidadorDTO validadorDTO = new ValidadorDTO();

		Query query = em.createQuery("select emp from EmpleadoPuesto emp "
				+ " where emp.activo is true " + "and emp.actual is true "
				+ "and emp.persona.idPersona = :id");

		query.setParameter("id", idPersona);
		List<EmpleadoPuesto> lista = new ArrayList<EmpleadoPuesto>();
		lista = q.getResultList();
		Integer anhoActual = (new Date()).getYear() + 1900;

		for (EmpleadoPuesto emp : lista) {
			Integer anhoInicio = emp.getFechaInicio().getYear() + 1900;
			Integer antiguedad = anhoActual - anhoInicio;
			if (antiguedad.intValue() >= referencias.getValorNum().intValue()) {
				validadorDTO.setValido(true);
				return validadorDTO;
			}
		}
		validadorDTO.setValido(false);
		if (detValidacionOee.getBloquea())
			validadorDTO.setMensaje("BL "
					+ SeamResourceBundle.getBundle().getString("CU524_caso7"));
		else
			validadorDTO.setMensaje("NB "
					+ SeamResourceBundle.getBundle().getString("CU524_caso7"));
		return validadorDTO;
	}

	/**
	 * Caso 8 - La persona tiene que estar en estado activo
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO personaInactiva(Long idPersona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();

		Query query = em.createQuery("select persona from Persona persona "
				+ " where persona.activo is true "
				+ "and persona.idPersona = :id");

		query.setParameter("id", idPersona);
		Persona persona = (Persona) query.getSingleResult();
		if (persona == null || persona.getIdPersona() == null) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso8"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso8"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 9 - La persona debe tener desvinculación por fenecimiento de
	 * contrato
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO fenecimientoContrato(Long idPersona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT emp.* "
				+ "FROM general.empleado_puesto emp "
				+ "INNER JOIN desvinculacion.desvinculacion desvinculacion "
				+ "ON (emp.id_empleado_puesto = desvinculacion.id_empleado_puesto) "
				+ "INNER JOIN general.persona persona ON (emp.id_persona = persona.id_persona) "
				+ "INNER JOIN seleccion.datos_especificos datos "
				+ "ON (desvinculacion.id_datos_especif_motivo = datos.id_datos_especificos) "
				+ "AND (persona.id_datos_especificos_nacionalid = datos.id_datos_especificos) "
				+ "INNER JOIN seleccion.datos_generales gral ON (datos.id_datos_generales = gral.id_datos_generales) "
				+ "WHERE emp.fecha_fin IS NOT NULL AND  "
				+ "datos.descripcion = 'FENECIMIENTO DE CONTRATO' AND  "
				+ "gral.descripcion = 'MOTIVOS DE DESVINCULACION'  "
				+ "AND general.persona.id_persona = " + idPersona;
		List<EmpleadoPuesto> listaEmpleados = new ArrayList<EmpleadoPuesto>();
		listaEmpleados = em.createNativeQuery(sql, EmpleadoPuesto.class)
				.getResultList();
		if (listaEmpleados.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso9"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso9"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 10 - La persona debe ser funcionario permanente
	 * 
	 * @param idPersona
	 * @return
	 */
	private ValidadorDTO funcionarioPermanente(Long idPersona) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT persona.* "
				+ "FROM general.persona persona "
				+ "INNER JOIN general.empleado_puesto emp ON (persona.id_persona = emp.id_persona) "
				+ "WHERE emp.activo is true AND emp.actual is true AND "
				+ "emp.contratado is false AND persona.id_persona = "
				+ idPersona;
		List<Persona> listaPersonas = new ArrayList<Persona>();
		listaPersonas = em.createNativeQuery(sql, Persona.class)
				.getResultList();
		if (listaPersonas.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso10"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso10"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 11 - La persona debe ser funcionario permanente o contratado de la
	 * OEE
	 * 
	 * @param idPersona
	 * @param idOee
	 * @return
	 */
	private ValidadorDTO permanenteOcontratadoOee(Long idPersona, Long idOee) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT persona.* "
				+ "FROM general.persona persona "
				+ "INNER JOIN general.empleado_puesto emp ON (persona.id_persona = emp.id_persona) "
				+ "INNER JOIN planificacion.planta_cargo_det puesto ON (emp.id_planta_cargo_det = puesto.id_planta_cargo_det) "
				+ "INNER JOIN planificacion.configuracion_uo_det uo_det ON (puesto.id_configuracion_uo_det = uo_det.id_configuracion_uo_det) "
				+ "INNER JOIN planificacion.configuracion_uo_cab oee ON (uo_det.id_configuracion_uo = oee.id_configuracion_uo) "
				+ "WHERE emp.activo is true AND emp.actual is true "
				+ "AND persona.id_persona = " + idPersona
				+ " AND oee.id_configuracion_uo = " + idOee;
		List<Persona> listaPersonas = new ArrayList<Persona>();
		listaPersonas = em.createNativeQuery(sql, Persona.class)
				.getResultList();
		if (listaPersonas.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso11"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso11"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 12 - El puesto debe estar vacante
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO puestoVacante(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query query = em
				.createQuery("select puesto from PlantaCargoDet puesto "
						+ " where puesto.estadoCab.descripcion = 'VACANTE' "
						+ "and puesto.estadoDet is null "
						+ "and puesto.idPlantaCargoDet = :id");

		query.setParameter("id", idPuesto);
		List<PlantaCargoDet> listaVac = query.getResultList();
		if (listaVac.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso12"));
			else
				validadorDTO.setMensaje("NL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso12"));

			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 13 - El puesto debe estar con estado listo para ingreso
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO listoParaIngreso(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT puesto.* "
				+ "FROM planificacion.planta_cargo_det puesto "
				+ "INNER JOIN planificacion.estado_cab cab ON (puesto.id_estado_cab = cab.id_estado_cab) "
				+ "WHERE cab.descripcion = 'CONCURSO' "
				+ "AND puesto.descripcion IN('FIRMADO DECRETO PRESIDENCIAL' , 'CONTRATADO', 'FIRMADO NOMBRAMIENTO') "
				+ "AND puesto.id_planta_cargo_det = " + idPuesto;

		List<PlantaCargoDet> listaPuestos = new ArrayList<PlantaCargoDet>();
		listaPuestos = em.createNativeQuery(sql, PlantaCargoDet.class)
				.getResultList();
		if (listaPuestos.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso13"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso13"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 14 - El puesto debe estar tipificado como electivo
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO electivo(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT puesto.* "
				+ "FROM planificacion.planta_cargo_det puesto "
				+ "INNER JOIN planificacion.det_tipo_nombramiento det_nombramiento ON (puesto.id_planta_cargo_det = det_nombramiento.id_planta_cargo_det) "
				+ "INNER JOIN planificacion.tipo_nombramiento nombramiento ON (det_nombramiento.id_tipo_nombramiento = nombramiento.id_tipo_nombramiento) "
				+ "WHERE nombramiento.descripcion = 'ELECTIVA' AND puesto.id_planta_cargo_det = "
				+ idPuesto;

		List<PlantaCargoDet> listaPuestos = new ArrayList<PlantaCargoDet>();
		listaPuestos = em.createNativeQuery(sql, PlantaCargoDet.class)
				.getResultList();
		if (listaPuestos.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso14"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso14"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 15 - El puesto debe estar tipificado como de confianza
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO confianza(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		String sql = "SELECT puesto.* "
				+ "FROM planificacion.planta_cargo_det puesto "
				+ "INNER JOIN planificacion.det_tipo_nombramiento det_nomb ON (puesto.id_planta_cargo_det = det_nomb.id_planta_cargo_det) "
				+ "INNER JOIN planificacion.tipo_nombramiento nomb ON (det_nomb.id_tipo_nombramiento = nomb.id_tipo_nombramiento) "
				+ "WHERE nomb.descripcion = 'NOMBRAMIENTO CONFIANZA' OR "
				+ "nomb.descripcion = 'NOMBRAMIENTO CONF. CARRERA' "
				+ "AND puesto.id_planta_cargo_det = " + idPuesto;
		List<PlantaCargoDet> listaPuestos = new ArrayList<PlantaCargoDet>();
		listaPuestos = em.createNativeQuery(sql, PlantaCargoDet.class)
				.getResultList();
		if (listaPuestos.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso15"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso15"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 16 - El puesto debe ser para contratado
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO contratado(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query query = em
				.createQuery("select puesto from PlantaCargoDet puesto "
						+ " where puesto.contratado is true "
						+ "and puesto.idPlantaCargoDet = :id");

		query.setParameter("id", idPuesto);
		List<PlantaCargoDet> listaPuesto = query.getResultList();
		if (listaPuesto.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso16"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso16"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 17 - El puesto no debe estar judicializado
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO judicializado(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query query = em
				.createQuery("select puesto from PlantaCargoDet puesto "
						+ " where puesto.estadoCab.descripcion = 'JUDICIALIZADO' "
						+ "and puesto.idPlantaCargoDet = :id");

		query.setParameter("id", idPuesto);
		List<PlantaCargoDet> listaPuesto = query.getResultList();
		if (!listaPuesto.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso17"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso17"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	/**
	 * Caso 18 - El puesto debe estar pendiente de renovación
	 * 
	 * @param idPuesto
	 * @return
	 */
	private ValidadorDTO pendiente(Long idPuesto) {
		ValidadorDTO validadorDTO = new ValidadorDTO();
		Query query = em
				.createQuery("select puesto from PlantaCargoDet puesto "
						+ " where puesto.estadoCab.descripcion = 'PENDIENTE DE RENOVACION'  "
						+ "and puesto.idPlantaCargoDet = :id");

		query.setParameter("id", idPuesto);
		List<PlantaCargoDet> listaPuesto = query.getResultList();
		if (listaPuesto.isEmpty()) {
			validadorDTO.setValido(false);
			if (detValidacionOee.getBloquea())
				validadorDTO.setMensaje("BL "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso18"));
			else
				validadorDTO.setMensaje("NB "
						+ SeamResourceBundle.getBundle().getString(
								"CU524_caso18"));
			return validadorDTO;
		}
		validadorDTO.setValido(true);
		return validadorDTO;
	}

	public DetValidacionOee getDetValidacionOee() {
		return detValidacionOee;
	}

	public void setDetValidacionOee(DetValidacionOee detValidacionOee) {
		this.detValidacionOee = detValidacionOee;
	}

	public List<DetValidacionOee> getDetValidacionOeeList() {
		return detValidacionOeeList;
	}

	public void setDetValidacionOeeList(
			List<DetValidacionOee> detValidacionOeeList) {
		this.detValidacionOeeList = detValidacionOeeList;
	}

}
