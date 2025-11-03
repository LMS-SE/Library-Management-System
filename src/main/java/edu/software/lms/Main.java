package edu.software.lms;



public class Main {
    public static final UserService userService=new UserService();


    public static void main(String[] args) {
        boolean getOut=false;
        NextWindow nextWindow=NextWindow.LOGIN_AND_SIGNUP;
        while (true) {

            switch (nextWindow){
                case EXIT ->{
                    System.out.println("Thank you for using Our Library System!");
                    getOut=true;
                }
                case LOGIN_AND_SIGNUP ->{
                    nextWindow =  UserInterface.logInSignUpWindow();

                }

                case ADMIN_BOOK_OPERATIONS -> {
                    System.out.println("TODO :D");
                    return;
                }

            }
            if(getOut)break;

        }
    }
}   