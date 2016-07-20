package py.com.excelsis.sicca.evaluacion.session.form;

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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.core.SeamResourceBundle;

import py.com.excelsis.sicca.entity.AlertasEval;
import py.com.excelsis.sicca.entity.AlertasEvalDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Sujetos;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.FuncionariosDTO;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("procesoEvaluacionDesempenhoFC566")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class ProcesoEvaluacionDesempenhoFC566 {
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	public static String SYSTEM_USR = "SYSTEM";
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private List<EmpleadoPuesto> listaEmpleados;
	private List<AlertasEval> listaEvals;
	private List<FuncionariosDTO> listaDTO;

	private Integer estadoFinalizado;

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when,
			@IntervalCron String interval) {
		
		init();
		obtenerListaFuncionarios();
		enviarMail();
		
		return null;
	}

	private void init() {

		Query q = em
				.createQuery("select r from Referencias r"
						+ " where  r.activo is true and r.dominio = 'ESTADOS_EVALUACION_DESEMPENO' "
						+ "and r.descAbrev = 'FINALIZADA'");
		Referencias ref = (Referencias) q.getSingleResult();
		estadoFinalizado = ref.getValorNum();
		listaEmpleados = new ArrayList<EmpleadoPuesto>();
		listaEvals = new ArrayList<AlertasEval>();
		List<EmpleadoPuesto> lista = obtenerEmpleados();
		Calendar calActual = Calendar.getInstance();
		//Date fechaActual = new Date();
		//calActual.setTime(fechaActual);
		for (EmpleadoPuesto e : lista) {
			Calendar calPuesto = Calendar.getInstance();
			
			calPuesto.setTime(e.getFechaInicio());
			if (cumpleFecha(calActual, calPuesto, e)) {
				if (formaParteEvaluacion(calActual, e.getIdEmpleadoPuesto()))
					listaEmpleados.add(e);
			}
		}
	}

	private Boolean cumpleFecha(Calendar calActual, Calendar calPuesto,
			EmpleadoPuesto puesto) {
		long milisActul = calActual.getTimeInMillis();
		long milisPuesto = calPuesto.getTimeInMillis();
		// calcular la diferencia en milisengundos
		long diff = milisActul - milisPuesto;
		// calcular la diferencia en meses
		long val1 = 30 * 24;
		long val2 = 3600 * 1000;
		long val = (val1 * val2);
		float diffMes = diff / val;
		
		//long diffMes = diff / (30 * 24 * 60 * 60 * 1000);

		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class, puesto
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getConfiguracionUoCab().getIdConfiguracionUo());

		AlertasEval alerta = obtenerAlertaPorOee(oee.getIdConfiguracionUo());
		if (alerta == null)
			return false;
		if (diffMes >= alerta.getPeriodo().longValue())
			return true;
		return false;
	}

	private Boolean formaParteEvaluacion(Calendar calActual, Long id) {
		long milisActul = calActual.getTimeInMillis();

		Query q = em
				.createQuery("select s from Sujetos s"
						+ " where  s.activo is true and s.empleadoPuesto.idEmpleadoPuesto = :id");
		q.setParameter("id", id);
		List<Sujetos> lista = q.getResultList();
		Boolean resultado = true;
		for (Sujetos s : lista) {
			if (s.getEvaluacionDesempeno().getEstado().intValue() == estadoFinalizado
					.intValue()
					&& s.getEvaluacionDesempeno().getFechaEvalDesde() != null) {
				Calendar calEvaluacion = Calendar.getInstance();
				calEvaluacion.setTime(s.getEvaluacionDesempeno()
						.getFechaEvalDesde());
				long milisEvaluacion = calEvaluacion.getTimeInMillis();
				resultado = validoEvaluacion(milisActul, milisEvaluacion,
						s.getEvaluacionDesempeno());
			} else
				return false;
		}
		return resultado;

	}

	private AlertasEval obtenerAlertaPorOee(Long id) {
		Query q = em
				.createQuery("select a from AlertasEval a"
						+ " where  a.configuracionUoCab.idConfiguracionUo = :id  and a.activarAlerta is true and a.activo is true");
		q.setParameter("id", id);
		List<AlertasEval> alerts = q.getResultList();
		if (alerts.isEmpty())
			return null;
		else {
			if (!formaParteDeAlertas(alerts.get(0)))
				listaEvals.add(alerts.get(0));
			return alerts.get(0);
		}

	}

	private Boolean validoEvaluacion(long milisActul, long milisEvaluacion,
			EvaluacionDesempeno evaluacion) {
		// calcular la diferencia en milisengundos
		long diff = milisActul - milisEvaluacion;
		// calcular la diferencia en dias
		long diffMes = diff / (30 * 24 * 60 * 60 * 1000);
		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class, evaluacion
				.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		AlertasEval alerta = obtenerAlertaPorOee(oee.getIdConfiguracionUo());
		if (diff > alerta.getPeriodo().longValue())

			return true;

		return false;
	}

	private Boolean formaParteDeAlertas(AlertasEval a) {
		for (AlertasEval le : listaEvals) {
			if (le.getIdAlertasEval().intValue() == a.getIdAlertasEval()
					.intValue())
				return true;
		}
		return false;
	}

	private List<EmpleadoPuesto> obtenerEmpleados() {
		Query q = em.createQuery("select e from EmpleadoPuesto e"
				+ " where  e.actual is true ");
		return q.getResultList();
	}

	private void obtenerListaFuncionarios() {
		listaDTO = new ArrayList<FuncionariosDTO>();
		for (EmpleadoPuesto e : listaEmpleados) {
			ConfiguracionUoDet uo = em.find(ConfiguracionUoDet.class, e
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getIdConfiguracionUoDet());
			if (!formaParteFuncionarios(uo)) {
				FuncionariosDTO dto = new FuncionariosDTO();
				dto.setConfiguracionUoCab(uo.getConfiguracionUoCab());
				dto.setConfiguracionUoDet(uo);
				dto.setFuncionarios(obtenerFuncionarios(uo));
				listaDTO.add(dto);
			}
		}
	}

	private String obtenerFuncionarios(ConfiguracionUoDet det) {
		String resultado = "";
		for (EmpleadoPuesto e : listaEmpleados) {
			if (e.getPlantaCargoDet().getConfiguracionUoDet()
					.getIdConfiguracionUoDet().intValue() == det
					.getIdConfiguracionUoDet()) {
				resultado += e.getPersona().getNombres() + " "
						+ e.getPersona().getApellidos() + " - "
						+ e.getPersona().getDocumentoIdentidad() + ", ";
			}

		}
		return resultado;
	}

	private Boolean formaParteFuncionarios(ConfiguracionUoDet det) {
		for (FuncionariosDTO fdto : listaDTO) {
			if (fdto.getConfiguracionUoDet().getIdConfiguracionUoDet()
					.intValue() == det.getIdConfiguracionUoDet().intValue())
				return true;
		}
		return false;
	}

	private List<AlertasEvalDet> obtenerDetalleAlertas(AlertasEval alerta) {
		Query q = em
				.createQuery("select det from AlertasEvalDet det"
						+ " where  det.alertasEval.idAlertasEval = :id  and det.activo is true");
		q.setParameter("id", alerta.getIdAlertasEval());
		return q.getResultList();
	}

	private void enviarMail() {
		if(listaDTO.isEmpty())
			return;
		usuarioPortalFormController =
			(UsuarioPortalFormController) Component.getInstance(UsuarioPortalFormController.class, true);
		String delimiter = ";";
		for (AlertasEval a : listaEvals) {
			List<AlertasEvalDet> listaAlertasEvalDet = new ArrayList<AlertasEvalDet>();
			listaAlertasEvalDet = obtenerDetalleAlertas(a);
			for (AlertasEvalDet detalle : listaAlertasEvalDet) {
				String[] dirEmails;
				String destinatario = null;
				if (detalle.getPersona() != null)
					destinatario = detalle.getPersona().getNombres() + " "
							+ detalle.getPersona().getApellidos();
				else
					destinatario = detalle.getDescripcion();
				dirEmails = detalle.getEMail().split(delimiter);
				if (dirEmails.length == 0)
					dirEmails[0] = detalle.getEMail();

				String asunto = "Portal Paraguay Concursa – "
						+ SeamResourceBundle.getBundle().getString(
								"CU566_asunto")
						+ ": Funcionarios pendientes por Evaluar ";
				String texto = "";
				try {
					texto = texto
							+ "<p align=\"justify\"> <b> Estimado(a): "
							+ destinatario
							+ "<p align=\"justify\"> "
							+ SeamResourceBundle.getBundle().getString(
									"CU566_texto")
							+ ":"
							+ "</p>"
							+ obtenerParteTexto(detalle.getAlertasEval()
									.getConfiguracionUoCab())
							+ "<p></p>"
							+ "<p> Atentamente,</p> "
							+ "<b> "
							+ SeamResourceBundle.getBundle().getString(
									"CU566_firma") + "</p></b>";
					for (int i = 0; i < dirEmails.length; i++) {
						usuarioPortalFormController.enviarMails(dirEmails[i],
								texto, asunto, null);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	private String obtenerParteTexto(ConfiguracionUoCab cab) {
		String resultado = "";
		for (FuncionariosDTO dto : listaDTO) {
			if (dto.getConfiguracionUoCab().getIdConfiguracionUo().intValue() == cab
					.getIdConfiguracionUo()) {
				resultado += dto.getConfiguracionUoDet().getDenominacionUnidad()
						+ ": " + dto.getFuncionarios() + "<br></br>";
			}
		}
		return resultado;
	}

	public List<EmpleadoPuesto> getListaEmpleados() {
		return listaEmpleados;
	}

	public void setListaEmpleados(List<EmpleadoPuesto> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}

	public List<AlertasEval> getListaEvals() {
		return listaEvals;
	}

	public void setListaEvals(List<AlertasEval> listaEvals) {
		this.listaEvals = listaEvals;
	}

	public List<FuncionariosDTO> getListaDTO() {
		return listaDTO;
	}

	public void setListaDTO(List<FuncionariosDTO> listaDTO) {
		this.listaDTO = listaDTO;
	}

	public Integer getEstadoFinalizado() {
		return estadoFinalizado;
	}

	public void setEstadoFinalizado(Integer estadoFinalizado) {
		this.estadoFinalizado = estadoFinalizado;
	}

}
