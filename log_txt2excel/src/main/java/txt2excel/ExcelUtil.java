package txt2excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {

	public static void exportdata(ArrayList<ArrayList<String>> datas) throws IOException {
		String excelPath = UserInputUtil.input("请输入excel的路径");
		File excelfile = new File(excelPath);  
		File fileParent = excelfile.getParentFile();  
		if(!fileParent.exists()){  
		    fileParent.mkdirs();  
		}  
		excelfile.createNewFile();
		
		HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        //创建内容
        for (int i = 0; i < datas.size(); i++) {
        	HSSFRow row = sheet.createRow(i);
        	ArrayList<String> list = datas.get(i);
			for (int k = 0; k < list.size()-1; k++) {
				String val = list.get(k+1);
				HSSFCell cell = row.createCell(k);
				cell.setCellValue(val);
			}
		}
        
        OutputStream stream = new FileOutputStream(excelfile);
		wb.write(stream);
	}

}
