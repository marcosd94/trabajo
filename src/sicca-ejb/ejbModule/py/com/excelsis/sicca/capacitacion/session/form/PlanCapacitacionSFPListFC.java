package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.capacitacion.session.PlanCapacitacionList;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("planCapacitacionSFPListFC")
public class PlanCapacitacionSFPListFC implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7628706301213459922L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	PlanCapacitacionList planCapacitacionList;

	private Long anio;
	private Long nro;
	private Boolean primeraEntrada = true;

	public void init() {

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);

			Date fechaActual = new Date();
			Integer an = fechaActual.getYear() + 1900;
			anio = new Long(an.toString());

			if (usuarioLogueado.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
			cargarCabecera();

		}

		search();
	}

	

	public void search() {
		planCapacitacionList.getPlanCapacitacion().setAnio(anio);
		planCapacitacionList.getPlanCapacitacion().setNro(nro);
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			SinNivelEntidad sin = em.find(SinNivelEntidad.class,
					nivelEntidadOeeUtil.getIdSinNivelEntidad());
			planCapacitacionList.setNenCodigo(sin.getNenCodigo());
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			SinEntidad sin = em.find(SinEntidad.class,
					nivelEntidadOeeUtil.getIdSinEntidad());
			planCapacitacionList.setNenCodigo(sin.getNenCodigo());
		}
		planCapacitacionList.setIdConfiguracionUoDet(nivelEntidadOeeUtil
				.getIdUnidadOrganizativa());
		planCapacitacionList.listaResultados();
	}

	public void searchAll() {
		nivelEntidadOeeUtil.limpiar();
		if (usuarioLogueado.getConfiguracionUoDet() != null) {
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());
			planCapacitacionList.setIdConfiguracionUoDet(nivelEntidadOeeUtil
					.getIdUnidadOrganizativa());
			cargarCabecera();
		}

		Date fechaActual = new Date();
		Integer an = fechaActual.getYear() + 1900;
		anio = new Long(an.toString());
		nro = null;

		search();

	}

	public void cargarCabecera() {

		// Nivel

		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad = em.find(SinEntidad.class, enti.getSinEntidad()
				.getIdSinEntidad());
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(enti
				.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	public Long getAnio() {
		return anio;
	}

	public void setAnio(Long anio) {
		this.anio = anio;
	}

	public Long getNro() {
		return nro;
	}

	public void setNro(Long nro) {
		this.nro = nro;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

}
