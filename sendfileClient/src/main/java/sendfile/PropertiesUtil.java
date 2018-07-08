package sendfile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertiesUtil {

	public static String getValue(String key) {
		ResourceBundle resource = null;
		try {
			InputStream inStream = new FileInputStream("conf.properties");
			resource = new PropertyResourceBundle(inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resource.getString(key);
	}

}
