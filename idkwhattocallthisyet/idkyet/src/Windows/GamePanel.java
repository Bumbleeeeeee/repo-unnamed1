package Windows;

import GraphicsEngine.*;

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
        File loadingFile = new File("idkwhattocallthisyet/idkyet/src/OBJMAPTEST/TownTest2.obj");
        compileMapFile(loadingFile, tris);
        
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
                faceList.add(faceLineParseHelper(curLine));}
        }
        for(Face curFace : faceList){
            
            if(curFace.getVertA() != -1 && curFace.getVertB() != -1 && curFace.getVertC() != -1){
                Vertex vA = vertexList.get(curFace.getVertA()-1);
                Vertex vB = vertexList.get(curFace.getVertB()-1);
                Vertex vC = vertexList.get(curFace.getVertC()-1);

                tris.add(new Triangle(vA,vB,vC,Color.white));
            }
        }
        
        
        System.out.println("comp finished");
    }
 
    catch(IOException e){e.printStackTrace();}
  }

  
  private Vertex vertexLineParseHelper(String curLine){
        //0 x, 1 y, 2 z
        int[] places = spaceParseHelper(curLine);
                
        float x = Float.parseFloat(curLine.substring(2,places[1]));
        float y = Float.parseFloat(curLine.substring(places[1]+1,places[2]));
        float z = Float.parseFloat(curLine.substring(places[2]+1));
                
       //edit
        return new Vertex(x*15, y*15, z*15);
  }

  private TextureCoordinate textureLineParseHelper(String curLine){

    int[] places = spaceParseHelper(curLine);

    float valA = Float.parseFloat(curLine.substring(3,places[1]));
    float valB = Float.parseFloat(curLine.substring(places[1]+1));

    return new TextureCoordinate(valA, valB);
  }

  private Normal normalLineParseHelper(String curLine){

    int[] places = spaceParseHelper(curLine);
                
        float valA = Float.parseFloat(curLine.substring(2,places[1]));
        float valB = Float.parseFloat(curLine.substring(places[1]+1,places[2]));
        float valC = Float.parseFloat(curLine.substring(places[2]+1));
                
       //edit
        return new Normal(valA, valB, valC);
  }

  private Face faceLineParseHelper(String curLine){

    ArrayList<Integer> slashPlaces = slashParseHelper(curLine);
    int[] spacePlaces = spaceParseHelper(curLine);

    if(slashPlaces.size() == 6){
        int vA = Integer.parseInt(curLine.substring(spacePlaces[0]+1, slashPlaces.get(0)));
        int tA = Integer.parseInt(curLine.substring(slashPlaces.get(0)+1, slashPlaces.get(1)));
        int nA = Integer.parseInt(curLine.substring(slashPlaces.get(1)+1, spacePlaces[1]));

        int vB = Integer.parseInt(curLine.substring(spacePlaces[1]+1, slashPlaces.get(2)));
        int tB = Integer.parseInt(curLine.substring(slashPlaces.get(2)+1, slashPlaces.get(3)));
        int nB = Integer.parseInt(curLine.substring(slashPlaces.get(3)+1, spacePlaces[2]));

        int vC = Integer.parseInt(curLine.substring(spacePlaces[2]+1, slashPlaces.get(4)));
        int tC = Integer.parseInt(curLine.substring(slashPlaces.get(4)+1, slashPlaces.get(5)));
        int nC = Integer.parseInt(curLine.substring(slashPlaces.get(5)+1));

        return new Face(vA,tA,nA, vB,tB,nB, vC,tC,nC);
    }

    int vA = Integer.parseInt(curLine.substring(spacePlaces[0]+1, slashPlaces.get(0)));
    int nA = Integer.parseInt(curLine.substring(slashPlaces.get(0)+1, spacePlaces[1]));

    int vB = Integer.parseInt(curLine.substring(spacePlaces[1]+1, slashPlaces.get(1)));
    int nB = Integer.parseInt(curLine.substring(slashPlaces.get(1)+1, spacePlaces[2]));

    int vC = Integer.parseInt(curLine.substring(spacePlaces[2]+1, slashPlaces.get(2)));
    int nC = Integer.parseInt(curLine.substring(slashPlaces.get(2)+1));

    return new Face(vA,nA, vB,nB, vC,nC);
  }
  
  
  private int[] spaceParseHelper(String str){
    int[] places = new int[3];
    int z = 0;

    for(int i = 1; i < str.length(); i++)
        if(str.substring(i,i+1).equals(" ")){
            places[z] = i; z++;}
    
            return places;
  }

    private ArrayList<Integer> slashParseHelper(String str){
    ArrayList<Integer> places = new ArrayList<Integer>();

    for(int i = 1; i < str.length(); i++)
        if(str.substring(i,i+1).equals("/")){
            places.add(i);}
    
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
