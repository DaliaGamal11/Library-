package ServerSide;

import java.io.*;

public class Book {
    private  String title;
    private  String author;
    private  String genre;
    private  String status;

    private  String owner;

    private  Float price;


    public Book(String title, String author, String genre, Float price,String status, String owner) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.status = status;
        this.owner = owner;
    }
    //  Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return owner;
    }
    public Float getPrice() {
        return price;
    }


    //  Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setPrice(Float price) {
        this.price = price;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }



    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", status='" + status + '\'' +
                ", username='" + owner + '\'' +
                ", price=" + price +
                '}';
    }
    public  String AddBook(String owner) throws IOException, IOException {
        boolean exists = false;

        //check if title already exists
        // The Book Title is unique for each user
        try {
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/"+this.getUsername()+".txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                if (line.split(",")[0].equals(this.getTitle())) {
                    exists = true;
                    break;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }



        if (!exists) {
            // add the book to the file of the owner
            FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Books/"+this.getUsername()+".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            //write the Book in the file in the format title,author,genre,price,status
            bw.write(this.getTitle() + "," + this.getAuthor() + "," + this.getGenre() + ","  + this.getPrice()  + ","+ this.getStatus()+","+this.getUsername());
            bw.newLine();
            bw.close();

            return "Book added successfully";
        } else {
            return "title already exists";
        }
    }

    public static String getAllAvailableBooks(String username) {
       //read the books from the files that in the folder Books in The System

        String Books = "";
        for (int i = 0; i < User.getUsers().size(); i++) {
            //check if the user is not the current user
            // To avoid showing the current user's books with the available books to borrow
            if (! User.getUsers().get(i).getUsername().equals(username)) {
                try {
                    FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + User.getUsers().get(i).getUsername() + ".txt");
                    BufferedReader br = new BufferedReader(fr);
                    String line = br.readLine();
                    while (line != null) {
                        if (line.split(",")[4].equals("Available")) {
                            Books += line + "\n";
                        }
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    System.out.println("File not found");
                }
            }
        }

        return Books;

    }

    public static String searchBookByTitle(String title) {
        //read the books from the files that in the folder Books

        String Books = "";
        for (int i = 0; i < User.getUsers().size(); i++) {
            try {
                FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + User.getUsers().get(i).getUsername() + ".txt");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while (line != null) {
                    if (line.split(",")[0].equals(title)&&line.split(",")[4].equals("Available")) {
                        Books += line + "\n";
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("File not found");
            }
        }

        return Books;

    }

    public static String searchBookByAuthor(String author) {
        //read the books from the files that in the folder Books
        String Books = "";
        for (int i = 0; i < User.getUsers().size(); i++) {
            try {
                FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + User.getUsers().get(i).getUsername() + ".txt");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while (line != null) {
                    if (line.split(",")[1].equals(author)&&line.split(",")[4].equals("Available")) {
                        Books += line + "\n";
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("File not found");
            }
        }

        return Books;

    }
    public static String searchBookByGenre(String genre) {
        //read the books from the files that in the folder Books

        String Books = "";
        for (int i = 0; i < User.getUsers().size(); i++) {
            try {
                FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + User.getUsers().get(i).getUsername() + ".txt");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while (line != null) {
                    if (line.split(",")[2].equals(genre)&&line.split(",")[4].equals("Available")) {
                        Books += line + "\n";
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("File not found");
            }
        }

        return Books;

    }

    public static String getAllBooks(){
        //read the books from the files that in the folder Books

        String Books = "";
        for (int i = 0; i < User.getUsers().size(); i++) {
            try {
                FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + User.getUsers().get(i).getUsername() + ".txt");
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while (line != null) {
                    Books += line + "\n";
                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("File not found");
            }
        }

        return Books;

    }
}
