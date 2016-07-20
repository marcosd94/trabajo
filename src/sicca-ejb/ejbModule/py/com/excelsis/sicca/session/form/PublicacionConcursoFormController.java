package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.PublicacionConcurso;
import py.com.excelsis.sicca.entity.PublicacionConcursoCab;
import py.com.excelsis.sicca.entity.PublicacionConcursoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.PublicacionConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.PublicacionConcursoCabHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("publicacionConcursoFormController")
public class PublicacionConcursoFormController implements Serializable {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	PublicacionConcursoCabHome publicacionConcursoCabHome;
	@In(create = true)
	PublicacionConcursoHome publicacionConcursoHome;
	@In
	StatusMessages statusMessages;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private PublicacionConcursoCab publicacionConcursoCab;
	private Long idMedioPublic;
	private Date fechaDesde;
	private Date fechaHasta;
	private String mensaje;
	private List<PublicacionConcursoDet> listaDetalle = new ArrayList<PublicacionConcursoDet>();
	private List<PublicacionConcursoDet> listaDetalleAux = new ArrayList<PublicacionConcursoDet>();
	private List<FechasGrupoPuesto> listaFechas = new ArrayList<FechasGrupoPuesto>();
	private List<PublicacionConcursoDet> listaDetalleCompleta = new ArrayList<PublicacionConcursoDet>();
	private SeguridadUtilFormController seguridadUtilFormController;

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

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		validarOee(concursoPuestoAgr.getConcurso());
		buscarDetalles();
		if (listaDetalleCompleta.size() > 0) {
			publicacionConcursoCab = new PublicacionConcursoCab();
			if(listaDetalleCompleta.get(0).getPublicacionConcursoCab()!=null && listaDetalleCompleta.get(0).getPublicacionConcursoCab().getIdPublicacionConc()!=null)
				publicacionConcursoCab=em.find(PublicacionConcursoCab.class, listaDetalleCompleta.get(0).getPublicacionConcursoCab().getIdPublicacionConc());
//			publicacionConcursoCab = listaDetalleCompleta.get(0)
//					.getPublicacionConcursoCab();
			publicacionConcursoCabHome.setInstance(publicacionConcursoCab);
		}
		buscarFechas();
	}

	@SuppressWarnings("unchecked")
	private void buscarDetalles() {
		String sql = "select det.* "
				+ "from seleccion.publicacion_concurso_det det "
				+ "join seleccion.publicacion_concurso_cab cab "
				+ "on cab.id_publicacion_conc = det.id_publicacion_conc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = cab.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and det.activo is true";

		String sql2 = "select det.* "
				+ "from seleccion.publicacion_concurso_det det "
				+ "join seleccion.publicacion_concurso_cab cab "
				+ "on cab.id_publicacion_conc = det.id_publicacion_conc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = cab.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

		listaDetalle = new ArrayList<PublicacionConcursoDet>();
		listaDetalle = em.createNativeQuery(sql, PublicacionConcursoDet.class)
				.getResultList();
		listaDetalleCompleta = new ArrayList<PublicacionConcursoDet>();
		listaDetalleCompleta = em.createNativeQuery(sql2,
				PublicacionConcursoDet.class).getResultList();
		listaDetalleAux.addAll(listaDetalle);
	}

	private Boolean estaDetalle(){
		for (PublicacionConcursoDet d : listaDetalle) {
			if(d.getDatosEspecificos().getIdDatosEspecificos().equals(idMedioPublic))
				return true;
		}
		return false;
	}
	public void agregarLista() {
		if (idMedioPublic == null) {
			mensaje = "Escoja un medio de publicación";
			return;
		}
		if (fechaDesde == null || fechaHasta == null || fechaDesde.after(fechaHasta)) {
			mensaje = "Ingrese fechas válidas";
			return;
		}
		if(estaDetalle()){
			mensaje = "El medio de publicación elegido ya forma parte de la lista";
			return;
		}
			
		for (FechasGrupoPuesto f : listaFechas) {
			if (fechaDesde.compareTo(f.getFechaPublicacionDesde()) < 0
					|| fechaHasta.compareTo(f.getFechaPublicacionHasta()) > 0) {
				
				SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
				
				String fechaDesde = sdf.format(f.getFechaPublicacionDesde());
				String fechaHasta = sdf.format(f.getFechaPublicacionHasta());
				
				mensaje = "La fecha desde tiene que ser mayor a " + fechaDesde + " y la fecha hasta menor a " + fechaHasta;
				return;
			}
		}
		
		PublicacionConcursoDet det = new PublicacionConcursoDet();
		det.setActivo(true);
		det.setDatosEspecificos(em.find(DatosEspecificos.class, idMedioPublic));
		det.setFechaDesde(fechaDesde);
		det.setFechaHasta(fechaHasta);
		listaDetalle.add(det);
		fechaDesde = null;
		fechaHasta = null;
		mensaje = null;
		idMedioPublic = null;

	}

	public void eliminar(Integer row) {
		PublicacionConcursoDet det = new PublicacionConcursoDet();
		det = listaDetalle.get(row);
		listaDetalle.remove(det);
	}

	@SuppressWarnings("unchecked")
	private void buscarFechas() {
		String cad = "select fechas.* "
				+ "from seleccion.fechas_grupo_puesto fechas "
				+ "where fechas.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaFechas = new ArrayList<FechasGrupoPuesto>();
		listaFechas = em.createNativeQuery(cad, FechasGrupoPuesto.class).getResultList();
		
		
		if (listaFechas == null || listaFechas.size() == 0){
			//Buscar por concurso
			cad = "select fechas.* "
				+ "from seleccion.fechas_grupo_puesto fechas "
				+ "where fechas.id_concurso = "
				+ concursoPuestoAgr.getConcurso().getIdConcurso();
			listaFechas = new ArrayList<FechasGrupoPuesto>();
			listaFechas = em.createNativeQuery(cad, FechasGrupoPuesto.class).getResultList();
		}
	}

	@SuppressWarnings("unchecked")
	private PublicacionConcurso buscarPublicacionConcurso() {
		String cad = "select publ.* "
				+ "from seleccion.publicacion_concurso publ "
				+ "where publ.id_concurso = "
				+ concursoPuestoAgr.getConcurso().getIdConcurso();
		List<PublicacionConcurso> listaPubl = new ArrayList<PublicacionConcurso>();
		listaPubl = em.createNativeQuery(cad, PublicacionConcurso.class)
				.getResultList();
		if (listaPubl.size() > 0)
			return listaPubl.get(0);
		return null;
	}

	public String save() {
		try {
			PublicacionConcurso publicacionConcurso = new PublicacionConcurso();
			publicacionConcurso = buscarPublicacionConcurso();
			
			if(publicacionConcurso == null){
				publicacionConcurso = new PublicacionConcurso();
				publicacionConcurso.setConcurso(concursoPuestoAgr.getConcurso());
				
				publicacionConcurso.setFechaSolicitud(new Date());
				publicacionConcurso.setUsuSolicitud(usuarioLogueado.getCodigoUsuario());	
				//String result =publicacionConcursoHome.persist();
				em.persist(publicacionConcurso);
				em.flush();
				String result ="persisted";
				publicacionConcursoHome.setInstance(publicacionConcurso);
//				if(result.equals("persisted")){
//					publicacionConcurso = publicacionConcursoHome.getInstance();
//				}
					
			}
			
			PublicacionConcursoCab cab = new PublicacionConcursoCab();
			cab.setConcursoPuestoAgr(concursoPuestoAgr);
			cab.setPublicacionConcurso(publicacionConcurso);
			
			cab.setFechaSolicitud(new Date());
			cab.setUsuSolicitud(usuarioLogueado.getCodigoUsuario());	
			em.persist(cab);
			em.flush();
			publicacionConcursoCabHome.setInstance(cab);
			String resultado ="persisted";// publicacionConcursoCabHome.persist();
			if (resultado.equals("persisted")) {
				for (PublicacionConcursoDet d : listaDetalle) {
					d.setFechaAlta(new Date());
					d.setPublicacionConcursoCab(cab);
					d.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(d);
					em.flush();
				}
				
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public String update() {
		try {

			for (PublicacionConcursoDet d : listaDetalle) {
				if (d.getPublicacionConcursoCab() == null) {
					d.setFechaAlta(new Date());
					d.setPublicacionConcursoCab(publicacionConcursoCabHome
							.getInstance());
					d.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(d);
					em.flush();
				}
			}
			for (PublicacionConcursoDet aux : listaDetalleAux) {
				Boolean esta = false;
				for (PublicacionConcursoDet d : listaDetalle) {
					if (d.getIdPublicacionDet().equals(
							aux.getIdPublicacionDet()))
						esta = true;
				}
				if (!esta) {
					aux.setActivo(false);
					aux.setFechaMod(new Date());
					aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(aux);
					em.flush();
				}

			}

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Long getIdMedioPublic() {
		return idMedioPublic;
	}

	public void setIdMedioPublic(Long idMedioPublic) {
		this.idMedioPublic = idMedioPublic;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public List<PublicacionConcursoDet> getListaDetalle() {
		return listaDetalle;
	}

	public void setListaDetalle(List<PublicacionConcursoDet> listaDetalle) {
		this.listaDetalle = listaDetalle;
	}

	public List<PublicacionConcursoDet> getListaDetalleAux() {
		return listaDetalleAux;
	}

	public void setListaDetalleAux(List<PublicacionConcursoDet> listaDetalleAux) {
		this.listaDetalleAux = listaDetalleAux;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public List<FechasGrupoPuesto> getListaFechas() {
		return listaFechas;
	}

	public void setListaFechas(List<FechasGrupoPuesto> listaFechas) {
		this.listaFechas = listaFechas;
	}

	public PublicacionConcursoCab getPublicacionConcursoCab() {
		return publicacionConcursoCab;
	}

	public void setPublicacionConcursoCab(
			PublicacionConcursoCab publicacionConcursoCab) {
		this.publicacionConcursoCab = publicacionConcursoCab;
	}

	public List<PublicacionConcursoDet> getListaDetalleCompleta() {
		return listaDetalleCompleta;
	}

	public void setListaDetalleCompleta(
			List<PublicacionConcursoDet> listaDetalleCompleta) {
		this.listaDetalleCompleta = listaDetalleCompleta;
	}

}
