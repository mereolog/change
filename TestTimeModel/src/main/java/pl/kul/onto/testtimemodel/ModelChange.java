package pl.kul.onto.testtimemodel;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 *
 * @author trypuz
 */
public class ModelChange {

    private static final String LEIDir = "src/main/resources";
    private static String constructQueryInputFileName = "sparql.xlsx";
    static int queryStringColumnNo = 3;
    static int numberOfQueries = 4;

    public static void main(String[] args) throws IOException, Exception {
        Repository db = new SailRepository(new MemoryStore());

        File directory = new File(LEIDir);
        File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        Arrays.sort(subdirs);
        //BufferedWriter out = new BufferedWriter(new FileWriter("output.csv"));
        try (PrintWriter out = new PrintWriter("sparql-test.csv")) {
            //BufferedWriter out = new BufferedWriter(new FileWriter("output.csv"));
            out.println(
                    "DirectoryNames,Contexts,ContextSizes,QueryNames,QueryTimes");
            for (File dirs : subdirs) {
                Workbook workbook = WorkbookFactory.create(new FileInputStream(constructQueryInputFileName)); // or sample.xls
                Sheet sheet0 = workbook.getSheetAt(0);
                db.initialize();
                try (RepositoryConnection conn = db.getConnection()) {
                    for (int j = 1; j <= sheet0.getLastRowNum(); j++) {
                        Row row = sheet0.getRow(j);
                        String constructQueryString = row.getCell(1).toString();
                        String contextName = row.getCell(2).toString();
                        String ontoName = row.getCell(0).toString();
                        ValueFactory vf = SimpleValueFactory.getInstance();
                        IRI context = vf.createIRI(contextName);

                        LEIModelCreator.createDataBase(dirs, conn);
                        int connSizeGLEIOTriples = (int) conn.size();
                        
                        //ostatni wiersz to statementcontextualization
                        if (j < sheet0.getLastRowNum()) {
                            ModelBuildingByConstruct.constructGraphs(conn, context, constructQueryString);
                        } else {
                            MultiModelBuildingByConstruct.constructMultiContextGraphs(conn, constructQueryString);
                        }
                        for (int k = queryStringColumnNo; k < numberOfQueries + queryStringColumnNo; k++) {
                            String queryName = sheet0.getRow(0).getCell(k).toString();
                            String queryString = row.getCell(k).toString();
                            
                            //ostatni wiersz to statementcontextualization                           
                            if (j < sheet0.getLastRowNum()) {
                                
                                //first pattern is just for starting...
                                if (j > 1)
                                {
                                    out.println(dirs.getName() + "," + context.toString() + "," + conn.size(context) + "," + queryName + "," + QueryMeasure.estimateTime(conn, context, queryString,queryName,dirs.getName()));
                                } 
                            } else {
                                out.println(dirs.getName() + "," + context.toString() + "," + (conn.size() - connSizeGLEIOTriples) + "," + queryName + "," + QueryMeasure.estimateTime(conn, context, queryString,queryName,dirs.getName()));
                            }
                        }
                        conn.clear(context);
                    }
                }
            }
        }
        
    }
}
