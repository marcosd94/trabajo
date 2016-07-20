package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.entity.AntecedenteGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.DetReqMinHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("requisitosMinimosFormController")
public class RequisitosMinimosFormController implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	DetReqMinHome detReqMinHome;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private AntecedenteGrupo antecedenteGrupo;
	private String mensaje;
	private String cu;
	private String fromCU;
	private Boolean habilitar;// = true;
	
	private List<DetReqMin>listaLink7Aux=new ArrayList<DetReqMin>();
	private List <RequisitosMinimosDTO>listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();

	private List<RequisitosMinimosDTO> listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
	private List<RequisitoMinimoCpt> listaValoracion = new ArrayList<RequisitoMinimoCpt>();
	private List<DetReqMin> listaRequisitosAux = new ArrayList<DetReqMin>();
	private List<DetMinimosRequeridos> listaDetMinimosInactivar = new ArrayList<DetMinimosRequeridos>();
	private List<DetOpcionesConvenientes> listaDetOpcInactivar = new ArrayList<DetOpcionesConvenientes>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concurso = new Concurso();

		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = concursoPuestoAgr.getConcurso();
		validarOee(concurso);
		habEdit();
		buscarDatosAsociadosUsuario();
		listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
		listaRequisitosAux = new ArrayList<DetReqMin>();
		//buscarRequerimientosMinimos();
		 buscarRequerimientosMinimosEdit();
		habilitarPantalla();
	}
	
	private void habilitarPantalla(){
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			Referencias refPendiente = new Referencias();
			refPendiente = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue())
				habilitar = true;
			if(concursoPuestoAgr.getEstado().intValue() == refAjuste.getValorNum().intValue())
				habilitar = false;
			
			if(concursoPuestoAgr.getEstado().intValue() == refIniciado.getValorNum().intValue())
				habilitar = false;
		}

	}

	/**
	 * Para el caso de editar No permitir editar registros, bloquear las cajas
	 * de textos
	 **/
	public boolean habEdit() {
		if ((fromCU != null && fromCU
				.equals("admPerfilMatrizHomologInstitucion_list"))||tienePerfilHomologado()) {
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	private Boolean tienePerfilHomologado() {
		if (concursoPuestoAgr.getHomologar() != null
				&& concursoPuestoAgr.getHomologar())
			return false;
		String sql = "select perfil.*  "
				+ "from seleccion.homologacion_perfil_matriz perfil "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_homologacion = perfil.id_homologacion "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<HomologacionPerfilMatriz> listaMatX = new ArrayList<HomologacionPerfilMatriz>();

		listaMatX = em.createNativeQuery(sql, HomologacionPerfilMatriz.class)
				.getResultList();
		if (listaMatX.size() == 0)
			return false;
		return true;
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			Entidad entidad = new Entidad();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracion() {
		String cadena = "select cpt.* from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo is TRUE order by cpt.orden asc";
		listaValoracion = em
				.createNativeQuery(cadena, RequisitoMinimoCpt.class)
				.getResultList();
	}
	public List<EscalaReqMin> traerEscalaReqMins(Long idRequisitoMinimoCpt){
		Query q = em.createQuery("select EscalaReqMin from EscalaReqMin EscalaReqMin " +
				" where EscalaReqMin.requisitoMinimoCpt.idRequisitoMinimoCpt = :idRequisitoMinimoCpt " +
				" and EscalaReqMin.activo is true order by desde asc, hasta asc ");
		q.setParameter("idRequisitoMinimoCpt", idRequisitoMinimoCpt);
		List<EscalaReqMin> lista =q.getResultList();
		return lista;
	}
	
	
	
	@SuppressWarnings("unused")
	private void buscarRequerimientosMinimosEdit() {
		String cad =
			"select * from planificacion.det_req_min det_req"
				+ " where det_req.id_planta_cargo_det = (select min(id_planta_cargo_det)  "
				+ "from seleccion.concurso_puesto_det where id_concurso_puesto_agr = "
				+concursoPuestoAgr.getIdConcursoPuestoAgr()+" )";
		
		listaLink7Aux = new ArrayList<DetReqMin>();
		listaLink7Aux = em.createNativeQuery(cad, DetReqMin.class).getResultList();
		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();
		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaLink7Aux) {
				RequisitoMinimoCpt reqCptActual = new RequisitoMinimoCpt();
				reqCptActual = det.getRequisitoMinimoCpt();
				if (reqCptActual != null
					&& reqCptActual.getIdRequisitoMinimoCpt().equals(req.getIdRequisitoMinimoCpt())) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv =
						new ArrayList<DetOpcionesConvenientes>();
					listaConv = buscarOpciones(det.getIdDetReqMin());
					Integer tamconv = listaConv.size();

					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);
					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
					listaReq = buscarMinimos(det.getIdDetReqMin());
					Integer tamreqmin = listaReq.size();

					DetMinimosRequeridos r = new DetMinimosRequeridos();
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					listaDtoLink7.add(dto);
				}
			}
			if (!esta) {
				RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
				dto.setRequisitoMinimoCpt(req);
				List<DetOpcionesConvenientes> listaConv = new ArrayList<DetOpcionesConvenientes>();
				DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
				listaConv.add(conv);
				dto.setListaDetOpcionesConvenientes(listaConv);
				List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
				DetMinimosRequeridos r = new DetMinimosRequeridos();
				listaReq.add(r);
				dto.setListaDetMinimosRequeridos(listaReq);
				listaDtoLink7.add(dto);
			}
		}
	}

	/**
	 * Busca datos para editar el Link07
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void buscarRequerimientosMinimos() {
		String cad = "select * from planificacion.det_req_min det_req"
				+ " where det_req.activo is true and det_req.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and det_req.tipo = 'GRUPO'";
		listaRequisitosAux = new ArrayList<DetReqMin>();
		listaRequisitosAux = em.createNativeQuery(cad, DetReqMin.class)
				.getResultList();

		// Si no hay datos del grupo, se debe buscar por el CPT
		if (listaRequisitosAux == null || listaRequisitosAux.size() == 0) {
			if (cu != null && cu.equals("404"))
				listaRequisitosAux = cargarRequerimientosMinimosDelCpt();
		}

		String cadena = "select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";

		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();
		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class)
				.getResultList();
		listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			Boolean agregado = false;
			Long id2 = req.getIdRequisitoMinimoCpt();
			Integer i = 0;
			while (i < listaRequisitosAux.size()) {
				DetReqMin det = new DetReqMin();
				det = listaRequisitosAux.get(i);
				Long id1 = det.getRequisitoMinimoCpt()
						.getIdRequisitoMinimoCpt();
				if (id1.equals(id2)) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv = new ArrayList<DetOpcionesConvenientes>();

					if (det.getIdDetReqMin() == null
							&& det.getDetOpcionesConvenienteses() != null)
						listaConv = det.getDetOpcionesConvenienteses();// Nuevo
					else
						listaConv = buscarOpciones(det.getIdDetReqMin());// Edicion

					Integer tamconv = listaConv.size();
					// if (!agregado) {
					agregado = true;
					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					conv.setActivo(true);
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);

					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();

					if (det.getIdDetReqMin() == null
							&& det.getDetMinimosRequeridoses() != null)
						listaReq = det.getDetMinimosRequeridoses();// Nuevo
					else
						listaReq = buscarMinimos(det.getIdDetReqMin());// Edicion

					Integer tamreqmin = listaReq.size();

					DetMinimosRequeridos r = new DetMinimosRequeridos();
					r.setActivo(true);
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					// }
					listaRequisitosDTO.add(dto);
					// i = listaRequisitosAux.size();
				}
				i++;
			}

			if (!esta) {
				RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
				dto.setRequisitoMinimoCpt(req);
				List<DetOpcionesConvenientes> listaConv = new ArrayList<DetOpcionesConvenientes>();
				DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
				conv.setActivo(true);
				listaConv.add(conv);
				dto.setListaDetOpcionesConvenientes(listaConv);
				List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
				DetMinimosRequeridos r = new DetMinimosRequeridos();
				r.setActivo(true);
				listaReq.add(r);
				dto.setListaDetMinimosRequeridos(listaReq);
				listaRequisitosDTO.add(dto);
			}
		}
	}

	private List<DetReqMin> cargarRequerimientosMinimosDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DetReqMin> listaAux = new ArrayList<DetReqMin>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_req_min det "
					+ " where det.id_cpt = " + cpt.getId()
					+ " and det.activo= true";

			List<DetReqMin> lista = new ArrayList<DetReqMin>();
			lista = em.createNativeQuery(cad, DetReqMin.class).getResultList();

			if (lista != null) {
				for (DetReqMin detReqMin : lista) {
					DetReqMin nuevo = new DetReqMin();
					nuevo.setActivo(true);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setRequisitoMinimoCpt(detReqMin
							.getRequisitoMinimoCpt());
					nuevo.setPuntajeReqMin(detReqMin.getPuntajeReqMin());
					nuevo.setTipo("GRUPO");

					// Se copian los detalles de minimos
					List<DetMinimosRequeridos> listaDesc = buscarMinimos(detReqMin
							.getIdDetReqMin());
					if (listaDesc != null) {
						List<DetMinimosRequeridos> listaDetalles = new ArrayList<DetMinimosRequeridos>();
						for (DetMinimosRequeridos detMinimosRequeridos : listaDesc) {
							DetMinimosRequeridos nuevoDetalle = new DetMinimosRequeridos();
							nuevoDetalle.setActivo(detMinimosRequeridos
									.getActivo());
							nuevoDetalle
									.setMinimosRequeridos(detMinimosRequeridos
											.getMinimosRequeridos());
							nuevoDetalle.setDetReqMin(nuevo);
							listaDetalles.add(nuevoDetalle);

						}
						nuevo.setDetMinimosRequeridoses(listaDetalles);
					}

					// Se copian los detalles de opciones convenientes
					List<DetOpcionesConvenientes> listaOpciones = buscarOpciones(detReqMin
							.getIdDetReqMin());
					if (listaDesc != null) {
						List<DetOpcionesConvenientes> listaDetalles = new ArrayList<DetOpcionesConvenientes>();
						for (DetOpcionesConvenientes detOpcionesConvenientes : listaOpciones) {
							DetOpcionesConvenientes nuevoDetalle = new DetOpcionesConvenientes();
							nuevoDetalle.setActivo(detOpcionesConvenientes
									.getActivo());
							nuevoDetalle
									.setOpcConvenientes(detOpcionesConvenientes
											.getOpcConvenientes());
							nuevoDetalle.setDetReqMin(nuevo);
							listaDetalles.add(nuevoDetalle);

						}
						nuevo.setDetOpcionesConvenienteses(listaDetalles);
					}

					listaAux.add(nuevo);
				}
			}
		}

		return listaAux;
	}

	@SuppressWarnings("unchecked")
	private List<DetOpcionesConvenientes> buscarOpciones(Long id) {
		String cad = "select opc.* from planificacion.det_opciones_convenientes opc"
				+ " where opc.activo is true and opc.id_det_req_min = " + id;

		return em.createNativeQuery(cad, DetOpcionesConvenientes.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<DetMinimosRequeridos> buscarMinimos(Long id) {
		String cad = "select min.* from planificacion.det_minimos_requeridos min"
				+ " where min.activo is true and min.id_det_req_min = " + id;

		return em.createNativeQuery(cad, DetMinimosRequeridos.class)
				.getResultList();
	}

	/**
	 * Método que agrega un registro a la sublista de Minimos requeridos de la
	 * lista gral
	 * 
	 * @param row
	 */
	public void agregarListaMinimos(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaRequisitosDTO.get(row);

		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet = dto.getListaDetMinimosRequeridos();
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		det.setActivo(true);
		listaDet.add(det);
		listaRequisitosDTO.set(row, dto);

	}

	public void eliminarListaMinimos(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaRequisitosDTO.get(row);
		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet.addAll(dto.getListaDetMinimosRequeridos());
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		if (det.getIdMinimosRequeridos() != null)
			listaDetMinimosInactivar.add(det);
		dto.setListaDetMinimosRequeridos(listaDet);
		listaRequisitosDTO.set(row, dto);

	}

	/**
	 * Método que agrega un registro a la sublista de Opciones Convenientes de
	 * la lista gral
	 * 
	 * @param row
	 */
	public void agregarListaOpcConvenientes(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaRequisitosDTO.get(row);
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		det.setActivo(true);
		dto.getListaDetOpcionesConvenientes().add(det);
		listaRequisitosDTO.set(row, dto);
	}

	public void eliminarListaOpcConvenientes(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaRequisitosDTO.get(row);
		List<DetOpcionesConvenientes> listaDet = new ArrayList<DetOpcionesConvenientes>();
		listaDet.addAll(dto.getListaDetOpcionesConvenientes());
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		if (det.getIdDetOpcionesConvenientes() != null)
			listaDetOpcInactivar.add(det);
		dto.setListaDetOpcionesConvenientes(listaDet);
		listaRequisitosDTO.set(row, dto);
	}

	private Boolean validacionEscala() {
		for (RequisitosMinimosDTO dto : listaRequisitosDTO) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto)) {
					return false;
				}
			}
		}
		return true;
	}

	public Boolean verificarValoracion(RequisitosMinimosDTO requisito) {
		List<EscalaReqMin> v = new ArrayList<EscalaReqMin>();
		v = requisito.getRequisitoMinimoCpt().getEscalaReqMins();
		Float valor = requisito.getPuntaje();
		Boolean cumple = false;
		for (EscalaReqMin o : v) {
			Float desde = new Float("" + o.getDesde());
			Float hasta = new Float("" + o.getHasta());
			if (desde.floatValue() <= valor.floatValue()
					&& hasta.floatValue() >= valor.floatValue()) {
				cumple = true;
			}
		}
		if (!cumple) {
			mensaje = "El puntaje ingresado no es valido, verifique la escala";
			return cumple;
		}
		mensaje = null;
		return cumple;
	}

	public String guardar() {
		if (!validacionEscala())
			return null;
		try {
			for (RequisitosMinimosDTO dto : listaRequisitosDTO) {
				if (dto.getPuntaje() != null) {
					DetReqMin detReqMin = new DetReqMin();
					detReqMin
							.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
					detReqMin.setPuntajeReqMin(dto.getPuntaje());
					detReqMin.setTipo("GRUPO");
					detReqMin.setConcursoPuestoAgr(concursoPuestoAgr);
					detReqMin.setActivo(true);
					detReqMinHome.setInstance(detReqMin);
					String resultado = detReqMinHome.persist();
					if (resultado.equals("persisted")) {
						List<DetMinimosRequeridos> listaDetMinimos = new ArrayList<DetMinimosRequeridos>();
						listaDetMinimos = dto.getListaDetMinimosRequeridos();
						for (DetMinimosRequeridos detMin : listaDetMinimos) {
							if (detMin.getMinimosRequeridos() != null
									&& !detMin.getMinimosRequeridos()
											.equals("")) {
								detMin.setMinimosRequeridos(detMin
										.getMinimosRequeridos().trim()
										.toUpperCase());
								detMin.setDetReqMin(detReqMinHome.getInstance());
								em.persist(detMin);
							}
						}
						em.flush();
						List<DetOpcionesConvenientes> listaDetOpc = new ArrayList<DetOpcionesConvenientes>();
						listaDetOpc = dto.getListaDetOpcionesConvenientes();
						for (DetOpcionesConvenientes detOpc : listaDetOpc) {
							if (detOpc.getOpcConvenientes() != null
									&& !detOpc.getOpcConvenientes().equals("")) {
								detOpc.setOpcConvenientes(detOpc
										.getOpcConvenientes().trim()
										.toUpperCase());
								detOpc.setDetReqMin(detReqMinHome.getInstance());
								em.persist(detOpc);
							}
						}
						em.flush();
						detReqMinHome.clearInstance();
					}
				}
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public String updated() {
		if (!validacionEscala())
			return null;
		try {
			for (RequisitosMinimosDTO dto : listaRequisitosDTO) {
				if (dto.getId() != null) {
					DetReqMin detReqMin = new DetReqMin();
					detReqMin = em.find(DetReqMin.class, dto.getId());
					detReqMin.setPuntajeReqMin(dto.getPuntaje());
					em.merge(detReqMin);
					detReqMinHome.setInstance(detReqMin);
					// String resultado = detReqMinHome.update();

					List<DetMinimosRequeridos> listaMinimos = new ArrayList<DetMinimosRequeridos>();
					listaMinimos = dto.getListaDetMinimosRequeridos();
					for (DetMinimosRequeridos min : listaMinimos) {
						if (min.getMinimosRequeridos() != null
								&& !min.getMinimosRequeridos().equals("")) {
							min.setMinimosRequeridos(min.getMinimosRequeridos()
									.trim().toUpperCase());
							min.setDetReqMin(detReqMinHome.getInstance());
							if (min.getIdMinimosRequeridos() == null)
								em.persist(min);
							else
								min = em.merge(min);
							// em.flush();
						}
					}

					List<DetOpcionesConvenientes> listaOpc = new ArrayList<DetOpcionesConvenientes>();
					listaOpc = dto.getListaDetOpcionesConvenientes();
					for (DetOpcionesConvenientes op : listaOpc) {

						if (op.getOpcConvenientes() != null
								&& !op.getOpcConvenientes().equals("")) {
							op.setOpcConvenientes(op.getOpcConvenientes()
									.trim().toUpperCase());
							op.setDetReqMin(detReqMinHome.getInstance());
							if (op.getIdDetOpcionesConvenientes() == null)
								em.persist(op);
							else
								em.merge(op);
						}

					}
					// em.flush();

				} else {
					if (dto.getPuntaje() != null) {
						DetReqMin detMin = new DetReqMin();
						detMin.setRequisitoMinimoCpt(dto
								.getRequisitoMinimoCpt());
						detMin.setConcursoPuestoAgr(concursoPuestoAgr);
						detMin.setTipo("GRUPO");
						detMin.setActivo(true);
						detMin.setPuntajeReqMin(dto.getPuntaje());

						em.persist(detMin);
						detReqMinHome.setInstance(detMin);
						// String resultado = detReqMinHome.persist();

						listaRequisitosAux.add(detReqMinHome.getInstance());
						// if (resultado.equals("persisted")) {
						List<DetMinimosRequeridos> listaDetMinimos = new ArrayList<DetMinimosRequeridos>();
						listaDetMinimos = dto.getListaDetMinimosRequeridos();
						for (DetMinimosRequeridos dMin : listaDetMinimos) {
							if (dMin.getMinimosRequeridos() != null
									&& !dMin.getMinimosRequeridos().equals("")) {
								dMin.setMinimosRequeridos(dMin
										.getMinimosRequeridos().trim()
										.toUpperCase());
								dMin.setDetReqMin(detReqMinHome.getInstance());

								em.persist(dMin);
							}
						}
						// em.flush();
						List<DetOpcionesConvenientes> listaDetOpc = new ArrayList<DetOpcionesConvenientes>();
						listaDetOpc = dto.getListaDetOpcionesConvenientes();
						for (DetOpcionesConvenientes detOpc : listaDetOpc) {
							if (detOpc.getOpcConvenientes() != null
									&& !detOpc.getOpcConvenientes().equals("")) {
								detOpc.setOpcConvenientes(detOpc
										.getOpcConvenientes().trim()
										.toUpperCase());
								detOpc.setDetReqMin(detReqMinHome.getInstance());
								em.persist(detOpc);
							}
						}
						// em.flush();
						// }
					}
					detReqMinHome.clearInstance();
				}
			}

			for (DetMinimosRequeridos dMin : listaDetMinimosInactivar) {
				dMin.setActivo(false);
				em.merge(dMin);
			}

			for (DetOpcionesConvenientes detOpc : listaDetOpcInactivar) {
				detOpc.setActivo(false);
				em.merge(detOpc);
			}

			// En concenso con el AF: pveron se decidio sacar la parte de
			// borrado fisico
			// for (DetReqMin detail : listaRequisitosAux) {
			// List<DetOpcionesConvenientes> listaAuxOpc = new
			// ArrayList<DetOpcionesConvenientes>();
			// listaAuxOpc.addAll(detail.getDetOpcionesConvenienteses());
			// List<DetMinimosRequeridos> listaAuxReqMin = new
			// ArrayList<DetMinimosRequeridos>();
			// listaAuxReqMin = getDetMinimosRequeridoses(detail);
			//
			// for (RequisitosMinimosDTO detailDTO : listaRequisitosDTO) {
			//
			// List<DetOpcionesConvenientes> listaDTOOpc = new
			// ArrayList<DetOpcionesConvenientes>();
			// listaDTOOpc.addAll(detailDTO.getListaDetOpcionesConvenientes());
			//
			// List<DetMinimosRequeridos> listaDTOReq = new
			// ArrayList<DetMinimosRequeridos>();
			// listaDTOReq.addAll(detailDTO.getListaDetMinimosRequeridos());
			// if (detail.getRequisitoMinimoCpt()
			// .getIdRequisitoMinimoCpt().longValue() ==
			// detailDTO.getRequisitoMinimoCpt()
			// .getIdRequisitoMinimoCpt().longValue()) {
			// for (DetOpcionesConvenientes opcAux : listaAuxOpc) {
			// Boolean esta = false;
			// for (DetOpcionesConvenientes opcDTO : listaDTOOpc) {
			// if (opcDTO.getIdDetOpcionesConvenientes() != null
			// && opcAux.getIdDetOpcionesConvenientes() != null) {
			// if (opcAux.getIdDetOpcionesConvenientes().longValue() ==
			// opcDTO.getIdDetOpcionesConvenientes().longValue()){
			// esta = true;
			// break;
			// }
			// }
			// }
			// if (!esta) {
			// try {
			// // em.remove(opcAux);
			// //em.flush();
			// } catch (Exception e) {
			// e.printStackTrace();
			// // TODO: handle exception
			// }
			// }
			//
			// }
			//
			// for (DetMinimosRequeridos reqAux : listaAuxReqMin) {
			// Boolean esta = false;
			// for (DetMinimosRequeridos reqDTO : listaDTOReq) {
			// if (reqDTO.getIdMinimosRequeridos() != null
			// && reqAux.getIdMinimosRequeridos() != null) {
			// if (reqAux.getIdMinimosRequeridos().longValue() ==
			// reqDTO.getIdMinimosRequeridos().longValue()){
			// esta = true;
			// break;
			// }
			//
			// }
			// }
			// if (!esta) {
			// try {
			// reqAux = em.find(DetMinimosRequeridos.class,
			// reqAux.getIdMinimosRequeridos());
			// //em.remove(reqAux);
			// //em.flush();
			// } catch (Exception e) {
			// e.printStackTrace();
			// // TODO: handle exception
			// }
			//
			// }
			// }
			// }
			// }
			// }

			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public List<DetMinimosRequeridos> getDetMinimosRequeridoses(
			DetReqMin detReqMin) {
		String consulta = ""
				+ " select detMinimosRequeridos from DetMinimosRequeridos detMinimosRequeridos "
				+ " join detMinimosRequeridos.detReqMin detReqMin "
				+ " where detReqMin.idDetReqMin = :idDetReqMin";

		Query q = em.createQuery(consulta);
		q.setParameter("idDetReqMin", detReqMin.getIdDetReqMin());
		List<DetMinimosRequeridos> lista = q.getResultList();
		return lista;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public AntecedenteGrupo getAntecedenteGrupo() {
		return antecedenteGrupo;
	}

	public void setAntecedenteGrupo(AntecedenteGrupo antecedenteGrupo) {
		this.antecedenteGrupo = antecedenteGrupo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public List<RequisitosMinimosDTO> getListaRequisitosDTO() {
		return listaRequisitosDTO;
	}

	public void setListaRequisitosDTO(
			List<RequisitosMinimosDTO> listaRequisitosDTO) {
		this.listaRequisitosDTO = listaRequisitosDTO;
	}

	public List<RequisitoMinimoCpt> getListaValoracion() {
		return listaValoracion;
	}

	public void setListaValoracion(List<RequisitoMinimoCpt> listaValoracion) {
		this.listaValoracion = listaValoracion;
	}

	public List<DetReqMin> getListaRequisitosAux() {
		return listaRequisitosAux;
	} 

	public void setListaRequisitosAux(List<DetReqMin> listaRequisitosAux) {
		this.listaRequisitosAux = listaRequisitosAux;
	}

	public List<DetOpcionesConvenientes> getListaDetOpcInactivar() {
		return listaDetOpcInactivar;
	}

	public void setListaDetOpcInactivar(
			List<DetOpcionesConvenientes> listaDetOpcInactivar) {
		this.listaDetOpcInactivar = listaDetOpcInactivar;
	}

	public List<DetMinimosRequeridos> getListaDetMinimosInactivar() {
		return listaDetMinimosInactivar;
	}

	public void setListaDetMinimosInactivar(
			List<DetMinimosRequeridos> listaDetMinimosInactivar) {
		this.listaDetMinimosInactivar = listaDetMinimosInactivar;
	}

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}
	
	public List<DetReqMin> getListaLink7Aux() {
		return listaLink7Aux;
	}

	public void setListaLink7Aux(List<DetReqMin> listaLink7Aux) {
		this.listaLink7Aux = listaLink7Aux;
	}
	public List<RequisitosMinimosDTO> getListaDtoLink7() {
		return listaDtoLink7;
	}

	public void setListaDtoLink7(List<RequisitosMinimosDTO> listaDtoLink7) {
		this.listaDtoLink7 = listaDtoLink7;
	}

}
