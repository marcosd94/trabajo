package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

@Name("matrizReferencialEncHome")
public class MatrizReferencialEncHome extends EntityHome<MatrizReferencialEnc> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	MatrizReferencialHome matrizReferencialHome;
	@In(create = true)
	MatrizReferencialDetHome matrizReferencialDetHome;

	public static final String CONTEXT_VAR_NAME = "matrizReferencialEnc";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	public void setMatrizReferencialEncIdMatrizReferencialEnc(Long id) {
		setId(id);
	}

	public Long getMatrizReferencialEncIdMatrizReferencialEnc() {
		return (Long) getId();
	}

	@Override
	protected MatrizReferencialEnc createInstance() {
		MatrizReferencialEnc matrizReferencialEnc = new MatrizReferencialEnc();
		return matrizReferencialEnc;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
		MatrizReferencial matrizReferencial = matrizReferencialHome
				.getDefinedInstance();
		if (matrizReferencial != null) {
			getInstance().setMatrizReferencial(matrizReferencial);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificos() == null)
			return false;
		if (getInstance().getMatrizReferencial() == null)
			return false;
		return true;
	}

	public MatrizReferencialEnc getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<MatrizReferencialDet> getMatrizReferencialDets() {
		return getInstance() == null ? null
				: new ArrayList<MatrizReferencialDet>(getInstance()
						.getMatrizReferencialDets());
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
		if(!getInstance().getActivo()){
			inactivarDependientes();
		}
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
//	METODOS PRIVADOS
	private void inactivarDependientes(){
		List<MatrizReferencialDet> listDetails = new ArrayList<MatrizReferencialDet>(getInstance().getMatrizReferencialDets());
		for(MatrizReferencialDet detail : listDetails){
			detail.setActivo(Estado.INACTIVO.getValor());
			matrizReferencialDetHome.setInstance(detail);
			matrizReferencialDetHome.update();
			matrizReferencialDetHome.clearInstance();
		}
	}

}
