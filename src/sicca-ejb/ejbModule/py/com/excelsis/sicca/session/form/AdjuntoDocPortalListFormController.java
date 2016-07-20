package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;
import enums.SiNo;



import py.com.excelsis.sicca.entity.AdjuntoDocPortal;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.AdjuntoDocPortalList;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("adjuntoDocPortalListFormController")
public class AdjuntoDocPortalListFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create=true)
	DatosEspecificosList datosEspecificosList;

	@In(create = true)
	AdjuntoDocPortalList adjuntoDocPortalList;
	

	private Long idTipoDocumento;
	private Estado estado=Estado.ACTIVO;
	private SiNo publicado=SiNo.SI;
	private String nombreDoc;
	private String descripcion;
	private List<Documentos> documentosList;
	private List<SelectItem> tiposDocs= new ArrayList<SelectItem>();

	
	public void init(){
		em.clear();
		getTipoDocPORTALSelectItems();
		
		
	}
	@SuppressWarnings("unchecked")
	public void iniciarListado(){
//		documentosList=em.createQuery(" Select ap.documentos from AdjuntoDocPortal ap " +
//				" where ap.publicado=TRUE  and ap.activo=true  and ap.cuentaUsuario= TRUE order by ap.nroOrden asc ").getResultList();
		documentosList=em.createQuery(" Select ap.documentos from AdjuntoDocPortal ap  where ap.publicado=TRUE and "
				  + "ap.activo=true and ap.cuentaUsuario= TRUE  and ap.documentos.datosEspecificos.descripcion "
				  + "like 'ARTICULOS-INTERES' order by ap.nroOrden asc ").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public void normativasList(){
		//documentosList=em.createQuery(" Select ap.documentos from AdjuntoDocPortal ap " +
			//	" where ap.publicado=TRUE  and ap.activo=true  and ap.portal= TRUE and  ap.documentos.datosEspecificos.descripcion like '%NORMATIVAS-CONCURSABILIDAD%'  order by ap.nroOrden asc ").getResultList();
		String query="select doc.* from general.documentos doc join seleccion.datos_especificos de "
				+ "on doc.id_datos_especificos_tipo_documento = de.id_datos_especificos "
				+ "where de.descripcion like 'NORMATIVAS-CONCURSABILIDAD'";
		
		query=" Select ap.documentos from AdjuntoDocPortal ap  where ap.publicado=TRUE and "
				+ "ap.activo=true and ap.portal= TRUE  and ap.documentos.datosEspecificos.descripcion "
				+ "like 'NORMATIVAS-CONCURSABILIDAD' order by ap.nroOrden asc ";
		
		documentosList=(List<Documentos>)em.createQuery(query).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public void iniciarListado380(){//para el CU 380
//		documentosList=em.createQuery(" Select ap.documentos from AdjuntoDocPortal ap " +
//				" where ap.publicado=TRUE and ap.activo=true and ap.portal= TRUE  order by ap.nroOrden asc ").getResultList();
		documentosList=em.createQuery(" Select ap.documentos from AdjuntoDocPortal ap  where ap.publicado=TRUE and "
									  + "ap.activo=true and ap.portal= TRUE  and ap.documentos.datosEspecificos.descripcion "
									  + "like 'ARTICULOS-INTERES' order by ap.nroOrden asc ").getResultList();

	}
	
	//ZD 15/03/16 -- Guias para remuneraciones
	@SuppressWarnings("unchecked")
	public void iniciarListadoRem(){
		documentosList=em.createQuery(" Select ap.documentos from AdjuntoDocPortal ap  where ap.publicado=TRUE and "
									  + "ap.activo=true and ap.cuentaUsuario= TRUE  and ap.documentos.datosEspecificos.descripcion "
									  + "like 'GUIAS-REMUNERACIONES' order by ap.nroOrden asc ").getResultList();

	}
	
	public void search(){
		adjuntoDocPortalList.setIdTipoDoc(idTipoDocumento);
		adjuntoDocPortalList.getAdjuntoDocPortal().setActivo(estado.getValor());
		adjuntoDocPortalList.setPublicado(publicado);
		adjuntoDocPortalList.getDocumentos().setDescripcion(descripcion);
		adjuntoDocPortalList.getDocumentos().setNombreLogDoc(nombreDoc);
		adjuntoDocPortalList.listaResultados();

	}
	
	public void searchAll(){
		idTipoDocumento=null;
		nombreDoc=null;
		descripcion=null;
		publicado=SiNo.SI;
		estado=Estado.ACTIVO;
		adjuntoDocPortalList.limpiarCU328();
	}
	
	public void abrirDoc(Long idDoc){
	
		Documentos documentos= em.find(Documentos.class, idDoc);
		//AdjuntoDocPortal docPortal= em.find(AdjuntoDocPortal.class, idDoc);
		
		String codUsu = "PORTAL";
		if(usuarioLogueado== null){
			 codUsu = "PORTAL";
		}else{
			codUsu = usuarioLogueado.getCodigoUsuario();
		}
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(documentos.getIdDocumento(), codUsu);
		//AdmDocAdjuntoFormController.abrirDocumentoFromCU(docPortal.getDocumentos().getIdDocumento(), codUsu);
		
	}
	

	
	public List<SelectItem> getTipoDocPORTALSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoPORTAL())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoPORTAL() {
		try {
			List<DatosEspecificos> datosEspecificosLists= em.createQuery("Select d from DatosEspecificos d " +
					" where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.valorAlf like 'PORTAL' and d.activo=true order by d.descripcion").getResultList();
		
			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	
	
//	GETTERS Y SETTERS	

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	

	public SiNo getPublicado() {
		return publicado;
	}
	public void setPublicado(SiNo publicado) {
		this.publicado = publicado;
	}
	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public List<Documentos> getDocumentosList() {
		return documentosList;
	}
	public void setDocumentosList(List<Documentos> documentosList) {
		this.documentosList = documentosList;
	}
	public List<SelectItem> getTiposDocs() {
		return tiposDocs;
	}
	public void setTiposDocs(List<SelectItem> tiposDocs) {
		this.tiposDocs = tiposDocs;
	}
	
	
	

	
	
	
	

	

	
	
	
}
