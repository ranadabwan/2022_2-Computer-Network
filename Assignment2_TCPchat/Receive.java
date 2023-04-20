import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Receive implements Runnable {

    Socket socket;

    String chroomName;
    String userName;

    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    PrintWriter printWriter;

    HashMap<String, HashMap<String, PrintWriter>> chroomTable;

    public Receive(Socket socket, String roomName, String userName, HashMap<String, HashMap<String, PrintWriter>> hashMap, BufferedReader br) {
        this.socket = socket;
        this.chroomName = roomName;
        this.userName = userName;
        this.chroomTable = hashMap;
        this.bufferedReader = br;
    }

    @Override
    public void run() {

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            printWriter = new PrintWriter(bufferedWriter, true); //Get output stream.

            for(;;){
                String clientMsg = bufferedReader.readLine(); //msg read

                if (!clientMsg.startsWith("#")){
                    String msgForm = userName + " : " + clientMsg;
                    //Send except sender
                    for(String user : chroomTable.get(chroomName).keySet()){ //Spinning the table
                        if(!user.equals(userName)) { If it's a name, it's a sender, so send it without him.
                            printWriter = new PrintWriter(chroomTable.get(chroomName).get(user), true);
                            printWriter.println(msgForm);
                        }
                    }
                }
                If it starts with //#, it is either a different command or an unsupported message.
                if(clientMsg.equals("#STATUS")){
                    //Current status (Current chat room name, member name output -> From client screen
                    List<String> member = new ArrayList<>();
                    for(String user : chroomTable.get(chroomName).keySet()){
                        member.add(user);
                    }
                    String msgForm = "";
                    for(int i = 0; i < member.size(); i++){
                        msgForm = msgForm + " " + member.get(i);
                    }
                    String msgFormPlus ="[" + msgForm + " ] in chatting room [ " + chroomName + " ].";
                    printWriter = new PrintWriter(bufferedWriter, true);
                    printWriter.println(msgFormPlus);
                }
                else if(clientMsg.equals("#EXIT")){
                    //Remove this member from the current chat room
                    printWriter.println(clientMsg);

                    synchronized (chroomTable){
                        HashMap<String, PrintWriter> Room = chroomTable.get(chroomName);
                        Room.remove(userName); // Delete a member from a member!
                        System.out.println(userName + " is OUT this chatting room [ " + chroomName + " ].");
                    }
                    bufferedReader.close();
                    bufferedWriter.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}