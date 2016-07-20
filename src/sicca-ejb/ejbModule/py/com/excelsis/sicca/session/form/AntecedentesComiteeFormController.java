package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.ConcursoPuestoAgrDTO;
import py.com.excelsis.sicca.entity.AntecedenteGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("antecedentesComiteeFormController")
public class AntecedentesComiteeFormController implements Serializable {

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
	private AntecedenteGrupo antecedenteGrupo;
	private String ultimoDet;
	private Boolean habilitar = true;

	List<AntecedenteGrupo> listaAntecedentes = new ArrayList<AntecedenteGrupo>();

	/**
	 * si recibe del CU 162
	 * **/
	private Long idConcursoPuestoAgr;
	private String fromCU = null;
	private SeguridadUtilFormController seguridadUtilFormController;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concurso = new Concurso();

		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();		
		concurso = concursoPuestoAgr.getConcurso();
		/*Incidencia 1014*/
		validarOee(concurso);
		 /* */
		buscarDatosAsociadosUsuario();
		buscarAntecedentesGrupos();
		habilitarPantalla();
	}
	
	private void habilitarPantalla() {
		habilitar = true;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = true;
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
							.getValorNum().intValue())
				habilitar = true;
			if(concursoPuestoAgr.getEstado().intValue() == refAjuste.getValorNum().intValue())
				habilitar = false;
		}

	}
	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		 
		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	@SuppressWarnings("unchecked")
	private void buscarAntecedentesGrupos() {
		String sql = "select ant.* " + "from seleccion.antecedente_grupo ant "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = ant.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and ant.activo= true";

		listaAntecedentes = new ArrayList<AntecedenteGrupo>();
		listaAntecedentes = em.createNativeQuery(sql, AntecedenteGrupo.class)
				.getResultList();
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

	public void guardar() {
		try {
			if (antecedenteGrupo.getDescripcion() != null
					&& !antecedenteGrupo.getDescripcion().trim().isEmpty()) {
				antecedenteGrupo.setConcursoPuestoAgr(concursoPuestoAgrHome
						.getInstance());
				antecedenteGrupo.setFechaAlta(new Date());
				antecedenteGrupo.setTipo("GRUPO");
				antecedenteGrupo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				antecedenteGrupo.setActivo(true);
				em.persist(antecedenteGrupo);
				em.flush();
				buscarAntecedentesGrupos();
			}
			else{
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Antecedente no cargado. Ingrese la descripción");
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}

	public void editar(Long id) {
		try {
			antecedenteGrupo = new AntecedenteGrupo();
			antecedenteGrupo = em.find(AntecedenteGrupo.class, id);
			ultimoDet = antecedenteGrupo.getDescripcion();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}

	public void actualizar() {
		try {
			if (fromCU != null
					&& fromCU.equals("admPerfilMatrizHomologInstitucion_list")) {
				AntecedenteGrupo aux = new AntecedenteGrupo();// se crea un
																// nuevo
																// registro
				aux.setActivo(true);
				aux.setConcursoPuestoAgr(antecedenteGrupo
						.getConcursoPuestoAgr());
				aux.setDescripcion(antecedenteGrupo.getDescripcion());
				aux.setFechaAlta(new Date());
				aux.setTipo("GRUPO");
				aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(aux);
				em.flush();
				em.clear();
				// se actualiza el siguiente pero sin las modificaciones hechas
				antecedenteGrupo.setDescripcion(ultimoDet);
				antecedenteGrupo.setActivo(false);
				em.merge(antecedenteGrupo);
				em.flush();
				ultimoDet = null;
			} else {
				antecedenteGrupo.setFechaMod(new Date());
				antecedenteGrupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(antecedenteGrupo);
				em.flush();
			}

			buscarAntecedentesGrupos();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}

	public void eliminar(Long id) {
		try {
			antecedenteGrupo = new AntecedenteGrupo();
			antecedenteGrupo = em.find(AntecedenteGrupo.class, id);
			em.remove(antecedenteGrupo);
			em.flush();
			listaAntecedentes.remove(antecedenteGrupo);

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}

	public void nuevo() {
		antecedenteGrupo = new AntecedenteGrupo();
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

	public List<AntecedenteGrupo> getListaAntecedentes() {
		return listaAntecedentes;
	}

	public void setListaAntecedentes(List<AntecedenteGrupo> listaAntecedentes) {
		this.listaAntecedentes = listaAntecedentes;
	}

	public AntecedenteGrupo getAntecedenteGrupo() {
		return antecedenteGrupo;
	}

	public void setAntecedenteGrupo(AntecedenteGrupo antecedenteGrupo) {
		this.antecedenteGrupo = antecedenteGrupo;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}
	public Boolean getHabilitar() {
		return habilitar;
	}
	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}
	
	

}
