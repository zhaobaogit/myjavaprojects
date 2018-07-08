package txt2excel;

import java.io.File;
import java.util.ArrayList;

public class Importtodb {

	public static void importtxt() {
		String txtpath = UserInputUtil.input("请输入日志的txt文件路径");
		File txtf = new File(txtpath);
		while(!txtf.exists()) {
			txtpath = UserInputUtil.input("找不到文件"+txtpath+" 请输入日志的txt文件路径");
			txtf = new File(txtpath);
		}
		ArrayList<String> logrows = FileUtil.readLines(txtf);
		if(logrows.isEmpty()){
			System.out.println("文件中没有数据,请调整后再导入");
			return;
		}
		DbUtil.saveLogData(logrows);
	}

}
