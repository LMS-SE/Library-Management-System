package edu.software.lms;
import java.util.logging.Logger;


public class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Window window= WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP,new UserService());
        while (window!=null) {
            window = window.buildNextWindow();

            //factory pattern
        }
        logger.info("Thank you for using Our Library System!");
    }
}   