package py.com.excelsis.sicca.seleccion.session.form;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;

 
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
 
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ExcepcionGrPuestoList;
import py.com.excelsis.sicca.session.form.AdministrarVacanciasListFormController;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admDesVacPlaFC")
public class AdmDesVacPlaFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	AdministrarVacanciasListFormController administrarVacanciasListFormController;
	private List<ExcepcionGrPuesto> lista;
	private String concursoFil;
	private String codigoUnidOrgDep;
	private String codigoPuesto;
	private PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private List<SelectItem> estadosSI;
	private Integer valorNumEstado;
	private Long idExcepcionGrPuesto;
	private Long idExcepcionDebloqueo;
	private String motivo;
	private Date fechaSistema = new Date();
	private ExcepcionGrPuesto excepcionGrPuestoFind;
	private ExcepcionGrPuesto excepcionGrPuestoVer;
	private String direccionFisica;

	public void init() {
		search();
		estadosSelectItems();
		administrarVacanciasListFormController.setElFrom("planificacion/admDesVacPla/AdmDesVacPla");
		administrarVacanciasListFormController.setConfiguracionUoCab(usuarioLogueado.getConfiguracionUoCab());
		administrarVacanciasListFormController.setSinEntidad(grupoPuestosController.getSinEntidad());
		administrarVacanciasListFormController.setNivelEntidad(grupoPuestosController.getSinNivelEntidad());
	}

 
	public void initEdit() {
		if (idExcepcionGrPuesto != null) {
			excepcionGrPuestoFind = em.find(ExcepcionGrPuesto.class, idExcepcionGrPuesto);
			obtenerDireccionFisica();
			grupoPuestosController.setConcursoPuestoAgr(excepcionGrPuestoFind.getConcursoPuestoAgr());
			grupoPuestosController.findEntidades();
		}
	}

	public void initVer() {
		if (idExcepcionDebloqueo != null) {
			
			Query q =
				em.createQuery("select ExcepcionGrPuesto from ExcepcionGrPuesto  ExcepcionGrPuesto"
					+ " where ExcepcionGrPuesto.excepcionByIdExcepcionDesbloq.idExcepcion = "
					+ idExcepcionDebloqueo + " and ExcepcionGrPuesto.activo is true");
			List<ExcepcionGrPuesto> lista = q.getResultList();
			if (lista.size() > 0) {
				excepcionGrPuestoVer = lista.get(0);
				grupoPuestosController.setConcursoPuestoAgr(excepcionGrPuestoVer.getConcursoPuestoAgr());
				grupoPuestosController.findEntidades();
			}
			obtenerDireccionFisica2();
		}
	}

	private void obtenerDireccionFisica() {
		Calendar cal = Calendar.getInstance();
		if (direccionFisica != null && direccionFisica.trim().isEmpty()) {

			direccionFisica =
				"//SICCA//"
					+ cal.get(Calendar.YEAR)
					+ "OEE"
					+ "//"
					+ excepcionGrPuestoFind.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
					+ "//"
					+ excepcionGrPuestoFind.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
					+ "//"
					+ excepcionGrPuestoFind.getConcursoPuestoAgr().getConcurso().getIdConcurso()
					+ "//" + excepcionGrPuestoFind.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
		}
	}

	private void obtenerDireccionFisica2() {
		Calendar cal = Calendar.getInstance();
		if (direccionFisica != null && direccionFisica.trim().isEmpty()) {

			direccionFisica =
				"//SICCA//"
					+ cal.get(Calendar.YEAR)
					+ "OEE"
					+ "//"
					+ excepcionGrPuestoVer.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
					+ "//"
					+ excepcionGrPuestoVer.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
					+ "//"
					+ excepcionGrPuestoVer.getConcursoPuestoAgr().getConcurso().getIdConcurso()
					+ "//" + excepcionGrPuestoVer.getConcursoPuestoAgr().getIdConcursoPuestoAgr();
		}
	}

	public void limpiar() {
		concursoFil = null;
		valorNumEstado = null;
		administrarVacanciasListFormController.setPlantaCargoDet(null);
		administrarVacanciasListFormController.setConfiguracionUoDet(null);
	}

	private Boolean precondSave() {
		if (idExcepcionGrPuesto == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU316_errFaltaidExcepcionGrPuesto"));
			return false;
		}
		if (motivo == null) {
			return false;
		}
		return true;
	}

	public String save() {
		if (precondSave()) {
			try {
				Date fechaSave = new Date();
				// Se crea la excepcion
				Excepcion excepcion = new Excepcion();
				excepcion.setActivo(true);
				excepcion.setTipo("DESBLOQUEO DE VACANCIAS - PLANIFICACION");
				excepcion.setConcurso(excepcionGrPuestoFind.getConcursoPuestoAgr().getConcurso());
				excepcion.setConcursoPuestoAgr(excepcionGrPuestoFind.getConcursoPuestoAgr());
				excepcion.setEstado("REGISTRADO");
				excepcion.setObservacion(motivo);
				excepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				excepcion.setFechaAlta(fechaSistema);
				em.persist(excepcion);
				// Se crea EXCEPCION_GR_PUESTO
				ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
				excepcionGrPuesto.setActivo(true);
				excepcionGrPuesto.setConcursoPuestoAgr(excepcionGrPuestoFind.getConcursoPuestoAgr());
				excepcionGrPuesto.setPlantaCargoDet(excepcionGrPuestoFind.getPlantaCargoDet());
				excepcionGrPuesto.setExcepcion(excepcion);
				excepcionGrPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				excepcionGrPuesto.setFechaAlta(fechaSave);
				em.persist(excepcionGrPuesto);
				// Se Actualiza excepcionGrPuestoFind
				excepcionGrPuestoFind.setFechaDesbloqueo(fechaSave);
				excepcionGrPuestoFind.setExcepcionByIdExcepcionDesbloq(new Excepcion());
				excepcionGrPuestoFind.getExcepcionByIdExcepcionDesbloq().setIdExcepcion(excepcion.getIdExcepcion());
				em.merge(excepcionGrPuestoFind);
				em.flush();
				System.out.println(excepcionGrPuestoFind.getExcepcionByIdExcepcionDesbloq().getIdConfiguracionUoBloq());
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return "FAIL";
			}
		}
		return "OK";
	}

	public String search() {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		StringBuffer SQL = new StringBuffer();
		StringBuffer elJoin = new StringBuffer();
		StringBuffer elWhere = new StringBuffer();

		elWhere.append(" WHERE Excepcion.activo is true and Excepcion.plantaCargoDet is not null and Excepcion.excepcion.tipo != 'DESBLOQUEO DE VACANCIAS - PLANIFICACION' "
			+ " and Excepcion.excepcion.fechaLimite < :fechaSistema ");
		parametros.put("fechaSistema", new QueryValue(new Date(), TemporalType.DATE));
		SQL.append("select Excepcion from ExcepcionGrPuesto Excepcion ");
		if (concursoFil != null && !concursoFil.trim().isEmpty()) {
			elJoin.append("  JOIN Excepcion.excepcion.concurso concurso ");
			elWhere.append(" AND concurso.nombre like '%" + concursoFil + "%'");
		}
		if (valorNumEstado != null) {
			elJoin.append("  JOIN Excepcion.excepcion excepcion  ");
			elWhere.append(" AND excepcion.tipoBloqueo = " + valorNumEstado.intValue());
		}
		if (administrarVacanciasListFormController.getPlantaCargoDet() != null
			&& administrarVacanciasListFormController.getPlantaCargoDet().getIdPlantaCargoDet() != null) {
			elJoin.append(" JOIN Excepcion.plantaCargoDet plantaCargoDet");
			elWhere.append(" AND plantaCargoDet.idPlantaCargoDet = "
				+ administrarVacanciasListFormController.getPlantaCargoDet().getIdPlantaCargoDet());
		}
		if (administrarVacanciasListFormController.getConfiguracionUoDet() != null
			&& administrarVacanciasListFormController.getConfiguracionUoDet().getIdConfiguracionUoDet() != null
			&& administrarVacanciasListFormController.getPlantaCargoDet() == null
			&& administrarVacanciasListFormController.getPlantaCargoDet().getIdPlantaCargoDet() == null) {
			elWhere.append(" AND Excepcion.plantaCargoDet.configuracionUoDet = "
				+ administrarVacanciasListFormController.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}

		SQL.append(elJoin);
		SQL.append(elWhere);
		ExcepcionGrPuestoList excepcionList =
			(ExcepcionGrPuestoList) Component.getInstance("excepcionGrPuestoList2", true);
		lista = excepcionList.searchExcepcionesGrPuesto(SQL.toString(), parametros);
		return "OK";
	}

	private List<Referencias> getEstados() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias where Referencias.dominio = 'TIPOS_BLOQUEO'  and Referencias.activo is true");
		return q.getResultList();
	}

	public List<SelectItem> estadosSelectItems() {
		if (estadosSI == null) {
			estadosSI = new Vector<SelectItem>();
		} else {
			estadosSI.clear();
		}
		estadosSI.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : getEstados())
			estadosSI.add(new SelectItem(o.getValorNum(), "" + o.getDescLarga()));
		return estadosSI;
	}

	public void searchAll() {
		limpiar();
		search();
	}

	public List<ExcepcionGrPuesto> getLista() {
		return lista;
	}

	public void setLista(List<ExcepcionGrPuesto> lista) {
		this.lista = lista;
	}

	public String getConcursoFil() {
		return concursoFil;
	}

	public void setConcursoFil(String concursoFil) {
		this.concursoFil = concursoFil;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public String getCodigoPuesto() {
		return codigoPuesto;
	}

	public void setCodigoPuesto(String codigoPuesto) {
		this.codigoPuesto = codigoPuesto;
	}

	public List<SelectItem> getEstadosSI() {
		return estadosSI;
	}

	public void setEstadosSI(List<SelectItem> estadosSI) {
		this.estadosSI = estadosSI;
	}

	public Integer getValorNumEstado() {
		return valorNumEstado;
	}

	public void setValorNumEstado(Integer valorNumEstado) {
		this.valorNumEstado = valorNumEstado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getFechaSistema() {
		return fechaSistema;
	}

	public void setFechaSistema(Date fechaSistema) {
		this.fechaSistema = fechaSistema;
	}

	public ExcepcionGrPuesto getExcepcionGrPuestoFind() {
		return excepcionGrPuestoFind;
	}

	public void setExcepcionGrPuestoFind(ExcepcionGrPuesto excepcionGrPuestoFind) {
		this.excepcionGrPuestoFind = excepcionGrPuestoFind;
	}

	public Long getIdExcepcionGrPuesto() {
		return idExcepcionGrPuesto;
	}

	public void setIdExcepcionGrPuesto(Long idExcepcionGrPuesto) {
		this.idExcepcionGrPuesto = idExcepcionGrPuesto;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Long getIdExcepcionDebloqueo() {
		return idExcepcionDebloqueo;
	}

	public void setIdExcepcionDebloqueo(Long idExcepcionDebloqueo) {
		this.idExcepcionDebloqueo = idExcepcionDebloqueo;
	}

	public ExcepcionGrPuesto getExcepcionGrPuestoVer() {
		return excepcionGrPuestoVer;
	}

	public void setExcepcionGrPuestoVer(ExcepcionGrPuesto excepcionGrPuestoVer) {
		this.excepcionGrPuestoVer = excepcionGrPuestoVer;
	}

}
