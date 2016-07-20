package py.com.excelsis.sicca.legajo.session.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.DatosSeguroMedico;
import py.com.excelsis.sicca.entity.DocumentosPersona;
import py.com.excelsis.sicca.entity.EquivalenciasLegajo;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.Familiares;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Migraciones;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.RecorridoLegajoMigrado;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import client.DatosFuncDTO;
import dblegajoclient.DBLegajoClient;
import dblegajoclient.Documento;
import dblegajoclient.EstudioRealizado;
import dblegajoclient.Familiar;
import dblegajoclient.HistoricoEmpleados;
import dblegajoclient.OtrosCursos;
import dblegajoclient.SeguroPersonaDTO;

@Name("genMigraFuncio752FC")
@Scope(ScopeType.CONVERSATION)
public class GenMigraFuncio752FC {
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	HashMap<String, String> diccSexo = new HashMap<String, String>();
	HashMap<String, String> diccEstadoCivil = new HashMap<String, String>();

	private Boolean migroBloque = false;

	public void init() {
		diccSexo.put("1", "M");
		diccSexo.put("6", "F");
		diccEstadoCivil.put("1", "C");
		diccEstadoCivil.put("2", "U");
		diccEstadoCivil.put("3", "E");
		diccEstadoCivil.put("4", "V");
		diccEstadoCivil.put("5", "S");
		diccEstadoCivil.put("6", "D");
		migroBloque = false;
	}

	private Long traerPersonaSicca(String nroCedula) {
		Query q =
			em.createQuery("select Persona from Persona Persona where Persona.documentoIdentidad = :documentoIdentidad");
		q.setParameter("documentoIdentidad", nroCedula);
		List<Persona> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPersona();

		return null;
	}

