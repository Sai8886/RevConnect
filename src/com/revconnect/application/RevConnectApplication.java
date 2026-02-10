package com.revconnect.application;

import com.revconnect.ui.LoginUI;

public class RevConnectApplication {

    public static void main(String[] args) {
    	System.out.println("========================================");
    	System.out.println("	WELCOME TO REVCONNECT");
        System.out.println("========================================");
        System.out.println();
        
        
        LoginUI loginUI = new LoginUI();
        loginUI.start();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("   Thank you for using RevConnect!");
        System.out.println("========================================");
    }
}