package py.com.excelsis.sicca.session.form;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.DatosGeneralesList;
import py.com.excelsis.sicca.session.ReferenciasList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.Referencias;

@Name("datosEspecificosformController")
@Scope(ScopeType.PAGE)
public class DatosEspecificosformController implements Serializable {

//	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;


	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;


	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	


	@In(create = true)
	DatosGeneralesList datosGeneralesList;
	@In(create = true)
	ReferenciasList referenciasList;
	
	
	private DatosEspecificos datosEspecificos;
	private Long idDatosEspecificos;
	private String mensaje;
	private Long idDatosGenerales;
	private boolean esTipo;
	private DatosGenerales datosGenerales;
	private List<SelectItem> tipoSelecItem;
	private Long idReferencias;
	private Boolean isGradoParentesco=false;
	private Long idGradoParentesco;
	private String tipoParentesco;
	

	public void init() {
		BigInteger idTmp;
		String q="select id_datos_generales from seleccion.datos_generales "
				+ "where descripcion like 'GRADO DE PARENTESCO'";
		
		idTmp =(BigInteger) em.createNativeQuery(q).getResultList().get(0);
		idGradoParentesco = idTmp.longValue();
		datosEspecificos = new DatosEspecificos();
		idReferencias=null;
		esTipo=false;
		
		if (idDatosEspecificos != null) {
			datosEspecificos = em.find(DatosEspecificos.class,idDatosEspecificos);
			datosEspecificosHome.setInstance(datosEspecificos);
			idDatosGenerales=datosEspecificos.getDatosGenerales().getIdDatosGenerales();
			if(datosEspecificos.getValorAlf()!=null && !datosEspecificos.getValorAlf().isEmpty()){
				obtRef(datosEspecificos.getValorAlf());
				if(idReferencias!=null)
					esTipo=true;
			}
			isGradoParentesco=(idDatosGenerales.equals(idGradoParentesco) )?true:false;
			
		} else {
			datosEspecificos.setActivo(true);
		}
		updateTipo();

	}
	
	public Boolean habTipo(){
			return esTipo;
	}
		
		
	public String update() {
			try {
				if(!checkDuplicate("update")){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
					return null;
				}
				if(datosEspecificos.getDescripcion().trim().isEmpty()){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
					return null;
				}
				if(seguridadUtilFormController.contieneCaracter(datosEspecificos.getDescripcion().trim())){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
					return null;
				}
				datosEspecificos.setFechaMod(new Date());
				datosEspecificos.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
				if(isGradoParentesco)
					datosEspecificos.setValorAlf(tipoParentesco);
					
			//	datosEspecificos.setDescripcion(datosEspecificos.getDescripcion().toUpperCase().trim());
			//FIXME
			//NO DEBE INACTIVAR SI ESTA SIENDO UTILIZADO POR OTRO
				em.merge(datosEspecificos);
				em.flush();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
			mensaje = "Registro actualizado con exito";
			statusMessages.add(Severity.INFO, mensaje);
			return "updated";
	}

	public Integer valNumParentesco() {
		String s = "select max(valor_num) from seleccion.datos_especificos where id_datos_generales = "
				+ "(select id_datos_generales from seleccion.datos_generales where descripcion like 'GRADO DE PARENTESCO')";
		return (Integer) em.createNativeQuery(s).getResultList().get(0)+1;
	
	}

		public String save() {
			try {
				if(!checkDuplicate("persist")){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
					return null;
				}
				if(datosEspecificos.getDescripcion().trim().isEmpty()){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
					return null;
				}
				if(seguridadUtilFormController.contieneCaracter(datosEspecificos.getDescripcion().trim())){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
					return null;
				}
				if(esTipo && idReferencias==null){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("DatosEspecificos_msg_tipo"));
					return null;
				}
				
				datosEspecificos.setFechaAlta(new Date());
				datosEspecificos.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
				datosEspecificos.setDescripcion(datosEspecificos.getDescripcion().toUpperCase().trim());
				datosEspecificos.setDatosGenerales(em.find(DatosGenerales.class, idDatosGenerales));
				if(isGradoParentesco){
					datosEspecificos.setValorAlf(tipoParentesco);
					datosEspecificos.setActivo(datosEspecificos.isActivo());
					datosEspecificos.setValorNum(valNumParentesco());
				/*	String q="insert into seleccion.datos_especificos (activo, id_datos_generales, descripcion,"
							+ " fecha_alta, fecha_mod, usu_alta, usu_mod, valor_alf, valor_num) "
							+ "values ("+datosEspecificos.isActivo()+", "+idDatosGenerales+", '"+datosEspecificos.getDescripcion()+"', '"
							+datosEspecificos.getFechaAlta()+"', NULL, '"+datosEspecificos.getUsuAlta()+"', NULL, '"
							+datosEspecificos.getValorAlf()+"', "+datosEspecificos.getValorNum()+") ";
					em.createNativeQuery(q);*/
					
				}
				if(esTipo && idReferencias!=null)
				{
					Referencias r = em.find(Referencias.class, idReferencias);
					datosEspecificos.setValorAlf(r.getDescAbrev());
				}
			
				em.persist(datosEspecificos);
				em.flush(); 
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				e.printStackTrace();
				return null;
			}
			mensaje = "Registro creado con exito";
			statusMessages.add(Severity.INFO, mensaje);
			return "persisted";
		}
		
