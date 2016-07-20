package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoriaContratados;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("asignarCategoriaReasignacion")
public class AsignarCategoriaReasignacion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -857004958228062625L;
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
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private ConcursoPuestoAgr concursoPuestoAgr = new ConcursoPuestoAgr();

	List<SinAnx> listaCategorias = new ArrayList<SinAnx>();
	List<SinCategoriaContratados> listaCategoriasContratados = new ArrayList<SinCategoriaContratados>();

	private Long idAgr;
	private Integer objCodigo;
	private Integer cantPuestosActivos;
	private BigDecimal monto;
	private String codigoPuesto;
	private String codigoSinarh;
	private String categoriaSearch;
	private String codigoSearch;
	private String from;
	private String cadenaRecibida;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {

		if (idAgr != null)
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idAgr);

		cargarDatosNivel();
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			codigoSinarh = em.find(ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()).getCodigoSinarh();
		cantidadPuestosActivos();
		buscarCategorias();
	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (usuarioLogueado.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {
		Long idOee = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		Long idEntidad = em.find(ConfiguracionUoCab.class, idOee).getEntidad()
				.getIdEntidad();
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(em
				.find(Entidad.class, idEntidad).getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad

		nivelEntidadOeeUtil.setIdSinEntidad(em.find(Entidad.class, idEntidad)
				.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		nivelEntidadOeeUtil.init();

	}

	@SuppressWarnings("unchecked")
	private void buscarCategorias() {
		Integer vrs_codigo;
		List<SinAnx> listaAx = new ArrayList<SinAnx>();
		if (cadenaRecibida.equalsIgnoreCase("PERMANENTE"))
			vrs_codigo = new Integer(50);
		else
			vrs_codigo = null;

			listaCategorias = new ArrayList<SinAnx>();
			listaCategoriasContratados = new ArrayList<SinCategoriaContratados>();
			if (cadenaRecibida.equalsIgnoreCase("CONTRATADO"))
				listaCategoriasContratados = em.createNativeQuery(queryContratado(), SinCategoriaContratados.class)
						.getResultList();
			else {
				listaAx = sinarhUtiles.obtenerListaSinAnx(null, vrs_codigo, objCodigo, codigoSearch, categoriaSearch);
				for (SinAnx ax : listaAx) {
					if (ax.getCantDisponible().intValue() >= cantPuestosActivos)
						listaCategorias.add(ax);
				}
			}
		
	}

	private String queryContratado(){
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String q = "select c.* " +
				"from sinarh.sin_categoria_contratados c " +
				"where c.obj_codigo = "+objCodigo
				+" and c.ani_aniopre = "+anho;
		return q;
	}
	public void search() {
		buscarCategorias();
	}

	public void searchAll() {
		codigoSearch = null;
		categoriaSearch = null;
		cantidadPuestosActivos();
		buscarCategorias();
	}

	private void cantidadPuestosActivos() {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", idAgr);
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (lista.isEmpty())
			cantPuestosActivos = 0;
		else
			cantPuestosActivos = lista.size();

	}

	public Long getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(Long idAgr) {
		this.idAgr = idAgr;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getCodigoPuesto() {
		return codigoPuesto;
	}

	public void setCodigoPuesto(String codigoPuesto) {
		this.codigoPuesto = codigoPuesto;
	}

	public String getCodigoSinarh() {
		return codigoSinarh;
	}

	public void setCodigoSinarh(String codigoSinarh) {
		this.codigoSinarh = codigoSinarh;
	}

	public String getCategoriaSearch() {
		return categoriaSearch;
	}

	public void setCategoriaSearch(String categoriaSearch) {
		this.categoriaSearch = categoriaSearch;
	}

	public String getCodigoSearch() {
		return codigoSearch;
	}

	public void setCodigoSearch(String codigoSearch) {
		this.codigoSearch = codigoSearch;
	}

	public String getCadenaRecibida() {
		return cadenaRecibida;
	}

	public void setCadenaRecibida(String cadenaRecibida) {
		this.cadenaRecibida = cadenaRecibida;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Integer getObjCodigo() {
		return objCodigo;
	}

	public void setObjCodigo(Integer objCodigo) {
		this.objCodigo = objCodigo;
	}

	public List<SinAnx> getListaCategorias() {
		return listaCategorias;
	}

	public void setListaCategorias(List<SinAnx> listaCategorias) {
		this.listaCategorias = listaCategorias;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Integer getCantPuestosActivos() {
		return cantPuestosActivos;
	}

	public void setCantPuestosActivos(Integer cantPuestosActivos) {
		this.cantPuestosActivos = cantPuestosActivos;
	}

	public List<SinCategoriaContratados> getListaCategoriasContratados() {
		return listaCategoriasContratados;
	}

	public void setListaCategoriasContratados(
			List<SinCategoriaContratados> listaCategoriasContratados) {
		this.listaCategoriasContratados = listaCategoriasContratados;
	}
	
	

}
