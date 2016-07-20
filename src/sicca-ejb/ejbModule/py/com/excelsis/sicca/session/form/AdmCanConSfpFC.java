package py.com.excelsis.sicca.session.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.CancelacionAutomaticaFC;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Name("admCanConSfpFC")
@Scope(ScopeType.CONVERSATION)
public class AdmCanConSfpFC {
	@In(create = true)
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager", create = true)
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	NivelEntidadOeeUtil nivelEntidadOeeUtil = new NivelEntidadOeeUtil();
	Date fecha = new Date();
	String motivo;
	Long idConcurso;
	private List<SelectItem> concursoSelecItem;
	Excepcion excepcion = new Excepcion();
	private String direccionFisica;
	private Boolean modoEditado = false;
	private String descConcurso;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;

	public void init() {
		nivelEntidadOeeUtil.setEm(em);
		if (modoEditado) {
			nivelEntidadOeeUtil.init();
		} else {
			// MODO VER
			excepcion = em.find(Excepcion.class, excepcion.getIdExcepcion());
			nivelEntidadOeeUtil.setCodSinEntidad(excepcion.getConcurso().getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo());
			nivelEntidadOeeUtil.setNombreSinEntidad(excepcion.getConcurso().getConfiguracionUoCab().getEntidad().getSinEntidad().getEntNombre());
			nivelEntidadOeeUtil.setOrdenUnidOrg(excepcion.getConcurso().getConfiguracionUoCab().getOrden());
			nivelEntidadOeeUtil.setDenominacionUnidad(excepcion.getConcurso().getConfiguracionUoCab().getDenominacionUnidad());
			nivelEntidadOeeUtil.setIdConfigCab(excepcion.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo());
			List<SinNivelEntidad> sin =
				em.createQuery("Select n from SinNivelEntidad n "
					+ " where n.aniAniopre ="
					+ excepcion.getConcurso().getConfiguracionUoCab().getEntidad().getSinEntidad().getAniAniopre()
					+ " and n.nenCodigo="
					+ excepcion.getConcurso().getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo()).getResultList();
			if (!sin.isEmpty()) {
				nivelEntidadOeeUtil.setNombreNivelEntidad(sin.get(0).getNenNombre());
				nivelEntidadOeeUtil.setCodNivelEntidad(sin.get(0).getNenCodigo());

			}
			motivo = excepcion.getObservacion();
			fecha = excepcion.getFechaAlta();
			descConcurso = excepcion.getConcurso().getNombre();
			initCabecera(excepcion.getIdConfiguracionUoBloq());

		}
		updateConcurso();
	}

	private void initCabecera(Long idCOnfUoCab) {
		grupoPuestosController.setIdConfUoCab(idCOnfUoCab);
		grupoPuestosController.findEntidades();
	}

	public String elFrom() {
		if (excepcion != null && excepcion.getIdExcepcion() != null) {
			return "seleccion/admCanConSfp/admCanConSfp?modoEditado=" + modoEditado
				+ "&idExcepcion=" + excepcion.getIdExcepcion();
		}
		return "seleccion/admCanConSfp/admCanConSfp?modoEditado=" + modoEditado;
	}

	private void obtenerDireccionFisica() {
		Calendar cal = Calendar.getInstance();
		if (idConcurso != null && direccionFisica != null && direccionFisica.trim().isEmpty()) {
			Concurso concurso = em.find(Concurso.class, idConcurso);
			direccionFisica =
				"//SICCA//" + cal.get(Calendar.YEAR) + "OEE" + "//"
					+ concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "//"
					+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
					+ concurso.getIdConcurso();
		}
	}

