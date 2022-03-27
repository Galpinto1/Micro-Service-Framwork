package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;


/**
 * Student is responsible for sending the {@link //TrainModelEvent},
 * {@link //TestModelEvent} and {@link //PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student my_student;
    private Model currModel;
    private Future<Model> currFuture;


    public StudentService(String name, Student student) {
        super(name);
        my_student = student;
        currModel = null;
        currFuture = null;
    }

    @Override
    protected void initialize() {

    subscribeBroadcast(LastCallBroadcast.class, message -> {
        terminate();
    });

     subscribeBroadcast(PublishConferenceBroadcast.class, message -> {
         for (Model model : message.getGoodModels()) {
             if (model.getStudent() == my_student) {
                 my_student.incPublications();
             }
             else {
                 my_student.incPapersRead();
             }
         }
     });
     subscribeBroadcast(TickBroadcast.class, message -> {
         if(currModel == null) {
             if(my_student.getModels().size() > 0) {
                 currModel = my_student.getModels().poll();
                 currFuture = sendEvent(new TrainModelEvent(currModel));
             }
         }
         else if(currModel.getStatus() == Model.status.Trained) {
             currModel = currFuture.get();
             currFuture = sendEvent(new TestModelEvent(currModel));
         }
         else if (currModel.getStatus() == Model.status.Tested){
             if(currModel.getResults() == Model.results.Good) {
                currModel = currFuture.get();
                sendEvent(new PublishResultEvent(currModel));
                currModel = null;
                currFuture = null;
             }
             else if(currModel.getResults() == Model.results.Bad) {
                 currModel = null;
                 currFuture = null;
             }
         }
     });


}
}

