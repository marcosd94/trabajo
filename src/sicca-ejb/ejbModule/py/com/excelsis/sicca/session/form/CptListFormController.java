package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.CptObs;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.CptList;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.Estado;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("cptListFormController")
public class CptListFormController implements Serializable {

	private Integer nivel;
	private Integer numero;
	private Long idTipoCpt;
	private Long idConfiguracionUoCab;
	
	private Integer nroEspecifico;
	private String denominacion;
	private Estado estado = Estado.ACTIVO;
	private String tipo;
	private List<Cpt> listaCpt = new ArrayList<Cpt>();
	private String from;
	private ConfiguracionUoCab configuracionUoCab;
	@In(create = true)
	CptList cptList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	
	public Boolean esHomologador;

	public void init() throws Exception {
		// listaCpt = new ArrayList<Cpt>();
		esHomologador = this.isUsuarioHomologador(usuarioLogueado);
		search();
	}
	
	public void initParaHomologacion() throws Exception {
		// listaCpt = new ArrayList<Cpt>();
		
		long idConfiguracionUoCabAux = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo().longValue();
		this.configuracionUoCab = em.find(ConfiguracionUoCab.class,idConfiguracionUoCabAux );
		esHomologador = this.isUsuarioHomologador(usuarioLogueado);
		searchParaHomologacion();
	}
	
	public boolean habilitarComboOEE(){
		return usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo().equals(new Long (1000));
	}


	public void searchAll() throws Exception {
		nivel = null;
		numero = null;
		idTipoCpt = null;
		nroEspecifico = null;
		denominacion = null;
		estado = Estado.ACTIVO;
		search();
	}
	
	public void searchAllParaHomologacion() throws Exception {
		nivel = null;
		numero = null;
		idTipoCpt = null;
		nroEspecifico = null;
		denominacion = null;
		idConfiguracionUoCab = null;
		estado = Estado.ACTIVO;
		searchParaHomologacion();
	}

	public void searchParaHomologacion() throws Exception {

		String query = getQuery();
		query += " and ( cpt.estadoHomologacion != '" + Cpt.ESTADO_HOMOLOGADO + "'";
		query += " or cpt.estadoHomologacion = null) ";
		query += " and cpt.cptPadre != null";
		if(idConfiguracionUoCab != null)
			query += " and configuracionUoCab.idConfiguracionUo = " + idConfiguracionUoCab;
		else{
		if(configuracionUoCab != null && configuracionUoCab.getIdConfiguracionUo() != null && !isUsuarioHomologador(this.usuarioLogueado))
			query += " and configuracionUoCab.idConfiguracionUo = " + configuracionUoCab.getIdConfiguracionUo();
		}
		
		if(this.esHomologador)
			query += " and cpt.estadoHomologacion = '" + Cpt.ESTADO_PARA_HOMOLOGACION + "'";
		
		if (query == null)
			return;
		listaCpt = cptList.listaResultadosCptParaHomologacion(query);
	
	}
	
	private boolean isUsuarioHomologador(Usuario usuario){
		Iterator <UsuarioRol> it = usuario.getUsuarioRols().iterator();
		boolean retorno = false;
		
		while (it.hasNext()){
			if(it.next().getRol().getHomologador())
				retorno = true;
			
		}
				
		return retorno;
		
	}
	
	
	
	public void search() throws Exception {

		String query = getQuery();
		if( tipo!=null && !tipo.equalsIgnoreCase("general"))
		query += " and cpt.estadoHomologacion = '" + Cpt.ESTADO_HOMOLOGADO + "'";
		
				
		if (query == null)
			return;
		listaCpt = cptList.listaResultadosCpt(query);
	
	}

	public String getQuery() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		// Select
		String query = "select cpt from Cpt cpt ";
		if (tipo.equals("especifico"))
			query += " join cpt.tipoCpt tipo";
		// Where
		
