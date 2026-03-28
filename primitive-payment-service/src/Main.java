import model.*;
import repository.*;
import repository.impl.BillRepository;
import repository.impl.PaymentRepository;
import service.*;
import constants.*;
import service.impl.PaymentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static PaymentService paymentService;

    public static void main(String[] args) {
        BillRepository billRepo = new BillRepository();
        PaymentRepository paymentRepo = new PaymentRepository();
        Account account = new Account(0.0);
        paymentService = new PaymentService(account, billRepo, paymentRepo);

        seedData(billRepo);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                continue;
            }

            String[] tokens = input.trim().split("\\s+");
            String command = tokens[0].toUpperCase();
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            if ("EXIT".equals(command)) {
                System.out.println("Good bye!");
                break;
            }

            processCommand(command, params);
        }
        scanner.close();
    }

    private static void processCommand(String command, String[] params) {
        try {
            switch (command) {
                case "CASH_IN":
                    handleCashIn(params);
                    break;
                case "LIST_BILL":
                    handleListBills();
                    break;
                case "PAY":
                    handlePay(params);
                    break;
                case "DUE_DATE":
                    handleDueDate();
                    break;
                case "SCHEDULE":
                    handleSchedule(params);
                    break;
                case "LIST_PAYMENT":
                    handleListPayment();
                    break;
                case "SEARCH_BILL_BY_PROVIDER":
                    handleSearch(params);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleCashIn(String[] params) {
        if (params.length < 1) return;
        double amount = Double.parseDouble(params[0]);
        paymentService.addFunds(amount);
        System.out.println("Your available balance: " + String.format("%.0f", paymentService.getBalance()));
    }

    private static void handleListBills() {
        System.out.println("Bill No. Type Amount Due Date State PROVIDER");
        paymentService.listBills().forEach(bill ->
                System.out.println(bill.getId() + ". " + bill.getType() + " " + String.format("%.0f", bill.getAmount()) + " " +
                        bill.getDueDate() + " " + bill.getState() + " " + bill.getProvider()));
    }

    private static void handlePay(String[] params) {
        if (params.length < 1) return;
        List<Long> ids = Arrays.stream(params).map(Long::parseLong).collect(Collectors.toList());
        paymentService.payBills(ids);
        System.out.println("Your current balance is: " + String.format("%.0f", paymentService.getBalance()));
    }

    private static void handleDueDate() {
        System.out.println("Bill No. Type Amount Due Date State PROVIDER");
        paymentService.getBillsDue().forEach(bill ->
                System.out.println(bill.getId() + ". " + bill.getType() + " " + String.format("%.0f", bill.getAmount()) + " " +
                        bill.getDueDate() + " " + bill.getState() + " " + bill.getProvider()));
    }

    private static void handleSchedule(String[] params) {
        if (params.length < 2) return;
        Long id = Long.parseLong(params[0]);
        String date = params[1];
        paymentService.schedulePayment(id, date);
        System.out.println("Payment for bill id " + id + " is scheduled on " + date);
    }

    private static void handleListPayment() {
        System.out.println("No. Amount Payment Date State Bill Id");
        List<Payment> payments = paymentService.listPayments();
        for (int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            System.out.println((i + 1) + ". " + String.format("%.0f", p.getAmount()) + " " +
                    p.getPaymentDate() + " " + p.getState() + " " + p.getBillId());
        }
    }

    private static void handleSearch(String[] params) {
        if (params.length < 1) return;
        System.out.println("Bill No. Type Amount Due Date State PROVIDER");
        paymentService.searchByProvider(params[0]).forEach(bill ->
                System.out.println(bill.getId() + ". " + bill.getType() + " " + String.format("%.0f", bill.getAmount()) + " " +
                        bill.getDueDate() + " " + bill.getState() + " " + bill.getProvider()));
    }

    private static void seedData(BillRepository repo) {
        repo.save(new Bill(1L, "ELECTRIC", 200000.0, LocalDate.of(2020, 10, 25), "EVN HCMC"));
        repo.save(new Bill(2L, "WATER", 175000.0, LocalDate.of(2020, 10, 30), "SAVACO HCMC"));
        repo.save(new Bill(3L, "INTERNET", 800000.0, LocalDate.of(2020, 11, 30), "VNPT"));
    }
}