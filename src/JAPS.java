import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class JAPS {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String target;
        int minPort;
        int maxPort;
        int timeout = 1000; // Connection timeout in milliseconds

        System.out.println("Port Scanner");
        System.out.print("Enter the target IP address or range (e.g., 192.168.0.1 or 192.168.0.1-192.168.0.10): ");
        target = scanner.nextLine().trim();
        System.out.print("Enter the minimum port number: ");
        minPort = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter the maximum port number: ");
        maxPort = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Type 'scan' to run the port scanner.");
        System.out.println("Type 'exit' to exit the program.");

        while (true) {
            String input = scanner.nextLine().trim();
            if ("scan".equalsIgnoreCase(input)) {
                System.out.println("Scanning ports on " + target + "...\n");
                scanPorts(target, minPort, maxPort, timeout);
            } else if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Exiting the program.");
                break;
            } else {
                System.out.println("Unknown command. Type 'scan' or 'exit'.");
            }
        }

        scanner.close();
    }

    private static void scanPorts(String target, int minPort, int maxPort, int timeout) {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        AtomicBoolean scanningComplete = new AtomicBoolean(false);
        AtomicInteger openPortCount = new AtomicInteger(0);

        for (int port = minPort; port <= maxPort; port++) {
            final int currentPort = port;
            executor.execute(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(target, currentPort), timeout);
                    System.out.println("Port " + currentPort + " is open");
                    socket.close();
                    openPortCount.incrementAndGet();
                } catch (Exception e) {
                    // Port is likely closed or the connection timed out, so we do nothing here.
                } finally {
                    if (currentPort == maxPort) {
                        scanningComplete.set(true);
                    }
                }
            });
        }

        executor.shutdown();
        try {
            while (!scanningComplete.get()) {
                Thread.sleep(100);
            }
            System.out.println("Scanning complete. Found " + openPortCount.get() + " open ports.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
