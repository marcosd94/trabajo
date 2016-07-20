package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.apache.commons.collections.map.HashedMap;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.entity.MatrizDocConfigGrupos;
import py.com.excelsis.sicca.entity.MatrizDocumentalDet;
import py.com.excelsis.sicca.entity.MatrizDocumentalEnc;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.MatrizDocConfigDetHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("matrizDocConfigDetEditFormController")
public class MatrizDocConfigDetEditFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	MatrizDocConfigDetHome matrizDocConfigDetHome;

	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private MatrizDocConfigDet matrizDocConfigDet;
	private Long idMatrizDocConfigDet;
	private List<MatrizDocumentalDet> matrizDocumentalDetList =
		new ArrayList<MatrizDocumentalDet>();
	private List<MatrizDocumentalEnc> matrizDocumentalEncList =
		new ArrayList<MatrizDocumentalEnc>();
	private List<MatrizDocConfigDet> matrizDocConfigDetList = new ArrayList<MatrizDocConfigDet>();
	private List<MatrizDocConfigGrupos> matrizDocConfigGruposList = new ArrayList<MatrizDocConfigGrupos>();
	private Long idConcursoPuestoAgr;// recibe del CU que le llama
	private ConcursoPuestoAgr concursoPuestoAgr;// enviado por el CU
	private Concurso concurso;
	private int selectedRow = -1;
	private int selectedRowEdit = -1;
	private int selectedRowEditGrupos = -1;
	

	private HashMap<String, Boolean> cacheMostrarLinksMatDocumental =
		new HashMap<String, Boolean>();

	private List<MatrizDocConfigDet> matDocConfigDetListRemove =
		new ArrayList<MatrizDocConfigDet>();
	
	private List<MatrizDocConfigGrupos> matDocConfigGruposListRemove =
			new ArrayList<MatrizDocConfigGrupos>();
	private boolean esSimplificado=false;

	/**
	 * si recibe del CU 162
	 **/
	private String fromCU = null;
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	public void init() {
		cacheMostrarLinksMatDocumental.clear();
		matDocConfigDetListRemove = new ArrayList<MatrizDocConfigDet>();

		matrizDocumentalDetList = new ArrayList<MatrizDocumentalDet>();
		matrizDocConfigDetList = new ArrayList<MatrizDocConfigDet>();
		matrizDocConfigGruposList = new ArrayList<MatrizDocConfigGrupos>();
		
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		if (concursoPuestoAgr.getConcurso() != null) {
			concurso = em.find(Concurso.class, concursoPuestoAgr.getConcurso().getIdConcurso());
			DatosEspecificos tipoConcurso=em.find(DatosEspecificos.class,concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() );
			if(tipoConcurso.getDescripcion().toUpperCase().equals("CONCURSO SIMPLIFICADO"))
				esSimplificado=true;
			else
				esSimplificado=false;
		}
		/* Incidencia 1014 */
		validarOee(concurso);
		/**/
		if (idMatrizDocConfigDet != null) {
			matrizDocConfigDet = em.find(MatrizDocConfigDet.class, idMatrizDocConfigDet);
			findDets();
			findGrupos();
			filaSeleccionada();
		} else {
			selectedRow = -1;
			cargarItems1Config();
		}
		matrizDocConfigDetHome.setInstance(matrizDocConfigDet);
		if (matrizDocConfigDetHome.isIdDefined()) {

		}

	}

	public String save() {
		try {
			if (matrizDocConfigDetList.isEmpty()) {
				statusMessages.add("Debe seleccionar almenos un registro para configurar, verifique");
				return null;
			}
			if(!seleccionOk())
				return null;
			
			MatrizDocConfigEnc matrizDocConfigEnc = new MatrizDocConfigEnc();
			matrizDocConfigEnc.setActivo(true);
			matrizDocConfigEnc.setConcursoPuestoAgr(concursoPuestoAgr);
			matrizDocConfigEnc.setDatosEspecificos(matrizDocumentalEncList.get(selectedRow).getDatosEspecificos());
			matrizDocConfigEnc.setFechaAlta(new Date());
			matrizDocConfigEnc.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(matrizDocConfigEnc);
			em.flush();
			
			for (int i = 0; i < matrizDocConfigGruposList.size(); i++) {
				MatrizDocConfigGrupos grupo = new MatrizDocConfigGrupos();
				grupo = matrizDocConfigGruposList.get(i);
				grupo.setActivo(true);
				grupo.setFechaAlta(new Date());
				grupo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				if(esSimplificado)
				{
					grupo.setAdjudicacion(true);
					grupo.setEvaluacionDoc(false);
				}
					
				grupo.setMatrizDocConfigEnc(matrizDocConfigEnc);
				em.persist(grupo);
				em.flush();
				
				List<MatrizDocConfigDet> listaDet = new ArrayList<MatrizDocConfigDet>();
				
				for(MatrizDocConfigDet det: this.matrizDocConfigDetList){
					if(grupo != null && det.getMatrizDocConfigGrupos()!= null){
						if(grupo.getNroOrden() == det.getMatrizDocConfigGrupos().getNroOrden())
							det.setMatrizDocConfigGrupos(grupo);
											
					}
					listaDet.add(det);
				}
				this.matrizDocConfigDetList = listaDet;
			

			}
			
			for (int i = 0; i < matrizDocConfigDetList.size(); i++) {
				MatrizDocConfigDet det = new MatrizDocConfigDet();
				det = matrizDocConfigDetList.get(i);
				det.setActivo(true);
				det.setNroOrden(i + 1);
				det.setFechaAlta(new Date());
				det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				if(esSimplificado && !det.getAgrupado())
				{
					det.setAdjudicacion(true);
					det.setEvaluacionDoc(false);
				}
				
				if(det.getAgrupado()){
					det.setObligatorio(null);
					det.setAdjudicacion(null);
					det.setEvaluacionDoc(null);
				}
					
				det.setMatrizDocConfigEnc(matrizDocConfigEnc);
				em.persist(det);
				em.flush();

			}
			
			

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String update() {
		try {
			
			if (selectedRowEdit != -1) {
				MatrizDocConfigDet det = new MatrizDocConfigDet();
				det = matrizDocConfigDetList.get(selectedRowEdit);
				if(!det.getAgrupado()){
					if((det.getEvaluacionDoc()==null|| !det.getEvaluacionDoc())&& 
							(det.getAdjudicacion()==null || !det.getAdjudicacion())){
							statusMessages.add(Severity.ERROR,"Debe seleccionar Evaluacion Documental o de Adjudicados. Verifique");
							return null;
						}
						if((det.getEvaluacionDoc()!=null&& det.getEvaluacionDoc())&& 
								(det.getAdjudicacion()!=null && det.getAdjudicacion())){
							statusMessages.add(Severity.ERROR,"No puede evaluar el mismo item en dos actividades. Verifique");
							return null;
						}
				}	
			}
			if(!tieneObligatorios())
			{
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"No se puede actualizar el Registro Debe poser:  al menos uno como Obligatorio para Eval. Documenta y otro para Adjudicaci&oacute;n"); 
				return null;
			}
			
			//SE INACTIVAN LOS REGISTROS QUE SE ELIMINAN
			for (int i = 0; i < matDocConfigDetListRemove.size(); i++) {
				MatrizDocConfigDet configDet =
					em.find(MatrizDocConfigDet.class, matDocConfigDetListRemove.get(i).getIdMatrizDocConfigDet());
				configDet.setActivo(false);
				configDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				configDet.setFechaMod(new Date());
				em.merge(configDet);
				em.flush();

			}
			
			for (int i = 0; i < matDocConfigGruposListRemove.size(); i++) {
				MatrizDocConfigGrupos configGrupos =
					em.find(MatrizDocConfigGrupos.class, matDocConfigGruposListRemove.get(i).getIdMatrizDocConfigGrupos());
				configGrupos.setActivo(false);
				configGrupos.setUsuMod(usuarioLogueado.getCodigoUsuario());
				configGrupos.setFechaMod(new Date());
				em.merge(configGrupos);
				em.flush();

			}
			
			//SI SE ELIMINO SE RECALCULA EL NRO_ORDEN
			if(!matDocConfigDetListRemove.isEmpty()){
				for (int i = 0; i < matrizDocConfigDetList.size(); i++) {
					MatrizDocConfigDet det = new MatrizDocConfigDet();
					det = matrizDocConfigDetList.get(i);
					det.setNroOrden(i + 1);
					em.merge(det);
					em.flush();
				}
			}
			
			if(!matDocConfigGruposListRemove.isEmpty()){
				for (int i = 0; i < matrizDocConfigGruposList.size(); i++) {
					MatrizDocConfigGrupos grupo = new MatrizDocConfigGrupos();
					grupo = matrizDocConfigGruposList.get(i);
					grupo.setNroOrden(i + 1);
					em.merge(grupo);
					em.flush();
				}
			}
			
			
			if (selectedRowEdit != -1) {
				MatrizDocConfigDet det = new MatrizDocConfigDet();
				det = matrizDocConfigDetList.get(selectedRowEdit);
				det.setActivo(true);
				det.setFechaMod(new Date());
				det.setUsuMod(usuarioLogueado.getCodigoUsuario());
				if(det.getAgrupado()){
					det.setObligatorio(null);
					det.setAdjudicacion(null);
					det.setEvaluacionDoc(null);
				}
				em.merge(det);
				em.flush();
			}

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void eliminar(int index) {
		try {
			if (matrizDocConfigDetList.size() == 1) {
				statusMessages.add(Severity.WARN, "No se puede eliminar el registro. La matriz debe tener al menos un ítem. Verifique");
				return;
			}
			setSelectedRowEdit(-1);
			MatrizDocConfigDet configDet = new MatrizDocConfigDet();
			configDet = matrizDocConfigDetList.get(index);
			matrizDocConfigDetList.remove(index);
			matDocConfigDetListRemove.add(configDet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void eliminarGrupo(int index) {
		try {
			
			setSelectedRowEditGrupos(-1);
			MatrizDocConfigGrupos grupo = new MatrizDocConfigGrupos();
			grupo = matrizDocConfigGruposList.get(index);
			matrizDocConfigGruposList.remove(index);
			matDocConfigGruposListRemove.add(grupo);
			
			//Habilitar los detalles para su edición
			
			List<MatrizDocConfigDet> listaDet = new ArrayList<MatrizDocConfigDet>();
			
			for(MatrizDocConfigDet det: this.matrizDocConfigDetList){
				if(grupo != null && det.getMatrizDocConfigGrupos()!= null){
					if(grupo == det.getMatrizDocConfigGrupos())
						det.setMatrizDocConfigGrupos(null);
						
					
				}
				listaDet.add(det);
			}
			this.matrizDocConfigDetList = listaDet;
			
			//Reordenar los Nro. de Grupo
			
			List<MatrizDocConfigGrupos> listaGrupos = new ArrayList<MatrizDocConfigGrupos>();
			for (MatrizDocConfigGrupos gr : this.matrizDocConfigGruposList){
				gr.setNroOrden(listaGrupos.size()+1);
				listaGrupos.add(gr);
			}
			
			this.matrizDocConfigGruposList = listaGrupos;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	public void agregarGrupo(){
					
		if(puedeAgrupar()){
			MatrizDocConfigGrupos grupo = new MatrizDocConfigGrupos();
			
			grupo.setNroOrden(matrizDocConfigGruposList.size()+1);
			
			for(MatrizDocConfigDet det : matrizDocConfigDetList){
				if(det.getAgrupado() && (det.getMatrizDocConfigGrupos() == null ))
					det.setMatrizDocConfigGrupos(grupo);
			}
			
			matrizDocConfigGruposList.add(grupo);
		}
		
	}
	
	private boolean puedeAgrupar(){
		boolean retorno = false;
			
			//recorrer la lista de detalles y ver si alguno tiene true aprobar pero nulo el grupo
		for(MatrizDocConfigDet det : this.matrizDocConfigDetList){
			if(det.getAgrupado() && det.getMatrizDocConfigGrupos() == null)
				retorno = true;
		}
		
			
		return retorno;
	}
	
	
	@SuppressWarnings("unchecked")
	public void seleccionar(int index) {
		if (!matrizDocConfigDetList.isEmpty()) {
			matrizDocumentalDetList = new ArrayList<MatrizDocumentalDet>();
			matrizDocConfigDetList = new ArrayList<MatrizDocConfigDet>();
		}
		setSelectedRow(index);
		matrizDocumentalDetList =
			em.createQuery("select mdDet from MatrizDocumentalDet mdDet "
				+ " where mdDet.matrizDocumentalEnc.idMatrizDocumentalEnc="
				+ matrizDocumentalEncList.get(index).getIdMatrizDocumentalEnc()
				+ " and mdDet.activo=TRUE order by mdDet.nroOrden asc").getResultList();

	}

	public void enviarItems(int index) {
		MatrizDocumentalDet mdDet = matrizDocumentalDetList.get(index);
		MatrizDocConfigDet documentalDet = new MatrizDocConfigDet();
		documentalDet.setActivo(true);
		documentalDet.setDatosEspecificos(mdDet.getDatosEspecificos());
		documentalDet.setFechaAlta(new Date());
		documentalDet.setNroOrden(mdDet.getNroOrden());
		documentalDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		documentalDet.setMatrizDocumentalDet(mdDet);
		matrizDocConfigDetList.add(documentalDet);
		matrizDocumentalDetList.remove(index);
		
	}

	public void enviarTodosItems() {
		
		for (int i = 0; i < matrizDocumentalDetList.size(); i++) {
			MatrizDocumentalDet mdDet = matrizDocumentalDetList.get(i);
			MatrizDocConfigDet documentalDet = new MatrizDocConfigDet();
			documentalDet.setActivo(true);
			documentalDet.setDatosEspecificos(mdDet.getDatosEspecificos());
			documentalDet.setFechaAlta(new Date());
			documentalDet.setNroOrden(mdDet.getNroOrden());
			documentalDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			documentalDet.setMatrizDocumentalDet(mdDet);
			matrizDocConfigDetList.add(documentalDet);
		}
		matrizDocumentalDetList.clear();
	}
	

	public void enviarPlantilla(int index) {
		MatrizDocumentalDet mdDet = matrizDocConfigDetList.get(index).getMatrizDocumentalDet();
		matrizDocumentalDetList.add(mdDet);
		matrizDocConfigDetList.remove(index);

	}

	
	
	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	public String getRowEditClass(int currentRow) {
		if (selectedRowEdit == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	public Boolean mostrarLink(Long idMatEnc) {
		if (cacheMostrarLinksMatDocumental.get(idMatEnc.toString()) != null) {
			return cacheMostrarLinksMatDocumental.get(idMatEnc.toString());
		}
		return false;
	}

	// METODOS PRIVADOS
	
	private boolean seleccionOk(){
		if(esSimplificado)
			return true;
		
		for (int i = 0; i < matrizDocConfigDetList.size(); i++) {
				int ordenTmp=i+1;
				
			if(!matrizDocConfigDetList.get(i).getAgrupado()){	
				if((matrizDocConfigDetList.get(i).getEvaluacionDoc()==null|| !matrizDocConfigDetList.get(i).getEvaluacionDoc())&& 
					(matrizDocConfigDetList.get(i).getAdjudicacion()==null || !matrizDocConfigDetList.get(i).getAdjudicacion())){
					statusMessages.add(Severity.ERROR,"Debe seleccionar Evaluacion Documental o de Adjudicados. Verifique el Nro de Orden "+ordenTmp);
					return false;
				}
				if((matrizDocConfigDetList.get(i).getEvaluacionDoc()!=null&& matrizDocConfigDetList.get(i).getEvaluacionDoc())&& 
						(matrizDocConfigDetList.get(i).getAdjudicacion()!=null && matrizDocConfigDetList.get(i).getAdjudicacion())){
					statusMessages.add(Severity.ERROR,"No puede evaluar el mismo item en dos actividades. Verifique el Nro de Orden "+ordenTmp);
					return false;
				}
			}
			
			
			
		}
		
			
		
			
		return tieneObligatorios();
	}
	
	
	private boolean tieneObligatorios(){
		boolean selObligatorioEv=false;
		boolean selObligatorioAd=false;
		
		for (MatrizDocConfigDet mConfigDet : matrizDocConfigDetList) {
			if(!mConfigDet.getAgrupado()){
				if(mConfigDet.getObligatorio()&& mConfigDet.getEvaluacionDoc())
					selObligatorioEv=true;
				if(mConfigDet.getObligatorio()&& mConfigDet.getAdjudicacion())
					selObligatorioAd=true;
			}else{
				if(mConfigDet.getMatrizDocConfigGrupos().getObligatorio()&& mConfigDet.getMatrizDocConfigGrupos().getEvaluacionDoc())
					selObligatorioEv=true;
//				if(mConfigDet.getMatrizDocConfigGrupos().getObligatorio()&& mConfigDet.getMatrizDocConfigGrupos().getAdjudicacion())
//					selObligatorioAd=true;
			}
			
			
		}
		if(!esSimplificado){
			if (!selObligatorioEv) {
				statusMessages
						.add(Severity.ERROR,
								"Debe Seleccionar al menos uno como Obligatorio para Eval. Documental");
				return false;
			}
		}
		if (!selObligatorioAd) {
				statusMessages
						.add(Severity.ERROR,
								"Debe Seleccionar al menos uno como Obligatorio para Adjudicaci&oacute;n");
				return false;

		}	
		
		
		return true;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void cargarItems1Config() {
		matrizDocumentalEncList =
			em.createQuery("Select mdEnc from  MatrizDocumentalEnc mdEnc "
				+ " where mdEnc.datosEspecificos.idDatosEspecificos="
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + ""
				+ " and mdEnc.activo=TRUE order by mdEnc.fechaAlta desc").getResultList();

		// Completar el cache de mostrar link
		cacheMostrarLinksMatDocumental.clear();
		for (MatrizDocumentalEnc o : matrizDocumentalEncList) {
			cacheMostrarLinksMatDocumental.put(o.getIdMatrizDocumentalEnc().toString(),detActivos(o.getIdMatrizDocumentalEnc()));
		}
	}
	
	//SI TIENE DETALLES Y SON ACTIVOS RETORNA TRUE
	@SuppressWarnings("unchecked")
	private boolean detActivos(Long id){
		List<MatrizDocumentalDet> det=
			em.createQuery("select mdDet from MatrizDocumentalDet mdDet "
				+ " where mdDet.matrizDocumentalEnc.idMatrizDocumentalEnc="+id
				+ " and mdDet.activo=TRUE order by mdDet.nroOrden asc").getResultList();
		if(det.isEmpty())
			return false;
		
		return true;
	}

	@SuppressWarnings("unchecked")
	private void findDets() {
		matrizDocConfigDetList =
			em.createQuery("select det from MatrizDocConfigDet det "
				+ " where det.matrizDocConfigEnc.idMatrizDocConfigEnc ="
				+ matrizDocConfigDet.getMatrizDocConfigEnc().getIdMatrizDocConfigEnc() + ""
				+ " and det.activo=true " + " order by det.nroOrden").getResultList();

	}
	
	@SuppressWarnings("unchecked")
	private void findGrupos() {
		matrizDocConfigGruposList =
			em.createQuery("select grupos from MatrizDocConfigGrupos grupos "
				+ " where grupos.matrizDocConfigEnc.idMatrizDocConfigEnc ="
				+ matrizDocConfigDet.getMatrizDocConfigEnc().getIdMatrizDocConfigEnc() + ""
				+ " and grupos.activo=true " + " order by grupos.nroOrden").getResultList();

	}


	private void filaSeleccionada() {
		for (int i = 0; i < matrizDocConfigDetList.size(); i++) {
			if (matrizDocConfigDetList.get(i).getIdMatrizDocConfigDet().longValue() == matrizDocConfigDet.getIdMatrizDocConfigDet().longValue()) {
				selectedRowEdit = i;
				break;
			}

		}
	}

	// GETTERS Y SETTERS
	public Long getIdMatrizDocConfigDet() {
		return idMatrizDocConfigDet;
	}

	public void setIdMatrizDocConfigDet(Long idMatrizDocConfigDet) {
		this.idMatrizDocConfigDet = idMatrizDocConfigDet;
	}

	public List<MatrizDocumentalDet> getMatrizDocumentalDetList() {
		return matrizDocumentalDetList;
	}

	public void setMatrizDocumentalDetList(List<MatrizDocumentalDet> matrizDocumentalDetList) {
		this.matrizDocumentalDetList = matrizDocumentalDetList;
	}

	public List<MatrizDocumentalEnc> getMatrizDocumentalEncList() {
		return matrizDocumentalEncList;
	}

	public void setMatrizDocumentalEncList(List<MatrizDocumentalEnc> matrizDocumentalEncList) {
		this.matrizDocumentalEncList = matrizDocumentalEncList;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public List<MatrizDocConfigDet> getMatrizDocConfigDetList() {
		return matrizDocConfigDetList;
	}

	public void setMatrizDocConfigDetList(List<MatrizDocConfigDet> matrizDocConfigDetList) {
		this.matrizDocConfigDetList = matrizDocConfigDetList;
	}

	public int getSelectedRowEdit() {
		return selectedRowEdit;
	}

	public void setSelectedRowEdit(int selectedRowEdit) {
		this.selectedRowEdit = selectedRowEdit;
	}

	public MatrizDocConfigDet getMatrizDocConfigDet() {
		return matrizDocConfigDet;
	}

	public void setMatrizDocConfigDet(MatrizDocConfigDet matrizDocConfigDet) {
		this.matrizDocConfigDet = matrizDocConfigDet;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public boolean isEsSimplificado() {
		return esSimplificado;
	}

	public void setEsSimplificado(boolean esSimplificado) {
		this.esSimplificado = esSimplificado;
	}

	public List<MatrizDocConfigGrupos> getMatrizDocConfigGruposList() {
		return matrizDocConfigGruposList;
	}

	public void setMatrizDocConfigGruposList(
			List<MatrizDocConfigGrupos> matrizDocConfigGruposList) {
		this.matrizDocConfigGruposList = matrizDocConfigGruposList;
	}
	
	public int getSelectedRowEditGrupos() {
		return selectedRowEditGrupos;
	}

	public void setSelectedRowEditGrupos(int selectedRowEditGrupos) {
		this.selectedRowEditGrupos = selectedRowEditGrupos;
	}
	

}
