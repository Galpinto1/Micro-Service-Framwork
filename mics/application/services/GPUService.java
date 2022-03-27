package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.LastCallBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * GPU service is responsible for handling the
 * {@link //TrainModelEvent} and {@link //TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU my_gpu;


    public GPUService(String name, GPU g) {
        super(name);
        my_gpu = g;
    }

    @Override
    protected void initialize() {

        subscribeBroadcast(LastCallBroadcast.class, message -> {
            terminate();
        });

        subscribeEvent(TrainModelEvent.class, message -> {
            if(my_gpu.getModel() == null) {
                my_gpu.setModel(message.getModel());
                my_gpu.act();
                if(my_gpu.getModel().getStatus() == Model.status.Trained) {
                    complete(message, my_gpu.getModel());
                    my_gpu.setModel(null);
                }
            }
        });

        subscribeEvent(TestModelEvent.class, message -> {
            if (my_gpu.getModel() == null) {
                my_gpu.setModel(message.getModel());
                my_gpu.testModel();
                complete(message, my_gpu.getModel());
                my_gpu.setModel(null);
            }
        });

        CRMSRunner.CDL.countDown();
    }
}
