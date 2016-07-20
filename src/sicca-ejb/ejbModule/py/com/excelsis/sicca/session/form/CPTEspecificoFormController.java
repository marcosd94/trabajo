package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.CondicionSeguridadDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoEspecifDTO;
import py.com.excelsis.sicca.dto.ContenidoFuncionalDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.entity.AntecedenteGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.DetContenidoFuncionalHome;
import py.com.excelsis.sicca.session.DetReqMinHome;
import py.com.excelsis.sicca.session.PlantaCargoDetHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("cptEspecificoFormController")
public class CPTEspecificoFormController implements Serializable {

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
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	//@In(create = true)
	//PlantaCargoDetHome plantaCargoDetHome;
	
	private List<SelectItem> cptSelecItem = new ArrayList<SelectItem>();
	private Long idTipoCpt;
	private Long idCpt;
	private String codigoCpt;
	private Long idCptFromList;
	private String descripcionCpt;
	//private List<PlantaCargoDet> plantaCargoDets = new ArrayList<PlantaCargoDet>();
	private Concurso concurso;
	
	/**
	 * from CU162-53
	 **/
	private String cu;
	private String fromCU;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	
	public void init(){
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = concursoPuestoAgr.getConcurso();
		idConcursoPuestoAgr = concursoPuestoAgr.getIdConcursoPuestoAgr();
		if(idCptFromList == null)	
			idCptFromList =  concursoPuestoAgr.getConcursoPuestoDets().get(0).getPlantaCargoDet().getCpt().getIdCpt();
		idTipoCpt = concursoPuestoAgr.getConcursoPuestoDets().get(0).getPlantaCargoDet().getCpt().getTipoCpt().getIdTipoCpt();
		buscarCodigoCpt();
		updateCpt();
	}
	
