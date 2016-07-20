package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
 
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
 

import enums.ActividadEnum;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HistoricosEstado;
 
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PublicacionConcurso;
import py.com.excelsis.sicca.entity.PublicacionConcursoRevDet;
import py.com.excelsis.sicca.entity.Referencias;
 
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Scope(ScopeType.CONVERSATION)
@Name("verificarPublicacionFormController")
public class VerificarPublicacionFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1661926467489160485L;
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
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	
	@In(create=true)
	ReponerCategoriasController reponerCategoriasController;

	ReportUtilFormController reportUtilFormController;

	private Long idConcursoPuestoAgr;
	private Long idPublicacionConcRevDet;
	private PublicacionConcursoRevDet publicacionConcursoRevDet;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	public Observacion observacion;
	private boolean ajusteEnviado = false;
	private String observacionString;

	private String entity = "Concurso";
	private String idEntity = "";
	private String nombrePantalla = "verificarPublicacion";
	private String ubicacionFisica = "";

	private int selectedRow = 0;

	private List<ConcursoPuestoAgr> listaGruposPorAjustar;
	private List<PublicacionConcursoRevDet> listaPublicacionConcursoRevDet;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
//		seguridadUtilFormController.verificarPerteneceOee(null, grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "AJUSTE PUBLICACION")
//			+ "", ActividadEnum.REVISION_PUBLICACION_OEE.getValor());
	}

	public void init() {
		cargar();
	}

	public void limpiar() {
		observacion = null;
		ajusteEnviado = false;
		observacionString = null;
	}

	public void cargar() {
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController.getConcursoPuestoAgr();
		validarOee();
		cargarGrupos(concursoPuestoAgr.getConcurso());
		cargarDetalles();

		long idTaskInstance =
			jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance());
		observacion = cargarObservacion(idTaskInstance);

		if (observacion == null) {
			observacion = new Observacion();
			ajusteEnviado = false;
		} else {
			ajusteEnviado = true;
		}

		Calendar c = Calendar.getInstance();
		ubicacionFisica =
			"\\SICCA\\"
				+ c.get(Calendar.YEAR)
				+ "\\OEE\\"
				+ concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
				+ "\\"
				+ concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ "\\" + concursoPuestoAgr.getConcurso().getIdConcurso();
	}

	public Observacion cargarObservacion(long idTaskInstance) {
		try {
			String cad =
				" select o.* " + " from seleccion.observacion o " + " where id_task_instance = "
					+ idTaskInstance + " ";

			List<Observacion> lista = em.createNativeQuery(cad, Observacion.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public String enviarAjustes() {

		if (listaPublicacionConcursoRevDet == null || listaPublicacionConcursoRevDet.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "No se encontraron ajustes que enviar.");
			return null;
		}

		PublicacionConcursoRevDet p = listaPublicacionConcursoRevDet.get(0);

		if (!funcionarioUtilFormController.vacio(p.getRespuesta()) && !p.getAjustadoOee()) {
			// Nuevo
			try {

				Date fecha = new Date();
				for (ConcursoPuestoAgr concursoPuestoAgr : listaGruposPorAjustar) {
					// Se actualiza el estado
					Referencias referencias =
						referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "A PUBLICAR");
					concursoPuestoAgr.setEstado(referencias.getValorNum());
					em.merge(concursoPuestoAgr);

					EstadoDet estadoDet =
						seleccionUtilFormController.buscarEstadoDet("CONCURSO", "A PUBLICAR");
					if (concursoPuestoAgr.getConcursoPuestoDets() != null) {
						for (ConcursoPuestoDet concursoPuestoDet : concursoPuestoAgr.getConcursoPuestoDets()) {
							concursoPuestoDet.setEstadoDet(estadoDet);
							em.merge(concursoPuestoDet);

							PlantaCargoDet plantaCargoDet = concursoPuestoDet.getPlantaCargoDet();
							plantaCargoDet.setEstadoDet(estadoDet);
							em.merge(plantaCargoDet);
						}
					}

					// Guardar Observacion
					long idTaskInstance =
						jbpmUtilFormController.getIdTaskInstanceActual(concursoPuestoAgr.getIdProcessInstance());
					observacion = new Observacion();
					observacion.setIdTaskInstance(idTaskInstance);
					observacion.setConcurso(concursoPuestoAgr.getConcurso());
					observacion.setFechaAlta(fecha);
					observacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					observacion.setObservacion(observacionString);
					em.persist(observacion);
					
					/**
					 * MODIFICACION DE A INCIDENCIA 0001397
					 * */
					// Se pasa los grupos a la siguiente tarea
					
					jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
					//jbpmUtilFormController.setActividadActual(ActividadEnum.REVISION_PUBLICACION_OEE);
					//jbpmUtilFormController.setActividadSiguiente(ActividadEnum.APROBAR_PUBLICACION);//MODIFICADO RV
					jbpmUtilFormController.setActividadPorGrupo(true);
					if (!jbpmUtilFormController.nextTask()) 
						return null;
					
					/**
					 * FIN
					 * */
					
				}

				// GUARDAR PUBLICACION_CONC_REV_DET
				p.setAjustadoOee(true);
				p.setUsuMod(usuarioLogueado.getCodigoUsuario());
				p.setFechaMod(fecha);
				em.persist(p);

				// GUARDAR PUBLICACION CONCONCURSO
				PublicacionConcurso publicacionConcurso =
					em.find(PublicacionConcurso.class, p.getPublicacionConcurso().getIdPublicacionConcurso());
				publicacionConcurso.setUsuUltRev(usuarioLogueado.getCodigoUsuario());
				publicacionConcurso.setFechaUltRev(fecha);
				em.persist(publicacionConcurso);

				em.flush();
				
			} catch (Exception e) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Se produjo un error al enviar el ajuste. Consulte con el administrador del sistema para mayor información.");
				return null;
			}
			ajusteEnviado = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Ajuste Enviado correctamente");
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe registrar una respuesta antes de ser enviada");
			return null;
		}

		return "nextTask";
	}

	@SuppressWarnings("unchecked")
	private List<PublicacionConcursoRevDet> cargarlistaPublicacionConcursoRevDet() {
		try {
			String cad =
				" " + " SELECT pcrd.* " +

				" FROM seleccion.publicacion_concurso_rev_det pcrd " +

				" JOIN seleccion.publicacion_concurso pc "
					+ " ON(pcrd.id_publicacion_concurso = pc.id_publicacion_concurso) " +

					" WHERE " + " pcrd.activo is true " + " and pc.id_concurso = "
					+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getIdConcurso() +

					" ORDER BY id_publicacion_conc_rev_det desc";

			Query q = em.createNativeQuery(cad, PublicacionConcursoRevDet.class);
			List<PublicacionConcursoRevDet> lista = q.getResultList();
			return lista;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void editDetalle() {
		if (idPublicacionConcRevDet == null) {
			// nuevo
			publicacionConcursoRevDet = new PublicacionConcursoRevDet();
		} else {
			publicacionConcursoRevDet =
				em.find(PublicacionConcursoRevDet.class, idPublicacionConcRevDet);
		}
		idPublicacionConcRevDet = null;
	}

	public void guardarDetalle() {
		if (publicacionConcursoRevDet != null
			&& !funcionarioUtilFormController.vacio(publicacionConcursoRevDet.getRespuesta())) {
			Date fecha = new Date();

			if (publicacionConcursoRevDet.getUsuRpta() == null) {
				publicacionConcursoRevDet.setUsuRpta(usuarioLogueado.getCodigoUsuario());
				publicacionConcursoRevDet.setFechaRpta(fecha);
			} else {
				publicacionConcursoRevDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				publicacionConcursoRevDet.setFechaMod(fecha);
			}

			em.merge(publicacionConcursoRevDet);

			em.flush();
			cargarDetalles();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		}
	}

	/**
	 * Carga la lista de los grupos: listaGruposPorAjustar
	 */
	private void cargarGrupos(Concurso concurso) {
		listaGruposPorAjustar =
			seleccionUtilFormController.buscarGruposConcursoPorEstado(concurso.getIdConcurso(), "AJUSTE PUBLICACION");
	}

	/**
	 * Carga la lista de detalles: solicProrrogaDetList
	 */
	private void cargarDetalles() {
		listaPublicacionConcursoRevDet = cargarlistaPublicacionConcursoRevDet();
	}

	public void imprimirPerfilMatriz() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idConcursoPuestoAgr);
		reportUtilFormController.imprimirReportePdf();
	}

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	public String cancelarGrupos() {
		boolean seleccionado = false;
		if (listaGruposPorAjustar != null && listaGruposPorAjustar.size() > 0) {
			Date fecha = new Date();
			for (ConcursoPuestoAgr concursoPuestoAgr : listaGruposPorAjustar) {
				if (concursoPuestoAgr.getSeleccionado()) {
					seleccionado = true;
					concursoPuestoAgr.setActivo(false);
					Referencias referencias =
						referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "GRUPO CANCELADO");
					concursoPuestoAgr.setEstado(referencias.getValorNum());
					em.merge(concursoPuestoAgr);

					// Actualizar puestos
					for (ConcursoPuestoDet concursoPuestoDet : concursoPuestoAgr.getConcursoPuestoDets()) {
						concursoPuestoDet =
							em.find(ConcursoPuestoDet.class, concursoPuestoDet.getId());
						PlantaCargoDet plantaCargoDet =
							em.find(PlantaCargoDet.class, concursoPuestoDet.getPlantaCargoDet().getIdPlantaCargoDet());
						EstadoCab estadoCab =
							seleccionUtilFormController.buscarEstadoCab("VACANTE");
						plantaCargoDet.setEstadoCab(estadoCab);
						plantaCargoDet.setEstadoDet(null);// se agrego en la incidencia 0001038
						em.merge(plantaCargoDet);

						concursoPuestoDet.setActivo(false);
						EstadoDet estadoDet =
							seleccionUtilFormController.buscarEstadoDet("CONCURSO", "CANCELADO");//se modifico en la incidencia 0001647
						concursoPuestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						concursoPuestoDet.setEstadoDet(estadoDet);// se agrego en la incidencia 0001038
						concursoPuestoDet.setFechaMod(fecha);
						em.merge(concursoPuestoDet);

						HistoricosEstado historicosEstado = new HistoricosEstado();
						historicosEstado.setEstadoCab(estadoCab);
						historicosEstado.setPlantaCargoDet(plantaCargoDet);
						historicosEstado.setFechaMod(fecha);
						historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.persist(historicosEstado);
					}

					// Pasar grupo a la siguiente tarea
					jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
					//jbpmUtilFormController.setActividadActual(ActividadEnum.REVISION_PUBLICACION_OEE);
					jbpmUtilFormController.setTransition("end");
					jbpmUtilFormController.setActividadPorGrupo(true);
					if (jbpmUtilFormController.nextTask()) {
						em.flush();
						
						/**
						 * INCIDENCIA  0001634
						 * o	Llamar al CU604  
						 * */
						reponerCategoriasController.reponerCategorias(concursoPuestoAgr, "GRUPO", "CANCELADO");
						/**
						 * FIN
						 * */
					} else {
						return null;
					}
				}
			}
		}

		// Refrescar lista de grupos
		ConcursoPuestoAgr concursoPuestoAgr = grupoPuestosController.getConcursoPuestoAgr();
		cargarGrupos(concursoPuestoAgr.getConcurso());

		statusMessages.clear();
		if (seleccionado) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} else {
			statusMessages.add(Severity.ERROR, "Debe seleccionar previamente el/los grupo/s a Cancelar");
		}
		return null;
	}
	

	public Boolean hayGrupos() {
		if (listaGruposPorAjustar != null && listaGruposPorAjustar.size() > 0) {
			return true;
		}
		return false;
	}

	public Observacion getObservacion() {
		return observacion;
	}

	public void setObservacion(Observacion observacion) {
		this.observacion = observacion;
	}

	public boolean isAjusteEnviado() {
		return ajusteEnviado;
	}

	public void setAjusteEnviado(boolean ajusteEnviado) {
		this.ajusteEnviado = ajusteEnviado;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public void setListaGruposPorAjustar(List<ConcursoPuestoAgr> listaGruposPorAjustar) {
		this.listaGruposPorAjustar = listaGruposPorAjustar;
	}

	public List<ConcursoPuestoAgr> getListaGruposPorAjustar() {
		return listaGruposPorAjustar;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public void setListaPublicacionConcursoRevDet(List<PublicacionConcursoRevDet> listaPublicacionConcursoRevDet) {
		this.listaPublicacionConcursoRevDet = listaPublicacionConcursoRevDet;
	}

	public List<PublicacionConcursoRevDet> getListaPublicacionConcursoRevDet() {
		return listaPublicacionConcursoRevDet;
	}

	public void setIdPublicacionConcRevDet(Long idPublicacionConcRevDet) {
		this.idPublicacionConcRevDet = idPublicacionConcRevDet;
	}

	public Long getIdPublicacionConcRevDet() {
		return idPublicacionConcRevDet;
	}

	public void setPublicacionConcursoRevDet(PublicacionConcursoRevDet publicacionConcursoRevDet) {
		this.publicacionConcursoRevDet = publicacionConcursoRevDet;
	}

	public PublicacionConcursoRevDet getPublicacionConcursoRevDet() {
		return publicacionConcursoRevDet;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setObservacionString(String observacionString) {
		this.observacionString = observacionString;
	}

	public String getObservacionString() {
		return observacionString;
	}
}
