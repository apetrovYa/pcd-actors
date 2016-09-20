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
package it.unipd.math.pcd.actors;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T>, Runnable {

    /*
    * Defines an object related with the OuterClass. The class serves as a container
    * over the Message and the Sender. An envelop over data payload. The visibility is
    * limited only internally to the OuterClass.
    * */
    static private class TextMessagePair<T extends Message> {
        private final T message;
        private final ActorRef<T> ref;

        /*
        * @param message: T. This field specs the message payload.
        * @param ref: ActorRef<T>. The ref field specs who is the message receiver.
        */
        public TextMessagePair(T message, ActorRef<T> ref) {
            this.message = message;
            this.ref = ref;
        }

        public T getKey() {
            return message;
        }

        public ActorRef<T> getValue() {
            return ref;
        }
    }

    /*
    * The class contains four internal fields. These have the responsibility to manage the specified 
    * contract. Messages and the stopping actor's behavior. Self and sender fields are the addresses
    * by which we can reference the requested characters into our system.
    *
    **/
    private boolean isStopped = false;
    private final ConcurrentLinkedQueue<TextMessagePair<T>> mailBox = new ConcurrentLinkedQueue<>();
    protected ActorRef<T> self;
    protected ActorRef<T> sender;


    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    } // end setSelf()


    /*
    *   Pre-condition: isStopped = false;
    *   Post-condition: isStopped = true; && all waiting threads are notified.
    * */
    public final void stop() {
        isStopped = true;
        synchronized(mailBox) { mailBox.notifyAll(); }
    } // end stop()

    /*
    * Pre-condition: message && sender are defined.
    * Post-condition: Length(mailBox) = OldLength(mailBox) + 1 &&
    *                 TextMessagePair<T>(message, sender) is a defined object &&
    *                 ok = true;
    *
    *                 Otherwise, ok == false &&
    *                 Length(mailBox) = OldLength(mailBox) &&
    *                 TextMessagePair<T>() is defined object
    * */
    public final boolean pushMessage(T message, ActorRef<T> sender) {
        boolean ok;
        synchronized(mailBox) {
            ok = mailBox.add(new TextMessagePair<T>(message, sender));
            mailBox.notifyAll();
        }
        return ok;
    }// end pushMessage()

    @Override
    public void run( ) {
        boolean stoppingActor = false;
        while(stoppingActor == false) {
            
            TextMessagePair<T> messageToExecute;
            
            synchronized (mailBox)  {
                
                while ( mailBox.isEmpty() ) {
                    stoppingActor = (isStopped) ? true : false;
                    
                    try { mailBox.wait(); }
                    catch (InterruptedException interruptedException) { return; }
                }// end the isEmptyWhile 
            }// end sync

            messageToExecute = mailBox.poll();
            receive(messageToExecute.getKey());
        }// end the external while.
    }// end run()

}