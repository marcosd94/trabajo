package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.IngresoRenovacionContrato;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Persona;

import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;

import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("ingresoPorRenovacionContratoFC")
public class IngresoPorRenovacionContratoFC {
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
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ValidadorController validadorController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	BuscadorDocsFC buscadorDocsFC;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private EmpleadoPuesto empleadoPuesto;
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private Long idEmpleadoPuesto;
	private Long idPlantaCargoDet;
	private Long idPais;
	private String docIdentidad = null;
	private Date fechaIngreso;
	private Persona personaView;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");

	private List<ConfiguracionUoDet> configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
	private List<IngresoRenovacionContrato> renovacionContratoList = new ArrayList<IngresoRenovacionContrato>();
	private List<Documentos> listaDocumentos = new ArrayList<Documentos>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos = new ArrayList<EmpleadoConceptoPago>();

	public void init() throws Exception {

		empleadoPuesto = new EmpleadoPuesto();

		if (idEmpleadoPuesto != null) {
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			obtenerDatos();
		}
		setearDatos();

	}

	public void initView() throws Exception {

		if (idEmpleadoPuesto != null) {
			if (!seguridadUtilFormController.validarInput(
					idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(),
					null)) {
				return;
			}
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			personaView = empleadoPuesto.getPersona();
			completarDatosPersona(personaView, true);

		}

		cargarNiveentidadOee();
		obtenerDatosView();

		fechaIngreso = empleadoPuesto.getFechaInicio();
	}

	private void completarDatosPersona(Persona persona, Boolean completo) {
		idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		docIdentidad = persona.getDocumentoIdentidad();
	}

