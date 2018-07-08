package txt2excel;

import java.io.IOException;

public class AppMain {

	public static void main(String[] args) throws Exception {
		FileUtil.readShow("firstWord.txt");
		inputCommand();
	}

	private static void inputCommand() throws IOException {
		FileUtil.readShow("menu.txt");
		String command = UserInputUtil.input();
		while(!UserInputUtil.validateinput(command,"1,2,3,4","输入错误请重输")){
			command = UserInputUtil.input();
		}
		if(command.equals("1")){//导入
			importtodb();
			inputCommand();
		}else if(command.equals("2")){//导出
			exporttoxls();
			inputCommand();
		}else if(command.equals("3")){//删除
			deldata();
			inputCommand();
		}else if(command.equals("4")){//退出
			System.exit(0);
		}
	}

	private static void deldata() {
		DbUtil.deldata();
	}

	private static void exporttoxls() throws IOException {
		Exporttoxls.exportxls();
	}

	private static void importtodb() {
		Importtodb.importtxt();
	}

}