	@SuppressWarnings("unchecked")
	public void updateCpt() {
		String cadena =
			" select cpt.* from planificacion.cpt cpt " + "where cpt.activo is true "
				+ "and cpt.id_tipo_cpt = " + idTipoCpt;

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
		cptSelecItem = new ArrayList<SelectItem>();
		cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (Cpt cpt : lista) {
				cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
			}
		}
	}
	
	private void buscarCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCptFromList);
		descripcionCpt = cptActual.getDenominacion();
		idCpt = idCptFromList;
		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}
	
	@SuppressWarnings("unchecked")
	public void findCpt() {
		try {
			if (codigoCpt != null && !codigoCpt.equals("")) {
				Integer nivelCpt = null;
				Integer gradoMin = null;
				Integer gradoMax = null;
				Integer numero = null;
				Integer nroEspecifico = null;
				String[] arrayCodigo = codigoCpt.split("\\.");
				for (int i = 0; i < arrayCodigo.length; i++) {
					if (i == 0)
						nivelCpt = new Integer(arrayCodigo[i]);
					if (i == 1)
						gradoMin = new Integer(arrayCodigo[i]);
					if (i == 2)
						gradoMax = new Integer(arrayCodigo[i]);
					if (i == 3)
						numero = new Integer(arrayCodigo[i]);
					if (i == 4)
						nroEspecifico = new Integer(arrayCodigo[i]);
				}
				String cadena =
					" select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
				if (lista.size() > 0) {
					for (Cpt cpt : lista) {
						cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
						idCpt = cpt.getIdCpt();
					}
				}

			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese un código válido");

		}

	}
	
	public void obtenerCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCpt);

		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}
	
	public String getUrlToPageCpt() {
		return "/seleccion/basesCondiciones/cptEspecifico/CptEspecificoList.xhtml?from=seleccion/basesCondiciones/cptEspecifico/CPTEspecifico&tipoCpt="
			+ idTipoCpt + "&tipo=especifico";
	}
	
	public void guardar(){
		try {
			if(idCpt != null){
				for(int i = 0; i < concursoPuestoAgr.getConcursoPuestoDets().size(); i++){
					concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet().setCpt(em.find(Cpt.class, idCpt));
					
					em.createNativeQuery ("delete from planificacion.det_descripcion_cont_funcional " 
							+ "where planificacion.det_descripcion_cont_funcional.id_contenido_funcional in "
							+ "(select planificacion.det_contenido_funcional.id_det_contenido_funcional " 
							+ "from planificacion.det_contenido_funcional where " 
							+ "planificacion.det_contenido_funcional.id_planta_cargo_det = " 
							+ concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet().getIdPlantaCargoDet()
							+ " or planificacion.det_contenido_funcional.id_concurso_puesto_agr = " 
							+ concursoPuestoAgr.getIdConcursoPuestoAgr()+" ); ").executeUpdate();
					
					em.createNativeQuery(
							" delete from planificacion.det_contenido_funcional "
							+ "where planificacion.det_contenido_funcional.id_planta_cargo_det = " 
							+ concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet().getIdPlantaCargoDet()
							+ " or planificacion.det_contenido_funcional.id_concurso_puesto_agr = " 
							+ concursoPuestoAgr.getIdConcursoPuestoAgr()).executeUpdate();
					
					em.createNativeQuery ("delete from planificacion.det_opciones_convenientes " 
							+ "where planificacion.det_opciones_convenientes.id_det_req_min in " 
							+ "(select planificacion.det_req_min.id_det_req_min " 
							+ "from planificacion.det_req_min where " 
							+ "planificacion.det_req_min.id_planta_cargo_det = " 
							+ concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet().getIdPlantaCargoDet() 
							+ " or planificacion.det_req_min.id_concurso_puesto_agr = " 
							+ concursoPuestoAgr.getIdConcursoPuestoAgr()+" ); ").executeUpdate();
					
					em.createNativeQuery ("delete from planificacion.det_minimos_requeridos " 
							+ "where planificacion.det_minimos_requeridos.id_det_req_min in " 
							+ "(select planificacion.det_req_min.id_det_req_min " 
							+ "from planificacion.det_req_min where " 
							+ "planificacion.det_req_min.id_planta_cargo_det = " 
							+ concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet().getIdPlantaCargoDet()
							+ " or planificacion.det_req_min.id_concurso_puesto_agr = " 
							+ concursoPuestoAgr.getIdConcursoPuestoAgr()+" ); ").executeUpdate();
					
					em.createNativeQuery("delete from planificacion.det_req_min "
							+ "where planificacion.det_req_min.id_planta_cargo_det = " 
							+ concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet().getIdPlantaCargoDet()
							+ "or planificacion.det_req_min.id_concurso_puesto_agr = " 
							+ concursoPuestoAgr.getIdConcursoPuestoAgr()).executeUpdate();
					
					concursoPuestoAgrHome.setInstance(concursoPuestoAgr);
					
				}
			String resultado = concursoPuestoAgrHome.persist();
			if (resultado.equals("persisted")) {
				em.flush();
				}
			}
			buscarContenidoFuncionalPuestoTrabajo();
			guardarLink6();
			buscarRequerimientosMinimosPuestoTrabajo();		
			guardarLink7();

			buscarContenidoFuncional();
			guardarFuncion();
			buscarRequerimientosMinimos();
			guardarRequisitos();

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}
	
	/*****************<CARGA_PARA_CONTENIDO_FUNCIONAL>***********************/
	/*************************<MODIFICACION>*********************************/
	/*************************MODIFICADO: 15/11/2013*************************/
	/*************************AUTOR: RODRIGO VELAZQUEZ***********************/	
	private List<ContenidoFuncionalDTO> listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
	private List<DetContenidoFuncional> listaLink6Aux = new ArrayList<DetContenidoFuncional>();
	@SuppressWarnings("unchecked")
	/*>>>>>>>>>>>>>>>>>>>>>>>>Cargar Funciones<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
	public void guardarLink6() {
		if (!validacionEscalaLink6())
			return;
		try {
			for (ContenidoFuncionalDTO dto : listaDtoLink6) {
				if (dto.getPuntaje() != null) {
					for(int i = 0; i < concursoPuestoAgr.getConcursoPuestoDets().size(); i++){
						DetContenidoFuncional detContenido = new DetContenidoFuncional();
						detContenido.setContenidoFuncional(dto.getContenidoFuncional());
						detContenido.setPuntaje(dto.getPuntaje());
						detContenido.setTipo("PUESTO");
						detContenido.setActivo(true);
						detContenido.setPlantaCargoDet(concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet());

						detContenidoFuncionalHome.setInstance(detContenido);
						String resultado = detContenidoFuncionalHome.persist();
						if (resultado.equals("persisted")) {
							List<DetDescripcionContFuncional> listaDescripcion =
									new ArrayList<DetDescripcionContFuncional>();
							listaDescripcion = dto.getListaDetDescripContFuncional();
							for (DetDescripcionContFuncional desc : listaDescripcion) {
								if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
									DetDescripcionContFuncional descripcion =
											new DetDescripcionContFuncional();
									descripcion.setActivo(desc.getActivo());
									descripcion.setDescripcion(desc.getDescripcion().trim().toUpperCase());
									descripcion.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
									em.persist(descripcion);
								}
							}
							em.flush();
						detContenidoFuncionalHome.clearInstance();
						}
					}
				}

			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}	
	
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncionalEdit() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.id_planta_cargo_det = "
				+ concursoPuestoAgr.getConcursoPuestoDets().get(0).getPlantaCargoDet().getIdPlantaCargoDet();
		listaLink6Aux = new ArrayList<DetContenidoFuncional>();
		listaLink6Aux = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaLink6Aux) {
				ContenidoFuncional contenidoActual = new ContenidoFuncional();
				contenidoActual = det.getContenidoFuncional();
				if (contenidoActual != null
					&& contenidoActual.getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					List<DetDescripcionContFuncional> listaDesc =
						new ArrayList<DetDescripcionContFuncional>();
					listaDesc = searchDetDescContFuncional(det.getIdDetContenidoFuncional());
					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoLink6.add(dto);
				}
			}
			if (!esta) {
				ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
				dto.setContenidoFuncional(contenido);
				List<DetDescripcionContFuncional> listaDesc =
					new ArrayList<DetDescripcionContFuncional>();

				DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
				listaDesc.add(descr);
				dto.setListaDetDescripContFuncional(listaDesc);
				listaDtoLink6.add(dto);
			}
		}

	}	
	
	private Boolean validacionEscalaLink6() {
		for (ContenidoFuncionalDTO dto : listaDtoLink6) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto))
					return false;
			}
		}
		return true;
	}
	
	private void buscarContenidoFuncionalPuestoTrabajo() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.id_cpt = " + idCpt;
		List<DetContenidoFuncional> listaAux = new ArrayList<DetContenidoFuncional>();
		listaAux = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
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
					listaDesc = searchDetDescContFuncional(det.getIdDetContenidoFuncional());
					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoLink6.add(dto);
				}
			}
			if (!esta) {
				ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
				dto.setContenidoFuncional(contenido);
				List<DetDescripcionContFuncional> listaDesc =
					new ArrayList<DetDescripcionContFuncional>();

				DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
				listaDesc.add(descr);
				dto.setListaDetDescripContFuncional(listaDesc);
				listaDtoLink6.add(dto);
			}
		}

	}
	
	/*>>>>>>>>>>>>>>>>>>>>>>>>Cargar Requisitos Minimos<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/	
	private String mensajeLink07;
	private List<DetReqMin> listaLink7Aux = new ArrayList<DetReqMin>();
	
	private Boolean validacionEscalaLink7() {
		for (RequisitosMinimosDTO dto : listaDtoLink7) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracionLink7(dto)) {
					return false;
				}
			}
		}
		return true;
	}	
	
	public Boolean verificarValoracionLink7(RequisitosMinimosDTO requisito) {
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
			mensajeLink07 = "El puntaje ingresado no es valido, verifique la escala";
			return cumple;
		}
		mensajeLink07 = null;
		return cumple;
	}
	
	public void guardarLink7() {
		if (!validacionEscalaLink7())
			return;
		try {
			for (RequisitosMinimosDTO dto : listaDtoLink7) {
				if (dto.getPuntaje() != null) {
					for(int i = 0; i < concursoPuestoAgr.getConcursoPuestoDets().size(); i++){
						DetReqMin detReqMin = new DetReqMin();
						detReqMin.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
						detReqMin.setPuntajeReqMin(dto.getPuntaje());
						detReqMin.setTipo("PUESTO");
						detReqMin.setActivo(true);
						detReqMin.setPlantaCargoDet(concursoPuestoAgr.getConcursoPuestoDets().get(i).getPlantaCargoDet());
						detReqMinHome.setInstance(detReqMin);
						String resultado = detReqMinHome.persist();
						if (resultado.equals("persisted")) {
							List<DetMinimosRequeridos> listaDetMinimos =
								new ArrayList<DetMinimosRequeridos>();
							listaDetMinimos = dto.getListaDetMinimosRequeridos();
							for (DetMinimosRequeridos detMin : listaDetMinimos) {
								if (detMin.getMinimosRequeridos() != null
										&& !detMin.getMinimosRequeridos().trim().isEmpty()) {
									DetMinimosRequeridos mini = new DetMinimosRequeridos();

									mini.setMinimosRequeridos(detMin.getMinimosRequeridos().trim().toUpperCase());
									mini.setDetReqMin(new DetReqMin());
									mini.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());
									mini.setActivo(detMin.getActivo());
									em.persist(mini);
								}

							}
							em.flush();
							List<DetOpcionesConvenientes> listaDetOpc =
								new ArrayList<DetOpcionesConvenientes>();
							listaDetOpc = dto.getListaDetOpcionesConvenientes();
							for (DetOpcionesConvenientes detOpc : listaDetOpc) {
								if (detOpc.getOpcConvenientes() != null
									&& !detOpc.getOpcConvenientes().equals("")) {
									DetOpcionesConvenientes convenientes =
										new DetOpcionesConvenientes();
									convenientes.setOpcConvenientes(detOpc.getOpcConvenientes().trim().toUpperCase());
									convenientes.setDetReqMin(new DetReqMin());
									convenientes.getDetReqMin().setIdDetReqMin((detReqMinHome.getInstance().getIdDetReqMin()));
									convenientes.setActivo(detOpc.getActivo());
									em.persist(convenientes);
								}
							}
							em.flush();
							detReqMinHome.clearInstance();
						}
					}
				}
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}
	
	private List<RequisitosMinimosDTO> listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
	@SuppressWarnings("unchecked")
	private void buscarRequerimientosMinimosPuestoTrabajo() {
		String cad =
			"select * from planificacion.det_req_min det_req" + " where det_req.id_cpt = " + idCpt;
		List<DetReqMin> listaAuxTab3 = new ArrayList<DetReqMin>();
		listaAuxTab3 = em.createNativeQuery(cad, DetReqMin.class).getResultList();

		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();

		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaAuxTab3) {
				if (det.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt().equals(req.getIdRequisitoMinimoCpt())) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv =
						new ArrayList<DetOpcionesConvenientes>();
					listaConv = buscarOpciones(det.getIdDetReqMin());
					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);
					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
					listaReq = buscarMinimos(det.getIdDetReqMin());
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
	
	@SuppressWarnings("unchecked")
	private List<DetDescripcionContFuncional> searchDetDescContFuncional(Long id) {
		String sql =
			"select d.* " + "from planificacion.det_descripcion_cont_funcional d "
				+ "where d.id_contenido_funcional = " + id;
		return em.createNativeQuery(sql, DetDescripcionContFuncional.class).getResultList();
	}
	/*************************<CARGA_PARA_CONCURSO>*********************************/
	/*************************<MODIFICACION>*********************************/
	/*************************MODIFICADO: 04/11/2013*************************/
	/*************************AUTOR: RODRIGO VELAZQUEZ***********************/
	/*>>>>>>>>>>>>>>>>>>>>>>>>Cargar Funciones<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
	List<DetContenidoFuncional> listaAux = new ArrayList<DetContenidoFuncional>();
	private List<ContenidoFuncionalDTO> listaFuncionesDTO = new ArrayList<ContenidoFuncionalDTO>();
	List<DetDescripcionContFuncional> listaDetInactivar =
			new ArrayList<DetDescripcionContFuncional>();
	
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
	
	private Boolean buscarEnListaFunciones(ContenidoFuncional conten) {
		for (ContenidoFuncionalDTO ctn : listaFuncionesDTO) {
			if (ctn.getContenidoFuncional().getIdContenidoFuncional().equals(conten.getIdContenidoFuncional()))
				return true;
		}
		return false;
	}
	String mensaje;
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
	
	private Boolean validacionEscala() {
		for (ContenidoFuncionalDTO dto : listaFuncionesDTO) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto))
					return false;
			}
		}
		return true;
	}
	
	public String guardarFuncion() {
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
	
	/*>>>>>>>>>>>>>>>>>>>>>>>>Cargar Requisitos<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
	private List<RequisitosMinimosDTO> listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
	private List<DetReqMin> listaRequisitosAux = new ArrayList<DetReqMin>();
	@In(create = true)
	DetReqMinHome detReqMinHome;
	private List<DetMinimosRequeridos> listaDetMinimosInactivar = new ArrayList<DetMinimosRequeridos>();
	private List<DetOpcionesConvenientes> listaDetOpcInactivar = new ArrayList<DetOpcionesConvenientes>();
	
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
	
	public String guardarRequisitos() {
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
	/*************************</MODIFICACION>********************************/
	
	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
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

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}
	
	public List<SelectItem> getCptSelecItem() {
		return cptSelecItem;
	}

	public void setCptSelecItem(List<SelectItem> cptSelecItem) {
		this.cptSelecItem = cptSelecItem;
	}
	
	public Long getIdCpt() {
		return idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;

	}
	
	public String getCodigoCpt() {
		return codigoCpt;
	}

	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}
	
	public Long getIdCptFromList() {
		return idCptFromList;
	}

	public void setIdCptFromList(Long idCptFromList) {
		this.idCptFromList = idCptFromList;
		if (idCptFromList != null)
			buscarCodigoCpt();
	}
	
	public String getDescripcionCpt() {
		return descripcionCpt;
	}

	public void setDescripcionCpt(String descripcionCpt) {
		this.descripcionCpt = descripcionCpt;
	}
}
