package pl.kul.onto.modelchange;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 *
 * @author trypuz
 */
public class MemoryStoreConnection {

    public static RepositoryConnection getConnection() {
        db = new SailRepository(new MemoryStore());
        db.initialize();
        conn = db.getConnection();
        return conn;
    }
    
    private static Repository db;
    private static RepositoryConnection conn;
}
