package py.com.excelsis.sicca.legajo.session.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.dto.InconstitucionalidadDTO;
import py.com.excelsis.sicca.dto.SumarioDTO;
import py.com.excelsis.sicca.entity.Excepcionados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("procesosJuridicosLegajoFC")
public class ProcesosJuridicosLegajoFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Long idPersona;
	private String texto;
	private String ubicacionInconstitucionalidad;
	private String ubicacionSumario;
	private String ubicacionExcepcionados;
	private Persona persona;
	private List<InconstitucionalidadDTO> listaAccionesDTO = new ArrayList<InconstitucionalidadDTO>();
	private List<SumarioDTO> listaSumarioDTO = new ArrayList<SumarioDTO>();
	private List<Excepcionados> listaExcepcionados = new ArrayList<Excepcionados>();

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		persona = em.find(Persona.class, idPersona);
		listaAccionesDTO = new ArrayList<InconstitucionalidadDTO>();
		buscarAcciones();
		listaSumarioDTO = new ArrayList<SumarioDTO>();
		buscarSumarios();
		cargarExcepcionados();
		cargarUbicaciones();
	}

	private void cargarUbicaciones() {
		Date fechaActual = new Date();
		Integer anhoActual = fechaActual.getYear() + 1900;
		String separador = File.separator;
		ubicacionInconstitucionalidad = separador + "SICCA" + separador
				+ anhoActual + separador + "PJ" + separador + "AI" + separador
				+ idPersona;
		ubicacionSumario = separador + "SICCA" + anhoActual + separador + "PJ"
				+ separador + "SUM" + separador + idPersona;

		ubicacionExcepcionados = separador + "SICCA" + separador + anhoActual
				+ separador + "USUARIO_PORTAL" + separador
				+ persona.getDocumentoIdentidad() + persona.getIdPersona();
	}

	private void buscarAcciones() {
		String sql = "SELECT AC.ID_ACCION_CAB AS ID, CASE WHEN AC.ESTADO = 'P' THEN 'PENDIENTE CON MEDIDA CAUTELAR' WHEN AC.ESTADO = 'A' THEN 'ACUERDO Y SENTENCIA' END AS ESTADO,"
				+ " JURIDICOS.FNC_GET_LEYES(AC.ID_ACCION_CAB) AS LEY, AC.FECHA_ALTA AS FECHA, AC.RESULTADO AS RESULTADO, AC.OBSERVACION AS OBSERVACION"
				+ " FROM JURIDICOS.ACCION_INCONST_CAB AC "
				+ "JOIN GENERAL.PERSONA ON AC.ID_PERSONA = PERSONA.ID_PERSONA "
				+ "WHERE AC.ID_PERSONA = " + idPersona;

		List<Object> rsl = em.createNativeQuery(sql).getResultList();
		if (!rsl.isEmpty()) {
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				InconstitucionalidadDTO dto = new InconstitucionalidadDTO();
				if (record[0] != null)
					dto.setId(Long.parseLong(record[0].toString()));
				if (record[1] != null)
					dto.setEstado((String) record[1]);
				if (record[2] != null)
					dto.setLey((String) record[2]);
				if (record[3] != null)
					dto.setFecha((Date) record[3]);
				if (record[4] != null)
					dto.setAcuerdo((String) record[4]);
				if (record[5] != null)
					dto.setObservacion((String) record[5]);
				listaAccionesDTO.add(dto);
			}
		}
	}

	private void buscarSumarios() {
		String sql = "SELECT SC.ID_SUMARIO_CAB AS ID, OEE.DENOMINACION_UNIDAD as OEE, SC.FECHA_ALTA AS FECHA, "
				+ "SC.NRO_EXP || '/' ||SC.ANIO_EXP AS NRO_INTERNO, "
				+ "JURIDICOS.FNC_GET_SUMARIOS(SC.ID_SUMARIO_CAB ) FALTAS, "
				+ "CASE WHEN SC.ESTADO_JUEZ = 'SO' THEN 'SOBRESEIMIENTO'  "
				+ "WHEN SC.ESTADO_JUEZ = 'SA' THEN 'SANCION' ||' - '|| S.DESCRIPCION ||' - '|| SC.TIEMPO_J ||' '||  "
				+ "CASE WHEN SC.EXPRESADO_J = 'D' THEN 'DÍA/S' WHEN SC.EXPRESADO_J = 'M' THEN 'MES/ES' WHEN SC.EXPRESADO_J = 'A' THEN 'AÑO/S' END "
				+ "WHEN SC.ESTADO_JUEZ = 'AR' THEN 'ARCHIVO' END as recomendacion_juez,  "
				+ "CASE WHEN SC.ESTADO = 'EC' THEN 'EN CURSO' WHEN SC.ESTADO = 'RJ' THEN 'CON RESOLUCION DEL JUEZ' "
				+ "WHEN SC.ESTADO = 'SO' THEN 'SOBRESEIMIENTO' WHEN SC.ESTADO = 'SA' THEN 'SANCION'  "
				+ "WHEN SC.ESTADO = 'AR' THEN 'ARCHIVO' END AS ESTADO_SUMARIO, "
				+ "CASE WHEN SC.ESTADO = 'SO' THEN 'SOBRESEIMIENTO - MAI' "
				+ "WHEN SC.ESTADO = 'SA' THEN 'SANCION MAI - ' || SD.DESCRIPCION ||' '|| CASE WHEN SD.INHABILITA = TRUE THEN SC.TIEMPO_M ||' '||  "
				+ "CASE WHEN SC.EXPRESADO_M = 'D' THEN 'DÍA/S' WHEN SC.EXPRESADO_M = 'M' THEN 'MES/ES' WHEN SC.EXPRESADO_M = 'A' THEN 'AÑO/S' END ELSE  SD.DESCRIPCION END "
				+ "WHEN SC.ESTADO = 'AR' THEN 'ARCHIVO - MAI' END as decision_mai, SC.OBS_M AS OBS_MAI	"
				+ "FROM JURIDICOS.SUMARIO_CAB SC "
				+ "LEFT JOIN JURIDICOS.SANCION S ON S.ID_SANCION = SC.ID_SANCION_J "
				+ "LEFT JOIN JURIDICOS.SANCION SD ON SD.ID_SANCION = SC.ID_SANCION_M "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON OEE.ID_CONFIGURACION_UO = SC.ID_CONFIGURACION_UO "

				+ "WHERE SC.ID_PERSONA = " + idPersona

				+ " ORDER BY SC.FECHA_ALTA";

		List<Object> rsl = em.createNativeQuery(sql).getResultList();
		if (!rsl.isEmpty()) {
			for (Object obj : (List<Object>) rsl) {
				Object[] record = (Object[]) obj;
				SumarioDTO dto = new SumarioDTO();
				if (record[0] != null)
					dto.setId(Long.parseLong(record[0].toString()));
				if (record[1] != null)
					dto.setOee((String) record[1]);
				if (record[2] != null)
					dto.setFecha((Date) record[2]);
				if (record[3] != null)
					dto.setNroInterno((String) record[3]);
				if (record[4] != null)
					dto.setFaltas((String) record[4]);
				if (record[5] != null)
					dto.setRecomendacionJuez((String) record[5]);
				if (record[6] != null)
					dto.setEstado((String) record[6]);
				if (record[7] != null)
					dto.setDecisionMai((String) record[7]);
				if (record[8] != null)
					dto.setObsMai((String) record[8]);
				listaSumarioDTO.add(dto);
			}
		}
	}

	private void cargarExcepcionados() {
		listaExcepcionados = new ArrayList<Excepcionados>();
		String consulta = "select e from Excepcionados e " + " where " +
		 "e.persona.idPersona = "+idPersona + " and e.activo is true ";
		listaExcepcionados = em.createQuery(consulta).getResultList();
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<InconstitucionalidadDTO> getListaAccionesDTO() {
		return listaAccionesDTO;
	}

	public void setListaAccionesDTO(
			List<InconstitucionalidadDTO> listaAccionesDTO) {
		this.listaAccionesDTO = listaAccionesDTO;
	}

	public List<SumarioDTO> getListaSumarioDTO() {
		return listaSumarioDTO;
	}

	public void setListaSumarioDTO(List<SumarioDTO> listaSumarioDTO) {
		this.listaSumarioDTO = listaSumarioDTO;
	}

	public List<Excepcionados> getListaExcepcionados() {
		return listaExcepcionados;
	}

	public void setListaExcepcionados(List<Excepcionados> listaExcepcionados) {
		this.listaExcepcionados = listaExcepcionados;
	}

	public String getUbicacionInconstitucionalidad() {
		return ubicacionInconstitucionalidad;
	}

	public void setUbicacionInconstitucionalidad(
			String ubicacionInconstitucionalidad) {
		this.ubicacionInconstitucionalidad = ubicacionInconstitucionalidad;
	}

	public String getUbicacionSumario() {
		return ubicacionSumario;
	}

	public void setUbicacionSumario(String ubicacionSumario) {
		this.ubicacionSumario = ubicacionSumario;
	}

	public String getUbicacionExcepcionados() {
		return ubicacionExcepcionados;
	}

	public void setUbicacionExcepcionados(String ubicacionExcepcionados) {
		this.ubicacionExcepcionados = ubicacionExcepcionados;
	}

}
