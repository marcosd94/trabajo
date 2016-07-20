package py.com.excelsis.sicca.seleccion.session.form;

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

import py.com.excelsis.sicca.dto.CondicionTrabajoDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.EscalaCondTrab;
import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("condicionTrabajoFormController")
public class CondicionTrabajoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6912411299156452746L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Long idConcursoPuestoAgr;

	private String mensajeLink;
	private List<CondicionTrabajoDTO> listaCondicionDto = new ArrayList<CondicionTrabajoDTO>();
	private List<CondicionTrabajo> listaValoracion = new ArrayList<CondicionTrabajo>();
	private String cu;
	private Boolean habilitar = true;
	/**
	 * from CU162
	 **/
	private String fromCU = null;
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

	public void init() {
		buscarCondicionTrabajoEdit();
		/* Incidencia 1014 */
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		validarOee(grupo.getConcurso());
		habilitarPantalla(grupo);
		/**/

	}

	private void habilitarPantalla(ConcursoPuestoAgr concursoPuestoAgr) {
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null && !concursoPuestoAgr.getHomologar()
			&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = true;
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
			if (concursoPuestoAgr.getEstado().intValue() == refPendiente.getValorNum().intValue())
				habilitar = true;
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refAjuste.getValorNum().intValue())
				habilitar = false;
		}

	}

	/**
	 * Busca datos para editar el Link8
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEdit() {
		String cad =
			"select * from planificacion.det_condicion_trabajo det_trab "
				+ " where det_trab.id_concurso_puesto_agr = " + idConcursoPuestoAgr;

		List<DetCondicionTrabajo> listaLink8Aux = new ArrayList<DetCondicionTrabajo>();
		listaLink8Aux = em.createNativeQuery(cad, DetCondicionTrabajo.class).getResultList();

		// Si no hay datos del grupo, se debe buscar por el CPT
		if (listaLink8Aux == null || listaLink8Aux.size() == 0) {
			if (cu != null && cu.equals("404")) {
				listaLink8Aux = cargarCondicionesDelCpt();
			}
		}

		String cadena =
			"select * from planificacion.condicion_trabajo cond "
				+ " where cond.activo is true order by cond.orden";

		List<CondicionTrabajo> lista = new ArrayList<CondicionTrabajo>();
		lista = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
		listaCondicionDto = new ArrayList<CondicionTrabajoDTO>();
		for (CondicionTrabajo condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajo det : listaLink8Aux) {
				if (det.getCondicionTrabajo().getIdCondicionTrabajo().equals(condicion.getIdCondicionTrabajo())) {
					esta = true;
					CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
					dto.setCondicionTrabajo(det.getCondicionTrabajo());
					dto.setId(det.getIdDetCondiconTrabajo());
					dto.setActivo(det.getActivo());

					dto.setPuntaje(det.getPuntajeCondTrab());
					listaCondicionDto.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
				dto.setCondicionTrabajo(condicion);
				listaCondicionDto.add(dto);
				System.out.println("#### " + condicion.getDescripcion());
			}
		}
	}

	private List<DetCondicionTrabajo> cargarCondicionesDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		List<DetCondicionTrabajo> listaLink8Aux = new ArrayList<DetCondicionTrabajo>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
			&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad =
				"select * from planificacion.det_condicion_trabajo det_trab "
					+ " where det_trab.id_cpt = " + cpt.getId() + " and det_trab.activo= true";

			List<DetCondicionTrabajo> lista = new ArrayList<DetCondicionTrabajo>();
			lista = em.createNativeQuery(cad, DetCondicionTrabajo.class).getResultList();

			if (lista != null) {
				for (DetCondicionTrabajo detCondicionTrabajo : lista) {
					DetCondicionTrabajo nuevo = new DetCondicionTrabajo();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setCondicionTrabajo(detCondicionTrabajo.getCondicionTrabajo());
					nuevo.setPuntajeCondTrab(detCondicionTrabajo.getPuntajeCondTrab());
					nuevo.setTipo("GRUPO");
					listaLink8Aux.add(nuevo);
				}
			}
		}

		return listaLink8Aux;
	}

	public String guardar() {
		if (!validacionEscala())
			return null;
		try {
			String mensaje = "";
			for (CondicionTrabajoDTO dto : listaCondicionDto) {
				if (dto.getPuntaje() != null) {
					if (dto.getId() == null) {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab.setActivo(dto.getActivo());
						detCondTrab.setCondicionTrabajo(dto.getCondicionTrabajo());
						detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
						detCondTrab.setTipo("GRUPO");

						ConcursoPuestoAgr concursoPuestoAgr =
							em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
						detCondTrab.setConcursoPuestoAgr(concursoPuestoAgr);
						em.persist(detCondTrab);
						mensaje = SeamResourceBundle.getBundle().getString("GENERICO_MSG");
					} else {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab = em.find(DetCondicionTrabajo.class, dto.getId());
						if (fromCU != null
							&& fromCU.equals("admPerfilMatrizHomologInstitucion_list")) {
							if (!detCondTrab.getPuntajeCondTrab().equals(dto.getPuntaje())) {
								detCondTrab.setActivo(false);
								em.merge(detCondTrab);
								DetCondicionTrabajo detCondTrabCopia = new DetCondicionTrabajo();// se crea una copia con la modificacion
								detCondTrabCopia.setActivo(dto.getActivo());
								detCondTrabCopia.setCondicionTrabajo(dto.getCondicionTrabajo());
								detCondTrabCopia.setPuntajeCondTrab(dto.getPuntaje());
								detCondTrabCopia.setTipo("GRUPO");
								ConcursoPuestoAgr concursoPuestoAgr =
									em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
								detCondTrabCopia.setConcursoPuestoAgr(concursoPuestoAgr);
								em.persist(detCondTrabCopia);
								mensaje = SeamResourceBundle.getBundle().getString("GENERICO_MSG");
							}

						} else {
							detCondTrab.setActivo(dto.getActivo());
							detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
							em.merge(detCondTrab);
							mensaje = SeamResourceBundle.getBundle().getString("GENERICO_MSG");
						}
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab = em.find(DetCondicionTrabajo.class, dto.getId());
						em.remove(detCondTrab);
						mensaje = SeamResourceBundle.getBundle().getString("GENERICO_MSG");
					}
				}
				em.flush();

			}
			buscarCondicionTrabajoEdit();

			statusMessages.clear();
			if ("".equals(mensaje))
				statusMessages.add(Severity.ERROR, "No se detectaron cambios que guardar.");
			else
				statusMessages.add(Severity.INFO, mensaje);

			return "ok";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	private Boolean validacionEscala() {
		for (CondicionTrabajoDTO dto : listaCondicionDto) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracionTab04(dto))
					return false;
			}
		}
		return true;
	}

	public Boolean verificarValoracionTab04(CondicionTrabajoDTO condicion) {
		List<EscalaCondTrab> escalaCondTrabList = new ArrayList<EscalaCondTrab>();
		if (condicion.getPuntaje() != null) {
			CondicionTrabajo condicionTrabajo = condicion.getCondicionTrabajo();
			condicionTrabajo = em.find(CondicionTrabajo.class, condicionTrabajo.getId());
			escalaCondTrabList = condicionTrabajo.getEscalaCondTrabs();
			Float valor = condicion.getPuntaje();
			Boolean cumple = false;
			for (EscalaCondTrab o : escalaCondTrabList) {
				Float desde = new Float("" + o.getDesde());
				Float hasta = new Float("" + o.getHasta());
				if (desde.floatValue() <= valor.floatValue()
					&& hasta.floatValue() >= valor.floatValue()) {
					cumple = true;
				}
			}
			if (!cumple) {
				if (escalaCondTrabList == null || escalaCondTrabList.size() == 0)
					mensajeLink = "No existe escala para la condición de trabajo. Verifique.";
				else
					mensajeLink = "El puntaje ingresado no es válido, verifique la escala.";
				return cumple;
			}
		}
		mensajeLink = null;

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
			if (v == null || v.size() == 0)
				mensajeLink = "No existe escala para el requisito mínimo. Verifique.";
			else
				mensajeLink = "El puntaje ingresado no es válido, verifique la escala.";
			return cumple;

		}
		mensajeLink = null;
		return cumple;
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracion() {
		String cadena =
			"select trab.* from planificacion.condicion_trabajo trab"
				+ " where trab.activo is TRUE order by trab.orden asc";
		listaValoracion = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
	}

	public List<EscalaCondTrab> traerEscalaCondTrab(Long idCondicionTrabajo) {

		Query q =
			em.createQuery("select EscalaCondTrab from EscalaCondTrab EscalaCondTrab " +
					" where EscalaCondTrab.activo is true and EscalaCondTrab.condicionTrabajo.idCondicionTrabajo = :idCondicionTrabajo order by desde asc, hasta asc");
		q.setParameter("idCondicionTrabajo", idCondicionTrabajo);
		List<EscalaCondTrab> lista = q.getResultList();
		return lista;
	}

	public String getMensajeLink() {
		return mensajeLink;
	}

	public void setMensajeLink(String mensajeLink) {
		this.mensajeLink = mensajeLink;
	}

	public List<CondicionTrabajoDTO> getListaCondicionDto() {
		return listaCondicionDto;
	}

	public void setListaCondicionDto(List<CondicionTrabajoDTO> listaCondicionDto) {
		this.listaCondicionDto = listaCondicionDto;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public List<CondicionTrabajo> getListaValoracion() {
		return listaValoracion;
	}

	public void setListaValoracionLink8(List<CondicionTrabajo> listaValoracion) {
		this.listaValoracion = listaValoracion;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
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
