package au.com.dius.pact.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.exampleclients.ConsumerClient;
import au.com.dius.pact.model.RequestResponsePact;

public class PactDslJsonBodyArrayLikeTest extends ConsumerPactTestMk2 {

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {
        DslPart body = new PactDslJsonBody()
            .id()
            .object("data")
                .eachLike("array1")
                    .id()
                    .stringType("name")
                    .date("dob")
                    .closeObject()
                .closeArray()
                .minArrayLike("array2", 1)
                    .ipAddress("address")
                    .stringType("name")
                    .closeObject()
                .closeArray()
                .array("array3")
                    .maxArrayLike(5)
                        .integerType("itemCount")
                        .closeObject()
                    .closeArray()
                .closeArray()
            .closeObject();
        RequestResponsePact pact = builder
                .uponReceiving("java test interaction with an array like matcher")
                .path("/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(body)
                .toPact();

        MatcherTestUtils.assertResponseMatcherKeysEqualTo(pact,
            "$.body.id",
            "$.body.data.array1",
            "$.body.data.array1[*].id",
            "$.body.data.array1[*].name",
            "$.body.data.array1[*].dob",
            "$.body.data.array2",
            "$.body.data.array2[*].address",
            "$.body.data.array2[*].name",
            "$.body.data.array3[0]",
            "$.body.data.array3[0][*].itemCount");

        MatcherTestUtils.assertResponseKeysEqualTo(pact,
            "/data",
            "/data/array1",
            "/data/array1/0/dob",
            "/data/array1/0/id",
            "/data/array1/0/name",
            "/data/array2",
            "/data/array2/0/address",
            "/data/array2/0/name",
            "/data/array3/0/0/itemCount",
            "/data/array3",
            "/id");

        return pact;
    }

    @Override
    protected String providerName() {
        return "test_provider_array";
    }

    @Override
    protected String consumerName() {
        return "test_consumer_array";
    }

    @Override
    protected void runTest(MockServer mockServer) {
        try {
            new ConsumerClient(mockServer.getUrl()).getAsMap("/", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
