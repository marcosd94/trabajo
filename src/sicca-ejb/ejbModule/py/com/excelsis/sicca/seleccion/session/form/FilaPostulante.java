package py.com.excelsis.sicca.seleccion.session.form;

public class FilaPostulante {
	private String documento;
	private String nombres;
	private String apellidos;
	private String puntaje;
	private String tipo;
	private String paraguayo;
	private String admitido;
	private String pais;
	public Boolean valido;
	private String observacion;
	
	/**
	 * Parsea una linea de un archivo csv.
	 * Se espera que los datos esten dispuestos de la siguiente manera:
	 * 3391416;RODRIGO GABRIEL;SANTOS BACCHETTO
	 * 3644302;ENRIQUE DAVID;CESPEDES MENDEZ
	 * 4206214;SANTIAGO DANIEL;TORTORA MORALES
	 * @param linea
	 *            cadena de texto separada por puntos y comas
	 * @return
	 */

	public FilaPostulante(String linea) {
		String campos[] = linea.split(";");
		if(campos.length!=3){
			valido = false;
		} else {
			documento = campos[0];
			nombres = campos[1];
			apellidos = campos[2];
			valido = validar();
		}
	}
	public FilaPostulante(String linea,String aux) {
		String campos[] = linea.split(";");
		
		if(aux.equals("LISTA_CORTA")){
			if(campos.length<5){
				valido = false;
			} else {
				documento = campos[0];
				nombres = campos[1];
				apellidos = campos[2];
				puntaje = campos[3];
				tipo = campos[4];
				valido = validar();
			}
		}
		else if(aux.equals("LISTA_FINAL")){
			if(campos.length<4){
				valido = false;
			} else if (!campos[3].equalsIgnoreCase("S") && !campos[3].equalsIgnoreCase("E")){
				valido = false;
			}else {
				documento = campos[0];
				nombres = campos[1];
				apellidos = campos[2];
				tipo = campos[3];
				valido = validar();
			}
		}
		else if(aux.equals("POSTULANTES")){
			if(campos.length>=6){
				// CI;NOMBRES;APELLIDOS;PARAGUAYO;PAIS;ADMITIDO;OBSERVACIONES
				if(campos[0] != null && !campos[0].trim().equals("")){
					documento = campos[0].trim();
				}else{ 
					observacion = "Los datos de la columna CI son requeridos";
					valido = false;
					return;
				}	
				if(campos[1] != null && !campos[1].trim().equals("")){
					nombres = campos[1].trim();
				}else{
					observacion = "Los datos de la columna NOMBRES son requeridos";
					valido = false;
					return;
				}
				if(campos[2] != null && !campos[2].trim().equals("")){
				apellidos = campos[2].trim();
				}else{
					observacion = "Los datos de la columna APELLIDOS son requeridos";
					valido = false;
					return;		
				}
				if(campos[3] != null && !campos[3].trim().equals("")){
					if(campos[3].trim().equalsIgnoreCase("si") || campos[3].trim().equalsIgnoreCase("no")){
						paraguayo = campos[3].trim();
						if(campos[4] != null && !campos[4].equals("")){
							if((campos[4].trim().equalsIgnoreCase("paraguay") && paraguayo.trim().equalsIgnoreCase("no"))
							   || (!campos[4].trim().equalsIgnoreCase("paraguay") && paraguayo.trim().equalsIgnoreCase("si"))){
								observacion = "Verificar las columnas PARAGUAYO y PAIS";
								valido = false;
								return;	
							}else{
								pais = campos[4];
							}
								
						}else{
							observacion = "Los datos de la columna PAIS son requeridos";
							valido = false;
							return;	
						}
					}else{
						observacion = "Los datos de la columna PARAGUAYO son incorrectos, los valores posibles son SI - NO";
						valido = false;
						return;	
					}
				}else{
					observacion = "Los datos de la columna PARAGUAYO son requeridos";
					valido = false;
					return;	
				}
				if(campos[5] != null && !campos[5].trim().equals("")){
					if(campos[5].trim().equalsIgnoreCase("si") || campos[5].trim().equalsIgnoreCase("no")){
						admitido = campos[5].trim();
						if(admitido.equalsIgnoreCase("no")){					
							if(campos.length < 7){
								observacion = "Los datos de la columna OBSERVACIONES son requeridos";
								valido = false;
								return;	
							}else{
								if(campos[6] != null && !campos[6].trim().equals("")){
									observacion = campos[6];
								}else{
									observacion = "Los datos de la columna OBSERVACIONES son requeridos";
									valido = false;
									return;	
								}
							}
								
							}
					}else{
						observacion = "Los datos de la columna ADMITIDO son incorrectos, los valores posibles son SI - NO";
						valido = false;
						return;	
					}
				}else{
					observacion = "Los datos de la columna ADMITIDO son requeridos";
					valido = false;
					return;	
				}
				// Control de nacionalidad
				/*if(paraguayo.equalsIgnoreCase("no")){
					pais = campos[4];
				}
				else if(!paraguayo.equalsIgnoreCase("si")){
					valido = false;
				}
				
				// Control de admision
				admitido = campos[5];
				
				if(admitido.equalsIgnoreCase("no")){					
					if(campos.length < 7){
						observacion = "No se especificó motivo.";
					}else{
						observacion = campos[6];
					}
				}else if(!admitido.equalsIgnoreCase("si")){
					valido = false;
				}*/
				
				// Validar si es que no especificamos previamente valido=false
				if(valido==null){
					valido = validar();
				}
			}
			else{
				//observacion = " Verifique la información de la fila, Faltan datos OBLIGATORIOS "; MP -20/01/2016
				observacion = " Los datos de la columna ADMITIDO son requeridos ";
				valido = false;
			}
		}
	}
	
	@Override
	public String toString() {
		return "FilaPostulante [documento=" + documento + ", nombres="
				+ nombres + ", apellidos=" + apellidos + ", valido=" + valido
				+ ", observacion=" + observacion + "]";
	}

	private boolean validar(){
		return true;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
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
	public String getPuntaje() {
		return puntaje;
	}
	public void setPuntaje(String puntaje) {
		this.puntaje = puntaje;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Boolean getValido() {
		return valido;
	}

	public void setValido(Boolean valido) {
		this.valido = valido;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public String getParaguayo() {
		return paraguayo;
	}

	public void setParaguayo(String paraguayo) {
		this.paraguayo = paraguayo;
	}
	
	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}
	
	public String getAdmitido() {
		return admitido;
	}

	public void setAdmitido(String admitido) {
		this.admitido = admitido;
	}
}