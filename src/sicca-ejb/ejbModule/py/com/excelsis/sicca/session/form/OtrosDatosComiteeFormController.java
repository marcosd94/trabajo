package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.NumeroLetra;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("otrosDatosComiteeFormController")
public class OtrosDatosComiteeFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;

	// Datos a ser guardados en este link
	private String modalidadVinculacion;
	private String fuenteFinanciacion;
	private String remuneracionBasica;
	private String otrasRemuneraciones;
	private String otrosBeneficios;
	private String horarioTrabajo;
	private String condicionLaboral;
	private String otrasInformaciones;
	private String postulantesHabilitados;
	private String postulantesNoHabilitados;
	private String documentos;
	private Date fechaLimite;
	private String lugar;
	private String contactoAclaraciones;
	private String objetivo;
	private String mision;
	private String misionEspecifica;
	private Boolean habilitar = true;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean carpeta = false;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concurso = new Concurso();

		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = concursoPuestoAgr.getConcurso();
		/* Incidencia 1014 */
		validarOee(concurso);
		/**/
		buscarDatosAsociadosUsuario();

		/************************* <MODIFICACION> *********************************/
		/************************* MODIFICADO: 07/11/2013 *************************/
		/************************* AUTOR: RODRIGO VELAZQUEZ ***********************/
		if(!concurso.getPromocion()){
			if (concursoPuestoAgr.getModalidad() == null
					&& concursoPuestoAgr.getConcursoPuestoDets().get(0)
							.getPlantaCargoDet().getContratado())
				modalidadVinculacion = "CONTRATADO";
			else if (concursoPuestoAgr.getModalidad() == null
					&& concursoPuestoAgr.getConcursoPuestoDets().get(0)
							.getPlantaCargoDet().getPermanente())
				modalidadVinculacion = "PERMANENTE";
			else
				modalidadVinculacion = concursoPuestoAgr.getModalidad();
			fuenteFinanciacion = concursoPuestoAgr.getFuenteFinanciacion();
		
/*		OTRAS REMUNERACIONES 
		Query q;
		q = em.createQuery("select concepto from GrupoConceptoPago concepto "
				+ " where concepto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo"
				+ " order by concepto.categoria asc");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<GrupoConceptoPago> grupoConceptoPagos = (List<GrupoConceptoPago>) q
				.getResultList();
		otrasRemuneraciones = "";
		for (GrupoConceptoPago grupoConceptoPago : grupoConceptoPagos) {
			if (grupoConceptoPago.getObjCodigo() != 111) {
				otrasRemuneraciones = otrasRemuneraciones
						+ "._ Obj. "
						+ grupoConceptoPago.getObjCodigo()
						+ (grupoConceptoPago.getCategoria() == null ? ""
								: " Cat. " + grupoConceptoPago.getCategoria())
						+ " Monto "
						+ grupoConceptoPago.getMonto()
						+ " (Gs. "
						+ new NumeroLetra().Convertir(
								grupoConceptoPago.getMonto(), true) + ") \n";
			}
		}
		 SUELDOS 
		List<PuestoConceptoPago> puestoConceptoPagos = new ArrayList<PuestoConceptoPago>();
		for (ConcursoPuestoDet cp : concursoPuestoAgr.getConcursoPuestoDets()) {
			q = em.createQuery("select concepto from PuestoConceptoPago concepto "
					+ " where concepto.plantaCargoDet.idPlantaCargoDet = :idPuesto"
					+ " order by concepto.categoria asc");
			q.setParameter("idPuesto", cp.getPlantaCargoDet()
					.getIdPlantaCargoDet());
			puestoConceptoPagos.addAll((List<PuestoConceptoPago>) q
					.getResultList());
		}
		// puestoConceptoPagos = quicksort(puestoConceptoPagos);
		remuneracionBasica = "";
		String categoriaActual;

		for (int j = 0; j < puestoConceptoPagos.size(); j++) {
			categoriaActual = puestoConceptoPagos.get(j).getCategoria();
			int i = 0;

			while (puestoConceptoPagos.size() > (j + i)
					&& categoriaActual.compareTo(puestoConceptoPagos.get(j + i)
							.getCategoria()) == 0) {
				i++;
			}
			if (puestoConceptoPagos.get(j).getObjCodigo() == 111) {
				remuneracionBasica = remuneracionBasica
						+ " - "
						+ i
						+ " vacancia(s) de "
						+ (puestoConceptoPagos.get(j).getCategoria() == null ? ""
								: " Categoría "
										+ puestoConceptoPagos.get(j)
												.getCategoria())
						+ " Monto "
						+ puestoConceptoPagos.get(j).getMonto()
						+ " (Gs. "
						+ new NumeroLetra().Convertir(puestoConceptoPagos
								.get(j).getMonto(), true) + ") \n";
			}
			j = j + i - 1;

		}*/

			if (concursoPuestoAgr.getMision() == null)// MODIFICADO RV
				mision = concursoPuestoAgr.getConcursoPuestoDets().get(0)
						.getPlantaCargoDet().getMision();
			else
				mision = concursoPuestoAgr.getMision();
	
			if (concursoPuestoAgr.getCarpeta() == null)
				carpeta = false;
			else
				carpeta = concursoPuestoAgr.getCarpeta();
		/************************* </MODIFICACION> ********************************/
		}
		horarioTrabajo = concursoPuestoAgr.getHorario();
		condicionLaboral = concursoPuestoAgr.getCondLaborales();
		otrasInformaciones = concursoPuestoAgr.getOtrasInf();
		postulantesHabilitados = concursoPuestoAgr.getPostHabilitados();
		postulantesNoHabilitados = concursoPuestoAgr.getPostNoHabilitados();
		documentos = concursoPuestoAgr.getDocumentos();
		fechaLimite = concursoPuestoAgr.getFechaLimite();
		lugar = concursoPuestoAgr.getLugar();
		contactoAclaraciones = concursoPuestoAgr.getContacto();
		objetivo = concursoPuestoAgr.getObjetivo();
		misionEspecifica = concursoPuestoAgr.getMisionEspecifica();
		otrosBeneficios = concursoPuestoAgr.getOtrosBeneficios();
		habilitarPantalla();
	}

	private static List<PuestoConceptoPago> quicksort(
			List<PuestoConceptoPago> puestoConceptoPago) {
		if (puestoConceptoPago.size() <= 1)
			return puestoConceptoPago;
		int pivot = puestoConceptoPago.size() / 2;
		List<PuestoConceptoPago> lesser = new ArrayList<PuestoConceptoPago>();
		List<PuestoConceptoPago> greater = new ArrayList<PuestoConceptoPago>();
		int sameAsPivot = 0;
		for (PuestoConceptoPago puesto : puestoConceptoPago) {
			if (puesto.getCategoria().compareTo(
					puestoConceptoPago.get(pivot).getCategoria()) > 0)
				greater.add(puesto);
			else if (puesto.getCategoria().compareTo(
					puestoConceptoPago.get(pivot).getCategoria()) < 0)
				lesser.add(puesto);
			else
				sameAsPivot++;
		}
		lesser = quicksort(lesser);
		for (int i = 0; i < sameAsPivot; i++)
			lesser.add(puestoConceptoPago.get(pivot));
		greater = quicksort(greater);
		List<PuestoConceptoPago> sorted = new ArrayList<PuestoConceptoPago>();
		for (PuestoConceptoPago puesto : lesser)
			sorted.add(puesto);
		for (PuestoConceptoPago puesto : greater)
			sorted.add(puesto);
		return sorted;
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			Entidad entidad = new Entidad();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	public String guardar() {
		try {

			if (fechaLimite != null) {
				if (!General.isFechaValida(fechaLimite)) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"Fecha Límite inválida. Verifique");
					return null;
				}
			}
			/*
			 * MODIFICADO RV if(mision==null|| mision.trim().equals("")||
			 * objetivo==null ||
			 * objetivo.trim().equals("")||misionEspecifica==null
			 * ||misionEspecifica.trim().equals("")){
			 * statusMessages.add(Severity
			 * .ERROR,"Debe llenar los campos obligatorios"); return null; }
			 */
			if (mision == null || mision.trim().equals("")) {
				statusMessages.add(Severity.ERROR,
						"Debe llenar los campos obligatorios");
				return null;
			}
			concursoPuestoAgr.setModalidad(modalidadVinculacion);
			concursoPuestoAgr.setFuenteFinanciacion(fuenteFinanciacion);
			//concursoPuestoAgr.setRemuneracion(remuneracionBasica);
			//concursoPuestoAgr.setOtrasRemuneraciones(otrasRemuneraciones);
			concursoPuestoAgr.setOtrosBeneficios(otrosBeneficios);
			concursoPuestoAgr.setHorario(horarioTrabajo);
			concursoPuestoAgr.setCondLaborales(condicionLaboral);
			concursoPuestoAgr.setOtrasInf(otrasInformaciones);
			concursoPuestoAgr.setPostHabilitados(postulantesHabilitados);
			concursoPuestoAgr.setPostNoHabilitados(postulantesNoHabilitados);
			concursoPuestoAgr.setDocumentos(documentos);
			concursoPuestoAgr.setFechaLimite(fechaLimite);
			concursoPuestoAgr.setLugar(lugar);
			concursoPuestoAgr.setContacto(contactoAclaraciones);
			concursoPuestoAgr.setMision(mision);
			concursoPuestoAgr.setObjetivo(objetivo);
			concursoPuestoAgr.setMisionEspecifica(misionEspecifica);
			concursoPuestoAgr.setCarpeta(carpeta);// MODIFICADO RV
			concursoPuestoAgrHome.setInstance(concursoPuestoAgr);

			String resultado = concursoPuestoAgrHome.update();
			if (resultado.equals("updated")) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				return "updated";
			}
			return null;

		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public List<SelectItem> recibiranCarpetas() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(true, "Si"));
		listaItem.add(new SelectItem(false, "No"));
		return listaItem;
	}

	private void habilitarPantalla() {
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			Referencias refPendiente = new Referencias();
			refPendiente = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refAjuste
							.getValorNum().intValue())
				habilitar = true;
			else
				habilitar = false;
		}

	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public String getModalidadVinculacion() {
		return modalidadVinculacion;
	}

	public void setModalidadVinculacion(String modalidadVinculacion) {
		this.modalidadVinculacion = modalidadVinculacion;
	}

	public String getFuenteFinanciacion() {
		return fuenteFinanciacion;
	}

	public void setFuenteFinanciacion(String fuenteFinanciacion) {
		this.fuenteFinanciacion = fuenteFinanciacion;
	}

	public String getRemuneracionBasica() {
		return remuneracionBasica;
	}

	public void setRemuneracionBasica(String remuneracionBasica) {
		this.remuneracionBasica = remuneracionBasica;
	}

	public String getOtrosBeneficios() {
		return otrosBeneficios;
	}

	public void setOtrosBeneficios(String otrosBeneficios) {
		this.otrosBeneficios = otrosBeneficios;
	}

	public String getHorarioTrabajo() {
		return horarioTrabajo;
	}

	public void setHorarioTrabajo(String horarioTrabajo) {
		this.horarioTrabajo = horarioTrabajo;
	}

	public String getCondicionLaboral() {
		return condicionLaboral;
	}

	public void setCondicionLaboral(String condicionLaboral) {
		this.condicionLaboral = condicionLaboral;
	}

	public String getOtrasInformaciones() {
		return otrasInformaciones;
	}

	public void setOtrasInformaciones(String otrasInformaciones) {
		this.otrasInformaciones = otrasInformaciones;
	}

	public String getPostulantesHabilitados() {
		return postulantesHabilitados;
	}

	public void setPostulantesHabilitados(String postulantesHabilitados) {
		this.postulantesHabilitados = postulantesHabilitados;
	}

	public String getPostulantesNoHabilitados() {
		return postulantesNoHabilitados;
	}

	public void setPostulantesNoHabilitados(String postulantesNoHabilitados) {
		this.postulantesNoHabilitados = postulantesNoHabilitados;
	}

	public String getDocumentos() {
		return documentos;
	}

	public void setDocumentos(String documentos) {
		this.documentos = documentos;
	}

	public Date getFechaLimite() {
		return fechaLimite;
	}

	public void setFechaLimite(Date fechaLimite) {
		this.fechaLimite = fechaLimite;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getContactoAclaraciones() {
		return contactoAclaraciones;
	}

	public void setContactoAclaraciones(String contactoAclaraciones) {
		this.contactoAclaraciones = contactoAclaraciones;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public String getMision() {
		return mision;
	}

	public void setMision(String mision) {
		this.mision = mision;
	}

	public String getMisionEspecifica() {
		return misionEspecifica;
	}

	public void setMisionEspecifica(String misionEspecifica) {
		this.misionEspecifica = misionEspecifica;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

	public Boolean getCarpeta() {// MODIFICADO RV
		return carpeta;
	}

	public void setCarpeta(Boolean carpeta) {
		this.carpeta = carpeta;
	}

}
