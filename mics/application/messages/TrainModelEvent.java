package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import static bgu.spl.mics.application.objects.Model.status.Training;


public class TrainModelEvent implements Event {
    private Model model;

    public TrainModelEvent(Model m) {
        model = m;
        model.setStatus(Training);
    }

    public Model getModel(){
        return model;
    }
}
