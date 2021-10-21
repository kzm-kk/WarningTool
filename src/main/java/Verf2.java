public class Verf2 {
    private final Verf3 verf3;

    public Verf2(){
        verf3 = new Verf3();
    }
    private void func(){
        String str = verf3.getStr();
        Verf1 verf1 = verf3.getVerf1();
        int num = verf3.getPerson().getAge();
        if(verf3.isBool()){
            System.out.println("ok");
        }
    }
}
