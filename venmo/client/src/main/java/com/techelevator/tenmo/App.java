package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.model.security.AuthenticatedUser;
import com.techelevator.tenmo.model.security.User;
import com.techelevator.tenmo.model.security.UserCredentials;
import com.techelevator.tenmo.services.*;
import com.techelevator.tenmo.services.security.AuthenticationService;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_APPROVED  = 2;
    private Integer currentUserId;
    private boolean isPending;

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private TransferService transferService;
    private AccountService accountService;
    private UserService userService;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }
    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }
    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }
    private void mainMenu() {

        transferService = new TransferService(currentUser.getToken(), API_BASE_URL);
        accountService = new AccountService(currentUser.getToken(), API_BASE_URL);
        userService = new UserService(currentUser.getToken(), API_BASE_URL);
        currentUserId = currentUser.getUser().getId();

        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                isPending = false;
                viewTransferHistory();
            } else if (menuSelection == 3) {
                isPending = true;
                viewTransferHistory();
            } else if (menuSelection == 4) {
                createTransfer(true);
            } else if (menuSelection == 5) {
                createTransfer(false);
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    /*
    TODO We were given some starter code
    Most everything above this line was supplied
    Along with AuthenticatedUser, User and UserCredentials Models
    Also the AuthenticationService and ConsoleService
     */

	private void viewCurrentBalance() {
        System.out.println("Your current account balance is: $" + accountService.getBalance());
	}

	private void viewTransferHistory() {
        TransferView[] transferViewList = transferService.viewTransfersByPending(isPending);

        System.out.println("ID  \t|\t  From/To   |\t   Amount");

        for (TransferView transfer: transferViewList) {
            String print = transfer.getTransferID() + "\t | \t";
            print += transfer.getType().equals("Send") ? "To: ": "From: ";
            print += transfer.getUserTo() + "\t | \t$" + transfer.getAmount();
            System.out.println(print);
        }

        TransferView transfer = this.chooseTransfer(transferViewList);
        if (transfer == null) return;

        if (isPending){
            this.confirmPending(transfer.getTransferID());
        } else {
            System.out.println("Id: " + transfer.getTransferID() + "\nTo: " + transfer.getUserTo() + "\nFrom: " + transfer.getUserFrom() + "\nType: " +
                    transfer.getType() + "\nAmount: $" + transfer.getAmount());
        }
    }
    private TransferView chooseTransfer(TransferView[] transferViewList) {
        String prompt;
        if (isPending) {
            prompt = "Please enter transfer ID to accept or reject (0 to cancel): ";
        } else {
            prompt = "Please enter transfer ID to view details (0 to cancel): ";
        }

        while(true) {
            int choice = consoleService.promptForInt(prompt);
            if (choice==0) return null;
            for(TransferView transfer: transferViewList){
                if(transfer.getTransferID()==choice) return transfer;
            }
        }
    }
    private void confirmPending(int transferId) {
        int choice = 0;
        while (choice!=1 && choice!=2) {
            choice = consoleService.promptForInt("1: Approve\n2: Reject\n0: Cancel\n");
            if (choice==0) return;
        }

        boolean isApproved = choice == 1;

        transferService.updateTransfer(transferId, isApproved);
    }

	private void createTransfer(boolean isSend) {
        String print;
        int status;
        if (isSend) {
            print = "\nSelect user your are sending money to (0 to cancel):";
            status = STATUS_APPROVED;
        } else {
            print = "\nSelect user your are requesting money from (0 to cancel):";
            status = STATUS_PENDING;
        }

        User choice = this.getUser(print);
        if (choice==null) return;

        int fromId = isSend ? currentUserId: choice.getId();
        int toId = isSend ? choice.getId(): currentUserId;

        Transfer transfer = new Transfer();
        transfer.setStatusId(status);
        transfer.setUserFromId(fromId);
        transfer.setUserToId(toId);
        transfer.setAmount(consoleService.promptForBigDecimal("\nEnter amount: "));

        transferService.createTransfer(transfer);
	}
    private User getUser(String print) {
        System.out.println("\nUsers\n");
        User[] users = userService.getUsers();
        for (int i = 0; i < users.length; i++) {
            System.out.println((i+1) + " | " + users[i].getUsername());
        }
        while (true) {
            int choice = consoleService.promptForInt(print);
            if (choice==0) return null;
            if (choice>0 && choice< users.length) return users[choice-1];
        }
    }




}
