package class9;

//package mk.ukim.finki.midterm;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}

//enum CallState {
//    INITIALIZED,
//    IN_PROGRESS,
//    PAUSED,
//    COMPLETED
//}

interface ICallState {
    void answer(long timestamp);

    void end(long timestamp);

    void pause(long timestamp);

    void resume(long timestamp);
}

abstract class CallState implements ICallState {
    Call call;

    public CallState(Call call) {
        this.call = call;
    }
}

class IdleState extends CallState {

    public IdleState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void end(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void pause(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void resume(long timestamp) {
        System.out.println("Invalid action");
    }
}

class RingingState extends CallState {
    public RingingState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        this.call.start = timestamp;
        this.call.state = new InProgressState(this.call);
    }

    @Override
    public void end(long timestamp) {
        this.call.end = timestamp;
        this.call.start = timestamp;
        this.call.state = new IdleState(this.call);
    }

    @Override
    public void pause(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void resume(long timestamp) {
        System.out.println("Invalid action");
    }
}

class InProgressState extends CallState {
    public InProgressState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void end(long timestamp) {
        this.call.end = timestamp;
        this.call.state = new IdleState(this.call);
    }

    @Override
    public void pause(long timestamp) {
        this.call.holdStartedAt = timestamp;
        this.call.state = new PausedState(this.call);
    }

    @Override
    public void resume(long timestamp) {
        System.out.println("Invalid action");
    }
}

class PausedState extends CallState {
    public PausedState(Call call) {
        super(call);

    }

    @Override
    public void answer(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void end(long timestamp) {
        this.call.end = timestamp;
        this.call.totalHoldTime += (timestamp - this.call.holdStartedAt);
        this.call.state = new IdleState(this.call);
    }

    @Override
    public void pause(long timestamp) {
        System.out.println("Invalid action");
    }

    @Override
    public void resume(long timestamp) {
        this.call.totalHoldTime += (timestamp - this.call.holdStartedAt);
        this.call.state = new InProgressState(this.call);
    }
}

class Call {
    String uuid;
    String dialer;
    String receiver;

    long initialized;

    long start;
    long end;

    long holdStartedAt;
    long totalHoldTime = 0;

    //    CallState state =  CallState.INITIALIZED;
    CallState state = new RingingState(this);

    public Call(String uuid, String dialer, String receiver, long initialized) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.initialized = initialized;
    }

    public void update(long timestamp, String action) {
        /*
        ANSWER (примачот го одговорил повикот)
        END (еден од учесниците го завршил повикот)
        HOLD (еден од учесниците го ставил повикот на чекање)
        RESUME (повикот продолжил по тоа што бил ставен на чекање)
        * */
//        if (state == CallState.INITIALIZED) {
//            if (action.equals("ANSWER")) {
//                state = CallState.IN_PROGRESS;
//                start = timestamp;
//            } else if (action.equals("END")) {
//                state = CallState.COMPLETED;
//                start = timestamp;
//                end = timestamp;
//            } else {
//                System.out.println("Invalid action");
//            }
//        } else if (state == CallState.IN_PROGRESS) {
//            if (action.equals("HOLD")) {
//                state = CallState.PAUSED;
//                holdStartedAt = timestamp;
//            } else if (action.equals("END")) {
//                state = CallState.COMPLETED;
//                end = timestamp;
//            } else {
//                System.out.println("Invalid action");
//            }
//        } else if (state == CallState.PAUSED) {
//            if (action.equals("RESUME")) {
//                state = CallState.IN_PROGRESS;
//                totalHoldTime += (timestamp - holdStartedAt);
//            } else if (action.equals("END")) {
//                state = CallState.COMPLETED;
//                end = timestamp;
//                totalHoldTime += (timestamp - holdStartedAt);
//            } else {
//                System.out.println("Invalid action");
//            }
//        } else {
//            System.out.println("Invalid action");
//        }
        switch (action) {
            case "ANSWER":
                state.answer(timestamp);
                break;
            case "HOLD":
                state.pause(timestamp);
                break;
            case "END":
                state.end(timestamp);
                break;
            case "RESUME":
                state.resume(timestamp);
                break;
        }
    }

    public long totalDuration() {
        return end - start - totalHoldTime;
    }

    @Override
    public String toString() {
        return String.format("uuid: %s, dialer: %s, receiver: %s duration: %s", uuid, dialer, receiver, DurationConverter.convert(totalDuration()));
    }
}

class TelcoApp {

    Map<String, Call> calls = new HashMap<>();

    public void addCall(String uuid, String dialer, String receiver, long timestamp) {
        calls.put(uuid, new Call(uuid, dialer, receiver, timestamp));
    }

    public void updateCall(String uuid, long timestamp, String action) {
        calls.get(uuid).update(timestamp, action);
    }

    public void printChronologicalReport() {

    }

    public void printReportByDuration() {
        calls.values().stream()
                .sorted(Comparator.comparing(Call::totalDuration))
                .forEach(System.out::println);
    }
}


public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
//                String phoneNumber = parts[1];
                app.printChronologicalReport();
            } else if (command.equals("printReportByDuration")) {
//                String phoneNumber = parts[1];
                app.printReportByDuration();
            } else {
//                app.printCallsDuration();
            }
        }

    }
}
