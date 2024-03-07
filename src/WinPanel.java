import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WinPanel extends JPanel{
    JLabel label = new JLabel(Main.result,SwingConstants.CENTER);
    WinPanel() {
        this.setPreferredSize(new Dimension(1000, 800));
        this.setLayout(null);
        this.setBackground(Color.red);
        this.add(label);
        label.setText(Main.result);
        label.setForeground(Color.yellow);
        label.setFont(new Font("Ariel", Font.BOLD, 100));
        label.setBounds(0,300,1000,200);
    }
}
