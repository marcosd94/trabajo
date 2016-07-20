package py.com.excelsis.sicca.seleccion.session.form;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import enums.Estado;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.session.DatosGeneralesList;
import py.com.excelsis.sicca.session.ReferenciasList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("datosEspecificosFCList")
public class DatosEspecificosFCList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	DatosEspecificosList datosEspecificosList;
	
	@In(create = true)
	DatosGeneralesList datosGeneralesList;
	@In(create = true)
	ReferenciasList referenciasList;
	
	@In(value = "entityManager")
	EntityManager em;

	
	private Estado estado = Estado.ACTIVO;
	private DatosEspecificos datosEspecificos= new DatosEspecificos();
	private Long idDatosGenerales= null;
	private Long idReferencias=null;
	private List<SelectItem> tipoSelecItem;
	private boolean esTipo;
	
	public void init(){
		search();
	}
	
	public void search(){
		datosEspecificosList.getDatosEspecificos().setDescripcion(datosEspecificos.getDescripcion());
		datosEspecificosList.setIdDatosGenerales(idDatosGenerales);
		if(idReferencias!=null){
			Referencias referencias= em.find(Referencias.class, idReferencias);
			datosEspecificosList.getDatosEspecificos().setValorAlf(referencias.getDescAbrev());
		}
		datosEspecificosList.setEstado(estado);
		datosEspecificosList.listaResultados();
	}
	
	public void searchAll(){
		datosEspecificos= new DatosEspecificos();
		estado = Estado.ACTIVO;
		idDatosGenerales=null;
		idReferencias=null;
		datosEspecificosList.limpiar();
	}
	
	 public void pdf() throws SQLException {
		 Connection conn =null; 
			try {
				HashMap<String, Object> mapa = obtenerMapaParametros();
				if (mapa == null) {
					throw new Exception(SeamResourceBundle.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				} else {
					 conn = JpaResourceBean.getConnection();
						JasperReportUtils.respondPDF("RPT_CU133_datos_especificos", mapa, false, conn, usuarioLogueado);
						conn.close();
				}
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.close();
		}
	        
	 }
	
	 public void upTipo(){
			if(idDatosGenerales!=null){
				datosGeneralesList.getDatosGenerales().setDescripcion("TIPOS DE DOCUMENTOS"); 
				datosGeneralesList.getDatosGenerales().setIdDatosGenerales(idDatosGenerales);
				DatosGenerales datosGenerales=datosGeneralesList.datosGeneralesFind();
				if(datosGenerales!=null){
					esTipo=true;
					updateTipo();
				}else{
					esTipo=false;
					idReferencias=null;
					tipoSelecItem= new ArrayList<SelectItem>();
				}
				
			}else{
				esTipo=false;
				idReferencias=null;
				tipoSelecItem= new ArrayList<SelectItem>();
			}
		}
	 public Boolean habTipo(){
			return esTipo;
	}
	 public String tipo(String valorAlf){
		 referenciasList.getReferencias().setValorAlf(valorAlf);
		 Referencias ref =referenciasList.referencia();
		 if(ref!=null)
			 return ref.getDescLarga();
		 
		 return "";
	 }
	
//	METODOS PRIVADOS
	 private  void updateTipo(){
			List<Referencias> tList = getTipoByDatoGral();
			tipoSelecItem = new ArrayList<SelectItem>();
			buildTipoSelectItem(tList);
	}
	 private List<Referencias> getTipoByDatoGral(){
			if(idDatosGenerales!= null){
				referenciasList.getReferencias().setDominio("TIPO_DOCUMENTO");
				referenciasList.setMaxResults(null);
				return referenciasList.getResultList();
			}
			return new ArrayList<Referencias>();
		}
	 private void buildTipoSelectItem(List<Referencias> tList){
			if(tipoSelecItem == null)
				tipoSelecItem = new ArrayList<SelectItem>();
			else
				tipoSelecItem.clear();
			
			tipoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			for(Referencias dep : tList ){
				tipoSelecItem.add(new SelectItem(dep.getIdReferencias(), dep.getDescLarga()));
			}
		}

	 private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		 SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
	        HashMap<String, Object> param = new HashMap<String, Object>();
	        param.put("usuario", usuarioLogueado.getCodigoUsuario());
	      
	        StringBuffer sql = new StringBuffer(); 
			
			sql.append(" select de.descripcion as descripcion,   ");
			sql.append(" case when de.activo = 'f'   then 'NO' else 'SI' end as activo ,   ");
			sql.append("case when dg.descripcion = 'TIPOS DE DOCUMENTOS'   then  true else false end as mostrar , ");
			sql.append(" de.valor_alf  as tipo,");
			sql.append("  dg.descripcion as general ");
			sql.append(" from seleccion.datos_especificos de  ");
			sql.append(" join seleccion.datos_generales dg on dg.id_datos_generales=de.id_datos_generales   ");
			sql.append("  where 1=1   ");
			if(datosEspecificos.getDescripcion()!=null && !datosEspecificos.getDescripcion().trim().isEmpty()){
				if (!sufc.validarInput(datosEspecificos.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
					return null;
				}
				sql.append("  and  lower(de.descripcion) like '%"+sufc.caracterInvalido(datosEspecificos.getDescripcion().toLowerCase())+"%'");
			}
			if(idDatosGenerales!=null){
				if (!sufc.validarInput(idDatosGenerales.toString(), TiposDatos.LONG.getValor(), null)) {
					return null;
				}
				sql.append("  and  dg.id_datos_generales ="+idDatosGenerales);
			}
			if(idReferencias!=null){
				if (!sufc.validarInput(idReferencias.toString(), TiposDatos.LONG.getValor(), null)) {
					return null;
				}
				Referencias referencias= em.find(Referencias.class, idReferencias);
				sql.append(" and de.valor_alf = '"+referencias.getDescAbrev()+"'");
			}
			if(estado.getValor()!=null){
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return null;
				}
				sql.append("  and  de.activo = "+estado.getValor());
			}
			sql.append("   group by de.descripcion, de.activo,dg.descripcion ,de.valor_alf  ");
			sql.append("   order by dg.descripcion   ");
	        param.put("sql", sql.toString());
	        if(datosEspecificos.getDescripcion()!=null)
	        	param.put("descripcion",!datosEspecificos.getDescripcion().equals("")? datosEspecificos.getDescripcion():"TODOS");
	        param.put("estado",estado.getDescripcion());
	    	ServletContext servletContext = (ServletContext) FacesContext
			.getCurrentInstance().getExternalContext().getContext();

	        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
	        return param;
	    }


//	GETTERS Y SETTERS
	


	public Estado getEstado() {
		return estado;
	}

	public Long getIdDatosGenerales() {
		return idDatosGenerales;
	}

	public void setIdDatosGenerales(Long idDatosGenerales) {
		this.idDatosGenerales = idDatosGenerales;
	}

	public Long getIdReferencias() {
		return idReferencias;
	}

	public void setIdReferencias(Long idReferencias) {
		this.idReferencias = idReferencias;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<SelectItem> getTipoSelecItem() {
		return tipoSelecItem;
	}

	public void setTipoSelecItem(List<SelectItem> tipoSelecItem) {
		this.tipoSelecItem = tipoSelecItem;
	}

	public boolean isEsTipo() {
		return esTipo;
	}

	public void setEsTipo(boolean esTipo) {
		this.esTipo = esTipo;
	}

	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}


	
	
	
}
