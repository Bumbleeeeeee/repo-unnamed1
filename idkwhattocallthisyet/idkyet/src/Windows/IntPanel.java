package Windows;

import javax.swing.JLayeredPane;
import java.awt.Container;

public class IntPanel extends JLayeredPane{

    Container parentWindow;

    public IntPanel(Container parent){
        this.parentWindow = parent;

        this.setOpaque(true);
        this.setVisible(true);
    }

  ////////////////////


    /**@return pointer to parentWindow of specified panel*/
    public Container getParentWindow() {return parentWindow;}
}
