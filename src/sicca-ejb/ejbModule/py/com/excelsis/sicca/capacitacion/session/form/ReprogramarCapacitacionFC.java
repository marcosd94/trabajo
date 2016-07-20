package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.jboss.ws.core.soap.attachment.CIDGenerator;

import com.sun.org.apache.bcel.internal.generic.NEW;

import enums.ActividadEnum;
import enums.DestinadoA;
import enums.ProcesoEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ConsultasCapacitacion;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.ProrrogaCap;
import py.com.excelsis.sicca.entity.PublicacionCapacitacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("reprogramarCapacitacionFC")
public class ReprogramarCapacitacionFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8609007183143839629L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private List<SelectItem> departamentosSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecItem = new ArrayList<SelectItem>();
	private List<CapacitacionCerrada> listaCapacitacionCerradas = new ArrayList<CapacitacionCerrada>();
	private List<CapacitacionCerrada> listaCapacitacionCerradasEliminar = new ArrayList<CapacitacionCerrada>();
	private List<ConsultasCapacitacion> listaConsultaCapacitacion = new ArrayList<ConsultasCapacitacion>();
	private List<ConsultasCapacitacion> listaConsultaCapacitacionEliminar = new ArrayList<ConsultasCapacitacion>();

	private Capacitaciones capacitaciones = new Capacitaciones();
	private ConfiguracionUoCab oee = new ConfiguracionUoCab();
	private CapacitacionCerrada cerrada = new CapacitacionCerrada();
	private ConsultasCapacitacion consulta = new ConsultasCapacitacion();
	NivelEntidadOeeUtil nivelEntidadOeeUtilCerrado = new NivelEntidadOeeUtil();

	private DestinadoA destinadoA = null;

	private Date fechaDesdeNva;
	private Date fechaHastaNva;
	private Date fechaPubDesdeNva;
	private Date fechaPubHastaNva;
	private Long idCapacitacion;
	private Long idPais;
	private Long idCiudad = null;
	private Long codDepartamento = null;
	private Long cupo = null;
	private Integer indexUpPart = null;
	private Integer indexUpConsulta = null;
	private boolean habCerrado = false;
	private boolean esEditCerrado = false;
	private boolean esEditConsulta = false;
	private String lugar;
	private String direccion;
	private String telefono;
	private String email;
	private String horaAtencionDesde;
	private String horaAtencionHasta;

	public void init() throws Exception {

		if (idCapacitacion != null) {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			if (capacitaciones == null
					|| capacitaciones.getIdCapacitacion() == null) {
				capacitaciones = new Capacitaciones();
				capacitaciones = em.find(Capacitaciones.class, idCapacitacion);

				seguridadUtilFormController.validarCapacitacion(idCapacitacion,
						capacitaciones.getEstado(),
						ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC
								.getValor());

				oee = usuarioLogueado.getConfiguracionUoCab();
				cargarDatosNivel();
				idCiudad = capacitaciones.getCiudad().getIdCiudad();
				codDepartamento = capacitaciones.getCiudad().getDepartamento()
						.getIdDepartamento();
				idPais = capacitaciones.getCiudad().getDepartamento().getPais()
						.getIdPais();
				updateCiudad();
				updateDepartamento();
				if (capacitaciones.getDestinadoA() != null) {
					destinadoA = DestinadoA.getTipoPorId(capacitaciones
							.getDestinadoA());
					if (destinadoA.getId().equals("C")) {
						if (listaCapacitacionCerradas.size() == 0)
							buscarCapacitacionesCerradas();
						if (listaConsultaCapacitacion.size() == 0)
							buscarConsultasCapacitacion();
					}
				}

			}
			nivelEntidadOeeUtilCerrado.init();
		}
	}

	public void initEdit() throws Exception {

		if (idCapacitacion != null) {
			SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			capacitaciones = new Capacitaciones();
			capacitaciones = em.find(Capacitaciones.class, idCapacitacion);

			oee = usuarioLogueado.getConfiguracionUoCab();
			cargarDatosNivel();
			idCiudad = capacitaciones.getCiudad().getIdCiudad();
			codDepartamento = capacitaciones.getCiudad().getDepartamento()
					.getIdDepartamento();
			idPais = capacitaciones.getCiudad().getDepartamento().getPais()
					.getIdPais();
			updateCiudad();
			updateDepartamento();
			if (capacitaciones.getDestinadoA() != null) {
				destinadoA = DestinadoA.getTipoPorId(capacitaciones
						.getDestinadoA());
				if (destinadoA.getId().equals("C")) {
					if (listaCapacitacionCerradas.size() == 0)
						buscarCapacitacionesCerradas();
					if (listaConsultaCapacitacion.size() == 0)
						buscarConsultasCapacitacion();
				}
			}

		}
		nivelEntidadOeeUtilCerrado.init();
	}

	/**
	 * Método que busca las capacitaciones cerradas pertenecientes al
	 * id_capacitacion recibido
	 */
	private void buscarCapacitacionesCerradas() {
		String sql = "SELECT C.* FROM capacitacion.capacitacion_cerrada C WHERE C.ID_CAPACITACION = "
				+ idCapacitacion;
		listaCapacitacionCerradas = em.createNativeQuery(sql,
				CapacitacionCerrada.class).getResultList();
	}

	/**
	 * Método que busca en la tabla capacitacion.consultas_capacitacion los
	 * registros con id_capacitacion igual al recibido
	 */
	private void buscarConsultasCapacitacion() {
		String sql = "select c.* from capacitacion.consultas_capacitacion c where c.id_capacitacion = "
				+ idCapacitacion;
		listaConsultaCapacitacion = em.createNativeQuery(sql,
				ConsultasCapacitacion.class).getResultList();
	}

	/**
	 * Método que habilita el panel cerrado en caso de que la capacitacion sea
	 * cerrada
	 */
	public void selecCerrado() {
		if (destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
	}

	/**
	 * Carga datos de la cabecera
	 */
	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitaciones.getConfiguracionUoDet() != null) {
				Long idUo = capacitaciones.getConfiguracionUoDet()
						.getIdConfiguracionUoDet();
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(idUo);
			}
		}
		cargarCabecera();
	}

	public void cargarCabecera() {
		Long idOee = capacitaciones.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		ConfiguracionUoCab uoCab = new ConfiguracionUoCab();
		uoCab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEnt = uoCab.getEntidad().getIdEntidad();
		Entidad ent = new Entidad();
		ent = em.find(Entidad.class, idEnt);
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(ent
				.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(ent.getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		nivelEntidadOeeUtil.init();

	}

	/**
	 * Método para el combo departamento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPais != null) {
			departamentoList.getPais().setIdPais(idPais);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	/**
	 * Método para el combo ciudad
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
	}

	private List<Ciudad> getCiudadByDpto() {
		if (codDepartamento != null) {
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Método que agrega un registro a la lista OEE
	 */
	public void addCerrado() {
		statusMessages.clear();
		if (nivelEntidadOeeUtilCerrado.getIdSinNivelEntidad() == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Nivel de Instituciones Participadoras antes de realizar esta acción.");
			return;
		}
		if (nivelEntidadOeeUtilCerrado.getIdSinEntidad() == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Entidad de Instituciones Participadoras antes de realizar esta acción.");
			return;
		}
		if (nivelEntidadOeeUtilCerrado.getIdConfigCab() == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo OEE de Instituciones Participadoras antes de realizar esta acción.");
			return;
		}
		if (cupo != null && cupo.longValue() <= 0) {
			statusMessages.add(Severity.WARN,
					"El campo cupo debe ser mayor  a cero. verifique");
			return;
		}
		CapacitacionCerrada aux = new CapacitacionCerrada();
		aux.setActivo(true);
		aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		if (cupo != null)
			aux.setCupo(cupo);

		aux.setFechaAlta(new Date());
		aux.setConfiguracionUo(em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtilCerrado.getIdConfigCab()));
		if (nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa() != null)
			aux.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class,
					nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa()));

		listaCapacitacionCerradas.add(aux);

		limpiarCerrado();

	}

	/**
	 * Método que limpia los campos del panel cerrado
	 */
	public void limpiarCerrado() {
		nivelEntidadOeeUtilCerrado.limpiar();
		cupo = null;
		esEditCerrado = false;
		indexUpPart = null;

		if (destinadoA != null && destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
	}

	/**
	 * Método que carga los campos a ser editados en en panel cerrado
	 * 
	 * @param index
	 */
	public void editarCerrado(int index) {
		cerrada = listaCapacitacionCerradas.get(index);
		esEditCerrado = true;
		cupo = cerrada.getCupo();

		if (cerrada.getConfiguracionUo() != null)
			cargarOeeCerrado(cerrada.getConfiguracionUo(),
					cerrada.getConfiguracionUoDet());

		if (destinadoA != null && destinadoA.getId().equals("C"))
			habCerrado = true;
		else
			habCerrado = false;
		esEditCerrado = true;
		indexUpPart = index;
	}

	public void cargarOeeCerrado(ConfiguracionUoCab oee, ConfiguracionUoDet uo) {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtilCerrado
				.getIdSinNivelEntidad(oee.getEntidad().getSinEntidad()
						.getNenCodigo());
		nivelEntidadOeeUtilCerrado.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtilCerrado.setIdSinEntidad(oee.getEntidad()
				.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtilCerrado.setIdConfigCab(oee.getIdConfiguracionUo());

		if (uo != null)
			nivelEntidadOeeUtilCerrado.setIdUnidadOrganizativa(uo
					.getIdConfiguracionUoDet());

		nivelEntidadOeeUtilCerrado.init();

	}

	/**
	 * Método que elimina un registro del panel cerrado
	 * 
	 * @param index
	 */
	public void eliminarCerrado(int index) {
		cerrada = listaCapacitacionCerradas.get(index);
		if (cerrada.getIdCapacitacionCerrada() != null) {
			listaCapacitacionCerradasEliminar.add(cerrada);
		}
		listaCapacitacionCerradas.remove(index);
	}

	/**
	 * Método que actualiza un registro del panel cerrado
	 */
	public void upCerrado() {
		statusMessages.clear();
		if (nivelEntidadOeeUtilCerrado.getIdSinNivelEntidad() == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Nivel de Instituciones Participadoras antes de realizar esta acción.");
			return;
		}
		if (nivelEntidadOeeUtilCerrado.getIdSinEntidad() == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Entidad de Instituciones Participadoras antes de realizar esta acción.");
			return;
		}
		if (nivelEntidadOeeUtilCerrado.getIdConfigCab() == null) {
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo OEE de Instituciones Participadoras antes de realizar esta acción.");
			return;
		}

		cerrada.setUsuMod(usuarioLogueado.getCodigoUsuario());
		cerrada.setFechaMod(new Date());

		cerrada.setActivo(true);
		cerrada.setCupo(cupo);
		cerrada.setConfiguracionUo(em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtilCerrado.getIdConfigCab()));
		if (nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa() != null)
			cerrada.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class,
					nivelEntidadOeeUtilCerrado.getIdUnidadOrganizativa()));

		listaCapacitacionCerradas.set(indexUpPart, cerrada);

		limpiarCerrado();

	}

	/**
	 * Metodo que separa en hh:mm el string ingresado
	 * 
	 * @param cod
	 * @return
	 */
	public String buscarHora(String cod) {

		String[] arrayCod = cod.split("\\ ");
		String[] arrayCodigo;
		if (arrayCod.length == 1)
			arrayCodigo = arrayCod[0].split(":");
		else
			arrayCodigo = arrayCod[3].split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	/**
	 * Método que carga los campos a ser editados en el panel lugares de
	 * consultas y aclaraciones
	 * 
	 * @param r
	 */
	public void editar(Integer r) {
		indexUpConsulta = r;
		consulta = new ConsultasCapacitacion();
		consulta = listaConsultaCapacitacion.get(indexUpConsulta);
		direccion = consulta.getDireccion();
		lugar = consulta.getLugar();
		horaAtencionDesde = buscarHora(consulta.getHoraDesde().toString());
		horaAtencionHasta = buscarHora(consulta.getHoraHasta().toString());
		telefono = consulta.getTelefono();
		email = consulta.getEmail();
		esEditConsulta = true;
	}

	/**
	 * Método que elimina un registro de la lista de lugares de consultas y
	 * aclaraciones
	 * 
	 * @param r
	 */
	public void eliminar(Integer r) {
		consulta = new ConsultasCapacitacion();
		consulta = listaConsultaCapacitacion.get(r);
		if (consulta.getIdConsultas() != null)
			listaConsultaCapacitacionEliminar.add(consulta);
		listaConsultaCapacitacion.remove(consulta);

	}

	/**
	 * Método que chequea que los campos obligatorios en el panel consultas y
	 * aclaraciones sean cargados
	 * 
	 * @return
	 */
	private Boolean check() {
		if (lugar == null || lugar.trim().isEmpty())
			return false;
		if (direccion == null || direccion.trim().isEmpty())
			return false;
		if (horaAtencionDesde == null || horaAtencionDesde.equals(""))
			return false;
		if (horaAtencionHasta == null || horaAtencionHasta.equals(""))
			return false;
		return true;
	}

	/**
	 * Método que agrega un registro a la lista del panel de lugares de
	 * consultas y aclaraciones
	 */
	public void agregarListaConsulta() {
		statusMessages.clear();
		consulta = new ConsultasCapacitacion();
		if (!check()) {
			statusMessages.add(Severity.WARN,
					"Debe ingresar los datos obligatorios.");

			return;
		}
		String msg = validarFecha();
		if (msg != null) {
			statusMessages.add(Severity.WARN, msg);
			return;
		}

		consulta.setActivo(true);
		consulta.setCapacitaciones(capacitaciones);
		consulta.setDireccion(direccion);
		consulta.setLugar(lugar);

		if (email != null && !email.trim().isEmpty())
			consulta.setEmail(email);
		if (telefono != null && !telefono.trim().isEmpty())
			consulta.setTelefono(telefono);

		listaConsultaCapacitacion.add(consulta);
		limpiarCampos();
	}

	/**
	 * Método que limpia los campos del panel de lugares de consultas y
	 * aclaraciones
	 */
	private void limpiarCampos() {
		direccion = null;
		lugar = null;
		horaAtencionDesde = null;
		horaAtencionHasta = null;
		telefono = null;
		email = null;
		esEditConsulta = false;
	}

	public void agregarEditadoLista() {
		statusMessages.clear();
		if (!check()) {
			statusMessages.add(Severity.WARN,
					"Debe ingresar los datos obligatorios.");
			return;
		}
		String msg = validarFecha();
		if (msg != null) {
			statusMessages.add(Severity.WARN, msg);
			return;
		}

		consulta.setDireccion(direccion);
		consulta.setLugar(lugar);
		if (email != null && !email.trim().isEmpty())
			consulta.setEmail(email);
		if (telefono != null && !telefono.trim().isEmpty())
			consulta.setTelefono(telefono);
		listaConsultaCapacitacion.set(indexUpConsulta, consulta);
		limpiarCampos();
	}

	public void cancelarEditado() {
		limpiarCampos();
	}

	/**
	 * Método que valida que la hora ingresada sea la correcta
	 * 
	 * @return
	 */
	private String validarFecha() {
		try {
			int[] horaDesde = getHora(horaAtencionDesde);
			if (horaDesde[0] > 23)
				return "La hora no puede ser mayor a 23";
			if (horaDesde[1] > 59)
				return "El minuto no puede ser mayor a 59";

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setLenient(false);
			try {
				Date hd = sdf.parse(horaAtencionDesde);
				Date hh = sdf.parse(horaAtencionHasta);
				if (hd.getTime() >= hh.getTime())
					return "La Hora de Atención Desde debe ser menor a la Hora Hasta";

			} catch (ParseException e) {
				return "Formato de Hora incorrecto. Debe introducir, Ej.: HH:mm (formato 24 Hs.)";
			}
			Date fechaDesde = new Date();
			fechaDesde.setHours(horaDesde[0]);
			fechaDesde.setMinutes(horaDesde[1]);
			consulta.setHoraDesde(fechaDesde);
			int[] horaHasta = getHora(horaAtencionHasta);
			if (horaHasta[0] > 23)
				return "La hora no puede ser mayor a 23";

			if (horaHasta[1] > 59)
				return "El minuto no puede ser mayor a 59";

			Date fechaHasta = new Date();

			fechaHasta.setHours(horaHasta[0]);
			fechaHasta.setMinutes(horaHasta[1]);
			consulta.setHoraHasta(fechaHasta);
		} catch (Exception e) {
			return "Debe ingresar horas válidas";

		}
		return null;
	}

	/**
	 * @param horaCad
	 * @return
	 */
	private int[] getHora(String horaCad) {
		String[] horas = horaCad.split(":");
		if (horas.length != 2) {
			return null;
		} else {
			String hora = horas[0];
			String minuto = horas[1];
			try {
				int hh = Integer.parseInt(hora);
				int mm = Integer.parseInt(minuto);

				if (hh < 0 || hh > 23 || mm < 0 || mm >= 60) {
					return null;
				}
				int[] v = new int[2];
				v[0] = hh;
				v[1] = mm;
				return v;
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * Método que cancela la capacitación
	 * 
	 * @return
	 */
	public String cancelarCapacitacion() {
		statusMessages.clear();
		if (capacitaciones.getMotivoCancelacion() == null
				|| capacitaciones.getMotivoCancelacion().trim().isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Motivo de Cancelación antes de realizar esta acción.");
			return null;
		}
		try {
			guardarActualizarProrroga();
			actualizarCapacitacion();
			enviarEmailCancelacion();
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitaciones);
			jbpmUtilFormController.setTransition("repCanCap_TO_end");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * En caso de que existan registros en la tabla capacitacion.prorroga_cap
	 * con id_capacitacion igual a la recibida se actualiza dicho registro, caso
	 * contrario se inserta uno nuevo
	 */
	private void guardarActualizarProrroga() {
		String sql = "select c.*  " + "from capacitacion.prorroga_cap c "
				+ "where c.estado = 'A PRORROGAR' and c.id_capacitacion = "
				+ capacitaciones.getIdCapacitacion();
		List<ProrrogaCap> listaProrroga = new ArrayList<ProrrogaCap>();
		listaProrroga = em.createNativeQuery(sql, ProrrogaCap.class)
				.getResultList();
		ProrrogaCap prorroga = new ProrrogaCap();
		if (listaProrroga != null && listaProrroga.size() > 0) {

			prorroga = listaProrroga.get(0);
			prorroga.setEstado("CANCELADO");
			prorroga.setUsuMod(usuarioLogueado.getCodigoUsuario());
			prorroga.setFechaMod(new Date());
			prorroga.setMotivo(capacitaciones.getMotivoCancelacion());
			em.merge(prorroga);
			return;
		}
		prorroga.setActivo(true);
		prorroga.setCapacitaciones(capacitaciones);
		prorroga.setEstado("CANCELADO");
		prorroga.setFechaAlta(new Date());
		prorroga.setMotivo(capacitaciones.getMotivoCancelacion());
		prorroga.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(prorroga);
		return;

	}

	/**
	 * Se actualiza la tabla capacitacion
	 */
	private void actualizarCapacitacion() {
		Referencias referencias = new Referencias();
		referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_CAPACITACION", "CANCELADA");
		capacitaciones.setActivo(false);
		capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
		capacitaciones.setFechaMod(new Date());
		if (referencias != null)
			capacitaciones.setEstado(referencias.getValorNum());
		em.merge(capacitaciones);
	}

	/**
	 * Envia email a las personas que se han postulado a la capacitación
	 * cancelada
	 */
	private void enviarEmailCancelacion() {
		String cad = "SELECT  CAPACITACION.FNC_EMAIL_CONSULTA(ID_CAPACITACION) AS EMAIL "
				+ "FROM  CAPACITACION.CAPACITACIONES "
				+ "WHERE ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();
		Object resultado = (Object) em.createNativeQuery(cad).getSingleResult();
		String listaCorreos = resultado.toString();
		String sql = "select p.* " + "from capacitacion.postulacion_cap p  "
				+ "Where p.activo is true " + "and p.id_capacitacion = "
				+ capacitaciones.getIdCapacitacion();
		List<PostulacionCap> listaPostulaciones = new ArrayList<PostulacionCap>();
		listaPostulaciones = em.createNativeQuery(sql, PostulacionCap.class)
				.getResultList();

		for (PostulacionCap p : listaPostulaciones) {
			String dirEmail = "";
			dirEmail = p.getPersona().getEMail();
			String asunto = "Portal Paraguay Concursa - " + p.getNombreAp()
					+ " - Cancelada";
			String texto = "";
			String configuracionCab = "";
			if (capacitaciones.getConfiguracionUoCab() != null)
				configuracionCab = capacitaciones.getConfiguracionUoCab()
						.getDenominacionUnidad();
			try {
				texto = texto
						+ "<p align=\"justify\"> <b> Estimado/a "
						+ p.getPersona().getNombreCompleto()
						+ "</b></p>"
						+ "<p align=\"justify\">Comunicamos a usted, que ha sido cancelada la ejecuci&oacute;n de: </p>"
						+ "<p align=\"center\"> <b>"
						+ p.getCapacitacion().getDatosEspecificosTipoCap()
								.getDescripcion()
						+ "</b></p>"
						+ "<p align=\"center\"> <b>"
						+ p.getCapacitacion().getNombre()
						+ "</b></p>"
						+ "<p align=\"justify\"><b> Motivo: "
						+ capacitaciones.getMotivoCancelacion()
						+ "</b></p>"

						+ "<p></p>"
						+ "<p> Atentamente,</p> "
						+ "<b>"
						+ configuracionCab
						+ "</p></b>"

						+ "<p align=\"justify\"><b>Para mayor información comunicarse con: </b> "
						+ listaCorreos + "</p>";

				usuarioPortalFormController.enviarMails(dirEmail, texto,
						asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private Boolean chequearCampos() {
		if (fechaDesdeNva == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Fecha Desde del panel Nuevas Fechas de Recepción de Postulaciones antes de realizar esta acción.");
			return false;
		}
		if (fechaHastaNva == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Fecha Hasta del panel Nuevas Fechas de Recepción de Postulaciones antes de realizar esta acción.");
			return false;
		}
		if (fechaHastaNva.before(fechaDesdeNva)) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"La Fecha Desde del panel Nuevas Fechas de Recepción de Postulaciones no puede ser mayor a la Fecha Hasta.");
			return false;
		}
		if (capacitaciones.getSede() == null
				|| capacitaciones.getSede().trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Sede del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (capacitaciones.getDireccion() == null
				|| capacitaciones.getDireccion().trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Dirección del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (idPais == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo País del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (codDepartamento == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Departamento del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (idCiudad == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Ciudad del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (capacitaciones.getFechaInicio() == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Fecha Inicio del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (capacitaciones.getFechaFin() == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"Debe completar el campo Fecha Fin del panel Datos de Publicación antes de realizar esta acción.");
			return false;
		}
		if (capacitaciones.getFechaFin()
				.before(capacitaciones.getFechaInicio())) {
			statusMessages.clear();
			statusMessages
					.add(Severity.WARN,
							"La Fecha Inicio del panel Datos de Publicación no debe ser mayor a la Fecha Fin.");
			return false;
		}

		return true;
	}

	public String reprogramarPublicar() {
		if (!chequearCampos())
			return null;
		try {
			actualizarProrrogaReprogamacion();
			actualizaConsultasCapacitacion();
			if (destinadoA.getId().equals("C"))
				actualizaCapacitacionCerrada();
			actualizarCapacitacionReprogramar();

			// Bloque REPROGRAMAR
			bloqueReprogramar();
			Referencias refReprogramar = new Referencias();
			refReprogramar = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "REPROGRAMAR");
			PublicacionCapacitacion publicacionReprogramacion = new PublicacionCapacitacion();
			publicacionReprogramacion.setActivo(true);
			publicacionReprogramacion.setBloque("REPROGRAMAR");
			publicacionReprogramacion.setCapacitaciones(capacitaciones);
			publicacionReprogramacion.setFechaAlta(new Date());
			publicacionReprogramacion.setUsuAlta(usuarioLogueado
					.getCodigoUsuario());
			if (refReprogramar != null)
				publicacionReprogramacion
						.setOrden(refReprogramar.getValorNum());
			publicacionReprogramacion.setTexto(obtenerTextoReprogramacion());
			em.persist(publicacionReprogramacion);

			// Bloque Destinatarios
			bloqueDestinatarios();
			Referencias refDestinatarios = new Referencias();
			refDestinatarios = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "DESTINATARIOS");
			PublicacionCapacitacion publicacionDestinatarios = new PublicacionCapacitacion();
			publicacionDestinatarios.setActivo(true);
			publicacionDestinatarios.setBloque("DESTINATARIOS");
			publicacionDestinatarios.setCapacitaciones(capacitaciones);
			publicacionDestinatarios.setFechaAlta(new Date());
			publicacionDestinatarios.setUsuAlta(usuarioLogueado
					.getCodigoUsuario());
			if (refDestinatarios != null)
				publicacionDestinatarios.setOrden(refDestinatarios
						.getValorNum());
			publicacionDestinatarios.setTexto(obtenerTextoDestinatarios());
			em.persist(publicacionDestinatarios);

			// Bloque Modalidad
			bloqueModalidad();
			Referencias refModalidad = new Referencias();
			refModalidad = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "MODALIDAD");
			PublicacionCapacitacion publicacionModalidad = new PublicacionCapacitacion();
			publicacionModalidad.setActivo(true);
			publicacionModalidad.setBloque("MODALIDAD");
			publicacionModalidad.setCapacitaciones(capacitaciones);
			publicacionModalidad.setFechaAlta(new Date());
			publicacionModalidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			if (refModalidad != null)
				publicacionModalidad.setOrden(refModalidad.getValorNum());
			publicacionModalidad.setTexto(obtenerTextoModalidad());
			em.persist(publicacionModalidad);

			// Bloque Consultas
			bloqueConsultas();
			Referencias refConsultas = new Referencias();
			refConsultas = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "CONSULTAS");
			PublicacionCapacitacion publicacionConsultas = new PublicacionCapacitacion();
			publicacionConsultas.setActivo(true);
			publicacionConsultas.setBloque("CONSULTAS");
			publicacionConsultas.setCapacitaciones(capacitaciones);
			publicacionConsultas.setFechaAlta(new Date());
			publicacionConsultas.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			if (refConsultas != null)
				publicacionConsultas.setOrden(refConsultas.getValorNum());
			publicacionConsultas.setTexto(obtenerTextoConsultas());
			em.persist(publicacionConsultas);

			enviarEmailReprogramacion();
			/* Pasa a la sgte tarea */

			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CAPA_REPROGRAMAR_CANCELAR_CAPAC);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC);
			jbpmUtilFormController.setTransition("repCanCap_TO_recPosIns");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitaciones);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			return null;
		}

	}

	private void actualizarProrrogaReprogamacion() {
		String sql = "select c.*  " + "from capacitacion.prorroga_cap c "
				+ "where c.estado = 'A PRORROGAR' and c.id_capacitacion = "
				+ capacitaciones.getIdCapacitacion();
		List<ProrrogaCap> listaProrroga = new ArrayList<ProrrogaCap>();
		listaProrroga = em.createNativeQuery(sql, ProrrogaCap.class)
				.getResultList();
		ProrrogaCap prorroga = new ProrrogaCap();
		if (listaProrroga != null && listaProrroga.size() > 0) {

			prorroga = listaProrroga.get(0);
			prorroga.setFechaRecDesN(fechaDesdeNva);
			prorroga.setFechaRecHasN(fechaHastaNva);
			prorroga.setFechaPubDesN(fechaPubDesdeNva);
			prorroga.setFechaPubHasN(fechaPubHastaNva);
			prorroga.setFechaRecDesA(capacitaciones.getFechaRecepDesde());
			prorroga.setFechaRecHasA(capacitaciones.getFechaRecepHasta());
			prorroga.setFechaPubDesA(capacitaciones.getFechaPubDesde());
			prorroga.setFechaPubHasA(capacitaciones.getFechaPubHasta());
			prorroga.setEstado("PRORROGADO");
			prorroga.setUsuMod(usuarioLogueado.getCodigoUsuario());
			prorroga.setFechaMod(new Date());
			em.merge(prorroga);
			return;
		}
		prorroga.setActivo(true);
		prorroga.setCapacitaciones(capacitaciones);
		prorroga.setFechaRecDesN(fechaDesdeNva);
		prorroga.setFechaRecHasN(fechaHastaNva);
		prorroga.setFechaPubDesN(fechaPubDesdeNva);
		prorroga.setFechaPubHasN(fechaPubHastaNva);
		prorroga.setFechaRecDesA(capacitaciones.getFechaRecepDesde());
		prorroga.setFechaRecHasA(capacitaciones.getFechaRecepHasta());
		prorroga.setFechaPubDesA(capacitaciones.getFechaPubDesde());
		prorroga.setFechaPubHasA(capacitaciones.getFechaPubHasta());
		prorroga.setEstado("PRORROGADO");
		prorroga.setFechaAlta(new Date());
		prorroga.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(prorroga);
		return;

	}

	private void actualizaConsultasCapacitacion() {
		for (ConsultasCapacitacion cel : listaConsultaCapacitacionEliminar)
			em.remove(cel);
		for (ConsultasCapacitacion c : listaConsultaCapacitacion) {
			if (c.getIdConsultas() != null) {
				c.setFechaMod(new Date());
				c.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(c);
			} else {
				c.setActivo(true);
				c.setCapacitaciones(capacitaciones);
				c.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				c.setFechaAlta(new Date());
				em.persist(c);
			}
		}
	}

	private void actualizaCapacitacionCerrada() {
		for (CapacitacionCerrada ce : listaCapacitacionCerradasEliminar)
			em.remove(ce);
		for (CapacitacionCerrada lc : listaCapacitacionCerradas) {
			if (lc.getIdCapacitacionCerrada() != null) {
				lc.setFechaMod(new Date());
				lc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(lc);
			} else {
				lc.setActivo(true);
				lc.setCapacitaciones(capacitaciones);
				lc.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				lc.setFechaAlta(new Date());
				em.persist(lc);
			}
		}
	}

	private void actualizarCapacitacionReprogramar() {
		Referencias referencias = new Referencias();
		referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_CAPACITACION", "RECEPCIONAR POSTULANTES");
		capacitaciones.setFechaRecepDesde(fechaDesdeNva);
		capacitaciones.setFechaRecepHasta(fechaHastaNva);
		capacitaciones.setFechaPubDesde(fechaPubDesdeNva);
		capacitaciones.setFechaPubHasta(fechaPubHastaNva);
		capacitaciones.setCiudad(em.find(Ciudad.class, idCiudad));
		capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
		capacitaciones.setFechaMod(new Date());
		capacitaciones.setEstadoPublic("A VERIFICAR");
		capacitaciones.setDestinadoA(destinadoA.getId());
		if (referencias != null)
			capacitaciones.setEstado(referencias.getValorNum());
		em.merge(capacitaciones);
	}

	private void bloqueReprogramar() {
		String sql = "SELECT P.* "
				+ "FROM CAPACITACION.PUBLICACION_CAPACITACION P "
				+ "WHERE P.ACTIVO IS TRUE " + "AND P.BLOQUE = 'REPROGRAMAR' "
				+ "AND P.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();

		List<PublicacionCapacitacion> listaPublicacion = new ArrayList<PublicacionCapacitacion>();
		listaPublicacion = em.createNativeQuery(sql,
				PublicacionCapacitacion.class).getResultList();

		for (PublicacionCapacitacion p : listaPublicacion) {
			p.setActivo(false);
			em.merge(p);
		}

	}

	private String obtenerTextoReprogramacion() {
		StringBuffer texto = new StringBuffer();
		String br = "<br/>";
		String color = "#FF0000";
		String spanColorO = "<span style=" + color + ">";
		String spanColorC = "</span>";
		SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");
		texto.append(spanColorO
				+ "Se ha reprogramado la Capacitación/Beca. Las Nuevas fechas de recepción serán: desde "
				+ sdfFecha.format(fechaDesdeNva) + " hasta "
				+ sdfFecha.format(fechaHastaNva) + spanColorC + br);

		return texto.toString();
	}

	private void bloqueDestinatarios() {
		String sql = "SELECT P.* "
				+ "FROM CAPACITACION.PUBLICACION_CAPACITACION P "
				+ "WHERE P.ACTIVO IS TRUE " + "AND P.BLOQUE = 'DESTINATARIOS' "
				+ "AND P.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();

		List<PublicacionCapacitacion> listaPublicacion = new ArrayList<PublicacionCapacitacion>();
		listaPublicacion = em.createNativeQuery(sql,
				PublicacionCapacitacion.class).getResultList();

		for (PublicacionCapacitacion p : listaPublicacion) {
			p.setActivo(false);
			em.merge(p);
		}
	}

	private String obtenerTextoDestinatarios() {
		StringBuffer texto = new StringBuffer();
		String h3O = "<h3>";
		String h3C = "</h3>";
		String pO = "<p>";
		String pC = "</p>";
		String br = "<br/>";
		texto.append(h3O + "DESTINATARIOS: " + h3C + br);
		texto.append(pO + destinadoA() + entidades() + pC + br);
		return texto.toString();
	}

	private void bloqueModalidad() {
		String sql = "SELECT P.* "
				+ "FROM CAPACITACION.PUBLICACION_CAPACITACION P "
				+ "WHERE P.ACTIVO IS TRUE " + "AND P.BLOQUE = 'MODALIDAD' "
				+ "AND P.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();

		List<PublicacionCapacitacion> listaPublicacion = new ArrayList<PublicacionCapacitacion>();
		listaPublicacion = em.createNativeQuery(sql,
				PublicacionCapacitacion.class).getResultList();

		for (PublicacionCapacitacion p : listaPublicacion) {
			p.setActivo(false);
			em.merge(p);
		}
	}

	private String obtenerTextoModalidad() {
		StringBuffer texto = new StringBuffer();
		String h3O = "<h3>";
		String h3C = "</h3>";
		String pO = "<p>";
		String pC = "</p>";
		String br = "<br/>";
		SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");

		texto.append(h3O + "MODALIDAD Y DURACIÓN: " + h3C + br);
		texto.append(pO + "Modalidad" + modalidad()
				+ "La carga horaria total es de "
				+ capacitaciones.getCargaHoraria()
				+ " horas. La fecha de Inicio programada es: "
				+ sdfFecha.format(capacitaciones.getFechaInicio())
				+ " y la fecha Finalización es "
				+ sdfFecha.format(capacitaciones.getFechaFin()) + pC + br);
		return texto.toString();
	}

	private void bloqueConsultas() {
		String sql = "SELECT P.* "
				+ "FROM CAPACITACION.PUBLICACION_CAPACITACION P "
				+ "WHERE P.ACTIVO IS TRUE " + "AND P.BLOQUE = 'CONSULTAS' "
				+ "AND P.ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();

		List<PublicacionCapacitacion> listaPublicacion = new ArrayList<PublicacionCapacitacion>();
		listaPublicacion = em.createNativeQuery(sql,
				PublicacionCapacitacion.class).getResultList();

		for (PublicacionCapacitacion p : listaPublicacion) {
			p.setActivo(false);
			em.merge(p);
		}
	}

	private String obtenerTextoConsultas() {
		StringBuffer texto = new StringBuffer();
		String h3O = "<h3>";
		String h3C = "</h3>";
		String pO = "<p>";
		String pC = "</p>";
		String br = "<br/>";

		texto.append(h3O + "CONSULTAS Y ACLARACIONES: " + h3C + br);
		texto.append(pO + aclaraciones() + pC + br);

		return texto.toString();
	}

	private String aclaraciones() {
		String resultado = "";
		SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm:ss");
		for (ConsultasCapacitacion o : listaConsultaCapacitacion) {
			resultado += "<p> Lugar: " + o.getLugar() + ". Dirección: "
					+ o.getDireccion() + ". Teléfonos: " + o.getTelefono()
					+ ". Hora desde: " + sdfHora.format(o.getHoraDesde())
					+ ". Hora hasta: " + sdfHora.format(o.getHoraHasta())
					+ "</p>";
		}
		return resultado;
	}

	private String modalidad() {
		if (capacitaciones.getModalidad().equals("P"))
			return "Presencial. ";
		if (capacitaciones.getModalidad().equals("S"))
			return "Semi - presencial. ";
		if (capacitaciones.getModalidad().equals("V"))
			return "Virtual. ";
		return "";
	}

	private String destinadoA() {
		if (destinadoA.getId().equals("C"))
			return "Cerrado. ";
		if (destinadoA.getId().equals("A"))
			return "Abierto a toda la ciudadanía. ";
		if (destinadoA.getId().equals("F"))
			return "Abierto a todos los Funcionarios Públicos. ";
		return "";
	}

	private String entidades() {
		if (destinadoA.getId().equals("C")) {
			String hql = "SELECT c.* FROM capacitacion.capacitacion_cerrada c "
					+ "WHERE c.id_capacitacion = "
					+ capacitaciones.getIdCapacitacion();
			List<CapacitacionCerrada> lista = new ArrayList<CapacitacionCerrada>();
			lista = lista = em
					.createNativeQuery(hql, CapacitacionCerrada.class)
					.getResultList();
			String resultado = "";
			if (lista != null && lista.size() > 0)
				resultado = "Entidades del Estado: ";
			for (CapacitacionCerrada o : lista) {
				if (o.getConfiguracionUoDet() != null)
					resultado += o.getConfiguracionUoDet()
							.getDenominacionUnidad() + ", ";
			}
			Integer indice = resultado.trim().length();
			if (indice > 0)
				return resultado.trim().substring(indice - 1);

		}
		return "";
	}

	private void enviarEmailReprogramacion() {
		String cad = "SELECT  CAPACITACION.FNC_EMAIL_CONSULTA(ID_CAPACITACION) AS EMAIL "
				+ "FROM  CAPACITACION.CAPACITACIONES "
				+ "WHERE ID_CAPACITACION = "
				+ capacitaciones.getIdCapacitacion();
		Object resultado = (Object) em.createNativeQuery(cad).getSingleResult();
		String listaCorreos = resultado.toString();
		String sql = "select p.* " + "from capacitacion.postulacion_cap p  "
				+ "Where p.activo is true " + "and p.id_capacitacion = "
				+ capacitaciones.getIdCapacitacion();
		List<PostulacionCap> listaPostulaciones = new ArrayList<PostulacionCap>();
		listaPostulaciones = em.createNativeQuery(sql, PostulacionCap.class)
				.getResultList();
		SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");
		for (PostulacionCap p : listaPostulaciones) {
			String dirEmail = "";
			dirEmail = p.getPersona().getEMail();
			String asunto = "Portal Paraguay Concursa - " + p.getNombreAp()
					+ " - Reprogramación";
			String texto = "";
			String configuracionCab = "";
			if (capacitaciones.getConfiguracionUoCab() != null)
				configuracionCab = capacitaciones.getConfiguracionUoCab()
						.getDenominacionUnidad();
			try {
				texto = texto
						+ "<p align=\"justify\"> <b> Estimado/a "
						+ p.getPersona().getNombreCompleto()
						+ "</b></p>"
						+ "<p align=\"justify\">Comunicamos a usted, que ha sido reprogramada la ejecuci&oacute;n de: </p>"
						+ "<p align=\"center\"> <b>"
						+ p.getCapacitacion().getDatosEspecificosTipoCap()
								.getDescripcion()
						+ "</b></p>"
						+ "<p align=\"center\"> <b>"
						+ p.getCapacitacion().getNombre()
						+ "</b></p>"
						+ "<p></p>"
						+ "<p align=\"justify\"><b>Recepci&oacute;n de Postulaciones</b></p>"
						+ "<p align=\"justify\"><b>- Fecha Desde: "
						+ sdfFecha.format(p.getCapacitacion()
								.getFechaRecepDesde())
						+ "</b></p>"
						+ "<p align=\"justify\"><b>- Fecha Hasta: "
						+ sdfFecha.format(p.getCapacitacion()
								.getFechaRecepHasta())
						+ "</b></p>"
						+ "<p></p>"
						+ "<p align=\"justify\"><b>Identificaci&oacute;n del Curso</b></p>"
						+ "<p align=\"justify\"><b>- Fecha Inicio: "
						+ sdfFecha.format(p.getCapacitacion().getFechaInicio())
						+ "</b></p>"
						+ "<p align=\"justify\"><b>- Fecha Fin: "
						+ sdfFecha.format(p.getCapacitacion().getFechaFin())
						+ "</b></p>"
						+ "<p align=\"justify\"><b>- Sede: "
						+ p.getCapacitacion().getSede()
						+ "</b></p>"
						+ "<p align=\"justify\"><b>- Direcci&oacute;n: "
						+ p.getCapacitacion().getDireccion()
						+ "</b></p>"
						+ "<p align=\"justify\"><b>- Ciudad: "
						+ p.getCapacitacion().getCiudad().getDescripcion()
						+ "</b></p>"

						+ "<p></p>"
						+ "<p> Atentamente,</p> "
						+ "<b>"
						+ configuracionCab
						+ "</p></b>"

						+ "<p align=\"justify\"><b>Para mayor informaci&oacute;n comunicarse con: </b> "
						+ listaCorreos + "</p>";

				usuarioPortalFormController.enviarMails(dirEmail, texto,
						asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public boolean cerrado() {

		return habCerrado;
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public ConfiguracionUoCab getOee() {
		return oee;
	}

	public void setOee(ConfiguracionUoCab oee) {
		this.oee = oee;
	}

	public Date getFechaDesdeNva() {
		return fechaDesdeNva;
	}

	public void setFechaDesdeNva(Date fechaDesdeNva) {
		this.fechaDesdeNva = fechaDesdeNva;
	}

	public Date getFechaHastaNva() {
		return fechaHastaNva;
	}

	public void setFechaHastaNva(Date fechaHastaNva) {
		this.fechaHastaNva = fechaHastaNva;
	}
	
	public Date getFechaPubDesdeNva() {
		return fechaPubDesdeNva;
	}

	public void setFechaPubDesdeNva(Date fechaPubDesdeNva) {
		this.fechaPubDesdeNva = fechaPubDesdeNva;
	}

	public Date getFechaPubHastaNva() {
		return fechaPubHastaNva;
	}

	public void setFechaPubHastaNva(Date fechaPubHastaNva) {
		this.fechaPubHastaNva = fechaPubHastaNva;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getCodDepartamento() {
		return codDepartamento;
	}

	public void setCodDepartamento(Long codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public DestinadoA getDestinadoA() {
		return destinadoA;
	}

	public void setDestinadoA(DestinadoA destinadoA) {
		this.destinadoA = destinadoA;
	}

	public boolean isHabCerrado() {
		return habCerrado;
	}

	public void setHabCerrado(boolean habCerrado) {
		this.habCerrado = habCerrado;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilCerrado() {
		return nivelEntidadOeeUtilCerrado;
	}

	public void setNivelEntidadOeeUtilCerrado(
			NivelEntidadOeeUtil nivelEntidadOeeUtilCerrado) {
		this.nivelEntidadOeeUtilCerrado = nivelEntidadOeeUtilCerrado;
	}

	public Long getCupo() {
		return cupo;
	}

	public void setCupo(Long cupo) {
		this.cupo = cupo;
	}

	public List<CapacitacionCerrada> getListaCapacitacionCerradas() {
		return listaCapacitacionCerradas;
	}

	public void setListaCapacitacionCerradas(
			List<CapacitacionCerrada> listaCapacitacionCerradas) {
		this.listaCapacitacionCerradas = listaCapacitacionCerradas;
	}

	public boolean isEsEditCerrado() {
		return esEditCerrado;
	}

	public void setEsEditCerrado(boolean esEditCerrado) {
		this.esEditCerrado = esEditCerrado;
	}

	public CapacitacionCerrada getCerrada() {
		return cerrada;
	}

	public void setCerrada(CapacitacionCerrada cerrada) {
		this.cerrada = cerrada;
	}

	public Integer getIndexUpPart() {
		return indexUpPart;
	}

	public void setIndexUpPart(Integer indexUpPart) {
		this.indexUpPart = indexUpPart;
	}

	public List<CapacitacionCerrada> getListaCapacitacionCerradasEliminar() {
		return listaCapacitacionCerradasEliminar;
	}

	public void setListaCapacitacionCerradasEliminar(
			List<CapacitacionCerrada> listaCapacitacionCerradasEliminar) {
		this.listaCapacitacionCerradasEliminar = listaCapacitacionCerradasEliminar;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHoraAtencionDesde() {
		return horaAtencionDesde;
	}

	public void setHoraAtencionDesde(String horaAtencionDesde) {
		this.horaAtencionDesde = horaAtencionDesde;
	}

	public String getHoraAtencionHasta() {
		return horaAtencionHasta;
	}

	public void setHoraAtencionHasta(String horaAtencionHasta) {
		this.horaAtencionHasta = horaAtencionHasta;
	}

	public List<ConsultasCapacitacion> getListaConsultaCapacitacion() {
		return listaConsultaCapacitacion;
	}

	public void setListaConsultaCapacitacion(
			List<ConsultasCapacitacion> listaConsultaCapacitacion) {
		this.listaConsultaCapacitacion = listaConsultaCapacitacion;
	}

	public List<ConsultasCapacitacion> getListaConsultaCapacitacionEliminar() {
		return listaConsultaCapacitacionEliminar;
	}

	public void setListaConsultaCapacitacionEliminar(
			List<ConsultasCapacitacion> listaConsultaCapacitacionEliminar) {
		this.listaConsultaCapacitacionEliminar = listaConsultaCapacitacionEliminar;
	}

	public ConsultasCapacitacion getConsulta() {
		return consulta;
	}

	public void setConsulta(ConsultasCapacitacion consulta) {
		this.consulta = consulta;
	}

	public Integer getIndexUpConsulta() {
		return indexUpConsulta;
	}

	public void setIndexUpConsulta(Integer indexUpConsulta) {
		this.indexUpConsulta = indexUpConsulta;
	}

	public boolean isEsEditConsulta() {
		return esEditConsulta;
	}

	public void setEsEditConsulta(boolean esEditConsulta) {
		this.esEditConsulta = esEditConsulta;
	}

}
