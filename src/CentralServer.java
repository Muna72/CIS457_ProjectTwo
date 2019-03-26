import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class CentralServer {
    //This socket waits for client connections.
    private static ServerSocket welcomeSocket;

    //users is an ArrayList of all clients connected to the CentralServer.
    public static ArrayList<ClientHandler> users = new ArrayList<ClientHandler>();
    //clientData holds a list of ClientData objects to create a list of all available files across clients.
    public static ArrayList<ClientData> clientData = new ArrayList<ClientData>();

    public static void main(String[] args) throws IOException {

        try {
            welcomeSocket = new ServerScoket(3158);
            System.out.println("Server is Up.");
        } catch (Exception e) {
            System.err.println("Error: Server was not started.");
        }
        try {
            while(true) {
                //connectionSocket waits for a client to connect.
                Socket connectionSocket = welcomeSocket.accept();

                //Input and output stream to communication with client.
                BufferedReader bufRead = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())):
                DataOutputStream dos = new DataOutputStream(connectionSocket.getOutputStream());

                //instantiates a new clienthandler object with client socket and streams.
                ClientHandler client = new ClientHandler(connectionSocket, bufRead, dos);

                //Client is added to the ArrayList of users.
                users.add(client);

                //New thread for client and clientHandler to work together.
                //Utilizes overridden run method.
                Thread t = new Thread(client);
                t.start();
            }

        } catch (Exception e) {
            System.err.println("Error: Client could not be connected.");
            e.printStackTrace();
        } finally {
            try {
                //In the event of an error close the welcomeSocket.
                welcomeSocket.close();
                System.out.println("Server socket closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
//ClientHandler manages information and stream from each Client.
//ClientData holds individual data for files available for download.
//Implements Runnable to be allowed to use thread created in main.
class ClientHandler implements Runnable {
    Socket connectionSocket;
    String fromClient;
    public String clientName;
    public String hostName;
    public int port;
    public String speed;
    BufferedReader dis;
    DataInputStream in;
    DataOutputStream out;
    boolean loggedIn;

    //Constructor for ClientHandler
    public ClientHandler(Socket connectionSocket, BufferedReader dis, DataOutputStream out) {
        this.connectionSocket = connectionSocket;
        this.dis = dis;
        this.out = out;
        this.loggedIn = true;
    }

    //Run method is overridden to allow multiple clients to use the server.
    @Overrride
    public void run() {
        //connectionString coming from localFtpClient.
        String connectionString;
        String fileList;

        int listSize;

        try {
            //First string received contains the username, hostname, and speed for that specific client.
            in = new DataInputStream(connectionSocket.getInputStream());
            connectionString = is.readUTF();

            //New StringTokenizer containing connectionString.
            String tokens = new StringTokenizer(connectionString);
            this.clientName = tokens.nextToken();
            this.hostName = tokens.nextToken();
            this.speed = tokens.nextToken();
            this.port = Integer.parseInt(tokens.nextToken());

            System.out.println(clientName + " has connection!");

            //reads in if the file has any files available for download.
            fileList = in.readUTF();

            //If no files are available fileList will equal only "404";
            //Might switch all "404" responses to a different number for clarity. Not technically a 404.
            if(!fileList.equals("404")) {
                tokens = new StringTokenizer(fileList);
                //read in next token containing status code.
                String data = tokens.nextToken();

                if(data.startsWith("200")) {
                    //Reads in the number of files available for download.
                    data = tokens.nextToken();
                    listSize = Integer.parseInt(data);

                    for(int i = 0; i < listSize; i++) {
                        //First string of file information.
                        String fileInfo = in.readUTF();
                        tokens = new StringTokenizer(fileInfo);
                        //$ is used to parse here because fileDescription contains spaces.
                        String fileName = tokens.nextToken($);
                        String fileDescription = tokens.nextToken();

                        //new clientdata object with necessary information for the file.
                        ClientData cd = new ClientData(this.clientName, this.hostName, this.port, fileName, fileDescription, this.speed);
                        //add file data to the ArrayList of all available files.
                        CentralServer.clientData.add(cd);
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            boolean hasNotQuit = true;

            //Parses messages received by the client into a command.
            do {
                //data fromClient
                fromClient = in.readUTF();
                //if output from client equals -1 quit server.
                if(fromClient.equal("-1")) {
                hasNotQuit = false;
                } else {
                    //Central server file description search.
                    for (int i = 0; i < Centralized_Server.clientData.size(); i++) {
                        if (Centralized_Server.clientData.get(i).fileDescription.contains(fromClient)) {
                            ClientData cd = Centralized_Server.clientData.get(i);
                            String str = cd.speed + " " + cd.hostName + " " + cd.port + " " + cd.fileName + " "
                                    + cd.hostUserName;
                            //sends string containing file information to be downloaded using retrieve.
                            dos.writeUTF(str);
                            System.out.println(cd.fileName);
                        }
                    }
                }
                dos.writeUTF("EOF");
            } while (hasNotQuit);
            //Online status set to offline.
            this.loggedIn = false;
            //remove file from clientData ArrayList.
            for (int i = 0; i < Centralized_Server.clientData.size(); i++) {
                if (Centralized_Server.clientData.get(i).hostName == this.hostName) {
                    Centralized_Server.clientData.remove(i);
                }
            }
            //close connectionSocket
            this.connectionSocket.close();
            System.out.println(clientName + "has disconnected!");
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
class ClientData {
    public String hostName;
    public String hostUserName;
    public String fileName;
    public String fileDescription;
    public String speed;
    public int port;

    //constructor which creates the actual ClientData object. holds info for file.
    public ClientData(String hostUserName, String hostName, int port, String fileName, String fileDescription, String speed) {
        this.hostUserName = hostUserName;
        this.hostName = hostName;
        this.port = port;
        this.fileName = fileName;
        this.fileDescription = fileDescription;
        this.speed = speed;
    }
}