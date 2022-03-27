package bgu.spl.mics.application.objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedBlockingQueue<Model> models;

    public ConfrenceInformation(String name, int date){
        this.name=name;
        this.date=date;
        models = new LinkedBlockingQueue<>();
    }

    public String getName(){
        return name;
    }

    public void setName(String name1){
        name = name1;
    }

    public int getDate(){
       return date;
    }

    public void setDate(int date1){
        date = date1;
    }

    public LinkedBlockingQueue<Model> getModels(){
        return models;
    }

    public void setModels(LinkedBlockingQueue<Model> Q) {
        models = Q;
    }

}


