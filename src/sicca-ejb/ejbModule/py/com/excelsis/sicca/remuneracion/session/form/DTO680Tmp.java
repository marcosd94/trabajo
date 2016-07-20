package py.com.excelsis.sicca.remuneracion.session.form;

import org.hibernate.JDBCException;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Persona;

public class DTO680Tmp {
	private Integer anhio;
	private Integer mes;
	private Integer nivelEnti;
	private Integer entidad;
	private Integer oee;
	private Integer linea;
	private String cedula;
	private String nombres;
	private String apellidos;
	private String estado;
	private Integer remuneracionTot;
	private Integer objetoGto;
	private Integer fuenteFinancia;
	private String categ;
	private Integer presup;
	private Integer deven;
	private String concep;
	private String movimiento;
	private String lugar;
	private String cargo;
	private String funcion;
	private String horario;
	private String discapaci;
	private Integer tipoDiscapaci;
	private Integer anhioIngreso;
	private String fecha;
	private String errorColumna;
	private Long idEp;
	private int index;
	private ConfiguracionUoCab confUoCab;
	private Persona persona;

	/*private Integer tipoPresupueso;
	private Integer programa;

	public Integer getTipoPresupueso() {
		return tipoPresupueso;
	}

	public void setTipoPresupueso(Integer tipoPresupueso) {
		this.tipoPresupueso = tipoPresupueso;
	}

	public Integer getPrograma() {
		return programa;
	}

	public void setPrograma(Integer programa) {
		this.programa = programa;
	}*/

