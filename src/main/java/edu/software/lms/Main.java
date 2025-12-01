package edu.software.lms;



public class Main {


    public static void main(String[] args) {
        Window window= WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP,new UserService());
        while (window!=null) {
            window = window.buildNextWindow();

            //factory pattern
        }
        System.out.println("Thank you for using Our Library System!");
    }
}   