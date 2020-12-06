public class Case_Problem6ex extends Case_Problem6{

    private int tmp = 0;
    public void setTmp(int value){
        this.tmp = value;
    }
    public int getTmp(){
        return this.tmp;
    }

    void func(int tmp, double num){
        super.setTmp(tmp);
        int field = super.getTmp();

        super.setNum(num);
        double fieldD = super.getNum();
    }
}
