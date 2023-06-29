package Windows;
import Main.*;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import java.awt.Container;
import javax.swing.JButton;

public class ExtFrame extends JFrame{

    public ExtFrame(){
        
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDev = graphics.getDefaultScreenDevice();

        this.setTitle("thisshouldntbeseen:3"); 
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        graphicsDev.setFullScreenWindow(this);
        this.setFocusable(false);
    }
    
    /**
     * quits both the JFrame and the Program simultaniously 
     * @return prints exit code 0 to console
    */
    public void quitExtFrame(){dispose(); src.exitProgram(0);}
}


