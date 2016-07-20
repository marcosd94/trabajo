package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.lang.descr.Restriction;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import py.com.excelsis.sicca.dto.ContenidoFuncionalDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizHome;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizList;
import py.com.excelsis.sicca.session.DetContenidoFuncionalHome;
import py.com.excelsis.sicca.session.DetReqMinHome;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admBancoPerfilesFormController")
public class AdmBancoPerfilesFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3123214343970641508L;

	ConfMatrizEvaluacionFormController matEvaluacionFormController;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	HomologacionPerfilMatrizList homologacionPerfilMatrizList;
	@In(create = true)
	HomologacionPerfilMatrizHome homologacionPerfilMatrizHome;

	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	@In(create = true)
	DetContenidoFuncionalHome detContenidoFuncionalHome;
	@In(create = true)
	DetReqMinHome detReqMinHome;

	private HomologacionPerfilMatriz homologacionPerfilMatriz;
	private Long idHomologacion;

	private float totalGraduacion = 0F;
	private float totalPuntajeMaximo = 0F;
	private String oficinas;

	private Long idConcursoPuestoAgr;

	private Integer rowSelected;

	private List<ContenidoFuncionalDTO> listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
	private List<DetContenidoFuncional> listaLink6Aux = new ArrayList<DetContenidoFuncional>();

	private List<RequisitosMinimosDTO> listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
	private List<DetReqMin> listaLink7Aux = new ArrayList<DetReqMin>();
	private List<MatrizReferencial> listaMatrizRefercial;

	private Boolean vista;
	private Boolean modoEditado;
	private Boolean modoVer;
	private String tipoOperacion;
	private String denominacion;
	private Boolean mostrarLinkEdit;
	private Integer idMatRefSel;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		funcionarioUtilFormController.setActivo(Estado.ACTIVO);
		search();
	}

	public void metodo() {
	}

	public void initTabF2() {

		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(MatrizReferencial.class);
		Criteria critMatRefConfEnc = crit.createCriteria("datosEspecificos",
				"datosEspe");
		crit.add(Restrictions.eq("activo", true));
		listaMatrizRefercial = crit.list();
		matEvaluacionFormController = (ConfMatrizEvaluacionFormController) Component
				.getInstance(ConfMatrizEvaluacionFormController.class, true);
		matEvaluacionFormController
				.setListaMatrizRefercial(listaMatrizRefercial);
		matEvaluacionFormController.setTipoOperacion(tipoOperacion);
		matEvaluacionFormController.setModoEditado(modoEditado);
		if (modoEditado) {
			matEvaluacionFormController.cargarListaSelEditar(idHomologacion);
		}
		matEvaluacionFormController
				.setListaMatrizRefercial(listaMatrizRefercial);
	}

	public void cargarListaSelCrear() throws CloneNotSupportedException {
		matEvaluacionFormController.setIndiceSeleccionado(idMatRefSel);
		matEvaluacionFormController.cargarListaSelCrear();
	}

	public void searchAll() {
		funcionarioUtilFormController.clear();

		funcionarioUtilFormController.init();
		funcionarioUtilFormController.setActivo(Estado.ACTIVO);
		denominacion = null;
		homologacionPerfilMatrizList.limpiarCU357();
	}

	public void search() {
		homologacionPerfilMatrizList.setIdTipoCpt(funcionarioUtilFormController
				.getIdTipoCpt());
		homologacionPerfilMatrizList.setIdCpt(funcionarioUtilFormController
				.getCpt().getId());
		if (denominacion != null && !denominacion.trim().isEmpty())
			homologacionPerfilMatrizList.setDenominacion(denominacion);

		if (funcionarioUtilFormController.getActivo() != null
				&& funcionarioUtilFormController.getActivo().getId() != null)
			homologacionPerfilMatrizList
					.setActivo(funcionarioUtilFormController.getActivo()
							.getValor());

		homologacionPerfilMatrizList.listaResultadosCU357();
	}

	public void verPerfilHomologado() {
		if (idHomologacion == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Perfil no válido.");
		} else {
			homologacionPerfilMatriz = em.find(HomologacionPerfilMatriz.class,
					idHomologacion);

		}

	}

	private void limpiarEdit() {
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		listaLink6Aux = new ArrayList<DetContenidoFuncional>();

		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		listaLink7Aux = new ArrayList<DetReqMin>();
	}

	public String getUrlToCPT() {
		if (funcionarioUtilFormController.getTipoEspGeneral() == null
				|| (funcionarioUtilFormController.getTipoEspGeneral().getId() == null)) {
			statusMessages.add(Severity.INFO,
					"Debe seleccionar si es General o Específico.");
			return "";
		}
		if (funcionarioUtilFormController.getIdTipoCpt() != null) {
			return "/planificacion/cpt/CptList.xhtml?tipoCpt="
					+ funcionarioUtilFormController.getIdTipoCpt()
					+ "&tipo="
					+ funcionarioUtilFormController.getTipoEspGeneral()
							.getValor();
		}
		return "/planificacion/cpt/CptList.xhtml?tipo="
				+ funcionarioUtilFormController.getTipoEspGeneral().getValor();
	}

	public Boolean verificarPerfilUsado(Long idHomoPerMat) {
		Query q = em
				.createQuery("select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
						+ "where concursoPuestoAgr.activo is true "
						+ " and concursoPuestoAgr.homologacionPerfilMatriz.idHomologacion = "
						+ idHomoPerMat);
		List<ConcursoPuestoAgr> lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		}
		return false;
	}

	public void edit() {
		limpiarEdit();
		// funcionarioUtilFormController.clear();
		if (isNew()) {
			// nuevo
			// clearEdit();
			homologacionPerfilMatriz = new HomologacionPerfilMatriz();
			homologacionPerfilMatriz.setActivo(true);
			// funcionarioUtilFormController.setCpt(null);
		} else {
			/* Inicio Incidencia 593 */
			mostrarLinkEdit = verificarPerfilUsado(idHomologacion);
			/* End Incidencia 593 */
			// edicion
			homologacionPerfilMatriz = em.find(HomologacionPerfilMatriz.class,
					idHomologacion);
			funcionarioUtilFormController.setCpt(homologacionPerfilMatriz
					.getCpt());
			funcionarioUtilFormController.setIdCpt(homologacionPerfilMatriz
					.getCpt().getId());
			funcionarioUtilFormController.setIdTipoCpt(homologacionPerfilMatriz
					.getCpt().getTipoCpt().getId());
		}

		homologacionPerfilMatrizHome.setInstance(homologacionPerfilMatriz);
		buscarContenidoFuncionalEdit();
		buscarRequerimientosMinimosEdit();

		rowSelected = -1;
	}

	public boolean isNew() {
		if (idHomologacion == null)
			return true;

		return false;
	}

	private Date calcFechaVencimientoLimite() {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ "where Referencias.activo is true and Referencias.dominio = 'BANCO_PERFILES'");
		List<Referencias> lista = q.getResultList();
		if (lista.size() == 1) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, lista.get(0).getValorNum());
			return cal.getTime();
		}
		return null;

	}

	public String save() throws Exception {
		if (!validate()) {
			return null;
		}

		homologacionPerfilMatriz.setCpt(funcionarioUtilFormController.getCpt());
		homologacionPerfilMatriz
				.setConfiguracionUoCab(new ConfiguracionUoCab());
		homologacionPerfilMatriz.getConfiguracionUoCab().setIdConfiguracionUo(
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());

		if (isNew()) {
			// Crear
			homologacionPerfilMatriz.setFechaAlta(new Date());
			homologacionPerfilMatriz.setUsuAlta(usuarioLogueado
					.getCodigoUsuario());
			/* Inicio Incidencia 593 */
			homologacionPerfilMatriz.setEstado("CARGA INICIAL");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			homologacionPerfilMatriz
					.setAnio(new Integer(sdf.format(new Date())));
			Date fecVenLimit = calcFechaVencimientoLimite();
			homologacionPerfilMatriz.setFechaVencimiento(fecVenLimit);
			homologacionPerfilMatriz.setFechaLimite(fecVenLimit);
			homologacionPerfilMatriz
					.setConfiguracionUoCab(new ConfiguracionUoCab());
			homologacionPerfilMatriz.getConfiguracionUoCab()
					.setIdConfiguracionUo(
							usuarioLogueado.getConfiguracionUoCab()
									.getIdConfiguracionUo());
			homologacionPerfilMatriz.setTipo("BANCO");
			/* Fin Incidencia 593 */
			em.persist(homologacionPerfilMatriz);
			// Tab2
			matEvaluacionFormController
					.setIdHomologacion(homologacionPerfilMatriz
							.getIdHomologacion());
			matEvaluacionFormController.guardar();
		} else {
			if (verificarPerfilUsado(idHomologacion)) {
				statusMessages
						.add(Severity.ERROR,
								"El Perfil ya está siendo utilizado. No se puede realizar actualización.");
				return "fail";
			}
			// Editar
			homologacionPerfilMatriz.setFechaMod(new Date());
			homologacionPerfilMatriz.setUsuMod(usuarioLogueado
					.getCodigoUsuario());
			em.merge(homologacionPerfilMatriz);
			// Tab2
			matEvaluacionFormController.update().equalsIgnoreCase("updated");

		}

		guardarLink6();
		guardarLink7();

		em.flush();

		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		searchAll();
		return "ok";
	}

	/**
	 * Busca datos del Link6
	 */
	// @SuppressWarnings("unchecked")
	// private void buscarContenidoFuncional() {
	// String cad =
	// "select * from planificacion.det_contenido_funcional cont_funcional"
	// + " where cont_funcional.id_cpt = " + idCpt;
	// List<DetContenidoFuncional> listaAux = new
	// ArrayList<DetContenidoFuncional>();
	// listaAux = em.createNativeQuery(cad,
	// DetContenidoFuncional.class).getResultList();
	//
	// String cadena =
	// "select * from planificacion.contenido_funcional funcional"
	// + " where funcional.activo = TRUE order by funcional.orden";
	// listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
	// List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();
	//
	// lista = em.createNativeQuery(cadena,
	// ContenidoFuncional.class).getResultList();
	//
	// for (ContenidoFuncional contenido : lista) {
	// Boolean esta = false;
	// for (DetContenidoFuncional det : listaAux) {
	// if (det.getContenidoFuncional().getIdContenidoFuncional()
	// .equals(contenido.getIdContenidoFuncional())) {
	// esta = true;
	// ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
	// dto.setContenidoFuncional(det.getContenidoFuncional());
	// dto.setId(det.getIdDetContenidoFuncional());
	//
	// dto.setPuntaje(det.getPuntaje());
	// List<DetDescripcionContFuncional> listaDesc = new
	// ArrayList<DetDescripcionContFuncional>();
	// listaDesc = det.getDetDescripcionContFuncionals();
	// DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
	// listaDesc.add(descr);
	// dto.setListaDetDescripContFuncional(listaDesc);
	// listaDtoLink6.add(dto);
	// }
	// }
	// if (!esta) {
	// ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
	// dto.setContenidoFuncional(contenido);
	// List<DetDescripcionContFuncional> listaDesc = new
	// ArrayList<DetDescripcionContFuncional>();
	//
	// DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
	// listaDesc.add(descr);
	// dto.setListaDetDescripContFuncional(listaDesc);
	// listaDtoLink6.add(dto);
	// }
	// }
	// }

	/**
	 * Método que agrega un detalle a la sublista de la tabla
	 * 
	 * @param row
	 */
	public void agregarListaLink6(Integer row) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaDtoLink6.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet = dto.getListaDetDescripContFuncional();
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		listaDet.add(det);
		listaDtoLink6.set(row, dto);
	}

	/*********************************************************************
	 * Metodos que eliminan registros de sublistas
	 *********************************************************************/

	/**
	 * Busca datos para editar el Link06
	 */
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncionalEdit() {

		if (idHomologacion == null) {
			// Nuevo
			// Se debe copiar los datos del CPT
			String cad = "select * from planificacion.det_contenido_funcional cont_funcional"
					+ " where cont_funcional.id_cpt = "
					+ funcionarioUtilFormController.getIdCpt();

			List<DetContenidoFuncional> lista = em.createNativeQuery(cad,
					DetContenidoFuncional.class).getResultList();

			listaLink6Aux = new ArrayList<DetContenidoFuncional>();

			if (lista != null && lista.size() > 0) {
				for (DetContenidoFuncional det : lista) {
					DetContenidoFuncional nuevo = new DetContenidoFuncional();
					nuevo.setActivo(det.getActivo());
					nuevo.setContenidoFuncional(det.getContenidoFuncional());
					nuevo.setPuntaje(det.getPuntaje());
					listaLink6Aux.add(nuevo);

					List<DetDescripcionContFuncional> subList = new ArrayList<DetDescripcionContFuncional>();
					if (det.getDetDescripcionContFuncionals() != null) {
						for (DetDescripcionContFuncional subDet : det
								.getDetDescripcionContFuncionals()) {
							DetDescripcionContFuncional subNew = new DetDescripcionContFuncional();
							subNew.setActivo(subDet.getActivo());
							subNew.setDescripcion(subDet.getDescripcion());
							subNew.setDetContenidoFuncional(nuevo);
							subList.add(subNew);
						}
					}
					nuevo.setDetDescripcionContFuncionals(subList);
				}
			}
		} else {// Editar
			String cad = "select * from planificacion.det_contenido_funcional cont_funcional"
					+ " where cont_funcional.id_homologacion = "
					+ idHomologacion;
			listaLink6Aux = new ArrayList<DetContenidoFuncional>();
			listaLink6Aux = em.createNativeQuery(cad,
					DetContenidoFuncional.class).getResultList();
		}

		String cadena = "select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class)
				.getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaLink6Aux) {
				if (det.getContenidoFuncional().getIdContenidoFuncional()
						.longValue() == contenido.getIdContenidoFuncional()
						.longValue()) {
					esta = true;
					// det.getDetDescripcionContFuncionals().clear();
					// det = em.find(DetContenidoFuncional.class,
					// det.getIdDetContenidoFuncional());
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					List<DetDescripcionContFuncional> listaDesc = new ArrayList<DetDescripcionContFuncional>();

					// listaDesc = det.getDetDescripcionContFuncionals();

					if (idHomologacion == null) {// Nuevo
						listaDesc = det.getDetDescripcionContFuncionals();
					} else {
						String cons = "select * from planificacion.det_descripcion_cont_funcional "
								+ " where id_contenido_funcional = "
								+ det.getIdDetContenidoFuncional();

						listaDesc = em.createNativeQuery(cons,
								DetDescripcionContFuncional.class)
								.getResultList();
					}

					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					// System.out.println("---- " + listaDesc.size());
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoLink6.add(dto);
				}
			}
			if (!esta) {
				ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
				dto.setContenidoFuncional(contenido);
				List<DetDescripcionContFuncional> listaDesc = new ArrayList<DetDescripcionContFuncional>();

				DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
				listaDesc.add(descr);
				dto.setListaDetDescripContFuncional(listaDesc);
				listaDtoLink6.add(dto);
			}
		}

	}

	public void eliminarListaLink6(Integer row, Integer subRow) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaDtoLink6.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet.addAll(dto.getListaDetDescripContFuncional());
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		// if (listaDet.size() == 0) {
		// det = new DetDescripcionContFuncional();
		// listaDet.add(det);
		// }
		dto.setListaDetDescripContFuncional(listaDet);
		listaDtoLink6.set(row, dto);
	}

	public void guardarLink6() {
		if (!validacionEscalaLink6())
			return;

		for (ContenidoFuncionalDTO dto : listaDtoLink6) {
			if (dto.getListaDetDescripContFuncional().size() > 0
					&& !vacio(dto.getListaDetDescripContFuncional().get(0)
							.getDescripcion())) {

				DetContenidoFuncional detContenido = dto
						.getListaDetDescripContFuncional().get(0)
						.getDetContenidoFuncional();

				if (detContenido == null
						|| detContenido.getIdDetContenidoFuncional() == null) {
					detContenido = new DetContenidoFuncional();
					detContenido.setContenidoFuncional(dto
							.getContenidoFuncional());
					detContenido.setActivo(true);
					detContenido.setTipo("HOMOLOGADO");
					detContenido
							.setHomologacionPerfilMatriz(homologacionPerfilMatriz);
					detContenidoFuncionalHome.setInstance(detContenido);

					if (detContenido.getIdDetContenidoFuncional() == null)
						detContenidoFuncionalHome.persist();
				} else {
					detContenidoFuncionalHome.setInstance(detContenido);
				}

				// String resultado = detContenidoFuncionalHome.persist();

				// if (resultado.equals("persisted")) {
				List<DetDescripcionContFuncional> listaDescripcion = new ArrayList<DetDescripcionContFuncional>();
				listaDescripcion = dto.getListaDetDescripContFuncional();
				for (DetDescripcionContFuncional desc : listaDescripcion) {
					if (desc.getDescripcion() != null
							&& !desc.getDescripcion().equals("")) {
						desc.setDescripcion(desc.getDescripcion().trim()
								.toUpperCase());
						desc.setDetContenidoFuncional(detContenidoFuncionalHome
								.getInstance());
						desc.setActivo(true);
						if (desc.getIdDetDescripcionContFuncional() == null)
							em.persist(desc);
						else
							em.merge(desc);
					}
				}
				// em.flush();
				detContenidoFuncionalHome.clearInstance();
				// }
			}
		}
	}

	public boolean vacio(String cadena) {
		if (cadena == null || "".equals(cadena))
			return true;
		return false;
	}

	/*******************************************************
	 * Verificaion y validacion de escalas
	 *********************************************************/
	private Boolean validacionEscalaLink6() {

		return true;
	}

	/*******************************************************
	 * Requisitos Minimos
	 *********************************************************/

	/**
	 * Método que agrega un registro a la sublista de Minimos requeridos de la
	 * lista gral
	 * 
	 * @param row
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void buscarRequerimientosMinimosEdit() {

		if (idHomologacion == null) {// Nuevo
			// Se debe copiar los datos del CPT
			String cad = "select * from planificacion.det_req_min det_req"
					+ " where id_cpt = "
					+ funcionarioUtilFormController.getIdCpt();

			List<DetReqMin> lista = em.createNativeQuery(cad, DetReqMin.class)
					.getResultList();

			listaLink7Aux = new ArrayList<DetReqMin>();

			if (lista != null && lista.size() > 0) {
				for (DetReqMin det : lista) {
					DetReqMin nuevo = new DetReqMin();
					nuevo.setActivo(det.getActivo());
					nuevo.setRequisitoMinimoCpt(det.getRequisitoMinimoCpt());
					nuevo.setPuntajeReqMin(det.getPuntajeReqMin());
					listaLink7Aux.add(nuevo);

					List<DetMinimosRequeridos> subList = new ArrayList<DetMinimosRequeridos>();
					if (det.getDetMinimosRequeridoses() != null) {
						for (DetMinimosRequeridos subDet : det
								.getDetMinimosRequeridoses()) {
							DetMinimosRequeridos subNew = new DetMinimosRequeridos();
							subNew.setActivo(subDet.getActivo());
							subNew.setMinimosRequeridos(subDet
									.getMinimosRequeridos());
							subNew.setDetReqMin(nuevo);
							subList.add(subNew);
						}
					}
					nuevo.setDetMinimosRequeridoses(subList);
				}
			}
		} else {// Editar
			String cad = "select * from planificacion.det_req_min det_req"
					+ " where det_req.id_homologacion = " + idHomologacion;
			listaLink7Aux = new ArrayList<DetReqMin>();
			listaLink7Aux = em.createNativeQuery(cad, DetReqMin.class)
					.getResultList();
		}

		String cadena = "select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();
		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class)
				.getResultList();
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaLink7Aux) {
				if (det.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt()
						.longValue() == req.getIdRequisitoMinimoCpt()
						.longValue()) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());

					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();

					if (idHomologacion == null) {// Nuevo
						listaReq = det.getDetMinimosRequeridoses();
					} else {
						String cons = "select * from planificacion.det_minimos_requeridos "
								+ " where id_det_req_min = "
								+ det.getIdDetReqMin();

						listaReq = em.createNativeQuery(cons,
								DetMinimosRequeridos.class).getResultList();
					}

					// Integer tamreqmin = listaReq.size();
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

	public void agregarListaMinimosReqLink7(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);

		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet = dto.getListaDetMinimosRequeridos();
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		listaDet.add(det);
		listaDtoLink7.set(row, dto);

	}

	public void eliminarListaMinimosReqLink7(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);
		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet.addAll(dto.getListaDetMinimosRequeridos());
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		// if (listaDet.size() == 0) {
		// det = new DetMinimosRequeridos();
		// listaDet.add(det);
		// }
		dto.setListaDetMinimosRequeridos(listaDet);
		listaDtoLink7.set(row, dto);

	}

	public void agregarListaOpcConvenientesLink7(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		dto.getListaDetOpcionesConvenientes().add(det);
		listaDtoLink7.set(row, dto);
	}

	public void eliminarListaOpcConvenientesLink7(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);
		List<DetOpcionesConvenientes> listaDet = new ArrayList<DetOpcionesConvenientes>();
		listaDet.addAll(dto.getListaDetOpcionesConvenientes());
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		dto.setListaDetOpcionesConvenientes(listaDet);
		listaDtoLink7.set(row, dto);
	}

	public void guardarLink7() {
		// if (!validacionEscalaLink7())
		// return;

		for (RequisitosMinimosDTO dto : listaDtoLink7) {
			if (dto.getListaDetMinimosRequeridos().size() > 0
					&& !vacio(dto.getListaDetMinimosRequeridos().get(0)
							.getMinimosRequeridos())) {

				DetReqMin detReqMin = dto.getListaDetMinimosRequeridos().get(0)
						.getDetReqMin();

				if (detReqMin == null || detReqMin.getIdDetReqMin() == null) {
					detReqMin = new DetReqMin();
					detReqMin
							.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
					detReqMin.setPuntajeReqMin(dto.getPuntaje());
					detReqMin.setTipo("HOMOLOGADO");
					detReqMin.setActivo(true);
					detReqMin
							.setHomologacionPerfilMatriz(homologacionPerfilMatriz);
					detReqMinHome.setInstance(detReqMin);

					if (detReqMin.getIdDetReqMin() == null)
						detReqMinHome.persist();
				} else {
					detReqMinHome.setInstance(detReqMin);
				}

				List<DetMinimosRequeridos> listaDetMinimos = new ArrayList<DetMinimosRequeridos>();
				listaDetMinimos = dto.getListaDetMinimosRequeridos();
				for (DetMinimosRequeridos detMin : listaDetMinimos) {
					if (detMin.getMinimosRequeridos() != null
							&& !detMin.getMinimosRequeridos().equals("")) {
						detMin.setMinimosRequeridos(detMin
								.getMinimosRequeridos().trim().toUpperCase());
						detMin.setDetReqMin(detReqMinHome.getInstance());
						detMin.setActivo(true);
						if (detMin.getIdMinimosRequeridos() == null)
							em.persist(detMin);
						else
							em.merge(detMin);
					}
				}
				// em.flush();
				detReqMinHome.clearInstance();

			}
		}
	}

	public String getSelectedCss(Integer row) {
		if (row.intValue() == rowSelected.intValue())
			return "background-color:lightgrey;";

		return "";
	}

	public void seleccionar(Integer row) {
		rowSelected = row;
	}

	public List<RequisitosMinimosDTO> getListaDtoLink7() {
		return listaDtoLink7;
	}

	public void setListaDtoLink7(List<RequisitosMinimosDTO> listaDtoLink7) {
		this.listaDtoLink7 = listaDtoLink7;
	}

	/**
	 * Valida el formaulario de edicion
	 * 
	 * @return
	 */
	private boolean validate() {
		statusMessages.clear();
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		String mensaje = "";
		String idCompomente = "";
		if (funcionarioUtilFormController.getIdCpt() == null) {
			mensaje = "CPT es un campo requerido. Verifique.";
			idCompomente = "form:cptDecorate:denominacionCPT";
		} else if (vacio(homologacionPerfilMatriz.getDenominacion())) {
			mensaje = "Denominación es un campo requerido. Verifique.";
			idCompomente = "form:denominacionDecorate:denominacion";
		} else if (vacio(homologacionPerfilMatriz.getObjetivo())) {
			mensaje = "Objetivo es un campo requerido. Verifique.";
			idCompomente = "form:objetivoDecorate:objetivo";
		} else if (vacio(homologacionPerfilMatriz.getMision())) {
			mensaje = "Misión es un campo requerido. Verifique.";
			idCompomente = "form:misionDecorate:mision";
		} else if (matEvaluacionFormController
				.getListaSeleccionadosMatrizRefDTO() == null) {
			mensaje = "Debe cargar los datos de la Pestaña Matriz. Verifique.";
		}

		if (!vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.add(Severity.ERROR, mensaje);
			// FACES ERROR
			message.setDetail(mensaje);
			message.setSummary(mensaje);
			context.addMessage(idCompomente, message);
			return false;
		}
		return matEvaluacionFormController.precondSave();

	}

	public void selecIdMatRef(Integer id) throws CloneNotSupportedException {
		if (listaMatrizRefercial != null && id < listaMatrizRefercial.size()) {
			idMatRefSel = id;
			cargarListaSelCrear();
		}

	}

	public void setIdHomologacion(Long idHomologacion) {
		this.idHomologacion = idHomologacion;
	}

	public Long getIdHomologacion() {
		return idHomologacion;
	}

	public void setHomologacionPerfilMatriz(
			HomologacionPerfilMatriz homologacionPerfilMatriz) {
		this.homologacionPerfilMatriz = homologacionPerfilMatriz;
	}

	public HomologacionPerfilMatriz getHomologacionPerfilMatriz() {
		return homologacionPerfilMatriz;
	}

	public float getTotalGraduacion() {
		return totalGraduacion;
	}

	public void setTotalGraduacion(float totalGraduacion) {
		this.totalGraduacion = totalGraduacion;
	}

	public float getTotalPuntajeMaximo() {
		return totalPuntajeMaximo;
	}

	public void setTotalPuntajeMaximo(float totalPuntajeMaximo) {
		this.totalPuntajeMaximo = totalPuntajeMaximo;
	}

	public void setOficinas(String oficinas) {
		this.oficinas = oficinas;
	}

	public String getOficinas() {
		return oficinas;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public List<ContenidoFuncionalDTO> getListaDtoLink6() {
		return listaDtoLink6;
	}

	public void setListaDtoLink6(List<ContenidoFuncionalDTO> listaDtoLink6) {
		this.listaDtoLink6 = listaDtoLink6;
	}

	public void setRowSelected(Integer rowSelected) {
		this.rowSelected = rowSelected;
	}

	public Integer getRowSelected() {
		return rowSelected;
	}

	public List<MatrizReferencial> getListaMatrizRefercial() {
		return listaMatrizRefercial;
	}

	public void setListaMatrizRefercial(
			List<MatrizReferencial> listaMatrizRefercial) {
		this.listaMatrizRefercial = listaMatrizRefercial;
	}

	public void setVista(Boolean vista) {
		this.vista = vista;
	}

	public Boolean getVista() {
		return vista;
	}

	public Boolean getModoEditado() {
		return modoEditado;
	}

	public void setModoEditado(Boolean modoEditado) {
		this.modoEditado = modoEditado;
	}

	public Boolean getModoVer() {
		return modoVer;
	}

	public void setModoVer(Boolean modoVer) {
		this.modoVer = modoVer;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public Boolean getMostrarLinkEdit() {
		return mostrarLinkEdit;
	}

	public void setMostrarLinkEdit(Boolean mostrarLinkEdit) {
		this.mostrarLinkEdit = mostrarLinkEdit;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

}
