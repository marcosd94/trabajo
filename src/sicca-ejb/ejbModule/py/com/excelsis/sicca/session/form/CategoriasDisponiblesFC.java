package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.arjuna.ats.internal.jta.resources.errorhandlers.tibco;

import enums.TiposDatos;

import py.com.excelsis.sicca.dto.CatAnxDto;
import py.com.excelsis.sicca.entity.CategoriaCpt;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoria;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("categoriasDisponiblesFC")
public class CategoriasDisponiblesFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();

	List<CatAnxDto> listaCategorias = new ArrayList<CatAnxDto>();

	private Long idPlantaCargo;
	private Long idAgr;
	private BigDecimal monto;
	private String codigoSinarh;
	private String categoriaSearch;
	private String codigoSearch;
	private String tipo;//si tipo=1(objeto gasto 111) si tipo=2 objetoGasto !=111
	private String categoria;
	private String nombreConcurso;
	private String nombreGrupo;
	private String ctgCodigo;
	private Integer cntGrupo;
	List<ConcursoPuestoDet> puestoDets= new ArrayList<ConcursoPuestoDet>();
	private String objGasto;
	List<GrupoConceptoPago> grupoConceptoPagoList= new ArrayList<GrupoConceptoPago>();
	private BigDecimal montoCarga;
	private Long idSinAuxCarga;
	private String from; 
	private boolean habSeleccionado;
	private Integer cntRetanteADescontar=null;
	private int indexSelec;
	private Boolean habilitarSeleccionar;
	

	/**
	 * Método que inicializa los datos
	 * @throws Exception 
	 */
	public void init() throws Exception {
		if (!seguridadUtilFormController.validarInput(idAgr.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		if (!seguridadUtilFormController.validarInput(tipo.toString(), TiposDatos.STRING.getValor(), null)) {
			return ;
		}
		habilitarSeleccionar = true;
		limpiarDatos();
		cargarCabecera();
		configuracionUoCab=em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		codigoSinarh = configuracionUoCab.getCodigoSinarh();
		extraerCategoria();
		buscarCategorias();
		gruposActivos();
		cntRetanteADescontar=null;
		statusMessages.clear();
		String mensaje = cantidadDisponible();
		if(!mensaje.trim().isEmpty()){
			habilitarSeleccionar = false;
			statusMessages.add(Severity.ERROR, "'No hay stock suficiente en el sinarh para el/los "+mensaje+" pero no es obligatorio para pasar de Tarea");
		}
		
	}
	
	private String cantidadDisponible(){
		String msg = "";
		
		for (CatAnxDto dto : listaCategorias) {
			if(dto.getCntDisponible().intValue() < cntGrupo.intValue())
				msg += " Objeto de Gasto "+dto.getObjCodigo()+" y Categoria "+dto.getCtgCodigo()+", Stock disponible = "+dto.getCntDisponible()+"; ";
		}
		return msg;
	}
	
	private void limpiarDatos(){
		listaCategorias=new Vector<CatAnxDto>();
		habSeleccionado=false;
		cntRetanteADescontar=null;
	}
	
	/**
	 * SE BUSCA DE LA TABLA GrupoConceptoPago DE ACUERDO A EL TIPO SI ES 1 EL OBJETO ES 111 SINO ES !=111
	 * SE OBTIENEN LAS CATEGORIAS , LOS OBEJETOS PARA EL CASO DE TIPO2
	 *  * */
	@SuppressWarnings("unchecked")
	private void extraerCategoria(){
		categoria="";
		objGasto="";
		String query= " Select cp from GrupoConceptoPago cp " +
		" where cp.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and cp.estado=:estado " ;
		if(tipo.equals("1")){
			query+=" and cp.objCodigo= 111";
			objGasto="111";
		}else{
			query+=" and cp.objCodigo != 111";
		}
		 grupoConceptoPagoList=em.createQuery(query).setParameter("idGrupo", idAgr)
		.setParameter("estado", seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO","PENDIENTE")).getResultList();
		 for (GrupoConceptoPago gcpagos :grupoConceptoPagoList) {
			 if(categoria.trim().equals("")){
				 categoria=gcpagos.getCategoria();
				 if(tipo.equals("2")){
					 if(!existeReservado(gcpagos.getObjCodigo())){
						 if(objGasto.equals(""))
							 objGasto=gcpagos.getObjCodigo()+"";
						 else
							 objGasto+=","+gcpagos.getObjCodigo();
					 }
					
					
				 }
					 
			 }else{
				 categoria+="."+gcpagos.getCategoria();
				 if(tipo.equals("2")){
					 if(!existeReservado(gcpagos.getObjCodigo())){
						 if(objGasto.equals(""))
							 objGasto=gcpagos.getObjCodigo()+"";
						 else
							 objGasto+=","+gcpagos.getObjCodigo();
					 }
					
				 }
			 }
				 
		}
		 
		
			
		
		
	}
	/**
	 * 	SE VERIFICA QUE NO EXISTA  un registro en esta tabla con estado = ‘RESERVADO’
	 * 
	 * */
	@SuppressWarnings("unchecked")
	private boolean existeReservado(Integer objG){
		String query= " Select cp from GrupoConceptoPago cp " +
		" where cp.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo and cp.estado=:estado and cp.objCodigo= "+objG ;
		List<GrupoConceptoPago> conceptoPagos= em.createQuery(query).setParameter("idGrupo", idAgr)
		.setParameter("estado",seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO","RESERVADO") ).getResultList();
		return !conceptoPagos.isEmpty();
	}
	/**
	 * TRAE TODOS LOS ACTIVOS DEL GRUPO QUE SE MANDO COMO PAREMETRO
	 * */
	@SuppressWarnings("unchecked")
	private void gruposActivos(){
		puestoDets=em.createQuery("Select d from ConcursoPuestoDet d " +
				" where d.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo" +
				" and d.activo=true").setParameter("idGrupo", idAgr).getResultList();
		cntGrupo=puestoDets.size();
	}
	

	public void cargarCabecera(){
		concursoPuestoAgr=em.find(ConcursoPuestoAgr.class,idAgr);
		nombreGrupo=concursoPuestoAgr.getDescripcionGrupo();
		concurso= em.find(Concurso.class, concursoPuestoAgr.getConcurso().getIdConcurso());
		nombreConcurso=concurso.getNombre();
		nivelEntidadOeeUtil.setIdConfigCab(concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
			
	}
	public boolean habMensaje(String cat){
		if(categoria.contains(cat))
			return true;
		
		return false;
	}
	public boolean habLinkSeleccionado(Long cnt){
		/**
		 * SI SUM(cant_disponible) de la tabla SIN_ANX es < a la cantidad de puestos activos 
		 * del grupo, el enlace ‘seleccionar’ se encuentra deshabilitado
		 * */
		if(cnt.intValue()< cntGrupo.intValue())
			return false;
	
		return true;
	}

	

	@SuppressWarnings("unchecked")
	private void buscarCategorias() {
		if (codigoSinarh != null) {
			String[] sinarh = codigoSinarh.split("\\/");
			

			Integer vrs_codigo = new Integer(50);

			String cadena = " select anx.obj_codigo, obj.obj_nombre, anx.ctg_codigo, SUM(anx.cant_disponible) as cnt" +
					" from sinarh.sin_anx anx join sinarh.sin_obj obj on  ( anx.obj_codigo = obj.obj_codigo and obj.ani_aniopre=anx.ani_aniopre) ";
			String where=" where (anx.nen_codigo||'.'||anx.ent_codigo||'.'||anx.tip_codigo||'.'||anx.pro_codigo) in ('";
			for (int i = 0; i < sinarh.length; i++) {
				where += sinarh[i];
				if (i < sinarh.length - 1)
					where += "', '";
				else
					where += "')";
			}
			
			
				
			where+= " and anx.ani_aniopre  = " + concurso.getAnhio()+ " and anx.vrs_codigo = "+vrs_codigo+" " +
					"and anx.ctg_codigo in ('"+categoria+"')";
			
			cadena+=where;
			
			if(!objGasto.trim().equals(""))
					cadena+= " and anx.obj_codigo in("+objGasto+")";

			if (codigoSearch != null && !codigoSearch.trim().isEmpty())
				cadena = cadena + " and anx.ctg_codigo = '" + codigoSearch
						+ "'";

			if (categoriaSearch != null && !categoriaSearch.trim().isEmpty())
				cadena = cadena + " and anx.anx_descrip = '" + categoriaSearch
						+ "'";
			cadena+=" GROUP BY anx.obj_codigo, obj.obj_nombre,anx.ctg_codigo " +
					" ORDER BY anx.obj_codigo, anx.ctg_codigo ";
			
			listaCategorias = new ArrayList<CatAnxDto>();
			Iterator<Object[]> resultado = em.createNativeQuery(cadena)
			.getResultList().iterator();
			for (; resultado.hasNext();) {
				Object[] fila = resultado.next();
				CatAnxDto aux= new CatAnxDto();
				if(fila[0]!=null)
					aux.setObjCodigo(Integer.parseInt(fila[0].toString()));
				if(fila[1]!=null)
					aux.setObjNombre(fila[1].toString());
				if(fila[2]!= null)
					aux.setCtgCodigo(fila[2].toString());
				if(fila[3]!=null)
					aux.setCntDisponible(Long.parseLong(fila[3].toString()));
				
				listaCategorias.add(aux);
					
			}
			
			
		}
	}

	public String seleccionar(int index){
		try {
			CatAnxDto anxDto=listaCategorias.get(index);
			
			System.out.println(index);
			seleccionar2(null, anxDto.getObjCodigo(), anxDto.getCtgCodigo());
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "/seleccion/validarCategoriaAdReferendum/ValidarCategorias.seam?concursoPuestoAgrIdConcursoPuestoAgr="+idAgr;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Se descuenta de la tabla sin_anx columna cant_disponible. 
	 * si desconto todo retorna true sino false   
	 * */
	private boolean descontarStock(SinAnx sinAnx){
		if(sinAnx.getCantDisponible()!=null && sinAnx.getCantDisponible().intValue()!=0){
			if(cntRetanteADescontar==null){
				if(sinAnx.getCantDisponible().intValue()<cntGrupo.intValue()){
					cntRetanteADescontar=cntGrupo.intValue()-sinAnx.getCantDisponible().intValue();
					sinAnx.setCantDisponible(new Integer(0));
				}else{
					sinAnx.setCantDisponible(sinAnx.getCantDisponible().intValue()-cntGrupo.intValue());
					cntRetanteADescontar=0;
				}
			}else{
				if(sinAnx.getCantDisponible().intValue()<cntRetanteADescontar.intValue()){
					cntRetanteADescontar=cntRetanteADescontar.intValue()-sinAnx.getCantDisponible().intValue();
					sinAnx.setCantDisponible(new Integer(0));
				}else{
					sinAnx.setCantDisponible(sinAnx.getCantDisponible().intValue()-cntRetanteADescontar.intValue());
					cntRetanteADescontar=0;
				}
			}
			if(cntRetanteADescontar.intValue()==0)
				return true;
			em.merge(sinAnx);
			
			
		}
		
		
		return false;
		
		
	}
	
	
	public String seleccionar2(BigDecimal montoCar,Integer objCod, String ctgCod){
		try {
			
			/**
			 * 
			 * OBJETO GASTO 111
			 * */
			/**
			 * el sistema debe descontar del stock de la tabla SIN_ANX para el objeto de gasto 111 
			 * , según la cantidad de puestos activos del grupo
			 * Se descuenta de la tabla sin_anx columna cant_disponible.
			 *  Se descuenta del primer registro que recupere el select, si la cantidad es suficiente,
			 * */
			
			List<SinAnx> sinAnxlist=obtenerSinAnx(objCod, ctgCod) ;
			for (SinAnx aux : sinAnxlist) {
				if(descontarStock(aux))
					break;
				
					
			}
			/**
			 * montoFinal para saber que monto ingresar en la tabla concepto_pago
			 * **/
			BigDecimal montoFinal=montoCar;
			/**
			 * 	Insertar un registro en puesto_concepto_pago con el objeto de gasto 111 
			 * */
			for (ConcursoPuestoDet puestoDet : puestoDets) {
				PuestoConceptoPago puestoConceptoPago= new PuestoConceptoPago();
				/**
				 * se ingresa el objeto y la categoria seleccionada
				 * */
				puestoConceptoPago.setObjCodigo(objCod);
				puestoConceptoPago.setCategoria(ctgCod);
				puestoConceptoPago.setFechaAlta(new Date());
				puestoConceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				puestoConceptoPago.setAnho( concurso.getAnhio());
				puestoConceptoPago.setActivo(true);
				puestoConceptoPago.setDisponible(true);
				/**
				 * el puesto asociado
				 * */
				puestoConceptoPago.setPlantaCargoDet(em.find(PlantaCargoDet.class, puestoDet.getPlantaCargoDet().getIdPlantaCargoDet()));
				/**
				 * . El estado de cada registro es = ‘RESERVADO’
				 * */
				puestoConceptoPago.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA","RESERVADO"));
				/**
				 * si el monto es de la pantalla de carga guarda directamente sin buscar
				 * */
				
				BigDecimal motoCalculado=null;
				if(montoCar==null){
					for (SinAnx sinAnx : sinAnxlist) {
						motoCalculado=getMontoFromCat(sinAnx);
						if(motoCalculado!=null)
							break;
					}
					montoFinal=motoCalculado;
					puestoConceptoPago.setMonto(Integer.parseInt(motoCalculado.toString()));	
				}else 
					puestoConceptoPago.setMonto(Integer.parseInt(montoCar.toString()));
					
				em.persist(puestoConceptoPago);
			}
			
			/**
			 * 	Insertar un registro en grupo_concepto_pago con el objeto de gasto 111 
			 * */
			insertarConceptoPago(ctgCod,objCod, montoFinal);
			montoCar=null;
			em.flush();
			if (from != null && !"".equals(from)){
				return from;
			};
			return "/seleccion/validarCategoriaAdReferendum/ValidarCategorias.seam&concursoPuestoAgrIdConcursoPuestoAgr="+idAgr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void cargarMonto(){
		if(montoCarga==null || montoCarga.equals(0))
		{
			statusMessages.add(Severity.ERROR,"Debe Ingresar el monto");
			return;
		}
		if(montoCarga.intValue()<0)
		{
			statusMessages.add(Severity.ERROR,"El monto debe ser mayo a cero, verifique");
			return;
		}
		CatAnxDto anxDto=listaCategorias.get(indexSelec);
		seleccionar2(montoCarga, anxDto.getObjCodigo(), anxDto.getCtgCodigo());
		
	}

	private void insertarConceptoPago(String categoria,Integer objCodigo,BigDecimal montoFinal){
		GrupoConceptoPago conceptoPago= new GrupoConceptoPago();
		conceptoPago.setCategoria(categoria);
		conceptoPago.setObjCodigo(objCodigo);
		conceptoPago.setMonto(Integer.parseInt(montoFinal.toString()));
		conceptoPago.setAnho(concurso.getAnhio());
		conceptoPago.setEstado(seguridadUtilFormController.estadoActividades("ESTADOS_CATEGORIA_GRUPO","RESERVADO"));
		conceptoPago.setTipo("GRUPO");
		conceptoPago.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class,idAgr));
		conceptoPago.setFechaAlta(new Date());
		conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		conceptoPago.setActivo(true);
		em.persist(conceptoPago);
		
		
	}
	

	private List<SinAnx> obtenerSinAnx(Integer objCodigo, String ctgCodigo){
		try {
			Integer vrs_codigo = new Integer(50);
			List<SinAnx> anxs=sinarhUtiles.obtenerListaSinAnx(concurso.getAnhio(), vrs_codigo, objCodigo, ctgCodigo, null);
				
			return anxs;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private BigDecimal getMontoFromCat(SinAnx sinAnx){
		String sql = "select * from sinarh.sin_categoria cat "
			+ "where cat.ctg_codigo = '" + sinAnx.getCtgCodigo() + "'"
			+ " and cat_grupo = " + sinAnx.getCatGrupo()
			+ " and ani_aniopre  = " + concurso.getAnhio()
			+ " and cat.vrs_codigo = '" + sinAnx.getVrsCodigo() + "'";
		List<SinCategoria> listaMonto = new ArrayList<SinCategoria>();
		listaMonto = em.createNativeQuery(sql, SinCategoria.class)
				.getResultList();
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
		
		return monto;
	}
	public boolean tieneMonto(int index){
		CatAnxDto anxDto=listaCategorias.get(index);
		
		List<SinAnx>  anxlis=obtenerSinAnx(anxDto.getObjCodigo(), anxDto.getCtgCodigo());
		BigDecimal monto=null;
		for (SinAnx sinAnx : anxlis) {
			monto=getMontoFromCat(sinAnx);
			if(monto!=null)
				break;
		}
		if(monto==null|| monto.equals(0))
			return false;
		return true;
	}
	public void selecCat(int index){
		indexSelec=index;
	}
	
	public void search() {
		buscarCategorias();
	}

	public void searchAll() {
		codigoSearch = null;
		categoriaSearch = null;
		buscarCategorias();
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

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	public void back(){
		listaCategorias= new Vector<CatAnxDto>();
		objGasto=null;
		codigoSearch=null;
		categoriaSearch=null;
		cntGrupo=null;
		cntRetanteADescontar=null;
		
	}
	
	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
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

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}

	
	public String getCodigoSinarh() {
		return codigoSinarh;
	}

	public void setCodigoSinarh(String codigoSinarh) {
		this.codigoSinarh = codigoSinarh;
	}

	

	public List<CatAnxDto> getListaCategorias() {
		return listaCategorias;
	}


	public void setListaCategorias(List<CatAnxDto> listaCategorias) {
		this.listaCategorias = listaCategorias;
	}


	public String getCategoriaSearch() {
		return categoriaSearch;
	}

	public void setCategoriaSearch(String categoriaSearch) {
		this.categoriaSearch = categoriaSearch;
	}

	public String getCodigoSearch() {
		return codigoSearch;
	}

	public void setCodigoSearch(String codigoSearch) {
		this.codigoSearch = codigoSearch;
	}

	public Long getIdPlantaCargo() {
		return idPlantaCargo;
	}

	public void setIdPlantaCargo(Long idPlantaCargo) {
		this.idPlantaCargo = idPlantaCargo;
	}

	public Long getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(Long idAgr) {
		this.idAgr = idAgr;
	}


	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	public String getNombreConcurso() {
		return nombreConcurso;
	}


	public void setNombreConcurso(String nombreConcurso) {
		this.nombreConcurso = nombreConcurso;
	}


	public String getNombreGrupo() {
		return nombreGrupo;
	}


	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}


	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}


	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}


	public Integer getCntGrupo() {
		return cntGrupo;
	}


	public void setCntGrupo(Integer cntGrupo) {
		this.cntGrupo = cntGrupo;
	}


	public List<ConcursoPuestoDet> getPuestoDets() {
		return puestoDets;
	}


	public void setPuestoDets(List<ConcursoPuestoDet> puestoDets) {
		this.puestoDets = puestoDets;
	}


	public String getObjGasto() {
		return objGasto;
	}


	public void setObjGasto(String objGasto) {
		this.objGasto = objGasto;
	}


	public List<GrupoConceptoPago> getGrupoConceptoPagoList() {
		return grupoConceptoPagoList;
	}


	public void setGrupoConceptoPagoList(
			List<GrupoConceptoPago> grupoConceptoPagoList) {
		this.grupoConceptoPagoList = grupoConceptoPagoList;
	}


	public BigDecimal getMontoCarga() {
		return montoCarga;
	}


	public void setMontoCarga(BigDecimal montoCarga) {
		this.montoCarga = montoCarga;
	}


	public Long getIdSinAuxCarga() {
		return idSinAuxCarga;
	}


	public void setIdSinAuxCarga(Long idSinAuxCarga) {
		this.idSinAuxCarga = idSinAuxCarga;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public boolean isHabSeleccionado() {
		return habSeleccionado;
	}


	public void setHabSeleccionado(boolean habSeleccionado) {
		this.habSeleccionado = habSeleccionado;
	}


	public int getIndexSelec() {
		return indexSelec;
	}


	public void setIndexSelec(int indexSelec) {
		this.indexSelec = indexSelec;
	}

	public String getCtgCodigo() {
		return ctgCodigo;
	}

	public void setCtgCodigo(String ctgCodigo) {
		this.ctgCodigo = ctgCodigo;
	}

	public Boolean getHabilitarSeleccionar() {
		return habilitarSeleccionar;
	}

	public void setHabilitarSeleccionar(Boolean habilitarSeleccionar) {
		this.habilitarSeleccionar = habilitarSeleccionar;
	}
	
	
	
	
}
