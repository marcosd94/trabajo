package py.com.excelsis.sicca.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;

public class EvalReferencialPostulanteConverter implements Converter {
	
	private String split = "::";

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		String [] str = arg2.split(split);
		//System.out.println("Str:"+ str);
		EvalReferencialPostulante eval = new EvalReferencialPostulante();
		eval.setIdEvalReferencialPostulante(Long.parseLong(str[0]));
		eval.setPostulacion(new Postulacion());
		eval.getPostulacion().setPersonaPostulante(new PersonaPostulante());
		eval.getPostulacion().getPersonaPostulante().setUsuAlta(str[1]);
		eval.getPostulacion().getPersonaPostulante().setPersona(new Persona());
		eval.getPostulacion().getPersonaPostulante().getPersona().setNombres(str[2]);
		eval.getPostulacion().getPersonaPostulante().getPersona().setApellidos(str[3]);
		eval.setPuntajeRealizado(Float.parseFloat(str[4]));
		eval.setPresente(Boolean.valueOf(str[5]));
		return eval;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		EvalReferencialPostulante eval = (EvalReferencialPostulante)arg2;
		String retorno = eval.getIdEvalReferencialPostulante() + split + eval.getPostulacion().getPersonaPostulante().getUsuAlta() + 
			split + eval.getPostulacion().getPersonaPostulante().getPersona().getNombres() + split + eval.getPostulacion().getPersonaPostulante().getPersona().getApellidos()+
			split + eval.getPuntajeRealizado() + split + eval.getPresente();
		return retorno;
	}

}
