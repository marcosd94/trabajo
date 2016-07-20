package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Mensaje;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;


import java.util.List;
@Scope(ScopeType.CONVERSATION)
@Name("mensajeList")
public class MensajeList extends CustomEntityQuery<Mensaje> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Mensaje> listaMensajes;
	private static final String EJBQL = "SELECT mensaje FROM Mensaje mensaje";

	private static final String ORDER = "mensaje.codigoMensaje desc,mensaje.valorMensaje desc ";
	private Mensaje mensaje = new Mensaje();
	private String  valor;
	private String codigo;

	public MensajeList() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Mensaje> listaResultados() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Mensaje> listaResultadosMensaje(String ejbql) {
		//setEjbql(ejbql);
		setCustomEjbql(ejbql);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<Mensaje> lista = getResultList(); 
		return lista;
		
		
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda para el UC192
	 */
	public List<Mensaje> buscarResultadosUC192() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Mensaje> limpiar() {
		mensaje = new Mensaje();
		codigo = null;
		valor = null;
		setEjbql(EJBQL);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


//	GETTERS Y SETTERS
	public Mensaje getMensaje() {
		return mensaje;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public List<Mensaje> obtenerListaMensajes(){
    	listaMensajes = getResultList(); 
    	return listaMensajes;
    }
    public List<Mensaje> getListaMensajes() {
		return listaMensajes;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	

}
