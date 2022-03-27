package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.LastCallBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private AtomicInteger currTime;

	public TimeService(int speed, int duration) {
		super("Timer");
		this.speed = speed;
		this.duration = duration;
		currTime = new AtomicInteger(1);
	}

	@Override
	protected void initialize() {
		while (currTime.intValue() < duration) {
			sendBroadcast(new TickBroadcast(currTime));
			currTime.incrementAndGet();
			try{
				sleep(speed);
			}catch (InterruptedException e){}
		}
		sendBroadcast(new LastCallBroadcast());
		terminate();
	}

}
