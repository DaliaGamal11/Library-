package ServerSide.LibrarySetup;

import ServerSide.Book;
import ServerSide.User;

import java.io.*;
import java.util.ArrayList;

public class LibrarySetupService {

    public void LibrarySetup(){
        try {
            // read the books from the files that in the folder Books
            // It Will Run Once When The Server Starts
            User.setUsers(readUsersFromFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //read Users from file using file reader and buffered reader
    public  ArrayList<User> readUsersFromFile() throws IOException {
        // read the users from the file Users.txt
        ArrayList<User> users = new ArrayList<User>();
        FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Users.txt");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            String[] user = line.split(",");
            User u = new User(user[0], user[1], user[2], user[3]);
            users.add(u);
            line = br.readLine();
        }
        br.close();
        return users;
    }



}
