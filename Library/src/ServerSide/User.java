package ServerSide;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
public class User {
        private String name;
        private String username;
        private String password;

        private String role;

        //arraylist to store the users
        private static ArrayList<User> users = new ArrayList<User>();
        //getters and setters
        public static ArrayList<User> getUsers() {
                return users;
        }
        public static void setUsers(ArrayList<User> users) {
                User.users = users;
        }
        //constructor
        public User(String name, String username, String password,String role) {
            this.name = name;
            this.username = username;
            this.password = password;
            this.role = role;
        }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

        public  String register(String name, String username, String password,String role ) throws IOException, IOException {
            boolean exists = false;
            //check if username already exists
            //sync the players between threads to prevent Multiple Users with same username at the same time
            synchronized (users) {
                for (User user : users) {
                    if (user.getUsername().equals(username)) {
                        exists = true;
                    }
                }
            }
            //if username doesn't exist write player in file and add it to the arraylist
            if (!exists) {
                //write the User in the file using file writer and buffered writer
                // open the file in append mode(true)
                FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Users.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                //write the User in the file in the format name,username,password
                if(role.equalsIgnoreCase("user")){
                    role = "user";
                }
                else if(role.equalsIgnoreCase("admin")){
                    role = "admin";
                }
                //write the User in the file in the format name,username,password,role
                bw.write(name + "," + username + "," + password+ "," + role);
                bw.newLine();
                bw.close();
                //sync the Users between threads to prevent adding the Users in the arraylist at the same time
                //add the User to the arraylist
                synchronized (users) {
                    // we need synchronized block to prevent the arraylist from being accessed by multiple threads at the same time
                    users.add(new User(name, username, password,role));
                }
                return "User added successfully as "+role+", Try To login Now !";

            } else {
                return "Username already exists";
            }
        }

