package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("bloqueoConcursoGrupoPuestoFC")
public class BloqueoConcursoGrupoPuestoFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1661926467489160485L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	
	@In(scope=ScopeType.SESSION, required=false)  
	@Out(scope=ScopeType.SESSION, required=false)  
	String roles; 
	
	
	private String entity = "excepcion";
	private String idEntity = "";
	private String nombrePantalla = "";
	private String ubicacionFisica = "";

	private String tipoBloqueo;
	private final String TIPO_BLOQUEO_POR_CONCURSO = "C";
	private final String TIPO_BLOQUEO_POR_GRUPO = "G";
	private final String TIPO_BLOQUEO_POR_PUESTO = "P";
	
	private String motivo;
	private Date fecha = new Date();
	private Integer tBloqueo;
	private Long idExcepcion;
	
	private String from;
	
	public void init() {
		cargar();
	}

	
	public void initView() {
		try{
			Excepcion excepcion = em.find(Excepcion.class, idExcepcion);
			grupoPuestosController.setIdConcurso(excepcion.getConcurso().getIdConcurso());
			grupoPuestosController.setConcurso(em.find(Concurso.class, grupoPuestosController.getIdConcurso()));
			cargar();
			motivo = excepcion.getObservacion();
			fecha = excepcion.getFechaAlta();
			
			Referencias referencias = referenciasUtilFormController.getReferenciaPorValorNum("TIPOS_BLOQUEO", excepcion.getTipoBloqueo().intValue());
			tBloqueo = referencias.getIdReferencias().intValue();
	
			if (esBloqueoPorGrupo()){
				grupoPuestosController.setListaFiltradaConcursoPuestoAgr(getListaGruposView());
			}
			else if (esBloqueoPorPuesto()){
				grupoPuestosController.setIdConcursoPuestoAgr(excepcion.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				grupoPuestosController.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class, grupoPuestosController.getIdConcursoPuestoAgr()));
				
				grupoPuestosController.setListaFiltradaConcursoPuestoDet(getListaPuestosView());
			}
			
			from = "seleccion/bloqueo/BloqueoConcursoGrupoPuestoView.seam?tipoBloqueo=" + tipoBloqueo + "&idExcepcion=" + idExcepcion;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cargar(){

		if (esBloqueoPorConcurso())
			nombrePantalla = "BloqueoConcurso";
		else if (esBloqueoPorGrupo())
			nombrePantalla = "BloqueoGrupoPuesto";
		else if (esBloqueoPorPuesto())
			nombrePantalla = "BloqueoPuesto";
		
		cargarDireccionAnexo();
	}
	
	public void cargarDireccionAnexo(){
		if (grupoPuestosController.getIdConcurso() != null){
			Calendar c = Calendar.getInstance();
			Concurso concurso = em.find(Concurso.class, grupoPuestosController.getIdConcurso());
			ubicacionFisica = "\\SICCA\\" + c.get(Calendar.YEAR) + "\\OEE\\" + concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "\\" +
							concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "\\" + 
							concurso.getIdConcurso();
		}
	}


	public List<SelectItem>  getTipoBloqueoSelectItem(){
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		String sql = "select r.* " +
		"from seleccion.referencias r " +
		"where dominio = 'TIPOS_BLOQUEO' " +
		"and activo is true";
		
		List<Referencias> l = em.createNativeQuery(sql, Referencias.class).getResultList();
		
		if(l != null){
			for(Referencias o : l){
				lista.add(new SelectItem(o.getIdReferencias(), o.getDescLarga()));
			}
		}
		
		return lista;
	}
	
	
	public String save(){
		
		if (!validate())
			return null;
		
		Excepcion excepcion = new Excepcion();
		Concurso concurso = em.find(Concurso.class, grupoPuestosController.getIdConcurso());
		excepcion.setConcurso(concurso);
		excepcion.setEstado("REGISTRADO");
		excepcion.setObservacion(motivo);
		excepcion.setActivo(true);
		excepcion.setFechaAlta(fecha);
		excepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		Referencias referencias = em.find(Referencias.class, tBloqueo.longValue());
		excepcion.setTipoBloqueo(referencias.getValorNum().longValue());
		excepcion.setIdEstadoConc(concurso.getEstado());
		
		if (esBloqueoPorConcurso()){
			excepcion.setTipo("BLOQUEO POR CONCURSO");
			
			Referencias ref = referenciasUtilFormController.getReferencia("PLAZO_BLOQUEO", "CONCURSO");
			Integer meses = ref.getValorNum();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, meses);
			excepcion.setFechaLimite(c.getTime());
			em.persist(excepcion);
			
			//Bloquear el Concurso.
			ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_CONCURSO", referencias.getDescLarga());
			bloquearConcurso(concurso, ref);
			
			EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet("CONCURSO", referencias.getDescLarga());
			
			//Bloquear ConcursoPuestoAgr's
			grupoPuestosController.setListaFiltradaConcursoPuestoAgr(concurso.getConcursoPuestoAgrs());
			for(ConcursoPuestoAgr concursoPuestoAgr : grupoPuestosController.getListaFiltradaConcursoPuestoAgr()){
				//Guardar ExcepcionGrPuesto
				concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, concursoPuestoAgr.getIdConcursoPuestoAgr());
				
				if ( concursoPuestoAgr.getConcursoPuestoDets().size() > 0){
					ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets().get(0);
					guardarExcepcionGrPuesto(excepcion, concursoPuestoAgr, concursoPuestoDet, false);
					
					//Bloquear grupo
					ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_GRUPO", referencias.getDescLarga());
					bloquearConcursoPuestoAgr(concursoPuestoAgr, ref);
					
					//Bloquear ConcursoPuestoDet's
					grupoPuestosController.setListaFiltradaConcursoPuestoDet(concursoPuestoAgr.getConcursoPuestoDets());
					for(ConcursoPuestoDet puesto : grupoPuestosController.getListaFiltradaConcursoPuestoDet()){
						puesto = em.find(ConcursoPuestoDet.class, puesto.getId());
						bloquearConcursoPuestoDet(puesto, estadoDet);
						
						//Actualizar Planta e Historico
						bloquearPlantaCargoDetHistorico(puesto, estadoDet);
					}
				}
			}
			
		} 
		else if (esBloqueoPorGrupo()){
			excepcion.setTipo("BLOQUEO POR GRUPO DE PUESTOS");
			
			Referencias ref = referenciasUtilFormController.getReferencia("PLAZO_BLOQUEO", "GRUPO_PUESTO");
			Integer meses = ref.getValorNum();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, meses);
			excepcion.setFechaLimite(c.getTime());
			em.persist(excepcion);
			
			EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet("CONCURSO", referencias.getDescLarga());
			
			boolean todosSeleccionados = true;
			for(ConcursoPuestoAgr concursoPuestoAgr : grupoPuestosController.getListaFiltradaConcursoPuestoAgr()){
				if (concursoPuestoAgr.getSeleccionado() == null || !concursoPuestoAgr.getSeleccionado())
					 todosSeleccionados = false;
				else{
					//Guardar ExcepcionGrPuesto
					concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, concursoPuestoAgr.getIdConcursoPuestoAgr());
					
					//Bloquear grupo
					ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_GRUPO", referencias.getDescLarga());
					bloquearConcursoPuestoAgr(concursoPuestoAgr, ref);
					
					if ( concursoPuestoAgr.getConcursoPuestoDets().size() > 0){
						ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets().get(0);
						guardarExcepcionGrPuesto(excepcion, concursoPuestoAgr, concursoPuestoDet, false);
						
						//Bloquear ConcursoPuestoDet's
						grupoPuestosController.setListaFiltradaConcursoPuestoDet(concursoPuestoAgr.getConcursoPuestoDets());
						for(ConcursoPuestoDet puesto : grupoPuestosController.getListaFiltradaConcursoPuestoDet()){
							
							guardarExcepcionGrPuesto(excepcion, concursoPuestoAgr, puesto, true);
							
							puesto = em.find(ConcursoPuestoDet.class, puesto.getId());
							bloquearConcursoPuestoDet(puesto, estadoDet);
							
							//Actualizar Planta e Historico
							bloquearPlantaCargoDetHistorico(puesto, estadoDet);
						}
					}
				}
			}
			
			if (todosSeleccionados){
				//Si todos los Grupos se encuentran seleccionados en la grilla, el Concurso deberá bloquearse.
				ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_CONCURSO", referencias.getDescLarga());
				bloquearConcurso(concurso, ref);
			}
		}
		else if (esBloqueoPorPuesto()){
			excepcion.setTipo("BLOQUEO POR PUESTOS");
			ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, grupoPuestosController.getIdConcursoPuestoAgr());
			excepcion.setConcursoPuestoAgr(concursoPuestoAgr);
			
			Referencias ref = referenciasUtilFormController.getReferencia("PLAZO_BLOQUEO", "PUESTO");
			Integer meses = ref.getValorNum();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, meses);
			excepcion.setFechaLimite(c.getTime());
			em.persist(excepcion);
			
			referencias = em.find(Referencias.class, tBloqueo.longValue());
			EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet("CONCURSO", referencias.getDescLarga());
			
			boolean todosSeleccionados = true;
			for(ConcursoPuestoDet concursoPuestoDet : grupoPuestosController.getListaFiltradaConcursoPuestoDet()){
				if (concursoPuestoDet.getSeleccionado() == null || !concursoPuestoDet.getSeleccionado())
					 todosSeleccionados = false;
				else{
					guardarExcepcionGrPuesto(excepcion, concursoPuestoAgr, concursoPuestoDet, true);
					
					//Actualizar Planta e Historico
					bloquearPlantaCargoDetHistorico(concursoPuestoDet, estadoDet);
					
					//Cambiar estado de ConcursoPuestoDet
					bloquearConcursoPuestoDet(concursoPuestoDet, estadoDet);
				}
			}
			
			if (todosSeleccionados){
				//Si todos los Puestos se encuentran seleccionados en la grilla, el Grupo de puestos deberá bloquearse.
				ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_GRUPO", referencias.getDescLarga());
				bloquearConcursoPuestoAgr(concursoPuestoAgr, ref);
			}
		}
		

		em.flush();
		
		idExcepcion = excepcion.getIdExcepcion();
		cargar();
		from = "seleccion/bloqueo/BloqueoConcursoGrupoPuesto.seam?tipoBloqueo=" + tipoBloqueo + "&idExcepcion=" + idExcepcion;
		
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			
		return null;
	}
	
	
	/**
	 * Valida el formaulario de carga
	 * 
	 * @return
	 */
	private boolean validate() {
		statusMessages.clear();
		String mensaje = "";

		if (grupoPuestosController.getIdConcurso() == null) {
			mensaje = "Concurso es un campo requerido. Verifique.";
		} 
		
		else if (esBloqueoPorGrupo()){
			if (!grupoPuestosController.hayGrupoSeleccionado()) {
				mensaje = "Debe seleccionar por lo menos un Grupo antes de guardar.";
			} 
		}
		else if (esBloqueoPorPuesto()){
			if (!grupoPuestosController.hayPuestoSeleccionado()) {
				mensaje = "Debe seleccionar por lo menos un Puesto antes de guardar.";
			} 
		}
		
		if (funcionarioUtilFormController.vacio(motivo)) {
			mensaje = "Motivo es un campo requerido. Verifique.";
		} 
		else if (tBloqueo == null) {
			mensaje = "Tipo de Bloqueo es un campo requerido. Verifique.";
		} 
		
		
		if (!funcionarioUtilFormController.vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.add(Severity.ERROR, mensaje);
			// FACES ERROR
			return false;
		}

		return true;
	}
	
	
	private void guardarExcepcionGrPuesto(Excepcion excepcion, ConcursoPuestoAgr concursoPuestoAgr, ConcursoPuestoDet concursoPuestoDet, boolean guardarPuesto){
		ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
		excepcionGrPuesto.setConcursoPuestoAgr(concursoPuestoAgr);
		excepcionGrPuesto.setExcepcion(excepcion);
		excepcionGrPuesto.setEstadoDet(concursoPuestoDet.getEstadoDet());
		excepcionGrPuesto.setIdEstadoGr(concursoPuestoAgr.getEstado().longValue());
		excepcionGrPuesto.setActivo(true);
		excepcionGrPuesto.setFechaAlta(fecha);
		excepcionGrPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		
		if (guardarPuesto)
			excepcionGrPuesto.setPlantaCargoDet(concursoPuestoDet.getPlantaCargoDet());
			
		em.persist(excepcionGrPuesto);
	}

	
	private void bloquearConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr, Referencias ref){
		concursoPuestoAgr.setEstado(ref.getValorNum());
		concursoPuestoAgr.setActivo(false);
		concursoPuestoAgr.setFechaMod(fecha);
		concursoPuestoAgr.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(concursoPuestoAgr);
	}

	
	private void bloquearConcursoPuestoDet(ConcursoPuestoDet concursoPuestoDet, EstadoDet estadoDet){
		concursoPuestoDet.setEstadoDet(estadoDet);
		concursoPuestoDet.setActivo(false);
		concursoPuestoDet.setFechaMod(fecha);
		concursoPuestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(concursoPuestoDet);
	}
	
	
	private void bloquearPlantaCargoDetHistorico(ConcursoPuestoDet concursoPuestoDet, EstadoDet estadoDet){
		// Registrar Planta
		PlantaCargoDet plantaCargoDet = em.find(PlantaCargoDet.class, concursoPuestoDet.getPlantaCargoDet().getId());
		if (plantaCargoDet.getActivo() != null && plantaCargoDet.getActivo()){
			EstadoCab estadoCab = seleccionUtilFormController.buscarEstadoCab("CONCURSO");
			plantaCargoDet.setEstadoCab(estadoCab);
			plantaCargoDet.setEstadoDet(estadoDet);
			plantaCargoDet.setActivo(false);
			plantaCargoDet.setFechaMod(fecha);
			plantaCargoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(plantaCargoDet);
			
			//Registrar historico
			HistoricosEstado historicosEstado = new HistoricosEstado();
			historicosEstado.setPlantaCargoDet(plantaCargoDet);
			historicosEstado.setEstadoCab(estadoCab);
			historicosEstado.setFechaMod(fecha);
			historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(historicosEstado);
		}
	}
	
	
	private void bloquearConcurso(Concurso concurso, Referencias ref){
		concurso.setEstado(ref.getValorNum());
		concurso.setActivo(false);
		concurso.setFechaMod(fecha);
		concurso.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(concurso);
	}
	
	
	public boolean esBloqueoPorConcurso(){
		if (TIPO_BLOQUEO_POR_CONCURSO.equalsIgnoreCase(tipoBloqueo))
			return true;
		
		return false;
	}
	
	public boolean esBloqueoPorGrupo(){
		if (TIPO_BLOQUEO_POR_GRUPO.equalsIgnoreCase(tipoBloqueo))
			return true;
		
		return false;
	}
	
	public boolean esBloqueoPorPuesto(){
		if (TIPO_BLOQUEO_POR_PUESTO.equalsIgnoreCase(tipoBloqueo))
			return true;
		
		return false;
	}
	
	
	public void publicar(){
		Excepcion excepcion = em.find(Excepcion.class, idExcepcion);
		excepcion.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
		excepcion.setFechaPublicacion(new Date());
		em.merge(excepcion);
		em.flush();
		
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}
	
	
	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoAgr> getListaGruposView(){
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		if(idExcepcion != null){
			String consulta = "" +
				" select concursoPuestoAgr from ExcepcionGrPuesto excepcionGrPuesto " +
				" join excepcionGrPuesto.concursoPuestoAgr concursoPuestoAgr " +
				" join excepcionGrPuesto.excepcion excepcion " + 
				" where excepcion.idExcepcion = :idExcepcion " +
				" 	and excepcionGrPuesto.plantaCargoDet is null ";
			
			Query q = em.createQuery(consulta);
			q.setParameter("idExcepcion", idExcepcion);
			
			lista = q.getResultList();
		}
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> getListaPuestosView(){
		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		if(idExcepcion != null){
			String consulta = "" +
				" select concursoPuestoDet from ExcepcionGrPuesto excepcionGrPuesto " +
				" join excepcionGrPuesto.concursoPuestoAgr concursoPuestoAgr " +
				" join concursoPuestoAgr.concursoPuestoDets concursoPuestoDet " +
				" join concursoPuestoDet.plantaCargoDet plantaCargoDet " +
				" join excepcionGrPuesto.plantaCargoDet plantaCargoDet2 " +
				" join excepcionGrPuesto.excepcion excepcion " + 
				" where plantaCargoDet = plantaCargoDet2 " +
				"	and excepcion.idExcepcion = :idExcepcion ";
			
			Query q = em.createQuery(consulta);
			q.setParameter("idExcepcion", idExcepcion);
			
			lista = q.getResultList();
		}
		return lista;
	}
	
	
	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}


	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}


	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}


	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}


	public void setTipoBloqueo(String tipoBloqueo) {
		this.tipoBloqueo = tipoBloqueo;
	}


	public String getTipoBloqueo() {
		return tipoBloqueo;
	}


	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	public String getMotivo() {
		return motivo;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}


	public Date getFecha() {
		return fecha;
	}


	public void settBloqueo(Integer tBloqueo) {
		this.tBloqueo = tBloqueo;
	}


	public Integer gettBloqueo() {
		return tBloqueo;
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
		return from;
	}

	
}
