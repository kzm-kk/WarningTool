import java.util.Random;

public class Case_Problem5{
    private int temp = 7;
    public void setTmp(int value){
        this.temp = value;
    }
    public int getTmp(){
        return this.temp;
    }

    private double num = 1.0;
    public void setNum(double value){
        this.num = value;
    }
    public double getNum(){
        return this.num;
    }

    public int rnd(){
        return new Random().nextInt(4);
    }
}
