package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;
import enums.TipoPostulacionEnums;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ExcepcionHome;
import py.com.excelsis.sicca.seleccion.session.PostulacionList;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.form.ConcursoListFormController;
import py.com.excelsis.sicca.session.form.Tab6VistaPreliminarFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.util.CU422DTO;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("buscarMisConcursosformController")
public class BuscarMisConcursosformController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PostulacionList postulacionList;
	@In(create = true)
	ConcursoListFormController concursoListFormController;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;
	@In(create = true)
	CancelacionPostulanteFormController cancelacionPostulanteFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	ExcepcionHome excepcionHome;
	private Long idTipoConcurso;
	private String entidad;
	private String puesto;
	private String motivo;
	private Long idPostulacion;
	private List<Postulacion> postulacions = new ArrayList<Postulacion>();
	private ArrayList<Integer> estadoDesierto;
	private TipoPostulacionEnums postulacionEnums=TipoPostulacionEnums.Oniline;
	/**
	 * 
	 */
	private List<CU422DTO> lista = new ArrayList<CU422DTO>();

	public void init() {
		estadoDesierto=estadoDesierto();
		search();
		idPostulacion = null;
		concursoListFormController.init();

	}

	public void registrarSele(Long id) {
		concursoListFormController.registrarSele(id);
		lista = new ArrayList<CU422DTO>();
		lista = concursoListFormController.imprimirHistorial("402");
	}

	public void imprimirCv(Postulacion post) {
		tab6VistaPreliminarFormController.setFromCU("402");
		tab6VistaPreliminarFormController.setIdPostulacion(post.getIdPostulacion());
		tab6VistaPreliminarFormController.pdf();
	}

	public void search() {
		postulacionList.setEntidad(entidad);
		postulacionList.setGrupo(puesto);
		postulacionList.setIdTipoConcurso(idTipoConcurso);
		postulacionList.setIdPersona(usuarioLogueado.getPersona().getIdPersona());
		postulacionList.setEstado(null);
		if(postulacionEnums.getValor().equals("O"))
			postulacions = postulacionList.listaResultados();
		else
			postulacions=postulacionList.listaResultadosCarpeta();
	}

	public void searchAll() {
		idTipoConcurso = null;
		entidad = null;
		puesto = null;
		postulacionList.setIdPersona(usuarioLogueado.getPersona().getIdPersona());
		postulacionEnums=TipoPostulacionEnums.Oniline;
		postulacions = postulacionList.limpiarXusuario();
		search();
	}

	public void cancelar(Long id) {
		idPostulacion = id;
		Postulacion aux = em.find(Postulacion.class, idPostulacion);
		motivo = aux.getObsCancelacion();

	}
	
	public String concatenarTipoConcursoYPcdYAdReferendum(Concurso concurso, String columna){
		String retorno = concurso.getDatosEspecificosTipoConc().getDescripcion();
		if(concurso.getPcd() != null)
			retorno	+=(concurso.getPcd()?" - " + esPcd(concurso.getPcd(),columna):"" );
		if(concurso.getAdReferendum() != null)
			retorno	+=(concurso.getAdReferendum()?" - " + esAdReferendum(concurso.getAdReferendum(),columna):"" );
		return retorno;
	}
	
	
	public String esPcd(Boolean pcd,String columna){
		
		if (pcd  && columna.equals("NUEVO"))
			return " PcD";
		else if (pcd && columna.equals("TIPO_CONCURSO"))
			return "Para Personas con Discapacidad";
		else 
			return "";
		
	}
	
	public String esAdReferendum(Boolean adReferendum,String columna){
		
		if (adReferendum != null && adReferendum  && columna.equals("NUEVO"))
			return " Ad-Referendum";
		else if (adReferendum && columna.equals("TIPO_CONCURSO"))
			return " Ad-Referendum";
		else 
			return "";
		
	}

	/**
	 * El enlace ‘cancelar postulación’ se deshabilita también
	 *  cuando el grupo tiene alguno de los siguientes estados:
	 *  INCIDENCIA 0001435
	 * */
	@SuppressWarnings("unchecked")
	public Boolean habilitarCancelar(Long idPostulacion) {
		if(idPostulacion!=null){
			
			String sql =
				"select agr.*  " + "from seleccion.referencias r, seleccion.postulacion postulacion "
					+ "join seleccion.concurso_puesto_agr agr "
					+ "on agr.id_concurso_puesto_agr = postulacion.id_concurso_puesto_agr "
					+ "where r.dominio = 'ESTADOS_GRUPO'   " + "and " +
					" (r.desc_abrev ='CON RESOLUCION ADJUDICACION'  "
				  + "or r.desc_abrev ='CONTRATADO'  "
				  + "or r.desc_abrev ='PERMANENTE D12'  "
				  + "or r.desc_abrev ='PERMANENTE N12'  "
				  + "or r.desc_abrev ='CON DECRETO' "
				  + "or r.desc_abrev ='FIRMADO DECRETO PRESIDENCIAL'  "
				   + "or r.desc_abrev ='CON RESOLUCION NOMBRAMIENTO'  "
					+ "or r.desc_abrev ='FIRMADO NOMBRAMIENTO' ) "
					 + "and r.activo is true  "
					+ "and postulacion.id_postulacion = " + idPostulacion
					+ " and r.valor_num = agr.estado";
			List<ConcursoPuestoAgr> listaC = new ArrayList<ConcursoPuestoAgr>();
			listaC = em.createNativeQuery(sql, ConcursoPuestoAgr.class).getResultList();
			if (listaC != null && listaC.size() > 0)
				return false;
//			for(FechasGrupoPuesto fgp : listaC.get(0).getFechasGrupoPuestos()) {
//				if( fgp.getFechaRecepcionHasta().before(new Date()) )
//					return false;
//			}
			return true;
		
		}
		return false;
	}

	public void confCancelar() {

		if (motivo == null || motivo.trim().equals("")) {
			statusMessages.add(Severity.ERROR, "Debe ingresar un motivo de cancelación");
			return;
		}
		Postulacion aux = em.find(Postulacion.class, idPostulacion);
		aux.setObsCancelacion(motivo);
		if(aux.getConcursoPuestoAgr().getEstado().intValue() < 20)//20 es el valor_num de LISTA LARGA
			aux.setActivo(false);
		
		aux.setEstadoPostulacion(Postulacion.ESTADO_POSTULACION_CANCELADO);
		aux.setUsuCancelacion(usuarioLogueado.getCodigoUsuario());
		aux.setFechaCancelacion(new Date());
		aux.setFechaModif(new Date());
		aux.setUsuModif(usuarioLogueado.getCodigoUsuario());
		aux.setDatosEspecificosTipoCanc(tipoCancelacion());
		em.merge(aux);
		
		enviarEmails(aux);
		datosIntegrantes(aux.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		em.flush();
		searchAll();
		
		
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

	}
	@SuppressWarnings("unchecked")
	private void datosIntegrantes(Long idGrupo){
		List<ComisionGrupo> comGrupos=em.createQuery("Select d from ComisionGrupo d " +
				" where d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo").setParameter("idGrupo", idGrupo).getResultList();
		for (int i = 0; i < comGrupos.size(); i++) {
			ComisionGrupo comisionGrupo=comGrupos.get(i);
			List<ComisionSeleccionDet> seleccionDets=em.createQuery("Select det from ComisionSeleccionDet det " +
					" where det.comisionSeleccionCab.idComisionSel=:idCab").setParameter("idCab",comisionGrupo.getComisionSeleccionCab().getIdComisionSel()).getResultList();
			for (int j = 0; j < seleccionDets.size(); j++) {
				if(seleccionDets.get(j).getPersona()!=null)
				enviarEmailPersona(em.find(Persona.class, seleccionDets.get(j).getPersona().getIdPersona()));
			}
		}
		
	}
	
	private DatosEspecificos tipoCancelacion(){
		try {
			DatosEspecificos d =(DatosEspecificos)em.createQuery("Select d from DatosEspecificos d " +
					" where d.datosGenerales.descripcion='TIPOS DE CANCELACION POSTULANTE' " +
					" and d.descripcion ='ON-LINE'").getSingleResult();
			return d;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void enviarEmails(Postulacion post) {
		PersonaPostulante persona = new PersonaPostulante();
		persona = post.getPersonaPostulante();
		String dirEmail = persona.getEMail();

		String asunto = null;
		String x =
			post.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDescripcionCorta();
		if (x != null && !x.trim().isEmpty())
			asunto = x;
		else
			asunto =
				post.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDenominacionUnidad();

		asunto = asunto + " - Cancelaci\u00F3n de Postulaci\u00F3n";
		String texto = "";

		try {
			texto =
				texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ persona.getNombres()
					+ " "
					+ persona.getApellidos()
					+ "</p> "
					+ "</b> <p align=\"justify\"> Se ha registrado la cancelaci&oacute;n de su postulaci&oacute;n para el Concurso: <b></p> "
					+ " <p align=\"center\"> <b>"
					+ post.getConcursoPuestoAgr().getConcurso().getNumero()
					+ "/"
					+ post.getConcursoPuestoAgr().getConcurso().getAnhio()
					+ " de "
					+ post.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDescripcionCorta()
					+ " - "
					+ post.getConcursoPuestoAgr().getConcurso().getNombre()
					+ "</b> </p> "
					+ " <p align=\"center\"> <b>"
					+ post.getConcursoPuestoAgr().getDescripcionGrupo()
					+ "</b> </p> "
					+ "<p align=\"justify\"> Le agradecemos el inter&eacute;s que ha mostrado para este concurso, y deseamos poder contar con usted para nuestros pr&oacute;ximos concursos. </p> "
					+ "<p> Atentamente,</p> "
					+ "<p>"
					+ post.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getDenominacionUnidad()
					+ "</p> " + "<p> Para mayor informaci&oacute;n comunicarse con: "
					+ informacion(post) + "</p> ";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);
			System.out.println("EL MAIL HA SIDO ENVIADO....");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public void enviarEmailPersona(Persona persona) {
		Postulacion aux = em.find(Postulacion.class, idPostulacion);
		ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class, aux.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		PersonaPostulante personaPostulante=em.find(PersonaPostulante.class, aux.getPersonaPostulante().getIdPersonaPostulante());
		String dirEmail =persona.getEMail();
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		Date fechaActual= new Date();
		String asunto = "Cancelaci\u00F3n de Postulaci\u00F3n on-line";
		
		
		
		String texto = "";

		try {
			texto =
				texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ persona.getNombres()
					+ " "
					+ persona.getApellidos()
					+ " </b> </p> "
					+ " <p align=\"justify\"> Le comunicamos que el Postulante: </p> "
					+ "<p align=\"justify\"> <b>"+ personaPostulante.getUsuAlta()+"</b>,</p>"
					+ "<p > <b>"+personaPostulante.getNombreCompleto()+"</b> con  	"
					+ " documento de identidad n&uacute;mero <b>"+personaPostulante.getDocumentoIdentidad()+"</b> y </p> " +
					  "<p> pa&iacute;s de expedici&oacute;n del documento  <b>" +personaPostulante.getPaisExpedicionDoc().getDescripcion()+"</b>, ha cancelado su postulaci&oacute;n  al </p> "
					+ "<p> Concurso: </p>"
					+ "<p align=\"center\"> <b>"
					+ agr.getConcurso().getNumero()
					+ "/"
					+ agr.getConcurso().getAnhio()
					+ " de "
					+ agr.getConcurso().getConfiguracionUoCab().getDescripcionCorta()
					+ " - "
					+ agr.getConcurso().getNombre()
					+ "</b> </p> "
					+ " <p align=\"center\">  "+agr.getDescripcionGrupo()+"</p> "
					+" <p align=\"justify\"> Accediendo a su cuenta en el Portal, en fecha: "+sdf.format(fechaActual)+"</p>"
					+ " <p> </p>" + " <p> </p>" + " <p> </p>" + "<p align=\"justify\">&nbsp; </p>"
					+ "<strong>Atentamente</strong>"
					+ " <p><strong> "+agr.getConcurso().getConfiguracionUoCab().getDenominacionUnidad()+"</strong></p>";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);
			System.out.println("EL MAIL HA SIDO ENVIADO....");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

	@SuppressWarnings("unchecked")
	private String informacion(Postulacion post) {
		String resultado = "";
		String sql1 =
			"select pres.* " + "from seleccion.present_aclarac_doc pres "
				+ "where pres.id_concurso_puesto_agr = "
				+ post.getConcursoPuestoAgr().getIdConcursoPuestoAgr();

		String sql2 =
			"select pres.* " + "from seleccion.present_aclarac_doc pres "
				+ "where pres.id_concurso = "
				+ post.getConcursoPuestoAgr().getConcurso().getIdConcurso();

		List<PresentAclaracDoc> listaPres = new ArrayList<PresentAclaracDoc>();
		listaPres = em.createNativeQuery(sql1, PresentAclaracDoc.class).getResultList();
		if (listaPres.size() == 0) {
			listaPres = em.createNativeQuery(sql2, PresentAclaracDoc.class).getResultList();
		}
		for (PresentAclaracDoc pr : listaPres) {
			resultado = resultado + " - " + pr.getEmail();
		}
		return resultado;

	}
	public boolean habCancelarPos(Long idGrup){
		if(idGrup!=null){
			ConcursoPuestoAgr agr= em.find(ConcursoPuestoAgr.class, idGrup);
			if(agr.getEstado()==null)
				return false;
			if(estadoDesierto.contains(agr.getEstado().intValue()))
				return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	private ArrayList<Integer> estadoDesierto(){
		ArrayList<Integer> estados_num  = new ArrayList<Integer>();
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_GRUPO' and "
				+ "(r.descAbrev = 'RECIBIR POSTULACION'"
				+ " or r.descAbrev = 'PUBLICADO' or r.descAbrev = 'PUBLICACION')").getResultList();
		if(referencias.isEmpty())
			estados_num.add(0);
		else
			for(Referencias r : referencias)
				estados_num.add(r.getValorNum());
		 return estados_num;
	}
	
	// GETTERS Y SETTERS
	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public List<Postulacion> getPostulacions() {
		return postulacions;
	}

	public void setPostulacions(List<Postulacion> postulacions) {
		this.postulacions = postulacions;
	}

	public List<CU422DTO> getLista() {
		return lista;
	}

	public void setLista(List<CU422DTO> lista) {
		this.lista = lista;
	}

	public TipoPostulacionEnums getPostulacionEnums() {
		return postulacionEnums;
	}

	public void setPostulacionEnums(TipoPostulacionEnums postulacionEnums) {
		this.postulacionEnums = postulacionEnums;
	}

}
