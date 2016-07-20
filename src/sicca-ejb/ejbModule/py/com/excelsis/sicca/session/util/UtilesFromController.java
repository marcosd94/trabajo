package py.com.excelsis.sicca.session.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.EvalReferencialTipoeval;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("utilesFromController")
public class UtilesFromController {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	public void init(){
		
	}

	public SinAnx getSinAnx( String codigoSinarh) {
		if ( codigoSinarh != null) {
			String[] arrayCodigo = codigoSinarh.split("\\.");
			Integer nen_codigo = new Integer(arrayCodigo[0]);
			Integer ent_codigo = new Integer(arrayCodigo[1]);
			Integer tip_codigo = new Integer(arrayCodigo[2]);
			Integer pro_codigo = new Integer(arrayCodigo[3]);
			Integer sub_codigo = new Integer(arrayCodigo[4]);
			Integer pry_codigo = new Integer(arrayCodigo[5]);
			Integer fue_codigo = new Integer(arrayCodigo[6]);
			Integer fin_codigo = new Integer(arrayCodigo[7]);
			Integer dpt_codigo = new Integer(arrayCodigo[8]);
			Integer vrs_codigo = new Integer(50);
			SinAnx sinAnx = new SinAnx();
			sinAnx.setNenCodigo(nen_codigo);
			sinAnx.setEntCodigo(ent_codigo);
			sinAnx.setTipCodigo(tip_codigo);
			sinAnx.setProCodigo(pro_codigo);
			sinAnx.setSubCodigo(sub_codigo);
			sinAnx.setPryCodigo(pry_codigo);
			sinAnx.setFueCodigo(fue_codigo);
			sinAnx.setFinCodigo(fin_codigo);
			sinAnx.setDptCodigo(dpt_codigo);
			sinAnx.setVrsCodigo(vrs_codigo);	
			return sinAnx;

		}
		return null;
	}

	
	public List<SelectItem> getSiNoSelectItems(){
		List<SelectItem> lista = new ArrayList<SelectItem>();
		//tipoOrganigramaSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		lista.add(new SelectItem(true, "SI"));
		lista.add(new SelectItem(false, "NO"));
		return lista;
	}
	
	
	public static Boolean hasErrorGenerico() {  
        FacesContext context = FacesContext.getCurrentInstance();  
        Iterator<FacesMessage> it = context.getMessages();  
        while (it.hasNext()) {  
        	FacesMessage f = it.next();
        	if("ERROR_GENERICO".equals(f.getDetail()))
        		return true;
        }  
        return false;  
    }  
	
	public boolean esParaguay(Long idPais){
		try {
			String cad = "SELECT * FROM general.pais " +
						 "where activo is true and id_pais = " + idPais;

			List<Pais> lista = new ArrayList<Pais>();
			lista = em.createNativeQuery(cad, Pais.class).getResultList();
			if (lista.size() > 0){
				Pais pais = lista.get(0);
				if ("paraguay".equalsIgnoreCase(pais.getDescripcion().trim()))
					return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
	}
}
