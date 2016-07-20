package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import bsh.commands.dir;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.capacitacion.session.ConsultasCapacitacionHome;
import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConsultasCapacitacion;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PublicacionCapacitacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("publicarCapacitacionFC")
public class PublicarCapacitacionFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	ConsultasCapacitacionHome consultasCapacitacionHome;

	private List<SelectItem> departamentosSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ciudadSelecItem = new ArrayList<SelectItem>();
	private List<ConsultasCapacitacion> listaConsultaCapacitacion = new ArrayList<ConsultasCapacitacion>();

	private Capacitaciones capacitacion = new Capacitaciones();
	private ConsultasCapacitacion consulta = new ConsultasCapacitacion();

	private Long idCapacitacion;
	private Long idPais;
	private Long idCiudad = null;
	private Long codDepartamento = null;
	private Integer row;
	private Boolean isEdit = false;

	private String horaDesdePublicacion;
	private String horaHastaPublicacion;
	private String horaDesdeRecepcion;
	private String horaHastaRecepcion;
	private String lugar;
	private String direccion;
	private String telefono;
	private String email;
	private String horaAtencionDesde;
	private String horaAtencionHasta;
	private String msg;
	private String from;

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		if (capacitacion == null || capacitacion.getIdCapacitacion() == null) {
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);
			seguridadUtilFormController.validarCapacitacion(idCapacitacion,
					capacitacion.getEstado(),
					ActividadEnum.CAPA_PUBLICAR_CAPACITACION.getValor());
		}
		if (capacitacion.getCiudad() != null) {
			idCiudad = capacitacion.getCiudad().getIdCiudad();
			codDepartamento = capacitacion.getCiudad().getDepartamento()
					.getIdDepartamento();
			idPais = capacitacion.getCiudad().getDepartamento().getPais()
					.getIdPais();
			updateCiudad();
			updateDepartamento();

		} else {
			idPais = idParaguay();
			updateDepartamento();
		}

		cargarDatosNivel();

	}

	public void initEdit() {
		SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm:ss");

		capacitacion = em.find(Capacitaciones.class, idCapacitacion);
		if (capacitacion.getFechaPubDesde() != null) {

			horaDesdePublicacion = sdfHora.format(capacitacion
					.getFechaPubDesde());
			horaHastaPublicacion = sdfHora.format(capacitacion
					.getFechaPubHasta());
		}
		if (capacitacion.getFechaRecepDesde() != null) {
			horaDesdeRecepcion = sdfHora.format(capacitacion
					.getFechaRecepDesde());
			horaHastaRecepcion = sdfHora.format(capacitacion
					.getFechaRecepHasta());
		}
		buscarConsultasCapacitacion();
		if (capacitacion.getCiudad() != null) {
			idCiudad = capacitacion.getCiudad().getIdCiudad();
			codDepartamento = capacitacion.getCiudad().getDepartamento()
					.getIdDepartamento();
			idPais = capacitacion.getCiudad().getDepartamento().getPais()
					.getIdPais();
			updateCiudad();
			updateDepartamento();

		} else {
			idPais = idParaguay();
			updateDepartamento();
		}

		cargarDatosNivel();

	}

	private void buscarConsultasCapacitacion() {
		String sql = "SELECT C.* "
				+ "FROM CAPACITACION.CONSULTAS_CAPACITACION C "
				+ "WHERE C.ID_CAPACITACION = "
				+ capacitacion.getIdCapacitacion();
		listaConsultaCapacitacion = new ArrayList<ConsultasCapacitacion>();
		listaConsultaCapacitacion = em.createNativeQuery(sql,
				ConsultasCapacitacion.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitacion.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(capacitacion.getConfiguracionUoCab()
						.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(capacitacion
				.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(capacitacion.getConfiguracionUoCab()
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

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

	private Boolean validarFecha() {
		try {
			int[] horaDesde = getHora(horaAtencionDesde);
			if (horaDesde[0] > 23) {
				msg = "La hora no puede ser mayor a 23";
				return false;
			}
			if (horaDesde[1] > 59) {
				msg = "El minuto no puede ser mayor a 59";
				return false;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			sdf.setLenient(false);
			try {
				Date hd = sdf.parse(horaAtencionDesde);
				Date hh = sdf.parse(horaAtencionHasta);
				if (hd.getTime() >= hh.getTime()) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"La Hora de Atención Desde debe ser menor a la Hora Hasta");
					return false;
				}
			} catch (ParseException e) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Formato de Hora incorrecto. Debe introducir, Ej.: HH:mm (formato 24 Hs.)");
				return false;
			}
			Date fechaDesde = new Date();
			fechaDesde.setHours(horaDesde[0]);
			fechaDesde.setMinutes(horaDesde[1]);
			consulta.setHoraDesde(fechaDesde);
			int[] horaHasta = getHora(horaAtencionHasta);
			if (horaHasta[0] > 23) {
				msg = "La hora no puede ser mayor a 23";
				return false;
			}
			if (horaHasta[1] > 59) {
				msg = "El minuto no puede ser mayor a 59";
				return false;
			}
			Date fechaHasta = new Date();

			fechaHasta.setHours(horaHasta[0]);
			fechaHasta.setMinutes(horaHasta[1]);
			consulta.setHoraHasta(fechaHasta);
		} catch (Exception e) {
			msg = "Debe ingresar horas válidas";
			return false;
		}
		return true;
	}

	public void agregarListaConsulta() {
		consulta = new ConsultasCapacitacion();
		if (!check()) {
			msg = "Debe ingresar los datos obligatorios";
			return;
		}
		if (!validarFecha())
			return;
		msg = null;

		consulta.setActivo(true);
		consulta.setCapacitaciones(capacitacion);
		consulta.setDireccion(direccion);
		consulta.setLugar(lugar);

		if (email != null && !email.trim().isEmpty())
			consulta.setEmail(email);
		if (telefono != null && !telefono.trim().isEmpty())
			consulta.setTelefono(telefono);

		listaConsultaCapacitacion.add(consulta);
		limpiarCampos();
	}

	public void editar(Integer r) {
		row = r;
		consulta = new ConsultasCapacitacion();
		consulta = listaConsultaCapacitacion.get(row);
		direccion = consulta.getDireccion();
		lugar = consulta.getLugar();
		horaAtencionDesde = buscarHora(consulta.getHoraDesde().toString());
		horaAtencionHasta = buscarHora(consulta.getHoraHasta().toString());
		telefono = consulta.getTelefono();
		email = consulta.getEmail();
		isEdit = true;
	}

	public void agregarEditadoLista() {
		if (!check()) {
			msg = "Debe ingresar los datos obligatorios";
			return;
		}
		if (!validarFecha())
			return;
		msg = null;
		consulta.setDireccion(direccion);
		consulta.setLugar(lugar);
		if (email != null && !email.trim().isEmpty())
			consulta.setEmail(email);
		if (telefono != null && !telefono.trim().isEmpty())
			consulta.setTelefono(telefono);
		listaConsultaCapacitacion.set(row, consulta);
		limpiarCampos();
	}

	public void cancelarEditado() {
		limpiarCampos();
	}

	public void eliminar(Integer r) {
		consulta = new ConsultasCapacitacion();
		consulta = listaConsultaCapacitacion.get(r);
		listaConsultaCapacitacion.remove(consulta);

	}

	private void limpiarCampos() {
		direccion = null;
		lugar = null;
		horaAtencionDesde = null;
		horaAtencionHasta = null;
		telefono = null;
		email = null;
	}

	private Boolean checkCamposObligatorios() {
		if (capacitacion.getFechaPubDesde() == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha de Publicación Fecha Desde antes de realizar esta acción");
			return false;
		}
		if (capacitacion.getFechaPubHasta() == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha de Publicación Fecha Hasta antes de realizar esta acción");
			return false;
		}
		if (horaDesdePublicacion == null
				|| horaDesdePublicacion.trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha de Publicación Hora Desde antes de realizar esta acción");
			return false;
		}
		if (horaHastaPublicacion == null
				|| horaHastaPublicacion.trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha de Publicación Hora Hasta antes de realizar esta acción");
			return false;
		}
		if (capacitacion.getRecepcionPostulacion()) {
			if (capacitacion.getFechaRecepDesde() == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo Recepción de Postulación Fecha Desde antes de realizar esta acción");
				return false;
			}
			if (capacitacion.getFechaRecepHasta() == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo Recepción de Postulación Fecha Hasta antes de realizar esta acción");
				return false;
			}
			if (horaDesdeRecepcion == null
					|| horaDesdeRecepcion.trim().isEmpty()) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo Recepción de Postulación Hora Desde antes de realizar esta acción");
				return false;
			}
			if (horaHastaRecepcion == null
					|| horaHastaRecepcion.trim().isEmpty()) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo Recepción de Postulación Hora Hasta antes de realizar esta acción");
				return false;
			}
		}

		if (capacitacion.getTituloPublic() == null
				|| capacitacion.getTituloPublic().trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Título de Publicación antes de realizar esta acción");
			return false;
		}
		if (capacitacion.getSede() == null
				|| capacitacion.getSede().trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Sede antes de realizar esta acción");
			return false;
		}
		if (capacitacion.getDireccion() == null
				|| capacitacion.getDireccion().trim().isEmpty()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Dirección antes de realizar esta acción");
			return false;
		}
		if (idPais == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo País antes de realizar esta acción");
			return false;
		}
		if (codDepartamento == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Departamento antes de realizar esta acción");
			return false;
		}
		if (idCiudad == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Ciudad antes de realizar esta acción");
			return false;
		}
		if (capacitacion.getFechaInicio() == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha Inicio antes de realizar esta acción");
			return false;
		}
		if (capacitacion.getFechaFin() == null) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Fecha Fin antes de realizar esta acción");
			return false;
		}
		return true;
	}

	private Boolean checkCamposPublicacion() {
		if (capacitacion.getFechaPubDesde().after(
				capacitacion.getFechaPubHasta())) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"La Fecha de Publicación Desde no puede ser mayor a la Fecha Hasta");
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setLenient(false);
		try {
			Date hd = sdf.parse(horaDesdePublicacion);
			Date hh = sdf.parse(horaHastaPublicacion);
			if (hd.getTime() >= hh.getTime()) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"La Hora de Publicación Desde debe ser menor a la Hora Hasta");
				return false;
			}
		} catch (ParseException e) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Formato de Hora incorrecto. Debe introducir, Ej.: HH:mm (formato 24 Hs.)");
			return false;
		}
		return true;
	}

	private Boolean checkCamposRecepcion() {
		if (capacitacion.getFechaRecepDesde().after(
				capacitacion.getFechaRecepHasta())) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"La Fecha de Recepción Desde no puede ser mayor a la Fecha Hasta");
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setLenient(false);
		try {
			Date hd = sdf.parse(horaDesdeRecepcion);
			Date hh = sdf.parse(horaHastaRecepcion);
			if (hd.getTime() >= hh.getTime()) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"La Hora de Recepción Desde debe ser menor a la Hora Hasta");
				return false;
			}
		} catch (ParseException e) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Formato de Hora incorrecto. Debe introducir, Ej.: HH:mm (formato 24 Hs.)");
			return false;
		}
		return true;
	}

	public String guardarPublicar() {
		/* Realiza las validaciones citadas en el etc */
		if (!checkCamposObligatorios())
			return null;
		if (!checkCamposPublicacion())
			return null;
		if (capacitacion.getRecepcionPostulacion()) {
			if (!checkCamposRecepcion())
				return null;
		}
		if (capacitacion.getFechaInicio().after(capacitacion.getFechaFin())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"La Fecha de Inicio no puede ser mayor a la Fecha Fin");
			return null;
		}
		if (listaConsultaCapacitacion == null
				|| listaConsultaCapacitacion.size() == 0) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe cargar al menos un registro en Lugares de Consultas y Aclaraciones");
			return null;
		}

		/* Actualiza la tabla capacitaciones */
		setearFechasHoras();
		capacitacion.setCiudad(em.find(Ciudad.class, idCiudad));
		capacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		capacitacion.setFechaMod(new Date());
		capacitacion.setEstadoPublic("A VERIFICAR");
		Referencias referencias = new Referencias();
		if (capacitacion.getRecepcionPostulacion())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CAPACITACION", "RECEPCIONAR POSTULANTES");

		else
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CAPACITACION", "FINALIZADO CIRCUITO");
		capacitacion.setEstado(referencias.getValorNum());
		try {
			em.merge(capacitacion);
			/* Inserta en la tabla consultas_capacitacion */
			for (ConsultasCapacitacion o : listaConsultaCapacitacion) {
				o.setFechaAlta(new Date());
				o.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(o);
			}

			/**
			 * Inserta en la tabla publicacion_capacitacion
			 * 
			 * */
			Referencias refTitulo = new Referencias();
			refTitulo = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "TITULO");
			Referencias refDestinatarios = new Referencias();
			refDestinatarios = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "DESTINATARIOS");
			Referencias refModalidad = new Referencias();
			refModalidad = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "MODALIDAD");
			Referencias refPostulaciones = new Referencias();
			refPostulaciones = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "POSTULACIONES");
			Referencias refConsultas = new Referencias();
			refConsultas = referenciasUtilFormController.getReferencia(
					"PUBLIC_CAPACITACION", "CONSULTAS");
			Referencias refProspecto = new Referencias();
		
			/* Inserta el titulo */
			PublicacionCapacitacion publicacionTitulo = new PublicacionCapacitacion();
			publicacionTitulo.setActivo(true);
			publicacionTitulo.setCapacitaciones(capacitacion);
			publicacionTitulo.setFechaAlta(new Date());
			publicacionTitulo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			publicacionTitulo.setBloque("TITULO");
			publicacionTitulo.setOrden(refTitulo.getValorNum());
			publicacionTitulo.setTexto(generarTextoTitulo());
			em.persist(publicacionTitulo);

			/* Inserta los destinatarios */
			PublicacionCapacitacion publicacionDestinatarios = new PublicacionCapacitacion();
			publicacionDestinatarios.setActivo(true);
			publicacionDestinatarios.setCapacitaciones(capacitacion);
			publicacionDestinatarios.setFechaAlta(new Date());
			publicacionDestinatarios.setUsuAlta(usuarioLogueado
					.getCodigoUsuario());
			publicacionDestinatarios.setBloque("DESTINATARIOS");
			publicacionDestinatarios.setOrden(refDestinatarios.getValorNum());
			publicacionDestinatarios.setTexto(generarTextoDestinatarios());
			em.persist(publicacionDestinatarios);

			/* Inserta los modalidad */
			PublicacionCapacitacion publicacionModalidad = new PublicacionCapacitacion();
			publicacionModalidad.setActivo(true);
			publicacionModalidad.setCapacitaciones(capacitacion);
			publicacionModalidad.setFechaAlta(new Date());
			publicacionModalidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			publicacionModalidad.setBloque("MODALIDAD");
			publicacionModalidad.setOrden(refModalidad.getValorNum());
			publicacionModalidad.setTexto(generarTextoModalidad());
			em.persist(publicacionModalidad);
			if (capacitacion.getRecepcionPostulacion()) {
				/* Inserta las postulaciones */
				PublicacionCapacitacion publicacionPostulaciones = new PublicacionCapacitacion();
				publicacionPostulaciones.setActivo(true);
				publicacionPostulaciones.setCapacitaciones(capacitacion);
				publicacionPostulaciones.setFechaAlta(new Date());
				publicacionPostulaciones.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				publicacionPostulaciones.setBloque("POSTULACION");
				publicacionPostulaciones.setOrden(refPostulaciones
						.getValorNum());
				publicacionPostulaciones.setTexto(generarTextoPostulacion());
				em.persist(publicacionPostulaciones);
			}
			/* Inserta las consultas */
			PublicacionCapacitacion publicacionConsultas = new PublicacionCapacitacion();
			publicacionConsultas.setActivo(true);
			publicacionConsultas.setCapacitaciones(capacitacion);
			publicacionConsultas.setFechaAlta(new Date());
			publicacionConsultas.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			publicacionConsultas.setBloque("CONSULTAS");
			publicacionConsultas.setOrden(refConsultas.getValorNum());
			publicacionConsultas.setTexto(generarTextoConsultas());
			em.persist(publicacionConsultas);

			/* Inserta las prospecto 
			PublicacionCapacitacion publicacionProspecto = new PublicacionCapacitacion();
			publicacionProspecto.setActivo(true);
			publicacionProspecto.setCapacitaciones(capacitacion);
			publicacionProspecto.setFechaAlta(new Date());
			publicacionProspecto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			publicacionProspecto.setBloque("PROSPECTO");
			publicacionProspecto.setOrden(refProspecto.getValorNum());
			publicacionProspecto.setTexto(generarTextoProspecto());
			em.persist(publicacionProspecto);
*/
		} catch (Exception e) {
			return null;
		}

		if (capacitacion.getRecepcionPostulacion()) {
			/* Pasa a la sgte tarea */

			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CAPA_PUBLICAR_CAPACITACION);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.CAPA_RECEPCIONAR_POST_INSC);
			jbpmUtilFormController.setTransition("pubCap_TO_recPosIns");
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitacion);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		} else {
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CAPA_PUBLICAR_CAPACITACION);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setCapacitacion(capacitacion);
			jbpmUtilFormController.setTransition("pubCap_TO_end");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		}
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "ok";

	}

	private String destinadoA() {
		if (capacitacion.getDestinadoA().equals("C"))
			return "Cerrado. ";
		if (capacitacion.getDestinadoA().equals("A"))
			return "Abierto a toda la ciudadanía. ";
		if (capacitacion.getDestinadoA().equals("F"))
			return "Abierto a todos los Funcionarios Públicos. ";
		return "";
	}

	private String entidades() {
		if (capacitacion.getDestinadoA().equals("C")
				|| capacitacion.getDestinadoA().equals("A")) {
			String hql = "SELECT c.* FROM capacitacion.capacitacion_cerrada c "
					+ "WHERE c.id_capacitacion = "
					+ capacitacion.getIdCapacitacion();
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

	private String modalidad() {
		if (capacitacion.getModalidad().equals("P"))
			return "Presencial. ";
		if (capacitacion.getModalidad().equals("S"))
			return "Semi - presencial. ";
		if (capacitacion.getModalidad().equals("V"))
			return "Virtual. ";
		return "";
	}

	private String informacion() {
		String hql = "SELECT d.* FROM seleccion.datos_especificos d "
				+ "join seleccion.datos_generales g "
				+ "on g.id_datos_generales = d.id_datos_generales "
				+ "WHERE d.activo = TRUE and g.activo = TRUE and g.descripcion = 'POSTULACION CAPACITACION'";
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(hql, DatosEspecificos.class)
				.getResultList();
		if (lista.size() > 0)
			return lista.get(0).getDescripcion();
		return "";

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

	private String generarTextoTitulo() {
		StringBuffer texto = new StringBuffer();
		String h2O = "<h2>";
		String h2C = "</h2>";
		String br = "<br/>";
		texto.append(h2O + capacitacion.getTituloPublic() + h2C + br);
		texto.append(h2O + capacitacion.getNombre() + h2C + br);
		return texto.toString();
	}

	private String generarTextoDestinatarios() {
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

	private String generarTextoModalidad() {
		StringBuffer texto = new StringBuffer();
		String h3O = "<h3>";
		String h3C = "</h3>";
		String pO = "<p>";
		String pC = "</p>";
		String br = "<br/>";
		SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");

		texto.append(h3O + "MODALIDAD Y DURACIÓN: " + h3C + br);
		texto.append(pO + "Modalidad " + modalidad()
				+ "La carga horaria total es de "
				+ capacitacion.getCargaHoraria()
				+ " horas. La fecha de Inicio programada es: "
				+ sdfFecha.format(capacitacion.getFechaInicio())
				+ " y la fecha Finalización es "
				+ sdfFecha.format(capacitacion.getFechaFin()) + pC + br);
		return texto.toString();
	}

	private String generarTextoPostulacion() {
		StringBuffer texto = new StringBuffer();
		String h3O = "<h3>";
		String h3C = "</h3>";
		String pO = "<p>";
		String pC = "</p>";
		String br = "<br/>";

		if (capacitacion.getRecepcionPostulacion()) {
			texto.append(h3O + "POSTULACIONES E INFORMACIONES ONLINE: " + h3C
					+ br);
			texto.append(pO + informacion() + pC + br);
		}
		return texto.toString();
	}

	private String generarTextoConsultas() {
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

	private String generarTextoProspecto() {
		StringBuffer texto = new StringBuffer();
		String h3O = "<h3>";
		String h3C = "</h3>";
		String pO = "<p>";
		String pC = "</p>";
		String br = "<br/>";
		String separador = File.separator;

		texto.append(pO + h3O + "PROSPECTO DEL CURSO: " + h3C + "<a href ="
				+ capacitacion.getDocumentos().getUbicacionFisica() + separador
				+ capacitacion.getDocumentos().getNombreFisicoDoc()
				+ ">Clic aqui</a>" + pC + br);

		// texto.append("<script language=\"Javascript\" type=\"text/javascript\"> function dpf(f) {var adp = f.adp;if (adp != null) {for (var i = 0;i < adp.length;i++) {f.removeChild(adp[i]);}}};function apf(f, pvp) {var adp = new Array();f.adp = adp;var i = 0;for (k in pvp) {var p = document.createElement(\"input\");p.type = \"hidden\";p.name = k;p.value = pvp[k];f.appendChild(p);adp[i++] = p;}};function jsfcljs(f, pvp, t) {apf(f, pvp);var ft = f.target;if (t) {f.target = t;}f.submit();f.target = ft;dpf(f);};</script>");

		// Esto se hace si es que la publicacion tiene reportes
		// texto.append(spanO
		// +
		// "<a id=\"idFormuPubli:print1\" href=\"#\" onclick=\"if(typeof jsfcljs == 'function'){jsfcljs(document.getElementById('idFormuPubli'),{'idFormuPubli:print1':'idFormuPubli:print1'},'');}return false\" > Lista Larga de Admitidos");
		// Los parametros del reporte
		/*
		 * texto.append("<input type=\"hidden\" value=\"" +
		 * listaPublicada.getConcursoPuestoAgr() .getIdConcursoPuestoAgr() +
		 * "\" name=\"idConcursoGrupoPuestoAgr\"/>"); texto.append(
		 * "<input type=\"hidden\" value=\"CU_86\" name=\"imprimirCU\"/>");
		 * texto.append("</a>" + spanC + br);
		 * 
		 * texto.append(spanO + "OBS.: " + spanC +
		 * listaPublicada.getObservacion());
		 */
		return texto.toString();
	}

	private void setearFechasHoras() {

		// Fecha Publicacion Desde
		int[] horaDesde = getHora(horaDesdePublicacion);
		Calendar fechaPublicacionDesde = Calendar.getInstance();
		capacitacion.getFechaPubDesde().setHours(horaDesde[0]);
		capacitacion.getFechaPubDesde().setMinutes(horaDesde[1]);
		fechaPublicacionDesde.setTime(capacitacion.getFechaPubDesde());
		capacitacion.setFechaPubDesde(fechaPublicacionDesde.getTime());

		// Fecha de Publicacion Hasta
		int[] horaHasta = getHora(horaHastaPublicacion);
		Calendar fechaPublicacionHasta = Calendar.getInstance();
		capacitacion.getFechaPubHasta().setHours(horaHasta[0]);
		capacitacion.getFechaPubHasta().setMinutes(horaHasta[1]);
		fechaPublicacionHasta.setTime(capacitacion.getFechaPubHasta());
		capacitacion.setFechaPubHasta(fechaPublicacionHasta.getTime());

		if (capacitacion.getRecepcionPostulacion()) {
			// Fecha Recepcion Desde
			int[] horaDesd = getHora(horaDesdeRecepcion);
			Calendar fechaRecepcionDesde = Calendar.getInstance();
			capacitacion.getFechaRecepDesde().setHours(horaDesd[0]);
			capacitacion.getFechaRecepDesde().setMinutes(horaDesd[1]);
			fechaRecepcionDesde.setTime(capacitacion.getFechaRecepDesde());
			capacitacion.setFechaRecepDesde(fechaRecepcionDesde.getTime());

			// Fecha de Recepcion Hasta
			int[] horaHast = getHora(horaHastaRecepcion);
			Calendar fechaRecepcionHasta = Calendar.getInstance();
			capacitacion.getFechaRecepHasta().setHours(horaHast[0]);
			capacitacion.getFechaRecepHasta().setMinutes(horaHast[1]);
			fechaRecepcionHasta.setTime(capacitacion.getFechaRecepHasta());
			capacitacion.setFechaRecepHasta(fechaRecepcionHasta.getTime());
		}
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

	public String buscarHora(String cod) {

		String[] arrayCod = cod.split("\\ ");
		String[] arrayCodigo;
		if (arrayCod.length > 1)
			arrayCodigo = arrayCod[3].split(":");
		else
			arrayCodigo = arrayCod[0].split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public String getHoraDesdePublicacion() {
		return horaDesdePublicacion;
	}

	public void setHoraDesdePublicacion(String horaDesdePublicacion) {
		this.horaDesdePublicacion = horaDesdePublicacion;
	}

	public String getHoraHastaPublicacion() {
		return horaHastaPublicacion;
	}

	public void setHoraHastaPublicacion(String horaHastaPublicacion) {
		this.horaHastaPublicacion = horaHastaPublicacion;
	}

	public String getHoraDesdeRecepcion() {
		return horaDesdeRecepcion;
	}

	public void setHoraDesdeRecepcion(String horaDesdeRecepcion) {
		this.horaDesdeRecepcion = horaDesdeRecepcion;
	}

	public String getHoraHastaRecepcion() {
		return horaHastaRecepcion;
	}

	public void setHoraHastaRecepcion(String horaHastaRecepcion) {
		this.horaHastaRecepcion = horaHastaRecepcion;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ConsultasCapacitacion getConsulta() {
		return consulta;
	}

	public void setConsulta(ConsultasCapacitacion consulta) {
		this.consulta = consulta;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	

}
