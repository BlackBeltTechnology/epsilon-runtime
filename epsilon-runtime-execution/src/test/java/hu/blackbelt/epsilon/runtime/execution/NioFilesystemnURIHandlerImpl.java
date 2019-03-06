/**
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package hu.blackbelt.epsilon.runtime.execution;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class NioFilesystemnURIHandlerImpl extends URIHandlerImpl
{
  /**
   * Creates an instance.
   */
  FileSystem fileSystem;

  public NioFilesystemnURIHandlerImpl(FileSystem fileSystem)
  {
    super();
    this.fileSystem = fileSystem;
  }

  @Override
  public boolean canHandle(URI uri)
  {
    return uri.scheme().equals("niofs");
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
  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    final String filePath = uri.toFileString();
    final File file = new File(filePath);

    String parent = file.getParent();
    if (parent != null) {
      Files.createDirectories(fileSystem.getPath(parent));
    }
    final Map<Object, Object> response = getResponse(options);
    OutputStream outputStream =


            new BufferedOutputStream(Files.newOutputStream(fileSystem.getPath(parent)))  {
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
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    String filePath = uri.toFileString();
    InputStream inputStream = Files.newInputStream(fileSystem.getPath(filePath));


    Map<Object, Object> response = getResponse(options);
    if (response != null)
    {
      response.put(URIConverter.RESPONSE_TIME_STAMP_PROPERTY, fileSystem.getPath(filePath).lastModified());
    }
    return inputStream;
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    String filePath = uri.toFileString();
    File file = new File(filePath);
    file.delete();
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    String filePath = uri.toFileString();
    File file = new File(filePath);
    return file.exists();
  }

  @Override
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    Map<String, Object> result = new HashMap<String, Object>();
    String filePath = uri.toFileString();
    File file = new File(filePath);
    if (file.exists())
    {
      Set<String> requestedAttributes = getRequestedAttributes(options);
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_TIME_STAMP))
      {
        result.put(URIConverter.ATTRIBUTE_TIME_STAMP, file.lastModified());
      }
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_LENGTH))
      {
        result.put(URIConverter.ATTRIBUTE_LENGTH, file.length());
      }
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY))
      {
        result.put(URIConverter.ATTRIBUTE_READ_ONLY, !file.canWrite());
      }
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_HIDDEN))
      {
        result.put(URIConverter.ATTRIBUTE_HIDDEN, file.isHidden());
      }
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_DIRECTORY))
      {
        result.put(URIConverter.ATTRIBUTE_DIRECTORY, file.isDirectory());
      }
    }
    return result;
  }

  @Override
  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    String filePath = uri.toFileString();
    File file = new File(filePath);
    if (file.exists())
    {
      Long timeStamp = (Long)attributes.get(URIConverter.ATTRIBUTE_TIME_STAMP);
      if (timeStamp != null && !file.setLastModified(timeStamp))
      {
        throw new IOException("Could not set the timestamp for the file '" + file +"'");
      }
      Boolean isReadOnly = (Boolean)attributes.get(URIConverter.ATTRIBUTE_READ_ONLY);
      if (Boolean.TRUE.equals(isReadOnly) && !file.setReadOnly())
      {
        throw new IOException("Could not set the file '" + file +"' to be read only");
      }
    }
    else
    {
      throw new FileNotFoundException("The file '" + file + "' does not exist");
    }
  }
}
