package py.com.excelsis.sicca.seleccion.session.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalAbiertas;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalReferencial;
import py.com.excelsis.sicca.entity.EvalReferencialTipoeval;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalAbiertasList;
import py.com.excelsis.sicca.session.ConcursoPuestoDetListCustom;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.PaisDptoCiudadBarrio;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ModalidadOcupacion;

@Scope(ScopeType.CONVERSATION)
@Name("admDisPue451FC")
public class AdmDisPue451FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(required = false, create = true)
	EvalAbiertasList lEvalsAbiertasList;

	PaisDptoCiudadBarrio paisDptoCiudadBarrio;
	private ModalidadOcupacion modalidadOcupacion;
	private Integer cantVacancia = 0;
	private Integer cantPostulantes = 0;
	private Integer cantSacar = 0;
	List<ConcursoPuestoDet> lista;
	ConcursoPuestoDetListCustom concursoPuestoDetListCustom;
	Date fechaSystem;
	private String from;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String codActividad;// se agrego para la incidencia 0001643
	private Boolean mostrarModal;

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

	private void initCab() {
		paisDptoCiudadBarrio =
			(PaisDptoCiudadBarrio) Component.getInstance(PaisDptoCiudadBarrio.class, true);
		paisDptoCiudadBarrio.updateDepartamento();
		paisDptoCiudadBarrio.updateCiudad();
		paisDptoCiudadBarrio.updateBarrio();
		calculoCantidadSacar(grupoPuestosController.getIdConcursoPuestoAgr());
	}

	public void init() {
		initCab();
		search();

		validarOee(grupoPuestosController.getConcursoPuestoAgr().getConcurso());
	}

	private Boolean validacionesIteracion() {
		Integer cantSelec = 0;
		for (ConcursoPuestoDet o : lista) {
			if (o.getSeleccionado() != null && o.getSeleccionado()) {
				cantSelec++;
				if (o.getObservacion() == null || o.getObservacion() != null
					&& o.getObservacion().trim().isEmpty()) {
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU451_errNoDesc"));
					return false;
				} else if (o.getObservacion().length() > 250) {
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
						+ ": Observación (250)");
				}
			}
		}
		if (cantSelec != cantSacar) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU451_errSeleccioyTopeNoCoin")
				+ ". La cantidad de puestos a sacar es: " + cantSacar);
			return false;
		}
		return true;
	}

	private Boolean precond() {
		/**
		 * Incidencia 0001649 Verificar que la cantidad que se quiere disminuir sea la correcta: realiza el calculo por actividad
		 */
		calculoCantidadSacar(grupoPuestosController.getIdConcursoPuestoAgr());
		/**
		 * fin incidencia 0001649
		 */
		Boolean respuesta = validacionesIteracion();
		return respuesta;
	}

	private EstadoDet calEstadoDet(String descripcionDet, String descripcionCab) {
		Query q =
			em.createQuery("select EstadoDet from EstadoDet EstadoDet "
				+ " where EstadoDet.activo is true and EstadoDet.descripcion='" + descripcionDet
				+ "'  " + " and EstadoDet.estadoCab.descripcion ='" + descripcionCab + "'");
		List<EstadoDet> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0);
		}
		return null;
	}

	private EstadoCab calEstadoCab(String desc) {
		Query q =
			em.createQuery("select EstadoCab from EstadoCab EstadoCab "
				+ " where EstadoCab.activo is true and EstadoCab.descripcion= :desc ");
		q.setParameter("desc", desc);
		List<EstadoCab> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0);
		}
		return null;
	}

	/**
	 * Verificar que la cantidad que se quiere disminuir sea la correcta:
	 * 
	 * @param idGrupo
	 *            grupo que recibe como parametro
	 */
	private void calculoCantidadSacar(Long idGrupo) {
		try {
			/**
			 * SI este CU fue llamado desde la actividad ‘EVALUACION DOCUMENTAL’ Contar cantidad de Postulantes Aprobados:
			 */
			Long cntPuesto;
			Long cntPorActividad = new Long(0);
			DatosGrupoPuesto datosGrupoPuesto = obtenerDatosGrupoPuesto();
			try {
				cntPuesto =
					(Long) em.createQuery("Select count(det) from ConcursoPuestoDet det"
						+ " where det.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and det.activo is true ").setParameter("idGrupo", idGrupo).getSingleResult();
			} catch (NoResultException e) {
				cntPuesto = new Long(0);
			}
			if (codActividad.equalsIgnoreCase("EVALUACION_DOCUMENTAL")) {
				try {
					cntPorActividad =
						(Long) em.createQuery("Select count(e) from EvalDocumentalCab e "
							+ " where e.postulacion.activo=true and e.activo=true and e.aprobado=true and e.tipo='EVALUACION DOCUMENTAL' "
							+ " and e.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idGrupo).getSingleResult();
				} catch (NoResultException e) {
					cntPorActividad = new Long(0);
				}

			} else if (codActividad.equalsIgnoreCase("REALIZAR_EVALUACIONES")) {
				/**
				 * SINO SI este CU fue llamado desde la actividad ‘REALIZAR EVALUACIONES OEE POR GRUPO’ Contar cantidad de Postulantes Aprobados
				 */
				try {
					
					
					if(datosGrupoPuesto.getPorMinFinEvaluacion() != null && datosGrupoPuesto.getPorMinFinEvaluacion()){
						StringBuffer var1 = new StringBuffer();
						var1.append(" SELECT Count(evalrefere0_.id_eval_referencial_postulante) AS col_0_0_ FROM  ");
						var1.append(" seleccion.eval_referencial_postulante evalrefere0_, seleccion.postulacion postulacio1_ ");
						var1.append(" JOIN seleccion.datos_grupo_puesto datos on datos.id_concurso_puesto_agr = postulacio1_.id_concurso_puesto_agr   ");
						var1.append(" WHERE evalrefere0_.id_postulacion = postulacio1_.id_postulacion  ");
						var1.append(" AND evalrefere0_.id_concurso_puesto_agr = :idGrupo AND evalrefere0_.activo = true  ");
						var1.append(" AND postulacio1_.activo = true ");
						var1.append(" AND evalrefere0_.puntaje_realizado >= datos.porc_minimo  ");
						var1.append(" AND postulacio1_.id_postulacion not in (select id_postulacion from seleccion.eval_referencial  ");
						var1.append(" where presente is false and id_postulacion in ");
						var1.append(" (	select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = :idGrupo))  ");
						
						Query q = em.createNativeQuery(var1.toString());
						q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
						
						cntPorActividad = ((java.math.BigInteger) q.getSingleResult()).longValue();
						
						if(cntPorActividad == 0){
													
							//tambien se debe controlar que los participantes hayan estado presentes, sino, esto tambien diminuye la cantidad de postulantes.
							String sql = " select count (distinct pos.id_postulacion) from seleccion.postulacion pos "
									+ " join seleccion.eval_referencial eval_referencial on eval_referencial.id_postulacion = pos.id_postulacion "
									+ " and eval_referencial.presente = true and eval_referencial.puntaje_realizado > 0 "
									+ " and id_eval_referencial_tipoeval = (select max(id_eval_referencial_tipoeval) "
									+ "							from seleccion.eval_referencial where pos.id_postulacion in "
									+ "							(select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "
									+								grupoPuestosController.getIdConcursoPuestoAgr()+"))"
									
									+ " where pos.id_postulacion in (select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = "
									+ grupoPuestosController.getIdConcursoPuestoAgr()+") ";
							
							cntPorActividad = ((java.math.BigInteger) em.createNativeQuery(sql).getSingleResult()).longValue(); 
						
						
						}
						
						
						
					}else if (datosGrupoPuesto.getPorMinPorEvaluacion() != null && datosGrupoPuesto.getPorMinPorEvaluacion()){
						
						
						
						//se busca el ultimo tipo de evaluacion que se haya realizado					
						String sql = " select * from seleccion.eval_referencial_tipoeval tipoeval where tipoeval.fecha_cierre_evaluacion is not null "
								+ " and id_concurso_puesto_agr =  "+grupoPuestosController.getIdConcursoPuestoAgr() 
								+ " order by tipoeval.fecha_cierre_evaluacion desc ";
						
						List<EvalReferencialTipoeval> listTipoeval = em.createNativeQuery(sql, EvalReferencialTipoeval.class).getResultList();
												
						if(listTipoeval.size()!= 0 && listTipoeval.get(0) != null){
							//Se trabajara con el view de EvalAbiertas para verificar que se haya aprobado la ultima evaluacion
							
							String sql2 = " select * from seleccion.eval_abiertas where "
									+ " id_concurso_puesto_agr =  "+grupoPuestosController.getIdConcursoPuestoAgr()
									+ " and id_eval_referencial_tipoeval = "+listTipoeval.get(0).getIdEvalReferencialTipoeval();							
							
							
							List <EvalAbiertas> evalAbiertas = em.createNativeQuery(sql2, EvalAbiertas.class).getResultList();
							
							if(evalAbiertas.size() != 0 )
								//cuenta los postulantes que hayan pasado la ultima evaluacion
								cntPorActividad =  new Long (this.contarAproboUltEval(evalAbiertas));
							
						}else{
							StringBuffer var1 = new StringBuffer();
							var1.append("SELECT Count(evalrefere0_.id_eval_referencial_postulante) AS col_0_0_ FROM  ");
							var1.append("seleccion.eval_referencial_postulante evalrefere0_, seleccion.postulacion postulacio1_ ");
							var1.append(" JOIN seleccion.datos_grupo_puesto datos on datos.id_concurso_puesto_agr = postulacio1_.id_concurso_puesto_agr   ");
							var1.append(" WHERE evalrefere0_.id_postulacion = postulacio1_.id_postulacion  ");
							var1.append("AND evalrefere0_.id_concurso_puesto_agr = :idGrupo AND evalrefere0_.activo = true  ");
							var1.append("AND postulacio1_.activo = true ");
							var1.append(" AND evalrefere0_.puntaje_realizado >= datos.porc_minimo  ");
							var1.append(" AND postulacio1_.id_postulacion not in (select id_postulacion from seleccion.eval_referencial  ");
							var1.append(" where presente is false and id_postulacion in ");
							var1.append(" (	select id_postulacion from seleccion.postulacion where id_concurso_puesto_agr = :idGrupo))  ");
							
							Query q = em.createNativeQuery(var1.toString());
							q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
							
							cntPorActividad = ((java.math.BigInteger) q.getSingleResult()).longValue();
						}
						
															
					}
						

				} catch (NoResultException e) {
					cntPorActividad = new Long(0);
				}
			} else if (codActividad.equalsIgnoreCase("COMPLETAR_CARPETAS")) {
				/**
				 * SINO SI este CU fue llamado desde la actividad ‘COMPLETAR CARPETAS’ Contar cantidad de postulantes activos:
				 */
				try {
					cntPorActividad =
						(Long) em.createQuery("Select count(p) from Postulacion p "
							+ " p.activo=true and p.usuCancelacion is null and p.fechaCancelacion is null "
							+ " and p.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idGrupo).getSingleResult();
				} catch (NoResultException e) {
					cntPorActividad = new Long(0);
				}
			}

			cantVacancia = cntPuesto.intValue();
			cantPostulantes = cntPorActividad.intValue();
			cantSacar = (cntPuesto.intValue() - cntPorActividad.intValue()) + 1;
			
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	
	
	private Integer contarAproboUltEval(List<EvalAbiertas> evalAbiertas) {
		// Traer la última evaluacion que recibió el postulante
		Integer cantidadAprobados = 0;
		
		for (EvalAbiertas eval : evalAbiertas  ){
			
		
			Query q = em
					.createQuery("select EvalReferencial from EvalReferencial EvalReferencial "
							+ " where EvalReferencial.postulacion.idPostulacion = "
							+ eval.getPostulacion().getIdPostulacion()
							+ " and EvalReferencial.evalReferencialTipoeval.concursoPuestoAgr.idConcursoPuestoAgr = "
							+ grupoPuestosController.getIdConcursoPuestoAgr()
							+ " order by EvalReferencial.idEvalReferencial desc ");
			List<EvalReferencial> lista = q.getResultList();
			
			if (lista.size() > 0) {
				EvalReferencial ultEvalRecibida = lista.get(0);
				if(ultEvalRecibida != null){
								
					if (ultEvalRecibida.getAprobado() != null && ultEvalRecibida.getAprobado() && ultEvalRecibida.isPresente()) {
						cantidadAprobados += 1;
					} 
				}
			}
			
		}
		
		return cantidadAprobados;
	}
	

	private DatosGrupoPuesto obtenerDatosGrupoPuesto() {
		Query q = em
				.createQuery("select DatosGrupoPuesto from DatosGrupoPuesto DatosGrupoPuesto "
						+ "where DatosGrupoPuesto.activo is true "
						+ "and DatosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getConcursoPuestoAgr()
								.getIdConcursoPuestoAgr()
						);
		return (DatosGrupoPuesto)q.getSingleResult();
		

	}

	public String save() {
		try {
			fechaSystem = new Date();
			if (precond()) {

				EstadoDet estadoDet1 = calEstadoDet("LIBRE", "CONCURSO");
				EstadoCab estadoCab = calEstadoCab("VACANTE");
				EstadoDet estadoDet2 =
					calEstadoDet("DISMINUCION POR FALTA DE POSTULANTES", "CONCURSO");
				for (ConcursoPuestoDet o : lista) {
					// Actualizar el ESTADO de los PUESTOS:
					if (o.getSeleccionado()) {
						o.getPlantaCargoDet().setEstadoDet(estadoDet1);
						o.getPlantaCargoDet().setEstadoCab(estadoCab);
						o.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
						o.getPlantaCargoDet().setFechaMod(fechaSystem);
						o.setPlantaCargoDet(em.merge(o.getPlantaCargoDet()));
						o.setEstadoDet(estadoDet2);
						o.setUsuMod(usuarioLogueado.getCodigoUsuario());
						o.setFechaMod(fechaSystem);
						o.setActivo(false);
						o = em.merge(o);

						/**
						 * Por cada id_concurso_puesto_det: llamar al CU604 (le envía como parámetros el id_concurso_puesto_det, y las cadenas ‘PUESTO’, ‘DISMINUIDO’)
						 */
						reponerCategoriasController.reponerCategorias(o, "PUESTO", "DISMINUIDO");
						/**
						 * fin
						 */
					}

				}

				ConcursoPuestoAgr grupo =
					em.find(ConcursoPuestoAgr.class, grupoPuestosController.getIdConcursoPuestoAgr());
				insertarPublicacionPortal(grupoPuestosController.getIdConcursoPuestoAgr(), grupo.getConcurso().getIdConcurso(), genTextoPublicacion());
				
				grupo.setCantidadPuestos(cantVacancia - cantSacar );
				this.mostrarModal = false;
				em.merge(grupo);
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				search();
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return "FAIL";
		}
		return "OK";
	}

	private void insertarPublicacionPortal(Long idConcursoPuestoAgr, Long idConcurso, String texto) {
		PublicacionPortal entity = new PublicacionPortal();
		entity.setFechaAlta(new Date());
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		entity.setActivo(true);
		entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(idConcursoPuestoAgr);
		entity.setConcurso(new Concurso());
		entity.getConcurso().setIdConcurso(idConcurso);
		entity.setTexto(texto);
		em.persist(entity);

	}

	private Integer calCantVacancias() {
		return cantVacancia + cantSacar;
	}

	public String todos() {
		paisDptoCiudadBarrio.setIdBarrio(null);
		paisDptoCiudadBarrio.setIdCiudad(null);
		paisDptoCiudadBarrio.setIdDpto(null);
		modalidadOcupacion = null;
		return "";
	}

	private String genTextoPublicacion() {
		StringBuffer texto = new StringBuffer();
		String h1O = "<h1>";
		String h1C = "</h1>";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		texto.append(h1O + "La cantidad de vacancias en fecha " + sdf.format(fechaSystem) + " es "
			+ cantVacancia
			+ ". Se  disminuyó "+cantSacar+" puestos por no contar con la cantidad suficiente de postulantes." + h1C);
		return texto.toString();
	}

	public String search() {

		StringBuffer SQL = new StringBuffer();
		StringBuffer elWhere = new StringBuffer();
		SQL.append("select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet ");
		elWhere.append(" WHERE concursoPuestoDet.activo is true "
			+ " and concursoPuestoAgr.idConcursoPuestoAgr = "
			+ grupoPuestosController.getIdConcursoPuestoAgr());
		if (paisDptoCiudadBarrio != null) {
			if (paisDptoCiudadBarrio.getIdBarrio() != null) {
				elWhere.append(" AND plantaCargoDet.oficina.barrio.idBarrio =  "
					+ paisDptoCiudadBarrio.getIdBarrio());
			}
			if (paisDptoCiudadBarrio.getIdCiudad() != null) {
				elWhere.append(" AND plantaCargoDet.oficina.ciudad.idCiudad =  "
					+ paisDptoCiudadBarrio.getIdCiudad());
			}
			if (paisDptoCiudadBarrio.getIdDpto() != null) {
				elWhere.append(" AND plantaCargoDet.oficina.ciudad.departamento.idDepartamento ="
					+ paisDptoCiudadBarrio.getIdDpto());
			}
		}
		if (modalidadOcupacion != null && modalidadOcupacion.getValor() != null) {
			if (modalidadOcupacion.getValor().equalsIgnoreCase(ModalidadOcupacion.PERMANENTE.getValor())) {
				elWhere.append(" AND plantaCargoDet.permanente = " + true);
			} else if (modalidadOcupacion.getValor().equalsIgnoreCase(ModalidadOcupacion.CONTRATADO.getValor())) {
				elWhere.append(" AND plantaCargoDet.contratado = " + true);
			}
		}
		SQL.append(elWhere);
		concursoPuestoDetListCustom =
			(ConcursoPuestoDetListCustom) Component.getInstance(ConcursoPuestoDetListCustom.class, true);
		lista = concursoPuestoDetListCustom.search(SQL.toString(), null);
		System.out.println(concursoPuestoDetListCustom.getResultCount());
		return "OK";

	}

	public void marcarTodos() {
		for (ConcursoPuestoDet o : lista) {
			o.setSeleccionado(true);
		}
	}

	public void desMarcarTodos() {
		for (ConcursoPuestoDet o : lista) {
			o.setSeleccionado(false);
		}
	}

	public String modalidadString(PlantaCargoDet planta) {

		if (planta.getPermanente() != null && planta.getPermanente())
			return "PERMANENTE";
		if (planta.getContratado() != null && planta.getContratado())
			return "CONTRATADO";
		return "-";
	}

	private List<PuestoConceptoPago> lPuestoConceptoPago(Boolean permanente, Long idPlantaCargoDet) {
		String elWhere =
			" where PuestoConceptoPago.activo is true and PuestoConceptoPago.plantaCargoDet.idPlantaCargoDet = "
				+ idPlantaCargoDet;
		if (permanente) {
			elWhere += " and PuestoConceptoPago.objCodigo = 111 ";
		}
		Query q =
			em.createQuery("select PuestoConceptoPago from PuestoConceptoPago PuestoConceptoPago "
				+ elWhere);
		return q.getResultList();
	}

	public String categoriaString(PlantaCargoDet planta) {
		List<PuestoConceptoPago> lResultado = null;
		String categoria = "";
		if (planta.getPermanente() != null && planta.getPermanente()) {
			lResultado = lPuestoConceptoPago(true, planta.getIdPlantaCargoDet());
		}
		if (planta.getContratado() != null && planta.getContratado()) {
			lResultado = lPuestoConceptoPago(false, planta.getIdPlantaCargoDet());
		}
		if (lResultado != null) {
			for (PuestoConceptoPago o : lResultado) {
				categoria += o.getCategoria() + " ";
			}
		}
		if (categoria.isEmpty()) {
			categoria = "Sin Categoría";
		}
		return categoria;
	}

	public GrupoPuestosController getGrupoPuestosController() {
		return grupoPuestosController;
	}

	public void setGrupoPuestosController(GrupoPuestosController grupoPuestosController) {
		this.grupoPuestosController = grupoPuestosController;
	}

	public PaisDptoCiudadBarrio getPaisDptoCiudadBarrio() {
		return paisDptoCiudadBarrio;
	}

	public void setPaisDptoCiudadBarrio(PaisDptoCiudadBarrio paisDptoCiudadBarrio) {
		this.paisDptoCiudadBarrio = paisDptoCiudadBarrio;
	}

	public ModalidadOcupacion getModalidadOcupacion() {
		return modalidadOcupacion;
	}

	public void setModalidadOcupacion(ModalidadOcupacion modalidadOcupacion) {
		this.modalidadOcupacion = modalidadOcupacion;
	}

	public Integer getCantVacancia() {
		return cantVacancia;
	}

	public void setCantVacancia(Integer cantVacancia) {
		this.cantVacancia = cantVacancia;
	}

	public Integer getCantPostulantes() {
		return cantPostulantes;
	}

	public void setCantPostulantes(Integer cantPostulantes) {
		this.cantPostulantes = cantPostulantes;
	}

	public Integer getCantSacar() {
		return cantSacar;
	}

	public void setCantSacar(Integer cantSacar) {
		this.cantSacar = cantSacar;
	}

	public List<ConcursoPuestoDet> getLista() {
		return lista;
	}

	public void setLista(List<ConcursoPuestoDet> lista) {
		this.lista = lista;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

}
