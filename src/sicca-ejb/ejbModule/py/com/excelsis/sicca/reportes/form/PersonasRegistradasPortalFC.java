package py.com.excelsis.sicca.reportes.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("personasRegistradasPortalFC")
public class PersonasRegistradasPortalFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	
	private Date fechaDesde;
	private Date fechaHasta;
	
	private String formato = "EXCEL";
	private List<SelectItem> formatoSelectItem;

	private String tipo = "D";
	private List<SelectItem> tipoSelectItem;
	
	private List<SelectItem> tituloSelectItem;
	private List<SelectItem> especialidadSelectItem;
	
	private Long idTitulo;
	private Long idEspecialidad;

	
	public void init() {
		cargarTitulo();
		cargarEspecialidad();
		limpiar();
	}
	
	
	private void cargarTitulo() {
		tituloSelectItem = new ArrayList<SelectItem>();
		tituloSelectItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		String sql = "select * from seleccion.titulo_academico " +
					 "where activo is true " +
					 "order by descripcion";
		
		List<TituloAcademico> lista = em.createNativeQuery(sql, TituloAcademico.class).getResultList();
		
		if(lista != null){
			for(TituloAcademico o : lista){
				tituloSelectItem.add(new SelectItem(o.getIdTituloAcademico(), o.getDescripcion() + " - " + o.getNivelEstudio().getDescripcion()));
			}
		}
	}


	private void cargarEspecialidad() {
		especialidadSelectItem = new ArrayList<SelectItem>();
		especialidadSelectItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		String sql = "select * from planificacion.especialidades " +
					 "where activo is true " +
					 "order by descripcion";
		
		List<Especialidades> lista = em.createNativeQuery(sql, Especialidades.class).getResultList();
		
		if(lista != null){
			for(Especialidades o : lista){
				especialidadSelectItem.add(new SelectItem(o.getIdEspecialidades(), o.getDescripcion()));
			}
		}
	}


	public void limpiar(){
		fechaDesde = null;
		fechaHasta = null;
		formato = "EXCEL";
		tipo = "D";
		setIdTitulo(null);
		setIdEspecialidad(null);
	}
	
	

	private Boolean validar(Boolean controlarCab) {	
		if ( fechaDesde == null) {
			statusMessages.add(Severity.ERROR, "Fecha Desde es un campo requerido. Verifique.");
			return false;
		}
		else if ( fechaHasta == null) {
			statusMessages.add(Severity.ERROR, "Fecha Hasta es un campo requerido. Verifique.");
			return false;
		}
		else if (fechaDesde.after(fechaHasta)){
			statusMessages.add(Severity.ERROR, "La Fecha Desde no puede ser posterior a la Fecha Hasta. Verifique.");
			return false;
		}
		
		return true;
	}

	
	public void imprimir() {
		
		boolean validarCabecera = false;
		if("D".equals(tipo))
			validarCabecera = true;
		
		if (!validar(validarCabecera))
			return;
		
		reportUtilFormController = (ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		
		if ("R".equalsIgnoreCase(tipo))
			reportUtilFormController.setNombreReporte("RPT_CU468_personas_registradas");
		else
			reportUtilFormController.setNombreReporte("RPT_CU468_personas_registradas_detallado");

		cargarParametros();
		
		if ("EXCEL".equals(formato))
			reportUtilFormController.imprimirReporteXLS();
		else
			reportUtilFormController.imprimirReportePdf();
	}
	
	
	private void cargarParametros() {
		try{
			reportUtilFormController.getParametros().put("desde", fechaDesde);
			reportUtilFormController.getParametros().put("hasta", fechaHasta);
			reportUtilFormController.getParametros().put("idTitulo", idTitulo);
			reportUtilFormController.getParametros().put("idEspecialidad", idEspecialidad);
			if (idTitulo != null){
				TituloAcademico tituloAcademico = em.find(TituloAcademico.class, idTitulo);
				reportUtilFormController.getParametros().put("titulo", tituloAcademico.getDescripcion());
			}
			else{
				reportUtilFormController.getParametros().put("titulo", "Todos");
			}
			
			if (idEspecialidad != null){
				Especialidades especialidades = em.find(Especialidades.class, idEspecialidad);
				reportUtilFormController.getParametros().put("especialidad", especialidades.getDescripcion());
			}
			else{
				reportUtilFormController.getParametros().put("especialidad", "Todos");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private void buildFormatoSelectItem() {
		formatoSelectItem = new ArrayList<SelectItem>();
		formatoSelectItem.add(new SelectItem("EXCEL", "EXCEL"));
		formatoSelectItem.add(new SelectItem("PDF", "PDF"));
	}
	

	private void buildTipoSelectItem() {
		tipoSelectItem = new ArrayList<SelectItem>();
		tipoSelectItem.add(new SelectItem("D", "Detallado"));
		tipoSelectItem.add(new SelectItem("R", "Resumido"));
	}


	
	public boolean mostrarFiltrosAdicionales(){
		if ("D".equalsIgnoreCase(tipo))
			return true;
			
		return false;
	}
	
	
	public void initSet(){

	}
	

	public void setFormato(String formato) {
		this.formato = formato;
	}
	public String getFormato() {
		return formato;
	}

	public void setFormatoSelectItem(List<SelectItem> formatoSelectItem) {
		this.formatoSelectItem = formatoSelectItem;
	}
	public List<SelectItem> getFormatoSelectItem() {
		if (formatoSelectItem == null)
			buildFormatoSelectItem();
		return formatoSelectItem;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getTipo() {
		return tipo;
	}

	public void setTipoSelectItem(List<SelectItem> tipoSelectItem) {
		this.tipoSelectItem = tipoSelectItem;
	}
	public List<SelectItem> getTipoSelectItem() {
		if (tipoSelectItem == null)
			buildTipoSelectItem();
		return tipoSelectItem;
	}


	public void setTituloSelectItem(List<SelectItem> tituloSelectItem) {
		this.tituloSelectItem = tituloSelectItem;
	}
	public List<SelectItem> getTituloSelectItem() {
		return tituloSelectItem;
	}

	public void setEspecialidadSelectItem(List<SelectItem> especialidadSelectItem) {
		this.especialidadSelectItem = especialidadSelectItem;
	}
	public List<SelectItem> getEspecialidadSelectItem() {
		return especialidadSelectItem;
	}


	public void setIdTitulo(Long idTitulo) {
		this.idTitulo = idTitulo;
	}


	public Long getIdTitulo() {
		return idTitulo;
	}


	public void setIdEspecialidad(Long idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}


	public Long getIdEspecialidad() {
		return idEspecialidad;
	}
}
