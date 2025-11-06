package edu.software.lms;

import java.util.Scanner;

public interface Window {
    final Scanner scanner = new Scanner(System.in);
    public Window buildNextWindow();
}
