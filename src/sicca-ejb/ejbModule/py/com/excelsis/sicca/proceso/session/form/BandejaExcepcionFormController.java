package py.com.excelsis.sicca.proceso.session.form;

import java.io.Serializable;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.ActividadEnum;
import enums.TipoExcepcion;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.proceso.session.BandejaExcepcionList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("bandejaExcepcionFormController")
public class BandejaExcepcionFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5895021722537525741L;
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
	BandejaExcepcionList bandejaExcepcionList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;


	private String concursoDesc;
	private Long idRol;

	private Long idExcepcion;
	private String actividad;
	
	private Boolean esAdministradorSFP = false;

	public void init() {
		cargarAdministradorSFP();

		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)){
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		
		search();
	}

	public void searchAll() {
		// configuracionUoCab = new ConfiguracionUoCab();
		if (!validar(false))
			return;
		
		idRol = null;
		concursoDesc = null;

		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();

		String query = getQuery();
		bandejaExcepcionList.listaResultados(query);
	}

	public void search() {
		try{
			if (!validar(false))
				return;
			
			String query = getQuery();
			bandejaExcepcionList.listaResultados(query);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getQuery() {
		String query =
			"select distinct bandejaExcepcion " + " from BandejaExcepcion bandejaExcepcion "
				+ " join bandejaExcepcion.configuracionUoCab configuracionUoCab "
				+ " join bandejaExcepcion.actividadProceso actividadProceso "
				+ " join actividadProceso.procActividadRols procActividadRol "
				+ " join procActividadRol.rol rol ";

		// WHERE
		query +=
			" where (bandejaExcepcion.usuario is null or bandejaExcepcion.usuario = '"
				+ usuarioLogueado.getCodigoUsuario() + "') ";

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			query +=
				" and configuracionUoCab.idConfiguracionUo = "
					+ nivelEntidadOeeUtil.getIdConfigCab() + " ";

		if (idRol != null) {
			query += " and rol.idRol = " + idRol + " ";
		} else {
			String roles = getIdRolesUsuario();
			query += " and rol.idRol in (" + roles + ") ";
		}

		if (concursoDesc != null)
			query += " and lower(bandejaExcepcion.concurso.nombre) like '%" + concursoDesc + "%'";

		return query;
	}

	
	public void cargarCabecera(){
		grupoPuestosController = (GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();
		
		SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();
		
		//Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());
		
		nivelEntidadOeeUtil.init();
	}




	private String getIdRolesUsuario() {
		String id = "";
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			id += "," + r.getId();
		}
		id = id.replaceFirst(",", "");
		return id;
	}

	public String redirect() {
		if (idExcepcion != null && actividad != null) {

			Excepcion excepcion = em.find(Excepcion.class, idExcepcion);
			boolean tareaAsignada =
				jbpmUtilFormController.asignarTaskInstanceActual(excepcion.getIdProcessInstance(), usuarioLogueado.getCodigoUsuario());

			if (!tareaAsignada) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ocurrio un error al inicializar la tarea. Consulte con el administrador del sistema.");
				return null;
			}

			if (ActividadEnum.EXCEP_APROBAR_SFP.getValor().equalsIgnoreCase(actividad)) {
				// APROBAR
				if (TipoExcepcion.INCLUSION_MIEMBRO_CS.getDescripcion().equalsIgnoreCase(excepcion.getTipo()))
					return "/seleccion/aprobacionInclusionSFP/ExcepcionList.seam?excepcionIdExcepcion="
						+ idExcepcion;
				else if (TipoExcepcion.PRORROGA_PLAZO_TOTAL_CONCURSO_GRUPO.getDescripcion().equalsIgnoreCase(excepcion.getTipo()))
					return null;

			} else if (ActividadEnum.EXCEP_REVISAR_OEE.getValor().equalsIgnoreCase(actividad)) {
				// REVISAR
				if (TipoExcepcion.INCLUSION_MIEMBRO_CS.getDescripcion().equalsIgnoreCase(excepcion.getTipo()))
					return "/seleccion/excepcion/inclusionMiembroCom/VerificarInclusionMiembro.seam?excepcion="
						+ idExcepcion;
				else if (TipoExcepcion.PRORROGA_PLAZO_TOTAL_CONCURSO_GRUPO.getDescripcion().equalsIgnoreCase(excepcion.getTipo())) {
					return "/seleccion/admSolicitudProrroga/SolicitudProrroga.seam?excepcion="
						+ idExcepcion + "&modoVista=false"
						+ "from=/proceso/BandejaExcepcionList.seam";
				}
			}

			else if (ActividadEnum.EXCEP_ADJUNTAR_DOC_CANCELACION_AUTOMATICA.getValor().equalsIgnoreCase(actividad)) {
				return "/seleccion/excepcion/cancelacion/AdmAdjuntoCancelacionEdit.seam?idExcepcion="
					+ idExcepcion;
			}

		}
		return null;
	}
	
	
	private Boolean validar(Boolean controlarCab) {
		if ( nivelEntidadOeeUtil.getIdSinNivelEntidad()== null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		}
		else if ( nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		}
		else if ( nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}	
		
		return true;
	}
	
	
	/**
	 * Verifica si un usuario puede ver todas las OEE o no
	 * @return
	 */
	private void cargarAdministradorSFP() {
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador() ){
				setEsAdministradorSFP(true);
				return;
			}
		}

		setEsAdministradorSFP(false);
	}
	

	public String getConcursoDesc() {
		return concursoDesc;
	}

	public void setConcursoDesc(String concursoDesc) {
		this.concursoDesc = concursoDesc;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

}
