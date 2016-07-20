package py.com.excelsis.sicca.seleccion.session.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.ProcesoEnum;

@Name("cancelacionAutomaticaFC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class CancelacionAutomaticaFC {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(required = false)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true, required=false)
	ReferenciasUtilFormController referenciasUtilFormController;
	
	@In(scope=ScopeType.SESSION, required=false)  
	@Out(scope=ScopeType.SESSION, required=false)  
	String roles; 
	private Boolean cancelarAutomatico = true;
	@In(required = false)
	Usuario usuarioLogueado;
	private final String USUARIO = "SYSTEM";

	public void init(){
		referenciasUtilFormController = (ReferenciasUtilFormController) Component.getInstance(ReferenciasUtilFormController.class, true);
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval) {
		
		procesoCancelar();
		
		return null;
	}

	
	private void procesoCancelar(){
		try{
			Date fecha = new Date();
			List<Excepcion> lista = getExcepcionesACancelar(fecha);

			if (lista != null && lista.size() > 0){
				if (seleccionUtilFormController == null){
					seleccionUtilFormController = (SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
				}
				
				Integer dias = getParametroDias();
				String tipo = "CANCELACION AUTOMATICA";
				String estado = "CANCELACION AUTOMATICA";
				String observacion = "Cancelación automática generada por el sistema";
				
				for (Excepcion excepcion : lista){
					//Calcular la fecha que se debe enviar el correo
					Calendar c = Calendar.getInstance();
					c.setTime(excepcion.getFechaLimite());
					c.add(Calendar.DAY_OF_MONTH, -dias);
					Date fechaCorreo = c.getTime();
					
					String pattern = "dd/MM/yyyy";
					SimpleDateFormat format = new SimpleDateFormat(pattern);
					String fechaString = format.format(fecha);
					String fechaCorreoString = format.format(fechaCorreo);
					String fechaLimiteString = format.format(excepcion.getFechaLimite());
					
					if( fechaString.equalsIgnoreCase(fechaCorreoString)){
						//Enviar correo
						List<ComisionSeleccionDet> comisionSeleccionDets = getComisionSeleccionDet(excepcion.getConcurso().getIdConcurso());
						if (comisionSeleccionDets != null){
							for (ComisionSeleccionDet comisionSeleccionDet : comisionSeleccionDets){
								Persona p = comisionSeleccionDet.getPersona();
								String asunto = excepcion.getConcurso().getNombre() + " – ALERTA: CANCELACION AUTOMÁTICA";
								String contenido = getContenidoMail(p, excepcion, fechaLimiteString);
								seleccionUtilFormController.enviarMails(p.getEMail(), contenido, asunto);
							}
						}
					}
					else if( fechaString.equalsIgnoreCase(fechaLimiteString)){
						//Cancelar
						cancelar(excepcion, tipo, estado, observacion, fecha, true, false);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private Integer getParametroDias(){
		String consulta = "" +
			" SELECT REF.* " +
			" FROM SELECCION.REFERENCIAS REF " +
			" WHERE DOMINIO = 'PLAZO' " +
			"     AND DESC_ABREV = 'CANCELACION_AUTOMATICA' " +
			"     AND ACTIVO = TRUE ";
		
		List<Referencias> lista = new ArrayList<Referencias>();
		lista = em.createNativeQuery(consulta, Referencias.class).getResultList();
		if (lista.size() > 0){
			Referencias referencias = lista.get(0);
			return referencias.getValorNum();
		}
		return null;
	}
	
	
	private List<Excepcion> getExcepcionesACancelar(Date fecha){
		
		String consulta = "" +
		" SELECT excepcion " +
		" FROM Excepcion excepcion " +
		" WHERE excepcion.tipo = 'BLOQUEO POR CONCURSO' " +
		"     AND excepcion.activo = :activo " +
		"     AND excepcion.fechaLimite >= :fecha";
	
		Query q = em.createQuery(consulta);
		q.setParameter("activo", true);
		q.setParameter("fecha", fecha);
		
		List<Excepcion> lista = q.getResultList();
		return lista;
	}
	
	
	private List<ComisionSeleccionDet> getComisionSeleccionDet(Long idConcurso){
		String consulta = "" +
		" SELECT comisionSeleccionDet " +
		" FROM ComisionSeleccionDet comisionSeleccionDet " +
		" JOIN comisionSeleccionDet.comisionSeleccionCab comisionSeleccionCab " +
		" JOIN comisionSeleccionCab.concurso concurso " +
		" WHERE concurso.idConcurso = :idConcurso " +
		"     AND comisionSeleccionDet.activo = :activo " +
		"     AND comisionSeleccionDet.tipo = :tipo ";
	
		Query q = em.createQuery(consulta);
		q.setParameter("idConcurso", idConcurso);
		q.setParameter("activo", true);
		q.setParameter("tipo", "CONCURSO");

		List<ComisionSeleccionDet> lista = q.getResultList();
		return lista;
	}
	
	
	private String getContenidoMail(Persona p, Excepcion excepcion, String fechaLimite) {
		String texto =
				"<p align=\"justify\"> <b> Estimado(a) " + p.getNombreCompleto() + " </b>"
				+ "<p></p> "
				+ " Le comunicamos que el Concurso " + excepcion.getConcurso().getNombre() 
				+ " se encuentra actualmente bloqueado y la fecha l&iacute;mite de bloqueo es el " + fechaLimite + ". "
				+ "<p></p> "
				+ "Si para ello, el Concurso sigue en ese estado, el sistema Cancelar&aacute; Autom&aacute;ticamente una vez cumplido el plazo."
				+ "<p></p> "
				+ "<p></p> "
				+ "<p> Atentamente,</p> "
				+ "<b><p>Secretaria de la Funci&oacute;n Pública</p></b>";
		
		return texto;
	}
	
	
	/**
	 * Cancela el concurso para una excepcion
	 * @param excepcion
	 * @param tipo
	 * @param estado
	 * @param observacion
	 * @param fecha
	 * @param iniciarTareajBPM
	 * @param inactivarConcurso
	 */
	public Excepcion cancelar(Excepcion excepcion, String tipo, String estado, String observacion, Date fecha, boolean iniciarTareajBPM, boolean inactivarConcurso) {
		Excepcion ex = new Excepcion();
		ex.setTipo(tipo);
		Concurso concurso = em.find(Concurso.class, excepcion.getConcurso().getIdConcurso());
		ex.setConcurso(concurso);
		ex.setEstado("REGISTRADO");
		ex.setObservacion(observacion);
		ex.setActivo(true);
		ex.setFechaAlta(fecha);
		if(!cancelarAutomatico){
			ex.setIdConfiguracionUoBloq(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		}
		ex.setUsuAlta(USUARIO);
		ex.setIdEstadoConc(concurso.getEstado());
		em.persist(ex);
		if(seleccionUtilFormController == null){
			seleccionUtilFormController = (SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
		}
		
		EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet("CONCURSO", estado);
		EstadoCab estadoCabPlanta = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		EstadoDet estadoDetJudicializacion = seleccionUtilFormController.buscarEstadoDet("CONCURSO", "JUDICIALIZACION");
		EstadoDet estadoDetFaltaDisponibilidad = seleccionUtilFormController.buscarEstadoDet("CONCURSO", "FALTA DE DISPONIBILIDAD PRESUPUESTARIA");
		EstadoDet estadoDetIntervencionConcurso = seleccionUtilFormController.buscarEstadoDet("CONCURSO", "INTERVENCION CONCURSO POR SFP");
		
		//Guardar ExcepcionGrPuesto
		List<ConcursoPuestoAgr> listaGrupos = concurso.getConcursoPuestoAgrs();
		if(referenciasUtilFormController == null){
			referenciasUtilFormController = (ReferenciasUtilFormController) Component.getInstance(ReferenciasUtilFormController.class,true);
		}
		for(ConcursoPuestoAgr concursoPuestoAgr : listaGrupos){
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, concursoPuestoAgr.getIdConcursoPuestoAgr());
			guardarExcepcionGrPuesto(ex, concursoPuestoAgr, fecha);
			
			//Cambiar estado del grupo
			Referencias ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_GRUPO", "GRUPO CANCELADO");
			cancelarConcursoPuestoAgr(concursoPuestoAgr, ref, fecha);
			
			//cancelar ConcursoPuestoDet's
			List<ConcursoPuestoDet> listaPuestos = concursoPuestoAgr.getConcursoPuestoDets();
			if (listaPuestos != null){
				for(ConcursoPuestoDet puesto : listaPuestos){
					puesto = em.find(ConcursoPuestoDet.class, puesto.getId());
					cancelarConcursoPuestoDet(puesto, estadoDet, fecha);
					
					//Actualizar Planta e Historico
					if (puesto.isActivo())
						cancelarPlantaCargoDetHistorico(puesto, estadoCabPlanta, fecha);
					else{
						if (puesto.getEstadoDet().getIdEstadoDet().longValue() == estadoDetJudicializacion.getIdEstadoDet().longValue() ||
								puesto.getEstadoDet().getIdEstadoDet().longValue() == estadoDetFaltaDisponibilidad.getIdEstadoDet().longValue() ||
								puesto.getEstadoDet().getIdEstadoDet().longValue() == estadoDetIntervencionConcurso.getIdEstadoDet().longValue()){
							actualizarPlantaCargoDet(puesto.getPlantaCargoDet(), true, fecha);
						}
					}
				}
			}
		}	
			
		//cancelar el Concurso
		Referencias ref = referenciasUtilFormController.getReferenciaDescLarga("ESTADOS_CONCURSO", estado);
		cancelarConcurso(concurso, ref, fecha, inactivarConcurso);
		
		if (iniciarTareajBPM){
			Long idProcessInstance = iniciarTareajBPM(excepcion);
			ex.setIdProcessInstance(idProcessInstance);
			em.merge(ex);
		}
		
		em.flush();
		return ex;
	}
	

	private void guardarExcepcionGrPuesto(Excepcion excepcion, ConcursoPuestoAgr concursoPuestoAgr, Date fecha){
		ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
		excepcionGrPuesto.setConcursoPuestoAgr(concursoPuestoAgr);
		excepcionGrPuesto.setExcepcion(excepcion);
		excepcionGrPuesto.setIdEstadoGr(concursoPuestoAgr.getEstado().longValue());
		excepcionGrPuesto.setActivo(true);
		excepcionGrPuesto.setFechaAlta(fecha);
		excepcionGrPuesto.setUsuAlta(USUARIO);
		em.persist(excepcionGrPuesto);
	}
	
	
	private void cancelarConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr, Referencias ref, Date fecha){
		concursoPuestoAgr.setEstado(ref.getValorNum());
		concursoPuestoAgr.setActivo(false);
		concursoPuestoAgr.setFechaMod(fecha);
		concursoPuestoAgr.setUsuMod(USUARIO);
		em.merge(concursoPuestoAgr);
	}
	
	
	private void cancelarConcurso(Concurso concurso, Referencias ref, Date fecha, boolean inactivarConcurso){
		concurso.setEstado(ref.getValorNum());
		concurso.setFechaMod(fecha);
		concurso.setUsuMod(USUARIO);
		
		if (inactivarConcurso)
			concurso.setActivo(false);
		
		em.merge(concurso);
	}
	
	
	private void cancelarConcursoPuestoDet(ConcursoPuestoDet concursoPuestoDet, EstadoDet estadoDet, Date fecha){
		concursoPuestoDet.setEstadoDet(estadoDet);
		concursoPuestoDet.setActivo(false);
		concursoPuestoDet.setFechaMod(fecha);
		concursoPuestoDet.setUsuMod(USUARIO);
		em.merge(concursoPuestoDet);
	}
	
	
	private void cancelarPlantaCargoDetHistorico(ConcursoPuestoDet concursoPuestoDet, EstadoCab estadoCab, Date fecha){
		// Registrar Planta
		PlantaCargoDet plantaCargoDet = em.find(PlantaCargoDet.class, concursoPuestoDet.getPlantaCargoDet().getId());
		plantaCargoDet.setEstadoCab(estadoCab);
		plantaCargoDet.setFechaMod(fecha);
		plantaCargoDet.setUsuMod(USUARIO);
		em.merge(plantaCargoDet);
			
		//Registrar historico
		HistoricosEstado historicosEstado = new HistoricosEstado();
		historicosEstado.setPlantaCargoDet(plantaCargoDet);
		historicosEstado.setEstadoCab(estadoCab);
		historicosEstado.setFechaMod(fecha);
		historicosEstado.setUsuMod(USUARIO);
		em.persist(historicosEstado);
	}
	
	
	private void actualizarPlantaCargoDet(PlantaCargoDet plantaCargoDet, boolean activo, Date fecha) {
		plantaCargoDet.setActivo(activo);
		plantaCargoDet.setFechaMod(fecha);
		plantaCargoDet.setUsuMod(USUARIO);
		em.merge(plantaCargoDet);
	}
	
	
	/**
	 * Inicia la tarea del jBPM para la excepcion
	 */
	private Long iniciarTareajBPM(Excepcion excepcion) {
		jbpmUtilFormController.setActividadSiguiente(ActividadEnum.EXCEP_ADJUNTAR_DOC_CANCELACION_AUTOMATICA);
		jbpmUtilFormController.setProcesoEnum(ProcesoEnum.EXCEPCION);
		jbpmUtilFormController.setTransition("next2");
		roles = jbpmUtilFormController.asignarRolesTarea(); 
		Long idProcess=jbpmUtilFormController.initProcess("excepciones");
		return idProcess;
	}

	public Boolean getCancelarAutomatico() {
		return cancelarAutomatico;
	}

	public void setCancelarAutomatico(Boolean cancelarAutomatico) {
		this.cancelarAutomatico = cancelarAutomatico;
	}
	

}