		query += " join cpt.configuracionUoCab configuracionUoCab"; 
		query += " where 1=1 ";

		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			if (estado.getValor()) {
				query += " and cpt.activo is true";
				cptList.setEstado(Estado.ACTIVO);
			} else {
				query += " and cpt.activo is false";
				cptList.setEstado(Estado.INACTIVO);
			}
		}else{
			cptList.setEstado(Estado.TODOS);
		}

		if (tipo.equals("especifico") && idTipoCpt != null) {
			if (!sufc.validarInput(idTipoCpt.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			query += " and tipo.idTipoCpt = " + idTipoCpt;
		}

		if (tipo.equals("general"))
			query += " and cpt.nroEspecifico is null";
		if (!(tipo.equals("general")) && nroEspecifico == null)
			query += " and cpt.nroEspecifico is not null";
		if (!(tipo.equals("general")) && nroEspecifico != null) {
			if (!sufc.validarInput(nroEspecifico.toString(), TiposDatos.INTEGER.getValor(), null)) {
				return null;
			}
			query += " and cpt.nroEspecifico = " + nroEspecifico;
		}

		if (nivel != null) {
			if (!sufc.validarInput(nivel.toString(), TiposDatos.INTEGER.getValor(), null)) {
				return null;
			}
			query += " and cpt.nivel = " + nivel;
		}

		if (numero != null) {
			if (!sufc.validarInput(numero.toString(), TiposDatos.INTEGER.getValor(), null)) {
				return null;
			}
			query += " and cpt.numero = " + numero;
		}

		if (denominacion != null && !denominacion.trim().isEmpty()) {
			if (!sufc.validarInput(denominacion, TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			query +=
				" and lower(cpt.denominacion) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(denominacion.toLowerCase())
					+ "','%')))";
		}
		return query;
	}
	
	
	public void enviarParaHomologacion(Long id){
		
		Cpt cpt = em.find(Cpt.class, id);
		if (cpt != null){
			cpt.setEstadoHomologacion(cpt.ESTADO_PARA_HOMOLOGACION);
			cpt.setFechaSolicitudHomologacion(new Date());
			em.merge(cpt);
			em.flush();
		}
		
		this.enviarMailSolicitud(id);
		
		
	}
	
	//ENVIO DE MAILS - REGLA DEL 13/05/2016	
	/*
	 * Durante el proceso de homologación de CPT Específico, 
	 * el sistema debe enviar emails a los responsables de la SFP y de la OEE en los siguientes momentos:
	 * 
	 *  1. Cuando el usuario OEE envía su pedido de homologación, 
	 *  	el sicca debe enviar un email al staff SFP con el siguiente texto: 
	 *  	“La Institución NOMBRE DE LA INSTITUCIÓN DEL USUARIO ha enviado el perfil NOMBRE DEL PERFIL para su homologación”
	 * 
	 *  2. Cuando el usuario SFP homologa un perfil, debe enviar un correo al usuario OEE que solicitó la homologación del perfil 
	 *  	con el siguiente texto: “La Secretaria de la Función Pública ha homologado su perfil NOMBRE DEL PERFIL”.
	 *  
	 *  3. Cuando el usuario SFP remite una observación sobre el perfil,
	 *  	 debe enviar un correo al usuario OEE que solicitó la homologación del perfil con el siguiente texto:
	 *  	 “La Secretaria de la Función Pública ha agregado observaciones acerca del perfil NOMBRE DEL PERFIL, favor verificar”.
	 *  
	 *  
	 *  El grupo de la SFP que debe recibir el email está dado por todos los usuarios con el perfil
	 *   "ADMINISTRADOR DE CONCURSOS Y PERFILES SFP". 
	 *   
	 *   El usuario de la OEE que recibirá los correos será el que haya solicitado la homologación.
	 * 
	 * */
	
	
	private void enviarMailSolicitud(Long idCpt){
		
		Cpt cpt = em.find(Cpt.class, idCpt);
		String asunto = "Solicitud de Homologación CPT";
		String texto = "La Institución "
				+ cpt.getConfiguracionUoCab().getDenominacionUnidad()
				+ " ha enviado el perfil "
				+ " <a href=\" https://www.paraguayconcursa.gov.py//sicca/planificacion/cpt/CptGestionarHomologacion.seam?"
				+ "cptIdCpt="+cpt.getIdCpt()
				+ "&tipo=especifico\""
					+ ">" + cpt.getDenominacion()+ "</a>"
				+ " para su homologación";
		
		String sql = " select * from general.persona persona "
				+ "join seguridad.usuario usuario on usuario.id_persona = persona.id_persona "
				+ "join seguridad.usuario_rol usuario_rol on usuario_rol.id_usuario = usuario.id_usuario "
				+ "join seguridad.rol rol on rol.id_rol = usuario_rol.id_rol "
				+ "and rol.descripcion = 'ADMINISTRADOR DE CONCURSOS Y PERFILES SFP'";
		
		
		List<Persona> listaPersonas = em.createNativeQuery(sql,Persona.class).getResultList();
		
		
		for(Persona persona : listaPersonas){
		
			String dirEmail = persona.getEMailFuncionario();
			
			try {
				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarMailHomologado(Long idCpt){
		
		Cpt cpt = em.find(Cpt.class, idCpt);
		String asunto = "Comunicación de Homologación CPT";
		String texto = "La Secretaria de la Función Pública ha homologado su perfil "
					+ " <a href=\" https://www.paraguayconcursa.gov.py//sicca/planificacion/cpt/CptGestionarHomologacion.seam?"
					+ "cptIdCpt="+cpt.getIdCpt()
					+ "&tipo=especifico\""
						+ ">" + cpt.getDenominacion()+ "</a>"
				
				;
		
		String sql = "select * from general.persona persona "
				+ "join seguridad.usuario usuario on usuario.id_persona = persona.id_persona "
				+ "and usuario.id_configuracion_uo =  "+cpt.getConfiguracionUoCab().getIdConfiguracionUo()
				+ "join seguridad.usuario_rol usuario_rol on usuario_rol.id_usuario = usuario.id_usuario  "
				+ "join seguridad.rol rol on rol.id_rol = usuario_rol.id_rol  "
				+ "and rol.descripcion = 'ADMINISTRADOR DE CONCURSOS Y PERFILES OEE' ";
		
		List<Persona> listaPersonas = em.createNativeQuery(sql,Persona.class).getResultList();
		
		
		for(Persona persona : listaPersonas){
		
			String dirEmail = persona.getEMailFuncionario();
			
			try {
				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void enviarMailObservacion(Long idCpt){
		Cpt cpt = em.find(Cpt.class, idCpt);
		//recuperar la observacion que no tenga respuesta. 
		
		String sqlObservaciones = "select observacion_sfp from planificacion.cpt_obs where respuesta_oee is null and id_cpt = " + idCpt;
		
		String observacion = (String) em.createNativeQuery(sqlObservaciones).getSingleResult();

		
		String asunto = "Comunicación de Registro de Observación CPT";
		String texto = "La Secretaria de la Función Pública ha agregado observaciones acerca del perfil "
				+ " <a href=\" https://www.paraguayconcursa.gov.py//sicca/planificacion/cpt/CptGestionarHomologacion.seam?"
				+ "cptIdCpt="+cpt.getIdCpt()
				+ "&tipo=especifico\""
					+ ">" + cpt.getDenominacion()+ "</a>"
				+ ". Favor Verificar"
				+"<br>Observación: "+observacion;
		
		
		
		
		
		String sql = "select * from general.persona persona "
				+ "join seguridad.usuario usuario on usuario.id_persona = persona.id_persona "
				+ "and usuario.id_configuracion_uo =  "+cpt.getConfiguracionUoCab().getIdConfiguracionUo()
				+ "join seguridad.usuario_rol usuario_rol on usuario_rol.id_usuario = usuario.id_usuario  "
				+ "join seguridad.rol rol on rol.id_rol = usuario_rol.id_rol  "
				+ "and rol.descripcion = 'ADMINISTRADOR DE CONCURSOS Y PERFILES OEE' ";
		
		List<Persona> listaPersonas = em.createNativeQuery(sql,Persona.class).getResultList();
		
		
	
		
		for(Persona persona : listaPersonas){
		
			String dirEmail = persona.getEMailFuncionario();
			
			try {
				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void enviarMailRespuesta(Long idCpt){
		
		Cpt cpt = em.find(Cpt.class, idCpt);
		String sqlRespuesta = "select respuesta_oee from planificacion.cpt_obs "
				+ "where fecha_respuesta_oee = (select max(fecha_respuesta_oee) from planificacion.cpt_obs where id_cpt = "+idCpt+")"
				+ " and id_cpt = "+idCpt;
		String respuesta = (String)em.createNativeQuery(sqlRespuesta).getSingleResult();
		
		String asunto = "Comunicación de Registro de Respuesta CPT";
				
		String texto = "La Institución "
				+ cpt.getConfiguracionUoCab().getDenominacionUnidad()
				+ " ha agregado Respuestas a las observaciones del perfil "
				+ " <a href=\" https://www.paraguayconcursa.gov.py//sicca/planificacion/cpt/CptGestionarHomologacion.seam?"
				+ "cptIdCpt="+cpt.getIdCpt()
				+ "&tipo=especifico\""
					+ ">" + cpt.getDenominacion()+ "</a>"
				+ ". Favor Verificar"
				+"<br>"
				+ "Respuesta: "+respuesta;
		
		String sql = " select * from general.persona persona "
				+ "join seguridad.usuario usuario on usuario.id_persona = persona.id_persona "
				+ "join seguridad.usuario_rol usuario_rol on usuario_rol.id_usuario = usuario.id_usuario "
				+ "join seguridad.rol rol on rol.id_rol = usuario_rol.id_rol "
				+ "and rol.descripcion = 'ADMINISTRADOR DE CONCURSOS Y PERFILES SFP'";
		
		List<Persona> listaPersonas = em.createNativeQuery(sql,Persona.class).getResultList();
		
		
		for(Persona persona : listaPersonas){
		
			String dirEmail = persona.getEMailFuncionario();
			
			try {
				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
		
	public void imprimirCptNuevoEscalasParametros(Long idCpt) {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idCpt",idCpt);
		
		
		//Consulta para recuperar todos los registros para la seccion de valorizacion de los componentes
		String sql = "select distinct req_min_cpt.orden " //0
				+ " ,req_min_cpt.descripcion as componente "//
				+ " ,escala.descripcion"//2
				+ " ,escala.desde "//6
				+ " ,escala.hasta"//4
				+ " ,det_req_min.puntaje_req_min as puntaje "//5
				+ " from planificacion.det_minimos_requeridos det_min "
				+ " join planificacion.det_req_min det_req_min "
				+ " on det_req_min.id_det_req_min = det_min.id_det_req_min "
				+ " join planificacion.requisito_minimo_cpt req_min_cpt "
				+ " on req_min_cpt.id_requisito_minimo_cpt = det_req_min.id_requisito_minimo_cpt "
				+ " join planificacion.escala_req_min escala "
				+ " on escala.id_requisito_minimo_cpt = req_min_cpt.id_requisito_minimo_cpt "
				+ " where det_min.id_det_req_min in "
				+ " (select id_det_req_min from planificacion.det_req_min drm where id_cpt = "+ idCpt+ ") "
				+ "  ORDER BY  orden,componente, desde";
		
		List<Object[]> lista = em.createNativeQuery(sql).getResultList();
		
		int fila = 0;
		int columna = 1;
		String componente = "";
		String escala = "";
		Float puntaje;
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("tipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("escala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("puntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
			
		
		JasperReportUtils.respondPDF("RPT_imprimirCpt_nuevo_parametros",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void enviarRespuesta(Long id){
		
		CptObs cptObs = em.find(CptObs.class, id);
		if (cptObs != null){
			if(cptObs.getRespuestaOee()!= null && !cptObs.getRespuestaOee().trim().equals("")){
				cptObs.setActivo(false);
				em.merge(cptObs);
				em.flush();
				
			}else{
				statusMessages.clear();
				statusMessages.add(Severity.WARN, "Existen Campos Vacios.. verifique");
				
			}
		}
	
		this.enviarMailRespuesta(cptObs.getCpt().getIdCpt());
		
	}
	
public void enviarObservacion(Long id){
		
		CptObs cptObs = em.find(CptObs.class, id);
		if (cptObs != null){
			if(cptObs.getObservacionSfp()!= null && !cptObs.getObservacionSfp().trim().equals("")){
				cptObs.setEnvioObservacion(true);
				em.merge(cptObs);
				em.flush();
			}else{
				statusMessages.clear();
				statusMessages.add(Severity.WARN, "Existen Campos Vacios.. verifique");
				
			}
		}
		
		this.enviarMailObservacion(cptObs.getCpt().getIdCpt());
		
	}
	
	
	public boolean isEstadoParaHomologacion(String estado){
		if (estado == null)
			return false;
		else
			return estado.equals(Cpt.ESTADO_PARA_HOMOLOGACION);
	}
	
	public boolean deshabilitarGestionarHomologacion(String estado){
		if (estado == null)
			return true;
		else
			return estado.equals("");
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Integer getNroEspecifico() {
		return nroEspecifico;
	}

	public void setNroEspecifico(Integer nroEspecifico) {
		this.nroEspecifico = nroEspecifico;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<Cpt> getListaCpt() {
		return listaCpt;
	}

	public void setListaCpt(List<Cpt> listaCpt) {
		this.listaCpt = listaCpt;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public Long getIdConfiguracionUoCab() {
		return idConfiguracionUoCab;
	}

	public void setIdConfiguracionUoCab(Long idConfiguracionUoCab) {
		this.idConfiguracionUoCab = idConfiguracionUoCab;
	}

	public Boolean getEsHomologador() {
		return esHomologador;
	}

	public void setEsHomologador(Boolean esHomologador) {
		this.esHomologador = esHomologador;
	}

	

}
