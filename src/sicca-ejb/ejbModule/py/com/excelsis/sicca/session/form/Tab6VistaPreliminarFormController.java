package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
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
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PersonaDiscapacidadHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("tab6VistaPreliminarFormController")
public class Tab6VistaPreliminarFormController implements Serializable {

	public List<Persona> getO() {
		return o;
	}

	public void setO(List<Persona> o) {
		this.o = o;
	}
	
	@RequestParameter
	Long personaIdPersona;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	PersonaDiscapacidadHome personaDiscapacidadHome;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PersonaList personaList;

	private List<Persona> o = new ArrayList<Persona>();
	private Persona persona = new Persona();
	private List<EstudiosRealizados> estudiosRealizadosList = new ArrayList<EstudiosRealizados>();
	private List<EstudiosRealizados> otrosEstudiosRealizadosList = new ArrayList<EstudiosRealizados>();
	private List<IdiomasPersona> idiomasPersonas = new ArrayList<IdiomasPersona>();
	private List<ExperienciaLaboral> experienciaLaboralsList = new ArrayList<ExperienciaLaboral>();
	private List<PersonaDiscapacidad> personaDiscapacidads = new ArrayList<PersonaDiscapacidad>();
	private List<ReprPersonaDiscapacidad> reprPersonaDiscapacidadList =
		new ArrayList<ReprPersonaDiscapacidad>();
	
	private String fromCU;
	private PersonaPostulante personaPostulante;
	private Long idPostulacion;

