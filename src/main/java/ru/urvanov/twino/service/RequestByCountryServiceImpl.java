package ru.urvanov.twino.service;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class RequestByCountryServiceImpl implements RequestsByCountryService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RequestByCountryServiceImpl.class);

    private static final String DEFAULT_COUNTRY_CODE = "LV";

    private ConcurrentMap<String, Long> countryHits = new ConcurrentHashMap<>();

    private static final Long MAX_HITS_PER_SECOND = 10L;

    private volatile Long lastClearHitsMillis = 0L;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private ObjectReader objectReader = new ObjectMapper().reader();

    private LoadingCache<String, String> ipToCountryCache;

    public RequestByCountryServiceImpl() {

        ipToCountryCache = CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {

                    @Override
                    public String load(String ip) throws Exception {
                        CloseableHttpClient httpClient = HttpClients
                                .createDefault();
                        HttpGet httpGet = new HttpGet(
                                "http://ipinfo.io/" + ip + "/json");

                        try (CloseableHttpResponse response = httpClient
                                .execute(httpGet)) {
                            HttpEntity entity = response.getEntity();
                            long contentLength = entity.getContentLength();
                            if (contentLength > 1000000)
                                throw new IllegalStateException(
                                        "content-length is too big.");
                            byte[] buff = new byte[(int) contentLength];
                            try (InputStream inputStream = entity
                                    .getContent()) {
                                int pos = 0;
                                int bytesReaded = 0;
                                while ((bytesReaded = inputStream.read(buff,
                                        pos, buff.length - pos)) != -1) {
                                    pos += bytesReaded;
                                }
                            }
                            String responseString = new String(buff);

                            EntityUtils.consume(entity);

                            JsonNode jsonNode = objectReader
                                    .readTree(responseString);
                            JsonNode countryNode = jsonNode.get("country");
                            String country = countryNode == null
                                    ? DEFAULT_COUNTRY_CODE
                                    : countryNode.textValue();
                            LOGGER.debug("requestContry={}", country);
                            return country;
                        }
                    }

                });
    }

    public boolean checkHitsPerSecond(String ip) {
        if (System.currentTimeMillis() > lastClearHitsMillis + 1_000L) {
            if (reentrantLock.tryLock()) {
                try {
                    // Another thread could clear map while we trying to
                    // own the lock.
                    if (System.currentTimeMillis() > lastClearHitsMillis + 1_000L) {
                        countryHits.clear();
                        lastClearHitsMillis = System.currentTimeMillis();
                    }
                } finally {
                    reentrantLock.unlock();
                }
            }
            // if false then another thread is already clearing countryHits.
        }

        String countryCode;
        try {
            countryCode = ipToCountryCache.get(ip);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to retrieve country code for ip=" + ip + ".",
                    e);
            countryCode = DEFAULT_COUNTRY_CODE;
        }

        Long newValue = countryHits.merge(countryCode, 1L,
                new BiFunction<Long, Long, Long>() {

                    @Override
                    public Long apply(Long t, Long u) {
                        return t + u;
                    }
                });
        if (newValue > MAX_HITS_PER_SECOND) {
            return false;
        }
        return true;
    }
}
