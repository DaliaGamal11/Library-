package ClientSide;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String args[]) {
        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            // create a new socket connected to the given address and port
            socket = new Socket("localhost", 5000);
            System.out.println("Connected");

            // create a new DataOutputStream to write data to the socket
            out = new DataOutputStream(socket.getOutputStream());

            // create a new BufferedReader to read data from the console
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // create a new DataInputStream to read data from the socket
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String output= in.readUTF();
            System.out.println(output);
            boolean loggedIn = false;
            // read The Login Menu from the server
            while (true) {
                String login_menu = in.readUTF();
                System.out.println(login_menu);
                // read the user's input for username and password or - to exit
                String choice = console.readLine();
                out.writeUTF(choice);
                // read the server's response
                output = in.readUTF();
                System.out.println(output);
                if (output.contains("Closing")) {
                    break;
                }
                else if(output.contains("Invalid choice")){
                    continue;
                }
                String userInfo = console.readLine();
                out.writeUTF(userInfo);
                output = in.readUTF();
                System.out.println(output);
                if(output.contains("logged in successfully") ){
                    loggedIn = true;
                    break;
                }
            }
            //menu for the user after logging in successfully
            if(loggedIn&& output.contains("as user")){
                while (true){
                    // read the menu from the server
                    String menu = in.readUTF();
                    System.out.println(menu);
                    // read the user's choice
                    String choice = console.readLine();
                    out.writeUTF(choice);
                    // read the server's response
                    output = in.readUTF();
                    System.out.println(output);
                    if(output.contains("Adding A Book")){
                        // read the book name AND Send it to the server
                        String bookinfo =console.readLine();
                        out.writeUTF(bookinfo);
                        String res=in.readUTF();
                        if (res.contains("Invalid input.")){
                            System.out.println(res);
                            continue;
                        }
                        System.out.println(res);
                    }
                    else if(output.contains(("Viewing My Books"))){
                        continue;

                    }
                    else if(output.contains("Deleting A Book")){
                        String any = console.readLine();
                        out.writeUTF(any);
                        String result = in.readUTF();
                        System.out.println(result);
                    }
                    else if(output.contains("Making A Book Available")){
                        String any = console.readLine();
                        out.writeUTF(any);
                        String result = in.readUTF();
                        System.out.println(result);

                    }
                    else if(output.contains("Browsing & Borrowing Books")){
                        // The Boolean firstTime is used to avoid printing the menu twice
                        boolean firstTime = true;
                        while (true) {
                            // it will skip the first time to avoid printing the menu twice
                            //To sync the menu with the server
                            if (firstTime) {
                                firstTime = false;
                            }
                            else{
                                // read the menu from the server
                                // to avoid the menu from being printed twice
                                String result = in.readUTF();
                                System.out.println(result);
                            }

                            String any = console.readLine();
                            out.writeUTF(any);
                            String result = in.readUTF();
                            System.out.println(result);
                            if (result.contains("Viewing All Available Books")) {
                                continue;
                            } else if (result.contains("Searching For A Book")) {
                                String choiceToSearch = console.readLine();
                                out.writeUTF(choiceToSearch);
                                String res = in.readUTF();
                                System.out.println(res);
                                String search = console.readLine();
                                out.writeUTF(search);
                                String books = in.readUTF();
                                System.out.println(books);
                            }
                            else if(result.contains("Borrowing")){
                                String choiceToBorrow = console.readLine();
                                out.writeUTF(choiceToBorrow);
                                String res = in.readUTF();
                                System.out.println(res);
                            }
                            else if(result.contains("Viewing Books Borrowed By Me")){
                                continue;
                            }
                            else if(result.contains("You chose to return to the main menu")){
                                break;
                            }
                        }

                    }
                    else if (output.contains("Requests")){
                        boolean firstTime = true;
                        while (true) {
                            // it will skip the first time to avoid printing the menu twice
                            //To sync the menu with the server
                            if (firstTime) {
                                firstTime = false;
                            }
                            else{
                                // read the menu from the server
                                // to avoid the menu from being printed twice
                                String result = in.readUTF();
                                System.out.println(result);
                            }
                            // read the user's choice
                            String any = console.readLine();
                            out.writeUTF(any);
                            String result = in.readUTF();
                            System.out.println(result);
                            if (result.contains("Incoming") || result.contains("Outgoing")){
                                continue;
                            }
                            else if(result.contains("Accepting")){
                                String choiceToAccept = console.readLine();
                                out.writeUTF(choiceToAccept);
                                String res = in.readUTF();
                                System.out.println(res);
                            }
                            else if(result.contains("Rejecting")){
                                String choiceToReject = console.readLine();
                                out.writeUTF(choiceToReject);
                                String res = in.readUTF();
                                System.out.println(res);
                            } else if (result.contains("To open a chat")) {
                                boolean firstTimeMenuChat= true;
                                while (true) {
                                    // it will skip the first time to avoid printing the menu twice
                                    //To sync the menu with the server
                                    if (firstTimeMenuChat) {
                                        firstTimeMenuChat = false;
                                    }
                                    else{
                                        // read the menu from the server
                                        // to avoid the menu from being printed twice
                                         result = in.readUTF();
                                        System.out.println(result);
                                    }
                                    String choiceToChat = console.readLine();
                                    out.writeUTF(choiceToChat);
                                    String res = in.readUTF();
                                    System.out.println(res);

                                    if (res.contains("You chose to return to the Previous menu")) {
                                        break;
                                    } else if (res.contains("Invalid choice")) {
                                        continue;
                                    }
                                    else if(res.contains("Chat with")) {
                                        boolean firstTimeChat = true;
                                        while (true) {
                                            // it will skip the first time to avoid printing the menu twice
                                            //To sync the menu with the server
                                            String resultChat="";
                                            if (firstTimeChat) {
                                                firstTimeChat = false;
                                            }
                                            else{
                                                // read the menu from the server
                                                // to avoid the menu from being printed twice
                                                 resultChat = in.readUTF();
                                                System.out.println(resultChat);
                                            }
                                            if (resultChat.contains("You chose to exit the chat")) {
                                                break;
                                            }
                                            System.out.println("Enter your message:");
                                            String message = console.readLine();
                                            out.writeUTF(message);

                                        }
                                    }
                                }

                            } else if(result.contains("return")){
                                break;
                            }

                        }

                    }
                    else if (output.contains("exit")) {
                        break;
                    }
                    else if(output.contains("Invalid choice")){
                        continue;
                    }
                }
            }
            else if(loggedIn && output.contains("as admin")){
                while (true){
                    // read the menu from the server
                    String menu = in.readUTF();
                    System.out.println(menu);
                    // read the user's choice
                    String choice = console.readLine();
                    out.writeUTF(choice);
                    // read the server's response
                    output = in.readUTF();
                    System.out.println(output);

                    if(output.contains("Logging Out")){
                        break;
                    }
                }

            }
            out.close();
            socket.close();
        } catch(UnknownHostException u) {
            // if the host is unknown
            // print the error message
            System.out.println(u);
        } catch(IOException i) {
            // if the input/output fails
            System.out.println(i);
        }
    }

}