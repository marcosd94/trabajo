package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.resource.spi.EISSystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioHome;
import py.com.excelsis.sicca.session.CiudadHome;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.MatrizReferencialDetHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("matrizReferencialDetEditFormController")
public class MatrizReferencialDetEditFormController implements Serializable {

	/**
	 * 
	 */
	// private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	MatrizReferencialDetHome matrizReferencialDetHome;

	@In(value = "entityManager")
	EntityManager em;

	private Long idMatrizReferencialEnc;// PARAMETRO QUE RECIBE
	private MatrizReferencialEnc matrizReferencialEnc = new MatrizReferencialEnc();
	private Long idMatrizReferencialDet;
	private MatrizReferencialDet matrizReferencialDet = new MatrizReferencialDet();
	private List<MatrizReferencialDet> itemsDetsList = new ArrayList<MatrizReferencialDet>();
	private String descripcion;
	private Float puntajeMinimo;
	private Float puntajeMaximo;
	private Float maxItems;
	private int selectedRow = -1;
	private List<MatrizReferencialDet> removeCollection = new ArrayList<MatrizReferencialDet>();

	public void init() {
		try {
			matrizReferencialDet = new MatrizReferencialDet();
			maxItems = null;
			puntajeMaximo = null;
			puntajeMinimo = null;
			descripcion = null;
			itemsDetsList = new ArrayList<MatrizReferencialDet>();
			selectedRow = -1;
			if (idMatrizReferencialDet != null) {

				matrizReferencialDet = em.find(MatrizReferencialDet.class, idMatrizReferencialDet);
				idMatrizReferencialEnc =
					matrizReferencialDet.getMatrizReferencialEnc().getIdMatrizReferencialEnc();
				matrizReferencialEnc = em.find(MatrizReferencialEnc.class, idMatrizReferencialEnc);
				matrizReferencialDetHome.setInstance(matrizReferencialDet);
				items();
				seleccionado();
				getRowClass(selectedRow);
			} else {
				if (idMatrizReferencialEnc != null) {
					matrizReferencialEnc =
						em.find(MatrizReferencialEnc.class, idMatrizReferencialEnc);
					matrizReferencialDet.setMatrizReferencialEnc(matrizReferencialEnc);
					items();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String save() {
		try {
			for (int i = 0; i < itemsDetsList.size(); i++) {
				MatrizReferencialDet aux = new MatrizReferencialDet();
				if (itemsDetsList.get(i).getIdMatrizReferencialDet() == null) {
					aux.setDescripcion(itemsDetsList.get(i).getDescripcion().toUpperCase());
					aux.setFechaAlta(new Date());
					aux.setUsuAlta(itemsDetsList.get(i).getUsuAlta());
					aux.setPuntajeMaximo(itemsDetsList.get(i).getPuntajeMaximo());
					aux.setPuntajeMinimo(itemsDetsList.get(i).getPuntajeMinimo());
					aux.setActivo(true);
					aux.setMatrizReferencialEnc(matrizReferencialEnc);
					em.persist(aux);
					em.flush();
				} else {
					aux =
						em.find(MatrizReferencialDet.class, itemsDetsList.get(i).getIdMatrizReferencialDet());
					aux.setActivo(itemsDetsList.get(i).getActivo());
					em.merge(aux);
					em.flush();
				}

			}
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String update() {
		try {
			for (int i = 0; i < itemsDetsList.size(); i++) {
				MatrizReferencialDet aux = new MatrizReferencialDet();
				aux =
					em.find(MatrizReferencialDet.class, itemsDetsList.get(i).getIdMatrizReferencialDet());
				aux.setActivo(itemsDetsList.get(i).getActivo());
				em.merge(aux);
				em.flush();
			}
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addItems() {
		if (descripcion == null || descripcion.trim().equals("")) {
			statusMessages.add(Severity.ERROR,"Debe Ingresar la descripcion");
			return;
		}
		if (puntajeMaximo == null || puntajeMinimo == null) {
			statusMessages.add(Severity.ERROR,"Debe ingresar los puntajes");
			return;
		}
		if(matrizReferencialEnc.getPuntajeMaximo().intValue()!=0){
			if (puntajeMinimo.intValue() < 0) {
				statusMessages.add(Severity.ERROR,"El Mínimo NO puede ser menor a 0");
				return;
			}
			if (puntajeMaximo.intValue() <= 0) {
				statusMessages.add(Severity.ERROR,"El Máximo NO puede ser menor o igual a 0");
				return;
			}
			if ( (puntajeMaximo.intValue()!=puntajeMinimo.intValue())&&(puntajeMaximo.intValue() <= puntajeMinimo.intValue())) {
				statusMessages.add(Severity.ERROR,"El campo M\u00EDnimo NO pueden ser mayor al Máximo");
				return;
			}
		}else{
			if(puntajeMinimo.intValue()!=0 || puntajeMaximo.intValue()!=0){
				statusMessages.add(Severity.ERROR,"El Puntaje Mínimo y Máximo deben ser cero, verifique ");
				return;
			}
		}
		
		
		if (existeItems()) {
			statusMessages.add(Severity.ERROR,"No se puede duplicar la descripcion con los puntajes. Verifique");
			return;
		}
		if (matrizReferencialEnc.getSumItemsSN()) {
			maxItems += puntajeMaximo;
			if (maxItems != null) {
				if (maxItems.intValue() > matrizReferencialEnc.getPuntajeMaximo().intValue()) {
					maxItems -= puntajeMaximo;
					statusMessages.add(Severity.ERROR,"La suma de los puntajes máximos ya supera la Graduaci\u00F3n de Puntaje");
					return;
				}
			}

		} else {
			if ((puntajeMaximo.intValue()!=matrizReferencialEnc.getPuntajeMaximo().intValue())&&(puntajeMaximo.intValue() > matrizReferencialEnc.getPuntajeMaximo().intValue())) {
				statusMessages.add(Severity.ERROR,"El puntaje m\u00E1ximo no debe superar la Graduaci\u00F3n de Puntaje");
				return;
			}

		}

		MatrizReferencialDet det = new MatrizReferencialDet();
		det.setDescripcion(descripcion);
		det.setFechaAlta(new Date());
		det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		det.setPuntajeMaximo(puntajeMaximo);
		det.setPuntajeMinimo(puntajeMinimo);
		det.setActivo(true);
		det.setMatrizReferencialEnc(matrizReferencialEnc);
		itemsDetsList.add(det);
		descripcion = null;
		puntajeMaximo = null;
		puntajeMinimo = null;
	}

	public void delItems(int index) {
		MatrizReferencialDet element = itemsDetsList.get(index);
		if (element.getIdMatrizReferencialDet() != null) {
			removeCollection.add(element);
		}
		maxItems -= element.getPuntajeMaximo();
		itemsDetsList.remove(element);
	}

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	// METODOS PRIVADO
	private void seleccionado() {
		for (int i = 0; i < itemsDetsList.size(); i++) {
			if (itemsDetsList.get(i).getIdMatrizReferencialDet().equals(idMatrizReferencialDet)) {
				selectedRow = i;
				break;
			}
		}
	}

	private boolean existeItems() {
		for (int i = 0; i < itemsDetsList.size(); i++) {
			if (itemsDetsList.get(i).getDescripcion().toLowerCase().equals(descripcion.toLowerCase())
				&& itemsDetsList.get(i).getPuntajeMaximo() == puntajeMaximo
				&& itemsDetsList.get(i).getPuntajeMinimo() == puntajeMinimo) {
				return true;
			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<MatrizReferencialDet> items() {
		try {
			itemsDetsList =
				em.createQuery(" Select d from MatrizReferencialDet d"
					+ " where d.matrizReferencialEnc.idMatrizReferencialEnc = "
					+ idMatrizReferencialEnc + "" + " order by d.idMatrizReferencialDet ").getResultList();
			maxItems = new Float(0);
			for (int i = 0; i < itemsDetsList.size(); i++) {
				if (itemsDetsList.get(i).getActivo())
					maxItems += itemsDetsList.get(i).getPuntajeMaximo();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsDetsList;
	}

	// GETTERS Y SETTERS

	public MatrizReferencialEnc getMatrizReferencialEnc() {
		return matrizReferencialEnc;
	}

	//
	public void setMatrizReferencialEnc(MatrizReferencialEnc matrizReferencialEnc) {
		this.matrizReferencialEnc = matrizReferencialEnc;
	}

	public Long getIdMatrizReferencialDet() {
		return idMatrizReferencialDet;
	}

	public void setIdMatrizReferencialDet(Long idMatrizReferencialDet) {
		this.idMatrizReferencialDet = idMatrizReferencialDet;
	}

	public MatrizReferencialDet getMatrizReferencialDet() {
		return matrizReferencialDet;
	}

	public void setMatrizReferencialDet(MatrizReferencialDet matrizReferencialDet) {
		this.matrizReferencialDet = matrizReferencialDet;
	}

	public List<MatrizReferencialDet> getItemsDetsList() {
		return itemsDetsList;
	}

	public void setItemsDetsList(List<MatrizReferencialDet> itemsDetsList) {
		this.itemsDetsList = itemsDetsList;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Float getPuntajeMinimo() {
		return puntajeMinimo;
	}

	public void setPuntajeMinimo(Float puntajeMinimo) {
		this.puntajeMinimo = puntajeMinimo;
	}

	public Float getPuntajeMaximo() {
		return puntajeMaximo;
	}

	public void setPuntajeMaximo(Float puntajeMaximo) {
		this.puntajeMaximo = puntajeMaximo;
	}

	public Float getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Float maxItems) {
		this.maxItems = maxItems;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public Long getIdMatrizReferencialEnc() {
		return idMatrizReferencialEnc;
	}

	public void setIdMatrizReferencialEnc(Long idMatrizReferencialEnc) {
		this.idMatrizReferencialEnc = idMatrizReferencialEnc;
	}

}
