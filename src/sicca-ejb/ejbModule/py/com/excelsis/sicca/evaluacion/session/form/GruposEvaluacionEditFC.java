package py.com.excelsis.sicca.evaluacion.session.form;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ComisionEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GruposEvaluacion;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.ListarConfiguracionUoDetFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("gruposEvaluacionEditFC")
@Scope(ScopeType.CONVERSATION)
public class GruposEvaluacionEditFC implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;



	

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false,create=true)
	ListarConfiguracionUoDetFormController listarConfiguracionUoDetFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	private GruposEvaluacion gruposEvaluacion;

	private Long idPaisExp=null;
	private String docIdentidad=null;
	private String evaluacion=null;
	private Boolean primeraEntrada = true;
	private Long idGrupoEvaluacion=null;
	private Long idEvaluacionDesempeno;
	private String grupo=null;
	private Long idComisionEval=null;
	private List<Sujetos> sujetosLista= new ArrayList<Sujetos>();
	private List<GruposSujetos> gruposSujetoLista= new ArrayList<GruposSujetos>();
	private List<GruposSujetos> delgruposSujetoLista= new ArrayList<GruposSujetos>();
	private String codigoUnidOrgDep;
	private String denominacion;
	private Boolean seleccionarTodos = false;
	private String puesto;
	
	private String gruposEvaluacionFrom;
	private String from;
	
	public void init() {
		cargarCabecera();
//		seguridadUtilFormController.verificarPerteneceOee(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
			
		
		if (primeraEntrada) {
			gruposEvaluacion= new GruposEvaluacion();
			primeraEntrada=false;
			if (idGrupoEvaluacion!= null) 
				obtenerDatos();
			
			limpiar();
			
			
			findFuncionariosSinAgrupar();
		
		}	
		
		nivelEntidadOeeUtil.init();
		
		

	}
	public void initVer() {
		cargarCabecera();
//		seguridadUtilFormController.verificarPerteneceOee(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
			
		
		
		gruposEvaluacion= new GruposEvaluacion();
		primeraEntrada=false;
		if (idGrupoEvaluacion!= null) 
				obtenerDatos();
			
		limpiar();
			
			
		findFuncionariosSinAgrupar();
		nivelEntidadOeeUtil.init();
		
		

	}
	
	
	private void obtenerDatos(){
		gruposEvaluacion=em.find(GruposEvaluacion.class, idGrupoEvaluacion);
		grupo=gruposEvaluacion.getGrupo();
		idComisionEval=gruposEvaluacion.getComisionEval().getIdComisionEval();
		findFuncionariosAgrupados();
		
	}
	
	private void cargarCabecera(){
		if(idEvaluacionDesempeno==null && idGrupoEvaluacion!=null)
		{
			GruposEvaluacion g=em.find(GruposEvaluacion.class,idGrupoEvaluacion);
			idEvaluacionDesempeno=g.getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		}
		EvaluacionDesempeno e=em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		ConfiguracionUoCab uo=em.find(ConfiguracionUoCab.class, e.getConfiguracionUoCab().getIdConfiguracionUo());
		evaluacion=e.getTitulo();
		nivelEntidadOeeUtil.setIdConfigCab(uo.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
//		codigoUnidOrgDep=listarConfiguracionUoDetFormController.obtenerCodigoMod(uo);
//		denominacion=uo.getDenominacionUnidad();
	}

	private void findFuncionariosSinAgrupar(){
		search();
	}
	
	@SuppressWarnings("unchecked")
	private void findFuncionariosAgrupados(){
		gruposSujetoLista= em.createQuery(" Select gs from  GruposSujetos gs " +
				" where gs.activo=true and gs.gruposEvaluacion.idGruposEvaluacion="+idGrupoEvaluacion+ " order by  " +
				" gs.sujetos.empleadoPuesto.persona.nombres, gs.sujetos.empleadoPuesto.plantaCargoDet.configuracionUoDet.denominacionUnidad").getResultList();
	}
	@SuppressWarnings("unchecked")
	public void search(){
		//agregado
//		String sql;
//		if (from != null) {
//			if (from.contentEquals("/evaluacionDesempenho/admGruposEvaluacion/GruposEvaluacionList.xhtml")) {
//				sql="Select s from Sujetos s " +
//						" where s.activo=true and s.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +
//						"  and  s.idSujetos in (Select gs.sujetos.idSujetos" +
//						" from GruposSujetos gs where gs.activo=true and  gs.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +" ) ";
//			}else{
//				sql="Select s from Sujetos s " +
//						" where s.activo=true and s.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +
//						"  and  s.idSujetos not in (Select gs.sujetos.idSujetos" +
//						" from GruposSujetos gs where gs.activo=true and  gs.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +" ) ";
//			}
//		}else{
//			sql="Select s from Sujetos s " +
//					" where s.activo=true and s.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +
//					"  and  s.idSujetos not in (Select gs.sujetos.idSujetos" +
//					" from GruposSujetos gs where gs.activo=true and  gs.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +" ) ";
//		}
		//******************
		
		String sql="Select s from Sujetos s " +
				" where s.activo=true and s.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +
				"  and  s.idSujetos not in (Select gs.sujetos.idSujetos" +
				" from GruposSujetos gs where gs.activo=true and  gs.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno +" ) ";
		
		if(idPaisExp!=null)
			sql+=" and s.empleadoPuesto.persona.paisByIdPaisExpedicionDoc.idPais="+idPaisExp;
		if(docIdentidad!=null && !docIdentidad.trim().equals(""))
			sql+=" and s.empleadoPuesto.persona.documentoIdentidad='"+docIdentidad+"'";
		if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null)
			sql+=" and s.empleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet="+nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		else{
			if(nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep()!=null && !nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep().trim().equals(""))
				sql+=" and s.empleadoPuesto.plantaCargoDet.configuracionUoDet.denominacionUnidad like '%"+nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep()+"%'";
		}
		if(puesto!=null && !puesto.trim().equals(""))
			sql+=" and s.empleadoPuesto.plantaCargoDet.descripcion LIKE '"+puesto.toUpperCase()+"%'";
		sql+=" order by s.empleadoPuesto.persona.nombres ";
		sujetosLista=em.createQuery(sql).getResultList();
	}
	
	public String antiguedad(Long idSujeto){
		if(idSujeto==null)
			return null;
		Sujetos s= em.find(Sujetos.class, idSujeto);
		if(s==null)
			return null;
		return General.getAntiguedadFechas(s.getEmpleadoPuesto().getFechaInicio(), null);
	}
	
	public void limpiar(){
		docIdentidad=null;
		idPaisExp=General.getIdParaguay();
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
		nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
		puesto=null;
		search();
	}
	
	


	public void agregarAgrupados(){
		if(!selecciono()){
			statusMessages.add(Severity.ERROR,"Debe seleccionar por lo menos un funcionario antes de realizar esta acci&oacute;n");
			return;
		}
		
		if(existeFuncionario()){
			statusMessages.add(Severity.ERROR,"El Funcionario seleccionado ya existe, verifique ");
			return;
		}
		for (Sujetos s : sujetosLista) {
			if(s.isSelecciono()){
				GruposSujetos gruposSujetos= new GruposSujetos();
				gruposSujetos.setSujetos(s);
				gruposSujetoLista.add(gruposSujetos);	
			}
			
		}
		save();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		
	}
	public void selectAll() {
		if (seleccionarTodos != null) {
			for (Sujetos sujetos: sujetosLista) {
				if (seleccionarTodos)
					sujetos.setSelecciono(true);
				else
					sujetos.setSelecciono(false);
			}

		}
	}
	
	
	public void eliminar(int index){
		GruposSujetos gs=gruposSujetoLista.get(index);
//		if(gs.getIdGrupoSujeto()!=null)
//			delgruposSujetoLista.add(gs);
//		gruposSujetoLista.remove(index);
		//agregado;Werner.
		if(gruposSujetoLista.size()==1 && from.contentEquals("/evaluacionDesempenho/admGruposEvaluacion/GruposEvaluacionList.xhtml")){
			statusMessages.add(Severity.ERROR,"Debe agregar por lo menos un funcionario antes de realizar esta acci&oacute;n");
		}else{
			if(gs.getIdGrupoSujeto()!=null)
				delgruposSujetoLista.add(gs);
			gruposSujetoLista.remove(index);
			for (GruposSujetos gsdel : delgruposSujetoLista) {
				GruposSujetos del=gsdel;
				em.remove(em.merge(del));
			}
			em.flush();
			findFuncionariosSinAgrupar();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		}
		//****************
	}
		
	private boolean selecciono(){
		for (Sujetos suj : sujetosLista) {
			if(suj.isSelecciono())
				return true;
		}
		return false;
	}
	private boolean existeFuncionario(){
		for (Sujetos sujetos : sujetosLista) {
			if(sujetos.isSelecciono()){
				for (GruposSujetos gSujetos : gruposSujetoLista) {
					if(gSujetos.getSujetos().getIdSujetos()==sujetos.getIdSujetos())
						return true;
				}
			}
			
		}
		return false;
		
	}
	
	private boolean checkDatos(){
		if(grupo==null || grupo.trim().isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo grupo antes de realizar esta acci&oacute;n, verifique ");
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(grupo.trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(idComisionEval==null ){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar la Comisi&oacute;n de Evaluaci&oacute;n antes de realizar la acci&oacute;n, verifique");
			return false;
		}
		if(gruposSujetoLista.isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe agregar por lo menos un funcionario antes de realizar esta acci&oacute;n");
			return false;
		}
		if(existe()){
			statusMessages.add(Severity.ERROR,"El Grupo ingresado ya existe para la Evaluacion, verifique");
			return false;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	private boolean existe(){
		String sql="Select d from GruposEvaluacion d where lower(d.grupo)=:grupo " +
		" and d.evaluacionDesempeno.idEvaluacionDesempeno=:idEval and d.activo=true ";
		if(gruposEvaluacion.getIdGruposEvaluacion()!=null)
			sql+=" and d.idGruposEvaluacion!="+gruposEvaluacion.getIdGruposEvaluacion().longValue();
		List<GruposEvaluacion> evaluacions=em.createQuery(sql).setParameter("grupo", grupo.toLowerCase().trim()).setParameter("idEval", idEvaluacionDesempeno).getResultList();
		return !evaluacions.isEmpty();
	}
	
	public String save() {
			if(!checkDatos())
				return null;
			try {
//			em.clear();
				gruposEvaluacion.setGrupo(grupo);
				gruposEvaluacion.setComisionEval(em.find(ComisionEval.class, idComisionEval));
			
				if(idGrupoEvaluacion==null){
					gruposEvaluacion.setEvaluacionDesempeno(em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno));
					gruposEvaluacion.setFechaAlta(new Date());
					gruposEvaluacion.setActivo(true);
					gruposEvaluacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(gruposEvaluacion);
					
				}else{
					gruposEvaluacion.setFechaMod(new Date());
					gruposEvaluacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(gruposEvaluacion);
				}
				
				for (GruposSujetos gs : gruposSujetoLista) {
					GruposSujetos gruposSujetos= gs;
					gruposSujetos.setGruposEvaluacion(gruposEvaluacion);
					if(gruposSujetos.getIdGrupoSujeto()!=null){
						gruposSujetos.setFechaMod(new Date());
						gruposSujetos.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(gruposSujetos);
					}else{
						gruposSujetos.setFechaAlta(new Date());
						gruposSujetos.setActivo(true);
						gruposSujetos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(gruposSujetos);
					}
					
				
					
				
					
				}
//				for (GruposSujetos gsdel : delgruposSujetoLista) {
//					GruposSujetos del=gsdel;
//					del.setFechaMod(new Date());
//					del.setActivo(false);
//					del.setUsuMod(usuarioLogueado.getCodigoUsuario());
////					em.merge(del);//original
//					em.remove(em.merge(del));
//				}
				em.flush();
				
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
				
				return "persisted";
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				e.printStackTrace();
				return null;
			}
		
			
	}
		
	@SuppressWarnings("unchecked")
	public List<ComisionEval> getComisionEvals() {
		try {
			return em.createQuery(" SELECT o FROM "
				+ ComisionEval.class.getName() + " o "
				+ "WHERE o.activo = true and o.evaluacionDesempeno.idEvaluacionDesempeno=:idEval ORDER BY o.nombre ")
				.setParameter("idEval", idEvaluacionDesempeno).getResultList();
		} catch (Exception ex) {
			return new Vector<ComisionEval>();
		}
	}

	public List<SelectItem> getComisionEvalSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ComisionEval o : getComisionEvals())
			si.add(new SelectItem(o.getIdComisionEval(), "" + o.getNombre()));
		return si;
	}
	//agregado;Werner.
	public Boolean ocultandoPaneles(){
		if (gruposEvaluacionFrom.contentEquals("/evaluacionDesempenho/admGruposEvaluacion/GruposEvaluacionBaja.xhtml")) {
			return true;
		}else{
			return false;
		}
	}	
	//******************
		
		
	// GETTER Y SETTER

	
	

	
	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}



	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	public String getEvaluacion() {
		return evaluacion;
	}
	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}
	

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public GruposEvaluacion getGruposEvaluacion() {
		return gruposEvaluacion;
	}

	public void setGruposEvaluacion(GruposEvaluacion gruposEvaluacion) {
		this.gruposEvaluacion = gruposEvaluacion;
	}
	public Long getIdGrupoEvaluacion() {
		return idGrupoEvaluacion;
	}
	public void setIdGrupoEvaluacion(Long idGrupoEvaluacion) {
		this.idGrupoEvaluacion = idGrupoEvaluacion;
	}
	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}
	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}


	public Long getIdComisionEval() {
		return idComisionEval;
	}


	public void setIdComisionEval(Long idComisionEval) {
		this.idComisionEval = idComisionEval;
	}


	public List<Sujetos> getSujetosLista() {
		return sujetosLista;
	}


	public void setSujetosLista(List<Sujetos> sujetosLista) {
		this.sujetosLista = sujetosLista;
	}


	public List<GruposSujetos> getGruposSujetoLista() {
		return gruposSujetoLista;
	}


	public void setGruposSujetoLista(List<GruposSujetos> gruposSujetoLista) {
		this.gruposSujetoLista = gruposSujetoLista;
	}


	public List<GruposSujetos> getDelgruposSujetoLista() {
		return delgruposSujetoLista;
	}


	public void setDelgruposSujetoLista(List<GruposSujetos> delgruposSujetoLista) {
		this.delgruposSujetoLista = delgruposSujetoLista;
	}


	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}


	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}


	public String getDenominacion() {
		return denominacion;
	}


	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}


	public Boolean getSeleccionarTodos() {
		return seleccionarTodos;
	}


	public void setSeleccionarTodos(Boolean seleccionarTodos) {
		this.seleccionarTodos = seleccionarTodos;
	}
	public String getPuesto() {
		return puesto;
	}
	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}
	public String getGruposEvaluacionFrom() {
		return gruposEvaluacionFrom;
	}
	public void setGruposEvaluacionFrom(String gruposEvaluacionFrom) {
		this.gruposEvaluacionFrom = gruposEvaluacionFrom;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}






}
