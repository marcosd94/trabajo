package py.com.excelsis.sicca.session.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CheckedInputStream;
import java.util.zip.Adler32;

public class CheckSum {

	public void comprobar() {
		try {
			String string = new String("esto es una prueba");
			//System.out.println("The data size is " + string.length());
			byte buffer[] = string.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			CheckedInputStream cis = new CheckedInputStream(bais, new Adler32());
			byte readBuffer[] = new byte[5];
			//System.out.println(cis.getChecksum().getValue());
			// while (cis.read(readBuffer) >= 0) {
			// long value = cis.getChecksum().getValue();
			// System.out.println("The value of checksum is " + value);
			// }
		} catch (Exception e) {
			System.out.println("Exception has been caught" + e);
		}
	}

	/***
	 * Convierte un arreglo de bytes a String usando valores hexadecimales
	 * @param digest arreglo de bytes a convertir 18 * @return String
	 * creado a partir de <code>digest</code> 19
	 */

	private static String toHexadecimal(byte[] digest) {
		String hash = "";
		for (byte aux : digest) {
			int b = aux & 0xff;
			if (Integer.toHexString(b).length() == 1)
				hash += "0";
			hash += Integer.toHexString(b);
		}
		return hash;
	}

	/***
	 * Realiza la suma de verificaci�n de un archivo mediante MD5
	 * 
	 * @param archivo
	 *            archivo a que se le aplicara la suma de verificaci�n
	 * @return valor de la suma de verificaci�n.
	 */
	
	public static String MD5Checksum(File archivo) {
		byte[] textBytes = new byte[1024];
		MessageDigest md = null;
		int read = 0;
		String md5 = null;
		try {
			InputStream is = new FileInputStream(archivo);
			md = MessageDigest.getInstance("MD5");
			while ((read = is.read(textBytes)) > 0) {
				md.update(textBytes, 0, read);
			}
			is.close();
			byte[] md5sum = md.digest();
			md5 = toHexadecimal(md5sum);
		} catch (FileNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (IOException e) {
		}

		return md5;
	}
}
