package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static class ClusterHolder {
		private static Cluster instance = new Cluster();
	}

	//fields
	private LinkedBlockingQueue<Gpu_DataBatch> unProcessed;
	private ConcurrentHashMap<CPU, GPU> CpuToGpu;
	private ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> processed;

	//statistics fields
	private LinkedList<Model> modelsTrained;
	private AtomicInteger totalBatchesProcessed;
	private AtomicInteger totalCpuTime;
	private AtomicInteger totalGpuTime;


	//constructor
	private Cluster(){
		unProcessed = new LinkedBlockingQueue<>();
		processed = new ConcurrentHashMap<>();
		CpuToGpu = new ConcurrentHashMap<>();
		modelsTrained = new LinkedList<>();
		totalBatchesProcessed = new AtomicInteger(0);
		totalCpuTime = new AtomicInteger(0);
		totalGpuTime = new AtomicInteger(0);

	}

	public static Cluster getInstance() {
		return Cluster.ClusterHolder.instance;
	}

	public LinkedBlockingQueue<Gpu_DataBatch> getUnProcessed(){
		return unProcessed;
	}

	public ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> getProcessed(){
		return processed;
	}

	public ConcurrentHashMap<CPU, GPU> getCpuToGpu(){return CpuToGpu;}

	public void incTotalBatchesProcessed(){
		totalBatchesProcessed.incrementAndGet();
	}

	public void incTotalCpuTime(){
		totalCpuTime.incrementAndGet();
	}

	public void incTotalGpuTime(){
		totalGpuTime.incrementAndGet();
	}

	public LinkedList<Model> getModelsTrained(){
		synchronized (modelsTrained) {
			return modelsTrained;
		}
	}

	public AtomicInteger getTotalBatcesProcessed(){
		return totalBatchesProcessed;
	}

	public AtomicInteger getTotalGpuTime(){
		return totalGpuTime;
	}

	public AtomicInteger getTotalCpuTime(){
		return totalCpuTime;
	}

}

