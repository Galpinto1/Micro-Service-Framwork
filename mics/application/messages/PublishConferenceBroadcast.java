package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.ConferenceService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class PublishConferenceBroadcast implements Broadcast {
    private LinkedBlockingQueue<Model> goodModels;

    public PublishConferenceBroadcast(LinkedBlockingQueue<Model> models) {
        goodModels = models;
    }

    public LinkedBlockingQueue<Model> getGoodModels() {
        return goodModels;
    }
}
