import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.util.Scanner;

public class p2pChat{

    //IP Address mapping(hash)
    public static String getRoomAddress(String chatRoomName){
        String ipAddress = null;
        try {
            //Using the hash of the input chat room name "SHA-256"
            //Convert to Multicast address 225.x.y.z -> Obtain (x, y, z) value using three digits behind the hashish

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");//Create a digest object for the corresponding message ("SHA-256)
            messageDigest.update(chatRoomName.getBytes()); //Update digest with specified byte data.

            byte byteMessage[] = messageDigest.digest();//returns hash as a byte array.

            StringBuffer stringBuffer =  new StringBuffer();//Create a data-type object that you use to add/delete strings

            for(int i = 0; i < byteMessage.length; i++){
                //Characters to add (convert byte to HexString))
                String string = Integer.toString((byteMessage[i] & 0xff) + 0x100, 16).substring(1);
                //0xff: ff(16) = 1111 1111(2) = 255(10)
                //byteMessage[i] = byteMessage[i] & 0xff to output existing values
                //1. Forced conversion from 8-bit byte type to 32-bit int type:byteMessage[i] & 0xff
                //2. Force conversion to 3-digit String: +0x100
                //3. Remove the first 1 unnecessarily attached:.substring(1)

                //Add String
                stringBuffer.append(string);
            }
            ipAddress = stringBuffer.toString();

            //Change to byte and pick the last 3 digits
            byte hashAddress[] = ipAddress.getBytes();
            //64 (0-63), so take the 61, 62, and 63 and put them in x, y, and z, respectively
		byte x = hashAddress[61];
            byte y = hashAddress[62];
            byte z = hashAddress[63];
            ipAddress = "225." + x + "." + y + "." + z;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    //peer performs both server and client roles -> Thread at the same time.


    public static void main(String[] args) throws InterruptedException {
        int portNo = 0;
        //Example: 'java p2pChat portNo' where port number means port number of the house.
        try{
            String portN = args[0]; //multicast port number
            portNo = Integer.parseInt(portN);
        } catch (NumberFormatException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        while (true){
            System.out.println("P2P Chatting room start!");
            System.out.println("What do you wnat? : ");

            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine(); //ex. #JOIN cNet yumin 또는 #EXIT
            String[] commandSplit = command.split("\\s");//commandSplit={#JOIN, cNet, yumin}

            //#JOIN
            //Open chat room if there is no name, and join if there is already one
            if(commandSplit[0].equals("#JOIN")){
                String chatRoomName = commandSplit[1];
                String userName = commandSplit[2];
                String roomIP = getRoomAddress(chatRoomName);

                Write write = new Write(portNo, roomIP, userName);
                Thread writer = new Thread(write);
                Read read = new Read(portNo, roomIP, userName);
                Thread reader = new Thread(read);

                writer.start();
                reader.start();
                writer.join();
                reader.join();
            }
            else{
                System.out.println("Wrong Command");
                System.out.println("ReWrite Command! ");
            }
        }
    }
}