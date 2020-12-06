public class Case_Problem3 {
    private Car repository = new Car();
    //inner/nested クラスの private なメソッドを enclosing クラスから呼び出せなくなったのでエラー
    public Car getAlldata(){
        Car data = new Car();
        data.getName();
        data.getKm();
        return this.repository;
    }
    public void setAlldata(Car value){
        repository = value;
    }
    class Car{
        private int km;
        private String name;
        Car(){
            km = 0;
            name = "";
        }
        public int getKm(){
            return this.km;
        }
        public void setKm(int value){
            this.km = value;
        }
        private String getName(){
            return this.name;
        }
        private void setName(String value){
            this.name = value;
        }
    }
    class Bycycle{
        class Unicycle{

        }
    }
    int tm;
}
