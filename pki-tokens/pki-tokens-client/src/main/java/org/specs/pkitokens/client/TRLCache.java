package org.specs.pkitokens.client;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.specs.pkitokens.client.exceptions.SynchronizationException;
import org.specs.pkitokens.core.RevocationVerifier;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

public class TRLCache implements RevocationVerifier {
    private static Logger log = Logger.getLogger(TRLCache.class);

    private Thread cacheSyncEngineThread;
    private JerseyClient jerseyClient;
    private String stsAddress;
    private Map<String, Date> trl;
    private boolean syncSuccessful = false;
    private Date syncToDate;

    public TRLCache(String stsAddress, String trustStoreFile, String trustStorePass) {
        this.jerseyClient = new JerseyClient(trustStoreFile, trustStorePass);
        this.stsAddress = stsAddress;

        CacheSyncEngine cacheSyncEngine = new CacheSyncEngine();
        cacheSyncEngineThread = new Thread(cacheSyncEngine);
        cacheSyncEngineThread.start();
        log.debug("TRLCache has started.");
    }

    // TODO: synchronization?
    @Override
    public boolean isRevoked(String tokenId) throws SynchronizationException {
        if (syncSuccessful) {
            return trl.containsKey(tokenId);
        }
        else {
            throw new SynchronizationException("The token revocation list is out-of-date.");
        }
    }

    public Collection<String> getAllRevokedTokens() {
        return trl.keySet();
    }

    public void stop() throws InterruptedException {
        log.debug("TRLCache is stopping...");
        cacheSyncEngineThread.interrupt();
        cacheSyncEngineThread.join();
        log.debug("TRLCache has stopped.");
    }

    private class CacheSyncEngine implements Runnable {

        @Override
        public void run() {
            try {
                initTrlRetry();
                if (log.isTraceEnabled()) {
                    dumpTRL();
                }
                Thread.sleep(10000);

                while (!Thread.currentThread().isInterrupted()) {
                    syncTrlRetry();
                    cleanUpTrl();
                    if (log.isTraceEnabled()) {
                        log.trace("TRL has been synchronized successfully.");
                        dumpTRL();
                    }
                    Thread.sleep(10000); // TODO: configuration param
                }
            }
            catch (InterruptedException e) {
                log.debug("CacheSyncEngine interrupted.");
            }
        }

        private void initTrlRetry() throws InterruptedException {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    initTrl();
                    syncSuccessful = true;
                    log.debug("TRL has been retrieved successfully.");
                    break;
                }
                catch (Exception e) {
                    syncSuccessful = false;
                    log.error("Failed to retrieve token revocation list: " + e.getMessage(), e);
                    Thread.sleep(15000); // TODO: configuration param
                }
            }
        }

        private void initTrl() throws SynchronizationException, JSONException {
            trl = new HashMap<String, Date>();
            Response response = jerseyClient.getClient()
                    .target(stsAddress)
                    .path("/trl")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() != 200) {
                throw new SynchronizationException(String.format("Invalid response code from the STS: %d %s",
                        response.getStatus(), response.getStatusInfo()));
            }

            String trlContent = response.readEntity(String.class);
            JSONObject trlContentJson = new JSONObject(trlContent);
            Date toDate = new Date(trlContentJson.getLong("toDate"));
            JSONArray tokensArray = trlContentJson.getJSONArray("tokens");
            for (int i = 0; i < tokensArray.length(); i++) {
                JSONObject item = tokensArray.getJSONObject(i);
                String tokenId = item.getString("id");
                long expiryTimeLong = item.getLong("exp");
                trl.put(tokenId, new Date(expiryTimeLong));
            }
            syncToDate = toDate;
        }

        private void syncTrlRetry() throws InterruptedException {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    syncTrl();
                    syncSuccessful = true;
                    break;
                }
                catch (Exception e) {
                    syncSuccessful = false;
                    log.error("Failed to synchronized token revocation list: " + e.getMessage(), e);
                    Thread.sleep(15000); // TODO: configuration param
                }
            }
        }

        private void syncTrl() throws SynchronizationException, JSONException {
            Response response = jerseyClient.getClient()
                    .target(stsAddress)
                    .path("/trl")
                    .queryParam("from", syncToDate.getTime())
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() != 200) {
                throw new SynchronizationException(String.format("Invalid response code from the STS: %d %s",
                        response.getStatus(), response.getStatusInfo()));
            }

            String deltaTrlContent = response.readEntity(String.class);
            JSONObject deltaTrlContentJson = new JSONObject(deltaTrlContent);
            Date toDate = new Date(deltaTrlContentJson.getLong("toDate"));
            JSONArray tokensArray = deltaTrlContentJson.getJSONArray("tokens");
            for (int i = 0; i < tokensArray.length(); i++) {
                JSONObject item = tokensArray.getJSONObject(i);
                String tokenId = item.getString("id");
                long expiryTimeLong = item.getLong("exp");
                trl.put(tokenId, new Date(expiryTimeLong));
            }
            syncToDate = toDate;
        }

        private void cleanUpTrl() {
            long now = new Date().getTime();
            Iterator<String> it = trl.keySet().iterator();
            while (it.hasNext()) {
                String tokenId = it.next();
                Date expiryDate = trl.get(tokenId);
                if (expiryDate.getTime() < now) {
                    it.remove();
                }
            }
        }

        private void dumpTRL() {
            log.trace("Revoked tokens: " + trl.keySet().toString());
        }
    }
}
