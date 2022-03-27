package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum status {
        PreTrained, Training, Trained,Tested
    }

    public enum results{
        None, Good, Bad
    }

    private String name;
    private int size;
    private String type;
    private Data data;
    private Student student;
    private status status;
    private results results;

    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        size = 0;
        type = "";
        this.student = student;
        this.status = status.PreTrained;
        this.results = results.None;
    }

    public String getName(){
        return name;
    }

    public void setName(String name1){
        name=name1;
    }

    public int getSize(){
        return size;
    }

    public String getType(){
        return type;
    }

    public void setType(String type1){
        type = type1;
    }

    public Data getData(){
        return data;
    }

    public synchronized void setData(Data data1){
        data=data1;
    }

    public Student getStudent(){
        return student;
    }

    public synchronized void setStudent(Student student1){
        student = student1;
    }

    public status getStatus(){
        return status;
    }

    public synchronized void setStatus(status s) {
        this.status = s;
    }

    public results getResults(){
        return results;
    }

    public synchronized void setResults(results r){results = r;}







}