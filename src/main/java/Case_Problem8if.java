public interface Case_Problem8if{
    final static int vals = 1000;
    default int getVals(){
        return vals;
    }
    public void setTmp(int value);
    public int getTmp();
    public void setNum(double value);
    public double getNum();
    public void start();
}
