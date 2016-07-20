package py.com.excelsis.sicca.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

import enums.AlquiladoProp;
import enums.Bloqueado;
import enums.ContenidoFuncionalEnum;
import enums.DestinadoA;
import enums.EsFuncionario;
import enums.Estado;
import enums.EstadoAprobPendiente;
import enums.EstadoEnum;
import enums.EstadoImportacion;
import enums.EstadoReclamo;
import enums.EstadoSumario;
import enums.EvalsReferenciales;
import enums.HorasAnios;
import enums.InstitucionFinaciadora;
import enums.ModalidadCapaSeleccione;
import enums.ModalidadCapacitacion;
import enums.ModalidadOcupacion;
import enums.ModalidadOcupacionSeleccion;
import enums.Modulo;
import enums.NivelAgrupacionEnum;
import enums.NivelCptEnum;
import enums.PermanenteContratadoEnums;
import enums.PorInstitucion;
import enums.RespuestaSolicitudEnums;
import enums.SeleccionadoResultado;
import enums.SexoEnums;
import enums.SiNo;
import enums.SiNoTodosEnums;
import enums.TipoEvaluacion;
import enums.TipoExcepcion;
import enums.TipoPostulacionEnums;
import enums.TipoReclamo;
import enums.TipoSeleccion;
import enums.TipoValidacion;
import enums.TitularSuplente;
import enums.AusentePresente;
import enums.TipoGeneralEspecifico;

@Name("enumAppHelper")
@Scope(ScopeType.APPLICATION)
public class EnumAppHelper {

	private static final String PACK_ENUM = "enums.";
	
	public String getAusentePresentePorId(Integer id) {
		return AusentePresente.getAusentePresentePorId(id).getDescripcion();
	}

	public AusentePresente[] getAusentePresente() {
		return AusentePresente.values();
	}
	
	public EstadoSumario[] getEstadoSumario() {
		return EstadoSumario.values();
	}
	
	public String getEstadoPorId(Integer id) {
		return Estado.getEstadoPorId(id).getDescripcion();
	}

	public Estado[] getEstados() {
		return Estado.values();
	}
	public EvalsReferenciales[] getEvalsReferenciales() {
		return EvalsReferenciales.values();
	}
	
	public String getModalidadPorId(Integer id) {
		return ModalidadOcupacion.getModalidadPorId(id).getDescripcion();
	}

	public ModalidadOcupacion[] getModalidadOcupacions() {
		return ModalidadOcupacion.values();
	}
	public TipoGeneralEspecifico[] getTipoGeneralEspecifico() {
		return TipoGeneralEspecifico.values();
	}
	public TipoExcepcion[] getTipoExcepcion() {
		return TipoExcepcion.values();
	}
	
	public String getModuloPorId(Integer id) {
		return Modulo.getModuloPorId(id).getDescripcion();
	}

	public Modulo[] getModulos() {
		return Modulo.values();
	}
	
	public String getAlquiladoPropPorId(Integer id) {
		return AlquiladoProp.getAlquiladoPropPorId(id).getDescripcion();
	}

	public AlquiladoProp[] getAlquiladoProp() {
		return AlquiladoProp.values();
	}
	
	public String getTipoReclamoPorId(String id) {
		return TipoReclamo.getTipoReclamoPorId(id).getDescripcion();
	}

	public TipoReclamo[] getTipoReclamos() {
		return TipoReclamo.values();
	}
	
	public String getPorInstitucionPorId(Integer id) {
		return PorInstitucion.getPorInstitucionPorId(id).getDescripcion();
	}

	public PorInstitucion[] getPorInstitucion() {
		return PorInstitucion.values();
	}
	
	
	public String getModalidadOcupacionSeleccionPorId(Integer id) {
		return ModalidadOcupacionSeleccion.getModalidadPorId(id).getDescripcion();
	}

