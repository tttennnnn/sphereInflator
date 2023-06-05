import java.awt.*;

public class renderedObj {
    Triangle[] faces;
    double horAngle = 0, vertAngle = 0;
    int inflatecount = 0;
    renderedObj(Triangle[] faces){
        this.faces = faces;
    }
    void resetFaces(){
        for (Triangle face : this.faces){
            face.resetVertices();
        }
        horAngle = vertAngle = 0;
    }
}
class Triangle {
    Vertex v1, v2, v3;
    private Vertex d1, d2, d3; // default val
    Color color = new Color(211,239,240,255);
    Triangle(Vertex v1, Vertex v2, Vertex v3){
        this.v1 = this.d1 = v1;
        this.v2 = this.d2 = v2;
        this.v3 = this.d3 = v3;
    }
    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color){
        this.v1 = this.d1 = v1;
        this.v2 = this.d2 = v2;
        this.v3 = this.d3 = v3;
        this.color = color;
    }
    void resetVertices(){
        this.v1 = this.d1;
        this.v2 = this.d2;
        this.v3 = this.d3;
    }
    void updateVertices(Vertex v1, Vertex v2, Vertex v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }
}

class Vertex {
    double x, y, z;
    Vertex(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
