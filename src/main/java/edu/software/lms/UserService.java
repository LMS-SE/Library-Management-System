package edu.software.lms;


import java.util.Scanner;

public class UserService {
    private static UserRepository userRepository=new InMemoryUserRepository();;
    private static BookRepository bookRepository=new InMemoryBooks();
    static Scanner sc=new Scanner(System.in);

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private User currentUser;
    public UserService() {

    }

    private LoginResult validateLoginCredentials(String username, String pwd) {
        if(pwd.equals("0")||username.equals("0")) {
            return null;
        }
        if(pwd.length()<8) {
            return LoginResult.PASSWORD_TOO_SHORT;
        }
        if(!CustomUtilities.isStrongPassword(pwd)) {
            return LoginResult.PASSWORD_WEAK;
        }
        //encrypt password
        String hashedPwd=CustomUtilities.hashPassword(pwd,username);

        Pair<User,LoginResult> result=userRepository.validateCredentials(username,hashedPwd);
        currentUser=result.first;
        return result.second;
    }
    private SignUpResult validateCreateAccountCredentials(String username, String pwd) {
        if(pwd.equals("0")||username.equals("0")) {
            return null;
        }
        if(pwd.length()<8) {
            return SignUpResult.PASSWORD_TOO_SHORT;
        }
        if(!CustomUtilities.isStrongPassword(pwd)) {
            return SignUpResult.PASSWORD_WEAK;
        }
        //encrypt password
        String hashedPwd=CustomUtilities.hashPassword(pwd,username);
        User newUser=new User(username,hashedPwd);
        boolean userCreationResult= userRepository.addUser(newUser);
        if(userCreationResult) {
            this.currentUser=newUser;
            return SignUpResult.USER_CREATED_SUCCESSFULLY;
        }
        return SignUpResult.USER_CREATION_FAILED;
    }

    public SignUpResult createAccountPrompt() {

        String username,pwd;
        System.out.println("==Create Account==");
        System.out.println("==To Go back type 0 in either the password or the username==");
        System.out.print("Username : ");
        username=sc.nextLine();
        System.out.print("Password : ");
        pwd=sc.nextLine();


        return validateCreateAccountCredentials(username, pwd);
    }

    public LoginResult logInPrompt() {

        String username,pwd;
        System.out.println("==Log In==");
        System.out.println("==To Go back type 0 in either the password or the username==");
        System.out.print("Username : ");
        username=sc.nextLine();
        System.out.print("Password : ");

        pwd=sc.nextLine();


        return validateLoginCredentials(username, pwd);
    }
}
