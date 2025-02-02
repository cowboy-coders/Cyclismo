/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.cowboycoders.cyclisimo.services;

import static com.google.android.testing.mocking.AndroidMock.expect;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;

import android.test.AndroidTestCase;

import java.io.File;

import org.cowboycoders.cyclisimo.services.RemoveTempFilesService;

/**
 * Tests {@link RemoveTempFilesService}.
 *
 * @author Sandor Dornbush
 */
public class RemoveTempFilesServiceTest extends AndroidTestCase {

  private static final String DIR_NAME = "/tmp";
  private static final String FILE_NAME = "foo";
  
  private RemoveTempFilesService service;

  @UsesMocks({ File.class, })
  protected void setUp() throws Exception {
    service = new RemoveTempFilesService();
  };

  /**
   * Tests when the directory doesn't exists.
   */
  public void test_noDir() {
    File dir = AndroidMock.createMock(File.class, DIR_NAME);
    expect(dir.exists()).andStubReturn(false);
    AndroidMock.replay(dir);

    assertEquals(0, service.cleanTempDirectory(dir));
    AndroidMock.verify(dir);
  }

  /**
   * Tests when the directory is empty.
   */
  public void test_emptyDir() {
    File dir = AndroidMock.createMock(File.class, DIR_NAME);
    expect(dir.exists()).andStubReturn(true);
    expect(dir.listFiles()).andStubReturn(new File[0]);
    AndroidMock.replay(dir);

    assertEquals(0, service.cleanTempDirectory(dir));
    AndroidMock.verify(dir);
  }

  /**
   * Tests when there is a new file and it shouldn't get deleted.
   */
  public void test_newFile() {
    File file = AndroidMock.createMock(File.class, DIR_NAME + FILE_NAME);
    expect(file.lastModified()).andStubReturn(System.currentTimeMillis());

    File dir = AndroidMock.createMock(File.class, DIR_NAME);
    expect(dir.exists()).andStubReturn(true);
    expect(dir.listFiles()).andStubReturn(new File[] { file });
    AndroidMock.replay(dir, file);

    assertEquals(0, service.cleanTempDirectory(dir));
    AndroidMock.verify(dir, file);
  }

  /**
   * Tests when there is an old file and it should get deleted.
   */
  public void test_oldFile() {
    File file = AndroidMock.createMock(File.class, DIR_NAME + FILE_NAME);
    // qSet to one hour and 1 millisecond later than the current time
    expect(file.lastModified()).andStubReturn(System.currentTimeMillis() - 3600001);
    expect(file.delete()).andStubReturn(true);
 
    File dir = AndroidMock.createMock(File.class, DIR_NAME);
    expect(dir.exists()).andStubReturn(true);
    expect(dir.listFiles()).andStubReturn(new File[] { file });
    AndroidMock.replay(dir, file);
    
    assertEquals(1, service.cleanTempDirectory(dir));
    AndroidMock.verify(dir, file);
  }
}
