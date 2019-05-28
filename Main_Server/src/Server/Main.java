package Server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String config = "";
        String value = "";

        try {
            File inputFile = new File("src/resource/server.config");
            Scanner input = new Scanner(inputFile);

            while (input.hasNextLine()) {
                config = input.next();
                value = input.next();

                if (config.equals("port")) break;
            }


        } catch (Exception e) {
            System.out.println("Error:Config file not found!");
        }


        if(config.equals("port"))
        {
            try{
                ServerSocket server = new ServerSocket(Integer.valueOf(value));


                while(true)
                {
                    System.out.println("Waiting for connection...");
                    Socket client = server.accept();
                    System.out.println("Connected with: " + client.toString() );
                    Thread eachClient = new Controller(client);
                    eachClient.start();

                }

            }catch(Exception e)
            {
                System.out.println("Error: 0" + e);
            }
        }
        else
        {
            System.out.println("Error:Invalid port!");
        }

    }

}
