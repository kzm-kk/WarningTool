public class Verf1 {
    private int num;
    private String str;

    class Verf1_Inner {
        private String str;
    }
    
    public void func(){
        Verf1_Inner verf1_inner = new Verf1_Inner();
        verf1_inner.str = "tpo";//ここコンバート後にエラー
        String tmp = verf1_inner.str;
    }

}
