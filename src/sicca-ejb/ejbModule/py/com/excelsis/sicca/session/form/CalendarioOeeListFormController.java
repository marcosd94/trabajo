package py.com.excelsis.sicca.session.form;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.CalendarioOeeCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CalendarioOeeCabList;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("calendarioOeeListFormController")
public class CalendarioOeeListFormController implements Serializable{

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CalendarioOeeCabList calendarioOeeCabList;
	CalendarioOeeCab calendarioOeeCab;
	ConfiguracionUoCab configuracionUoCab;
	Integer anho;
	
	public void init() {
		configuracionUoCab = new ConfiguracionUoCab();
		Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
		searchAll();
		
	}
	
	public void searchAll() {
		
		calendarioOeeCabList.setId(configuracionUoCab.getIdConfiguracionUo());
		anho = null;
		calendarioOeeCabList.limpiar();

	}

	
	public void search(){
		calendarioOeeCabList.setAnho(anho);
		calendarioOeeCabList.setId(configuracionUoCab.getIdConfiguracionUo());
		calendarioOeeCabList.listaResultados();
	}
	
	public CalendarioOeeCab getCalendarioOeeCab() {
		return calendarioOeeCab;
	}

	public void setCalendarioOeeCab(CalendarioOeeCab calendarioOeeCab) {
		this.calendarioOeeCab = calendarioOeeCab;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}
	
	
	
}
