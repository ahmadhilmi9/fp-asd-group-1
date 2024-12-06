/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - 5026221016 - Arjuna Ahmad Dewangga Aljabbar
 * 2 - 5026221126 -  Nur Ghulam Musthafa Al Kautsar
 * 3 - 5026221147 -  Ahmad Hilmi Dwi Setiawan
 */

package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeScreen extends JFrame {
private static final long serialVersionUID = 1L;
    private final SoundManager soundManager = new SoundManager();

    // Components
    private JTextField usernameField;
    private JButton easyButton, mediumButton, hardButton;

    // Constructor
    public WelcomeScreen() {
        // Set up the frame
        setTitle("Welcome to Sudoku");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK); // Mengatur latar belakang panel menjadi hitam
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Layout untuk ikon dan teks

        // Icon for the title
        ImageIcon icon = new ImageIcon("zombie.png"); // Ganti dengan path ke ikon yang diinginkan
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Ukuran ikon disesuaikan
        icon = new ImageIcon(img);
        JLabel iconLabel = new JLabel(icon);

        // Title label
        JLabel titleLabel = new JLabel("Sudoku Game");
        titleLabel.setFont(new Font("Chiller", Font.BOLD, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.GREEN); // Mengatur warna teks menjadi hijau

        titlePanel.add(iconLabel); // Menambahkan ikon di sebelah kiri
        titlePanel.add(titleLabel); // Menambahkan teks judul di sebelah kanan

        add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1, 10, 10));
        inputPanel.setBackground(Color.BLACK); // Warna latar belakang panel

        // Label untuk username
        JLabel usernameLabel = new JLabel("Enter your username:");
        usernameLabel.setFont(new Font("Chiller", Font.BOLD, 30)); // Font seram
        usernameLabel.setForeground(Color.RED); // Warna teks merah
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(usernameLabel);

        // TextField untuk username
        usernameField = new JTextField();
        usernameField.setFont(new Font("Chiller", Font.BOLD, 50)); // Font teks zombie
        usernameField.setBackground(Color.BLACK); // Warna latar belakang kotak teks
        usernameField.setForeground(Color.GREEN); // Warna teks kotak teks
        usernameField.setCaretColor(Color.RED); // Warna kursor (caret) menjadi merah
        usernameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Border merah
        usernameField.setHorizontalAlignment(SwingConstants.CENTER); // Teks di tengah horizontal
        inputPanel.add(usernameField);

        // Tambahkan panel ke frame
        add(inputPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(Color.DARK_GRAY); // Latar belakang panel tombol gelap

        // Tombol untuk tingkat kesulitan
        easyButton = new JButton("Easy");
        mediumButton = new JButton("Medium");
        hardButton = new JButton("Hard");

        // Mengatur gaya tombol
        easyButton.setFont(new Font("Chiller", Font.BOLD, 24));
        easyButton.setBackground(Color.GREEN); // Tombol hijau untuk "Easy"
        easyButton.setForeground(Color.BLACK);
        easyButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Border merah
        easyButton.setFocusPainted(false);

        mediumButton.setFont(new Font("Chiller", Font.BOLD, 24));
        mediumButton.setBackground(Color.ORANGE); // Tombol oranye untuk "Medium"
        mediumButton.setForeground(Color.BLACK);
        mediumButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        mediumButton.setFocusPainted(false);

        hardButton.setFont(new Font("Chiller", Font.BOLD, 24));
        hardButton.setBackground(Color.RED); // Tombol merah untuk "Hard"
        hardButton.setForeground(Color.BLACK);
        hardButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        hardButton.setFocusPainted(false);

        // Menambahkan tombol ke panel
        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        // Menambahkan panel tombol ke frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Add Action Listeners for difficulty buttons
        easyButton.addActionListener(e -> startGame(1));
        mediumButton.addActionListener(e -> startGame(2));
        hardButton.addActionListener(e -> startGame(3));

        // Menampilkan frame
        setVisible(true);
    }

    // Method to start the game
    private void startGame(int difficulty) {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Close the Welcome Screen
        dispose();

        // Start the Sudoku game with the username and difficulty
        SwingUtilities.invokeLater(() -> new Sudoku(username, difficulty));
    }

    // Main method to launch the welcome screen
    public static void main(String[] args) {
        new WelcomeScreen();
    }
}
