package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.dto.EvalDocumentalDetDTO;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalDocumentalComis;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.util.IntegrantesDTO;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("listaElegiblesFormController")
public class ListaElegiblesFormController implements Serializable {

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
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	EvalDocumentalCabHome evalDocumentalCabHome;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private String observacion;
	private Integer cantPuestosCubrir;
	private Integer cantVacancia;
	private Integer cantAprobados;

	private List<EvalReferencialPostulante> listaReferencialPostulantes;
	private List<MatrizDocConfigDet> listaDocAdjudicados;
	private List<EvalDocumentalDet> listaEvalDocDet;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		if (concurso != null) {
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null)
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		cantVacancia = buscarCantidadVacancia();
		cantAprobados = buscarCantidadAprobados();
		cantPuestosCubrir = cantVacancia - cantAprobados + 1;
		buscarEvaluacionPostulantes();

	}

	/**
	 * Busca la cantidad de vacancias para el puesto
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadVacancia() {
		Integer resultado = null;
		String sql =
			"select count(puesto_det.*) " + "from seleccion.concurso_puesto_det puesto_det "
				+ "where puesto_det.activo is true " + "and puesto_det.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	/**
	 * Busca la cantidad de aprobados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadAprobados() {
		Integer resultado = null;
		String sql =
			"select count(cab.*) " + "from seleccion.eval_documental_cab cab "
				+ "where cab.activo is true " + "and cab.aprobado is true "
				+ "and cab.id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cab.tipo = 'EVALUACION ADJUDICADOS'";
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	/**
	 * Busca la lista de postulantes a ser evaluados de acuerdo al grupo recibido desde la bandeja
	 */
	@SuppressWarnings("unchecked")
	public void buscarEvaluacionPostulantes() {
		String sql =
			"select eval_post.* "
				+ "from seleccion.eval_referencial_postulante eval_post, seleccion.postulacion postulacion "
				+ "where postulacion.id_Postulacion = eval_post.id_postulacion "
				+ " and eval_post.lista_corta is true " + "and eval_post.seleccionado is false "
				+ " and eval_post.activo is true " + " AND postulacion.usu_cancelacion is null "
				+ " AND postulacion.fecha_cancelacion is null "
				+ " AND postulacion.activo is true " + " and eval_post.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " order by eval_post.puntaje_realizado desc";
		listaReferencialPostulantes = new ArrayList<EvalReferencialPostulante>();
		listaReferencialPostulantes =
			em.createNativeQuery(sql, EvalReferencialPostulante.class).getResultList();
		/* START Producto de la Incidencia Nº: 479 (apartado de Validaciones) */
		recortarLista(listaReferencialPostulantes, calcCantidadRecortar(listaReferencialPostulantes.size()), "BOTTOM-UP");
		/* END Producto de la Incidencia Nº: 479 */
	}

	private Integer calcCantidadRecortar(Integer sizeLista) {
		Integer cantMaxPermitida = cantVacancia.intValue() - cantAprobados.intValue() + 1;
		if (cantMaxPermitida.intValue() < sizeLista.intValue()) {
			return cantMaxPermitida.intValue() - sizeLista.intValue();
		} else {
			return 0;
		}
	}

	private void recortarLista(List<EvalReferencialPostulante> listaARecortar, Integer cantidad, String sentido) {
		if (cantidad.intValue() <= listaARecortar.size() && cantidad.intValue() > 0) {
			if (sentido.equalsIgnoreCase("BOTTOM-UP")) {
				for (int i = 0; i < cantidad; i++) {
					listaARecortar.remove(listaARecortar.size() - 1);
				}
			} else {
				for (int i = 0; i < cantidad; i++) {
					listaARecortar.remove(i);
				}
			}
		}
	}

	/**
	 * Busca la lista de Documentos Adjudicados teniendo en cuenta ciertos criterios
	 */
	@SuppressWarnings("unchecked")
	private void buscarDocsAdjudicados() {
		String sql =
			"select matriz_det.* " + "from seleccion.matriz_doc_config_enc matriz_enc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = matriz_enc.id_concurso_puesto_agr "
				+ "join seleccion.matriz_doc_config_det matriz_det "
				+ "on matriz_det.id_matriz_doc_config_enc = matriz_enc.id_matriz_doc_config_enc "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and matriz_det.activo is true "
				+ "and matriz_det.adjudicacion is true " + "order by matriz_det.nro_orden";
		listaDocAdjudicados = new ArrayList<MatrizDocConfigDet>();
		listaDocAdjudicados = em.createNativeQuery(sql, MatrizDocConfigDet.class).getResultList();
	}

	/**
	 * Inserta datos tanto en la tabla eval_documental_cab como en la tabla eval_documental_det
	 */
	private void insertarEvaluacionDocumental() {
		try {
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				EvalDocumentalCab evalDocumentalCab = new EvalDocumentalCab();
				evalDocumentalCab.setActivo(true);
				evalDocumentalCab.setTipo("EVALUACION ADJUDICADOS");
				evalDocumentalCab.setPostulacion(eval.getPostulacion());
				evalDocumentalCab.setConcursoPuestoAgr(concursoPuestoAgr);
				evalDocumentalCabHome.setInstance(evalDocumentalCab);
				String resultado = evalDocumentalCabHome.persist();
				if (resultado.equals("persisted")) {
					listaEvalDocDet = new ArrayList<EvalDocumentalDet>();
					for (MatrizDocConfigDet matrizDet : listaDocAdjudicados) {
						EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();
						evalDocumentalDet.setActivo(true);
						evalDocumentalDet.setEvalDocumentalCab(evalDocumentalCabHome.getInstance());
						evalDocumentalDet.setMatrizDocConfigDet(matrizDet);
						evalDocumentalDet.setFechaAlta(new Date());
						evalDocumentalDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(evalDocumentalDet);
						listaEvalDocDet.add(evalDocumentalDet);
					}
					em.flush();
					evalDocumentalCabHome.clearInstance();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Actualiza los detalles en caso de que el documento haya sido presentado
	 */
	private void actualizarDocumentalDetalle() {
		try {
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				List<Documentos> listaDocs = new ArrayList<Documentos>();
				listaDocs = buscarDocumentos(eval.getIdEvalReferencialPostulante());
				for (EvalDocumentalDet det : listaEvalDocDet) {
					Boolean aprobado = false;
					if (listaDocs != null) {
						aprobado = verificarExistenciaTipoDoc(listaDocs, det);
					}
					det.setAprobadoConDocumentos(aprobado);
					det.setFechaMod(new Date());
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(det);
				}
				em.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	private List<Documentos> buscarDocumentos(Long id) {
		String sql =
			"select doc.* " + "from general.documentos doc "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post  "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_referencial_postulante eval_ref "
				+ "on eval_ref.id_postulacion = post.id_postulacion " + "where doc.activo is true "
				+ "and eval_ref.id_eval_referencial_postulante = " + id;
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}

	/**
	 * Verifica existencia de un tipo de documento
	 * 
	 * @param documentos
	 * @param d
	 * @return
	 */
	private Boolean verificarExistenciaTipoDoc(List<Documentos> documentos, EvalDocumentalDet d) {
		for (Documentos doc : documentos) {
			if (d.getMatrizDocConfigDet().getDatosEspecificos().getIdDatosEspecificos().equals(doc.getDatosEspecificos().getIdDatosEspecificos()))
				return true;
		}
		return false;
	}

	public String save() {
		if (observacion == null || observacion.trim().isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los campos obligatorios");
			return null;
		}

		try {
			buscarDocsAdjudicados();
			insertarEvaluacionDocumental();
			actualizarDocumentalDetalle();
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				eval.setObsElegible(observacion);
				eval.setFechaMod(new Date());
				eval.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(eval);
			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getUrlToPageEvaluarDocAdjudicado();
	}

	/**
	 * Métodos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPageEvaluarDocAdjudicado() {
		return "/seleccion/evaluacionDocumentalAdjudicados/EvaluarDocAdjudicado.xhtml?fromToPage=seleccion/evaluacionDocumentalAdjudicados/ListaElegibles&concursoPuestoAgrIdConcursoPuestoAgr="
			+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	/**
	 * Busca el nivel correspondiente a la entidad
	 * 
	 * @param nenCodigo
	 * @return
	 */
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Integer getCantPuestosCubrir() {
		return cantPuestosCubrir;
	}

	public void setCantPuestosCubrir(Integer cantPuestosCubrir) {
		this.cantPuestosCubrir = cantPuestosCubrir;
	}

	public Integer getCantVacancia() {
		return cantVacancia;
	}

	public void setCantVacancia(Integer cantVacancia) {
		this.cantVacancia = cantVacancia;
	}

	public Integer getCantAprobados() {
		return cantAprobados;
	}

	public void setCantAprobados(Integer cantAprobados) {
		this.cantAprobados = cantAprobados;
	}

	public List<EvalReferencialPostulante> getListaReferencialPostulantes() {
		return listaReferencialPostulantes;
	}

	public void setListaReferencialPostulantes(List<EvalReferencialPostulante> listaReferencialPostulantes) {
		this.listaReferencialPostulantes = listaReferencialPostulantes;
	}

	public List<MatrizDocConfigDet> getListaDocAdjudicados() {
		return listaDocAdjudicados;
	}

	public void setListaDocAdjudicados(List<MatrizDocConfigDet> listaDocAdjudicados) {
		this.listaDocAdjudicados = listaDocAdjudicados;
	}

	public List<EvalDocumentalDet> getListaEvalDocDet() {
		return listaEvalDocDet;
	}

	public void setListaEvalDocDet(List<EvalDocumentalDet> listaEvalDocDet) {
		this.listaEvalDocDet = listaEvalDocDet;
	}

}
