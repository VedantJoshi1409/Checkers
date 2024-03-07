import javax.swing.JFrame;

public class GameFrame extends JFrame{ 
    MyPanel panel = new MyPanel();
    GameFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);  
    }
}
