package net.foxtam.warpathlorry;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VersionTest {
    Version version = new Version("0.5.1");

    @Test
    public void isGreater() {
        assertTrue(version.isGreater(new Version("0.5")));
        assertTrue(version.isGreater(new Version("0.5.0")));
        assertTrue(version.isGreater(new Version("0.5.0.3")));
        assertTrue(version.isGreater(new Version("0.4")));
        assertTrue(version.isGreater(new Version("0.4.2")));

        assertFalse(version.isGreater(version));
        assertFalse(version.isGreater(new Version("1.0")));
        assertFalse(version.isGreater(new Version("0.9.0")));
        assertFalse(version.isGreater(new Version("0.5.1.1")));
        assertFalse(version.isGreater(new Version("0.5.1.0")));
    }

    @Test
    public void isLess() {
        assertFalse(version.isLess(version));
        assertFalse(version.isLess(new Version("0.5")));
        assertFalse(version.isLess(new Version("0.5.0")));
        assertFalse(version.isLess(new Version("0.5.0.3")));
        assertFalse(version.isLess(new Version("0.4")));
        assertFalse(version.isLess(new Version("0.4.2")));
        assertFalse(version.isLess(new Version("0.5.1.0")));

        assertTrue(version.isLess(new Version("1.0")));
        assertTrue(version.isLess(new Version("0.9.0")));
        assertTrue(version.isLess(new Version("0.5.1.1")));
    }
}