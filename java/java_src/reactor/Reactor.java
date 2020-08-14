package reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocket;

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(
                new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        SelectionKey sk =
                serverSocket.register(selector,SelectionKey.OP_ACCEPT);

        sk.attach(new Acceptor());
    }

    /*
    Alternatively, use explicit SPI provider:
    SelectorProvider p = SelectorProvider.provider();
    selector = p.openSelector();
    serverSocket = p.openServerSocketChannel();
    */
    public void run() { // normally in a new Thread
        try {
            while (!Thread.interrupted()) {
                selector.select(); //이거에 대한 인지는 Selector가 알아서처리.. 엄밀히말하면 selector안에 등록되어있는 channel이 알아서해주는듯함..
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext())
                    dispatch((SelectionKey) (it.next()));
                selected.clear();
            }
        } catch (IOException ex) { /* ... */ }
    }

    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment());
        if (r != null)
            r.run();
    }

    class Acceptor implements Runnable { // inner

        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null)
                    new Handler(selector, c);
            } catch (IOException ex) { /* ... */ }
        }
    }

    public static void main(String[] args) throws IOException {
        Reactor reactor =new Reactor(9898);
        Thread t=new Thread(reactor);
        t.start();

    }
}