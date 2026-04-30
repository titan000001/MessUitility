package com.messutility.core;

import com.messutility.models.users.Resident;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SettlementEngine {

    public static List<Transaction> calculateSettlement(List<Resident> residents) {
        List<Transaction> transactions = new ArrayList<>();

        // PriorityQueue to always get the largest debtor
        PriorityQueue<ResidentNode> debtors = new PriorityQueue<>(Comparator.comparingDouble(r -> -r.balance));
        // PriorityQueue to always get the largest creditor
        PriorityQueue<ResidentNode> creditors = new PriorityQueue<>(Comparator.comparingDouble(r -> -r.balance));

        for (Resident r : residents) {
            double netBalance = r.getNetBalance();
            if (netBalance < -0.01) {
                // Negative balance means they owe money (Debtor). Store as positive magnitude for queue.
                debtors.add(new ResidentNode(r, Math.abs(netBalance)));
            } else if (netBalance > 0.01) {
                // Positive balance means they are owed money (Creditor).
                creditors.add(new ResidentNode(r, netBalance));
            }
        }

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            ResidentNode debtor = debtors.poll();
            ResidentNode creditor = creditors.poll();

            double amountToSettle = Math.min(debtor.balance, creditor.balance);
            
            // Round to 2 decimal places to avoid floating point issues
            amountToSettle = Math.round(amountToSettle * 100.0) / 100.0;

            if (amountToSettle > 0) {
                transactions.add(new Transaction(debtor.resident, creditor.resident, amountToSettle));
            }

            debtor.balance -= amountToSettle;
            creditor.balance -= amountToSettle;

            // If they still have remaining balance, push them back to the queue
            if (debtor.balance > 0.01) {
                debtors.add(debtor);
            }
            if (creditor.balance > 0.01) {
                creditors.add(creditor);
            }
        }

        return transactions;
    }

    private static class ResidentNode {
        Resident resident;
        double balance;

        ResidentNode(Resident resident, double balance) {
            this.resident = resident;
            this.balance = balance;
        }
    }
}
