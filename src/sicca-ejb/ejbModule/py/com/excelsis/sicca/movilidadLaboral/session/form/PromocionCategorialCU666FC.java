package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("promocionCategorialCU666FC")
public class PromocionCategorialCU666FC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 890520858127796720L;
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
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(create = true, required = false)
	JasperReportUtils jasperReportUtils;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	BuscadorDocsFC buscadorDocsFC;
	@In(create = true)
	SinarhUtiles sinarhUtiles;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true, required = false)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Long idPaisExp = null;
	private Long idPersona = null;
	private Long idEmpleadoPuesto;
	private Long idFuncionario;
	private Long idPlantaCargoDet;
	private Long idPais;
	private Integer monto;
	private boolean habUpdate = false;
	private boolean habPaisExp = true;
	private boolean habBtnAddPersons = false;
	private Boolean primeraEntrada = true;
	private String docIdentidad = null;
	private String observacion;
	private String nombreNivelEntidad;
	private BigDecimal codNivelEntidad;
	private Date fechaInicio;
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private EmpleadoPuesto empleadoPuesto;
	private EmpleadoPuesto funcionario = new EmpleadoPuesto();
	private Persona persona;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private List<ConfiguracionUoDet> configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList = new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos = new ArrayList<EmpleadoConceptoPago>();
	private List<Documentos> listaDocumentos = new ArrayList<Documentos>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosFuncionario = new ArrayList<EmpleadoConceptoPago>();

	public void init() throws Exception {

		if (idPersona != null) {
			if (!seguridadUtilFormController.validarInput(idPersona.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}

		}
		idPaisExp = General.getIdParaguay();
		cargarNiveentidadOee();
		if (primeraEntrada) {
			empleadoPuesto = new EmpleadoPuesto();
			primeraEntrada = false;
			if (idEmpleadoPuesto != null) {
				empleadoPuesto = em
						.find(EmpleadoPuesto.class, idEmpleadoPuesto);
				obtenerDatos();
			} else {
				setearDatos();
			}
		}
		if (idFuncionario != null) {
			funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
			if (funcionario.getPlantaCargoDet().getContratado() != null
					&& funcionario.getPlantaCargoDet().getContratado()) {
				funcionario = new EmpleadoPuesto();
				statusMessages.add(Severity.ERROR,
						"Escoja un Funcionario Permanente");
				return;
			}
			seleccionUtilFormController.setPlantaCargoDet(funcionario
					.getPlantaCargoDet());
			seleccionUtilFormController.setCodigoSinarh(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getCodigoSinarh());
			Long idSinNivelEntidad = getIdSinNivelEntidad(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getNenCodigo());
			SinNivelEntidad nivelEntidad = em.find(SinNivelEntidad.class,
					idSinNivelEntidad);
			nombreNivelEntidad = nivelEntidad.getNenNombre();
			codNivelEntidad = nivelEntidad.getNenCodigo();
		}
	}

	public void initView() throws Exception {

		if (idEmpleadoPuesto != null) {
			if (!seguridadUtilFormController.validarInput(
					idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(),
					null)) {
				return;
			}
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			observacion = empleadoPuesto.getObservacion();
			persona = empleadoPuesto.getPersona();
			// completarDatosPersona(persona, true);
			habBtnAddPersons = false;
		}
		idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		cargarNiveentidadOee();
		obtenerDatosView();

		fechaInicio = empleadoPuesto.getFechaInicio();
	}

	private void buscarConceptoPagoFuncionario() {
		String sql = "SELECT pago.* "
				+ "FROM movilidad.empleado_movilidad_cab cab "
				+ "INNER JOIN general.empleado_puesto emp ON (cab.id_empleado_puesto_anterior = emp.id_empleado_puesto) "
				+ "INNER JOIN general.empleado_concepto_pago pago ON (emp.id_empleado_puesto = pago.id_empleado_puesto) "
				+ "INNER JOIN general.persona persona ON (emp.id_persona = persona.id_persona) "
				+ "WHERE cab.id_empleado_puesto_anterior = emp.id_empleado_puesto AND  "
				+ "cab.activo = TRUE  " + "AND persona.id_persona = "
				+ persona.getIdPersona();
		empleadoConceptoPagosFuncionario = new ArrayList<EmpleadoConceptoPago>();
		empleadoConceptoPagosFuncionario = em.createNativeQuery(sql,
				EmpleadoConceptoPago.class).getResultList();
	}

	private void obtenerDatosView() {
		em.clear();
		persona = em.find(Persona.class, empleadoPuesto.getPersona()
				.getIdPersona());
		// completarDatosPersona(persona, true);
		List<ReferenciaAdjuntos> r = em.createQuery(
				"Select r from ReferenciaAdjuntos r "
						+ " where r.idRegistroTabla=" + idEmpleadoPuesto)
				.getResultList();
		listaDocumentos = new ArrayList<Documentos>();
		for (ReferenciaAdjuntos ad : r)
			listaDocumentos.add(ad.getDocumentos());

		List<EmpleadoMovilidadCab> mov = em.createQuery(
				"Select r from EmpleadoMovilidadCab r "
						+ " where r.empleadoPuestoNuevo.idEmpleadoPuesto="
						+ idEmpleadoPuesto).getResultList();
		if (!mov.isEmpty()) {
			funcionario = mov.get(0).getEmpleadoPuestoAnterior();
			persona = funcionario.getPersona();
			Long idSinNivelEntidad = getIdSinNivelEntidad(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getNenCodigo());
			SinNivelEntidad nivelEntidad = em.find(SinNivelEntidad.class,
					idSinNivelEntidad);
			nombreNivelEntidad = nivelEntidad.getNenNombre();
			codNivelEntidad = nivelEntidad.getNenCodigo();
			buscarConceptoPagoFuncionario();

		}

		plantaCargoDetView = em.find(PlantaCargoDet.class, empleadoPuesto
				.getPlantaCargoDet().getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet = em.find(ConfiguracionUoDet.class,
				plantaCargoDetView.getConfiguracionUoDet()
						.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
		empleadoConceptoPagos = em
				.createQuery(
						"Select e from EmpleadoConceptoPago e "
								+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
				.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();
	}

	public Long getIdSinNivelEntidad(BigDecimal codNivelEntidad) {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		Query q = em
				.createQuery("select sinNivelEntidad from SinNivelEntidad sinNivelEntidad "
						+ "WHERE sinNivelEntidad.nenCodigo ="
						+ codNivelEntidad
						+ " and sinNivelEntidad.activo is true "
						+ "order by sinNivelEntidad.aniAniopre DESC");
		List<SinNivelEntidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdSinNivelEntidad();
		}
		return null;
	}

	public void descargarDocBD(Integer row) throws FileNotFoundException,
			IOException {
		Documentos doc = listaDocumentos.get(row);
		if (doc.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(
					doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList = em
				.createQuery(
						"Select det from ConfiguracionUoDet det "
								+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE ")
				.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab())
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private void obtenerDatos() {

		List<ReferenciaAdjuntos> r = em.createQuery(
				"Select r from ReferenciaAdjuntos r "
						+ " where r.idRegistroTabla=" + idEmpleadoPuesto)
				.getResultList();
		if (!r.isEmpty()) {
			buscadorDocsFC.setDocDecreto(r.get(0).getDocumentos());
			buscadorDocsFC.cargarDatosDocumento();
		}
		plantaCargoDetView = em.find(PlantaCargoDet.class, empleadoPuesto
				.getPlantaCargoDet().getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet = em.find(ConfiguracionUoDet.class,
				plantaCargoDetView.getConfiguracionUoDet()
						.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
		empleadoConceptoPagos = em
				.createQuery(
						"Select e from EmpleadoConceptoPago e "
								+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
				.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();
	}

	private void setearDatos() {

		idPaisExp = General.getIdParaguay();
		habPaisExp = true;
		plantaCargoDetList = new ArrayList<PlantaCargoDet>();
		funcionario = new EmpleadoPuesto();
		idFuncionario = null;
	}

	public List<SelectItem> updateTipoDocSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoDocumento())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoDocumento() {
		try {
			List<DatosEspecificos> datosEspecificosLists = em
					.createQuery(
							"Select d from DatosEspecificos d "
									+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.descripcion like 'RESOLUCION' and d.valorAlf like 'RES' and d.activo=true order by d.descripcion")
					.getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public String getUrlToPageSearchFuncionario() {

		return "/busquedas/funcionarios/EmpleadoPuestoList.xhtml?from=movilidadLaboral/promocionCategorial/PromocionCategorialEditCU666";
	}

	public void agregar() {
		if (seleccionUtilFormController.getCodigoObj() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Cod. Objeto Gasto");
			return;
		}

		if (seleccionUtilFormController.getCodigoObj() != 111) {
			statusMessages.add(Severity.ERROR,
					"Solo permite Objeto de Gasto 111");
			return;
		}
		if (seleccionUtilFormController.getCodigoCategoria() == null
				|| seleccionUtilFormController.getCodigoCategoria().trim()
						.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar la Categoria");
			return;
		}
		if (seleccionUtilFormController.getMonto() == null) {
			statusMessages.add(Severity.ERROR, "El campo Monto es obligatorio");
			return;
		}

		if (seleccionUtilFormController.getMonto().intValue() < 0) {
			statusMessages
					.add(Severity.ERROR, "El Monto debe ser mayor a cero");
			return;
		}
		EmpleadoConceptoPago empleadoConceptoPago = new EmpleadoConceptoPago();
		empleadoConceptoPago.setObjCodigo(seleccionUtilFormController
				.getCodigoObj());
		empleadoConceptoPago.setCategoria(seleccionUtilFormController
				.getCodigoCategoria());
		empleadoConceptoPago.setMonto(seleccionUtilFormController.getMonto());
		empleadoConceptoPagos.add(empleadoConceptoPago);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

		setearConPago();
	}

	private void setearConPago() {
		seleccionUtilFormController.setearValoresObjetosGasto();
	}

	public void eliminar(int index) {
		empleadoConceptoPagos.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	private List<PuestoConceptoPago> buscarPuestoConceptoPago(
			PlantaCargoDet puesto) {

		String query = "select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  ";
		Query q = em.createQuery(query);
		q.setParameter("id", puesto.getIdPlantaCargoDet());

		return q.getResultList();
	}

	private List<Referencias> obtenerCorreos() {
		String query = "select ref from Referencias ref "
				+ " where ref.activo is true "
				+ " and ref.dominio = 'EMAIL_COPIA_SFP' and ref.descAbrev = 'ALERTA_MOVILIDAD'";
		Query q = em.createQuery(query);
		return q.getResultList();
	}

	private String textoCorreo() {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String texto = "<p align=\"justify\"> <b> Se ha realizado un registro de promoci&oacute;n categorial,  "
				+ "</p> "
				+ "</b>"
				+ "<p align=\"justify\">OEE "
				+ funcionario.getPlantaCargoDet().getConfiguracionUoDet()
						.getConfiguracionUoCab().getDenominacionUnidad()
				+ " del funcionario "
				+ funcionario.getPersona().getNombres()
				+ " "
				+ funcionario.getPersona().getApellidos()
				+ " con fecha de inicio "
				+ sdf.format(fechaInicio)
				+ " del Puesto "
				+ funcionario.getPlantaCargoDet().getDescripcion()
				+ "</p> <p> SICCA</p> ";

		return texto;
	}

	public void enviarEmails() {
		List<Referencias> listaCorreos = obtenerCorreos();
		if (!listaCorreos.isEmpty()) {
			String asunto = null;
			asunto = " NOTIF_PROMOCION_CATEGORIAL ";
			String texto = textoCorreo();
			for (Referencias correo : listaCorreos) {
				try {
					usuarioPortalFormController.enviarMails(
							correo.getDescLarga(), texto, asunto, null);
					System.out.println("EL MAIL HA SIDO ENVIADO....");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Boolean chequeaObjeto111(PlantaCargoDet pcdFuncionario) {

		Integer montoAnterior = 0;
		Integer valorRef = referenciasUtilFormController.getReferencia(
				"MONTO_PROMOCION", "MONTO_PROMOCION").getValorNum();
		List<PuestoConceptoPago> lista = buscarPuestoConceptoPago(pcdFuncionario);
		if (!lista.isEmpty()) {
			for (PuestoConceptoPago pcp : lista) {
				if (pcp.getObjCodigo() == 111)
					montoAnterior = pcp.getMonto();
			}
		}
		montoAnterior = montoAnterior + valorRef;

		for (EmpleadoConceptoPago conceptoPago : empleadoConceptoPagos) {
			if (conceptoPago.getObjCodigo() == 111) {
				if (montoAnterior.intValue() > conceptoPago.getMonto()
						.intValue()) {
					statusMessages
							.add(Severity.ERROR,
									"No cumple con monto m�nimo de categor�a superior. Debe registrarse a trav�s de una 'Promoci�n sin concurso'");
					return false;
				}
			}
		}

		return true;

	}

	public String save() {
		try {
			if (!chkDatos())
				return null;

			PlantaCargoDet plantaCargoDetFuncionario = em.find(
					PlantaCargoDet.class, funcionario.getPlantaCargoDet()
							.getIdPlantaCargoDet());
			if (!chequeaObjeto111(plantaCargoDetFuncionario))
				return null;
			/**
			 * Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
			 * */

			funcionario.setActual(false);
			funcionario.setFechaFin(new Date());
			funcionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
			funcionario.setFechaMod(new Date());
			em.merge(funcionario);

			/**
			 * Se repone las categorias utilizadas
			 */
			List<PuestoConceptoPago> lista = buscarPuestoConceptoPago(plantaCargoDetFuncionario);
			if (!lista.isEmpty()) {
				for (PuestoConceptoPago pcp : lista) {
					if (pcp.getObjCodigo() == 111) {
						actualizarDisponibles(pcp.getCategoria(),
								pcp.getObjCodigo());
						pcp.setActivo(false);
						em.merge(pcp);
					}
				}

			}

			/**
			 * Guardan en la tabla EMPLEADO_PUESTO
			 * */

			empleadoPuesto.setFechaInicio(fechaInicio);
			empleadoPuesto.setActual(true);
			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
			empleadoPuesto.setContratado(false);
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspEstado(datosEspecificosHome
							.getDatosPermanenteEstadoEmpleado());
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(searchDatosEsp());
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoRegistro(searchTipoRegistro());
			empleadoPuesto.setPlantaCargoDet(plantaCargoDetFuncionario);
			empleadoPuesto.setPersona(funcionario.getPersona());
			empleadoPuesto.setIncidenAntiguedad(true);
			if (observacion != null && !observacion.trim().isEmpty())
				empleadoPuesto.setObservacion(observacion);

			em.persist(empleadoPuesto);

			/**
			 * Se actualiza empleado puesto
			 */
			List<PuestoConceptoPago> listaP = buscarPuestoConceptoPago(plantaCargoDetFuncionario);
			if (!listaP.isEmpty()) {
				for (PuestoConceptoPago pcp : listaP) {
					if (pcp.getObjCodigo() != 111) {
						EmpleadoConceptoPago emp = new EmpleadoConceptoPago();
						emp.setCategoria(pcp.getCategoria());
						emp.setMonto(pcp.getMonto());
						emp.setObjCodigo(pcp.getObjCodigo());
						emp.setEmpleadoPuesto(empleadoPuesto);
						emp.setFechaAlta(new Date());
						emp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						emp.setAnho(Integer.parseInt(sdfAnio
								.format(fechaInicio)));

						em.persist(emp);

					}
				}

			}

			/**
			 * Se insertan datos de categor�as/remuneraciones:
			 * */
			for (EmpleadoConceptoPago conceptoPago : empleadoConceptoPagos) {
				EmpleadoConceptoPago emp = new EmpleadoConceptoPago();
				emp.setCategoria(conceptoPago.getCategoria());
				emp.setMonto(conceptoPago.getMonto());
				emp.setObjCodigo(conceptoPago.getObjCodigo());
				emp.setEmpleadoPuesto(empleadoPuesto);
				emp.setFechaAlta(new Date());
				emp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				emp.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));

				em.persist(emp);

				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 * */
				if (seleccionUtilFormController.getCategoria() != null
						&& seleccionUtilFormController.getSinAnx() != null) {
					SinAnx anx = em.find(SinAnx.class,
							seleccionUtilFormController.getSinAnx().getIdAnx());
					anx.setCantDisponible(anx.getCantDisponible() - 1);
					em.merge(anx);
				}
			}

			/**
			 * Registra la movilidad en la tabla EMPLEADO_MOVILIDAD_CAB
			 */
			EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
			empleadoMovilidadCab.setActivo(true);
			empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);
			empleadoMovilidadCab.setEmpleadoPuestoNuevo(empleadoPuesto);
			empleadoMovilidadCab.setMovilidadSicca(true);
			empleadoMovilidadCab.setFechaInicio(fechaInicio);
			empleadoMovilidadCab.setFechaAlta(new Date());
			empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			if (observacion != null && !observacion.trim().isEmpty())
				empleadoMovilidadCab.setObservacion(observacion);
			em.persist(empleadoMovilidadCab);
			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en bot�n Buscar = ok
			 * Insertar registro en Documentos y el archivo pdf
			 * 
			 * *
			 */
			Long idDoc = null;
			if (buscadorDocsFC.getFileActoAdmin() != null) {
				if (buscadorDocsFC.getDocDecreto() == null) {
					idDoc = guardarAdjuntos(buscadorDocsFC.getfName(),
							buscadorDocsFC.getFileActoAdmin().getFileSize(),
							buscadorDocsFC.getFileActoAdmin().getContentType(),
							buscadorDocsFC.getFileActoAdmin(),
							buscadorDocsFC.getIdTipoDoc(), null);
					if (idDoc == null)
						return null;
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setNroDocumento(buscadorDocsFC.getNroDoc());
					doc.setAnhoDocumento(Integer.parseInt(sdfAnio
							.format(buscadorDocsFC.getFechaDoc())));
					doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
					doc.setConfiguracionUoCab(usuarioLogueado
							.getConfiguracionUoCab());
					em.merge(doc);
				}
			}

			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */
			if (idDoc == null)
				idDoc = buscadorDocsFC.getDocDecreto().getIdDocumento();
			insertarAdjunto(idDoc);

			em.flush();
			enviarEmails();
			setearDatos();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "promocionCategorial_edit";// cambiar
			String direccionFisica = "";
			String sf = File.separator;

			direccionFisica = sf + "SICCA" + sf + anio + sf + "OEE" + sf
					+ nivelEntidadOeeUtil.getIdConfigCab() + sf + "MOVILIDAD";

			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(file,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto
	 * administrativo
	 * */
	private void insertarAdjunto(Long idDocumento) {
		ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
		referenciaAdjuntos.setDocumentos(new Documentos());
		referenciaAdjuntos.getDocumentos().setIdDocumento(idDocumento);
		referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto
				.getIdEmpleadoPuesto());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}

	private DatosEspecificos searchDatosEsp() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'PROMOCION CATEGORIAL' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();

	}

	private DatosEspecificos searchTipoRegistro() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'MOVILIDAD' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'TIPOS DE REGISTRO INGRESOS Y MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();

	}

	private void actualizarDisponibles(String categoria, Integer codigo) {

		List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(null,
				new Integer(50), codigo, categoria, null);
		if (!listaAnx.isEmpty()) {
			SinAnx anx = new SinAnx();
			anx = listaAnx.get(0);
			anx.setCantDisponible(anx.getCantDisponible() + 1);
			em.merge(anx);
		}

	}

	private boolean chkDatos() {
		if (buscadorDocsFC.getNroDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Numero de Documento");
			return false;
		}
		if (buscadorDocsFC.getFechaDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (buscadorDocsFC.getIdTipoDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (buscadorDocsFC.getDocDecreto() == null
				&& buscadorDocsFC.getFileActoAdmin() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar un archivo, verifique");
			return false;
		}
		if (fechaInicio == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe Ingresar datos en el campo obligatorio Fecha de Inicio");
			return false;
		}

		if (funcionario == null || funcionario.getIdEmpleadoPuesto() == null) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingres� el dato correspondiente al campo obligatorio: Funcionario");
			return false;
		}
		if (empleadoConceptoPagos.isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe agregar almenos un Objeto de Gasto y Monto");
			return false;
		}
		if (observacion == null || observacion.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar datos en el campo obligatorio Observaci�n");
			return false;
		}
		return true;
	}

	public String toFindPersonaToView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/movilidadLaboral/promocionCategorial/PromocionCategorialViewCU666&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
	}

	public String obtenerCodigoPuesto(EmpleadoPuesto empleado) {
		if (empleado != null) {
			PlantaCargoDet det = new PlantaCargoDet();
			det = empleado.getPlantaCargoDet();
			if (det != null) {
				ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
				uoDet = det.getConfiguracionUoDet();
				nivelEntidadOeeUtil.setIdConfigCab(uoDet
						.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
						.getIdConfiguracionUoDet());
				nivelEntidadOeeUtil.init2();
				String code = nivelEntidadOeeUtil.getOrdenUnidOrg()
						+ nivelEntidadOeeUtil.getCodigoUnidOrgDep();
				if (det.getContratado())
					code = code + "C";
				if (det.getPermanente())
					code = code + "P";
				code = code + det.getOrden();
				return code;
			}
			return "";
		}
		return "";
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}

	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public boolean isHabUpdate() {
		return habUpdate;
	}

	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
	}

	public boolean isHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}

	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
	}

	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}

	public void setConfiguracionUoDetList(
			List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}

	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}

	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
		return empleadoConceptoPagos;
	}

	public void setEmpleadoConceptoPagos(
			List<EmpleadoConceptoPago> empleadoConceptoPagos) {
		this.empleadoConceptoPagos = empleadoConceptoPagos;
	}

	public List<Documentos> getListaDocumentos() {
		return listaDocumentos;
	}

	public void setListaDocumentos(List<Documentos> listaDocumentos) {
		this.listaDocumentos = listaDocumentos;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}

	public Long getIdFuncionario() {
		return idFuncionario;
	}

	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getNombreNivelEntidad() {
		return nombreNivelEntidad;
	}

	public void setNombreNivelEntidad(String nombreNivelEntidad) {
		this.nombreNivelEntidad = nombreNivelEntidad;
	}

	public BigDecimal getCodNivelEntidad() {
		return codNivelEntidad;
	}

	public void setCodNivelEntidad(BigDecimal codNivelEntidad) {
		this.codNivelEntidad = codNivelEntidad;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagosFuncionario() {
		return empleadoConceptoPagosFuncionario;
	}

	public void setEmpleadoConceptoPagosFuncionario(
			List<EmpleadoConceptoPago> empleadoConceptoPagosFuncionario) {
		this.empleadoConceptoPagosFuncionario = empleadoConceptoPagosFuncionario;
	}

}
