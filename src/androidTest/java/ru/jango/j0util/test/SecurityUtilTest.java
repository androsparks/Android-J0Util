package ru.jango.j0util.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import ru.jango.j0util.SecurityUtil;

public class SecurityUtilTest extends TestCase {

    public void testHashes() {
        Assert.assertEquals("a619d974658f3e749b2d88b215baea46", SecurityUtil.md5("ololo"));
        Assert.assertEquals("b4d0dc9c94e97fbb1a99115116fce87196795d06", SecurityUtil.sha1("ololo"));
        Assert.assertEquals("c09ccadebf4dba75c9b677b26ff7ed496e7c08c4152ff220cc0e9535cab84e03e7b07a4848fef4b9f08d2dd97148f1896d1d862cae6f42e0ec5f8f731ccbe15f", SecurityUtil.sha512("ololo"));

        Assert.assertEquals("0cb2ac8bcf600372b573bf9f807de3eb0b3ceda51c0a2045d6902412c53451f2", SecurityUtil.hash("ololo", "SHA-256"));
        Assert.assertEquals("1208dbea2cafcd3bab8e3aec065b861b9a81c17d0b057265ccfb054ec1f9cd25b9f3bd0061442734b1049f4f60d81f98", SecurityUtil.hash("ololo", "SHA-384"));

        // too old algorithm, or just too old for Android
        final String md2 = SecurityUtil.hash("ololo", "MD2");
        if (md2 != null) Assert.assertEquals("82e489d19d80a2a407d19cf91a5724f4", md2);
    }
}
