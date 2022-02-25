package com.oliveryasuna.idea.qemu.util;

import com.intellij.openapi.diagnostic.Logger;
import com.oliveryasuna.commons.language.condition.Arguments;
import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;
import com.oliveryasuna.commons.language.marker.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Utility
public final class CommandUtils {

  // Logging
  //--------------------------------------------------

  private static final Logger LOGGER = Logger.getInstance(CommandUtils.class);

  // Static utility methods
  //--------------------------------------------------

  public static List<File> findExecutables(final String commandRegex) {
    Arguments.requireNotNull(commandRegex, "commandRegex == null");

    return Stream.concat(
            FileUtils.getPath()
                .filter(File::isDirectory),
            Stream.of(FileUtils.getCurrentDirectory()))
        .flatMap(directory -> {
          try {
            return FileUtils.findFiles(directory, 1, commandRegex)
                .filter(file -> Files.isExecutable(file.toPath()));
          } catch(final IOException e) {
            LOGGER.error(e); // TODO: Error reporting.

            return Stream.empty();
          }
        })
        .collect(Collectors.toUnmodifiableList());

    /*return Arrays.stream(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
        .map(Paths::get)
        .anyMatch(path -> {
          try {
            final Path resolvedPath = path.resolve(command);

            return Files.isExecutable(resolvedPath);
          } catch(final Exception e) {
            return false;
          }
        });*/
  }

  // Constructors
  //--------------------------------------------------

  private CommandUtils() {
    super();

    throw new UnsupportedInstantiationException();
  }

}
