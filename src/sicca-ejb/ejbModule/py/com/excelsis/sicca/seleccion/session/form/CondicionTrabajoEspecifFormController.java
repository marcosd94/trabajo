package py.com.excelsis.sicca.seleccion.session.form;


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

import py.com.excelsis.sicca.dto.CondicionTrabajoEspecifDTO;
import py.com.excelsis.sicca.dto.ValoracionTab05CPT;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("condicionTrabajoEspecifFormController")
public class CondicionTrabajoEspecifFormController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -9185098668415919457L;
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
	private List<CondicionTrabajoEspecifDTO> listaDto = new ArrayList<CondicionTrabajoEspecifDTO>();
	private List<ValoracionTab05CPT> listaValoracion = new ArrayList<ValoracionTab05CPT>();
	private SeguridadUtilFormController seguridadUtilFormController;
	private String cu;
	private Boolean habilitar = true;

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
		/* Incidencia 1014 */
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		validarOee(grupo.getConcurso());
		/**/
		buscarCondicionTrabajoEspecificaEdit();
		habilitarPantalla(grupo);
	}
	
	
	/**
	 * Busca datos para editar
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEspecificaEdit() {
		String cad = "select * from planificacion.det_condicion_trabajo_especif det_trab"
				+ " where det_trab.id_concurso_puesto_agr = " + idConcursoPuestoAgr+ " and det_trab.activo=true";

		List<DetCondicionTrabajoEspecif> listaDet = new ArrayList<DetCondicionTrabajoEspecif>();
		listaDet = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class).getResultList();
		
		/**
		 * Incidencia 0001747
		 * */
		if(cu!=null && cu.equals("404")){
			//Si no hay datos del grupo, se debe buscar por el CPT
			if (listaDet == null || listaDet.size() == 0){
				listaDet = cargarCondicionesDelCpt();
			}
		}
		/**fin**/
		

		String cadena = "select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajoEspecif> lista = new ArrayList<CondicionTrabajoEspecif>();

		lista = em.createNativeQuery(cadena, CondicionTrabajoEspecif.class).getResultList();
		listaDto = new ArrayList<CondicionTrabajoEspecifDTO>();
		for (CondicionTrabajoEspecif condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajoEspecif det : listaDet) {
				if (det.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif().equals(condicion.getIdCondicionesTrabajoEspecif())) {
					esta = true;
					CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
					dto.setCondicionTrabajoEspecif(det.getCondicionTrabajoEspecif());
					dto.setId(det.getIdDetCondicionTrabajoEspecif());
					dto.setActivo(det.getActivo());
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondTrabEspecif());
					listaDto.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
				dto.setCondicionTrabajoEspecif(condicion);
				listaDto.add(dto);
			}
		}
	}

	private List<DetCondicionTrabajoEspecif> cargarCondicionesDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		List<DetCondicionTrabajoEspecif> nuevaLista = new ArrayList<DetCondicionTrabajoEspecif>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null && concursoPuestoAgr.getConcursoPuestoDets().size() > 0){
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();
			
			String cad =
				"select * from planificacion.det_condicion_trabajo_especif det_trab "
					+ " where det_trab.id_cpt = " + cpt.getId() 
					+ " and det_trab.activo= true";

			List<DetCondicionTrabajoEspecif> lista = new ArrayList<DetCondicionTrabajoEspecif>();
			lista = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class).getResultList();
			
			if (lista != null){
				for(DetCondicionTrabajoEspecif detCondicionTrabajoEspecif : lista){
					DetCondicionTrabajoEspecif nuevo = new DetCondicionTrabajoEspecif();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setCondicionTrabajoEspecif(detCondicionTrabajoEspecif.getCondicionTrabajoEspecif());
					nuevo.setPuntajeCondTrabEspecif(detCondicionTrabajoEspecif.getPuntajeCondTrabEspecif());
					nuevo.setAjustes(detCondicionTrabajoEspecif.getAjustes());
					nuevo.setJustificacion(detCondicionTrabajoEspecif.getJustificacion());
					nuevo.setTipo("GRUPO");
					nuevaLista.add(nuevo);
				}
			}
		}
		
		return nuevaLista;
	}

	public void guardar() {
		if (!validacionEscala())
			return;
		try {
			for (CondicionTrabajoEspecifDTO dto : listaDto) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null && dto.getJustificacion() != null) {
					if (dto.getId() == null) {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab.setActivo(dto.getActivo());
						detCondTrab.setCondicionTrabajoEspecif(dto.getCondicionTrabajoEspecif());
						detCondTrab.setPuntajeCondTrabEspecif(dto.getPuntaje());
						detCondTrab.setAjustes(dto.getAjustes().trim().toUpperCase());
						detCondTrab.setJustificacion(dto.getJustificacion().trim().toUpperCase());
						
						detCondTrab.setTipo("GRUPO");
						ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
						detCondTrab.setConcursoPuestoAgr(concursoPuestoAgr);
						
						em.persist(detCondTrab);
					} else {
						DetCondicionTrabajoEspecif detCondTrabBD = new DetCondicionTrabajoEspecif();
						detCondTrabBD = em.find(DetCondicionTrabajoEspecif.class, dto.getId());
						if(cu==null || !cu.equals("404")){
							if (!detCondTrabBD.getPuntajeCondTrabEspecif().equals(dto.getPuntaje())) {
								detCondTrabBD.setActivo(false);//se inactiva
								em.merge(detCondTrabBD);
								/**COPIA*/
								DetCondicionTrabajoEspecif detCondTrabEspcCopia = new DetCondicionTrabajoEspecif();// se crea una copia con la modificacion
								detCondTrabEspcCopia.setActivo(true);
								detCondTrabEspcCopia.setCondicionTrabajoEspecif(dto.getCondicionTrabajoEspecif());
								detCondTrabEspcCopia.setPuntajeCondTrabEspecif(dto.getPuntaje());
								detCondTrabEspcCopia.setTipo("GRUPO");
								detCondTrabEspcCopia.setAjustes(dto.getAjustes().trim().toUpperCase());
								detCondTrabEspcCopia.setJustificacion(dto.getJustificacion().trim().toUpperCase());
								ConcursoPuestoAgr concursoPuestoAgr =
									em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
								detCondTrabEspcCopia.setConcursoPuestoAgr(concursoPuestoAgr);
								em.persist(detCondTrabEspcCopia);
								}
						}else{
							detCondTrabBD.setActivo(dto.getActivo());
							detCondTrabBD.setPuntajeCondTrabEspecif(dto.getPuntaje());
							detCondTrabBD.setAjustes(dto.getAjustes().trim().toUpperCase());
							detCondTrabBD.setJustificacion(dto.getJustificacion().trim().toUpperCase());
							em.merge(detCondTrabBD);
						}
						
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab = em.find(DetCondicionTrabajoEspecif.class,	dto.getId());
						em.remove(detCondTrab);
					}
				}
				em.flush();
			}
			buscarCondicionTrabajoEspecificaEdit();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	
	
	private Boolean validacionEscala() {
		for (CondicionTrabajoEspecifDTO dto : listaDto) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto))
					return false;
			}
		}
		return true;
	}
	
	
	
	public Boolean verificarValoracion(CondicionTrabajoEspecifDTO condicion) {
		List<EscalaCondTrabEspecif> lista = new ArrayList<EscalaCondTrabEspecif>();
		if (condicion.getPuntaje() != null && listaValoracion.size() > 0) {
			lista = listaValoracion.get(0).getListaEscala();
			Float valor = condicion.getPuntaje();
			Boolean cumple = false;
			for (EscalaCondTrabEspecif o : lista) {
				Float desde = new Float("" + o.getDesde());
				Float hasta = new Float("" + o.getHasta());
				if (desde.floatValue() <= valor.floatValue()
						&& hasta.floatValue() >= valor.floatValue()) {
					cumple = true;
				}
			}
			if (!cumple) {
				mensajeLink = "El puntaje ingresado no es valido, verifique la escala";
				return cumple;
			}
		}
		mensajeLink = null;
		return true;
	}
	
	
	public void buscarValoracion() {
		String cadena = "select escala.* from planificacion.escala_cond_trab_especif escala "
				+ "where escala.activo is TRUE order by desde, hasta";
		List<EscalaCondTrabEspecif> lista = new ArrayList<EscalaCondTrabEspecif>();
		listaValoracion = new ArrayList<ValoracionTab05CPT>();
		lista = em.createNativeQuery(cadena, EscalaCondTrabEspecif.class).getResultList();
		
		String cad = "select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo is TRUE order by cond.orden";
		
		List<CondicionTrabajoEspecif> listaCondTrabEspec = new ArrayList<CondicionTrabajoEspecif>();

		listaCondTrabEspec = em.createNativeQuery(cad,CondicionTrabajoEspecif.class).getResultList();

		for (CondicionTrabajoEspecif o : listaCondTrabEspec) {
			ValoracionTab05CPT valor = new ValoracionTab05CPT();
			valor.setCondicionTrabajoEspecif(o);
			valor.setListaEscala(lista);
			listaValoracion.add(valor);
		}
	}
	
	private void habilitarPantalla(ConcursoPuestoAgr concursoPuestoAgr) {
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

	

	public String getMensajeLink() {
		return mensajeLink;
	}

	public void setMensajeLink(String mensajeLink) {
		this.mensajeLink = mensajeLink;
	}

	public List<CondicionTrabajoEspecifDTO> getListaDto() {
		return listaDto;
	}

	public void setListaDto(List<CondicionTrabajoEspecifDTO> listaDto) {
		this.listaDto = listaDto;
	}
	
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}
	
	public List<ValoracionTab05CPT> getListaValoracion() {
		return listaValoracion;
	}

	public void setListaValoracion(List<ValoracionTab05CPT> listaValoracion) {
		this.listaValoracion = listaValoracion;
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
