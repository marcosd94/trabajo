package py.com.excelsis.sicca.dto;


import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PreguntaConfigurada;
import py.com.excelsis.sicca.entity.RespuestaMultipleConfigurada;
import py.com.excelsis.sicca.entity.ResultadoEncuesta;

public class ResutadosEncuestasPlantillasDTO{

	private ResultadoEncuesta resultadoEncuesta;
	private RespuestaMultipleConfigurada respuestaMultipleConfigurada;
	private DatosEspecificos grupo;
	private String respuestaTxt;
	private Long cntRpta;
	private boolean esGrupo;
	private boolean rptTxt;
	private PreguntaConfigurada preguntaConfigurada;
	
	
	public ResutadosEncuestasPlantillasDTO() {
		
	}



	public ResultadoEncuesta getResultadoEncuesta() {
		return resultadoEncuesta;
	}



	public void setResultadoEncuesta(ResultadoEncuesta resultadoEncuesta) {
		this.resultadoEncuesta = resultadoEncuesta;
	}



	public RespuestaMultipleConfigurada getRespuestaMultipleConfigurada() {
		return respuestaMultipleConfigurada;
	}



	public void setRespuestaMultipleConfigurada(
			RespuestaMultipleConfigurada respuestaMultipleConfigurada) {
		this.respuestaMultipleConfigurada = respuestaMultipleConfigurada;
	}



	public DatosEspecificos getGrupo() {
		return grupo;
	}



	public void setGrupo(DatosEspecificos grupo) {
		this.grupo = grupo;
	}



	public String getRespuestaTxt() {
		return respuestaTxt;
	}



	public void setRespuestaTxt(String respuestaTxt) {
		this.respuestaTxt = respuestaTxt;
	}



	public Long getCntRpta() {
		return cntRpta;
	}



	public void setCntRpta(Long cntRpta) {
		this.cntRpta = cntRpta;
	}



	public boolean isEsGrupo() {
		return esGrupo;
	}



	public void setEsGrupo(boolean esGrupo) {
		this.esGrupo = esGrupo;
	}



	public boolean isRptTxt() {
		return rptTxt;
	}



	public void setRptTxt(boolean rptTxt) {
		this.rptTxt = rptTxt;
	}



	public PreguntaConfigurada getPreguntaConfigurada() {
		return preguntaConfigurada;
	}



	public void setPreguntaConfigurada(PreguntaConfigurada preguntaConfigurada) {
		this.preguntaConfigurada = preguntaConfigurada;
	}
	
	




	
	
}