    public String login(String username, String password) {
            //check if the username and password are correct
            boolean exists = false;
            boolean correctPassword = false;
            String role="" ;
        for (User user : users) {
            if (user.getUsername().equals(username) ) {
                exists = true;
                if (user.getPassword().equals(password)) {
                    correctPassword = true;
                    role = user.role;
                }
            }
        }
            if (!exists) {
                //if the username doesn't exist return 404 error
                return "404 error, not found";
            } else if (!correctPassword) {
                //if the password is incorrect return 401 error
                return "401 error, unauthorized";
            }
            else {
                return "logged in successfully as "+role ;
            }

    }
    public String getRole(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user.role;
            }
        }
        return "User not found";
    }
    //getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getMyBooks() {
        String Books = " \n";
        //read history from the file with the username
        try {
            //read the books from the file with the username
            // open the file in read mode
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + username + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                Books += line + "\n";
                line = br.readLine();
            }
        } catch (IOException e) {
            // if the file is not found which means the user has no books it prints file not found and returns empty string
            System.out.println("File not found");
        }
        return Books;
    }

    public String deleteBook(String book) {
        //delete the book from the file with the username
        try {
            // write the books in String  then delete the file and write the books back without the deleted book
            String books = "";
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + username + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                //split the line to get the book name and check if it is the book to be deleted
                String parts[] = line.split(",");
                if (!parts[0].equals(book)) {
                    // if the book is not the book to be deleted add it to the string
                    // to write it back to the file
                    books += line + "\n";
                }
                line = br.readLine();
            }

            //write the books back to the file
            FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Books/" + username + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(books);
            bw.close();

            return "Book deleted successfully";

        } catch (IOException e) {
            return "Error deleting book";
        }
    }

    public String getBorrowRequests() {
        String requestslist = "";
        //read requests from the file with the username
        try {
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                // if the username is the same as the Owner username in the request add the request to the string
                if (parts[1].equals(username)) {
                    requestslist += line + "\n";
                }
                line = br.readLine();

                System.out.println(requestslist);

            }
        } catch (IOException e) {

            System.out.println("File not found");

        }
        if(requestslist.equals("")){
            return "No requests";
        }
        return requestslist;
    }

    public  String getLendRequests(){
        String requests = "";
        //read requests from the file with the username
        try {
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                // if the username is the same as the Borrower username in the request add the request to the string
                if (parts[0].equals(username)) {
                    requests += line + "\n";
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
        if(requests.equals("")){
            return "No requests";
        }
        return requests;
    }
    public String acceptRequest(Request request) {
            //check if the Request existed
            boolean exists = false;
            try {
                FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while (line != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(request.getBorrower()) && parts[1].equals(request.getOwner()) && parts[2].equals(request.getBookTitle()) && parts[3].equals("Pending")) {
                        exists = true;
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("File not found");
            }
            if (!exists) {
                return "Request not found";
            }
        // change the status of the request to accepted
        try {
            // write the requests in String  then delete the file and write the requests back with the new status
            String requests = "";
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String parts[] = line.split(",");
                // if the request is the request to be accepted change the status to accepted
                if (parts[0].equals(request.getBorrower()) && parts[1].equals(request.getOwner()) && parts[2].equals(request.getBookTitle())) {
                    line = parts[0] + "," + parts[1] + "," + parts[2] + "," + "accepted"+","+parts[4];
                }
                requests += line + "\n";
                line = br.readLine();
            }
            //write the requests back to the file with the new status of the accepted request
            FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(requests);
            bw.close();
            //change the status of the book to lent from available
            changeBookStatus(request.getBookTitle(), "Lent");
            //create a chat between the owner and the borrower of the book
            creatChat(request.getBorrower(), request.getOwner());
            return "Request accepted successfully";
        } catch (IOException e) {
            return "Error accepting request";
        }
    }

    public String changeBookStatus(String bookTitle, String status) {
        //change the status of the book to the new status
        try {
            // write the books in String  then delete the file and write the books back with the new status
            String books = "";
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + username + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String parts[] = line.split(",");
                // if the book is the book to be changed change the status to the new status
                if (parts[0].equals(bookTitle)) {
                    line = parts[0] + "," + parts[1] + "," + parts[2]+ ","+parts[3]+","+status+"," + parts[5];
                }
                books += line + "\n";
                line = br.readLine();
            }
            // write the books back to the file with the new status of the book
            FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Books/" + username + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(books);
            bw.close();
            return "Status changed successfully";

        } catch (IOException e) {
            return "Error changing status";
        }
    }
    public void creatChat(String borrower, String owner) {
        //create a chat file between the borrower and the owner

        //check if the chat already exists between the two users to avoid creating a new chat to the same users
        // to Save space and time and to avoid confusion
        if(new File("src/ServerSide/LibrarySetup/Chats/" + borrower+"_"+ owner + ".txt").exists()){
            return;
        }
        if (new File("src/ServerSide/LibrarySetup/Chats/" + owner+"_"+ borrower + ".txt").exists()){
            return;
        }
        // create a new chat file and write the chat messages in it
        try {
            File file = new File("src/ServerSide/LibrarySetup/Chats/" + borrower+"_"+ owner + ".txt");
            if (file.createNewFile()) {
                System.out.println("Chat created: " + file.getName());
            } else {
                System.out.println("Chat already exists.");
            }
        } catch (IOException e) {
            // if an error occurred while creating the chat file print the error
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String rejectRequest(Request request) {
        // change the status of the request to rejected
        try {
            // write the requests in String  then delete the file and write the requests back with the new status
            String requests = "";
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String parts[] = line.split(",");
                // if the request is the request to be rejected change the status to rejected
                if (parts[0].equals(request.getBorrower()) && parts[1].equals(request.getOwner()) && parts[2].equals(request.getBookTitle())) {
                    line = parts[0] + "," + parts[1] + "," + parts[2] + "," + "rejected"+","+parts[4];
                }
                requests += line + "\n";
                line = br.readLine();
            }
            //write the requests back to the file with the new status of the rejected request
            FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(requests);
            bw.close();
            return "Request rejected successfully";

        } catch (IOException e) {
            return "Error rejecting request";
        }
    }

    public String getBooksBorrowed(){
            String books = "";
        //read books from the file with the username
        try {
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                // if the username is the same as the Borrower username in the request add the request to the string
                if (parts[0].equals(username) && parts[3].equals("accepted")) {
                    books += parts[1]+","+parts[2] + "\n";
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
        if(books.equals("")){
            return "No books borrowed";
        }
        return books;
    }

    public String makeAvailable(String bookTitle){
        //change the status of the book to available
        changeBookStatus(bookTitle, "Available");
        return "Book is now available";
    }

    public  String getAllUsers_I_ChatWith(){
        String users = "";
        //read all the chat files and get the usernames of the users that the current user has chatted with
        File folder = new File("src/ServerSide/LibrarySetup/Chats");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String[] parts = file.getName().split("_");
                if(parts[0].contains(this.username)){
                    // split the file name to get the usernames of the users
                    // remove the .txt extension
                    String user2=parts[1].split("\\.")[0];
                    users += user2 + "\n";
                }
                if(parts[1].contains(this.username)){
                    // split the file name to get the usernames of the users
                    users += parts[0] + "\n";
                }
            }
        }
        if(users.equals("")){
            return "No users \n";
        }
        return users;
    }

    public  Boolean chatExists(String user){
        //check if a chat file exists between the current user and the user passed as a parameter
        File file = new File("src/ServerSide/LibrarySetup/Chats/" + this.username+"_"+ user + ".txt");
        File file2 = new File("src/ServerSide/LibrarySetup/Chats/" + user+"_"+ this.username + ".txt");
        return file.exists() || file2.exists();
    }

    public String getChat(String user){
        //get the chat file between the current user and the user passed as a parameter
        File file = new File("src/ServerSide/LibrarySetup/Chats/" +this.username+"_"+ user + ".txt");
        File file2 = new File("src/ServerSide/LibrarySetup/Chats/" + user+"_"+ this.username + ".txt");
        String finalFile = "";
        if(file.exists()){
            // if the file exists get the file name
            // eg. if the current user is user1 and the user passed is user2 the file name will be user1_user2.txt
            finalFile = "src/ServerSide/LibrarySetup/Chats/" + this.username+"_"+ user + ".txt";
        }
        else if(file2.exists()){
            // if the file exists get the file name
            // eg. if the current user is user1 and the user passed is user2 the file name will be user2_user1.txt
            finalFile = "src/ServerSide/LibrarySetup/Chats/" + user+"_"+this.username + ".txt";
        }
        String chat = "";
        try {
            // read the chat file and add the chat to a string
            FileReader fr = new FileReader(finalFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                chat += line + "\n";
                line = br.readLine();
            }

        } catch (IOException e) {
            // when the file is not found it will notify me that the file is not found and this will be printed
            System.out.println("File not found");
        }
        return chat;
    }
    public  String sendMessage(String user, String message) {
        //check if a chat file exists between the current user and the user passed as a parameter
        File file = new File("src/ServerSide/LibrarySetup/Chats/" + this.username + "_" + user + ".txt");
        File file2 = new File("src/ServerSide/LibrarySetup/Chats/" + user + "_" + this.username + ".txt");
        String finalFile = "";
        if (file.exists()) {
            // if the file exists get the file name
            // eg. if the current user is user1 and the user passed is user2 the file name will be user1_user2.txt
            finalFile = "src/ServerSide/LibrarySetup/Chats/" + this.username + "_" + user + ".txt";
        } else if (file2.exists()) {
            // if the file exists get the file name
            // eg. if the current user is user1 and the user passed is user2 the file name will be user2_user1.txt
            finalFile = "src/ServerSide/LibrarySetup/Chats/" + user + "_" + this.username + ".txt";
        }
        try {
            LocalDateTime myDateObj = LocalDateTime.now();
            // Save the current date and time in a string like YYYY-MM-DD-HH-MM
            String formattedDate =   "Date ("+myDateObj.getDayOfMonth() +"-" + myDateObj.getMonthValue() + "-" +myDateObj.getYear()   + ") Time (" + myDateObj.getHour() + "-" + myDateObj.getMinute()+")" ;

            //write the message to the chat file
            FileWriter fw = new FileWriter(finalFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.username + ": " + message + " ::: at(" + formattedDate + ")" + "\n");
            bw.close();
            return "Message sent successfully";
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return "Error sending message";
        }

    }
}