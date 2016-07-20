package py.com.excelsis.sicca.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
@Entity
@Table(name = "parentesco", schema = "general")
public class Parentesco implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long idParentesco;
	private String nombres;
	private String apellidos;
	private String documentoIdentidad;
	private String institucion;
	private DatosEspecificos datosEspecificos;
	private Boolean activo;
	private Persona persona;
	
	
	public Parentesco(){
	
	
	}
	public Parentesco(Long idParentesco, String documentoIdentidad, String nombres, 
			String apellidos,String institucion, Boolean activo, DatosEspecificos datosEspecificos) {
		
		this.idParentesco = idParentesco;
		this.documentoIdentidad = documentoIdentidad;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.institucion = institucion;
		this.datosEspecificos = datosEspecificos;
		this.activo = activo;

	}
	
	@Id
	@Column(name = "id_parentesco", unique = true, nullable = false)
	@GeneratedValue(generator="PARENTESCO_GENERATOR")
	@SequenceGenerator(name="PARENTESCO_GENERATOR",sequenceName="general.parentesco_id_parentesco_seq")
	public Long getIdParentesco() {
		return this.idParentesco;
	}
	public void setIdParentesco(Long idParentesco) {
		this.idParentesco = idParentesco;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona" ) 
	public Persona getPersona() {
		return this.persona;
	}
	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	
	@Column(name = "nombres", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getNombres() {
		return this.nombres;
	}
	
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	
	@Column(name = "apellidos", nullable = false, length = 80)
	@NotNull
	@Length(max = 80)
	public String getApellidos() {
		return this.apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
		
	}
	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	@Column(name = "documento_identidad", nullable = false, length = 30)
	@NotNull
	@Length(max = 30)
	public String getDocumentoIdentidad() {
		return this.documentoIdentidad;
	}

	public void setDocumentoIdentidad(String documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}
	@Column(name = "institucion", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getInstitucion() {
		return this.institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;	
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos" ) 
	public DatosEspecificos getDatosEspecificos() {
		return this.datosEspecificos;
	}
	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}
	


}
