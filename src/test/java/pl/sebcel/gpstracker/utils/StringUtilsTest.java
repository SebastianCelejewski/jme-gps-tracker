package pl.sebcel.gpstracker.utils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    public void test_Should_return_array_with_one_element_containing_unmodified_input_when_no_tokens_are_found() {
        String[] output = StringUtils.split("abc", ' ');
        Assert.assertNotNull(output);
        Assert.assertEquals(1, output.length);
        Assert.assertEquals("abc", output[0]);
    }

    public void test_Should_return_an_empty_array_when_no_input_is_provided() {
        String[] output = StringUtils.split("", ' ');
        Assert.assertNotNull(output);
        Assert.assertEquals(0, output.length);
    }

    public void test_Should_return_array_of_tokens_in_valid_order_when_input_contains_separators() {
        String[] output = StringUtils.split("a b c", ' ');
        Assert.assertNotNull(output);
        Assert.assertEquals(3, output.length);
        Assert.assertEquals("First token in input should be the first element in array", "a", output[0]);
        Assert.assertEquals("Second token in input should be the second element in array", "b", output[1]);
        Assert.assertEquals("Third token in input should be the third element in array", "c", output[2]);
    }

    public void test_Should_treat_consecutive_separators_as_a_single_separator() {
        String[] output = StringUtils.split("a    b", ' ');
        Assert.assertNotNull(output);
        Assert.assertEquals(2, output.length);
        Assert.assertEquals("First token in input should be the first element in array", "a", output[0]);
        Assert.assertEquals("Second token in input should be the second element in array, not the token", "b", output[1]);
    }

    public void test_Real_data_01_response_from_http_server() {
        String input = "OK\naction=PAIRED\nauthToken=3uXyTrFeTeOSTcpEXQZ8gA\nmeasure=METRIC\ndisplayName=Sebastian Celejewski\nuserId=5821064\nfacebookConnected=false";
        String[] output = StringUtils.split(input, '\n');
        Assert.assertNotNull(output);
        Assert.assertEquals(7, output.length);
        Assert.assertEquals("OK", output[0]);
        Assert.assertEquals("action=PAIRED", output[1]);
        Assert.assertEquals("authToken=3uXyTrFeTeOSTcpEXQZ8gA", output[2]);
        Assert.assertEquals("measure=METRIC", output[3]);
        Assert.assertEquals("displayName=Sebastian Celejewski", output[4]);
        Assert.assertEquals("userId=5821064", output[5]);
        Assert.assertEquals("facebookConnected=false", output[6]);
    }
}