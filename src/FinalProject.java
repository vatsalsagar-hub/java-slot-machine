import javax.swing.JOptionPane;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Rebuilt version of an earlier Java Slot Machine course project.
 * This portfolio version adds spin history, a stats screen, and visible payout rules.
 */
public class FinalProject {
    private static final String SAVE_FILE = "slot_machine_account.txt";
    private static final int NUM_REELS = 5;
    private static final String[] SYMBOLS = {
            "CHERRY", "LEMON", "ORANGE", "PLUM", "BELL", "BAR", "SEVEN"
    };

    private static final Random RANDOM = new Random();

    private static class SessionStats {
        int spins;
        int wins;
        int losses;
        double biggestWin;
        final List<String> history = new ArrayList<>();
    }

    public static void main(String[] args) {
        String username = askUsername();
        double balance = loadBalance(username);
        SessionStats stats = new SessionStats();

        if (balance < 0) {
            balance = 10.00;
            show("Welcome, " + username + "!\nA new account was created with $10.00.");
        } else {
            show("Welcome back, " + username + "!\nSaved balance: $" + formatMoney(balance));
        }

        showPayoutRules();

        while (balance > 0) {
            int choice = askMenuChoice(balance);

            if (choice == 2) {
                showStats(stats, balance);
                continue;
            }

            if (choice == 3) {
                showHistory(stats.history);
                continue;
            }

            if (choice == 4) {
                showPayoutRules();
                continue;
            }

            if (choice == 5) {
                saveBalance(username, balance);
                show("Game saved. Goodbye, " + username + "!");
                return;
            }

            Integer bet = askBet(balance);
            if (bet == null) {
                continue;
            }

            balance -= bet;

            String[] reels = spin();
            double winnings = calculateWinnings(reels, bet);
            balance += winnings;

            stats.spins++;
            if (winnings > 0) {
                stats.wins++;
                if (winnings > stats.biggestWin) {
                    stats.biggestWin = winnings;
                }
            } else {
                stats.losses++;
            }

            String historyEntry = "Spin " + stats.spins
                    + ": " + formatReels(reels)
                    + " | Bet $" + formatMoney(bet)
                    + " | Won $" + formatMoney(winnings)
                    + " | Balance $" + formatMoney(balance);
            stats.history.add(historyEntry);

            StringBuilder result = new StringBuilder();
            result.append("Reels:\n");
            result.append(formatReels(reels));
            result.append("\n\nBet: $").append(formatMoney(bet));
            result.append("\nWinnings: $").append(formatMoney(winnings));
            result.append("\nBalance: $").append(formatMoney(balance));

            if (winnings > 0) {
                result.append("\n\nYou won!");
            } else {
                result.append("\n\nNo win this spin.");
            }

            show(result.toString());
            saveBalance(username, balance);

            if (balance <= 0) {
                show("Your balance reached $0.00. Game over!");
                showStats(stats, balance);
                saveBalance(username, 0.00);
                break;
            }
        }
    }

    private static int askMenuChoice(double balance) {
        String[] options = {
                "Spin",
                "Stats",
                "History",
                "Payout Rules",
                "Save & Quit"
        };

        int selected = JOptionPane.showOptionDialog(
                null,
                "Current balance: $" + formatMoney(balance) + "\nChoose an option:",
                "Java Slot Machine",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == JOptionPane.CLOSED_OPTION) {
            return 5;
        }

        return selected + 1;
    }

