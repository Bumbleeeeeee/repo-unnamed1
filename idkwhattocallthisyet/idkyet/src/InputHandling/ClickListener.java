package InputHandling;

import java.awt.event.MouseListener;
import java.awt.event.*;
public class ClickListener implements MouseListener {

    public ClickListener(){}

  public void mousePressed(MouseEvent e) {
    saySomething("Mouse pressed; # of clicks: " + e.getClickCount(), e);
  }

    public void mouseReleased(MouseEvent e) {
      saySomething("Mouse released; # of clicks: " + e.getClickCount(), e);
    }
  

    public void mouseEntered(MouseEvent e) {
       saySomething("Mouse entered", e);
      }
  

    public void mouseExited(MouseEvent e) {
       saySomething("Mouse exited", e);
      }
  

    public void mouseClicked(MouseEvent e) {
    }
  

    void saySomething(String eventDescription, MouseEvent e) {
        System.out.println(eventDescription + " detected on "
                        + e.getComponent().getClass().getName()
                        + ".\n");
    }
}
