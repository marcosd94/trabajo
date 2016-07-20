package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import enums.TiposDatos;
import py.com.excelsis.sicca.dto.ComisionSeleccionCabDTO;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ComisionSeleccionCabList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("comisionSeleccionCabListFormController")
public class ComisionSeleccionCabListFormController implements Serializable {

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

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ComisionSeleccionCabList comisionSeleccionCabList;

	private Long idConcursoPuestoAgr;// recibe del CU que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;// enviado por el CU
	private String descripcion;
	private List<ComisionSeleccionCabDTO> comisionSeleccionCabDTOList;
	private Boolean cerradoGrupo = false;
	private Integer tam = 0;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String from;

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	public void init() {
		concurso = em.find(Concurso.class, idConcursoPuestoAgr);
		/* Incidencia 1014 */
		validarOee();
		/**/
		try {

			findEntidades();// Trae las entidades segun el grupo que se envio
			searchAll();
			/**
			 * PARA LA INCIENCIA 0000812 MOD. DE cu
			 */

			/**
			 * MODIFICACION DE LA INCIDENCIA 0001448 SE MODIFICO EN VES DE FINALIZADO ES 'CERRADO GRUPOS’
			 * */
			if (concurso.getEstado().intValue() == cerradoGrupo())
				cerradoGrupo = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void search() {

		comisionSeleccionCabDTOList = new ArrayList<ComisionSeleccionCabDTO>();
		comisionSeleccionCabList.setIdConcurso(concurso.getIdConcurso());
		comisionSeleccionCabList.getComisionSeleccionCab().setDescripcion(descripcion);
		List<ComisionSeleccionCab> cabLists = comisionSeleccionCabList.listaResultadosCU165();
		tam = cabLists.size();
		for (int i = 0; i < cabLists.size(); i++) {
			List<ComisionSeleccionDet> dets =
				em.createQuery("select d from ComisionSeleccionDet d "
					+ " where d.comisionSeleccionCab.idComisionSel=:id ").setParameter("id", cabLists.get(i).getIdComisionSel()).getResultList();
			ComisionSeleccionCabDTO cabDTO = new ComisionSeleccionCabDTO();
			cabDTO.setComisionSeleccionCab(cabLists.get(i));
			cabDTO.setMostrarAcciones(true);
			cabDTO.setMostrarCab(true);
			cabDTO.setMostrarDet(false);
			comisionSeleccionCabDTOList.add(cabDTO);
			for (int j = 0; j < dets.size(); j++) {
				cabDTO = new ComisionSeleccionCabDTO();
				cabDTO.setComisionSeleccionDet(dets.get(j));
				cabDTO.setMostrarAcciones(false);
				cabDTO.setMostrarCab(false);
				cabDTO.setMostrarDet(true);
				comisionSeleccionCabDTOList.add(cabDTO);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void searchAll() {
		descripcion = null;
		comisionSeleccionCabDTOList = new ArrayList<ComisionSeleccionCabDTO>();
		comisionSeleccionCabList.setIdConcurso(idConcursoPuestoAgr);
		List<ComisionSeleccionCab> cabLists = comisionSeleccionCabList.limpiarCU165();
		tam = cabLists.size();
		for (int i = 0; i < cabLists.size(); i++) {
			List<ComisionSeleccionDet> dets =
				em.createQuery("select d from ComisionSeleccionDet d "
					+ " where d.comisionSeleccionCab.idComisionSel=:id and d.activo= true").setParameter("id", cabLists.get(i).getIdComisionSel()).getResultList();
			ComisionSeleccionCabDTO cabDTO = new ComisionSeleccionCabDTO();
			cabDTO.setComisionSeleccionCab(cabLists.get(i));
			cabDTO.setMostrarAcciones(true);
			cabDTO.setMostrarCab(true);
			cabDTO.setMostrarDet(false);
			comisionSeleccionCabDTOList.add(cabDTO);
			for (int j = 0; j < dets.size(); j++) {
				cabDTO = new ComisionSeleccionCabDTO();
				cabDTO.setComisionSeleccionDet(dets.get(j));
				cabDTO.setMostrarAcciones(false);
				cabDTO.setMostrarCab(false);
				cabDTO.setMostrarDet(true);
				comisionSeleccionCabDTOList.add(cabDTO);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab =
			em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad =
				em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n " + " where n.aniAniopre ="
					+ sinEntidad.getAniAniopre() + " and n.nenCodigo=" + sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}

	public void print() throws SQLException {
		Connection conn =null; 
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				throw new Exception(SeamResourceBundle.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
			}else{
				 conn = JpaResourceBean.getConnection();
				 JasperReportUtils.respondPDF("RPT_CU166", obtenerMapaParametros(), false, conn, usuarioLogueado);
				 conn.close();
			} 

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.close();
		}

	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if(descripcion!=null && !descripcion.trim().equals("")){
			if (!sufc.validarInput(descripcion, TiposDatos.STRING.getValor(), null)) {
				return null;
			}
		}
		
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("idCocurso", concurso.getIdConcurso());
		if(descripcion==null)
			param.put("comision","%%");
		else
			param.put("comision","%"+descripcion.toLowerCase()+"%");
		
		param.put("entidad", sinEntidad.getEntNombre());
		param.put("uniO", configuracionUoCab.getDenominacionUnidad());
		param.put("concurso", concurso.getNombre());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	@SuppressWarnings("unchecked")
	private Integer cerradoGrupo() {
		List<Referencias> ref =
			em.createQuery(" Select r from Referencias r "
				+ " where r.dominio like 'ESTADOS_CONCURSO' and r.descAbrev like 'CERRADO GRUPOS'").getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum().intValue();
	}
	
	
	
public boolean tieneDocAdjunto(Long idConcurso){
		
		String sql  ="select doc.* from general.documentos doc "
					+ " where nombre_tabla = 'ComisionSeleccionCab'"
					+ " and id_concurso  = " + idConcurso;
		
		List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
		
		return docs.isEmpty();
		
	}
	
	
	public void imprimirDocumentoComision(Long idComisionSel) {
		
		ComisionSeleccionCab cab = em.find(ComisionSeleccionCab.class, idComisionSel);
		
		Long idConcurso = cab.getConcurso().getIdConcurso();
		

		Long id  = null;
	
		try{
			if(cab.getDocumentos() != null)
				id = cab.getDocumentos().getIdDocumento();
			else{
				String sql  ="select doc.* from general.documentos doc "
						+ " where nombre_tabla = 'ComisionSeleccionCab'"
						+ " and id_concurso  = " + idConcurso
						+" order by id_documento desc";
			
			
				List <Documentos> docs = em.createNativeQuery(sql,Documentos.class).getResultList();
			
				id = docs.get(0).getIdDocumento();
			}
				
			String usuario = "Invitado";
			
			if (usuarioLogueado != null )
				usuario = usuarioLogueado.getCodigoUsuario();
			
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id,usuario);
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
					
	}

	// GETTERS Y SETTERS
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
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

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<ComisionSeleccionCabDTO> getComisionSeleccionCabDTOList() {
		return comisionSeleccionCabDTOList;
	}

	public void setComisionSeleccionCabDTOList(List<ComisionSeleccionCabDTO> comisionSeleccionCabDTOList) {
		this.comisionSeleccionCabDTOList = comisionSeleccionCabDTOList;
	}

	public Boolean getCerradoGrupo() {
		return cerradoGrupo;
	}

	public void setCerradoGrupo(Boolean cerradoGrupo) {
		this.cerradoGrupo = cerradoGrupo;
	}

	public Integer getTam() {
		return tam;
	}

	public void setTam(Integer tam) {
		this.tam = tam;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
