package cz.incad.kramerius.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import cz.incad.kramerius.service.MigrateSolrIndexException;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.StringUtils;
import cz.incad.kramerius.utils.XMLUtils;
import cz.incad.kramerius.utils.XMLUtils.ElementsFilter;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class MigrationUtils {

    private static final String DEST_SOLR_HOST = ".dest.solrHost";
    private static final String SOLR_MIGRATION_QUERY_KEY = ".migration.solr.query";
    private static final String SOLR_MIGRATION_ROWS_KEY = ".migration.solr.rows";
    
    private static final String SOLR_MIGRATION_THREAD_KEY = ".migration.threads";
    private static final String SOLR_MIGRATION_BATCHSIZE_KEY = ".migration.solr.batchsize";
    

    private static final String SOLR_MIGRATION_BUIDLD_COMPOSITE =".migration.build.composite";
    
    
    public static final String DEFAULT_QEURY = "*:*";
    public static final int DEFAULT_NUMBER_OF_ROWS = 100;
    public static final int DEFAULT_NUMBER_OF_THREADS = 4;
    public static final int DEFAULT_BATCHSIZE = 10;
    public static final int START = 0;
    
    public static final Logger LOGGER = Logger.getLogger(MigrationUtils.class.getName());
    
    private MigrationUtils() {
    }

    
    public static void sendToDest(Client client, Document batchDoc)  throws MigrateSolrIndexException {
        try {
            StringWriter writer = new StringWriter();
            String destSolr = confiugredDestinationServer();
            WebResource r = client.resource(destSolr);
            
            XMLUtils.print(batchDoc, writer);
            ClientResponse resp = r.accept(MediaType.TEXT_XML).type(MediaType.TEXT_XML).entity(writer.toString(), MediaType.TEXT_XML).post(ClientResponse.class);
            if (resp.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                ByteArrayOutputStream bos  = new ByteArrayOutputStream();
                InputStream entityInputStream = resp.getEntityInputStream();
                IOUtils.copyStreams(entityInputStream, bos);
                LOGGER.log(Level.SEVERE, new String(bos.toByteArray()));
                StringWriter batch = new StringWriter();
                XMLUtils.print(batchDoc, batch);
                LOGGER.log(Level.SEVERE, "critical batch is " + batch.toString());
            }
        } catch (UniformInterfaceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MigrateSolrIndexException(e);
        } catch (ClientHandlerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MigrateSolrIndexException(e);
        } catch (TransformerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MigrateSolrIndexException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MigrateSolrIndexException(e);
        }
    }

    
    public static void commit(Client client, String destServer) throws MigrateSolrIndexException {
        try {
            String destSolr = destServer+"?commit=true";
            WebResource r = client.resource(destSolr);
            Document document = XMLUtils.crateDocument("add");
            StringWriter strWriter = new StringWriter();
            XMLUtils.print(document, strWriter);
            String t = r.accept(MediaType.TEXT_XML).type(MediaType.TEXT_XML).entity(strWriter.toString(), MediaType.TEXT_XML).post(String.class);
        } catch (UniformInterfaceException e) {
            throw new MigrateSolrIndexException(e);
        } catch (ClientHandlerException e) {
            throw new MigrateSolrIndexException(e);
        } catch (ParserConfigurationException e) {
            throw new MigrateSolrIndexException(e);
        } catch (TransformerException e) {
            throw new MigrateSolrIndexException(e);
        }
    }


    /**
     * REturns configured destination server
     * @return
     * @throws MigrateSolrIndexException
     */
    public static int configuredRowsSize() throws MigrateSolrIndexException {
        int rows = KConfiguration.getInstance().getConfiguration().getInt(SOLR_MIGRATION_ROWS_KEY,DEFAULT_NUMBER_OF_ROWS);
        int batchSize = KConfiguration.getInstance().getConfiguration().getInt(SOLR_MIGRATION_BATCHSIZE_KEY,DEFAULT_BATCHSIZE);
        if (rows < batchSize) {
            throw new MigrateSolrIndexException("Illegal state. Property '.migration.solr.rows' is smaller than property  '.migration.solr.batchsize'");
        }
        return  rows;
    }

    
    /**
     * REturns configured destination server
     * @return
     * @throws MigrateSolrIndexException
     */
    public static int configuredBatchSize() throws MigrateSolrIndexException {
        int batchSize = KConfiguration.getInstance().getConfiguration().getInt(SOLR_MIGRATION_BATCHSIZE_KEY,DEFAULT_BATCHSIZE);
        int rows = KConfiguration.getInstance().getConfiguration().getInt(SOLR_MIGRATION_ROWS_KEY,DEFAULT_NUMBER_OF_ROWS);
        if (rows < batchSize) {
            throw new MigrateSolrIndexException("Illegal state. Property '.migration.solr.rows' is smaller than property  '.migration.solr.batchsize'");
        }
        return batchSize;
    }
    
    
    /**
     * REturns configured destination server
     * @return
     * @throws MigrateSolrIndexException
     */
    public static int configuredNumberOfThreads() throws MigrateSolrIndexException {
        return  KConfiguration.getInstance().getConfiguration().getInt(SOLR_MIGRATION_THREAD_KEY,DEFAULT_NUMBER_OF_THREADS);
    }

    
    public static boolean configuredBuildCompositeId() throws MigrateSolrIndexException {
        return  KConfiguration.getInstance().getConfiguration().getBoolean(SOLR_MIGRATION_BUIDLD_COMPOSITE,false);
    }
    
    
    /**
     * REturns configured destination server
     * @return
     * @throws MigrateSolrIndexException
     */
    public static String confiugredDestinationServer() throws MigrateSolrIndexException {
        String targetDest = KConfiguration.getInstance().getConfiguration().getString(DEST_SOLR_HOST);
        String source = KConfiguration.getInstance().getSolrHost();
        if (targetDest == null || !StringUtils.isAnyString(targetDest)) {
            throw new MigrateSolrIndexException(String.format("missing property %s", DEST_SOLR_HOST));
        }
        if (targetDest.startsWith(source)) {
            throw new MigrateSolrIndexException(String.format("the same index problem %s %s", targetDest,source));
        }
        return targetDest;
    }
    
    /**
     * Returns source server
     * @return
     * @throws MigrateSolrIndexException
     */
    public static String configuredSourceServer() throws MigrateSolrIndexException {
        String targetDest = KConfiguration.getInstance().getSolrHost();
        return targetDest;
    }
    
    /**
     * Returns configured query; it could redefined by setting property '.migration.solr.query'
     * @return
     * @throws MigrateSolrIndexException
     */
    public static String configuredMigrationQuery() throws MigrateSolrIndexException {
        try {
            String query = KConfiguration.getInstance().getConfiguration().getString(SOLR_MIGRATION_QUERY_KEY, DEFAULT_QEURY);
            return "select?q="+URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MigrateSolrIndexException(e.getMessage());
        }
    }

    
    /**
     * Return url for for quering
     * @return
     * @throws MigrateSolrIndexException 
     */
    public static String constructedQueryURL() throws MigrateSolrIndexException {
        String solrQuery = KConfiguration.getInstance().getSolrHost();
        solrQuery += (solrQuery.endsWith("/") ? "" : "/")+ configuredMigrationQuery();
        return solrQuery;
    }
    
    

    /**
     * Sends request to solr 
     * @param client
     * @param solrQuery
     * @param rows
     * @param cursor
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws MigrateSolrIndexException
     */
    public static Element querySolr(Client client, String solrQuery, int rows, int cursor)
            throws ParserConfigurationException, SAXException, IOException, MigrateSolrIndexException {
        String formatted = solrQuery+String.format("&rows=%d&start=%d",rows, cursor);
        SolrWorker.LOGGER.info(String.format("processing %s",formatted));
        
        WebResource r = client.resource(formatted);
        String t = r.accept(MediaType.APPLICATION_XML).get(String.class);
        Document parseDocument = XMLUtils.parseDocument(new StringReader(t));
        Element result = XMLUtils.findElement(parseDocument.getDocumentElement(), new XMLUtils.ElementsFilter() {
            
            @Override
            public boolean acceptElement(Element element) {
                String nodeName = element.getNodeName();
                return nodeName.equals("result");
           }
        });
        
        return result;
    }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, MigrateSolrIndexException, TransformerException {
        Client client = Client.create();

        String solrQuery = constructedQueryURL();
        System.out.println(solrQuery);
    //        Element sourceRequest = MigrationUtils.querySolr(client, solrQuery, 0,0);
//        XMLUtils.print(sourceRequest, System.out);
        
    }

}
