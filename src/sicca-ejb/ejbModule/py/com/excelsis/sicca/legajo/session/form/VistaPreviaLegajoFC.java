package py.com.excelsis.sicca.legajo.session.form;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.dto.CapacitacionesDTO;
import py.com.excelsis.sicca.dto.FuncionarioCptDTO;
import py.com.excelsis.sicca.dto.InconstitucionalidadDTO;
import py.com.excelsis.sicca.dto.RecorridoLaboralDTO;
import py.com.excelsis.sicca.dto.SumarioDTO;
import py.com.excelsis.sicca.entity.AuditLegajo;
import py.com.excelsis.sicca.entity.AuditLegajoDet;
import py.com.excelsis.sicca.entity.AuditLegajoObs;
import py.com.excelsis.sicca.entity.DatosAmonestacion;
import py.com.excelsis.sicca.entity.DatosEnfermedad;
import py.com.excelsis.sicca.entity.DatosLicenciaMedica;
import py.com.excelsis.sicca.entity.DatosPermiso;
import py.com.excelsis.sicca.entity.DatosPremio;
import py.com.excelsis.sicca.entity.DatosSeguroMedico;
import py.com.excelsis.sicca.entity.DatosVacaciones;
import py.com.excelsis.sicca.entity.DeclaracionJurada;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.DiscapacidadApoyos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.DocumentosPersona;
import py.com.excelsis.sicca.entity.EmpleadoPuesto651;
import py.com.excelsis.sicca.entity.EstudiosRealizadosLegajo;
import py.com.excelsis.sicca.entity.EvaluacionesFuncionario;
import py.com.excelsis.sicca.entity.Excepcionados;
import py.com.excelsis.sicca.entity.ExperienciaLaboralLegajo;
import py.com.excelsis.sicca.entity.Familiares;
import py.com.excelsis.sicca.entity.IdiomasLegajoPersona;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidadLegajo;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidadLegajo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("vistaPreviaLegajoFC")
public class VistaPreviaLegajoFC {

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
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	LegajoPrincipalFC legajoPrincipalFC;
	@In(create = true)
	EvaluacionesLegajoFC evaluacionesLegajoFC;


	private Long idPersona;
	private String texto;
	private String observacionesPendientes;
	private String antiguedad;
	private Boolean rehInstituto = false;
	private Boolean rehHospital = false;
	private Boolean rehParticular = false;
	private Boolean rehEntrenamiento = false;
	private Boolean ninguno = false;
	private Boolean otro = false;
	private Boolean mostrarObservacion = false;
	private Boolean mostrarBtnImprimir = false;
	private Persona persona;
	private Legajos legajo;
	private DiscapacidadApoyos apoyos;

