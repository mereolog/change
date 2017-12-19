package pl.kul.onto.modelchange;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class ModelChange {

    private static RepositoryConnection conn;
    private static final String LEIDir = "resources";

    public static void main(String[] args) throws IOException, Exception {

        File directory = 
            new File(LEIDir);
        
        File[] subdirs = 
            directory.listFiles(
                (FileFilter) DirectoryFileFilter.DIRECTORY);
        
        PrintWriter out = 
            new PrintWriter(
                "outputs.csv");
        
        out.println(
            "DirectoryNames,Contexts,ContextSizes,QueryNames,QueryTimes");
        
        for (File dirs : subdirs) 
        {
            System.out.println("Directory: " + dirs.getName());
            conn = null;
            conn = LEIDataBaseCreation.createDataBase(dirs);
            System.out.println("Number of the orginal LEI triples: "+ conn.size()+"\n");
            ModelBuildingByConstruct.constructGraphs(conn, out, dirs.getName(), true);
            conn.close();
            System.out.println("****************************************************************************************************");
            System.out.println("****************************************************************************************************");
            System.out.println("****************************************************************************************************");
        }
        
        out.close();
    }
}
