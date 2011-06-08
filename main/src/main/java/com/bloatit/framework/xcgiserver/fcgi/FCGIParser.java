/*
 * Copyright (C) 2011 Linkeos.
 *
 * This file is part of BloatIt.
 *
 * BloatIt is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * BloatIt is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.framework.xcgiserver.fcgi;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.bloatit.framework.xcgiserver.XcgiParser;

/**
 * <p>
 * Parse a fastcgi input stream to extract http params and post stream.
 * </p>
 * <p>
 * The fastcgi protocol specification can be found here:
 * {@link "http://www.fastcgi.com/devkit/doc/fcgi-spec.html"}
 * </p>
 * <p>
 * The fastcgi protocol use 2 stream, 1 input stream and 1 output stream,
 * composed by records. The input stream contains the request and the
 * outputstream contains the response.
 * </p>
 * <p>
 * In the input stream, 2 virtuals stream are composed by 2 type of record: the
 * params records and the stdin record.
 * </p>
 */
public class FCGIParser implements XcgiParser {

    /**
     * The FCGI version is always 1.
     */
    final static byte FCGI_VERSION_1 = 1;

    /**
     * Values of type component of record
     */
    private final static byte FCGI_BEGIN_REQUEST = 1;
    final static byte FCGI_ABORT_REQUEST = 2;
    final static byte FCGI_END_REQUEST = 3;
    final static byte FCGI_PARAMS = 4;
    private final static byte FCGI_STDIN = 5;
    final static byte FCGI_STDOUT = 6;
    final static byte FCGI_STDERR = 7;
    final static byte FCGI_DATA = 8;
    private final static byte FCGI_GET_VALUES = 9;
    final static byte FCGI_GET_VALUES_RESULT = 10;
    final static byte FCGI_UNKNOWN_TYPE = 11;
    final static byte FCGI_MAXTYPE = FCGI_UNKNOWN_TYPE;

    /**
     * Mask for flags component of FCGI_BEGIN_REQUEST body
     */
    private final static byte FCGI_KEEP_CONN = 1;

    /**
     * Values for role component of FCGI_BeginRequestBody
     */
    private final static byte FCGI_RESPONDER = 1;
    final static byte FCGI_AUTHORIZER = 2; // NO_UCD
    final static byte FCGI_FILTER = 3; // NO_UCD

    /**
     * Input stream
     */
    private final DataInputStream dataInput;

    /**
     * Status of param input stream. The param input stream is closed when a
     * empty FCGI_PARAMS record is received.
     */
    private boolean paramStreamOpen = true;

    /**
     * Status of post input stream. The post input stream is closed when a empty
     * FCGI_STDIN record is received.
     */
    private boolean postStreamOpen = true;

    /**
     * Http header params. This map is full with param input stream record's
     * content.
     */
    private final Map<String, String> env;

    /**
     * Stream give to users to write their response.
     */
    private final OutputStream responseStream;

    /**
     * Stream give to users to read the post content
     */
    private final FCGIPostStream postStream;

    /**
     * Create a fcgi parser with the 2 stream of the web server's socket.
     *
     * @param input stream containing data from the web server
     * @param output stream where to write the response to the web server
     * @throws IOException
     */
    public FCGIParser(final InputStream input, final OutputStream output) throws IOException {
        // The FCGIOutputStream has a BufferedOutputStream before and a
        // BufferedOutputStream after.
        // The first avoid to give too small or too big packet to put in on
        // record and the
        // second avoid to
        responseStream = new FCGIOutputStream(this, output);

        postStream = new FCGIPostStream(this);

        dataInput = new DataInputStream(input);
        env = new HashMap<String, String>();

    }

    private static class FCGIException extends IOException {

        private static final long serialVersionUID = 1L;

        private FCGIException(final String message) {
            super(message);
        }
    }

    @Override
    public OutputStream getResponseStream() {
        return responseStream;
    }

    @Override
    public Map<String, String> getEnv() throws IOException {
        while (paramStreamOpen) {
            parseRecord();
        }
        return env;
    }

    /**
     * Fetch one post record if possible
     *
     * @throws IOException
     */
    protected void fetchPostRecord() throws IOException {

        while (postStreamOpen && parseRecord() != FCGI_STDIN) {
            // If the post stream is not closed, parse some record until finding
            // a new post record.
        }
    }

    protected void fetchAll() throws IOException {
        while (paramStreamOpen && postStreamOpen) {
            parseRecord();
        }
    }

    public byte[] readRecordHeader() throws IOException {

        byte[] header = new byte[8];
        dataInput.read(header);

        return header;
    }

