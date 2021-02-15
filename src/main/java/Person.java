import org.jetbrains.annotations.NotNull;

public class Person {

    private String name;
    private String[] names = new String[2];
    private Case_Problem3.Car car;
    private int[] nums = new int[2];
    private int age;

    public void sleep(){
        name = "Haru";
        System.out.println("zzz");
    }

    public void func_pos(@NotNull String pos){
        if(pos != null) name = pos;
        int tmp = 5;
        System.out.println("I am " + this.name);
        for(int i = 0; i<1 ; i++){
            System.out.println("loop");
        }
    }

    public void func_null(String pos){
        name = null;
        int tmp = 5;
        System.out.println("I am " + this.name);
        for(int i = 0; i<1 ; i++){
            System.out.println("loop");
        }
    }

    public void sayHi(String whom, int age){
        System.out.println("Hello " + whom);
    }

    public void sampif(){
        if(age == 5) {
            age++;
            System.out.println("age");
        }
    }

    // this method must not be used.

    @Deprecated
    public void xxx(){
    }
}
