import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class Read implements Runnable{
    int portNum;
    String IpAddress;
    String UserName;

    Read(int portNumber, String ipAddress, String userName){
        portNum = portNumber;
        IpAddress = ipAddress;
        UserName = userName;
    }

    @Override
    public void run() {
        try {

            //Create socket to receive
            MulticastSocket multicastSocket = new MulticastSocket(portNum);

            //joining chatgroup
            multicastSocket.joinGroup(InetAddress.getByName(IpAddress));

            //massage Continue to receive
            while(true){
                //512 chunk
                byte readcontent[] = new byte[512];

                DatagramPacket datagramPacket = new DatagramPacket(readcontent, 512);

                multicastSocket.receive(datagramPacket);

                String stringMsg = new String(readcontent);

                System.out.println(stringMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}