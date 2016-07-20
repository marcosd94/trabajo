package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinAnxList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.UtilesFromController;
import py.com.excelsis.sicca.util.SICCAAppHelper;

/**
 * @author jmelgarejo Clase manejadora UC192
 */
@Scope(ScopeType.CONVERSATION)
@Name("categoriaAnexoSearchListFormController")
public class CategoriaAnexoSearchListFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2991843744538615618L;

	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinAnxList sinAnxList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(required = false, create = true)
	UtilesFromController utilesFromController;

	private SinAnx sinAnx = new SinAnx();
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Integer anhoActual = null;
	private Boolean filtroSinar = false;
	private Long idSinNivelEntidad = null;
	private Long idSinEntidad = null;
	private boolean primeraEntrada=true;

	private String descNivelEntidad, descEntidad, fromToPage;

	public void init() {
		
		
		if (sinNivelEntidad.getIdSinNivelEntidad() != null
				&& sinNivelEntidad.getNenNombre() == null) {
				sinNivelEntidad =
					em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		}
		if (sinEntidad.getIdSinEntidad() != null && sinEntidad.getEntNombre() == null) {
			sinEntidad = em.find(SinEntidad.class, sinEntidad.getIdSinEntidad());
			if (sinNivelEntidad.getNenCodigo() == null) {
				sinNivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
				obtenerDescNivelEntidad();
			}
		}
		if(primeraEntrada){
			primeraEntrada=false;
			sinNivelEntidad = new SinNivelEntidad();
			sinEntidad = new SinEntidad();
			cargarAnhoActual();
			sinAnx.setAniAniopre(anhoActual);
		}
		
		
		search();
	}

	private String calcCodSinarh(Long idUsuario) {
		Query q =
			em.createQuery("SELECT  cuo FROM ConfiguracionUoCab cuo, Usuario usu WHERE cuo = usu.configuracionUoCab "
				+ "AND usu.idUsuario = " + idUsuario);
		List<ConfiguracionUoCab> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0).getCodigoSinarh();
		}
		return null;
	}

	public void search() {
		sinAnxList.getSinAnx().setAniAniopre(sinAnx.getAniAniopre());
		sinAnxList.getSinAnx().setVrsCodigo(50);
		if (filtroSinar) {
			String codSinarh = calcCodSinarh(usuarioLogueado.getIdUsuario());
			//SinAnx sinAnx = utilesFromController.getSinAnx(codSinarh);
			List<String> listaSinAnxs=listarSin(codSinarh);
			if(listaSinAnxs.size()>0)
				sinAnxList.setCods(listaSinAnxs);
			else
				sinAnxList.setCods(null);
			
		
		}
		if(sinAnx.getAnxDescrip()!=null && !sinAnx.getAnxDescrip().trim().equals(""))
			sinAnxList.getSinAnx().setAnxDescrip(sinAnx.getAnxDescrip().trim());
	
		if(sinAnx.getCtgCodigo()!=null && !sinAnx.getCtgCodigo().trim().equals(""))
				sinAnxList.getSinAnx().setCtgCodigo(sinAnx.getCtgCodigo().trim());
		sinAnxList.buscarResultados();
	}
	
	private List<String> listarSin(String cod){
		String[] codsArr=cod.split("\\/");
		List<String> c=new ArrayList<String>();
		for (int i = 0; i < codsArr.length; i++) {
			c.add(codsArr[i]);
		}
		return c;
	}
	
	public void searchAll() {
		sinAnx = new SinAnx();
		cargarAnhoActual();
		sinAnx.setAniAniopre(anhoActual);
		sinNivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		sinAnxList.limpiarResultados();
	}

	public void obtenerDescNivelEntidad() {
		descNivelEntidad = null;
		if (sinNivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			SinNivelEntidad nEnt = sinNivelEntidadList.nivelEntidadMaxAnho();
			sinNivelEntidad = nEnt != null ? nEnt : new SinNivelEntidad();
			descNivelEntidad = nEnt != null ? nEnt.getNenNombre() : null;
		}
	}

	public void obtenerDescEntidad() {
		descEntidad = null;
		if (sinNivelEntidad.getNenCodigo() != null && sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setNenCodigo(sinNivelEntidad.getNenCodigo());
			sinEntidadList.getSinEntidad().setEntCodigo(sinEntidad.getEntCodigo());
			SinEntidad ent = sinEntidadList.entidadMaxAnho();
			sinEntidad = ent != null ? ent : new SinEntidad();
			descEntidad = ent != null ? ent.getEntNombre() : null;
		}
	}

	public String getUrlToPageEntidad() {
		if (sinNivelEntidad.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		sinNivelEntidad = em.find(SinNivelEntidad.class, sinNivelEntidad.getIdSinNivelEntidad());
		String page = "/planificacion/searchForms/FindNivelEntidad.xhtml?from=planificacion/searchForms/SinAnxList&codigoNivel="
			+ sinNivelEntidad.getNenCodigo();
		if(fromToPage != null)
			page += "&fromToPage="+fromToPage+"&conversationPropagation=join";
		return page;
	}

	// METODOS PRIVADOS
	private void cargarAnhoActual() {
		Calendar cal = Calendar.getInstance();
		anhoActual = cal.get(Calendar.YEAR);
	}

	// GETTERS Y SETTERS
	public SinAnx getSinAnx() {
		return sinAnx;
	}

	public void setSinAnx(SinAnx sinAnx) {
		this.sinAnx = sinAnx;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public String getDescNivelEntidad() {
		return descNivelEntidad;
	}

	public void setDescNivelEntidad(String descNivelEntidad) {
		this.descNivelEntidad = descNivelEntidad;
	}

	public String getDescEntidad() {
		return descEntidad;
	}

	public void setDescEntidad(String descEntidad) {
		this.descEntidad = descEntidad;
	}

	public Boolean getFiltroSinar() {
		return filtroSinar;
	}

	public void setFiltroSinar(Boolean filtroSinar) {
		this.filtroSinar = filtroSinar;
	}

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public String getFromToPage() {
		return fromToPage;
	}

	public void setFromToPage(String fromToPage) {
		this.fromToPage = fromToPage;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	
}
