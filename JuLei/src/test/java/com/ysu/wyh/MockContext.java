package com.ysu.wyh;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Record;
import org.apache.pulsar.functions.api.utils.FunctionRecord;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MockContext implements Context {
    public HashMap<String,Long> conters=new HashMap<>();
    public HashMap<String,ByteBuffer> stats=new HashMap<>();

    @Override
    public Collection<String> getInputTopics() {
        return List.of();
    }

    @Override
    public String getOutputTopic() {
        return "";
    }

    @Override
    public Record<?> getCurrentRecord() {
        return null;
    }

    @Override
    public String getOutputSchemaType() {
        return "";
    }

    @Override
    public String getFunctionName() {
        return "";
    }

    @Override
    public String getFunctionId() {
        return "";
    }

    @Override
    public String getFunctionVersion() {
        return "";
    }

    @Override
    public Map<String, Object> getUserConfigMap() {
        return Map.of();
    }

    @Override
    public Optional<Object> getUserConfigValue(String key) {
        return Optional.empty();
    }

    @Override
    public Object getUserConfigValueOrDefault(String key, Object defaultValue) {
        return null;
    }

    @Override
    public PulsarAdmin getPulsarAdmin() {
        return null;
    }

    @Override
    public <X> CompletableFuture<Void> publish(String topicName, X object, String schemaOrSerdeClassName) {
        return null;
    }

    @Override
    public <X> CompletableFuture<Void> publish(String topicName, X object) {
        return null;
    }

    @Override
    public <X> TypedMessageBuilder<X> newOutputMessage(String topicName, Schema<X> schema) throws PulsarClientException {
        return null;
    }

    @Override
    public <X> ConsumerBuilder<X> newConsumerBuilder(Schema<X> schema) throws PulsarClientException {
        return null;
    }

    @Override
    public <X> FunctionRecord.FunctionRecordBuilder<X> newOutputRecordBuilder(Schema<X> schema) {
        return null;
    }

    @Override
    public String getTenant() {
        return "";
    }

    @Override
    public String getNamespace() {
        return "";
    }

    @Override
    public int getInstanceId() {
        return 0;
    }

    @Override
    public int getNumInstances() {
        return 0;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public String getSecret(String secretName) {
        return "";
    }

    @Override
    public void putState(String key, ByteBuffer value) {
        if(!stats.containsKey(key)){
            stats.put(key,value);
        }
    }

    @Override
    public CompletableFuture<Void> putStateAsync(String key, ByteBuffer value) {
        return null;
    }

    @Override
    public ByteBuffer getState(String key) {
        if(!stats.containsKey(key)){
            return null;
        }
        else return stats.get(key);

    }

    @Override
    public CompletableFuture<ByteBuffer> getStateAsync(String key) {
        return null;
    }

    @Override
    public void deleteState(String key) {

    }

    @Override
    public CompletableFuture<Void> deleteStateAsync(String key) {
        return null;
    }

    @Override
    public void incrCounter(String key, long amount) {
        boolean b = conters.containsKey(key);
        //如果存在的话
        if (b) {
            Long v = conters.get(key);
            conters.put(key, (v+amount));
        }
        else {
            conters.put(key,amount);
        }
    }

    @Override
    public CompletableFuture<Void> incrCounterAsync(String key, long amount) {
        return null;
    }

    @Override
    public long getCounter(String key) {
        boolean b = conters.containsKey(key);
        if (b) {
            return conters.get(key);
        }
        return -1;
    }

    @Override
    public CompletableFuture<Long> getCounterAsync(String key) {
        return null;
    }

    @Override
    public void recordMetric(String metricName, double value) {

    }

    @Override
    public void fatal(Throwable t) {

    }
}
