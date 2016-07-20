package py.com.excelsis.sicca.movilidadLaboral.session.form;
import java.util.Date;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.movilidadLaboral.session.EmpleadoMovilidadCabList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("puestoProvisorioComisionadoList748FC")
public class PuestoProvisorioComisionadoList748FC{
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	EmpleadoMovilidadCabList empleadoMovilidadCabList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	
	




	public void init() {
		cargarCabecera();
		search();
	}
	
	
	
	
	
	public void search(){
	    empleadoMovilidadCabList.setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
		empleadoMovilidadCabList.listaResultados748();
	}
	

	



	public void cargarCabecera(){
			//OEE
		
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
		
		
			
		
	}
	

	
	
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	
	
	

	
}
