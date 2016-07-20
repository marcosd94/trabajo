package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.movilidadLaboral.session.SolicitudTrasladoCabList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("visualizarSolicitudTrasladoCU745FC")
public class VisualizarSolicitudTrasladoCU745FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SolicitudTrasladoCabList solicitudTrasladoCabList;

	private List<SelectItem> tipoSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> estadoSelectItems = new ArrayList<SelectItem>();

	private String funcionario;
	private Long idTipo;
	private Long idEstado;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		setearDatos();
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		cargarTipoSelectItems();
		cargarEstadoSelectItems();
	}
	
	public void search(){
		if(funcionario != null && !funcionario.trim().isEmpty())
			solicitudTrasladoCabList.getPersona().setNombres(funcionario);
		if(idEstado != null)
			solicitudTrasladoCabList.setIdEstadoTraslado(idEstado);
		if(idTipo != null)
			solicitudTrasladoCabList.setIdTipoTraslado(idTipo);
		solicitudTrasladoCabList.listaResultados();
	}
	
	public void searchAll(){
		setearDatos();
		solicitudTrasladoCabList.limpiar();
	}

	private void cargarTipoSelectItems() {
		String sql = "SELECT de.* "
				+ "FROM seleccion.datos_generales dg "
				+ "INNER JOIN seleccion.datos_especificos de ON (dg.id_datos_generales = de.id_datos_generales) "
				+ "WHERE dg.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' "
				+ "AND  de.descripcion LIKE 'TRASLADO%' AND de.activo = true";
		List<DatosEspecificos> lista = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		tipoSelectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : lista)
			tipoSelectItems.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
	}
	
	private void cargarEstadoSelectItems() {
		String sql = "SELECT de.* " +
				"FROM seleccion.datos_generales dg " +
				"INNER JOIN seleccion.datos_especificos de ON (dg.id_datos_generales = de.id_datos_generales) " +
				"WHERE dg.descripcion = 'ESTADOS SOLICITUD MOVILIDAD'  AND de.activo = true";
		List<DatosEspecificos> lista = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		estadoSelectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : lista)
			estadoSelectItems.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
	}

	private void setearDatos() {
		idEstado = null;
		idTipo = null;
		funcionario = null;
	}

	private void cargarCabecera() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	

	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public List<SelectItem> getTipoSelectItems() {
		return tipoSelectItems;
	}

	public void setTipoSelectItems(List<SelectItem> tipoSelectItems) {
		this.tipoSelectItems = tipoSelectItems;
	}

	public List<SelectItem> getEstadoSelectItems() {
		return estadoSelectItems;
	}

	public void setEstadoSelectItems(List<SelectItem> estadoSelectItems) {
		this.estadoSelectItems = estadoSelectItems;
	}

}
