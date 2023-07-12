package Windows;

import GraphicsEngine.*;

import java.awt.*;
import java.lang.Runnable;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLayeredPane;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.geom.Path2D;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.io.*;

public class GamePanel extends IntPanel implements Runnable{

    ExtFrame parentWindow = (ExtFrame)this.getParentWindow();
    SettingsMenu menuP;
    
    final int FPS = 144;    
    
    Thread gameThread;
    File curMapFile;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    
    double scale = 1.0;
    double cameraSpeed = 10.0;
    
    int mouseX = 0;
    int mouseY = 0;
    
    float pushX = 0;
    float pushY = 0;
    float pushZ = 0;
    
    ArrayList<Triangle> tris = new ArrayList<Triangle>();

    public GamePanel(Container parent){
        super(parent);
        
        this.setBackground(Color.BLUE);
        this.setSize(parent.getSize());
        this.setFocusable(true);
        
        this.addMouseMotionListener(new MotionListener());
        this.addKeyListener(new PressListener());
    }

//////////////////////////////////////////////    
    
    public void start(){
        tris = new ArrayList<Triangle>();
        //File loadingFile = new File("idkwhattocallthisyet/idkyet/src/OBJMAPTEST/teapottest/Teapot set.obj");
        File loadingFile = new File("idkwhattocallthisyet/idkyet/src/OBJMAPTEST/towntest/TownTest.obj");
        compileMapFile(loadingFile, tris);
        
        startGameThread();
        repaint();}
    
//////////////////////////////////////////////    

        public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();}

