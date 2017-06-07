package org.embulk.output.mailchimp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Multimap;
import org.embulk.output.mailchimp.helper.MailChimpHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.embulk.output.mailchimp.helper.MailChimpHelper.containsCaseInsensitive;
import static org.embulk.output.mailchimp.helper.MailChimpHelper.extractMemberStatus;
import static org.embulk.output.mailchimp.helper.MailChimpHelper.maskEmail;
import static org.embulk.output.mailchimp.helper.MailChimpHelper.toJsonNode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by thangnc on 4/26/17.
 */
public class TestMailChimpHelper
{
    @Test
    public void test_maskEmail_validInErrorResponse()
    {
        String given = "thang0001@example.com looks fake or invalid, please enter a real email address.";
        String expect = "t******01@example.com looks fake or invalid, please enter a real email address.";
        assertEquals("Email should match", expect, maskEmail(given));
    }

    @Test
    public void test_containsCaseInsensitive_validMergeFields()
    {
        String expect = "United State";
        assertEquals("Interest category should match", expect, containsCaseInsensitive("united state",
                                                                                       Arrays.asList("Donating", "United State")));
    }

    @Test
    public void test_extractMemberStatus()
    {
        List<JsonNode> given = new ArrayList<>();
        given.add(JsonNodeFactory.instance.objectNode().put("status", "subcribed").put("email", "thang@gmail.com"));
        given.add(JsonNodeFactory.instance.objectNode().put("status", "pending").put("email", "thang123@gmail.com"));
        given.add(JsonNodeFactory.instance.objectNode().put("status", "abc").put("email", "thang456@gmail.com"));
        given.add(JsonNodeFactory.instance.objectNode().put("status", "pending").put("email", "thang789@gmail.com"));
        given.add(JsonNodeFactory.instance.objectNode().put("status", "subcribed").put("email", "thang000@gmail.com"));

        Multimap<String, JsonNode> statusMap = extractMemberStatus(given);
        assertEquals("Status should match", 3, statusMap.keySet().size());
        assertEquals("Status should contain keys", true, statusMap.containsKey("pending"));
        assertEquals("Status should contain keys", true, statusMap.containsKey("subcribed"));
        assertEquals("Status should contain keys", true, statusMap.containsKey("abc"));
    }

    @Test
    public void test_fromCommaSeparatedString()
    {
        String[] expect = new String[]{"Donating", "United State"};
        List separatedString = MailChimpHelper.fromCommaSeparatedString("Donating,United State");

        assertEquals("Length should match", expect.length, separatedString.size());
        assertArrayEquals("Should match", expect, separatedString.toArray());
    }

    @Test
    public void test_toJsonNode_validJsonString()
    {
        String given = "{\"addr1\":\"1234\",\"city\":\"mountain view\",\"country\":\"US\",\"state\":\"CA\",\"zip\":\"95869\"}";
        String expect = "US";

        assertEquals("Should be Json", ObjectNode.class, toJsonNode(given).getClass());
        assertEquals("Should have attribute `country`", expect, toJsonNode(given).get("country").asText());
    }

    @Test
    public void test_toJsonNode_invalidJSonString()
    {
        assertEquals("Should be NullNode", NullNode.class, toJsonNode("abc").getClass());
    }
}
