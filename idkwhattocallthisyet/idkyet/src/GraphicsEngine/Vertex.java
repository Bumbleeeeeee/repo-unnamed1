package GraphicsEngine;

public class Vertex {
    
    float x;
    float y;
    float z;
    
    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {return x;}
    public float getY() {return y;}
    public float getZ() {return z;}

    public void scaleCoords(double scale, float pushX, float pushY, float pushZ){
    x *= scale; y *= scale; z *= scale;
    x += pushX; y+= pushY; z += pushZ;}
}
