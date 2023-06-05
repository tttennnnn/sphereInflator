public class baryCentric {
    Vertex v1, v2, v3;
    baryCentric(Triangle face){
        this.v1 = new Vertex(face.v1.x, face.v1.y, face.v1.z);
        this.v2 = new Vertex(face.v2.x, face.v2.y, face.v2.z);
        this.v3 = new Vertex(face.v3.x, face.v3.y, face.v3.z);
    }
    void translate(double xt, double yt){
        this.v1.x += xt;
        this.v2.x += xt;
        this.v3.x += xt;
        this.v1.y += yt;
        this.v2.y += yt;
        this.v3.y += yt;
    }
    int getminX(){
        return (int) Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x)));
    }
    int getmaxX(){
        return (int) Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x)));
    }
    int getminY(){
        return (int) Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y)));
    }
    int getmaxY(){
        return (int) Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y)));
    }
    static double triArea(double x1, double y1, double x2, double y2, double x3, double y3){
        return (x1*y2 + x2*y3 + x3*y1 - x1*y3 - x2*y1 - x3*y2);
    }
}
