package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EscalaEval;
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
@Name("configurarPlantillaEvalRecEditFC")
public class ConfigurarPlantillaEvalRecEditFC implements Serializable {

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
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	
	@In(required = false, create = true)
	PlantillaEvalConfDetList plantillaEvalConfDetList;

	private List<PlantillaEvalEscala> evalEscalas= new Vector<PlantillaEvalEscala>();
	private List<PlantillaEvalEscala> eliminarEvalDets = new ArrayList<PlantillaEvalEscala>();
	private Long idEvaluacionDesempeno = null;
	private Long idGrupoEvaluacion = null;
	private Long idMetodologia = null;
	private String evaluacion=null;
	private String grupo=null;
	private String plantilla=null;
	private Long orden=null;
	private String criterio=null;
	private Long idtipo=null;
	private String descripcion=null;
	private Long idEscalaPuntaje=null;
	private String descripcionNivel;
	private PlantillaEvalConfDet plantillaEvalConfDet= new PlantillaEvalConfDet();
	
	private Long idPlantillaEvalConfDet=null;
	private boolean modoUpdate;
	private Long maxOrden = null;
	private int indexUpdate = -1;
	private boolean esEdit;
	/**
	 * Recibe como parámetro: ( ID_GRUPO_METODOLOGIA)
	 */
	private Long idGrupoMetodologia;
	private String from;
	
//agregado; Werner.
	private float pesoPercepcion;
	private List<PlantillaEvalConfDet> plantillaEvalConfDets= new Vector<PlantillaEvalConfDet>();
	private Long idTipo2 =null;
	private String criterio2=null;
	//****************************

	public void init() throws Exception {
		
			cargarCabecera();
			EvaluacionDesempeno eDesempeno = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
			//Se deshabilitó condición UO; Werner.
//			if (!seguridadUtilFormController.verificarPerteneceUoDet(eDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet())) {
//				throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
//			}
			if(idPlantillaEvalConfDet!=null){
				cargarDatos();
			}else
			{
				limpiar2();
			}
			search();
			
	}
	private void cargarDatos(){
		plantillaEvalConfDet=em.find(PlantillaEvalConfDet.class, idPlantillaEvalConfDet);
		criterio=plantillaEvalConfDet.getResultadoEsperado();
		idtipo=plantillaEvalConfDet.getDatosEspecificoByTipoVar().getIdDatosEspecificos();
		descripcion=plantillaEvalConfDet.getDescripcion();
		modoUpdate=true;
		orden=plantillaEvalConfDet.getOrden();
		//agregado...
		pesoPercepcion = plantillaEvalConfDet.getPesoPercepcion();
		//***********
	}

