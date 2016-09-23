/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;



/*
*   @author Andrey Petrov
*   @version 1.0.0
*   The class <i>AbsActorSystem</i>  is an abstract entity
*   needed by the framework to manage at abstract level the Actors within the system.
*   The class offers the following interface:
*   <dl>
*       <dt>Public Interface</dt>
*       <dd>
*           <ul>
*               <li>actorOf(actor:Actor, mode: ActorMode)</li>
*               <li>actorOf(actor:Actor)</li>
*               <li>stop(actor:ActorRef)</li>
*               <li>stop()</li>
*           </ul>
*       </dd>
*
*       <dt>Protected Interface</dt>
*       <dd>
*           <ul>
*               <li>getActor(actorRef: ActorRef)</li>
*               <li>createActorReference(mode: ActorMode)</li>
*           </ul>
*       </dd>
*   </dl>
* */
public abstract class AbsActorSystem implements ActorSystem {
    private final int nThreads = Runtime.getRuntime().availableProcessors();
    protected final Map<ActorRef<?>, Actor<?>> actors = new ConcurrentHashMap<>();
    protected final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode) {
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor
            Actor actorInstance = ((AbsActor) actor.newInstance()).setSelf(reference);
            // Associate the reference to the actor
            actors.put(reference, actorInstance);
            executorService.execute((AbsActor) actorInstance);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }

    /*
    *   Pre-condition:  The actor is defined  and the actor system is initialized
    *   Post-condition: If the actor is null the side-effects are no one otherwise
    *                   the method stops the given actor and removes it from the registry of actors
    * */
    public void stop(ActorRef actor) {
            getActor(actor).stop();
            actors.remove(actor);
    }
    
    /*
    *   Pre-condition: The actors reference is defined, in sense that the actors registry containes
    *                 at least one actor.
    *   Post-condition: The method stops all actors into the registry one by one. At the end all actors
    *                   will be stopped.
    * */
    public void stop() {
        for (ActorRef a : actors.keySet())
                stop(a);
    }

    /*
    *    Pre-condition: The actors is an Actor Registry. In containes the set of addresses to which is possible to
    *                   interact with.
    *                   actorRef: ActorRef is defined.
    *    Post-condition: The actor reference is defined && actors contains the actor's address => the method returns
    *                   a valid actor reference. Otherwise the method throws an Exception===NoSuchActorException
    * */
    protected AbsActor getActor(ActorRef actorRef) throws NoSuchActorException {
        if (actors.containsKey(actorRef))
                return (AbsActor) actors.get(actorRef);
        throw new NoSuchActorException();
    }



    protected abstract ActorRef createActorReference(ActorMode mode);
}
