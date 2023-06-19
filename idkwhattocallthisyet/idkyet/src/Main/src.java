package Main;
import javax.swing.JLayeredPane;
import Windows.*;


public class src{
    public static void main(String[] args){
        
        
    //Window Creation//
        ExtFrame mainWin = new ExtFrame();
        JLayeredPane onion = new JLayeredPane();
        mainWin.setContentPane(onion);

        GamePanel gameP = new GamePanel(mainWin);
        mainWin.add(gameP, JLayeredPane.DEFAULT_LAYER);

        mainWin.createMenu();

    //Starts Processes//
        mainWin.setVisible(true);
        gameP.start();
    }

    
    public static void exitProgram(int exitCode){
        System.out.println(exitCode); System.exit(0);
    }
}