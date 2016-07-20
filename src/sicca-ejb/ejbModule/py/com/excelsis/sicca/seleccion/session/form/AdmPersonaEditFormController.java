package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admPersonaEditFormController")
public class AdmPersonaEditFormController implements
		Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	PersonaHome personaHome;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	
	private Persona persona;
	private Long idPersona;
	private Long idPersonaFamiliar;
	
	private String from;

	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;

	private List<SelectItem> departamentosDirSelecItem;
	private List<SelectItem> ciudadDirSelecItem;
	private List<SelectItem> localidadDirSelecItem;

	private Long idSexo;
	private Long idEstadoCivil;
	private Long idPaisNac;
	private Long idDptoNac;
	private Long idCiudadNac;
	private Long idPaisDir;
	private Long idDptoDir;
	private Long idCiudadDir;
	private Long idLocalidadDir;
	private String dptoNro;
	private Long idNacionalidad;
	
	private Long idPaisExpe;
	private boolean habComExp;
	private PersonaDTO personaDTO;
	private boolean habCamposPersona=true;// que campos habilitar y que no
	private String documentoCi=null;
	private boolean permitirGuardar=true;;
	/**
	 * AGREGADO PARA LA PANTALLA MOTIVO
	 * **/
	private Long idMotivo;
	private Boolean estado;
	/**
	 * **/
	
	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		
		
			
			persona = new Persona();
			persona.setActivo(true);
			if(idPersona!=null){
				cargarDatosPersona(idPersona);
				/**
				 * a.	Si la Persona es de PARAGUAY
				 * */
				if(idPaisExpe.longValue()==idParaguay().longValue()){
					habCamposPersona=false;
				}else{
					habCamposPersona=true;
				}
			} else {
				habComExp=true;
				idSexo = null;
				idEstadoCivil = null;
				idCiudadNac = null;
				idDptoNac = null;
				idPaisNac = null;
				idPaisDir = null;
				idPaisExpe=idParaguay();
				idDptoDir = null;
				idCiudadDir = null;
				idLocalidadDir = null;
				updateDepartamentoDir();
				updateCiudadDir();
				updateBarrio();
				updateCiudad();
				updateDepartamento();
				documentoCi=null;
				persona.setActivo(true);
				
			}
			personaHome.setInstance(persona);
		}
		
	@SuppressWarnings("unchecked")
	private Long idParaguay(){
			List<Pais> p= em.createQuery(" Select p from Pais p" +
					" where lower(p.descripcion) like 'paraguay'").getResultList();
			if(!p.isEmpty())
				return p.get(0).getIdPais();
			
			return null;
	}

	@SuppressWarnings("unchecked")
	private Long searchSexo(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'SEXO' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	@SuppressWarnings("unchecked")
	private Long searchEstadoCivil(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	/**
	 * Combo Departamento Nacimiento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDptoNac = null;
		idCiudadNac = null;

	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPaisNac != null) {
			departamentoList.getPais().setIdPais(idPaisNac);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Nacimiento
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudadNac = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDptoNac != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoNac);
			ciudadList.setMaxResults(null);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Combo Departamento Direccion
	 */
	public void updateDepartamentoDir() {
		List<Departamento> dptoList = getDepartamentosDirByPais();
		departamentosDirSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
		idDptoDir = null;
		idCiudadDir = null;
		idLocalidadDir = null;
	}

	private List<Departamento> getDepartamentosDirByPais() {
		if (idPaisDir != null) {
			departamentoList.getPais().setIdPais(idPaisDir);
			departamentoList.setOrder("descripcion");
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoDirSelectItem(List<Departamento> dptoList) {
		if (departamentosDirSelecItem == null)
			departamentosDirSelecItem = new ArrayList<SelectItem>();
		else
			departamentosDirSelecItem.clear();

		departamentosDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosDirSelecItem.add(new SelectItem(dep
					.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Direccion
	 */
	public void updateCiudadDir() {
		List<Ciudad> ciuList = getCiudadByDptoDir();
		ciudadDirSelecItem = new ArrayList<SelectItem>();
		buildCiudadDirSelectItem(ciuList);
		idCiudadDir = null;
		idLocalidadDir = null;
	}

	private List<Ciudad> getCiudadByDptoDir() {
		if (idDptoDir != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoDir);
			ciudadList.setMaxResults(null);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadDirSelectItem(List<Ciudad> ciudadList) {
		if (ciudadDirSelecItem == null)
			ciudadDirSelecItem = new ArrayList<SelectItem>();
		else
			ciudadDirSelecItem.clear();

		ciudadDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadDirSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		localidadDirSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idLocalidadDir = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudadDir != null) {
			barrioList.setIdCiudad(idCiudadDir);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.listaPorCiudad();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (localidadDirSelecItem == null)
			localidadDirSelecItem = new ArrayList<SelectItem>();
		else
			localidadDirSelecItem.clear();

		localidadDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			localidadDirSelecItem.add(new SelectItem(bar.getIdBarrio(), bar
					.getDescripcion()));
		}
	}

	@SuppressWarnings("unchecked")
	public String save() {
		if(idPaisExpe==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe seleccionar el País de Expedición. Verifique");
			return null;
		}
		
		if(!permitirGuardar){
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			return null;
		}
		if(personaDTO==null){
			statusMessages.add(Severity.ERROR,"No se puede realizar la operacion");
			return null;
		}
		if(!personaDTO.getEstado().equals("NUEVO")&&persona.getIdPersona()==null ){
			if(idPaisExpe.longValue()==idParaguay().longValue()){
				statusMessages.add(Severity.ERROR,"No se puede realizar la operacion");
				return null;
			}
			
		}
		
		if(documentoCi==null ||documentoCi.trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Documento de Identidad. Verifique");
			return null;
		}
		if(!validarDocIdentidad(documentoCi)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Documento Identidad inválido. Verifique");
			return null;
		}
		persona.setDocumentoIdentidad(documentoCi);
		
		if(persona.getNombres().trim().equals("") ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Nombre. Verifique");
			return null;
		}
		if( persona.getApellidos().trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Apellido. Verifique");
			return null;
		}
		/**
		 * 	Si el País Exp. Documento = ‘Paraguay’: validar que la persona no exista en el SICCA 
		 * (tener en cuenta los activos e inactivos para realizar este chequeo)
		 * SINO
		 * 	Si el País Exp. Documento <> ‘Paraguay’: validar que la persona no exista en el SICCA 
		 * (tener en cuenta solo los activos para realizar este chequeo)
		 * */
		List<Persona> perExiste= new ArrayList<Persona>();
		if(idPaisExpe.longValue()==General.getIdParaguay().longValue()){
			if(persona.getIdPersona()==null){//de modo guardar
				perExiste=em.createQuery("Select p from Persona p " +
						" where p.documentoIdentidad = '"
						+persona.getDocumentoIdentidad()+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPaisExpe).getResultList();
				
			
			}
			
		}else{
			if(persona.getIdPersona()==null){//de modo guardar
				perExiste=em.createQuery("Select p from Persona p " +
						" where p.documentoIdentidad = '"
						+persona.getDocumentoIdentidad()+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPaisExpe+"  and p.activo=true").getResultList();
			
			
			}
		}
		if(!perExiste.isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"La Persona ingresada ya existe. Verifique");
			return null;
		}
		
		

		if(compareDate(persona.getFechaNacimiento(), new Date()) >= 0){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "La fecha de nacimiento no puede ser mayor o igual a la fecha actual. Verifique ");
			return null;
		}
		if( !General.isFechaValida(persona.getFechaNacimiento())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Nacimiento inválida. Verifique");
			return null;
		}
		if(from != null){
			if(!from.equalsIgnoreCase("/legajos/AdmDatosFamilia/AdmDatosFamilia644")){
				if(!validarFechasNac(persona.getFechaNacimiento())){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "El rango de edades debe comprender entre "+edadMinima()+" y "+edadMaxima());
					return null;
				}
				
				if(persona.getEMail() == null || persona.getEMail().trim().isEmpty()){
					statusMessages.add(Severity.ERROR,"Email Personal no ingresado.");
					return null;
				}
				
				if(persona.getEMailFuncionario() == null || persona.getEMailFuncionario().trim().isEmpty()){
					statusMessages.add(Severity.ERROR,"Email Funcionario no ingresado.");
					return null;
				}
				
				if(!General.isEmail(persona.getEMail().toLowerCase()))
				{
					statusMessages.add(Severity.ERROR,"Ingrese un mail valido. Verifique");
					return null;
				}
			}
		}
		
		
		persona.setEMail(persona.getEMail().toLowerCase());
		try {
			if (idLocalidadDir != null)
				persona.setBarrio(em.find(Barrio.class, idLocalidadDir));
			if (idCiudadDir != null)
				persona.setCiudadByIdCiudadDirecc(em.find(Ciudad.class,
						idCiudadDir));
			if (idCiudadNac != null)
				persona.setCiudadByIdCiudadNac(em.find(Ciudad.class,
						idCiudadNac));
			if (idEstadoCivil != null)
				persona.setEstadoCivil(em
						.find(Referencias.class, idEstadoCivil).getDescAbrev());
			if (idSexo != null)
				persona.setSexo(em.find(Referencias.class, idSexo)
						.getDescAbrev());
				persona.setApellidos(persona.getApellidos().trim());
			if(idPaisExpe!=null)
				persona.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, idPaisExpe));
			persona.setDatosEspecificos(em.find(DatosEspecificos.class, idNacionalidad));
		
			persona.setActivo(true);
			
			if(persona.getIdPersona()==null){
				persona.setFechaAlta(new Date());
				persona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(persona);
				
			}else{
				persona.setFechaMod(new Date());
				persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(persona);
			}
				
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			if(from.equalsIgnoreCase("/legajos/AdmDatosFamilia/AdmDatosFamilia644")){
				return "/legajos/MenuDatosPersonales.seam" + "?texto=GShow644&accion=EDITAR&personaIdPersona="+idPersonaFamiliar+"&personaIdFamiliar="+persona.getIdPersona();
			}
			
			if (from != null && !"".equals(from)){
				return from+".seam" + "?personaIdPersona=" + persona.getIdPersona();
			}
			
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}
	
	
	private boolean validarFechasNac(Date fecha){
		try {
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			Integer anioActual=Integer.parseInt(sdfSoloAnio.format(new Date())) ;
			Integer anioNacimiento=Integer.parseInt(sdfSoloAnio.format(fecha)) ;
			Integer edad= anioActual-anioNacimiento;
			if(edad<=edadMaxima()&& edad>=edadMinima())
				return true;
	
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private boolean validarDocIdentidad(String doc){
		 String patron ="^[a-zA-Z0-9]+$";
	        Pattern p = Pattern.compile(patron);
	        Matcher  matcher = p.matcher(doc);
	        if(!matcher.matches())
	            return false;
	       return true;
	}
	
	@SuppressWarnings("unchecked")
	private Integer edadMaxima(){
		List<Referencias> maxima= em.createQuery("Select m from Referencias m " +
				" where m.dominio='EDAD_MAXIMA_LEGAL' ").getResultList();
		if(!maxima.isEmpty())
			return maxima.get(0).getValorNum();
		return 60;//en caso que no exista dato 
	}
	@SuppressWarnings("unchecked")
	private Integer edadMinima(){
		List<Referencias> minima= em.createQuery("Select m from Referencias m " +
				" where m.dominio='EDAD_MINIMA_LEGAL' ").getResultList();
		if(!minima.isEmpty())
			return minima.get(0).getValorNum();
		return 18;//en caso que no exista dato 
	}
	@SuppressWarnings("unchecked")
	public String update() {
		if(idPaisExpe==null){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe seleccionar el País de Expedición. Verifique");
			return null;
		}
		if(documentoCi==null ||documentoCi.trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Documento de Identidad. Verifique");
			return null;
		}
		if(!validarDocIdentidad(documentoCi)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Documento Identidad inválido. Verifique");
			return null;
		}
		persona.setDocumentoIdentidad(documentoCi);
		if(persona.getNombres().trim().equals("") ){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Nombre. Verifique");
			return null;
		}
		if( persona.getApellidos().trim().equals("")){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Apellido. Verifique");
			return null;
		}
		if(compareDate(persona.getFechaNacimiento(), new Date()) >= 0){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "La fecha de nacimiento no puede ser mayor o igual a la fecha actual. Verifique ");
			return null;
		}
		if( !General.isFechaValida(persona.getFechaNacimiento())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Nacimiento inválida. Verifique");
			return null;
		}
		
		if(!validarFechasNac(persona.getFechaNacimiento())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "El rango de edades debe comprender entre "+edadMinima()+" y "+edadMaxima());
			return null;
		}
//		if(!persona.getActivo() && !inactivar()){
//			statusMessages.add("No se puede inactivar el registro, Esta siendo utilizado");
//			return null;
//		}
		if(persona.getIdPersona()==null){
			List<Persona> per=em.createQuery("Select p from Persona p " +
					" where p.documentoIdentidad = '"
					+persona.getDocumentoIdentidad()+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPaisExpe).getResultList();
			if(!per.isEmpty()){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,"La Persona ingresada ya existe. Verifique");
				return null;
			}
		
		}
		
		try {
			if (idLocalidadDir != null)
				persona.setBarrio(em.find(Barrio.class, idLocalidadDir));
			if (idCiudadDir != null)
				persona.setCiudadByIdCiudadDirecc(em.find(Ciudad.class,
						idCiudadDir));
			if (idCiudadNac != null)
				persona.setCiudadByIdCiudadNac(em.find(Ciudad.class,
						idCiudadNac));
			if (idEstadoCivil != null)
				persona.setEstadoCivil(em
						.find(Referencias.class, idEstadoCivil).getDescAbrev());
			if (idSexo != null)
				persona.setSexo(em.find(Referencias.class, idSexo)
						.getDescAbrev());
				persona.setApellidos(persona.getApellidos().trim());
			if(idPaisExpe!=null)
					persona.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, idPaisExpe));
			persona.setDatosEspecificos(em.find(DatosEspecificos.class, idNacionalidad));
		
			persona.setFechaMod(new Date());
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(persona);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			if (from != null && !"".equals(from)){
				return from+".seam" + "?personaIdPersona=" + persona.getIdPersona();
			}
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}
		
	public String saveMotivo(){
		try {
			if(persona.getObsMotivo().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Motivo. Verifique");
				return null;
			}
			if(estado==persona.getActivo()){
				statusMessages.add(Severity.ERROR,"Debe modificar el estado del registro. Verifique");
				return null;
			}


			persona.setDatosEspecificosMotivo(em.find(DatosEspecificos.class, idMotivo));
			persona.setActivo(estado);
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona.setFechaMod(new Date());
			em.merge(persona);
			em.flush();
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
//	private boolean inactivar(){
//		if(!persona.getEmpleados().isEmpty()|| !persona.getUsuarios().isEmpty() || !persona.getEmpresaPersonas().isEmpty()
//			|| !persona.getExperienciaLaborals().isEmpty() || !persona.getEmpresaPersonas().isEmpty()){
//			return false;
//		}
//		return true;
//	}
	public int compareDate(Date d1, Date d2) {
	    if (d1.getYear() != d2.getYear()) 
	        return d1.getYear() - d2.getYear();
	    if (d1.getMonth() != d2.getMonth()) 
	        return d1.getMonth() - d2.getMonth();
	    return d1.getDate() - d2.getDate();
	}

	
	/**
	 * nacionalidad
	 * */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		List<Object[]> n=em.createNativeQuery("Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r," +
				"seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales"
				+ " AND de.activo " +
				" Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();
		
		 while(it.hasNext()){
			 Object[] fila = it.next();
			 if(fila[0]!=null)
				 si.add(new SelectItem(fila[0],fila[1]!=null?fila[1].toString():""));
		 }
			
		return si;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public void seleccionoExDoc(){
		if(idPaisExpe==null){
			statusMessages.add("Debe completar previamente el país de expedición de documento");
			return ;
		}else{
			String ci=persona.getDocumentoIdentidad();
			if(ci!=null && !ci.trim().equals("")){
				habComExp=false;
			}else
				habComExp=true;
			List<Persona> personas=em.createQuery("Select p from Persona p " +
						" where p.documentoIdentidad = :doc and p.paisByIdPaisExpedicionDoc.idPais="+idPaisExpe).setParameter("doc",persona.getDocumentoIdentidad()) .getResultList();
				if(!personas.isEmpty()){
					persona=personas.get(0);
					dptoNro = persona.getDepartamentoNro();
					idSexo = searchSexo(persona.getSexo());
					idEstadoCivil = searchEstadoCivil(persona.getEstadoCivil());
					if (persona.getCiudadByIdCiudadNac() != null) {
						idPaisNac = persona.getCiudadByIdCiudadNac()
								.getDepartamento().getPais().getIdPais();
						updateDepartamento();
						idDptoNac = persona.getCiudadByIdCiudadNac()
								.getDepartamento().getIdDepartamento();
						updateCiudad();
						idCiudadNac = persona.getCiudadByIdCiudadNac()
								.getIdCiudad();
					} else {
						updateDepartamento();
						updateCiudad();
					}
					
					if(persona.getPaisByIdPaisExpedicionDoc()!=null)
						idPaisExpe=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
					if(persona.getDatosEspecificos()!=null)
						idNacionalidad=persona.getDatosEspecificos().getIdDatosEspecificos();
					if (persona.getCiudadByIdCiudadDirecc() != null) {
						idPaisDir = persona.getCiudadByIdCiudadDirecc()
								.getDepartamento().getPais().getIdPais();

						updateDepartamentoDir();
						idDptoDir = persona.getCiudadByIdCiudadDirecc()
								.getDepartamento().getIdDepartamento();
						updateCiudadDir();
						idCiudadDir = persona.getCiudadByIdCiudadDirecc()
								.getIdCiudad();
						updateBarrio();
						if (persona.getBarrio() != null)

							idLocalidadDir = persona.getBarrio().getIdBarrio();
						else
							idLocalidadDir = null;

					} else {
						updateDepartamentoDir();
						updateCiudadDir();
						updateBarrio();
					}
				}else{
					persona=new Persona();
					idSexo = null;
					idEstadoCivil = null;
					idCiudadNac = null;
					idDptoNac = null;
					idPaisNac = null;
					idPaisDir = null;
					//idPaisExpe = idParaguay();
					idDptoDir = null;
					idCiudadDir = null;
					idLocalidadDir = null;
					idNacionalidad=null;
					updateDepartamentoDir();
					updateCiudadDir();
					updateBarrio();
					updateCiudad();
					updateDepartamento();
					persona.setActivo(true);
				}
				persona.setDocumentoIdentidad(ci);
			//	personaHome.setInstance(persona);
			//}
		}
			
	}
	
	public void buscarPersona(){
		try {
			permitirGuardar=true;
			/* Validaciones */
			if (idPaisExpe == null
				|| !seguridadUtilFormController.validarInput(idPaisExpe.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			if (documentoCi == null || documentoCi.trim().equals("")
					|| !seguridadUtilFormController.validarInput(documentoCi, TiposDatos.STRING.getValor(), null)) {
					return;
				}
			Pais pais = em.find(Pais.class, idPaisExpe);
			if (pais == null)
				return;
			
			/* fin validaciones */
			personaDTO = seleccionUtilFormController.buscarPersona(documentoCi, pais.getDescripcion());
			habCamposPersona=true;
			if (personaDTO.getHabilitarBtn() == null) {
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
				limpiarDatosPersona();
				permitirGuardar=false;
			} else if (personaDTO.getHabilitarBtn()) {
				if(personaDTO.getEstado().equals("NUEVO")){
					persona=personaDTO.getPersona();
					idSexo = searchSexo(persona.getSexo());
					if(persona.getDatosEspecificos()!=null)
						idNacionalidad=persona.getDatosEspecificos().getIdDatosEspecificos();
					persona.setActivo(true);
					habCamposPersona=false;
				}else{
					limpiarDatosPersona();
				}
			}else{
				if(idPaisExpe.longValue()==idParaguay().longValue()){
					if(personaDTO.getPersona().getIdPersona()!=null){
						cargarDatosPersona(personaDTO.getPersona().getIdPersona());
						habCamposPersona=false;
					}
									
				}else{
					if(personaDTO.getPersona().getIdPersona()!=null){
						cargarDatosPersona(personaDTO.getPersona().getIdPersona());
						habCamposPersona=true;
					}else
						limpiarDatosPersona();
				}
					
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void limpiarDatosPersona() {
		persona= new Persona();
		idSexo = null;
		idEstadoCivil = null;
		idCiudadNac = null;
		idDptoNac = null;
		idPaisNac = null;
		idPaisDir = null;
		idDptoDir = null;
		idCiudadDir = null;
		idLocalidadDir = null;
		idNacionalidad=null;
		updateDepartamentoDir();
		updateCiudadDir();
		updateBarrio();
		updateCiudad();
		updateDepartamento();
		persona.setActivo(true);
		
		
	}
	private void cargarDatosPersona(Long idPersona){
		em.clear();
		persona = em.find(Persona.class, idPersona);
		documentoCi=persona.getDocumentoIdentidad();
		dptoNro = persona.getDepartamentoNro();
		idSexo = searchSexo(persona.getSexo());
		habComExp=false;
		idEstadoCivil = searchEstadoCivil(persona.getEstadoCivil());
		if(persona.getDatosEspecificosMotivo()!=null)
			idMotivo=persona.getDatosEspecificosMotivo().getIdDatosEspecificos();
		estado=persona.getActivo();
		if (persona.getCiudadByIdCiudadNac() != null) {
			idPaisNac = persona.getCiudadByIdCiudadNac()
					.getDepartamento().getPais().getIdPais();
			updateDepartamento();
			idDptoNac = persona.getCiudadByIdCiudadNac()
					.getDepartamento().getIdDepartamento();
			updateCiudad();
			idCiudadNac = persona.getCiudadByIdCiudadNac()
					.getIdCiudad();
		} else {
			updateDepartamento();
			updateCiudad();
		}
		if(persona.getPaisByIdPaisExpedicionDoc()!=null)
			idPaisExpe=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		if(persona.getDatosEspecificos()!=null)
			idNacionalidad=persona.getDatosEspecificos().getIdDatosEspecificos();
		if (persona.getCiudadByIdCiudadDirecc() != null) {
			idPaisDir = persona.getCiudadByIdCiudadDirecc()
					.getDepartamento().getPais().getIdPais();

			updateDepartamentoDir();
			idDptoDir = persona.getCiudadByIdCiudadDirecc()
					.getDepartamento().getIdDepartamento();
			updateCiudadDir();
			idCiudadDir = persona.getCiudadByIdCiudadDirecc()
					.getIdCiudad();
			updateBarrio();
			if (persona.getBarrio() != null)

				idLocalidadDir = persona.getBarrio().getIdBarrio();
			else
				idLocalidadDir = null;
			
		} else {
			updateDepartamentoDir();
			updateCiudadDir();
			updateBarrio();
		}
	}

	public void cambioPais(){
		persona=new Persona();
	 habCamposPersona=true;
		documentoCi=null;
		idSexo = null;
		idEstadoCivil = null;
		idCiudadNac = null;
		idDptoNac = null;
		idPaisNac = null;
		idPaisDir = null;
		idDptoDir = null;
		idCiudadDir = null;
		idLocalidadDir = null;
		idNacionalidad=null;
		updateDepartamentoDir();
		updateCiudadDir();
		updateBarrio();
		updateCiudad();
		updateDepartamento();
		persona.setActivo(true);
	}
	
	
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdSexo() {
		return idSexo;
	}

	public void setIdSexo(Long idSexo) {
		this.idSexo = idSexo;
	}

	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public Long getIdPaisNac() {
		return idPaisNac;
	}

	public void setIdPaisNac(Long idPaisNac) {
		this.idPaisNac = idPaisNac;
	}

	public Long getIdDptoNac() {
		return idDptoNac;
	}

	public void setIdDptoNac(Long idDptoNac) {
		this.idDptoNac = idDptoNac;
	}

	public Long getIdCiudadNac() {
		return idCiudadNac;
	}

	public void setIdCiudadNac(Long idCiudadNac) {
		this.idCiudadNac = idCiudadNac;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public List<SelectItem> getDepartamentosDirSelecItem() {
		return departamentosDirSelecItem;
	}

	public void setDepartamentosDirSelecItem(
			List<SelectItem> departamentosDirSelecItem) {
		this.departamentosDirSelecItem = departamentosDirSelecItem;
	}

	public List<SelectItem> getCiudadDirSelecItem() {
		return ciudadDirSelecItem;
	}

	public void setCiudadDirSelecItem(List<SelectItem> ciudadDirSelecItem) {
		this.ciudadDirSelecItem = ciudadDirSelecItem;
	}

	public Long getIdPaisDir() {
		return idPaisDir;
	}

	public void setIdPaisDir(Long idPaisDir) {
		this.idPaisDir = idPaisDir;
	}

	public Long getIdDptoDir() {
		return idDptoDir;
	}

	public void setIdDptoDir(Long idDptoDir) {
		this.idDptoDir = idDptoDir;
	}

	public Long getIdCiudadDir() {
		return idCiudadDir;
	}

	public void setIdCiudadDir(Long idCiudadDir) {
		this.idCiudadDir = idCiudadDir;
	}

	public Long getIdLocalidadDir() {
		return idLocalidadDir;
	}

	public void setIdLocalidadDir(Long idLocalidadDir) {
		this.idLocalidadDir = idLocalidadDir;
	}

	public List<SelectItem> getLocalidadDirSelecItem() {
		return localidadDirSelecItem;
	}

	public void setLocalidadDirSelecItem(List<SelectItem> localidadDirSelecItem) {
		this.localidadDirSelecItem = localidadDirSelecItem;
	}



	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}
	
	public Long getIdPersonaFamiliar() {
		return idPersonaFamiliar;
	}

	public void setIdPersonaFamiliar(Long idPersonaFamiliar) {
		this.idPersonaFamiliar = idPersonaFamiliar;
	}

	

	public String getDptoNro() {
		return dptoNro;
	}

	public void setDptoNro(String dptoNro) {
		this.dptoNro = dptoNro;
	}



	public Long getIdNacionalidad() {
		return idNacionalidad;
	}



	public void setIdNacionalidad(Long idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}



	

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Long getIdPaisExpe() {
		return idPaisExpe;
	}



	public void setIdPaisExpe(Long idPaisExpe) {
		this.idPaisExpe = idPaisExpe;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public boolean isHabComExp() {
		return habComExp;
	}

	public void setHabComExp(boolean habComExp) {
		this.habComExp = habComExp;
	}

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}

	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}

	public boolean isHabCamposPersona() {
		return habCamposPersona;
	}

	public void setHabCamposPersona(boolean habCamposPersona) {
		this.habCamposPersona = habCamposPersona;
	}

	public String getDocumentoCi() {
		return documentoCi;
	}

	public void setDocumentoCi(String documentoCi) {
		this.documentoCi = documentoCi;
	}

	public boolean isPermitirGuardar() {
		return permitirGuardar;
	}

	public void setPermitirGuardar(boolean permitirGuardar) {
		this.permitirGuardar = permitirGuardar;
	}
	
	@SuppressWarnings("unchecked")
	public String volverFrom() {
		if(from != null && !from.trim().isEmpty()){
			if(from.equalsIgnoreCase("/legajos/AdmDatosFamilia/AdmDatosFamilia644")){
				//return "/legajos/MenuDatosPersonales.xhtml?accion='EDITAR'&texto='GShow644'";
				return "/legajos/MenuDatosPersonales.xhtml";
			}
			else{
				return "/"+from+".xhtml";
			}
		}
		else{
			return "/seleccion/persona/PersonaList.xhtml";
		}
			
	}
}
