import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Centralized_Server {

        public static void main(String[] args) throws IOException {
	}
}

class ClientHandler implements Runnable {

	public ClientHandler(Socket connectionSocket, BufferedReader dis, DataOutputStream dos) {
    }
    
	public void run() {
    }
}

class ClientData {

	public String hostName;
	public String hostUserName;
	public String fileName;
	public String fileDescription;
	public String speed;
	public int port;

	public ClientData(String hostUserName, String hn, int port, String fn, String fd, String sp) {
		this.hostUserName = hostUserName;
		this.hostName = hn;
		this.port = port;
		this.fileName = fn;
		this.fileDescription = fd;
		this.speed = sp;
	}
}