	public static DTO680Tmp descomponerLinea(String unaLinea) {
		String[] compos = unaLinea.split(";");
		if (compos.length < 26) {
			//return null;
		}
		DTO680Tmp dto = new DTO680Tmp();
		try {
			if (compos[0] != null && !compos[0].trim().isEmpty()){
				try{
					dto.setAnhio(Integer.parseInt(compos[0]));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Año. ");
				}	
			}
			else
				dto.setErrorColumna("Error, columna Año vacia. ");
			
			if (compos[1] != null && !compos[1].trim().isEmpty()){
				try{
					dto.setMes(Integer.parseInt(compos[1]));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Mes. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Mes vacia. ");
			
			if (compos[2] != null && !compos[2].trim().isEmpty()){
				try{
					dto.setNivelEnti(Integer.parseInt(compos[2]));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Nivel. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Nivel vacia. ");
			
			if (compos[3] != null && !compos[3].trim().isEmpty()){
				try{
					dto.setEntidad(Integer.parseInt(compos[3]));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Entidad. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Entidad vacia. ");
			
			if (compos[4] != null && !compos[4].trim().isEmpty()){
				try{
					dto.setOee(Integer.parseInt(compos[4]));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Depende/OEE. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna OEE vacia. ");
			
			if (compos[5] != null && !compos[5].trim().isEmpty()){
				try{
					dto.setLinea(Integer.parseInt(compos[5].trim()));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Linea. ");
				}
			}
			if (compos[6] != null && !compos[6].trim().isEmpty()){
				try{
					dto.setCedula(compos[6].trim());
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Cedula. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Cedula vacia. ");
			
			if (compos[7] != null && !compos[7].trim().isEmpty()){
				try{
					if(compos[7].length() <= 50)
						dto.setNombres(compos[7].trim());
					else
						dto.setErrorColumna("Valor muy extenso en columna Nombres");
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Nombres. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Nombres vacia. ");
			
			if (compos[8] != null && !compos[8].trim().isEmpty()){
				try{
					if(compos[8].length() <= 50)
						dto.setApellidos(compos[8].trim());
					else
						dto.setErrorColumna("Valor muy extenso en columna Apellidos");
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Apellidos. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Apellidos vacia. ");
			
			if (compos[9] != null && !compos[9].trim().isEmpty()){
				try{
					if(compos[9].length() <= 12)
						dto.setEstado(compos[9].trim());
					else
						dto.setErrorColumna("Valor muy extenso en columna Estado");
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Estado. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Estado vacia. ");
			
			if (compos[10] != null && !compos[10].trim().isEmpty()){
				try{
					dto.setRemuneracionTot(Integer.parseInt(compos[10].trim()));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Remuneracion Total. ");
				}
			}
			if (compos[11] != null && !compos[11].trim().isEmpty()){
				try{
					dto.setObjetoGto(Integer.parseInt(compos[11].trim()));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Objeto Gto. ");
				}
			}
			else
				dto.setErrorColumna("Error, columna Objeto Gto. vacia. ");
			
			if (compos[12] != null && !compos[12].trim().isEmpty()){
				try{
					dto.setFuenteFinancia(Integer.parseInt(compos[12].trim()));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Fuente Financiamiento. ");
				}
			}else
				dto.setErrorColumna("Error, columna F.F. vacia. ");
			if (compos[13] != null && !compos[13].trim().isEmpty()){
				try{
					if(compos[13].length() <= 4)
						dto.setCateg(compos[13].trim());
					else
						dto.setErrorColumna("Valor muy extenso en columna Categoria");
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Categoria. ");
				}
			}
			if (compos[14] != null && !compos[14].trim().isEmpty()){
				try{
					dto.setPresup(Integer.parseInt(compos[14]));
				}catch (Exception e) {
					dto.setErrorColumna("Error en columna Presupuestado. ");
				}
			}else{
				dto.setErrorColumna("Error, columna Presupuestado vacia. ");
			}
				
			if (compos.length > 15) {
				if (compos[15] != null && !compos[15].trim().isEmpty()){
					try{
						dto.setDeven(Integer.parseInt(compos[15]));
						if(dto.getPresup() != null && dto.getPresup() < dto.getDeven())
						  dto.setErrorColumna("Devengado no puede ser mayor al Presupuestado.");
					}catch (Exception e) {
						dto.setErrorColumna("Error en columna Devengado. ");
					}
				}else
					dto.setErrorColumna("Error, columna Devengado vacia. ");
				
				if (compos.length > 16) {
					if (compos[16] != null && !compos[16].trim().isEmpty()){
						try{
							if(compos[16].length() <= 200)
								dto.setConcep(compos[16].trim());
							else
								dto.setErrorColumna("Valor muy extenso en columna Concepto");
						}catch (Exception e) {
							dto.setErrorColumna("Error en columna Concepto. ");
						}
					}
					if (compos.length > 17) {
						if (compos[17] != null && !compos[17].trim().isEmpty()){
							try{
								if(compos[17].length() <= 3)
									dto.setMovimiento(compos[17].trim());
								else
									dto.setErrorColumna("Valor muy extenso en columna Movimiento");
							}catch (Exception e) {
								dto.setErrorColumna("Error en columna Movimiento. ");
							}
						}
						
						if (compos.length > 18) {
							if (compos[18] != null && !compos[19].trim().isEmpty()){
								try{
									if(compos[18].length() <= 125)
										dto.setLugar(compos[18].trim());
									else
										dto.setErrorColumna("Valor muy extenso en columna Lugar");
								}catch (Exception e) {
									dto.setErrorColumna("Error en columna Lugar. ");
								}
							}
							
							if (compos.length > 19) {
								if (compos[19] != null && !compos[19].trim().isEmpty()){
									try{
										if(compos[19].length() <= 250)
											dto.setCargo(compos[19].trim());
										else
											dto.setErrorColumna("Valor muy extenso en columna Cargo");
									}catch (Exception e) {
										dto.setErrorColumna("Error en columna Cargo. ");
									}
								}
				
								if (compos.length > 20) {
									if (compos[20] != null && !compos[20].trim().isEmpty()){
										try{
											if(compos[20].length() <= 500)
												dto.setFuncion(compos[20].trim());
											else
												dto.setErrorColumna("Valor muy extenso en columna Funcion Cumplida");
										}catch (Exception e) {
											dto.setErrorColumna("Error en columna Funcion. ");
										}
									}
									if (compos.length > 21) {
										if (compos[21] != null && !compos[21].trim().isEmpty()){
											try{
												dto.setHorario(compos[21].trim());
											}catch (Exception e) {
												dto.setErrorColumna("Error en columna Horario. ");
											}
										}
					
										if (compos.length > 22) {
											if (compos[22] != null && !compos[22].trim().isEmpty()){
												try{
													dto.setDiscapaci(compos[22].trim());
													if (compos[22].trim().equalsIgnoreCase("S") || compos[22].trim().equalsIgnoreCase("SI")){
//														if (compos[23] == null || compos[23].trim().isEmpty()){
//															dto.setErrorColumna("Error, no especificado Tipo Discapacidad. ");
//														}	
														if (compos.length > 23) {
															if (compos[23] != null && !compos[23].trim().isEmpty()){
																try{
																	dto.setTipoDiscapaci(Integer.parseInt(compos[23].trim()));
																}catch (Exception e) {
																	dto.setErrorColumna("Error en columna Tipo Discapacidad. ");
																}
															}else{
																dto.setErrorColumna("Error Tipo Discapacidad no especificado. ");
														}
													}
												  }
												}catch (Exception e) {
													dto.setErrorColumna("Error en columna Discapacidad. ");
												}
											/*else{
												dto.setErrorColumna("Error, columna Discapacidad vacía. ");
											}*/
										
											if (compos.length > 23 && compos.length > 24) {
											
													if (compos[24] != null && !compos[24].trim().isEmpty()){
														try{
															if(compos[24].length() <= 4)
															  dto.setAnhioIngreso(Integer.parseInt(compos[24].trim()));
															else
															  dto.setErrorColumna("Valor muy extenso en columna Año Ingreso.");
														}catch (Exception e) {
															dto.setErrorColumna("Error en columna Año Ingreso. ");
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return dto;
		}catch (JDBCException jdbce) {
			jdbce.getSQLException().getNextException().printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return dto;
		}
	}

	/*public static DTO680Tmp descomponerLinea2(String unaLinea) {
		String[] compos = unaLinea.split(";");
		if (compos.length < 17) {
			return null;
		}
		try {
			DTO680Tmp dto = new DTO680Tmp();

			if (compos[0] != null && !compos[0].trim().isEmpty())
				dto.setNivelEnti(Integer.parseInt(compos[0]));
			if (compos[1] != null && !compos[1].trim().isEmpty())
				dto.setEntidad(Integer.parseInt(compos[1]));
			if (compos[2] != null && !compos[2].trim().isEmpty())
				dto.setTipoPresupueso(Integer.parseInt(compos[2]));
			if (compos[3] != null && !compos[3].trim().isEmpty())
				dto.setPrograma(Integer.parseInt(compos[3]));
			if (compos[4] != null && !compos[4].trim().isEmpty())
				dto.setAnhio(Integer.parseInt(compos[4]));
			if (compos[5] != null && !compos[5].trim().isEmpty())
				dto.setMes(Integer.parseInt(compos[5]));
			if (compos[6] != null && !compos[6].trim().isEmpty())
				dto.setCedula(compos[6].trim());
			if (compos[7] != null && !compos[7].trim().isEmpty())
				dto.setNombres(compos[7].trim());
			if (compos[8] != null && !compos[8].trim().isEmpty())
				dto.setApellidos(compos[8].trim());
			if (compos[9] != null && !compos[9].trim().isEmpty())
				dto.setLinea(Integer.parseInt(compos[9]));
			if (compos[10] != null && !compos[10].trim().isEmpty())
				dto.setObjetoGto(Integer.parseInt(compos[10].trim()));
			if (compos[11] != null && !compos[11].trim().isEmpty())
				dto.setDescripConcepto(compos[11].trim());
			if (compos[12] != null && !compos[12].trim().isEmpty())
				dto.setFuenteFinancia(Integer.parseInt(compos[12].trim()));
			if (compos[13] != null && !compos[13].trim().isEmpty())
				dto.setCateg(compos[13].trim());
			if (compos[14] != null && !compos[14].trim().isEmpty())
				dto.setDescripCatego(compos[14].trim());
			if (compos[15] != null && !compos[15].trim().isEmpty())
				dto.setPresup(Integer.parseInt(compos[15]));
			if (compos[16] != null && !compos[16].trim().isEmpty())
				dto.setDeven(Integer.parseInt(compos[16]));
			

			
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/

	public Integer getAnhio() {
		return anhio;
	}

	public void setAnhio(Integer anhio) {
		this.anhio = anhio;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getNivelEnti() {
		return nivelEnti;
	}

	public void setNivelEnti(Integer nivelEnti) {
		this.nivelEnti = nivelEnti;
	}

	public Integer getEntidad() {
		return entidad;
	}

	public void setEntidad(Integer entidad) {
		this.entidad = entidad;
	}
	
	public Integer getOee() {
		return oee;
	}

	public void setOee(Integer oee) {
		this.oee = oee;
	}
	
	public Integer getLinea() {
		return linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public Integer getRemuneracionTot() {
		return remuneracionTot;
	}

	public void setRemuneracionTot(Integer remuneracionTot) {
		this.remuneracionTot = remuneracionTot;
	}

	public Integer getObjetoGto() {
		return objetoGto;
	}

	public void setObjetoGto(Integer objetoGto) {
		this.objetoGto = objetoGto;
	}

	public Integer getFuenteFinancia() {
		return fuenteFinancia;
	}

	public void setFuenteFinancia(Integer fuenteFinancia) {
		this.fuenteFinancia = fuenteFinancia;
	}

	public String getCateg() {
		return categ;
	}

	public void setCateg(String categ) {
		this.categ = categ;
	}

	public Integer getPresup() {
		return presup;
	}

	public void setPresup(Integer presup) {
		this.presup = presup;
	}

	public Integer getDeven() {
		return deven;
	}

	public void setDeven(Integer deven) {
		this.deven = deven;
	}

	public String getConcep() {
		return concep;
	}

	public void setConcep(String concep) {
		this.concep = concep;
	}
	
	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	
	public String getFuncion() {
		return funcion;
	}

	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}
	
	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public String getDiscapaci() {
		return discapaci;
	}

	public void setDiscapaci(String discapaci) {
		this.discapaci = discapaci;
	}
	
	public Integer getTipoDiscapaci() {
		return tipoDiscapaci;
	}

	public void setTipoDiscapaci(Integer tipoDiscapaci) {
		this.tipoDiscapaci = tipoDiscapaci;
	}
	
	public Integer getAnhioIngreso() {
		return anhioIngreso;
	}

	public void setAnhioIngreso(Integer anhioIngreso) {
		this.anhioIngreso = anhioIngreso;
	}
	
	public Long getIdEp() {
		return idEp;
	}

	public void setIdEp(Long idEp) {
		this.idEp = idEp;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getErrorColumna() {
		return errorColumna;
	}

	public void setErrorColumna(String errorColumna) {
		this.errorColumna = errorColumna;
	}
	
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return confUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab confUoCab) {
		this.confUoCab = confUoCab;
	}
	
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	
	public String getCedulaValida(String cedula){
		String cedulaValida;
		if(cedula.length()<2){
			cedulaValida = cedula;
		}
		else{
			if(cedula.substring(0, 3).equalsIgnoreCase("EXT"))
				//cedulaValida = cedula.substring(3);
				//EN CASO DE SER EXTRANJERO RETORNA EL DOCUMENTO DE IDENTIDAD SIN "EXT" Y SIN EL CODIGO CORTO DEL PAIS  MP - 17/05/2016
				cedulaValida = cedula.substring(5);
			else
				cedulaValida = cedula;
		}
		return cedulaValida;
	}

}
