package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import enums.Estado;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.session.CptList;

/**
 * @author jmelgarejo
 * Clase manejadora UC192
 * */
@Scope(ScopeType.PAGE)
@Name("cptSearchListFormController")
public class CptSearchListFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7564265762876058867L;

	@In(create = true)
	CptList cptList;
	
	private Cpt	cpt = new Cpt();
	private Long codTipoCpt;
	private Estado estado = Estado.ACTIVO;
	private String from= null;
	private String tipo;
	
	public void init(){
		cpt.setCptPadre(new Cpt());
		cpt.getCptPadre().setActivo(Estado.ACTIVO.getValor());
		cpt.setActivo(Estado.ACTIVO.getValor());
		search();
	}
	
	public void search(){

		String query = getQuery();
		List<Cpt> listaCpt = cptList.listaResultadosCpt(query);
		System.out.println(listaCpt.size());
	}
	public String getQuery() {
		//Select
		String query = "select cpt from Cpt cpt ";
		if ((tipo == null || !tipo.equals("general")) && codTipoCpt!=null)
			query += " join cpt.tipoCpt tipo";
		//Where
		query += " where cpt.activo = "+estado.getValor();
		if((tipo == null || !tipo.equals("general")) && codTipoCpt != null)
			query += " and tipo.idTipoCpt = "+codTipoCpt;
		if (tipo != null && tipo.equals("general"))
			query += " and cpt.nroEspecifico is null";
		if((tipo == null || !tipo.equals("general")) && cpt.getNroEspecifico() != null)
			query += " and cpt.nroEspecifico = "+cpt.getNroEspecifico();
		if(cpt.getNivel() != null)
			query += " and cpt.nivel = "+cpt.getNivel();
		if(cpt.getNumero() != null)
			query += " and cpt.numero = "+cpt.getNumero();
		if(tipo != null && tipo.equals("especifico"))//MODIFICADO RV
			query 	+= " and cpt.cptPadre is not null"
					+  " and cpt.estadoHomologacion = '" + Cpt.ESTADO_HOMOLOGADO + "'";
		if(cpt.getDenominacion() != null && !cpt.getDenominacion().trim().isEmpty())
			query += " and lower(cpt.denominacion) like lower(concat('%', concat('"+cpt.getDenominacion()+"','%')))";
		if(cpt.getCptPadre() != null && cpt.getCptPadre().getDenominacion() != null && !cpt.getCptPadre().getDenominacion().trim().isEmpty())
			query += " and lower(cpt.cptPadre.denominacion) like lower(concat('%', concat('"+cpt.getCptPadre().getDenominacion()+"','%')))";

		return query;
	}
	
	public void searchAll(){
		
		cpt = new Cpt();
		estado = Estado.ACTIVO;
		cpt.setCptPadre(new Cpt());
		cpt.getCptPadre().setActivo(Estado.ACTIVO.getValor());
		if(from==null)
			codTipoCpt = null;
			
		search();
			
	}

//	GETTERS Y SETTERS
	public Cpt getCpt() {
		return cpt;
	}
	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}
	public Long getCodTipoCpt() {
		return codTipoCpt;
	}
	public void setCodTipoCpt(Long codTipoCpt) {
		this.codTipoCpt = codTipoCpt;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
	
	
	
}