	public ModalidadOcupacionSeleccion[] getModalidadOcupacionSeleccions() {
		return ModalidadOcupacionSeleccion.values();
	}
	
	
	public String getTitSuplSeleccionPorId(Integer id) {
		return TitularSuplente.getTitularSuplentePorId(id).getDescripcion();
	}

	public TitularSuplente[] getTitularSuplenteSeleccions() {
		return TitularSuplente.values(); 
	}
	
	public String geTipoEvaluacionPorId(String id) {
		return TipoEvaluacion.getTipoPorId(id).getDescripcion();
	}

	public TipoEvaluacion[] getTiposEvauacions() {
		return TipoEvaluacion.values();
	}
	
	
	public String getEstadoAprobPendientePorId(String id) {
		return EstadoAprobPendiente.getEstadoAprobPendientePorId(id).getDescripcion();
	}

	public EstadoAprobPendiente[] getEstadoAprobPendientes() {
		return EstadoAprobPendiente.values();
	}
	
	public String getBloqueadoPorId(String id) {
		return Bloqueado.getBloqueadoPorId(id).getDescripcion();
	}

	public Bloqueado[] getBloqueados() {
		return Bloqueado.values();
	}
	
	public String getEstadoReclamoPorId(String id) {
		return EstadoReclamo.getEstadoReclamoPorId(id).getDescripcion();
	}

	public EstadoReclamo[] getEstadoReclamos() {
		return EstadoReclamo.values();
	}
	

	public String getHorasAniosPorId(String id) {
		return HorasAnios.getHorasAniosPorId(id).getDescripcion();
	}

	public HorasAnios[] getHorasAnios() {
		return HorasAnios.values();
	}
	
	public HorasAnios[] getHorasAniosbyParam(String param){
		HorasAnios[] v = new HorasAnios[2];
		if(param.equals("F")){
			for(HorasAnios h : HorasAnios.values()){
				if(h.getValor().equals("A"))
					v[0] = h;
				else if(h.getValor().equals("H"))
					v[1] = h;
			}
		}else if(param.equals("C")){
			for(HorasAnios h : HorasAnios.values()){
				if(h.getValor().equals("C"))
					v[0] = h;
				else if(h.getValor().equals("S"))
					v[1] = h;
			}
		}
		return v;
	}
	
	public String getNivelAgrupacionEnumPorId(String id) {
		return NivelAgrupacionEnum.getNivelAgrupacionEnumPorId(id).getDescripcion();
	}

	public NivelAgrupacionEnum[] getNivelAgrupacionEnum() {
		return NivelAgrupacionEnum.values();
	}
	
	public String getNivelCptEnumPorId(String id) {
		return NivelCptEnum.getNivelCptEnumPorId(id).getDescripcion();
	}
	public RespuestaSolicitudEnums[] getRespuestaSolicitudEnums() {
		return RespuestaSolicitudEnums.values();
	}
	
	public String getRespuestaSolicitudEnumsPorId(String id) {
		return RespuestaSolicitudEnums.getRespuestaSolicitudEnumsPorId(id).getDescripcion();
	}

	public NivelCptEnum[] getNivelCptEnum() {
		return NivelCptEnum.values();
	}
	
	public String getEstadoImpPorId(String id) {
		return EstadoImportacion.getEstadoImportacionPorId(id).getDescripcion();
	}

	public EstadoImportacion[] getEstadoImp() {
		return EstadoImportacion.values();
	}
	
	public String getContenidoFuncionalPorId(String id) {
		return ContenidoFuncionalEnum.getContenidoFuncionalEnumPorId(id).getDescripcion();
	}

	public ContenidoFuncionalEnum[] getContenidoFuncionals() {
		return ContenidoFuncionalEnum.values();
	}
	
	public String getPublicadoPorId(Integer id) {
		return SiNo.getSiNoPorId(id).getDescripcion();
	}

	public SiNo[] getSinos() {
		return SiNo.values();
	}
	
	public String getTipoSeleccionPorId(String id) {
		return TipoSeleccion.getTipoPorId(id).getDescripcion();
	}

