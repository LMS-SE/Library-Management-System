package edu.software.lms;

public class AdminBookOperationsWindow implements Window {
    public AdminBookOperationsWindow(UserService userService) {
        this.userService=userService;
    }


    private void printMessage() {

        System.out.println("Welcome to Admin Book Operations");
        System.out.println("0: exit application");
        System.out.println("back: log-out");
        //todo: add and search for books functionality, also refine these commands ..
    }


    private final UserService userService;

    @Override
    public Window buildNextWindow() {
        printMessage();
        String choice = scanner.nextLine();
        switch (choice) {
            case "back" -> {
                System.out.println("logging out : ");
                return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP,userService);
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
}
