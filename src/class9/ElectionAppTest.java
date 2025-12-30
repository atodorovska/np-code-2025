package class9;

import java.util.*;
import java.util.Scanner;
import java.util.stream.Collectors;


interface Subscriber {
    void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit);

    void printStatistics();
}

class VotersTurnoutApp implements Subscriber {

    Map<Integer, Integer> castedVotesPerUnit = new HashMap<>();
    Map<Integer, Integer> totalVotersPerUnitMap = new HashMap<>();

    @Override
    public void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit) {
        //1 100
        //1 200
        //1 300

        //1 600

        castedVotesPerUnit.putIfAbsent(unit, 0);
        castedVotesPerUnit.put(unit, castedVotesPerUnit.get(unit) + votes);

        totalVotersPerUnitMap.putIfAbsent(unit, totalVotersPerUnit);
    }

    @Override
    public void printStatistics() {
        //Unit: Casted: Voters: Turnout:
        //    1     800    5000  16.00%

        System.out.println("Unit: Casted: Voters: Turnout:");
        for (int unit = 1; unit <= 6; unit++) {
            if (castedVotesPerUnit.containsKey(unit)) {
                System.out.println(
                        String.format(
                                "%5d %7d %7d %7.2f%%",
                                unit,
                                castedVotesPerUnit.get(unit),
                                totalVotersPerUnitMap.get(unit),
                                100.0 * castedVotesPerUnit.get(unit) / totalVotersPerUnitMap.get(unit)
                        ));
            }
        }

    }
}


class ElectionUnitSeatDistribution {
    int unit;
    Map<String, Integer> votesPerParty = new HashMap<>();
    int totalVotes = 0;

    public ElectionUnitSeatDistribution(int unit) {
        this.unit = unit;
    }

    public void addVotes(String party, int votes) {
        votesPerParty.putIfAbsent(party, 0);
        votesPerParty.put(party, votesPerParty.get(party) + votes);

        totalVotes += votes;
    }

    public Map<String, Integer> distributeSeats() {
        int votesPerSeat = totalVotes / 20;
        int seatsAssigned = 0;

        Map<String, Integer> distribution = new HashMap<>();

        for (Map.Entry<String, Integer> entry : votesPerParty.entrySet()) {
            String party = entry.getKey();
            Integer votes = entry.getValue();
            int seats = votes / votesPerSeat;
            seatsAssigned+=seats;
            distribution.put(party, seats);
        }

        if (seatsAssigned < 20) {
            String winnerParty = votesPerParty.entrySet()
                    .stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .get().getKey();

            int notAllocatedSeats = 20 - seatsAssigned;
            distribution.put(winnerParty, distribution.get(winnerParty) + notAllocatedSeats);
        }

        return distribution;

    }

    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Party      Votes   %Votes Seats   %Seats\n");
        Map<String, Integer> distribution = distributeSeats();
        for (Map.Entry<String, Integer> partyVotes : votesPerParty.entrySet()) {
            String party = partyVotes.getKey();
            Integer votes = partyVotes.getValue();
            sb.append(String.format(
                    "%10s %5d %7.2f%% %5d %7.2f%%",
                    party,
                    votes,
                    100.0*votes/totalVotes,
                    distribution.get(party),
                    100.0*distribution.get(party)/20.0
            )).append("\n");
        }
        return sb.toString();
    }
}

class SeatsApp implements Subscriber {

    Map<Integer, ElectionUnitSeatDistribution> distribution = new TreeMap<>();

    @Override
    public void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit) {
        distribution.putIfAbsent(unit, new ElectionUnitSeatDistribution(unit));
        distribution.get(unit).addVotes(party, votes);
    }

    @Override
    public void printStatistics() {
        for (ElectionUnitSeatDistribution unit : distribution.values()) {
            System.out.println(unit.getStatistics());
        }

    }
}

class ElectionUnit { //Publisher

    int unit;
    Map<String, Integer> votersByPoll;
    List<Subscriber> subscribers;

    ElectionUnit(int unit, Map<String, Integer> votersByPoll) {
        this.unit = unit;
        this.votersByPoll = votersByPoll;
        subscribers = new ArrayList<>();
    }

    void addVotes(String pollId, String party, int votes) {
        for (Subscriber subscriber : subscribers) {
            subscriber.updateVotes(
                    this.unit,
                    pollId,
                    party,
                    votes,
                    this.votersByPoll.get(pollId),
                    this.votersByPoll.values().stream().mapToInt(Integer::intValue).sum()
            );
        }
    }

    void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
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

    public void addElectionUnit(ElectionUnit electionUnit) {
        units.put(electionUnit.unit, electionUnit);
    }

    public void addVotes(String pollId, String party, int votes) throws InvalidVotesException {
        if (!parties.contains(party)) {
            throw new InvalidVotesException(String.format("Party %s is not registed for these elections.", party));
        }

        int unit = unitPerPoll.get(pollId);
        units.get(unit).addVotes(pollId, party, votes);
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
