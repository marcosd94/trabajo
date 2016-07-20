package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sun.org.apache.bcel.internal.generic.NEW;

import enums.TiposDatos;

import py.com.excelsis.sicca.dto.ResutadosEncuestasPlantillasDTO;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PlantillaConfigurada;
import py.com.excelsis.sicca.entity.PreguntaConfigurada;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RespuestaMultipleConfigurada;
import py.com.excelsis.sicca.entity.ResultadoEncuesta;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarResultadoEncuestaFC")
public class CargarResultadoEncuestaFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Capacitaciones capacitaciones = new Capacitaciones();
	private Long idCapacitacion;
	private String tipoCapac = null;
	private String nombreEncuesta = null;
	private Long cntEvalTotal = null;
	private Long cntEvalActual = null;
	private PlantillaConfigurada plantillaConfigurada = new PlantillaConfigurada();
	private boolean habEncuesta;
	private Long valor;
	private List<PreguntaConfigurada> preguntaConfiguradasList =
		new ArrayList<PreguntaConfigurada>();
	private List<ResutadosEncuestasPlantillasDTO> encuestasPlantillasMultDTOs =
		new ArrayList<ResutadosEncuestasPlantillasDTO>();
	private List<ResutadosEncuestasPlantillasDTO> encuestasPlantillasTxtDTOs =
		new ArrayList<ResutadosEncuestasPlantillasDTO>();

	public void init() {
		capacitaciones = em.find(Capacitaciones.class, idCapacitacion);
		setearVar();
		seguridadUtilFormController.validarCapacitacionPerteneceUO(idCapacitacion);
		if (capacitaciones.getDatosEspecificosTipoCap() != null)
			tipoCapac = capacitaciones.getDatosEspecificosTipoCap().getDescripcion();

		cargarPlantilla();

	}

	public void imprimir(String tipo) {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		if (tipo.equalsIgnoreCase("GRAFICO")) {
			reportUtilFormController.setNombreReporte("RPT_CU532_imprimir_eval_capacitacion_grafico");
		} else {
			reportUtilFormController.setNombreReporte("RPT_CU532_imprimir_eval_capacitacion");
		}
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
		
	}

	private void cargarParametros() {
		try {
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
			nivelEntidadOeeUtil.setIdConfigCab(capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			ConfiguracionUoCab configuracionUoCab =  em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa", capacitaciones.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa", capacitaciones.getDatosEspecificosTipoCap().getDescripcion());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado", configuracionUoCab.getDenominacionUnidad());

			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("idPlantilla", plantillaConfigurada.getIdPlantillaConf());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cargarPlantilla() {
		try {
			plantillaConfigurada =
				(PlantillaConfigurada) em.createQuery("select c from PlantillaConfigurada c "
					+ " where c.capacitaciones.idCapacitacion =:id  " +
							" and c.valor=:valor").setParameter("id", idCapacitacion).setParameter("valor",valor).getSingleResult();
			nombreEncuesta = plantillaConfigurada.getNombre();
			if (plantillaConfigurada != null) {
				cntEvalTotal = plantillaConfigurada.getTotalEval();
				cntEvalActual = plantillaConfigurada.getActualEval();

				if (cntEvalTotal == null)
					habEncuesta = true;
				else if (cntEvalActual != null
					&& (cntEvalActual.longValue() < cntEvalTotal.longValue()))
					habEncuesta = true;
				else
					habEncuesta = false;

				if (habEncuesta)
					cargarPreguntasConf(plantillaConfigurada.getIdPlantillaConf());
				else {
					cargarRespuestasMultiples();
					cargarRespuestasTxt();
				}

				if (cntEvalActual == null)
					cntEvalActual = new Long(1);
				else {
					if (cntEvalActual.longValue() != cntEvalTotal.longValue())
						cntEvalActual = cntEvalActual + 1;

				}
			}

		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void cargarPreguntasConf(Long idPlantilla) {
		preguntaConfiguradasList =
			em.createQuery("select p from PreguntaConfigurada p "
				+ " where p.plantillaConfigurada.idPlantillaConf=:id "
				+ "order by p.datosEspecificosGrupo.descripcion,p.orden").setParameter("id", idPlantilla).getResultList();
		Long idGrupoAnterior = new Long(-1);
		for (int i = 0; i < preguntaConfiguradasList.size(); i++) {
			PreguntaConfigurada aux = preguntaConfiguradasList.get(i);
			aux.setRespuesta(null);
			aux.setRespuestaTxt(null);
			if (i == 0) {
				idGrupoAnterior =
					preguntaConfiguradasList.get(i).getDatosEspecificosGrupo().getIdDatosEspecificos();
				aux.setHabGrupo(true);
				preguntaConfiguradasList.set(i, aux);
			} else {
				if (idGrupoAnterior.longValue() == preguntaConfiguradasList.get(i).getDatosEspecificosGrupo().getIdDatosEspecificos().intValue()) {// si pertenecfe al mismo grupo
					aux.setHabGrupo(false);
					preguntaConfiguradasList.set(i, aux);
				} else {
					idGrupoAnterior =
						preguntaConfiguradasList.get(i).getDatosEspecificosGrupo().getIdDatosEspecificos();
					aux.setHabGrupo(true);
					preguntaConfiguradasList.set(i, aux);
				}
			}
		}
	}

	public String guardar() {
		try {

			if (preguntaConfiguradasList == null || preguntaConfiguradasList.isEmpty()) {
				statusMessages.add(Severity.ERROR, "No posee preguntas. Verifique");
				return null;
			}
			if (cntEvalTotal.longValue() < cntEvalActual.longValue()) {
				statusMessages.add(Severity.ERROR, "La Cantidad de Participantes Evaluados no puede ser menor al Nro. ingresado. Verifiqe ");
				return null;
			}
			if (cntEvalTotal.longValue() < 0) {
				statusMessages.add(Severity.ERROR, "La Cantidad de Participantes Evaluados no puede ser menor a cero. Verifiqe ");
				return null;
			}
			if (!checkRequired())
				return null;
			/**
			 * 3.3.1.1. Guardar el valor en la tabla PLANTILLA_CONFIGURADA
			 **/
			plantillaConfigurada.setTotalEval(cntEvalTotal);// si tenia valor se actualiza
			plantillaConfigurada.setActualEval(cntEvalActual);
			plantillaConfigurada.setUsuMod(usuarioLogueado.getCodigoUsuario());
			plantillaConfigurada.setFechaMod(new Date());
			em.merge(plantillaConfigurada);
			/** 3.3.23.3.2. Guardar los valores correspondientes al formulario -Tabla RESULTADO_ENCUESTA **/

			for (int i = 0; i < preguntaConfiguradasList.size(); i++) {
				ResultadoEncuesta resultadoEncuesta = new ResultadoEncuesta();
				/**
				 * 3.3.2.1.1. Si la pregunta es del TIPO_PREGUNTA = ‘T’ genera un registro en la tabla RESULTADO_ENCUESTA
				 **/
				if (preguntaConfiguradasList.get(i).getTipoPregunta().equals("T")) {
					resultadoEncuesta.setPreguntaConfigurada(preguntaConfiguradasList.get(i));
					resultadoEncuesta.setRespuestaTexto(preguntaConfiguradasList.get(i).getRespuestaTxt());
					resultadoEncuesta.setActivo(true);
					resultadoEncuesta.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					resultadoEncuesta.setFechaAlta(new Date());
					em.persist(resultadoEncuesta);
				}
				/**
				 * 3.3.2.1.2. Si la pregunta es del TIPO_PREGUNTA = ‘M’ genera un registro en la tabla RESULTADO_ENCUESTA:
				 **/
				if (preguntaConfiguradasList.get(i).getTipoPregunta().equals("M")) {
					resultadoEncuesta.setPreguntaConfigurada(preguntaConfiguradasList.get(i));
					resultadoEncuesta.setRespuestaMultipleConfigurada(em.find(RespuestaMultipleConfigurada.class, preguntaConfiguradasList.get(i).getRespuesta()));
					resultadoEncuesta.setActivo(true);
					resultadoEncuesta.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					resultadoEncuesta.setFechaAlta(new Date());
					em.persist(resultadoEncuesta);
				}

			}

			em.flush();
			/**
			 * 3.3.2.1.3.1. Si llega a hacer el paso 3.2 (es porque ya no hay encuestas por cargar), las cantidades son iguales
			 */
			if (cntEvalActual.longValue() == cntEvalTotal.longValue())
				habEncuesta = false;
			/**
			 * Actualizar el estado de la Capacitación
			 */
			if (!habEncuesta) {
				capacitaciones.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_CAPACITACION", "FINALIZADA"));
				capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
				capacitaciones.setFechaMod(new Date());
				em.merge(capacitaciones);
				
				plantillaConfigurada.setEstado("F");
				plantillaConfigurada.setFechaMod(new Date());
				plantillaConfigurada.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(plantillaConfigurada);
				
				em.flush();
			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			preguntaConfiguradasList = new ArrayList<PreguntaConfigurada>();
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Se ha producido un error: " + e.getMessage());
			return null;
		}
	}

	private boolean checkRequired() {
		for (PreguntaConfigurada p : preguntaConfiguradasList) {
			if (p.getObligatorio()) {
				if (p.getTipoPregunta().equals("M")) {
					if (p.getRespuesta() == null) {
						statusMessages.add(Severity.ERROR, "Debe completar la pregunta del grupo: "
							+ p.getDatosEspecificosGrupo().getDescripcion());
						return false;
					}
				} else if (p.getTipoPregunta().equals("T")) {
					if (p.getRespuestaTxt() == null || p.getRespuestaTxt().trim().equals("")) {
						statusMessages.add(Severity.ERROR, "Debe completar la pregunta del grupo: "
							+ p.getDatosEspecificosGrupo().getDescripcion());
						return false;
					}
				}
			}
		}
		return true;
	}

	public List<SelectItem> getRespuestasSelectItems(Long idpregunta) {
		List<SelectItem> si = new Vector<SelectItem>();
		for (RespuestaMultipleConfigurada o : respuestas(idpregunta))
			si.add(new SelectItem(o.getIdRespuestaMConf(), "" + o.getRespuestaOpc()));
		return si;
	}

	@SuppressWarnings("unchecked")
	private List<RespuestaMultipleConfigurada> respuestas(Long idpregunta) {
		List<RespuestaMultipleConfigurada> r =
			em.createQuery("Select r from RespuestaMultipleConfigurada r "
				+ " where r.preguntaConfigurada.idPreguntaConf=:idPreguntaCong order by r.idRespuestaMConf ").setParameter("idPreguntaCong", idpregunta).getResultList();

		return r;

	}

	@SuppressWarnings("unchecked")
	private List<Long> idRptasListas(Long idPregunta) {
		try {
			List<Long> respuestas =
				em.createQuery("Select r.respuestaMultipleConfigurada.idRespuestaMConf from ResultadoEncuesta r"
					+ " where r.preguntaConfigurada.idPreguntaConf =:id").setParameter("id", idPregunta).getResultList();
			return respuestas;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Long>();
		}
	}

	@SuppressWarnings("unchecked")
	private void cargarRespuestasMultiples() {
		List<ResultadoEncuesta> resultadoEncuestas =
			em.createQuery("Select r from ResultadoEncuesta r "
				+ " where r.preguntaConfigurada.plantillaConfigurada.idPlantillaConf=:idPlantillaConf "
				+ " and r.respuestaMultipleConfigurada.idRespuestaMConf is not null order by "
				+ " r.preguntaConfigurada.datosEspecificosGrupo.descripcion,r.preguntaConfigurada.idPreguntaConf ").setParameter("idPlantillaConf", plantillaConfigurada.getIdPlantillaConf()).getResultList();
		encuestasPlantillasMultDTOs = new ArrayList<ResutadosEncuestasPlantillasDTO>();
		Long idGrupoanterior = new Long(-1);
		Long idPreguntaAnterior = new Long(-1);
		for (int i = 0; i < resultadoEncuestas.size(); i++) {
			ResultadoEncuesta resultado = resultadoEncuestas.get(i);
			PreguntaConfigurada preguntaConfigurada =
				em.find(PreguntaConfigurada.class, resultado.getPreguntaConfigurada().getIdPreguntaConf());
			DatosEspecificos grupo =
				em.find(DatosEspecificos.class, preguntaConfigurada.getDatosEspecificosGrupo().getIdDatosEspecificos());

			ResutadosEncuestasPlantillasDTO aux = new ResutadosEncuestasPlantillasDTO();

			if (resultado.getRespuestaMultipleConfigurada() != null) {
				if (i == 0
					|| idGrupoanterior.longValue() != grupo.getIdDatosEspecificos().longValue()) {
					aux.setEsGrupo(true);
					aux.setGrupo(grupo);
					aux.setResultadoEncuesta(resultado);
					encuestasPlantillasMultDTOs.add(aux);// se agrega solo el grupo

					idGrupoanterior = grupo.getIdDatosEspecificos();

					ResutadosEncuestasPlantillasDTO auxResul =
						new ResutadosEncuestasPlantillasDTO();
					auxResul.setPreguntaConfigurada(preguntaConfigurada);
					auxResul.setRptTxt(false);
					auxResul.setEsGrupo(false);
					encuestasPlantillasMultDTOs.add(auxResul);// se agrega solo la pregunta
					idPreguntaAnterior = preguntaConfigurada.getIdPreguntaConf();
					for (RespuestaMultipleConfigurada o : respuestas(preguntaConfigurada.getIdPreguntaConf())) {
						ResutadosEncuestasPlantillasDTO rpta =
							new ResutadosEncuestasPlantillasDTO();
						rpta.setRespuestaMultipleConfigurada(o);
						rpta.setRptTxt(false);
						for (Long id : idRptasListas(preguntaConfigurada.getIdPreguntaConf())) {
							if (id.longValue() == o.getIdRespuestaMConf().longValue())
								rpta.setCntRpta(cntRespuestasMultiples(resultado.getPreguntaConfigurada().getIdPreguntaConf(), id));
							else {
								if (rpta.getCntRpta() == null)
									rpta.setCntRpta(new Long(0));
							}

						}
						encuestasPlantillasMultDTOs.add(rpta);
					}
				} else {// si el mismo grupo,pero pregunta diferente
					if (idPreguntaAnterior.longValue() != preguntaConfigurada.getIdPreguntaConf().longValue()) {
						ResutadosEncuestasPlantillasDTO auxResul =
							new ResutadosEncuestasPlantillasDTO();
						auxResul.setPreguntaConfigurada(preguntaConfigurada);
						auxResul.setRptTxt(false);
						idPreguntaAnterior = preguntaConfigurada.getIdPreguntaConf();
						encuestasPlantillasMultDTOs.add(auxResul);// se agrega solo la pregunta
						for (RespuestaMultipleConfigurada o : respuestas(preguntaConfigurada.getIdPreguntaConf())) {
							ResutadosEncuestasPlantillasDTO rpta =
								new ResutadosEncuestasPlantillasDTO();
							rpta.setRespuestaMultipleConfigurada(o);
							rpta.setRptTxt(false);
							for (Long id : idRptasListas(preguntaConfigurada.getIdPreguntaConf())) {
								if (id.longValue() == o.getIdRespuestaMConf().longValue())
									rpta.setCntRpta(cntRespuestasMultiples(resultado.getPreguntaConfigurada().getIdPreguntaConf(), id));
								else {
									if (rpta.getCntRpta() == null)
										rpta.setCntRpta(new Long(0));
								}
							}
							encuestasPlantillasMultDTOs.add(rpta);
						}
					}
				}

			}

		}
	}

	@SuppressWarnings("unchecked")
	private void cargarRespuestasTxt() {
		List<ResultadoEncuesta> resultadoEncuestas =
			em.createQuery("Select r from ResultadoEncuesta r "
				+ " where r.preguntaConfigurada.plantillaConfigurada.idPlantillaConf=:idPlantillaConf "
				+ " and r.respuestaMultipleConfigurada.idRespuestaMConf is null order by "
				+ " r.preguntaConfigurada.datosEspecificosGrupo.descripcion,r.preguntaConfigurada.idPreguntaConf ").setParameter("idPlantillaConf", plantillaConfigurada.getIdPlantillaConf()).getResultList();
		Long idGrupoanterior = new Long(-1);
		Long idPreguntaAnterior = new Long(-1);

		ResutadosEncuestasPlantillasDTO aux = new ResutadosEncuestasPlantillasDTO();
		for (int i = 0; i < resultadoEncuestas.size(); i++) {
			ResultadoEncuesta res = resultadoEncuestas.get(i);
			PreguntaConfigurada preguntaConfigurada =
				em.find(PreguntaConfigurada.class, res.getPreguntaConfigurada().getIdPreguntaConf());
			DatosEspecificos grupo =
				em.find(DatosEspecificos.class, preguntaConfigurada.getDatosEspecificosGrupo().getIdDatosEspecificos());

			aux = new ResutadosEncuestasPlantillasDTO();
			if (res.getRespuestaTexto() != null) {
				if (i == 0
					|| idGrupoanterior.longValue() != grupo.getIdDatosEspecificos().longValue()) {
					aux.setGrupo(grupo);
					aux.setRptTxt(true);
					aux.setResultadoEncuesta(res);
					aux.setPreguntaConfigurada(res.getPreguntaConfigurada());
					aux.setRespuestaTxt(respuestasTxt(preguntaConfigurada.getIdPreguntaConf()));
					encuestasPlantillasTxtDTOs.add(aux);
					idGrupoanterior = grupo.getIdDatosEspecificos();
					idPreguntaAnterior = preguntaConfigurada.getIdPreguntaConf();
				} else {
					if (idPreguntaAnterior.longValue() != preguntaConfigurada.getIdPreguntaConf().longValue()) {
						aux.setResultadoEncuesta(res);
						aux.setPreguntaConfigurada(res.getPreguntaConfigurada());
						aux.setRespuestaTxt(respuestasTxt(preguntaConfigurada.getIdPreguntaConf()));
						encuestasPlantillasTxtDTOs.add(aux);
						idGrupoanterior = grupo.getIdDatosEspecificos();
						idPreguntaAnterior = preguntaConfigurada.getIdPreguntaConf();
					}
				}

			}
		}
	}

	private Long cntRespuestasMultiples(Long idPreguntaConf, Long idRespuestaConf) {
		try {

			String sql =
				"SELECT  capacitacion.fnc_resultado_opcion_multiple(:idPregunta,:idRespuesta) as cnt "
					+ "FROM  CAPACITACION.CAPACITACIONES ";
			Object resultado =
				(Object) em.createNativeQuery(sql).setParameter("idPregunta", idPreguntaConf).setParameter("idRespuesta", idRespuestaConf).getSingleResult();
			if (resultado == null)
				return new Long(0);

			return Long.parseLong(resultado.toString());

		} catch (Exception e) {
			return new Long(0);
		}
	}

	private String respuestasTxt(Long idPregunta) {
		try {
			String sql =
				"SELECT   capacitacion.fnc_resultado_texto_libre(:idPregunta) as resul "
					+ "FROM  CAPACITACION.CAPACITACIONES ";
			Object resultado =
				(Object) em.createNativeQuery(sql).setParameter("idPregunta", idPregunta).getSingleResult();
			if (resultado == null)
				return null;

			return resultado.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private void setearVar() {
		habEncuesta = true;
		preguntaConfiguradasList = new ArrayList<PreguntaConfigurada>();
		encuestasPlantillasMultDTOs = new ArrayList<ResutadosEncuestasPlantillasDTO>();
		encuestasPlantillasTxtDTOs = new ArrayList<ResutadosEncuestasPlantillasDTO>();
		cntEvalActual = null;
		cntEvalTotal = null;

	}

	/** GETTER'S && SETTER'S **/

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public String getTipoCapac() {
		return tipoCapac;
	}

	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public String getNombreEncuesta() {
		return nombreEncuesta;
	}

	public void setNombreEncuesta(String nombreEncuesta) {
		this.nombreEncuesta = nombreEncuesta;
	}

	public Long getCntEvalTotal() {
		return cntEvalTotal;
	}

	public void setCntEvalTotal(Long cntEvalTotal) {
		this.cntEvalTotal = cntEvalTotal;
	}

	public Long getCntEvalActual() {
		return cntEvalActual;
	}

	public void setCntEvalActual(Long cntEvalActual) {
		this.cntEvalActual = cntEvalActual;
	}

	public PlantillaConfigurada getPlantillaConfigurada() {
		return plantillaConfigurada;
	}

	public void setPlantillaConfigurada(PlantillaConfigurada plantillaConfigurada) {
		this.plantillaConfigurada = plantillaConfigurada;
	}

	public boolean isHabEncuesta() {
		return habEncuesta;
	}

	public void setHabEncuesta(boolean habEncuesta) {
		this.habEncuesta = habEncuesta;
	}

	public List<PreguntaConfigurada> getPreguntaConfiguradasList() {
		return preguntaConfiguradasList;
	}

	public void setPreguntaConfiguradasList(List<PreguntaConfigurada> preguntaConfiguradasList) {
		this.preguntaConfiguradasList = preguntaConfiguradasList;
	}

	public List<ResutadosEncuestasPlantillasDTO> getEncuestasPlantillasMultDTOs() {
		return encuestasPlantillasMultDTOs;
	}

	public void setEncuestasPlantillasMultDTOs(List<ResutadosEncuestasPlantillasDTO> encuestasPlantillasMultDTOs) {
		this.encuestasPlantillasMultDTOs = encuestasPlantillasMultDTOs;
	}

	public List<ResutadosEncuestasPlantillasDTO> getEncuestasPlantillasTxtDTOs() {
		return encuestasPlantillasTxtDTOs;
	}

	public void setEncuestasPlantillasTxtDTOs(List<ResutadosEncuestasPlantillasDTO> encuestasPlantillasTxtDTOs) {
		this.encuestasPlantillasTxtDTOs = encuestasPlantillasTxtDTOs;
	}

	public Long getValor() {
		return valor;
	}

	public void setValor(Long valor) {
		this.valor = valor;
	}

}
