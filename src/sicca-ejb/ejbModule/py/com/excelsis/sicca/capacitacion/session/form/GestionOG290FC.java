package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.capacitacion.session.CapaciatcionesListCustom;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("gestionOG290FC")
public class GestionOG290FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	CapaciatcionesListCustom capaciatcionesListCustom;

	private List<SelectItem> comboTipoCapaSI;
	private List<SelectItem> comboEstadoCapaSI;
	private Integer idEstado;
	private Long idTipo;
	private String capaDesc;
	private Map<String, String> diccEstado = new HashMap<String, String>();
	private Boolean habLink1 = false;
	private Boolean habLink2 = false;
	private Boolean habLink3 = false;
	private Boolean habLink4 = false;
	private Boolean habLink5 = false;
	private Boolean habLink6 = false;
	private Boolean habLink7 = false;
	private String mensaje;

	public void init() {
		updateEstadoCapa();
		updateTipoCapa();
		cargarCabecera();
		search();
		diccEstado.clear();
		if (mensaje != null && !mensaje.trim().isEmpty()) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mensaje = null;
		}
	}

	public void search() {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		String elWhere = "";
		elWhere += " and Capacitaciones.configuracionUoCab.idConfiguracionUo =  :idUoCab ";
		parametros.put("idUoCab", new QueryValue(nivelEntidadOeeUtil.getIdConfigCab()));
		String sql =
			"select Capacitaciones  from Capacitaciones Capacitaciones, Referencias Referencias "
				+ " where Capacitaciones.tipo = 'CAP_OG290' "
				+ "	and Capacitaciones.activo is true "
				+ " and Referencias.dominio = 'ESTADOS_CAPACITACION' "
				+ " and Referencias.valorNum = Capacitaciones.estado ";

		if (idEstado == null) {
			elWhere +=
				" and (Referencias.descAbrev in ('CARGAR PARTICIPANTES', 'EVALUACION PARTICIPANTES','EVALUACION CAPACITACION', "
					+ "'FINALIZADA', 'CANCELADA') or ( Referencias.descAbrev ='CARGA' AND Capacitaciones.recepcionPostulacion is false ))";
		} else {
			elWhere +=
				" and (( Referencias.descAbrev ='CARGA' AND Referencias.valorNum =:idEstado  AND Capacitaciones.recepcionPostulacion is false ) or (Referencias.descAbrev <>'CARGA' and Referencias.valorNum = :idEstado))";
			parametros.put("idEstado", new QueryValue(idEstado));
		}
		if (idTipo != null) {
			elWhere += " and Capacitaciones.datosEspecificosTipoCap.idDatosEspecificos = :idTipo ";
			parametros.put("idTipo", new QueryValue(idTipo));
		}
		if (capaDesc != null && !capaDesc.isEmpty()) {
			elWhere += " and upper(Capacitaciones.nombre) like :capaDesc ";
			capaDesc = "%" + capaDesc + "%";
			parametros.put("capaDesc", new QueryValue(capaDesc.toUpperCase()));
		}
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
			elWhere += " and  Capacitaciones.configuracionUoDet.idConfiguracionUoDet =  :idUoDet ";
			parametros.put("idUoDet", new QueryValue(nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
		} else if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
			&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().isEmpty()) {
			elWhere += " and  Capacitaciones.configuracionUoDet.idConfiguracionUoDet =  :idUoDet ";
			parametros.put("idUoDet", new QueryValue(new Long(-1)));
		}
		sql = sql + elWhere;
		capaciatcionesListCustom.listaResultados(sql, parametros);
		System.out.println("");
	}

	public String traerEstado(Integer valorNum) {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.valorNum = :valorNum and Referencias.activo is true and Referencias.dominio = 'ESTADOS_CAPACITACION'");
		q.setParameter("valorNum", valorNum);
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getDescAbrev();
		} else
			return "";
	}

	public Boolean habLinks(Capacitaciones capa) {
		String elKey = capa.getEstado().toString();
		if (!diccEstado.containsKey(elKey)) {
			diccEstado.put(elKey, traerEstado(capa.getEstado()));
		}

		// i.
		if (!capa.getRecepcionPostulacion() && diccEstado.get(elKey).equalsIgnoreCase("CARGA")) {
			habLink1 = true;
			habLink2 = habLink3 = habLink4 = habLink5 = habLink6 = habLink7 = false;
		}
		// ii.
		if (diccEstado.get(elKey).equalsIgnoreCase("CARGAR PARTICIPANTES")) {
			habLink1 = true;
			if (existeListaCab(capa.getIdCapacitacion())) {
				// i.
				if (capa.getCargaHoraria() >= traerCaragaHoraria()) {
					habLink3 = true;
					habLink2 = habLink4 = habLink5 = habLink6 = habLink7 = false;
				} else {
					habLink3 = false;
					habLink5 = habLink6 = true;
					habLink2 = habLink4 = habLink7 = false;
				}
			} else {
				// ii.
				habLink3 = true;
				habLink2 = habLink4 = habLink5 = habLink6 = habLink7 = false;
			}
		}
		// iii.
		if (diccEstado.get(elKey).equalsIgnoreCase("EVALUACION PARTICIPANTES")) {
			habLink2 = habLink4 = habLink5 = habLink6 = true;
			habLink1 = habLink3 = habLink7 = false;
		}
		// iv.
		if (diccEstado.get(elKey).equalsIgnoreCase("EVALUACION CAPACITACION")) {
			habLink2 = true;
			// 1.
			if (existeEvalTipo(capa.getIdCapacitacion())) {
				habLink6 = true;
			} else {
				habLink6 = false;
			}
			habLink1 = habLink3 = habLink4 = habLink5 = habLink7 = false;
		}
		// v.
		if (diccEstado.get(elKey).equalsIgnoreCase("FINALIZADA")) {
			habLink2 = habLink7 = true;
			// 1.
			if (existeEvalTipo(capa.getIdCapacitacion())) {
				habLink4 = true;
			} else {
				habLink4 = false;
			}
			habLink1 = habLink3 =  habLink5 = habLink6 = false;
		}
		// vi.
		if (diccEstado.get(elKey).equalsIgnoreCase("CANCELADA")) {
			habLink1 = habLink2 = habLink3 = habLink4 = habLink5 = habLink6 = habLink7 = false;
		}
		return habLink1;
	}

	private Integer traerCaragaHoraria() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.activo is true and Referencias.dominio ='GESTION_CAPACITACION' "
				+ " and Referencias.descAbrev = 'EVAL_PART_OG'");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	private Boolean existeListaCab(Long idCapa) {
		Query q =
			em.createQuery("select ListaCab from ListaCab ListaCab "
				+ "where ListaCab.activo is true and ListaCab.capacitaciones.idCapacitacion = :idCapa");
		q.setParameter("idCapa", idCapa);
		List lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private Boolean existeEvalTipo(Long idCapa) {
		Query q =
			em.createQuery("select EvaluacionTipo from EvaluacionTipo EvaluacionTipo "
				+ " where EvaluacionTipo.activo is true "
				+ " and EvaluacionTipo.capacitaciones.idCapacitacion= :idCapa "
				+ " and EvaluacionTipo.tipo ='EVAL_PART'");
		q.setParameter("idCapa", idCapa);
		List lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean habLink2(Capacitaciones capa) {
		String elKey = capa.getEstado().toString();
		if (!diccEstado.containsKey(elKey)) {
			diccEstado.put(elKey, traerEstado(capa.getEstado()));
		}
		if (diccEstado.get(elKey).equalsIgnoreCase("CANCELADA")) {
			return false;
		}
		if (diccEstado.get(elKey).equalsIgnoreCase("CARGAR PARTICIPANTES")) {
			return true;
		}
		if (!capa.getRecepcionPostulacion() && diccEstado.get(elKey).equalsIgnoreCase("CARGA")) {
			return true;
		}
		return true;
	}

	public void limpiar() {
		idEstado = null;
		idTipo = null;
		capaDesc = null;
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
		nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
		diccEstado.clear();
	}

	private void cargarCabecera() {
		if (nivelEntidadOeeUtil == null) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		}
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

	}

	public void updateTipoCapa() {
		List<DatosEspecificos> lista = traerTipoCapa();
		comboTipoCapaSI = buildSITipoCapa(lista);
	}

	private List<SelectItem> buildSITipoCapa(List<DatosEspecificos> lista) {
		if (comboTipoCapaSI == null)
			comboTipoCapaSI = new ArrayList<SelectItem>();
		else
			comboTipoCapaSI.clear();
		comboTipoCapaSI.add(new SelectItem(null, "TODOS"));
		for (DatosEspecificos o : lista) {
			comboTipoCapaSI.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		}
		return comboTipoCapaSI;
	}

	private List<Referencias> traerEstadoCapa() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.descAbrev in ('CARGA','CARGAR PARTICIPANTES', 'EVALUACION PARTICIPANTES', 'EVALUACION CAPACITACION',"
				+ " 'FINALIZADA', 'CANCELADA') and Referencias.dominio = 'ESTADOS_CAPACITACION' "
				+ " order by Referencias.descAbrev asc ");
		return q.getResultList();
	}

	public void updateEstadoCapa() {
		List<Referencias> lista = traerEstadoCapa();
		comboEstadoCapaSI = buildSIEstadoCapa(lista);
	}

	private List<SelectItem> buildSIEstadoCapa(List<Referencias> lista) {
		if (comboEstadoCapaSI == null)
			comboEstadoCapaSI = new ArrayList<SelectItem>();
		else
			comboEstadoCapaSI.clear();
		comboEstadoCapaSI.add(new SelectItem(null, "TODOS"));
		for (Referencias o : lista) {
			comboEstadoCapaSI.add(new SelectItem(o.getValorNum(), o.getDescAbrev()));
		}
		return comboEstadoCapaSI;
	}

	private List<DatosEspecificos> traerTipoCapa() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ "where DatosEspecificos.datosGenerales.descripcion ='TIPOS DE CAPACITACIONES' and DatosEspecificos.activo is true"
				+ " order by DatosEspecificos.descripcion ");
		return q.getResultList();
	}

	public List<SelectItem> getComboTipoCapaSI() {
		return comboTipoCapaSI;
	}

	public void setComboTipoCapaSI(List<SelectItem> comboTipoCapaSI) {
		this.comboTipoCapaSI = comboTipoCapaSI;
	}

	public List<SelectItem> getComboEstadoCapaSI() {
		return comboEstadoCapaSI;
	}

	public void setComboEstadoCapaSI(List<SelectItem> comboEstadoCapaSI) {
		this.comboEstadoCapaSI = comboEstadoCapaSI;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public Long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}

	public String getCapaDesc() {
		return capaDesc;
	}

	public void setCapaDesc(String capaDesc) {
		this.capaDesc = capaDesc;
	}

	public Boolean getHabLink1() {
		return habLink1;
	}

	public void setHabLink1(Boolean habLink1) {
		this.habLink1 = habLink1;
	}

	public Boolean getHabLink2() {
		return habLink2;
	}

	public void setHabLink2(Boolean habLink2) {
		this.habLink2 = habLink2;
	}

	public Boolean getHabLink3() {
		return habLink3;
	}

	public void setHabLink3(Boolean habLink3) {
		this.habLink3 = habLink3;
	}

	public Boolean getHabLink4() {
		return habLink4;
	}

	public void setHabLink4(Boolean habLink4) {
		this.habLink4 = habLink4;
	}

	public Boolean getHabLink5() {
		return habLink5;
	}

	public void setHabLink5(Boolean habLink5) {
		this.habLink5 = habLink5;
	}

	public Boolean getHabLink6() {
		return habLink6;
	}

	public void setHabLink6(Boolean habLink6) {
		this.habLink6 = habLink6;
	}

	public Boolean getHabLink7() {
		return habLink7;
	}

	public void setHabLink7(Boolean habLink7) {
		this.habLink7 = habLink7;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
