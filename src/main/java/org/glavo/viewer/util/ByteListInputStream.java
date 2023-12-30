package org.glavo.viewer.util;

import java.io.IOException;
import java.io.InputStream;

public class ByteListInputStream extends InputStream {
    protected ByteList list;

    protected int pos;
    protected int mark = 0;
    protected int count;

    public ByteListInputStream(ByteList list) {
        this.list = list;
        this.pos = 0;
        this.count = list.size();
    }

    public ByteListInputStream(ByteList list, int offset, int length) {
        this.list = list;
        this.pos = offset;
        this.count = Math.min(offset + length, list.size());
        this.mark = offset;
    }

    public synchronized int read() {
        return (pos < count) ? (list.get(pos++) & 0xff) : -1;
    }

    public synchronized int read(byte b[], int off, int len) {
        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }

        for (int i = 0; i < len; i++) {
            b[off + i] = list.get(pos + 1);
        }
        pos += len;
        return len;
    }


    public synchronized long skip(long n) {
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }

    public synchronized int available() {
        return count - pos;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark = pos;
    }


    public synchronized void reset() {
        pos = mark;
    }

    public void close() throws IOException {
    }
}
