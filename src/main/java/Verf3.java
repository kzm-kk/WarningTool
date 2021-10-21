public class Verf3 {
    private String str;
    private boolean isBool = false;
    private Verf1 verf1;
    private Person person = null;
    public Verf3(){
        person = new Person();
    }
    
    public Person getPerson(){
        return person;
    }
    public void setPerson(Person person){
        this.person = person;
    }
    
    public String getStr(){
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public boolean isBool(){
        return isBool;
    }

    public Verf1 getVerf1(){
        return verf1;
    }
}
