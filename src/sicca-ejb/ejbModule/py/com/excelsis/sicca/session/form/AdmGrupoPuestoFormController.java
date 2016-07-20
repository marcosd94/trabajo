package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.dto.CondicionTrabajoDTO;
import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.ConcursoPuestoDetList;
import py.com.excelsis.sicca.session.DetCondicionTrabajoHome;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admGrupoPuestoFormController")
public class AdmGrupoPuestoFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	DetCondicionTrabajoHome detCondicionTrabajoHome;

	@In(create = true)
	ConcursoPuestoDetList concursoPuestoDetList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	private Actor actor;

	private Concurso concurso;
	private ConcursoPuestoAgr puestoAgr;
	private String codigo;
	private Integer orden;
	private String denominacion;
	private String observacion;
	private Long idNivelEntidad;
	private Long idSinEntidad;
	private Long idConfiguracionUoCab;
	private String operacion;
	private String ubicacionFisica;
	private String fromConcurso;
	private Long idAgr;
	private Integer nroActividad;

	private List<ConcursoPuestoDet> puestosSinAsignar = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoDet> puestosAsignados = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoDet> puestosAsignadosAux = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoAgr> puestosAgrupados = new ArrayList<ConcursoPuestoAgr>();
	private List<ConcursoPuestoAgr> puestosAgrupadosAux = new ArrayList<ConcursoPuestoAgr>();
	private boolean primeraEntrada = true;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean mostrarModal = false;
	private static String CONCURSO_INTERNO_INTERINSTITUCIONAL = "CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL";

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		asignarRolesTarea();
		puestoAgr = new ConcursoPuestoAgr();

		puestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		concurso = concursoHome.getInstance();
		validarOee();

		orden = valorMaximoOrden() + 1;
		codigo = valorMaximoCodigo().toString();
		traerDatos();
		if (concursoPuestoAgrHome.isIdDefined() && primeraEntrada) {
			operacion = "updated";
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;

			String separador = File.separator;
			ubicacionFisica = separador
					+ "SICCA"
					+ separador
					+ anho
					+ separador
					+ "OEE"
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos() + separador
					+ concurso.getIdConcurso() + separador
					+ puestoAgr.getIdConcursoPuestoAgr();

			codigo = puestoAgr.getCodGrupo();
			orden = puestoAgr.getNroOrden();
			denominacion = puestoAgr.getDescripcionGrupo();
			observacion = puestoAgr.getObservacion();
			puestosAgrupadosAux = new ArrayList<ConcursoPuestoAgr>();
			if (puestosAgrupados != null && puestosAgrupados.size() > 0)
				puestosAgrupadosAux.addAll(puestosAgrupados);
		}

		if (primeraEntrada)
			primeraEntrada = false;

	}

	/**
	 * Método que recupera los roles de la tarea actual
	 */
	private void asignarRolesTarea() {
		roles = seleccionUtilFormController.getRolesTarea(
				ActividadEnum.MODIFICAR_DATOS_CONCURSO.getValor(),
				ProcesoEnum.CONCURSO.getValor());
	}

	/**
	 * Método que valida si la OEE del concurso es igual al del usuario logueado
	 */
	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (puestoAgr != null && puestoAgr.getConcurso() != null) {
			if (!seguridadUtilFormController.verificarPerteneceOee(puestoAgr
					.getConcurso().getConfiguracionUoCab()
					.getIdConfiguracionUo())) {
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_OEE_NO_VALIDA"));
			}
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}

	}

	/**
	 * Método que recupera los puestos asignados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> traerPuestosAsignados() {
		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		Long id = puestoAgr.getIdConcursoPuestoAgr();
		if (puestoAgr != null && puestoAgr.getIdConcursoPuestoAgr() != null) {
			String cadena = "select distinct(puesto_det.*) "
					+ "from seleccion.concurso_puesto_det puesto_det "
					+ "join seleccion.concurso_puesto_agr puesto_agr "
					+ "on puesto_agr.id_concurso_puesto_agr=puesto_det.id_concurso_puesto_agr "
					+ "where puesto_det.nro_orden = 3 ";

			cadena = cadena + " and puesto_agr.id_concurso_puesto_agr = "
					+ puestoAgr.getIdConcursoPuestoAgr();

			lista = em.createNativeQuery(cadena, ConcursoPuestoDet.class)
					.getResultList();
			puestosAgrupados = new ArrayList<ConcursoPuestoAgr>();
			for (ConcursoPuestoDet o : lista) {
				puestosAgrupados.add(o.getConcursoPuestoAgr());
			}
		}

		return lista;
	}

	/**
	 * Método que recupera la lista de puestos posibles a asignar
	 * 
	 * @return
	 */
	private List<ConcursoPuestoDet> listaPuestosPosibles() {
		String select = " select distinct(puesto_det.*) "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
				+ "join planificacion.estado_cab  "
				+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab  "
				+ " where puesto_det.id_concurso_puesto_agr is null "
				+ "and lower(estado_det.descripcion) = 'en reserva' "
				+ "and lower(estado_cab.descripcion) = 'concurso' "
				+ "and puesto_det.id_concurso = " + concurso.getIdConcurso();

		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		lista = em.createNativeQuery(select, ConcursoPuestoDet.class)
				.getResultList();

		return lista;
	}

	/**
	 * Validaciones realizadas sobre puestos permanentes
	 * 
	 * @param det
	 * @return
	 */
	private boolean validarPermanente(ConcursoPuestoDet det) {
		if (det.getConcurso().getDatosEspecificosTipoConc().getDescripcion()
				.equalsIgnoreCase(CONCURSO_INTERNO_INTERINSTITUCIONAL)) {
			if (cuentaConCpt(det.getPlantaCargoDet()))
				return true;
			return false;
		} else {
			if (cuentaConCpt(det.getPlantaCargoDet())) {
				if (estaEnPuestoConceptoPago(det))
					return true;
				return false;
			}
			return false;

		}
	}

	/**
	 * Método que verifica que el puesto cuente con Concepto Pago
	 * 
	 * @param det
	 * @return
	 */
	private boolean estaEnPuestoConceptoPago(ConcursoPuestoDet det) {
		Referencias referenciaNoRef = new Referencias();
		referenciaNoRef = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO");
		Referencias referenciaRef = new Referencias();
		referenciaRef = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "PENDIENTE");
		Query q = em.createQuery("select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true and pago.objCodigo = :cod "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  "
				+ " and pago.estado = :estado  ");
		q.setParameter("cod", new Integer(111));
		q.setParameter("id", det.getPlantaCargoDet().getIdPlantaCargoDet());
		if (concurso.getAdReferendum())
			q.setParameter("estado", referenciaRef.getValorNum());
		else
			q.setParameter("estado", referenciaNoRef.getValorNum());
		if (q.getResultList().isEmpty())
			return false;

		return true;
	}

	/**
	 * Método que verifica que el puesto cuente con CPT
	 * 
	 * @param planta
	 * @return
	 */
	private boolean cuentaConCpt(PlantaCargoDet planta) {
		if (planta.getCpt() != null && planta.getCpt().getIdCpt() != null)
			return true;
		return false;
	}

	/**
	 * Método que trae los puestos sin asignar
	 * 
	 * @return
	 */
	private List<ConcursoPuestoDet> traerPuestos() {

		List<ConcursoPuestoDet> listaRetorno = new ArrayList<ConcursoPuestoDet>();
		try {
			List<ConcursoPuestoDet> puestosPosibles = listaPuestosPosibles();
			for (ConcursoPuestoDet puesto : puestosPosibles) {
				if (puesto.getPlantaCargoDet().getContratado()) {
					if (cuentaConCpt(puesto.getPlantaCargoDet()))
						listaRetorno.add(puesto);
				}
				if (puesto.getPlantaCargoDet().getPermanente()) {
					if (validarPermanente(puesto))
						listaRetorno.add(puesto);
				}
				if (puesto.getPlantaCargoDet().getComisionado()) {//MODIFICADO RV
						listaRetorno.add(puesto);
				}
			}

			return listaRetorno;
		} catch (Exception ex) {
			return new Vector<ConcursoPuestoDet>();
		}
	}

	/**
	 * Método que carga las listas tanto de puestos asignados como de puestos
	 * sin asignar
	 */
	public void traerDatos() {
		puestosAsignados = new ArrayList<ConcursoPuestoDet>();
		puestosAsignadosAux = new ArrayList<ConcursoPuestoDet>();
		puestosSinAsignar = new ArrayList<ConcursoPuestoDet>();
		puestosSinAsignar = traerPuestos();
		puestosAsignados = traerPuestosAsignados();
		if (puestosAsignados != null && puestosAsignados.size() > 0)
			puestosAsignadosAux.addAll(puestosAsignados);

	}

	/**
	 * Método que verifica que si existe el orden ingresado
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Boolean existeOrden() {
		String cad = "select agr.* "
				+ "from seleccion.concurso_puesto_agr agr "
				+ "join seleccion.concurso concurso  "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where agr.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr()
				+ " and concurso.id_concurso = " + concurso.getIdConcurso()
				+ " and agr.nro_orden = " + orden;
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		if (lista.size() > 0)
			return true;
		return false;
	}

	/**
	 * Método que inicializa la tarea en caso de que sea llamado desde la
	 * actividad 8
	 * 
	 * @return
	 * @throws Exception
	 */
	public String startTask() throws Exception {

		boolean tareaAsignada = jbpmUtilFormController
				.asignarTaskInstanceActual(puestoAgr.getIdProcessInstance(),
						usuarioLogueado.getCodigoUsuario());
		if (!tareaAsignada) {
			throw new Exception("No se pudo asignar el Task Instance Actual.");
		}
		return "OK";

	}

	/**
	 * Método que valida que todos los datos obligatorios hayan sido cargados
	 * para ser guardados
	 * 
	 * @return
	 */
	private String validacion() {

		Long idCpt = null;
		String ms = "";
		for (ConcursoPuestoDet c : puestosAsignados) {
			Boolean cumple = false;

			if (c.getPlantaCargoDet().getCpt() != null) {
				if (idCpt == null) {
					idCpt = c.getPlantaCargoDet().getCpt().getId();
				}

				if (c.getPlantaCargoDet().getCpt().getId().longValue() == idCpt
						.longValue()) {
					cumple = true;
				} else {
					ms += c.getPlantaCargoDet().getIdPlantaCargoDet() + " - "
							+ c.getPlantaCargoDet().getDescripcion();
				}
			}

			if (!cumple) {
				return ms;
			}
		}
		return "";

	}

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	
	/*************************<MODIFICACION>*********************************/
	/*************************MODIFICADO: 22/10/2013*************************/
	/*************************AUTOR: RODRIGO VELAZQUEZ***********************/
	public boolean comprobarCondicionesTrabajos(){
		for(ConcursoPuestoDet cpd1 : puestosAsignados)
			for(DetCondicionTrabajo dct1 : cpd1.getPlantaCargoDet().getDetCondicionTrabajos())
				for(ConcursoPuestoDet cpd2 : puestosAsignados)
					for(DetCondicionTrabajo dct2 : cpd2.getPlantaCargoDet().getDetCondicionTrabajos()){
						if(dct1.getCondicionTrabajo().getId() == dct2.getCondicionTrabajo().getId())
							if(dct1.getActivo() != dct2.getActivo() || Float.compare(dct1.getPuntajeCondTrab(), dct2.getPuntajeCondTrab()) != 0)
								return false;
					}
		return true;
	}

	public boolean combprobarCondicionesTrabajosEspecificos(){
		for(ConcursoPuestoDet cpd1 : puestosAsignados)
			for(DetCondicionTrabajoEspecif dcte1 : cpd1.getPlantaCargoDet().getDetCondicionTrabajoEspecifs())
				for(ConcursoPuestoDet cpd2 : puestosAsignados)
					for(DetCondicionTrabajoEspecif dcte2 : cpd2.getPlantaCargoDet().getDetCondicionTrabajoEspecifs()){
						if(dcte1.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif() == dcte2.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif())
							if(dcte1.getActivo() != dcte2.getActivo() || Float.compare(dcte1.getPuntajeCondTrabEspecif(), dcte2.getPuntajeCondTrabEspecif()) != 0)
								return false;
					}
		return true;
	}	

	public boolean combprobarCondicionesSeguridad(){
		for(ConcursoPuestoDet cpd1 : puestosAsignados)
			for(DetCondicionSegur dcs1 : cpd1.getPlantaCargoDet().getDetCondicionSegurs())
				for(ConcursoPuestoDet cpd2 : puestosAsignados)
					for(DetCondicionSegur dcs2 : cpd2.getPlantaCargoDet().getDetCondicionSegurs()){
						if(dcs1.getCondicionSegur().getId() == dcs2.getCondicionSegur().getId())
							if(dcs1.getActivo() != dcs2.getActivo() || Float.compare(dcs1.getPuntajeCondSegur(), dcs2.getPuntajeCondSegur()) != 0)
								return false;
					}
		return true;
	}
	
	public boolean combprobarTipoVinculacion(){
		for(ConcursoPuestoDet cpd1 : puestosAsignados){
			for(DetTipoNombramiento dtn1 : cpd1.getPlantaCargoDet().getDetTipoNombramientos()){
				for(ConcursoPuestoDet cpd2 : puestosAsignados){
					boolean p = false;
					for(DetTipoNombramiento dtn2 : cpd2.getPlantaCargoDet().getDetTipoNombramientos()){
						if(dtn1.getTipoNombramiento().getIdTipoNombramiento() == dtn2.getTipoNombramiento().getIdTipoNombramiento())
							p=true;
					}
					if(!p)
						return false;
				}
			}
		}
		return true;	
	}
	
	public boolean combprobarModalidadOcupacion(){
		for(ConcursoPuestoDet cpd1 : puestosAsignados){
			for(ConcursoPuestoDet cpd2 : puestosAsignados){
				if(cpd1.getPlantaCargoDet().getContratado() != cpd2.getPlantaCargoDet().getContratado() && 
						cpd1.getPlantaCargoDet().getPermanente() != cpd2.getPlantaCargoDet().getPermanente()){
					return false;
				}
			}
		}
		return true;	
	}
	
	public boolean combprobarComisionado(){
		for(ConcursoPuestoDet cpd1 : puestosAsignados){
			if(cpd1.getPlantaCargoDet().getComisionado()){
					return false;
			}
		}
		return true;	
	}
	/*************************</MODIFICACION>********************************/
	
	public String guardar() {
		String returnResp = "persisted";
		
		
		
		if (puestosAsignados == null || puestosAsignados.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos un puesto para agrupar");
			return null;
		}
		
		/*************************<MODIFICACION>*********************************/
		/*************************MODIFICADO: 22/10/2013*************************/
		/*************************AUTOR: RODRIGO VELAZQUEZ***********************/
		if(!comprobarCondicionesTrabajos()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Las condiciones de trabajos deben ser iguales");
			return null;
		}
		if(!combprobarCondicionesTrabajosEspecificos()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Las condiciones de trabajos especificos deben ser iguales");
			return null;					
		}
		if(!combprobarCondicionesSeguridad()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
				"Las condiciones de seguridad deben ser iguales");
			return null;					
		}
		
		if(!combprobarTipoVinculacion()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
				"Los tipos de vinculaciones deben ser iguales");
			return null;					
		}
		
		if(!combprobarModalidadOcupacion()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
				"Las modalidades de ocupacion deben ser iguales");
			return null;					
		}
		
		if(!combprobarComisionado()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
				"No se puede agrupar puestos con la modalidad de ocupacion del tipo Comisionado");
			return null;					
		}
		/*************************</MODIFICACION>********************************/
		
		if (orden.intValue() <= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El número de Orden debe ser mayor a 0. Verifique");
			return null;
		}

		if (exiteNroOrden("save")) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El número de Orden ingresado ya existe. Verifique");
			return null;
		}
		List<String> lIn = Arrays.asList("1", "8");
		if (nroActividad == null
				|| (nroActividad != null && !lIn.contains(nroActividad
						.toString()))) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Se debe proveer el número de Actividad, este debe ser 1 u 8. Verifique");
			return null;
		}
		try {
			puestoAgr = new ConcursoPuestoAgr();
			puestoAgr.setActivo(true);
			puestoAgr.setCodGrupo(codigo);
			puestoAgr.setConcurso(concurso);
			puestoAgr.setDescripcionGrupo(denominacion);
			
			
			
			
			//GUARDAR CONDICION DE TRABAJO ESPECIFICO JD
		    Set<DetCondicionTrabajo>  listaGuardarDCT = ((ConcursoPuestoDet)puestosAsignados.get(0)).getPlantaCargoDet().getDetCondicionTrabajos();
			for (Iterator iterator = listaGuardarDCT.iterator(); iterator.hasNext();) {
				DetCondicionTrabajo aux = (DetCondicionTrabajo) iterator.next();
				
				DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();

				detCondTrab.setActivo(aux.getActivo());
				detCondTrab.setCondicionTrabajo(aux.getCondicionTrabajo());
				detCondTrab.setPuntajeCondTrab(aux.getPuntajeCondTrab());
				detCondTrab.setConcursoPuestoAgr(puestoAgr);
				detCondTrab.setTipo("GRUPO");
				em.persist(detCondTrab);
				//detCondicionTrabajoHome.setInstance(detCondTrab);
			}
			
			//GUARDAR CONDICION DE TRABAJO ESPECIFICO JD
			Set<DetCondicionTrabajoEspecif> listaGuardarDCTE = ((ConcursoPuestoDet)puestosAsignados.get(0)).getPlantaCargoDet().getDetCondicionTrabajoEspecifs();
			for (Iterator iteratorCDTE = listaGuardarDCTE.iterator(); iteratorCDTE.hasNext();) {
				DetCondicionTrabajoEspecif auxCDTE = (DetCondicionTrabajoEspecif) iteratorCDTE.next();
				
				DetCondicionTrabajoEspecif detCondTrabEsp = new DetCondicionTrabajoEspecif();
				

				detCondTrabEsp.setActivo(auxCDTE.getActivo());
				detCondTrabEsp.setCondicionTrabajoEspecif(auxCDTE.getCondicionTrabajoEspecif());
				detCondTrabEsp.setConcursoPuestoAgr(puestoAgr);
				detCondTrabEsp.setJustificacion(auxCDTE.getJustificacion());
				detCondTrabEsp.setAjustes(auxCDTE.getAjustes());
				detCondTrabEsp.setPuntajeCondTrabEspecif(auxCDTE.getPuntajeCondTrabEspecif());
				detCondTrabEsp.setTipo("GRUPO");
				em.persist(detCondTrabEsp);
				
				//detCondicionTrabajoHome.setInstance(detCondTrab);
			}
			

			//GUARDAR CONDICION DE SEGURIDAD JD
			Set<DetCondicionSegur> listaGuardarDCS = ((ConcursoPuestoDet)puestosAsignados.get(0)).getPlantaCargoDet().getDetCondicionSegurs();
			for (Iterator iteratorCDS = listaGuardarDCS.iterator(); iteratorCDS.hasNext();) {
				DetCondicionSegur auxCDS = (DetCondicionSegur) iteratorCDS.next();
				
				DetCondicionSegur detCondicionSegur = new DetCondicionSegur();
				
				detCondicionSegur.setCondicionSegur(auxCDS.getCondicionSegur());
				detCondicionSegur.setTipo("GRUPO");
				detCondicionSegur.setPuntajeCondSegur(auxCDS.getPuntajeCondSegur());
				detCondicionSegur.setAjustes(auxCDS.getAjustes());
				detCondicionSegur.setJustificacion(auxCDS.getJustificacion());
				detCondicionSegur.setActivo(auxCDS.getActivo());
				detCondicionSegur.setConcursoPuestoAgr(puestoAgr);
				em.persist(detCondicionSegur);
				
				//detCondicionTrabajoHome.setInstance(detCondTrab);
			}
			
			
			Referencias ref = new Referencias();
			
			if (nroActividad.intValue() == 1) {
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "CREADO");
			} else {
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR");
			}
			if (ref != null) {
				puestoAgr.setEstado(ref.getValorNum());
			}
			EstadoDet estadoDet = buscarEstado("agrupado");
			if (!existeOrden())
				puestoAgr.setNroOrden(orden);
			else {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El orden ingresado ya existe o no es valido");
				return null;
			}

			String mensaje = validacion();

			if (!mensaje.trim().isEmpty()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Los puestos: " + mensaje
						+ " no pueden ser agrupados, son de CPT's distintos");
				return null;
			}

			if (nroActividad.intValue() == 8
					&& puestoAgr.getIdProcessInstance() == null) {

				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.MODIFICAR_DATOS_CONCURSO);
				jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CONCURSO);
				jbpmUtilFormController.setTransition("next4");
				actor.setId(usuarioLogueado.getCodigoUsuario());
				Long processId = jbpmUtilFormController
						.initProcess("seleccion");
				if (processId != null) {
					puestoAgr.setIdProcessInstance(processId);
					mostrarModal = true;
					returnResp = "START_TASK";
				} else {
					throw new Exception(
							"No se pudo generar el process Instance.");
				}

			} else {
				mostrarModal = false;
			}

			puestoAgr.setObservacion(observacion);
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			

			puestoAgr.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoAgr.setFechaAlta(new Date());
			em.persist(puestoAgr);
			concursoPuestoAgrHome.setInstance(puestoAgr);
			String separador = File.separator;
			ubicacionFisica = separador
					+ "SICCA"
					+ separador
					+ anho
					+ separador
					+ "OEE"
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos()
					+ separador
					+ concurso.getIdConcurso()
					+ separador
					+ concursoPuestoAgrHome.getInstance()
							.getIdConcursoPuestoAgr();

			for (ConcursoPuestoDet c : puestosAsignados) {
				c.setNroOrden(3);
				c.setEstadoDet(estadoDet);
				c.setConcursoPuestoAgr(concursoPuestoAgrHome.getInstance());
				em.merge(c);

				PlantaCargoDet planta = new PlantaCargoDet();
				planta = c.getPlantaCargoDet();
				planta.setEstadoDet(estadoDet);
				em.merge(planta);
			}

			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return returnResp;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean exiteNroOrden(String op) {
		String sql = "Select c from ConcursoPuestoAgr c "
				+ " where c.nroOrden= " + orden + " and c.concurso.idConcurso="
				+ concurso.getIdConcurso();
		if (op.equals("update"))
			sql += " and c.idConcursoPuestoAgr !="
					+ puestoAgr.getIdConcursoPuestoAgr();
		List<ConcursoPuestoAgr> agrs = em.createQuery(sql).getResultList();
		if (!agrs.isEmpty())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	private PuestoConceptoPago buscarObjeto(Long id, Concurso c) {
		Referencias referenciasPendiente = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "PENDIENTE");
		Referencias referenciasReservado = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "RESERVADO");
		String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
				+ "where pago.obj_codigo = 111 "
				+ "and pago.id_planta_cargo_det = " + id;
		if (c.getAdReferendum())
			cad += " and pago.estado = " + referenciasPendiente.getValorNum();
		if (!c.getAdReferendum())
			cad += " and pago.estado = " + referenciasReservado.getValorNum();

		List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
				PuestoConceptoPago.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	/**
	 * Método que setea todos los datos necesarios para luego actualizarlos.
	 * 
	 * @return
	 */
	public String actualizar() {

		if (puestosAsignados == null || puestosAsignados.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos un puesto para agrupar");
			return null;
		}
		if (orden.intValue() <= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El numero de Orden debe ser mayor a 0. Verifique");
			return null;
		}

		if (exiteNroOrden("update")) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El numero de Orden ingresado ya existe. Verifique");
			return null;
		}
		try {
			puestoAgr.setCodGrupo(codigo);
			puestoAgr.setDescripcionGrupo(denominacion);
			puestoAgr.setObservacion(observacion);
			if (!orden.equals(puestoAgr.getNroOrden())) {
				if (!existeOrden())
					puestoAgr.setNroOrden(orden);
				else {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El orden ingresado ya existe o no es valido");
					return null;
				}
			}

			puestoAgr.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoAgr.setFechaAlta(new Date());
			em.persist(puestoAgr);
			concursoPuestoAgrHome.setInstance(puestoAgr);

			String mensaje = "";
			try {

				for (ConcursoPuestoDet agr : puestosAsignadosAux) {
					Boolean esta = false;
					for (ConcursoPuestoDet c : puestosAsignados) {
						if (c.getIdConcursoPuestoDet() != null
								&& c.getIdConcursoPuestoDet().equals(
										agr.getIdConcursoPuestoDet())) {
							esta = true;
						}
					}
					if (!esta) {
						List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
						lista = buscarConcurso();
						for (ConcursoPuestoDet l : lista) {
							l.setConcursoPuestoAgr(null);
							l.setEstadoDet(buscarEstado("en reserva"));
							em.merge(l);
							PlantaCargoDet planta = new PlantaCargoDet();
							planta = l.getPlantaCargoDet();
							planta.setEstadoDet(buscarEstado("en reserva"));
							em.merge(planta);
						}

					}
				}

				Long idCpt = null;
				String categoria = null;
				for (ConcursoPuestoDet c : puestosAsignados) {

					Boolean cumple = false;
					if (c.getPlantaCargoDet().getContratado()) {
						if (c.getPlantaCargoDet().getCpt() != null) {
							if (idCpt == null) {
								idCpt = c.getPlantaCargoDet().getCpt().getId();
							}

							if (c.getPlantaCargoDet().getCpt().getId()
									.longValue() == idCpt.longValue()) {
								cumple = true;
							}
						}
					}

					if (c.getPlantaCargoDet().getPermanente()) {
						if (c.getPlantaCargoDet().getCpt() != null) {
							if (c.getPlantaCargoDet().getCpt() != null) {
								if (idCpt == null) {
									idCpt = c.getPlantaCargoDet().getCpt()
											.getId();
								}

								if (c.getPlantaCargoDet().getCpt().getId()
										.longValue() == idCpt.longValue()) {

									PuestoConceptoPago puestoConceptoPago = buscarObjeto(
											c.getPlantaCargoDet()
													.getIdPlantaCargoDet(),
											c.getConcurso());
									if (puestoConceptoPago != null) {
										if (categoria == null) {
											categoria = puestoConceptoPago
													.getCategoria();
										}

										if (categoria == null
												|| categoria
														.equals(puestoConceptoPago
																.getCategoria())) {
											cumple = true;
										}
									}
								}
							}
						}
					}

					if (cumple) {
						c.setNroOrden(3);
						c.setEstadoDet(buscarEstado("agrupado"));
						c.setConcursoPuestoAgr(concursoPuestoAgrHome
								.getInstance());
						if (c.getConcursoPuestoAgr() == null)
							em.persist(c);
						else
							em.merge(c);

					} else {
						mensaje += " " + c.getPlantaCargoDet().getDescripcion();

					}
					PlantaCargoDet planta = new PlantaCargoDet();
					planta = c.getPlantaCargoDet();
					planta.setEstadoDet(buscarEstado("agrupado"));
					em.merge(planta);

				}
				if (!mensaje.trim().isEmpty()) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"Los puestos: "
											+ mensaje
											+ " no pueden ser agrupados, no cumplen las condiciones");
					concursoPuestoAgrHome.setInstance(null);
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (mensaje.trim().isEmpty()) {
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			}
			operacion = "volver";
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ComisionGrupo> searchComisionGrupo() {
		String sql = "select gr.* " + "from seleccion.comision_grupo gr "
				+ "where gr.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr();
		return em.createNativeQuery(sql, ComisionGrupo.class).getResultList();
	}

	/**
	 * Método que elimina el grupo actual
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String eliminar() {
		try {
			List<ComisionGrupo> listaGr = searchComisionGrupo();
			if (listaGr != null && listaGr.size() > 0) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El grupo no puede eliminarse, ya cuenta con comisión de selección");

				return "ok";
			}

			String sqlDoc = "select doc.* " + "from general.documentos doc "
					+ "where doc.nombre_tabla = 'ConcursoPuestoAgr' "
					+ "and doc.id_tabla = "
					+ puestoAgr.getIdConcursoPuestoAgr();

			List<Documentos> listaDocumentos = new ArrayList<Documentos>();
			listaDocumentos = em.createNativeQuery(sqlDoc, Documentos.class)
					.getResultList();
			List<ConcursoPuestoDet> listaDet = new ArrayList<ConcursoPuestoDet>();
			listaDet = puestoAgr.getConcursoPuestoDets();
			for (Documentos doc : listaDocumentos) {
				em.remove(doc);

			}
			if (listaDet.size() > 0) {
				for (ConcursoPuestoDet d : listaDet) {
					PlantaCargoDet planta = d.getPlantaCargoDet();
					planta.setEstadoDet(buscarEstado("libre"));
					em.merge(planta);

					em.remove(d);
				}

			}
			try {
				em.remove(puestoAgr);
				em.flush();
			} catch (Exception e) {
				// TODO: handle exception
			}

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> buscarConcurso() {
		String cad = " select puesto_det.* "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det "
				+ "join planificacion.estado_cab  "
				+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab "
				+ "where puesto_det.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr()
				+ " and lower(estado_det.descripcion) = 'agrupado' "
				+ "and lower(estado_cab.descripcion) = 'concurso'";
		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		lista = em.createNativeQuery(cad, ConcursoPuestoDet.class)
				.getResultList();
		return lista;
	}

	/**
	 * Busca el id correspondiente a un estado
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private EstadoDet buscarEstado(String estado) {
		String cad = "select det.* from planificacion.estado_cab cab "
				+ "join planificacion.estado_det det  "
				+ "on det.id_estado_cab = cab.id_estado_cab "
				+ "where lower(cab.descripcion)='concurso' "
				+ "and lower(det.descripcion)='" + estado + "'";
		List<EstadoDet> lista = new ArrayList<EstadoDet>();
		lista = em.createNativeQuery(cad, EstadoDet.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	/**
	 * Método que busca el máximo orden correspondiente a un concurso
	 * determinado
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer valorMaximoOrden() {
		String cad = "select max(puesto_agr.nro_orden) as valor from "
				+ "seleccion.concurso_puesto_agr puesto_agr "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = puesto_agr.id_concurso "
				+ "where concurso.id_concurso = " + concurso.getIdConcurso();

		List<Object[]> config = em.createNativeQuery(cad).getResultList();
		if (config == null) {
			return 0;
		}
		Object obj1 = config.get(0);
		if (obj1 == null)
			return 0;
		String v = obj1.toString();

		return new Integer(v);
	}

	private Integer valorMaximoCodigo() {
		String cad = "select agr.* "
				+ "from seleccion.concurso_puesto_agr agr "
				+ "join seleccion.concurso concurso  "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where concurso.id_concurso = " + concurso.getIdConcurso();
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		Integer valorMax = 0;
		for (ConcursoPuestoAgr ag : lista) {
			
			try {
				Integer valorActual = new Integer(ag.getCodGrupo());
				if(valorActual.intValue() > valorMax.intValue())
					valorMax = valorActual.intValue();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		valorMax = valorMax.intValue() + 1;
		return valorMax;

	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConcursoPuestoAgr getPuestoAgr() {
		return puestoAgr;
	}

	public void setPuestoAgr(ConcursoPuestoAgr puestoAgr) {
		this.puestoAgr = puestoAgr;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Long getIdNivelEntidad() {
		return idNivelEntidad;
	}

	public void setIdNivelEntidad(Long idNivelEntidad) {
		this.idNivelEntidad = idNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Long getIdConfiguracionUoCab() {
		return idConfiguracionUoCab;
	}

	public void setIdConfiguracionUoCab(Long idConfiguracionUoCab) {
		this.idConfiguracionUoCab = idConfiguracionUoCab;
	}

	public List<ConcursoPuestoDet> getPuestosSinAsignar() {
		return puestosSinAsignar;
	}

	public void setPuestosSinAsignar(List<ConcursoPuestoDet> puestosSinAsignar) {
		this.puestosSinAsignar = puestosSinAsignar;
	}

	public List<ConcursoPuestoDet> getPuestosAsignados() {
		return puestosAsignados;
	}

	public void setPuestosAsignados(List<ConcursoPuestoDet> puestosAsignados) {
		this.puestosAsignados = puestosAsignados;
	}

	public List<ConcursoPuestoAgr> getPuestosAgrupados() {
		return puestosAgrupados;
	}

	public void setPuestosAgrupados(List<ConcursoPuestoAgr> puestosAgrupados) {
		this.puestosAgrupados = puestosAgrupados;
	}

	public List<ConcursoPuestoAgr> getPuestosAgrupadosAux() {
		return puestosAgrupadosAux;
	}

	public void setPuestosAgrupadosAux(
			List<ConcursoPuestoAgr> puestosAgrupadosAux) {
		this.puestosAgrupadosAux = puestosAgrupadosAux;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getFromConcurso() {
		if (fromConcurso == null || "".equals(fromConcurso))
			return "/home.xhtml";

		if (fromConcurso.contains(".seam")) {
			if (!fromConcurso.startsWith("/"))
				return "/" + fromConcurso;
			else
				return fromConcurso;
		}

		if (fromConcurso.contains("?")) {
			if (!fromConcurso.startsWith("/"))
				return "/"
						+ fromConcurso.substring(0, fromConcurso.indexOf("?"))
						+ ".seam"
						+ fromConcurso.substring(fromConcurso.indexOf("?"),
								fromConcurso.length());
			else
				return fromConcurso.substring(0, fromConcurso.indexOf("?"))
						+ ".seam"
						+ fromConcurso.substring(fromConcurso.indexOf("?"),
								fromConcurso.length());
		}

		if (!fromConcurso.contains(".xhtml")) {
			if (!fromConcurso.startsWith("/"))
				return "/" + fromConcurso + ".xhtml";
			else
				return fromConcurso + ".xhtml";
		}
		return fromConcurso;
	}

	public void setFromConcurso(String fromConcurso) {
		this.fromConcurso = fromConcurso;
	}

	public Long getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(Long idAgr) {
		this.idAgr = idAgr;
	}

	public List<ConcursoPuestoDet> getPuestosAsignadosAux() {
		return puestosAsignadosAux;
	}

	public void setPuestosAsignadosAux(
			List<ConcursoPuestoDet> puestosAsignadosAux) {
		this.puestosAsignadosAux = puestosAsignadosAux;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public Integer getNroActividad() {
		return nroActividad;
	}

	public void setNroActividad(Integer nroActividad) {
		this.nroActividad = nroActividad;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

}
