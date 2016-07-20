package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.ParseException;	
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.Naming;

import py.com.excelsis.sicca.entity.AuditLegajoObs;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.DiscapacidadApoyos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.EstudiosRealizadosLegajo;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.ExperienciaLaboralLegajo;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.IdiomasLegajoPersona;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigGrupos;
import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaDiscapacidadLegajo;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidadLegajo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.PersonaPostulanteHome;
import py.com.excelsis.sicca.seleccion.session.PostulacionHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.form.Tab6VistaPreliminarFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("registrarPostulacionFormController")
public class RegistrarPostulacionFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	PersonaPostulanteHome personaPostulanteHome;
	@In(create = true)
	PostulacionHome postulacionHome;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(required = false, create = true)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private PersonaPostulante personaPostulante;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Concurso concurso;
	private Persona persona;
	private Postulacion postulacion;
	private String codConcurso;
	private String interesDescripcion = "";
	private List<EstudiosRealizados> listaEstudiosRealizados;
	private List<EstudiosRealizadosLegajo> listaEstudiosRealizadosLegajo;
	private List<IdiomasPersona> listaIdiomas;
	private List<IdiomasLegajoPersona> listaIdiomasLegajo;
	private List<ExperienciaLaboral> listaExperienciaLaboral;
	private List<ExperienciaLaboralLegajo> listaExperienciaLaboralLegajo;
	private List<ReprPersonaDiscapacidad> listaReprPersonaDiscapacidad;
	private List<ReprPersonaDiscapacidadLegajo> listaReprPersonaDiscapacidadLegajo;
	private List<Documentos> listaDocumentos;
	private List<Documentos> listaDocumentosInsertados;
	List<PersonaDiscapacidad> listaPersonasDiscapacidad;
	List<PersonaDiscapacidadLegajo> listaPersonasDiscapacidadLegajo;
	private List<String> listaDocsNoPresentados;
	private String msjError = "";
	private String mensajeModal;
	private Boolean mostrarOkyCancelar = false;
	private String dirPostulacion = "/seleccion/registrarPostulacion/PostulacionEdit.seam";
	private Boolean fallo = false;
	private Long idGrupoAcctionListener;
	private Boolean permitirPostular;
	private Boolean faltanDocs = false;
	private Boolean faltanDocsAgrupados = false;
	private List<String> docsFaltantesList = new ArrayList<String>();
	private List<String> docsAgrupadosFaltantesList = new ArrayList<String>();
	private List<String> docsNoObligatoriosFaltantesList = new ArrayList<String>();
	private List<String> docsPresentados = new ArrayList<String>();
	private List<String> docsSolicitados = new ArrayList<String>();
	private List<String> docsSolicitadosNoObligatorios = new ArrayList<String>();
	private List<String> docsSolicitadosAgrupados = new ArrayList<String>();
	private Boolean faltanDocsNoObligatorios = false;
	private Boolean faltaEvaluacionDesempenho = false;
	private Boolean necesitaEvaluacionDesempenho = false;
	private static final String PRE = locate();
	private boolean valido = true;
	private boolean tieneParientes=true;
	private Boolean estaEnRangoFechaPostu=false;
	private Date fechaPostulacionDesde;
	private Date fechaPostulacionHasta;
	/**
	 * Método que inicializa los datos
	 * @throws Exception 
	 */

	public String init() throws Exception {
		limpiar();
		obtenerDatosPantalla1();
		docsObligatoriosFaltantes();
		docsNoObligatorios();
		docsPresentados();
		docsSolicitadosObligatorios();
		docsSolicitadosNoObligatorios();
		docsSolicitadosAgrupados();
		tienePariente();
		// docsFaltantePrevioConfirmar();
		setValido(true);
		if (!validar()) {
			setValido(false);
			return "FALLO";
		}

		if (!precondiciones()) {
			setValido(false);
			return "FALLO";
		}
		
		
		return "ok";
	}
	
	public void tieneEvaluacionDesempenho(){
		
		if(concurso.getDatosEspecificosTipoConc().getDescripcion().equals("CONCURSO INTERNO DE PROMOCION SALARIAL"))
		{	necesitaEvaluacionDesempenho = true;
		
			String sql = "select * from general.persona persona 	"
					+ "	join general.empleado_puesto empleado_puesto	"
					+ "	on empleado_puesto.id_persona = persona.id_persona	"
					+ "	and empleado_puesto.actual = true "
					+ "	and id_empleado_puesto in "
					+ "	( "
					+ "		select distinct id_empleado_puesto from eval_desempeno.sujetos suj	"
					+ "		join eval_desempeno.evaluacion_desempeno eval on eval.id_evaluacion_desempeno = suj.id_evaluacion_desempeno"
					+ "		and eval.id_configuracion_uo_cab = "+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
							//SE PODRAN AGREGAR MAS FILTROS PARA LAS FECHAS DE LAS EVALUACIONES Y LOS PUNTAJES OBTENIDOS.. 
					+ "	) "
					+ " where persona.id_persona = "+persona.getIdPersona();
			
			List<Persona> listaPersona = em.createNativeQuery(sql, Persona.class).getResultList();
			
			if(listaPersona.size() <= 0)
				faltaEvaluacionDesempenho = true;
		}
		
	}

	public void tienePariente() {
		if (persona.getTienePariente() != null && persona.getTienePariente()) {
			List<Parentesco> listDetails = new ArrayList<Parentesco>();
			List<Parentesco> listDetails2 = new ArrayList<Parentesco>();

			listDetails = em
					.createQuery(
							" select e from Parentesco e "
									+ " where e.persona.idPersona= "
									+ persona.getIdPersona()
									+ " and e.activo = 'true' and e.datosEspecificos.valorAlf like 'C'")
					.getResultList();

			listDetails2 = em
					.createQuery(
							" select e from Parentesco e "
									+ " where e.persona.idPersona= "
									+ persona.getIdPersona()
									+ "and e.activo = 'true'  and e.datosEspecificos.valorAlf like 'A'")
					.getResultList();

			if (listDetails.size() == 0 && listDetails2.size() == 0) {
				tieneParientes = false;
				faltanDocs = false;
				faltanDocsNoObligatorios = false;
			}
		}
	}

	public void continuar() {
		faltanDocsNoObligatorios = false;
	}

	public String obtenerMensaje() {
		DatosEspecificos dato = seleccionUtilFormController
				.traerDatosEspecificosConDG("DESCRIPCION DE POSTULACION");
		if (dato == null)
			return "";

		return dato.getDescripcion();
	}

	public Boolean verifCompletoFichaDisca(Long idPersona) {
		Query q = em
				.createQuery("select PersonaDiscapacidad from PersonaDiscapacidad PersonaDiscapacidad "
						+ "where PersonaDiscapacidad.persona.idPersona = "
						+ idPersona + " and PersonaDiscapacidad.activo is true");
		List<PersonaDiscapacidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public String controlesPcd(Long idGrupo) {
		if (permitirPostular == null || !permitirPostular) {
			return null;
		} else if (permitirPostular) {
			ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class,
					idGrupoAcctionListener);
			if (grupo.getConcurso().getDatosEspecificosTipoConc()
					.getDescripcion()
					.equalsIgnoreCase("CONCURSO PUBLICO DE OPOSICION")
					|| grupo.getConcurso().getDatosEspecificosTipoConc()
							.getDescripcion()
							.equalsIgnoreCase("CONCURSO DE MERITOS")) {
				dirPostulacion = "/seleccion/registrarPostulacion/PostulacionEdit.seam?concursoPuestoAgrIdConcursoPuestoAgr= "
						+ idGrupoAcctionListener;
			} else if (grupo
					.getConcurso()
					.getDatosEspecificosTipoConc()
					.getDescripcion()
					.equalsIgnoreCase(
							"CONCURSO INTERNO DE OPOSICION INSTITUCIONAL")
					|| grupo.getConcurso()
							.getDatosEspecificosTipoConc()
							.getDescripcion()
							.equalsIgnoreCase(
									"CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL")
					|| grupo.getConcurso()
							.getDatosEspecificosTipoConc()
							.getDescripcion()
							.equalsIgnoreCase(
									"CONCURSO INTERNO DE PROMOCION SALARIAL")				
					) {
				dirPostulacion = "/seleccion/registrarPostulacion/CIO/PostulacionEdit.seam?concursoPuestoAgrIdConcursoPuestoAgr= "
						+ idGrupoAcctionListener;
			} else if (grupo.getConcurso().getDatosEspecificosTipoConc()
					.getDescripcion().equalsIgnoreCase("CONCURSO SIMPLIFICADO")) {
				dirPostulacion = "/seleccion/registrarPostulacion/CSI/PostulacionEdit.seam?concursoPuestoAgrIdConcursoPuestoAgr= "
						+ idGrupoAcctionListener;
			}
		}
		return dirPostulacion;
	}

	public void permitirPostular(Long idGrupo) throws Exception {
		if (idGrupo == null)
			return;

		Boolean completoFicha = verifCompletoFichaDisca(usuarioLogueado
				.getPersona().getIdPersona());
		ConcursoPuestoAgr entity = em.find(ConcursoPuestoAgr.class, idGrupo);
		if (entity.getConcurso().getPcd() != null
				&& entity.getConcurso().getPcd()) {
			if (completoFicha) {
				/*
				 * El Postulante ha completado su ‘Ficha de Discapacidad’.
				 * Permitir POSTULAR
				 */
				permitirPostular = true;
			} else {
				/* no permitir POSTULAR */
				mensajeModal = "El concurso para el que está intentando postular es sólo para personas con discapacidad. </br>Debe llenar su ficha de discapacidades";
				mostrarOkyCancelar = false;
				permitirPostular = null;
			}
		} else {
			Query q = em
					.createQuery("select DatosGrupoPuesto from DatosGrupoPuesto DatosGrupoPuesto "
							+ "where DatosGrupoPuesto.activo is true and DatosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ idGrupo);
			List<DatosGrupoPuesto> lista = q.getResultList();
			if (lista.size() == 1
					&& lista.get(0).getPreferenciaPersDiscapacidad()) {
				if (completoFicha) {
					/*
					 * El Postulante ha completado su ‘Ficha de Discapacidad’.
					 * Permitir POSTULAR
					 */
					permitirPostular = true;
				} else {
					/* Preguntar si desea POSTULAR - Mostrar ok y cancelar */
					mensajeModal = "El Grupo para el que está intentando postular tiene preferencia para las Personas con Discapacidad. <br/>"
							+ "¿Desea postular sin haber llenado su Ficha?";
					mostrarOkyCancelar = true;
					permitirPostular = null;
				}
			} else {
				permitirPostular = true;
				if(entity.getConcurso().getInstitucional() != null && entity.getConcurso().getInstitucional()){
					
					if(esFuncionario(entity.getConcurso(),usuarioLogueado))
						permitirPostular = true;
					else
						permitirPostular = false;
				}
				
				if(entity.getConcurso().getPromocion() != null && entity.getConcurso().getPromocion()){
					
					if(esFuncionario(entity.getConcurso(),usuarioLogueado))
						permitirPostular = true;
					else
						permitirPostular = false;
				}	
				
				if(entity.getConcurso().getInterinstitucional() != null && entity.getConcurso().getInterinstitucional()){
					
					if(esFuncionario(entity.getConcurso(),usuarioLogueado))
						permitirPostular = true;
					else
						permitirPostular = false;
				}
				
//				else
//					permitirPostular = true;
				
				
				
				
			}
		}

	}
	
	
	public Boolean esFuncionario (Concurso concurso, Usuario usuario) throws Exception{
		Boolean retorno = false;
		
		String sql = "select  uo_cab.* from planificacion.configuracion_uo_cab uo_cab "
				+ " join planificacion.configuracion_uo_det uo_det on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo"
				+ " join planificacion.planta_cargo_det cargo on uo_det.id_configuracion_uo_det = cargo.id_configuracion_uo_det"
				+ " join general.empleado_puesto emp_puesto on emp_puesto.id_planta_cargo_det = cargo.id_planta_cargo_det "
				+ "	and emp_puesto.fecha_fin is null and emp_puesto.actual = true"
				+ " join general.persona per on per.id_persona = emp_puesto.id_persona and per.id_persona = "+usuario.getPersona().getIdPersona();
		
		List <ConfiguracionUoCab> list = em.createNativeQuery(sql, ConfiguracionUoCab.class).getResultList();
		if(list.size() == 0)
			retorno = false;
		else{
			if(list.get(0) != null){
				ConfiguracionUoCab uoCab = list.get(0);
				
				if(concurso.getInstitucional()){
					if(uoCab.getIdConfiguracionUo().equals(concurso.getConfiguracionUoCab().getIdConfiguracionUo()))
						retorno = true;
										
				}if(concurso.getPromocion()){
					if(uoCab.getIdConfiguracionUo().equals(concurso.getConfiguracionUoCab().getIdConfiguracionUo()))
						retorno = true;
										
				}
				else if (concurso.getInterinstitucional()){
					retorno = true;
				}
			}
				
		}
		
		
		return retorno;
	}

	public void limpiar() {
		listaEstudiosRealizados = null;
		listaIdiomas = null;
		listaExperienciaLaboral = null;
		listaReprPersonaDiscapacidad = null;
		listaDocumentos = null;
		listaDocumentosInsertados = null;
		listaPersonasDiscapacidad = null;
		msjError = "";
	}

	public boolean validar() {
		if (!estaEnRangoFecha()) { // Se volvió a agregar éste control. Valida que se encuentre en fecha de postulacion. AB											
			return false;
		}
		if (controlarInhabilitado()) {
			msjError = "Error";
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Usted se encuentra Inhabilitado. Para más Información, contactar con la Secretaría de la Función Pública");
			return false;
		} else if (controlarJubilado()) {
			msjError = "Error";
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Usted se encuentra Jubilado. Para más Información, contactar con la Secretaría de la Función Pública");
			return false;
		} else if (!controlarNacionalidad()) {
			msjError = "Error";
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU241_msg6"));
			return false;
		} else if (!esMayorDeEdad()) {
			msjError = "Error";
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU241_msg7"));
			return false;
		}
		// } else {
		//
		// statusMessages.clear();
		// statusMessages.add(Severity.ERROR,
		// SeamResourceBundle.getBundle().getString("CU241_msg1"));
		// return "fallo";
		// }

		return true;
	}

	public void obtenerDatosPantalla1() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,concursoPuestoAgr.getIdConcursoPuestoAgr());
		concurso = concursoPuestoAgr.getConcurso();
		Long idPersona = usuarioLogueado.getPersona().getIdPersona();
		persona = new Persona();
		persona = em.find(Persona.class, idPersona);
		codConcurso = concurso.getNumero() == null ? "-" : concurso.getNumero()
				.toString();
		codConcurso += "/";
		codConcurso += concurso.getAnhio() == null ? "-" : concurso.getAnhio();
		codConcurso += " de "
				+ concurso.getConfiguracionUoCab().getDescripcionCorta();

		listaEstudiosRealizados = new ArrayList<EstudiosRealizados>();
		listaIdiomas = new ArrayList<IdiomasPersona>();
		listaExperienciaLaboral = new ArrayList<ExperienciaLaboral>();
		listaPersonasDiscapacidad = new ArrayList<PersonaDiscapacidad>();
		listaDocumentos = new ArrayList<Documentos>();
		listaDocumentosInsertados = new ArrayList<Documentos>();
		listaReprPersonaDiscapacidad = new ArrayList<ReprPersonaDiscapacidad>();
		listaDocsNoPresentados = new ArrayList<String>();

	}

	/**
	 * Verifica que se cumpla los rangos de fechas
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Boolean estaEnRangoFecha() {
		String sql = "select fechas.*  "
				+ "from seleccion.fechas_grupo_puesto fechas  "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = fechas.id_concurso_puesto_agr  "
				+ "join seleccion.concurso concurso  "
				+ "on concurso.id_concurso = agr.id_concurso  "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		List<FechasGrupoPuesto> lista = new ArrayList<FechasGrupoPuesto>();
		lista = em.createNativeQuery(sql, FechasGrupoPuesto.class)
				.getResultList();
		Date fechaHoy = new Date();
		
		for (FechasGrupoPuesto f : lista) {
			/*fechaDesdeCompuesta y fechaHastaCompuesta serán desplegados en pantalla 
			  si se desea postular fuera del rango de fecha de postulacion.*/
			SimpleDateFormat sdfAux = new SimpleDateFormat("dd/MM/yyyy");
			String fechaDesde = sdfAux.format(f.getFechaRecepcionDesde());
			String fechaHasta = sdfAux.format(f.getFechaRecepcionHasta());
			
			sdfAux = new SimpleDateFormat("HH:mm");
			String HoraDesde = sdfAux.format(f.getHoraRecepcionDesde());
			String HoraHasta = sdfAux.format(f.getHoraRecepcionHasta());				
				
			sdfAux = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date fechaDesdeCompuesta = new Date();
			Date fechaHastaCompuesta = new Date();
			
			try {				
				fechaDesdeCompuesta = sdfAux.parse(fechaDesde + " " + HoraDesde);	
				fechaHastaCompuesta = sdfAux.parse(fechaHasta + " " + HoraHasta);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			// Verificación de que se encuentre en el rango de postulación.
			if (fechaHoy.compareTo(fechaDesdeCompuesta) < 0
					|| fechaHoy.compareTo(fechaHastaCompuesta) > 0) {
				setEstaEnRangoFechaPostu(false);				
				setFechaPostulacionDesde(fechaDesdeCompuesta);
				setFechaPostulacionHasta(fechaHastaCompuesta);							
				return false;
			} 
			
		}
		setEstaEnRangoFechaPostu(true);
		return true;
	}

	/**
	 * Verifica si la persona se encuentra inhabilitada
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean controlarInhabilitado() {
		String cad = " select inh.* "
				+ " from desvinculacion.inhabilitados inh "
				+ " join general.empleado_puesto emp "
				+ " on emp.id_empleado_puesto = inh.id_empleado_puesto "
				+ " where inh.inhabilitado is true " + " and emp.id_persona = "
				+ persona.getIdPersona();

		List<Inhabilitados> lista = new ArrayList<Inhabilitados>();
		lista = em.createNativeQuery(cad, Inhabilitados.class).getResultList();
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica si la persona se encuentra jubilada
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean controlarJubilado() {
		String cad = " select * " + " from desvinculacion.jubilados jub "
				+ " join general.empleado_puesto emp "
				+ " on emp.id_empleado_puesto = jub.id_empleado_puesto "
				+ " where jub.inhabilitado = true " + " and emp.id_persona = "
				+ persona.getIdPersona();

		List<Jubilados> lista = new ArrayList<Jubilados>();
		lista = em.createNativeQuery(cad, Jubilados.class).getResultList();
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica que la persona sea de nacionalidad paraguaya
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean controlarNacionalidad() {

		if ("CPO".equals(concurso.getDatosEspecificosTipoConc().getValorAlf())
				&& esContratadoOpermanente().equalsIgnoreCase("PERMANENTE")) {

			String cad = "select p.* "
					+ "from general.persona p "
					+ "where p.id_datos_especificos_nacionalid in ( select de.id_datos_especificos "
					+ "from seleccion.datos_especificos de "
					+ "join seleccion.datos_generales dg "
					+ "on de.id_datos_generales = dg.id_datos_generales "
					+ "where dg.descripcion = 'NACIONALIDAD' "
					+ "and (de.descripcion = 'PARAGUAYA' or de.descripcion = 'PARAGUAYA NATURALIZADA')) "
					+ "and p.id_persona = " + persona.getIdPersona();
			List<Persona> lista = new ArrayList<Persona>();
			lista = em.createNativeQuery(cad, Persona.class).getResultList();
			if (lista != null && lista.size() > 0) {
				return true;
			}
			return false;
		}
		return true;

	}

	private String esContratadoOpermanente() {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (!lista.isEmpty()) {
			if (lista.get(0).getPlantaCargoDet().getContratado())
				return "CONTRATADO";
			if (lista.get(0).getPlantaCargoDet().getPermanente())
				return "PERMANENTE";
		}
		return null;
	}

	/**
	 * Verifica que la persona sea mayor de edad
	 * 
	 * @return
	 */
	public Boolean esMayorDeEdad() {
		Date fechaHoy = new Date();
		Integer anho = fechaHoy.getYear() + 1900;
		Date fechaNac = persona.getFechaNacimiento();
		if (fechaNac == null)
			return true;
		else {
			Integer anhoNac = fechaNac.getYear() + 1900;
			Integer edad = anho - anhoNac;
			if (edad.intValue() >= 18)
				return true;
			else
				return false;
		}
	}

	public void imprimir() {

		try {
			Connection conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU98_comprobantePostula",
					obtenerMapaParametros(), false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idPostulante", personaPostulanteHome.getInstance()
				.getIdPersonaPostulante().toString());
		param.put("cod_Postulante", personaPostulanteHome.getInstance()
				.getUsuAlta());
		param.put("denoUnidad", concurso.getConfiguracionUoCab()
				.getDenominacionUnidad());

		return param;

	}

	public Boolean verificarPostulacion(Long idGrupo) throws Exception {
		
		//stortora, 20140529. Verificar que no sea uno de esos concursos por carpeta.
		ConcursoPuestoAgr Grupo = em.find(ConcursoPuestoAgr.class, idGrupo);
		
		if(Grupo.dondePostularse().equals(SeamResourceBundle.getBundle().getString("postularse_en_entidad")))
			return false;
		else{
			permitirPostular(idGrupo);
			if(permitirPostular != null && !permitirPostular)
				return permitirPostular;
		}
		Long idPersona = usuarioLogueado.getPersona().getIdPersona();
		
		
		Query q = em
				.createQuery("select PersonaPostulante from PersonaPostulante PersonaPostulante "
						+ " where PersonaPostulante.persona.idPersona =:idPersona and PersonaPostulante.activo is true ");
		q.setParameter("idPersona", idPersona);
		List lista = q.getResultList();
		if (lista.size() == 0) {
			q = em.createQuery("select Postulacion from Postulacion Postulacion where Postulacion.persona.idPersona = :idPersona "
					+ " and Postulacion.activo is true "
					+ " and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
			q.setParameter("idPersona", idPersona);
			q.setParameter("idGrupo", idGrupo);
			lista = q.getResultList();
			if (lista.size() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			/* verificar si ya es postulante para el grupoPuesto */
			StringBuffer SQL = new StringBuffer();
			SQL.append(" SELECT");
			SQL.append("    seleccion.persona_postulante.id_persona,");
			SQL.append("    seleccion.postulacion.id_concurso_puesto_agr,");
			SQL.append("    seleccion.postulacion.id_persona_postulante,");
			SQL.append("    seleccion.postulacion.activo ");
			SQL.append(" FROM");
			SQL.append("    seleccion.postulacion");
			SQL.append(" INNER JOIN seleccion.persona_postulante ");
			SQL.append(" ON");
			SQL.append("    (");
			SQL.append("        seleccion.postulacion.id_persona_postulante =");
			SQL.append("        seleccion.persona_postulante.id_persona_postulante");
			SQL.append("    )");
			SQL.append(" WHERE");
			SQL.append("    seleccion.persona_postulante.id_persona = "
					+ usuarioLogueado.getPersona().getIdPersona());
			SQL.append(" AND seleccion.postulacion.id_concurso_puesto_agr = "
					+ idGrupo + " AND postulacion.activo is true ");
			List lista2 = em.createNativeQuery(SQL.toString()).getResultList();
			if (lista2 != null && lista2.size() > 0) {
				return false;
			}
			
				
			return true;
		}

	}

	public Boolean precondiciones() throws Exception {
		/* verificar si ya es postulante para ese gruoPuesto */
		if (!verificarPostulacion(concursoPuestoAgr.getIdConcursoPuestoAgr())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU241_msg4"));
			msjError = "Error";
			return false;
		}
		return true;
	}

	/**
	 * Confirma la postulacion
	 * 
	 * @return
	 * @throws Exception
	 */

	public String confirmar() throws Exception {

		if (!verificarPostulacion(concursoPuestoAgr.getIdConcursoPuestoAgr())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU241_msg4"));
			return null;
		}
/*		if(interesDescripcion == null){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU3401_msg1"));
			return null;
		}*/
		/* 1. COPIAR DATOS DE LA TABLA PERSONAS A SEL_PERSONA_POSTULANTE */
		String resultadoPostulante = guardarPersonaPostulante();
		/* 2. GENERAR UN REGISTRO EN LA TABLA SEL_POSTULACION */
		String resultadoPostulacion = null;
		if (resultadoPostulante.equals("persisted")) {
			resultadoPostulacion = guardarPostulacion();
			if (resultadoPostulacion.equalsIgnoreCase("persited")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU241_msg3"));
				return null;

			}
			/*
			 * 3. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE DE
			 * LA TABLA SEL_ESTUDIOS_REALIDADOS del ID_PERSONA E INSERTAR EN LA
			 * MISMA TABLA TOMANDO EN CUENTA LO SGTE
			 */
			if (resultadoPostulacion.equals("persisted")) {
				listaEstudiosRealizados = buscaEstudiosRealizados();
				if (listaEstudiosRealizados != null
						&& listaEstudiosRealizados.size() > 0) {
					copiarEstudiosRealizados();
				}
				/*
				 * 4. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_IDIOMAS_PERSONA del ID_PERSONA E INSERTAR EN
				 * LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaIdiomas = buscarIdiomas();
				if (listaIdiomas != null && listaIdiomas.size() > 0) {
					copiarIdiomas();
				}
				/*
				 * 5. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_EXPERIENCIA_LABORAL del ID_PERSONA E INSERTAR
				 * EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaExperienciaLaboral = buscarExperienciaLaboral();
				if (listaExperienciaLaboral != null
						&& listaExperienciaLaboral.size() > 0) {
					copiarExperienciaLaboral();
				}
				/*
				 * 6. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_PERSONA_DISCAPACIDAD del ID_PERSONA E
				 * INSERTAR EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaPersonasDiscapacidad = buscarPersonaDiscapacidad();
				if (listaPersonasDiscapacidad != null
						&& listaPersonasDiscapacidad.size() > 0) {
					copiarPersonaDiscapacidad();

				}
				/*
				 * 7. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_REPR_PERSONA_DISCAPACIDAD del ID_PERSONA E
				 * INSERTAR EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE.
				 */
				listaReprPersonaDiscapacidad = buscarReprPersonaDiscapacidad();
				if (listaReprPersonaDiscapacidad != null
						&& listaReprPersonaDiscapacidad.size() > 0) {
					copiarReprPersonaDiscapacidad();
				}
				/*
				 * 8. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA TBL_DOCUMENTOS donde la columna: UBICACION_FISICA
				 * contenga el path: \Usuario_Portal \CI_ID_PERSONA E INSERTAR
				 * EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaDocumentos = buscarDocumentos();
				if ((listaDocumentos != null && listaDocumentos.size() > 0)) {
					copiarDocumentos();
					/*
					 * 9. Obtener los ID_DOCUMENTOS que fueron insertados en el
					 * paso 8 e ir insertando en la tabla
					 * SEL_POSTULACION_ADJUNTOS
					 */
					if (listaDocumentosInsertados != null
							&& listaDocumentosInsertados.size() > 0) {
						copiarIdDocumentos();
						/*
						 * 10. COPIAR DEL SISTEMA DE ARCHIVOS LA CARPETA
						 * CI_ID_PERSONA ( junto con todo su contenido ) a la
						 * RUTA: SICCA / Año/ OEE / ID_OEE / Tipo_Concurso /
						 * Id_concurso / Id_concurso_puesto_agr / Postulantes /
						 */
						String ubiFisica = generarUbiFisica();
						String CI_ID_PERSONA = calcCIPersona();
						String prefijoUnidad = calcPrefijoUnidad();
						String separador = File.separator;
						File srcFile = new File(prefijoUnidad + separador
								+ "SICCA" + separador + "USUARIO_PORTAL"
								+ separador + CI_ID_PERSONA);
						File dstFile = new File(prefijoUnidad + separador
								+ ubiFisica + separador + CI_ID_PERSONA);
						// Para verificar si existe el directorio destino, si
						// existe se borra todo y se crea de nuevo
						File destino = new File(prefijoUnidad + separador
								+ ubiFisica + separador + CI_ID_PERSONA);
						if (destino.exists()) {
							destino.delete();
						}

						try {
							Utiles.copyDirectory(srcFile, dstFile);
							fallo = false;
						} catch (Exception e) {
							statusMessages.clear();
							statusMessages
									.add(Severity.ERROR,
											"No se pudo realizar la copia de archivos.");
							fallo = true;
							e.printStackTrace();
							Transaction.instance().rollback();
						}
					}
				}
			}
		}

		if (!fallo) {
			em.flush();
			/*
			 * 11. Comparar cada registro de la tabla SEL_MATRIZ_DOC_CONFIG_DET
			 * ( donde en la cabecera SEL_MATRIZ_DOC_CONFIG_ENC contenga el
			 * ID_CONCURSO_PUESTO_AGR con estado ACTIVO = true) deberá comparar
			 * con los tipos de documentos de los registros de la tabla
			 * TBL_DOCUMENTO donde se tenga referenciado en la tabla
			 * SEL_POSTULACION_ADJUNTOS
			 */
			List<Object[]> listaIdDocsNoPresentes = calcIdNoPresentes();
			listaDocsNoPresentados.clear();
			if (listaIdDocsNoPresentes != null) {
				for (Object[] o : listaIdDocsNoPresentes) {
					listaDocsNoPresentados.add(o[1].toString());
				}
			}
			/* Enviar Mail */
			statusMessages.clear();
			enviarEmails();
			return "/seleccion/registrarPostulacion/PostulacionExitosa.xhtml";

		} else {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			return null;
		}
	}
	
	
	/**
	 * Confirma la postulacion Para concurso CIO
	 * 
	 * @return
	 * @throws Exception
	 */

	public String confirmarCIO() throws Exception {

		if (!verificarPostulacion(concursoPuestoAgr.getIdConcursoPuestoAgr())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU241_msg4"));
			return null;
		}
/*		if(interesDescripcion == null){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU3401_msg1"));
			return null;
		}*/
		/* 1. COPIAR DATOS DE LA TABLA PERSONAS A SEL_PERSONA_POSTULANTE */
		String resultadoPostulante = guardarPersonaPostulante();
		/* 2. GENERAR UN REGISTRO EN LA TABLA SEL_POSTULACION */
		String resultadoPostulacion = null;
		if (resultadoPostulante.equals("persisted")) {
			resultadoPostulacion = guardarPostulacion();
			if (resultadoPostulacion.equalsIgnoreCase("persited")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU241_msg3"));
				return null;

			}
			/*
			 * 3. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE DE
			 * LA TABLA SEL_ESTUDIOS_REALIDADOS del ID_PERSONA E INSERTAR EN LA
			 * MISMA TABLA TOMANDO EN CUENTA LO SGTE
			 */
			if (resultadoPostulacion.equals("persisted")) {
				listaEstudiosRealizadosLegajo = buscaEstudiosRealizadosCIO();
				if (listaEstudiosRealizadosLegajo != null
						&& listaEstudiosRealizadosLegajo.size() > 0) {
					copiarEstudiosRealizadosCIO();
				}
				/*
				 * 4. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_IDIOMAS_PERSONA del ID_PERSONA E INSERTAR EN
				 * LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaIdiomasLegajo = buscarIdiomasCIO();
				if (listaIdiomasLegajo != null && listaIdiomasLegajo.size() > 0) {
					copiarIdiomasCIO();
				}
				/*
				 * 5. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_EXPERIENCIA_LABORAL del ID_PERSONA E INSERTAR
				 * EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaExperienciaLaboralLegajo = buscarExperienciaLaboralCIO();
				if (listaExperienciaLaboralLegajo != null
						&& listaExperienciaLaboralLegajo.size() > 0) {
					copiarExperienciaLaboralCIO();
				}
				/*
				 * 6. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_PERSONA_DISCAPACIDAD del ID_PERSONA E
				 * INSERTAR EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaPersonasDiscapacidadLegajo = buscarPersonaDiscapacidadCIO();
				if (listaPersonasDiscapacidadLegajo != null
						&& listaPersonasDiscapacidadLegajo.size() > 0) {
					copiarPersonaDiscapacidadCIO();

				}
				/*
				 * 7. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA SEL_REPR_PERSONA_DISCAPACIDAD del ID_PERSONA E
				 * INSERTAR EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE.
				 */
				listaReprPersonaDiscapacidadLegajo = buscarReprPersonaDiscapacidadCIO();
				if (listaReprPersonaDiscapacidadLegajo != null
						&& listaReprPersonaDiscapacidadLegajo.size() > 0) {
					copiarReprPersonaDiscapacidadCIO();
				}
				/*
				 * 8. CREAR UNA COPIA DE LOS REGISTROS CON ESTADO ACTIVO = TRUE
				 * DE LA TABLA TBL_DOCUMENTOS donde la columna: UBICACION_FISICA
				 * contenga el path: \Usuario_Portal \CI_ID_PERSONA E INSERTAR
				 * EN LA MISMA TABLA TOMANDO EN CUENTA LO SGTE
				 */
				listaDocumentos = buscarDocumentosCIO();
				if ((listaDocumentos != null && listaDocumentos.size() > 0)) {
					copiarDocumentos();
					/*
					 * 9. Obtener los ID_DOCUMENTOS que fueron insertados en el
					 * paso 8 e ir insertando en la tabla
					 * SEL_POSTULACION_ADJUNTOS
					 */
					if (listaDocumentosInsertados != null
							&& listaDocumentosInsertados.size() > 0) {
						copiarIdDocumentos();
						/*
						 * 10. COPIAR DEL SISTEMA DE ARCHIVOS LA CARPETA
						 * CI_ID_PERSONA ( junto con todo su contenido ) a la
						 * RUTA: SICCA / Año/ OEE / ID_OEE / Tipo_Concurso /
						 * Id_concurso / Id_concurso_puesto_agr / Postulantes /
						 */
						String ubiFisica = generarUbiFisica();
						String CI_ID_PERSONA = calcCIPersona();
						String prefijoUnidad = calcPrefijoUnidad();
						String separador = File.separator;
						File srcFile = new File(prefijoUnidad + separador
								+ "SICCA" + separador + "USUARIO_PORTAL"
								+ separador + CI_ID_PERSONA);
						File dstFile = new File(prefijoUnidad + separador
								+ ubiFisica + separador + CI_ID_PERSONA);
						// Para verificar si existe el directorio destino, si
						// existe se borra todo y se crea de nuevo
						File destino = new File(prefijoUnidad 
								+ ubiFisica + separador + CI_ID_PERSONA);
						if (destino.exists()) {
							destino.delete();
						}

						try {
							
							Utiles.copyDirectory(srcFile, dstFile);
							//Generar el PDF con los datos del Legajo al momento de la postulacion y guardarlo en la carpeta dstFile
							
							generarPdfLegajo(prefijoUnidad + ubiFisica + separador + CI_ID_PERSONA);
							
							
							fallo = false;
						} catch (Exception e) {
							statusMessages.clear();
							statusMessages
									.add(Severity.ERROR,
											"No se pudo realizar la copia de archivos.");
							fallo = true;
							e.printStackTrace();
							Transaction.instance().rollback();
						}
					}
				}
			}
		}

		if (!fallo) {
			em.flush();
			/*
			 * 11. Comparar cada registro de la tabla SEL_MATRIZ_DOC_CONFIG_DET
			 * ( donde en la cabecera SEL_MATRIZ_DOC_CONFIG_ENC contenga el
			 * ID_CONCURSO_PUESTO_AGR con estado ACTIVO = true) deberá comparar
			 * con los tipos de documentos de los registros de la tabla
			 * TBL_DOCUMENTO donde se tenga referenciado en la tabla
			 * SEL_POSTULACION_ADJUNTOS
			 */
			List<Object[]> listaIdDocsNoPresentes = calcIdNoPresentes();
			listaDocsNoPresentados.clear();
			if (listaIdDocsNoPresentes != null) {
				for (Object[] o : listaIdDocsNoPresentes) {
					listaDocsNoPresentados.add(o[1].toString());
				}
			}
			/* Enviar Mail */
			statusMessages.clear();
			enviarEmails();
			return "/seleccion/registrarPostulacion/PostulacionExitosa.xhtml";

		} else {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			return null;
		}
	}
	
	
	/**
	 * Imprimir Reporte
	 */

	public void generarPdfLegajo(String ubiFisica) {
		try {
			
			Connection conn = JpaResourceBean.getConnection();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("id_persona", persona.getIdPersona());
			param.put("nombre_completo", persona.getNombreCompleto());
			param.put("grupo_sangre",persona.getGrupoSanguineoAbo()+ persona.getGrupoSanguineoRh());
			
			String antiguedad = this.obtenerAntiguedad(persona.getIdPersona());
			
			param.put("antiguedad", antiguedad);
			
			DiscapacidadApoyos apoyos = obtenerApoyos (persona.getIdPersona());
			
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
			
			param.put("mostrarObs", "SI");
			
			String observacionesPendientes = obtenerObservaciones(persona.getIdPersona());
			
			param.put("observaciones", observacionesPendientes);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");		
			String nombreDoc = "Copia_legajo_al_postularse_"+ persona.getDocumentoIdentidad()+"_" + sdf.format(new Date()) + ".pdf";
			JasperReportUtils.guardarPDFenDestino("RPT_CU659_Curriculum_Legajo", param, conn, ubiFisica +"\\"+nombreDoc);
			//GUARDAR UN NUEVO REGISTRO DEL DOCUMENTO GENERADO
			Documentos registroDoc = new Documentos();
			registroDoc.setNombreFisicoDoc(nombreDoc);
			registroDoc.setUbicacionFisica(ubiFisica);
			registroDoc.setMimetype("application/pdf");
			registroDoc.setActivo(true);
			registroDoc.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			registroDoc.setFechaAlta(new Date());
			registroDoc.setConcurso(this.concurso);
			registroDoc.setPersona(this.persona);
			registroDoc.setNombreTabla("persona_postulante");
			registroDoc.setIdTabla(personaPostulanteHome.getInstance().getIdPersonaPostulante());
			em.persist(registroDoc);
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String ponerBarra(String direccion) {
		if (direccion.contains("\\")) {
			direccion = direccion.replace("\\",
					System.getProperty("file.separator"));

		} else if (direccion.contains("//")) {
			direccion = direccion.replace("//",
					System.getProperty("file.separator"));

		} else if (direccion.contains("/")) {
			direccion = direccion.replace("/",
					System.getProperty("file.separator"));

		}
		return direccion;
	}
	
	@SuppressWarnings("unchecked")
	private static String locate() {
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		String dir = "";
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		emsta = emf.createEntityManager();
		List<Referencias> referencias = emsta
				.createQuery(
						"Select r from Referencias r "
								+ " where r.descAbrev like 'ADJUNTOS' and r.dominio like 'ADJUNTOS'")
				.getResultList();
		if (!referencias.isEmpty()) {
			dir = referencias.get(0).getDescLarga();
			if (dir.contains("\\")) {
				dir = dir.replace("\\", System.getProperty("file.separator"));

			} else if (dir.contains("//")) {
				dir = dir.replace("//", System.getProperty("file.separator"));

			} else if (dir.contains("/")) {
				dir = dir.replace("/", System.getProperty("file.separator"));

			}

		}
		return dir;

	}
	
	private String obtenerAntiguedad(Long idPersona) {
		String sql = "select sum(e.fecha_fin - e.fecha_inicio) as dias "
				+ "from general.empleado_puesto e " + "where e.id_persona = "
				+ idPersona + "and e.incide_antiguedad is true "
				+ "and e.fecha_inicio is not null and e.fecha_fin is not null";
		Object cantdias = (Object) em.createNativeQuery(sql).getSingleResult();
		Integer anhos = 0;
		Integer meses = 0;
		Integer dias = 0;
		Integer diferencia = 0;
		if (cantdias != null)
			diferencia = new Integer(cantdias.toString());
		if (diferencia.intValue() > 365) {
			anhos = diferencia / 365;
			diferencia -= (365 * anhos);
		}
		if (diferencia.intValue() > 30) {
			meses = diferencia / 30;
			diferencia -= (30 * meses);

		}
		dias = diferencia;
		return anhos + " años " + meses + " meses " + dias + " dias ";
	}
	
	private DiscapacidadApoyos obtenerApoyos(Long idPersona) {

		Query q = em.createQuery("select d from DiscapacidadApoyos d "
				+ "  where d.activo is true and d.persona.idPersona = "
				+ idPersona);
		List<DiscapacidadApoyos> ap = q.getResultList();
		DiscapacidadApoyos apoyos = new DiscapacidadApoyos();
		if (!ap.isEmpty()) {
			return ap.get(0);
			
		}else
			return null;
		
	}
	
	private String obtenerObservaciones(Long idPersona) {
		List <AuditLegajoObs> listaLegajoObs = new ArrayList<AuditLegajoObs>();

		String query = "SELECT ao.* "
				+ "FROM legajo.audit_legajo_obs ao "
				+ "join legajo.audit_legajo a on a.audit_legajo = ao.audit_legajo "
				+ "WHERE a.estado = 2 and trim(ao.observacion) <> '' AND a.ID_PERSONA = "
				+ idPersona;
		listaLegajoObs = em.createNativeQuery(query, AuditLegajoObs.class).getResultList();
		String observacionesPendientes = "";
		if (!listaLegajoObs.isEmpty()) {
			for (AuditLegajoObs obj : listaLegajoObs) {
				if (obj.getFicha().intValue() == 1)
					observacionesPendientes += "Datos Personales: ";
				else
					observacionesPendientes += "Datos Familiares: ";

				observacionesPendientes += obj.getObservacion() + ". ";
			}
		}
		return observacionesPendientes;
	}

	@SuppressWarnings("unchecked")
	
	public void enviarEmails() {
		String asunto = null;
		asunto = " Confirmación de Postulación - Portal Paraguay Concursa";
		String mensaje = "<h4><b>Estimado/a "
				+ persona.getNombres()
				+ " "
				+ persona.getApellidos()
				+ ".</b></h4>"
				+ "<p> Se ha registrado con &eacute;xito la postulaci&oacute;n al Concurso: "
				+ "<b>";
				if( concurso.getNumero() != null &&   concurso.getAnhio() != null)
					mensaje += concurso.getNumero()	+ "/"+ concurso.getAnhio();
				
				mensaje += " de "
				+ concurso.getConfiguracionUoCab().getDenominacionUnidad()
				+ " - "
				+ concurso.getNombre()
				+ ", "
				+ concursoPuestoAgr.getDescripcionGrupo()
				+ " </b></p>"
				+ "<p><b> Adem&aacute;s le recordamos que el C&oacute;digo de Postulaci&oacute;n que utilizar&aacute; "
				+ "el Portal Paraguay Concursa es: "
				+ usuarioLogueado.getCodigoUsuario()
				+ " </b></p> "
				+ "<p> <b>&#161;Importante&#33;: </b>  Si desea consultar sus postulaciones o cancelarlas, podr&aacute; "
				+ "acceder a su cuenta en Portal,y en el apartado &quot;Mis Postulaciones&quot;, donde encontrar&aacute; "
				+ "todas las postulaciones activas en las que se encuentre inscripto/a, junto con sus respectivos enlaces de "
				+ "&quot;Cancelar Postulaci&oacute;n&quot;. </p> "
				+ "<p align=\"justify\"> </p><strong>Atentamente,</strong>"
				+ "<p><strong>Portal Paraguay Concursa.</strong></p>";
		try {
			usuarioPortalFormController.enviarMails(persona.getEMail(),
					mensaje, asunto, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pdf() {
		tab6VistaPreliminarFormController.setPersona(persona);
		listaExperienciaLaboral = buscarExperienciaLaboral();
		tab6VistaPreliminarFormController
				.setExperienciaLaboralsList(listaExperienciaLaboral);
		tab6VistaPreliminarFormController.pdf();
	}

	/**
	 * Este metodo calcula el prefijo antes de la ruta completa del archivo.
	 * 
	 * @return
	 */
	private String calcPrefijoUnidad() {
		Query q = em
				.createQuery("SELECT ref.descLarga FROM Referencias ref WHERE ref.dominio = 'ADJUNTOS'");
		String descLarga = (String) q.getSingleResult();
		if (descLarga == null) {
			descLarga = "";
		}
		return descLarga;
	}

	private String calcCIPersona() {
		return postulacionHome.getInstance().getPersonaPostulante()
				.getPersona().getDocumentoIdentidad()
				+ "_"
				+ postulacionHome.getInstance().getPersonaPostulante()
						.getPersona().getIdPersona();

	}

	/**
	 * Este metodo hace una diferencia entre dos resultSets
	 * 
	 * @return
	 */

	private List calcIdNoPresentes() {
		List lista = null;
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT T1.* FROM ");
		SQL.append(" ( ");
		SQL.append(" ( SELECT seleccion.matriz_doc_config_det.id_datos_especificos_tipo_docu, ");
		SQL.append("         seleccion.datos_especificos.descripcion ");
		SQL.append(" FROM    seleccion.matriz_doc_config_det ");
		SQL.append("         INNER JOIN seleccion.matriz_doc_config_enc ");
		SQL.append("         ON      ( ");
		SQL.append("                         seleccion.matriz_doc_config_det.id_matriz_doc_config_enc = seleccion.matriz_doc_config_enc.id_matriz_doc_config_enc ");
		SQL.append("                 ) ");
		SQL.append("         INNER JOIN seleccion.datos_especificos ");
		SQL.append("         ON      ( ");
		SQL.append("                         seleccion.matriz_doc_config_det.id_datos_especificos_tipo_docu = seleccion.datos_especificos.id_datos_especificos ");
		SQL.append("                 ) ");
		SQL.append("                 WHERE ");
		SQL.append("                 seleccion.matriz_doc_config_enc.id_concurso_puesto_agr =  "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		SQL.append(" ) ");
		SQL.append(" ");
		SQL.append("EXCEPT ");
		SQL.append("       ( SELECT general.documentos.id_datos_especificos_tipo_documento, ");
		SQL.append("               seleccion.datos_especificos.descripcion ");
		SQL.append("       FROM    seleccion.postulacion_adjuntos ");
		SQL.append("               INNER JOIN general.documentos ");
		SQL.append("               ON      ( ");
		SQL.append("                               seleccion.postulacion_adjuntos.id_documento = general.documentos.id_documento ");
		SQL.append("                       ) ");
		SQL.append("               INNER JOIN seleccion.datos_especificos ");
		SQL.append("               ON      ( ");
		SQL.append("                               general.documentos.id_datos_especificos_tipo_documento = seleccion.datos_especificos.id_datos_especificos ");
		SQL.append("                       ) ");
		SQL.append("                       WHERE ");
		SQL.append("                       seleccion.postulacion_adjuntos.id_postulacion = "
				+ postulacion.getIdPostulacion());
		SQL.append("       )");
		SQL.append("       ) AS T1");

		System.out.println("EL QUERY calcIdNoPresentes:  " + SQL.toString());
		try {
			lista = em.createNativeQuery(SQL.toString()).getResultList();
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}
		return lista;
	}

	public String docsExcept() {
		String except = " SELECT general.documentos.id_datos_especificos_tipo_documento, seleccion.datos_especificos.descripcion "
				+ " FROM general.documentos "
				+ " INNER JOIN seleccion.datos_especificos "
				+ " ON      ( "
				+ "  general.documentos.id_datos_especificos_tipo_documento = seleccion.datos_especificos.id_datos_especificos )"
				+ "WHERE general.documentos.activo is true "
				+ " and general.documentos.id_persona = " +persona.getIdPersona()
				

//				+ "and upper(general.documentos.ubicacion_fisica) like upper('%SICCA%Usuario_Portal%"
//				+ persona.getDocumentoIdentidad()
//				+ "_"
//				+ persona.getIdPersona()+  "')"
				; 
		
		if(esFuncionario(persona.getIdPersona())
				&& !(this.concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CPO")
						|| this.concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CME")))
			except += " and general.documentos.nombre_pantalla like '%_legajo%' " ;
		
		
		return except;
	}
	
	public Boolean esFuncionario(Long idPersona){
		
		Boolean retorno = false;
		EmpleadoPuesto empleado;
		String sql = " select * from general.empleado_puesto where id_persona = "+ idPersona;
		
		List <EmpleadoPuesto> listaEmpleado =  em.createNativeQuery(sql, EmpleadoPuesto.class).getResultList();
		
		if(listaEmpleado.size() > 0)
				 empleado = listaEmpleado.get(0);
		else 
			empleado = new EmpleadoPuesto();
		
		if(empleado.getIdEmpleadoPuesto() != null )
			retorno = true;
		return retorno;
	}

	@SuppressWarnings("unchecked")
	public void docsAgrupdosObligatoriosFaltantes() {
		
		String sqlGrupos = "select * from seleccion.matriz_doc_config_grupos where id_matriz_doc_config_enc in "
				+ "		(select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc where id_concurso_puesto_agr = "
				+ this.concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and activo = true ) and evaluacion_doc = true ";
		List <MatrizDocConfigGrupos> listaGrupos = em.createNativeQuery(sqlGrupos, MatrizDocConfigGrupos.class).getResultList();
		
		List <Documentos> listaDocsPostulantes = this.ListarDocsPostulante();
		docsAgrupadosFaltantesList.clear();
		if(listaGrupos.size() > 0){
			//debe recorrer cada grupo de documentos y verificar que tenga por lo menos unos de los documentos. 
			boolean existeUno = false;
			
			for(MatrizDocConfigGrupos grupo : listaGrupos ){
				String sqlDetalles = "select * from seleccion.matriz_doc_config_det where id_matriz_doc_config_grupos = "+grupo.getIdMatrizDocConfigGrupos();
				List <MatrizDocConfigDet> listaDocsGrupo = em.createNativeQuery(sqlDetalles, MatrizDocConfigDet.class).getResultList();
				
				existeUno = false;
				for (MatrizDocConfigDet det :listaDocsGrupo ){
					
					for(Documentos doc : listaDocsPostulantes){
						
						if(det.getDatosEspecificos() != null && doc.getDatosEspecificos() != null){
							if(det.getDatosEspecificos().getIdDatosEspecificos() == doc.getDatosEspecificos().getIdDatosEspecificos() ){
								existeUno = true;
								docsAgrupadosFaltantesList.clear();
								
							}
						}
						
					}
					
					if(!existeUno){
						docsAgrupadosFaltantesList.add( grupo.getDescripcion() + " - "+ det.getDatosEspecificos().getDescripcion());
					}
						
					
				}
				
			}
			
			
		}
				
		if (docsAgrupadosFaltantesList.size() > 0)
				faltanDocsAgrupados = true;
			else
				faltanDocsAgrupados = false;
		
	}
	
	public List<Documentos> ListarDocsPostulante() {
		String sql = " SELECT general.documentos.* "
				+ " FROM general.documentos "
				+ " INNER JOIN seleccion.datos_especificos "
				+ " ON      ( "
				+ "  general.documentos.id_datos_especificos_tipo_documento = seleccion.datos_especificos.id_datos_especificos )"
				+ "WHERE general.documentos.activo is true and upper(general.documentos.ubicacion_fisica) like upper('%SICCA%Usuario_Portal%"
				+ persona.getDocumentoIdentidad()
				+ "_"
				+ persona.getIdPersona()+  "')"; 
		
		if(esFuncionario(persona.getIdPersona()) 
				&& !(this.concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CPO")
				|| this.concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CME")))
			sql += " and general.documentos.nombre_pantalla like '%_legajo%' " ;
		
		
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public void docsNoObligatorios() {

		String docObligatorios = "select de.id_datos_especificos, de.descripcion "
				+ "from seleccion.datos_especificos as de where de.id_datos_especificos in "
				+ "	(select T.id_datos_especificos_tipo_docu from "
				+ "(select * from seleccion.matriz_doc_config_det where id_matriz_doc_config_enc = "
				+ "(select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()  + " and activo = true "
				+ ")) as T "
				+ " where T.obligatorio = FALSE and T.evaluacion_doc = TRUE )	";

		String query = "	SELECT T1.* FROM  ( ( " + docObligatorios
				+ " ) EXCEPT ( " + docsExcept() + ")) AS T1";
		List<Object[]> lista = null;
		try {
			lista = em.createNativeQuery(query).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// se obtienen los nombres de tipos de documentos
		docsNoObligatoriosFaltantesList.clear();
		if (lista != null) {
			for (Object[] o : lista) {
				docsNoObligatoriosFaltantesList.add(o[1].toString());
				System.out.println(o[1].toString());
			}
			if (lista.size() > 0)
				faltanDocsNoObligatorios = true;
			else
				faltanDocsNoObligatorios = false;
		} else {
			faltanDocsNoObligatorios = false;
		}
	}
	

	
	
	@SuppressWarnings("unchecked")
	public void docsPresentados() {
		String sqlPresentados = docsExcept();
		
		List<Object[]> lista = null;
		try {
			lista = em.createNativeQuery(sqlPresentados).getResultList();
			docsPresentados.clear();
			if (lista != null) {
				for (Object[] o : lista) {
					docsPresentados.add(o[1].toString());
					
				}
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void docsSolicitadosObligatorios() {
		String sqlObligatorios = "select de.id_datos_especificos, de.descripcion "
				+ "from seleccion.datos_especificos as de where de.id_datos_especificos in "
				+ "	(select T.id_datos_especificos_tipo_docu from "
				+ "(select * from seleccion.matriz_doc_config_det where id_matriz_doc_config_enc = "
				+ "(select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and activo = true "
				+ ")) as T "
				+ " where T.obligatorio = TRUE and T.evaluacion_doc = TRUE )	";;
		
		List<Object[]> lista = null;
		try {
			lista = em.createNativeQuery(sqlObligatorios).getResultList();
			docsSolicitados.clear();
			if (lista != null) {
				for (Object[] o : lista) {
					docsSolicitados.add(o[1].toString());
					
				}
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void docsSolicitadosNoObligatorios() {
		String sql = "select de.id_datos_especificos, de.descripcion "
				+ "from seleccion.datos_especificos as de where de.id_datos_especificos in "
				+ "	(select T.id_datos_especificos_tipo_docu from "
				+ "(select * from seleccion.matriz_doc_config_det where id_matriz_doc_config_enc = "
				+ "(select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()  + " and activo = true "
				+ ")) as T "
				+ " where T.obligatorio = FALSE and T.evaluacion_doc = TRUE )	";
		
		List<Object[]> lista = null;
		try {
			lista = em.createNativeQuery(sql).getResultList();
			docsSolicitadosNoObligatorios.clear();
			if (lista != null) {
				for (Object[] o : lista) {
					docsSolicitadosNoObligatorios.add(o[1].toString());
					
				}
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void docsSolicitadosAgrupados() {
		String sql = "select * from seleccion.matriz_doc_config_grupos where id_matriz_doc_config_enc in "
				+ "		(select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc where id_concurso_puesto_agr = "
				+ this.concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and activo = true ) and evaluacion_doc = true ";
		
		List<Object[]> lista = null;
		try {
			lista = em.createNativeQuery(sql).getResultList();
			docsSolicitadosAgrupados.clear();
			if (lista != null) {
				for (Object[] o : lista) {
					docsSolicitadosAgrupados.add(o[1].toString());
					
				}
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	@SuppressWarnings("unchecked")
	public void docsObligatoriosFaltantes() {

		String docObligatorios = "select de.id_datos_especificos, de.descripcion "
				+ "from seleccion.datos_especificos as de where de.id_datos_especificos in "
				+ "	(select T.id_datos_especificos_tipo_docu from "
				+ "(select * from seleccion.matriz_doc_config_det where id_matriz_doc_config_enc = "
				+ "(select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and activo = true "
				+ ")) as T "
				+ " where T.obligatorio = TRUE and T.evaluacion_doc = TRUE )	";

		String query = "	SELECT T1.* FROM  ( ( " + docObligatorios
				+ " ) EXCEPT ( " + docsExcept() + ")) AS T1";
		List<Object[]> lista = null;
		try {
			lista = em.createNativeQuery(query).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// se obtienen los nombres de tipos de documentos
		docsFaltantesList.clear();
		if (lista != null) {
			for (Object[] o : lista) {
				docsFaltantesList.add(o[1].toString());
				
			}
			if (lista.size() > 0)
				faltanDocs = true;
			else
				faltanDocs = false;
		} else {
			faltanDocs = false;
		}
		
		docsAgrupdosObligatoriosFaltantes();
	}


	/**
	 * Obtiene los documentos que le faltan al postulante para postularse
	 * 
	 * @return
	 */

	private void docsFaltantePrevioConfirmar() {
		List<Object[]> lista = null;
		String cSepardor = "%";
		StringBuffer SQL = new StringBuffer();

		SQL.append(" SELECT T1.* FROM  (  ( SELECT seleccion.matriz_doc_config_det.id_datos_especificos_tipo_docu, seleccion.datos_especificos.descripcion ");
		SQL.append("  FROM    seleccion.matriz_doc_config_det ");

		SQL.append("  INNER JOIN seleccion.matriz_doc_config_enc ");
		SQL.append("     ON ( seleccion.matriz_doc_config_det.id_matriz_doc_config_enc = seleccion.matriz_doc_config_enc.id_matriz_doc_config_enc  )");
		SQL.append("   INNER JOIN seleccion.datos_especificos ");
		SQL.append("      ON      ( ");
		SQL.append("              seleccion.matriz_doc_config_det.id_datos_especificos_tipo_docu = seleccion.datos_especificos.id_datos_especificos ");
		SQL.append("      ) ");
		SQL.append("        WHERE  ");
		SQL.append(" 	seleccion.matriz_doc_config_enc.id_concurso_puesto_agr ="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		SQL.append("  )  WHERE seleccion.matriz_doc_config_det.obligatorio = TRUE and seleccion.matriz_doc_config_det.evaluacion_doc = TRUE");
		SQL.append(" EXCEPT (");
		SQL.append("   SELECT general.documentos.id_datos_especificos_tipo_documento, ");
		SQL.append("             seleccion.datos_especificos.descripcion ");
		SQL.append("              FROM general.documentos ");
		SQL.append("              INNER JOIN seleccion.datos_especificos ");
		SQL.append("              ON      (");
		SQL.append("                            general.documentos.id_datos_especificos_tipo_documento = seleccion.datos_especificos.id_datos_especificos ");
		SQL.append("                    ) ");
		SQL.append("                     WHERE general.documentos.activo is true and upper(general.documentos.ubicacion_fisica) like upper('%SICCA%Usuario_Portal%"
				+ persona.getDocumentoIdentidad()
				+ "_"
				+ persona.getIdPersona() + "')");
		SQL.append(" 	)) AS T1");

		System.out.println("EL QUERY docsFaltantePrevioConfirmar:  "
				+ SQL.toString());
		try {
			lista = em.createNativeQuery(SQL.toString()).getResultList();
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}

		// se obtienen los nombres de tipos de documentos
		docsFaltantesList.clear();
		if (lista != null) {
			for (Object[] o : lista) {
				docsFaltantesList.add(o[1].toString());
				System.out.println(o[1].toString());
			}
		}

		if (lista.size() > 0)
			faltanDocs = true;

	}

	/**
	 * Copia los datos de la persona en persona_postulante
	 * 
	 * @return
	 */
	private String guardarPersonaPostulante() {
		try {
			personaPostulante = new PersonaPostulante();
			personaPostulante.setFechaAlta(new Date());
			personaPostulante.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			personaPostulante.setActivo(true);
			personaPostulante.setApellidos(persona.getApellidos());
			if (persona.getBarrio() != null)
				personaPostulante.setBarrio(persona.getBarrio());
			personaPostulante.setCallePrincipal(persona.getCallePrincipal());
			if (persona.getCiudadByIdCiudadDirecc() != null)
				personaPostulante.setCiudadDirecc(persona
						.getCiudadByIdCiudadDirecc());
			if (persona.getCiudadByIdCiudadNac() != null)
				personaPostulante
						.setCiudadNac(persona.getCiudadByIdCiudadNac());
			if (persona.getDatosEspecificos() != null)
				personaPostulante.setDatosEspecificos(persona
						.getDatosEspecificos());
			personaPostulante.setDepartamentoNro(persona.getDepartamentoNro());
			personaPostulante
					.setDireccionLaboral(persona.getDireccionLaboral());
			personaPostulante.setDocumentoIdentidad(persona
					.getDocumentoIdentidad());
			personaPostulante.setEMail(persona.getEMail());
			personaPostulante.setEstadoCivil(persona.getEstadoCivil());
			personaPostulante.setFechaNacimiento(persona.getFechaNacimiento());
			personaPostulante.setNombres(persona.getNombres());
			personaPostulante.setObservacion(persona.getObservacion());
			personaPostulante
					.setOtrasDirecciones(persona.getOtrasDirecciones());
			if (persona.getPaisByIdPaisExpedicionDoc() != null)
				personaPostulante.setPaisExpedicionDoc(persona
						.getPaisByIdPaisExpedicionDoc());
			personaPostulante.setPersona(persona);
			personaPostulante.setPiso(persona.getPiso());
			personaPostulante.setPrimeraLateral(persona.getPrimeraLateral());
			personaPostulante.setSegundaLateral(persona.getSegundaLateral());
			personaPostulante.setSexo(persona.getSexo());
			personaPostulante.setTelefonos(persona.getTelefonos());
			personaPostulante.setTelefonoLab(persona.getTelefonoLab());
			personaPostulante.setTelefonoPart(persona.getTelefonoPart());
			
			Set<ExperienciaLaboral> CopiaExperienciaLaboral = new HashSet<ExperienciaLaboral>(0);
			CopiaExperienciaLaboral.addAll(persona.getExperienciaLaborals());
			personaPostulante.setExperienciaLaborals(CopiaExperienciaLaboral);
			
			Set<ExperienciaLaboralLegajo> CopiaExperienciaLaboralLegajo = new HashSet<ExperienciaLaboralLegajo>(0);
			CopiaExperienciaLaboralLegajo.addAll(persona.getExperienciaLaboralsLegajo());
			personaPostulante.setExperienciaLaboralsLegajo(CopiaExperienciaLaboralLegajo);
			
			personaPostulanteHome.setInstance(personaPostulante);
			personaPostulanteHome.persist();

			return "persisted";

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return "FALLO";
	}

	/**
	 * Copia los datos en la tabla postulacion
	 * 
	 * @return
	 */
	private String guardarPostulacion() {
		try {
			postulacion = new Postulacion();
			postulacion.setActivo(true);
			postulacion.setConcursoPuestoAgr(concursoPuestoAgr);
			postulacion.setInteresCargo(interesDescripcion);
			postulacion.setFechaPostulacion(new Date());
			postulacion.setUsuPostulacion(usuarioLogueado.getCodigoUsuario());
			postulacion.setPersonaPostulante(personaPostulanteHome
					.getInstance());
			postulacion.setEstadoPostulacion(Postulacion.ESTADO_POSTULACION_ACTIVO);
			postulacionHome.setInstance(postulacion);
			postulacionHome.persist();

			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "FALLO";
	}

	/**
	 * Recupera la lista de Estudios Realizados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<EstudiosRealizados> buscaEstudiosRealizados() {

		try {
			String sql = "select est.* "
					+ "from seleccion.estudios_realizados est "
					+ "join general.persona p "
					+ "on p.id_persona = est.id_persona "
					+ "where est.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();

			return em.createNativeQuery(sql, EstudiosRealizados.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Recupera la lista de Estudios Realizados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<EstudiosRealizadosLegajo> buscaEstudiosRealizadosCIO() {

		try {
			String sql = "select est.* "
					+ "from legajo.estudios_realizados est "
					+ "join general.persona p "
					+ "on p.id_persona = est.id_persona "
					+ "where est.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();

			return em.createNativeQuery(sql, EstudiosRealizadosLegajo.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Inserta una copia de los estudios realizados
	 * 
	 * @return
	 * @throws Exception
	 */
	private String copiarEstudiosRealizados() throws Exception {
		try {
			for (int i = 0; i < listaEstudiosRealizados.size(); i++) {
				EstudiosRealizados estudio = new EstudiosRealizados();
				estudio = listaEstudiosRealizados.get(i);
				EstudiosRealizados actual = new EstudiosRealizados();
				actual.setActivo(true);
				actual.setDuracionHoras(estudio.getDuracionHoras());
				actual.setEspecialidades(estudio.getEspecialidades());
				actual.setFechaFin(estudio.getFechaFin());
				actual.setFechaInicio(estudio.getFechaInicio());
				actual.setFinalizo(estudio.getFinalizo());
				actual.setInstitucionEducativa(estudio
						.getInstitucionEducativa());
				actual.setOtraInstit(estudio.getOtraInstit());
				actual.setOtroTituloObt(estudio.getOtroTituloObt());
				actual.setPais(estudio.getPais());
				actual.setPersonaPostulante(personaPostulanteHome.getInstance());
				actual.setTituloAcademico(estudio.getTituloAcademico());
				actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				actual.setFechaAlta(new Date());
				actual.setTipoDuracion(estudio.getTipoDuracion());// MODIFICADO
																	// RV
				actual.setEstadoActual(estudio.getEstadoActual());// MODIFICADO
																	// RV
				actual.setOtroNivel(estudio.getOtroNivel()); //Agregado por Santiago
				em.persist(actual);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}
	}
	
	/**
	 * Inserta una copia de los estudios realizados
	 * 
	 * @return
	 * @throws Exception
	 */
	private String copiarEstudiosRealizadosCIO() throws Exception {
		try {
			for (int i = 0; i < listaEstudiosRealizadosLegajo.size(); i++) {
				EstudiosRealizadosLegajo estudio = new EstudiosRealizadosLegajo();
				estudio = listaEstudiosRealizadosLegajo.get(i);
				EstudiosRealizados actual = new EstudiosRealizados();
				actual.setActivo(true);
				actual.setDuracionHoras(estudio.getDuracionHoras());
				actual.setEspecialidades(estudio.getEspecialidades());
				actual.setFechaFin(estudio.getFechaFin());
				actual.setFechaInicio(estudio.getFechaInicio());
				actual.setFinalizo(estudio.getFinalizo());
				actual.setInstitucionEducativa(estudio
						.getInstitucionEducativa());
				actual.setOtraInstit(estudio.getOtraInstit());
				actual.setOtroTituloObt(estudio.getOtroTituloObt());
				actual.setPais(estudio.getPais());
				actual.setPersonaPostulante(personaPostulanteHome.getInstance());
				actual.setTituloAcademico(estudio.getTituloAcademico());
				actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				actual.setFechaAlta(new Date());
				actual.setTipoDuracion(estudio.getTipoDuracion());// MODIFICADO
																	// RV
//				actual.setEstadoActual(estudio.getEstadoActual());// MODIFICADO
																	// RV
				actual.setOtroNivel(estudio.getOtroNivel()); //Agregado por Santiago
				em.persist(actual);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}
	}

	/**
	 * Recupera la lista de Idiomas
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<IdiomasPersona> buscarIdiomas() {
		try {
			String sql = "select idioma.* "
					+ "from seleccion.idiomas_persona idioma "
					+ "join general.persona p  "
					+ "on p.id_persona = idioma.id_persona "
					+ "where idioma.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();
			System.out.println("SQL: " + sql);
			return em.createNativeQuery(sql, IdiomasPersona.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * Recupera la lista de Idiomas
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<IdiomasLegajoPersona> buscarIdiomasCIO() {
		try {
			String sql = "select idioma.* "
					+ "from legajo.idiomas_persona idioma "
					+ "join general.persona p  "
					+ "on p.id_persona = idioma.id_persona "
					+ "where idioma.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();
			System.out.println("SQL: " + sql);
			return em.createNativeQuery(sql, IdiomasLegajoPersona.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Copia la lista de idiomas, asignandole un postulante
	 * 
	 * @return
	 */
	private String copiarIdiomas() {
		try {
			for (int i = 0; i < listaIdiomas.size(); i++) {
				IdiomasPersona idioma = new IdiomasPersona();
				idioma = listaIdiomas.get(i);
				IdiomasPersona actual = new IdiomasPersona();
				actual.setActivo(true);
				actual.setDatosEspecificos(idioma.getDatosEspecificos());
				actual.setEscribe(idioma.getEscribe());
				actual.setHabla(idioma.getHabla());
				actual.setLee(idioma.getLee());
				actual.setPersonaPostulante(personaPostulanteHome.getInstance());
				actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				actual.setFechaAlta(new Date());
				em.persist(actual);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * Copia la lista de idiomas, asignandole un postulante
	 * 
	 * @return
	 */
	private String copiarIdiomasCIO() {
		try {
			for (int i = 0; i < listaIdiomasLegajo.size(); i++) {
				IdiomasLegajoPersona idioma = new IdiomasLegajoPersona();
				idioma = listaIdiomasLegajo.get(i);
				IdiomasPersona actual = new IdiomasPersona();
				actual.setActivo(true);
				actual.setDatosEspecificos(idioma.getDatosEspecificos());
				actual.setEscribe(idioma.getEscribe());
				actual.setHabla(idioma.getHabla());
				actual.setLee(idioma.getLee());
				actual.setPersonaPostulante(personaPostulanteHome.getInstance());
				actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				actual.setFechaAlta(new Date());
				em.persist(actual);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Recupera la lista de Experiencias Laborales
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ExperienciaLaboral> buscarExperienciaLaboral() {

		try {
			String sql = "select exp.*  "
					+ "from seleccion.experiencia_laboral exp "
					+ "join general.persona p  "
					+ "on p.id_persona = exp.id_persona "
					+ "where exp.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();
			System.out.println("SQL " + sql);
			return em.createNativeQuery(sql, ExperienciaLaboral.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * Recupera la lista de Experiencias Laborales
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ExperienciaLaboralLegajo> buscarExperienciaLaboralCIO() {

		try {
			String sql = "select exp.*  "
					+ "from legajo.experiencia_laboral exp "
					+ "join general.persona p  "
					+ "on p.id_persona = exp.id_persona "
					+ "where exp.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();
			System.out.println("SQL " + sql);
			return em.createNativeQuery(sql, ExperienciaLaboralLegajo.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Recupera la lista de ReprPersonaDiscapacidad
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ReprPersonaDiscapacidad> buscarReprPersonaDiscapacidad() {
		try {
			String sql = "select repr.*  "
					+ "from seleccion.repr_persona_discapacidad repr "
					+ "join general.persona p  "
					+ "on p.id_persona = repr.id_persona "
					+ "where repr.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();

			return em.createNativeQuery(sql, ReprPersonaDiscapacidad.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * Recupera la lista de ReprPersonaDiscapacidad
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ReprPersonaDiscapacidadLegajo> buscarReprPersonaDiscapacidadCIO() {
		try {
			String sql = "select repr.*  "
					+ "from seleccion.repr_persona_discapacidad repr "
					+ "join general.persona p  "
					+ "on p.id_persona = repr.id_persona "
					+ "where repr.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();

			return em.createNativeQuery(sql, ReprPersonaDiscapacidadLegajo.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	private List<Documentos> buscarDocumentos() {
		try {
			String cSepardor = "%";

			String sql = "select docs.*  "
					+ "from general.documentos docs"
					+ " where docs.activo is true "
					+ "and upper(docs.ubicacion_fisica) like upper('"
					+ cSepardor
					+ "SICCA"
					+ cSepardor
					+ "Usuario_Portal"
					+ cSepardor
					+ postulacionHome.getInstance().getPersonaPostulante()
							.getPersona().getDocumentoIdentidad()
					+ "_"
					+ postulacionHome.getInstance().getPersonaPostulante()
							.getPersona().getIdPersona() + "')";
			System.out.println("EL QUERY :" + sql);
			return em.createNativeQuery(sql, Documentos.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Documentos> buscarDocumentosCIO() {
		try {
			String cSepardor = "%";

			String sql = "select docs.*  "
					+ "from general.documentos docs"
					+ " where docs.activo is true "
					+ "and upper(docs.ubicacion_fisica) like upper('"
					+ cSepardor
					+ "SICCA"
					+ cSepardor
					+ "Usuario_Portal"
					+ cSepardor
					+ postulacionHome.getInstance().getPersonaPostulante()
							.getPersona().getDocumentoIdentidad()
					+ "_"
					+ postulacionHome.getInstance().getPersonaPostulante()
							.getPersona().getIdPersona() + "')";
			System.out.println("EL QUERY :" + sql);
			return em.createNativeQuery(sql, Documentos.class).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Copia la lista de experiencias laborales, asignandole un postulante
	 * 
	 * @return
	 * @throws Exception
	 */
	private String copiarExperienciaLaboral() throws Exception {
		try {
			for (int i = 0; i < listaExperienciaLaboral.size(); i++) {
				ExperienciaLaboral experiencia = new ExperienciaLaboral();
				experiencia = listaExperienciaLaboral.get(i);
				if (true) {
					ExperienciaLaboral actual = new ExperienciaLaboral();
					actual.setActivo(experiencia.getActivo());
					actual.setEmpresa(experiencia.getEmpresa());
					actual.setFechaAlta(new Date());
					actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					actual.setFechaHasta(experiencia.getFechaHasta());
					actual.setFechaDesde(experiencia.getFechaDesde());
					actual.setFuncionesRealizadas(experiencia
							.getFuncionesRealizadas());
					actual.setPersonaPostulante(personaPostulanteHome
							.getInstance());
					actual.setPosicion(experiencia.getPosicion());
					actual.setReferenciasLaborales(experiencia
							.getReferenciasLaborales());
					actual.setPuestoCargo(experiencia.getPuestoCargo());// MODIFICADO
																		// RV
					em.persist(actual);

				}
			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}
	
	/**
	 * Copia la lista de experiencias laborales, asignandole un postulante
	 * 
	 * @return
	 * @throws Exception
	 */
	private String copiarExperienciaLaboralCIO() throws Exception {
		try {
			for (int i = 0; i < listaExperienciaLaboralLegajo.size(); i++) {
				ExperienciaLaboralLegajo experiencia = new ExperienciaLaboralLegajo();
				experiencia = listaExperienciaLaboralLegajo.get(i);
				if (true) {
					ExperienciaLaboral actual = new ExperienciaLaboral();
					actual.setActivo(experiencia.getActivo());
					actual.setEmpresa(experiencia.getEmpresa());
					actual.setFechaAlta(new Date());
					actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					actual.setFechaHasta(experiencia.getFechaHasta());
					actual.setFechaDesde(experiencia.getFechaDesde());
					actual.setFuncionesRealizadas(experiencia
							.getFuncionesRealizadas());
					actual.setPersonaPostulante(personaPostulanteHome
							.getInstance());
					actual.setPosicion(experiencia.getPosicion());
					actual.setReferenciasLaborales(experiencia
							.getReferenciasLaborales());
					actual.setPuestoCargo(experiencia.getPuestoCargo());// MODIFICADO
																		// RV
					em.persist(actual);

				}
			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}

	private String copiarReprPersonaDiscapacidad() throws Exception {
		try {
			for (ReprPersonaDiscapacidad o : listaReprPersonaDiscapacidad) {
				ReprPersonaDiscapacidad entidad = new ReprPersonaDiscapacidad();
				entidad.setActivo(true);
				entidad.setFechaAlta(new Date());
				entidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				entidad.setObservacion(o.getObservacion());
				entidad.setPersonaPostulante(personaPostulanteHome
						.getInstance());
				entidad.setPersonaRep(new Persona());
				entidad.getPersonaRep().setIdPersona(
						o.getPersonaRep().getIdPersona());
				em.persist(entidad);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}
	
	private String copiarReprPersonaDiscapacidadCIO() throws Exception {
		try {
			for (ReprPersonaDiscapacidadLegajo o : listaReprPersonaDiscapacidadLegajo) {
				ReprPersonaDiscapacidad entidad = new ReprPersonaDiscapacidad();
				entidad.setActivo(true);
				entidad.setFechaAlta(new Date());
				entidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				entidad.setObservacion(o.getObservacion());
				entidad.setPersonaPostulante(personaPostulanteHome.getInstance());
				entidad.setPersonaRep(new Persona());
				entidad.getPersonaRep().setIdPersona(o.getPersonaRep().getIdPersona());
				em.persist(entidad);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}

	private String generarUbiFisica() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String ubiFisica = "";
		String separador = File.separator;

		ubiFisica = separador + "SICCA" + separador + sdf.format(new Date())
				+ separador + "OEE" + separador;
		ubiFisica += concursoPuestoAgr.getConcurso().getConfiguracionUoCab()
				.getIdConfiguracionUo()
				+ separador;
		ubiFisica += concursoPuestoAgr.getConcurso()
				.getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ separador;
		ubiFisica += concursoPuestoAgr.getConcurso().getIdConcurso()
				+ separador;
		ubiFisica += concursoPuestoAgr.getIdConcursoPuestoAgr() + separador;
		ubiFisica += "POSTULANTES" + separador;

		return ubiFisica;
	}

	private String copiarIdDocumentos() throws Exception {
		try {
			for (Documentos o : listaDocumentosInsertados) {
				PostulacionAdjuntos entidad = new PostulacionAdjuntos();
				entidad.setActivo(true);
				entidad.setDocumento(new Documentos());
				entidad.getDocumento().setIdDocumento(o.getIdDocumento());
				entidad.setFechaAlta(new Date());
				entidad.setPostulacion(new Postulacion());
				entidad.getPostulacion().setIdPostulacion(
						postulacion.getIdPostulacion());
				entidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(entidad);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}

	private String copiarDocumentos() throws Exception {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			listaDocumentosInsertados = new ArrayList<Documentos>();
			int ultIndice = 0;
			Documentos entidad = null;
			String ubiFisica = null;
			for (Documentos o : listaDocumentos) {
				listaDocumentosInsertados.add(new Documentos());
				ultIndice = listaDocumentosInsertados.size() - 1;
				entidad = listaDocumentosInsertados.get(ultIndice);
				entidad.setActivo(true);
				entidad.setAnhoDocumento(o.getAnhoDocumento());
				entidad.setConcurso(o.getConcurso());
				entidad.setDatosEspecificos(o.getDatosEspecificos());
				entidad.setDescripcion(o.getDescripcion());
				entidad.setFechaAlta(new Date());
				entidad.setIdTabla(personaPostulanteHome.getInstance()
						.getIdPersonaPostulante());
				entidad.setMimetype(o.getMimetype());
				entidad.setNombreFisicoDoc(o.getNombreFisicoDoc());
				entidad.setNombreLogDoc(o.getNombreLogDoc());
				entidad.setNombrePantalla(o.getNombrePantalla());
				entidad.setNombreTabla("persona_postulante");
				entidad.setNroDocumento(o.getNroDocumento());
				entidad.setTamanhoDoc(o.getTamanhoDoc());
				ubiFisica = generarUbiFisica();
				ubiFisica += postulacionHome.getInstance()
						.getPersonaPostulante().getPersona()
						.getDocumentoIdentidad()
						+ "_"
						+ postulacionHome.getInstance().getPersonaPostulante()
								.getPersona().getIdPersona();
				ubiFisica = calcPrefijoUnidad() + ubiFisica;
				entidad.setUbicacionFisica(ubiFisica);
				entidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(entidad);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}

	/**
	 * Recupera la lista de Persona Discapacidad
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<PersonaDiscapacidad> buscarPersonaDiscapacidad() {
		try {
			String sql = "select disc.*  "
					+ "from seleccion.persona_discapacidad disc "
					+ "join general.persona p  "
					+ "on p.id_persona = disc.id_persona "
					+ "where disc.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();

			return em.createNativeQuery(sql, PersonaDiscapacidad.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}
	
	/**
	 * Recupera la lista de Persona Discapacidad
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<PersonaDiscapacidadLegajo> buscarPersonaDiscapacidadCIO() {
		try {
			String sql = "select disc.*  "
					+ "from legajo.persona_discapacidad disc "
					+ "join general.persona p  "
					+ "on p.id_persona = disc.id_persona "
					+ "where disc.activo is true " + "and p.id_persona = "
					+ persona.getIdPersona();

			return em.createNativeQuery(sql, PersonaDiscapacidadLegajo.class)
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * Copia la lista de personas discapacidad, asignandole un postulante
	 * 
	 * @return
	 * @throws Exception
	 */
	private String copiarPersonaDiscapacidad() throws Exception {
		try {
			for (int i = 0; i < listaPersonasDiscapacidad.size(); i++) {
				PersonaDiscapacidad personaDiscapacidad = new PersonaDiscapacidad();
				personaDiscapacidad = listaPersonasDiscapacidad.get(i);
				PersonaDiscapacidad actual = new PersonaDiscapacidad();
				actual.setActividadRealizar(personaDiscapacidad
						.getActividadRealizar());
				actual.setActivo(true);
				actual.setCausa(personaDiscapacidad.getCausa());
				actual.setDatosEspecificosByIdDatosEspecificosDiscapac(personaDiscapacidad
						.getDatosEspecificosByIdDatosEspecificosDiscapac());
				actual.setDatosEspecificosByIdDatosEspecificosGradoAutonom(personaDiscapacidad
						.getDatosEspecificosByIdDatosEspecificosGradoAutonom());
				actual.setDificultadActividad(personaDiscapacidad
						.getDificultadActividad());
				actual.setFechaAlta(new Date());
				actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				actual.setFechaEmision(personaDiscapacidad.getFechaEmision());
				actual.setGrado(personaDiscapacidad.getGrado());
				actual.setNroCertificado(personaDiscapacidad
						.getNroCertificado());
				actual.setPersonaPostulante(personaPostulanteHome.getInstance());
				em.persist(actual);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}
	
	/**
	 * Copia la lista de personas discapacidad, asignandole un postulante
	 * 
	 * @return
	 * @throws Exception
	 */
	private String copiarPersonaDiscapacidadCIO() throws Exception {
		try {
			for (int i = 0; i < listaPersonasDiscapacidadLegajo.size(); i++) {
				PersonaDiscapacidadLegajo personaDiscapacidad = new PersonaDiscapacidadLegajo();
				personaDiscapacidad = listaPersonasDiscapacidadLegajo.get(i);
				PersonaDiscapacidad actual = new PersonaDiscapacidad();
				actual.setActividadRealizar(personaDiscapacidad
						.getActividadRealizar());
				actual.setActivo(true);
				actual.setCausa(personaDiscapacidad.getCausa());
				actual.setDatosEspecificosByIdDatosEspecificosDiscapac(personaDiscapacidad
						.getDatosEspecificosByIdDatosEspecificosDiscapac());
				actual.setDatosEspecificosByIdDatosEspecificosGradoAutonom(personaDiscapacidad
						.getDatosEspecificosByIdDatosEspecificosGradoAutonom());
				actual.setDificultadActividad(personaDiscapacidad
						.getDificultadActividad());
				actual.setFechaAlta(new Date());
				actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				actual.setFechaEmision(personaDiscapacidad.getFechaEmision());
				actual.setGrado(personaDiscapacidad.getGrado());
				actual.setNroCertificado(personaDiscapacidad
						.getNroCertificado());
				actual.setPersonaPostulante(personaPostulanteHome.getInstance());
				em.persist(actual);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("");
		}

	}

	/**
	 * Incidencia 0001706
	 */
	public void ocultarModal() {
		mensajeModal = null;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getInteresDescripcion() {
		return interesDescripcion;
	}

	public void setInteresDescripcion(String interesDescripcion) {
		this.interesDescripcion = interesDescripcion;
	}

	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}

	public void setPersonaPostulante(PersonaPostulante personaPostulante) {
		this.personaPostulante = personaPostulante;
	}

	public Postulacion getPostulacion() {
		return postulacion;
	}

	public void setPostulacion(Postulacion postulacion) {
		this.postulacion = postulacion;
	}

	public List<EstudiosRealizados> getListaEstudiosRealizados() {
		return listaEstudiosRealizados;
	}

	public void setListaEstudiosRealizados(
			List<EstudiosRealizados> listaEstudiosRealizados) {
		this.listaEstudiosRealizados = listaEstudiosRealizados;
	}

	public List<IdiomasPersona> getListaIdiomas() {
		return listaIdiomas;
	}

	public void setListaIdiomas(List<IdiomasPersona> listaIdiomas) {
		this.listaIdiomas = listaIdiomas;
	}

	public List<ExperienciaLaboral> getListaExperienciaLaboral() {
		return listaExperienciaLaboral;
	}

	public void setListaExperienciaLaboral(
			List<ExperienciaLaboral> listaExperienciaLaboral) {
		this.listaExperienciaLaboral = listaExperienciaLaboral;
	}

	public List<PersonaDiscapacidad> getListaPersonasDiscapacidad() {
		return listaPersonasDiscapacidad;
	}

	public void setListaPersonasDiscapacidad(
			List<PersonaDiscapacidad> listaPersonasDiscapacidad) {
		this.listaPersonasDiscapacidad = listaPersonasDiscapacidad;
	}

	public List<String> getListaDocsNoPresentados() {
		return listaDocsNoPresentados;
	}

	public void setListaDocsNoPresentados(List<String> listaDocsNoPresentados) {
		this.listaDocsNoPresentados = listaDocsNoPresentados;
	}

	public String getMsjError() {
		return msjError;
	}

	public void setMsjError(String msjError) {
		this.msjError = msjError;
	}

	public String getMensajeModal() {
		return mensajeModal;
	}

	public void setMensajeModal(String mensajeModal) {
		this.mensajeModal = mensajeModal;
	}

	public Boolean getMostrarOkyCancelar() {
		return mostrarOkyCancelar;
	}

	public void setMostrarOkyCancelar(Boolean mostrarOkyCancelar) {
		this.mostrarOkyCancelar = mostrarOkyCancelar;
	}

	public String getDirPostulacion() {
		return dirPostulacion;
	}

	public void setDirPostulacion(String dirPostulacion) {
		this.dirPostulacion = dirPostulacion;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public boolean isValido() {
		return valido;
	}

	public Long getIdGrupoAcctionListener() {
		return idGrupoAcctionListener;
	}

	public void setIdGrupoAcctionListener(Long idGrupoAcctionListener) throws Exception {
		this.idGrupoAcctionListener = idGrupoAcctionListener;
		permitirPostular(this.idGrupoAcctionListener);
	}

	public Boolean getFaltanDocs() {
		return faltanDocs;
	}

	public void setFaltanDocs(Boolean faltanDocs) {
		this.faltanDocs = faltanDocs;
	}

	public List<String> getDocsFaltantesList() {
		return docsFaltantesList;
	}

	public void setDocsFaltantesList(List<String> docsFaltantesList) {
		this.docsFaltantesList = docsFaltantesList;
	}

	public List<String> getDocsNoObligatoriosFaltantesList() {
		return docsNoObligatoriosFaltantesList;
	}

	public void setDocsNoObligatoriosFaltantesList(
			List<String> docsNoObligatoriosFaltantesList) {
		this.docsNoObligatoriosFaltantesList = docsNoObligatoriosFaltantesList;
	}

	public Boolean getFaltanDocsNoObligatorios() {
		return faltanDocsNoObligatorios;
	}

	public void setFaltanDocsNoObligatorios(Boolean faltanDocsNoObligatorios) {
		this.faltanDocsNoObligatorios = faltanDocsNoObligatorios;
	}

	public boolean isTieneParientes() {
		return tieneParientes;
	}

	public void setTieneParientes(boolean tieneParientes) {
		this.tieneParientes = tieneParientes;
	}

	public List<String> getDocsAgrupadosFaltantesList() {
		return docsAgrupadosFaltantesList;
	}

	public void setDocsAgrupadosFaltantesList(
			List<String> docsAgrupadosFaltantesList) {
		this.docsAgrupadosFaltantesList = docsAgrupadosFaltantesList;
	}

	public Boolean getFaltanDocsAgrupados() {
		return faltanDocsAgrupados;
	}

	public void setFaltanDocsAgrupados(Boolean faltanDocsAgrupados) {
		this.faltanDocsAgrupados = faltanDocsAgrupados;
	}
	
	public Boolean getEstaEnRangoFechaPostu(){
		return estaEnRangoFechaPostu;
	}
	
	public void setEstaEnRangoFechaPostu(Boolean estaEnRangoFechaPostu) {
		this.estaEnRangoFechaPostu = estaEnRangoFechaPostu;
	}
	
	
	public String getFechaPostulacionDesde(){
		if (fechaPostulacionDesde == null)
			return "";
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return sdf.format(fechaPostulacionDesde);
		}
	}
	
	public void setFechaPostulacionDesde(Date fechaPostulacionDesde) {
		this.fechaPostulacionDesde = fechaPostulacionDesde;
	}	
	
	public String getFechaPostulacionHasta(){
		if (fechaPostulacionHasta == null)
			return "";
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return sdf.format(fechaPostulacionHasta);
		}
	}
	
	public void setFechaPostulacionHasta(Date fechaPostulacionHasta) {
		this.fechaPostulacionHasta = fechaPostulacionHasta;
	}

	public Boolean getFaltaEvaluacionDesempenho() {
		return faltaEvaluacionDesempenho;
	}

	public void setFaltaEvaluacionDesempenho(Boolean faltaEvaluacionDesempenho) {
		this.faltaEvaluacionDesempenho = faltaEvaluacionDesempenho;
	}

	public Boolean getNecesitaEvaluacionDesempenho() {
		return necesitaEvaluacionDesempenho;
	}

	public void setNecesitaEvaluacionDesempenho(Boolean necesitaEvaluacionDesempenho) {
		this.necesitaEvaluacionDesempenho = necesitaEvaluacionDesempenho;
	}

	public List<String> getDocsPresentados() {
		return docsPresentados;
	}

	public void setDocsPresentados(List<String> docsPresentados) {
		this.docsPresentados = docsPresentados;
	}

	public List<String> getDocsSolicitados() {
		return docsSolicitados;
	}

	public void setDocsSolicitados(List<String> docsSolicitados) {
		this.docsSolicitados = docsSolicitados;
	}

	public List<String> getDocsSolicitadosNoObligatorios() {
		return docsSolicitadosNoObligatorios;
	}

	public void setDocsSolicitadosNoObligatorios(
			List<String> docsSolicitadosNoObligatorios) {
		this.docsSolicitadosNoObligatorios = docsSolicitadosNoObligatorios;
	}

	public List<String> getDocsSolicitadosAgrupados() {
		return docsSolicitadosAgrupados;
	}

	public void setDocsSolicitadosAgrupados(List<String> docsSolicitadosAgrupados) {
		this.docsSolicitadosAgrupados = docsSolicitadosAgrupados;
	}	

}
