package py.com.excelsis.sicca.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.circuitoCSI.session.form.AdmSorteo511;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.seleccion.session.form.ElaborarPublicacionSeleccionadosFormController;
import py.com.excelsis.sicca.seleccion.session.form.ListaLargaFormController;
import py.com.excelsis.sicca.seleccion.session.form.PublicacionListaCortaFormController;
import py.com.excelsis.sicca.seleccion.session.form.RealizarEvalsFormController;
import py.com.excelsis.sicca.seleccion.session.form.TachasReclamosModifFormController;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;

@Scope(ScopeType.PAGE)
@Name("publicacionPortalFC")
public class PublicacionPortalFC {
	@In(value = "entityManager")
	EntityManager em;
	Long idConcursoPuestoAgr;
	Long idDatosEspecificos;

	String idPublicacionPortal;
	

	Long idConcurso;
	PublicacionPortal publicacionPortal;
	String imprimir;
	List<String> listaTexto;
	
	//variables para el boton Volver.
	String from;
	String sugestion;
	String sugestionGrupo;
	Integer firstResult;

	public void init() throws Exception {
		if (idConcurso != null && idConcursoPuestoAgr != null) {
			Query q = em
					.createQuery("select PublicacionPortal from PublicacionPortal PublicacionPortal "
							+ " where PublicacionPortal.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ idConcursoPuestoAgr
							+ " and PublicacionPortal.concurso.idConcurso = "
							+ idConcurso
							+ " and PublicacionPortal.activo = true order by PublicacionPortal.fechaAlta asc");
			List<PublicacionPortal> lista = q.getResultList();
			listaTexto = new ArrayList<String>();
			if(lista != null){
				for (PublicacionPortal o : lista) {
					//ZD 18/03/16 -- Si contiene archivo a descargar, incluir variable from para el botón Volver
					if(o.getTexto().contains("href=") && !o.getTexto().contains("from=") && o.getTexto().contains("?")){
						int index = o.getTexto().lastIndexOf("?");
						String texto = o.getTexto().substring(0, index)+"?from="+from+"&"+o.getTexto().substring(index+1);
						listaTexto.add(texto);
				     }else{
				    	listaTexto.add(o.getTexto()); 
				     }
				}
			}
		}
		if (imprimir != null && imprimir.equalsIgnoreCase("CU_86")) {
			imprimirCU86();
		
		} else if (imprimir != null && imprimir.equalsIgnoreCase("CU_86Rectificado")) {
			this.imprimirCU86Rectificado();
			
		}else if (imprimir != null && imprimir.equalsIgnoreCase("CU_87")) {
			imprimirCU87();
		}else if (imprimir != null && imprimir.equalsIgnoreCase("CU_87Rectificado")) {
			imprimirCU87Rectificado();
		} else if (imprimir != null && imprimir.equalsIgnoreCase("imprimirListaFinalTerna")) {
			imprimirListaFinalTerna();
		
		} else if (imprimir != null && imprimir.equalsIgnoreCase("CU_161")) {
			imprimirCU161();
		}else if (imprimir != null && imprimir.equalsIgnoreCase("EvalDet")) {
			imprimirCUEvalDet();
		}else if (imprimir != null && imprimir.equalsIgnoreCase("EvalDetRectificado")) {
			imprimirCUEvalDetRectificado();
		}else if (imprimir != null && imprimir.equalsIgnoreCase("ListaNoAdmitidosRectificado")) {
			imprimirListaNoAdmitidosRectificado();
		
		}else if (imprimir != null && imprimir.equalsIgnoreCase("ImprimirDocAdjunto")) {
			imprimirDocAdjunto(idPublicacionPortal);
		}else if (imprimir != null && imprimir.equalsIgnoreCase("ListaNoAdmitidos")) {
			imprimirListaNoAdmitidos();
		
		}else if (imprimir != null && imprimir.equalsIgnoreCase("ListaAdmitidosXEvaluacion")) {
			imprimirListaAdmitidosXEvaluacion();
		}else if (imprimir != null && imprimir.equalsIgnoreCase("reporteEvaluacionesXEtapa")) {
				imprimirReporteEvaluacionesXEtapa();
		}else if (imprimir != null && imprimir.equalsIgnoreCase("ListaNoAprobadosXEvaluacion")) {
			imprimirListaNoAprobadosXEvaluacion();
		}
			
	}
	
	
	public void imprimirDocAdjunto(String idPublicacionPortal) {
		try{
		
		
			
		String sql  ="select doc.* from general.documentos doc "
				+ " join seleccion.datos_especificos datos on doc.id_datos_especificos_tipo_documento = datos.id_datos_especificos"
				+ " and  datos.valor_alf = 'DOC_PUBLICACION'"
				+ " and doc.activo = true "
				+ " where id_tabla = " + idPublicacionPortal;
		
		List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
		
		Long id = docs.get(0).getIdDocumento();
		
		String usuario = "";
		
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,usuario);
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	public void imprimirCUEvalDet() {
		
		PublicacionListaCortaFormController publicacionListaCortaFormController = (PublicacionListaCortaFormController) Component
				.getInstance(PublicacionListaCortaFormController.class, true);
		publicacionListaCortaFormController
				.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		publicacionListaCortaFormController.imprimirCUEvalDet();
	}

	public void imprimirCU87() {
		
		PublicacionListaCortaFormController publicacionListaCortaFormController = (PublicacionListaCortaFormController) Component
				.getInstance(PublicacionListaCortaFormController.class, true);
		publicacionListaCortaFormController
				.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		publicacionListaCortaFormController.print2();
	}
	
	public void imprimirListaFinalTerna() {
		
		PublicacionListaCortaFormController publicacionListaCortaFormController = (PublicacionListaCortaFormController) Component
				.getInstance(PublicacionListaCortaFormController.class, true);
		publicacionListaCortaFormController
				.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		publicacionListaCortaFormController.imprimirListaFinalTerna();
	}

	public void imprimirCU86() {
		//System.out.println("#### " + idConcursoPuestoAgr + " ###");
		ListaLargaFormController listaLargaFormController = (ListaLargaFormController) Component
				.getInstance(ListaLargaFormController.class, true);
		listaLargaFormController
				.setIdConcursoGrupoPuestoAgr(idConcursoPuestoAgr);
		listaLargaFormController.print2();
	}
	
	public void imprimirListaNoAdmitidos() {
		
		ListaLargaFormController listaLargaFormController = (ListaLargaFormController) Component
				.getInstance(ListaLargaFormController.class, true);
		listaLargaFormController.setIdConcursoGrupoPuestoAgr(idConcursoPuestoAgr);
		listaLargaFormController.imprimirListaNoAdmitidos();
	}
	
	public void imprimirListaAdmitidosXEvaluacion() {
			
		RealizarEvalsFormController realizarEvalsFormController = (RealizarEvalsFormController) Component
				.getInstance(RealizarEvalsFormController.class, true);
		realizarEvalsFormController.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		realizarEvalsFormController.setIdDatosEspecificos(idDatosEspecificos);
		realizarEvalsFormController.imprimirListaAdmitidosXEvaluacion();
	}
	
	public void imprimirReporteEvaluacionesXEtapa() {
		
		RealizarEvalsFormController realizarEvalsFormController = (RealizarEvalsFormController) Component
				.getInstance(RealizarEvalsFormController.class, true);
		realizarEvalsFormController.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		realizarEvalsFormController.setIdDatosEspecificos(idDatosEspecificos);
		realizarEvalsFormController.imprimirReporteEvaluacionesXEtapa();
	}
	
	
	public void imprimirListaNoAprobadosXEvaluacion() {
		
		RealizarEvalsFormController realizarEvalsFormController = (RealizarEvalsFormController) Component
				.getInstance(ListaLargaFormController.class, true);
		realizarEvalsFormController.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		realizarEvalsFormController.setIdDatosEspecificos(idDatosEspecificos);
		realizarEvalsFormController.imprimirListaNoAprobadosXEvaluacion();
	}


	public void imprimirCU161() {
		
		ElaborarPublicacionSeleccionadosFormController elaborarPublicacionSeleccionadosFormController = (ElaborarPublicacionSeleccionadosFormController) Component
				.getInstance(
						ElaborarPublicacionSeleccionadosFormController.class,
						true);
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		elaborarPublicacionSeleccionadosFormController
				.setConcursoPuestoAgr(concursoPuestoAgr);
		elaborarPublicacionSeleccionadosFormController.init2();
		elaborarPublicacionSeleccionadosFormController.print();
	}
	public void imprimirCU511() {
		//System.out.println("#### " + idConcursoPuestoAgr + " ###");
		AdmSorteo511 admSorteo511= (AdmSorteo511) Component
				.getInstance(
						AdmSorteo511.class,
						true);
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		admSorteo511
				.setConcursoPuestoAgr(concursoPuestoAgr);
		admSorteo511.init2();
		admSorteo511.imprimir();
	}
	
	
	public void imprimirCU86Rectificado() throws Exception {
		//System.out.println("#### " + idConcursoPuestoAgr + " ###");
		TachasReclamosModifFormController tachasReclamosModifFormController = (TachasReclamosModifFormController) Component
				.getInstance(TachasReclamosModifFormController.class, true);
		tachasReclamosModifFormController.setIdConcursoGrupoPuestoAgr(idConcursoPuestoAgr);
		tachasReclamosModifFormController.print();
	}
	
	public void imprimirListaNoAdmitidosRectificado() throws Exception {
		
		TachasReclamosModifFormController tachasReclamosModifFormController = (TachasReclamosModifFormController) Component
				.getInstance(TachasReclamosModifFormController.class, true);
		tachasReclamosModifFormController.setIdConcursoGrupoPuestoAgr(idConcursoPuestoAgr);
		tachasReclamosModifFormController.imprimirListaNoAdmitidos();
	}


	public void imprimirCUEvalDetRectificado() throws Exception {
		
		TachasReclamosModifFormController tachasReclamosModifFormController = (TachasReclamosModifFormController) Component
				.getInstance(TachasReclamosModifFormController.class, true);
		tachasReclamosModifFormController.setIdConcursoGrupoPuestoAgr(idConcursoPuestoAgr);
		tachasReclamosModifFormController.imprimirEvaluacionesDetalladas();
	}
	
	public void imprimirCU87Rectificado() throws Exception {
		
		TachasReclamosModifFormController tachasReclamosModifFormController = (TachasReclamosModifFormController) Component
				.getInstance(TachasReclamosModifFormController.class, true);
		tachasReclamosModifFormController.setIdConcursoGrupoPuestoAgr(idConcursoPuestoAgr);
		tachasReclamosModifFormController.esMeritoOTerna();
		if (tachasReclamosModifFormController.getTipoMT().equals("T"))
			tachasReclamosModifFormController.imprimirListaCortaTerna();
		else if (tachasReclamosModifFormController.getTipoMT().equals("M"))
			tachasReclamosModifFormController.imprimirListaFinalMerito();
	}
	


	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public PublicacionPortal getPublicacionPortal() {
		return publicacionPortal;
	}

	public void setPublicacionPortal(PublicacionPortal publicacionPortal) {
		this.publicacionPortal = publicacionPortal;
	}

	public String getImprimir() {
		return imprimir;
	}

	public void setImprimir(String imprimir) {
		this.imprimir = imprimir;
	}

	public List<String> getListaTexto() {
		return listaTexto;
	}

	public void setListaTexto(List<String> listaTexto) {
		this.listaTexto = listaTexto;
	}
	public String getIdPublicacionPortal() {
		return idPublicacionPortal;
	}


	public void setIdPublicacionPortal(String idPublicacionPortal) {
		this.idPublicacionPortal = idPublicacionPortal;
	}
	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}


	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}
	
	
	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public String getSugestion() {
		return sugestion;
	}


	public void setSugestion(String sugestion) {
		this.sugestion = sugestion;
	}


	public String getSugestionGrupo() {
		return sugestionGrupo;
	}


	public void setSugestionGrupo(String sugestionGrupo) {
		this.sugestionGrupo = sugestionGrupo;
	}


	public Integer getFirstResult() {
		return firstResult;
	}


	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}



	
	
	
}
