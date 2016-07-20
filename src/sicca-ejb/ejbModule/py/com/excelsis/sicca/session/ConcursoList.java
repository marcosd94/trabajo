package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.BandejaEntrada;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("concursoList")
public class ConcursoList extends CustomEntityQuery<Concurso> {

	private static final String EJBQL = "select concurso from Concurso concurso";

	private static final String[] RESTRICTIONS = {
			"lower(concurso.nombre) like lower(concat('%', concat(#{concursoList.concurso.nombre},'%')))",
			"concurso.activo=#{concursoList.estado}",
			"concurso.configuracionUoCab.idConfiguracionUo=#{concursoList.idConfiguracionUOCab}",
			"concurso.datosEspecificosTipoConc.idDatosEspecificos=#{concursoList.idTipoConcurso}",
			"concurso.configuracionUoCab.entidad.idEntidad=#{concursoList.idEntidad}",
			"concurso.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{concursoList.nenCodigo}",
	};

	private static final String ORDER = "concurso.fechaCreacion DESC";
	private Concurso concurso = new Concurso();
	private Long idConfiguracionUOCab;
	private Long idTipoConcurso;
	private Long idEntidad;
	private BigDecimal nenCodigo;
	private Boolean estado = true;

	public ConcursoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la b�squeda.
	 */
	public List<Concurso> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public List<Concurso> listaResultados(String ejbql){
		setEjbql(ejbql);
		setOrder(ORDER);
		List<Concurso> lista = getResultList(); 
		return lista;
	}
	

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Concurso> limpiar() {
		try{
			concurso= new Concurso();
		//	idConfiguracionUOCab = null;
			idTipoConcurso = null;
		//	nenCodigo=null;
		//	idEntidad=null;
			setEjbql(EJBQL);
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
			setOrder(ORDER);
			setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
			
			List<Concurso> lista = getResultList();
			return lista;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Concurso> listaConcursos(String ejbql){
		//	setEjbql(ejbql);
			setCustomEjbql(ejbql);
			setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
			setOrder(ORDER);
			List<Concurso> lista = getResultList(); 
			
			return lista;
		}
		
	
	public Concurso getConcurso() {
		return concurso;
	}

	public Long getIdConfiguracionUOCab() {
		return idConfiguracionUOCab;
	}

	public void setIdConfiguracionUOCab(Long idConfiguracionUOCab) {
		this.idConfiguracionUOCab = idConfiguracionUOCab;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Long getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}

	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	

	
	
	
	
}
