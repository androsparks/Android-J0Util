/*
 * The MIT License Copyright (c) 2014 Krayushkin Konstantin (jangokvk@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.jango.j0util;

import android.net.Uri;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class for working with paths in {@link java.io.File}, {@link java.net.URL} and
 * {@link java.net.URI}.
 */
public class PathUtil {

    public static String getLastPathSegment(String path) {
        if (path == null) return null;

        try {
            String segment = path;
            if (segment.endsWith("/"))
                segment = segment.substring(0, segment.length() - 1);

            return segment.substring(segment.lastIndexOf("/") + 1);
        } catch(Exception e) { return null; }
    }

    public static String getLastPathSegment(URL url) {
        if (url == null) return null;
        return getLastPathSegment(Uri.decode(url.toString()));
    }

    public static String getLastPathSegment(URI uri) {
        if (uri == null) return null;
        return getLastPathSegment(Uri.decode(uri.toString()));
    }

    public static String getLastPathSegment(File file) {
        if (file == null) return null;
        return getLastPathSegment(file.getAbsolutePath());
    }

    ////////////////////////////////////////////////////////////////////////////

    public static String getFilenameWithoutExt(String path) {
        if (path == null) return null;

        final String lastSegment = getLastPathSegment(path);
        if (!lastSegment.contains(".")) return lastSegment;

        return lastSegment.substring(0, lastSegment.lastIndexOf("."));
    }

    public static String getFilenameWithoutExt(URI uri) {
        if (uri == null) return null;
        return getFilenameWithoutExt(uri.toString());
    }

    public static String getFilenameWithoutExt(URL url) {
        if (url == null) return null;
        return getFilenameWithoutExt(url.toString());
    }

    public static String getFilenameWithoutExt(File file) {
        if (file == null) return null;
        return getFilenameWithoutExt(file.getAbsolutePath());
    }

    ////////////////////////////////////////////////////////////////////////////

    public static String getExt(String path) {
        if (path == null) return null;
        if (path.endsWith("/")) return null;

        final String lastSegment = getLastPathSegment(path);
        if (!lastSegment.contains(".")) return null;

        return lastSegment.substring(lastSegment.lastIndexOf(".") + 1);
    }

    public static String getExt(URI uri) {
        if (uri == null) return null;
        return getExt(uri.toString());
    }

    public static String getExt(URL url) {
        if (url == null) return null;
        return getExt(url.toString());
    }

    public static String getExt(File file) {
        if (file == null) return null;
        return getExt(file.getAbsolutePath());
    }

    ////////////////////////////////////////////////////////////////////////////

    public static URI stringToURI(String str) throws URISyntaxException {
        if (str == null) return null;
        return new URI(Uri.encode(Uri.decode(str), ":/?="));
    }

    public static URI safeStringToURI(String str) {
        try { return stringToURI(str); }
        catch(Exception e) { return null; }
    }

    public static URI urlToURI(URL url) throws URISyntaxException {
        if (url == null) return null;
        return stringToURI(url.toString());
    }

    public static URI safeUrlToURI(URL url) {
        try { return urlToURI(url); }
        catch(Exception e) { return null; }
    }

    public static boolean uriEquals(URI u1, URI u2) {
        if (u1 == u2) return true;
        if (u1 == null || u2 == null) return false;

        try {
            final URI du1 = stringToURI(u1.toString());
            final URI du2 = stringToURI(u2.toString());
            return du1.equals(du2);
        } catch(Exception e) { return false; }
    }

}
