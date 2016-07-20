package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.session.MatrizReferencialEncHome;
import py.com.excelsis.sicca.session.MatrizReferencialHome;
import py.com.excelsis.sicca.session.MatrizReferencialList;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("factorEvaluacionFormController")
public class FactorEvaluacionFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7244638552490997404L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	MatrizReferencialEncHome matrizReferencialEncHome;
	@In(create = true)
	MatrizReferencialHome matrizReferencialHome;
	@In(create = true)
	MatrizReferencialList matrizReferencialList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	private MatrizReferencialEnc matrizReferencialEnc;
	private MatrizReferencial matrizReferencial;
	private Long idMatrizReferencial;
	private Long idDatosEspecificosByTipoEvaluacion;
	private int selectedRow = -1;
	private Boolean filtroActivo;

	private List<MatrizReferencialEnc> listDetails;
	private List<SelectItem> plantillaMatrizSelectItems = new ArrayList<SelectItem>();

	public void init() {
		matrizReferencialEnc = matrizReferencialEncHome.getInstance();
		if (matrizReferencialEncHome.isIdDefined()) {
			matrizReferencial = matrizReferencialEnc.getMatrizReferencial();
			cargarDatosMatriz();
		} else {
			matrizReferencial = matrizReferencialHome.getInstance();
			cargarDatosMatriz();
		}
		posicionSeleccionada();
		matrizReferencialEnc = new MatrizReferencialEnc();
		updateSelectItem(matrizReferencial);
	}

	public String save() {
		String result = null;
		try {
			if (!checkData())
				return null;

			for (MatrizReferencialEnc detail : listDetails) {

				matrizReferencialEncHome.setInstance(detail);
				if (detail.getIdMatrizReferencialEnc() == null)
					result = matrizReferencialEncHome.persist();
				else
					result = matrizReferencialEncHome.update();

				matrizReferencialEncHome.clearInstance();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
				matrizReferencialEncHome.clearInstance();
			}
		}
		return result;
	}

	public void addRow() {
		if (!toInsertDetailOk())
			return;
		Integer nro = matrizReferencialEnc.getNroOrden();
		cambiarOrdenLista(nro);
		matrizReferencialEnc.setDescripcion(matrizReferencialEnc.getDescripcion().trim().toUpperCase());
		matrizReferencialEnc.setMatrizReferencial(matrizReferencial);
		matrizReferencialEnc.setDatosEspecificos(em.find(DatosEspecificos.class, idDatosEspecificosByTipoEvaluacion));
		matrizReferencialEnc.setActivo(Estado.ACTIVO.getValor());

		listDetails.add(matrizReferencialEnc);
		clearParametersDetail();
	}

	private void cambiarOrdenLista(Integer valor) {
		for (int i = 0; i < listDetails.size(); i++) {
			MatrizReferencialEnc m = new MatrizReferencialEnc();
			m = listDetails.get(i);
			if (m.getNroOrden().intValue() == valor.intValue()) {
				valor = m.getNroOrden() + 1;
				m.setNroOrden(valor);
				listDetails.set(i, m);
			}

		}
	}

	public void removeRow(int row) {
		listDetails.remove(row);
	}

	public void obtenerMatrizReferencial() {
		matrizReferencial = em.find(MatrizReferencial.class, idMatrizReferencial);
	}

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	private Boolean superaSumaMax(List<MatrizReferencialEnc> lFactores, float sumaCab) {
		long sumaFactor = 0;

		for (MatrizReferencialEnc o : lFactores) {
			if (o.getActivo())
				sumaFactor += o.getPuntajeMaximo().floatValue();
		}
		if (sumaCab < sumaFactor) {
			statusMessages.add(Severity.ERROR, "La suma de Factores no puede ser mayor a "
				+ sumaCab);
			return true;
		}
		return false;
	}

	// METODOS PRIVADOS
	private Boolean checkData() {
		if (listDetails.isEmpty()) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_list_empty"));
			return false;
		}
		/* Incidencia 0000768 */
		if (superaSumaMax(listDetails, listDetails.get(0).getMatrizReferencial().getPuntajeMaximo())) {
			return false;
		}
		// Control para que no se repitan factores
		List<String> lCache = new ArrayList<String>();
		String elKey = "";
		for (MatrizReferencialEnc factor : listDetails) {
			elKey =
				factor.getDescripcion().toLowerCase() + "#"
					+ factor.getDatosEspecificos().getIdDatosEspecificos();

			if (factor.getActivo() && !lCache.contains(elKey)) {
				lCache.add(elKey);
			} else if (factor.getActivo() && lCache.contains(elKey)) {
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_factor_en_detalle"));
				return false;
			}
		}
		/***************/

		/* Incidencia 1580 */
		boolean encontro = false;
		DatosEspecificos de =
			seleccionUtilFormController.traerDatosEspecificos("TIPOS DE EVALUACION", "EVALUACION CURRICULAR");
		for (MatrizReferencialEnc detail : listDetails) {
			if (detail.getDatosEspecificos().getIdDatosEspecificos().intValue() == de.getIdDatosEspecificos().intValue()) {
				encontro = true;
			}
		}
		if (!encontro) {
			statusMessages.add(Severity.ERROR, "La Evaluación Curricular es obligatoria.");
			return false;
		}
		/*****************/
		return true;
	}

	private void cargarDatosMatriz() {
		idMatrizReferencial = matrizReferencial.getIdMatrizReferencial();
		listDetails = buscarLista(matrizReferencial.getIdMatrizReferencial());
	}

	@SuppressWarnings("unchecked")
	private List<MatrizReferencialEnc> buscarLista(Long id) {

		String cad =
			"select mat.* from seleccion.matriz_referencial_enc mat "
				+ "where mat.id_matriz_referencial = " + id + " order by mat.nro_orden ";

		return em.createNativeQuery(cad, MatrizReferencialEnc.class).getResultList();

	}

	public void searchAll() {
		cargarDatosMatriz();
	}

	public void search() {
		cargarDatosMatriz();
		if (filtroActivo != null) {
			List<MatrizReferencialEnc> listaAx = new ArrayList<MatrizReferencialEnc>();
			for (MatrizReferencialEnc enc : listDetails) {
				if (enc.getActivo().equals(filtroActivo))
					listaAx.add(enc);

			}
			listDetails.clear();
			listDetails.addAll(listaAx);
		}
	}

	private Boolean toInsertDetailOk() {
		if (idMatrizReferencial == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_plantilla"));
			return false;
		}
		if (idDatosEspecificosByTipoEvaluacion == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_tipo_eval"));
			return false;
		}
		if (matrizReferencialEnc.getDescripcion() == null
			|| matrizReferencialEnc.getDescripcion().isEmpty()) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_descripcion_factor"));
			return false;
		}
		if (matrizReferencialEnc.getNroOrden() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_nro_orden"));
			return false;
		}
		if (matrizReferencialEnc.getNroOrden().intValue() <= 0) {
			statusMessages.add(Severity.ERROR, "El Número de Orden NO puede ser menor o igual a 0");
			return false;
		}

		if (matrizReferencialEnc.getPuntajeMaximo() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_graduacion"));
			return false;
		}

		if (matrizReferencialEnc.getObligatorioSN() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_obligatorio"));
			return false;
		}
		if (matrizReferencialEnc.getSumItemsSN() == null) {
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_sin_sun_items"));
			return false;
		}
		if (matrizReferencialEnc.getPuntajeMaximo() > matrizReferencial.getPuntajeMaximo()) {
			statusMessages.add(Severity.ERROR, "La Graduación Puntaje de los factores NO puede exceder el puntaje máximo de la cabecera");
			return false;
		}
		if(!esConcursoSimplificado(matrizReferencial.getDatosEspecificos().getIdDatosEspecificos())){
			if (matrizReferencialEnc.getPuntajeMaximo() <= 0) {
				statusMessages.add(Severity.ERROR, "La Graduación Puntaje de los factores debe ser mayor a cero");
				return false;
			}
		}
		
		if (!listDetails.isEmpty()) {
			for (MatrizReferencialEnc factor : listDetails) {
				if (factor.getActivo()
					&& factor.getDescripcion().toLowerCase().equals(matrizReferencialEnc.getDescripcion().trim().toLowerCase())
					&& factor.getDatosEspecificos().getIdDatosEspecificos().equals(idDatosEspecificosByTipoEvaluacion)) {
					statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("MatrizReferencial_msg_factor_en_detalle"));
					return false;
				}
			}
		}
		return true;
	}

	private void clearParametersDetail() {
		matrizReferencialEnc = new MatrizReferencialEnc();
		idDatosEspecificosByTipoEvaluacion = null;
	}
	
	private boolean esConcursoSimplificado(Long idDatosEspecifico){
		DatosEspecificos tipoConcurso=em.find(DatosEspecificos.class,idDatosEspecifico);
		if(tipoConcurso.getDescripcion().toUpperCase().equals("CONCURSO SIMPLIFICADO"))
			return true;
		
		return false;
	}
	
	private void updateSelectItem(MatrizReferencial matriz) {
		plantillaMatrizSelectItems.clear();
		plantillaMatrizSelectItems.add(new SelectItem(null, SICCAAppHelper.getBundleMessage("opt_select_one")));
		if (matriz != null && matriz.getIdMatrizReferencial() != null) {
			matrizReferencialList.getMatrizReferencial().setDescripcion(matriz.getDescripcion());
			String descMat = matriz.getDescripcion();
			matrizReferencialList.setOrder("matrizReferencial.descripcion");
		}
		matrizReferencialList.setMaxResults(null);

		List<MatrizReferencial> list = matrizReferencialList.getResultList();
		if (list != null) {
			for (MatrizReferencial mat : list) {
				// if (!mat.getMatrizReferencialEncs().isEmpty()) {
				plantillaMatrizSelectItems.add(new SelectItem(mat.getIdMatrizReferencial(), mat.getDescripcion()));
				// }
			}
		} else
			plantillaMatrizSelectItems.add(new SelectItem(matriz.getIdMatrizReferencial(), matriz.getDescripcion()));
	}

	private void posicionSeleccionada() {
		if (matrizReferencialEncHome.getMatrizReferencialEncIdMatrizReferencialEnc() != null) {
			for (int i = 0; i < listDetails.size(); i++) {
				if (listDetails.get(i).getIdMatrizReferencialEnc().equals(matrizReferencialEncHome.getMatrizReferencialEncIdMatrizReferencialEnc())) {
					selectedRow = i;
					break;
				}
			}
		}
	}

	// GETTERS Y SETTERS
	public MatrizReferencialEnc getMatrizReferencialEnc() {
		return matrizReferencialEnc;
	}

	public void setMatrizReferencialEnc(MatrizReferencialEnc matrizReferencialEnc) {
		this.matrizReferencialEnc = matrizReferencialEnc;
	}

	public MatrizReferencial getMatrizReferencial() {
		return matrizReferencial;
	}

	public void setMatrizReferencial(MatrizReferencial matrizReferencial) {
		this.matrizReferencial = matrizReferencial;
	}

	public Long getIdMatrizReferencial() {
		return idMatrizReferencial;
	}

	public void setIdMatrizReferencial(Long idMatrizReferencial) {
		this.idMatrizReferencial = idMatrizReferencial;
	}

	public Long getIdDatosEspecificosByTipoEvaluacion() {
		return idDatosEspecificosByTipoEvaluacion;
	}

	public void setIdDatosEspecificosByTipoEvaluacion(Long idDatosEspecificosByTipoEvaluacion) {
		this.idDatosEspecificosByTipoEvaluacion = idDatosEspecificosByTipoEvaluacion;
	}

	public List<MatrizReferencialEnc> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<MatrizReferencialEnc> listDetails) {
		this.listDetails = listDetails;
	}

	public List<SelectItem> getPlantillaMatrizSelectItems() {
		return plantillaMatrizSelectItems;
	}

	public void setPlantillaMatrizSelectItems(List<SelectItem> tipoEvaluacionSelectItems) {
		this.plantillaMatrizSelectItems = tipoEvaluacionSelectItems;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public Boolean getFiltroActivo() {
		return filtroActivo;
	}

	public void setFiltroActivo(Boolean filtroActivo) {
		this.filtroActivo = filtroActivo;
	}

}
