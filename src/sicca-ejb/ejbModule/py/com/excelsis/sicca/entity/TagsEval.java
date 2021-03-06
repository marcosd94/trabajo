package py.com.excelsis.sicca.entity;

// Generated 10/09/2012 04:57:06 PM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * TagsEval generated by hbm2java
 */
@Entity
@Table(name = "tags_eval", schema = "eval_desempeno")
public class TagsEval implements java.io.Serializable {

	private Long idTagEval;
	private String tag;
	private String descripcion;
	private String select;

	public TagsEval() {
	}

	public TagsEval(Long idTagEval, String tag, String descripcion) {
		this.idTagEval = idTagEval;
		this.tag = tag;
		this.descripcion = descripcion;
	}

	public TagsEval(Long idTagEval, String tag, String descripcion,
			String select) {
		this.idTagEval = idTagEval;
		this.tag = tag;
		this.descripcion = descripcion;
		this.select = select;
	}

	@Id
	@Column(name = "id_tag_eval", unique = true, nullable = false)
	public Long getIdTagEval() {
		return this.idTagEval;
	}

	public void setIdTagEval(Long idTagEval) {
		this.idTagEval = idTagEval;
	}

	@Column(name = "tag", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Column(name = "descripcion", nullable = false, length = 150)
	@NotNull
	@Length(max = 150)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "select_", length = 1000)
	@Length(max = 1000)
	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

}
