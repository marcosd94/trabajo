package py.com.excelsis.sicca.remuneracion.session.form;

public class DTO680 {
	private Integer anhio;
	private Integer mes;
	private Integer nivelEnti;
	private Integer entidad;
	private String cedula;
	private String nombres;
	private String apellidos;
	private String estado;
	private Integer objetoGto;
	private Integer fuenteFinancia;
	private String categ;
	private Integer presup;
	private Integer deven;
	private String movimiento;
	private String fecha;
	private String cargo;
	private String discapaci;
	private String descripConcepto;
	private Integer linea;
	private String descripCatego;
	private Integer tipoPresupueso;
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
	}

	public static DTO680 descomponerLinea(String unaLinea) {
		String[] compos = unaLinea.split(";");
		if (compos.length < 21) {
			//return null;
		}
		try {
			DTO680 dto = new DTO680();
			if (compos[0] != null && !compos[0].trim().isEmpty())
				dto.setAnhio(Integer.parseInt(compos[0]));
			if (compos[1] != null && !compos[1].trim().isEmpty())
				dto.setMes(Integer.parseInt(compos[1]));
			if (compos[2] != null && !compos[2].trim().isEmpty())
				dto.setNivelEnti(Integer.parseInt(compos[2]));
			if (compos[3] != null && !compos[3].trim().isEmpty())
				dto.setEntidad(Integer.parseInt(compos[3]));
			if (compos[4] != null && !compos[4].trim().isEmpty())
				dto.setCedula(compos[4].trim());
			if (compos[5] != null && !compos[5].trim().isEmpty())
				dto.setNombres(compos[5].trim());
			if (compos[6] != null && !compos[6].trim().isEmpty())
				dto.setApellidos(compos[6].trim());
			if (compos[7] != null && !compos[7].trim().isEmpty())
				dto.setEstado(compos[7].trim());
			if (compos[8] != null && !compos[8].trim().isEmpty())
				dto.setObjetoGto(Integer.parseInt(compos[8].trim()));
			if (compos[9] != null && !compos[9].trim().isEmpty())
				dto.setFuenteFinancia(Integer.parseInt(compos[9].trim()));
			if (compos[10] != null && !compos[10].trim().isEmpty())
				dto.setCateg(compos[10].trim());
			if (compos[11] != null && !compos[11].trim().isEmpty())
				dto.setPresup(Integer.parseInt(compos[11]));
			if (compos[12] != null && !compos[12].trim().isEmpty())
				dto.setDeven(Integer.parseInt(compos[12]));
			if (compos[13] != null && !compos[13].trim().isEmpty())
				dto.setMovimiento(compos[13].trim());
			if (compos[14] != null && !compos[14].trim().isEmpty())
				dto.setFecha(compos[14].trim());
			if (compos[15] != null && !compos[15].trim().isEmpty())
				dto.setCargo(compos[15].trim());
			if (compos[16] != null && !compos[16].trim().isEmpty())
				dto.setDescripConcepto(compos[16].trim());
			if (compos[17] != null && !compos[17].trim().isEmpty())
				dto.setLinea(Integer.parseInt(compos[17]));
			if (compos[18] != null && !compos[18].trim().isEmpty())
				dto.setDescripCatego(compos[18].trim());
			if (compos[19] != null && !compos[19].trim().isEmpty())
				dto.setDiscapaci(compos[19].trim());
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static DTO680 descomponerLinea2(String unaLinea) {
		String[] compos = unaLinea.split(";");
		if (compos.length < 17) {
			return null;
		}
		try {
			DTO680 dto = new DTO680();

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
	}

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

	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getDiscapaci() {
		return discapaci;
	}

	public void setDiscapaci(String discapaci) {
		this.discapaci = discapaci;
	}

	public String getDescripConcepto() {
		return descripConcepto;
	}

	public void setDescripConcepto(String descripConcepto) {
		this.descripConcepto = descripConcepto;
	}

	public Integer getLinea() {
		return linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}


	public String getDescripCatego() {
		return descripCatego;
	}

	public void setDescripCatego(String descripCatego) {
		this.descripCatego = descripCatego;
	}

}
