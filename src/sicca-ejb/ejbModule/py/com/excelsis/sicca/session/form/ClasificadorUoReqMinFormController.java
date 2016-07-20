package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import com.sun.mail.imap.protocol.Status;

import py.com.excelsis.sicca.dto.RequisitosMinDTO;
import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.ClasificadorUoRequisito;
import py.com.excelsis.sicca.entity.RequisitoMinimoCuo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ClasificadorUoHome;
import py.com.excelsis.sicca.session.ClasificadorUoRequisitoHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("clasificadorUoReqMinFormController")
@Scope(ScopeType.PAGE)
public class ClasificadorUoReqMinFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2881610119430601586L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	ClasificadorUoRequisitoHome clasificadorUoRequisitoHome;

	@In(create = true)
	ClasificadorUoHome clasificadorUoHome;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	SeguridadUtilFormController seguridadFC;

	ClasificadorUoRequisito clasificadorUoRequisito;

	private Long idClasificadorUo;
	private ClasificadorUo clasificadorUo;
	private List<RequisitosMinDTO> listaDto = new ArrayList<RequisitosMinDTO>();
	private List<ClasificadorUoRequisito> listaClasificadorUo =
		new ArrayList<ClasificadorUoRequisito>();
	private List<RequisitoMinimoCuo> listaRequisitoMin = new ArrayList<RequisitoMinimoCuo>();
	private String mensaje;

	public void init() {
		clasificadorUo = clasificadorUoHome.getInstance();
		listaClasificadorUo = new ArrayList<ClasificadorUoRequisito>();
		buscarClasificadores();
		listaRequisitoMin = new ArrayList<RequisitoMinimoCuo>();
		buscarRequisitos();
		listaDto = new ArrayList<RequisitosMinDTO>();
		agregarAdto();

	}

	private void agregarAdto() {
		for (ClasificadorUoRequisito clasificador : listaClasificadorUo) {
			if (clasificador.getActivo()) {

				RequisitosMinDTO dto = new RequisitosMinDTO();
				dto.setClasificadorUo(clasificadorUo);
				dto.setDescripcion(clasificador.getDescripcion());
				dto.setActivo(clasificador.getActivo());
				dto.setIdClasificadorUoReq(clasificador.getIdClasificadorUoRequisito());
				dto.setRequisitoMinimoCuo(clasificador.getRequisitoMinimoCuo());
				listaDto.add(dto);

				listaRequisitoMin.remove(clasificador.getRequisitoMinimoCuo());
			} else {

				RequisitosMinDTO dto = new RequisitosMinDTO();
				dto.setClasificadorUo(clasificadorUo);
				dto.setDescripcion(clasificador.getDescripcion());
				dto.setActivo(clasificador.getActivo());
				dto.setIdClasificadorUoReq(clasificador.getIdClasificadorUoRequisito());
				dto.setRequisitoMinimoCuo(clasificador.getRequisitoMinimoCuo());
				listaDto.add(dto);
				dto = new RequisitosMinDTO();
				dto.setClasificadorUo(clasificadorUo);
				dto.setRequisitoMinimoCuo(clasificador.getRequisitoMinimoCuo());
				dto.setActivo(true);
				listaDto.add(dto);

				listaRequisitoMin.remove(clasificador.getRequisitoMinimoCuo());
			}
		}
		for (RequisitoMinimoCuo requisito : listaRequisitoMin) {
			RequisitosMinDTO dto = new RequisitosMinDTO();
			dto.setClasificadorUo(clasificadorUo);
			dto.setRequisitoMinimoCuo(requisito);
			dto.setActivo(true);
			listaDto.add(dto);
		}

	}

	private Boolean precondUpdate() throws Exception {
		if (listaDto != null) {
			if (seguridadFC == null) {
				seguridadFC =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			
		}
		return true;
	}

	public String update() {
		try {
			if (!precondUpdate())
				return null;
			for (RequisitosMinDTO dto : listaDto) {
				if (dto.getIdClasificadorUoReq() == null && !dto.getDescripcion().equals("")
					&& dto.getDescripcion() != null) {
					if (!formaParteLista(dto.getRequisitoMinimoCuo())) {
						ClasificadorUoRequisito clasificadorUoRequisito =
							new ClasificadorUoRequisito();
						clasificadorUoRequisito.setActivo(dto.getActivo());
						clasificadorUoRequisito.setClasificadorUo(clasificadorUo);
						clasificadorUoRequisito.setDescripcion(dto.getDescripcion().toUpperCase());
						clasificadorUoRequisito.setRequisitoMinimoCuo(dto.getRequisitoMinimoCuo());
						clasificadorUoRequisito.setFechaAlta(new Date());
						clasificadorUoRequisito.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(clasificadorUoRequisito);
						mensaje = null;
					} else {

						mensaje = "No pueden existir dos requisitos activos al mismo tiempo";
						statusMessages.add(Severity.ERROR, mensaje);
						return null;
					}
				}
				if (dto.getIdClasificadorUoReq() != null && !dto.getDescripcion().equals("")
					&& dto.getDescripcion() != null) {
					ClasificadorUoRequisito clasificadorUoRequisito = new ClasificadorUoRequisito();
					clasificadorUoRequisito =
						em.find(ClasificadorUoRequisito.class, dto.getIdClasificadorUoReq());
					clasificadorUoRequisito.setActivo(dto.getActivo());
					clasificadorUoRequisito.setClasificadorUo(clasificadorUo);
					clasificadorUoRequisito.setDescripcion(dto.getDescripcion().toUpperCase());
					clasificadorUoRequisito.setRequisitoMinimoCuo(dto.getRequisitoMinimoCuo());
					clasificadorUoRequisito.setFechaMod(new Date());
					clasificadorUoRequisito.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(clasificadorUoRequisito);
				}
				em.flush();
			}
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}

		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("ClasificadorUoRequisito_updated"));
		return "updated";
	}

	private Boolean formaParteLista(RequisitoMinimoCuo req) {
		for (RequisitosMinDTO dto : listaDto) {
			if (dto.getIdClasificadorUoReq() != null
				&& dto.getRequisitoMinimoCuo().getIdRequisitoMinimoCuo() == req.getIdRequisitoMinimoCuo()
				&& dto.getActivo())
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void buscarClasificadores() {
		String cadena =
			"select uo.* from planificacion.clasificador_uo_requisito uo"
				+ " where uo.activo is true and uo.id_clasificador_uo = "
				+ clasificadorUo.getIdClasificadorUo();
		listaClasificadorUo =
			em.createNativeQuery(cadena, ClasificadorUoRequisito.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void buscarRequisitos() {
		String cadena =
			"select cuo.* from planificacion.requisito_minimo_cuo cuo where cuo.activo is true order by cuo.orden";
		listaRequisitoMin = em.createNativeQuery(cadena, RequisitoMinimoCuo.class).getResultList();
	}

	public ClasificadorUoRequisito getClasificadorUoRequisito() {
		return clasificadorUoRequisito;
	}

	public void setClasificadorUoRequisito(ClasificadorUoRequisito clasificadorUoRequisito) {
		this.clasificadorUoRequisito = clasificadorUoRequisito;
	}

	public Long getIdClasificadorUo() {
		return idClasificadorUo;
	}

	public void setIdClasificadorUo(Long idClasificadorUo) {
		this.idClasificadorUo = idClasificadorUo;
	}

	public ClasificadorUo getClasificadorUo() {
		return clasificadorUo;
	}

	public void setClasificadorUo(ClasificadorUo clasificadorUo) {
		this.clasificadorUo = clasificadorUo;
	}

	public List<RequisitosMinDTO> getListaDto() {
		return listaDto;
	}

	public void setListaDto(List<RequisitosMinDTO> listaDto) {
		this.listaDto = listaDto;
	}

	public List<ClasificadorUoRequisito> getListaClasificadorUo() {
		return listaClasificadorUo;
	}

	public void setListaClasificadorUo(List<ClasificadorUoRequisito> listaClasificadorUo) {
		this.listaClasificadorUo = listaClasificadorUo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