	private void obtenerDatosView() {
		List<ReferenciaAdjuntos> r = em.createQuery(
				"Select r from ReferenciaAdjuntos r "
						+ " where r.idRegistroTabla=" + idEmpleadoPuesto)
				.getResultList();
		listaDocumentos = new ArrayList<Documentos>();
		for (ReferenciaAdjuntos ad : r)
			listaDocumentos.add(ad.getDocumentos());

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

	private void setearDatos() {
		renovacionContratoList = new ArrayList<IngresoRenovacionContrato>();
		List<IngresoRenovacionContrato> lista = new ArrayList<IngresoRenovacionContrato>();

		lista = em.createQuery(
				"select distinct renovacion "
						+ " from IngresoRenovacionContrato renovacion")
				.getResultList();
		for (IngresoRenovacionContrato ing : lista) {
			ing.setFechaFin(null);
			ing.setFechaInicio(null);
			ing.setNroContrato(null);
			renovacionContratoList.add(ing);
		}
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
									+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.valorAlf like 'CON' and d.activo=true order by d.descripcion")
					.getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public String obtenerCodigoPuesto(Long idDet) {
		PlantaCargoDet det = new PlantaCargoDet();

		det = em.find(PlantaCargoDet.class, idDet);

		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
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

	public String obtenerCodigoPuesto() {

		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = plantaCargoDetView.getConfiguracionUoDet();
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		String code = nivelEntidadOeeUtil.getOrdenUnidOrg()
				+ nivelEntidadOeeUtil.getCodigoUnidOrgDep();
		if (plantaCargoDetView.getContratado())
			code = code + "C";
		if (plantaCargoDetView.getPermanente())
			code = code + "P";
		code = code + plantaCargoDetView.getOrden();
		return code;
	}

	public void eliminar(IngresoRenovacionContrato ingreso) {
		renovacionContratoList.remove(ingreso);
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
		for (IngresoRenovacionContrato ing : renovacionContratoList) {
			if (!registroCompleto(ing))
				return false;
			if (!validar(ing))
				return false;
			if(ing.getFechaInicio() != null && ing.getFechaFin() != null){
				if (ing.getFechaInicio().after(ing.getFechaFin())) {
					statusMessages
							.add(Severity.ERROR,
									"La fecha de inicio no puede ser mayor a la fecha fin. Verifique");
					return false;
				}
			}
		}

		return true;
	}

	private Boolean registroCompleto(IngresoRenovacionContrato ingreso) {
		if (ingreso.getNroContrato() != null || ingreso.getFechaFin() != null
				|| ingreso.getFechaInicio() != null) {
			if (ingreso.getNroContrato() != null
					&& ingreso.getNroContrato().intValue() < 0) {
				statusMessages.add(Severity.ERROR,
						"El número de contrato no puede ser menor a 0");
				return false;
			}
			if (ingreso.getNroContrato() == null
					|| ingreso.getFechaInicio() == null) {
				statusMessages.add(Severity.ERROR,
						"Ingrese los datos obligatorios");
				return false;
			}
		}
		return true;
	}

	private Boolean validar(IngresoRenovacionContrato ingreso) {
		String grupoValidador = "IDRENCONTR";
		PlantaCargoDet det = em.find(PlantaCargoDet.class,
				ingreso.getIdPuesto());
		ValidadorDTO validadorDTOPersona = validadorController.validarCfg(
				"PERSONA", grupoValidador, em.find(Persona.class,
						ingreso.getIdPersona()), det, null, det
						.getConfiguracionUoDet().getConfiguracionUoCab());
		if (!validadorDTOPersona.isValido()) {
			statusMessages
					.add(Severity.ERROR, validadorDTOPersona.getMensaje());
			return false;
		}
		ValidadorDTO validadorDTOPuesto = validadorController.validarCfg(
				"PUESTO", grupoValidador, em.find(Persona.class,
						ingreso.getIdPersona()), det, null, det
						.getConfiguracionUoDet().getConfiguracionUoCab());
		if (!validadorDTOPuesto.isValido()) {
			statusMessages
					.add(Severity.ERROR, validadorDTOPersona.getMensaje());
			return false;
		}

		return true;
	}

	public String save() {

		try {
			if (!chkDatos())
				return null;
			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
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
					em.clear();
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setNroDocumento(buscadorDocsFC.getNroDoc());
					doc.setAnhoDocumento(Integer.parseInt(sdfAnio
							.format(buscadorDocsFC.getFechaDoc())));
					doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
					
						em.merge(doc);
						
					

				}
			}
			
			
			for (IngresoRenovacionContrato ing : renovacionContratoList) {
				if (ing.getNroContrato() != null) {
					/**
					 * Inserta un nuevo registro en la tabla EMPLEADO_PUESTO
					 * */
					Persona persona = em
							.find(Persona.class, ing.getIdPersona());
					PlantaCargoDet plantaCargoDet = em.find(
							PlantaCargoDet.class, ing.getIdPuesto());
					EmpleadoPuesto empleado = new EmpleadoPuesto();

					Long idDato = searchDatosEspecificos(
							"TIPOS DE INGRESOS Y MOVILIDAD",
							"INGRESO DIRECTO PERSONAL RENOVACION CONTRATO")
							.getIdDatosEspecificos();
					DatosEspecificos datoTipoIngreso = em.find(
							DatosEspecificos.class, idDato);
					empleado.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(datoTipoIngreso);
					empleado.setFechaInicio(ing.getFechaInicio());
					empleado.setActual(true);
					empleado.setPin(seleccionUtilFormController.generarPIN());
					empleado.setContratado(false);
					empleado.setLineaPresupuestaria(0);
					if (ing.getFechaFin() != null)
						empleado.setFechaFinContrato(ing.getFechaFin());
					empleado.setNroContrato(ing.getNroContrato());

					empleado.setDatosEspecificosByIdDatosEspEstado(searchDatosEspecificos(
							"ESTADO EMPLEADO PUESTO", "PERMANENTE"));
					empleado.setDatosEspecificosByIdDatosEspTipoRegistro(searchDatosEspecificos(
							"TIPO REGISTRO", "INGRESOS"));
					empleado.setFechaAlta(new Date());
					empleado.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					empleado.setPersona(persona);
					empleado.setPlantaCargoDet(plantaCargoDet);
					
						em.persist(empleado);
						
					

					/** Actualiza el registro modificado Empleado_puesto **/
					EmpleadoPuesto empleadoActualizado = em.find(
							EmpleadoPuesto.class, ing.getIdEmpleado());
					empleadoActualizado.setActual(false);
					empleadoActualizado.setFechaFin(new Date());
					empleadoActualizado.setFechaMod(new Date());
					empleadoActualizado.setUsuMod(usuarioLogueado
							.getCodigoUsuario());
					
						em.merge(empleadoActualizado);
						
					

					/** Actualiza el registro modificado planta_cargo_det **/
					plantaCargoDet.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("OCUPADO"));
					plantaCargoDet.setFechaMod(new Date());
					plantaCargoDet
							.setUsuMod(usuarioLogueado.getCodigoUsuario());
					
						em.merge(plantaCargoDet);
						
					

					/**
					 * Guarda un registro en la tabla de historicos de estado
					 * */
					HistoricosEstado historico = new HistoricosEstado();
					historico.setEstadoCab(seleccionUtilFormController
							.buscarEstadoCab("OCUPADO"));
					historico.setFechaMod(new Date());
					historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
					historico.setPlantaCargoDet(plantaCargoDet);

					em.persist(historico);

					/**
					 * Inserta un nuevo registro en la tabla
					 * empleado_concepto_pago
					 **/

					EmpleadoConceptoPago empleadoConceptoPago = new EmpleadoConceptoPago();
					empleadoConceptoPago.setEmpleadoPuesto(empleado);
					empleadoConceptoPago.setFechaAlta(new Date());
					empleadoConceptoPago.setUsuAlta(new String());
					empleadoConceptoPago.setMonto(ing.getMonto());
					
						em.persist(empleadoConceptoPago);
						
					

					/**
					 * Si no se inserta en tabla documentos y solo se referencia
					 * */
					if (idDoc == null)
						idDoc = buscadorDocsFC.getDocDecreto().getIdDocumento();

					/**
					 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el
					 * acto administrativo
					 * */
					ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
					referenciaAdjuntos.setPersona(em.find(Persona.class,
							ing.getIdPersona()));
					referenciaAdjuntos.setDocumentos(new Documentos());
					referenciaAdjuntos.getDocumentos().setIdDocumento(idDoc);
					referenciaAdjuntos.setIdRegistroTabla(empleado
							.getIdEmpleadoPuesto());
					referenciaAdjuntos.setUsuAlta(usuarioLogueado
							.getCodigoUsuario());
					referenciaAdjuntos.setFechaAlta(new Date());

					em.persist(referenciaAdjuntos);

					/**
					 * El sistema genera un registro en la tabla LEGAJOS , si la
					 * persona aun no existe la persona en la tabla Legajos.
					 * */
					if (!exiteLegajo(persona.getIdPersona())) {
						Legajos legajos = new Legajos();
						legajos.setPersona(persona);
						legajos.setFechaIngreso(ing.getFechaInicio());
						legajos.setDatosEspecificosTipoIngreso(seleccionUtilFormController
								.traerDatosEspecificos(
										"TIPOS DE INGRESOS Y MOVILIDAD",
										"INGRESO DIRECTO PERSONAL RENOVACION CONTRATO"));

						em.persist(legajos);

					}
				}

			}
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

	private DatosEspecificos searchDatosEspecificos(String datoGeneral,
			String datoEspecifico) {
		String sql = "select de.* from seleccion.datos_especificos de "
				+ "join seleccion.datos_generales gral "
				+ "on gral.id_datos_generales = de.id_datos_generales "
				+ "where gral.descripcion = '" + datoGeneral + "' "
				+ "and de.descripcion = '" + datoEspecifico + "' "
				+ "and de.activo is true " + "and gral.activo is true";
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();
		if (lista.isEmpty())
			return null;
		else
			return lista.get(0);
	}

	@SuppressWarnings("unchecked")
	private boolean exiteLegajo(Long idPersona) {
		List<Legajos> legajos = em
				.createQuery(
						"Select l from Legajos l "
								+ " where l.persona.idPersona=:idPersona")
				.setParameter("idPersona", idPersona).getResultList();
		return !legajos.isEmpty();
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "ingresoRenovacionContrato_edit";
			String direccionFisica = "";
			String sf = File.separator;
			direccionFisica = sf + "SICCA" + sf + anio + sf + "OEE" + sf
					+ nivelEntidadOeeUtil.getIdConfigCab();

			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(file,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

	public String toFindPersonaToView() {
		if (personaView.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/seleccion/ingresoDirecto/porRenovacionContrato/RenovacionContratoView&personaIdPersona="
				+ personaView.getIdPersona() + "&conversationPropagation=join";
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
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

	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}

	public void setConfiguracionUoDetList(
			List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}

	public List<IngresoRenovacionContrato> getRenovacionContratoList() {
		return renovacionContratoList;
	}

	public void setRenovacionContratoList(
			List<IngresoRenovacionContrato> renovacionContratoList) {
		this.renovacionContratoList = renovacionContratoList;
	}

	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}

	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
	}

	public Persona getPersonaView() {
		return personaView;
	}

	public void setPersonaView(Persona personaView) {
		this.personaView = personaView;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public List<Documentos> getListaDocumentos() {
		return listaDocumentos;
	}

	public void setListaDocumentos(List<Documentos> listaDocumentos) {
		this.listaDocumentos = listaDocumentos;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
		return empleadoConceptoPagos;
	}

	public void setEmpleadoConceptoPagos(
			List<EmpleadoConceptoPago> empleadoConceptoPagos) {
		this.empleadoConceptoPagos = empleadoConceptoPagos;
	}

}
