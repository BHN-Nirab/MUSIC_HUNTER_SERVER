package Server;

import client.Account;
import objectPacking.ObjectPacking;
import security.SHA_1;
import user.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Controller extends Thread {

    Socket client = null;

    private Controller()
    {

    }

    public Controller(Socket client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        try{

            InputStream input = client.getInputStream();
            OutputStream output = client.getOutputStream();

            ObjectInputStream objectInput = new ObjectInputStream(input);
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);

            ObjectPacking response = new ObjectPacking();

            ObjectPacking object = (ObjectPacking)objectInput.readObject();

            if(object.command.equals("createAccount"))
            {
                User user = (User)object.data;
                Account acc = new Account();
                Boolean feedback = acc.createAccount(user);

                response.command = "createAccount";
                response.from = "server";
                response.to = user.userID;

                if(feedback) response.comment = "true";
                else response.comment = "false";

            }

            else if(object.command.equals("login"))
            {
                String userName = "";
                String password = "";

                String data[] = object.comment.split(" ");

                Boolean feedback = false;

                if(data.length!=2)
                {
                    feedback = false;
                }

                else {
                    userName = data[0];
                    password = SHA_1.encodeHex(data[1]);

                    Account acc = new Account();
                    feedback = acc.login(userName, password);
                }

                response.command = "login";
                response.from = "server";
                response.to = userName;

                if(feedback) response.comment = "true";
                else response.comment = "false";
            }

            else if(object.command.equals("initializeUserInfo"))
            {
                ObjectInputStream userObject = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + object.comment + ".txt")));

                User user = (User)userObject.readObject();

                response.data = user;
                response.from = "server";
                response.to = user.userID;

            }

            else if(object.command.equals("addMusictoPlaylist"))
            {
                FileOutputStream out = new FileOutputStream(new File("src/resource/playlist/" + object.from + ".txt"), true);
                out.write(object.comment.getBytes());
                out.close();

                response.from = "server";
                response.comment = "done";
            }

            else if(object.command.equals("initializeUserPlaylist"))
            {
                User data = new User();
                File file = new File("src/resource/playlist/" + object.comment + ".txt");
                if(file.exists())
                {

                    data.DP = Files.readAllBytes(file.toPath());

                    response.comment = object.comment;
                    response.data = data;
                }
                else
                    data.DP = new byte[1];


            }

            else if(object.command.equals("backup"))
            {
                User data = new User();
                data = (User)object.data;
                File file = new File("src/resource/playlist/" + object.comment + ".txt");
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(data.DP);

                response.comment = "done";


            }

            objectOutput.writeObject(response);

            client.close();

        }catch(Exception e)
        {
            System.out.println("Error 1: " + e);
        }
    }

}
