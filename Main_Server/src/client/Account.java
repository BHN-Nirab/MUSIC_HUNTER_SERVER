package client;

import playlistcontroller.Music;
import security.SHA_1;
import user.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Account{

    public User user;

    public boolean createAccount(User user) throws IOException {

        if (user.firstName.isEmpty() || user.lastName.isEmpty() || user.password.isEmpty() || user.userID.isEmpty()) {
            return false;
        }


        try {
            File inputFile = new File("src/resource/login/loginData.txt");
            Scanner input = new Scanner(inputFile);
            String inputuserID = "";
            String inputpassword = "";

            while (input.hasNextLine()) {
                inputuserID = input.next();
                inputpassword = input.next();

                if (inputuserID.equals(user.userID)) return false;
            }


        } catch (Exception e) {
        }

        this.user = user;
        this.user.password = SHA_1.encodeHex(user.password);

        FileOutputStream fout = new FileOutputStream("src/resource/img/userdp/" + user.userID + ".jpg");
        fout.write(user.DP);
        fout.close();

        ObjectOutputStream object = null;
        try {
            object = new ObjectOutputStream(new FileOutputStream(new File("src/resource/userdata/" + user.userID + ".txt")));
            object.writeObject(user);

            object.close();
        } catch (Exception e) {
        }

        try {
            String loginData = user.userID + " " + user.password + "\n";

            OutputStream out = new FileOutputStream("src/resource/login/loginData.txt", true);
            byte[] ara = loginData.getBytes();

            for (int i = 0; i < ara.length; i++) out.write(ara[i]);

            out.close();
        } catch (Exception e) {
        }

        return true;
    }


    public boolean login(String userID, String password) {

        try {
            File inputFile = new File("src/resource/login/loginData.txt");
            Scanner input = new Scanner(inputFile);
            String inputuserID = "";
            String inputpassword = "";

            while (input.hasNextLine()) {
                inputuserID = input.next();
                inputpassword = input.next();

                if (inputuserID.equals(userID) && inputpassword.equals(password)) return true;
            }


        } catch (Exception e) {
        }

        return false;

    }

    public void addFriend(String loginID, String friendID) {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + loginID + ".txt")));
            User tmp = (User) input.readObject();
            tmp.friendList.add(friendID);

            for (int i = 0; i < tmp.friendReq.size(); i++) {
                String s = tmp.friendReq.get(i);
                if (s.equals(friendID)) tmp.friendReq.remove(s);
            }

            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File("src/resource/userdata/" + loginID + ".txt")));
            output.writeObject(tmp);

            input.close();
            output.close();
        } catch (Exception e) {
        }

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + friendID + ".txt")));
            User tmp1 = (User) input.readObject();
            tmp1.friendList.add(loginID);


            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File("src/resource/userdata/" + friendID + ".txt")));
            output.writeObject(tmp1);

            input.close();
            output.close();
        } catch (Exception e) {
        }


    }


    public void friendReqList(String loginID, String friendID) {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + friendID + ".txt")));
            User tmp = (User) input.readObject();
            tmp.friendReq.add(loginID);
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File("src/resource/userdata/" + friendID + ".txt")));
            output.writeObject(tmp);

            input.close();
            output.close();
        } catch (Exception e) {
        }
    }

    public ArrayList<String> suggestion(String user) {
        ArrayList<String> suggestedList = new ArrayList<String>();
        User tmp = null;
        try {
            ObjectInputStream userInput = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + user + ".txt")));
            tmp = (User) userInput.readObject();
            userInput.close();
        } catch (Exception e) {
        }


        try {
            File inputFile = new File("src/resource/login/loginData.txt");
            Scanner input = new Scanner(inputFile);
            String inputuserID = "";
            String inputpassword = "";

            while (input.hasNextLine()) {
                inputuserID = input.next();
                inputpassword = input.next();

                boolean found = false;

                for (int i = 0; i < tmp.friendList.size(); i++) {
                    String frnd = tmp.friendList.get(i);
                    if (frnd.equals(inputuserID)) found = true;
                }
                for (int i = 0; i < tmp.friendReq.size(); i++) {
                    String frnd = tmp.friendReq.get(i);
                    if (frnd.equals(inputuserID)) found = true;
                }


                if (!found && !inputuserID.equals(user)) suggestedList.add(inputuserID);

            }


        } catch (Exception e) {
        }

        return suggestedList;
    }
}
