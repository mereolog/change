/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.kul.onto.modelchange;

import java.io.FileInputStream;
import java.io.PrintWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author BORO Ontologist
 */
public class MultipleModelBuildingByConstruct extends ModelBuildingByConstruct{
    private static final String constructQueryInputFileName = "change_indexedgraphs.xlsx";
    private static final String replacementTemplate = "<UPDATEDATEVALUE>";
   
    public static RepositoryConnection constructGraphs(RepositoryConnection conn, PrintWriter out, String dirName) throws Exception {
        workbook = WorkbookFactory.create(new FileInputStream(constructQueryInputFileName)); // or sample.xls
        Sheet sheet0 = workbook.getSheetAt(0);
        for (int j = 1; j <= sheet0.getLastRowNum(); j++) {
            Row row = sheet0.getRow(j);
            
            String timeIndexQueryString = row.getCell(1).toString();
            String contextName = row.getCell(3).toString();
            
            TupleQuery timeIndexQuery = conn.prepareTupleQuery(timeIndexQueryString);
                        
            try (TupleQueryResult timeIndices = timeIndexQuery.evaluate()) {
 
            while (timeIndices.hasNext()) {
                BindingSet timeIndex = timeIndices.next();

                String dateString = timeIndex.getValue("updateDate").stringValue();
                
                String constructQueryString = row.getCell(2).toString();

                String appendedConstructQueryString = AddDateToQuery(constructQueryString, dateString);

                context = null;
                context = vf.createIRI(dateString);
                GraphQuery constructQuery = conn.prepareGraphQuery(appendedConstructQueryString);

                try (GraphQueryResult result = constructQuery.evaluate()) {
                while (result.hasNext()) {
                    Statement st = result.next();
                    conn.add(st, context);
                    //System.out.println(st);
                    }
                }
                
                System.out.println("Context: " + context + " has the following number of triples: " + conn.size(context)+"\n");
                               
                }
            
            rowAboutOntology = j;
            QueryMeasure.estimateTime(conn, context, out, dirName, rowAboutOntology, constructQueryInputFileName, 4);
            
 //           try (TupleQueryResult timeIndices2 = timeIndexQuery.evaluate()) {
            
//            while (timeIndices2.hasNext()) {
//                BindingSet timeIndex = timeIndices2.next();
//
//                String dateString = timeIndex.getValue("updateDate").stringValue();
//                
//                QueryMeasure.estimateTime(conn, context, out, dirName, rowAboutOntology);
//                
//                }
//            }
            }
            conn.clear(
                context);           
        }

        return 
            conn;
    }
    
    private static String AddDateToQuery(String queryString, String replacementString)
    {
        String appendedConstructQueryString = queryString.replace(replacementTemplate, replacementString);
        
        return appendedConstructQueryString;
    }
}
