package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

@Scope(ScopeType.CONVERSATION)
@Name("selFuncionDatosEspList")
public class SelFuncionDatosEspList extends EntityQuery<SelFuncionDatosEsp> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 93524249877633306L;

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<SelFuncionDatosEsp> listaFuncionDatosEsp;

	private static final String EJBQL = "select selFuncionDatosEsp from SelFuncionDatosEsp selFuncionDatosEsp";

	private static final String[] RESTRICTIONS = {
			"lower(selFuncionDatosEsp.usuAlta) like lower(concat(#{selFuncionDatosEspList.selFuncionDatosEsp.usuAlta},'%'))",
			"lower(selFuncionDatosEsp.usuMod) like lower(concat(#{selFuncionDatosEspList.selFuncionDatosEsp.usuMod},'%'))", 
			"selFuncionDatosEsp.activo=#{selFuncionDatosEspList.estado.valor}",
			"selFuncionDatosEsp.datosEspecificos.idDatosEspecificos=#{selFuncionDatosEspList.idTipoDoc}",
			"selFuncionDatosEsp.funcion.idFuncion=#{selFuncionDatosEspList.idPagina}",
	};

	private SelFuncionDatosEsp selFuncionDatosEsp = new SelFuncionDatosEsp();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "selFuncionDatosEsp.funcion.descripcion";
	private Long idPagina;
	private Long idTipoDoc;
	
	
	

	public SelFuncionDatosEspList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SelFuncionDatosEsp> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<SelFuncionDatosEsp> limpiar() {
		selFuncionDatosEsp= new SelFuncionDatosEsp();
		estado = Estado.ACTIVO;
		idPagina=null;
		idTipoDoc=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public SelFuncionDatosEsp getSelFuncionDatosEsp() {
		return selFuncionDatosEsp;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdPagina() {
		return idPagina;
	}

	public void setIdPagina(Long idPagina) {
		this.idPagina = idPagina;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}
	
	public List<SelFuncionDatosEsp> obtenerListaFuncionDatosEsp(){
    	listaFuncionDatosEsp = getResultList(); 
    	return listaFuncionDatosEsp;
    }

	
	
	
}
