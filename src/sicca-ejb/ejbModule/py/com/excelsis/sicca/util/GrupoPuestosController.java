package py.com.excelsis.sicca.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.VwEntidadOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.MatrizDocConfigDetList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;

@Scope(ScopeType.CONVERSATION)
@Name("grupoPuestosController")
public class GrupoPuestosController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;

	private final String nivelEntidadQuery = "" + "SELECT " + " 	sne.nen_codigo as nivel_codigo, "
		+ "   sne.nen_nombre as nivel, " + "   se.ent_codigo as entidad_codigo, "
		+ "   se.ent_nombre as entidad " +

		"FROM planificacion.planta_cargo_det pcd " 
		
		+" join planificacion.configuracion_uo_det uo_det on (uo_det.id_configuracion_uo_det = pcd.id_configuracion_uo_det) "
		+ "join planificacion.configuracion_uo_cab uo_cab on uo_cab.id_configuracion_uo = uo_det.id_configuracion_uo "
		+ "join planificacion.entidad e  on (e.id_entidad = uo_cab.id_entidad)" +

		" join sinarh.sin_entidad se " + " on (e.id_sin_entidad = se.id_sin_entidad) " +

		" join sinarh.sin_nivel_entidad sne "
		+ " on (sne.ani_aniopre = se.ani_aniopre and sne.nen_codigo = se.nen_codigo) " +

		" where id_planta_cargo_det = :idPlantaCargoDet";

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	MatrizDocConfigDetList matrizDocConfigDetList;

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Long idConcursoPuestoAgr;// recibe del CU que le llama
	private Long idPuesto;// recibe del CU que le llama
	private PlantaCargoDet puesto;
	private Long idTipoDocumento;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;// enviado por el CU
	private Long idConcurso;// enviado por el CU
	private Concurso concurso;// enviado por el CU

	private List<ConcursoPuestoAgr> listaFiltradaConcursoPuestoAgr =
		new ArrayList<ConcursoPuestoAgr>();
	private Boolean seleccionarTodosConcursoPuestoAgr = false;

	private List<ConcursoPuestoDet> listaFiltradaConcursoPuestoDet =
		new ArrayList<ConcursoPuestoDet>();
	private Boolean seleccionarTodosConcursoPuestoDet = false;
	private Long idConfUoCab;

	public void initCabecera() {
		idTipoDocumento = null;

		if (idConcursoPuestoAgr != null)
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);

		if (idPuesto != null)
			puesto = em.find(PlantaCargoDet.class, idPuesto);
		if (idConcurso != null) {
			concurso = em.find(Concurso.class, idConcurso);
		}
		findEntidades();// Trae las entidades segun el grupo que se envio
	}

	public void initCabeceraPorConcurso() {
		findEntidadesPorConcurso();
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos) {
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}

	public String obtenerCodigoPuesto(PlantaCargoDet plantaCargoDet) {
		plantaCargoDet = em.find(PlantaCargoDet.class, plantaCargoDet.getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = plantaCargoDet.getConfiguracionUoDet();
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		if (plantaCargoDet.getPermanente()) {
			code = code + "P";
		} else if (plantaCargoDet.getContratado()) {
			code = code + "C";
		}
		code = code + plantaCargoDet.getOrden();

		BigDecimal codigoNivelEntidad = getCodigoNivelEntidad(plantaCargoDet);
		BigDecimal codigoEntidad = getCodigoEntidad(plantaCargoDet);
		code = codigoNivelEntidad + "." + codigoEntidad + "." + code;

		return code;

	}

	@SuppressWarnings("unchecked")
	public void findEntidades() {

		if (idConcursoPuestoAgr != null) {
			configuracionUoCab =
				em.find(ConfiguracionUoCab.class, concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo());
		} else if (idConfUoCab != null) {
			configuracionUoCab = new ConfiguracionUoCab();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfUoCab);
		} else if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
		}

		if (configuracionUoCab != null) {
			//sinEntidad = em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			obtenerSinEntidad();
			/*List<SinNivelEntidad> sin =	em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty()){
				sinNivelEntidad = em.find(SinNivelEntidad.class, sin.get(0).getIdSinNivelEntidad());
			}*/
		}
	}
	
	private void obtenerSinEntidad(){
		String sql = "Select v.* from planificacion.vw_entidad_oee v " +
				"join planificacion.configuracion_uo_cab oee on v.id_configuracion_uo = oee.id_configuracion_uo" +
				" where oee.id_configuracion_uo = "+ configuracionUoCab.getIdConfiguracionUo();
		List<VwEntidadOee> sin =	em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()){
			sinEntidad = em.find(SinEntidad.class, sin.get(0).getSinEntidad().getIdSinEntidad());
			sinNivelEntidad = em.find(SinNivelEntidad.class, sin.get(0).getIdNivelEntidad());
		}
	}

	@SuppressWarnings("unchecked")
	private void findEntidadesPorConcurso() {
		concurso = em.find(Concurso.class, idConcurso);
		configuracionUoCab =
			em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			/*sinEntidad =
				em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);
	*/
			obtenerSinEntidad();
		}
	}

	public BigDecimal getCodigoNivelEntidad(PlantaCargoDet plantaCargoDet) {
		try {
			Query q = em.createNativeQuery(nivelEntidadQuery);
			q.setParameter("idPlantaCargoDet", plantaCargoDet.getIdPlantaCargoDet());
			List lista = q.getResultList();
			if (lista != null && lista.size() > 0) {
				Object[] v = (Object[]) lista.get(0);
				return (BigDecimal) v[0];
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public BigDecimal getCodigoEntidad(PlantaCargoDet plantaCargoDet) {
		try {
			Query q = em.createNativeQuery(nivelEntidadQuery);
			q.setParameter("idPlantaCargoDet", plantaCargoDet.getIdPlantaCargoDet());
			List lista = q.getResultList();
			if (lista != null && lista.size() > 0) {
				Object[] v = (Object[]) lista.get(0);
				return (BigDecimal) v[2];
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getConcursoSelectItem() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));

		String consulta =
			"" + " select concurso from Concurso concurso "
				+ " join concurso.configuracionUoCab configuracionUoCab "
				+ " where configuracionUoCab.idConfiguracionUo = :idConfiguracionUo "
				+ "   and concurso.activo = :activo " + " order by concurso.nombre ";

		try {
			Query q = em.createQuery(consulta);
			q.setParameter("idConfiguracionUo", configuracionUoCab.getIdConfiguracionUo());
			q.setParameter("activo", true);

			List<Concurso> concursos = q.getResultList();
			if (concursos != null) {
				for (Concurso c : concursos) {
					lista.add(new SelectItem(c.getIdConcurso(), c.getNombre()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getConcursosNoFinalizadosSelectItem() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));

		String consulta =
			"" + " select concurso from Concurso concurso "
				+ " join concurso.configuracionUoCab configuracionUoCab "
				+ " where configuracionUoCab.idConfiguracionUo = :idConfiguracionUo "
				+ "   and concurso.activo = :activo " + "   and concurso.estado <> :estado "
				+ " order by concurso.nombre ";

		try {
			Referencias referencias =
				referenciasUtilFormController.getReferencia("ESTADOS_CONCURSO", "FINALIZADO");

			Query q = em.createQuery(consulta);
			q.setParameter("idConfiguracionUo", configuracionUoCab.getIdConfiguracionUo());
			q.setParameter("activo", true);
			q.setParameter("estado", referencias.getValorNum());

			List<Concurso> concursos = q.getResultList();
			if (concursos != null) {
				for (Concurso c : concursos) {
					lista.add(new SelectItem(c.getIdConcurso(), c.getNombre()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	public void cargarListaFiltradaConcursoPuestoAgr() {

		seleccionarTodosConcursoPuestoAgr = false;
		cargarSeleccionarTodosConcursoPuestoAgr();

		listaFiltradaConcursoPuestoAgr = new ArrayList<ConcursoPuestoAgr>();
		if (idConcurso != null) {
			String consulta =
				"" + " select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
					+ " join concursoPuestoAgr.concurso concurso "
					+ " where concurso.idConcurso = :idConcurso "
					+ "   and concursoPuestoAgr.activo = :activo "
					+ " order by concursoPuestoAgr.descripcionGrupo ";

			try {

				Query q = em.createQuery(consulta);
				q.setParameter("idConcurso", idConcurso);
				q.setParameter("activo", true);

				listaFiltradaConcursoPuestoAgr = q.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getConcursoPuestoAgrFiltradoSelectItem() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));

		if (idConcurso != null) {
			String consulta =
				"" + " select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr "
					+ " join concursoPuestoAgr.concurso concurso "
					+ " where concurso.idConcurso = :idConcurso "
					+ "   and concursoPuestoAgr.activo = :activo "
					+ " order by concursoPuestoAgr.descripcionGrupo ";

			try {
				Query q = em.createQuery(consulta);
				q.setParameter("idConcurso", idConcurso);
				q.setParameter("activo", true);

				List<ConcursoPuestoAgr> concursoPuestoAgrs = q.getResultList();
				if (concursoPuestoAgrs != null) {
					for (ConcursoPuestoAgr c : concursoPuestoAgrs) {
						lista.add(new SelectItem(c.getIdConcursoPuestoAgr(), c.getDescripcionGrupo()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lista;
	}

	/**
	 * Seleccciona / Deselecciona todos los elementos de la lista de grupos
	 */
	public void cargarSeleccionarTodosConcursoPuestoAgr() {
		if (listaFiltradaConcursoPuestoAgr != null) {
			for (ConcursoPuestoAgr concursoPuestoAgr : listaFiltradaConcursoPuestoAgr) {
				concursoPuestoAgr.setSeleccionado(seleccionarTodosConcursoPuestoAgr);
			}
		}
	}

	public void cargarListaFiltradaConcursoPuestoDet() {

		seleccionarTodosConcursoPuestoDet = false;
		cargarSeleccionarTodosConcursoPuestoDet();

		listaFiltradaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
		if (idConcursoPuestoAgr != null) {
			String consulta =
				"" + " select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet "
					+ " join concursoPuestoDet.concursoPuestoAgr concursoPuestoAgr "
					+ " where concursoPuestoAgr.idConcursoPuestoAgr = :idConcursoPuestoAgr "
					+ "   and concursoPuestoDet.activo = :activo ";

			try {
				Query q = em.createQuery(consulta);
				q.setParameter("idConcursoPuestoAgr", idConcursoPuestoAgr);
				q.setParameter("activo", true);

				listaFiltradaConcursoPuestoDet = q.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Seleccciona / Deselecciona todos los elementos de la lista de puestos
	 */
	public void cargarSeleccionarTodosConcursoPuestoDet() {
		if (listaFiltradaConcursoPuestoDet != null) {
			for (ConcursoPuestoDet concursoPuestoDet : listaFiltradaConcursoPuestoDet) {
				concursoPuestoDet.setSeleccionado(seleccionarTodosConcursoPuestoDet);
			}
		}
	}

	public boolean hayGrupoSeleccionado() {
		for (ConcursoPuestoAgr concursoPuestoAgr : listaFiltradaConcursoPuestoAgr) {
			if (concursoPuestoAgr.getSeleccionado() != null && concursoPuestoAgr.getSeleccionado())
				return true;
		}
		return false;
	}

	public boolean hayPuestoSeleccionado() {
		for (ConcursoPuestoDet concursoPuestoDet : listaFiltradaConcursoPuestoDet) {
			if (concursoPuestoDet.getSeleccionado() != null && concursoPuestoDet.getSeleccionado())
				return true;
		}
		return false;
	}

	// GETTERS Y SETTERS
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
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

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdPuesto() {
		return idPuesto;
	}

	public void setIdPuesto(Long idPuesto) {
		this.idPuesto = idPuesto;
	}

	public PlantaCargoDet getPuesto() {
		return puesto;
	}

	public void setPuesto(PlantaCargoDet puesto) {
		this.puesto = puesto;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public void setListaFiltradaConcursoPuestoAgr(List<ConcursoPuestoAgr> listaFiltradaConcursoPuestoAgr) {
		this.listaFiltradaConcursoPuestoAgr = listaFiltradaConcursoPuestoAgr;
	}

	public List<ConcursoPuestoAgr> getListaFiltradaConcursoPuestoAgr() {
		return listaFiltradaConcursoPuestoAgr;
	}

	public void setSeleccionarTodosConcursoPuestoAgr(Boolean seleccionarTodosConcursoPuestoAgr) {
		this.seleccionarTodosConcursoPuestoAgr = seleccionarTodosConcursoPuestoAgr;
	}

	public Boolean getSeleccionarTodosConcursoPuestoAgr() {
		return seleccionarTodosConcursoPuestoAgr;
	}

	public void setListaFiltradaConcursoPuestoDet(List<ConcursoPuestoDet> listaFiltradaConcursoPuestoDet) {
		this.listaFiltradaConcursoPuestoDet = listaFiltradaConcursoPuestoDet;
	}

	public List<ConcursoPuestoDet> getListaFiltradaConcursoPuestoDet() {
		return listaFiltradaConcursoPuestoDet;
	}

	public void setSeleccionarTodosConcursoPuestoDet(Boolean seleccionarTodosConcursoPuestoDet) {
		this.seleccionarTodosConcursoPuestoDet = seleccionarTodosConcursoPuestoDet;
	}

	public Boolean getSeleccionarTodosConcursoPuestoDet() {
		return seleccionarTodosConcursoPuestoDet;
	}

	public Long getIdConfUoCab() {
		return idConfUoCab;
	}

	public void setIdConfUoCab(Long idConfUoCab) {
		this.idConfUoCab = idConfUoCab;
	}

}
