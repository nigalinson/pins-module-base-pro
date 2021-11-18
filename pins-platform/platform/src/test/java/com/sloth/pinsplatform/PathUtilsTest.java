package com.sloth.pinsplatform;

import org.junit.Test;

/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2020/04/09
 *     desc  :
 * </pre>
 */
public class PathUtilsTest extends com.blankj.utilcode.util.BaseTest {

    @Test
    public void join() {
        assertEquals(PathUtils.join("", ""), "");
        assertEquals(PathUtils.join("", "data"), "/data");

        assertEquals(PathUtils.join("", "//data"), "/data");
        assertEquals(PathUtils.join("", "data//"), "/data");
        assertEquals(PathUtils.join("", "//data//"), "/data");

        assertEquals(PathUtils.join("/sdcard", "data"), "/sdcard/data");
        assertEquals(PathUtils.join("/sdcard/", "data"), "/sdcard/data");
    }
}