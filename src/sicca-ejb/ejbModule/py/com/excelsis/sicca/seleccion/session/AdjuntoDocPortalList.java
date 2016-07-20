package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;
import enums.SiNo;

import py.com.excelsis.sicca.entity.AdjuntoDocPortal;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("adjuntoDocPortalList")
public class AdjuntoDocPortalList extends EntityQuery<AdjuntoDocPortal> {

	private static final String EJBQL = "select adjuntoDocPortal from AdjuntoDocPortal adjuntoDocPortal";

	private static final String[] RESTRICTIONS = {
			"lower(adjuntoDocPortal.usuAlta) like lower(concat(#{adjuntoDocPortalList.adjuntoDocPortal.usuAlta},'%'))",
			"lower(adjuntoDocPortal.usuMod) like lower(concat(#{adjuntoDocPortalList.adjuntoDocPortal.usuMod},'%'))",
			"lower(adjuntoDocPortal.documentos.nombreLogDoc) like concat('%',lower(concat(#{adjuntoDocPortalList.documentos.nombreLogDoc},'%')))",
			"lower(adjuntoDocPortal.documentos.descripcion) like concat('%',lower(concat(#{adjuntoDocPortalList.documentos.descripcion},'%')))",
			"adjuntoDocPortal.documentos.datosEspecificos.idDatosEspecificos =#{adjuntoDocPortalList.idTipoDoc}",
			"adjuntoDocPortal.publicado =#{adjuntoDocPortalList.publicado.valor}",
			"adjuntoDocPortal.activo=#{adjuntoDocPortalList.adjuntoDocPortal.activo}",
			 };

	private AdjuntoDocPortal adjuntoDocPortal = new AdjuntoDocPortal();
	private static final String ORDER = "adjuntoDocPortal.nroOrden asc";
	private Documentos documentos= new Documentos(); 
	private SiNo publicado=SiNo.SI;
	private Long idTipoDoc=null;
	private Boolean activo=true;
	
	public AdjuntoDocPortalList() {
		adjuntoDocPortal.setActivo(activo);
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<AdjuntoDocPortal> listaResultados() {
		adjuntoDocPortal.setActivo(activo);
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
	public List<AdjuntoDocPortal> limpiar() {
		documentos= new Documentos();
		adjuntoDocPortal= new AdjuntoDocPortal();
		activo=true;
		adjuntoDocPortal.setActivo(activo);
		idTipoDoc=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	/**
	 * 
	 * @return la lista completa publicado=true.
	 */
	public List<AdjuntoDocPortal> limpiarCU328() {
		documentos= new Documentos();
		adjuntoDocPortal= new AdjuntoDocPortal();
		idTipoDoc=null;
		publicado=SiNo.SI;
		activo=true;
		adjuntoDocPortal.setActivo(activo);
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public AdjuntoDocPortal getAdjuntoDocPortal() {
		return adjuntoDocPortal;
	}


	public Documentos getDocumentos() {
		return documentos;
	}


	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}



	public Long getIdTipoDoc() {
		return idTipoDoc;
	}


	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}


	public SiNo getPublicado() {
		return publicado;
	}


	public void setPublicado(SiNo publicado) {
		this.publicado = publicado;
	}


	public Boolean getActivo() {
		return activo;
	}


	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	
	
	
}
