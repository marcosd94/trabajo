package py.com.excelsis.sicca.session.form;

import java.awt.Stroke;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import py.com.excelsis.sicca.dto.RequisitosMinDTO;
import py.com.excelsis.sicca.entity.AgrupamientoCce;
import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.ClasificadorUoRequisito;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.entity.GradoSalarialCab;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCuo;
import py.com.excelsis.sicca.entity.TipoCce;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AgrupamientoCceList;
import py.com.excelsis.sicca.session.ClasificadorUoRequisitoHome;
import py.com.excelsis.sicca.session.GradoSalarialCabHome;
import py.com.excelsis.sicca.session.GradoSalarialHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("gradoSalarialFormController")
@Scope(ScopeType.PAGE)
public class GradoSalarialFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2881610119430601586L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	GradoSalarialCabHome gradoSalarialCabHome;

	@In(create = true)
	AgrupamientoCceList agrupamientoCceList;

	private Long idGradoSalarialCab;
	private GradoSalarialCab gradoSalarialCab;
	private List<GradoSalarial> gradoSalaDetList = new ArrayList<GradoSalarial>();
	private Long idNivelGradoSalarial;
	private Integer montoMinimo;
	private Integer montoMaximo;
	private Integer montoM;

	public Integer getMontoM() {
		return montoM;
	}

	public void setMontoM(Integer montoM) {
		this.montoM = montoM;
	}

	private Integer anho;
	private Long idTipoCce;
	private Integer grado;
	private Integer cntGrado;
	private boolean ultimoDet = false;
	private int selectedRow = -1;

	private boolean esEditDet = false;

	List<AgrupamientoCce> niveles;

	public void init() {

		gradoSalarialCab = new GradoSalarialCab();
		clrVar();
		if (idGradoSalarialCab != null) {
			gradoSalarialCab = em.find(GradoSalarialCab.class, idGradoSalarialCab);
			gradoSalarialCabHome.setInstance(gradoSalarialCab);
			anho = gradoSalarialCab.getAnho();
			obtnedetalle();
			cntGrado = gradoSalaDetList.size();
			grado = cntGrado;
			ultimoDet = true;
			if (!gradoSalaDetList.isEmpty()) {
				montoMinimo = gradoSalaDetList.get(gradoSalaDetList.size() - 1).getMontoMaximo();

			}
			idTipoCce = gradoSalarialCab.getTipoCce().getIdTipoCce();
			esEditDet = false;
		} else {
			gradoSalarialCab.setActivo(true);
		}

	}

	public Long getIdGradoSalarialCab() {
		return idGradoSalarialCab;
	}

	public void setIdGradoSalarialCab(Long idGradoSalarialCab) {
		this.idGradoSalarialCab = idGradoSalarialCab;
	}

	private void clrVar() {
		anho = null;
		montoMaximo = null;
		montoMinimo = null;
		grado = null;
		cntGrado = null;
		gradoSalaDetList = new ArrayList<GradoSalarial>();
	}

	@SuppressWarnings("unchecked")
	private void obtnedetalle() {
		gradoSalaDetList =
			em.createQuery(" select m from GradoSalarial m "
				+ " where m.gradoSalarialCab.idGradoSalarialCab=" + idGradoSalarialCab
				+ " order by m.numero desc").getResultList();
	}

	public void agregarDet() {
		try {
			if(idTipoCce==null){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de CCE. Verifique");
				return;
			}
			if( anho==null ){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Debe Ingresar el año. Verifique");
				return;
			}
			if(cntGrado==null ){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Cnt. de grado. Verifique");
				return;
			}
			if(cntGrado.intValue()<0){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Cnt. de grado positivo. Verifique");
				return;
			}
			if(cntGrado.intValue()==0){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Cnt. de grado mayor a Cero. Verifique");
				return;
			}
			if (cntGrado.intValue() == grado.intValue()
				&& cntGrado.intValue() == gradoSalaDetList.size()) {
				statusMessages.add(Severity.ERROR,"No se puede agregar el registro, verifique la cantidad de grado a ingresar");
				return;
			}
			if (cntGrado.intValue() <= gradoSalaDetList.size()) {
				statusMessages.add(Severity.ERROR,"No se puede agregar el registro, verifique la cantidad de grado");
				return;
			}

			if (cntGrado == null) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GradoSalarialCabs_msg_cantidad"));
				return;
			}

			if (idNivelGradoSalarial == null || montoMaximo == null || montoMinimo == null) {
				statusMessages.add(Severity.ERROR,"Debe Ingresar los campos obligatorios");
				return;
			}
			if(montoMinimo==null)
			{
				statusMessages.add(Severity.ERROR,"Ingrese el monto mínimo");
				return;	
			}
			if(montoMinimo.intValue()<0){
				statusMessages.add(Severity.ERROR,"El monto mínimo no puede ser negativo");
				return;

			}
			if(montoMaximo==null)
			{
				statusMessages.add(Severity.ERROR,"Ingrese el monto máximo");
				return;
			}
			if(montoMaximo.intValue()<0){
				statusMessages.add(Severity.ERROR,"El monto máximo no puede ser negativo");
				return;
				
			}
			
			if (montoMaximo.intValue() <= montoMinimo.intValue()) {
				statusMessages.add(Severity.ERROR,"El monto Máximo debe ser mayor al Monto Mínimo.");
				return;
			}
			if (cntGrado == grado)
				ultimoDet = true;
			GradoSalarial var = new GradoSalarial();
			var.setAgrupamientoCce(em.find(AgrupamientoCce.class, idNivelGradoSalarial));
			var.setMontoMaximo(montoMaximo);
			var.setMontoMinimo(montoMinimo);
			var.setNumero(grado);
			gradoSalaDetList.add(var);
			montoMinimo = montoMaximo + 1;
			montoMaximo = null;
			idNivelGradoSalarial = null;
			if (grado != cntGrado)
				grado++;
			// else
			// montoMaximo=null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@SuppressWarnings("unchecked")
	private boolean validarAnio(){
		List<Referencias> minimo= em.createQuery("Select r from Referencias r " +
				" where r.dominio='ANHO_MINIMO' and r.activo=true").getResultList();
		List<Referencias> maximo= em.createQuery("Select r from Referencias r " +
		" where r.dominio='ANHO_MAXIMO' and r.activo=true").getResultList();
		if(!minimo.isEmpty())
		{
			if(!maximo.isEmpty()){
				if(anho>=minimo.get(0).getValorNum().intValue() && anho<=maximo.get(0).getValorNum().intValue()){
					return true;
				}else
					return false;
			}else{
				if(anho>=minimo.get(0).getValorNum().intValue()){
					return true;
				}else
					return false;
			}
		}else{
			if(!maximo.isEmpty()){
				if( anho<=maximo.get(0).getValorNum().intValue()){
					return true;
				}else
					return false;
			}
		}
		
		return true;
		
		
	}

	public String update() {
		try {
			if (gradoSalaDetList.isEmpty()) {
				statusMessages.add(Severity.ERROR, "Debe agregar al menos un detalle");
				return null;
			}
			if (anho == null) {
				statusMessages.add(Severity.ERROR,"Debe ingresar el año. Verifique");
				return null;
			}
			if(anho.intValue()<0){
				statusMessages.add(Severity.ERROR,"El año debe ser positivo");
				return null ;
			}
			if (anho.toString().length() != 4) {
				statusMessages.add(Severity.ERROR,"El año debe tener 4 dígitos.");
				return null;
			}
			if(!validarAnio()){
				statusMessages.add(Severity.ERROR,"Año no válido");
				return null;
			}
			if (idTipoCce == null) {
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de CCE. Verifique");
				return null;
			}
			
			if (exite("update")) {
				statusMessages.add(Severity.ERROR,"El Tipo de CCE y el año ya exiten. Verifique");
				return null;
			}

			gradoSalarialCab.setFechaMod(new Date());
			gradoSalarialCab.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
			gradoSalarialCab.setTipoCce(em.find(TipoCce.class, idTipoCce));
			gradoSalarialCab.setAnho(anho);
			em.merge(gradoSalarialCab);
			em.flush();
			for (GradoSalarial var : gradoSalaDetList) {
				GradoSalarial grado = new GradoSalarial();
				if (var.getIdGradoSalarial() != null) {
					grado = em.find(GradoSalarial.class, var.getIdGradoSalarial());
					grado.setFechaMod(new Date());
					grado.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
					grado.setAnho(anho);
					grado.setGradoSalarialCab(gradoSalarialCab);
					grado.setAgrupamientoCce(var.getAgrupamientoCce());
					grado.setMontoMaximo(var.getMontoMaximo());
					grado.setMontoMinimo(var.getMontoMinimo());
					grado.setNumero(var.getNumero());
					em.merge(var);
				} else {
					grado.setFechaAlta(new Date());
					grado.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
					grado.setAnho(anho);
					grado.setGradoSalarialCab(gradoSalarialCab);
					grado.setAgrupamientoCce(var.getAgrupamientoCce());
					grado.setMontoMaximo(var.getMontoMaximo());
					grado.setMontoMinimo(var.getMontoMinimo());
					grado.setNumero(var.getNumero());
					em.persist(grado);
				}

				em.flush();
			}

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		String mens = "Registro actualizado con exito";
		statusMessages.add(Severity.INFO, mens);
		return "updated";
	}

	public String save() {
		try {
			if (gradoSalaDetList.isEmpty()) {
				statusMessages.add(Severity.ERROR, "Debe agregar al menos un detalle");
				return null;
			}
			if (anho == null) {
				statusMessages.add(Severity.ERROR,"Debe ingresar el año. Verifique");
				return null;
			}
			if(anho.intValue()<0){
				statusMessages.add(Severity.ERROR,"El año debe ser positivo");
				return null ;
			}
			if (anho.toString().length() != 4) {
				statusMessages.add(Severity.ERROR,"El año debe tener 4 dígitos.");
				return null;
			}
			if(!validarAnio()){
				statusMessages.add(Severity.ERROR,"Año no válido");
				return null;
			}
			if (idTipoCce == null) {
				statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de CCE. Verifique");
				return null;
			}
			
			if (exite("save")) {
				statusMessages.add(Severity.ERROR,"El Tipo de CCE y el año ya exiten. Verifique");
				return null;
			}

			gradoSalarialCab.setFechaAlta(new Date());
			gradoSalarialCab.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			gradoSalarialCab.setTipoCce(em.find(TipoCce.class, idTipoCce));
			gradoSalarialCab.setAnho(anho);
			em.persist(gradoSalarialCab);
			em.flush();

			for (GradoSalarial var : gradoSalaDetList) {
				GradoSalarial grado = new GradoSalarial();
				grado.setFechaAlta(new Date());
				grado.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
				grado.setAnho(anho);
				grado.setGradoSalarialCab(gradoSalarialCab);
				grado.setAgrupamientoCce(var.getAgrupamientoCce());
				grado.setMontoMaximo(var.getMontoMaximo());
				grado.setMontoMinimo(var.getMontoMinimo());
				grado.setNumero(var.getNumero());
				em.persist(grado);
				em.flush();
			}

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
		String mens = "Registro creado con exito";
		statusMessages.add(Severity.INFO, mens);
		return "persisted";
	}

	@SuppressWarnings("unchecked")
	private boolean exite(String op) {
		List<GradoSalarialCab> cabs = new ArrayList<GradoSalarialCab>();
		String sql="Select c from GradoSalarialCab c where c.tipoCce.idTipoCce="
			+ idTipoCce + " and c.anho=" + anho;
		if(op.equals("update"))
			sql+=" and c.idGradoSalarialCab!="+gradoSalarialCab.getIdGradoSalarialCab();
		cabs =em.createQuery(sql).getResultList();
		if (!cabs.isEmpty())
			return true;

		return false;
	}

	public void editDet(int index) {
		selectedRow = index;
		esEditDet = true;
		GradoSalarial aux = gradoSalaDetList.get(index);
		setMontoMaximo(aux.getMontoMaximo());
		montoM = aux.getMontoMinimo();
		setMontoMinimo(aux.getMontoMinimo());
		setIdNivelGradoSalarial(aux.getAgrupamientoCce().getIdAgrupamientoCce());
		grado = aux.getNumero();
	}

	public void confirmar() {
		
		if(idTipoCce==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de CCE. Verifique");
			return;
		}
		if( anho==null ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Ingresar el año. Verifique");
			return;
		}
		if(cntGrado==null ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"Debe Ingresar la Cnt. de grado. Verifique");
			return;
		}
		

		if (cntGrado == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GradoSalarialCabs_msg_cantidad"));
			return;
		}

		if (idNivelGradoSalarial == null || montoMaximo == null || montoMinimo == null) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar los campos obligatorios");
			return;
		}
		if (montoMaximo.intValue() <= montoMinimo.intValue()) {
			statusMessages.add(Severity.ERROR,"El monto Máximo debe ser mayor al Monto Mínimo.");
			return;
		}
		
		GradoSalarial aux = new GradoSalarial();
		aux = gradoSalaDetList.get(selectedRow);
		aux.setAgrupamientoCce(em.find(AgrupamientoCce.class, idNivelGradoSalarial));
		aux.setMontoMinimo(montoMinimo);
		aux.setMontoMaximo(montoMaximo);
		gradoSalaDetList.set(selectedRow, aux);
		upMontoMax(selectedRow);
		upMontoMin(selectedRow);
		// montoMaximo=gradoSalaDetList.get(gradoSalaDetList.size()-1).getMontoMinimo();
		int ultpos = gradoSalaDetList.size() - 1;
		if (gradoSalaDetList.get(ultpos).getNumero() != cntGrado)
			grado = gradoSalaDetList.get(ultpos).getNumero() + 1;
		else
			grado = gradoSalaDetList.get(ultpos).getNumero();
		esEditDet = false;
		// montoMinimo=null;
		montoMinimo = gradoSalaDetList.get(gradoSalaDetList.size() - 1).getMontoMaximo() + 1;
		montoMaximo = null;
		idNivelGradoSalarial = null;

	}

	public void cancelar() {
		int ultpos = gradoSalaDetList.size() - 1;
		montoMinimo = gradoSalaDetList.get(ultpos).getMontoMaximo()+1;
		if (gradoSalaDetList.get(ultpos).getNumero().intValue() == cntGrado.intValue())
			grado = gradoSalaDetList.get(ultpos).getNumero();
		else
			grado = gradoSalaDetList.get(ultpos).getNumero() + 1;

		esEditDet = false;
		montoMaximo = null;
		idNivelGradoSalarial = null;
	}

	public boolean getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return false;
		}
		return true;
	}

	public void upGrado() {
		int tam = 0;

		if (!gradoSalaDetList.isEmpty()) {
			tam = gradoSalaDetList.size() - 1;
			if (cntGrado < gradoSalaDetList.get(tam).getNumero().intValue()) {
				statusMessages.add("La Cantidad de grado no puede ser menor al grado");
				cntGrado = grado;
			}
		}

		if (gradoSalaDetList.isEmpty() && cntGrado != null) {
			grado = 1;
		} else if (cntGrado >= grado && ultimoDet
			&& gradoSalaDetList.get(tam).getNumero().intValue() == grado.intValue()) {
			ultimoDet = false;
			grado++;
		} else if (cntGrado >= grado && !ultimoDet
			&& gradoSalaDetList.get(tam).getNumero().intValue() != grado.intValue()) {
			grado = gradoSalaDetList.get(tam).getNumero();
			grado++;
		}// else if(grado>cntGrado){
			// if(gradoSalaDetList.size()==grado){
			// statusMessages.add("La Cantidad de grado no puede ser menor al grado");
			// cntGrado=grado;
			// }
			// else
			// grado=cntGrado;
			// }else
			// grado++;

	}

	@SuppressWarnings("unchecked")
	public void copiLastYear() {
		if (idTipoCce == null) {
			statusMessages.add(Severity.ERROR,"Seleccione el Tipo de CCE para realizar la copia");
			return;
		}
		if (anho == null) {
			statusMessages.add(Severity.ERROR,"Indique un año");
			return;
		}
		if(anho.intValue()<0){
			statusMessages.add(Severity.ERROR,"El año debe ser positivo");
			return ;
		}
		if (anho.toString().length() != 4) {
			statusMessages.add(Severity.ERROR,"El año debe tener 4 dígitos.");
			return;
		}
		if(!validarAnio()){
			statusMessages.add(Severity.ERROR,"Año no válido");
			return;
		}
		
		List<GradoSalarialCab> cad =
			em.createQuery(" select m from GradoSalarialCab m "
				+ " where m.anho = "+(anho-1)+" and m.tipoCce.idTipoCce=" + idTipoCce).getResultList();
		if (!cad.isEmpty()) {
			gradoSalaDetList =
				em.createQuery(" select m from GradoSalarial m "
					+ " where m.gradoSalarialCab.idGradoSalarialCab="
					+ cad.get(0).getIdGradoSalarialCab() + " " + " order by m.numero ").getResultList();
			anho = cad.get(0).getAnho() + 1;
			cntGrado = gradoSalaDetList.size();
			grado = cntGrado;
			// montoMaximo=gradoSalaDetList.get(gradoSalaDetList.size()-1).getMontoMinimo();
			montoMinimo = gradoSalaDetList.get(gradoSalaDetList.size() - 1).getMontoMaximo() + 1;
			montoMaximo = null;
			idTipoCce = cad.get(0).getTipoCce().getIdTipoCce();
			updatesNivel();
			esEditDet = false;
			ultimoDet = true;
		} else {
			statusMessages.add(Severity.WARN,"El Tipo CCE no posee datos, seleccione otro");
			montoMaximo = null;
			montoMinimo = null;
			gradoSalaDetList = new ArrayList<GradoSalarial>();
			gradoSalaDetList = new ArrayList<GradoSalarial>();
			niveles = new ArrayList<AgrupamientoCce>();
			ultimoDet = false;
			esEditDet = false;
			 selectedRow = -1;
			 updatesNivel();

		}

	}

	public void updatesNivel() {
		if (idTipoCce == null) {
			niveles = new ArrayList<AgrupamientoCce>();
		} else {
			niveles = new ArrayList<AgrupamientoCce>();
			agrupamientoCceList.setIdTipoCce(idTipoCce);
			niveles = agrupamientoCceList.listaTodos();
		}

	}

	public List<SelectItem> getNivelesTipoCeeSelectItem() {
		if (niveles == null) {
			updatesNivel();
		}
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (AgrupamientoCce o : niveles)
			si.add(new SelectItem(o.getIdAgrupamientoCce(), "" + o.getDescripcion()));
		return si;
	}

	public void upMontoMax(int index) {
		int idxMax = index - 1;

		if (idxMax >= 0) {
			gradoSalaDetList.get(idxMax).setMontoMaximo(gradoSalaDetList.get(index).getMontoMinimo() - 1);
		}
	}

	public void upMontoMin(int index) {
		int idxMin = index + 1;// Si modifica el max es el min del sgt

		if (idxMin < gradoSalaDetList.size()) {
			gradoSalaDetList.get(idxMin).setMontoMinimo((gradoSalaDetList.get(index).getMontoMaximo()) + 1);
		}
	}

	public Long getIdNivelGradoSalarial() {
		return idNivelGradoSalarial;
	}

	public void setIdNivelGradoSalarial(Long idNivelGradoSalarial) {
		this.idNivelGradoSalarial = idNivelGradoSalarial;
	}

	public Integer getMontoMinimo() {
		return montoMinimo;
	}

	public void setMontoMinimo(Integer montoMinimo) {
		this.montoMinimo = montoMinimo;
	}

	public Integer getMontoMaximo() {
		return montoMaximo;
	}

	public void setMontoMaximo(Integer montoMaximo) {
		this.montoMaximo = montoMaximo;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public Long getIdTipoCce() {
		return idTipoCce;
	}

	public void setIdTipoCce(Long idTipoCce) {
		this.idTipoCce = idTipoCce;
	}

	public Integer getGrado() {
		return grado;
	}

	public void setGrado(Integer grado) {
		this.grado = grado;
	}

	public Integer getCntGrado() {
		return cntGrado;
	}

	public void setCntGrado(Integer cntGrado) {
		this.cntGrado = cntGrado;
	}

	public GradoSalarialCab getGradoSalarialCab() {
		return gradoSalarialCab;
	}

	public void setGradoSalarialCab(GradoSalarialCab gradoSalarialCab) {
		this.gradoSalarialCab = gradoSalarialCab;
	}

	public List<GradoSalarial> getGradoSalaDetList() {
		return gradoSalaDetList;
	}

	public void setGradoSalaDetList(List<GradoSalarial> gradoSalaDetList) {
		this.gradoSalaDetList = gradoSalaDetList;
	}

	public boolean isUltimoDet() {
		return ultimoDet;
	}

	public void setUltimoDet(boolean ultimoDet) {
		this.ultimoDet = ultimoDet;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public boolean isEsEditDet() {
		return esEditDet;
	}

	public void setEsEditDet(boolean esEditDet) {
		this.esEditDet = esEditDet;
	}

}