    private static String askUsername() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    null,
                    "Enter your username:",
                    "Slot Machine Login",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input == null) {
                System.exit(0);
            }

            input = input.trim();
            if (!input.isEmpty()) {
                return input;
            }

            show("Username cannot be blank.");
        }
    }

    private static Integer askBet(double balance) {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    null,
                    "Balance: $" + formatMoney(balance)
                            + "\nEnter bet amount ($1, $2, or $3)."
                            + "\nPress Cancel to return to the menu:",
                    "Place Your Bet",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input == null) {
                return null;
            }

            try {
                int bet = Integer.parseInt(input.trim());

                if (bet < 1 || bet > 3) {
                    show("Bet must be $1, $2, or $3.");
                } else if (bet > balance) {
                    show("You do not have enough money for that bet.");
                } else {
                    return bet;
                }
            } catch (NumberFormatException e) {
                show("Please enter a whole number: 1, 2, or 3.");
            }
        }
    }

    private static String[] spin() {
        String[] reels = new String[NUM_REELS];

        for (int i = 0; i < NUM_REELS; i++) {
            reels[i] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
        }

        return reels;
    }

    /**
     * Portfolio payout rules:
     * - 5 matching symbols = 10x bet
     * - 4 matching symbols = 5x bet
     * - 3 matching symbols = 3x bet
     * - 2 matching symbols = 2x bet
     * - otherwise = 0
     */
    private static double calculateWinnings(String[] reels, int bet) {
        int maxMatches = 1;

        for (int i = 0; i < reels.length; i++) {
            int count = 1;
            for (int j = i + 1; j < reels.length; j++) {
                if (reels[i].equals(reels[j])) {
                    count++;
                }
            }
            if (count > maxMatches) {
                maxMatches = count;
            }
        }

        switch (maxMatches) {
            case 5:
                return bet * 10.0;
            case 4:
                return bet * 5.0;
            case 3:
                return bet * 3.0;
            case 2:
                return bet * 2.0;
            default:
                return 0.0;
        }
    }

    private static void showPayoutRules() {
        show("Payout Rules\n\n"
                + "5 matching symbols = 10x bet\n"
                + "4 matching symbols = 5x bet\n"
                + "3 matching symbols = 3x bet\n"
                + "2 matching symbols = 2x bet\n"
                + "No match = $0.00");
    }

    private static void showStats(SessionStats stats, double balance) {
        double winRate = stats.spins == 0 ? 0.0 : (stats.wins * 100.0) / stats.spins;

        show("Session Stats\n\n"
                + "Spins: " + stats.spins + "\n"
                + "Wins: " + stats.wins + "\n"
                + "Losses: " + stats.losses + "\n"
                + "Win rate: " + String.format("%.1f", winRate) + "%\n"
                + "Biggest win: $" + formatMoney(stats.biggestWin) + "\n"
                + "Current balance: $" + formatMoney(balance));
    }

    private static void showHistory(List<String> history) {
        if (history.isEmpty()) {
            show("No spins have been played in this session yet.");
            return;
        }

        StringBuilder sb = new StringBuilder("Spin History\n\n");
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            sb.append(history.get(i)).append("\n\n");
        }

        show(sb.toString());
    }

    private static String formatReels(String[] reels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reels.length; i++) {
            sb.append("[").append(reels[i]).append("]");
            if (i < reels.length - 1) {
                sb.append("  ");
            }
        }
        return sb.toString();
    }

    private static double loadBalance(String username) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return -1;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(username)) {
                    return Double.parseDouble(parts[1]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            show("Could not read saved account data. A new account will be created.");
        }

        return -1;
    }

    private static void saveBalance(String username, double balance) {
        File file = new File(SAVE_FILE);
        File temp = new File(SAVE_FILE + ".tmp");
        boolean updated = false;

        try (BufferedReader reader = file.exists()
                ? new BufferedReader(new FileReader(file))
                : null;
             PrintWriter writer = new PrintWriter(new FileWriter(temp))) {

            if (reader != null) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2 && parts[0].equalsIgnoreCase(username)) {
                        writer.println(username + "|" + formatMoney(balance));
                        updated = true;
                    } else {
                        writer.println(line);
                    }
                }
            }

            if (!updated) {
                writer.println(username + "|" + formatMoney(balance));
            }
        } catch (IOException e) {
            show("Warning: account data could not be saved.");
            return;
        }

        if (file.exists() && !file.delete()) {
            show("Warning: old save file could not be replaced.");
            return;
        }

        if (!temp.renameTo(file)) {
            show("Warning: save file could not be finalized.");
        }
    }

    private static String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }

    private static void show(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Java Slot Machine",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
