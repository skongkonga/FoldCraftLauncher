/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tungsten.fclcore.auth.authlibinjector;

import com.tungsten.fclauncher.utils.FCLPath;
import com.tungsten.fclcore.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class AuthlibInjectorArtifactInfo {

    public static AuthlibInjectorArtifactInfo from(Path location) throws IOException {
        try (JarFile jarFile = new JarFile(location.toFile())) {
            Attributes attributes = jarFile.getManifest().getMainAttributes();

            String title = Optional.ofNullable(attributes.getValue("Implementation-Title"))
                    .orElseThrow(() -> new IOException("Missing Implementation-Title"));
            if (!"authlib-injector".equals(title)) {
                throw new IOException("Bad Implementation-Title");
            }

            String version = Optional.ofNullable(attributes.getValue("Implementation-Version"))
                    .orElseThrow(() -> new IOException("Missing Implementation-Version"));

            int buildNumber;
            try {
                buildNumber = Optional.ofNullable(attributes.getValue("Build-Number"))
                        .map(Integer::parseInt)
                        .orElseThrow(() -> new IOException("Missing Build-Number"));
            } catch (NumberFormatException e) {
                throw new IOException("Bad Build-Number", e);
            }
            return new AuthlibInjectorArtifactInfo(buildNumber, version, location.toAbsolutePath());
        }
    }

    /**
     * 从APK内部读取“authlib-injector.jar”文件，并读取它里面的MANIFEST.MF文件信息
     * @return 返回一个AuthlibInjectorArtifactInfo类型
     * @throws IOException
    **/
    public static AuthlibInjectorArtifactInfo from() throws IOException {
        IOUtils.copyAssets(FCLPath.ASSETS_AUTHLIB_INJECTOR_JAR, FCLPath.CACHE_DIR + "/authlib-injector.jar");

        return from(new File(FCLPath.CACHE_DIR + "/authlib-injector.jar").toPath());
    }


    private int buildNumber;
    private String version;
    private Path location;

    public AuthlibInjectorArtifactInfo(int buildNumber, String version, Path location) {
        this.buildNumber = buildNumber;
        this.version = version;
        this.location = location;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getVersion() {
        return version;
    }

    public Path getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "authlib-injector [buildNumber=" + buildNumber + ", version=" + version + "]";
    }
}
