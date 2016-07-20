package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.ejb3.interceptors.ManagedObject;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;
import py.com.excelsis.sicca.dto.CategoriaDTO;
import py.com.excelsis.sicca.dto.CondicionSeguridadDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoEspecifDTO;
import py.com.excelsis.sicca.dto.ContenidoFuncionalDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.dto.ValoracionTab05CPT;
import py.com.excelsis.sicca.dto.ValoracionTab06CPT;
import py.com.excelsis.sicca.entity.CategoriaCpt;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.CptNivelesCargos;
import py.com.excelsis.sicca.entity.CptObs;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.EscalaCondSegur;
import py.com.excelsis.sicca.entity.EscalaCondTrab;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.NivelesDeCargos;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.TipoCce;
import py.com.excelsis.sicca.entity.TipoCpt;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.seguridad.session.RolHome;
import py.com.excelsis.sicca.session.CptHome;
import py.com.excelsis.sicca.session.CptNivelesDeCargosHome;
import py.com.excelsis.sicca.session.CptObsHome;
import py.com.excelsis.sicca.session.CptObsList;
import py.com.excelsis.sicca.session.DetContenidoFuncionalHome;
import py.com.excelsis.sicca.session.DetReqMinHome;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("administrarCptFormController")
@Scope(ScopeType.CONVERSATION)
public class AdministrarCptFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 839954995636576561L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	CptHome cptHome;

	@In(create = true)
	CptObsHome cptObsHome;
	
	@In(create = true)
	CptNivelesDeCargosHome cptNivelesDeCargosHome;
	
	@In(create = true)
	DetContenidoFuncionalHome detContenidoFuncionalHome;

	@In(create = true)
	DetReqMinHome detReqMinHome;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	CptListFormController cptListFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	NivelesDeCargos nivelesDeCargos = new NivelesDeCargos();
	Cpt cpt = new Cpt();
	private Cpt cptPadre = new Cpt();
	private Long idGradoSalarialMin;
	private Long idGradoSalarialMax;
	private Long idTipoCpt;
	private Long idTipoCce;
	private Long idCptPadre;
	private Long idCpt;	
	private Boolean conJefatura=null;
	private Boolean activo;
	private String selectedTab = null;
	private String mensajeTab02;
	private String mensajeTab03;
	private String mensajeTab04;
	private String mensajeTab05;
	private String mensajeTab06;
	private String mensajeCategorias;
	private String tipo;
	private String codigoCpt;
	private Long idAnx;
	private Boolean mostrarCategoria = false;
	private Boolean guardarMas = false;
	
	private Long idCptPopUp;
	
	public Cpt	cptPopUp= new Cpt();
	private Long idTipoCcePopUp;
	private String codigoCptPopUp;
	private Long idTipoCptPopUp;
	private Long idGradoSalarialMaxPopUp;
	private Long idGradoSalarialMinPopUp;
	private Long idNivelesDeCargos;
	private List<CptNivelesCargos> listaCptNivelesCargos = new ArrayList<CptNivelesCargos>();
	private List<NivelesDeCargos> listaNivelesDeCargos = new ArrayList<NivelesDeCargos>();
	private List<String> listaTabla = new ArrayList<String>();
	private Boolean modoEditar=false;

	private List<TipoPlanta> listaTipoPlanta = new ArrayList<TipoPlanta>();
	private List<TipoPlanta> listaDetalleTipoPlanta = new ArrayList<TipoPlanta>();
	private List<ContenidoFuncionalDTO> listaDtoTab2 = new ArrayList<ContenidoFuncionalDTO>();
	private List<ContenidoFuncional> listaValoracionTab2 = new ArrayList<ContenidoFuncional>();
	private List<RequisitosMinimosDTO> listaDtoTab3 = new ArrayList<RequisitosMinimosDTO>();
	private List<RequisitoMinimoCpt> listaValoracionTab3 = new ArrayList<RequisitoMinimoCpt>();
	private List<CondicionTrabajoDTO> listaCondicionDtoTab4 = new ArrayList<CondicionTrabajoDTO>();
	private List<CondicionTrabajo> listaValoracionTab4 = new ArrayList<CondicionTrabajo>();
	private List<CondicionTrabajoEspecifDTO> listaDtoTab5 =
		new ArrayList<CondicionTrabajoEspecifDTO>();
	private List<ValoracionTab05CPT> listaValoracionTab5 = new ArrayList<ValoracionTab05CPT>();
	private List<ValoracionTab06CPT> listaValoracionTab6 = new ArrayList<ValoracionTab06CPT>();
	private List<CondicionSeguridadDTO> listaDtoTab6 = new ArrayList<CondicionSeguridadDTO>();
	List<CondicionTrabajoEspecif> listaCondTrabEspecificas =
		new ArrayList<CondicionTrabajoEspecif>();
	List<CondicionSegur> listaCondSegur = new ArrayList<CondicionSegur>();

	private List<CategoriaDTO> listaCategoriasDTO = new ArrayList<CategoriaDTO>();
	private List<CategoriaCpt> listaCategoriaAux = new ArrayList<CategoriaCpt>();
	ReportUtilFormController reportUtilFormController;
	
	//Variable para las pantallas de homologacion de cpt
	private List <CptObs> listaCptObs = new ArrayList<CptObs>();
	private CptObs cptObs = new CptObs();
	private Long idCptObs; 
	private boolean habilitarObservacionSfp;
	private boolean habilitarEditarObservacionSfp;
	private boolean habilitarRespuestaOee;
	private boolean habilitarEditarRespuestaOee;
	private boolean habilitarHomologar;
	private boolean deshabilitarEditarCpt;


	/**
	 * Listas auxiliares que se usaran solo para la edicion de datos
	 */
	private List<ContenidoFuncionalDTO> listaDetalleDtoTab2 = new ArrayList<ContenidoFuncionalDTO>();
	private List<DetContenidoFuncional> listaDetalleTab2 = new ArrayList<DetContenidoFuncional>();
	private List<DetTipoNombramiento> listaDetalleTab1 = new ArrayList<DetTipoNombramiento>();
	private List<DetTipoNombramiento> listaAuxTab1 = new ArrayList<DetTipoNombramiento>();
	private List<DetContenidoFuncional> listaAuxTab2 = new ArrayList<DetContenidoFuncional>();
	private List<DetReqMin> listaAuxTab3 = new ArrayList<DetReqMin>();
	private List<DetCondicionTrabajo> listaAuxTab4 = new ArrayList<DetCondicionTrabajo>();
	private List<DetCondicionTrabajoEspecif> listaAuxTab5 =
		new ArrayList<DetCondicionTrabajoEspecif>();
	private List<DetCondicionSegur> listaAuxTab6 = new ArrayList<DetCondicionSegur>();
	private List<SelectItem> gradoSalarialSelecItem;


	private final String TAB_NOMBRAMIENTO = "tabNombramiento";
	private final String TAB_CONTENIDO_FUNCIONAL = "tabContenidoFuncional";
	private final String TAB_REQUISITOS_MIN = "tabRequisitosMin";
	private final String TAB_CONDICION_TRAB = "tabCondicionTrab";
	private final String TAB_CONDICION_TRAB_ESPECIF = "tabCondicionTrabEspecif";
	private final String TAB_CONDICION_SEGURIDAD = "tabCondicionSeguridad";

	// habilita jefatura en caso que nivel supere 4
	private boolean habJefatura;

	public void init() {
		conJefatura = (conJefatura != null)? conJefatura : false;
		cptPadre = null;//Agregado RV
		codigoCpt = null;
		listaTipoPlanta=null;
		
		if (cptHome.isIdDefined()) {
			cpt = cptHome.getInstance();
			
			buscarCCe();
			updateGradoSalarial();
			if (cpt.getGradoSalarialMax() != null)
				idGradoSalarialMax = cpt.getGradoSalarialMax().getIdGradoSalarial();
			if (cpt.getGradoSalarialMin() != null)
				idGradoSalarialMin = cpt.getGradoSalarialMin().getIdGradoSalarial();
			if (cpt.getTipoCpt() != null)
				idTipoCpt = cpt.getTipoCpt().getIdTipoCpt();
			if(cpt.getCptPadre() != null){
				cptPadre = cpt.getCptPadre();
				buscarCodigoCpt();
				//idCptPadre = cpt.getCptPadre().getIdCpt();
			}
			
			
			modoEditar=true;
			//conJefatura = cpt.getJefatura();
			activo = cpt.getActivo();
			//buscarListaTabla();
			buscarNombramientos();
			buscarContenidoFuncional();
			//buscarCondicionTrabajo();
			//buscarCondicionTrabajoEspecifica();
			//buscarDetCondicionSegur();
			buscarRequerimientosMinimos();
			if (listaCategoriasDTO.size() == 0)
				buscarCategoriasCargadas();
			valNivel();

		} else {
			modoEditar=false;
			habJefatura = true;
			if(idCptPadre == null){
			cpt = cptHome.getInstance();
			
			updateGradoSalarial();
			}
			//buscarListaTabla();
			mensajeTab02 = null;
			mensajeTab03 = null;
			mensajeTab04 = null;
			mensajeTab05 = null;
			mensajeTab06 = null;
			buscarTipoPlanta();
			buscarContenidoFuncionalTab2();
			buscarRequisitosMinimosCPTTab3();
		//	buscarCondicionesTrabajo();
		//	buscarCondicionesTrabajoEspecif();
			//buscarCondicionesSeguridad();
			activo = true;
			if (listaCategoriasDTO.size() == 0)
				iniciarCategoriasPosibles();
		}
		buscarValoracionTab02();
		buscarValoracionTab03();
		buscarValoracionTab04();
		buscarValoracionTab05();
		buscarValoracionTab06();
		if(idCptPadre != null){
			cptPadre = em.find(Cpt.class, idCptPadre);
			buscarCodigoCpt();
			idCptPadre = null;
		}
		
	}
	
	
	public void initCptGestionarHomologacion(){
		cpt = cptHome.getInstance();
		idCpt = cpt.getId();
		idCptObs = null;
		
		buscarCCe();
		updateGradoSalarial();
		if (cpt.getGradoSalarialMax() != null)
			idGradoSalarialMax = cpt.getGradoSalarialMax().getIdGradoSalarial();
		if (cpt.getGradoSalarialMin() != null)
			idGradoSalarialMin = cpt.getGradoSalarialMin().getIdGradoSalarial();
		if (cpt.getTipoCpt() != null)
			idTipoCpt = cpt.getTipoCpt().getIdTipoCpt();
		if(cpt.getCptPadre() != null){
			cptPadre = cpt.getCptPadre();
			buscarCodigoCpt();
			//idCptPadre = cpt.getCptPadre().getIdCpt();
		}
		
		
		modoEditar=true;
		//conJefatura = cpt.getJefatura();
		activo = cpt.getActivo();
		//buscarListaTabla();
		buscarNombramientos();
		buscarContenidoFuncional();
		//buscarCondicionTrabajo();
		//buscarCondicionTrabajoEspecifica();
		//buscarDetCondicionSegur();
		buscarRequerimientosMinimos();
		if (listaCategoriasDTO.size() == 0)
			buscarCategoriasCargadas();
		valNivel();
		
		
		if (cpt != null)
			this.listaCptObs = this.obtenerObservacionesPorCpt(cpt); 
		
		
		if(isUsuarioHomologador(usuarioLogueado)){
			habilitarHomologar = puedeHomologar();
			habilitarObservacionSfp = true;
			habilitarEditarObservacionSfp = true;
			habilitarRespuestaOee = false;
			habilitarEditarRespuestaOee = false;
		}else{
			habilitarHomologar = false;
			habilitarObservacionSfp = false;
			habilitarEditarObservacionSfp = false;
			habilitarRespuestaOee = true;
			habilitarEditarRespuestaOee = true;
		}
		deshabilitarEditarCpt = this.sinEnvioPendientes();
		
	}
	
	public boolean tieneRespuesta(CptObs cptObs){
		if(cptObs.getRespuestaOee() == null)
			return false;
		else
			return true;
	}
	public boolean deshabilitarEnvioRespuesta(CptObs cptObs){
		boolean retorno;
		
		if(cptObs.getRespuestaOee() == null || (cptObs.getRespuestaOee() != null && cptObs.getRespuestaOee().trim().equals("")))
			retorno =  true;
		else
			if(cptObs.isActivo())
				retorno =  false;
			else 
				retorno =  true;
		
		deshabilitarEditarCpt = this.sinEnvioPendientes();
		return retorno;
	}
	
	public boolean deshabilitarEnvioObservacion(CptObs cptObs){
		if(cptObs.getObservacionSfp() == null || (cptObs.getObservacionSfp() != null && cptObs.getObservacionSfp().trim().equals("")))
			return true;
		else
			if(cptObs.isEnvioObservacion())
				return true;
			else 
				return false;
	}
	
	public boolean deshabilitarEditar(CptObs cptObs){
		
			if(cptObs.isEnvioObservacion())
				return true;
			else 
				return false;
	}
	
	public boolean sinEnvioPendientes(){
		boolean retorno = true;
		for (CptObs cptObs : listaCptObs){
			if(cptObs.isActivo())
				retorno = false;
		}
		return retorno;
	}
	


	public void initCptHomologacionObservacionSfp () throws Exception{
		cpt = cptHome.getInstance();
		idCpt = cpt.getId();
		
		cptObs = cptObsHome.getInstance();
		
		if(cptObs == null || cptObs.getIdCptObs() == null){
			cptObs = new CptObs();
			cptObs.setFechaObservacionSfp(new Date());
			cptObs.setUsuObservacionSfp(usuarioLogueado.getCodigoUsuario());
			cptObs.setFechaAlta(new Date());
			cptObs.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			cptObs.setActivo(true);
			cptObs.setCpt(cpt);
		}		
		
		
	}
	
	public void initCptHomologacionRespuestaOee () throws Exception{
		
		if(isUsuarioHomologador(usuarioLogueado)){
			habilitarHomologar = puedeHomologar();
			habilitarObservacionSfp = true;
			habilitarEditarObservacionSfp = true;
			habilitarRespuestaOee = false;
			habilitarEditarRespuestaOee = false;
		}else{
			habilitarHomologar = false;
			habilitarObservacionSfp = false;
			habilitarEditarObservacionSfp = false;
			habilitarRespuestaOee = true;
			habilitarEditarRespuestaOee = true;
		}
		
		cpt = cptHome.getInstance();
		idCpt = cpt.getId();
		
		cptObs = cptObsHome.getInstance();
		if(this.habilitarEditarRespuestaOee)
			cptObs.setFechaRespuestaOee(new Date());
		cptObs.setUsuRespuestaOee(usuarioLogueado.getCodigoUsuario());
		cptObs.setFechaMod(new Date());
		cptObs.setUsuMod(usuarioLogueado.getCodigoUsuario());
		cptObs.setActivo(true);
			
		
		
	}
	
	public String saveCptObs(){
		if(cptObs.getObservacionSfp()!= null && !cptObs.getObservacionSfp().equals("")){
			cptObs.setEnvioObservacion(false);
			em.persist(cptObs);
			em.flush();
			this.volverCptGestionarHomologacion();
		}else {
			statusMessages.clear();
			statusMessages.add(Severity.WARN, "Existen Campos Vacios.. verifique");
			return null;
		}
		return "persisted";
	}
	
	public String updateCptObs(){
		if(cptObs.getObservacionSfp()!= null && !cptObs.getObservacionSfp().trim().equals("")){
			cptObs.setEnvioObservacion(false);
			em.merge(cptObs);
			em.flush();
			this.volverCptGestionarHomologacion();
		}else{
			statusMessages.clear();
			statusMessages.add(Severity.WARN, "Existen Campos Vacios.. verifique");
			return null;
		}
		return "updated";
	}
	
		
	public String updateCptResp(){
		if(cptObs.getRespuestaOee()!= null && !cptObs.getRespuestaOee().trim().equals("")){
			em.merge(cptObs);
			em.flush();
			this.volverCptGestionarHomologacion();
		}else{
			statusMessages.clear();
			statusMessages.add(Severity.WARN, "Existen Campos Vacios.. verifique");
			return null;
		}
		return "updated";
		
	}
	
	public boolean puedeHomologar(){
		List<CptObs> lista = this.obtenerObservacionesPorCpt(this.cpt);
		boolean homologar = true;
		CptObs obs = new CptObs();
		for (int i = 0; i < lista.size(); i++){
			obs = lista.get(i);
			if(obs.getRespuestaOee() == null || obs.getRespuestaOee().equals(""))
				homologar = false;
		}
		return homologar;
	}
	
	public String homologar(){
		
		if(habilitarHomologar){
			cpt.setEstadoHomologacion(Cpt.ESTADO_HOMOLOGADO);
			cpt.setFechaHomologacion(new Date());
			cpt.setUsuHomologacion(this.usuarioLogueado.getCodigoUsuario());
			em.merge(cpt);
			em.flush();
			
			this.cptListFormController.enviarMailHomologado(cpt.getIdCpt());
			
			return "homologado";
						
			
		}else{
			statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
			return "error";
		}
		
		
			 
	}
	
	
	
	
	private boolean isUsuarioHomologador(Usuario usuario){
		Iterator <UsuarioRol> it = usuario.getUsuarioRols().iterator();
		boolean retorno = false;
		
		while (it.hasNext()){
			if(it.next().getRol().getHomologador())
				retorno = true;
			
		}
				
		return retorno;
		
	}
	
	private List<CptObs> obtenerObservacionesPorCpt(Cpt cpt){
		String sql = "select * from planificacion.cpt_obs where id_cpt = "+cpt.getIdCpt();
		
		if(!isUsuarioHomologador(usuarioLogueado))
				sql+= " and envio_observacion = true ";
				
				sql+= " Order by fecha_observacion_sfp desc ";
		
		return em.createNativeQuery(sql, CptObs.class).getResultList();
	}
	
	
	/*
	 * NIVELES DE CARGOS POSIBLES
	 * 	
	 */
	public String buscarDescripcion(){
		List<NivelesDeCargos> lista = new ArrayList<NivelesDeCargos>();
		lista = em.createQuery("select o from NivelesDeCargos o  where "
				+ "id_niveles_de_cargos = "+getIdNivelesDeCargos()).getResultList();
		
		return lista.get(0).getDescripcion();
	} 
	
	public void buscarListaTabla(){
		
		/*for (int i = 0; i < larray.size(); i++) {
			Object[] obj = (Object[]) larray.get(i);
			listaTabla.add((String) obj[0]);
			CptNivelesCargos cptNC = new CptNivelesCargos();
			cptNC.setIdCpt(cpt.getId());
			BigInteger bi = (BigInteger) obj[1];
			cptNC.setIdNivelesCargos(bi.longValue());
			listaCptNivelesCargos.add(cptNC);
		}*/
		
		Long idTmp= (cptPadre!=null)? cptPadre.getIdCpt()  : cpt.getIdCpt();
		
		String	q1 = "select nc.descripcion, nc.id_niveles_de_cargos, nc.tipo from planificacion.cpt_niveles_cargos cptnc,"
				+ " planificacion.niveles_de_cargos nc where cptnc.id_niveles_de_cargos = "
				+ "nc.id_niveles_de_cargos and cptnc.id_cpt = "+idTmp;
		
	
		List larray = em.createNativeQuery(q1).getResultList();
		listaTabla = new ArrayList<String>();
		listaCptNivelesCargos = new ArrayList<CptNivelesCargos>();
		listaNivelesDeCargos = new ArrayList<NivelesDeCargos>();
	
		for(int i = 0; i < larray.size(); i++) {
			Object[] obj = (Object[]) larray.get(i);
			
			//CARGA LA LISTA QUE SE MUESTRA EN CptEdit.xhtml y Cpt.xhtml
			NivelesDeCargos aux = new NivelesDeCargos();
			aux.setDescripcion((String) obj[0]);
			aux.setTipo(obj[2].toString());
			listaNivelesDeCargos.add(aux);

			//CARGA LA LISTA QUE MODIFICA LOS REGISTROS DE LA TABLA cpt_niveles_cargos
			CptNivelesCargos cptNC = new CptNivelesCargos();
			cptNC.setIdCpt(cpt.getId());
			BigInteger bi = (BigInteger) obj[1];
			cptNC.setIdNivelesCargos(bi.longValue());
			listaCptNivelesCargos.add(cptNC);
				
		}
	
	}
	
	
	
	public String verDetalles(Long aux,int index){
		
		//idCptPopUp=(idCptPadre!=null)? idCptPadre : aux;
		idCptPopUp=aux;
		cptPopUp = em.find(Cpt.class, idCptPopUp);
		buscarCodigoCptPopUp();
		cargarTab1PopUp();
		cargarTab2PopUp();
		
		return "Richfaces.showModalPanel('pnl1', {top:'50px', left:'200px', height:'400px'});";
		
		
	}
	/*
	 * CARGAR TIPO DE NOMBRAMIENTO POPUP
	 */

	public void cargarTab1PopUp(){
	
		
		String cadena =
				"select * from planificacion.tipo_planta tipo " + "where tipo.activo IS TRUE";
			listaDetalleTipoPlanta = new ArrayList<TipoPlanta>();
			listaDetalleTipoPlanta = em.createNativeQuery(cadena, TipoPlanta.class).getResultList();
			
		String cad =
			"select * from planificacion.det_tipo_nombramiento det_tipo"
				+ " where det_tipo.id_cpt = " + idCptPopUp;
		listaDetalleTab1 = new ArrayList<DetTipoNombramiento>();
		listaDetalleTab1 = em.createNativeQuery(cad, DetTipoNombramiento.class).getResultList();
			for (int i = 0; i < listaDetalleTipoPlanta.size(); i++) {
			TipoPlanta planta = new TipoPlanta();
			planta = listaDetalleTipoPlanta.get(i);
			
			List<TipoNombramiento> listaNombramientos = planta.getTipoNombramientos();
			for (DetTipoNombramiento detalle : listaDetalleTab1) {
				for (TipoNombramiento nombramiento : listaNombramientos) {
					if (nombramiento.getIdTipoNombramiento().equals(detalle.getTipoNombramiento().getIdTipoNombramiento())) {
						nombramiento.setSeleccionado(true);
					}
				}
			}
			
			planta.setTipoNombramientos(listaNombramientos);
			listaDetalleTipoPlanta.set(i, planta);
		}
	
				
			
	}
	/*
	 * CARGAR CONTENIDO FUNCIONAL POPUP
	 */
	
	
	private void cargarTab2PopUp() {
	
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.id_cpt = " + idCptPopUp;
		listaDetalleTab2 = new ArrayList<DetContenidoFuncional>();
		listaDetalleTab2 = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDetalleDtoTab2 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaDetalleTab2) {
				if (det.getContenidoFuncional().getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					dto.setPuntajeString(dto.getPuntaje().toString());
					List<DetDescripcionContFuncional> listaDesc =
						new ArrayList<DetDescripcionContFuncional>();
					listaDesc = det.getDetDescripcionContFuncionals();
					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDetalleDtoTab2.add(dto);
				}
			}
			if (!esta) {
				ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
				dto.setContenidoFuncional(contenido);
				List<DetDescripcionContFuncional> listaDesc =
					new ArrayList<DetDescripcionContFuncional>();

				DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
				listaDesc.add(descr);
				dto.setListaDetDescripContFuncional(listaDesc);
				listaDetalleDtoTab2.add(dto);
			}
		}

		
	}
	

	private void buscarCodigoCptPopUp() {
		String sql = "select cpt.* from planificacion.cpt cpt "
				+ "where cpt.id_cpt = " + idCptPopUp;

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(sql, Cpt.class).getResultList();
		setCodigoCptPopUp(cptPopUp.getNivel() + "."
				+ cptPopUp.getGradoSalarialMin().getNumero() + "."
				+ cptPopUp.getGradoSalarialMax().getNumero() + "." + cptPopUp.getNumero());
		try{
			if(cptPopUp!= null){
			setIdTipoCptPopUp(cptPopUp.getTipoCpt().getIdTipoCpt());
			setIdTipoCcePopUp(cptPopUp.getGradoSalarialMax().getGradoSalarialCab().getTipoCce().getIdTipoCce());
			updateGradoSalarial();
			setIdGradoSalarialMaxPopUp(cptPopUp.getGradoSalarialMax().getIdGradoSalarial());
			setIdGradoSalarialMinPopUp(cptPopUp.getGradoSalarialMin().getIdGradoSalarial());
			
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	private void buscarCodigoCpt() {
		String sql = "select cpt.* from planificacion.cpt cpt "
				+ "where cpt.id_cpt = " + cpt.getIdCpt();

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(sql, Cpt.class).getResultList();
		codigoCpt = cptPadre.getNivel() + "."
				+ cptPadre.getGradoSalarialMin().getNumero() + "."
				+ cptPadre.getGradoSalarialMax().getNumero() + "." + cptPadre.getNumero();
		try{
			idTipoCpt = cptPadre.getTipoCpt().getIdTipoCpt();
			idTipoCce = cptPadre.getGradoSalarialMax().getGradoSalarialCab().getTipoCce().getIdTipoCce();
			updateGradoSalarial();
			idGradoSalarialMax = cptPadre.getGradoSalarialMax().getIdGradoSalarial();
			idGradoSalarialMin = cptPadre.getGradoSalarialMin().getIdGradoSalarial();
			cpt.setNumero(cptPadre.getNumero());
			cpt.setNivel(cptPadre.getNivel());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void imprimir() throws Exception {
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU_140_IMPRIMIR_CPT");
		reportUtilFormController.getParametros().put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));
		reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
		if (cptListFormController != null && cptListFormController.getEstado().getValor() != null) {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			if (!sufc.validarInput(cptListFormController.getEstado().getValor().toString(), TiposDatos.BOOLEAN.toString(), null)) {
				return;
			}
			String elWhere =
				" where planificacion.cpt.activo is "
					+ cptListFormController.getEstado().getValor();

			if (cptListFormController.getTipo().equalsIgnoreCase("especifico")) {
				reportUtilFormController.getParametros().put("mNumeroEspecifico", true);
				elWhere += " and planificacion.cpt.nro_especifico is not null ";
			} else {
				reportUtilFormController.getParametros().put("mNumeroEspecifico", false);
				elWhere += " and planificacion.cpt.nro_especifico is null ";
			}
			elWhere +=
				" order by planificacion.cpt.nivel desc ,gradSalMin.numero  desc,  gradSalMax.numero desc,planificacion.cpt.numero asc ";
			reportUtilFormController.getParametros().put("elWhere", elWhere);
		}
		reportUtilFormController.imprimirReportePdf();
	}

	@SuppressWarnings("unchecked")
	private void buscarCCe() {
		String sql =
			"select cee.* "
				+ "from planificacion.tipo_cce cee "
				+ "join planificacion.grado_salarial_cab cab "
				+ "on cab.id_tipo_cce = cee.id_tipo_cce "
				+ "join planificacion.grado_salarial sal_min "
				+ "on sal_min.id_grado_salarial_cab = cab.id_grado_salarial_cab "
				+ "join planificacion.grado_salarial sal_max "
				+ "on sal_max.id_grado_salarial_cab = cab.id_grado_salarial_cab "
				+ "join planificacion.cpt cpt "
				+ "on cpt.id_grado_salarial_min = sal_min.id_grado_salarial and cpt.id_grado_salarial_max = sal_max.id_grado_salarial "
				+ "where cpt.id_cpt = " + cpt.getIdCpt();
		try {
			List<TipoCce> listaCee = new ArrayList<TipoCce>();
			listaCee = em.createNativeQuery(sql, TipoCce.class).getResultList();
			if (listaCee.size() > 0)
				idTipoCce = listaCee.get(0).getIdTipoCce();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void iniciarCategoriasPosibles() {
		listaCategoriasDTO = new ArrayList<CategoriaDTO>();
		CategoriaDTO dto = new CategoriaDTO();
		listaCategoriasDTO.add(dto);
	}

	/**
	 * Carga el combo de ciudad de acuerdo al departamento seleccionado
	 */
	public void updateGradoSalarial() {
		List<GradoSalarial> gradoSalarialList = getGradoSalarialByCce();
		gradoSalarialSelecItem = new ArrayList<SelectItem>();
		buildGradoSalarialSelectItem(gradoSalarialList);
	}

	@SuppressWarnings("unchecked")
	private List<GradoSalarial> getGradoSalarialByCce() {
		List<GradoSalarial> listaGrad = new ArrayList<GradoSalarial>();
		if (idTipoCce != null) {
			try {
				String sql1 =
					"select max(grado.anho) " + "from planificacion.grado_salarial grado "
						+ "join planificacion.grado_salarial_cab cab "
						+ "on cab.id_grado_salarial_cab = grado.id_grado_salarial_cab "
						+ "join planificacion.tipo_cce cee "
						+ "on cee.id_tipo_cce = cab.id_tipo_cce " + "where cee.id_tipo_cce = "
						+ idTipoCce + " and cab.activo is true";
				Object obj = em.createNativeQuery(sql1).getSingleResult();
				Integer anho = new Integer(obj.toString());
				String sql2 =
					"select grado.* " + "from planificacion.grado_salarial grado "
						+ "join planificacion.grado_salarial_cab cab "
						+ "on cab.id_grado_salarial_cab = grado.id_grado_salarial_cab "
						+ "join planificacion.tipo_cce cee "
						+ "on cee.id_tipo_cce = cab.id_tipo_cce " + "where cee.id_tipo_cce = "
						+ idTipoCce + " and grado.anho = " + anho + " order by grado.numero";

				listaGrad = em.createNativeQuery(sql2, GradoSalarial.class).getResultList();
				return listaGrad;
			} catch (Exception e) {
				// TODO: handle exception
			}
			return new ArrayList<GradoSalarial>();

		}
		return new ArrayList<GradoSalarial>();
	}

	private void buildGradoSalarialSelectItem(List<GradoSalarial> gradoSalList) {
		if (gradoSalarialSelecItem == null)
			gradoSalarialSelecItem = new ArrayList<SelectItem>();
		else
			gradoSalarialSelecItem.clear();

		gradoSalarialSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (GradoSalarial gr : gradoSalList) {
			gradoSalarialSelecItem.add(new SelectItem(gr.getId(), "" + gr.getNumero()));
		}
	}

	@SuppressWarnings("unchecked")
	private void buscarCategoriasCargadas() {
		listaCategoriasDTO = new ArrayList<CategoriaDTO>();

		String cad =
			"select cat.* from planificacion.categoria_cpt cat where cat.id_cpt = " + cpt.getId();
		listaCategoriaAux = new ArrayList<CategoriaCpt>();
		listaCategoriaAux = em.createNativeQuery(cad, CategoriaCpt.class).getResultList();
		for (CategoriaCpt c : listaCategoriaAux) {
			CategoriaDTO dto = new CategoriaDTO();
			dto.setCategoria(c.getCategoria());
			dto.setId(c.getIdCategoriaCpt());
			String valor = buscarDenominacion(c.getCategoria());
			if (valor != null)
				dto.setDenominacion(valor);
			listaCategoriasDTO.add(dto);
		}
		CategoriaDTO dto = new CategoriaDTO();
		listaCategoriasDTO.add(dto);
	}

	@SuppressWarnings("unchecked")
	private String buscarDenominacion(String codigo) {
		String cadena =
			"select anx.* from sinarh.sin_anx anx " + "where anx.ctg_codigo= '" + codigo + "'"
				+ "order by anx.ani_aniopre desc";
		List<SinAnx> l = new ArrayList<SinAnx>();
		l = em.createNativeQuery(cadena, SinAnx.class).getResultList();
		if (l.size() > 0)
			return l.get(0).getAnxDescrip();
		return null;
	}

	@SuppressWarnings("unchecked")
	public void buscarDenominacion(Integer row) {
		CategoriaDTO cat = listaCategoriasDTO.get(row);
		String cadena =
			"select anx.* from sinarh.sin_anx anx " + "where anx.ctg_codigo= '"
				+ cat.getCategoria() + "'" + "order by anx.ani_aniopre desc";
		List<SinAnx> l = new ArrayList<SinAnx>();
		l = em.createNativeQuery(cadena, SinAnx.class).getResultList();
		if (l.size() > 0) {
			cat.setDenominacion(l.get(0).getAnxDescrip());
			listaCategoriasDTO.set(row, cat);
			mensajeCategorias = null;
		} else {
			cat.setDenominacion(null);
			listaCategoriasDTO.set(row, cat);
			mensajeCategorias = "No existe el categoria ingresada";
		}

	}

	/**
	 * Metodo que busca el contenido funcional para el TAB02
	 */
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncionalTab2() {
		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoTab2 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();
		if (lista.size() > 0) {
			for (ContenidoFuncional o : lista) {
				ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
				dto.setContenidoFuncional(o);
				List<DetDescripcionContFuncional> listaDet =
					new ArrayList<DetDescripcionContFuncional>();
				DetDescripcionContFuncional det = new DetDescripcionContFuncional();
				listaDet.add(det);
				dto.setListaDetDescripContFuncional(listaDet);
				listaDtoTab2.add(dto);
			}
		}

	}

	/**
	 * Metodo que busca los requisitos minimos cpt para el TAB03
	 */
	@SuppressWarnings("unchecked")
	private void buscarRequisitosMinimosCPTTab3() {
		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoTab3 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();

		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		if (lista.size() > 0) {
			for (RequisitoMinimoCpt o : lista) {
				RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
				dto.setRequisitoMinimoCpt(o);
				List<DetOpcionesConvenientes> listaDetOpc =
					new ArrayList<DetOpcionesConvenientes>();
				DetOpcionesConvenientes detOpc = new DetOpcionesConvenientes();
				listaDetOpc.add(detOpc);
				dto.setListaDetOpcionesConvenientes(listaDetOpc);
				List<DetMinimosRequeridos> listaDetMin = new ArrayList<DetMinimosRequeridos>();
				DetMinimosRequeridos detMin = new DetMinimosRequeridos();
				listaDetMin.add(detMin);
				dto.setListaDetMinimosRequeridos(listaDetMin);
				listaDtoTab3.add(dto);
			}
		}

	}

	/**
	 * Metodo que busca los Tipos de planta para el TAB01
	 */
	@SuppressWarnings("unchecked")
	private void buscarTipoPlanta() {
		String cadena =
			"select * from planificacion.tipo_planta tipo " + "where tipo.activo IS TRUE";
		listaTipoPlanta = new ArrayList<TipoPlanta>();
		listaTipoPlanta = em.createNativeQuery(cadena, TipoPlanta.class).getResultList();
	}

	public List<TipoNombramiento> traerTipoNombramiento(Long id) {
			System.out.println("traerTipoNombramiento :" + id  );
			Query q =
					em.createQuery("select TipoNombramiento from TipoNombramiento TipoNombramiento "
						+ " where TipoNombramiento.tipoPlanta.idTipoPlanta = :idTipoPlanta "
						+ " and TipoNombramiento.activo is true " + " order by descripcion ");
				q.setParameter("idTipoPlanta", id);
				return q.getResultList();

	}

	
	//Trae en funcion a li
	public List<TipoNombramiento> traerTipoNombramiento2(Long idTipoPlanta) {
		System.out.println("traerTipoNombramiento2 :" + idTipoPlanta  );
		DetTipoNombramiento dtn;
		TipoNombramiento tn;
		List<TipoNombramiento> retorno = new ArrayList<TipoNombramiento>(); 

		
		//Lista con todos los tipos de nombramiento
		Query q = em.createQuery("select TipoNombramiento from TipoNombramiento TipoNombramiento "
									+ " where TipoNombramiento.tipoPlanta.idTipoPlanta = :idTipoPlanta "
									+ " and TipoNombramiento.activo is true "
									+ " order by descripcion ");

		q.setParameter("idTipoPlanta", idTipoPlanta);
		List<TipoNombramiento> todosTiposNombramientosPlanta = q.getResultList();

		
		//Detectar CptPAdre
		Long idCptPadreQuery = null;
		
		if(cpt!= null && cpt.getCptPadre() != null){
			idCptPadreQuery = cpt.getCptPadre().getIdCpt();
			
		}else if(cptPadre != null){
			
			idCptPadreQuery = cptPadre.getIdCpt();
			
		}
		
		System.out.println("idCptPadreQuery es: " + idCptPadreQuery);
		
		//Lista con los tipos de nombramiento del CptPadre
		String cad = "select * from planificacion.det_tipo_nombramiento det_tipo"
					+ " where det_tipo.id_cpt = " + idCptPadreQuery;
		
		List<DetTipoNombramiento> listaDetTipoNomCptPadre = new ArrayList<DetTipoNombramiento>();
		listaDetTipoNomCptPadre = em.createNativeQuery(cad, DetTipoNombramiento.class).getResultList();
		
		
	
		
		System.out.println("Para tipoPlanta " + idTipoPlanta + " el tamaño es " + todosTiposNombramientosPlanta.size());
		for (int j = 0; j < todosTiposNombramientosPlanta.size(); j++) {
			
			tn = todosTiposNombramientosPlanta.get(j);
		
			System.out.println("Empezando tipo de planta : " + j +" con IdTipoNombramiento="+ tn.getIdTipoNombramiento() );
			
			TipoNombramiento tnGuardado = new TipoNombramiento();
			tnGuardado.setIdTipoNombramiento(tn.getIdTipoNombramiento());
			tnGuardado.setDescripcion(tn.getDescripcion());
			tnGuardado.setActivo(tn.getActivo());
			boolean seleccionado = false;
			for (int i = 0; i < listaDetTipoNomCptPadre.size(); i++) {
				dtn = listaDetTipoNomCptPadre.get(i);

				System.out.println("tnGuardadoA : " + tnGuardado.getIdTipoNombramiento());
				System.out.println("tnGuardadoB :"  + dtn.getTipoNombramiento().getIdTipoNombramiento());
				if(dtn.getTipoNombramiento().getIdTipoNombramiento() == tnGuardado.getIdTipoNombramiento()){
					seleccionado = true;
					break;
				}
				
			}
			tnGuardado.setSeleccionado(seleccionado);
			System.out.println("tnGuardado" + tnGuardado.getDescripcion() +" Resultado :"  + tnGuardado.getSeleccionado());
			retorno.add(tnGuardado);
		}
		return retorno;
	}
	
	
	/**
	 * Metodo que busca las Condiciones de trabajo para el TAB04
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionesTrabajo() {
		String cadena =
			"select * from planificacion.condicion_trabajo cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajo> lista = new ArrayList<CondicionTrabajo>();

		lista = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
		if (lista.size() > 0) {
			listaCondicionDtoTab4 = new ArrayList<CondicionTrabajoDTO>();
			for (CondicionTrabajo o : lista) {
				CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
				dto.setCondicionTrabajo(o);
				listaCondicionDtoTab4.add(dto);
			}
		}
	}

	/**
	 * Metodo que busca las Condiciones de trabajo especificas para el TAB05
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionesTrabajoEspecif() {
		String cadena =
			"select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		listaCondTrabEspecificas = new ArrayList<CondicionTrabajoEspecif>();

		listaCondTrabEspecificas =
			em.createNativeQuery(cadena, CondicionTrabajoEspecif.class).getResultList();
		if (listaCondTrabEspecificas.size() > 0) {
			listaDtoTab5 = new ArrayList<CondicionTrabajoEspecifDTO>();
			for (CondicionTrabajoEspecif o : listaCondTrabEspecificas) {
				CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
				dto.setCondicionTrabajoEspecif(o);
				listaDtoTab5.add(dto);
			}
		}
	}

	/**
	 * Metodo que busca las Condiciones de seguridad para el TAB06
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionesSeguridad() {
		String cadena =
			"select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		listaCondSegur = new ArrayList<CondicionSegur>();

		listaCondSegur = em.createNativeQuery(cadena, CondicionSegur.class).getResultList();
		if (listaCondSegur.size() > 0) {
			listaDtoTab6 = new ArrayList<CondicionSeguridadDTO>();
			for (CondicionSegur o : listaCondSegur) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setCondicionSegur(o);
				listaDtoTab6.add(dto);
			}
		}
	}

	/**********************************************************
	 * Metodos agregan registros a sublistas
	 **********************************************************/

	/**
	 * Método que agrega un detalle a la sublista de la tabla
	 * 
	 * @param row
	 */
	public void agregarListaTab2(Integer row) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaDtoTab2.get(row);
		try {
			dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
		} catch (Exception e) {
			dto.setMostrar(false);
			statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
			return;
		}

		if (dto.getPuntaje() < 0) {
			dto.setMostrar(false);
			statusMessages.add(Severity.WARN, "Los puntajes no pueden ser negativos. Verifique");
			return;
		}
		dto.setMostrar(true);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet = dto.getListaDetDescripContFuncional();
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		listaDet.add(det);
		listaDtoTab2.set(row, dto);
	}

	/**
	 * Método que agrega un registro a la sublista de Minimos requeridos de la lista gral
	 * 
	 * @param row
	 */
	public void agregarListaMinimosReqTab3(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoTab3.get(row);
		try {
			dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
		} catch (Exception e) {
			dto.setMostrar(false);
			statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
			return;
		}
		if (dto.getPuntaje() < 0) {
			dto.setMostrar(false);
			statusMessages.add(Severity.WARN, "Los puntajes no pueden ser negativos. Verifique");
			return;
		}
		dto.setMostrar(true);
		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet = dto.getListaDetMinimosRequeridos();

		DetMinimosRequeridos det = new DetMinimosRequeridos();

		listaDet.add(det);
		listaDtoTab3.set(row, dto);

	}

	/**
	 * Método que agrega un registro a la sublista de Opciones Convenientes de la lista gral
	 * 
	 * @param row
	 */
	public void agregarListaOpcConvenientesTab3(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoTab3.get(row);
		if (dto.getPuntaje() < 0) {
			dto.setMostrar(false);
			statusMessages.add(Severity.WARN, "Los puntajes no pueden ser negativos. Verifique");
			return;
		}
		dto.setMostrar(true);
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		dto.getListaDetOpcionesConvenientes().add(det);
		listaDtoTab3.set(row, dto);

	}

	/*********************************************************************
	 * Metodos que eliminan registros de sublistas
	 *********************************************************************/

	public void eliminarListaTab2(Integer row, Integer subRow) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaDtoTab2.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet.addAll(dto.getListaDetDescripContFuncional());
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		det = listaDet.get(subRow);

		listaDet.remove(det);

		dto.setListaDetDescripContFuncional(listaDet);
		listaDtoTab2.set(row, dto);

	}

	public void eliminarListaMinimosReqTab3(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoTab3.get(row);
		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet.addAll(dto.getListaDetMinimosRequeridos());
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		det = listaDet.get(subRow);

		listaDet.remove(det);

		dto.setListaDetMinimosRequeridos(listaDet);
		listaDtoTab3.set(row, dto);

	}

	public void eliminarListaOpcConvenientesTab3(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoTab3.get(row);
		List<DetOpcionesConvenientes> listaDet = new ArrayList<DetOpcionesConvenientes>();
		listaDet.addAll(dto.getListaDetOpcionesConvenientes());
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		det = listaDet.get(subRow);

		listaDet.remove(det);

		dto.setListaDetOpcionesConvenientes(listaDet);
		listaDtoTab3.set(row, dto);

	}

	/*******************************************************
	 * Metodos que buscan valoraciones
	 ******************************************************/
	/**
	 * Método que obtiene la valoracion correspondiente al tab02; contenido funcional
	 */
	@SuppressWarnings("unchecked")
	public void buscarValoracionTab02() {
		String cadena =
			"select funcional.* from planificacion.contenido_funcional funcional "
				+ "where funcional.activo is TRUE order by funcional.orden asc";
		listaValoracionTab2 =
			em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();
	}

	public List<ValorNivelOrg> traerValorNivelOrg(Long id) {
		Query q =
			em.createQuery("select ValorNivelOrg from ValorNivelOrg ValorNivelOrg "
				+ " where ValorNivelOrg.activo is true and ValorNivelOrg.contenidoFuncional.idContenidoFuncional = :id "
				+ " order by desde asc, hasta asc ");
		q.setParameter("id", id);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionTab03() {
		String cadena =
			"select cpt.* from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo is TRUE order by cpt.orden asc ";
		listaValoracionTab3 =
			em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
	}

	public List<EscalaReqMin> traerEscalaReqMin(Long id) {
		Query q =
			em.createQuery("select EscalaReqMin from EscalaReqMin EscalaReqMin "
				+ " where EscalaReqMin.activo is true and EscalaReqMin.requisitoMinimoCpt.idRequisitoMinimoCpt = :idRequisitoMinimoCpt "
				+ " order by desde asc, hasta asc");
		q.setParameter("idRequisitoMinimoCpt", id);
		return q.getResultList();

	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionTab04() {
		String cadena =
			"select trab.* from planificacion.condicion_trabajo trab"
				+ " where trab.activo is TRUE order by trab.orden asc";
		listaValoracionTab4 = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
	}

	public List<EscalaCondTrab> traerEscalaCondTrab(Long id) {
		Query q =
			em.createQuery("select EscalaCondTrab from EscalaCondTrab EscalaCondTrab "
				+ " where EscalaCondTrab.condicionTrabajo.idCondicionTrabajo = :id"
				+ " order by desde asc, hasta asc ");
		q.setParameter("id", id);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionTab05() {
		String cadena =
			"select escala.* from planificacion.escala_cond_trab_especif escala "
				+ "where escala.activo is TRUE order by desde asc, hasta asc";
		List<EscalaCondTrabEspecif> lista = new ArrayList<EscalaCondTrabEspecif>();
		listaValoracionTab5 = new ArrayList<ValoracionTab05CPT>();
		lista = em.createNativeQuery(cadena, EscalaCondTrabEspecif.class).getResultList();
		String cad =
			"select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo is TRUE order by cond.orden asc";
		List<CondicionTrabajoEspecif> listaCondTrabEspec = new ArrayList<CondicionTrabajoEspecif>();

		listaCondTrabEspec =
			em.createNativeQuery(cad, CondicionTrabajoEspecif.class).getResultList();

		for (CondicionTrabajoEspecif o : listaCondTrabEspec) {
			ValoracionTab05CPT valor = new ValoracionTab05CPT();
			valor.setCondicionTrabajoEspecif(o);
			valor.setListaEscala(lista);
			listaValoracionTab5.add(valor);
		}
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionTab06() {
		String cadena =
			"select escala.* from planificacion.escala_cond_segur escala "
				+ "where escala.activo is TRUE order by desde asc, hasta asc";
		List<EscalaCondSegur> lista = new ArrayList<EscalaCondSegur>();
		listaValoracionTab6 = new ArrayList<ValoracionTab06CPT>();
		lista = em.createNativeQuery(cadena, EscalaCondSegur.class).getResultList();

		String cad =
			"select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> listaSegur = new ArrayList<CondicionSegur>();

		listaSegur = em.createNativeQuery(cad, CondicionSegur.class).getResultList();

		for (CondicionSegur o : listaSegur) {
			ValoracionTab06CPT valor = new ValoracionTab06CPT();
			valor.setCondicionSegur(o);
			valor.setListaEscalaCondSeg(lista);
			listaValoracionTab6.add(valor);
		}
	}

	/********************************************************
	 * Métodos que verifican valoraciones
	 *********************************************************/

	/**
	 * Método que verifica si el puntaje ingresado en el tab03 se encuentra en el rango mostrado en la escala
	 * 
	 * @param requisito
	 * @return
	 */

	public Boolean verificarValoracionTab3(RequisitosMinimosDTO requisito) {

		List<EscalaReqMin> v = new ArrayList<EscalaReqMin>();
		v = requisito.getRequisitoMinimoCpt().getEscalaReqMins();
		Float valor = requisito.getPuntaje();
		Boolean cumple = false;
		for (EscalaReqMin o : v) {
			Float desde = new Float("" + o.getDesde());
			Float hasta = new Float("" + o.getHasta());
			if (desde.floatValue() <= valor.floatValue()
				&& hasta.floatValue() >= valor.floatValue()) {
				cumple = true;

			}
		}
		if (!cumple) {
			mensajeTab03 = "El puntaje ingresado no es valido, verifique la escala";
			return cumple;
		}
		mensajeTab03 = null;
		return cumple;
	}

	/**
	 * Método que verifica si el puntaje ingresado en el tab02 se encuentra en el rango mostrado en la escala
	 * 
	 * @param contenido
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean verificarValoracion(ContenidoFuncionalDTO contenido) {
		List<ValorNivelOrg> v = new ArrayList<ValorNivelOrg>();
		v = contenido.getContenidoFuncional().getValorNivelOrgs();
		Float valor = contenido.getPuntaje();
		Boolean cumple = false;
		for (ValorNivelOrg o : v) {
			Float desde = new Float("" + o.getDesde());
			Float hasta = new Float("" + o.getHasta());
			if (desde.floatValue() <= valor.floatValue()
				&& hasta.floatValue() >= valor.floatValue()) {
				cumple = true;
			}
		}
		if (!cumple) {
			mensajeTab02 = "El puntaje ingresado no es valido, verifique la escala";
			return cumple;
		}
		mensajeTab02 = null;
		return cumple;
	}

	public Boolean verificarValoracionTab04(CondicionTrabajoDTO condicion) {
		List<EscalaCondTrab> v = new ArrayList<EscalaCondTrab>();
		if (condicion.getPuntaje() != null) {
			v = condicion.getCondicionTrabajo().getEscalaCondTrabs();
			Float valor = condicion.getPuntaje();
			Boolean cumple = false;
			for (EscalaCondTrab o : v) {
				Float desde = new Float("" + o.getDesde());
				Float hasta = new Float("" + o.getHasta());
				if (desde.floatValue() <= valor.floatValue()
					&& hasta.floatValue() >= valor.floatValue()) {
					cumple = true;
				}
			}
			if (!cumple) {
				mensajeTab04 = "El puntaje ingresado no es valido, verifique la escala";
				return cumple;
			}
		}
		mensajeTab04 = null;

		return true;
	}

	public Boolean verificarValoracionTab05(CondicionTrabajoEspecifDTO condicion) {
		List<EscalaCondTrabEspecif> v = new ArrayList<EscalaCondTrabEspecif>();
		if (condicion.getPuntaje() != null) {
			v = listaValoracionTab5.get(0).getListaEscala();
			Float valor = condicion.getPuntaje();
			Boolean cumple = false;
			for (EscalaCondTrabEspecif o : v) {
				Float desde = new Float("" + o.getDesde());
				Float hasta = new Float("" + o.getHasta());
				if (desde.floatValue() <= valor.floatValue()
					&& hasta.floatValue() >= valor.floatValue()) {
					cumple = true;
				}
			}
			if (!cumple) {
				mensajeTab05 = "El puntaje ingresado no es valido, verifique la escala";
				return cumple;
			}
		}
		mensajeTab05 = null;
		return true;
	}

	public Boolean verificarValoracionTab06(CondicionSeguridadDTO condicion) {
		List<EscalaCondSegur> v = new ArrayList<EscalaCondSegur>();
		if (condicion.getPuntaje() != null) {
			v = listaValoracionTab6.get(0).getListaEscalaCondSeg();
			Float valor = condicion.getPuntaje();
			Boolean cumple = false;
			for (EscalaCondSegur o : v) {
				Float desde = new Float("" + o.getDesde());
				Float hasta = new Float("" + o.getHasta());
				if (desde.floatValue() <= valor.floatValue()
					&& hasta.floatValue() >= valor.floatValue()) {
					cumple = true;
				}
			}
			if (!cumple) {
				mensajeTab06 = "El puntaje ingresado no es valido, verifique la escala";
				return cumple;
			}
		}
		mensajeTab06 = null;
		return true;
	}

	public void selectTab(int tabIndex) {
		switch (tabIndex) {
		case 1:
			selectedTab = TAB_NOMBRAMIENTO;
			break;
		case 2:
			selectedTab = TAB_CONTENIDO_FUNCIONAL;
			break;
		case 3:
			selectedTab = TAB_REQUISITOS_MIN;
			break;
		case 4:
			selectedTab = TAB_CONDICION_TRAB;
			break;
		case 5:
			selectedTab = TAB_CONDICION_TRAB_ESPECIF;
			break;
		case 6:
			selectedTab = TAB_CONDICION_SEGURIDAD;
			break;
		default:
			selectedTab = TAB_NOMBRAMIENTO;
			break;
		}
	}

	/*****************************************************
	 * Metodos que realizan procesos
	 *****************************************************/

	private Boolean validacionesEscalas() {
		for (ContenidoFuncionalDTO dto : listaDtoTab2) {
			if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
				dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
				if (!verificarValoracion(dto)) {
					selectTab(2);
					return false;
				}
			}
		}
		for (RequisitosMinimosDTO dto : listaDtoTab3) {
			if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
				dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
				if (!verificarValoracionTab3(dto)) {
					selectTab(3);
					return false;
				}
			}
		}
		for (CondicionTrabajoDTO dto : listaCondicionDtoTab4) {
			if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
				try {
					dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
				} catch (Exception e) {
					statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
					return false;
				}
				if (!verificarValoracionTab04(dto)) {
					selectTab(4);
					return false;
				}
			}
		}
		for (CondicionTrabajoEspecifDTO dto : listaDtoTab5) {
			if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
				try {
					dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
				} catch (Exception e) {
					statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
					return false;
				}
				if (!verificarValoracionTab05(dto)) {
					selectTab(5);
					return false;
				}
			}
		}
		for (CondicionSeguridadDTO dto : listaDtoTab6) {
			if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
				try {
					dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
				} catch (Exception e) {
					statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
					return false;
				}
				if (!verificarValoracionTab06(dto)) {
					selectTab(6);
					return false;
				}
			}
		}
		return true;
	}

	private Boolean validacionesVarias() {
		if (cpt.getNivel() < 0) {
			statusMessages.add(Severity.WARN, "No se permiten valores negativos en Nivel");
			return false;
		}
		if (cpt.getNumero() < 0) {
			statusMessages.add(Severity.WARN, "No se permiten valores negativos en Número");
			return false;
		}
		if ("especifico".equalsIgnoreCase(tipo)) {		
			if (cpt.getNroEspecifico() < 0) {
				statusMessages.add(Severity.WARN, "No se permiten valores negativos en Número Específico");
				return false;
			}
		}
		if (seguridadUtilFormController.contieneCaracter(cpt.getDenominacion().trim())) {
			statusMessages.add(Severity.WARN, "Ingrese caracteres válidos en Denominación");
			return false;
		}

		return true;
	}
	/**
	 * Método que llama al Reporte para Imprimir los datos del CPT
	 */
	public void imprimirCpt() {
			
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idCpt",this.idCpt);
		
		JasperReportUtils.respondPDF("RPT_imprimirCpt",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Método que llama al Reporte para Imprimir los datos del CPT
	 */
	public void imprimirCptNuevoEscalasParametros() {
			
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		if(this.idCpt == null)
			this.idCpt = this.cpt.getId();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idCpt",this.idCpt);
		
		
		//Consulta para recuperar todos los registros para la seccion de valorizacion de los componentes
		String sql = "select distinct req_min_cpt.orden " //0
				+ " ,req_min_cpt.descripcion as componente "//
				+ " ,escala.descripcion"//2
				+ " ,escala.desde "//6
				+ " ,escala.hasta"//4
				+ " ,det_req_min.puntaje_req_min as puntaje "//5
				+ " from planificacion.det_minimos_requeridos det_min "
				+ " join planificacion.det_req_min det_req_min "
				+ " on det_req_min.id_det_req_min = det_min.id_det_req_min "
				+ " join planificacion.requisito_minimo_cpt req_min_cpt "
				+ " on req_min_cpt.id_requisito_minimo_cpt = det_req_min.id_requisito_minimo_cpt "
				+ " join planificacion.escala_req_min escala "
				+ " on escala.id_requisito_minimo_cpt = req_min_cpt.id_requisito_minimo_cpt "
				+ " where det_min.id_det_req_min in "
				+ " (select id_det_req_min from planificacion.det_req_min drm where id_cpt = "+ this.idCpt+ ") "
				+ "  ORDER BY  orden,componente, desde";
		
		List<Object[]> lista = em.createNativeQuery(sql).getResultList();
		
		int fila = 0;
		int columna = 1;
		String componente = "";
		String escala = "";
		Float puntaje;
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("tipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("escala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("puntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
			
		
		JasperReportUtils.respondPDF("RPT_imprimirCpt_nuevo_parametros",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que guarda los datos en base de datos
	 * 
	 * @return
	 */
	public String save() {
		try {
			cargarNroEspecifico();
			if (conJefatura == null)
				conJefatura = false;
			
			if (idGradoSalarialMax == null || idGradoSalarialMin == null) {
				statusMessages.clear();
				statusMessages.add(Severity.WARN, "Ingrese los grados salariales correspondientes.");
				return null;
			}
			
			
			if (cpt.getNivel() == null) {
				statusMessages.add(Severity.WARN, "Debe Seleccionar un Nivel. Verifique");
				return null;
			}
			
			if (cpt.getNivel() != null && cpt.getNivel().intValue() < 4 && conJefatura) {
				statusMessages.add(Severity.WARN, "Los Niveles menores a 4 no pueden ser de Jefatura. Verifique");
				return null;
			}

			if (!validacionesVarias())
				return null;

			if (!validacionesEscalas())
				return null;

			if (!verificarRepetidos())
				return null;
			
			if(this.faltanDescripcionesContenidoFuncional(listaDtoTab2)){
				
				statusMessages.add(Severity.WARN, "Debe Ingresar Ingresar los puntajes y las descripciones correspondientes en Contenido Funcional. Verifique");
				return null;
				
			}
			
			
			if(!this.tipo.equals("general") && this.faltanDescripcionesRequisitosMinimos( listaDtoTab3)){
				
				statusMessages.add(Severity.WARN, "Debe Ingresar Ingresar los puntajes y las descripciones correspondientes en Requisitos Minimos. Verifique");
				return null;
			}
				
			
			

			cpt.setActivo(activo);
			ConfiguracionUoCab configuracionUoCab = em.find(ConfiguracionUoCab.class, this.usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			cpt.setConfiguracionUoCab(configuracionUoCab);
			cpt.setGradoSalarialMax(em.find(GradoSalarial.class, idGradoSalarialMax));

			cpt.setGradoSalarialMin(em.find(GradoSalarial.class, idGradoSalarialMin));
			cpt.setDenominacion(cpt.getDenominacion().trim().toUpperCase());
			
			if (idTipoCpt != null)
				cpt.setTipoCpt(em.find(TipoCpt.class, idTipoCpt));
			cpt.setJefatura(conJefatura);
			if(cptPadre != null && cptPadre.getIdCpt()!=null)
				cpt.setCptPadre(cptPadre);
			cptHome.setInstance(cpt);
			String resultOperation = cptHome.persist();
			
			
			if (resultOperation.equals("persisted")) {
				
				//JD.guarda los niveles de cargo asociados a un CPT
				if(listaCptNivelesCargos != null && listaCptNivelesCargos.size()>0){
							String transaccion;
					
							for(int i=0;i<listaCptNivelesCargos.size();i++){
								CptNivelesCargos aux= listaCptNivelesCargos.get(i);
								aux.setIdCpt(cpt.getIdCpt());
									
																
								cptNivelesDeCargosHome.setInstance(aux);					
								transaccion=cptNivelesDeCargosHome.persist();
							}
						
				}
				//JD.fin guardado de cptnivelescargos
				if (cptPadre == null) {
					for (TipoPlanta o : listaTipoPlanta) {
						List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
						lista = o.getTipoNombramientos();
						for (TipoNombramiento obj : lista) {
							if (obj.getSeleccionado() != null
									&& obj.getSeleccionado()) {
								DetTipoNombramiento det = new DetTipoNombramiento();
								det.setCpt(cptHome.getInstance());
								det.setTipoNombramiento(obj);
								det.setTipo("CPT");
								em.persist(det);

							}
						}

					}
				}
				for (ContenidoFuncionalDTO dto : listaDtoTab2) {

					if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
						dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
						if (dto.getPuntaje() >= 0) {
							DetContenidoFuncional detContenido = new DetContenidoFuncional();
							detContenido.setContenidoFuncional(dto.getContenidoFuncional());
							detContenido.setPuntaje(dto.getPuntaje());
							detContenido.setTipo("CPT");
							detContenido.setActivo(true);
							detContenido.setCpt(cptHome.getInstance());
							detContenidoFuncionalHome.setInstance(detContenido);
							String resultado = detContenidoFuncionalHome.persist();
							if (resultado.equals("persisted")) {
								List<DetDescripcionContFuncional> listaDescripcion =
									new ArrayList<DetDescripcionContFuncional>();
								listaDescripcion = dto.getListaDetDescripContFuncional();
								for (DetDescripcionContFuncional desc : listaDescripcion) {
									if (desc.getDescripcion() != null
										&& !desc.getDescripcion().equals("")) {
										desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
									}
									else{
										statusMessages.add(Severity.WARN, "Debe Ingresar las Descripciones correspondientes. Verifique");
										return null;
									}
									
										desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
										desc.setActivo(true);
										em.persist(desc);
									
								}

								detContenidoFuncionalHome.clearInstance();
							}

						} else {
							statusMessages.clear();
							statusMessages.add(Severity.WARN, "Los puntajes no pueden ser negativos. Verifique");
							return null;
						}
					}else {
						
						List<DetDescripcionContFuncional> listaDescripcion =new ArrayList<DetDescripcionContFuncional>();
						listaDescripcion = dto.getListaDetDescripContFuncional();
						for (DetDescripcionContFuncional desc : listaDescripcion) {
							if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
									
								statusMessages.clear();
								this.mensajeTab02 = "Existen Descripciones con puntajes nulos. Verifique";
								statusMessages.add(Severity.WARN, mensajeTab02);
								return null;
							}else{
								statusMessages.add(Severity.WARN, "Debe Ingresar las Descripciones correspondientes. Verifique");
								return null;
							}
						}
						
					}

				}

				for (RequisitosMinimosDTO dto : listaDtoTab3) {
					if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
						dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
						if (dto.getPuntaje() >= 0) {
							DetReqMin detReqMin = new DetReqMin();
							detReqMin.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
							detReqMin.setPuntajeReqMin(dto.getPuntaje());
							detReqMin.setTipo("CPT");
							detReqMin.setCpt(cptHome.getInstance());
							detReqMin.setActivo(true);
							detReqMinHome.setInstance(detReqMin);
							String resultado = detReqMinHome.persist();
							if (resultado.equals("persisted")) {
								List<DetMinimosRequeridos> listaDetMinimos =
									new ArrayList<DetMinimosRequeridos>();
								listaDetMinimos = dto.getListaDetMinimosRequeridos();
								for (DetMinimosRequeridos detMin : listaDetMinimos) {
									if (detMin.getMinimosRequeridos() != null
										&& !detMin.getMinimosRequeridos().equals("")) {
										detMin.setMinimosRequeridos(detMin.getMinimosRequeridos().trim().toUpperCase());
										detMin.setActivo(true);
										detMin.setDetReqMin(detReqMinHome.getInstance());

										em.persist(detMin);
									}else{
										statusMessages.add(Severity.WARN, "Debe Ingresar las Descripciones correspondientes. Verifique");
										return null;
									}
								}


								List<DetOpcionesConvenientes> listaDetOpc =
									new ArrayList<DetOpcionesConvenientes>();
								listaDetOpc = dto.getListaDetOpcionesConvenientes();
								for (DetOpcionesConvenientes detOpc : listaDetOpc) {
									if (detOpc.getOpcConvenientes() != null
										&& !detOpc.getOpcConvenientes().equals("")) {
										detOpc.setOpcConvenientes(detOpc.getOpcConvenientes().trim().toUpperCase());
										detOpc.setActivo(true);
										detOpc.setDetReqMin(detReqMinHome.getInstance());

										em.persist(detOpc);
									}
								}


								detReqMinHome.clearInstance();
							}
						} else {
							
							
							statusMessages.clear();
							statusMessages.add(Severity.WARN, "Los puntajes no pueden ser negativos. Verifique");
							return null;
						}
					} else {
						List<DetMinimosRequeridos> listaDetMinimos =new ArrayList<DetMinimosRequeridos>();
						listaDetMinimos = dto.getListaDetMinimosRequeridos();
						for (DetMinimosRequeridos detMin : listaDetMinimos) {
							if (detMin.getMinimosRequeridos() != null
								&& !detMin.getMinimosRequeridos().equals("")) {
						
								statusMessages.clear();
								this.mensajeTab03 = "Existen Descripciones con puntajes nulos. Verifique";
								statusMessages.add(Severity.WARN,mensajeTab03 );
								return null;
						
							}
						}
						
						List<DetOpcionesConvenientes> listaDetOpc =	new ArrayList<DetOpcionesConvenientes>();
							listaDetOpc = dto.getListaDetOpcionesConvenientes();
							for (DetOpcionesConvenientes detOpc : listaDetOpc) {
								if (detOpc.getOpcConvenientes() != null
									&& !detOpc.getOpcConvenientes().equals("")) {
									statusMessages.clear();
									this.mensajeTab03 = "Existen Descripciones con puntajes nulos. Verifique";
									statusMessages.add(Severity.WARN,mensajeTab03 );
									return null;
								}
							}
					}
				}
				

				
			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return getUrlToPageList();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}

		
	}
	
	private boolean faltanDescripcionesContenidoFuncional(List<ContenidoFuncionalDTO> listaDtoTab2){
				
		List<DetDescripcionContFuncional> listaDescripcion =new ArrayList<DetDescripcionContFuncional>();
		
		for (ContenidoFuncionalDTO dto : listaDtoTab2) {
			listaDescripcion = dto.getListaDetDescripContFuncional();
			
			for (DetDescripcionContFuncional desc : listaDescripcion) {
				if (desc.getDescripcion() == null || desc.getDescripcion().equals("")) {
					
					return true;
					
				}
			}
			
			if (dto.getPuntaje() == null)
				return true;
		}
			return false;
		
		
	}
	
	private boolean faltanDescripcionesRequisitosMinimos(List<RequisitosMinimosDTO> listaDtoTab3){
		List<DetMinimosRequeridos> listaDetMinimos =new ArrayList<DetMinimosRequeridos>();
		List<DetOpcionesConvenientes> listaOpcionesConvenientes = new ArrayList<DetOpcionesConvenientes>();
		for (RequisitosMinimosDTO dto : listaDtoTab3) {
			listaDetMinimos = dto.getListaDetMinimosRequeridos();
			listaOpcionesConvenientes = dto.getListaDetOpcionesConvenientes();
			
			DetMinimosRequeridos detMin =  !listaDetMinimos.equals(null)?listaDetMinimos.get(0):null;
			DetOpcionesConvenientes opcConv = !listaOpcionesConvenientes.equals(null)?listaOpcionesConvenientes.get(0):null;
			if ( detMin != null ) {
				if (detMin.getMinimosRequeridos() == null || detMin.getMinimosRequeridos().equals("")) {
						if(opcConv != null){
							if(opcConv.getOpcConvenientes() == null || opcConv.getOpcConvenientes().equals("")){
								return true;
							}else{
								detMin.setMinimosRequeridos(" ");
							}
						}else							
						  return true;
				}
			}
			if (dto.getPuntaje() == null)
				return true;
		}
			return false;
		
		
	}

	@SuppressWarnings("unchecked")
	public boolean verificarRepetidos() {

		if ("especifico".equalsIgnoreCase(tipo)) {
			String consulta =
				" select cpt from Cpt cpt " + " join cpt.tipoCpt tipoCpt "
					+ " where cpt.nivel = :nivel " + "    and cpt.numero = :numero "
					+ "    and cpt.nroEspecifico = :nroEspecifico "
					+ "    and tipoCpt.idTipoCpt = :idTipoCpt ";

			if (cpt.getId() != null)
				consulta += "    and cpt.idCpt <> :idCpt ";

			Query q = em.createQuery(consulta);
			q.setParameter("nivel", cpt.getNivel());
			q.setParameter("numero", cpt.getNumero());
			q.setParameter("nroEspecifico", cpt.getNroEspecifico());
			q.setParameter("idTipoCpt", idTipoCpt);

			if (cpt.getId() != null)
				q.setParameter("idCpt", cpt.getId());

			List<Cpt> lista = q.getResultList();
			if (lista != null && lista.size() > 0 ) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El registro ya existe. Verifique.");
				return false;
			}
		}

		if ("general".equalsIgnoreCase(tipo)) {
			String consulta =
				" select cpt from Cpt cpt " + " where cpt.nivel = :nivel "
					+ "    and cpt.numero = :numero and cpt.cptPadre is null ";

			if (cpt.getId() != null)
				consulta += "    and cpt.idCpt <> :idCpt ";

			Query q = em.createQuery(consulta);
			q.setParameter("nivel", cpt.getNivel());
			q.setParameter("numero", cpt.getNumero());

			if (cpt.getId() != null)
				q.setParameter("idCpt", cpt.getId());

			List<Cpt> lista = q.getResultList();
			if (lista != null && lista.size() > 0 ) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El registro ya existe. Verifique.");
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public Integer obtenerNumeroEspecifico() {

		if (cpt.getNivel() != null && cpt.getNumero() != null && idTipoCpt != null) {
			try {
				String consulta =
					" select max(cpt.nroEspecifico) from Cpt cpt " + " join cpt.tipoCpt tipoCpt "
						+ " where cpt.nivel = :nivel " + "    and cpt.numero = :numero "
						+ "    and tipoCpt.idTipoCpt = :idTipoCpt ";

				Query q = em.createQuery(consulta);
				q.setParameter("nivel", cpt.getNivel());
				q.setParameter("numero", cpt.getNumero());
				q.setParameter("idTipoCpt", idTipoCpt);

				List<Integer> lista = q.getResultList();
				if (lista != null && lista.size() > 0) {
					Object o = lista.get(0);
					if (o == null)
						return new Integer(1);

					Integer nro = (Integer) o;
					return nro + 1;
				} else {
					return new Integer(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void cargarNroEspecifico() {
		if ("especifico".equalsIgnoreCase(tipo) && cpt.getId() == null) {
			Integer nro = obtenerNumeroEspecifico();
			cpt.setNroEspecifico(nro);
		}
	}

	public void cargarVariable() {
		
	}

	public String getUrlToPageList() {
		
		if(tipo.equalsIgnoreCase("especifico"))
			 if((cptListFormController != null && cptListFormController.isEstadoParaHomologacion(this.cpt.getEstadoHomologacion())))
				 return "/planificacion/cpt/CptGestionarHomologacion.xhtml?fromX=planificacion/cpt/CptEdit&tipo=" + tipo;
			 else
				 return "/planificacion/cpt/CptHomologacionList.xhtml?fromX=planificacion/cpt/CptEdit&tipo=" + tipo;
		else
			return "updatedCpt";
			//return "/planificacion/cpt/CptList.seam?tipo=" + tipo;
	}

	public String saveMore() {
//		try {
//			cargarNroEspecifico();
//			if (idGradoSalarialMax == null || idGradoSalarialMin == null) {
//				statusMessages.clear();
//				statusMessages.add(Severity.WARN, "Ingrese los grados salariales correspondientes.");
//				return null;
//			}
//
//			if (cpt.getNivel() == null) {
//				statusMessages.add("Debe Seleccionar un Nivel. Verifique");
//				return null;
//			}
//			if (cpt.getNivel() != null && cpt.getNivel().intValue() < 4
//				&& (conJefatura != null && conJefatura)) {
//				statusMessages.add("Los Niveles menores a 4 no pueden ser de Jefatura. Verifique");
//				return null;
//			}
//			if (!validacionesVarias())
//				return null;
//
//			if (!validacionesEscalas())
//				return null;
//
//			if (!verificarRepetidos())
//				return null;
//			
//			cpt.setActivo(activo);
//
//			cpt.setGradoSalarialMax(em.find(GradoSalarial.class, idGradoSalarialMax));
//
//			cpt.setGradoSalarialMin(em.find(GradoSalarial.class, idGradoSalarialMin));
//			cpt.setDenominacion(cpt.getDenominacion().trim().toUpperCase());
//			if (idTipoCpt != null)
//				cpt.setTipoCpt(em.find(TipoCpt.class, idTipoCpt));
//
//			cpt.setJefatura(conJefatura);
//			if(cptPadre != null)
//				cpt.setCptPadre(cptPadre);
//			cptHome.setInstance(cpt);
//			String resultOperation = cptHome.persist();
//		
//			
//
//			
//			
//			if (resultOperation.equals("persisted")) {
//				
//				//JD.guarda los niveles de cargo asociados a un CPT
//				if(listaCptNivelesCargos != null && listaCptNivelesCargos.size()>0){
//							String transaccion;
//							if(modoEditar){
//								em.createNativeQuery("delete from planificacion.cpt_niveles_cargos where cpt_niveles_cargos.id_cpt= "
//									+cpt.getIdCpt()).executeUpdate();
//							
//							}
//							for(int i=0;i<listaCptNivelesCargos.size();i++){
//								CptNivelesCargos aux= listaCptNivelesCargos.get(i);
//								aux.setIdCpt(cpt.getIdCpt());
//									
//								System.out.println(" aux.getIdCpt:"+ aux.getIdCpt());
//								System.out.println(" aux.getIdNivelesCargos:"+ aux.getIdNivelesCargos());
//								System.out.println(" aux.IdCptNivelesCargos:"+ aux.getIdCptNivelesCargos());
//									
//								cptNivelesDeCargosHome.setInstance(aux);					
//								transaccion=cptNivelesDeCargosHome.persist();
//							}
//						
//				}
//				//JD.fin guardado de cptnivelescargos
//				
//				if (cptPadre == null) {
//					for (TipoPlanta o : listaTipoPlanta) {
//						List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
//						lista = o.getTipoNombramientos();
//						for (TipoNombramiento obj : lista) {
//							if (obj.getSeleccionado() != null
//									&& obj.getSeleccionado()) {
//								DetTipoNombramiento det = new DetTipoNombramiento();
//								det.setCpt(cptHome.getInstance());
//								det.setTipoNombramiento(obj);
//								det.setTipo("CPT");
//
//								em.persist(det);
//								em.flush();
//							}
//						}
//
//					}
//				}
//				for (ContenidoFuncionalDTO dto : listaDtoTab2) {
//					if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
//						dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
//
//						DetContenidoFuncional detContenido = new DetContenidoFuncional();
//						detContenido.setContenidoFuncional(dto.getContenidoFuncional());
//						detContenido.setPuntaje(dto.getPuntaje());
//						detContenido.setTipo("CPT");
//						detContenido.setActivo(true);
//						detContenido.setCpt(cptHome.getInstance());
//						detContenidoFuncionalHome.setInstance(detContenido);
//						String resultado = detContenidoFuncionalHome.persist();
//						if (resultado.equals("persisted")) {
//							List<DetDescripcionContFuncional> listaDescripcion =
//								new ArrayList<DetDescripcionContFuncional>();
//							listaDescripcion = dto.getListaDetDescripContFuncional();
//							for (DetDescripcionContFuncional desc : listaDescripcion) {
//								if (desc.getDescripcion() != null
//									&& !desc.getDescripcion().equals("")) {
//									desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
//									desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
//									em.persist(desc);
//								}
//							}
//							em.flush();
//							detContenidoFuncionalHome.clearInstance();
//						}
//					}
//
//				}
//
//				for (RequisitosMinimosDTO dto : listaDtoTab3) {
//					if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
//						dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
//						DetReqMin detReqMin = new DetReqMin();
//						detReqMin.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
//						detReqMin.setPuntajeReqMin(dto.getPuntaje());
//						detReqMin.setTipo("CPT");
//						detReqMin.setActivo(true);
//						detReqMin.setCpt(cptHome.getInstance());
//						detReqMinHome.setInstance(detReqMin);
//						String resultado = detReqMinHome.persist();
//						if (resultado.equals("persisted")) {
//							List<DetMinimosRequeridos> listaDetMinimos =
//								new ArrayList<DetMinimosRequeridos>();
//							listaDetMinimos = dto.getListaDetMinimosRequeridos();
//							for (DetMinimosRequeridos detMin : listaDetMinimos) {
//								if (detMin.getMinimosRequeridos() != null
//									&& !detMin.getMinimosRequeridos().equals("")) {
//									detMin.setMinimosRequeridos(detMin.getMinimosRequeridos().trim().toUpperCase());
//									detMin.setDetReqMin(detReqMinHome.getInstance());
//									detMin.setActivo(true);
//									em.persist(detMin);
//								}
//							}
//							em.flush();
//
//							List<DetOpcionesConvenientes> listaDetOpc =
//								new ArrayList<DetOpcionesConvenientes>();
//							listaDetOpc = dto.getListaDetOpcionesConvenientes();
//							for (DetOpcionesConvenientes detOpc : listaDetOpc) {
//								if (detOpc.getOpcConvenientes() != null
//									&& !detOpc.getOpcConvenientes().equals("")) {
//									detOpc.setOpcConvenientes(detOpc.getOpcConvenientes().trim().toUpperCase());
//									detOpc.setDetReqMin(detReqMinHome.getInstance());
//									detOpc.setActivo(true);
//									em.persist(detOpc);
//								}
//							}
//							em.flush();
//
//							detReqMinHome.clearInstance();
//						}
//					}
//				}
//			/*	for (CondicionTrabajoDTO dto : listaCondicionDtoTab4) {
//					if (dto.getPuntajeString() != null && !dto.getPuntajeString().trim().isEmpty()) {
//						try {
//							dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
//						} catch (Exception e) {
//							statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
//							return null;
//						}
//						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
//						detCondTrab.setActivo(dto.getActivo());
//						detCondTrab.setCondicionTrabajo(dto.getCondicionTrabajo());
//						detCondTrab.setCpt(cptHome.getInstance());
//						detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
//						detCondTrab.setTipo("CPT");
//						detCondTrab.setActivo(true);
//						em.persist(detCondTrab);
//					}
//
//				}
//				em.flush();*/
//
//			/*	for (CondicionTrabajoEspecifDTO dto : listaDtoTab5) {
//					if (dto.getPuntajeString() != null && dto.getAjustes() != null
//						&& dto.getJustificacion() != null
//						&& !dto.getPuntajeString().trim().isEmpty()) {
//						try {
//							dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
//						} catch (Exception e) {
//							statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
//							return null;
//						}
//						DetCondicionTrabajoEspecif det = new DetCondicionTrabajoEspecif();
//						det.setActivo(dto.getActivo());
//						det.setAjustes(dto.getAjustes().trim().toUpperCase());
//						det.setCondicionTrabajoEspecif(dto.getCondicionTrabajoEspecif());
//						det.setCpt(cptHome.getInstance());
//						det.setJustificacion(dto.getJustificacion().trim().toUpperCase());
//						det.setPuntajeCondTrabEspecif(dto.getPuntaje());
//						det.setTipo("CPT");
//						det.setActivo(true);
//
//						em.persist(det);
//					}
//
//				}
//				em.flush();*/
//
//				/*for (CondicionSeguridadDTO dto : listaDtoTab6) {
//					if (dto.getPuntajeString() != null && dto.getAjustes() != null
//						&& dto.getJustificacion() != null
//						&& !dto.getPuntajeString().trim().isEmpty()) {
//						try {
//							dto.setPuntaje(Float.valueOf(dto.getPuntajeString()));
//						} catch (Exception e) {
//							statusMessages.add(Severity.WARN, "Los puntajes deben ser numéricos. Verifique");
//							return null;
//						}
//						DetCondicionSegur det = new DetCondicionSegur();
//						det.setActivo(dto.getActivo());
//						det.setAjustes(dto.getAjustes().trim().toUpperCase());
//						det.setCondicionSegur(dto.getCondicionSegur());
//						det.setCpt(cptHome.getInstance());
//						det.setJustificacion(dto.getJustificacion().trim().toUpperCase());
//						det.setPuntajeCondSegur(dto.getPuntaje());
//						det.setTipo("CPT");
//						det.setActivo(true);
//						em.persist(det);
//					}
//
//				}
//				em.flush();*/
//
//				/**
//				 * Categorias posibles
//				 */
//
//				/*for (CategoriaDTO c : listaCategoriasDTO) {
//					String categ = c.getCategoria();
//					if (categ != null && !categ.equals("")) {
//						CategoriaCpt cat = new CategoriaCpt();
//
//						cat.setCategoria(categ);
//						cat.setCpt(cptHome.getInstance());
//						cat.setFechaAlta(new Date());
//						cat.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//
//						em.persist(cat);
//					}
//				}
//				em.flush();*/
//			}
//			statusMessages.clear();
//			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
//			// cptHome.clearInstance();
//			buscarTipoPlanta();
//			buscarContenidoFuncionalTab2();
//			//buscarRequisitosMinimosCPTTab3();
//			//buscarCondicionesTrabajo();
//			//buscarCondicionesTrabajoEspecif();
//			//buscarCondicionesSeguridad();
//			activo = true;
//			idGradoSalarialMax = null;
//			idGradoSalarialMin = null;
//			conJefatura = false;
//			cptPadre = new Cpt();
//			codigoCpt = null;
//			iniciarCategoriasPosibles();
//			idTipoCce = null;
//			return resultOperation;
//			} catch (Exception e) {
//			e.printStackTrace();
//			statusMessages.clear();
//			statusMessages.add(Severity.ERROR, e.getMessage());
//		}

		return null;
	}

	/**
	 * Metodo que guarda los datos en base de datos
	 * 
	 * @return
	 */
	public String updated() {
		try {
			if (idGradoSalarialMax == null || idGradoSalarialMin == null) {
				statusMessages.clear();
				statusMessages.add(Severity.WARN, "Ingrese los grados salariales correspondientes.");
				return null;
			}
			if (cpt.getNivel() == null) {
				statusMessages.add("Debe Seleccionar un Nivel. Verifique");
				return null;
			}
			if (cpt.getNivel() != null && cpt.getNivel().intValue() < 4 && conJefatura) {
				statusMessages.add("Los Niveles menores a 4 no pueden ser de Jefatura. Verifique");
				return null;
			}
			if (!validacionesVarias())
				return null;

			if (!validacionesEscalas())
				return null;

			if (!verificarRepetidos())
				return null;
			
			if(this.faltanDescripcionesContenidoFuncional(listaDtoTab2)){
				
				statusMessages.add(Severity.WARN, "Debe Ingresar Ingresar los puntajes y las descripciones correspondientes en Contenido Funcional. Verifique");
				return null;
				
			}
			
			if(!this.tipo.equals("general") && this.faltanDescripcionesRequisitosMinimos( listaDtoTab3)){
				
				statusMessages.add(Severity.WARN, "Debe Ingresar los puntajes y las descripciones correspondientes en Requisitos Mínimos o Opciones Convenientes. Verifique");
				return null;
			}

			cpt.setActivo(activo);

			cpt.setGradoSalarialMax(em.find(GradoSalarial.class, idGradoSalarialMax));

			cpt.setGradoSalarialMin(em.find(GradoSalarial.class, idGradoSalarialMin));
			cpt.setDenominacion(cpt.getDenominacion().trim().toUpperCase());
			if (idTipoCpt != null)
				cpt.setTipoCpt(em.find(TipoCpt.class, idTipoCpt));
			cpt.setJefatura(conJefatura);

			if(cptPadre != null)
				cpt.setCptPadre(cptPadre);
			
			cptHome.setInstance(cpt);
			String resultOperation = cptHome.update();

			if (resultOperation.equals("updated")) {
				
				//JD.guarda los niveles de cargo asociados a un CPT
				if(listaCptNivelesCargos != null){
							String transaccion;
							if(cpt.getIdCpt()!=null){
								System.out.println("INI BORRADO DE CPTNIVELESCARGO");
								int r = em.createNativeQuery("delete from planificacion.cpt_niveles_cargos where cpt_niveles_cargos.id_cpt= "
									+cpt.getIdCpt()).executeUpdate();
								em.flush();
								System.out.println("BORRADO DE CPTNIVELESCARGO : " + r);
							}
							
							for(int i=0;i<listaCptNivelesCargos.size();i++){
								CptNivelesCargos aux= listaCptNivelesCargos.get(i);
								aux.setIdCpt(cpt.getIdCpt());
									
								System.out.println(" aux.getIdCpt:"+ aux.getIdCpt());
								System.out.println(" aux.getIdNivelesCargos:"+ aux.getIdNivelesCargos());
								System.out.println(" aux.IdCptNivelesCargos:"+ aux.getIdCptNivelesCargos());
									
								cptNivelesDeCargosHome.setInstance(aux);					
								transaccion=cptNivelesDeCargosHome.persist();
							}
						
				}
				//JD.fin guardado de cptnivelescargos
				
				
				
				
				
				
				/**
				 * Tab 01
				 */
				for (TipoPlanta o : listaTipoPlanta) {
					List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
					lista = o.getTipoNombramientos();
					for (DetTipoNombramiento tb1 : listaAuxTab1) {
						for (TipoNombramiento obj : lista) {
							if (tb1.getTipoNombramiento().getIdTipoNombramiento().equals(obj.getIdTipoNombramiento())) {
								if (obj.getSeleccionado() != null && obj.getSeleccionado()) {
									em.merge(tb1);
									// em.flush();
								}
								if (obj.getSeleccionado() != null && !obj.getSeleccionado()) {
									em.remove(tb1);
									// em.flush();
								}
							}
						}
					}
				}
				for (TipoPlanta o : listaTipoPlanta) {
					List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
					lista = o.getTipoNombramientos();
					for (TipoNombramiento obj : lista) {
						Boolean esta = false;
						for (DetTipoNombramiento tb1 : listaAuxTab1) {
							if (tb1.getTipoNombramiento().getIdTipoNombramiento().equals(obj.getIdTipoNombramiento()))
								esta = true;
						}
						if (!esta) {
							if (obj.getSeleccionado() != null && obj.getSeleccionado()) {
								DetTipoNombramiento det = new DetTipoNombramiento();
								det.setCpt(cptHome.getInstance());
								det.setTipoNombramiento(obj);
								det.setTipo("CPT");
								em.persist(det);
								// em.flush();
							}
						}
					}
				}
				/**
				 * Tab 02
				 */
				for (ContenidoFuncionalDTO dto : listaDtoTab2) {
					if (dto.getId() != null) {
						DetContenidoFuncional detContenido = new DetContenidoFuncional();
						detContenido = em.find(DetContenidoFuncional.class, dto.getId());
						
						if(dto.getPuntaje() == null)
							detContenido.setPuntaje(new Float(0));
						else
							detContenido.setPuntaje(dto.getPuntaje());
						
						detContenidoFuncionalHome.setInstance(detContenido);
						String resultado = detContenidoFuncionalHome.update();
						if (resultado.equals("updated")) {
							List<DetDescripcionContFuncional> listaDescripcion =
								new ArrayList<DetDescripcionContFuncional>();
							listaDescripcion = dto.getListaDetDescripContFuncional();
							for (DetDescripcionContFuncional desc : listaDescripcion) {
								if (desc.getDescripcion() != null
										&& !desc.getDescripcion().equals("")) {
										desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
									}
								
									
										desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
										desc.setActivo(true);
										em.persist(desc);
							}
						}
					} else {
						if (dto.getPuntaje() != null) {
							DetContenidoFuncional detContenido = new DetContenidoFuncional();
							detContenido.setContenidoFuncional(dto.getContenidoFuncional());
							detContenido.setPuntaje(dto.getPuntaje());
							detContenido.setTipo("CPT");
							detContenido.setActivo(true);
							detContenido.setCpt(cptHome.getInstance());
							detContenidoFuncionalHome.setInstance(detContenido);
							String resultado = detContenidoFuncionalHome.persist();
							if (resultado.equals("persisted")) {
								List<DetDescripcionContFuncional> listaDescripcion =
									new ArrayList<DetDescripcionContFuncional>();
								listaDescripcion = dto.getListaDetDescripContFuncional();
								for (DetDescripcionContFuncional desc : listaDescripcion) {
									if (desc.getDescripcion() != null
											&& !desc.getDescripcion().equals("")) {
											desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
										}
										
										desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
										desc.setActivo(true);
										em.persist(desc);
									
								}
								// em.flush();

							}
						}else {
							
							List<DetDescripcionContFuncional> listaDescripcion =new ArrayList<DetDescripcionContFuncional>();
								listaDescripcion = dto.getListaDetDescripContFuncional();
								for (DetDescripcionContFuncional desc : listaDescripcion) {
									if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
											
										statusMessages.clear();
										this.mensajeTab02 = "Existen Descripciones con puntajes nulos. Verifique";
										statusMessages.add(Severity.WARN, mensajeTab02);
										return null;
									}
									
								}
						}
					}

					detContenidoFuncionalHome.clearInstance();

				}
				for (DetContenidoFuncional detail : listaAuxTab2) {
					List<DetDescripcionContFuncional> listaAuxDesc =
						new ArrayList<DetDescripcionContFuncional>();
					listaAuxDesc.addAll(detail.getDetDescripcionContFuncionals());
					for (ContenidoFuncionalDTO detailDTO : listaDtoTab2) {

						List<DetDescripcionContFuncional> listaDTODesc =
							new ArrayList<DetDescripcionContFuncional>();
						listaDTODesc.addAll(detailDTO.getListaDetDescripContFuncional());
						if (detail.getContenidoFuncional().getIdContenidoFuncional().equals(detailDTO.getContenidoFuncional().getIdContenidoFuncional())) {
							for (DetDescripcionContFuncional descAux : listaAuxDesc) {
								Boolean esta = false;
								for (DetDescripcionContFuncional descDTO : listaDTODesc) {
									if (descDTO.getIdDetDescripcionContFuncional() != null
										&& descAux.getIdDetDescripcionContFuncional() != null) {
										if (descAux.getIdDetDescripcionContFuncional().equals(descDTO.getIdDetDescripcionContFuncional()))
											esta = true;
									}
								}
								if (!esta) {
									em.remove(descAux);
									// em.flush();Cpt actualizado con éxito
								}
							}
						}
					}
				}

				/**
				 * Tab 03
				 */

				for (RequisitosMinimosDTO dto : listaDtoTab3) {
					if (dto.getId() != null) {
						DetReqMin detReqMin = new DetReqMin();
						detReqMin = em.find(DetReqMin.class, dto.getId());
						detReqMin.setPuntajeReqMin(dto.getPuntaje());
						detReqMinHome.setInstance(detReqMin);
						String resultado = detReqMinHome.update();
						if (resultado.equals("updated")) {
							List<DetMinimosRequeridos> listaMinimos =
								new ArrayList<DetMinimosRequeridos>();
							listaMinimos = dto.getListaDetMinimosRequeridos();
							Iterator<DetMinimosRequeridos> iter = listaMinimos.iterator();
							while (iter.hasNext()) {
								DetMinimosRequeridos min = iter.next();
								if (min.getMinimosRequeridos() != null
									&& !min.getMinimosRequeridos().equals("")) {
									min.setMinimosRequeridos(min.getMinimosRequeridos().trim().toUpperCase());
									min.setDetReqMin(new DetReqMin());
									min.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());
									min.setActivo(true);
									em.persist(min);
									// em.flush();
								}
								
							}

							List<DetOpcionesConvenientes> listaOpc =
								new ArrayList<DetOpcionesConvenientes>();
							listaOpc = dto.getListaDetOpcionesConvenientes();
							for (DetOpcionesConvenientes op : listaOpc) {
								if (op.getOpcConvenientes() != null
									&& !op.getOpcConvenientes().equals("")) {
									op.setOpcConvenientes(op.getOpcConvenientes().trim().toUpperCase());
									op.setDetReqMin(detReqMinHome.getInstance());
									op.setActivo(true);
									em.persist(op);
								}
							}
							// em.flush();
						}
					} else {
						if (dto.getPuntaje() != null) {
							DetReqMin detMin = new DetReqMin();
							detMin.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
							detMin.setPuntajeReqMin(dto.getPuntaje());
							detMin.setTipo("CPT");
							detMin.setActivo(true);
							detMin.setCpt(new Cpt());
							detMin.getCpt().setIdCpt(cptHome.getInstance().getIdCpt());
							em.persist(detMin);
							detReqMinHome.setInstance(detMin);
							String resultado = detReqMinHome.update();
							if (resultado.equals("updated")) {
								List<DetMinimosRequeridos> listaDetMinimos =
									new ArrayList<DetMinimosRequeridos>();
								listaDetMinimos = dto.getListaDetMinimosRequeridos();
								Iterator<DetMinimosRequeridos> iter = listaDetMinimos.iterator();

								while (iter.hasNext()) {
									DetMinimosRequeridos dMin = iter.next();
									if (dMin.getMinimosRequeridos() != null
										&& !dMin.getMinimosRequeridos().equals("")) {
										dMin.setMinimosRequeridos(dMin.getMinimosRequeridos().trim().toUpperCase());
										dMin.setDetReqMin(new DetReqMin());
										dMin.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());
										dMin.setActivo(true);
										em.persist(dMin);

									}
								}
								// em.flush();
								List<DetOpcionesConvenientes> listaDetOpc =
									new ArrayList<DetOpcionesConvenientes>();
								listaDetOpc = dto.getListaDetOpcionesConvenientes();
								for (DetOpcionesConvenientes detOpc : listaDetOpc) {
									if (detOpc.getOpcConvenientes() != null
										&& !detOpc.getOpcConvenientes().equals("")) {
										detOpc.setOpcConvenientes(detOpc.getOpcConvenientes().trim().toUpperCase());
										detOpc.setDetReqMin(detReqMinHome.getInstance());
										detOpc.setActivo(true);
										em.persist(detOpc);
									}
								}
								// em.flush();
							}
						}else {
							List<DetMinimosRequeridos> listaDetMinimos =new ArrayList<DetMinimosRequeridos>();
							listaDetMinimos = dto.getListaDetMinimosRequeridos();
							for (DetMinimosRequeridos detMin : listaDetMinimos) {
								if (detMin.getMinimosRequeridos() != null
									&& !detMin.getMinimosRequeridos().equals("")) {
							
									statusMessages.clear();
									this.mensajeTab03 = "Existen Descripciones con puntajes nulos. Verifique";
									statusMessages.add(Severity.WARN,mensajeTab03 );
									return null;
							
								}
							}
							
							List<DetOpcionesConvenientes> listaDetOpc =	new ArrayList<DetOpcionesConvenientes>();
								listaDetOpc = dto.getListaDetOpcionesConvenientes();
								for (DetOpcionesConvenientes detOpc : listaDetOpc) {
									if (detOpc.getOpcConvenientes() != null
										&& !detOpc.getOpcConvenientes().equals("")) {
										statusMessages.clear();
										this.mensajeTab03 = "Existen Descripciones con puntajes nulos. Verifique";
										statusMessages.add(Severity.WARN,mensajeTab03 );
										return null;
									}
								}
						}
						detContenidoFuncionalHome.clearInstance();
					}
				}

				for (DetReqMin detail : listaAuxTab3) {
					List<DetOpcionesConvenientes> listaAuxOpc =
						new ArrayList<DetOpcionesConvenientes>();
					listaAuxOpc.addAll(detail.getDetOpcionesConvenienteses());
					List<DetMinimosRequeridos> listaAuxReqMin =
						new ArrayList<DetMinimosRequeridos>();
					listaAuxReqMin.addAll(detail.getDetMinimosRequeridoses());

					for (RequisitosMinimosDTO detailDTO : listaDtoTab3) {

						List<DetOpcionesConvenientes> listaDTOOpc =
							new ArrayList<DetOpcionesConvenientes>();
						listaDTOOpc.addAll(detailDTO.getListaDetOpcionesConvenientes());
						List<DetMinimosRequeridos> listaDTOReq =
							new ArrayList<DetMinimosRequeridos>();
						listaDTOReq.addAll(detailDTO.getListaDetMinimosRequeridos());
						if (detail.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt().equals(detailDTO.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt())) {
							for (DetOpcionesConvenientes opcAux : listaAuxOpc) {
								Boolean esta = false;
								for (DetOpcionesConvenientes opcDTO : listaDTOOpc) {
									if (opcDTO.getIdDetOpcionesConvenientes() != null
										&& opcAux.getIdDetOpcionesConvenientes() != null) {
										if (opcAux.getIdDetOpcionesConvenientes().equals(opcDTO.getIdDetOpcionesConvenientes()))
											esta = true;
									}
								}
								if (!esta) {
									em.remove(opcAux);
									// em.flush();
								}
							}

							for (DetMinimosRequeridos reqAux : listaAuxReqMin) {
								Boolean esta = false;
								for (DetMinimosRequeridos reqDTO : listaDTOReq) {
									if (reqDTO.getIdMinimosRequeridos() != null
										&& reqAux.getIdMinimosRequeridos() != null) {
										if (reqAux.getIdMinimosRequeridos().equals(reqDTO.getIdMinimosRequeridos()))
											esta = true;
									}
								}
								if (!esta) {
									em.remove(reqAux);
									// em.flush();
								}
							}
						}
					}
				}
				/**
				 * Tab 04
				 */

				// Actualiza los datos y elimina los que tengan puntaje nulo
				for (CondicionTrabajoDTO dto : listaCondicionDtoTab4) {
					if (dto.getPuntaje() != null) {
						if (dto.getId() == null) {
							DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
							detCondTrab.setActivo(dto.getActivo());
							detCondTrab.setCondicionTrabajo(dto.getCondicionTrabajo());
							detCondTrab.setCpt(cptHome.getInstance());
							detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
							detCondTrab.setTipo("CPT");
							em.persist(detCondTrab);
						} else {
							DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
							detCondTrab = em.find(DetCondicionTrabajo.class, dto.getId());
							detCondTrab.setActivo(dto.getActivo());
							detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
							em.merge(detCondTrab);
						}
						// em.flush();
					} else {
						if (dto.getId() != null) {
							DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
							detCondTrab = em.find(DetCondicionTrabajo.class, dto.getId());
							em.remove(detCondTrab);
						}
					}
					// em.flush();

				}

				/**
				 * Tab 05
				 */
				for (CondicionTrabajoEspecifDTO dto : listaDtoTab5) {
					if (dto.getPuntaje() != null && dto.getAjustes() != null
						&& dto.getJustificacion() != null) {
						if (dto.getId() == null) {
							DetCondicionTrabajoEspecif detCondTrab =
								new DetCondicionTrabajoEspecif();
							detCondTrab.setActivo(dto.getActivo());
							detCondTrab.setCondicionTrabajoEspecif(dto.getCondicionTrabajoEspecif());
							detCondTrab.setCpt(cptHome.getInstance());
							detCondTrab.setPuntajeCondTrabEspecif(dto.getPuntaje());
							detCondTrab.setTipo("CPT");
							detCondTrab.setAjustes(dto.getAjustes().trim().toUpperCase());
							detCondTrab.setJustificacion(dto.getJustificacion().trim().toUpperCase());
							em.persist(detCondTrab);
						} else {
							DetCondicionTrabajoEspecif detCondTrab =
								new DetCondicionTrabajoEspecif();
							detCondTrab = em.find(DetCondicionTrabajoEspecif.class, dto.getId());
							detCondTrab.setActivo(dto.getActivo());
							detCondTrab.setPuntajeCondTrabEspecif(dto.getPuntaje());
							detCondTrab.setAjustes(dto.getAjustes().trim().toUpperCase());
							detCondTrab.setJustificacion(dto.getJustificacion().trim().toUpperCase());
							em.merge(detCondTrab);
						}
						// em.flush();
					} else {
						if (dto.getId() != null) {
							DetCondicionTrabajoEspecif detCondTrab =
								new DetCondicionTrabajoEspecif();
							detCondTrab = em.find(DetCondicionTrabajoEspecif.class, dto.getId());
							em.remove(detCondTrab);
						}
					}
					// em.flush();

				}

				/**
				 * Tab 06
				 */

				for (CondicionSeguridadDTO dto : listaDtoTab6) {
					if (dto.getPuntaje() != null && dto.getAjustes() != null
						&& dto.getJustificacion() != null) {
						if (dto.getId() == null) {
							DetCondicionSegur detCondSeg = new DetCondicionSegur();
							detCondSeg.setActivo(dto.getActivo());
							detCondSeg.setCondicionSegur(dto.getCondicionSegur());
							detCondSeg.setCpt(cptHome.getInstance());
							detCondSeg.setPuntajeCondSegur(dto.getPuntaje());
							detCondSeg.setTipo("CPT");
							detCondSeg.setAjustes(dto.getAjustes().trim().toUpperCase());
							detCondSeg.setJustificacion(dto.getJustificacion().trim().toUpperCase());
							em.persist(detCondSeg);
						} else {
							DetCondicionSegur detCondSeg = new DetCondicionSegur();
							detCondSeg = em.find(DetCondicionSegur.class, dto.getId());
							detCondSeg.setActivo(dto.getActivo());
							detCondSeg.setPuntajeCondSegur(dto.getPuntaje());
							detCondSeg.setAjustes(dto.getAjustes().trim().toUpperCase());
							detCondSeg.setJustificacion(dto.getJustificacion().trim().toUpperCase());
							em.merge(detCondSeg);
						}
						// em.flush();
					} else {
						if (dto.getId() != null) {
							DetCondicionSegur detCondSeg = new DetCondicionSegur();
							detCondSeg = em.find(DetCondicionSegur.class, dto.getId());
							em.remove(detCondSeg);
						}
					}
					// em.flush();

				}

				/**
				 * Categorias posibles
				 */

				for (CategoriaDTO c : listaCategoriasDTO) {
					if (c.getId() == null) {
						String categ = c.getCategoria();
						if (categ != null && !categ.equals("")) {
							CategoriaCpt cat = new CategoriaCpt();

							cat.setCategoria(categ);
							cat.setCpt(cptHome.getInstance());
							cat.setFechaAlta(new Date());
							cat.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							em.persist(cat);
							// em.flush();
						}

					} else {
						String categ = c.getCategoria();
						if (categ != null) {
							CategoriaCpt cat = new CategoriaCpt();
							cat = em.find(CategoriaCpt.class, c.getId());
							cat.setCategoria(categ);

							cat.setFechaMod(new Date());
							cat.setUsuMod(usuarioLogueado.getCodigoUsuario());
							em.merge(cat);
							// em.flush();
						}

					}
				}

				for (CategoriaCpt catCpt : listaCategoriaAux) {
					Boolean esta = false;
					for (CategoriaDTO c : listaCategoriasDTO) {
						if (c.getId() != null && catCpt.getIdCategoriaCpt().equals(c.getId()))
							esta = true;
					}
					if (!esta) {
						em.remove(catCpt);
						// em.flush();
					}
				}

			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			em.flush();
			return getUrlToPageList();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	/**********************************************************
	 * Metodos que recuperan datos para editar
	 **********************************************************/
	/**
	 * Busca datos del TAB01
	 */
	@SuppressWarnings("unchecked")
	private void buscarNombramientos() {
		buscarTipoPlanta();
		String cad =
			"select * from planificacion.det_tipo_nombramiento det_tipo"
				+ " where det_tipo.id_cpt = " + cpt.getIdCpt();
		listaAuxTab1 = new ArrayList<DetTipoNombramiento>();
		listaAuxTab1 = em.createNativeQuery(cad, DetTipoNombramiento.class).getResultList();
		for (int i = 0; i < listaTipoPlanta.size(); i++) {
			TipoPlanta planta = new TipoPlanta();
			planta = listaTipoPlanta.get(i);
			List<TipoNombramiento> listaNombramientos = planta.getTipoNombramientos();
			for (DetTipoNombramiento detalle : listaAuxTab1) {
				for (TipoNombramiento nombramiento : listaNombramientos) {
					if (nombramiento.getIdTipoNombramiento().equals(detalle.getTipoNombramiento().getIdTipoNombramiento())) {
						nombramiento.setSeleccionado(true);
					}
				}
			}
			planta.setTipoNombramientos(listaNombramientos);
			listaTipoPlanta.set(i, planta);
		}
	}
	

	/**
	 * Busca datos del TAB02
	 */
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncional() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.activo = true and cont_funcional.id_cpt = " + cpt.getIdCpt();
		listaAuxTab2 = new ArrayList<DetContenidoFuncional>();
		listaAuxTab2 = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoTab2 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaAuxTab2) {
				if (det.getContenidoFuncional().getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					dto.setPuntajeString(dto.getPuntaje().toString());
					List<DetDescripcionContFuncional> listaDesc = 	new ArrayList<DetDescripcionContFuncional>();
					listaDesc = det.getDetDescripcionContFuncionals();
//					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
//					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoTab2.add(dto);
				}
			}
			if (!esta) {
				ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
				dto.setContenidoFuncional(contenido);
				List<DetDescripcionContFuncional> listaDesc =
					new ArrayList<DetDescripcionContFuncional>();

				DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
				listaDesc.add(descr);
				dto.setListaDetDescripContFuncional(listaDesc);
				listaDtoTab2.add(dto);
			}
		}

	}

	/**
	 * Busca datos del TAB03
	 */
	@SuppressWarnings("unchecked")
	private void buscarRequerimientosMinimos() {
		String cad =
			"select * from planificacion.det_req_min det_req" + " where det_req.id_cpt = "
				+ cpt.getIdCpt();
		listaAuxTab3 = new ArrayList<DetReqMin>();
		listaAuxTab3 = em.createNativeQuery(cad, DetReqMin.class).getResultList();

		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoTab3 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();

		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		listaDtoTab3 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaAuxTab3) {
				if (det.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt().equals(req.getIdRequisitoMinimoCpt())) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					dto.setPuntajeString(dto.getPuntaje().toString());
					List<DetOpcionesConvenientes> listaConv =
						new ArrayList<DetOpcionesConvenientes>();
					listaConv = det.getDetOpcionesConvenienteses();
					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);
					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
					listaReq = det.getDetMinimosRequeridoses();
					DetMinimosRequeridos r = new DetMinimosRequeridos();
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					listaDtoTab3.add(dto);
				}
			}
			if (!esta) {
				RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
				dto.setRequisitoMinimoCpt(req);
				List<DetOpcionesConvenientes> listaConv = new ArrayList<DetOpcionesConvenientes>();

				DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
				listaConv.add(conv);
				dto.setListaDetOpcionesConvenientes(listaConv);
				List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();

				DetMinimosRequeridos r = new DetMinimosRequeridos();
				listaReq.add(r);
				dto.setListaDetMinimosRequeridos(listaReq);
				listaDtoTab3.add(dto);
			}
		}

	}

	/**
	 * Busca datos del TAB04
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajo() {
		String cad =
			"select * from planificacion.det_condicion_trabajo det_trab"
				+ " where det_trab.id_cpt = " + cpt.getIdCpt();
		listaAuxTab4 = new ArrayList<DetCondicionTrabajo>();
		listaAuxTab4 = em.createNativeQuery(cad, DetCondicionTrabajo.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_trabajo cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajo> lista = new ArrayList<CondicionTrabajo>();

		lista = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
		listaCondicionDtoTab4 = new ArrayList<CondicionTrabajoDTO>();
		for (CondicionTrabajo condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajo det : listaAuxTab4) {
				if (det.getCondicionTrabajo().getIdCondicionTrabajo().equals(condicion.getIdCondicionTrabajo())) {
					esta = true;
					CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
					dto.setCondicionTrabajo(det.getCondicionTrabajo());
					dto.setId(det.getIdDetCondiconTrabajo());
					dto.setActivo(det.getActivo());
					dto.setPuntaje(det.getPuntajeCondTrab());
					dto.setPuntajeString(dto.getPuntaje().toString());
					listaCondicionDtoTab4.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
				dto.setCondicionTrabajo(condicion);
				listaCondicionDtoTab4.add(dto);
			}
		}

	}

	/**
	 * Busca datos del TAB05
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEspecifica() {
		String cad =
			"select * from planificacion.det_condicion_trabajo_especif det_trab"
				+ " where det_trab.id_cpt = " + cpt.getIdCpt();
		List<DetCondicionTrabajoEspecif> listaDet = new ArrayList<DetCondicionTrabajoEspecif>();
		listaDet = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajoEspecif> lista = new ArrayList<CondicionTrabajoEspecif>();

		lista = em.createNativeQuery(cadena, CondicionTrabajoEspecif.class).getResultList();
		listaDtoTab5 = new ArrayList<CondicionTrabajoEspecifDTO>();
		for (CondicionTrabajoEspecif condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajoEspecif det : listaDet) {
				if (det.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif().equals(condicion.getIdCondicionesTrabajoEspecif())) {
					esta = true;
					CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
					dto.setCondicionTrabajoEspecif(det.getCondicionTrabajoEspecif());
					dto.setId(det.getIdDetCondicionTrabajoEspecif());
					dto.setActivo(det.getActivo());
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondTrabEspecif());
					dto.setPuntajeString(dto.getPuntaje().toString());
					listaDtoTab5.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
				dto.setCondicionTrabajoEspecif(condicion);
				listaDtoTab5.add(dto);
			}
		}

	}

	/**
	 * Busca datos del TAB06
	 */
	@SuppressWarnings("unchecked")
	private void buscarDetCondicionSegur() {
		String cad =
			"select * from planificacion.det_condicion_segur det_cond"
				+ " where det_cond.id_cpt = " + cpt.getIdCpt();
		listaAuxTab6 = new ArrayList<DetCondicionSegur>();
		listaAuxTab6 = em.createNativeQuery(cad, DetCondicionSegur.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> lista = new ArrayList<CondicionSegur>();

		lista = em.createNativeQuery(cadena, CondicionSegur.class).getResultList();
		listaDtoTab6 = new ArrayList<CondicionSeguridadDTO>();
		for (CondicionSegur seg : lista) {
			Boolean esta = false;
			for (DetCondicionSegur det : listaAuxTab6) {
				if (det.getCondicionSegur().getIdCondicionSegur().equals(seg.getIdCondicionSegur())) {
					esta = true;
					CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
					dto.setCondicionSegur(det.getCondicionSegur());
					dto.setId(det.getIdDetCondicionSegur());
					dto.setActivo(det.getActivo());
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondSegur());
					dto.setPuntajeString(dto.getPuntaje().toString());
					listaDtoTab6.add(dto);
				}
			}
			if (!esta) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setCondicionSegur(seg);
				listaDtoTab6.add(dto);
			}
		}

	}

	private void cargarListaCategorias(Long id) {
		SinAnx anx = new SinAnx();
		anx = em.find(SinAnx.class, id);
		CategoriaDTO dto = new CategoriaDTO();
		dto.setCategoria(anx.getCtgCodigo());
		dto.setDenominacion(anx.getAnxDescrip());
		Integer tam = listaCategoriasDTO.size();
		listaCategoriasDTO.set(tam - 1, dto);
		setIdAnx(null);

	}
    public void agregarCptNivelesCargos(){
    	System.out.print("AGREGAR NIVELES DE CARGOS: ");
    	
    	if(idNivelesDeCargos !=null){
    		System.out.print("AGREGAR NIVELES DE CARGOS DENTRO DEL IF 1: ");
    		NivelesDeCargos nivcarfind = em.find(NivelesDeCargos.class, idNivelesDeCargos);
	    	if(!listaNivelesDeCargos.contains(nivcarfind)){
	    	  	
	    		listaNivelesDeCargos.add(nivcarfind);
		    	CptNivelesCargos nuevo = new CptNivelesCargos();
		    	nuevo.setIdCpt(cpt.getIdCpt());
		       	nuevo.setIdNivelesCargos(new Long(idNivelesDeCargos));
		       	
		    	listaCptNivelesCargos.add(nuevo);
		    	System.out.print("AGREGAR NIVELES DE CARGOS: " + nuevo.getIdNivelesCargos());
	    	}
    	}
    }
    public void eliminarCptNivelesCargos(int row){
    	
    	listaNivelesDeCargos.remove(row);
    	listaCptNivelesCargos.remove(row);
    	
    }
	public void agregarListaCategorias() {
		CategoriaDTO dto = new CategoriaDTO();

		listaCategoriasDTO.add(dto);
	}

	public void eliminarListaCategoria(Integer row) {
		CategoriaDTO dto = new CategoriaDTO();
		dto = listaCategoriasDTO.get(row);
		listaCategoriasDTO.remove(dto);
	}

	public void valNivel() {
		if (cpt.getNivel() != null && cpt.getNivel().intValue() < 4)
			habJefatura = false;
		else
			habJefatura = true;
	}
	
	
	public String volverCptGestionarHomologacion(){
		if (cpt != null)
			this.listaCptObs = this.obtenerObservacionesPorCpt(cpt);
		
		return "/planificacion/cpt/CptGestionarHomologacion.seam?cptIdCpt="+this.idCpt+"&tipo=especifico";
	}

	/**********************************************************
	 * Metodos Getters y Setters
	 **********************************************************/
	public Cpt getCpt() {
		return cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	public Long getIdGradoSalarialMin() {
		return idGradoSalarialMin;
	}

	public void setIdGradoSalarialMin(Long idGradoSalarialMin) {
		this.idGradoSalarialMin = idGradoSalarialMin;
	}

	public Long getIdGradoSalarialMax() {
		return idGradoSalarialMax;
	}

	public void setIdGradoSalarialMax(Long idGradoSalarialMax) {
		this.idGradoSalarialMax = idGradoSalarialMax;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Boolean getConJefatura() {
		return conJefatura;
	}

	public void setConJefatura(Boolean conJefatura) {
		this.conJefatura = conJefatura;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public List<TipoPlanta> getListaTipoPlanta() {
		return listaTipoPlanta;
	}

	public void setListaTipoPlanta(List<TipoPlanta> listaTipoPlanta) {
		this.listaTipoPlanta = listaTipoPlanta;
	}

	public List<ContenidoFuncionalDTO> getListaDtoTab2() {
		return listaDtoTab2;
	}

	public void setListaDtoTab2(List<ContenidoFuncionalDTO> listaDtoTab2) {
		this.listaDtoTab2 = listaDtoTab2;
	}

	public List<ContenidoFuncional> getListaValoracionTab2() {
		return listaValoracionTab2;
	}

	public void setListaValoracionTab2(List<ContenidoFuncional> listaValoracionTab2) {
		this.listaValoracionTab2 = listaValoracionTab2;
	}

	public String getMensajeTab02() {
		return mensajeTab02;
	}

	public void setMensajeTab02(String mensajeTab02) {
		this.mensajeTab02 = mensajeTab02;
	}

	public List<RequisitosMinimosDTO> getListaDtoTab3() {
		return listaDtoTab3;
	}

	public void setListaDtoTab3(List<RequisitosMinimosDTO> listaDtoTab3) {
		this.listaDtoTab3 = listaDtoTab3;
	}

	public String getMensajeTab03() {
		return mensajeTab03;
	}

	public void setMensajeTab03(String mensajeTab03) {
		this.mensajeTab03 = mensajeTab03;
	}

	public List<RequisitoMinimoCpt> getListaValoracionTab3() {
		return listaValoracionTab3;
	}

	public void setListaValoracionTab3(List<RequisitoMinimoCpt> listaValoracionTab3) {
		this.listaValoracionTab3 = listaValoracionTab3;
	}

	public String getMensajeTab04() {
		return mensajeTab04;
	}

	public void setMensajeTab04(String mensajeTab04) {
		this.mensajeTab04 = mensajeTab04;
	}

	public List<CondicionTrabajoDTO> getListaCondicionDtoTab4() {
		return listaCondicionDtoTab4;
	}

	public void setListaCondicionDtoTab4(List<CondicionTrabajoDTO> listaCondicionDtoTab4) {
		this.listaCondicionDtoTab4 = listaCondicionDtoTab4;
	}

	public List<CondicionTrabajo> getListaValoracionTab4() {
		return listaValoracionTab4;
	}

	public void setListaValoracionTab4(List<CondicionTrabajo> listaValoracionTab4) {
		this.listaValoracionTab4 = listaValoracionTab4;
	}

	public List<CondicionTrabajoEspecifDTO> getListaDtoTab5() {
		return listaDtoTab5;
	}

	public void setListaDtoTab5(List<CondicionTrabajoEspecifDTO> listaDtoTab5) {
		this.listaDtoTab5 = listaDtoTab5;
	}

	public List<ValoracionTab05CPT> getListaValoracionTab5() {
		return listaValoracionTab5;
	}

	public void setListaValoracionTab5(List<ValoracionTab05CPT> listaValoracionTab5) {
		this.listaValoracionTab5 = listaValoracionTab5;
	}

	public List<ValoracionTab06CPT> getListaValoracionTab6() {
		return listaValoracionTab6;
	}

	public void setListaValoracionTab6(List<ValoracionTab06CPT> listaValoracionTab6) {
		this.listaValoracionTab6 = listaValoracionTab6;
	}

	public String getMensajeTab05() {
		return mensajeTab05;
	}

	public void setMensajeTab05(String mensajeTab05) {
		this.mensajeTab05 = mensajeTab05;
	}

	public String getMensajeTab06() {
		return mensajeTab06;
	}

	public void setMensajeTab06(String mensajeTab06) {
		this.mensajeTab06 = mensajeTab06;
	}

	public List<CondicionSeguridadDTO> getListaDtoTab6() {
		return listaDtoTab6;
	}

	public void setListaDtoTab6(List<CondicionSeguridadDTO> listaDtoTab6) {
		this.listaDtoTab6 = listaDtoTab6;
	}

	public List<CondicionTrabajoEspecif> getListaCondTrabEspecificas() {
		return listaCondTrabEspecificas;
	}

	public void setListaCondTrabEspecificas(List<CondicionTrabajoEspecif> listaCondTrabEspecificas) {
		this.listaCondTrabEspecificas = listaCondTrabEspecificas;
	}

	public List<CondicionSegur> getListaCondSegur() {
		return listaCondSegur;
	}

	public void setListaCondSegur(List<CondicionSegur> listaCondSegur) {
		this.listaCondSegur = listaCondSegur;
	}

	public List<DetTipoNombramiento> getListaAuxTab1() {
		return listaAuxTab1;
	}

	public void setListaAuxTab1(List<DetTipoNombramiento> listaAuxTab1) {
		this.listaAuxTab1 = listaAuxTab1;
	}

	public List<DetContenidoFuncional> getListaAuxTab2() {
		return listaAuxTab2;
	}

	public void setListaAuxTab2(List<DetContenidoFuncional> listaAuxTab2) {
		this.listaAuxTab2 = listaAuxTab2;
	}

	public List<DetReqMin> getListaAuxTab3() {
		return listaAuxTab3;
	}

	public void setListaAuxTab3(List<DetReqMin> listaAuxTab3) {
		this.listaAuxTab3 = listaAuxTab3;
	}

	public List<DetCondicionTrabajo> getListaAuxTab4() {
		return listaAuxTab4;
	}

	public void setListaAuxTab4(List<DetCondicionTrabajo> listaAuxTab4) {
		this.listaAuxTab4 = listaAuxTab4;
	}

	public List<DetCondicionTrabajoEspecif> getListaAuxTab5() {
		return listaAuxTab5;
	}

	public void setListaAuxTab5(List<DetCondicionTrabajoEspecif> listaAuxTab5) {
		this.listaAuxTab5 = listaAuxTab5;
	}

	public List<DetCondicionSegur> getListaAuxTab6() {
		return listaAuxTab6;
	}

	public void setListaAuxTab6(List<DetCondicionSegur> listaAuxTab6) {
		this.listaAuxTab6 = listaAuxTab6;
	}

	public List<CategoriaDTO> getListaCategoriasDTO() {
		return listaCategoriasDTO;
	}

	public void setListaCategoriasDTO(List<CategoriaDTO> listaCategoriasDTO) {
		this.listaCategoriasDTO = listaCategoriasDTO;
	}

	public List<CategoriaCpt> getListaCategoriaAux() {
		return listaCategoriaAux;
	}

	public void setListaCategoriaAux(List<CategoriaCpt> listaCategoriaAux) {
		this.listaCategoriaAux = listaCategoriaAux;
	}

	public Long getIdAnx() {
		return idAnx;
	}

	public void setIdAnx(Long idAnx) {
		this.idAnx = idAnx;
		if (idAnx != null) {

			cargarListaCategorias(idAnx);
		}

	}

	public String getMensajeCategorias() {
		return mensajeCategorias;
	}

	public void setMensajeCategorias(String mensajeCategorias) {
		this.mensajeCategorias = mensajeCategorias;
	}

	public Boolean getMostrarCategoria() {
		return mostrarCategoria;
	}

	public void setMostrarCategoria(Boolean mostrarCategoria) {
		this.mostrarCategoria = mostrarCategoria;
	}

	public void mostrarCategoriasPosibles() {
		mostrarCategoria = (mostrarCategoria)? false : true;
		if(mostrarCategoria) buscarListaTabla();
	}

	public Boolean getGuardarMas() {
		return guardarMas;
	}

	public void setGuardarMas(Boolean guardarMas) {
		this.guardarMas = guardarMas;
	}

	public Long getIdTipoCce() {
		return idTipoCce;
	}

	public void setIdTipoCce(Long idTipoCce) {
		this.idTipoCce = idTipoCce;
	}

	public List<SelectItem> getGradoSalarialSelecItem() {
		return gradoSalarialSelecItem;
	}

	public void setGradoSalarialSelecItem(List<SelectItem> gradoSalarialSelecItem) {
		this.gradoSalarialSelecItem = gradoSalarialSelecItem;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isHabJefatura() {
		return habJefatura;
	}

	public void setHabJefatura(boolean habJefatura) {
		this.habJefatura = habJefatura;
	}

	public String getCodigoCpt() {
		return codigoCpt;
	}

	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}

	public Cpt getCptPadre() {
		return cptPadre;
	}

	public void setCptPadre(Cpt cptPadre) {
		this.cptPadre = cptPadre;
	}

	public Long getIdCptPadre() {
		return idCptPadre;
	}

	public void setIdCptPadre(Long idCptPadre) {
		this.idCptPadre = idCptPadre;
	}
	
	public String getUrlToPageCpt() {
		
		return "/planificacion/searchForms/CptList.xhtml?from=planificacion/cpt/CptEdit&tipo=general";

	}

	public Long getIdTipoCcePopUp() {
		return idTipoCcePopUp;
	}

	public void setIdTipoCcePopUp(Long idTipoCcePopUp) {
		this.idTipoCcePopUp = idTipoCcePopUp;
	}

	public String getCodigoCptPopUp() {
		return codigoCptPopUp;
	}

	public void setCodigoCptPopUp(String codigoCptPopUp) {
		this.codigoCptPopUp = codigoCptPopUp;
	}

	public Long getIdTipoCptPopUp() {
		return idTipoCptPopUp;
	}

	public void setIdTipoCptPopUp(Long idTipoCptPopUp) {
		this.idTipoCptPopUp = idTipoCptPopUp;
	}

	public Long getIdGradoSalarialMaxPopUp() {
		return idGradoSalarialMaxPopUp;
	}
	

	public void setIdGradoSalarialMaxPopUp(Long idGradoSalarialMaxPopUp) {
		this.idGradoSalarialMaxPopUp = idGradoSalarialMaxPopUp;
	}

	public Long getIdGradoSalarialMinPopUp() {
		return idGradoSalarialMinPopUp;
	}

	public void setIdGradoSalarialMinPopUp(Long idGradoSalarialMinPopUp) {
		this.idGradoSalarialMinPopUp = idGradoSalarialMinPopUp;
	}
	public void setCptPopUp(Cpt cptPopUp) {
		this.cptPopUp = cptPopUp;
	}
	public Cpt getCptPopUp() {
		return cptPopUp;
	}

	public void setIdCptPopUp(Long idCptPopUp) {
		this.idCptPopUp = idCptPopUp;
	}
	
	public Long getIdCptPopUp() {
		return idCptPopUp;
	}

	public Long getIdNivelesDeCargos() {
		return idNivelesDeCargos;
	}

	public void setIdNivelesDeCargos(Long idNivelesDeCargos) {
		this.idNivelesDeCargos = idNivelesDeCargos;
	}
	public List<CptNivelesCargos> getListaCptNivelesCargos() {
		return listaCptNivelesCargos;
	}
	public void setListaCptNivelesCargos(List<CptNivelesCargos> listaCptNivelesCargos) {
		this.listaCptNivelesCargos = listaCptNivelesCargos;
	}
	public List<String> getListaTabla(){
		return listaTabla;
	}
	public void setListaTabla(List<String> listaTabla){
		this.listaTabla=listaTabla;
	}
	public Boolean getModoEditar() {
		return modoEditar;
	}
	public void setModoEditar(Boolean modoEditar) {
		this.modoEditar = modoEditar;
	}
	public List<NivelesDeCargos> getListaNivelesDeCargos() {
		return listaNivelesDeCargos;
	}
	public void setListaNivelesDeCargos(List<NivelesDeCargos> listaNivelesDeCargos) {
		this.listaNivelesDeCargos = listaNivelesDeCargos;
	}
	public List<DetContenidoFuncional> getListaDetalleTab2() {
		return listaDetalleTab2;
	}
	public void setListaDetalleTab2(List<DetContenidoFuncional> listaDetalleTab2) {
		this.listaDetalleTab2 = listaDetalleTab2;
	}
	public List<ContenidoFuncionalDTO> getListaDetalleDtoTab2() {
		return listaDetalleDtoTab2;
	}

	public void setListaDetalleDtoTab2(List<ContenidoFuncionalDTO> listaDetalleDtoTab2) {
		this.listaDetalleDtoTab2 = listaDetalleDtoTab2;
	}
	public List<DetTipoNombramiento> getListaDetalleTab1() {
		return listaDetalleTab1;
	}
	public void setListaDetalleTab1(List<DetTipoNombramiento> listaDetalleTab1) {
		this.listaDetalleTab1 = listaDetalleTab1;
	}
	public List<TipoPlanta> getListaDetalleTipoPlanta() {
		return listaDetalleTipoPlanta;
	}
	public void setListaDetalleTipoPlanta(List<TipoPlanta> listaDetalleTipoPlanta) {
		this.listaDetalleTipoPlanta = listaDetalleTipoPlanta;
	}
	public List<CptObs> getListaCptObs() {
		return listaCptObs;
	}

	public void setListaCptObs(List<CptObs> listaCptObs) {
		this.listaCptObs = listaCptObs;
	}
	
	public CptObs getCptObs() {
		return cptObs;
	}
	public void setCptObs(CptObs cptObs) {
		this.cptObs = cptObs;
	}
	public Long getIdCpt() {
		return idCpt;
	}
	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}
	
	public boolean isHabilitarEditarObservacionSfp() {
		return habilitarEditarObservacionSfp;
	}


	public void setHabilitarEditarObservacionSfp(
			boolean habilitarEditarObservacionSfp) {
		this.habilitarEditarObservacionSfp = habilitarEditarObservacionSfp;
	}


	public boolean isHabilitarRespuestaOee() {
		return habilitarRespuestaOee;
	}


	public void setHabilitarRespuestaOee(boolean habilitarRespuestaOee) {
		this.habilitarRespuestaOee = habilitarRespuestaOee;
	}


	public boolean isHabilitarEditarRespuestaOee() {
		return habilitarEditarRespuestaOee;
	}


	public void setHabilitarEditarRespuestaOee(boolean habilitarEditarRespuestaOee) {
		this.habilitarEditarRespuestaOee = habilitarEditarRespuestaOee;
	}
	
	public boolean isHabilitarObservacionSfp() {
		return habilitarObservacionSfp;
	}


	public void setHabilitarObservacionSfp(boolean habilitarObservacionSfp) {
		this.habilitarObservacionSfp = habilitarObservacionSfp;
	}
	public Long getIdCptObs() {
		return idCptObs;
	}


	public void setIdCptObs(long idCptObs) {
		this.idCptObs = idCptObs;
	}
	
	public boolean isHabilitarHomologar() {
		return habilitarHomologar;
	}


	public void setHabilitarHomologar(boolean habilitarHomologar) {
		this.habilitarHomologar = habilitarHomologar;
	}


	public boolean isDeshabilitarEditarCpt() {
		return deshabilitarEditarCpt;
	}


	public void setDeshabilitarEditarCpt(boolean deshabilitarEditarCpt) {
		this.deshabilitarEditarCpt = deshabilitarEditarCpt;
	}


}
