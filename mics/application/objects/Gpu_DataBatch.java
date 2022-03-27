package bgu.spl.mics.application.objects;

public class Gpu_DataBatch {
    private GPU g;
    private DataBatch db;

    public Gpu_DataBatch(GPU g, DataBatch db) {
        this.g = g;
        this.db = db;
    }

    public GPU getGpu(){
        return g;
    }

    public DataBatch getDataBatch(){
        return db;
    }
}
