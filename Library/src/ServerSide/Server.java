package ServerSide;

import ServerSide.LibrarySetup.LibrarySetupService;

import java.io.*;
import java.net.*;

public class Server extends Thread {
    private Socket socket = null;
    DataOutputStream out = null;
    DataInputStream in = null;

    private User user;


    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public static void main(String args[]) {
        // create a new ServerSocket instance bound to the given port
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server started");
            System.out.println("Library Setup Started...");

            LibrarySetupService librarySetupService = new LibrarySetupService();
            librarySetupService.LibrarySetup();
            //print game configurations
            System.out.println("Library Setup Finished....");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Make the server wait for clients to connect
        while (true) {
            try {
                // wait for a client to connect
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted");
                //create a new thread to handle the client
                Server librarServer = new Server();
                librarServer.setSocket(socket);
                //add the client to the list of clients

                //start the thread to call the run() method
                librarServer.start();
                System.out.println("Thread #" + librarServer.getId() + " started");

            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    // override the run() method of the Thread class
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // write a welcome message to the client
            out.writeUTF("Hello from Library Server");
            // call the menu method
            loginMenu(in, out);
            if (user != null) {
                // check if the user is an admin or a user
                if (user.getRole(user.getUsername()).equalsIgnoreCase("admin")) {
                    // call the admin menu
                    adminMenu(in, out);
                } else if (user.getRole(user.getUsername()).equalsIgnoreCase("user")) {
                    // call the user menu
                    mainMenu(in, out);
                }
            }
            // close the connection
            out.writeUTF("Closing connection");
            socket.close();
            in.close();
        } catch (IOException e) {
            // print an error message if an exception occurs or the client disconnects
            System.out.println("Unable to communicate with the client, with error: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginMenu(DataInputStream in, DataOutputStream out) throws IOException {
        while (true) {
            // write a message to the client to choose from the menu
            out.writeUTF("choose from menu.\n 1. Register \n 2. Sign in \n (-) to exit from server");
            // read the user's choice
            String choice = in.readUTF();

            if (choice.equals("1")) {
                // perform the registration process
                out.writeUTF("You chose to register. Please enter your username ,name ,password and Role \n" +
                        "separated by comma \n eg: name,username,password,role");
                // read the user's input for username and name and password
                String line = in.readUTF();
                if (line.split(",").length != 4) {
                    // check if the user entered the correct number of details required
                    out.writeUTF("Invalid input. Please enter your username ,name and password \n" +
                            "separated by comma \n eg: name,username,password");
                    continue;
                }
                // split the user's input into an array
                // as name, username, password and role
                String[] userDetails = line.split(",");
                String name = userDetails[0];
                String username = userDetails[1];
                String password = userDetails[2];
                String role = userDetails[3];
                // check if the role entered is either admin or user
                if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("user")) {
                    out.writeUTF("Invalid role. Please enter either admin or user");
                    continue;
                }
                // create a new user object
                User u = new User(name, username, password, role);
                // call the register method
                String register = u.register(name, username, password, role);
                // send the response to the client
                out.writeUTF(register);
            } else if (choice.equals("2")) {
                // perform the sign-in process
                out.writeUTF("You chose to sign in. Please enter your username and password \n" +
                        "separated by comma \n eg: username,password\"");
                // read the user's input for username and password
                String line = in.readUTF();
                if (line.split(",").length != 2) {
                    out.writeUTF("Invalid input. Please enter your username and password \n" +
                            "separated by comma \n eg: username,password");
                    continue;
                }
                // split the user's input into an array
                String[] playerDetails = line.split(",");
                // as username and password
                String username = playerDetails[0];
                String password = playerDetails[1];
                User u = new User(username, password);
                // call the login method
                String login = u.login(username, password);
                out.writeUTF(login);
                if (login.contains("logged in successfully")) {
                    // set the user object to the user that logged in
                    user = u;
                    break;
                }

            } else if (choice.equals("-")) {
                break;
            } else {
                // handle invalid input
                out.writeUTF("Invalid choice. Please choose either 1 or 2.");
            }
        }
    }

    public void adminMenu(DataInputStream in, DataOutputStream out) throws IOException {
        // perform the admin menu
        while (true) {
            out.writeUTF("\n------------------------------------------------\n" +
                    "choose from menu.\n---------------------\n" +
                    " 1. View All Requests \n" +
                    " 2. View ALL Books \n" +
                    " 3. Log Out. \n");
            String choiceLib = in.readUTF();
            if (choiceLib.contains("1")) {
                // View all requests
                out.writeUTF("Viewing All Requests... \n---------------------------\n" +
                        "Format: Borrower,Owner,Book Title,Status,CreatedAT of The Request" +
                        "\n-------------------------------------------------------------------------------\n" +
                        Request.getAllRequests() + "\n---------------------------\n");
                //print all requests

            } else if (choiceLib.contains("2")) {
                // View all books
                out.writeUTF("Viewing All Books... \n---------------------------\n" +
                        "Book Title,Author,Genre,Price,Status,Owner\n---------------------------\n" +
                        Book.getAllBooks() + "\n---------------------------\n");
                //print all books
            } else if (choiceLib.contains("3")) {
                // Log out
                out.writeUTF("Logging Out... \n---------------------------\n ");
                break;
            } else {
                // handle invalid input
                out.writeUTF("Invalid choice. Please choose either 1 or 2.");
            }

        }
    }

    public void mainMenu(DataInputStream in, DataOutputStream out) throws IOException, InterruptedException {
        while (true) {
            out.writeUTF("\n------------------------------------------------\n" +
                    "choose from menu.\n---------------------\n" +
                    " 1. Add Book \n" +
                    " 2. View My Books \n" +
                    " 3. Delete Book \n" +
                    " 4. Make a Book Available \n" +
                    " 5. Browse & Borrow Books \n" +
                    " 6. Requests & Chats \n" +
                    " 7. Exit.");

            String choiceLib = in.readUTF();
            if (choiceLib.contains("1")) {
                out.writeUTF("Adding A Book... \n---------------------------\n " +
                        "Enter the book details separated by comma \n eg: title,author,genre,price");
                String line = in.readUTF();
                if (line.split(",").length != 4) {
                    // check if the user entered the correct number of details required

                    out.writeUTF("Invalid input.");
                    continue;
                }
                // split the user's input into an array
                String[] bookDetails = line.split(",");
                String title = bookDetails[0];
                String author = bookDetails[1];
                String genre = bookDetails[2];
                float price = Float.parseFloat(bookDetails[3]);
                // create a new book object
                Book b = new Book(title, author, genre, price, "Available", user.getUsername());
                // call the addBook method to add the book to the user's library(file)
                String addBook = b.AddBook(user.getUsername());
                // send the response to the client
                out.writeUTF(addBook);

            } else if (choiceLib.contains("2")) {
                // Get the list of books for the user
                // call the getMyBooks method to get the list of books in the user's library
                out.writeUTF("\n \n Viewing My Books... \n---------------------------\n " +
                        "Books in your library are: \n" +
                        "Format: Title, Author, Genre, Price, Status, Owner UserName \n" +
                        user.getMyBooks() + "\n"
                );

            } else if (choiceLib.contains("3")) {
                // perform the delete book process
                out.writeUTF("\n \n Deleting A Book... \n---------------------------\n " +
                        "Enter the title of the book you want to delete");
                // read the user's input for the title of the book to delete
                String title = in.readUTF();
                String deleteBook = user.deleteBook(title);
                out.writeUTF(deleteBook);

            } else if (choiceLib.contains("4")) {
                // The Owner of the book can make the book available for borrowing after it has been borrowed by another user
                out.writeUTF("\n \n Making A Book Available... \n---------------------------\n " +
                        "Enter the title of the book you want to make available");
                String title = in.readUTF();
                String makeAvailable = user.makeAvailable(title);
                out.writeUTF(makeAvailable);

            }
            else if (choiceLib.contains("5")) {
                // perform the browse and borrow process for the user
                // call the browse_borrowMenu method
                browse_borrowMenu(in, out);

            } else if (choiceLib.contains("6")) {
                // perform the request and chat process
                // call the requestMenu method
                requestMenu(in, out);
            } else if (choiceLib.contains("7")) {
                // perform the exit process
                out.writeUTF("You chose to exit . Thank you for playing HangMan Game");
                break;
            } else {
                // handle invalid input
                out.writeUTF("Invalid choice. Please choose either 1 or 2 or 3 or 4 or 5.\n");
            }
        }
    }

    public void browse_borrowMenu(DataInputStream in, DataOutputStream out) throws IOException {
        while (true) {
            out.writeUTF("\n \nBrowsing & Borrowing Books... \n---------------------------\n" +
                    "1. View All Available Books \n" +
                    "2. Search For A Book \n" +
                    "3. Borrow A Book \n" +
                    "4. View Books Borrowed By Me \n" +
                    "5. Return To Main Menu \n");
            String choice = in.readUTF();
            if (choice.contains("1")) {
                out.writeUTF("\n \nViewing All Available Books... \n---------------------------\n" +
                        "Books available in the library are: \n ------------------------------\n" +
                        "Format: Title, Author, Genre, Price, Status, Owner\n" +
                        Book.getAllAvailableBooks(user.getUsername()) + "\n"
                );
            } else if (choice.contains("2")) {
                // Search for a book by title or author or genre Menu
                searchBook(in, out);

            } else if (choice.contains("3")) {
                // Borrow a book
                out.writeUTF("\n \nBorrowing A Book... \n---------------------------\n" +
                        "Enter the title,owner of the book you want to borrow to send a request to the owner \n eg: title,owner\n");
                String title_owner = in.readUTF();
                if (title_owner.split(",").length != 2) {
                    // check if the user entered the correct number of details required
                    // if not, send an error message to the user to enter the correct details
                    // the correct details should be in the format: title,owner
                    out.writeUTF("Invalid input.");
                    continue;
                }
                // split the user's input into an array
                String[] bookDetails = title_owner.split(",");
                String title = bookDetails[0];
                String owner = bookDetails[1];
                // get the borrower's username
                String borrower = user.getUsername();
                // create a new request object
                Request r = new Request(borrower, title, "Pending", owner);
                // call the borrowBookRequest method to send a request to the owner of the book
                String borrowBook = r.borrowBookRequest();
                out.writeUTF(borrowBook);
            } else if (choice.contains("4")) {
                out.writeUTF("\n \nViewing Books Borrowed By Me... \n---------------------------\n" +
                        "Books borrowed by you are: \n" +
                        "----------------------------------------\n" +
                        "Format: Owner , BookTitle\n" +
                        "----------------------------------------\n\n"+
                        user.getBooksBorrowed() + "\n" +
                        "----------------------------------------\n"
                );
            } else if (choice.contains("5")) {
                out.writeUTF("You chose to return to the main menu");
                break;
            } else {
                // handle invalid input
                out.writeUTF("Invalid choice. Please choose either 1 or 2 or 3 or 4.\n");
            }
        }
    }

    public void searchBook(DataInputStream in, DataOutputStream out) throws IOException {
        out.writeUTF("\n \nSearching For A Book... \n---------------------------\n" +
                "1. Title of the book you want to search for \n" +
                "2. Author of the book you want to search for \n" +
                "3. Genre of the book you want to search for \n"
        );
        String choice2 = in.readUTF();
        if (choice2.contains("1")) {
            out.writeUTF(
                    "Enter the title of the book you want to search for" +
                            "\n------------------------------------------------------\n");
            String title = in.readUTF();
            out.writeUTF("\n \nSearching For A Book... \n---------------------------\n" +
                    "Books available in the library with Title (" + title + ") are: \n-----------------------------------------\n" +
                    "Format: Title, Author, Genre, Price, Status, Owner\n" +
                    "----------------------------------------------------\n"+
                    Book.searchBookByTitle(title) + "\n"+
                    "----------------------------------------------------\n"
            );
        } else if (choice2.contains("2")) {
            out.writeUTF(
                    "Enter the Author you want to search for" +
                            "\n------------------------------------------------------\n");
            String author = in.readUTF();
            out.writeUTF("\n \nSearching For A Book... \n---------------------------\n" +
                    "Books available in the library for Author (" + author + ")are: \n------------------------------------------\n" +
                    "Format: Title, Author, Genre, Price, Status, Owner\n" +
                    "----------------------------------------------------\n"+
                    Book.searchBookByAuthor(author) + "\n"+
                    "----------------------------------------------------\n"
            );
        } else if (choice2.contains("3")) {
            out.writeUTF(
                    "Enter the Genre you want to search for" +
                            "\n------------------------------------------------------\n");
            String genre = in.readUTF();
            out.writeUTF("\n \nSearching For A Book... \n---------------------------\n" +
                    "Books available in Gener (" + genre + ")  are: \n ------------------------------\n" +
                    "Format: Title, Author, Genre, Price, Status, Owner\n" +
                    "----------------------------------------------------\n"+
                    Book.searchBookByGenre(genre) + "\n"+
                    "----------------------------------------------------\n"

            );
        }
        else {
            // handle invalid input
            out.writeUTF("Invalid choice.\n");
        }

    }

    public void requestMenu(DataInputStream in, DataOutputStream out) throws IOException {
        while (true) {
            // perform the request handling process
            out.writeUTF("\n \nRequests... \n---------------------------\n" +
                    "1. View Borrow Requests \n" +
                    "2. View Lend Requests \n" +
                    "3. Accept Request \n" +
                    "4. Reject Request \n" +
                    "5. Chats \n" +
                    "6. Return To Main Menu \n");
            String choice = in.readUTF();
            if (choice.contains("1")) {
                // View Borrow Requests
                out.writeUTF("\n\nViewing Incoming Requests... \n---------------------------\n" +
                        "Requests for your books are: \n" +
                        "Format:Borrower,Owner,Book Title,Status,Created AT" +
                        "\n----------------------------------------------------------------------------\n" +
                        user.getBorrowRequests() +
                        "\n----------------------------------------------------------------------------\n"

                );
            } else if (choice.contains("2")) {
                // View Lend Requests To Track The Status Of The Requests You Sent
                out.writeUTF("\n\nViewing Outgoing Requests Status... \n---------------------------\n" +
                        "Requests for books you want to lend: \n" +
                        "Format: Borrower,Owner,Book Title,Status,Created AT" +
                        "\n-------------------------------------------------------------------------------\n" +
                          user.getLendRequests() +
                        "\n-------------------------------------------------------------------------------\n"
                );
            } else if (choice.contains("3")) {
                out.writeUTF("\n\nAccepting A Request...\n---------------------------------\n " +
                        "Enter the borrower UserName and the title of the book you want to accept separated by comma \n eg: username,title");
                String req = in.readUTF();

                if (req.split(",").length != 2) {
                    // handle invalid input
                    // if the input is not in the format username(of the Borrower),title
                    out.writeUTF("Invalid input.");
                    continue;
                }
                // split the input into username and title
                String[] reqDetails = req.split(",");
                String username = reqDetails[0];
                String title = reqDetails[1];
                // create a request object
                Request request = new Request(username, title, "Accepted", user.getUsername());
                // call the acceptRequest method of the user object
                // to accept the request
                // and Creat a chat between the borrower and the owner
                String acceptRequest = user.acceptRequest(request);
                out.writeUTF(acceptRequest);
            } else if (choice.contains("4")) {
                out.writeUTF("\n \nRejecting A Request... \n---------------------------\n " +
                        "Enter the borrower UserName and the title of the book you want to Reject separated by comma \n eg: username,title");
                String req = in.readUTF();

                if (req.split(",").length != 2) {
                    out.writeUTF("Invalid input.");
                    continue;
                }
                String[] reqDetails = req.split(",");
                String username = reqDetails[0];
                String title = reqDetails[1];
                Request request = new Request(username, title, "Rejected", user.getUsername());

                String acceptRequest = user.rejectRequest(request);
                out.writeUTF(acceptRequest);
            } else if (choice.contains("5")) {
                // open the chat menu
                chatMenu(in, out);

            } else if (choice.contains("6")) {
                out.writeUTF("You chose to return to the main menu");
                break;
            } else {
                // handle invalid input
                out.writeUTF("Invalid choice. Please choose either 1 or 2 or 3 or 4 or 5.\n");
            }
        }
    }

    public void chatMenu(DataInputStream in, DataOutputStream out) throws IOException {
        while (true) {
            // Show All Chats
            out.writeUTF("\n \nAll Chats... \n---------------------------\n" +
                    "To open a chat, enter the username of the person you want to chat with \n" +
                    "----------------------------------------------------------------------------------\n" +
                    user.getAllUsers_I_ChatWith() +
                    "------------------------------------------------\n"+
                    "To return to the main menu, enter - \n");
            // Allow the user to choose a chat to open by entering the username of the person they want to chat with
            String choice = in.readUTF();
            if (choice.equals("-")) {
                // if the user enters - return to the Previous Menu
                out.writeUTF("You chose to return to the Previous menu");
                break;
            }
            if(user.chatExists(choice)){
                // if the chat exists, open the chat
                // by calling the chat method
                chat(in, out, choice);
            } else {
                out.writeUTF("Invalid choice. Please choose a valid username.\n");
            }

        }
    }

    public void chat(DataInputStream in, DataOutputStream out, String username) throws IOException {
        while (true) {
            // Show the chat with the user
            // the Chat File is updated every time a message is sent
            // so the chat is always up to date
            out.writeUTF("\n \nChat with " + username + "... \n---------------------------\n" +
                    "To exit the chat, enter - \n"+
                    "To send a message, enter the message you want to send \n"+
                    "Messages: \n" +
                    user.getChat(username) + "\n"
                    + "-------------------------------------------------------\n"
            );
            // Allow the user to send a message to the other user until they choose to exit by entering -
            String message = in.readUTF();
            if (message.equals("-")) {
                out.writeUTF("You chose to exit the chat");
                break;
            }
            user.sendMessage(username, message);
        }
    }
}