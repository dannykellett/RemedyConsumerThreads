/*
 * https://communities.bmc.com/message/889052
 */
package RemedyConsumerThreads;

import com.bmc.arsys.api.*;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class App {
    private static final Logger LOG = Logger.getLogger(GenerateData.class);
    private final String BANNER = "Remedy Consumer Threads v1.0 - me@dannykellett.com";
    private static final String FORM = "DummyForm";
    private final BlockingQueue<Entry> queue = new LinkedBlockingDeque<>();
    private final ARServerUser s = new ARServerUser();

    /**
     * If the AR Server has Max Entries Returned By GetList set in the AR System Administration Console,
     * then any one query will only return that amount of entries. E.g. if this is set to 2000 but your query
     * matches 3000, only 2000 will be returned for the first query. Leaving you to execute another, starting from
     * the last value returned. Therefore to check and get all entries, getServerInfo must be called and looped
     * until all entries are received.
     */
    private int getMaxRetrieve(ARServerUser s) throws ARException {
        ServerInfoMap map= s.getServerInfo(new int[] {
                Constants.AR_SERVER_INFO_MAX_ENTRIES});
        Optional<Value> v = Optional.ofNullable(map.get(com.bmc.arsys.api.Constants.AR_SERVER_INFO_MAX_ENTRIES));
        return v.map(Value::getIntValue).orElse(Constants.AR_NO_MAX_LIST_RETRIEVE);
    }

    /**
     * For my test lab, I created a Regular form with no additional fields and used the GenerateData class to
     * fill it with 10000 entries.
     *
     * This method is the code that populates the List which will be consumed by the threads.
     * It has code to include matches that are more than the possibly configured Max Entries
     */
    public List<Entry> getEntries() {
        List<Entry> entriesToProcess = null;
        int server_max_retrieve = 0;
        s.setServer("192.168.1.36");
        s.setUser("Demo");
        s.setPassword("Demo");
        s.setPort(46262);
        try {
            s.login();
            server_max_retrieve = getMaxRetrieve(s);
            QualifierInfo QUALIFIER = s.parseQualification(FORM, "'" + Constants.AR_CORE_STATUS + "' = \"New\"");
            int[] fieldIds = new int[] { Constants.AR_CORE_ENTRY_ID };
            OutputInteger numMatches = new OutputInteger();
            ArrayList<SortInfo> sortOrder = new ArrayList<SortInfo>();
            sortOrder.add(new SortInfo(Constants.AR_CORE_CREATE_DATE, 2));
            entriesToProcess = s.getListEntryObjects(FORM, QUALIFIER, Constants.AR_START_WITH_FIRST_ENTRY,
                    server_max_retrieve, sortOrder, fieldIds, true, numMatches);
            while (entriesToProcess.size() < numMatches.intValue()) {
                entriesToProcess.addAll(s.getListEntryObjects(FORM, QUALIFIER, entriesToProcess.size(),
                        server_max_retrieve, sortOrder, fieldIds, true, numMatches));
            }
        } catch (ARException e) {
            LOG.error(e.toString());
            e.printStackTrace();
        }
        return entriesToProcess == null ? Collections.emptyList() : entriesToProcess;
    }

    /**
     * Now we have a List of Entries, we need to populate the BlockingQueue from the List and then spawn a set
     * of threads to process the list.
     * As this is a proof of concept, I am using all available processors. If this was production code, I would
     * advise not to do that and control that number. Remember to leave processing power for the OS/Other apps etc
     */
    public void spawnThreadsAndProcess() throws InterruptedException {
        queue.addAll(getEntries());
        if (queue.size() > 0) {
            for (int j = 0; j < Runtime.getRuntime().availableProcessors(); j++) {
                new Thread(new PluginsmiUpdate(queue, s)).start();
            }
        }
    }

    /**
     * Main function that puts it all together for this proof of concept.
     *
     */
    public static void main(String[] args) throws InterruptedException {
        // Instantiate the app class
        App a = new App();
        // Log the banner so we know it's started
        LOG.info(a.BANNER);
        // Query the DummyForm for records to process
        LOG.info(a.getEntries().size()+" Entries found to update");
        // Spawn the threads to process/update the status value to Assigned
        a.spawnThreadsAndProcess();
    }
}
