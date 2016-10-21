package com.syncleus.ferma;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.AbstractTransaction;
import java.util.function.Consumer;

/**
 * A set of methods that allow for control of transactional behavior of a {@link WrappedFramedGraph} instance. Providers may
 * consider using {@link AbstractTransaction} as a base implementation that provides default features for most of
 * these methods.
 * <p/>
 * It is expected that this interface be implemented by providers in a {@link ThreadLocal} fashion. In other words
 * transactions are bound to the current thread, which means that any graph operation executed by the thread occurs
 * in the context of that transaction and that there may only be one thread executing in a single transaction.
 * <p/>
 * It is important to realize that this class is not a "transaction object".  It is a class that holds transaction
 * related methods thus hiding them from the {@link WrappedFramedGraph} interface.  This object is not meant to be passed around
 * as a transactional context.
 */
public interface WrappedTransaction extends AutoCloseable {

    /**
     * Opens a transaction.
     */
    public void open();

    /**
     * Commits a transaction.
     */
    public void commit();

    /**
     * Rolls back a transaction.
     */
    public void rollback();

    /**
     * Creates a transaction that can be executed across multiple threads. The {@link WrappedFramedGraph} returned from this
     * method is not meant to represent some form of child transaction that can be committed from this object.
     * A threaded transaction is a {@link Graph} instance that has a transaction context that enables multiple
     * threads to collaborate on the same transaction.  A standard transactional context tied to a {@link WrappedFramedGraph}
     * that supports transactions will typically bind a transaction to a single thread via {@link ThreadLocal}.
     */
    public WrappedFramedGraph createThreadedTx();

    /**
     * Determines if a transaction is currently open.
     */
    public boolean isOpen();

    /**
     * An internal function that signals a read or a write has occurred - not meant to be called directly by end users.
     */
    public void readWrite();

    @Override
    public void close();

    /**
     * Adds a listener that is called back with a status when a commit or rollback is successful.  It is expected
     * that listeners be bound to the current thread as is standard for transactions.  Therefore a listener registered
     * in the current thread will not get callback events from a commit or rollback call in a different thread.
     */
    public void addTransactionListener(final Consumer<org.apache.tinkerpop.gremlin.structure.Transaction.Status> listener);

    /**
     * Removes a transaction listener.
     */
    public void removeTransactionListener(final Consumer<org.apache.tinkerpop.gremlin.structure.Transaction.Status> listener);

    /**
     * Removes all transaction listeners.
     */
    public void clearTransactionListeners();
}