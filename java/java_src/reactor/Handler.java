package reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

final class Handler implements Runnable {
    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector sel, SocketChannel c)
            throws IOException {
        socket = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = socket.register(sel, SelectionKey.OP_READ);
        sk.attach(this);
//        sel.wakeup();
    }

    boolean inputIsComplete() { /* ... */ return true;}

    boolean outputIsComplete() { /* ... */ return true;}

    void process() { /* ... */System.out.println("processsssss"); }


    // class Handler continued
    public void run() {
        try {
            if (state == READING) read();
            else if (state == SENDING) send();
        } catch (IOException ex) { /* ... */ }
    }

    void read() throws IOException {
        int readCount = socket.read(input);
        if (readCount > 0) {
            process();
        }
        output=input.duplicate();
        state = SENDING;
        // Normally also do first write now
        sk.interestOps(SelectionKey.OP_WRITE);
    }

    void send() throws IOException {
        output.flip();
        socket.write(output);

//        if (outputIsComplete()) sk.cancel();
        state=READING;
        sk.interestOps(SelectionKey.OP_READ);
    }
}
