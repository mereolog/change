package pl.kul.onto.modelchange;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 *
 * @author trypuz
 */
public class ModelChange {

    private static RepositoryConnection conn;
    private static final String LEIDir = "src/main/resources";

    public static void main(String[] args) throws IOException, Exception {

        File directory = new File(LEIDir);
        File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        for (File dirs : subdirs) {
            System.out.println("Directory: " + dirs.getName());
            conn = LEIDataBaseCreation.createDataBase(dirs);
            System.out.println("   Number of the orginal LEI triples: "+ conn.size()+"\n");
            ModelBuldingByConstruct.constructGraphs(conn);
        }
    }
}