//////////////////////////////////////////////

    @Override
    public void run(){
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

//////////////////////////////////////////////

    public void update(){
        if(upPressed){
            scale += 0.05;}
        
        if(downPressed){
            scale -= 0.05;}

        if(rightPressed){
            pushX += cameraSpeed;}

        if(leftPressed){
            pushX -= cameraSpeed;}
  }

//////////////////////////////////////////////

  public void paintComponent(Graphics g){

    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    
    g2.setColor(Color.BLACK);
    g2.fillRect(0, 0, getWidth(), getHeight());

    //loadTetraTest();
    
    double heading = Math.toRadians(mouseX);
    Matrix3 headingTransform = new Matrix3(new double[]{
        Math.cos(heading), 0, -Math.sin(heading),
        0, 1, 0,
        Math.sin(heading), 0, Math.cos(heading)
    });
    double pitch = Math.toRadians(mouseY);
    Matrix3 pitchTransform = new Matrix3(new double[]{
        1, 0, 0,
        0, Math.cos(pitch), Math.sin(pitch),
        0, -Math.sin(pitch), Math.cos(pitch)
    });
// Merge matrices in advance
Matrix3 transform = headingTransform.multiply(pitchTransform);
   
   // The generated shape is centered on the origin (0, 0, 0), and we will do rotation around the origin later.
    g2.translate(getWidth() / 2, getHeight() / 2);
    g2.setColor(Color.WHITE);
    
    for (Triangle t : tris) {
        Vertex v1 = transform.transform(t.getV1());
        Vertex v2 = transform.transform(t.getV2());
        Vertex v3 = transform.transform(t.getV3());

        v1.scaleCoords(scale, pushX, pushY, pushZ); v2.scaleCoords(scale, pushX, pushY, pushZ); v3.scaleCoords(scale, pushX, pushY, pushZ);
        
        Path2D path = new Path2D.Double();
        path.moveTo(v1.getX(), v1.getY());
        path.lineTo(v2.getX(), v2.getY());
        path.lineTo(v3.getX(), v3.getY());
        path.closePath();
        
        g2.draw(path);
    }
        
    g2.dispose();
  }

  //////////////////////////////////////////////

  public void createMenu(){
        JLayeredPane contentPane = (JLayeredPane)parentWindow.getContentPane();
        
        menuP = new SettingsMenu(this);
        contentPane.add(menuP, JLayeredPane.POPUP_LAYER); menuP.setVisible(false);}

    public void flipflopMenu(){menuP.setVisible(!menuP.isVisible());}
    
    

    //////////////////////////////////////////////

  /**loads input map file into vertices, inputs verticies into the tris list */
  public void compileMapFile(File fileIn, ArrayList<Triangle> tris){

    FileReader fr = null;
    try{fr = new FileReader(fileIn);}
    catch(FileNotFoundException e){ e.printStackTrace();}
    
    BufferedReader br = new BufferedReader(fr);
        
    try{
        String curLine;
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
        ArrayList<TextureCoordinate> textureList = new ArrayList<TextureCoordinate>();
        ArrayList<Normal> normalList = new ArrayList<Normal>(); 

        ArrayList<Face> faceList = new ArrayList<Face>();
        
        while((curLine = br.readLine()) != null){
            
            //Adds vertexes to vertexList
            if(!curLine.equals("") && (curLine.substring(0,2).equals("v "))){
                vertexList.add(vertexLineParseHelper(curLine));}
            
            //Adds textures to textureList
            else if(!curLine.equals("") && (curLine.substring(0,2).equals("vt "))){
                textureList.add(textureLineParseHelper(curLine));}
            
            //adds normals to normalList
            else if(!curLine.equals("") && (curLine.substring(0,2).equals("vn "))){
                normalList.add(normalLineParseHelper(curLine));}
            
            //adds faces to faceList
            else if(!curLine.equals("") && (curLine.substring(0,2).equals("f "))){
                faceList.addAll(faceLineParseHelper(curLine));}
        }
        for(Face curFace : faceList){
            
            if(curFace.getVertA() != -1 && curFace.getVertB() != -1 && curFace.getVertC() != -1){
                Vertex vA = vertexList.get(curFace.getVertA()-1);
                Vertex vB = vertexList.get(curFace.getVertB()-1);
                Vertex vC = vertexList.get(curFace.getVertC()-1);

                tris.add(new Triangle(vA,vB,vC,Color.white));
            }
        }
        
        System.out.println(tris.size() + ", " + vertexList.size());
        System.out.println("comp finished");
    }
 
    catch(IOException e){e.printStackTrace();}
  }

  
  private Vertex vertexLineParseHelper(String curLine){
        //0 x, 1 y, 2 z
        ArrayList<Integer> places = spaceParseHelper(curLine);
                
        float x = Float.parseFloat(curLine.substring(2,places.get(1)));
        float y = Float.parseFloat(curLine.substring(places.get(1)+1,places.get(2)));
        float z = Float.parseFloat(curLine.substring(places.get(2)+1));
                
       //edit
        return new Vertex(x*15, y*15, z*15);
  }

  private TextureCoordinate textureLineParseHelper(String curLine){

    ArrayList<Integer> places = spaceParseHelper(curLine);

    float valA = Float.parseFloat(curLine.substring(3,places.get(1)));
    float valB = Float.parseFloat(curLine.substring(places.get(1)+1));

    return new TextureCoordinate(valA, valB);
  }

  private Normal normalLineParseHelper(String curLine){

    ArrayList<Integer> places = spaceParseHelper(curLine);
                
        float valA = Float.parseFloat(curLine.substring(2,places.get(1)));
        float valB = Float.parseFloat(curLine.substring(places.get(1)+1,places.get(2)));
        float valC = Float.parseFloat(curLine.substring(places.get(2)+1));
                
       //edit
        return new Normal(valA, valB, valC);
  }

  private ArrayList<Face> faceLineParseHelper(String curLine){

    ArrayList<Face> out = new ArrayList<Face>();
    
    ArrayList<Integer> slashPlaces = slashParseHelper(curLine);
    ArrayList<Integer> spacePlaces = spaceParseHelper(curLine);

    if(slashPlaces.size() >= 6){
        int curVert = 1;
        //System.out.println(spacePlaces.size());
        for(int i = 0; i <= spacePlaces.size()-3; i++){
            
            int vA = Integer.parseInt(curLine.substring(spacePlaces.get(0)+1, slashPlaces.get(0)));
            int tA = Integer.parseInt(curLine.substring(slashPlaces.get(0)+1, slashPlaces.get(1)));
            int nA = Integer.parseInt(curLine.substring(slashPlaces.get(1)+1, spacePlaces.get(1)));
            
            int vB = Integer.parseInt(curLine.substring(spacePlaces.get(curVert)+1, slashPlaces.get((curVert+1)*2-2)));
            int tB = Integer.parseInt(curLine.substring(slashPlaces.get((curVert+1)*2-2)+1, slashPlaces.get((curVert+1)*2-1)));
            int nB = Integer.parseInt(curLine.substring(slashPlaces.get((curVert+1)*2-1)+1, spacePlaces.get(curVert+1)));
            
            int vC = Integer.parseInt(curLine.substring(spacePlaces.get(curVert+1)+1, slashPlaces.get((curVert+2)*2-2)));;
            int tC = Integer.parseInt(curLine.substring(slashPlaces.get((curVert+2)*2-2)+1, slashPlaces.get((curVert+2)*2-1)));
            int nC;
            

            try{nC = Integer.parseInt(curLine.substring(slashPlaces.get((curVert+2)*2-1)+1, spacePlaces.get(curVert+2)));}
            catch(IndexOutOfBoundsException e){nC = Integer.parseInt(curLine.substring(slashPlaces.get((curVert+2)*2-1)+1));}
            out.add(new Face(vA,tA,nA, vB,tB,nB, vC,tC,nC));
            curVert++;
        }
        
        return out;
    }

    int vA = Integer.parseInt(curLine.substring(spacePlaces.get(0)+1, slashPlaces.get(0)));
    int nA = Integer.parseInt(curLine.substring(slashPlaces.get(0)+1, spacePlaces.get(1)));

    int vB = Integer.parseInt(curLine.substring(spacePlaces.get(1)+1, slashPlaces.get(1)));
    int nB = Integer.parseInt(curLine.substring(slashPlaces.get(1)+1, spacePlaces.get(2)));

    int vC = Integer.parseInt(curLine.substring(spacePlaces.get(2)+1, slashPlaces.get(2)));
    int nC = Integer.parseInt(curLine.substring(slashPlaces.get(2)+1));

    out.add(new Face(vA,nA, vB,nB, vC,nC));
    return out;
  }
  
  
  private ArrayList<Integer> spaceParseHelper(String str){
    ArrayList<Integer> places = new ArrayList<Integer>();
    int z = 0;

    for(int i = 1; i < str.length(); i++)
        if(str.substring(i,i+1).equals(" ")){
            places.add(i); z++;}
    
            return places;
  }

  private ArrayList<Integer> slashParseHelper(String str){
    ArrayList<Integer> places = new ArrayList<Integer>();

    for(int i = 1; i < str.length(); i++)
        if(str.substring(i,i+1).equals("/")){
            places.add(i);}
    
            return places;
  }
  
  
//////////////////////////////////////////////  
  
  
  public void loadTetraTest(){
    
    tris.add(new Triangle(
        new Vertex(100, 100, 100),
        new Vertex(-100, -100, 100),
        new Vertex(-100, 100, -100),
        Color.WHITE));
    tris.add(new Triangle(
        new Vertex(100, 100, 100),
        new Vertex(-100, -100, 100),
        new Vertex(100, -100, -100),
        Color.RED));
    tris.add(new Triangle(
        new Vertex(-100, 100, -100),
        new Vertex(100, -100, -100),
        new Vertex(100, 100, 100),
        Color.GREEN));
    tris.add(new Triangle(
        new Vertex(-100, 100, -100),
        new Vertex(100, -100, -100),
        new Vertex(-100, -100, 100),
        Color.BLUE));
    tris.add(new Triangle(
        new Vertex(300,300,300),
        new Vertex(-300,-300,300),
        new Vertex(-300,300,-300), 
        Color.BLUE));
  }
   
  
////////////////////////////////////////////////////////////////
    /////////////////helpers for inputs///////////////////
    
/**handles inputs, is a helper*/
private class PressListener implements KeyListener{
  

    @Override
    public void keyPressed(KeyEvent e){

        int code = e.getKeyCode();
    
        if(code == KeyEvent.VK_W){
            upPressed = true;}

        if(code == KeyEvent.VK_S){
            downPressed = true;}

        if(code == KeyEvent.VK_A){
            leftPressed = true;}

        if(code == KeyEvent.VK_D){
            rightPressed = true;}
    }

    @Override
    public void keyReleased(KeyEvent e){

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_ESCAPE)
            flipflopMenu();
    
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

//////////////////////////////////////////////  
  
    private class MotionListener implements MouseMotionListener {
            
        @Override
        public void mouseDragged(MouseEvent e) {
            
            //xi and yi are essentially sensativity, 1 being 1:1 relation between mouse movements and rotation
            double yi =  0.25; //180.0 / getHeight();
            double xi = 0.25; //180.0 / getWidth();
            
            mouseX = (int) (e.getX() * xi);
            mouseY = -(int) (e.getY() * yi);
            
            //repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

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

    public void makeJButtons(){

        exitButton = new JButton("Quit");

        exitButton.setBounds(0,0,200,100);
        exitButton.setVisible(true);

        exitButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e){
                ExtFrame mainWin = (ExtFrame)GamePanel.this.parentWindow; mainWin.quitExtFrame();
            }});
        
            this.add(exitButton);
        }
    }
}
