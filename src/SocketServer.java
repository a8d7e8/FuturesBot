import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

import messenger.facebook;
import messenger.gtalk;
 
public class SocketServer extends java.lang.Thread {
 
    private boolean OutServer = false;
    private ServerSocket server;
    private final int ServerPort = 8888;// 要監控的port
    static gtalk g = gtalk.getInstance();
	static facebook f = facebook.getInstance();
	//static final String Email;
	//static final String fb;
	static final String botname = "bot";
 
    public SocketServer() {
        try {
            server = new ServerSocket(ServerPort);
 
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
 
    public void run() {
        Socket socket;
        java.io.BufferedInputStream in;
        NewDdeClient client = new NewDdeClient();
 
        System.out.println("伺服器已啟動 !");
        while (!OutServer) {
            socket = null;
            try {
                synchronized (server) {
                    socket = server.accept();
                }
                System.out.println("取得連線 : InetAddress = "
                        + socket.getInetAddress() + " FlagStatus:" + client.runflag);
                // TimeOut時間
                socket.setSoTimeout(120000);
 
                in = new java.io.BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[20480];
                String data = "";
                //int length;
                int length;
                while ((length = in.read(b)) > 0)// <=0的話就是結束了
                {	
                    data = new String(b, 0, length);
                    String[] temp = data.split(";");
                    for (String input:temp){
                    	if (input.length() > 0) {
                    		client.doit(input);
                    		//System.out.println(input);
                    	}
                    }
                }
                System.out.println("Client Say Goodbye!!");
                in.close();
                in = null;
                socket.close();
                check_runtime();
                if (OutServer) {
                	client.close();
                } else if (client.runflag == false) {
                    client.runflag = true;
                }
            } catch (java.io.IOException e) {
                System.out.println("Socket連線有問題 !");
                System.out.println("IOException :" + e.toString());
                /*g.alert(botname, Email, " futuresbot " + e.toString());
				f.alert(botname, fb, " futuresbot " + e.toString());*/
            }
        }
    }
    
    void check_runtime() {
		java.util.Date now = new java.util.Date(); // 取得現在時間
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss E",
				java.util.Locale.TAIWAN);
		String sGMT = sf.format(now);
		int hour = Integer.valueOf(sGMT.substring(0, 2)).intValue();
		int min = Integer.valueOf(sGMT.substring(3, 5)).intValue();
		int sec = Integer.valueOf(sGMT.substring(6, 8)).intValue();
		if (hour > 12 && min > 49 && sec > 0) {
			OutServer = true;
		}
    }
 
    public static void main(String args[]) {
        new SocketServer().start();
    }
 
}
