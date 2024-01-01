package org.glavo.viewer.util;

import kala.collection.primitive.ByteSeq;

import java.io.IOException;
import java.io.InputStream;

public class ByteSeqInputStream extends InputStream {
    protected ByteSeq seq;

    protected int pos;
    protected int mark = 0;
    protected int count;

    public ByteSeqInputStream(ByteSeq seq) {
        this.seq = seq;
        this.pos = 0;
        this.count = seq.size();
    }

    public ByteSeqInputStream(ByteSeq seq, int offset, int length) {
        this.seq = seq;
        this.pos = offset;
        this.count = Math.min(offset + length, seq.size());
        this.mark = offset;
    }

    public synchronized int read() {
        return (pos < count) ? (seq.get(pos++) & 0xff) : -1;
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
            b[off + i] = seq.get(pos + 1);
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