	public TipoSeleccion[] getTipoSeleccions() {
		return TipoSeleccion.values();
	}
	public String getModalidadCapacitacionPorId(String id) {
		return ModalidadCapacitacion.getTipoPorId(id).getDescripcion();
	}

	public ModalidadCapacitacion[] getModalidadCapacitacions() {
		return ModalidadCapacitacion.values();
	}
	public String getDestinadoAPorId(String id) {
		return DestinadoA.getTipoPorId(id).getDescripcion();
	}

	public DestinadoA[] getDestinadoAs() {
		return DestinadoA.values();
	}
	public String getInstitucionFinaciadoraPorId(String id) {
		return InstitucionFinaciadora.getTipoPorId(id).getDescripcion();
	}

	public InstitucionFinaciadora[] getInstitucionFinaciadoras() {
		return InstitucionFinaciadora.values();
	}
	public String getModalidadCapaSeleccionePorId(String id) {
		return ModalidadCapaSeleccione.getModalidadCapaSeleccioneId(id).getDescripcion();
	}

	public ModalidadCapaSeleccione[] getModalidadCapaSelecciones() {
		return ModalidadCapaSeleccione.values();
	}
	public String getSeleccionadoResultadoPorId(String id) {
		return SeleccionadoResultado.getSeleccionadoResultadoId(id).getDescripcion();
	}

	public SeleccionadoResultado[] getSeleccionadoResultados() {
		return SeleccionadoResultado.values();
	}
	
	public String getTipoValidacionPorId(Integer id) {
		return TipoValidacion.getTipoValidacionPorId(id).getDescripcion();
	}

	public TipoValidacion[] getTipoValidacions() {
		return TipoValidacion.values();
	}
	
	public String getTipoPostulacionEnumsPorId(String id) {
		return TipoPostulacionEnums.getTipoPostulacionEnumsPorId(id).getDescripcion();
	}

	public TipoPostulacionEnums[] getTipoPostulacionEnums() {
		return TipoPostulacionEnums.values();
	}
	public String getPermanenteContratadoEnumsPorId(Integer id) {
		return PermanenteContratadoEnums.getPermanenteContratadoEnumsPorId(id).getDescripcion();
	}

	public PermanenteContratadoEnums[] getPermanenteContratadoEnums() {
		return PermanenteContratadoEnums.values();
	}
	public String getSiNoTodosEnumsPorId(Integer id) {
		return SiNoTodosEnums.getSiNoTodosEnumsPorId(id).getDescripcion();
	}

	public SiNoTodosEnums[] getSiNoTodosEnums() {
		return SiNoTodosEnums.values();
	}
	public String getSexoEnumsPorId(Integer id) {
		return SexoEnums.getSexoEnumsPorId(id).getDescripcion();
	}

	public SexoEnums[] getSexoEnums() {
		return SexoEnums.values();
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SelectItem> enumsValueItems(String type, String metodName, boolean... mostrarSelect) {
		List<SelectItem> lista = new ArrayList<SelectItem>();

		String evalString = type == null || type.trim().isEmpty() ? null : type.trim();
		
		if (evalString == null) {
			return lista;
		}
		
		
		if (mostrarSelect.length == 0) {
			mostrarSelect = new boolean[1];
			mostrarSelect[0] = true;
		}
		if (mostrarSelect[0]) {
			lista.add(new SelectItem(null, SeamResourceBundle.getBundle()
					.getString("opt_select_one")));
		}
		try {
			Class<Enum> enumerator = (Class<Enum>) Class.forName(PACK_ENUM
					+ type);

			for (Enum o : enumerator.getEnumConstants()) {

				lista.add(new SelectItem(o.getClass().getMethod(metodName)
						.invoke(o), o.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			lista
					.add(new SelectItem(null,
							"Error al Cargar el enum. Nombre invalido o no se encuentra."));
		}

		return lista;
	}
}
