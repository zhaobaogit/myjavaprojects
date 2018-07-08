package txt2excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FileUtil {

	public static void readShow(String fileName) {
		ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL url = classLoader.getResource(fileName);
        File file = new File(url.getFile());
        FileReader reader = null;
        BufferedReader br = null;
        try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			String tempstr = "";
			tempstr = br.readLine();
			while(tempstr!=null) {
				System.out.println(tempstr);
				tempstr = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					reader=null;
				}
			}
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					br=null;
				}
			}
		}
	}

	public static ArrayList<String> readLines(File txtf) {
		ArrayList<String> resutl = new ArrayList<String>();
		FileReader reader = null;
        BufferedReader br = null;
        try {
			reader = new FileReader(txtf);
			br = new BufferedReader(reader);
			String tempstr = "";
			tempstr = br.readLine();
			while(tempstr!=null) {
				resutl.add(tempstr);
				tempstr = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					reader=null;
				}
			}
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					br=null;
				}
			}
		}
		return resutl;
	}

}
