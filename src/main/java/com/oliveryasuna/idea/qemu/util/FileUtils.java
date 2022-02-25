/*
 * Copyright 2022 Oliver Yasuna
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oliveryasuna.idea.qemu.util;

import com.oliveryasuna.commons.language.condition.Arguments;
import com.oliveryasuna.commons.language.marker.Utility;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// TODO: Move to commons-language.
@Utility
public final class FileUtils {

  // Static utility methods
  //--------------------------------------------------

  public static Stream<File> findFiles(final File directory, final int maxDepth, final FileFilter filter) throws IOException {
    Arguments.requireNotNull(directory, "directory == null");

    return Files.find(directory.toPath(), maxDepth, (path, basicFileAttributes) -> filter == null || filter.accept(path.toFile()))
        .map(Path::toFile);
  }

  public static Stream<File> findFiles(final File directory, final int maxDepth, final FilenameFilter filter) throws IOException {
    Arguments.requireNotNull(directory, "directory == null");

    return Files.find(directory.toPath(), maxDepth, (path, basicFileAttributes) -> filter == null || filter.accept(directory, path.toFile().getName()))
        .map(Path::toFile);
  }

  public static Stream<File> findFiles(final File directory, final int maxDepth, final String filenameRegex) throws IOException {
    Arguments.requireNotNull(directory, "directory == null");

    return Files.find(directory.toPath(), maxDepth, (path, basicFileAttributes) -> filenameRegex == null || path.toFile().getName().matches(filenameRegex))
        .map(Path::toFile);
  }

  public static Stream<File> getPath(final String name) {
    Arguments.requireNotNull(name, "name == null");

    final String path = System.getenv(name);
    final String[] files = path.split(Pattern.quote(File.pathSeparator));

    return Arrays.stream(files)
        .map(File::new);
  }

  public static Stream<File> getPath() {
    return getPath("PATH");
  }

  public static File getCurrentDirectory() {
    return new File(".");
  }

  // Constructors
  //--------------------------------------------------

  private FileUtils() {
    super();
  }

}
