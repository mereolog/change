package pl.kul.onto.modelchange;

import java.io.FileInputStream;
import java.io.PrintWriter;
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
public class ModelBuildingByConstruct {
    static Workbook workbook;
    static final ValueFactory vf = SimpleValueFactory.getInstance();
    static IRI context;
    static int rowAboutOntology;
    
    private static final String constructQueryInputFileName = "change.xlsx";

    public static RepositoryConnection constructGraphs(RepositoryConnection conn, PrintWriter out, String dirName, boolean cleanRepo) throws Exception {
        workbook = WorkbookFactory.create(new FileInputStream(constructQueryInputFileName)); // or sample.xls
        Sheet sheet0 = workbook.getSheetAt(0);
        for (int j = 1; j <= sheet0.getLastRowNum(); j++) {
            Row row = sheet0.getRow(j);
            String constructQueryString = row.getCell(1).toString();
            String contextName = row.getCell(2).toString();
            context = null;
            context = vf.createIRI(contextName);
            //System.out.println("Construct: " + constructQueryString);
            GraphQuery constructQuery = conn.prepareGraphQuery(constructQueryString);
            
            try (GraphQueryResult result = constructQuery.evaluate()) {
                while (result.hasNext()) {
                    Statement st = result.next();
                    conn.add(st, context);
                    //System.out.println(st);
                }
            }
            System.out.println("#################################################################################################");
            System.out.println("Context: " + context + " has the following number of triples: " + conn.size(context)+"\n");
            rowAboutOntology = j;
            QueryMeasure.estimateTime(conn, context, out, dirName, rowAboutOntology, constructQueryInputFileName, 3);
            
            if (cleanRepo)
                conn.clear(
                    context);           
        }

        return 
            conn;
    }
}
