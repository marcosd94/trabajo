package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.dto.DetalleMatrizDocumentalDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.MatrizDocumentalDet;
import py.com.excelsis.sicca.entity.MatrizDocumentalEnc;
import py.com.excelsis.sicca.entity.PreguntasFrecuentes;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.MatrizDocumentalEncHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("admDetMatrizDocumentalFormController")
@Scope(ScopeType.PAGE)
public class AdmDetMatrizDocumentalFormController implements Serializable {

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	MatrizDocumentalEncHome matrizDocumentalEncHome;
	MatrizDocumentalEnc cabecera;
	String mensaje;

	List<MatrizDocumentalDet> listaDetalles = new ArrayList<MatrizDocumentalDet>();
	List<DetalleMatrizDocumentalDTO> listaDTO = new ArrayList<DetalleMatrizDocumentalDTO>();

	public void init() {
		cabecera = matrizDocumentalEncHome.getInstance();
		buscarDetalles();
		listaDTO = new ArrayList<DetalleMatrizDocumentalDTO>();
		for (MatrizDocumentalDet d : listaDetalles) {
			DetalleMatrizDocumentalDTO dto = new DetalleMatrizDocumentalDTO();
			dto.setActivo(d.isActivo());
			dto.setDatosEspecificos(d.getDatosEspecificos());
			dto.setIdDatosEspecificos(d.getDatosEspecificos().getIdDatosEspecificos());
			dto.setId(d.getIdMatrizDocumentalDet());
			dto.setOrden(d.getNroOrden());
			dto.setBloquear(true);
			listaDTO.add(dto);
		}
		DetalleMatrizDocumentalDTO dto = new DetalleMatrizDocumentalDTO();
		dto.setActivo(true);
		dto.setBloquear(false);
		listaDTO.add(dto);
	}

	public void modificarFilaDet(Integer row) {

		DetalleMatrizDocumentalDTO dto = new DetalleMatrizDocumentalDTO();
		dto = listaDTO.get(row);

		renumerarNroOrden();
		if (verificarOrden(dto.getOrden(), dto.getIdDatosEspecificos())) {
			// dto.setBloquear(true);
			if (dto.getIdDatosEspecificos() != null)
				dto.setDatosEspecificos(em.find(DatosEspecificos.class, dto.getIdDatosEspecificos()));
			listaDTO.set(row, dto);
			if(listaDTO.size() == row + 1) //nuevo elemento
				agregarListaDto(row);
			mensaje = null;
		}

	}

	@SuppressWarnings("unchecked")
	private void buscarDetalles() {
		String sql =
			"select det.* from seleccion.matriz_documental_det det"
				+ " where det. id_matriz_documental_enc = "
				+ matrizDocumentalEncHome.getInstance().getIdMatrizDocumentalEnc();
		listaDetalles = new ArrayList<MatrizDocumentalDet>();
		listaDetalles = em.createNativeQuery(sql, MatrizDocumentalDet.class).getResultList();
	}

	public void agregarListaDto(Integer row) {
		DetalleMatrizDocumentalDTO dto = new DetalleMatrizDocumentalDTO();
		dto = listaDTO.get(row);
		renumerarNroOrden();
		if (verificarOrden(dto.getOrden(), dto.getIdDatosEspecificos())) {
			dto.setBloquear(true);
			if (dto.getIdDatosEspecificos() != null)
				dto.setDatosEspecificos(em.find(DatosEspecificos.class, dto.getIdDatosEspecificos()));
			listaDTO.set(row, dto);
			DetalleMatrizDocumentalDTO d = new DetalleMatrizDocumentalDTO();
			d.setActivo(true);
			d.setBloquear(false);

			listaDTO.add(d);
			mensaje = null;

		}

	}

	private Boolean verificarOrden(Integer orden, Long datoE) {

		if (orden == null || orden <= 0) {
			mensaje = "El nro. de orden ingresado no es válido, verifique";
			return false;
		}
		if (datoE == null) {
			mensaje = "El Tipo de Documento no es válido, verifique";
			return false;
		}
		Integer countOrden = 0;
		Integer countTipoDoc = 0;
		for (DetalleMatrizDocumentalDTO deta : listaDTO) {
			if (deta != null && deta.getOrden() != null && orden != null
				&& deta.getOrden().equals(orden))
				countOrden++;
			if (deta.getId() != null
				&& deta.getDatosEspecificos() != null
				&& datoE != null
				&& deta.getDatosEspecificos().getIdDatosEspecificos().toString().equals(datoE.toString())) {

				countTipoDoc++;
			}

		}
		if (countOrden > 1) {
			mensaje = "No se puede repetir el Número de Orden, verifique";
			return false;
		}
		if (countTipoDoc > 0) {
			mensaje = "No se puede repetir el Tipo de Documento, verifique";
			return false;
		}

		mensaje = "";
		return true;
	}

