package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.DatosGrupoPuestoHome;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("datosGrupoPuestoFormController")
public class DatosGrupoPuestoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3536773346937197789L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	DatosGrupoPuestoHome datosGrupoPuestoHome;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	Long idConcursoPuestoAgr;
	private Boolean mostrarPanelPrefePersonDisc = false;
	private String concurso;
	private String grupo;
	private String minimoAplicar;
	private String seleccionAdjudicados;
	private String seleccionPersonaConDisc;
	private Integer minimoEvaluacion;
	private String titulo;
	private String observacion;
	private Boolean isCS = false;
	private Boolean isCIO = false;
	private Boolean isDESPRE = false;
	private String permanente = null;
	private Boolean habilitar = true;

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	private DatosGrupoPuesto datosGrupoPuesto;

	public void setDatosGrupoPuesto(DatosGrupoPuesto datosGrupoPuesto) {
		this.datosGrupoPuesto = datosGrupoPuesto;
	}

	public String errorMinimo = null;

	public static final String CODIGO_POR_EVALUACION = "CE";
	public static final String CODIGO_FIN_EVALUACION = "FE";
	public static final String CODIGO_TERNA = "T";
	public static final String CODIGO_PUNTAJE = "P";
	private SeguridadUtilFormController seguridadUtilFormController;

	public void init() {
		errorMinimo = null;
		if (idConcursoPuestoAgr != null) {
			// EDIT
			cargarDatos();
			datosGrupoPuestoHome.setInstance(datosGrupoPuesto);
			initBools();
			if (datosGrupoPuesto.getConcursoPuestoAgr().getConcurso().getPcd() == null
				|| !datosGrupoPuesto.getConcursoPuestoAgr().getConcurso().getPcd()) {
				mostrarPanelPrefePersonDisc = true;
			} else {
				mostrarPanelPrefePersonDisc = false;
			}
			habilitarPantalla();
		} else {
			statusMessages.add(Severity.ERROR, "Grupo de Puestos no válidos");
		}
	}

	private void initBools() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		Concurso concurso = concursoPuestoAgr.getConcurso();
		if (concurso.getDatosEspecificosTipoConc().getDescripcion().equalsIgnoreCase("CONCURSO SIMPLIFICADO")) {
			isCS = true;
		} else {
			isCS = false;
		}
		if (concurso.getDatosEspecificosTipoConc().getDescripcion().equalsIgnoreCase("CONCURSO INTERNO DE OPOSICION INSTITUCIONAL")) {
			isCIO = true;
			this.permanente = "P";
		} else {
			isCIO = false;
		}
		if (concurso.getDesprecarizacion() != null && concurso.getDesprecarizacion()) {
			isDESPRE = true;
			this.permanente = "C";
		} else {
			isDESPRE = false;
		}

	}

	private void validarOee(Concurso concurso) {
		/* Incidencia 1014 */
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
		/**/
	}

	private void cargarDatos() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		grupo = concursoPuestoAgr.getDescripcionGrupo();
		concurso = concursoPuestoAgr.getConcurso().getNombre();
		/* Incidencia 1014 */
		validarOee(concursoPuestoAgr.getConcurso());
		/**/

		datosGrupoPuesto = getDatosGrupoPuesto();

		if (datosGrupoPuesto == null) {
			datosGrupoPuesto = new DatosGrupoPuesto();
		} else {
			if (datosGrupoPuesto.getPorMinPorEvaluacion() == null)
				minimoAplicar = CODIGO_FIN_EVALUACION;
			else
				minimoAplicar = CODIGO_POR_EVALUACION;
			if (datosGrupoPuesto.getPreferenciaPersDiscapacidad() != null
				&& datosGrupoPuesto.getPreferenciaPersDiscapacidad()) {
				seleccionPersonaConDisc = "SI";
			} else if (datosGrupoPuesto.getPreferenciaPersDiscapacidad() != null
				&& !datosGrupoPuesto.getPreferenciaPersDiscapacidad()) {
				seleccionPersonaConDisc = "NO";
			}
			if (datosGrupoPuesto.getTituloAPublicar() != null) {
				titulo = datosGrupoPuesto.getTituloAPublicar();
			}
			if (datosGrupoPuesto.getObservacionAPublicar() != null) {
				observacion = datosGrupoPuesto.getObservacionAPublicar();
			}
			if (datosGrupoPuesto.getTerna())
				seleccionAdjudicados = CODIGO_TERNA;
			else
				seleccionAdjudicados = CODIGO_PUNTAJE;
			minimoEvaluacion = datosGrupoPuesto.getPorcMinimo();
		}
		datosGrupoPuesto.setConcursoPuestoAgr(concursoPuestoAgr);
		if(concursoPuestoAgr.getPermanente()!= null && concursoPuestoAgr.getPermanente()){
			permanente = "P";	
		}else if(concursoPuestoAgr.getContratado()!= null && concursoPuestoAgr.getContratado()){
			permanente = "C";	
		}		
	}

	@SuppressWarnings("unchecked")
	public DatosGrupoPuesto getDatosGrupoPuesto() {
		String consulta =
			" select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto "
				+ " where datosGrupoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idConcursoPuestoAgr";

		Query q = em.createQuery(consulta);
		q.setParameter("idConcursoPuestoAgr", idConcursoPuestoAgr);
		List<DatosGrupoPuesto> lista = q.getResultList();
		if (lista != null && lista.size() > 0)
			return lista.get(0);

		return null;
	}

	/**
	 * Obtiene una actividad_proceso
	 * 
	 * @param actividad
	 * @param proceso
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActividadProceso buscarActividadProceso(String actividad, String proceso) {
		try {
			String cad =
				" select ap.* " + " from proceso.actividad_proceso ap " +

				" join proceso.actividad a " + " on a.id_actividad = ap.id_actividad " +

				" join proceso.proceso p " + " on p.id_proceso = ap.id_proceso " +

				" where upper(a.descripcion) = upper('" + actividad.toUpperCase() + "') "
					+ "      and upper(p.descripcion) = upper('" + proceso.toUpperCase() + "')";

			List<ActividadProceso> lista =
				em.createNativeQuery(cad, ActividadProceso.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean isNew() {
		if (datosGrupoPuesto == null || datosGrupoPuesto.getIdDatosGrupoPuesto() == null)
			return true;

		return false;
	}

	public void publicacionPortal(Boolean nuevo, String texto) {
		PublicacionPortal entity = null;
		if (nuevo) {
			entity = new PublicacionPortal();
			entity.setActivo(true);
			entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			entity.setFechaAlta(new Date());
			entity.setConcurso(new Concurso());
			entity.getConcurso().setIdConcurso(datosGrupoPuesto.getConcursoPuestoAgr().getConcurso().getIdConcurso());
			entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
			entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(datosGrupoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			entity.setObservacion(true);
			entity.setTexto(texto);
			em.persist(entity);
		} else {
			Query q =
				em.createQuery("select PublicacionPortal from PublicacionPortal PublicacionPortal "
					+ " where PublicacionPortal.concurso.idConcurso = "
					+ datosGrupoPuesto.getConcursoPuestoAgr().getConcurso().getIdConcurso()
					+ " and PublicacionPortal.concursoPuestoAgr.idConcursoPuestoAgr = "
					+ datosGrupoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr()
					+ " and PublicacionPortal.observacion is true");
			List<PublicacionPortal> lista = q.getResultList();
			if (lista.size() == 1) {
				entity = lista.get(0);
				entity.setTexto(texto);
				entity.setUsuMod(usuarioLogueado.getCodigoUsuario());
				entity.setFechaMod(new Date());
				em.merge(entity);
				em.flush();
			} else if (lista.size() == 0) {
				entity = new PublicacionPortal();
				entity.setActivo(true);
				entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				entity.setFechaAlta(new Date());
				entity.setConcurso(new Concurso());
				entity.getConcurso().setIdConcurso(datosGrupoPuesto.getConcursoPuestoAgr().getConcurso().getIdConcurso());
				entity.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				entity.getConcursoPuestoAgr().setIdConcursoPuestoAgr(datosGrupoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
				entity.setTexto(texto);
				entity.setObservacion(true);
				em.persist(entity);
				em.flush();
			}
		}

	}

	public String genTextoPublicacion() {
		ConcursoPuestoAgr concursoPuestoAgr =  em.find(ConcursoPuestoAgr.class, datosGrupoPuesto.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		datosGrupoPuesto.setConcursoPuestoAgr(concursoPuestoAgr);
		String texto;
		String hr = "<hr>";
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdfFecha.format(new Date()).toString();
		String h1O = "<h1>";
		String h1C = "</h1>";
		String h2O = "<h2>";
		String h2C = "</h2>";
		String spanO = "<span>";
		String spanC = "</span>";
		String br = "</br>";
		texto = hr + fechaPublicacion + h2O + concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getDenominacionUnidad() + h2C;
		texto = texto + h2O + concursoPuestoAgr.getConcurso().getNombre() + h2C;
		texto = texto + h2O + datosGrupoPuesto.getConcursoPuestoAgr().getDescripcionGrupo() + h2C;
		if (datosGrupoPuesto.getTituloAPublicar() != null
			&& !datosGrupoPuesto.getTituloAPublicar().trim().isEmpty()) {
			texto = texto + h1O + datosGrupoPuesto.getTituloAPublicar() + h1C;
		}
		if (datosGrupoPuesto.getObservacionAPublicar() != null
			&& !datosGrupoPuesto.getObservacionAPublicar().trim().isEmpty()) {
			texto = texto + spanO + datosGrupoPuesto.getObservacionAPublicar() + spanC;
		}
		return texto;
	}

	public String save() {
		try {
			if (!validar()) {
				return null;
			}

			if (CODIGO_POR_EVALUACION.equals(minimoAplicar)) {
				datosGrupoPuesto.setPorMinPorEvaluacion(true);
				datosGrupoPuesto.setPorMinFinEvaluacion(null);
			} else {
				datosGrupoPuesto.setPorMinPorEvaluacion(null);
				datosGrupoPuesto.setPorMinFinEvaluacion(true);
			}
			if (isCS) {
				datosGrupoPuesto.setTerna(false);
				datosGrupoPuesto.setMerito(true);
			} else {
				if (CODIGO_TERNA.equals(seleccionAdjudicados)) {
					datosGrupoPuesto.setTerna(true);
					datosGrupoPuesto.setMerito(false);
				} else {
					datosGrupoPuesto.setTerna(false);
					datosGrupoPuesto.setMerito(true);
				}
			}
			// /FF

			datosGrupoPuesto.setPorcMinimo(minimoEvaluacion);
			datosGrupoPuesto.setActivo(true);
			datosGrupoPuesto.setTituloAPublicar(titulo);
			datosGrupoPuesto.setObservacionAPublicar(observacion);

			if (seleccionPersonaConDisc != null && seleccionPersonaConDisc.equalsIgnoreCase("SI"))
				datosGrupoPuesto.setPreferenciaPersDiscapacidad(true);
			else
				datosGrupoPuesto.setPreferenciaPersDiscapacidad(false);
			if (isNew()) {
				// nuevo.
				datosGrupoPuesto.setFechaAlta(new Date());
				datosGrupoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(datosGrupoPuesto);
				publicacionPortal(true, genTextoPublicacion());
			} else {
				// editar
				datosGrupoPuesto.setFechaMod(new Date());
				datosGrupoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				datosGrupoPuesto = em.merge(datosGrupoPuesto);
				publicacionPortal(false, genTextoPublicacion());
			}
			/* Incidencia 0001613 */
			if (isCIO && !isDESPRE) {
				if (permanente.equalsIgnoreCase("P")) {
					datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(true);
					datosGrupoPuesto.getConcursoPuestoAgr().setContratado(false);
					datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
					datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
				} else {
					datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(false);
					datosGrupoPuesto.getConcursoPuestoAgr().setContratado(true);
					datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
					datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
				}
			} else if (isDESPRE) {
				datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(false);
				datosGrupoPuesto.getConcursoPuestoAgr().setContratado(true);
				datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
				datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
			} else {
				datosGrupoPuesto.getConcursoPuestoAgr().setPermanente(null);
				datosGrupoPuesto.getConcursoPuestoAgr().setContratado(null);
				datosGrupoPuesto.getConcursoPuestoAgr().setFechaMod(new Date());
				datosGrupoPuesto.getConcursoPuestoAgr().setUsuMod(usuarioLogueado.getCodigoUsuario());
			}
			em.merge(datosGrupoPuesto.getConcursoPuestoAgr());
			/********************/
			em.flush();
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "persisted";
	}

	public boolean validar() {
		if (minimoEvaluacion.compareTo(new Integer(0)) <= 0) {
			errorMinimo = "% Mínimo debe ser Mayor a 0";
			statusMessages.add(Severity.ERROR, errorMinimo);
			return false;
		} else if (minimoEvaluacion.compareTo(new Integer(100)) > 0) {
			errorMinimo = "% Mínimo debe ser Menor o igual a 100";
			statusMessages.add(Severity.ERROR, errorMinimo);
			return false;
		}
		if (mostrarPanelPrefePersonDisc) {
			if (seleccionPersonaConDisc == null) {
				statusMessages.add(Severity.ERROR, "Debe seleccionar uan opción en: Con Preferecia Persona con Discapacidad.");
				return false;
			}
		}
		if (titulo.length() > 200) {
			statusMessages.add(Severity.ERROR, "El Título puede tener hasta 200 caracteres.");
			return false;
		}
		if (observacion.length() > 1000) {
			statusMessages.add(Severity.ERROR, "La Observación puede tener hasta 1000 caracteres.");
			return false;
		}
		if (isCIO && !isDESPRE) {
			if (permanente == null) {
				statusMessages.add(Severity.ERROR, "Debe seleccionar si es Permanente o Contratado");
				return false;
			}
		}

		return true;
	}

	public List<SelectItem> minimoAplicarValueItems() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(CODIGO_POR_EVALUACION, "Por cada Evaluación"));
		listaItem.add(new SelectItem(CODIGO_FIN_EVALUACION, "Al finalizar las Evaluaciones"));
		return listaItem;
	}

	public List<SelectItem> permanenteContratadoValueItems() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem("P", "Permanente"));
		listaItem.add(new SelectItem("C", "Contratado"));
		return listaItem;
	}

	public List<SelectItem> seleccionAdjudicadosValueItems() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(CODIGO_TERNA, "Por Terna o Dupla"));
		listaItem.add(new SelectItem(CODIGO_PUNTAJE, "Por Puntaje"));
		return listaItem;
	}

	public List<SelectItem> seleccionPersonasConDiscValueItems() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem("SI", "SI"));
		listaItem.add(new SelectItem("NO", "NO"));
		return listaItem;
	}
	
	private void habilitarPantalla() {
		habilitar = false;
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr); 
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue())
				habilitar = true;
			else
				habilitar = false;
		}

	}


	public String getConcurso() {
		return concurso;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getMinimoAplicar() {
		return minimoAplicar;
	}

	public void setMinimoAplicar(String minimoAplicar) {
		this.minimoAplicar = minimoAplicar;
	}

	public String getSeleccionAdjudicados() {
		return seleccionAdjudicados;
	}

	public void setSeleccionAdjudicados(String seleccionAdjudicados) {
		this.seleccionAdjudicados = seleccionAdjudicados;
	}

	public Integer getMinimoEvaluacion() {
		return minimoEvaluacion;
	}

	public void setMinimoEvaluacion(Integer minimoEvaluacion) {
		this.minimoEvaluacion = minimoEvaluacion;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getErrorMinimo() {
		return errorMinimo;
	}

	public void setErrorMinimo(String errorMinimo) {
		this.errorMinimo = errorMinimo;
	}

	public String getSeleccionPersonaConDisc() {
		return seleccionPersonaConDisc;
	}

	public void setSeleccionPersonaConDisc(String seleccionPersonaConDisc) {
		this.seleccionPersonaConDisc = seleccionPersonaConDisc;
	}

	public Boolean getMostrarPanelPrefePersonDisc() {
		return mostrarPanelPrefePersonDisc;
	}

	public void setMostrarPanelPrefePersonDisc(Boolean mostrarPanelPrefePersonDisc) {
		this.mostrarPanelPrefePersonDisc = mostrarPanelPrefePersonDisc;
	}

	public Boolean getIsCS() {
		return isCS;
	}

	public void setIsCS(Boolean isCS) {
		this.isCS = isCS;
	}

	public Boolean getIsCIO() {
		return isCIO;
	}

	public void setIsCIO(Boolean isCIO) {
		this.isCIO = isCIO;
	}

	public Boolean getIsDESPRE() {
		return isDESPRE;
	}

	public void setIsDESPRE(Boolean isDESPRE) {
		this.isDESPRE = isDESPRE;
	}

	public String getPermanente() {
		return permanente;
	}

	public void setPermanente(String permanente) {
		this.permanente = permanente;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}
	
	

}
