package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("transladoLineaConSolic710ListFC")
public class TransladoLineaConSolic710ListFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	TransladoLineaSinSolic711ListFC transladoLineaSinSolic711ListFC;
	 

	public void init() {
		if (transladoLineaSinSolic711ListFC == null) {
			transladoLineaSinSolic711ListFC =
				(TransladoLineaSinSolic711ListFC) Component.getInstance(TransladoLineaSinSolic711ListFC.class, true);
		}
		 
		transladoLineaSinSolic711ListFC.cuNro = "710";
		transladoLineaSinSolic711ListFC.init();
	}

	

}
