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

import py.com.excelsis.sicca.dto.ContenidoFuncionalDTO;
import py.com.excelsis.sicca.entity.AntecedenteGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.DetContenidoFuncionalHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("funcionComisionFormController")
public class FuncionComisionFormController implements Serializable {

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
	DetContenidoFuncionalHome detContenidoFuncionalHome;
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
	private Boolean habilitar;// = true;

	private List<ContenidoFuncionalDTO> listaFuncionesDTO = new ArrayList<ContenidoFuncionalDTO>();
	private List<ContenidoFuncional> listaValoracion = new ArrayList<ContenidoFuncional>();
	List<DetContenidoFuncional> listaAux = new ArrayList<DetContenidoFuncional>();
	List<DetDescripcionContFuncional> listaDetInactivar =
		new ArrayList<DetDescripcionContFuncional>();

	/**
	 * from CU162-53
	 **/
	private String fromCU;
	private Long idConcursoPuestoAgr;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
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
		// habEdit();
		buscarDatosAsociadosUsuario();
		buscarContenidoFuncional();
		habilitarPantalla();
	}

	/**
	 * Para el caso de editar No permitir editar registros, bloquear las cajas de textos cuando es llamado desde este CU.
	 **/
	/*
	 * public boolean habEdit() { if ((fromCU != null && fromCU .equals("admPerfilMatrizHomologInstitucion_list")) || tienePerfilHomologado()) { return false; } return true;
	 * 
	 * }
	 */
	private void habilitarPantalla() {
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null && !concursoPuestoAgr.getHomologar()
			&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			
			refIniciado =
				referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "INICIADO CIRCUITO");
							
			Referencias refPendiente = new Referencias();
			refPendiente =
				referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste =
				referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado.getValorNum().intValue()
				|| concursoPuestoAgr.getEstado().intValue() == refPendiente.getValorNum().intValue())
				habilitar = true;
			if (concursoPuestoAgr.getEstado().intValue() == refAjuste.getValorNum().intValue())
				habilitar = false;
			
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado.getValorNum().intValue())
				habilitar = false;
		}

	}

	/*
	 * 
	 * @SuppressWarnings("unchecked") private Boolean tienePerfilHomologado() { if (concursoPuestoAgr.getHomologar() != null && concursoPuestoAgr.getHomologar()) return false;
	 * 
	 * String sql = "select perfil.*  " + "from seleccion.homologacion_perfil_matriz perfil " + "join seleccion.concurso_puesto_agr agr " + "on agr.id_homologacion = perfil.id_homologacion " +
	 * "where agr.id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr(); List<HomologacionPerfilMatriz> listaMatX = new ArrayList<HomologacionPerfilMatriz>();
	 * 
	 * listaMatX = em.createNativeQuery(sql, HomologacionPerfilMatriz.class) .getResultList(); if (listaMatX.size() == 0) return false; return true; }
	 */
	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
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
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Busca datos para editar el ContenidoFuncional
	 */
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncional() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.activo is true and cont_funcional.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and cont_funcional.tipo = 'GRUPO'";
		listaAux = new ArrayList<DetContenidoFuncional>();

		listaAux = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		// Si no hay datos del grupo, se debe buscar por el CPT
		if (listaAux == null || listaAux.size() == 0) {
			if (cu != null && cu.equals("404"))
				listaAux = cargarContenidoFuncionalDelCpt();
		}

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaFuncionesDTO = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> listaDto = new ArrayList<ContenidoFuncional>();

		listaDto = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : listaDto) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaAux) {
				if (det.getContenidoFuncional().getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());

					List<DetDescripcionContFuncional> listaDesc =
						new ArrayList<DetDescripcionContFuncional>();

					if (det.getIdDetContenidoFuncional() == null
						&& det.getDetDescripcionContFuncionals() != null)
						listaDesc = det.getDetDescripcionContFuncionals();// Nuevo
					else
						listaDesc = buscarDescripcion(det.getIdDetContenidoFuncional());// Edicion

					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					descr.setActivo(true);
					listaDesc.add(descr);

					dto.setListaDetDescripContFuncional(listaDesc);
					listaFuncionesDTO.add(dto);
				}
			}
			if (!esta) {
				if (!buscarEnListaFunciones(contenido)) {
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(contenido);
					List<DetDescripcionContFuncional> listaDesc =
						new ArrayList<DetDescripcionContFuncional>();

					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					descr.setActivo(true);
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaFuncionesDTO.add(dto);
				}
			}
		}
	}

	private List<DetContenidoFuncional> cargarContenidoFuncionalDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr =
			em.find(ConcursoPuestoAgr.class, this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DetContenidoFuncional> nuevaLista = new ArrayList<DetContenidoFuncional>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
			&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad =
				"select * from planificacion.det_contenido_funcional det " + " where det.id_cpt = "
					+ cpt.getId() + " and det.activo= true";

			List<DetContenidoFuncional> lista = new ArrayList<DetContenidoFuncional>();
			lista = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

			if (lista != null) {
				for (DetContenidoFuncional detContenidoFuncional : lista) {
					DetContenidoFuncional nuevo = new DetContenidoFuncional();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setContenidoFuncional(detContenidoFuncional.getContenidoFuncional());
					nuevo.setPuntaje(detContenidoFuncional.getPuntaje());
					nuevo.setTipo("GRUPO");

					// Se copian los detalles
					List<DetDescripcionContFuncional> listaDesc =
						buscarDescripcion(detContenidoFuncional.getIdDetContenidoFuncional());
					if (listaDesc != null) {
						List<DetDescripcionContFuncional> listaDetalles =
							new ArrayList<DetDescripcionContFuncional>();
						for (DetDescripcionContFuncional detDescripcionContFuncional : listaDesc) {
							DetDescripcionContFuncional nuevoDetalle =
								new DetDescripcionContFuncional();
							nuevoDetalle.setActivo(detDescripcionContFuncional.getActivo());
							nuevoDetalle.setDescripcion(detDescripcionContFuncional.getDescripcion());
							nuevoDetalle.setDetContenidoFuncional(nuevo);
							listaDetalles.add(nuevoDetalle);

						}
						nuevo.setDetDescripcionContFuncionals(listaDetalles);
					}

					nuevaLista.add(nuevo);
				}
			}
		}

		return nuevaLista;
	}

	private Boolean buscarEnListaFunciones(ContenidoFuncional conten) {
		for (ContenidoFuncionalDTO ctn : listaFuncionesDTO) {
			if (ctn.getContenidoFuncional().getIdContenidoFuncional().equals(conten.getIdContenidoFuncional()))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<DetDescripcionContFuncional> buscarDescripcion(Long id) {
		String sql =
			"select descr.* " + "from planificacion.det_descripcion_cont_funcional descr "
				+ "join planificacion.det_contenido_funcional det "
				+ "on det.id_det_contenido_funcional = descr.id_contenido_funcional "
				+ "where descr.activo is true and det.id_det_contenido_funcional = " + id;
		List<DetDescripcionContFuncional> lista = new ArrayList<DetDescripcionContFuncional>();

		lista = em.createNativeQuery(sql, DetDescripcionContFuncional.class).getResultList();
		return lista;
	}

	/**
	 * Método que agrega un detalle a la sublista de la tabla
	 * 
	 * @param row
	 */

	public void agregarLista(Integer row) {

		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaFuncionesDTO.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet = dto.getListaDetDescripContFuncional();
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		det.setActivo(true);
		listaDet.add(det);
		listaFuncionesDTO.set(row, dto);

	}

	public void eliminarLista(Integer row, Integer subRow) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaFuncionesDTO.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet.addAll(dto.getListaDetDescripContFuncional());
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		if (det.getIdDetDescripcionContFuncional() != null)
			listaDetInactivar.add(det);
		if (listaDet.size() == 0) {
			det = new DetDescripcionContFuncional();
			listaDet.add(det);
		}
		dto.setListaDetDescripContFuncional(listaDet);
		listaFuncionesDTO.set(row, dto);
	}

	public String guardar() {
		if (!validacionEscala())
			return null;
		try {
			for (ContenidoFuncionalDTO dto : listaFuncionesDTO) {
				if (dto.getPuntaje() != null) {
					DetContenidoFuncional detContenido = new DetContenidoFuncional();
					detContenido.setContenidoFuncional(dto.getContenidoFuncional());
					detContenido.setPuntaje(dto.getPuntaje());
					detContenido.setTipo("GRUPO");
					detContenido.setConcursoPuestoAgr(concursoPuestoAgr);
					detContenido.setActivo(true);
					detContenidoFuncionalHome.setInstance(detContenido);
					String resultado = detContenidoFuncionalHome.persist();
					if (resultado.equals("persisted")) {
						List<DetDescripcionContFuncional> listaDescripcion =
							new ArrayList<DetDescripcionContFuncional>();
						listaDescripcion = dto.getListaDetDescripContFuncional();
						for (DetDescripcionContFuncional desc : listaDescripcion) {
							if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
								desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
								desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
								em.persist(desc);
							}
						}
						em.flush();
						detContenidoFuncionalHome.clearInstance();
					}
				}

			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
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
			for (ContenidoFuncionalDTO dto : listaFuncionesDTO) {
				if (dto.getId() != null) {
					DetContenidoFuncional detContenido = new DetContenidoFuncional();
					detContenido = em.find(DetContenidoFuncional.class, dto.getId());
					detContenido.setPuntaje(dto.getPuntaje());

					detContenidoFuncionalHome.setInstance(detContenido);
					String resultado = detContenidoFuncionalHome.update();
					if (resultado.equals("updated")) {
						List<DetDescripcionContFuncional> listaDescripcion =
							new ArrayList<DetDescripcionContFuncional>();
						listaDescripcion = dto.getListaDetDescripContFuncional();
						for (DetDescripcionContFuncional desc : listaDescripcion) {
							if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
								if (desc.getIdDetDescripcionContFuncional() == null) {
									desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
									desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
									em.persist(desc);

								} else {
									desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
									desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
									em.merge(desc);

								}
							}
						}
					}
				} else {
					if (dto.getPuntaje() != null) {
						DetContenidoFuncional detContenido = new DetContenidoFuncional();
						detContenido.setContenidoFuncional(dto.getContenidoFuncional());
						detContenido.setPuntaje(dto.getPuntaje());
						detContenido.setTipo("GRUPO");
						detContenido.setActivo(true);
						detContenido.setConcursoPuestoAgr(concursoPuestoAgr);
						detContenidoFuncionalHome.setInstance(detContenido);
						String resultado = detContenidoFuncionalHome.persist();
						if (resultado.equals("persisted")) {
							List<DetDescripcionContFuncional> listaDescripcion =
								new ArrayList<DetDescripcionContFuncional>();
							listaDescripcion = dto.getListaDetDescripContFuncional();
							for (DetDescripcionContFuncional desc : listaDescripcion) {
								if (desc.getDescripcion() != null
									&& !desc.getDescripcion().equals("")) {
									desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
									desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
									em.persist(desc);
								}
							}
							em.flush();
						}
					}
				}
				detContenidoFuncionalHome.clearInstance();
			}
			for (DetDescripcionContFuncional detail : listaDetInactivar) {
				detail.setActivo(false);
				em.merge(detail);
			}

			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	private Boolean validacionEscala() {
		for (ContenidoFuncionalDTO dto : listaFuncionesDTO) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto))
					return false;
			}
		}
		return true;
	}

	/**
	 * Método que verifica si el puntaje ingresado en el link06 se encuentra en el rango mostrado en la escala
	 * 
	 * @param contenido
	 * @return
	 */
	public Boolean verificarValoracion(ContenidoFuncionalDTO contenido) {
		List<ValorNivelOrg> v = new ArrayList<ValorNivelOrg>();
		v = contenido.getContenidoFuncional().getValorNivelOrgs();
		Float valor = contenido.getPuntaje();
		Boolean cumple = false;
		for (ValorNivelOrg o : v) {
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

	/**
	 * Método que obtiene la valoracion correspondiente al link06; contenido funcional
	 */
	@SuppressWarnings("unchecked")
	public void buscarValoracion() {
		listaValoracion = new ArrayList<ContenidoFuncional>();
		String cadena =
			"select funcional.* from planificacion.contenido_funcional funcional "
				+ "where funcional.activo is TRUE order by funcional.orden";
		listaValoracion = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();
	}

	public List<ValorNivelOrg> traerValorNivelOrgs(Long idContenidoFuncional) {
		Query q =
			em.createQuery("select ValorNivelOrg from ValorNivelOrg ValorNivelOrg where ValorNivelOrg.activo is true and ValorNivelOrg.contenidoFuncional.idContenidoFuncional = :idContenidoFuncional order by desde asc, hasta asc");
		q.setParameter("idContenidoFuncional", idContenidoFuncional);
		List<ValorNivelOrg> lista = q.getResultList();

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

	public List<ContenidoFuncionalDTO> getListaFuncionesDTO() {
		return listaFuncionesDTO;
	}

	public void setListaFuncionesDTO(List<ContenidoFuncionalDTO> listaFuncionesDTO) {
		this.listaFuncionesDTO = listaFuncionesDTO;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public List<ContenidoFuncional> getListaValoracion() {
		return listaValoracion;
	}

	public void setListaValoracion(List<ContenidoFuncional> listaValoracion) {
		this.listaValoracion = listaValoracion;
	}

	public List<DetContenidoFuncional> getListaAux() {
		return listaAux;
	}

	public void setListaAux(List<DetContenidoFuncional> listaAux) {
		this.listaAux = listaAux;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public List<DetDescripcionContFuncional> getListaDetInactivar() {
		return listaDetInactivar;
	}

	public void setListaDetInactivar(List<DetDescripcionContFuncional> listaDetInactivar) {
		this.listaDetInactivar = listaDetInactivar;
	}

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

}
