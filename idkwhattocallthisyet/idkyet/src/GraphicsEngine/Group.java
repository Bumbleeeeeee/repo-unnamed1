package GraphicsEngine;

public class Group {
    
    String name;
    String usemtl;

    int start;
    int end;

    public Group(String name, String usemtl, int start, int end){
        this.name = name; this.usemtl = usemtl; this.start = start; this.end = end;
    }

    public String getName() {
        return name;
    }

    public String getUsemtl() {
        return usemtl;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }
}
