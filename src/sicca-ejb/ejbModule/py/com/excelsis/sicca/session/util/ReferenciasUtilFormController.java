package py.com.excelsis.sicca.session.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("referenciasUtilFormController")
public class ReferenciasUtilFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4991047933512556323L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	
	public void init() {

	}


	@SuppressWarnings("unchecked")
	public Referencias getReferencia(String dominio, String descrpcionAbreviada) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '"+ dominio + "' " +
			"and desc_abrev = '"+ descrpcionAbreviada + "' " +
			"and activo is true";
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		}
		catch(Exception e){
			
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Referencias getReferenciaDescLarga(String dominio, String descrpcionLarga) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '"+ dominio + "' " +
			"and desc_larga = '"+ descrpcionLarga + "' " +
			"and activo is true";
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		}
		catch(Exception e){
			
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Referencias getReferenciaPorValorNum(String dominio, Integer valorNum) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '"+ dominio + "' " +
			"and valor_num = '"+ valorNum + "' " +
			"and activo is true";
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			
			if(lista != null && lista.size() > 0)
				return lista.get(0);
		}
		catch(Exception e){
			
		}
		return null;
	}
	
	
	public List<SelectItem> getSexoSelectItem() {
		return getSelectItemDominio("SEXO");
	}
	
	
	public List<SelectItem> getEstadoCivilSelectItem() {
		return getSelectItemDominio("ESTADO_CIVIL");
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSelectItemDominio(String dominio) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '" + dominio + "' " +
			"and activo is true " +
			"order by r.desc_larga";
			
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			if(lista != null && lista.size() > 0){
				for(Referencias r : lista){
					selectItems.add(new SelectItem(r.getDescAbrev(), r.getDescLarga()));
				}
				
			}
			return selectItems;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSelectItemValor(String dominio) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '" + dominio + "' " +
			"and activo is true " +
			"order by r.desc_larga";
			
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			if(lista != null && lista.size() > 0){
				for(Referencias r : lista){
					selectItems.add(new SelectItem(r.getValorNum(), r.getDescLarga()));
				}
				
			}
			return selectItems;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSelectItemValorFinaalizados(String dominio) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '" + dominio + "' and (r.desc_larga='FINALIZADA' or r.desc_larga='FINALIZADO CIRCUITO')" +
			"and activo is true " +
			"order by r.desc_larga";
			
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			if(lista != null && lista.size() > 0){
				for(Referencias r : lista){
					selectItems.add(new SelectItem(r.getValorNum(), r.getDescLarga()));
				}
				
			}
			return selectItems;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSelectItemCombo(String dominio) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '" + dominio + "' " +
			"and activo is true " +
			"order by r.DESC_ABREV";
			
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			if(lista != null && lista.size() > 0){
				for(Referencias r : lista){
					selectItems.add(new SelectItem(r.getIdReferencias(), r.getDescLarga()));
				}
				
			}
			return selectItems;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSelectItemComboTodos(String dominio) {
		try{
			String sql = "select r.* " +
			"from seleccion.referencias r " +
			"where dominio = '" + dominio + "' " +
			"and activo is true " +
			"order by r.DESC_ABREV";
			
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, "Todos"));
			
			List<Referencias> lista = em.createNativeQuery(sql, Referencias.class).getResultList();
			if(lista != null && lista.size() > 0){
				for(Referencias r : lista){
					selectItems.add(new SelectItem(r.getIdReferencias(), r.getDescLarga()));
				}
				
			}
			return selectItems;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
