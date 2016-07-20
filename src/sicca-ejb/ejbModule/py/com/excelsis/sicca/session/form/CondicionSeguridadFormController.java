package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.CondicionSeguridadDTO;
import py.com.excelsis.sicca.dto.ValoracionTab06CPT;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EscalaCondSegur;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("condicionSeguridadFormController")
public class CondicionSeguridadFormController implements Serializable {

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
	ReferenciasUtilFormController referenciasUtilFormController;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String mensaje;
	private String cu;
	private Boolean habilitar ;
	
	private List<DetCondicionSegur> listaLink10Aux = new ArrayList<DetCondicionSegur>();
	private List<CondicionSeguridadDTO> listaDtoLink10 = new ArrayList<CondicionSeguridadDTO>();
	
	private List<ValoracionTab06CPT> listaValoracion = new ArrayList<ValoracionTab06CPT>();
	private List<CondicionSeguridadDTO> listaCondicionDTO = new ArrayList<CondicionSeguridadDTO>();
	private List<DetCondicionSegur> listaCondicionAux = new ArrayList<DetCondicionSegur>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
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
		/* Incidencia 1014 */
		validarOee();
		/**/

		buscarDatosAsociadosUsuario();
		//buscarDetCondicionSegur();
		buscarDetCondicionSegurEdit();
		habilitarPantalla();
	}
	
	private void habilitarPantalla() {
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = true;
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
			if ( concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue())
				habilitar = true;
			if(concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					||concursoPuestoAgr.getEstado().intValue() == refAjuste.getValorNum().intValue())
				habilitar = false;
		}

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
		
		String cadena = "select escala.* from planificacion.escala_cond_segur escala "
				+ "where escala.activo is TRUE order by desde,hasta";
		List<EscalaCondSegur> lista = new ArrayList<EscalaCondSegur>();
		listaValoracion = new ArrayList<ValoracionTab06CPT>();
		lista = em.createNativeQuery(cadena, EscalaCondSegur.class)
				.getResultList();

		String cad = "select * from planificacion.condicion_segur cond"
				+ " where cond.activo is TRUE order by cond.orden";
		List<CondicionSegur> listaSegur = new ArrayList<CondicionSegur>();

		listaSegur = em.createNativeQuery(cad, CondicionSegur.class)
				.getResultList();

		for (CondicionSegur o : listaSegur) {
			ValoracionTab06CPT valor = new ValoracionTab06CPT();
			valor.setCondicionSegur(o);
			valor.setListaEscalaCondSeg(lista);
			listaValoracion.add(valor);
		}
	}
	//AGREGADO JD
	@SuppressWarnings("unused")
	private void buscarDetCondicionSegurEdit() {
				
		String cad =
			"select * from planificacion.det_condicion_segur det_cond"
				+ " where det_cond.id_planta_cargo_det = (select min(id_planta_cargo_det)  "
					+ "from seleccion.concurso_puesto_det where id_concurso_puesto_agr = "
					+concursoPuestoAgr.getIdConcursoPuestoAgr()+" )";
		
		listaLink10Aux = new ArrayList<DetCondicionSegur>();
		listaLink10Aux = em.createNativeQuery(cad, DetCondicionSegur.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> lista = new ArrayList<CondicionSegur>();

		lista = em.createNativeQuery(cadena, CondicionSegur.class).getResultList();
		listaDtoLink10 = new ArrayList<CondicionSeguridadDTO>();
		for (CondicionSegur seg : lista) {
			Boolean esta = false;
			for (DetCondicionSegur det : listaLink10Aux) {
				if (det.getCondicionSegur().getIdCondicionSegur().equals(seg.getIdCondicionSegur())) {
					esta = true;
					CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
					dto.setCondicionSegur(det.getCondicionSegur());
					dto.setId(det.getIdDetCondicionSegur());
					dto.setActivo(det.getActivo());
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondSegur());
					listaDtoLink10.add(dto);
				}
			}
			if (!esta) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setCondicionSegur(seg);
				listaDtoLink10.add(dto);
			}
		}

	}

	/**
	 * Busca datos para editar el Link10
	 */
	@SuppressWarnings("unchecked")
	private void buscarDetCondicionSegur() {
		String cad = "select * from planificacion.det_condicion_segur det_cond"
				+ " where det_cond.activo is true and det_cond.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaCondicionAux = new ArrayList<DetCondicionSegur>();
		listaCondicionAux = em.createNativeQuery(cad, DetCondicionSegur.class)
				.getResultList();

		if (listaCondicionAux == null || listaCondicionAux.size() == 0) {
			if (cu != null && cu.equals("404"))
				listaCondicionAux = cargarDetCondicionSegurDelCpt();
		}

		String cadena = "select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> lista = new ArrayList<CondicionSegur>();

		lista = em.createNativeQuery(cadena, CondicionSegur.class)
				.getResultList();
		listaCondicionDTO = new ArrayList<CondicionSeguridadDTO>();
		for (CondicionSegur seg : lista) {
			Boolean esta = false;
			for (DetCondicionSegur det : listaCondicionAux) {
				if (det.getCondicionSegur().getIdCondicionSegur()
						.equals(seg.getIdCondicionSegur())) {
					esta = true;
					CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
					dto.setCondicionSegur(det.getCondicionSegur());
					dto.setId(det.getIdDetCondicionSegur());
					dto.setActivo(true);
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondSegur());
					listaCondicionDTO.add(dto);
				}
			}
			if (!esta) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setActivo(true);
				dto.setCondicionSegur(seg);
				listaCondicionDTO.add(dto);
			}
		}

	}

	private List<DetCondicionSegur> cargarDetCondicionSegurDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DetCondicionSegur> nuevaLista = new ArrayList<DetCondicionSegur>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_condicion_segur det_trab "
					+ " where det_trab.id_cpt = "
					+ cpt.getId()
					+ " and det_trab.activo= true";

			List<DetCondicionSegur> lista = new ArrayList<DetCondicionSegur>();
			lista = em.createNativeQuery(cad, DetCondicionSegur.class)
					.getResultList();

			if (lista != null) {
				for (DetCondicionSegur detCondicionSegur : lista) {
					DetCondicionSegur nuevo = new DetCondicionSegur();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setCondicionSegur(detCondicionSegur
							.getCondicionSegur());
					nuevo.setPuntajeCondSegur(detCondicionSegur
							.getPuntajeCondSegur());
					nuevo.setAjustes(detCondicionSegur.getAjustes());
					nuevo.setJustificacion(detCondicionSegur.getJustificacion());
					nuevo.setTipo("GRUPO");
					nuevaLista.add(nuevo);
				}
			}
		}

		return nuevaLista;
	}

	private Boolean validacionEscala() {
		for (CondicionSeguridadDTO dto : listaCondicionDTO) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto)) {
					return false;
				}
			}
		}
		return true;
	}

	public Boolean verificarValoracion(CondicionSeguridadDTO condicion) {
		if (listaValoracion == null || listaValoracion.size() == 0)
			buscarValoracion();
		List<EscalaCondSegur> v = new ArrayList<EscalaCondSegur>();
		if (condicion.getPuntaje() != null) {
			v = listaValoracion.get(0).getListaEscalaCondSeg();
			Float valor = condicion.getPuntaje();
			Boolean cumple = false;
			for (EscalaCondSegur o : v) {
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
		}
		mensaje = null;
		return true;
	}

	public String guardar() {
		if (!validacionEscala())
			return null;
		try {
			for (CondicionSeguridadDTO dto : listaCondicionDTO) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
						&& dto.getJustificacion() != null) {
					DetCondicionSegur det = new DetCondicionSegur();
					det.setActivo(dto.getActivo());
					det.setAjustes(dto.getAjustes().trim().toUpperCase());
					det.setCondicionSegur(dto.getCondicionSegur());
					det.setConcursoPuestoAgr(concursoPuestoAgr);
					det.setJustificacion(dto.getJustificacion().trim()
							.toUpperCase());
					det.setPuntajeCondSegur(dto.getPuntaje());
					det.setTipo("GRUPO");

					em.persist(det);
				}

			}
			em.flush();
			em.flush();
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
			for (CondicionSeguridadDTO dto : listaCondicionDTO) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
						&& dto.getJustificacion() != null) {
					if (dto.getId() == null) {
						DetCondicionSegur detCondSeg = new DetCondicionSegur();
						detCondSeg.setActivo(dto.getActivo());
						detCondSeg.setCondicionSegur(dto.getCondicionSegur());
						detCondSeg.setConcursoPuestoAgr(concursoPuestoAgr);
						detCondSeg.setPuntajeCondSegur(dto.getPuntaje());
						detCondSeg.setTipo("GRUPO");
						detCondSeg.setAjustes(dto.getAjustes().trim()
								.toUpperCase());
						detCondSeg.setJustificacion(dto.getJustificacion()
								.trim().toUpperCase());
						em.persist(detCondSeg);
					} else {
						DetCondicionSegur detCondSeg = new DetCondicionSegur();
						detCondSeg = em.find(DetCondicionSegur.class,
								dto.getId());
						if (cu != null && cu.equalsIgnoreCase("404")) {

							detCondSeg.setActivo(dto.getActivo());
							detCondSeg.setPuntajeCondSegur(dto.getPuntaje());
							detCondSeg.setAjustes(dto.getAjustes().trim()
									.toUpperCase());
							detCondSeg.setJustificacion(dto.getJustificacion()
									.trim().toUpperCase());
							em.merge(detCondSeg);
						} else {
							DetCondicionSegur detCondSegAx = new DetCondicionSegur();
							detCondSegAx = em.find(DetCondicionSegur.class,
									dto.getId());
							if (!detCondSeg.getPuntajeCondSegur().equals(
									detCondSegAx.getPuntajeCondSegur())
									|| !detCondSeg
											.getJustificacion()
											.equalsIgnoreCase(
													detCondSegAx
															.getJustificacion())
									|| detCondSeg.getAjustes()
											.equalsIgnoreCase(
													detCondSegAx.getAjustes())) {
								detCondSeg.setActivo(false);
								DetCondicionSegur detCondS = new DetCondicionSegur();
								detCondS.setActivo(dto.getActivo());
								detCondS.setCondicionSegur(dto
										.getCondicionSegur());
								detCondS.setConcursoPuestoAgr(concursoPuestoAgr);
								detCondS.setPuntajeCondSegur(dto.getPuntaje());
								detCondS.setTipo("GRUPO");
								detCondS.setAjustes(dto.getAjustes().trim()
										.toUpperCase());
								detCondS.setJustificacion(dto
										.getJustificacion().trim()
										.toUpperCase());
								em.persist(detCondS);
							}
						}
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionSegur detCondSeg = new DetCondicionSegur();
						detCondSeg = em.find(DetCondicionSegur.class,
								dto.getId());
						em.remove(detCondSeg);
					}
				}
				em.flush();

			}

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

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public List<ValoracionTab06CPT> getListaValoracion() {
		return listaValoracion;
	}

	public void setListaValoracion(List<ValoracionTab06CPT> listaValoracion) {
		this.listaValoracion = listaValoracion;
	}

	public List<CondicionSeguridadDTO> getListaCondicionDTO() {
		return listaCondicionDTO;
	}

	public void setListaCondicionDTO(
			List<CondicionSeguridadDTO> listaCondicionDTO) {
		this.listaCondicionDTO = listaCondicionDTO;
	}

	public List<DetCondicionSegur> getListaCondicionAux() {
		return listaCondicionAux;
	}

	public void setListaCondicionAux(List<DetCondicionSegur> listaCondicionAux) {
		this.listaCondicionAux = listaCondicionAux;
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
	
	public List<DetCondicionSegur> getListaLink10Aux(){
		return listaLink10Aux;
	}
	public void setListaLink10Aux(List<DetCondicionSegur> listaLink10Aux){
		this.listaLink10Aux = listaLink10Aux;
	}
	
	public List<CondicionSeguridadDTO> getListaDtoLink10(){
		return listaDtoLink10;
	}
	public void setListaDtoLink10(List<CondicionSeguridadDTO> listaDtoLink10){
		this.listaDtoLink10 = listaDtoLink10;
	}

}