	private void renumerarNroOrden() {
		if (listaDTO != null) {
			int contador = 1;
			for (DetalleMatrizDocumentalDTO o : listaDTO) {
				o.setOrden(contador++);
			}
		}
	}

	public void eliminarListaDto(Integer row) {
		DetalleMatrizDocumentalDTO dto = new DetalleMatrizDocumentalDTO();
		dto = listaDTO.get(row);
		listaDTO.remove(dto);
		renumerarNroOrden();
	}

	private Boolean precond() {
		Map<String, String> cacheTipoDoc = new HashMap<String, String>();
		for (int i = 0; i < listaDTO.size() - 1; i++) {
			DetalleMatrizDocumentalDTO d = new DetalleMatrizDocumentalDTO();
			d = listaDTO.get(i);
			if (d.getDatosEspecificos() == null) {
				statusMessages.add(Severity.ERROR, "Debe cargar todos los Tipo de Documento (para Nro. Orden "
					+ d.getOrden() + ")");
				return false;
			}
			if (cacheTipoDoc.get(d.getDatosEspecificos().getIdDatosEspecificos().toString()) != null) {
				mensaje = "No se puede repetir el Tipo de Documento, verifique";
				return false;
			} else {
				cacheTipoDoc.put(d.getDatosEspecificos().getIdDatosEspecificos().toString(), d.getDatosEspecificos().getIdDatosEspecificos().toString());
			}
		}
		return true;
	}

	public String save() {
		try {
			if (precond()) {
				for (MatrizDocumentalDet o : listaDetalles) {
					Boolean esta = false;
					for (DetalleMatrizDocumentalDTO d : listaDTO) {
						if (o.getIdMatrizDocumentalDet().equals(d.getId()))
							esta = true;
					}
					if (!esta)
						em.remove(o);
				}
				em.flush();
				for (int i = 0; i < listaDTO.size() - 1; i++) {

					DetalleMatrizDocumentalDTO d = new DetalleMatrizDocumentalDTO();
					d = listaDTO.get(i);
					if (d.getDatosEspecificos() == null) {
						statusMessages.add(Severity.ERROR, "Debe cargar todos los Tipo de Documento");

					}
					if (d.getId() != null) {
						MatrizDocumentalDet matriz = new MatrizDocumentalDet();
						matriz = em.find(MatrizDocumentalDet.class, d.getId());
						matriz.setUsuMod(usuarioLogueado.getCodigoUsuario());
						matriz.setFechaMod(new Date());
						matriz.setNroOrden(d.getOrden());
						matriz.setDatosEspecificos(d.getDatosEspecificos());
						matriz.setActivo(d.getActivo());
						em.merge(matriz);
					} else {
						MatrizDocumentalDet matriz = new MatrizDocumentalDet();
						matriz.setActivo(d.getActivo());
						matriz.setDatosEspecificos(d.getDatosEspecificos());
						matriz.setFechaAlta(new Date());
						matriz.setMatrizDocumentalEnc(cabecera);
						matriz.setNroOrden(d.getOrden());
						matriz.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(matriz);
					}
					em.flush();
				}

				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				return "ok";
			}
			return "fail";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public MatrizDocumentalEnc getCabecera() {
		return cabecera;
	}

	public void setCabecera(MatrizDocumentalEnc cabecera) {
		this.cabecera = cabecera;
	}

	public List<MatrizDocumentalDet> getListaDetalles() {
		return listaDetalles;
	}

	public void setListaDetalles(List<MatrizDocumentalDet> listaDetalles) {
		this.listaDetalles = listaDetalles;
	}

	public List<DetalleMatrizDocumentalDTO> getListaDTO() {
		return listaDTO;
	}

	public void setListaDTO(List<DetalleMatrizDocumentalDTO> listaDTO) {
		this.listaDTO = listaDTO;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
