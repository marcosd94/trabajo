package py.com.excelsis.sicca.evaluacion.session.form;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PlantillaEvalEscala;
import py.com.excelsis.sicca.evaluacion.session.PlantillaEvalConfDetList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("configurarPlantillaEvalRecFC")
public class ConfigurarPlantillaEvalRecFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false, create = true)
	PlantillaEvalConfDetList plantillaEvalConfDetList;

	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;


	private List<PlantillaEvalConfDet> plantillaEvalConfDets= new Vector<PlantillaEvalConfDet>();
	private Long idEvaluacionDesempeno = null;
	private Long idGrupoEvaluacion = null;
	private Long idMetodologia = null;
	private String evaluacion;
	private String grupo;
	private String metodologia;
	private Long idTipo=null;
	private String criterio=null;
	private String ver;
	
	
	/**
	 * Recibe como parámetro: ( ID_GRUPO_METODOLOGIA)
	 */
	private Long idGrupoMetodologia;
	private String from;

	public void init() {
		try {
			
			cargarCabecera();
			if(ver==null || ver.trim().equals("")){
				EvaluacionDesempeno eDesempeno = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
				//Se Deshabilitó condición UO; Werner.
//				if (!seguridadUtilFormController.verificarPerteneceUoDet(eDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet())) {
//					throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
//				}
			}
			search();
	
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	

	

	private void cargarCabecera() throws Exception {
		
		GrupoMetodologia grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		idEvaluacionDesempeno =
			grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		grupo = grupoMetodologia.getGruposEvaluacion().getGrupo();
		metodologia = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
		EvaluacionDesempeno eDesempeno = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		evaluacion = eDesempeno.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(eDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(eDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		criterio=null;
		idTipo=null;
	
	}

	public void search() {
		plantillaEvalConfDetList.setIdGrupoMetodologia(idGrupoMetodologia);
		plantillaEvalConfDetList.setIdtipo(idTipo);
		plantillaEvalConfDetList.getPlantillaEvalConfDet().setResultadoEsperado(criterio);
		plantillaEvalConfDetList.getPlantillaEvalConfDet().setActivo(true);
		plantillaEvalConfDets=plantillaEvalConfDetList.listaResultados();
		GrupoMetodologia grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		//agregado; Werner.
		if (plantillaEvalConfDets.isEmpty()){ 
			grupoMetodologia.setCargado(false);em.merge(grupoMetodologia);}
		 else{ 
			grupoMetodologia.setCargado(true);em.merge(grupoMetodologia);}
		em.flush();
		//***********************
	}

	public void searchAll() {
		idTipo=null;
		criterio=null;
		search();
	}

	public List<SelectItem> updateTipoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipo())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipo() {
		try {
			List<DatosEspecificos> datosEspecificosLists =
				em.createQuery("Select d from DatosEspecificos d "
					+ " where d.datosGenerales.descripcion like 'TIPOS DE VARIABLES DE EVALUACION'  and d.activo=true order by d.descripcion").getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	

	public void eliminar(Long id) {
		PlantillaEvalConfDet confDetDelet=em.find(PlantillaEvalConfDet.class, id);
		eliminarDetalles(id);
//		confDetDelet.setActivo(false);
//		confDetDelet.setUsuMod(usuarioLogueado.getCodigoUsuario());
//		confDetDelet.setFechaMod(new Date());
//		em.merge(confDetDelet);
		em.remove(confDetDelet);
		recalcularOrden(id);
//		em.remove(confDetDelet);
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		search();
	}
	@SuppressWarnings("unchecked")
	private void eliminarDetalles(Long id){
		List<PlantillaEvalEscala> escalas=em.createQuery("Select d from PlantillaEvalEscala d " +
				" where d.activo=true and d.plantillaEvalConfDet.idPlantillaEvalConfDet=:idCab").setParameter("idCab", id).getResultList();
		for (PlantillaEvalEscala plantillaEvalEscala : escalas) {
//			plantillaEvalEscala.setActivo(false);
//			plantillaEvalEscala.setFechaMod(new Date());
//			plantillaEvalEscala.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			em.merge(plantillaEvalEscala);
			em.remove(plantillaEvalEscala);
		}
	}

	@SuppressWarnings("unchecked")
	private void recalcularOrden(Long id){
		String sql=" select p from PlantillaEvalConfDet p " +
		" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true and p.idPlantillaEvalConfDet!=:id  order by p.orden";
		List<PlantillaEvalConfDet> confDets=em.createQuery(sql)
		.setParameter("idGrupo",idGrupoMetodologia).setParameter("id",id).getResultList();
		Long o=new Long(1);
		for (PlantillaEvalConfDet pConfDet : confDets) {
			PlantillaEvalConfDet det=new PlantillaEvalConfDet();
			det.setActividades(pConfDet.getActividades());
			det.setActivo(pConfDet.getActivo());
			det.setDatosEspecificoByTipoVar(pConfDet.getDatosEspecificoByTipoVar());
			det.setDescripcion(pConfDet.getDescripcion());
			det.setFechaAlta(pConfDet.getFechaAlta());
			det.setFechaMod(pConfDet.getFechaMod());
			det.setGrupoMetodologia(pConfDet.getGrupoMetodologia());
			det.setIdPlantillaEvalConfDet(pConfDet.getIdPlantillaEvalConfDet());
			det.setIndicadoresCump(pConfDet.getIndicadoresCump());
			det.setOrden(pConfDet.getOrden());
			det.setResultadoEsperado(pConfDet.getResultadoEsperado());
			det.setUsuAlta(pConfDet.getUsuAlta());
			det.setUsuMod(pConfDet.getUsuMod());
			det.setOrden(o);
			
			//agregado
			det.setPesoPercepcion(pConfDet.getPesoPercepcion());
			//******************			
			
			o++;
			em.merge(det);
		}
	}
	
	public void imprimirCriterios() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		
		reportUtilFormController.setNombreReporte("RPT_CU641_1");
		ConfiguracionUoCab oeeUsuario=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupoMetodologia", idGrupoMetodologia);
		reportUtilFormController.imprimirReportePdf();

	}
	public void imprimirEvaluacion() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		
		reportUtilFormController.setNombreReporte("RPT_CU641_2");
		ConfiguracionUoCab oeeUsuario=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupoMetodologia", idGrupoMetodologia);
		reportUtilFormController.imprimirReportePdf();

	}
	
	//agregado****************************
	public Float suma(){
		Float suma=0F;
		for (int i = 0; i < plantillaEvalConfDets.size(); i++)
			suma = suma + plantillaEvalConfDets.get(i).getPesoPercepcion();
		return suma;
	}
	//************************************
	
	// GETTERS Y SETTERS

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public Long getIdGrupoEvaluacion() {
		return idGrupoEvaluacion;
	}

	public void setIdGrupoEvaluacion(Long idGrupoEvaluacion) {
		this.idGrupoEvaluacion = idGrupoEvaluacion;
	}

	public Long getIdMetodologia() {
		return idMetodologia;
	}

	public void setIdMetodologia(Long idMetodologia) {
		this.idMetodologia = idMetodologia;
	}

	public String getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public Long getIdGrupoMetodologia() {
		return idGrupoMetodologia;
	}

	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}


	

	public List<PlantillaEvalConfDet> getPlantillaEvalConfDets() {
		return plantillaEvalConfDets;
	}

	public void setPlantillaEvalConfDets(
			List<PlantillaEvalConfDet> plantillaEvalConfDets) {
		this.plantillaEvalConfDets = plantillaEvalConfDets;
	}

	public String getCriterio() {
		return criterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}


	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}
	

}
