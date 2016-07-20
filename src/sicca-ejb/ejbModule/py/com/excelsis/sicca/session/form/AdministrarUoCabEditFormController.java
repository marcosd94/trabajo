package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabHome;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Name("administrarUoCabEditFormController")
@Scope(ScopeType.CONVERSATION)
public class AdministrarUoCabEditFormController implements Serializable {

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConfiguracionUoCabHome configuracionUoCabHome;

	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	General general;
	private Long idConfiguracionUo;
	private ConfiguracionUoCab configuracionUoCab;
	private String mensaje;
	private String codigo;
	private Boolean inactivar;
	private List<ConfiguracionUoDet> hijosInactivos;

	SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
	private String anioActual = sdfSoloAnio.format(new Date());
	private boolean primeraEntrada=true;
	private Integer anio=null;
	
	public void init() {
		try {
			if(primeraEntrada)
			{
				primeraEntrada=false;
				configuracionUoCab = new ConfiguracionUoCab();
				codigo=null;
				if (idConfiguracionUo != null) {
					configuracionUoCab = em.find(ConfiguracionUoCab.class,
							idConfiguracionUo);
					cargarCabecera();
					
				}else{
					nivelEntidadOeeUtil.limpiar();
					configuracionUoCab.setActivo(true);
				}
					
				
				configuracionUoCabHome.setInstance(configuracionUoCab);
			}else{
				nivelEntidadOeeUtil.init();
				if (nivelEntidadOeeUtil.getIdSinEntidad()!= null) {
					calcularOrden();
				}
			}

			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void cargarCabecera(){
		
		nivelEntidadOeeUtil.setIdConfigCab(idConfiguracionUo);
		nivelEntidadOeeUtil.setAnioOee(anio);
		nivelEntidadOeeUtil.init2();
		obtenerCod();
	}


	

	



	private Integer obtenerOrden() {
		Integer ordern = 0;
		try {
			if (nivelEntidadOeeUtil.getIdSinEntidad()!=null){
				String consulta = "" + 
					"select max(vwEntidadOee.orden) " +
					"from VwEntidadOee vwEntidadOee " +
					"where  vwEntidadOee.sinEntidad.idSinEntidad=:idSinEntidad";
					
				Query q = em.createQuery(consulta);
				q.setParameter("idSinEntidad", nivelEntidadOeeUtil.getIdSinEntidad());
				ordern = (Integer) q.getSingleResult();
				
				if (ordern == null)
					ordern = 1;
				else
					ordern++;
			}
		} catch (NoResultException ex) {
			ex.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ordern;
	}
	
	
	public void calcularOrden(){
		if (configuracionUoCab != null){
			configuracionUoCab.setOrden(obtenerOrden());
			obtenerCod();
		}
	}
	public void cargarEntidad(){
		nivelEntidadOeeUtil.findEntidad();
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
			calcularOrden();
		else
		{
			configuracionUoCab.setOrden(null);
			codigo=null;
		}
	}

	public void obtenerCod() {

		if (configuracionUoCab.getOrden() != null) {
			codigo = nivelEntidadOeeUtil.getCodNivelEntidad() + "." + nivelEntidadOeeUtil.getCodSinEntidad() + "."
					+ configuracionUoCab.getOrden();
		} else {
			codigo =nivelEntidadOeeUtil.getCodNivelEntidad() + "." + nivelEntidadOeeUtil.getCodSinEntidad();
		}

	}

	

	

	public String update() {
		try {
			if (!checkData("update"))
				return null;
			if (!valido("updated")) {
				mensaje = "Ya existe una OEE con el codigo ingresado";
				statusMessages.add(Severity.ERROR, mensaje);
				return null;
			}
			if (!configuracionUoCab.getActivo()) {
				inactivar();
			} else {
				activar();
			}
			if (!configuracionUoCab.getActivo() && !inactivar)// Si no se puede
																// inactivar
			{
				statusMessages
						.add(Severity.INFO,
								"No se puede inactivar. No posee puesto vacante o inactivo, Verifique");
				return null;
			}
			upDetalles();

			configuracionUoCab.setFechaMod(new Date());
			configuracionUoCab.setUsuMod(usuarioLogueado.getCodigoUsuario()
					.toUpperCase());
		//	configuracionUoCab.setEntidad(em.find(Entidad.class, nivelEntidadOeeUtil.getIdSinEntidad()));
			configuracionUoCab.setDenominacionUnidad(configuracionUoCab
					.getDenominacionUnidad().trim().toUpperCase());
			configuracionUoCab.setMision(configuracionUoCab.getMision()
					.toUpperCase().trim());
			configuracionUoCab.setDescripcionCorta(configuracionUoCab
					.getDescripcionCorta().toUpperCase().trim());
			configuracionUoCab.setDireccion(configuracionUoCab.getDireccion()
					.toUpperCase().trim());
			//SE AGREGA CAMPO URL - MP 28-12-2015
			configuracionUoCab.setUrl(configuracionUoCab.getUrl()
					.toUpperCase().trim());
			em.merge(configuracionUoCab);
			
			
			
			
			
			em.flush();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "updated";
	}

	public String save() {
		try {
			if (!checkData("persist"))
				return null;
			if (!valido("save")) {
				mensaje = "Ya existe una OEE con el codigo ingresado";
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, mensaje);
				return null;
			}
			Date actual = new Date();
			configuracionUoCab.setAnho(actual.getYear() + 1900);
			configuracionUoCab.setFechaAlta(new Date());
			configuracionUoCab.setUsuAlta(usuarioLogueado.getCodigoUsuario()
					.toUpperCase());
			configuracionUoCab.setDenominacionUnidad(configuracionUoCab
					.getDenominacionUnidad().trim().toUpperCase());
			configuracionUoCab.setMision(configuracionUoCab.getMision()
					.toUpperCase().trim());
			configuracionUoCab.setDescripcionCorta(configuracionUoCab
					.getDescripcionCorta().toUpperCase().trim());
			configuracionUoCab.setDireccion(configuracionUoCab.getDireccion()
					.toUpperCase().trim());
			configuracionUoCab.setUrl(configuracionUoCab.getUrl()
					.toUpperCase().trim());
			em.persist(configuracionUoCab);
			
			
			String consulta = "" + 
					"select s.aniAniopre " +
					"from SinEntidad s " +
					"where  s.idSinEntidad=:idSinEntidad";
					
			Query q = em.createQuery(consulta);
			q.setParameter("idSinEntidad", nivelEntidadOeeUtil.getIdSinEntidad());
			BigDecimal	ani_aniopre = (BigDecimal) q.getSingleResult();
		
			SinEntidad s = em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad());
			Entidad entidad= new Entidad();
			entidad.setAnho(actual.getYear() + 1900);
			entidad.setConfiguracionUoCab(configuracionUoCab);
			entidad.setSinEntidad(em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad()));
			entidad.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
			entidad.setEntCodigo(nivelEntidadOeeUtil.getCodSinEntidad());
			entidad.setAniAniopre(ani_aniopre);
			em.persist(entidad);
			
			configuracionUoCab.setEntidad(entidad);
			em.merge(configuracionUoCab);
			
			em.flush();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "persisted";
	}

	/**
	 * Verifica que si existe no debe crear
	 * */
	@SuppressWarnings("unchecked")
	private Boolean valido(String accion) {
		String sql = "select vwOee   "
				+ " from VwEntidadOee vwOee "
				+ " where vwOee.codigoNivel=:nenCod and vwOee.codigoEntidad=:entCod "
				+ " and vwOee.orden=:orden";

		if (accion.equals("updated"))
			sql = sql + " and vwOee.configuracionUo.idConfiguracionUo!= "
					+ configuracionUoCab.getIdConfiguracionUo();
		List<ConfiguracionUoCab> lista = new ArrayList<ConfiguracionUoCab>();
		lista = em.createQuery(sql).setParameter("nenCod", nivelEntidadOeeUtil.getCodNivelEntidad())
		.setParameter("entCod", nivelEntidadOeeUtil.getCodNivelEntidad()+"."+nivelEntidadOeeUtil.getCodSinEntidad()).setParameter("orden", configuracionUoCab.getOrden()).getResultList();
		if (lista.size() > 0) 
			return false;
		
		return true;
	}

	/**
	 * Retorna el codigo de OEE
	 * */
	public String codigos(Long id) {
		Map<String, String> codOEe= nivelEntidadOeeUtil.codigoDescripcionOee(id);
		return codOEe.get("CODIGO");
	}

	/**
	 * verificaque los datos no se dupliquen
	 * */
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql = "SELECT o FROM ConfiguracionUoCab o " +
				" where lower(o.denominacionUnidad)= :desc";
				
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idConfiguracionUo != "
					+ configuracionUoCab.getIdConfiguracionUo().longValue();
		}
		List<RequisitoMinimoCpt> list = em.createQuery(hql).setParameter("desc", configuracionUoCab.getDenominacionUnidad()).getResultList();
		return list.isEmpty();
	}

