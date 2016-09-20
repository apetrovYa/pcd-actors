/*
 * @author Andrey Petrov
 * @version 1.0.0
 * */
package it.unipd.math.pcd.actors.system;

import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.LocalActorRef;

/*
*  Extends the AbsActorSystem  and implements the createActorReference
*
*  @author Andrey Petrov
*  @version 1.0.0
*
* */
public final class ConcreteActorSystem extends AbsActorSystem {
	private static ConcreteActorSystem actorSystem;
	
	public ConcreteActorSystem() { }
	/*
	* Pre-condition: The formal parameter set is empty && the actorSystem reference may be defined or undefined reference.
	* Post-condition: The method returns a defined reference to the ConcreteActorSystem instance.
	* */
	public static synchronized ConcreteActorSystem getActorSystem() {
		if (actorSystem == null)
			actorSystem = new ConcreteActorSystem();
		return actorSystem;
	}

	/*
	* Pre-condition: The mode:ActorMode is defined.
	* Post-condition: If mode == ActorMode.LOCAL the method returns a new LocalActorRef() object; otherwise the method
	* 				  throws an IllegalArgumentException object.
	* 
	* @param mode: ActorMode. An Actor can be of two types: LOCAL or REMOTE. 
	*				          The mode defines dynamic actor creation based on the type. 
	* */
	@Override
	protected ActorRef createActorReference(ActorMode mode) throws IllegalArgumentException {
		if(mode == null) mode = ActorMode.LOCAL;
		if (mode == ActorMode.LOCAL)
			return new LocalActorRef();
		throw new IllegalArgumentException();
	}
}
