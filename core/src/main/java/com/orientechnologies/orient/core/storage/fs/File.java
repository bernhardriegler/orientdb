package com.orientechnologies.orient.core.storage.fs;

import com.orientechnologies.common.collection.closabledictionary.OClosableItem;
import com.orientechnologies.common.util.ORawPair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface File extends OClosableItem {
  int HEADER_SIZE = 1024;

  long allocateSpace(int size) throws IOException;

  void shrink(long size) throws IOException;

  long getFileSize();

  void read(long offset, ByteBuffer buffer, boolean throwOnEof) throws IOException;

  void write(long offset, ByteBuffer buffer) throws IOException;

  CountDownLatch write(List<ORawPair<Long, ByteBuffer>> buffers) throws IOException;

  void synch();

  void create() throws IOException;

  /*
   * (non-Javadoc)
   *
   * @see com.orientechnologies.orient.core.storage.fs.OFileAAA#open()
   */
  void open();

  /*
   * (non-Javadoc)
   *
   * @see com.orientechnologies.orient.core.storage.fs.OFileAAA#close()
   */
  void close();

  /*
   * (non-Javadoc)
   *
   * @see com.orientechnologies.orient.core.storage.fs.OFileAAA#delete()
   */
  void delete() throws IOException;

  /*
   * (non-Javadoc)
   *
   * @see com.orientechnologies.orient.core.storage.fs.OFileAAA#isOpen()
   */
  boolean isOpen();

  /*
   * (non-Javadoc)
   *
   * @see com.orientechnologies.orient.core.storage.fs.OFileAAA#exists()
   */
  boolean exists();

  String getName();

  String getPath();

  void renameTo(Path newFile) throws IOException;

  void replaceContentWith(Path newContentFile) throws IOException;

  /*
   * (non-Javadoc)
   *
   * @see com.orientechnologies.orient.core.storage.fs.OFileAAA#toString()
   */
  @Override
  String toString();
}