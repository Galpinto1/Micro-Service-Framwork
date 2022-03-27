package bgu.spl.mics.application.objects;


import java.util.LinkedList;
import java.util.List;

public class Input {
    private LinkedList<Student> Students;
    private LinkedList<String> GPUS;
    private LinkedList<Integer> CPUS;
    private LinkedList<ConfrenceInformation> Conferences;
    private int TickTime;
    private int Duration;


    public LinkedList<Student> getStudents() {
        return Students;
    }

    public LinkedList<ConfrenceInformation> getConf() {
        return Conferences;
    }

    public LinkedList<String> getGpus() {
        return GPUS;
    }

    public LinkedList<Integer> getCpus() {
        return CPUS;
    }

    public int getTickTime() {
        return TickTime;
    }

    public int getDuration() {
        return Duration;
    }
}