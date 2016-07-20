package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.hibernate.Query;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.Estado;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.entity.MatrizRefConfDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.MatrizDocConfigDetList;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.MatrizRefConfDTO;

@Scope(ScopeType.CONVERSATION)
@Name("matrizDocConfigDetListFormController")
public class MatrizDocConfigDetListFormController implements Serializable{
	
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
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	@In(create = true)
	MatrizDocConfigDetList matrizDocConfigDetList;
	private MatrizDocConfigDet matrizDocConfigDet;
	private MatrizDocConfigEnc matrizDocConfigEnc;

	
	private Long idConcursoPuestoAgr;//recibe del CU que le llama
	private Long idTipoDocumento;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;//enviado por el CU
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean habilitar = true;
	private Long idMatrizDocConfigDet;
	private Long idMatrizDocConfigEnc;
	
	

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		Concurso concurso = concursoPuestoAgr.getConcurso();
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		 
		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	
	public void init(){
		idTipoDocumento=null;
		concursoPuestoAgr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
		/*Incidencia 1014*/
		validarOee();
		/**/
		findEntidades();//Trae las entidades segun el grupo que se envio
		search();
		
		habilitarPantalla();
	}
	
	private void habilitarPantalla() {
		habilitar = true;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = true;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			Referencias refPendiente = new Referencias();
			refPendiente = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue())
				habilitar = true;
			if(concursoPuestoAgr.getEstado().intValue() == refAjuste.getValorNum().intValue())
				habilitar = false;
		}

	}
	
	public void search(){
		matrizDocConfigDetList.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		matrizDocConfigDetList.setIdDatosEspecificos(idTipoDocumento);
		matrizDocConfigDetList.listaResultados();

	}
	
	public void searchAll(){
		idTipoDocumento=null;
		matrizDocConfigDetList.setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		matrizDocConfigDetList.setIdDatosEspecificos(idTipoDocumento);
		matrizDocConfigDetList.limpiarXPuesto();
	}

	@SuppressWarnings("unchecked")
	private void findEntidades(){
		configuracionUoCab=em.find(ConfiguracionUoCab.class, concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()) ;
		if(configuracionUoCab.getEntidad()!=null){
			sinEntidad=em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin=em.createQuery("Select n from SinNivelEntidad n " +
					" where n.aniAniopre ="+sinEntidad.getAniAniopre() +" and n.nenCodigo="+sinEntidad.getNenCodigo()).getResultList();
			if(!sin.isEmpty())
				sinNivelEntidad=sin.get(0);
			
		}
		
	}
	 public void print() {
		 try {
			 
				Connection conn = JpaResourceBean.getConnection();
				JasperReportUtils.respondPDF("RPT_CU244_matriz_doc_con_selec", obtenerMapaParametros(), false, conn, usuarioLogueado);
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	        
	 }
	 private HashMap<String, Object> obtenerMapaParametros() {
	        HashMap<String, Object> param = new HashMap<String, Object>();
	        param.put("usuario", usuarioLogueado.getCodigoUsuario());
	      
	        StringBuffer sql = new StringBuffer(); 
	        sql.append(" SELECT	MATRIZ_CONFIG_DET.NRO_ORDEN AS nroOrden ");
	        sql.append(" ,	DE_TIPO_DOC.DESCRIPCION AS tipoDocumento ");
	        sql.append(" ,	 case when MATRIZ_CONFIG_DET.OBLIGATORIO = 'f'   then 'NO' else 'SI' end   AS obligatorio ");
	        sql.append(" ,	 case when MATRIZ_CONFIG_DET.EVALUACION_DOC = 'f'   then 'NO' else 'SI' end AS evalDocumental ");
	        sql.append(" ,	case when MATRIZ_CONFIG_DET.ADJUDICACION = 'f'   then 'NO' else 'SI' end  AS adjudicacion ");
	        sql.append(" FROM seleccion.matriz_doc_config_det 	MATRIZ_CONFIG_DET ");
	        sql.append(" JOIN seleccion.matriz_doc_config_enc	MATRIZ_CONFIG_ENC ");
	        sql.append(" ON MATRIZ_CONFIG_ENC.ID_MATRIZ_DOC_CONFIG_ENC = MATRIZ_CONFIG_DET.ID_MATRIZ_DOC_CONFIG_ENC ");
	        sql.append(" JOIN seleccion.datos_especificos DE_TIPO_DOC ");
	        sql.append(" ON DE_TIPO_DOC.ID_DATOS_ESPECIFICOS = MATRIZ_CONFIG_DET.ID_DATOS_ESPECIFICOS_TIPO_DOCU ");
		
			sql.append(" where   MATRIZ_CONFIG_ENC.ID_CONCURSO_PUESTO_AGR  ="+idConcursoPuestoAgr);
			
			if(idTipoDocumento!=null){
				sql.append(" and  MATRIZ_CONFIG_DET.ID_DATOS_ESPECIFICOS_TIPO_DOCU  = "+idTipoDocumento);
			}
			sql.append(" and  MATRIZ_CONFIG_DET.activo =true ");
			sql.append("   ORDER BY 	MATRIZ_CONFIG_DET.NRO_ORDEN   ");
	        param.put("sql", sql.toString());
	        if(sinEntidad!=null)
	        	param.put("entidad",sinEntidad.getEntCodigo()+"-"+sinEntidad.getEntNombre());
	        	
	        if(sinNivelEntidad!=null)
	        	param.put("nivel",sinNivelEntidad.getNenCodigo()+"-"+sinNivelEntidad.getNenNombre());
	        
	        if(configuracionUoCab!=null)
	        	param.put("oee",configuracionUoCab.getOrden()+"-"+configuracionUoCab.getDenominacionUnidad());
	        
	        param.put("grupoPuesto",concursoPuestoAgr.getDescripcionGrupo());
	        param.put("concurso",concursoPuestoAgr.getConcurso().getNombre());
	        
	        	
	       
	    	ServletContext servletContext = (ServletContext) FacesContext
			.getCurrentInstance().getExternalContext().getContext();

	        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
	        return param;
	    }
	 
	 public String eliminarPlantilla(){
		 
		 try {
			 	Date fechaMod = new Date();
	            //Recuperamos la cabecera
	            String query =" select id_matriz_doc_config_enc from seleccion.matriz_doc_config_enc mEnc"
						+ " where mEnc.id_concurso_puesto_agr= "
	                    + idConcursoPuestoAgr + " and mEnc.activo=true";
	          
	            //MatrizDocConfigEnc matrizDocConfigEnc = (MatrizDocConfigEnc)em.createQuery(query).getSingleResult();
	            Long idEnc = Long.parseLong(em.createNativeQuery(query).getSingleResult().toString());

	            //Creamos iterador para el detalle y luego iteramos poniendo como inactivo cada item
	            
	            
	            String query3 = " select * from seleccion.matriz_doc_config_det mDet"
						+ " where mDet.id_matriz_doc_config_enc= "
						+ idEnc + " and mDet.activo=true";
	           
	            matrizDocConfigDetList.setIdConcursoPuestoAgr(this.idConcursoPuestoAgr);
	            for(MatrizDocConfigDet m: matrizDocConfigDetList.Eliminar(idConcursoPuestoAgr,idEnc))
	            {
	            	m.setActivo(false);
	            	m.setFechaMod(fechaMod);
                    m.setUsuMod(usuarioLogueado.getCodigoUsuario());
	            	em.merge(m);
	            }
	            //Finalmente ponemos como inactiva la cabecera
	            matrizDocConfigEnc = em.find(MatrizDocConfigEnc.class, idEnc);
				matrizDocConfigEnc.setActivo(false);
				matrizDocConfigEnc.setFechaMod(fechaMod);
				matrizDocConfigEnc.setUsuMod(
						usuarioLogueado.getCodigoUsuario());
	            em.merge(matrizDocConfigEnc);
	           
	            em.flush();
	       
	            return "OK";
	           
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            return null;
	        }
		}
	
//	GETTERS Y SETTERS	
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

	
	
	
	
	

	

	
	
	
}
