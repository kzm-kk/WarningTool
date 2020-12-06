public class Case_Problem8 implements Case_Problem8if{
    
    private int temp = 7;
    @Override
    public void setTmp(int value){
        this.temp = value;
    }
    @Override
    public int getTmp(){
        return this.temp;
    }

    private double num = 1.0;
    @Override
    public void setNum(double value){
        this.num = value;
    }
    @Override
    public double getNum(){
        return this.num;
    }
    
    @Override
    public void start(){
        System.out.println("start");
    }
    
    public void function(int vals){
        int mos = vals;
        mos = this.getVals();
    }
}
