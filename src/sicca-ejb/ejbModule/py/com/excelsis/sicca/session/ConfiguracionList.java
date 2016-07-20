package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.Configuracion;

@Name("configuracionList")
public class ConfiguracionList extends EntityQuery<Configuracion> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Configuracion> listaConfiguracions;
	
	private static final String EJBQL = "select configuracion from Configuracion configuracion";

	private static final String[] RESTRICTIONS = {
			"lower(configuracion.descIdentVac) like lower(concat(#{configuracionList.configuracion.descIdentVac},'%'))",
			"lower(configuracion.undMedIdentVac) like lower(concat(#{configuracionList.configuracion.undMedIdentVac},'%'))",
			"lower(configuracion.descConfComite) like lower(concat(#{configuracionList.configuracion.descConfComite},'%'))",
			"lower(configuracion.unidMedConfComite) like lower(concat(#{configuracionList.configuracion.unidMedConfComite},'%'))",
};

	private Configuracion configuracion = new Configuracion();

	public ConfiguracionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public Configuracion getConfiguracion() {
		return configuracion;
	}
	
	public List<Configuracion> obtenerListaConfiguracions(){
    	listaConfiguracions = getResultList(); 
    	return listaConfiguracions;
    }
	
    public List<Configuracion> getListaConfiguracions() {
		return listaConfiguracions;
	}
}
