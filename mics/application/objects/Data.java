package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
   public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    // constructor
    public Data(Type type, int size) {
        this.type = type;
        processed = 0;
        this.size = size;
    }

    public Type getType(){
        return type;
    }

    public void setType(Type type1){
        type = type1;
    }

    public int getSize(){
        return size;
    }

    public void setSize(int size1){
        size = size1;
    }
}
