package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.CalendarioOeeCab;
import py.com.excelsis.sicca.entity.CalendarioOeeDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CalendarioOeeCabHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("calendarioOeeFormController")
public class CalendarioOeeFormController implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CalendarioOeeCabHome calendarioOeeCabHome;
	CalendarioOeeCab calendarioOeeCab;
	ConfiguracionUoCab configuracionUoCab;

	private Boolean lunes;
	private Boolean martes;
	private Boolean miercoles;
	private Boolean jueves;
	private Boolean viernes;
	private Boolean sabado;
	private Boolean domingo;
	private Boolean cargadoHorario;

	private String horaDesdeLunes;
	private String horaHastaLunes;
	private String horaDesdeMartes;
	private String horaHastaMartes;
	private String horaDesdeMiercoles;
	private String horaHastaMiercoles;
	private String horaDesdeJueves;
	private String horaHastaJueves;
	private String horaDesdeViernes;
	private String horaHastaViernes;
	private String horaDesdeSabado;
	private String horaHastaSabado;
	private String horaDesdeDomingo;
	private String horaHastaDomingo;
	private Date fechaFestiva;
	private String descripcionFestiva;

	List<CalendarioOeeDet> listaDetOee = new ArrayList<CalendarioOeeDet>();
	List<CalendarioOeeDet> listaDetOeeAux = new ArrayList<CalendarioOeeDet>();

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		cargadoHorario = false;
		listaDetOeeAux = new ArrayList<CalendarioOeeDet>();
		configuracionUoCab = new ConfiguracionUoCab();
		Long id = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
		listaDetOee = new ArrayList<CalendarioOeeDet>();
		calendarioOeeCab = new CalendarioOeeCab();
		calendarioOeeCab = calendarioOeeCabHome.getInstance();
		if (calendarioOeeCabHome.isIdDefined()) {
			listaDetOee = buscarDetalles();
			if (calendarioOeeCab.getDomHoraDesde() != null
					&& calendarioOeeCab.getDomHoraHasta() != null) {
				domingo = true;
				horaDesdeDomingo = buscarHora(calendarioOeeCab
						.getDomHoraDesde().toString());
				horaHastaDomingo = buscarHora(calendarioOeeCab
						.getDomHoraHasta().toString());
			}
			if (calendarioOeeCab.getLunHoraDesde() != null
					&& calendarioOeeCab.getLunHoraHasta() != null) {
				lunes = true;
				horaDesdeLunes = buscarHora(calendarioOeeCab.getLunHoraDesde()
						.toString());
				horaHastaLunes = buscarHora(calendarioOeeCab.getLunHoraHasta()
						.toString());
			}
			if (calendarioOeeCab.getMarHoraDesde() != null
					&& calendarioOeeCab.getMarHoraHasta() != null) {
				martes = true;
				horaDesdeMartes = buscarHora(calendarioOeeCab.getMarHoraDesde()
						.toString());
				horaHastaMartes = buscarHora(calendarioOeeCab.getMarHoraHasta()
						.toString());
			}

			if (calendarioOeeCab.getMieHoraDesde() != null
					&& calendarioOeeCab.getMieHoraHasta() != null) {
				miercoles = true;
				horaDesdeMiercoles = buscarHora(calendarioOeeCab
						.getMieHoraDesde().toString());
				horaHastaMiercoles = buscarHora(calendarioOeeCab
						.getMieHoraHasta().toString());
			}

			if (calendarioOeeCab.getJueHoraDesde() != null
					&& calendarioOeeCab.getJueHoraHasta() != null) {
				jueves = true;
				horaDesdeJueves = buscarHora(calendarioOeeCab.getJueHoraDesde()
						.toString());
				horaHastaJueves = buscarHora(calendarioOeeCab.getJueHoraHasta()
						.toString());
			}

			if (calendarioOeeCab.getVieHoraDesde() != null
					&& calendarioOeeCab.getVieHoraHasta() != null) {
				viernes = true;
				horaDesdeViernes = buscarHora(calendarioOeeCab
						.getVieHoraDesde().toString());
				horaHastaViernes = buscarHora(calendarioOeeCab
						.getVieHoraHasta().toString());
			}

			if (calendarioOeeCab.getSabHoraDesde() != null
					&& calendarioOeeCab.getSabHoraHasta() != null) {
				sabado = true;
				horaDesdeSabado = buscarHora(calendarioOeeCab.getSabHoraDesde()
						.toString());
				horaHastaSabado = buscarHora(calendarioOeeCab.getSabHoraHasta()
						.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<CalendarioOeeDet> buscarDetalles() {
		String sql = "select oee.* " + "from seleccion.calendario_oee_det oee "
				+ "where oee.id_calendario_oee_cab = "
				+ calendarioOeeCab.getIdCalendarioOeeCab()
				+ " and oee.activo is true";
		List<CalendarioOeeDet> lista = new ArrayList<CalendarioOeeDet>();
		lista = em.createNativeQuery(sql, CalendarioOeeDet.class)
				.getResultList();
		return lista;
	}

	private String buscarHora(String cod) {

		String[] arrayCodigo = cod.split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	public void agregarLista() {
		if (fechaFestiva != null && descripcionFestiva != null
				&& !descripcionFestiva.trim().isEmpty()) {
			CalendarioOeeDet calendarioOeeDet = new CalendarioOeeDet();
			calendarioOeeDet.setActivo(true);
			calendarioOeeDet.setDescripcion(descripcionFestiva);
			calendarioOeeDet.setFechaFestiva(fechaFestiva);
			listaDetOee.add(calendarioOeeDet);
			descripcionFestiva = null;
			fechaFestiva = null;
		}
	}

	public void eliminarLista(Integer row) {
		CalendarioOeeDet calendarioOeeDet = new CalendarioOeeDet();
		calendarioOeeDet = listaDetOee.get(row);
		if (calendarioOeeDet.getIdCalendarioOeeDet() == null) {
			listaDetOee.remove(calendarioOeeDet);
		} else {
			calendarioOeeDet.setActivo(false);
			listaDetOeeAux.add(calendarioOeeDet);
			listaDetOee.remove(calendarioOeeDet);
		}
	}

	@SuppressWarnings("unchecked")
	private Boolean check() {
		String sql = "select oee.* " + "from seleccion.calendario_oee_cab oee "
				+ "where oee.anho =  " + calendarioOeeCab.getAnho()
				+ " and oee.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo();
		List<CalendarioOeeCab> lista = new ArrayList<CalendarioOeeCab>();
		lista = em.createNativeQuery(sql, CalendarioOeeCab.class)
				.getResultList();
		if (lista.size() > 0)
			return false;
		return true;
	}

	/**
	 * Metodo que guarda los datos en la bd
	 * 
	 * @return
	 */
	public String save() {
		if (check()) {
			try {
				Boolean cumple = false;
				if (lunes) {
					cumple = false;
					if (horaDesdeLunes != null && !horaDesdeLunes.equals("")
							&& horaHastaLunes != null
							&& !horaHastaLunes.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeLunes);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setLunHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaLunes);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setLunHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}
				if (martes) {
					cumple = false;
					if (horaDesdeMartes != null && !horaDesdeMartes.equals("")
							&& horaHastaMartes != null
							&& !horaHastaMartes.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeMartes);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setMarHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaMartes);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setMarHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}

				if (miercoles) {
					cumple = false;
					if (horaDesdeMiercoles != null
							&& !horaDesdeMiercoles.equals("")
							&& horaHastaMiercoles != null
							&& !horaHastaMiercoles.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeMiercoles);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setMieHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaMiercoles);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setMieHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}

				if (jueves) {
					cumple = false;
					if (horaDesdeJueves != null && !horaDesdeJueves.equals("")
							&& horaHastaJueves != null
							&& !horaHastaJueves.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeJueves);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setJueHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaJueves);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setJueHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}

				if (viernes) {
					cumple = false;
					if (horaDesdeViernes != null
							&& !horaDesdeViernes.equals("")
							&& horaHastaViernes != null
							&& !horaHastaViernes.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeViernes);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setVieHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaViernes);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setVieHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}

				if (sabado) {
					cumple = false;
					if (horaDesdeSabado != null && !horaDesdeSabado.equals("")
							&& horaHastaSabado != null
							&& !horaHastaSabado.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeSabado);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setSabHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaSabado);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setSabHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}

				if (domingo) {
					cumple = false;
					if (horaDesdeDomingo != null
							&& !horaDesdeDomingo.equals("")
							&& horaHastaDomingo != null
							&& !horaHastaDomingo.equals(""))
						cumple = true;
					if (cumple) {
						int[] horaDesde = getHora(horaDesdeDomingo);
						Date fechaDesde = new Date();
						fechaDesde.setHours(horaDesde[0]);
						fechaDesde.setMinutes(horaDesde[1]);
						calendarioOeeCab.setDomHoraDesde(fechaDesde);
						int[] horaHasta = getHora(horaHastaDomingo);
						Date fechaHasta = new Date();
						fechaHasta.setHours(horaHasta[0]);
						fechaHasta.setMinutes(horaHasta[1]);
						calendarioOeeCab.setDomHoraHasta(fechaHasta);
						cargadoHorario = true;
					} else {
						statusMessages.clear();
						statusMessages
								.add(Severity.INFO,
										"Debe ingresar los valores de Horas correspondientes al día seleccionado");
						return null;
					}
				}
				if (cargadoHorario) {
					calendarioOeeCab.setConfiguracionUoCab(configuracionUoCab);
					calendarioOeeCabHome.setInstance(calendarioOeeCab);
					String resultado = calendarioOeeCabHome.persist();
					if (resultado.equals("persisted")) {

						for (CalendarioOeeDet det : listaDetOee) {
							det.setCalendarioOeeCab(calendarioOeeCabHome
									.getInstance());
							det.setFechaAlta(new Date());
							det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
							em.persist(det);
							em.flush();
						}

						statusMessages.clear();
						statusMessages.add(Severity.INFO, SeamResourceBundle
								.getBundle().getString("GENERICO_MSG"));

					}
					return resultado;
				}
				else{
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SeamResourceBundle
							.getBundle().getString("CU294_msg"));
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El registro ingresado ya existe, favor verificar");
			return null;
		}
	}

	/**
	 * Metodo que guarda los datos en la bd
	 * 
	 * @return
	 */
	public String update() {

		try {
			Boolean cumple = false;
			if (lunes) {
				cumple = false;
				if (horaDesdeLunes != null && !horaDesdeLunes.equals("")
						&& horaHastaLunes != null && !horaHastaLunes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeLunes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setLunHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaLunes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setLunHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}
			if (martes) {
				cumple = false;
				if (horaDesdeMartes != null && !horaDesdeMartes.equals("")
						&& horaHastaMartes != null
						&& !horaHastaMartes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeMartes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setMarHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaMartes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setMarHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (miercoles) {
				cumple = false;
				if (horaDesdeMiercoles != null
						&& !horaDesdeMiercoles.equals("")
						&& horaHastaMiercoles != null
						&& !horaHastaMiercoles.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeMiercoles);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setMieHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaMiercoles);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setMieHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (jueves) {
				cumple = false;
				if (horaDesdeJueves != null && !horaDesdeJueves.equals("")
						&& horaHastaJueves != null
						&& !horaHastaJueves.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeJueves);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setJueHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaJueves);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setJueHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (viernes) {
				cumple = false;
				if (horaDesdeViernes != null && !horaDesdeViernes.equals("")
						&& horaHastaViernes != null
						&& !horaHastaViernes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeViernes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setVieHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaViernes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setVieHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (sabado) {
				cumple = false;
				if (horaDesdeSabado != null && !horaDesdeSabado.equals("")
						&& horaHastaSabado != null
						&& !horaHastaSabado.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeSabado);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setSabHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaSabado);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setSabHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (domingo) {
				cumple = false;
				if (horaDesdeDomingo != null && !horaDesdeDomingo.equals("")
						&& horaHastaDomingo != null
						&& !horaHastaDomingo.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeDomingo);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioOeeCab.setDomHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaDomingo);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioOeeCab.setDomHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}
			if (!cargadoHorario) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU294_msg"));
				return null;
			}
			calendarioOeeCab.setConfiguracionUoCab(configuracionUoCab);
			calendarioOeeCabHome.setInstance(calendarioOeeCab);
			String resultado = calendarioOeeCabHome.update();
			if (resultado.equals("updated")) {

				for (CalendarioOeeDet det : listaDetOee) {
					if (det.getCalendarioOeeCab() == null) {
						det.setCalendarioOeeCab(calendarioOeeCabHome
								.getInstance());
						det.setFechaAlta(new Date());
						det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(det);
						em.flush();
					}
				}

				for (CalendarioOeeDet ax : listaDetOeeAux) {

					ax.setFechaMod(new Date());
					ax.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(ax);
					em.flush();

				}

				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));

			}
			return resultado;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * @param horaCad
	 * @return
	 */
	private int[] getHora(String horaCad) {
		String[] horas = horaCad.split(":");
		if (horas.length != 2) {
			return null;
		} else {
			String hora = horas[0];
			String minuto = horas[1];
			try {
				int hh = Integer.parseInt(hora);
				int mm = Integer.parseInt(minuto);

				if (hh < 0 || hh > 23 || mm < 0 || mm >= 60) {
					return null;
				}
				int[] v = new int[2];
				v[0] = hh;
				v[1] = mm;
				return v;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public CalendarioOeeCab getCalendarioOeeCab() {
		return calendarioOeeCab;
	}

	public void setCalendarioOeeCab(CalendarioOeeCab calendarioOeeCab) {
		this.calendarioOeeCab = calendarioOeeCab;
	}

	public Boolean getLunes() {
		return lunes;
	}

	public void setLunes(Boolean lunes) {
		this.lunes = lunes;
	}

	public Boolean getMartes() {
		return martes;
	}

	public void setMartes(Boolean martes) {
		this.martes = martes;
	}

	public Boolean getMiercoles() {
		return miercoles;
	}

	public void setMiercoles(Boolean miercoles) {
		this.miercoles = miercoles;
	}

	public Boolean getJueves() {
		return jueves;
	}

	public void setJueves(Boolean jueves) {
		this.jueves = jueves;
	}

	public Boolean getViernes() {
		return viernes;
	}

	public void setViernes(Boolean viernes) {
		this.viernes = viernes;
	}

	public Boolean getSabado() {
		return sabado;
	}

	public void setSabado(Boolean sabado) {
		this.sabado = sabado;
	}

	public Boolean getDomingo() {
		return domingo;
	}

	public void setDomingo(Boolean domingo) {
		this.domingo = domingo;
	}

	public String getHoraDesdeLunes() {
		return horaDesdeLunes;
	}

	public void setHoraDesdeLunes(String horaDesdeLunes) {
		this.horaDesdeLunes = horaDesdeLunes;
	}

	public String getHoraHastaLunes() {
		return horaHastaLunes;
	}

	public void setHoraHastaLunes(String horaHastaLunes) {
		this.horaHastaLunes = horaHastaLunes;
	}

	public String getHoraDesdeMartes() {
		return horaDesdeMartes;
	}

	public void setHoraDesdeMartes(String horaDesdeMartes) {
		this.horaDesdeMartes = horaDesdeMartes;
	}

	public String getHoraHastaMartes() {
		return horaHastaMartes;
	}

	public void setHoraHastaMartes(String horaHastaMartes) {
		this.horaHastaMartes = horaHastaMartes;
	}

	public String getHoraDesdeMiercoles() {
		return horaDesdeMiercoles;
	}

	public void setHoraDesdeMiercoles(String horaDesdeMiercoles) {
		this.horaDesdeMiercoles = horaDesdeMiercoles;
	}

	public String getHoraHastaMiercoles() {
		return horaHastaMiercoles;
	}

	public void setHoraHastaMiercoles(String horaHastaMiercoles) {
		this.horaHastaMiercoles = horaHastaMiercoles;
	}

	public String getHoraDesdeJueves() {
		return horaDesdeJueves;
	}

	public void setHoraDesdeJueves(String horaDesdeJueves) {
		this.horaDesdeJueves = horaDesdeJueves;
	}

	public String getHoraHastaJueves() {
		return horaHastaJueves;
	}

	public void setHoraHastaJueves(String horaHastaJueves) {
		this.horaHastaJueves = horaHastaJueves;
	}

	public String getHoraDesdeViernes() {
		return horaDesdeViernes;
	}

	public void setHoraDesdeViernes(String horaDesdeViernes) {
		this.horaDesdeViernes = horaDesdeViernes;
	}

	public String getHoraHastaViernes() {
		return horaHastaViernes;
	}

	public void setHoraHastaViernes(String horaHastaViernes) {
		this.horaHastaViernes = horaHastaViernes;
	}

	public String getHoraDesdeSabado() {
		return horaDesdeSabado;
	}

	public void setHoraDesdeSabado(String horaDesdeSabado) {
		this.horaDesdeSabado = horaDesdeSabado;
	}

	public String getHoraHastaSabado() {
		return horaHastaSabado;
	}

	public void setHoraHastaSabado(String horaHastaSabado) {
		this.horaHastaSabado = horaHastaSabado;
	}

	public String getHoraDesdeDomingo() {
		return horaDesdeDomingo;
	}

	public void setHoraDesdeDomingo(String horaDesdeDomingo) {
		this.horaDesdeDomingo = horaDesdeDomingo;
	}

	public String getHoraHastaDomingo() {
		return horaHastaDomingo;
	}

	public void setHoraHastaDomingo(String horaHastaDomingo) {
		this.horaHastaDomingo = horaHastaDomingo;
	}

	public List<CalendarioOeeDet> getListaDetOee() {
		return listaDetOee;
	}

	public void setListaDetOee(List<CalendarioOeeDet> listaDetOee) {
		this.listaDetOee = listaDetOee;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Date getFechaFestiva() {
		return fechaFestiva;
	}

	public void setFechaFestiva(Date fechaFestiva) {
		this.fechaFestiva = fechaFestiva;
	}

	public String getDescripcionFestiva() {
		return descripcionFestiva;
	}

	public void setDescripcionFestiva(String descripcionFestiva) {
		this.descripcionFestiva = descripcionFestiva;
	}

	public Boolean getCargadoHorario() {
		return cargadoHorario;
	}

	public void setCargadoHorario(Boolean cargadoHorario) {
		this.cargadoHorario = cargadoHorario;
	}

}
