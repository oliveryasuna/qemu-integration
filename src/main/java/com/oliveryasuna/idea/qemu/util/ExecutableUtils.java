package com.oliveryasuna.idea.qemu.util;

import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;
import com.oliveryasuna.commons.language.marker.Utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

@Utility
public final class ExecutableUtils {

  // Static utility methods
  //--------------------------------------------------

  public static boolean canExecute(final String executable) {
    return Arrays.stream(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
        .map(Paths::get)
        .anyMatch(path -> {
          try {
            final Path resolvedPath = path.resolve(executable);

            return Files.isExecutable(resolvedPath);
          } catch(final Exception e) {
            return false;
          }
        });
  }

  // Constructors
  //--------------------------------------------------

  private ExecutableUtils() {
    super();

    throw new UnsupportedInstantiationException();
  }

}
