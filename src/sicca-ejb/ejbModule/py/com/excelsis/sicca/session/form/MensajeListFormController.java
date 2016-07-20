package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.ws.Action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.Mensaje;
import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.MensajeHome;
import py.com.excelsis.sicca.session.MensajeList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import sun.misc.Cleaner;
import enums.Estado;
import enums.TiposDatos;

//@Scope(ScopeType.CONVERSATION)
@Scope(ScopeType.EVENT)//MODIFICADO RV
@Name("mensajeListFormController")
public class MensajeListFormController implements Serializable {

	@In(value="entityManager")
    EntityManager em;
	@In(create = true)
	MensajeList mensajeList;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(required = false)
	Usuario usuarioLogueado;
	

	private String valor;
	private String codigo = null;
	private Boolean activo;
	private List<Mensaje> listaMensaje = new ArrayList<Mensaje>();
	private Mensaje mensaje;
	private MensajeHome mensajeHome;
	private Long idMensaje;
	private Boolean edit=false;
	
	
	public void init() throws Exception {
	 search();

	}

	public void searchAll() throws Exception {
		valor = null; 
		codigo = null;
		edit = false;
		search();
	}
	
	@SuppressWarnings("unchecked")
	public void search() throws Exception {

		String query = getQuery();
		if (query == null)
			return;
		try{
		listaMensaje = em.createQuery(query).getResultList();
		
		}catch(Exception e){
			e.getStackTrace();
		}
	}
	

	public void editar(int index){
		edit=true;
		
		System.out.print("PROBANDO IMPRIMIR: "+ index);
		System.out.print("TAMAÑO LISTA: "+ listaMensaje.size());
		valor=listaMensaje.get(index).getValorMensaje();
		System.out.print("PROBANDO IMPRIMIR: "+ valor);
		

	}

	public void update(){
		mensaje.setValorMensaje(getValor());
		mensajeHome.setInstance(mensaje);
		mensajeHome.update();
		mensajeHome.save();
		mensajeHome.clearInstance();
	}
	public String getQuery() throws Exception {

		String query = "select mensaje from Mensaje mensaje ";
 
		query += " where 1=1 ";
		if (valor != null) 
			query += " and mensaje.valorMensaje like '" + valor+"'";
	
		if (codigo != null) 
			query += " and mensaje.codigoMensaje like '" + codigo+"'";
		
		return query;
	}
	
	public String getValorByCodigo(String codigo){

		
		String mensaje_descripcion = "DESCRIPCION NO CARGADA para "+codigo;
		
		setCodigo(codigo);
		try {
			listaMensaje = mensajeList.listaResultadosMensaje(this.getQuery());
			mensaje_descripcion = listaMensaje.get(0).getValorMensaje();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mensaje_descripcion;
	}
	
	public void listar(){
	
		if(getCodigo()!=null){
		listaMensaje =	em.createQuery(" select e from Mensaje e " + " where e.codigo= '"+ getCodigo()+"'").getResultList();
		System.out.println("LISTAR UNO SOLO : "+listaMensaje.get(0).getValorMensaje()+"\n\n\n\n\n\n\n\n\n");
		}else{
			listaMensaje =	em.createQuery(" select e from Mensaje e").getResultList();
			System.out.println("LISTAR TODOS: "+listaMensaje.get(0).getValorMensaje()+"\n\n\n\n\n\n\n\n\n");
		}
	
	
	}


	public String getMensajeByIdMensaje(Long idMensaje){
		listaMensaje = em.createQuery(" select e from Mensaje e " + " where e.id_mensaje = "+mensaje.getIdMensaje()).getResultList();
		return listaMensaje.get(0).getValorMensaje();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public List<Mensaje> getListaMensaje() {
		return listaMensaje;
	}

	public void setListaMensaje(List<Mensaje> listaMensaje) {
		this.listaMensaje = listaMensaje;
	}

	
	
	public Long getIdMensaje() {
		return idMensaje;
	}

	public void setIdMensaje(Long idMensaje) {
		this.idMensaje = idMensaje;
	}

	public Boolean getEdit() {
		return edit;
	}

	public void setEdit(Boolean edit) {
		this.edit = edit;
	}
	
	
}
