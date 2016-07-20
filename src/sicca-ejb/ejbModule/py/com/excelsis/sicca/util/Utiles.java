package py.com.excelsis.sicca.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utiles {

	public static void actualizarCampos(Object objOrigen, Object objDestino)
			throws IllegalArgumentException, SecurityException, IllegalAccessException,
			NoSuchFieldException {
		Class classOrigen = objOrigen.getClass();
		Class classDestino = objDestino.getClass();
		for (Field o : classOrigen.getDeclaredFields()) {
			for (Field m : classDestino.getDeclaredFields()) {
				if (o.getName().equals(m.getName())) {
					System.out.println(o.getName() + ":" + o.getType().getName() + "::");
					classDestino.getDeclaredField(o.getName()).set(objDestino, classOrigen.getDeclaredField(o.getName()).get(objOrigen));
					if (classDestino.getDeclaredField(o.getName()).get(objDestino) != null)
						System.out.println("\t"
							+ classDestino.getDeclaredField(o.getName()).get(objDestino).toString());
					break;
				}
			}
		}
	}

	public static void copyDirectory(File srcPath, File dstPath) throws Exception {
		System.out.println("SRC: " + srcPath.getCanonicalPath());
		System.out.println(" DEST: " + dstPath.getCanonicalPath());
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdirs();
			}
			String files[] = srcPath.list();
			for (int i = 0; i < files.length; i++) {
				System.out.println("src: " + srcPath);
				copyDirectory(new File(srcPath, files[i]), new File(dstPath, files[i]));
			}
		} else {
			if (!srcPath.exists()) {
				throw new Exception("El archivo o directorio Origen no existe");
			} else {
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
		System.out.println("Directorio copiado.");
	}

	/**
	 * @param horaCad
	 * @return
	 */
	public static int[] getHora(String horaCad) {
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

	public static boolean vacio(String s) {
		if (s == null || "".equals(s.trim()))
			return true;

		return false;
	}

	/**
	 * Valida que la fecha desde sea menor a la fecha hasta. True: fecha desde menor a fecha hasta. False cualquier otro caso
	 * 
	 * @throws ParseException
	 */
	public static Boolean valFechasPrecedencia(Date fechaDesde, Date horaDesde, Date fechaHasta, Date horaHasta)
			throws ParseException {
		Calendar fDesde = Calendar.getInstance();
		Calendar fHasta = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
		fDesde.setTime(sdf.parse(sdfFecha.format(fechaDesde) + " " + sdfHora.format(horaDesde)));
		fHasta.setTime(sdf.parse(sdfFecha.format(fechaHasta) + " " + sdfHora.format(horaHasta)));
		if (fDesde.getTime().getTime() >= fHasta.getTime().getTime()) {
			return false;
		}
		return true;
	}
}
