package sendfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendUtil {

	public static String serverName = PropertiesUtil.getValue("serverName");
	public static int serverPort = Integer.parseInt(PropertiesUtil.getValue("serverPort"));
	private static Socket client;
    private static FileInputStream fis;
 
    private static DataOutputStream dos;
    private static DataInputStream dis;
    
	public static void sendFile() {
		linkServer();
		
		try {
			String filepath = UserInputUtil.input("请输入要上传的文件名");
			File file = new File(filepath);
			while(!file.exists()) {
				filepath = UserInputUtil.input("未找到文件,请输入要上传的文件名");
				file = new File(filepath);
			}
			//服务端的输入输出流
			dos = new DataOutputStream(client.getOutputStream());
			dis = new DataInputStream(client.getInputStream());
			dos.writeUTF("file");
			dos.flush();
			String serverfilepath = UserInputUtil.input("请输入要上传到的目标路径含文件名");
			dos.writeUTF(serverfilepath);
			dos.flush();
			if(!dis.readUTF().equals("OK")) {
				System.out.println("目标路径有误");
				throw new RuntimeException();
			}
			if(file.isDirectory()) {
				throw new RuntimeException("请输入文件而不是文件夹");
			}
			if(file.exists()) {
				fis = new FileInputStream(file);
				
				// 文件名和长度
				dos.writeUTF(file.getName());
				dos.flush();
				
				// 开始传输文件
				System.out.println("======== 开始传输文件 ========");
				byte[] bytes = new byte[1024];
				int length = 0;
				long progress = 0;
				while((length = fis.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
					progress += length;
					System.out.print("| " + (100*progress/file.length()) + "% |");
				}
				System.out.println();
				System.out.println("======== 文件传输成功 ========");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeInOut();
		}
	}

	private static void closeInOut() {
		try {
			if(fis != null)
				fis.close();
		} catch (IOException e) {
			fis = null;
		}
		try {
			if(dos != null)
				dos.close();
		} catch (IOException e) {
			dos = null;
		}
		try {
			if(dis != null)
				dis.close();
		} catch (IOException e) {
			dis = null;
		}
		try {
			if(client != null)
				client.close();
		} catch (IOException e) {
			client = null;
		}
	}
	
	

	private static void linkServer() {
		try {
			client = new Socket(serverName, serverPort);
			System.out.println("连接成功");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] sendFolderCreateFolders() {
		linkServer();
		String filepath = "";
		String serverfilepath = "";
		try {
			filepath = UserInputUtil.input("请输入要上传的文件夹名");
			File file = new File(filepath);
			while(!file.exists()) {
				filepath = UserInputUtil.input("未找到文件,请输入要上传的文件夹名");
				file = new File(filepath);
			}
			//服务端的输入输出流
			dos = new DataOutputStream(client.getOutputStream());
			dis = new DataInputStream(client.getInputStream());
			dos.writeUTF("folder");
			dos.flush();
			serverfilepath = UserInputUtil.input("请输入要上传到的目标路径含文件夹名");
			dos.writeUTF(serverfilepath);
			dos.flush();
			if(!dis.readUTF().equals("OK")) {
				System.out.println("目标路径有误");
				throw new RuntimeException();
			}
			if(file.isFile()) {
				throw new RuntimeException("请输入文件夹而不是文件");
			}
			if(file.exists()) {
				
				// 开始传输文件
				System.out.println("======== 开始传输文件夹 ========");
				sendSubFolderCreateFolders(file,"");
				dos.writeUTF("endsend");
				System.out.println("endsend");
	        	dos.flush();
				System.out.println("======== 文件夹创建成功 ========");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeInOut();
		}
		return new String[]{filepath, serverfilepath};
	}

	private static void sendSubFolderCreateFolders(File file, String rootpath) throws FileNotFoundException, IOException {
		File[] files = file.listFiles();
		for (File file2 : files) {
			if(file2.isDirectory()){
				dos.writeUTF("createDir");
				System.out.println("createDir");
	        	dos.flush();
	        	dos.writeUTF(rootpath+file2.getName());
	        	System.out.println(rootpath+file2.getName());
	        	dos.flush();
	        	sendSubFolderCreateFolders(file2, rootpath+file2.getName()+File.separatorChar);
			}
			/*else{
				dos.writeUTF(rootpath+file2.getName());
				System.out.println(rootpath+file2.getName());
	        	dos.flush();
				fis = new FileInputStream(file2);
				byte[] bytes = new byte[1024];
				int length = 0;
				long progress = 0;
				while((length = fis.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
					progress += length;
					System.out.print("| " + (100*progress/file.length()) + "% |");
				}
				System.out.println();
			}*/
		}
	}

	public static void sendFolder() {
		String[] results = sendFolderCreateFolders();
		String filepath = results[0];
		String serverfilepath = results[1];
		File file = new File(filepath);
		sendFolderFiles(file, serverfilepath);
	}

	private static void sendFolderFiles(File file, String serverfilepath) {
		File[] files = file.listFiles();
		for (File file2 : files) {
			if(file2.isDirectory()){
				sendFolderFiles(file2, serverfilepath+File.separatorChar+file2.getName());
			}else{
				sendFolderFile(file2, serverfilepath+File.separatorChar+file2.getName());
			}
		}
	
	
	}

	private static void sendFolderFile(File file, String filename) {
		linkServer();
		try{
			//服务端的输入输出流
			dos = new DataOutputStream(client.getOutputStream());
			dis = new DataInputStream(client.getInputStream());
			dos.writeUTF("folderfile");
			dos.flush();
			if(file.exists()) {
				fis = new FileInputStream(file);
				
				// 文件名和长度
				dos.writeUTF(filename);
				dos.flush();
				
				// 开始传输文件
				System.out.println("======== 开始传输文件 ========");
				byte[] bytes = new byte[1024];
				int length = 0;
				long progress = 0;
				while((length = fis.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
					progress += length;
					System.out.print("| " + (100*progress/file.length()) + "% |");
				}
				System.out.println();
				System.out.println("======== 文件传输成功 ========");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeInOut();
		}
	}
}
