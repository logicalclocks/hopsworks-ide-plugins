/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.logicalclocks.upload.net;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader {

    private RandomAccessFile raf;

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public long open(String filePath) throws IOException{
        raf = new RandomAccessFile(filePath, "r");
        long fileSize = raf.length();
        logger.info("File Size: "+ fileSize);
        return fileSize;

    }

    public void close() throws IOException {
        raf.close();
    }

    public byte[] readChunk(int numberOfBytes) throws IOException {
        byte[] buffer = new byte[(int) numberOfBytes];
        int bytesRead = raf.read(buffer);
        if (bytesRead != -1) {
            return buffer;
        }
        else
        {
            throw new IOException("Read File Failed");
        }

    }

}