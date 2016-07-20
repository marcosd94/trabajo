package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("lugarPresentacionAclaracionFormController")
public class LugarPresentacionAclaracionFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;

	
	
	private Long idConcursoPuestoAgr;//recibe del CU que le llama
	private Long idConcurso;//recibe del CU que le llama
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;//enviado por el CU
	private Concurso concurso;
	private List<PresentAclaracDoc> presentacionDocs= new ArrayList<PresentAclaracDoc>();
	private List<PresentAclaracDoc> aclaracionDocs= new ArrayList<PresentAclaracDoc>();
	private PresentAclaracDoc presentacion= new PresentAclaracDoc();
	private PresentAclaracDoc aclaracion= new PresentAclaracDoc();
	private String desdeAcla;
	private String hastaAcla;
	private String desdePre;
	private String hastaPre;
	private boolean esEditPrese;
	private boolean esEditAclara;
	private int posPrese;
	private int posAcla;
	private Long idFromCU;
	General general;
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

	
	public void init(){
		if(idConcursoPuestoAgr!=null){
			concursoPuestoAgr= em.find(ConcursoPuestoAgr.class,idConcursoPuestoAgr);
			validarOee(concursoPuestoAgr.getConcurso());
			findPresentAclaracDocByGrupo();
			findEntidadesByGrupo();
		}else
			concursoPuestoAgr= null;
		if(idConcurso!=null){
			concurso= em.find(Concurso.class, idConcurso);
			validarOee(concurso);
			findPresentAclaracDocByConcurso();
			findEntidadesByConcurso();
		}else
			concurso=null;
		
			
		
		
	}
	

	@SuppressWarnings("unchecked")
	private void findEntidadesByConcurso(){
		configuracionUoCab=em.find(ConfiguracionUoCab.class, concurso.getConfiguracionUoCab().getIdConfiguracionUo()) ;
		if(configuracionUoCab.getEntidad()!=null){
			sinEntidad=em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin=em.createQuery("Select n from SinNivelEntidad n " +
					" where n.aniAniopre ="+sinEntidad.getAniAniopre() +" and n.nenCodigo="+sinEntidad.getNenCodigo()).getResultList();
			if(!sin.isEmpty())
				sinNivelEntidad=sin.get(0);
			
		}
	}
		@SuppressWarnings("unchecked")
		private void findEntidadesByGrupo(){
			configuracionUoCab=em.find(ConfiguracionUoCab.class, concursoPuestoAgr.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()) ;
			if(configuracionUoCab.getEntidad()!=null){
				sinEntidad=em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
				List<SinNivelEntidad> sin=em.createQuery("Select n from SinNivelEntidad n " +
						" where n.aniAniopre ="+sinEntidad.getAniAniopre() +" and n.nenCodigo="+sinEntidad.getNenCodigo()).getResultList();
				if(!sin.isEmpty())
					sinNivelEntidad=sin.get(0);
				
			}
		
	}
	
	@SuppressWarnings("unchecked")
	private void findPresentAclaracDocByGrupo(){
		presentacionDocs= em.createQuery("Select p from PresentAclaracDoc p " +
				" where p.tipoPA like 'P'  and p.activo=TRUE and p.concursoPuestoAgr.idConcursoPuestoAgr="+idConcursoPuestoAgr).getResultList();
		aclaracionDocs= em.createQuery("Select p from PresentAclaracDoc p " +
				" where p.tipoPA like 'A'  and p.activo=TRUE and p.concursoPuestoAgr.idConcursoPuestoAgr="+idConcursoPuestoAgr).getResultList();
		
	}
	@SuppressWarnings("unchecked")
	private void findPresentAclaracDocByConcurso(){
		presentacionDocs= em.createQuery("Select p from PresentAclaracDoc p " +
				" where p.tipoPA like 'P'  and p.activo=TRUE and p.concurso.idConcurso="+idConcurso).getResultList();
		aclaracionDocs= em.createQuery("Select p from PresentAclaracDoc p " +
				" where p.tipoPA like 'A'  and p.activo=TRUE and p.concurso.idConcurso="+idConcurso).getResultList();
		
	}
	
	
	
	public void addPresentacion(){
		PresentAclaracDoc presentAclaracDoc= new PresentAclaracDoc();
		if(presentacion.getLugar()==null || presentacion.getLugar().trim().equals("") || presentacion.getDireccion()==null ||
				 presentacion.getDireccion().trim().equals("")|| desdePre==null ||desdePre.trim().equals("") ||
			hastaPre==null||hastaPre.trim().equals("")|| presentacion.getFechaPresDesde()==null || presentacion.getFechaPresHasta()==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar los datos obligatorios antes de agregar");
			return;
		}
		
		if(presentacion.getEmail()!=null && !presentacion.getEmail().trim().equals("")){
			
			if(!general.isEmail(presentacion.getEmail().toLowerCase()))
			{
				statusMessages.add(Severity.ERROR,"Ingrese un mail valido. Verifique");
				return ;
			}
			statusMessages.clear();
			presentacion.setEmail(presentacion.getEmail().toLowerCase());
		}
		
		if( !general.isFechaValida(presentacion.getFechaPresDesde())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Desde inválida");
			return ;
		}
		if( !general.isFechaValida(presentacion.getFechaPresHasta())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Desde inválida");
			return ;
		}
		
		if(presentacion.getFechaPresDesde().after(presentacion.getFechaPresHasta())){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,"La Fecha Desde no puede ser Mayor a la Fecha Hasta. Verifique ");
			return;
		}
		
		
		if(!validarHoras(desdePre)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR," Horario de Atención Desde inválido. Verifique");
			return;
		}
		if(!validarHoras(hastaPre)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR," Horario de Atención Hasta inválido. Verifique");
			return;
		}
		if(!horaValida(desdePre)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR," Horario de Atención Desde inválido, la hora no puede ser mayor a 24Hs ni menor a 0. Verifique");
			return;
		}
		if(!minutoValido(desdePre)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR," Horario de Atención Desde inválido, el minuto no puede ser mayor a 60min ni menor a 0. Verifique");
			return;
		}
		if(!horaValida(hastaPre)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR," Horario de Atención Hasta inválido, la hora no puede ser mayor a 24Hs ni menor a 0. Verifique");
			return;
		}	
		
		if(!minutoValido(hastaPre)){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR," Horario de Atención Hasta inválido, el minuto no puede ser mayor a 60min ni menor a 0. Verifique");
			return;
		}
			
		
		
		
		
		/**
		 * desde
		 * **/
             int[]hora = getHora(desdePre);
             Date fecha;
             if (presentacion.getHorarioDesde()!= null){
                     fecha = presentacion.getHorarioDesde();
             }
             else{
                     fecha = new Date();
             }
             if (hora != null){
                     fecha.setHours(hora[0]);
                     fecha.setMinutes(hora[1]);
             }
             presentacion.setHorarioDesde(fecha);
            /**hasta
             * */
             int[]horaFin = getHora(hastaPre);
             Date fechaFin;
             if (presentacion.getHorarioHasta()!= null){
                     fechaFin = presentacion.getHorarioHasta();
             }
             else{
                     fechaFin = new Date();
             }
             if (horaFin != null){
                     fechaFin.setHours(horaFin[0]);
                     fechaFin.setMinutes(horaFin[1]);
             }
             presentacion.setHorarioHasta(fechaFin);
             
		if(existePresentacion()){
			statusMessages.add("Ya exite el mismo registro con el mismo Lugar, Dirección, Horario desde/hasta, verifique ");
			return;
		}
		presentacion.setActivo(true);
		presentacion.setTipoPA("P");
		if(idConcursoPuestoAgr!=null)
			presentacion.setConcursoPuestoAgr(concursoPuestoAgr);
		if(idConcurso!=null)
			presentacion.setConcurso(concurso);

		if(esEditPrese){
			presentacion.setFechaMod(new Date());
			presentacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			presentacionDocs.set(posPrese, presentacion);
		}else{
			presentacion.setFechaAlta(new Date());
			presentacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			presentacionDocs.add(presentacion);
		}
		desdePre=null;
		hastaPre=null;
		presentacion=new PresentAclaracDoc();
		
		esEditPrese=false;
		
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}
	
	

	
	public void addAclaracion(){
		
		
		if(aclaracion.getLugar()==null || aclaracion.getLugar().trim().equals("") || aclaracion.getDireccion()==null ||
				aclaracion.getDireccion().trim().equals("")|| desdeAcla==null ||desdeAcla.trim().equals("") ||
				hastaAcla==null||hastaAcla.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe ingresar los datos obligatorios antes de agregar");
				return;
			}
		
			if(aclaracion.getEmail()!=null && !aclaracion.getEmail().trim().equals("")){
				if(!general.isEmail(aclaracion.getEmail().toLowerCase()))
				{
					statusMessages.add(Severity.ERROR,"Ingrese un mail válido. Verifique");
					return ;
				}
				statusMessages.clear();
				aclaracion.setEmail(aclaracion.getEmail().toLowerCase());
			}
			if(!validarHoras(desdeAcla)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR," Horario de Atención Desde inválido. Verifique");
				return;
			}
			if(!validarHoras(hastaAcla)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR," Horario de Atención Hasta inválido. Verifique");
				return;
			}
			if(!horaValida(desdeAcla)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR," Horario de Atención Desde inválido, la hora no puede ser mayor a 24Hs ni menor a 0. Verifique");
				return;
			}
			if(!minutoValido(desdeAcla)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR," Horario de Atención Desde inválido, el minuto no puede ser mayor a 60min ni menor a 0. Verifique");
				return;
			}
			if(!horaValida(hastaAcla)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR," Horario de Atención Hasta inválido, la hora no puede ser mayor a 24Hs ni menor a 0. Verifique");
				return;
			}	
			
			if(!minutoValido(hastaAcla)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR," Horario de Atención Hasta inválido, el minuto no puede ser mayor a 60min ni menor a 0. Verifique");
				return;
			}
			
		
			
		
			/** 
			 * desde
			 * **/
	             int[]hora = getHora(desdeAcla);
	             Date fecha;
	             if (aclaracion.getHorarioDesde()!= null){
	                     fecha = aclaracion.getHorarioDesde();
	             }
	             else{
	                     fecha = new Date();
	             }
	             if (hora != null){
	                     fecha.setHours(hora[0]);
	                     fecha.setMinutes(hora[1]);
	             }
	             aclaracion.setHorarioDesde(fecha);
	            /**hasta
	             * */
	             int[]horaFin = getHora(hastaAcla);
	             Date fechaFin;
	             if (aclaracion.getHorarioHasta()!= null){
	                     fechaFin = aclaracion.getHorarioHasta();
	             }
	             else{
	                     fechaFin = new Date();
	             }
	             if (horaFin != null){
	                     fechaFin.setHours(horaFin[0]);
	                     fechaFin.setMinutes(horaFin[1]);
	             }
	             aclaracion.setHorarioHasta(fechaFin);
	             
			if(existeAclaracion()){
				statusMessages.add(Severity.ERROR,"Ya exite el mismo registro con el mismo Lugar, Dirección, Horario desde/hasta, verifique ");
				return;
			}
			aclaracion.setActivo(true);
			aclaracion.setTipoPA("A");
			if(idConcursoPuestoAgr!=null)
				aclaracion.setConcursoPuestoAgr(concursoPuestoAgr);
			if(idConcurso!=null)
				aclaracion.setConcurso(concurso);
			
			