	private Legajos traerLegajo(Long idPersona) {
		Query q =
			em.createQuery("select Legajos  from Legajos Legajos where Legajos.persona.idPersona = :idPersona  ");
		q.setParameter("idPersona", idPersona);
		List<Legajos> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	private void insertMigraciones(DatosFuncDTO datosFuncDTO, Long idConfigUO, Long mensaje) {
		Migraciones migra = new Migraciones();
		migra.setDocumentoIdentidad(datosFuncDTO.getNroCedula());
		migra.setNombre(datosFuncDTO.getNombre());
		migra.setApellido(datosFuncDTO.getApellido());
		migra.setTipoPersona(datosFuncDTO.getTipoPersona());
		if (idConfigUO != null) {
			migra.setConfiguracionUoCab(new ConfiguracionUoCab());
			migra.getConfiguracionUoCab().setIdConfiguracionUo(idConfigUO);
		}
		migra.setMensaje(mensaje);
		migra.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		migra.setFechaAlta(new Date());
		em.persist(migra);
		em.flush();
	}

	private void pasoA1(Long idPerSicca, Integer idPerLegajo, String docIdentidad, Long idConfigUO, String tipoPersona) {
		init();
		bloqueDocumentos(idPerSicca, idPerLegajo, idConfigUO);
		bloqueSeguroMedico(idPerSicca, idPerLegajo, docIdentidad, idConfigUO);
		bloqueFamiliar(idPerSicca, idPerLegajo, idConfigUO, docIdentidad, tipoPersona);
		bloqueEstudiosRealizados(idPerSicca, idPerLegajo, idConfigUO, docIdentidad);
		bloqueOtrosCursos(idPerSicca, idPerLegajo, idConfigUO, docIdentidad);
		bloqueHistoricoDePuestos(idPerSicca, idPerLegajo, idConfigUO, docIdentidad, tipoPersona);
		pasoFinal(idPerSicca);
	}

	private void pasoFinal(Long idPerSicca) {
		if (migroBloque) {
			Legajos legajo = traerLegajo(idPerSicca);
			legajo.setMigrado(true);
			legajo = em.merge(legajo);
			migroBloque = false;
			em.flush();
		}
	}

	private DatosEspecificos traerDatosEspecificosEstado(String tipoPersona) {

		String estado = null;
		if (tipoPersona.equalsIgnoreCase("C")) {
			estado = "CONTRATADO";
		} else if (tipoPersona.equalsIgnoreCase("P")) {
			estado = "PERMANANETE";
		}
		DatosEspecificos de =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", estado);
		return de;

	}

	private void bloqueHistoricoDePuestos(Long idPerSicca, Integer idPerLegajo, Long idConfigUO, String nroDocumento, String tipoPersona) {
		List<HistoricoEmpleados> lista = DBLegajoClient.queryHistoricoEmpleadosLegajo(idPerLegajo);
		for (HistoricoEmpleados o : lista) {
			try {
//				EmpleadoPuesto ep = new EmpleadoPuesto();
//				ep.setActivo(true);
//				ep.setFechaAlta(new Date());
//				ep.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//				if (tipoPersona.equalsIgnoreCase("C")) {
//					ep.setContratado(true);
//				} else {
//					ep.setContratado(false);
//				}
//				ep.setDatosEspecificosByIdDatosEspEstado(traerDatosEspecificosEstado(tipoPersona));
//				ep.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR MIGRACION"));
//				ep.setPersona(new Persona());
//				ep.getPersona().setIdPersona(idPerSicca);
//				ep.setObservacion(o.getId().getObservacion());
//				ep.setFechaInicio(calcDate(o.getId().getFechaInicio()));
//				ep.setFechaFin(calcDate(o.getId().getFechaFin()));
//				ep.setIncidenAntiguedad(false);
//				ep.setDescPuestoHistorico(o.getId().getPuesto());
//				ep.setDescOeeHistorico(o.getId().getEntidad());
//				ep.setActual(false);
				RecorridoLegajoMigrado r = new RecorridoLegajoMigrado();
				r.setCargo(o.getId().getPuesto());
				r.setOee(o.getId().getEntidad());
				r.setPersona(new Persona());
				r.getPersona().setIdPersona(idPerSicca);
				r.setObservacion(o.getId().getObservacion());
				em.persist(r);
				insertarMigraciones(idPerSicca, nroDocumento, idConfigUO, "legajo.recorrido_legajo_migrado",r.getIdRecorrido());
				em.flush();
				migroBloque = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Long equivalenciasCodEspecialidad(Integer codEspecialidad, String tablaLegajo, String tablaSicca) {
		Long codSicca = traerCodSicca(codEspecialidad.longValue(), tablaLegajo, tablaSicca);
		if (codSicca == null) {
			String descripcion = DBLegajoClient.traerEspecializacion(codEspecialidad);
			Long idEspecialidades = verifExisteEnPlaniEspe(codEspecialidad, descripcion);
			if (idEspecialidades == null) {
				idEspecialidades = insertarEnEspecialidades(descripcion);
			}
			codSicca =
				insertarEquivalencia(tablaLegajo, tablaSicca, new Long(codEspecialidad), idEspecialidades);
		}
		return codSicca;
	}

	private Long equivalenciasCodPaises(String codPais, String tablaLegajo, String tablaSicca) {
		Long codSicca = traerCodSicca(codPais, tablaLegajo, tablaSicca);
		if (codSicca == null) {
			String descripcion = DBLegajoClient.traerPais(codPais.toString());
			Long idPais = verifExisteEnPais(descripcion);
			if (idPais == null) {
				idPais = insertarEnPais(descripcion);
			}
			codSicca = insertarEquivalencia(tablaLegajo, tablaSicca, codPais, idPais);
		}
		return codSicca;
	}

	private Long traerTituloAcademico() {
		Query q =
			em.createQuery("select TituloAcademico from TituloAcademico TituloAcademico "
				+ " where TituloAcademico.descripcion = :titDesc and TituloAcademico.nivelEstudio.descripcion = :nivelDesc");
		q.setParameter("titDesc", "OTROS");
		q.setParameter("nivelDesc", "OTROS");
		List<TituloAcademico> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdTituloAcademico();
		}
		return null;
	}

	private Long traerInstiEducativa() {
		Query q =
			em.createQuery("select InstitucionEducativa from InstitucionEducativa InstitucionEducativa "
				+ " where InstitucionEducativa.descripcion = :desc and activo is true  ");
		q.setParameter("desc", "OTRAS");

		List<InstitucionEducativa> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdInstEducativa();
		}
		return null;
	}

	private void bloqueOtrosCursos(Long idPerSicca, Integer idPerLegajo, Long idConfigUO, String nroDocumento) {
		List<OtrosCursos> lista = DBLegajoClient.queryOtrosCursosLegajo(idPerLegajo);
		Long idEspecialidad = null;
		Long idPais = null;
		for (OtrosCursos o : lista) {
			try {
				EstudiosRealizados er = new EstudiosRealizados();
				if (o.getId().getEspecialidad() != null) {
					idEspecialidad =
						equivalenciasCodEspecialidad(o.getId().getEspecialidad(), "especializacion", "planificacion.especialidades");
				}
				if (o.getId().getCodPais() != null) {
					idPais = equivalenciasCodPaises(o.getId().getCodPais(), "pais", "general.pais");
				}
				// Insert Estudios Realizados
				er.setActivo(true);
				er.setFechaAlta(new Date());
				er.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				er.setPersona(new Persona());
				er.getPersona().setIdPersona(idPerSicca);
				er.setTituloAcademico(new TituloAcademico());
				er.getTituloAcademico().setIdTituloAcademico(traerTituloAcademico());
				er.setFechaInicio(calcDate(o.getId().getFecha()));
				er.setFechaFin(calcDate(o.getId().getFecha()));
				if (idPais != null) {
					er.setPais(new Pais());
					er.getPais().setIdPais(idPais);
				}
				if (idEspecialidad != null) {
					er.setEspecialidades(new Especialidades());
					er.getEspecialidades().setIdEspecialidades(idEspecialidad);
				}
				er.setOtroTituloObt(o.getId().getNombreCurso());
				er.setInstitucionEducativa(new InstitucionEducativa());
				er.getInstitucionEducativa().setIdInstEducativa(traerInstiEducativa());
				er.setTipoDuracion("H");
				er.setFinalizo(true);
				if (o.getId().getDuracionHoras() != null)
					er.setDuracionHoras(o.getId().getDuracionHoras().intValue());
				em.persist(er);
				insertarMigraciones(idPerSicca, nroDocumento, idConfigUO, "seleccion.estudios_realizados", er.getIdEstudiosRealizados());
				em.flush();
				migroBloque = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void bloqueEstudiosRealizados(Long idPerSicca, Integer idPerLegajo, Long idConfigUO, String nroDocumento) {
		List<EstudioRealizado> lista = DBLegajoClient.queryEstudioRealizadoLegajo(idPerLegajo);
		Long idEspecialidad = null;
		Long idPais = null;
		for (EstudioRealizado o : lista) {
			try {
				EstudiosRealizados er = new EstudiosRealizados();
				if (o.getId().getCodEspecialidad() != null) {
					idEspecialidad =
						equivalenciasCodEspecialidad(o.getId().getCodEspecialidad(), "especializacion", "planificacion.especialidades");
				}
				if (o.getId().getCodPais() != null) {
					idPais = equivalenciasCodPaises(o.getId().getCodPais(), "pais", "general.pais");
				}
				if (o.getId().getDuracionHoras() != null) {
					er.setTipoDuracion("H");
				} else {
					er.setTipoDuracion("A");
				}
				// Insert Estudios Realizados
				er.setActivo(true);
				er.setFechaAlta(new Date());
				er.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				er.setPersona(new Persona());
				er.getPersona().setIdPersona(idPerSicca);
				er.setTituloAcademico(new TituloAcademico());
				er.getTituloAcademico().setIdTituloAcademico(traerTituloAcademico());
				if (idPais != null) {
					er.setPais(new Pais());
					er.getPais().setIdPais(idPais);
				}
				if (idEspecialidad != null) {
					er.setEspecialidades(new Especialidades());
					er.getEspecialidades().setIdEspecialidades(idEspecialidad);
				}
				er.setOtroTituloObt(o.getId().getTituloObtenido());
				er.setInstitucionEducativa(new InstitucionEducativa());
				er.getInstitucionEducativa().setIdInstEducativa(traerInstiEducativa());
				if (o.getId().getFinalizo() != null
					&& o.getId().getFinalizo().equalsIgnoreCase("S")) {
					er.setFinalizo(true);
				} else if (o.getId().getFinalizo() != null
					&& o.getId().getFinalizo().equalsIgnoreCase("N")) {
					er.setFinalizo(false);
				}
				if (o.getId().getDuracionHoras() != null)
					er.setDuracionHoras(o.getId().getDuracionHoras().intValue());

				em.persist(er);
				// Registrar Migracion
				insertarMigraciones(idPerSicca, nroDocumento, idConfigUO, "seleccion.estudios_realizados", er.getIdEstudiosRealizados());
				em.flush();
				migroBloque = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void insertarMigraciones2(String nombre, String apellido, String tipoPersona, String nroDocumento, Long idConfigUO, Long nroMensaje) {
		Migraciones migra = new Migraciones();
		migra.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		migra.setFechaAlta(new Date());
		migra.setNombre(nombre);
		migra.setTipoPersona(tipoPersona);
		migra.setApellido(apellido);
		migra.setDocumentoIdentidad(nroDocumento);
		migra.setMensaje(nroMensaje);
		migra.setConfiguracionUoCab(new ConfiguracionUoCab());
		migra.getConfiguracionUoCab().setIdConfiguracionUo(idConfigUO);
		em.persist(migra);
	}

	private void insertarMigraciones(Long idPersona, String nroDocumento, Long idConfigUO, String tablaSicca, Long idTabla) {
		Migraciones migra = new Migraciones();
		migra.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		migra.setFechaAlta(new Date());
		migra.setPersona(new Persona());
		migra.getPersona().setIdPersona(idPersona);
		migra.setDocumentoIdentidad(nroDocumento);
		migra.setTablaSicca(tablaSicca);
		migra.setIdTabla(idTabla);
		migra.setConfiguracionUoCab(new ConfiguracionUoCab());
		migra.getConfiguracionUoCab().setIdConfiguracionUo(idConfigUO);
		em.persist(migra);
	}

	private Date calcDate(XMLGregorianCalendar xmlGregorian) {
		if (xmlGregorian == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaString =
			xmlGregorian.getDay() + "/" + xmlGregorian.getMonth() + "/" + xmlGregorian.getYear();
		Date fecha = null;
		try {
			fecha = sdf.parse(fechaString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fecha;
	}

	private Long obtenerParentezco(Integer parentezcoCod, String dgDesc) {
		Query q =
			em.createQuery("select  DatosEspecificos from DatosEspecificos DatosEspecificos  "
				+ " where DatosEspecificos.valorNum = :valorNum "
				+ " and DatosEspecificos.datosGenerales.descripcion = :datosGenerales");
		q.setParameter("valorNum", parentezcoCod);
		q.setParameter("datosGenerales", dgDesc);

		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdDatosEspecificos();
		return null;
	}

	private void bloqueFamiliar(Long idPerSicca, Integer idPerLegajo, Long idConfigUO, String nroDocumento, String tipoPersona) {
		List<Familiar> lista = DBLegajoClient.queryFamiliarLegajo(idPerLegajo);
		PersonaDTO wsResponse = null;
		Long mensaje = new Long(5);
		for (Familiar o : lista) {
			try {
				wsResponse =
					seleccionUtilFormController.buscarPersona(o.getId().getNumeroDoc(), "PARAGUAY");
				if (wsResponse.getHabilitarBtn() == null) {
					mensaje = new Long(3);
					insertarMigraciones2(o.getId().getNombre(), o.getId().getApellido(), tipoPersona, nroDocumento, idConfigUO, mensaje);
					em.flush();
					migroBloque = true;
				} else if (wsResponse.getHabilitarBtn()) {
					Persona persona = new Persona();
					persona.setActivo(true);
					persona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					persona.setFechaAlta(new Date());
					persona.setNombres(o.getId().getNombre());
					persona.setApellidos(o.getId().getApellido());
					persona.setDocumentoIdentidad(o.getId().getNumeroDoc());
					persona.setEMail(o.getId().getEmail());
					if (o.getId().getEstadoCivil() != null) {
						persona.setEstadoCivil(diccEstadoCivil.get(o.getId().getEstadoCivil().toString()));
					}
					if (o.getId().getSexo() != null) {
						persona.setSexo(diccSexo.get(o.getId().getSexo().toString()));
					}
					persona.setFechaNacimiento(calcDate(o.getId().getFechaNacimiento()));
					persona.setCallePrincipal(o.getId().getCallePrincipal());
					persona.setPrimeraLateral(o.getId().getCalleSecundaria());
					persona.setTelefonoPart(o.getId().getTelefono());
					persona.setTelefonos(o.getId().getCelular());
					em.persist(persona);
					paso322(persona, idPerSicca, o, idConfigUO, tipoPersona);
					em.flush();
					migroBloque = true;
				} else {
					paso322(wsResponse.getPersona(), idPerSicca, o, idConfigUO, tipoPersona);
					em.flush();
					migroBloque = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void paso322(Persona persona, Long idPerSicca, Familiar o, Long idConfigUO, String tipoPersona) {
		Long idParentezco =
			obtenerParentezco(o.getId().getParentescoCod().intValue(), "GRADO DE PARENTESCO");
		if (idParentezco == null) {
			insertarMigraciones2(persona.getNombres(), persona.getApellidos(), tipoPersona, persona.getDocumentoIdentidad(), idConfigUO, new Long(4));
			return;
		}
		// Insertar familiares
		Familiares familiares = new Familiares();
		familiares.setActivo(true);
		familiares.setFechaAlta(new Date());
		familiares.setPersona(new Persona());
		familiares.getPersona().setIdPersona(idPerSicca);
		familiares.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		familiares.setPersonaFamiliar(new Persona());
		familiares.getPersonaFamiliar().setIdPersona(persona.getIdPersona());
		familiares.setDatosEspecificosFamiliar(new DatosEspecificos());
		familiares.getDatosEspecificosFamiliar().setIdDatosEspecificos(idParentezco);
		familiares.setFuncionarioPublico(2);
		em.persist(familiares);
		// Registrar la migracion
		insertarMigraciones(idPerSicca, null, idConfigUO, "legajo.familiares", familiares.getIdFamiliar());
	}

	private void bloqueDocumentos(Long idPerSicca, Integer idPerLegajo, Long idConfigUO) {
		List<Documento> lista = DBLegajoClient.queryDocumentoLegajo(idPerLegajo);
		for (Documento o : lista) {
			try {
				DocumentosPersona docPer = new DocumentosPersona();
				docPer.setPersona(new Persona());
				docPer.setFechaAlta(new Date());
				docPer.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				docPer.getPersona().setIdPersona(idPerSicca);
				if (o.getId().getTipoDocCod() != null)
					docPer.setTipoDocumento(o.getId().getTipoDocCod().intValue());
				docPer.setDocumentoNro(o.getId().getDocumentoNro());
				docPer.setOtroDato(o.getId().getEspecialidad());
				em.persist(docPer);
				insertarMigraciones(idPerSicca, o.getId().getDocumentoNro(), idConfigUO, "legajo.documentos_persona", docPer.getIdDocumentosPersona());
				em.flush();
				migroBloque = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Long traerCodSicca(Long codLegajo, String tablaLegajo, String tablaSicca) {
		Query q =
			em.createQuery("select EquivalenciasLegajo from EquivalenciasLegajo EquivalenciasLegajo "
				+ " where EquivalenciasLegajo.codLegajo = :codLegajo "
				+ " and EquivalenciasLegajo.tablaLegajo = :tablaLegajo "
				+ " and EquivalenciasLegajo.tablaSicca = :tablaSicca ");
		q.setParameter("codLegajo", codLegajo);
		q.setParameter("tablaLegajo", tablaLegajo);
		q.setParameter("tablaSicca", tablaSicca);
		List<EquivalenciasLegajo> lista = q.getResultList();

		if (lista.size() > 0)
			return lista.get(0).getCodSicca();
		return null;
	}

	private Long traerCodSicca(String codLegajo, String tablaLegajo, String tablaSicca) {
		Query q =
			em.createQuery("select EquivalenciasLegajo from EquivalenciasLegajo EquivalenciasLegajo "
				+ " where EquivalenciasLegajo.codLegajoChar = :codLegajoChar "
				+ " and EquivalenciasLegajo.tablaLegajo = :tablaLegajo "
				+ " and EquivalenciasLegajo.tablaSicca = :tablaSicca ");
		q.setParameter("codLegajoChar", codLegajo);
		q.setParameter("tablaLegajo", tablaLegajo);
		q.setParameter("tablaSicca", tablaSicca);
		List<EquivalenciasLegajo> lista = q.getResultList();

		if (lista.size() > 0)
			return lista.get(0).getCodSicca();
		return null;
	}

	private Long verifExisteDE(String paramDatoGeneral, String paramDatoEspe) {

		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where upper(DatosEspecificos.descripcion) = :deDesc "
				+ " and upper(DatosEspecificos.datosGenerales.descripcion) = :dgDesc ");
		q.setParameter("deDesc", paramDatoEspe.toUpperCase());
		q.setParameter("dgDesc", paramDatoGeneral.toUpperCase());
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdDatosEspecificos();
		}
		return null;
	}

	private Long verifExisteEnPlaniEspe(Integer codEspecialidad, String descripcion) {

		Query q =
			em.createQuery("select Especialidades from Especialidades Especialidades "
				+ " where upper(Especialidades.descripcion) = :descripcion");
		q.setParameter("descripcion", descripcion);
		List<Especialidades> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdEspecialidades();
		}
		return null;
	}

	private Long verifExisteEnPais(String descripcion) {

		Query q =
			em.createQuery("select Pais from Pais Pais "
				+ " where upper(Pais.descripcion) = :descripcion");
		q.setParameter("descripcion", descripcion);
		List<Pais> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdPais();
		}
		return null;
	}

	private DatosGenerales traerDatosGenerales(String descripcion) {
		return null;
	}

	private Long insertarDatosEspecificos(String descDG, String descDE) {
		DatosEspecificos de = new DatosEspecificos();
		de.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		de.setDescripcion(descDE);
		de.setFechaAlta(new Date());
		de.setActivo(true);
		de.setDatosGenerales(traerDatosGenerales(descDG));
		em.persist(de);
		return de.getIdDatosEspecificos();
	}

	private Long insertarEnEspecialidades(String desc) {
		Especialidades esp = new Especialidades();
		esp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		esp.setFechaAlta(new Date());
		esp.setActivo(true);
		esp.setDescripcion(desc);
		em.persist(esp);
		return esp.getIdEspecialidades();
	}

	private Long insertarEnPais(String desc) {
		Pais pais = new Pais();
		pais.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		pais.setFechaAlta(new Date());
		pais.setActivo(true);
		pais.setDescripcion(desc);
		em.persist(pais);
		return pais.getIdPais();
	}

	private Long insertarEquivalencia(String tablaLegajo, String tablaSicca, Long codLegajo, Long codSicca) {
		EquivalenciasLegajo el = new EquivalenciasLegajo();
		el.setTablaLegajo(tablaLegajo);
		el.setTablaSicca(tablaSicca);
		el.setCodLegajo(codLegajo);
		el.setCodSicca(codSicca);
		em.persist(el);
		return el.getCodSicca();
	}

	private Long insertarEquivalencia(String tablaLegajo, String tablaSicca, String codLegajo, Long codSicca) {
		EquivalenciasLegajo el = new EquivalenciasLegajo();
		el.setTablaLegajo(tablaLegajo);
		el.setTablaSicca(tablaSicca);
		el.setCodLegajoChar(codLegajo);
		el.setCodSicca(codSicca);
		em.persist(el);
		return el.getCodSicca();
	}

	private Long equivalenciasSeguroMedico(String seguroCod, String descSeguroMedico, String tablaLegajo, String tablaSicca) {
		Long codSicca = traerCodSicca(new Long(seguroCod), tablaLegajo, tablaSicca);
		if (codSicca == null) {
			Long datosEspec = verifExisteDE("SEGURO MEDICO", descSeguroMedico);
			if (datosEspec == null) {
				datosEspec = insertarDatosEspecificos("SEGURO MEDICO", descSeguroMedico);
			}
			codSicca =
				insertarEquivalencia(tablaLegajo, tablaSicca, new Long(seguroCod), datosEspec);
		}
		return codSicca;
	}

	private void registrarMigracionSeguro(String docIdentidad, Long idPersona, Long idDatosSeguroMedico, Long idConfUO) {
		Migraciones migra = new Migraciones();
		migra.setDocumentoIdentidad(docIdentidad);
		migra.setPersona(new Persona());
		migra.getPersona().setIdPersona(idPersona);
		migra.setTablaSicca("legajo.datos_seguro_medico");
		migra.setIdTabla(idDatosSeguroMedico);
		migra.setConfiguracionUoCab(new ConfiguracionUoCab());
		migra.getConfiguracionUoCab().setIdConfiguracionUo(idConfUO);
		migra.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		migra.setFechaAlta(new Date());
		em.persist(em);
		em.flush();
	}

	private void bloqueSeguroMedico(Long idPerSicca, Integer idPerLegajo, String docIdentidad, Long idConfigUO) {

		List<SeguroPersonaDTO> lSeguro = DBLegajoClient.querySeguroPersona(idPerLegajo);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (SeguroPersonaDTO o : lSeguro) {
			try {

				Long codSicca =
					equivalenciasSeguroMedico(o.getSeguroCod(), o.getDescSeguroMedico(), "seguro_medico", "seleccion.datos_especificos");

				DatosSeguroMedico dsm = new DatosSeguroMedico();
				dsm.setActivo(true);
				dsm.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				dsm.setFechaAlta(new Date());
				dsm.setPersona(new Persona());
				dsm.getPersona().setIdPersona(idPerSicca);
				dsm.setDatosEspSeguroMedico(new DatosEspecificos());
				dsm.getDatosEspSeguroMedico().setIdDatosEspecificos(codSicca);
				dsm.setPlanSeguroMedico(o.getTipoPlan());
				dsm.setNombreAsegurador(o.getNombreAsegurador());
				if (o.getFechaIngreso() != null)
					dsm.setFechaIngreso(sdf.parse(o.getFechaIngreso()));
				em.persist(dsm);
				insertarMigraciones(idPerSicca, docIdentidad, idConfigUO, "legajo.datos_seguro_medico", dsm.getIdDatosSeguroMedico());
				em.flush();
				migroBloque = true;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}

	public String generarMigracion(List<client.DatosFuncDTO> lista, Long idConfigUO) {
		Long idPerSicca = null;
		Integer idPerLegajo = null;
		Legajos legajo = null;
		try {
			for (DatosFuncDTO o : lista) {
				idPerSicca = traerPersonaSicca(o.getNroCedula());
				idPerLegajo = DBLegajoClient.traerPersonaLegajo(new Long(o.getNroCedula()));
				if (idPerSicca != null) {
					legajo = traerLegajo(idPerSicca);
					if (legajo != null) {
						if (legajo.getMigrado() != null && legajo.getMigrado()) {
							continue;
						} else {
							pasoA1(idPerSicca, idPerLegajo, o.getNroCedula(), idConfigUO, o.getTipoPersona());
						}
					} else {
						insertMigraciones(o, idConfigUO, new Long("2"));
					}
				} else {
					insertMigraciones(o, idConfigUO, new Long("1"));
				}
			}
			return "FINALIZADO";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}
}
