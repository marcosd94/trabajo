package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.jboss.seam.security.AuthorizationException;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import com.sun.org.apache.bcel.internal.generic.NEW;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ComisionEval;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EscalaEval;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposEvaluacion;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PlantillaEvalDet;
import py.com.excelsis.sicca.entity.PlantillaEvalEscala;
import py.com.excelsis.sicca.entity.PlantillaEvalPdec;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecCab;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecDet;
import py.com.excelsis.sicca.evaluacion.session.ComisionEvalList;
import py.com.excelsis.sicca.evaluacion.session.GrupoMetodologiaList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("configurarPlantillaEvaCU558FC")
public class ConfigurarPlantillaEvaCU558FC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private List<PlantillaEvalConfDet> plantillaEvalConfDets =
		new ArrayList<PlantillaEvalConfDet>();
	private List<PlantillaEvalConfDet> eliminarPlantillaEvalConfDets =
		new ArrayList<PlantillaEvalConfDet>();
	private Long idEvaluacionDesempeno = null;
	private Long idGrupoEvaluacion = null;
	private Long idMetodologia = null;
	private String evaluacion;
	private String grupo;
	private String plantilla;
	private Long idPlantillaEval = null;
	private Long orden;
	private String resultadoEsperado;
	private String actividades;
	private String indicadoresCump;
	private Long idPlaEvalConfEdit = null;
	private boolean modoUpdate;
	private Long maxOrden = null;
	private int indexUpdate = -1;
	private EvaluacionDesempeno eDesempeno= new EvaluacionDesempeno();
	/**
	 * Recibe como parámetro: ( ID_GRUPO_METODOLOGIA)
	 */
	private Long idGrupoMetodologia;
	private String from;

	
	
	public void init() {
		try {
			inicializar();
			if (!seguridadUtilFormController.verificarPerteneceOee(eDesempeno.getConfiguracionUoCab().getIdConfiguracionUo())) {
				throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
			}
			cargarCabecera();
			search();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initVer() {
		try {
			inicializar();
			cargarCabecera();
			search();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void inicializar(){
		GrupoMetodologia grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		idEvaluacionDesempeno =
			grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		 eDesempeno= em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
			evaluacion = eDesempeno.getTitulo();
			grupo = grupoMetodologia.getGruposEvaluacion().getGrupo();
			plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
	}
	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU567_IMPRIMIR_METODOLOGIA");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	public boolean habImprimir(){
		if(!plantillaEvalConfDets.isEmpty())
		{
			if(plantillaEvalConfDets.get(0).getIdPlantillaEvalConfDet()!=null)
				return true;
		}
		return false;
	}
	
	private void cargarParametros() {
		try {

			ConfiguracionUoCab configuracionUoCab =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oeeFil", diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("uniO", diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("idGrupoMetodologia", idGrupoMetodologia.intValue());
			reportUtilFormController.getParametros().put("oee", configuracionUoCab.getDenominacionUnidad());
			reportUtilFormController.getParametros().put("usuAlta", usuarioLogueado.getCodigoUsuario());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void copiarDatos() {
		List<PlantillaEvalDet> evalDets =
			em.createQuery("Select p from PlantillaEvalDet p"
				+ " where p.activo=true and p.plantillaEval.idPlantillaEval=:idPlantillaEval").setParameter("idPlantillaEval", idPlantillaEval).getResultList();
		for (PlantillaEvalDet plantillaEvalDet : evalDets) {
			PlantillaEvalConfDet confDet = new PlantillaEvalConfDet();
			confDet.setOrden(plantillaEvalDet.getOrden());
			confDet.setResultadoEsperado(plantillaEvalDet.getResultadoEsperado());
			confDet.setActividades(plantillaEvalDet.getActividades());
			confDet.setIndicadoresCump(plantillaEvalDet.getIndicadoresCump());
			confDet.setActivo(true);
			confDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			confDet.setFechaAlta(new Date());
			plantillaEvalConfDets.add(confDet);
		}
		calculoMaxNroOrdenTMP();
	}

	private void cargarCabecera() throws Exception {
		
		
		
		nivelEntidadOeeUtil.setIdConfigCab(eDesempeno.getConfiguracionUoCab().getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(eDesempeno.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		limpiar();
		idPlaEvalConfEdit = null;
		idPlantillaEval = null;
		calculoMaxNroOrden();
		eliminarPlantillaEvalConfDets= new ArrayList<PlantillaEvalConfDet>();
	}

	@SuppressWarnings("unchecked")
	public void search() {
		plantillaEvalConfDets =
			em.createQuery("Select g from PlantillaEvalConfDet g "
				+ " where g.grupoMetodologia.idGrupoMetodologia=:idGrupoMeto "
				+ " and g.activo=true order by orden ").setParameter("idGrupoMeto", idGrupoMetodologia).getResultList();

	}

	private boolean checkValues() {

		if (orden == null) {
			statusMessages.add(Severity.ERROR, "Debe completar el campo Orden antes de realizar esta acci&oacute;n");
			return false;
		}
		if (orden.longValue() <= 0) {
			statusMessages.add(Severity.ERROR, "El campo Orden debe ser mayor a cero");
			return false;
		}
		if (orden.intValue() > maxOrden.intValue()) {
			statusMessages.add(Severity.ERROR, "El campo orden no puede ser mayor a el Maximo sugerido "
				+ maxOrden);
			return false;
		}
		if(indexUpdate!=-1){
			if (orden.intValue() >= maxOrden.intValue()) {
				statusMessages.add(Severity.ERROR, "El campo orden no puede ser mayor a el Maximo  "
					+ plantillaEvalConfDets.get(plantillaEvalConfDets.size()-1).getOrden());
				return false;
			}
		}
		

		if (resultadoEsperado == null || resultadoEsperado.trim().equals("")) {
			statusMessages.add(Severity.ERROR, "Debe completar el campo Resultado Esperado antes de realizar esta acci&oacute;n");
			return false;
		}
		if (actividades == null || actividades.trim().equals("")) {
			statusMessages.add(Severity.ERROR, "Debe completar el campo Actividades antes de realizar esta acci&oacute;n");
			return false;
		}

		if (indicadoresCump == null || indicadoresCump.trim().equals("")) {
			statusMessages.add(Severity.ERROR, "Debe completar el campo Indicadores de Cumplimientos antes de realizar esta acci&oacute;n");
			return false;
		}
		for (int i = 0; i < plantillaEvalConfDets.size(); i++) {
			PlantillaEvalConfDet det = plantillaEvalConfDets.get(i);
			if (i != indexUpdate) {
				if (det.getActividades().trim().equalsIgnoreCase(actividades.trim())
					&& det.getResultadoEsperado().trim().equalsIgnoreCase(resultadoEsperado.trim())
					&& det.getIndicadoresCump().trim().equalsIgnoreCase(indicadoresCump.trim())) {
					statusMessages.add(Severity.ERROR, "El registro ya existe. Verifique");
					return false;
				}
			}
		}

		return true;
	}

	public void agregar() {
		try {
			if (!checkValues())
				return;

			PlantillaEvalConfDet confDet = new PlantillaEvalConfDet();
			if (idPlaEvalConfEdit != null) {
				confDet = em.find(PlantillaEvalConfDet.class, idPlaEvalConfEdit);
				confDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				confDet.setFechaMod(new Date());
			} else {
				confDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				confDet.setFechaAlta(new Date());
			}
			confDet.setOrden(orden);
			confDet.setActividades(actividades);
			confDet.setIndicadoresCump(indicadoresCump);
			confDet.setResultadoEsperado(resultadoEsperado);
			confDet.setActivo(true);
			if (indexUpdate != -1) {
				if (!recalcularNroOrdenUp(orden, confDet)) {
					plantillaEvalConfDets.remove(indexUpdate);
					plantillaEvalConfDets.add(confDet);
				}

			} else {
				if (!recalcularNroOrden(orden, confDet))
					plantillaEvalConfDets.add(confDet);

			}

			limpiar();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculoMaxNroOrden() {
		try {
			maxOrden =
				(Long) em.createQuery("Select max(p.orden) from PlantillaEvalConfDet p"
					+ " where p.grupoMetodologia.idGrupoMetodologia=:idGrupoMet and p.activo=true").setParameter("idGrupoMet", idGrupoMetodologia).getSingleResult();
			if (maxOrden == null) {
				maxOrden = new Long(1);
			} else
				maxOrden = maxOrden + 1;
			orden = maxOrden;
		} catch (NoResultException e) {
			orden = new Long(1);
		}

	}

	private void calculoMaxNroOrdenTMP() {
		try {
			int max = plantillaEvalConfDets.size() - 1;
			maxOrden = plantillaEvalConfDets.get(max).getOrden().longValue() + 1;
			if (maxOrden == null)
				maxOrden = new Long(1);
			orden = maxOrden;
		} catch (Exception e) {
			orden = new Long(1);
		}

	}

	/**
	 * RECALCULA EL ORDEN DE LA LISTA PARA EL CASO DE AGREGAR EN EL CASO QUE SE HAYA MODIFICADO
	 **/
	private boolean recalcularNroOrden(Long orden, PlantillaEvalConfDet evalConfDet) {
		if (orden.longValue() == maxOrden.longValue())
			return false;
		boolean recalcular = false;
		List<PlantillaEvalConfDet> aux = new ArrayList<PlantillaEvalConfDet>();
		for (int i = 0; i < plantillaEvalConfDets.size(); i++) {
			if (evalConfDet != null) {// SI VIENE de AGREGAR
				if (plantillaEvalConfDets.get(i).getOrden().longValue() != orden.longValue())
					aux.add(plantillaEvalConfDets.get(i));
				else
					recalcular = true;
			} else {
				int var = plantillaEvalConfDets.get(i).getOrden().intValue() + 1;
				aux.add(plantillaEvalConfDets.get(i));
				if (var == orden.intValue())
					recalcular = true;
			}

			if (recalcular) {// se agrega el nuevo
				Long ordenMod = null;
				int desde = i;
				if (evalConfDet != null) {
					aux.add(evalConfDet);
					ordenMod = orden + 1;
					// desde++;

				} else {// para el caso qeu se elimine
					ordenMod = orden;
				}

				// recalcular
				if (desde == plantillaEvalConfDets.size()) {
					PlantillaEvalConfDet det = plantillaEvalConfDets.get(desde - 1);
					det.setOrden(ordenMod);
					aux.add(det);
					break;
				}
				int tam = plantillaEvalConfDets.size();
				for (int j = desde; j < plantillaEvalConfDets.size(); j++) {
					PlantillaEvalConfDet det = new PlantillaEvalConfDet();
					det.setActividades(plantillaEvalConfDets.get(j).getActividades());
					det.setActivo(true);
					det.setFechaAlta(plantillaEvalConfDets.get(j).getFechaAlta());
					det.setFechaMod(plantillaEvalConfDets.get(j).getFechaMod());
					det.setGrupoMetodologia(plantillaEvalConfDets.get(j).getGrupoMetodologia());
					det.setIndicadoresCump(plantillaEvalConfDets.get(j).getIndicadoresCump());
					det.setResultadoEsperado(plantillaEvalConfDets.get(j).getResultadoEsperado());
					det.setUsuAlta(plantillaEvalConfDets.get(j).getUsuAlta());
					det.setUsuMod(plantillaEvalConfDets.get(j).getUsuMod());
					det.setIdPlantillaEvalConfDet(plantillaEvalConfDets.get(j).getIdPlantillaEvalConfDet());
					System.out.println(ordenMod);
					System.out.println(det.getActividades());
					det.setOrden(ordenMod);
					aux.add(det);
					ordenMod++;

				}
				break;
			}
		}
		// volcamos a la lista original
		plantillaEvalConfDets = new ArrayList<PlantillaEvalConfDet>();
		plantillaEvalConfDets.addAll(aux);

		return true;

	}

	/**
	 * RECALCULA EL ORDEN DE LA LISTA PARA EL CASO DE AGREGAR-ELIMINAR EN EL CASO QUE SE HAYA MODIFICADO
	 **/
	private boolean recalcularNroOrdenUp(Long orden, PlantillaEvalConfDet evalConfDet) {
		if (orden.longValue() == maxOrden.longValue())
			return false;
		boolean recalcular = false;
		List<PlantillaEvalConfDet> aux = new ArrayList<PlantillaEvalConfDet>();
		for (int i = 0; i < plantillaEvalConfDets.size(); i++) {
			if (i == indexUpdate) {
				if (plantillaEvalConfDets.get(i).getOrden().equals(orden)) {
					plantillaEvalConfDets.remove(indexUpdate);
					plantillaEvalConfDets.add(indexUpdate, evalConfDet);
					aux.addAll(plantillaEvalConfDets);
					break;
				} else
					recalcular = true;
			} else {
				if (orden.longValue() == plantillaEvalConfDets.get(i).getOrden().longValue())
					recalcular = true;
				else
					aux.add(plantillaEvalConfDets.get(i));
			}
			if (recalcular) {// se agrega el nuevo
				Long ordenMod = plantillaEvalConfDets.get(i).getOrden();
				int desde = i;
				int tam = plantillaEvalConfDets.size();
				// recalcular
				for (int j = desde; j < plantillaEvalConfDets.size(); j++) {
					PlantillaEvalConfDet det = new PlantillaEvalConfDet();
					det.setActividades(plantillaEvalConfDets.get(j).getActividades());
					det.setActivo(true);
					det.setFechaAlta(plantillaEvalConfDets.get(j).getFechaAlta());
					det.setFechaMod(plantillaEvalConfDets.get(j).getFechaMod());
					det.setGrupoMetodologia(plantillaEvalConfDets.get(j).getGrupoMetodologia());
					det.setIndicadoresCump(plantillaEvalConfDets.get(j).getIndicadoresCump());
					det.setResultadoEsperado(plantillaEvalConfDets.get(j).getResultadoEsperado());
					det.setUsuAlta(plantillaEvalConfDets.get(j).getUsuAlta());
					det.setUsuMod(plantillaEvalConfDets.get(j).getUsuMod());
					det.setIdPlantillaEvalConfDet(plantillaEvalConfDets.get(j).getIdPlantillaEvalConfDet());
					if (j != indexUpdate) {
						if (plantillaEvalConfDets.get(j).getOrden().longValue() == orden.longValue()) {
							if(ordenMod.longValue()<plantillaEvalConfDets.get(j).getOrden().longValue()){
								//se agrega el que tenia el valor anterior
								det.setOrden(ordenMod);
								aux.add(det);
								ordenMod++;
								//se agrega el que se quiere modificar
								aux.add(evalConfDet);
								ordenMod++;
							}else{
								//se agrega el que se quiere modificar
								aux.add(evalConfDet);
								ordenMod++;
								//se agrega el que tenia el valor anterior
								det.setOrden(ordenMod);
								aux.add(det);
								ordenMod++;
							}
							
						}else if (aux.size() < tam) {
							det.setOrden(ordenMod);
							aux.add(det);
							ordenMod++;
						}

					} else {
						if (plantillaEvalConfDets.get(j).getOrden().longValue() < orden.longValue()) {
							if (j + 1 < tam) {

								PlantillaEvalConfDet var = new PlantillaEvalConfDet();
								Long nuevaOrden = ordenMod;
								var.setOrden(nuevaOrden);
								var.setActividades(plantillaEvalConfDets.get(j + 1).getActividades());
								var.setActivo(true);
								var.setFechaAlta(plantillaEvalConfDets.get(j + 1).getFechaAlta());
								var.setFechaMod(plantillaEvalConfDets.get(j + 1).getFechaMod());
								var.setGrupoMetodologia(plantillaEvalConfDets.get(j + 1).getGrupoMetodologia());
								var.setIndicadoresCump(plantillaEvalConfDets.get(j + 1).getIndicadoresCump());
								var.setResultadoEsperado(plantillaEvalConfDets.get(j + 1).getResultadoEsperado());
								var.setUsuAlta(plantillaEvalConfDets.get(j + 1).getUsuAlta());
								var.setUsuMod(plantillaEvalConfDets.get(j + 1).getUsuMod());
								var.setIdPlantillaEvalConfDet(plantillaEvalConfDets.get(j + 1).getIdPlantillaEvalConfDet());
								aux.add(var);
								ordenMod++;

								if (plantillaEvalConfDets.get(j + 1).getOrden().longValue() == orden.longValue()) {
									aux.add(evalConfDet);
									ordenMod++;
								}
								j++;

							}

						}
					}

				}
				break;
			}
		}
		// volcamos a la lista original
		plantillaEvalConfDets = new ArrayList<PlantillaEvalConfDet>();
		plantillaEvalConfDets.addAll(aux);

		return true;

	}

	public void limpiar() {
		actividades = null;
		indicadoresCump = null;
		resultadoEsperado = null;
		modoUpdate = false;
		
		if (!plantillaEvalConfDets.isEmpty())
			calculoMaxNroOrdenTMP();
		else
			calculoMaxNroOrden();

		indexUpdate = -1;

	}

	public void editar(int index) {
		PlantillaEvalConfDet aux = plantillaEvalConfDets.get(index);
		orden = aux.getOrden();
		resultadoEsperado = aux.getResultadoEsperado();
		indicadoresCump = aux.getIndicadoresCump();
		actividades = aux.getActividades();
		idPlaEvalConfEdit = aux.getIdPlantillaEvalConfDet();
		modoUpdate = true;
		indexUpdate = index;
	}

	public String save() {
		try {
			for (PlantillaEvalConfDet confDet : plantillaEvalConfDets) {
				confDet.setGrupoMetodologia(em.find(GrupoMetodologia.class, idGrupoMetodologia));
				if (confDet.getIdPlantillaEvalConfDet() != null) {
					em.merge(confDet);
				} else
					em.persist(confDet);
			}
			for (PlantillaEvalConfDet confDetDelete : eliminarPlantillaEvalConfDets) {
				confDetDelete.setActivo(false);
				confDetDelete.setUsuMod(usuarioLogueado.getCodigoUsuario());
				confDetDelete.setFechaMod(new Date());
				em.merge(confDetDelete);
			}
			em.flush();
			System.out.println(from);
			search();
			calculoMaxNroOrden();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, "Se ha producido un error: " + e.getMessage());
			return null;
		}
	}

	public String volver() {
		limpiar();
		idPlaEvalConfEdit = null;
		idPlantillaEval = null;
		plantillaEvalConfDets = new ArrayList<PlantillaEvalConfDet>();
		eliminarPlantillaEvalConfDets= new ArrayList<PlantillaEvalConfDet>();
		search();
		em.clear();
		return from;

	}

	public void eliminar(int index) {
		PlantillaEvalConfDet aux = plantillaEvalConfDets.get(index);
		if (aux.getIdPlantillaEvalConfDet() != null)
			eliminarPlantillaEvalConfDets.add(aux);
		int tam = plantillaEvalConfDets.size() - 1;
		plantillaEvalConfDets.remove(index);

		System.out.println(tam + "-----" + index);
		if (index != tam) {
			if (index == 0)
				recalcularOrdenEliminar(aux.getOrden());
			else
				recalcularOrdenEliminar(plantillaEvalConfDets.get(0).getOrden());

		}
		if (!plantillaEvalConfDets.isEmpty())
			calculoMaxNroOrdenTMP();
		else
			calculoMaxNroOrden();
	}

	private void recalcularOrdenEliminar(Long ordenEliminado) {
		Long ordenMod = ordenEliminado;
		List<PlantillaEvalConfDet> aux = new Vector<PlantillaEvalConfDet>();
		for (int i = 0; i < plantillaEvalConfDets.size(); i++) {
			PlantillaEvalConfDet det = new PlantillaEvalConfDet();
			det.setActividades(plantillaEvalConfDets.get(i).getActividades());
			det.setActivo(true);
			det.setFechaAlta(plantillaEvalConfDets.get(i).getFechaAlta());
			det.setFechaMod(plantillaEvalConfDets.get(i).getFechaMod());
			det.setGrupoMetodologia(plantillaEvalConfDets.get(i).getGrupoMetodologia());
			det.setIndicadoresCump(plantillaEvalConfDets.get(i).getIndicadoresCump());
			det.setResultadoEsperado(plantillaEvalConfDets.get(i).getResultadoEsperado());
			det.setUsuAlta(plantillaEvalConfDets.get(i).getUsuAlta());
			det.setUsuMod(plantillaEvalConfDets.get(i).getUsuMod());
			det.setOrden(ordenMod);
			det.setIdPlantillaEvalConfDet(plantillaEvalConfDets.get(i).getIdPlantillaEvalConfDet());
			aux.add(det);
			ordenMod++;
		}
		plantillaEvalConfDets = new Vector<PlantillaEvalConfDet>();
		plantillaEvalConfDets.addAll(aux);
	}

	// GETTERS Y SETTERS

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public Long getIdGrupoEvaluacion() {
		return idGrupoEvaluacion;
	}

	public void setIdGrupoEvaluacion(Long idGrupoEvaluacion) {
		this.idGrupoEvaluacion = idGrupoEvaluacion;
	}

	public Long getIdMetodologia() {
		return idMetodologia;
	}

	public void setIdMetodologia(Long idMetodologia) {
		this.idMetodologia = idMetodologia;
	}

	public String getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}

	public Long getIdGrupoMetodologia() {
		return idGrupoMetodologia;
	}

	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}

	public List<PlantillaEvalConfDet> getPlantillaEvalConfDets() {
		return plantillaEvalConfDets;
	}

	public void setPlantillaEvalConfDets(List<PlantillaEvalConfDet> plantillaEvalConfDets) {
		this.plantillaEvalConfDets = plantillaEvalConfDets;
	}

	public Long getIdPlantillaEval() {
		return idPlantillaEval;
	}

	public void setIdPlantillaEval(Long idPlantillaEval) {
		this.idPlantillaEval = idPlantillaEval;
	}

	public Long getIdPlaEvalConfEdit() {
		return idPlaEvalConfEdit;
	}

	public void setIdPlaEvalConfEdit(Long idPlaEvalConfEdit) {
		this.idPlaEvalConfEdit = idPlaEvalConfEdit;
	}

	public boolean isModoUpdate() {
		return modoUpdate;
	}

	public void setModoUpdate(boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

	public List<PlantillaEvalConfDet> getEliminarPlantillaEvalConfDets() {
		return eliminarPlantillaEvalConfDets;
	}

	public void setEliminarPlantillaEvalConfDets(List<PlantillaEvalConfDet> eliminarPlantillaEvalConfDets) {
		this.eliminarPlantillaEvalConfDets = eliminarPlantillaEvalConfDets;
	}

	public String getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	public Long getOrden() {
		return orden;
	}

	public void setOrden(Long orden) {
		this.orden = orden;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getResultadoEsperado() {
		return resultadoEsperado;
	}

	public void setResultadoEsperado(String resultadoEsperado) {
		this.resultadoEsperado = resultadoEsperado;
	}

	public String getActividades() {
		return actividades;
	}

	public void setActividades(String actividades) {
		this.actividades = actividades;
	}

	public String getIndicadoresCump() {
		return indicadoresCump;
	}

	public void setIndicadoresCump(String indicadoresCump) {
		this.indicadoresCump = indicadoresCump;
	}

	public int getIndexUpdate() {
		return indexUpdate;
	}

	public void setIndexUpdate(int indexUpdate) {
		this.indexUpdate = indexUpdate;
	}

	public Long getMaxOrden() {
		return maxOrden;
	}

	public void setMaxOrden(Long maxOrden) {
		this.maxOrden = maxOrden;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
