package GraphicsEngine;

public class Face {
    
    //Vertices of face
    int vertA;
    int vertB;
    int vertC;

    //textures of face
    int textureA;
    int textureB;
    int textureC;


    //normals of face
    int normalA;
    int normalB;
    int normalC;

    public Face(int vertA, int textureA, int normalA, int vertB, int textureB, int normalB, int vertC, int textureC, int normalC){
        this.vertA = vertA; this.textureA = textureA; this.normalA = normalA;
        this.vertB = vertB; this.textureB = textureB; this.normalB = normalB;
        this.vertC = vertC; this.textureC = textureC; this.normalC = normalC;
    }

    public Face(int vertA, int normalA, int vertB, int normalB, int vertC, int normalC){
        this.vertA = vertA; this.normalA = normalA;
        this.vertB = vertB; this.normalB = normalB;
        this.vertC = vertC; this.normalC = normalC;
    }

    public int getVertA(){return vertA;} public int getTextureA(){return textureA;} public int getNormalA(){return normalA;}
    public int getVertB(){return vertB;} public int getTextureB(){return textureB;} public int getNormalB(){return normalB;}
    public int getVertC(){return vertC;} public int getTextureC(){return textureC;} public int getNormalC(){return normalC;}
}
