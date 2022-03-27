package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.LastCallBroadcast;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link //PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    //fields
    private ConfrenceInformation confi;
    private int ticks;

    public ConferenceService(String name, ConfrenceInformation c) {
        super(name);
        confi = c;
        ticks = 0;
    }

    public int getTicks(){
        return ticks;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(LastCallBroadcast.class, message -> {
            terminate();
        });

        subscribeBroadcast(TickBroadcast.class, message -> {
            ticks++;
            if(ticks == confi.getDate()) {
                sendBroadcast(new PublishConferenceBroadcast(confi.getModels()));
                terminate();
            }
        });

        subscribeEvent(PublishResultEvent.class, message -> {
            confi.getModels().add(message.getModel());
            this.complete(message, true);
        });


        CRMSRunner.CDL.countDown();
    }

}
