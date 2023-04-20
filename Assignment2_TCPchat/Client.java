import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Client {

    private static String chroomName = null;
    private static String userName = null;
    private static Socket socket1;
    //Socket socket1;

    public static void main(String[] args) throws IOException {
        String ipAddress = args[0];
        int portNumber1 = Integer.parseInt(args[1]); // send and receive chat messages + command transfer purpose
        int portNumber2 = Integer.parseInt(args[2]); //#PUT, #GET Operation Purpose

        System.out.println(" -------------------TCP chatting room start!------------------- ");
        System.out.println(" ----------------You can run the command below.---------------- ");
        System.out.println(" #CREATE (chatroom name) (username) : create new chatting room. ");
        System.out.println(" #JOIN (chatroom name) (username) : join existing chatting room.");
        System.out.println(" #EXIT : exit this chatting room.");
        System.out.println(" #STATUS : check member of this chatting room.");
        System.out.println(" ---------------or You can send and receive FILE.-------------- ");
        System.out.println(" #PUT (Filename) : upload FIlE on server.");
        System.out.println(" #GET (Filename) : download FILE from server.");
        System.out.print("==>> ");

        Scanner scanner = new Scanner(System.in);

        String command = scanner.nextLine();
        String[] commandSplit = command.split(" ");

            switch (commandSplit[0]) {
                case "#CREATE": case "#JOIN":
                    //#CREATE/JOIN (Name of chat room to create) (User name)
                    chroomName = commandSplit[1];
                    userName = commandSplit[2];

                    socket1 = new Socket(ipAddress, portNumber1); //Sockets to connect to the server

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
                    PrintWriter printWriter = new PrintWriter(bufferedWriter, true);

                    printWriter.println(command);

                    Read read = new Read(socket1, userName, chroomName);
                    Thread reader = new Thread(read); //Read from server
                    reader.start();

                    while (true){
                        command = scanner.nextLine();
                        if(command.equals("#STATUS")){
                            printWriter.println(command);
                        }
                        else if(command.equals("#EXIT")){
                            printWriter.println(command);
                            break;
                        }
                        else if(command.startsWith("#")){
                            System.out.println("ERROR: Message starts with # is only Command.");
                        }
                        else{
                            printWriter.println(command);
                        }
                    }

                case "#PUT":
                    //#PUT (FileName)

                case "#GET":
                    //#GET (FileName)
            }
        //}
    }
}