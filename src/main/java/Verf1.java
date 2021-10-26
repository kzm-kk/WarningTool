public class Verf1 {
    private int num;
    private String str;
    private Verf1_Inner2 inners;

    public Verf1(){
        if(num == 1) inners = new Verf1_Inner2_ex("OK");
        else inners = new Verf1_Inner2_ex2("OK2");
        String str2 = inners.mthd();

        str2 = str.toLowerCase();
        if(num != 1) str = "true";
        else str = "false";
        
        
        str = "ok";
        str = "ok2";
        
    }

    class Verf1_Inner {
        private String str;
    }

    abstract class Verf1_Inner2{
        public abstract String mthd();
    }

    class Verf1_Inner2_ex extends Verf1_Inner2{
        private String str;
        
        public Verf1_Inner2_ex(String str){
            this.str = str;
        }
        public String mthd(){
            return this.str;
        }
    }

    class Verf1_Inner2_ex2 extends Verf1_Inner2{
        private String str;

        public Verf1_Inner2_ex2(String str){
            this.str = str;
        }
        public String mthd(){
            return this.str;
        }
    }
    
    public void func(){
        Verf1_Inner verf1_inner = new Verf1_Inner();
        verf1_inner.str = "tpo";//ここコンバート後にエラー
        String tmp = verf1_inner.str.split("")[0];
    }

}
