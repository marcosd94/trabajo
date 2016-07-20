package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("transladoLinea673EditFC")
public class TransladoLinea673EditFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	TransladoLineaSinSolic711EditFC transladoLineaSinSolic711EditFC;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private Long idSolicitudTrasladoCab;
	private SolicitudTrasladoCab solicitudTrasladoCab = new SolicitudTrasladoCab();
	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
	EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
	private Persona persona = new Persona();
	private EmpleadoPuesto funcionario = new EmpleadoPuesto();
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;

	private List<ReferenciaAdjuntos> adjuntos = new ArrayList<ReferenciaAdjuntos>();
	private List<EmpleadoMovilidadAnexo> conceptoPagosOrigen =
		new ArrayList<EmpleadoMovilidadAnexo>();
	private List<EmpleadoMovilidadAnexo> conceptoPagosActual = new ArrayList<EmpleadoMovilidadAnexo>();

	public void init() throws Exception {
		if (transladoLineaSinSolic711EditFC == null) {
			transladoLineaSinSolic711EditFC = (TransladoLineaSinSolic711EditFC) org.jboss.seam.Component
					.getInstance(TransladoLineaSinSolic711EditFC.class, true);
		}
		SolicitudTrasladoCab solicitudTrasladoCabAux = em.find(SolicitudTrasladoCab.class, idSolicitudTrasladoCab);
		transladoLineaSinSolic711EditFC.setIdFuncionario(solicitudTrasladoCabAux.getEmpleadoPuesto().getIdEmpleadoPuesto());
		transladoLineaSinSolic711EditFC.setPrimeraEntrada(false);
		transladoLineaSinSolic711EditFC.init();
		transladoLineaSinSolic711EditFC.setEmpleadoPuesto(transladoLineaSinSolic711EditFC.getFuncionario());
		
		transladoLineaSinSolic711EditFC.seleccionarPuestoInit(solicitudTrasladoCabAux.getPlantaCargoDet());
		seleccionUtilFormController.setCodigoObj(solicitudTrasladoCabAux.getCodObjeto());
		seleccionUtilFormController.setCodigoCategoria(solicitudTrasladoCabAux.getCodCategoria());
		seleccionUtilFormController.setValorObj(solicitudTrasladoCabAux.getValorObj());
		seleccionUtilFormController.setCategoria(solicitudTrasladoCabAux.getCategoria());
		seleccionUtilFormController.setMonto(solicitudTrasladoCabAux.getMonto().intValue());
		transladoLineaSinSolic711EditFC.agregarInit();
		
		adjuntos=em.createQuery("Select r from ReferenciaAdjuntos r " +
				" where r.idRegistroTabla="+idSolicitudTrasladoCab+" and (r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO'"+
				" or r.actividadProceso.actividad.descripcion='RESPONDER TRASLADO')").getResultList();
	}

	public void initVer() throws Exception {
		if (transladoLineaSinSolic711EditFC == null) {
			transladoLineaSinSolic711EditFC = (TransladoLineaSinSolic711EditFC) org.jboss.seam.Component
					.getInstance(TransladoLineaSinSolic711EditFC.class, true);
		}
		transladoLineaSinSolic711EditFC.init();
		if (idSolicitudTrasladoCab != null) {
			solicitudTrasladoCab = em.find(SolicitudTrasladoCab.class,
					idSolicitudTrasladoCab);
			cargarDatosVer();

			persona = empleadoMovilidadCab.getEmpleadoPuestoAnterior().getPersona();
			funcionario = new EmpleadoPuesto();

			funcionario = empleadoMovilidadCab.getEmpleadoPuestoAnterior();
			empleadoPuesto = empleadoMovilidadCab.getEmpleadoPuestoNuevo();
			plantaCargoDetView =
				em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
			 
			obtenerCatOrigen();
			obtenerCatDestino();
			cargarNEOFuncionario();
		}
	}
	
	private void cargarNEOFuncionario() {
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilAnterior.setIdUnidadOrganizativa(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet());
		entidadOeeUtilAnterior.init2();
	}
	@SuppressWarnings("unchecked")
	private void obtenerCatDestino() {
		conceptoPagosActual =
			em.createQuery("Select e from EmpleadoMovilidadAnexo e "
				+ " where e.empleadoMovilidadCab.empleadoPuestoNuevo.idEmpleadoPuesto=:idEmpleadoPuesto "
				+ " and  e.activo=true and e.anexoOee='D'").setParameter("idEmpleadoPuesto", empleadoPuesto.getIdEmpleadoPuesto()).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private void obtenerCatOrigen() {
		conceptoPagosOrigen = new ArrayList<EmpleadoMovilidadAnexo>();
		List<EmpleadoConceptoPago> m =
			em.createQuery("Select e from EmpleadoConceptoPago e "
				+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpleadoPuesto").setParameter("idEmpleadoPuesto", funcionario.getIdEmpleadoPuesto()).getResultList();
		for (EmpleadoConceptoPago empleadoConceptoPago : m) {
			EmpleadoMovilidadAnexo movilidadAnexo = new EmpleadoMovilidadAnexo();
			movilidadAnexo.setCategoria(empleadoConceptoPago.getCategoria());
			movilidadAnexo.setMonto(empleadoConceptoPago.getMonto());
			movilidadAnexo.setObjCodigo(empleadoConceptoPago.getObjCodigo());
			conceptoPagosOrigen.add(movilidadAnexo);
		}
	}

	private void cargarDatosVer() {
		adjuntos = em.createQuery(
				"Select r from ReferenciaAdjuntos r "
						+ " where r.idRegistroTabla="
						+ solicitudTrasladoCab.getIdSolicitudTrasladoCab())
				.getResultList();
		
		List<EmpleadoMovilidadCab> listaMovCab = em
		.createQuery(
				"Select e from EmpleadoMovilidadCab e "
						+ " where e.solicitudTrasladoCab.idSolicitudTrasladoCab="
						+ solicitudTrasladoCab
								.getIdSolicitudTrasladoCab())
		.getResultList();
		if(!listaMovCab.isEmpty())
			empleadoMovilidadCab = listaMovCab.get(0);
		
	}

	
	public String save() {
		try {
			if (!transladoLineaSinSolic711EditFC.chkDatos("save"))
				return null;
			PlantaCargoDet puestoSelec = em.find(PlantaCargoDet.class,
					transladoLineaSinSolic711EditFC.getIdPlantaCargoDet());

			/**
			 * Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
			 */
			em.clear();
			transladoLineaSinSolic711EditFC.getFuncionario().setPin(
					seleccionUtilFormController.generarPIN());
			transladoLineaSinSolic711EditFC.getFuncionario().setActual(false);
			transladoLineaSinSolic711EditFC.getFuncionario().setFechaFin(
					new Date());
			transladoLineaSinSolic711EditFC.getFuncionario().setUsuMod(
					usuarioLogueado.getCodigoUsuario());
			transladoLineaSinSolic711EditFC.getFuncionario().setFechaMod(
					new Date());
			transladoLineaSinSolic711EditFC.setFuncionario(em
					.merge(transladoLineaSinSolic711EditFC.getFuncionario()));
			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al
			 * Funcionario seleccionado
			 */
			PlantaCargoDet plantaCargoDetFuncionario = em.find(
					PlantaCargoDet.class, transladoLineaSinSolic711EditFC
							.getFuncionario().getPlantaCargoDet()
							.getIdPlantaCargoDet());
			plantaCargoDetFuncionario.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("LIBRE"));
			plantaCargoDetFuncionario.setUsuMod(usuarioLogueado
					.getCodigoUsuario());
			plantaCargoDetFuncionario.setFechaMod(new Date());
			plantaCargoDetFuncionario.setEstadoDet(null);
			plantaCargoDetFuncionario = em.merge(plantaCargoDetFuncionario);
			transladoLineaSinSolic711EditFC.getFuncionario().setPlantaCargoDet(
					plantaCargoDetFuncionario);
			/***
			 * • Registra el histórico de cambios de estados del Puesto anterior
			 * en la tabla HISTORICOS_ESTADO , registrando el estado actual
			 */
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("LIBRE"));
			historico.setFechaMod(new Date());
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historico.setPlantaCargoDet(plantaCargoDetFuncionario);
			em.persist(historico);

			/**
			 * se guardan los datos en la tabla EMPLEADO_PUESTO
			 */
			//EmpleadoPuesto aux = transladoLineaSinSolic711EditFC.getEmpleadoPuesto();
			//DatosEspecificos aux2 = seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","TRASLADO DEFINITIVO SIN LINEAS DE CARGO");
			//transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(aux2);
			/*transladoLineaSinSolic711EditFC.getEmpleadoPuesto().
			setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","TRASLADO DEFINITIVO SIN LINEAS DE CARGO"));
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setFechaInicio(transladoLineaSinSolic711EditFC.getFechaInicio());
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setActual(true);
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setActivo(true);
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setPin(seleccionUtilFormController.generarPIN());
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setContratado(false);
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().
			setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE"));
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().
			setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD","MOVILIDAD"));
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setFechaAlta(new Date());
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setUsuAlta(usuarioLogueado.getCodigoUsuario());
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setPlantaCargoDet(em.find(PlantaCargoDet.class,transladoLineaSinSolic711EditFC.getIdPlantaCargoDet()));
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setPersona(transladoLineaSinSolic711EditFC.getPersona());
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setObservacion(transladoLineaSinSolic711EditFC.getObservacion());
			transladoLineaSinSolic711EditFC.getEmpleadoPuesto().setIncidenAntiguedad(true);
			transladoLineaSinSolic711EditFC.setEmpleadoPuesto(em.merge(transladoLineaSinSolic711EditFC.getEmpleadoPuesto()));*/
			
			empleadoPuesto = new EmpleadoPuesto();
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.
					traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD","TRASLADO DEFINITIVO SIN LINEAS DE CARGO"));
			empleadoPuesto.setFechaInicio(transladoLineaSinSolic711EditFC.getFechaInicio());
			empleadoPuesto.setActual(true);
			empleadoPuesto.setActivo(true);
			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
			empleadoPuesto.setContratado(false);
			empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE"));
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD","MOVILIDAD"));
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,transladoLineaSinSolic711EditFC.getIdPlantaCargoDet()));
			empleadoPuesto.setPersona(transladoLineaSinSolic711EditFC.getPersona());
			empleadoPuesto.setObservacion(transladoLineaSinSolic711EditFC.getObservacion());
			empleadoPuesto.setIncidenAntiguedad(true);
			em.persist(empleadoPuesto);
			
			/**
			 * Cambiar el estado del puesto a OCUPADO
			 */
			puestoSelec.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
			puestoSelec.setFechaMod(new Date());
			em.merge(puestoSelec);

			/**
			 * Generar un registro del cambio de estado en la tabla
			 * HISTORICOS_ESTADO
			 */
			HistoricosEstado historial = new HistoricosEstado();
			historial.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			historial.setFechaMod(new Date());
			historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historial.setPlantaCargoDet(puestoSelec);

			em.persist(historial);

			/**
			 * Registrar datos de movilidad. en la tabla EMPLEADO_MOVILIDAD_CAB
			 */
			EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
			empleadoMovilidadCab.setActivo(true);
			empleadoMovilidadCab
					.setEmpleadoPuestoAnterior(transladoLineaSinSolic711EditFC
							.getFuncionario());
			empleadoMovilidadCab
					.setEmpleadoPuestoNuevo(transladoLineaSinSolic711EditFC
							.getEmpleadoPuesto());
			empleadoMovilidadCab.setMovilidadSicca(false);
			empleadoMovilidadCab.setFechaInicio(transladoLineaSinSolic711EditFC
					.getFechaInicio());
			empleadoMovilidadCab.setFechaAlta(new Date());
			empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoMovilidadCab.setObservacion(transladoLineaSinSolic711EditFC
					.getObservacion());
			empleadoMovilidadCab
					.setSolicitudTrasladoCab(new SolicitudTrasladoCab());
			empleadoMovilidadCab.getSolicitudTrasladoCab()
					.setIdSolicitudTrasladoCab(idSolicitudTrasladoCab);
			em.persist(empleadoMovilidadCab);

			/* • Registrar cambio de estado de solicitud */
			SolicitudTrasladoCab solicitudTrasladoCab = em.find(
					SolicitudTrasladoCab.class, idSolicitudTrasladoCab);
			solicitudTrasladoCab
					.setDatosEspEstadoSolicitud(seleccionUtilFormController
							.traerDatosEspecificos(
									"ESTADOS SOLICITUD MOVILIDAD",
									"GESTION FINALIZADA"));
			solicitudTrasladoCab.setFechaMod(new Date());
			solicitudTrasladoCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
			solicitudTrasladoCab = em.merge(solicitudTrasladoCab);

			/**
			 * • Gestionar objetos y categorías para el puesto ocupado
			 */
			// De Categorías/remuneraciones en OEE Origen
			for (EmpleadoMovilidadAnexo movilidadAnexo : transladoLineaSinSolic711EditFC
					.getConceptoPagosOrigen()) {
				EmpleadoConceptoPago conceptoPago = new EmpleadoConceptoPago();
				conceptoPago.setEmpleadoPuesto(transladoLineaSinSolic711EditFC
						.getEmpleadoPuesto());
				conceptoPago.setFechaAlta(new Date());
				conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				conceptoPago.setAnho(Integer
						.parseInt(transladoLineaSinSolic711EditFC.sdfAnio
								.format(transladoLineaSinSolic711EditFC
										.getFechaInicio())));
				conceptoPago.setObjCodigo(movilidadAnexo.getObjCodigo());
				conceptoPago.setMonto(movilidadAnexo.getMonto());
				conceptoPago.setCategoria(movilidadAnexo.getCategoria());
				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 */
				if (conceptoPago.getCategoria() != null
						&& transladoLineaSinSolic711EditFC
								.getSinAnxSeleccionado() != null) {
					SinAnx anx = em.find(SinAnx.class,
							transladoLineaSinSolic711EditFC
									.getSinAnxSeleccionado().getIdAnx());
					anx.setCantDisponible(anx.getCantDisponible() + 1);
					em.merge(anx);
				}
				em.persist(conceptoPago);

			}
			// De Categorías/remuneraciones en OEE Destino
			for (EmpleadoMovilidadAnexo movilidadAnexoDestino : transladoLineaSinSolic711EditFC
					.getConceptoPagosActual()) {
				EmpleadoConceptoPago conceptoPago = new EmpleadoConceptoPago();
				conceptoPago.setEmpleadoPuesto(transladoLineaSinSolic711EditFC
						.getEmpleadoPuesto());
				conceptoPago.setFechaAlta(new Date());
				conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				conceptoPago.setAnho(Integer
						.parseInt(transladoLineaSinSolic711EditFC.sdfAnio
								.format(transladoLineaSinSolic711EditFC
										.getFechaInicio())));
				conceptoPago.setObjCodigo(movilidadAnexoDestino.getObjCodigo());
				conceptoPago.setMonto(movilidadAnexoDestino.getMonto());
				conceptoPago.setCategoria(movilidadAnexoDestino.getCategoria());
				em.persist(conceptoPago);
				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 */
				if (conceptoPago.getCategoria() != null
						&& transladoLineaSinSolic711EditFC
								.getSinAnxSeleccionado() != null) {
					SinAnx anx = em.find(SinAnx.class,
							transladoLineaSinSolic711EditFC
									.getSinAnxSeleccionado().getIdAnx());
					anx.setCantDisponible(anx.getCantDisponible() - 1);
					em.merge(anx);
				}
				em.persist(conceptoPago);

			}

			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
			 * Insertar registro en Documentos y el archivo pdf *
			 */
			// Por Acto administrativo de Entidad Origen
			/*Long idDocOrigen = null;
			transladoLineaSinSolic711EditFC.nombrePantalla = "transladoLineaConSolic710EditFC_edit";
			if (transladoLineaSinSolic711EditFC.getFileActoAdmin() != null) {
				idDocOrigen = transladoLineaSinSolic711EditFC.guardarAdjuntos(
						transladoLineaSinSolic711EditFC.getfName(),
						transladoLineaSinSolic711EditFC.getFileActoAdmin()
								.getFileSize(), transladoLineaSinSolic711EditFC
								.getFileActoAdmin().getContentType(),
						transladoLineaSinSolic711EditFC.getFileActoAdmin(),
						transladoLineaSinSolic711EditFC.getIdTipoDoc(), null);
				if (idDocOrigen == null)
					return null;
				Documentos doc = em.find(Documentos.class, idDocOrigen);
				doc.setNroDocumento(transladoLineaSinSolic711EditFC.getNroDoc());
				doc.setAnhoDocumento(Integer
						.parseInt(transladoLineaSinSolic711EditFC.sdfAnio
								.format(transladoLineaSinSolic711EditFC
										.getFechaDoc())));
				doc.setFechaDoc(transladoLineaSinSolic711EditFC.getFechaDoc());
				em.merge(doc);

			}
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 */

			//transladoLineaSinSolic711EditFC.insertarRerAdjunto(idDocOrigen);

			// Por Acto administrativo de Entidad Destino
			Long idDocDestino = null;
			if (transladoLineaSinSolic711EditFC.getBuscadorDocsDesctino()
					.getFileActoAdmin() != null) {

				idDocDestino = transladoLineaSinSolic711EditFC
						.guardarAdjuntos(transladoLineaSinSolic711EditFC
								.getBuscadorDocsDesctino().getfName(),
								transladoLineaSinSolic711EditFC
										.getBuscadorDocsDesctino()
										.getFileActoAdmin().getFileSize(),
								transladoLineaSinSolic711EditFC
										.getBuscadorDocsDesctino()
										.getFileActoAdmin().getContentType(),
								transladoLineaSinSolic711EditFC
										.getBuscadorDocsDesctino()
										.getFileActoAdmin(),
								transladoLineaSinSolic711EditFC
										.getBuscadorDocsDesctino()
										.getIdTipoDoc(), null);
				if (idDocDestino == null)
					return null;
				Documentos doc = em.find(Documentos.class, idDocDestino);
				doc.setNroDocumento(transladoLineaSinSolic711EditFC
						.getBuscadorDocsDesctino().getNroDoc());
				doc.setAnhoDocumento(Integer
						.parseInt(transladoLineaSinSolic711EditFC.sdfAnio
								.format(transladoLineaSinSolic711EditFC
										.getBuscadorDocsDesctino()
										.getFechaDoc())));
				doc.setFechaDoc(transladoLineaSinSolic711EditFC
						.getBuscadorDocsDesctino().getFechaDoc());
				em.merge(doc);

			}
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 */

			transladoLineaSinSolic711EditFC.insertarRerAdjunto(idDocDestino);
			enviarAlerta(transladoLineaSinSolic711EditFC.getFuncionario());

			jbpmUtilFormController
					.setSolicitudTrasladoCab(solicitudTrasladoCab);
			jbpmUtilFormController
					.setConcursoPuestoAgr(transladoLineaSinSolic711EditFC
							.getFuncionario().getConcursoPuestoAgr());
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.MOV_REGISTRAR_TRASLADO_CON_LINEA);
			jbpmUtilFormController.setTransition("regTraDefSinLin_TO_end");
			jbpmUtilFormController.setActividadSiguiente(null);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			} else {
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_NO_MSG"));
				return "FAIL";
			}

			transladoLineaSinSolic711EditFC.setearDatos();
			transladoLineaSinSolic711EditFC.setPrimeraEntrada(true);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("GENERICO_NO_MSG"));
			e.printStackTrace();
			return null;
		}

	}

	public String toFindPersonaView() {

		String from = "";
		if (transladoLineaSinSolic711EditFC.getIdEmpleadoPuesto() == null)
			from = "/movilidadLaboral/transladoDefSinLineaConSolic/TransladoSinLineaConSolic673";
		else
			from = "/movilidadLaboral/transladoDefSinLineaConSolic/TransladoSinLineaConSolic673";
		return "/seleccion/persona/Persona.xhtml?personaFrom="
				+ from
				+ "&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
	}

	private List<String> traerDestinatario() {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.dominio = 'EMAIL_COPIA_SFP' AND Referencias.descAbrev = 'ALERTA MOVILIDAD'");
		List<Referencias> lista = q.getResultList();
		List<String> lRespuesta = new ArrayList<String>();
		for (Referencias o : lista) {
			lRespuesta.add(o.getDescLarga());
		}
		return lRespuesta;
	}

	private String cargarCuerpoMail(EmpleadoPuesto empleadoPuesto,
			String template) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		VelocityEngine ve = new VelocityEngine();
		java.util.Properties p = new java.util.Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);
		VelocityContext context = new VelocityContext();
		context.put("OEE", empleadoPuesto.getConcursoPuestoAgr().getConcurso()
				.getConfiguracionUoCab().getDenominacionUnidad());
		context.put("FUNCIONARIO", empleadoPuesto.getPersona().getNombres()
				+ " " + empleadoPuesto.getPersona().getApellidos());
		context.put("FECHA_INICIO", sdf.format(empleadoPuesto.getFechaInicio()));
		context.put("PUESTO_ANTERIOR", empleadoPuesto.getPlantaCargoDet()
				.getDescripcion());

		Template t = ve.getTemplate(template);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		return writer.toString();
	}

	private void enviarAlerta(EmpleadoPuesto empleadoPuesto) {
		PlantaCargoDet plantaCargoDetFuncionario = em.find(
				PlantaCargoDet.class, transladoLineaSinSolic711EditFC
						.getFuncionario().getPlantaCargoDet()
						.getIdPlantaCargoDet());
		Query q = em
				.createQuery("select DetTipoNombramiento from DetTipoNombramiento DetTipoNombramiento "
						+ " where DetTipoNombramiento.plantaCargoDet.idPlantaCargoDet = :idPlantaCargoDet "
						+ " and DetTipoNombramiento.tipoNombramiento.descripcion = 'NOMBRAMIENTO CONFIANZA' "
						+ " or DetTipoNombramiento.tipoNombramiento.descripcion = 'NOMBRAMIENTO CONF. CARRERA'");
		q.setParameter("idPlantaCargoDet",
				plantaCargoDetFuncionario.getIdPlantaCargoDet());
		List lista = q.getResultList();
		if (lista.size() == 0) {
			// El cargo NO ES de confianza
			List<String> destinatarios = traerDestinatario();
			for (String o : destinatarios) {
				String texto = "";
				texto = cargarCuerpoMail(empleadoPuesto,
						"/templates/email710.vm");
				seleccionUtilFormController.enviarMails(o, texto,
						"NOTIF_TRASLADO_DEFINITIVO_SIN_LINEA", null);
			}

		}

	}

	public Long getIdSolicitudTrasladoCab() {
		return idSolicitudTrasladoCab;
	}

	public void setIdSolicitudTrasladoCab(Long idSolicitudTrasladoCab) {
		this.idSolicitudTrasladoCab = idSolicitudTrasladoCab;
	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public void setSolicitudTrasladoCab(
			SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public EmpleadoMovilidadCab getEmpleadoMovilidadCab() {
		return empleadoMovilidadCab;
	}

	public void setEmpleadoMovilidadCab(EmpleadoMovilidadCab empleadoMovilidadCab) {
		this.empleadoMovilidadCab = empleadoMovilidadCab;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}

	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}

	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
	}

	public List<EmpleadoMovilidadAnexo> getConceptoPagosOrigen() {
		return conceptoPagosOrigen;
	}

	public void setConceptoPagosOrigen(
			List<EmpleadoMovilidadAnexo> conceptoPagosOrigen) {
		this.conceptoPagosOrigen = conceptoPagosOrigen;
	}

	public List<EmpleadoMovilidadAnexo> getConceptoPagosActual() {
		return conceptoPagosActual;
	}

	public void setConceptoPagosActual(
			List<EmpleadoMovilidadAnexo> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}

	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}

	public void setEntidadOeeUtilAnterior(NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}
	
	

}
