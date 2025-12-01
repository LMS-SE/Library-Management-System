package edu.software.lms;

import java.util.Scanner;




public class LoginAndSignUpWindow implements Window {
    UserService userService;

    public LoginAndSignUpWindow(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Window buildNextWindow() {
        printMessage();
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> {
                return loginOperation();
            }
            case "2" -> {
                return SignUpOperation();
            }
            case "0" -> {
                return WindowFactory.create(NextWindow.EXIT,userService);
            }
            default -> {
                System.out.println("Invalid choice. Try again.");
                return this;
            }
        }
    }


    private void printMessage(){
        System.out.println("\n===== Library System Menu =====");
        System.out.println("1. Log In");
        System.out.println("2. Sign Up");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private Window SignUpOperation() {

        SignUpResult signUpResult;
        while(true){
            signUpResult=createAccountPrompt();
            if(exitSignUp(signUpResult)){
                break;
            }
            else {
                System.out.println(signUpResult.message);
            }
        }
        System.out.print(signUpResult.message);
        if(signUpResult==SignUpResult.CANCEL_SIGNUP){
            return this;
        }
        return WindowFactory.create(NextWindow.ADMIN_BOOK_OPERATIONS,userService);
    }

    private Window loginOperation() {
        LoginResult loginResult;
        while(true){
            loginResult= logInPrompt();
            if(exitLogin(loginResult)){
                break;
            }
            else {
                System.out.println(loginResult.message);
            }
        }
        System.out.print(loginResult.message);
        if(loginResult==LoginResult.CANCEL_LOGIN){
            return this;
        }
        return WindowFactory.create(NextWindow.ADMIN_BOOK_OPERATIONS,userService);
    }

    private boolean exitSignUp(SignUpResult signUpResult) {
        boolean cancelled=signUpResult==SignUpResult.CANCEL_SIGNUP;
        boolean SignedUp=signUpResult==SignUpResult.USER_CREATED_SUCCESSFULLY;

        return SignedUp||cancelled;
    }

    public LoginResult logInPrompt() {

        String username,pwd;
        System.out.println("==Log In==");
        System.out.println("==To Go back type 0 in either the password or the username==");
        System.out.print("Username : ");
        username=scanner.nextLine();
        System.out.print("Password : ");

        pwd=scanner.nextLine();


        return userService.validateLoginCredentials(username, pwd);
    }
    public SignUpResult createAccountPrompt() {

        String username,pwd,email;
        System.out.println("==Create Account==");
        System.out.println("==To Go back type 0 in either the password or the username==");
        System.out.print("Username : ");
        username=scanner.nextLine();
        System.out.print("Password : ");
        pwd=scanner.nextLine();
        System.out.print("Email : ");
        email=scanner.nextLine();

        return userService.validateCreateAccountCredentials(username, pwd,email);
    }

    private boolean exitLogin(LoginResult loginResult) {
        boolean cancelled=loginResult==LoginResult.CANCEL_LOGIN;
        boolean loggedIn=loginResult==LoginResult.USER_FOUND_SUCCESSFULLY;

        return loggedIn||cancelled;
    }
}
