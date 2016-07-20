package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ProcesoGestion;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("procesoGestionList")
public class ProcesoGestionList extends EntityQuery<ProcesoGestion> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<ProcesoGestion> listaProcesoGestions;
	
	private static final String EJBQL = "select procesoGestion from ProcesoGestion procesoGestion";

	private static final String[] RESTRICTIONS = {
			"lower(procesoGestion.descripcion) like lower(concat(#{procesoGestionList.procesoGestion.descripcion},'%'))",
			"lower(procesoGestion.usuAlta) like lower(concat(#{procesoGestionList.procesoGestion.usuAlta},'%'))",
			"lower(procesoGestion.usuMod) like lower(concat(#{procesoGestionList.procesoGestion.usuMod},'%'))",
	};
	private static final String[] RESTRICTIONS_BY_CONF_DET = {
		"procesoGestion.configuracionUoDet.idConfiguracionUoDet = #{procesoGestionList.configuracionUoDet.idConfiguracionUoDet}",
	};

	private ProcesoGestion procesoGestion = new ProcesoGestion();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();

	public ProcesoGestionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}
	
	public List<ProcesoGestion> listProcessByConfigDet(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_BY_CONF_DET));
		setOrder("procesoGestion.descripcion");
		return getResultList();
	}

//	GETTERS Y SETTERS
	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public ProcesoGestion getProcesoGestion() {
		return procesoGestion;
	}
	
	public List<ProcesoGestion> obtenerListaProcesoGestions(){
    	listaProcesoGestions = getResultList(); 
    	return listaProcesoGestions;
    }
	
    public List<ProcesoGestion> getListaProcesoGestions() {
		return listaProcesoGestions;
	}
}
