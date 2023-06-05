public class Matrix {
    double[] mat;
    Matrix(double[] mat) {
        this.mat = mat;
    }
    // res = this.mat * sec
    void mtp3x3(double[] sec){
        double[] res = new double[9];
        for (int r = 0; r < 3; r++){
            for (int c = 0; c < 3; c++){
                for (int i = 0; i < 3; i++){
                    res[3*r + c] += this.mat[3*r + i] * sec[3*i + c];
                }
            }
        }
        this.mat = res;
    }
    // v = this.mat * v
    Vertex transform(Vertex v){
        return new Vertex(this.mat[0] * v.x + this.mat[1] * v.y + this.mat[2] * v.z,
                this.mat[3] * v.x + this.mat[4] * v.y + this.mat[5] * v.z,
                this.mat[6] * v.x + this.mat[7] * v.y + this.mat[8] * v.z);
    }
    static Vertex normal(Vertex v1, Vertex v2, Vertex v3){
        Vertex w1 = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
        Vertex w2 = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
        // w1 cross w2
        Vertex normal = new Vertex(w1.y * w2.z - w1.z * w2.y,
                                   w1.z * w2.x - w1.x * w2.z,
                                   w1.x * w2.y - w1.y * w2.x);
        double norm = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
        normal.x /= norm;
        normal.y /= norm;
        normal.z /= norm;
        return normal;
    }
}
