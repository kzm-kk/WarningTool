import java.util.ArrayList;

public class Case_Problem4 {
    Case_Problem4_Class[] arrays = new Case_Problem4_Class[3];
    Case_Problem4_Class[] arrays2 = {new Case_Problem4_Class(),
            new Case_Problem4_Class(), new Case_Problem4_Class()};
    ArrayList<Case_Problem4_Class> list = new ArrayList<Case_Problem4_Class>(3);


    void action(){
        arrays[0] = new Case_Problem4_Class();
        arrays[0].getNum();
        arrays2[0] = new Case_Problem4_Class();
        arrays2[0].getNum();
        list.add(new Case_Problem4_Class());
        list.get(0).getNum();
    }
}