    private byte parseRecord() throws IOException {

        byte[] header = readRecordHeader();

        final byte version = header[0];
        final byte type = header[1];
        final int requestId = readUnsignedShort(header[2], header[3]);
        final int contentLength = readUnsignedShort(header[4], header[5]);
        final int paddingLength = readUnsignedByte(header[6]);
        // 1 Reserved byte is not read

        if (version != FCGI_VERSION_1) {
            throw new FCGIException("Bad FCGI version code. Found '" + version + "' but '" + FCGI_VERSION_1 + "' excepted.");
        }

        if (requestId != 1) {
            throw new FCGIException("Bad request ID. Found '" + requestId + "' but '" + 1 + "' excepted.");
        }

        switch (type) {
            case FCGI_BEGIN_REQUEST:
                parseBeginRequestRecord(contentLength);
                break;
            case FCGI_ABORT_REQUEST:
                throw new NotImplementedException("TODO type: " + type);
                // break;
            case FCGI_PARAMS:
                parseParamsRecord(contentLength);
                break;
            case FCGI_STDIN:
                parseStdinRecord(contentLength);
                break;
            case FCGI_DATA:
                throw new NotImplementedException("TODO type: " + type);
                // break;
            case FCGI_GET_VALUES:
                throw new NotImplementedException("TODO type: " + type);
                // break;
            default:
                throw new FCGIException("Invalid type code for a record: " + type);
        }

        // Skip padding
        dataInput.skipBytes(paddingLength);
        return type;
    }

    private void parseBeginRequestRecord(final int contentLenght) throws IOException {
        if (contentLenght != 8) {
            throw new FCGIException("Bad lenght for begin request record. Found '" + contentLenght + "' but '8' excepted.");
        }
        final short role = dataInput.readShort();
        final byte flags = dataInput.readByte();
        // Skip 5 reserved bytes
        dataInput.skipBytes(5);

        if ((flags & FCGI_KEEP_CONN) != 0) {
            throw new NotImplementedException("Keep Connection mode is not implemented");
        }

        if (role != FCGI_RESPONDER) {
            throw new NotImplementedException("Only FCGI responder role is implemented");
        }

    }

    private void parseParamsRecord(final int contentLength) throws IOException {
        if (contentLength == 0) {
            // End of param stream
            paramStreamOpen = false;
            return;
        }

        int remainingLength = contentLength;

        while (remainingLength > 0) {
            final int usedLength = parseNameValuePair();
            if (usedLength > remainingLength) {
                throw new FCGIException("Bad format un params record");
            }
            remainingLength -= usedLength;
        }
    }

    private int parseNameValuePair() throws IOException {
        int usedLength = 0;
        long nameLength = 0;
        final int firstNameLengthByte = dataInput.readUnsignedByte();
        usedLength++;

        if ((firstNameLengthByte & 0x80) != 0) {// 10000000
            final int[] lengthArray = new int[4];
            lengthArray[3] = firstNameLengthByte;
            lengthArray[2] = dataInput.readUnsignedByte();
            lengthArray[1] = dataInput.readUnsignedByte();
            lengthArray[0] = dataInput.readUnsignedByte();
            usedLength += 3;

            nameLength = unsignedIntToLong(lengthArray);
        } else {
            nameLength = firstNameLengthByte;
        }

        long valueLength = 0;

        final int firstValueLengthByte = dataInput.readUnsignedByte();
        usedLength++;

        if ((firstValueLengthByte >> 7) == 1) { // 10000000
            final int[] lengthArray = new int[4];
            lengthArray[3] = firstValueLengthByte;
            lengthArray[2] = dataInput.readUnsignedByte();
            lengthArray[1] = dataInput.readUnsignedByte();
            lengthArray[0] = dataInput.readUnsignedByte();
            usedLength += 3;

            valueLength = unsignedIntToLong(lengthArray);
        } else {
            valueLength = firstValueLengthByte;
        }

        final byte[] nameArray = new byte[(int) nameLength];
        dataInput.read(nameArray);
        usedLength += nameLength;
        final String name = new String(nameArray);

        final byte[] valueArray = new byte[(int) valueLength];
        dataInput.read(valueArray);
        usedLength += valueLength;
        final String value = new String(valueArray);

        env.put(name, value);

        return usedLength;
    }

    private void parseStdinRecord(final int contentLength) throws IOException {
        if (contentLength == 0) {
            // End of stdin stream
            postStreamOpen = false;
            return;
        }

        final byte[] data = new byte[contentLength];

        int readLength = 0;

        while (readLength < contentLength) {
            final int size = dataInput.read(data, readLength, contentLength - readLength);
            if (size == -1) {
                throw new EOFException();
            }
            readLength += size;
        }
        postStream.pushData(data);
    }

    /**
     * Converts a 4 byte array of unsigned bytes to an long
     *
     * @param b an array of 4 unsigned bytes
     * @return a long representing the unsigned int
     */
    private static final long unsignedIntToLong(final int[] b) {
        long l = 0;

        l += b[0];
        l += b[1] << 8;
        l += b[2] << 16;
        l += (b[3] & 0x7F) << 24;

        return l;
    }

    @Override
    public InputStream getPostStream() {
        return postStream;
    }

    public boolean isPostStreamOpen() {
        return postStreamOpen;
    }

    public static int readUnsignedByte(byte b) {
        return b & 0xFF;
    }

    public static int readUnsignedShort(byte b1, byte b2) {
        int firstByte = 0x000000FF & b1;
        int secondByte = 0x000000FF & b2;
        return (firstByte << 8 | secondByte);
    }
}
