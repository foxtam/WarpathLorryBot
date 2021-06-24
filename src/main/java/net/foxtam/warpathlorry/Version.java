package net.foxtam.warpathlorry;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Version {
    private final List<Integer> subVersions;
    private final String strVersion;

    public Version(String version) {
        this.subVersions = Arrays.stream(version.split("\\.")).map(Integer::parseInt).toList();
        this.strVersion = version;
    }

    public boolean isGreater(Version version) {
        return version.isLess(this);
    }

    public boolean isLess(Version that) {
        for (int i = 0; i < Math.max(subVersions.size(), that.subVersions.size()); i++) {
            Integer v1 = i < subVersions.size() ? subVersions.get(i) : 0;
            Integer v2 = i < that.subVersions.size() ? that.subVersions.get(i) : 0;
            if (v1.compareTo(v2) < 0) return true;
            else if (v1.compareTo(v2) > 0) return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return strVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return subVersions.equals(version.subVersions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subVersions);
    }
}
