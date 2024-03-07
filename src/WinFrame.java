import javax.swing.*;

public class WinFrame extends JFrame{
    WinPanel winPanel = new WinPanel();
    WinFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(winPanel);
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);  
    }
}
