package RemedyConsumerThreads;

import com.bmc.arsys.api.*;
import org.apache.log4j.Logger;

import java.util.UUID;

public class GenerateData {
    private static final Logger LOG = Logger.getLogger(GenerateData.class);
    private static final String FORM = "DummyForm";

    public static void main(String[] args) {
        ARServerUser s = new ARServerUser();
        s.setServer("192.168.1.36");
        s.setUser("Demo");
        s.setPassword("Demo");
        s.setPort(46262);
        try {
            s.login();
            LOG.info(s.getServerVersion() == null ? "Server Version is  null" : s.getServerVersion());
            Entry entry = new Entry();
            entry.put(Constants.AR_CORE_STATUS, new Value("New"));
            entry.put(Constants.AR_CORE_SUBMITTER, new Value("Demo"));
            for (int i = 0; i < 10000; i++) {
                entry.put(Constants.AR_CORE_SHORT_DESCRIPTION, new Value(UUID.randomUUID().toString()));
                LOG.info("Created entry : "+s.createEntry(FORM,entry));
            }
        } catch (ARException e) {
            LOG.error(e.toString());
            e.printStackTrace();
        }
    }

}
