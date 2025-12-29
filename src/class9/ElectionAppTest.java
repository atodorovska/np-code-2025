package class9;

import com.sun.source.tree.Tree;

import java.util.*;
import java.util.stream.Collectors;

interface Subscriber {
    void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit);

    void printStatistics();
}

class VotersTurnoutApp implements Subscriber {

    Map<Integer, Integer> castedVotesPerUnit = new TreeMap<>();
    Map<Integer, Integer> totalVotersPerUnitMap = new TreeMap<>();

    @Override
    public void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit) {

        totalVotersPerUnitMap.putIfAbsent(unit, totalVotersPerUnit);

        castedVotesPerUnit.putIfAbsent(unit, 0);
        castedVotesPerUnit.put(unit, castedVotesPerUnit.get(unit) + votes);

    }

    @Override
    public void printStatistics() {
        System.out.println(String.format("%10s %7s %7s %9s", "Unit:", "Casted:", "Voters:", "Turnout:"));

        for (Integer unit : castedVotesPerUnit.keySet()) {
            double turnout = (100.0 * castedVotesPerUnit.get(unit)) / totalVotersPerUnitMap.get(unit);
            System.out.println(String.format("%10d %7d %7d %7.2f%%", unit, castedVotesPerUnit.get(unit), totalVotersPerUnitMap.get(unit), turnout));
        }

    }
}

class SeatsApp implements Subscriber {

    Map<String, Integer> votesPerParty = new TreeMap<>();
    int castedVotes = 0;

    @Override
    public void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit) {
        votesPerParty.putIfAbsent(party, 0);
        votesPerParty.put(party, votesPerParty.get(party) + votes);

        castedVotes+=votes;
    }

    @Override
    public void printStatistics() {

        int votesPerSeat = castedVotes / 20;

        Map<String, Integer> seatsPerParty = new TreeMap<>();

        votesPerParty.forEach((party, votes) -> {
            seatsPerParty.put(party, votes/votesPerSeat);
        });

        int seatsAllocated = seatsPerParty.values().stream().mapToInt(s -> s).sum();

        int seatNotAllocated = 20 - seatsAllocated;

        if (seatNotAllocated>0){
            String winnerParty = votesPerParty.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
            seatsPerParty.put(winnerParty, seatsPerParty.get(winnerParty) + seatNotAllocated);
        }

        System.out.println("Party      Votes   %Votes Seats   %Seats");

        for (String party : seatsPerParty.keySet()) {
            System.out.println(String.format(
                    "%10s %5d %7.2f%% %5d %7.2f%%",
                    party,
                    votesPerParty.get(party),
                    (100.0 * votesPerParty.get(party)) / castedVotes,
                    seatsPerParty.get(party),
                    (100.0 * seatsPerParty.get(party)) / 20
                    ));
        }


    }
}


class ElectionUnit {

    int unit;
    Map<String, Integer> votersByPoll;
    int totalVotersPerUnit;
    List<Subscriber> subscribers;

    ElectionUnit(int unit, Map<String, Integer> votersByPoll) {
        this.unit = unit;
        this.votersByPoll = votersByPoll;
        this.totalVotersPerUnit = votersByPoll.values().stream().mapToInt(Integer::intValue).sum();
        subscribers = new ArrayList<>();
    }

    void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }


    void addVotes(String pollId, String party, int votes) {
        for (Subscriber subscriber : subscribers) {
            subscriber.updateVotes(unit, pollId, party, votes, votersByPoll.get(pollId), totalVotersPerUnit);
        }
    }
}

class InvalidVotesException extends Exception {
    public InvalidVotesException(String message) {
        super(message);
    }
}

class VotesController {

    List<String> parties;
    Map<String, Integer> unitPerPoll;
    Map<Integer, ElectionUnit> units;

    VotesController(List<String> parties, Map<String, Integer> unitPerPoll) {
        this.parties = parties;
        this.unitPerPoll = unitPerPoll;
        units = new HashMap<>();
    }

    void addElectionUnit(ElectionUnit electionUnit) {
        units.put(electionUnit.unit, electionUnit);
    }

    void addVotes(String pollId, String party, int votes) throws InvalidVotesException {
        if (!parties.contains(party)) {
            //100 invalid votes were cast for option X at poll 123
            throw new InvalidVotesException(String.format("Party %s is not registered on this election.", party));
        }

        this.units.get(this.unitPerPoll.get(pollId)).addVotes(pollId, party, votes);
    }
}

public class ElectionAppTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> parties = Arrays.stream(sc.nextLine().split("\\s+")).collect(Collectors.toList());
        Map<String, Integer> unitPerPoll = new HashMap<>();
        Map<Integer, ElectionUnit> electionUnitMap = new TreeMap<>();

        int totalUnits = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < totalUnits; i++) {
            Map<String, Integer> votersPerPoll = new HashMap<>();
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            Integer unit = Integer.parseInt(parts[0]);
            for (int j = 1; j < parts.length; j += 2) {
                String pollId = parts[j];
                int voters = Integer.parseInt(parts[j + 1]);
                unitPerPoll.putIfAbsent(pollId, unit);
                votersPerPoll.put(pollId, voters);
            }

            electionUnitMap.putIfAbsent(unit, new ElectionUnit(unit, votersPerPoll));
        }
        VotesController controller = new VotesController(parties, unitPerPoll);

        electionUnitMap.values().forEach(controller::addElectionUnit);

        VotersTurnoutApp votersTurnoutApp = new VotersTurnoutApp();
        SeatsApp seatsApp = new SeatsApp();


        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String testCase = parts[0];

            if (testCase.equals("subscribe")) { //Example: subscribe votersTurnoutApp 1
                int unit = Integer.parseInt(parts[1]);
                String app = parts[2];
                if (app.equals("votersTurnoutApp")) {
                    electionUnitMap.get(unit).subscribe(votersTurnoutApp);
                } else {
                    electionUnitMap.get(unit).subscribe(seatsApp);
                }
            } else if (testCase.equals("unsubscribe")) { //Example: unsubscribe votersTurnoutApp 1
                int unit = Integer.parseInt(parts[1]);
                String app = parts[2];
                if (app.equals("votersTurnoutApp")) {
                    electionUnitMap.get(unit).unsubscribe(votersTurnoutApp);
                } else {
                    electionUnitMap.get(unit).unsubscribe(seatsApp);
                }
            } else if (testCase.equals("addVotes")) { // Example: addVotes 1234 A 1000
                String pollId = parts[1];
                String party = parts[2];
                int votes = Integer.parseInt(parts[3]);
                try {
                    controller.addVotes(pollId, party, votes);
                } catch (InvalidVotesException e) {
                    System.out.println(e.getMessage());
                }
            } else if (testCase.equals("printStatistics")) {
                String app = parts[1];
                if (app.equals("votersTurnoutApp")) {
                    System.out.println("PRINTING STATISTICS FROM VOTERS TURNOUT APPLICATION");
                    votersTurnoutApp.printStatistics();
                } else {
                    System.out.println("PRINTING STATISTICS FROM SEATS APPLICATION");
                    seatsApp.printStatistics();
                }
            }
        }
    }
}
