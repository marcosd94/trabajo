package py.com.excelsis.sicca.util;


public class PasswordGenerator {

	public static String NUMEROS = "123456789";
	public static String MAYUSCULAS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
	public static String MINUSCULAS = "abcdefghijkmnpqrstuvwxyz";
	public static String ESPECIALES = "Ò—";
	
	
	public static String getPassword() {
		return getPassword(6);
	}

	public static String getPassword(int length) {
		return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
	}

	@SuppressWarnings("unused")
	public static String getPassword(String key, int length) {
		String pswd = "";
		for (int i = 0; i < length; i++) {
			pswd += (key.charAt((int) (Math.random() * key.length())));
		}
	
		Sha1 sha = new Sha1();
		return pswd;
	}
}
