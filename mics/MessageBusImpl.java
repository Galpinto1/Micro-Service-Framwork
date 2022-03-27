package bgu.spl.mics;
import bgu.spl.mics.application.messages.LastCallBroadcast;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	//fields
	private ConcurrentHashMap <MicroService, LinkedBlockingQueue<Message>> ms_queue;
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> messages_queue;
	private ConcurrentHashMap<Event, Future> future_queue;

	//constructor
	private MessageBusImpl() {
		ms_queue = new ConcurrentHashMap<>();
		messages_queue = new ConcurrentHashMap<>();
		future_queue = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		messages_queue.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		messages_queue.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		messages_queue.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		messages_queue.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(future_queue.get(e) != null) {
			Future completed = future_queue.get(e);
			completed.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(messages_queue.containsKey(b.getClass())){
			for (MicroService m : messages_queue.get(b.getClass())) {
				LinkedBlockingQueue<Message> Q = ms_queue.get(m);
				if (b instanceof LastCallBroadcast) {
					Q.clear();
					Q.add(b);
				} else {
					Q.add(b);
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if ((e != null && messages_queue.containsKey(e.getClass()) && !messages_queue.get(e.getClass()).isEmpty())) {
			Future<T> f = new Future<>();
			//find the first microservice subscribes to this type of event
			MicroService my_ms = messages_queue.get(e.getClass()).poll();
			//get it back to the end of the queue
			messages_queue.get(e.getClass()).add(my_ms);
			//add the event to the microservice's queue
			ms_queue.get(my_ms).add(e);
			//add the future of this event to the future's queue
			future_queue.put(e, f);
			return f;
		}
		return null;
	}


	@Override
	public void register(MicroService m) {
		ms_queue.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for (Map.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : messages_queue.entrySet()) {
			if (entry.getValue().contains(m)) {
				entry.getValue().remove(m);
			}
		}
		ms_queue.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message first_message = null;
		try {
			  first_message = ms_queue.get(m).take();
		} catch (InterruptedException e) {}
		return first_message;
	}

	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;
	}

}
