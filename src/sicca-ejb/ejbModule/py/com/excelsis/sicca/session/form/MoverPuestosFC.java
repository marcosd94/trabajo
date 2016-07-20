package py.com.excelsis.sicca.session.form;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("moverPuestosFC")
@Scope(ScopeType.CONVERSATION)
public class MoverPuestosFC {
	@In(create = true)
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager", create = true)
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	NivelEntidadOeeUtil nivelEntidadOeeUtilOrigen = new NivelEntidadOeeUtil();
	NivelEntidadOeeUtil nivelEntidadOeeUtilDestino = new NivelEntidadOeeUtil();
	private List<PlantaCargoDet> lPlantaCargoDetOrigen;

	String ordenOEEDepOrigen;
	private Boolean selTodo = false;

	Long ultUnidadOrg = new Long(-1);

	public void init() {

		nivelEntidadOeeUtilOrigen.setEm(em);
		nivelEntidadOeeUtilDestino.setEm(em);
		nivelEntidadOeeUtilDestino.setVarDestinoConfigUo("configuracionUoDetIdConfiguracionUoDet2");
		nivelEntidadOeeUtilDestino.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilDestino.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilDestino.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
		nivelEntidadOeeUtilDestino.setVarDestinoUnidadOrg("configuracionUoDetIdConfiguracionUoDet2");
		if (nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa() != null
			&& nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa().longValue() != ultUnidadOrg.longValue()) {
			lPlantaCargoDetOrigen = cargarOrigen();
			ultUnidadOrg = nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa();

		}
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();

	}

	public void selectAll() {
		if (lPlantaCargoDetOrigen != null) {
			if (selTodo)
				for (PlantaCargoDet o : lPlantaCargoDetOrigen) {
					o.setSelected(true);
				}
			else {
				for (PlantaCargoDet o : lPlantaCargoDetOrigen) {
					o.setSelected(false);
				}
			}
		}
	}

	public String findNivelEntidadOrigen() {
		nivelEntidadOeeUtilOrigen.findNivelEntidad();
		nivelEntidadOeeUtilOrigen.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String findNivelEntidadDestino() {
		nivelEntidadOeeUtilDestino.findNivelEntidad();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String findEntidadOrigen() {
		nivelEntidadOeeUtilOrigen.findEntidad();
		nivelEntidadOeeUtilOrigen.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String findEntidadDestino() {
		nivelEntidadOeeUtilDestino.findEntidad();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String findUnidadOrganizativaOrigen() {
		nivelEntidadOeeUtilOrigen.findUnidadOrganizativa();
		nivelEntidadOeeUtilOrigen.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String findUnidadOrganizativaDestino() {
		nivelEntidadOeeUtilDestino.findUnidadOrganizativa();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String obtenerUnidadOrganizativaDepOrigen() {
		nivelEntidadOeeUtilOrigen.obtenerUnidadOrganizativaDep();
		nivelEntidadOeeUtilOrigen.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	public String obtenerUnidadOrganizativaDepDestino() {
		nivelEntidadOeeUtilDestino.obtenerUnidadOrganizativaDep();
		nivelEntidadOeeUtilOrigen.init();
		nivelEntidadOeeUtilDestino.init();
		lPlantaCargoDetOrigen = cargarOrigen();
		return "";
	}

	private Boolean precondSave() {
		if (nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa() == null
			|| nivelEntidadOeeUtilDestino.getIdUnidadOrganizativa() == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_OBLIGATORIO"));
			return false;
		}
		if (nivelEntidadOeeUtilDestino.getIdUnidadOrganizativa().longValue() == nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa().longValue()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU398_errOrigenIgualDestino"));
			return false;
		}

		if (lPlantaCargoDetOrigen == null || lPlantaCargoDetOrigen.size() < 1) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU398_errCargarDestino"));
			return false;
		}else{
			Boolean noHaySeleccionados = true;
			for (PlantaCargoDet o : lPlantaCargoDetOrigen) {
				if(o.isSelected()){
					return true;
				}
			}
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU398_errCargarDestino"));
			return false;
		}
		

	}

	public String save() {
		if (precondSave()) {
			try {
				// Cambiar los id de las cabeceras del destino

				for (PlantaCargoDet o : lPlantaCargoDetOrigen) {
					if (o.isSelected()) {
						o.setSelected(false);
						o.setConfiguracionUoDet(new ConfiguracionUoDet());
						o.getConfiguracionUoDet().setIdConfiguracionUoDet(nivelEntidadOeeUtilDestino.getIdUnidadOrganizativa());
						o.setFechaMod(new Date());
						o.setUsuMod(usuarioLogueado.getCodigoUsuario());
						o = em.merge(o);
					}
				}
				em.flush();

				lPlantaCargoDetOrigen = null;
				nivelEntidadOeeUtilDestino.init();
				nivelEntidadOeeUtilOrigen.init();

				lPlantaCargoDetOrigen = cargarOrigen();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				e.printStackTrace();

			}
		} else {

			nivelEntidadOeeUtilDestino.init();
			nivelEntidadOeeUtilOrigen.init();
			lPlantaCargoDetOrigen = cargarOrigen();

		}

		return "OK";
	}

	public void limpiar() {
		nivelEntidadOeeUtilOrigen = new NivelEntidadOeeUtil();
		nivelEntidadOeeUtilDestino = new NivelEntidadOeeUtil();

		lPlantaCargoDetOrigen = null;
	}

	public List<PlantaCargoDet> cargarOrigen() {
		if (nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa() != null) {
			Query q =
				em.createQuery("select PlantaCargoDet from PlantaCargoDet PlantaCargoDet "
					+ " where PlantaCargoDet.configuracionUoDet.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtilOrigen.getIdUnidadOrganizativa()
					+ " and PlantaCargoDet.estadoCab.descripcion = 'VACANTE'"
					+ " and PlantaCargoDet.activo is true "
					+ " order by PlantaCargoDet.descripcion");
			List<PlantaCargoDet> lista = q.getResultList();

			return lista;
		}
		return null;

	}

	private void obtnerOrdenOEEDep() {
		ordenOEEDepOrigen =
			nivelEntidadOeeUtilOrigen.getNombreNivelEntidad() + "."
				+ nivelEntidadOeeUtilOrigen.getNombreSinEntidad() + "."
				+ nivelEntidadOeeUtilOrigen.getOrdenUnidOrg();
	}

	public List<PlantaCargoDet> getlPlantaCargoDetOrigen() {
		return lPlantaCargoDetOrigen;
	}

	public void setlPlantaCargoDetOrigen(List<PlantaCargoDet> lPlantaCargoDetOrigen) {
		this.lPlantaCargoDetOrigen = lPlantaCargoDetOrigen;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilDestino() {
		return nivelEntidadOeeUtilDestino;
	}

	public void setNivelEntidadOeeUtilDestino(NivelEntidadOeeUtil nivelEntidadOeeUtilDestino) {
		this.nivelEntidadOeeUtilDestino = nivelEntidadOeeUtilDestino;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilOrigen() {
		return nivelEntidadOeeUtilOrigen;
	}

	public void setNivelEntidadOeeUtilOrigen(NivelEntidadOeeUtil nivelEntidadOeeUtilOrigen) {
		this.nivelEntidadOeeUtilOrigen = nivelEntidadOeeUtilOrigen;
	}

	public Boolean getSelTodo() {
		return selTodo;
	}

	public void setSelTodo(Boolean selTodo) {
		this.selTodo = selTodo;
	}

}
