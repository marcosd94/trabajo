package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("tab7VistaPrePostulacionActualFC")
public class Tab7VistaPrePostulacionActualFC implements Serializable {

	
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(required = false)
	Usuario usuarioLogueado;

	@RequestParameter
	Long concursoPuestoAgrIdConcursoPuestoAgr;
	
	@RequestParameter
	Long personaIdPersona;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PersonaList personaList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;


	private PersonaPostulante persona = new PersonaPostulante();
	private List<EstudiosRealizados> estudiosRealizadosList = new ArrayList<EstudiosRealizados>();
	private List<IdiomasPersona> idiomasPersonas = new ArrayList<IdiomasPersona>();
	private List<ExperienciaLaboral> experienciaLaboralsList = new ArrayList<ExperienciaLaboral>();
	private List<PersonaDiscapacidad> personaDiscapacidads = new ArrayList<PersonaDiscapacidad>();
	private List<ReprPersonaDiscapacidad> reprPersonaDiscapacidadList =
		new ArrayList<ReprPersonaDiscapacidad>();
	
	private String fromCU;
	private Long idPostulacion;
	private Long idPersonaPostulante;
	private Long idPersona;
	private Long idConcursoPuestoAgr;
	

	/**
	 * ES LLAMADO DESDE LAS FICHAS
	 * */
	public void init() {
		try {
			/**
			 * validacion 
			 * */
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
			.getInstance(SeguridadUtilFormController.class, true);
			if (personaIdPersona != null) {
				if (!sufc.validarInput(personaIdPersona.toString(),
						TiposDatos.LONG.getValor(), null))
					return;
				idPersona=personaIdPersona;
			}
			if (concursoPuestoAgrIdConcursoPuestoAgr != null) {
				if (!sufc.validarInput(concursoPuestoAgrIdConcursoPuestoAgr.toString(),
						TiposDatos.LONG.getValor(), null))
					return;
				idConcursoPuestoAgr=concursoPuestoAgrIdConcursoPuestoAgr;
			}
			/**
			 * buscar en la tabla POSTULACION para obtener el id_persona_postulante. 
			 * */
			Postulacion p=seleccionUtilFormController.getPostulacion(idConcursoPuestoAgr, idPersona);
			if(p.getPersonaPostulante()!=null){
				idPersonaPostulante=p.getPersonaPostulante().getIdPersonaPostulante();
				findPersonaPostulante();
				inicializarDatos();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void inicializarDatos(){
		findPersonaPostulante();
		findEstudiosRealizados();
		findIdiomasPerson();
		findExperiencias();
		findDiscapacidad();
		findRepresentante();
	}
	
	/**
	 * PARA CUANDO ES LLAMADO DESDE OTRO cu
	 * @throws Exception 
	 * **/
	public void init2() throws Exception{
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
		.getInstance(SeguridadUtilFormController.class, true);
		if (idPersonaPostulante != null) {
			if (!sufc.validarInput(idPersonaPostulante.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		findPersonaPostulante();
		inicializarDatos();

	}
	
	
	public void findPersonaPostulante(){
		persona=em.find(PersonaPostulante.class,idPersonaPostulante);
	}
	

	public String estadoCivil(String elEstadoCorto) {
		Query q =
			em.createQuery("select referencias from Referencias referencias "
				+ " where referencias.dominio = 'ESTADO_CIVIL' AND referencias.descAbrev = '"
				+ elEstadoCorto + "'");
		List<Referencias> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0).getDescLarga();
		} else {
			return "-";
		}
	}

	public void pdf() {
		try {

			Connection conn = JpaResourceBean.getConnection();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("idPersona", usuarioLogueado.getPersona().getIdPersona().longValue());
			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("REPORT_CONNECTION", conn);
			 
		
			
			if (experienciaLaboralsList.size() == 0) {
				param.put("sinDetalle", "si");
			} else
				param.put("sinDetalle", "no");

			param.put("datosPersonales", "si");
			param.put("whereExLab", " where es.ID_PERSONA_POSTULANTE = "
					+ persona.getIdPersonaPostulante()
					+ " and es.activo = true");
			param.put("whereIdioma",
					" WHERE IDIOMAS_PERSONA.ID_PERSONA_POSTULANTE  = "
							+ persona.getIdPersonaPostulante()
							+ " AND IDIOMAS_PERSONA.ACTIVO= TRUE");
			param.put("whereExProfe",
					" WHERE  EXPERIENCIA_LABORAL.ID_PERSONA_POSTULANTE  = "
							+ persona.getIdPersonaPostulante()
							+ " and EXPERIENCIA_LABORAL.activo = true");
			param.put("whereDiscapacidad",
					" WHERE PERSONA_DISCAPACIDAD.ID_PERSONA_POSTULANTE  = "
							+ persona.getIdPersonaPostulante()
							+ " and PERSONA_DISCAPACIDAD.activo = true");
			param.put(
					"whereRepresentate",
					" WHERE pd.ID_PERSONA_POSTULANTE  = "
							+ persona.getIdPersonaPostulante()
							+ " and pd.activo = true");

			param.put("nombreCompleto", persona.getNombreCompleto());

			param.put("sql", getQueryDatosPersonales(idPersonaPostulante)
					.toString());

			JasperReportUtils.respondPDF("RPT_CU239_curriculum", param, false, conn, usuarioLogueado);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// METODOS PRIVADO
	
	
	
	private String getQueryDatosPersonales(Long idPersonaPostulante){
		 StringBuffer sql = new StringBuffer(); 
		sql.append(" SELECT 	PERSONA.NOMBRES ||' '|| PERSONA.APELLIDOS as nombreCompleto ");
		sql.append(",	PERSONA.DOCUMENTO_IDENTIDAD AS documentoIdentidad ");
		sql.append(",	PERSONA.FECHA_NACIMIENTO AS fechaNac  ");
		sql.append(" ,	CIUDAD_NAC.DESCRIPCION AS lugarNacimiento  ");
		sql.append(" ,	DATOS_ESPECIFICOS.DESCRIPCION AS nacionalidad  ");
		sql.append(" , 	PERSONA.CALLE_PRINCIPAL AS direcParticular  ");
		sql.append(" ,	CIUDAD_DIR.DESCRIPCION AS ciudad  ");
		sql.append(" ,	DEPARTAMENTO.DESCRIPCION AS departamento  ");
		sql.append(" ,	PERSONA.TELEFONOS AS telefonos  ");
		sql.append(" ,	PERSONA.TELEFONO_PART AS telef_part");
		sql.append(" ,	PERSONA.TELEFONO_LAB AS telef_lab"); 
		sql.append(" ,	PERSONA.E_MAIL AS email  ");
		sql.append(" ,	PERSONA.SEXO  AS sexo  ");
		sql.append(" ,	PAIS.DESCRIPCION AS pais  ");
		sql.append(" ,	(select referencias.desc_larga from seleccion.referencias referencias where referencias.dominio  ");
		sql.append(" = 'ESTADO_CIVIL' AND referencias.desc_abrev = PERSONA.ESTADO_CIVIL  )  AS estadoCivil  ");
		sql.append(" FROM 	SELECCION.PERSONA_POSTULANTE PERSONA  ");
		sql.append(" LEFT OUTER JOIN 	GENERAL.CIUDAD CIUDAD_NAC ON		CIUDAD_NAC.ID_CIUDAD = PERSONA.ID_CIUDAD_NAC  ");
		sql.append(" LEFT OUTER JOIN 	GENERAL.CIUDAD CIUDAD_DIR ON		CIUDAD_DIR.ID_CIUDAD = PERSONA.ID_CIUDAD_DIRECC  ");
		sql.append(" LEFT OUTER JOIN 	GENERAL.DEPARTAMENTO ON	DEPARTAMENTO.ID_DEPARTAMENTO = CIUDAD_DIR.ID_DEPARTAMENTO  ");
		sql.append(" JOIN 	GENERAL.PAIS ON	 PAIS.ID_PAIS = PERSONA.ID_PAIS_EXPEDICION_DOC  ");
		sql.append(" JOIN    SELECCION.DATOS_ESPECIFICOS ON DATOS_ESPECIFICOS.ID_DATOS_ESPECIFICOS = PERSONA.ID_DATOS_ESPECIFICOS_NACIONALID  ");
		sql.append(" WHERE PERSONA.ID_PERSONA_POSTULANTE =  "+idPersonaPostulante.longValue());
		
		return sql.toString();

	}
	
	
	@SuppressWarnings("unchecked")
	private void findEstudiosRealizados() {
		estudiosRealizadosList = em
				.createQuery(
						" select e from EstudiosRealizados e"
						+ "  where e.personaPostulante.idPersonaPostulante=:idPersonaPostulante  and e.activo=true ")
				.setParameter("idPersonaPostulante", idPersonaPostulante)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findIdiomasPerson() {
		idiomasPersonas =
			em.createQuery("Select i from IdiomasPersona i where i.personaPostulante.idPersonaPostulante=:idPersonaPostulante"
				+ " AND i.activo = true ").setParameter("idPersonaPostulante", idPersonaPostulante).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findExperiencias() {
		experienciaLaboralsList =
			em.createQuery(" select e from ExperienciaLaboral e where e.personaPostulante.idPersonaPostulante=:idPersonaPostulante"
				 + " and e.activo = true ").setParameter("idPersonaPostulante", idPersonaPostulante).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findDiscapacidad() {
		personaDiscapacidads =
			em.createQuery(" select p from PersonaDiscapacidad p where  p.personaPostulante.idPersonaPostulante=:idPersonaPostulante "
			+ " and p.activo =true").setParameter("idPersonaPostulante", idPersonaPostulante).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findRepresentante() {
		reprPersonaDiscapacidadList =
			em.createQuery(" select p from ReprPersonaDiscapacidad p "
				+ " where p.personaPostulante.idPersonaPostulante=:idPersonaPostulante " +
						" and p.activo = true").setParameter("idPersonaPostulante", idPersonaPostulante).getResultList();
	}

	// GETTERS Y SETTERS
	public List<EstudiosRealizados> getEstudiosRealizadosList() {
		return estudiosRealizadosList;
	}

	public void setEstudiosRealizadosList(List<EstudiosRealizados> estudiosRealizadosList) {
		this.estudiosRealizadosList = estudiosRealizadosList;
	}

	public List<IdiomasPersona> getIdiomasPersonas() {
		return idiomasPersonas;
	}

	public void setIdiomasPersonas(List<IdiomasPersona> idiomasPersonas) {
		this.idiomasPersonas = idiomasPersonas;
	}

	public List<ExperienciaLaboral> getExperienciaLaboralsList() {
		return experienciaLaboralsList;
	}

	public void setExperienciaLaboralsList(List<ExperienciaLaboral> experienciaLaboralsList) {
		this.experienciaLaboralsList = experienciaLaboralsList;
	}

	public List<PersonaDiscapacidad> getPersonaDiscapacidads() {
		return personaDiscapacidads;
	}

	public void setPersonaDiscapacidads(List<PersonaDiscapacidad> personaDiscapacidads) {
		this.personaDiscapacidads = personaDiscapacidads;
	}


	public List<ReprPersonaDiscapacidad> getReprPersonaDiscapacidadList() {
		return reprPersonaDiscapacidadList;
	}

	public void setReprPersonaDiscapacidadList(List<ReprPersonaDiscapacidad> reprPersonaDiscapacidadList) {
		this.reprPersonaDiscapacidadList = reprPersonaDiscapacidadList;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Long getIdPostulacion() {
		return idPostulacion;
	}

	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}

	public Long getIdPersonaPostulante() {
		return idPersonaPostulante;
	}

	public void setIdPersonaPostulante(Long idPersonaPostulante) {
		this.idPersonaPostulante = idPersonaPostulante;
	}

	public PersonaPostulante getPersona() {
		return persona;
	}

	public void setPersona(PersonaPostulante persona) {
		this.persona = persona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	
	
	
}
