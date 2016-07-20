package py.com.excelsis.sicca.legajo.session;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import py.com.excelsis.sicca.entity.DatosPermiso;
import py.com.excelsis.sicca.entity.DatosVacaciones;
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
@Name("admPermisos655FC")
public class AdmPermisos655FC {
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

	private Date fechaDesde;
	private Date fechaHasta;
	private Date horaDesde;
	private Date horaHasta;
	SimpleDateFormat sdfHORA = new SimpleDateFormat("HH:mm");
	private Long idEstado;
	private Long idMotivo;
	private List<DatosPermiso> lGrilla1;

	private Integer nroActo;
	private String observacion;
	private Date fechaActo;
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

	private List<DatosEspecificos> traerEstado() {
		Query q =
				em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
					+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
					+ " and DatosEspecificos.activo is true  order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "ESTADOS DE PERMISO");

		return q.getResultList();
	}
	
	private DatosEspecificos traerEstadoAprobado() {
		Query q =
				em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
					+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
					+ " and DatosEspecificos.descripcion =:aprobado and DatosEspecificos.activo is true  order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "ESTADOS DE PERMISO");
		q.setParameter("aprobado", "APROBADO");

		return (DatosEspecificos) q.getResultList().get(0);
	}

	public List<SelectItem> traerEstadoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione un Estado"));
		for (DatosEspecificos o : traerEstado())
			si.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		return si;
	}

	private List<DatosEspecificos> traerMotivo() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
				+ " and DatosEspecificos.activo is true  order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "MOTIVO DE PERMISO");

		return q.getResultList();
	}

	public List<SelectItem> traerMotivoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione un Motivo"));
		for (DatosEspecificos o : traerMotivo())
			si.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		return si;
	}

	private List<DatosEspecificos> traerTipoDoc() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
				+ " and DatosEspecificos.activo is true and DatosEspecificos.descripcion = :dEDsc and DatosEspecificos.valorAlf = :dEValAlf order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "TIPOS DE DOCUMENTOS");
		q.setParameter("dEDsc", "RESOLUCION");
		q.setParameter("dEValAlf", "RES");
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
			em.createQuery("select DatosPermiso from DatosPermiso DatosPermiso "
				+ " where DatosPermiso.activo is true and DatosPermiso.persona.idPersona = :idPersona order by DatosPermiso.fechaFin desc ");
		q.setParameter("idPersona", idPersona);
		lGrilla1 = q.getResultList();
	}

	private Boolean precondAgregar(Boolean valDocumento) {
		if (valDocumento) {
			if (fechaDesde == null || nroActo == null || fechaActo == null || fechaHasta == null
				|| idMotivo == null || /*idEstado == null ||*/ uFile1 == null) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
				return false;
			}
		} else {
			if (fechaDesde == null || fechaHasta == null || idMotivo == null /*|| idEstado == null*/) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
				return false;
			}
		}
		if (fechaDesde.getTime() > fechaHasta.getTime()) {
			statusMessages.add(Severity.ERROR, "La Fecha Desde no puede ser mayor a la Fecha Hasta");
			return false;
		}
		if (horaDesde != null && horaHasta == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar la Hora Hasta");
			return false;
		}
		if (horaHasta != null && horaDesde == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar la Hora Desde");
			return false;
		}
		if (horaDesde != null && horaHasta != null)
			if (horaDesde.getTime() > horaHasta.getTime()) {
				statusMessages.add(Severity.ERROR, "La Hora Desde no puede ser mayor a la Hora Hasta");
				return false;
			}

		if (observacion != null && observacion.length() > 500) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Observación (500)");
			return false;
		}
		return true;
	}

	public String agregar() {
		if (!precondAgregar(true))
			return "FAIL";
		try {
			DatosPermiso dv = new DatosPermiso();
			// Adjuntar documento
			Long idDocuGenerado = guardarDocumento(null);
			if (idDocuGenerado != null) {
				dv.setDocumentos(new Documentos());
				dv.getDocumentos().setIdDocumento(idDocuGenerado);

				dv.setActivo(true);
	
				dv.setPersona(new Persona());
				dv.getPersona().setIdPersona(idPersona);
				dv.setHoraInicio(horaDesde);
				dv.setHoraFin(horaHasta);
				dv.setFechaInicio(fechaDesde);
				dv.setFechaFin(fechaHasta);
				dv.setObsPermiso(observacion);
				dv.setNroActo(nroActo);
				dv.setFechaActo(fechaActo);
				dv.setDatosEspEstadoPermiso(traerEstadoAprobado());
	//			dv.getDatosEspEstadoPermiso().setIdDatosEspecificos(idEstado);
				dv.setDatosEspMotivoPermiso(new DatosEspecificos());
				dv.getDatosEspMotivoPermiso().setIdDatosEspecificos(idMotivo);
				dv.setFechaAlta(new Date());
	
				dv.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(dv);
				em.flush();
				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDocuGenerado);
				doc.setPersona(em.find(Persona.class, idPersona));
				doc.setIdTabla(dv.getIdDatosPermiso());
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

	public DatosPermiso updateMotivo(DatosPermiso dp) {
		if (dp.getDatosEspMotivoPermiso() != null) {
			DatosEspecificos de =
				em.find(DatosEspecificos.class, dp.getDatosEspMotivoPermiso().getIdDatosEspecificos());
			dp.setDatosEspMotivoPermiso(de);
		}
		return dp;
	}

	private Long guardarDocumento(Documentos docu) {
		if (uFile1 != null) {
			Long idDocAnterior = (docu != null ? docu.getIdDocumento() : null);
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fName1, uFile1.length, cType1, uFile1);
			Long idDocuGenerado;
			String nombreTabla = "DatosPermiso";
			String nombrePantalla = "admPermisos655FC";
			String cSeparador = File.separator;
			Persona persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			String direccionFisica =
				cSeparador + "LEGAJO" + cSeparador
					+ persona.getDocumentoIdentidad() + "_" + persona.getIdPersona()+ cSeparador +"PERMISOS"+ cSeparador;
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
				DatosPermiso dp = lGrilla1.get(idElemUpdate);
				if (uFile1 != null) {
					// Adjuntar documento
					Long idDocuGenerado = guardarDocumento(dp.getDocumentos());
					if (idDocuGenerado != null) {
						dp.setDocumentos(new Documentos());
						dp.getDocumentos().setIdDocumento(idDocuGenerado);
					
						Documentos doc = new Documentos();
						doc = em.find(Documentos.class, idDocuGenerado);
						doc.setPersona(em.find(Persona.class, idPersona));
						doc.setIdTabla(dp.getIdDatosPermiso());
						em.merge(doc);
						em.flush();
					}else{
						return null;
					}
				}
				dp.setFechaActo(fechaActo);
				dp.setDatosEspEstadoPermiso(new DatosEspecificos());
				dp.getDatosEspEstadoPermiso().setIdDatosEspecificos(idEstado);
				dp.setDatosEspMotivoPermiso(new DatosEspecificos());
				dp.getDatosEspMotivoPermiso().setIdDatosEspecificos(idMotivo);
				dp.setHoraInicio(horaDesde);
				dp.setHoraFin(horaHasta);
				dp.setNroActo(nroActo);
				dp.setFechaInicio(fechaDesde);
				dp.setFechaFin(fechaHasta);
				dp.setObsPermiso(observacion);
				dp.setFechaMod(new Date());
				dp.setUsuMod(usuarioLogueado.getCodigoUsuario());
				lGrilla1.set(idElemUpdate.intValue(), em.merge(dp));
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
			DatosPermiso dp = lGrilla1.get(idElemUpdate);
			em.refresh(dp);
			if (dp.getDocumentos() != null){
				idTipoDoc = dp.getDocumentos().getDatosEspecificos().getIdDatosEspecificos();
				nombreDoc = dp.getDocumentos().getNombreFisicoDoc();
			}
			fechaActo = dp.getFechaActo();
			fechaDesde = dp.getFechaInicio();
			fechaHasta = dp.getFechaFin();
			observacion = dp.getObsPermiso();
			idEstado = dp.getDatosEspEstadoPermiso().getIdDatosEspecificos();
			idMotivo = dp.getDatosEspMotivoPermiso().getIdDatosEspecificos();
			horaDesde = dp.getHoraInicio();
			horaHasta = dp.getHoraFin();
			nroActo = dp.getNroActo();
		}
	}

	public void eliminar(int index) {
		try {
			if (lGrilla1.get(index).getIdDatosPermiso() != null) {
				DatosPermiso dp = lGrilla1.get(index);
				dp.setFechaMod(new Date());
				dp.setUsuMod(usuarioLogueado.getCodigoUsuario());
				dp.setActivo(false);
				dp = em.merge(dp);
				// Inactivar el documento
				if (dp.getDocumentos() != null) {
					Documentos docu =
						em.find(Documentos.class, dp.getDocumentos().getIdDocumento());
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

		horaDesde = null;
		horaHasta = null;
		fechaActo = null;
		fechaDesde = null;
		fechaHasta = null;
		fName1 = null;
		idElemUpdate = null;
		idTipoDoc = null;
		idEstado = null;
		idMotivo = null;
		nroActo = null;
		uFile1 = null;
		observacion = null;
		nombreDoc = null;
	}

	public void limpiar() {
		idTipoDoc = null;
		cType1 = null;
		observacion = null;
		horaDesde = null;
		horaHasta = null;
		fechaActo = null;
		fechaDesde = null;
		fechaHasta = null;
		fName1 = null;
		idElemUpdate = null;
		idTipoDoc = null;
		idEstado = null;
		idMotivo = null;
		nroActo = null;
		uFile1 = null;
		nombreDoc = null;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public String getHoraDesde() {
		if (horaDesde != null) {
			return sdfHORA.format(horaDesde);
		}
		return null;
	}

	public void setHoraDesde(String horaDesdeParam) {
		try {
			if (horaDesdeParam != null && !horaDesdeParam.trim().isEmpty())
				horaDesde = sdfHORA.parse(horaDesdeParam);

		} catch (ParseException e) {
			horaDesde = null;
			e.printStackTrace();
		}
	}

	public String getHoraHasta() {
		if (horaHasta != null) {
			return sdfHORA.format(horaHasta);
		}
		return null;
	}

	public void setHoraHasta(String horaHastaParam) {
		try {
			if (horaHastaParam != null && !horaHastaParam.trim().isEmpty())
				horaHasta = sdfHORA.parse(horaHastaParam);
		} catch (ParseException e) {
			horaHasta = null;
			e.printStackTrace();
		}
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

	public Integer getNroActo() {
		return nroActo;
	}

	public void setNroActo(Integer nroActo) {
		this.nroActo = nroActo;
	}

	public Date getFechaActo() {
		return fechaActo;
	}

	public void setFechaActo(Date fechaActo) {
		this.fechaActo = fechaActo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}

	public List<DatosPermiso> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<DatosPermiso> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

}
