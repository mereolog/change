/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.kul.onto.modelchange;

import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class QueryMeasure {

    private static int numberOfQueries;
    private static final int numberOfQueryRepetition = 5;
    private static final String constructQueryInputFileName = "change.xlsx";
    private static Workbook workbook;

    public static RepositoryConnection estimateTime(RepositoryConnection conn, int rowAboutOntology) throws Exception {
        workbook = WorkbookFactory.create(new FileInputStream(constructQueryInputFileName)); // or sample.xls
        Sheet sheet0 = workbook.getSheetAt(0);
        numberOfQueries = sheet0.getRow(1).getPhysicalNumberOfCells() - 3;
        Row row = sheet0.getRow(rowAboutOntology);
        for (int k = 3; k < numberOfQueries + 3; k++) {
            String testQueryString = row.getCell(k).toString();
            System.out.println("   Query: " + testQueryString);
            long sum = 0;
            for (int i = 1; i <= numberOfQueryRepetition; i++) {
                long startTime = System.nanoTime();
                TupleQuery query = conn.prepareTupleQuery(testQueryString);
                TupleQueryResult result = query.evaluate();
                long endTime = System.nanoTime();

                long estimatedTime = endTime - startTime;
                System.out.println("    estimatedTime: " + estimatedTime);
                sum = sum + estimatedTime;
            }
            long avarage = sum / numberOfQueryRepetition;
            double seconds = (double) avarage / 1000000000;
            System.out.println("    avarage (nono sec): " + avarage + ";    avarage (sec): " + seconds);
            System.out.println();
        }
        //}
        return conn;
    }
}
