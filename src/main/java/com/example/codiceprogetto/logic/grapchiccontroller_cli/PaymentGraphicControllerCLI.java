package com.example.codiceprogetto.logic.grapchiccontroller_cli;

import com.example.codiceprogetto.logic.appcontroller.PaymentApplicativeController;
import com.example.codiceprogetto.logic.bean.PaymentBean;
import com.example.codiceprogetto.logic.enumeration.PaymentType;
import com.example.codiceprogetto.logic.exception.DAOException;
import com.example.codiceprogetto.logic.exception.InvalidFormatException;
import com.example.codiceprogetto.logic.utils.PrinterCLI;
import com.example.codiceprogetto.logic.utils.SessionUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentGraphicControllerCLI extends AbsGraphicControllerCLI {
    PaymentApplicativeController toPay = new PaymentApplicativeController();
    SessionUser su = SessionUser.getInstance();
    int count = 1;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void start() {
        int choice = -1;
        while (choice == -1) {
            try {
                choice = showMenu();
                switch (choice) {
                    case 1:
                        handlePayment();
                        break;
                    case 2:
                        cancelOrderAndReturnHome();
                        break;
                    default:
                        throw new InvalidFormatException("Invalid choice");
                }
            } catch (IOException | DAOException | InvalidFormatException | SQLException e) {
                choice = -1;
                Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
            }
        }
    }

    private void handlePayment() throws IOException, SQLException, InvalidFormatException, DAOException {
        if (count % 2 == 0) {
            Logger.getAnonymousLogger().log(Level.INFO, "Payment rejected, order deleted!");
            new HomeGraphicControllerCLI().start();
            return;
        }

        PrinterCLI.print("Do you want to pay with your credit card or with PayPal? (digit CARD or PAYPAL)");
        String choose = reader.readLine();

        if ("CARD".equals(choose)) {
            handleCardPayment();
        } else {
            throw new InvalidFormatException("Invalid payment method");
        }
    }

    private void handleCardPayment() throws IOException, SQLException, InvalidFormatException, DAOException {
        PrinterCLI.print("Do you want to use your memorized payment method or use a new one (digit OWN or NEW): ");
        String choose = reader.readLine();

        if ("NEW".equals(choose)) {
            handleNewCardPayment();
        } else if ("OWN".equals(choose)) {
            throw new InvalidFormatException("Not implemented yet");
        } else {
            throw new InvalidFormatException("Invalid choice");
        }
    }

    private void handleNewCardPayment() throws IOException, SQLException, DAOException {
        String name, lastName, cardNumber, cvv, expiration, zip;
        PrinterCLI.print("Name: ");
        name = reader.readLine();
        PrinterCLI.print("Last name: ");
        lastName = reader.readLine();
        PrinterCLI.print("Card number: ");
        cardNumber = reader.readLine();
        PrinterCLI.print("Cvv: ");
        cvv = reader.readLine();
        PrinterCLI.print("Expiration: ");
        expiration = reader.readLine();
        PrinterCLI.print("Billing zip code: ");
        zip = reader.readLine();

        PaymentBean paymentBean = new PaymentBean(name, lastName, expiration, cardNumber, cvv, zip);

        if (toPay.checkCustomerPayment(su.getThisUser().getEmail()) && askSave()) {
            toPay.insertPayment(paymentBean, su.getThisUser().getEmail());
        }

        toPay.createTransaction(su.getThisUser().getEmail(), PaymentType.CARD.getId());
        new PaymentSummaryGraphicControllerCLI().start();
    }

    private void cancelOrderAndReturnHome() throws SQLException, DAOException {
        toPay.deleteOrder(su.getThisUser().getEmail());
        new HomeGraphicControllerCLI().start();
    }

    @Override
    public int showMenu() throws IOException {
        PrinterCLI.printf("--- Bubble Shop ---");
        PrinterCLI.printf("1. Choose payment method and complete your payment");
        PrinterCLI.printf("2. Back (back to home!)");

        return getMenuChoice(1, 2);
    }

    private boolean askSave() throws IOException {
        PrinterCLI.print("Do you want to save your payment method? (y/n)");
        String choose = reader.readLine();
        return choose.equalsIgnoreCase("y");
    }

}
