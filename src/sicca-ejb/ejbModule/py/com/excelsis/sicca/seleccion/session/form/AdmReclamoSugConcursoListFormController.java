package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.EstadoReclamo;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.ReclamoSugerenciaList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("admReclamoSugConcursoListFormController")
public class AdmReclamoSugConcursoListFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	ReclamoSugerenciaList reclamoSugerenciaList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	Usuario usuarioLogueado;
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private String puesto;
	private String codigoUnidOrgDep;
	private Integer ordenUnidOrg;
	private Long idConfigCab;
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private String nomConcurso;
	private String nomGrupo;
	private Date desde;
	private Date hasta;
	private EstadoReclamo estado;

	

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		if (idSinNivelEntidad != null)
			nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		

		if (idSinEntidad != null) {
			sinEntidad = em.find(SinEntidad.class, idSinEntidad);

		}
		if (idConfigCab != null) {
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);
			ordenUnidOrg = configuracionUoCab.getOrden();
		}
		if (idSinEntidad == null && idSinNivelEntidad == null
				&& idConfigCab == null)
				searchAll();

	}

	
	@SuppressWarnings( "unchecked" )
	public String findEstado(Integer n){
		List<Referencias> ref=em.createQuery(" Select r from Referencias r " +
				" where r.dominio like 'ESTADOS_GRUPO' and r.valorNum ="+n).getResultList();
		if(!ref.isEmpty())
			return ref.get(0).getDescAbrev();
		return "";
	}
	
	public String getUrlToPageEntidad() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=seleccion/admReclamoSugerenciaConcurso/AdmReclamoSugerenciaConcursoList&codigoNivel="
				+ nivelEntidad.getNenCodigo();
	}

	public String getUrlToPageNivel() {
		
		return "/planificacion/sinNivelEntidad/SinNivelEntidadList.xhtml?from=seleccion/admReclamoSugerenciaConcurso/AdmReclamoSugerenciaConcursoList";
	}
	
	public String getUrlToPageOee() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}

		sinEntidad = em.find(SinEntidad.class, idSinEntidad);

		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=seleccion/admReclamoSugerenciaConcurso/AdmReclamoSugerenciaConcursoList&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		return retorno;
	}

	
	public String getUrlToPageDependencia() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);

		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (idConfigCab == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
			return null;
		}
		configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=seleccion/admReclamoSugerenciaConcurso/AdmReclamoSugerenciaConcursoList&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
	}

	public void searchAll() {
		nivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		entidad = new Entidad();
		configuracionUoCab = new ConfiguracionUoCab();
		puesto = null;
		codigoUnidOrgDep = null;
		ordenUnidOrg = null;
		desde=null;
		hasta=null;
		idConfigCab = null;
		estado=null;
		reclamoSugerenciaList.limpiarCU200();
		
	}
	

	
	public void search() {
		
		reclamoSugerenciaList.setSinEntidad(sinEntidad);
		reclamoSugerenciaList.setSinNivelEntidad(nivelEntidad);
		reclamoSugerenciaList.setConfiguracionUoCab(configuracionUoCab);
		reclamoSugerenciaList.setNomConcurso(nomConcurso);
		reclamoSugerenciaList.setNomGrupo(nomGrupo);
		reclamoSugerenciaList.setEstado(estado.getValor());
		reclamoSugerenciaList.setDesde(desde);
		reclamoSugerenciaList.setHasta(hasta);
		reclamoSugerenciaList.listaResultadosCU200();
		System.out.println(reclamoSugerenciaList);
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				statusMessages.add(Severity.ERROR, SICCAAppHelper
						.getBundleMessage("nivel_msg_1"));
				return;
			}
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Método que busca la entidad correspondiente al codigo ingresado y el
	 * nivel
	 */
	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null
				&& sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setEntCodigo(
					sinEntidad.getEntCodigo());
			sinEntidadList.getSinEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			sinEntidad = sinEntidadList.entidadMaxAnho();

			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
				idSinEntidad = sinEntidad.getIdSinEntidad();
				entidadList.getSinEntidad().setIdSinEntidad(
						sinEntidad.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
			}
			else{
				statusMessages.add(Severity.ERROR, SICCAAppHelper
						.getBundleMessage("nivel_msg_1"));
				return;
			}
				
		}
	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo
	 * ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {
		if (ordenUnidOrg != null) {
			configuracionUoCabList.getConfiguracionUoCab().setOrden(
					ordenUnidOrg);
			if (entidad != null) {
				Long id = entidad.getIdEntidad();
				configuracionUoCabList.setIdEntidad(id);
			}

			configuracionUoCab = configuracionUoCabList.searchUnidad();
			if (configuracionUoCab != null)
				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
			else{
				statusMessages.add(Severity.ERROR, SICCAAppHelper
						.getBundleMessage("nivel_msg_1"));
				return;
			}
				
		} else
			configuracionUoCab = new ConfiguracionUoCab();
	}

	



	






	
	
	

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */

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

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	


	

	

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}



	public String getNomConcurso() {
		return nomConcurso;
	}



	public void setNomConcurso(String nomConcurso) {
		this.nomConcurso = nomConcurso;
	}



	public String getNomGrupo() {
		return nomGrupo;
	}



	public void setNomGrupo(String nomGrupo) {
		this.nomGrupo = nomGrupo;
	}



	public Date getDesde() {
		return desde;
	}



	public void setDesde(Date desde) {
		this.desde = desde;
	}



	public Date getHasta() {
		return hasta;
	}



	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}



	public EstadoReclamo getEstado() {
		return estado;
	}



	public void setEstado(EstadoReclamo estado) {
		this.estado = estado;
	}



	
	

	

}
