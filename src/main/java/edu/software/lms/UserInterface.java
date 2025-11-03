package edu.software.lms;
import java.util.Scanner;

import static edu.software.lms.Main.userService;


public class UserInterface {
    private static final Scanner scanner = new Scanner(System.in);
    public static NextWindow logInSignUpWindow(){
        System.out.println("\n===== Library System Menu =====");
        System.out.println("1. Log In");
        System.out.println("2. Sign Up");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> {
                LoginResult loginResult;
                do{
                    //todo : handle the other returned cases
                    loginResult= userService.logInPrompt();
                    if(loginResult==null) {
                        break;
                    }
                }while(loginResult!=LoginResult.USER_FOUND_SUCCESSFULLY);
                System.out.print("Good username and pwd pal! ");
                return NextWindow.ADMIN_BOOK_OPERATIONS;

            }
            case "2" -> {
                SignUpResult signUpResult;
                do{
                    //todo : handle the other returned cases
                    signUpResult=userService.createAccountPrompt();
                    if(signUpResult==null) {
                        break;
                    }
                }while(signUpResult!=SignUpResult.USER_CREATED_SUCCESSFULLY);

            }
            case "0" -> {
                return NextWindow.EXIT;
            }
            default -> System.out.println("Invalid choice. Try again.");
        }
        return null;
    }


}
