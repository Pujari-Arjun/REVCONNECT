package com.revconnect.application;

import com.revconnect.ui.LoginUI;

public class RevConnectApplication {

    public static void main(String[] args) {
    	System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
    	System.out.println("       WELCOME TO REVCONNECT ");
    	System.out.println("  Connect • Share • Grow Together");
    	System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println();
        
        
        LoginUI loginUI = new LoginUI();
        loginUI.start();
        
        System.out.println();
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("   Thanks for spending time on RevConnect!");
        System.out.println("   See you again soon ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }
}