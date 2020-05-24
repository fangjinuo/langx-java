package com.jn.langx.classpath.cp;

import com.jn.langx.classpath.Classpaths;
import com.jn.langx.io.resource.DirectoryBasedFileResourceLoader;
import com.jn.langx.io.resource.Location;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.io.file.filter.FilenameSuffixFilter;

import java.io.File;
import java.util.List;
import java.util.Set;

public class JarDirectoryClasspath extends AbstractClasspath {
    private List<JarClasspath> jars = Collects.emptyArrayList();

    public JarDirectoryClasspath(String dirName) {
        List<File> files = new DirectoryBasedFileResourceLoader(dirName).listFiles(new FilenameSuffixFilter(new String[]{"jar", "zip"}, true));

        Collects.forEach(files, new Consumer2<Integer, File>() {
            @Override
            public void accept(Integer index, File jarfile) {
                jars.add(new JarClasspath(jarfile));
            }
        });
    }

    @Override
    public Resource findResource(final String relativePath, final boolean isClass) {
        final String path = Classpaths.getPath(relativePath, isClass);
        return Collects.firstMap(jars, new Function2<Integer, JarClasspath, Resource>() {
            @Override
            public Resource apply(Integer index, JarClasspath jarClasspath) {
                return jarClasspath.findResource(path, false);
            }
        });
    }

    @Override
    public Location getRoot() {
        return null;
    }

    @Override
    public Set<Location> allResources() {
        return null;
    }
}