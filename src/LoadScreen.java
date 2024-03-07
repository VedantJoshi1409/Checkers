import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LoadScreen extends JPanel implements ActionListener, ChangeListener{
    public static int gameType = -1;
    public static int computerDepth = 1;
    public static int theBlackDepth = 1;
    public static int theWhiteDepth = 1;
    public static boolean confirmed = false;
    JButton computer = new JButton("Player VS Computer");
    JButton player = new JButton("Player VS Player");
    JButton showcase = new JButton("Computer VS Computer");
    JButton confirm = new JButton("Confirm");
    JButton loadFile = new JButton("Load File");
    JSlider blackDepth = new JSlider(1,10,1);
    JSlider whiteDepth = new JSlider(1,10,1);
    JSlider depth = new JSlider(1,10,1);
    JLabel showWhite = new JLabel();
    JLabel showBlack = new JLabel();
    JLabel showDepth = new JLabel();
    LoadScreen() {
        this.setPreferredSize(new Dimension(1000, 800));
        this.setLayout(null);
        this.setBackground(Color.lightGray);
        this.add(computer);
        this.add(player);
        this.add(showcase);
        this.add(blackDepth);
        this.add(whiteDepth);
        this.add(depth);
        this.add(showBlack);
        this.add(showWhite);
        this.add(showDepth);
        this.add(confirm);
        this.add(loadFile);
        blackDepth.setPaintTicks(true);
        blackDepth.setMajorTickSpacing(1);
        blackDepth.setBounds(700,300,200,50);
        blackDepth.setPaintLabels(true);
        blackDepth.setBackground(Color.lightGray);
        blackDepth.setFont(new Font("Ariel", Font.PLAIN,15));
        blackDepth.addChangeListener(this);
        
        whiteDepth.setPaintTicks(true);
        whiteDepth.setMajorTickSpacing(1);
        whiteDepth.setBounds(700,200,200,50);
        whiteDepth.setPaintLabels(true);
        whiteDepth.setBackground(Color.lightGray);
        whiteDepth.setFont(new Font("Ariel", Font.PLAIN,15));
        whiteDepth.addChangeListener(this);

        depth.setPaintTicks(true);
        depth.setMajorTickSpacing(1);
        depth.setBounds(100,200,200,50);
        depth.setPaintLabels(true);
        depth.setBackground(Color.lightGray);
        depth.setFont(new Font("Ariel", Font.PLAIN,15));
        depth.addChangeListener(this);

        showBlack.setText("Black Depth = " + blackDepth.getValue());
        showBlack.setBounds(700,250,200,50);
        showBlack.setFont(new Font("Ariel", Font.PLAIN,25));

        showWhite.setText("White Depth = " + blackDepth.getValue());
        showWhite.setBounds(700,150,200,50);
        showWhite.setFont(new Font("Ariel", Font.PLAIN,25));

        showDepth.setText("Depth = " + blackDepth.getValue());
        showDepth.setBounds(100,150,200,50);
        showDepth.setFont(new Font("Ariel", Font.PLAIN,25));

        confirm.setBounds(400,500,200,50);
        confirm.addActionListener(this);
        confirm.setBackground(Color.white);
        loadFile.setBounds(400, 400, 200, 50);
        loadFile.addActionListener(this);
        loadFile.setBackground(Color.white);
        computer.setBounds(100,100, 200, 50);
        computer.addActionListener(this);
        player.setBounds(400,100,200,50);
        player.addActionListener(this);
        showcase.setBounds(700,100,200,50);
        showcase.addActionListener(this);
        computer.setBackground(Color.white);
        player.setBackground(Color.white);
        showcase.setBackground(Color.white);
        computer.addActionListener(this);
        player.addActionListener(this);
        showcase.addActionListener(this);
        depth.setVisible(false);
        showDepth.setVisible(false);
        blackDepth.setVisible(false);
        whiteDepth.setVisible(false);
        showWhite.setVisible(false);
        showBlack.setVisible(false);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == computer) {
            gameType = 0;
            depth.setVisible(true);
            showDepth.setVisible(true);
            blackDepth.setVisible(false);
            whiteDepth.setVisible(false);
            showWhite.setVisible(false);
            showBlack.setVisible(false);
        } else if (e.getSource() == player) {
            gameType = 1;
            depth.setVisible(false);
            showDepth.setVisible(false);
            blackDepth.setVisible(false);
            whiteDepth.setVisible(false);
            showWhite.setVisible(false);
            showBlack.setVisible(false);
        } else if (e.getSource() == showcase) {
            gameType = 2;
            depth.setVisible(false);
            showDepth.setVisible(false);
            blackDepth.setVisible(true);
            whiteDepth.setVisible(true);
            showWhite.setVisible(true);
            showBlack.setVisible(true);
        } else if (e.getSource() == confirm) {
            if (gameType != -1) {
                confirmed = true;
            }
        } else if (e.getSource() == loadFile) {
            String[] extras = Main.extrasFileRead("fileSave.txt");
            gameType = Integer.parseInt(extras[1]);
            Main.theBoard = Main.boardFileRead("fileSave.txt");
            Main.thePlayer = Boolean.parseBoolean(extras[0]);
            computerDepth = Integer.parseInt(extras[2]);
            confirmed = true;
        }
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        showBlack.setText("Black Depth = " + blackDepth.getValue());
        showWhite.setText("White Depth = " + whiteDepth.getValue());
        showDepth.setText("Depth = " + depth.getValue());
        computerDepth = depth.getValue();
        theBlackDepth = blackDepth.getValue();
        theWhiteDepth = whiteDepth.getValue();
    }
}
