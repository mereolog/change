package pl.kul.onto.testtimemodel;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class MultiModelBuildingByConstruct {

    private static final String replacementTemplate = "<UPDATEDATEVALUE>";

    public static RepositoryConnection constructMultiContextGraphs(RepositoryConnection conn, String constructQueryString) throws Exception {

        String timeIndexQueryString = "PREFIX : <http://lei.info/gleio/>\n"
                + "\n"
                + "SELECT DISTINCT ?updateDate \n"
                + "WHERE \n"
                + "{\n"
                + "?legalEntity a :LegalEntity ;\n"
                + ":hasManifestation ?manifestation.\n"
                + "\n"
                + "?manifestation :hasUpdateDate ?updateDate .\n"
                + "}";
        TupleQuery timeIndexQuery = conn.prepareTupleQuery(timeIndexQueryString);
        try (TupleQueryResult timeIndices = timeIndexQuery.evaluate()) {
            while (timeIndices.hasNext()) {
                BindingSet timeIndex = timeIndices.next();  
                String dateString = timeIndex.getValue("updateDate").stringValue();
                String appendedConstructQueryString = constructQueryString.replace(replacementTemplate, dateString);
                ValueFactory vf = SimpleValueFactory.getInstance();
                IRI context = null;
                context = vf.createIRI(dateString);
                GraphQuery constructQuery = conn.prepareGraphQuery(appendedConstructQueryString);

                try (GraphQueryResult result = constructQuery.evaluate()) {
                    while (result.hasNext()) {
                        Statement st = result.next();
                        conn.add(st, context);
                    }
                }                
                System.out.println("Context: " + context + " has the following number of triples: " + conn.size(context) + "\n");
            }
        }

        return conn;
    }
}