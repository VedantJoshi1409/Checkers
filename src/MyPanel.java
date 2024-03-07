import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class MyPanel extends JPanel implements MouseListener, ActionListener {
    JLabel label;
    JButton button;
    JButton fileSave = new JButton("File Save");
    public static int clickPiece = -1;
    public static boolean quit = false;
    MyPanel() {
        this.setPreferredSize(new Dimension(1000, 800));
        this.setLayout(null);
        label = new JLabel();
        label.setBounds(0, 0, 800, 800);
        label.setOpaque(false);
        label.addMouseListener(this);
        this.add(label);
        fileSave.setBounds(850, 150, 100, 50);
        fileSave.setFont(new Font("Ariel", Font.BOLD, 15));
        fileSave.setBackground(Color.white);
        fileSave.addActionListener(this);
        button = new JButton();
        button.setBounds(850, 50, 100, 50);
        button.setFont(new Font("Ariel", Font.BOLD, 20));
        button.setText("Resign");
        button.setForeground(Color.black);
        button.setBackground(Color.white);
        button.addActionListener(this);
        button.setBorderPainted(false);
        if (LoadScreen.gameType != 2) {
            this.add(button);
            this.add(fileSave);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean color = false;
        Graphics2D g2D = (Graphics2D) g;
        g2D.setFont(new Font("Ariel", Font.PLAIN, 50));
        g2D.setPaint(Color.black);
        g2D.fillRect(800, 0, 200, 800);
        for (int i = 0; i < 800; i += 100) {
            for (int j = 0; j < 800; j += 100) {
                if (color) {
                    g2D.setPaint(Color.red);
                } else {
                    g2D.setPaint(Color.white);
                }
                g2D.fillRect(i, j, 100, 100);
                color = !color;
            }
            color = !color;
        }
        g2D.setStroke(new BasicStroke(5));
        for (int k = 0; k < 8; k++) {
            for (int p = 0; p < 8; p++) {
                if (Main.theBoard[k][p] != null) {
                    if (Main.theBoard[k][p].charAt(0) == 'W') {
                        g2D.setPaint(Color.white);
                    } else {
                        g2D.setPaint(Color.black);
                    }
                    g2D.fillOval(p * 100 + 20, k * 100 + 20, 60, 60);
                    if (Main.theBoard[k][p].charAt(1) == 'K') {
                        g2D.setPaint(Color.yellow);
                        g2D.drawString("\uD83D\uDC51", p * 100 + 25, k * 100 + 65);
                    }
                }
            }
        }
        if (Main.highlightMove > -1) {
            g2D.setPaint(Color.yellow);
            g2D.drawOval(Main.highlightMove % 10 * 100 + 20,
                    (Main.highlightMove - Main.highlightMove % 10) / 10 * 100 + 20, 60, 60);
        }
        g2D.setPaint(Color.red);
        g2D.setFont(new Font("Ariel", Font.PLAIN, 20));
        g2D.drawString(String.format("Evaluation: %.2f",Main.theEval), 825, 500);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clickPiece = (e.getY() / 100) * 10 + (e.getX() / 100);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            quit = true;
        } else if (e.getSource() == fileSave) {
            Main.fileSave(Main.theBoard, Main.thePlayer, LoadScreen.gameType, LoadScreen.computerDepth);
            Main.gameFrame.dispose();
        }
    }
}