	public void initVer() {
		try {
			
			cargarCabecera();
			//agregado...
			if(idPlantillaEvalConfDet!=null){
				cargarDatos();
			}else
			{
				limpiar2();
			}
			//***********
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
		plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
		EvaluacionDesempeno eDesempeno = em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		evaluacion = eDesempeno.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(eDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(eDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		calculoMaxNroOrden();
		
	}

	@SuppressWarnings("unchecked")
	public void search() {
		//agregado; Werner.
		searchOtroFormulario();
		//************************
		
		evalEscalas =
			em.createQuery("Select d from PlantillaEvalEscala d "
				+ " where d.plantillaEvalConfDet.idPlantillaEvalConfDet=:idCab "
				+ " and d.activo=true  ").setParameter("idCab", idPlantillaEvalConfDet).getResultList();

	}
	private boolean checkValuesAdd(){
//		if(idEscalaPuntaje==null){
//			statusMessages.add(Severity.ERROR,"Debe completar el campo Escala/Puntaje antes de realizar esta acción");
//			return false;
//		}
//		if(descripcionNivel==null|| descripcionNivel.trim().equals("")){
//			statusMessages.add(Severity.ERROR,"Debe completar el campo Descripci&oacute;n del Nivel antes de realizar esta acci&oacute;n");
//			return false;
//		}
		boolean existe=false;
		for (int i = 0; i < evalEscalas.size(); i++) {
			PlantillaEvalEscala es=evalEscalas.get(i);
				if(es.getEscalaEval().getIdEscalaEval().equals(idEscalaPuntaje)&&
						es.getDescripcion().trim().toLowerCase().equals(descripcionNivel.trim().toLowerCase())){
					if(esEdit)
					{
						if(i!=indexUpdate)
							existe=true;
					}else
						existe=true;
				}
			
		}
		if(existe){
			statusMessages.add(Severity.ERROR," El registro ingresado ya existe, verifique");
			return false;
		}
			
			
		
		return true;
	}
	
	private void limpiandoEscala(){
		evalEscalas = new Vector<PlantillaEvalEscala>();
		limpiar2();
	}

	@SuppressWarnings("unchecked")
	private boolean checkValues() {
		if(criterio==null|| criterio.trim().equals("")){
			limpiandoEscala();
			statusMessages.add(Severity.ERROR,"Debe completar el campo Criterio antes de realizar esta acci&oacute;n");
			return false;
		}
		if(idtipo==null){
			limpiandoEscala();
			statusMessages.add(Severity.ERROR,"Debe completar el campo Tipo antes de realizar esta acci&oacute;n");
			return false;
		}
		
		if (orden == null) {
			limpiandoEscala();
			statusMessages.add(Severity.ERROR, "Debe completar el campo Orden antes de realizar esta acci&oacute;n");
			return false;
		}
		if (orden.longValue() <= 0) {
			limpiandoEscala();
			statusMessages.add(Severity.ERROR, "El campo Orden debe ser mayor a cero");
			return false;
		}
		if (orden.intValue() > maxOrden.intValue()) {
			limpiandoEscala();
			statusMessages.add(Severity.ERROR, "El campo orden no puede ser mayor a el Maximo sugerido "
				+ maxOrden);
			return false;
		}
//		if(evalEscalas.isEmpty()){
//			statusMessages.add(Severity.ERROR,"Debe ingresar almenos items");
//			return false;
//		}
		
		String sql= " Select d from PlantillaEvalConfDet d " +
				" where d.activo=true and d.grupoMetodologia.idGrupoMetodologia=:idGrupo and " +
				" d.datosEspecificoByTipoVar.idDatosEspecificos=:idTipo and lower(d.resultadoEsperado) like :criterio";
		if(idPlantillaEvalConfDet!=null)
			sql+=" and d.idPlantillaEvalConfDet!="+idPlantillaEvalConfDet;
		
		List<PlantillaEvalConfDet> confDets=em.createQuery(sql).setParameter("idTipo", idtipo)
		.setParameter("idGrupo", idGrupoMetodologia).setParameter("criterio",criterio.toLowerCase()).getResultList();
		if(!confDets.isEmpty()){
			limpiandoEscala();
			statusMessages.add(Severity.ERROR,"El registro ingresado ya existe, verifique");
			return false;
		}
		//agregado; Werner.
		if (pesoPercepcion <= 0) {
			limpiandoEscala();
			statusMessages.add(Severity.ERROR, "El campo Peso debe ser mayor a cero");
			return false;
		}

		return true;
	}

	public void agregar() {
		try {
//			if (!checkValuesAdd())
//				return;
//			PlantillaEvalEscala escala= new PlantillaEvalEscala();
			
//			if(esEdit){
//				escala=evalEscalas.get(indexUpdate);
//				escala.setFechaMod(new Date());
//				escala.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			}else{
//				escala.setFechaAlta(new Date());
//				escala.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//			}
			//agregado; Werner.
			for (EscalaEval o : listaEscala()){
				PlantillaEvalEscala escala= new PlantillaEvalEscala();
				escala.setFechaAlta(new Date());
				escala.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				escala.setEscalaEval(em.find(EscalaEval.class, o.getIdEscalaEval()));
				evalEscalas.add(escala);
			}
			//******************
			
//			escala.setEscalaEval(em.find(EscalaEval.class, idEscalaPuntaje));//original
//			escala.setDescripcion(descripcionNivel);//original
			
//			if(esEdit)
//				evalEscalas.remove(indexUpdate);
//			evalEscalas.add(escala);
			limpiar();
			save();
//			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void limpiar(){
		idEscalaPuntaje=null;
		descripcionNivel=null;
		esEdit=false;
		indexUpdate=-1;
	}
	public void limpiar2(){
		limpiar();
		modoUpdate=false;
		descripcion=null;
		idtipo=null;
		criterio=null;
		evalEscalas= new Vector<PlantillaEvalEscala>();
		eliminarEvalDets= new Vector<PlantillaEvalEscala>();
		idPlantillaEvalConfDet=null;
		plantillaEvalConfDet= new PlantillaEvalConfDet();
	}

	private void calculoMaxNroOrden() {
		try {
			maxOrden =
				(Long) em.createQuery("Select max(p.orden) from PlantillaEvalConfDet p"
					+ " where p.grupoMetodologia.idGrupoMetodologia=:idGrupoMet and p.activo=true").setParameter("idGrupoMet", idGrupoMetodologia).getSingleResult();
			if (maxOrden == null) {
				maxOrden = new Long(1);
			} else
				maxOrden = maxOrden + 1;
			orden = maxOrden;
		} catch (NoResultException e) {
			orden = new Long(1);
		}

	}

	

	public void volver(){
		limpiar2();
	}
	
	
	public void editar(int index) {
		PlantillaEvalEscala escala=evalEscalas.get(index);
		idEscalaPuntaje=escala.getEscalaEval().getIdEscalaEval();
		descripcionNivel=escala.getDescripcion();
		esEdit=true;
		indexUpdate = index;
	}

	public String save() {
		try {
			if(!checkValues())
				return null;
			verificarOrden();
			PlantillaEvalConfDet plantillaEvalConfDet= new PlantillaEvalConfDet();
			plantillaEvalConfDet.setOrden(orden);
			plantillaEvalConfDet.setGrupoMetodologia(em.find(GrupoMetodologia.class, idGrupoMetodologia));
			plantillaEvalConfDet.setDescripcion(descripcion);
			plantillaEvalConfDet.setDatosEspecificoByTipoVar(em.find(DatosEspecificos.class, idtipo));
			plantillaEvalConfDet.setResultadoEsperado(criterio);
			plantillaEvalConfDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			plantillaEvalConfDet.setFechaAlta(new Date());
			plantillaEvalConfDet.setActivo(true);
			//agregado; Werner.
			plantillaEvalConfDet.setPesoPercepcion(pesoPercepcion);
			//******************
			em.persist(plantillaEvalConfDet);
			
			for (PlantillaEvalEscala escala :evalEscalas) {
				escala.setActivo(true);
				escala.setPlantillaEvalConfDet(plantillaEvalConfDet);
				escala.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				escala.setFechaAlta(new Date());
				em.persist(escala);
			}
			
			
			
			em.flush();
			limpiar2();
			//agregado
			
			limpiandoEscala();
			init();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Se ha producido un error: " + e.getMessage());
			return null;
		}
	}
	public String update() {
		try {
			if(!checkValues())
				return null;
			verificarOrden();
			PlantillaEvalConfDet plantillaEvalConfDet=em.find(PlantillaEvalConfDet.class, idPlantillaEvalConfDet);
			plantillaEvalConfDet.setOrden(orden);
			plantillaEvalConfDet.setDescripcion(descripcion);
			plantillaEvalConfDet.setDatosEspecificoByTipoVar(em.find(DatosEspecificos.class, idtipo));
			plantillaEvalConfDet.setResultadoEsperado(criterio);
			plantillaEvalConfDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			plantillaEvalConfDet.setFechaMod(new Date());
			//agregado; Werner.
			plantillaEvalConfDet.setPesoPercepcion(pesoPercepcion);
			//**************
			em.merge(plantillaEvalConfDet);
			
			for (PlantillaEvalEscala escala :evalEscalas) {
				escala.setActivo(true);
				escala.setPlantillaEvalConfDet(plantillaEvalConfDet);
				if(escala.getIdPlantillaEvalEscala()!=null){
					escala.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					escala.setFechaAlta(new Date());
					em.merge(escala);
				}else{
					escala.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					escala.setFechaAlta(new Date());
					em.persist(escala);
				}
				
			}
			
			
			for (PlantillaEvalEscala escalaDel :eliminarEvalDets) {
				escalaDel.setActivo(false);
				escalaDel.setUsuMod(usuarioLogueado.getCodigoUsuario());
				escalaDel.setFechaMod(new Date());
				em.merge(escalaDel);
			}
			
			
			em.flush();
			modoUpdate=false;
			limpiar2();
			//agregado
			searchOtroFormulario();
			init();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Se ha producido un error: " + e.getMessage());
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	private void verificarOrden(){
		String sql=" select p from PlantillaEvalConfDet p " +
				" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true and orden =:orden";
		if(idPlantillaEvalConfDet!=null)
			sql+=" and p.idPlantillaEvalConfDet.idPlantillaEvalConfDet!="+idPlantillaEvalConfDet;
		List<PlantillaEvalConfDet> confDets=em.createQuery(sql)
		.setParameter("idGrupo", idGrupoMetodologia).setParameter("orden", orden).getResultList();
		if(!confDets.isEmpty())
			recalcular();
	}
	
	@SuppressWarnings("unchecked")
	private void recalcular(){
		String sql=" select p from PlantillaEvalConfDet p " +
		" where p.grupoMetodologia.idGrupoMetodologia=:idGrupo and p.activo=true  ";
		List<PlantillaEvalConfDet> todos=em.createQuery(sql+" order by p.orden ").setParameter("idGrupo", idGrupoMetodologia).getResultList();
		
		if(idPlantillaEvalConfDet!=null)
			sql+=" and p.idPlantillaEvalConfDet.idPlantillaEvalConfDet!="+idPlantillaEvalConfDet;
		
		sql+=" order by p.orden ";
		List<PlantillaEvalConfDet> confDets=em.createQuery(sql)
		.setParameter("idGrupo", idGrupoMetodologia).getResultList();
		Long o=todos.get(0).getOrden();
	
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
			det.setPesoPercepcion(pConfDet.getPesoPercepcion());
			
			if(o.equals(orden) )
			{
				det.setOrden(o+1);
			
			}
			else
				det.setOrden(o);
			
			if(pConfDet.getOrden().equals(orden))
				o++;
			det.setUsuMod(usuarioLogueado.getCodigoUsuario());
			det.setFechaMod(new Date());
			em.merge(det);
			
			o++;
		}
	}


	public void eliminar(int index) {
		PlantillaEvalEscala escalaDel=evalEscalas.get(index);
		if(escalaDel.getIdPlantillaEvalEscala()!=null)
			eliminarEvalDets.add(escalaDel);
		
		evalEscalas.remove(index);
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
	public List<SelectItem> escalaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (EscalaEval o : listaEscala())
			si.add(new SelectItem(o.getIdEscalaEval(), "" + o.getDescripcion()+"-desde "+o.getDesde()+"/hasta "+o.getHasta()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<EscalaEval> listaEscala() {
		try {
			List<EscalaEval> escalaLists =
				em.createQuery("Select d from EscalaEval d "
					+ " where d.grupoMetodologia.idGrupoMetodologia=:idGrupo  and d.activo=true order by d.desde")
					.setParameter("idGrupo", idGrupoMetodologia).getResultList();

			return escalaLists;
		} catch (Exception ex) {
			return new Vector<EscalaEval>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean deshabilitandoAgregar() {
		return !evalEscalas.isEmpty();
	}

	
	public void searchOtroFormulario() {
		criterio2=null;
		idTipo2=null;
		
//		plantillaEvalConfDetList = new PlantillaEvalConfDetList();
		
		plantillaEvalConfDetList.setIdGrupoMetodologia(idGrupoMetodologia);
		plantillaEvalConfDetList.setIdtipo(idTipo2);
		plantillaEvalConfDetList.getPlantillaEvalConfDet().setResultadoEsperado(criterio2);
		plantillaEvalConfDetList.getPlantillaEvalConfDet().setActivo(true);
		
		plantillaEvalConfDets= new Vector<PlantillaEvalConfDet>();
		
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
	//agregado; Werner.***********************************************
	public void eliminar2(Long id) {
		PlantillaEvalConfDet confDetDelet=em.find(PlantillaEvalConfDet.class, id);
		eliminarDetalles(id);
		em.remove(confDetDelet);
		recalcularOrden(id);
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		limpiar2();
		calculoMaxNroOrden();
		search();
	}
	@SuppressWarnings("unchecked")
	private void eliminarDetalles(Long id){
		List<PlantillaEvalEscala> escalas=em.createQuery("Select d from PlantillaEvalEscala d " +
				" where d.activo=true and d.plantillaEvalConfDet.idPlantillaEvalConfDet=:idCab").setParameter("idCab", id).getResultList();
		for (PlantillaEvalEscala plantillaEvalEscala : escalas) {
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
			det.setPesoPercepcion(pConfDet.getPesoPercepcion());		
			o++;
			em.merge(det);
		}
	}
	//***************************************************************
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



	
	public boolean isModoUpdate() {
		return modoUpdate;
	}

	public void setModoUpdate(boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

	
	public String getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	public Long getOrden() {
		return orden;
	}

	public void setOrden(Long orden) {
		this.orden = orden;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	
	public int getIndexUpdate() {
		return indexUpdate;
	}

	public void setIndexUpdate(int indexUpdate) {
		this.indexUpdate = indexUpdate;
	}

	public Long getMaxOrden() {
		return maxOrden;
	}

	public void setMaxOrden(Long maxOrden) {
		this.maxOrden = maxOrden;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<PlantillaEvalEscala> getEvalEscalas() {
		return evalEscalas;
	}

	public void setEvalEscalas(List<PlantillaEvalEscala> evalEscalas) {
		this.evalEscalas = evalEscalas;
	}

	public List<PlantillaEvalEscala> getEliminarEvalDets() {
		return eliminarEvalDets;
	}

	public void setEliminarEvalDets(List<PlantillaEvalEscala> eliminarEvalDets) {
		this.eliminarEvalDets = eliminarEvalDets;
	}

	public String getCriterio() {
		return criterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public Long getIdtipo() {
		return idtipo;
	}

	public void setIdtipo(Long idtipo) {
		this.idtipo = idtipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getIdEscalaPuntaje() {
		return idEscalaPuntaje;
	}

	public void setIdEscalaPuntaje(Long idEscalaPuntaje) {
		this.idEscalaPuntaje = idEscalaPuntaje;
	}

	public Long getIdPlantillaEvalConfDet() {
		return idPlantillaEvalConfDet;
	}

	public void setIdPlantillaEvalConfDet(Long idPlantillaEvalConfDet) {
		this.idPlantillaEvalConfDet = idPlantillaEvalConfDet;
	}

	public String getDescripcionNivel() {
		return descripcionNivel;
	}

	public void setDescripcionNivel(String descripcionNivel) {
		this.descripcionNivel = descripcionNivel;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}
	public float getPesoPercepcion() {
		return pesoPercepcion;
	}
	public void setPesoPercepcion(float pesoPercepcion) {
		this.pesoPercepcion = pesoPercepcion;
	}
	public List<PlantillaEvalConfDet> getPlantillaEvalConfDets() {
		return plantillaEvalConfDets;
	}
	public void setPlantillaEvalConfDets(List<PlantillaEvalConfDet> plantillaEvalConfDets) {
		this.plantillaEvalConfDets = plantillaEvalConfDets;
	}

	
}
