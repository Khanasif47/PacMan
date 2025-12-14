import javax.swing.*;

public class App {

    public static void main(String[] args) {

        // ALWAYS start Swing on EDT
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Pac Man");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            PackMan pacmanGame = new PackMan();
            frame.add(pacmanGame);

            frame.pack();                 // size from JPanel
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);       // ✅ LAST

            pacmanGame.requestFocusInWindow(); // ✅ CORRECT
        });
    }
}
