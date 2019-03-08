package hu.blackbelt.epsilon.runtime.execution.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import java.io.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NioFilesystemnRelativePathURIHandlerImpl extends URIHandlerImpl {
  /**
   * Creates an instance.
   */
  FileSystem fileSystem;
  String rootPath;
  String urlSchema;

  public NioFilesystemnRelativePathURIHandlerImpl(String urlSchema, FileSystem fileSystem, String rootPath) {
    super();
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.urlSchema = urlSchema;
    log.info("Creating NIO filesystem URI handler on {}", rootPath);
  }

  @Override
  public boolean canHandle(URI uri)
  {
    return uri.scheme().equals(urlSchema); // && exists(uri, null);
  }

  /**
   * Creates an output stream for the file path and returns it.
   * <p>
   * This implementation allocates a {@link OutputStream} and creates subdirectories as necessary.
   * </p>
   * @return an open output stream.
   * @exception IOException if there is a problem obtaining an open output stream.
   */
  @Override
  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
    String filePath = rootPath + File.separator + uri.opaquePart();
    final File file = new File(filePath);

    String parent = file.getParent();
    if (parent != null) {
      Files.createDirectories(fileSystem.getPath(parent));
    }
    final Map<Object, Object> response = getResponse(options);
    OutputStream outputStream =
            new BufferedOutputStream(Files.newOutputStream(fileSystem.getPath(filePath)))  {
              @Override
              public void close() throws IOException {
                try {
                  super.close();
                }
                finally {
                  if (response != null) {
                    response.put(URIConverter.RESPONSE_TIME_STAMP_PROPERTY, file.lastModified());
                  }
                }
              }
            };
    return outputStream;
  }

  /**
   * Creates an input stream for the file path and returns it.
   * <p>
   * This implementation allocates a {@link FileInputStream}.
   * </p>
   * @return an open input stream.
   * @exception IOException if there is a problem obtaining an open input stream.
   */
  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
    String filePath = rootPath + File.separator + uri.opaquePart();
    InputStream inputStream = Files.newInputStream(fileSystem.getPath(filePath));

    Map<Object, Object> response = getResponse(options);
    if (response != null) {
      response.put(URIConverter.RESPONSE_TIME_STAMP_PROPERTY, Files.getLastModifiedTime(fileSystem.getPath(filePath)).toMillis());
    }
    return inputStream;
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException {
    String filePath = rootPath + File.separator + uri.opaquePart();
    Files.delete(fileSystem.getPath(filePath));
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options) {
    String filePath = rootPath + File.separator + uri.opaquePart();
    return Files.exists(fileSystem.getPath(filePath));
  }

  @Override
  @lombok.SneakyThrows(IOException.class)
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
    Map<String, Object> result = new HashMap<String, Object>();
    String filePath = rootPath + File.separator + uri.opaquePart();
    if (exists(uri, options)) {
        Set<String> requestedAttributes = getRequestedAttributes(options);
        if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_TIME_STAMP)) {
          result.put(URIConverter.ATTRIBUTE_TIME_STAMP, Files.getLastModifiedTime(fileSystem.getPath(filePath)).toMillis());
        }
        if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_LENGTH)) {
          result.put(URIConverter.ATTRIBUTE_LENGTH, Files.size(fileSystem.getPath(filePath)));
        }
        if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY)) {
          result.put(URIConverter.ATTRIBUTE_READ_ONLY, !Files.isWritable(fileSystem.getPath(filePath)));
        }
        if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_HIDDEN)) {
          result.put(URIConverter.ATTRIBUTE_HIDDEN, Files.isHidden(fileSystem.getPath(filePath)));
        }
        if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_DIRECTORY)) {
          result.put(URIConverter.ATTRIBUTE_DIRECTORY, Files.isDirectory(fileSystem.getPath(filePath)));
        }
    }
    return result;
  }

  @Override
  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException {
    String filePath = rootPath + File.separator + uri.opaquePart();
    Path file = fileSystem.getPath(filePath);
    if (exists(uri, options)) {
      Long timeStamp = (Long) attributes.get(URIConverter.ATTRIBUTE_TIME_STAMP);
      if (timeStamp != null) {
        Files.setLastModifiedTime(file, FileTime.fromMillis(timeStamp));
      }
      Boolean isReadOnly = (Boolean) attributes.get(URIConverter.ATTRIBUTE_READ_ONLY);

      FileStore fileStore = Files.getFileStore(file);
      if (fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
        // Set read-only
        DosFileAttributeView attrs =
                Files.getFileAttributeView(
                        file, DosFileAttributeView.class);
        attrs.setReadOnly(true);
      } else if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
        // Change permissions
        PosixFileAttributeView attrs =
                Files.getFileAttributeView(
                        file, PosixFileAttributeView.class);
      }
    }
    else {
      throw new FileNotFoundException("The file '" + file + "' does not exist");
    }
  }
}
