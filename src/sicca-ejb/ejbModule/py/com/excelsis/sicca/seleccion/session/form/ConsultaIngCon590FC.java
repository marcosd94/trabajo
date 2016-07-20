package py.com.excelsis.sicca.seleccion.session.form;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.ListarConfiguracionUoDetFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("consultaIngCon590FC")
public class ConsultaIngCon590FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	ListarConfiguracionUoDetFormController listarConfiguracionUoDetFormController;

	private Long idConcurso;
	private List<SelectItem> concursosSI;
	private List<ConcursoPuestoAgr> lGrupos;
	private List<EmpleadoPuesto> lEmpleadoPuesto;
	private List<EmpleadoConceptoPago> lEmpleadoConceptoPago;
	private List<ReferenciaAdjuntos> lActoAdmin;
	private String codPuesto;
	private String descpuesto;
	private String descUoDet;
	private String codUoDet;

	public String getCodPuesto() {
		return codPuesto;
	}

	public void setCodPuesto(String codPuesto) {
		this.codPuesto = codPuesto;
	}

	public String getDescpuesto() {
		return descpuesto;
	}

	public void setDescpuesto(String descpuesto) {
		this.descpuesto = descpuesto;
	}

	private Persona persona;
	private Long idPersona;
	private Long idEmpleadoPuesto;

	public void init() {
		cargarCabecera(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
	}

	private void cargarCabecera(Long idUoCab) {
		if (idUoCab != null) {
			nivelEntidadOeeUtil.init();
			nivelEntidadOeeUtil.setIdConfigCab(idUoCab);
			nivelEntidadOeeUtil.init2();
		} else {
			nivelEntidadOeeUtil.limpiar();
		}

		updateConcurso();
	}

	public void updateConcurso() {
		List<Concurso> lista = obtenerConcursos();
		concursosSI = new ArrayList<SelectItem>();
		buildSI(lista);

	}

	public void initVer() {

		cargarPersona();
		cargarEmpleadoConceptoPago();
		cargarActoAdmin();
		obtenerCabecera2();
	}

	public void obtenerCabecera2() {
		Query q =
			em.createQuery("select EmpleadoPuesto.plantaCargoDet from EmpleadoPuesto EmpleadoPuesto where EmpleadoPuesto.activo is true and "
				+ " EmpleadoPuesto.persona.idPersona = :idPersona ");
		q.setParameter("idPersona", idPersona);
		List<PlantaCargoDet> lista = q.getResultList();

		if (lista.size() > 0) {
			PlantaCargoDet plantaCargoDet = lista.get(0);
			codPuesto = seleccionUtilFormController.obtenerCodigoPuesto(plantaCargoDet);
			descpuesto = plantaCargoDet.getDescripcion();
			if (plantaCargoDet.getConfiguracionUoDet() != null) {
				if (plantaCargoDet.getConfiguracionUoDet().getConfiguracionUoCab() != null) {
					cargarCabecera(plantaCargoDet.getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
				} else {
					cargarCabecera(null);
				}

				codUoDet =
					listarConfiguracionUoDetFormController.obtenerCodigoMod(plantaCargoDet.getConfiguracionUoDet());
				descUoDet = plantaCargoDet.getConfiguracionUoDet().getDenominacionUnidad();
			} else {
				codUoDet = "-";
				descUoDet = "-";
			}
		} else {
			codPuesto = "-";
			descpuesto = "-";
		}
	}

	private void cargarEmpleadoConceptoPago() {
		Query q =
			em.createQuery("select EmpleadoConceptoPago from EmpleadoConceptoPago EmpleadoConceptoPago "
				+ " where EmpleadoConceptoPago.empleadoPuesto.idEmpleadoPuesto = :idEmpleadoPuesto");
		q.setParameter("idEmpleadoPuesto", idEmpleadoPuesto);
		lEmpleadoConceptoPago = q.getResultList();

	}

	private void cargarActoAdmin() {
		Query q =
			em.createQuery("select ReferenciaAdjuntos from ReferenciaAdjuntos ReferenciaAdjuntos "
				+ " where  ReferenciaAdjuntos.persona.idPersona = :idPersona ");
		q.setParameter("idPersona", idPersona);
		lActoAdmin = q.getResultList();

	}

	private void cargarPersona() {
		persona = em.find(Persona.class, idPersona);
	}

	private List<Concurso> obtenerConcursos() {
		Query q =
			em.createQuery("select Concurso from Concurso Concurso where Concurso.activo is true "
				+ " and Concurso.configuracionUoCab.idConfiguracionUo = :idUo order by Concurso.nombre asc");
		q.setParameter("idUo", nivelEntidadOeeUtil.getIdConfigCab());

		return q.getResultList();
	}

	private void buildSI(List<Concurso> lista) {
		if (concursosSI == null)
			concursosSI = new ArrayList<SelectItem>();
		else
			concursosSI.clear();

		concursosSI.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Concurso o : lista) {
			concursosSI.add(new SelectItem(o.getIdConcurso(), o.getNombre()));
		}
	}

	public void searchConcurso() {
		Query q =
			em.createQuery("select ConcursoPuestoAgr from ConcursoPuestoAgr ConcursoPuestoAgr where ConcursoPuestoAgr.activo is true "
				+ " and ConcursoPuestoAgr.concurso.idConcurso = :idConcurso ");
		q.setParameter("idConcurso", idConcurso);
		lGrupos = q.getResultList();
	}

	public void markAll(Boolean allTrue) {
		if (lGrupos != null)
			for (ConcursoPuestoAgr o : lGrupos) {
				if (allTrue) {
					o.setSeleccionado(true);
				} else {
					o.setSeleccionado(false);
				}
			}
	}

	public void findUnidadOrganizativa() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
		updateConcurso();
	}

	private String precondSearch() {
		String listaIn = "";
		if (lGrupos != null)
			for (ConcursoPuestoAgr o : lGrupos) {
				if (o.getSeleccionado() != null && o.getSeleccionado()) {
					listaIn += "," + o.getIdConcursoPuestoAgr();
				}
			}
		if (listaIn.trim().isEmpty()) {
			return null;
		}
		listaIn = listaIn.replaceFirst(",", "");
		listaIn = "(" + listaIn + ")";
		return listaIn;
	}

	public void limpiar() {
		idConcurso = null;
		if (lEmpleadoPuesto != null) {
			lEmpleadoPuesto.clear();
		}
		if (lGrupos != null) {
			lGrupos.clear();
		}
		cargarCabecera(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
	}

	public void search() {
		String listaIn = precondSearch();
		if (listaIn == null) {
			lEmpleadoPuesto = null;
			statusMessages.add(Severity.ERROR, "Debe seleccionar por lo menos un Grupo");
			return;
		}

		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto where EmpleadoPuesto.activo is true "
				+ " and EmpleadoPuesto.concursoPuestoAgr.idConcursoPuestoAgr in " + listaIn);
		lEmpleadoPuesto = q.getResultList();
	}

	public String formatearNumero(Integer monto) {
		if (monto == null)
			return "0";
		NumberFormat formatter = new DecimalFormat("###,###");
		return formatter.format(monto);
	}

	public void descargarArchivo(int index) {
		ReferenciaAdjuntos referenciaAdjuntos = lActoAdmin.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(referenciaAdjuntos.getDocumentos().getIdDocumento(), usuarioLogueado.getCodigoUsuario());
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public List<SelectItem> getConcursosSI() {
		return concursosSI;
	}

	public void setConcursosSI(List<SelectItem> concursosSI) {
		this.concursosSI = concursosSI;
	}

	public List<ConcursoPuestoAgr> getlGrupos() {
		return lGrupos;
	}

	public void setlGrupos(List<ConcursoPuestoAgr> lGrupos) {
		this.lGrupos = lGrupos;
	}

	public List<EmpleadoPuesto> getlEmpleadoPuesto() {
		return lEmpleadoPuesto;
	}

	public void setlEmpleadoPuesto(List<EmpleadoPuesto> lEmpleadoPuesto) {
		this.lEmpleadoPuesto = lEmpleadoPuesto;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public List<EmpleadoConceptoPago> getlEmpleadoConceptoPago() {
		return lEmpleadoConceptoPago;
	}

	public void setlEmpleadoConceptoPago(List<EmpleadoConceptoPago> lEmpleadoConceptoPago) {
		this.lEmpleadoConceptoPago = lEmpleadoConceptoPago;
	}

	public List<ReferenciaAdjuntos> getlActoAdmin() {
		return lActoAdmin;
	}

	public void setlActoAdmin(List<ReferenciaAdjuntos> lActoAdmin) {
		this.lActoAdmin = lActoAdmin;
	}

	public String getDescUoDet() {
		return descUoDet;
	}

	public void setDescUoDet(String descUoDet) {
		this.descUoDet = descUoDet;
	}

	public String getCodUoDet() {
		return codUoDet;
	}

	public void setCodUoDet(String codUoDet) {
		this.codUoDet = codUoDet;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

}
