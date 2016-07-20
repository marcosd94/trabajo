package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.EstadoAprobPendiente;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("moficacionFechaSfpFormController")
public class MoficacionFechaSfpFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	SolicProrrogaCabList solicProrrogaCabList;
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	private String puesto;
	private Date desde;
	private Date hasta;
	private EstadoAprobPendiente aprobPendiente= EstadoAprobPendiente.PENDIENTE;
	private String  nomConcurso;
	private String nomGrupo;
	
	
	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		search();
	}

//	public String getUrlToPageEntidad() {
//		if (idSinNivelEntidad == null) {
//			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
//			return null;
//		}
//		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
//		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=seleccion/modificacionFechaSFP/ModificacionFechaSFPList&codigoNivel="
//			+ nivelEntidad.getNenCodigo();
//	}
//
//	public String getUrlToPageNivel() {
//
//		return "/planificacion/sinNivelEntidad/SinNivelEntidadList.xhtml?from=seleccion/modificacionFechaSFP/ModificacionFechaSFPList";
//	}
//
//	public String getUrlToPageOee() {
//		if (idSinNivelEntidad == null) {
//			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
//			return null;
//		}
//		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
//		if (idSinEntidad == null) {
//			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
//			return null;
//		}
//
//		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
//
//		String retorno =
//			"/planificacion/searchForms/FindDependencias.xhtml?from=seleccion/modificacionFechaSFP/ModificacionFechaSFPList&sinNivelEntidadIdSinNivelEntidad="
//				+ nivelEntidad.getIdSinNivelEntidad();
//		if (idSinEntidad != null)
//			retorno = retorno + "&sinEntidadIdSinEntidad=" + sinEntidad.getIdSinEntidad();
//		return retorno;
//	}
//
//	public String getUrlToPageDependencia() {
//		if (idSinNivelEntidad == null) {
//			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
//			return null;
//		}
//		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
//
//		if (idSinEntidad == null) {
//			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
//			return null;
//		}
//		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
//		if (idConfigCab == null) {
//			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
//			return null;
//		}
//		configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);
//
//		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=seleccion/modificacionFechaSFP/ModificacionFechaSFPList&idNivelEntidad="
//			+ nivelEntidad.getIdSinNivelEntidad()
//			+ "&sinEntidadIdSinEntidad="
//			+ sinEntidad.getIdSinEntidad()
//			+ "&idConfiguracionUoCab="
//			+ configuracionUoCab.getIdConfiguracionUo();
//	}

	public void searchAll() {
		nivelEntidadOeeUtil.limpiar();
		puesto = null;
		desde = null;
		hasta = null;
		nomConcurso=null;
		nomGrupo=null;
		aprobPendiente = EstadoAprobPendiente.PENDIENTE;
		solicProrrogaCabList.setEstado(aprobPendiente.getValor());
		solicProrrogaCabList.limpiar261();
	}

	public void search() {
		solicProrrogaCabList.setEstado(aprobPendiente.getValor());
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
			solicProrrogaCabList.setSinEntidad(em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad()));
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
			solicProrrogaCabList.setSinNivelEntidad(em.find(SinNivelEntidad.class, nivelEntidadOeeUtil.getIdSinNivelEntidad()));
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
			solicProrrogaCabList.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
		solicProrrogaCabList.setNomConcurso(nomConcurso);
		solicProrrogaCabList.setNomGrupo(nomGrupo);
		solicProrrogaCabList.setEstado(aprobPendiente.getValor());
		solicProrrogaCabList.setDesde(desde);
		solicProrrogaCabList.setHasta(hasta);
		solicProrrogaCabList.listaResultados261();
	}

//	/**
//	 * Método que busca el nivel correspondiente al codigo ingresado
//	 */
//	public void findNivelEntidad() {
//		if (nivelEntidad.getNenCodigo() != null) {
//			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
//			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
//			if (nivelEntidad != null)
//				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
//			else {
//				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
//				return;
//			}
//		} else
//			nivelEntidad = new SinNivelEntidad();
//	}
//
//	/**
//	 * Método que busca la entidad correspondiente al codigo ingresado y el nivel
//	 */
//	public void findEntidad() {
//		if (nivelEntidad != null && nivelEntidad.getNenCodigo() != null
//			&& sinEntidad.getEntCodigo() != null) {
//			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidad.getEntCodigo());
//			sinEntidadList.getSinEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
//			sinEntidad = sinEntidadList.entidadMaxAnho();
//
//			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
//				idSinEntidad = sinEntidad.getIdSinEntidad();
//				entidadList.getSinEntidad().setIdSinEntidad(sinEntidad.getIdSinEntidad());
//				entidad = entidadList.getEntidadBySinEntidad();
//			} else {
//				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
//				return;
//			}
//
//		}
//	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo ingresado, a la entidad y al nivel
	 */
//	public void findUnidadOrganizativa() {
//		if (ordenUnidOrg != null) {
//			configuracionUoCabList.getConfiguracionUoCab().setOrden(ordenUnidOrg);
//			if (entidad != null) {
//				Long id = entidad.getIdEntidad();
//				configuracionUoCabList.setIdEntidad(id);
//			}
//
//			configuracionUoCab = configuracionUoCabList.searchUnidad();
//			if (configuracionUoCab != null)
//				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
//			else {
//				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("nivel_msg_1"));
//				return;
//			}
//
//		} else
//			configuracionUoCab = new ConfiguracionUoCab();
//	}

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */






	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
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

	public EstadoAprobPendiente getAprobPendiente() {
		return aprobPendiente;
	}

	public void setAprobPendiente(EstadoAprobPendiente aprobPendiente) {
		this.aprobPendiente = aprobPendiente;
	}
	
	

}
