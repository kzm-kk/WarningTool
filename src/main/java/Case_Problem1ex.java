public class Case_Problem1ex extends Case_Problem1{

    int tmp = 0;
    public void setTmp(int value){
        this.tmp = value;
    }
    public int getTmp(){
        return this.tmp;
    }

    void func(){
        this.setTmp(4);
        int field = this.getTmp();
    }
}
