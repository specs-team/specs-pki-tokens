package org.specs.pkitokens.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CompressUtilsTest {

    @Test
    public void testCompress() throws Exception {
        Token token = Utils.createToken();
        String data = token.toJson();
        byte[] compressed = CompressUtils.compress(data);
        String decompressed = CompressUtils.decompress(compressed);
        assertEquals(data, decompressed);
        System.out.println(String.format("Data: %d, compressed: %d", data.length(), compressed.length));
    }

}