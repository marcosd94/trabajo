package py.com.excelsis.sicca.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;



/**
*
* Clase utilizada para dibujar el organigrama de una OEE
*/

/**
*
* @author Eduardo Rivas
*/
public class OrganigramaUtil {
	
	/**
     * Este metodo se encarga de generar una imagen que representa el organigrama.
     * @param: contenidoArchivo: es el contenido del archivo que representa el organigrama.
     * @param: nombreArchivo: es el nombre del archivo generado.
     * @param: nombreImagen: es el nombre de la imagen generada.
     */
    public static void dibujarOrganigrama(String contenidoArchivo, String nombreArchivo, String nombreImagen){

        generarImagen(contenidoArchivo, nombreArchivo, nombreImagen);
        
        try{
            Thread.sleep(1500); // Esperamos a que se genere toda la imagen antes de volver
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    /*
     * Genera el archivo necesario para construir la imagen a partir de el.
     * Y luego crea la imagen del organigrama.
     */
    private static void generarImagen(String contenidoArchivo, String nombreArchivo, String nombreImagen) {

        // Se crea el archivo
        try {
            FileWriter fw = new FileWriter(nombreArchivo);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter salida = new PrintWriter(bw);
            salida.println(contenidoArchivo);
            salida.close();
        }
        catch (Exception err) {
            err.printStackTrace();
            System.out.println("\nError en: generarImagen al intentar generar el archivo: " + nombreArchivo);
        }
        
        // Se genera la imagen
        String comando = "dot -o " + nombreImagen + " -Tpng " + nombreArchivo + " -Gcharset=latin1";
        
        if (OSValidator.isWindows()){
        	comando = "cmd /c c:\\Programs\\Graphviz\\bin\\dot -o " + nombreImagen + " -Tpng " + nombreArchivo + " -Gcharset=latin1";
        	//comando = "cmd /c dot -o " + nombreImagen + " -Tpng " + "c:\\Instaladores\\jboss-5.1.0.GA\\bin\\" + nombreArchivo;
    	}

     //   System.out.println("\n\nComando ejecutado----------------->> " + comando);
        
        try {
            Process p = Runtime.getRuntime().exec(comando);
        	//Process p = Runtime.getRuntime().exec(new String[] {comando});
            if(p.waitFor() != 0)
            	System.out.println(getOutAndErrStream(p));
        }
        catch (Exception err) {
            err.printStackTrace();
            System.out.println("\nError en: generarImagen al intentar generar la imagen: " + nombreImagen);
        }
        
    }

    
    private static String getOutAndErrStream(Process p) {

		StringBuffer cmd_out = new StringBuffer("");
		if (p != null) {
			BufferedReader is = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String buf = "";
			try {
				while ((buf = is.readLine()) != null) {
					cmd_out.append(buf);
					cmd_out.append(System.getProperty("line.separator"));
				}
				is.close();
				is = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				while ((buf = is.readLine()) != null) {
					cmd_out.append(buf);
					cmd_out.append("\n");
				}
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cmd_out.toString();
	}

}
