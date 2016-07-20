package py.com.excelsis.sicca.remuneracion.session.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;


import py.com.excelsis.sicca.entity.HistoricoRemuneraciones;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("generarBakupFC")
public class GenerarBakupFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;




	
	private Date fechaInicio;
	private Date fechaFin;
	
	public void init(){
		fechaFin=null;
		fechaInicio=null;
	}

	
	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void print() {
		if(fechaInicio.after(fechaFin)){
			statusMessages.add(Severity.ERROR,"La fecha de Inicio no puede ser mayor a la fecha fin");
			return;
		}
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
	
		List<Object[]> lista = consulta();
		
		if (lista != null) {
			for (Object[] obj : lista) {
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("insert", armarInsert(obj));
				listaDatosReporte.add(map);
			}
			borrar();
		
		}
		
		
		
	//	statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		String nombre="hist_remun_"+sdf.format(fechaInicio)+"-"+sdf.format(fechaFin);
		JasperReportUtils.respondPDF("RPT_CU713",nombre, false,
				listaDatosReporte, param);
		
	
	}
	public void limpiar(){
		fechaFin=null;
		fechaInicio=null;
	}
	
	private String armarInsert(Object[] obj){
		String insert="";
		String pre=" INSERT INTO remuneracion.historico_remuneraciones( "+
         "   id_historico_remuneracion, id_empleado_puesto, obj_codigo, categoria,  "+
         "   anho, mes, presupuestado, devengado, activo, usu_alta, fecha_alta,  "+
         "   usu_mod, fecha_mod) "+
         "   VALUES (nextval('remuneracion.historico_remuneraciones_id_remuneracion_seq'::regclass), ";
		insert+=pre;
		if(obj[0]!=null)
			insert+= obj[0].toString()+",";
		else
			insert+="null,";
		if(obj[1]!=null)
			insert+= obj[1].toString()+",";
		else
			insert+="null,";
		if(obj[2]!=null)
			insert+= "'"+obj[2].toString()+ "'"+",";
		else
			insert+="null,";
		if(obj[3]!=null)
			insert+= "'"+obj[3].toString()+"'"+",";
		else
			insert+="null,";
		if(obj[4]!=null)
			insert+= obj[4].toString()+",";
		else
			insert+="null,";
			
		if(obj[5]!=null)
			insert+= obj[5].toString()+",";
		else
			insert+="null,";
		if(obj[6]!=null)
			insert+= obj[6].toString()+",";
		else
			insert+="null,";
		if(obj[7]!=null)
			insert+= obj[7].toString()+",";
		else
			insert+="null,";
		if(obj[8]!=null)
			insert+= "'"+obj[8].toString()+"',";
		else
			insert+="null,";
		if(obj[9]!=null)
			insert+= "'"+obj[9].toString()+"',";
		else
			insert+="null,";
		if(obj[10]!=null)
			insert+= "'"+obj[10].toString()+"'"+",";
		else
			insert+="null ,";
		if(obj[11]!=null)
			insert+= "'"+obj[11].toString()+"'"+");";
		else
			insert+="null );";
	
		return insert;
		
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> consulta() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT   "+
					"	ID_EMPLEADO_PUESTO, OBJ_CODIGO, "+
					"	CATEGORIA, ANHO, "+
					"	MES, PRESUPUESTADO,"+ 
					"	DEVENGADO, ACTIVO, "+
					"	USU_ALTA, FECHA_ALTA,"+ 
					"	USU_MOD, FECHA_MOD"+
					"	FROM REMUNERACION.HISTORICO_REMUNERACIONES " +
					"   WHERE date(FECHA_ALTA) >= to_date('" + sdf.format(fechaInicio)
					+ "','DD-MM-YYYY')  AND  date(FECHA_ALTA) <= to_date('" + sdf.format(fechaFin)
					+ "','DD-MM-YYYY')";

		try {

			List<Object[]> config = em.createNativeQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<HistoricoRemuneraciones> borrarLista() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = " Select d from  HistoricoRemuneraciones d  "+
					"   WHERE date(d.fechaAlta) >= to_date('" + sdf.format(fechaInicio)
					+ "','DD-MM-YYYY')  AND  date(d.fechaAlta) <= to_date('" + sdf.format(fechaFin)
					+ "','DD-MM-YYYY')";

		try {

			List<HistoricoRemuneraciones> config = em.createQuery(sql).getResultList();
			if (config == null || config.size() == 0) {
				return new ArrayList<HistoricoRemuneraciones>();
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ArrayList<HistoricoRemuneraciones>();
	}
	
	
	
	
	public void borrar(){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			List<HistoricoRemuneraciones> objectsList=borrarLista();
			for (HistoricoRemuneraciones objects : objectsList) {
				em = persistenceContext.getEntityManager();
				em.remove(objects);
				em.flush();
			}
			
		} catch (javax.naming.NamingException e) {
			e.printStackTrace();
		} catch (javax.transaction.SystemException e) {
			e.printStackTrace();
		}
	}


	public Date getFechaInicio() {
		return fechaInicio;
	}


	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}


	public Date getFechaFin() {
		return fechaFin;
	}


	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	
	

}