		public void upTipo(){
			
			isGradoParentesco=(idDatosGenerales == idGradoParentesco)?true:false;

			if(idDatosGenerales!=null){
				datosGeneralesList.getDatosGenerales().setDescripcion("TIPOS DE DOCUMENTOS"); 
				datosGeneralesList.getDatosGenerales().setIdDatosGenerales(idDatosGenerales);
				datosGenerales=datosGeneralesList.datosGeneralesFind();
				if(datosGenerales!=null){
					esTipo=true;
					updateTipo();
				}else{
					esTipo=false;
					idReferencias=null;
					tipoSelecItem= new ArrayList<SelectItem>();
				}
				
			}else
			{
				esTipo=false;
				idReferencias=null;
				tipoSelecItem= new ArrayList<SelectItem>();
			}
		}
		
		
		
//		METODOS PRIVADOS
		@SuppressWarnings("unchecked")
		private boolean checkDuplicate(String operation){
			String hql = "SELECT o FROM DatosEspecificos o WHERE lower(o.descripcion) =:desc  "+
					" and o.datosGenerales.idDatosGenerales="+idDatosGenerales;
			if(operation.equalsIgnoreCase("update")){
				hql += " AND o.idDatosEspecificos != " + datosEspecificos.getIdDatosEspecificos().longValue();
			}
			List<CondicionTrabajoEspecif> list = em.createQuery(hql).setParameter("desc", datosEspecificos.getDescripcion().trim().toLowerCase()).getResultList();
			return list.isEmpty();
		}
	
		private void obtRef(String descsAbre){
			referenciasList.getReferencias().setDescAbrev(descsAbre);
			List<Referencias> r =referenciasList.getResultList();
			if(!r.isEmpty())
				idReferencias=r.get(0).getIdReferencias();
		}
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
		
		// GETTER Y SETTER
		public DatosEspecificos getDatosEspecificos() {
			return datosEspecificos;
		}
		public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
			this.datosEspecificos = datosEspecificos;
		}
		public Long getIdDatosEspecificos() {
			return idDatosEspecificos;
		}
		public void setIdDatosEspecificos(Long idDatosEspecificos) {
			this.idDatosEspecificos = idDatosEspecificos;
		}

		public Long getIdDatosGenerales() {
			return idDatosGenerales;
		}

		public void setIdDatosGenerales(Long idDatosGenerales) {
			this.idDatosGenerales = idDatosGenerales;
		}

		public boolean isEsTipo() {
			return esTipo;
		}
		public void setEsTipo(boolean esTipo) {
			this.esTipo = esTipo;
		}

		public List<SelectItem> getTipoSelecItem() {
			return tipoSelecItem;
		}
		public void setTipoSelecItem(List<SelectItem> tipoSelecItem) {
			this.tipoSelecItem = tipoSelecItem;
		}

		public Long getIdReferencias() {
			return idReferencias;
		}
		public void setIdReferencias(Long idReferencias) {
			this.idReferencias = idReferencias;
		}

		public Boolean getIsGradoParentesco() {
			return isGradoParentesco;
		}

		public void setIsGradoParentesco(Boolean isGradoParentesco) {
			this.isGradoParentesco = isGradoParentesco;
		}

		public Long getIdGradoParentesco() {
			return idGradoParentesco;
		}

		public void setIdGradoParentesco(Long idGradoParentesco) {
			this.idGradoParentesco = idGradoParentesco;
		}

		public String getTipoParentesco() {
			return tipoParentesco;
		}

		public void setTipoParentesco(String tipoParentesco) {
			this.tipoParentesco = tipoParentesco;
		}







}
