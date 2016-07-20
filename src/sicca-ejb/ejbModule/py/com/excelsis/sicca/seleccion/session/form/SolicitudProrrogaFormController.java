package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("solicitudProrrogaFormController")
public class SolicitudProrrogaFormController implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2733138495988896462L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	SolicProrrogaCabList solicProrrogaCabList;
	
	@In(create = true)
	SolicProrrogaCabHome solicProrrogaCabHome;
	
	@In(create = true)
	SolicProrrogaDetList solicProrrogaDetList;
	
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	
	@In(scope=ScopeType.SESSION, required=false)  
	@Out(scope=ScopeType.SESSION, required=false)  
	String roles; 
	
	private Long idConcursoPuestoAgr;
	private String concurso;
	private String grupoPuesto;
	private Date fechaDesde;
	private Date fechaHasta;

	private List<SolicProrrogaCab> listaSolicProrrogaCab;

	//Edit
	private Long idSolicCab;
	private Long idSolicDet;
	private String fechaPor;
	private Long idConcurso;
	private Date fechaFin;
	private Boolean activo;
	private SolicProrrogaCab solicProrrogaCab;
	private SolicProrrogaDet solicProrrogaDet;
	private String estado;
	private FechasGrupoPuesto fechasGrupoPuesto;
	private Boolean modoVista;
	
	private final String CODIGO_FECHA_POR_CONCURSO = "C";
	private final String CODIGO_FECHA_POR_GRUPO = "G";
	
	private final String ESTADO_PENDIENTE_APROBACION = "PENDIENTE APROBACION";
	private final String ESTADO_A_SOLICITAR = "A SOLICITAR";
	private final String ESTADO_PENDIENTE_REVISION = "PENDIENTE REVISION";
	
	private List<SelectItem> concursoSelecItem;
	private List<SelectItem> grupoPuestoSelecItem;
	
	private int selectedRow = 0;
	
	private Long idExcepcion;
	
	private String from;

	 /**
	  * Método que inicia los parametros
	  */
	public void init() {
		search();
	}
	
	public void searchAll(){
		concurso = null;
		grupoPuesto = null;
		fechaDesde = null;
		fechaHasta = null;
		search();
	}
	
	public void search(){
		if (fechaDesde != null && fechaHasta != null && fechaDesde.after(fechaHasta)) {
            statusMessages.add(Severity.ERROR, "El rango de fechas es inv\u00E1lido, la Fecha Desde no debe superar la Fecha Hasta.");
            return;
        }
		
		buscar();
	}
	
	public void buscar(){
		String consulta = "" +
			"select solicProrrogaCab from SolicProrrogaCab solicProrrogaCab " +
			" join solicProrrogaCab.fechasGrupoPuesto fechasGrupoPuesto " +
			" left join fechasGrupoPuesto.concurso concurso " +
			" left join fechasGrupoPuesto.concursoPuestoAgr concursoPuestoAgr " +
			" left join concursoPuestoAgr.concurso concurso2 " +
			" where (concurso.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo or concurso2.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo)";
		
		if(!funcionarioUtilFormController.vacio(concurso)){
			consulta += " and (lower(concurso.nombre) like :nombreConcurso or lower(concurso2.nombre) like :nombreConcurso) ";
		}
		
		if(!funcionarioUtilFormController.vacio(grupoPuesto)){
			consulta += " and lower(concursoPuestoAgr.descripcionGrupo) like :grupoPuesto ";
		}
		
		if(fechaDesde != null){
			consulta += " and cast(solicProrrogaCab.fechaSol as date) >= cast(:fechaDesde as date)  ";
		}
		
		if(fechaHasta != null){
			consulta += " and cast(solicProrrogaCab.fechaSol as date) <= cast(:fechaHasta as date) ";
		}
		
	//	Query q = em.createQuery(consulta);
		Map<String, QueryValue> params = new HashMap<String, QueryValue>();
		params.put("idConfiguracionUo", new QueryValue(grupoPuestosController.getConfiguracionUoCab().getIdConfiguracionUo()));
		
		if(!funcionarioUtilFormController.vacio(concurso))
			params.put("nombreConcurso", new QueryValue("%" + concurso + "%"));
		
		if(!funcionarioUtilFormController.vacio(grupoPuesto))
			params.put("grupoPuesto", new QueryValue("%" + grupoPuesto + "%"));
		
		if(fechaDesde != null)
			params.put("fechaDesde", new QueryValue(fechaDesde));
		
		if(fechaHasta!= null)
			params.put("fechaHasta", new QueryValue(fechaHasta));
		
		listaSolicProrrogaCab = solicProrrogaCabList.listaResultadosCU234(consulta, params);
	}
	
	/**********************************************************************************/
	/********************************* EDITAR *****************************************/
	/**********************************************************************************/
	
	public void initEdit() {
		if (isNew()){
			//Nuevo
			clearEdit();
			fechaPor = CODIGO_FECHA_POR_CONCURSO;
			solicProrrogaCab = new SolicProrrogaCab();
			solicProrrogaDet = new SolicProrrogaDet();
			cargarConcursosSelectItem();
			updateGrupoPuesto();
		}
		else{
			//editar
			solicProrrogaCab = em.find(SolicProrrogaCab.class, idSolicCab);
			fechasGrupoPuesto = em.find(FechasGrupoPuesto.class, solicProrrogaCab.getFechasGrupoPuesto().getIdFechas());
			Concurso c = getConcurso(fechasGrupoPuesto);
			concurso = c.getNombre();
			if (fechasGrupoPuesto.getConcursoPuestoAgr() != null)
				grupoPuesto = fechasGrupoPuesto.getConcursoPuestoAgr().getDescripcionGrupo();
			
			estado = solicProrrogaCab.getEstado();
			fechaFin = fechasGrupoPuesto.getFechaVigProcHasta();
			
			cargarDetalles();
		}
		solicProrrogaCabHome.setInstance(solicProrrogaCab);
	}

	public void clearEdit(){
		fechaPor = null;
		fechaFin = null;
		idConcurso = null;
		idConcursoPuestoAgr = null;
		concurso = null;
		grupoPuesto = null;
	}
	
	public boolean isNew(){
		if(idSolicCab == null)
			return true;
		
		return false;
	}
	
	public String getTitulo(){
		if (isNew()){
			return "Cargar Solicitud de Modificaci\u00F3n de Fechas del Concurso/Grupo de Puestos";
		}

		return "Editar Solicitud de Modificaci\u00F3n de Fechas del Concurso/Grupo de Puestos";
	}
	
	
	public String save() {
		if (!validate()) {
			return null;
		}
		
		Date fecha = new Date();
		
		if(isNew()){
			if (CODIGO_FECHA_POR_CONCURSO.equals(fechaPor)){
				solicProrrogaCab.setTipo("PLAZO TOTAL/CONC");
			}
			else if (CODIGO_FECHA_POR_GRUPO.equals(fechaPor)){
				solicProrrogaCab.setTipo("PLAZO TOTAL/GRUP");
			}
			
			solicProrrogaCab.setEstado(ESTADO_A_SOLICITAR);
			solicProrrogaCab.setFechasGrupoPuesto(fechasGrupoPuesto);
			solicProrrogaCab.setFechaSol(fecha);
			solicProrrogaCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			solicProrrogaCab.setFechaAlta(fecha);
			solicProrrogaCab.setFechaVigHastaAnt(fechasGrupoPuesto.getFechaVigProcHasta());
			
			cargarFechaVigHastaNuev();
			
			em.persist(solicProrrogaCab);
			solicProrrogaCab.setNroSol(Integer.parseInt(solicProrrogaCab.getIdSolicCab().toString()));
			em.merge(solicProrrogaCab);
			
			
			//Guardar detalle
			solicProrrogaDet.setSolicProrrogaCab(solicProrrogaCab);
			solicProrrogaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			solicProrrogaDet.setUsuObs(usuarioLogueado.getCodigoUsuario());
			solicProrrogaDet.setFechaAlta(fecha);
			solicProrrogaDet.setFechaObs(fecha);
			solicProrrogaDet.setActivo(true);
			em.persist(solicProrrogaDet);
			solicProrrogaDet.setNro(Integer.parseInt("" + solicProrrogaDet.getIdSolicDet().toString()));
			em.merge(solicProrrogaDet);
			
			//Crear excepcion
			Concurso concurso = em.find(Concurso.class, idConcurso);
			Excepcion excepcion = new Excepcion();
			excepcion.setTipo("PRORROGA PLAZO TOTAL CONCURSO/GRUPO");
			excepcion.setConcurso(concurso);
			excepcion.setEstado(ESTADO_A_SOLICITAR);
			excepcion.setActivo(true);
			excepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			excepcion.setFechaAlta(fecha);
			excepcion.setSolicProrrogaCab(solicProrrogaCab);
			em.persist(excepcion);
			
			em.flush();
		}
		
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		return "ok";
	}
	
	
	/**
	 * Carga la lista de detalles: solicProrrogaDetList
	 */
	private void cargarDetalles() {
		solicProrrogaDetList.setActivo(new Boolean(true));
		solicProrrogaDetList.setIdSolicCab(idSolicCab);
		solicProrrogaDetList.listaResultados();
	}
	
	private void cargarFechaVigHastaNuev() {
		Integer cantDiasProrrog = getCantDiasProrrog(fechasGrupoPuesto);
		
		if(cantDiasProrrog != null){
			solicProrrogaCab.setCantDiasProrrog(cantDiasProrrog);
			Concurso concurso = getConcurso(fechasGrupoPuesto);
			Long idConfiguracionUo = concurso.getConfiguracionUoCab().getIdConfiguracionUo();
			
			int dias = 1;
			int diasMaximos = 1;
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechasGrupoPuesto.getFechaVigProcHasta());
			while(dias <= cantDiasProrrog.intValue() && diasMaximos <= 366){
				cal.add(Calendar.DATE, 1);
				if (seleccionUtilFormController.fechaTrabajaOee(cal.getTime(), idConfiguracionUo)){
					dias++;
				}
				diasMaximos++;
			}
			
			if (dias == cantDiasProrrog.intValue() + 1){
				solicProrrogaCab.setFechaVigHastaNuev(cal.getTime());
			}
		}
	}
	

	/**
	 * Calcula la Cantidad de dias prorrogados
	 * @param fechasGrupoPuesto
	 * @return
	 */
	private Integer getCantDiasProrrog(FechasGrupoPuesto fechasGrupoPuesto) {
		Concurso concurso = getConcurso(fechasGrupoPuesto);
		if (concurso != null){
			concurso = em.find(Concurso.class, concurso.getIdConcurso());
			String consulta = "" +
					" SELECT REF.* " +
					" FROM SELECCION.REFERENCIAS REF " +
					" WHERE REF.DOMINIO = 'PRORROGA_CONC' " +
					" AND REF.VALOR_ALF = '" + concurso.getDatosEspecificosTipoConc().getValorAlf().trim() + "' " +
					" AND REF.ACTIVO = TRUE";
			
			List<Referencias> lista = em.createNativeQuery(consulta, Referencias.class).getResultList();
			if (lista != null && lista.size() > 0){
				Referencias referencias = lista.get(0);
				return referencias.getValorNum();
			}
		}
		
		return null;
	}
	
	public Concurso getConcurso(FechasGrupoPuesto fechasGrupoPuesto){
		if(fechasGrupoPuesto == null)
			return null;
		
		fechasGrupoPuesto = em.find(FechasGrupoPuesto.class, fechasGrupoPuesto.getIdFechas());
		Concurso concurso = null;
		if (fechasGrupoPuesto.getConcurso() != null)
			concurso = fechasGrupoPuesto.getConcurso();
		else if (fechasGrupoPuesto.getConcursoPuestoAgr() != null){
			ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, fechasGrupoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			concurso = concursoPuestoAgr.getConcurso();
		}
		
		if (concurso != null){
			concurso = em.find(Concurso.class, concurso.getIdConcurso());
			return concurso;
		}
		
		return null;
	}

	public void cargarConcursosSelectItem(){
		List<Concurso> lista = getConcursos();
		grupoPuestoSelecItem = new ArrayList<SelectItem>();
		buildConcursoSelectItem(lista);
	}
	
	@SuppressWarnings("unchecked")
	private List<Concurso> getConcursos(){

		String consulta = "" +
				" select distinct c.* " +
				" from seleccion.concurso c " +
	
				" join seleccion.referencias r " +
				" on(c.estado = r.valor_num) " +
	
				" where c.activo = true " +
				" 	and r.activo = true " +
				" 	and r.dominio = 'ESTADOS_CONCURSO' " +
				" 	and r.desc_abrev in ('CERRADO GRUPOS')";
			
		List<Concurso> lista = em.createNativeQuery(consulta, Concurso.class).getResultList();
		if (lista == null)
			lista = new ArrayList<Concurso>();
		
		return lista;
	}
	
	private void buildConcursoSelectItem(List<Concurso> lista){
		if(concursoSelecItem == null)
			concursoSelecItem = new ArrayList<SelectItem>();
		else
			concursoSelecItem.clear();
		
		concursoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Concurso c : lista){
			concursoSelecItem.add(new SelectItem(c.getIdConcurso(), c.getNombre()));
		}
	}
	
	
	public void updateGrupoPuesto(){
		idConcursoPuestoAgr = null;
		List<ConcursoPuestoAgr> lista = getGrupoPuestoByConcurso();
		grupoPuestoSelecItem = new ArrayList<SelectItem>();
		buildGrupoPuestoSelectItem(lista);
		
		updateFechaFin();
	}
	
	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoAgr> getGrupoPuestoByConcurso(){
		if(idConcurso != null){
			String consulta = "" +
				" select distinct cp.*  " +
				" from seleccion.concurso_puesto_agr cp " +

				" join seleccion.referencias r " +
				" on(cp.estado = r.valor_num) " +

				" where cp.id_concurso = " + idConcurso + 
				" 	and cp.activo = true " +
				" 	and r.activo = true " +
				" 	and r.dominio = 'ESTADOS_GRUPO' " +
				" 	and r.desc_abrev in ('EVALUACION DOCUMENTAL', 'LISTA LARGA', 'EVALUACION REFERENCIAL', 'LISTA CORTA', 'ENTREVISTA MAI', 'EVALUACION DOCUMENTAL ADJ')";
			
			List<ConcursoPuestoAgr> lista = em.createNativeQuery(consulta, ConcursoPuestoAgr.class).getResultList();
			return lista;
		}
		return new ArrayList<ConcursoPuestoAgr>();
	}
	
	private void buildGrupoPuestoSelectItem(List<ConcursoPuestoAgr> lista){
		if(grupoPuestoSelecItem == null)
			grupoPuestoSelecItem = new ArrayList<SelectItem>();
		else
			grupoPuestoSelecItem.clear();
		
		grupoPuestoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(ConcursoPuestoAgr c : lista){
			grupoPuestoSelecItem.add(new SelectItem(c.getIdConcursoPuestoAgr(), c.getDescripcionGrupo()));
		}
	}
	
	/**
	 * Valida el formaulario de edicion
	 * 
	 * @return
	 */
	private boolean validate() {
		statusMessages.clear();
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		String mensaje = "";
		String idCompomente = "";
		if (fechaPor == null) {
			mensaje = "Solicitar modificación de fecha por es un campo requerido. Verifique.";
			idCompomente = "form:fechaPorDecorate:fechaPor";
		} 
		else if (CODIGO_FECHA_POR_CONCURSO.equals(fechaPor)){
			if (idConcurso == null){
				mensaje = "Concurso es un campo requerido. Verifique.";
				idCompomente = "form:concursoDecorate:concurso";
			}
		}
		else if (CODIGO_FECHA_POR_GRUPO.equals(fechaPor)){
			if (idConcursoPuestoAgr == null){
				mensaje = "Grupo de Puestos es un campo requerido. Verifique.";
				idCompomente = "form:grupoDecorate:grupo";
			}
		}
		
		
		if(fechaFin == null || fechasGrupoPuesto == null){
			mensaje = "Fecha Fin Actual del Proceso de Seleccion es un campo requerido. Verifique.";
			idCompomente = "form:fechaFinDecorate:fechaFin";
		}
		else if (funcionarioUtilFormController.vacio(solicProrrogaDet.getObservacion())){
			mensaje = "Obervacion es un campo requerido. Verifique.";
			idCompomente = "form:obsField:observacion";
		}

		if (!funcionarioUtilFormController.vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.add(Severity.ERROR, mensaje);
			// FACES ERROR
			message.setDetail(mensaje);
			message.setSummary(mensaje);
			context.addMessage(idCompomente, message);
			return false;
		}

		return true;
	}
	
	
	public boolean disableGroup(){
		if (fechaPor == null || CODIGO_FECHA_POR_CONCURSO.equalsIgnoreCase(fechaPor))
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void updateFechaFin(){
		fechasGrupoPuesto = null;
		fechaFin = null;
		if (fechaPor != null){
			Query q = null;
			if (CODIGO_FECHA_POR_CONCURSO.equalsIgnoreCase(fechaPor) && idConcurso != null){
				String consulta = "" +
					" select fechasGrupoPuesto from FechasGrupoPuesto fechasGrupoPuesto " +
					" where fechasGrupoPuesto.concurso.idConcurso = :idConcurso " +
					" 	and fechasGrupoPuesto.activo = :activo ";
				
				q = em.createQuery(consulta);
				q.setParameter("idConcurso", idConcurso);
				q.setParameter("activo", true);
			}
			else if (CODIGO_FECHA_POR_GRUPO.equalsIgnoreCase(fechaPor) && idConcursoPuestoAgr != null){
				String consulta = "" +
				" select fechasGrupoPuesto from FechasGrupoPuesto fechasGrupoPuesto " +
				" where fechasGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idConcursoPuestoAgr " +
				" 	and fechasGrupoPuesto.activo = :activo ";
			
				q = em.createQuery(consulta);
				q.setParameter("idConcursoPuestoAgr", idConcursoPuestoAgr);
				q.setParameter("activo", true);
			}
			
			if (q != null){
				List <FechasGrupoPuesto> lista = q.getResultList();
				if (lista != null && lista.size() > 0){
					fechasGrupoPuesto = lista.get(0);
					fechaFin = fechasGrupoPuesto.getFechaVigProcHasta();
				}
			}
		}
	}

	public void editDetalle(){
		if(idSolicDet == null){
			//nuevo
			solicProrrogaDet = new SolicProrrogaDet();
		}
		else{
			solicProrrogaDet = em.find(SolicProrrogaDet.class, idSolicDet);
		}
		idSolicDet = null;
	}
	
	public void guardarDetalle(){
		if (solicProrrogaDet != null && !funcionarioUtilFormController.vacio(solicProrrogaDet.getObservacion())){
			Date fecha = new Date();
			if(solicProrrogaDet.getIdSolicDet() == null){
				//nuevo
				solicProrrogaDet.setSolicProrrogaCab(solicProrrogaCab);
				solicProrrogaDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				solicProrrogaDet.setUsuObs(usuarioLogueado.getCodigoUsuario());
				solicProrrogaDet.setFechaAlta(fecha);
				solicProrrogaDet.setFechaObs(fecha);
				solicProrrogaDet.setActivo(true);
				em.persist(solicProrrogaDet);
				solicProrrogaDet.setNro(Integer.parseInt("" + solicProrrogaDet.getIdSolicDet().toString()));
				em.merge(solicProrrogaDet);
			}
			else{
				solicProrrogaDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				solicProrrogaDet.setFechaMod(fecha);
				em.merge(solicProrrogaDet);
			}
			em.flush();
			cargarDetalles();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		}
	}
	
	
	public String enviarSolicitud(){
		//Validar
		statusMessages.clear();
		solicProrrogaCab = em.find(SolicProrrogaCab.class, idSolicCab);
		if (ESTADO_PENDIENTE_APROBACION.equalsIgnoreCase(solicProrrogaCab.getEstado())){
			statusMessages.add(Severity.ERROR, "La solicitud ya ha sido enviada a la SFP");
			return null;
		}
		
		String consulta = ""+
			" select solicProrrogaDet from SolicProrrogaDet solicProrrogaDet " +
			" where solicProrrogaDet.activo = :activo " +
			"	and solicProrrogaDet.solicProrrogaCab.idSolicCab = :idSolicCab " +	
			"	and solicProrrogaDet.respuesta is null ";
		
		Query q = em.createQuery(consulta);
		q.setParameter("idSolicCab", idSolicCab);
		q.setParameter("activo", true);
	
		List<SolicProrrogaDet> lista = q.getResultList();
		if (lista == null || lista.size() == 0){
			statusMessages.add(Severity.ERROR, "Debe registrar una observación para enviar a la SFP");
			return null;
		}
		
		
		//enviar la solicitud
		Excepcion ex = em.find(Excepcion.class, idExcepcion);
		if (ESTADO_A_SOLICITAR.equalsIgnoreCase(solicProrrogaCab.getEstado())){
			Date fecha = new Date();
			ex.setUsuSolicitud(usuarioLogueado.getCodigoUsuario());
			ex.setFechaSolicitud(fecha);
			ex.setEstado(ESTADO_PENDIENTE_APROBACION);
			
			//Iniciar tarea jBPM
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_APROBAR_SFP);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EXCEPCION);
			roles = jbpmUtilFormController.asignarRolesTarea(); 
			Long idProcess=jbpmUtilFormController.initProcess("excepciones");
			
			ex.setIdProcessInstance(idProcess);
			em.merge(ex);
			
			solicProrrogaCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
			solicProrrogaCab.setFechaMod(fecha);
		}
		else if (ESTADO_PENDIENTE_REVISION.equalsIgnoreCase(solicProrrogaCab.getEstado())){
			jbpmUtilFormController.setActividadActual(ActividadEnum.EXCEP_REVISAR_OEE);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_APROBAR_SFP);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EXCEPCION);
			jbpmUtilFormController.setExcepcion(ex);
			
			if (!jbpmUtilFormController.nextTask()){
				statusMessages.add(Severity.ERROR, "Se produjo un error al finalizar la tarea. Consulte con el administador del sistema.");
				return null;
			}
		}
		
		solicProrrogaCab.setEstado(ESTADO_PENDIENTE_APROBACION);
		em.merge(solicProrrogaCab);

		em.flush();
		clearEdit();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "ok";
	}
	
	
	public boolean detalleEditable(){
		if (solicProrrogaCab != null && ESTADO_PENDIENTE_APROBACION.equalsIgnoreCase(solicProrrogaCab.getEstado()))
			return false;
		return true;
	}
	
	
	public boolean habilitarCronograma(){
		if (modoVista)
			return false;
		
		if (solicProrrogaCab != null && (ESTADO_PENDIENTE_APROBACION.equalsIgnoreCase(solicProrrogaCab.getEstado()) || ESTADO_A_SOLICITAR.equalsIgnoreCase(solicProrrogaCab.getEstado())))
			return true;
		
		return false;
	}
	
	
	public String getCronogramaIdEntidad(){
		if (fechasGrupoPuesto.getConcurso() != null)
			return "idConcurso";
		else if (fechasGrupoPuesto.getConcursoPuestoAgr() != null){
			return "puesto_agr";
		}
		return null;
	}
	
	public Long getCronogramaIdEntidadValor(){
		if (fechasGrupoPuesto.getConcurso() != null)
			return fechasGrupoPuesto.getConcurso().getIdConcurso();
		else if (fechasGrupoPuesto.getConcursoPuestoAgr() != null){
			return fechasGrupoPuesto.getConcursoPuestoAgr() .getIdConcursoPuestoAgr();
		}
		return null;
	}
	
	
	public String getRowClass(int currentRow)
    {
        if(selectedRow == currentRow)
        {
            return "selectedRow" ;
        }
        return "notSelectedRow" ;
    }
	
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public String getGrupoPuesto() {
		return grupoPuesto;
	}

	public void setGrupoPuesto(String grupoPuesto) {
		this.grupoPuesto = grupoPuesto;
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

	public void setListaSolicProrrogaCab(List<SolicProrrogaCab> listaSolicProrrogaCab) {
		this.listaSolicProrrogaCab = listaSolicProrrogaCab;
	}

	public List<SolicProrrogaCab> getListaSolicProrrogaCab() {
		return listaSolicProrrogaCab;
	}

	public void setIdSolicCab(Long idSolicCab) {
		this.idSolicCab = idSolicCab;
	}

	public Long getIdSolicCab() {
		return idSolicCab;
	}

	public String getFechaPor() {
		return fechaPor;
	}

	public void setFechaPor(String fechaPor) {
		this.fechaPor = fechaPor;
	}

	public void setConcursoSelecItem(List<SelectItem> concursoSelecItem) {
		this.concursoSelecItem = concursoSelecItem;
	}

	public List<SelectItem> getConcursoSelecItem() {
		return concursoSelecItem;
	}

	public void setGrupoPuestoSelecItem(List<SelectItem> grupoPuestoSelecItem) {
		this.grupoPuestoSelecItem = grupoPuestoSelecItem;
	}

	public List<SelectItem> getGrupoPuestoSelecItem() {
		return grupoPuestoSelecItem;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setSolicProrrogaCab(SolicProrrogaCab solicProrrogaCab) {
		this.solicProrrogaCab = solicProrrogaCab;
	}

	public SolicProrrogaCab getSolicProrrogaCab() {
		return solicProrrogaCab;
	}

	public void setSolicProrrogaDet(SolicProrrogaDet solicProrrogaDet) {
		this.solicProrrogaDet = solicProrrogaDet;
	}

	public SolicProrrogaDet getSolicProrrogaDet() {
		return solicProrrogaDet;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEstado() {
		return estado;
	}

	public FechasGrupoPuesto getFechasGrupoPuesto() {
		return fechasGrupoPuesto;
	}

	public void setFechasGrupoPuesto(FechasGrupoPuesto fechasGrupoPuesto) {
		this.fechasGrupoPuesto = fechasGrupoPuesto;
	}

	public void setModoVista(Boolean modoVista) {
		this.modoVista = modoVista;
	}

	public Boolean getModoVista() {
		return modoVista;
	}

	public void setIdSolicDet(Long idSolicDet) {
		this.idSolicDet = idSolicDet;
	}

	public Long getIdSolicDet() {
		return idSolicDet;
	}

	public String getESTADO_PENDIENTE_APROBACION() {
		return ESTADO_PENDIENTE_APROBACION;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public String getESTADO_A_SOLICITAR() {
		return ESTADO_A_SOLICITAR;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		if (from == null)
			return "/";
		
		return from;
	}
	
}
