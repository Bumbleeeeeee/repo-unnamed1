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
    
    SettingsMenu menuP;

    public ExtFrame(){
        
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDev = graphics.getDefaultScreenDevice();

        this.setTitle("thisshouldntbeseen:3"); 
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        graphicsDev.setFullScreenWindow(this);

        this.addKeyListener(new PressListener());
    }

    
    public void createMenu(){
        JLayeredPane contentPane = (JLayeredPane)this.getContentPane();
        
        menuP = new SettingsMenu(this);
        contentPane.add(menuP, JLayeredPane.POPUP_LAYER); menuP.setVisible(false);}

    public void flipflopMenu(){menuP.setVisible(!menuP.isVisible());}
    
    
    /**
     * quits both the JFrame and the Program simultaniously 
     * @return prints exit code 0 to console
    */
    public void quitExtFrame(){dispose(); src.exitProgram(0);}

    
    
    
    
    
    
    /////////////////helpers for inputs///////////////////
    
/**handles inputs, is a helper*/
private class PressListener implements KeyListener{
  
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyPressed(KeyEvent e){

        int code = e.getKeyCode();
    
        if(code == KeyEvent.VK_ESCAPE)
            flipflopMenu();
    
        if(code == KeyEvent.VK_W)
            upPressed = true;

        if(code == KeyEvent.VK_S)
            downPressed = true;

        if(code == KeyEvent.VK_A)
            leftPressed = true;

        if(code == KeyEvent.VK_D)
            rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e){

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_ESCAPE)
      
    
        if(code == KeyEvent.VK_W)
            upPressed = false;

        if(code == KeyEvent.VK_S)
            downPressed = false;

        if(code == KeyEvent.VK_A)
            leftPressed = false;

        if(code == KeyEvent.VK_D)
            rightPressed = false;
        }

        @Override
        public void  keyTyped(KeyEvent e){
        }
    }



/**the settings and quit menu*/
private class SettingsMenu extends IntPanel{
    
    JButton exitButton;
    
    public SettingsMenu(Container parent){
        
        super(parent);

        this.setBounds(0,0,400,400);
        makeJButtons();
    }

    //test for now
    public void makeJButtons(){

        exitButton = new JButton("Quit");

        exitButton.setBounds(0,0,200,100);
        exitButton.setVisible(true);

        exitButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e){
                ExtFrame mainWin = (ExtFrame)getParentWindow(); mainWin.quitExtFrame();
            }});
        
            this.add(exitButton);
        }
    }
}


