package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    //fields
    private int cores;
    private DataBatch db;
    private int ticks;
    private int requiredTicksToProcess;
    private Cluster cluster;


    //constructor
    public CPU(int numOfCores) {
        cores = numOfCores;
        this.db = null;
        ticks = 0;
        requiredTicksToProcess = -1;
        this.cluster = Cluster.getInstance();
    }

    //methods
    public DataBatch getDataBatch(){
        return db;
    }

    public void allocateNewDataBatch(){
    if(cluster.getUnProcessed().size() > 0) {
        Gpu_DataBatch curr = cluster.getUnProcessed().poll();
        if(curr != null && curr.getGpu() != null) {
            cluster.getCpuToGpu().put(this, curr.getGpu());
            db = curr.getDataBatch();
            if (db.getData().getType() == Data.Type.Images) {
                requiredTicksToProcess = ((32 / cores) * 4);
            } else if (db.getData().getType() == Data.Type.Text) {
                requiredTicksToProcess = ((32 / cores) * 2);
            } else if (db.getData().getType() == Data.Type.Tabular) {
                requiredTicksToProcess = ((32 / cores) * 1);
            }
            ticks++;
            curr.getGpu().incTicks();
            cluster.incTotalCpuTime();
        }
    }
}

    public void processData(){
        ticks++;
        cluster.getCpuToGpu().get(this).incTicks();
        cluster.incTotalCpuTime();
        if(ticks == requiredTicksToProcess){
          GPU g = cluster.getCpuToGpu().remove(this);
          cluster.getProcessed().get(g).add(db);
          cluster.incTotalBatchesProcessed();
          ticks = 0;
          db = null;
        }
    }

}
