package py.com.excelsis.sicca.legajo.session;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DeclaracionJurada;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admDeclaraJuradas656FC")
public class AdmDeclaraJuradas656FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private Date fechaPrese;

	private List<DeclaracionJurada> lGrilla1;

	private String observacion;

	private Long idTipoDoc;

	private Long idPersona;
	private Integer idElemUpdate;
	private byte[] uFile1 = null;
	private String cType1 = null;
	private String fName1 = null;
	private String texto;
	
	private String nombreDoc;

	public void init() {
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		cargarCabecera();
		cargarGrilla1();

	}

	private Boolean precondInit() {

		return true;
	}

	private void cargarCabecera() {

	}

	public DeclaracionJurada updateDoc(DeclaracionJurada dj) {
		if (dj.getDocumento() != null) {
			Documentos doc = em.find(Documentos.class, dj.getDocumento().getIdDocumento());
			dj.setDocumento(doc);
		}
		return dj;
	}

	private List<DatosEspecificos> traerTipoDoc() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
				+ " and DatosEspecificos.activo is true and DatosEspecificos.valorAlf = :dEValAlf order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "TIPOS DE DOCUMENTOS");

		q.setParameter("dEValAlf", "LDJ");
		return q.getResultList();
	}

	public List<SelectItem> traerTipoDocumentoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione un Tipo de Documento"));
		for (DatosEspecificos o : traerTipoDoc())
			si.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		return si;
	}

	private void cargarGrilla1() {
		Query q =
			em.createQuery("select DeclaracionJurada from DeclaracionJurada DeclaracionJurada "
				+ " where DeclaracionJurada.activo is true and DeclaracionJurada.persona.idPersona = :idPersona order by DeclaracionJurada.fechaPresentacion desc");
		q.setParameter("idPersona", idPersona);
		lGrilla1 = q.getResultList();
	}

	private Boolean precondAgregar(Boolean valDocumento) {
		if (valDocumento) {
			if (fechaPrese == null || observacion == null || idTipoDoc == null || uFile1 == null) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
				return false;
			}
		} else {
			if (fechaPrese == null || observacion == null) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
				return false;
			}
		}

		if (observacion.length() > 500) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Comentario (500)");
			return false;
		}
		return true;
	}

	public String agregar() {
		if (!precondAgregar(true))
			return "FAIL";
		try {
			DeclaracionJurada dj = new DeclaracionJurada();
			// Adjuntar documento
			Long idDocuGenerado = guardarDocumento(null);
			if (idDocuGenerado != null) {
				dj.setDocumento(new Documentos());
				dj.getDocumento().setIdDocumento(idDocuGenerado);
				dj.setActivo(true);
				dj.setPersona(new Persona());
				dj.getPersona().setIdPersona(idPersona);
				dj.setFechaPresentacion(fechaPrese);
				dj.setComentario(observacion);
				dj.setFechaAlta(new Date());
				dj.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(dj);
				em.flush();
				
				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDocuGenerado);
				doc.setPersona(em.find(Persona.class, idPersona));
				doc.setIdTabla(dj.getIdDeclaracionJurada());
				em.merge(doc);
				em.flush();
			}else{
				return null;
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			// refresh
			cargarGrilla1();
			limpiar();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return null;

		}

	}

	private Long guardarDocumento(Documentos docu) {
		if (uFile1 != null) {
			Long idDocAnterior = (docu != null ? docu.getIdDocumento() : null);
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fName1, uFile1.length, cType1, uFile1);
			Long idDocuGenerado;
			String nombreTabla = "DeclaracionJurada";
			String nombrePantalla = "admDeclaraJuradas656FC";
			String cSeparador = File.separator;
			Persona persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			String direccionFisica =
				cSeparador + "LEGAJO" + cSeparador
					+ persona.getDocumentoIdentidad() + "_" + persona.getIdPersona()+ cSeparador + "DJ" + cSeparador;
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocAnterior);
			return idDocuGenerado;
		}
		return null;
	}

	public String actualizar() {
		if (!precondAgregar(false)) {
			return null;
		}
		if (idElemUpdate != null) {
			try {
				DeclaracionJurada dj = lGrilla1.get(idElemUpdate);
				if (uFile1 != null) {
					// Adjuntar documento
					Long idDocuGenerado = guardarDocumento(dj.getDocumento());
					if (idDocuGenerado != null) {
						dj.setDocumento(new Documentos());
						dj.getDocumento().setIdDocumento(idDocuGenerado);
					
						Documentos doc = new Documentos();
						doc = em.find(Documentos.class, idDocuGenerado);
						doc.setPersona(em.find(Persona.class, idPersona));
						doc.setIdTabla(dj.getIdDeclaracionJurada());
						em.merge(doc);
						em.flush();
					}else{
						return null;
					}
				}
				dj.setFechaPresentacion(fechaPrese);
				dj.setComentario(observacion);
				dj.setFechaMod(new Date());
				dj.setUsuMod(usuarioLogueado.getCodigoUsuario());
				lGrilla1.set(idElemUpdate.intValue(), em.merge(dj));
				idElemUpdate = null;
				limpiarNuevo();
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return null;

			}
		}
		return null;
	}

	public void editar(int index) {
		limpiar();
		if (index < lGrilla1.size()) {
			idElemUpdate = index;
			DeclaracionJurada dj = lGrilla1.get(idElemUpdate);

			if (dj.getDocumento() != null){
				Documentos doc = em.find(Documentos.class, dj.getDocumento().getIdDocumento());
				idTipoDoc = doc.getDatosEspecificos().getIdDatosEspecificos();
				nombreDoc = doc.getNombreFisicoDoc();
			}
			fechaPrese = dj.getFechaPresentacion();
			observacion = dj.getComentario();
		}
	}

	public void eliminar(int index) {
		try {
			if (lGrilla1.get(index).getIdDeclaracionJurada() != null) {
				DeclaracionJurada dp = lGrilla1.get(index);
				dp.setFechaMod(new Date());
				dp.setUsuMod(usuarioLogueado.getCodigoUsuario());
				dp.setActivo(false);

				dp = em.merge(dp);
				// Inactivar el documento
				if (dp.getDocumento() != null) {
					Documentos docu = em.find(Documentos.class, dp.getDocumento().getIdDocumento());
					docu.setActivo(false);
					docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
					docu.setFechaMod(new Date());
					em.merge(docu);
				}
				em.flush();
			}
			lGrilla1.remove(index);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			e.printStackTrace();
		}
	}

	public void limpiarNuevo() {
		cType1 = null;
		fechaPrese = null;
		fName1 = null;
		idElemUpdate = null;
		idTipoDoc = null;
		uFile1 = null;
		observacion = null;
		nombreDoc = null;
	}

	public void limpiar() {
		cType1 = null;
		fechaPrese = null;
		fName1 = null;
		idElemUpdate = null;
		idTipoDoc = null;
		uFile1 = null;
		observacion = null;
		nombreDoc = null;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public byte[] getuFile1() {
		return uFile1;
	}

	public void setuFile1(byte[] uFile1) {
		this.uFile1 = uFile1;
	}

	public String getcType1() {
		return cType1;
	}

	public void setcType1(String cType1) {
		this.cType1 = cType1;
	}

	public String getfName1() {
		return fName1;
	}

	public void setfName1(String fName1) {
		this.fName1 = fName1;
	}

	public Integer getIdElemUpdate() {
		return idElemUpdate;
	}

	public void setIdElemUpdate(Integer idElemUpdate) {
		this.idElemUpdate = idElemUpdate;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Date getFechaPrese() {
		return fechaPrese;
	}

	public void setFechaPrese(Date fechaPrese) {
		this.fechaPrese = fechaPrese;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public List<DeclaracionJurada> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<DeclaracionJurada> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

}
