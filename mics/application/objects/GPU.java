package bgu.spl.mics.application.objects;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    //fields
    private Type type;
    private Model model;
    private Cluster cluster;
    private int VramCapacity;
    private int requiredTimeToTrain;
    private int numOfTotalBatches;
    private int numOfProcessedBatches;
    private int numOfTrainedBatches;
    private int ticks;
    private ConcurrentLinkedQueue<DataBatch> Disk;
    private ConcurrentLinkedQueue<DataBatch> VRAM;

    //constructor
    public GPU(Type t) {
        type = t;
        model = null;
        cluster = Cluster.getInstance();
        if (t == Type.RTX3090) {
            VramCapacity = 32;
            requiredTimeToTrain = 1;
        } else if (t == Type.RTX2080) {
            VramCapacity = 16;
            requiredTimeToTrain = 2;
        } else if (t == Type.GTX1080) {
            VramCapacity = 8;
            requiredTimeToTrain = 4;
        }

        numOfTotalBatches = 0;
        numOfProcessedBatches = 0;
        numOfTrainedBatches = 0;
        ticks = 0;
        Disk = new ConcurrentLinkedQueue<>();
        VRAM = new ConcurrentLinkedQueue<>();
    }

    public Model getModel() {return model;}

    public void setModel(Model m) {
        model = m;
    }

    public void incTicks() {
        ticks++;
    }

    public void act(){
        createBatches();
        while(model.getStatus().equals(Model.status.Training)){
            sendDataToProcess();
            takeProcessedDataAndTrain();
            trainData();
        }
    }

    public void createBatches() {
        for (int i = 0; i < model.getData().getSize(); i = i + 1000) {
            Disk.add(new DataBatch(model.getData(), i));
            numOfTotalBatches++;
            cluster.getProcessed().putIfAbsent(this, new LinkedBlockingQueue<>());
        }
    }
    public void sendDataToProcess(){
        int currDiskSize = Disk.size();
        for (int i = 0; i < VramCapacity && i < currDiskSize; i++) {
            DataBatch tosend = Disk.poll();
            if(tosend != null) {
                Gpu_DataBatch g_db = new Gpu_DataBatch(this, tosend);
                cluster.getUnProcessed().add(g_db);
            }
        }
    }

    public void takeProcessedDataAndTrain(){
        for(int i = 0; i < VramCapacity && numOfTotalBatches > numOfProcessedBatches; i++) {
            try {
                VRAM.add(cluster.getProcessed().get(this).take());
                numOfProcessedBatches++;
                trainData();
            } catch(InterruptedException e) {}
        }
    }

    public void trainData(){
        while(VRAM.size() > 0) {
            if(ticks >= requiredTimeToTrain) {
                VRAM.poll();
                ticks = ticks - requiredTimeToTrain;
                numOfTrainedBatches++;
                for (int j = 0; j < requiredTimeToTrain; j++) {
                    cluster.incTotalGpuTime();
                }
            }
        }
        if(numOfTrainedBatches == numOfTotalBatches){
            cluster.getModelsTrained().add(model);
            model.setStatus(Model.status.Trained);
        }
    }

    public void testModel() {
        if (model.getStudent().getDegree() == Student.Degree.MSc) {
            double prob = Math.random();
            if (prob < 0.6) {
                model.setResults(Model.results.Good);
            } else {
                model.setResults(Model.results.Bad);
            }
        }
        else {
            double prob = Math.random();
            if (prob < 0.8) {
                model.setResults(Model.results.Good);
            } else {
                model.setResults(Model.results.Bad);
            }
        }
        model.setStatus(Model.status.Tested);
        cluster.incTotalGpuTime();
    }

}





