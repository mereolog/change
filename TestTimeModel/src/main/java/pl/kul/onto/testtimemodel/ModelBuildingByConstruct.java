package pl.kul.onto.testtimemodel;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class ModelBuildingByConstruct {

    public static RepositoryConnection constructGraphs(RepositoryConnection conn, IRI context, String constructQueryString) throws Exception {

            GraphQuery constructQuery = conn.prepareGraphQuery(constructQueryString);

            try (GraphQueryResult result = constructQuery.evaluate()) {
                while (result.hasNext()) {
                    Statement st = result.next();
                    conn.add(st, context);
                    //System.out.println(st);
                }
            }
            //System.out.println("#################################################################################################");
            System.out.println("Context: " + context + " has the following number of triples: " + conn.size(context) + "\n");
        
        return conn;
    }
}
