package sendfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Appmain {

	public static String serverName = PropertiesUtil.getValue("serverName");
	public static int serverPort = Integer.parseInt(PropertiesUtil.getValue("serverPort"));
	private static ServerSocket serverSocket;
	
	public static void main(String[] args) throws Exception {
		FileUtil.readShow("firstWord.txt");
		serverSocket = new ServerSocket(serverPort);
        while (true) {
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = serverSocket.accept();
            /**
             * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
             * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
             * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
             */
            // 每接收到一个Socket就建立一个新的线程来处理它
            new Thread(new Task(socket)).start();
        }
	}
	
	 
    /**
     * 处理客户端传输过来的文件线程类
     */
    static class Task implements Runnable {
 
        private Socket socket;
 
        private DataInputStream dis;
        private DataOutputStream dos;
 
        private FileOutputStream fos;
 
        public Task(Socket socket) {
            this.socket = socket;
        }
 
        public void run() {
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                String type = dis.readUTF();
                if(type.equals("file")) {
                	accessFile();
                } else if (type.equals("folder")) {
                	accessFolder();
                } else if (type.equals("folderfile")) {
                	accessFolderfile();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fos != null)
                        fos.close();
                    if(dos != null)
                    	dos.close();
                    if(dis != null)
                        dis.close();
                    socket.close();
                } catch (Exception e) {}
            }
        }

        private void accessFolderfile() throws IOException {
			// 文件名和长度
			String fileName = dis.readUTF();
			File file = new File(fileName);
			fos = new FileOutputStream(file);
 
			// 开始接收文件
			byte[] bytes = new byte[1024];
			int length = 0;
			while((length = dis.read(bytes, 0, bytes.length)) != -1) {
			    fos.write(bytes, 0, length);
			    fos.flush();
			}
			System.out.println("======== 文件接收成功 [File Name：" + fileName + "] ========");
		}

		private void accessFolder() throws IOException, FileNotFoundException {
        	String tofile = "";
        	File directory = null;
        	tofile = dis.readUTF();
        	try {
        		directory = new File(tofile);
        		if (!directory.exists()) {
        			directory.mkdirs();
        		}
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        		dos.writeUTF("NO");
        		dos.flush();
        		throw new RuntimeException();
        	} 
        	dos.writeUTF("OK");
        	dos.flush();
        	// 文件名和长度
        	String fileName = dis.readUTF();
        	System.out.println(fileName);
        	
        	while(!fileName.equals("endsend")) {
        		if(fileName.equals("createDir")) {
        			fileName = dis.readUTF();
        			System.out.println(fileName);
        			File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
        			file.mkdirs();
        			fileName = dis.readUTF();
        			System.out.println(fileName);
        		}
//        		File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
//        		if(!file.getParentFile().exists()) {
//        			file.getParentFile().mkdirs();
//        		}
//        		fos = new FileOutputStream(file);
//        		// 开始接收文件
//        		byte[] bytes = new byte[1024];
//        		int length = 0;
//        		while((length = dis.read(bytes, 0, bytes.length)) != -1) {
//        			fos.write(bytes, 0, length);
//        			fos.flush();
//        		}
//        		fileName = dis.readUTF();
//        		System.out.println(fileName);
        	}
        	dos.writeUTF("endaccess");
        	dos.flush();
        	System.out.println("======== 文件夹创建成功 [File Name：" + tofile + "] ========");
        }
		private void accessFile() throws IOException, FileNotFoundException {
			String tofile = "";
			File directory = null;
			tofile = dis.readUTF();
			try {
				directory = new File(tofile).getParentFile();
				if (!directory.exists()) {
					directory.mkdirs();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				dos.writeUTF("NO");
				dos.flush();
				throw new RuntimeException();
			} 
			dos.writeUTF("OK");
			dos.flush();
			// 文件名和长度
			String fileName = dis.readUTF();
			File file = new File(tofile);
			fos = new FileOutputStream(file);
 
			// 开始接收文件
			byte[] bytes = new byte[1024];
			int length = 0;
			while((length = dis.read(bytes, 0, bytes.length)) != -1) {
			    fos.write(bytes, 0, length);
			    fos.flush();
			}
			System.out.println("======== 文件接收成功 [File Name：" + tofile + "] ========");
		}
    }
 
}