	private Boolean precondSave() {
		if (idConcurso == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("CU313_errFaltaConcurso"));
			return false;
		}
		if(motivo== null || motivo.trim().isEmpty()){			
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("CU313_errFaltaMotivo"));
			return false;
		}
		return true;
	}

	public String save() {
		if (precondSave()) {
			try {
				excepcion = new Excepcion();
				excepcion.setConcurso(new Concurso());
				excepcion.getConcurso().setIdConcurso(idConcurso);
				CancelacionAutomaticaFC cancelacionAutomaticaFC =
					(py.com.excelsis.sicca.seleccion.session.form.CancelacionAutomaticaFC) Component.getInstance(CancelacionAutomaticaFC.class, true);
				cancelacionAutomaticaFC.setCancelarAutomatico(false);
				excepcion =
					cancelacionAutomaticaFC.cancelar(excepcion, "CANCELACION DE CONCURSO POR SFP", "CONCURSO CANCELADO", motivo, fecha, false, true);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("GENERICO_NO_MSG"));
			}
		}
		updateConcurso();
		return "";
	}

	private Boolean precondPublicar() {
		if (excepcion == null || excepcion.getIdExcepcion() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("CU313_errFaltaExcepcion"));
			return false;
		}
		return true;
	}

	public void publicar() {
		if (precondPublicar()) {
			try {
				excepcion.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
				excepcion.setFechaPublicacion(new Date());
				excepcion = em.merge(excepcion);
				enviarMail();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			}
		}
		updateConcurso();
	}

	public void updateConcurso() {
		List<Concurso> lista = getConcurso();
		concursoSelecItem = new ArrayList<SelectItem>();
		buildConcursoSelectItem(lista);
	}

	private List<Concurso> getConcurso() {
		Query q =
			em.createQuery("select Concurso from Concurso Concurso,Referencias Referencias "
				+ " where Concurso.estado = Referencias.valorNum "
				+ " and Referencias.dominio ='ESTADOS_CONCURSO' and Referencias.descAbrev != 'FINALIZADO'"
				+ " and  Concurso.configuracionUoCab.idConfiguracionUo ="
				+ nivelEntidadOeeUtil.getIdConfigCab() + " and Concurso.activo is true");
		List<Concurso> lista = q.getResultList();
		return lista;
	}

	private List<EvalReferencialPostulante> lEvalRefPsot(String inConcursoPuesto) {
		if (inConcursoPuesto != null) {
			Query q =
				em.createQuery("select EvalReferencialPostulante from EvalReferencialPostulante EvalReferencialPostulante "
					+ " where EvalReferencialPostulante.activo is true "
					+ " and EvalReferencialPostulante.postulacion.activo is true "
					+ " and EvalReferencialPostulante.concursoPuestoAgr.idConcursoPuestoAgr in  "
					+ inConcursoPuesto);

			List<EvalReferencialPostulante> lista = q.getResultList();
			return lista;
		}
		return null;

	}

	private String conjuntoInConcursoPuesto(Long idConcurso) {
		Query q =
			em.createQuery("select ConcursoPuestoAgr from ConcursoPuestoAgr ConcursoPuestoAgr "
				+ "where ConcursoPuestoAgr.concurso.idConcurso = " + idConcurso
				+ " and ConcursoPuestoAgr.activo is true ");
		List<ConcursoPuestoAgr> lista = q.getResultList();
		String respuesta = "";
		for (ConcursoPuestoAgr o : lista) {
			respuesta += "," + o.getIdConcursoPuestoAgr();
		}
		if (respuesta.trim().isEmpty()) {
			return null;
		} else {
			respuesta = respuesta.replaceFirst(",", "");
			respuesta = "(" + respuesta + ")";
		}
		return respuesta;
	}

	private void enviarMail() {
		UsuarioPortalFormController usuarioPortalFormController =
			(UsuarioPortalFormController) Component.getInstance(UsuarioPortalFormController.class, true);

		List<EvalReferencialPostulante> lRemitente =
			lEvalRefPsot(conjuntoInConcursoPuesto(idConcurso));
		String dirEmail = null;
		Concurso concurso = em.find(Concurso.class, idConcurso);
		for (EvalReferencialPostulante o : lRemitente) {
			dirEmail = o.getPostulacion().getPersonaPostulante().getPersona().getEMail();
			String asunto = concurso.getNombre() + " CANCELADO POR LA SFP";
			String texto = "";
			try {
				texto =
					texto + "<p align=\"justify\"> <b> Estimado(a) "
						+ o.getPostulacion().getPersonaPostulante().getPersona().getNombres()
						+ o.getPostulacion().getPersonaPostulante().getPersona().getApellidos()
						+ ", " + "<p align=\"justify\">Le comunicamos que el Concurso "
						+ concurso.getNombre() + " ha sido Cancelado.</p>"
						+ "<p align=\"justify\">Motivo: " + motivo + ".</p>"
						+ "<p> Atentamente,</p> "
						+ "<b><p>Secretaria de la Funci&oacute;n Pública</p></b>";

				usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void buildConcursoSelectItem(List<Concurso> lista) {
		if (concursoSelecItem == null)
			concursoSelecItem = new ArrayList<SelectItem>();
		else
			concursoSelecItem.clear();

		concursoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Concurso o : lista) {
			concursoSelecItem.add(new SelectItem(o.getIdConcurso(), o.getNombre()));
		}
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public List<SelectItem> getConcursoSelecItem() {
		return concursoSelecItem;
	}

	public void setConcursoSelecItem(List<SelectItem> concursoSelecItem) {
		this.concursoSelecItem = concursoSelecItem;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public String getDireccionFisica() {
		obtenerDireccionFisica();
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Boolean getModoEditado() {
		return modoEditado;
	}

	public void setModoEditado(Boolean modoEditado) {
		this.modoEditado = modoEditado;
	}

	public String getDescConcurso() {
		return descConcurso;
	}

	public void setDescConcurso(String descConcurso) {
		this.descConcurso = descConcurso;
	}

	public String dirModoEditado() {
		return "&modoEditado=true";
	}

	public String getUrlToPageNivel(String elFrom) {
		return nivelEntidadOeeUtil.getUrlToPageNivel(elFrom);
	}

}
