package py.com.excelsis.sicca.seleccion.session.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.ExcepcionList;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TipoExcepcion;

@Scope(ScopeType.CONVERSATION)
@Name("lisSolTipExcFC")
public class LisSolTipExcFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	private String concursoFil;
	private Date fechaDesdeFil;
	private Date fechaHastaFil;
	private TipoExcepcion tipoExcepcion;
	private TipoExcepcion tipoExcepcionModal;
	private String nivelCod;
	private String nivelDesc;
	private String entidadCod;
	private String entidadDesc;
	private String oeeCod;
	private String oeeDesc;
	private List<Excepcion> lista;
	private String DIR_CU304 =
		"/seleccion/excepcion/inclusionMiembroCom/CargarInclusionMiembro.xhtml?inclusionfrom=/seleccion/LisSolTipExc/LisSolTipExc.xhtml";
	private String DIR_CU304_VIEW =
		"/seleccion/excepcion/inclusionMiembroCom/VerInclusionMiembro.xhtml";
	private String DIR_CU306 =
		"/seleccion/reemplazoMiembroComite/ReemplazoMiembroEdit.xhtml?from=/seleccion/LisSolTipExc/LisSolTipExc.xhtml";
	private String DIR_CU306_VIEW =
		"/seleccion/reemplazoMiembroComite/ReemplazoMiembro.xhtml?from=/seleccion/LisSolTipExc/LisSolTipExc.xhtml";

	private String DIR_CU308 =
		"/seleccion/excepcion/exclusionPostulante/ExclusionPostulanteEdit.xhtml";
	private String DIR_CU308_VIEW =
		"/seleccion/excepcion/exclusionPostulante/ExclusionPostulante.xhtml";

	private String DIR_CU314 = "/seleccion/bloqueo/BloqueoConcursoGrupoPuesto.xhtml";
	private String DIR_CU314_VIEW = "/seleccion/bloqueo/BloqueoConcursoGrupoPuestoView.xhtml";
	private String DIR_CU317 = "";
	private String DIR_CU234 = "/seleccion/admSolicitudProrroga/SolicitudProrrogaEdit.xhtml";
	private String DIR_CU234_VIEW = "/seleccion/admSolicitudProrroga/SolicitudProrroga.xhtml";
	private String DIR_CU313 = "/seleccion/admCanConSfp/admCanConSfp.xhtml";
	private String DIR_CU313_VIEW = "/seleccion/admCanConSfp/admCanConSfpVer.xhtml";
	private String DIR_CU428_VIEW = "/seleccion/excepcion/cancelacion/AdmAdjuntoCancelacion.xhtml";
	private String DIR_CU316 = " ";
	private String DIR_CU316_VIEW = "/planificacion/admDesVacPla/AdmDesVacPlaVer.xhtml";
	private String DIR_CU315 = "/seleccion/admDesConGruPue/admDesConGruPueEdit.xhtml";
	private String DIR_CU315_VIEW = "/seleccion/admDesConGruPue/admDesConcGruPue.xhtml";
	private String DIR_CU589 = "/excepcionesSeleccion/GenExcIngConcurso/GenExcIngConcurso589.xhtml";
	private String DIR_CU589_VIEW = "/excepcionesSeleccion/GenExcIngConcurso/GenExcIngConcurso589Ver.xhtml";
	private Integer idTipoExcepcion;

	private Long idExecpion;

	public void init() {
		initCab();

	}

	public String irCUVer() {
		Excepcion ex = em.find(Excepcion.class, idExecpion);
		String tipo = ex.getTipo();
		if (tipo.equalsIgnoreCase(TipoExcepcion.INCLUSION_MIEMBRO_CS.getDescripcion())) {
			return DIR_CU304_VIEW + "?excepcion=" + ex.getIdExcepcion();// CU 304-

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.INGRESO_POR_CONCURSO_MERITOS.getDescripcion())
			|| tipo.equalsIgnoreCase(TipoExcepcion.INGRESO_POR_CONCURSO_CPO.getDescripcion())
			|| tipo.equalsIgnoreCase(TipoExcepcion.INGRESO_POR_CONCURSO_SIMPLIFICADO.getDescripcion())
			|| tipo.equalsIgnoreCase(TipoExcepcion.INGRESO_POR_CONCURSO_INSTITUCIONAL.getDescripcion())
			|| tipo.equalsIgnoreCase(TipoExcepcion.INGRESO_POR_CONCURSO_INTERINSTITUCIONAL.getDescripcion())) {
			return DIR_CU589_VIEW + "?idExcepcion=" + ex.getIdExcepcion()+"&from=/seleccion/LisSolTipExc/LisSolTipExc.seam"; // CU 306

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.REEMPLAZO_MIEMBRO_CS.getDescripcion())) {
			return DIR_CU306_VIEW + "?idExcepcion=" + ex.getIdExcepcion(); // CU 306

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.EXCLUSION_MIEMBRO_CS.getDescripcion())) {
			return DIR_CU306_VIEW + "?exclusion=true" + "&idExcepcion=" + ex.getIdExcepcion();// CU 306

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.EXCLUSION_POSTULANTES.getDescripcion())) {
			return DIR_CU308_VIEW + "?from=/seleccion/LisSolTipExc/LisSolTipExc.seam"
				+ "&idExcepcion=" + ex.getIdExcepcion();// CU 308

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.EXCLUSION_POSTULANTES_POR_OEE.getDescripcion())) {
			return DIR_CU308_VIEW + "?from=/seleccion/LisSolTipExc/LisSolTipExc.seam?anexo=true"
				+ "&idExcepcion=" + ex.getIdExcepcion();// CU 308

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.BLOQUEO_CONCURSO.getDescripcion())) {
			return DIR_CU314_VIEW + "?tipoBloqueo=C" + "&idExcepcion=" + ex.getIdExcepcion();

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.BLOQUEO_GRUPO.getDescripcion())) {
			return DIR_CU314_VIEW + "?tipoBloqueo=G" + "&idExcepcion=" + ex.getIdExcepcion();

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.BLOQUEO_PUESTO.getDescripcion())) {
			return DIR_CU314_VIEW + "?tipoBloqueo=P" + "&idExcepcion=" + ex.getIdExcepcion();

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.PRORROGA_PLAZO_TOTAL_CONCURSO_GRUPO.getDescripcion())) {
			return DIR_CU234_VIEW + "?idSolicCab=" + ex.getSolicProrrogaCab().getIdSolicCab()
				+ "&modoVista=true&idExcepcion=" + ex.getIdExcepcion()
				+ "&from=/seleccion/LisSolTipExc/LisSolTipExc.seam";// CU 234

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.CANCELACION_AUTOMATICA.getDescripcion())) {
			return DIR_CU428_VIEW + "?idExcepcion=" + ex.getIdExcepcion();// CU 428

		} else if (tipo.equalsIgnoreCase(TipoExcepcion.CANCELACION_CONCURSO.getDescripcion())) {
			return DIR_CU313_VIEW + "?idExcepcion=" + ex.getIdExcepcion() + "&modoEditado=false"
				+ "&from=seleccion/LisSolTipExc/LisSolTipExc";// CU 313
		} else if (tipo.equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_VACANCIAS_PLANIFICACION.getDescripcion())) {
			return DIR_CU316_VIEW + "?idExcepcionDebloqueo=" + ex.getIdExcepcion();// CU 316
		} else if (tipo.equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_POR_CONCURSO.getDescripcion())
			|| (tipo.equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_POR_GRUPO.getDescripcion()))
			|| (tipo.equalsIgnoreCase(TipoExcepcion.DESBLOQUEO_POR_PUESTO.getDescripcion()))) {
			return DIR_CU315_VIEW + "?idExcepcion=" + ex.getIdExcepcion()
				+ "&from=/seleccion/LisSolTipExc/LisSolTipExc";// CU 316
		}
		return "";

	}

	public String irCU() {
		if (idTipoExcepcion == null || idTipoExcepcion == null) {
			return "";
		}
		switch (idTipoExcepcion) {
		case 1:
			return DIR_CU304;// CU 304

		case 2:
			return DIR_CU306; // CU 306

		case 3:
			return DIR_CU306 + "?exclusion=true";// CU 306

		case 5:
			return DIR_CU234 + "?from=/seleccion/LisSolTipExc/LisSolTipExc.seam"; // CU 234

		case 6:
			return DIR_CU314 + "?tipoBloqueo=C"; // CU 314

		case 7:
			return DIR_CU314 + "?tipoBloqueo=G"; // CU 314

		case 8:
			return DIR_CU314 + "?tipoBloqueo=P"; // CU 314
		case 9:
			return DIR_CU313 + "?modoEditado=true" + "&from=seleccion/LisSolTipExc/LisSolTipExc"; // CU 314

		case 12:
			return DIR_CU308 + "?from=/seleccion/LisSolTipExc/LisSolTipExc.seam"; // CU 308
		case 14:
			return DIR_CU315 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 6; // CU 315
		case 15:
			return DIR_CU315 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 7; // CU 315
		case 16:
			return DIR_CU315 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 8; // CU 315
		case 17:
			return DIR_CU589 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 17; // CU 589
		case 18:
			return DIR_CU589 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 18; // CU 589
		case 19:
			return DIR_CU589 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 19; // CU 589
		case 20:
			return DIR_CU589 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 20; // CU 589
		case 21:
			return DIR_CU589 + "?from=/seleccion/LisSolTipExc/LisSolTipExc" + "&idTipoExcepcion="
				+ 21; // CU 589
		default:
			return "";

		}
	}

	private void initCab() {
		try {
			ConfiguracionUoCab configuracionUoCab =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			entidadCod = configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo().toString();
			entidadDesc = configuracionUoCab.getEntidad().getSinEntidad().getEntNomabre();
			oeeCod = configuracionUoCab.getOrden().toString();
			oeeDesc = configuracionUoCab.getDenominacionUnidad();

			SinNivelEntidad sinNivelEnt =
				(SinNivelEntidad) em.createQuery("Select n from SinNivelEntidad n "
					+ " where n.aniAniopre ="
					+ configuracionUoCab.getEntidad().getSinEntidad().getAniAniopre()
					+ " and n.nenCodigo="
					+ configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo()).getSingleResult();
			nivelCod = sinNivelEnt.getNenCodigo().toString();
			nivelDesc = sinNivelEnt.getNenNombre();
			search();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String formatFecha(Date laFecha) {
		if (laFecha == null) {
			return "-";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(laFecha);
	}

	public String search() {
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		StringBuffer SQL = new StringBuffer();
		StringBuffer elJoin = new StringBuffer();
		StringBuffer elWhere = new StringBuffer();
		elWhere.append(" WHERE Excepcion.activo is true "
			+ "and (Excepcion.estado = 'APROBADO' or Excepcion.estado ='REGISTRADO' or Excepcion.estado ='A SOLICITAR' or Excepcion.estado ='PENDIENTE')"
			+ " and Excepcion.concurso.configuracionUoCab.idConfiguracionUo = "
			+ usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());

		SQL.append("select Excepcion from Excepcion Excepcion ");
		if (concursoFil != null && !concursoFil.trim().isEmpty()) {
			elJoin.append("  JOIN Excepcion.concurso concurso ");
			elWhere.append(" AND concurso.nombre = :concurso ");
			parametros.put("concurso", new QueryValue(concursoFil));
		}
		if (tipoExcepcion != null && tipoExcepcion.getId() != null) {
			elWhere.append(" AND tipo = :tipoExcepcion ");
			parametros.put("tipoExcepcion", new QueryValue(tipoExcepcion.getDescripcion()));
		}
		if (fechaDesdeFil != null && fechaHastaFil != null) {
			if (fechaDesdeFil.getTime() > fechaHastaFil.getTime()) {
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_FEC_DESDE_MAY_FEC_HASTA"));
				return null;
			}
			elWhere.append(" AND fechaAlta between :fechaDesde and :fechaHasta ");
			parametros.put("fechaDesde", new QueryValue(fechaDesdeFil, TemporalType.DATE));
			parametros.put("fechaHasta", new QueryValue(fechaHastaFil, TemporalType.DATE));
		}
		SQL.append(elJoin);
		SQL.append(elWhere);
		ExcepcionList excepcionList =
			(ExcepcionList) Component.getInstance(ExcepcionList.class, true);
		lista = excepcionList.searchExcepciones(SQL.toString(), parametros);
		return "OK";
	}

	public void searchAll() {
		limpiar();
		search();
	}

	public void limpiar() {
		concursoFil = null;
		fechaDesdeFil = null;
		fechaHastaFil = null;
	}

	public List<SelectItem> getTipoExcepcionNuevo() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (TipoExcepcion t : TipoExcepcion.values()) {
			if (t.getId() == null
				|| (t.getId().intValue() != TipoExcepcion.CANCELACION_AUTOMATICA.getId().intValue())
				&& t.getId().intValue() != TipoExcepcion.EXCLUSION_POSTULANTES.getId().intValue()
				&& t.getId().intValue() != TipoExcepcion.DESBLOQUEO_VACANCIAS_PLANIFICACION.getId().intValue())
				si.add(new SelectItem(t.getId(), "" + t.getDescripcion()));

		}
		return si;
	}

	public Date getFechaDesdeFil() {
		return fechaDesdeFil;
	}

	public void setFechaDesdeFil(Date fechaDesdeFil) {
		this.fechaDesdeFil = fechaDesdeFil;
	}

	public Date getFechaHastaFil() {
		return fechaHastaFil;
	}

	public void setFechaHastaFil(Date fechaHastaFil) {
		this.fechaHastaFil = fechaHastaFil;
	}

	public TipoExcepcion getTipoExcepcion() {
		return tipoExcepcion;
	}

	public void setTipoExcepcion(TipoExcepcion tipoExcepcion) {
		this.tipoExcepcion = tipoExcepcion;
	}

	public String getConcursoFil() {
		return concursoFil;
	}

	public void setConcursoFil(String concursoFil) {
		this.concursoFil = concursoFil;
	}

	public String getNivelCod() {
		return nivelCod;
	}

	public void setNivelCod(String nivelCod) {
		this.nivelCod = nivelCod;
	}

	public String getNivelDesc() {
		return nivelDesc;
	}

	public void setNivelDesc(String nivelDesc) {
		this.nivelDesc = nivelDesc;
	}

	public String getOeeCod() {
		return oeeCod;
	}

	public void setOeeCod(String oeeCod) {
		this.oeeCod = oeeCod;
	}

	public String getOeeDesc() {
		return oeeDesc;
	}

	public void setOeeDesc(String oeeDesc) {
		this.oeeDesc = oeeDesc;
	}

	public String getEntidadCod() {
		return entidadCod;
	}

	public void setEntidadCod(String entidadCod) {
		this.entidadCod = entidadCod;
	}

	public String getEntidadDesc() {
		return entidadDesc;
	}

	public void setEntidadDesc(String entidadDesc) {
		this.entidadDesc = entidadDesc;
	}

	public List<Excepcion> getLista() {
		return lista;
	}

	public void setLista(List<Excepcion> lista) {
		this.lista = lista;
	}

	public TipoExcepcion getTipoExcepcionModal() {
		return tipoExcepcionModal;
	}

	public void setTipoExcepcionModal(TipoExcepcion tipoExcepcionModal) {
		this.tipoExcepcionModal = tipoExcepcionModal;
	}

	public Long getIdExecpion() {
		return idExecpion;
	}

	public void setIdExecpion(Long idExecpion) {
		this.idExecpion = idExecpion;
	}

	public Integer getIdTipoExcepcion() {
		return idTipoExcepcion;
	}

	public void setIdTipoExcepcion(Integer idTipoExcepcion) {
		this.idTipoExcepcion = idTipoExcepcion;
	}

}
