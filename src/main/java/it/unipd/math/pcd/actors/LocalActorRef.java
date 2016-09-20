/*
* @author Andrey Petrov
* @version 1.0.0
* */
package it.unipd.math.pcd.actors;
import it.unipd.math.pcd.actors.system.ConcreteActorSystem;

/*
* The class implements the ActorRef<T> interface. This class serves as
* a concrete example of an Actor which can be compared and send messages.
* */
public class LocalActorRef<T extends Message> implements ActorRef<T> {

    /*
    * How the global sending message works?
    * First of all it's needed to request the global reference to the ActorSystem.
    * Once the ActorSystem reference is obtained, the reference to the Actor to is needed.
    * The current Thread sends a message from the current context to the Actor to.
    * */
	@Override
	public void send(T message, ActorRef to) {
        ConcreteActorSystem actorSystem = ConcreteActorSystem.getActorSystem();
		AbsActor actor = actorSystem.getActor(to);
		actor.pushMessage(message, this);
	}
		
	@Override
	public int compareTo(ActorRef actorRef) {
		return toString().compareTo(actorRef.toString());
	}
}
