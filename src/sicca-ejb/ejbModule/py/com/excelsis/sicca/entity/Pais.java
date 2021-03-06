package py.com.excelsis.sicca.entity;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Pais generated by Generality
 */
@Entity
@Table(name = "pais", schema = "general")
public class Pais implements java.io.Serializable {
	private Long idPais;
	private String descripcion;
	private String usuAlta;
	private String usuMod;
	private Integer paisCodigoSinarh;
	private Date fechaMod;
	private Date fechaAlta;
	private Boolean activo;
	//ZD 03/08/15
	private String prefijo;

	private Set<InstitucionEducativa> institucionEducativas = new HashSet<InstitucionEducativa>(0);
	private Set<Departamento> departamentos= new HashSet<Departamento>(0);
	private Set<EstudiosRealizados> estudiosRealizados= new HashSet<EstudiosRealizados>(0);
	private Set<EstudiosRealizadosLegajo> estudiosRealizadosLegajo= new HashSet<EstudiosRealizadosLegajo>(0);

	private Set<ExperienciaLaboral> experienciaLaborals= new HashSet<ExperienciaLaboral>(0);
	private Set<ExperienciaLaboralLegajo> experienciaLaboralsLegajo= new HashSet<ExperienciaLaboralLegajo>(0);
	private Set<Persona> personas= new HashSet<Persona>(0);
	private Set<PersonaPostulante> personaPostulantes= new HashSet<PersonaPostulante>(0);
	
	
	
	
	
	public Pais() {
	}

	public Pais(Long idPais) {
		this.idPais = idPais;
	}

	@Id
	@GeneratedValue(generator="PAIS_GENERATOR")
	@SequenceGenerator(name="PAIS_GENERATOR",sequenceName="general.pais_id_pais_seq")

	@Column(name = "id_pais")
	public Long getIdPais() {
		return this.idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}
	

	@Column(name = "descripcion")
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	

	@Column(name = "usu_alta")
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}
	

	@Column(name = "usu_mod")
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}
	

	@Column(name = "pais_codigo_sinarh")
	public Integer getPaisCodigoSinarh() {
		return this.paisCodigoSinarh;
	}

	public void setPaisCodigoSinarh(Integer paisCodigoSinarh) {
		this.paisCodigoSinarh = paisCodigoSinarh;
	}
	
	@Column(name = "prefijo")
	public String getPrefijo() {
		return prefijo;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	public Set<InstitucionEducativa> getInstitucionEducativas() {
		return this.institucionEducativas;
	}

	public void setInstitucionEducativas(
			Set<InstitucionEducativa> institucionEducativas) {
		this.institucionEducativas = institucionEducativas;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	

	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPais == null) ? 0 : idPais.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pais other = (Pais) obj;
		if (idPais == null) {
			if (other.idPais != null)
				return false;
		} else if (!idPais.equals(other.idPais))
			return false;
		return true;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	public Set<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(Set<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	public Set<EstudiosRealizados> getEstudiosRealizados() {
		return estudiosRealizados;
	}

	public void setEstudiosRealizados(Set<EstudiosRealizados> estudiosRealizados) {
		this.estudiosRealizados = estudiosRealizados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	public Set<EstudiosRealizadosLegajo> getEstudiosRealizadosLegajo() {
		return estudiosRealizadosLegajo;
	}

	public void setEstudiosRealizadosLegajo(Set<EstudiosRealizadosLegajo> estudiosRealizadosLegajo) {
		this.estudiosRealizadosLegajo = estudiosRealizadosLegajo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	public Set<ExperienciaLaboral> getExperienciaLaborals() {
		return experienciaLaborals;
	}

	public void setExperienciaLaborals(Set<ExperienciaLaboral> experienciaLaborals) {
		this.experienciaLaborals = experienciaLaborals;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais")
	public Set<ExperienciaLaboralLegajo> getExperienciaLaboralsLegajo() {
		return experienciaLaboralsLegajo;
	}

	public void setExperienciaLaboralsLegajo(Set<ExperienciaLaboralLegajo> experienciaLaboralsLegajo) {
		this.experienciaLaboralsLegajo = experienciaLaboralsLegajo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paisByIdPaisExpedicionDoc")
	public Set<Persona> getPersonas() {
		return personas;
	}

	public void setPersonas(Set<Persona> personas) {
		this.personas = personas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paisExpedicionDoc")
	public Set<PersonaPostulante> getPersonaPostulantes() {
		return personaPostulantes;
	}

	public void setPersonaPostulantes(Set<PersonaPostulante> personaPostulantes) {
		this.personaPostulantes = personaPostulantes;
	}
	
	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}
	
}
