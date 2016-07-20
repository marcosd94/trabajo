package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.jboss.ejb3.metamodel.CurrentMessage;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.capacitacion.session.CapacitacionesList;
import py.com.excelsis.sicca.entity.CapacitacionFinalizada;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionTipo;
import py.com.excelsis.sicca.entity.ListaCab;
import py.com.excelsis.sicca.entity.PlantillaConfigurada;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gestionCapacitacionesFC")
public class GestionCapacitacionesFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	CapacitacionesList capacitacionesList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false)
	Usuario usuarioLogueado;

	private Capacitaciones capacitaciones = new Capacitaciones();
	private List<SelectItem> tipoSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> estadoSelecItem = new ArrayList<SelectItem>();
	private List<Capacitaciones> listaCapacitaciones = new ArrayList<Capacitaciones>();
	private Long idTipoCapacitacion;
	private Integer valorEstado;
	private Boolean esAdministradorSFP = false;
	private Boolean mostrarLinkGestPart = false;
	private Boolean mostrarLinkConfPlantilla = false;
	private Boolean mostrarLinkEvalCapac = false;
	private Boolean mostrarLinkVerEvalCapac = false;
	private Boolean mostrarLinkVerPart = false;
	private Boolean mostrarLinkVerEvalPart = false;
	private Boolean mostrarLinkEvalPart = false;
	private Boolean mostrarLinkConf2 = false;
	private Boolean mostrarLinkVerEval2 = false;
	private String mensaje;

	public void init() throws Exception {
		
		
		cargarDatosNivel();
		buscarTipoCapacitacion();
		buscarEstadosCapacitacion();
		cargarAdministradorSFP();
		search();
		if(mensaje != null && !mensaje.trim().isEmpty()){
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			mensaje = null;
		}
		
		

	}

	/**
	 * Carga datos de la cabecera
	 */
	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
			Long id = usuarioLogueado.getConfiguracionUoDet()
					.getIdConfiguracionUoDet();
			uoDet = em.find(ConfiguracionUoDet.class, id);
			if (uoDet != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(id);

		}
		cargarCabecera();
	}

	private void cargarCabecera() {
		Long idOee = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		ConfiguracionUoCab uoCab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEntidad = uoCab.getEntidad().getIdEntidad();
		Entidad entidad = new Entidad();
		entidad = em.find(Entidad.class, idEntidad);
		Long idSinEn = entidad.getSinEntidad().getIdSinEntidad();
		SinEntidad sinEnt = new SinEntidad();
		sinEnt = em.find(SinEntidad.class, idSinEn);
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(sinEnt.getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEnt.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		nivelEntidadOeeUtil.init();

	}

	/**
	 * Verifica si un usuario puede ver todas las OEE o no
	 * 
	 * @return
	 */
	private void cargarAdministradorSFP() {
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador()) {
				esAdministradorSFP = true;
				return;
			}
		}

		esAdministradorSFP = false;
	}

	private void buscarTipoCapacitacion() {
		String sql = "SELECT DE.* " + "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'TIPOS DE CAPACITACIONES' "
				+ "AND DE.ACTIVO = TRUE ORDER BY DE.DESCRIPCION ";
		List<DatosEspecificos> datos = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		if (tipoSelecItem == null || datos.isEmpty())
			tipoSelecItem = new ArrayList<SelectItem>();
		else
			tipoSelecItem.clear();

		tipoSelecItem.add(new SelectItem(null, "TODOS"));
		if (!datos.isEmpty()) {
			for (DatosEspecificos d : datos)
				tipoSelecItem.add(new SelectItem(d.getIdDatosEspecificos(), d
						.getDescripcion()));
		}

	}

	private void buscarEstadosCapacitacion() {
		String sql = "SELECT REF.* "
				+ "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE DOMINIO = 'ESTADOS_CAPACITACION' AND ACTIVO = TRUE "
				+ "AND (DESC_ABREV = 'FINALIZADO CIRCUITO' OR DESC_ABREV = 'EVALUACION PARTICIPANTES' "
				+ "OR DESC_ABREV = 'EVALUACION CAPACITACION' OR DESC_ABREV = 'FINALIZADA' OR DESC_ABREV = 'CANCELADA') "
				+ "ORDER BY DESC_ABREV";
		List<Referencias> referencias = em.createNativeQuery(sql,
				Referencias.class).getResultList();
		if (estadoSelecItem == null || referencias.isEmpty())
			estadoSelecItem = new ArrayList<SelectItem>();
		else
			estadoSelecItem.clear();

		estadoSelecItem.add(new SelectItem(null, "TODOS"));
		if (!referencias.isEmpty()) {
			for (Referencias r : referencias)
				estadoSelecItem.add(new SelectItem(r.getValorNum(), r
						.getDescAbrev()));
		}

	}

	public String obtenerEstadoActual(Long id) {
		Capacitaciones cap = new Capacitaciones();
		cap = em.find(Capacitaciones.class, id);
		habilitaLink(id);
		String val = obtenerEstadoCapacitacion(cap.getEstado());
		return val;
	}

	private String obtenerEstadoCapacitacion(Integer val) {
		String sql = "SELECT REF.DESC_ABREV "
				+ "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE DOMINIO = 'ESTADOS_CAPACITACION' AND ACTIVO = TRUE AND VALOR_NUM = "
				+ val;
		Object resultado = (Object) em.createNativeQuery(sql).getSingleResult();
		return resultado.toString();
	}

	public void searchAll() throws Exception {
		idTipoCapacitacion = null;
		valorEstado = null;
		capacitaciones = new Capacitaciones();
		seguridadUtilFormController.init();
		search();
	}

	public void search() throws Exception {
		String query = getQuery();

		capacitacionesList.listaResultados534(query);

	}

	public String getQuery() throws Exception {

		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);

		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(),
				TiposDatos.LONG.getValor(), null))
			return null;
		String query = "select distinct capacitaciones "
				+ " from Capacitaciones capacitaciones "
				+ " join capacitaciones.configuracionUoCab configuracionUoCab "
				+ " join capacitaciones.configuracionUoDet configuracionUoDet "
				+ " join capacitaciones.datosEspecificosTipoCap tipo ";
		// WHERE
		query += " where configuracionUoCab.idConfiguracionUo = "
				+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
			if (!sufc.validarInput(nivelEntidadOeeUtil
					.getIdUnidadOrganizativa().toString(), TiposDatos.LONG
					.getValor(), null))
				return null;
			query += " and configuracionUoDet.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		}
		query += " and capacitaciones.tipo = 'CAP_SFP'";
		List<Referencias> lista = obtenerEstadosCapacitacion();
		if (!lista.isEmpty()) {
			query += " and (";
			for (int i = 0; i < lista.size(); i++) {
				Referencias r = new Referencias();
				r = lista.get(i);
				query += " capacitaciones.estado = " + r.getValorNum();
				Integer cont = i + 1;
				if (cont < lista.size())
					query += " or";
			}
			query += ")";

		}
		if (idTipoCapacitacion != null) {
			if (!sufc.validarInput(idTipoCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return null;
			query += " and tipo.idDatosEspecificos = " + idTipoCapacitacion;
		}
		if (valorEstado != null) {
			if (!sufc.validarInput(valorEstado.toString(),
					TiposDatos.INTEGER.getValor(), null))
				return null;
			query += " and capacitaciones.estado = " + valorEstado;
		}
		if (capacitaciones != null && capacitaciones.getNombre() != null
				&& !capacitaciones.getNombre().trim().isEmpty()) {
			if (!sufc.validarInput(capacitaciones.getNombre(),
					TiposDatos.STRING.getValor(), null))
				return null;

			query += " and lower(capacitaciones.nombre) like lower(concat('%', concat('"
					+ seguridadUtilFormController
							.caracterInvalido(capacitaciones.getNombre()
									.toLowerCase()) + "','%')))";
		}
		return query;
	}

	private List<Referencias> obtenerEstadosCapacitacion() {
		String sql = "SELECT REF.* "
				+ "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE DOMINIO = 'ESTADOS_CAPACITACION' AND ACTIVO = TRUE "
				+ "AND DESC_ABREV IN ('FINALIZADO CIRCUITO', 'EVALUACION PARTICIPANTES', 'EVALUACION CAPACITACION', 'CANCELADA', 'FINALIZADA')";
		return em.createNativeQuery(sql, Referencias.class).getResultList();
	}

	private void habilitaFinalizadoCircuito(Long carga) {
		String sql = "SELECT REF.VALOR_NUM "
				+ "FROM SELECCION.REFERENCIAS REF "
				+ "WHERE DOMINIO = 'GESTION_CAPACITACION' AND ACTIVO = TRUE AND DESC_ABREV = 'EVAL_PART_SFP'";
		Object resultado = (Object) em.createNativeQuery(sql).getSingleResult();
		if (resultado != null) {
			Integer val = new Integer(resultado.toString());

			if (carga.intValue() >= val.intValue())
				mostrarLinkEvalPart = true;

			else {
				mostrarLinkConfPlantilla = true;
				mostrarLinkEvalCapac = true;
			}
		} else
			mostrarLinkEvalPart = true;

	}

	private void habilitarEvaluacion(Long id) {
		String sql = "SELECT EVAL.* "
				+ "FROM CAPACITACION.EVALUACION_TIPO EVAL "
				+ "WHERE EVAL.TIPO = 'EVAL_PART'  "
				+ "AND EVAL.ID_CAPACITACION = " + id;
		List<EvaluacionTipo> lista = em.createNativeQuery(sql,
				EvaluacionTipo.class).getResultList();
		if (!lista.isEmpty())
			mostrarLinkVerEvalPart = true;
	}
	
	private void habilitarConfPlantilla2(Long id){
		String sql = "select p.* " +
				"from capacitacion.plantilla_configurada p " +
				"where p.valor = 2 " +
				"and p.id_capacitacion = "+id;
		List<PlantillaConfigurada> lista = em.createNativeQuery(sql,
				PlantillaConfigurada.class).getResultList();
		if (lista.isEmpty())
			mostrarLinkConf2 = true;
		else{
			for (PlantillaConfigurada d : lista){
				if(d.getEstado() != null){
					mostrarLinkConf2 = false;
					return;
				}
					
			}
			mostrarLinkConf2 = true;
		}
	}
	
	private void habilitarVer2(Long id) {
		String sql = "select p.* " +
				"from capacitacion.plantilla_configurada p " +
				"where p.valor = 2 and p.estado = 'F' " +
				"and p.id_capacitacion = " + id;
		List<PlantillaConfigurada> lista = em.createNativeQuery(sql,
				PlantillaConfigurada.class).getResultList();
		if (!lista.isEmpty())
			mostrarLinkVerEval2 = true;
		else
			mostrarLinkVerEval2 = false;
	}

	private Boolean cuentaConListaCab(Long id) {
		String sql = "SELECT L.* FROM CAPACITACION.LISTA_CAB L "
				+ "WHERE L.ID_CAPACITACION = "
				+ id;
		List<ListaCab> listaCab = em.createNativeQuery(sql, ListaCab.class)
				.getResultList();
		if (listaCab.isEmpty())
			return false;
		return true;
	}

	public void habilitaLink(Long id) {
		Capacitaciones cap = new Capacitaciones();
		cap = em.find(Capacitaciones.class, id);

		String estadoActual = obtenerEstadoCapacitacion(cap.getEstado());
		if (estadoActual.equals("FINALIZADO CIRCUITO")) {
			mostrarLinkGestPart = true;
			mostrarLinkConfPlantilla = false;
			mostrarLinkEvalCapac = false;
			mostrarLinkVerEvalCapac = false;
			mostrarLinkVerPart = false;
			mostrarLinkVerEvalPart = false;
			mostrarLinkEvalPart = false;
			mostrarLinkConf2 = false;
			mostrarLinkVerEval2 = false;
			if (cuentaConListaCab(id))
				habilitaFinalizadoCircuito(cap.getCargaHoraria());
		}
		if (estadoActual.equals("EVALUACION PARTICIPANTES")) {
			mostrarLinkVerPart = true;
			mostrarLinkVerEvalPart = true;
			mostrarLinkConfPlantilla = true;
			mostrarLinkEvalCapac = true;
			mostrarLinkVerEvalCapac = false;
			mostrarLinkEvalPart = false;
			mostrarLinkGestPart = false;
			mostrarLinkConf2 = false;
			mostrarLinkVerEval2 = false;
		}
		if (estadoActual.equals("EVALUACION CAPACITACION")) {
			mostrarLinkVerPart = true;
			mostrarLinkEvalCapac = true;
			mostrarLinkVerEvalCapac = false;
			mostrarLinkConfPlantilla = false;
			mostrarLinkVerEvalPart = false;
			mostrarLinkEvalPart = false;
			mostrarLinkGestPart = false;
			mostrarLinkConf2 = false;
			mostrarLinkVerEval2 = false;
			habilitarEvaluacion(cap.getIdCapacitacion());
		}
		if (estadoActual.equals("FINALIZADA")) {
			mostrarLinkVerPart = true;
			mostrarLinkVerEvalCapac = true;
			mostrarLinkConfPlantilla = false;
			mostrarLinkEvalCapac = false;
			mostrarLinkEvalPart = false;
			mostrarLinkGestPart = false;
			mostrarLinkVerEvalPart = false;
			habilitarEvaluacion(cap.getIdCapacitacion());
			habilitarConfPlantilla2(cap.getIdCapacitacion());
			habilitarVer2(cap.getIdCapacitacion());
		}
		if (estadoActual.equals("CANCELADA")) {
			mostrarLinkConfPlantilla = false;
			mostrarLinkEvalCapac = false;
			mostrarLinkEvalPart = false;
			mostrarLinkGestPart = false;
			mostrarLinkVerEvalCapac = false;
			mostrarLinkVerEvalPart = false;
			mostrarLinkVerPart = false;
			mostrarLinkConf2 = false;
			mostrarLinkVerEval2 = false;
		}
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public List<SelectItem> getTipoSelecItem() {
		return tipoSelecItem;
	}

	public void setTipoSelecItem(List<SelectItem> tipoSelecItem) {
		this.tipoSelecItem = tipoSelecItem;
	}

	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}

	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}

	public List<SelectItem> getEstadoSelecItem() {
		return estadoSelecItem;
	}

	public void setEstadoSelecItem(List<SelectItem> estadoSelecItem) {
		this.estadoSelecItem = estadoSelecItem;
	}

	public Integer getValorEstado() {
		return valorEstado;
	}

	public void setValorEstado(Integer valorEstado) {
		this.valorEstado = valorEstado;
	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public List<Capacitaciones> getListaCapacitaciones() {
		return listaCapacitaciones;
	}

	public void setListaCapacitaciones(List<Capacitaciones> listaCapacitaciones) {
		this.listaCapacitaciones = listaCapacitaciones;
	}

	public Boolean getMostrarLinkGestPart() {
		return mostrarLinkGestPart;
	}

	public void setMostrarLinkGestPart(Boolean mostrarLinkGestPart) {
		this.mostrarLinkGestPart = mostrarLinkGestPart;
	}

	public Boolean getMostrarLinkConfPlantilla() {
		return mostrarLinkConfPlantilla;
	}

	public void setMostrarLinkConfPlantilla(Boolean mostrarLinkConfPlantilla) {
		this.mostrarLinkConfPlantilla = mostrarLinkConfPlantilla;
	}

	public Boolean getMostrarLinkEvalCapac() {
		return mostrarLinkEvalCapac;
	}

	public void setMostrarLinkEvalCapac(Boolean mostrarLinkEvalCapac) {
		this.mostrarLinkEvalCapac = mostrarLinkEvalCapac;
	}

	public Boolean getMostrarLinkVerEvalCapac() {
		return mostrarLinkVerEvalCapac;
	}

	public void setMostrarLinkVerEvalCapac(Boolean mostrarLinkVerEvalCapac) {
		this.mostrarLinkVerEvalCapac = mostrarLinkVerEvalCapac;
	}

	public Boolean getMostrarLinkVerPart() {
		return mostrarLinkVerPart;
	}

	public void setMostrarLinkVerPart(Boolean mostrarLinkVerPart) {
		this.mostrarLinkVerPart = mostrarLinkVerPart;
	}

	public Boolean getMostrarLinkVerEvalPart() {
		return mostrarLinkVerEvalPart;
	}

	public void setMostrarLinkVerEvalPart(Boolean mostrarLinkVerEvalPart) {
		this.mostrarLinkVerEvalPart = mostrarLinkVerEvalPart;
	}

	public Boolean getMostrarLinkEvalPart() {
		return mostrarLinkEvalPart;
	}

	public void setMostrarLinkEvalPart(Boolean mostrarLinkEvalPart) {
		this.mostrarLinkEvalPart = mostrarLinkEvalPart;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Boolean getMostrarLinkConf2() {
		return mostrarLinkConf2;
	}

	public void setMostrarLinkConf2(Boolean mostrarLinkConf2) {
		this.mostrarLinkConf2 = mostrarLinkConf2;
	}

	public Boolean getMostrarLinkVerEval2() {
		return mostrarLinkVerEval2;
	}

	public void setMostrarLinkVerEval2(Boolean mostrarLinkVerEval2) {
		this.mostrarLinkVerEval2 = mostrarLinkVerEval2;
	}
	
	
}
