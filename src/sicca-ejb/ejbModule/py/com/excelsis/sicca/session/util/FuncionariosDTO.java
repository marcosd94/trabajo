package py.com.excelsis.sicca.session.util;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;

public class FuncionariosDTO {
	private ConfiguracionUoCab configuracionUoCab;
	private ConfiguracionUoDet configuracionUoDet;
	private String funcionarios;

	public FuncionariosDTO() {

	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(String funcionarios) {
		this.funcionarios = funcionarios;
	}

	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}
	
	

}
