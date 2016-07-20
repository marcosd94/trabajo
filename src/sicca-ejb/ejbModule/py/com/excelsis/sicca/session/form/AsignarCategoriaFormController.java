package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.mvel2.util.ParseTools;

import py.com.excelsis.sicca.entity.CategoriaCpt;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PromocionSalarial;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoria;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.PlantaCargoDetHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("asignarCategoriaFormController")
public class AsignarCategoriaFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private Concurso concurso;
	private PlantaCargoDet plantaCargoDet;
	private PromocionSalarial promocionSalarial;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();

	List<SinAnx> listaCategorias = new ArrayList<SinAnx>();

	private Long idPlantaCargo;
	private Long idPromocionSalarial;
	private Long idAgr;
	private BigDecimal monto;
	private String codigoPuesto;
	private String categoriaSearch;
	private String codigoSearch;
	private String fromConcurso;
	private String nroActividad;
	// String que recibe una lista de ids para asignar varias categorias de una vez
	private String plantaCargoDetIdList;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {

		concurso = new Concurso();
		concurso = concursoHome.getInstance();
		plantaCargoDet = new PlantaCargoDet();
		if (idPlantaCargo != null){
			plantaCargoDet = em.find(PlantaCargoDet.class, idPlantaCargo);
		}
			
		
			
		buscarDatosAsociadosUsuario();
		configuracionUoDet = new ConfiguracionUoDet();
		configuracionUoDet = plantaCargoDet.getConfiguracionUoDet();
		if (configuracionUoDet != null)
			codigoPuesto = obtenerCodigo(configuracionUoDet);
		buscarCategorias();
	}
	
	
	public void initPromocionSalarial() {

		concurso = new Concurso();
		concurso = concursoHome.getInstance();
		promocionSalarial = new PromocionSalarial();
		if (idPromocionSalarial != null){
			promocionSalarial = em.find(PromocionSalarial.class, idPromocionSalarial);
		}
			
		
			
		buscarDatosAsociadosUsuario();
		configuracionUoCab = new ConfiguracionUoCab();
		configuracionUoCab = promocionSalarial.getConfiguracionUoCab();
//		if (configuracionUoCab != null)
//			codigoPuesto = obtenerCodigo(configuracionUoCab);
	buscarCategorias();
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			Entidad entidad = new Entidad();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	@SuppressWarnings("unchecked")
	private void buscarCategorias() {
		List<PuestoConceptoPago> listaPagos = buscarPuestoConceptoPago();
		List<SinAnx> listaAnx = new ArrayList<SinAnx>();
				
			listaAnx = sinarhUtiles.buscarListaSinAnx(null, new Integer(50), new Integer(111), codigoSearch, categoriaSearch);
			listaCategorias = new ArrayList<SinAnx>();
			//ZD 17/06/16 VALIDAR QUE TENGA ASIGNADO CODIGO SINARH
			if(listaAnx == null){
				statusMessages.clear();
				statusMessages.add(Severity.WARN, "Debe asignar algun Codigo SINARH para su OEE");
				return;
			}
			Referencias referencias = new Referencias();
			for (PuestoConceptoPago p : listaPagos) {
				SinAnx snx = estaEnConceptoPago(p, listaAnx);
				if (snx != null) {
					if (p.getEstado() != null) {
						referencias = referenciasUtilFormController
								.getReferenciaPorValorNum("ESTADOS_CATEGORIA",
										p.getEstado());
						if (referencias != null
								&& referencias.getIdReferencias() != null)
							snx.setEstado("CATEGORIA "
									+ referencias.getDescLarga());
					}
					if (!concurso.getAdReferendum()
							&& snx.getCantDisponible().intValue() > 0)
						listaCategorias.add(snx);
				}
			}
			if (concurso.getAdReferendum() != null && concurso.getAdReferendum())
				listaCategorias.addAll(listaAnx);
			else {
				for (SinAnx lnx : listaAnx) {
					if (lnx.getCantDisponible().intValue() > 0)
						listaCategorias.add(lnx);
				}
			}	
	
	}

	private SinAnx estaEnConceptoPago(PuestoConceptoPago p, List<SinAnx> lista) {

		for (SinAnx an : lista) {
			if (p.getCategoria().equalsIgnoreCase(an.getCtgCodigo())) {

				lista.remove(an);
				return an;
			}

		}
		return null;
	}

	private List<PuestoConceptoPago> buscarPuestoConceptoPago() {
		Query q = em.createQuery("select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true and pago.objCodigo = :cod "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  ");
		q.setParameter("cod", new Integer(111));
		q.setParameter("id", idPlantaCargo);
		return q.getResultList();
	}


	
	public void search() {
		buscarCategorias();
	}

	public void searchAll() {
		codigoSearch = null;
		categoriaSearch = null;
		buscarCategorias();
	}

	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}
	
	
	public String buscarMonto(SinAnx sinAnx) {
		BigDecimal monto = null;
		
		Integer cat_grupo = sinAnx.getCatGrupo();
		
		String valor = "" + cat_grupo;
			String sql = "select * from sinarh.sin_categoria cat "
					+ "where cat.ctg_codigo = '" + sinAnx.getCtgCodigo() + "'"
					+ " and cat_grupo = " + cat_grupo
					+ " and cat.vrs_codigo = '" + valor + "'";
			List<SinCategoria> listaMonto = new ArrayList<SinCategoria>();
			
			listaMonto = em.createNativeQuery(sql, SinCategoria.class).getResultList();
			
			if (listaMonto.size() > 0) {
				Date fechaActual = new Date();
				Integer mes = fechaActual.getMonth();
				if (mes == 0)
					monto = listaMonto.get(0).getCtgValor1();
				if (mes == 1)
					monto = listaMonto.get(0).getCtgValor2();
				if (mes == 2)
					monto = listaMonto.get(0).getCtgValor3();
				if (mes == 3)
					monto = listaMonto.get(0).getCtgValor4();
				if (mes == 4)
					monto = listaMonto.get(0).getCtgValor5();
				if (mes == 5)
					monto = listaMonto.get(0).getCtgValor6();
				if (mes == 6)
					monto = listaMonto.get(0).getCtgValor7();
				if (mes == 7)
					monto = listaMonto.get(0).getCtgValor8();
				if (mes == 8)
					monto = listaMonto.get(0).getCtgValor9();
				if (mes == 9)
					monto = listaMonto.get(0).getCtgValor10();
				if (mes == 10)
					monto = listaMonto.get(0).getCtgValor11();
				if (mes == 11)
					monto = listaMonto.get(0).getCtgValor12();
			}
		
		
		return ""+monto.intValue();
	}


	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
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

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public String getCodigoPuesto() {
		return codigoPuesto;
	}

	public void setCodigoPuesto(String codigoPuesto) {
		this.codigoPuesto = codigoPuesto;
	}

/*	public String getCodigoSinarh() {
		return codigoSinarh;
	}

	public void setCodigoSinarh(String codigoSinarh) {
		this.codigoSinarh = codigoSinarh;
	}
*/
	public List<SinAnx> getListaCategorias() {
		return listaCategorias;
	}

	public void setListaCategorias(List<SinAnx> listaCategorias) {
		this.listaCategorias = listaCategorias;
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

	public Long getIdPlantaCargo() {
		return idPlantaCargo;
	}

	public void setIdPlantaCargo(Long idPlantaCargo) {
		this.idPlantaCargo = idPlantaCargo;
	}

	public Long getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(Long idAgr) {
		this.idAgr = idAgr;
	}

	public String getFromConcurso() {
		return fromConcurso;
	}

	public void setFromConcurso(String fromConcurso) {
		this.fromConcurso = fromConcurso;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getNroActividad() {
		return nroActividad;
	}

	public void setNroActividad(String nroActividad) {
		this.nroActividad = nroActividad;
	}

	public String getPlantaCargoDetIdList() {
		return plantaCargoDetIdList;
	}

	public void setPlantaCargoDetIdList(String plantaCargoDetIdList) {
		this.plantaCargoDetIdList = plantaCargoDetIdList;
	}


	public Long getIdPromocionSalarial() {
		return idPromocionSalarial;
	}


	public void setIdPromocionSalarial(Long idPromocionSalarial) {
		this.idPromocionSalarial = idPromocionSalarial;
	}


	public PromocionSalarial getPromocionSalarial() {
		return promocionSalarial;
	}


	public void setPromocionSalarial(PromocionSalarial promocionSalarial) {
		this.promocionSalarial = promocionSalarial;
	}


	
}
