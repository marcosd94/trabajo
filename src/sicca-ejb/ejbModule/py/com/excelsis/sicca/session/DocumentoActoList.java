package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.DocumentoActo;

@Name("documentoActoList")
public class DocumentoActoList extends EntityQuery<DocumentoActo> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<DocumentoActo> listaDocumentoActos;
	
	private static final String EJBQL = "select documentoActo from DocumentoActo documentoActo";

	private static final String[] RESTRICTIONS = {
};

	private DocumentoActo documentoActo = new DocumentoActo();

	public DocumentoActoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public DocumentoActo getDocumentoActo() {
		return documentoActo;
	}
	
	public List<DocumentoActo> obtenerListaDocumentoActos(){
    	listaDocumentoActos = getResultList(); 
    	return listaDocumentoActos;
    }
	
    public List<DocumentoActo> getListaDocumentoActos() {
		return listaDocumentoActos;
	}
}
