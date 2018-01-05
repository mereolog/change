package pl.kul.onto.testtimemodel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class QueryMeasure {

    private static final int numberOfQueryRepetition = 90;
    private static final int doNotCount = 10;

    public static String estimateTime(RepositoryConnection conn, IRI context, String queryString, String queryName, String dirName) throws Exception {
        //PrintWriter out = new PrintWriter("output-estimatedTime.csv");
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("sparql-estimatedTime.csv", true)));
        DecimalFormat decimalFormat = new DecimalFormat("0.000000");

        //out.println("DirectoryNames,Contexts,ContextSizes,QueryNames,QueryTimes");
        long sum = 0;
        for (int i = 1; i <= numberOfQueryRepetition + doNotCount; i++) {

            long startTime = System.nanoTime();
            TupleQuery query = conn.prepareTupleQuery(queryString);
            TupleQueryResult result = query.evaluate();
            long endTime = System.nanoTime();

            long estimatedTime = endTime - startTime;
            if (i > doNotCount) {
                sum = sum + estimatedTime;
                
                double secondsEstimatedTime = (double) estimatedTime / 1000000000.0;
                String secondNumberAsStringCommaEstimatedTime = decimalFormat.format(secondsEstimatedTime);
                String secondNumberAsStringEstimatedTime = secondNumberAsStringCommaEstimatedTime.replace(',', '.');
                out.println(dirName + "," + context + "," + queryName + "," + secondNumberAsStringEstimatedTime);
            }
        }
        out.close();

        long average = sum / numberOfQueryRepetition;
        double seconds = (double) average / 1000000000.0;
        String secondNumberAsStringComma = decimalFormat.format(seconds);
        String secondNumberAsString = secondNumberAsStringComma.replace(',', '.');

        System.out.println("Query took average (nano sec): " + average + ";    average (sec): " + seconds + ";    average (sec): " + secondNumberAsString);
        System.out.println();
        return secondNumberAsString;
    }
}