//			aclaracDoc.setActivo(presentacion.isActivo());
//			aclaracDoc.setConcurso(presentacion.getConcurso());
//			aclaracDoc.setConcursoPuestoAgr(presentacion.getConcursoPuestoAgr());
//			aclaracDoc.setDescripcion(presentacion.getDescripcion());
//			aclaracDoc.setDireccion(presentacion.getDescripcion());
//			aclaracDoc.setEmail(presentacion.getEmail());
//			aclaracDoc.setFechaPresDesde(presentacion.getFechaPresDesde());
//			aclaracDoc.setFechaPresHasta(presentacion.getFechaPresHasta());
//			aclaracDoc.setHorarioDesde(presentacion.getHorarioDesde());
//			aclaracDoc.setHorarioHasta(presentacion.getHorarioHasta());
//			aclaracDoc.setLugar(presentacion.getLugar());
//			aclaracDoc.setTelefono(presentacion.getTelefono());
//			aclaracDoc.setTipoPA(presentacion.getTipoPA());
			
			if(esEditAclara){
				aclaracion.setFechaMod(new Date());
				aclaracion.setUsuMod(usuarioLogueado.getCodigoUsuario());
				//aclaracion.setIdPresentAclaracDoc(aclaracion.getIdPresentAclaracDoc());
				aclaracionDocs.set(posAcla, aclaracion);
			}else{
				aclaracion.setFechaAlta(new Date());
				aclaracion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				aclaracionDocs.add(aclaracion);
			}
			esEditAclara=false;
			desdeAcla=null;
			hastaAcla=null;
			aclaracion=new PresentAclaracDoc();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
	}
	public void editarPresentacion(int index){
		PresentAclaracDoc aux= new PresentAclaracDoc();
		esEditPrese=true;
		posPrese=index;
		aux=presentacionDocs.get(index);
		presentacion.setActivo(aux.isActivo());
		presentacion.setConcurso(aux.getConcurso());
		presentacion.setConcursoPuestoAgr(aux.getConcursoPuestoAgr());
		presentacion.setDescripcion(aux.getDescripcion());
		presentacion.setDireccion(aux.getDireccion());
		presentacion.setEmail(aux.getEmail());
		presentacion.setFechaAlta(aux.getFechaAlta());
		presentacion.setFechaMod(aux.getFechaMod());
		presentacion.setFechaPresDesde(aux.getFechaPresDesde());
		presentacion.setFechaPresHasta(aux.getFechaPresHasta());
		presentacion.setHorarioDesde(aux.getHorarioDesde());
		presentacion.setHorarioHasta(aux.getHorarioHasta());
		presentacion.setIdPresentAclaracDoc(aux.getIdPresentAclaracDoc());
		presentacion.setLugar(aux.getLugar());
		presentacion.setTelefono(aux.getTelefono());
		presentacion.setTipoPA(aux.getTipoPA());
		presentacion.setUsuAlta(aux.getUsuAlta());
		presentacion.setUsuMod(aux.getUsuMod());
		cargarHorasPresentacion();
	
			
	}
	public void editarAclaracion(int index){
		PresentAclaracDoc aux= new PresentAclaracDoc();
		posAcla=index;
		esEditAclara=true;
		aux=aclaracionDocs.get(index);
		aclaracion.setActivo(aux.isActivo());
		aclaracion.setConcurso(aux.getConcurso());
		aclaracion.setConcursoPuestoAgr(aux.getConcursoPuestoAgr());
		aclaracion.setDescripcion(aux.getDescripcion());
		aclaracion.setDireccion(aux.getDireccion());
		aclaracion.setEmail(aux.getEmail());
		aclaracion.setFechaAlta(aux.getFechaAlta());
		aclaracion.setFechaMod(aux.getFechaMod());
		aclaracion.setFechaPresDesde(aux.getFechaPresDesde());
		aclaracion.setFechaPresHasta(aux.getFechaPresHasta());
		aclaracion.setHorarioDesde(aux.getHorarioDesde());
		aclaracion.setHorarioHasta(aux.getHorarioHasta());
		aclaracion.setIdPresentAclaracDoc(aux.getIdPresentAclaracDoc());
		aclaracion.setLugar(aux.getLugar());
		aclaracion.setTelefono(aux.getTelefono());
		aclaracion.setTipoPA(aux.getTipoPA());
		aclaracion.setUsuAlta(aux.getUsuAlta());
		aclaracion.setUsuMod(aux.getUsuMod());
		
		cargarHorasAclaracion();
		
			
	}
	public void eliminarPres(Long idPresentAclaracDoc){
		try {
			PresentAclaracDoc aux= em.find(PresentAclaracDoc.class, idPresentAclaracDoc);
			presentacionDocs.remove(aux);
			aux.setActivo(false);
			aux.setFechaAlta(new Date());
			aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.merge(aux);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al intentar eliminar el registro");
		}
		
	
	}
	public void eliminarPresAux(int index){
		
		presentacionDocs.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}
	public void eliminarAclaAux(int index){
		
		aclaracionDocs.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}
	public void eliminarAcla(Long idPresentAclaracDoc){
		try {
			PresentAclaracDoc aux= em.find(PresentAclaracDoc.class, idPresentAclaracDoc);
			aclaracionDocs.remove(aux);
			aux.setActivo(false);
			aux.setFechaAlta(new Date());
			aux.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.merge(aux);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al intentar eliminar el registro");
		}
		
	}
	
	
	public void cancelarPos(){
		desdePre=null;
		hastaPre=null;
		presentacion=new PresentAclaracDoc();
		esEditPrese=false;
	}
	public void cancelarAcla(){
		esEditAclara=false;
		desdeAcla=null;
		hastaAcla=null;
		aclaracion=new PresentAclaracDoc();
	}
	
	
	public String save(){
		try {
			//if(presentacionDocs.isEmpty() && aclaracionDocs.isEmpty()){//MODIFICADO RV
			if(aclaracionDocs.isEmpty()){
				statusMessages.add("Debe especificar alguna Aclaración");
				return null;
			}
			
			/*for (int i = 0; i < presentacionDocs.size(); i++) {//MODIFICADO RV
				if(presentacionDocs.get(i).getIdPresentAclaracDoc()==null){
					em.persist(presentacionDocs.get(i));
				}else{
					em.merge(presentacionDocs.get(i));
				}
				em.flush();
			}*/
			for (int i = 0; i < aclaracionDocs.size(); i++) {
				if(aclaracionDocs.get(i).getIdPresentAclaracDoc()!=null){
					em.merge(aclaracionDocs.get(i));
					
				}else{
					em.persist(aclaracionDocs.get(i));
					
				}
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		
		
	}
	
	private boolean existePresentacion(){
		for (int i = 0; i < presentacionDocs.size(); i++) {
			if(!esEditPrese){
				if(presentacionDocs.get(i).getLugar().toLowerCase().equals(presentacion.getLugar().toLowerCase())||
						presentacionDocs.get(i).getDireccion().toLowerCase().equals(presentacion.getDireccion().toLowerCase())||
						presentacionDocs.get(i).getHorarioDesde()==presentacion.getHorarioDesde()||
						presentacionDocs.get(i).getHorarioHasta()==presentacion.getHorarioHasta())
						return true;
			}else{
				if((presentacionDocs.get(i).getLugar().toLowerCase().equals(presentacion.getLugar().toLowerCase())||
						presentacionDocs.get(i).getDireccion().toLowerCase().equals(presentacion.getDireccion().toLowerCase())||
						presentacionDocs.get(i).getHorarioDesde()==presentacion.getHorarioDesde()||
						presentacionDocs.get(i).getHorarioHasta()==presentacion.getHorarioHasta()) && presentacion.getIdPresentAclaracDoc()!=presentacionDocs.get(i).getIdPresentAclaracDoc())
						return true;
			}
			
			
		}
		return false;
	}
	private boolean existeAclaracion(){
		for (int i = 0; i < aclaracionDocs.size(); i++) {
			
			if(!esEditAclara){
				if(aclaracionDocs.get(i).getLugar().toLowerCase().equals(aclaracion.getLugar().toLowerCase())||
						aclaracionDocs.get(i).getDireccion().toLowerCase().equals(aclaracion.getDireccion().toLowerCase())||
						aclaracionDocs.get(i).getHorarioDesde()==aclaracion.getHorarioDesde()||
						aclaracionDocs.get(i).getHorarioHasta()==aclaracion.getHorarioHasta())
					return true;
			}else{
				if((aclaracionDocs.get(i).getLugar().toLowerCase().equals(aclaracion.getLugar().toLowerCase())||
						aclaracionDocs.get(i).getDireccion().toLowerCase().equals(aclaracion.getDireccion().toLowerCase())||
						aclaracionDocs.get(i).getHorarioDesde()==aclaracion.getHorarioDesde()||
						aclaracionDocs.get(i).getHorarioHasta()==aclaracion.getHorarioHasta())&& aclaracion.getIdPresentAclaracDoc()!=aclaracionDocs.get(i).getIdPresentAclaracDoc())
					return true;
			}
			
		}
		return false;
	}
	
	
	private void cargarHorasPresentacion(){
		  //Hora Apertura
		
      if (presentacion.getHorarioDesde() != null){
              desdePre= "";
              Date fecha = presentacion.getHorarioDesde();
              if (fecha.getHours() < 10)
            	  desdePre += "0" + fecha.getHours();
              else
            	  desdePre += fecha.getHours();
              
              desdePre += ":";
              
              if (fecha.getMinutes() < 10)
            	  desdePre += "0" + fecha.getMinutes();
              else
            	  desdePre += fecha.getMinutes();
      }
      
      //Hora fin
      if (presentacion.getHorarioHasta()!= null){
              hastaPre = "";
              Date fecha =presentacion.getHorarioHasta();
              if (fecha.getHours() < 10)
            	  hastaPre += "0" + fecha.getHours();
              else
            	  hastaPre += fecha.getHours();
              
              hastaPre += ":";
              
              if (fecha.getMinutes() < 10)
            	  hastaPre += "0" + fecha.getMinutes();
              else
            	  hastaPre += fecha.getMinutes();
      }
      
	}
	private void cargarHorasAclaracion(){
		  //Hora Apertura
		
    if (aclaracion.getHorarioDesde() != null){
            desdeAcla= "";
            Date fecha = aclaracion.getHorarioDesde();
            if (fecha.getHours() < 10)
          	  desdeAcla += "0" + fecha.getHours();
            else
          	  desdeAcla += fecha.getHours();
            
            desdeAcla += ":";
            
            if (fecha.getMinutes() < 10)
          	  desdeAcla += "0" + fecha.getMinutes();
            else
          	  desdeAcla += fecha.getMinutes();
    }
    
    //Hora fin
    if (aclaracion.getHorarioHasta()!= null){
            hastaAcla = "";
            Date fecha =aclaracion.getHorarioHasta();
            if (fecha.getHours() < 10)
          	  hastaAcla += "0" + fecha.getHours();
            else
          	  hastaAcla += fecha.getHours();
            
            hastaAcla += ":";
            
            if (fecha.getMinutes() < 10)
          	  hastaAcla += "0" + fecha.getMinutes();
            else
          	  hastaAcla += fecha.getMinutes();
    }
	}
	 private int[] getHora(String horaCad) {
	        String[] horas = horaCad.split(":");
	                if (horas.length != 2){
	                        return null; 
	                }
	                else{
	                        String hora = horas[0];
	                        String minuto = horas[1];
	                        try{
	                                int hh = Integer.parseInt(hora);
	                                int mm = Integer.parseInt(minuto);
	                                
	                                if(hh < 0 || hh > 23 || mm < 0 || mm >= 60){
	                                        return null;
	                                }
	                                int [] v = new int[2];
	                                v[0] = hh;
	                                v[1] = mm;
	                                return v;
	                        }
	                        catch(Exception e){
	                                return null; 
	                        }
	                }
	        }
	 
	 
	 private boolean validarHoras(String hhmm){
		   String[] horas = hhmm.split(":");
		 if (horas.length != 2)
			   return false;
		 if(horas[0]==null || horas[0].trim().equals(""))
			 return false;
		 if(horas[1]==null || horas[1].trim().equals(""))
			 return false;
		 if(!isNumeric(horas[0]) || !isNumeric(horas[1]))
			 return false;
		 
		 return true;
	 }
	 
	 private boolean horaValida(String hhmm){
		 String[] horas = hhmm.split(":");
		 Integer hh=Integer.parseInt(horas[0]);
		 if(hh.intValue()>=24 || hh.intValue()<0)
			return false;
		 return true;
	 }
	 private boolean minutoValido(String hhmm){
		 String[] minutos = hhmm.split(":");
		 Integer mm=Integer.parseInt(minutos[1]);
		 if(mm.intValue()>=60 || mm.intValue()<0)
			return false;
		 
		 return true;
	 }
	 
	 private  boolean isNumeric(String cadena){
	        try {
		        Integer.parseInt(cadena);
		        return true;
		        } catch (NumberFormatException nfe){
		        return false;
	        }
	      }
	 
	 
//	GETTERS Y SETTERS	
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}


	public List<PresentAclaracDoc> getPresentacionDocs() {
		return presentacionDocs;
	}


	public void setPresentacionDocs(List<PresentAclaracDoc> presentacionDocs) {
		this.presentacionDocs = presentacionDocs;
	}


	public List<PresentAclaracDoc> getAclaracionDocs() {
		return aclaracionDocs;
	}


	public void setAclaracionDocs(List<PresentAclaracDoc> aclaracionDocs) {
		this.aclaracionDocs = aclaracionDocs;
	}


	public PresentAclaracDoc getPresentacion() {
		return presentacion;
	}


	public void setPresentacion(PresentAclaracDoc presentacion) {
		this.presentacion = presentacion;
	}


	public PresentAclaracDoc getAclaracion() {
		return aclaracion;
	}


	public void setAclaracion(PresentAclaracDoc aclaracion) {
		this.aclaracion = aclaracion;
	}


	public String getDesdeAcla() {
		return desdeAcla;
	}


	public void setDesdeAcla(String desdeAcla) {
		this.desdeAcla = desdeAcla;
	}


	public String getHastaAcla() {
		return hastaAcla;
	}


	public void setHastaAcla(String hastaAcla) {
		this.hastaAcla = hastaAcla;
	}


	public String getDesdePre() {
		return desdePre;
	}


	public void setDesdePre(String desdePre) {
		this.desdePre = desdePre;
	}


	public String getHastaPre() {
		return hastaPre;
	}


	public void setHastaPre(String hastaPre) {
		this.hastaPre = hastaPre;
	}


	public boolean isEsEditPrese() {
		return esEditPrese;
	}


	public void setEsEditPrese(boolean esEditPrese) {
		this.esEditPrese = esEditPrese;
	}


	public boolean isEsEditAclara() {
		return esEditAclara;
	}


	public void setEsEditAclara(boolean esEditAclara) {
		this.esEditAclara = esEditAclara;
	}


	public int getPosPrese() {
		return posPrese;
	}


	public void setPosPrese(int posPrese) {
		this.posPrese = posPrese;
	}


	public int getPosAcla() {
		return posAcla;
	}


	public void setPosAcla(int posAcla) {
		this.posAcla = posAcla;
	}


	public Long getIdConcurso() {
		return idConcurso;
	}


	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}


	public Concurso getConcurso() {
		return concurso;
	}


	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}


	public Long getIdFromCU() {
		return idFromCU;
	}


	public void setIdFromCU(Long idFromCU) {
		this.idFromCU = idFromCU;
	}


	
	
	

	

	
	
	
}
