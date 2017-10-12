package pl.kul.onto.modelchange;

import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

/**
 *
 * @author trypuz
 */
public class ModelBuldingByConstruct {

    private static final String constructQueryInputFileName = "change.xlsx";
    private static Workbook workbook;

    private static final ValueFactory vf = SimpleValueFactory.getInstance();
    private static IRI context;
    private static int rowAboutOntology;

    public static RepositoryConnection constructGraphs(RepositoryConnection conn) throws Exception {
        workbook = WorkbookFactory.create(new FileInputStream(constructQueryInputFileName)); // or sample.xls
        Sheet sheet0 = workbook.getSheetAt(0);
        for (int j = 1; j <= sheet0.getLastRowNum(); j++) {
            Row row = sheet0.getRow(j);
            String constructQueryString = row.getCell(1).toString();
            String contextName = row.getCell(2).toString();
            context = vf.createIRI(contextName);

            GraphQuery constructQuery = conn.prepareGraphQuery(constructQueryString);

            try (GraphQueryResult result = constructQuery.evaluate()) {
                while (result.hasNext()) {
                    Statement st = result.next();
                    conn.add(st, context);
                }
            }
            System.out.println("   Context: " + context + " has the following number of triples: " + conn.size(context));
            rowAboutOntology = j;
            QueryMeasure.estimateTime(conn,rowAboutOntology);

            // checks the data that is actually in the database
            try (RepositoryResult<Statement> result = conn.getStatements(null, null, context);) {
                while (result.hasNext()) {
                    conn.remove(result, context);
                }
            }
        }
        try (RepositoryResult<Statement> result = conn.getStatements(null, null, null);) {
            while (result.hasNext()) {
                conn.remove(result);
            }
        }
        return conn;
    }
}
