package pl.kul.onto.modelchange;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author trypuz
 */
public class LEIDataBaseCreation {


    public static RepositoryConnection createDataBase(File directory) throws IOException {

        RepositoryConnection conn = MemoryStoreConnection.getConnection();

        System.out.println("   Getting all files in " + directory.getCanonicalPath());
        List<File> files = (List<File>) FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            String filename = file.getName();
            if (!filename.equals(".DS_Store")) {
                try {
                    InputStream input = new FileInputStream(file);
                    Model model = Rio.parse(input, "", RDFFormat.TURTLE);
                    conn.add(model);
                } catch (IOException | RDFParseException | UnsupportedRDFormatException ex) {
                    Logger.getLogger(LEIDataBaseCreation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return conn;
    }
}
