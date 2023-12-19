package org.jembi.jempi.shared.kafka.global_context;

import org.apache.kafka.clients.admin.DeleteTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.errors.UnknownTopicIdException;
import org.jembi.jempi.shared.kafka.global_context.store_processor.StoreProcessorFactory;
import org.jembi.jempi.shared.kafka.global_context.store_processor.StoreProcessor;

import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreProcessorFactoryTest {
    TestUtils testUtils;

    @BeforeAll
    void prepareForTests(){
        testUtils = new TestUtils("localhost:9097");
    }

    <T> StoreProcessorFactory<T> getGlobalKTableWrapperInstance(Boolean restAll) throws ExecutionException, InterruptedException {
        if (restAll){
            Collection<String> collection = testUtils.kafkaAdminClient.listTopics(new ListTopicsOptions().listInternal(false)).listings().get().stream()
                    .map(TopicListing::name)
                    .filter(name -> name.startsWith("testTopic"))
                    .collect(Collectors.toCollection(ArrayList::new));


            testUtils.kafkaAdminClient.deleteTopics(collection, new DeleteTopicsOptions()).all().get();
            Thread.sleep(1000);
        }
        return new StoreProcessorFactory<T>(testUtils.bootStrapServer);
    }
    @Test
    void testCanCreateNewInstance() throws ExecutionException, InterruptedException {
        StoreProcessorFactory<TestUtils.MockTableData> factory = getGlobalKTableWrapperInstance(true);
        StoreProcessor<TestUtils.MockTableData> sampleInstance = factory.getCreate(testUtils.getTestTopicName("sample-table"), TestUtils.MockTableData.class);
        assertInstanceOf(StoreProcessor.class, sampleInstance);
    }
    @Test
    void testItErrorsOutWhenGlobalKTableDoesNotExists() throws ExecutionException, InterruptedException {
        assertThrows(UnknownTopicIdException.class, () -> {
            this.<TestUtils.MockTableData>getGlobalKTableWrapperInstance(true).get(testUtils.getTestTopicName("sample-table"), TestUtils.MockTableData.class);
        });
    }
    @Test
    void testDoesNotRecreateIfGlobalKTableAlreadyExists() throws ExecutionException, InterruptedException {
        StoreProcessorFactory<TestUtils.MockTableData>  factory = getGlobalKTableWrapperInstance(true);
        StoreProcessor<TestUtils.MockTableData> sampleInstance = factory.getCreate(testUtils.getTestTopicName("sample-table"), TestUtils.MockTableData.class);
        assertInstanceOf(StoreProcessor.class, sampleInstance);

        assertEquals(sampleInstance.hashCode(), factory.get(testUtils.getTestTopicName("sample-table"), TestUtils.MockTableData.class).hashCode());
    }
}
