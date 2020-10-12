package RemedyConsumerThreads;

import com.bmc.arsys.api.*;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class PluginsmiUpdate implements Runnable{
    private static final Logger LOG = Logger.getLogger(PluginsmiUpdate.class);
    private static final String FORM = "DummyForm";
    private final BlockingQueue<Entry> entries;
    private ARServerUser s;

    public PluginsmiUpdate(BlockingQueue<Entry> entries, ARServerUser serverUser) {
        this.entries = entries;
        this.s = serverUser;
    }

    @Override
    public void run() {
        try {
            while (!this.entries.isEmpty()) {
                Entry entry = entries.take();
                entry.put(Constants.AR_CORE_STATUS, new Value("Assigned"));
                LOG.info(Thread.currentThread().getName()+" Updating entry "+entry.getEntryId());
                this.s.setEntry(FORM,entry.getEntryId(),entry,null,Constants.AR_JOIN_SETOPTION_REF);
            }
        } catch (InterruptedException | ARException e) {
            Thread.currentThread().interrupt();
        }
    }
}
