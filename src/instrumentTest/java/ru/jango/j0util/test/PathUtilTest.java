package ru.jango.j0util.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.net.URI;

import ru.jango.j0util.PathUtil;

public class PathUtilTest extends TestCase {

    public void testGetLastPathSegment() throws Exception {
        Assert.assertEquals("file.f", PathUtil.getLastPathSegment("http://ololo.com/dir/dir/file.f"));
        Assert.assertEquals("file.f", PathUtil.getLastPathSegment("/dir/dir/file.f"));
        Assert.assertEquals("f", PathUtil.getLastPathSegment("/dir/dir/f"));
        Assert.assertEquals("dir", PathUtil.getLastPathSegment("content://com.ololo.provider/dir/dir/"));

        final String s = null;
        Assert.assertNull(PathUtil.getLastPathSegment(s));
    }

    public void testGetFilenameWithoutExt() throws Exception {
        Assert.assertEquals("file", PathUtil.getFilenameWithoutExt("http://ololo.com/dir/dir/file.f"));
        Assert.assertEquals("file", PathUtil.getFilenameWithoutExt("/dir/dir/file.f"));
        Assert.assertEquals("f", PathUtil.getFilenameWithoutExt("/dir/dir/f"));
        Assert.assertEquals("file.f", PathUtil.getFilenameWithoutExt("/dir/dir/file.f.ff"));
        Assert.assertEquals("dir", PathUtil.getFilenameWithoutExt("content://com.ololo.provider/dir/dir/"));

        final String s = null;
        Assert.assertNull(PathUtil.getFilenameWithoutExt(s));
    }

    public void testGetExt() throws Exception {
        Assert.assertEquals("f", PathUtil.getExt("http://ololo.com/dir/dir/file.f"));
        Assert.assertEquals("f", PathUtil.getExt("/dir/dir/file.f"));
        Assert.assertEquals("ff", PathUtil.getExt("/dir/dir/file.f.ff"));

        final String s = null;
        Assert.assertNull(PathUtil.getExt(s));
        Assert.assertNull(PathUtil.getExt("/dir/dir/f"));
        Assert.assertNull(PathUtil.getExt("content://com.ololo.provider/dir/dir/"));
    }

    public void testStringToUri() throws Exception {
        Assert.assertEquals("http://ololo.com/dir/dir/file.f", PathUtil.stringToURI("http://ololo.com/dir/dir/file.f").toString());
        Assert.assertEquals("http://ololo.com/dir/dir/", PathUtil.stringToURI("http://ololo.com/dir/dir/").toString());
        Assert.assertEquals("http://ololo.com/o.php?a=a%20a", PathUtil.stringToURI("http://ololo.com/o.php?a=a a").toString());
        Assert.assertEquals("http://ololo.com/o.php?a=a%20a", PathUtil.stringToURI("http://ololo.com/o.php?a=a%20a").toString());
        Assert.assertEquals("http://ololo.com/o.php?a=%D0%BE%D0%BB%D0%BE%D0%BB%D0%BE", PathUtil.stringToURI("http://ololo.com/o.php?a=ололо").toString());
        Assert.assertEquals("https://ololo.com:555/dir/dir/", PathUtil.stringToURI("https://ololo.com:555/dir/dir/").toString());
        Assert.assertEquals("/ololo/dir/dir/", PathUtil.stringToURI("/ololo/dir/dir/").toString());
        Assert.assertEquals("file:///ololo/dir/dir/", PathUtil.stringToURI("file:///ololo/dir/dir/").toString());

        final String s = null;
        Assert.assertNull(PathUtil.stringToURI(s));
    }

    public void testUriEquals() throws Exception {
        Assert.assertTrue(PathUtil.uriEquals(null, null));
        Assert.assertTrue(PathUtil.uriEquals(new URI("http://ololo.com/dir/dir/file.f"), new URI("http://ololo.com/dir/dir/file.f")));
        Assert.assertTrue(PathUtil.uriEquals(new URI("http://ololo.com/dir/dir/"), new URI("http://ololo.com/dir/dir/")));
        Assert.assertTrue(PathUtil.uriEquals(new URI("http://ololo.com/o.php?a=a%20a"), new URI("http://ololo.com/o.php?a=a%20a")));
        Assert.assertTrue(PathUtil.uriEquals(new URI("http://ololo.com/o.php?a=%D0%BE%D0%BB%D0%BE%D0%BB%D0%BE"), new URI("http://ololo.com/o.php?a=ололо")));
        Assert.assertTrue(PathUtil.uriEquals(new URI("https://ololo.com:555/dir/dir/"), new URI("https://ololo.com:555/dir/dir/")));
        Assert.assertTrue(PathUtil.uriEquals(new URI("/ololo/dir/dir/"), new URI("/ololo/dir/dir/")));
        Assert.assertTrue(PathUtil.uriEquals(new URI("file:///ololo/dir/dir/"), new URI("file:///ololo/dir/dir/")));

        Assert.assertFalse(PathUtil.uriEquals(new URI("ololo"), null));
        Assert.assertFalse(PathUtil.uriEquals(null, new URI("ololo")));
        Assert.assertFalse(PathUtil.uriEquals(new URI("ololo1"), new URI("ololo2")));
    }

}
