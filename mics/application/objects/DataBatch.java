package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    //fields
  private Data data;
  private int start_index;
  private boolean processed;


  //constructor
    public DataBatch(Data data, int index) {
        this.data = data;
        start_index = index;
        processed = false;
    }

    public Data getData(){
        return data;
    }

}
