package ServerSide;

import java.io.*;

import java.time.LocalDateTime;
public class Request {
    private String borrower;
    private String bookTitle;
    private String status;
    private String owner;

    public Request(String borrower, String bookTitle, String status, String owner) {
        this.borrower = borrower;
        this.bookTitle = bookTitle;
        this.status = status;
        this.owner = owner;
    }

    public String getBorrower() {
        return borrower;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getStatus() {
        return status;
    }

    public String getOwner() {
        return owner;
    }


    public String borrowBookRequest() throws IOException {
        //read the books from the files that in the folder Books
        String Book = "";
        for (int i = 0; i < User.getUsers().size(); i++) {
            if (User.getUsers().get(i).getUsername().equals(owner)) {
                try {
                    // read the file of the owner
                    // and check if the book is available
                    FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Books/" + User.getUsers().get(i).getUsername() + ".txt");
                    BufferedReader br = new BufferedReader(fr);
                    String line = br.readLine();
                    while (line != null) {
                        // check if the book is available and the owner is the same as the owner of the book
                        if (line.split(",")[0].equals(this.getBookTitle())
                                && line.split(",")[4].equals("Available")
                                && line.split(",")[5].equals(this.getOwner())) {
                            Book += line + "\n";
                            line = br.readLine();
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("File not found");
                }
            }
        }
        // check if this request is already sent
        // To Prevent the user from sending the same request multiple times
        FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            if (line.split(",")[0].equals(this.getBorrower())
                    && line.split(",")[1].equals(this.getOwner())
                    && line.split(",")[2].equals(this.getBookTitle())
                    && line.split(",")[3].equals("Pending")) {
                return "Request already sent";
            }
            line = br.readLine();
        }


        if (Book.equals("")) {
            return "Book not found";
        } else {
            LocalDateTime myDateObj = LocalDateTime.now();
            // Save the current date and time in a string like this "Date (day-month-year) Time (hour-minute)"
            String formattedDate =   "Date ("+myDateObj.getDayOfMonth() +"-" + myDateObj.getMonthValue() + "-" +myDateObj.getYear()   + ") Time (" + myDateObj.getHour() + "-" + myDateObj.getMinute()+")" ;

            //write the request in the file of the owner
            FileWriter fw = new FileWriter("src/ServerSide/LibrarySetup/Requests/Requests.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.getBorrower() + "," + this.getOwner() + "," +this.getBookTitle()  + "," + this.getStatus()+",Created AT "+formattedDate+ "\n" );
            bw.close();
            return "Request sent successfully";

        }
    }

    public static String getAllRequests(){
        String Requests = "";
        try {
            FileReader fr = new FileReader("src/ServerSide/LibrarySetup/Requests/Requests.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                Requests += line + "\n";
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
        return Requests;
    }
}
