package Windows;

import GraphicsEngine.Matrix3;
import GraphicsEngine.Triangle;
import GraphicsEngine.Vertex;

import java.awt.*;
import java.lang.Runnable;
import java.util.ArrayList;
import java.awt.geom.Path2D;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.io.*;

public class GamePanel extends IntPanel implements Runnable{

    final int FPS = 60;    
    
    Thread gameThread;

    File curMapFile;

    double[] x = new double[2];
    double[] y = new double[2];

    ArrayList<Triangle> tris = new ArrayList<Triangle>();

    public GamePanel(Container parent){
        super(parent);
        
        this.setOpaque(true);
        this.setBackground(Color.BLUE);
        this.setSize(parent.getSize());
        this.setVisible(true);
        this.addMouseMotionListener(new MotionListener());
    }

    public void start(){
        tris = new ArrayList<Triangle>();
        File loadingFile = new File("D:/Nerd Shit/prokects/idkwhattocallthisyet/idkyet/src/OBJMAPTEST/TownTest.obj");
        compileMapFileIntoVert(loadingFile, tris);
        
        startGameThread();
        repaint();}
    
    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();}

  
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
                //repaint();
                delta--;
                //System.out.println("frame");
            }
        }
    }

    ///////////////////

    public void update(){
    
  }

  public void paintComponent(Graphics g){

    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    
    g2.setColor(Color.BLACK);
    g2.fillRect(0, 0, getWidth(), getHeight());

    //loadTetraTest();
    
    double heading = Math.toRadians(x[0]);
    Matrix3 headingTransform = new Matrix3(new double[]{
        Math.cos(heading), 0, -Math.sin(heading),
        0, 1, 0,
        Math.sin(heading), 0, Math.cos(heading)
    });
    double pitch = Math.toRadians(y[0]);
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
    Path2D path = new Path2D.Double();
    path.moveTo(v1.getX(), v1.getY());
    path.lineTo(v2.getX(), v2.getY());
    path.lineTo(v3.getX(), v3.getY());
    path.closePath();
    g2.draw(path);
}
        
    g2.dispose();
  }

  /*public Matrix3 Rotate(ArrayList<Triangle> tris,double[] x, double[] y){

    double heading = Math.toRadians(x[0]);
    Matrix3 headingTransform = new Matrix3(new double[]{
        Math.cos(heading), 0, -Math.sin(heading),
        0, 1, 0,
        Math.sin(heading), 0, Math.cos(heading)
    });
    
    double pitch = Math.toRadians(y[0]);
    Matrix3 pitchTransform = new Matrix3(new double[]{
        1, 0, 0,
        0, Math.cos(pitch), Math.sin(pitch),
        0, -Math.sin(pitch), Math.cos(pitch)
    });
// Merge matrices in advance
   return headingTransform.multiply(pitchTransform);
  }*/

  /**loads input map file into vertices, inputs verticies into the tris list */
  public void compileMapFileIntoVert(File fileIn, ArrayList<Triangle> tris){

    FileReader fr = null;
    try{fr = new FileReader(fileIn);}
    catch(FileNotFoundException e){ e.printStackTrace();}
    
    BufferedReader br = new BufferedReader(fr);
        
    try{
        String curLine;
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
        int i = 0;
        while((curLine = br.readLine()) != null){
            
           // System.out.println(curLine);
            
            if(!curLine.equals("") && (curLine.substring(0,1).equals("v") && curLine.substring(1,2).equals(" "))){
                vertexList.add(vertLineParseHelper01(curLine)); i++;}
            
            if(i == 3){
                //System.out.println(vertexList);
                tris.add(new Triangle(vertexList.get(0), vertexList.get(1), vertexList.get(2), Color.BLUE));
                vertexList.clear(); i = 0;
            }
        }
        System.out.println("comp finished");
    }
 
    catch(IOException e){e.printStackTrace();}
  }

  
  private Vertex vertLineParseHelper01(String curLine){
    //System.out.println(curLine);
        //0 x, 1 y, 2 z
        int[] places = vertLineParseHelper02(curLine);

        //System.out.println(places[0] + "    " + places[1] + "    " + places[2]);
        //System.out.println(curLine.substring(2,places[1]) + "|||" + curLine.substring(places[1]+1,places[2]) + "|||" + curLine.substring(places[2]+1));
                
        float x = Float.parseFloat(curLine.substring(2,places[1]));
        float y = Float.parseFloat(curLine.substring(places[1]+1,places[2]));
        float z = Float.parseFloat(curLine.substring(places[2]+1));
                
        return new Vertex(x, y, z);
  }
  
  
  private int[] vertLineParseHelper02(String str){
    int[] places = new int[3];
    int z = 0;

    for(int i = 1; i < str.length(); i++)
        if(str.substring(i,i+1).equals(" ")){
            places[z] = i; z++;}
    
            return places;
  }
  
  
  
  
  
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
   
  

    private class MotionListener implements MouseMotionListener {
            
        @Override
        public void mouseDragged(MouseEvent e) {
            double yi = 180.0 / getHeight();
            double xi = 180.0 / getWidth();
            x[0] = (int) (e.getX() * xi);
            y[0] = -(int) (e.getY() * yi);
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }
}
