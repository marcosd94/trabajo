package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.VwListaElegible;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalReferencialPostulanteList;
import py.com.excelsis.sicca.seleccion.session.VwEvalRefPostuList;
import py.com.excelsis.sicca.seleccion.session.VwListaElegibleList;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("listaElegibleFC")
public class ListaElegibleFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	VwListaElegibleList vwListaElegibleList;
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	
	
	@In(required = false)
	Usuario usuarioLogueado;

	private String concurso;
	private String grupo;
	private boolean primeraEntrada=true;
	private String cu="CU 324";
	private List<VwListaElegible> listaElegiblesLista= new ArrayList<VwListaElegible>();

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		
		if(primeraEntrada){
			primeraEntrada=false;
			cargarCabecera();
		}
		nivelEntidadOeeUtil.init();
		search();
	}

	private void cargarCabecera(){
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}


	public void searchAll() {
		nivelEntidadOeeUtil.limpiar();
		concurso=null;
		grupo=null;
		cargarCabecera();
		listaElegiblesLista=vwListaElegibleList.limpiar();
		
	}

	public void search() {
	
		vwListaElegibleList.getVwListaElegible().setEntCodigo(nivelEntidadOeeUtil.getCodSinEntidad());
		vwListaElegibleList.getVwListaElegible().setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
		vwListaElegibleList.setIdOee(nivelEntidadOeeUtil.getIdConfigCab());
		if(concurso!=null && !concurso.trim().equals(""))
			vwListaElegibleList.getVwListaElegible().setConcurso(concurso);
		else
			vwListaElegibleList.getVwListaElegible().setConcurso(null);
		if(grupo!=null && !grupo.trim().equals(""))
			vwListaElegibleList.getVwListaElegible().setGrupo(grupo);
		else
			vwListaElegibleList.getVwListaElegible().setGrupo(null);
		listaElegiblesLista= vwListaElegibleList.listaResultados();
	}

	


	
	
	 public void print(Long idGrupo,Date fecha) {
		 try {
				Connection conn = JpaResourceBean.getConnection();
				JasperReportUtils.respondPDF("RPT_CU324", obtenerMapaParametros(idGrupo,fecha), false, conn, usuarioLogueado);
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	        
	 }
	
	
	 private HashMap<String, Object> obtenerMapaParametros(Long idGrupo,Date fecha) {
	        HashMap<String, Object> param = new HashMap<String, Object>();
	        
	        param.put("usuario", usuarioLogueado.getCodigoUsuario());
	   
	        ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,idGrupo);
	        
			if(nivelEntidadOeeUtil!=null && nivelEntidadOeeUtil.getCodNivelEntidad()!=null){
				param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+"-"+nivelEntidadOeeUtil.getNombreNivelEntidad());
			}
			if(nivelEntidadOeeUtil!=null && nivelEntidadOeeUtil.getNombreSinEntidad()!=null){
				param.put("entidad",nivelEntidadOeeUtil.getCodNivelEntidad()+"-"+nivelEntidadOeeUtil.getNombreSinEntidad());
			}
			if(nivelEntidadOeeUtil!=null && nivelEntidadOeeUtil.getDenominacionUnidad()!=null){
				param.put("oee",nivelEntidadOeeUtil.getOrdenUnidOrg()+"-"+nivelEntidadOeeUtil.getDenominacionUnidad());
			}
			
		
			param.put("idGrupo",idGrupo);
			
			param.put("grup",agr.getDescripcionGrupo());
	    	ServletContext servletContext = (ServletContext) FacesContext
			.getCurrentInstance().getExternalContext().getContext();

	        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			
			return param;
	    }
	
	
	

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */
	
	public String getConcurso() {
		return concurso;
	}



	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}



	public String getGrupo() {
		return grupo;
	}



	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}




	public SeguridadUtilFormController getSeguridadUtilFormController() {
		return seguridadUtilFormController;
	}




	public void setSeguridadUtilFormController(
			SeguridadUtilFormController seguridadUtilFormController) {
		this.seguridadUtilFormController = seguridadUtilFormController;
	}

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}

	public List<VwListaElegible> getListaElegiblesLista() {
		return listaElegiblesLista;
	}

	public void setListaElegiblesLista(List<VwListaElegible> listaElegiblesLista) {
		this.listaElegiblesLista = listaElegiblesLista;
	}
	
	

	

}
