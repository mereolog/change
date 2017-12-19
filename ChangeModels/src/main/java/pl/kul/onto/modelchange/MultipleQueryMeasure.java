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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class MultipleQueryMeasure {

    private static int numberOfQueries;
    private static final int numberOfQueryRepetition = 30;
    private static final String constructQueryInputFileName = "change_indexedgraphs.xlsx";
    private static Workbook workbook;

    public static RepositoryConnection estimateTime(RepositoryConnection conn, IRI context, PrintWriter out, String dirName, int rowAboutOntology, String replacementTemplate, String dateString) throws Exception {
        workbook = WorkbookFactory.create(new FileInputStream(constructQueryInputFileName)); // or sample.xls
        Sheet sheet0 = workbook.getSheetAt(0);
        
        numberOfQueries = sheet0.getRow(1).getPhysicalNumberOfCells() - 4;
        
        Row row = sheet0.getRow(rowAboutOntology);
        
        for (int k = 4; k < numberOfQueries + 4; k++) {
            String queryString = row.getCell(k).toString();
            queryString = AddDateToQuery(queryString, replacementTemplate, dateString);
            String queryName = sheet0.getRow(0).getCell(k).toString();
            System.out.println("Query: " + queryName);
            //System.out.println("Query: " + queryString);
            long sum = 0;
            int tripleFoundNo = 0;
            for (int i = 1; i <= numberOfQueryRepetition; i++) {
                long startTime = System.nanoTime();
                TupleQuery query = conn.prepareTupleQuery(queryString);
                TupleQueryResult result = query.evaluate();
                long endTime = System.nanoTime();
                
                long estimatedTime = endTime - startTime;
                //System.out.println("    estimatedTime: " + estimatedTime);
                sum = sum + estimatedTime;
                
                out.println(
                    dirName + "," + context.toString() + "," + conn.size(context)+ "," + queryName + "," + estimatedTime);
                
                tripleFoundNo = 0;
                
                while (result.hasNext())
                {
                    tripleFoundNo++;
                    
                    //System.out.println(result.next());
                    result.next();
                    //System.out.println(result.next().getBinding("legalEntity"));
                }
            }
            long average = sum / numberOfQueryRepetition;
            double seconds = (double) average / 1000000000;
            System.out.println("Found " + tripleFoundNo + " triples.");
            
            System.out.println("Query took average (nano sec): " + average + ";    average (sec): " + seconds);
            System.out.println("Repetitions no " + numberOfQueryRepetition);
            System.out.println();
            
            out.flush();
        }
        //}
        return conn;
    }
    
    private static String AddDateToQuery(String queryString, String replacementTemplate, String replacementString)
    {
        String appendedConstructQueryString = queryString.replace(replacementTemplate, replacementString);
        
        return appendedConstructQueryString;
    }
}
