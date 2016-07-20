package py.com.excelsis.sicca.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;
import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("plantaCargoDetList")
public class PlantaCargoDetList extends CustomEntityQuery<PlantaCargoDet> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<PlantaCargoDet> listaPlantaCargoDets;

	private static final String EJBQL = "select plantaCargoDet from PlantaCargoDet plantaCargoDet";

	private static final String EJBQLCU0253 = "select distinct(plantaCargoDet) from PlantaCargoDet plantaCargoDet"
			+ " left join plantaCargoDet.detTipoNombramientos nom left join nom.tipoNombramiento tipo ";

	private static final String[] RESTRICTIONS = {
			"lower(plantaCargoDet.descripcion) like lower(concat(#{plantaCargoDetList.plantaCargoDet.descripcion},'%'))",
			"lower(plantaCargoDet.fundamentacionTecnica) like lower(concat(#{plantaCargoDetList.plantaCargoDet.fundamentacionTecnica},'%'))",
			"lower(plantaCargoDet.mision) like lower(concat(#{plantaCargoDetList.plantaCargoDet.mision},'%'))",
			"lower(plantaCargoDet.objetivo) like lower(concat(#{plantaCargoDetList.plantaCargoDet.objetivo},'%'))",
			"lower(plantaCargoDet.usuAlta) like lower(concat(#{plantaCargoDetList.plantaCargoDet.usuAlta},'%'))",
			"lower(plantaCargoDet.usuMod) like lower(concat(#{plantaCargoDetList.plantaCargoDet.usuMod},'%'))"
	};
	private static final String[] RESTRICTIONSCU253 = {
			"plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{plantaCargoDetList.sinEntidad.idSinEntidad}",
			"plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{plantaCargoDetList.sinEntidad.nenCodigo}",
			"plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{plantaCargoDetList.configuracionUoCab.idConfiguracionUo}",
			"plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=#{plantaCargoDetList.configuracionUoDet.idConfiguracionUoDet}",
			"plantaCargoDet.oficina.ciudad.departamento.pais.idPais=#{plantaCargoDetList.idPais}",
			"plantaCargoDet.cpt.tipoCpt.idTipoCpt=#{plantaCargoDetList.idTipoCpt}",
			"plantaCargoDet.oficina.ciudad.departamento.idDepartamento=#{plantaCargoDetList.idDpto}",
			"plantaCargoDet.oficina.ciudad.idCiudad=#{plantaCargoDetList.idCiudad}",
			"tipo.idTipoNombramiento=#{plantaCargoDetList.idTipoNombramiento}",
			"plantaCargoDet.oficina.barrio.idBarrio=#{plantaCargoDetList.idBarrio}",
			"plantaCargoDet.contratado=#{plantaCargoDetList.contratado}",
			"plantaCargoDet.permanente=#{plantaCargoDetList.permanente}",
			"plantaCargoDet.activo=#{plantaCargoDetList.estado}",
			"plantaCargoDet.estadoCab.idEstadoCab=#{plantaCargoDetList.idEstado}",

	};

	private static final String[] RESTRICTIONSCU120 = {
			"plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{plantaCargoDetList.sinEntidad.idSinEntidad}",
			"plantaCargoDet.configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{plantaCargoDetList.sinEntidad.nenCodigo}",
			"plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo=#{plantaCargoDetList.configuracionUoCab.idConfiguracionUo}",
			"plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=#{plantaCargoDetList.configuracionUoDet.idConfiguracionUoDet}",
			"plantaCargoDet.activo=#{plantaCargoDetList.estado}",
			"plantaCargoDet.estadoCab.idEstadoCab=#{plantaCargoDetList.idEstado}",
			"plantaCargoDet.cpt.tipoCpt.idTipoCpt=#{plantaCargoDetList.idTipoCpt}",
			"plantaCargoDet.cpt.idCpt=#{plantaCargoDetList.cpt.idCpt}",
			"lower(plantaCargoDet.descripcion) like lower(concat('%', concat(#{plantaCargoDetList.puesto},'%')))", };

	private static final String[] RESTRICTIONSCU434 = {
			"plantaCargoDet.configuracionUoDet.idConfiguracionUoDet=#{plantaCargoDetList.configuracionUoDet.idConfiguracionUoDet}",
			"lower(plantaCargoDet.descripcion) like lower(concat('%', concat(#{plantaCargoDetList.puesto},'%')))",
			"plantaCargoDet.activo=#{plantaCargoDetList.estado}", };

	private PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
	private static final String ORDER = "plantaCargoDet.descripcion";
	private Boolean estado;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConfiguracionUoDet configuracionUoDet;
	private Cpt cpt;
	private Long idPlantaCargoDet;
	private Long idPais;
	private Long idTipoCpt;
	private Long idDpto;
	private Long idCiudad;
	private Long idTipoNombramiento;
	private Long idBarrio;
	private Boolean contratado;
	private Boolean permanente;
	private Long idEstado;
	private String puesto;

	public PlantaCargoDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<PlantaCargoDet> searchInit() {
		estado = null;
		idPais = null;
		idTipoCpt = null;
		idDpto = null;
		idCiudad = null;
		idTipoCpt = null;
		idDpto = null;
		idCiudad = null;
		idTipoNombramiento = null;
		idBarrio = null;
		contratado = null;
		permanente = null;
		setEjbql(EJBQLCU0253);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU253));
		setOrder(ORDER);
		setMaxResults(null);
		List<PlantaCargoDet> lista = new ArrayList<PlantaCargoDet>();
		lista = getResultList();
		List<PlantaCargoDet> listaResult = new ArrayList<PlantaCargoDet>();
		for (PlantaCargoDet p : lista) {
			String cab;
			cab = p.getEstadoCab().getDescripcion();
			if (cab.equals("JUDICIALIZADO") || cab.equals("VACANTE")
					|| !p.getActivo())
				listaResult.add(p);

		}
		return listaResult;
	}

	public List<PlantaCargoDet> search() {
		setEjbql(EJBQLCU0253);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU253));
		setOrder(ORDER);
		setMaxResults(null);

		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<PlantaCargoDet> limpiarCU120() {
		plantaCargoDet = new PlantaCargoDet();
		estado = null;
		sinNivelEntidad = null;
		sinEntidad = null;
		configuracionUoCab = null;
		configuracionUoDet = null;
		puesto = null;
		idEstado = null;
		idTipoCpt = null;
		cpt = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU120));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantaCargoDet> listaResultadosCU120() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU120));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<PlantaCargoDet> listaResultadosVacancias(String ejbql,
			Boolean seleccionar) {
		setEjbql(ejbql);
		setMaxResults(null);
		setOrder(ORDER);
		List<PlantaCargoDet> lista = getResultList();

		for (PlantaCargoDet p : lista) {
			p.setSeleccionado(seleccionar);
		}

		return lista;
	}

	public List<PlantaCargoDet> listaResultadosPuestos(String ejbql) {

		setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		List<PlantaCargoDet> lista = getResultList();

		return lista;
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantaCargoDet> listaResultadosCU434() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSCU434));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<PlantaCargoDet> lista = getResultList();
		return lista;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public List<PlantaCargoDet> obtenerListaPlantaCargoDets() {
		listaPlantaCargoDets = getResultList();
		return listaPlantaCargoDets;
	}

	public List<PlantaCargoDet> getListaPlantaCargoDets() {
		return listaPlantaCargoDets;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
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

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdTipoNombramiento() {
		return idTipoNombramiento;
	}

	public void setIdTipoNombramiento(Long idTipoNombramiento) {
		this.idTipoNombramiento = idTipoNombramiento;
	}

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}

	public Boolean getContratado() {
		return contratado;
	}

	public void setContratado(Boolean contratado) {
		this.contratado = contratado;
	}

	public Boolean getPermanente() {
		return permanente;
	}

	public void setPermanente(Boolean permanente) {
		this.permanente = permanente;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public Cpt getCpt() {
		return cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

}
