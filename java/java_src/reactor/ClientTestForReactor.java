package reactor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientTestForReactor {
    public static void main(String[] args) throws IOException {
        Socket s=new Socket("127.0.0.1",9898);
        BufferedOutputStream bos=new BufferedOutputStream(s.getOutputStream());
        BufferedInputStream bis=new BufferedInputStream(s.getInputStream());
        while(true){
            bos.write("hihi".getBytes());
            bos.flush();

            byte[] byteBuf=new byte[1024];
            bis.read(byteBuf);
            System.out.println("받은거 : "+new String(byteBuf));
        }

    }
}