	/**
	 * Verifica que cumpla con las validaciones antes de guardar
	 * **/
	private boolean checkData(String operation) {

		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_registro_duplicado"));
			return false;
		}
		if (configuracionUoCab.getCodigoSinarh() != null
				&& !configuracionUoCab.getCodigoSinarh().trim().isEmpty()) {
			if (!sinarhValido()) {
				return false;
			}
			
		}
		
		if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar una Entidad ");
			return false;
		}
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null || nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(" Seleccione los campos obligatorios");
			return false;
		}
		if(configuracionUoCab.getVigenciaDesde()!=null){
			if( !general.isFechaValida(configuracionUoCab.getVigenciaDesde())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha Vigencia Desde inválida");
				return false;
			}
		}
		if(configuracionUoCab.getVigenciaHasta()!=null){
			if( !general.isFechaValida(configuracionUoCab.getVigenciaHasta())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha Vigencia Hasta inválida");
				return false;
			}
		}
		
		if(configuracionUoCab.getDenominacionUnidad()==null || configuracionUoCab.getDenominacionUnidad().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe ingresar el campo Denominaci\u00F3n Unidad antes de realizar la acci\u00F3n. Verifique");
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(configuracionUoCab.getDenominacionUnidad().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		
//		if(configuracionUoCab.getMision()==null || configuracionUoCab.getMision().trim().equals("")){
//			statusMessages.add(Severity.ERROR,"Debe ingresar el campo Mision / Finalidad antes de realizar la acci\u00F3n. Verifique");
//			return false;
//		}
		if(seguridadUtilFormController.contieneCaracter(configuracionUoCab.getMision().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(configuracionUoCab.getOrden()==null){
			statusMessages.add(Severity.ERROR, "Debe ingresar el campo Orden antes de realizar la acci\u00F3n. Verifique");
			return false;
		}
		if(configuracionUoCab.getOrden().intValue()<=0){
			statusMessages.add(Severity.ERROR, "El campo Orden debe ser mayor a cero. Verifique");
			return false;
		}
		
		if (configuracionUoCab.getVigenciaDesde() != null && configuracionUoCab.getVigenciaHasta() != null) {
			if (configuracionUoCab.getVigenciaDesde().after(
				configuracionUoCab.getVigenciaHasta())) {
				statusMessages.add("La fecha DESDE no debe ser mayor a la fecha HASTA, verifique");
				return false;
			}
		}
		

		return true;
	}
	/**
	 * Verifica que el codigo sinarh sea valido
	 * */
	@SuppressWarnings("unchecked")
	private boolean sinarhValido() {
		String codSin = configuracionUoCab.getCodigoSinarh();
		String[] cihar = codSin.split("\\/");
		List<SinAnx> sinAnxsList = new ArrayList<SinAnx>();
		boolean existe=true;
		String codigosInvalidos="";
		
		if(codSin.contains("//")){
			statusMessages
			.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("ConfiguracionUoDet_msg_sinhar"));
			return false;
		}
		
		
		for (int i = 0; i < cihar.length; i++) {
			String cod=cihar[i];
			sinAnxsList = em.createQuery(
						"Select a from SinAnx a " +
						" where a.nenCodigo||'.'||a.entCodigo||'.'||a.tipCodigo||'.'||a.proCodigo =:cod"+
						" order by a.aniAniopre desc " 	).setParameter("cod", cod).getResultList();
			
			if(sinAnxsList.isEmpty())
			{
				existe=false;
				if(codigosInvalidos.equals(""))
					codigosInvalidos+= cod;
				else
					codigosInvalidos+=" / "+ cod;
			}
		}
		if(!existe){
			statusMessages
			.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("ConfiguracionUoDet_msg_sinhar")+". Verifique los codigos: "+codigosInvalidos);
		}
		
		return existe;
		
	}

	/**
	 * Selecciona los hijos que van ser seleccionados , modifica el campo setEstadoAnterior
	 * */
	@SuppressWarnings("unchecked")
	private void inactivar() {
		inactivar = true;
		List<ConfiguracionUoDet> hijos = new ArrayList<ConfiguracionUoDet>();
		hijosInactivos = new ArrayList<ConfiguracionUoDet>();
		hijos = em.createQuery(
				" select d from ConfiguracionUoDet d where "
						+ " d.configuracionUoCab.idConfiguracionUo = "
						+ configuracionUoCab.getIdConfiguracionUo())
				.getResultList();

		for (int i = 0; i < hijos.size(); i++) {
			ConfiguracionUoDet aux = new ConfiguracionUoDet();

			if (esVacante(hijos.get(i).getIdConfiguracionUoDet())) {
				aux = hijos.get(i);
				if (aux.getActivo())
					aux.setEstadoAnterior("A");
				else
					aux.setEstadoAnterior("I");
				hijosInactivos.add(aux);

			} else {
				inactivar = false;
				hijosInactivos.clear();
				break;
			}
		}
	}

	/**
	 * Modifica el campo EstadoAnterior y seleciona los hijos a ser actualizados a activos
	 * */
	@SuppressWarnings("unchecked")
	private void activar() {
		List<ConfiguracionUoDet> hijos = new ArrayList<ConfiguracionUoDet>();
		hijosInactivos = new ArrayList<ConfiguracionUoDet>();
		hijos = em.createQuery(
				" select d from ConfiguracionUoDet d where "
						+ " d.configuracionUoCab.idConfiguracionUo = "
						+ configuracionUoCab.getIdConfiguracionUo())
				.getResultList();

		for (int i = 0; i < hijos.size(); i++) {
			ConfiguracionUoDet det = new ConfiguracionUoDet();
			if (hijos.get(i).getEstadoAnterior() != null
					&& hijos.get(i).getEstadoAnterior().equals("A")) {
				det = hijos.get(i);
				det.setActivo(true);
				hijosInactivos.add(det);
			}
		}

	}

	
	@SuppressWarnings("unchecked")
	private boolean esVacante(Long idConfUoDet) {
		List<PlantaCargoDet> det = em
				.createQuery(
						" select p from PlantaCargoDet p "
								+ " where p.configuracionUoDet.idConfiguracionUoDet= "
								+ idConfUoDet
								+ " and (lower(p.estado) like 'vacante' or p.activo = FALSE)")
				.getResultList();
		return !det.isEmpty();
	}

	/**
	 * Inactiva/Activa los hijos
	 * */
	private void upDetalles() {
		for (int i = 0; i < hijosInactivos.size(); i++) {
			em.merge(hijosInactivos.get(i));
			em.flush();
		}
	}

	// GETTER Y SETTER

	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	


	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	

	


}
