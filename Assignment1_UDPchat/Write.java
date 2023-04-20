import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

//Enter Chat
public class Write implements Runnable{
    int portNum;
    String IpAddress;
    String PeerName;

    Write(int portNumber, String ipAddress, String userName){
        portNum = portNumber;
        IpAddress = ipAddress;
        PeerName = userName;
    }

    @Override
    public void run() {
        //Continue to write messages
        while(true){
            Scanner scanner = new Scanner(System.in);
            String writeMsg = scanner.nextLine();
            boolean containShap = writeMsg.contains("#");

            if(writeMsg.equals("#EXIT")){
                writeMsg = PeerName + " is out.";

                try {
                    //Make a packet to send
                    DatagramPacket datagramPacket = new DatagramPacket(writeMsg.getBytes(), writeMsg.getBytes().length, InetAddress.getByName(IpAddress), portNum);
                    //Use the socket to send
                    MulticastSocket multicastSocket = new MulticastSocket();
                    multicastSocket.joinGroup(InetAddress.getByName(IpAddress));
                    multicastSocket.send(datagramPacket);

                    multicastSocket.close();
                    System.exit(0);
                    scanner.close();
                    break;

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //#If It's not EXIT, # not allowed
            else if(containShap){
                System.out.println("# is only command");
            }
            //If it is not EXIT then it's a message, so make it and deliver it
            else{
                writeMsg = PeerName + ": " + writeMsg;

                try {
                    DatagramPacket datagramPacket = new DatagramPacket(writeMsg.getBytes(), writeMsg.getBytes().length, InetAddress.getByName(IpAddress), portNum);
                    MulticastSocket multicastSocket = new MulticastSocket();
                    multicastSocket.joinGroup(InetAddress.getByName(IpAddress));
                    multicastSocket.send(datagramPacket);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
