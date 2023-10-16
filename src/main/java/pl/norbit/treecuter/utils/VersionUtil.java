package pl.norbit.treecuter.utils;

import java.util.List;

public class VersionUtil {

    private static final List<String> SUPPORTED_VERSIONS = List.of("1.17.1", "1.18.2", "1.19.4", "1.20.2");

    public static boolean isSupportedVersion(String version){
        return SUPPORTED_VERSIONS.stream().filter(version::contains).findFirst().orElse(null) != null;
    }

    public static List<String> getSupportedVersion(){
        return SUPPORTED_VERSIONS;
    }
}