	private List<DocumentosPersona> listaDocAdicionales;
	private List<Documentos> listaDocAdjuntos;
	private List<DatosEnfermedad> listaEnfermedades;
	private List<DatosLicenciaMedica> listaLicencias;
	private List<DatosSeguroMedico> listaSeguros;
	private List<PersonaDiscapacidadLegajo> listaDiscapacidad;
	private List<ReprPersonaDiscapacidadLegajo> listaRepreDiscapacidad;
	private List<Familiares> listaFamiliares;
	private List<EstudiosRealizadosLegajo> listaEstudiosRealizados;
	private List<EstudiosRealizadosLegajo> listaOtrosEstudiosRealizados;
	private List<IdiomasLegajoPersona> listaIdiomas;
	private List<CapacitacionesDTO> listaCapacitacionesDTO;
	private List<DeclaracionJurada> listaDeclaracionJurada;
	private List<ExperienciaLaboralLegajo> listaExpLaboral;
	private List<EvaluacionesFuncionario> listaEvalFuncionarios;
	private List<InconstitucionalidadDTO> listaInconstitucionalidadDTO;
	private List<SumarioDTO> listaSumariosDTO;
	private List<Excepcionados> listaExcepcionados;
	private List<Desvinculacion> listaDesvinculaciones;
	private List<Inhabilitados> listaInhabilitados;
	private List<Jubilados> listaJubilados;
	private List<DatosVacaciones> listaVacaciones;
	private List<DatosPermiso> listaPermisos;
	private List<DatosPremio> listaPremios;
	private List<DatosAmonestacion> listaAmonestaciones;
	private List<RecorridoLaboralDTO> listaRecorridoLaboral;
	private List<AuditLegajoObs> listaLegajoObs;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		persona = em.find(Persona.class, idPersona);
		rehInstituto = false;
		rehHospital = false;
		rehParticular = false;
		rehEntrenamiento = false;
		ninguno = false;
		otro = false;
		obtenerDocPersonas();
		obtenerDocAdicionales();
		obtenerDocAdjuntos();
		obtenerEnfermedades();
		obtenerLicencias();
		obtenerSeguros();
		obtenerApoyos();
		obtenerDiscapacidad();
		obtenerRepresentanteDiscapacidad();
		obtenerFamiliares();
		obtenerEstudiosRealizados();
		obtenerOtrosEstudiosRealizados();
		obtenerIdiomas();
		obtenerCapacitaciones();
		obtenerDeclaracionJurada();
		obtenerExpLaboral();
		obtenerEvalFuncionario();
		obtenerIncontitucionalidad();
		obtenerSumarios();
		obtenerExcepcionados();
		obtenerDesvinculaciones();
		obtenerInhabilitados();
		obtenerJubilados();
		obtenerVacaciones();
		obtenerPermisos();
		obtenerPremios();
		obtenerAmonestaciones();
		obtenerAntiguedad();
		obtenerRecorridoLaboral();
		validarObservacion();
		//legajoPrincipalFC.cancelar();
		evaluacionesLegajoFC.setIdPersona(idPersona);
		evaluacionesLegajoFC.setTexto(texto);
		evaluacionesLegajoFC.init();
				
	}
	
	public void initEvaluacion() {
		persona = em.find(Persona.class, idPersona);
		rehInstituto = false;
		rehHospital = false;
		rehParticular = false;
		rehEntrenamiento = false;
		ninguno = false;
		otro = false;
		obtenerDocPersonas();
		obtenerDocAdicionales();
		obtenerEnfermedades();
		obtenerLicencias();
		obtenerSeguros();
		obtenerApoyos();
		obtenerDiscapacidad();
		obtenerRepresentanteDiscapacidad();
		obtenerFamiliares();
		obtenerEstudiosRealizados();
		obtenerOtrosEstudiosRealizados();
		obtenerIdiomas();
		obtenerCapacitaciones();
		obtenerDeclaracionJurada();
		obtenerExpLaboral();
		obtenerEvalFuncionario();
		obtenerIncontitucionalidad();
		obtenerSumarios();
		obtenerExcepcionados();
		obtenerDesvinculaciones();
		obtenerInhabilitados();
		obtenerJubilados();
		obtenerVacaciones();
		obtenerPermisos();
		obtenerPremios();
		obtenerAmonestaciones();
		obtenerAntiguedad();
		obtenerRecorridoLaboral();
		

	}


	private void validarObservacion() {
		if (texto.equalsIgnoreCase("P"))
			mostrarObservacion = false;
		else {
			obtenerObservaciones();
			if (listaLegajoObs.isEmpty())
				mostrarObservacion = false;
			else
				mostrarObservacion = true;
		}
		validarImprimir();

	}

	private void validarImprimir() {
		if (texto.equalsIgnoreCase("P")) {
			Query q = em
					.createQuery("select l from AuditLegajo l "
							+ "  where l.persona.idPersona = "
							+ persona.getIdPersona());
			List<AuditLegajo> lista = q.getResultList();
			if (lista.isEmpty()) {
				mostrarBtnImprimir = true;
				return;
			}
			for (AuditLegajo obj : lista) {
				if (obj.getEstado().intValue() != 1) {
					mostrarBtnImprimir = false;
					return;
				}
			}
			mostrarBtnImprimir = true;
		} else
			mostrarBtnImprimir = true;
	}

	private void obtenerDocPersonas() {
		Query q = em.createQuery("select l from Legajos l "
				+ "  where l.persona.idPersona = " + persona.getIdPersona());
		List<Legajos> lista = q.getResultList();
		if (!lista.isEmpty())
			legajo = lista.get(0);
		else
			legajo = new Legajos();
	}

	public String estadoCivil(String elEstadoCorto) {
		Query q = em
				.createQuery("select referencias from Referencias referencias "
						+ " where referencias.dominio = 'ESTADO_CIVIL' AND referencias.descAbrev = '"
						+ elEstadoCorto + "'");
		List<Referencias> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0).getDescLarga();
		} else {
			return "-";
		}
	}
	
	public String parentezco(Familiares familiar) {
		if(familiar.getPersonaFamiliar() != null){
			/*Query q = em
					.createQuery("select referencias from Referencias referencias "
							+ " where referencias.dominio = 'ESTADO_CIVIL' AND referencias.descAbrev = '"
							+ elEstadoCorto + "'");
			List<Referencias> lista = q.getResultList();
			if (lista.size() == 1) {
				return lista.get(0).getDescLarga();
			} else {
				return "-";
			}*/
			//String texto = familiar.getPersonaFamiliar().getDatosEspecificos().getDescripcion();
			String texto = familiar.getDatosEspecificosFamiliar().getDescripcion();
			return texto;
		}
		else{
			String texto = familiar.getDatosEspecificosFamiliar().getDescripcion();
			return texto;
		}
	}


	/**
	 * Seccion Documentos Adicionales
	 */
	private void obtenerDocAdicionales() {
		listaDocAdicionales = new ArrayList<DocumentosPersona>();
		Query q = em.createQuery("select d from DocumentosPersona d "
				+ "  where d.persona.idPersona = " + persona.getIdPersona());
		listaDocAdicionales = q.getResultList();

	}
	
	/**
	 * Seccion Documentos Adjunos
	 */
	private void obtenerDocAdjuntos() {
		listaDocAdjuntos = new ArrayList<Documentos>();
				
		listaDocAdjuntos = em.createQuery(
				" Select d from Documentos d "
						+ " where d.activo= TRUE "
						+ " and d.nombrePantalla = 'Tab4AdjuntarDocumentos_edit_legajo'"
						+ " and d.validoLegajo = TRUE "
						+ " and d.persona.idPersona ="
						+ idPersona + " order by d.fechaDoc desc ").getResultList();

	}

	public String obtenerDescDocAdicional(DocumentosPersona dp) {
		Referencias ref = referenciasUtilFormController
				.getReferenciaPorValorNum("LEGAJO_DOC", dp.getTipoDocumento());
		if (ref != null)
			return ref.getDescLarga();
		return null;
	}

	public String obtenerValorDocAdicional(DocumentosPersona dp) {
		Referencias ref = referenciasUtilFormController
				.getReferenciaPorValorNum("LEGAJO_DOC", dp.getTipoDocumento());
		if (ref != null)
			return ref.getValorAlf();
		return null;
	}

	/**
	 * Fin DOCUMENTOS ADICIONALES
	 * 
	 */

	/**
	 * Seccion ENFERMEDADES
	 */

	private void obtenerEnfermedades() {
		listaEnfermedades = new ArrayList<DatosEnfermedad>();
		Query q = em.createQuery("select d from DatosEnfermedad d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ persona.getIdPersona());
		listaEnfermedades = q.getResultList();

	}

	/**
	 * Fin ENFERMEDADES
	 * 
	 */

	/**
	 * Seccion LICENCIAS MEDICAS / REPOSOS
	 */

	private void obtenerLicencias() {
		listaLicencias = new ArrayList<DatosLicenciaMedica>();
		Query q = em.createQuery("select d from DatosLicenciaMedica d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ persona.getIdPersona());
		listaLicencias = q.getResultList();
	}

	/**
	 * Fin LICENCIAS
	 * 
	 */

	/**
	 * Seccion SEGUROS MEDICOS
	 */

	private void obtenerSeguros() {
		listaSeguros = new ArrayList<DatosSeguroMedico>();
		Query q = em.createQuery("select d from DatosSeguroMedico d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ persona.getIdPersona());
		listaSeguros = q.getResultList();
	}

	/**
	 * Fin SEGUROS MEDICOS
	 * 
	 */

	/**
	 * Seccion DISCAPACIDAD
	 */

	private void obtenerApoyos() {

		Query q = em.createQuery("select d from DiscapacidadApoyos d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ persona.getIdPersona());
		List<DiscapacidadApoyos> ap = q.getResultList();
		apoyos = new DiscapacidadApoyos();
		if (!ap.isEmpty()) {
			apoyos = ap.get(0);
			cargarDatosApoyo();
		}
	}

	private void cargarDatosApoyo() {
		if (apoyos.getCentroSal())
			rehHospital = true;
		if (apoyos.getInstRehab())
			rehHospital = true;
		if (apoyos.getNinguno())
			ninguno = true;
		if (apoyos.getOtroApoyo())
			otro = true;
		if (apoyos.getParticular())
			rehParticular = true;
		if (apoyos.getEntrenamiento())
			rehEntrenamiento = true;
	}

	private void obtenerDiscapacidad() {
		listaDiscapacidad = new ArrayList<PersonaDiscapacidadLegajo>();
		Query q = em.createQuery("select d from PersonaDiscapacidadLegajo d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ persona.getIdPersona());
		listaDiscapacidad = q.getResultList();

	}

	private void obtenerRepresentanteDiscapacidad() {
		listaRepreDiscapacidad = new ArrayList<ReprPersonaDiscapacidadLegajo>();
		Query q = em.createQuery("select d from ReprPersonaDiscapacidadLegajo d "
				+ "  where d.activo is true and d.personaRep.idPersona = "
				+ persona.getIdPersona());
		listaRepreDiscapacidad = q.getResultList();

	}

	/**
	 * Fin SECCION DISCAPACIDAD
	 * 
	 */

	/**
	 * Seccion FAMILIARES
	 */

	private void obtenerFamiliares() {
		listaFamiliares = new ArrayList<Familiares>();

		String query = "SELECT F.* FROM LEGAJO.FAMILIARES F "
				//+ "JOIN SELECCION.DATOS_ESPECIFICOS DE "
				//+ "ON DE.ID_DATOS_ESPECIFICOS = F.ID_DATOS_ESPECIFICOS_FAMILIAR "
				//+ "JOIN GENERAL.PERSONA P "
				//+ "ON P.ID_PERSONA = F.ID_PERSONA_FAMILIAR "
				//+ "JOIN GENERAL.PAIS "
				//+ "ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC "
				//+ "JOIN SELECCION.REFERENCIAS REF_SEXO "
				//+ "ON REF_SEXO.DESC_ABREV = P.SEXO "
				//+ "JOIN SELECCION.REFERENCIAS REF_EST "
				//+ "ON REF_EST.DESC_ABREV = P.ESTADO_CIVIL "
				//+ "JOIN SELECCION.REFERENCIAS REF_FUN "
				//+ "ON REF_FUN.VALOR_NUM = F.FUNCIONARIO_PUBLICO "
				+ "WHERE F.ACTIVO= TRUE " + "AND F.ID_PERSONA = " + idPersona;
				//+ " AND REF_SEXO.DOMINIO = 'SEXO' "
				//+ "AND REF_EST.DOMINIO = 'ESTADO_CIVIL' ";
				//+ "AND REF_FUN.DOMINIO = 'LEGAJO_FUNC'";

		listaFamiliares = em.createNativeQuery(query, Familiares.class)
				.getResultList();
	}

	public String esFuncionarioPublico(Familiares f) {
		Referencias ref = referenciasUtilFormController
				.getReferenciaPorValorNum("LEGAJO_FUNC",
						f.getFuncionarioPublico());
		if (ref != null)
			return ref.getDescLarga();
		return null;
	}

	/**
	 * Fin SECCION FAMILIARES
	 * 
	 */

	/**
	 * Seccion FORMACIÓN ACADEMICA
	 */

	private void obtenerEstudiosRealizados() {
		listaEstudiosRealizados = new ArrayList<EstudiosRealizadosLegajo>();
		Query q = em.createQuery("select e from EstudiosRealizadosLegajo e "
				+ "  where e.activo is true and e.otroNivel is null and e.persona.idPersona = "
				+ persona.getIdPersona() + " order by e.fechaFin desc");
		listaEstudiosRealizados = q.getResultList();
	}
	
	private void obtenerOtrosEstudiosRealizados() {
		listaOtrosEstudiosRealizados = new ArrayList<EstudiosRealizadosLegajo>();
		Query q = em.createQuery("select e from EstudiosRealizadosLegajo e "
				+ "  where e.activo is true and e.otroNivel is not null  and e.persona.idPersona = "
				+ persona.getIdPersona() + " order by e.fechaFin desc");
		listaOtrosEstudiosRealizados = q.getResultList();
	}

	private void obtenerIdiomas() {
		listaIdiomas = new ArrayList<IdiomasLegajoPersona>();
		Query q = em.createQuery("select i from IdiomasLegajoPersona i "
				+ "  where i.activo is true and i.persona.idPersona = "
				+ persona.getIdPersona());
		listaIdiomas = q.getResultList();
	}

	private void obtenerCapacitaciones() {
		listaCapacitacionesDTO = new ArrayList<CapacitacionesDTO>();
		String sql = "SELECT 	DISTINCT DE.DESCRIPCION AS TIPO, "
				+ "C.NOMBRE AS CURSO_BECA, OEE_CAP.DENOMINACION_UNIDAD OEE, "
				+ "DATE(C.FECHA_INICIO) ||' - '|| DATE(C.FECHA_FIN) AS FECHA_DESDE_HAST, "
				+ "C.CARGA_HORARIA ||'HS. - PAIS: '|| PAIS.DESCRIPCION AS CARGA_HORARIA,  "
				+ "CASE WHEN E.CERTIFICADO = 'P' THEN 'CERTIFICADO DE PARTICIPACIÓN' "
				+ "WHEN  E.CERTIFICADO = 'A' THEN 'CERTIFICADO DE APROBACIÓN' END AS CERTIFICADO "
				+ "FROM CAPACITACION.LISTA_DET DET "
				+ "JOIN CAPACITACION.POSTULACION_CAP POST ON POST.ID_POSTULACION = DET.ID_POSTULACION "
				+ "JOIN CAPACITACION.CAPACITACIONES C ON C.ID_CAPACITACION = POST.ID_CAPACITACION "
				+ "JOIN GENERAL.PERSONA P ON P.ID_PERSONA = POST.ID_PERSONA "
				+ "JOIN GENERAL.PAIS ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC  "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE_CAP "
				+ "ON OEE_CAP.ID_CONFIGURACION_UO =  C.ID_CONFIGURACION_UO "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE "
				+ "ON DE.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP "
				+ "LEFT JOIN CAPACITACION.EVALUACION_PART E ON E.ID_POSTULACION = POST.ID_POSTULACION "
				+ "WHERE DET.TIPO = 'P' AND POST.ID_PERSONA = " + idPersona;
		List<Object> rsl = em.createNativeQuery(sql).getResultList();

		if (!rsl.isEmpty()) {
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				CapacitacionesDTO dto = new CapacitacionesDTO();
				if (record[0] != null)
					dto.setTipo((String) record[0]);
				if (record[1] != null)
					dto.setCursoBeca((String) record[1]);
				if (record[2] != null)
					dto.setOee((String) record[2]);
				if (record[3] != null)
					dto.setFechaDesdeHasta((String) record[3]);
				if (record[4] != null)
					dto.setCargaHoraria((String) record[4]);
				if (record[5] != null)
					dto.setCertificado((String) record[5]);
				listaCapacitacionesDTO.add(dto);
			}
		}
	}

	/**
	 * Fin FORMACIÓN ACADEMICA
	 * 
	 */

	/**
	 * Seccion DECLARACION JURADA
	 */

	private void obtenerDeclaracionJurada() {
		listaDeclaracionJurada = new ArrayList<DeclaracionJurada>();
		Query q = em.createQuery("select d from DeclaracionJurada d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ persona.getIdPersona());
		listaDeclaracionJurada = q.getResultList();
	}

	/**
	 * Fin DECLARACION JURADA
	 * 
	 */

	/**
	 * Seccion EXPERIENCIA LABORAL
	 */
	private void obtenerExpLaboral() {
		listaExpLaboral = new ArrayList<ExperienciaLaboralLegajo>();
		Query q = em.createQuery("select e from ExperienciaLaboralLegajo e "
				+ "  where e.activo is true and e.persona.idPersona = "
				+ persona.getIdPersona());
		listaExpLaboral = q.getResultList();
	}

	/**
	 * Fin EXPERIENCIA LABORAL
	 * 
	 */

	/**
	 * Seccion RECORRIDO LABORAL
	 */
	private void obtenerAntiguedad() {
		String sql = "select sum(e.fecha_fin - e.fecha_inicio) as dias "
				+ "from general.empleado_puesto e " + "where e.id_persona = "
				+ idPersona + "and e.incide_antiguedad is true "
				+ "and e.fecha_inicio is not null and e.fecha_fin is not null";
			Object cantdias = (Object) em.createNativeQuery(sql).getSingleResult();
			
			String sql2 = "select sum(current_date- e.fecha_inicio) as dias "
				+ "from general.empleado_puesto e " + "where e.id_persona = "
				+ idPersona + "and e.actual is true "
				+ "and e.fecha_inicio is not null and e.fecha_fin is null";
			Object cantdias2 = (Object) em.createNativeQuery(sql2).getSingleResult();
			Integer anhos = 0;
			Integer meses = 0;
			Integer dias = 0;
			Integer diferencia = 0;
			Integer diferencia2 = 0;
			if (cantdias != null)
				diferencia = new Integer(cantdias.toString());
			if (cantdias2 != null)
				diferencia2 = new Integer(cantdias2.toString());
			dias = diferencia + diferencia2;
			while (dias >= 365) {
				anhos = dias / 365;
				dias -= (365 * anhos);
				
			} 
			while (dias >= 30) {
				meses = dias / 30;
				dias -= (30 * meses);
		
			}
			antiguedad = "";
			if (anhos == 1)
				antiguedad += anhos +" año ";
			else 
				antiguedad += anhos +" años ";
			
			if (meses == 1)
				antiguedad += meses +" mes ";
			else
				antiguedad += meses +" meses ";
			
			if (dias == 1)
				antiguedad += dias +" dia ";
			else 
				antiguedad += dias +" dias ";
	}

	@SuppressWarnings("unchecked")
	private void obtenerRecorridoLaboral() {
		String sql=" select oee,uo,puesto,fecha_inicio,fecha_fin,estado from general.empleado_puesto_651 r where r.id_persona=:idPersona order by fecha_fin desc ";
		List<Object> rsl = em.createNativeQuery(sql).setParameter("idPersona", idPersona).getResultList();
		listaRecorridoLaboral = new ArrayList<RecorridoLaboralDTO>();
		if (!rsl.isEmpty()) {
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				RecorridoLaboralDTO dto = new RecorridoLaboralDTO();
				if (record[0] != null)
					dto.setOee((String) record[0]);
				if (record[1] != null)
					dto.setUo((String) record[1]);
				if (record[2] != null)
					dto.setPuesto((String) record[2]);
				if (record[3] != null)
					dto.setFechaInicio((Date) record[3]);
				if (record[4] != null)
					dto.setFechaFin((Date) record[4]);
				if (record[5] != null)
					dto.setEstado((String) record[5]);

				listaRecorridoLaboral.add(dto);
			}
		}
	}

	/**
	 * Fin RECORRIDO LABORAL
	 */

	/**
	 * Seccion EVALUACION DESEMPEÑO
	 */

	private void obtenerEvalFuncionario() {
		listaEvalFuncionarios = new ArrayList<EvaluacionesFuncionario>();
		Query q = em.createQuery("select e from EvaluacionesFuncionario e "
				+ "  where e.persona.idPersona = " + persona.getIdPersona());
		listaEvalFuncionarios = q.getResultList();
	}

	/**
	 * Fin EVALUACION DESEMPEÑO
	 * 
	 */

	/**
	 * Seccion PROCESOS JURIDICOS
	 */

	private void obtenerIncontitucionalidad() {
		listaInconstitucionalidadDTO = new ArrayList<InconstitucionalidadDTO>();
		String sql = "SELECT CASE WHEN AC.ESTADO = 'P' THEN 'PENDIENTE CON MEDIDA CAUTELAR' "
				+ "WHEN AC.ESTADO = 'A' THEN 'ACUERDO Y SENTENCIA' END AS ESTADO, "
				+ "JURIDICOS.FNC_GET_LEYES(AC.ID_ACCION_CAB) AS LEYES_ARTICULOS, "
				+ "DATE(AC.FECHA_ALTA) as fecha, AC.RESULTADO as resultado, ac.observacion as observacion "
				+ "FROM JURIDICOS.ACCION_INCONST_CAB AC "
				+ "JOIN GENERAL.PERSONA ON AC.ID_PERSONA = PERSONA.ID_PERSONA "
				+ "WHERE 	AC.ID_PERSONA = "
				+ idPersona
				+ " ORDER BY AC.FECHA_ALTA";
		List<Object> rsl = em.createNativeQuery(sql).getResultList();

		if (!rsl.isEmpty()) {
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				InconstitucionalidadDTO dto = new InconstitucionalidadDTO();
				if (record[0] != null)
					dto.setEstado((String) record[0]);
				if (record[1] != null)
					dto.setLey((String) record[1]);
				if (record[2] != null)
					dto.setFecha((Date) record[2]);
				if (record[3] != null)
					dto.setAcuerdo((String) record[3]);
				if (record[4] != null)
					dto.setObservacion((String) record[4]);
				listaInconstitucionalidadDTO.add(dto);
			}
		}
	}

	private void obtenerSumarios() {
		listaSumariosDTO = new ArrayList<SumarioDTO>();
		String sql = "SELECT OEE.DENOMINACION_UNIDAD OEE, DATE(SC.FECHA_ALTA) FECHA, "
				+ "SC.NRO_EXP || '/' ||SC.ANIO_EXP || ' ESTADO: ' || "
				+ "CASE WHEN SC.ESTADO = 'EC' THEN 'EN CURSO' "
				+ "WHEN SC.ESTADO = 'RJ' THEN 'CON RESOLUCION DEL JUEZ' "
				+ "WHEN SC.ESTADO = 'SO' THEN 'SOBRESEIMIENTO' WHEN SC.ESTADO = 'SA' THEN 'SANCION'  "
				+ "WHEN SC.ESTADO = 'AR' THEN 'ARCHIVO' END AS NRO_ESTADO_SUMARIO, "
				+ "JURIDICOS.FNC_GET_SUMARIOS(SC.ID_SUMARIO_CAB ) FALTAS, "
				+ "CASE WHEN SC.ESTADO_JUEZ = 'SO' THEN 'SOBRESEIMIENTO' "
				+ "WHEN SC.ESTADO_JUEZ = 'SA' THEN 'SANCION' ||' - '|| S.DESCRIPCION ||' - '|| "
				+ "SC.TIEMPO_J ||' '|| CASE WHEN SC.EXPRESADO_J = 'D' THEN 'DÍA/S' WHEN SC.EXPRESADO_J = 'M' "
				+ "THEN 'MES/ES' WHEN SC.EXPRESADO_J = 'A' THEN 'AÑO/S' END "
				+ "WHEN SC.ESTADO_JUEZ = 'AR' THEN 'ARCHIVO' END ||' - OBS.: '|| SC.OBS_J AS OBS_JUEZ, "
				+ "CASE WHEN SC.ESTADO = 'SO' THEN 'SOBRESEIMIENTO - MAI' "
				+ "WHEN SC.ESTADO = 'SA' THEN 'SANCION MAI - ' || SD.DESCRIPCION ||' '|| "
				+ "CASE WHEN SD.INHABILITA = TRUE THEN SC.TIEMPO_M ||' '|| CASE WHEN SC.EXPRESADO_M = 'D' "
				+ "THEN 'DÍA/S' WHEN SC.EXPRESADO_M = 'M' THEN 'MES/ES' WHEN SC.EXPRESADO_M = 'A' THEN 'AÑO/S' "
				+ "END ELSE  SD.DESCRIPCION END WHEN SC.ESTADO = 'AR' THEN 'ARCHIVO - MAI' END AS DECISION_MAI, "
				+ "SC.OBS_M AS OBS_MAI FROM JURIDICOS.SUMARIO_CAB SC "
				+ "LEFT JOIN JURIDICOS.SANCION S ON S.ID_SANCION = SC.ID_SANCION_J "
				+ "LEFT JOIN JURIDICOS.SANCION SD ON SD.ID_SANCION = SC.ID_SANCION_M "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON OEE.ID_CONFIGURACION_UO = SC.ID_CONFIGURACION_UO "
				+ "WHERE SC.ID_PERSONA = "
				+ idPersona
				+ " ORDER BY SC.FECHA_ALTA";
		List<Object> rsl = em.createNativeQuery(sql).getResultList();

		if (!rsl.isEmpty()) {
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				SumarioDTO dto = new SumarioDTO();
				if (record[0] != null)
					dto.setOee((String) record[0]);
				if (record[1] != null)
					dto.setFecha((Date) record[1]);
				if (record[2] != null)
					dto.setNroInterno((String) record[2]);
				if (record[3] != null)
					dto.setFaltas((String) record[3]);
				if (record[4] != null)
					dto.setRecomendacionJuez((String) record[4]);
				if (record[5] != null)
					dto.setDecisionMai((String) record[5]);
				if (record[6] != null)
					dto.setObsMai((String) record[6]);
				listaSumariosDTO.add(dto);
			}
		}
	}

	private void obtenerExcepcionados() {
		listaExcepcionados = new ArrayList<Excepcionados>();
		Query q = em.createQuery("select e from Excepcionados e "
				+ "  where e.activo is true and e.persona.idPersona = "
				+ persona.getIdPersona());
		listaExcepcionados = q.getResultList();
	}

	/**
	 * Fin PROCESOS JURIDICOS
	 * 
	 */

	/**
	 * Seccion DESVINCULACIONES
	 */
	private void obtenerDesvinculaciones() {
		listaDesvinculaciones = new ArrayList<Desvinculacion>();
		Query q = em.createQuery("select d from Desvinculacion d "
				+ "  where d.empleadoPuesto.persona.idPersona = "
				+ persona.getIdPersona());
		listaDesvinculaciones = q.getResultList();
	}

	private void obtenerInhabilitados() {
		listaInhabilitados = new ArrayList<Inhabilitados>();
		Query q = em.createQuery("select d from Inhabilitados d "
				+ "  where d.persona.idPersona = " + persona.getIdPersona());
		listaInhabilitados = q.getResultList();
	}

	private void obtenerJubilados() {
		listaJubilados = new ArrayList<Jubilados>();
		Query q = em.createQuery("select d from Jubilados d "
				+ "  where d.persona.idPersona = " + persona.getIdPersona());
		listaJubilados = q.getResultList();
	}

	/**
	 * Fin DESVINCULACIONES
	 * 
	 */

	/**
	 * Seccion VACACIONES
	 */

	private void obtenerVacaciones() {
		listaVacaciones = new ArrayList<DatosVacaciones>();
		Query q = em.createQuery("select v from DatosVacaciones v "
				+ "  where v.activo is true and v.persona.idPersona = "
				+ persona.getIdPersona());
		listaVacaciones = q.getResultList();
	}

	/**
	 * Fin VACACIONES
	 * 
	 */

	/**
	 * Seccion PERMISOS
	 */
	private void obtenerPermisos() {
		listaPermisos = new ArrayList<DatosPermiso>();
		Query q = em.createQuery("select p from DatosPermiso p "
				+ "  where p.activo is true and p.persona.idPersona = "
				+ persona.getIdPersona());
		listaPermisos = q.getResultList();
	}

	/**
	 * Fin PERMISOS
	 * 
	 */

	/**
	 * Seccion PREMIOS
	 */
	private void obtenerPremios() {
		listaPremios = new ArrayList<DatosPremio>();
		Query q = em.createQuery("select p from DatosPremio p "
				+ "  where p.activo is true and p.persona.idPersona = "
				+ persona.getIdPersona());
		listaPremios = q.getResultList();
	}

	/**
	 * Fin PREMIOS
	 * 
	 */

	/**
	 * Seccion AMONESTACIONES
	 */
	private void obtenerAmonestaciones() {
		listaAmonestaciones = new ArrayList<DatosAmonestacion>();
		Query q = em.createQuery("select p from DatosAmonestacion p "
				+ "  where p.activo is true and p.persona.idPersona = "
				+ persona.getIdPersona());
		listaAmonestaciones = q.getResultList();
	}

	/**
	 * Fin AMONESTACIONES
	 * 
	 */

	/**
	 * Seccion PENDIENTES DE VALIDACION
	 */
	private void obtenerObservaciones() {
		listaLegajoObs = new ArrayList<AuditLegajoObs>();

		String query = "SELECT ao.* "
				+ "FROM legajo.audit_legajo_obs ao "
				+ "join legajo.audit_legajo a on a.audit_legajo = ao.audit_legajo "
				+ "WHERE a.estado = 2 and trim(ao.observacion) <> '' AND a.ID_PERSONA = "
				+ idPersona;
		listaLegajoObs = em.createNativeQuery(query, AuditLegajoObs.class)
				.getResultList();
		observacionesPendientes = "";
		if (!listaLegajoObs.isEmpty()) {
			for (AuditLegajoObs obj : listaLegajoObs) {
				if (obj.getFicha().intValue() == 1)
					observacionesPendientes += "Datos Personales: ";
				else
					observacionesPendientes += "Datos Familiares: ";

				observacionesPendientes += obj.getObservacion() + ". ";
			}
		}
	}

	/**
	 * Fin PENDIENTES DE VALIDACION
	 * 
	 */

	/**
	 * Imprimir Reporte
	 */

	public void pdf() {
		try {

			Connection conn = JpaResourceBean.getConnection();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("id_persona", idPersona);
			param.put("nombre_completo", persona.getNombreCompleto());
			param.put(
					"grupo_sangre",
					persona.getGrupoSanguineoAbo()
							+ persona.getGrupoSanguineoRh());
			param.put("antiguedad", antiguedad);
			if (apoyos != null && apoyos.getInstRehab()!= null && apoyos.getInstRehab())
				param.put("reh_instituto", "SI");
			else
				param.put("reh_instituto", "NO");
			if (apoyos != null && apoyos.getCentroSal()!= null && apoyos.getCentroSal())
				param.put("reh_hospital", "SI");
			else
				param.put("reh_hospital", "NO");
			if (apoyos != null && apoyos.getEntrenamiento() != null && apoyos.getEntrenamiento())
				param.put("entrenamiento", "SI");
			else
				param.put("entrenamiento", "NO");

			if (apoyos != null && apoyos.getParticular() != null && apoyos.getParticular())
				param.put("reh_particular", "SI");
			else
				param.put("reh_particular", "NO");

			if (apoyos != null && apoyos.getNinguno() != null && apoyos.getNinguno())
				param.put("ninguno", "SI");
			else
				param.put("ninguno", "NO");
			if (apoyos != null && apoyos.getOtroApoyo() != null && apoyos.getOtroApoyo()) {
				param.put("otro", "SI");
				param.put("descripcion", apoyos.getComentario());
			} else
				param.put("otro", "NO");

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("REPORT_CONNECTION", conn);
			if (mostrarObservacion){
				param.put("mostrarObs", "SI");
				param.put("observaciones", observacionesPendientes);
			}
			else
				param.put("mostrarObs", "NO");

			JasperReportUtils.respondPDF("RPT_CU659_Curriculum_Legajo", param,
					false, conn, usuarioLogueado);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public String getTipoVinculacion(ExperienciaLaboralLegajo exp){
		/*System.out.println("exp"+exp);
		System.out.println("DatosEspecificosTipoVinculo"+exp.getDatosEspecificosTipoVinculo());
		System.out.println("descripcion"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
		if(exp != null && exp.getDatosEspecificosTipoVinculo()	!= null
				&& exp.getDatosEspecificosTipoVinculo().getDescripcion() != null){
			/*System.out.println("exp2"+exp);
			System.out.println("DatosEspecificosTipoVinculo2"+exp.getDatosEspecificosTipoVinculo());
			System.out.println("descripcion2"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
			if( exp.getDatosEspecificosTipoVinculo().getDescripcion().equals("OTROS"))
					return exp.getOtroVincu();
			else 
				return exp.getDatosEspecificosTipoVinculo().getDescripcion();
		}else
			return "";
	}
		
	
	public String getSector(ExperienciaLaboralLegajo exp){
		/*System.out.println("exp"+exp);
		System.out.println("DatosEspecificosTipoVinculo"+exp.getDatosEspecificosTipoVinculo());
		System.out.println("descripcion"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
		if(exp != null && exp.getDatosEspecificosSector() != null
				&& exp.getDatosEspecificosSector().getDescripcion() != null){
			/*System.out.println("exp2"+exp);
			System.out.println("DatosEspecificosTipoVinculo2"+exp.getDatosEspecificosTipoVinculo());
			System.out.println("descripcion2"+exp.getDatosEspecificosTipoVinculo().getDescripcion());*/
			if( exp.getDatosEspecificosSector().getDescripcion().equals("OTROS"))
					return exp.getOtroSector();
			else 
				return exp.getDatosEspecificosSector().getDescripcion();
		}else
			return "";
	}
	
	
	
	
	public void xls() {
		try {

			Connection conn = JpaResourceBean.getConnection();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("id_persona", idPersona);
			param.put("nombre_completo", persona.getNombreCompleto());
			if (apoyos != null && apoyos.getInstRehab() != null && apoyos.getInstRehab())
				param.put("reh_instituto", "SI");
			else
				param.put("reh_instituto", "NO");
			if (apoyos != null && apoyos.getCentroSal() != null && apoyos.getCentroSal())
				param.put("reh_hospital", "SI");
			else
				param.put("reh_hospital", "NO");
			if (apoyos != null && apoyos.getEntrenamiento() != null && apoyos.getEntrenamiento())
				param.put("entrenamiento", "SI");
			else
				param.put("entrenamiento", "NO");

			if (apoyos != null && apoyos.getParticular() != null && apoyos.getParticular())
				param.put("reh_particular", "SI");
			else
				param.put("reh_particular", "NO");

			if (apoyos != null && apoyos.getNinguno() != null && apoyos.getNinguno())
				param.put("ninguno", "SI");
			else
				param.put("ninguno", "NO");
			if (apoyos != null && apoyos.getOtroApoyo() != null && apoyos.getOtroApoyo()) {
				param.put("otro", "SI");
				param.put("descripcion", apoyos.getComentario());
			} else
				param.put("otro", "NO");

			param.put(
					"grupo_sangre",
					persona.getGrupoSanguineoAbo()
							+ persona.getGrupoSanguineoRh());
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("REPORT_CONNECTION", conn);

			JasperReportUtils.respondXLS("RPT_CU659_Curriculum_Legajo", param,
					conn, usuarioLogueado);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Legajos getLegajo() {
		return legajo;
	}

	public void setLegajo(Legajos legajo) {
		this.legajo = legajo;
	}

	public List<DocumentosPersona> getListaDocAdicionales() {
		return listaDocAdicionales;
	}

	public void setListaDocAdicionales(
			List<DocumentosPersona> listaDocAdicionales) {
		this.listaDocAdicionales = listaDocAdicionales;
	}

	public List<DatosEnfermedad> getListaEnfermedades() {
		return listaEnfermedades;
	}

	public void setListaEnfermedades(List<DatosEnfermedad> listaEnfermedades) {
		this.listaEnfermedades = listaEnfermedades;
	}

	public List<DatosLicenciaMedica> getListaLicencias() {
		return listaLicencias;
	}

	public void setListaLicencias(List<DatosLicenciaMedica> listaLicencias) {
		this.listaLicencias = listaLicencias;
	}

	public List<DatosSeguroMedico> getListaSeguros() {
		return listaSeguros;
	}

	public void setListaSeguros(List<DatosSeguroMedico> listaSeguros) {
		this.listaSeguros = listaSeguros;
	}

	public Boolean getRehInstituto() {
		return rehInstituto;
	}

	public void setRehInstituto(Boolean rehInstituto) {
		this.rehInstituto = rehInstituto;
	}

	public Boolean getRehHospital() {
		return rehHospital;
	}

	public void setRehHospital(Boolean rehHospital) {
		this.rehHospital = rehHospital;
	}

	public Boolean getRehParticular() {
		return rehParticular;
	}

	public void setRehParticular(Boolean rehParticular) {
		this.rehParticular = rehParticular;
	}

	public Boolean getRehEntrenamiento() {
		return rehEntrenamiento;
	}

	public void setRehEntrenamiento(Boolean rehEntrenamiento) {
		this.rehEntrenamiento = rehEntrenamiento;
	}

	public Boolean getNinguno() {
		return ninguno;
	}

	public void setNinguno(Boolean ninguno) {
		this.ninguno = ninguno;
	}

	public Boolean getOtro() {
		return otro;
	}

	public void setOtro(Boolean otro) {
		this.otro = otro;
	}

	public DiscapacidadApoyos getApoyos() {
		return apoyos;
	}

	public void setApoyos(DiscapacidadApoyos apoyos) {
		this.apoyos = apoyos;
	}

	public List<PersonaDiscapacidadLegajo> getListaDiscapacidad() {
		return listaDiscapacidad;
	}

	public void setListaDiscapacidad(List<PersonaDiscapacidadLegajo> listaDiscapacidad) {
		this.listaDiscapacidad = listaDiscapacidad;
	}

	public List<ReprPersonaDiscapacidadLegajo> getListaRepreDiscapacidad() {
		return listaRepreDiscapacidad;
	}

	public void setListaRepreDiscapacidad(
			List<ReprPersonaDiscapacidadLegajo> listaRepreDiscapacidad) {
		this.listaRepreDiscapacidad = listaRepreDiscapacidad;
	}

	public List<Familiares> getListaFamiliares() {
		return listaFamiliares;
	}

	public void setListaFamiliares(List<Familiares> listaFamiliares) {
		this.listaFamiliares = listaFamiliares;
	}

	public List<EstudiosRealizadosLegajo> getListaEstudiosRealizados() {
		return listaEstudiosRealizados;
	}

	public void setListaEstudiosRealizados(
			List<EstudiosRealizadosLegajo> listaEstudiosRealizados) {
		this.listaEstudiosRealizados = listaEstudiosRealizados;
	}

	public List<IdiomasLegajoPersona> getListaIdiomas() {
		return listaIdiomas;
	}

	public void setListaIdiomas(List<IdiomasLegajoPersona> listaIdiomas) {
		this.listaIdiomas = listaIdiomas;
	}

	public List<CapacitacionesDTO> getListaCapacitacionesDTO() {
		return listaCapacitacionesDTO;
	}

	public void setListaCapacitacionesDTO(
			List<CapacitacionesDTO> listaCapacitacionesDTO) {
		this.listaCapacitacionesDTO = listaCapacitacionesDTO;
	}

	public List<DeclaracionJurada> getListaDeclaracionJurada() {
		return listaDeclaracionJurada;
	}

	public void setListaDeclaracionJurada(
			List<DeclaracionJurada> listaDeclaracionJurada) {
		this.listaDeclaracionJurada = listaDeclaracionJurada;
	}

	public List<ExperienciaLaboralLegajo> getListaExpLaboral() {
		return listaExpLaboral;
	}

	public void setListaExpLaboral(List<ExperienciaLaboralLegajo> listaExpLaboral) {
		this.listaExpLaboral = listaExpLaboral;
	}

	public List<EvaluacionesFuncionario> getListaEvalFuncionarios() {
		return listaEvalFuncionarios;
	}

	public void setListaEvalFuncionarios(
			List<EvaluacionesFuncionario> listaEvalFuncionarios) {
		this.listaEvalFuncionarios = listaEvalFuncionarios;
	}

	public List<InconstitucionalidadDTO> getListaInconstitucionalidadDTO() {
		return listaInconstitucionalidadDTO;
	}

	public void setListaInconstitucionalidadDTO(
			List<InconstitucionalidadDTO> listaInconstitucionalidadDTO) {
		this.listaInconstitucionalidadDTO = listaInconstitucionalidadDTO;
	}

	public List<SumarioDTO> getListaSumariosDTO() {
		return listaSumariosDTO;
	}

	public void setListaSumariosDTO(List<SumarioDTO> listaSumariosDTO) {
		this.listaSumariosDTO = listaSumariosDTO;
	}

	public List<Excepcionados> getListaExcepcionados() {
		return listaExcepcionados;
	}

	public void setListaExcepcionados(List<Excepcionados> listaExcepcionados) {
		this.listaExcepcionados = listaExcepcionados;
	}

	public List<Desvinculacion> getListaDesvinculaciones() {
		return listaDesvinculaciones;
	}

	public void setListaDesvinculaciones(
			List<Desvinculacion> listaDesvinculaciones) {
		this.listaDesvinculaciones = listaDesvinculaciones;
	}

	public List<Inhabilitados> getListaInhabilitados() {
		return listaInhabilitados;
	}

	public void setListaInhabilitados(List<Inhabilitados> listaInhabilitados) {
		this.listaInhabilitados = listaInhabilitados;
	}

	public List<Jubilados> getListaJubilados() {
		return listaJubilados;
	}

	public void setListaJubilados(List<Jubilados> listaJubilados) {
		this.listaJubilados = listaJubilados;
	}

	public List<DatosVacaciones> getListaVacaciones() {
		return listaVacaciones;
	}

	public void setListaVacaciones(List<DatosVacaciones> listaVacaciones) {
		this.listaVacaciones = listaVacaciones;
	}

	public List<DatosPermiso> getListaPermisos() {
		return listaPermisos;
	}

	public void setListaPermisos(List<DatosPermiso> listaPermisos) {
		this.listaPermisos = listaPermisos;
	}

	public List<DatosPremio> getListaPremios() {
		return listaPremios;
	}

	public void setListaPremios(List<DatosPremio> listaPremios) {
		this.listaPremios = listaPremios;
	}

	public List<DatosAmonestacion> getListaAmonestaciones() {
		return listaAmonestaciones;
	}

	public void setListaAmonestaciones(
			List<DatosAmonestacion> listaAmonestaciones) {
		this.listaAmonestaciones = listaAmonestaciones;
	}

	public String getAntiguedad() {
		return antiguedad;
	}

	public void setAntiguedad(String antiguedad) {
		this.antiguedad = antiguedad;
	}

	public List<RecorridoLaboralDTO> getListaRecorridoLaboral() {
		return listaRecorridoLaboral;
	}

	public void setListaRecorridoLaboral(
			List<RecorridoLaboralDTO> listaRecorridoLaboral) {
		this.listaRecorridoLaboral = listaRecorridoLaboral;
	}

	public List<AuditLegajoObs> getListaLegajoObs() {
		return listaLegajoObs;
	}

	public void setListaLegajoObs(List<AuditLegajoObs> listaLegajoObs) {
		this.listaLegajoObs = listaLegajoObs;
	}

	public Boolean getMostrarObservacion() {
		return mostrarObservacion;
	}

	public void setMostrarObservacion(Boolean mostrarObservacion) {
		this.mostrarObservacion = mostrarObservacion;
	}

	public Boolean getMostrarBtnImprimir() {
		return mostrarBtnImprimir;
	}

	public void setMostrarBtnImprimir(Boolean mostrarBtnImprimir) {
		this.mostrarBtnImprimir = mostrarBtnImprimir;
	}

	public String getObservacionesPendientes() {
		return observacionesPendientes;
	}

	public void setObservacionesPendientes(String observacionesPendientes) {
		this.observacionesPendientes = observacionesPendientes;
	}

	public List<Documentos> getListaDocAdjuntos() {
		return listaDocAdjuntos;
	}

	public void setListaDocAdjuntos(List<Documentos> listaDocAdjuntos) {
		this.listaDocAdjuntos = listaDocAdjuntos;
	}

	public List<EstudiosRealizadosLegajo> getListaOtrosEstudiosRealizados() {
		return listaOtrosEstudiosRealizados;
	}

	public void setListaOtrosEstudiosRealizados(
			List<EstudiosRealizadosLegajo> listaOtrosEstudiosRealizados) {
		this.listaOtrosEstudiosRealizados = listaOtrosEstudiosRealizados;
	}
	
	public String descInstitucionEducativa(EstudiosRealizadosLegajo estudio){
		if(estudio.getInstitucionEducativa().getDescripcion().equalsIgnoreCase("OTRAS"))
			return estudio.getOtraInstit();
		else
			return estudio.getInstitucionEducativa().getDescripcion();
	}

}
