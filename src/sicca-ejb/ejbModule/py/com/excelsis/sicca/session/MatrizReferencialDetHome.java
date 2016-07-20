package py.com.excelsis.sicca.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("matrizReferencialDetHome")
public class MatrizReferencialDetHome extends EntityHome<MatrizReferencialDet> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	MatrizReferencialEncHome matrizReferencialEncHome;

	public static final String CONTEXT_VAR_NAME = "matrizReferencialDet";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	public void setMatrizReferencialDetIdMatrizReferencialDet(Long id) {
		setId(id);
	}

	public Long getMatrizReferencialDetIdMatrizReferencialDet() {
		return (Long) getId();
	}

	@Override
	protected MatrizReferencialDet createInstance() {
		MatrizReferencialDet matrizReferencialDet = new MatrizReferencialDet();
		return matrizReferencialDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		MatrizReferencialEnc matrizReferencialEnc = matrizReferencialEncHome
				.getDefinedInstance();
		if (matrizReferencialEnc != null) {
			getInstance().setMatrizReferencialEnc(matrizReferencialEnc);
		}
	}

	public boolean isWired() {
		return true;
	}

	public MatrizReferencialDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

}
