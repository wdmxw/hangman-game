import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class Main {
    private static HangmanGame hangmanGame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            hangmanGame = new HangmanGame();
            JFrame frame = new JFrame("❤️  Hangman Game - Special Edition ❤️");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(hangmanGame);
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
            hangmanGame.requestFocusInWindow();
        });
    }
}

class HangmanGame extends JPanel implements KeyListener {
    private String secretWord = "MYLIU TAVE IEVUTE";
    private Set<Character> guessedLetters = new HashSet<>();
    private Set<Character> correctLetters = new HashSet<>();
    private int wrongGuesses = 0;
    private final int MAX_WRONG = 6;
    private boolean gameWon = false;
    private String lastGuessedLetter = "";

    public HangmanGame() {
        setLayout(null);
        setBackground(new Color(240, 248, 255));
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameWon) return;

        char keyChar = Character.toUpperCase(e.getKeyChar());

        if (keyChar >= 'A' && keyChar <= 'Z') {
            if (guessedLetters.contains(keyChar)) {
                lastGuessedLetter = "Already guessed!";
            } else {
                lastGuessedLetter = "";
                guessLetter(keyChar);
            }
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void guessLetter(char letter) {
        guessedLetters.add(letter);

        if (secretWord.contains(String.valueOf(letter))) {
            correctLetters.add(letter);

            if (isWordComplete()) {
                gameWon = true;
            }
        } else {
            wrongGuesses++;
            if (wrongGuesses >= MAX_WRONG) {
                // Show popup with Lithuanian message and reset
                showBonusLifePopup();
            }
        }
    }

    private void showBonusLifePopup() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                "Kadangi tu labai faina, gauni dar viena gyvybe!",
                "❤️ Bonus Life! ❤️",
                JOptionPane.INFORMATION_MESSAGE
            );
            wrongGuesses = 5; // Reset to 5/6
            repaint();
        });
    }

    private boolean isWordComplete() {
        for (char c : secretWord.toCharArray()) {
            if (c != ' ' && !correctLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private String getDisplayWord() {
        StringBuilder display = new StringBuilder();
        for (char c : secretWord.toCharArray()) {
            if (c == ' ') {
                display.append("  ");
            } else if (correctLetters.contains(c)) {
                display.append(c).append(" ");
            } else {
                display.append("_ ");
            }
        }
        return display.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(new Color(220, 20, 60));
        g2.drawString("❤️  Hangman Game - Special Edition ❤️", 130, 40);

        // Hangman drawing
        drawHangman(g2, 50, 80);

        // Display word
        g2.setFont(new Font("Courier New", Font.BOLD, 36));
        g2.setColor(new Color(0, 100, 200));
        g2.drawString(getDisplayWord(), 350, 180);

        // Instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.BLACK);
        g2.drawString("Type letters on your keyboard to guess", 350, 210);

        // Guessed letters
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.BLACK);
        g2.drawString("Guessed Letters: " + formatGuessedLetters(), 350, 235);

        // Wrong count
        g2.setColor(new Color(220, 20, 60));
        g2.drawString("Wrong Guesses: " + wrongGuesses + "/" + MAX_WRONG, 350, 260);

        // Error message
        if (!lastGuessedLetter.isEmpty()) {
            g2.setColor(new Color(220, 20, 60));
            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            g2.drawString(lastGuessedLetter, 350, 280);
        }

        // Game status
        if (gameWon) {
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.setColor(new Color(0, 180, 0));
            g2.drawString("🎉 YOU WIN! 🎉", 280, 400);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString("The answer is: " + secretWord, 240, 450);
        }
    }

    private String formatGuessedLetters() {
        StringBuilder sb = new StringBuilder();
        for (char c : guessedLetters) {
            sb.append(c).append(" ");
        }
        return sb.toString().isEmpty() ? "None" : sb.toString();
    }

    private void drawHangman(Graphics2D g, int x, int y) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));

        // Post
        g.drawLine(x + 20, y + 20, x + 20, y + 150);
        g.drawLine(x + 20, y + 20, x + 100, y + 20);
        g.drawLine(x + 100, y + 20, x + 100, y + 50);

        // Head
        if (wrongGuesses >= 1) {
            g.fillOval(x + 85, y + 50, 30, 30);
        }

        // Body
        if (wrongGuesses >= 2) {
            g.drawLine(x + 100, y + 80, x + 100, y + 120);
        }

        // Left arm
        if (wrongGuesses >= 3) {
            g.drawLine(x + 100, y + 90, x + 75, y + 110);
        }

        // Right arm
        if (wrongGuesses >= 4) {
            g.drawLine(x + 100, y + 90, x + 125, y + 110);
        }

        // Left leg
        if (wrongGuesses >= 5) {
            g.drawLine(x + 100, y + 120, x + 75, y + 150);
        }

        // Right leg
        if (wrongGuesses >= 6) {
            g.drawLine(x + 100, y + 120, x + 125, y + 150);
        }
    }
}

