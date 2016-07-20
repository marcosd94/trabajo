package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.dto.CondicionSeguridadDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoEspecifDTO;
import py.com.excelsis.sicca.dto.ContenidoFuncionalDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.dto.ValoracionTab05CPT;
import py.com.excelsis.sicca.dto.ValoracionTab06CPT;
import py.com.excelsis.sicca.entity.CategoriaCpt;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
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
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.FusionPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.NivelesDeCargos;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PromocionSalarial;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoria;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.entity.VwEntidadOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.CptList;
import py.com.excelsis.sicca.session.DetContenidoFuncionalHome;
import py.com.excelsis.sicca.session.DetReqMinHome;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.PlantaCargoDetHome;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.SinObjList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("administrarPuestosTrabajoFormController")
public class AdministrarPuestosTrabajoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7584583092698135149L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	CptList cptList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(create = true)
	SinObjList sinObjList;
	@In(create = true)
	DetContenidoFuncionalHome detContenidoFuncionalHome;
	@In(create = true)
	DetReqMinHome detReqMinHome;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SinarhUtiles sinarhUtiles;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Long idGrupo;
	private Long idCptFromList;
	private PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
	private PromocionSalarial promocionSalarial = new PromocionSalarial();
	private Long idPromocionSalarial;
	private EstadoCab estadoCab = new EstadoCab();
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = null;
	private SinAnx sinAnx = new SinAnx();
	private String mostrarPanel;
	private String codigoUnidOrgDep;
	private String codigoCpt;
	private String modalidadOcupLink1;
	private String codigoPuestoLink1;
	private String codigoCategoria;
	private String valorObj;
	private String categoria;
	private String horaDesdeLunes;
	private String horaHastaLunes;
	private String horaDesdeMartes;
	private String horaHastaMartes;
	private String horaDesdeMiercoles;
	private String horaHastaMiercoles;
	private String horaDesdeJueves;
	private String horaHastaJueves;
	private String horaDesdeViernes;
	private String horaHastaViernes;
	private String horaDesdeSabado;
	private String horaHastaSabado;
	private String horaDesdeDomingo;
	private String horaHastaDomingo;
	private String mensajeLink06;
	private String mensajeLink07;
	private String mensajeLink08;
	private String mensajeLink09;
	private String mensajeLink10;
	private String mensajeLink03;
	/*Se volviï¿½ a hablitar el String "radioButton", este al estar en comentario ocasionaba problemas desde el formulario
	/sicca/seleccion/admCargaGrupo/ConcursoPuestoDetList.xhtml; Werner*/
	private String radioButton;
	private String motivoRadioButton;
	private String ordenOEEDep;
	private String descripcionCpt;
	private String ubicacionFisica;
	private String plantaCargoDetFrom;
	private String plantaFrom;
	private String from;
	private Integer ordenUnidOrg;
	private Integer codigoObj;
	private Integer ordenPuesto;
	private Boolean jefatura;
	private Integer cantPuestosReplicados;
	private Long idConfigCab;
	private Long idTipoCpt;
	private Long idCpt;
	private Long idCptPadre;
	private Long idEspecialidad;
	private Long idOficina;
	private Long idSinAnx;
	private Long idBusqUnOrgDep;
	private BigDecimal monto;
	private Boolean tieneUsuario;
	private Boolean isNewLink1;
	private Boolean isNewLink3;
	private Boolean isNewLink5;
	private Boolean isNewLink6;
	private Boolean isNewLink7;
	private Boolean isNewLink8;
	private Boolean isNewLink9;
	private Boolean isNewLink10;
	private Boolean isNewLink11;
	private Boolean isEdit;
	private Boolean mostrarTodosLinks;
	
	private List<String> listaTabla = new ArrayList<String>();
	private List<String> listaTablaTipo = new ArrayList<String>();
	private List<NivelesDeCargos> listaNivelesDeCargos = new ArrayList<NivelesDeCargos>();
	private List<TipoNombramiento> listaTipoNombramiento = new ArrayList<TipoNombramiento>();
	private List<SelectItem> cptSelecItem = new ArrayList<SelectItem>();
	private List<PuestoConceptoPago> listaPuestoConceptoPago = new ArrayList<PuestoConceptoPago>();
	private List<SelectItem> oficinaSelecItem;
	private List<TipoPlanta> listaTipoPlanta = new ArrayList<TipoPlanta>();
	private List<ContenidoFuncional> listaValoracionTab2 = new ArrayList<ContenidoFuncional>();
	private List<ContenidoFuncionalDTO> listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
	private List<RequisitoMinimoCpt> listaValoracionTab3 = new ArrayList<RequisitoMinimoCpt>();
	private List<RequisitosMinimosDTO> listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
	private List<CondicionTrabajo> listaValoracionLink8 = new ArrayList<CondicionTrabajo>();
	private List<CondicionTrabajoDTO> listaCondicionDtoLink8 = new ArrayList<CondicionTrabajoDTO>();
	private List<ValoracionTab05CPT> listaValoracionLink9 = new ArrayList<ValoracionTab05CPT>();
	private List<CondicionTrabajoEspecifDTO> listaDtoLink9 =
		new ArrayList<CondicionTrabajoEspecifDTO>();
	private List<ValoracionTab06CPT> listaValoracionLink10 = new ArrayList<ValoracionTab06CPT>();
	private List<CondicionSeguridadDTO> listaDtoLink10 = new ArrayList<CondicionSeguridadDTO>();
	private List<PuestoConceptoPago> listaLink3 = new ArrayList<PuestoConceptoPago>();
	// Listas auxiliares
	private List<PuestoConceptoPago> listaLink3Aux = new ArrayList<PuestoConceptoPago>();
	private List<DetTipoNombramiento> listaLink5Aux = new ArrayList<DetTipoNombramiento>();
	private List<DetContenidoFuncional> listaLink6Aux = new ArrayList<DetContenidoFuncional>();
	private List<DetReqMin> listaLink7Aux = new ArrayList<DetReqMin>();
	private List<DetCondicionTrabajo> listaLink8Aux = new ArrayList<DetCondicionTrabajo>();
	private List<DetCondicionTrabajoEspecif> listaLink9Aux =
		new ArrayList<DetCondicionTrabajoEspecif>();
	private List<DetCondicionSegur> listaLink10Aux = new ArrayList<DetCondicionSegur>();

	// Fusion de Puestos
	private List<PlantaCargoDet> puestosSinFusionar = new ArrayList<PlantaCargoDet>();
	private List<PlantaCargoDet> puestosFusionados = new ArrayList<PlantaCargoDet>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private String elFrom;
	private boolean esHomologador;

	/**
	 * Mï¿½todo que valida el oee actual con el de usuario logueado
	 * 
	 * @param idOee
	 */
	private void validarOee(Long idOee) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(idOee)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/**
	 * Mï¿½todo que inicializa los datos
	 */
	public void init() {
		mostrarTodosLinks = true;
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		
		esHomologador = seguridadUtilFormController.esRolHomologar(this.usuarioLogueado.getIdUsuario());
		if(elFrom != null && elFrom.equals("admDocAdjunto") && (this.plantaCargoDetFrom.equals("editar") ||this.plantaCargoDetFrom.equals("nuevo") ))
			elFrom = "planificacion/puestosTrabajo/PlantaCargoDetList";
		
		from=plantaFrom == null? elFrom : plantaFrom;
		if(from == null) from ="planificacion/puestosTrabajo/PlantaCargoDetList";
		
		if(plantaCargoDetHome.isIdDefined() && (isEdit != null && isEdit))
			plantaCargoDetHome.setInstance(em.find(PlantaCargoDet.class, plantaCargoDetHome.getInstance().getIdPlantaCargoDet()));
			//em.refresh(plantaCargoDetHome.getInstance());
		
		if (plantaCargoDetHome.isIdDefined() && (isEdit == null)) {
			isEdit = true;
			plantaCargoDet = plantaCargoDetHome.getInstance();
			validarOee(plantaCargoDet.getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
			/**
			 * Datos del Link1 Datos Puesto
			 */
			tieneUsuario = true;
			configuracionUoDet = plantaCargoDet.getConfiguracionUoDet();

			configuracionUoCab = configuracionUoDet.getConfiguracionUoCab();
			sinEntidad = obtenerEntidad();
			nivelEntidad = obtenerNivel();

			ordenUnidOrg = configuracionUoCab.getOrden();
			ordenPuesto = plantaCargoDet.getOrden();
			jefatura = plantaCargoDet.getJefatura();
			idTipoCpt = plantaCargoDet.getCpt().getTipoCpt().getIdTipoCpt();
			updateCpt();
			idCpt = plantaCargoDet.getCpt().getIdCpt();
			
			if(plantaCargoDet.getCpt().getCptPadre() != null){
				idCptPadre = plantaCargoDet.getCpt().getCptPadre().getIdCpt();
			}else{
				idCptPadre=null;
			}
			
			Cpt cptActual = new Cpt();
			cptActual = em.find(Cpt.class, idCpt);
			codigoCpt =
				cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
					+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero()
					+ "." + cptActual.getNroEspecifico();
			descripcionCpt=cptActual.getDenominacion();
			if (plantaCargoDet.getComisionado())//AGREGADO RV
				modalidadOcupLink1 = "3";			
			if (plantaCargoDet.getContratado())
				modalidadOcupLink1 = "2";
			if (plantaCargoDet.getPermanente())
				modalidadOcupLink1 = "1";
			obtenerCodigo();
			if (plantaCargoDet.getEspecialidades() != null)
				idEspecialidad = plantaCargoDet.getEspecialidades().getIdEspecialidades();

			mostrarPanel = "panel1";
			isNewLink1 = false;
			isNewLink3 = false;
			isNewLink5 = false;
			isNewLink6 = false;
			isNewLink7 = false;
			isNewLink8 = false;
			isNewLink9 = false;
			isNewLink10 = false;
			setIsNewLink11(false);
			estadoCab = plantaCargoDet.getEstadoCab();
			
			recuperarCodigoOeeDep(obtenerCodigo(configuracionUoDet));
			buscarDatosLink3();
			buscarDatosLink4();
			buscarNombramientosEdit();
			buscarContenidoFuncionalEdit();
			buscarRequerimientosMinimosEdit();
			buscarCondicionTrabajoEdit();
			buscarCondicionTrabajoEspecificaEdit();
			buscarDetCondicionSegurEdit();
			obtenerUbicacionFisica();
			buscarListaTabla();
			
			
			

		} else if (!plantaCargoDetHome.isIdDefined()) {
			
			plantaCargoDet = new PlantaCargoDet();
			plantaCargoDet = plantaCargoDetHome.getInstance();
			estadoCab = em.find(EstadoCab.class, new Long("1"));
			plantaCargoDet.setEstadoCab(estadoCab);
			plantaCargoDet.setActivo(true);
			if (motivoRadioButton == null || !motivoRadioButton.equals("f"))
				mostrarPanel = "panel1";
			tieneUsuario = false;
			isNewLink1 = true;
			isNewLink3 = true;
			isNewLink5 = true;
			isNewLink6 = true;
			isNewLink7 = true;
			isNewLink8 = true;
			isNewLink9 = true;
			isNewLink10 = true;
			isNewLink11= true;
			isEdit = false;

			buscarDatosAsociadosUsuario();

			if (motivoRadioButton == null)
				motivoRadioButton = "n";
			if (cptSelecItem == null || cptSelecItem.size() == 1) {
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			}
		}
	}
	
	
	
	public void initPromocionSalarial() {
		mostrarTodosLinks = true;
		
		if(elFrom != null && elFrom.equals("admDocAdjunto") && (this.plantaCargoDetFrom.equals("editar") ||this.plantaCargoDetFrom.equals("nuevo") ))
			elFrom = "planificacion/promocionSalarial/PromocionSalarialList";
		
		from=plantaFrom == null? elFrom : plantaFrom;
		if(from == null) from ="planificacion/promocionSalarial/PromocionSalarialList";
		
//		if(plantaCargoDetHome.isIdDefined() && (isEdit != null && isEdit))
//			plantaCargoDetHome.setInstance(em.find(PlantaCargoDet.class, plantaCargoDetHome.getInstance().getIdPlantaCargoDet()));
			//em.refresh(plantaCargoDetHome.getInstance());
		
		if ( idPromocionSalarial != null && isEdit == null) {
			isEdit = true;
			//plantaCargoDet = plantaCargoDetHome.getInstance();
			this.promocionSalarial = em.find(PromocionSalarial.class,this.idPromocionSalarial );
			validarOee(promocionSalarial.getConfiguracionUoCab().getIdConfiguracionUo());
			/**
			 * Datos del Link1 Datos Puesto
			 */
			tieneUsuario = true;
//			configuracionUoDet = plantaCargoDet.getConfiguracionUoDet();

			configuracionUoCab = promocionSalarial.getConfiguracionUoCab();
			sinEntidad = obtenerEntidad();
			nivelEntidad = obtenerNivel();

			ordenUnidOrg = configuracionUoCab.getOrden();
			ordenPuesto = promocionSalarial.getOrden();
			
			idTipoCpt = promocionSalarial.getCpt().getTipoCpt().getIdTipoCpt();
			updateCpt();
			idCpt = promocionSalarial.getCpt().getIdCpt();
			
			if(promocionSalarial.getCpt().getCptPadre() != null){
				idCptPadre = promocionSalarial.getCpt().getCptPadre().getIdCpt();
			}else{
				idCptPadre=null;
			}
			
			Cpt cptActual = new Cpt();
			cptActual = em.find(Cpt.class, idCpt);
			codigoCpt =
				cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
					+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero()
					+ "." + cptActual.getNroEspecifico();
			descripcionCpt=cptActual.getDenominacion();
//			if (plantaCargoDet.getComisionado())//AGREGADO RV
//				modalidadOcupLink1 = "3";			
//			if (plantaCargoDet.getContratado())
//				modalidadOcupLink1 = "2";
//			if (plantaCargoDet.getPermanente())
//				modalidadOcupLink1 = "1";
//			obtenerCodigo();
//			if (plantaCargoDet.getEspecialidades() != null)
//				idEspecialidad = plantaCargoDet.getEspecialidades().getIdEspecialidades();

			mostrarPanel = "panel1";
			isNewLink1 = false;
			isNewLink3 = false;
			isNewLink5 = false;
			isNewLink6 = false;
			isNewLink7 = false;
			isNewLink8 = false;
			isNewLink9 = false;
			isNewLink10 = false;
			setIsNewLink11(false);
			estadoCab = plantaCargoDet.getEstadoCab();
			
//			recuperarCodigoOeeDep(obtenerCodigo(configuracionUoDet));
//			buscarDatosLink3();
//			buscarDatosLink4();
//			buscarNombramientosEdit();
			buscarContenidoFuncionalEditPromocionSalarial();
			buscarRequerimientosMinimosEditPromocionSalarial();
//			buscarCondicionTrabajoEdit();
//			buscarCondicionTrabajoEspecificaEdit();
//			buscarDetCondicionSegurEdit();
//			obtenerUbicacionFisica();
			buscarListaTablaPromocionSalarial();
			

		} else if (this.idPromocionSalarial == null) {
			
			promocionSalarial = new PromocionSalarial();
			
			estadoCab = em.find(EstadoCab.class, new Long("1"));
			promocionSalarial.setEstadoCab(estadoCab);
			promocionSalarial.setActivo(true);
			if (motivoRadioButton == null || !motivoRadioButton.equals("f"))
				mostrarPanel = "panel1";
			tieneUsuario = false;
			isNewLink1 = true;
			isNewLink3 = true;
			isNewLink5 = true;
			isNewLink6 = true;
			isNewLink7 = true;
			isNewLink8 = true;
			isNewLink9 = true;
			isNewLink10 = true;
			isNewLink11= true;
			isEdit = false;

			buscarDatosAsociadosUsuario();

			if (motivoRadioButton == null)
				motivoRadioButton = "n";
			if (cptSelecItem == null || cptSelecItem.size() == 1) {
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
			}
		}
	}
	
	public void limpiar() {
		this.plantaCargoDetFrom = "";
		this.plantaCargoDet = new PlantaCargoDet();
		this.idCpt = null;
		this.configuracionUoCab = new ConfiguracionUoCab();
		this.configuracionUoDet = null;
	}
	//Trae en funcion a li
	public List<TipoNombramiento> traerTipoNombramiento2(Long idTipoPlanta) {

		Cpt cpt = new Cpt();
		Cpt cptPadre=new Cpt();
		cpt = em.find(Cpt.class, idCpt);
		cptPadre=cpt.getCptPadre();
		
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
		listaTipoNombramiento = retorno;
		return retorno;
	}

	public List<TipoNombramiento> lTipoNombramiento(List<TipoNombramiento> lParam) {
		List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
		if (lParam != null)
			for (TipoNombramiento o : lParam) {
				if (o.getActivo()!=null && o.getActivo())
					lista.add(o);
			}

		return lista;
	}

	/**
	 * Mï¿½todo que obtiene la ubicaciï¿½n fisica donde se guardarï¿½ el documento adjunto
	 */
	private void obtenerUbicacionFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;

		String separator = File.separator;
		ubicacionFisica =
			separator + "SICCA" + separator + anho + separator + "OEE" + separator
				+ configuracionUoCab.getIdConfiguracionUo() + separator + "Puestos" + separator
				+ configuracionUoDet.getIdConfiguracionUoDet() + separator
				+ plantaCargoDetHome.getInstance().getIdPlantaCargoDet();
	}

	/**
	 * Mï¿½todo que obtiene el orden sugerido para el puesto actual
	 */
	private void buscarOrdenSugerido() {
		String sql =
			"select max(p.orden) from planificacion.planta_cargo_det p  "
				+ "join planificacion.configuracion_uo_det det  "
				+ "on det.id_configuracion_uo_det = p.id_configuracion_uo_det  "
				+ "join planificacion.configuracion_uo_cab cab  "
				+ "on cab.id_configuracion_uo  = det. id_configuracion_uo   "
				+ "where det.id_configuracion_uo_det = "
				+ configuracionUoDet.getIdConfiguracionUoDet();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			ordenPuesto = 0;
		} else
			ordenPuesto = new Integer(config.toString());
		ordenPuesto = ordenPuesto + 1;
	}

	/**
	 * Llama al listado de la tabla sin_anx
	 * 
	 * @return
	 */
	public String getUrlToPageCategoria() {
		String url =
			"/planificacion/searchForms/SinAnxList.xhtml?fromToPage=planificacion/puestosTrabajo/PlantaCargoDetEdit&filtroSinarh=true";
		return url;
	}

	/**
	 * Busqueda avanzada de Unidad Organizaciï¿½n Dependiente
	 * 
	 * @return
	 */
	public String getUrlToPageUnidadOrgDep() {
		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=planificacion/puestosTrabajo/PlantaCargoDetEdit&sinEntidadIdSinEntidad="
			+ sinEntidad.getIdSinEntidad()
			+ "&idNivelEntidad="
			+ nivelEntidad.getIdSinNivelEntidad()
			+ "&idConfiguracionUoCab="
			+ configuracionUoCab.getIdConfiguracionUo();
	}

	
	public String getUrlToPageUnidadOrgDepPromocion() {
		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=planificacion/promocionSalarial/PromocionSalarialEdit&sinEntidadIdSinEntidad="
			+ sinEntidad.getIdSinEntidad()
			+ "&idNivelEntidad="
			+ nivelEntidad.getIdSinNivelEntidad()
			+ "&idConfiguracionUoCab="
			+ configuracionUoCab.getIdConfiguracionUo();
	}

	/**
	 * Busqueda avanzada de CPT
	 * 
	 * @return
	 */
	public String getUrlToPageCpt() {
		return "/planificacion/searchForms/CptEspecificoList.xhtml?from=planificacion/puestosTrabajo/PlantaCargoDetEdit&tipoCpt="
			+ idTipoCpt + "&tipo=especifico";
	}
	
	public String getUrlToPageCptPromocion() {
		return "/planificacion/searchForms/CptEspecificoList.xhtml?from=planificacion/promocionSalarial/PromocionSalarialEdit&tipoCpt="
			+ idTipoCpt + "&tipo=especifico";
	}

	/**********************************************************
	 * Metodos que recuperan datos para editar
	 **********************************************************/

	/**
	 * Busca datos para editar el Link5 Nombramiento
	 */
	@SuppressWarnings("unchecked")
	private void buscarNombramientosEdit() {
		buscarTipoPlanta();
		String cad =
			"select * from planificacion.det_tipo_nombramiento det_tipo"
				+ " where det_tipo.id_planta_cargo_det = " + plantaCargoDet.getIdPlantaCargoDet();
		listaLink5Aux = new ArrayList<DetTipoNombramiento>();
		listaLink5Aux = em.createNativeQuery(cad, DetTipoNombramiento.class).getResultList();
		for (int i = 0; i < listaTipoPlanta.size(); i++) {
			TipoPlanta planta = new TipoPlanta();
			planta = listaTipoPlanta.get(i);

			String sql =
				" select * from planificacion.tipo_nombramiento "
					+ " where activo is true and id_tipo_planta = " + planta.getId();
			List<TipoNombramiento> listaNombramientos =
				em.createNativeQuery(sql, TipoNombramiento.class).getResultList();

			for (DetTipoNombramiento detalle : listaLink5Aux) {
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
	 * Busca la Descripciï¿½n del Contenido Funcional
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DetDescripcionContFuncional> searchDetDescContFuncional(Long id) {
		String sql =
			"select d.* " + "from planificacion.det_descripcion_cont_funcional d "
				+ "where d.id_contenido_funcional = " + id;
		return em.createNativeQuery(sql, DetDescripcionContFuncional.class).getResultList();
	}

	/**
	 * Busca datos para editar el Link06 Contenido Funcional
	 */
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncionalEdit() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.id_planta_cargo_det = "
				+ plantaCargoDet.getIdPlantaCargoDet();
		listaLink6Aux = new ArrayList<DetContenidoFuncional>();
		listaLink6Aux = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaLink6Aux) {
				ContenidoFuncional contenidoActual = new ContenidoFuncional();
				contenidoActual = det.getContenidoFuncional();
				if (contenidoActual != null
					&& contenidoActual.getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					List<DetDescripcionContFuncional> listaDesc = new ArrayList<DetDescripcionContFuncional>();
					listaDesc = searchDetDescContFuncional(det.getIdDetContenidoFuncional());
					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoLink6.add(dto);
				
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
				listaDtoLink6.add(dto);
			
			}
		}

	}
	
	
	
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncionalEditPromocionSalarial() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.id_cpt = "
				+ promocionSalarial.getCpt().getIdCpt();
		listaLink6Aux = new ArrayList<DetContenidoFuncional>();
		listaLink6Aux = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaLink6Aux) {
				ContenidoFuncional contenidoActual = new ContenidoFuncional();
				contenidoActual = det.getContenidoFuncional();
				if (contenidoActual != null
					&& contenidoActual.getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					List<DetDescripcionContFuncional> listaDesc = new ArrayList<DetDescripcionContFuncional>();
					listaDesc = searchDetDescContFuncional(det.getIdDetContenidoFuncional());
					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoLink6.add(dto);
				
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
				listaDtoLink6.add(dto);
			
			}
		}

	}

	/**
	 * Busca datos para editar el Link07 Contenido Funcional
	 */

	@SuppressWarnings({ "unchecked", "unused" })
	private void buscarRequerimientosMinimosEdit() {
		String cad =
			"select * from planificacion.det_req_min det_req"
				+ " where det_req.id_planta_cargo_det = " + plantaCargoDet.getIdPlantaCargoDet();
		listaLink7Aux = new ArrayList<DetReqMin>();
		listaLink7Aux = em.createNativeQuery(cad, DetReqMin.class).getResultList();
		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();
		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaLink7Aux) {
				RequisitoMinimoCpt reqCptActual = new RequisitoMinimoCpt();
				reqCptActual = det.getRequisitoMinimoCpt();
				if (reqCptActual != null
					&& reqCptActual.getIdRequisitoMinimoCpt().equals(req.getIdRequisitoMinimoCpt())) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv =
						new ArrayList<DetOpcionesConvenientes>();
					listaConv = buscarOpciones(det.getIdDetReqMin());
					Integer tamconv = listaConv.size();

					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);
					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
					listaReq = buscarMinimos(det.getIdDetReqMin());
					Integer tamreqmin = listaReq.size();

					DetMinimosRequeridos r = new DetMinimosRequeridos();
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					listaDtoLink7.add(dto);
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
				listaDtoLink7.add(dto);
			}
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void buscarRequerimientosMinimosEditPromocionSalarial() {
		String cad =
			"select * from planificacion.det_req_min det_req"
				+ " where det_req.id_cpt = " + promocionSalarial.getCpt().getId();
		listaLink7Aux = new ArrayList<DetReqMin>();
		listaLink7Aux = em.createNativeQuery(cad, DetReqMin.class).getResultList();
		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();
		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaLink7Aux) {
				RequisitoMinimoCpt reqCptActual = new RequisitoMinimoCpt();
				reqCptActual = det.getRequisitoMinimoCpt();
				if (reqCptActual != null
					&& reqCptActual.getIdRequisitoMinimoCpt().equals(req.getIdRequisitoMinimoCpt())) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv =
						new ArrayList<DetOpcionesConvenientes>();
					listaConv = buscarOpciones(det.getIdDetReqMin());
					Integer tamconv = listaConv.size();

					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);
					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
					listaReq = buscarMinimos(det.getIdDetReqMin());
					Integer tamreqmin = listaReq.size();

					DetMinimosRequeridos r = new DetMinimosRequeridos();
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					listaDtoLink7.add(dto);
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
				listaDtoLink7.add(dto);
			}
		}
	}

	/**
	 * Mï¿½todo que busca Opciones Convenientes
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DetOpcionesConvenientes> buscarOpciones(Long id) {
		String cad =
			"select opc.* from planificacion.det_opciones_convenientes opc"
				+ " where opc.id_det_req_min = " + id;

		return em.createNativeQuery(cad, DetOpcionesConvenientes.class).getResultList();
	}

	/**
	 * Mï¿½todo que recupera datos para el link Requisitos Minimos
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DetMinimosRequeridos> buscarMinimos(Long id) {
		String cad =
			"select min.* from planificacion.det_minimos_requeridos min"
				+ " where min.id_det_req_min = " + id;

		return em.createNativeQuery(cad, DetMinimosRequeridos.class).getResultList();
	}

	/**
	 * Busca datos para editar el Link8 Condiciï¿½n de Trabajo
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEdit() {
		String cad =
			"select * from planificacion.det_condicion_trabajo det_trab"
				+ " where det_trab.id_planta_cargo_det = " + plantaCargoDet.getIdPlantaCargoDet();
		listaLink8Aux = new ArrayList<DetCondicionTrabajo>();
		listaLink8Aux = em.createNativeQuery(cad, DetCondicionTrabajo.class).getResultList();
		String cadena =
			"select * from planificacion.condicion_trabajo cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajo> lista = new ArrayList<CondicionTrabajo>();
		lista = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
		listaCondicionDtoLink8 = new ArrayList<CondicionTrabajoDTO>();
		for (CondicionTrabajo condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajo det : listaLink8Aux) {
				if (det.getCondicionTrabajo().getIdCondicionTrabajo().equals(condicion.getIdCondicionTrabajo())) {
					esta = true;
					CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
					dto.setCondicionTrabajo(det.getCondicionTrabajo());
					dto.setId(det.getIdDetCondiconTrabajo());
					dto.setActivo(det.getActivo());
					dto.setPuntaje(det.getPuntajeCondTrab());
					listaCondicionDtoLink8.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
				dto.setCondicionTrabajo(condicion);
				listaCondicionDtoLink8.add(dto);
			}
		}
	}

	/**
	 * Busca datos para editar el Link09 Condiciï¿½n de Trabajo Especï¿½fica
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEspecificaEdit() {
		String cad =
			"select * from planificacion.det_condicion_trabajo_especif det_trab"
				+ " where det_trab.id_planta_cargo_det = " + plantaCargoDet.getIdPlantaCargoDet();
		List<DetCondicionTrabajoEspecif> listaDet = new ArrayList<DetCondicionTrabajoEspecif>();
		listaDet = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajoEspecif> lista = new ArrayList<CondicionTrabajoEspecif>();

		lista = em.createNativeQuery(cadena, CondicionTrabajoEspecif.class).getResultList();
		listaDtoLink9 = new ArrayList<CondicionTrabajoEspecifDTO>();
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
					listaDtoLink9.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
				dto.setCondicionTrabajoEspecif(condicion);
				listaDtoLink9.add(dto);
			}
		}
	}

	/**
	 * Busca datos para editar el Link10 Condiciones de Seguridad
	 */
	@SuppressWarnings("unchecked")
	private void buscarDetCondicionSegurEdit() {
		String cad =
			"select * from planificacion.det_condicion_segur det_cond"
				+ " where det_cond.id_planta_cargo_det = " + plantaCargoDet.getIdPlantaCargoDet();
		listaLink10Aux = new ArrayList<DetCondicionSegur>();
		listaLink10Aux = em.createNativeQuery(cad, DetCondicionSegur.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> lista = new ArrayList<CondicionSegur>();

		lista = em.createNativeQuery(cadena, CondicionSegur.class).getResultList();
		listaDtoLink10 = new ArrayList<CondicionSeguridadDTO>();
		for (CondicionSegur seg : lista) {
			Boolean esta = false;
			for (DetCondicionSegur det : listaLink10Aux) {
				if (det.getCondicionSegur().getIdCondicionSegur().equals(seg.getIdCondicionSegur())) {
					esta = true;
					CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
					dto.setCondicionSegur(det.getCondicionSegur());
					dto.setId(det.getIdDetCondicionSegur());
					dto.setActivo(det.getActivo());
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondSegur());
					listaDtoLink10.add(dto);
				}
			}
			if (!esta) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setCondicionSegur(seg);
				listaDtoLink10.add(dto);
			}
		}

	}

	/**
	 * Busca datos para el link3 Valor Econï¿½mico
	 */
	@SuppressWarnings("unchecked")
	private void buscarDatosLink3() {
		Referencias referencias =
			referenciasUtilFormController.getReferencia("ESTADOS_CATEGORIA", "MODELO");
		String sql =
			"select pag.* from planificacion.puesto_concepto_pago pag " + "where pag.estado = "
				+ referencias.getValorNum() + " and pag.id_planta_cargo_det = "
				+ plantaCargoDet.getIdPlantaCargoDet();
		listaLink3 = new ArrayList<PuestoConceptoPago>();
		listaLink3Aux = new ArrayList<PuestoConceptoPago>();
		listaLink3 = em.createNativeQuery(sql, PuestoConceptoPago.class).getResultList();
		if (listaLink3.size() > 0)
			listaLink3Aux.addAll(listaLink3);
	}

	/**
	 * Busca datos para el Link4 Otros Datos
	 */
	private void buscarDatosLink4() {
		idOficina =
			plantaCargoDet.getOficina() != null ? plantaCargoDet.getOficina().getIdOficina() : null;
		if (plantaCargoDet.getDoHoraDesde() != null)
			horaDesdeDomingo = buscarHora(plantaCargoDet.getDoHoraDesde());
		if (plantaCargoDet.getDoHoraHasta() != null)
			horaHastaDomingo = buscarHora(plantaCargoDet.getDoHoraHasta());
		if (plantaCargoDet.getLuHoraDesde() != null)
			horaDesdeLunes = buscarHora(plantaCargoDet.getLuHoraDesde());
		if (plantaCargoDet.getLuHoraHasta() != null)
			horaHastaLunes = buscarHora(plantaCargoDet.getLuHoraHasta());
		if (plantaCargoDet.getMaHoraDesde() != null)
			horaDesdeMartes = buscarHora(plantaCargoDet.getMaHoraDesde());
		if (plantaCargoDet.getMaHoraHasta() != null)
			horaHastaMartes = buscarHora(plantaCargoDet.getMaHoraHasta());
		if (plantaCargoDet.getMiHoraDesde() != null)
			horaDesdeMiercoles = buscarHora(plantaCargoDet.getMiHoraDesde());
		if (plantaCargoDet.getMiHoraHasta() != null)
			horaHastaMiercoles = buscarHora(plantaCargoDet.getMiHoraHasta());
		if (plantaCargoDet.getJuHoraDesde() != null)
			horaDesdeJueves = buscarHora(plantaCargoDet.getJuHoraDesde());
		if (plantaCargoDet.getJuHoraHasta() != null)
			horaHastaJueves = buscarHora(plantaCargoDet.getJuHoraHasta());
		if (plantaCargoDet.getViHoraDesde() != null)
			horaDesdeViernes = buscarHora(plantaCargoDet.getViHoraDesde());
		if (plantaCargoDet.getViHoraHasta() != null)
			horaHastaViernes = buscarHora(plantaCargoDet.getViHoraHasta());
		if (plantaCargoDet.getSaHoraDesde() != null)
			horaDesdeSabado = buscarHora(plantaCargoDet.getSaHoraDesde());
		if (plantaCargoDet.getSaHoraHasta() != null)
			horaHastaSabado = buscarHora(plantaCargoDet.getSaHoraHasta());

	}

	/**
	 * Mï¿½todo que recupera la hora y minuto
	 * 
	 * @param fech
	 * @return String
	 */
	private String buscarHora(Date fech) {
		String cod = fech.toString();
		String[] arrayCod = cod.split(" ");
		String valor = null;
		if (arrayCod.length > 2)
			valor = arrayCod[3];
		else
			valor = arrayCod[1];
		String[] arrayCodigo = valor.split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	/**
	 * Mï¿½todo que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null)
			nivelEntidad = obtenerNivelEntidad(nivelEntidad.getNenCodigo());
		else
			nivelEntidad = new SinNivelEntidad();
	}

	private SinNivelEntidad obtenerNivelEntidad(BigDecimal codNivelEntidad) {
		String sql =
			"Select v.* from planificacion.vw_entidad_oee v " + " where v.codigo_nivel = "
				+ codNivelEntidad;
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()) {
			SinNivelEntidad sinNivelEntidad =
				em.find(SinNivelEntidad.class, sin.get(0).getIdNivelEntidad());
			return sinNivelEntidad;
		}
		return null;
	}

	/**
	 * Mï¿½todo que busca la entidad correspondiente al codigo ingresado y el nivel
	 */
	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null && sinEntidad.getEntCodigo() != null)
			obtenerEntidad(sinEntidad.getEntCodigo());

	}

	private SinEntidad obtenerEntidad(BigDecimal codSinEntidad) {
		String sql =
			"Select v.* from planificacion.vw_entidad_oee v " + " where v.ent_codigo = "
				+ codSinEntidad + " and v.id_sin_nivel_entidad = "
				+ nivelEntidad.getIdSinNivelEntidad();
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()) {
			SinEntidad sinE =
				em.find(SinEntidad.class, sin.get(0).getSinEntidad().getIdSinEntidad());
			return sinE;
		}
		return null;
	}

	private ConfiguracionUoCab obtenerOee() {
		String sql =
			"Select v.* from planificacion.vw_entidad_oee v "
				+ "join planificacion.configuracion_uo_cab uo on uo.id_configuracion_uo = v.id_configuracion_uo"
				+ " where v.id_sin_entidad = " + sinEntidad.getIdSinEntidad()
				+ " and v.id_sin_nivel_entidad = " + nivelEntidad.getIdSinNivelEntidad()
				+ " and uo.orden = " + ordenUnidOrg;
		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()) {
			ConfiguracionUoCab oee =
				em.find(ConfiguracionUoCab.class, sin.get(0).getConfiguracionUo().getIdConfiguracionUo());
			return oee;
		}
		return null;
	}

	/**
	 * Mï¿½todo que busca la unidad organizativa correspondiente al codigo ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {
		if (ordenUnidOrg != null) {
			configuracionUoCab = obtenerOee();
			idConfigCab = configuracionUoCab.getIdConfiguracionUo();
		} else
			configuracionUoCab = new ConfiguracionUoCab();
	}

	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	/**
	 * Mï¿½todo que busca la unidad organizativa dependiente de la unidad organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */
	public void obtenerUnidadOrganizativaDep() {
		try {
			if (codigoUnidOrgDep != null && !codigoUnidOrgDep.trim().isEmpty()) {
				String[] arrayCodigo = codigoUnidOrgDep.split("\\.");
				Integer orden = new Integer(arrayCodigo[0]);
				Integer tam = arrayCodigo.length;
				findUnidadOrganizativa();
				configuracionUoDet = new ConfiguracionUoDet();

				configuracionUoDet = buscarDetalle(configuracionUoCab, orden);

				ConfiguracionUoDet det = new ConfiguracionUoDet();
				if (tam == 1)
					det = null;
				for (int i = 1; i < arrayCodigo.length; i++) {
					Integer ord = new Integer(arrayCodigo[i]);

					det = buscarDetalle(configuracionUoDet, ord);
					if (det != null)
						configuracionUoDet = det;
				}
				if (motivoRadioButton == null || motivoRadioButton.equals("n")) {
					buscarOrdenSugerido();

				}
				if (idBusqUnOrgDep != null) {
					obtenerCodigo();
					statusMessages.clear();
				}
			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("nivel_msg_1"));
		}

	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoCab padre, Integer orden) {
		String cad =
			"select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo = " + padre.getIdConfiguracionUo()
				+ " and det.orden = " + orden + " and det.id_configuracion_uo_det_padre is null";
		List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
		lista = em.createNativeQuery(cad, ConfiguracionUoDet.class).getResultList();
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (lista.size() > 0) {
			actual = lista.get(0);
			return actual;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoDet padre, Integer orden) {
		String cad =
			"select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo_det_padre = " + padre.getIdConfiguracionUoDet()
				+ " and det.orden = " + orden;
		List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
		lista = em.createNativeQuery(cad, ConfiguracionUoDet.class).getResultList();
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (lista.size() > 0) {
			actual = lista.get(0);
			return actual;
		}
		return null;
	}

	/**
	 * Mï¿½todo que busca el CPT
	 */
	@SuppressWarnings("unchecked")
	public void findCpt() {
		try {
			if (codigoCpt != null && !codigoCpt.equals("")) {
				Integer nivelCpt = null;
				Integer gradoMin = null;
				Integer gradoMax = null;
				Integer numero = null;
				Integer nroEspecifico = null;
				String[] arrayCodigo = codigoCpt.split("\\.");
				for (int i = 0; i < arrayCodigo.length; i++) {
					if (i == 0)
						nivelCpt = new Integer(arrayCodigo[i]);
					if (i == 1)
						gradoMin = new Integer(arrayCodigo[i]);
					if (i == 2)
						gradoMax = new Integer(arrayCodigo[i]);
					if (i == 3)
						numero = new Integer(arrayCodigo[i]);
					if (i == 4)
						nroEspecifico = new Integer(arrayCodigo[i]);
				}
				String cadena =
					" select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
				if (lista.size() > 0) {
					for (Cpt cpt : lista) {
						cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
						idCpt = cpt.getIdCpt();
					}
				}

			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese un cï¿½digo vï¿½lido");

		}

	}

	@SuppressWarnings("unchecked")
	public void updateCpt() {
		String cadena =
			" select cpt.* from planificacion.cpt cpt " + "where cpt.activo is true "
				+ "and cpt.id_tipo_cpt = " + idTipoCpt;

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
		cptSelecItem = new ArrayList<SelectItem>();
		cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (Cpt cpt : lista) {
				cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
			}
		}

		idCpt = null;
		codigoCpt = null;
		idCptFromList = null;
	}

	/**
	 * Mï¿½todo que obtiene el codigo del puesto
	 */
	public void obtenerCodigo() {
		if (configuracionUoDet != null && configuracionUoDet.getIdConfiguracionUoDet() != null) {
			ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
			uoDet = configuracionUoDet;
			String code = "";
			List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
			for (Integer codigo : listCodes) {
				code += codigo + ".";
			}
			if (modalidadOcupLink1.equals("1")) {
				plantaCargoDet.setPermanente(true);
				plantaCargoDet.setContratado(false);
				plantaCargoDet.setComisionado(false);
				code = code + "P";
			}
			/*************************<MODIFICACION>*********************************/
			/*************************MODIFICADO: 24/10/2013*************************/
			/*************************AUTOR: RODRIGO VELAZQUEZ***********************/
			if (modalidadOcupLink1.equals("2")) {
				plantaCargoDet.setPermanente(false);
				plantaCargoDet.setContratado(true);
				plantaCargoDet.setComisionado(false);
				code = code + "P";//<<<<<<<<<<code = code + "C";
			}
			if (modalidadOcupLink1.equals("3")) {
				plantaCargoDet.setPermanente(false);
				plantaCargoDet.setContratado(false);
				plantaCargoDet.setComisionado(true);
				code = code + "P";
			}
			/*************************</MODIFICACION>********************************/
			if (plantaCargoDet != null && plantaCargoDet.getIdPlantaCargoDet() != null)
				code = code + plantaCargoDet.getOrden();
			else
				code = code + ordenPuesto;
			codigoPuestoLink1 = code;
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Escoja primeramente la Unidad Organizativa");
			return;
		}
	}

	/**
	 * Metodo privado llamado en obtenerCodigo()
	 * 
	 * @param uoDet
	 * @param listCodigos
	 * @return
	 */
	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	/**
	 * Mï¿½todo que busca los datos asociados al usuario logueado
	 */
	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {
			tieneUsuario = true;
			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)
				ordenUnidOrg = configuracionUoCab.getOrden();
			sinEntidad = obtenerEntidad();
			nivelEntidad = obtenerNivel();
			ordenOEEDep = "";
			ordenOEEDep =
				ordenOEEDep + nivelEntidad.getNenCodigo() + "." + sinEntidad.getEntCodigo() + "."
					+ configuracionUoCab.getOrden();
		}
	}

	private SinEntidad obtenerEntidad() {
		String sql =
			"Select v.* from planificacion.vw_entidad_oee v " + " where v.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo();

		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()) {
			SinEntidad sinE =
				em.find(SinEntidad.class, sin.get(0).getSinEntidad().getIdSinEntidad());
			return sinE;
		}
		return null;
	}

	private SinNivelEntidad obtenerNivel() {
		String sql =
			"Select v.* from planificacion.vw_entidad_oee v " + " where v.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo() + " and v.id_sin_entidad = "
				+ sinEntidad.getIdSinEntidad();

		List<VwEntidadOee> sin = em.createNativeQuery(sql, VwEntidadOee.class).getResultList();
		if (!sin.isEmpty()) {
			SinNivelEntidad nivel = em.find(SinNivelEntidad.class, sin.get(0).getIdNivelEntidad());
			return nivel;
		}
		return null;
	}

	/**
	 * Chequea que todos los datos obligatorios sean cargados en el Link1 Datos del Puesto
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Boolean checkLink1() {
		if (configuracionUoDet == null || idCpt == null || plantaCargoDet == null
			|| plantaCargoDet.getDescripcion() == null
			|| plantaCargoDet.getDescripcion().trim().isEmpty()
			/*|| plantaCargoDet.getFundamentacionTecnica() == null
			|| plantaCargoDet.getFundamentacionTecnica().trim().isEmpty()*/
			|| modalidadOcupLink1 == null) {

			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return false;
		} else {
			// Verificar que no se repitan La unidad Organizativa y el Numero de
			// Orden
			String consulta =
				"" + "select plantaCargoDet from PlantaCargoDet plantaCargoDet "
					+ "join plantaCargoDet.configuracionUoDet configuracionUoDet "
					+ "where plantaCargoDet.orden = :orden "
					+ "	and configuracionUoDet.idConfiguracionUoDet = :idConfiguracionUoDet";

			if (plantaCargoDet.getId() != null)
				consulta += "	and plantaCargoDet.idPlantaCargoDet <> :idPlantaCargoDet";

			javax.persistence.Query q = em.createQuery(consulta);
			q.setParameter("orden", ordenPuesto);
			q.setParameter("idConfiguracionUoDet", configuracionUoDet.getIdConfiguracionUoDet());

			if (plantaCargoDet.getId() != null)
				q.setParameter("idPlantaCargoDet", plantaCargoDet.getId());

			List lista = q.getResultList();
			if (lista != null && lista.size() > 0) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La unidad Organizativa y el Nï¿½mero de Orden ya estï¿½n siendo utilizado por otro registro. Verifique.");
				return false;
			}

		}
		return true;
	}

	/**
	 * Mï¿½todo que guarda el Link1 Datos del Puesto
	 */
	@SuppressWarnings("unchecked")
	public void guardarLink1() {
		if (!checkLink1()) {

			return;
		}
		try {
			if (configuracionUoDet != null)
				plantaCargoDet.setConfiguracionUoDet(configuracionUoDet);

			plantaCargoDet.setCpt(em.find(Cpt.class, idCpt));

			if (idEspecialidad != null)
				plantaCargoDet.setEspecialidades(em.find(Especialidades.class, idEspecialidad));
			if (ordenPuesto == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ingrese el orden");
				return;
			}

			if (!checkJefatura()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ya existe un cargo de Jefatura para la Unidad Organizativa seleccionada. Verificar.");
				return;
			}

			plantaCargoDet.setJefatura(jefatura);
			
			if(!esHomologador)
				plantaCargoDet.setEsPlantilla(false);
			
			plantaCargoDet.setOrden(ordenPuesto);
			plantaCargoDet.setDescripcion(plantaCargoDet.getDescripcion().trim().toUpperCase());
//			plantaCargoDet.setFundamentacionTecnica(plantaCargoDet.getFundamentacionTecnica().trim().toUpperCase());
			if (plantaCargoDet.getMision() != null && !plantaCargoDet.getMision().equals(""))
				plantaCargoDet.setMision(plantaCargoDet.getMision().trim().toUpperCase());
			else
				plantaCargoDet.setMision(null);
			if (plantaCargoDet.getObjetivo() != null && !plantaCargoDet.getObjetivo().equals(""))
				plantaCargoDet.setObjetivo(plantaCargoDet.getObjetivo().trim().toUpperCase());
			else
				plantaCargoDet.setObjetivo(null);
			plantaCargoDetHome.setInstance(plantaCargoDet);
			String resultado = plantaCargoDetHome.persist();
			if (resultado.equals("persisted")) {
				HistoricosEstado hist = new HistoricosEstado();
				hist.setEstadoCab(estadoCab);
				hist.setPlantaCargoDet(plantaCargoDetHome.getInstance());
				em.persist(hist);
				em.flush();				
				buscarNombramientos();
				guardarLink5();
				buscarContenidoFuncional();
				guardarLink6();
				buscarRequerimientosMinimos();
				guardarLink7();
				buscarCondicionTrabajo();
				guardarLink8();
				buscarCondicionTrabajoEspecifica();
				guardarLink9();
				buscarDetCondicionSegur();
				guardarLink10();
				buscarListaTabla();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			}
			guardarFusiones();
			obtenerUbicacionFisica();
			//ZD 04/11/15 HABILITAR BOTON GUARDAR
			mostrarTodosLinks = true;
			mostrarPanel = "panel4";
			isNewLink1 = false;	
			plantaCargoDetFrom = "nuevo";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void guardarPromocionSalarial() {
		if (!checkPromocionSalarial()) {

			return;
		}
		try {
			if (configuracionUoCab != null)
				promocionSalarial.setConfiguracionUoCab(configuracionUoCab);

			promocionSalarial.setCpt(em.find(Cpt.class, idCpt));

			
//			if (ordenPuesto == null) {
//				statusMessages.clear();
//				statusMessages.add(Severity.ERROR, "Ingrese el orden");
//				return;
//			}


//			promocionSalarial.setOrden(ordenPuesto);
			promocionSalarial.setDescripcion(promocionSalarial.getDescripcion().trim().toUpperCase());
			promocionSalarial.setEstadoCab(estadoCab);
			promocionSalarial.setActivo(true);
			promocionSalarial.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			promocionSalarial.setFechaAlta(new Date());

			String resultado = "";
			try {
				em.persist(promocionSalarial);
				em.flush();
				this.idPromocionSalarial = promocionSalarial.getIdPromocionSalarial();
				buscarContenidoFuncionalEditPromocionSalarial();
				buscarRequerimientosMinimosEditPromocionSalarial();
				buscarListaTablaPromocionSalarial();
				resultado = "persisted";
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
			if (resultado.equals("persisted")) {
				
				buscarListaTabla();
				mostrarTodosLinks = true;
				
//				mostrarPanel = "panel4";
				isNewLink1 = false;				
				
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			}
//			guardarFusiones();
//			obtenerUbicacionFisica();
			this.plantaCargoDetFrom = "editar";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private Boolean checkPromocionSalarial() {
		if (configuracionUoCab == null || idCpt == null || promocionSalarial == null
			|| promocionSalarial.getDescripcion() == null
			|| promocionSalarial.getDescripcion().trim().isEmpty()) {

			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return false;
		
		} 
//			else if( this.ordenPuesto != null ){
//			// Verificar que no se repitan La unidad Organizativa y el Numero de
//			// Orden
//			String consulta = "select * from seleccion.promocion_salarial ps "
//					+ " join planificacion.configuracion_uo_cab det "
//					+ " on det.id_configuracion_uo = ps.id_configuracion_uo_cab"
//					+ " where ps.orden = "+ this.ordenPuesto;
//		
//
//			if (promocionSalarial.getIdPromocionSalarial() != null)
//				consulta += " and ps.id_promocion_salarial  <> " +  promocionSalarial.getIdPromocionSalarial();
//
//
//			
//			List lista = em.createNativeQuery(consulta, PromocionSalarial.class).getResultList();
//			if (lista != null && lista.size() > 0) {
//				statusMessages.clear();
//				statusMessages.add(Severity.ERROR, "El Nï¿½mero de Orden ya estï¿½n siendo utilizado por otro registro. Verifique.");
//				return false;
//			}
//
//		}
		return true;
	}
	
	public void buscarListaTabla(){
		Cpt cpt= new Cpt();
		cpt = em.find(Cpt.class, idCpt);
		
		if(cpt.getCptPadre() != null && cpt.getCptPadre().getIdCpt() != null){
			String	q1 = "select nc.descripcion, nc.tipo from planificacion.cpt_niveles_cargos cptnc,"
					+ " planificacion.niveles_de_cargos nc where cptnc.id_niveles_de_cargos = "
					+ "nc.id_niveles_de_cargos and cptnc.id_cpt ="+cpt.getCptPadre().getIdCpt();
			
			
			List larray = em.createNativeQuery(q1).getResultList();
		
			listaTabla = new ArrayList<String>();
			listaNivelesDeCargos = new ArrayList<NivelesDeCargos>();
		
			for (int i = 0; i < larray.size(); i++) {
				Object[] obj = (Object[]) larray.get(i);
				
				NivelesDeCargos aux = new NivelesDeCargos();
				aux.setDescripcion((String) obj[0]);
				aux.setTipo(obj[1].toString());
				listaNivelesDeCargos.add(aux);
			}
				
		}
		
	
	}
	
	
	public void buscarListaTablaPromocionSalarial(){
		Cpt cpt= new Cpt();
		cpt = em.find(Cpt.class, idCpt);
		
		if(cpt.getCptPadre() != null && cpt.getCptPadre().getIdCpt() != null){
			String	q1 = "select nc.descripcion, nc.tipo from planificacion.cpt_niveles_cargos cptnc,"
					+ " planificacion.niveles_de_cargos nc where cptnc.id_niveles_de_cargos = "
					+ "nc.id_niveles_de_cargos and cptnc.id_cpt ="+cpt.getCptPadre().getIdCpt();
			
			
			List larray = em.createNativeQuery(q1).getResultList();
		
			listaTabla = new ArrayList<String>();
			listaNivelesDeCargos = new ArrayList<NivelesDeCargos>();
		
			for (int i = 0; i < larray.size(); i++) {
				Object[] obj = (Object[]) larray.get(i);
				
				NivelesDeCargos aux = new NivelesDeCargos();
				aux.setDescripcion((String) obj[0]);
				aux.setTipo(obj[1].toString());
				listaNivelesDeCargos.add(aux);
			}
				
		}
		
	
	}
	
	
	
	

	@SuppressWarnings("unchecked")
	private boolean checkJefatura() {
		if (jefatura == null || !jefatura)
			return true;

		if (configuracionUoDet != null && configuracionUoDet.getIdConfiguracionUoDet() != null) {
			String consulta =
				"" + "select plantaCargoDet from PlantaCargoDet plantaCargoDet "
					+ "join plantaCargoDet.configuracionUoDet configuracionUoDet "
					+ "where configuracionUoDet.idConfiguracionUoDet = :idConfiguracionUoDet "
					+ "and plantaCargoDet.jefatura is true ";

			if (plantaCargoDet.getIdPlantaCargoDet() != null)
				consulta += "and plantaCargoDet.idPlantaCargoDet <> :idPlantaCargoDet ";

			javax.persistence.Query q = em.createQuery(consulta);
			q.setParameter("idConfiguracionUoDet", configuracionUoDet.getIdConfiguracionUoDet());

			if (plantaCargoDet.getIdPlantaCargoDet() != null)
				q.setParameter("idPlantaCargoDet", plantaCargoDet.getIdPlantaCargoDet());

			List<PlantaCargoDet> lista = q.getResultList();
			if (lista != null && lista.size() > 0)
				return false;
		}
		return true;
	}

	private void guardarFusiones() {
		try {
			for (PlantaCargoDet pue : puestosFusionados) {
				FusionPuesto fusion = new FusionPuesto();
				fusion.setPlantaCargoDetByIdPlantaCargoDet(plantaCargoDetHome.getInstance());
				fusion.setPlantaCargoDetByPuestoFusionado(pue);
				em.persist(fusion);
				em.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Llama al caso de uso 199
	 */
	public void print() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU199_imprimir_puesto");

		String codigoUnidOrgDep = obtenerCodigo(plantaCargoDet.getConfiguracionUoDet());

		grupoPuestosController =
			(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		// String codigoPuesto = grupoPuestosController
		// .obtenerCodigoPuesto(plantaCargoDet);
		String codigoPuesto = seleccionUtilFormController.obtenerCodigoPuesto(plantaCargoDet);

		reportUtilFormController.getParametros().put("idPlantaCargoDet", plantaCargoDet.getIdPlantaCargoDet());
		reportUtilFormController.getParametros().put("codigoUnidOrgDep", codigoUnidOrgDep);
		reportUtilFormController.getParametros().put("codigoPuesto", codigoPuesto);
		reportUtilFormController.imprimirReportePdf();
	}
	
public void imprimirPuestoParametros() {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idCpt",idCpt);
		param.put("idPlantaCargoDet",this.plantaCargoDet.getIdPlantaCargoDet());
		
		
		//SECCION 2.3. VALORACIÓN DEL NIVEL ORGANIZATIVO de las TAREAS
		String sql = " select  funcional.orden, "
				+ "  funcional.descripcion as componente, "
				+ "  valor.descripcion, "
				+ "  valor.desde, "
				+ "  valor.hasta, "
				+ "  det_funcional.puntaje "
				+ "  from planificacion.contenido_funcional funcional "
				+ "  join planificacion.valor_nivel_org valor on valor.id_contenido_funcional = funcional.id_contenido_funcional "
				+ "  join planificacion.det_contenido_funcional det_funcional on det_funcional.id_contenido_funcional = funcional.id_contenido_funcional "
				+ "  and det_funcional.id_planta_cargo_det = "+this.plantaCargoDet.getIdPlantaCargoDet()
				+ "  where funcional.activo is TRUE "
				+ "  order by funcional.orden asc,valor.desde "
				+ "  ";
		
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
		
		
		//SECCION 3.1. VALORACIÓN GENERAL DE LAS CONDICIONES de TRABAJO
		String sql2 = " select  trab.orden, "
				+ "  trab.descripcion as componente, "
				+ "  valor.descripcion, "
				+ "  valor.desde, "
				+ "  valor.hasta, "
				+ "  det_trab.puntaje_cond_trab as puntaje "
				+ "  from planificacion.condicion_trabajo trab "
				+ "  join planificacion.escala_cond_trab valor on valor.id_condicion_trabajo = trab.id_condicion_trabajo "
				+ "  join planificacion.det_condicion_trabajo det_trab on det_trab.id_condicion_trabajo = trab.id_condicion_trabajo "
				+ "  and det_trab.id_planta_cargo_det =  "+this.plantaCargoDet.getIdPlantaCargoDet()
				+ "  where trab.activo is TRUE  "
				+ "  order by trab.orden asc,valor.desde "
				+ "  ";
		
		lista = em.createNativeQuery(sql2).getResultList();
		
		 fila = 0;
		 columna = 1;
		 componente = "";
		 escala = "";
		 puntaje = new Float(0);
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("Atipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("Aescala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("Apuntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		//SECCION 3.2. VALORACIÓN ESPECÍFICA DE LAS CONDICIONES de TRABAJO
		// VALORACIÓN DEL PUESTO RESPECTO DE LAS COMPETENCIAS ESPECÍFICAS REQUERIDAS Y AJUSTES POSIBLES

		//Escalas del encabezado
		String sqlEscalas = "select descripcion from planificacion.escala_cond_trab_especif order by desde";
		
		lista = em.createNativeQuery(sqlEscalas).getResultList();
		columna = 1;
		
		for (Object obj : lista){
			
			if(!componente.equals(obj.toString())){
				componente = obj.toString(); // descripcion del componente
				param.put("3_escala_"+columna, componente);
				columna++;
				
			}
		
		}
		
		//tipos, descripcion, justificacion, ajustes y puntaje. 
		String sqlTipos = "select especif.orden "
				+ " , especif.tipo  "
				+ " ,especif.descripcion   "
				+ " , trabajo_especif.puntaje_cond_trab_especif  "
				+ " , trabajo_especif.justificacion  "
				+ " , trabajo_especif.ajustes  "
				+ " from planificacion.condicion_trabajo_especif especif   "
				+ " join planificacion.det_condicion_trabajo_especif trabajo_especif on trabajo_especif.id_condiciones_trabajo_especif = especif.id_condiciones_trabajo_especif  "
				+ " and trabajo_especif.id_planta_cargo_det =  "+this.plantaCargoDet.getIdPlantaCargoDet()
				+ " where especif.activo = true  "
				+ " order by especif.orden  "
				+ "  ";
		
		lista = em.createNativeQuery(sqlTipos).getResultList();
		
		 fila = 0;
		 columna = 1;
		 String tipo = "";
		 String orden = "";
		 String descripcion  = "";
		 String justificacion = "";
		 String ajustes = "";
		 puntaje = new Float(0);
		
		for (Object[] obj : lista){
			
			if(!tipo.equals(obj[1].toString())){
				tipo = obj[1].toString(); // descripcion del tipo
				fila++;
				param.put("3_tipo_"+fila, tipo);
				
			}
			orden = obj[0].toString();//descripcion 
			param.put("3_orden_"+fila, orden);
			descripcion = obj[2].toString();//descripcion 
			param.put("3_descripcion_"+fila, descripcion);
			
			puntaje = Float.parseFloat(obj[3].toString());//puntaje 
			param.put("3_puntaje_"+fila, puntaje);
			
			justificacion =obj[4].toString();//justificacion 
			param.put("3_justificacion_"+fila, justificacion);
			
			ajustes =obj[5].toString();//ajustes 
			param.put("3_ajustes_"+fila, ajustes);
			
			if(columna < 6){
				columna ++;
			}else{
				if(columna == 6)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		//3.3 EVALUACIÓN DE CONDICIONES DE SEGURIDAD
		//verificar de donde traer las escalas de los encabezado
		
		//Escalas del encabezado
				sqlEscalas = "select descripcion from planificacion.escala_cond_segur order by desde";
				
				lista = em.createNativeQuery(sqlEscalas).getResultList();
				columna = 1;
				
				for (Object obj : lista){
					
					if(!componente.equals(obj.toString())){
						componente = obj.toString(); // descripcion del componente
						param.put("4_escala_"+columna, componente);
						columna++;
						
					}
				
				}
				
		//tipos, descripcion, justificacion, ajustes y puntaje. 
				 sqlTipos = "select segur.orden as tipo"
						+ " , segur.descripcion   "
						+ " , det_segur.puntaje_cond_segur  "
						+ " , det_segur.justificacion  "
						+ " , det_segur.ajustes  "
						+ " from planificacion.condicion_segur segur   "
						+ " join planificacion.det_condicion_segur det_segur on det_segur.id_condicion_segur = segur.id_condicion_segur "
						+ " and det_segur.id_planta_cargo_det =  "+this.plantaCargoDet.getIdPlantaCargoDet()
						+ " where segur.activo = true  "
						+ " order by segur.orden  "
						+ "  ";
				
				lista = em.createNativeQuery(sqlTipos).getResultList();
				
				 fila = 0;
				 columna = 1;
				  tipo = "";
				  orden = "";
				  descripcion  = "";
				  justificacion = "";
				  ajustes = "";
				 puntaje = new Float(0);
				
				for (Object[] obj : lista){
					
					if(!tipo.equals(obj[0].toString())){
						tipo = obj[0].toString(); // descripcion del tipo
						fila++;
						param.put("4_tipo_"+fila, tipo);
						
					}
					
					descripcion = obj[1].toString();//descripcion 
					param.put("4_descripcion_"+fila, descripcion);
					
					puntaje = Float.parseFloat(obj[2].toString());//puntaje 
					param.put("4_puntaje_"+fila, puntaje);
					
					justificacion =obj[3].toString();//justificacion 
					param.put("4_justificacion_"+fila, justificacion);
					
					ajustes =obj[4].toString();//ajustes 
					param.put("4_ajustes_"+fila, ajustes);
					
					if(columna < 5){
						columna ++;
					}else{
						if(columna == 5)
							columna = 1;
						else
							columna++;
						
						}
				}
		
				
		//4. REQUISITOS DEL PUESTO :
		//		4.1 DESCRIPCIÓN DE LOS REQUISITOS MÍNIMOS Y OPCIONALES
				
				
		String sqlRequisitos = "select componente.descripcion as componente	"
				+ ", minimos.minimos_requeridos as minimos	"
				+ ", opciones.opc_convenientes as opciones "
				+ "	from planificacion.det_req_min det_req_min "
				+ "	join planificacion.requisito_minimo_cpt  componente on componente.id_requisito_minimo_cpt = det_req_min.id_requisito_minimo_cpt "
				+ " left join planificacion.det_minimos_requeridos minimos on minimos.id_det_req_min = det_req_min.id_det_req_min"
				+ " left join planificacion.det_opciones_convenientes opciones on opciones.id_det_req_min =  det_req_min.id_det_req_min "
				+ "	where det_req_min.id_planta_cargo_det =  "+this.plantaCargoDet.getIdPlantaCargoDet()
				+ " order by componente.orden";
		
		lista = em.createNativeQuery(sqlRequisitos).getResultList();
		
		fila = 0;
		 columna = 1;
		  componente = "";
		  String requisito = "";
		  String opcion = " ";
		
		for (Object[] obj : lista){
				
			componente = obj[0].toString(); 
			fila++;
			param.put("5_componente_"+fila, componente);
				
			
			
			requisito = obj[1].toString();
			param.put("5_minimo_"+fila, requisito);
			
			if(obj[2] != null)		
				opcion =obj[2].toString();//ajustes 
			
			param.put("5_opcion_"+fila, opcion);
			
			if(columna < 3){
				columna ++;
			}else{
				if(columna == 3)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		//4.2 VALORACIÓN DE LOS REQUISITOS DEL PUESTO
		String sqlValoracion = " select  requisito.orden "
				+ "  , requisito.descripcion as componente "
				+ "  , valor.descripcion "
				+ "  , valor.desde "
				+ "  , valor.hasta "
				+ "  , det_req_min.puntaje_req_min "
				+ "  from planificacion.requisito_minimo_cpt requisito "
				+ "  join planificacion.escala_req_min valor on valor.id_requisito_minimo_cpt = requisito.id_requisito_minimo_cpt "
				+ "  join planificacion.det_req_min det_req_min  on det_req_min.id_requisito_minimo_cpt = requisito.id_requisito_minimo_cpt "
				+ "  and det_req_min .id_planta_cargo_det = "+this.plantaCargoDet.getIdPlantaCargoDet()
				+ "  where requisito.activo is TRUE "
				+ "  order by requisito.orden asc,valor.desde ";
		
		 lista = em.createNativeQuery(sqlValoracion).getResultList();
		
		 fila = 0;
		 columna = 1;
		 componente = "";
		 escala = "";
		 puntaje = new Float(0);
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("5_tipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("5_escala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("5_puntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		
		
		
		JasperReportUtils.respondPDF("RPT_imprimirPuesto_nuevo_parametros",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Metodo que carga datos del link a mostrar
	 * 
	 * @param panel
	 */
	public void mostrar(String panel) {

		if (motivoRadioButton == null || motivoRadioButton.equals("n")) {
			mostrarPanel = "panel1";
			if (mostrarPanel.equals("panel1") && !mostrarPanel.equals(panel)) {
				updatedLink1();
			}
			if (mostrarPanel.equals("panel3") && !mostrarPanel.equals(panel)) {
				updatedLink3();
			}
			if (mostrarPanel.equals("panel4") && !mostrarPanel.equals(panel)) {
				updatedLink4();
			}
			if (mostrarPanel.equals("panel5") && !mostrarPanel.equals(panel)) {
				updatedLink5();
			}
			if (mostrarPanel.equals("panel6") && !mostrarPanel.equals(panel)) {
				updatedLink6();
			}
			if (mostrarPanel.equals("panel7") && !mostrarPanel.equals(panel)) {
				updatedLink7();
			}
			if (mostrarPanel.equals("panel8") && !mostrarPanel.equals(panel)) {
				updatedLink8();
			}
			if (mostrarPanel.equals("panel9") && !mostrarPanel.equals(panel)) {
				updatedLink9();
			}
			if (mostrarPanel.equals("panel10") && !mostrarPanel.equals(panel)) {
				updatedLink10();
			}
		}
		statusMessages.clear();
		mostrarPanel = panel;
		if(elFrom == null ||
				!elFrom.equalsIgnoreCase("admDocAdjunto"))
			mostrarTodosLinks = false;
		limpiarValorEconomico();
	}
	
	
	
	public void mostrarPromocion(String panel) {

		
			mostrarPanel = panel;
			return;
		
	}

	/*******************************************************
	 * Hasta aca link1 Datos del Puesto
	 *********************************************************/

	/*******************************************************
	 * Desde aca link3 Valor Econï¿½mico
	 *********************************************************/

	/**
	 * Mï¿½todo que busca el objeto de gasto
	 */
	public void findObj() {
		if (codigoObj != null) {
			if (plantaCargoDet.getContratado()
				&& (codigoObj.intValue() < 140 || codigoObj.intValue() > 149)) {
				//ZD 04/11/15 -- UNIFICAR MENSAJES
				//mensajeLink03 = "Puestos Contratados solo admiten Obj. Gasto 140 al 149. Verifique";
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Puestos Contratados solo admiten Obj. Gasto 140 al 149. Verifique");
				valorObj = null;
				return;
			}
			sinObjList.setCod(codigoObj);
			List<SinObj> lista = new ArrayList<SinObj>();
			lista = sinObjList.listaResultados();
			if (lista.size() > 0) {
				valorObj = lista.get(0).getObjNombre();
				//mensajeLink03 = null;
			} else {
				//ZD 04/11/15 -- UNIFICAR MENSAJES
				//mensajeLink03 = "No existe el Cï¿½digo de Objeto de Gasto ingresado. Verifique";
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "No existe el Codigo de Objeto de Gasto ingresado. Verifique");
				
			}
		}
	}

	/**
	 * Mï¿½todo que busca la categoria correspondiente al Obj. de Gasto y al codigo ingresado
	 */
	@SuppressWarnings("unchecked")
	public void findCategoria() {
		if (!Utiles.vacio(codigoCategoria)) {
			if (codigoObj != null && codigoObj == 111) {
				if (plantaCargoDetHome.getInstance().getPermanente()) {
					String cadCateg =
						"select * from planificacion.categoria_cpt cat " + "where cat.id_cpt = "
							+ idCpt + " and cat.categoria = '" + codigoCategoria + "'";
					List<CategoriaCpt> listaCatCpt = new ArrayList<CategoriaCpt>();
					listaCatCpt =
						em.createNativeQuery(cadCateg, CategoriaCpt.class).getResultList();
					if (listaCatCpt.size() > 0)
						mensajeLink03 = null;
					else {
						//ZD 04/11/15 -- UNIFICAR MENSAJES
						//mensajeLink03 = SeamResourceBundle.getBundle().getString("CU120_link3_msg");
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU120_link3_msg"));
					}

				} else
					mensajeLink03 = null;

				try {

					List<SinAnx> lista = new ArrayList<SinAnx>();
					lista =
						sinarhUtiles.obtenerListaSinAnx(null, new Integer(50), null, codigoCategoria, null);
					if (lista.size() > 0) {
						categoria = lista.get(0).getAnxDescrip();
						buscarMonto(lista.get(0).getCatGrupo());
						mensajeLink03 = null;
					} else {
						//ZD 04/11/15 -- UNIFICAR MENSAJES
						//mensajeLink03 = "La categorï¿½a ingresada no existe para el Cï¿½digo SINARH asignado al OEE. Verifique";
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "La categoria ingresada no existe para el Codigo SINARH asignado al OEE. Verifique");
						
					}

				} catch (Exception e) {
					monto = null;
					e.printStackTrace();
					//mensajeLink03 = "Cï¿½digo de sinarh no vï¿½lido. Verifique.";
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Codigo de sinarh no valido. Verifique.");
					return;
				}
			}
		} else {
			//mensajeLink03 ="Debe asignar el cï¿½digo de sinarh al OEE antes de realizar esta acciï¿½n. Verifique.";
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe asignar el codigo de sinarh al OEE antes de realizar esta accion. Verifique.");
			return;
		}

	}

	/**
	 * Verifica que la OEE cuente con Codigo Sinarh
	 * 
	 * @return
	 */
	public boolean tieneCodigoSinarh() {
		String codigoSinarh = configuracionUoCab.getCodigoSinarh();
		if (!Utiles.vacio(codigoSinarh))
			return true;

		return false;
	}

	/**
	 * Mï¿½todo que limpia las variables para ingresar otro registro
	 */
	public void limpiarValorEconomico() {
		mensajeLink03 = null;
		codigoObj = null;
		valorObj = null;
		codigoCategoria = null;
		categoria = null;
		monto = null;
	}

	/**
	 * Busca el monto correspondiente
	 * 
	 * @param cat_grupo
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void buscarMonto(Integer cat_grupo) {
		if (plantaCargoDetHome.getInstance() != null
			&& plantaCargoDetHome.getInstance().getContratado()) {
			monto = null;

		} else if (plantaCargoDetHome.getInstance() != null
			&& plantaCargoDetHome.getInstance().getPermanente()) {
			String valor = "" + cat_grupo;
			String sql =
				"select * from sinarh.sin_categoria cat " + "where cat.ctg_codigo = '"
					+ codigoCategoria + "'" + " and cat_grupo = " + cat_grupo
					+ " and cat.vrs_codigo = '" + valor + "'";
			List<SinCategoria> listaMonto = new ArrayList<SinCategoria>();
			listaMonto = em.createNativeQuery(sql, SinCategoria.class).getResultList();
			if (listaMonto.size() > 0) {
				Date fechaActual = new Date();
				Integer mes = fechaActual.getMonth();
				if (mes == 0)
					monto = listaMonto.get(0).getCtgValor1();
				if (mes == 1)
					monto = listaMonto.get(0).getCtgValor2();
				if (mes == 2)
					monto = listaMonto.get(0).getCtgValor3();
				if (mes == 3)
					monto = listaMonto.get(0).getCtgValor4();
				if (mes == 4)
					monto = listaMonto.get(0).getCtgValor5();
				if (mes == 5)
					monto = listaMonto.get(0).getCtgValor6();
				if (mes == 6)
					monto = listaMonto.get(0).getCtgValor7();
				if (mes == 7)
					monto = listaMonto.get(0).getCtgValor8();
				if (mes == 8)
					monto = listaMonto.get(0).getCtgValor9();
				if (mes == 9)
					monto = listaMonto.get(0).getCtgValor10();
				if (mes == 10)
					monto = listaMonto.get(0).getCtgValor11();
				if (mes == 11)
					monto = listaMonto.get(0).getCtgValor12();
			}
		}
	}

	/**
	 * Mï¿½todo que agrega un registro a la grilla de Valor Econï¿½mico
	 */
	@SuppressWarnings("deprecation")
	public void agregarListaLink3() {
		mensajeLink03 = null;

		PuestoConceptoPago concepto = new PuestoConceptoPago();

		if (codigoObj == null && monto == null && Utiles.vacio(codigoCategoria)) {
			//ZD 04/11/15 -- UNIFICAR MENSAJES
			mensajeLink03 = "Debe especificar por lo menos un valor antes de agregar. Verifique.";
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe especificar por lo menos un valor antes de agregar. Verifique.");
			
		}
		if (monto != null)
			concepto.setMonto(monto.intValue());

		findObj();
		if (mensajeLink03 != null)
			return;
		if (codigoObj.intValue() == 111 || codigoObj.intValue() == 113
			|| codigoObj.intValue() == 112) {
			if (codigoCategoria == null || codigoCategoria.trim().isEmpty()) {
				mensajeLink03 =
					"La categoria es campo obligatorio para el Codigo OBJ correspondiente . Verifique.";
				//ZD 04/11/15 -- UNIFICAR MENSAJES
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La categoria es campo obligatorio para el Codigo OBJ correspondiente . Verifique.");
				return;
			}
		}
		if (codigoCategoria != null && !codigoCategoria.trim().isEmpty()) {
			findCategoria();
			if (mensajeLink03 != null)
				return;
		}

		if (codigoObj != null) {
			if (plantaCargoDetHome.getInstance().getPermanente()) {

				if (codigoObj >= 140 && codigoObj <= 145) {
					//ZD 04/11/15 -- UNIFICAR MENSAJES
					//mensajeLink03 = "Puestos Permanente no admiten los Objetos de Gastos 140 al 145.";
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Puestos Permanente no admiten los Objetos de Gastos 140 al 145.");
					return;
				}
			}
			concepto.setObjCodigo(codigoObj);
		}

		if (codigoCategoria != null && !codigoCategoria.trim().isEmpty())
			concepto.setCategoria(codigoCategoria);
		Date fecha = new Date();
		Integer anho = fecha.getYear() + 1900;
		concepto.setAnho(anho);

		concepto.setPlantaCargoDet(plantaCargoDetHome.getInstance());
		concepto.setFechaAlta(fecha);
		concepto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		listaLink3.add(concepto);
		codigoCategoria = null;
		codigoObj = null;
		valorObj = null;
		categoria = null;
		monto = null;
		mensajeLink03 = null;
		idSinAnx = null;
	}

	/**
	 * Mï¿½todo que elimina un registro de la grilla Valor Econï¿½mico
	 * 
	 * @param row
	 */
	public void eliminarListaLink3(Integer row) {
		PuestoConceptoPago concepto = new PuestoConceptoPago();
		concepto = listaLink3.get(row);

		listaLink3.remove(concepto);
	}

	/**
	 * Mï¿½todo que guarda los registros de la grilla Valor Econï¿½mico a la bd
	 */
	public void guardarLink3() {
		try {
			Referencias referencias =
				referenciasUtilFormController.getReferencia("ESTADOS_CATEGORIA", "MODELO");
			for (PuestoConceptoPago p : listaLink3) {
				if (referencias != null)
					p.setEstado(referencias.getValorNum());
				p.setActivo(true);
				em.persist(p);
			}
			em.flush();
			mostrarPanel = "panel4";
			isNewLink3 = false;
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link3 Valor Econï¿½mico
	 *********************************************************/

	/*******************************************************
	 * Desde aca link4 Otros Datos
	 *********************************************************/

	/**
	 * Mï¿½todo que carga el combo Oficinas
	 */
	public List<SelectItem> getOficinaSelectItems() {
		if (oficinaSelecItem == null)
			oficinaSelecItem = new ArrayList<SelectItem>();
		else
			oficinaSelecItem.clear();

		oficinaSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (configuracionUoCab != null && configuracionUoCab.getIdConfiguracionUo() != null) {
			List<Oficina> listaOficina = new ArrayList<Oficina>();
			listaOficina = configuracionUoCab.getOficinas();
			for (Oficina ofi : listaOficina) {
				oficinaSelecItem.add(new SelectItem(ofi.getIdOficina(), ofi.getDescripcion()));
			}
		}
		return oficinaSelecItem;
	}

	/**
	 * Metodo que actualiza los datos del Puesto
	 */
	@SuppressWarnings("deprecation")
	public void guardarLink4() {
		try {
			plantaCargoDet = plantaCargoDetHome.getInstance();
			
			
				if (horaDesdeLunes != null && !horaDesdeLunes.equals("")) {
					int[] hora = getHora(horaDesdeLunes);
					if (hora == null) {
						horaDesdeLunes = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setLuHoraDesde(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora desde para el día Lunes");
						return;
					}
				}
				
				if (horaHastaLunes != null && !horaHastaLunes.equals("")) {
					int[] hora = getHora(horaHastaLunes);
					if (hora == null) {
						horaHastaLunes = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setLuHoraHasta(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora hasta para el día Lunes");
						return;
					}
				}
				
				if (horaDesdeMartes != null && !horaDesdeMartes.equals("")) {
					int[] hora = getHora(horaDesdeMartes);
					if (hora == null) {
						horaDesdeMartes = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setMaHoraDesde(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){	
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora desde para el día Martes");
						return;
					}
				}
				
				if (horaHastaMartes != null && !horaHastaMartes.equals("")) {
					int[] hora = getHora(horaHastaMartes);
					if (hora == null) {
						horaHastaMartes = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setMaHoraHasta(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora hasta para el día Martes");
						return;
					}
				}
				if (horaDesdeMiercoles != null && !horaDesdeMiercoles.equals("")) {
					int[] hora = getHora(horaDesdeMiercoles);
					if (hora == null) {
						horaDesdeMiercoles = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setMiHoraDesde(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora desde para el día Miércoles");
						return;
					}
				}
				if (horaHastaMiercoles != null && !horaHastaMiercoles.equals("")) {
					int[] hora = getHora(horaHastaMiercoles);
					if (hora == null) {
						horaHastaMiercoles = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setMiHoraHasta(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora hasta para el día Miércoles");
						return;
					}
				}
				if (horaDesdeJueves != null && !horaDesdeJueves.equals("")) {
					int[] hora = getHora(horaDesdeJueves);
					if (hora == null) {
						horaDesdeJueves = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setJuHoraDesde(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora desde para el día Jueves");
						return;
					}
				}
				if (horaHastaJueves != null && !horaHastaJueves.equals("")) {
					int[] hora = getHora(horaHastaJueves);
					if (hora == null) {
						horaHastaJueves = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setJuHoraHasta(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora hasta para el día Jueves");
						return;
					}
				}
	
				if (horaDesdeViernes != null && !horaDesdeViernes.equals("")) {
					int[] hora = getHora(horaDesdeViernes);
					if (hora == null) {
						horaDesdeViernes = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setViHoraDesde(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora desde para el día Viernes");
						return;
					}
				}
				if (horaHastaViernes != null && !horaHastaViernes.equals("")) {
					int[] hora = getHora(horaHastaViernes);
					if (hora == null) {
						horaHastaViernes = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setViHoraHasta(fecha);
				}else{
					if(plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese hora hasta para el día Viernes");
						return;
					}
				}
	
				if (horaDesdeSabado != null && !horaDesdeSabado.equals("")) {
					int[] hora = getHora(horaDesdeSabado);
					if (hora == null) {
						horaDesdeSabado = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setSaHoraDesde(fecha);
				}
				if (horaHastaSabado != null && !horaHastaSabado.equals("")) {
					int[] hora = getHora(horaHastaSabado);
					if (hora == null) {
						horaHastaSabado = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setSaHoraHasta(fecha);
				}
	
				if (horaDesdeDomingo != null && !horaDesdeDomingo.equals("")) {
					int[] hora = getHora(horaDesdeDomingo);
					if (hora == null) {
						horaDesdeDomingo = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setDoHoraDesde(fecha);
				}
				if (horaHastaDomingo != null && !horaHastaDomingo.equals("")) {
					int[] hora = getHora(horaHastaDomingo);
					if (hora == null) {
						horaHastaDomingo = null;
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Ingrese horarios con el formato hh:mm");
						return;
					}
					Date fecha = new Date();
					fecha.setHours(hora[0]);
					fecha.setMinutes(hora[1]);
					plantaCargoDet.setDoHoraHasta(fecha);
				}
				
				
			
			//EN CASO DE QUE NO SEA EL TIPO DE CPT "CARRERA CIVIL" SE DEBERA CARGAR POR LO MENOS UN DIA 
			if(!plantaCargoDet.getCpt().getTipoCpt().getDescripcion().equals("CARRERA CIVIL")){
				if(
					(horaDesdeLunes != null && horaDesdeLunes.endsWith(""))&&
					(horaHastaLunes != null && horaHastaLunes.endsWith(""))&&
					
					(horaDesdeMartes != null && horaDesdeMartes.endsWith(""))&&
					(horaHastaMartes != null && horaHastaMartes.endsWith(""))&&
					
					(horaDesdeMiercoles != null && horaDesdeMiercoles.endsWith(""))&&
					(horaHastaMiercoles != null && horaHastaMiercoles.endsWith(""))&&
					
					(horaDesdeJueves != null && horaDesdeJueves.endsWith(""))&&
					(horaHastaJueves != null && horaHastaJueves.endsWith(""))&&
					
					(horaDesdeViernes != null && horaDesdeViernes.endsWith(""))&&
					(horaHastaViernes != null && horaHastaViernes.endsWith(""))&&
					
					(horaDesdeSabado != null && horaDesdeSabado.endsWith(""))&&
					(horaHastaSabado != null && horaHastaSabado.endsWith(""))&&
					
					(horaDesdeDomingo != null && horaDesdeDomingo.endsWith(""))&&
					(horaHastaDomingo != null && horaHastaDomingo.endsWith(""))
				){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Debe Registrar el Horario.");
					return;
				}
			}
			
			
				
			if (idOficina != null) {
				Oficina of = new Oficina();
				of = em.find(Oficina.class, idOficina);
				plantaCargoDet.setOficina(of);
			}
			else{
//				ZD 03/11/15 MOSTRAR MSJ AL USUARIO
				mostrarPanel = "panel4";
				isNewLink1 = false;
				mostrarTodosLinks = true;
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Seleccione una Oficina.");
				return;
			}
			plantaCargoDetHome.setInstance(plantaCargoDet);
			String resultado = plantaCargoDetHome.update();
			if (resultado.equals("updated")) {
				mostrarPanel = "panel8";
				mostrarTodosLinks = true;
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			}
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link4 Otros Datos
	 *********************************************************/

	/*******************************************************
	 * Desde aca link5 Nombramiento
	 *********************************************************/

	/**
	 * Mï¿½todo que busca los Nombramientos correspondientes al CPT escogido
	 */
	@SuppressWarnings("unchecked")
	private void buscarNombramientos() {
		buscarTipoPlanta();
		Cpt cpt = new Cpt();
		Cpt cptPadre=new Cpt();
		cpt = em.find(Cpt.class, idCpt);
		cptPadre=cpt.getCptPadre();
		String cad =
			"select * from planificacion.det_tipo_nombramiento det_tipo"
				+ " where det_tipo.id_cpt = " + cptPadre.getIdCpt();
		List<DetTipoNombramiento> listaAuxTab1 = new ArrayList<DetTipoNombramiento>();
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
	 * Metodo que busca los Tipos de planta para el Link5 Nombramiento
	 */
	@SuppressWarnings("unchecked")
	private void buscarTipoPlanta() {
		String cadena =
			"select * from planificacion.tipo_planta tipo " + "where tipo.activo is TRUE";
		listaTipoPlanta = new ArrayList<TipoPlanta>();
		listaTipoPlanta = em.createNativeQuery(cadena, TipoPlanta.class).getResultList();
	}

	/**
	 * Mï¿½todo que guardar el Link5 Nombramiento
	 */
	public void guardarLink5() {
		try {

			for (TipoPlanta o : listaTipoPlanta) {
				List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
				lista = o.getTipoNombramientos();
				for (TipoNombramiento obj : lista) {
					if (obj.getSeleccionado() != null && obj.getSeleccionado()) {
						DetTipoNombramiento det = new DetTipoNombramiento();
						det.setTipoNombramiento(obj);
						det.setPlantaCargoDet(plantaCargoDetHome.getInstance());
						det.setTipo("PUESTO");
						em.persist(det);
						em.flush();
					}
				}
				mostrarTodosLinks = true;
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				mostrarPanel = "panel6";
				isNewLink5 = false;
			}
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link5 Nombramiento
	 *********************************************************/

	/*******************************************************
	 * Desde aca link6 Contenido Funcional
	 *********************************************************/
	/**
	 * Busca datos del Contenido Funcional correspondiente al CPT escogido
	 */
	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncional() {
		String cad =
			"select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.id_cpt = " + idCpt;
		List<DetContenidoFuncional> listaAux = new ArrayList<DetContenidoFuncional>();
		listaAux = em.createNativeQuery(cad, DetContenidoFuncional.class).getResultList();

		String cadena =
			"select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaDtoLink6 = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> lista = new ArrayList<ContenidoFuncional>();

		lista = em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();

		for (ContenidoFuncional contenido : lista) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaAux) {
				if (det.getContenidoFuncional().getIdContenidoFuncional().equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());
					List<DetDescripcionContFuncional> listaDesc =
						new ArrayList<DetDescripcionContFuncional>();
					listaDesc = searchDetDescContFuncional(det.getIdDetContenidoFuncional());
					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaDtoLink6.add(dto);
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
				listaDtoLink6.add(dto);
			}
		}

	}

	/**
	 * Mï¿½todo que agrega un detalle a la sublista de la tabla Contenido Funcional
	 * 
	 * @param row
	 */
	public void agregarListaLink6(Integer row) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaDtoLink6.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet = dto.getListaDetDescripContFuncional();
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		listaDet.add(det);
		listaDtoLink6.set(row, dto);
	}

	/*********************************************************************
	 * Metodo que elimina registros de sublistas de Contenido Funcional
	 *********************************************************************/

	public void eliminarListaLink6(Integer row, Integer subRow) {
		ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
		dto = listaDtoLink6.get(row);
		List<DetDescripcionContFuncional> listaDet = new ArrayList<DetDescripcionContFuncional>();
		listaDet.addAll(dto.getListaDetDescripContFuncional());
		DetDescripcionContFuncional det = new DetDescripcionContFuncional();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		if (listaDet.size() == 0) {
			det = new DetDescripcionContFuncional();
			listaDet.add(det);
		}
		dto.setListaDetDescripContFuncional(listaDet);
		listaDtoLink6.set(row, dto);
	}

	/**
	 * Mï¿½todo que guarda datos del Link6 Contenido Funcional
	 */
	public void guardarLink6() {
		if (!validacionEscalaLink6())
			return;
		try {
			for (ContenidoFuncionalDTO dto : listaDtoLink6) {
				if (dto.getPuntaje() != null) {
					DetContenidoFuncional detContenido = new DetContenidoFuncional();
					detContenido.setContenidoFuncional(dto.getContenidoFuncional());
					detContenido.setPuntaje(dto.getPuntaje());
					detContenido.setTipo("PUESTO");
					detContenido.setActivo(true);
					detContenido.setPlantaCargoDet(plantaCargoDetHome.getInstance());

					detContenidoFuncionalHome.setInstance(detContenido);
					String resultado = detContenidoFuncionalHome.persist();
					if (resultado.equals("persisted")) {
						List<DetDescripcionContFuncional> listaDescripcion =
							new ArrayList<DetDescripcionContFuncional>();
						listaDescripcion = dto.getListaDetDescripContFuncional();
						for (DetDescripcionContFuncional desc : listaDescripcion) {
							if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
								DetDescripcionContFuncional descripcion =
									new DetDescripcionContFuncional();
								descripcion.setActivo(true);
								descripcion.setDescripcion(desc.getDescripcion().trim().toUpperCase());
								descripcion.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
								em.persist(descripcion);
							}
						}
						em.flush();
						detContenidoFuncionalHome.clearInstance();
					}
				}

			}
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mostrarPanel = "panel7";
			isNewLink6 = false;

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link6 Contenido Funcional
	 *********************************************************/

	/*******************************************************
	 * Desde aca link7 Requisitos Minimos
	 *********************************************************/
	/**
	 * Busca datos de la tabla planificacion.det_req_min det_req correspondiente al CPT elegido
	 */
	@SuppressWarnings("unchecked")
	private void buscarRequerimientosMinimos() {
		String cad =
			"select * from planificacion.det_req_min det_req" + " where det_req.id_cpt = " + idCpt;
		List<DetReqMin> listaAuxTab3 = new ArrayList<DetReqMin>();
		listaAuxTab3 = em.createNativeQuery(cad, DetReqMin.class).getResultList();

		String cadena =
			"select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();

		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
		listaDtoLink7 = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			for (DetReqMin det : listaAuxTab3) {
				if (det.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt().equals(req.getIdRequisitoMinimoCpt())) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv =
						new ArrayList<DetOpcionesConvenientes>();
					listaConv = buscarOpciones(det.getIdDetReqMin());
					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);
					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
					listaReq = buscarMinimos(det.getIdDetReqMin());
					DetMinimosRequeridos r = new DetMinimosRequeridos();
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					listaDtoLink7.add(dto);
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
				listaDtoLink7.add(dto);
			}
		}

	}

	/**
	 * Mï¿½todo que agrega un registro a la sublista de Minimos Requeridos de la lista gral
	 * 
	 * @param row
	 */
	public void agregarListaMinimosReqLink7(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);

		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet = dto.getListaDetMinimosRequeridos();
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		listaDet.add(det);
		listaDtoLink7.set(row, dto);

	}

	/**
	 * Mï¿½todo que elimina un registro a la sublista de Minimos Requeridos de la lista gral
	 * 
	 * @param row
	 * @param subRow
	 */
	public void eliminarListaMinimosReqLink7(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);
		List<DetMinimosRequeridos> listaDet = new ArrayList<DetMinimosRequeridos>();
		listaDet.addAll(dto.getListaDetMinimosRequeridos());
		DetMinimosRequeridos det = new DetMinimosRequeridos();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		dto.setListaDetMinimosRequeridos(listaDet);
		listaDtoLink7.set(row, dto);

	}

	/**
	 * Mï¿½todo que agrega un registro a la sublista de Opciones Convenientes de la lista gral
	 * 
	 * @param row
	 */
	public void agregarListaOpcConvenientesLink7(Integer row) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		dto.getListaDetOpcionesConvenientes().add(det);
		listaDtoLink7.set(row, dto);
	}

	/**
	 * Mï¿½todo que agrega un registro a la sublista de Opciones Convenientes de la lista gral
	 * 
	 * @param row
	 * @param subRow
	 */
	public void eliminarListaOpcConvenientesLink7(Integer row, Integer subRow) {
		RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
		dto = listaDtoLink7.get(row);
		List<DetOpcionesConvenientes> listaDet = new ArrayList<DetOpcionesConvenientes>();
		listaDet.addAll(dto.getListaDetOpcionesConvenientes());
		DetOpcionesConvenientes det = new DetOpcionesConvenientes();
		det = listaDet.get(subRow);
		listaDet.remove(det);
		dto.setListaDetOpcionesConvenientes(listaDet);
		listaDtoLink7.set(row, dto);
	}

	/**
	 * Guarda datos cargados en la grilla de Requisitos Minimos y Opciones Convenientes
	 */
	public void guardarLink7() {
		if (!validacionEscalaLink7())
			return;
		try {
			for (RequisitosMinimosDTO dto : listaDtoLink7) {
				if (dto.getPuntaje() != null) {
					DetReqMin detReqMin = new DetReqMin();
					detReqMin.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
					detReqMin.setPuntajeReqMin(dto.getPuntaje());
					detReqMin.setTipo("PUESTO");
					detReqMin.setActivo(true);
					detReqMin.setPlantaCargoDet(plantaCargoDetHome.getInstance());
					detReqMinHome.setInstance(detReqMin);
					String resultado = detReqMinHome.persist();
					if (resultado.equals("persisted")) {
						List<DetMinimosRequeridos> listaDetMinimos =
							new ArrayList<DetMinimosRequeridos>();
						listaDetMinimos = dto.getListaDetMinimosRequeridos();
						for (DetMinimosRequeridos detMin : listaDetMinimos) {
							if (detMin.getMinimosRequeridos() != null
								&& !detMin.getMinimosRequeridos().trim().isEmpty()) {
								DetMinimosRequeridos mini = new DetMinimosRequeridos();

								mini.setMinimosRequeridos(detMin.getMinimosRequeridos().trim().toUpperCase());
								mini.setDetReqMin(new DetReqMin());
								mini.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());
								mini.setActivo(true);
								em.persist(mini);
							}

						}
						em.flush();
						List<DetOpcionesConvenientes> listaDetOpc =
							new ArrayList<DetOpcionesConvenientes>();
						listaDetOpc = dto.getListaDetOpcionesConvenientes();
						for (DetOpcionesConvenientes detOpc : listaDetOpc) {
							if (detOpc.getOpcConvenientes() != null
								&& !detOpc.getOpcConvenientes().equals("")) {
								DetOpcionesConvenientes convenientes =
									new DetOpcionesConvenientes();
								convenientes.setOpcConvenientes(detOpc.getOpcConvenientes().trim().toUpperCase());
								convenientes.setDetReqMin(new DetReqMin());
								convenientes.getDetReqMin().setIdDetReqMin((detReqMinHome.getInstance().getIdDetReqMin()));
								convenientes.setActivo(true);
								em.persist(convenientes);
							}
						}
						em.flush();
						detReqMinHome.clearInstance();
					}
				}
			}
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mostrarPanel = "panel8";
			isNewLink8 = false;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link7 Requisitos Minimos
	 *********************************************************/

	/*******************************************************
	 * Desde aca link8 Condiciï¿½n de Trabajo
	 *********************************************************/
	/**
	 * Busca datos de la planificacion.det_condicion_trabajo correspondiente al cpt elegido
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajo() {
		String cad =
			"select * from planificacion.det_condicion_trabajo det_trab"
				+ " where det_trab.id_cpt = " + idCpt;
		List<DetCondicionTrabajo> listaAuxTab4 = new ArrayList<DetCondicionTrabajo>();
		listaAuxTab4 = em.createNativeQuery(cad, DetCondicionTrabajo.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_trabajo cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajo> lista = new ArrayList<CondicionTrabajo>();

		lista = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
		listaCondicionDtoLink8 = new ArrayList<CondicionTrabajoDTO>();
		for (CondicionTrabajo condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajo det : listaAuxTab4) {
				if (det.getCondicionTrabajo().getIdCondicionTrabajo().equals(condicion.getIdCondicionTrabajo())) {
					esta = true;
					CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
					dto.setCondicionTrabajo(det.getCondicionTrabajo());
					dto.setId(det.getIdDetCondiconTrabajo());
					dto.setActivo(true);
					dto.setPuntaje(det.getPuntajeCondTrab());
					listaCondicionDtoLink8.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
				dto.setCondicionTrabajo(condicion);
				listaCondicionDtoLink8.add(dto);
			}
		}

	}

	/**
	 * Guarda los datos ingresado en Condicion de Trabajo
	 */
	public void guardarLink8() {
		buscarValoracionLink8();
		if (!validacionEscalaLink8()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "El puntaje ingresado no forma parte de la escala");
			return;
		}
		try {

			for (CondicionTrabajoDTO dto : listaCondicionDtoLink8) {
				if (dto.getPuntaje() != null) {
					DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
					detCondTrab.setActivo(true);
					detCondTrab.setCondicionTrabajo(dto.getCondicionTrabajo());
					detCondTrab.setPlantaCargoDet(plantaCargoDetHome.getInstance());
					detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
					detCondTrab.setTipo("PUESTO");
					em.persist(detCondTrab);
				}

			}
			em.flush();
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mostrarPanel = "panel9";
			isNewLink8 = false;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link8 Condiciï¿½n de Trabajo
	 *********************************************************/

	/*******************************************************
	 * Desde aca link9 Condiciï¿½n de Trabajo Especifico
	 *********************************************************/
	/**
	 * Busca datos de la tabla det_condicion_trabajo_especif correspondiente al cpt elegido
	 */
	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEspecifica() {
		String cad =
			"select * from planificacion.det_condicion_trabajo_especif det_trab"
				+ " where det_trab.id_cpt = " + idCpt;
		List<DetCondicionTrabajoEspecif> listaDet = new ArrayList<DetCondicionTrabajoEspecif>();
		listaDet = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajoEspecif> lista = new ArrayList<CondicionTrabajoEspecif>();

		lista = em.createNativeQuery(cadena, CondicionTrabajoEspecif.class).getResultList();
		listaDtoLink9 = new ArrayList<CondicionTrabajoEspecifDTO>();
		for (CondicionTrabajoEspecif condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajoEspecif det : listaDet) {
				if (det.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif().equals(condicion.getIdCondicionesTrabajoEspecif())) {
					esta = true;
					CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
					dto.setCondicionTrabajoEspecif(det.getCondicionTrabajoEspecif());
					dto.setId(det.getIdDetCondicionTrabajoEspecif());
					dto.setActivo(true);
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondTrabEspecif());
					listaDtoLink9.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
				dto.setCondicionTrabajoEspecif(condicion);
				listaDtoLink9.add(dto);
			}
		}

	}

	/**
	 * Guarda los datos ingresados en Condiciï¿½n de Trabajo Especifico
	 */
	public void guardarLink9() {
		buscarValoracionLink9();
		if (!validacionEscalaLink9())
			return;
		try {
			for (CondicionTrabajoEspecifDTO dto : listaDtoLink9) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
					&& dto.getJustificacion() != null) {
					DetCondicionTrabajoEspecif det = new DetCondicionTrabajoEspecif();
					det.setActivo(true);
					det.setAjustes(dto.getAjustes().trim().toUpperCase());
					det.setCondicionTrabajoEspecif(dto.getCondicionTrabajoEspecif());
					det.setPlantaCargoDet(plantaCargoDetHome.getInstance());
					det.setJustificacion(dto.getJustificacion().trim().toUpperCase());
					det.setPuntajeCondTrabEspecif(dto.getPuntaje());
					det.setTipo("PUESTO");
					em.persist(det);
				}
			}
			em.flush();
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mostrarPanel = "panel10";
			isNewLink9 = false;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link9 Condiciï¿½n de Trabajo Especifico
	 *********************************************************/

	/*******************************************************
	 * Desde aca link10 Condiciones de Seguridad
	 *********************************************************/
	/**
	 * Busca datos de la tabla det_condicion_segur correspondiente al cpt escogido
	 */
	@SuppressWarnings("unchecked")
	private void buscarDetCondicionSegur() {
		String cad =
			"select * from planificacion.det_condicion_segur det_cond"
				+ " where det_cond.id_cpt = " + idCpt;
		List<DetCondicionSegur> listaAuxTab6 = new ArrayList<DetCondicionSegur>();
		listaAuxTab6 = em.createNativeQuery(cad, DetCondicionSegur.class).getResultList();

		String cadena =
			"select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> lista = new ArrayList<CondicionSegur>();

		lista = em.createNativeQuery(cadena, CondicionSegur.class).getResultList();
		listaDtoLink10 = new ArrayList<CondicionSeguridadDTO>();
		for (CondicionSegur seg : lista) {
			Boolean esta = false;
			for (DetCondicionSegur det : listaAuxTab6) {
				if (det.getCondicionSegur().getIdCondicionSegur().equals(seg.getIdCondicionSegur())) {
					esta = true;
					CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
					dto.setCondicionSegur(det.getCondicionSegur());
					dto.setId(det.getIdDetCondicionSegur());
					dto.setActivo(true);
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondSegur());
					listaDtoLink10.add(dto);
				}
			}
			if (!esta) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setCondicionSegur(seg);
				listaDtoLink10.add(dto);
			}
		}

	}

	/**
	 * Guarda los datos ingresados en Condiciones de Seguridad
	 */
	public void guardarLink10() {
		buscarValoracionLink10();
		if (!validacionEscalaLink10())
			return;
		try {
			for (CondicionSeguridadDTO dto : listaDtoLink10) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
					&& dto.getJustificacion() != null) {
					DetCondicionSegur det = new DetCondicionSegur();
					det.setActivo(true);
					det.setAjustes(dto.getAjustes().trim().toUpperCase());
					det.setCondicionSegur(dto.getCondicionSegur());
					det.setPlantaCargoDet(plantaCargoDetHome.getInstance());
					det.setJustificacion(dto.getJustificacion().trim().toUpperCase());
					det.setPuntajeCondSegur(dto.getPuntaje());
					det.setTipo("PUESTO");

					em.persist(det);
				}

			}
			em.flush();
			em.flush();
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mostrarPanel = "panel1";
			isNewLink10 = false;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/*******************************************************
	 * Hasta aca link10 Condiciones de Seguridad
	 *********************************************************/

	/*******************************************************
	 * Mï¿½todo que Replica los puestos en caso de que se desee
	 *********************************************************/
	public String replicarPuestos() {
		if (cantPuestosReplicados != null) {
			PlantaCargoDet puesto = new PlantaCargoDet();
			puesto = plantaCargoDetHome.getInstance();
			String resultado = null;
			for (int i = 0; i < cantPuestosReplicados; i++) {
				PlantaCargoDet replica = new PlantaCargoDet();
				replica.setActivo(puesto.getActivo());
				replica.setConfiguracionUoDet(puesto.getConfiguracionUoDet());
				replica.setContratado(puesto.getContratado());
				replica.setComisionado(puesto.getComisionado());//MODIFICADO RV
				replica.setCpt(puesto.getCpt());
				replica.setDescripcion(puesto.getDescripcion());
				replica.setDoHoraDesde(puesto.getDoHoraDesde());
				replica.setDoHoraHasta(puesto.getDoHoraHasta());

				replica.setEspecialidades(puesto.getEspecialidades());
				replica.setEstadoCab(puesto.getEstadoCab());
//				replica.setFundamentacionTecnica(puesto.getFundamentacionTecnica());
				replica.setJuHoraDesde(puesto.getJuHoraDesde());
				replica.setJuHoraHasta(puesto.getJuHoraHasta());
				replica.setLuHoraDesde(puesto.getLuHoraDesde());
				replica.setLuHoraHasta(puesto.getLuHoraHasta());
				replica.setMaHoraDesde(puesto.getMaHoraDesde());
				replica.setMaHoraHasta(puesto.getMaHoraHasta());
				replica.setMiHoraDesde(puesto.getMiHoraDesde());
				replica.setMiHoraHasta(puesto.getMiHoraHasta());
				replica.setMision(puesto.getMision());
				replica.setObjetivo(puesto.getObjetivo());
				replica.setOficina(puesto.getOficina());
				replica.setJefatura(puesto.getJefatura());
				replica.setEstadoCab(em.find(EstadoCab.class, new Long(1)));
				Integer ord =
					buscarOrden2(puesto.getConfiguracionUoDet().getIdConfiguracionUoDet());
				if (ord == null)
					ord = puesto.getOrden();
				replica.setOrden(ord);
				replica.setPermanente(puesto.getPermanente());
				replica.setSaHoraDesde(puesto.getSaHoraDesde());
				replica.setSaHoraHasta(puesto.getSaHoraHasta());
				replica.setViHoraDesde(puesto.getViHoraDesde());
				replica.setViHoraHasta(puesto.getViHoraHasta());
				replica.setEsPlantilla(false);

				plantaCargoDetHome.setInstance(replica);
				resultado = plantaCargoDetHome.persist();
				if (resultado.equals("persisted")) {
					guardarLink4();
					if (!isNewLink3) {
						for (PuestoConceptoPago p : listaLink3) {
							PuestoConceptoPago p2 = new PuestoConceptoPago();
							p2.setActivo(true);
							p2.setAnho(p.getAnho());
							p2.setCategoria(p.getCategoria());
							p2.setDisponible(p.getDisponible());
							p2.setFechaAlta(p.getFechaAlta());
							p2.setMonto(p.getMonto());
							p2.setObjCodigo(p.getObjCodigo());
							p2.setUsuAlta(p.getUsuAlta());
							p2.setEstado(1); //El puesto replicado siempre es "vacante"
							p2.setPlantaCargoDet(plantaCargoDetHome.getInstance());
							em.persist(p2);
						}
						em.flush();
					}
					if (!isNewLink5)
						guardarLink5();

					if (!isNewLink6)
						guardarLink6();
					if (!isNewLink7)
						guardarLink7();

					if (!isNewLink8)
						guardarLink8();
					if (!isNewLink9)
						guardarLink9();
					if (!isNewLink10)
						guardarLink10();
				}
			}
			isEdit = null;
			return resultado;
		}
		return null;

	}

	/*******************************************************
	 * Verificaion y validacion de escalas
	 *********************************************************/
	private Boolean validacionEscalaLink6() {
		for (ContenidoFuncionalDTO dto : listaDtoLink6) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto))
					return false;
			}
		}
		return true;
	}

	private Boolean validacionEscalaLink7() {
		for (RequisitosMinimosDTO dto : listaDtoLink7) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracionLink7(dto)) {
					return false;
				}
			}
		}
		return true;
	}

	private Boolean validacionEscalaLink8() {
		for (CondicionTrabajoDTO dto : listaCondicionDtoLink8) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracionTab04(dto))
					return false;
			}else{
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El puntaje no puede ser nulo");
				return false;
			}
		}
		return true;
	}

	private Boolean validacionEscalaLink9() {
		for (CondicionTrabajoEspecifDTO dto : listaDtoLink9) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracionLink9(dto))

					return false;

			}
		}
		return true;
	}

	private Boolean validacionEscalaLink10() {
		for (CondicionSeguridadDTO dto : listaDtoLink10) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracionLink10(dto)) {
					return false;
				}
			}
		}
		return true;
	}

	public Boolean verificarValoracionLink9(CondicionTrabajoEspecifDTO condicion) {
		List<EscalaCondTrabEspecif> v = new ArrayList<EscalaCondTrabEspecif>();
		if (condicion.getPuntaje() != null) {
			v = listaValoracionLink9.get(0).getListaEscala();
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
				//ZD 04/11/15 -- UNIFICAR MENSAJES
				//mensajeLink09 = "El puntaje ingresado no es valido, verifique la escala";
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El puntaje ingresado no es valido, verifique la escala");
				return cumple;
			}
		}
		//mensajeLink09 = null;
		return true;
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
				//ZD 04/11/15 -- UNIFICAR MENSAJES
				//mensajeLink08 = "El puntaje ingresado no es valido, verifique la escala";
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El puntaje ingresado no es valido, verifique la escala");
				return cumple;
			}
		}
		//mensajeLink08 = null;

		return true;
	}

	public Boolean verificarValoracionLink7(RequisitosMinimosDTO requisito) {
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
			//ZD 04/11/15 -- UNIFICAR MENSAJES
			//mensajeLink07 = "El puntaje ingresado no es valido, verifique la escala";
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "El puntaje ingresado no es valido, verifique la escala");
			return cumple;
		}
		//mensajeLink07 = null;
		return cumple;
	}

	public Boolean verificarValoracionLink10(CondicionSeguridadDTO condicion) {
		List<EscalaCondSegur> v = new ArrayList<EscalaCondSegur>();
		if (condicion.getPuntaje() != null) {
			v = listaValoracionLink10.get(0).getListaEscalaCondSeg();
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
				//ZD 04/11/15 -- UNIFICAR MENSAJES
				//mensajeLink10 = "El puntaje ingresado no es valido, verifique la escala";
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El puntaje ingresado no es valido, verifique la escala");
				return cumple;
			}
		}
		//mensajeLink10 = null;
		return true;
	}

	/**
	 * Mï¿½todo que verifica si el puntaje ingresado en el link06 se encuentra en el rango mostrado en la escala
	 * 
	 * @param contenido
	 * @return
	 */
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
			//ZD 04/11/15 -- UNIFICAR MENSAJES
			//mensajeLink06 = "El puntaje ingresado no es valido, verifique la escala";
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "El puntaje ingresado no es valido, verifique la escala");
			
			return cumple;
		}
		//mensajeLink06 = null;
		return cumple;
	}

	/*******************************************************
	 * Metodos que buscan valoraciones
	 ******************************************************/
	/**
	 * Mï¿½todo que obtiene la valoracion correspondiente al link06; contenido funcional
	 */
	@SuppressWarnings("unchecked")
	public void buscarValoracionTab02() {
		String cadena =
			"select funcional.* from planificacion.contenido_funcional funcional "
				+ "where funcional.activo = TRUE order by funcional.orden";
		listaValoracionTab2 =
			em.createNativeQuery(cadena, ContenidoFuncional.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionTab03() {
		String cadena =
			"select cpt.* from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";
		listaValoracionTab3 =
			em.createNativeQuery(cadena, RequisitoMinimoCpt.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionLink8() {
		String cadena =
			"select trab.* from planificacion.condicion_trabajo trab"
				+ " where trab.activo = TRUE order by trab.orden";
		listaValoracionLink8 = em.createNativeQuery(cadena, CondicionTrabajo.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionLink9() {
		String cadena =
			"select escala.* from planificacion.escala_cond_trab_especif escala "
				+ " where escala.activo = TRUE" + " order by desde, hasta";
		List<EscalaCondTrabEspecif> lista = new ArrayList<EscalaCondTrabEspecif>();
		listaValoracionLink9 = new ArrayList<ValoracionTab05CPT>();
		lista = em.createNativeQuery(cadena, EscalaCondTrabEspecif.class).getResultList();
		String cad =
			"select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajoEspecif> listaCondTrabEspec = new ArrayList<CondicionTrabajoEspecif>();

		listaCondTrabEspec =
			em.createNativeQuery(cad, CondicionTrabajoEspecif.class).getResultList();

		for (CondicionTrabajoEspecif o : listaCondTrabEspec) {
			ValoracionTab05CPT valor = new ValoracionTab05CPT();
			valor.setCondicionTrabajoEspecif(o);
			valor.setListaEscala(lista);
			listaValoracionLink9.add(valor);
		}
	}

	@SuppressWarnings("unchecked")
	public void buscarValoracionLink10() {
		String cadena =
			"select escala.* from planificacion.escala_cond_segur escala "
				+ " where escala.activo = TRUE " + " order by desde, hasta";
		List<EscalaCondSegur> lista = new ArrayList<EscalaCondSegur>();
		listaValoracionLink10 = new ArrayList<ValoracionTab06CPT>();
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
			listaValoracionLink10.add(valor);
		}
	}

	/*******************************************************
	 * Mï¿½todos para actualizar links
	 *********************************************************/
	public void actualizarLink1() {
		updatedLink1();

	}
	
	public void actualizarLink1PromocionSalarial() {
		updatedLink1PromocionSalarial();

	}

	/**
	 * Actualiza el Link1 Datos del Puesto
	 */
	@SuppressWarnings("unused")
	public void updatedLink1() {
		if (!checkLink1()) {
			return;
		}
		try {
			Long idCptOriginal = plantaCargoDetHome.getInstance().getCpt().getIdCpt();
			if (!idCpt.equals(idCptOriginal)) {
				plantaCargoDet.setCpt(em.find(Cpt.class, idCpt));
				eliminarTodoLink3();
				eliminarTodoLink5();
				eliminarTodoLink6();
				eliminarTodoLink7();
				eliminarTodoLink8();
				eliminarTodoLink9();
				eliminarTodoLink10();
			}
			if (idEspecialidad != null)
				plantaCargoDet.setEspecialidades(em.find(Especialidades.class, idEspecialidad));
			plantaCargoDet.setDescripcion(plantaCargoDet.getDescripcion().trim().toUpperCase());
//			plantaCargoDet.setFundamentacionTecnica(plantaCargoDet.getFundamentacionTecnica().trim().toUpperCase());
			if (plantaCargoDet.getMision() != null && !plantaCargoDet.getMision().equals(""))
				plantaCargoDet.setMision(plantaCargoDet.getMision().trim().toUpperCase());
			else
				plantaCargoDet.setMision(null);

			if (!checkJefatura()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Ya existe un cargo de Jefatura para la Unidad Organizativa seleccionada. Verificar.");
				return;
			}
			plantaCargoDet.setJefatura(jefatura);

			if (plantaCargoDet.getObjetivo() != null && !plantaCargoDet.getObjetivo().equals(""))
				plantaCargoDet.setObjetivo(plantaCargoDet.getObjetivo().trim().toUpperCase());
			else
				plantaCargoDet.setObjetivo(null);
			plantaCargoDetHome.setInstance(plantaCargoDet);
			String resultado = plantaCargoDetHome.update();
			mostrarPanel = "panel4";
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			obtenerUbicacionFisica();
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}
	
	
	@SuppressWarnings("unused")
	public void updatedLink1PromocionSalarial() {
		if (!checkPromocionSalarial()) {
			return;
		}
		try {
			Long idCptOriginal = promocionSalarial.getCpt().getIdCpt();
			if (!idCpt.equals(idCptOriginal)) {
				promocionSalarial.setCpt(em.find(Cpt.class, idCpt));
//				eliminarTodoLink3();
//				eliminarTodoLink5();
//				eliminarTodoLink6();
//				eliminarTodoLink7();
//				eliminarTodoLink8();
//				eliminarTodoLink9();
//				eliminarTodoLink10();
			}
			em.merge(promocionSalarial);
			em.flush();
			mostrarPanel = "panel1";
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
//			obtenerUbicacionFisica();
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	/**
	 * Actualiza el Link3 Valor Econï¿½mico
	 */
	public void updatedLink3() {

		try {
			Referencias referencias =
				referenciasUtilFormController.getReferencia("ESTADOS_CATEGORIA", "MODELO");
			for (PuestoConceptoPago p : listaLink3) {
				if (p.getIdPuestoConceptoPago() == null) {
					if (referencias != null)
						p.setEstado(referencias.getValorNum());
					p.setActivo(true);
					em.persist(p);
				}
			}
			em.flush();
			for (PuestoConceptoPago paux : listaLink3Aux) {
				Boolean esta = false;
				for (PuestoConceptoPago p : listaLink3) {
					if (p.getIdPuestoConceptoPago().equals(paux.getIdPuestoConceptoPago()))
						esta = true;
				}
				if (!esta) {
					em.remove(paux);
					em.flush();
				}
			}
			buscarDatosLink3();
			mostrarPanel = "panel4";
			mostrarTodosLinks = true;

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	/**
	 * Actualiza el Link4 Otros Datos
	 */
	public void updatedLink4() {
		guardarLink4();
		buscarDatosLink4();
		mostrarPanel = "panel8";
		mostrarTodosLinks = true;
		return;
	}

	/**
	 * Actualiza el Link5 Nombramiento
	 */
	public void updatedLink5() {
		try {
			for (TipoPlanta o : listaTipoPlanta) {
				List<TipoNombramiento> lista = new ArrayList<TipoNombramiento>();
				lista = o.getTipoNombramientos();
				for (DetTipoNombramiento tb1 : listaLink5Aux) {
					for (TipoNombramiento obj : lista) {
						if (tb1.getTipoNombramiento().getIdTipoNombramiento().equals(obj.getIdTipoNombramiento())) {
							if (obj.getSeleccionado()) {
								em.merge(tb1);
								em.flush();
							}
							if (!obj.getSeleccionado()) {
								em.remove(tb1);
								em.flush();
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
					for (DetTipoNombramiento tb1 : listaLink5Aux) {
						if (tb1.getTipoNombramiento().getIdTipoNombramiento().equals(obj.getIdTipoNombramiento()))
							esta = true;
					}
					if (!esta) {
						if (obj.getSeleccionado()) {
							DetTipoNombramiento det = new DetTipoNombramiento();
							det.setPlantaCargoDet(plantaCargoDetHome.getInstance());
							det.setTipoNombramiento(obj);
							det.setTipo("PUESTO");
							em.persist(det);
							em.flush();
						}
					}
				}
			}
			buscarNombramientosEdit();
			mostrarPanel = "panel6";
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	/**
	 * Actualiza el Link6 Contenido Funcional
	 */
	public void updatedLink6() {
		if (!validacionEscalaLink6())
			return;
		try {
			for (ContenidoFuncionalDTO dto : listaDtoLink6) {
				if (dto.getId() != null) {
					DetContenidoFuncional detContenido = new DetContenidoFuncional();
					detContenido = em.find(DetContenidoFuncional.class, dto.getId());
					detContenido.setPuntaje(dto.getPuntaje());

					detContenidoFuncionalHome.setInstance(detContenido);
					String resultado = detContenidoFuncionalHome.update();
					if (resultado.equals("updated")) {
						List<DetDescripcionContFuncional> listaDescripcion =
							new ArrayList<DetDescripcionContFuncional>();
						listaDescripcion = dto.getListaDetDescripContFuncional();
						for (DetDescripcionContFuncional desc : listaDescripcion) {
							if (desc.getDescripcion() != null && !desc.getDescripcion().equals("")) {
								desc.setDescripcion(desc.getDescripcion().trim().toUpperCase());
								desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
								em.persist(desc);
								em.flush();
							}
						}
					}
				} else {
					if (dto.getPuntaje() != null) {
						DetContenidoFuncional detContenido = new DetContenidoFuncional();
						detContenido.setContenidoFuncional(dto.getContenidoFuncional());
						detContenido.setPuntaje(dto.getPuntaje());
						detContenido.setTipo("PUESTO");
						detContenido.setActivo(true);
						detContenido.setPlantaCargoDet(plantaCargoDetHome.getInstance());
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
									desc.setDetContenidoFuncional(detContenidoFuncionalHome.getInstance());
									em.persist(desc);
								}
							}
							em.flush();
						}
					}
				}
				detContenidoFuncionalHome.clearInstance();
			}
			for (DetContenidoFuncional detail : listaLink6Aux) {
				List<DetDescripcionContFuncional> listaAuxDesc =
					new ArrayList<DetDescripcionContFuncional>();
				listaAuxDesc.addAll(detail.getDetDescripcionContFuncionals());
				for (ContenidoFuncionalDTO detailDTO : listaDtoLink6) {

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
								em.flush();
							}
						}
					}
				}
			}
			buscarContenidoFuncionalEdit();
			mostrarPanel = "panel7";
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	/**
	 * Actualiza el Link7 Requisitos Minimos
	 */
	public void updatedLink7() {

		if (!validacionEscalaLink7())
			return;
		try {
			for (RequisitosMinimosDTO dto : listaDtoLink7) {
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
						for (DetMinimosRequeridos dMin : listaMinimos) {
							if (dMin.getMinimosRequeridos() != null
								&& !dMin.getMinimosRequeridos().equals("")) {
								dMin.setMinimosRequeridos(dMin.getMinimosRequeridos().trim().toUpperCase());
								dMin.setDetReqMin(new DetReqMin());
								dMin.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());

								em.persist(dMin);
							}
						}
						em.flush();
						List<DetOpcionesConvenientes> listaOpc =
							new ArrayList<DetOpcionesConvenientes>();
						listaOpc = dto.getListaDetOpcionesConvenientes();
						for (DetOpcionesConvenientes detOp : listaOpc) {
							if (detOp.getOpcConvenientes() != null
								&& !detOp.getOpcConvenientes().equals("")) {

								detOp.setOpcConvenientes(detOp.getOpcConvenientes().trim().toUpperCase());
								detOp.setDetReqMin(new DetReqMin());
								detOp.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());

								em.persist(detOp);
							}
						}
						em.flush();
					}
				} else {
					if (dto.getPuntaje() != null) {
						DetReqMin detMin = new DetReqMin();
						detMin.setRequisitoMinimoCpt(dto.getRequisitoMinimoCpt());
						detMin.setPlantaCargoDet(plantaCargoDetHome.getInstance());
						detMin.setTipo("PUESTO");
						detMin.setPuntajeReqMin(dto.getPuntaje());
						detReqMinHome.setInstance(detMin);
						String resultado = detReqMinHome.persist();
						listaLink7Aux.add(detReqMinHome.getInstance());
						if (resultado.equals("persisted")) {
							List<DetMinimosRequeridos> listaDetMinimos =
								new ArrayList<DetMinimosRequeridos>();
							listaDetMinimos = dto.getListaDetMinimosRequeridos();
							for (DetMinimosRequeridos dMin : listaDetMinimos) {
								if (dMin.getMinimosRequeridos() != null
									&& !dMin.getMinimosRequeridos().trim().isEmpty()) {

									dMin.setMinimosRequeridos(dMin.getMinimosRequeridos().trim().toUpperCase());

									dMin.setDetReqMin(new DetReqMin());
									dMin.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());
									em.persist(dMin);
								}
							}
							em.flush();
							List<DetOpcionesConvenientes> listaDetOpc =
								new ArrayList<DetOpcionesConvenientes>();
							listaDetOpc = dto.getListaDetOpcionesConvenientes();
							for (DetOpcionesConvenientes detOpc : listaDetOpc) {
								if (detOpc.getOpcConvenientes() != null
									&& !detOpc.getOpcConvenientes().equals("")) {
									detOpc.setOpcConvenientes(detOpc.getOpcConvenientes().trim().toUpperCase());
									detOpc.setDetReqMin(new DetReqMin());
									detOpc.getDetReqMin().setIdDetReqMin(detReqMinHome.getInstance().getIdDetReqMin());
									em.persist(detOpc);
								}
							}
							em.flush();
						}
					}
					detReqMinHome.clearInstance();
				}
			}

			for (DetReqMin detail : listaLink7Aux) {
				List<DetOpcionesConvenientes> listaAuxOpc =
					new ArrayList<DetOpcionesConvenientes>();
				listaAuxOpc.addAll(detail.getDetOpcionesConvenienteses());
				List<DetMinimosRequeridos> listaAuxReqMin = new ArrayList<DetMinimosRequeridos>();
				listaAuxReqMin.addAll(detail.getDetMinimosRequeridoses());

				for (RequisitosMinimosDTO detailDTO : listaDtoLink7) {

					List<DetOpcionesConvenientes> listaDTOOpc =
						new ArrayList<DetOpcionesConvenientes>();
					listaDTOOpc.addAll(detailDTO.getListaDetOpcionesConvenientes());
					List<DetMinimosRequeridos> listaDTOReq = new ArrayList<DetMinimosRequeridos>();
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
								em.flush();
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
								em.flush();
							}
						}
					}
				}
			}
			mostrarPanel = "panel8";
			isNewLink8 = false;
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	public void updatedLink8() {
		if (!validacionEscalaLink8()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "El puntaje ingresado no forma parte de la escala");
			return;
		}
		try {
			for (CondicionTrabajoDTO dto : listaCondicionDtoLink8) {
				if (dto.getPuntaje() != null) {
					if (dto.getId() == null) {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab.setActivo(true);
						detCondTrab.setCondicionTrabajo(dto.getCondicionTrabajo());
						detCondTrab.setPlantaCargoDet(plantaCargoDetHome.getInstance());
						detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
						detCondTrab.setTipo("PUESTO");
						em.persist(detCondTrab);
					} else {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab = em.find(DetCondicionTrabajo.class, dto.getId());
						detCondTrab.setActivo(true);
						detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
						em.merge(detCondTrab);
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab = em.find(DetCondicionTrabajo.class, dto.getId());
						em.remove(detCondTrab);
					}
					else {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Debe completar todos los datos");
						return;
					}
				}
				em.flush();

			}
			buscarCondicionTrabajoEdit();
			mostrarPanel = "panel9";
			mostrarTodosLinks = true;
			isNewLink9 = false;
			plantaCargoDetFrom = "editar";
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	public void updatedLink9() {
		buscarValoracionLink9();
		if (!validacionEscalaLink9())
			return;
		try {
			for (CondicionTrabajoEspecifDTO dto : listaDtoLink9) {
				//ZD 04/11/15 -- VALIDAR JUSTIFICACIÓN SI EL PUNTAJE ES MAYOR A 7
				if(dto.getPuntaje() != null && dto.getPuntaje() > 6 && (dto.getJustificacion() == null || dto.getJustificacion().equals(""))){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Puntaje mayor a 7 se debe justificar!");
					return;
				}
				if (dto.getPuntaje() != null && dto.getAjustes() != null
					&& dto.getJustificacion() != null) {
					if (dto.getId() == null) {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab.setActivo(true);
						detCondTrab.setCondicionTrabajoEspecif(dto.getCondicionTrabajoEspecif());
						detCondTrab.setPlantaCargoDet(plantaCargoDetHome.getInstance());
						detCondTrab.setPuntajeCondTrabEspecif(dto.getPuntaje());
						detCondTrab.setTipo("PUESTO");
						detCondTrab.setAjustes(dto.getAjustes().trim().toUpperCase());
						detCondTrab.setJustificacion(dto.getJustificacion().trim().toUpperCase());
						em.persist(detCondTrab);
					} else {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab = em.find(DetCondicionTrabajoEspecif.class, dto.getId());
						detCondTrab.setActivo(true);
						detCondTrab.setPuntajeCondTrabEspecif(dto.getPuntaje());
						detCondTrab.setAjustes(dto.getAjustes().trim().toUpperCase());
						detCondTrab.setJustificacion(dto.getJustificacion().trim().toUpperCase());
						em.merge(detCondTrab);
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab = em.find(DetCondicionTrabajoEspecif.class, dto.getId());
						em.remove(detCondTrab);
					}
					else {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Debe completar todos los datos");
						return;
					}
				}
				em.flush();
			}
			buscarCondicionTrabajoEspecificaEdit();
			mostrarPanel = "panel10";
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	public void updatedLink10() {
		buscarValoracionLink10();
		if (!validacionEscalaLink10())
			return;
		try {
			for (CondicionSeguridadDTO dto : listaDtoLink10) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
					&& dto.getJustificacion() != null) {
					if (dto.getId() == null) {
						DetCondicionSegur detCondSeg = new DetCondicionSegur();
						detCondSeg.setActivo(true);
						detCondSeg.setCondicionSegur(dto.getCondicionSegur());
						detCondSeg.setPlantaCargoDet(plantaCargoDetHome.getInstance());
						detCondSeg.setPuntajeCondSegur(dto.getPuntaje());
						detCondSeg.setTipo("PUESTO");
						detCondSeg.setAjustes(dto.getAjustes().trim().toUpperCase());
						detCondSeg.setJustificacion(dto.getJustificacion().trim().toUpperCase());
						em.persist(detCondSeg);
					} else {
						DetCondicionSegur detCondSeg = new DetCondicionSegur();
						detCondSeg = em.find(DetCondicionSegur.class, dto.getId());
						detCondSeg.setActivo(true);
						detCondSeg.setPuntajeCondSegur(dto.getPuntaje());
						detCondSeg.setAjustes(dto.getAjustes().trim().toUpperCase());
						detCondSeg.setJustificacion(dto.getJustificacion().trim().toUpperCase());
						em.merge(detCondSeg);
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionSegur detCondSeg = new DetCondicionSegur();
						detCondSeg = em.find(DetCondicionSegur.class, dto.getId());
						em.remove(detCondSeg);
					}
					else {
						statusMessages.clear();
						statusMessages.add(Severity.ERROR, "Debe completar todos los datos");
						return;
					}
				}
				em.flush();

			}
			buscarDetCondicionSegurEdit();
			mostrarPanel = "panel1";
			mostrarTodosLinks = true;
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return;
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	/**
	 * Metodos que son llamados cuando si se cambia cpt
	 */
	private void eliminarTodoLink3() {
		if (listaLink3Aux.size() > 0) {
			for (PuestoConceptoPago c : listaLink3Aux) {
				em.remove(c);
			}
			em.flush();
			listaLink3Aux.clear();
		}
		if (listaLink3.size() > 0)
			listaLink3.clear();
		isNewLink3 = true;
	}

	private void eliminarTodoLink5() {
		if (listaLink5Aux.size() > 0) {
			for (DetTipoNombramiento nom : listaLink5Aux)
				em.remove(nom);
			em.flush();
			listaLink5Aux.clear();
		}
		buscarNombramientos();
		isNewLink5 = true;
	}

	private void eliminarTodoLink6() {
		if (listaLink6Aux.size() > 0) {
			for (DetContenidoFuncional c : listaLink6Aux) {
				List<DetDescripcionContFuncional> listaDesc =
					new ArrayList<DetDescripcionContFuncional>();
				listaDesc = c.getDetDescripcionContFuncionals();
				for (DetDescripcionContFuncional d : listaDesc)
					em.remove(d);
				em.remove(c);
				em.flush();
			}
			listaLink6Aux.clear();
		}
		buscarContenidoFuncional();
		isNewLink6 = true;
	}

	private void eliminarTodoLink7() {
		if (listaLink7Aux.size() > 0) {
			for (DetReqMin req : listaLink7Aux) {
				List<DetMinimosRequeridos> listaMin = new ArrayList<DetMinimosRequeridos>();
				listaMin = req.getDetMinimosRequeridoses();
				List<DetOpcionesConvenientes> listaOpc = new ArrayList<DetOpcionesConvenientes>();
				listaOpc = req.getDetOpcionesConvenienteses();
				for (DetMinimosRequeridos min : listaMin)
					em.remove(min);
				for (DetOpcionesConvenientes opc : listaOpc)
					em.remove(opc);
				em.remove(req);
				em.flush();
			}

			listaLink7Aux.clear();
		}
		buscarRequerimientosMinimos();
		isNewLink7 = true;
	}

	private void eliminarTodoLink8() {
		if (listaLink8Aux.size() > 0) {
			for (DetCondicionTrabajo t : listaLink8Aux)
				em.remove(t);
			em.flush();
			listaLink8Aux.clear();
		}
		buscarCondicionTrabajo();
		isNewLink8 = true;
	}

	private void eliminarTodoLink9() {
		if (listaLink9Aux.size() > 0) {
			for (DetCondicionTrabajoEspecif te : listaLink9Aux)
				em.remove(te);
			em.flush();
			listaLink9Aux.clear();
		}
		buscarCondicionTrabajoEspecifica();
		isNewLink9 = true;
	}

	private void eliminarTodoLink10() {
		if (listaLink10Aux.size() > 0) {
			for (DetCondicionSegur s : listaLink10Aux)
				em.remove(s);
			em.flush();
			listaLink10Aux.clear();
		}
		buscarDetCondicionSegur();
		isNewLink10 = true;
	}

	/**
	 * Metodos de uso general
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarOrden(Long id) {
		String cad =
			"select p.* from planificacion.planta_cargo_det p "
				+ "join planificacion.configuracion_uo_det det "
				+ "on det.id_configuracion_uo_det = p.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab cab "
				+ "on cab.id_configuracion_uo  = det. id_configuracion_uo  "
				+ "where cab.id_configuracion_uo = " + id + " order by p.orden desc";
		List<PlantaCargoDet> lista = new ArrayList<PlantaCargoDet>();
		lista = em.createNativeQuery(cad, PlantaCargoDet.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getOrden() + 1;
		return null;
	}

	/**
	 * Metodos de uso general
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarOrden2(Long id) {
		String cad =
			"select p.* from planificacion.planta_cargo_det p "
				+ " where  p.id_configuracion_uo_det = " + id + " order by p.orden desc";
		List<PlantaCargoDet> lista = new ArrayList<PlantaCargoDet>();
		lista = em.createNativeQuery(cad, PlantaCargoDet.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getOrden() + 1;
		return null;
	}

	/**
	 * @param horaCad
	 * @return
	 */
	private int[] getHora(String horaCad) {
		String[] horas = horaCad.split(":");
		if (horas.length != 2) {
			return null;
		} else {
			String hora = horas[0];
			String minuto = horas[1];
			try {
				int hh = Integer.parseInt(hora);
				int mm = Integer.parseInt(minuto);

				if (hh < 0 || hh > 23 || mm < 0 || mm >= 60) {
					return null;
				}
				int[] v = new int[2];
				v[0] = hh;
				v[1] = mm;
				return v;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public void obtenerCodigoCpt() {
	 if(idCpt!=null){
		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCpt);

		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();
	 }

	}

	private void buscarCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCptFromList);
		descripcionCpt = cptActual.getDenominacion();
		idCpt = idCptFromList;
		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}

	private void recuperarCodigoOeeDep(String cad) {
		String[] arrayCodigo = cad.split("\\.");
		ordenOEEDep = "";
		codigoUnidOrgDep = "";
		for (int i = 0; i < arrayCodigo.length; i++) {
			if (i == 0)
				ordenOEEDep += arrayCodigo[i] + ".";
			if (i == 1)
				ordenOEEDep += arrayCodigo[i] + ".";
			if (i == 2)
				ordenOEEDep += arrayCodigo[i];
			if (i > 2)
				codigoUnidOrgDep += arrayCodigo[i] + ".";

		}
		codigoUnidOrgDep = codigoUnidOrgDep.substring(0, codigoUnidOrgDep.length() - 1);
	}

	/**
	 * Fusion de Puestos
	 */

	public void mostrarModalMotivo() {
		if (motivoRadioButton.equals("f"))
			mostrar("panelx");
		else
			mostrar("panel1");

		codigoUnidOrgDep = null;
		configuracionUoDet = new ConfiguracionUoDet();

	}

	public void traerDatos() {
		puestosSinFusionar = new ArrayList<PlantaCargoDet>();
		puestosSinFusionar = traerPuestos();
	}

	@SuppressWarnings("unchecked")
	private List<PlantaCargoDet> traerPuestos() {
		try {
			String sql = " select o from " + PlantaCargoDet.class.getName() + " o ";
			String where = " where o.activo is true ";
			if (configuracionUoDet != null && configuracionUoDet.getIdConfiguracionUoDet() != null) {
				sql += " join o.configuracionUoDet uo ";
				where +=
					" and uo.idConfiguracionUoDet = "
						+ configuracionUoDet.getIdConfiguracionUoDet();
			}
			sql += where;
			sql += " order by o.descripcion";
			return em.createQuery(sql).getResultList();
		} catch (Exception ex) {
			return new Vector<PlantaCargoDet>();
		}
	}

	public void guardarMemoriaFusion() {
		mostrar("panel1");
		motivoRadioButton = "n";
	}

	public void storeItemInList() {
		;
	}

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */
	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public EstadoCab getEstadoCab() {
		return estadoCab;
	}

	public void setEstadoCab(EstadoCab estadoCab) {
		this.estadoCab = estadoCab;
	}

	public String getMostrarPanel() {
		return mostrarPanel;
	}

	public void setMostrarPanel(String mostrarPanel) {
		this.mostrarPanel = mostrarPanel;
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

	/*
	 * public Entidad getEntidad() { return entidad; }
	 * 
	 * public void setEntidad(Entidad entidad) { this.entidad = entidad; }
	 */

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public String getCodigoCpt() {
		return codigoCpt;
	}

	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}

	public List<SelectItem> getCptSelecItem() {
		return cptSelecItem;
	}

	public void setCptSelecItem(List<SelectItem> cptSelecItem) {
		this.cptSelecItem = cptSelecItem;
	}

	public Long getIdCpt() {
		return idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;

	}

	public String getModalidadOcupLink1() {
		return modalidadOcupLink1;
	}

	public void setModalidadOcupLink1(String modalidadOcupLink1) {
		this.modalidadOcupLink1 = modalidadOcupLink1;
	}

	public Long getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(Long idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}

	public String getCodigoPuestoLink1() {
		return codigoPuestoLink1;
	}

	public void setCodigoPuestoLink1(String codigoPuestoLink1) {
		this.codigoPuestoLink1 = codigoPuestoLink1;
	}

	public Boolean getTieneUsuario() {
		return tieneUsuario;
	}

	public void setTieneUsuario(Boolean tieneUsuario) {
		this.tieneUsuario = tieneUsuario;
	}

	public String getCodigoCategoria() {
		return codigoCategoria;
	}

	public void setCodigoCategoria(String codigoCategoria) {
		this.codigoCategoria = codigoCategoria;
	}

	public Integer getCodigoObj() {
		return codigoObj;
	}

	public void setCodigoObj(Integer codigoObj) {
		this.codigoObj = codigoObj;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public List<PuestoConceptoPago> getListaPuestoConceptoPago() {
		return listaPuestoConceptoPago;
	}

	public void setListaPuestoConceptoPago(List<PuestoConceptoPago> listaPuestoConceptoPago) {
		this.listaPuestoConceptoPago = listaPuestoConceptoPago;
	}

	public String getValorObj() {
		return valorObj;
	}

	public void setValorObj(String valorObj) {
		this.valorObj = valorObj;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getHoraDesdeLunes() {
		return horaDesdeLunes;
	}

	public void setHoraDesdeLunes(String horaDesdeLunes) {
		this.horaDesdeLunes = horaDesdeLunes;
	}

	public String getHoraHastaLunes() {
		return horaHastaLunes;
	}

	public void setHoraHastaLunes(String horaHastaLunes) {
		this.horaHastaLunes = horaHastaLunes;
	}

	public String getHoraDesdeMartes() {
		return horaDesdeMartes;
	}

	public void setHoraDesdeMartes(String horaDesdeMartes) {
		this.horaDesdeMartes = horaDesdeMartes;
	}

	public String getHoraHastaMartes() {
		return horaHastaMartes;
	}

	public void setHoraHastaMartes(String horaHastaMartes) {
		this.horaHastaMartes = horaHastaMartes;
	}

	public String getHoraDesdeMiercoles() {
		return horaDesdeMiercoles;
	}

	public void setHoraDesdeMiercoles(String horaDesdeMiercoles) {
		this.horaDesdeMiercoles = horaDesdeMiercoles;
	}

	public String getHoraHastaMiercoles() {
		return horaHastaMiercoles;
	}

	public void setHoraHastaMiercoles(String horaHastaMiercoles) {
		this.horaHastaMiercoles = horaHastaMiercoles;
	}

	public String getHoraDesdeJueves() {
		return horaDesdeJueves;
	}

	public void setHoraDesdeJueves(String horaDesdeJueves) {
		this.horaDesdeJueves = horaDesdeJueves;
	}

	public String getHoraHastaJueves() {
		return horaHastaJueves;
	}

	public void setHoraHastaJueves(String horaHastaJueves) {
		this.horaHastaJueves = horaHastaJueves;
	}

	public String getHoraDesdeViernes() {
		return horaDesdeViernes;
	}

	public void setHoraDesdeViernes(String horaDesdeViernes) {
		this.horaDesdeViernes = horaDesdeViernes;
	}

	public String getHoraHastaViernes() {
		return horaHastaViernes;
	}

	public void setHoraHastaViernes(String horaHastaViernes) {
		this.horaHastaViernes = horaHastaViernes;
	}

	public String getHoraDesdeSabado() {
		return horaDesdeSabado;
	}

	public void setHoraDesdeSabado(String horaDesdeSabado) {
		this.horaDesdeSabado = horaDesdeSabado;
	}

	public String getHoraHastaSabado() {
		return horaHastaSabado;
	}

	public void setHoraHastaSabado(String horaHastaSabado) {
		this.horaHastaSabado = horaHastaSabado;
	}

	public String getHoraDesdeDomingo() {
		return horaDesdeDomingo;
	}

	public void setHoraDesdeDomingo(String horaDesdeDomingo) {
		this.horaDesdeDomingo = horaDesdeDomingo;
	}

	public String getHoraHastaDomingo() {
		return horaHastaDomingo;
	}

	public void setHoraHastaDomingo(String horaHastaDomingo) {
		this.horaHastaDomingo = horaHastaDomingo;
	}

	public Long getIdOficina() {
		return idOficina;
	}

	public void setIdOficina(Long idOficina) {
		this.idOficina = idOficina;
	}

	public List<TipoPlanta> getListaTipoPlanta() {
		return listaTipoPlanta;
	}

	public void setListaTipoPlanta(List<TipoPlanta> listaTipoPlanta) {
		this.listaTipoPlanta = listaTipoPlanta;
	}

	public Boolean getIsNewLink3() {
		return isNewLink3;
	}

	public void setIsNewLink3(Boolean isNewLink3) {
		this.isNewLink3 = isNewLink3;
	}

	public Boolean getIsNewLink5() {
		return isNewLink5;
	}

	public void setIsNewLink5(Boolean isNewLink5) {
		this.isNewLink5 = isNewLink5;
	}

	public Boolean getIsNewLink6() {
		return isNewLink6;
	}

	public void setIsNewLink6(Boolean isNewLink6) {
		this.isNewLink6 = isNewLink6;
	}

	public Boolean getIsNewLink1() {
		return isNewLink1;
	}

	public void setIsNewLink1(Boolean isNewLink1) {
		this.isNewLink1 = isNewLink1;
	}

	public List<ContenidoFuncional> getListaValoracionTab2() {
		return listaValoracionTab2;
	}

	public void setListaValoracionTab2(List<ContenidoFuncional> listaValoracionTab2) {
		this.listaValoracionTab2 = listaValoracionTab2;
	}

	public String getMensajeLink06() {
		return mensajeLink06;
	}

	public void setMensajeLink06(String mensajeLink06) {
		this.mensajeLink06 = mensajeLink06;
	}

	public List<ContenidoFuncionalDTO> getListaDtoLink6() {
		return listaDtoLink6;
	}

	public void setListaDtoLink6(List<ContenidoFuncionalDTO> listaDtoLink6) {
		this.listaDtoLink6 = listaDtoLink6;
	}

	public List<RequisitoMinimoCpt> getListaValoracionTab3() {
		return listaValoracionTab3;
	}

	public void setListaValoracionTab3(List<RequisitoMinimoCpt> listaValoracionTab3) {
		this.listaValoracionTab3 = listaValoracionTab3;
	}

	public Boolean getIsNewLink7() {
		return isNewLink7;
	}

	public void setIsNewLink7(Boolean isNewLink7) {
		this.isNewLink7 = isNewLink7;
	}

	public Boolean getIsNewLink8() {
		return isNewLink8;
	}

	public void setIsNewLink8(Boolean isNewLink8) {
		this.isNewLink8 = isNewLink8;
	}

	public Boolean getIsNewLink9() {
		return isNewLink9;
	}

	public void setIsNewLink9(Boolean isNewLink9) {
		this.isNewLink9 = isNewLink9;
	}

	public Boolean getIsNewLink10() {
		return isNewLink10;
	}

	public void setIsNewLink10(Boolean isNewLink10) {
		this.isNewLink10 = isNewLink10;
	}

	public String getMensajeLink07() {
		return mensajeLink07;
	}

	public void setMensajeLink07(String mensajeLink07) {
		this.mensajeLink07 = mensajeLink07;
	}

	public List<RequisitosMinimosDTO> getListaDtoLink7() {
		return listaDtoLink7;
	}

	public void setListaDtoLink7(List<RequisitosMinimosDTO> listaDtoLink7) {
		this.listaDtoLink7 = listaDtoLink7;
	}

	public List<CondicionTrabajo> getListaValoracionLink8() {
		return listaValoracionLink8;
	}

	public void setListaValoracionLink8(List<CondicionTrabajo> listaValoracionLink8) {
		this.listaValoracionLink8 = listaValoracionLink8;
	}

	public String getMensajeLink08() {
		return mensajeLink08;
	}

	public void setMensajeLink08(String mensajeLink08) {
		this.mensajeLink08 = mensajeLink08;
	}

	public List<CondicionTrabajoDTO> getListaCondicionDtoLink8() {
		return listaCondicionDtoLink8;
	}

	public void setListaCondicionDtoLink8(List<CondicionTrabajoDTO> listaCondicionDtoLink8) {
		this.listaCondicionDtoLink8 = listaCondicionDtoLink8;
	}

	public List<ValoracionTab05CPT> getListaValoracionLink9() {
		return listaValoracionLink9;
	}

	public void setListaValoracionLink9(List<ValoracionTab05CPT> listaValoracionLink9) {
		this.listaValoracionLink9 = listaValoracionLink9;
	}

	public String getMensajeLink09() {
		return mensajeLink09;
	}

	public void setMensajeLink09(String mensajeLink09) {
		this.mensajeLink09 = mensajeLink09;
	}

	public List<CondicionTrabajoEspecifDTO> getListaDtoLink9() {
		return listaDtoLink9;
	}

	public void setListaDtoLink9(List<CondicionTrabajoEspecifDTO> listaDtoLink9) {
		this.listaDtoLink9 = listaDtoLink9;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public List<ValoracionTab06CPT> getListaValoracionLink10() {
		return listaValoracionLink10;
	}

	public void setListaValoracionLink10(List<ValoracionTab06CPT> listaValoracionLink10) {
		this.listaValoracionLink10 = listaValoracionLink10;
	}

	public String getMensajeLink10() {
		return mensajeLink10;
	}

	public void setMensajeLink10(String mensajeLink10) {
		this.mensajeLink10 = mensajeLink10;
	}

	public List<CondicionSeguridadDTO> getListaDtoLink10() {
		return listaDtoLink10;
	}

	public void setListaDtoLink10(List<CondicionSeguridadDTO> listaDtoLink10) {
		this.listaDtoLink10 = listaDtoLink10;
	}

	public String getMensajeLink03() {
		return mensajeLink03;
	}

	public void setMensajeLink03(String mensajeLink03) {
		this.mensajeLink03 = mensajeLink03;
	}

	public List<PuestoConceptoPago> getListaLink3() {
		return listaLink3;
	}

	public void setListaLink3(List<PuestoConceptoPago> listaLink3) {
		this.listaLink3 = listaLink3;
	}
	//radioButton, se habilitï¿½ de vuelta; Werner.
	public String getRadioButton() {
		return radioButton;
	}

	public void setRadioButton(String radioButton) {
		this.radioButton = radioButton;
	}

	public Integer getCantPuestosReplicados() {
		return cantPuestosReplicados;
	}

	public void setCantPuestosReplicados(Integer cantPuestosReplicados) {
		this.cantPuestosReplicados = cantPuestosReplicados;
	}

	public List<PuestoConceptoPago> getListaLink3Aux() {
		return listaLink3Aux;
	}

	public void setListaLink3Aux(List<PuestoConceptoPago> listaLink3Aux) {
		this.listaLink3Aux = listaLink3Aux;
	}

	public List<DetTipoNombramiento> getListaLink5Aux() {
		return listaLink5Aux;
	}

	public void setListaLink5Aux(List<DetTipoNombramiento> listaLink5Aux) {
		this.listaLink5Aux = listaLink5Aux;
	}

	public List<DetContenidoFuncional> getListaLink6Aux() {
		return listaLink6Aux;
	}

	public void setListaLink6Aux(List<DetContenidoFuncional> listaLink6Aux) {
		this.listaLink6Aux = listaLink6Aux;
	}

	public List<DetReqMin> getListaLink7Aux() {
		return listaLink7Aux;
	}

	public void setListaLink7Aux(List<DetReqMin> listaLink7Aux) {
		this.listaLink7Aux = listaLink7Aux;
	}

	public List<DetCondicionTrabajo> getListaLink8Aux() {
		return listaLink8Aux;
	}

	public void setListaLink8Aux(List<DetCondicionTrabajo> listaLink8Aux) {
		this.listaLink8Aux = listaLink8Aux;
	}

	public List<DetCondicionTrabajoEspecif> getListaLink9Aux() {
		return listaLink9Aux;
	}

	public void setListaLink9Aux(List<DetCondicionTrabajoEspecif> listaLink9Aux) {
		this.listaLink9Aux = listaLink9Aux;
	}

	public List<DetCondicionSegur> getListaLink10Aux() {
		return listaLink10Aux;
	}

	public void setListaLink10Aux(List<DetCondicionSegur> listaLink10Aux) {
		this.listaLink10Aux = listaLink10Aux;
	}

	public SinAnx getSinAnx() {
		return sinAnx;
	}

	public void setSinAnx(SinAnx sinAnx) {
		this.sinAnx = sinAnx;

	}

	public Long getIdSinAnx() {
		return idSinAnx;
	}

	public void setIdSinAnx(Long idSinAnx) {
		this.idSinAnx = idSinAnx;
		if (idSinAnx != null) {
			mostrarPanel = "panel3";
			sinAnx = em.find(SinAnx.class, idSinAnx);
			if (sinAnx != null) {
				categoria = sinAnx.getAnxDescrip();
				codigoCategoria = sinAnx.getCtgCodigo();
				findCategoria();
				monto = null;
				buscarMonto(sinAnx.getCatGrupo());
			}
		}
	}

	public Long getIdBusqUnOrgDep() {
		return idBusqUnOrgDep;
	}

	public void setIdBusqUnOrgDep(Long idBusqUnOrgDep) {
		this.idBusqUnOrgDep = idBusqUnOrgDep;
		if (idBusqUnOrgDep != null) {
			configuracionUoDet = em.find(ConfiguracionUoDet.class, idBusqUnOrgDep);
			buscarOrdenSugerido();
			String codResul = obtenerCodigo(configuracionUoDet);
			recuperarCodigoOeeDep(codResul);
			if (modalidadOcupLink1 != null)
				obtenerCodigo();
		}
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Integer getOrdenPuesto() {
		return ordenPuesto;
	}

	public void setOrdenPuesto(Integer ordenPuesto) {
		this.ordenPuesto = ordenPuesto;
	}

	public Long getIdCptFromList() {
		return idCptFromList;
	}

	public void setIdCptFromList(Long idCptFromList) {
		this.idCptFromList = idCptFromList;
		if (idCptFromList != null)
			buscarCodigoCpt();
	}

	public Boolean getMostrarTodosLinks() {
		return mostrarTodosLinks;
	}

	public void setMostrarTodosLinks(Boolean mostrarTodosLinks) {
		this.mostrarTodosLinks = mostrarTodosLinks;
	}

	public String getOrdenOEEDep() {
		return ordenOEEDep;
	}

	public void setOrdenOEEDep(String ordenOEEDep) {
		this.ordenOEEDep = ordenOEEDep;
	}

	public String getDescripcionCpt() {
		return descripcionCpt;
	}

	public void setDescripcionCpt(String descripcionCpt) {
		this.descripcionCpt = descripcionCpt;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getMotivoRadioButton() {
		return motivoRadioButton;
	}

	public void setMotivoRadioButton(String motivoRadioButton) {
		this.motivoRadioButton = motivoRadioButton;
	}

	public List<PlantaCargoDet> getPuestosSinFusionar() {
		return puestosSinFusionar;
	}

	public void setPuestosSinFusionar(List<PlantaCargoDet> puestosSinFusionar) {
		this.puestosSinFusionar = puestosSinFusionar;
	}

	public List<PlantaCargoDet> getPuestosFusionados() {
		return puestosFusionados;
	}

	public void setPuestosFusionados(List<PlantaCargoDet> puestosFusionados) {
		this.puestosFusionados = puestosFusionados;
	}

	public String getPlantaCargoDetFrom() {
		return plantaCargoDetFrom;
	}

	public void setPlantaCargoDetFrom(String plantaCargoDetFrom) {
		this.plantaCargoDetFrom = plantaCargoDetFrom;
	}

	public String getPlantaFrom() {
		return plantaFrom;
	}

	public void setPlantaFrom(String plantaFrom) {
		this.plantaFrom = plantaFrom;
	}

	public void setJefatura(Boolean jefatura) {
		this.jefatura = jefatura;
	}

	public Boolean getJefatura() {
		return jefatura;
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	public Boolean getIsNewLink11() {
		return isNewLink11;
	}

	public void setIsNewLink11(Boolean isNewLink11) {
		this.isNewLink11 = isNewLink11;
	}

	public Long getIdCptPadre() {
		return idCptPadre;
	}

	public void setIdCptPadre(Long idCptPadre) {
		this.idCptPadre = idCptPadre;
	}
	public List<String> getListaTabla(){
		return listaTabla;
	} 
	public void setListaTabla(List<String> listaTabla){
		this.listaTabla = listaTabla;
	}

	public List<String> getListaTablaTipo() {
		return listaTablaTipo;
	}

	public void setListaTablaTipo(List<String> listaTablaTipo) {
		this.listaTablaTipo = listaTablaTipo;
	}

	public List<NivelesDeCargos> getListaNivelesDeCargos() {
		return listaNivelesDeCargos;
	}

	public void setListaNivelesDeCargos(List<NivelesDeCargos> listaNivelesDeCargos) {
		this.listaNivelesDeCargos = listaNivelesDeCargos;
	}
	
	public boolean mostrarReplicarPuesto() {
		boolean condTrabajoCompletado = (listaCondicionDtoLink8.size()>0);
		for (CondicionTrabajoDTO dto : listaCondicionDtoLink8) {
			if (dto.getPuntaje() == null) {
				condTrabajoCompletado = false;
				break;
			}
		}
		boolean condTrabajoEspCompletado = (listaDtoLink9.size()>0);
		for (CondicionTrabajoEspecifDTO dto : listaDtoLink9) {
			if (dto.getPuntaje() == null || dto.getAjustes() == null
				|| dto.getJustificacion() == null) {
					condTrabajoEspCompletado = false;
					break;
				}
		}
		boolean condSeguridadCompletado = (listaDtoLink10.size()>0);
		for (CondicionSeguridadDTO dto : listaDtoLink10) {
			if (dto.getPuntaje() == null || dto.getAjustes() == null
				|| dto.getJustificacion() == null) {
				condSeguridadCompletado = false;
				break;
			}
		}
		return  (mostrarTodosLinks			&&
				condSeguridadCompletado 	&&
				condTrabajoCompletado 		&&
				condTrabajoEspCompletado)	||
				this.plantaCargoDet.validarCondiciones().equals("Ok");
	}
	
	public Boolean debug(){
		return true;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	/**
	 * Asignar Categoria a concursos seleccionados
	 * 
	 * @return
	 
	 
	 href="
	  plantaCargoDetFrom=seleccion/concursoPuestoDet/ConcursoPuestoDetList

	  concursoIdConcurso=
	  fromConcurso=/seleccion/concurso/ConcursoList.xhtml
	 plantaCargoDetIdPlantaCargoDet=83
	 nroActividad=1
	 * 
	 * 
	 */
	
	
			
	public String getUrlToAsignarCategoria() {
		return "/seleccion/asignarCategoria/AsignarCategoria.xhtml?"
				+"fromConcurso=/seleccion/concurso/ConcursoList.xhtml"
			+ "&plantaCargoDetIdPlantaCargoDet="+plantaCargoDet.getIdPlantaCargoDet()
			+ "&nroActividad="
			+ nivelEntidad.getIdSinNivelEntidad()
			+ "&idConfiguracionUoCab="
			+ configuracionUoCab.getIdConfiguracionUo();
	}
	public String getUrlToPageUnidadOrgDesdfp() {
		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=planificacion/puestosTrabajo/PlantaCargoDetEdit&sinEntidadIdSinEntidad="
			+ sinEntidad.getIdSinEntidad()
			+ "&idNivelEntidad="
			+ nivelEntidad.getIdSinNivelEntidad()
			+ "&idConfiguracionUoCab="
			+ configuracionUoCab.getIdConfiguracionUo();
	}

	public PromocionSalarial getPromocionSalarial() {
		return promocionSalarial;
	}

	public void setPromocionSalarial(PromocionSalarial promocionSalarial) {
		this.promocionSalarial = promocionSalarial;
	}

	public Long getIdPromocionSalarial() {
		return idPromocionSalarial;
	}

	public void setIdPromocionSalarial(Long idPromocionSalarial) {
		this.idPromocionSalarial = idPromocionSalarial;
	}

	public boolean isEsHomologador() {
		return esHomologador;
	}

	public void setEsHomologador(boolean esHomologador) {
		this.esHomologador = esHomologador;
	}
}