	public void init() {
		try {
			persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			o = new ArrayList<Persona>();
			o.add(persona);
			findDiscapacidad();
			findEstudiosRealizados();
			findOtrosEstudiosRealizados();
			findExperiencias();
			findIdiomasPerson();
			findRepresentante();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void initCarpeta() {
		try {
			persona = em.find(Persona.class, personaIdPersona);
			o = new ArrayList<Persona>();
			o.add(persona);
			findDiscapacidad();
			findEstudiosRealizados();
			findOtrosEstudiosRealizados();
			findExperiencias();
			findIdiomasPerson();
			findRepresentante();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * PARA CUANDO ES LLAMADO DESDE OTRO cu
	 * **/
	public void inicializar(){
		persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
	}
	
	public void findPos(){
		Postulacion postulacion= em.find(Postulacion.class, idPostulacion);
		personaPostulante=em.find(PersonaPostulante.class, postulacion.getPersonaPostulante().getIdPersonaPostulante());
		persona = em.find(Persona.class, personaPostulante.getPersona().getIdPersona());
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
			
			if(persona!=null&& persona.getIdPersona()!=null){
				Long idP = persona.getIdPersona();
				param.put("idPersona", persona.getIdPersona().longValue());
			}
			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("REPORT_CONNECTION", conn);
			 
		
			
			if (experienciaLaboralsList.size() == 0) {
				param.put("sinDetalle", "si");
			} else
				param.put("sinDetalle", "no");
			 StringBuffer sql = new StringBuffer(); 
			
			
			if(fromCU!=null){
				if(idPostulacion!=null)
					findPos();
				sql.append(" SELECT  	PERSONA_POST.NOMBRES ||' '|| PERSONA_POST.APELLIDOS as nombreCompleto");
				sql.append(" ,	PERSONA_POST.DOCUMENTO_IDENTIDAD  AS documentoIdentidad");
				sql.append(" ,	PERSONA_POST.FECHA_NACIMIENTO  AS fechaNac");
				sql.append(" ,	CIUDAD_NAC.DESCRIPCION  AS lugarNacimiento");
				sql.append(" ,	DATOS_ESPECIFICOS.DESCRIPCION AS nacionalidad");
				sql.append(" , 	PERSONA_POST.CALLE_PRINCIPAL  AS direcParticular");
				sql.append(" ,	CIUDAD_DIR.DESCRIPCION AS ciudad");
				sql.append(" ,	DEPARTAMENTO.DESCRIPCION  AS departamento");
				sql.append(" ,	PERSONA_POST.TELEFONOS  AS telefonos");
				sql.append(" ,	PERSONA_POST.TELEFONO_PART AS telef_part");
				sql.append(" ,	PERSONA_POST.TELEFONO_LAB AS telef_lab"); 
				sql.append(" ,	PERSONA_POST.E_MAIL AS email");
				sql.append(" ,	PERSONA_POST.SEXO AS sexo");
				sql.append(" ,	PAIS.DESCRIPCION  AS pais");
				sql.append(" ,	(select referencias.desc_larga from seleccion.referencias referencias where referencias.dominio = 'ESTADO_CIVIL'");
				sql.append(" AND referencias.desc_abrev = PERSONA_POST.ESTADO_CIVIL  ) AS estadoCivil");
				sql.append(" FROM	SELECCION.PERSONA_POSTULANTE PERSONA_POST");
				sql.append(" LEFT OUTER JOIN  	GENERAL.CIUDAD CIUDAD_NAC ON CIUDAD_NAC.ID_CIUDAD = PERSONA_POST.ID_CIUDAD_NAC ");
				sql.append(" LEFT OUTER JOIN 	GENERAL.CIUDAD CIUDAD_DIR ON 	CIUDAD_DIR.ID_CIUDAD = PERSONA_POST.ID_CIUDAD_DIRECC");
				sql.append(" LEFT OUTER JOIN 	GENERAL.DEPARTAMENTO DEPARTAMENTO ON DEPARTAMENTO.ID_DEPARTAMENTO = CIUDAD_DIR.ID_DEPARTAMENTO");
				sql.append(" JOIN  	GENERAL.PAIS PAIS ON	PAIS.ID_PAIS = PERSONA_POST.ID_PAIS_EXPEDICION_DOC");
				sql.append(" JOIN 	SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS ON DATOS_ESPECIFICOS.ID_DATOS_ESPECIFICOS = PERSONA_POST.ID_DATOS_ESPECIFICOS_NACIONALID");
				sql.append(" WHERE 	PERSONA_POST.ID_PERSONA_POSTULANTE ="+personaPostulante.getIdPersonaPostulante());
				
				param.put("sql",sql.toString());

				if(fromCU.equals("88"))
				{
					param.put("datosPersonales", "no");
					param.put("sinDetalle", "no");
					param.put("whereExLab", " where es.ID_PERSONA_POSTULANTE = "+personaPostulante.getIdPersonaPostulante()+" and es.activo = true and es.otro_nivel is null order by es.fecha_fin DESC" );
					param.put("whereExLabOtros", " where es.ID_PERSONA_POSTULANTE = "+personaPostulante.getIdPersonaPostulante()+" and es.activo = true and es.otro_nivel is not null" );
					param.put("whereIdioma", " WHERE IDIOMAS_PERSONA.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante()+" AND IDIOMAS_PERSONA.ACTIVO= TRUE");
					param.put("whereExProfe", " WHERE  EXPERIENCIA_LABORAL.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante()+" and EXPERIENCIA_LABORAL.activo = true order by EXPERIENCIA_LABORAL.fecha_desde DESC, EXPERIENCIA_LABORAL.fecha_hasta DESC");
					param.put("whereDiscapacidad", " WHERE PERSONA_DISCAPACIDAD.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante() +" and PERSONA_DISCAPACIDAD.activo = true");
					param.put("whereRepresentate", " WHERE pd.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante()+" and pd.activo = true");
					param.put("idPersona", personaPostulante.getPersona().getIdPersona().longValue());
					param.put("nombreCompleto", personaPostulante.getUsuAlta());
				}
				else if(fromCU.equals("402")){
					param.put("datosPersonales", "si");
					param.put("sinDetalle", "no");
					param.put("whereExLab", " where es.ID_PERSONA_POSTULANTE = "+personaPostulante.getIdPersonaPostulante()+" and es.activo = true and es.otro_nivel is null order by es.fecha_fin DESC" );
					param.put("whereExLabOtros", " where es.ID_PERSONA_POSTULANTE = "+personaPostulante.getIdPersonaPostulante()+" and es.activo = true and es.otro_nivel is not null" );
					param.put("whereIdioma", " WHERE IDIOMAS_PERSONA.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante()+" AND IDIOMAS_PERSONA.ACTIVO= TRUE");
					param.put("whereExProfe", " WHERE  EXPERIENCIA_LABORAL.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante()+" and EXPERIENCIA_LABORAL.activo = true order by EXPERIENCIA_LABORAL.fecha_desde DESC, EXPERIENCIA_LABORAL.fecha_hasta DESC");
					param.put("whereDiscapacidad", " WHERE PERSONA_DISCAPACIDAD.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante() +" and PERSONA_DISCAPACIDAD.activo = true");
					param.put("whereRepresentate", " WHERE pd.ID_PERSONA_POSTULANTE  = "+personaPostulante.getIdPersonaPostulante()+" and pd.activo = true");
					param.put("idPersona", personaPostulante.getPersona().getIdPersona().longValue());
					
					param.put("nombreCompleto",persona.getNombreCompleto());
				}
				
			}else{
				param.put("datosPersonales", "si");
				if(persona == null || persona.getIdPersona() == null){
					findPos();
					param.put("idPersona", persona.getIdPersona().longValue());
					
				}
				param.put("whereExLab", " WHERE es.ID_PERSONA = "+persona.getIdPersona()+" and es.activo = true and es.otro_nivel is null order by es.fecha_fin DESC");
				param.put("whereExLabOtros", " where es.ID_PERSONA = "+persona.getIdPersona()+" and es.activo = true and es.otro_nivel is not null" );
				param.put("whereIdioma", " WHERE IDIOMAS_PERSONA.ID_PERSONA = "+persona.getIdPersona()+" AND IDIOMAS_PERSONA.ACTIVO= TRUE");
				param.put("whereExProfe", " WHERE  EXPERIENCIA_LABORAL.ID_PERSONA = "+persona.getIdPersona()+" and EXPERIENCIA_LABORAL.activo = true order by EXPERIENCIA_LABORAL.fecha_desde DESC, EXPERIENCIA_LABORAL.fecha_hasta DESC");
				param.put("whereDiscapacidad", " WHERE PERSONA_DISCAPACIDAD.ID_PERSONA = "+persona.getIdPersona() +" and PERSONA_DISCAPACIDAD.activo = true");
				param.put("whereRepresentate", " WHERE pd.ID_PERSONA = "+persona.getIdPersona()+" and pd.activo = true");
				
				param.put("nombreCompleto",persona.getNombreCompleto());
				
				sql.append(" SELECT	PERSONA.NOMBRES ||' '|| PERSONA.APELLIDOS  as nombreCompleto ");
				sql.append(" ,	PERSONA.DOCUMENTO_IDENTIDAD AS documentoIdentidad ");
				sql.append(" ,	PERSONA.FECHA_NACIMIENTO AS fechaNac "); 
				sql.append(" ,	CIUDAD_NAC.DESCRIPCION AS lugarNacimiento"); 
				sql.append(" ,	DATOS_ESPECIFICOS.DESCRIPCION AS nacionalidad"); 
				sql.append(" , 	PERSONA.CALLE_PRINCIPAL AS direcParticular"); 
				sql.append(" ,	CIUDAD_DIR.DESCRIPCION AS ciudad"); 
				sql.append(" ,	DEPARTAMENTO.DESCRIPCION AS departamento"); 
				sql.append(" ,	PERSONA.TELEFONOS AS telefonos"); 
				sql.append(" ,	PERSONA.TELEFONO_PART AS telef_part");
				sql.append(" ,	PERSONA.TELEFONO_LAB AS telef_lab"); 
				sql.append(" ,	PERSONA.e_mail AS email"); 
				sql.append(" ,	PERSONA.SEXO AS sexo"); 
				sql.append(" ,	PAIS.DESCRIPCION AS pais"); 
				sql.append(" ,	(select referencias.desc_larga from seleccion.referencias referencias where referencias.dominio");
				sql.append("  = 'ESTADO_CIVIL' AND referencias.desc_abrev = PERSONA.ESTADO_CIVIL  ) AS estadoCivil"); 
				sql.append(" FROM general.persona PERSONA "); 
				sql.append(" LEFT OUTER JOIN general.ciudad CIUDAD_NAC ON	CIUDAD_NAC.ID_CIUDAD = PERSONA.ID_CIUDAD_NAC");
				sql.append(" LEFT OUTER JOIN general.ciudad CIUDAD_DIR ON	CIUDAD_DIR.ID_CIUDAD = PERSONA.ID_CIUDAD_DIRECC");
				sql.append(" LEFT OUTER JOIN general.departamento DEPARTAMENTO ON	DEPARTAMENTO.ID_DEPARTAMENTO = CIUDAD_DIR.ID_DEPARTAMENTO");
				sql.append(" JOIN general.pais PAIS ON PAIS.ID_PAIS = PERSONA.ID_PAIS_EXPEDICION_DOC");
				sql.append(" JOIN seleccion.datos_especificos DATOS_ESPECIFICOS ON	 DATOS_ESPECIFICOS.ID_DATOS_ESPECIFICOS = PERSONA.ID_DATOS_ESPECIFICOS_NACIONALID");
				sql.append(" WHERE	PERSONA.ID_PERSONA ="+persona.getIdPersona());
				
				param.put("sql",sql.toString());
			}

			JasperReportUtils.respondPDF("RPT_CU239_curriculum", param, false, conn, usuarioLogueado);
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// METODOS PRIVADO

	@SuppressWarnings("unchecked")
	private void findEstudiosRealizados() {
		estudiosRealizadosList =
			em.createQuery(" select e from EstudiosRealizados e " + " where e.persona.idPersona= "
				+ persona.getIdPersona() +" and e.activo=true and e.otroNivel is null order by e.fechaFin DESC").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private void findOtrosEstudiosRealizados() {
		otrosEstudiosRealizadosList =
			em.createQuery(" select e from EstudiosRealizados e " + " where e.persona.idPersona= "
				+ persona.getIdPersona() +" and e.activo=true and e.otroNivel is not null").getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findIdiomasPerson() {
		idiomasPersonas =
			em.createQuery("Select i from IdiomasPersona i " + " where i.persona.idPersona="
				+ persona.getIdPersona() + " AND i.activo = true ").getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findExperiencias() {
		experienciaLaboralsList =
			em.createQuery(" select e from ExperienciaLaboral e " + " where e.persona.idPersona="
				+ persona.getIdPersona() + " and e.activo = true order by e.fechaDesde DESC, e.fechaHasta DESC").getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findDiscapacidad() {
		personaDiscapacidads =
			em.createQuery(" select p from PersonaDiscapacidad p " + " where p.persona.idPersona ="
				+ persona.getIdPersona()+ " and p.activo =true").getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findRepresentante() {
		reprPersonaDiscapacidadList =
			em.createQuery(" select p from ReprPersonaDiscapacidad p "
				+ " where p.persona.idPersona =" + persona.getIdPersona() + " and p.activo = true").getResultList();
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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
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

	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}

	public void setPersonaPostulante(PersonaPostulante personaPostulante) {
		this.personaPostulante = personaPostulante;
	}

	public Long getIdPostulacion() {
		return idPostulacion;
	}

	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}

	public List<EstudiosRealizados> getOtrosEstudiosRealizadosList() {
		return otrosEstudiosRealizadosList;
	}

	public void setOtrosEstudiosRealizadosList(
			List<EstudiosRealizados> otrosEstudiosRealizadosList) {
		this.otrosEstudiosRealizadosList = otrosEstudiosRealizadosList;
	}

	
	
	
}
