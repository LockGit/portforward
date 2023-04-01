/**
 * @author Lock
 * @version 2023/4/1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PortForward {

    public final static Logger log = LoggerFactory.getLogger(PortForward.class);

    public static void help() {
        System.out.println("Usage: PortForward.jar -l [local_addr] -p [port] [-t target_addr] [-p target_port]");
        System.out.println("Options:");
        System.out.println("  -h, --help\t\tPrint this help message");
        System.out.println("  -l, --input\t\tSpecify local listen ip address");
        System.out.println("  -p, --output\t\tSpecify listen port");
        System.out.println("  -t, --output\t\tSpecify forward target listen ip address");
        System.out.println("for example:\t\t java -jar PortForward.jar -l 0.0.0.0 -p 1234 -t 127.0.0.1 -p 5678");
    }

    public static void main(String[] args) {
        if (args.length >= 1 && ("-h".equals(args[0]) || "--help".equals(args[0]))) {
            help();
            System.exit(1);
            return;
        }

        String localIp = args.length > 1 ? args[1] : "127.0.0.1";
        int port = args.length > 3 ? Integer.parseInt(args[3]) : 1234;
        String targetIp = args.length > 5 ? args[5] : "127.0.0.1";
        int targetPort = args.length > 7 ? Integer.parseInt(args[7]) : 5678;
        log.info("local is: {}:{},target is:{}:{}", localIp, port, targetIp, targetPort);

        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(localIp, port));
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("get new conn form {}", clientSocket.getRemoteSocketAddress().toString());
                Socket forwardSocket = null;
                while (true) {
                    try {
                        forwardSocket = new Socket(targetIp, targetPort);
                        break;
                    } catch (Exception e) {
                        log.error("dail target network error {}", e.getMessage());
                        Thread.sleep(5000);
                    }
                }
                if (forwardSocket == null) {
                    log.error("dail target network try error");
                    return;
                }
                PortForward pf = new PortForward();
                pf.forward(forwardSocket, clientSocket);
            }
        } catch (Exception e) {
            log.info("PortForward.main.exception:{}", e.getMessage());
        }
    }

    public static class Pipe extends Thread {
        private final InputStream in;
        private final OutputStream out;

        public Pipe(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                log.error("Pipe exception:{}", e.getMessage());
            }
        }
    }

    public void forward(Socket forwardSocket, Socket clientSocket) throws IOException {
        InputStream inStream = clientSocket.getInputStream();
        OutputStream outStream = clientSocket.getOutputStream();

        InputStream forwardInStream = forwardSocket.getInputStream();
        OutputStream forwardOutStream = forwardSocket.getOutputStream();

        Pipe inputPipe = new Pipe(inStream, forwardOutStream);
        Pipe outputPipe = new Pipe(forwardInStream, outStream);

        inputPipe.start();
        outputPipe.start();
    }
}
