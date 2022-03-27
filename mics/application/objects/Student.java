package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD,None
    }

    private String name;
    private String department;
    private Degree degree;
    private String status;
    private int publications;
    private int papersRead;
    private ConcurrentLinkedQueue<Model> models;

    //constructor
    public Student(String name, String department, Degree degree, ConcurrentLinkedQueue<Model> models) {
        this.name = name;
        this.department = department;
        this.degree = degree;
        this.status = "";
        publications = 0;
        papersRead = 0;
        this.models = models;
    }


    public String getName(){
        return name;
    }

    public void setName(String name1){
        name = name1;
    }

    public String getDepartment(){
        return department;
    }

    public Degree getDegree(){
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public String getStatus(){
        return status;
    }

    public int getPublications(){return publications;}

    public int getPapersRead(){return papersRead;}

    public void incPublications(){
        publications++;
    }

    public void incPapersRead(){
        papersRead++;
    }

    public ConcurrentLinkedQueue<Model> getModels(){
        return models;
    }
}
