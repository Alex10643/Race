import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    private static final int NUMBER_OF_DUDES = 10;
    private static final int TUNNEL_ALLOWED = 3;

    private static final CountDownLatch start = new CountDownLatch(NUMBER_OF_DUDES);
    private static final CountDownLatch finish = new CountDownLatch(NUMBER_OF_DUDES);

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_DUDES);
        List<Car> cars = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_DUDES; i++) {
            Car car = new Car(i + 1);
            cars.add(car);
            executorService.execute(car);
        }

        try {
            start.await();
            System.out.println("Race started!");

            finish.await();

            long winnerTime = Long.MAX_VALUE;
            int winnerNumber = -1;

            System.out.println("Race finished:");
            for (Car car : cars) {
                System.out.println("Car " + car.getNumber() + " finished in " + car.getFinishTime() + " ms");
                if (car.getFinishTime() < winnerTime) {
                    winnerTime = car.getFinishTime();
                    winnerNumber = car.getNumber();
                }
            }
            System.out.println("Winner: Car " + winnerNumber + " with time " + winnerTime + " ms");

            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Car implements Runnable {
        private final int number;
        private long prepareTime;
        private long roadTime1;
        private long tunnelTime;
        private long roadTime2;
        private long finishTime;

        public Car(int number) {
            this.number = number;
            Random random = new Random();
            prepareTime = random.nextInt(500) + 500;
            roadTime1 = random.nextInt(1500) + 1000;
            tunnelTime = random.nextInt(1500) + 1000;
            roadTime2 = random.nextInt(1500) + 1000;
        }

        public int getNumber() {
            return number;
        }

        public long getFinishTime() {
            return finishTime;
        }

        private void prepare() throws InterruptedException {
            System.out.println("Car " + number + " is getting ready...");
            Thread.sleep(prepareTime);
            System.out.println("Car #" + number + " is ready!");
        }

        private void driveOnRoad1() throws InterruptedException {
            System.out.println("Car " + number + " is driving on road 1...");
            Thread.sleep(roadTime1);
            System.out.println("Car " + number + " finished road 1!");
        }

        private void driveThroughTunnel() throws InterruptedException {
            System.out.println("Car " + number + " is driving through the tunnel...");
            Thread.sleep(tunnelTime);
            System.out.println("Car " + number + " finished the tunnel!");
        }

        private void driveOnRoad2() throws InterruptedException {
            System.out.println("Car " + number + " is driving on road 2...");
            Thread.sleep(roadTime2);
            System.out.println("Car " + number + " finished road 2!");
        }

        @Override
        public void run() {
            try {
                prepare();
                start.countDown();
                start.await();
                long startTime = System.currentTimeMillis();
                driveOnRoad1();
                driveThroughTunnel();
                driveOnRoad2();
                finishTime = System.currentTimeMillis() - startTime;
                finish.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}