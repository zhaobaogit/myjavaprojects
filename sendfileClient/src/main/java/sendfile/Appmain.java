package sendfile;

import java.io.IOException;

public class Appmain {
	
	public static void main(String[] args) throws Exception {
		FileUtil.readShow("firstWord.txt");
		inputCommand();
	}


	private static void inputCommand() throws IOException {
		FileUtil.readShow("menu.txt");
		String command = UserInputUtil.input();
		while(!UserInputUtil.validateinput(command,"1,2,3","输入错误请重输")){
			command = UserInputUtil.input();
		}
		if(command.equals("1")){//传文件
			sendFile();
			inputCommand();
		}else if(command.equals("2")){//传文件夹
			sendFolder();
			inputCommand();
		}else if(command.equals("3")){//退出
			System.exit(0);
		}
	}

	private static void sendFolder() {
		// 传文件夹
		SendUtil.sendFolder();
	}

	private static void sendFile() {
		// 传文件
		SendUtil.sendFile();
	}

}
