package se.ifmo.databases.tutor.repositories;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class AbstractFilesystemSourceRepository<T> {

  private static final String RW_MODE = "rw";

  public String save(final String mimeType, final InputStream inputStream) {
    final UUID uuid = UUID.randomUUID();
    final Path filePath = getPath(uuid, mimeType);
    try (final var fileWriter = new RandomAccessFile(filePath.toAbsolutePath().toFile(), RW_MODE)) {
      final var fileChannel = fileWriter.getChannel();
      final var byteBuffer = ByteBuffer.wrap(inputStream.readAllBytes());
      fileChannel.write(byteBuffer);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return getFullSourceName(uuid, mimeType);
  }

  public boolean delete(final String fullSourceName) {
    try {
      return Files.deleteIfExists(getPath(fullSourceName));
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  public Path getPath(final String fullSourceName) {
    return Paths.get(getSourceDirectory(), fullSourceName);
  }

  protected abstract String getSourceDirectory();

  private Path getPath(final UUID fileUuid, final String mimeType) {
    return Paths.get(getSourceDirectory(), getFullSourceName(fileUuid, mimeType));
  }

  private String getFullSourceName(final UUID avatarUuid, String mimeType) {
    return format("%s.%s", avatarUuid.toString(), mimeType);
  }
}
