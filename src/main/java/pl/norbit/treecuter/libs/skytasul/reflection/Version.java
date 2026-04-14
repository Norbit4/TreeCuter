package pl.norbit.treecuter.libs.skytasul.reflection;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Patched version of {@link fr.skytasul.reflection.Version}
 */
public record Version(int major, int minor, int patch) implements Comparable<Version> {
    public static final Version ZERO = new Version(0, 0, 0);

    public boolean is(int major, int minor, int patch) {
        return this.major() == major && this.minor() == minor && this.patch() == patch;
    }

    public boolean is(@NotNull Version version) {
        return this.equals(version);
    }

    public boolean isAfter(int major, int minor, int patch) {
        if (this.major() > major) {
            return true;
        } else if (this.major() < major) {
            return false;
        } else if (this.minor() > minor) {
            return true;
        } else if (this.minor() < minor) {
            return false;
        } else {
            return this.patch() >= patch;
        }
    }

    public boolean isAfter(@NotNull Version version) {
        return this.isAfter(version.major, version.minor, version.patch);
    }

    public boolean isBefore(int major, int minor, int patch) {
        return !this.isAfter(major, minor, patch);
    }

    public boolean isBefore(@NotNull Version version) {
        return this.isBefore(version.major, version.minor, version.patch);
    }

    public int compareTo(Version o) {
        if (o.equals(this)) {
            return 0;
        } else {
            return this.isAfter(o) ? 1 : -1;
        }
    }

    public @NotNull String toString() {
        return this.toString(false);
    }

    public @NotNull String toString(boolean omitPatch) {
        return omitPatch && this.patch == 0 ? "%d.%d".formatted(this.major, this.minor) : "%d.%d.%d".formatted(this.major, this.minor, this.patch);
    }

    public static @NotNull Version parse(@NotNull String string) {
        if (string.isBlank()) {
            throw new IllegalArgumentException("Version string cannot be blank");
        }

        // '-' never appears but after patch or minor version
        int dash = string.indexOf('-');
        String str = (dash == -1) ? string : string.substring(0, dash);

        // String[] parts = string.split("\\.");
        // Narrow parts down to the last numeric part before the first non-numerical part, supporting versions like "26.1.1.build.1234"
        String[] parts = Stream.of(str.split("\\.")).filter(s -> s.matches("\\d+")).toArray(String[]::new);
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Malformed version: " + string);
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
            return new Version(major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Malformed version: " + string, e);
        }
    }

    public static @Nullable Version parseOrNull(@NotNull String string) {
        try {
            return parse(string);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static @NotNull Version @NotNull [] parseArray(String... versions) {
        return (Version[])Stream.of(versions).map(Version::parse).toArray((x$0) -> new Version[x$0]);
    }
}

