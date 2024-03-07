import javax.swing.*;

public class MyFrame extends JFrame{
    LoadScreen loadScreen;
    MyFrame(){
        loadScreen = new LoadScreen(); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(loadScreen);
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);  
    }
}
