package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("documentosList")
public class DocumentosList extends EntityQuery<Documentos> {

	private static final String EJBQL = "select documentos from Documentos documentos";

	private static final String[] RESTRICTIONS = {
			"lower(documentos.nombreLogDoc) like lower(concat(#{documentosList.documentos.nombreLogDoc},'%'))",
			"lower(documentos.nombreFisicoDoc) like lower(concat(#{documentosList.documentos.nombreFisicoDoc},'%'))",
			"lower(documentos.descripcion) like lower(concat(#{documentosList.documentos.descripcion},'%'))",
			"lower(documentos.tamanhoDoc) like lower(concat(#{documentosList.documentos.tamanhoDoc},'%'))",
			"lower(documentos.ubicacionFisica) like lower(concat(#{documentosList.documentos.ubicacionFisica},'%'))",
			"lower(documentos.mimetype) like lower(concat(#{documentosList.documentos.mimetype},'%'))",
			"lower(documentos.usuAlta) like lower(concat(#{documentosList.documentos.usuAlta},'%'))",
			"lower(documentos.usuMod) like lower(concat(#{documentosList.documentos.usuMod},'%'))",
			"lower(documentos.usuUltDesc) like lower(concat(#{documentosList.documentos.usuUltDesc},'%'))",
			"documentos.idTabla =#{documentosList.documentos.idTabla}",
			"lower(documentos.nombreTabla) like concat('%',lower(concat(#{documentosList.documentos.nombreTabla},'%')))",
			"lower(documentos.nombrePantalla) like concat('%',lower(concat(#{documentosList.documentos.nombrePantalla},'%')))",
			"documentos.activo=#{documentosList.estado.valor}",
			};

	private Documentos documentos = new Documentos();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "documentos.nroDocumento,documentos.anhoDocumento";
	
	
	public DocumentosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public Documentos getDocumentos() {
		return documentos;
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Documentos> listaResultados() {
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
	public List<Documentos> limpiar() {
		documentos= new Documentos();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
}
